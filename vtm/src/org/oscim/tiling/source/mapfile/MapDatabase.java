/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2013, 2014 Hannes Janetzek
 * Copyright 2016 devemux86
 * Copyright 2016 Andrey Novikov
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

import org.oscim.core.GeometryBuffer.GeometryType;
import org.oscim.core.MapElement;
import org.oscim.core.MercatorProjection;
import org.oscim.core.Tag;
import org.oscim.core.Tile;
import org.oscim.layers.tile.MapTile;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.source.mapfile.header.SubFileParameter;
import org.oscim.utils.geom.TileClipper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;

import static org.oscim.core.GeometryBuffer.GeometryType.LINE;
import static org.oscim.core.GeometryBuffer.GeometryType.POLY;
import static org.oscim.tiling.QueryResult.FAILED;
import static org.oscim.tiling.QueryResult.SUCCESS;

/**
 * A class for reading binary map files.
 *
 * @see <a
 * href="http://code.google.com/p/mapsforge/wiki/SpecificationBinaryMapFile">Specification</a>
 */
public class MapDatabase implements ITileDataSource {
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

    static final Logger log = LoggerFactory.getLogger(MapDatabase.class);

    /**
     * Maximum way nodes sequence length which is considered as valid.
     */
    private static final int MAXIMUM_WAY_NODES_SEQUENCE_LENGTH = 8192 * 10;

    /**
     * Maximum number of map objects in the zoom table which is considered as
     * valid.
     */
    private static final int MAXIMUM_ZOOM_TABLE_OBJECTS = 65536 * 2;

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

    private long mFileSize;
    private boolean mDebugFile;
    private RandomAccessFile mInputFile;
    private ReadBuffer mReadBuffer;
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

    private final MapFileTileSource mTileSource;

    public MapDatabase(MapFileTileSource tileSource) throws IOException {
        mTileSource = tileSource;
        try {
            /* open the file in read only mode */
            mInputFile = new RandomAccessFile(tileSource.mapFile, "r");
            mFileSize = mInputFile.length();
            mReadBuffer = new ReadBuffer(mInputFile);

        } catch (IOException e) {
            log.error(e.getMessage());
            /* make sure that the file is closed */
            dispose();
            throw new IOException();
        }

        mTileProjection = new TileProjection();
        mTileClipper = new TileClipper(0, 0, 0, 0);
    }

    public MapFileTileSource getTileSource() {
        return mTileSource;
    }

    @Override
    public void query(MapTile tile, ITileDataSink sink) {

        if (mTileSource.fileHeader == null) {
            sink.completed(FAILED);
            return;
        }

        if (mIntBuffer == null)
            mIntBuffer = new int[MAXIMUM_WAY_NODES_SEQUENCE_LENGTH * 2];

        try {
            mTileProjection.setTile(tile);
            //mTile = tile;

            /* size of tile in map coordinates; */

            QueryParameters queryParameters = new QueryParameters();
            queryParameters.queryZoomLevel =
                    mTileSource.fileHeader.getQueryZoomLevel(tile.zoomLevel);

            /* get and check the sub-file for the query zoom level */
            SubFileParameter subFileParameter =
                    mTileSource.fileHeader.getSubFileParameter(queryParameters.queryZoomLevel);

            if (subFileParameter == null) {
                log.warn("no sub-file for zoom level: "
                        + queryParameters.queryZoomLevel);

                sink.completed(FAILED);
                return;
            }

            QueryCalculations.calculateBaseTiles(queryParameters, tile, subFileParameter);
            QueryCalculations.calculateBlocks(queryParameters, subFileParameter);
            processBlocks(sink, queryParameters, subFileParameter);
        } catch (IOException e) {
            log.error(e.getMessage());
            sink.completed(FAILED);
            return;
        }

        sink.completed(SUCCESS);
    }

