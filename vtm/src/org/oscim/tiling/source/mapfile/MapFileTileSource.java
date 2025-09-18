/*
 * Copyright 2013 mapsforge.org
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2020 devemux86
 *
 * This file is part of the OpenScienceMap project (http://www.opensciencemap.org).
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.oscim.tiling.source.mapfile;

import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.map.Viewport;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.OverzoomTileDataSource;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.mapfile.header.MapFileHeader;
import org.oscim.tiling.source.mapfile.header.MapFileInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.logging.Logger;

public class MapFileTileSource extends TileSource implements IMapFileTileSource {
    private static final Logger log = Logger.getLogger(MapFileTileSource.class.getName());

    /**
     * Amount of cache blocks that the index cache should store.
     */
    private static final int INDEX_CACHE_SIZE = 64;

    MapFileHeader fileHeader;
    MapFileInfo fileInfo;
    IndexCache databaseIndexCache;
    boolean experimental;
    File mapFile;
    FileInputStream mapFileInputStream;
    private FileChannel inputChannel;

    /**
     * The preferred language when extracting labels from this tile source.
     */
    private String preferredLanguage;
    private Callback callback;

    /**
     * Priority of this MapFileTileSource. A higher number means a higher priority. Negative numbers have a special
     * meaning, they should only be used for so-called background maps. Data from background maps is only read
     * if no other map has provided a (complete) map tile. The most famous example of a background map is a
     * low-resolution world map. The default priority is 0.
     */
    private int priority = 0;

    public MapFileTileSource() {
        this(Viewport.MIN_ZOOM_LEVEL, Viewport.MAX_ZOOM_LEVEL);
    }

    public MapFileTileSource(int zoomMin, int zoomMax) {
        this(zoomMin, zoomMax, BuildingLayer.MIN_ZOOM);
    }

    public MapFileTileSource(int zoomMin, int zoomMax, int overZoom) {
        super(zoomMin, zoomMax, overZoom);
    }

    /**
     * Extracts substring of preferred language from multilingual string using
     * the preferredLanguage setting.
     */
    String extractLocalized(String s) {
        if (callback != null)
            return callback.extractLocalized(s);
        return MapFileUtils.extract(s, preferredLanguage);
    }

    @Override
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public boolean setMapFile(String filename) {
        setOption("file", filename);

        File file = new File(filename);

        if (!file.exists()) {
            return false;
        } else if (!file.isFile()) {
            return false;
        } else if (!file.canRead()) {
            return false;
        }

        return true;
    }

    public void setMapFileInputStream(FileInputStream fileInputStream) {
        this.mapFileInputStream = fileInputStream;
    }

    @Override
    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    /**
     * Returns the priority of this MapFileTileSource. A higher number means a higher priority. Negative numbers
     * have a special meaning, they should only be used for so-called background maps. Data from background
     * maps is only read if no other map has provided a (complete) map tile. The most famous example of a
     * background map is a low-resolution world map.
     *
     * @return The priority of this MapFileTileSource. Default is 0.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets the priority of this MapFileTileSource. A higher number means a higher priority. Negative numbers have
     * a special meaning, they should only be used for so-called background maps. Data from background maps is
     * only read if no other map has provided a (complete) map tile. The most famous example of a background
     * map is a low-resolution world map. The default priority is 0.
     *
     * @param priority Priority of this MapFileTileSource. Negative number means background map priority (see above
     *                 for the description).
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public OpenResult open() {
        if (mapFileInputStream == null && !options.containsKey("file"))
            return new OpenResult("no map file set");

        try {
            // false positive: stream gets closed when the channel is closed
            // see e.g. http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4796385
            File file = null;
            if (mapFileInputStream != null)
                inputChannel = mapFileInputStream.getChannel();
            else {
                // make sure to close any previously opened file first
                //close();

                file = new File(options.get("file"));

                // check if the file exists and is readable
                if (!file.exists()) {
                    return new OpenResult("file does not exist: " + file);
                } else if (!file.isFile()) {
                    return new OpenResult("not a file: " + file);
                } else if (!file.canRead()) {
                    return new OpenResult("cannot read file: " + file);
                }

                FileInputStream fis = new FileInputStream(file);
                inputChannel = fis.getChannel();
            }
            long fileSize = inputChannel.size();
            ReadBuffer readBuffer = new ReadBuffer(inputChannel);

            fileHeader = new MapFileHeader();
            OpenResult openResult = fileHeader.readHeader(readBuffer, fileSize);

            if (!openResult.isSuccess()) {
                close();
                return openResult;
            }
            fileInfo = fileHeader.getMapFileInfo();
            mapFile = file;
            databaseIndexCache = new IndexCache(inputChannel, INDEX_CACHE_SIZE);

            if (fileInfo != null)
                log.fine("File version: " + fileInfo.fileVersion);
            return OpenResult.SUCCESS;
        } catch (IOException e) {
            log.severe(e.toString());
            // make sure that the file is closed
            close();
            return new OpenResult(e.toString());
        }
    }

    @Override
    public ITileDataSource getDataSource() {
        try {
            return new OverzoomTileDataSource(new MapFile(this), mOverZoom);
        } catch (IOException e) {
            log.fine(e.toString());
        }
        return null;
    }

    @Override
    public void close() {
        if (inputChannel != null) {
            try {
                inputChannel.close();
                inputChannel = null;
            } catch (IOException e) {
                log.severe(e.toString());
            }
        }
        fileHeader = null;
        fileInfo = null;
        mapFile = null;

        if (databaseIndexCache != null) {
            databaseIndexCache.destroy();
            databaseIndexCache = null;
        }
    }

    public MapInfo getMapInfo() {
        return fileInfo;
    }

    public interface Callback {
        /**
         * Extracts substring of preferred language from multilingual string.
         */
        String extractLocalized(String s);
    }
}
