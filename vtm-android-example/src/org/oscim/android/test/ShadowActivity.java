/*
 * Copyright 2019 Gustl22
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
import android.widget.SeekBar;

import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.renderer.light.Sun;

public class ShadowActivity extends SimpleMapActivity implements SeekBar.OnSeekBarChangeListener {

    public ShadowActivity() {
        super(R.layout.activity_shadow);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        BuildingLayer.SHADOW = true;
        super.onCreate(savedInstanceState);
        ((SeekBar) findViewById(R.id.seekBarShadow)).setOnSeekBarChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BuildingLayer.SHADOW = false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int id = seekBar.getId();
        if (id == R.id.seekBarShadow) {
            Sun sun = mBuildingLayer.getExtrusionRenderer().getSun();
            sun.setProgress(progress / 1000f);
            sun.updatePosition();
            sun.updateColor();

            mMap.updateMap(true);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
