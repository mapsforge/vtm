/*
 * Copyright 2018-2019 devemux86
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

public class MapsforgeS3DBTest extends MapsforgeTest {

    private MapsforgeS3DBTest(File demFolder, List<File> mapFiles) {
        super(demFolder, mapFiles, true, false);
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        File demFolder = getDemFolder(args);
        if (demFolder != null)
            args = Arrays.copyOfRange(args, 1, args.length);
        GdxMapApp.run(new MapsforgeS3DBTest(demFolder, getMapFiles(args)));
    }
}
