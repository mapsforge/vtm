/*
 * Copyright 2014 Hannes Janetzek
 * Copyright 2016 devemux86
 * Copyright 2018 xiaoyan-qq
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
package org.oscim.layers;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import io.jeo.geom.CoordinatePath;
import org.oscim.core.*;
import org.oscim.layers.vector.AbstractVectorLayer;
import org.oscim.map.Map;
import org.oscim.renderer.bucket.LineBucket;
import org.oscim.renderer.bucket.MeshBucket;
import org.oscim.utils.geom.SimplifyDP;
import org.oscim.utils.geom.SimplifyVW;

public abstract class JtsLayer extends AbstractVectorLayer<Geometry> {

    private double mMinX;
    private double mMinY;

    public JtsLayer(Map map) {
        super(map);
    }

    @Override
    protected void processFeatures(Task t, Box bbox) {
        processFeatures(t, new Envelope(bbox.xmin, bbox.xmax, bbox.ymin, bbox.ymax));

    }

    protected abstract void processFeatures(Task t, Envelope e);

    protected int transformPath(MapPosition pos, GeometryBuffer g, CoordinatePath path) {

        double scale = pos.scale * Tile.SIZE / UNSCALE_COORD;
        int cnt = 0;
        O:
        while (path.hasNext()) {
            Coordinate c = path.next();
            float x = (float) ((MercatorProjection.longitudeToX(c.x) - pos.x) * scale);
            float y = (float) ((MercatorProjection.latitudeToY(c.y) - pos.y) * scale);

            switch (path.step()) {
                case MOVE_TO:
                    if (g.isPoly())
                        g.startPolygon();
                    else if (g.isLine())
                        g.startLine();

                    cnt++;
                    g.addPoint(x, y);
                    break;
                case LINE_TO:
                    cnt++;
                    g.addPoint(x, y);
                    break;
                case CLOSE:
                    //g.addPoint(x, y);
                    //if (g.type == GeometryType.POLY)
                    break;
                case STOP:
                    break O;
            }
        }
        return cnt;
    }

    SimplifyDP mSimpDP = new SimplifyDP();
    SimplifyVW mSimpVW = new SimplifyVW();

    protected void addPolygon(Task t, Geometry g, MeshBucket ml, LineBucket ll) {
        mGeom.clear();
        mGeom.startPolygon();

        CoordinatePath p = CoordinatePath.create(g);
        if (mMinX > 0 || mMinY > 0)
            p.generalize(mMinX, mMinY);

        if (transformPath(t.position, mGeom, p) < 3)
            return;

        if (!mClipper.clip(mGeom))
            return;

        mSimpVW.simplify(mGeom, 0.1f);
        mSimpDP.simplify(mGeom, 0.5f);

        ll.addLine(mGeom);
        ml.addMesh(mGeom);
    }

    protected void addLine(Task t, Geometry g, LineBucket ll) {
        mGeom.clear();
        mGeom.startLine();

        CoordinatePath p = CoordinatePath.create(g);
        transformPath(t.position, mGeom, p);

        ll.addLine(mGeom);
    }
}
