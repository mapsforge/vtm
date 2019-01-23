/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2019 devemux86
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
import org.oscim.backend.GLAdapter;
import org.oscim.utils.FastMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.oscim.backend.GLAdapter.gl;

/**
 * Utility functions
 */
public class GLUtils {
    static final Logger log = LoggerFactory.getLogger(GLUtils.class);

    public static void setColor(int location, int color, float alpha) {
        if (alpha >= 1)
            alpha = ((color >>> 24) & 0xff) / 255f;
        else if (alpha < 0)
            alpha = 0;
        else
            alpha *= ((color >>> 24) & 0xff) / 255f;

        if (alpha == 1) {
            gl.uniform4f(location,
                    ((color >>> 16) & 0xff) / 255f,
                    ((color >>> 8) & 0xff) / 255f,
                    ((color >>> 0) & 0xff) / 255f,
                    alpha);
        } else {
            gl.uniform4f(location,
                    ((color >>> 16) & 0xff) / 255f * alpha,
                    ((color >>> 8) & 0xff) / 255f * alpha,
                    ((color >>> 0) & 0xff) / 255f * alpha,
                    alpha);
        }
    }

    public static void setColorBlend(int location, int color1, int color2, float mix) {
        float a1 = (((color1 >>> 24) & 0xff) / 255f) * (1 - mix);
        float a2 = (((color2 >>> 24) & 0xff) / 255f) * mix;
        gl.uniform4f(location,
                ((((color1 >>> 16) & 0xff) / 255f) * a1
                        + (((color2 >>> 16) & 0xff) / 255f) * a2),
                ((((color1 >>> 8) & 0xff) / 255f) * a1
                        + (((color2 >>> 8) & 0xff) / 255f) * a2),
                ((((color1 >>> 0) & 0xff) / 255f) * a1
                        + (((color2 >>> 0) & 0xff) / 255f) * a2),
                (a1 + a2));
    }

    public static void setTextureParameter(int min_filter, int mag_filter, int wrap_s, int wrap_t) {
        gl.texParameterf(GL.TEXTURE_2D,
                GL.TEXTURE_MIN_FILTER,
                min_filter);
        gl.texParameterf(GL.TEXTURE_2D,
                GL.TEXTURE_MAG_FILTER,
                mag_filter);
        gl.texParameterf(GL.TEXTURE_2D,
                GL.TEXTURE_WRAP_S,
                wrap_s); // Set U Wrapping
        gl.texParameterf(GL.TEXTURE_2D,
                GL.TEXTURE_WRAP_T,
                wrap_t); // Set V Wrapping
    }

    public static int loadTexture(byte[] pixel, int width, int height, int format,
                                  int min_filter, int mag_filter, int wrap_s, int wrap_t) {

        int[] textureIds = GLUtils.glGenTextures(1);
        GLState.bindTex2D(textureIds[0]);

        setTextureParameter(min_filter, mag_filter, wrap_s, wrap_t);

        ByteBuffer buf = ByteBuffer.allocateDirect(width * height).order(ByteOrder.nativeOrder());
        buf.put(pixel);
        buf.position(0);
        IntBuffer intBuf = buf.asIntBuffer();
        gl.texImage2D(GL.TEXTURE_2D, 0, format, width, height, 0, format,
                GL.UNSIGNED_BYTE, intBuf);

        GLState.bindTex2D(0);

        return textureIds[0];
    }

    /**
     * Check the status of current framebuffer.
     * See: https://www.khronos.org/registry/OpenGL-Refpages/es2.0/xhtml/glCheckFramebufferStatus.xml
     *
     * @param op the operation which should be debugged
     * @return the status code
     */
    public static int checkFramebufferStatus(String op) {
        int status = gl.checkFramebufferStatus(GL.FRAMEBUFFER);
        if (status != GL.FRAMEBUFFER_COMPLETE)
            log.error(op + ": \tglFramebuffer " + getFramebufferStatusString(status) + " (" + status + ")");
        return status;
    }

    /**
     * @param status the status code of a framebuffer
     * @return the status code as string
     */
    public static String getFramebufferStatusString(int status) {
        switch (status) {
            case GL.FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
                return "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT";
            case GL.FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
                return "GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS";
            case GL.FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
                return "GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT";
            case GL.FRAMEBUFFER_UNSUPPORTED:
                return "GL_FRAMEBUFFER_UNSUPPORTED";
            case GL.FRAMEBUFFER_COMPLETE:
                return "GL_FRAMEBUFFER_COMPLETE";
            default:
                return String.valueOf(status);
        }
    }

    /**
     * Check GL error.
     * See: https://www.khronos.org/opengl/wiki/OpenGL_Error
     *
     * @param op the operation which should be debugged
     */
    public static void checkGlError(String op) {
        int error; // GL.NO_ERROR
        while ((error = gl.getError()) != GL.NO_ERROR) {
            log.error(op + ": \tglError " + getGlErrorString(error) + " (" + error + ")");
            // throw new RuntimeException(op + ": glError " + error);
        }
    }

    /**
     * Check GL error.
     * See: https://www.khronos.org/opengl/wiki/OpenGL_Error
     *
     * @param op the operation which should be debugged
     * @param findError the error to be found
     * @return true if error occurs
     */
    public static boolean checkGlError(String op, int findError) {
        int error; // GL.NO_ERROR
        boolean occur = false;
        while ((error = gl.getError()) != GL.NO_ERROR) {
            log.error(op + ": \tglError " + getGlErrorString(error) + " (" + error + ")");
            // throw new RuntimeException(op + ": glError " + error);
            if (error == findError)
                occur = true;
        }
        return occur;
    }

