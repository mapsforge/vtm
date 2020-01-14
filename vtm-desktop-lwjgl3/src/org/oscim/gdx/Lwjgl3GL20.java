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
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.oscim.backend.GL;

import java.nio.*;

/**
 * See https://github.com/libgdx/libgdx/blob/master/backends/gdx-backend-lwjgl3/src/com/badlogic/gdx/backends/lwjgl3/Lwjgl3GL20.java
 */
public class Lwjgl3GL20 implements GL {
    private ByteBuffer buffer = null;
    private FloatBuffer floatBuffer = null;
    private IntBuffer intBuffer = null;

    private void ensureBufferCapacity(int numBytes) {
        if (buffer == null || buffer.capacity() < numBytes) {
            buffer = com.badlogic.gdx.utils.BufferUtils.newByteBuffer(numBytes);
            floatBuffer = buffer.asFloatBuffer();
            intBuffer = buffer.asIntBuffer();
        }
    }

    private FloatBuffer toFloatBuffer(float v[], int offset, int count) {
        ensureBufferCapacity(count << 2);
        floatBuffer.clear();
        floatBuffer.limit(count);
        floatBuffer.put(v, offset, count);
        floatBuffer.position(0);
        return floatBuffer;
    }

    private IntBuffer toIntBuffer(int v[], int offset, int count) {
        ensureBufferCapacity(count << 2);
        intBuffer.clear();
        intBuffer.limit(count);
        intBuffer.put(v, offset, count);
        intBuffer.position(0);
        return intBuffer;
    }

    @Override
    public void activeTexture(int texture) {
        GL13.glActiveTexture(texture);
    }

    @Override
    public void attachShader(int program, int shader) {
        GL20.glAttachShader(program, shader);
    }

    @Override
    public void bindAttribLocation(int program, int index, String name) {
        GL20.glBindAttribLocation(program, index, name);
    }

    @Override
    public void bindBuffer(int target, int buffer) {
        GL15.glBindBuffer(target, buffer);
    }

    @Override
    public void bindFramebuffer(int target, int framebuffer) {
        EXTFramebufferObject.glBindFramebufferEXT(target, framebuffer);
    }

    @Override
    public void bindRenderbuffer(int target, int renderbuffer) {
        EXTFramebufferObject.glBindRenderbufferEXT(target, renderbuffer);
    }

    @Override
    public void bindTexture(int target, int texture) {
        GL11.glBindTexture(target, texture);
    }

    @Override
    public void blendColor(float red, float green, float blue, float alpha) {
        GL14.glBlendColor(red, green, blue, alpha);
    }

    @Override
    public void blendEquation(int mode) {
        GL14.glBlendEquation(mode);
    }

    @Override
    public void blendEquationSeparate(int modeRGB, int modeAlpha) {
        GL20.glBlendEquationSeparate(modeRGB, modeAlpha);
    }

    @Override
    public void blendFunc(int sfactor, int dfactor) {
        GL11.glBlendFunc(sfactor, dfactor);
    }

