/*
 * Copyright 2014 Hannes Janetzek
 * Copyright 2018-2019 Gustl22
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

package org.oscim.gdx.poi3d;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.utils.Array;

import org.oscim.core.Box;
import org.oscim.core.GeometryBuffer;
import org.oscim.core.MapElement;
import org.oscim.core.MapPosition;
import org.oscim.core.MercatorProjection;
import org.oscim.core.Tag;
import org.oscim.core.Tile;
import org.oscim.event.Event;
import org.oscim.gdx.GdxAssets;
import org.oscim.layers.Layer;
import org.oscim.layers.tile.MapTile;
import org.oscim.layers.tile.MapTile.TileData;
import org.oscim.layers.tile.TileSet;
import org.oscim.layers.tile.ZoomLimiter;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.VectorTileLayer.TileLoaderProcessHook;
import org.oscim.map.Map;
import org.oscim.model.VtmModels;
import org.oscim.renderer.bucket.RenderBuckets;
import org.oscim.renderer.bucket.SymbolItem;
import org.oscim.utils.geom.GeometryUtils;
import org.oscim.utils.geom.TileClipper;
import org.oscim.utils.pool.Inlist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Experimental Layer to display POIs with 3D models.
 */
public class Poi3DLayer extends Layer implements Map.UpdateListener, ZoomLimiter.IZoomLimiter {

    private static final Logger log = LoggerFactory.getLogger(Poi3DLayer.class);

    static class Poi3DTileData extends TileData {
        public final HashMap<ModelHolder, List<SymbolItem>> symbols = new HashMap<>();

        @Override
        protected void dispose() {
            for (List<SymbolItem> symbolItems : symbols.values()) {
                SymbolItem.pool.releaseAll(symbolItems.clear());
            }
            symbols.clear();
        }
    }

    public static final int MIN_ZOOM = BuildingLayer.MIN_ZOOM;
    static final String POI_DATA = Poi3DLayer.class.getSimpleName();
    public static final boolean RANDOM_TRANSFORM = true; // TODO customizable for each tag

    public static final Tag TAG_TREE = new Tag("natural", "tree");
    public static final Tag TAG_MEMORIAL = new Tag("historic", "memorial");
    public static final Tag TAG_FOREST = new Tag("landuse", "forest");
    public static final Tag TAG_WOOD = new Tag("natural", "wood");
    // Not supported by Oscim Tiles
    public static final Tag TAG_ARTWORK = new Tag("tourism", "artwork");
    public static final Tag TAG_TREE_BROADLEAVED = new Tag("leaf_type", "broadleaved");
    public static final Tag TAG_TREE_NEEDLELEAVED = new Tag("leaf_type", "needleleaved");
    public static final Tag TAG_TREE_ROW = new Tag("natural", "tree_row");
    public static final Tag TAG_STREETLAMP = new Tag("highway", "street_lamp");

    /**
     * Distance in meter between two 3d-models in an area or on a line (e.g. trees in forest).
     * Indicator for density. Actual distance depends on RANDOM_TRANSFORM.
     */
    public static float MODEL_DISTANCE = 8f;

    AssetManager mAssets;
    GdxRenderer3D2 mG3d;
    Set<Tag> mHideThemeRenders = new HashSet<>();
    boolean mLoading;
    LinkedHashMap<Tag, List<ModelHolder>> mScenes = new LinkedHashMap<>();
    TileClipper mTileClipper = new TileClipper(0, 0, Tile.SIZE, Tile.SIZE);
    VectorTileLayer mTileLayer;
    LinkedHashMap<Tile, Array<ModelInstance>> mTileMap = new LinkedHashMap<>();
    TileSet mTileSet = new TileSet();
    TileSet mPrevTiles = new TileSet();

    /**
     * Use ZoomLimiter to avoid different results on different zoom levels (e.g. for areas)
     */
    final ZoomLimiter mZoomLimiter;

    public Poi3DLayer(Map map, VectorTileLayer tileLayer) {
        this(map, tileLayer, true);
    }

