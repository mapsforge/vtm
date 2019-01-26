/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2019 Gustl22
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
package org.oscim.gdx;

import android.annotation.SuppressLint;
import android.opengl.GLES20;

import com.badlogic.gdx.backends.android.AndroidGL20;

import org.oscim.backend.GL;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@SuppressLint("NewApi")
public class AndroidGL implements GL {

    private static final AndroidGL20 androidGL = new AndroidGL20();

    @Override
    public void attachShader(int program, int shader) {
        GLES20.glAttachShader(program, shader);
    }

    @Override
    public void bindAttribLocation(int program, int index, String name) {
        GLES20.glBindAttribLocation(program, index, name);
    }

    @Override
    public void bindBuffer(int target, int buffer) {
        GLES20.glBindBuffer(target, buffer);
    }

    @Override
    public void bindFramebuffer(int target, int framebuffer) {
        GLES20.glBindFramebuffer(target, framebuffer);
    }

    @Override
    public void bindRenderbuffer(int target, int renderbuffer) {
        GLES20.glBindRenderbuffer(target, renderbuffer);
    }

    @Override
    public void blendColor(float red, float green, float blue, float alpha) {
        GLES20.glBlendColor(red, green, blue, alpha);
    }

    @Override
    public void blendEquation(int mode) {
        GLES20.glBlendEquation(mode);
    }

    @Override
    public void blendEquationSeparate(int modeRGB, int modeAlpha) {
        GLES20.glBlendEquationSeparate(modeRGB, modeAlpha);
    }

