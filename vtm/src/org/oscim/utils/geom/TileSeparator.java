/*
 * Copyright 2013 Hannes Janetzek
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
package org.oscim.utils.geom;

import org.oscim.core.GeometryBuffer;

import java.util.logging.Logger;

public class TileSeparator {

    private static final Logger log = Logger.getLogger(TileSeparator.class.getName());

    private float xmin;
    private float xmax;
    private float ymin;
    private float ymax;

    public TileSeparator(float xmin, float ymin, float xmax, float ymax) {
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;
    }

    private boolean isInside(float cx, float cy) {
        return cx >= xmin && cx < xmax && cy >= ymin && cy < ymax;
    }

    public void setRect(float xmin, float ymin, float xmax, float ymax) {
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;
    }

    /**
     * Separates geometry from tile without clipping it. Points and polys only.
     *
     * @param geom the geometry to be separated
     * @return true if geometry is inside the tile, false otherwise
     */
    public boolean separate(GeometryBuffer geom) {
        if (geom.isPoint()) {
            if (geom.index.length > 1 && geom.index[0] == 2) {
                float cx = geom.points[0];
                float cy = geom.points[1];

                if (isInside(cx, cy))
                    return true;
            } else
                log.warning("Geometry (Point) has wrong format: " + geom);
        } else if (geom.isPoly()) {
            int pointPos = 0;

            for (int indexPos = 0, n = geom.index.length; indexPos < n; indexPos++) {
                int len = geom.index[indexPos];
                if (len < 0)
                    break;

                if (len < 6) {
                    pointPos += len;
                    continue;
                }

                int end = pointPos + len;

                for (int i = pointPos; i < end; ) {
                    float cx = geom.points[i++];
                    float cy = geom.points[i++];

                    if (isInside(cx, cy))
                        return true;
                }

                pointPos += len;
            }
        }
        return false;
    }
}
