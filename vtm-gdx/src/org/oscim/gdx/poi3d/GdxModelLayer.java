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
import org.oscim.theme.VtmModels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Experimental layer to display 3d models.
 */
public class GdxModelLayer extends Layer implements Map.UpdateListener {

    static final Logger log = LoggerFactory.getLogger(GdxModelLayer.class);

    public class ModelPosition {
        public double x;
        public double y;
        public float rotation;

        public ModelPosition(double lat, double lon, float rotation) {
            setPosition(lat, lon, rotation);
        }

        public void setPosition(double lat, double lon, float rotation) {
            this.y = MercatorProjection.latitudeToY(lat);
            this.x = MercatorProjection.longitudeToX(lon);
            this.rotation = rotation;
        }

        public double getLat() {
            return MercatorProjection.toLatitude(y);
        }

        public double getLon() {
            return MercatorProjection.toLongitude(x);
        }

        public float getRotation() {
            return rotation;
        }
    }

    public final static int MIN_ZOOM = BuildingLayer.MIN_ZOOM;
    private GdxRenderer3D2 g3d;
    private boolean loading;
    private HashMap<ModelPosition, Poi3DModel> mScenes = new HashMap<>();
    private AssetManager assets;

    public GdxModelLayer(Map map) {
        super(map);

        mRenderer = g3d = new GdxRenderer3D2(mMap);

        // Material mat = new
        // Material(ColorAttribute.createDiffuse(Color.BLUE));
        // ModelBuilder modelBuilder = new ModelBuilder();
        // long attributes = Usage.Position | Usage.Normal |
        // Usage.TextureCoordinates;

        // mModel = modelBuilder.createSphere(10f, 10f, 10f, 12, 12,
        // mat, attributes);

        assets = new AssetManager();
    }

    public ModelPosition addModel(VtmModels model, double lat, double lon, float rotation) {
        return addModel(GdxAssets.getAssetPath(model.getPath()), lat, lon, rotation);
    }

    /**
     * Add model with specified path and position.
     *
     * @return the models position, can be modified during rendering e.g. to make animations.
     * Don't forget to trigger map events (as it usually does if something changes).
     */
    public ModelPosition addModel(String path, double lat, double lon, float rotation) {
        ModelPosition pos = new ModelPosition(lat, lon, rotation);

        mScenes.put(pos, new Poi3DModel(path));

        assets.load(path, Model.class);
        if (!loading)
            loading = true;

        return pos;
    }

    private void doneLoading() {
        for (Poi3DModel poiModel : mScenes.values()) {
            Model model = assets.get(poiModel.getPath());
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
            poiModel.setModel(model);
        }

        loading = false;
    }

    private Array<ModelInstance> added = new Array<>();

    @Override
    public void onMapEvent(Event ev, MapPosition pos) {

//        if (ev == Map.CLEAR_EVENT) {
//             synchronized (g3d) {
//                g3d.instances.clear();
//            }
//        }

        if (loading && assets.update()) {
            doneLoading();

            for (java.util.Map.Entry<ModelPosition, Poi3DModel> scene : mScenes.entrySet()) {
                ModelInstance inst = new ModelInstance(scene.getValue().getModel());
                inst.userData = scene.getKey();
                added.add(inst); // Local stored
                g3d.instances.add(inst);  // g3d stored
            }
        }

        if (loading)
            return;

        double lat = MercatorProjection.toLatitude(pos.y);
        float groundscale = (float) MercatorProjection
                .groundResolutionWithScale(lat, 1 << pos.zoomLevel);


        float scale = 1f / groundscale;

        synchronized (g3d) {
            // remove if out of visible zoom range
            g3d.instances.removeAll(added, true);
            if (pos.getZoomLevel() >= MIN_ZOOM) {
                g3d.instances.addAll(added);
            }

            for (ModelInstance inst : added) {
                ModelPosition p = (ModelPosition) inst.userData;

                float dx = (float) ((p.x - pos.x) * (Tile.SIZE << pos.zoomLevel));
                float dy = (float) ((p.y - pos.y) * (Tile.SIZE << pos.zoomLevel));

                inst.transform.idt();
                inst.transform.scale(scale, scale, scale);
                inst.transform.translate(dx / scale, dy / scale, 0);
                inst.transform.rotate(0, 0, 1, p.getRotation());
            }
        }

        g3d.cam.setMapPosition(pos.x, pos.y, 1 << pos.getZoomLevel());
    }
}
