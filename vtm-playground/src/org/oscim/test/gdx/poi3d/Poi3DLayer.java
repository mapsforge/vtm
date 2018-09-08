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
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.VectorTileLayer.TileLoaderProcessHook;
import org.oscim.map.Map;
import org.oscim.model.VtmModels;
import org.oscim.renderer.bucket.RenderBuckets;
import org.oscim.renderer.bucket.SymbolItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * Experimental Layer to display POIs with 3D models.
 */
public class Poi3DLayer extends Layer implements Map.UpdateListener {

    private static final Logger log = LoggerFactory.getLogger(Poi3DLayer.class);

    static final String POI_DATA = Poi3DLayer.class.getSimpleName();
    static final Tag TREE_TAG = new Tag("natural", "tree");

    private final String pathToTree;

    AssetManager assets;
    GdxRenderer3D2 g3d;
    boolean loading;
    Model mModel;
    VectorTileLayer mTileLayer;

    LinkedHashMap<Tile, Array<ModelInstance>> mTileMap = new LinkedHashMap<>();

    TileSet mTileSet = new TileSet();
    TileSet mPrevTiles = new TileSet();

    public Poi3DLayer(Map map, VectorTileLayer tileLayer) {
        super(map);
        tileLayer.addHook(new TileLoaderProcessHook() {

            @Override
            public boolean process(MapTile tile, RenderBuckets buckets, MapElement element) {

                if (!element.tags.contains(TREE_TAG))
                    return false;

                Poi3DTileData td = get(tile);
                PointF p = element.getPoint(0);
                SymbolItem s = SymbolItem.pool.get();
                s.x = p.x;
                s.y = p.y;
                td.symbols.push(s);

                return true;
            }

            @Override
            public void complete(MapTile tile, boolean success) {
            }
        });
        mTileLayer = tileLayer;

        mRenderer = g3d = new GdxRenderer3D2(mMap);

        // Material mat = new
        // Material(ColorAttribute.createDiffuse(Color.BLUE));
        // ModelBuilder modelBuilder = new ModelBuilder();
        // long attributes = Usage.Position | Usage.Normal |
        // Usage.TextureCoordinates;

        // mModel = modelBuilder.createSphere(10f, 10f, 10f, 12, 12,
        // mat, attributes);

        pathToTree = GdxAssets.getAssetPath(VtmModels.TREE.getPath());

        assets = new AssetManager();
        assets.load(pathToTree, Model.class);
        loading = true;
    }

    private void doneLoading() {
        Model model = assets.get(pathToTree, Model.class);
        for (int i = 0; i < model.nodes.size; i++) {
            for (Node node : model.nodes) {
                log.debug("loader node " + node.id);

                /* Use with {@link GdxRenderer3D} */
                if (node.hasChildren() && ((Object) g3d) instanceof GdxRenderer3D) {
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
            mModel = model;
        }

        loading = false;
    }

    private Poi3DTileData get(MapTile tile) {
        Poi3DTileData ld = (Poi3DTileData) tile.getData(POI_DATA);
        if (ld == null) {
            ld = new Poi3DTileData();
            tile.addData(POI_DATA, ld);
        }
        return ld;
    }

    @Override
    public void onMapEvent(Event ev, MapPosition pos) {

        if (ev == Map.CLEAR_EVENT) {
            mTileSet = new TileSet();
            mPrevTiles = new TileSet();
            mTileMap = new LinkedHashMap<>();
            synchronized (g3d) {
                g3d.instances.clear();
            }
        }

        if (loading && assets.update()) {
            doneLoading();
            // Renderable renderable = new Renderable();
            // new ModelInstance(mModel).getRenderable(renderable);
            // Shader shader = new DefaultShader(renderable, true, false,
            // false, false, 1, 0, 0, 0);
        }
        if (loading)
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

            for (SymbolItem it : ld.symbols) {

                ModelInstance inst = new ModelInstance(mModel);
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

        TileSet tmp = mPrevTiles;
        mPrevTiles = mTileSet;
        mTileSet = tmp;

        if (!changed)
            return;

        // scale aka tree height
        float scale = (float) (1f / Math.pow(2, (17 - zoom))) * 8;

        double tileX = (pos.x * (Tile.SIZE << zoom));
        double tileY = (pos.y * (Tile.SIZE << zoom));

        synchronized (g3d) {

            for (Entry<Tile, Array<ModelInstance>> e : mTileMap.entrySet()) {
                Tile t = e.getKey();

                float dx = (float) (t.tileX * Tile.SIZE - tileX);
                float dy = (float) (t.tileY * Tile.SIZE - tileY);

                for (ModelInstance inst : e.getValue()) {
                    SymbolItem it = (SymbolItem) inst.userData;

                    // variable height
                    float s = scale + (it.x * it.y) % 3;
                    float r = (it.x * it.y) % 360;

                    inst.transform.idt();
                    inst.transform.scale(s, s, s);
                    inst.transform.translate((dx + it.x) / s, (dy + it.y) / s, 0);
                    inst.transform.rotate(0, 0, 1, r);

                    // inst.transform.setToTranslationAndScaling((dx +
                    // it.x), (dy + it.y),
                    // 0, s, s, s);

                }
            }

            g3d.instances.removeAll(removed, true);
            g3d.instances.addAll(added);
            g3d.cam.setMapPosition(pos.x, pos.y, 1 << zoom);
        }
    }

    static class Poi3DTileData extends TileData {
        public final List<SymbolItem> symbols = new List<>();

        @Override
        protected void dispose() {
            SymbolItem.pool.releaseAll(symbols.clear());
        }
    }
}
