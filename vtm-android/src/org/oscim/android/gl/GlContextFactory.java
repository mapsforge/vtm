/*
 * Copyright 2019 Gustl22
 * Copyright 2019 devemux86
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
/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.oscim.android.gl;

import android.opengl.GLSurfaceView;
import org.oscim.android.MapView;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import java.util.logging.Logger;

/**
 * https://developer.android.com/guide/topics/graphics/opengl.html#version-check
 * <p>
 * https://github.com/libgdx/libgdx/blob/master/backends/gdx-backend-android/src/com/badlogic/gdx/backends/android/surfaceview/GLSurfaceView20.java
 */
public class GlContextFactory implements GLSurfaceView.EGLContextFactory {

    private static final Logger log = Logger.getLogger(GlContextFactory.class.getName());

    private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

    @Override
    public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
        log.info("creating OpenGL ES " + MapView.OPENGL_VERSION + " context");
        checkEglError("Before eglCreateContext " + MapView.OPENGL_VERSION, egl);
        int[] attrib_list = {EGL_CONTEXT_CLIENT_VERSION, (int) MapView.OPENGL_VERSION, EGL10.EGL_NONE};
        EGLContext context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
        boolean success = checkEglError("After eglCreateContext " + MapView.OPENGL_VERSION, egl);

        if ((!success || context == null) && MapView.OPENGL_VERSION > 2) {
            log.warning("Falling back to GLES 2");
            MapView.OPENGL_VERSION = 2.0;
            return createContext(egl, display, eglConfig);
        }
        log.info("Returning a GLES " + MapView.OPENGL_VERSION + " context");
        return context;
    }

    @Override
    public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
        egl.eglDestroyContext(display, context);
    }

    private static boolean checkEglError(String prompt, EGL10 egl) {
        int error;
        boolean result = true;
        while ((error = egl.eglGetError()) != EGL10.EGL_SUCCESS) {
            result = false;
            log.severe(String.format("%s: EGL error: 0x%x", prompt, error));
        }
        return result;
    }
}
