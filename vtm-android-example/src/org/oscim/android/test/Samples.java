/*
 * Copyright 2010, 2011, 2012, 2013 mapsforge.org
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2019 devemux86
 * Copyright 2016 mar-v-in
 * Copyright 2016 Mathieu de Brito
 * Copyright 2017-2018 Longri
 * Copyright 2017 nebular
 * Copyright 2018 boldtrn
 * Copyright 2018-2019 Gustl22
 * Copyright 2019 Andrea Antonello
 * Copyright 2019 Kostas Tzounopoulos
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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Start screen for the sample activities.
 */
public class Samples extends Activity {

    private Button createButton(Class<?> clazz) {
        return this.createButton(clazz, null, null);
    }

    private Button createButton(final Class<?> clazz, String text, View.OnClickListener customListener) {
        Button button = new Button(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            button.setAllCaps(false);
        if (text == null) {
            button.setText(clazz.getSimpleName());
        } else {
            button.setText(text);
        }
        if (customListener == null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Samples.this, clazz));
                }
            });
        } else {
            button.setOnClickListener(customListener);
        }
        return button;
    }

    private TextView createLabel(String text) {
        TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER);
        if (text == null) {
            textView.setText("----------");
        } else {
            textView.setText(text);
        }
        return textView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_samples);
        LinearLayout linearLayout = findViewById(R.id.samples);
        linearLayout.addView(createButton(GettingStarted.class));
        linearLayout.addView(createLabel(null));
        linearLayout.addView(createButton(SimpleMapActivity.class));
        linearLayout.addView(createButton(MapsforgeActivity.class));
        linearLayout.addView(createButton(MapilionMvtActivity.class));
        /*linearLayout.addView(createButton(MapzenMvtActivity.class));
        linearLayout.addView(createButton(MapzenGeojsonActivity.class));
        linearLayout.addView(createButton(NextzenMvtActivity.class));
        linearLayout.addView(createButton(NextzenGeojsonActivity.class));
        linearLayout.addView(createButton(OpenMapTilesMvtActivity.class));*/
        linearLayout.addView(createButton(GdxActivity.class));
        linearLayout.addView(createButton(MBTilesMvtTileActivity.class));

        linearLayout.addView(createLabel("Features"));
        linearLayout.addView(createButton(null, "GraphHopper Routing", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/graphhopper/graphhopper/tree/master/android")));
            }
        }));
        linearLayout.addView(createButton(LocationActivity.class));
        linearLayout.addView(createButton(LocationTextureActivity.class));
        linearLayout.addView(createButton(PoiSearchActivity.class));

        linearLayout.addView(createLabel("Vector Features"));
        linearLayout.addView(createButton(MapsforgeStyleActivity.class));
        linearLayout.addView(createButton(MapsforgeS3DBActivity.class));
        linearLayout.addView(createButton(ShadowActivity.class));

        linearLayout.addView(createLabel("Raster Maps"));
        linearLayout.addView(createButton(BitmapTileActivity.class));
        linearLayout.addView(createButton(MBTilesBitmapTileActivity.class));

        linearLayout.addView(createLabel("Overlays"));
        linearLayout.addView(createButton(MarkerOverlayActivity.class));
        linearLayout.addView(createButton(PathOverlayActivity.class));
        linearLayout.addView(createButton(LineTexActivity.class));
        linearLayout.addView(createButton(VectorLayerActivity.class));
        linearLayout.addView(createButton(AtlasMultiTextureActivity.class));

        linearLayout.addView(createLabel("Experiments"));
        linearLayout.addView(createButton(ReverseGeocodeActivity.class));
        linearLayout.addView(createButton(ThemeStylerActivity.class));
        linearLayout.addView(createButton(JeoIndoorActivity.class));
        linearLayout.addView(createButton(GdxPoi3DActivity.class));
        linearLayout.addView(createButton(OverpassActivity.class));
        linearLayout.addView(createButton(ClusterMarkerOverlayActivity.class));
    }
}
