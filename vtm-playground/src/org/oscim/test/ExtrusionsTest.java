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
import org.oscim.gdx.GdxMapApp;
import org.oscim.layers.TileGridLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.buildings.S3DBLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.test.tiling.source.TestTileSource;
import org.oscim.theme.VtmThemes;

public class ExtrusionsTest extends GdxMapApp {

    enum GroundShape {
        Rect, Hexagon, L, O,
    }

    public GroundShape mGroundShape = GroundShape.Rect;

    Tag[] mTags = {
            new Tag(Tag.KEY_BUILDING, Tag.VALUE_YES),
            new Tag(Tag.KEY_BUILDING_LEVELS, "2"),
            new Tag(Tag.KEY_ROOF_LEVELS, "2"),
            new Tag(Tag.KEY_ROOF_COLOR, "#99ffff"),
            new Tag(Tag.KEY_BUILDING_COLOR, "white")
    };

    private void addExtrusions(TestTileSource tts) {

        MapElement e = new MapElement();
        e.startPolygon();
        switch (mGroundShape) {
            case Rect:
                rectangleGround(e);
                break;
            case Hexagon:
                hexagonGround(e);
                break;
            case L:
                lGround(e);
                break;
            case O:
                oGround(e);
                break;
        }
        e.tags.set(mTags);

        MapElement building;

        building = new MapElement(e);
        building = building.translate(0, 0);
        building.tags.add(new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_FLAT));
        tts.addMapElement(building);

        building = new MapElement(e);
        building = building.translate(100, 0);
        building.tags.add(new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_GABLED));
        tts.addMapElement(building);

        building = new MapElement(e);
        building = building.translate(200, 0);
        building.tags.add(new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_PYRAMIDAL));
        tts.addMapElement(building);

        building = new MapElement(e);
        building = building.translate(300, 0);
        building.tags.add(new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_DOME));
        tts.addMapElement(building);

        building = new MapElement(e);
        building = building.translate(0, 100);
        building.tags.add(new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_GAMBREL));
        tts.addMapElement(building);

        building = new MapElement(e);
        building = building.translate(100, 100);
        building.tags.add(new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_HALF_HIPPED));
        tts.addMapElement(building);

        building = new MapElement(e);
        building = building.translate(200, 100);
        building.tags.add(new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_HIPPED));
        tts.addMapElement(building);

        building = new MapElement(e);
        building = building.translate(300, 100);
        building.tags.add(new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_MANSARD));
        tts.addMapElement(building);

        building = new MapElement(e);
        building = building.translate(0, 200);
        building.tags.add(new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_ONION));
        tts.addMapElement(building);

        building = new MapElement(e);
        building = building.translate(100, 200);
        building.tags.add(new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_ROUND));
        tts.addMapElement(building);

        building = new MapElement(e);
        building = building.translate(200, 200);
        building.tags.add(new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_SALTBOX));
        tts.addMapElement(building);

        building = new MapElement(e);
        building = building.translate(300, 200);
        building.tags.add(new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_SKILLION));
        tts.addMapElement(building);
    }

    private void rectangleGround(MapElement e) {
        e.addPoint(0, 0);
        e.addPoint(25, 0);
        e.addPoint(25, 20);
        e.addPoint(0, 20);
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

    private void lGround(MapElement e) {
        e.addPoint(0, 0);
        e.addPoint(20, 0);
        e.addPoint(20, 15);
        e.addPoint(12, 15);
        e.addPoint(12, 10);
        e.addPoint(0, 10);
    }

    private void oGround(MapElement e) {
        hexagonGround(e);
        e.reverse();
        e.startHole();
        hexagonGround(e, 5, 5, 5);
        e.reverse();
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
