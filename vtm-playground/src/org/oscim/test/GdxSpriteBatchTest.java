package org.oscim.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.oscim.backend.GL;
import org.oscim.core.GeoPoint;
import org.oscim.core.Point;
import org.oscim.gdx.GdxMap;
import org.oscim.gdx.GdxMapApp;
import org.oscim.renderer.GLState;
import org.oscim.renderer.MapRenderer;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;

import static org.oscim.backend.GLAdapter.gl;

/**
 * Created by Gerber Lóránt Viktor on 2017. 03. 18..
 */

public class GdxSpriteBatchTest extends GdxMap {
    private GeoPoint position = new GeoPoint(47.1970869, 18.4398422);
    private int mapScale = 2000 << 6;

    // I'm guessing it is private in GdxMap?
    private MapRenderer mapRenderer;

    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch spriteBatch;

    private Texture texture;

    @Override
    protected void createLayers() {
        TileSource tileSource = new OSciMap4TileSource();
        initDefaultLayers(tileSource, false, true, false);
        mMap.setMapPosition(position.getLatitude(), position.getLongitude(), mapScale);

        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        spriteBatch = new SpriteBatch();

        mapRenderer = new MapRenderer(mMap);
        mapRenderer.onSurfaceCreated();
        mapRenderer.onSurfaceChanged(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        mMap.viewport().setScreenSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Generate a simple texture for testing
        Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGB565);
        pixmap.setColor(0, 0, 0, 1);
        pixmap.fillRectangle(0, 0, 64, 64);
        pixmap.setColor(1, 0, 1, 1);
        pixmap.fillRectangle(0, 0, 32, 32);
        pixmap.fillRectangle(32, 32, 32, 32);
        texture = new Texture(pixmap);
    }

    @Override
    public void render() {
        // Centre the camera
        camera.position.x = Gdx.graphics.getWidth() / 2;
        camera.position.y = Gdx.graphics.getHeight() / 2;
        camera.update();

        // Code taken from Cachebox
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ?
                GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

        GLState.enableVertexArrays(-1, -1);

        gl.viewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gl.frontFace(GL.CW);

        mapRenderer.onDrawFrame();

        gl.flush();
        GLState.bindVertexBuffer(0);
        GLState.bindElementBuffer(0);
        gl.frontFace(GL.CCW);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        Point point = new Point();
        mMap.viewport().toScreenPoint(mMap.getMapPosition().getGeoPoint(), false, point);
        Vector2 worldPos = viewport.unproject(new Vector2((int)point.x, (int)point.y));
        spriteBatch.draw(texture, worldPos.x - 32, worldPos.y - 32, 64, 64);
        spriteBatch.end();

        // Fly over the city.
        double lon = position.getLongitude();
        lon += 0.0001;
        position = new GeoPoint(position.getLatitude(), lon);
        mMap.setMapPosition(position.getLatitude(), position.getLongitude(), mapScale);
        mMap.updateMap(true);
    }

    @Override
    public void resize(int w, int h) {
        mapRenderer.onSurfaceChanged(w, h);
        mMap.viewport().setScreenSize(w, h);
        viewport.update(w, h);
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new GdxSpriteBatchTest());
    }
}
