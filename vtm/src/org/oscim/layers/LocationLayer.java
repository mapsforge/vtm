/*
 * Copyright 2013 Ahmad Saleem
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 devemux86
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.oscim.layers;

import org.oscim.core.MercatorProjection;
import org.oscim.map.Map;
import org.oscim.renderer.LocationRenderer;

public class LocationLayer extends Layer {
    public final LocationRenderer locationRenderer;

    public LocationLayer(Map map) {
        super(map);

        mRenderer = locationRenderer = new LocationRenderer(mMap, this);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled == isEnabled())
            return;

        super.setEnabled(enabled);

        if (!enabled)
            locationRenderer.animate(false);
    }

    public void setPosition(double latitude, double longitude, double accuracy) {
        double x = MercatorProjection.longitudeToX(longitude);
        double y = MercatorProjection.latitudeToY(latitude);
        double radius = accuracy / MercatorProjection.groundResolution(latitude, 1);
        locationRenderer.setLocation(x, y, radius);
        locationRenderer.animate(true);
    }
}
