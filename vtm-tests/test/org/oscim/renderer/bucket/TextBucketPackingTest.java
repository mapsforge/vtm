/*
 * Copyright 2026 jhotadhari
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

import org.junit.BeforeClass;
import org.junit.Test;
import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Canvas;
import org.oscim.backend.canvas.Paint;
import org.oscim.renderer.MapRenderer;
import org.oscim.theme.styles.TextStyle;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Packing invariant and benchmark test for the opt-in TextBucket packing fix
 * (CanvasAdapter.correctTextPacking).
 */
public class TextBucketPackingTest {

    private static final int MAX_V = (int) (TextureBucket.TEXTURE_HEIGHT * MapRenderer.COORD_SCALE);

    static class TestBitmap implements Bitmap {
        final int w, h;

        TestBitmap(int w, int h) {
            this.w = w;
            this.h = h;
        }

        @Override public int getWidth() { return w; }
        @Override public int getHeight() { return h; }
        @Override public void recycle() {}
        @Override public int[] getPixels() { return new int[0]; }
        @Override public void eraseColor(int color) {}
        @Override public void uploadToTexture(boolean replace) {}
        @Override public boolean isValid() { return true; }
        @Override public byte[] getPngEncodedData() { return new byte[0]; }
        @Override public void scaleTo(int width, int height) {}
    }

    static class TestPaint implements Paint {
        float textSize = 10;

        @Override public int getColor() { return 0; }
        @Override public void setColor(int color) {}
        @Override public void setStrokeCap(Cap cap) {}
        @Override public void setStrokeJoin(Join join) {}
        @Override public void setStrokeWidth(float width) {}
        @Override public void setStyle(Style style) {}
        @Override public void setTextAlign(Align align) {}
        @Override public void setTextSize(float textSize) { this.textSize = textSize; }
        @Override public void setTypeface(FontFamily fontFamily, FontStyle fontStyle) {}
        @Override public float measureText(String text) { return text.length() * textSize * 0.55f; }
        @Override public float getFontHeight() { return textSize * 1.2f; }
        @Override public float getFontDescent() { return textSize * 0.2f; }
        @Override public float getStrokeWidth() { return 0; }
        @Override public Style getStyle() { return Style.FILL; }
        @Override public float getTextHeight(String text) { return getFontHeight(); }
        @Override public float getTextWidth(String text) { return measureText(text); }
    }

    static class TestCanvas implements Canvas {
        @Override public void setBitmap(Bitmap bitmap) {}
        @Override public void drawText(String string, float x, float y, Paint paint) {}
        @Override public void drawText(String string, float x, float y, Paint fill, Paint stroke) {}
        @Override public void drawBitmap(Bitmap bitmap, float x, float y) {}
        @Override public void drawBitmapScaled(Bitmap bitmap) {}
        @Override public void drawCircle(float x, float y, float radius, Paint paint) {}
        @Override public void drawLine(float x1, float y1, float x2, float y2, Paint paint) {}
        @Override public void fillColor(int color) {}
        @Override public void fillRectangle(float x, float y, float width, float height, int color) {}
        @Override public int getHeight() { return 0; }
        @Override public int getWidth() { return 0; }
    }

    static class TestCanvasAdapter extends CanvasAdapter {
        static void initAdapter() {
            CanvasAdapter.init(new TestCanvasAdapter());
        }

        @Override
        protected Canvas newCanvasImpl() { return new TestCanvas(); }
        @Override
        protected Paint newPaintImpl() { return new TestPaint(); }
        @Override
        protected Bitmap newBitmapImpl(int width, int height, int format) {
            return new TestBitmap(width, height);
        }
        @Override
        protected Bitmap decodeSvgBitmapImpl(java.io.InputStream inputStream, int width, int height, int percent) {
            return new TestBitmap(width, height);
        }
        @Override
        protected Bitmap decodeBitmapImpl(java.io.InputStream inputStream) throws java.io.IOException {
            return new TestBitmap(0, 0);
        }
        @Override
        protected Bitmap decodeBitmapImpl(java.io.InputStream inputStream, int width, int height, int percent) throws java.io.IOException {
            return new TestBitmap(width, height);
        }
        @Override
        protected Bitmap loadBitmapAssetImpl(String relativePathPrefix, String src,
                                             org.oscim.theme.XmlThemeResourceProvider resourceProvider,
                                             int width, int height, int percent,
                                             org.oscim.theme.ThemeCallback themeCallback) {
            return new TestBitmap(width, height);
        }
    }

    @BeforeClass
    public static void setup() {
        TestCanvasAdapter.initAdapter();
    }

    static TextStyle style(float fontSize, boolean caption) {
        return TextStyle.builder().fontSize(fontSize).isCaption(caption).build();
    }

