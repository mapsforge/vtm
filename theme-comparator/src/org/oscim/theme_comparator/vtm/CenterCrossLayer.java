/*
 * Copyright 2017 Longri
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
package org.oscim.theme_comparator.vtm;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import org.oscim.backend.canvas.Color;
import org.oscim.backend.canvas.Paint;
import org.oscim.core.GeometryBuffer;
import org.oscim.layers.GenericLayer;
import org.oscim.map.Map;
import org.oscim.renderer.BucketRenderer;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.bucket.LineBucket;
import org.oscim.theme.styles.LineStyle;

/**
 * Created by Longri on 19.03.17.
 */
public class CenterCrossLayer extends GenericLayer implements Disposable {

    private static final float crossLength = 50;
    private static final float crossWidth = 1.7f;
    private static final float circleRadius = 25;
    private static final int drawingColor = Color.RED;

    CenterCrossLayer(Map map) {
        super(map, new Renderer());
        ((Renderer) this.mRenderer).setLayer(this);
    }

    @Override
    public void dispose() {
        ((Renderer) this.mRenderer).dispose();
    }

    private static class Renderer extends BucketRenderer {

        final LineBucket ll = buckets.addLineBucket(0,
                new LineStyle(drawingColor, crossWidth, Paint.Cap.ROUND));


        final GeometryBuffer g1 = new GeometryBuffer(2, 1);
        final GeometryBuffer g2 = new GeometryBuffer(2, 1);
        final GeometryBuffer g3 = new GeometryBuffer(2, 1);

        private CenterCrossLayer centerCrossLayer;

        @Override
        public void update(GLViewport v) {
            buckets.clear();
            if (!centerCrossLayer.isEnabled()) return;
            mMapPosition.copy(v.pos);
            buckets.set(ll);

            g1.clear();
            g1.startLine();
            g1.addPoint(-crossLength, -crossLength);
            g1.addPoint(crossLength, crossLength);
            ll.addLine(g1);

            g2.clear();
            g2.startLine();
            g2.addPoint(-crossLength, crossLength);
            g2.addPoint(crossLength, -crossLength);
            ll.addLine(g2);

            g3.clear();
            // calculate segment count
            float alpha = (360 * 5) / (MathUtils.PI2 * circleRadius);
            int segmente = Math.max(16, (int) (360 / alpha));

            // calculate theta step
            float thetaStep = (MathUtils.PI2 / segmente);

            g3.startLine();
            for (float i = 0, n = MathUtils.PI2 + thetaStep; i < n; i += thetaStep) {
                g3.addPoint(circleRadius * MathUtils.cos(i), circleRadius * MathUtils.sin(i));
            }

            ll.addLine(g3);

            compile();
        }

        void setLayer(CenterCrossLayer centerCrossLayer) {
            this.centerCrossLayer = centerCrossLayer;
        }

        void dispose() {
            centerCrossLayer = null;
        }
    }

}
