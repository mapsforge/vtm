/*
 * Copyright (C) 2018 team-cachebox.de
 *
 * Licensed under the : GNU General Public License (GPL);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
