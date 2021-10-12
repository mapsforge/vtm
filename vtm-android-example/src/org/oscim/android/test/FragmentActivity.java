/*
 * Copyright 2020 Meibes
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

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

@SuppressWarnings("deprecation")
public class FragmentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_fragment);

        setTitle(getClass().getSimpleName());

        if (savedInstanceState == null) {
            android.app.Fragment mapFragment = MapFragment.newInstance();
            android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.fragment_container, mapFragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fragment_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.replace_fragment) {
            android.app.Fragment blankFragment = BlankFragment.newInstance();
            android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, blankFragment);
            ft.addToBackStack(null);
            ft.commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
