package org.oscim.ios.backend;

import java.io.IOException;
import java.io.InputStream;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Canvas;
import org.oscim.backend.canvas.Paint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IosGraphics extends CanvasAdapter {

	static final Logger log = LoggerFactory.getLogger(IosGraphics.class);

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
			log.error("decodeBitmapImpl",e);
			return null;
		}
	}



	@Override
	protected Bitmap loadBitmapAssetImpl(String fileName) {
		try {
			return new IosBitmap(fileName);
		} catch (IOException e) {
			log.error("loadBitmapAssetImpl",e);
			return null;
		}
	}

}
