/*
 * Copyright 2012-2014 Hannes Janetzek
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
package org.oscim.renderer.bucket;

import org.oscim.backend.GL;
import org.oscim.core.Tile;
import org.oscim.layers.tile.MapTile.TileData;
import org.oscim.renderer.BufferObject;
import org.oscim.renderer.MapRenderer;
import org.oscim.theme.styles.AreaStyle;
import org.oscim.theme.styles.CircleStyle;
import org.oscim.theme.styles.LineStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ShortBuffer;

import static org.oscim.renderer.bucket.RenderBucket.CIRCLE;
import static org.oscim.renderer.bucket.RenderBucket.HAIRLINE;
import static org.oscim.renderer.bucket.RenderBucket.LINE;
import static org.oscim.renderer.bucket.RenderBucket.MESH;
import static org.oscim.renderer.bucket.RenderBucket.POLYGON;
import static org.oscim.renderer.bucket.RenderBucket.TEXLINE;

/**
 * This class is primarily intended for rendering the vector elements of a
 * MapTile. It can be used for other purposes as well but some optimizations
 * (and limitations) probably wont make sense in different contexts.
 */
public class RenderBuckets extends TileData {

    static final Logger log = LoggerFactory.getLogger(RenderBuckets.class);

    public final static int[] VERTEX_SHORT_CNT = {
            4, // LINE_VERTEX
            6, // TEXLINE_VERTEX
            2, // POLY_VERTEX
            2, // MESH_VERTEX
            4, // EXTRUSION_VERTEX
            2, // HAIRLINE_VERTEX
            6, // SYMBOL
            6, // BITMAP
            2, // CIRCLE
    };

    private final static int SHORT_BYTES = 2;

    private RenderBucket buckets;

    /**
     * VBO holds all vertex data to draw lines and polygons after compilation.
     * Layout:
     * 16 bytes fill coordinates,
     * n bytes polygon vertices,
     * m bytes lines vertices
     * ...
     */
    public BufferObject vbo;
    public BufferObject ibo;

    /**
     * To not need to switch VertexAttribPointer positions all the time:
     * 1. polygons are packed in VBO at offset 0
     * 2. lines afterwards at lineOffset
     * 3. other buckets keep their byte offset in offset
     */
    public int[] offset = {0, 0};

    private RenderBucket mCurBucket;

    /**
     * add the LineBucket for a level with a given Line style. Levels are
     * ordered from bottom (0) to top
     */
    public LineBucket addLineBucket(int level, LineStyle style) {
        LineBucket l = (LineBucket) getBucket(level, LINE);
        if (l == null)
            return null;
        // FIXME l.scale = style.width;
        l.scale = 1;
        l.line = style;
        return l;
    }

    public PolygonBucket addPolygonBucket(int level, AreaStyle style) {
        PolygonBucket l = (PolygonBucket) getBucket(level, POLYGON);
        if (l == null)
            return null;
        l.area = style;
        return l;
    }

    public MeshBucket addMeshBucket(int level, AreaStyle style) {
        MeshBucket l = (MeshBucket) getBucket(level, MESH);
        if (l == null)
            return null;
        l.area = style;
        return l;
    }

    public HairLineBucket addHairLineBucket(int level, LineStyle style) {
        HairLineBucket ll = getHairLineBucket(level);
        if (ll == null)
            return null;
        ll.line = style;

        return ll;
    }

    public CircleBucket addCircleBucket(int level, CircleStyle style) {
        CircleBucket l = (CircleBucket) getBucket(level, CIRCLE);
        if (l == null)
            return null;
        l.circle = style;
        return l;
    }

    /**
     * Get or add the LineBucket for a level. Levels are ordered from
     * bottom (0) to top
     */
    public LineBucket getLineBucket(int level) {
        return (LineBucket) getBucket(level, LINE);
    }

    /**
     * Get or add the MeshBucket for a level. Levels are ordered from
     * bottom (0) to top
     */
    public MeshBucket getMeshBucket(int level) {
        return (MeshBucket) getBucket(level, MESH);
    }

