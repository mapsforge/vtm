/*
 * Copyright 2017 Gerber Lóránt Viktor
 * Copyright 2017 devemux86
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
import org.oscim.core.Point;
import org.oscim.gdx.GdxMapApp;
import org.oscim.renderer.GLState;
import org.oscim.renderer.MapRenderer;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;

import static org.oscim.backend.GLAdapter.gl;

public class GdxSpriteBatchTest extends GdxMapApp {
    private double latitude = 47.1970869;
    private double longitude = 18.4398422;
    private double scale = 1 << 17;

    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch spriteBatch;

    private Texture texture;

    @Override
    public void createLayers() {
        TileSource tileSource = OSciMap4TileSource.builder()
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                .build();
        initDefaultLayers(tileSource, false, true, false);
        mMap.setMapPosition(latitude, longitude, scale);

        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        spriteBatch = new SpriteBatch();

        mMapRenderer = new MapRenderer(mMap);
        mMapRenderer.onSurfaceCreated();
        mMapRenderer.onSurfaceChanged(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        mMap.viewport().setViewSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

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

        mMapRenderer.onDrawFrame();

        gl.flush();
        GLState.bindVertexBuffer(0);
        GLState.bindElementBuffer(0);
        gl.frontFace(GL.CCW);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        Point point = new Point();
        mMap.viewport().toScreenPoint(mMap.getMapPosition().getGeoPoint(), false, point);
        Vector2 worldPos = viewport.unproject(new Vector2((int) point.x, (int) point.y));
        spriteBatch.draw(texture, worldPos.x - 32, worldPos.y - 32, 64, 64);
        spriteBatch.end();

        // Fly over the city.
        longitude += 0.0001;
        mMap.setMapPosition(latitude, longitude, scale);
        mMap.updateMap(true);
    }

    @Override
    public void resize(int w, int h) {
        mMapRenderer.onSurfaceChanged(w, h);
        mMap.viewport().setViewSize(w, h);
        viewport.update(w, h);
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new GdxSpriteBatchTest());
    }
}
