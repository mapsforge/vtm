package org.oscim.gdx.poi3d;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import org.oscim.backend.GL;
import org.oscim.core.Tile;
import org.oscim.map.Map;
import org.oscim.map.Viewport;
import org.oscim.renderer.GLState;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.LayerRenderer;
import org.oscim.utils.geom.GeometryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.oscim.backend.GLAdapter.gl;

/**
 * Gdx renderer for more complex 3D models (with child parts)
 */
public class GdxRenderer3D2 extends LayerRenderer {
    static final Logger log = LoggerFactory.getLogger(GdxRenderer3D2.class);

    ModelBatch modelBatch;
    public MapCamera cam;
    Map mMap;

    boolean loading;

    public Environment lights;

    public Array<ModelInstance> instances = new Array<>();

    public GdxRenderer3D2(Map map) {
        mMap = map;
    }

    @Override
    public boolean setup() {

        modelBatch = new ModelBatch(new DefaultShaderProvider());

        lights = new Environment();

        lights.add(new DirectionalLight().set(0.7f, 0.7f, 0.7f, 0, 1, -0.2f));
        lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));

        cam = new MapCamera(mMap);

        return true;
    }

    @Override
    public synchronized void update(GLViewport v) {
        // if (loading && assets.update())
        // doneLoading();

        if (!isReady()) {
            cam.setPosition(v.pos);
            setReady(true);
        }

        // if (changed) {
        // cam.update(position, matrices);
        // }
    }

    Vector3 tempVector = new Vector3();
    float[] mBox = new float[8];

    @Override
    public void render(GLViewport v) {
        if (instances.size == 0)
            return;

        // GLUtils.checkGlError(">" + TAG);

        gl.depthMask(true);

        if (v.pos.zoomLevel < 17)
            gl.clear(GL.DEPTH_BUFFER_BIT);

        gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, 0);

        // set state that is expected after modelBatch.end();
        // modelBatch keeps track of its own state
        GLState.enableVertexArrays(GLState.DISABLED, GLState.DISABLED);
        GLState.bindTex2D(GLState.DISABLED);
        GLState.useProgram(GLState.DISABLED);
        GLState.test(false, false);
        GLState.blend(false);

        // GL.cullFace(GL20.BACK);
        // GL.frontFace(GL20.CW);

        cam.update(v);
        long time = System.currentTimeMillis();

        int cnt = 0;
        int rnd = 0;

        Viewport p = mMap.viewport();
        p.getMapExtents(mBox, 10);
        float scale = (float) (cam.mMapPosition.scale / v.pos.scale);

        float dx = (float) (cam.mMapPosition.x - v.pos.x)
                * (Tile.SIZE << cam.mMapPosition.zoomLevel);
        float dy = (float) (cam.mMapPosition.y - v.pos.y)
                * (Tile.SIZE << cam.mMapPosition.zoomLevel);

        for (int i = 0; i < 8; i += 2) {
            mBox[i] *= scale;
            mBox[i] -= dx;
            mBox[i + 1] *= scale;
            mBox[i + 1] -= dy;
        }

        synchronized (this) {
            modelBatch.begin(cam);
            cnt = instances.size;

            for (ModelInstance instance : instances) {
                instance.transform.getTranslation(tempVector);
                tempVector.scl(0.9f, 0.9f, 1);
                if (!GeometryUtils.pointInPoly(tempVector.x, tempVector.y, mBox, 8, 0))
                    continue;

                modelBatch.render(instance, lights);
                rnd++;
            }
            modelBatch.end();
        }
        log.debug(">>> " + (System.currentTimeMillis() - time) + " " + cnt + "/" + rnd);

        // GLUtils.checkGlError("<" + TAG);

        gl.depthMask(false);
        gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, 0);
        gl.bindBuffer(GL.ARRAY_BUFFER, 0);

        // GLState.bindTex2D(-1);
        // GLState.useProgram(-1);
    }

    // @Override
    // public void dispose () {
    // modelBatch.dispose();
    // assets.dispose();
    // assets = null;
    // axesModel.dispose();
    // axesModel = null;
    // }
}
