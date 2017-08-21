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

import ch.qos.logback.classic.LoggerContext;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.jtattoo.plaf.DecorationHelper;
import com.jtattoo.plaf.hifi.HiFiLookAndFeel;
import org.oscim.awt.AwtGraphics;
import org.oscim.backend.GLAdapter;
import org.oscim.gdx.GdxAssets;
import org.oscim.gdx.LwjglGL20;
import org.oscim.theme_comparator.logging.AllAppender;
import org.oscim.theme_comparator.logging.BaseAppender;
import org.oscim.theme_comparator.logging.MapsforgeAppender;
import org.oscim.theme_comparator.logging.VtmAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Main {

    public static Preferences prefs = Preferences.userNodeForPackage(Main.class);
    public static boolean useDarkTheme = true; // set black look and feel as default for unimpaired color impression
    public final static BaseAppender ALL_APPENDER = new AllAppender();
    public final static BaseAppender VTM_APPENDER = new VtmAppender();
    public final static BaseAppender MAPSFORGE_APPENDER = new MapsforgeAppender();

    public final static int MAX_ZOOM_LEVEL = 21;
    public final static int MIN_ZOOM_LEVEL = 0;


    public final static ImageIcon ICON_OPEN = new ImageIcon(Main.class.getClassLoader().getResource("menu-open.png"));
    public final static ImageIcon ICON_SAVE = new ImageIcon(Main.class.getClassLoader().getResource("menu-saveall.png"));
    public static final ImageIcon ICON_EDIT = new ImageIcon(Main.class.getClassLoader().getResource("editSource_dark.png"));
    public static final ImageIcon ICON_DEBUG = new ImageIcon(Main.class.getClassLoader().getResource("debug_dark.png"));
    public static final ImageIcon ICON_EXIT = new ImageIcon(Main.class.getClassLoader().getResource("exit_dark.png"));
    public static final ImageIcon ICON_LOCATE = new ImageIcon(Main.class.getClassLoader().getResource("locate_dark.png"));

    private static JFrame window;

    // add TextArea logging appender
    static {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger rootLogger = lc.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(ALL_APPENDER);
        ALL_APPENDER.start();
        rootLogger.addAppender(VTM_APPENDER);
        VTM_APPENDER.start();
        rootLogger.addAppender(MAPSFORGE_APPENDER);
        MAPSFORGE_APPENDER.start();
    }


    public static void main(String[] args) throws Exception {

        useDarkTheme = prefs.getBoolean("DARK", true);

        setTheme();

        System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");

        new SharedLibraryLoader().load("vtm-jni");
        AwtGraphics.init();
        GdxAssets.init("");
        GLAdapter.init(new LwjglGL20());
        GLAdapter.GDX_DESKTOP_QUIRKS = true;
        window = new MainWindow();
        window.setSize(800, 600);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    private static void setTheme() {
        if (useDarkTheme) {
            try {
                UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
                Properties properties = HiFiLookAndFeel.getThemeProperties("Default");
                properties.setProperty("textAntiAliasing", "on");
                properties.setProperty("backgroundPattern", "off");
                properties.put("logoString", "");
                HiFiLookAndFeel.setTheme(properties);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    static void switchTheme() {

        useDarkTheme = !useDarkTheme;

        prefs.putBoolean("DARK", useDarkTheme);
        try {
            prefs.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }

        setTheme();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SwingUtilities.updateComponentTreeUI(window);
                if (!useDarkTheme) {
                    DecorationHelper.decorateWindows(false);
                    window.dispose();
                    window.setUndecorated(false);
                    window.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
                    window.setVisible(true);
                } else {
                    DecorationHelper.decorateWindows(true);
                    window.dispose();
                    window.setUndecorated(true);
                    window.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
                    window.setVisible(true);
                }

            }
        });

    }
}
