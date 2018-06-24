/*
 * Copyright 2013 Hannes Janetzek
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

import org.oscim.layers.tile.TileLayer;
import org.oscim.layers.tile.TileManager;
import org.oscim.layers.tile.TileRenderer;
import org.oscim.layers.tile.ZoomLimiter;
import org.oscim.map.Map;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.LayerRenderer;
import org.oscim.renderer.OffscreenRenderer;
import org.oscim.renderer.OffscreenRenderer.Mode;
import org.oscim.tiling.TileSource;

public class S3DBTileLayer extends TileLayer {

    private final static int MAX_CACHE = 32;

    private final static int MIN_ZOOM = 16;
    private final static int MAX_ZOOM = 16;

    private final TileSource mTileSource;

    public S3DBTileLayer(Map map, TileSource tileSource) {
        this(map, tileSource, true, false);
    }

    /**
     * Simple-3D-Buildings OSCIM4 Tile Layer
     *
     * @param map        Stored map workaround
     * @param tileSource Source of loaded tiles in {@link org.oscim.layers.tile.vector.VectorTileLayer}
     * @param fxaa       Switch on Fast Approximate Anti-Aliasing
     * @param ssao       Switch on Screen Space Ambient Occlusion
     */
    public S3DBTileLayer(Map map, TileSource tileSource, boolean fxaa, boolean ssao) {
        super(map, new TileManager(map, MAX_CACHE));
        setRenderer(new S3DBTileRenderer(mTileManager, fxaa, ssao));

        mTileManager.setZoomLevel(MIN_ZOOM, MAX_ZOOM);
        mTileSource = tileSource;
        initLoader(2);
    }

    @Override
    protected S3DBTileLoader createLoader() {
        return new S3DBTileLoader(getManager(), mTileSource);
    }

    public static class S3DBTileRenderer extends TileRenderer {
        LayerRenderer mRenderer;

        public S3DBTileRenderer(TileManager manager, boolean fxaa, boolean ssao) {
            mRenderer = new BuildingRenderer(this, new ZoomLimiter(manager, MIN_ZOOM, MAX_ZOOM, MIN_ZOOM), true, false);

            if (fxaa || ssao) {
                Mode mode = Mode.FXAA;
                if (fxaa && ssao)
                    mode = Mode.SSAO_FXAA;
                else if (ssao)
                    mode = Mode.SSAO;
                mRenderer = new OffscreenRenderer(mode, mRenderer);
            }
        }

        @Override
        public synchronized void update(GLViewport v) {
            super.update(v);
            mRenderer.update(v);
            setReady(mRenderer.isReady());
        }

        @Override
        public synchronized void render(GLViewport v) {
            mRenderer.render(v);
        }

        @Override
        public boolean setup() {
            mRenderer.setup();
            return super.setup();
        }
    }
}
