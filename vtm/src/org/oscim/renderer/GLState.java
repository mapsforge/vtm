/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 devemux86
 * Copyright 2018 Longri
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
import org.oscim.event.Event;
import org.oscim.event.EventDispatcher;
import org.oscim.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import static org.oscim.backend.GLAdapter.gl;

public class GLState {
    static final Logger log = LoggerFactory.getLogger(GLState.class);

    private final static boolean[] vertexArray = {false, false};
    private static boolean blend = false;
    private static boolean depth = false;
    private static boolean stencil = false;
    private static int shader;
    private static float[] clearColor;
    private static int glVertexBuffer;
    private static int glIndexBuffer;
    private static int currentTexId;
    private static int MAX_TEXTURE_SIZE;
    public static final Event GLStateInitEVENT = new Event();
    private static boolean init = false;

    public static final EventDispatcher<Listener, Object> event =
            new EventDispatcher<Listener, Object>() {
                @Override
                public void tell(Listener listener, Event event, Object data) {
                    listener.onGLStateInitEvent(event);
                }
            };

    public interface Listener extends EventListener {
        void onGLStateInitEvent(Event event);
    }

    static void init() {
        vertexArray[0] = false;
        vertexArray[1] = false;
        blend = false;
        depth = false;
        stencil = false;
        shader = -1;
        currentTexId = -1;
        glVertexBuffer = -1;
        glIndexBuffer = -1;
        clearColor = null;

        gl.disable(GL.STENCIL_TEST);
        gl.disable(GL.DEPTH_TEST);
        gl.disable(GL.BLEND);

        if (MAX_TEXTURE_SIZE <= 0) {
            IntBuffer max = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
            gl.getIntegerv(GL.MAX_TEXTURE_SIZE, max);
            MAX_TEXTURE_SIZE = max.get(0);
            if (MAX_TEXTURE_SIZE > 4096) MAX_TEXTURE_SIZE = 4096;
            init = true;
            event.fire(GLStateInitEVENT, null);
        }

    }

    public static int getDeviceMaxGlTextureSize() {
        if (!init)
            throw new RuntimeException("Can't get max TextureSize before GL_SurfaceView is created\n" +
                    "You can catch the GLState init event like:\n" +
                    "GLState.event.bind(new GLState.Listener() {\n" +
                    "            @Override\n" +
                    "            public void onGLStateInitEvent(Event event) {\n" +
                    "                // load Theme with TextureAtlas\n" +
                    "                ...                            \n" +
                    "            }\n" +
                    "        });");
        return MAX_TEXTURE_SIZE;
    }

    public static boolean useProgram(int shaderProgram) {
        if (shaderProgram < 0) {
            shader = -1;
        } else if (shaderProgram != shader) {
            gl.useProgram(shaderProgram);
            shader = shaderProgram;
            return true;
        }
        return false;
    }

    public static void blend(boolean enable) {
        if (blend == enable)
            return;

        if (enable)
            gl.enable(GL.BLEND);
        else
            gl.disable(GL.BLEND);
        blend = enable;
    }

    public static void testDepth(boolean enable) {
        if (depth != enable) {

            if (enable)
                gl.enable(GL.DEPTH_TEST);
            else
                gl.disable(GL.DEPTH_TEST);

            depth = enable;
        }
    }

    public static void test(boolean depthTest, boolean stencilTest) {
        if (depth != depthTest) {

            if (depthTest)
                gl.enable(GL.DEPTH_TEST);
            else
                gl.disable(GL.DEPTH_TEST);

            depth = depthTest;
        }

        if (stencil != stencilTest) {

            if (stencilTest)
                gl.enable(GL.STENCIL_TEST);
            else
                gl.disable(GL.STENCIL_TEST);

            stencil = stencilTest;
        }
    }

    public static void enableVertexArrays(int va1, int va2) {
        if (va1 > 1 || va2 > 1)
            log.debug("FIXME: enableVertexArrays...");

        if ((va1 == 0 || va2 == 0)) {
            if (!vertexArray[0]) {
                gl.enableVertexAttribArray(0);
                vertexArray[0] = true;
            }
        } else {
            if (vertexArray[0]) {
                gl.disableVertexAttribArray(0);
                vertexArray[0] = false;
            }
        }

        if ((va1 == 1 || va2 == 1)) {
            if (!vertexArray[1]) {
                gl.enableVertexAttribArray(1);
                vertexArray[1] = true;
            }
        } else {
            if (vertexArray[1]) {
                gl.disableVertexAttribArray(1);
                vertexArray[1] = false;
            }
        }
    }

    public static void bindTex2D(int id) {
        if (id < 0) {
            gl.bindTexture(GL.TEXTURE_2D, 0);
            currentTexId = 0;
        } else if (currentTexId != id) {
            gl.bindTexture(GL.TEXTURE_2D, id);
            currentTexId = id;
        }
    }

    public static void setClearColor(float[] color) {
        // Workaround for artifacts at canvas resize on desktop
        if (!GLAdapter.GDX_DESKTOP_QUIRKS) {
            if (clearColor != null &&
                    color[0] == clearColor[0] &&
                    color[1] == clearColor[1] &&
                    color[2] == clearColor[2] &&
                    color[3] == clearColor[3])
                return;
        }

        clearColor = color;
        gl.clearColor(color[0], color[1], color[2], color[3]);
    }

    public static void bindBuffer(int target, int id) {
        //log.debug(">> buffer {} {}", target == GL20.ARRAY_BUFFER, id);

        if (target == GL.ARRAY_BUFFER) {
            if (glVertexBuffer == id)
                return;
            glVertexBuffer = id;
        } else if (target == GL.ELEMENT_ARRAY_BUFFER) {
            if (glIndexBuffer == id)
                return;
            glIndexBuffer = id;
        } else {
            log.debug("invalid target {}", target);
            return;
        }
        //log.debug("bind buffer {} {}", target == GL20.ARRAY_BUFFER, id);

        if (id >= 0)
            gl.bindBuffer(target, id);
    }

    public static void bindElementBuffer(int id) {

        if (glIndexBuffer == id)
            return;
        glIndexBuffer = id;

        if (id >= 0)
            gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, id);

    }

    public static void bindVertexBuffer(int id) {

        if (glVertexBuffer == id)
            return;
        glVertexBuffer = id;

        if (id >= 0)
            gl.bindBuffer(GL.ARRAY_BUFFER, id);

    }
}
