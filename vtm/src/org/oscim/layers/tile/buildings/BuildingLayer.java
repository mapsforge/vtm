/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2017 devemux86
 * Copyright 2016 Robin Boldt
 * Copyright 2017 Gustl22
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BuildingLayer extends Layer implements TileLoaderThemeHook {

    private final static int BUILDING_LEVEL_HEIGHT = 280; // cm

    private final static int MIN_ZOOM = 17;
    private final static int MAX_ZOOM = 17;

    private final static boolean POST_AA = false;
    public static boolean TRANSLUCENT = true;

    private static final Object BUILDING_DATA = BuildingLayer.class.getName();

    // Can be replaced with Multimap in Java 8
    private HashMap<Integer, List<BuildingElement>> mBuildings = new HashMap<>();

    class BuildingElement {
        MapElement element;
        ExtrusionStyle style;
        boolean isPart;

        BuildingElement(MapElement element, ExtrusionStyle style, boolean isPart) {
            this.element = element;
            this.style = style;
            this.isPart = isPart;
        }
    }

    public BuildingLayer(Map map, VectorTileLayer tileLayer) {
        this(map, tileLayer, MIN_ZOOM, MAX_ZOOM, false);
    }

    public BuildingLayer(Map map, VectorTileLayer tileLayer, boolean isMesh) {
        this(map, tileLayer, MIN_ZOOM, MAX_ZOOM, isMesh);
    }

    public BuildingLayer(Map map, VectorTileLayer tileLayer, int zoomMin, int zoomMax, boolean isMesh) {

        super(map);

        tileLayer.addHook(this);

        mRenderer = new BuildingRenderer(tileLayer.tileRenderer(),
                zoomMin, zoomMax,
                isMesh, !isMesh && TRANSLUCENT); // alpha must be disabled for mesh renderer
        if (POST_AA || isMesh) // simplified that every mesh layer uses SSAO and FXAA
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

        // Filter all building elements
        // TODO #TagFromTheme: load from theme or decode tags to generalize mapsforge tags
        boolean isBuildingPart = element.tags.containsKey(Tag.KEY_BUILDING_PART)
                || (element.tags.containsKey("kind") && element.tags.getValue("kind").equals("building_part")); // Mapzen
        if (element.tags.containsKey(Tag.KEY_BUILDING) || isBuildingPart
                || (element.tags.containsKey("kind") && element.tags.getValue("kind").equals("building"))) { // Mapzen
            List<BuildingElement> buildingElements = mBuildings.get(tile.hashCode());
            if (buildingElements == null) {
                buildingElements = new ArrayList<>();
                mBuildings.put(tile.hashCode(), buildingElements);
            }
            element = new MapElement(element); // Deep copy, because element will be cleared
            buildingElements.add(new BuildingElement(element, extrusion, isBuildingPart));
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
    private void processElement(MapElement element, ExtrusionStyle extrusion, MapTile tile) {
        int height = 0; // cm
        int minHeight = 0; // cm

        String v = element.tags.getValue(Tag.KEY_HEIGHT);
        if (v != null)
            height = (int) (Float.parseFloat(v) * 100);
        else {
            // #TagFromTheme: generalize level/height tags
            if ((v = element.tags.getValue(Tag.KEY_BUILDING_LEVELS)) != null)
                height = (int) (Float.parseFloat(v) * BUILDING_LEVEL_HEIGHT);
        }

        v = element.tags.getValue(Tag.KEY_MIN_HEIGHT);
        if (v != null)
            minHeight = (int) (Float.parseFloat(v) * 100);
        else {
            // #TagFromTheme: level/height tags
            if ((v = element.tags.getValue(Tag.KEY_BUILDING_MIN_LEVEL)) != null)
                minHeight = (int) (Float.parseFloat(v) * BUILDING_LEVEL_HEIGHT);
        }

        if (height == 0)
            height = extrusion.defaultHeight * 100;

        ExtrusionBuckets ebs = get(tile);

        for (ExtrusionBucket b = ebs.buckets; b != null; b = b.next()) {
            if (b.colors == extrusion.colors) {
                b.add(element, height, minHeight);
                return;
            }
        }

        double lat = MercatorProjection.toLatitude(tile.y);
        float groundScale = (float) MercatorProjection
                .groundResolutionWithScale(lat, 1 << tile.zoomLevel);

        ebs.buckets = Inlist.push(ebs.buckets,
                new ExtrusionBucket(0, groundScale,
                        extrusion.colors));

        ebs.buckets.add(element, height, minHeight);
    }

    /**
     * Process all stored map elements (here only buildings).
     *
     * @param tile the tile which contains stored map elements
     */
    private void processElements(MapTile tile) {
        if (!mBuildings.containsKey(tile.hashCode()))
            return;

        List<BuildingElement> tileBuildings = mBuildings.get(tile.hashCode());
        Set<BuildingElement> rootBuildings = new HashSet<>();
        for (BuildingElement partBuilding : tileBuildings) {
            if (!partBuilding.isPart)
                continue;

            String refId = partBuilding.element.tags.getValue(Tag.KEY_REF); // #TagFromTheme
            refId = refId == null ? partBuilding.element.tags.getValue("root_id") : refId; // Mapzen
            if (refId == null)
                continue;

            // Search buildings which inherit parts
            for (BuildingElement rootBuilding : tileBuildings) {
                if (rootBuilding.isPart
                        || !(refId.equals(rootBuilding.element.tags.getValue(Tag.KEY_ID))))
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

    @Override
    public void complete(MapTile tile, boolean success) {
        if (success) {
            processElements(tile);
            get(tile).prepare();
        } else
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
