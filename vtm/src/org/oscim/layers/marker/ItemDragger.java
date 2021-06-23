package org.oscim.layers.marker;

import org.oscim.core.GeoPoint;
import org.oscim.event.MotionEvent;

class ItemDragger {

    private final ItemizedLayer itemizedLayer;
    private MarkerItem dragItem;

    public ItemDragger(final ItemizedLayer itemizedLayer) {
        this.itemizedLayer = itemizedLayer;
    }

    public boolean startDrag(final MotionEvent e) {
        dragItem = null;
        return itemizedLayer.activateSelectedItems(
                e,
                new ItemizedLayer.ActiveItem() {
                    @Override
                    public boolean run(final int index) {
                        final MarkerItem markerItem = (MarkerItem) itemizedLayer.mItemList.get(index);
                        dragItem = markerItem.isDraggable() ? markerItem : null;
                        return dragItem != null;
                    }
                });
    }

    public boolean dragItemTo(final GeoPoint geoPoint) {
        if (dragItem == null) {
            return false;
        }
        dragItem.geoPoint = geoPoint;
        update();
        return true;
    }

    public void noDrag() {
        dragItem = null;
    }

    private void update() {
        itemizedLayer.populate();
        itemizedLayer.mMarkerRenderer.update();
        itemizedLayer.map().render();
    }
}
