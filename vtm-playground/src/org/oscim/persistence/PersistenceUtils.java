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
package org.oscim.persistence;

import org.mapsforge.map.awt.util.JavaPreferences;
import org.mapsforge.map.model.common.PreferencesFacade;
import org.oscim.core.MapPosition;

import java.util.prefs.Preferences;

public class PersistenceUtils {
    private static final PreferencesFacade preferencesFacade = new JavaPreferences(Preferences.userNodeForPackage(PersistenceUtils.class));

    public static synchronized void loadMapPosPrefs(MapPosition position) {
        position.setPosition(preferencesFacade.getDouble("LATITUDE", position.getLatitude()),
                preferencesFacade.getDouble("LONGITUDE", position.getLongitude()));
        position.setScale(preferencesFacade.getDouble("SCALE", position.getScale()));
        position.setBearing(preferencesFacade.getFloat("BEARING", position.getBearing()));
        position.setTilt(preferencesFacade.getFloat("TILT", position.getTilt()));
    }

    public static synchronized void saveMapPosPrefs(MapPosition position) {
        System.out.println("MP task: " + position);
        preferencesFacade.putDouble("LATITUDE", position.getLatitude());
        preferencesFacade.putDouble("LONGITUDE", position.getLongitude());
        preferencesFacade.putDouble("SCALE", position.getScale());
        preferencesFacade.putFloat("BEARING", position.getBearing());
        preferencesFacade.putFloat("TILT", position.getTilt());
    }

    public PersistenceUtils() {
    }
}
