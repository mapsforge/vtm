/*
 * Copyright 2014 Hannes Janetzek
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

import android.os.Bundle;

import org.oscim.backend.canvas.Color;
import org.oscim.core.MapPosition;
import org.oscim.event.Event;
import org.oscim.layers.vector.PathLayer;
import org.oscim.map.Map.UpdateListener;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a very INEFFICIENT and somewhat less useful example for how to use
 * PathLayers!
 */
public class PathOverlayActivity extends SimpleMapActivity {

    private static final boolean ANIMATION = true;

    private List<PathLayer> mPathLayers = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for (double lat = -90; lat <= 90; lat += 5) {
            int c = Color.fade(Color.rainbow((float) (lat + 90) / 180), 0.5f);
            PathLayer pathLayer = new PathLayer(mMap, c, 6);
            mMap.layers().add(pathLayer);
            mPathLayers.add(pathLayer);
        }

        if (ANIMATION)
            mMap.events.bind(new UpdateListener() {
                @Override
                public void onMapEvent(Event e, MapPosition mapPosition) {
                    //if (e == Map.UPDATE_EVENT) {
                    long t = System.currentTimeMillis();
                    float pos = t % 20000 / 10000f - 1f;
                    createLayers(pos);
                    mMap.updateMap(true);
                    //}
                }
            });
        else
            createLayers(1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* ignore saved position */
        mMap.setMapPosition(0, 0, 1 << 2);
    }

    void createLayers(float pos) {

        int i = 0;
        for (double lat = -90; lat <= 90; lat += 5) {
            double[] packedCoordinates = new double[360 + 2];
            //List<GeoPoint> pts = new ArrayList<>();
            int c = 0;
            for (double lon = -180; lon <= 180; lon += 2) {
                //pts.add(new GeoPoint(lat, lon));
                double longitude = lon;

                double latitude = lat + (pos * 90);
                if (latitude < -90)
                    latitude += 180;
                if (latitude > 90)
                    latitude -= 180;

                latitude += Math.sin((Math.abs(pos) * (lon / Math.PI)));

                packedCoordinates[c++] = longitude;
                packedCoordinates[c++] = latitude;
            }

            //LineString line = new LineString(factory.create(packedCoordinates, 2), geomFactory);
            //mPathLayers.get(i++).setLineString(line);

            mPathLayers.get(i++).setLineString(packedCoordinates);

        }
    }
}
