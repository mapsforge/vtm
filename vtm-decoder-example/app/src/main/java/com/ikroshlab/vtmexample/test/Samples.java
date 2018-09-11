/*
 * Copyright 2010, 2011, 2012, 2013 mapsforge.org
 * Copyright 2013 Hannes Janetzek
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
package com.ikroshlab.vtmexample.test;


import com.ikroshlab.vtmexample.R;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple start screen for the sample activities.
 */
public class Samples extends AppCompatActivity {

    final private int  REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_samples);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // START PERMISSION CHECK
        getPermissions();
	}


    private void activate() {

        logDebug("Samples - activate()");

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.samples);
        linearLayout.addView(createButton(MapsforgeMapActivity.class, "MapsForge offline maps"));
        linearLayout.addView(createButton(ForgeOnlineMapActivity.class, "MapsForge online maps"));
    }


	private Button createButton(final Class<?> clazz) {
		return this.createButton(clazz, null);
	}

	private Button createButton(final Class<?> clazz, String text) {
		Button button = new Button(this);
		if (text == null) {
			button.setText(clazz.getSimpleName());
		} else {
			button.setText(text);
			button.setBackgroundColor(Color.CYAN);
		}
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(Samples.this, clazz));
			}
		});
		return button;
	}





    ///////////////////////////////// handling runtime permissions  ////////////////////////////////
    private void getPermissions() {

        List<String> permissions = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);    // not granted yet
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);  // not granted yet
        }
        if (!permissions.isEmpty()) {    // not all permissions have been granted yet
            String[] params = permissions.toArray(new String[permissions.size()]);
            requestPermissions(params, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

            return;
        }

        // We already have permissions, so handle as normal
        activate();

    }  // end getPermissions()





    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();

                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION,   PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                // Check for ACCESS_FINE_LOCATION and WRITE_EXTERNAL_STORAGE
                Boolean location = perms.get(Manifest.permission.ACCESS_FINE_LOCATION)   == PackageManager.PERMISSION_GRANTED;
                Boolean storage  = perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

                if (location && storage) {  // All Permissions Granted

                    activate();
                    //permissionsGrantedListener.onPermissionsGranted(true);
                }
                else {

                    //permissionsGrantedListener.onPermissionsGranted(false);
                }
            }
            break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    } // end onRequestPermissionsResult()
    ////////////////////////////////////////////////////////////////////////////////////////////////


    // handling Logging:
    private void logDebug(String msg) { Log.d("ikroshlab", " ### " + getClass().getName() + " ###  " + msg); }
}
