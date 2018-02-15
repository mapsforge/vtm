/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 devemux86
 * Copyright 2016 Andrey Novikov
 * Copyright 2016 Longri
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
package org.oscim.layers;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.Platform;
import org.oscim.core.MapPosition;
import org.oscim.core.Tile;
import org.oscim.event.Event;
import org.oscim.event.Gesture;
import org.oscim.event.MotionEvent;
import org.oscim.map.Animator2;
import org.oscim.map.Map;
import org.oscim.map.Map.InputListener;
import org.oscim.map.ViewController;
import org.oscim.utils.FastMath;
import org.oscim.utils.Parameters;
import org.oscim.utils.async.Task;

import static org.oscim.backend.CanvasAdapter.dpi;
import static org.oscim.utils.FastMath.withinSquaredDist;

/**
 * Changes Viewport by handling move, fling, scale, rotation and tilt gestures.
 * <p/>
 * TODO rewrite using gesture primitives to build more complex gestures:
 * maybe something similar to this https://github.com/ucbvislab/Proton
 */
public class MapEventLayer2 extends AbstractMapEventLayer implements InputListener {

    private boolean mEnableRotate = true;
    private boolean mEnableTilt = true;
    private boolean mEnableMove = true;
    private boolean mEnableScale = true;
    private boolean mFixOnCenter = false;

    /* possible state transitions */
    private boolean mCanScale;
    private boolean mCanRotate;
    private boolean mCanTilt;

    /* current gesture state */
    private boolean mDoRotate;
    private boolean mDoScale;
    private boolean mDoTilt;

    private boolean mDown;
    private boolean mDragZoom;
    private boolean mTwoFingers;
    private boolean mTwoFingersDone;
    private int mTaps;
    private long mStartDown;
    private MotionEvent mLastTap;

    private float mPrevX1;
    private float mPrevY1;
    private float mPrevX2;
    private float mPrevY2;

    private float mPivotX;
    private float mPivotY;

    private double mAngle;
    private double mPrevPinchWidth;
    private long mStartMove;

    /**
     * 1in = 25.4mm
     */
    private static final float INCH = 25.4f;

    /**
     * 2mm as minimal distance to start move: dpi / 25.4
     */
    private static final float MIN_SLOP = INCH / 2;

    /**
     * 1cm distance in dips between the first touch and second touch to still be considered a double tap
     */
    private static final float DOUBLE_TAP_SLOP = INCH / 10;

    private static final float PINCH_ZOOM_THRESHOLD = INCH / 4; // 4mm
    private static final float PINCH_TILT_THRESHOLD = INCH / 4; // 4mm
    private static final float PINCH_TILT_SLOPE = 0.75f;
    private static final float PINCH_ROTATE_THRESHOLD = 0.2f;
    private static final float PINCH_ROTATE_THRESHOLD2 = 0.5f;

    //TODO Should be initialized with platform specific defaults
    /**
     * 100 ms since start of move to reduce fling scroll
     */
    private static final long FLING_MIN_THRESHOLD = 100;
    private static final long DOUBLE_TAP_THRESHOLD = 300;
    private static final long LONG_PRESS_THRESHOLD = 500;

    private final VelocityTracker mScrollTracker;
    private final VelocityTracker mScaleTracker;
    private final VelocityTracker mRotateTracker;

    private Task mGestureTask;

    private final MapPosition mapPosition = new MapPosition();

    public MapEventLayer2(Map map) {
        super(map);
        mScrollTracker = new VelocityTracker();
        mScaleTracker = new VelocityTracker();
        mRotateTracker = new VelocityTracker();
    }

    @Override
    public void onInputEvent(Event e, MotionEvent motionEvent) {
        onTouchEvent(motionEvent);
    }

    @Override
    public void enableRotation(boolean enable) {
        mEnableRotate = enable;
    }

    @Override
    public boolean rotationEnabled() {
        return mEnableRotate;
    }

    @Override
    public void enableTilt(boolean enable) {
        mEnableTilt = enable;
    }

    @Override
    public boolean tiltEnabled() {
        return mEnableTilt;
    }

    @Override
    public void enableMove(boolean enable) {
        mEnableMove = enable;
    }

