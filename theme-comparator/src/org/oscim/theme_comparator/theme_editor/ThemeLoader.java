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
package org.oscim.theme_comparator.theme_editor;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.oscim.theme_comparator.Main;
import org.oscim.theme_comparator.MainMenu;
import org.oscim.theme_comparator.Utils;
import org.oscim.theme_comparator.mapsforge.MapsforgeMapPanel;
import org.oscim.theme_comparator.vtm.VtmPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.prefs.BackingStoreException;

/**
 * Created by Longri on 17.08.2017.
 */
public class ThemeLoader {

    final private Logger vtmLog = LoggerFactory.getLogger("org.oscim.ThemeLoader");
    final private Logger mapsforgeLog = LoggerFactory.getLogger("org.mapsforge.ThemeLoader");


    final private VtmPanel vtmPanel;
    final private MapsforgeMapPanel mapsforgeMapPanel;
    final private RSyntaxTextArea syntaxTextArea;

    private String themePath;
    private String editorText;

    ThemeLoader(VtmPanel vtmPanel, MapsforgeMapPanel mapsforgeMapPanel, RSyntaxTextArea editorPane, MainMenu mainMenu) {
        this.vtmPanel = vtmPanel;
        this.mapsforgeMapPanel = mapsforgeMapPanel;
        this.syntaxTextArea = editorPane;
        mainMenu.setThemeLoader(this);
    }

    public void selectThemeFile() throws IOException {
        String themePath = Utils.getFile(Main.prefs.get("lastTheme", ""), "*.xml", "Load Theme File");

        if (themePath == null) return;

        //store last selected folder
        File file = new File(themePath);
        if (!file.exists() || !file.canRead()) return;
        File dir = file.getParentFile();
        Main.prefs.put("lastTheme", dir.getAbsolutePath());
        try {
            Main.prefs.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }

        setTheme(themePath);
    }

    private void setTheme(String themePath) throws IOException {
        if (themePath != null) {
            this.themePath = themePath;
            BufferedReader br = new BufferedReader(new FileReader(this.themePath));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                editorText = sb.toString();

            } finally {
                br.close();
            }
            syntaxTextArea.setText(editorText);

            syntaxTextArea.setCaretPosition(0);


            try {
                this.mapsforgeMapPanel.setTheme(this.themePath);
            } catch (Exception e) {
                mapsforgeLog.error("LoadTheme", e);
            }

            try {
                this.vtmPanel.setTheme(this.themePath);
            } catch (Exception e) {
                vtmLog.error("LoadTheme", e);
            }
        }
    }

    void saveAndReload() throws IOException {
        int lastCaretPosition = syntaxTextArea.getCaretPosition();

        editorText = syntaxTextArea.getText();

        Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(this.themePath), "UTF-8"));
        try {
            out.write(editorText);
        } finally {
            out.close();
        }

        setTheme(themePath);
        syntaxTextArea.setCaretPosition(lastCaretPosition);

    }
}
