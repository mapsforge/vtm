/*
 * Copyright 2019 Gustl22
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
package org.oscim.renderer.light;

import org.oscim.backend.GL;
import org.oscim.backend.GLAdapter;
import org.oscim.renderer.GLState;
import org.oscim.renderer.GLUtils;

import static org.oscim.backend.GLAdapter.gl;
import static org.oscim.backend.GLAdapter.gl30;

/**
 * The frame buffer for the shadow pass. This class sets up the depth texture
 * which can be rendered to during the shadow render pass, producing a shadow
 * map.
 * <p>
 * See ThinMatrix on Youtube: https://youtu.be/o6zDfDkOFIc
 *
 * @author Karl
 */
public class ShadowFrameBuffer {

    private final int WIDTH;
    private final int HEIGHT;
    private int defaultWidth;
    private int defaultHeight;
    private int defaultFrameBuffer;
    private int defaultTexture;
    private int fbo;
    private int shadowMap;

    /**
     * Initialises the frame buffer and shadow map of a certain size.
     *
     * @param width  - the width of the shadow map in pixels.
     * @param height - the height of the shadow map in pixels.
     */
    protected ShadowFrameBuffer(int width, int height) {
        this.WIDTH = width;
        this.HEIGHT = height;

        updateViewportDimensions();

        fbo = createFrameBuffer();
        shadowMap = createDepthBufferAttachment(width, height);
        unbindFrameBuffer();
    }

    /**
     * Deletes the frame buffer and shadow map texture when the game closes.
     */
    protected void cleanUp() {
        GLUtils.glDeleteFrameBuffers(1, new int[]{fbo});
        GLUtils.glDeleteTextures(1, new int[]{shadowMap});
    }

    /**
     * Unbinds the frame buffer, setting the default frame buffer as the current
     * render target.
     */
    protected void unbindFrameBuffer() {
        GLState.bindFramebuffer(defaultFrameBuffer);
        GLState.viewport(defaultWidth, defaultHeight);
    }

    /**
     * @return The ID of the shadow map texture.
     */
    protected int getShadowMap() {
        return shadowMap;
    }

    /**
     * Binds the frame buffer as the current render target.
     */
    public void bindFrameBuffer() {
        updateViewportDimensions();
        GLState.bindTex2D(defaultTexture);

        defaultFrameBuffer = GLState.getFramebuffer();
        GLState.bindFramebuffer(fbo);
        GLState.viewport(WIDTH, HEIGHT);
    }

    /**
     * Creates a frame buffer and binds it so that attachments can be added to
     * it. The draw buffer is set to none, indicating that there's no colour
     * buffer to be rendered to.
     *
     * @return The newly created frame buffer's ID.
     */
    private static int createFrameBuffer() {
        int frameBuffer = GLUtils.glGenFrameBuffers(1)[0];
        GLState.bindFramebuffer(frameBuffer);
        if (GLAdapter.isGL30()) {
            GLUtils.glDrawBuffers(1, new int[]{GL.NONE});
            gl30.readBuffer(GL.NONE);
        }
        return frameBuffer;
    }

    /**
     * Creates a depth buffer texture attachment.
     *
     * @param width  - the width of the texture.
     * @param height - the height of the texture.
     * @return The ID of the depth texture.
     */
    private int createDepthBufferAttachment(int width, int height) {
        defaultTexture = GLState.getTexture();
        int[] texture = GLUtils.glGenTextures(1);
        GLState.bindTex2D(texture[0]);
        if (GLAdapter.isGL30()) {
            gl.texImage2D(gl.TEXTURE_2D, 0, gl30.DEPTH_COMPONENT16,
                    width, height, 0, gl30.DEPTH_COMPONENT,
                    gl.UNSIGNED_SHORT, null);
        } else {
            gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA,
                    width, height, 0, gl.RGBA,
                    gl.UNSIGNED_BYTE, null);
        }
        // Alternatively set to 32 bit float texture
//        gl.texImage2D(gl.TEXTURE_2D, 0, gl30.DEPTH_COMPONENT32F, width, height, 0,
//                gl.DEPTH_COMPONENT, gl.FLOAT, null);
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.NEAREST);
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.NEAREST);
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, GL.CLAMP_TO_EDGE);
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, GL.CLAMP_TO_EDGE);
        if (GLAdapter.isGL30()) {
            gl.framebufferTexture2D(GL.FRAMEBUFFER, GL.DEPTH_ATTACHMENT, GL.TEXTURE_2D, texture[0], 0);
        } else {
            gl.framebufferTexture2D(GL.FRAMEBUFFER, GL.COLOR_ATTACHMENT0, GL.TEXTURE_2D, texture[0], 0);
        }
        return texture[0];
    }

    private void updateViewportDimensions() {
        defaultWidth = GLState.getViewportWidth();
        defaultHeight = GLState.getViewportHeight();
    }
}