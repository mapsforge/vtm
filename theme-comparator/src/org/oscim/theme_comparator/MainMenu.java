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

import com.badlogic.gdx.utils.StringBuilder;
import org.mapsforge.core.util.MercatorProjection;
import org.oscim.theme_comparator.theme_editor.ThemeLoader;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.prefs.BackingStoreException;

public class MainMenu extends JMenuBar {

    private final MapLoader mapLoader;
    private final JMenu fileMenu = new JMenu("File");
    private final JMenu viewMenu = new JMenu("View");
    private final JMenu posMenu = new JMenu("Pos");
    private ThemeLoader themeLoader;

     MainMenu(MapLoader mapLoader, BothMapPositionHandler mapPosition) {
        this.mapLoader = mapLoader;
        mapPosition.setCallBack(this);
        addFileEntrys();
        addViewEntrys();
        addPosEntrys();
    }

    private void addFileEntrys() {
        fileMenu.getPopupMenu().setLightWeightPopupEnabled(false);
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.add(itemLoadMap());
        fileMenu.add(itemLoadTheme());
        fileMenu.addSeparator();
        fileMenu.add(itemExit());
        this.add(fileMenu);
    }

    private void addViewEntrys() {
        JCheckBoxMenuItem checkBoxItem = new JCheckBoxMenuItem("Dark Theme");
        checkBoxItem.setState(Main.useDarkTheme);
        checkBoxItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.switchTheme();
            }
        });
        viewMenu.getPopupMenu().setLightWeightPopupEnabled(false);
        viewMenu.add(checkBoxItem);
        this.add(viewMenu);
    }

    private void addPosEntrys() {
        this.add(Box.createHorizontalGlue());
        posMenu.getPopupMenu().setLightWeightPopupEnabled(false);
        this.add(posMenu);
    }

    private JMenuItem itemExit() {
        JMenuItem item = new JMenuItem("Exit", Main.ICON_EXIT);
        item.setMnemonic(KeyEvent.VK_E);
        item.setToolTipText("Exit application");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        return item;
    }

    private JMenuItem itemLoadMap() {
        JMenuItem item = new JMenuItem("Load Map file", Main.ICON_OPEN);
        item.setMnemonic(KeyEvent.VK_M);
        item.setToolTipText("Load Mapsforge map file (*.map)");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // load Map
                String mapPath = Utils.getFile(Main.prefs.get("lastMapDir", ""), "map", "Load Map");
                if (mapPath != null) {

                    //store last selected folder
                    File file = new File(mapPath);
                    if (!file.exists() || !file.canRead()) return;
                    File dir = file.getParentFile();
                    Main.prefs.put("lastMapDir", dir.getAbsolutePath());
                    try {
                        Main.prefs.flush();
                    } catch (BackingStoreException ex) {
                        ex.printStackTrace();
                    }

                    mapLoader.loadMap(new File(mapPath), true);
                }
            }
        });
        return item;
    }

    private JMenuItem itemLoadTheme() {
        JMenuItem item = new JMenuItem("Load Theme file", Main.ICON_OPEN);
        item.setMnemonic(KeyEvent.VK_M);
        item.setToolTipText("Load Theme file (*.xml)");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    MainMenu.this.themeLoader.selectThemeFile();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        return item;
    }


     void setPos(double latitude, double longitude, byte zoomLevel) {

        //clear string builder
        stringBuilder.length = 0;
        Arrays.fill(stringBuilder.chars, Character.MIN_VALUE);

        stringBuilder.append("LAT: ");
        FormatDM(stringBuilder, latitude, "N", "S");
        stringBuilder.append("  LON: ");
        FormatDM(stringBuilder, longitude, "E", "W");
        stringBuilder.append("  ZOOM: ");
        stringBuilder.append(zoomLevel);
        stringBuilder.append("    Center Tile: X=");
        stringBuilder.append(MercatorProjection.longitudeToTileX(longitude, zoomLevel));
        stringBuilder.append("   Y= ");
        stringBuilder.append(MercatorProjection.latitudeToTileY(latitude, zoomLevel));
        stringBuilder.append("   Z= ");
        stringBuilder.append(zoomLevel);
        posMenu.setText(stringBuilder.toString());

    }

    private final StringBuilder stringBuilder = new StringBuilder(13);

    private void FormatDM(StringBuilder stringBuilder, double coord, String positiveDirection, String negativeDirection) {

        stringBuilder.append(Math.abs(((int) coord)));
        stringBuilder.append("\u00B0 ");
        stringBuilder.append(String.format("%.3f", Math.abs((coord - ((int) coord)) * 60)));
        if (coord < 0)
            stringBuilder.append(negativeDirection);
        else
            stringBuilder.append(positiveDirection);
    }

    public void setThemeLoader(ThemeLoader themeLoader) {
        this.themeLoader = themeLoader;
    }
}
