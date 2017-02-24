/*
 * Copyright 2012 Hannes Janetzek
 * Copyright 2016 devemux86
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
package org.oscim.renderer.bucket;

import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.PointF;
import org.oscim.renderer.atlas.TextureAtlas;
import org.oscim.utils.pool.Inlist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ShortBuffer;

public final class SymbolBucket extends TextureBucket {
    static final Logger log = LoggerFactory.getLogger(SymbolBucket.class);

    private final static float SCALE = 8.0f;
    private final static int VERTICES_PER_SPRITE = 4;
    private final static int LBIT_MASK = 0xfffffffe;

    private TextureItem prevTextures;
    private List<SymbolItem> mSymbols = new List<SymbolItem>();

    public SymbolBucket() {
        super(RenderBucket.SYMBOL);
        fixed = true;
    }

    /* TODO move sorting items to 'prepare' */
    public void addSymbol(SymbolItem item) {

        /* needed to calculate 'sbuf' size for compile */
        numVertices += VERTICES_PER_SPRITE;

        for (SymbolItem it : mSymbols) {
            if (it.bitmap == item.bitmap) {
                /* insert after same bitmap */
                item.next = it.next;
                it.next = item;
                return;
            }
        }
        mSymbols.push(item);
    }

    public void pushSymbol(SymbolItem item) {
        /* needed to calculate 'sbuf' size for compile */
        numVertices += VERTICES_PER_SPRITE;
        mSymbols.push(item);
    }

    @Override
    protected void compile(ShortBuffer vboData, ShortBuffer iboData) {
        /* offset of layer data in vbo */
        this.vertexOffset = vboData.position() * 2; //SHORT_BYTES;

        int numIndices = 0;

        prevTextures = textures;
        textures = null;
        TextureItem t = null;
        TextureItem lastTexture = null;

        for (SymbolItem it = mSymbols.head(); it != null; ) {
            int width = 0, height = 0;
            int x = 0;
            int y = 0;

            // FIXME Use simultaneously TextureAtlas and external symbols
            if (it.texRegion != null) {

                if (it.texRegion.texture.id == -1) {
                    //upload texture for give correct texID
                    it.texRegion.texture.upload();
                }

                if (textures == null || lastTexture == null || lastTexture.id != it.texRegion.texture.id) {
                    /* clone TextureItem to use same texID with
                     * multiple TextureItem */
                    int nextOffset = 0;

                    if (t != null) {
                        nextOffset = t.offset + t.indices;
                    }

                    t = TextureItem.clone(it.texRegion.texture);
                    t.offset = nextOffset;
                    textures = Inlist.appendItem(textures, t);
                    lastTexture = t;
                }

                TextureAtlas.Rect r = it.texRegion.rect;
                x = r.x;
                y = r.y;
                width = r.w;
                height = r.h;

            } else if (it.bitmap != null) {
                t = getTexture(it.bitmap);

                if (t == null) {
                    t = new TextureItem(it.bitmap);
                    textures = Inlist.appendItem(textures, t);
                    t.offset = numIndices;
                    t.indices = 0;
                }
                width = t.width;
                height = t.height;

            } else { //if (to == null) {
                log.debug("Bad SymbolItem");
                continue;
            }

            short u1 = (short) (SCALE * x);
            short v1 = (short) (SCALE * y);
            short u2 = (short) (SCALE * (x + width));
            short v2 = (short) (SCALE * (y + height));

            PointF prevOffset = null;
            short x1 = 0, y1 = 0, x2 = 0, y2 = 0;

            /* add symbol items referencing the same bitmap */
            for (SymbolItem prev = it; it != null; it = it.next) {

                if (prev.bitmap != null && prev.bitmap != it.bitmap)
                    break;

                if (prev.texRegion != null && prev.texRegion != it.texRegion)
                    break;

                if (it == prev || it.offset != prevOffset) {
                    prevOffset = it.offset;
                    if (it.offset == null) {
                        float hw = width / 2f;
                        float hh = height / 2f;

                        x1 = (short) (SCALE * (-hw));
                        x2 = (short) (SCALE * (hw));
                        y1 = (short) (SCALE * (hh));
                        y2 = (short) (SCALE * (-hh));
                    } else {
                        float hw = (float) (it.offset.x * width);
                        float hh = (float) (it.offset.y * height);
                        x1 = (short) (SCALE * (-hw));
                        x2 = (short) (SCALE * (width - hw));
                        y1 = (short) (SCALE * (height - hh));
                        y2 = (short) (SCALE * (-hh));
                    }
                }

                /* add vertices */
                short tx = (short) ((int) (SCALE * it.x) & LBIT_MASK
                        | (it.billboard ? 1 : 0));

                short ty = (short) (SCALE * it.y);

                vertexItems.add(tx, ty, x1, y1, u1, v2);
                vertexItems.add(tx, ty, x1, y2, u1, v1);
                vertexItems.add(tx, ty, x2, y1, u2, v2);
                vertexItems.add(tx, ty, x2, y2, u2, v1);

                /* six elements used to draw the four vertices */
                t.indices += TextureBucket.INDICES_PER_SPRITE;
            }
            numIndices += t.indices;
        }

        vertexItems.compile(vboData);

        for (t = prevTextures; t != null; t = t.dispose()) ;
        prevTextures = null;
    }

    private TextureItem getTexture(Bitmap bitmap) {
        TextureItem t;

        for (t = prevTextures; t != null; t = t.next) {
            if (t.bitmap == bitmap) {
                prevTextures = Inlist.remove(prevTextures, t);
                textures = Inlist.appendItem(textures, t);

                t.offset = 0;
                t.indices = 0;
                return t;
            }
        }
        return null;
    }

    public void clearItems() {
        SymbolItem.pool.releaseAll(mSymbols.clear());
    }

    @Override
    public void clear() {
        super.clear();
        clearItems();
    }
}
