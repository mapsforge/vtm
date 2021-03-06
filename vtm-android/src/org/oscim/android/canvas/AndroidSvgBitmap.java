/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2013-2014 Ludwig M Brinckmann
 * Copyright 2014-2018 devemux86
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
package org.oscim.android.canvas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.RectF;

import com.caverock.androidsvg.SVG;

import org.oscim.backend.CanvasAdapter;
import org.oscim.utils.GraphicUtils;

import java.io.IOException;
import java.io.InputStream;

public class AndroidSvgBitmap extends AndroidBitmap {
    /**
     * Default size is 20x20px (400px) at baseline mdpi (160dpi).
     */
    public static float DEFAULT_SIZE = 400f;

    public static android.graphics.Bitmap getResourceBitmap(InputStream inputStream, float scaleFactor, float defaultSize, int width, int height, int percent) throws IOException {
        try {
            SVG svg = SVG.getFromInputStream(inputStream);
            Picture picture = svg.renderToPicture();

            double scale = scaleFactor / Math.sqrt((picture.getHeight() * picture.getWidth()) / defaultSize);

            float[] bmpSize = GraphicUtils.imageSize(picture.getWidth(), picture.getHeight(), (float) scale, width, height, percent);

            android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap((int) Math.ceil(bmpSize[0]),
                    (int) Math.ceil(bmpSize[1]), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawPicture(picture, new RectF(0, 0, bmpSize[0], bmpSize[1]));

            return bitmap;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private static android.graphics.Bitmap getResourceBitmapImpl(InputStream inputStream, int width, int height, int percent) throws IOException {
        synchronized (SVG.getVersion()) {
            return getResourceBitmap(inputStream, CanvasAdapter.getScale(), DEFAULT_SIZE, width, height, percent);
        }
    }

    public AndroidSvgBitmap(InputStream inputStream, int width, int height, int percent) throws IOException {
        super(getResourceBitmapImpl(inputStream, width, height, percent));
    }
}
