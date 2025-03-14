/*
 * Copyright 2016-2019 devemux86
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
package org.oscim.test;

import com.badlogic.gdx.Input;
import org.oscim.gdx.GdxMapApp;
import org.oscim.theme.StreamRenderTheme;
import org.oscim.theme.XmlRenderThemeMenuCallback;
import org.oscim.theme.XmlRenderThemeStyleLayer;
import org.oscim.theme.XmlRenderThemeStyleMenu;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class MapsforgeStyleTest extends MapsforgeTest {

    private MapsforgeStyleTest(File demFolder, List<File> mapFiles, File themeFile) {
        super(demFolder, mapFiles, themeFile);
    }

    @Override
    void loadTheme(final String styleId) {
        mMap.setTheme(new StreamRenderTheme("", getClass().getResourceAsStream("/assets/vtm/stylemenu.xml"), new XmlRenderThemeMenuCallback() {
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

    @Override
    protected boolean onKeyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.NUM_1:
                loadTheme("1");
                mMap.clearMap();
                return true;
            case Input.Keys.NUM_2:
                loadTheme("2");
                mMap.clearMap();
                return true;
        }

        return super.onKeyDown(keycode);
    }

    /**
     * @param args command line args: expects the map files as multiple parameters
     *             with possible theme file as 1st argument
     *             and possible SRTM hgt folder as 2nd argument.
     */
    public static void main(String[] args) {
        GdxMapApp.init();
        File themeFile = getThemeFile(args);
        if (themeFile != null)
            args = Arrays.copyOfRange(args, 1, args.length);
        File demFolder = getDemFolder(args);
        if (demFolder != null)
            args = Arrays.copyOfRange(args, 1, args.length);
        GdxMapApp.run(new MapsforgeStyleTest(demFolder, getMapFiles(args), themeFile));
    }
}
