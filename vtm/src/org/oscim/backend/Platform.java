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
package org.oscim.backend;


public enum Platform {
    ANDROID(false, false),
    IOS(true, false),
    MAC_OS(true, true),
    LINUX(false, true),
    WINDOWS(false, true),
    WEB(false, false),
    UNKNOWN(false, false);


    Platform(boolean buildingLayerTranslucent, boolean desktopQuirks) {
        this.BUILDING_LAYER_TRANSLUCENT = buildingLayerTranslucent;
        this.GDX_DESKTOP_QUIRKS = desktopQuirks;
    }


    public boolean BUILDING_LAYER_TRANSLUCENT;

    public boolean GDX_DESKTOP_QUIRKS;

    /**
     * Returns true when This is WINDOWS, LINUX or MAC_OS other, false
     *
     * @return boolean
     */
    public boolean isAnyDesktop() {
        return this == LINUX || this == WINDOWS || this == MAC_OS;
    }
}
