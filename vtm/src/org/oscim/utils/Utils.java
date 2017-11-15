/*
 * Copyright 2016-2017 devemux86
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
package org.oscim.utils;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Canvas;
import org.oscim.renderer.bucket.TextureItem;
import org.oscim.utils.math.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Utils {

    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    /**
     * Null safe equals.
     */
    public static boolean equals(Object o1, Object o2) {
        return (o1 == o2) || (o1 != null && o1.equals(o2));
    }

    /**
     * Load a texture from a specified location and optional dimensions.
     */
    public static TextureItem loadTexture(String relativePathPrefix, String src, int width, int height, int percent) {
        if (src == null || src.length() == 0)
            return null;

        try {
            Bitmap bitmap = CanvasAdapter.getBitmapAsset(relativePathPrefix, src, width, height, percent);
            if (bitmap != null) {
                log.debug("loading {}", src);
                return new TextureItem(potBitmap(bitmap), true);
            }
        } catch (Exception e) {
            log.error("{}: missing file / {}", src, e.getMessage());
        }
        return null;
    }

    /**
     * Returns a Bitmap with POT size, if {@link Parameters#POT_TEXTURES} is true.
     * Else the returned Bitmap is the same instance of given Bitmap.
     * If the given Bitmap has POT size, the given instance is returned.
     */
    public static Bitmap potBitmap(Bitmap bitmap) {
        if (Parameters.POT_TEXTURES) {
            int potWidth = MathUtils.nextPowerOfTwo(bitmap.getWidth());
            int potHeight = MathUtils.nextPowerOfTwo(bitmap.getHeight());
            if (potWidth != bitmap.getWidth() || potHeight != bitmap.getHeight()) {
                log.debug("POT texture: {}x{} -> {}x{}", bitmap.getWidth(), bitmap.getHeight(), potWidth, potHeight);
                Bitmap potBitmap = CanvasAdapter.newBitmap(potWidth, potHeight, 0);
                Canvas potCanvas = CanvasAdapter.newCanvas();
                potCanvas.setBitmap(potBitmap);
                potCanvas.drawBitmapScaled(bitmap);
                bitmap = potBitmap;
            }
        }
        return bitmap;
    }

    private Utils() {
        throw new IllegalStateException();
    }
}
