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
package org.oscim.theme_comparator.logging;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.oscim.theme_comparator.Main;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created by Longri on 18.08.2017.
 */
abstract class BaseLoggingPane extends JPanel {

    private final RSyntaxTextArea textArea;

    BaseLoggingPane(BaseAppender appender) {
        super(new BorderLayout());
        textArea = new RSyntaxTextArea(5, 60);
        textArea.enableInputMethods(false);
        RTextScrollPane sp = new RTextScrollPane(textArea);
        sp.setLineNumbersEnabled(false);
        this.add(sp);
        setTheme(Main.useDarkTheme);
        appender.setTextArea(textArea);
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
