/*
 * Copyright 2012, 2013 Hannes Janetzek
 * Copyright 2016 Longri
 * Copyright 2018 devemux86
 *
 * This file is part of the OpenScienceMap project (http://www.opensciencemap.org).
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General License for more details.
 *
 * You should have received a copy of the GNU Lesser General License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.oscim.renderer;

import org.oscim.backend.GL;
import org.oscim.backend.GLAdapter;
import org.oscim.backend.canvas.Color;
import org.oscim.map.Map;
import org.oscim.renderer.bucket.RenderBuckets;
import org.oscim.renderer.bucket.TextureItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.oscim.backend.GLAdapter.gl;

public class MapRenderer {
    static final Logger log = LoggerFactory.getLogger(MapRenderer.class);

    /**
     * scale factor used for short vertices
     */
    public static float COORD_SCALE = 8.0f;

    private final Map mMap;
    private final GLViewport mViewport;

    private static float[] mClearColor;

    private static int mQuadIndicesID;
    private static int mQuadVerticesID;

    /**
     * Number of Quads that can be rendered with bindQuadIndicesVBO()
     */
    public static final int MAX_QUADS = 512;
    /**
     * Number of Indices that can be rendered with bindQuadIndicesVBO()
     */
    public static final int MAX_INDICES = MAX_QUADS * 6;

    public static long frametime;
    private static boolean rerender;

    private static NativeBufferPool mBufferPool;

    private float viewPortScale = 1;

    public MapRenderer(Map map) {
        mMap = map;
        mViewport = new GLViewport();
        mBufferPool = new NativeBufferPool();

        /* FIXME should be done in 'destroy' method
         * clear all previous vbo refs */
        BufferObject.clear();
        setBackgroundColor(Color.DKGRAY);
    }

    public static void setBackgroundColor(int color) {
        mClearColor = GLUtils.colorToFloat(color);
    }

    /**
     * Set the scale value for map viewport.
     */
    public void setViewPortScale(float scale) {
        this.viewPortScale = scale;
    }

    public void onDrawFrame() {
        frametime = System.currentTimeMillis();
        rerender = false;

        mMap.beginFrame();

        draw();

        mMap.doneFrame(rerender);

        mBufferPool.releaseBuffers();
        TextureItem.disposeTextures();
    }

    private void draw() {

        GLState.setClearColor(mClearColor);

        gl.depthMask(true);
        gl.stencilMask(0xFF);

        gl.clear(GL.COLOR_BUFFER_BIT
                | GL.DEPTH_BUFFER_BIT
                | GL.STENCIL_BUFFER_BIT);

        gl.depthMask(false);
        gl.stencilMask(0);

        GLState.test(false, false);
        GLState.blend(false);
        GLState.bindTex2D(GLState.DISABLED);
        GLState.useProgram(GLState.DISABLED);
        GLState.bindElementBuffer(GLState.DISABLED);
        GLState.bindVertexBuffer(GLState.DISABLED);

        mViewport.setFrom(mMap);

        if (GLAdapter.debugView) {
            /* modify this to scale only the view, to see
             * which tiles are rendered */
            mViewport.mvp.setScale(0.5f, 0.5f, 1);
            mViewport.viewproj.multiplyLhs(mViewport.mvp);
            mViewport.proj.multiplyLhs(mViewport.mvp);
        }

        if (this.viewPortScale != 1) {
            mViewport.mvp.setScale(this.viewPortScale, this.viewPortScale, 1);
            mViewport.viewproj.multiplyLhs(mViewport.mvp);
            mViewport.proj.multiplyLhs(mViewport.mvp);
        }

        /* update layers */
        LayerRenderer[] layers = mMap.layers().getLayerRenderer();

        for (int i = 0, n = layers.length; i < n; i++) {
            LayerRenderer renderer = layers[i];
            if (renderer == null)
                continue;

            if (!renderer.isInitialized) {
                renderer.setup();
                renderer.isInitialized = true;
            }

            renderer.update(mViewport);

            if (renderer.isReady)
                renderer.render(mViewport);

            if (GLAdapter.debug)
                GLUtils.checkGlError(renderer.getClass().getName());
        }

        if (GLUtils.checkGlError(getClass().getName() + ": finish", GL.OUT_OF_MEMORY)) {
            BufferObject.checkBufferUsage(true);
            // FIXME also throw out some textures etc
        }
    }

    public void onSurfaceChanged(int width, int height) {
        //log.debug("onSurfaceChanged: new={}, {}x{}", mNewSurface, width, height);

        if (width <= 0 || height <= 0)
            return;

        GLState.viewport(width, height);

        //GL.scissor(0, 0, width, height);
        //GL.enable(GL20.SCISSOR_TEST);

        gl.clearStencil(0x00);

        gl.disable(GL.CULL_FACE);
        gl.blendFunc(GL.ONE, GL.ONE_MINUS_SRC_ALPHA);

        gl.frontFace(GL.CW);
        gl.cullFace(GL.BACK);

        if (!mNewSurface) {
            mMap.updateMap(false);
            return;
        }

        mNewSurface = false;

        /* initialize quad indices used by Texture- and LineTexRenderer */
        int[] vboIds = GLUtils.glGenBuffers(2);

        mQuadIndicesID = vboIds[0];

        short[] indices = new short[MAX_INDICES];
        for (int i = 0, j = 0; i < MAX_INDICES; i += 6, j += 4) {
            indices[i + 0] = (short) (j + 0);
            indices[i + 1] = (short) (j + 1);
            indices[i + 2] = (short) (j + 2);

            indices[i + 3] = (short) (j + 2);
            indices[i + 4] = (short) (j + 1);
            indices[i + 5] = (short) (j + 3);
        }
        ShortBuffer buf = MapRenderer.getShortBuffer(indices.length);
        buf.put(indices);
        buf.flip();

        GLState.bindElementBuffer(mQuadIndicesID);
        gl.bufferData(GL.ELEMENT_ARRAY_BUFFER,
                indices.length * 2, buf,
                GL.STATIC_DRAW);
        GLState.bindElementBuffer(GLState.UNBIND);

        /* initialize default quad */
        FloatBuffer floatBuffer = MapRenderer.getFloatBuffer(8);
        float[] quad = new float[]{-1, -1, -1, 1, 1, -1, 1, 1};
        floatBuffer.put(quad);
        floatBuffer.flip();
        mQuadVerticesID = vboIds[1];

        GLState.bindVertexBuffer(mQuadVerticesID);
        gl.bufferData(GL.ARRAY_BUFFER,
                quad.length * 4, floatBuffer,
                GL.STATIC_DRAW);
        GLState.bindVertexBuffer(GLState.UNBIND);

        GLState.init();

        mMap.updateMap(true);
    }

    public void onSurfaceCreated() {
        // log.debug(GL.getString(GL20.EXTENSIONS));
        String vendor = gl.getString(GL.VENDOR);
        String renderer = gl.getString(GL.RENDERER);
        String version = gl.getString(GL.VERSION);
        log.debug("{}/{}/{}", vendor, renderer, version);

        // Prevent issue with Adreno 3xx series
        if (renderer != null && renderer.startsWith("Adreno (TM) 3")) {
            log.debug("==> not using glBufferSubData");
            GLAdapter.NO_BUFFER_SUB_DATA = true;
        }

        GLState.init();

        // Set up some vertex buffer objects
        BufferObject.init(200);

        // classes that require GL context for initialization
        RenderBuckets.initRenderer();

        mNewSurface = true;
    }

    private boolean mNewSurface;

    /**
     * Bind VBO for a simple quad. Handy for simple custom RenderLayers
     * Vertices: float[]{ -1, -1, -1, 1, 1, -1, 1, 1 }
     * <p/>
     * GL.drawArrays(GL20.TRIANGLE_STRIP, 0, 4);
     */
    public static void bindQuadVertexVBO(int location) {

        if (location >= 0) {
            GLState.bindVertexBuffer(mQuadVerticesID);
            GLState.enableVertexArrays(location, GLState.DISABLED);
            gl.vertexAttribPointer(location, 2, GL.FLOAT, false, 0, 0);
        }
    }

    /**
     * Bind indices for rendering up to MAX_QUADS (512),
     * ie. MAX_INDICES (512*6) in one draw call.
     * Vertex order is 0-1-2 2-1-3
     */
    public static void bindQuadIndicesVBO() {
        GLState.bindElementBuffer(mQuadIndicesID);
    }

    /**
     * Trigger next redraw from GL-Thread. This should be used to animate
     * LayerRenderers instead of calling Map.render().
     */
    public static void animate() {
        rerender = true;
    }

    public static FloatBuffer getFloatBuffer(int size) {
        return mBufferPool.getFloatBuffer(size);
    }

    public static ShortBuffer getShortBuffer(int size) {
        return mBufferPool.getShortBuffer(size);
    }

    public static IntBuffer getIntBuffer(int size) {
        return mBufferPool.getIntBuffer(size);
    }

}
