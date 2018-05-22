/*
 * Copyright 2017-2018 Longri
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
import org.oscim.renderer.GLState;
import org.oscim.renderer.atlas.TextureAtlas;
import org.oscim.renderer.atlas.TextureRegion;

import java.util.List;
import java.util.Map;

public class TextureAtlasUtils {

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

        int maxTextureSize = GLState.getDeviceMaxGlTextureSize();
        BitmapPacker bitmapPacker = new BitmapPacker(true, maxTextureSize, PAD, flipY);

        for (Map.Entry<Object, Bitmap> entry : inputMap.entrySet()) {
            bitmapPacker.pack(entry.getKey(), entry.getValue());
        }

        bitmapPacker.generateTextureAtlas();

        for (int i = 0, n = bitmapPacker.getAtlasCount(); i < n; i++) {
            PackerAtlasItem packerAtlasItem = bitmapPacker.getAtlasItem(i);
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
