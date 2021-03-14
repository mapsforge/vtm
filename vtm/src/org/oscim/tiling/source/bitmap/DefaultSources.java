/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 Andrey Novikov
 * Copyright 2016-2020 devemux86
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
package org.oscim.tiling.source.bitmap;

import org.oscim.layers.tile.bitmap.BitmapTileLayer.FadeStep;
import org.oscim.map.Viewport;
import org.oscim.tiling.source.bitmap.BitmapTileSource.Builder;

/**
 * Do not use in applications unless you read through and comply to
 * their terms of use! Only added here for testing purposes.
 */
public class DefaultSources {

    private static final FadeStep[] FADE_STEPS = new FadeStep[]{
            new FadeStep(0, 8 - 1, 1, 0.7f),
            // don't fade between zoom-min/max
            // fade above zoom max + 2, interpolate 1 to 0
            new FadeStep(8 - 1, 8 + 1, 0.7f, 0)
    };

    // Requires a valid HTTP User-Agent identifying application
    // https://operations.osmfoundation.org/policies/tiles/
    public static Builder<?> OPENSTREETMAP = BitmapTileSource.builder()
            .url("https://tile.openstreetmap.org")
            .zoomMax(18);

    public static Builder<?> STAMEN_TONER = BitmapTileSource.builder()
            .url("https://stamen-tiles.a.ssl.fastly.net/toner")
            .zoomMax(18);

    public static Builder<?> STAMEN_WATERCOLOR = BitmapTileSource.builder()
            .url("https://stamen-tiles.a.ssl.fastly.net/watercolor")
            .tilePath("/{Z}/{X}/{Y}.jpg")
            .zoomMax(18);

    public static Builder<?> NE_LANDCOVER = BitmapTileSource.builder()
            .url("http://opensciencemap.org/tiles/ne")
            .fadeSteps(FADE_STEPS)
            .zoomMax(8);

    public static Builder<?> HIKEBIKE = BitmapTileSource.builder()
            .url("https://tiles.wmflabs.org/hikebike")
            .tilePath("/{Z}/{X}/{Y}.png")
            .zoomMax(17);

    public static Builder<?> HIKEBIKE_HILLSHADE = BitmapTileSource.builder()
            .url("https://tiles.wmflabs.org/hillshading")
            .tilePath("/{Z}/{X}/{Y}.png")
            .zoomMax(14);

    // Needs an API key
    public static Builder<?> MAPILION_HILLSHADE_1 = BitmapTileSource.builder()
            .url("https://tiles.mapilion.com/hillshades/v1")
            .tilePath("/{Z}/{X}/{Y}.png")
            .zoomMin(1)
            .zoomMax(12);

    // Needs an API key
    public static Builder<?> MAPILION_HILLSHADE_2 = BitmapTileSource.builder()
            .url("https://tiles.mapilion.com/hillshades/v2")
            .tilePath("/{Z}/{X}/{Y}.png")
            .fadeSteps(new FadeStep[]{
                    new FadeStep(0, Viewport.MAX_ZOOM_LEVEL, 1, 0.2f)
            })
            .zoomMax(12);
}
