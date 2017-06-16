/*
 * Copyright 2014 Hannes Janetzek
 * Copyright 2016-2017 devemux86
 * Copyright 2017 Akarsh Seggemu
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

import org.jeo.map.CartoCSS;
import org.jeo.map.RGB;
import org.jeo.map.Rule;
import org.jeo.map.Style;
import org.jeo.vector.Feature;
import org.jeo.vector.VectorDataset;
import org.oscim.backend.canvas.Color;
import org.oscim.jeo.JeoUtils;
import org.oscim.map.Map;
import org.oscim.renderer.bucket.LineBucket;
import org.oscim.renderer.bucket.MeshBucket;
import org.oscim.renderer.bucket.TextBucket;
import org.oscim.renderer.bucket.TextItem;
import org.oscim.theme.styles.AreaStyle;
import org.oscim.theme.styles.LineStyle;
import org.oscim.theme.styles.TextStyle;

import java.util.HashMap;

public class OSMIndoorLayer extends JeoVectorLayer {

    protected TextBucket mTextLayer;
    protected TextStyle mText;

    public OSMIndoorLayer(Map map, VectorDataset data, Style style) {
        this(map, data, style, 1);
    }

    public OSMIndoorLayer(Map map, VectorDataset data, Style style, float scale) {
        super(map, data, style);

        mText = TextStyle.builder()
                .fontSize(16 * scale).color(Color.BLACK)
                .strokeWidth(2.2f * scale).strokeColor(Color.WHITE)
                .build();
    }

    public boolean[] activeLevels = new boolean[10];

    @Override
    protected void processFeatures(Task t, Envelope b) {
        mTextLayer = new TextBucket();

        t.buckets.set(mTextLayer);

        super.processFeatures(t, b);

        //render TextItems to a bitmap and prepare vertex buffer data.
        mTextLayer.prepare();
    }

    protected void addLine(Task t, Feature f, Rule rule, Geometry g) {

        if (((LineString) g).isClosed()) {
            addPolygon(t, f, rule, g);
            return;
        }

        int level = getLevel(f);

        LineBucket ll = t.buckets.getLineBucket(level * 3 + 2);
        if (ll.line == null) {
            RGB color = rule.color(f, CartoCSS.LINE_COLOR, RGB.black);
            float width = rule.number(f, CartoCSS.LINE_WIDTH, 1.2f);
            ll.line = new LineStyle(0, JeoUtils.color(color), width);
            ll.heightOffset = level * 4;
            ll.setDropDistance(0);
        }

        addLine(t, g, ll);
    }

    protected void addPolygon(Task t, Feature f, Rule rule, Geometry g) {
        int level = getLevel(f);

        LineBucket ll = t.buckets.getLineBucket(level * 3 + 1);

        boolean active = activeLevels[level + 1];

        if (ll.line == null) {
            float width = rule.number(f, CartoCSS.LINE_WIDTH, 1.2f);
            int color = Color.rainbow((level + 1) / 10f);

            if (level > -2 && !active)
                color = Color.fade(color, 0.1f);

            ll.line = new LineStyle(0, color, width);
            ll.heightOffset = level * 4;
            ll.setDropDistance(0);
        }

        MeshBucket mesh = t.buckets.getMeshBucket(level * 3);
        if (mesh.area == null) {
            int color = JeoUtils.color(rule.color(f, CartoCSS.POLYGON_FILL, RGB.red));
            if (level > -2 && !active)
                color = Color.fade(color, 0.1f);

            mesh.area = new AreaStyle(color);
            //mesh.area = new Area(Color.fade(Color.DKGRAY, 0.1f));
            mesh.heightOffset = level * 4f;
        }

        addPolygon(t, g, mesh, ll);

        if (active) {
            Object o = f.get("name");
            if (o instanceof String) {
                float x = 0;
                float y = 0;
                int n = mGeom.index[0];
                if (n > 0) {
                    for (int i = 0; i < n; ) {
                        x += mGeom.points[i++];
                        y += mGeom.points[i++];
                    }

                    TextItem ti = TextItem.pool.get();
                    ti.set(x / (n / 2), y / (n / 2), (String) o, mText);

                    mTextLayer.addText(ti);
                }
            }
        }
    }

    @Override
    protected void addPoint(Task t, Feature f, Rule rule, Geometry g) {

    }

    private int getLevel(Feature f) {
        /* not sure if one could match these geojson properties with cartocss */
        Object o = f.get("@relations");
        if (o instanceof HashMap) {
            @SuppressWarnings("unchecked")
            HashMap<String, Object> tags = (HashMap<String, Object>) o;
            @SuppressWarnings("unchecked")
            HashMap<String, Object> reltags = (HashMap<String, Object>) tags.get("reltags");

            if (reltags != null) {
                o = reltags.get("level");
                if (o instanceof String) {
                    //log.debug("got level {}", o);
                    return Integer.parseInt((String) o);
                }
            }
        }

        o = f.get("level");
        if (o instanceof String) {
            double doubleLevelValue = Double.parseDouble((String) o);
            return (int) doubleLevelValue;
        }

        return 0;
    }

}
