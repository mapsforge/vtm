/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2013, 2014 Hannes Janetzek
 * Copyright 2014-2015 Ludwig M Brinckmann
 * Copyright 2016-2022 devemux86
 * Copyright 2016 Andrey Novikov
 * Copyright 2017-2018 Gustl22
 * Copyright 2018 Bezzu
 * Copyright 2019 marq24
 * Copyright 2019 Justus Schmidt
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

import org.oscim.backend.CanvasAdapter;
import org.oscim.core.*;
import org.oscim.core.GeometryBuffer.GeometryType;
import org.oscim.layers.tile.MapTile;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.QueryResult;
import org.oscim.tiling.TileDataSink;
import org.oscim.tiling.source.mapfile.header.SubFileParameter;
import org.oscim.utils.Constants;
import org.oscim.utils.Parameters;
import org.oscim.utils.geom.TileClipper;
import org.oscim.utils.geom.TileSeparator;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * A class for reading binary map files.
 *
 * @see <a href="https://github.com/mapsforge/mapsforge/blob/master/docs/Specification-Binary-Map-File.md">Specification</a>
 */
public class MapFile implements ITileDataSource {
    private static final Logger log = Logger.getLogger(MapFile.class.getName());

    /**
     * Bitmask to extract the block offset from an index entry.
     */
    private static final long BITMASK_INDEX_OFFSET = 0x7FFFFFFFFFL;

    /**
     * Bitmask to extract the water information from an index entry.
     */
    private static final long BITMASK_INDEX_WATER = 0x8000000000L;

    /**
     * Debug message prefix for the block signature.
     */
    private static final String DEBUG_SIGNATURE_BLOCK = "block signature: ";

    /** Debug message prefix for the POI signature. */
    // private static final String DEBUG_SIGNATURE_POI = "POI signature: ";

    /**
     * Debug message prefix for the way signature.
     */
    private static final String DEBUG_SIGNATURE_WAY = "way signature: ";

    /**
     * Error message for an invalid first way offset.
     */
    private static final String INVALID_FIRST_WAY_OFFSET = "invalid first way offset: ";

    /**
     * Bitmask for the optional POI feature "elevation".
     */
    private static final int POI_FEATURE_ELEVATION = 0x20;

    /**
     * Bitmask for the optional POI feature "house number".
     */
    private static final int POI_FEATURE_HOUSE_NUMBER = 0x40;

    /**
     * Bitmask for the optional POI feature "name".
     */
    private static final int POI_FEATURE_NAME = 0x80;

    /**
     * Bitmask for the POI layer.
     */
    private static final int POI_LAYER_BITMASK = 0xf0;

    /**
     * Bit shift for calculating the POI layer.
     */
    private static final int POI_LAYER_SHIFT = 4;

    /**
     * Bitmask for the number of POI tags.
     */
    private static final int POI_NUMBER_OF_TAGS_BITMASK = 0x0f;

    /**
     * Length of the debug signature at the beginning of each block.
     */
    private static final byte SIGNATURE_LENGTH_BLOCK = 32;

    /**
     * Length of the debug signature at the beginning of each POI.
     */
    private static final byte SIGNATURE_LENGTH_POI = 32;

    /**
     * Length of the debug signature at the beginning of each way.
     */
    private static final byte SIGNATURE_LENGTH_WAY = 32;

    /**
     * Bitmask for the optional way data blocks byte.
     */
    private static final int WAY_FEATURE_DATA_BLOCKS_BYTE = 0x08;

    /**
     * Bitmask for the optional way double delta encoding.
     */
    private static final int WAY_FEATURE_DOUBLE_DELTA_ENCODING = 0x04;

    /**
     * Bitmask for the optional way feature "house number".
     */
    private static final int WAY_FEATURE_HOUSE_NUMBER = 0x40;

    /**
     * Bitmask for the optional way feature "label position".
     */
    private static final int WAY_FEATURE_LABEL_POSITION = 0x10;

    /**
     * Bitmask for the optional way feature "name".
     */
    private static final int WAY_FEATURE_NAME = 0x80;

    /**
     * Bitmask for the optional way feature "reference".
     */
    private static final int WAY_FEATURE_REF = 0x20;

    /**
     * Bitmask for the way layer.
     */
    private static final int WAY_LAYER_BITMASK = 0xf0;

    /**
     * Bit shift for calculating the way layer.
     */
    private static final int WAY_LAYER_SHIFT = 4;

    /**
     * Bitmask for the number of way tags.
     */
    private static final int WAY_NUMBER_OF_TAGS_BITMASK = 0x0f;

    /**
     * Way filtering reduces the number of ways returned to only those that are
     * relevant for the tile requested, leading to performance gains, but can
     * cause line clipping artifacts (particularly at higher zoom levels). The
     * risk of clipping can be reduced by either turning way filtering off or by
     * increasing the wayFilterDistance which governs how large an area surrounding
     * the requested tile will be returned.
     * For most use cases the standard settings should be sufficient.
     */
    public static boolean wayFilterEnabled = true;
    public static int wayFilterDistance = 20;

    /**
     * Reduce points on-the-fly while reading from map files.
     */
    public static int SIMPLIFICATION_MIN_ZOOM = 8;
    public static int SIMPLIFICATION_MAX_ZOOM = 11;

    private long mFileSize;
    private boolean mDebugFile;
    private FileChannel mInputChannel;
    private String mSignatureBlock;
    private String mSignaturePoi;
    private String mSignatureWay;
    private int mTileLatitude;
    private int mTileLongitude;
    private int[] mIntBuffer;

    private final MapElement mElem = new MapElement();

    private int minDeltaLat, minDeltaLon;

    private final TileProjection mTileProjection;
    private final TileClipper mTileClipper;
    private final TileSeparator mTileSeparator;

    private final MapFileTileSource mTileSource;

    private int zoomLevelMin = 0;
    private int zoomLevelMax = Byte.MAX_VALUE;

    private boolean deduplicate;

    /**
     * Priority of this MapFile. A higher number means a higher priority. Negative numbers have a special
     * meaning, they should only be used for so-called background maps. Data from background maps is only read
     * if no other map has provided a (complete) map tile. The most famous example of a background map is a
     * low-resolution world map. The default priority is 0.
     */
    private int priority = 0;

