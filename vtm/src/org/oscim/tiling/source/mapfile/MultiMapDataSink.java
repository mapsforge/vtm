/*
 * Copyright 2016 devemux86
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
package org.oscim.tiling.source.mapfile;

import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.MapElement;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.QueryResult;

class MultiMapDataSink implements ITileDataSink {

    private QueryResult result;
    private final ITileDataSink tileDataSink;

    MultiMapDataSink(ITileDataSink tileDataSink) {
        this.tileDataSink = tileDataSink;
    }

    QueryResult getResult() {
        return result;
    }

    @Override
    public void process(MapElement element) {
        tileDataSink.process(element);
    }

    @Override
    public void setTileImage(Bitmap bitmap) {
        tileDataSink.setTileImage(bitmap);
    }

    @Override
    public void completed(QueryResult result) {
        this.result = result;
    }
}
