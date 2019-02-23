package org.oscim.utils;

import org.junit.Test;
import org.oscim.backend.canvas.Color;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ColorTest {

    // See: https://en.wikipedia.org/wiki/ANSI_escape_code
    public static final String ANSI_RESET = "\033[0m";
    public static final String ANSI_PREFIX_BACKGROUND_24Bit = "\033[48;2;";
    public static final String ANSI_PREFIX_FOREGROUND_24Bit = "\033[38;2;";

    public static final boolean DISPLAY_IN_BROWSER = false;

    @Test
    public void testColorHSV() {
        List<Integer> colors = new ArrayList<>();
        int color;

        // Hue
        color = Color.get(255, 0, 0);
        colors.add(color);
        colors.add(ColorUtil.modHsv(color, 0.33f, 1f, 1f, false));

        color = Color.get(0, 0, 255);
        colors.add(color);
        colors.add(ColorUtil.modHsv(color, 0.33f, 1f, 1f, false));

        // Saturation
        color = Color.get(255, 200, 200);
        colors.add(color);
        colors.add(ColorUtil.modHsv(color, 0f, 1.5f, 1f, false));

        color = Color.get(255, 0, 0);
        colors.add(color);
        colors.add(ColorUtil.modHsv(color, 0f, 0.5f, 1f, false));

        // Lightness (value)
        color = Color.get(255, 255, 255);
        colors.add(color);
        colors.add(ColorUtil.modHsv(color, 0f, 1f, 0.8f, false));

        color = Color.get(0, 0, 0);
        colors.add(color);
        colors.add(ColorUtil.modHsv(color, 0f, 1f, 1.5f, false));

        printColors(colors);
    }

    private void printColors(List<Integer> colors) {

        for (int color : colors) {
            // Try to display them in terminal (Intellij not supports 24 bit colors)
            System.out.println(ANSI_PREFIX_BACKGROUND_24Bit + Color.r(color) + ";" + Color.g(color) + ";" + Color.b(color) + "m " + Integer.toString(color) + ANSI_RESET);
        }

        if (DISPLAY_IN_BROWSER) {
            try {
                File tempFile;
                tempFile = File.createTempFile("test-color-", ".html");
                tempFile.deleteOnExit();
                StringBuilder builder = new StringBuilder("<html>");

                for (int color : colors) {
                    builder.append(String.format("<div style=\"background:rgb(%s,%s,%s);\">", Color.r(color), Color.g(color), Color.b(color)));
                    builder.append(Color.toString(color));
                    builder.append("</div>");
                }
                builder.append("</html>");

                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
                writer.write(builder.toString());
                writer.close();

                Desktop.getDesktop().browse(tempFile.toURI());

                Thread.sleep(2000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
