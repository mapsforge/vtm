/*
 * Copyright 2014 Hannes Janetzek
 * Copyright 2019 Gustl22
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.oscim.backend.GLAdapter.gl;

public class OffscreenRenderer extends LayerRenderer {
    static final Logger log = LoggerFactory.getLogger(OffscreenRenderer.class);

    public enum Mode {
        FXAA, // Fast Approximate Anti-Aliasing
        SSAO, // Screen Space Ambient Occlusion
        SSAO_FXAA,
        BYPASS
    }

    int fb;
    int renderTex;
    int renderDepth;

    int texW = -1;
    int texH = -1;

    boolean initialized;
    private float[] mClearColor = {0, 0, 0, 0};

    private boolean useDepthTexture = false;
    private Shader mShader;

    static class Shader extends GLShader {
        int aPos, uTexDepth, uTexColor, uPixel;

        Shader(String shaderFile) {
            if (!create(shaderFile))
                return;
            aPos = getAttrib("a_pos");
            uTexColor = getUniform("u_texColor");
            uTexDepth = getUniform("u_tex");
            uPixel = getUniform("u_pixel");
        }
    }

    public final Mode mode;

    public OffscreenRenderer(Mode mode, LayerRenderer renderer) {
        this.mode = mode;
        if (mode == Mode.SSAO || mode == Mode.SSAO_FXAA)
            useDepthTexture = true;
        setRenderer(renderer);
    }

    protected boolean setupFBO(GLViewport viewport) {
        texW = (int) viewport.getWidth();
        texH = (int) viewport.getHeight();

        fb = GLUtils.glGenFrameBuffers(1)[0];
        renderTex = GLUtils.glGenTextures(1)[0];

        GLUtils.checkGlError(getClass().getName() + ": 0");

        gl.bindFramebuffer(GL.FRAMEBUFFER, fb);

        // generate color texture
        gl.bindTexture(GL.TEXTURE_2D, renderTex);

        GLUtils.setTextureParameter(
                GL.LINEAR,
                GL.LINEAR,
                //GL20.NEAREST,
                //GL20.NEAREST,
                GL.CLAMP_TO_EDGE,
                GL.CLAMP_TO_EDGE);

        gl.texImage2D(GL.TEXTURE_2D, 0,
                GL.RGBA, texW, texH, 0, GL.RGBA,
                GL.UNSIGNED_BYTE, null);

        gl.framebufferTexture2D(GL.FRAMEBUFFER,
                GL.COLOR_ATTACHMENT0,
                GL.TEXTURE_2D,
                renderTex, 0);
        GLUtils.checkGlError(getClass().getName() + ": 1");

        if (useDepthTexture) {
            renderDepth = GLUtils.glGenTextures(1)[0];
            gl.bindTexture(GL.TEXTURE_2D, renderDepth);
            GLUtils.setTextureParameter(GL.NEAREST,
                    GL.NEAREST,
                    GL.CLAMP_TO_EDGE,
                    GL.CLAMP_TO_EDGE);

            gl.texImage2D(GL.TEXTURE_2D, 0,
                    GL.DEPTH_COMPONENT,
                    texW, texH, 0,
                    GL.DEPTH_COMPONENT,
                    GL.UNSIGNED_SHORT, null);

            gl.framebufferTexture2D(GL.FRAMEBUFFER,
                    GL.DEPTH_ATTACHMENT,
                    GL.TEXTURE_2D,
                    renderDepth, 0);
        } else {
            int depthRenderbuffer = GLUtils.glGenRenderBuffers(1)[0];

            gl.bindRenderbuffer(GL.RENDERBUFFER, depthRenderbuffer);

            gl.renderbufferStorage(GL.RENDERBUFFER,
                    GL.DEPTH_COMPONENT16,
                    texW, texH);

            gl.framebufferRenderbuffer(GL.FRAMEBUFFER,
                    GL.DEPTH_ATTACHMENT,
                    GL.RENDERBUFFER,
                    depthRenderbuffer);
        }

        GLUtils.checkGlError(getClass().getName() + ": 2");

        int status = GLUtils.checkFramebufferStatus(getClass().getName());
        gl.bindFramebuffer(GL.FRAMEBUFFER, 0);
        gl.bindTexture(GL.TEXTURE_2D, 0);

        return status == GL.FRAMEBUFFER_COMPLETE;
    }

    public void enable(boolean on) {
        if (on)
            GLState.bindFramebuffer(fb);
        else
            GLState.bindFramebuffer(0);
    }

    public void begin() {
        GLState.bindFramebuffer(fb);
        gl.depthMask(true);
        gl.clear(GL.DEPTH_BUFFER_BIT);
    }

    LayerRenderer mRenderer;

    public void setRenderer(LayerRenderer renderer) {
        mRenderer = renderer;
    }

    @Override
    public boolean setup() {
        mRenderer.setup();
        return super.setup();
    }

    @Override
    public void update(GLViewport viewport) {
        if (texW != viewport.getWidth() || texH != viewport.getHeight()) {
            setupFBO(viewport);
            switch (mode) {
                case FXAA:
                    mShader = new Shader("post_fxaa");
                    break;
                case SSAO:
                    mShader = new Shader("post_ssao");
                    break;
                case SSAO_FXAA:
                    mShader = new Shader("post_combined");
                    break;
                case BYPASS:
                    mShader = new Shader("post_bypass");
                    break;
            }
        }
        mRenderer.update(viewport);
        setReady(mRenderer.isReady());
    }

    @Override
    public void render(GLViewport viewport) {
        GLState.bindFramebuffer(fb);
        GLState.viewport(texW, texH);
        gl.depthMask(true);
        GLState.setClearColor(mClearColor); // FIXME SHADOW remove to use default clear color
        gl.clear(GL.DEPTH_BUFFER_BIT | GL.COLOR_BUFFER_BIT);

        mRenderer.render(viewport);

        GLState.bindFramebuffer(0);

        mShader.useProgram();

        /* bind depth texture */
        if (useDepthTexture) {
            gl.activeTexture(GL.TEXTURE1);
            GLState.bindTex2D(renderDepth);
            gl.uniform1i(mShader.uTexDepth, 1);
            gl.activeTexture(GL.TEXTURE0);
        }
        /* bind color texture */
        GLState.bindTex2D(renderTex);
        gl.uniform1i(mShader.uTexColor, 0);

        MapRenderer.bindQuadVertexVBO(mShader.aPos);

        gl.uniform2f(mShader.uPixel,
                (float) (1.0 / texW * 0.5),
                (float) (1.0 / texH * 0.5));

        GLState.test(false, false);
        GLState.blend(true);
        // FIXME SHADOW to work with ShadowRenderer: gl.blendFunc(GL.ZERO, GL.SRC_COLOR);
        gl.drawArrays(GL.TRIANGLE_STRIP, 0, 4);
        GLUtils.checkGlError(getClass().getName() + ": render() end");
    }
}
