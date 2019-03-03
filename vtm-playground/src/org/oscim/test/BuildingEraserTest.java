package org.oscim.test;

import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.*;
import org.oscim.gdx.GdxAssets;
import org.oscim.gdx.GdxMapApp;
import org.oscim.gdx.poi3d.GdxModelLayer;
import org.oscim.layers.GroupLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.map.Map;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.QueryResult;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.geojson.GeoJsonTileDecoder;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;

import java.io.IOException;

public class BuildingEraserTest extends GdxMapApp {

    @Override
    public void createLayers() {
        Map map = getMap();

        TileSource tileSource = OSciMap4TileSource.builder()
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                .build();
        VectorTileLayer l = map.setBaseMap(tileSource);

        final BuildingLayer buildingLayer = new BuildingLayer(map, l);

        GeoJsonTileDecoder gjd = new GeoJsonTileDecoder(null);

        Tile baseTile = new Tile(0,0,(byte)0);
        try {
            gjd.decode(baseTile, new ITileDataSink() {
                @Override
                public void process(MapElement element) {
                    buildingLayer.eraserPoints.addAll(element.geoPoints);
                }

                @Override
                public void setTileImage(Bitmap bitmap) {

                }

                @Override
                public void completed(QueryResult result) {

                }
            },getClass().getResourceAsStream("/assets/eraser.geojson"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        GroupLayer groupLayer = new GroupLayer(mMap);
        groupLayer.layers.add(buildingLayer);

        GdxModelLayer gdxModelLayer = new GdxModelLayer(mMap);
        mMap.layers().add(gdxModelLayer);
        gdxModelLayer.addModel(GdxAssets.getAssetPath("models/buildings/shibuya109.g3db"), 35.659582, 139.698956,33f,-90f);

        groupLayer.layers.add(new LabelLayer(map, l));
        map.layers().add(groupLayer);

        map.setTheme(VtmThemes.DEFAULT);
        MapPosition pos = MapPreferences.getMapPosition();
        if (pos != null)
            map.setMapPosition(pos);
        else
            map.setMapPosition(35.659582, 139.698956, 1 << 17);
    }

    @Override
    public void dispose() {
        MapPreferences.saveMapPosition(mMap.getMapPosition());
        super.dispose();
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new BuildingEraserTest());
    }
}
