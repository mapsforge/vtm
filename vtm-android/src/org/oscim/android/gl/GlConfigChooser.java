package org.oscim.android.gl;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

public class GlConfigChooser implements GLSurfaceView.EGLConfigChooser {

    @Override
    public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
        int[] val = new int[1];

        // Try to find a normal multisample configuration first.
        int[] configSpec = {
                EGL10.EGL_RED_SIZE, 5,
                EGL10.EGL_GREEN_SIZE, 6,
                EGL10.EGL_BLUE_SIZE, 5,
                EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_DEPTH_SIZE, 16,
                // Requires that setEGLContextClientVersion(2) is called on the view.
                EGL10.EGL_RENDERABLE_TYPE, 4 /*EGL14.EGL_OPENGL_ES2_BIT*/ /*0x40 /*EGLExt.EGL_OPENGL_ES3_BIT_KHR*/,
                EGL10.EGL_STENCIL_SIZE, 8,
                EGL10.EGL_NONE};

        if (!egl.eglChooseConfig(display, configSpec, null, 0, val)) {
            throw new IllegalArgumentException("eglChooseConfig failed");
        }
        int numConfigs = val[0];

        if (numConfigs <= 0) {

            configSpec = new int[]{
                    // EGL10.EGL_RENDERABLE_TYPE, 4, EGL10.EGL_NONE };
                    EGL10.EGL_RED_SIZE, 8,
                    EGL10.EGL_GREEN_SIZE, 8,
                    EGL10.EGL_BLUE_SIZE, 8,
                    EGL10.EGL_ALPHA_SIZE, 8,
                    EGL10.EGL_DEPTH_SIZE, 16,
                    EGL10.EGL_RENDERABLE_TYPE, 4 /*EGL14.EGL_OPENGL_ES2_BIT*/ /*0x40 /*EGLExt.EGL_OPENGL_ES3_BIT_KHR*/,
                    EGL10.EGL_STENCIL_SIZE, 8,
                    EGL10.EGL_NONE};

            if (!egl.eglChooseConfig(display, configSpec, null, 0, val)) {
                throw new IllegalArgumentException("eglChooseConfig failed");
            }
            numConfigs = val[0];

            if (numConfigs <= 0) {
                throw new IllegalArgumentException("No configs match configSpec");
            }
        }

        // Get all matching configurations.
        EGLConfig[] configs = new EGLConfig[numConfigs];
        if (!egl.eglChooseConfig(display, configSpec, configs, numConfigs, val)) {
            throw new IllegalArgumentException("data eglChooseConfig failed");
        }

        // CAUTION! eglChooseConfigs returns configs with higher bit depth
        // first: Even though we asked for rgb565 configurations, rgb888
        // configurations are considered to be "better" and returned first.
        // You need to explicitly filter the data returned by eglChooseConfig!

        EGLConfig config = configs.length > 0 ? configs[0] : null;
        if (config == null) {
            throw new IllegalArgumentException("No config chosen");
        }
        return config;
    }
}
