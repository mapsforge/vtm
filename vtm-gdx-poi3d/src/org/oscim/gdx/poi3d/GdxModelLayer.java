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
package org.oscim.gdx.poi3d;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.utils.Array;

import org.oscim.core.MapPosition;
import org.oscim.core.MercatorProjection;
import org.oscim.core.Tile;
import org.oscim.event.Event;
import org.oscim.gdx.GdxAssets;
import org.oscim.layers.Layer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.map.Map;
import org.oscim.model.VtmModels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Experimental layer to display 3d models.
 */
public class GdxModelLayer extends Layer implements Map.UpdateListener {

    private static final Logger log = LoggerFactory.getLogger(GdxModelLayer.class);

    private static final int MIN_ZOOM = BuildingLayer.MIN_ZOOM;

    private Array<ModelInstance> mAdded = new Array<>();
    private AssetManager mAssets;
    private GdxRenderer3D2 mG3d;
    private boolean mLoading;
    private java.util.Map<ModelPosition, ModelHolder> mScenes = new HashMap<>();

    public GdxModelLayer(Map map) {
        super(map);

        mRenderer = mG3d = new GdxRenderer3D2(mMap);

        // Material mat = new
        // Material(ColorAttribute.createDiffuse(Color.BLUE));
        // ModelBuilder modelBuilder = new ModelBuilder();
        // long attributes = Usage.Position | Usage.Normal |
        // Usage.TextureCoordinates;

        // mModel = modelBuilder.createSphere(10f, 10f, 10f, 12, 12,
        // mat, attributes);

        mAssets = new AssetManager();
    }

    public ModelPosition addModel(VtmModels model, double lat, double lon, float rotation, float xRotation) {
        return addModel(GdxAssets.getAssetPath(model.getPath()), lat, lon, rotation, xRotation);
    }

    /**
     * Add model with specified path and position.
     *
     * @return the models position, can be modified during rendering e.g. to make animations.
     * Don't forget to trigger map events (as it usually does if something changes).
     */
    public ModelPosition addModel(String path, double lat, double lon, float rotation, float xRotation) {
        ModelPosition pos = new ModelPosition(lat, lon, rotation, xRotation);

        mScenes.put(pos, new ModelHolder(path));

        mAssets.load(path, Model.class);
        if (!mLoading)
            mLoading = true;

        return pos;
    }

    private void doneLoading() {
        for (ModelHolder poiModel : mScenes.values()) {
            Model model = mAssets.get(poiModel.getPath());
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
            poiModel.setModel(model);
        }

        mLoading = false;
    }

    @Override
    public void onMapEvent(Event ev, MapPosition pos) {

//        if (ev == Map.CLEAR_EVENT) {
//             synchronized (g3d) {
//                g3d.instances.clear();
//            }
//        }

        if (mLoading && mAssets.update()) {
            doneLoading();
            refreshModelInstances();
        }

        if (mLoading)
            return;

        double lat = MercatorProjection.toLatitude(pos.y);
        float groundscale = (float) MercatorProjection
                .groundResolutionWithScale(lat, 1 << pos.zoomLevel);


        float scale = 1f / groundscale;

        synchronized (mG3d) {
            // remove if out of visible zoom range
            mG3d.instances.removeAll(mAdded, true);
            if (pos.getZoomLevel() >= MIN_ZOOM) {
                mG3d.instances.addAll(mAdded);
            }

            for (ModelInstance inst : mAdded) {
                ModelPosition p = (ModelPosition) inst.userData;

                float dx = (float) ((p.x - pos.x) * (Tile.SIZE << pos.zoomLevel));
                float dy = (float) ((p.y - pos.y) * (Tile.SIZE << pos.zoomLevel));

                inst.transform.idt();
                inst.transform.scale(scale, scale, scale);
                inst.transform.translate(dx / scale, dy / scale, 0);
                inst.transform.rotate(0, 0, 1, p.getRotation());
                inst.transform.rotate(1, 0, 0, p.getXRotation());
            }
        }

        mG3d.cam.setMapPosition(pos.x, pos.y, 1 << pos.getZoomLevel());
    }

    public void refreshModelInstances() {
        for (java.util.Map.Entry<ModelPosition, ModelHolder> scene : mScenes.entrySet()) {
            mAdded.clear();
            mG3d.instances.clear();

            ModelInstance inst = new ModelInstance(scene.getValue().getModel());
            inst.userData = scene.getKey();
            mAdded.add(inst); // Local stored
            mG3d.instances.add(inst);  // g3d stored
        }
    }

    public void removeModel(ModelPosition position) {
        mScenes.remove(position);
        if (!mLoading)
            refreshModelInstances();
    }
}
