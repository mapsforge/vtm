/*
 * Copyright 2012 Hannes Janetzek
 * Copyright 2016 devemux86
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
package org.oscim.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;

import org.oscim.android.canvas.AndroidGraphics;
import org.oscim.android.gl.AndroidGL;
import org.oscim.android.gl.GlConfigChooser;
import org.oscim.android.input.AndroidMotionEvent;
import org.oscim.android.input.GestureHandler;
import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.GLAdapter;
import org.oscim.map.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * The MapView,
 * <p/>
 * add it your App, have a map!
 * <p/>
 * Dont forget to call onPause / onResume!
 */
public class MapView extends GLSurfaceView {

    static final Logger log = LoggerFactory.getLogger(MapView.class);

    static {
        System.loadLibrary("vtm-jni");
    }

    protected final AndroidMap mMap;
    protected GestureDetector mGestureDetector;
    protected final AndroidMotionEvent mMotionEvent;

    public MapView(Context context) {
        this(context, null);
    }

    public MapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        /* Not sure if this makes sense */
        this.setWillNotDraw(true);
        this.setClickable(true);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);

        /* Setup android backedn */
        AndroidGraphics.init();
        AndroidAssets.init(context);
        GLAdapter.init(new AndroidGL());

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        CanvasAdapter.dpi = (int) Math.max(metrics.xdpi, metrics.ydpi);

        /* Initialize the Map */
        mMap = new AndroidMap(this);

        /* Initialize Renderer */
        setEGLConfigChooser(new GlConfigChooser());
        setEGLContextClientVersion(2);

        if (GLAdapter.debug)
            setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR
                    | GLSurfaceView.DEBUG_LOG_GL_CALLS);

        setRenderer(new GLRenderer(mMap));
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mMap.clearMap();
        mMap.updateMap(false);

        if (!Map.NEW_GESTURES) {
            GestureHandler gestureHandler = new GestureHandler(mMap);
            mGestureDetector = new GestureDetector(context, gestureHandler);
            mGestureDetector.setOnDoubleTapListener(gestureHandler);
        }

        mMotionEvent = new AndroidMotionEvent();
    }

    public void onStop() {

    }

    public void onPause() {
        mMap.pause(true);
    }

    public void onResume() {
        mMap.pause(false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(android.view.MotionEvent motionEvent) {
        if (!isClickable())
            return false;

        if (mGestureDetector != null) {
            if (mGestureDetector.onTouchEvent(motionEvent))
                return true;
        }

        mMap.input.fire(null, mMotionEvent.wrap(motionEvent));
        mMotionEvent.recycle();
        return true;
    }

    @Override
    protected void onSizeChanged(int width, int height,
                                 int oldWidth, int oldHeight) {

        super.onSizeChanged(width, height, oldWidth, oldHeight);

        if (width > 0 && height > 0)
            mMap.viewport().setScreenSize(width, height);
    }

    public Map map() {
        return mMap;
    }

    static class AndroidMap extends Map {

        private final MapView mMapView;

        private boolean mRenderRequest;
        private boolean mRenderWait;
        private boolean mPausing;

        public AndroidMap(MapView mapView) {
            super();
            mMapView = mapView;
        }

        @Override
        public int getWidth() {
            return mMapView.getWidth();
        }

        @Override
        public int getHeight() {
            return mMapView.getHeight();
        }

        private final Runnable mRedrawCb = new Runnable() {
            @Override
            public void run() {
                prepareFrame();
                mMapView.requestRender();
            }
        };

        @Override
        public void updateMap(boolean redraw) {
            synchronized (mRedrawCb) {
                if (mPausing)
                    return;

                if (!mRenderRequest) {
                    mRenderRequest = true;
                    mMapView.post(mRedrawCb);
                } else {
                    mRenderWait = true;
                }
            }
        }

        @Override
        public void render() {
            if (mPausing)
                return;

            /** TODO should not need to call prepareFrame in mRedrawCb */
            updateMap(false);
        }

        @Override
        public void beginFrame() {
        }

        @Override
        public void doneFrame(boolean animate) {
            synchronized (mRedrawCb) {
                mRenderRequest = false;
                if (animate || mRenderWait) {
                    mRenderWait = false;
                    render();
                }
            }
        }

        @Override
        public boolean post(Runnable runnable) {
            return mMapView.post(runnable);
        }

        @Override
        public boolean postDelayed(Runnable action, long delay) {
            return mMapView.postDelayed(action, delay);
        }

        public void pause(boolean pause) {
            log.debug("pause... {}", pause);
            mPausing = pause;
        }
    }

    static class GLRenderer extends org.oscim.renderer.MapRenderer
            implements GLSurfaceView.Renderer {

        public GLRenderer(Map map) {
            super(map);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            super.onSurfaceCreated();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            super.onSurfaceChanged(width, height);

        }

        @Override
        public void onDrawFrame(GL10 gl) {
            super.onDrawFrame();
        }
    }
}
