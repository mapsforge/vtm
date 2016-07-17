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
package org.oscim.android.canvas;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import com.badlogic.gdx.graphics.Pixmap;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static android.graphics.Bitmap.Config.ARGB_8888;

public class AndroidBitmap implements org.oscim.backend.canvas.Bitmap {
    final Bitmap mBitmap;

    public AndroidBitmap(InputStream inputStream) {
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        try {
            GLUtils.getType(bitmap);
        } catch (IllegalArgumentException e) {
            bitmap = bitmap.copy(ARGB_8888, false);
        }
        mBitmap = bitmap;
    }

    @Override
    public boolean isValid() {
        return mBitmap != null;
    }

    @Override
    public Pixmap getPixmap() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        this.mBitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        byte[] encodedData = outputStream.toByteArray();
        return new Pixmap(encodedData, 0, encodedData.length);
    }

    /**
     * @param format ignored always ARGB8888
     */
    public AndroidBitmap(int width, int height, int format) {
        mBitmap = android.graphics.Bitmap
                .createBitmap(width, height, ARGB_8888);
    }

    public AndroidBitmap(android.graphics.Bitmap bitmap) {
        mBitmap = bitmap;
    }

    @Override
    public int getWidth() {
        return mBitmap.getWidth();
    }

    @Override
    public int getHeight() {
        return mBitmap.getHeight();
    }

    @Override
    public int[] getPixels() {
        int width = getWidth();
        int height = getHeight();
        int[] colors = new int[width * height];
        mBitmap.getPixels(colors, 0, width, 0, 0, width, height);
        return colors;
    }

    @Override
    public void eraseColor(int color) {
        //int a = android.graphics.Color.TRANSPARENT;
        mBitmap.eraseColor(color);
    }

    @Override
    public void uploadToTexture(boolean replace) {
        int format = GLUtils.getInternalFormat(mBitmap);
        int type = GLUtils.getType(mBitmap);

        if (replace)
            GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0,
                    mBitmap, format, type);
        else
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, format,
                    mBitmap, type, 0);
    }

    @Override
    public void recycle() {
        if (mBitmap == null)
            return;

        mBitmap.recycle();
    }
}
