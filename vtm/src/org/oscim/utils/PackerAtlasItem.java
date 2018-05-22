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

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Canvas;
import org.oscim.backend.canvas.Color;
import org.oscim.renderer.atlas.TextureAtlas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Longri on 17.05.2018.
 */
public class PackerAtlasItem {
    HashMap<Object, PackerAtlasItem.Rect> rects = new HashMap<>();
    final Bitmap image;
    final Canvas canvas;
    final ArrayList<Object> addedRects = new ArrayList<>();

    PackerAtlasItem(BitmapPacker packer) {
        // On Desktop we use BufferedImage.TYPE_INT_ARGB_PRE (3) format
        int format = CanvasAdapter.platform.isDesktop() ? 3 : 0;
        image = CanvasAdapter.newBitmap(packer.atlasWidth, packer.atlasHeight, format);
        canvas = CanvasAdapter.newCanvas();
        canvas.setBitmap(this.image);
        canvas.fillColor(Color.TRANSPARENT);
    }

    PackerAtlasItem(int atlasWidth,int atlasHeight) {
        // On Desktop we use BufferedImage.TYPE_INT_ARGB_PRE (3) format
        int format = CanvasAdapter.platform.isDesktop() ? 3 : 0;
        image = CanvasAdapter.newBitmap(atlasWidth, atlasHeight, format);
        canvas = CanvasAdapter.newCanvas();
        canvas.setBitmap(this.image);
        canvas.fillColor(Color.TRANSPARENT);
    }

    public TextureAtlas getAtlas() {
        TextureAtlas atlas = new TextureAtlas(image);
        //add regions
        for (Map.Entry<Object, PackerAtlasItem.Rect> entry : rects.entrySet()) {
            atlas.addTextureRegion(entry.getKey(), entry.getValue().getAtlasRect());
        }
        return atlas;
    }

    void drawBitmap(Bitmap image, int x, int y) {
        canvas.drawBitmap(image, x, y);
    }

    static class Rect {
        int x, y, width, height;

        Rect() {
        }

        Rect(int x, int y, int width, int height) {
            this.set(x, y, width, height);
        }

        void set(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        TextureAtlas.Rect getAtlasRect() {
            return new TextureAtlas.Rect(x, y, width, height);
        }
    }
}
