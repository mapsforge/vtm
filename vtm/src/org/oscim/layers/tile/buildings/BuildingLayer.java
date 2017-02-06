/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 devemux86
 * Copyright 2016 Robin Boldt
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
package org.oscim.layers.tile.buildings;

import org.oscim.core.MapElement;
import org.oscim.core.MercatorProjection;
import org.oscim.core.Tag;
import org.oscim.layers.Layer;
import org.oscim.layers.tile.MapTile;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.VectorTileLayer.TileLoaderThemeHook;
import org.oscim.map.Map;
import org.oscim.renderer.OffscreenRenderer;
import org.oscim.renderer.OffscreenRenderer.Mode;
import org.oscim.renderer.bucket.ExtrusionBucket;
import org.oscim.renderer.bucket.ExtrusionBuckets;
import org.oscim.renderer.bucket.RenderBuckets;
import org.oscim.theme.styles.ExtrusionStyle;
import org.oscim.theme.styles.RenderStyle;
import org.oscim.utils.pool.Inlist;

public class BuildingLayer extends Layer implements TileLoaderThemeHook {

    private final static int MIN_ZOOM = 17;
    private final static int MAX_ZOOM = 17;

    private final static boolean POST_AA = false;
    public static boolean TRANSLUCENT = true;

    private static final Object BUILDING_DATA = BuildingLayer.class.getName();

    public BuildingLayer(Map map, VectorTileLayer tileLayer) {
        this(map, tileLayer, MIN_ZOOM, MAX_ZOOM);
    }

    public BuildingLayer(Map map, VectorTileLayer tileLayer, int zoomMin, int zoomMax) {

        super(map);

        tileLayer.addHook(this);

        mRenderer = new BuildingRenderer(tileLayer.tileRenderer(),
                zoomMin, zoomMax,
                false, TRANSLUCENT);
        if (POST_AA)
            mRenderer = new OffscreenRenderer(Mode.SSAO_FXAA, mRenderer);
    }

    /**
     * TileLoaderThemeHook
     */
    @Override
    public boolean process(MapTile tile, RenderBuckets buckets, MapElement element,
                           RenderStyle style, int level) {

        if (!(style instanceof ExtrusionStyle))
            return false;

        ExtrusionStyle extrusion = (ExtrusionStyle) style.current();

        int height = 0;
        int minHeight = 0;

        String v = element.tags.getValue(Tag.KEY_HEIGHT);
        if (v != null)
            height = (int) Float.parseFloat(v);

        v = element.tags.getValue(Tag.KEY_MIN_HEIGHT);
        if (v != null)
            minHeight = (int) Float.parseFloat(v);

        /* 12m default */
        if (height == 0)
            height = 12 * 100;

        ExtrusionBuckets ebs = get(tile);

        for (ExtrusionBucket b = ebs.buckets; b != null; b = b.next()) {
            if (b.colors == extrusion.colors) {
                b.add(element, height, minHeight);
                return true;
            }
        }

        double lat = MercatorProjection.toLatitude(tile.y);
        float groundScale = (float) MercatorProjection
                .groundResolution(lat, 1 << tile.zoomLevel);

        ebs.buckets = Inlist.push(ebs.buckets,
                new ExtrusionBucket(0, groundScale,
                        extrusion.colors));

        ebs.buckets.add(element, height, minHeight);

        return true;
    }

    public static ExtrusionBuckets get(MapTile tile) {
        ExtrusionBuckets eb = (ExtrusionBuckets) tile.getData(BUILDING_DATA);
        if (eb == null) {
            eb = new ExtrusionBuckets(tile);
            tile.addData(BUILDING_DATA, eb);
        }
        return eb;
    }

    @Override
    public void complete(MapTile tile, boolean success) {
        if (success)
            get(tile).prepare();
        else
            get(tile).setBuckets(null);
    }

    //    private int multi;
    //    @Override
    //    public void onInputEvent(Event event, MotionEvent e) {
    //        int action = e.getAction() & MotionEvent.ACTION_MASK;
    //        if (action == MotionEvent.ACTION_POINTER_DOWN) {
    //            multi++;
    //        } else if (action == MotionEvent.ACTION_POINTER_UP) {
    //            multi--;
    //            if (!mActive && mAlpha > 0) {
    //                // finish hiding
    //                //log.debug("add multi hide timer " + mAlpha);
    //                addShowTimer(mFadeTime * mAlpha, false);
    //            }
    //        } else if (action == MotionEvent.ACTION_CANCEL) {
    //            multi = 0;
    //            log.debug("cancel " + multi);
    //            if (mTimer != null) {
    //                mTimer.cancel();
    //                mTimer = null;
    //            }
    //        }
    //    }

}
