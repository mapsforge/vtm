/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 Stephan Leuschner
 * Copyright 2016 devemux86
 * Copyright 2016 Izumi Kawashima
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
package org.oscim.map;

import org.oscim.backend.CanvasAdapter;
import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.core.Point;
import org.oscim.core.Tile;
import org.oscim.renderer.MapRenderer;
import org.oscim.utils.Easing;
import org.oscim.utils.ThreadUtils;
import org.oscim.utils.async.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.oscim.core.MercatorProjection.latitudeToY;
import static org.oscim.core.MercatorProjection.longitudeToX;
import static org.oscim.utils.FastMath.clamp;

public class Animator {
    static final Logger log = LoggerFactory.getLogger(Animator.class);

    private final static int ANIM_NONE = 0;
    private final static int ANIM_MOVE = 1 << 0;
    private final static int ANIM_SCALE = 1 << 1;
    private final static int ANIM_ROTATE = 1 << 2;
    private final static int ANIM_TILT = 1 << 3;
    private final static int ANIM_FLING = 1 << 4;

    private final Map mMap;

    private final MapPosition mCurPos = new MapPosition();
    private final MapPosition mStartPos = new MapPosition();
    private final MapPosition mDeltaPos = new MapPosition();

    private final Point mScroll = new Point();
    private final Point mPivot = new Point();
    private final Point mVelocity = new Point();

    private float mDuration = 500;
    private long mAnimEnd = -1;
    private Easing.Type mEasingType = Easing.Type.LINEAR;

    private int mState = ANIM_NONE;

    public Animator(Map map) {
        mMap = map;
    }

    public synchronized void animateTo(long duration, BoundingBox bbox) {
        animateTo(duration, bbox, Easing.Type.LINEAR);
    }

    public synchronized void animateTo(long duration, BoundingBox bbox, Easing.Type easingType) {
        ThreadUtils.assertMainThread();

        mMap.getMapPosition(mStartPos);
        /* TODO for large distance first scale out, then in
         * calculate the maximum scale at which the BoundingBox
         * is completely visible */
        double dx = Math.abs(longitudeToX(bbox.getMaxLongitude())
                - longitudeToX(bbox.getMinLongitude()));

        double dy = Math.abs(latitudeToY(bbox.getMinLatitude())
                - latitudeToY(bbox.getMaxLatitude()));

        log.debug("anim bbox " + bbox);

        double zx = mMap.getWidth() / (dx * Tile.SIZE);
        double zy = mMap.getHeight() / (dy * Tile.SIZE);
        double newScale = Math.min(zx, zy);

        GeoPoint p = bbox.getCenterPoint();

        mDeltaPos.set(longitudeToX(p.getLongitude()) - mStartPos.x,
                latitudeToY(p.getLatitude()) - mStartPos.y,
                newScale - mStartPos.scale,
                -mStartPos.bearing,
                -mStartPos.tilt);

        animStart(duration, ANIM_MOVE | ANIM_SCALE | ANIM_ROTATE | ANIM_TILT, easingType);
    }

    public void animateTo(BoundingBox bbox) {
        animateTo(1000, bbox, Easing.Type.LINEAR);
    }

    /**
     * Animate to GeoPoint
     *
     * @param duration in ms
     * @param geoPoint
     * @param scale
     * @param relative alter scale relative to current scale
     */
    public void animateTo(long duration, GeoPoint geoPoint,
                          double scale, boolean relative) {
        animateTo(duration, geoPoint, scale, relative, Easing.Type.LINEAR);
    }

    /**
     * Animate to GeoPoint
     *
     * @param duration   in ms
     * @param geoPoint
     * @param scale
     * @param relative   alter scale relative to current scale
     * @param easingType easing function
     */
    public void animateTo(long duration, GeoPoint geoPoint,
                          double scale, boolean relative, Easing.Type easingType) {
        ThreadUtils.assertMainThread();

        mMap.getMapPosition(mStartPos);

        if (relative)
            scale = mStartPos.scale * scale;

        scale = mMap.viewport().limitScale(scale);

        mDeltaPos.set(longitudeToX(geoPoint.getLongitude()) - mStartPos.x,
                latitudeToY(geoPoint.getLatitude()) - mStartPos.y,
                scale - mStartPos.scale,
                0, 0);

        animStart(duration, ANIM_MOVE | ANIM_SCALE, easingType);
    }

    public void animateTo(GeoPoint p) {
        animateTo(500, p, 1, true, Easing.Type.LINEAR);
    }

    public void animateTo(long duration, MapPosition pos) {
        animateTo(duration, pos, Easing.Type.LINEAR);
    }

    public void animateTo(long duration, MapPosition pos, Easing.Type easingType) {
        ThreadUtils.assertMainThread();

        mMap.getMapPosition(mStartPos);

        pos.scale = mMap.viewport().limitScale(pos.scale);

        mDeltaPos.set(pos.x - mStartPos.x,
                pos.y - mStartPos.y,
                pos.scale - mStartPos.scale,
                pos.bearing - mStartPos.bearing,
                mMap.viewport().limitTilt(pos.tilt) - mStartPos.tilt);

        animStart(duration, ANIM_MOVE | ANIM_SCALE | ANIM_ROTATE | ANIM_TILT, easingType);
    }

