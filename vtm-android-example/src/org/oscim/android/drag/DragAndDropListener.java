package org.oscim.android.drag;

import org.oscim.core.GeoPoint;

public interface DragAndDropListener {

    void startDragItemAtGeoPoint(DraggableMarkerItem item, final GeoPoint geoPoint);

    void ongoingDragItemToGeoPoint(DraggableMarkerItem item, final GeoPoint geoPoint);

    void dropItemAtGeoPoint(DraggableMarkerItem item, final GeoPoint geoPoint);
}
