/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2018 devemux86
 * Copyright 2016 Robin Boldt
 * Copyright 2017-2019 Gustl22
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
import org.oscim.core.Tag;
import org.oscim.layers.Layer;
import org.oscim.layers.tile.MapTile;
import org.oscim.layers.tile.ZoomLimiter;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.VectorTileLayer.TileLoaderThemeHook;
import org.oscim.map.Map;
import org.oscim.renderer.ExtrusionRenderer;
import org.oscim.renderer.OffscreenRenderer;
import org.oscim.renderer.OffscreenRenderer.Mode;
import org.oscim.renderer.light.ShadowRenderer;
import org.oscim.renderer.bucket.ExtrusionBuckets;
import org.oscim.renderer.bucket.RenderBuckets;
import org.oscim.theme.IRenderTheme;
import org.oscim.theme.styles.ExtrusionStyle;
import org.oscim.theme.styles.RenderStyle;
import org.oscim.utils.geom.GeometryUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BuildingLayer extends Layer implements TileLoaderThemeHook, ZoomLimiter.IZoomLimiter {

    protected static final int BUILDING_LEVEL_HEIGHT = 280; // cm

    public static final int MIN_ZOOM = 17;

    /**
     * Use Fast Approximate Anti-Aliasing (FXAA) and Screen Space Ambient Occlusion (SSAO).
     */
    public static boolean POST_AA = false;

    /**
     * Use real time calculations to pre-process data.
     */
    public static boolean RAW_DATA = false;

    /**
     * Let vanish extrusions / meshes which are covered by others.
     * {@link org.oscim.renderer.bucket.RenderBucket#EXTRUSION}: roofs are always translucent.
     * <p>
     * To better notice the difference, reduce the alpha value of extrusion colors in themes.
     */
    public static boolean TRANSLUCENT = true;

    /**
     * Use shadow rendering
     */
    public static boolean SHADOW = false;

    private static final Object BUILDING_DATA = BuildingLayer.class.getName();

    // Can be replaced with Multimap in Java 8
    protected java.util.Map<Integer, List<BuildingElement>> mBuildings = new HashMap<>();

    protected final ExtrusionRenderer mExtrusionRenderer;

    private final ZoomLimiter mZoomLimiter;

    protected final IRenderTheme mRenderTheme;

    class BuildingElement {
        MapElement element;
        ExtrusionStyle style;

        BuildingElement(MapElement element, ExtrusionStyle style) {
            this.element = element;
            this.style = style;
        }
    }

    public BuildingLayer(Map map, VectorTileLayer tileLayer) {
        this(map, tileLayer, false);
    }

    public BuildingLayer(Map map, VectorTileLayer tileLayer, boolean mesh) {
        this(map, tileLayer, MIN_ZOOM, map.viewport().getMaxZoomLevel(), mesh);
    }

    /**
     * @param map       The map data to add
     * @param tileLayer The vector tile layer which contains the tiles and the map elements
     * @param zoomMin   The minimum zoom at which the layer appears
     * @param zoomMax   The maximum zoom at which the layer appears
     * @param mesh      Declare if using mesh or polygon renderer
     */
    public BuildingLayer(Map map, VectorTileLayer tileLayer, int zoomMin, int zoomMax, boolean mesh) {

        super(map);

        tileLayer.addHook(this);

        // Use zoomMin as zoomLimit to render buildings only once
        mZoomLimiter = new ZoomLimiter(tileLayer.getManager(), zoomMin, zoomMax, zoomMin);

        mRenderer = mExtrusionRenderer = new BuildingRenderer(tileLayer.tileRenderer(), mZoomLimiter, mesh, TRANSLUCENT);
        if (SHADOW) {
            mRenderer = new ShadowRenderer(mExtrusionRenderer);
        }
        // TODO allow SHADOW and POST_AA at same time
        else if (POST_AA)
            mRenderer = new OffscreenRenderer(Mode.SSAO_FXAA, mRenderer);

        mRenderTheme = tileLayer.getTheme();
    }

    @Override
    public void addZoomLimit() {
        mZoomLimiter.addZoomLimit();
    }

    @Override
    public void removeZoomLimit() {
        mZoomLimiter.removeZoomLimit();
    }

    /**
     * TileLoaderThemeHook
     */
    @Override
    public boolean process(MapTile tile, RenderBuckets buckets, MapElement element,
                           RenderStyle style, int level) {
        // FIXME artifacts at tile borders in last extraction zoom as they're clipped

        if (!(style instanceof ExtrusionStyle))
            return false;
        if (tile.zoomLevel > mZoomLimiter.getZoomLimit())
            return false;

        ExtrusionStyle extrusion = (ExtrusionStyle) style.current();

        // Filter all building elements
        // TODO #TagFromTheme: load from theme or decode tags to generalize mapsforge tags
        if (element.isBuilding() || element.isBuildingPart()) {
            List<BuildingElement> buildingElements = mBuildings.get(tile.hashCode());
            if (buildingElements == null) {
                buildingElements = new ArrayList<>();
                mBuildings.put(tile.hashCode(), buildingElements);
            }
            element = new MapElement(element); // Deep copy, because element will be cleared
            if (RAW_DATA && element.isClockwise() > 0) {
                // Buildings must be counter clockwise in VTM (mirrored to OSM)
                element.reverse();
            }
            buildingElements.add(new BuildingElement(element, extrusion));
            return true;
        }

        // Process other elements immediately
        processElement(element, extrusion, tile);

        return true;
    }

    /**
     * Process map element.
     *
     * @param element   the map element
     * @param extrusion the style of map element
     * @param tile      the tile which contains map element
     */
    protected void processElement(MapElement element, ExtrusionStyle extrusion, MapTile tile) {
        int height = 0; // cm
        int minHeight = 0; // cm

        Float f = element.getHeight(mRenderTheme);
        if (f != null)
            height = (int) (f * 100);
        else {
            // #TagFromTheme: generalize level/height tags
            String v = getValue(element, Tag.KEY_BUILDING_LEVELS);
            if (v != null)
                height = (int) (Float.parseFloat(v) * BUILDING_LEVEL_HEIGHT);
        }

        f = element.getMinHeight(mRenderTheme);
        if (f != null)
            minHeight = (int) (f * 100);
        else {
            // #TagFromTheme: level/height tags
            String v = getValue(element, Tag.KEY_BUILDING_MIN_LEVEL);
            if (v != null)
                minHeight = (int) (Float.parseFloat(v) * BUILDING_LEVEL_HEIGHT);
        }

        if (height == 0)
            height = extrusion.defaultHeight * 100;

        ExtrusionBuckets ebs = get(tile);
        ebs.addPolyElement(element, tile.getGroundScale(), extrusion.colors, height, minHeight);
    }

    /**
     * Process all stored map elements (here only buildings).
     *
     * @param tile the tile which contains stored map elements
     */
    protected void processElements(MapTile tile) {
        if (!mBuildings.containsKey(tile.hashCode()))
            return;

        List<BuildingElement> tileBuildings = mBuildings.get(tile.hashCode());
        Set<BuildingElement> rootBuildings = new HashSet<>();
        for (BuildingElement partBuilding : tileBuildings) {
            if (!partBuilding.element.isBuildingPart())
                continue;

            String refId = getValue(partBuilding.element, Tag.KEY_REF);
            if (refId == null)
                continue;

            // Search buildings which inherit parts
            for (BuildingElement rootBuilding : tileBuildings) {
                if (rootBuilding.element.isBuildingPart())
                    continue;
                if (RAW_DATA) {
                    float[] center = GeometryUtils.center(partBuilding.element.points, 0, partBuilding.element.pointNextPos, null);
                    if (!GeometryUtils.pointInPoly(center[0], center[1], rootBuilding.element.points, rootBuilding.element.index[0], 0))
                        continue;
                } else if (!(refId.equals(getValue(rootBuilding.element, Tag.KEY_ID))))
                    continue;

                rootBuildings.add(rootBuilding);
                break;
            }
        }

        tileBuildings.removeAll(rootBuildings); // root buildings aren't rendered

        for (BuildingElement buildingElement : tileBuildings) {
            processElement(buildingElement.element, buildingElement.style, tile);
        }
        mBuildings.remove(tile.hashCode());
    }

    /**
     * @param tile the MapTile
     * @return ExtrusionBuckets of the tile
     */
    public static ExtrusionBuckets get(MapTile tile) {
        ExtrusionBuckets ebs = (ExtrusionBuckets) tile.getData(BUILDING_DATA);
        if (ebs == null) {
            ebs = new ExtrusionBuckets(tile);
            tile.addData(BUILDING_DATA, ebs);
        }
        return ebs;
    }

    /**
     * Get the ExtrusionRenderer for customization.
     */
    public ExtrusionRenderer getExtrusionRenderer() {
        return mExtrusionRenderer;
    }

    protected String getKeyOrDefault(String key) {
        if (mRenderTheme == null)
            return key;
        String res = mRenderTheme.transformKey(key);
        return res != null ? res : key;
    }

    protected String getValue(MapElement element, String key) {
        return element.tags.getValue(getKeyOrDefault(key));
    }

    @Override
    public void complete(MapTile tile, boolean success) {
        if (success) {
            processElements(tile);
            get(tile).prepare();
        } else
            get(tile).resetBuckets(null);
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
