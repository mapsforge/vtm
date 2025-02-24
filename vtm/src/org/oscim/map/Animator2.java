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
import org.oscim.core.Point;
import org.oscim.core.Tile;
import org.oscim.renderer.MapRenderer;
import org.oscim.utils.ThreadUtils;
import org.oscim.utils.animation.DragForce;
import org.oscim.utils.animation.Easing;

import java.util.logging.Logger;

import static org.oscim.utils.FastMath.clamp;

public class Animator2 extends Animator {
    private static final Logger log = Logger.getLogger(Animator2.class.getName());

    private static final int ANIM_KINETIC = 1 << 5;

    /**
     * The minimum changes that are pleasant for users.
     */
    private static final float DEFAULT_MIN_VISIBLE_CHANGE_PIXELS = 0.6f;
    private static final float DEFAULT_MIN_VISIBLE_CHANGE_RADIAN = 0.001f;
    private static final float DEFAULT_MIN_VISIBLE_CHANGE_SCALE = 1f;

    /**
     * The friction scalar for fling movements (1 as base).
     */
    public static float FLING_FRICTION_MOVE = 1.0f;

    /**
     * The friction scalar for fling rotations (1 as base).
     */
    public static float FLING_FRICTION_ROTATE = 1.2f;

    /**
     * The friction scalar for fling scales (1 as base).
     */
    public static float FLING_FRICTION_SCALE = 1.2f;

    private final DragForce mFlingRotateForce = new DragForce();
    private final DragForce mFlingScaleForce = new DragForce();
    private final DragForce mFlingScrollForce = new DragForce();

    private final Point mMovePoint = new Point();
    private final Point mScrollRatio = new Point();

    private long mFrameStart = -1;
    private float mScrollDet2D = 1f;

    public Animator2(Map map) {
        super(map);

        // Init fling force thresholds
        mFlingRotateForce.setValueThreshold(DEFAULT_MIN_VISIBLE_CHANGE_RADIAN);
        mFlingScrollForce.setValueThreshold(DEFAULT_MIN_VISIBLE_CHANGE_PIXELS);
        mFlingScaleForce.setValueThreshold(DEFAULT_MIN_VISIBLE_CHANGE_SCALE);
    }

    /**
     * Animates a physical fling for rotations.
     *
     * @param angularVelocity angular velocity in radians
     */
    public void animateFlingRotate(float angularVelocity, float pivotX, float pivotY) {
        ThreadUtils.assertMainThread();

        float flingFactor = -0.25f; // Can be changed but should be standardized for all callers
        angularVelocity *= flingFactor;

        mFlingRotateForce.setFrictionScalar(FLING_FRICTION_ROTATE);
        mFlingRotateForce.setValueAndVelocity(0f, angularVelocity);

        if (!isActive()) {
            mMap.getMapPosition(mStartPos);

            mPivot.x = pivotX;
            mPivot.y = pivotY;

            animFlingStart(ANIM_ROTATE);
        } else {
            mState |= ANIM_ROTATE;
        }
    }

    /**
     * Animates a physical fling for scrolls.
     *
     * @param velocityX the x velocity depends on screen resolution
     * @param velocityY the y velocity depends on screen resolution
     */
    public void animateFlingScroll(float velocityX, float velocityY,
                                   int xmin, int xmax, int ymin, int ymax) {
        ThreadUtils.assertMainThread();

        if (velocityX * velocityX + velocityY * velocityY < 2048)
            return;

        float flingFactor = 2.0f; // Can be changed but should be standardized for all callers
        float screenFactor = CanvasAdapter.DEFAULT_DPI / CanvasAdapter.dpi;

        velocityX *= screenFactor * flingFactor;
        velocityY *= screenFactor * flingFactor;
        velocityX = clamp(velocityX, xmin, xmax);
        velocityY = clamp(velocityY, ymin, ymax);

        float sumVelocity = Math.abs(velocityX) + Math.abs(velocityY);
        mScrollRatio.x = velocityX / sumVelocity;
        mScrollRatio.y = velocityY / sumVelocity;
        mScrollDet2D = (float) (mScrollRatio.x * mScrollRatio.x + mScrollRatio.y * mScrollRatio.y);

        mFlingScrollForce.setFrictionScalar(FLING_FRICTION_MOVE);
        mFlingScrollForce.setValueAndVelocity(0f, (float) Math.sqrt(velocityX * velocityX + velocityY * velocityY));

        if (!isActive()) {
            mMap.getMapPosition(mStartPos);

            animFlingStart(ANIM_MOVE);
        } else {
            mState |= ANIM_MOVE;
        }
    }