    @Override
    public boolean moveEnabled() {
        return mEnableMove;
    }

    @Override
    public void enableZoom(boolean enable) {
        mEnableScale = enable;
    }

    @Override
    public boolean zoomEnabled() {
        return mEnableScale;
    }

    /**
     * When enabled zoom- and rotation-gestures will not move the viewport.
     */
    @Override
    public void setFixOnCenter(boolean enable) {
        mFixOnCenter = enable;
    }

    private boolean onTouchEvent(final MotionEvent e) {
        int action = getAction(e);
        final long time = e.getTime();

        if (action == MotionEvent.ACTION_DOWN) {
            if (mGestureTask != null) {
                mGestureTask.cancel();
                mGestureTask = null;
            }
            mMap.handleGesture(Gesture.PRESS, e);
            mDown = true;
            mStartDown = time;
            if (mTaps > 0) {
                float mx = e.getX(0) - mLastTap.getX();
                float my = e.getY(0) - mLastTap.getY();
                if (!isDoubleTap(mx, my)) {
                    mTaps = 0;
                    mMap.handleGesture(Gesture.TAP, mLastTap);
                }
            } else {
                mMap.animator().cancel();

                mStartMove = -1;
                mDragZoom = false;
                mTwoFingers = false;
                mTwoFingersDone = false;

                mGestureTask = new Task() {
                    @Override
                    public int go(boolean canceled) {
                        if (canceled || mTwoFingers || mStartMove != -1)
                            return Task.DONE;
                        mMap.handleGesture(Gesture.LONG_PRESS, e);
                        return Task.DONE;
                    }
                };
                mMap.postDelayed(mGestureTask, LONG_PRESS_THRESHOLD);
            }

            mPrevX1 = e.getX(0);
            mPrevY1 = e.getY(0);

            return true;
        }
        if (!mDown) {
            /* no down event received */
            return false;
        }

        if (action == MotionEvent.ACTION_MOVE) {
            onActionMove(e);
            return true;
        }
        if (action == MotionEvent.ACTION_UP) {
            mDown = false;
            if (mGestureTask != null) {
                mGestureTask.cancel();
                mGestureTask = null;
            }
            if (mStartMove > 0) {
                /* handle fling gesture */
                mScrollTracker.update(e.getX(), e.getY(), e.getTime());
                float vx = mScrollTracker.getVelocityX();
                float vy = mScrollTracker.getVelocityY();

                /* reduce velocity for short moves */
                float t = e.getTime() - mStartMove;
                if (t < FLING_MIN_THRESHOLD) {
                    t = t / FLING_MIN_THRESHOLD;
                    vy *= t * t;
                    vx *= t * t;
                }
                if (mEnableMove)
                    doFlingScroll(vx, vy);
            }

            if (Parameters.ANIMATOR2) {
                if (mRotateTracker.mNumSamples >= 0) {
                    mDoRotate = mCanRotate = false;
                    ((Animator2) mMap.animator()).animateFlingRotate(mRotateTracker.getVelocityX(), mPivotX, mPivotY);
                    mRotateTracker.mNumSamples = -1; // Reset tracker
                }
                if (mScaleTracker.mNumSamples >= 0) {
                    mDoScale = mCanScale = false;
                    ((Animator2) mMap.animator()).animateFlingZoom(mScaleTracker.getVelocityX(), mPivotX, mPivotY);
                    mScaleTracker.mNumSamples = -1; // Reset tracker
                }
            }

            if (time - mStartDown > LONG_PRESS_THRESHOLD) {
                // this was not a tap
                mTaps = 0;
                return true;
            }

            if (mTaps > 0) {
                if ((time - mLastTap.getTime()) >= DOUBLE_TAP_THRESHOLD) {
                    mTaps = 1;
                    mMap.handleGesture(Gesture.TAP, mLastTap);
                } else {
                    mTaps += 1;
                }
            } else {
                mTaps = 1;
            }

            if (mLastTap != null) {
                mLastTap.recycle();
            }
            mLastTap = e.copy();

            if (mTaps == 3) {
                mTaps = 0;
                mMap.handleGesture(Gesture.TRIPLE_TAP, e);
            } else if (mTaps == 2) {
                mGestureTask = new Task() {
                    @Override
                    public int go(boolean canceled) {
                        if (canceled)
                            return Task.DONE;
                        mTaps = 0;
                        if (mDragZoom)
                            return Task.DONE;
                        if (!mMap.handleGesture(Gesture.DOUBLE_TAP, e)) {
                            /* handle double tap zoom */
                            final float pivotX = mFixOnCenter ? 0 : mPrevX1 - mMap.getWidth() / 2;
                            final float pivotY = mFixOnCenter ? 0 : mPrevY1 - mMap.getHeight() / 2;
                            mMap.animator().animateZoom(300, 2, pivotX, pivotY);
                            if (CanvasAdapter.platform.isDesktop())
                                mMap.updateMap(true);
                        }
                        return Task.DONE;
                    }
                };
                mMap.postDelayed(mGestureTask, DOUBLE_TAP_THRESHOLD);
            } else {
                mGestureTask = new Task() {
                    @Override
                    public int go(boolean canceled) {
                        if (canceled)
                            return Task.DONE;
                        mTaps = 0;
                        if (!mTwoFingers && mStartMove == -1) {
                            mMap.handleGesture(Gesture.TAP, e);
                        }
                        return Task.DONE;
                    }
                };
                mMap.postDelayed(mGestureTask, DOUBLE_TAP_THRESHOLD);
            }
            return true;
        }
        if (action == MotionEvent.ACTION_CANCEL) {
            mTaps = 0;
            return false;
        }
        if (action == MotionEvent.ACTION_POINTER_DOWN) {
            mStartMove = -1;
            updateMulti(e);
            return true;
        }
        if (action == MotionEvent.ACTION_POINTER_UP) {
            if (e.getPointerCount() == 2 && !mTwoFingersDone) {
                if (!mMap.handleGesture(Gesture.TWO_FINGER_TAP, e)) {
                    mMap.animator().animateZoom(300, 0.5, 0f, 0f);
                }
            }
            updateMulti(e);
            return true;
        }

        return false;
    }

