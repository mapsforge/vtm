/*
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
package org.oscim.layers.tile.vector.labeling;

import org.oscim.core.MapPosition;
import org.oscim.core.Tile;
import org.oscim.layers.tile.MapTile;
import org.oscim.layers.tile.TileRenderer;
import org.oscim.layers.tile.TileSet;
import org.oscim.map.Map;
import org.oscim.renderer.bucket.SymbolBucket;
import org.oscim.renderer.bucket.SymbolItem;
import org.oscim.renderer.bucket.TextItem;
import org.oscim.theme.styles.TextStyle;
import org.oscim.utils.FastMath;
import org.oscim.utils.geom.OBB2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.oscim.layers.tile.MapTile.State.NEW_DATA;
import static org.oscim.layers.tile.MapTile.State.READY;

public class LabelPlacement {
    static final boolean dbg = false;
    static final Logger log = LoggerFactory.getLogger(LabelPlacement.class);

    public final static LabelTileData getLabels(MapTile tile) {
        return (LabelTileData) tile.getData(LabelLayer.LABEL_DATA);
    }

    private final static float MIN_CAPTION_DIST = 5;
    private final static float MIN_WAY_DIST = 3;

    /**
     * thread local pool of for unused label items
     */
    private final LabelPool mPool = new LabelPool();

    private final TileSet mTileSet = new TileSet();
    private final TileRenderer mTileRenderer;
    private final Map mMap;

    /**
     * list of current labels
     */
    private Label mLabels;

    private float mSquareRadius;

    /**
     * incremented each update, to prioritize labels
     * that became visible ealier.
     */
    private int mRelabelCnt;

    public LabelPlacement(Map map, TileRenderer tileRenderer) {
        mMap = map;
        mTileRenderer = tileRenderer;
    }

    /**
     * remove Label l from mLabels and return l.next
     */
    private Label removeLabel(Label l) {
        Label ret = (Label) l.next;
        mLabels = (Label) mPool.release(mLabels, l);
        return ret;
    }

    public void addLabel(Label l) {
        l.next = mLabels;
        mLabels = l;
    }

    private byte checkOverlap(Label l) {

        for (Label o = mLabels; o != null; ) {
            //check bounding box
            if (!Label.bboxOverlaps(l, o, 100)) {
                o = (Label) o.next;
                continue;
            }

            if (Label.shareText(l, o)) {
                // keep the label that was active earlier
                if (o.active <= l.active)
                    return 1;

                // keep the label with longer segment
                if (o.length < l.length) {
                    o = removeLabel(o);
                    continue;
                }
                // keep other
                return 2;
            }
            if (l.bbox.overlaps(o.bbox)) {
                if (o.active <= l.active)
                    return 1;

                if (!o.text.caption
                        && (o.text.priority > l.text.priority
                        || o.length < l.length)) {

                    o = removeLabel(o);
                    continue;
                }
                // keep other
                return 1;
            }
            o = (Label) o.next;
        }
        return 0;
    }

    private boolean isVisible(float x, float y) {
        // rough filter
        float dist = x * x + y * y;
        if (dist > mSquareRadius)
            return false;

        return true;
    }

    private boolean wayIsVisible(Label ti) {
        // rough filter
        float dist = ti.x * ti.x + ti.y * ti.y;
        if (dist < mSquareRadius)
            return true;

        dist = ti.x1 * ti.x1 + ti.y1 * ti.y1;
        if (dist < mSquareRadius)
            return true;

        dist = ti.x2 * ti.x2 + ti.y2 * ti.y2;
        if (dist < mSquareRadius)
            return true;

        return false;
    }

    private Label getLabel() {
        Label l = (Label) mPool.get();
        l.active = Integer.MAX_VALUE;

        return l;
    }

    private static float flipLongitude(float dx, int max) {
        // flip around date-line
        if (dx > max)
            dx = dx - max * 2;
        else if (dx < -max)
            dx = dx + max * 2;

        return dx;
    }

    private void placeLabelFrom(Label l, TextItem ti) {
        // set line endpoints relative to view to be able to
        // check intersections with label from other tiles
        float w = (ti.x2 - ti.x1) / 2f;
        float h = (ti.y2 - ti.y1) / 2f;

        l.x1 = l.x - w;
        l.y1 = l.y - h;
        l.x2 = l.x + w;
        l.y2 = l.y + h;
    }

    private Label addWayLabels(MapTile t, Label l, float dx, float dy,
                               double scale) {

        LabelTileData ld = getLabels(t);
        if (ld == null)
            return l;

        for (TextItem ti : ld.labels) {
            if (ti.text.caption)
                continue;

            /* acquire a TextItem to add to TextLayer */
            if (l == null)
                l = getLabel();

            /* check if path at current scale is long enough */
            if (!dbg && ti.width > ti.length * scale)
                continue;

            l.clone(ti);
            l.x = (float) ((dx + ti.x) * scale);
            l.y = (float) ((dy + ti.y) * scale);
            placeLabelFrom(l, ti);

            if (!wayIsVisible(l))
                continue;

            byte overlaps = -1;

            if (l.bbox == null)
                l.bbox = new OBB2D(l.x, l.y, l.x1, l.y1,
                        l.width + MIN_WAY_DIST,
                        l.text.fontHeight + MIN_WAY_DIST);
            else
                l.bbox.set(l.x, l.y, l.x1, l.y1,
                        l.width + MIN_WAY_DIST,
                        l.text.fontHeight + MIN_WAY_DIST);

            if (dbg || ti.width < ti.length * scale)
                overlaps = checkOverlap(l);

            if (dbg)
                Debug.addDebugBox(l, ti, overlaps, false, (float) scale);

            if (overlaps == 0) {
                addLabel(l);
                l.item = TextItem.copy(ti);
                l.tileX = t.tileX;
                l.tileY = t.tileY;
                l.tileZ = t.zoomLevel;
                l.active = mRelabelCnt;
                l = null;
            }
        }
        return l;
    }

    private Label addNodeLabels(MapTile t, Label l, float dx, float dy,
                                double scale, float cos, float sin) {

        LabelTileData ld = getLabels(t);
        if (ld == null)
            return l;

        O:
        for (TextItem ti : ld.labels) {
            if (!ti.text.caption)
                continue;

            // acquire a TextItem to add to TextLayer
            if (l == null)
                l = getLabel();

            l.clone(ti);
            l.x = (float) ((dx + ti.x) * scale);
            l.y = (float) ((dy + ti.y) * scale);
            if (!isVisible(l.x, l.y))
                continue;

            if (l.bbox == null)
                l.bbox = new OBB2D();

            l.bbox.setNormalized(l.x, l.y, cos, -sin,
                    l.width + MIN_CAPTION_DIST,
                    l.text.fontHeight + MIN_CAPTION_DIST,
                    l.text.dy);

            for (Label o = mLabels; o != null; ) {
                if (l.bbox.overlaps(o.bbox)) {
                    if (l.text.priority < o.text.priority) {
                        o = removeLabel(o);
                        continue;
                    }
                    continue O;
                }
                o = (Label) o.next;
            }

            addLabel(l);
            l.item = TextItem.copy(ti);
            l.tileX = t.tileX;
            l.tileY = t.tileY;
            l.tileZ = t.zoomLevel;
            l.active = mRelabelCnt;
            l = null;
        }
        return l;
    }

    boolean updateLabels(LabelTask work) {

        /* get current tiles */
        boolean changedTiles = mTileRenderer.getVisibleTiles(mTileSet);

        if (mTileSet.cnt == 0) {
            return false;
        }

        MapPosition pos = work.pos;
        boolean changedPos = mMap.viewport().getMapPosition(pos);

        /* do not loop! */
        if (!changedTiles && !changedPos)
            return false;

        mRelabelCnt++;

        MapTile[] tiles = mTileSet.tiles;
        int zoom = tiles[0].zoomLevel;

        /* estimation for visible area to be labeled */
        int mw = (mMap.getWidth() + Tile.SIZE) / 2;
        int mh = (mMap.getHeight() + Tile.SIZE) / 2;
        mSquareRadius = mw * mw + mh * mh;

        /* scale of tiles zoom-level relative to current position */
        double scale = pos.scale / (1 << zoom);

        double angle = Math.toRadians(pos.bearing);
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);

        int maxx = Tile.SIZE << (zoom - 1);

        // FIXME ???
        SymbolBucket sl = work.symbolLayer;
        sl.clearItems();

        double tileX = (pos.x * (Tile.SIZE << zoom));
        double tileY = (pos.y * (Tile.SIZE << zoom));

        /* put current label to previous label */
        Label prevLabels = mLabels;

        /* new labels */
        mLabels = null;
        Label l = null;

        /* add currently active labels first */
        for (l = prevLabels; l != null; ) {

            if (l.text.caption) {
                // TODO!!!
                l = mPool.releaseAndGetNext(l);
                continue;
            }

            int diff = l.tileZ - zoom;
            if (diff > 1 || diff < -1) {
                l = mPool.releaseAndGetNext(l);
                continue;
            }

            float div = FastMath.pow(diff);
            float sscale = (float) (pos.scale / (1 << l.tileZ));

            // plus 10 to rather keep label and avoid flickering
            if (l.width > (l.length + 10) * sscale) {
                l = mPool.releaseAndGetNext(l);
                continue;
            }

            float dx = (float) (l.tileX * Tile.SIZE - tileX * div);
            float dy = (float) (l.tileY * Tile.SIZE - tileY * div);

            dx = flipLongitude(dx, maxx);
            l.x = (float) ((dx + l.item.x) * sscale);
            l.y = (float) ((dy + l.item.y) * sscale);
            placeLabelFrom(l, l.item);

            if (!wayIsVisible(l)) {
                l = mPool.releaseAndGetNext(l);
                continue;
            }

            l.bbox.set(l.x, l.y, l.x1, l.y1,
                    l.width + MIN_WAY_DIST,
                    l.text.fontHeight + MIN_WAY_DIST);

            byte overlaps = checkOverlap(l);

            if (dbg)
                Debug.addDebugBox(l, l.item, overlaps, true, sscale);

            if (overlaps == 0) {
                Label ll = l;
                l = (Label) l.next;

                ll.next = null;
                addLabel(ll);
                continue;
            }
            l = mPool.releaseAndGetNext(l);
        }

        /* add way labels */
        for (int i = 0, n = mTileSet.cnt; i < n; i++) {
            MapTile t = tiles[i];
            if (!t.state(READY | NEW_DATA))
                continue;

            float dx = (float) (t.tileX * Tile.SIZE - tileX);
            float dy = (float) (t.tileY * Tile.SIZE - tileY);
            dx = flipLongitude(dx, maxx);

            l = addWayLabels(t, l, dx, dy, scale);
        }

        /* add caption */
        for (int i = 0, n = mTileSet.cnt; i < n; i++) {
            MapTile t = tiles[i];
            if (!t.state(READY | NEW_DATA))
                continue;

            float dx = (float) (t.tileX * Tile.SIZE - tileX);
            float dy = (float) (t.tileY * Tile.SIZE - tileY);
            dx = flipLongitude(dx, maxx);

            l = addNodeLabels(t, l, dx, dy, scale, cos, sin);
        }

        for (Label ti = mLabels; ti != null; ti = (Label) ti.next) {
            /* add caption symbols */
            if (ti.text.caption) {
                if (ti.text.bitmap != null || ti.text.texture != null) {
                    SymbolItem s = SymbolItem.pool.get();
                    if (ti.text.bitmap != null)
                        s.bitmap = ti.text.bitmap;
                    else
                        s.texRegion = ti.text.texture;
                    s.x = ti.x;
                    s.y = ti.y;
                    s.billboard = true;
                    sl.addSymbol(s);
                }
                continue;
            }

            /* flip way label orientation */
            if (cos * (ti.x2 - ti.x1) - sin * (ti.y2 - ti.y1) < 0) {
                float tmp = ti.x1;
                ti.x1 = ti.x2;
                ti.x2 = tmp;

                tmp = ti.y1;
                ti.y1 = ti.y2;
                ti.y2 = tmp;
            }
        }

        /* add symbol items */
        for (int i = 0, n = mTileSet.cnt; i < n; i++) {
            MapTile t = tiles[i];
            if (!t.state(READY | NEW_DATA))
                continue;

            float dx = (float) (t.tileX * Tile.SIZE - tileX);
            float dy = (float) (t.tileY * Tile.SIZE - tileY);
            dx = flipLongitude(dx, maxx);

            LabelTileData ld = getLabels(t);
            if (ld == null)
                continue;

            for (SymbolItem ti : ld.symbols) {
                if (ti.bitmap == null && ti.texRegion == null)
                    continue;

                int x = (int) ((dx + ti.x) * scale);
                int y = (int) ((dy + ti.y) * scale);

                if (!isVisible(x, y))
                    continue;

                SymbolItem s = SymbolItem.pool.get();
                if (ti.bitmap != null)
                    s.bitmap = ti.bitmap;
                else
                    s.texRegion = ti.texRegion;
                s.x = x;
                s.y = y;
                s.billboard = true;
                sl.addSymbol(s);
            }
        }

        /* temporary used Label */
        l = (Label) mPool.release(l);

        /* draw text to bitmaps and create vertices */
        work.textLayer.labels = groupLabels(mLabels);
        work.textLayer.prepare();
        work.textLayer.labels = null;

        /* remove tile locks */
        mTileRenderer.releaseTiles(mTileSet);

        return true;
    }

    public void cleanup() {
        mLabels = (Label) mPool.releaseAll(mLabels);
        mTileSet.releaseTiles();
    }

    /**
     * group labels by string and type
     */
    protected Label groupLabels(Label labels) {
        for (Label cur = labels; cur != null; cur = (Label) cur.next) {
            /* keep pointer to previous for removal */
            Label p = cur;
            TextStyle t = cur.text;
            float w = cur.width;

            /* iterate through following */
            for (Label l = (Label) cur.next; l != null; l = (Label) l.next) {

                if (w != l.width || t != l.text || !cur.label.equals(l.label)) {
                    p = l;
                    continue;
                } else if (cur.next == l) {
                    l.label = cur.label;
                    p = l;
                    continue;
                }
                l.label = cur.label;

                /* insert l after cur */
                Label tmp = (Label) cur.next;
                cur.next = l;

                /* continue outer loop at l */
                cur = l;

                /* remove l from previous place */
                p.next = l.next;
                l.next = tmp;

                /* continue from previous */
                l = p;
            }
        }
        return labels;
    }
}
