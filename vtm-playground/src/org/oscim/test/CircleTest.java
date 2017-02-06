/*
 * Copyright 2016 devemux86
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

import org.oscim.backend.GLAdapter;
import org.oscim.backend.canvas.Color;
import org.oscim.core.GeometryBuffer;
import org.oscim.gdx.GdxMap;
import org.oscim.gdx.GdxMapApp;
import org.oscim.layers.GenericLayer;
import org.oscim.renderer.BucketRenderer;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.MapRenderer;
import org.oscim.renderer.bucket.CircleBucket;
import org.oscim.theme.styles.CircleStyle;

public class CircleTest extends GdxMap {

    private final GeometryBuffer geom = new GeometryBuffer(2, 1);
    private final Renderer renderer = new Renderer();

    private void addCircle(float x, float y, CircleBucket cb) {
        geom.clear();
        geom.startPoints();
        geom.addPoint(x, y);
        cb.addCircle(geom);
    }

    private class Renderer extends BucketRenderer {

        Renderer() {
            mMapPosition.scale = 0;
        }

        @Override
        public void update(GLViewport v) {
            if (mMapPosition.scale == 0)
                mMapPosition.copy(v.pos);

            if (!isReady())
                compile();
        }
    }

    @Override
    protected void createLayers() {
        MapRenderer.setBackgroundColor(Color.BLACK);

        mMap.setMapPosition(0, 0, 1 << 4);

        CircleStyle cs = CircleStyle.builder()
                .radius(30)
                .color(Color.MAGENTA)
                .strokeWidth(6)
                .strokeColor(Color.WHITE)
                .build();
        CircleBucket cb = renderer.buckets.addCircleBucket(0, cs);
        addCircle(200, -200, cb);
        addCircle(-200, -200, cb);
        addCircle(-200, 200, cb);
        addCircle(200, 200, cb);

        mMap.layers().add(new GenericLayer(mMap, renderer));
    }

    public static void main(String[] args) {
        // Draw circles with quads or points
        GLAdapter.CIRCLE_QUADS = false;

        GdxMapApp.init();
        GdxMapApp.run(new CircleTest());
    }
}
