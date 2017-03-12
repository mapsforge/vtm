/*
 * Copyright 2016-2017 Longri
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
package org.oscim.ios.backend;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;

import org.oscim.backend.AssetAdapter;
import org.oscim.backend.GL;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Color;
import org.robovm.apple.coregraphics.CGBitmapContext;
import org.robovm.apple.coregraphics.CGBlendMode;
import org.robovm.apple.coregraphics.CGColor;
import org.robovm.apple.coregraphics.CGColorSpace;
import org.robovm.apple.coregraphics.CGImage;
import org.robovm.apple.coregraphics.CGImageAlphaInfo;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;

/**
 * iOS specific implementation of {@link Bitmap}.
 */
public class IosBitmap implements Bitmap {

    static final Logger log = LoggerFactory.getLogger(IosBitmap.class);

    CGBitmapContext cgBitmapContext;
    final int width;
    final int height;
    private int glInternalFormat = Integer.MIN_VALUE;
    private int glFormat = Integer.MIN_VALUE;
    private int glType = Integer.MIN_VALUE;
    private Buffer directPixelBuffer;

    /**
     * Constructor<br>
     * Create a IosBitmap with given width and height. <br>
     * The format is ignored and set always to ARGB8888
     *
     * @param width
     * @param height
     * @param format ignored always ARGB8888
     */
    public IosBitmap(int width, int height, int format) {
        this.width = width;
        this.height = height;
        this.cgBitmapContext = CGBitmapContext.create(width, height, 8, 4 * width,
                CGColorSpace.createDeviceRGB(), CGImageAlphaInfo.PremultipliedLast);
    }

    /**
     * Constructor<br>
     * Create a IosBitmap from given InputStream
     *
     * @param inputStream
     * @throws IOException
     */
    public IosBitmap(InputStream inputStream) throws IOException {
        NSData data = new NSData(toByteArray(inputStream));
        CGImage image = new UIImage(data).getCGImage();
        this.width = (int) image.getWidth();
        this.height = (int) image.getHeight();
        this.cgBitmapContext = CGBitmapContext.create(width, height, 8, 4 * width,
                CGColorSpace.createDeviceRGB(), CGImageAlphaInfo.PremultipliedLast);

        this.cgBitmapContext.drawImage(new CGRect(0, 0, width, height), image);
        image.dispose();
    }

    /**
     * Constructor<br>
     * Load a IosBitmap from Asset with given FilePath
     *
     * @param fileName
     * @throws IOException
     */
    public IosBitmap(String fileName) throws IOException {
        if (fileName == null || fileName.length() == 0) {
            // no image source defined
            this.cgBitmapContext = null;
            this.width = 0;
            this.height = 0;
            return;
        }

        InputStream inputStream = AssetAdapter.readFileAsStream(fileName);
        if (inputStream == null) {
            log.error("invalid bitmap source: " + fileName);
            // no image source defined
            this.cgBitmapContext = null;
            this.width = 0;
            this.height = 0;
            return;
        }

        NSData data = new NSData(toByteArray(inputStream));
        CGImage image = new UIImage(data).getCGImage();
        this.width = (int) image.getWidth();
        this.height = (int) image.getHeight();
        this.cgBitmapContext = CGBitmapContext.create(width, height, 8, 4 * width,
                CGColorSpace.createDeviceRGB(), CGImageAlphaInfo.PremultipliedLast);

        this.cgBitmapContext.drawImage(new CGRect(0, 0, width, height), image);
        image.dispose();
    }

    /**
     * Protected constructor for create IosBitmap from IosSvgBitmap.
     *
     * @param image
     */
    protected IosBitmap(UIImage image) {
        CGImage cgiIimage = image.getCGImage();
        this.width = (int) cgiIimage.getWidth();
        this.height = (int) cgiIimage.getHeight();
        this.cgBitmapContext = CGBitmapContext.create(width, height, 8, 4 * width,
                CGColorSpace.createDeviceRGB(), CGImageAlphaInfo.PremultipliedLast);

        this.cgBitmapContext.drawImage(new CGRect(0, 0, width, height), cgiIimage);

        // can dispose helper images for release memory
        image.dispose();
        cgiIimage.dispose();
    }


    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void recycle() {
        //CGContext.close() will close the object and release any system resources it holds.
        if (this.cgBitmapContext != null) {
            this.cgBitmapContext.close();
            this.cgBitmapContext = null;
        }
        if (this.directPixelBuffer != null) {
            this.directPixelBuffer.clear();
            this.directPixelBuffer = null;
        }
    }

    @Override
    public int[] getPixels() {
        //Todo fill a int[] with pixmap.getPixel(x,y)
        return new int[0];
    }

    @Override
    public void eraseColor(int color) {

        //delete all pixels and fill with given color

        CGRect rect = new CGRect(0, 0, this.width, this.height);
        this.cgBitmapContext.setFillColor(getCGColor(color));
        this.cgBitmapContext.setBlendMode(CGBlendMode.Clear);
        this.cgBitmapContext.fillRect(rect);
        this.cgBitmapContext.setBlendMode(CGBlendMode.Normal);
        this.cgBitmapContext.fillRect(rect);
    }

    @Override
    public void uploadToTexture(boolean replace) {

        //create a pixel buffer for upload from direct memory pointer
        if (directPixelBuffer == null) {

            //create Pixmap from cgBitmapContext for extract glFormat info's
            UIImage uiImage = new UIImage(cgBitmapContext.toImage());
            NSData data = uiImage.toPNGData();
            byte[] encodedData = data.getBytes();
            Pixmap pixmap = new Pixmap(encodedData, 0, encodedData.length);

            glInternalFormat = pixmap.getGLInternalFormat();
            glFormat = pixmap.getGLFormat();
            glType = pixmap.getGLType();

            directPixelBuffer = cgBitmapContext.getData().asIntBuffer(encodedData.length / 4);
            pixmap.dispose();

        }

        Gdx.gl.glTexImage2D(GL.TEXTURE_2D, 0, glInternalFormat, this.width, this.height, 0
                , glFormat, glType, directPixelBuffer);
    }

    @Override
    public boolean isValid() {
        return this.cgBitmapContext != null;
    }

    /**
     * Returns a ByteArray from InputStream
     *
     * @param in InputStream
     * @return
     * @throws IOException
     */
    static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buff = new byte[8192];
        while (in.read(buff) > 0) {
            out.write(buff);
        }
        out.close();
        return out.toByteArray();
    }

    /**
     * Returns the CGColor from given int
     *
     * @param color
     * @return
     */
    static CGColor getCGColor(int color) {
        return UIColor.fromRGBA(
                Color.a(color),
                Color.g(color),
                Color.b(color),
                Color.r(color))
                .getCGColor();
    }

    @Override
    public byte[] getPngEncodedData() {
        UIImage uiImage = new UIImage(cgBitmapContext.toImage());
        NSData data = uiImage.toPNGData();
        return data.getBytes();
    }
}
