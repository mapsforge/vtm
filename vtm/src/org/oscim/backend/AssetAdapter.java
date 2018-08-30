/*
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
package org.oscim.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * The Class AssetAdapter.
 */
public abstract class AssetAdapter {

    /**
     * The instance provided by backend
     */
    static AssetAdapter g;

    /**
     * Open file from asset path as stream.
     */
    protected abstract InputStream openFileAsStream(String file);

    public static InputStream readFileAsStream(String file) {
        return g.openFileAsStream(file);
    }

    public static String readTextFile(String file) {
        StringBuilder sb = new StringBuilder();

        InputStream is = g.openFileAsStream(file);
        if (is == null)
            return null;

        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            while ((line = r.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();

    }

    public static void init(AssetAdapter adapter) {
        g = adapter;
    }
}
