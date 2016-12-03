/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 devemux86
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
package org.oscim.theme.styles;

/**
 * Represents a round area on the map.
 */
public final class CircleStyle extends RenderStyle<CircleStyle> {

    public final int level;

    public final int fill;
    public final int outline;
    public final float radius;
    public final boolean scaleRadius;
    public final float strokeWidth;

    public CircleStyle(Float radius, boolean scaleRadius, int fill, int stroke,
                       float strokeWidth, int level) {
        super();

        this.radius = radius;
        this.scaleRadius = scaleRadius;

        this.fill = fill;
        this.outline = stroke;

        this.strokeWidth = strokeWidth;
        this.level = level;
    }

    @Override
    public void renderNode(Callback cb) {
        cb.renderCircle(this, this.level);
    }

    @Override
    public CircleStyle current() {
        return (CircleStyle) mCurrent;
    }
}