    /**
     * Animates a physical fling for zooms.
     *
     * @param scaleVelocity the scale velocity depends on screen resolution
     */
    public void animateFlingZoom(float scaleVelocity, float pivotX, float pivotY) {
        ThreadUtils.assertMainThread();

        float flingFactor = -1.0f; // Can be changed but should be standardized for all callers
        float screenFactor = CanvasAdapter.DEFAULT_DPI / CanvasAdapter.dpi;
        scaleVelocity *= flingFactor * screenFactor;

        mFlingScaleForce.setFrictionScalar(FLING_FRICTION_SCALE);
        mFlingScaleForce.setValueAndVelocity(0f, scaleVelocity);

        if (!isActive()) {
            mMap.getMapPosition(mStartPos);

            mPivot.x = pivotX;
            mPivot.y = pivotY;

            animFlingStart(ANIM_SCALE);
        } else {
            mState |= ANIM_SCALE;
        }
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
     * Alternative implementation of Animator's <code>animateFling</code>.
     * Uses scheme of predictable animations using mDeltaPos.
     *
     * @param velocityX the x velocity depends on screen resolution
     * @param velocityY the y velocity depends on screen resolution
     */
    public void kineticScroll(float velocityX, float velocityY,
                              int xmin, int xmax, int ymin, int ymax) {
        ThreadUtils.assertMainThread();

        if (velocityX * velocityX + velocityY * velocityY < 2048)
            return;

        mMap.getMapPosition(mStartPos);

        float duration = 500;

        float screenFactor = CanvasAdapter.DEFAULT_DPI / CanvasAdapter.dpi;
        velocityX = velocityX * screenFactor;
        velocityY = velocityY * screenFactor;
        velocityX = clamp(velocityX, xmin, xmax);
        velocityY = clamp(velocityY, ymin, ymax);
        if (Float.isNaN(velocityX) || Float.isNaN(velocityY)) {
            log.fine("fling NaN!");
            return;
        }

        double tileScale = mStartPos.scale * Tile.SIZE;
        ViewController.applyRotation(-velocityX, -velocityY, mStartPos.bearing, mMovePoint);
        mDeltaPos.setX(mMovePoint.x / tileScale);
        mDeltaPos.setY(mMovePoint.y / tileScale);

        animStart(duration, ANIM_KINETIC | ANIM_MOVE, Easing.Type.SINE_OUT);
    }

    /**
     * called by MapRenderer at begin of each frame.
     */
    @Override
    void updateAnimation() {
        if (mState == ANIM_NONE)
            return;

        ViewController v = mMap.viewport();

        /* cancel animation when position was changed since last
         * update, i.e. when it was modified outside the animator. */
        if (v.getMapPosition(mCurPos)) {
            log.fine("cancel anim - changed");
            cancel();
            return;
        }

        final long currentFrametime = MapRenderer.frametime;

        if ((mState & ANIM_FLING) == 0) {
            // Do predicted animations
            long millisLeft = mAnimEnd - currentFrametime;

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

            if ((mState & ANIM_KINETIC) != 0) {
                // Reduce value to simulate kinetic behaviour
                adv = (float) Math.sqrt(adv);
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

            if ((mState & ANIM_SCALE) != 0) {
                float valueDelta = mFlingScaleForce.updateValueAndVelocity(deltaT) / 1000f;
                float velocity = mFlingScaleForce.getVelocity();
                if (valueDelta != 0) {
                    valueDelta = valueDelta > 0 ? valueDelta + 1 : -1 / (valueDelta - 1);
                    v.scaleMap(valueDelta, (float) mPivot.x, (float) mPivot.y);
                }

                if (velocity == 0) {
                    mState &= (~ANIM_SCALE); // End scale mode
                }
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

                if (velocity == 0) {
                    mState &= (~ANIM_MOVE); // End move mode
                }
            }

            if ((mState & ANIM_ROTATE) != 0) {
                float valueDelta = mFlingRotateForce.updateValueAndVelocity(deltaT);
                float velocity = mFlingRotateForce.getVelocity();

                v.rotateMap(valueDelta, (float) mPivot.x, (float) mPivot.y);

                if (velocity == 0) {
                    mState &= (~ANIM_ROTATE); // End rotate mode
                }
            }

            /*if ((mState & ANIM_TILT) != 0) {
                // Do some tilt fling
                if(velocity == 0) {
                    mState &= (~ANIM_TILT); // End tilt mode
                }
            }*/

            if ((mState & (ANIM_MOVE | ANIM_ROTATE | ANIM_SCALE)) == 0) {
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
}
