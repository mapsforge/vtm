/*
 * Copyright 2018 devemux86
 * Copyright 2018 Gustl22
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
package org.oscim.tiling;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.MapElement;
import org.oscim.core.Tile;
import org.oscim.utils.geom.TileClipper;
import org.oscim.utils.geom.TileSeparator;

class OverzoomDataSink implements ITileDataSink {

    private final ITileDataSink sink;

    private final TileClipper clipper;
    private final TileSeparator separator;
    private final float dx, dy, scale;

    OverzoomDataSink(ITileDataSink sink, Tile overzoomTile, Tile tile) {
        this.sink = sink;

        int diff = tile.zoomLevel - overzoomTile.zoomLevel;
        dx = (tile.tileX - (overzoomTile.tileX << diff)) * Tile.SIZE;
        dy = (tile.tileY - (overzoomTile.tileY << diff)) * Tile.SIZE;
        scale = 1 << diff;
        float buffer = 32 * CanvasAdapter.getScale();
        clipper = new TileClipper((dx - buffer) / scale, (dy - buffer) / scale,
                (dx + Tile.SIZE + buffer) / scale, (dy + Tile.SIZE + buffer) / scale);
        separator = new TileSeparator(dx / scale, dy / scale,
                (dx + Tile.SIZE) / scale, (dy + Tile.SIZE) / scale);
    }

    @Override
    public void process(MapElement element) {
        if (element.isBuilding() || element.isBuildingPart() || element.isPoint()) {
            if (!separator.separate(element))
                return;
        } else {
            if (!clipper.clip(element))
                return;
        }
        element.scale(scale, scale);
        element.translate(-dx, -dy);
        sink.process(element);
    }

    @Override
    public void setTileImage(Bitmap bitmap) {
        sink.setTileImage(bitmap);
    }

    @Override
    public void completed(QueryResult result) {
        sink.completed(result);
    }
}
