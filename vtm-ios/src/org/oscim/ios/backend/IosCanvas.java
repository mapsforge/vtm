package org.oscim.ios.backend;

import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Canvas;
import org.oscim.backend.canvas.Paint;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph;

public class IosCanvas implements Canvas {

	IosBitmap bitmap;
	static BitmapFont font = new BitmapFont();

	public IosCanvas() {
		// canvas comes with gdx pixmap
	}

	@Override
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = (IosBitmap) bitmap;
		this.bitmap.pixmap.setColor(0);
		this.bitmap.pixmap.fill();
	}

	@Override
	public void drawText(String string, float x, float y, Paint fill, Paint stroke) {
		if (bitmap == null) {
			// log.debug("no bitmap set");
			return;
		}

		// IosPaint p = (IosPaint) paint;

		Pixmap pixmap = bitmap.pixmap;

		TextureData td = font.getRegion().getTexture().getTextureData();
		if (!td.isPrepared())
			td.prepare();

		Pixmap f = td.consumePixmap();

		int adv = (int) x;
		Glyph last = null;

		int ch = (int) font.getCapHeight();
		int h = (int) font.getLineHeight();
		int yy = (int) (y - font.getLineHeight());
		if (y < 0)
			y = 0;

		// pixmap.setColor(0xff0000ff);
		// int w = (int) font.getBounds(string).width;
		// pixmap.drawRectangle((int) x - 4, (int) y - 4, w + 8, h + 8);

		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			Glyph g = font.getData().getGlyph(c);
			if (g == null)
				g = font.getData().getGlyph(' ');

			if (i > 0)
				adv += last.getKerning(c);
			pixmap.drawPixmap(f, adv, //- g.xoffset,
					yy - (g.height + g.yoffset) - (h - ch),
					g.srcX, g.srcY,
					g.width, g.height);
			adv += g.width;
			last = g;
		}
	}


	@Override
	public void drawBitmap(Bitmap bitmap, float x, float y) {
	}
}
