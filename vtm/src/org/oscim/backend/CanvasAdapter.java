/*
 * Copyright 2013 Hannes Janetzek
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

import java.io.IOException;
import java.io.InputStream;

import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Canvas;
import org.oscim.backend.canvas.Paint;

/**
 * The Class CanvasAdapter.
 */
public abstract class CanvasAdapter {

	/** The instance provided by backend */
	public static CanvasAdapter g;

	/** The dpi. */
	public static float dpi = 240;

	/** The text scale. */
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
	 * @param width the width
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
	 * Create {@link Bitmap} from bundled assets.
	 * 
	 * @param fileName the file name
	 * @return the bitmap
	 */
	protected abstract Bitmap loadBitmapAssetImpl(String fileName);

	public static Bitmap getBitmapAsset(String fileName) {
		return g.loadBitmapAssetImpl(fileName);
	}

	protected static Bitmap createBitmap(String src) throws IOException {
		if (src == null || src.length() == 0) {
			// no image source defined
			return null;
		}

		InputStream inputStream = AssetAdapter.g.openFileAsStream(src);
		if (inputStream == null) {
			//log.error("invalid bitmap source: " + src);
			return null;
		}

		Bitmap bitmap = decodeBitmap(inputStream);
		inputStream.close();
		return bitmap;
	}

	protected static void init(CanvasAdapter adapter) {
		g = adapter;
	}
}
