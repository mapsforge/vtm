package org.oscim.android.drag;

import org.oscim.core.GeoPoint;
import org.oscim.event.MotionEvent;
import org.oscim.layers.marker.ItemizedLayer;

class ItemDragger {

    private final DraggableItemizedLayer draggableItemizedLayer;
    private final LocationUIUpdater locationUIUpdater;
    private DraggableMarkerItem dragItem;

    public ItemDragger(final DraggableItemizedLayer draggableItemizedLayer,
                       final LocationUIUpdater locationUIUpdater) {
        this.draggableItemizedLayer = draggableItemizedLayer;
        this.locationUIUpdater = locationUIUpdater;
    }

    public boolean startDragItem(final MotionEvent event, final GeoPoint geoPoint) {
        dragItem = null;
        return draggableItemizedLayer.activateSelectedItems(
                event,
                new ItemizedLayer.ActiveItem() {
                    @Override
                    public boolean run(final int index) {
                        dragItem = (DraggableMarkerItem) draggableItemizedLayer.getMarkerItems().get(index);
                        if (!dragItem.isDraggable()) {
                            dragItem = null;
                            return false;
                        }
                        dragItem.getDragAndDropListener().startDragItemAtGeoPoint(dragItem, geoPoint);
                        locationUIUpdater.update(dragItem, geoPoint);
                        return true;
                    }
                });
    }

    public boolean ongoingDragItemTo(final GeoPoint geoPoint) {
        if (dragItem == null) {
            return false;
        }
        dragItem.getDragAndDropListener().ongoingDragItemToGeoPoint(dragItem, geoPoint);
        locationUIUpdater.update(dragItem, geoPoint);
        return true;
    }

    public boolean dropItemAt(final GeoPoint geoPoint) {
        if (dragItem == null) {
            return false;
        }
        dragItem.getDragAndDropListener().dropItemAtGeoPoint(dragItem, geoPoint);
        locationUIUpdater.update(dragItem, geoPoint);
        return true;
    }

    public void noDrag() {
        dragItem = null;
    }
}
