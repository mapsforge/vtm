package org.oscim.gdx.poi3d;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
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

public class GdxRenderer3D extends LayerRenderer {
    static final Logger log = LoggerFactory.getLogger(GdxRenderer3D.class);

    ModelBatch modelBatch;
    public MapCamera cam;
    Map mMap;

    boolean loading;

    public Environment lights;

    public Array<ModelInstance> instances = new Array<>();

    public Shader shader;
    public RenderContext renderContext;
    public Model model;

    public GdxRenderer3D(Map map) {
        mMap = map;
    }

    @Override
    public boolean setup() {

        modelBatch = new ModelBatch(new DefaultShaderProvider());

        lights = new Environment();
        lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 1.0f, 1.0f, 1.0f, 1.f));
        lights.add(new DirectionalLight().set(0.3f, 0.3f, 0.3f, 0, 1, -0.2f));

        cam = new MapCamera(mMap);

        renderContext =
                new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.LRU, 1));

        // shader = new DefaultShader(renderable.material,
        // renderable.mesh.getVertexAttributes(), true, false, 1, 0, 0, 0);
        // shader.init();

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

    Renderable r = new Renderable();

    @Override
    public void render(GLViewport v) {
        if (instances.size == 0)
            return;

        // GLUtils.checkGlError(">" + TAG);

        gl.depthMask(true);

        // if (position.zoomLevel < 17)
        // GL.clear(GL20.DEPTH_BUFFER_BIT);

        // Unbind via GLState to ensure no buffer is replaced by accident
        GLState.bindElementBuffer(GLState.UNBIND);
        GLState.bindBuffer(GL.ARRAY_BUFFER, GLState.UNBIND);

        // set state that is expected after modelBatch.end();
        // modelBatch keeps track of its own state
        GLState.enableVertexArrays(GLState.DISABLED, GLState.DISABLED);
        GLState.bindTex2D(GLState.DISABLED);
        GLState.useProgram(GLState.DISABLED);
        GLState.test(false, false);
        GLState.blend(false);

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

        int w = mMap.getWidth() / 2;
        int h = mMap.getHeight() / 2;

        float sqRadius = (w * w + h * h) / scale;

        synchronized (this) {
            if (instances.size == 0)
                return;

            cnt = instances.size;

            renderContext.begin();

            if (shader == null) {
                r = instances.get(0).getRenderable(r);
                DefaultShader.Config c = new DefaultShader.Config();
                c.numBones = 0;
                c.numDirectionalLights = 1;
                r.environment = lights;
                // shader = new DefaultShader(r, true, false, false, false, 1,
                // 0, 0, 0);
                shader = new DefaultShader(r, c);
                shader.init();
            }

            shader.begin(cam, renderContext);

            for (ModelInstance instance : instances) {
                instance.transform.getTranslation(tempVector);

                if (tempVector.x * tempVector.x + tempVector.y * tempVector.y > sqRadius)
                    continue;

                tempVector.scl(0.8f, 0.8f, 1);

                if (!GeometryUtils.pointInPoly(tempVector.x, tempVector.y, mBox, 8, 0))
                    continue;

                instance.getRenderable(r);
                // r.lights = lights;
                // r.environment = lights;
                shader.render(r);

                rnd++;
            }

            shader.end();
            renderContext.end();
        }
        //log.debug(">>> " + (System.currentTimeMillis() - time) + " " + cnt + "/" + rnd);

        gl.depthMask(false);
        GLState.bindElementBuffer(GLState.UNBIND);
        GLState.bindBuffer(GL.ARRAY_BUFFER, GLState.UNBIND);
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
