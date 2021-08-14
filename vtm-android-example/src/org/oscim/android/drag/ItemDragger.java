package org.oscim.android.drag;

import org.oscim.core.GeoPoint;
import org.oscim.event.MotionEvent;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerLayer;
import org.oscim.map.Map;

class ItemDragger {

    private final DraggableItemizedLayer draggableItemizedLayer;
    private final DragAndDropListener dragItemAndRedrawListener;
    private DraggableMarkerItem dragItem;

    public ItemDragger(final DraggableItemizedLayer draggableItemizedLayer, final Map map) {
        this.draggableItemizedLayer = draggableItemizedLayer;
        dragItemAndRedrawListener = createDragItemAndRedrawListener(draggableItemizedLayer, map);
    }

    public boolean startDragItem(final MotionEvent event, final GeoPoint geoPoint) {
        dragItem = null;
        return draggableItemizedLayer.activateSelectedItems(
                event,
                new ItemizedLayer.ActiveItem() {
                    @Override
                    public boolean run(final int index) {
                        dragItem = (DraggableMarkerItem) draggableItemizedLayer.getMarkerItems().get(index);
                        dragItemAndRedrawListener.startDragItemAtGeoPoint(dragItem, geoPoint);
                        return true;
                    }
                });
    }

    public boolean ongoingDragItemTo(final GeoPoint geoPoint) {
        if (dragItem == null) {
            return false;
        }
        dragItemAndRedrawListener.ongoingDragItemToGeoPoint(dragItem, geoPoint);
        return true;
    }

    public boolean dropItemAt(final GeoPoint geoPoint) {
        if (dragItem == null) {
            return false;
        }
        dragItemAndRedrawListener.dropItemAtGeoPoint(dragItem, geoPoint);
        return true;
    }

    public void noDrag() {
        dragItem = null;
    }

    private DragAndDropListener createDragItemAndRedrawListener(final MarkerLayer markerLayer,
                                                                final Map map) {
        return new DragAndDropListener() {

            @Override
            public void startDragItemAtGeoPoint(final DraggableMarkerItem item, final GeoPoint geoPoint) {
                item.getDragAndDropListener().startDragItemAtGeoPoint(item, geoPoint);
                updateLocationOfMarkerItemAndRedraw(item, geoPoint);
            }

            @Override
            public void ongoingDragItemToGeoPoint(final DraggableMarkerItem item, final GeoPoint geoPoint) {
                item.getDragAndDropListener().ongoingDragItemToGeoPoint(item, geoPoint);
                updateLocationOfMarkerItemAndRedraw(item, geoPoint);
            }

            @Override
            public void dropItemAtGeoPoint(final DraggableMarkerItem item, final GeoPoint geoPoint) {
                item.getDragAndDropListener().dropItemAtGeoPoint(item, geoPoint);
                updateLocationOfMarkerItemAndRedraw(item, geoPoint);
            }

            private void updateLocationOfMarkerItemAndRedraw(final MarkerItem markerItem, final GeoPoint location) {
                markerItem.geoPoint = location;
                markerLayer.populate();
                map.render();
            }
        };
    }
}