    public Poi3DLayer(Map map, VectorTileLayer tileLayer, boolean useDefaults) {
        super(map);
        tileLayer.addHook(new TileLoaderProcessHook() {

            @Override
            public boolean process(MapTile tile, RenderBuckets buckets, MapElement element) {

                if (tile.zoomLevel < mZoomLimiter.getMinZoom()) return false;

                Poi3DTileData td = get(tile);

                for (Entry<Tag, List<ModelHolder>> scene : mScenes.entrySet()) {
                    if (!element.tags.contains(scene.getKey()))
                        continue;
                    List<ModelHolder> holders = scene.getValue();

                    SymbolItem s;
                    int pointCount;
                    float[] points;

                    // Fill poly with items
                    if (element.isPoly() || element.isLine()) {
                        GeometryBuffer geom = new GeometryBuffer(element);
                        mTileClipper.clip(geom); // single points should already have been clipped

                        points = geom.points;
                        pointCount = element.getNumPoints() * 2;
                        if (pointCount < 4)
                            return false; // Elements may have no points after clipping
                        // TODO lazy init?
                        double scale = MercatorProjection.zoomLevelToScale(tile.zoomLevel);
                        double lat = MercatorProjection.tileYToLatitudeWithScale(tile.tileY, scale);
                        double modelDistPix = MercatorProjection.metersToPixelsWithScale(MODEL_DISTANCE, lat, scale);

                        ArrayList<Float> polyPoints = new ArrayList<>(); // TODO fixed array reasonable?
                        float variation = (float) modelDistPix / 16f; // customizable

                        if (geom.isPoly()) {
                            Box box = new Box();
                            box.setExtents(points, pointCount);

                            double pixelsX = Tile.SIZE * tile.tileX;
                            double pixelsY = Tile.SIZE * tile.tileY;
                            box.xmin += (pixelsX + box.xmin) % modelDistPix;
                            box.ymin += (pixelsY + box.ymin) % modelDistPix;

                            while (box.xmin < box.xmax) {
                                double tmpY = box.ymin;
                                while (tmpY < box.ymax) {
                                    float x = (float) box.xmin;
                                    float y = (float) tmpY;
                                    if (RANDOM_TRANSFORM) {
                                        int hashX = getPosXHash(tile, x);
                                        int hashY = getPosYHash(tile, y);
                                        int hash = hashY * hashX;
                                        x += (((hash + hashX) % 14) - 7) * variation;
                                        y += (((hash + hashY) % 14) - 7) * variation;
                                    }
                                    if (GeometryUtils.pointInPoly(x, y, points, pointCount, 0)) {
                                        polyPoints.add(x);
                                        polyPoints.add(y);
                                    }
                                    tmpY += modelDistPix;
                                }
                                box.xmin += modelDistPix;
                            }
                        } else {
                            // Place models on a line with a gap of MODEL_DISTANCE
                            float[] p1 = {points[0], points[1]};
                            float[] p2 = new float[2];
                            float sumDist = 0;
                            for (int i = 2; i < pointCount; i += 2) {
                                p2[0] = points[i];
                                p2[1] = points[i + 1];
                                float dist = (float) GeometryUtils.distance2D(p1, p2);
                                float[] vec = GeometryUtils.scale(GeometryUtils.diffVec(p2, p1), 1 / dist);
                                while (sumDist < dist) {
                                    float[] tmp = GeometryUtils.scale(vec, sumDist);
                                    polyPoints.add(p1[0] + tmp[0]);
                                    polyPoints.add(p1[1] + tmp[1]);
                                    sumDist += modelDistPix;
                                }
                                sumDist -= dist;
                                p1[0] = p2[0];
                                p1[1] = p2[1];
                            }
                        }

                        points = new float[polyPoints.size()];
                        for (int i = 0; i < polyPoints.size(); i++) {
                            points[i] = polyPoints.get(i);
                        }
                        pointCount = points.length;
                    } else {
                        pointCount = element.getNumPoints() * 2;
                        points = element.points;
                    }


                    for (int i = 0; i < pointCount; i += 2) {
                        s = SymbolItem.pool.get();
                        s.x = points[i];
                        s.y = points[i + 1];

                        ModelHolder holder;
                        if (holders.size() > 1) {
                            // Use random for tags with multiple models.
                            int random = getPosHash(tile, s) % holders.size();
                            holder = holders.get(random);
                        } else
                            holder = holders.get(0);

                        Inlist.List<SymbolItem> symbolItems = td.symbols.get(holder);
                        if (symbolItems == null) {
                            symbolItems = new Inlist.List<>();
                            td.symbols.put(holder, symbolItems);
                        }

                        symbolItems.push(s);
                    }

                    // If set, prevent element from further rendering
                    return mHideThemeRenders.contains(scene.getKey());
                }

                return false;
            }

            @Override
            public void complete(MapTile tile, boolean success) {
            }
        });
        mTileLayer = tileLayer;

        mZoomLimiter = new ZoomLimiter(tileLayer.getManager(), MIN_ZOOM, map.viewport().getMaxZoomLevel(), MIN_ZOOM);

        mRenderer = mG3d = new GdxRenderer3D2(mMap);

        // Material mat = new
        // Material(ColorAttribute.createDiffuse(Color.BLUE));
        // ModelBuilder modelBuilder = new ModelBuilder();
        // long attributes = Usage.Position | Usage.Normal |
        // Usage.TextureCoordinates;

        // mModel = modelBuilder.createSphere(10f, 10f, 10f, 12, 12,
        // mat, attributes);

        mAssets = new AssetManager();

        if (useDefaults)
            useDefaults();
    }

