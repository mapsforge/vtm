/*
 * Copyright 2016-2018 devemux86
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
package org.oscim.scalebar;

import org.oscim.core.MapPosition;
import org.oscim.event.Event;
import org.oscim.layers.Layer;
import org.oscim.map.Map;
import org.oscim.renderer.BitmapRenderer;

public class MapScaleBarLayer extends Layer implements Map.UpdateListener {
    private final MapScaleBar mapScaleBar;
    private final BitmapRenderer bitmapRenderer;

    public MapScaleBarLayer(Map map, MapScaleBar mapScaleBar) {
        super(map);
        this.mapScaleBar = mapScaleBar;

        mRenderer = bitmapRenderer = new MapScaleBarRenderer();
        bitmapRenderer.setBitmap(mapScaleBar.mapScaleBitmap, mapScaleBar.mapScaleBitmap.getWidth(), mapScaleBar.mapScaleBitmap.getHeight());
    }

    @Override
    public BitmapRenderer getRenderer() {
        return bitmapRenderer;
    }

    @Override
    public void onMapEvent(Event e, MapPosition mapPosition) {
        if (e == Map.UPDATE_EVENT)
            return;

        if (mapScaleBar.mapScaleBitmap == null)
            return;

        if (!mapScaleBar.isVisible())
            return;

        if (mMap.getHeight() == 0)
            return;

        if (!mapScaleBar.isRedrawNecessary())
            return;

        synchronized (mapScaleBar.mapScaleBitmap) {
            mapScaleBar.drawScaleBar();
        }

        bitmapRenderer.updateBitmap();

        mapScaleBar.redrawNeeded = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mapScaleBar.destroy();
    }
}
