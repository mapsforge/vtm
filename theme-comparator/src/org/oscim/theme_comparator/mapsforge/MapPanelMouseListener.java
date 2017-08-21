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
package org.oscim.theme_comparator.mapsforge;

import org.mapsforge.map.awt.input.MouseEventListener;
import org.mapsforge.map.view.MapView;
import org.oscim.theme_comparator.BothMapPositionHandler;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Created by Longri on 14.08.2017.
 */
public class MapPanelMouseListener extends MouseEventListener {

    private BothMapPositionHandler mapPositionHandler;
    private final MapView mapView;

    MapPanelMouseListener(MapView mapView) {
        super(mapView);
        this.mapView = mapView;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        if (this.mapPositionHandler != null) {
            this.mapPositionHandler.mapPositionChangedFromMapPanel(this.mapView.getModel().mapViewPosition.getMapPosition());
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        super.mouseWheelMoved(e);
        if (this.mapPositionHandler != null) {
            this.mapPositionHandler.mapPositionChangedFromMapPanel(this.mapView.getModel().mapViewPosition.getMapPosition());
        }
    }

    void setMapPositionHandler(BothMapPositionHandler mapPositionHandler) {
        this.mapPositionHandler = mapPositionHandler;
    }
}