    /**
     * Get or add the PolygonBucket for a level. Levels are ordered from
     * bottom (0) to top
     */
    public PolygonBucket getPolygonBucket(int level) {
        return (PolygonBucket) getBucket(level, POLYGON);
    }

    /**
     * Get or add the TexLineBucket for a level. Levels are ordered from
     * bottom (0) to top
     */
    public LineTexBucket getLineTexBucket(int level) {
        return (LineTexBucket) getBucket(level, TEXLINE);
    }

    /**
     * Get or add the TexLineBucket for a level. Levels are ordered from
     * bottom (0) to top
     */
    public HairLineBucket getHairLineBucket(int level) {
        return (HairLineBucket) getBucket(level, HAIRLINE);
    }

    /**
     * Get or add the CircleBucket for a level. Levels are ordered from
     * bottom (0) to top
     */
    public CircleBucket getCircleBucket(int level) {
        return (CircleBucket) getBucket(level, CIRCLE);
    }

    /**
     * Set new bucket items and clear previous.
     */
    public void set(RenderBucket buckets) {
        for (RenderBucket l = this.buckets; l != null; l = l.next)
            l.clear();

        this.buckets = buckets;
    }

    /**
     * @return internal linked list of RenderBucket items
     */
    public RenderBucket get() {
        return buckets;
    }

    private RenderBucket getBucket(int level, int type) {
        RenderBucket bucket = null;

        if (mCurBucket != null && mCurBucket.level == level) {
            bucket = mCurBucket;
            if (bucket.type != type) {
                log.error("BUG wrong bucket {} {} on level {}",
                        Integer.valueOf(bucket.type),
                        Integer.valueOf(type),
                        Integer.valueOf(level));

                throw new IllegalArgumentException();
            }
            return bucket;
        }

        RenderBucket b = buckets;
        if (b == null || b.level > level) {
            /* insert new bucket at start */
            b = null;
        } else {
            if (mCurBucket != null && level > mCurBucket.level)
                b = mCurBucket;

            while (true) {
                /* found bucket */
                if (b.level == level) {
                    bucket = b;
                    break;
                }
                /* insert bucket between current and next bucket */
                if (b.next == null || b.next.level > level)
                    break;

                b = b.next;
            }
        }

        if (bucket == null) {
            /* add a new RenderElement */
            if (type == LINE)
                bucket = new LineBucket(level);
            else if (type == POLYGON)
                bucket = new PolygonBucket(level);
            else if (type == TEXLINE)
                bucket = new LineTexBucket(level);
            else if (type == MESH)
                bucket = new MeshBucket(level);
            else if (type == HAIRLINE)
                bucket = new HairLineBucket(level);
            else if (type == CIRCLE)
                bucket = new CircleBucket(level);

            if (bucket == null)
                throw new IllegalArgumentException();

            if (b == null) {
                /** insert at start */
                bucket.next = buckets;
                buckets = bucket;
            } else {
                bucket.next = b.next;
                b.next = bucket;
            }
        }

        /* check if found buckets matches requested type */
        if (bucket.type != type) {
            log.error("BUG wrong bucket {} {} on level {}",
                    Integer.valueOf(bucket.type),
                    Integer.valueOf(type),
                    Integer.valueOf(level));

            throw new IllegalArgumentException();
        }

        mCurBucket = bucket;

        return bucket;
    }

    private int countVboSize() {
        int vboShorts = 0;

        for (RenderBucket l = buckets; l != null; l = l.next)
            vboShorts += l.numVertices * VERTEX_SHORT_CNT[l.type];

        return vboShorts;
    }

    private int countIboSize() {
        int numIndices = 0;

        for (RenderBucket l = buckets; l != null; l = l.next)
            numIndices += l.numIndices;

        return numIndices;
    }

    public void setFrom(RenderBuckets buckets) {
        if (buckets == this)
            throw new IllegalArgumentException("Cannot set from oneself!");

        set(buckets.buckets);

        mCurBucket = null;
        buckets.buckets = null;
        buckets.mCurBucket = null;
    }

