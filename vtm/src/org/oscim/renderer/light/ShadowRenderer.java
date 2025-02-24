/*
 * Copyright 2019 Gustl22
 * Copyright 2019 schedul-xor
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
package org.oscim.renderer.light;

import org.oscim.backend.GL;
import org.oscim.renderer.*;
import org.oscim.utils.math.MathUtils;

import java.nio.FloatBuffer;

import static org.oscim.backend.GLAdapter.gl;

public class ShadowRenderer extends LayerRenderer {

    public static boolean DEBUG = false;

    private ExtrusionRenderer mRenderer;

    private float SHADOWMAP_RESOLUTION = 2048f;
    private int mGroundQuad;
    //private int mGroundShadowQuad;
    private ShadowFrameBuffer mFrameBuffer;

    private float[] mOrthoMat = new float[16];
    private float[] mViewProjTmp = new float[16];
    private GLMatrix mLightMat = new GLMatrix();
    private GLMatrix mRotTmp = new GLMatrix();

    static float[] texUnitConverterF = new float[]{
            0.5f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.5f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f};
    static GLMatrix texUnitConverter = new GLMatrix();

    static {
        texUnitConverter.set(texUnitConverterF);
    }

    /**
     * Shader to draw the extrusions.
     */
    private Shader mExtrusionShader;

    /**
     * Shader to draw the ground.
     */
    private GroundShader mGroundShader;

    /**
     * Shader to create shadow map (of ground and extrusions) from lights view.
     */
    private ExtrusionRenderer.Shader mLightShader;

    public static class GroundShader extends GLShader {
        int aPos, uLightColor, uLightMvp, uMVP, uShadowMap, uShadowRes;

        public GroundShader(String shader) {
            if (!createDirective(shader, "#define SHADOW 1\n"))
                return;

            aPos = getAttrib("a_pos");
            uLightColor = getUniform("u_lightColor");
            uLightMvp = getUniform("u_light_mvp");
            uMVP = getUniform("u_mvp");
            uShadowMap = getUniform("u_shadowMap");
            uShadowRes = getUniform("u_shadowRes");
        }
    }

    public static class Shader extends ExtrusionRenderer.Shader {
        /**
         * For temporary use.
         */
        static final GLMatrix lightMvp = new GLMatrix();

        /**
         * The light view projection matrix.
         */
        GLMatrix lightMat = null;

        /**
         * The light color and shadow transparency as uniform.
         */
        int uLightColor;

        /**
         * The light mvp for shadow as uniform.
         */
        int uLightMvp;

        /**
         * The shadow map texture as uniform.
         */
        int uShadowMap;

        /**
         * The shadow map resolution as uniform.
         */
        int uShadowRes;

        public Shader(String shader) {
            super(shader, "#define SHADOW 1\n");
            uLightColor = getUniform("u_lightColor");
            uLightMvp = getUniform("u_light_mvp");
            uShadowMap = getUniform("u_shadowMap");
            uShadowRes = getUniform("u_shadowRes");
        }

        public void setLightMVP(GLMatrix model) {
            if (lightMat == null) return;
            synchronized (lightMvp) {
                lightMvp.copy(model);
                lightMvp.multiplyLhs(lightMat);
                //lightMvp.addDepthOffset(delta);
                lightMvp.setAsUniform(uLightMvp);
            }
        }
    }

    public ShadowRenderer(ExtrusionRenderer renderer) {
        setRenderer(renderer);
    }

    public void setRenderer(ExtrusionRenderer renderer) {
        mRenderer = renderer;
    }

    /**
     * Bind a plane to easily draw all over the ground.
     */
    private static int bindPlane(float width, float height) {
        int vertexBuffer;
        int[] vboIds = GLUtils.glGenBuffers(1);
        FloatBuffer floatBuffer = MapRenderer.getFloatBuffer(8);
        // indices:  0 1 2 - 2 1 3
        float[] quad = new float[]{
                -width, height,
                width, height,
                -width, -height,
                width, -height
        };
        floatBuffer.put(quad);
        floatBuffer.flip();
        vertexBuffer = vboIds[0];
        GLState.bindVertexBuffer(vertexBuffer);
        gl.bufferData(GL.ARRAY_BUFFER,
                quad.length * 4, floatBuffer,
                GL.STATIC_DRAW);
        GLState.bindVertexBuffer(GLState.UNBIND);
        return vertexBuffer;
    }

    @Override
    public boolean setup() {
        // Ground plane for shadow map
        //mGroundShadowQuad = bindPlane(SHADOWMAP_RESOLUTION * 1.1f, SHADOWMAP_RESOLUTION * 1.1f);
        if (!DEBUG) {
            // Ground plane to draw shadows an
            mGroundQuad = bindPlane(Short.MAX_VALUE, Short.MAX_VALUE);
        } else {
            mGroundQuad = bindPlane(SHADOWMAP_RESOLUTION * 1.1f, SHADOWMAP_RESOLUTION * 1.1f);
        }

        // Shader
        mGroundShader = new GroundShader("extrusion_shadow_ground");
        mLightShader = new ExtrusionRenderer.Shader("extrusion_shadow_light");
        if (mRenderer.isMesh())
            mExtrusionShader = new Shader("extrusion_layer_mesh");
        else
            mExtrusionShader = new Shader("extrusion_layer_ext");

        mFrameBuffer = new ShadowFrameBuffer((int) SHADOWMAP_RESOLUTION, (int) SHADOWMAP_RESOLUTION);

        //mRenderer.setup(); // No need to setup, as shaders are taken from here

        return super.setup();
    }

    @Override
    public void update(GLViewport viewport) {
        mRenderer.update(viewport);
        setReady(mRenderer.isReady());
    }

    @Override
    public void render(GLViewport viewport) {
        /* Prepare rendering from lights view: */
        // Store vp-matrix temporarily and use vp-matrix as lightMat
        viewport.viewproj.get(mViewProjTmp);

        float projWidth = SHADOWMAP_RESOLUTION;
        float projHeight = SHADOWMAP_RESOLUTION;
        if (DEBUG) {
            projWidth *= (3. / 4);
            projHeight *= (3. / 4);
        }
        GLMatrix.orthoM(mOrthoMat, 0, -projWidth, projWidth, projHeight, -projHeight, -SHADOWMAP_RESOLUTION, SHADOWMAP_RESOLUTION);
        viewport.viewproj.set(mOrthoMat);

        // Rotate from light direction
        float[] lightPos = mRenderer.getSun().getPosition();
        float rot = (float) Math.acos(lightPos[2] / 1.) * MathUtils.radiansToDegrees;
        mRotTmp.setRotation(rot, 1f, 0, 0); // tilt
        viewport.viewproj.multiplyRhs(mRotTmp);

        rot = MathUtils.atan2(lightPos[0], lightPos[1]) * MathUtils.radiansToDegrees;
        mRotTmp.setRotation(rot, 0, 0, 1f); // bearing
        viewport.viewproj.multiplyRhs(mRotTmp);

        // DRAW SHADOW MAP
        {
            // START DEPTH FRAMEBUFFER
            mFrameBuffer.bindFrameBuffer();

            GLState.blend(false);  // depth cannot be transparent
            gl.depthMask(true);
            GLState.test(true, false);

            // Clear color (for gl20) and depth (for gl30)
            gl.clear(gl.COLOR_BUFFER_BIT | GL.DEPTH_BUFFER_BIT);

            // Draw GROUND shadow map (usually the ground does not cast any shadow)
            /*{
                mLightShader.useProgram();

                viewport.viewproj.setAsUniform(mLightShader.uMVP);
                gl.uniform1f(mLightShader.uAlpha, 1f);
                GLState.bindVertexBuffer(mGroundShadowQuad);
                GLState.enableVertexArrays(mLightShader.aPos, GLState.DISABLED);
                gl.vertexAttribPointer(mLightShader.aPos, 2, GL.FLOAT, false, 0, 0);
                MapRenderer.bindQuadIndicesVBO();

                gl.drawElements(GL.TRIANGLES, 6, GL.UNSIGNED_SHORT, 0);
            }*/

            // Draw EXTRUSION shadow map (in ExtrusionRenderer)
            {
                //ExtrusionRenderer.Shader tmpShader = mRenderer.getShader();
                mRenderer.setShader(mLightShader);
                mRenderer.useLight(false);
                mRenderer.render(viewport);
                //mRenderer.setShader(tmpShader);
            }

            // END DEPTH FRAMEBUFFER
            mFrameBuffer.unbindFrameBuffer();
        }
        mLightMat.copy(viewport.viewproj); // save lightMat
        mLightMat.multiplyLhs(texUnitConverter); // apply shadow map converter to mLightMat

        viewport.viewproj.set(mViewProjTmp); // write back stored vp-matrix

        // DRAW SCENE
        {
            int lightColor = mRenderer.getSun().getColor();
            GLState.test(false, false);
            gl.clear(GL.DEPTH_BUFFER_BIT);

            // Bind shadow map texture
            gl.activeTexture(gl.TEXTURE2);
            GLState.bindTex2D(mFrameBuffer.getShadowMap());

            // Draw GROUND
            {
                mGroundShader.useProgram();
                viewport.viewproj.setAsUniform(mGroundShader.uMVP);

                gl.uniform1i(mGroundShader.uShadowMap, 2); // TEXTURE2 for shadows
                GLUtils.setColor(mGroundShader.uLightColor, lightColor);
                gl.uniform1f(mGroundShader.uShadowRes, SHADOWMAP_RESOLUTION);
                mLightMat.setAsUniform(mGroundShader.uLightMvp);

                // Bind VBO
                GLState.bindVertexBuffer(mGroundQuad);
                GLState.enableVertexArrays(mGroundShader.aPos, GLState.DISABLED);
                gl.vertexAttribPointer(mGroundShader.aPos, 2, GL.FLOAT, false, 0, 0);
                MapRenderer.bindQuadIndicesVBO();
                GLState.blend(true);  // allow transparency
                gl.blendFunc(GL.ZERO, GL.SRC_COLOR); // multiply frame colors
                gl.drawElements(GL.TRIANGLES, 6, GL.UNSIGNED_SHORT, 0);
                GLState.blend(false);
                gl.blendFunc(GL.ONE, GL.ONE_MINUS_SRC_ALPHA); // Reset to default func
            }

            // Draw EXTRUSIONS (in ExtrusionRenderer)
            {
                //ExtrusionRenderer.Shader tmpShader = mRenderer.getShader();
                mExtrusionShader.useProgram();
                gl.uniform1i(mExtrusionShader.uShadowMap, 2); // TEXTURE2 for shadows
                GLUtils.setColor(mExtrusionShader.uLightColor, lightColor);
                gl.uniform1f(mExtrusionShader.uShadowRes, SHADOWMAP_RESOLUTION);

                mExtrusionShader.lightMat = mLightMat;
                mRenderer.setShader(mExtrusionShader);
                mRenderer.useLight(true);
                mRenderer.render(viewport);
                //mRenderer.setShader(tmpShader);
            }

            gl.activeTexture(GL.TEXTURE0); // reset active Texture

        }
    }
}
