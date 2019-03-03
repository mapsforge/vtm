/*
 * Copyright 2018 Gustl22
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
package org.oscim.gdx.poi3d;

import org.oscim.core.MercatorProjection;

public class ModelPosition {
    public double x;
    public double y;
    public float rotation;
    public float xRotation;

    public ModelPosition(double lat, double lon, float rotation, float xRotation) {
        setPosition(lat, lon, rotation, xRotation);
    }

    public double getLat() {
        return MercatorProjection.toLatitude(y);
    }

    public double getLon() {
        return MercatorProjection.toLongitude(x);
    }

    public float getRotation() {
        return rotation;
    }

    public float getXRotation() {
        return xRotation;
    }

    public void setPosition(double lat, double lon, float rotation, float xRotation) {
        this.y = MercatorProjection.latitudeToY(lat);
        this.x = MercatorProjection.longitudeToX(lon);
        this.rotation = rotation;
        this.xRotation = xRotation;
    }
}
