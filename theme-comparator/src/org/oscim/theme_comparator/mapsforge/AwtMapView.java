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

import org.mapsforge.map.awt.input.MapViewComponentListener;
import org.mapsforge.map.awt.view.MapView;
import org.oscim.theme_comparator.BothMapPositionHandler;

import java.awt.*;

public class AwtMapView extends MapView {

    private static final long serialVersionUID = 1L;
    private final MapPanelMouseListener mouseEventListener = new MapPanelMouseListener(this);

    AwtMapView() {
        super();
        addMyListeners();
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
        int xc = this.getWidth() / 2;
        int yc = this.getHeight() / 2;

        graphics.setColor(Color.RED);
        graphics.drawLine(xc - 50, yc - 50, xc + 50, yc + 50);
        graphics.drawLine(xc - 50, yc + 50, xc + 50, yc - 50);
        graphics.drawOval(xc - 25, yc - 25, 50, 50);
    }


    @Override
    public void addListeners() {
        //do nothing
    }

    void addMyListeners() {
        addComponentListener(new MapViewComponentListener(this));
        addMouseListener(mouseEventListener);
        addMouseMotionListener(mouseEventListener);
        addMouseWheelListener(mouseEventListener);
    }

    void setMapPositionHandler(BothMapPositionHandler mapPositionHandler) {
        mouseEventListener.setMapPositionHandler(mapPositionHandler);
    }
}