    private static int getAction(MotionEvent e) {
        return e.getAction() & MotionEvent.ACTION_MASK;
    }

    private void onActionMove(MotionEvent e) {
        ViewController mViewport = mMap.viewport();
        float x1 = e.getX(0);
        float y1 = e.getY(0);

        float mx = x1 - mPrevX1;
        float my = y1 - mPrevY1;

        float width = mMap.getWidth();
        float height = mMap.getHeight();

        if (e.getPointerCount() < 2) {
            mPrevX1 = x1;
            mPrevY1 = y1;

            /* double-tap drag zoom */
            if (mTaps == 1) {
                if (!mDragZoom && !isMinimalMove(mx, my)) {
                    mPrevX1 -= mx;
                    mPrevY1 -= my;
                    return;
                }

                // TODO limit scale properly
                mDragZoom = true;
                mViewport.scaleMap(1 + my / (height / 6), 0, 0);
                mMap.updateMap(true);
                mStartMove = -1;
                return;
            }

            /* simple move */
            if (!mEnableMove)
                return;

            if (mStartMove < 0) {
                if (!isMinimalMove(mx, my)) {
                    mPrevX1 -= mx;
                    mPrevY1 -= my;
                    return;
                }

                mStartMove = e.getTime();
                mScrollTracker.start(x1, y1, mStartMove);
                return;
            }
            mViewport.moveMap(mx, my);
            mScrollTracker.update(x1, y1, e.getTime());
            mMap.updateMap(true);
            if (mMap.viewport().getMapPosition(mapPosition))
                mMap.events.fire(Map.MOVE_EVENT, mapPosition);
            return;
        }
        mStartMove = -1;

        float x2 = e.getX(1);
        float y2 = e.getY(1);
        float dx = (x1 - x2);
        float dy = (y1 - y2);

        double rotateBy = 0;
        float scaleBy = 1;
        float tiltBy = 0;

        mx = ((x1 + x2) - (mPrevX1 + mPrevX2)) / 2;
        my = ((y1 + y2) - (mPrevY1 + mPrevY2)) / 2;

        if (mCanTilt) {
            float slope = (dx == 0) ? 0 : dy / dx;

            if (Math.abs(slope) < PINCH_TILT_SLOPE) {

                if (mDoTilt) {
                    tiltBy = my / 5;
                } else if (Math.abs(my) > (dpi / PINCH_TILT_THRESHOLD)) {
                    /* enter exclusive tilt mode */
                    mCanScale = false;
                    mCanRotate = false;
                    mDoTilt = true;
                    mTwoFingersDone = true;
                }
            }
        }

        double pinchWidth = Math.sqrt(dx * dx + dy * dy);
        double deltaPinch = pinchWidth - mPrevPinchWidth;

        if (mCanRotate) {
            double rad = Math.atan2(dy, dx);
            double r = rad - mAngle;

            if (mDoRotate) {
                double da = rad - mAngle;

                if (Math.abs(da) > 0.0001) {
                    rotateBy = da;
                    mAngle = rad;

                    deltaPinch = 0;

                    if (Parameters.ANIMATOR2) {
                        double clampedRotation = FastMath.clampRadian(rotateBy);
                        if (mRotateTracker.mNumSamples < 0)
                            mRotateTracker.start(mRotateTracker.mLastX + (float) clampedRotation, 0, e.getTime());
                        else
                            mRotateTracker.update(mRotateTracker.mLastX + (float) clampedRotation, 0, e.getTime());
                    }
                }
            } else {
                r = Math.abs(r);
                if (r > PINCH_ROTATE_THRESHOLD) {
                    /* start rotate, disable tilt */
                    mDoRotate = true;
                    mCanTilt = false;
                    mTwoFingersDone = true;

                    /*start from recognized position (smoother rotation)*/
                    mAngle = rad;
                } else if (!mDoScale) {
                    /* reduce pinch trigger by the amount of rotation */
                    deltaPinch *= 1 - (r / PINCH_ROTATE_THRESHOLD);
                } else {
                    mPrevPinchWidth = pinchWidth;
                }
            }
        } else if (mDoScale && mEnableRotate) {
            /* re-enable rotation when higher threshold is reached */
            double rad = Math.atan2(dy, dx);
            double r = rad - mAngle;

            if (r > PINCH_ROTATE_THRESHOLD2) {
                /* start rotate again */
                mDoRotate = true;
                mCanRotate = true;
                mTwoFingersDone = true;

                /*start from recognized position (smoother rotation)*/
                mAngle = rad;
            }
        }

        if (mCanScale || mDoRotate) {
            if (!(mDoScale || mDoRotate)) {
                /* enter exclusive scale mode */
                if (Math.abs(deltaPinch) > (dpi / PINCH_ZOOM_THRESHOLD)) {

                    if (!mDoRotate) {
                        mPrevPinchWidth = pinchWidth;
                        mCanRotate = false;
                    }

                    mCanTilt = false;
                    mDoScale = true;
                    mTwoFingersDone = true;
                }
            }
            if (mDoScale || mDoRotate) {
                scaleBy = (float) (pinchWidth / mPrevPinchWidth);
                mPrevPinchWidth = pinchWidth;

                if (Parameters.ANIMATOR2) {
                    if (scaleBy != 1f) {
                        if (mScaleTracker.mNumSamples < 0)
                            mScaleTracker.start((float) pinchWidth, 0, e.getTime());
                        else
                            mScaleTracker.update((float) pinchWidth, 0, e.getTime());
                    }
                }
            }
        }

        if (!(mDoRotate || mDoScale || mDoTilt))
            return;

        if (!mFixOnCenter) {
            mPivotX = (x2 + x1) / 2 - width / 2;
            mPivotY = (y2 + y1) / 2 - height / 2;
        }

        synchronized (mViewport) {
            if (!mDoTilt) {
                if (rotateBy != 0)
                    mViewport.rotateMap(rotateBy, mPivotX, mPivotY);
                if (scaleBy != 1)
                    mViewport.scaleMap(scaleBy, mPivotX, mPivotY);

                if (!mFixOnCenter)
                    mViewport.moveMap(mx, my);
            } else {
                if (tiltBy != 0 && mViewport.tiltMap(-tiltBy))
                    mViewport.moveMap(0, my / 2);
            }
        }

        mPrevX1 = x1;
        mPrevY1 = y1;
        mPrevX2 = x2;
        mPrevY2 = y2;

        mMap.updateMap(true);

        if (mMap.viewport().getMapPosition(mapPosition)) {
            if (mDoScale)
                mMap.events.fire(Map.SCALE_EVENT, mapPosition);
            if (mDoRotate)
                mMap.events.fire(Map.ROTATE_EVENT, mapPosition);
            if (mDoTilt)
                mMap.events.fire(Map.TILT_EVENT, mapPosition);
        }
    }

