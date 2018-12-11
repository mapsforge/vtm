/*
 * Copyright 2017-2018 devemux86
 * Copyright 2018 Gustl22
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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.mapsforge.core.model.Tag;
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
public class PoiSearchActivity extends MapsforgeActivity implements ItemizedLayer.OnItemGestureListener<MarkerItem> {

    private static final Logger log = LoggerFactory.getLogger(PoiSearchActivity.class);

    private static final String POI_CATEGORY = "Restaurants";
    private static final int SELECT_POI_FILE = MapsforgeActivity.SELECT_THEME_FILE + 1;

    private ItemizedLayer<MarkerItem> mMarkerLayer;
    private PoiPersistenceManager mPersistenceManager;

    public static class PoiFilePicker extends FilePicker {
        public PoiFilePicker() {
            setFileDisplayFilter(new FilterByFileExtension(".poi"));
        }
    }

    public PoiSearchActivity() {
        super(false, R.layout.activity_map_poi);
    }

    private void initSearch() {
        final List<Pattern> searchPatterns = new ArrayList<>();

        final PatternAdapter adapter = new PatternAdapter(this, searchPatterns);
        ListView searchList = (ListView) findViewById(R.id.search_list);
        searchList.setAdapter(adapter);

        Button addItem = (Button) findViewById(R.id.add_item);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPatterns.add(new Pattern("", ""));
                adapter.notifyDataSetChanged();
            }
        });

        Button startSearch = (Button) findViewById(R.id.start_search);
        startSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear overlays
                mMarkerLayer.removeAllItems();
                mMap.render();

                // POI search
                List<Tag> tags = new ArrayList<>();
                for (Pattern pattern : searchPatterns)
                    tags.add(new Tag(pattern.key, pattern.val));
                new PoiSearchTask(PoiSearchActivity.this, null, tags).execute(mMap.getBoundingBox(0));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initSearch();

        // Map events receiver
        mMap.layers().add(new PoiSearchActivity.MapEventsReceiver(mMap));
    }

    @Override
    protected void onDestroy() {
        if (mPersistenceManager != null)
            mPersistenceManager.close();

        super.onDestroy();
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

            String file = intent.getStringExtra(FilePicker.SELECTED_FILE);
            mPersistenceManager = AndroidPoiPersistenceManagerFactory.getPoiPersistenceManager(file);

            Bitmap bitmap = drawableToBitmap(getResources().getDrawable(R.drawable.marker_green));
            MarkerSymbol symbol = new MarkerSymbol(bitmap, MarkerSymbol.HotspotPlace.BOTTOM_CENTER);
            mMarkerLayer = new ItemizedLayer<>(mMap, new ArrayList<MarkerItem>(), symbol, this);
            mMap.layers().add(mMarkerLayer);
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

    public void onToggleControls(View view) {
        findViewById(R.id.controls).setVisibility(((ToggleButton) view).isChecked() ?
                View.VISIBLE : View.GONE);
    }

    private class MapEventsReceiver extends Layer implements GestureListener {

        MapEventsReceiver(Map map) {
            super(map);
        }

        @Override
        public boolean onGesture(Gesture g, MotionEvent e) {
            if (g instanceof Gesture.LongPress) {
                // Clear overlays
                mMarkerLayer.removeAllItems();
                mMap.render();
                // POI search
                new PoiSearchTask(PoiSearchActivity.this, POI_CATEGORY, null).execute(mMap.getBoundingBox(0));
                return true;
            }
            return false;
        }
    }

    private class Pattern {
        String key;
        String val;

        Pattern(String key, String val) {
            this.key = key;
            this.val = val;
        }
    }

    private class PatternAdapter extends ArrayAdapter<Pattern> {
        PatternAdapter(Context context, List<Pattern> patterns) {
            super(context, 0, patterns);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Pattern pattern = getItem(position);
            assert pattern != null;
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_poi_search, parent, false);

            // Populate the data
            EditText etKey = (EditText) convertView.findViewById(R.id.key);
            etKey.removeTextChangedListener((PatternTextWatcher) etKey.getTag()); // remove previous listeners
            etKey.setText(pattern.key); // set text when no listener is attached
            PatternTextWatcher ptwKey = new PatternTextWatcher(pattern, true);
            etKey.setTag(ptwKey);
            etKey.addTextChangedListener(ptwKey);

            EditText etValue = (EditText) convertView.findViewById(R.id.value);
            etValue.removeTextChangedListener((PatternTextWatcher) etValue.getTag());
            etValue.setText(pattern.val);
            PatternTextWatcher ptwVal = new PatternTextWatcher(pattern, false);
            etValue.setTag(ptwVal);
            etValue.addTextChangedListener(ptwVal);

            Button remove = (Button) convertView.findViewById(R.id.remove);
            remove.setTag(pattern);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Pattern pat = (Pattern) v.getTag();
                    remove(pat);
                }
            });

            return convertView;
        }
    }

    private class PatternTextWatcher implements TextWatcher {
        private Pattern pattern;
        private boolean isKey;

        PatternTextWatcher(Pattern pattern, boolean isKey) {
            this.pattern = pattern;
            this.isKey = isKey;
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (isKey)
                pattern.key = s.toString();
            else
                pattern.val = s.toString();
        }
    }

    private class PoiSearchTask extends AsyncTask<BoundingBox, Void, Collection<PointOfInterest>> {
        private final WeakReference<PoiSearchActivity> weakActivity;
        private final String category;
        private final List<Tag> patterns;

        private PoiSearchTask(PoiSearchActivity activity, String category, List<Tag> patterns) {
            this.weakActivity = new WeakReference<>(activity);
            this.category = category;
            this.patterns = patterns;
        }

        @Override
        protected Collection<PointOfInterest> doInBackground(BoundingBox... params) {
            // Search POI
            try {
                PoiCategoryManager categoryManager = mPersistenceManager.getCategoryManager();
                PoiCategoryFilter categoryFilter = new ExactMatchPoiCategoryFilter();
                if (category != null)
                    categoryFilter.addCategory(categoryManager.getPoiCategoryByTitle(category));
                org.mapsforge.core.model.BoundingBox bb = new org.mapsforge.core.model.BoundingBox(
                        params[0].getMinLatitude(), params[0].getMinLongitude(),
                        params[0].getMaxLatitude(), params[0].getMaxLongitude());
                return mPersistenceManager.findInRect(bb, categoryFilter, patterns, Integer.MAX_VALUE);
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Collection<PointOfInterest> pointOfInterests) {
            PoiSearchActivity activity = weakActivity.get();
            if (activity == null) {
                return;
            }
            Toast.makeText(activity, (category != null ? category : "Results") + ": "
                    + (pointOfInterests != null ? pointOfInterests.size() : 0), Toast.LENGTH_SHORT).show();
            if (pointOfInterests == null) {
                return;
            }

            // Overlay POI
            List<MarkerItem> pts = new ArrayList<>();
            for (PointOfInterest pointOfInterest : pointOfInterests)
                pts.add(new MarkerItem(pointOfInterest.getName(), "", new GeoPoint(pointOfInterest.getLatitude(), pointOfInterest.getLongitude())));
            mMarkerLayer.addItems(pts);
            mMap.render();
        }
    }
}
