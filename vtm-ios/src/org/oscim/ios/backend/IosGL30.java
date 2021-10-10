/*
 * Copyright 2019 Gustl22
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
package org.oscim.ios.backend;

import com.badlogic.gdx.backends.iosrobovm.IOSGLES30;
import org.oscim.backend.GL30;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

/**
 * iOS specific implementation of {@link GL30}.
 */
@SuppressWarnings("deprecation")
public class IosGL30 extends IosGL implements GL30 {

    private static final IOSGLES30 iOSGL = new IOSGLES30();

    @Override
    public void readBuffer(int mode) {
        iOSGL.glReadBuffer(mode);
    }

    @Override
    public void drawRangeElements(int mode, int start, int end, int count, int type, Buffer indices) {
        iOSGL.glDrawRangeElements(mode, start, end, count, type, indices);
    }

    @Override
    public void drawRangeElements(int mode, int start, int end, int count, int type, int offset) {
        iOSGL.glDrawRangeElements(mode, start, end, count, type, offset);
    }

    @Override
    public void texImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, Buffer pixels) {
        iOSGL.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, pixels);
    }

    @Override
    public void texImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, int offset) {
        iOSGL.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, offset);
    }

    @Override
    public void texSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, Buffer pixels) {
        iOSGL.glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, pixels);
    }

    @Override
    public void texSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, int offset) {
        iOSGL.glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, offset);
    }

    @Override
    public void copyTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int x, int y, int width, int height) {
        iOSGL.glCopyTexSubImage3D(target, level, xoffset, yoffset, zoffset, x, y, width, height);
    }

    @Override
    public void genQueries(int n, int[] ids, int offset) {
        iOSGL.glGenQueries(n, ids, offset);
    }

    @Override
    public void genQueries(int n, IntBuffer ids) {
        iOSGL.glGenQueries(n, ids);
    }

    @Override
    public void deleteQueries(int n, int[] ids, int offset) {
        iOSGL.glDeleteQueries(n, ids, offset);
    }

    @Override
    public void deleteQueries(int n, IntBuffer ids) {
        iOSGL.glDeleteQueries(n, ids);
    }

    @Override
    public boolean isQuery(int id) {
        iOSGL.glIsQuery(id);
        return false;
    }

    @Override
    public void beginQuery(int target, int id) {
        iOSGL.glBeginQuery(target, id);
    }

    @Override
    public void endQuery(int target) {
        iOSGL.glEndQuery(target);
    }

    @Override
    public void getQueryiv(int target, int pname, IntBuffer params) {
        iOSGL.glGetQueryiv(target, pname, params);
    }

    @Override
    public void getQueryObjectuiv(int id, int pname, IntBuffer params) {
        iOSGL.glGetQueryObjectuiv(id, pname, params);
    }

    @Override
    public boolean unmapBuffer(int target) {
        iOSGL.glUnmapBuffer(target);
        return false;
    }

    @Override
    public Buffer getBufferPointerv(int target, int pname) {
        iOSGL.glGetBufferPointerv(target, pname);
        return null;
    }

    @Override
    public void drawBuffers(int n, IntBuffer bufs) {
        iOSGL.glDrawBuffers(n, bufs);
    }

    @Override
    public void uniformMatrix2x3fv(int location, int count, boolean transpose, FloatBuffer value) {
        iOSGL.glUniformMatrix2x3fv(location, count, transpose, value);
    }

    @Override
    public void uniformMatrix3x2fv(int location, int count, boolean transpose, FloatBuffer value) {
        iOSGL.glUniformMatrix3x2fv(location, count, transpose, value);
    }

    @Override
    public void uniformMatrix2x4fv(int location, int count, boolean transpose, FloatBuffer value) {
        iOSGL.glUniformMatrix2x4fv(location, count, transpose, value);
    }

    @Override
    public void uniformMatrix4x2fv(int location, int count, boolean transpose, FloatBuffer value) {
        iOSGL.glUniformMatrix4x2fv(location, count, transpose, value);
    }

    @Override
    public void uniformMatrix3x4fv(int location, int count, boolean transpose, FloatBuffer value) {
        iOSGL.glUniformMatrix3x4fv(location, count, transpose, value);
    }

    @Override
    public void uniformMatrix4x3fv(int location, int count, boolean transpose, FloatBuffer value) {
        iOSGL.glUniformMatrix4x3fv(location, count, transpose, value);
    }

    @Override
    public void blitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
        iOSGL.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
    }

    @Override
    public void renderbufferStorageMultisample(int target, int samples, int internalformat, int width, int height) {
        iOSGL.glRenderbufferStorageMultisample(target, samples, internalformat, width, height);
    }

    @Override
    public void framebufferTextureLayer(int target, int attachment, int texture, int level, int layer) {
        iOSGL.glFramebufferTextureLayer(target, attachment, texture, level, layer);
    }

    @Override
    public void flushMappedBufferRange(int target, int offset, int length) {
        iOSGL.glFlushMappedBufferRange(target, offset, length);
    }

    @Override
    public void bindVertexArray(int array) {
        iOSGL.glBindVertexArray(array);
    }

    @Override
    public void deleteVertexArrays(int n, int[] arrays, int offset) {
        iOSGL.glDeleteVertexArrays(n, arrays, offset);
    }

    @Override
    public void deleteVertexArrays(int n, IntBuffer arrays) {
        iOSGL.glDeleteVertexArrays(n, arrays);
    }

    @Override
    public void genVertexArrays(int n, int[] arrays, int offset) {
        iOSGL.glGenVertexArrays(n, arrays, offset);
    }

    @Override
    public void genVertexArrays(int n, IntBuffer arrays) {
        iOSGL.glGenVertexArrays(n, arrays);
    }

    @Override
    public boolean isVertexArray(int array) {
        iOSGL.glIsVertexArray(array);
        return false;
    }

    @Override
    public void beginTransformFeedback(int primitiveMode) {
        iOSGL.glBeginTransformFeedback(primitiveMode);
    }

    @Override
    public void endTransformFeedback() {
        iOSGL.glEndTransformFeedback();
    }

    @Override
    public void bindBufferRange(int target, int index, int buffer, int offset, int size) {
        iOSGL.glBindBufferRange(target, index, buffer, offset, size);
    }

    @Override
    public void bindBufferBase(int target, int index, int buffer) {
        iOSGL.glBindBufferBase(target, index, buffer);
    }

    @Override
    public void transformFeedbackVaryings(int program, String[] varyings, int bufferMode) {
        iOSGL.glTransformFeedbackVaryings(program, varyings, bufferMode);
    }

    @Override
    public void vertexAttribIPointer(int index, int size, int type, int stride, int offset) {
        iOSGL.glVertexAttribIPointer(index, size, type, stride, offset);
    }

    @Override
    public void getVertexAttribIiv(int index, int pname, IntBuffer params) {
        iOSGL.glGetVertexAttribIiv(index, pname, params);
    }

    @Override
    public void getVertexAttribIuiv(int index, int pname, IntBuffer params) {
        iOSGL.glGetVertexAttribIuiv(index, pname, params);
    }

    @Override
    public void vertexAttribI4i(int index, int x, int y, int z, int w) {
        iOSGL.glVertexAttribI4i(index, x, y, z, w);
    }

    @Override
    public void vertexAttribI4ui(int index, int x, int y, int z, int w) {
        iOSGL.glVertexAttribI4ui(index, x, y, z, w);
    }

    @Override
    public void getUniformuiv(int program, int location, IntBuffer params) {
        iOSGL.glGetUniformuiv(program, location, params);
    }

    @Override
    public int getFragDataLocation(int program, String name) {
        iOSGL.glGetFragDataLocation(program, name);
        return 0;
    }

    @Override
    public void uniform1uiv(int location, int count, IntBuffer value) {
        iOSGL.glUniform1uiv(location, count, value);
    }

    @Override
    public void uniform3uiv(int location, int count, IntBuffer value) {
        iOSGL.glUniform3uiv(location, count, value);
    }

    @Override
    public void uniform4uiv(int location, int count, IntBuffer value) {
        iOSGL.glUniform4uiv(location, count, value);
    }

    @Override
    public void clearBufferiv(int buffer, int drawbuffer, IntBuffer value) {
        iOSGL.glClearBufferiv(buffer, drawbuffer, value);
    }

    @Override
    public void clearBufferuiv(int buffer, int drawbuffer, IntBuffer value) {
        iOSGL.glClearBufferuiv(buffer, drawbuffer, value);
    }

    @Override
    public void clearBufferfv(int buffer, int drawbuffer, FloatBuffer value) {
        iOSGL.glClearBufferfv(buffer, drawbuffer, value);
    }

    @Override
    public void clearBufferfi(int buffer, int drawbuffer, float depth, int stencil) {
        iOSGL.glClearBufferfi(buffer, drawbuffer, depth, stencil);
    }

    @Override
    public String getStringi(int name, int index) {
        iOSGL.glGetStringi(name, index);
        return null;
    }

    @Override
    public void copyBufferSubData(int readTarget, int writeTarget, int readOffset, int writeOffset, int size) {
        iOSGL.glCopyBufferSubData(readTarget, writeTarget, readOffset, writeOffset, size);
    }

    @Override
    public void getUniformIndices(int program, String[] uniformNames, IntBuffer uniformIndices) {
        iOSGL.glGetUniformIndices(program, uniformNames, uniformIndices);
    }

    @Override
    public void getActiveUniformsiv(int program, int uniformCount, IntBuffer uniformIndices, int pname, IntBuffer params) {
        iOSGL.glGetActiveUniformsiv(program, uniformCount, uniformIndices, pname, params);
    }

    @Override
    public int getUniformBlockIndex(int program, String uniformBlockName) {
        return
                iOSGL.glGetUniformBlockIndex(program, uniformBlockName);
    }

    @Override
    public void getActiveUniformBlockiv(int program, int uniformBlockIndex, int pname, IntBuffer params) {
        iOSGL.glGetActiveUniformBlockiv(program, uniformBlockIndex, pname,
                params);
    }

    @Override
    public void getActiveUniformBlockName(int program, int uniformBlockIndex, Buffer length, Buffer uniformBlockName) {
        iOSGL.glGetActiveUniformBlockName(program, uniformBlockIndex, length,
                uniformBlockName);
    }

    @Override
    public String getActiveUniformBlockName(int program, int uniformBlockIndex) {
        iOSGL.glGetActiveUniformBlockName(program, uniformBlockIndex);
        return null;
    }

    @Override
    public void uniformBlockBinding(int program, int uniformBlockIndex, int uniformBlockBinding) {
        iOSGL.glUniformBlockBinding(program, uniformBlockIndex, uniformBlockBinding);
    }

    @Override
    public void drawArraysInstanced(int mode, int first, int count, int instanceCount) {
        iOSGL.glDrawArraysInstanced(mode, first, count, instanceCount);
    }

    @Override
    public void drawElementsInstanced(int mode, int count, int type, int indicesOffset, int instanceCount) {
        iOSGL.glDrawElementsInstanced(mode, count, type, indicesOffset,
                instanceCount);
    }

    @Override
    public void getInteger64v(int pname, LongBuffer params) {
        iOSGL.glGetInteger64v(pname, params);
    }

    @Override
    public void getBufferParameteri64v(int target, int pname, LongBuffer params) {
        iOSGL.glGetBufferParameteri64v(target, pname, params);
    }

    @Override
    public void genSamplers(int count, int[] samplers, int offset) {
        iOSGL.glGenSamplers(count, samplers, offset);
    }

    @Override
    public void genSamplers(int count, IntBuffer samplers) {
        iOSGL.glGenSamplers(count, samplers);
    }

    @Override
    public void deleteSamplers(int count, int[] samplers, int offset) {
        iOSGL.glDeleteSamplers(count, samplers, offset);
    }

    @Override
    public void deleteSamplers(int count, IntBuffer samplers) {
        iOSGL.glDeleteSamplers(count, samplers);
    }

    @Override
    public boolean isSampler(int sampler) {
        iOSGL.glIsSampler(sampler);
        return false;
    }

    @Override
    public void bindSampler(int unit, int sampler) {
        iOSGL.glBindSampler(unit, sampler);
    }

    @Override
    public void samplerParameteri(int sampler, int pname, int param) {
        iOSGL.glSamplerParameteri(sampler, pname, param);
    }

    @Override
    public void samplerParameteriv(int sampler, int pname, IntBuffer param) {
        iOSGL.glSamplerParameteriv(sampler, pname, param);
    }

    @Override
    public void samplerParameterf(int sampler, int pname, float param) {
        iOSGL.glSamplerParameterf(sampler, pname, param);
    }

    @Override
    public void samplerParameterfv(int sampler, int pname, FloatBuffer param) {
        iOSGL.glSamplerParameterfv(sampler, pname, param);
    }

    @Override
    public void getSamplerParameteriv(int sampler, int pname, IntBuffer params) {
        iOSGL.glGetSamplerParameteriv(sampler, pname, params);
    }

    @Override
    public void getSamplerParameterfv(int sampler, int pname, FloatBuffer params) {
        iOSGL.glGetSamplerParameterfv(sampler, pname, params);
    }

    @Override
    public void vertexAttribDivisor(int index, int divisor) {
        iOSGL.glVertexAttribDivisor(index, divisor);
    }

    @Override
    public void bindTransformFeedback(int target, int id) {
        iOSGL.glBindTransformFeedback(target, id);
    }

    @Override
    public void deleteTransformFeedbacks(int n, int[] ids, int offset) {
        iOSGL.glDeleteTransformFeedbacks(n, ids, offset);
    }

    @Override
    public void deleteTransformFeedbacks(int n, IntBuffer ids) {
        iOSGL.glDeleteTransformFeedbacks(n, ids);
    }

    @Override
    public void genTransformFeedbacks(int n, int[] ids, int offset) {
        iOSGL.glGenTransformFeedbacks(n, ids, offset);
    }

    @Override
    public void genTransformFeedbacks(int n, IntBuffer ids) {
        iOSGL.glGenTransformFeedbacks(n, ids);
    }

    @Override
    public boolean isTransformFeedback(int id) {
        iOSGL.glIsTransformFeedback(id);
        return false;
    }

    @Override
    public void pauseTransformFeedback() {
        iOSGL.glPauseTransformFeedback();
    }

    @Override
    public void resumeTransformFeedback() {
        iOSGL.glResumeTransformFeedback();
    }

    @Override
    public void programParameteri(int program, int pname, int value) {
        iOSGL.glProgramParameteri(program, pname, value);
    }

    @Override
    public void invalidateFramebuffer(int target, int numAttachments, IntBuffer attachments) {
        iOSGL.glInvalidateFramebuffer(target, numAttachments, attachments);
    }

    @Override
    public void invalidateSubFramebuffer(int target, int numAttachments, IntBuffer attachments, int x, int y, int width, int height) {
        iOSGL.glInvalidateSubFramebuffer(target, numAttachments, attachments,
                x, y, width, height);
    }
}
