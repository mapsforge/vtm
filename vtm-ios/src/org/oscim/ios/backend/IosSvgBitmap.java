/*
 * Copyright 2016 Longri
 * Copyright 2016 devemux86
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
package org.oscim.ios.backend;

import org.oscim.backend.CanvasAdapter;
import org.oscim.utils.IOUtils;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.uikit.UIImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import svg.SVGRenderer;

public class IosSvgBitmap extends IosBitmap {
    private static final Logger log = LoggerFactory.getLogger(IosSvgBitmap.class);

    /**
     * Default size is 20x20px (400px) at 160dpi.
     */
    public static float DEFAULT_SIZE = 400f;

    private static String getStringFromInputStream(InputStream is) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(br);
        }
        return sb.toString();
    }

    public static UIImage getResourceBitmap(InputStream inputStream, float scaleFactor, float defaultSize, int width, int height, int percent) {
        String svg = getStringFromInputStream(inputStream);
        SVGRenderer renderer = new SVGRenderer(svg);
        CGRect viewRect = renderer.getViewRect();

        double scale = scaleFactor / Math.sqrt((viewRect.getHeight() * viewRect.getWidth()) / defaultSize);

        float bitmapWidth = (float) (viewRect.getWidth() * scale);
        float bitmapHeight = (float) (viewRect.getHeight() * scale);

        float aspectRatio = (float) (viewRect.getWidth() / viewRect.getHeight());

        if (width != 0 && height != 0) {
            // both width and height set, override any other setting
            bitmapWidth = width;
            bitmapHeight = height;
        } else if (width == 0 && height != 0) {
            // only width set, calculate from aspect ratio
            bitmapWidth = height * aspectRatio;
            bitmapHeight = height;
        } else if (width != 0 && height == 0) {
            // only height set, calculate from aspect ratio
            bitmapHeight = width / aspectRatio;
            bitmapWidth = width;
        }

        if (percent != 100) {
            bitmapWidth *= percent / 100f;
            bitmapHeight *= percent / 100f;
        }

        return renderer.asImageWithSize(new CGSize(bitmapWidth, bitmapHeight), 1);
    }

    private static UIImage getResourceBitmapImpl(InputStream inputStream, int width, int height, int percent) {
        return getResourceBitmap(inputStream, CanvasAdapter.dpi / CanvasAdapter.DEFAULT_DPI, DEFAULT_SIZE, width, height, percent);
    }

    public IosSvgBitmap(InputStream inputStream, int width, int height, int percent) throws IOException {
        super(getResourceBitmapImpl(inputStream, width, height, percent));
    }
}
