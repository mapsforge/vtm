package org.oscim.android.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
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

public class MapFragmentActivity extends FragmentActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tile.SIZE = Tile.calculateTileSize(getResources().getDisplayMetrics().scaledDensity);
        setContentView(R.layout.activity_map_fragment);

        setTitle(getClass().getSimpleName());

        MapFragment newFragment = new MapFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, newFragment)
                .commit();
    }

    public static class MapFragment extends Fragment {

        private MapView mMapView;
        private Map mMap;
        private TileSource mTileSource;
        private MapPreferences mPrefs;
        private VectorTileLayer mBaseLayer;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View contentView = super.onCreateView(inflater, container, savedInstanceState);
            if (contentView == null) {
                contentView = inflater.inflate(R.layout.fragment_map, container, false);
            }
            return contentView;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            mMapView = (MapView) view.findViewById(R.id.mapView);
            mMapView.setZOrderOnTop(true);
            mMap = mMapView.map();
            mPrefs = new MapPreferences(MapFragment.class.getName(), getContext());

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
    }
}
