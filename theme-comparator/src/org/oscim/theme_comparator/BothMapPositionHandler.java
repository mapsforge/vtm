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
package org.oscim.theme_comparator;

import com.badlogic.gdx.Gdx;
import org.mapsforge.core.model.MapPosition;
import org.oscim.theme_comparator.location.LocationData;
import org.oscim.theme_comparator.location.LocationDataListener;
import org.oscim.theme_comparator.mapsforge.MapsforgeMapPanel;
import org.oscim.theme_comparator.vtm.VtmPanel;

import java.util.prefs.BackingStoreException;

public class BothMapPositionHandler implements LocationDataListener {

    private final MapsforgeMapPanel mapsforgeMapPanel;
    private final org.oscim.theme_comparator.vtm.VtmPanel vtmPanel;
    private MainMenu menu;

    public BothMapPositionHandler(MapsforgeMapPanel mapsforgeMapPanel, VtmPanel vtmPanel) {
        this.mapsforgeMapPanel = mapsforgeMapPanel;
        this.vtmPanel = vtmPanel;
        LocationData.addChangeListener(this);
    }

    void setCoordinate(final double latitude, final double longitude, final byte zoomLevel) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                mapsforgeMapPanel.setCoordinate(latitude, longitude, zoomLevel);
                vtmPanel.setCoordinate(latitude, longitude, (byte) (zoomLevel - 1));
                storePositionOnPrefs(latitude, longitude, zoomLevel);
            }
        });

    }

    public void mapPositionChangedFromMapPanel(final MapPosition mapPosition) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                vtmPanel.setCoordinate(mapPosition.latLong.latitude, mapPosition.latLong.longitude, (byte) (mapPosition.zoomLevel - 1));
                //TODO wy is zoom level on vtm x-1?
                storePositionOnPrefs(mapPosition.latLong.latitude, mapPosition.latLong.longitude, mapPosition.zoomLevel);
            }
        });
    }

    public void mapPositionChangedFromVtmMap(org.oscim.core.MapPosition mapPosition) {
        mapsforgeMapPanel.setCoordinate(mapPosition.getLatitude(), mapPosition.getLongitude(), (byte) (mapPosition.zoomLevel + 1));
        storePositionOnPrefs(mapPosition.getLatitude(), mapPosition.getLongitude(), (byte) (mapPosition.zoomLevel));
    }

    private void storePositionOnPrefs(double latitude, double longitude, byte zoomLevel) {
        Main.prefs.putDouble("latitude", latitude);
        Main.prefs.putDouble("longitude", longitude);
        Main.prefs.putInt("zoomLevel", zoomLevel);
        try {
            Main.prefs.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }

        // change position label on MenuBar
        if (this.menu != null) this.menu.setPos(latitude, longitude, zoomLevel);

        // change values on Location panel
        LocationData.set(latitude, longitude, zoomLevel);
    }

    void loadPrefsPosition() {
        double latitude = Main.prefs.getDouble("latitude", 300);
        double longitude = Main.prefs.getDouble("longitude", 300);
        byte zoomLevel = (byte) Main.prefs.getInt("zoomLevel", -1);

        if (latitude < 300) {
            setCoordinate(latitude, longitude, zoomLevel);
        }
    }

    void setCallBack(MainMenu menu) {
        this.menu = menu;
    }

    @Override
    public void valueChanged() {

        double latitude = LocationData.getLatitude();
        double longitude = LocationData.getLongitude();
        int zoomLevel = LocationData.getZoom();

        if (LocationData.getNS() == LocationData.Orientation.SOUTH) {
            latitude *= -1;
        }

        if (LocationData.getEW() == LocationData.Orientation.WEST) {
            longitude *= -1;
        }

        setCoordinate(latitude, longitude, (byte) zoomLevel);
    }
}
