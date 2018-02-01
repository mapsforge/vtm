/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 Stephan Leuschner
 * Copyright 2016 devemux86
 * Copyright 2016 Izumi Kawashima
 * Copyright 2017 Wolfgang Schramm
 * Copyright 2018 Gustl22
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

    public final static int ANIM_NONE = 0;
    public final static int ANIM_MOVE = 1 << 0;
    public final static int ANIM_SCALE = 1 << 1;
    public final static int ANIM_ROTATE = 1 << 2;
    public final static int ANIM_TILT = 1 << 3;
    public final static int ANIM_FLING = 1 << 4;

    /**
     * The minimum changes that are pleasant for users.
     */
    private static final float DEFAULT_MIN_VISIBLE_CHANGE_PIXELS = 0.5f;
    private static final float DEFAULT_MIN_VISIBLE_CHANGE_DEGREE = 0.001f;
    private static final float DEFAULT_MIN_VISIBLE_CHANGE_SCALE = 1f;

    private static final float FLING_FRICTION_MOVE = 0.9f;
    private static final float FLING_FRICTION_ROTATE = 1.0f;
    private static final float FLING_FRICTION_SCALE = 1.2f;

    private final Map mMap;

    private final MapPosition mCurPos = new MapPosition();
    private final MapPosition mStartPos = new MapPosition();
    private final MapPosition mDeltaPos = new MapPosition();

    private final Point mPivot = new Point();
    private final Point mScrollRatio = new Point();
    private final DragForce mFlingScrollForce = new DragForce();
    private final DragForce mFlingScaleForce = new DragForce();
    private final DragForce mFlingRotateForce = new DragForce();

    private float mScrollDet2D = 1f;
    private float mDuration = 500;
    private long mAnimEnd = -1;
    private long mFrameStart = -1;
    private Easing.Type mEasingType = Easing.Type.LINEAR;

    private int mState = ANIM_NONE;

    public Animator(Map map) {
        mMap = map;
    }

    public synchronized void animateTo(long duration, BoundingBox bbox) {
        animateTo(duration, bbox, Easing.Type.LINEAR);
    }

    public synchronized void animateTo(long duration, BoundingBox bbox, Easing.Type easingType) {
        animateTo(duration, bbox, easingType, ANIM_MOVE | ANIM_SCALE | ANIM_ROTATE | ANIM_TILT);
    }

    public synchronized void animateTo(long duration, BoundingBox bbox, Easing.Type easingType, int state) {
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

        animEaseStart(duration, state, easingType);
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
        animateTo(duration, geoPoint, scale, relative, easingType, ANIM_MOVE | ANIM_SCALE);
    }

    /**
     * Animate to GeoPoint
     *
     * @param duration   in ms
     * @param geoPoint
     * @param scale
     * @param relative   alter scale relative to current scale
     * @param easingType easing function
     * @param state      animation state
     */
    public void animateTo(long duration, GeoPoint geoPoint,
                          double scale, boolean relative, Easing.Type easingType, int state) {
        ThreadUtils.assertMainThread();

        mMap.getMapPosition(mStartPos);

        if (relative)
            scale = mStartPos.scale * scale;

        scale = mMap.viewport().limitScale(scale);

        mDeltaPos.set(longitudeToX(geoPoint.getLongitude()) - mStartPos.x,
                latitudeToY(geoPoint.getLatitude()) - mStartPos.y,
                scale - mStartPos.scale,
                0, 0);

        animEaseStart(duration, state, easingType);
    }

    public void animateTo(GeoPoint p) {
        animateTo(500, p, 1, true, Easing.Type.LINEAR);
    }

    public void animateTo(long duration, MapPosition pos) {
        animateTo(duration, pos, Easing.Type.LINEAR);
    }

    public void animateTo(long duration, MapPosition pos, Easing.Type easingType) {
        animateTo(duration, pos, easingType, ANIM_MOVE | ANIM_SCALE | ANIM_ROTATE | ANIM_TILT);
    }

    public void animateTo(long duration, MapPosition pos, Easing.Type easingType, int state) {
        ThreadUtils.assertMainThread();

        mMap.getMapPosition(mStartPos);

        pos.scale = mMap.viewport().limitScale(pos.scale);

        mDeltaPos.set(pos.x - mStartPos.x,
                pos.y - mStartPos.y,
                pos.scale - mStartPos.scale,
                pos.bearing - mStartPos.bearing,
                mMap.viewport().limitTilt(pos.tilt) - mStartPos.tilt);

        animEaseStart(duration, state, easingType);
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

        animEaseStart(duration, ANIM_SCALE, easingType);
    }

    /**
     * @param velocityX the x velocity depends on screen resolution
     * @param velocityY the y velocity depends on screen resolution
     */
    public void animateFlingScroll(float velocityX, float velocityY,
                             int xmin, int xmax, int ymin, int ymax) {
        ThreadUtils.assertMainThread();

        if (velocityX * velocityX + velocityY * velocityY < 2048)
            return;

        mMap.getMapPosition(mStartPos);

        float flingFactor = 2.3f; // Can be changed but should be standardized for all callers
        float screenFactor = CanvasAdapter.DEFAULT_DPI / CanvasAdapter.dpi;

        velocityX *= screenFactor * flingFactor;
        velocityY *= screenFactor * flingFactor;
        velocityX = clamp(velocityX, xmin, xmax);
        velocityY = clamp(velocityY, ymin, ymax);

        float sumVelocity = Math.abs(velocityX) + Math.abs(velocityY);
        mScrollRatio.x = velocityX / sumVelocity;
        mScrollRatio.y = velocityY / sumVelocity;
        mScrollDet2D = (float) (mScrollRatio.x * mScrollRatio.x + mScrollRatio.y * mScrollRatio.y);

        mFlingScrollForce.setValueThreshold(DEFAULT_MIN_VISIBLE_CHANGE_PIXELS);
        mFlingScrollForce.setFrictionScalar(FLING_FRICTION_MOVE);
        mFlingScrollForce.setValueAndVelocity(0f, (float) Math.sqrt(velocityX * velocityX + velocityY * velocityY));

        animFlingStart(ANIM_MOVE);
    }

    public void animateFlingRotate(float angularVelocity, float pivotX, float pivotY) {
        ThreadUtils.assertMainThread();

        //if (Math.abs(angularVelocity) < 0.01)
        //    return;

        mMap.getMapPosition(mStartPos);

        mPivot.x = pivotX;
        mPivot.y = pivotY;

        float flingFactor = -0.4f; // Can be changed but should be standardized for all callers
        angularVelocity *= flingFactor;

        mFlingRotateForce.setValueThreshold(DEFAULT_MIN_VISIBLE_CHANGE_DEGREE);
        mFlingRotateForce.setFrictionScalar(FLING_FRICTION_ROTATE);
        mFlingRotateForce.setValueAndVelocity(0f, angularVelocity);

        animFlingStart(ANIM_ROTATE);
    }

    /**
     * @param scaleVelocity the scale velocity depends on screen resolution
     */
    public void animateFlingZoom(float scaleVelocity, float pivotX, float pivotY) {
        ThreadUtils.assertMainThread();

        mMap.getMapPosition(mStartPos);

        mPivot.x = pivotX;
        mPivot.y = pivotY;

        float flingFactor = -1.0f; // Can be changed but should be standardized for all callers
        float screenFactor = CanvasAdapter.DEFAULT_DPI / CanvasAdapter.dpi;
        scaleVelocity *= flingFactor * screenFactor;

        mFlingScaleForce.setValueThreshold(DEFAULT_MIN_VISIBLE_CHANGE_SCALE);
        mFlingScaleForce.setFrictionScalar(FLING_FRICTION_SCALE);
        mFlingScaleForce.setValueAndVelocity(0f, scaleVelocity);

        animFlingStart(ANIM_SCALE);
    }

    private void animEaseStart(float duration, int state, Easing.Type easingType) {
        if (!isActive())
            mMap.events.fire(Map.ANIM_START, mMap.mMapPosition);
        mCurPos.copy(mStartPos);
        mState = state;
        mDuration = duration;
        mAnimEnd = System.currentTimeMillis() + (long) duration;
        mEasingType = easingType;
        mMap.render();
    }

    private void animFlingStart(int state) {
        if (!isActive())
            mMap.events.fire(Map.ANIM_START, mMap.mMapPosition);
        mCurPos.copy(mStartPos);
        mState |= ANIM_FLING | state;
        mFrameStart = MapRenderer.frametime; // CurrentTimeMillis would cause negative delta
        mMap.render();
    }

    /**
     * called by MapRenderer at begin of each frame.
     */
    void updateAnimation() {
        if (mState == ANIM_NONE)
            return;

        ViewController v = mMap.viewport();

        /* cancel animation when position was changed since last
         * update, i.e. when it was modified outside the animator. */
        if (v.getMapPosition(mCurPos)) {
            log.debug("cancel anim - changed");
            cancel();
            return;
        }

        final long currentFrametime = MapRenderer.frametime;

        if ((mState & ANIM_FLING) == 0) {
            // Do predicted animations
            float adv;
            long millisLeft = mAnimEnd - currentFrametime;

            adv = clamp(1.0f - millisLeft / mDuration, 1E-6f, 1);
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
        } else {
            // Do physical fling animation
            long deltaT = currentFrametime - mFrameStart;
            mFrameStart = currentFrametime;
            boolean isAnimationFinished = true;

            if ((mState & ANIM_SCALE) != 0) {
                float valueDelta = mFlingScaleForce.updateValueAndVelocity(deltaT) / 1000f;
                float velocity = mFlingScaleForce.getVelocity();
                if (valueDelta != 0) {
                    valueDelta = valueDelta > 0 ? valueDelta + 1 : -1 / (valueDelta - 1);
                    v.scaleMap(valueDelta, (float) mPivot.x, (float) mPivot.y);
                }
                isAnimationFinished = isAnimationFinished && (velocity == 0);
            }

            if ((mState & ANIM_MOVE) != 0) {
                float valueDelta = mFlingScrollForce.updateValueAndVelocity(deltaT);
                float velocity = mFlingScrollForce.getVelocity();

                float valFactor = (float) Math.sqrt((valueDelta * valueDelta) / mScrollDet2D);
                float dx = (float) mScrollRatio.x * valFactor;
                float dy = (float) mScrollRatio.y * valFactor;

                if (dx != 0 || dy != 0) {
                    v.moveMap(dx, dy);
                }

                isAnimationFinished = isAnimationFinished && (velocity == 0);
            }

            if ((mState & ANIM_ROTATE) != 0) {
                float valueDelata = mFlingRotateForce.updateValueAndVelocity(deltaT);
                float velocity = mFlingRotateForce.getVelocity();

                v.rotateMap(valueDelata, (float) mPivot.x, (float) mPivot.y);

                isAnimationFinished = isAnimationFinished && (velocity == 0);
            }

//            if ((mState & ANIM_TILT) != 0) {
//                Do some tilt fling
//                isAnimationFinished = isAnimationFinished && (velocity == 0);
//            }

            if (isAnimationFinished) {
                //log.debug("animate END");
                cancel();
            }
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

    /**
     * Get the map position at animation end.<br>
     * Note: valid only with animateTo methods.
     */
    public MapPosition getEndPosition() {
        return mDeltaPos;
    }


    /*
     * Copyright (C) 2017 The Android Open Source Project
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     *      http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */

    /**
     * See: https://developer.android.com/reference/android/support/animation/FlingAnimation.html
     * Package: android.support.animation.FlingAnimation
     */
    private final class DragForce {

        private static final float DEFAULT_FRICTION = -4.2f;

        // This multiplier is used to calculate the velocity threshold given a certain value
        // threshold. The idea is that if it takes >= 1 frame to move the value threshold amount,
        // then the velocity is a reasonable threshold.
        private static final float VELOCITY_THRESHOLD_MULTIPLIER = 1000f / 16f; // 1 frame ≙ 16 ms (62.5 fps)
        private float mFriction = DEFAULT_FRICTION;
        private float mVelocityThreshold = DEFAULT_MIN_VISIBLE_CHANGE_PIXELS * VELOCITY_THRESHOLD_MULTIPLIER;

        // Internal state to hold a value/velocity pair.
        private float mValue;
        private float mVelocity;

        void setFrictionScalar(float frictionScalar) {
            mFriction = frictionScalar * DEFAULT_FRICTION;
        }

        float getFrictionScalar() {
            return mFriction / DEFAULT_FRICTION;
        }

        /**
         * Updates the animation state (i.e. value and velocity).
         *
         * @param deltaT time elapsed in millisecond since last frame
         * @return the value delta since last frame
         */
        float updateValueAndVelocity(long deltaT) {
            float velocity = mVelocity;
            mVelocity = (float) (velocity * Math.exp((deltaT / 1000f) * mFriction));
            float valueDelta = (mVelocity - velocity);
            mValue += valueDelta;
            if (isAtEquilibrium(mValue, mVelocity)) {
                mVelocity = 0f;
            }
            return valueDelta;
        }

        public void setValueAndVelocity(float value, float velocity) {
            mValue = value;
            mVelocity = velocity;
        }

        public float getValue() {
            return mValue;
        }

        public float getVelocity() {
            return mVelocity;
        }

        public float getAcceleration(float position, float velocity) {
            return velocity * mFriction;
        }

        public boolean isAtEquilibrium(float value, float velocity) {
            return Math.abs(velocity) < mVelocityThreshold;
        }

        void setValueThreshold(float threshold) {
            mVelocityThreshold = threshold * VELOCITY_THRESHOLD_MULTIPLIER;
        }
    }
}
