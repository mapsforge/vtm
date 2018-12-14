/*
 * Copyright 2017 Longri
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
package org.oscim.theme.comparator.location;

import org.oscim.theme.comparator.BothMapPositionHandler;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public final class LocationData {


    public enum Orientation {
        EAST, WEST, NORTH, SOUTH;

        @Override
        public String toString() {
            return this.name().substring(0, 1) + this.name().substring(1).toLowerCase();
        }
    }

    private static double latitude;
    private static double longitude;
    private static int zoom;
    private static Orientation ns;
    private static Orientation ew;

    static {
        latitude = 47.48135;
        longitude = 8.20797;
        ns = Orientation.NORTH;
        ew = Orientation.EAST;
    }

    private static Vector<LocationDataListener> listeners = new Vector<>();

    public static void addChangeListener(LocationDataListener listener) {
        listeners.add(listener);
    }


    private static void notifyChange() {
        if (blockCircleEvent.get()) return;
        notifyChange(true);
    }

    private static AtomicBoolean blockCircleEvent = new AtomicBoolean(false);

    private static void notifyChange(boolean all) {

        if (!all) blockCircleEvent.set(true);

        for (LocationDataListener l : listeners) {
            if (all) {
                l.valueChanged();
            } else {
                if (!(l instanceof BothMapPositionHandler))
                    l.valueChanged();
            }
        }
        blockCircleEvent.set(false);
    }

    private LocationData() {
    }

    public static int getZoom() {
        return zoom;
    }


    public static synchronized double getLatitude() {
        return latitude;
    }

    static synchronized void setLatitude(double latitude) {
        if (LocationData.latitude != latitude) {
            LocationData.latitude = latitude;
            notifyChange();
        }
    }

    public static synchronized double getLongitude() {
        return longitude;
    }

    public static synchronized void setLongitude(double longitude) {
        if (LocationData.longitude != longitude) {
            LocationData.longitude = longitude;
            notifyChange();
        }
    }

    public static Orientation getEW() {
        return ew;
    }

    static void setEW(Orientation ew) {
        if (ew == Orientation.NORTH || ew == Orientation.SOUTH)
            throw new IllegalArgumentException();
        if (LocationData.ew != ew) {
            LocationData.ew = ew;
            notifyChange();
        }
    }

    public static Orientation getNS() {
        return ns;
    }

    static void setNS(Orientation ns) {
        if (ns == Orientation.EAST || ns == Orientation.WEST)
            throw new IllegalArgumentException();
        if (LocationData.ns != ns) {
            LocationData.ns = ns;
            notifyChange();
        }
    }

    public static void setZoom(int zoomLevel) {
        if (LocationData.zoom != zoomLevel) {
            LocationData.zoom = zoomLevel;
            notifyChange();
        }
    }

    public static void set(double latitude, double longitude, byte zoomLevel) {

        boolean change = false;

        //set orientation
        Orientation ns = Orientation.NORTH;
        Orientation ew = Orientation.EAST;

        if (latitude < 0) {
            ns = Orientation.SOUTH;
            latitude *= -1;
        }

        if (longitude < 0) {
            ew = Orientation.WEST;
            longitude *= -1;
        }

        if (LocationData.ns != ns) {
            LocationData.ns = ns;
            change = true;
        }

        if (LocationData.ew != ew) {
            LocationData.ew = ew;
            change = true;
        }

        if (LocationData.latitude != latitude) {
            LocationData.latitude = latitude;
            change = true;
        }

        if (LocationData.longitude != longitude) {
            LocationData.longitude = longitude;
            change = true;
        }

        if (LocationData.zoom != zoomLevel) {
            LocationData.zoom = zoomLevel;
            change = true;
        }

        if (change) {
            notifyChange(false);
        }

    }
}
