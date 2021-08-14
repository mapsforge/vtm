package org.oscim.android.drag;

import org.oscim.core.GeoPoint;
import org.oscim.map.Map;

class LocationUIUpdater {

    private final DraggableItemizedLayer draggableItemizedLayer;
    private final Map map;

    public LocationUIUpdater(final DraggableItemizedLayer draggableItemizedLayer, final Map map) {
        this.draggableItemizedLayer = draggableItemizedLayer;
        this.map = map;
    }

    public void update(final DraggableMarkerItem dragItem, final GeoPoint location) {
        dragItem.geoPoint = location;
        draggableItemizedLayer.populate();
        map.render();
    }
}
