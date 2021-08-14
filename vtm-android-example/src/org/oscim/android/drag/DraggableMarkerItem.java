package org.oscim.android.drag;

import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.MarkerSymbol;

public class DraggableMarkerItem extends DisableableMarkerItem implements Draggable, DisableableAndDraggable {

    private boolean draggable;
    private final DragAndDropListener dragAndDropListener;

    public DraggableMarkerItem(final Object uid,
                               final String title,
                               final String description,
                               final GeoPoint geoPoint,
                               // FK-TODO: enabledMarker und disabledMarker in einer neuen Klasse zusammenfassen
                               final MarkerSymbol enabledMarker,
                               final MarkerSymbol disabledMarker,
                               final boolean draggable,
                               final DragAndDropListener dragAndDropListener) {
        super(uid, title, description, geoPoint, enabledMarker, disabledMarker);
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
