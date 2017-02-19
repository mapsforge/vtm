package org.oscim.android.test;

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

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.markercluster.ClusteredItemizedLayer;
import org.oscim.layers.markercluster.ClusteredMarkerItem;
import org.oscim.layers.markercluster.ClusteredMarkerRenderer;
import org.oscim.theme.VtmThemes;

import java.util.ArrayList;

public class ClusteredItemsDemoActivity extends BaseMapActivity {

	final static int COUNT = 5;
	final float STEP = 100f / 110000f; // roughly 100 meters

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		mMap.setTheme(VtmThemes.DEFAULT);

		MapPosition pos = new MapPosition();
		mMap.getMapPosition(pos);
        pos.setZoomLevel(2);
        mMap.setMapPosition(pos);

        /////////////////////////

        // Create the itemized layer and assign cluster icon style
        ClusteredItemizedLayer layer = new ClusteredItemizedLayer(
                mMap,
                null,
                new ClusteredMarkerRenderer.ClusterStyle(0xffffffff, 0xff123456)
        );

        // add it top the map
        mMap.layers().add(layer);

        // create a symbol, for simplicity we will use this symbol for all created markers
		MarkerSymbol symbol = new MarkerSymbol(
				new AndroidBitmap(((BitmapDrawable)(getResources().getDrawable(R.drawable.marker_poi))).getBitmap()),
				MarkerSymbol.HotspotPlace.CENTER
		);


		// create some markers spaced STEP degrees
        GeoPoint center = pos.getGeoPoint();
        ArrayList<ClusteredMarkerItem> list= new ArrayList<>();

		for (int x = -COUNT; x < COUNT; x++) {
			for (int y = -COUNT; y < COUNT; y++) {

                double random = STEP * Math.random() * 2;

				ClusteredMarkerItem item = new ClusteredMarkerItem(
						"Demo Marker " + ((x * COUNT) + y),
						"Your typical marker in your typical map",
						new GeoPoint(center.getLatitude() + y * STEP + random, center.getLongitude() + x * STEP + random)
				);

				item.setMarker(symbol);
				list.add(item);
			}
		}

		// add'em all at once
		layer.addItems(list);

	}

}
