/*
 * Copyright 2012, 2013 Hannes Janetzek
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
package org.oscim.renderer.bucket;

import org.oscim.backend.canvas.Color;
import org.oscim.core.GeometryBuffer;
import org.oscim.core.Tile;
import org.oscim.utils.ExtrusionUtils;
import org.oscim.utils.FastMath;
import org.oscim.utils.KeyMap;
import org.oscim.utils.KeyMap.HashItem;
import org.oscim.utils.Tessellator;
import org.oscim.utils.geom.LineClipper;
import org.oscim.utils.pool.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ShortBuffer;

import static org.oscim.renderer.MapRenderer.COORD_SCALE;

public class ExtrusionBucket extends RenderBucket {
    static final Logger log = LoggerFactory.getLogger(ExtrusionBucket.class);

    private VertexData mIndices[];
    private LineClipper mClipper;

    /**
     * 16 floats rgba for top, even-side, odd-sides and outline
     */
    private final float[] colors;
    private final int color;

    /**
     * indices for: 0. even sides, 1. odd sides, 2. roof, 3. roof outline
     */
    public int idx[] = {0, 0, 0, 0, 0};

    /**
     * indices offsets in bytes
     */
    public int off[] = {0, 0, 0, 0, 0};

    //private final static int IND_EVEN_SIDE = 0;
    //private final static int IND_ODD_SIDE = 1;
    private final static int IND_ROOF = 2;

    // FIXME flip OUTLINE / MESH!
    private final static int IND_OUTLINE = 3;
    private final static int IND_MESH = 4;

    private final float mGroundResolution;

    private KeyMap<Vertex> mVertexMap;

    //private static final int NORMAL_DIR_MASK = 0xFFFFFFFE;
    //private int numIndexHits = 0;

    /**
     * ExtrusionLayer for polygon geometries.
     */
    public ExtrusionBucket(int level, float groundResolution, float[] colors) {
        super(RenderBucket.EXTRUSION, true, false);
        this.level = level;
        this.colors = colors;
        this.color = 0;

        mGroundResolution = groundResolution;

        mIndices = new VertexData[5];

        for (int i = 0; i <= IND_MESH; i++)
            mIndices[i] = new VertexData();

        mClipper = new LineClipper(0, 0, Tile.SIZE, Tile.SIZE);
    }

    /**
     * ExtrusionLayer for triangle geometries / meshes.
     */
    public ExtrusionBucket(int level, float groundResolution, int color) {
        super(RenderBucket.EXTRUSION, true, false);
        this.level = level;
        this.color = color;

        float a = Color.aToFloat(color);
        colors = new float[4]; // Why not 16?
        colors[0] = a * Color.rToFloat(color);
        colors[1] = a * Color.gToFloat(color);
        colors[2] = a * Color.bToFloat(color);
        colors[3] = a;

        mGroundResolution = groundResolution;

        mIndices = new VertexData[5];
        mIndices[4] = new VertexData();

        synchronized (vertexPool) {
            mVertexMap = vertexMapPool.get();
        }
    }

    static Pool<Vertex> vertexPool = new Pool<Vertex>() {
        @Override
        protected Vertex createItem() {
            return new Vertex();
        }
    };

    static Pool<KeyMap<Vertex>> vertexMapPool = new Pool<KeyMap<Vertex>>() {
        @Override
        protected KeyMap<Vertex> createItem() {
            return new KeyMap<Vertex>(2048);
        }
    };

    static class Vertex extends HashItem {
        short x, y, z, n;
        int id;

        @Override
        public boolean equals(Object obj) {
            Vertex o = (Vertex) obj;
            return x == o.x && y == o.y && z == o.z && n == o.n;
        }

        @Override
        public int hashCode() {
            return 7 + ((x << 16 | y) ^ (n << 16 | z)) * 31;
        }

        public Vertex set(short x, short y, short z, short n) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.n = n;
            return this;
        }
    }

    /**
     * Add MapElement which provides meshes
     *
     * @param element the map element to add
     */
    public void addMesh(GeometryBuffer element) {
        if (!element.isTris())
            return;

        int[] index = element.index;
        float[] points = element.points;

        int vertexCnt = numVertices;
        synchronized (vertexPool) {

            Vertex key = vertexPool.get();
            double scale = COORD_SCALE * Tile.SIZE / 4096;

            // n is introduced if length increases while processing
            for (int k = 0, n = index.length; k < n; ) {
                if (index[k] < 0)
                    break;

                /* FIXME: workaround: dont overflow max index id. */
                if (vertexCnt >= 1 << 16)
                    break;

                // Get position of points for each polygon (which always has 3 points)
                int vtx1 = index[k++] * 3;
                int vtx2 = index[k++] * 3;
                int vtx3 = index[k++] * 3;

                float vx1 = points[vtx1 + 0];
                float vy1 = points[vtx1 + 1];
                float vz1 = points[vtx1 + 2];

                float vx2 = points[vtx2 + 0];
                float vy2 = points[vtx2 + 1];
                float vz2 = points[vtx2 + 2];

                float vx3 = points[vtx3 + 0];
                float vy3 = points[vtx3 + 1];
                float vz3 = points[vtx3 + 2];

                // Calculate normal for color gradient
                float ax = vx2 - vx1;
                float ay = vy2 - vy1;
                float az = vz2 - vz1;

                float bx = vx3 - vx1;
                float by = vy3 - vy1;
                float bz = vz3 - vz1;

                // Vector product (c is at right angle to a and b)
                float cx = ay * bz - az * by;
                float cy = az * bx - ax * bz;
                float cz = ax * by - ay * bx;

                double len = Math.sqrt(cx * cx + cy * cy + cz * cz);

                // packing the normal in two bytes
                //    int mx = FastMath.clamp(127 + (int) ((cx / len) * 128), 0, 0xff);
                //    int my = FastMath.clamp(127 + (int) ((cy / len) * 128), 0, 0xff);
                //    short normal = (short) ((my << 8) | (mx & NORMAL_DIR_MASK) | (cz > 0 ? 1 : 0));

                double p = Math.sqrt((cz / len) * 8.0 + 8.0);
                int mx = FastMath.clamp(127 + (int) ((cx / len / p) * 128), 0, 255);
                int my = FastMath.clamp(127 + (int) ((cy / len / p) * 128), 0, 255);
                short normal = (short) ((my << 8) | mx);

                if (key == null)
                    key = vertexPool.get();

                key.set((short) (vx1 * scale),
                        (short) (vy1 * scale),
                        (short) (vz1 * scale),
                        normal);

                Vertex vertex = mVertexMap.put(key, false);

                if (vertex == null) {
                    key.id = vertexCnt++;
                    addMeshIndex(key, true);
                    key = vertexPool.get();
                } else {
                    //numIndexHits++;
                    addMeshIndex(vertex, false);
                }

                key.set((short) (vx2 * scale),
                        (short) (vy2 * scale),
                        (short) (vz2 * scale),
                        normal);

                vertex = mVertexMap.put(key, false);

                if (vertex == null) {
                    key.id = vertexCnt++;
                    addMeshIndex(key, true);
                    key = vertexPool.get();
                } else {
                    //numIndexHits++;
                    addMeshIndex(vertex, false);
                }

                key.set((short) (vx3 * scale),
                        (short) (vy3 * scale),
                        (short) (vz3 * scale),
                        (short) normal);

                vertex = mVertexMap.put(key, false);
                if (vertex == null) {
                    key.id = vertexCnt++;
                    addMeshIndex(key, true);
                    key = vertexPool.get();
                } else {
                    //numIndexHits++;
                    addMeshIndex(vertex, false);
                }
            }

            vertexPool.release(key);
        }
        numVertices = vertexCnt;
    }

    private void addMeshIndex(Vertex v, boolean addVertex) {
        if (addVertex)
            vertexItems.add(v.x, v.y, v.z, v.n);

        mIndices[IND_MESH].add((short) v.id);
        numIndices++;
    }

    //    private void encodeNormal(float v[], int offset) {
    //        var p = Math.sqrt(cartesian.z * 8.0 + 8.0);
    //        var result = new Cartesian2();
    //        result.x = cartesian.x / p + 0.5;
    //        result.y = cartesian.y / p + 0.5;
    //        return result;
    //    }
    //
    //public void addNoNormal(MapElement element) {
    //    if (element.type != GeometryType.TRIS)
    //        return;
    //
    //    short[] index = element.index;
    //    float[] points = element.points;
    //
    //    /* current vertex id */
    //    int startVertex = sumVertices;
    //
    //    /* roof indices for convex shapes */
    //    int i = mCurIndices[IND_MESH].used;
    //    short[] indices = mCurIndices[IND_MESH].vertices;
    //
    //    int first = startVertex;
    //
    //    for (int k = 0, n = index.length; k < n;) {
    //        if (index[k] < 0)
    //            break;
    //
    //        if (i == VertexItem.SIZE) {
    //            mCurIndices[IND_MESH] = VertexItem.getNext(mCurIndices[IND_MESH]);
    //            indices = mCurIndices[IND_MESH].vertices;
    //            i = 0;
    //        }
    //        indices[i++] = (short) (first + index[k++]);
    //        indices[i++] = (short) (first + index[k++]);
    //        indices[i++] = (short) (first + index[k++]);
    //    }
    //    mCurIndices[IND_MESH].used = i;
    //
    //    short[] vertices = mCurVertices.vertices;
    //    int v = mCurVertices.used;
    //
    //    int vertexCnt = element.pointPos;
    //
    //    for (int j = 0; j < vertexCnt;) {
    //        /* add bottom and top vertex for each point */
    //        if (v == VertexItem.SIZE) {
    //            mCurVertices = VertexItem.getNext(mCurVertices);
    //            vertices = mCurVertices.vertices;
    //            v = 0;
    //        }
    //        /* set coordinate */
    //        vertices[v++] = (short) (points[j++] * COORD_SCALE);
    //        vertices[v++] = (short) (points[j++] * COORD_SCALE);
    //        vertices[v++] = (short) (points[j++] * COORD_SCALE);
    //        v++;
    //    }
    //
    //    mCurVertices.used = v;
    //    sumVertices += (vertexCnt / 3);
    //}

    /**
     * Add MapElement which provides polygons
     *
     * @param element   the map element to add
     * @param height    the maximum height of element
     * @param minHeight the minimum height of element
     */
    public void addPoly(GeometryBuffer element, float height, float minHeight) {

        int[] index = element.index;
        float[] points = element.points;

        /* 10 cm steps */
        /* match height with ground resolution (meter per pixel) */
        height = ExtrusionUtils.mapGroundScale(height, mGroundResolution);
        minHeight = ExtrusionUtils.mapGroundScale(minHeight, mGroundResolution);

        boolean complexOutline = false;
        boolean simpleOutline = true;

        /* current vertex id */
        int startVertex = numVertices;
        int length = 0, ipos = 0, ppos = 0;

        for (int n = index.length; ipos < n; ipos++, ppos += length) {
            length = index[ipos];

            /* end marker */
            if (length < 0)
                break;

            /* start next polygon */
            if (length == 0) {
                startVertex = numVertices;
                simpleOutline = true;
                complexOutline = false;
                continue;
            }

            /* check: drop last point from explicitly closed rings */
            int len = length;
            if (points[ppos] == points[ppos + len - 2]
                    && points[ppos + 1] == points[ppos + len - 1]) {
                len -= 2;
                log.debug("explicit closed poly " + len);
            }

            /* need at least three points (x and y) */
            if (len < 6)
                continue;

            /* check if polygon contains inner rings */
            if (simpleOutline && (ipos < n - 1) && (index[ipos + 1] > 0))
                simpleOutline = false;

            boolean convex = extrudeOutline(points, ppos, len, minHeight,
                    height, simpleOutline);

            if (simpleOutline && (convex || len <= 8)) {
                addRoofSimple(startVertex, len);
            } else if (!complexOutline) {
                complexOutline = true;
                addRoof(startVertex, element, ipos, ppos);
            }
        }
    }

    /**
     * roof indices for convex shapes
     */
    private void addRoofSimple(int startVertex, int len) {
        short first = (short) (startVertex + 1);
        VertexData it = mIndices[IND_ROOF];
        len -= 4;
        for (int k = 0; k < len; k += 2) {
            it.add(first,
                    (short) (first + k + 2),
                    (short) (first + k + 4));
        }
        numIndices += (len / 2) * 3;
    }

    /**
     * roof indices for concave shapes
     */
    private void addRoof(int startVertex, GeometryBuffer geom, int ipos, int ppos) {
        int[] index = geom.index;
        float[] points = geom.points;

        int numPoints = 0;
        int numRings = 0;

        /* get sum of points in polygon */
        // n is introduced if length increases while processing
        for (int i = ipos, n = index.length; i < n && index[i] > 0; i++) {
            numPoints += index[i];
            numRings++;
        }

        numIndices += Tessellator.tessellate(points, ppos, numPoints,
                index, ipos, numRings,
                startVertex + 1,
                mIndices[IND_ROOF]);
    }

    private boolean extrudeOutline(float[] points, int pos, int len,
                                   float minHeight, float height, boolean convex) {

        /* add two vertices for last face to make zigzag indices work */
        boolean addFace = (len % 4 != 0);
        int vertexCnt = len + (addFace ? 2 : 0);

        float cx = points[pos + len - 2];
        float cy = points[pos + len - 1];
        float nx = points[pos + 0];
        float ny = points[pos + 1];

        /* vector to next point */
        float vx = nx - cx;
        float vy = ny - cy;
        /* vector from previous point */
        float ux, uy;

        float a = (float) Math.sqrt(vx * vx + vy * vy);
        short color1 = (short) ((1 + vx / a) * 127);

        short fcolor = color1, color2 = 0;

        short h = (short) height, mh = (short) minHeight;

        int even = 0;
        int changeX = 0, changeY = 0, angleSign = 0;

        /* vertex offset for all vertices in layer */
        int vOffset = numVertices;

        mClipper.clipStart((int) nx, (int) ny);

        for (int i = 2, n = vertexCnt + 2; i < n; i += 2 /* , v += 8 */) {
            cx = nx;
            cy = ny;

            ux = vx;
            uy = vy;

            /* get direction to next point */
            if (i < len) {
                nx = points[pos + i + 0];
                ny = points[pos + i + 1];
            } else if (i == len) {
                nx = points[pos + 0];
                ny = points[pos + 1];
            } else { // if (addFace)
                short c = (short) (color1 | fcolor << 8);
                /* add bottom and top vertex for each point */
                vertexItems.add((short) (cx * COORD_SCALE), (short) (cy * COORD_SCALE), mh, c);
                vertexItems.add((short) (cx * COORD_SCALE), (short) (cy * COORD_SCALE), h, c);

                //v += 8;
                break;
            }

            vx = nx - cx;
            vy = ny - cy;

            /* set lighting (by direction) */
            a = (float) Math.sqrt(vx * vx + vy * vy);
            color2 = (short) ((1 + vx / a) * 127);

            short c;
            if (even == 0)
                c = (short) (color1 | color2 << 8);
            else
                c = (short) (color2 | color1 << 8);

            /* add bottom and top vertex for each point */
            vertexItems.add((short) (cx * COORD_SCALE), (short) (cy * COORD_SCALE), mh, c);
            vertexItems.add((short) (cx * COORD_SCALE), (short) (cy * COORD_SCALE), h, c);

            color1 = color2;

            /* check if polygon is convex */
            if (convex) {
                /* TODO simple polys with only one concave arc
                 * could be handled without special triangulation */
                if ((ux < 0 ? 1 : -1) != (vx < 0 ? 1 : -1))
                    changeX++;
                if ((uy < 0 ? 1 : -1) != (vy < 0 ? 1 : -1))
                    changeY++;

                if (changeX > 2 || changeY > 2)
                    convex = false;

                float cross = ux * vy - uy * vy;

                if (cross > 0) {
                    if (angleSign == -1)
                        convex = false;
                    angleSign = 1;
                } else if (cross < 0) {
                    if (angleSign == 1)
                        convex = false;
                    angleSign = -1;
                }
            }

            /* check if face is within tile */
            if (mClipper.clipNext((int) nx, (int) ny) == LineClipper.OUTSIDE) {
                even = ++even % 2;
                continue;
            }

            /* add ZigZagQuadIndices(tm) for sides */
            short vert = (short) (vOffset + (i - 2));
            short s0 = vert++;
            short s1 = vert++;
            short s2 = vert++;
            short s3 = vert++;

            /* connect last to first (when number of faces is even) */
            if (!addFace && i == len) {
                s2 -= len;
                s3 -= len;
            }

            mIndices[even].add(s0, s2, s1);
            mIndices[even].add(s1, s2, s3);
            numIndices += 6;

            /* flipp even-odd */
            even = ++even % 2;

            /* add roof outline indices */
            mIndices[IND_OUTLINE].add(s1, s3);
            numIndices += 2;
        }

        numVertices += vertexCnt;
        return convex;
    }

    @Override
    public void compile(ShortBuffer vboData, ShortBuffer iboData) {

        if (numVertices == 0)
            return;

        indiceOffset = iboData.position();

        int iOffset = indiceOffset;
        for (int i = 0; i <= IND_MESH; i++) {
            if (mIndices[i] != null) {
                idx[i] = mIndices[i].compile(iboData);
                off[i] = iOffset * 2;
                iOffset += idx[i];
            }
        }
        vertexOffset = vboData.position() * 2;
        vertexItems.compile(vboData);

        clear();
    }

    @Override
    public void clear() {
        mClipper = null;
        releaseVertexPool();

        if (mIndices != null) {
            for (int i = 0; i <= IND_MESH; i++) {
                if (mIndices[i] == null)
                    continue;
                mIndices[i].dispose();
            }
            mIndices = null;

            vertexItems.dispose();
        }
    }

    /**
     * @return the polygon colors (top, side, side, line)
     */
    public float[] getColors() {
        return colors;
    }

    /**
     * @return the mesh color
     */
    public int getColor() {
        return color;
    }

    @Override
    protected void prepare() {
        mClipper = null;
        releaseVertexPool();
    }

    void releaseVertexPool() {
        if (mVertexMap == null)
            return;

        synchronized (vertexPool) {
            vertexPool.releaseAll(mVertexMap.releaseItems());
            mVertexMap = vertexMapPool.release(mVertexMap);
        }
    }

    public ExtrusionBucket next() {
        return (ExtrusionBucket) next;
    }
}
