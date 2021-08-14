package org.oscim.android.drag;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;

import org.oscim.android.MapView;
import org.oscim.map.Map;
import org.oscim.utils.Parameters;

public class DragMapView extends MapView {

    private DragGestureHandler gestureHandler;

    public DragMapView(final Context context) {
        super(context);
    }

    public DragMapView(final Context context, final AttributeSet attributeSet) {
        super(context, attributeSet);
        if (!Parameters.MAP_EVENT_LAYER2) {
            gestureHandler = new DragGestureHandler(mMap);
            mGestureDetector = new GestureDetector(context, gestureHandler);
            mGestureDetector.setOnDoubleTapListener(gestureHandler);
        }
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent motionEvent) {
        if (!isClickable()) {
            return false;
        }

        if (motionEvent.getAction() == android.view.MotionEvent.ACTION_UP) {
            if (gestureHandler.isScrolling()) {
                gestureHandler.setScrolling(false);
                return ((Map) mMap).handleGesture(DragGestureHandler.END_DRAG, mMotionEvent.wrap(motionEvent));
            }
        }

        return super.onTouchEvent(motionEvent);
    }
}
