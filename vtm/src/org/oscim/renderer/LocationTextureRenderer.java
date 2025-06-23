/*
 * Copyright 2013 Ahmad Saleem
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2019 devemux86
 * Copyright 2016 ocsike
 * Copyright 2017 Mathieu De Brito
 * Copyright 2017-2018 Longri
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

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.GL;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.Box;
import org.oscim.core.Point;
import org.oscim.core.Tile;
import org.oscim.layers.Layer;
import org.oscim.map.Map;
import org.oscim.renderer.bucket.SymbolBucket;
import org.oscim.renderer.bucket.SymbolItem;
import org.oscim.utils.FastMath;
import org.oscim.utils.math.Interpolation;

import static org.oscim.backend.GLAdapter.gl;

public class LocationTextureRenderer extends BucketRenderer {

    private static final long ANIM_RATE = 50;
    private static final long INTERVAL = 2000;

    public static float ACCURACY_CIRCLE_SIZE = 0;
    public static float CIRCLE_SIZE = /*30*/0;
    private static final int COLOR = 0xff3333cc;
    private static final int SHOW_ACCURACY_ZOOM = 16;

    private final Map mMap;
    private final Layer mLayer;
    protected final float mScale;
    private final SymbolBucket mSymbolLayer;

    private String mShaderFile;
    protected int mShaderProgram;
    private int hVertexPosition;
    private int hMatrixPosition;
    private int hScale;
    private int hPhase;
    private int uColor;
    private int uMode;

    private final Point mIndicatorPosition = new Point();

    private final Point mScreenPoint = new Point();
    private final Box mBBox = new Box();

    private boolean mLocationIsVisible;

    private boolean mRunAnim;
    private boolean mAnimate = true;
    private long mAnimStart;
    private boolean mCenter;

    private boolean mBillboard;
    private Bitmap mBitmapArrow, mBitmapMarker;
    private LocationCallback mCallback;
    private int mColor = COLOR;
    private final Point mLocation = new Point(Double.NaN, Double.NaN);
    private double mRadius;
    private int mShowAccuracyZoom = SHOW_ACCURACY_ZOOM;

    public LocationTextureRenderer(Map map, Layer layer) {
        this(map, layer, CanvasAdapter.getScale());
    }

    public LocationTextureRenderer(Map map, Layer layer, float scale) {
        mMap = map;
        mLayer = layer;
        mScale = scale;

        mSymbolLayer = new SymbolBucket();
    }

    public void setAnimate(boolean animate) {
        mAnimate = animate;
    }

    public void setBillboard(boolean billboard) {
        mBillboard = billboard;
    }

    public void setBitmapArrow(Bitmap bitmap) {
        mBitmapArrow = bitmap;
    }

    public void setBitmapMarker(Bitmap bitmap) {
        mBitmapMarker = bitmap;
    }

    public void setCallback(LocationCallback callback) {
        mCallback = callback;
    }

    public void setCenter(boolean center) {
        mCenter = center;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public void setLocation(double x, double y, double radius) {
        mLocation.x = x;
        mLocation.y = y;
        mRadius = radius;
    }

    public void setShader(String shaderFile) {
        mShaderFile = shaderFile;
        mInitialized = false;
    }

    public void setShowAccuracyZoom(int showAccuracyZoom) {
        mShowAccuracyZoom = showAccuracyZoom;
    }

    public void animate(boolean enable) {
        if (mRunAnim == enable)
            return;

        mRunAnim = enable;
        if (!enable)
            return;
        if (!mAnimate)
            return;

        final Runnable action = new Runnable() {
            private long lastRun;

            @Override
            public void run() {
                if (!mRunAnim)
                    return;
                if (!mAnimate)
                    return;

                long diff = System.currentTimeMillis() - lastRun;
                mMap.postDelayed(this, Math.min(ANIM_RATE, diff));
                mMap.render();
                lastRun = System.currentTimeMillis();
            }
        };

        mAnimStart = System.currentTimeMillis();
        mMap.postDelayed(action, ANIM_RATE);
    }

    private float animPhase() {
        return (float) ((MapRenderer.frametime - mAnimStart) % INTERVAL) / INTERVAL;
    }

    @Override
    public synchronized void update(GLViewport v) {

        if (!mInitialized) {
            init();
            mInitialized = true;
        }

        if (!mLayer.isEnabled()) {
            setReady(false);
            return;
        }

        /*if (!v.changed() && isReady())
            return;*/

        setReady(true);

        int width = mMap.getWidth();
        int height = mMap.getHeight();

        double x, y;
        if (mCenter) {
            x = (width >> 1) + width * mMap.viewport().getMapViewCenterX();
            y = (height >> 1) + height * mMap.viewport().getMapViewCenterY();
        } else {
            // clamp location to a position that can be
            // safely translated to screen coordinates
            v.getBBox(mBBox, 0);

            x = mLocation.x;
            y = mLocation.y;

            if (!mBBox.contains(mLocation)) {
                x = FastMath.clamp(x, mBBox.xmin, mBBox.xmax);
                y = FastMath.clamp(y, mBBox.ymin, mBBox.ymax);
            }

            // get position of Location in pixel relative to
            // screen center
            v.toScreenPoint(x, y, mScreenPoint);

            x = mScreenPoint.x + (width >> 1);
            y = mScreenPoint.y + (height >> 1);
        }

        // clip position to screen boundaries
        int visible = 0;

        if (x > width - 5)
            x = width;
        else if (x < 5)
            x = 0;
        else
            visible++;

        if (y > height - 5)
            y = height;
        else if (y < 5)
            y = 0;
        else
            visible++;

        mLocationIsVisible = (visible == 2);

        // set location indicator position
        v.fromScreenPoint(x, y, mIndicatorPosition);

        /* Texture */

        buckets.clear();

        if (mBitmapArrow == null || mBitmapMarker == null)
            return;

        if (!mLocationIsVisible)
            return;

        float itx, ity;
        if (mCenter) {
            itx = 0;
            ity = 0;
        } else {
            double mx = v.pos.x;
            double my = v.pos.y;
            double scale = Tile.SIZE * v.pos.scale;

            long flip = (long) (Tile.SIZE * v.pos.scale) >> 1;

            itx = (float) ((mLocation.x - mx) * scale);
            ity = (float) ((mLocation.y - my) * scale);

            if (itx > flip)
                itx -= (flip << 1);
            else if (itx < -flip)
                itx += (flip << 1);
        }

        mMapPosition.copy(v.pos);
        mMapPosition.bearing = -mMapPosition.bearing;

        SymbolItem s = SymbolItem.pool.get();
        if (mCallback != null && mCallback.hasRotation())
            s.set(itx, ity, mBitmapArrow, mCallback.getRotation(), mBillboard);
        else
            s.set(itx, ity, mBitmapMarker, mBillboard);
        mSymbolLayer.pushSymbol(s);

        buckets.set(mSymbolLayer);
        buckets.prepare();

        compile();
    }

    @Override
    public synchronized void render(GLViewport v) {

        GLState.useProgram(mShaderProgram);
        GLState.blend(true);
        GLState.test(false, false);

        GLState.enableVertexArrays(hVertexPosition, GLState.DISABLED);
        MapRenderer.bindQuadVertexVBO(hVertexPosition/*, true*/);

        float radius;
        boolean viewShed = false;
        if (!mLocationIsVisible /* || pos.zoomLevel < SHOW_ACCURACY_ZOOM */) {
            radius = ACCURACY_CIRCLE_SIZE * mScale;
            animate(true);
        } else {
            radius = CIRCLE_SIZE * mScale;
            if (v.pos.zoomLevel >= mShowAccuracyZoom)
                radius = (float) (mRadius * v.pos.scale);
            radius = Math.max(CIRCLE_SIZE * mScale, radius);

            viewShed = true;
            animate(false);
        }
        gl.uniform1f(hScale, radius);

        double x = mIndicatorPosition.x - v.pos.x;
        double y = mIndicatorPosition.y - v.pos.y;
        double tileScale = Tile.SIZE * v.pos.scale;

        v.mvp.setTransScale((float) (x * tileScale), (float) (y * tileScale), 1);
        v.mvp.multiplyMM(v.viewproj, v.mvp);
        v.mvp.setAsUniform(hMatrixPosition);

        if (!viewShed && mAnimate) {
            float phase = Math.abs(animPhase() - 0.5f) * 2;
            //phase = Interpolation.fade.apply(phase);
            phase = Interpolation.swing.apply(phase);

            gl.uniform1f(hPhase, 0.8f + phase * 0.2f);
        } else {
            gl.uniform1f(hPhase, 1);
        }

        if (viewShed && mLocationIsVisible) {
            if (mCallback != null && mCallback.hasRotation())
                gl.uniform1i(uMode, 1); // With bearing
            else
                gl.uniform1i(uMode, 0); // Without bearing
        } else
            gl.uniform1i(uMode, -1); // Outside screen

        GLUtils.setColor(uColor, mColor);

        gl.drawArrays(GL.TRIANGLE_STRIP, 0, 4);

        /* Texture */

        super.render(v);
    }

    protected boolean init() {
        int program = GLShader.loadShader(mShaderFile != null ? mShaderFile : "accuracy_1");
        if (program == 0)
            return false;

        mShaderProgram = program;
        hVertexPosition = gl.getAttribLocation(program, "a_pos");
        hMatrixPosition = gl.getUniformLocation(program, "u_mvp");
        hPhase = gl.getUniformLocation(program, "u_phase");
        hScale = gl.getUniformLocation(program, "u_scale");
        uColor = gl.getUniformLocation(program, "u_color");
        uMode = gl.getUniformLocation(program, "u_mode");

        return true;
    }
}
