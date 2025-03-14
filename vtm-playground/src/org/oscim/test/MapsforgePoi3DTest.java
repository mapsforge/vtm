/*
 * Copyright 2018-2019 devemux86
 * Copyright 2018 Gustl22
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

import org.oscim.gdx.GdxMapApp;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MapsforgePoi3DTest extends MapsforgeTest {

    private MapsforgePoi3DTest(File demFolder, List<File> mapFiles, File themeFile) {
        super(demFolder, mapFiles, false, true, themeFile);
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
        GdxMapApp.run(new MapsforgePoi3DTest(demFolder, getMapFiles(args), themeFile));
    }
}
