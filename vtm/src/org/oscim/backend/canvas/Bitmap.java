/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 Longri
 * Copyright 2018 devemux86
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
package org.oscim.backend.canvas;

/**
 * The Interface Bitmap.
 */
public interface Bitmap {

    /**
     * Gets the width.
     *
     * @return the width
     */
    int getWidth();

    /**
     * Gets the height.
     *
     * @return the height
     */
    int getHeight();

    /**
     * Recycle.
     */
    void recycle();

    /**
     * Gets the pixels as ARGB int array.
     *
     * @return the pixels
     */
    int[] getPixels();

    /**
     * Erase color, clear Bitmap.
     *
     * @param color the color
     */
    void eraseColor(int color);

    /**
     * Upload Bitmap to currently bound GL texture.
     *
     * @param replace true, when glSubImage2D can be used for upload
     */
    void uploadToTexture(boolean replace);

    boolean isValid();

    byte[] getPngEncodedData();

    void scaleTo(int width, int height);
}
