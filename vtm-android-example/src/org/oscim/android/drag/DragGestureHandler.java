package org.oscim.android.drag;

import android.view.MotionEvent;

import org.oscim.android.input.AndroidMotionEvent;
import org.oscim.android.input.GestureHandler;
import org.oscim.event.Gesture;
import org.oscim.map.Map;

class DragGestureHandler extends GestureHandler {

    public static final Gesture START_DRAG = new Gesture() {
    };
    public static final Gesture ONGOING_DRAG = new Gesture() {
    };
    public static Gesture END_DRAG = new Gesture() {
    };

    private final AndroidMotionEvent mMotionEvent;
    private final Map mMap;
    private boolean scrolling = false;

    public DragGestureHandler(final Map map) {
        super(map);
        mMotionEvent = new AndroidMotionEvent();
        mMap = map;
    }

    public boolean isScrolling() {
        return scrolling;
    }

    public void setScrolling(final boolean scrolling) {
        this.scrolling = scrolling;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        scrolling = true;
        mMap.handleGesture(START_DRAG, mMotionEvent.wrap(e));
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        scrolling = true;
        return mMap.handleGesture(ONGOING_DRAG, mMotionEvent.wrap(e2));
    }
}
