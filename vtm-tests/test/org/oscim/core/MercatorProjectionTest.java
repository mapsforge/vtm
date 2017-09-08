/*
 * Copyright 2010, 2011, 2012, 2013 mapsforge.org
 * Copyright 2017 devemux86
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
package org.oscim.core;

import org.junit.Assert;
import org.junit.Test;

public class MercatorProjectionTest {
    private static final int[] TILE_SIZES = {256, 128, 376, 512, 100};
    private static final int ZOOM_LEVEL_MAX = 30;
    private static final int ZOOM_LEVEL_MIN = 0;

    private static void verifyInvalidGetMapSize(byte zoomLevel) {
        try {
            MercatorProjection.getMapSize(zoomLevel);
            Assert.fail("zoomLevel: " + zoomLevel);
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
    }

    private static void verifyInvalidPixelXToLongitude(double pixelX, byte zoomLevel) {
        try {
            MercatorProjection.pixelXToLongitude(pixelX, MercatorProjection.getMapSize(zoomLevel));
            Assert.fail("pixelX: " + pixelX + ", zoomLevel: " + zoomLevel);
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
    }

    private static void verifyInvalidPixelYToLatitude(double pixelY, byte zoomLevel) {
        try {
            MercatorProjection.pixelYToLatitude(pixelY, MercatorProjection.getMapSize(zoomLevel));
            Assert.fail("pixelY: " + pixelY + ", zoomLevel: " + zoomLevel);
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void getMapSizeTest() {
        for (int tileSize : TILE_SIZES) {
            Tile.SIZE = tileSize;
            for (byte zoomLevel = ZOOM_LEVEL_MIN; zoomLevel <= ZOOM_LEVEL_MAX; ++zoomLevel) {
                long factor = Math.round(MercatorProjection.zoomLevelToScale(zoomLevel));
                Assert.assertEquals(Tile.SIZE * factor, MercatorProjection.getMapSize(zoomLevel));
                Assert.assertEquals(MercatorProjection.getMapSizeWithScale(MercatorProjection.zoomLevelToScale(zoomLevel)),
                        MercatorProjection.getMapSize(zoomLevel));
            }
            verifyInvalidGetMapSize((byte) -1);
        }
    }

    @Test
    public void latitudeToPixelYTest() {
        for (int tileSize : TILE_SIZES) {
            Tile.SIZE = tileSize;
            for (byte zoomLevel = ZOOM_LEVEL_MIN; zoomLevel <= ZOOM_LEVEL_MAX; ++zoomLevel) {
                long mapSize = MercatorProjection.getMapSize(zoomLevel);
                double pixelY = MercatorProjection.latitudeToPixelY(MercatorProjection.LATITUDE_MAX, mapSize);
                Assert.assertEquals(0, pixelY, 0);

                pixelY = MercatorProjection.latitudeToPixelYWithScale(MercatorProjection.LATITUDE_MAX, MercatorProjection.zoomLevelToScale(zoomLevel));
                Assert.assertEquals(0, pixelY, 0);

                pixelY = MercatorProjection.latitudeToPixelY(0, mapSize);
                Assert.assertEquals((float) mapSize / 2, pixelY, 0);
                pixelY = MercatorProjection.latitudeToPixelYWithScale(0, MercatorProjection.zoomLevelToScale(zoomLevel));
                Assert.assertEquals((float) mapSize / 2, pixelY, 0);

                pixelY = MercatorProjection.latitudeToPixelY(MercatorProjection.LATITUDE_MIN, mapSize);
                Assert.assertEquals(mapSize, pixelY, 0);
                pixelY = MercatorProjection.latitudeToPixelYWithScale(MercatorProjection.LATITUDE_MIN, MercatorProjection.zoomLevelToScale(zoomLevel));
                Assert.assertEquals(mapSize, pixelY, 0);
            }
        }
    }

    @Test
    public void latitudeToTileYTest() {
        for (byte zoomLevel = ZOOM_LEVEL_MIN; zoomLevel <= ZOOM_LEVEL_MAX; ++zoomLevel) {
            long tileY = MercatorProjection.latitudeToTileY(MercatorProjection.LATITUDE_MAX, zoomLevel);
            Assert.assertEquals(0, tileY);
            tileY = MercatorProjection.latitudeToTileYWithScale(MercatorProjection.LATITUDE_MAX, MercatorProjection.zoomLevelToScale(zoomLevel));
            Assert.assertEquals(0, tileY);

            tileY = MercatorProjection.latitudeToTileY(MercatorProjection.LATITUDE_MIN, zoomLevel);
            Assert.assertEquals(Tile.getMaxTileNumber(zoomLevel), tileY);
            tileY = MercatorProjection.latitudeToTileYWithScale(MercatorProjection.LATITUDE_MIN, MercatorProjection.zoomLevelToScale(zoomLevel));
            Assert.assertEquals(Tile.getMaxTileNumber(zoomLevel), tileY);
        }
    }

    @Test
    public void longitudeToPixelXTest() {
        for (int tileSize : TILE_SIZES) {
            Tile.SIZE = tileSize;
            for (byte zoomLevel = ZOOM_LEVEL_MIN; zoomLevel <= ZOOM_LEVEL_MAX; ++zoomLevel) {
                long mapSize = MercatorProjection.getMapSize(zoomLevel);
                double pixelX = MercatorProjection.longitudeToPixelX(MercatorProjection.LONGITUDE_MIN, mapSize);
                Assert.assertEquals(0, pixelX, 0);
                pixelX = MercatorProjection.longitudeToPixelXWithScale(MercatorProjection.LONGITUDE_MIN, MercatorProjection.zoomLevelToScale(zoomLevel));
                Assert.assertEquals(0, pixelX, 0);

                pixelX = MercatorProjection.longitudeToPixelX(0, mapSize);
                Assert.assertEquals((float) mapSize / 2, pixelX, 0);
                mapSize = MercatorProjection.getMapSizeWithScale(MercatorProjection.zoomLevelToScale(zoomLevel));
                pixelX = MercatorProjection.longitudeToPixelXWithScale(0, MercatorProjection.zoomLevelToScale(zoomLevel));
                Assert.assertEquals((float) mapSize / 2, pixelX, 0);

                pixelX = MercatorProjection.longitudeToPixelX(MercatorProjection.LONGITUDE_MAX, mapSize);
                Assert.assertEquals(mapSize, pixelX, 0);
                pixelX = MercatorProjection.longitudeToPixelXWithScale(MercatorProjection.LONGITUDE_MAX, MercatorProjection.zoomLevelToScale(zoomLevel));
                Assert.assertEquals(mapSize, pixelX, 0);
            }
        }
    }

    @Test
    public void longitudeToTileXTest() {
        for (byte zoomLevel = ZOOM_LEVEL_MIN; zoomLevel <= ZOOM_LEVEL_MAX; ++zoomLevel) {
            long tileX = MercatorProjection.longitudeToTileX(MercatorProjection.LONGITUDE_MIN, zoomLevel);
            Assert.assertEquals(0, tileX);
            tileX = MercatorProjection.longitudeToTileXWithScale(MercatorProjection.LONGITUDE_MIN, MercatorProjection.zoomLevelToScale(zoomLevel));
            Assert.assertEquals(0, tileX);

            tileX = MercatorProjection.longitudeToTileX(MercatorProjection.LONGITUDE_MAX, zoomLevel);
            Assert.assertEquals(Tile.getMaxTileNumber(zoomLevel), tileX);
            tileX = MercatorProjection.longitudeToTileXWithScale(MercatorProjection.LONGITUDE_MAX, MercatorProjection.zoomLevelToScale(zoomLevel));
            Assert.assertEquals(Tile.getMaxTileNumber(zoomLevel), tileX);
        }
    }

    @Test
    public void metersToPixelTest() {
        for (int tileSize : TILE_SIZES) {
            Tile.SIZE = tileSize;
            Assert.assertTrue(MercatorProjection.metersToPixels(10, 10.0, MercatorProjection.getMapSize((byte) 1)) < 1);
            Assert.assertTrue(MercatorProjection.metersToPixels((int) (40 * 10e7), 10.0, MercatorProjection.getMapSize((byte) 1)) > 1);
            Assert.assertTrue(MercatorProjection.metersToPixels(10, 10.0, MercatorProjection.getMapSize((byte) 20)) > 1);
            Assert.assertTrue(MercatorProjection.metersToPixels(10, 89.0, MercatorProjection.getMapSize((byte) 1)) < 1);
            Assert.assertTrue(MercatorProjection.metersToPixels((int) (40 * 10e3), 50, MercatorProjection.getMapSize((byte) 10)) > 1);
        }
    }

    @Test
    public void pixelXToLongitudeTest() {
        for (int tileSize : TILE_SIZES) {
            Tile.SIZE = tileSize;
            for (byte zoomLevel = ZOOM_LEVEL_MIN; zoomLevel <= ZOOM_LEVEL_MAX; ++zoomLevel) {
                long mapSize = MercatorProjection.getMapSize(zoomLevel);
                double longitude = MercatorProjection.pixelXToLongitude(0, mapSize);
                Assert.assertEquals(MercatorProjection.LONGITUDE_MIN, longitude, 0);
                longitude = MercatorProjection.pixelXToLongitudeWithScale(0, MercatorProjection.zoomLevelToScale(zoomLevel));
                Assert.assertEquals(MercatorProjection.LONGITUDE_MIN, longitude, 0);

                longitude = MercatorProjection.pixelXToLongitude((float) mapSize / 2, mapSize);
                Assert.assertEquals(0, longitude, 0);
                mapSize = MercatorProjection.getMapSizeWithScale(MercatorProjection.zoomLevelToScale(zoomLevel));
                longitude = MercatorProjection.pixelXToLongitudeWithScale((float) mapSize / 2, MercatorProjection.zoomLevelToScale(zoomLevel));
                Assert.assertEquals(0, longitude, 0);

                longitude = MercatorProjection.pixelXToLongitude(mapSize, mapSize);
                Assert.assertEquals(MercatorProjection.LONGITUDE_MAX, longitude, 0);
                longitude = MercatorProjection.pixelXToLongitudeWithScale(mapSize, MercatorProjection.zoomLevelToScale(zoomLevel));
                Assert.assertEquals(MercatorProjection.LONGITUDE_MAX, longitude, 0);
            }

            verifyInvalidPixelXToLongitude(-1, (byte) 0);
            verifyInvalidPixelXToLongitude(Tile.SIZE + 1, (byte) 0);
        }
    }

    @Test
    public void pixelXToTileXTest() {
        for (int tileSize : TILE_SIZES) {
            Tile.SIZE = tileSize;
            for (byte zoomLevel = ZOOM_LEVEL_MIN; zoomLevel <= ZOOM_LEVEL_MAX; ++zoomLevel) {
                Assert.assertEquals(0, MercatorProjection.pixelXToTileX(0, zoomLevel));
                Assert.assertEquals(0, MercatorProjection.pixelXToTileXWithScale(0, MercatorProjection.zoomLevelToScale(zoomLevel)));
            }
        }
    }

    @Test
    public void pixelYToLatitudeTest() {
        for (int tileSize : TILE_SIZES) {
            Tile.SIZE = tileSize;
            for (byte zoomLevel = ZOOM_LEVEL_MIN; zoomLevel <= ZOOM_LEVEL_MAX; ++zoomLevel) {
                long mapSize = MercatorProjection.getMapSize(zoomLevel);
                double latitude = MercatorProjection.pixelYToLatitude(0, mapSize);
                Assert.assertEquals(MercatorProjection.LATITUDE_MAX, latitude, 0);
                latitude = MercatorProjection.pixelYToLatitudeWithScale(0, MercatorProjection.zoomLevelToScale(zoomLevel));
                Assert.assertEquals(MercatorProjection.LATITUDE_MAX, latitude, 0);

                latitude = MercatorProjection.pixelYToLatitude((float) mapSize / 2, mapSize);
                Assert.assertEquals(0, latitude, 0);
                mapSize = MercatorProjection.getMapSizeWithScale(MercatorProjection.zoomLevelToScale(zoomLevel));
                latitude = MercatorProjection.pixelYToLatitudeWithScale((float) mapSize / 2, MercatorProjection.zoomLevelToScale(zoomLevel));
                Assert.assertEquals(0, latitude, 0);

                latitude = MercatorProjection.pixelYToLatitude(mapSize, mapSize);
                Assert.assertEquals(MercatorProjection.LATITUDE_MIN, latitude, 0);
                latitude = MercatorProjection.pixelYToLatitudeWithScale(mapSize, MercatorProjection.zoomLevelToScale(zoomLevel));
                Assert.assertEquals(MercatorProjection.LATITUDE_MIN, latitude, 0);
            }

            verifyInvalidPixelYToLatitude(-1, (byte) 0);
            verifyInvalidPixelYToLatitude(Tile.SIZE + 1, (byte) 0);
        }
    }

    @Test
    public void pixelYToTileYTest() {
        for (int tileSize : TILE_SIZES) {
            Tile.SIZE = tileSize;
            for (byte zoomLevel = ZOOM_LEVEL_MIN; zoomLevel <= ZOOM_LEVEL_MAX; ++zoomLevel) {
                Assert.assertEquals(0, MercatorProjection.pixelYToTileY(0, zoomLevel));
                Assert.assertEquals(0, MercatorProjection.pixelYToTileYWithScale(0, MercatorProjection.zoomLevelToScale(zoomLevel)));
            }
        }
    }

    @Test
    public void tileToPixelTest() {
        for (int tileSize : TILE_SIZES) {
            Tile.SIZE = tileSize;
            Assert.assertEquals(0, MercatorProjection.tileToPixel(0));
            Assert.assertEquals(Tile.SIZE, MercatorProjection.tileToPixel(1));
            Assert.assertEquals(Tile.SIZE * 2, MercatorProjection.tileToPixel(2));
        }
    }

    @Test
    public void tileXToLongitudeTest() {
        for (int tileSize : TILE_SIZES) {
            Tile.SIZE = tileSize;
            for (byte zoomLevel = ZOOM_LEVEL_MIN; zoomLevel <= ZOOM_LEVEL_MAX; ++zoomLevel) {
                double longitude = MercatorProjection.tileXToLongitude(0, zoomLevel);
                Assert.assertEquals(MercatorProjection.LONGITUDE_MIN, longitude, 0);
                longitude = MercatorProjection.tileXToLongitudeWithScale(0, MercatorProjection.zoomLevelToScale(zoomLevel));
                Assert.assertEquals(MercatorProjection.LONGITUDE_MIN, longitude, 0);

                long tileX = MercatorProjection.getMapSize(zoomLevel) / Tile.SIZE;
                longitude = MercatorProjection.tileXToLongitude(tileX, zoomLevel);
                Assert.assertEquals(MercatorProjection.LONGITUDE_MAX, longitude, 0);
                tileX = MercatorProjection.getMapSizeWithScale(MercatorProjection.zoomLevelToScale(zoomLevel)) / Tile.SIZE;
                longitude = MercatorProjection.tileXToLongitudeWithScale(tileX, MercatorProjection.zoomLevelToScale(zoomLevel));
                Assert.assertEquals(MercatorProjection.LONGITUDE_MAX, longitude, 0);
            }
        }
    }

    @Test
    public void tileYToLatitudeTest() {
        for (int tileSize : TILE_SIZES) {
            Tile.SIZE = tileSize;
            for (byte zoomLevel = ZOOM_LEVEL_MIN; zoomLevel <= ZOOM_LEVEL_MAX; ++zoomLevel) {
                double latitude = MercatorProjection.tileYToLatitude(0, zoomLevel);
                Assert.assertEquals(MercatorProjection.LATITUDE_MAX, latitude, 0);
                latitude = MercatorProjection.tileYToLatitudeWithScale(0, MercatorProjection.zoomLevelToScale(zoomLevel));
                Assert.assertEquals(MercatorProjection.LATITUDE_MAX, latitude, 0);

                long tileY = MercatorProjection.getMapSize(zoomLevel) / Tile.SIZE;
                latitude = MercatorProjection.tileYToLatitude(tileY, zoomLevel);
                Assert.assertEquals(MercatorProjection.LATITUDE_MIN, latitude, 0);
                tileY = MercatorProjection.getMapSizeWithScale(MercatorProjection.zoomLevelToScale(zoomLevel)) / Tile.SIZE;
                latitude = MercatorProjection.tileYToLatitudeWithScale(tileY, MercatorProjection.zoomLevelToScale(zoomLevel));
                Assert.assertEquals(MercatorProjection.LATITUDE_MIN, latitude, 0);
            }
        }
    }

    @Test
    public void zoomLevelToScaleTest() {
        for (byte zoomLevel = ZOOM_LEVEL_MIN; zoomLevel <= ZOOM_LEVEL_MAX; ++zoomLevel) {
            double scale = MercatorProjection.zoomLevelToScale(zoomLevel);
            Assert.assertEquals(zoomLevel, MercatorProjection.scaleToZoomLevel(scale), 0.0001f);
        }
    }
}
