/*
 * Copyright 2016 devemux86
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
package org.oscim.test.renderer;

import org.oscim.backend.GL;
import org.oscim.backend.canvas.Color;
import org.oscim.gdx.GdxMapApp;
import org.oscim.layers.GenericLayer;
import org.oscim.renderer.BucketRenderer;
import org.oscim.renderer.BufferObject;
import org.oscim.renderer.GLShader;
import org.oscim.renderer.GLState;
import org.oscim.renderer.GLUtils;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.MapRenderer;
import org.oscim.utils.FastMath;

import java.nio.FloatBuffer;

import static org.oscim.backend.GLAdapter.gl;
import static org.oscim.renderer.MapRenderer.COORD_SCALE;

public class HexagonRenderTest extends GdxMapApp {

    @Override
    public void createLayers() {
        mMap.setMapPosition(0, 0, 1 << 4);
        mMap.layers().add(new GenericLayer(mMap, new HexagonRenderer()));
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new HexagonRenderTest());
    }

    /* This is an example how to integrate custom OpenGL drawing routines as map
     * overlay
     *
     * based on chapter 2 from:
     * https://github.com/dalinaum/opengl-es-book-samples/tree/master/Android */

    static class HexagonRenderer extends BucketRenderer {

        private int mProgramObject;
        private int hVertexPosition;
        private int hMatrixPosition;
        private int hColorPosition;
        private int hCenterPosition;

        //private FloatBuffer mVertices;
        private boolean mInitialized;
        private BufferObject mVBO;

        int mZoom = -1;
        float mCellScale = 60 * COORD_SCALE;

        @Override
        public void update(GLViewport v) {
            if (!mInitialized) {
                if (!init()) {
                    return;
                }
                mInitialized = true;

                compile();
                mMapPosition.copy(v.pos);
            }

            //if (mZoom != v.pos.zoomLevel) {
            //    mMapPosition.copy(v.pos);
            //    mZoom = v.pos.zoomLevel;
            //}
        }

        @Override
        protected void compile() {

            float[] vertices = new float[12];

            for (int i = 0; i < 6; i++) {
                vertices[i * 2 + 0] = (float) Math.cos(Math.PI * 2 * i / 6) * mCellScale;
                vertices[i * 2 + 1] = (float) Math.sin(Math.PI * 2 * i / 6) * mCellScale;
            }
            FloatBuffer buf = MapRenderer.getFloatBuffer(12);
            buf.put(vertices);

            mVBO = BufferObject.get(GL.ARRAY_BUFFER, 0);
            mVBO.loadBufferData(buf.flip(), 12 * 4);

            setReady(true);
        }

        @Override
        public void render(GLViewport v) {

            // Use the program object
            GLState.useProgram(mProgramObject);

            GLState.blend(true);
            GLState.test(false, false);

            // bind VBO data
            mVBO.bind();

            // set VBO vertex layout
            gl.vertexAttribPointer(hVertexPosition, 2, GL.FLOAT, false, 0, 0);

            GLState.enableVertexArrays(hVertexPosition, GLState.DISABLED);

            /* apply view and projection matrices */
            // set mvp (tmp) matrix relative to mMapPosition
            // i.e. fixed on the map
            setMatrix(v);
            v.mvp.setAsUniform(hMatrixPosition);

            final int offset_x = 4;
            final int offset_y = 16;

            float h = (float) (Math.sqrt(3) / 2);
            for (int y = -offset_y; y < offset_y; y++) {
                for (int x = -offset_x; x < offset_x; x++) {
                    float xx = x * 2 + (y % 2 == 0 ? 1 : 0);
                    float yy = y * h + h / 2;

                    gl.uniform2f(hCenterPosition, xx * (mCellScale * 1.5f), yy * mCellScale);

                    //float alpha = 1 + (float) Math.log10(FastMath.clamp(
                    //        (float) Math.sqrt(xx * xx + yy * yy) / offset_y, 0.0f, 1.0f)) * 2;

                    float alpha = (float) Math.sqrt(xx * xx + yy * yy) / offset_y;

                    float fy = (float) (y + offset_y) / (offset_y * 2);
                    float fx = (float) (x + offset_x) / (offset_x * 2);
                    float fz = FastMath.clamp(
                            (float) (x < 0 || y < 0 ? 1 - Math.sqrt(fx * fx + fy
                                    * fy)
                                    : 0),
                            0,
                            1);

                    int c = 0xff << 24
                            | (int) (0xff * fy) << 16
                            | (int) (0xff * fx) << 8
                            | (int) (0xff * fz);

                    GLUtils.setColor(hColorPosition, c, alpha);

                    gl.drawArrays(GL.TRIANGLE_FAN, 0, 6);
                }
            }

            GLUtils.setColor(hColorPosition, Color.DKGRAY, 0.3f);

            for (int y = -offset_y; y < offset_y; y++) {
                for (int x = -offset_x; x < offset_x; x++) {
                    float xx = x * 2 + (y % 2 == 0 ? 1 : 0);
                    float yy = y * h + h / 2;

                    gl.uniform2f(hCenterPosition, xx * (mCellScale * 1.5f), yy * mCellScale);
                    gl.drawArrays(GL.LINE_LOOP, 0, 6);
                }
            }

            GLUtils.checkGlError(getClass().getName() + ": render() end");
        }

        private boolean init() {
            // Load the vertex/fragment shaders
            int programObject = GLShader.createProgram(vShaderStr, fShaderStr);

            if (programObject == 0)
                return false;

            // Handle for vertex position in shader
            hVertexPosition = gl.getAttribLocation(programObject, "a_pos");

            hMatrixPosition = gl.getUniformLocation(programObject, "u_mvp");

            hColorPosition = gl.getUniformLocation(programObject, "u_color");

            hCenterPosition = gl.getUniformLocation(programObject, "u_center");

            // Store the program object
            mProgramObject = programObject;

            return true;
        }

        private static final String vShaderStr = ""
                + "#ifdef GLES\n"
                + "precision mediump float;\n"
                + "#endif\n"
                + "uniform mat4 u_mvp;"
                + "uniform vec2 u_center;"
                + "attribute vec2 a_pos;"
                + "void main()"
                + "{"
                + "   gl_Position = u_mvp * vec4(u_center + a_pos, 0.0, 1.0);"
                + "}";

        private static final String fShaderStr = ""
                + "#ifdef GLES\n"
                + "precision mediump float;\n"
                + "#endif\n"
                + "varying float alpha;"
                + "uniform vec4 u_color;"
                + "void main()"
                + "{"
                + "  gl_FragColor = u_color;"
                + "}";

    }

}
