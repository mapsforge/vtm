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
package org.oscim.android.test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.jeo.map.Style;
import org.jeo.vector.VectorDataset;
import org.oscim.layers.OSMIndoorLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.test.JeoTest;
import org.oscim.theme.VtmThemes;
import org.oscim.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

public class JeoIndoorMapActivity extends BaseMapActivity {
    public static final Logger log = LoggerFactory.getLogger(JeoIndoorMapActivity.class);

    // from http://overpass-turbo.eu/s/2vp
    String PATH = "https://gist.github.com/anonymous/8960337/raw/overpass.geojson";
    //String PATH = "https://gist.github.com/hjanetzek/9280925/raw/overpass.geojson";

    private OSMIndoorLayer mIndoorLayer;

    public JeoIndoorMapActivity() {
        super(R.layout.jeo_indoor_map);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMap.addTask(new Runnable() {
            @Override
            public void run() {
                showToast("load data");
                InputStream is = null;
                try {
                    //    File file = new File(Environment.getExternalStorageDirectory()
                    //        .getAbsolutePath(), "osmindoor.json");
                    //    is = new FileInputStream(file);

                    URL url = new URL(PATH);
                    URLConnection conn = url.openConnection();
                    is = conn.getInputStream();
                    loadJson(is);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }
        });

        mMap.setTheme(VtmThemes.DEFAULT);

        mMap.layers().add(new BuildingLayer(mMap, mBaseLayer));
        mMap.layers().add(new LabelLayer(mMap, mBaseLayer));

        //    String file = Environment.getExternalStorageDirectory().getAbsolutePath();
        //    VectorDataset data = (VectorDataset) JeoTest.getJsonData(file + "/states.json", true);
        //    Style style = JeoTest.getStyle();
        //    mMap.layers().add(new JeoVectorLayer(mMap, data, style));
    }

    void loadJson(InputStream is) {
        showToast("got data");

        VectorDataset data = JeoTest.readGeoJson(is);
        Style style = JeoTest.getStyle();
        mIndoorLayer = new OSMIndoorLayer(mMap, data, style);
        mMap.layers().add(mIndoorLayer);

        showToast("data ready");
        mMap.updateMap(true);

        mIndoorLayer.activeLevels[0] = true;
        shift();
    }

    public void showToast(final String text) {
        final Context ctx = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(ctx, text, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    boolean mShift = true;

    public void shift() {
        if (!mShift)
            return;

        mMap.postDelayed(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    if (mIndoorLayer.activeLevels[i]) {
                        mIndoorLayer.activeLevels[i] = false;
                        mIndoorLayer.activeLevels[(i + 1) % 9] = true;
                        mIndoorLayer.update();
                        break;
                    }
                }
                shift();
            }
        }, 200);

    }

    public void onClick(View v) {
        mShift = false;

        if (mIndoorLayer == null)
            return;

        int i = 0;

        if (v instanceof ToggleButton) {
            ToggleButton b = (ToggleButton) v;
            i = (b.getTextOn().charAt(0) - '0') + 1;
        }

        if (i < 0 || i > 9)
            i = 0;

        mIndoorLayer.activeLevels[i] ^= true;
        if (v instanceof ToggleButton)
            ((ToggleButton) v).setChecked(mIndoorLayer.activeLevels[i]);
        log.debug(Arrays.toString(mIndoorLayer.activeLevels));
        mIndoorLayer.update();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* ignore saved position */
        //mMap.setMapPosition(49.417, 8.673, 1 << 17);
        mMap.setMapPosition(53.5620092, 9.9866457, 1 << 16);
    }
}
