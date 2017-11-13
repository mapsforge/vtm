/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2017 Andrey Novikov
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
package org.oscim.layers.tile.bitmap;

import org.oscim.core.MapPosition;
import org.oscim.event.Event;
import org.oscim.layers.tile.TileLayer;
import org.oscim.layers.tile.TileLoader;
import org.oscim.layers.tile.TileManager;
import org.oscim.layers.tile.VectorTileRenderer;
import org.oscim.map.Map;
import org.oscim.renderer.bucket.TextureItem.TexturePool;
import org.oscim.tiling.TileSource;
import org.oscim.utils.FastMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitmapTileLayer extends TileLayer {

    protected static final Logger log = LoggerFactory.getLogger(BitmapTileLayer.class);

    private final static int CACHE_LIMIT = 40;

    protected final TileSource mTileSource;
    private float mBitmapAlpha;

    public static class FadeStep {
        public final double scaleStart, scaleEnd;
        public final float alphaStart, alphaEnd;

        public FadeStep(int zoomStart, int zoomEnd, float alphaStart, float alphaEnd) {
            this.scaleStart = 1 << zoomStart;
            this.scaleEnd = 1 << zoomEnd;
            this.alphaStart = alphaStart;
            this.alphaEnd = alphaEnd;
        }

        public FadeStep(double scaleStart, double scaleEnd, float alphaStart, float alphaEnd) {
            this.scaleStart = scaleStart;
            this.scaleEnd = scaleEnd;
            this.alphaStart = alphaStart;
            this.alphaEnd = alphaEnd;
        }
    }

    public BitmapTileLayer(Map map, TileSource tileSource) {
        this(map, tileSource, CACHE_LIMIT);
    }

    public BitmapTileLayer(Map map, TileSource tileSource, float transparency) {
        this(map, tileSource, CACHE_LIMIT, transparency);
    }

    public BitmapTileLayer(Map map, TileSource tileSource, int cacheLimit) {
        this(map, tileSource, cacheLimit, 0f);
    }

    public BitmapTileLayer(Map map, TileSource tileSource, int cacheLimit, float transparency) {
        super(map,
                new TileManager(map, cacheLimit),
                new VectorTileRenderer());

        mTileManager.setZoomLevel(tileSource.getZoomLevelMin(),
                tileSource.getZoomLevelMax());

        mTileSource = tileSource;
        mBitmapAlpha = 1 - transparency;
        tileRenderer().setBitmapAlpha(mBitmapAlpha);
        initLoader(getNumLoaders());
        setFade(map.getMapPosition());
    }

    public void setTransparency(float transparency) {
        mBitmapAlpha = 1 - transparency;
        tileRenderer().setBitmapAlpha(mBitmapAlpha);
        map().updateMap(true);
    }

    @Override
    public void onMapEvent(Event event, MapPosition pos) {
        super.onMapEvent(event, pos);

        if (event != Map.POSITION_EVENT)
            return;

        setFade(pos);
    }

    private void setFade(MapPosition pos) {
        FadeStep[] fade = mTileSource.getFadeSteps();

        if (fade == null) {
            //mRenderLayer.setBitmapAlpha(1);
            return;
        }

        float alpha = 0f;
        for (FadeStep f : fade) {
            if (pos.scale < f.scaleStart || pos.scale > f.scaleEnd)
                continue;

            if (f.alphaStart == f.alphaEnd) {
                alpha = f.alphaStart;
                break;
            }
            double range = f.scaleEnd - f.scaleStart;
            float a = (float) ((pos.scale - f.scaleStart) / range);
            a = FastMath.clamp(a, 0f, 1f);
            // interpolate alpha between start and end
            alpha = (1 - a) * f.alphaStart + a * f.alphaEnd;
            break;
        }

        alpha = FastMath.clamp(alpha, 0f, mBitmapAlpha);
        tileRenderer().setBitmapAlpha(alpha);
    }

    @Override
    protected TileLoader createLoader() {
        return new BitmapTileLoader(this, mTileSource);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        pool.clear();
    }

    final static int POOL_FILL = 20;

    /**
     * pool shared by TextLayers
     */
    final TexturePool pool = new TexturePool(POOL_FILL) {

        //        int sum = 0;
        //
        //        public TextureItem release(TextureItem item) {
        //            log.debug(getFill() + " " + sum + " release tex " + item.id);
        //            return super.release(item);
        //        };
        //
        //        public synchronized TextureItem get() {
        //            log.debug(getFill() + " " + sum + " get tex ");
        //
        //            return super.get();
        //        };
        //
        //        protected TextureItem createItem() {
        //            log.debug(getFill() + " " + (sum++) + " create tex ");
        //
        //            return super.createItem();
        //        };
        //
        //        protected void freeItem(TextureItem t) {
        //            log.debug(getFill() + " " + (sum--) + " free tex ");
        //            super.freeItem(t);
        //
        //        };
    };

}
