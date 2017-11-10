/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2017 devemux86
 * Copyright 2017 Gustl22
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
package org.oscim.tiling.source.oscimap4;

import org.oscim.core.GeometryBuffer.GeometryType;
import org.oscim.core.MapElement;
import org.oscim.core.Tag;
import org.oscim.core.TagSet;
import org.oscim.core.Tile;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.source.PbfDecoder;
import org.oscim.utils.math.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class TileDecoder extends PbfDecoder {
    static final Logger log = LoggerFactory.getLogger(TileDecoder.class);

    private static final int TAG_TILE_VERSION = 1;
    //private static final int TAG_TILE_TIMESTAMP = 2;
    //private static final int TAG_TILE_ISWATER = 3;

    private static final int TAG_TILE_NUM_TAGS = 11;
    private static final int TAG_TILE_NUM_KEYS = 12;
    private static final int TAG_TILE_NUM_VALUES = 13;

    private static final int TAG_TILE_TAG_KEYS = 14;
    private static final int TAG_TILE_TAG_VALUES = 15;
    private static final int TAG_TILE_TAGS = 16;

    private static final int TAG_TILE_LINE = 21;
    private static final int TAG_TILE_POLY = 22;
    private static final int TAG_TILE_POINT = 23;
    /**
     * since version 5
     */
    private static final int TAG_TILE_MESH = 24;

    private static final int TAG_ELEM_NUM_INDICES = 1;
    private static final int TAG_ELEM_NUM_TAGS = 2;
    /**
     * since version 5
     */
    private static final int TAG_ELEM_NUM_COORDINATES = 3;
    private static final int TAG_ELEM_TAGS = 11;
    private static final int TAG_ELEM_INDEX = 12;
    private static final int TAG_ELEM_COORDS = 13;
    private static final int TAG_ELEM_LAYER = 21;

    private int[] mSArray = new int[100];

    private Tile mTile;

    private final MapElement mElem;

    private final TagSet mTileTags;
    private ITileDataSink mMapDataSink;

    private int mVersion;

    // scale coordinates to tile size
    private final static float REF_TILE_SIZE = 4096.0f;
    private final float mScaleFactor = REF_TILE_SIZE / Tile.SIZE;

    public TileDecoder() {
        mElem = new MapElement();
        mTileTags = new TagSet(100);
    }

    @Override
    public boolean decode(Tile tile, ITileDataSink sink, InputStream is)
            throws IOException {

        readUnsignedInt(is, buffer);
        setInputStream(is);

        mTile = tile;
        mMapDataSink = sink;

        mTileTags.clearAndNullTags();

        int val;
        int numTags = 0;
        int numKeys = -1;
        int numValues = -1;

        int curKey = 0;
        int curValue = 0;

        String[] keys = null;
        String[] values = null;

        while (hasData() && (val = decodeVarint32()) > 0) {
            // read tag and wire type
            int tag = (val >> 3);
            //log.debug("tag: " + tag);

            switch (tag) {
                case TAG_TILE_LINE:
                case TAG_TILE_POLY:
                case TAG_TILE_POINT:
                case TAG_TILE_MESH:
                    decodeTileElement(tag);
                    break;

                case TAG_TILE_TAG_KEYS:
                    if (keys == null || curKey >= numKeys) {
                        log.debug("{} wrong number of keys {}",
                                mTile, numKeys);
                        return false;
                    }
                    keys[curKey++] = decodeString().intern();
                    break;

                case TAG_TILE_TAG_VALUES:
                    if (values == null || curValue >= numValues) {
                        log.debug("{} wrong number of values {}",
                                mTile, numValues);
                        return false;
                    }
                    values[curValue++] = decodeString();
                    break;

                case TAG_TILE_NUM_TAGS:
                    numTags = decodeVarint32();
                    //log.debug("num tags " + numTags);
                    break;

                case TAG_TILE_NUM_KEYS:
                    numKeys = decodeVarint32();
                    //log.debug("num keys " + numKeys);
                    keys = new String[numKeys];
                    break;

                case TAG_TILE_NUM_VALUES:
                    numValues = decodeVarint32();
                    //log.debug("num values " + numValues);
                    values = new String[numValues];
                    break;

                case TAG_TILE_TAGS:
                    int len = numTags * 2;
                    if (mSArray.length < len)
                        mSArray = new int[len];

                    decodeVarintArray(len, mSArray);
                    if (!decodeTileTags(numTags, mSArray, keys, values)) {
                        log.debug("{} invalid tags", mTile);
                        return false;
                    }
                    break;

                case TAG_TILE_VERSION:
                    int version = decodeVarint32();
                    if (version < 4 || mVersion > 5) {
                        log.debug("{} invalid version:{}",
                                mTile, version);
                        return false;
                    }
                    break;

                default:
                    log.debug("{} invalid type for tile:{}",
                            mTile, tag);
                    return false;
            }
        }

        return true;
    }

    private boolean decodeTileTags(int numTags, int[] tagIdx,
                                   String[] keys, String[] vals) {

        Tag tag;
        for (int i = 0, n = (numTags << 1); i < n; i += 2) {
            int k = tagIdx[i];
            int v = tagIdx[i + 1];
            String key, val;

            if (k < Tags.ATTRIB_OFFSET) {
                if (k > Tags.MAX_KEY)
                    return false;
                key = Tags.keys[k];
            } else {
                k -= Tags.ATTRIB_OFFSET;
                if (k >= keys.length)
                    return false;
                key = keys[k];
            }

            if (v < Tags.ATTRIB_OFFSET) {
                if (v > Tags.MAX_VALUE)
                    return false;
                val = Tags.values[v];
            } else {
                v -= Tags.ATTRIB_OFFSET;
                if (v >= vals.length)
                    return false;
                val = vals[v];
            }

            // FIXME filter out all variable tags
            // might depend on theme though
            if (Tag.KEY_NAME.equals(key)
                    || Tag.KEY_HOUSE_NUMBER.equals(key)
                    || Tag.KEY_REF.equals(key)
                    || Tag.KEY_ELE.equals(key))
                tag = new Tag(key, val, false);
            else if (Tag.KEY_HEIGHT.equals(key)
                    || Tag.KEY_MIN_HEIGHT.equals(key)) {
                // Reformat values to established meters in OSM
                tag = new Tag(key, String.valueOf(MathUtils.round2(Float.valueOf(val) / 100)), false);
            } else
                tag = new Tag(key, val, false, true);

            mTileTags.add(tag);
        }

        return true;
    }

    private int decodeWayIndices(int indexCnt, boolean shift) throws IOException {
        mElem.ensureIndexSize(indexCnt, false);
        decodeVarintArray(indexCnt, mElem.index);

        int[] index = mElem.index;
        int coordCnt = 0;

        if (shift) {
            for (int i = 0; i < indexCnt; i++) {
                coordCnt += index[i];
                index[i] *= 2;
            }
        }
        // set end marker
        if (indexCnt < index.length)
            index[indexCnt] = -1;

        return coordCnt;
    }

    private boolean decodeTileElement(int type) throws IOException {

        int bytes = decodeVarint32();

        int end = position() + bytes;
        int numIndices = 1;
        int numTags = 1;

        //boolean skip = false;
        boolean fail = false;

        int coordCnt = 0;
        if (type == TAG_TILE_POINT) {
            coordCnt = 1;
            mElem.index[0] = 2;
        }

        mElem.layer = 5;

        while (position() < end) {
            // read tag and wire type
            int val = decodeVarint32();
            if (val == 0)
                break;

            int tag = (val >> 3);

            switch (tag) {
                case TAG_ELEM_TAGS:
                    if (!decodeElementTags(numTags))
                        return false;
                    break;

                case TAG_ELEM_NUM_INDICES:
                    numIndices = decodeVarint32();
                    break;

                case TAG_ELEM_NUM_TAGS:
                    numTags = decodeVarint32();
                    break;

                case TAG_ELEM_NUM_COORDINATES:
                    coordCnt = decodeVarint32();
                    break;

                case TAG_ELEM_INDEX:
                    if (type == TAG_TILE_MESH) {
                        decodeWayIndices(numIndices, false);
                    } else {
                        coordCnt = decodeWayIndices(numIndices, true);
                        // otherwise using TAG_ELEM_NUM_COORDINATES
                    }
                    break;

                case TAG_ELEM_COORDS:
                    if (coordCnt == 0) {
                        log.debug("{} no coordinates", mTile);
                    }

                    if (type == TAG_TILE_MESH) {
                        mElem.ensurePointSize((coordCnt * 3 / 2), false);
                        int cnt = decodeInterleavedPoints3D(mElem.points, 1);

                        if (cnt != (3 * coordCnt)) {
                            log.debug("{} wrong number of coordintes {}/{}", mTile,
                                    Integer.valueOf(coordCnt),
                                    Integer.valueOf(cnt));
                            fail = true;
                        }
                        mElem.pointPos = cnt;
                    } else {
                        mElem.ensurePointSize(coordCnt, false);
                        int cnt = decodeInterleavedPoints(mElem, mScaleFactor);

                        if (cnt != coordCnt) {
                            log.debug("{} wrong number of coordintes {}/{}", mTile,
                                    Integer.valueOf(coordCnt),
                                    Integer.valueOf(cnt));
                            fail = true;
                        }
                    }
                    break;

                case TAG_ELEM_LAYER:
                    mElem.layer = decodeVarint32();
                    break;

                default:
                    log.debug("{} invalid type for way: {}", mTile, tag);
            }
        }

        if (fail || numTags == 0 || numIndices == 0) {
            log.debug("{} failed: bytes:{} tags:{} ({},{})",
                    mTile, Integer.valueOf(bytes),
                    mElem.tags,
                    Integer.valueOf(numIndices),
                    Integer.valueOf(coordCnt));
            return false;
        }

        switch (type) {
            case TAG_TILE_LINE:
                mElem.type = GeometryType.LINE;
                break;
            case TAG_TILE_POLY:
                mElem.type = GeometryType.POLY;
                break;
            case TAG_TILE_POINT:
                mElem.type = GeometryType.POINT;
                break;
            case TAG_TILE_MESH:
                mElem.type = GeometryType.TRIS;
                break;
        }

        mMapDataSink.process(mElem);

        return true;
    }

    private boolean decodeElementTags(int numTags) throws IOException {
        if (mSArray.length < numTags)
            mSArray = new int[numTags];
        int[] tagIds = mSArray;

        decodeVarintArray(numTags, tagIds);

        mElem.tags.clear();

        int max = mTileTags.size() - 1;

        for (int i = 0; i < numTags; i++) {
            int idx = tagIds[i];

            if (idx < 0 || idx > max) {
                log.debug("{} invalid tag:{}", mTile,
                        Integer.valueOf(idx),
                        Integer.valueOf(i));
                return false;
            }
            mElem.tags.add(mTileTags.get(idx));
        }

        return true;
    }
}
