/*
 * Copyright 2024 jhotadhari
 * Copyright 2025 devemux86
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
package org.oscim.tiling.source.hills;

import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.map.layer.hills.AdaptiveClasyHillShading;
import org.mapsforge.map.layer.hills.DemFolder;
import org.mapsforge.map.layer.hills.ShadingAlgorithm;
import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Color;
import org.oscim.core.Tile;
import org.oscim.map.Viewport;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.ITileDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class HillshadingTileSource extends TileSource {

    private static final Logger log = Logger.getLogger(HillshadingTileSource.class.getName());

    final DemFolder mDemFolder;
    final ShadingAlgorithm mAlgorithm;
    final int mMagnitude;
    final int mColor;
    final GraphicFactory mGraphicFactory;

    public HillshadingTileSource(DemFolder demFolder, GraphicFactory graphicFactory) {
        this(Viewport.MIN_ZOOM_LEVEL, Viewport.MAX_ZOOM_LEVEL, demFolder, new AdaptiveClasyHillShading(), 128, Color.BLACK, graphicFactory);
    }

    public HillshadingTileSource(int zoomMin, int zoomMax, DemFolder demFolder, ShadingAlgorithm algorithm, int magnitude, int color, GraphicFactory graphicFactory) {
        super(zoomMin, zoomMax);
        mDemFolder = demFolder;
        mAlgorithm = algorithm;
        mMagnitude = magnitude;
        mColor = color;
        mGraphicFactory = graphicFactory;
    }

    @Override
    public ITileDataSource getDataSource() {
        return new HillshadingTileDataSource(this, new TileDecoder());
    }

    @Override
    public OpenResult open() {
        return OpenResult.SUCCESS;
    }

    @Override
    public void close() {
        getDataSource().dispose();
    }

    public static class TileDecoder implements ITileDecoder {

        @Override
        public boolean decode(Tile tile, ITileDataSink sink, InputStream is)
                throws IOException {

            Bitmap bitmap = CanvasAdapter.decodeBitmap(is);
            if (!bitmap.isValid()) {
                log.fine(tile + " invalid bitmap");
                return false;
            }
            sink.setTileImage(bitmap);

            return true;
        }
    }
}
