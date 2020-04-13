/*
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
package org.oscim.layers;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

import org.oscim.jeo.JeoUtils;
import org.oscim.map.Map;
import org.oscim.renderer.bucket.LineBucket;
import org.oscim.renderer.bucket.MeshBucket;
import org.oscim.theme.styles.AreaStyle;
import org.oscim.theme.styles.LineStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import io.jeo.geom.Geom;
import io.jeo.map.CartoCSS;
import io.jeo.map.RGB;
import io.jeo.map.Rule;
import io.jeo.map.RuleList;
import io.jeo.map.Style;
import io.jeo.vector.Feature;
import io.jeo.vector.VectorDataset;
import io.jeo.vector.VectorQuery;

public class JeoVectorLayer extends JtsLayer {

    public static final Logger log = LoggerFactory.getLogger(JeoVectorLayer.class);
    static final boolean dbg = false;

    private final VectorDataset mDataset;
    private final RuleList mRules;

    protected double mDropPointDistance = 0.01;
    private double mMinX;
    private double mMinY;

    public JeoVectorLayer(Map map, VectorDataset data, Style style) {
        super(map);
        mDataset = data;

        mRules = style.getRules().selectById(data.name(), true).flatten();
        //mRules = style.getRules().selectById("way", true).flatten();
        log.debug(mRules.toString());

        mRenderer = new Renderer();
    }

    @Override
    protected void processFeatures(Task t, Envelope b) {
        if (mDropPointDistance > 0) {
            /* reduce lines points min distance */
            mMinX = ((b.getMaxX() - b.getMinX()) / mMap.getWidth());
            mMinY = ((b.getMaxY() - b.getMinY()) / mMap.getHeight());
            mMinX *= mDropPointDistance;
            mMinY *= mDropPointDistance;
        }

        try {
            VectorQuery q = new VectorQuery().bounds(b);
            if (dbg)
                log.debug("query {}", b);
            for (Feature f : mDataset.cursor(q)) {
                if (dbg)
                    log.debug("feature {}", f);

                RuleList rs = mRules.match(f);
                if (rs.isEmpty())
                    continue;

                Rule r = rs.collapse();
                if (r == null)
                    continue;

                Geometry g = f.geometry();
                if (g == null)
                    continue;

                switch (Geom.Type.from(g)) {
                    case POINT:
                        addPoint(t, f, r, g);
                        break;
                    case MULTIPOINT:
                        for (int i = 0, n = g.getNumGeometries(); i < n; i++)
                            addPoint(t, f, r, g.getGeometryN(i));
                        break;
                    case LINESTRING:
                        addLine(t, f, r, g);
                        break;
                    case MULTILINESTRING:
                        for (int i = 0, n = g.getNumGeometries(); i < n; i++)
                            addLine(t, f, r, g.getGeometryN(i));
                        break;
                    case POLYGON:
                        addPolygon(t, f, r, g);
                        break;
                    case MULTIPOLYGON:
                        for (int i = 0, n = g.getNumGeometries(); i < n; i++)
                            addPolygon(t, f, r, g.getGeometryN(i));
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            log.error("Error querying layer " + mDataset.name() + e);
        }
    }

    protected void addLine(Task t, Feature f, Rule rule, Geometry g) {

        if (((LineString) g).isClosed()) {
            addPolygon(t, f, rule, g);
            return;
        }

        LineBucket ll = t.buckets.getLineBucket(2);
        if (ll.line == null) {
            RGB color = rule.color(f, CartoCSS.LINE_COLOR, RGB.black);
            float width = rule.number(f, CartoCSS.LINE_WIDTH, 1.2f);
            ll.line = new LineStyle(0, JeoUtils.color(color), width);
            ll.setDropDistance(0.5f);
        }

        addLine(t, g, ll);
    }

    protected void addPolygon(Task t, Feature f, Rule rule, Geometry g) {

        LineBucket ll = t.buckets.getLineBucket(1);

        if (ll.line == null) {
            float width = rule.number(f, CartoCSS.LINE_WIDTH, 1.2f);
            RGB color = rule.color(f, CartoCSS.LINE_COLOR, RGB.black);
            ll.line = new LineStyle(0, JeoUtils.color(color), width);
            ll.setDropDistance(0.5f);
        }

        MeshBucket mesh = t.buckets.getMeshBucket(0);
        if (mesh.area == null) {
            int color = JeoUtils.color(rule.color(f, CartoCSS.POLYGON_FILL, RGB.red));
            mesh.area = new AreaStyle(color);
        }

        addPolygon(t, g, mesh, ll);
    }

    protected void addPoint(Task t, Feature f, Rule rule, Geometry g) {

    }
}
