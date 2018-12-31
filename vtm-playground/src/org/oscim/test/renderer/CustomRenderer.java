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
package org.oscim.test.renderer;

import org.oscim.backend.GL;
import org.oscim.core.MapPosition;
import org.oscim.map.Map;
import org.oscim.renderer.GLShader;
import org.oscim.renderer.GLState;
import org.oscim.renderer.GLUtils;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.LayerRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static org.oscim.backend.GLAdapter.gl;

/*
 * This is an example how to integrate custom OpenGL drawing routines as map overlay
 *
 * based on chapter 2 from:
 * https://github.com/dalinaum/opengl-es-book-samples/tree/master/Android
 * */

public class CustomRenderer extends LayerRenderer {

    private final Map mMap;
    private final MapPosition mMapPosition;

    private int mProgramObject;
    private int hVertexPosition;
    private int hMatrixPosition;

    private FloatBuffer mVertices;
    private final float[] mVerticesData = {
            -200, -200, 1.0f,
            200, 200, 0,
            -200, 200, 0.5f,
            200, -200, 0.5f,
    };
    private boolean mInitialized;

    public CustomRenderer(Map map) {
        mMap = map;
        mMapPosition = new MapPosition();
    }

    // ---------- everything below runs in GLRender Thread ----------
    @Override
    public void update(GLViewport v) {
        if (!mInitialized) {
            if (!init())
                return;

            mInitialized = true;

            // fix current MapPosition
            mMapPosition.copy(v.pos);

            compile();
        }
    }

    protected void compile() {
        // modify mVerticesData and put in FloatBuffer

        mVertices.clear();
        mVertices.put(mVerticesData);
        mVertices.flip();

        setReady(true);
    }

    @Override
    public void render(GLViewport v) {

        // Use the program object
        GLState.useProgram(mProgramObject);

        GLState.blend(true);
        GLState.test(false, false);

        // unbind previously bound VBOs
        GLState.bindBuffer(GL.ARRAY_BUFFER, GLState.UNBIND);

        // Load the vertex data
        //mVertices.position(0);
        gl.vertexAttribPointer(hVertexPosition, 3, GL.FLOAT, false, 0, mVertices);
        //mVertices.position(2);
        //GL.vertexAttribPointer(hVertexPosition, 2, GL20.FLOAT, false, 4, mVertices);

        GLState.enableVertexArrays(hVertexPosition, GLState.DISABLED);

        /* apply view and projection matrices */
        // set mvp (tmp) matrix relative to mMapPosition
        // i.e. fixed on the map

        float ratio = 1f / mMap.getWidth();

        v.mvp.setScale(ratio, ratio, 1);
        v.mvp.multiplyLhs(v.proj);
        v.mvp.setAsUniform(hMatrixPosition);

        // Draw the triangle
        gl.drawArrays(GL.TRIANGLE_STRIP, 0, 4);

        GLUtils.checkGlError(getClass().getName() + ": ...");
    }

    private boolean init() {
        // Load the vertex/fragment shaders
        int programObject = GLShader.createProgram(vShaderStr, fShaderStr);

        if (programObject == 0)
            return false;

        // Handle for vertex position in shader
        hVertexPosition = gl.getAttribLocation(programObject, "a_pos");

        hMatrixPosition = gl.getUniformLocation(programObject, "u_mvp");

        // Store the program object
        mProgramObject = programObject;

        mVertices = ByteBuffer.allocateDirect(mVerticesData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        return true;
    }

    private static final String vShaderStr = "" +
            "precision mediump float;"
            + "uniform mat4 u_mvp;"
            + "attribute vec4 a_pos;"
            + "varying float alpha;"
            + "void main()"
            + "{"
            + "   gl_Position = u_mvp * vec4(a_pos.xy, 0.0, 1.0);"
            + "   alpha = a_pos.z;"
            + "}";

    private static final String fShaderStr = "" +
            "precision mediump float;"
            + "varying float alpha;"
            + "void main()"
            + "{"
            + "  gl_FragColor = vec4 (alpha, 1.0-alpha, 0.0, 0.7 );"
            + "}";

}
