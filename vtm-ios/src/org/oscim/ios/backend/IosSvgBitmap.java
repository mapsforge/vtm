/*
 * Copyright 2016 Longri
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
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.uikit.UIImage;
import svg.SVGRenderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Longri on 17.07.16.
 */
public class IosSvgBitmap extends IosBitmap {
    private static final float DEFAULT_SIZE = 400f;

    /**
     * Constructor<br>
     * @param inputStream
     * @throws IOException
     */
    public IosSvgBitmap(InputStream inputStream) throws IOException {
        super(getUIImage(inputStream));
    }

    //get UIImage from SVG file
    private static UIImage getUIImage(InputStream inputStream) {
        String svg = getStringFromInputStream(inputStream);
        SVGRenderer renderer = new SVGRenderer(svg);
        CGRect viewRect = renderer.getViewRect();

        float scaleFactor = CanvasAdapter.dpi / 240;
        double scale = scaleFactor / Math.sqrt((viewRect.getHeight() * viewRect.getWidth()) / DEFAULT_SIZE);

        float bitmapWidth = (float) (viewRect.getWidth() * scale);
        float bitmapHeight = (float) (viewRect.getHeight() * scale);

        return renderer.asImageWithSize(new CGSize(bitmapWidth, bitmapHeight), 1);
    }


    // convert InputStream to String
    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
