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
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.oscim.theme_comparator.Main;
import org.oscim.theme_comparator.MainMenu;
import org.oscim.theme_comparator.mapsforge.MapsforgeMapPanel;
import org.oscim.theme_comparator.vtm.VtmPanel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created by Longri on 17.08.2017.
 */
public class EditorPane extends JPanel {

    private final RSyntaxTextArea textArea;


    /**
     * It's used the RSyntaxTextArea. Show https://github.com/bobbylight/RSyntaxTextArea/wiki
     */
    public EditorPane(VtmPanel vtmPanel, MapsforgeMapPanel mapsforgeMapPanel, MainMenu mainMenu) {
        super(new BorderLayout());
        textArea = new RSyntaxTextArea(5, 60);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
        textArea.setCodeFoldingEnabled(true);
        RTextScrollPane sp = new RTextScrollPane(textArea);
        this.add(sp);
        setTheme(Main.useDarkTheme);
        add(new Menu(vtmPanel, mapsforgeMapPanel, textArea, mainMenu), BorderLayout.NORTH);
    }


    private void setTheme(boolean dark) {
        try {
            Theme theme;
            if (dark) {
                theme = Theme.load(getClass().getResourceAsStream(
                        "/org/fife/ui/rsyntaxtextarea/themes/dark.xml"));
            } else {
                theme = Theme.load(getClass().getResourceAsStream(
                        "/org/fife/ui/rsyntaxtextarea/themes/default.xml"));
            }
            theme.apply(textArea);
        } catch (IOException ioe) { // Never happens
            ioe.printStackTrace();
        }
    }
}
