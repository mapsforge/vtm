package org.oscim.tiling.source;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.oscim.core.Tile;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static org.fest.assertions.api.Assertions.assertThat;

public class OkHttpEngineTest {
    private OkHttpEngine engine;
    private MockWebServer server;

    @Before
    public void setUp() throws Exception {
        MockResponse mockResponse = new MockResponse();
        mockResponse.setBody("TEST RESPONSE");
        server = new MockWebServer();
        server.enqueue(mockResponse);
        server.start();
        engine = (OkHttpEngine) new OkHttpEngine.OkHttpFactory()
                .create(new OSciMap4TileSource(server.url("/tiles/vtm").toString()));
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    public void shouldNotBeNull() throws Exception {
        assertThat(engine).isNotNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendRequest_shouldRejectNullTile() throws Exception {
        engine.sendRequest(null);
    }

    @Test
    public void sendRequest_shouldAppendXYZToPath() throws Exception {
        engine.sendRequest(new Tile(1, 2, (byte) 3));

        RecordedRequest request = server.takeRequest();
        assertThat(request.getPath()).isEqualTo("/tiles/vtm/3/1/2.vtm");
    }

    @Test
    public void read_shouldReturnResponseStream() throws Exception {
        engine.sendRequest(new Tile(1, 2, (byte) 3));

        InputStream responseStream = engine.read();
        String response = new BufferedReader(new InputStreamReader(responseStream)).readLine();
        assertThat(response).isEqualTo("TEST RESPONSE");
    }

    //    @Test(expected = IOException.class)
    //    public void close_shouldCloseInputStream() throws Exception {
    //        engine.sendRequest(new Tile(1, 2, new Integer(3).byteValue()));
    //        engine.close();
    //        // Calling read after the stream is closed should throw an exception.
    //        InputStream responseStream = engine.read();
    //        responseStream.read();
    //    }
    //
    //    @Test(expected = IOException.class)
    //    public void requestCompleted_shouldCloseInputStream() throws Exception {
    //        engine.sendRequest(new Tile(1, 2, new Integer(3).byteValue()));
    //        engine.requestCompleted(true);
    //        // Calling read after the stream is closed should throw an exception.
    //        InputStream responseStream = engine.read();
    //        responseStream.read();
    //    }

    @Test
    public void requestCompleted_shouldReturnValueGiven() throws Exception {
        assertThat(engine.requestCompleted(true)).isTrue();
        assertThat(engine.requestCompleted(false)).isFalse();
    }

    @Test
    public void create_shouldUseTileSourceCache() throws Exception {
        Cache cache = new Cache(new File("tmp"), 1024);
        OSciMap4TileSource tileSource =
                new OSciMap4TileSource(server.url("/tiles/vtm").toString());
        OkHttpClient.Builder builder = new OkHttpClient.Builder().cache(cache);
        engine = (OkHttpEngine) new OkHttpEngine.OkHttpFactory(builder).create(tileSource);
        engine.sendRequest(new Tile(1, 2, (byte) 3));
        engine.requestCompleted(true);
        assertThat(cache.requestCount()).isEqualTo(1);
    }
}
