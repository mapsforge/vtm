/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2017 devemux86
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
package org.oscim.web.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.gwt.GwtApplication;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.GL;
import org.oscim.backend.GLAdapter;
import org.oscim.core.MapPosition;
import org.oscim.gdx.GdxAssets;
import org.oscim.gdx.GdxMap;
import org.oscim.gdx.client.GwtGdxGraphics;
import org.oscim.gdx.client.MapConfig;
import org.oscim.gdx.client.MapUrl;
import org.oscim.renderer.MapRenderer;
import org.oscim.web.js.JsMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GwtMap extends GdxMap {
    static final Logger log = LoggerFactory.getLogger(GwtMap.class);

    @Override
    public void create() {

        GwtGdxGraphics.init();
        GdxAssets.init("");
        CanvasAdapter.textScale = 0.7f;
        GLAdapter.init((GL) Gdx.graphics.getGL20());
        MapRenderer.setBackgroundColor(0xffffff);

        JsMap.init(mMap);

        if (GwtApplication.agentInfo().isLinux() &&
                GwtApplication.agentInfo().isFirefox())
            GwtGdxGraphics.NO_STROKE_TEXT = true;

        MapConfig c = MapConfig.get();
        super.create();

        MapPosition p = new MapPosition();
        p.setZoomLevel(c.getZoom());
        p.setPosition(c.getLatitude(), c.getLongitude());

        MapUrl mapUrl = new MapUrl(mMap);
        mapUrl.parseUrl(p);
        mapUrl.scheduleRepeating(5000);
    }

    private final native void createLayersN()/*-{
        $wnd.createLayers();
    }-*/;

    @Override
    protected void createLayers() {
        log.debug("<<< create layers >>>");
        createLayersN();
    }
}
