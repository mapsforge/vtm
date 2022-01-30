/*
 * Copyright 2012 Hannes Janetzek
 * Copyright 2016 Andrey Novikov
 * Copyright 2017-2019 Gustl22
 * Copyright 2018-2022 devemux86
 * Copyright 2019 marq24
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
package org.oscim.core;

import org.oscim.theme.IRenderTheme;

/**
 * The MapElement class is a reusable container for a geometry with tags.
 * <p>
 * MapElement is created by TileDataSource(s) and passed to MapTileLoader via ITileDataSink.process().
 * This is just a buffer that belongs to TileDataSource,
 * so don't keep a reference to it when passed as parameter or make sure to use a copy.
 */
public class MapElement extends GeometryBuffer {

    public PointF centroidPosition, labelPosition;

    /**
     * layer of the element (0-10) overrides the theme drawing order.
     */
    public int layer;

    public int level;

    public final TagSet tags = new TagSet();

    public MapElement() {
        super(1024, 16);
    }

    public MapElement(int numPoints, int numIndices) {
        super(numPoints, numIndices);
    }

    public MapElement(float[] points, int[] index) {
        super(points, index);
    }

    /**
     * @param element the map element to copy
     */
    public MapElement(MapElement element) {
        super(element);
        this.tags.set(element.tags.asArray());
        this.centroidPosition = element.centroidPosition;
        this.labelPosition = element.labelPosition;
        this.setLayer(element.layer);
        this.level = element.level;
    }

    /**
     * @return height in meters, if present
     */
    public Float getHeight(IRenderTheme theme) {
        String res = theme != null ? theme.transformBackwardKey(Tag.KEY_HEIGHT) : Tag.KEY_HEIGHT;
        String v = tags.getValue(res != null ? res : Tag.KEY_HEIGHT);
        if (v != null)
            return Float.parseFloat(v);
        return null;
    }

    /**
     * @return minimum height in meters, if present
     */
    public Float getMinHeight(IRenderTheme theme) {
        String res = theme != null ? theme.transformBackwardKey(Tag.KEY_MIN_HEIGHT) : Tag.KEY_MIN_HEIGHT;
        String v = tags.getValue(res != null ? res : Tag.KEY_MIN_HEIGHT);
        if (v != null)
            return Float.parseFloat(v);
        return null;
    }

    /**
     * @return true if this is a building, else false.
     */
    public boolean isBuilding() { // TODO from themes (with overzoom ref)
        return tags.containsKey(Tag.KEY_BUILDING)
                || "building".equals(tags.getValue("kind")) // Mapzen
                || "building".equals(tags.getValue("layer")); // OpenMapTiles
    }

    /**
     * @return true if this is a building part, else false.
     */
    public boolean isBuildingPart() { // TODO from themes (with overzoom ref)
        return tags.containsKey(Tag.KEY_BUILDING_PART)
                || "building_part".equals(tags.getValue("kind")) // Mapzen
                || "building:part".equals(tags.getValue("layer")); // OpenMapTiles
    }

    public void setCentroidPosition(float x, float y) {
        centroidPosition = new PointF(x, y);
    }

    public void setLabelPosition(float x, float y) {
        labelPosition = new PointF(x, y);
    }

    /**
     * Set the layer of the element (0-10) to override theme drawing order.
     */
    public void setLayer(int layer) {
        this.layer = layer;
    }

    @Override
    public MapElement clear() {
        layer = 5;
        level = 0;
        super.clear();
        return this;
    }

    @Override
    public MapElement scale(float scaleX, float scaleY) {
        super.scale(scaleX, scaleY);
        if (centroidPosition != null) {
            centroidPosition.x *= scaleX;
            centroidPosition.y *= scaleY;
        }
        if (labelPosition != null) {
            labelPosition.x *= scaleX;
            labelPosition.y *= scaleY;
        }
        return this;
    }

    @Override
    public MapElement translate(float dx, float dy) {
        super.translate(dx, dy);
        if (centroidPosition != null) {
            centroidPosition.x += dx;
            centroidPosition.y += dy;
        }
        if (labelPosition != null) {
            labelPosition.x += dx;
            labelPosition.y += dy;
        }
        return this;
    }

    @Override
    public String toString() {
        return tags.toString() + '\n' + super.toString() + '\n';
    }
}
