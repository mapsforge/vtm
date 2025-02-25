/*
 * Copyright 2017-2022 devemux86
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

import java.util.HashSet;
import java.util.Set;

public final class Parameters {

    public enum SymbolScaling {ALL, POI}

    /**
     * If true the <code>Animator2</code> will be used instead of default <code>Animator</code>.
     */
    public static boolean ANIMATOR2 = true;

    /**
     * Allow custom scale factor for short vertices instead of the calculated one.
     */
    public static boolean CUSTOM_COORD_SCALE = false;

    /**
     * Allow custom tile size instead of the calculated one.
     */
    public static boolean CUSTOM_TILE_SIZE = false;

    /**
     * Rendering of distant labels.
     */
    public static boolean DISTANT_LABELS = false;

    /**
     * If true the <code>MapEventLayer2</code> will be used instead of default <code>MapEventLayer</code>.
     */
    public static boolean MAP_EVENT_LAYER2 = false;

    /**
     * If true the markers are sorted in y-axis.
     */
    public static boolean MARKER_SORT = true;

    /**
     * Maximum buffer size for map files.
     */
    public static int MAXIMUM_BUFFER_SIZE = 10000000;

    /**
     * Calculation of centroids for all polygons.
     * (may affect performance)
     * <p>
     * When a way will be rendered then typically a label / symbol will be applied
     * by the render theme. If the way does not come with a defined labelPosition
     * we should calculate a position, that is based on all points of the given way.
     * This "auto" position calculation is also done in the LabelTileLoaderHook class
     * but then the points of the way have been already reduced cause of the clipping
     * that is happening. So the suggestion here is to calculate the centroid of the way
     * and use that as centroidPosition of the element.
     */
    public static boolean POLY_CENTROID = false;

    /**
     * Optimal placement of labels or symbols on polygons.
     */
    public static boolean POLY_LABEL = false;

    /**
     * Placement of symbols on polygons.
     */
    public static boolean POLY_SYMBOL = true;

    /**
     * POT textures in themes.
     */
    public static boolean POT_TEXTURES = false;

    /**
     * Simplification exceptions.
     */
    public static final Set<Tag> SIMPLIFICATION_EXCEPTIONS = new HashSet<>();

    /**
     * Reduce points on-the-fly while reading from vector maps.
     * e.g. 0 (no simplification), 2, 4, ...
     */
    public static int SIMPLIFICATION_TOLERANCE = 0;

    /**
     * Symbol scaling mode.
     */
    public static SymbolScaling SYMBOL_SCALING = SymbolScaling.ALL;

    /**
     * Texture atlas in themes.
     */
    public static boolean TEXTURE_ATLAS = false;

    /**
     * Threaded system initialization.
     */
    public static boolean THREADED_INIT = false;

    private Parameters() {
        throw new IllegalStateException();
    }
}
