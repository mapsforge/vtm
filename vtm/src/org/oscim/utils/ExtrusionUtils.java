/*
 * Copyright 2012, 2013 Hannes Janetzek
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
package org.oscim.utils;

import org.oscim.core.GeometryBuffer;
import org.oscim.core.Tile;

/**
 * A utility class with extrusion helper methods.
 */
public final class ExtrusionUtils {

    public static final float REF_TILE_SIZE = 4096.0f; // Standard ref tile size

    /**
     * @param value       the height value
     * @param groundScale the ground scale of tile
     * @return the corresponding height in the specific tile
     */
    public static float mapGroundScale(float value, float groundScale) {
        /* match height with ground resolution (meter per pixel) */
        return value / (groundScale * 10); // 10 cm steps
    }

    /**
     * Map the raw buffer scale to scale of coordinates
     */
    public static void mapPolyCoordScale(GeometryBuffer buffer) {
        float tileScale = REF_TILE_SIZE / Tile.SIZE;
        float[] points = buffer.points;
        for (int pPos = 0; pPos < buffer.getNumCoords(); pPos++) {
            points[pPos] = points[pPos] * tileScale;
        }
    }

    private ExtrusionUtils() {
    }
}