    public MapFile(MapFileTileSource tileSource) throws IOException {
        mTileSource = tileSource;
        try {
            // false positive: stream gets closed when the channel is closed
            // see e.g. http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4796385
            if (tileSource.mapFileInputStream != null)
                mInputChannel = tileSource.mapFileInputStream.getChannel();
            else {
                FileInputStream fis = new FileInputStream(tileSource.mapFile);
                mInputChannel = fis.getChannel();
            }
            mFileSize = mInputChannel.size();
        } catch (IOException e) {
            log.severe(e.toString());
            /* make sure that the file is closed */
            dispose();
            throw new IOException();
        }

        mTileProjection = new TileProjection();
        mTileClipper = new TileClipper(0, 0, 0, 0);
        mTileSeparator = new TileSeparator(0, 0, 0, 0);
    }

    public MapFileTileSource getTileSource() {
        return mTileSource;
    }

    @Override
    public void query(MapTile tile, ITileDataSink sink) {
        try {
            if (mTileSource.fileHeader == null) {
                sink.completed(QueryResult.FAILED);
                return;
            }

            if (mIntBuffer == null)
                mIntBuffer = new int[Short.MAX_VALUE * 2];

            mTileProjection.setTile(tile);
            //mTile = tile;

            if (Parameters.SIMPLIFICATION_TOLERANCE > 0
                    && tile.zoomLevel >= SIMPLIFICATION_MIN_ZOOM && tile.zoomLevel <= SIMPLIFICATION_MAX_ZOOM) {
                /* size of tile in map coordinates; */
                double size = 1.0 / (1 << tile.zoomLevel);

                /* simplification tolerance */
                int pixel = Parameters.SIMPLIFICATION_TOLERANCE;

                int simplify = Tile.SIZE / pixel;

                /* translate screen pixel for tile to latitude and longitude
                 * tolerance for point reduction before projection. */
                minDeltaLat = (int) (Math.abs(MercatorProjection.toLatitude(tile.y + size)
                        - MercatorProjection.toLatitude(tile.y)) * 1e6) / simplify;
                minDeltaLon = (int) (Math.abs(MercatorProjection.toLongitude(tile.x + size)
                        - MercatorProjection.toLongitude(tile.x)) * 1e6) / simplify;
            } else {
                minDeltaLat = 0;
                minDeltaLon = 0;
            }

            QueryParameters queryParameters = new QueryParameters();
            queryParameters.queryZoomLevel =
                    mTileSource.fileHeader.getQueryZoomLevel(tile.zoomLevel);

            /* get and check the sub-file for the query zoom level */
            SubFileParameter subFileParameter =
                    mTileSource.fileHeader.getSubFileParameter(queryParameters.queryZoomLevel);

            if (subFileParameter == null) {
                log.warning("no sub-file for zoom level: "
                        + queryParameters.queryZoomLevel);

                sink.completed(QueryResult.FAILED);
                return;
            }

            QueryCalculations.calculateBaseTiles(queryParameters, tile, subFileParameter);
            QueryCalculations.calculateBlocks(queryParameters, subFileParameter);
            if (deduplicate)
                processBlocks(sink, queryParameters, subFileParameter, tile.getBoundingBox(), Selector.ALL, new MapReadResult());
            else
                processBlocks(sink, queryParameters, subFileParameter);
            sink.completed(QueryResult.SUCCESS);
        } catch (Throwable t) {
            log.severe(t.toString());
            sink.completed(QueryResult.FAILED);
        }
    }

    @Override
    public void dispose() {
        if (mInputChannel != null) {
            try {
                mInputChannel.close();
                mInputChannel = null;
            } catch (IOException e) {
                log.severe(e.toString());
            }
        }
    }

    @Override
    public void cancel() {
    }

    /**
     * Logs the debug signatures of the current way and block.
     */
    private void logDebugSignatures() {
        if (mDebugFile) {
            log.warning(DEBUG_SIGNATURE_WAY + mSignatureWay);
            log.warning(DEBUG_SIGNATURE_BLOCK + mSignatureBlock);
        }
    }

    /**
     * Processes a single block and executes the callback functions on all map
     * elements.
     *
     * @param queryParameters  the parameters of the current query.
     * @param subFileParameter the parameters of the current map file.
     * @param mapDataSink      the callback which handles the extracted map elements.
     */
    private void processBlock(QueryParameters queryParameters,
                              SubFileParameter subFileParameter, ITileDataSink mapDataSink,
                              BoundingBox boundingBox, Selector selector,
                              MapReadResult mapReadResult, ReadBuffer readBuffer) {

        if (!processBlockSignature(readBuffer)) {
            return;
        }

        int[][] zoomTable = readZoomTable(subFileParameter, readBuffer);
        if (zoomTable == null) {
            return;
        }
        int zoomTableRow = queryParameters.queryZoomLevel - subFileParameter.zoomLevelMin;
        int poisOnQueryZoomLevel = zoomTable[zoomTableRow][0];
        int waysOnQueryZoomLevel = zoomTable[zoomTableRow][1];

        /* get the relative offset to the first stored way in the block */
        int firstWayOffset = readBuffer.readUnsignedInt();
        if (firstWayOffset < 0) {
            log.warning(INVALID_FIRST_WAY_OFFSET + firstWayOffset);
            if (mDebugFile) {
                log.warning(DEBUG_SIGNATURE_BLOCK + mSignatureBlock);
            }
            return;
        }

        /* add the current buffer position to the relative first way offset */
        firstWayOffset += readBuffer.getBufferPosition();
        if (firstWayOffset > readBuffer.getBufferSize()) {
            log.warning(INVALID_FIRST_WAY_OFFSET + firstWayOffset);
            if (mDebugFile) {
                log.warning(DEBUG_SIGNATURE_BLOCK + mSignatureBlock);
            }
            return;
        }

        boolean filterRequired = queryParameters.queryZoomLevel > subFileParameter.baseZoomLevel;

        List<PointOfInterest> pois = null;
        if (mapReadResult != null)
            pois = new ArrayList<>();

        if (!processPOIs(mapDataSink, poisOnQueryZoomLevel, boundingBox, filterRequired, pois, readBuffer)) {
            return;
        }

        /* finished reading POIs, check if the current buffer position is valid */
        if (readBuffer.getBufferPosition() > firstWayOffset) {
            log.warning("invalid buffer position: " + readBuffer.getBufferPosition());
            if (mDebugFile) {
                log.warning(DEBUG_SIGNATURE_BLOCK + mSignatureBlock);
            }
            return;
        }

        /* move the pointer to the first way */
        readBuffer.setBufferPosition(firstWayOffset);

        List<Way> ways = null;
        if (mapReadResult != null && Selector.POIS != selector)
            ways = new ArrayList<>();

        if (!processWays(queryParameters, mapDataSink, waysOnQueryZoomLevel, boundingBox, filterRequired, selector, ways, readBuffer)) {
            return;
        }

        if (mapReadResult != null) {
            if (Selector.POIS == selector)
                ways = Collections.emptyList();
            mapReadResult.add(new PoiWayBundle(pois, ways));
        }
    }

