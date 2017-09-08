/*
 * Copyright 2017 devemux86
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

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import org.oscim.backend.CanvasAdapter;
import org.oscim.core.GeoPoint;
import org.oscim.core.GeometryBuffer;
import org.oscim.core.MercatorProjection;
import org.oscim.core.Point;
import org.oscim.core.Tag;
import org.oscim.core.Tile;
import org.oscim.event.Gesture;
import org.oscim.event.GestureListener;
import org.oscim.event.MotionEvent;
import org.oscim.layers.Layer;
import org.oscim.layers.TileGridLayer;
import org.oscim.map.Map;
import org.oscim.tiling.source.mapfile.MapDatabase;
import org.oscim.tiling.source.mapfile.MapReadResult;
import org.oscim.tiling.source.mapfile.PointOfInterest;
import org.oscim.tiling.source.mapfile.Way;
import org.oscim.utils.GeoPointUtils;

import java.util.List;

/**
 * Reverse Geocoding with long press.
 * <p/>
 * - POI in specified radius.<br/>
 * - Ways containing touch point.
 */
public class ReverseGeocodeActivity extends MapsforgeMapActivity {

    private static final int TOUCH_RADIUS = 32 / 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Map events receiver
        mMap.layers().add(new MapEventsReceiver(mMap));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == SELECT_MAP_FILE) {
            // For debug
            TileGridLayer gridLayer = new TileGridLayer(mMap, getResources().getDisplayMetrics().density);
            mMap.layers().add(gridLayer);
        }
    }

    private class MapEventsReceiver extends Layer implements GestureListener {

        MapEventsReceiver(Map map) {
            super(map);
        }

        @Override
        public boolean onGesture(Gesture g, MotionEvent e) {
            if (g instanceof Gesture.LongPress) {
                GeoPoint p = mMap.viewport().fromScreenPoint(e.getX(), e.getY());

                // Read all labeled POI and ways for the area covered by the tiles under touch
                float touchRadius = TOUCH_RADIUS * CanvasAdapter.dpi / CanvasAdapter.DEFAULT_DPI;
                long mapSize = MercatorProjection.getMapSize((byte) mMap.getMapPosition().getZoomLevel());
                double pixelX = MercatorProjection.longitudeToPixelX(p.getLongitude(), mapSize);
                double pixelY = MercatorProjection.latitudeToPixelY(p.getLatitude(), mapSize);
                int tileXMin = MercatorProjection.pixelXToTileX(pixelX - touchRadius, (byte) mMap.getMapPosition().getZoomLevel());
                int tileXMax = MercatorProjection.pixelXToTileX(pixelX + touchRadius, (byte) mMap.getMapPosition().getZoomLevel());
                int tileYMin = MercatorProjection.pixelYToTileY(pixelY - touchRadius, (byte) mMap.getMapPosition().getZoomLevel());
                int tileYMax = MercatorProjection.pixelYToTileY(pixelY + touchRadius, (byte) mMap.getMapPosition().getZoomLevel());
                Tile upperLeft = new Tile(tileXMin, tileYMin, (byte) mMap.getMapPosition().getZoomLevel());
                Tile lowerRight = new Tile(tileXMax, tileYMax, (byte) mMap.getMapPosition().getZoomLevel());
                MapReadResult mapReadResult = ((MapDatabase) mTileSource.getDataSource()).readLabels(upperLeft, lowerRight);

                StringBuilder sb = new StringBuilder();

                // Filter POI
                sb.append("*** POI ***");
                for (PointOfInterest pointOfInterest : mapReadResult.pointOfInterests) {
                    Point layerXY = new Point();
                    mMap.viewport().toScreenPoint(pointOfInterest.position, false, layerXY);
                    Point tapXY = new Point(e.getX(), e.getY());
                    if (layerXY.distance(tapXY) > touchRadius) {
                        continue;
                    }
                    sb.append("\n");
                    List<Tag> tags = pointOfInterest.tags;
                    for (Tag tag : tags) {
                        sb.append("\n").append(tag.key).append("=").append(tag.value);
                    }
                }

                // Filter ways
                sb.append("\n\n").append("*** WAYS ***");
                for (Way way : mapReadResult.ways) {
                    if (way.geometryType != GeometryBuffer.GeometryType.POLY
                            || !GeoPointUtils.contains(way.geoPoints[0], p)) {
                        continue;
                    }
                    sb.append("\n");
                    List<Tag> tags = way.tags;
                    for (Tag tag : tags) {
                        sb.append("\n").append(tag.key).append("=").append(tag.value);
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(ReverseGeocodeActivity.this);
                builder.setIcon(android.R.drawable.ic_menu_search);
                builder.setTitle(R.string.dialog_reverse_geocoding_title);
                builder.setMessage(sb);
                builder.setPositiveButton(R.string.ok, null);
                builder.show();

                return true;
            }
            return false;
        }
    }
}
