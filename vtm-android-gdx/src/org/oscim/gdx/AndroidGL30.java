/*
 * Copyright 2018 Gustl22
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

/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.oscim.gdx;

import android.annotation.SuppressLint;
import android.opengl.GLES30;

import org.oscim.backend.GL3;

/**
 * See: https://github.com/libgdx/libgdx/blob/master/backends/gdx-backend-android/src/com/badlogic/gdx/backends/android/AndroidGL30.java
 */
@SuppressLint("NewApi")
public class AndroidGL30 extends AndroidGL implements GL3 {
    @Override
    public void readBuffer(int mode) {
        GLES30.glReadBuffer(mode);
    }

    @Override
    public void drawRangeElements(int mode, int start, int end, int count, int type, java.nio.Buffer indices) {
        GLES30.glDrawRangeElements(mode, start, end, count, type, indices);
    }

    @Override
    public void drawRangeElements(int mode, int start, int end, int count, int type, int offset) {
        GLES30.glDrawRangeElements(mode, start, end, count, type, offset);
    }

    @Override
    public void texImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format,
                           int type, java.nio.Buffer pixels) {
        if (pixels == null)
            GLES30.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, 0);
        else
            GLES30.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, pixels);
    }

    @Override
    public void texImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format,
                           int type, int offset) {
        GLES30.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, offset);
    }

    @Override
    public void texSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth,
                              int format, int type, java.nio.Buffer pixels) {
        GLES30.glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, pixels);
    }

    @Override
    public void texSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth,
                              int format, int type, int offset) {
        GLES30.glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, offset);
    }

    @Override
    public void copyTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int x, int y, int width,
                                  int height) {
        GLES30.glCopyTexSubImage3D(target, level, xoffset, yoffset, zoffset, x, y, width, height);
    }

//
// @Override
// public void compressedTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int
// imageSize, java.nio.Buffer data) {
// GLES30.glCompressedTexImage3D(target, level, internalformat, width, height, depth, border, imageSize, data);
// }
//
// @Override
// public void compressedTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int
// imageSize, int offset) {
// GLES30.glCompressedTexImage3D(target, level, internalformat, width, height, depth, border, imageSize, offset);
// }
//
// @Override
// public void compressedTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int
// depth, int format, int imageSize, java.nio.Buffer data) {
// GLES30.glCompressedTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, imageSize, data);
// }
//
// @Override
// public void compressedTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int
// depth, int format, int imageSize, int offset) {
// GLES30.glCompressedTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, imageSize, offset);
// }

    @Override
    public void genQueries(int n, int[] ids, int offset) {
        GLES30.glGenQueries(n, ids, offset);
    }

    @Override
    public void genQueries(int n, java.nio.IntBuffer ids) {
        GLES30.glGenQueries(n, ids);
    }

    @Override
    public void deleteQueries(int n, int[] ids, int offset) {
        GLES30.glDeleteQueries(n, ids, offset);
    }

    @Override
    public void deleteQueries(int n, java.nio.IntBuffer ids) {
        GLES30.glDeleteQueries(n, ids);
    }

    @Override
    public boolean isQuery(int id) {
        return GLES30.glIsQuery(id);
    }

    @Override
    public void beginQuery(int target, int id) {
        GLES30.glBeginQuery(target, id);
    }

    @Override
    public void endQuery(int target) {
        GLES30.glEndQuery(target);
    }

// @Override
// public void getQueryiv(int target, int pname, int[] params, int offset) {
// GLES30.glGetQueryiv(target, pname, params, offset);
// }

    @Override
    public void getQueryiv(int target, int pname, java.nio.IntBuffer params) {
        GLES30.glGetQueryiv(target, pname, params);
    }

// @Override
// public void getQueryObjectuiv(int id, int pname, int[] params, int offset) {
// GLES30.glGetQueryObjectuiv(id, pname, params, offset);
// }

    @Override
    public void getQueryObjectuiv(int id, int pname, java.nio.IntBuffer params) {
        GLES30.glGetQueryObjectuiv(id, pname, params);
    }

    @Override
    public boolean unmapBuffer(int target) {
        return GLES30.glUnmapBuffer(target);
    }

    @Override
    public java.nio.Buffer getBufferPointerv(int target, int pname) {
        return GLES30.glGetBufferPointerv(target, pname);
    }

