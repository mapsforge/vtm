package org.oscim.android.test;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.oscim.android.MapView;
import org.oscim.backend.CanvasAdapter;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.renderer.GLViewport;
import org.oscim.scalebar.DefaultMapScaleBar;
import org.oscim.scalebar.MapScaleBar;
import org.oscim.scalebar.MapScaleBarLayer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.mapfile.MapFileTileSource;

import java.io.File;

/**
 * A very basic Android app example.
 * <p>
 * You'll need a map with filename berlin.map from download.mapsforge.org in device storage.
 * /storage/emulated/0/Android/data/org.oscim.android.test/files/berlin.map
 */
public class MapFragment extends Fragment {

    private MapView mapView;

    public static MapFragment newInstance() {
        MapFragment instance = new MapFragment();

        Bundle args = new Bundle();
        instance.setArguments(args);
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = new MapView(getActivity());
        RelativeLayout relativeLayout = view.findViewById(R.id.mapView);
        relativeLayout.addView(mapView);

        // Tile source
        MapFileTileSource tileSource = new MapFileTileSource();
        File file = new File(getActivity().getExternalFilesDir(null), "berlin.map");
        tileSource.setMapFile(file.getAbsolutePath());

        // Vector layer
        VectorTileLayer tileLayer = mapView.map().setBaseMap(tileSource);

        // Building layer
        mapView.map().layers().add(new BuildingLayer(mapView.map(), tileLayer));

        // Label layer
        mapView.map().layers().add(new LabelLayer(mapView.map(), tileLayer));

        // Render theme
        mapView.map().setTheme(VtmThemes.DEFAULT);

        // Scale bar
        MapScaleBar mapScaleBar = new DefaultMapScaleBar(mapView.map());
        MapScaleBarLayer mapScaleBarLayer = new MapScaleBarLayer(mapView.map(), mapScaleBar);
        mapScaleBarLayer.getRenderer().setPosition(GLViewport.Position.BOTTOM_LEFT);
        mapScaleBarLayer.getRenderer().setOffset(5 * CanvasAdapter.getScale(), 0);
        mapView.map().layers().add(mapScaleBarLayer);

        // Note: this map position is specific to Berlin area
        mapView.map().setMapPosition(52.517037, 13.38886, 1 << 12);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mapView != null) {
            mapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (mapView != null) {
            mapView.onDestroy();
            mapView = null;
        }
        super.onDestroyView();
    }
}