    void setDeduplicate(boolean deduplicate) {
        this.deduplicate = deduplicate;
    }

    /**
     * Returns the priority of this MapFile. A higher number means a higher priority. Negative numbers
     * have a special meaning, they should only be used for so-called background maps. Data from background
     * maps is only read if no other map has provided a (complete) map tile. The most famous example of a
     * background map is a low-resolution world map.
     *
     * @return The priority of this MapFile. Default is 0.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets the priority of this MapFile. A higher number means a higher priority. Negative numbers have
     * a special meaning, they should only be used for so-called background maps. Data from background maps is
     * only read if no other map has provided a (complete) map tile. The most famous example of a background
     * map is a low-resolution world map. The default priority is 0.
     *
     * @param priority Priority of this MapFile. Negative number means background map priority (see above
     *                 for the description).
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    private void setTileClipping(QueryParameters queryParameters, SubFileParameter subFileParameter,
                                 long currentRow, long currentCol) {
        long numRows = queryParameters.toBlockY - queryParameters.fromBlockY;
        long numCols = queryParameters.toBlockX - queryParameters.fromBlockX;

        //log.debug(numCols + "/" + numRows + " " + currentCol + " " + currentRow);

        // At large query zoom levels use enlarged buffer
        int buffer;
        if (queryParameters.queryZoomLevel > BuildingLayer.MIN_ZOOM)
            buffer = Tile.SIZE / 2;
        else
            buffer = (int) (16 * CanvasAdapter.getScale() + 0.5f);

        int xmin = -buffer;
        int ymin = -buffer;
        int xmax = Tile.SIZE + buffer;
        int ymax = Tile.SIZE + buffer;

        int xSmin = 0;
        int ySmin = 0;
        int xSmax = Tile.SIZE;
        int ySmax = Tile.SIZE;

        if (numRows > 0) {
            /* If blocks are at a border, sometimes too less blocks are requested,
             * so the divisor for tile dimensions is increased to base tile subdivision.
             */
            boolean isTopBorder = queryParameters.fromBaseTileY < subFileParameter.boundaryTileTop;
            boolean isLeftBorder = queryParameters.fromBaseTileX < subFileParameter.boundaryTileLeft;
            long numSubX = queryParameters.toBaseTileX - queryParameters.fromBaseTileX;
            long numSubY = queryParameters.toBaseTileY - queryParameters.fromBaseTileY;
            long numDifX = numSubX - numCols; // 0 except at map borders
            long numDifY = numSubY - numRows; // 0 except at map borders

            int w = (int) (Tile.SIZE / (numSubX + 1));
            int h = (int) (Tile.SIZE / (numSubY + 1));

            if (currentCol > 0)
                xSmin = xmin = (int) ((currentCol + (isLeftBorder ? numDifX : 0)) * w);

            if (currentCol < numCols)
                xSmax = xmax = (int) ((currentCol + (isLeftBorder ? numDifX : 0)) * w + w);

            if (currentRow > 0)
                ySmin = ymin = (int) ((currentRow + (isTopBorder ? numDifY : 0)) * h);