// @Override
// public void drawBuffers(int n, int[] bufs, int offset) {
// GLES30.glDrawBuffers(n, bufs, offset);
// }

    @Override
    public void drawBuffers(int n, java.nio.IntBuffer bufs) {
        GLES30.glDrawBuffers(n, bufs);
    }

// @Override
// public void uniformMatrix2x3fv(int location, int count, boolean transpose, float[] value, int offset) {
// GLES30.glUniformMatrix2x3fv(location, count, transpose, value, offset);
// }

    @Override
    public void uniformMatrix2x3fv(int location, int count, boolean transpose, java.nio.FloatBuffer value) {
        GLES30.glUniformMatrix2x3fv(location, count, transpose, value);
    }

// @Override
// public void uniformMatrix3x2fv(int location, int count, boolean transpose, float[] value, int offset) {
// GLES30.glUniformMatrix3x2fv(location, count, transpose, value, offset);
// }

    @Override
    public void uniformMatrix3x2fv(int location, int count, boolean transpose, java.nio.FloatBuffer value) {
        GLES30.glUniformMatrix3x2fv(location, count, transpose, value);
    }

// @Override
// public void uniformMatrix2x4fv(int location, int count, boolean transpose, float[] value, int offset) {
// GLES30.glUniformMatrix2x4fv(location, count, transpose, value, offset);
// }

    @Override
    public void uniformMatrix2x4fv(int location, int count, boolean transpose, java.nio.FloatBuffer value) {
        GLES30.glUniformMatrix2x4fv(location, count, transpose, value);
    }

// @Override
// public void uniformMatrix4x2fv(int location, int count, boolean transpose, float[] value, int offset) {
// GLES30.glUniformMatrix4x2fv(location, count, transpose, value, offset);
// }

    @Override
    public void uniformMatrix4x2fv(int location, int count, boolean transpose, java.nio.FloatBuffer value) {
        GLES30.glUniformMatrix4x2fv(location, count, transpose, value);
    }

// @Override
// public void uniformMatrix3x4fv(int location, int count, boolean transpose, float[] value, int offset) {
// GLES30.glUniformMatrix3x4fv(location, count, transpose, value, offset);
// }

    @Override
    public void uniformMatrix3x4fv(int location, int count, boolean transpose, java.nio.FloatBuffer value) {
        GLES30.glUniformMatrix3x4fv(location, count, transpose, value);
    }

// @Override
// public void uniformMatrix4x3fv(int location, int count, boolean transpose, float[] value, int offset) {
// GLES30.glUniformMatrix4x3fv(location, count, transpose, value, offset);
// }

    @Override
    public void uniformMatrix4x3fv(int location, int count, boolean transpose, java.nio.FloatBuffer value) {
        GLES30.glUniformMatrix4x3fv(location, count, transpose, value);
    }

    @Override
    public void blitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1,
                                int mask, int filter) {
        GLES30.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
    }

    @Override
    public void renderbufferStorageMultisample(int target, int samples, int internalformat, int width, int height) {
        GLES30.glRenderbufferStorageMultisample(target, samples, internalformat, width, height);
    }

    @Override
    public void framebufferTextureLayer(int target, int attachment, int texture, int level, int layer) {
        GLES30.glFramebufferTextureLayer(target, attachment, texture, level, layer);
    }

// @Override
// public java.nio.Buffer mapBufferRange(int target, int offset, int length, int access) {
// return GLES30.glMapBufferRange(target, offset, length, access);
// }

    @Override
    public void flushMappedBufferRange(int target, int offset, int length) {
        GLES30.glFlushMappedBufferRange(target, offset, length);
    }

    @Override
    public void bindVertexArray(int array) {
        GLES30.glBindVertexArray(array);
    }

    @Override
    public void deleteVertexArrays(int n, int[] arrays, int offset) {
        GLES30.glDeleteVertexArrays(n, arrays, offset);
    }

    @Override
    public void deleteVertexArrays(int n, java.nio.IntBuffer arrays) {
        GLES30.glDeleteVertexArrays(n, arrays);
    }

    @Override
    public void genVertexArrays(int n, int[] arrays, int offset) {
        GLES30.glGenVertexArrays(n, arrays, offset);
    }

    @Override
    public void genVertexArrays(int n, java.nio.IntBuffer arrays) {
        GLES30.glGenVertexArrays(n, arrays);
    }

    @Override
    public boolean isVertexArray(int array) {
        return GLES30.glIsVertexArray(array);
    }