    public void animateZoom(long duration, double scaleBy,
                            float pivotX, float pivotY) {
        animateZoom(duration, scaleBy, pivotX, pivotY, Easing.Type.LINEAR);
    }

    public void animateZoom(long duration, double scaleBy,
                            float pivotX, float pivotY, Easing.Type easingType) {
        ThreadUtils.assertMainThread();

        mMap.getMapPosition(mCurPos);

        if (mState == ANIM_SCALE)
            scaleBy = (mStartPos.scale + mDeltaPos.scale) * scaleBy;
        else
            scaleBy = mCurPos.scale * scaleBy;

        mStartPos.copy(mCurPos);
        scaleBy = mMap.viewport().limitScale(scaleBy);
        if (scaleBy == 0.0)
            return;

        mDeltaPos.scale = scaleBy - mStartPos.scale;

        mPivot.x = pivotX;
        mPivot.y = pivotY;

        animStart(duration, ANIM_SCALE, easingType);
    }

    public void animateFling(float velocityX, float velocityY,
                             int xmin, int xmax, int ymin, int ymax) {

        ThreadUtils.assertMainThread();

        if (velocityX * velocityX + velocityY * velocityY < 2048)
            return;

        mMap.getMapPosition(mStartPos);

        mScroll.x = 0;
        mScroll.y = 0;

        float duration = 500;

        float flingFactor = CanvasAdapter.DEFAULT_DPI / CanvasAdapter.dpi;
        mVelocity.x = velocityX * flingFactor;
        mVelocity.y = velocityY * flingFactor;
        mVelocity.x = clamp(mVelocity.x, xmin, xmax);
        mVelocity.y = clamp(mVelocity.y, ymin, ymax);
        if (Double.isNaN(mVelocity.x) || Double.isNaN(mVelocity.y)) {
            log.debug("fling NaN!");
            return;
        }

        animStart(duration, ANIM_FLING, Easing.Type.LINEAR);
    }

    private void animStart(float duration, int state, Easing.Type easingType) {
        if (!isActive())
            mMap.events.fire(Map.ANIM_START, mMap.mMapPosition);
        mCurPos.copy(mStartPos);
        mState = state;
        mDuration = duration;
        mAnimEnd = System.currentTimeMillis() + (long) duration;
        mEasingType = easingType;
        mMap.render();
    }

    /**
     * called by MapRenderer at begin of each frame.
     */
    void updateAnimation() {
        if (mState == ANIM_NONE)
            return;

        long millisLeft = mAnimEnd - MapRenderer.frametime;

        ViewController v = mMap.viewport();

        /* cancel animation when position was changed since last
         * update, i.e. when it was modified outside the animator. */
        if (v.getMapPosition(mCurPos)) {
            log.debug("cancel anim - changed");
            cancel();
            return;
        }

        float adv = clamp(1.0f - millisLeft / mDuration, 1E-6f, 1);
        // Avoid redundant calculations in case of linear easing
        if (mEasingType != Easing.Type.LINEAR) {
            adv = Easing.ease(0, (long) (adv * Long.MAX_VALUE), Long.MAX_VALUE, mEasingType);
            adv = clamp(adv, 0, 1);
        }

        double scaleAdv = 1;
        if ((mState & ANIM_SCALE) != 0) {
            scaleAdv = doScale(v, adv);
        }

        if ((mState & ANIM_MOVE) != 0) {
            v.moveTo(mStartPos.x + mDeltaPos.x * (adv / scaleAdv),
                    mStartPos.y + mDeltaPos.y * (adv / scaleAdv));
        }

        if ((mState & ANIM_FLING) != 0) {
            adv = (float) Math.sqrt(adv);
            double dx = mVelocity.x * adv;
            double dy = mVelocity.y * adv;
            if ((dx - mScroll.x) != 0 || (dy - mScroll.y) != 0) {
                v.moveMap((float) (dx - mScroll.x),
                        (float) (dy - mScroll.y));
                mScroll.x = dx;
                mScroll.y = dy;
            }
        }
        if ((mState & ANIM_ROTATE) != 0) {
            v.setRotation(mStartPos.bearing + mDeltaPos.bearing * adv);
        }

        if ((mState & ANIM_TILT) != 0) {
            v.setTilt(mStartPos.tilt + mDeltaPos.tilt * adv);
        }

        if (millisLeft <= 0) {
            //log.debug("animate END");
            cancel();
        }

        /* remember current map position */
        final boolean changed = v.getMapPosition(mCurPos);

        if (changed) {
            mMap.updateMap(true);
        } else {
            mMap.postDelayed(updateTask, 10);
        }
    }

    private Task updateTask = new Task() {
        @Override
        public int go(boolean canceled) {
            if (!canceled)
                updateAnimation();
            return Task.DONE;
        }
    };

    private double doScale(ViewController v, float adv) {
        double newScale = mStartPos.scale + mDeltaPos.scale * Math.sqrt(adv);

        v.scaleMap((float) (newScale / mCurPos.scale),
                (float) mPivot.x, (float) mPivot.y);

        return newScale / (mStartPos.scale + mDeltaPos.scale);
    }

    public void cancel() {
        //ThreadUtils.assertMainThread();
        mState = ANIM_NONE;
        mPivot.x = 0;
        mPivot.y = 0;
        mMap.events.fire(Map.ANIM_END, mMap.mMapPosition);
    }

    public boolean isActive() {
        return mState != ANIM_NONE;
    }
}
