/*
 * Copyright 2013 Hannes Janetzek
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
package org.oscim.android.input;

import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

import org.oscim.event.Gesture;
import org.oscim.map.Map;

public class GestureHandler implements OnGestureListener, OnDoubleTapListener {
    private final AndroidMotionEvent mMotionEvent;
    private final Map mMap;

    // Quick scale (double tap + swipe)
    protected boolean quickScale;

    public GestureHandler(Map map) {
        mMotionEvent = new AndroidMotionEvent();
        mMap = map;
    }

    /* OnGestureListener */

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // Quick scale (no long press)
        if (quickScale)
            return;

        mMap.handleGesture(Gesture.LONG_PRESS, mMotionEvent.wrap(e));
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        quickScale = false;

        return mMap.handleGesture(Gesture.PRESS, mMotionEvent.wrap(e));
    }

    /* OnDoubleTapListener */

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return mMap.handleGesture(Gesture.TAP, mMotionEvent.wrap(e));
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        int action = e.getActionMasked();

        // Quick scale
        quickScale = (action == MotionEvent.ACTION_MOVE);

        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return mMap.handleGesture(Gesture.DOUBLE_TAP, mMotionEvent.wrap(e));
    }
}