    @Override
    public void blendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        GL14.glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
    }

    @Override
    public void bufferData(int target, int size, Buffer data, int usage) {
        if (data == null)
            GL15.glBufferData(target, size, usage);
        else if (data instanceof ByteBuffer)
            GL15.glBufferData(target, (ByteBuffer) data, usage);
        else if (data instanceof IntBuffer)
            GL15.glBufferData(target, (IntBuffer) data, usage);
        else if (data instanceof FloatBuffer)
            GL15.glBufferData(target, (FloatBuffer) data, usage);
        else if (data instanceof DoubleBuffer)
            GL15.glBufferData(target, (DoubleBuffer) data, usage);
        else if (data instanceof ShortBuffer) //
            GL15.glBufferData(target, (ShortBuffer) data, usage);
    }

    @Override
    public void bufferSubData(int target, int offset, int size, Buffer data) {
        if (data == null)
            throw new GdxRuntimeException("Using null for the data not possible, blame LWJGL");
        else if (data instanceof ByteBuffer)
            GL15.glBufferSubData(target, offset, (ByteBuffer) data);
        else if (data instanceof IntBuffer)
            GL15.glBufferSubData(target, offset, (IntBuffer) data);
        else if (data instanceof FloatBuffer)
            GL15.glBufferSubData(target, offset, (FloatBuffer) data);
        else if (data instanceof DoubleBuffer)
            GL15.glBufferSubData(target, offset, (DoubleBuffer) data);
        else if (data instanceof ShortBuffer) //
            GL15.glBufferSubData(target, offset, (ShortBuffer) data);
    }

    @Override
    public int checkFramebufferStatus(int target) {
        return EXTFramebufferObject.glCheckFramebufferStatusEXT(target);
    }

    @Override
    public void clear(int mask) {
        GL11.glClear(mask);
    }

    @Override
    public void clearColor(float red, float green, float blue, float alpha) {
        GL11.glClearColor(red, green, blue, alpha);
    }

    @Override
    public void clearDepthf(float depth) {
        GL11.glClearDepth(depth);
    }

    @Override
    public void clearStencil(int s) {
        GL11.glClearStencil(s);
    }

    @Override
    public void colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        GL11.glColorMask(red, green, blue, alpha);
    }

    @Override
    public void compileShader(int shader) {
        GL20.glCompileShader(shader);
    }

    @Override
    public void compressedTexImage2D(int target, int level, int internalformat, int width, int height, int border,
                                     int imageSize, Buffer data) {
        if (data instanceof ByteBuffer) {
            GL13.glCompressedTexImage2D(target, level, internalformat, width, height, border, (ByteBuffer) data);
        } else {
            throw new GdxRuntimeException("Can't use " + data.getClass().getName() + " with this method. Use ByteBuffer instead.");
        }
    }

    @Override
    public void compressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format,
                                        int imageSize, Buffer data) {
        throw new GdxRuntimeException("not implemented");
    }

    @Override
    public void copyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height, int border) {
        GL11.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border);
    }

    @Override
    public void copyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height) {
        GL11.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
    }

    @Override
    public int createProgram() {
        return GL20.glCreateProgram();
    }

    @Override
    public int createShader(int type) {
        return GL20.glCreateShader(type);
    }

    @Override
    public void cullFace(int mode) {
        GL11.glCullFace(mode);
    }

    @Override
    public void deleteBuffers(int n, IntBuffer buffers) {
        GL15.glDeleteBuffers(buffers);
    }

    @Override
    public void deleteBuffer(int buffer) {
        GL15.glDeleteBuffers(buffer);
    }

    @Override
    public void deleteFramebuffers(int n, IntBuffer framebuffers) {
        EXTFramebufferObject.glDeleteFramebuffersEXT(framebuffers);
    }

    @Override
    public void deleteFramebuffer(int framebuffer) {
        EXTFramebufferObject.glDeleteFramebuffersEXT(framebuffer);
    }

    @Override
    public void deleteProgram(int program) {
        GL20.glDeleteProgram(program);
    }

    @Override
    public void deleteRenderbuffers(int n, IntBuffer renderbuffers) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(renderbuffers);
    }

    @Override
    public void deleteRenderbuffer(int renderbuffer) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(renderbuffer);
    }

    @Override
    public void deleteShader(int shader) {
        GL20.glDeleteShader(shader);
    }

    @Override
    public void deleteTextures(int n, IntBuffer textures) {
        GL11.glDeleteTextures(textures);
    }

    @Override
    public void deleteTexture(int texture) {
        GL11.glDeleteTextures(texture);
    }

    @Override
    public void depthFunc(int func) {
        GL11.glDepthFunc(func);
    }

    @Override
    public void depthMask(boolean flag) {
        GL11.glDepthMask(flag);
    }

    @Override
    public void depthRangef(float zNear, float zFar) {
        GL11.glDepthRange(zNear, zFar);
    }

    @Override
    public void detachShader(int program, int shader) {
        GL20.glDetachShader(program, shader);
    }

    @Override
    public void disable(int cap) {
        GL11.glDisable(cap);
    }

    @Override
    public void disableVertexAttribArray(int index) {
        GL20.glDisableVertexAttribArray(index);
    }

    @Override
    public void drawArrays(int mode, int first, int count) {
        GL11.glDrawArrays(mode, first, count);
    }

    @Override
    public void drawElements(int mode, int count, int type, Buffer indices) {
        if (indices instanceof ShortBuffer && type == com.badlogic.gdx.graphics.GL20.GL_UNSIGNED_SHORT)
            GL11.glDrawElements(mode, (ShortBuffer) indices);
        else if (indices instanceof ByteBuffer && type == com.badlogic.gdx.graphics.GL20.GL_UNSIGNED_SHORT)
            GL11.glDrawElements(mode, ((ByteBuffer) indices).asShortBuffer());
        else if (indices instanceof ByteBuffer && type == com.badlogic.gdx.graphics.GL20.GL_UNSIGNED_BYTE)
            GL11.glDrawElements(mode, (ByteBuffer) indices);
        else
            throw new GdxRuntimeException("Can't use " + indices.getClass().getName()
                    + " with this method. Use ShortBuffer or ByteBuffer instead. Blame LWJGL");
    }

    @Override
    public void enable(int cap) {
        GL11.glEnable(cap);
    }

    @Override
    public void enableVertexAttribArray(int index) {
        GL20.glEnableVertexAttribArray(index);
    }

    @Override
    public void finish() {
        GL11.glFinish();
    }

    @Override
    public void flush() {
        GL11.glFlush();
    }

    @Override
    public void framebufferRenderbuffer(int target, int attachment, int renderbuffertarget, int renderbuffer) {
        EXTFramebufferObject.glFramebufferRenderbufferEXT(target, attachment, renderbuffertarget, renderbuffer);
    }

    @Override
    public void framebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
        EXTFramebufferObject.glFramebufferTexture2DEXT(target, attachment, textarget, texture, level);
    }

    @Override
    public void frontFace(int mode) {
        GL11.glFrontFace(mode);
    }

    @Override
    public void genBuffers(int n, IntBuffer buffers) {
        GL15.glGenBuffers(buffers);
    }

    @Override
    public int genBuffer() {
        return GL15.glGenBuffers();
    }

    @Override
    public void genFramebuffers(int n, IntBuffer framebuffers) {
        EXTFramebufferObject.glGenFramebuffersEXT(framebuffers);
    }

    @Override
    public int genFramebuffer() {
        return EXTFramebufferObject.glGenFramebuffersEXT();
    }

    @Override
    public void genRenderbuffers(int n, IntBuffer renderbuffers) {
        EXTFramebufferObject.glGenRenderbuffersEXT(renderbuffers);
    }

    @Override
    public int genRenderbuffer() {
        return EXTFramebufferObject.glGenRenderbuffersEXT();
    }

    @Override
    public void genTextures(int n, IntBuffer textures) {
        GL11.glGenTextures(textures);
    }

    @Override
    public int genTexture() {
        return GL11.glGenTextures();
    }

    @Override
    public void generateMipmap(int target) {
        EXTFramebufferObject.glGenerateMipmapEXT(target);
    }

    @Override
    public String getActiveAttrib(int program, int index, IntBuffer size, Buffer type) {
        IntBuffer typeTmp = BufferUtils.createIntBuffer(2);
        String name = GL20.glGetActiveAttrib(program, index, 256, size, typeTmp);
        size.put(typeTmp.get(0));
        if (type instanceof IntBuffer) ((IntBuffer) type).put(typeTmp.get(1));
        return name;
    }

    @Override
    public String getActiveUniform(int program, int index, IntBuffer size, Buffer type) {
        IntBuffer typeTmp = BufferUtils.createIntBuffer(2);
        String name = GL20.glGetActiveUniform(program, index, 256, size, typeTmp);
        size.put(typeTmp.get(0));
        if (type instanceof IntBuffer) ((IntBuffer) type).put(typeTmp.get(1));
        return name;
    }

    @Override
    public void getAttachedShaders(int program, int maxcount, Buffer count, IntBuffer shaders) {
        GL20.glGetAttachedShaders(program, (IntBuffer) count, shaders);
    }

    @Override
    public int getAttribLocation(int program, String name) {
        return GL20.glGetAttribLocation(program, name);
    }

    @Override
    public void getBooleanv(int pname, Buffer params) {
        GL11.glGetBooleanv(pname, (ByteBuffer) params);
    }

    @Override
    public void getBufferParameteriv(int target, int pname, IntBuffer params) {
        GL15.glGetBufferParameteriv(target, pname, params);
    }

    @Override
    public int getError() {
        return GL11.glGetError();
    }

    @Override
    public void getFloatv(int pname, FloatBuffer params) {
        GL11.glGetFloatv(pname, params);
    }

    @Override
    public void getFramebufferAttachmentParameteriv(int target, int attachment, int pname, IntBuffer params) {
        EXTFramebufferObject.glGetFramebufferAttachmentParameterivEXT(target, attachment, pname, params);
    }

    @Override
    public void getIntegerv(int pname, IntBuffer params) {
        GL11.glGetIntegerv(pname, params);
    }

    @Override
    public String getProgramInfoLog(int program) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 10);
        buffer.order(ByteOrder.nativeOrder());
        ByteBuffer tmp = ByteBuffer.allocateDirect(4);
        tmp.order(ByteOrder.nativeOrder());
        IntBuffer intBuffer = tmp.asIntBuffer();

        GL20.glGetProgramInfoLog(program, intBuffer, buffer);
        int numBytes = intBuffer.get(0);
        byte[] bytes = new byte[numBytes];
        buffer.get(bytes);
        return new String(bytes);
    }

    @Override
    public void getProgramiv(int program, int pname, IntBuffer params) {
        GL20.glGetProgramiv(program, pname, params);
    }

    @Override
    public void getRenderbufferParameteriv(int target, int pname, IntBuffer params) {
        EXTFramebufferObject.glGetRenderbufferParameterivEXT(target, pname, params);
    }

    @Override
    public String getShaderInfoLog(int shader) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 10);
        buffer.order(ByteOrder.nativeOrder());
        ByteBuffer tmp = ByteBuffer.allocateDirect(4);
        tmp.order(ByteOrder.nativeOrder());
        IntBuffer intBuffer = tmp.asIntBuffer();

        GL20.glGetShaderInfoLog(shader, intBuffer, buffer);
        int numBytes = intBuffer.get(0);
        byte[] bytes = new byte[numBytes];
        buffer.get(bytes);
        return new String(bytes);
    }

    @Override
    public void getShaderPrecisionFormat(int shadertype, int precisiontype, IntBuffer range, IntBuffer precision) {
        throw new UnsupportedOperationException("unsupported, won't implement");
    }

    @Override
    public void getShaderiv(int shader, int pname, IntBuffer params) {
        GL20.glGetShaderiv(shader, pname, params);
    }

    @Override
    public String getString(int name) {
        return GL11.glGetString(name);
    }

    @Override
    public void getTexParameterfv(int target, int pname, FloatBuffer params) {
        GL11.glGetTexParameterfv(target, pname, params);
    }

    @Override
    public void getTexParameteriv(int target, int pname, IntBuffer params) {
        GL11.glGetTexParameteriv(target, pname, params);
    }

    @Override
    public int getUniformLocation(int program, String name) {
        return GL20.glGetUniformLocation(program, name);
    }

    @Override
    public void getUniformfv(int program, int location, FloatBuffer params) {
        GL20.glGetUniformfv(program, location, params);
    }

    @Override
    public void getUniformiv(int program, int location, IntBuffer params) {
        GL20.glGetUniformiv(program, location, params);
    }

    @Override
    public void getVertexAttribPointerv(int index, int pname, Buffer pointer) {
        throw new UnsupportedOperationException("unsupported, won't implement");
    }

    @Override
    public void getVertexAttribfv(int index, int pname, FloatBuffer params) {
        GL20.glGetVertexAttribfv(index, pname, params);
    }

    @Override
    public void getVertexAttribiv(int index, int pname, IntBuffer params) {
        GL20.glGetVertexAttribiv(index, pname, params);
    }

    @Override
    public void hint(int target, int mode) {
        GL11.glHint(target, mode);
    }

    @Override
    public boolean isBuffer(int buffer) {
        return GL15.glIsBuffer(buffer);
    }

    @Override
    public boolean isEnabled(int cap) {
        return GL11.glIsEnabled(cap);
    }

    @Override
    public boolean isFramebuffer(int framebuffer) {
        return EXTFramebufferObject.glIsFramebufferEXT(framebuffer);
    }

    @Override
    public boolean isProgram(int program) {
        return GL20.glIsProgram(program);
    }

    @Override
    public boolean isRenderbuffer(int renderbuffer) {
        return EXTFramebufferObject.glIsRenderbufferEXT(renderbuffer);
    }

    @Override
    public boolean isShader(int shader) {
        return GL20.glIsShader(shader);
    }

    @Override
    public boolean isTexture(int texture) {
        return GL11.glIsTexture(texture);
    }

    @Override
    public void lineWidth(float width) {
        GL11.glLineWidth(width);
    }

    @Override
    public void linkProgram(int program) {
        GL20.glLinkProgram(program);
    }

    @Override
    public void pixelStorei(int pname, int param) {
        GL11.glPixelStorei(pname, param);
    }

    @Override
    public void polygonOffset(float factor, float units) {
        GL11.glPolygonOffset(factor, units);
    }

    @Override
    public void readPixels(int x, int y, int width, int height, int format, int type, Buffer pixels) {
        if (pixels instanceof ByteBuffer)
            GL11.glReadPixels(x, y, width, height, format, type, (ByteBuffer) pixels);
        else if (pixels instanceof ShortBuffer)
            GL11.glReadPixels(x, y, width, height, format, type, (ShortBuffer) pixels);
        else if (pixels instanceof IntBuffer)
            GL11.glReadPixels(x, y, width, height, format, type, (IntBuffer) pixels);
        else if (pixels instanceof FloatBuffer)
            GL11.glReadPixels(x, y, width, height, format, type, (FloatBuffer) pixels);
        else
            throw new GdxRuntimeException("Can't use " + pixels.getClass().getName()
                    + " with this method. Use ByteBuffer, ShortBuffer, IntBuffer or FloatBuffer instead. Blame LWJGL");
    }

    @Override
    public void releaseShaderCompiler() {
        // nothing to do here
    }

    @Override
    public void renderbufferStorage(int target, int internalformat, int width, int height) {
        EXTFramebufferObject.glRenderbufferStorageEXT(target, internalformat, width, height);
    }

    @Override
    public void sampleCoverage(float value, boolean invert) {
        GL13.glSampleCoverage(value, invert);
    }

    @Override
    public void scissor(int x, int y, int width, int height) {
        GL11.glScissor(x, y, width, height);
    }

    @Override
    public void shaderBinary(int n, IntBuffer shaders, int binaryformat, Buffer binary, int length) {
        throw new UnsupportedOperationException("unsupported, won't implement");
    }

    @Override
    public void shaderSource(int shader, String string) {
        GL20.glShaderSource(shader, string);
    }

    @Override
    public void stencilFunc(int func, int ref, int mask) {
        GL11.glStencilFunc(func, ref, mask);
    }

    @Override
    public void stencilFuncSeparate(int face, int func, int ref, int mask) {
        GL20.glStencilFuncSeparate(face, func, ref, mask);
    }

    @Override
    public void stencilMask(int mask) {
        GL11.glStencilMask(mask);
    }

    @Override
    public void stencilMaskSeparate(int face, int mask) {
        GL20.glStencilMaskSeparate(face, mask);
    }

    @Override
    public void stencilOp(int fail, int zfail, int zpass) {
        GL11.glStencilOp(fail, zfail, zpass);
    }

    @Override
    public void stencilOpSeparate(int face, int fail, int zfail, int zpass) {
        GL20.glStencilOpSeparate(face, fail, zfail, zpass);
    }

    @Override
    public void texImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type,
                           Buffer pixels) {
        if (pixels == null)
            GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, (ByteBuffer) null);
        else if (pixels instanceof ByteBuffer)
            GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, (ByteBuffer) pixels);
        else if (pixels instanceof ShortBuffer)
            GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, (ShortBuffer) pixels);
        else if (pixels instanceof IntBuffer)
            GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, (IntBuffer) pixels);
        else if (pixels instanceof FloatBuffer)
            GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, (FloatBuffer) pixels);
        else if (pixels instanceof DoubleBuffer)
            GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, (DoubleBuffer) pixels);
        else
            throw new GdxRuntimeException("Can't use " + pixels.getClass().getName()
                    + " with this method. Use ByteBuffer, ShortBuffer, IntBuffer, FloatBuffer or DoubleBuffer instead. Blame LWJGL");
    }

    @Override
    public void texParameterf(int target, int pname, float param) {
        GL11.glTexParameterf(target, pname, param);
    }

    @Override
    public void texParameterfv(int target, int pname, FloatBuffer params) {
        GL11.glTexParameterfv(target, pname, params);
    }

    @Override
    public void texParameteri(int target, int pname, int param) {
        GL11.glTexParameteri(target, pname, param);
    }

    @Override
    public void texParameteriv(int target, int pname, IntBuffer params) {
        GL11.glTexParameteriv(target, pname, params);
    }

    @Override
    public void texSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type,
                              Buffer pixels) {
        if (pixels instanceof ByteBuffer)
            GL11.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, (ByteBuffer) pixels);
        else if (pixels instanceof ShortBuffer)
            GL11.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, (ShortBuffer) pixels);
        else if (pixels instanceof IntBuffer)
            GL11.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, (IntBuffer) pixels);
        else if (pixels instanceof FloatBuffer)
            GL11.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, (FloatBuffer) pixels);
        else if (pixels instanceof DoubleBuffer)
            GL11.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, (DoubleBuffer) pixels);
        else
            throw new GdxRuntimeException("Can't use " + pixels.getClass().getName()
                    + " with this method. Use ByteBuffer, ShortBuffer, IntBuffer, FloatBuffer or DoubleBuffer instead. Blame LWJGL");
    }

    @Override
    public void uniform1f(int location, float x) {
        GL20.glUniform1f(location, x);
    }

    @Override
    public void uniform1fv(int location, int count, FloatBuffer v) {
        GL20.glUniform1fv(location, v);
    }

    @Override
    public void uniform1fv(int location, int count, float[] v, int offset) {
        GL20.glUniform1fv(location, toFloatBuffer(v, offset, count));
    }

    @Override
    public void uniform1i(int location, int x) {
        GL20.glUniform1i(location, x);
    }

    @Override
    public void uniform1iv(int location, int count, IntBuffer v) {
        GL20.glUniform1iv(location, v);
    }

    @Override
    public void uniform1iv(int location, int count, int[] v, int offset) {
        GL20.glUniform1iv(location, toIntBuffer(v, offset, count));
    }

    @Override
    public void uniform2f(int location, float x, float y) {
        GL20.glUniform2f(location, x, y);
    }

    @Override
    public void uniform2fv(int location, int count, FloatBuffer v) {
        GL20.glUniform2fv(location, v);
    }

    @Override
    public void uniform2fv(int location, int count, float[] v, int offset) {
        GL20.glUniform2fv(location, toFloatBuffer(v, offset, count << 1));
    }

    @Override
    public void uniform2i(int location, int x, int y) {
        GL20.glUniform2i(location, x, y);
    }

    @Override
    public void uniform2iv(int location, int count, IntBuffer v) {
        GL20.glUniform2iv(location, v);
    }

    @Override
    public void uniform2iv(int location, int count, int[] v, int offset) {
        GL20.glUniform2iv(location, toIntBuffer(v, offset, count << 1));
    }

    @Override
    public void uniform3f(int location, float x, float y, float z) {
        GL20.glUniform3f(location, x, y, z);
    }

    @Override
    public void uniform3fv(int location, int count, FloatBuffer v) {
        GL20.glUniform3fv(location, v);
    }

    @Override
    public void uniform3fv(int location, int count, float[] v, int offset) {
        GL20.glUniform3fv(location, toFloatBuffer(v, offset, count * 3));
    }

    @Override
    public void uniform3i(int location, int x, int y, int z) {
        GL20.glUniform3i(location, x, y, z);
    }

    @Override
    public void uniform3iv(int location, int count, IntBuffer v) {
        GL20.glUniform3iv(location, v);
    }

    @Override
    public void uniform3iv(int location, int count, int[] v, int offset) {
        GL20.glUniform3iv(location, toIntBuffer(v, offset, count * 3));
    }

    @Override
    public void uniform4f(int location, float x, float y, float z, float w) {
        GL20.glUniform4f(location, x, y, z, w);
    }

    @Override
    public void uniform4fv(int location, int count, FloatBuffer v) {
        GL20.glUniform4fv(location, v);
    }

    @Override
    public void uniform4fv(int location, int count, float[] v, int offset) {
        GL20.glUniform4fv(location, toFloatBuffer(v, offset, count << 2));
    }

    @Override
    public void uniform4i(int location, int x, int y, int z, int w) {
        GL20.glUniform4i(location, x, y, z, w);
    }

    @Override
    public void uniform4iv(int location, int count, IntBuffer v) {
        GL20.glUniform4iv(location, v);
    }

    @Override
    public void uniform4iv(int location, int count, int[] v, int offset) {
        GL20.glUniform4iv(location, toIntBuffer(v, offset, count << 2));
    }

    @Override
    public void uniformMatrix2fv(int location, int count, boolean transpose, FloatBuffer value) {
        GL20.glUniformMatrix2fv(location, transpose, value);
    }

    @Override
    public void uniformMatrix2fv(int location, int count, boolean transpose, float[] value, int offset) {
        GL20.glUniformMatrix2fv(location, transpose, toFloatBuffer(value, offset, count << 2));
    }

    @Override
    public void uniformMatrix3fv(int location, int count, boolean transpose, FloatBuffer value) {
        GL20.glUniformMatrix3fv(location, transpose, value);
    }

    @Override
    public void uniformMatrix3fv(int location, int count, boolean transpose, float[] value, int offset) {
        GL20.glUniformMatrix3fv(location, transpose, toFloatBuffer(value, offset, count * 9));
    }

    @Override
    public void uniformMatrix4fv(int location, int count, boolean transpose, FloatBuffer value) {
        GL20.glUniformMatrix4fv(location, transpose, value);
    }

    @Override
    public void uniformMatrix4fv(int location, int count, boolean transpose, float[] value, int offset) {
        GL20.glUniformMatrix4fv(location, transpose, toFloatBuffer(value, offset, count << 4));
    }

    @Override
    public void useProgram(int program) {
        GL20.glUseProgram(program);
    }

    @Override
    public void validateProgram(int program) {
        GL20.glValidateProgram(program);
    }

    @Override
    public void vertexAttrib1f(int indx, float x) {
        GL20.glVertexAttrib1f(indx, x);
    }

    @Override
    public void vertexAttrib1fv(int indx, FloatBuffer values) {
        GL20.glVertexAttrib1f(indx, values.get());
    }

    @Override
    public void vertexAttrib2f(int indx, float x, float y) {
        GL20.glVertexAttrib2f(indx, x, y);
    }

    @Override
    public void vertexAttrib2fv(int indx, FloatBuffer values) {
        GL20.glVertexAttrib2f(indx, values.get(), values.get());
    }

    @Override
    public void vertexAttrib3f(int indx, float x, float y, float z) {
        GL20.glVertexAttrib3f(indx, x, y, z);
    }

    @Override
    public void vertexAttrib3fv(int indx, FloatBuffer values) {
        GL20.glVertexAttrib3f(indx, values.get(), values.get(), values.get());
    }

    @Override
    public void vertexAttrib4f(int indx, float x, float y, float z, float w) {
        GL20.glVertexAttrib4f(indx, x, y, z, w);
    }

    @Override
    public void vertexAttrib4fv(int indx, FloatBuffer values) {
        GL20.glVertexAttrib4f(indx, values.get(), values.get(), values.get(), values.get());
    }

    @Override
    public void vertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, Buffer buffer) {
        if (buffer instanceof ByteBuffer) {
            if (type == GL.BYTE)
                GL20.glVertexAttribPointer(indx, size, type, normalized, stride, (ByteBuffer) buffer);
            else if (type == GL.UNSIGNED_BYTE)
                GL20.glVertexAttribPointer(indx, size, type, normalized, stride, (ByteBuffer) buffer);
            else if (type == GL.SHORT)
                GL20.glVertexAttribPointer(indx, size, type, normalized, stride, ((ByteBuffer) buffer).asShortBuffer());
            else if (type == GL.UNSIGNED_SHORT)
                GL20.glVertexAttribPointer(indx, size, type, normalized, stride, ((ByteBuffer) buffer).asShortBuffer());
            else if (type == GL.FLOAT)
                GL20.glVertexAttribPointer(indx, size, type, normalized, stride, ((ByteBuffer) buffer).asFloatBuffer());
            else
                throw new GdxRuntimeException(
                        "Can't use "
                                + buffer.getClass().getName()
                                + " with type "
                                + type
                                + " with this method. Use ByteBuffer and one of GL_BYTE, GL_UNSIGNED_BYTE, GL_SHORT, GL_UNSIGNED_SHORT or GL_FLOAT for type. Blame LWJGL");
        } else if (buffer instanceof FloatBuffer) {
            if (type == GL.FLOAT)
                GL20.glVertexAttribPointer(indx, size, type, normalized, stride, (FloatBuffer) buffer);
            else
                throw new GdxRuntimeException("Can't use " + buffer.getClass().getName() + " with type " + type
                        + " with this method.");
        } else
            throw new GdxRuntimeException("Can't use " + buffer.getClass().getName()
                    + " with this method. Use ByteBuffer instead. Blame LWJGL");
    }

    @Override
    public void viewport(int x, int y, int width, int height) {
        GL11.glViewport(x, y, width, height);
    }

    @Override
    public void drawElements(int mode, int count, int type, int indices) {
        GL11.glDrawElements(mode, count, type, indices);
    }

    @Override
    public void vertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int ptr) {
        GL20.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
    }
}
