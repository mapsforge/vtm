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
import org.mapsforge.map.layer.hills.*;
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
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/**
 * Contains code from {@link org.mapsforge.map.rendertheme.renderinstruction.Hillshading}.
 */
public class HillshadingTileDataSource implements ITileDataSource {

    private static final Logger log = Logger.getLogger(HillshadingTileDataSource.class.getName());

    /**
     * Default name prefix for additional reading threads created and used by hill shading. A numbered suffix will be appended.
     */
    private static final String ThreadPoolName = "MapsforgeHillShading";

    private static final int ShadingLatStep = 1;
    private static final int ShadingLonStep = 1;

    private final HillshadingTileSource mTileSource;
    private final ITileDecoder mTileDecoder;
    private final HillsRenderConfig mHillsRenderConfig;

    /**
     * Static thread pool shared by all tasks.
     */
    private static final AtomicReference<HillShadingUtils.HillShadingThreadPool> ThreadPool = new AtomicReference<>(null);

    public HillshadingTileDataSource(HillshadingTileSource tileSource, ITileDecoder tileDecoder) {
        mTileSource = tileSource;
        mTileDecoder = tileDecoder;

        MemoryCachingHgtReaderTileSource shadeTileSource = new MemoryCachingHgtReaderTileSource(tileSource.mDemFolder, tileSource.mAlgorithm, tileSource.mGraphicFactory);
        mHillsRenderConfig = new HillsRenderConfig(shadeTileSource);
        mHillsRenderConfig.indexOnThread();
    }

