package org.oscim.tiling.source.mvt;

import org.junit.Test;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.MapElement;
import org.oscim.core.Tile;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.VectorTileLoader;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.QueryResult;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MvtTileDecoderTest {

    @Test
    public void tileDecodingTest() throws Exception {
        MvtTileDecoder decoder = new MvtTileDecoder();
        Tile tile = new Tile(0, 0, (byte) 0);
        ITileDataSink sink = new ITileDataSink() {
            @Override
            public void process(MapElement element) {
                if(element.tags.contains("class", "ocean")){
                    assertEquals(4, element.getNumPoints());
                }if(element.tags.contains("layer", "water_name")){
                    assertEquals("Irish Sea", element.tags.getValue("name"));
                }
            }

            @Override
            public void setTileImage(Bitmap bitmap) {

            }

            @Override
            public void completed(QueryResult result) {

            }
        };
        File f = new File("vtm-tests/resources/mvt-test.pbf");
        System.out.println(f.getAbsolutePath());
        decoder.decode(tile, sink, new FileInputStream(f));
    }

}
