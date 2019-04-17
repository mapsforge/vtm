package org.oscim.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import org.oscim.backend.canvas.Color;
import org.oscim.core.GeometryBuffer;
import org.oscim.gdx.lwjgl.GdxMapApp;
import org.oscim.layers.GenericLayer;
import org.oscim.renderer.BucketRenderer;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.MapRenderer;
import org.oscim.renderer.bucket.LineBucket;
import org.oscim.theme.styles.LineStyle;

public class LineTest extends GdxMapApp {

    @Override
    protected boolean onKeyDown(int keycode) {
        if (keycode == Input.Keys.NUM_1) {
            angle++;
            mMap.render();
            return true;

        }

        if (keycode == Input.Keys.NUM_2) {
            angle--;
            mMap.render();
            return true;

        }
        return false;
    }

    float angle = 0;

    @Override
    public void createLayers() {
        mMap.layers().add(new GenericLayer(mMap, new BucketRenderer() {
            boolean init;

            LineBucket ll = buckets.addLineBucket(0,
                    new LineStyle(Color.fade(Color.CYAN, 0.5f), 1.5f));

            GeometryBuffer g = new GeometryBuffer(10, 1);

            @Override
            public void update(GLViewport v) {
                if (!init) {
                    mMapPosition.copy(v.pos);
                    init = true;

                    // g.addPoint(0, 0);
                    // g.addPoint(0, 1);
                    // g.addPoint(0.1f, 0);
                    //
                    // g.addPoint(1, 1);
                    // g.addPoint(2, 0);
                    //
                    // g.addPoint(2, 1);
                    // g.addPoint(2, 0);
                    //
                    // g.addPoint(3, 1);
                    // g.addPoint(3, 0);
                    // g.addPoint(3, 1);
                    //
                    // for (int i = 0; i < 60; i++){
                    // g.startLine();
                    // g.addPoint(0, 0);
                    // g.addPoint(0, 1);
                    // }
                    //
                    // g.scale(100, 100);
                    //
                    // ll.addLine(g);
                    //
                    // compile();
                }

                buckets.clear();
                buckets.set(ll);
                g.clear();
                //for (int i = 0; i < 60; i++) {
                g.startLine();
                g.addPoint(-1, 0);
                g.addPoint(0, 0);
                g.addPoint((float) Math.cos(Math.toRadians(angle)),
                        (float) Math.sin(Math.toRadians(angle)));
                //}

                g.scale(100, 100);

                ll.addLine(g);

                compile();

                angle = Gdx.input.getX() / 2f % 360;

                MapRenderer.animate();
            }
        }));
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new LineTest(), null, 256);
    }
}
