/*
 * Copyright 2017-2018 Longri
 * Copyright 2017-2018 devemux86
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

import org.oscim.event.Event;
import org.oscim.renderer.GLState;
import org.oscim.utils.Parameters;

public class AtlasThemeActivity extends SimpleMapActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Parameters.TEXTURE_ATLAS = true;
        super.onCreate(savedInstanceState);
    }

    @Override
    void createLayers() {
        // wait for gl surface view is created
        GLState.event.bind(new GLState.Listener() {
            @Override
            public void onGLStateInitEvent(Event event) {
                // load Theme with TextureAtlas
                AtlasThemeActivity.super.createLayers();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Parameters.TEXTURE_ATLAS = false;
    }
}
