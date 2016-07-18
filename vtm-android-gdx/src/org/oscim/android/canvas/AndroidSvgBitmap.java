/*
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
package org.oscim.android.canvas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.RectF;

import com.caverock.androidsvg.SVG;

import org.oscim.backend.CanvasAdapter;

import java.io.IOException;
import java.io.InputStream;

public class AndroidSvgBitmap extends AndroidBitmap {

    private static android.graphics.Bitmap getResourceBitmap(InputStream inputStream) throws IOException {
        synchronized (SVG.getVersion()) {
            try {
                SVG svg = SVG.getFromInputStream(inputStream);
                Picture picture = svg.renderToPicture();

                float scaleFactor = CanvasAdapter.dpi / 160;
                double scale = 1/(13 * scaleFactor);

                float bitmapWidth = (float) (picture.getWidth() * scale);
                float bitmapHeight = (float) (picture.getHeight() * scale);

                android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap((int) Math.ceil(bitmapWidth),
                        (int) Math.ceil(bitmapHeight), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawPicture(picture, new RectF(0, 0, bitmapWidth, bitmapHeight));

                return bitmap;
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }

    AndroidSvgBitmap(InputStream inputStream) throws IOException {
        super(getResourceBitmap(inputStream));
    }
}
