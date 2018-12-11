/*
 * Copyright 2013 Tobias Knerr
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
package org.oscim.core.osm;

import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.impl.PackedCoordinateSequenceFactory;
import org.oscim.core.TagSet;

import java.util.List;

public class OsmWay extends OsmElement {

    public final List<OsmNode> nodes;

    public OsmWay(TagSet tags, long id, List<OsmNode> nodes) {
        super(tags, id);
        this.nodes = nodes;
    }

    public boolean isClosed() {
        return nodes.size() > 0 &&
                nodes.get(0).equals(nodes.get(nodes.size() - 1));
    }

    @Override
    public String toString() {
        return "w" + id;
    }

    @Override
    public Geometry toJts() {
        double[] coords = new double[nodes.size() * 2];
        int i = 0;
        for (OsmNode n : nodes) {
            coords[i++] = n.lon;
            coords[i++] = n.lat;
        }

        CoordinateSequence c = PackedCoordinateSequenceFactory.DOUBLE_FACTORY.create(coords, 2);
        return new LineString(c, null);
    }
}
