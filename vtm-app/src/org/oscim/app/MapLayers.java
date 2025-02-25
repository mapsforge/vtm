/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2020 devemux86
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
package org.oscim.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.oscim.android.cache.TileCache;
import org.oscim.layers.GenericLayer;
import org.oscim.layers.Layer;
import org.oscim.layers.TileGridLayer;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.theme.ThemeFile;
import org.oscim.theme.internal.VtmThemes;
import org.oscim.tiling.ITileCache;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.UrlTileSource;
import org.oscim.tiling.source.bitmap.DefaultSources;
import org.oscim.tiling.source.mapfile.MapFileTileSource;
import org.oscim.tiling.source.mapnik.MapnikVectorTileSource;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;

import java.io.File;
import java.util.Collections;

public class MapLayers {

    abstract static class Config {
        final String name;

        public Config(String name) {
            this.name = name;
        }

        abstract TileSource init();
    }

    static Config[] configs = new Config[]{new Config("OPENSCIENCEMAP4") {
        @Override
        TileSource init() {
            return new OSciMap4TileSource();
        }
    }, new Config("MAPSFORGE") {
        @Override
        TileSource init() {
            return new MapFileTileSource().setOption("file",
                    new File("/sdcard/berlin.map").getAbsolutePath());
        }
    }, new Config("MAPNIK_VECTOR") {
        @Override
        TileSource init() {
            return new MapnikVectorTileSource();
        }
    }};

    private VectorTileLayer mBaseLayer;
    private String mMapDatabase;
    private ITileCache mCache;

    private GenericLayer mGridOverlay;
    private boolean mGridEnabled;

    // FIXME -> implement LayerGroup
    private int mBackgroundId = -2;
    private Layer mBackroundPlaceholder;
    private Layer mBackgroundLayer;

    public MapLayers() {
        mBackroundPlaceholder = new Layer(null) {
        };
        setBackgroundMap(-1);
    }

    void setBaseMap(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String dbname = preferences.getString("mapDatabase", "OPENSCIENCEMAP4");

        if (dbname.equals(mMapDatabase) && mBaseLayer != null)
            return;

        TileSource tileSource = null;
        for (Config c : configs)
            if (c.name.equals(dbname))
                tileSource = c.init();

        if (tileSource == null) {
            tileSource = configs[0].init();
            dbname = configs[0].name;
            preferences.edit().putString("mapDatabase", dbname).commit();
        }

        if (tileSource instanceof UrlTileSource) {
            mCache = new TileCache(App.activity, context.getExternalCacheDir().getAbsolutePath(), dbname);
            mCache.setCacheSize(512 * (1 << 10));
            tileSource.setCache(mCache);
        } else {
            mCache = null;
        }

        if (mBaseLayer == null) {
            mBaseLayer = App.map.setBaseMap(tileSource);
            App.map.layers().add(2, new BuildingLayer(App.map, mBaseLayer));
            App.map.layers().add(3, new LabelLayer(App.map, mBaseLayer));
        } else
            mBaseLayer.setTileSource(tileSource);

        mMapDatabase = dbname;
    }

    void setPreferences(Context context) {
        setBaseMap(context);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        ThemeFile theme = VtmThemes.DEFAULT;
        if (preferences.contains("theme")) {
            String name = preferences.getString("theme", "DEFAULT");
            try {
                theme = VtmThemes.valueOf(name);
            } catch (IllegalArgumentException e) {
                theme = VtmThemes.DEFAULT;
            }
        }

        App.map.setTheme(theme);

        // default cache size 20MB
        int cacheSize = preferences.getInt("cacheSize", 20);

        if (mCache != null)
            mCache.setCacheSize(cacheSize * (1 << 20));

    }

    void enableGridOverlay(Context context, boolean enable) {
        if (mGridEnabled == enable)
            return;

        if (enable) {
            if (mGridOverlay == null)
                mGridOverlay = new TileGridLayer(App.map);

            App.map.layers().add(mGridOverlay);
        } else {
            App.map.layers().remove(mGridOverlay);
        }

        mGridEnabled = enable;
        App.map.updateMap(true);
    }

    boolean isGridEnabled() {
        return mGridEnabled;
    }

    void setBackgroundMap(int id) {
        if (id == mBackgroundId)
            return;

        App.map.layers().remove(mBackgroundLayer);
        mBackgroundLayer = null;

        if (id == R.id.menu_layer_openstreetmap) {
            UrlTileSource tileSource = DefaultSources.OPENSTREETMAP.build();
            tileSource.setHttpRequestHeaders(Collections.singletonMap("User-Agent", "vtm-playground"));
            mBackgroundLayer = new BitmapTileLayer(App.map, tileSource);
        } else if (id == R.id.menu_layer_naturalearth) {
            mBackgroundLayer = new BitmapTileLayer(App.map, DefaultSources.NE_LANDCOVER.build());
        } else {
            mBackgroundLayer = mBackroundPlaceholder;
            id = -1;
        }

        if (mBackgroundLayer instanceof BitmapTileLayer)
            App.map.setBaseMap((BitmapTileLayer) mBackgroundLayer);
        else
            App.map.layers().add(1, mBackroundPlaceholder);

        mBackgroundId = id;
    }

    int getBackgroundId() {
        return mBackgroundId;
    }

    public void deleteCache() {
        if (mCache != null)
            mCache.setCacheSize(0);
    }
}
