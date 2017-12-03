/*
 * Copyright 2012 Hannes Janetzek
 * Copyright 2016 Andrey Novikov
 * Copyright 2017 Gustl22
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

import java.util.Arrays;

/**
 * The MapElement class is a reusable containter for a geometry
 * with tags.
 * MapElement is created by TileDataSource(s) and passed to
 * MapTileLoader via ITileDataSink.process().
 * This is just a buffer that belongs to TileDataSource,
 * so dont keep a reference to it when passed as parameter.
 */
public class MapElement extends GeometryBuffer {

    public PointF labelPosition;

    /**
     * layer of the element (0-10) overrides the theme drawing order
     */
    public int layer;

    public final TagSet tags = new TagSet();

    public MapElement() {
        super(1024, 16);
    }

    public MapElement(int points, int indices) {
        super(points, indices);
    }

    private MapElement(float[] points, int[] index) {
        super(points, index);
    }

    public void setLabelPosition(float x, float y) {
        labelPosition = new PointF(x, y);
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    @Override
    public MapElement clear() {
        layer = 5;
        super.clear();
        return this;
    }

    @Override
    public String toString() {

        return tags.toString() + '\n' + super.toString() + '\n';

    }

    /**
     * @return a deep copy of this MapElement
     */
    public MapElement clone() {
        int indexSize = this.indexPos + 1;
        for (int i = 0; i < this.index.length; i++) {
            if (index[i] == -1) {
                indexSize = i;
                break;
            }
        }
        float[] copyPoints = Arrays.copyOf(this.points, this.pointPos);
        int[] copyIndex = Arrays.copyOf(this.index, indexSize);

        MapElement copy = new MapElement(copyPoints, copyIndex);
        copy.tags.set(this.tags.asArray());
        copy.pointPos = this.pointPos;
        copy.labelPosition = this.labelPosition;
        copy.setLayer(this.layer);
        copy.indexPos = this.indexPos;
        copy.type = this.type;
        return copy;
    }
}