    @Override
    public void dispose() {
        mReadBuffer = null;
        if (mInputFile != null) {

            try {
                mInputFile.close();
                mInputFile = null;
            } catch (IOException e) {
                log.error(e.getMessage());
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
            log.warn(DEBUG_SIGNATURE_WAY + mSignatureWay);
            log.warn(DEBUG_SIGNATURE_BLOCK + mSignatureBlock);
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
                              SubFileParameter subFileParameter, ITileDataSink mapDataSink) {

        if (!processBlockSignature()) {
            return;
        }

        int[][] zoomTable = readZoomTable(subFileParameter);
        if (zoomTable == null) {
            return;
        }
        int zoomTableRow = queryParameters.queryZoomLevel - subFileParameter.zoomLevelMin;
        int poisOnQueryZoomLevel = zoomTable[zoomTableRow][0];
        int waysOnQueryZoomLevel = zoomTable[zoomTableRow][1];

        /* get the relative offset to the first stored way in the block */
        int firstWayOffset = mReadBuffer.readUnsignedInt();
        if (firstWayOffset < 0) {
            log.warn(INVALID_FIRST_WAY_OFFSET + firstWayOffset);
            if (mDebugFile) {
                log.warn(DEBUG_SIGNATURE_BLOCK + mSignatureBlock);
            }
            return;
        }

        /* add the current buffer position to the relative first way offset */
        firstWayOffset += mReadBuffer.getBufferPosition();
        if (firstWayOffset > mReadBuffer.getBufferSize()) {
            log.warn(INVALID_FIRST_WAY_OFFSET + firstWayOffset);
            if (mDebugFile) {
                log.warn(DEBUG_SIGNATURE_BLOCK + mSignatureBlock);
            }
            return;
        }

        if (!processPOIs(mapDataSink, poisOnQueryZoomLevel)) {
            return;
        }

        /* finished reading POIs, check if the current buffer position is valid */
        if (mReadBuffer.getBufferPosition() > firstWayOffset) {
            log.warn("invalid buffer position: " + mReadBuffer.getBufferPosition());
            if (mDebugFile) {
                log.warn(DEBUG_SIGNATURE_BLOCK + mSignatureBlock);
            }
            return;
        }

        /* move the pointer to the first way */
        mReadBuffer.setBufferPosition(firstWayOffset);

        if (!processWays(queryParameters, mapDataSink, waysOnQueryZoomLevel)) {
            return;
        }

    }

    //    private long mCurrentRow;
    //    private long mCurrentCol;

    private int xmin, ymin, xmax, ymax;

    private void setTileClipping(QueryParameters queryParameters, long mCurrentRow, long mCurrentCol) {
        long numRows = queryParameters.toBlockY - queryParameters.fromBlockY;
        long numCols = queryParameters.toBlockX - queryParameters.fromBlockX;

        //log.debug(numCols + "/" + numRows + " " + mCurrentCol + " " + mCurrentRow);
        xmin = -16;
        ymin = -16;
        xmax = Tile.SIZE + 16;
        ymax = Tile.SIZE + 16;

        if (numRows > 0) {
            int w = (int) (Tile.SIZE / (numCols + 1));
            int h = (int) (Tile.SIZE / (numRows + 1));

            if (mCurrentCol > 0)
                xmin = (int) (mCurrentCol * w);

            if (mCurrentCol < numCols)
                xmax = (int) (mCurrentCol * w + w);

            if (mCurrentRow > 0)
                ymin = (int) (mCurrentRow * h);

            if (mCurrentRow < numRows)
                ymax = (int) (mCurrentRow * h + h);
        }
        mTileClipper.setRect(xmin, ymin, xmax, ymax);
    }

    //private final static Tag mWaterTag = new Tag("natural", "water");

    private void processBlocks(ITileDataSink mapDataSink, QueryParameters queryParams,
                               SubFileParameter subFileParameter) throws IOException {

        /* read and process all blocks from top to bottom and from left to right */
        for (long row = queryParams.fromBlockY; row <= queryParams.toBlockY; row++) {
            for (long column = queryParams.fromBlockX; column <= queryParams.toBlockX; column++) {
                //mCurrentCol = column - queryParameters.fromBlockX;
                //mCurrentRow = row - queryParameters.fromBlockY;

                setTileClipping(queryParams,
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
                    log.warn("invalid current block pointer: " + blockPointer);
                    log.warn("subFileSize: " + subFileParameter.subFileSize);
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
                        log.warn("invalid next block pointer: " + nextBlockPointer);
                        log.warn("sub-file size: " + subFileParameter.subFileSize);
                        return;
                    }
                }

                /* calculate the size of the current block */
                int blockSize = (int) (nextBlockPointer - blockPointer);
                if (blockSize < 0) {
                    log.warn("current block size must not be negative: "
                            + blockSize);
                    return;
                } else if (blockSize == 0) {
                    /* the current block is empty, continue with the next block */
                    continue;
                } else if (blockSize > ReadBuffer.MAXIMUM_BUFFER_SIZE) {
                    /* the current block is too large, continue with the next
                     * block */
                    log.warn("current block size too large: " + blockSize);
                    continue;
                } else if (blockPointer + blockSize > mFileSize) {
                    log.warn("current block larger than file size: "
                            + blockSize);
                    return;
                }

                /* seek to the current block in the map file */
                mInputFile.seek(subFileParameter.startAddress + blockPointer);

                /* read the current block into the buffer */
                if (!mReadBuffer.readFromFile(blockSize)) {
                    /* skip the current block */
                    log.warn("reading current block has failed: " + blockSize);
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

                processBlock(queryParams, subFileParameter, mapDataSink);
            }
        }
    }

    /**
     * Processes the block signature, if present.
     *
     * @return true if the block signature could be processed successfully,
     * false otherwise.
     */
    private boolean processBlockSignature() {
        if (mDebugFile) {
            /* get and check the block signature */
            mSignatureBlock = mReadBuffer.readUTF8EncodedString(SIGNATURE_LENGTH_BLOCK);
            if (!mSignatureBlock.startsWith("###TileStart")) {
                log.warn("invalid block signature: " + mSignatureBlock);
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
    private boolean processPOIs(ITileDataSink mapDataSink, int numberOfPois) {
        Tag[] poiTags = mTileSource.fileInfo.poiTags;
        MapElement e = mElem;

        int numTags = 0;

        for (int elementCounter = numberOfPois; elementCounter != 0; --elementCounter) {
            if (mDebugFile) {
                /* get and check the POI signature */
                mSignaturePoi = mReadBuffer.readUTF8EncodedString(SIGNATURE_LENGTH_POI);
                if (!mSignaturePoi.startsWith("***POIStart")) {
                    log.warn("invalid POI signature: " + mSignaturePoi);
                    log.warn(DEBUG_SIGNATURE_BLOCK + mSignatureBlock);
                    return false;
                }
            }

            /* get the POI latitude offset (VBE-S) */
            int latitude = mTileLatitude + mReadBuffer.readSignedInt();
            /* get the POI longitude offset (VBE-S) */
            int longitude = mTileLongitude + mReadBuffer.readSignedInt();

            /* get the special byte which encodes multiple flags */
            byte specialByte = mReadBuffer.readByte();

            /* bit 1-4 represent the layer */
            byte layer = (byte) ((specialByte & POI_LAYER_BITMASK) >>> POI_LAYER_SHIFT);

            /* bit 5-8 represent the number of tag IDs */
            byte numberOfTags = (byte) (specialByte & POI_NUMBER_OF_TAGS_BITMASK);

            if (numberOfTags != 0) {
                if (!mReadBuffer.readTags(e.tags, poiTags, numberOfTags))
                    return false;

                numTags = numberOfTags;
            }

            /* reset to common tag position */
            e.tags.numTags = numTags;

            /* get the feature bitmask (1 byte) */
            byte featureByte = mReadBuffer.readByte();

            /* bit 1-3 enable optional features
             * check if the POI has a name */
            if ((featureByte & POI_FEATURE_NAME) != 0) {
                String str = mTileSource.extractLocalized(mReadBuffer.readUTF8EncodedString());
                e.tags.add(new Tag(Tag.KEY_NAME, str, false));
            }

            /* check if the POI has a house number */
            if ((featureByte & POI_FEATURE_HOUSE_NUMBER) != 0) {
                String str = mReadBuffer.readUTF8EncodedString();
                e.tags.add(new Tag(Tag.KEY_HOUSE_NUMBER, str, false));
            }

            /* check if the POI has an elevation */
            if ((featureByte & POI_FEATURE_ELEVATION) != 0) {
                String str = Integer.toString(mReadBuffer.readSignedInt());
                e.tags.add(new Tag(Tag.KEY_ELE, str, false));
            }
            mTileProjection.projectPoint(latitude, longitude, e);

            e.setLayer(layer);

            mapDataSink.process(e);
        }

        return true;
    }

    private boolean processWayDataBlock(MapElement e, boolean doubleDeltaEncoding, boolean isLine) {
        /* get and check the number of way coordinate blocks (VBE-U) */
        int numBlocks = mReadBuffer.readUnsignedInt();
        if (numBlocks < 1 || numBlocks > Short.MAX_VALUE) {
            log.warn("invalid number of way coordinate blocks: " + numBlocks);
            return false;
        }

        int[] wayLengths = e.ensureIndexSize(numBlocks, false);
        if (wayLengths.length > numBlocks)
            wayLengths[numBlocks] = -1;

        /* read the way coordinate blocks */
        for (int coordinateBlock = 0; coordinateBlock < numBlocks; ++coordinateBlock) {
            int numWayNodes = mReadBuffer.readUnsignedInt();

            if (numWayNodes < 2 || numWayNodes > MAXIMUM_WAY_NODES_SEQUENCE_LENGTH) {
                log.warn("invalid number of way nodes: " + numWayNodes);
                logDebugSignatures();
                return false;
            }

            /* each way node consists of latitude and longitude */
            int len = numWayNodes * 2;

            /*if (doubleDeltaEncoding)
            {
                wayLengths[coordinateBlock] = decodeWayNodesDoubleDelta(e, len, isLine);
            } else {
                wayLengths[coordinateBlock] = decodeWayNodesSingleDelta(e, len, isLine);
            }*/
            wayLengths[coordinateBlock] = decodeWayNodes(doubleDeltaEncoding, e, len, isLine);
        }

        return true;
    }


    private int decodeWayNodesDoubleDelta(MapElement e, int length, boolean isLine) {
        // get the first way node latitude offset (VBE-S)
        int firstLatitude = mTileLatitude + mReadBuffer.readSignedInt();

        // get the first way node longitude offset (VBE-S)
        int firstLongitude = this.mTileLongitude + this.mReadBuffer.readSignedInt();

        double[] outBuffer = e.ensurePointSize(e.pointPos + length, true);
        int outPos = e.pointPos;
        // store the first way node
        int currentLat = firstLatitude;
        int currentLon = firstLongitude;
        outBuffer[outPos++] = currentLon;
        outBuffer[outPos++] = currentLat;
        int cnt = 2;

        int previousSingleDeltaLatitude = 0;
        int previousSingleDeltaLongitude = 0;
        for (int wayNodesIndex = 2; wayNodesIndex < length; wayNodesIndex += 2) {
            // get the way node latitude double-delta offset (VBE-S)
            int doubleDeltaLatitude = mReadBuffer.readSignedInt();

            // get the way node longitude double-delta offset (VBE-S)
            int doubleDeltaLongitude = mReadBuffer.readSignedInt();

            int singleDeltaLatitude = doubleDeltaLatitude + previousSingleDeltaLatitude;
            int singleDeltaLongitude = doubleDeltaLongitude + previousSingleDeltaLongitude;

            currentLat = currentLat + singleDeltaLatitude;
            currentLon = currentLon + singleDeltaLongitude;

            boolean line = isLine || (currentLon != firstLongitude && currentLat != firstLatitude);

            if (line)
            {
                outBuffer[outPos++] = currentLon;
                outBuffer[outPos++] = currentLat;
                cnt += 2;
            }

            previousSingleDeltaLatitude = singleDeltaLatitude;
            previousSingleDeltaLongitude = singleDeltaLongitude;
        }
        if (e.type == GeometryType.NONE)
            e.type = isLine ? LINE : POLY;

        e.pointPos = outPos;
        return cnt;
    }

    private int decodeWayNodesSingleDelta(MapElement e, int length, boolean isLine) {
        // get the first way node latitude single-delta offset (VBE-S)
        int firstLatitude = mTileLatitude + mReadBuffer.readSignedInt();

        // get the first way node longitude single-delta offset (VBE-S)
        int firstLongitude = mTileLongitude + mReadBuffer.readSignedInt();

        double[] outBuffer = e.ensurePointSize(e.pointPos + length, true);
        int outPos = e.pointPos;
        // store the first way node
        int currentLat = firstLatitude;
        int currentLon = firstLongitude;
        outBuffer[outPos++] = currentLon;
        outBuffer[outPos++] = currentLat;
        int cnt = 2;

        for (int wayNodesIndex = 2; wayNodesIndex < length; wayNodesIndex += 2) {
            // get the way node latitude offset (VBE-S)
            currentLat = currentLat + mReadBuffer.readSignedInt();

            // get the way node longitude offset (VBE-S)
            currentLon = currentLon + mReadBuffer.readSignedInt();


            boolean line = isLine || (currentLon != firstLongitude && currentLat != firstLatitude);
            if (line)
            {
                outBuffer[outPos++] = currentLon;
                outBuffer[outPos++] = currentLat;
                cnt += 2;
            }
        }
        if (e.type == GeometryType.NONE)
            e.type = isLine ? LINE : POLY;

        e.pointPos = outPos;
        return cnt;
    }

    private int decodeWayNodes(boolean doubleDelta, MapElement e, int length, boolean isLine) {
        int[] buffer = mIntBuffer;
        mReadBuffer.readSignedInt(buffer, length);

        double[] outBuffer = e.ensurePointSize(e.pointPos + length, true);
        int outPos = e.pointPos;
        double lat, lon;

        /* first node latitude single-delta offset */
        double firstLat = lat = mTileLatitude + buffer[0];
        double firstLon = lon = mTileLongitude + buffer[1];

        outBuffer[outPos++] = lon;
        outBuffer[outPos++] = lat;
        int cnt = 2;

        double deltaLat = 0;
        double deltaLon = 0;

        for (int pos = 2; pos < length; pos += 2) {
            if (doubleDelta) {
                deltaLat = buffer[pos] + deltaLat;
                deltaLon = buffer[pos + 1] + deltaLon;
            } else {
                deltaLat = buffer[pos];
                deltaLon = buffer[pos + 1];
            }
            lat += deltaLat;
            lon += deltaLon;

            if (pos == length - 2) {
                boolean line = isLine || (lon != firstLon && lat != firstLat);

                if (line) {
                    outBuffer[outPos++] = lon;
                    outBuffer[outPos++] = lat;
                    cnt += 2;
                }

                if (e.type == GeometryType.NONE)
                    e.type = line ? LINE : POLY;

            } else /*if ((deltaLon > minDeltaLon || deltaLon < -minDeltaLon
                    || deltaLat > minDeltaLat || deltaLat < -minDeltaLat)
                    || e.tags.contains("natural", "nosea"))*/ {
                // Avoid additional simplification
                // https://github.com/mapsforge/vtm/issues/39
                outBuffer[outPos++] = lon;
                outBuffer[outPos++] = lat;
                cnt += 2;
            }
        }

        e.pointPos = outPos;

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
    private boolean processWays(QueryParameters queryParameters,
                                ITileDataSink mapDataSink, int numberOfWays) {

        Tag[] wayTags = mTileSource.fileInfo.wayTags;
        MapElement e = mElem;

        int numTags = 0;

        int wayDataBlocks;

        // skip string block
        int stringsSize = 0;
        stringOffset = 0;

        if (mTileSource.experimental) {
            stringsSize = mReadBuffer.readUnsignedInt();
            stringOffset = mReadBuffer.getBufferPosition();
            mReadBuffer.skipBytes(stringsSize);
        }

        //setTileClipping(queryParameters);

        for (int elementCounter = numberOfWays; elementCounter != 0; --elementCounter) {
            if (mDebugFile) {
                // get and check the way signature
                mSignatureWay = mReadBuffer.readUTF8EncodedString(SIGNATURE_LENGTH_WAY);
                if (!mSignatureWay.startsWith("---WayStart")) {
                    log.warn("invalid way signature: " + mSignatureWay);
                    log.warn(DEBUG_SIGNATURE_BLOCK + mSignatureBlock);
                    return false;
                }
            }

            if (queryParameters.useTileBitmask) {
                elementCounter = mReadBuffer.skipWays(queryParameters.queryTileBitmask,
                        elementCounter);

                if (elementCounter == 0)
                    return true;

                if (elementCounter < 0)
                    return false;

                if (mTileSource.experimental && mReadBuffer.lastTagPosition > 0) {
                    int pos = mReadBuffer.getBufferPosition();
                    mReadBuffer.setBufferPosition(mReadBuffer.lastTagPosition);

                    byte numberOfTags =
                            (byte) (mReadBuffer.readByte() & WAY_NUMBER_OF_TAGS_BITMASK);
                    if (!mReadBuffer.readTags(e.tags, wayTags, numberOfTags))
                        return false;

                    numTags = numberOfTags;

                    mReadBuffer.setBufferPosition(pos);
                }
            } else {
                int wayDataSize = mReadBuffer.readUnsignedInt();
                if (wayDataSize < 0) {
                    log.warn("invalid way data size: " + wayDataSize);
                    if (mDebugFile) {
                        log.warn(DEBUG_SIGNATURE_BLOCK + mSignatureBlock);
                    }
                    log.error("BUG way 2");
                    return false;
                }

                /* ignore the way tile bitmask (2 bytes) */
                mReadBuffer.skipBytes(2);
            }

            /* get the special byte which encodes multiple flags */
            byte specialByte = mReadBuffer.readByte();

            /* bit 1-4 represent the layer */
            byte layer = (byte) ((specialByte & WAY_LAYER_BITMASK) >>> WAY_LAYER_SHIFT);
            /* bit 5-8 represent the number of tag IDs */
            byte numberOfTags = (byte) (specialByte & WAY_NUMBER_OF_TAGS_BITMASK);

            if (numberOfTags != 0) {

                if (!mReadBuffer.readTags(e.tags, wayTags, numberOfTags))
                    return false;

                numTags = numberOfTags;
            }

            /* get the feature bitmask (1 byte) */
            byte featureByte = mReadBuffer.readByte();

            /* bit 1-6 enable optional features */
            boolean featureWayDoubleDeltaEncoding =
                    (featureByte & WAY_FEATURE_DOUBLE_DELTA_ENCODING) != 0;

            boolean hasName = (featureByte & WAY_FEATURE_NAME) != 0;
            boolean hasHouseNr = (featureByte & WAY_FEATURE_HOUSE_NUMBER) != 0;
            boolean hasRef = (featureByte & WAY_FEATURE_REF) != 0;

            e.tags.numTags = numTags;

            if (mTileSource.experimental) {
                if (hasName) {
                    int textPos = mReadBuffer.readUnsignedInt();
                    String str = mTileSource.extractLocalized(mReadBuffer.readUTF8EncodedStringAt(stringOffset + textPos));
                    e.tags.add(new Tag(Tag.KEY_NAME, str, false));
                }
                if (hasHouseNr) {
                    int textPos = mReadBuffer.readUnsignedInt();
                    String str = mReadBuffer.readUTF8EncodedStringAt(stringOffset + textPos);
                    e.tags.add(new Tag(Tag.KEY_HOUSE_NUMBER, str, false));
                }
                if (hasRef) {
                    int textPos = mReadBuffer.readUnsignedInt();
                    String str = mReadBuffer.readUTF8EncodedStringAt(stringOffset + textPos);
                    e.tags.add(new Tag(Tag.KEY_REF, str, false));
                }
            } else {
                if (hasName) {
                    String str = mTileSource.extractLocalized(mReadBuffer.readUTF8EncodedString());
                    e.tags.add(new Tag(Tag.KEY_NAME, str, false));
                }
                if (hasHouseNr) {
                    String str = mReadBuffer.readUTF8EncodedString();
                    e.tags.add(new Tag(Tag.KEY_HOUSE_NUMBER, str, false));
                }
                if (hasRef) {
                    String str = mReadBuffer.readUTF8EncodedString();
                    e.tags.add(new Tag(Tag.KEY_REF, str, false));
                }
            }

            int[] labelPosition = null;
            if ((featureByte & WAY_FEATURE_LABEL_POSITION) != 0) {
                labelPosition = readOptionalLabelPosition();
            }

            if ((featureByte & WAY_FEATURE_DATA_BLOCKS_BYTE) != 0) {
                wayDataBlocks = mReadBuffer.readUnsignedInt();

                if (wayDataBlocks < 1) {
                    log.warn("invalid number of way data blocks: " + wayDataBlocks);
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

                if (!processWayDataBlock(e, featureWayDoubleDeltaEncoding, linearFeature))
                    return false;

                /* drop invalid outer ring */
                if (e.isPoly() && e.index[0] < 6) {
                   continue;
                }

                if (labelPosition != null && wayDataBlock == 0)
                    e.setLabelPosition(e.points[0] + labelPosition[0], e.points[1] + labelPosition[1]);
                mTileProjection.project(e);

                if (!e.tags.containsKey("building"))
                    if (!mTileClipper.clip(e)) {
                        continue;
                    }

                e.simplify(0, true);

                e.setLayer(layer);

                mapDataSink.process(e);
            }
        }

        return true;
    }

    private int[] readOptionalLabelPosition() {
        int[] labelPosition = new int[2];

        /* get the label position latitude offset (VBE-S) */
        labelPosition[1] = mReadBuffer.readSignedInt();

        /* get the label position longitude offset (VBE-S) */
        labelPosition[0] = mReadBuffer.readSignedInt();

        return labelPosition;
    }

    private int[][] readZoomTable(SubFileParameter subFileParameter) {
        int rows = subFileParameter.zoomLevelMax - subFileParameter.zoomLevelMin + 1;
        int[][] zoomTable = new int[rows][2];

        int cumulatedNumberOfPois = 0;
        int cumulatedNumberOfWays = 0;

        for (int row = 0; row < rows; row++) {
            cumulatedNumberOfPois += mReadBuffer.readUnsignedInt();
            cumulatedNumberOfWays += mReadBuffer.readUnsignedInt();

            if (cumulatedNumberOfPois < 0
                    || cumulatedNumberOfPois > MAXIMUM_ZOOM_TABLE_OBJECTS) {
                log.warn("invalid cumulated number of POIs in row " + row + ' '
                        + cumulatedNumberOfPois);
                if (mDebugFile) {
                    log.warn(DEBUG_SIGNATURE_BLOCK + mSignatureBlock);
                }
                return null;
            } else if (cumulatedNumberOfWays < 0
                    || cumulatedNumberOfWays > MAXIMUM_ZOOM_TABLE_OBJECTS) {
                log.warn("invalid cumulated number of ways in row " + row + ' '
                        + cumulatedNumberOfWays);
                if (mTileSource.fileInfo.debugFile) {
                    log.warn(DEBUG_SIGNATURE_BLOCK + mSignatureBlock);
                }
                return null;
            }

            zoomTable[row][0] = cumulatedNumberOfPois;
            zoomTable[row][1] = cumulatedNumberOfWays;
        }

        return zoomTable;
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

            /* scale latidute to map-pixel */
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

            double[] coords = e.points;
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
                    pLon = lon;
                    pLat = lat;
                    coords[outPos++] = (int)pLon;
                    coords[outPos++] = (int)pLat;
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
        }
    }
}