    public void addModel(VtmModels model, Tag tag) {
        addModel(GdxAssets.getAssetPath(model.getPath()), tag);
    }

    /**
     * Assign model with specified path to an OSM tag. You can assign multiple models to one tag, too.
     */
    public void addModel(String path, Tag tag) {
        List<ModelHolder> scene = mScenes.get(tag);
        if (scene == null) {
            scene = new ArrayList<>();
            mScenes.put(tag, scene);
        }
        scene.add(new ModelHolder(path));
        mAssets.load(path, Model.class);
        if (!mLoading)
            mLoading = true;
    }

    private void doneLoading() {
        for (List<ModelHolder> holders : mScenes.values()) {
            for (ModelHolder holder : holders) {
                Model model = mAssets.get(holder.getPath());
                for (Node node : model.nodes) {
                    log.debug("loader node " + node.id);

                    /* Use with {@link GdxRenderer3D} */
                    if (node.hasChildren() && ((Object) mG3d) instanceof GdxRenderer3D) {
                        if (model.nodes.size != 1) {
                            throw new RuntimeException("Model has more than one node with GdxRenderer: " + model.toString());
                        }
                        node = node.getChild(0);
                        log.debug("loader node " + node.id);

                        model.nodes.removeIndex(0);
                        model.nodes.add(node);
                    }
                    node.scale.set(-1, 1, 1);
                    node.rotation.setFromAxis(1, 0, 0, 90);
                }
                holder.setModel(model);
            }
        }

        mLoading = false;
    }

    private Poi3DTileData get(MapTile tile) {
        Poi3DTileData ld = (Poi3DTileData) tile.getData(POI_DATA);
        if (ld == null) {
            ld = new Poi3DTileData();
            tile.addData(POI_DATA, ld);
        }
        return ld;
    }

    /**
     * @return an int which is equal in all zoom levels
     */
    private int getPosHash(Tile tile, SymbolItem item) {
        int a = getPosXHash(tile, item.x);
        int b = getPosYHash(tile, item.y);
        return Math.abs(a * b);
    }

    private int getPosXHash(Tile tile, float x) {
        return (int) (((tile.tileX + ((double) x / Tile.SIZE)) * 1000000000) / (1 << tile.zoomLevel)) * 37;
    }

    private int getPosYHash(Tile tile, float y) {
        return (int) (((tile.tileY + ((double) y / Tile.SIZE)) * 1000000000) / (1 << tile.zoomLevel)) * 73;
    }

