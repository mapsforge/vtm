/*
 * Copyright 2013 Hannes Janetzek
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
package org.oscim.tiling.source.oscimap;

import org.oscim.core.GeometryBuffer.GeometryType;
import org.oscim.core.MapElement;
import org.oscim.core.Tag;
import org.oscim.core.Tile;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.source.PbfDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class TileDecoder extends PbfDecoder {
    static final Logger log = LoggerFactory.getLogger(TileDecoder.class);

    private final static float REF_TILE_SIZE = 4096.0f;

    private static final int TAG_TILE_TAGS = 1;
    private static final int TAG_TILE_WAYS = 2;
    private static final int TAG_TILE_POLY = 3;
    private static final int TAG_TILE_NODES = 4;
    private static final int TAG_WAY_TAGS = 11;
    private static final int TAG_WAY_INDEX = 12;
    private static final int TAG_WAY_COORDS = 13;
    private static final int TAG_WAY_LAYER = 21;
    private static final int TAG_WAY_NUM_TAGS = 1;
    private static final int TAG_WAY_NUM_INDICES = 2;
    private static final int TAG_WAY_NUM_COORDS = 3;

    private static final int TAG_NODE_TAGS = 11;
    private static final int TAG_NODE_COORDS = 12;
    private static final int TAG_NODE_LAYER = 21;
    private static final int TAG_NODE_NUM_TAGS = 1;
    private static final int TAG_NODE_NUM_COORDS = 2;

    private int MAX_TILE_TAGS = 100;
    private Tag[] curTags = new Tag[MAX_TILE_TAGS];
    private int mCurTagCnt;

    private ITileDataSink mSink;
    private float mScale;
    private Tile mTile;
    private final MapElement mElem;

    TileDecoder() {
        mElem = new MapElement();
    }

    @Override
    public boolean decode(Tile tile, ITileDataSink sink, InputStream is)
            throws IOException {

        setInputStream(is);

        mTile = tile;
        mSink = sink;
        mScale = REF_TILE_SIZE / Tile.SIZE;
        return decode();
    }

    private static final int MAX_TAGS_CACHE = 100;
    private static Map<String, Tag> tagHash =
            Collections.synchronizedMap(new LinkedHashMap<String, Tag>(MAX_TAGS_CACHE,
                    0.75f,
                    true) {

                private static final long serialVersionUID = 1L;

                //@Override
                //protected boolean removeEldestEntry(Entry<String, Tag> e) {
                //if (size() < MAX_TAGS_CACHE)
                //return false;
                //return true;
                //}
            });

    private boolean decode() throws IOException {
        int val;
        mCurTagCnt = 0;

        while (hasData() && (val = decodeVarint32()) > 0) {
            // read tag and wire type
            int tag = (val >> 3);

            switch (tag) {
                case TAG_TILE_TAGS:
                    decodeTileTags();
                    break;

                case TAG_TILE_WAYS:
                    decodeTileWays(false);
                    break;

                case TAG_TILE_POLY:
                    decodeTileWays(true);
                    break;

                case TAG_TILE_NODES:
                    decodeTileNodes();
                    break;

                default:
                    log.debug("invalid type for tile: " + tag);
                    return false;
            }
        }
        return true;
    }

    private boolean decodeTileTags() throws IOException {
        String tagString = decodeString();

        if (tagString == null || tagString.length() == 0) {
            curTags[mCurTagCnt++] = new Tag(Tag.KEY_NAME, "...");
            return false;
        }

        Tag tag = tagHash.get(tagString);

        if (tag == null) {
            if (tagString.startsWith(Tag.KEY_NAME))
                tag = new Tag(Tag.KEY_NAME, tagString.substring(5), false);
            else
                tag = Tag.parse(tagString);

            if (tag != null)
                tagHash.put(tagString, tag);
        }

        if (mCurTagCnt >= MAX_TILE_TAGS) {
            MAX_TILE_TAGS = mCurTagCnt + 10;
            Tag[] tmp = new Tag[MAX_TILE_TAGS];
            System.arraycopy(curTags, 0, tmp, 0, mCurTagCnt);
            curTags = tmp;
        }
        curTags[mCurTagCnt++] = tag;

        return true;
    }

    private boolean decodeTileWays(boolean polygon) throws IOException {
        int bytes = decodeVarint32();

        int end = position() + bytes;
        int indexCnt = 0;
        int tagCnt = 0;
        int coordCnt = 0;
        int layer = 5;

        boolean fail = false;

        while (position() < end) {
            // read tag and wire type
            int val = decodeVarint32();
            if (val == 0)
                break;

            int tag = (val >> 3);

            switch (tag) {
                case TAG_WAY_TAGS:
                    if (!decodeWayTags(tagCnt))
                        return false;
                    break;

                case TAG_WAY_INDEX:
                    decodeWayIndices(indexCnt);
                    break;

                case TAG_WAY_COORDS:
                    if (coordCnt == 0) {
                        log.debug(mTile + " no coordinates");
                    }

                    mElem.ensurePointSize(coordCnt, false);
                    int cnt = decodeInterleavedPoints(mElem.points, mScale);

                    if (cnt != coordCnt) {
                        log.debug(mTile + " wrong number of coordintes "
                                + coordCnt + "/" + cnt);
                        fail = true;
                    }

                    break;

                case TAG_WAY_LAYER:
                    layer = decodeVarint32();
                    break;

                case TAG_WAY_NUM_TAGS:
                    tagCnt = decodeVarint32();
                    break;

                case TAG_WAY_NUM_INDICES:
                    indexCnt = decodeVarint32();
                    break;

                case TAG_WAY_NUM_COORDS:
                    coordCnt = decodeVarint32();
                    break;

                default:
                    log.debug("X invalid type for way: " + tag);
            }
        }

        if (fail || indexCnt == 0 || tagCnt == 0) {
            log.debug("failed reading way: bytes:" + bytes + " index:"
                    //+ (tags != null ? tags.toString() : "...") + " "
                    + indexCnt + " " + coordCnt + " " + tagCnt);
            return false;
        }

        // FIXME, remove all tiles from cache then remove this below
        //if (layer == 0)
        //    layer = 5;
        mElem.type = polygon ? GeometryType.POLY : GeometryType.LINE;
        mElem.setLayer(layer);
        mSink.process(mElem);
        return true;
    }

    private boolean decodeTileNodes() throws IOException {
        int bytes = decodeVarint32();

        int end = position() + bytes;
        int tagCnt = 0;
        int coordCnt = 0;
        byte layer = 0;

        while (position() < end) {
            // read tag and wire type
            int val = decodeVarint32();
            if (val == 0)
                break;

            int tag = (val >> 3);

            switch (tag) {
                case TAG_NODE_TAGS:
                    if (!decodeWayTags(tagCnt))
                        return false;
                    break;

                case TAG_NODE_COORDS:
                    int cnt = decodeNodeCoordinates(coordCnt, layer);
                    if (cnt != coordCnt) {
                        log.debug("X wrong number of coordintes");
                        return false;
                    }
                    break;

                case TAG_NODE_LAYER:
                    layer = (byte) decodeVarint32();
                    break;

                case TAG_NODE_NUM_TAGS:
                    tagCnt = decodeVarint32();
                    break;

                case TAG_NODE_NUM_COORDS:
                    coordCnt = decodeVarint32();
                    break;

                default:
                    log.debug("X invalid type for node: " + tag);
            }
        }

        return true;
    }

    private int decodeNodeCoordinates(int numNodes, byte layer)
            throws IOException {
        int bytes = decodeVarint32();

        fillBuffer(bytes);
        int cnt = 0;
        int end = position() + bytes;
        // read repeated sint32
        int lastX = 0;
        int lastY = 0;
        double[] coords = mElem.ensurePointSize(numNodes, false);

        while (position() < end && cnt < numNodes) {
            int lon = deZigZag(decodeVarint32());
            int lat = deZigZag(decodeVarint32());
            lastX = lon + lastX;
            lastY = lat + lastY;
            coords[cnt++] = lastX / mScale;
            coords[cnt++] = Tile.SIZE - lastY / mScale;
        }

        mElem.index[0] = (short) numNodes;
        mElem.type = GeometryType.POINT;
        mElem.setLayer(layer);
        mSink.process(mElem);

        return cnt;
    }

    private boolean decodeWayTags(int tagCnt) throws IOException {
        int bytes = decodeVarint32();

        mElem.tags.clear();

        int cnt = 0;
        int end = position() + bytes;
        int max = mCurTagCnt;

        for (; position() < end; cnt++) {
            int tagNum = decodeVarint32();

            if (tagNum < 0 || cnt == tagCnt) {
                log.debug("NULL TAG: " + mTile
                        + " invalid tag:" + tagNum
                        + " " + tagCnt + "/" + cnt);
                continue;
            }

            if (tagNum < Tags.MAX) {
                mElem.tags.add(Tags.tags[tagNum]);
                continue;
            }

            tagNum -= Tags.LIMIT;

            if (tagNum >= 0 && tagNum < max) {
                mElem.tags.add(curTags[tagNum]);
            } else {
                log.debug("NULL TAG: " + mTile
                        + " could find tag:"
                        + tagNum + " " + tagCnt
                        + "/" + cnt);
            }
        }

        if (tagCnt != cnt) {
            log.debug("NULL TAG: " + mTile);
            return false;
        }

        return true;
    }

    private int decodeWayIndices(int indexCnt) throws IOException {
        mElem.ensureIndexSize(indexCnt, false);

        decodeVarintArray(indexCnt, mElem.index);

        int[] index = mElem.index;
        int coordCnt = 0;

        for (int i = 0; i < indexCnt; i++) {
            coordCnt += index[i];
            index[i] *= 2;
        }

        // set end marker
        if (indexCnt < index.length)
            index[indexCnt] = -1;

        return coordCnt;
    }

    //@Override
    protected int decodeInterleavedPoints(double[] coords, float scale)
            throws IOException {

        int bytes = decodeVarint32();
        fillBuffer(bytes);

        int cnt = 0;
        int lastX = 0;
        int lastY = 0;
        boolean even = true;

        byte[] buf = buffer;
        int pos = bufferPos;
        int end = pos + bytes;
        int val;

        while (pos < end) {
            if (buf[pos] >= 0) {
                val = buf[pos++];

            } else if (buf[pos + 1] >= 0) {
                val = (buf[pos++] & 0x7f)
                        | buf[pos++] << 7;

            } else if (buf[pos + 2] >= 0) {
                val = (buf[pos++] & 0x7f)
                        | (buf[pos++] & 0x7f) << 7
                        | (buf[pos++]) << 14;

            } else if (buf[pos + 3] >= 0) {
                val = (buf[pos++] & 0x7f)
                        | (buf[pos++] & 0x7f) << 7
                        | (buf[pos++] & 0x7f) << 14
                        | (buf[pos++]) << 21;

            } else {
                val = (buf[pos++] & 0x7f)
                        | (buf[pos++] & 0x7f) << 7
                        | (buf[pos++] & 0x7f) << 14
                        | (buf[pos++] & 0x7f) << 21
                        | (buf[pos]) << 28;

                if (buf[pos++] < 0)
                    throw INVALID_VARINT;
            }

            // zigzag decoding
            int s = ((val >>> 1) ^ -(val & 1));

            if (even) {
                lastX = lastX + s;
                coords[cnt++] = lastX / scale;
                even = false;
            } else {
                lastY = lastY + s;
                coords[cnt++] = Tile.SIZE - lastY / scale;
                even = true;
            }
        }

        if (pos != bufferPos + bytes)
            throw INVALID_PACKED_SIZE;

        bufferPos = pos;

        // return number of points read
        return cnt;
    }

}
