/*
 * Copyright 2014 Hannes Janetzek
 * Copyright 2016-2018 devemux86
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
package org.oscim.test;

import com.badlogic.gdx.Input;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Color;
import org.oscim.backend.canvas.Paint.Cap;
import org.oscim.core.GeometryBuffer;
import org.oscim.gdx.lwjgl.GdxMapApp;
import org.oscim.layers.GenericLayer;
import org.oscim.renderer.BucketRenderer;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.MapRenderer;
import org.oscim.renderer.bucket.LineBucket;
import org.oscim.renderer.bucket.LineTexBucket;
import org.oscim.renderer.bucket.TextureItem;
import org.oscim.theme.styles.LineStyle;

import java.io.IOException;

public class LineRenderTest extends GdxMapApp {

    GeometryBuffer mGeom = new GeometryBuffer(2, 1);
    GeometryBuffer mLine = new GeometryBuffer(2, 1);

    static boolean fixedLineWidth = true;
    LineTest l = new LineTest();

    @Override
    public void createLayers() {
        MapRenderer.setBackgroundColor(0xff000000);

        mMap.setMapPosition(0, 0, 1 << 4);

        GeometryBuffer g = mLine;
        g.startLine();
        g.addPoint(-100, 0);
        g.addPoint(100, 0);

        addLines(l, 0, true, false);

        mMap.layers().add(new GenericLayer(mMap, l));
    }

    void addLines(LineTest l, int layer, boolean addOutline, boolean fixed) {

        GeometryBuffer g = mLine;

        LineStyle line1, line2, line3, line4;

        if (fixed) {
            line1 = new LineStyle(Color.RED, 0.5f);
            line2 = new LineStyle(Color.GREEN, 1);
            line4 = new LineStyle(Color.LTGRAY, 3);
        } else {
            line1 = new LineStyle(0, null, Color.fade(Color.RED, 0.5f), 4.0f, Cap.BUTT, false, 1, 0, 0, 0, 0, 1f, false, null, true, null, LineStyle.REPEAT_START_DEFAULT, LineStyle.REPEAT_GAP_DEFAULT);
            line2 = new LineStyle(0, null, Color.GREEN, 6.0f, Cap.BUTT, false, 1, 0, 0, 0, 0, 1f, false, null, true, null, LineStyle.REPEAT_START_DEFAULT, LineStyle.REPEAT_GAP_DEFAULT);
            line4 = new LineStyle(0, null, Color.LTGRAY, 2.0f, Cap.ROUND, false, 1, 0, 0, 0, 0, 1f, false, null, true, null, LineStyle.REPEAT_START_DEFAULT, LineStyle.REPEAT_GAP_DEFAULT);
        }

        TextureItem tex = null;
        try {
            tex = new TextureItem(CanvasAdapter.getBitmapAsset("", "patterns/dot.png"));
            tex.mipmap = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        line3 = LineStyle.builder()
                .stippleColor(Color.CYAN)
                .stipple(8)
                .stippleWidth(0.6f)
                .strokeWidth(4)
                .strokeColor(Color.BLUE)
                .fixed(fixed)
                .texture(tex)
                .randomOffset(true)
                .build();

        LineStyle outline = new LineStyle(0, null, Color.BLUE, 2.0f, Cap.ROUND, false, 1, 0, 0, 0, 0, 1f, true, null, true, null, LineStyle.REPEAT_START_DEFAULT, LineStyle.REPEAT_GAP_DEFAULT);
        LineStyle outline2 = new LineStyle(0, null, Color.RED, 2.0f, Cap.ROUND, false, 1, 0, 0, 0, 0, 0, true, null, true, null, LineStyle.REPEAT_START_DEFAULT, LineStyle.REPEAT_GAP_DEFAULT);

        LineBucket ol = l.buckets.addLineBucket(0, outline);
        LineBucket ol2 = l.buckets.addLineBucket(5, outline2);

        LineBucket ll = l.buckets.addLineBucket(10, line1);
        ll.addLine(g.translate(0, -20));
        ll.addLine(g.translate(0, 10.5f));
        addCircle(-200, -200, 100, ll);

        if (addOutline)
            ol.addOutline(ll);

        ll = l.buckets.addLineBucket(20, line2);
        ll.addLine(g.translate(0, 10.5f));
        ll.addLine(g.translate(0, 10.5f));
        addCircle(200, -200, 100, ll);

        if (addOutline)
            ol.addOutline(ll);

        LineTexBucket lt = l.buckets.getLineTexBucket(30);
        lt.line = line3;
        lt.addLine(g.translate(0, 10.5f));
        lt.addLine(g.translate(0, 10.5f));
        addCircle(200, 200, 100, lt);

        ll = l.buckets.addLineBucket(40, line4);
        ll.addLine(g.translate(0, 10.5f));
        ll.addLine(g.translate(0, 10.5f));
        addCircle(-200, 200, 100, ll);

        if (addOutline)
            ol2.addOutline(ll);
    }

    void addCircle(float cx, float cy, float radius, LineBucket ll) {
        GeometryBuffer g = mGeom;

        g.clear();
        g.startLine();
        g.addPoint(cx, cy);
        g.addPoint(cx, cy);

        for (int i = 0; i < 60; i++) {
            double d = Math.toRadians(i * 6);
            g.setPoint(1, cx + (float) Math.sin(d) * radius,
                    cy + (float) Math.cos(d) * radius);
            ll.addLine(g);
        }
    }

    void addCircle(float cx, float cy, float radius, LineTexBucket ll) {
        GeometryBuffer g = mGeom;

        g.clear();
        g.startLine();
        g.addPoint(cx, cy);
        g.addPoint(cx, cy);

        for (int i = 0; i < 60; i++) {
            double d = Math.toRadians(i * 6);
            g.setPoint(1, cx + (float) Math.sin(d) * radius,
                    cy + (float) Math.cos(d) * radius);
            ll.addLine(g);
        }
    }

    @Override
    protected boolean onKeyDown(int keycode) {
        if (keycode < Input.Keys.NUM_1 || keycode > Input.Keys.NUM_4)
            return false;

        synchronized (l) {
            l.clear();

            GeometryBuffer g = mLine;
            g.clear();
            g.startLine();
            g.addPoint(-100, 0);
            g.addPoint(100, 0);

            if (keycode == Input.Keys.NUM_1)
                addLines(l, 0, true, true);
            else if (keycode == Input.Keys.NUM_2)
                addLines(l, 0, true, false);
            else if (keycode == Input.Keys.NUM_3)
                addLines(l, 0, false, true);
            else if (keycode == Input.Keys.NUM_4)
                addLines(l, 0, false, false);
        }

        mMap.updateMap(true);

        return true;
    }

    class LineTest extends BucketRenderer {

        public LineTest() {
            mMapPosition.scale = 0;
        }

        public synchronized void clear() {
            buckets.clear();
            setReady(false);
        }

        @Override
        public synchronized void update(GLViewport v) {

            if (mMapPosition.scale == 0)
                mMapPosition.copy(v.pos);

            if (!isReady()) {
                compile();
            }
        }

        // @Override
        // protected void setMatrix(MapPosition pos, Matrices m, boolean
        // project) {
        // m.useScreenCoordinates(true, 8f);
        // }
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new LineRenderTest(), null, 256);
    }
}
