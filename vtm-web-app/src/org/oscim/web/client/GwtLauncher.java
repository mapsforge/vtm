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
package org.oscim.web.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.backends.gwt.preloader.Preloader.PreloaderCallback;
import com.badlogic.gdx.backends.gwt.preloader.Preloader.PreloaderState;

import org.oscim.core.Tile;
import org.oscim.gdx.client.MapConfig;

public class GwtLauncher extends GwtApplication {

    private ApplicationListener applicationListener;

    @Override
    public GwtApplicationConfiguration getConfig() {

        GwtApplicationConfiguration cfg =
                new GwtApplicationConfiguration(getWindowWidth(),
                        getWindowHeight());

        cfg.canvasId = "map-canvas";
        cfg.stencil = true;

        return cfg;
    }

    @Override
    public ApplicationListener getApplicationListener() {
        Tile.SIZE = MapConfig.get().getTileSize();

        if (applicationListener == null) {
            applicationListener = createApplicationListener();
        }
        return applicationListener;
    }

    @Override
    public ApplicationListener createApplicationListener() {
        return new GwtMap();
    }

    @Override
    public PreloaderCallback getPreloaderCallback() {
        return new PreloaderCallback() {

            @Override
            public void update(PreloaderState state) {
            }

            @Override
            public void error(String file) {
                //log.debug("error loading " + file);
            }
        };
    }

    private static native int getWindowWidth() /*-{
        return $wnd.innerWidth;
    }-*/;

    private static native int getWindowHeight() /*-{
        return $wnd.innerHeight;
    }-*/;

}
