package org.oscim.android.drag;

import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;

public class DisableableMarkerItem extends MarkerItem implements Disableable {

    private boolean enabled = true;
    private final MarkerSymbol enabledMarker;
    private final MarkerSymbol disabledMarker;

    public DisableableMarkerItem(final Object uid,
                                 final String title,
                                 final String description,
                                 final GeoPoint geoPoint,
                                 final MarkerSymbol enabledMarker,
                                 final MarkerSymbol disabledMarker) {
        super(uid, title, description, geoPoint);
        this.enabledMarker = enabledMarker;
        this.disabledMarker = disabledMarker;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void setMarker(final MarkerSymbol marker) {
        // throw new IllegalStateException();
    }

    @Override
    public MarkerSymbol getMarker() {
        return isEnabled() ? enabledMarker : disabledMarker;
    }

    @Override
    public void setRotation(final float rotation) {
        enabledMarker.setRotation(rotation);
        disabledMarker.setRotation(rotation);
    }
}
