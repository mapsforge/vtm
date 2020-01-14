/*
 * Copyright 2018 boldtrn
 * Copyright 2018 devemux86
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
package org.oscim.tiling.source.mvt;

import org.junit.Test;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.MapElement;
import org.oscim.core.Tile;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.QueryResult;

import static org.junit.Assert.assertEquals;

public class TileDecoderTest {

    @Test
    public void tileDecodingTest() throws Exception {
        TileDecoder decoder = new TileDecoder();
        Tile tile = new Tile(0, 0, (byte) 0);
        ITileDataSink sink = new ITileDataSink() {
            @Override
            public void process(MapElement element) {
                if (element.tags.contains("class", "ocean"))
                    assertEquals(4, element.getNumPoints());
                if (element.tags.contains("layer", "water_name"))
                    assertEquals("Irish Sea", element.tags.getValue("name"));
            }

            @Override
            public void setTileImage(Bitmap bitmap) {
            }

            @Override
            public void completed(QueryResult result) {
            }
        };
        decoder.decode(tile, sink, getClass().getResourceAsStream("/mvt-test.pbf"));
    }
}