// @Override
// public void getIntegeri_v(int target, int index, int[] data, int offset) {
// GLES30.glGetIntegeri_v(target, index, data, offset);
// }

// @Override
// public void getIntegeri_v(int target, int index, java.nio.IntBuffer data) {
// GLES30.glGetIntegeri_v(target, index, data);
// }

    @Override
    public void beginTransformFeedback(int primitiveMode) {
        GLES30.glBeginTransformFeedback(primitiveMode);
    }

    @Override
    public void endTransformFeedback() {
        GLES30.glEndTransformFeedback();
    }

    @Override
    public void bindBufferRange(int target, int index, int buffer, int offset, int size) {
        GLES30.glBindBufferRange(target, index, buffer, offset, size);
    }

    @Override
    public void bindBufferBase(int target, int index, int buffer) {
        GLES30.glBindBufferBase(target, index, buffer);
    }

    @Override
    public void transformFeedbackVaryings(int program, String[] varyings, int bufferMode) {
        GLES30.glTransformFeedbackVaryings(program, varyings, bufferMode);
    }

// @Override
// public void getTransformFeedbackVarying(int program, int index, int bufsize, int[] length, int lengthOffset, int[] size, int
// sizeOffset, int[] type, int typeOffset, byte[] name, int nameOffset) {
// GLES30.glGetTransformFeedbackVarying(program, index, bufsize, length, lengthOffset, size, sizeOffset, type, typeOffset, name,
// nameOffset);
// }

// @Override
// public void getTransformFeedbackVarying(int program, int index, int bufsize, java.nio.IntBuffer length, java.nio.IntBuffer
// size, java.nio.IntBuffer type, byte name) {
// GLES30.glGetTransformFeedbackVarying(program, index, bufsize, length, size, type, name);
// }
//
// @Override
// public String getTransformFeedbackVarying(int program, int index, int[] size, int sizeOffset, int[] type, int typeOffset) {
// return GLES30.glGetTransformFeedbackVarying(program, index, size, sizeOffset, type, typeOffset);
// }
//
// @Override
// public String getTransformFeedbackVarying(int program, int index, java.nio.IntBuffer size, java.nio.IntBuffer type) {
// return GLES30.glGetTransformFeedbackVarying(program, index, size, type);
// }

    @Override
    public void vertexAttribIPointer(int index, int size, int type, int stride, int offset) {
        GLES30.glVertexAttribIPointer(index, size, type, stride, offset);
    }

// @Override
// public void getVertexAttribIiv(int index, int pname, int[] params, int offset) {
// GLES30.glGetVertexAttribIiv(index, pname, params, offset);
// }

    @Override
    public void getVertexAttribIiv(int index, int pname, java.nio.IntBuffer params) {
        GLES30.glGetVertexAttribIiv(index, pname, params);
    }

// @Override
// public void getVertexAttribIuiv(int index, int pname, int[] params, int offset) {
// GLES30.glGetVertexAttribIuiv(index, pname, params, offset);
// }

    @Override
    public void getVertexAttribIuiv(int index, int pname, java.nio.IntBuffer params) {
        GLES30.glGetVertexAttribIuiv(index, pname, params);
    }

    @Override
    public void vertexAttribI4i(int index, int x, int y, int z, int w) {
        GLES30.glVertexAttribI4i(index, x, y, z, w);
    }

    @Override
    public void vertexAttribI4ui(int index, int x, int y, int z, int w) {
        GLES30.glVertexAttribI4ui(index, x, y, z, w);
    }

