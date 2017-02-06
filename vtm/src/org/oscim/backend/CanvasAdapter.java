/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 devemux86
 * Copyright 2017 Longri
 *
 * This file is part of the OpenScienceMap project (http://www.opensciencemap.org).
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
package org.oscim.backend;

import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Canvas;
import org.oscim.backend.canvas.Paint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * The Class CanvasAdapter.
 */
public abstract class CanvasAdapter {
    private static final Logger log = LoggerFactory.getLogger(CanvasAdapter.class);

    private static final String PREFIX_ASSETS = "assets:";
    private static final String PREFIX_FILE = "file:";

    /**
     * The instance provided by backend
     */
    static CanvasAdapter g;

    /**
     * Default dpi.
     */
    public static final float DEFAULT_DPI = 160;

    /**
     * The dpi.
     */
    public static float dpi = DEFAULT_DPI;

    /**
     * The used platform.
     */
    public static Platform platform = Platform.UNKNOWN;

    /**
     * The scale.
     */
    public static float scale = 1;

    /**
     * The text scale.
     */
    public static float textScale = 1;

    /**
     * Create a Canvas.
     *
     * @return the canvas
     */
    protected abstract Canvas newCanvasImpl();

    public static Canvas newCanvas() {
        return g.newCanvasImpl();
    }

    /**
     * Create Paint.
     *
     * @return the paint
     */
    protected abstract Paint newPaintImpl();

    public static Paint newPaint() {
        return g.newPaintImpl();
    }

    /**
     * Create {@link Bitmap} with given dimensions.
     *
     * @param width  the width
     * @param height the height
     * @param format the format
     * @return the bitmap
     */
    protected abstract Bitmap newBitmapImpl(int width, int height, int format);

    public static Bitmap newBitmap(int width, int height, int format) {
        return g.newBitmapImpl(width, height, format);
    }

    /**
     * Create {@link Bitmap} from InputStream.
     *
     * @param inputStream the input stream
     * @return the bitmap
     */
    protected abstract Bitmap decodeBitmapImpl(InputStream inputStream);

    public static Bitmap decodeBitmap(InputStream inputStream) {
        return g.decodeBitmapImpl(inputStream);
    }

    /**
     * Create SVG {@link Bitmap} from InputStream.
     *
     * @param inputStream the input stream
     * @return the SVG bitmap
     */
    protected abstract Bitmap decodeSvgBitmapImpl(InputStream inputStream, int width, int height, int percent);

    public static Bitmap decodeSvgBitmap(InputStream inputStream, int width, int height, int percent) {
        return g.decodeSvgBitmapImpl(inputStream, width, height, percent);
    }

    /**
     * Create {@link Bitmap} from bundled assets.
     *
     * @param relativePathPrefix the prefix for relative resource path
     * @param src                the resource
     * @return the bitmap
     */
    protected abstract Bitmap loadBitmapAssetImpl(String relativePathPrefix, String src, int width, int height, int percent);

    public static Bitmap getBitmapAsset(String relativePathPrefix, String src) {
        return getBitmapAsset(relativePathPrefix, src, 0, 0, 100);
    }

    public static Bitmap getBitmapAsset(String relativePathPrefix, String src, int width, int height, int percent) {
        return g.loadBitmapAssetImpl(relativePathPrefix, src, width, height, percent);
    }

    protected static Bitmap createBitmap(String relativePathPrefix, String src, int width, int height, int percent) throws IOException {
        if (src == null || src.length() == 0) {
            // no image source defined
            return null;
        }

        InputStream inputStream;
        if (src.startsWith(PREFIX_ASSETS)) {
            src = src.substring(PREFIX_ASSETS.length());
            inputStream = inputStreamFromAssets(relativePathPrefix, src);
        } else if (src.startsWith(PREFIX_FILE)) {
            src = src.substring(PREFIX_FILE.length());
            inputStream = inputStreamFromFile(relativePathPrefix, src);
        } else {
            inputStream = inputStreamFromFile(relativePathPrefix, src);

            if (inputStream == null)
                inputStream = inputStreamFromAssets(relativePathPrefix, src);
        }

        if (inputStream == null) {
            log.error("invalid resource: " + src);
            return null;
        }

        Bitmap bitmap;
        if (src.toLowerCase(Locale.ENGLISH).endsWith(".svg"))
            bitmap = decodeSvgBitmap(inputStream, width, height, percent);
        else
            bitmap = decodeBitmap(inputStream);
        inputStream.close();
        return bitmap;
    }

    private static InputStream inputStreamFromAssets(String relativePathPrefix, String src) throws IOException {
        String pathName = (relativePathPrefix == null || relativePathPrefix.length() == 0 ? "" : relativePathPrefix + File.separatorChar) + src;
        return AssetAdapter.g.openFileAsStream(pathName);
    }

    private static InputStream inputStreamFromFile(String relativePathPrefix, String src) throws IOException {
        File file = getFile(relativePathPrefix, src);
        if (!file.exists()) {
            if (src.length() > 0 && src.charAt(0) == File.separatorChar) {
                file = getFile(relativePathPrefix, src.substring(1));
            }
            if (!file.exists()) {
                file = null;
            }
        } else if (!file.isFile() || !file.canRead()) {
            file = null;
        }
        if (file != null) {
            return new FileInputStream(file);
        }
        return null;
    }

    private static File getFile(String parentPath, String pathName) {
        if (pathName.charAt(0) == File.separatorChar) {
            return new File(pathName);
        }
        return new File(parentPath, pathName);
    }

    protected static void init(CanvasAdapter adapter) {
        g = adapter;
    }
}
