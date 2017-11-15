/*
 * Copyright 2017 Longri
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
package org.oscim.ios.test;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.GLAdapter;
import org.oscim.core.GeometryBuffer;
import org.oscim.core.Tag;
import org.oscim.core.TagSet;
import org.oscim.gdx.GdxAssets;
import org.oscim.gdx.GdxMap;
import org.oscim.ios.backend.IosGL;
import org.oscim.ios.backend.IosGraphics;
import org.oscim.layers.GenericLayer;
import org.oscim.renderer.BucketRenderer;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.MapRenderer;
import org.oscim.renderer.bucket.LineTexBucket;
import org.oscim.renderer.bucket.TextureItem;
import org.oscim.theme.IRenderTheme;
import org.oscim.theme.ThemeLoader;
import org.oscim.theme.VtmThemes;
import org.oscim.theme.styles.LineStyle;
import org.oscim.theme.styles.RenderStyle;

import java.io.IOException;

public class IOSLineTexBucketTest extends GdxMap {

    public static void init() {
        // init globals
        IosGraphics.init();
        GdxAssets.init("assets/");
        GLAdapter.init(new IosGL());
    }

    GeometryBuffer mLine = new GeometryBuffer(2, 1);

    LineTest l = new LineTest();

    @Override
    public void createLayers() {
        MapRenderer.setBackgroundColor(0xffffffff);

        mMap.setMapPosition(0, 0, 1 << 4);

        GeometryBuffer g = mLine;
        g.startLine();
        g.addPoint(-100, 0);
        g.addPoint(100, 0);

        addLines(l);

        mMap.layers().add(new GenericLayer(mMap, l));
    }

    void addLines(LineTest l) {

        GeometryBuffer g = mLine;
        LineStyle lineStyle;
        TextureItem tex = null;

        try {
            tex = new TextureItem(CanvasAdapter.getBitmapAsset("", "patterns/pike.png"));
            tex.mipmap = true;
        } catch (IOException e) {
            e.printStackTrace();
        }


        IRenderTheme t = ThemeLoader.load(VtmThemes.DEFAULT);

        TagSet tags = new TagSet();
        tags.add(new Tag("highway", null));
        tags.add(new Tag("oneway", "yes"));

        RenderStyle[] ri = t.matchElement(GeometryBuffer.GeometryType.LINE, tags, 16);

        lineStyle = (LineStyle) ri[0];

        LineTexBucket lt = l.buckets.getLineTexBucket(20);
        lt.line = lineStyle;
        lt.addLine(g.translate(0, 10.5f));

    }

    class LineTest extends BucketRenderer {

        public LineTest() {
            mMapPosition.scale = 0;
        }

        @Override
        public synchronized void update(GLViewport v) {
            if (mMapPosition.scale == 0)
                mMapPosition.copy(v.pos);

            if (!isReady()) {
                compile();
            }
        }
    }

}