    @Override
    public void onMapEvent(Event ev, MapPosition pos) {

        if (ev == Map.CLEAR_EVENT) {
            mTileSet = new TileSet();
            mPrevTiles = new TileSet();
            mTileMap = new LinkedHashMap<>();
            synchronized (mG3d) {
                mG3d.instances.clear();
            }
        }

        if (mLoading && mAssets.update()) {
            doneLoading();
            // Renderable renderable = new Renderable();
            // new ModelInstance(mModel).getRenderable(renderable);
            // Shader shader = new DefaultShader(renderable, true, false,
            // false, false, 1, 0, 0, 0);
        }
        if (mLoading)
            return;

        // log.debug("update");

        Integer tZoom = mTileLayer.tileRenderer().getVisibleTiles(mTileSet, true);

        if (mTileSet.cnt == 0 || tZoom == null) {
            mTileSet.releaseTiles();
            return;
        }

        int zoom;
        if (tZoom > mZoomLimiter.getZoomLimit()) {
            // render from zoom limit tiles (avoid duplicates and null)
            Set<MapTile> hashTiles = new HashSet<>();
            for (int i = 0; i < mTileSet.cnt; i++) {
                MapTile t = mZoomLimiter.getTile(mTileSet.tiles[i]);
                if (t == null)
                    continue;
                hashTiles.add(t);
            }

            mTileSet.cnt = hashTiles.size();
            if (mTileSet.cnt == 0) // If no ancestor tiles were found
                return;
            mTileSet.tiles = hashTiles.toArray(new MapTile[mTileSet.cnt]);
            zoom = mZoomLimiter.getZoomLimit();
        } else {
            zoom = tZoom;
        }

        boolean changed = false;

        Array<ModelInstance> added = new Array<>();
        Array<ModelInstance> removed = new Array<>();

        for (int i = 0; i < mTileSet.cnt; i++) {
            MapTile t = mTileSet.tiles[i];
            if (mPrevTiles.contains(t))
                continue;

            Array<ModelInstance> instances = new Array<>();

            Poi3DTileData ld = (Poi3DTileData) t.getData(POI_DATA);
            if (ld == null)
                continue;

            for (Entry<ModelHolder, Inlist.List<SymbolItem>> entry : ld.symbols.entrySet()) {
                for (SymbolItem it : entry.getValue()) {

                    ModelInstance inst = new ModelInstance(entry.getKey().getModel());
                    inst.userData = it;
                    // float r = 0.5f + 0.5f * (float) Math.random();
                    // float g = 0.5f + 0.5f * (float) Math.random();
                    // float b = 0.5f + 0.5f * (float) Math.random();

                    // inst.transform.setTranslation(new Vector3(it.x, it.y,
                    // 10));
                    // inst.materials.get(0).set(ColorAttribute.createDiffuse(r,
                    // g, b, 0.8f));
                    instances.add(inst);
                    added.add(inst);
                }
            }

            if (instances.size == 0)
                continue;

            log.debug("add " + t + " " + instances.size);

            changed = true;

            mTileMap.put(t, instances);
        }

        for (int i = 0; i < mPrevTiles.cnt; i++) {
            MapTile t = mPrevTiles.tiles[i];
            if (mTileSet.contains(t))
                continue;

            Array<ModelInstance> instances = mTileMap.get(t);
            if (instances == null)
                continue;

            changed = true;

            removed.addAll(instances);
            mTileMap.remove(t);
            log.debug("remove " + t);
        }

        mPrevTiles.releaseTiles();

        float groundScale = mTileSet.tiles[0].getGroundScale();

        TileSet tmp = mPrevTiles;
        mPrevTiles = mTileSet;
        mTileSet = tmp;

        if (!changed)
            return;

        // scale relative to latitude
        float scale = 1f / groundScale;

        double tileX = (pos.x * (Tile.SIZE << zoom));
        double tileY = (pos.y * (Tile.SIZE << zoom));

        synchronized (mG3d) {

            for (Entry<Tile, Array<ModelInstance>> e : mTileMap.entrySet()) {
                Tile t = e.getKey();

                float dx = (float) (t.tileX * Tile.SIZE - tileX);
                float dy = (float) (t.tileY * Tile.SIZE - tileY);

                for (ModelInstance inst : e.getValue()) {
                    SymbolItem it = (SymbolItem) inst.userData;

                    float s = scale;
                    float r = 0f;

                    // random/variable height and rotation
                    if (RANDOM_TRANSFORM) {
                        float deviationStep = s * 0.1f;
                        int hash = getPosHash(t, it); // Use absolute coordinates
                        s += ((hash % 4) - 2) * deviationStep;
                        r = hash % 360;
                    }

                    inst.transform.idt();
                    inst.transform.scale(s, s, s);
                    inst.transform.translate((dx + it.x) / s, (dy + it.y) / s, 0);
                    inst.transform.rotate(0, 0, 1, r);

                    // inst.transform.setToTranslationAndScaling((dx +
                    // it.x), (dy + it.y),
                    // 0, s, s, s);

                }
            }

            mG3d.instances.removeAll(removed, true);
            mG3d.instances.addAll(added);
            mG3d.cam.setMapPosition(pos.x, pos.y, 1 << zoom);
        }
    }

    /**
     * Provide elements with specified tag from being rendered with theme rules.
     * This gives more flexibility without changing render theme.
     */
    public void hideThemeRenders(Tag tag) {
        mHideThemeRenders.add(tag);
    }

    /**
     * Enable theme rendering of previously hidden elements.
     */
    public void showThemeRenders(Tag tag) {
        mHideThemeRenders.remove(tag);
    }

    public void useDefaults() {
        /* Keep order (the upper tags have higher priority)
         * Example: needle leaved woods only get fir model although it has the wood tag.
         */
        addModel(VtmModels.MEMORIAL, TAG_ARTWORK);
        addModel(VtmModels.MEMORIAL, TAG_MEMORIAL);
        addModel(VtmModels.STREETLAMP, TAG_STREETLAMP);
        addModel(VtmModels.TREE_FIR, TAG_TREE_NEEDLELEAVED);
        addModel(VtmModels.TREE_OAK, TAG_TREE_BROADLEAVED);
        addModel(VtmModels.TREE_ASH, TAG_TREE);
        addModel(VtmModels.TREE_ASH, TAG_TREE_ROW);
        addModel(VtmModels.TREE_FIR, TAG_WOOD);
        addModel(VtmModels.TREE_OAK, TAG_WOOD);
        addModel(VtmModels.TREE_ASH, TAG_WOOD);
        addModel(VtmModels.TREE_OAK, TAG_FOREST);
        addModel(VtmModels.TREE_ASH, TAG_FOREST);
        addModel(VtmModels.TREE_FIR, TAG_FOREST);

        hideThemeRenders(TAG_STREETLAMP);
    }

    @Override
    public void addZoomLimit() {
        mZoomLimiter.addZoomLimit();
    }

    @Override
    public void removeZoomLimit() {
        mZoomLimiter.removeZoomLimit();
    }
}
