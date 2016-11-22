/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 devemux86
 * Copyright 2016 Andrey Novikov
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

import org.oscim.core.MapPosition;
import org.oscim.core.Tile;
import org.oscim.event.Event;
import org.oscim.event.Gesture;
import org.oscim.event.MotionEvent;
import org.oscim.map.Map;
import org.oscim.map.Map.InputListener;
import org.oscim.map.ViewController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

import static org.oscim.backend.CanvasAdapter.dpi;
import static org.oscim.utils.FastMath.withinSquaredDist;

/**
 * Changes Viewport by handling move, fling, scale, rotation and tilt gestures.
 * <p/>
 * TODO rewrite using gesture primitives to build more complex gestures:
 * maybe something similar to this https://github.com/ucbvislab/Proton
 */
public class MapEventLayer extends Layer implements InputListener {

    private static final Logger log = LoggerFactory.getLogger(MapEventLayer.class);

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
    private long mLastTap;

    private float mPrevX1;
    private float mPrevY1;
    private float mPrevX2;
    private float mPrevY2;

    private double mAngle;
    private double mPrevPinchWidth;
    private long mStartMove;

    /**
     * 2mm as minimal distance to start move: dpi / 25.4
     */
    private static final float MIN_SLOP = 25.4f / 2;

    private static final float PINCH_ZOOM_THRESHOLD = MIN_SLOP / 2;
    private static final float PINCH_TILT_THRESHOLD = MIN_SLOP / 2;
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

    private final VelocityTracker mTracker;
    private final Timer mTimer;
    private TimerTask mTimerTask;

    private final MapPosition mapPosition = new MapPosition();

    public MapEventLayer(Map map) {
        super(map);
        mTracker = new VelocityTracker();
        mTimer = new Timer();
    }

    @Override
    public void onDetach() {
        mTimer.cancel();
        mTimer.purge();
    }

    @Override
    public void onInputEvent(Event e, MotionEvent motionEvent) {
        if (motionEvent.getAction() != MotionEvent.ACTION_MOVE)
            log.error("{} {}", motionEvent.getX(), motionEvent.getY());
        onTouchEvent(motionEvent);
    }

    public void enableRotation(boolean enable) {
        mEnableRotate = enable;
    }

    public boolean rotationEnabled() {
        return mEnableRotate;
    }

    public void enableTilt(boolean enable) {
        mEnableTilt = enable;
    }

    public boolean tiltEnabled() {
        return mEnableTilt;
    }

    public void enableMove(boolean enable) {
        mEnableMove = enable;
    }

    public boolean moveEnabled() {
        return mEnableMove;
    }

    public void enableZoom(boolean enable) {
        mEnableScale = enable;
    }

    public boolean zoomEnabled() {
        return mEnableScale;
    }

    /**
     * When enabled zoom- and rotation-gestures will not move the viewport.
     */
    public void setFixOnCenter(boolean enable) {
        mFixOnCenter = enable;
    }

    boolean onTouchEvent(final MotionEvent e) {
        int action = getAction(e);
        final long time = e.getTime();

        if (action == MotionEvent.ACTION_DOWN) {
            if (mTimerTask != null) {
                mTimerTask.cancel();
                mTimer.purge();
                mTimerTask = null;
            }
            mMap.handleGesture(Gesture.PRESS, e);
            mDown = true;
            mStartDown = time;
            if (mTaps == 0) {
                mMap.animator().cancel();

                mStartMove = -1;
                mDragZoom = false;
                mTwoFingers = false;

                mPrevX1 = e.getX(0);
                mPrevY1 = e.getY(0);

                mDown = true;
                mTaps = 0;

                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (mTwoFingers || mStartMove != -1)
                            return;
                        mMap.post(new Runnable() {
                            @Override
                            public void run() {
                                log.debug("long press {} {}", e.getX(), e.getY());
                                mMap.handleGesture(Gesture.LONG_PRESS, e);
                            }
                        });
                    }
                };
                mTimer.schedule(mTimerTask, LONG_PRESS_THRESHOLD);
            }
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
            if (mTimerTask != null) {
                mTimerTask.cancel();
                mTimer.purge();
                mTimerTask = null;
            }
            if (mStartMove > 0) {
                /* handle fling gesture */
                mTracker.update(e.getX(), e.getY(), e.getTime());
                float vx = mTracker.getVelocityX();
                float vy = mTracker.getVelocityY();

                /* reduce velocity for short moves */
                float t = e.getTime() - mStartMove;
                if (t < FLING_MIN_THRESHOLD) {
                    t = t / FLING_MIN_THRESHOLD;
                    vy *= t * t;
                    vx *= t * t;
                }
                doFling(vx, vy);
            }

