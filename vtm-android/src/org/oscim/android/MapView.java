/*
 * Copyright 2012 Hannes Janetzek
 * Copyright 2016-2020 devemux86
 * Copyright 2018-2019 Gustl22
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
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.WindowManager;
import org.oscim.android.canvas.AndroidGraphics;
import org.oscim.android.gl.AndroidGL;
import org.oscim.android.gl.AndroidGL30;
import org.oscim.android.gl.GlConfigChooser;
import org.oscim.android.gl.GlContextFactory;
import org.oscim.android.input.AndroidMotionEvent;
import org.oscim.android.input.GestureHandler;
import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.DateTime;
import org.oscim.backend.DateTimeAdapter;
import org.oscim.backend.GLAdapter;
import org.oscim.core.Tile;
import org.oscim.map.Map;
import org.oscim.renderer.MapRenderer;
import org.oscim.utils.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The MapView,
 * <p/>
 * add it your App, have a map!
 * <p/>
 * Don't forget to call onPause / onResume!
 */
public class MapView extends GLSurfaceView {

    static final Logger log = LoggerFactory.getLogger(MapView.class);

    private static final Pattern GL_PATTERN = Pattern.compile("OpenGL ES (\\d(\\.\\d){0,2})");

    /**
     * OpenGL ES 2.0 default on Android for performance / stability.
     * Any larger not available versions fall back to OpenGL ES 2.0.
     */
    public static double OPENGL_VERSION = 2.0;

    private static void init() {
        if (Parameters.THREADED_INIT)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.loadLibrary("vtm-jni");
                }
            }).start();
        else
            System.loadLibrary("vtm-jni");
    }

    protected AndroidMap mMap;
    protected GestureDetector mGestureDetector;
    protected AndroidMotionEvent mMotionEvent;

    private final Point mScreenSize = new Point();

    public MapView(Context context) {
        this(context, null);
    }

    @SuppressWarnings("deprecation")
    public MapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        if (isInEditMode())
            return;

        init();

        /* Not sure if this makes sense */
        this.setWillNotDraw(true);
        this.setClickable(true);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);

        /* Setup android backend */
        AndroidGraphics.init();
        AndroidAssets.init(context);
        DateTimeAdapter.init(new DateTime());

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        CanvasAdapter.dpi = (int) (metrics.density * CanvasAdapter.DEFAULT_DPI);
        if (!Parameters.CUSTOM_TILE_SIZE)
            Tile.SIZE = Tile.calculateTileSize();

        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
            display.getSize(mScreenSize);
        else {
            mScreenSize.x = display.getWidth();
            mScreenSize.y = display.getHeight();
        }

        if (!Parameters.CUSTOM_COORD_SCALE) {
            if (Math.min(mScreenSize.x, mScreenSize.y) > 1080)
                MapRenderer.COORD_SCALE = 4.0f;
        }

        /* Initialize the Map */
        mMap = new AndroidMap(this);

        /* Initialize Renderer */
        if (OPENGL_VERSION == 2.0)
            setEGLContextClientVersion(2);
        else {
            // OpenGL ES 3.0 is supported with Android 4.3 (API level 18) and higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                try {
                    setEGLContextFactory(new GlContextFactory());
                } catch (Throwable t) {
                    log.error("Falling back to GLES 2", t);
                    setEGLContextClientVersion(2);
                }
            } else
                setEGLContextClientVersion(2);
        }
        setEGLConfigChooser(new GlConfigChooser());

        if (GLAdapter.debug)
            setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR
                    | GLSurfaceView.DEBUG_LOG_GL_CALLS);

        setRenderer(new GLRenderer(mMap));
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mMap.clearMap();
        mMap.updateMap(false);

        if (!Parameters.MAP_EVENT_LAYER2) {
            GestureHandler gestureHandler = new GestureHandler(mMap);
            mGestureDetector = new GestureDetector(context, gestureHandler);
            mGestureDetector.setOnDoubleTapListener(gestureHandler);
        }

        mMotionEvent = new AndroidMotionEvent();
    }

    public void onDestroy() {
        mMap.destroy();
    }

    @Override
    public void onPause() {
        mMap.pause(true);
    }

    @Override
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

        if (!isInEditMode()) {
            if (width > 0 && height > 0)
                mMap.viewport().setViewSize(width, height);
        }
    }

    public Map map() {
        return mMap;
    }

    static class AndroidMap extends Map {

        private final MapView mMapView;
        private final WindowManager mWindowManager;

        private boolean mRenderRequest;
        private boolean mRenderWait;
        private boolean mPausing;

        public AndroidMap(MapView mapView) {
            super();
            mMapView = mapView;
            mWindowManager = (WindowManager) mMapView.getContext().getSystemService(Context.WINDOW_SERVICE);
        }

        @Override
        public int getWidth() {
            return mMapView.getWidth();
        }

        @Override
        public int getHeight() {
            return mMapView.getHeight();
        }

        @Override
        public int getScreenWidth() {
            return mMapView.mScreenSize.x;
        }

        @Override
        public int getScreenHeight() {
            return mMapView.mScreenSize.y;
        }

        private final Runnable mRedrawCb = new Runnable() {
            @Override
            public void run() {
                prepareFrame();
                mMapView.requestRender();
            }
        };

        @Override
        public void updateMap() {
            updateMap(true);
        }

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

            /* TODO should not need to call prepareFrame in mRedrawCb */
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

        /**
         * @return GL version as [major, minor, release]
         */
        private int[] extractVersion(String versionString) {
            int[] version = new int[3];
            Matcher matcher = GL_PATTERN.matcher(versionString);
            if (matcher.find()) {
                String[] split = matcher.group(1).split("\\.");
                version[0] = parseInt(split[0], 2);
                version[1] = split.length < 2 ? 0 : parseInt(split[1], 0);
                version[2] = split.length < 3 ? 0 : parseInt(split[2], 0);
            } else {
                log.error("Invalid version string: " + versionString);
                version[0] = 2;
                version[1] = 0;
                version[2] = 0;
            }
            return version;
        }

        /**
         * Forgiving parsing of GL major, minor and release versions as some manufacturers don't adhere to spec.
         **/
        private int parseInt(String value, int defaultValue) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                log.error("Error parsing number: " + value + ", assuming: " + defaultValue);
                return defaultValue;
            }
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            if (OPENGL_VERSION == 2.0)
                GLAdapter.init(new AndroidGL());
            else {
                try {
                    // Create a minimum supported OpenGL ES context, then check:
                    String versionString = gl.glGetString(GL10.GL_VERSION);
                    log.info("Version: " + versionString);
                    // The version format is displayed as: "OpenGL ES <major>.<minor>"
                    // followed by optional content provided by the implementation.

                    // OpenGL<space>ES<space><version number><space><vendor-specific information>.
                    int[] version = extractVersion(versionString);
                    int majorVersion = Math.min(version[0], (int) OPENGL_VERSION);
                    if (majorVersion >= 3)
                        GLAdapter.init(new AndroidGL30());
                    else
                        GLAdapter.init(new AndroidGL());
                } catch (Throwable t) {
                    log.error("Falling back to GLES 2", t);
                    GLAdapter.init(new AndroidGL());
                }
            }

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
