/*
 * Copyright 2016 devemux86
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

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.oscim.android.theme.AssetsRenderTheme;
import org.oscim.theme.XmlRenderThemeMenuCallback;
import org.oscim.theme.XmlRenderThemeStyleLayer;
import org.oscim.theme.XmlRenderThemeStyleMenu;

import java.util.Set;

/**
 * Load render theme from Android assets folder and show a configuration menu based on stylemenu.
 */
public class MapsforgeStyleActivity extends MapsforgeMapActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.style_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.style_1:
                item.setChecked(true);
                loadTheme("1");
                mMap.clearMap();
                Toast.makeText(this, "Show nature layers", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.style_2:
                item.setChecked(true);
                loadTheme("2");
                mMap.clearMap();
                Toast.makeText(this, "Hide nature layers", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void loadTheme(final String styleId) {
        mMap.setTheme(new AssetsRenderTheme(this, "", "vtm/stylemenu.xml", new XmlRenderThemeMenuCallback() {
            @Override
            public Set<String> getCategories(XmlRenderThemeStyleMenu renderThemeStyleMenu) {
                // Use the selected style or the default
                String style = styleId != null ? styleId : renderThemeStyleMenu.getDefaultValue();

                // Retrieve the layer from the style id
                XmlRenderThemeStyleLayer renderThemeStyleLayer = renderThemeStyleMenu.getLayer(style);
                if (renderThemeStyleLayer == null) {
                    System.err.println("Invalid style " + style);
                    return null;
                }

                // First get the selected layer's categories that are enabled together
                Set<String> categories = renderThemeStyleLayer.getCategories();

                // Then add the selected layer's overlays that are enabled individually
                // Here we use the style menu, but users can use their own preferences
                for (XmlRenderThemeStyleLayer overlay : renderThemeStyleLayer.getOverlays()) {
                    if (overlay.isEnabled())
                        categories.addAll(overlay.getCategories());
                }

                // This is the whole categories set to be enabled
                return categories;
            }
        }));
    }
}
