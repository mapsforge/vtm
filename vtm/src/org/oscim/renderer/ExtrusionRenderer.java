/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2017 Izumi Kawashima
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
package org.oscim.renderer;

import org.oscim.backend.GL;
import org.oscim.core.Tile;
import org.oscim.renderer.bucket.ExtrusionBucket;
import org.oscim.renderer.bucket.ExtrusionBuckets;
import org.oscim.utils.FastMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.oscim.backend.GLAdapter.gl;
import static org.oscim.renderer.MapRenderer.COORD_SCALE;

public abstract class ExtrusionRenderer extends LayerRenderer {
    static final Logger log = LoggerFactory.getLogger(ExtrusionRenderer.class);

    private final boolean mTranslucent;
    private final int mMode;
    private Shader mShader;

    protected ExtrusionBuckets[] mExtrusionBucketSet = {};
    protected int mBucketsCnt;
    protected float mAlpha = 1;

    private float mZSolutionLimit;

    public ExtrusionRenderer(boolean mesh, boolean alpha) {
        mMode = mesh ? 1 : 0;
        mTranslucent = alpha;
    }

    public static class Shader extends GLShader {
        int uMVP, uColor, uAlpha, uMode, aPos, aLight, uZSolutionLimit;

        public Shader(String shader) {
            if (!create(shader))
                return;

            uMVP = getUniform("u_mvp");
            uColor = getUniform("u_color");
            uAlpha = getUniform("u_alpha");
            uMode = getUniform("u_mode");
            uZSolutionLimit = getUniform("u_z_solution_limit");
            aPos = getAttrib("a_pos");
            aLight = getAttrib("a_light");
        }
    }

    @Override
    public boolean setup() {
        if (mMode == 0)
            mShader = new Shader("extrusion_layer_ext");
        else
            mShader = new Shader("extrusion_layer_mesh");

        return true;
    }

    private void renderCombined(int vertexPointer, ExtrusionBuckets ebs) {

        for (ExtrusionBucket eb = ebs.buckets(); eb != null; eb = eb.next()) {

            gl.vertexAttribPointer(vertexPointer, 3,
                    GL.SHORT, false, 8,
                    eb.getVertexOffset());

            int sumIndices = eb.idx[0] + eb.idx[1] + eb.idx[2];

            /* extrusion */
            if (sumIndices > 0)
                gl.drawElements(GL.TRIANGLES, sumIndices,
                        GL.UNSIGNED_SHORT, eb.off[0]);

            /* mesh */
            if (eb.idx[4] > 0) {
                gl.drawElements(GL.TRIANGLES, eb.idx[4],
                        GL.UNSIGNED_SHORT, eb.off[4]);
            }
        }
    }

    public void setZSolutionLimit(float zSolutionLimit) {
        this.mZSolutionLimit = zSolutionLimit;
    }

