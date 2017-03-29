/*
 * Copyright 2012 Hannes Janetzek
 * Copyright 2016-2017 devemux86
 * Copyright 2017 Longri
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
import org.oscim.renderer.GLMatrix;
import org.oscim.renderer.atlas.TextureAtlas;
import org.oscim.utils.pool.Inlist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ShortBuffer;

import static org.oscim.renderer.MapRenderer.COORD_SCALE;

public final class SymbolBucket extends TextureBucket {
    static final Logger log = LoggerFactory.getLogger(SymbolBucket.class);

    private final static int VERTICES_PER_SPRITE = 4;
    private final static int LBIT_MASK = 0xfffffffe;

    private TextureItem prevTextures;
    private List<SymbolItem> mSymbols = new List<SymbolItem>();

    private final float[] points = new float[8];
    private final GLMatrix rotationMatrix = new GLMatrix();
    private final GLMatrix translateMatrix = new GLMatrix();

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

            short u1 = (short) (COORD_SCALE * x);
            short v1 = (short) (COORD_SCALE * y);
            short u2 = (short) (COORD_SCALE * (x + width));
            short v2 = (short) (COORD_SCALE * (y + height));

            PointF prevOffset = null;
            short x1 = 0, y1 = 0, x2 = 0, y2 = 0;
            float minX, minY, maxX, maxY;

            /* add symbol items referencing the same bitmap */
            for (SymbolItem prev = it; it != null; it = it.next) {
                if (it.rotation == 0) { // without rotation
                    if (prev.bitmap != null && prev.bitmap != it.bitmap)
                        break;

                    if (prev.texRegion != null && prev.texRegion != it.texRegion)
                        break;

                    if (it == prev || it.offset != prevOffset) {
                        prevOffset = it.offset;
                        if (it.offset == null) {
                            float hw = width / 2f;
                            float hh = height / 2f;

                            x1 = (short) (COORD_SCALE * (-hw));
                            x2 = (short) (COORD_SCALE * (hw));
                            y1 = (short) (COORD_SCALE * (hh));
                            y2 = (short) (COORD_SCALE * (-hh));
                        } else {
                            float hw = (float) (it.offset.x * width);
                            float hh = (float) (it.offset.y * height);
                            x1 = (short) (COORD_SCALE * (-hw));
                            x2 = (short) (COORD_SCALE * (width - hw));
                            y1 = (short) (COORD_SCALE * (height - hh));
                            y2 = (short) (COORD_SCALE * (-hh));
                        }
                    }

                    /* add vertices */
                    short tx = (short) ((int) (COORD_SCALE * it.x) & LBIT_MASK
                            | (it.billboard ? 1 : 0));

                    short ty = (short) (COORD_SCALE * it.y);

                    vertexItems.add(tx, ty, x1, y1, u1, v2);
                    vertexItems.add(tx, ty, x1, y2, u1, v1);
                    vertexItems.add(tx, ty, x2, y1, u2, v2);
                    vertexItems.add(tx, ty, x2, y2, u2, v1);
                } else { // with rotation
                    if (prev.bitmap != null && prev.bitmap != it.bitmap && prev.rotation != it.rotation)
                        break;

                    if (prev.texRegion != null && prev.texRegion != it.texRegion && prev.rotation != it.rotation)
                        break;

                    short offsetX, offsetY;
                    if (it.offset == null) {
                        offsetX = 0;
                        offsetY = 0;
                    } else {
                        offsetX = (short) (((width / 2f) - (it.offset.x * width)) * COORD_SCALE);
                        offsetY = (short) (((height / 2f) - (it.offset.y * height)) * COORD_SCALE);
                    }

                    float hw = width / 2f;
                    float hh = height / 2f;

                    minX = (COORD_SCALE * (-hw));
                    maxX = (COORD_SCALE * (hw));
                    minY = (COORD_SCALE * (hh));
                    maxY = (COORD_SCALE * (-hh));

                    // target drawing rectangle
                    { // lower-left
                        points[0] = minX;
                        points[1] = minY;
                    }

                    { // upper-left
                        points[2] = minX;
                        points[3] = maxY;
                    }

                    { // upper-right
                        points[6] = maxX;
                        points[7] = maxY;
                    }

                    { // lower-right
                        points[4] = maxX;
                        points[5] = minY;
                    }

                    if (it.rotation != 0) {
                        rotationMatrix.setRotation(it.rotation, 0, 0, 1);
                        rotationMatrix.prj2D(points, 0, 4);
                    }

                    /* add vertices */
                    short tx = (short) (((int) (COORD_SCALE * it.x) & LBIT_MASK
                            | (it.billboard ? 1 : 0)) + offsetX);
                    short ty = (short) ((COORD_SCALE * it.y) + offsetY);

                    vertexItems.add(tx, ty, points[0], points[1], u1, v2); // lower-left
                    vertexItems.add(tx, ty, points[2], points[3], u1, v1); // upper-left
                    vertexItems.add(tx, ty, points[4], points[5], u2, v2); // upper-right
                    vertexItems.add(tx, ty, points[6], points[7], u2, v1); // lower-right
                }

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
