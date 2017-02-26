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

import org.oscim.backend.canvas.Bitmap;
import org.oscim.renderer.atlas.TextureAtlas;
import org.oscim.renderer.atlas.TextureRegion;

import java.util.List;
import java.util.Map;

public class TextureAtlasUtils {

    private final static int MAX_ATLAS_SIZE = 2048;
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


        //step 2: calculate Atlas count and size
        int completePixel = PAD * PAD;
        int minHeight = Integer.MAX_VALUE;
        int maxHeight = Integer.MIN_VALUE;
        for (Map.Entry<Object, Bitmap> entry : inputMap.entrySet()) {
            int height = entry.getValue().getHeight();
            completePixel += (entry.getValue().getWidth() + PAD)
                    * (height + PAD);

            minHeight = Math.min(minHeight, height);
            maxHeight = Math.max(maxHeight, height);
        }

        BitmapPacker.PackStrategy strategy = maxHeight - minHeight < 50 ? new BitmapPacker.SkylineStrategy() :
                new BitmapPacker.GuillotineStrategy();
        completePixel *= 1.2; // add estimated blank pixels
        int atlasWidth = Math.min(MAX_ATLAS_SIZE, (int) Math.sqrt(completePixel));

        BitmapPacker bitmapPacker = new BitmapPacker(atlasWidth, atlasWidth, PAD, strategy, flipY);

        for (Map.Entry<Object, Bitmap> entry : inputMap.entrySet()) {
            completePixel += (entry.getValue().getWidth() + PAD)
                    * (entry.getValue().getHeight() + PAD);
            bitmapPacker.add(entry.getKey(), entry.getValue());
        }

        for (int i = 0, n = bitmapPacker.getAtlasCount(); i < n; i++) {
            BitmapPacker.PackerAtlasItem packerAtlasItem = bitmapPacker.getAtlasItem(i);
            TextureAtlas atlas = packerAtlasItem.getAtlas();
            atlasList.add(atlas);
            outputMap.putAll(atlas.getAllRegions());
        }

        if (disposeBitmaps) {
            for (Bitmap bmp : inputMap.values()) {
                bmp.recycle();
            }
            inputMap.clear();
        }
    }

}