// @Override
// public void vertexAttribI4iv(int index, int[] v, int offset) {
// GLES30.glVertexAttribI4iv(index, v, offset);
// }
//
// @Override
// public void vertexAttribI4iv(int index, java.nio.IntBuffer v) {
// GLES30.glVertexAttribI4iv(index, v);
// }
//
// @Override
// public void vertexAttribI4uiv(int index, int[] v, int offset) {
// GLES30.glVertexAttribI4uiv(index, v, offset);
// }
//
// @Override
// public void vertexAttribI4uiv(int index, java.nio.IntBuffer v) {
// GLES30.glVertexAttribI4uiv(index, v);
// }
//
// @Override
// public void getUniformuiv(int program, int location, int[] params, int offset) {
// GLES30.glGetUniformuiv(program, location, params, offset);
// }

    @Override
    public void getUniformuiv(int program, int location, java.nio.IntBuffer params) {
        GLES30.glGetUniformuiv(program, location, params);
    }

    @Override
    public int getFragDataLocation(int program, String name) {
        return GLES30.glGetFragDataLocation(program, name);
    }

// @Override
// public void uniform1ui(int location, int v0) {
// GLES30.glUniform1ui(location, v0);
// }
//
// @Override
// public void uniform2ui(int location, int v0, int v1) {
// GLES30.glUniform2ui(location, v0, v1);
// }
//
// @Override
// public void uniform3ui(int location, int v0, int v1, int v2) {
// GLES30.glUniform3ui(location, v0, v1, v2);
// }

// @Override
// public void uniform4ui(int location, int v0, int v1, int v2, int v3) {
// GLES30.glUniform4ui(location, v0, v1, v2, v3);
// }
//
// @Override
// public void uniform1uiv(int location, int count, int[] value, int offset) {
// GLES30.glUniform1uiv(location, count, value, offset);
// }

    @Override
    public void uniform1uiv(int location, int count, java.nio.IntBuffer value) {
        GLES30.glUniform1uiv(location, count, value);
    }

// @Override
// public void uniform2uiv(int location, int count, int[] value, int offset) {
// GLES30.glUniform2uiv(location, count, value, offset);
// }
//
// @Override
// public void uniform2uiv(int location, int count, java.nio.IntBuffer value) {
// GLES30.glUniform2uiv(location, count, value);
// }
//
// @Override
// public void uniform3uiv(int location, int count, int[] value, int offset) {
// GLES30.glUniform3uiv(location, count, value, offset);
// }

    @Override
    public void uniform3uiv(int location, int count, java.nio.IntBuffer value) {
        GLES30.glUniform3uiv(location, count, value);
    }

// @Override
// public void uniform4uiv(int location, int count, int[] value, int offset) {
// GLES30.glUniform4uiv(location, count, value, offset);
// }

    @Override
    public void uniform4uiv(int location, int count, java.nio.IntBuffer value) {
        GLES30.glUniform4uiv(location, count, value);
    }

// @Override
// public void clearBufferiv(int buffer, int drawbuffer, int[] value, int offset) {
// GLES30.glClearBufferiv(buffer, drawbuffer, value, offset);
// }

    @Override
    public void clearBufferiv(int buffer, int drawbuffer, java.nio.IntBuffer value) {
        GLES30.glClearBufferiv(buffer, drawbuffer, value);
    }

// @Override
// public void clearBufferuiv(int buffer, int drawbuffer, int[] value, int offset) {
// GLES30.glClearBufferuiv(buffer, drawbuffer, value, offset);
// }

    @Override
    public void clearBufferuiv(int buffer, int drawbuffer, java.nio.IntBuffer value) {
        GLES30.glClearBufferuiv(buffer, drawbuffer, value);
    }

//
// @Override
// public void clearBufferfv(int buffer, int drawbuffer, float[] value, int offset) {
// GLES30.glClearBufferfv(buffer, drawbuffer, value, offset);
// }

    @Override
    public void clearBufferfv(int buffer, int drawbuffer, java.nio.FloatBuffer value) {
        GLES30.glClearBufferfv(buffer, drawbuffer, value);
    }

    @Override
    public void clearBufferfi(int buffer, int drawbuffer, float depth, int stencil) {
        GLES30.glClearBufferfi(buffer, drawbuffer, depth, stencil);
    }

    @Override
    public String getStringi(int name, int index) {
        return GLES30.glGetStringi(name, index);
    }

    @Override
    public void copyBufferSubData(int readTarget, int writeTarget, int readOffset, int writeOffset, int size) {
        GLES30.glCopyBufferSubData(readTarget, writeTarget, readOffset, writeOffset, size);
    }

