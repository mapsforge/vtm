package org.oscim.ios.backend;

import java.io.IOException;
import java.io.InputStream;

import org.oscim.backend.GL;
import org.oscim.backend.canvas.Bitmap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;

public class IosBitmap implements Bitmap {

	Pixmap pixmap;
	boolean disposable;

	/** always argb8888 */
	public IosBitmap(int width, int height, int format) {
		pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
	}

	public IosBitmap(String fileName) {
		FileHandle handle = Gdx.files.internal(fileName);
		pixmap = new Pixmap(handle);
		disposable = true;
	}

	public IosBitmap(InputStream inputStream) throws IOException {
		pixmap = new Pixmap(new Gdx2DPixmap(inputStream, Gdx2DPixmap.GDX2D_FORMAT_RGBA8888));
	}

	@Override
	public int getWidth() {
		return pixmap.getWidth();
	}

	@Override
	public int getHeight() {
		return pixmap.getHeight();
	}

	@Override
	public void recycle() {
		// FIXME this should be called at some point in time
		pixmap.dispose();

	}

	@Override
	public int[] getPixels() {
		return null;
	}

	@Override
	public void eraseColor(int color) {
	}

	@Override
	public void uploadToTexture(boolean replace) {

		Gdx.gl.glTexImage2D(GL.TEXTURE_2D, 0, pixmap.getGLInternalFormat(),
		                    pixmap.getWidth(), pixmap.getHeight(), 0,
		                    pixmap.getGLFormat(), pixmap.getGLType(),
		                    pixmap.getPixels());

		if (disposable) {
			pixmap.dispose();
		}
	}

	@Override
	public boolean isValid() {
		return true;
	}
}
