/*
 * Copyright 2016-2022 devemux86
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

import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.MapElement;

import java.util.HashSet;
import java.util.Set;

public class TileDataSink implements ITileDataSink {

    public final Set<Integer> hashPois = new HashSet<>();
    public final Set<Integer> hashWays = new HashSet<>();

    public int level, levels;
    private QueryResult result;
    private final ITileDataSink sink;

    public TileDataSink(ITileDataSink sink) {
        this.sink = sink;
    }

    public QueryResult getResult() {
        return result;
    }

    @Override
    public void process(MapElement element) {
        sink.process(element);
    }

    @Override
    public void setTileImage(Bitmap bitmap) {
        sink.setTileImage(bitmap);
    }

    @Override
    public void completed(QueryResult result) {
        this.result = result;
    }
}
