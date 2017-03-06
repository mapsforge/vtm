/*
 * Copyright 2017 Longri
 *
 * Based on PixmapPacker from LibGdx converted to use VTM Bitmaps without any LibGdx dependencies:
 * https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/graphics/g2d/PixmapPacker.java
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BitmapPacker {

    private final int atlasWidth, atlasHeight;
    private final int padding;
    private final PackStrategy packStrategy;
    private final boolean flipY;

    private final List<PackerAtlasItem> packerAtlasItems = new ArrayList<>();

    public BitmapPacker(int atlasWidth, int atlasHeight, int padding, boolean flipY) {
        this(atlasWidth, atlasHeight, padding, new GuillotineStrategy(), flipY);
    }

    public BitmapPacker(int atlasWidth, int atlasHeight, int padding, PackStrategy packStrategy,
                        boolean flipY) {
        this.atlasWidth = atlasWidth;
        this.atlasHeight = atlasHeight;
        this.padding = padding;
        this.packStrategy = packStrategy;
        this.flipY = flipY;
    }

    public synchronized Rect add(Object key, Bitmap image) {
        Rect rect = new Rect(0, 0, image.getWidth(), image.getHeight());
        if (rect.width > atlasWidth || rect.height > atlasHeight) {
            if (key == null)
                throw new RuntimeException("PackerAtlasItem size too small for Bitmap.");
            throw new RuntimeException("PackerAtlasItem size too small for Bitmap: " + key);
        }

        PackerAtlasItem packerAtlasItem = packStrategy.pack(this, key, rect);
        if (key != null) {
            packerAtlasItem.rects.put(key, rect);
            packerAtlasItem.addedRects.add(key);
        }

        int rectX = rect.x, rectY = rect.y, rectWidth = rect.width, rectHeight = rect.height;

        packerAtlasItem.drawBitmap(image, rectX,
                flipY ? packerAtlasItem.image.getHeight() - rectY - rectHeight : rectY);

        return rect;
    }

    public synchronized PackerAtlasItem getAtlasItem(int index) {
        return packerAtlasItems.get(index);
    }

    public int getAtlasCount() {
        return packerAtlasItems.size();
    }

    public static class PackerAtlasItem {
        HashMap<Object, Rect> rects = new HashMap<>();
        final Bitmap image;
        final Canvas canvas;
        final ArrayList<Object> addedRects = new ArrayList<>();

        PackerAtlasItem(BitmapPacker packer) {
            image = CanvasAdapter.newBitmap(packer.atlasWidth, packer.atlasHeight, 0);
            canvas = CanvasAdapter.newCanvas();
            canvas.setBitmap(this.image);
            canvas.fillColor(Color.TRANSPARENT);
        }

        public TextureAtlas getAtlas() {
            TextureAtlas atlas = new TextureAtlas(image);
            //add regions
            for (Map.Entry<Object, Rect> entry : rects.entrySet()) {
                atlas.addTextureRegion(entry.getKey(), entry.getValue().getAtlasRect());
            }
            return atlas;
        }

        void drawBitmap(Bitmap image, int x, int y) {
            canvas.drawBitmap(image, x, y);
        }
    }

    public interface PackStrategy {
        void sort(ArrayList<Bitmap> images);

        PackerAtlasItem pack(BitmapPacker packer, Object key, Rect rect);
    }

    /**
     * Does bin packing by inserting to the right or below previously packed rectangles.
     * This is good at packing arbitrarily sized images.
     *
     * @author mzechner
     * @author Nathan Sweet
     * @author Rob Rendell
     */
    public static class GuillotineStrategy implements PackStrategy {
        Comparator<Bitmap> comparator;

        public void sort(ArrayList<Bitmap> Bitmaps) {
            if (comparator == null) {
                comparator = new Comparator<Bitmap>() {
                    public int compare(Bitmap o1, Bitmap o2) {
                        return Math.max(o1.getWidth(), o1.getHeight()) - Math.max(o2.getWidth(), o2.getHeight());
                    }
                };
            }
            Collections.sort(Bitmaps, comparator);
        }

        public PackerAtlasItem pack(BitmapPacker packer, Object key, Rect rect) {
            GuillotineAtlasItem atlasItem;
            if (packer.packerAtlasItems.size() == 0) {
                // Add a atlas item if empty.
                atlasItem = new GuillotineAtlasItem(packer);
                packer.packerAtlasItems.add(atlasItem);
            } else {
                // Always try to pack into the last atlas item.
                atlasItem = (GuillotineAtlasItem) packer.packerAtlasItems.get(packer.packerAtlasItems.size() - 1);
            }

            int padding = packer.padding;
            rect.width += padding;
            rect.height += padding;
            Node node = insert(atlasItem.root, rect);
            if (node == null) {
                // Didn't fit, pack into a new atlas item.
                atlasItem = new GuillotineAtlasItem(packer);
                packer.packerAtlasItems.add(atlasItem);
                node = insert(atlasItem.root, rect);
            }
            node.full = true;
            rect.set(node.rect.x, node.rect.y, node.rect.width - padding, node.rect.height - padding);
            return atlasItem;
        }

        private Node insert(Node node, Rect rect) {
            if (!node.full && node.leftChild != null && node.rightChild != null) {
                Node newNode = insert(node.leftChild, rect);
                if (newNode == null) newNode = insert(node.rightChild, rect);
                return newNode;
            } else {
                if (node.full) return null;
                if (node.rect.width == rect.width && node.rect.height == rect.height) return node;
                if (node.rect.width < rect.width || node.rect.height < rect.height) return null;

                node.leftChild = new Node();
                node.rightChild = new Node();

                int deltaWidth = node.rect.width - rect.width;
                int deltaHeight = node.rect.height - rect.height;
                if (deltaWidth > deltaHeight) {
                    node.leftChild.rect.x = node.rect.x;
                    node.leftChild.rect.y = node.rect.y;
                    node.leftChild.rect.width = rect.width;
                    node.leftChild.rect.height = node.rect.height;

                    node.rightChild.rect.x = node.rect.x + rect.width;
                    node.rightChild.rect.y = node.rect.y;
                    node.rightChild.rect.width = node.rect.width - rect.width;
                    node.rightChild.rect.height = node.rect.height;
                } else {
                    node.leftChild.rect.x = node.rect.x;
                    node.leftChild.rect.y = node.rect.y;
                    node.leftChild.rect.width = node.rect.width;
                    node.leftChild.rect.height = rect.height;

                    node.rightChild.rect.x = node.rect.x;
                    node.rightChild.rect.y = node.rect.y + rect.height;
                    node.rightChild.rect.width = node.rect.width;
                    node.rightChild.rect.height = node.rect.height - rect.height;
                }

                return insert(node.leftChild, rect);
            }
        }

        static final class Node {
            Node leftChild;
            Node rightChild;
            final Rect rect = new Rect();
            boolean full;
        }

        static class GuillotineAtlasItem extends PackerAtlasItem {
            Node root;

            GuillotineAtlasItem(BitmapPacker packer) {
                super(packer);
                root = new Node();
                root.rect.x = packer.padding;
                root.rect.y = packer.padding;
                root.rect.width = packer.atlasWidth - packer.padding * 2;
                root.rect.height = packer.atlasHeight - packer.padding * 2;
            }
        }
    }

    /**
     * Does bin packing by inserting in rows. This is good at packing images that have similar heights.
     *
     * @author Nathan Sweet
     */
    public static class SkylineStrategy implements PackStrategy {
        Comparator<Bitmap> comparator;

        public void sort(ArrayList<Bitmap> images) {
            if (comparator == null) {
                comparator = new Comparator<Bitmap>() {
                    public int compare(Bitmap o1, Bitmap o2) {
                        return o1.getHeight() - o2.getHeight();
                    }
                };
            }
            Collections.sort(images, comparator);
        }

        public PackerAtlasItem pack(BitmapPacker packer, Object key, Rect rect) {
            int padding = packer.padding;
            int atlasWidth = packer.atlasWidth - padding * 2, atlasHeight = packer.atlasHeight - padding * 2;
            int rectWidth = rect.width + padding, rectHeight = rect.height + padding;
            for (int i = 0, n = packer.packerAtlasItems.size(); i < n; i++) {
                SkylineAtlasItem atlasItem = (SkylineAtlasItem) packer.packerAtlasItems.get(i);
                SkylineAtlasItem.Row bestRow = null;
                // Fit in any row before the last.
                for (int ii = 0, nn = atlasItem.rows.size() - 1; ii < nn; ii++) {
                    SkylineAtlasItem.Row row = atlasItem.rows.get(ii);
                    if (row.x + rectWidth >= atlasWidth) continue;
                    if (row.y + rectHeight >= atlasHeight) continue;
                    if (rectHeight > row.height) continue;
                    if (bestRow == null || row.height < bestRow.height) bestRow = row;
                }
                if (bestRow == null) {
                    // Fit in last row, increasing height.
                    SkylineAtlasItem.Row row = atlasItem.rows.get(atlasItem.rows.size() - 1);
                    if (row.y + rectHeight >= atlasHeight) continue;
                    if (row.x + rectWidth < atlasWidth) {
                        row.height = Math.max(row.height, rectHeight);
                        bestRow = row;
                    } else {
                        // Fit in new row.
                        bestRow = new SkylineAtlasItem.Row();
                        bestRow.y = row.y + row.height;
                        bestRow.height = rectHeight;
                        if (bestRow.y + bestRow.height > atlasHeight) continue;
                        atlasItem.rows.add(bestRow);
                    }
                }

                rect.x = bestRow.x;
                rect.y = bestRow.y;
                bestRow.x += rectWidth;
                return atlasItem;
            }
            // Fit in new atlas item.
            SkylineAtlasItem atlasItem = new SkylineAtlasItem(packer);
            packer.packerAtlasItems.add(atlasItem);
            SkylineAtlasItem.Row row = new SkylineAtlasItem.Row();
            row.x = padding + rectWidth;
            row.y = padding;
            row.height = rectHeight;
            atlasItem.rows.add(row);
            rect.x = padding;
            rect.y = padding;
            return atlasItem;
        }

        static class SkylineAtlasItem extends PackerAtlasItem {
            ArrayList<Row> rows = new ArrayList<>();

            SkylineAtlasItem(BitmapPacker packer) {
                super(packer);

            }

            static class Row {
                int x, y, height;
            }
        }
    }

    private static class Rect {
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
