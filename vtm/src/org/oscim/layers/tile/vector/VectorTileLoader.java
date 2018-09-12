/*
 * Copyright 2012-2014 Hannes Janetzek
 * Copyright 2016-2017 devemux86
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
package org.oscim.layers.tile.vector;

import org.oscim.core.GeometryBuffer.GeometryType;
import org.oscim.core.MapElement;
import org.oscim.core.MercatorProjection;
import org.oscim.core.Tag;
import org.oscim.core.TagSet;
import org.oscim.core.Tile;
import org.oscim.layers.tile.MapTile;
import org.oscim.layers.tile.TileLoader;
import org.oscim.renderer.bucket.CircleBucket;
import org.oscim.renderer.bucket.LineBucket;
import org.oscim.renderer.bucket.LineTexBucket;
import org.oscim.renderer.bucket.MeshBucket;
import org.oscim.renderer.bucket.PolygonBucket;
import org.oscim.renderer.bucket.RenderBuckets;
import org.oscim.theme.IRenderTheme;
import org.oscim.theme.RenderTheme;
import org.oscim.theme.styles.AreaStyle;
import org.oscim.theme.styles.CircleStyle;
import org.oscim.theme.styles.ExtrusionStyle;
import org.oscim.theme.styles.LineStyle;
import org.oscim.theme.styles.RenderStyle;
import org.oscim.theme.styles.SymbolStyle;
import org.oscim.theme.styles.TextStyle;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.oscim.layers.tile.MapTile.State.LOADING;

public class VectorTileLoader extends TileLoader implements RenderStyle.Callback {

    static final Logger log = LoggerFactory.getLogger(VectorTileLoader.class);

    protected static final double STROKE_INCREASE = Math.sqrt(2.5);
    protected static final byte LAYERS = 11;

    public static final byte STROKE_MIN_ZOOM = 12;

    protected IRenderTheme renderTheme;

    /**
     * current TileDataSource used by this MapTileLoader
     */
    protected ITileDataSource mTileDataSource;

    /**
     * currently processed MapElement
     */
    protected MapElement mElement;

    /**
     * current line bucket (will be used for outline bucket)
     */
    protected LineBucket mCurLineBucket;

    /**
     * Current bucket for adding elements
     */
    protected int mCurBucket;

    /**
     * Line-scale-factor depending on zoom and latitude
     */
    protected float mLineScale = 1.0f;

    protected RenderBuckets mBuckets;

    private final VectorTileLayer mTileLayer;

    public VectorTileLoader(VectorTileLayer tileLayer) {
        super(tileLayer.getManager());
        mTileLayer = tileLayer;
    }

    @Override
    public void dispose() {
        if (mTileDataSource != null)
            mTileDataSource.dispose();
    }

    @Override
    public void cancel() {
        if (mTileDataSource != null)
            mTileDataSource.cancel();
    }

    @Override
    public boolean loadTile(MapTile tile) {

        if (mTileDataSource == null) {
            log.error("no tile source is set");
            return false;
        }
        renderTheme = mTileLayer.getTheme();
        if (renderTheme == null) {
            log.error("no theme is set");
            return false;
        }

        //mTileLayer.getLoaderHooks();

        /* account for area changes with latitude */
        double lat = MercatorProjection.toLatitude(tile.y);
        mLineScale = (float) Math.pow(STROKE_INCREASE, tile.zoomLevel - STROKE_MIN_ZOOM);
        if (mLineScale < 1)
            mLineScale = 1;

        /* scale line width relative to latitude + PI * thumb */
        mLineScale *= 0.4f + 0.6f * ((float) Math.sin(Math.abs(lat) * (Math.PI / 180)));
        mBuckets = new RenderBuckets();
        tile.data = mBuckets;

        try {
            /* query data source, which calls process() callback */
            mTileDataSource.query(tile, this);
        } catch (NullPointerException e) {
            log.debug("NPE {} {}", tile, e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            log.debug("{} {}", tile, e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void completed(QueryResult result) {
        boolean ok = (result == QueryResult.SUCCESS);

        mTileLayer.callHooksComplete(mTile, ok);

        /* finish buckets- tessellate and cleanup on worker-thread */
        mBuckets.prepare();
        clearState();

        super.completed(result);
    }

    protected static int getValidLayer(int layer) {
        if (layer < 0) {
            return 0;
        } else if (layer >= LAYERS) {
            return LAYERS - 1;
        } else {
            return layer;
        }
    }

    public void setDataSource(ITileDataSource dataSource) {
        dispose();
        mTileDataSource = dataSource;
    }

    static class TagReplacement {
        public TagReplacement(String key) {
            this.key = key;
            this.tag = new Tag(key, null);
        }

        String key;
        Tag tag;
    }

    /**
     * Override this method to change tags that should be passed
     * to {@link RenderTheme} matching.
     * E.g. to replace tags that should not be cached in Rendertheme
     */
    protected TagSet filterTags(TagSet tagSet) {
        return tagSet;
    }

    @Override
    public void process(MapElement element) {
        if (isCanceled() || !mTile.state(LOADING))
            return;

        if (mTileLayer.callProcessHooks(mTile, mBuckets, element))
            return;

        TagSet tags = filterTags(element.tags);
        if (tags == null)
            return;

        mElement = element;

        /* get and apply render instructions */
        if (element.type == GeometryType.POINT) {
            renderNode(renderTheme.matchElement(element.type, tags, mTile.zoomLevel));
        } else {
            mCurBucket = getValidLayer(element.layer) * renderTheme.getLevels();
            renderWay(renderTheme.matchElement(element.type, tags, mTile.zoomLevel));
        }
        clearState();
    }

    protected void renderWay(RenderStyle[] style) {
        if (style == null)
            return;

        for (int i = 0, n = style.length; i < n; i++)
            style[i].renderWay(this);
    }

    protected void renderNode(RenderStyle[] style) {
        if (style == null)
            return;

        for (int i = 0, n = style.length; i < n; i++)
            style[i].renderNode(this);
    }

    protected void clearState() {
        mCurLineBucket = null;
        mElement = null;
    }

    /***
     * RenderThemeCallback
     ***/
    @Override
    public void renderWay(LineStyle line, int level) {
        int nLevel = mCurBucket + level;

        if (line.outline && mCurLineBucket == null) {
            log.debug("missing line for outline! " + mElement.tags
                    + " lvl:" + level + " layer:" + mElement.layer);
            return;
        }

        //LineBucket lb;

        if (line.stipple == 0 && line.texture == null) {
            //lb = mBuckets.getLineBucket(nLevel);
            //    else
            //        lb = mBuckets.getLineTexBucket(nLevel);

            LineBucket lb = mBuckets.getLineBucket(nLevel);

            if (lb.line == null) {
                lb.line = line;
                lb.scale = line.fixed ? 1 : mLineScale;
                lb.setExtents(-16, Tile.SIZE + 16);
            }

            if (line.outline) {
                lb.addOutline(mCurLineBucket);
                return;
            }

            lb.addLine(mElement);

            /* keep reference for outline layer(s) */
            //if (!(lb instanceof LineTexBucket))
            mCurLineBucket = lb;

        } else {
            LineTexBucket lb = mBuckets.getLineTexBucket(nLevel);

            if (lb.line == null) {
                lb.line = line;
                lb.scale = line.fixed ? 1 : mLineScale;
                lb.setExtents(-16, Tile.SIZE + 16);
            }
            //if (lb.line == null) {
            //    lb.line = line;
            //    float w = line.width;
            //    if (!line.fixed)
            //        w *= mLineScale;
            //    lb.scale = w;
            //}

            lb.addLine(mElement);
        }
    }

    /* slower to load (requires tesselation) and uses
     * more memory but should be faster to render */
    public static boolean USE_MESH_POLY = false;

    @Override
    public void renderArea(AreaStyle area, int level) {
        /* dont add faded out polygon layers */
        if (mTile.zoomLevel < area.fadeScale)
            return;

        int nLevel = mCurBucket + level;

        mTileLayer.callThemeHooks(mTile, mBuckets, mElement, area, nLevel);

        if (USE_MESH_POLY || area.mesh) {
            MeshBucket mb = mBuckets.getMeshBucket(nLevel);
            mb.area = area;
            mb.addMesh(mElement);
        } else {
            PolygonBucket pb = mBuckets.getPolygonBucket(nLevel);
            pb.area = area;
            pb.addPolygon(mElement.points, mElement.index);
        }
    }

    @Override
    public void renderSymbol(SymbolStyle symbol) {
        mTileLayer.callThemeHooks(mTile, mBuckets, mElement, symbol, 0);
    }

    @Override
    public void renderExtrusion(ExtrusionStyle extrusion, int level) {
        mTileLayer.callThemeHooks(mTile, mBuckets, mElement, extrusion, level);
    }

    @Override
    public void renderCircle(CircleStyle circle, int level) {
        int nLevel = mCurBucket + level;
        CircleBucket cb = mBuckets.getCircleBucket(nLevel);
        cb.circle = circle;
        cb.addCircle(mElement);
    }

    @Override
    public void renderText(TextStyle text) {
        mTileLayer.callThemeHooks(mTile, mBuckets, mElement, text, 0);
    }
}
