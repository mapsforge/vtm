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
package org.oscim.theme_comparator.vtm;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.oscim.theme_comparator.BothMapPositionHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;

public class VtmPanel extends JPanel {

    private final MapApplicationAdapter appListener;
    private final Canvas vtmCanvas = new Canvas();

    public VtmPanel(MapApplicationAdapter.MapReadyCallback callback) {
        this.setLayout(null);
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 300;
        config.height = 300;

        appListener = new MapApplicationAdapter(callback);

        new LwjglApplication(appListener, config, vtmCanvas);

        this.add(vtmCanvas);
        this.setSize(350, 350);
        this.setBorder(BorderFactory.createTitledBorder("VTM-Map"));

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                boundsChanged();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                super.componentMoved(e);
                boundsChanged();
            }

        });

        boundsChanged();

    }

    private void boundsChanged() {
        vtmCanvas.setBounds(getX() + 10, getY() + 20, getWidth() - 20, getHeight() - 30);
        appListener.setBounds(getX() + 10, getY() + 20, getWidth() - 20, getHeight() - 30);
    }

    public void loadMap(File mapFile, File themeFile) {
        appListener.loadMap(mapFile, themeFile);
    }

    public void setCoordinate(double latidude, double longitude, byte zoomLevel) {
        appListener.setCoordinate(latidude, longitude, zoomLevel);
    }

    public void setMapPositionHandler(BothMapPositionHandler bothMapPositionHandler) {
        appListener.setMapPositionHandler(bothMapPositionHandler);
    }

    public void setTheme(String themePath) {
        appListener.setTheme(themePath);
    }
}
