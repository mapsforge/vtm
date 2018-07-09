/*
 * Copyright 2018 Longri
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
package org.oscim.utils;

import org.oscim.backend.canvas.Bitmap;
import org.oscim.utils.math.MathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Longri on 17.05.2018.
 */
public class BitmapPacker {
    private final static boolean ALLOW_FLIP = false;
    private final static boolean WRITE_DEBUG = false;

    private final boolean FLIP_Y;
    private final boolean FORCE_POT;
    private final int MAX_TEXTURE_SIZE, PADDING, PADDING2X;
    private final List<objStruct> list = new ArrayList<>();
    int atlasWidth, atlasHeight;
    private PackerAtlasItem[] pages;

    private int count;

    BitmapPacker(boolean force_pot, int maxTextureSize, int padding, boolean flipY) {
        FLIP_Y = flipY;
        FORCE_POT = force_pot;
        MAX_TEXTURE_SIZE = maxTextureSize;
        PADDING = padding;
        PADDING2X = PADDING * 2;
    }

    void pack(Object ref, Bitmap bitmap) {
        list.add(new objStruct(count++, ref, bitmap));
    }

    synchronized PackerAtlasItem getAtlasItem(int index) {
        return pages[index];
    }

    int getAtlasCount() {
        return pages.length;
    }

    void generateTextureAtlas() {
        //create short array for call native
        int recCount = list.size();
        short[] valueArray = new short[recCount * 7];

        int index = 0;
        for (int i = 0; i < recCount; i++) {
            objStruct obj = list.get(i);
            valueArray[index + 0] = (short) obj.index; // index
            valueArray[index + 1] = 0; // x
            valueArray[index + 2] = 0; // y
            valueArray[index + 3] = (short) (obj.bitmap.getWidth() + PADDING2X); // width
            valueArray[index + 4] = (short) (obj.bitmap.getHeight() + PADDING2X); // height
            valueArray[index + 5] = 0; // flipped
            valueArray[index + 6] = 0; // texture index
            index += 7;
        }

        int[] pages = NativePacker.packNative(valueArray, valueArray.length / 7, MAX_TEXTURE_SIZE, ALLOW_FLIP, WRITE_DEBUG);
        int pageCount = pages[0];
        this.pages = new PackerAtlasItem[pageCount];
        int idx = 1;
        for (int i = 0; i < pageCount; i++) {
            int pageWidth = pages[idx++];
            int pageHeight = pages[idx++];
            if (FORCE_POT) {
                pageWidth = MathUtils.nextPowerOfTwo(pageWidth);
                pageHeight = MathUtils.nextPowerOfTwo(pageHeight);
            }
            this.pages[i] = new PackerAtlasItem(pageWidth, pageHeight);
        }

        //draw textures to bitmap pages
        index = 0;
        for (int i = 0; i < recCount; i++) {
            objStruct obj = list.get(i);
            obj.x = valueArray[index + 1] + PADDING; // x
            obj.y = valueArray[index + 2] + PADDING; // y
            int pageIndex = valueArray[index + 6]; // page index

            PackerAtlasItem packerAtlasItem = this.pages[pageIndex];
            Object key = obj.ref;
            PackerAtlasItem.Rect rect = new PackerAtlasItem.Rect(obj.x, obj.y, obj.bitmap.getWidth(), obj.bitmap.getHeight());
            if (key != null) {
                packerAtlasItem.rects.put(key, rect);
                packerAtlasItem.addedRects.add(key);
            }

            packerAtlasItem.drawBitmap(obj.bitmap, rect.x,
                    FLIP_Y ? packerAtlasItem.image.getHeight() - rect.y - rect.height : rect.y);

            index += 7;
        }
    }

    private static class objStruct {
        final int index;
        final Object ref;
        final Bitmap bitmap;
        int x, y;

        private objStruct(int index, Object ref, Bitmap bitmap) {
            this.index = index;
            this.ref = ref;
            this.bitmap = bitmap;
        }
    }

}
