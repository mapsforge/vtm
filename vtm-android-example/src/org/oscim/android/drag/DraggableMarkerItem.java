package org.oscim.android.drag;

import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.MarkerItem;

public class DraggableMarkerItem extends MarkerItem {

    private final DragAndDropListener dragAndDropListener;

    public DraggableMarkerItem(final String title,
                               final String description,
                               final GeoPoint geoPoint,
                               final DragAndDropListener dragAndDropListener) {
        super(title, description, geoPoint);
        this.dragAndDropListener = dragAndDropListener;
    }

    DragAndDropListener getDragAndDropListener() {
        return dragAndDropListener;
    }
}
