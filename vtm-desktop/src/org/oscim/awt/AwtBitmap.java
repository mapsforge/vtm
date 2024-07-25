/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2018 devemux86
 * Copyright 2016-2017 Longri
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
package org.oscim.awt;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.BufferUtils;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.GL;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.renderer.bucket.TextureBucket;
import org.oscim.utils.GraphicUtils;
import org.oscim.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

public class AwtBitmap implements Bitmap {
    private static final Logger log = LoggerFactory.getLogger(AwtBitmap.class);

    BufferedImage bitmap;

    public AwtBitmap(int width, int height, int format) {
        bitmap = new BufferedImage(width, height, format != 0 ? format : BufferedImage.TYPE_INT_ARGB);

        // if (!this.bitmap.isAlphaPremultiplied())
        // this.bitmap.coerceData(true);
    }

    AwtBitmap(InputStream inputStream) throws IOException {

        this.bitmap = ImageIO.read(inputStream);
        if (!this.bitmap.isAlphaPremultiplied()
                && this.bitmap.getType() == BufferedImage.TYPE_INT_ARGB)
            this.bitmap.coerceData(true);
    }

    AwtBitmap(InputStream inputStream, int width, int height, int percent) throws IOException {
        this(inputStream);
        float[] newSize = GraphicUtils.imageSize(getWidth(), getHeight(), CanvasAdapter.getScale(), width, height, percent);
        scaleTo((int) newSize[0], (int) newSize[1]);
    }

    public AwtBitmap(BufferedImage bitmap) {
        this.bitmap = bitmap;
        if (!this.bitmap.isAlphaPremultiplied()
                && this.bitmap.getType() == BufferedImage.TYPE_INT_ARGB)
            this.bitmap.coerceData(true);
    }

    @Override
    public int getWidth() {
        return bitmap.getWidth();
    }

    @Override
    public int getHeight() {
        return bitmap.getHeight();
    }

    @Override
    public int[] getPixels() {
        return null;
    }

    @Override
    public void eraseColor(int transparent) {
    }

    private static final IntBuffer tmpBuffer = BufferUtils
            .newIntBuffer(TextureBucket.TEXTURE_HEIGHT
                    * TextureBucket.TEXTURE_WIDTH);
    private static final int[] tmpPixel = new int[TextureBucket.TEXTURE_HEIGHT
            * TextureBucket.TEXTURE_WIDTH];

    private static final boolean WRITE_TEX = false;
    private int dbgCnt;

    @Override
    public void uploadToTexture(boolean replace) {
        int[] pixels;
        IntBuffer buffer;

        if (bitmap.getWidth() * bitmap.getHeight() < TextureBucket.TEXTURE_HEIGHT * TextureBucket.TEXTURE_WIDTH) {
            pixels = tmpPixel;
            buffer = tmpBuffer;
            buffer.clear();
        } else {
            pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
            buffer = BufferUtils.newIntBuffer(bitmap.getWidth() * bitmap.getHeight());
        }

        // FIXME dont convert to argb when there data is greyscale
        bitmap.getRGB(0, 0, bitmap.getWidth(), bitmap.getHeight(), pixels, 0, bitmap.getWidth());

        if (WRITE_TEX) {
            try {
                boolean ok = ImageIO.write(bitmap, "png", new File("texture_" + dbgCnt + ".png"));
                System.out.println("write tex " + ok + " " + dbgCnt);
                dbgCnt++;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        for (int i = 0, n = bitmap.getWidth() * bitmap.getHeight(); i < n; i++) {
            int c = pixels[i];
            if (c == 0)
                continue;

            float alpha = (c >>> 24) / 255f;
            int r = (int) ((c & 0x000000ff) * alpha);
            int b = (int) (((c & 0x00ff0000) >>> 16) * alpha);
            int g = (int) (((c & 0x0000ff00) >>> 8) * alpha);
            pixels[i] = (c & 0xff000000) | r << 16 | g << 8 | b;
        }

        buffer.put(pixels, 0, bitmap.getWidth() * bitmap.getHeight());
        buffer.flip();

        Gdx.gl20.glTexImage2D(GL.TEXTURE_2D, 0, GL.RGBA, bitmap.getWidth(),
                bitmap.getHeight(), 0, GL.RGBA, GL.UNSIGNED_BYTE, buffer);
    }

    @Override
    public void recycle() {
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public byte[] getPngEncodedData() {
        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(this.bitmap, "png", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error(e.toString(), e);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
        return null;
    }

    @Override
    public void scaleTo(int width, int height) {
        if (getWidth() != width || getHeight() != height) {
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = resizedImage.createGraphics();
            graphics.setComposite(AlphaComposite.Src);
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.drawImage(bitmap, 0, 0, width, height, null);
            graphics.dispose();
            bitmap = resizedImage;
        }
    }
}
