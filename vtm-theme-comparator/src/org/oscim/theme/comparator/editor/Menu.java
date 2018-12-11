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
package org.oscim.theme.comparator.editor;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.oscim.theme.comparator.MainMenu;
import org.oscim.theme.comparator.mapsforge.MapsforgeMapPanel;
import org.oscim.theme.comparator.vtm.VtmPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JToolBar;

import static org.oscim.theme.comparator.Main.ICON_OPEN;
import static org.oscim.theme.comparator.Main.ICON_SAVE;

class Menu extends JToolBar {

    private final ThemeLoader themeLoader;

    Menu(VtmPanel vtmPanel, MapsforgeMapPanel mapsforgeMapPanel, RSyntaxTextArea textArea, MainMenu mainMenu) {
        this.setRollover(false);


        JButton openButton = new JButton(ICON_OPEN);
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    themeLoader.selectThemeFile();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        this.add(openButton);
        this.addSeparator();


        JButton saveButton = new JButton(ICON_SAVE);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Save xml File and reload Theme
                try {
                    themeLoader.saveAndReload();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        this.add(saveButton);


        themeLoader = new ThemeLoader(vtmPanel, mapsforgeMapPanel, textArea, mainMenu);
    }


}
