/*
 * Copyright 2017 usrusr
 * Copyright 2017 oruxman
 * Copyright 2024 Sublimis
 * Copyright 2024 jhotadhari
 * Copyright 2025 devemux86
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
package org.oscim.tiling.source.hills;

import org.mapsforge.core.graphics.HillshadingBitmap;
import org.mapsforge.core.model.Rectangle;
import org.mapsforge.core.util.MercatorProjection;
import org.mapsforge.map.layer.hills.HillsRenderConfig;
import org.mapsforge.map.layer.hills.MemoryCachingHgtReaderTileSource;
import org.mapsforge.map.layer.renderer.HillshadingContainer;
import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Canvas;
import org.oscim.core.Point;
import org.oscim.core.Tile;
import org.oscim.layers.tile.MapTile;
import org.oscim.tiling.ITileCache;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.QueryResult;
import org.oscim.tiling.source.ITileDecoder;
import org.oscim.utils.IOUtils;

import java.io.*;
import java.util.logging.Logger;

public class HillshadingTileDataSource implements ITileDataSource {
    private static final Logger log = Logger.getLogger(HillshadingTileDataSource.class.getName());

    private final HillshadingTileSource mTileSource;
    private final ITileDecoder mTileDecoder;
    private final HillsRenderConfig mHillsConfig;

    public HillshadingTileDataSource(HillshadingTileSource tileSource, ITileDecoder tileDecoder) {
        mTileSource = tileSource;
        mTileDecoder = tileDecoder;

        MemoryCachingHgtReaderTileSource shadeTileSource = new MemoryCachingHgtReaderTileSource(tileSource.mDemFolder, tileSource.mAlgorithm, tileSource.mGraphicFactory);
        mHillsConfig = new HillsRenderConfig(shadeTileSource);
        mHillsConfig.indexOnThread();
    }

    @Override
    public void query(final MapTile tile, final ITileDataSink sink) {
        ITileCache cache = mTileSource.tileCache;

        // Out of zoom bounds, load nothing
        byte zoomLevel = tile.zoomLevel;
        if (tile.zoomLevel > mTileSource.getZoomLevelMax() || zoomLevel < mTileSource.getZoomLevelMin()) {
            sink.completed(QueryResult.SUCCESS);
            return;
        }

        // Try to load from cache
        if (mTileSource.tileCache != null) {
            ITileCache.TileReader c = cache.getTile(tile);
            if (c != null) {
                InputStream is = c.getInputStream();
                try {
                    if (mTileDecoder.decode(tile, sink, is)) {
                        sink.completed(QueryResult.SUCCESS);
                        return;
                    }
                } catch (IOException e) {
                    log.fine(tile + " Cache read: " + e);
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }
        }

        // Create a new hillshading tile
        QueryResult res = createTile(tile, sink, mTileSource.tileCache);
        sink.completed(res);
    }

    @Override
    public void dispose() {
    }

    @Override
    public void cancel() {
    }

    /**
     * Create new hillshading tile from hgt files and set the sink.
     * Mostly copy of org.mapsforge.map.rendertheme.renderinstruction.Hillshading render method.
     *
     * @See https://github.com/mapsforge/mapsforge/blob/e45c41dc46cdfdf0770d687adee8f6d051511f5e/mapsforge-map/src/main/java/org/mapsforge/map/rendertheme/renderinstruction/Hillshading.java#L57
     */
    private QueryResult createTile(final MapTile tile, final ITileDataSink sink, ITileCache cache) {
        QueryResult res = QueryResult.FAILED;
        ITileCache.TileWriter cacheWriter = null;
        try {
            float effectiveMagnitude = Math.min(Math.max(0f, mTileSource.mMagnitude * mHillsConfig.getMagnitudeScaleFactor()), 255f) / 255f;

            Point origin = tile.getOrigin();
            double maptileTopLat = MercatorProjection.pixelYToLatitude((long) origin.y, tile.mapSize);
            double maptileLeftLng = MercatorProjection.pixelXToLongitude((long) origin.x, tile.mapSize);

            double maptileBottomLat = MercatorProjection.pixelYToLatitude((long) origin.y + Tile.SIZE, tile.mapSize);
            double maptileRightLng = MercatorProjection.pixelXToLongitude((long) origin.x + Tile.SIZE, tile.mapSize);

            double mapTileLatDegrees = maptileTopLat - maptileBottomLat;
            double mapTileLngDegrees = maptileRightLng - maptileLeftLng;
            double pxPerLat = (Tile.SIZE / mapTileLatDegrees);
            double pxPerLng = (Tile.SIZE / mapTileLngDegrees);

            if (maptileRightLng < maptileLeftLng)
                maptileRightLng += tile.mapSize;

            // Init tile bitmap to hold all the shaded parts
            Bitmap tileBitmap = CanvasAdapter.newBitmap(Tile.SIZE, Tile.SIZE, 0);
            Canvas canvas = CanvasAdapter.newCanvas();
            canvas.setBitmap(tileBitmap);

            int shadingLngStep = 1;
            int shadingLatStep = 1;
            for (int shadingLeftLng = (int) Math.floor(maptileLeftLng); shadingLeftLng <= maptileRightLng; shadingLeftLng += shadingLngStep) {
                for (int shadingBottomLat = (int) Math.floor(maptileBottomLat); shadingBottomLat <= maptileTopLat; shadingBottomLat += shadingLatStep) {
                    int shadingRightLng = shadingLeftLng + 1;
                    int shadingTopLat = shadingBottomLat + 1;

                    HillshadingBitmap shadingTile;
                    try {
                        shadingTile = mHillsConfig.getShadingTile(shadingBottomLat, shadingLeftLng, tile.zoomLevel, pxPerLat, pxPerLng, mTileSource.mColor);
                    } catch (Exception e) {
                        log.fine(e.getMessage());
                        continue;
                    }
                    if (shadingTile == null)
                        continue;

                    int padding = shadingTile.getPadding();
                    int shadingInnerWidth = shadingTile.getWidth() - 2 * padding;
                    int shadingInnerHeight = shadingTile.getHeight() - 2 * padding;

                    // shading tile subset if it fully fits inside map tile
                    double shadingSubrectTop = padding;
                    double shadingSubrectLeft = padding;
                    double shadingSubrectRight = shadingSubrectLeft + shadingInnerWidth;
                    double shadingSubrectBottom = shadingSubrectTop + shadingInnerHeight;

                    // map tile subset if it fully fits inside shading tile
                    double maptileSubrectLeft = 0;
                    double maptileSubrectTop = 0;
                    double maptileSubrectRight = Tile.SIZE;
                    double maptileSubrectBottom = Tile.SIZE;

                    // find the intersection between map tile and shading tile in earth coordinates and determine the pixel
                    if (shadingTopLat > maptileTopLat) { // map tile ends in shading tile
                        shadingSubrectTop = padding + shadingInnerHeight * ((shadingTopLat - maptileTopLat) / shadingLatStep);
                    } else if (maptileTopLat > shadingTopLat) {
                        maptileSubrectTop = MercatorProjection.latitudeToPixelY(shadingTopLat, tile.mapSize) - origin.y;
                    }
                    if (shadingBottomLat < maptileBottomLat) { // map tile ends in shading tile
                        shadingSubrectBottom = padding + shadingInnerHeight - shadingInnerHeight * ((maptileBottomLat - shadingBottomLat) / shadingLatStep);
                    } else if (maptileBottomLat < shadingBottomLat) {
                        maptileSubrectBottom = MercatorProjection.latitudeToPixelY(shadingBottomLat, tile.mapSize) - origin.y;
                    }
                    if (shadingLeftLng < maptileLeftLng) { // map tile ends in shading tile
                        shadingSubrectLeft = padding + shadingInnerWidth * ((maptileLeftLng - shadingLeftLng) / shadingLngStep);
                    } else if (maptileLeftLng < shadingLeftLng) {
                        maptileSubrectLeft = MercatorProjection.longitudeToPixelX(shadingLeftLng, tile.mapSize) - origin.x;
                    }
                    if (shadingRightLng > maptileRightLng) { // map tile ends in shading tile
                        shadingSubrectRight = padding + shadingInnerWidth - shadingInnerWidth * ((shadingRightLng - maptileRightLng) / shadingLngStep);
                    } else if (maptileRightLng > shadingRightLng) {
                        maptileSubrectRight = MercatorProjection.longitudeToPixelX(shadingRightLng, tile.mapSize) - origin.x;
                    }

                    Rectangle hillsRect = new Rectangle(shadingSubrectLeft, shadingSubrectTop, shadingSubrectRight, shadingSubrectBottom);
                    Rectangle maptileRect = new Rectangle(maptileSubrectLeft, maptileSubrectTop, maptileSubrectRight, maptileSubrectBottom);
                    HillshadingContainer hillShape = new HillshadingContainer(shadingTile, effectiveMagnitude, mTileSource.mColor, hillsRect, maptileRect);

                    // Render ShapeContainer to a Mapsforge bitmap
                    org.mapsforge.core.graphics.Bitmap mapsforgeBitmap = mTileSource.mGraphicFactory.createBitmap(Tile.SIZE, Tile.SIZE, true);
                    org.mapsforge.core.graphics.Canvas mapsforgeCanvas = mTileSource.mGraphicFactory.createCanvas();
                    mapsforgeCanvas.setBitmap(mapsforgeBitmap);
                    mapsforgeCanvas.shadeBitmap(hillShape.bitmap, hillShape.hillsRect, hillShape.tileRect, hillShape.magnitude, hillShape.color, true);

                    // Convert Mapsforge bitmap to VTM bitmap
                    Bitmap bitmap = bitmapMapsforgeToVtm(mapsforgeBitmap);

                    // Draw shaded bitmap on the tile bitmap
                    canvas.drawBitmap(bitmap, 0, 0);
                }
            }

            // Set tile bitmap to sink
            if (!tileBitmap.isValid()) {
                log.fine(tile + " invalid bitmap");
            } else {
                // Set sink
                sink.setTileImage(tileBitmap);

                // Write to cache
                if (cache != null) {
                    cacheWriter = cache.writeTile(tile);
                    OutputStream outputStream = cacheWriter.getOutputStream();
                    try {
                        byte[] pngBytes = tileBitmap.getPngEncodedData();
                        outputStream.write(pngBytes);
                    } catch (IOException e) {
                        log.severe(e.toString());
                    }
                }
                res = QueryResult.SUCCESS;
            }
        } catch (Throwable t) {
            log.severe(t.toString());
        } finally {
            if (cacheWriter != null) {
                cacheWriter.complete(res == QueryResult.SUCCESS);
            }
        }
        return res;
    }

    /**
     * Converts a Mapsforge bitmap to a VTM bitmap.
     */
    private Bitmap bitmapMapsforgeToVtm(org.mapsforge.core.graphics.Bitmap mapsforgeBitmap) throws IOException {
        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            mapsforgeBitmap.compress(outputStream);
            return CanvasAdapter.decodeBitmap(new ByteArrayInputStream(outputStream.toByteArray()));
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }
}