    private void updateMulti(MotionEvent e) {
        int cnt = e.getPointerCount();

        mPrevX1 = e.getX(0);
        mPrevY1 = e.getY(0);

        if (cnt == 2) {
            mTwoFingers = true;

            mDoScale = false;
            mDoRotate = false;
            mDoTilt = false;
            mCanScale = mEnableScale;
            mCanRotate = mEnableRotate;
            mCanTilt = mEnableTilt;

            mPrevX2 = e.getX(1);
            mPrevY2 = e.getY(1);
            double dx = mPrevX1 - mPrevX2;
            double dy = mPrevY1 - mPrevY2;

            mAngle = Math.atan2(dy, dx);
            mPrevPinchWidth = Math.sqrt(dx * dx + dy * dy);
        }
    }

    private boolean isDoubleTap(float mx, float my) {
        float minSlop = (dpi / DOUBLE_TAP_SLOP);
        return withinSquaredDist(mx, my, minSlop * minSlop);
    }

    private boolean isMinimalMove(float mx, float my) {
        float minSlop = (dpi / MIN_SLOP);
        return !withinSquaredDist(mx, my, minSlop * minSlop);
    }

    private boolean doFlingScroll(float velocityX, float velocityY) {

        int w = Tile.SIZE * 5;
        int h = Tile.SIZE * 5;

        if (Parameters.ANIMATOR2) {
            if (!CanvasAdapter.platform.isDesktop() && CanvasAdapter.platform != Platform.WEBGL) {
                velocityX *= 2;
                velocityY *= 2;
            }
            ((Animator2) mMap.animator()).animateFlingScroll(velocityX, velocityY, -w, w, -h, h);
        } else
            mMap.animator().animateFling(velocityX * 2, velocityY * 2, -w, w, -h, h);
        return true;
    }

