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
import org.oscim.utils.math.MathUtils;

import java.util.List;
import java.util.Map;

public class TextureAtlasUtils {

    private static final int MAX_ATLAS_SIZE = 2048;
    private static final int PAD = 2;

    /**
     * Create atlas texture regions from bitmaps.
     *
     * @param inputMap       input bitmaps
     * @param outputMap      generated texture regions
     * @param atlasList      created texture atlases
     * @param disposeBitmaps recycle input bitmaps
     * @param flipY          texture items flip over y (needed on iOS)
     */
    public static void createTextureRegions(final Map<Object, Bitmap> inputMap,
                                            Map<Object, TextureRegion> outputMap,
                                            List<TextureAtlas> atlasList, boolean disposeBitmaps,
                                            boolean flipY) {
        // calculate atlas size
        int completePixel = PAD * PAD;
        int minHeight = Integer.MAX_VALUE;
        int maxHeight = Integer.MIN_VALUE;
        for (Map.Entry<Object, Bitmap> entry : inputMap.entrySet()) {
            int height = entry.getValue().getHeight();
            completePixel += (entry.getValue().getWidth() + PAD) * (height + PAD);

            minHeight = Math.min(minHeight, height);
            maxHeight = Math.max(maxHeight, height);
        }

        BitmapPacker.PackStrategy strategy = maxHeight - minHeight < 50
                ? new BitmapPacker.SkylineStrategy()
                : new BitmapPacker.GuillotineStrategy();
        completePixel *= 1.2; // add estimated blank pixels
        int atlasWidth = (int) Math.sqrt(completePixel);
        // next power of two
        atlasWidth = MathUtils.nextPowerOfTwo(MathUtils.nextPowerOfTwo(atlasWidth) + 1);
        // limit to max
        atlasWidth = Math.min(MAX_ATLAS_SIZE, atlasWidth);

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
            outputMap.putAll(atlas.getRegions());
        }

        if (disposeBitmaps) {
            for (Bitmap bmp : inputMap.values()) {
                bmp.recycle();
            }
            inputMap.clear();
        }
    }
}
