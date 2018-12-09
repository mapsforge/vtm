/*
 * Copyright 2017-2018 Longri
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
package org.oscim.renderer;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.GL;
import org.oscim.backend.canvas.Color;
import org.oscim.core.Box;
import org.oscim.core.Point;
import org.oscim.core.PointF;
import org.oscim.core.Tile;
import org.oscim.map.Map;
import org.oscim.renderer.atlas.TextureRegion;
import org.oscim.renderer.bucket.SymbolBucket;
import org.oscim.renderer.bucket.SymbolItem;
import org.oscim.utils.FastMath;
import org.oscim.utils.geom.GeometryUtils;
import org.oscim.utils.math.Interpolation;

import java.util.Locale;

import static org.oscim.backend.GLAdapter.gl;

public class LocationTextureRenderer extends BucketRenderer {

    private static final PointF CENTER_OFFSET = new PointF(0.5f, 0.5f);
    private static final long ANIM_RATE = 50;
    private static final long INTERVAL = 2000;
    private static final float CIRCLE_SIZE = 30;
    private static final int SHOW_ACCURACY_ZOOM = 13;
    private static final boolean IS_MAC = System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("mac");

    private static final String V_SHADER = (""
            + "precision highp float;"
            + "uniform mat4 u_mvp;"
            + "uniform float u_phase;"
            + "uniform float u_scale;"
            + "attribute vec2 a_pos;"
            + "varying vec2 v_tex;"
            + "void main() {"
            + "  gl_Position = u_mvp * vec4(a_pos * u_scale * u_phase, 0.0, 1.0);"
            + "  v_tex = a_pos;"
            + "}").replace("precision highp float;", IS_MAC ? "" : "precision highp float;");

    // only circle without direction
    private static final String F_SHADER = (""
            + "precision highp float;"
            + "varying vec2 v_tex;"
            + "uniform float u_scale;"
            + "uniform float u_phase;"
            + "uniform vec4 u_fill;"
            + "void main() {"
            + "  float len = 1.0 - length(v_tex);"
            ///  outer ring
            + "  float a = smoothstep(0.0, 2.0 / u_scale, len);"
            ///  inner ring
            + "  float b = 0.8 * smoothstep(3.0 / u_scale, 4.0 / u_scale, len);"
            ///  center point
            + "  float c = 0.5 * (1.0 - smoothstep(14.0 / u_scale, 16.0 / u_scale, 1.0 - len));"
            + "  vec2 dir = normalize(v_tex);"
            ///  - subtract inner from outer to create the outline
            ///  - multiply by viewshed
            ///  - add center point
            + "  a = (a - (b + c)) + c;"
            + "  gl_FragColor = u_fill * a;"
            + "}").replace("precision highp float;", IS_MAC ? "" : "precision highp float;");

    private final SymbolBucket symbolBucket;
    private final float[] box = new float[8];
    private final Point mapPoint = new Point();
    private final Map map;
    private boolean initialized;
    private boolean locationIsVisible;
    private int shaderProgramNumber;
    private int hVertexPosition;
    private int hMatrixPosition;
    private int hScale;
    private int hPhase;
    private int uFill;
    private double radius;

    private final Point indicatorPosition = new Point();
    private final Point screenPoint = new Point();
    private final Box boundingBox = new Box();
    private boolean runAnim;
    private boolean animate = true;
    private long animStart;
    private boolean update;
    private float bearing;

    // properties
    private TextureRegion textureRegion;
    private int accuracyColor = Color.BLUE;
    private int viewShedColor = Color.RED;
    private boolean billboard = false;

    public LocationTextureRenderer(Map map) {
        this.map = map;
        symbolBucket = new SymbolBucket();
    }

    public void setAccuracyColor(int color) {
        this.accuracyColor = color;
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    public void setBillboard(boolean billboard) {
        this.billboard = billboard;
    }

    public void setIndicatorColor(int color) {
        this.viewShedColor = color;
    }

    public void setLocation(double x, double y, float bearing, double radius) {
        update = true;
        mapPoint.x = x;
        mapPoint.y = y;
        this.bearing = bearing;
        this.radius = radius;
    }

    public void setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    public void setTextureRegion(TextureRegion textureRegion, boolean billboard) {
        this.textureRegion = textureRegion;
        this.billboard = billboard;
    }

    public void animate(boolean enable) {
        if (runAnim == enable)
            return;

        runAnim = enable;
        if (!enable)
            return;
        if (!animate)
            return;

        final Runnable action = new Runnable() {
            private long lastRun;

            @Override
            public void run() {
                if (!runAnim)
                    return;
                if (!animate)
                    return;

                long diff = System.currentTimeMillis() - lastRun;
                map.postDelayed(this, Math.min(ANIM_RATE, diff));
                map.render();
                lastRun = System.currentTimeMillis();
            }
        };

        animStart = System.currentTimeMillis();
        map.postDelayed(action, ANIM_RATE);
    }

    private float animPhase() {
        return (float) ((MapRenderer.frametime - animStart) % INTERVAL) / INTERVAL;
    }

    @Override
    public synchronized void update(GLViewport v) {
        if (!v.changed() && !update)
            return;

        // accuracy
        if (!initialized) {
            init();
            initialized = true;
        }
        setReady(true);

        int width = map.getWidth();
        int height = map.getHeight();
        v.getBBox(boundingBox, 0);

        double x = mapPoint.x;
        double y = mapPoint.y;

        if (!boundingBox.contains(mapPoint)) {
            x = FastMath.clamp(x, boundingBox.xmin, boundingBox.xmax);
            y = FastMath.clamp(y, boundingBox.ymin, boundingBox.ymax);
        }

        // get position of Location in pixel relative to
        // screen center
        v.toScreenPoint(x, y, screenPoint);

        x = screenPoint.x + width / 2;
        y = screenPoint.y + height / 2;

        // clip position to screen boundaries
        int visible = 0;

        if (x > width - 5)
            x = width;
        else if (x < 5)
            x = 0;
        else
            visible++;

        if (y > height - 5)
            y = height;
        else if (y < 5)
            y = 0;
        else
            visible++;

        locationIsVisible = (visible == 2);

        if (locationIsVisible)
            animate(false);
        else
            animate(true);
        // set location indicator position
        v.fromScreenPoint(x, y, indicatorPosition);

        // Texture
        mMapPosition.copy(v.pos);

        double mx = v.pos.x;
        double my = v.pos.y;
        double scale = Tile.SIZE * v.pos.scale;
        map.viewport().getMapExtents(box, 100);
        long flip = (long) (Tile.SIZE * v.pos.scale) >> 1;

        /* check visibility */
        float symbolX = (float) ((mapPoint.x - mx) * scale);
        float symbolY = (float) ((mapPoint.y - my) * scale);

        if (symbolX > flip)
            symbolX -= (flip << 1);
        else if (symbolX < -flip)
            symbolX += (flip << 1);
        buckets.clear();
        if (!GeometryUtils.pointInPoly(symbolX, symbolY, box, 8, 0))
            return;

        mMapPosition.bearing = -mMapPosition.bearing;
        if (textureRegion == null)
            return;
        SymbolItem symbolItem = SymbolItem.pool.get();
        symbolItem.set(symbolX, symbolY, textureRegion, this.bearing, this.billboard);
        symbolItem.offset = CENTER_OFFSET;
        symbolBucket.pushSymbol(symbolItem);

        buckets.set(symbolBucket);
        buckets.prepare();
        buckets.compile(true);
        compile();
        update = false;
    }

    @Override
    public void render(GLViewport v) {
        renderAccuracyCircle(v);
        super.render(v);
    }

    private void init() {
        int shader = GLShader.createProgram(V_SHADER, F_SHADER);
        if (shader == 0)
            return;

        shaderProgramNumber = shader;
        hVertexPosition = gl.getAttribLocation(shader, "a_pos");
        hMatrixPosition = gl.getUniformLocation(shader, "u_mvp");
        hPhase = gl.getUniformLocation(shader, "u_phase");
        hScale = gl.getUniformLocation(shader, "u_scale");
        uFill = gl.getUniformLocation(shader, "u_fill");
    }

    private void renderAccuracyCircle(GLViewport v) {
        GLState.useProgram(shaderProgramNumber);
        GLState.blend(true);
        GLState.test(false, false);

        GLState.enableVertexArrays(hVertexPosition, GLState.DISABLED);
        MapRenderer.bindQuadVertexVBO(hVertexPosition/*, true*/);

        float radius = 10;
        boolean viewShed = false;
        if (!locationIsVisible)
            radius = CIRCLE_SIZE * CanvasAdapter.getScale();
        else {
            if (v.pos.zoomLevel >= SHOW_ACCURACY_ZOOM)
                radius = (float) (this.radius * v.pos.scale);
            radius = Math.max(2, radius);
            viewShed = true;
        }
        gl.uniform1f(hScale, radius);

        double x = indicatorPosition.x - v.pos.x;
        double y = indicatorPosition.y - v.pos.y;
        double tileScale = Tile.SIZE * v.pos.scale;

        v.mvp.setTransScale((float) (x * tileScale), (float) (y * tileScale), 1);
        v.mvp.multiplyMM(v.viewproj, v.mvp);
        v.mvp.setAsUniform(hMatrixPosition);

        if (!viewShed && animate) {
            float phase = Math.abs(animPhase() - 0.5f) * 2;
            //phase = Interpolation.fade.apply(phase);
            phase = Interpolation.swing.apply(phase);
            gl.uniform1f(hPhase, 0.8f + phase * 0.2f);
        } else
            gl.uniform1f(hPhase, 1);

        if (viewShed && locationIsVisible)
            GLUtils.setColor(uFill, accuracyColor, 1);
        else
            GLUtils.setColor(uFill, viewShedColor, 1);

        gl.drawArrays(GL.TRIANGLE_STRIP, 0, 4);
        gl.flush();
    }
}