    @Override
    public void query(MapTile tile, ITileDataSink sink) {
        // Out of zoom bounds, load nothing
        byte zoomLevel = tile.zoomLevel;
        if (zoomLevel > mTileSource.getZoomLevelMax() || zoomLevel < mTileSource.getZoomLevelMin()) {
            sink.completed(QueryResult.SUCCESS);
            return;
        }

        ITileCache cache = mTileSource.tileCache;

        // Try to load from cache
        if (cache != null) {
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

        // Create a new hillshading tile and set the sink
        createTile(tile, sink, cache);
    }

    @Override
    public void dispose() {
    }

    @Override
    public void cancel() {
    }

    /**
     * Create new hillshading tile from hgt files and set the sink.
     */
    private void createTile(MapTile tile, ITileDataSink sink, ITileCache cache) {
        QueryResult res = QueryResult.FAILED;
        ITileCache.TileWriter cacheWriter = null;
        try {
            final byte zoomLevel = tile.zoomLevel >= 0 ? tile.zoomLevel : 0;

            if (checkZoomLevelCoarse(zoomLevel, mHillsRenderConfig)) {
                // Init tile bitmap to hold all the shaded parts
                Bitmap tileBitmap = CanvasAdapter.newBitmap(Tile.SIZE, Tile.SIZE, 0);
                Canvas canvas = CanvasAdapter.newCanvas();
                canvas.setBitmap(tileBitmap);

                final Point origin = tile.getOrigin();

                final double maptileLeftLon = MercatorProjection.pixelXToLongitude(origin.x, tile.mapSize);
                double maptileRightLon = MercatorProjection.pixelXToLongitude(origin.x + Tile.SIZE, tile.mapSize);
                if (maptileRightLon < maptileLeftLon)
                    maptileRightLon += tile.mapSize;

                final double maptileTopLat = MercatorProjection.pixelYToLatitude(origin.y, tile.mapSize);
                final double maptileBottomLat = MercatorProjection.pixelYToLatitude(origin.y + Tile.SIZE, tile.mapSize);

                final float effectiveMagnitude = Math.min(Math.max(0f, mTileSource.mMagnitude * mHillsRenderConfig.getMagnitudeScaleFactor()), 255f) / 255f;
                final int effectiveColor = getEffectiveColor(mHillsRenderConfig);

                createThreadPoolsMaybe();

                final Deque<HillShadingUtils.SilentFutureTask> deque = new ArrayDeque<>();

                for (int shadingLeftLon = (int) Math.floor(maptileLeftLon); shadingLeftLon <= maptileRightLon; shadingLeftLon += ShadingLonStep) {
                    final HillShadingUtils.SilentFutureTask code = renderLatStrip(shadingLeftLon, zoomLevel, tile, maptileBottomLat, maptileTopLat, maptileLeftLon, maptileRightLon, effectiveMagnitude, effectiveColor, canvas);
                    deque.addLast(code);
                }

                while (!deque.isEmpty()) {
                    deque.pollFirst().get();
                }

                if (!tileBitmap.isValid()) {
                    log.fine(tile + " invalid bitmap");
                    return;
                }

                // Set tile bitmap to sink
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
                    } finally {
                        IOUtils.closeQuietly(outputStream);
                    }
                }

                res = QueryResult.SUCCESS;
            }
        } catch (Throwable t) {
            log.severe(t.toString());
        } finally {
            boolean ok = (res == QueryResult.SUCCESS);

            if (cacheWriter != null)
                cacheWriter.complete(ok);

            sink.completed(res);
        }
    }

    private int getEffectiveColor(HillsRenderConfig hillsRenderConfig) {
        int retVal = hillsRenderConfig.getColor();

        if (retVal == 0) {
            retVal = mTileSource.mColor;
        }

        return retVal;
    }

    private HillShadingUtils.SilentFutureTask renderLatStrip(final int shadingLeftLon, final byte zoomLevel, final MapTile tile, final double maptileBottomLat, final double maptileTopLat, final double maptileLeftLon, final double maptileRightLon, final float effectiveMagnitude, final int effectiveColor, final Canvas canvas) {
        Callable<Boolean> runnable = new Callable<Boolean>() {
            public Boolean call() {
                try {
                    final int shadingRightLon = shadingLeftLon + ShadingLonStep;
                    final double leftX = MercatorProjection.longitudeToPixelX(shadingLeftLon, tile.mapSize);
                    final double rightX = MercatorProjection.longitudeToPixelX(shadingRightLon, tile.mapSize);
                    final double pxPerLon = (rightX - leftX) / ShadingLonStep;

                    for (int shadingBottomLat = (int) Math.floor(maptileBottomLat); shadingBottomLat <= maptileTopLat; shadingBottomLat += ShadingLatStep) {
                        final int shadingTopLat = shadingBottomLat + ShadingLatStep;

                        final double topY = MercatorProjection.latitudeToPixelY(shadingTopLat, tile.mapSize);
                        final double bottomY = MercatorProjection.latitudeToPixelY(shadingBottomLat, tile.mapSize);
                        final double pxPerLat = (bottomY - topY) / ShadingLatStep;

                        HillshadingBitmap shadingTile = null;

                        if (checkZoomLevelFine(zoomLevel, mHillsRenderConfig, shadingBottomLat, shadingLeftLon)) {
                            try {
                                shadingTile = mHillsRenderConfig.getShadingTile(shadingBottomLat, shadingLeftLon, zoomLevel, pxPerLat, pxPerLon, effectiveColor);
                            } catch (Exception e) {
                                log.severe(e.toString());
                            }
                        }

                        if (shadingTile == null) {
                            continue;
                        }

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

                        final Point origin = tile.getOrigin();

                        // find the intersection between map tile and shading tile in earth coordinates and determine the pixel

                        if (shadingBottomLat > maptileBottomLat) {
                            // Shading tile ends in map tile
                            maptileSubrectBottom = Math.round(MercatorProjection.latitudeToPixelY(shadingBottomLat, tile.mapSize)) - origin.y;
                            mergeNeighbor(shadingTile, padding, mHillsRenderConfig, HillshadingBitmap.Border.SOUTH, shadingBottomLat, shadingLeftLon, zoomLevel, pxPerLat, pxPerLon, effectiveColor);
                        } else if (shadingBottomLat < maptileBottomLat) {
                            // Map tile ends in shading tile
                            shadingSubrectBottom -= shadingInnerHeight * ((maptileBottomLat - shadingBottomLat) / ShadingLatStep);
                        }

                        if (shadingTopLat < maptileTopLat) {
                            // Shading tile ends in map tile
                            maptileSubrectTop = Math.round(MercatorProjection.latitudeToPixelY(shadingTopLat, tile.mapSize)) - origin.y;
                            mergeNeighbor(shadingTile, padding, mHillsRenderConfig, HillshadingBitmap.Border.NORTH, shadingBottomLat, shadingLeftLon, zoomLevel, pxPerLat, pxPerLon, effectiveColor);
                        } else if (shadingTopLat > maptileTopLat) {
                            // Map tile ends in shading tile
                            shadingSubrectTop += shadingInnerHeight * ((shadingTopLat - maptileTopLat) / ShadingLatStep);
                        }

                        if (shadingLeftLon > maptileLeftLon) {
                            // Shading tile ends in map tile
                            maptileSubrectLeft = Math.round(MercatorProjection.longitudeToPixelX(shadingLeftLon, tile.mapSize)) - origin.x;
                            mergeNeighbor(shadingTile, padding, mHillsRenderConfig, HillshadingBitmap.Border.WEST, shadingBottomLat, shadingLeftLon, zoomLevel, pxPerLat, pxPerLon, effectiveColor);
                        } else if (shadingLeftLon < maptileLeftLon) {
                            // Map tile ends in shading tile
                            shadingSubrectLeft += shadingInnerWidth * ((maptileLeftLon - shadingLeftLon) / ShadingLonStep);
                        }

                        if (shadingRightLon < maptileRightLon) {
                            // Shading tile ends in map tile
                            maptileSubrectRight = Math.round(MercatorProjection.longitudeToPixelX(shadingRightLon, tile.mapSize)) - origin.x;
                            mergeNeighbor(shadingTile, padding, mHillsRenderConfig, HillshadingBitmap.Border.EAST, shadingBottomLat, shadingLeftLon, zoomLevel, pxPerLat, pxPerLon, effectiveColor);
                        } else if (shadingRightLon > maptileRightLon) {
                            // Map tile ends in shading tile
                            shadingSubrectRight -= shadingInnerWidth * ((shadingRightLon - maptileRightLon) / ShadingLonStep);
                        }

                        final Rectangle hillsRect = new Rectangle(shadingSubrectLeft, shadingSubrectTop, shadingSubrectRight, shadingSubrectBottom);
                        final Rectangle maptileRect = new Rectangle(maptileSubrectLeft, maptileSubrectTop, maptileSubrectRight, maptileSubrectBottom);
                        final HillshadingContainer hillShape = new HillshadingContainer(shadingTile, effectiveMagnitude, effectiveColor, hillsRect, maptileRect);

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
                } catch (Throwable t) {
                    log.severe(t.toString());
                }
                return true;
            }
        };

        final HillShadingUtils.SilentFutureTask code = new HillShadingUtils.SilentFutureTask(runnable);

        postToThreadPoolOrRun(code);

        return code;
    }

    private static void postToThreadPoolOrRun(final Runnable code) {
        final HillShadingUtils.HillShadingThreadPool threadPool = ThreadPool.get();

        if (threadPool != null) {
            threadPool.executeOrRun(code);
        }
    }

    private static void createThreadPoolsMaybe() {
        final AtomicReference<HillShadingUtils.HillShadingThreadPool> threadPoolReference = ThreadPool;

        if (threadPoolReference.get() == null) {
            synchronized (threadPoolReference) {
                if (threadPoolReference.get() == null) {
                    threadPoolReference.set(createThreadPool());
                }
            }
        }
    }

    private static HillShadingUtils.HillShadingThreadPool createThreadPool() {
        final int threadCount = AThreadedHillShading.ReadingThreadsCountDefault;
        final int queueSize = Integer.MAX_VALUE;
        return new HillShadingUtils.HillShadingThreadPool(threadCount, threadCount, queueSize, 5, ThreadPoolName).start();
    }

    private boolean checkZoomLevelCoarse(int zoomLevel, HillsRenderConfig hillsRenderConfig) {
        boolean retVal = true;

        if (hillsRenderConfig.isAdaptiveZoomEnabled()) {
            // Pass, wide zoom range algorithms will later use finer zoom level support check
        } else {
            if (zoomLevel > mTileSource.getZoomLevelMax()) {
                retVal = false;
            } else if (zoomLevel < mTileSource.getZoomLevelMin()) {
                retVal = false;
            }
        }

        return retVal;
    }

    private boolean checkZoomLevelFine(int zoomLevel, HillsRenderConfig hillsRenderConfig, int shadingBottomLat, int shadingLeftLon) {
        boolean retVal = true;

        if (hillsRenderConfig.isAdaptiveZoomEnabled()) {
            retVal = hillsRenderConfig.isZoomLevelSupported(zoomLevel, shadingBottomLat, shadingLeftLon);
        }

        return retVal;
    }

    private HillshadingBitmap getNeighbor(HillsRenderConfig hillsRenderConfig, HillshadingBitmap.Border border, int shadingBottomLat, int shadingLeftLon, int zoomLevel, double pxPerLat, double pxPerLon, int effectiveColor) {

        HillshadingBitmap neighbor = null;
        try {
            switch (border) {
                case NORTH:
                    neighbor = hillsRenderConfig.getShadingTile(shadingBottomLat + ShadingLatStep, shadingLeftLon, zoomLevel, pxPerLat, pxPerLon, effectiveColor);
                    break;
                case SOUTH:
                    neighbor = hillsRenderConfig.getShadingTile(shadingBottomLat - ShadingLatStep, shadingLeftLon, zoomLevel, pxPerLat, pxPerLon, effectiveColor);
                    break;
                case EAST:
                    neighbor = hillsRenderConfig.getShadingTile(shadingBottomLat, shadingLeftLon + ShadingLonStep, zoomLevel, pxPerLat, pxPerLon, effectiveColor);
                    break;
                case WEST:
                    neighbor = hillsRenderConfig.getShadingTile(shadingBottomLat, shadingLeftLon - ShadingLonStep, zoomLevel, pxPerLat, pxPerLon, effectiveColor);
                    break;
            }
        } catch (Exception e) {
            log.severe(e.toString());
        }

        return neighbor;
    }

    private void mergePaddingOnBitmap(HillshadingBitmap center, HillshadingBitmap neighbor, HillshadingBitmap.Border border, int padding) {
        if (neighbor != null && padding > 0) {
            final org.mapsforge.core.graphics.Canvas copyCanvas = mTileSource.mGraphicFactory.createCanvas();

            HgtCache.mergeSameSized(center, neighbor, border, padding, copyCanvas);
        }
    }

    private void mergeNeighbor(HillshadingBitmap monoBitmap, int padding, HillsRenderConfig hillsRenderConfig, HillshadingBitmap.Border border, int shadingBottomLat, int shadingLeftLon, int zoomLevel, double pxPerLat, double pxPerLon, int effectiveColor) {
        if (monoBitmap != null && padding > 0) {
            final HillshadingBitmap neighbor = getNeighbor(hillsRenderConfig, border, shadingBottomLat, shadingLeftLon, zoomLevel, pxPerLat, pxPerLon, effectiveColor);
            mergePaddingOnBitmap(monoBitmap, neighbor, border, padding);
        }
    }

    /**
     * Converts a Mapsforge bitmap to a VTM bitmap.
     */
    private static Bitmap bitmapMapsforgeToVtm(org.mapsforge.core.graphics.Bitmap mapsforgeBitmap) throws IOException {
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