    @Override
    public void render(GLViewport v) {

        float[] currentColors = null;
        float currentAlpha = 0;

        gl.depthMask(true);
        gl.clear(GL.DEPTH_BUFFER_BIT);

        GLState.test(true, false);

        Shader s = mShader;
        s.useProgram();
        GLState.enableVertexArrays(s.aPos, -1);

        /* only use face-culling when it's unlikely
         * that one'moves through the building' */
        if (v.pos.zoomLevel < 18)
            gl.enable(GL.CULL_FACE);

        gl.depthFunc(GL.LESS);
        gl.uniform1f(s.uAlpha, mAlpha);
        gl.uniform1f(s.uZSolutionLimit, mZSolutionLimit);

        ExtrusionBuckets[] ebs = mExtrusionBucketSet;

        if (mTranslucent) {
            /* only draw to depth buffer */
            GLState.blend(false);
            gl.colorMask(false, false, false, false);
            gl.uniform1i(s.uMode, -1);

            for (int i = 0; i < mBucketsCnt; i++) {
                if (ebs[i].ibo == null)
                    return;

                ebs[i].ibo.bind();
                ebs[i].vbo.bind();

                setMatrix(s, v, ebs[i]);

                float alpha = mAlpha * getFade(ebs[i]);
                if (alpha != currentAlpha) {
                    gl.uniform1f(s.uAlpha, alpha);
                    currentAlpha = alpha;
                }

                renderCombined(s.aPos, ebs[i]);
            }

            /* only draw to color buffer */
            gl.colorMask(true, true, true, true);
            gl.depthMask(false);
            gl.depthFunc(GL.EQUAL);
        }

        GLState.blend(true);

        GLState.enableVertexArrays(s.aPos, s.aLight);

        for (int i = 0; i < mBucketsCnt; i++) {
            if (ebs[i].ibo == null)
                continue;

            ebs[i].ibo.bind();
            ebs[i].vbo.bind();

            if (!mTranslucent)
                setMatrix(s, v, ebs[i]);

            float alpha = mAlpha * getFade(ebs[i]);
            if (alpha != currentAlpha) {
                gl.uniform1f(s.uAlpha, alpha);
                currentAlpha = alpha;
            }

            ExtrusionBucket eb = ebs[i].buckets();

            for (; eb != null; eb = eb.next()) {

                if (eb.getColors() != currentColors) {
                    currentColors = eb.getColors();
                    GLUtils.glUniform4fv(s.uColor,
                            mMode == 0 ? 4 : 1,
                            currentColors);
                }

                gl.vertexAttribPointer(s.aPos, 3, GL.SHORT,
                        false, 8, eb.getVertexOffset());

                gl.vertexAttribPointer(s.aLight, 2, GL.UNSIGNED_BYTE,
                        false, 8, eb.getVertexOffset() + 6);

                /* draw extruded outlines */
                if (eb.idx[0] > 0) {
                    if (mTranslucent) {
                        gl.depthFunc(GL.EQUAL);
                        setMatrix(s, v, ebs[i]);
                    }

                    /* draw roof */
                    gl.uniform1i(s.uMode, 0);
                    gl.drawElements(GL.TRIANGLES, eb.idx[2],
                            GL.UNSIGNED_SHORT, eb.off[2]);

                    /* draw sides 1 */
                    gl.uniform1i(s.uMode, 1);
                    gl.drawElements(GL.TRIANGLES, eb.idx[0],
                            GL.UNSIGNED_SHORT, eb.off[0]);

                    /* draw sides 2 */
                    gl.uniform1i(s.uMode, 2);
                    gl.drawElements(GL.TRIANGLES, eb.idx[1],
                            GL.UNSIGNED_SHORT, eb.off[1]);

                    if (mTranslucent) {
                        /* drawing gl_lines with the same coordinates
                         * does not result in same depth values as
                         * polygons, so add offset and draw gl_lequal */
                        gl.depthFunc(GL.LEQUAL);
                        v.mvp.addDepthOffset(100);
                        v.mvp.setAsUniform(s.uMVP);
                    }

                    gl.uniform1i(s.uMode, 3);

                    gl.drawElements(GL.LINES, eb.idx[3],
                            GL.UNSIGNED_SHORT, eb.off[3]);
                }

                /* draw triangle meshes */
                if (eb.idx[4] > 0) {
                    gl.drawElements(GL.TRIANGLES, eb.idx[4],
                            GL.UNSIGNED_SHORT, eb.off[4]);
                }
            }

            /* just a temporary reference! */
            ebs[i] = null;
        }

        if (!mTranslucent)
            gl.depthMask(false);

        if (v.pos.zoomLevel < 18)
            gl.disable(GL.CULL_FACE);
    }

    private float getFade(ExtrusionBuckets ebs) {
        if (ebs.animTime == 0)
            ebs.animTime = MapRenderer.frametime - 50;

        return FastMath.clamp((float) (MapRenderer.frametime - ebs.animTime) / 300f, 0f, 1f);
    }

    private void setMatrix(Shader s, GLViewport v, ExtrusionBuckets l) {

        int z = l.zoomLevel;
        double curScale = Tile.SIZE * v.pos.scale;
        float scale = (float) (v.pos.scale / (1 << z));

        float x = (float) ((l.x - v.pos.x) * curScale);
        float y = (float) ((l.y - v.pos.y) * curScale);

        v.mvp.setTransScale(x, y, scale / COORD_SCALE);
        v.mvp.setValue(10, scale / 10);
        v.mvp.multiplyLhs(v.viewproj);

        if (mTranslucent) {
            /* should avoid z-fighting of overlapping
             * building from different tiles */
            int zoom = (1 << z);
            int delta = (int) (l.x * zoom) % 4 + (int) (l.y * zoom) % 4 * 4;
            v.mvp.addDepthOffset(delta);
        }
        v.mvp.setAsUniform(s.uMVP);
    }
}