    /**
     * cleanup only when buckets are not used by tile or bucket anymore!
     */
    public void clear() {
        /* NB: set null calls clear() on each bucket! */
        set(null);
        mCurBucket = null;

        vbo = BufferObject.release(vbo);
        ibo = BufferObject.release(ibo);
    }

    /**
     * cleanup only when buckets are not used by tile or bucket anymore!
     */
    public void clearBuckets() {
        /* NB: set null calls clear() on each bucket! */
        for (RenderBucket l = buckets; l != null; l = l.next)
            l.clear();

        mCurBucket = null;
    }

    @Override
    protected void dispose() {
        clear();
    }

    public void prepare() {
        for (RenderBucket l = buckets; l != null; l = l.next)
            l.prepare();
    }

    public void bind() {
        if (vbo != null)
            vbo.bind();

        if (ibo != null)
            ibo.bind();

    }

    public boolean compile(boolean addFill) {

        int vboSize = countVboSize();

        if (vboSize <= 0) {
            vbo = BufferObject.release(vbo);
            ibo = BufferObject.release(ibo);
            return false;
        }

        if (addFill)
            vboSize += 8;

        ShortBuffer vboData = MapRenderer.getShortBuffer(vboSize);

        if (addFill)
            vboData.put(fillCoords, 0, 8);

        ShortBuffer iboData = null;

        int iboSize = countIboSize();
        if (iboSize > 0) {
            iboData = MapRenderer.getShortBuffer(iboSize);
        }

        int pos = addFill ? 4 : 0;

        for (RenderBucket l = buckets; l != null; l = l.next) {
            if (l.type == POLYGON) {
                l.compile(vboData, iboData);
                l.vertexOffset = pos;
                pos += l.numVertices;
            }
        }

        for (RenderBucket l = buckets; l != null; l = l.next) {
            if (l.type == CIRCLE) {
                l.compile(vboData, iboData);
                l.vertexOffset = pos;
                pos += l.numVertices;
            }
        }

        offset[LINE] = vboData.position() * SHORT_BYTES;
        pos = 0;
        for (RenderBucket l = buckets; l != null; l = l.next) {
            if (l.type == LINE) {
                l.compile(vboData, iboData);

                l.vertexOffset = pos;
                pos += l.numVertices;
            }
        }

        for (RenderBucket l = buckets; l != null; l = l.next) {
            if (l.type != LINE && l.type != POLYGON) {
                l.compile(vboData, iboData);
            }
        }

        if (vboSize != vboData.position()) {
            log.debug("wrong vertex buffer size: "
                    + " new size: " + vboSize
                    + " buffer pos: " + vboData.position()
                    + " buffer limit: " + vboData.limit()
                    + " buffer fill: " + vboData.remaining());
            return false;
        }

        if (iboSize > 0 && iboSize != iboData.position()) {
            log.debug("wrong indice buffer size: "
                    + " new size: " + iboSize
                    + " buffer pos: " + iboData.position()
                    + " buffer limit: " + iboData.limit()
                    + " buffer fill: " + iboData.remaining());
            return false;
        }

        if (vbo == null)
            vbo = BufferObject.get(GL.ARRAY_BUFFER, vboSize);

        vbo.loadBufferData(vboData.flip(), vboSize * 2);

        if (iboSize > 0) {
            if (ibo == null)
                ibo = BufferObject.get(GL.ELEMENT_ARRAY_BUFFER, iboSize);

            ibo.loadBufferData(iboData.flip(), iboSize * 2);
        }

        return true;
    }

    private static short[] fillCoords;

    static {
        short s = (short) (Tile.SIZE * MapRenderer.COORD_SCALE);
        fillCoords = new short[]{0, s, s, s, 0, 0, s, 0};
    }

    public static void initRenderer() {
        LineBucket.Renderer.init();
        LineTexBucket.Renderer.init();
        PolygonBucket.Renderer.init();
        TextureBucket.Renderer.init();
        BitmapBucket.Renderer.init();
        MeshBucket.Renderer.init();
        HairLineBucket.Renderer.init();
        CircleBucket.Renderer.init();
    }
}
