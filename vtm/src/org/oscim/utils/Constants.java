/*
 * Copyright 2025 devemux86
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
package org.oscim.utils;

import org.oscim.core.Tag;

public final class Constants {

    /**
     * Mapsforge artificial tags for land/sea areas.
     */
    public static final Tag TAG_MAPSFORGE_ISSEA = new Tag("natural", "issea");
    public static final Tag TAG_MAPSFORGE_NOSEA = new Tag("natural", "nosea");
    public static final Tag TAG_MAPSFORGE_SEA = new Tag("natural", "sea");

    /**
     * Freizeitkarte artificial tags for land/sea areas.
     */
    public static final Tag TAG_FREIZEITKARTE_LAND = new Tag("freizeitkarte", "land");
    public static final Tag TAG_FREIZEITKARTE_MEER = new Tag("freizeitkarte", "meer");

    private Constants() {
        throw new IllegalStateException();
    }
}
