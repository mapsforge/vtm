/*
 * Copyright 2017 Longri
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
package org.oscim.theme_comparator;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Created by Longri on 17.08.2017.
 */
public class Utils {

    public static String getFile(String initialPath, final String extension, String TitleText) {

        final String ext = extension.replace("*", "");

        JFileChooser chooser = new JFileChooser();

        chooser.setCurrentDirectory(new java.io.File(initialPath));
        chooser.setDialogTitle(TitleText);

        FileFilter filter = new FileFilter() {

            @Override
            public String getDescription() {
                return extension;
            }

            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getAbsolutePath().endsWith(ext);
            }
        };

        chooser.setFileFilter(filter);

        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }
}
