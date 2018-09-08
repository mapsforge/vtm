/*
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
package org.oscim.gdx;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

import org.oscim.core.Tile;
import org.oscim.map.Animator2;
import org.oscim.map.Map;
import org.oscim.utils.Parameters;

public class GestureHandler implements GestureListener {
    private boolean mayFling = true;

    private boolean mPinch;

    private boolean mBeginScale;
    private float mSumScale;
    private float mSumRotate;

    private boolean mBeginRotate;
    private boolean mBeginTilt;

    private float mPrevX;
    private float mPrevY;

    private float mPrevX2;
    private float mPrevY2;

    private float mFocusX;
    private float mFocusY;

    private double mAngle;
    protected double mPrevPinchWidth = -1;

    protected static final int JUMP_THRESHOLD = 100;
    protected static final double PINCH_ZOOM_THRESHOLD = 5;
    protected static final double PINCH_ROTATE_THRESHOLD = 0.02;
    protected static final float PINCH_TILT_THRESHOLD = 1f;

    //private ViewController mViewport;
    private final Map mMap;

    public GestureHandler(Map map) {
        //mViewport = mMap.viewport();
        mMap = map;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        mayFling = true;
        mPinch = false;

        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(final float velocityX, final float velocityY,
                         int button) {
        //log.debug("fling " + button + " " + velocityX + "/" + velocityY);
        if (mayFling && button == Buttons.LEFT) {
            if (Parameters.ANIMATOR2)
                ((Animator2) mMap.animator()).animateFlingScroll(velocityX, velocityY);
            else {
                int m = Tile.SIZE * 4;
                mMap.animator().animateFling(velocityX, velocityY, -m, m, -m, m);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        if (mPinch)
            return true;

        mMap.viewport().moveMap(deltaX, deltaY);
        mMap.updateMap(true);

        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
                         Vector2 pointer1, Vector2 pointer2) {
        mayFling = false;

        if (!mPinch) {
            mPrevX = pointer1.x;
            mPrevY = pointer1.y;
            mPrevX2 = pointer2.x;
            mPrevY2 = pointer2.y;

            double dx = mPrevX - mPrevX2;
            double dy = mPrevY - mPrevY2;

            mAngle = Math.atan2(dy, dx);
            mPrevPinchWidth = Math.sqrt(dx * dx + dy * dy);

            mPinch = true;

            mBeginTilt = false;
            mBeginRotate = false;
            mBeginScale = false;

            return true;
        }

        float x1 = pointer1.x;
        float y1 = pointer1.y;

        //float mx = x1 - mPrevX;
        float my = y1 - mPrevY;

        float x2 = pointer2.x;
        float y2 = pointer2.y;

        float dx = (x1 - x2);
        float dy = (y1 - y2);
        float slope = 0;

        if (dx != 0)
            slope = dy / dx;

        double pinchWidth = Math.sqrt(dx * dx + dy * dy);

        final double deltaPinchWidth = pinchWidth - mPrevPinchWidth;

        double rad = Math.atan2(dy, dx);
        double r = rad - mAngle;

        boolean startScale = (Math.abs(deltaPinchWidth) > PINCH_ZOOM_THRESHOLD);

        boolean changed = false;

        if (!mBeginTilt && (mBeginScale || startScale)) {
            mBeginScale = true;

            float scale = (float) (pinchWidth / mPrevPinchWidth);

            // decrease change of scale by the change of rotation
            // * 20 is just arbitrary
            if (mBeginRotate)
                scale = 1 + ((scale - 1) * Math.max((1 - (float) Math.abs(r) * 20), 0));

            mSumScale *= scale;

            if ((mSumScale < 0.99 || mSumScale > 1.01)
                    && mSumRotate < Math.abs(0.02))
                mBeginRotate = false;

            float fx = (x2 + x1) / 2 - mMap.getWidth() / 2;
            float fy = (y2 + y1) / 2 - mMap.getHeight() / 2;

            // log.debug("zoom " + deltaPinchWidth + " " + scale + " " +
            // mSumScale);
            changed = mMap.viewport().scaleMap(scale, fx, fy);
        }

        if (!mBeginRotate && Math.abs(slope) < 1) {
            float my2 = y2 - mPrevY2;
            float threshold = PINCH_TILT_THRESHOLD;

            // log.debug(r + " " + slope + " m1:" + my + " m2:" + my2);

            if ((my > threshold && my2 > threshold)
                    || (my < -threshold && my2 < -threshold)) {
                mBeginTilt = true;
                changed = mMap.viewport().tiltMap(my / 5);
            }
        }

        if (!mBeginTilt
                && (mBeginRotate || (Math.abs(slope) > 1 && Math.abs(r) > PINCH_ROTATE_THRESHOLD))) {
            // log.debug("rotate: " + mBeginRotate + " " +
            // Math.toDegrees(rad));
            if (!mBeginRotate) {
                mAngle = rad;

                mSumScale = 1;
                mSumRotate = 0;

                mBeginRotate = true;

                mFocusX = (x1 + x2) / 2 - (mMap.getWidth() / 2);
                mFocusY = (y1 + y2) / 2 - (mMap.getHeight() / 2);
            } else {
                double da = rad - mAngle;
                mSumRotate += da;

                if (Math.abs(da) > 0.001) {
                    double rsin = Math.sin(r);
                    double rcos = Math.cos(r);
                    float x = (float) (mFocusX * rcos + mFocusY * -rsin - mFocusX);
                    float y = (float) (mFocusX * rsin + mFocusY * rcos - mFocusY);

                    mMap.viewport().rotateMap(da, x, y);
                    changed = true;
                }
            }
            mAngle = rad;
        }

        if (changed) {
            mMap.updateMap(true);
            mPrevPinchWidth = pinchWidth;
            mPrevY2 = y2;

        }

        mPrevX = x1;
        mPrevY = y1;
        mPrevX2 = x2;

        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public void pinchStop() {
    }
}
