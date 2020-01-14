/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.oscim.gdx;

import com.badlogic.gdx.utils.GdxRuntimeException;
import org.lwjgl.opengl.*;

import java.nio.*;

/**
 * See https://github.com/libgdx/libgdx/blob/master/backends/gdx-backend-lwjgl/src/com/badlogic/gdx/backends/lwjgl/LwjglGL30.java
 */
public class LwjglGL30 extends LwjglGL20 implements org.oscim.backend.GL30 {
    @Override
    public void readBuffer(int mode) {
        GL11.glReadBuffer(mode);
    }

    @Override
    public void drawRangeElements(int mode, int start, int end, int count, int type, Buffer indices) {
        if (indices instanceof ByteBuffer)
            GL12.glDrawRangeElements(mode, start, end, (ByteBuffer) indices);
        else if (indices instanceof ShortBuffer)
            GL12.glDrawRangeElements(mode, start, end, (ShortBuffer) indices);
        else if (indices instanceof IntBuffer)
            GL12.glDrawRangeElements(mode, start, end, (IntBuffer) indices);
        else throw new GdxRuntimeException("indices must be byte, short or int buffer");
    }

    @Override
    public void drawRangeElements(int mode, int start, int end, int count, int type, int offset) {
        GL12.glDrawRangeElements(mode, start, end, count, type, offset);
    }

