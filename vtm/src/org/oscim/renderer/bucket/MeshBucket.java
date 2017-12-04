/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2017 devemux86
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
import org.oscim.backend.canvas.Color;
import org.oscim.core.GeometryBuffer;
import org.oscim.core.MapPosition;
import org.oscim.core.MercatorProjection;
import org.oscim.renderer.GLShader;
import org.oscim.renderer.GLState;
import org.oscim.renderer.GLUtils;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.bucket.VertexData.Chunk;
import org.oscim.theme.styles.AreaStyle;
import org.oscim.utils.ColorUtil;
import org.oscim.utils.TessJNI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.oscim.backend.GLAdapter.gl;
import static org.oscim.renderer.MapRenderer.COORD_SCALE;

public class MeshBucket extends RenderBucket {
    static final Logger log = LoggerFactory.getLogger(MeshBucket.class);
    static final boolean dbgRender = false;

    public AreaStyle area;
    public float heightOffset;

    private TessJNI tess;

    private int numPoints;

    public MeshBucket(int level) {
        super(RenderBucket.MESH, true, false);
        this.level = level;
    }

    public void addMesh(GeometryBuffer geom) {
        numPoints += geom.pointNextPos;
        if (tess == null)
            tess = new TessJNI(8);

        tess.addContour2D(geom.index, geom.points);
    }

    public void addConvexMesh(GeometryBuffer geom) {
        short start = (short) numVertices;

        if (numVertices >= (1 << 16)) {
            return;
        }

        vertexItems.add(geom.points[0] * COORD_SCALE,
                geom.points[1] * COORD_SCALE);

        vertexItems.add(geom.points[2] * COORD_SCALE,
                geom.points[3] * COORD_SCALE);
        short prev = (short) (start + 1);

        numVertices += 2;

        for (int i = 4; i < geom.index[0]; i += 2) {

            vertexItems.add(geom.points[i + 0] * COORD_SCALE,
                    geom.points[i + 1] * COORD_SCALE);

            indiceItems.add(start, prev, ++prev);
            numVertices++;

            numIndices += 3;
        }

        //numPoints += geom.pointPos;
        //tess.addContour2D(geom.index, geom.points);
    }

    protected void prepare() {
        if (tess == null)
            return;

        if (numPoints == 0) {
            tess.dispose();
            return;
        }
        if (!tess.tesselate()) {
            tess.dispose();
            log.error("error in tessellation {}", numPoints);
            return;
        }

        int nelems = tess.getElementCount() * 3;

        //int startVertex = vertexItems.countSize();

        for (int offset = indiceItems.countSize(); offset < nelems; ) {
            int size = nelems - offset;
            if (size > VertexData.SIZE)
                size = VertexData.SIZE;

            Chunk chunk = indiceItems.obtainChunk();

            tess.getElements(chunk.vertices, offset, size);
            offset += size;

            //if (startVertex != 0)
            // FIXME

            indiceItems.releaseChunk(size);
        }

        int nverts = tess.getVertexCount() * 2;

        for (int offset = 0; offset < nverts; ) {
            int size = nverts - offset;
            if (size > VertexData.SIZE)
                size = VertexData.SIZE;

            Chunk chunk = vertexItems.obtainChunk();

            tess.getVertices(chunk.vertices, offset, size, COORD_SCALE);
            offset += size;

            vertexItems.releaseChunk(size);
        }

        this.numIndices += nelems;
        this.numVertices += nverts >> 1;

        tess.dispose();
    }

    public static class Renderer {
        static Shader shader;

        static boolean init() {
            shader = new Shader("mesh_layer_2D");
            return true;
        }

        static class Shader extends GLShader {
            int uMVP, uColor, uHeight, aPos;

            Shader(String shaderFile) {
                if (!create(shaderFile))
                    return;

                uMVP = getUniform("u_mvp");
                uColor = getUniform("u_color");
                uHeight = getUniform("u_height");
                aPos = getAttrib("a_pos");
            }
        }

        public static RenderBucket draw(RenderBucket l, GLViewport v) {
            GLState.blend(true);

            Shader s = shader;

            s.useProgram();
            GLState.enableVertexArrays(s.aPos, -1);

            v.mvp.setAsUniform(s.uMVP);

            float heightOffset = 0;
            gl.uniform1f(s.uHeight, heightOffset);

            for (; l != null && l.type == MESH; l = l.next) {
                MeshBucket ml = (MeshBucket) l;
                AreaStyle area = ml.area.current();

                if (area.heightOffset != ml.heightOffset)
                    ml.heightOffset = area.heightOffset;
                if (ml.heightOffset != heightOffset) {
                    heightOffset = ml.heightOffset;

                    gl.uniform1f(s.uHeight, heightOffset /
                            MercatorProjection.groundResolution(v.pos));
                }

                if (ml.area == null)
                    GLUtils.setColor(s.uColor, Color.BLUE, 0.4f);
                else {
                    setColor(area, s, v.pos);
                }
                gl.vertexAttribPointer(s.aPos, 2, GL.SHORT,
                        false, 0, ml.vertexOffset);

                gl.drawElements(GL.TRIANGLES,
                        ml.numIndices,
                        GL.UNSIGNED_SHORT,
                        ml.indiceOffset);

                if (dbgRender) {
                    int c = (ml.area == null) ? Color.BLUE : ml.area.color;
                    gl.lineWidth(1);
                    //c = ColorUtil.shiftHue(c, 0.5);
                    c = ColorUtil.modHsv(c, 1.1, 1.0, 0.8, true);
                    GLUtils.setColor(s.uColor, c, 1);
                    gl.drawElements(GL.LINES,
                            ml.numIndices,
                            GL.UNSIGNED_SHORT,
                            ml.vertexOffset);
                }
            }
            return l;
        }

        private static final int OPAQUE = 0xff000000;

        static void setColor(AreaStyle a, Shader s, MapPosition pos) {
            float fade = a.getFade(pos.scale);
            float blend = a.getBlend(pos.scale);

            if (fade < 1.0f) {
                GLState.blend(true);
                GLUtils.setColor(s.uColor, a.color, fade);
            } else if (blend > 0.0f) {
                if (blend == 1.0f)
                    GLUtils.setColor(s.uColor, a.blendColor, 1);
                else
                    GLUtils.setColorBlend(s.uColor, a.color,
                            a.blendColor, blend);
            } else {
                /* test if color contains alpha */
                GLState.blend((a.color & OPAQUE) != OPAQUE);
                GLUtils.setColor(s.uColor, a.color, 1);
            }
        }
    }
}