            if (currentRow < numRows)
                ySmax = ymax = (int) ((currentRow + (isTopBorder ? numDifY : 0)) * h + h);
        }
        mTileClipper.setRect(xmin, ymin, xmax, ymax);
        mTileSeparator.setRect(xSmin, ySmin, xSmax, ySmax);
    }

    //private static final Tag mWaterTag = new Tag("natural", "water");

    /**
     * Map rendering.
     */
    private void processBlocks(ITileDataSink mapDataSink, QueryParameters queryParams,
                               SubFileParameter subFileParameter) throws IOException {
        processBlocks(mapDataSink, queryParams, subFileParameter, null, null, null);
    }

    /**
     * Map data reading.
     */
    private void processBlocks(QueryParameters queryParams,
                               SubFileParameter subFileParameter, BoundingBox boundingBox,
                               Selector selector, MapReadResult mapReadResult) throws IOException {
        processBlocks(null, queryParams, subFileParameter, boundingBox, selector, mapReadResult);
    }

    private void processBlocks(ITileDataSink mapDataSink, QueryParameters queryParams,
                               SubFileParameter subFileParameter, BoundingBox boundingBox,
                               Selector selector, MapReadResult mapReadResult) throws IOException {

        /* read and process all blocks from top to bottom and from left to right */
        for (long row = queryParams.fromBlockY; row <= queryParams.toBlockY; row++) {
            for (long column = queryParams.fromBlockX; column <= queryParams.toBlockX; column++) {
                setTileClipping(queryParams, subFileParameter,
                        row - queryParams.fromBlockY,
                        column - queryParams.fromBlockX);

                /* calculate the actual block number of the needed block in the
                 * file */
                long blockNumber = row * subFileParameter.blocksWidth + column;

                /* get the current index entry */
                long blockIndexEntry = mTileSource.databaseIndexCache.getIndexEntry(subFileParameter,
                        blockNumber);

                /* check the water flag of the block in its index entry */
                if ((blockIndexEntry & BITMASK_INDEX_WATER) != 0) {
                    // Deprecate water tiles rendering
                    /*MapElement e = mElem;
                    e.clear();
                    e.tags.clear();
                    e.tags.add(mWaterTag);
                    e.startPolygon();
                    e.addPoint(xmin, ymin);
                    e.addPoint(xmax, ymin);
                    e.addPoint(xmax, ymax);
                    e.addPoint(xmin, ymax);
                    mapDataSink.process(e);*/
                }

                /* get and check the current block pointer */
                long blockPointer = blockIndexEntry & BITMASK_INDEX_OFFSET;
                if (blockPointer < 1 || blockPointer > subFileParameter.subFileSize) {
                    log.warning("invalid current block pointer: " + blockPointer);
                    log.warning("subFileSize: " + subFileParameter.subFileSize);
                    return;
                }

                long nextBlockPointer;
                /* check if the current block is the last block in the file */
                if (blockNumber + 1 == subFileParameter.numberOfBlocks) {
                    /* set the next block pointer to the end of the file */
                    nextBlockPointer = subFileParameter.subFileSize;
                } else {
                    /* get and check the next block pointer */
                    nextBlockPointer = mTileSource.databaseIndexCache.getIndexEntry(subFileParameter,
                            blockNumber + 1);
                    nextBlockPointer &= BITMASK_INDEX_OFFSET;

                    if (nextBlockPointer < 1 || nextBlockPointer > subFileParameter.subFileSize) {
                        log.warning("invalid next block pointer: " + nextBlockPointer);
                        log.warning("sub-file size: " + subFileParameter.subFileSize);
                        return;
                    }
                }

                /* calculate the size of the current block */
                int blockSize = (int) (nextBlockPointer - blockPointer);
                if (blockSize < 0) {
                    log.warning("current block size must not be negative: "
                            + blockSize);
                    return;
                } else if (blockSize == 0) {
                    /* the current block is empty, continue with the next block */
                    continue;
                } else if (blockSize > Parameters.MAXIMUM_BUFFER_SIZE) {
                    /* the current block is too large, continue with the next
                     * block */
                    log.warning("current block size too large: " + blockSize);
                    continue;
                } else if (blockPointer + blockSize > mFileSize) {
                    log.warning("current block larger than file size: "
                            + blockSize);
                    return;
                }

                /* seek to the current block in the map file */
                /* read the current block into the buffer */
                ReadBuffer readBuffer = new ReadBuffer(mInputChannel);
                if (!readBuffer.readFromFile(subFileParameter.startAddress + blockPointer, blockSize)) {
                    /* skip the current block */
                    log.warning("reading current block has failed: " + blockSize);
                    return;
                }

                /* calculate the top-left coordinates of the underlying tile */
                double tileLatitudeDeg =
                        Projection.tileYToLatitude(subFileParameter.boundaryTileTop + row,
                                subFileParameter.baseZoomLevel);
                double tileLongitudeDeg =
                        Projection.tileXToLongitude(subFileParameter.boundaryTileLeft + column,
                                subFileParameter.baseZoomLevel);

                mTileLatitude = (int) (tileLatitudeDeg * 1E6);
                mTileLongitude = (int) (tileLongitudeDeg * 1E6);

                processBlock(queryParams, subFileParameter, mapDataSink, boundingBox, selector, mapReadResult, readBuffer);
            }
        }
    }

    /**
     * Processes the block signature, if present.
     *
     * @return true if the block signature could be processed successfully,
     * false otherwise.
     */
    private boolean processBlockSignature(ReadBuffer readBuffer) {
        if (mDebugFile) {
            /* get and check the block signature */
            mSignatureBlock = readBuffer.readUTF8EncodedString(SIGNATURE_LENGTH_BLOCK);
            if (!mSignatureBlock.startsWith("###TileStart")) {
                log.warning("invalid block signature: " + mSignatureBlock);
                return false;
            }
        }
        return true;
    }

    /**
     * Processes the given number of POIs.
     *
     * @param mapDataSink  the callback which handles the extracted POIs.
     * @param numberOfPois how many POIs should be processed.
     * @return true if the POIs could be processed successfully, false
     * otherwise.
     */
    private boolean processPOIs(ITileDataSink mapDataSink, int numberOfPois, BoundingBox boundingBox,
                                boolean filterRequired, List<PointOfInterest> pois, ReadBuffer readBuffer) {
        Tag[] poiTags = mTileSource.fileInfo.poiTags;
        MapElement e = mElem;

        for (int elementCounter = numberOfPois; elementCounter != 0; --elementCounter) {
            /* reset to common tag position */
            e.tags.clear();

            if (mDebugFile) {
                /* get and check the POI signature */
                mSignaturePoi = readBuffer.readUTF8EncodedString(SIGNATURE_LENGTH_POI);
                if (!mSignaturePoi.startsWith("***POIStart")) {
                    log.warning("invalid POI signature: " + mSignaturePoi);
                    log.warning(DEBUG_SIGNATURE_BLOCK + mSignatureBlock);
                    return false;
                }
            }

            /* get the POI latitude offset (VBE-S) */
            int latitude = mTileLatitude + readBuffer.readSignedInt();
            /* get the POI longitude offset (VBE-S) */
            int longitude = mTileLongitude + readBuffer.readSignedInt();

            /* get the special byte which encodes multiple flags */
            byte specialByte = readBuffer.readByte();

            /* bit 1-4 represent the layer */
            byte layer = (byte) ((specialByte & POI_LAYER_BITMASK) >>> POI_LAYER_SHIFT);

            /* bit 5-8 represent the number of tag IDs */
            byte numberOfTags = (byte) (specialByte & POI_NUMBER_OF_TAGS_BITMASK);

            if (numberOfTags != 0) {
                if (!readBuffer.readTags(e.tags, poiTags, numberOfTags))
                    return false;
            }

            /* get the feature bitmask (1 byte) */
            byte featureByte = readBuffer.readByte();

            /* bit 1-3 enable optional features
             * check if the POI has a name */
            if ((featureByte & POI_FEATURE_NAME) != 0) {
                String str = mTileSource.extractLocalized(readBuffer.readUTF8EncodedString());
                e.tags.add(new Tag(Tag.KEY_NAME, str, false));
            }

            /* check if the POI has a house number */
            if ((featureByte & POI_FEATURE_HOUSE_NUMBER) != 0) {
                String str = readBuffer.readUTF8EncodedString();
                e.tags.add(new Tag(Tag.KEY_HOUSE_NUMBER, str, false));
            }

            /* check if the POI has an elevation */
            if ((featureByte & POI_FEATURE_ELEVATION) != 0) {
                String str = Integer.toString(readBuffer.readSignedInt());
                e.tags.add(new Tag(Tag.KEY_ELE, str, false));
            }
            mTileProjection.projectPoint(latitude, longitude, e);

            if (!mTileSeparator.separate(e))
                continue;

            e.setLayer(layer);

            PointOfInterest poi = null;
            if (pois != null) {
                List<Tag> tags = new ArrayList<>();
                for (int i = 0; i < e.tags.size(); i++)
                    tags.add(e.tags.get(i));
                GeoPoint position = new GeoPoint(latitude, longitude);
                // depending on the zoom level configuration the poi can lie outside
                // the tile requested, we filter them out here
                if (!filterRequired || boundingBox.contains(position)) {
                    poi = new PointOfInterest(layer, tags, position);
                    pois.add(poi);
                }
            }

            if (mapDataSink != null) {
                if (!deduplicate || poi == null || (mapDataSink instanceof TileDataSink && ((TileDataSink) mapDataSink).hashPois.add(poi.hashCode())))
                    mapDataSink.process(e);
            }
        }

        return true;
    }

    private boolean processWayDataBlock(MapElement e, boolean doubleDeltaEncoding, boolean isLine, List<GeoPoint[]> wayCoordinates, int[] labelPosition, ReadBuffer readBuffer) {
        /* get and check the number of way coordinate blocks (VBE-U) */
        int numBlocks = readBuffer.readUnsignedInt();
        if (numBlocks < 1 || numBlocks > Short.MAX_VALUE) {
            log.warning("invalid number of way coordinate blocks: " + numBlocks);
            return false;
        }

        int[] wayLengths = e.ensureIndexSize(numBlocks, false);
        if (wayLengths.length > numBlocks)
            wayLengths[numBlocks] = -1;

        /* read the way coordinate blocks */
        for (int coordinateBlock = 0; coordinateBlock < numBlocks; ++coordinateBlock) {
            int numWayNodes = readBuffer.readUnsignedInt();

            if (numWayNodes < 2 || numWayNodes > Short.MAX_VALUE) {
                log.warning("invalid number of way nodes: " + numWayNodes);
                logDebugSignatures();
                return false;
            }

            /* each way node consists of latitude and longitude */
            int len = numWayNodes * 2;

            // create the array which will store the current way segment
            GeoPoint[] waySegment = null;
            if (wayCoordinates != null)
                waySegment = new GeoPoint[numWayNodes];

            // label position must be set on first coordinate block
            wayLengths[coordinateBlock] = decodeWayNodes(doubleDeltaEncoding, e, len, isLine,
                    coordinateBlock == 0 ? labelPosition : null, waySegment, readBuffer);

            if (wayCoordinates != null)
                wayCoordinates.add(waySegment);
        }

        return true;
    }

    private int decodeWayNodes(boolean doubleDelta, MapElement e, int length, boolean isLine, int[] labelPosition, GeoPoint[] waySegment, ReadBuffer readBuffer) {
        int[] buffer = mIntBuffer;
        readBuffer.readSignedInt(buffer, length);

        float[] outBuffer = e.ensurePointSize(e.pointNextPos + length, true);
        int outPos = e.pointNextPos;
        float pLat, pLon;

        /* first node latitude single-delta offset */
        int rawLat = mTileLatitude + buffer[0];
        int rawLon = mTileLongitude + buffer[1];

        float firstLat = pLat = mTileProjection.projectLat(rawLat);
        float firstLon = pLon = mTileProjection.projectLon(rawLon);

        outBuffer[outPos++] = pLon;
        outBuffer[outPos++] = pLat;
        int cnt = 2;

        /* reset label position single-delta to first way node */
        if (labelPosition != null) {
            labelPosition[1] = rawLat + labelPosition[1];
            labelPosition[0] = rawLon + labelPosition[0];
        }

        if (waySegment != null)
            waySegment[0] = new GeoPoint(rawLat / 1E6, rawLon / 1E6);

        int deltaLat = 0;
        int deltaLon = 0;

        for (int pos = 2; pos < length; pos += 2) {
            if (doubleDelta) {
                deltaLat = buffer[pos] + deltaLat;
                deltaLon = buffer[pos + 1] + deltaLon;
            } else {
                deltaLat = buffer[pos];
                deltaLon = buffer[pos + 1];
            }
            rawLat += deltaLat;
            rawLon += deltaLon;

            if (waySegment != null)
                waySegment[pos / 2] = new GeoPoint(rawLat / 1E6, rawLon / 1E6);

            float lat = mTileProjection.projectLat(rawLat);
            float lon = mTileProjection.projectLon(rawLon);

            if (pos == length - 2) {
                boolean line = isLine || (lon != firstLon || lat != firstLat);

                if (line) {
                    outBuffer[outPos++] = lon;
                    outBuffer[outPos++] = lat;
                    cnt += 2;
                }

                if (e.type == GeometryType.NONE)
                    e.type = line ? GeometryType.LINE : GeometryType.POLY;

            } else if (lat == pLat && lon == pLon) {
                /* drop small distance intermediate nodes */
                //log.debug("drop zero delta ");
            } else if (Parameters.SIMPLIFICATION_TOLERANCE == 0
                    || (isLine
                    || e.tags.contains(Constants.TAG_MAPSFORGE_ISSEA)
                    || e.tags.contains(Constants.TAG_MAPSFORGE_NOSEA)
                    || e.tags.contains(Constants.TAG_MAPSFORGE_SEA)
                    || e.tags.contains(Constants.TAG_FREIZEITKARTE_LAND)
                    || e.tags.contains(Constants.TAG_FREIZEITKARTE_MEER)
                    || e.tags.contains(Parameters.SIMPLIFICATION_EXCEPTIONS)
                    || deltaLon > minDeltaLon || deltaLon < -minDeltaLon
                    || deltaLat > minDeltaLat || deltaLat < -minDeltaLat)) {
                // Point reduction except lines and land/sea polygons
                outBuffer[outPos++] = pLon = lon;
                outBuffer[outPos++] = pLat = lat;
                cnt += 2;
            }
        }

        e.pointNextPos = outPos;

        return cnt;
    }

    private int stringOffset = -1;

    /**
     * Processes the given number of ways.
     *
     * @param queryParameters the parameters of the current query.
     * @param mapDataSink     the callback which handles the extracted ways.
     * @param numberOfWays    how many ways should be processed.
     * @return true if the ways could be processed successfully, false
     * otherwise.
     */
    private boolean processWays(QueryParameters queryParameters, ITileDataSink mapDataSink,
                                int numberOfWays, BoundingBox boundingBox, boolean filterRequired,
                                Selector selector, List<Way> ways, ReadBuffer readBuffer) {

        Tag[] wayTags = mTileSource.fileInfo.wayTags;
        MapElement e = mElem;

        int wayDataBlocks;

        // skip string block
        int stringsSize = 0;
        stringOffset = 0;

        if (mTileSource.experimental) {
            stringsSize = readBuffer.readUnsignedInt();
            stringOffset = readBuffer.getBufferPosition();
            readBuffer.skipBytes(stringsSize);
        }

        //setTileClipping(queryParameters);

        for (int elementCounter = numberOfWays; elementCounter != 0; --elementCounter) {
            /* reset to common tag position */
            e.tags.clear();

            if (mDebugFile) {
                // get and check the way signature
                mSignatureWay = readBuffer.readUTF8EncodedString(SIGNATURE_LENGTH_WAY);
                if (!mSignatureWay.startsWith("---WayStart")) {
                    log.warning("invalid way signature: " + mSignatureWay);
                    log.warning(DEBUG_SIGNATURE_BLOCK + mSignatureBlock);
                    return false;
                }
            }

            if (queryParameters.useTileBitmask) {
                elementCounter = readBuffer.skipWays(queryParameters.queryTileBitmask,
                        elementCounter);

                if (elementCounter == 0)
                    return true;

                if (elementCounter < 0)
                    return false;

                if (mTileSource.experimental && readBuffer.lastTagPosition > 0) {
                    int pos = readBuffer.getBufferPosition();
                    readBuffer.setBufferPosition(readBuffer.lastTagPosition);

                    byte numberOfTags =
                            (byte) (readBuffer.readByte() & WAY_NUMBER_OF_TAGS_BITMASK);
                    if (!readBuffer.readTags(e.tags, wayTags, numberOfTags))
                        return false;

                    readBuffer.setBufferPosition(pos);
                }
            } else {
                int wayDataSize = readBuffer.readUnsignedInt();
                if (wayDataSize < 0) {
                    log.warning("invalid way data size: " + wayDataSize);
                    if (mDebugFile) {
                        log.warning(DEBUG_SIGNATURE_BLOCK + mSignatureBlock);
                    }
                    log.severe("BUG way 2");
                    return false;
                }

                /* ignore the way tile bitmask (2 bytes) */
                readBuffer.skipBytes(2);
            }

            /* get the special byte which encodes multiple flags */
            byte specialByte = readBuffer.readByte();

            /* bit 1-4 represent the layer */
            byte layer = (byte) ((specialByte & WAY_LAYER_BITMASK) >>> WAY_LAYER_SHIFT);
            /* bit 5-8 represent the number of tag IDs */
            byte numberOfTags = (byte) (specialByte & WAY_NUMBER_OF_TAGS_BITMASK);

            if (numberOfTags != 0) {
                if (!readBuffer.readTags(e.tags, wayTags, numberOfTags))
                    return false;
            }

            /* get the feature bitmask (1 byte) */
            byte featureByte = readBuffer.readByte();

            /* bit 1-6 enable optional features */
            boolean featureWayDoubleDeltaEncoding =
                    (featureByte & WAY_FEATURE_DOUBLE_DELTA_ENCODING) != 0;

            boolean hasName = (featureByte & WAY_FEATURE_NAME) != 0;
            boolean hasHouseNr = (featureByte & WAY_FEATURE_HOUSE_NUMBER) != 0;
            boolean hasRef = (featureByte & WAY_FEATURE_REF) != 0;

            if (mTileSource.experimental) {
                if (hasName) {
                    int textPos = readBuffer.readUnsignedInt();
                    String str = mTileSource.extractLocalized(readBuffer.readUTF8EncodedStringAt(stringOffset + textPos));
                    e.tags.add(new Tag(Tag.KEY_NAME, str, false));
                }
                if (hasHouseNr) {
                    int textPos = readBuffer.readUnsignedInt();
                    String str = readBuffer.readUTF8EncodedStringAt(stringOffset + textPos);
                    e.tags.add(new Tag(Tag.KEY_HOUSE_NUMBER, str, false));
                }
                if (hasRef) {
                    int textPos = readBuffer.readUnsignedInt();
                    String str = readBuffer.readUTF8EncodedStringAt(stringOffset + textPos);
                    e.tags.add(new Tag(Tag.KEY_REF, str, false));
                }
            } else {
                if (hasName) {
                    String str = mTileSource.extractLocalized(readBuffer.readUTF8EncodedString());
                    e.tags.add(new Tag(Tag.KEY_NAME, str, false));
                }
                if (hasHouseNr) {
                    String str = readBuffer.readUTF8EncodedString();
                    e.tags.add(new Tag(Tag.KEY_HOUSE_NUMBER, str, false));
                }
                if (hasRef) {
                    String str = readBuffer.readUTF8EncodedString();
                    e.tags.add(new Tag(Tag.KEY_REF, str, false));
                }
            }

            int[] labelPosition = null;
            if ((featureByte & WAY_FEATURE_LABEL_POSITION) != 0) {
                labelPosition = readOptionalLabelPosition(readBuffer);
            }

            if ((featureByte & WAY_FEATURE_DATA_BLOCKS_BYTE) != 0) {
                wayDataBlocks = readBuffer.readUnsignedInt();

                if (wayDataBlocks < 1) {
                    log.warning("invalid number of way data blocks: " + wayDataBlocks);
                    logDebugSignatures();
                    return false;
                }
            } else {
                wayDataBlocks = 1;
            }

            /* some guessing if feature is a line or a polygon */
            boolean linearFeature = !OSMUtils.isArea(e);

            for (int wayDataBlock = 0; wayDataBlock < wayDataBlocks; wayDataBlock++) {
                e.clear();

                List<GeoPoint[]> wayNodes = null;
                if (ways != null)
                    wayNodes = new ArrayList<>();

                if (!processWayDataBlock(e, featureWayDoubleDeltaEncoding, linearFeature, wayNodes, labelPosition, readBuffer))
                    return false;

                /* drop invalid outer ring */
                if (e.isPoly() && e.index[0] < 6) {
                    continue;
                }

                if (labelPosition != null && wayDataBlock == 0)
                    e.setLabelPosition(mTileProjection.projectLon(labelPosition[0]), mTileProjection.projectLat(labelPosition[1]));
                else
                    e.labelPosition = null;

                // When a way will be rendered then typically a label / symbol will be applied
                // by the render theme. If the way does not come with a defined labelPosition
                // we should calculate a position, that is based on all points of the given way.
                // This "auto" position calculation is also done in the LabelTileLoaderHook class
                // but then the points of the way have been already reduced cause of the clipping
                // that is happening. So the suggestion here is to calculate the centroid of the way
                // and use that as centroidPosition of the element.
                if (Parameters.POLY_CENTROID && e.labelPosition == null) {
                    float x = 0;
                    float y = 0;
                    int n = e.index[0];
                    for (int i = 0; i < n; ) {
                        x += e.points[i++];
                        y += e.points[i++];
                    }
                    x /= (n / 2);
                    y /= (n / 2);
                    e.setCentroidPosition(x, y);
                }

                // Avoid clipping for buildings, which slows rendering.
                // But clip everything if buildings are displayed.
                if (!e.tags.containsKey(Tag.KEY_BUILDING)
                        && !e.tags.containsKey(Tag.KEY_BUILDING_PART)) {
                    if (!mTileClipper.clip(e))
                        continue;
                } else if (queryParameters.queryZoomLevel >= BuildingLayer.MIN_ZOOM) {
                    if (!mTileSeparator.separate(e))
                        continue;
                }
                e.simplify(1, true);

                e.setLayer(layer);

                Way way = null;
                if (ways != null) {
                    BoundingBox wayFilterBbox = boundingBox.extendMeters(wayFilterDistance);
                    GeoPoint[][] wayNodesArray = wayNodes.toArray(new GeoPoint[wayNodes.size()][]);
                    if (!filterRequired || !wayFilterEnabled || wayFilterBbox.intersectsArea(wayNodesArray)) {
                        List<Tag> tags = new ArrayList<>();
                        for (int i = 0; i < e.tags.size(); i++)
                            tags.add(e.tags.get(i));
                        if (Selector.ALL == selector || hasName || hasHouseNr || hasRef || wayAsLabelTagFilter(tags)) {
                            GeoPoint labelPos = labelPosition != null ? new GeoPoint(labelPosition[1] / 1E6, labelPosition[0] / 1E6) : null;
                            way = new Way(layer, tags, wayNodesArray, labelPos, e.type);
                            ways.add(way);
                        }
                    }
                }

                if (mapDataSink != null) {
                    if (!deduplicate || way == null || (mapDataSink instanceof TileDataSink && ((TileDataSink) mapDataSink).hashWays.add(way.hashCode()))) {
                        if (mapDataSink instanceof TileDataSink)
                            e.level = e.isLine() ? ((TileDataSink) mapDataSink).levels : ((TileDataSink) mapDataSink).level;
                        mapDataSink.process(e);
                    }
                }
            }
        }

        return true;
    }

    /**
     * Reads only named items for a tile.
     *
     * @param tile tile for which data is requested.
     * @return label data for the tile.
     */
    public MapReadResult readNamedItems(Tile tile) {
        return readMapData(tile, tile, Selector.NAMED);
    }

    /**
     * Reads data for an area defined by the tile in the upper left and the tile in
     * the lower right corner.
     * Precondition: upperLeft.tileX <= lowerRight.tileX && upperLeft.tileY <= lowerRight.tileY
     *
     * @param upperLeft  tile that defines the upper left corner of the requested area.
     * @param lowerRight tile that defines the lower right corner of the requested area.
     * @return map data for the tile.
     */
    public MapReadResult readNamedItems(Tile upperLeft, Tile lowerRight) {
        return readMapData(upperLeft, lowerRight, Selector.NAMED);
    }

    /**
     * Reads all map data for the area covered by the given tile at the tile zoom level.
     *
     * @param tile defines area and zoom level of read map data.
     * @return the read map data.
     */
    public MapReadResult readMapData(Tile tile) {
        return readMapData(tile, tile, Selector.ALL);
    }

    /**
     * Reads data for an area defined by the tile in the upper left and the tile in
     * the lower right corner.
     * Precondition: upperLeft.tileX <= lowerRight.tileX && upperLeft.tileY <= lowerRight.tileY
     *
     * @param upperLeft  tile that defines the upper left corner of the requested area.
     * @param lowerRight tile that defines the lower right corner of the requested area.
     * @return map data for the tile.
     */
    public MapReadResult readMapData(Tile upperLeft, Tile lowerRight) {
        return readMapData(upperLeft, lowerRight, Selector.ALL);
    }

    private MapReadResult readMapData(Tile upperLeft, Tile lowerRight, Selector selector) {
        if (mTileSource.fileHeader == null)
            return null;

        MapReadResult mapReadResult = new MapReadResult();

        if (mIntBuffer == null)
            mIntBuffer = new int[Short.MAX_VALUE * 2];

        try {
            mTileProjection.setTile(upperLeft);

            QueryParameters queryParameters = new QueryParameters();
            queryParameters.queryZoomLevel =
                    mTileSource.fileHeader.getQueryZoomLevel(upperLeft.zoomLevel);

            /* get and check the sub-file for the query zoom level */
            SubFileParameter subFileParameter =
                    mTileSource.fileHeader.getSubFileParameter(queryParameters.queryZoomLevel);

            if (subFileParameter == null) {
                log.warning("no sub-file for zoom level: "
                        + queryParameters.queryZoomLevel);

                return null;
            }

            QueryCalculations.calculateBaseTiles(queryParameters, upperLeft, lowerRight, subFileParameter);
            QueryCalculations.calculateBlocks(queryParameters, subFileParameter);
            processBlocks(queryParameters, subFileParameter, Tile.getBoundingBox(upperLeft, lowerRight), selector, mapReadResult);
        } catch (IOException e) {
            log.severe(e.toString());
            return null;
        }

        return mapReadResult;
    }

    private int[] readOptionalLabelPosition(ReadBuffer readBuffer) {
        int[] labelPosition = new int[2];

        /* get the label position latitude offset (VBE-S) */
        labelPosition[1] = readBuffer.readSignedInt();

        /* get the label position longitude offset (VBE-S) */
        labelPosition[0] = readBuffer.readSignedInt();

        return labelPosition;
    }

    /**
     * Reads only POI data for tile.
     *
     * @param tile tile for which data is requested.
     * @return POI data for the tile.
     */
    public MapReadResult readPoiData(Tile tile) {
        return readMapData(tile, tile, Selector.POIS);
    }

    /**
     * Reads POI data for an area defined by the tile in the upper left and the tile in
     * the lower right corner.
     * This implementation takes the data storage of a MapFile into account for greater efficiency.
     *
     * @param upperLeft  tile that defines the upper left corner of the requested area.
     * @param lowerRight tile that defines the lower right corner of the requested area.
     * @return map data for the tile.
     */
    public MapReadResult readPoiData(Tile upperLeft, Tile lowerRight) {
        return readMapData(upperLeft, lowerRight, Selector.POIS);
    }

    private int[][] readZoomTable(SubFileParameter subFileParameter, ReadBuffer readBuffer) {
        int rows = subFileParameter.zoomLevelMax - subFileParameter.zoomLevelMin + 1;
        int[][] zoomTable = new int[rows][2];

        int cumulatedNumberOfPois = 0;
        int cumulatedNumberOfWays = 0;

        for (int row = 0; row < rows; row++) {
            cumulatedNumberOfPois += readBuffer.readUnsignedInt();
            cumulatedNumberOfWays += readBuffer.readUnsignedInt();

            zoomTable[row][0] = cumulatedNumberOfPois;
            zoomTable[row][1] = cumulatedNumberOfWays;
        }

        return zoomTable;
    }

    /**
     * Restricts returns of data to zoom level range specified. This can be used to restrict
     * the use of this map data base when used in MultiMapFile settings.
     *
     * @param minZoom minimum zoom level supported
     * @param maxZoom maximum zoom level supported
     */
    public void restrictToZoomRange(int minZoom, int maxZoom) {
        this.zoomLevelMax = maxZoom;
        this.zoomLevelMin = minZoom;
    }

    /**
     * Returns true if MapFile contains tile.
     *
     * @param tile tile to be rendered.
     * @return true if tile is part of database.
     */
    public boolean supportsTile(Tile tile) {
        return supportsArea(tile.getBoundingBox(), tile.zoomLevel);
    }

    /**
     * Returns true if MapFile contains a complete tile.
     *
     * @param tile tile to be rendered.
     * @return true if complete tile is part of database.
     */
    public boolean supportsFullTile(Tile tile) {
        return supportsFullArea(tile.getBoundingBox(), tile.zoomLevel);
    }

    /**
     * Returns true if MapFile covers (even partially) certain area in required zoom level.
     *
     * @param boundingBox area we test
     * @param zoomLevel   zoom level we test
     * @return true if area is part of the database.
     */
    public boolean supportsArea(BoundingBox boundingBox, int zoomLevel) {
        return boundingBox.intersects(mTileSource.getMapInfo().boundingBox)
                && (zoomLevel >= this.zoomLevelMin && zoomLevel <= this.zoomLevelMax);
    }

    /**
     * Returns true if MapFile covers certain area completely in required zoom level.
     *
     * @param boundingBox area we test
     * @param zoomLevel   zoom level we test
     * @return true if complete area is part of the database.
     */
    public boolean supportsFullArea(BoundingBox boundingBox, int zoomLevel) {
        final BoundingBox bbox1 = mTileSource.getMapInfo().boundingBox;
        final BoundingBox bbox2 = boundingBox;
        return bbox1.intersects(bbox2)
                && zoomLevel >= this.zoomLevelMin
                && zoomLevel <= this.zoomLevelMax
                && bbox1.contains(bbox2.maxLatitudeE6, bbox2.maxLongitudeE6)
                && bbox1.contains(bbox2.minLatitudeE6, bbox2.minLongitudeE6)
                && bbox1.contains(bbox2.maxLatitudeE6, bbox2.minLongitudeE6)
                && bbox1.contains(bbox2.minLatitudeE6, bbox2.maxLongitudeE6);
    }

    /**
     * Returns true if a way should be included in the result set for readLabels()
     * By default only ways with names, house numbers or a ref are included in the result set
     * of readLabels(). This is to reduce the set of ways as much as possible to save memory.
     *
     * @param tags the tags associated with the way
     * @return true if the way should be included in the result set
     */
    public boolean wayAsLabelTagFilter(List<Tag> tags) {
        return false;
    }

    /**
     * The Selector enum is used to specify which data subset is to be retrieved from a MapFile:
     * ALL: all data (as in version 0.6.0)
     * POIS: only poi data, no ways (new after 0.6.0)
     * NAMED: poi data and ways that have a name (new after 0.6.0)
     */
    private enum Selector {
        /**
         * All data.
         */
        ALL,
        /**
         * Only POI data.
         */
        POIS,
        /**
         * POI data and ways that have a name.
         */
        NAMED
    }

    static class TileProjection {
        private static final double COORD_SCALE = 1000000.0;

        long dx, dy;
        double divx, divy;

        void setTile(Tile tile) {
            /* tile position in pixels at tile zoom */
            long x = tile.tileX * Tile.SIZE;
            long y = tile.tileY * Tile.SIZE + Tile.SIZE;

            /* size of the map in pixel at tile zoom */
            long mapExtents = Tile.SIZE << tile.zoomLevel;

            /* offset relative to lat/lon == 0 */
            dx = (x - (mapExtents >> 1));
            dy = (y - (mapExtents >> 1));

            /* scales longitude(1e6) to map-pixel */
            divx = (180.0 * COORD_SCALE) / (mapExtents >> 1);

            /* scale latitude to map-pixel */
            divy = (Math.PI * 2.0) / (mapExtents >> 1);
        }

        public void projectPoint(int lat, int lon, MapElement out) {
            out.clear();
            out.startPoints();
            out.addPoint(projectLon(lon), projectLat(lat));
        }

        public float projectLat(double lat) {
            double s = Math.sin(lat * ((Math.PI / 180) / COORD_SCALE));
            double r = Math.log((1.0 + s) / (1.0 - s));

            return Tile.SIZE - (float) (r / divy + dy);
        }

        public float projectLon(double lon) {
            return (float) (lon / divx - dx);
        }

        void project(MapElement e) {

            float[] coords = e.points;
            int[] indices = e.index;

            int inPos = 0;
            int outPos = 0;

            boolean isPoly = e.isPoly();

            for (int idx = 0, m = indices.length; idx < m; idx++) {
                int len = indices[idx];
                if (len == 0)
                    continue;
                if (len < 0)
                    break;

                float lat, lon, pLon = 0, pLat = 0;
                int cnt = 0, first = outPos;

                for (int end = inPos + len; inPos < end; inPos += 2) {
                    lon = projectLon(coords[inPos]);
                    lat = projectLat(coords[inPos + 1]);

                    if (cnt != 0) {
                        /* drop small distance intermediate nodes */
                        if (lat == pLat && lon == pLon) {
                            //log.debug("drop zero delta ");
                            continue;
                        }
                    }
                    coords[outPos++] = pLon = lon;
                    coords[outPos++] = pLat = lat;
                    cnt += 2;
                }

                if (isPoly && coords[first] == pLon && coords[first + 1] == pLat) {
                    /* remove identical start/end point */
                    //log.debug("drop closing point {}", e);
                    indices[idx] = (short) (cnt - 2);
                    outPos -= 2;
                } else {
                    indices[idx] = (short) cnt;
                }
            }
            if (e.labelPosition != null) {
                e.labelPosition.x = projectLon(e.labelPosition.x);
                e.labelPosition.y = projectLat(e.labelPosition.y);
            }
            if (e.centroidPosition != null) {
                e.centroidPosition.x = projectLon(e.centroidPosition.x);
                e.centroidPosition.y = projectLat(e.centroidPosition.y);
            }
        }
    }
}