    @Override
    public void texImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format,
                           int type, Buffer pixels) {
        if (pixels == null)
            GL12.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, (ByteBuffer) null);
        else if (pixels instanceof ByteBuffer)
            GL12.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, (ByteBuffer) pixels);
        else if (pixels instanceof ShortBuffer)
            GL12.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, (ShortBuffer) pixels);
        else if (pixels instanceof IntBuffer)
            GL12.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, (IntBuffer) pixels);
        else if (pixels instanceof FloatBuffer)
            GL12.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, (FloatBuffer) pixels);
        else if (pixels instanceof DoubleBuffer)
            GL12.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, (DoubleBuffer) pixels);
        else
            throw new GdxRuntimeException("Can't use " + pixels.getClass().getName()
                    + " with this method. Use ByteBuffer, ShortBuffer, IntBuffer, FloatBuffer or DoubleBuffer instead. Blame LWJGL");
    }

    @Override
    public void texImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format,
                           int type, int offset) {
        GL12.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, offset);
    }

    @Override
    public void texSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth,
                              int format, int type, Buffer pixels) {
        if (pixels instanceof ByteBuffer)
            GL12.glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, (ByteBuffer) pixels);
        else if (pixels instanceof ShortBuffer)
            GL12.glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, (ShortBuffer) pixels);
        else if (pixels instanceof IntBuffer)
            GL12.glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, (IntBuffer) pixels);
        else if (pixels instanceof FloatBuffer)
            GL12.glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, (FloatBuffer) pixels);
        else if (pixels instanceof DoubleBuffer)
            GL12.glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, (DoubleBuffer) pixels);
        else
            throw new GdxRuntimeException("Can't use " + pixels.getClass().getName()
                    + " with this method. Use ByteBuffer, ShortBuffer, IntBuffer, FloatBuffer or DoubleBuffer instead. Blame LWJGL");
    }

    @Override
    public void texSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth,
                              int format, int type, int offset) {
        GL12.glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, offset);
    }

    @Override
    public void copyTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int x, int y, int width,
                                  int height) {
        GL12.glCopyTexSubImage3D(target, level, xoffset, yoffset, zoffset, x, y, width, height);
    }

    @Override
    public void genQueries(int n, int[] ids, int offset) {
        for (int i = offset; i < offset + n; i++) {
            ids[i] = GL15.glGenQueries();
        }
    }

    @Override
    public void genQueries(int n, IntBuffer ids) {
        for (int i = 0; i < n; i++) {
            ids.put(GL15.glGenQueries());
        }
    }

    @Override
    public void deleteQueries(int n, int[] ids, int offset) {
        for (int i = offset; i < offset + n; i++) {
            GL15.glDeleteQueries(ids[i]);
        }
    }

    @Override
    public void deleteQueries(int n, IntBuffer ids) {
        for (int i = 0; i < n; i++) {
            GL15.glDeleteQueries(ids.get());
        }
    }

    @Override
    public boolean isQuery(int id) {
        return GL15.glIsQuery(id);
    }

    @Override
    public void beginQuery(int target, int id) {
        GL15.glBeginQuery(target, id);
    }

    @Override
    public void endQuery(int target) {
        GL15.glEndQuery(target);
    }

    @Override
    public void getQueryiv(int target, int pname, IntBuffer params) {
        GL15.glGetQuery(target, pname, params);
    }

    @Override
    public void getQueryObjectuiv(int id, int pname, IntBuffer params) {
        GL15.glGetQueryObjectu(id, pname, params);
    }

    @Override
    public boolean unmapBuffer(int target) {
        return GL15.glUnmapBuffer(target);
    }

    @Override
    public Buffer getBufferPointerv(int target, int pname) {
        return GL15.glGetBufferPointer(target, pname);
    }

    @Override
    public void drawBuffers(int n, IntBuffer bufs) {
        GL20.glDrawBuffers(bufs);
    }

    @Override
    public void uniformMatrix2x3fv(int location, int count, boolean transpose, FloatBuffer value) {
        GL21.glUniformMatrix2x3(location, transpose, value);
    }

    @Override
    public void uniformMatrix3x2fv(int location, int count, boolean transpose, FloatBuffer value) {
        GL21.glUniformMatrix3x2(location, transpose, value);
    }

    @Override
    public void uniformMatrix2x4fv(int location, int count, boolean transpose, FloatBuffer value) {
        GL21.glUniformMatrix2x4(location, transpose, value);
    }

    @Override
    public void uniformMatrix4x2fv(int location, int count, boolean transpose, FloatBuffer value) {
        GL21.glUniformMatrix4x2(location, transpose, value);
    }

    @Override
    public void uniformMatrix3x4fv(int location, int count, boolean transpose, FloatBuffer value) {
        GL21.glUniformMatrix3x4(location, transpose, value);
    }


    @Override
    public void uniformMatrix4x3fv(int location, int count, boolean transpose, FloatBuffer value) {
        GL21.glUniformMatrix4x3(location, transpose, value);
    }

    @Override
    public void blitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1,
                                int mask, int filter) {
        GL30.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
    }

    @Override
    public void bindFramebuffer(int target, int framebuffer) {
        GL30.glBindFramebuffer(target, framebuffer);
    }

    @Override
    public void bindRenderbuffer(int target, int renderbuffer) {
        GL30.glBindRenderbuffer(target, renderbuffer);
    }

    @Override
    public int checkFramebufferStatus(int target) {
        return GL30.glCheckFramebufferStatus(target);
    }

    @Override
    public void deleteFramebuffers(int n, IntBuffer framebuffers) {
        GL30.glDeleteFramebuffers(framebuffers);
    }

    @Override
    public void deleteFramebuffer(int framebuffer) {
        GL30.glDeleteFramebuffers(framebuffer);
    }

    @Override
    public void deleteRenderbuffers(int n, IntBuffer renderbuffers) {
        GL30.glDeleteRenderbuffers(renderbuffers);
    }

    @Override
    public void deleteRenderbuffer(int renderbuffer) {
        GL30.glDeleteRenderbuffers(renderbuffer);
    }

    @Override
    public void generateMipmap(int target) {
        GL30.glGenerateMipmap(target);
    }

    @Override
    public void genFramebuffers(int n, IntBuffer framebuffers) {
        GL30.glGenFramebuffers(framebuffers);
    }

    @Override
    public int genFramebuffer() {
        return GL30.glGenFramebuffers();
    }

    @Override
    public void genRenderbuffers(int n, IntBuffer renderbuffers) {
        GL30.glGenRenderbuffers(renderbuffers);
    }

    @Override
    public int genRenderbuffer() {
        return GL30.glGenRenderbuffers();
    }

    @Override
    public void getRenderbufferParameteriv(int target, int pname, IntBuffer params) {
        GL30.glGetRenderbufferParameter(target, pname, params);
    }

    @Override
    public boolean isFramebuffer(int framebuffer) {
        return GL30.glIsFramebuffer(framebuffer);
    }

    @Override
    public boolean isRenderbuffer(int renderbuffer) {
        return GL30.glIsRenderbuffer(renderbuffer);
    }

    @Override
    public void renderbufferStorage(int target, int internalformat, int width, int height) {
        GL30.glRenderbufferStorage(target, internalformat, width, height);
    }

    @Override
    public void renderbufferStorageMultisample(int target, int samples, int internalformat, int width, int height) {
        GL30.glRenderbufferStorageMultisample(target, samples, internalformat, width, height);
    }

    @Override
    public void framebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
        GL30.glFramebufferTexture2D(target, attachment, textarget, texture, level);
    }

    @Override
    public void framebufferRenderbuffer(int target, int attachment, int renderbuffertarget, int renderbuffer) {
        GL30.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
    }

    @Override
    public void framebufferTextureLayer(int target, int attachment, int texture, int level, int layer) {
        GL30.glFramebufferTextureLayer(target, attachment, texture, level, layer);
    }

    @Override
    public void flushMappedBufferRange(int target, int offset, int length) {
        GL30.glFlushMappedBufferRange(target, offset, length);
    }

    @Override
    public void bindVertexArray(int array) {
        GL30.glBindVertexArray(array);
    }

    @Override
    public void deleteVertexArrays(int n, int[] arrays, int offset) {
        for (int i = offset; i < offset + n; i++) {
            GL30.glDeleteVertexArrays(arrays[i]);
        }
    }

    @Override
    public void deleteVertexArrays(int n, IntBuffer arrays) {
        GL30.glDeleteVertexArrays(arrays);
    }

    @Override
    public void genVertexArrays(int n, int[] arrays, int offset) {
        for (int i = offset; i < offset + n; i++) {
            arrays[i] = GL30.glGenVertexArrays();
        }
    }

    @Override
    public void genVertexArrays(int n, IntBuffer arrays) {
        GL30.glGenVertexArrays(arrays);
    }

    @Override
    public boolean isVertexArray(int array) {
        return GL30.glIsVertexArray(array);
    }

    @Override
    public void beginTransformFeedback(int primitiveMode) {
        GL30.glBeginTransformFeedback(primitiveMode);
    }

    @Override
    public void endTransformFeedback() {
        GL30.glEndTransformFeedback();
    }

    @Override
    public void bindBufferRange(int target, int index, int buffer, int offset, int size) {
        GL30.glBindBufferRange(target, index, buffer, offset, size);
    }

    @Override
    public void bindBufferBase(int target, int index, int buffer) {
        GL30.glBindBufferBase(target, index, buffer);
    }

    @Override
    public void transformFeedbackVaryings(int program, String[] varyings, int bufferMode) {
        GL30.glTransformFeedbackVaryings(program, varyings, bufferMode);
    }

    @Override
    public void vertexAttribIPointer(int index, int size, int type, int stride, int offset) {
        GL30.glVertexAttribIPointer(index, size, type, stride, offset);
    }

    @Override
    public void getVertexAttribIiv(int index, int pname, IntBuffer params) {
        GL30.glGetVertexAttribI(index, pname, params);
    }

    @Override
    public void getVertexAttribIuiv(int index, int pname, IntBuffer params) {
        GL30.glGetVertexAttribIu(index, pname, params);
    }

    @Override
    public void vertexAttribI4i(int index, int x, int y, int z, int w) {
        GL30.glVertexAttribI4i(index, x, y, z, w);
    }

    @Override
    public void vertexAttribI4ui(int index, int x, int y, int z, int w) {
        GL30.glVertexAttribI4ui(index, x, y, z, w);
    }

    @Override
    public void getUniformuiv(int program, int location, IntBuffer params) {
        GL30.glGetUniformu(program, location, params);
    }

    @Override
    public int getFragDataLocation(int program, String name) {
        return GL30.glGetFragDataLocation(program, name);
    }

    @Override
    public void uniform1uiv(int location, int count, IntBuffer value) {
        GL30.glUniform1u(location, value);
    }

    @Override
    public void uniform3uiv(int location, int count, IntBuffer value) {
        GL30.glUniform3u(location, value);
    }

    @Override
    public void uniform4uiv(int location, int count, IntBuffer value) {
        GL30.glUniform4u(location, value);
    }

    @Override
    public void clearBufferiv(int buffer, int drawbuffer, IntBuffer value) {
        GL30.glClearBuffer(buffer, drawbuffer, value);
    }

    @Override
    public void clearBufferuiv(int buffer, int drawbuffer, IntBuffer value) {
        GL30.glClearBufferu(buffer, drawbuffer, value);
    }

    @Override
    public void clearBufferfv(int buffer, int drawbuffer, FloatBuffer value) {
        GL30.glClearBuffer(buffer, drawbuffer, value);
    }

    @Override
    public void clearBufferfi(int buffer, int drawbuffer, float depth, int stencil) {
        GL30.glClearBufferfi(buffer, drawbuffer, depth, stencil);
    }

    @Override
    public String getStringi(int name, int index) {
        return GL30.glGetStringi(name, index);
    }

    @Override
    public void copyBufferSubData(int readTarget, int writeTarget, int readOffset, int writeOffset, int size) {
        GL31.glCopyBufferSubData(readTarget, writeTarget, readOffset, writeOffset, size);
    }

    @Override
    public void getUniformIndices(int program, String[] uniformNames, IntBuffer uniformIndices) {
        GL31.glGetUniformIndices(program, uniformNames, uniformIndices);
    }

    @Override
    public void getActiveUniformsiv(int program, int uniformCount, IntBuffer uniformIndices, int pname, IntBuffer params) {
        GL31.glGetActiveUniforms(program, uniformIndices, pname, params);
    }

    @Override
    public int getUniformBlockIndex(int program, String uniformBlockName) {
        return GL31.glGetUniformBlockIndex(program, uniformBlockName);
    }

    @Override
    public void getActiveUniformBlockiv(int program, int uniformBlockIndex, int pname, IntBuffer params) {
        params.put(GL31.glGetActiveUniformBlocki(program, uniformBlockIndex, pname));
    }

    @Override
    public void getActiveUniformBlockName(int program, int uniformBlockIndex, Buffer length, Buffer uniformBlockName) {
        GL31.glGetActiveUniformBlockName(program, uniformBlockIndex, (IntBuffer) length, (ByteBuffer) uniformBlockName);
    }

    @Override
    public String getActiveUniformBlockName(int program, int uniformBlockIndex) {
        return GL31.glGetActiveUniformBlockName(program, uniformBlockIndex, 1024);
    }

    @Override
    public void uniformBlockBinding(int program, int uniformBlockIndex, int uniformBlockBinding) {
        GL31.glUniformBlockBinding(program, uniformBlockIndex, uniformBlockBinding);
    }

    @Override
    public void drawArraysInstanced(int mode, int first, int count, int instanceCount) {
        GL31.glDrawArraysInstanced(mode, first, count, instanceCount);
    }

    @Override
    public void drawElementsInstanced(int mode, int count, int type, int indicesOffset, int instanceCount) {
        GL31.glDrawElementsInstanced(mode, count, type, indicesOffset, instanceCount);

    }

    @Override
    public void getInteger64v(int pname, LongBuffer params) {
        GL32.glGetInteger64(pname, params);
    }

    @Override
    public void getBufferParameteri64v(int target, int pname, LongBuffer params) {
        params.put(GL32.glGetBufferParameteri64(target, pname));
    }

    @Override
    public void genSamplers(int count, int[] samplers, int offset) {
        for (int i = offset; i < offset + count; i++) {
            samplers[i] = GL33.glGenSamplers();
        }
    }

    @Override
    public void genSamplers(int count, IntBuffer samplers) {
        GL33.glGenSamplers(samplers);
    }

    @Override
    public void deleteSamplers(int count, int[] samplers, int offset) {
        for (int i = offset; i < offset + count; i++) {
            GL33.glDeleteSamplers(samplers[i]);
        }
    }

    @Override
    public void deleteSamplers(int count, IntBuffer samplers) {
        GL33.glDeleteSamplers(samplers);
    }

    @Override
    public boolean isSampler(int sampler) {
        return GL33.glIsSampler(sampler);
    }

    @Override
    public void bindSampler(int unit, int sampler) {
        GL33.glBindSampler(unit, sampler);
    }

    @Override
    public void samplerParameteri(int sampler, int pname, int param) {
        GL33.glSamplerParameteri(sampler, pname, param);
    }

    @Override
    public void samplerParameteriv(int sampler, int pname, IntBuffer param) {
        GL33.glSamplerParameter(sampler, pname, param);
    }

    @Override
    public void samplerParameterf(int sampler, int pname, float param) {
        GL33.glSamplerParameterf(sampler, pname, param);
    }

    @Override
    public void samplerParameterfv(int sampler, int pname, FloatBuffer param) {
        GL33.glSamplerParameter(sampler, pname, param);
    }

    @Override
    public void getSamplerParameteriv(int sampler, int pname, IntBuffer params) {
        GL33.glGetSamplerParameterI(sampler, pname, params);
    }

    @Override
    public void getSamplerParameterfv(int sampler, int pname, FloatBuffer params) {
        GL33.glGetSamplerParameter(sampler, pname, params);

    }

    @Override
    public void vertexAttribDivisor(int index, int divisor) {
        GL33.glVertexAttribDivisor(index, divisor);
    }

    @Override
    public void bindTransformFeedback(int target, int id) {
        GL40.glBindTransformFeedback(target, id);
    }

    @Override
    public void deleteTransformFeedbacks(int n, int[] ids, int offset) {
        for (int i = offset; i < offset + n; i++) {
            GL40.glDeleteTransformFeedbacks(ids[i]);
        }
    }

    @Override
    public void deleteTransformFeedbacks(int n, IntBuffer ids) {
        GL40.glDeleteTransformFeedbacks(ids);
    }

    @Override
    public void genTransformFeedbacks(int n, int[] ids, int offset) {
        for (int i = offset; i < offset + n; i++) {
            ids[i] = GL40.glGenTransformFeedbacks();
        }
    }

    @Override
    public void genTransformFeedbacks(int n, IntBuffer ids) {
        GL40.glGenTransformFeedbacks(ids);
    }

    @Override
    public boolean isTransformFeedback(int id) {
        return GL40.glIsTransformFeedback(id);
    }

    @Override
    public void pauseTransformFeedback() {
        GL40.glPauseTransformFeedback();
    }

    @Override
    public void resumeTransformFeedback() {
        GL40.glResumeTransformFeedback();
    }

    @Override
    public void programParameteri(int program, int pname, int value) {
        GL41.glProgramParameteri(program, pname, value);
    }

    @Override
    public void invalidateFramebuffer(int target, int numAttachments, IntBuffer attachments) {
        GL43.glInvalidateFramebuffer(target, attachments);
    }

    @Override
    public void invalidateSubFramebuffer(int target, int numAttachments, IntBuffer attachments, int x, int y, int width,
                                         int height) {
        GL43.glInvalidateSubFramebuffer(target, attachments, x, y, width, height);
    }
}