    /**
     * Check GL errors.
     * See: https://www.khronos.org/opengl/wiki/OpenGL_Error
     *
     * @param op the operation which should be debugged
     * @return the OpenGL error codes
     */
    public static List<Integer> checkGlErrors(String op) {
        int error; // GL.NO_ERROR
        List<Integer> errors = new ArrayList<>();
        while ((error = gl.getError()) != GL.NO_ERROR) {
            log.error(op + ": \tglError " + getGlErrorString(error) + " (" + error + ")");
            errors.add(error);
            // throw new RuntimeException(op + ": glError " + error);
        }
        return errors;
    }

    /**
     * @param error the error code of OpenGL
     * @return the error code as string
     */
    public static String getGlErrorString(int error) {
        switch (error) {
            case GL.INVALID_ENUM:
                return "GL_INVALID_ENUM";
            case GL.INVALID_VALUE:
                return "GL_INVALID_VALUE";
            case GL.INVALID_OPERATION:
                return "GL_INVALID_OPERATION";
            case 0x0503: // GL.STACK_OVERFLOW
                return "GL_STACK_OVERFLOW";
            case 0x0504: // GL.STACK_UNDERFLOW
                return "GL_STACK_UNDERFLOW";
            case GL.OUT_OF_MEMORY:
                return "GL_OUT_OF_MEMORY";
            case GL.INVALID_FRAMEBUFFER_OPERATION:
                return "GL_INVALID_FRAMEBUFFER_OPERATION";
            case 0x0507: // GL.CONTEXT_LOST (with OpenGL 4.5)
                return "GL_CONTEXT_LOST";
            case 0x8031: // GL.TABLE_TOO_LARGE (deprecated in OpenGL 3.0)
                return "GL_TABLE_TOO_LARGE";
            case GL.NO_ERROR:
                return "GL_NO_ERROR";
            default:
                return String.valueOf(error);
        }
    }

    public static void setColor(int handle, float[] c, float alpha) {
        if (alpha >= 1) {
            gl.uniform4f(handle, c[0], c[1], c[2], c[3]);
        } else {
            if (alpha < 0) {
                log.debug("setColor: " + alpha);
                alpha = 0;
                gl.uniform4f(handle, 0, 0, 0, 0);
            }

            gl.uniform4f(handle,
                    c[0] * alpha, c[1] * alpha,
                    c[2] * alpha, c[3] * alpha);
        }
    }

    public static float[] colorToFloat(int color) {
        float[] c = new float[4];
        c[3] = (color >> 24 & 0xff) / 255.0f;
        c[0] = (color >> 16 & 0xff) / 255.0f;
        c[1] = (color >> 8 & 0xff) / 255.0f;
        c[2] = (color >> 0 & 0xff) / 255.0f;
        return c;
    }

    // premultiply alpha
    public static float[] colorToFloatP(int color) {
        float[] c = new float[4];
        c[3] = (color >> 24 & 0xff) / 255.0f;
        c[0] = (color >> 16 & 0xff) / 255.0f * c[3];
        c[1] = (color >> 8 & 0xff) / 255.0f * c[3];
        c[2] = (color >> 0 & 0xff) / 255.0f * c[3];
        return c;
    }

    /**
     * public-domain function by Darel Rex Finley from
     * http://alienryderflex.com/saturation.html
     *
     * @param color  The passed-in RGB values can be on any desired scale, such as
     *               0 to 1, or 0 to 255.
     * @param change 0.0 creates a black-and-white image. 0.5 reduces the color
     *               saturation by half. 1.0 causes no change. 2.0 doubles the
     *               color saturation.
     */
    public static void changeSaturation(float color[], float change) {
        float r = color[0];
        float g = color[1];
        float b = color[2];
        double p = Math.sqrt(r * r * 0.299f + g * g * 0.587f + b * b * 0.114f);
        color[0] = FastMath.clampN((float) (p + (r - p) * change));
        color[1] = FastMath.clampN((float) (p + (g - p) * change));
        color[2] = FastMath.clampN((float) (p + (b - p) * change));
    }

    public static void glUniform4fv(int location, int count, float[] val) {
        FloatBuffer buf = MapRenderer.getFloatBuffer(count * 4);
        buf.put(val);
        buf.flip();
        gl.uniform4fv(location, count, buf);
    }

    public static int[] glGenBuffers(int num) {
        IntBuffer buf = MapRenderer.getIntBuffer(num);
        buf.position(0);
        buf.limit(num);
        gl.genBuffers(num, buf);
        int[] ret = new int[num];
        buf.position(0);
        buf.limit(num);
        buf.get(ret);
        return ret;
    }

    public static void glDeleteBuffers(int num, int[] ids) {
        IntBuffer buf = MapRenderer.getIntBuffer(num);
        buf.put(ids, 0, num);
        buf.flip();
        gl.deleteBuffers(num, buf);
    }

    public static int[] glGenTextures(int num) {
        if (num <= 0)
            return null;

        int[] ret = new int[num];
        // Workaround for texture memory leaks on desktop
        IntBuffer buf;
        if (GLAdapter.GDX_DESKTOP_QUIRKS)
            buf = ByteBuffer.allocateDirect(num * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
        else
            buf = MapRenderer.getIntBuffer(num);

        if (GLAdapter.GDX_WEBGL_QUIRKS) {
            for (int i = 0; i < num; i++) {
                gl.genTextures(num, buf);
                buf.position(0);
                ret[i] = buf.get();
                buf.position(0);
            }
        } else {
            gl.genTextures(num, buf);
            buf.position(0);
            buf.get(ret);
        }

        return ret;
    }

    public static void glDeleteTextures(int num, int[] ids) {
        IntBuffer buf = MapRenderer.getIntBuffer(num);
        buf.put(ids, 0, num);
        buf.flip();
        gl.deleteTextures(num, buf);
    }
}
