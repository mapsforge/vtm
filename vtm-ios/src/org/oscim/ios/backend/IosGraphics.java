package org.oscim.ios.backend;

import java.io.IOException;
import java.io.InputStream;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Canvas;
import org.oscim.backend.canvas.Paint;

public class IosGraphics extends CanvasAdapter {

	private static final IosGraphics INSTANCE = new IosGraphics();

	public static CanvasAdapter get() {
		return INSTANCE;
	}

	public static void init() {
		g = INSTANCE;
	}


	@Override
	protected Canvas newCanvasImpl() {
		return new IosCanvas();
	}

	@Override
	protected Paint newPaintImpl() {
		return new IosPaint();
	}

	@Override
	protected Bitmap newBitmapImpl(int width, int height, int format) {
		return new IosBitmap(width, height, format);
	}

	@Override
	protected Bitmap decodeBitmapImpl(InputStream inputStream) {
		try {
			return new IosBitmap(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}



	@Override
	protected Bitmap loadBitmapAssetImpl(String fileName) {
		return new IosBitmap(fileName);
	}

}
