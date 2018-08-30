/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 Stephan Leuschner
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
package org.oscim.android;

import android.content.Context;

import org.oscim.backend.AssetAdapter;

import java.io.IOException;
import java.io.InputStream;

public class AndroidAssets extends AssetAdapter {
    Context mContext;

    public static void init(Context ctx) {
        AssetAdapter.init(new AndroidAssets(ctx));
    }

    private AndroidAssets(Context ctx) {
        mContext = ctx.getApplicationContext();
    }

    @Override
    public InputStream openFileAsStream(String fileName) {
        try {
            return mContext.getAssets().open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
