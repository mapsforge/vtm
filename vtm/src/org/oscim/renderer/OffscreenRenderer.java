package org.oscim.renderer;

import org.oscim.backend.GL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.IntBuffer;

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
        IntBuffer buf = MapRenderer.getIntBuffer(1);

        texW = (int) viewport.getWidth();
        texH = (int) viewport.getHeight();

        gl.genFramebuffers(1, buf);
        fb = buf.get(0);

        buf.clear();
        gl.genTextures(1, buf);
        renderTex = buf.get(0);

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
            buf.clear();
            gl.genTextures(1, buf);
            renderDepth = buf.get(0);
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
            buf.clear();
            gl.genRenderbuffers(1, buf);
            int depthRenderbuffer = buf.get(0);

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

        int status = GLUtils.checkFrameBuffer(getClass().getName());
        gl.bindFramebuffer(GL.FRAMEBUFFER, 0);
        gl.bindTexture(GL.TEXTURE_2D, 0);

        return status == GL.FRAMEBUFFER_COMPLETE;
    }

    public void enable(boolean on) {
        if (on)
            gl.bindFramebuffer(GL.FRAMEBUFFER, fb);
        else
            gl.bindFramebuffer(GL.FRAMEBUFFER, 0);
    }

    public void begin() {
        gl.bindFramebuffer(GL.FRAMEBUFFER, fb);
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
        gl.bindFramebuffer(GL.FRAMEBUFFER, fb);
        gl.viewport(0, 0, texW, texH);
        gl.depthMask(true);
        GLState.setClearColor(mClearColor);
        gl.clear(GL.DEPTH_BUFFER_BIT | GL.COLOR_BUFFER_BIT);

        mRenderer.render(viewport);

        gl.bindFramebuffer(GL.FRAMEBUFFER, 0);

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
        gl.drawArrays(GL.TRIANGLE_STRIP, 0, 4);
        GLUtils.checkGlError(getClass().getName() + ": render() end");
    }
}