    @Override
    public void blendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        GLES20.glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
    }

    @Override
    public void bufferData(int target, int size, Buffer data, int usage) {
        GLES20.glBufferData(target, size, data, usage);
    }

    @Override
    public void bufferSubData(int target, int offset, int size, Buffer data) {
        GLES20.glBufferSubData(target, offset, size, data);
    }

    @Override
    public int checkFramebufferStatus(int target) {
        return GLES20.glCheckFramebufferStatus(target);
    }

    @Override
    public void compileShader(int shader) {
        GLES20.glCompileShader(shader);
    }

    @Override
    public int createProgram() {
        return GLES20.glCreateProgram();
    }

    @Override
    public int createShader(int type) {
        return GLES20.glCreateShader(type);
    }

    @Override
    public void deleteBuffer(int buffer) {
        androidGL.glDeleteBuffer(buffer);
    }

    @Override
    public void deleteBuffers(int n, IntBuffer buffers) {
        GLES20.glDeleteBuffers(n, buffers);
    }

    @Override
    public void deleteFramebuffer(int framebuffer) {
        androidGL.glDeleteFramebuffer(framebuffer);
    }

    @Override
    public void deleteFramebuffers(int n, IntBuffer framebuffers) {
        GLES20.glDeleteFramebuffers(n, framebuffers);
    }

    @Override
    public void deleteProgram(int program) {
        GLES20.glDeleteProgram(program);
    }

    @Override
    public void deleteRenderbuffer(int renderbuffer) {
        androidGL.glDeleteRenderbuffer(renderbuffer);
    }

    @Override
    public void deleteRenderbuffers(int n, IntBuffer renderbuffers) {
        GLES20.glDeleteRenderbuffers(n, renderbuffers);
    }

    @Override
    public void deleteShader(int shader) {
        GLES20.glDeleteShader(shader);
    }

    @Override
    public void detachShader(int program, int shader) {
        GLES20.glDetachShader(program, shader);
    }

    @Override
    public void disableVertexAttribArray(int index) {
        GLES20.glDisableVertexAttribArray(index);
    }

    @Override
    public void drawElements(int mode, int count, int type, int offset) {
        GLES20.glDrawElements(mode, count, type, offset);
    }

    @Override
    public void enableVertexAttribArray(int index) {
        GLES20.glEnableVertexAttribArray(index);
    }

    @Override
    public void framebufferRenderbuffer(int target, int attachment, int renderbuffertarget,
                                        int renderbuffer) {
        GLES20.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
    }

    @Override
    public void framebufferTexture2D(int target, int attachment, int textarget, int texture,
                                     int level) {
        GLES20.glFramebufferTexture2D(target, attachment, textarget, texture, level);
    }

    @Override
    public int genBuffer() {
        return androidGL.glGenBuffer();
    }

    @Override
    public void genBuffers(int n, IntBuffer buffers) {
        GLES20.glGenBuffers(n, buffers);
    }

    @Override
    public void generateMipmap(int target) {
        GLES20.glGenerateMipmap(target);
    }

    @Override
    public int genFramebuffer() {
        return androidGL.glGenFramebuffer();
    }

    @Override
    public void genFramebuffers(int n, IntBuffer framebuffers) {
        GLES20.glGenFramebuffers(n, framebuffers);
    }

    @Override
    public int genRenderbuffer() {
        return androidGL.glGenRenderbuffer();
    }

    @Override
    public void genRenderbuffers(int n, IntBuffer renderbuffers) {
        GLES20.glGenRenderbuffers(n, renderbuffers);
    }

    @Override
    public String getActiveAttrib(int program, int index, IntBuffer size, Buffer type) {
        return GLES20.glGetActiveAttrib(program, index, size, (IntBuffer) type);
    }

    @Override
    public String getActiveUniform(int program, int index, IntBuffer size, Buffer type) {
        return androidGL.glGetActiveUniform(program, index, size, type);
    }

    @Override
    public void getAttachedShaders(int program, int maxcount, Buffer count, IntBuffer shaders) {
        androidGL.glGetAttachedShaders(program, maxcount, count, shaders);
    }

    @Override
    public int getAttribLocation(int program, String name) {
        return GLES20.glGetAttribLocation(program, name);
    }

    @Override
    public void getBooleanv(int pname, Buffer params) {
        androidGL.glGetBooleanv(pname, params);
    }

    @Override
    public void getBufferParameteriv(int target, int pname, IntBuffer params) {
        GLES20.glGetBufferParameteriv(target, pname, params);

    }

    @Override
    public void getFloatv(int pname, FloatBuffer params) {
        GLES20.glGetFloatv(pname, params);

    }

    @Override
    public void getFramebufferAttachmentParameteriv(int target, int attachment, int pname,
                                                    IntBuffer params) {
        GLES20.glGetFramebufferAttachmentParameteriv(target, attachment, pname, params);
    }

    @Override
    public void getProgramiv(int program, int pname, IntBuffer params) {
        GLES20.glGetProgramiv(program, pname, params);

    }

    @Override
    public String getProgramInfoLog(int program) {
        return GLES20.glGetProgramInfoLog(program);
    }

    @Override
    public void getRenderbufferParameteriv(int target, int pname, IntBuffer params) {
        GLES20.glGetRenderbufferParameteriv(target, pname, params);

    }

    @Override
    public void getShaderiv(int shader, int pname, IntBuffer params) {
        GLES20.glGetShaderiv(shader, pname, params);

    }

    @Override
    public String getShaderInfoLog(int shader) {
        return GLES20.glGetShaderInfoLog(shader);
    }

    @Override
    public void getShaderPrecisionFormat(int shadertype, int precisiontype, IntBuffer range,
                                         IntBuffer precision) {
        GLES20.glGetShaderPrecisionFormat(shadertype, precisiontype, range, precision);
    }

    @Override
    public void getTexParameterfv(int target, int pname, FloatBuffer params) {
        GLES20.glGetTexParameterfv(target, pname, params);

    }

    @Override
    public void getTexParameteriv(int target, int pname, IntBuffer params) {
        GLES20.glGetTexParameteriv(target, pname, params);

    }

    @Override
    public void getUniformfv(int program, int location, FloatBuffer params) {
        GLES20.glGetUniformfv(program, location, params);

    }

    @Override
    public void getUniformiv(int program, int location, IntBuffer params) {
        GLES20.glGetUniformiv(program, location, params);

    }

    @Override
    public int getUniformLocation(int program, String name) {
        return GLES20.glGetUniformLocation(program, name);
    }

    @Override
    public void getVertexAttribfv(int index, int pname, FloatBuffer params) {
        GLES20.glGetVertexAttribfv(index, pname, params);

    }

    @Override
    public void getVertexAttribiv(int index, int pname, IntBuffer params) {
        GLES20.glGetVertexAttribiv(index, pname, params);

    }

    @Override
    public void getVertexAttribPointerv(int index, int pname, Buffer pointer) {
        androidGL.glGetVertexAttribPointerv(index, pname, pointer);
    }

    @Override
    public boolean isBuffer(int buffer) {
        return GLES20.glIsBuffer(buffer);
    }

    @Override
    public boolean isEnabled(int cap) {
        return GLES20.glIsEnabled(cap);
    }

    @Override
    public boolean isFramebuffer(int framebuffer) {
        return GLES20.glIsFramebuffer(framebuffer);
    }

    @Override
    public boolean isProgram(int program) {
        return GLES20.glIsProgram(program);
    }

    @Override
    public boolean isRenderbuffer(int renderbuffer) {
        return GLES20.glIsRenderbuffer(renderbuffer);
    }

    @Override
    public boolean isShader(int shader) {
        return GLES20.glIsShader(shader);
    }

    @Override
    public boolean isTexture(int texture) {
        return GLES20.glIsTexture(texture);
    }

    @Override
    public void linkProgram(int program) {
        GLES20.glLinkProgram(program);

    }

    @Override
    public void releaseShaderCompiler() {
        GLES20.glReleaseShaderCompiler();

    }

    @Override
    public void renderbufferStorage(int target, int internalformat, int width, int height) {
        GLES20.glRenderbufferStorage(target, internalformat, width, height);

    }

    @Override
    public void sampleCoverage(float value, boolean invert) {
        GLES20.glSampleCoverage(value, invert);

    }

    @Override
    public void shaderBinary(int n, IntBuffer shaders, int binaryformat, Buffer binary, int length) {
        GLES20.glShaderBinary(n, shaders, binaryformat, binary, length);

    }

    @Override
    public void shaderSource(int shader, String string) {
        GLES20.glShaderSource(shader, string);

    }

    @Override
    public void stencilFuncSeparate(int face, int func, int ref, int mask) {
        GLES20.glStencilFuncSeparate(face, func, ref, mask);

    }

    @Override
    public void stencilMaskSeparate(int face, int mask) {
        GLES20.glStencilMaskSeparate(face, mask);

    }

    @Override
    public void stencilOpSeparate(int face, int fail, int zfail, int zpass) {
        GLES20.glStencilOpSeparate(face, fail, zfail, zpass);

    }

    @Override
    public void texParameterfv(int target, int pname, FloatBuffer params) {
        GLES20.glTexParameterfv(target, pname, params);

    }

    @Override
    public void texParameteri(int target, int pname, int param) {
        GLES20.glTexParameteri(target, pname, param);

    }

    @Override
    public void texParameteriv(int target, int pname, IntBuffer params) {
        GLES20.glTexParameteriv(target, pname, params);

    }

    @Override
    public void uniform1f(int location, float x) {
        GLES20.glUniform1f(location, x);

    }

    @Override
    public void uniform1fv(int location, int count, FloatBuffer v) {
        GLES20.glUniform1fv(location, count, v);

    }

    @Override
    public void uniform1fv(int location, int count, float[] v, int offset) {
        GLES20.glUniform1fv(location, count, v, offset);
    }

    @Override
    public void uniform1i(int location, int x) {
        GLES20.glUniform1i(location, x);

    }

    @Override
    public void uniform1iv(int location, int count, IntBuffer v) {
        GLES20.glUniform1iv(location, count, v);

    }

    @Override
    public void uniform1iv(int location, int count, int[] v, int offset) {
        GLES20.glUniform1iv(location, count, v, offset);
    }

    @Override
    public void uniform2f(int location, float x, float y) {
        GLES20.glUniform2f(location, x, y);

    }

    @Override
    public void uniform2fv(int location, int count, FloatBuffer v) {
        GLES20.glUniform2fv(location, count, v);

    }

    @Override
    public void uniform2fv(int location, int count, float[] v, int offset) {
        GLES20.glUniform2fv(location, count, v, offset);
    }

    @Override
    public void uniform2i(int location, int x, int y) {
        GLES20.glUniform2i(location, x, y);

    }

    @Override
    public void uniform2iv(int location, int count, IntBuffer v) {
        GLES20.glUniform2iv(location, count, v);

    }

    @Override
    public void uniform2iv(int location, int count, int[] v, int offset) {
        GLES20.glUniform2iv(location, count, v, offset);
    }

    @Override
    public void uniform3f(int location, float x, float y, float z) {
        GLES20.glUniform3f(location, x, y, z);

    }

    @Override
    public void uniform3fv(int location, int count, FloatBuffer v) {
        GLES20.glUniform3fv(location, count, v);

    }

    @Override
    public void uniform3fv(int location, int count, float[] v, int offset) {
        GLES20.glUniform3fv(location, count, v, offset);
    }

    @Override
    public void uniform3i(int location, int x, int y, int z) {
        GLES20.glUniform3i(location, x, y, z);

    }

    @Override
    public void uniform3iv(int location, int count, IntBuffer v) {
        GLES20.glUniform3iv(location, count, v);

    }

    @Override
    public void uniform3iv(int location, int count, int[] v, int offset) {
        GLES20.glUniform3iv(location, count, v, offset);
    }

    @Override
    public void uniform4f(int location, float x, float y, float z, float w) {
        GLES20.glUniform4f(location, x, y, z, w);
    }

    @Override
    public void uniform4fv(int location, int count, FloatBuffer v) {
        GLES20.glUniform4fv(location, count, v);
    }

    @Override
    public void uniform4fv(int location, int count, float[] v, int offset) {
        GLES20.glUniform4fv(location, count, v, offset);
    }

    @Override
    public void uniform4i(int location, int x, int y, int z, int w) {
        GLES20.glUniform4i(location, x, y, z, w);

    }

    @Override
    public void uniform4iv(int location, int count, IntBuffer v) {
        GLES20.glUniform4iv(location, count, v);

    }

    @Override
    public void uniform4iv(int location, int count, int[] v, int offset) {
        GLES20.glUniform4iv(location, count, v, offset);
    }

    @Override
    public void uniformMatrix2fv(int location, int count, boolean transpose, FloatBuffer value) {
        GLES20.glUniformMatrix2fv(location, count, transpose, value);

    }

    @Override
    public void uniformMatrix2fv(int location, int count, boolean transpose, float[] value, int offset) {
        GLES20.glUniformMatrix2fv(location, count, transpose, value, offset);
    }

    @Override
    public void uniformMatrix3fv(int location, int count, boolean transpose, FloatBuffer value) {
        GLES20.glUniformMatrix3fv(location, count, transpose, value);

    }

    @Override
    public void uniformMatrix3fv(int location, int count, boolean transpose, float[] value, int offset) {
        GLES20.glUniformMatrix3fv(location, count, transpose, value, offset);
    }

    @Override
    public void uniformMatrix4fv(int location, int count, boolean transpose, FloatBuffer value) {
        GLES20.glUniformMatrix4fv(location, count, transpose, value);

    }

    @Override
    public void uniformMatrix4fv(int location, int count, boolean transpose, float[] value, int offset) {
        GLES20.glUniformMatrix4fv(location, count, transpose, value, offset);
    }

    @Override
    public void useProgram(int program) {
        GLES20.glUseProgram(program);

    }

    @Override
    public void validateProgram(int program) {
        GLES20.glValidateProgram(program);

    }

    @Override
    public void vertexAttrib1f(int indx, float x) {
        GLES20.glVertexAttrib1f(indx, x);

    }

    @Override
    public void vertexAttrib1fv(int indx, FloatBuffer values) {
        GLES20.glVertexAttrib1fv(indx, values);

    }

    @Override
    public void vertexAttrib2f(int indx, float x, float y) {
        GLES20.glVertexAttrib2f(indx, x, y);

    }

    @Override
    public void vertexAttrib2fv(int indx, FloatBuffer values) {
        GLES20.glVertexAttrib2fv(indx, values);

    }

    @Override
    public void vertexAttrib3f(int indx, float x, float y, float z) {
        GLES20.glVertexAttrib3f(indx, x, y, z);

    }

    @Override
    public void vertexAttrib3fv(int indx, FloatBuffer values) {
        GLES20.glVertexAttrib3fv(indx, values);

    }

    @Override
    public void vertexAttrib4f(int indx, float x, float y, float z, float w) {
        GLES20.glVertexAttrib4f(indx, x, y, z, w);

    }

    @Override
    public void vertexAttrib4fv(int indx, FloatBuffer values) {
        GLES20.glVertexAttrib4fv(indx, values);

    }

    @Override
    public void vertexAttribPointer(int indx, int size, int type, boolean normalized, int stride,
                                    Buffer ptr) {
        GLES20.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
    }

    @Override
    public void vertexAttribPointer(int indx, int size, int type, boolean normalized, int stride,
                                    int offset) {
        // FIXME check implementation!
        GLES20.glVertexAttribPointer(indx, size, type, normalized, stride, offset);
        //throw new UnsupportedOperationException("missing implementation");
    }

    @Override
    public void activeTexture(int texture) {
        GLES20.glActiveTexture(texture);

    }

    @Override
    public void bindTexture(int target, int texture) {
        GLES20.glBindTexture(target, texture);

    }

    @Override
    public void blendFunc(int sfactor, int dfactor) {
        GLES20.glBlendFunc(sfactor, dfactor);

    }

    @Override
    public void clear(int mask) {
        GLES20.glClear(mask);

    }

    @Override
    public void clearColor(float red, float green, float blue, float alpha) {
        GLES20.glClearColor(red, green, blue, alpha);

    }

    @Override
    public void clearDepthf(float depth) {
        GLES20.glClearDepthf(depth);

    }

    @Override
    public void clearStencil(int s) {
        GLES20.glClearStencil(s);

    }

    @Override
    public void colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        GLES20.glColorMask(red, green, blue, alpha);

    }

    @Override
    public void compressedTexImage2D(int target, int level, int internalformat, int width,
                                     int height, int border, int imageSize, Buffer data) {
        androidGL.glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, data);
    }

    @Override
    public void compressedTexSubImage2D(int target, int level, int xoffset, int yoffset,
                                        int width, int height, int format, int imageSize, Buffer data) {
        androidGL.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, data);
    }

    @Override
    public void copyTexImage2D(int target, int level, int internalformat, int x, int y,
                               int width, int height, int border) {
        GLES20.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border);
    }

    @Override
    public void copyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y,
                                  int width, int height) {
        GLES20.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
    }

    @Override
    public void cullFace(int mode) {
        GLES20.glCullFace(mode);

    }

    @Override
    public void deleteTextures(int n, IntBuffer textures) {
        GLES20.glDeleteTextures(n, textures);

    }

    @Override
    public void deleteTexture(int texture) {
        androidGL.glDeleteTexture(texture);
    }

    @Override
    public void depthFunc(int func) {
        GLES20.glDepthFunc(func);

    }

    @Override
    public void depthMask(boolean flag) {
        GLES20.glDepthMask(flag);

    }

    @Override
    public void depthRangef(float zNear, float zFar) {
        GLES20.glDepthRangef(zNear, zFar);

    }

    @Override
    public void disable(int cap) {
        GLES20.glDisable(cap);

    }

    @Override
    public void drawArrays(int mode, int first, int count) {
        GLES20.glDrawArrays(mode, first, count);

    }

    @Override
    public void drawElements(int mode, int count, int type, Buffer indices) {
        GLES20.glDrawElements(mode, count, type, indices);

    }

    @Override
    public void enable(int cap) {
        GLES20.glEnable(cap);

    }

    @Override
    public void finish() {
        GLES20.glFinish();

    }

    @Override
    public void flush() {
        GLES20.glFlush();

    }

    @Override
    public void frontFace(int mode) {
        GLES20.glFrontFace(mode);

    }

    @Override
    public void genTextures(int n, IntBuffer textures) {
        GLES20.glGenTextures(n, textures);

    }

    @Override
    public int genTexture() {
        return androidGL.glGenTexture();
    }

    @Override
    public int getError() {
        return GLES20.glGetError();
    }

    @Override
    public void getIntegerv(int pname, IntBuffer params) {
        GLES20.glGetIntegerv(pname, params);

    }

    @Override
    public String getString(int name) {
        return GLES20.glGetString(name);
    }

    @Override
    public void hint(int target, int mode) {
        GLES20.glHint(target, mode);
    }

    @Override
    public void lineWidth(float width) {
        GLES20.glLineWidth(width);

    }

    @Override
    public void pixelStorei(int pname, int param) {
        GLES20.glPixelStorei(pname, param);

    }

    @Override
    public void polygonOffset(float factor, float units) {
        GLES20.glPolygonOffset(factor, units);

    }

    @Override
    public void readPixels(int x, int y, int width, int height, int format, int type,
                           Buffer pixels) {
        GLES20.glReadPixels(x, y, width, height, format, type, pixels);
    }

    @Override
    public void scissor(int x, int y, int width, int height) {
        GLES20.glScissor(x, y, width, height);
    }

    @Override
    public void stencilFunc(int func, int ref, int mask) {
        GLES20.glStencilFunc(func, ref, mask);
    }

    @Override
    public void stencilMask(int mask) {
        GLES20.glStencilMask(mask);
    }

    @Override
    public void stencilOp(int fail, int zfail, int zpass) {
        GLES20.glStencilOp(fail, zfail, zpass);
    }

    @Override
    public void texImage2D(int target, int level, int internalformat, int width, int height,
                           int border, int format, int type, Buffer pixels) {
        GLES20.glTexImage2D(target, level, internalformat, width, height, border, format, type,
                pixels);
    }

    @Override
    public void texParameterf(int target, int pname, float param) {
        GLES20.glTexParameterf(target, pname, param);
    }

    @Override
    public void texSubImage2D(int target, int level, int xoffset, int yoffset, int width,
                              int height, int format, int type, Buffer pixels) {
        GLES20.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);

    }

    @Override
    public void viewport(int x, int y, int width, int height) {
        GLES20.glViewport(x, y, width, height);
    }

}