//
// @Override
// public void getUniformIndices(int program, String[] uniformNames, int[] uniformIndices, int uniformIndicesOffset) {
// GLES30.glGetUniformIndices(program, uniformNames, uniformIndices, uniformIndicesOffset);
// }

    @Override
    public void getUniformIndices(int program, String[] uniformNames, java.nio.IntBuffer uniformIndices) {
        GLES30.glGetUniformIndices(program, uniformNames, uniformIndices);
    }

// @Override
// public void getActiveUniformsiv(int program, int uniformCount, int[] uniformIndices, int uniformIndicesOffset, int pname,
// int[] params, int paramsOffset) {
// GLES30.glGetActiveUniformsiv(program, uniformCount, uniformIndices, uniformIndicesOffset, pname, params, paramsOffset);
// }

    @Override
    public void getActiveUniformsiv(int program, int uniformCount, java.nio.IntBuffer uniformIndices, int pname,
                                    java.nio.IntBuffer params) {
        GLES30.glGetActiveUniformsiv(program, uniformCount, uniformIndices, pname, params);
    }

    @Override
    public int getUniformBlockIndex(int program, String uniformBlockName) {
        return GLES30.glGetUniformBlockIndex(program, uniformBlockName);
    }

//
// @Override
// public void getActiveUniformBlockiv(int program, int uniformBlockIndex, int pname, int[] params, int offset) {
// GLES30.glGetActiveUniformBlockiv(program, uniformBlockIndex, pname, params, offset);
// }

    @Override
    public void getActiveUniformBlockiv(int program, int uniformBlockIndex, int pname, java.nio.IntBuffer params) {
        GLES30.glGetActiveUniformBlockiv(program, uniformBlockIndex, pname, params);
    }

//
// @Override
// public void getActiveUniformBlockName(int program, int uniformBlockIndex, int bufSize, int[] length, int lengthOffset, byte[]
// uniformBlockName, int uniformBlockNameOffset) {
// GLES30.glGetActiveUniformBlockName(program, uniformBlockIndex, bufSize, length, lengthOffset, uniformBlockName,
// uniformBlockNameOffset);
// }

    @Override
    public void getActiveUniformBlockName(int program, int uniformBlockIndex, java.nio.Buffer length,
                                          java.nio.Buffer uniformBlockName) {
        GLES30.glGetActiveUniformBlockName(program, uniformBlockIndex, length, uniformBlockName);
    }

    @Override
    public String getActiveUniformBlockName(int program, int uniformBlockIndex) {
        return GLES30.glGetActiveUniformBlockName(program, uniformBlockIndex);
    }

    @Override
    public void uniformBlockBinding(int program, int uniformBlockIndex, int uniformBlockBinding) {
        GLES30.glUniformBlockBinding(program, uniformBlockIndex, uniformBlockBinding);
    }

    @Override
    public void drawArraysInstanced(int mode, int first, int count, int instanceCount) {
        GLES30.glDrawArraysInstanced(mode, first, count, instanceCount);
    }

// @Override
// public void drawElementsInstanced(int mode, int count, int type, java.nio.Buffer indices, int instanceCount) {
// GLES30.glDrawElementsInstanced(mode, count, type, indices, instanceCount);
// }

    @Override
    public void drawElementsInstanced(int mode, int count, int type, int indicesOffset, int instanceCount) {
        GLES30.glDrawElementsInstanced(mode, count, type, indicesOffset, instanceCount);
    }

// @Override
// public long fenceSync(int condition, int flags) {
// return GLES30.glFenceSync(condition, flags);
// }
//
// @Override
// public boolean isSync(long sync) {
// return GLES30.glIsSync(sync);
// }
//
// @Override
// public void deleteSync(long sync) {
// GLES30.glDeleteSync(sync);
// }
//
// @Override
// public int clientWaitSync(long sync, int flags, long timeout) {
// return GLES30.glClientWaitSync(sync, flags, timeout);
// }

