/* Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2012 Hannes Janetzek
 * Copyright 2016-2021 devemux86
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
package org.oscim.app;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Toast;
import org.oscim.android.MapView;
import org.oscim.app.location.Compass;
import org.oscim.app.location.LocationDialog;
import org.oscim.app.location.LocationHandler;
import org.oscim.app.preferences.EditPreferences;
import org.oscim.core.GeoPoint;
import org.oscim.overlay.DistanceTouchOverlay;
import org.osmdroid.location.POI;
import org.osmdroid.overlays.MapEventsReceiver;

import java.util.Map;
import java.util.logging.Logger;

public class TileMap extends MapActivity implements MapEventsReceiver {
    private static final Logger log = Logger.getLogger(TileMap.class.getName());

    private static final int DIALOG_ENTER_COORDINATES = 0;
    private static final int DIALOG_LOCATION_PROVIDER_DISABLED = 2;

    //private static final int SELECT_RENDER_THEME_FILE = 1;
    protected static final int POIS_REQUEST = 2;

    private LocationHandler mLocation;
    private Compass mCompass;

    private Menu mMenu = null;
    private MapLayers mMapLayers;

    public MapLayers getMapLayers() {
        return mMapLayers;
    }

    private DistanceTouchOverlay mDistanceTouch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        setContentView(R.layout.activity_tilemap);
        App.view = (MapView) findViewById(R.id.mapView);
        registerMapView(App.view);

        App.map = mMap;
        App.activity = this;

        mMapLayers = new MapLayers();
        mMapLayers.setBaseMap(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (!prefs.contains("distanceTouch"))
            prefs.edit().putBoolean("distanceTouch", true).apply();

        if (prefs.getBoolean("distanceTouch", true)) {
            mDistanceTouch = new DistanceTouchOverlay(mMap, this);
            mMap.layers().add(mDistanceTouch);
        }

        mCompass = new Compass(this, mMap);
        mMap.layers().add(mCompass);

        mLocation = new LocationHandler(this, mCompass);

        App.poiSearch = new POISearch();
        App.routeSearch = new RouteSearch();

        registerForContextMenu(App.view);

        handleIntent(getIntent(), true);
    }

    public Compass getCompass() {
        return mCompass;
    }

    public LocationHandler getLocationHandler() {
        return mLocation;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, false);
    }

    private void handleIntent(Intent intent, boolean start) {
        if (intent == null)
            return;

        Uri uri = intent.getData();
        if (uri != null) {
            String scheme = uri.getSchemeSpecificPart();
            log.fine("got intent: " + (scheme == null ? "" : scheme));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        mMenu = menu;
        toggleMenuCheck();
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_info_about) {
            startActivity(new Intent(this, InfoView.class));
        } else if (itemId == R.id.menu_position) {
        } else if (itemId == R.id.menu_poi_nearby) {
            Intent intent = new Intent(this, POIActivity.class);
            startActivityForResult(intent, TileMap.POIS_REQUEST);
        } else if (itemId == R.id.menu_compass_2d) {
            if (!item.isChecked()) {
                // FIXME
                //mMapView.getMapViewPosition().setTilt(0);
                mCompass.setMode(Compass.Mode.C2D);
            } else {
                mCompass.setMode(Compass.Mode.OFF);
            }
        } else if (itemId == R.id.menu_compass_3d) {
            if (!item.isChecked()) {
                mCompass.setMode(Compass.Mode.C3D);
            } else {
                mCompass.setMode(Compass.Mode.OFF);
            }
        } else if (itemId == R.id.menu_position_my_location_enable) {
            if (!item.isChecked()) {
                mLocation.setMode(LocationHandler.Mode.SHOW);
                mLocation.setCenterOnFirstFix();
            } else {
                mLocation.setMode(LocationHandler.Mode.OFF);
            }
        } else if (itemId == R.id.menu_position_follow_location) {
            if (!item.isChecked()) {
                mLocation.setMode(LocationHandler.Mode.SNAP);
            } else {
                mLocation.setMode(LocationHandler.Mode.OFF);
            }
        } else if (itemId == R.id.menu_layer_openstreetmap || itemId == R.id.menu_layer_naturalearth) {
            int bgId = item.getItemId();
            // toggle if already enabled
            if (bgId == mMapLayers.getBackgroundId())
                bgId = -1;

            mMapLayers.setBackgroundMap(bgId);
            mMap.updateMap(true);
        } else if (itemId == R.id.menu_layer_grid) {
            mMapLayers.enableGridOverlay(this, !mMapLayers.isGridEnabled());
            mMap.updateMap(true);
        } else if (itemId == R.id.menu_position_enter_coordinates) {
            showDialog(DIALOG_ENTER_COORDINATES);
        /*} else if (itemId == R.id.menu_position_map_center) {
            MapPosition mapCenter = mBaseLayer.getMapFileCenter();
            if (mapCenter != null)
                mMap.setCenter(mapCenter.getGeoPoint());*/
        } else if (itemId == R.id.menu_preferences) {
            startActivity(new Intent(this, EditPreferences.class));
            overridePendingTransition(R.anim.slide_right, R.anim.slide_left2);
        } else {
            return false;
        }

        toggleMenuCheck();

        return true;
    }

    private void toggleMenuCheck() {

        mMenu.findItem(R.id.menu_compass_2d)
                .setChecked(mCompass.getMode() == Compass.Mode.C2D);
        mMenu.findItem(R.id.menu_compass_3d)
                .setChecked(mCompass.getMode() == Compass.Mode.C3D);

        mMenu.findItem(R.id.menu_position_my_location_enable)
                .setChecked(mLocation.getMode() == LocationHandler.Mode.SHOW);
        mMenu.findItem(R.id.menu_position_follow_location)
                .setChecked(mLocation.getMode() == LocationHandler.Mode.SNAP);

        int bgId = mMapLayers.getBackgroundId();
        mMenu.findItem(R.id.menu_layer_naturalearth)
                .setChecked(bgId == R.id.menu_layer_naturalearth);

        mMenu.findItem(R.id.menu_layer_openstreetmap)
                .setChecked(bgId == R.id.menu_layer_openstreetmap);

        mMenu.findItem(R.id.menu_layer_grid)
                .setChecked(mMapLayers.isGridEnabled());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (!isPreHoneyComb()) {
            menu.clear();
            onCreateOptionsMenu(menu);
        }

        menu.findItem(R.id.menu_position_map_center).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case POIS_REQUEST:
                log.fine("result: POIS_REQUEST");
                if (resultCode == RESULT_OK) {
                    int id = intent.getIntExtra("ID", 0);
                    log.fine("result: POIS_REQUEST: " + id);

                    App.poiSearch.poiMarkers.showBubbleOnItem(id);
                    POI poi = App.poiSearch.getPOIs().get(id);

                    if (poi.bbox != null)
                        mMap.animator().animateTo(poi.bbox);
                    else
                        mMap.animator().animateTo(poi.location);
                }
                break;
            default:
                break;
        }
    }

    static boolean isPreHoneyComb() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (id == DIALOG_ENTER_COORDINATES) {
            if (mLocationDialog == null)
                mLocationDialog = new LocationDialog();

            return mLocationDialog.createDialog(this);

        } else if (id == DIALOG_LOCATION_PROVIDER_DISABLED) {
            builder.setIcon(android.R.drawable.ic_menu_info_details);
            builder.setTitle(R.string.error);
            builder.setMessage(R.string.no_location_provider_available);
            builder.setPositiveButton(R.string.ok, null);
            return builder.create();
        } else {
            // no dialog will be created
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCompass.pause();
        mLocation.pause();
    }

    LocationDialog mLocationDialog;

    @SuppressWarnings("deprecation")
    @Override
    protected void onPrepareDialog(int id, final Dialog dialog) {
        if (id == DIALOG_ENTER_COORDINATES) {

            mLocationDialog.prepareDialog(mMap, dialog);

        } else {
            super.onPrepareDialog(id, dialog);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCompass.resume();
        mLocation.resume();

        mMapLayers.setPreferences(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (preferences.getBoolean("fullscreen", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }

        App.lockOrientation(this);

        boolean distanceTouch = preferences.getBoolean("distanceTouch", true);
        if (distanceTouch) {
            if (mDistanceTouch == null) {
                mDistanceTouch = new DistanceTouchOverlay(mMap, this);
                mMap.layers().add(mDistanceTouch);
            }
        } else {
            mMap.layers().remove(mDistanceTouch);
            mDistanceTouch = null;
        }

        mMap.updateMap(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Uses the UI thread to display the given text message as toast
     * notification.
     *
     * @param text the text message to display
     */
    public void showToastOnUiThread(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(TileMap.this, text, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private enum Mode {
        DEFAULT,
        SHOW_LOCATION,
        SNAP_LOCATION,
        COMPASS_2D,
        COMPASS_3D,
    }

    private int mMapMode = 0;

    public void toggleLocation(View V) {

        ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);

        mMapMode += 1;
        mMapMode %= Mode.values().length;

        setInteractionMode(mMapMode);
    }

    private void setInteractionMode(int mapMode) {
        Mode m = Mode.values()[mapMode];

        switch (m) {
            case DEFAULT:

                mLocation.setMode(LocationHandler.Mode.OFF);
                mCompass.setMode(Compass.Mode.OFF);

                App.activity.showToastOnUiThread("Manual");

                break;
            case SHOW_LOCATION:
                mLocation.setMode(LocationHandler.Mode.SHOW);
                mCompass.setMode(Compass.Mode.OFF);
                App.activity.showToastOnUiThread(App.activity
                        .getString(R.string.menu_position_my_location_enable));
                break;

            case SNAP_LOCATION:
                mLocation.setMode(LocationHandler.Mode.SNAP);
                mCompass.setMode(Compass.Mode.OFF);
                App.activity.showToastOnUiThread(App.activity
                        .getString(R.string.menu_position_follow_location));
                break;

            case COMPASS_2D:
                // FIXME
                //mMapView.getMapViewPosition().setTilt(0);

                mLocation.setMode(LocationHandler.Mode.SHOW);
                mCompass.setMode(Compass.Mode.C2D);
                App.activity.showToastOnUiThread("Compass 2D");
                break;

            case COMPASS_3D:
                mLocation.setMode(LocationHandler.Mode.SHOW);
                mCompass.setMode(Compass.Mode.C3D);
                App.activity.showToastOnUiThread("Compass 3D");
                break;

            default:
                break;
        }

        App.map.updateMap(true);
    }

    /**
     * Context Menu when clicking on the {@link Map}
     */
    private GeoPoint mLongPressGeoPoint;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);

        if (App.poiSearch.getPOIs().isEmpty())
            menu.removeItem(R.id.menu_poi_clear);

        if (App.routeSearch.isEmpty())
            menu.removeItem(R.id.menu_route_clear);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (App.poiSearch.onContextItemSelected(item, mLongPressGeoPoint))
            return true;

        if (App.routeSearch.onContextItemSelected(item, mLongPressGeoPoint))
            return true;

        return super.onContextItemSelected(item);
    }

    /**
     * MapEventsReceiver implementation
     */
    @Override
    public boolean singleTapUpHelper(GeoPoint p) {
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        mLongPressGeoPoint = p;
        openContextMenu(App.view);
        return true;
    }

    @Override
    public boolean longPressHelper(final GeoPoint p1, final GeoPoint p2) {
        ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);
        showToastOnUiThread("Distance Touch!");
        App.routeSearch.showRoute(p1, p2);
        return true;
    }
}
