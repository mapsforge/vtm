/*
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
package org.oscim.android.gl;

import android.opengl.GLSurfaceView;

import org.oscim.android.MapView;
import org.slf4j.LoggerFactory;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * Cf.: https://github.com/libgdx/libgdx/blob/master/backends/gdx-backend-android/src/com/badlogic/gdx/backends/android/surfaceview/GLSurfaceView20.java
 */
public class GlContextFactory implements GLSurfaceView.EGLContextFactory {

    static final org.slf4j.Logger log = LoggerFactory.getLogger(GLSurfaceView.class);

    private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

    public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
        //log.debug("creating OpenGL ES " + MapView.targetGLESVersion + ".0 context");
        checkEglError("Before eglCreateContext " + MapView.targetGLESVersion, egl);
        int[] attrib_list = {EGL_CONTEXT_CLIENT_VERSION, MapView.targetGLESVersion, EGL10.EGL_NONE};
        EGLContext context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
        boolean success = checkEglError("After eglCreateContext " + MapView.targetGLESVersion, egl);

        if ((!success || context == null) && MapView.targetGLESVersion > 2) {
            //log.warn("Falling back to GLES 2");
            MapView.targetGLESVersion = 2;
            return createContext(egl, display, eglConfig);
        }
        //log.debug("Returning a GLES " + MapView.targetGLESVersion + " context");
        return context;
    }

    public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
        egl.eglDestroyContext(display, context);
    }

    private static boolean checkEglError(String prompt, EGL10 egl) {
        int error;
        boolean result = true;
        while ((error = egl.eglGetError()) != EGL10.EGL_SUCCESS) {
            result = false;
            //log.warn(String.format("%s: EGL error: 0x%x", prompt, error));
        }
        return result;
    }
}