// @Override
// public void waitSync(long sync, int flags, long timeout) {
// GLES30.glWaitSync(sync, flags, timeout);
// }
//
// @Override
// public void getInteger64v(int pname, long[] params, int offset) {
// GLES30.glGetInteger64v(pname, params, offset);
// }

    @Override
    public void getInteger64v(int pname, java.nio.LongBuffer params) {
        GLES30.glGetInteger64v(pname, params);
    }

// @Override
// public void getSynciv(long sync, int pname, int bufSize, int[] length, int lengthOffset, int[] values, int valuesOffset) {
// GLES30.glGetSynciv(sync, pname, bufSize, length, lengthOffset, values, valuesOffset);
// }
//
// @Override
// public void getSynciv(long sync, int pname, int bufSize, java.nio.IntBuffer length, java.nio.IntBuffer values) {
// GLES30.glGetSynciv(sync, pname, bufSize, length, values);
// }
//
// @Override
// public void getInteger64i_v(int target, int index, long[] data, int offset) {
// GLES30.glGetInteger64i_v(target, index, data, offset);
// }
//
// @Override
// public void getInteger64i_v(int target, int index, java.nio.LongBuffer data) {
// GLES30.glGetInteger64i_v(target, index, data);
// }
//
// @Override
// public void getBufferParameteri64v(int target, int pname, long[] params, int offset) {
// GLES30.glGetBufferParameteri64v(target, pname, params, offset);
// }

    @Override
    public void getBufferParameteri64v(int target, int pname, java.nio.LongBuffer params) {
        GLES30.glGetBufferParameteri64v(target, pname, params);
    }

    @Override
    public void genSamplers(int count, int[] samplers, int offset) {
        GLES30.glGenSamplers(count, samplers, offset);
    }

    @Override
    public void genSamplers(int count, java.nio.IntBuffer samplers) {
        GLES30.glGenSamplers(count, samplers);
    }

    @Override
    public void deleteSamplers(int count, int[] samplers, int offset) {
        GLES30.glDeleteSamplers(count, samplers, offset);
    }

    @Override
    public void deleteSamplers(int count, java.nio.IntBuffer samplers) {
        GLES30.glDeleteSamplers(count, samplers);
    }

    @Override
    public boolean isSampler(int sampler) {
        return GLES30.glIsSampler(sampler);
    }

    @Override
    public void bindSampler(int unit, int sampler) {
        GLES30.glBindSampler(unit, sampler);
    }

    @Override
    public void samplerParameteri(int sampler, int pname, int param) {
        GLES30.glSamplerParameteri(sampler, pname, param);
    }

//
// @Override
// public void samplerParameteriv(int sampler, int pname, int[] param, int offset) {
// GLES30.glSamplerParameteriv(sampler, pname, param, offset);
// }

    @Override
    public void samplerParameteriv(int sampler, int pname, java.nio.IntBuffer param) {
        GLES30.glSamplerParameteriv(sampler, pname, param);
    }

    @Override
    public void samplerParameterf(int sampler, int pname, float param) {
        GLES30.glSamplerParameterf(sampler, pname, param);
    }

// @Override
// public void samplerParameterfv(int sampler, int pname, float[] param, int offset) {
// GLES30.glSamplerParameterfv(sampler, pname, param, offset);
// }

    @Override
    public void samplerParameterfv(int sampler, int pname, java.nio.FloatBuffer param) {
        GLES30.glSamplerParameterfv(sampler, pname, param);
    }

//
// @Override
// public void getSamplerParameteriv(int sampler, int pname, int[] params, int offset) {
// GLES30.glGetSamplerParameteriv(sampler, pname, params, offset);
// }

    @Override
    public void getSamplerParameteriv(int sampler, int pname, java.nio.IntBuffer params) {
        GLES30.glGetSamplerParameteriv(sampler, pname, params);
    }