    private class VelocityTracker {
        /* sample window, 200ms */
        private static final int MAX_MS = 200;
        private static final int SAMPLES = 32;

        private float mLastX, mLastY;
        private long mLastTime;
        private int mNumSamples;
        private int mIndex;

        private float[] mMeanX = new float[SAMPLES];
        private float[] mMeanY = new float[SAMPLES];
        private int[] mMeanTime = new int[SAMPLES];

        public void start(float x, float y, long time) {
            mLastX = x;
            mLastY = y;
            mNumSamples = 0;
            mIndex = SAMPLES;
            mLastTime = time;
        }

        public void update(float x, float y, long time) {
            if (time == mLastTime)
                return;

            if (--mIndex < 0)
                mIndex = SAMPLES - 1;

            mMeanX[mIndex] = x - mLastX;
            mMeanY[mIndex] = y - mLastY;
            mMeanTime[mIndex] = (int) (time - mLastTime);

            mLastTime = time;
            mLastX = x;
            mLastY = y;

            mNumSamples++;
        }

        private float getVelocity(float[] move) {
            mNumSamples = Math.min(SAMPLES, mNumSamples);

            double duration = 0;
            double amount = 0;

            for (int c = 0; c < mNumSamples; c++) {
                int index = (mIndex + c) % SAMPLES;

                float d = mMeanTime[index];
                if (c > 0 && duration + d > MAX_MS)
                    break;

                duration += d;
                amount += move[index] * (d / duration);
            }

            if (duration == 0)
                return 0;

            return (float) ((amount * 1000) / duration);
        }

        float getVelocityY() {
            return getVelocity(mMeanY);
        }

        float getVelocityX() {
            return getVelocity(mMeanX);
        }

        @Override
        public String toString() {
            return "VelocityX: " + getVelocityX()
                    + "\tVelocityY: " + getVelocityY()
                    + "\tNumSamples: " + mNumSamples;
        }
    }
}
