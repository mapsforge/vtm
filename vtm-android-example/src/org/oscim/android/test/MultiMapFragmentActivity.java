package org.oscim.android.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.oscim.android.MapPreferences;
import org.oscim.android.MapView;
import org.oscim.core.MapPosition;
import org.oscim.core.Tile;
import org.oscim.layers.GroupLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.map.Map;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;

public class MultiMapFragmentActivity extends FragmentActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tile.SIZE = Tile.calculateTileSize(getResources().getDisplayMetrics().scaledDensity);
        setContentView(R.layout.activity_map_multi_fragment);

        setTitle(getClass().getSimpleName());

        addFragment();
    }

    public void addFragment() {
        MapFragment newFragment = new MapFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, newFragment)
                .addToBackStack(newFragment.getName())
                .commit();
    }

    @Override
    public void onBackPressed() {
        int nbMapFragments = getSupportFragmentManager().getBackStackEntryCount();
        if (nbMapFragments > 1) {
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.itmAddFragment:
                addFragment();
                return true;
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_multi_map_fragments_menu, menu);
        return true;
    }

    public static class MapFragment extends Fragment {

        private static int nbFragments = 0;

        private MapView mMapView;
        private Map mMap;
        private TileSource mTileSource;
        private MapPreferences mPrefs;
        private VectorTileLayer mBaseLayer;
        private int currentFragmentNumber;

        public MapFragment() {
            super();
            currentFragmentNumber = nbFragments;
            nbFragments++;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View contentView = super.onCreateView(inflater, container, savedInstanceState);
            if (contentView == null) {
                contentView = inflater.inflate(R.layout.fragment_map_multi_fragment, container, false);
            }
            return contentView;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            mMapView = (MapView) view.findViewById(R.id.mapView);
            mMap = mMapView.map();
            mPrefs = new MapPreferences(getName(), getContext());

            mTileSource = new OSciMap4TileSource();
            mBaseLayer = mMap.setBaseMap(mTileSource);

            GroupLayer groupLayer = new GroupLayer(mMap);
            groupLayer.layers.add(new BuildingLayer(mMap, mBaseLayer));
            groupLayer.layers.add(new LabelLayer(mMap, mBaseLayer));
            mMap.layers().add(groupLayer);
            mMap.setTheme(VtmThemes.DEFAULT);

            // set initial position on first run
            MapPosition pos = new MapPosition();
            mMap.getMapPosition(pos);
            if (pos.x == 0.5 && pos.y == 0.5)
                mMap.setMapPosition(53.08, 8.83, Math.pow(2, 16));
        }

        @Override
        public void onResume() {
            super.onResume();

            mPrefs.load(mMapView.map());
            mMapView.onResume();
        }

        @Override
        public void onPause() {
            super.onPause();

            mMapView.onPause();
            mPrefs.save(mMapView.map());
        }

        public String getName() {
            return MapFragment.class.getName() + currentFragmentNumber;
        }
    }
}
