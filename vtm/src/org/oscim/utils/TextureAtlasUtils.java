/*
 * Copyright 2017 Longri
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

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Canvas;
import org.oscim.renderer.atlas.TextureAtlas;
import org.oscim.renderer.atlas.TextureRegion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TextureAtlasUtils {

    private final static int MAX_ATLAS_SIZE = 1024;
    private final static int PAD = 2;

    /**
     * Create a Map<Object, TextureRegion> from Map<Object, Bitmap>!
     * <br/>
     * The List<TextureAtlas> contains the generated TextureAtlas object, for disposing if no longer needed!<br/>
     * With tha param disposeBitmap, all Bitmaps will released!<br/>
     * With parameter flipY, the Atlas TextureItem will flipped over Y. (Is needed by iOS)<br/>
     *
     * @param inputMap       Map<Object, Bitmap> input Map with all Bitmaps, from which the regions are to be created
     * @param outputMap      Map<Object, TextureRegion> contains all generated TextureRegions
     * @param atlasList      List<TextureAtlas> contains all created TextureAtlases
     * @param disposeBitmaps boolean (will recycle all Bitmap's)
     * @param flipY          boolean (set True with iOS)
     */
    public static void createTextureRegions(final Map<Object, Bitmap> inputMap, Map<Object, TextureRegion> outputMap,
                                            List<TextureAtlas> atlasList, boolean disposeBitmaps, boolean flipY) {

        // step 1: sort inputMap by Bitmap size
        List<Map.Entry<Object, Bitmap>> list =
                new LinkedList<>(inputMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<Object, Bitmap>>() {
            public int compare(Map.Entry<Object, Bitmap> o1, Map.Entry<Object, Bitmap> o2) {
                int width1 = o1.getValue().getWidth();
                int width2 = o2.getValue().getWidth();
                int height1 = o1.getValue().getHeight();
                int height2 = o2.getValue().getHeight();
                return Math.max(width2, height2) - Math.max(width1, height1);
            }
        });

        Map<Object, Object> sortedByValues = new LinkedHashMap<>();
        for (Map.Entry<Object, Bitmap> entry : list) {
            sortedByValues.put(entry.getKey(), entry.getValue());
        }


        //step 2: calculate Atlas count and size
        int completePixel = PAD * PAD;
        for (Map.Entry<Object, Object> entry : sortedByValues.entrySet()) {
            completePixel += (((Bitmap) entry.getValue()).getWidth() + PAD)
                    * (((Bitmap) entry.getValue()).getHeight() + PAD);
        }
        completePixel *= 1.2; // add estimated blank pixels
        int atlasWidth = (int) Math.sqrt(completePixel);
        int atlasCount = (atlasWidth / MAX_ATLAS_SIZE) + 1;
        if (atlasCount > 1) atlasWidth = MAX_ATLAS_SIZE;


        //step 3: replace value object with object that holds the Bitmap and a Rectangle
        for (Map.Entry<Object, Object> entry : sortedByValues.entrySet()) {
            BmpRectangleObject newObject = new BmpRectangleObject();
            newObject.bitmap = (Bitmap) entry.getValue();
            newObject.rec = new TextureAtlas.Rect(0, 0,
                    newObject.bitmap.getWidth(), newObject.bitmap.getHeight());
            entry.setValue(newObject);
        }


        //step 4: calculate Regions(rectangles) and split to atlases
        List<AtlasElement> atlases = new ArrayList<>();
        atlases.add(new AtlasElement());
        int atlasIndex = 0;
        int maxLineHeight = PAD;
        for (Map.Entry<Object, Object> entry : sortedByValues.entrySet()) {
            BmpRectangleObject obj = (BmpRectangleObject) entry.getValue();
            AtlasElement atlas = atlases.get(atlasIndex);
            if ((atlas.width + obj.rec.w + PAD) > atlasWidth) {
                // not enough space, try next line
                if ((atlas.height + maxLineHeight + obj.rec.h + PAD) > MAX_ATLAS_SIZE) {
                    // not enough space, take new Atlas
                    atlas.width = atlasWidth;
                    atlas.height += PAD + maxLineHeight;
                    atlasIndex++;
                    atlas = new AtlasElement();
                    atlases.add(atlas);

                    obj.rec.x = PAD + atlas.width;
                    obj.rec.y = PAD + atlas.height;
                    atlas.width += obj.rec.w + PAD;
                    maxLineHeight = obj.rec.h + PAD;
                } else {
                    // new line
                    atlas.width = 0;
                    atlas.height += PAD + maxLineHeight;
                    obj.rec.x = PAD + atlas.width;
                    obj.rec.y = PAD + atlas.height;
                    atlas.width += obj.rec.w + PAD;
                }
            } else {
                // new row
                obj.rec.x = PAD + atlas.width;
                obj.rec.y = PAD + atlas.height;
                atlas.width += obj.rec.w + PAD;
                maxLineHeight = Math.max(maxLineHeight, obj.rec.h + PAD);
            }
            atlas.map.put(entry.getKey(), obj);
        }
        AtlasElement lastAtlas = atlases.get(atlases.size() - 1);
        lastAtlas.width = atlasWidth;
        lastAtlas.height += maxLineHeight;


        //step 5: create TextureAtlases and there TextureRegions
        for (AtlasElement atlas : atlases) {
            Bitmap atlasBitmap = CanvasAdapter.newBitmap(atlas.width, atlas.height, 0);
            Canvas canvas = CanvasAdapter.newCanvas();
            canvas.setBitmap(atlasBitmap);

            // draw regions into texture
            for (Map.Entry<Object, Object> entry : atlas.map.entrySet()) {
                BmpRectangleObject obj = (BmpRectangleObject) entry.getValue();
                if (obj.rec.x + obj.rec.w > atlasBitmap.getWidth() ||
                        obj.rec.y + obj.rec.h > atlasBitmap.getHeight()) {
                    throw new RuntimeException("atlas region outside of textureRegion");
                }
                canvas.drawBitmap(obj.bitmap, obj.rec.x, flipY ? atlas.height - obj.rec.y - obj.rec.h : obj.rec.y);
            }
            TextureAtlas textureAtlas = new TextureAtlas(atlasBitmap);
            atlasList.add(textureAtlas);

            //register regions and put there into outputMap
            for (Map.Entry<Object, Object> entry : atlas.map.entrySet()) {
                textureAtlas.addTextureRegion(entry.getKey(), ((BmpRectangleObject) entry.getValue()).rec);
                outputMap.put(entry.getKey(), textureAtlas.getTextureRegion(entry.getKey()));
            }
        }

        if (disposeBitmaps) {
            for (Bitmap bmp : inputMap.values()) {
                bmp.recycle();
            }
            inputMap.clear();
        }
    }

    private static class BmpRectangleObject {
        private Bitmap bitmap;
        private TextureAtlas.Rect rec;
    }

    private static class AtlasElement {
        int width = 0, height = 0;
        Map<Object, Object> map = new LinkedHashMap<>();
    }
}
