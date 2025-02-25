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
import com.badlogic.gdx.graphics.glutils.GLVersion;
import org.oscim.backend.*;
import org.oscim.core.MapPosition;
import org.oscim.gdx.GdxAssets;
import org.oscim.gdx.GdxMap;
import org.oscim.gdx.client.GwtDateTime;
import org.oscim.gdx.client.GwtGdxGraphics;
import org.oscim.gdx.client.MapConfig;
import org.oscim.gdx.client.MapUrl;
import org.oscim.renderer.MapRenderer;
import org.oscim.web.js.JsMap;

import java.util.logging.Logger;

public class GwtMap extends GdxMap {
    private static final Logger log = Logger.getLogger(GwtMap.class.getName());

    @Override
    public void create() {

        GwtGdxGraphics.init();
        GdxAssets.init("");
        DateTimeAdapter.init(new GwtDateTime());
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
    protected void initGLAdapter(GLVersion version) {
        if (version.getMajorVersion() >= 3)
            GLAdapter.init((GL30) Gdx.graphics.getGL30());
        else
            GLAdapter.init((GL) Gdx.graphics.getGL20());
    }

    @Override
    protected void createLayers() {
        log.fine("<<< create layers >>>");
        createLayersN();
    }
}
