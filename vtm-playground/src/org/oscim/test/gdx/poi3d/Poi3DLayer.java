/*
 * Copyright 2014 Hannes Janetzek
 * Copyright 2018 Gustl22
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

package org.oscim.test.gdx.poi3d;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.utils.Array;

import org.oscim.core.MapElement;
import org.oscim.core.MapPosition;
import org.oscim.core.PointF;
import org.oscim.core.Tag;
import org.oscim.core.Tile;
import org.oscim.event.Event;
import org.oscim.gdx.GdxAssets;
import org.oscim.layers.Layer;
import org.oscim.layers.tile.MapTile;
import org.oscim.layers.tile.MapTile.TileData;
import org.oscim.layers.tile.TileSet;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.VectorTileLayer.TileLoaderProcessHook;
import org.oscim.map.Map;
import org.oscim.model.VtmModels;
import org.oscim.renderer.bucket.RenderBuckets;
import org.oscim.renderer.bucket.SymbolItem;
import org.oscim.utils.pool.Inlist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * Experimental Layer to display POIs with 3D models.
 */
public class Poi3DLayer extends Layer implements Map.UpdateListener {

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

    public final static int MIN_ZOOM = BuildingLayer.MIN_ZOOM;
    static final String POI_DATA = Poi3DLayer.class.getSimpleName();
    public final static boolean RANDOM_TRANSFORM = true; // TODO customizable for each tag

    public final static Tag TAG_TREE = new Tag("natural", "tree");
    public final static Tag TAG_MEMORIAL = new Tag("historic", "memorial");
    public final static Tag TAG_FOREST = new Tag("landuse", "forest");
    public final static Tag TAG_WOOD = new Tag("natural", "wood");
    // Not supported by Oscim Tiles
    public final static Tag TAG_ARTWORK = new Tag("tourism", "artwork");
    public final static Tag TAG_TREE_BROADLEAVED = new Tag("leaf_type", "broadleaved");
    public final static Tag TAG_TREE_NEEDLELEAVED = new Tag("leaf_type", "needleleaved");
    public final static Tag TAG_STREETLAMP = new Tag("highway", "street_lamp");

    AssetManager mAssets;
    GdxRenderer3D2 mG3d;
    boolean mLoading;
    LinkedHashMap<Tag, List<ModelHolder>> mScenes = new LinkedHashMap<>();
    VectorTileLayer mTileLayer;
    LinkedHashMap<Tile, Array<ModelInstance>> mTileMap = new LinkedHashMap<>();
    TileSet mTileSet = new TileSet();
    TileSet mPrevTiles = new TileSet();

    public Poi3DLayer(Map map, VectorTileLayer tileLayer) {
        this(map, tileLayer, true);
    }

    public Poi3DLayer(Map map, VectorTileLayer tileLayer, boolean useDefaults) {
        super(map);
        tileLayer.addHook(new TileLoaderProcessHook() {

            @Override
            public boolean process(MapTile tile, RenderBuckets buckets, MapElement element) {

                if (tile.zoomLevel < MIN_ZOOM) return false;

                Poi3DTileData td = get(tile);

                for (Entry<Tag, List<ModelHolder>> scene : mScenes.entrySet()) {
                    if (!element.tags.contains(scene.getKey()))
                        continue;
                    List<ModelHolder> holders = scene.getValue();

                    PointF p;
                    SymbolItem s;
                    // TODO fill poly area with items
                    for (int i = 0; i < element.getNumPoints(); i++) {
                        p = element.getPoint(i);
                        s = SymbolItem.pool.get();
                        s.x = p.x;
                        s.y = p.y;

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

                    return true;
                }

                return false;
            }

            @Override
            public void complete(MapTile tile, boolean success) {
            }
        });
        mTileLayer = tileLayer;

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
        int a = (int) (((tile.tileX + ((double) item.x / Tile.SIZE)) * 1000000000) / (1 << tile.zoomLevel));
        int b = (int) (((tile.tileY + ((double) item.y / Tile.SIZE)) * 1000000000) / (1 << tile.zoomLevel));
        return Math.abs(a * b);
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

        mTileLayer.tileRenderer().getVisibleTiles(mTileSet);

        if (mTileSet.cnt == 0) {
            mTileSet.releaseTiles();
            return;
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

        int zoom = mTileSet.tiles[0].zoomLevel;
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
        addModel(VtmModels.TREE_FIR, TAG_WOOD);
        addModel(VtmModels.TREE_OAK, TAG_WOOD);
        addModel(VtmModels.TREE_ASH, TAG_WOOD);
        addModel(VtmModels.TREE_OAK, TAG_FOREST);
        addModel(VtmModels.TREE_ASH, TAG_FOREST);
        addModel(VtmModels.TREE_FIR, TAG_FOREST);
    }
}
