/*
 * Copyright 2013 Ahmad Saleem
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2019 devemux86
 * Copyright 2017-2018 Longri
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

import org.oscim.backend.CanvasAdapter;
import org.oscim.core.MercatorProjection;
import org.oscim.map.Map;
import org.oscim.renderer.LocationTextureRenderer;

public class LocationTextureLayer extends Layer {
    public final LocationTextureRenderer locationRenderer;

    public LocationTextureLayer(Map map) {
        this(map, CanvasAdapter.getScale());
    }

    public LocationTextureLayer(Map map, float scale) {
        super(map);

        mRenderer = locationRenderer = new LocationTextureRenderer(map, this, scale);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled == isEnabled())
            return;

        super.setEnabled(enabled);

        if (!enabled)
            locationRenderer.animate(false);
    }

    public void setPosition(double latitude, double longitude, float accuracy) {
        double x = MercatorProjection.longitudeToX(longitude);
        double y = MercatorProjection.latitudeToY(latitude);
        double radius = accuracy / MercatorProjection.groundResolutionWithScale(latitude, 1);
        locationRenderer.setLocation(x, y, radius);
        locationRenderer.animate(true);
    }
}
