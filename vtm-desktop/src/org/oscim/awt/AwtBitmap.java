/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 devemux86
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

import org.oscim.backend.GL;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.renderer.bucket.TextureBucket;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

public class AwtBitmap implements Bitmap {
    BufferedImage bitmap;
    int width;
    int height;

    boolean internal;

    public AwtBitmap(int width, int height, int format) {
        bitmap = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.width = width;
        this.height = height;

        internal = true;
        // if (!this.bitmap.isAlphaPremultiplied())
        // this.bitmap.coerceData(true);
    }

    AwtBitmap(InputStream inputStream) throws IOException {

        this.bitmap = ImageIO.read(inputStream);
        this.width = this.bitmap.getWidth();
        this.height = this.bitmap.getHeight();
        if (!this.bitmap.isAlphaPremultiplied()
                && this.bitmap.getType() == BufferedImage.TYPE_INT_ARGB)
            this.bitmap.coerceData(true);
    }

    public AwtBitmap(BufferedImage bitmap) {
        this.bitmap = bitmap;
        this.width = this.bitmap.getWidth();
        this.height = this.bitmap.getHeight();
        if (!this.bitmap.isAlphaPremultiplied()
                && this.bitmap.getType() == BufferedImage.TYPE_INT_ARGB)
            this.bitmap.coerceData(true);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int[] getPixels() {
        return null;
    }

    @Override
    public void eraseColor(int transparent) {
    }

    private final static IntBuffer tmpBuffer = BufferUtils
            .newIntBuffer(TextureBucket.TEXTURE_HEIGHT
                    * TextureBucket.TEXTURE_WIDTH);
    private final static int[] tmpPixel = new int[TextureBucket.TEXTURE_HEIGHT
            * TextureBucket.TEXTURE_WIDTH];

    private final static boolean WRITE_TEX = false;
    private int dbgCnt;

    @Override
    public void uploadToTexture(boolean replace) {
        int[] pixels;
        IntBuffer buffer;

        if (width * height < TextureBucket.TEXTURE_HEIGHT * TextureBucket.TEXTURE_WIDTH) {
            pixels = tmpPixel;
            buffer = tmpBuffer;
            buffer.clear();
        } else {
            pixels = new int[width * height];
            buffer = BufferUtils.newIntBuffer(width * height);
        }

        // FIXME dont convert to argb when there data is greyscale
        bitmap.getRGB(0, 0, width, height, pixels, 0, width);

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

        for (int i = 0, n = width * height; i < n; i++) {
            int c = pixels[i];
            if (c == 0)
                continue;

            float alpha = (c >>> 24) / 255f;
            int r = (int) ((c & 0x000000ff) * alpha);
            int b = (int) (((c & 0x00ff0000) >>> 16) * alpha);
            int g = (int) (((c & 0x0000ff00) >>> 8) * alpha);
            pixels[i] = (c & 0xff000000) | r << 16 | g << 8 | b;
        }

        buffer.put(pixels, 0, width * height);
        buffer.flip();

        Gdx.gl20.glTexImage2D(GL.TEXTURE_2D, 0, GL.RGBA, width,
                height, 0, GL.RGBA, GL.UNSIGNED_BYTE, buffer);
    }

    @Override
    public void recycle() {
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public byte[] getPngEncodedData(){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(this.bitmap, "png", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }
}