            if (time - mStartDown > FLING_MIN_THRESHOLD) {
                // this was not a tap
                mTaps = 0;
                return true;
            }

            if (mTaps > 0 && (time - mLastTap) < DOUBLE_TAP_THRESHOLD) {
                mTaps += 1;
            } else {
                mTaps = 1;
            }

            mLastTap = time;

            if (mTaps == 3) {
                mTaps = 0;
                log.debug("triple tap {} {}", e.getX(), e.getY());
                mMap.handleGesture(Gesture.TRIPLE_TAP, e);
            } else if (mTaps == 2) {
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        mTaps = 0;
                        if (mDragZoom)
                            return;
                        mMap.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!mMap.handleGesture(Gesture.DOUBLE_TAP, e)) {
                                    /* handle double tap zoom */
                                    log.debug("execute double tap {} {}", e.getX(), e.getY());
                                    final float pivotX = mFixOnCenter ? 0 : mPrevX1 - mMap.getWidth() / 2;
                                    final float pivotY = mFixOnCenter ? 0 : mPrevY1 - mMap.getHeight() / 2;
                                    mMap.animator().animateZoom(300, 2, pivotX, pivotY);
                                }
                            }
                        });
                    }
                };
                mTimer.schedule(mTimerTask, DOUBLE_TAP_THRESHOLD);
            } else {
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        mTaps = 0;
                        if (mTwoFingers) {
                            if (mTwoFingersDone)
                                return;
                            mMap.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (!mMap.handleGesture(Gesture.TWO_FINGER_TAP, e)) {
                                        log.debug("execute two finger tap");
                                        mMap.animator().animateZoom(300, 0.5, 0f, 0f);
                                    }
                                }
                            });
                        } else if (mStartMove == -1) {
                            mMap.post(new Runnable() {
                                @Override
                                public void run() {
                                    log.debug("tap {} {}", e.getX(), e.getY());
                                    mMap.handleGesture(Gesture.TAP, e);
                                }
                            });
                        }
                    }
                };
                mTimer.schedule(mTimerTask, DOUBLE_TAP_THRESHOLD);
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
                mTracker.start(x1, y1, mStartMove);
                return;
            }
            mViewport.moveMap(mx, my);
            mTracker.update(x1, y1, e.getTime());
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
                }
            } else {
                r = Math.abs(r);
                if (r > PINCH_ROTATE_THRESHOLD) {
                    /* start rotate, disable tilt */
                    mDoRotate = true;
                    mCanTilt = false;
                    mTwoFingersDone = true;

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
                mAngle = rad;
                mTwoFingersDone = true;
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
            }
        }

        if (!(mDoRotate || mDoScale || mDoTilt))
            return;

        float pivotX = 0, pivotY = 0;

        if (!mFixOnCenter) {
            pivotX = (x2 + x1) / 2 - width / 2;
            pivotY = (y2 + y1) / 2 - height / 2;
        }

        synchronized (mViewport) {
            if (!mDoTilt) {
                if (rotateBy != 0)
                    mViewport.rotateMap(rotateBy, pivotX, pivotY);
                if (scaleBy != 1)
                    mViewport.scaleMap(scaleBy, pivotX, pivotY);

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
    }

    private void updateMulti(MotionEvent e) {
        int cnt = e.getPointerCount();

        mPrevX1 = e.getX(0);
        mPrevY1 = e.getY(0);

        if (cnt == 2) {
            mTwoFingers = true;
            mTwoFingersDone = false;

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

    private boolean isMinimalMove(float mx, float my) {
        float minSlop = (dpi / MIN_SLOP);
        return !withinSquaredDist(mx, my, minSlop * minSlop);
    }

    private boolean doFling(float velocityX, float velocityY) {

        int w = Tile.SIZE * 5;
        int h = Tile.SIZE * 5;

        mMap.animator().animateFling(velocityX * 2, velocityY * 2,
                -w, w, -h, h);
        return true;
    }

    private static class VelocityTracker {
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
    }
}
