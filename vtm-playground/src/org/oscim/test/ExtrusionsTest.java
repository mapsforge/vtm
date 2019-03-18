/*
 * Copyright 2019 Gustl22
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
package org.oscim.test;

import org.oscim.core.MapElement;
import org.oscim.core.Tag;
import org.oscim.core.Tile;
import org.oscim.gdx.GdxMapApp;
import org.oscim.layers.TileGridLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.buildings.S3DBLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.test.tiling.source.TestTileSource;
import org.oscim.theme.VtmThemes;

public class ExtrusionsTest extends GdxMapApp {

    enum GroundShape {
        HEXAGON, RECTANGLE, SHAPE_L, SHAPE_O, SHAPE_T, SHAPE_U, SHAPE_V, SHAPE_X, SHAPE_Z, TEST
    }

    // Default ground shape
    public GroundShape mGroundShape = GroundShape.RECTANGLE;

    // Default roof shape
    public Tag mRoofShape = new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_GABLED);

    /**
     * Iterate through ground or roof shapes.
     * 0: default ground and roof
     * 1: default ground, all roofs
     * 2: default roof, all grounds
     * 3: iterate all
     */
    public int mMode = 1;

    private Tag[] mTags = {
            new Tag(Tag.KEY_BUILDING, Tag.VALUE_YES),
            new Tag(Tag.KEY_BUILDING_LEVELS, "2"),
            new Tag(Tag.KEY_ROOF_LEVELS, "2"),
            new Tag(Tag.KEY_ROOF_COLOR, "#99ffff"),
            new Tag(Tag.KEY_BUILDING_COLOR, "white")
    };

    private Tag[] mRoofShapes = {
            new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_FLAT),
            new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_GABLED),
            new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_PYRAMIDAL),
            new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_DOME),
            new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_GAMBREL),
            new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_HALF_HIPPED),
            new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_HIPPED),
            new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_MANSARD),
            new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_ONION),
            new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_ROUND),
            new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_SALTBOX),
            new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_SKILLION)
    };

    private void addExtrusions(TestTileSource tileSource) {
        Tag[] roofShapes;
        GroundShape[] groundShapes;
        if (mMode == 0) {
            roofShapes = new Tag[]{mRoofShape};
            groundShapes = new GroundShape[]{mGroundShape};
        } else if (mMode == 1) {
            roofShapes = mRoofShapes;
            groundShapes = new GroundShape[]{mGroundShape};
        } else if (mMode == 2) {
            roofShapes = new Tag[]{mRoofShape};
            groundShapes = GroundShape.values();
        } else {
            roofShapes = mRoofShapes;
            groundShapes = GroundShape.values();
        }
        MapElement e;
        int x = 0, y = 0;
        for (GroundShape ground : groundShapes) {
            e = new MapElement();
            e.startPolygon();
            applyGroundShape(ground, e);
            e.tags.set(mTags);

            MapElement building;
            for (Tag roofShape : roofShapes) {
                building = new MapElement(e);
                building = building.translate(x, y);
                building.tags.add(roofShape);
                tileSource.addMapElement(building);

                x += 64;
                if (x >= Tile.SIZE) {
                    y += 64;
                    x = 0;
                    if (y >= Tile.SIZE) {
                        x = 32;
                        y = 32;
                    }
                }
            }
        }
    }

    private void applyGroundShape(GroundShape shape, MapElement e) {
        switch (shape) {
            case HEXAGON:
                hexagonGround(e);
                break;
            case RECTANGLE:
                rectangleGround(e);
                break;
            case SHAPE_L:
                shapeLGround(e);
                break;
            case SHAPE_O:
                shapeOGround(e);
                break;
            case SHAPE_T:
                shapeTGround(e);
                break;
            case SHAPE_U:
                shapeUGround(e);
                break;
            case SHAPE_V:
                shapeVGround(e);
                break;
            case SHAPE_X:
                shapeXGround(e);
                break;
            case SHAPE_Z:
                shapeZGround(e);
                break;
            case TEST:
                testGround(e);
                break;
        }
    }

    private void hexagonGround(MapElement e) {
        hexagonGround(e, 10, 0, 0);
    }

    private void hexagonGround(MapElement e, float unit, float shiftX, float shiftY) {
        float sqrt2 = unit * (float) Math.sqrt(2);

        e.addPoint(shiftX + unit, shiftY + 0);
        e.addPoint(shiftX + unit + sqrt2, shiftY + 0);
        e.addPoint(shiftX + 2 * unit + sqrt2, shiftY + unit);
        e.addPoint(shiftX + 2 * unit + sqrt2, shiftY + unit + sqrt2);
        e.addPoint(shiftX + unit + sqrt2, shiftY + 2 * unit + sqrt2);
        e.addPoint(shiftX + unit, shiftY + 2 * unit + sqrt2);
        e.addPoint(shiftX + 0, shiftY + unit + sqrt2);
        e.addPoint(shiftX + 0, shiftY + unit);
    }

    private void rectangleGround(MapElement e) {
        e.addPoint(0, 0);
        e.addPoint(25, 0);
        e.addPoint(25, 20);
        e.addPoint(0, 20);
    }

    private void shapeLGround(MapElement e) {
        e.addPoint(0, 0);
        e.addPoint(12, 0);
        e.addPoint(20, 0);
        e.addPoint(20, 15);
        e.addPoint(12, 15);
        e.addPoint(12, 10);
        e.addPoint(0, 10);
    }

    private void shapeOGround(MapElement e) {
        hexagonGround(e);
        e.reverse();
        e.startHole();
        hexagonGround(e, 5, 5, 5);
        e.reverse();
    }

    private void shapeTGround(MapElement e) {
        e.addPoint(0, 0);
        e.addPoint(10, 0);
        e.addPoint(20, 0);
        e.addPoint(30, 0);
        e.addPoint(30, 15);
        e.addPoint(20, 15);
        e.addPoint(20, 30);
        e.addPoint(10, 30);
        e.addPoint(10, 10);
        e.addPoint(0, 10);
    }

    private void shapeUGround(MapElement e) {
        e.addPoint(0, 0);
        e.addPoint(5, 0);
        e.addPoint(5, 10);
        e.addPoint(20, 10);
        e.addPoint(20, 0);
        e.addPoint(30, 0);
        e.addPoint(30, 20);
        e.addPoint(0, 20);
    }

    private void shapeVGround(MapElement e) {
        e.addPoint(0, 0);
        e.addPoint(5, 0);
        e.addPoint(15, 15);
        e.addPoint(20, 0);
        e.addPoint(30, 0);
        e.addPoint(20, 25);
        e.addPoint(15, 25);
    }

    private void shapeXGround(MapElement e) {
        e.addPoint(0, 10);
        e.addPoint(10, 10);
        e.addPoint(10, 0);
        e.addPoint(20, 0);
        e.addPoint(20, 15);
        e.addPoint(30, 15);
        e.addPoint(30, 25);
        e.addPoint(20, 25);
        e.addPoint(20, 30);
        e.addPoint(10, 30);
        e.addPoint(10, 20);
        e.addPoint(0, 20);
    }

    private void shapeZGround(MapElement e) {
        e.addPoint(0, 0);
        e.addPoint(10, 0);
        e.addPoint(10, 5);
        e.addPoint(20, 5);
        e.addPoint(20, 20);
        e.addPoint(12, 20);
        e.addPoint(12, 15);
        e.addPoint(0, 15);
    }

    private void testGround(MapElement e) {
        e.addPoint(39.967926f, 35.67258f);
        e.addPoint(6.667015f, 38.9533f);
        e.addPoint(4.519531f, 25.69272f);
        e.addPoint(1.253567f, 5.45771f);
        e.addPoint(0.701783f, 2.0393f);
        e.addPoint(20.804614f, 0.0204f);
        e.addPoint(22.743317f, 10.80325f);
        e.addPoint(38.178354f, 9.77085f);
    }

    @Override
    public void createLayers() {
        TestTileSource tts = new TestTileSource();

        addExtrusions(tts);

        VectorTileLayer vtl = mMap.setBaseMap(tts);
        BuildingLayer buildingLayer = new S3DBLayer(mMap, vtl, true);
        buildingLayer.getExtrusionRenderer().getSun().setProgress(0.1f);
        buildingLayer.getExtrusionRenderer().getSun().updatePosition();

        mMap.layers().add(buildingLayer);
        mMap.layers().add(new TileGridLayer(mMap));

        mMap.setTheme(VtmThemes.DEFAULT);

        mMap.setMapPosition(0, 0, 1 << 17);
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new ExtrusionsTest());
    }
}
