/*
 * Copyright 2012 Hannes Janetzek
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

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Canvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.oscim.renderer.MapRenderer.COORD_SCALE;

public class TextBucket extends TextureBucket {
    static final Logger log = LoggerFactory.getLogger(TextBucket.class);

    protected final static int LBIT_MASK = 0xfffffffe;

    protected static int mFontPadX = 1;
    //private static int mFontPadY = 1;

    public TextItem labels;
    protected final Canvas mCanvas;

    public TextItem getLabels() {
        return labels;
    }

    public void setLabels(TextItem labels) {
        this.labels = labels;
    }

    public TextBucket() {
        super(RenderBucket.SYMBOL);
        mCanvas = CanvasAdapter.newCanvas();
        fixed = true;
        level = -1;
    }

    public void addText(TextItem item) {
        TextItem it = labels;

        for (; it != null; it = it.next) {

            if (item.text == it.text) {
                while (it.next != null
                        /* break if next item uses different text style */
                        && item.text == it.next.text
                        /* check same string instance */
                        && item.label != it.label
                        /* check same string */
                        && !item.label.equals(it.label))
                    it = it.next;

                /* unify duplicate string
                 * // Note: this is required for 'packing test' in prepare to
                 * work! */
                if (item.label != it.label && item.label.equals(it.label))
                    item.label = it.label;

                /* insert after text of same type and/or before same string */
                item.next = it.next;
                it.next = item;
                return;
            }
        }

        item.next = labels;
        labels = item;
    }

    @Override
    public void prepare() {
        int numIndices = 0;
        int offsetIndices = 0;

        int advanceY = 0;
        float x = 0;
        float y = 0;
        float yy;

        TextureItem t = pool.get();
        textures = t;
        mCanvas.setBitmap(t.bitmap);

        for (TextItem it = labels; it != null; ) {

            float width = it.width + 2 * mFontPadX;
            float height = (int) (it.text.fontHeight) + 0.5f;

            if (height > TEXTURE_HEIGHT)
                height = TEXTURE_HEIGHT;

            if (height > advanceY)
                advanceY = (int) height;

            if (x + width > TEXTURE_WIDTH) {
                x = 0;
                y += advanceY;
                advanceY = (int) (height + 0.5f);

                if (y + height > TEXTURE_HEIGHT) {
                    t.offset = offsetIndices;
                    t.indices = (numIndices - offsetIndices);
                    offsetIndices = numIndices;

                    t.next = pool.get();
                    t = t.next;

                    mCanvas.setBitmap(t.bitmap);

                    x = 0;
                    y = 0;
                    advanceY = (int) height;
                }
            }

            yy = y + height - it.text.fontDescent;

            mCanvas.drawText(it.label, x, yy, it.text.paint, it.text.stroke);

            // FIXME !!!
            if (width > TEXTURE_WIDTH)
                width = TEXTURE_WIDTH;

            while (it != null) {
                addItem(it, width, height, x, y);

                /* six indices to draw the four vertices */
                numIndices += TextureBucket.INDICES_PER_SPRITE;
                numVertices += 4;

                if (it.next == null
                        || (it.next.text != it.text)
                        || (it.next.label != it.label)) {
                    it = it.next;
                    break;
                }
                it = it.next;

            }
            x += width;
        }

        t.offset = offsetIndices;
        t.indices = (numIndices - offsetIndices);
    }

    protected void addItem(TextItem it,
                           float width, float height, float x, float y) {
        /* texture coordinates */
        short u1 = (short) (COORD_SCALE * x);
        short v1 = (short) (COORD_SCALE * y);
        short u2 = (short) (COORD_SCALE * (x + width));
        short v2 = (short) (COORD_SCALE * (y + height));

        short x1, x2, x3, x4, y1, y3, y2, y4;
        float hw = width / 2.0f;
        float hh = height / 2.0f;
        if (it.text.caption) {
            x1 = x3 = (short) (COORD_SCALE * -hw);
            x2 = x4 = (short) (COORD_SCALE * hw);
            y1 = y2 = (short) (COORD_SCALE * (it.text.dy + hh));
            y3 = y4 = (short) (COORD_SCALE * (it.text.dy - hh));
        } else {
            float vx = it.x1 - it.x2;
            float vy = it.y1 - it.y2;
            float a = (float) Math.sqrt(vx * vx + vy * vy);
            vx = vx / a;
            vy = vy / a;

            float ux = -vy * hh;
            float uy = vx * hh;

            float ux2 = -vy * hh;
            float uy2 = vx * hh;

            vx *= hw;
            vy *= hw;

            /* top-left */
            x1 = (short) (COORD_SCALE * (vx - ux));
            y1 = (short) (COORD_SCALE * (vy - uy));
            /* top-right */
            x2 = (short) (COORD_SCALE * (-vx - ux));
            y2 = (short) (COORD_SCALE * (-vy - uy));
            /* bot-right */
            x4 = (short) (COORD_SCALE * (-vx + ux2));
            y4 = (short) (COORD_SCALE * (-vy + uy2));
            /* bot-left */
            x3 = (short) (COORD_SCALE * (vx + ux2));
            y3 = (short) (COORD_SCALE * (vy + uy2));
        }

        /* add vertices */
        int tmp = (int) (COORD_SCALE * it.x) & LBIT_MASK;
        short tx = (short) (tmp | (it.text.caption ? 1 : 0));
        short ty = (short) (COORD_SCALE * it.y);

        vertexItems.add(tx, ty, x1, y1, u1, v2);
        vertexItems.add(tx, ty, x3, y3, u1, v1);
        vertexItems.add(tx, ty, x2, y2, u2, v2);
        vertexItems.add(tx, ty, x4, y4, u2, v1);
    }

    @Override
    public void clear() {
        super.clear();
        clearLabels();
    }

    public void clearLabels() {
        labels = TextItem.pool.releaseAll(labels);
    }
}
