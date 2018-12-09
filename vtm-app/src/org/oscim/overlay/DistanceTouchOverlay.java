/*
 * Copyright 2013 Ahmad Saleem
 * Copyright 2013 Hannes Janetzek
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

package org.oscim.overlay;

import org.oscim.core.GeoPoint;
import org.oscim.event.Event;
import org.oscim.event.Gesture;
import org.oscim.event.GestureListener;
import org.oscim.event.MotionEvent;
import org.oscim.layers.Layer;
import org.oscim.map.Map;
import org.osmdroid.overlays.MapEventsReceiver;

import java.util.Timer;
import java.util.TimerTask;

public class DistanceTouchOverlay extends Layer implements Map.InputListener,
        GestureListener {

    private static final int LONGPRESS_THRESHOLD = 800;

    private Timer mLongpressTimer;

    private float mPrevX1, mPrevX2, mPrevY1, mPrevY2;
    private float mCurX1, mCurX2, mCurY1, mCurY2;

    // private static final int POINTER_UP = -1;
    // private int mPointer1 = POINTER_UP;
    // private int mPointer2 = POINTER_UP;

    private final MapEventsReceiver mReceiver;

    /**
     * @param map      the Map
     * @param receiver the object that will receive/handle the events. It must
     *                 implement MapEventsReceiver interface.
     */
    public DistanceTouchOverlay(Map map, MapEventsReceiver receiver) {
        super(map);
        mReceiver = receiver;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void cancel() {

        if (mLongpressTimer != null) {
            mLongpressTimer.cancel();
            mLongpressTimer = null;
        }
    }

    @Override
    public void onInputEvent(Event event, MotionEvent e) {

        int action = e.getAction() & MotionEvent.ACTION_MASK;

        if ((action == MotionEvent.ACTION_CANCEL)) {
            cancel();
            return;
        }

        if (mLongpressTimer != null) {
            // any pointer up while long press detection
            // cancels timer
            if (action == MotionEvent.ACTION_POINTER_UP
                    || action == MotionEvent.ACTION_UP) {

                cancel();
                return;
            }

            // two fingers must still be down, tested
            // one above.
            if (action == MotionEvent.ACTION_MOVE) {
                // update pointer positions
                // int idx1 = e.findPointerIndex(mPointer1);
                // int idx2 = e.findPointerIndex(mPointer2);

                mCurX1 = e.getX(0);
                mCurY1 = e.getY(0);
                mCurX2 = e.getX(1);
                mCurY2 = e.getY(1);

                // cancel if moved one finger more than 50 pixel
                float maxSq = 10 * 10;
                float d = (mCurX1 - mPrevX1) * (mCurX1 - mPrevX1)
                        + (mCurY1 - mPrevY1) * (mCurY1 - mPrevY1);
                if (d > maxSq) {
                    cancel();
                    return;
                }
                d = (mCurX2 - mPrevX2) * (mCurX2 - mPrevX2)
                        + (mCurY2 - mPrevY2) * (mCurY2 - mPrevY2);
                if (d > maxSq) {
                    cancel();
                    return;
                }
            }
        }

        if ((action == MotionEvent.ACTION_POINTER_DOWN)
                && (e.getPointerCount() == 2)) {
            // App.log.debug("down");

            // keep track of pointer ids, only
            // use these for gesture, ignoring
            // more than two pointer

            // mPointer1 = e.getPointerId(0);
            // mPointer2 = e.getPointerId(1);

            if (mLongpressTimer == null) {
                // start timer, keep initial down position
                mCurX1 = mPrevX1 = e.getX(0);
                mCurY1 = mPrevY1 = e.getY(0);
                mCurX2 = mPrevX2 = e.getX(1);
                mCurY2 = mPrevY2 = e.getY(1);
                runLongpressTimer();
            }
        }
    }

    // @Override
    // public boolean onLongPress(MotionEvent e) {
    // // dont forward long press when two fingers are down.
    // // maybe should be only done if our timer is still running.
    // // ... not sure if this is even needed
    // GeoPoint p = mMap.getViewport().fromScreenPoint(e.getX(), e.getY());
    // return mReceiver.longPressHelper(p);
    // }

    public void runLongpressTimer() {
        // mMap.postDelayed(action, delay);

        mLongpressTimer = new Timer();
        mLongpressTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                final GeoPoint p1 = mMap.viewport().fromScreenPoint(mCurX1,
                        mCurY1);
                final GeoPoint p2 = mMap.viewport().fromScreenPoint(mCurX2,
                        mCurY2);

                mMap.post(new Runnable() {
                    @Override
                    public void run() {
                        mReceiver.longPressHelper(p1, p2);
                    }
                });
            }
        }, LONGPRESS_THRESHOLD);
    }

    @Override
    public boolean onGesture(Gesture g, MotionEvent e) {
        if (g instanceof Gesture.LongPress) {
            GeoPoint p = mMap.viewport().fromScreenPoint(e.getX(), e.getY());
            return mReceiver.longPressHelper(p);
        }
        return false;
    }

}
