/*
 * Copyright 2016-2019 devemux86
 * Copyright 2018 Longri
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

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.MapPosition;
import org.oscim.layers.LocationTextureLayer;
import org.oscim.renderer.LocationCallback;
import org.oscim.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class LocationTextureActivity extends BitmapTileActivity implements LocationListener {
    private Location location;
    private LocationTextureLayer locationLayer;
    private LocationManager locationManager;
    private final MapPosition mapPosition = new MapPosition();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        InputStream is = null;
        Bitmap bitmapArrow = null;
        try {
            is = getResources().openRawResource(R.raw.arrow);
            bitmapArrow = CanvasAdapter.decodeSvgBitmap(is, (int) (48 * CanvasAdapter.getScale()), (int) (48 * CanvasAdapter.getScale()), 100);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }

        Bitmap bitmapMarker = null;
        try {
            is = getResources().openRawResource(R.raw.marker);
            bitmapMarker = CanvasAdapter.decodeSvgBitmap(is, (int) (48 * CanvasAdapter.getScale()), (int) (48 * CanvasAdapter.getScale()), 100);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }

        locationLayer = new LocationTextureLayer(mMap);
        locationLayer.locationRenderer.setBitmapArrow(bitmapArrow);
        locationLayer.locationRenderer.setBitmapMarker(bitmapMarker);
        locationLayer.locationRenderer.setCallback(new LocationCallback() {
            @Override
            public boolean hasRotation() {
                return location != null && location.hasBearing();
            }

            @Override
            public float getRotation() {
                return location != null && location.hasBearing() ? location.getBearing() : 0;
            }
        });
        locationLayer.setEnabled(false);
        mMap.layers().add(locationLayer);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                enableAvailableProviders();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        enableAvailableProviders();
    }

    @Override
    public void onStop() {
        locationManager.removeUpdates(this);

        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        locationLayer.setEnabled(true);
        locationLayer.setPosition(location.getLatitude(), location.getLongitude(), location.getAccuracy());

        // Follow location
        mMap.getMapPosition(mapPosition);
        mapPosition.setPosition(location.getLatitude(), location.getLongitude());
        mMap.setMapPosition(mapPosition);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    private void enableAvailableProviders() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                return;
            }
        }

        locationManager.removeUpdates(this);

        for (String provider : locationManager.getProviders(true)) {
            if (LocationManager.GPS_PROVIDER.equals(provider)
                    || LocationManager.NETWORK_PROVIDER.equals(provider)) {
                locationManager.requestLocationUpdates(provider, 0, 0, this);
            }
        }
    }
}
