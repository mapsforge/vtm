/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2017 devemux86
 *
 * This file is part of the OpenScienceMap project (http://www.opensciencemap.org).
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

import org.oscim.layers.tile.buildings.BuildingLayer;

public class GLAdapter {

    public final static boolean debug = false;
    public final static boolean debugView = false;

    /**
     * The instance provided by backend
     */
    public static GL gl;

    public static boolean GDX_DESKTOP_QUIRKS = false;
    public static boolean GDX_WEBGL_QUIRKS = false;

    /**
     * Set true as workaround for adreno driver issue:
     * https://github.com/opensciencemap/vtm/issues/52
     */
    public static boolean NO_BUFFER_SUB_DATA = false;

    /**
     * Draw circles with quads or points.
     */
    public static boolean CIRCLE_QUADS = false;

    public static void init(GL gl20) {
        gl = gl20;

        GDX_DESKTOP_QUIRKS = CanvasAdapter.platform.isDesktop();
        GDX_WEBGL_QUIRKS = (CanvasAdapter.platform == Platform.WEBGL);

        // Buildings translucency does not work on macOS, see #61
        if (CanvasAdapter.platform == Platform.MACOS)
            BuildingLayer.TRANSLUCENT = false;
    }
}
