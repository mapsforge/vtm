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
package org.oscim.theme_comparator;

import com.badlogic.gdx.Gdx;
import org.oscim.theme_comparator.mapsforge.MapsforgeMapPanel;
import org.oscim.theme_comparator.vtm.MapApplicationAdapter;
import org.oscim.theme_comparator.vtm.VtmPanel;

import javax.swing.*;
import java.awt.*;

class MainWindow extends JFrame {

    private final org.oscim.theme_comparator.vtm.VtmPanel vtmPanel;
    private final MapLoader mapLoader;
    private final BothMapPositionHandler bothMapPositionHandler;
    private final MapsforgeMapPanel mapsforgeMapPanel;
    private final InfoPanel infoPanel;
    private final MapsPanel mapsPanel;

    MainWindow() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        MapApplicationAdapter.MapReadyCallback callback = new MapApplicationAdapter.MapReadyCallback() {
            @Override
            public void ready() {
                mapLoader.loadPrefsMapFile();
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        bothMapPositionHandler.loadPrefsPosition();
                    }
                });
            }
        };

        vtmPanel = new VtmPanel(callback);
        mapsforgeMapPanel = new MapsforgeMapPanel();

        mapsPanel = new MapsPanel(vtmPanel, mapsforgeMapPanel);

        bothMapPositionHandler = new BothMapPositionHandler(mapsforgeMapPanel, vtmPanel);
        mapsforgeMapPanel.setMapPositionHandler(bothMapPositionHandler);
        vtmPanel.setMapPositionHandler(bothMapPositionHandler);

        mapLoader = new MapLoader(mapsforgeMapPanel, vtmPanel, bothMapPositionHandler);

        MainMenu mainMenu = new MainMenu(mapLoader, bothMapPositionHandler);
        setJMenuBar(mainMenu);

        infoPanel = new InfoPanel(vtmPanel, mapsforgeMapPanel, mainMenu);

        this.setMinimumSize(new Dimension(600, 400));
        mapsPanel.setMinimumSize(new Dimension(200, 100));
        infoPanel.setMinimumSize(new Dimension(200, 100));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(mapsPanel);
        splitPane.setBottomComponent(infoPanel);

        splitPane.setResizeWeight(0.5);

        this.add(splitPane);
    }

}
