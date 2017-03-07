/*
 * Copyright 2016-2017 devemux86
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
package org.oscim.test;

import org.oscim.core.GeometryBuffer.GeometryType;
import org.oscim.core.MapElement;
import org.oscim.core.MapPosition;
import org.oscim.core.MercatorProjection;
import org.oscim.event.Event;
import org.oscim.gdx.GdxMapApp;
import org.oscim.layers.tile.MapTile;
import org.oscim.layers.tile.MapTile.TileNode;
import org.oscim.layers.tile.TileLoader;
import org.oscim.layers.tile.TileManager;
import org.oscim.layers.tile.TileSet;
import org.oscim.layers.tile.VectorTileRenderer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.VectorTileLoader;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.map.Map;
import org.oscim.renderer.MapRenderer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.QueryResult;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;

public class TileRenderTest extends GdxMapApp {

    static boolean loadOneTile = true;
    //    static int tileX = 34365 >> 2;
    //    static int tileY = 21333 >> 2;
    //    static byte tileZ = 14;

    static int tileX = 68747 >> 3;
    static int tileY = 42648 >> 3;
    static byte tileZ = 14;

    @Override
    public void createLayers() {

        MapTile tile = new MapTile(null, tileX, tileY, tileZ);

        double w = 1.0 / (1 << tile.zoomLevel);
        double minLon = MercatorProjection.toLongitude(tile.x);
        double maxLon = MercatorProjection.toLongitude(tile.x + w);
        double minLat = MercatorProjection.toLatitude(tile.y + w);
        double maxLat = MercatorProjection.toLatitude(tile.y);
        double lat = minLat + (maxLat - minLat) / 2;
        double lon = minLon + (maxLon - minLon) / 2;

        MapPosition mapPosition = new MapPosition(lat, lon, 1 << tile.zoomLevel);

        mMap.setMapPosition(mapPosition);

        final TileManager tileManager;

        if (loadOneTile) {
            tile = new MapTile(new TileNode(), tileX, tileY, tileZ);
            /* setup tile quad-tree, expected for locking */
            tile.node.parent = tile.node;
            tile.node.parent.parent = tile.node;

            /* setup TileSet contatining one tile */
            final TileSet tiles = new TileSet();
            tiles.cnt = 1;
            tiles.tiles[0] = tile;
            tiles.lockTiles();

            tileManager = new TestTileManager(mMap, tiles);
        } else {
            /* create TileManager and calculate tiles for current position */
            tileManager = new TileManager(mMap, 100);
        }

        /* get the loader created by VectorTileLayer ... */
        final TestTileLoader[] tileLoader = {null};

        TestVectorTileLayer tileLayer = new TestVectorTileLayer(mMap, tileManager);
        tileLoader[0] = tileLayer.getTileLoader();

        TileSource tileSource = new OSciMap4TileSource();
        //TileSource tileSource = new TestTileSource();

        tileLayer.setTileSource(tileSource);

        //IRenderTheme theme = ThemeLoader.load(VtmThemes.OSMARENDER);
        //tileLayer.setRenderTheme(theme);
        //tileLayer.setRenderTheme(new DebugTheme());

        /* need to create the labellayer here to get the tileloaded event */
        LabelLayer labelLayer = new LabelLayer(mMap, tileLayer);

        //mMap.layers().add(tileLayer);
        mMap.setBaseMap(tileLayer);
        mMap.setTheme(VtmThemes.DEFAULT);

        log.debug("load tiles:");
        if (loadOneTile) {
            log.debug("load {}", tile);

            tileLoader[0].loadTile(tile);
            tileManager.jobCompleted(tile, QueryResult.SUCCESS);
        } else {
            tileManager.update(mapPosition);
            MapTile t = tileManager.getTileJob();
            while (t != null) {
                log.debug("load {}", t);

                tileLoader[0].loadTile(t);
                tileManager.jobCompleted(t, QueryResult.SUCCESS);

                t = tileManager.getTileJob();
            }
        }

        mMap.layers().add(labelLayer);

        MapRenderer.setBackgroundColor(0xff888888);
    }

    static class TestTileLoader extends VectorTileLoader {

        public TestTileLoader(VectorTileLayer tileLayer) {
            super(tileLayer);
        }

        @Override
        public boolean loadTile(MapTile tile) {
            mTile = tile;
            return super.loadTile(tile);
        }

        @Override
        public void process(MapElement element) {
            /* ignore polygons for testing */
            if (element.type != GeometryType.LINE)
                return;

            if (element.tags.containsKey("name"))
                super.process(element);
        }
    }

    static class TestVectorTileLayer extends VectorTileLayer {
        final VectorTileLoader[] tileLoader = {null};

        public TestVectorTileLayer(Map map, TileManager tileManager) {
            super(map, tileManager, new VectorTileRenderer());
        }

        TestTileLoader getTileLoader() {
            return (TestTileLoader) mTileLoader[0];
        }

        @Override
        protected int getNumLoaders() {
            return 1;
        }

        @Override
        protected void initLoader(int numLoaders) {
            mTileLoader = new TileLoader[numLoaders];
            for (int i = 0; i < numLoaders; i++) {
                mTileLoader[i] = new TestTileLoader(this);
            }
        }

        @Override
        public void onMapEvent(Event event, MapPosition mapPosition) {
            /* ignore map events */
            if (event != Map.CLEAR_EVENT)
                return;

            //super.onMapEvent(event, mapPosition);
        }
    }

    static class TestTileManager extends TileManager {
        TileSet fixedTiles;

        public TestTileManager(Map map, TileSet fixedTiles) {
            super(map, 100);
            this.fixedTiles = fixedTiles;
        }

        @Override
        public boolean getActiveTiles(TileSet tileSet) {
            if (tileSet == null)
                tileSet = new TileSet(fixedTiles.cnt);

            tileSet.setTiles(fixedTiles);
            return true;
        }
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new TileRenderTest());
    }
}
