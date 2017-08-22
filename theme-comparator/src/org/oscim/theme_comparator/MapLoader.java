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
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.reader.MapFile;
import org.oscim.theme_comparator.mapsforge.MapsforgeMapPanel;
import org.oscim.theme_comparator.vtm.VtmPanel;

import java.io.File;
import java.util.prefs.BackingStoreException;

class MapLoader {

    private final MapsforgeMapPanel mapsforgeMapPanel;
    private final org.oscim.theme_comparator.vtm.VtmPanel vtmPanel;
    private final BothMapPositionHandler mapPosition;
    private File mapFile;

    MapLoader(MapsforgeMapPanel mapsforgeMapPanel, VtmPanel vtmPanel, BothMapPositionHandler mapPosition) {

        this.vtmPanel = vtmPanel;
        this.mapsforgeMapPanel = mapsforgeMapPanel;
        this.mapPosition = mapPosition;
    }

    void loadPrefsMapFile() {
        String path = Main.prefs.get("loadedMap", "");

        if (!path.isEmpty()) {
            loadMap(new File(path), false);
        }
    }


    void loadMap(final File file, final boolean setCenter) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                mapFile = file;

                //store prefs
                Main.prefs.put("loadedMap", file.getAbsolutePath());
                try {
                    Main.prefs.flush();
                } catch (BackingStoreException e) {
                    e.printStackTrace();
                }

                mapsforgeMapPanel.loadMap(mapFile, null);
                vtmPanel.loadMap(mapFile, null);
                if (setCenter) {
                    MapFile mapsforgeFile = new MapFile(mapFile);
                    LatLong centerPoint = mapsforgeFile.boundingBox().getCenterPoint();
                    mapPosition.setCoordinate(centerPoint.latitude, centerPoint.longitude, (byte) 10);
                }
            }
        });
    }
}