// @Override
// public void getSamplerParameterfv(int sampler, int pname, float[] params, int offset) {
// GLES30.glGetSamplerParameterfv(sampler, pname, params, offset);
// }

    @Override
    public void getSamplerParameterfv(int sampler, int pname, java.nio.FloatBuffer params) {
        GLES30.glGetSamplerParameterfv(sampler, pname, params);
    }

    @Override
    public void vertexAttribDivisor(int index, int divisor) {
        GLES30.glVertexAttribDivisor(index, divisor);
    }

    @Override
    public void bindTransformFeedback(int target, int id) {
        GLES30.glBindTransformFeedback(target, id);
    }

    @Override
    public void deleteTransformFeedbacks(int n, int[] ids, int offset) {
        GLES30.glDeleteTransformFeedbacks(n, ids, offset);
    }

    @Override
    public void deleteTransformFeedbacks(int n, java.nio.IntBuffer ids) {
        GLES30.glDeleteTransformFeedbacks(n, ids);
    }

    @Override
    public void genTransformFeedbacks(int n, int[] ids, int offset) {
        GLES30.glGenTransformFeedbacks(n, ids, offset);
    }

    @Override
    public void genTransformFeedbacks(int n, java.nio.IntBuffer ids) {
        GLES30.glGenTransformFeedbacks(n, ids);
    }

    @Override
    public boolean isTransformFeedback(int id) {
        return GLES30.glIsTransformFeedback(id);
    }

    @Override
    public void pauseTransformFeedback() {
        GLES30.glPauseTransformFeedback();
    }

    @Override
    public void resumeTransformFeedback() {
        GLES30.glResumeTransformFeedback();
    }

// @Override
// public void getProgramBinary(int program, int bufSize, int[] length, int lengthOffset, int[] binaryFormat, int
// binaryFormatOffset, java.nio.Buffer binary) {
// GLES30.glGetProgramBinary(program, bufSize, length, lengthOffset, binaryFormat, binaryFormatOffset, binary);
// }
//
// @Override
// public void getProgramBinary(int program, int bufSize, java.nio.IntBuffer length, java.nio.IntBuffer binaryFormat,
// java.nio.Buffer binary) {
// GLES30.glGetProgramBinary(program, bufSize, length, binaryFormat, binary);
// }

// @Override
// public void programBinary(int program, int binaryFormat, java.nio.Buffer binary, int length) {
// GLES30.glProgramBinary(program, binaryFormat, binary, length);
// }

    @Override
    public void programParameteri(int program, int pname, int value) {
        GLES30.glProgramParameteri(program, pname, value);
    }

// @Override
// public void invalidateFramebuffer(int target, int numAttachments, int[] attachments, int offset) {
// GLES30.glInvalidateFramebuffer(target, numAttachments, attachments, offset);
// }

    @Override
    public void invalidateFramebuffer(int target, int numAttachments, java.nio.IntBuffer attachments) {
        GLES30.glInvalidateFramebuffer(target, numAttachments, attachments);
    }

//
// @Override
// public void invalidateSubFramebuffer(int target, int numAttachments, int[] attachments, int offset, int x, int y, int width,
// int height) {
// GLES30.glInvalidateSubFramebuffer(target, numAttachments, attachments, offset, x, y, width, height);
// }

    @Override
    public void invalidateSubFramebuffer(int target, int numAttachments, java.nio.IntBuffer attachments, int x, int y,
                                         int width, int height) {
        GLES30.glInvalidateSubFramebuffer(target, numAttachments, attachments, x, y, width, height);
    }

// @Override
// public void texStorage2D(int target, int levels, int internalformat, int width, int height) {
// GLES30.glTexStorage2D(target, levels, internalformat, width, height);
// }

// @Override
// public void texStorage3D(int target, int levels, int internalformat, int width, int height, int depth) {
// GLES30.glTexStorage3D(target, levels, internalformat, width, height, depth);
// }
//
// @Override
// public void getInternalformativ(int target, int internalformat, int pname, int bufSize, int[] params, int offset) {
// GLES30.glGetInternalformativ(target, internalformat, pname, bufSize, params, offset);
// }
//
// @Override
// public void getInternalformativ(int target, int internalformat, int pname, int bufSize, java.nio.IntBuffer params) {
// GLES30.glGetInternalformativ(target, internalformat, pname, bufSize, params);
// }
}