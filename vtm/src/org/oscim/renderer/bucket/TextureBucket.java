/*
 * Copyright 2012, 2013 Hannes Janetzek
 * Copyright 2016 devemux86
 *
 * This file is part of the OpenScienceMap project (http://www.opensciencemap.org).
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
package org.oscim.renderer.bucket;

import org.oscim.backend.GL;
import org.oscim.renderer.GLShader;
import org.oscim.renderer.GLState;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.MapRenderer;
import org.oscim.renderer.bucket.TextureItem.TexturePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ShortBuffer;

import static org.oscim.backend.GLAdapter.gl;
import static org.oscim.renderer.MapRenderer.COORD_SCALE;
import static org.oscim.renderer.MapRenderer.MAX_INDICES;

public class TextureBucket extends RenderBucket {

    static final Logger log = LoggerFactory.getLogger(TextureBucket.class);

    public final static int INDICES_PER_SPRITE = 6;
    final static int VERTICES_PER_SPRITE = 4;
    final static int SHORTS_PER_VERTICE = 6;

    public final static int TEXTURE_HEIGHT = 256;
    public final static int TEXTURE_WIDTH = 1024;
    final static int POOL_FILL = 4;

    /**
     * pool shared by TextLayers
     */
    public final static TexturePool pool = new TexturePool(POOL_FILL,
            TEXTURE_WIDTH,
            TEXTURE_HEIGHT,
            false);

    public TextureBucket(byte type) {
        super(type, false, true);
    }

    /**
     * holds textures and offset in vbo
     */
    public TextureItem textures;

    /**
     * scale mode
     */
    public boolean fixed;

    @Override
    protected void compile(ShortBuffer vboData, ShortBuffer iboData) {

        for (TextureItem t = textures; t != null; t = t.next)
            t.upload();

        /* add vertices to vbo */
        compileVertexItems(vboData);
    }

    protected void clear() {
        while (textures != null)
            textures = textures.dispose();
        super.clear();
    }

    static class Shader extends GLShader {
        int uMV, uProj, uScale, uCoordScale, uTexSize, aPos, aTexCoord;

        Shader() {
            if (!create("texture_layer"))
                return;

            uMV = getUniform("u_mv");
            uProj = getUniform("u_proj");
            uScale = getUniform("u_scale");
            uCoordScale = getUniform("u_coord_scale");
            uTexSize = getUniform("u_div");
            aPos = getAttrib("vertex");
            aTexCoord = getAttrib("tex_coord");
        }

        @Override
        public boolean useProgram() {
            if (super.useProgram()) {
                GLState.enableVertexArrays(aPos, aTexCoord);
                return true;
            }
            return false;
        }
    }

    static Shader shader;

    public static final class Renderer {

        static void init() {
            shader = new Shader();

            /* FIXME pool should be disposed on exit... */
            pool.init(0);
        }

        public static RenderBucket draw(RenderBucket b, GLViewport v, float scale) {

            GLState.test(false, false);
            GLState.blend(true);

            shader.useProgram();

            TextureBucket tb = (TextureBucket) b;
            gl.uniform1f(shader.uScale, tb.fixed ? 1 / scale : 1);
            gl.uniform1f(shader.uCoordScale, COORD_SCALE);

            v.proj.setAsUniform(shader.uProj);
            v.mvp.setAsUniform(shader.uMV);

            MapRenderer.bindQuadIndicesVBO();

            for (TextureItem t = tb.textures; t != null; t = t.next) {
                gl.uniform2f(shader.uTexSize,
                        1f / (t.width * COORD_SCALE),
                        1f / (t.height * COORD_SCALE));
                t.bind();

                /* draw up to maxVertices in each iteration */
                for (int i = 0; i < t.indices; i += MAX_INDICES) {
                    /* to.offset * (24(shorts) * 2(short-bytes)
                     * / 6(indices) == 8) */
                    int off = (t.offset + i) * 8 + tb.vertexOffset;

                    int numIndices = t.indices - i;
                    if (numIndices > MAX_INDICES)
                        numIndices = MAX_INDICES;

                    tb.render(off, numIndices);
                }
            }

            return b.next;
        }
    }

    public TextureItem getTextures() {
        return textures;
    }

    public void render(int offset, int numIndices) {
        gl.vertexAttribPointer(shader.aPos, 4, GL.SHORT,
                false, 12, offset);

        gl.vertexAttribPointer(shader.aTexCoord, 2, GL.SHORT,
                false, 12, offset + 8);

        gl.drawElements(GL.TRIANGLES, numIndices,
                GL.UNSIGNED_SHORT, 0);
    }
}
