/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2017 Andrey Novikov
 * Copyright 2017-2018 devemux86
 * Copyright 2019 Gustl22
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

    private static final int CACHE_LIMIT = 40;

    protected final TileSource mTileSource;

    /**
     * Bitmap alpha in range 0 to 1
     */
    private float mBitmapAlpha = 1.0f;

    public static class FadeStep implements Comparable<FadeStep> {
        public final double scaleStart, scaleEnd;
        public final double zoomStart, zoomEnd;
        public final float alphaStart, alphaEnd;

        /**
         * @param zoomStart  the zoom start in range {@value org.oscim.map.Viewport#MIN_ZOOM_LEVEL} to {@value org.oscim.map.Viewport#MAX_ZOOM_LEVEL}
         * @param zoomEnd    the zoom end, must be greater than zoomStart
         * @param alphaStart the alpha start value in range 0 to 1
         * @param alphaEnd   the alpha end value in range 0 to 1
         */
        public FadeStep(int zoomStart, int zoomEnd, float alphaStart, float alphaEnd) {
            if (zoomStart > zoomEnd)
                throw new IllegalArgumentException("zoomStart must be smaller than zoomEnd");
            this.scaleStart = 1 << zoomStart;
            this.scaleEnd = 1 << zoomEnd;
            this.zoomStart = zoomStart;
            this.zoomEnd = zoomEnd;
            this.alphaStart = alphaStart;
            this.alphaEnd = alphaEnd;
        }

        public FadeStep(double scaleStart, double scaleEnd, float alphaStart, float alphaEnd) {
            if (scaleStart > scaleEnd)
                throw new IllegalArgumentException("scaleStart must be smaller than scaleEnd");
            this.scaleStart = scaleStart;
            this.scaleEnd = scaleEnd;
            this.zoomStart = Math.log(scaleStart) / Math.log(2);
            this.zoomEnd = Math.log(scaleEnd) / Math.log(2);
            this.alphaStart = alphaStart;
            this.alphaEnd = alphaEnd;
        }

        @Override
        public int compareTo(FadeStep o) {
            return Double.compare(this.zoomStart, o.zoomStart);
        }
    }

    public BitmapTileLayer(Map map, TileSource tileSource) {
        this(map, tileSource, CACHE_LIMIT);
    }

    public BitmapTileLayer(Map map, TileSource tileSource, float bitmapAlpha) {
        this(map, tileSource, CACHE_LIMIT, bitmapAlpha);
    }

    public BitmapTileLayer(Map map, TileSource tileSource, int cacheLimit) {
        this(map, tileSource, cacheLimit, tileSource.getAlpha());
    }

    public BitmapTileLayer(Map map, TileSource tileSource, int cacheLimit, float bitmapAlpha) {
        super(map,
                new TileManager(map, cacheLimit),
                new VectorTileRenderer());

        mTileManager.setZoomLevel(tileSource.getZoomLevelMin(),
                tileSource.getZoomLevelMax());

        mTileSource = tileSource;
        setBitmapAlpha(bitmapAlpha, false);
        initLoader(getNumLoaders());
        setFade(map.getMapPosition());
    }

    public void setBitmapAlpha(float bitmapAlpha, boolean redraw) {
        mBitmapAlpha = FastMath.clamp(bitmapAlpha, 0f, 1f);
        tileRenderer().setBitmapAlpha(mBitmapAlpha);
        if (redraw)
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

        float alpha = mBitmapAlpha;
        for (FadeStep f : fade) {
            if (pos.scale < f.scaleStart) {
                alpha = f.alphaStart;
                continue;
            } else if (pos.scale > f.scaleEnd) {
                alpha = f.alphaEnd;
                continue;
            }

            if (f.alphaStart == f.alphaEnd) {
                alpha = f.alphaStart;
                break;
            }
            // interpolate alpha between start and end
            alpha = (float) (f.alphaStart + (pos.getZoom() - f.zoomStart) * (f.alphaEnd - f.alphaStart) / (f.zoomEnd - f.zoomStart));
            break;
        }

        alpha = FastMath.clamp(alpha, 0f, 1f) * mBitmapAlpha;
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

    static final int POOL_FILL = 20;

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
