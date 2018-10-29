/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2018 devemux86
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
package org.oscim.gdx.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

import org.oscim.backend.GL;
import org.oscim.backend.canvas.Bitmap;

public class GwtBitmap implements Bitmap {
    Pixmap pixmap;
    Image image;
    boolean disposable;

    public GwtBitmap(Image data) {
        ImageElement imageElement = ImageElement.as(data.getElement());
        pixmap = new Pixmap(imageElement);
        image = data;
    }

    /**
     * always argb8888
     */
    public GwtBitmap(int width, int height, int format) {
        pixmap = new Pixmap(width, height, null);
    }

    public GwtBitmap(String fileName) {
        FileHandle handle = Gdx.files.internal(fileName);
        pixmap = new Pixmap(handle);
        disposable = true;
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

        if (image != null)
            RootPanel.get().remove(image);
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

        Gdx.gl.glTexImage2D(GL.TEXTURE_2D, 0, pixmap.getGLInternalFormat(), pixmap.getWidth(),
                pixmap.getHeight(), 0,
                pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels());

        if (disposable || image != null) {
            //log.debug("dispose pixmap " + getWidth() + "/" + getHeight());
            pixmap.dispose();

            if (image != null)
                RootPanel.get().remove(image);
        }
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public byte[] getPngEncodedData() {
        // TODO
        return null;
    }

    @Override
    public void scaleTo(int width, int height) {
        // TODO
    }
}
