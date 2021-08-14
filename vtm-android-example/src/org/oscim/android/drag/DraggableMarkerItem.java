package org.oscim.android.drag;

import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.MarkerItem;

public class DraggableMarkerItem extends MarkerItem implements Draggable {

    private boolean draggable;
    private final DragAndDropListener dragAndDropListener;

    public DraggableMarkerItem(final String title,
                               final String description,
                               final GeoPoint geoPoint,
                               final boolean draggable,
                               final DragAndDropListener dragAndDropListener) {
        super(title, description, geoPoint);
        this.draggable = draggable;
        this.dragAndDropListener = dragAndDropListener;
    }

    @Override
    public void setDraggable(final boolean draggable) {
        this.draggable = draggable;
    }

    @Override
    public boolean isDraggable() {
        return draggable;
    }

    DragAndDropListener getDragAndDropListener() {
        return dragAndDropListener;
    }
}
