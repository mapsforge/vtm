package org.oscim.android.drag;

import static org.oscim.android.drag.DragGestureHandler.END_DRAG;
import static org.oscim.android.drag.DragGestureHandler.ONGOING_DRAG;
import static org.oscim.android.drag.DragGestureHandler.START_DRAG;

import org.oscim.core.GeoPoint;
import org.oscim.event.Gesture;
import org.oscim.event.GestureListener;
import org.oscim.event.MotionEvent;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerInterface;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.map.Map;

import java.util.List;

public class DraggableItemizedLayer extends ItemizedLayer implements GestureListener {

    private final ItemDragger itemDragger;

    public DraggableItemizedLayer(final Map map,
                                  final List<MarkerInterface> markerItems,
                                  final MarkerSymbol defaultMarker,
                                  final OnItemGestureListener<MarkerInterface> listener) {
        super(map, markerItems, defaultMarker, listener);
        itemDragger =
                new ItemDragger(
                        this,
                        new LocationUIUpdater(this, mMap));
    }

    @Override
    protected boolean activateSelectedItems(final MotionEvent event, final ActiveItem task) {
        return super.activateSelectedItems(event, task);
    }

    protected List<MarkerInterface> getMarkerItems() {
        return mItemList;
    }

    @Override
    public boolean onGesture(final Gesture gesture, final MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        if (gesture == START_DRAG) {
            return itemDragger.startDragItem(event, getGeoPoint(event));
        } else if (gesture == ONGOING_DRAG) {
            return itemDragger.ongoingDragItemTo(getGeoPoint(event));
        } else if (gesture == END_DRAG) {
            return itemDragger.dropItemAt(getGeoPoint(event));
        } else {
            itemDragger.noDrag();
        }

        return super.onGesture(gesture, event);
    }

    private GeoPoint getGeoPoint(final MotionEvent event) {
        return map().viewport().fromScreenPoint(event.getX(), event.getY());
    }
}
