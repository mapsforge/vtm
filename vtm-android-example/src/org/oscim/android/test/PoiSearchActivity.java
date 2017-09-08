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

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import org.mapsforge.poi.android.storage.AndroidPoiPersistenceManagerFactory;
import org.mapsforge.poi.storage.ExactMatchPoiCategoryFilter;
import org.mapsforge.poi.storage.PoiCategoryFilter;
import org.mapsforge.poi.storage.PoiCategoryManager;
import org.mapsforge.poi.storage.PoiPersistenceManager;
import org.mapsforge.poi.storage.PointOfInterest;
import org.oscim.android.filepicker.FilePicker;
import org.oscim.android.filepicker.FilterByFileExtension;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.oscim.event.Gesture;
import org.oscim.event.GestureListener;
import org.oscim.event.MotionEvent;
import org.oscim.layers.Layer;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.map.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.oscim.android.canvas.AndroidGraphics.drawableToBitmap;

/**
 * POI search.<br/>
 * Long press on map to search inside visible bounding box.<br/>
 * Tap on POIs to show their name (in default locale).
 */
public class PoiSearchActivity extends MapsforgeMapActivity implements ItemizedLayer.OnItemGestureListener<MarkerItem> {

    private static final Logger log = LoggerFactory.getLogger(PoiSearchActivity.class);

    private static String POI_FILE;
    private static final String POI_CATEGORY = "Restaurants";
    private static final int SELECT_POI_FILE = MapsforgeMapActivity.SELECT_THEME_FILE + 1;

    public static class PoiFilePicker extends FilePicker {
        public PoiFilePicker() {
            setFileDisplayFilter(new FilterByFileExtension(".poi"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Map events receiver
        mMap.layers().add(new PoiSearchActivity.MapEventsReceiver(mMap));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == SELECT_MAP_FILE) {
            if (mTileSource != null)
                startActivityForResult(new Intent(this, PoiFilePicker.class),
                        SELECT_POI_FILE);
            else
                finish();
        } else if (requestCode == SELECT_POI_FILE) {
            if (resultCode != RESULT_OK || intent == null || intent.getStringExtra(FilePicker.SELECTED_FILE) == null) {
                finish();
                return;
            }

            POI_FILE = intent.getStringExtra(FilePicker.SELECTED_FILE);
        }
    }

    @Override
    public boolean onItemSingleTapUp(int index, MarkerItem item) {
        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onItemLongPress(int index, MarkerItem item) {
        return false;
    }

    private class MapEventsReceiver extends Layer implements GestureListener {

        MapEventsReceiver(Map map) {
            super(map);
        }

        @Override
        public boolean onGesture(Gesture g, MotionEvent e) {
            if (g instanceof Gesture.LongPress) {
                // POI search
                new PoiSearchTask(PoiSearchActivity.this, POI_CATEGORY).execute(mMap.getBoundingBox(0));
                return true;
            }
            return false;
        }
    }

    private class PoiSearchTask extends AsyncTask<BoundingBox, Void, Collection<PointOfInterest>> {
        private final WeakReference<PoiSearchActivity> weakActivity;
        private final String category;

        private PoiSearchTask(PoiSearchActivity activity, String category) {
            this.weakActivity = new WeakReference<>(activity);
            this.category = category;
        }

        @Override
        protected Collection<PointOfInterest> doInBackground(BoundingBox... params) {
            // Search POI
            PoiPersistenceManager persistenceManager = null;
            try {
                persistenceManager = AndroidPoiPersistenceManagerFactory.getPoiPersistenceManager(POI_FILE);
                PoiCategoryManager categoryManager = persistenceManager.getCategoryManager();
                PoiCategoryFilter categoryFilter = new ExactMatchPoiCategoryFilter();
                categoryFilter.addCategory(categoryManager.getPoiCategoryByTitle(this.category));
                org.mapsforge.core.model.BoundingBox bb = new org.mapsforge.core.model.BoundingBox(
                        params[0].getMinLatitude(), params[0].getMinLongitude(),
                        params[0].getMaxLatitude(), params[0].getMaxLongitude());
                return persistenceManager.findInRect(bb, categoryFilter, null, Integer.MAX_VALUE);
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            } finally {
                if (persistenceManager != null) {
                    persistenceManager.close();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Collection<PointOfInterest> pointOfInterests) {
            PoiSearchActivity activity = weakActivity.get();
            if (activity == null) {
                return;
            }
            Toast.makeText(activity, category + ": " + (pointOfInterests != null ? pointOfInterests.size() : 0), Toast.LENGTH_SHORT).show();
            if (pointOfInterests == null) {
                return;
            }

            // Overlay POI
            Bitmap bitmap = drawableToBitmap(getResources(), R.drawable.marker_poi);
            MarkerSymbol symbol = new MarkerSymbol(bitmap, MarkerSymbol.HotspotPlace.CENTER);
            ItemizedLayer<MarkerItem> markerLayer = new ItemizedLayer<>(mMap, new ArrayList<MarkerItem>(), symbol, PoiSearchActivity.this);
            mMap.layers().add(markerLayer);
            List<MarkerItem> pts = new ArrayList<>();
            for (PointOfInterest pointOfInterest : pointOfInterests)
                pts.add(new MarkerItem(pointOfInterest.getName(), "", new GeoPoint(pointOfInterest.getLatitude(), pointOfInterest.getLongitude())));
            markerLayer.addItems(pts);
            mMap.render();
        }
    }
}