    static TextBucket makeBucket(Random r, int count, float[] fontSizes) {
        TextBucket bucket = new TextBucket();
        for (int i = 0; i < count; i++) {
            float fontSize = fontSizes[r.nextInt(fontSizes.length)];
            TextStyle textStyle = style(fontSize, fontSize > 30);
            int len = 2 + r.nextInt(20);
            StringBuilder sb = new StringBuilder(len);
            for (int j = 0; j < len; j++)
                sb.append((char) ('a' + r.nextInt(26)));
            TextItem item = TextItem.pool.get();
            item.set(r.nextInt(1000), r.nextInt(1000), sb.toString(), textStyle);
            bucket.addText(item);
        }
        return bucket;
    }

    static short[] collectVertices(TextBucket bucket) {
        VertexData vi = bucket.vertexItems;
        int size = vi.countSize();
        short[] out = new short[size];
        int pos = 0;
        for (VertexData.Chunk c = vi.head(); c != null; c = c.next) {
            int n = Math.min(c.used, VertexData.SIZE);
            System.arraycopy(c.vertices, 0, out, pos, n);
            pos += n;
        }
        return out;
    }

    static int maxV(short[] vertices) {
        int max = 0;
        for (int i = 5; i < vertices.length; i += 6)
            max = Math.max(max, vertices[i] & 0xffff);
        return max;
    }

    static int textureCount(TextBucket bucket) {
        int n = 0;
        for (TextureItem t = bucket.textures; t != null; t = t.next)
            n++;
        return n;
    }

    static long timePrepare(long seed, int count, float[] sizes, int reps) {
        for (int i = 0; i < 3; i++)
            makeBucket(new Random(seed), count, sizes).prepare();
        long start = System.nanoTime();
        for (int i = 0; i < reps; i++)
            makeBucket(new Random(seed), count, sizes).prepare();
        return (System.nanoTime() - start) / reps;
    }

    @Test
    public void typicalSetPackAllSmall() {
        float[] sizes = {10, 12, 13};
        long seed = 42;

        CanvasAdapter.correctTextPacking = false;
        TextBucket off = makeBucket(new Random(seed), 300, sizes);
        off.prepare();
        int maxVOff = maxV(collectVertices(off));
        int texturesOff = textureCount(off);
        long timeOff = timePrepare(seed, 300, sizes, 100);

        CanvasAdapter.correctTextPacking = true;
        TextBucket on = makeBucket(new Random(seed), 300, sizes);
        on.prepare();
        int maxVOn = maxV(collectVertices(on));
        int texturesOn = textureCount(on);
        long timeOn = timePrepare(seed, 300, sizes, 100);

        System.out.println("typical: maxV off=" + maxVOff + " on=" + maxVOn
                + " (limit " + MAX_V + ") textures off=" + texturesOff + " on=" + texturesOn
                + " prepare off=" + timeOff + "ns on=" + timeOn + "ns");

        assertEquals("small labels must not overflow without fix", true, maxVOff <= MAX_V);
        assertEquals("small labels must not overflow with fix", true, maxVOn <= MAX_V);
        assertEquals(texturesOff, texturesOn);

        CanvasAdapter.correctTextPacking = false;
    }

    @Test
    public void tallCaptionsOverflowOnlyWithoutFix() {
        float[] sizes = {12, 67};
        long seed = 1234;

        CanvasAdapter.correctTextPacking = false;
        TextBucket off = makeBucket(new Random(seed), 300, sizes);
        off.prepare();
        int maxVOff = maxV(collectVertices(off));
        int texturesOff = textureCount(off);
        long timeOff = timePrepare(seed, 300, sizes, 100);

        CanvasAdapter.correctTextPacking = true;
        TextBucket on = makeBucket(new Random(seed), 300, sizes);
        on.prepare();
        int maxVOn = maxV(collectVertices(on));
        int texturesOn = textureCount(on);
        long timeOn = timePrepare(seed, 300, sizes, 100);

        System.out.println("tall: maxV off=" + maxVOff + " on=" + maxVOn
                + " (limit " + MAX_V + ") textures off=" + texturesOff + " on=" + texturesOn
                + " prepare off=" + timeOff + "ns on=" + timeOn + "ns");

        assertTrue("without fix tall captions overflow the atlas (bug)", maxVOff > MAX_V);
        assertTrue("with fix no cell exceeds the atlas", maxVOn <= MAX_V);
        assertTrue("fix may only add textures", texturesOn >= texturesOff);

        CanvasAdapter.correctTextPacking = false;
    }

    @Test
    public void mixedSetTimings() {
        float[] sizes = {10, 12, 13, 40, 56};
        long seed = 7;

        CanvasAdapter.correctTextPacking = false;
        long timeOff = timePrepare(seed, 300, sizes, 100);

        CanvasAdapter.correctTextPacking = true;
        long timeOn = timePrepare(seed, 300, sizes, 100);

        System.out.println("mixed: prepare off=" + timeOff + "ns on=" + timeOn + "ns");

        CanvasAdapter.correctTextPacking = false;
    }
}
