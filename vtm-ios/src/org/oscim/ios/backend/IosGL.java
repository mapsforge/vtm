/*
 * Copyright 2016 Longri
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

import com.badlogic.gdx.backends.iosrobovm.IOSGLES20;

import org.oscim.backend.GL;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * iOS specific implementation of {@link GL}.
 */
public class IosGL implements GL {

    private static final IOSGLES20 iOSGL = new IOSGLES20();

    @Override
    public void activeTexture(int texture) {
        iOSGL.glActiveTexture(texture);
    }

    @Override
    public void bindTexture(int target, int texture) {
        iOSGL.glBindTexture(target, texture);
    }

    @Override
    public void blendFunc(int sfactor, int dfactor) {
        iOSGL.glBlendFunc(sfactor, dfactor);
    }

    @Override
    public void clear(int mask) {
        iOSGL.glClear(mask);
    }

    @Override
    public void clearColor(float red, float green, float blue, float alpha) {
        iOSGL.glClearColor(red, green, blue, alpha);
    }

    @Override
    public void clearDepthf(float depth) {
        iOSGL.glClearDepthf(depth);
    }

    @Override
    public void clearStencil(int s) {
        iOSGL.glClearStencil(s);
    }

    @Override
    public void colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        iOSGL.glColorMask(red, green, blue, alpha);
    }

    @Override
    public void compressedTexImage2D(int target, int level, int internalformat, int width,
                                     int height, int border,
                                     int imageSize, Buffer data) {
        iOSGL.glCompressedTexImage2D(
                target,
                level,
                internalformat,
                width,
                height,
                border,
                imageSize,
                data);
    }

    @Override
    public void compressedTexSubImage2D(int target, int level, int xoffset, int yoffset,
                                        int width, int height, int format,
                                        int imageSize, Buffer data) {
        iOSGL.glCompressedTexSubImage2D(
                target,
                level,
                xoffset,
                yoffset,
                width,
                height,
                format,
                imageSize,
                data);
    }

    @Override
    public void copyTexImage2D(int target, int level, int internalformat, int x, int y,
                               int width, int height, int border) {
        iOSGL.glCopyTexImage2D(
                target,
                level,
                internalformat,
                x,
                y,
                width,
                height,
                border);
    }

    @Override
    public void copyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y,
                                  int width, int height) {
        iOSGL.glCopyTexSubImage2D(
                target,
                level,
                xoffset,
                yoffset,
                x,
                y,
                width,
                height);
    }

    @Override
    public void cullFace(int mode) {
        iOSGL.glCullFace(mode);
    }

    @Override
    public void deleteTextures(int n, IntBuffer textures) {
        iOSGL.glDeleteTextures(n, textures);
    }

    @Override
    public void depthFunc(int func) {
        iOSGL.glDepthFunc(func);
    }

    @Override
    public void depthMask(boolean flag) {
        iOSGL.glDepthMask(flag);
    }

    @Override
    public void depthRangef(float zNear, float zFar) {
        iOSGL.glDepthRangef(zNear, zFar);
    }

    @Override
    public void disable(int cap) {
        iOSGL.glDisable(cap);
    }

    @Override
    public void drawArrays(int mode, int first, int count) {
        iOSGL.glDrawArrays(mode, first, count);
    }

    @Override
    public void drawElements(int mode, int count, int type, Buffer indices) {
        iOSGL.glDrawElements(mode, count, type, indices);
    }

    @Override
    public void enable(int cap) {
        iOSGL.glEnable(cap);
    }

    @Override
    public void finish() {
        iOSGL.glFinish();
    }

    @Override
    public void flush() {
        iOSGL.glFlush();
    }

    @Override
    public void frontFace(int mode) {
        iOSGL.glFrontFace(mode);
    }

    @Override
    public void genTextures(int n, IntBuffer textures) {
        iOSGL.glGenTextures(n, textures);
    }

    @Override
    public int getError() {
        return iOSGL.glGetError();
    }

    @Override
    public void getIntegerv(int pname, IntBuffer params) {
        iOSGL.glGetIntegerv(pname, params);
    }

    @Override
    public String getString(int name) {
        return iOSGL.glGetString(name);
    }

    @Override
    public void hint(int target, int mode) {
        iOSGL.glHint(target, mode);
    }

    @Override
    public void lineWidth(float width) {
        iOSGL.glLineWidth(width);
    }

    @Override
    public void pixelStorei(int pname, int param) {
        iOSGL.glPixelStorei(pname, param);
    }

    @Override
    public void polygonOffset(float factor, float units) {
        iOSGL.glPolygonOffset(factor, units);
    }

    @Override
    public void readPixels(int x, int y, int width, int height, int format, int type,
                           Buffer pixels) {
        iOSGL.glReadPixels(
                x,
                y,
                width,
                height,
                format,
                type,
                pixels);
    }

    @Override
    public void scissor(int x, int y, int width, int height) {
        iOSGL.glScissor(x, y, width, height);
    }

    @Override
    public void stencilFunc(int func, int ref, int mask) {
        iOSGL.glStencilFunc(func, ref, mask);
    }

    @Override
    public void stencilMask(int mask) {
        iOSGL.glStencilMask(mask);
    }

    @Override
    public void stencilOp(int fail, int zfail, int zpass) {
        iOSGL.glStencilOp(fail, zfail, zpass);
    }

    @Override
    public void texImage2D(int target, int level, int internalFormat, int width, int height,
                           int border, int format, int type,
                           Buffer pixels) {
        iOSGL.glTexImage2D(
                target,
                level,
                internalFormat,
                width,
                height,
                border,
                format,
                type,
                pixels);
    }

    @Override
    public void texParameterf(int target, int pname, float param) {
        iOSGL.glTexParameterf(target, pname, param);
    }

    @Override
    public void texSubImage2D(int target, int level, int xoffset, int yoffset, int width,
                              int height, int format, int type,
                              Buffer pixels) {
        iOSGL.glTexSubImage2D(
                target,
                level,
                xoffset,
                yoffset,
                width,
                height,
                format,
                type,
                pixels);
    }

    @Override
    public void viewport(int x, int y, int width, int height) {
        iOSGL.glViewport(x, y, width, height);
    }

    @Override
    public void getFloatv(int pname, FloatBuffer params) {
        iOSGL.glGetFloatv(pname, params);
    }

    @Override
    public void getTexParameterfv(int target, int pname, FloatBuffer params) {
        iOSGL.glGetTexParameterfv(target, pname, params);
    }

    @Override
    public void texParameterfv(int target, int pname, FloatBuffer params) {
        iOSGL.glTexParameterfv(target, pname, params);
    }

    @Override
    public void bindBuffer(int target, int buffer) {
        iOSGL.glBindBuffer(target, buffer);
    }

    @Override
    public void bufferData(int target, int size, Buffer data, int usage) {
        iOSGL.glBufferData(target, size, data, usage);
    }

    @Override
    public void bufferSubData(int target, int offset, int size, Buffer data) {
        iOSGL.glBufferSubData(target, offset, size, data);
    }

    @Override
    public void deleteBuffers(int n, IntBuffer buffers) {
        iOSGL.glDeleteBuffers(n, buffers);
    }

    @Override
    public void getBufferParameteriv(int target, int pname, IntBuffer params) {
        iOSGL.glGetBufferParameteriv(target, pname, params);
    }

    @Override
    public void genBuffers(int n, IntBuffer buffers) {
        iOSGL.glGenBuffers(n, buffers);
    }

    @Override
    public void getTexParameteriv(int target, int pname, IntBuffer params) {
        iOSGL.glGetTexParameteriv(target, pname, params);
    }

    @Override
    public boolean isBuffer(int buffer) {
        return iOSGL.glIsBuffer(buffer);
    }

    @Override
    public boolean isEnabled(int cap) {
        return iOSGL.glIsEnabled(cap);
    }

    @Override
    public boolean isTexture(int texture) {
        return iOSGL.glIsTexture(texture);
    }

    @Override
    public void texParameteri(int target, int pname, int param) {
        iOSGL.glTexParameteri(target, pname, param);
    }

    @Override
    public void texParameteriv(int target, int pname, IntBuffer params) {
        iOSGL.glTexParameteriv(target, pname, params);
    }

    @Override
    public void drawElements(int mode, int count, int type, int indices) {
        iOSGL.glDrawElements(mode, count, type, indices);
    }

    @Override
    public void attachShader(int program, int shader) {
        iOSGL.glAttachShader(program, shader);
    }

    @Override
    public void bindAttribLocation(int program, int index, String name) {
        iOSGL.glBindAttribLocation(program, index, name);
    }

    @Override
    public void bindFramebuffer(int target, int framebuffer) {
        iOSGL.glBindFramebuffer(target, framebuffer);
    }

    @Override
    public void bindRenderbuffer(int target, int renderbuffer) {
        iOSGL.glBindRenderbuffer(target, renderbuffer);
    }

    @Override
    public void blendColor(float red, float green, float blue, float alpha) {
        iOSGL.glBlendColor(red, green, blue, alpha);
    }

    @Override
    public void blendEquation(int mode) {
        iOSGL.glBlendEquation(mode);
    }

    @Override
    public void blendEquationSeparate(int modeRGB, int modeAlpha) {
        iOSGL.glBlendEquationSeparate(modeRGB, modeAlpha);
    }

    @Override
    public void blendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        iOSGL.glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
    }

    @Override
    public int checkFramebufferStatus(int target) {
        return iOSGL.glCheckFramebufferStatus(target);
    }

    @Override
    public void compileShader(int shader) {
        iOSGL.glCompileShader(shader);
    }

    @Override
    public int createProgram() {
        return iOSGL.glCreateProgram();
    }

    @Override
    public int createShader(int type) {
        return iOSGL.glCreateShader(type);
    }

    @Override
    public void deleteFramebuffers(int n, IntBuffer framebuffers) {
        iOSGL.glDeleteFramebuffers(n, framebuffers);
    }

    @Override
    public void deleteProgram(int program) {
        iOSGL.glDeleteProgram(program);
    }

    @Override
    public void deleteRenderbuffers(int n, IntBuffer renderbuffers) {
        iOSGL.glDeleteRenderbuffers(
                n,
                renderbuffers);
    }

    @Override
    public void deleteShader(int shader) {
        iOSGL.glDeleteShader(shader);
    }

    @Override
    public void detachShader(int program, int shader) {
        iOSGL.glDetachShader(program, shader);
    }

    @Override
    public void disableVertexAttribArray(int index) {
        iOSGL.glDisableVertexAttribArray(index);
    }

    @Override
    public void enableVertexAttribArray(int index) {
        iOSGL.glEnableVertexAttribArray(index);
    }

    @Override
    public void framebufferRenderbuffer(int target, int attachment, int renderbuffertarget,
                                        int renderbuffer) {
        iOSGL.glFramebufferRenderbuffer(
                target,
                attachment,
                renderbuffertarget,
                renderbuffer);
    }

    @Override
    public void framebufferTexture2D(int target, int attachment, int textarget, int texture,
                                     int level) {
        iOSGL.glFramebufferTexture2D(
                target,
                attachment,
                textarget,
                texture,
                level);
    }

    @Override
    public void generateMipmap(int target) {
        iOSGL.glGenerateMipmap(target);
    }

    @Override
    public void genFramebuffers(int n, IntBuffer framebuffers) {
        iOSGL.glGenFramebuffers(n, framebuffers);
    }

    @Override
    public void genRenderbuffers(int n, IntBuffer renderbuffers) {
        iOSGL
                .glGenRenderbuffers(n, renderbuffers);
    }

    @Override
    public String getActiveAttrib(int program, int index, IntBuffer size, Buffer type) {
        return iOSGL.glGetActiveAttrib(
                program,
                index,
                size,
                type);
    }

    @Override
    public String getActiveUniform(int program, int index, IntBuffer size, Buffer type) {
        return iOSGL.glGetActiveUniform(
                program,
                index,
                size,
                type);
    }

    @Override
    public void getAttachedShaders(int program, int maxcount, Buffer count, IntBuffer shaders) {
        iOSGL.glGetAttachedShaders(
                program,
                maxcount,
                count,
                shaders);
    }

    @Override
    public int getAttribLocation(int program, String name) {
        return iOSGL.glGetAttribLocation(program, name);
    }

    @Override
    public void getBooleanv(int pname, Buffer params) {
        iOSGL.glGetBooleanv(pname, params);
    }

    @Override
    public void getFramebufferAttachmentParameteriv(int target, int attachment, int pname,
                                                    IntBuffer params) {
        iOSGL.glGetFramebufferAttachmentParameteriv(
                target,
                attachment,
                pname,
                params);
    }

    @Override
    public void getProgramiv(int program, int pname, IntBuffer params) {
        iOSGL.glGetProgramiv(program, pname, params);
    }

    @Override
    public String getProgramInfoLog(int program) {
        return iOSGL.glGetProgramInfoLog(program);
    }

    @Override
    public void getRenderbufferParameteriv(int target, int pname, IntBuffer params) {
        iOSGL.glGetRenderbufferParameteriv(
                target,
                pname,
                params);
    }

    @Override
    public void getShaderiv(int shader, int pname, IntBuffer params) {
        iOSGL.glGetShaderiv(shader, pname, params);
    }

    @Override
    public String getShaderInfoLog(int shader) {
        return iOSGL.glGetShaderInfoLog(shader);
    }

    @Override
    public void getShaderPrecisionFormat(int shadertype, int precisiontype, IntBuffer range,
                                         IntBuffer precision) {
        iOSGL.glGetShaderPrecisionFormat(
                shadertype,
                precisiontype,
                range,
                precision);
    }

    @Override
    public void getShaderSource(int shader, int bufsize, Buffer length, String source) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void getUniformfv(int program, int location, FloatBuffer params) {
        iOSGL.glGetUniformfv(program, location, params);
    }

    @Override
    public void getUniformiv(int program, int location, IntBuffer params) {
        iOSGL.glGetUniformiv(program, location, params);
    }

    @Override
    public int getUniformLocation(int program, String name) {
        return iOSGL.glGetUniformLocation(program, name);
    }

    @Override
    public void getVertexAttribfv(int index, int pname, FloatBuffer params) {
        iOSGL.glGetVertexAttribfv(index, pname, params);
    }

    @Override
    public void getVertexAttribiv(int index, int pname, IntBuffer params) {
        iOSGL.glGetVertexAttribiv(index, pname, params);
    }

    @Override
    public void getVertexAttribPointerv(int index, int pname, Buffer pointer) {
        iOSGL.glGetVertexAttribPointerv(
                index,
                pname,
                pointer);
    }

    @Override
    public boolean isFramebuffer(int framebuffer) {
        return iOSGL.glIsFramebuffer(framebuffer);
    }

    @Override
    public boolean isProgram(int program) {
        return iOSGL.glIsProgram(program);
    }

    @Override
    public boolean isRenderbuffer(int renderbuffer) {
        return iOSGL.glIsRenderbuffer(renderbuffer);
    }

    @Override
    public boolean isShader(int shader) {
        return iOSGL.glIsShader(shader);
    }

    @Override
    public void linkProgram(int program) {
        iOSGL.glLinkProgram(program);
    }

    @Override
    public void releaseShaderCompiler() {
        iOSGL.glReleaseShaderCompiler();
    }

    @Override
    public void renderbufferStorage(int target, int internalformat, int width, int height) {
        iOSGL.glRenderbufferStorage(target, internalformat, width, height);
    }

    @Override
    public void sampleCoverage(float value, boolean invert) {
        iOSGL.glSampleCoverage(value, invert);
    }

    @Override
    public void shaderBinary(int n, IntBuffer shaders, int binaryformat, Buffer binary, int length) {
        iOSGL.glShaderBinary(
                n,
                shaders,
                binaryformat,
                binary,
                length);
    }

    @Override
    public void shaderSource(int shader, String string) {
        iOSGL.glShaderSource(shader, string);
    }

    @Override
    public void stencilFuncSeparate(int face, int func, int ref, int mask) {
        iOSGL.glStencilFuncSeparate(face, func, ref, mask);
    }

    @Override
    public void stencilMaskSeparate(int face, int mask) {
        iOSGL.glStencilMaskSeparate(face, mask);
    }

    @Override
    public void stencilOpSeparate(int face, int fail, int zfail, int zpass) {
        iOSGL.glStencilOpSeparate(face, fail, zfail, zpass);
    }

    @Override
    public void uniform1f(int location, float x) {
        iOSGL.glUniform1f(location, x);
    }

    @Override
    public void uniform1fv(int location, int count, FloatBuffer v) {
        iOSGL.glUniform1fv(location, count, v);
    }

    @Override
    public void uniform1i(int location, int x) {
        iOSGL.glUniform1i(location, x);
    }

    @Override
    public void uniform1iv(int location, int count, IntBuffer v) {
        iOSGL.glUniform1iv(location, count, v);
    }

    @Override
    public void uniform2f(int location, float x, float y) {
        iOSGL.glUniform2f(location, x, y);
    }

    @Override
    public void uniform2fv(int location, int count, FloatBuffer v) {
        iOSGL.glUniform2fv(location, count, v);
    }

    @Override
    public void uniform2i(int location, int x, int y) {
        iOSGL.glUniform2i(location, x, y);
    }

    @Override
    public void uniform2iv(int location, int count, IntBuffer v) {
        iOSGL.glUniform2iv(location, count, v);
    }

    @Override
    public void uniform3f(int location, float x, float y, float z) {
        iOSGL.glUniform3f(location, x, y, z);
    }

    @Override
    public void uniform3fv(int location, int count, FloatBuffer v) {
        iOSGL.glUniform3fv(location, count, v);
    }

    @Override
    public void uniform3i(int location, int x, int y, int z) {
        iOSGL.glUniform3i(location, x, y, z);
    }

    @Override
    public void uniform3iv(int location, int count, IntBuffer v) {
        iOSGL.glUniform3iv(location, count, v);
    }

    @Override
    public void uniform4f(int location, float x, float y, float z, float w) {
        iOSGL.glUniform4f(location, x, y, z, w);
    }

    @Override
    public void uniform4fv(int location, int count, FloatBuffer v) {
        iOSGL.glUniform4fv(location, count, v);
    }

    @Override
    public void uniform4i(int location, int x, int y, int z, int w) {
        iOSGL.glUniform4i(location, x, y, z, w);
    }

    @Override
    public void uniform4iv(int location, int count, IntBuffer v) {
        iOSGL.glUniform4iv(location, count, v);
    }

    @Override
    public void uniformMatrix2fv(int location, int count, boolean transpose, FloatBuffer value) {
        iOSGL.glUniformMatrix2fv(
                location,
                count,
                transpose,
                value);
    }

    @Override
    public void uniformMatrix3fv(int location, int count, boolean transpose, FloatBuffer value) {
        iOSGL.glUniformMatrix3fv(
                location,
                count,
                transpose,
                value);
    }

    @Override
    public void uniformMatrix4fv(int location, int count, boolean transpose, FloatBuffer value) {
        iOSGL.glUniformMatrix4fv(
                location,
                count,
                transpose,
                value);
    }

    @Override
    public void useProgram(int program) {
        iOSGL.glUseProgram(program);
    }

    @Override
    public void validateProgram(int program) {
        iOSGL.glValidateProgram(program);
    }

    @Override
    public void vertexAttrib1f(int indx, float x) {
        iOSGL.glVertexAttrib1f(indx, x);
    }

    @Override
    public void vertexAttrib1fv(int indx, FloatBuffer values) {
        iOSGL.glVertexAttrib1fv(indx, values);
    }

    @Override
    public void vertexAttrib2f(int indx, float x, float y) {
        iOSGL.glVertexAttrib2f(indx, x, y);
    }

    @Override
    public void vertexAttrib2fv(int indx, FloatBuffer values) {
        iOSGL.glVertexAttrib2fv(indx, values);
    }

    @Override
    public void vertexAttrib3f(int indx, float x, float y, float z) {
        iOSGL.glVertexAttrib3f(indx, x, y, z);
    }

    @Override
    public void vertexAttrib3fv(int indx, FloatBuffer values) {
        iOSGL.glVertexAttrib3fv(indx, values);
    }

    @Override
    public void vertexAttrib4f(int indx, float x, float y, float z, float w) {
        iOSGL.glVertexAttrib4f(indx, x, y, z, w);
    }

    @Override
    public void vertexAttrib4fv(int indx, FloatBuffer values) {
        iOSGL.glVertexAttrib4fv(indx, values);
    }

    @Override
    public void vertexAttribPointer(int indx, int size, int type, boolean normalized, int stride,
                                    Buffer ptr) {
        iOSGL.glVertexAttribPointer(
                indx,
                size,
                type,
                normalized,
                stride,
                ptr);
    }

    @Override
    public void vertexAttribPointer(int indx, int size, int type, boolean normalized, int stride,
                                    int ptr) {
        iOSGL.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
    }
}
