package org.oscim.ios.backend;

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

import com.badlogic.gdx.backends.iosrobovm.IOSGLES20;
import org.oscim.backend.GL;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class IosGL implements GL {
	
	private static final IOSGLES20 iOSGL=new IOSGLES20();
	
	
	public void activeTexture(int texture) {
		iOSGL.glActiveTexture(texture);
	}

	public void bindTexture(int target, int texture) {
		iOSGL.glBindTexture(target, texture);
	}

	public void blendFunc(int sfactor, int dfactor) {
		iOSGL.glBlendFunc(sfactor, dfactor);
	}

	public void clear(int mask) {
		iOSGL.glClear(mask);
	}

	public void clearColor(float red, float green, float blue, float alpha) {
		iOSGL.glClearColor(red, green, blue, alpha);
	}

	public void clearDepthf(float depth) {
		iOSGL.glClearDepthf(depth);
	}

	public void clearStencil(int s) {
		iOSGL.glClearStencil(s);
	}

	public void colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
		iOSGL.glColorMask(red, green, blue, alpha);
	}

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

	public void cullFace(int mode) {
		iOSGL.glCullFace(mode);
	}

	public void deleteTextures(int n, IntBuffer textures) {
		iOSGL.glDeleteTextures(n, textures);
	}

	public void depthFunc(int func) {
		iOSGL.glDepthFunc(func);
	}

	public void depthMask(boolean flag) {
		iOSGL.glDepthMask(flag);
	}

	public void depthRangef(float zNear, float zFar) {
		iOSGL.glDepthRangef(zNear, zFar);
	}

	public void disable(int cap) {
		iOSGL.glDisable(cap);
	}

	public void drawArrays(int mode, int first, int count) {
		iOSGL.glDrawArrays(mode, first, count);
	}

	public void drawElements(int mode, int count, int type, Buffer indices) {
		iOSGL.glDrawElements(mode, count, type, indices);
	}

	public void enable(int cap) {
		iOSGL.glEnable(cap);
	}

	public void finish() {
		iOSGL.glFinish();
	}

	public void flush() {
		iOSGL.glFlush();
	}

	public void frontFace(int mode) {
		iOSGL.glFrontFace(mode);
	}

	public void genTextures(int n, IntBuffer textures) {
		iOSGL.glGenTextures(n, textures );
	}

	public int getError() {
		return iOSGL.glGetError();
	}

	public void getIntegerv(int pname, IntBuffer params) {
		iOSGL.glGetIntegerv(pname, params);
	}

	public String getString(int name) {
		return iOSGL.glGetString(name);
	}

	public void hint(int target, int mode) {
		iOSGL.glHint(target, mode);
	}

	public void lineWidth(float width) {
		iOSGL.glLineWidth(width);
	}

	public void pixelStorei(int pname, int param) {
		iOSGL.glPixelStorei(pname, param);
	}

	public void polygonOffset(float factor, float units) {
		iOSGL.glPolygonOffset(factor, units);
	}

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

	public void scissor(int x, int y, int width, int height) {
		iOSGL.glScissor(x, y, width, height);
	}

	public void stencilFunc(int func, int ref, int mask) {
		iOSGL.glStencilFunc(func, ref, mask);
	}

	public void stencilMask(int mask) {
		iOSGL.glStencilMask(mask);
	}

	public void stencilOp(int fail, int zfail, int zpass) {
		iOSGL.glStencilOp(fail, zfail, zpass);
	}

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

	public void texParameterf(int target, int pname, float param) {
		iOSGL.glTexParameterf(target, pname, param);
	}

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

	public void viewport(int x, int y, int width, int height) {
		iOSGL.glViewport(x, y, width, height);
	}

	public void getFloatv(int pname, FloatBuffer params) {
		iOSGL.glGetFloatv(pname, params);
	}

	public void getTexParameterfv(int target, int pname, FloatBuffer params) {
		iOSGL.glGetTexParameterfv(target, pname, params);
	}

	public void texParameterfv(int target, int pname, FloatBuffer params) {
		iOSGL.glTexParameterfv(target, pname, params);
	}

	public void bindBuffer(int target, int buffer) {
		iOSGL.glBindBuffer(target, buffer);
	}

	public void bufferData(int target, int size, Buffer data, int usage) {
		iOSGL.glBufferData(target, size, data, usage);
	}

	public void bufferSubData(int target, int offset, int size, Buffer data) {
		iOSGL.glBufferSubData(target, offset, size, data);
	}

	public void deleteBuffers(int n, IntBuffer buffers) {
		iOSGL.glDeleteBuffers(n, buffers);
	}

	public void getBufferParameteriv(int target, int pname, IntBuffer params) {
		iOSGL.glGetBufferParameteriv(target, pname, params);
	}

	public void genBuffers(int n, IntBuffer buffers) {
		iOSGL.glGenBuffers(n, buffers);
	}

	public void getTexParameteriv(int target, int pname, IntBuffer params) {
		iOSGL.glGetTexParameteriv(target, pname, params);
	}

	public boolean isBuffer(int buffer) {
		return iOSGL.glIsBuffer(buffer);
	}

	public boolean isEnabled(int cap) {
		return iOSGL.glIsEnabled(cap);
	}

	public boolean isTexture(int texture) {
		return iOSGL.glIsTexture(texture);
	}

	public void texParameteri(int target, int pname, int param) {
		iOSGL.glTexParameteri(target, pname, param);
	}

	public void texParameteriv(int target, int pname, IntBuffer params) {
		iOSGL.glTexParameteriv(target, pname, params);
	}

	public void drawElements(int mode, int count, int type, int indices) {
		iOSGL.glDrawElements(mode, count, type, indices);
	}

	public void attachShader(int program, int shader) {
		iOSGL.glAttachShader(program, shader);
	}

	public void bindAttribLocation(int program, int index, String name) {
		iOSGL.glBindAttribLocation(program, index, name);
	}

	public void bindFramebuffer(int target, int framebuffer) {
		iOSGL.glBindFramebuffer(target, framebuffer);
	}

	public void bindRenderbuffer(int target, int renderbuffer) {
		iOSGL.glBindRenderbuffer(target, renderbuffer);
	}

	public void blendColor(float red, float green, float blue, float alpha) {
		iOSGL.glBlendColor(red, green, blue, alpha);
	}

	public void blendEquation(int mode) {
		iOSGL.glBlendEquation(mode);
	}

	public void blendEquationSeparate(int modeRGB, int modeAlpha) {
		iOSGL.glBlendEquationSeparate(modeRGB, modeAlpha);
	}

	public void blendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
		iOSGL.glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
	}

	public int checkFramebufferStatus(int target) {
		return iOSGL.glCheckFramebufferStatus(target);
	}

	public void compileShader(int shader) {
		iOSGL.glCompileShader(shader);
	}

	public int createProgram() {
		return iOSGL.glCreateProgram();
	}

	public int createShader(int type) {
		return iOSGL.glCreateShader(type);
	}

	public void deleteFramebuffers(int n, IntBuffer framebuffers) {
		iOSGL.glDeleteFramebuffers(n, framebuffers);
	}

	public void deleteProgram(int program) {
		iOSGL.glDeleteProgram(program);
	}

	public void deleteRenderbuffers(int n, IntBuffer renderbuffers) {
		iOSGL.glDeleteRenderbuffers(
		                                                  n,
		                                                  renderbuffers);
	}

	public void deleteShader(int shader) {
		iOSGL.glDeleteShader(shader);
	}

	public void detachShader(int program, int shader) {
		iOSGL.glDetachShader(program, shader);
	}

	public void disableVertexAttribArray(int index) {
		iOSGL.glDisableVertexAttribArray(index);
	}

	public void enableVertexAttribArray(int index) {
		iOSGL.glEnableVertexAttribArray(index);
	}

	public void framebufferRenderbuffer(int target, int attachment, int renderbuffertarget,
	        int renderbuffer) {
		iOSGL.glFramebufferRenderbuffer(
		                                                      target,
		                                                      attachment,
		                                                      renderbuffertarget,
		                                                      renderbuffer);
	}

	public void framebufferTexture2D(int target, int attachment, int textarget, int texture,
	        int level) {
		iOSGL.glFramebufferTexture2D(
		                                                   target,
		                                                   attachment,
		                                                   textarget,
		                                                   texture,
		                                                   level);
	}

	public void generateMipmap(int target) {
		iOSGL.glGenerateMipmap(target);
	}

	public void genFramebuffers(int n, IntBuffer framebuffers) {
		iOSGL.glGenFramebuffers(n, framebuffers);
	}

	public void genRenderbuffers(int n, IntBuffer renderbuffers) {
		iOSGL
		    .glGenRenderbuffers(n, renderbuffers);
	}

	public String getActiveAttrib(int program, int index, IntBuffer size, Buffer type) {
		return iOSGL.glGetActiveAttrib(
		                                                  program,
		                                                  index,
		                                                  size,
		                                                  type);
	}

	public String getActiveUniform(int program, int index, IntBuffer size, Buffer type) {
		return iOSGL.glGetActiveUniform(
		                                                   program,
		                                                   index,
		                                                   size,
		                                                   type);
	}

	public void getAttachedShaders(int program, int maxcount, Buffer count, IntBuffer shaders) {
		iOSGL.glGetAttachedShaders(
		                                              program,
		                                              maxcount,
		                                              count,
		                                              shaders);
	}

	public int getAttribLocation(int program, String name) {
		return iOSGL.glGetAttribLocation(program, name);
	}

	public void getBooleanv(int pname, Buffer params) {
		iOSGL.glGetBooleanv(pname, params);
	}

	public void getFramebufferAttachmentParameteriv(int target, int attachment, int pname,
	        IntBuffer params) {
		iOSGL.glGetFramebufferAttachmentParameteriv(
		                                                                  target,
		                                                                  attachment,
		                                                                  pname,
		                                                                  params);
	}

	public void getProgramiv(int program, int pname, IntBuffer params) {
		iOSGL.glGetProgramiv(program, pname, params);
	}

	public String getProgramInfoLog(int program) {
		return iOSGL.glGetProgramInfoLog(program);
	}

	public void getRenderbufferParameteriv(int target, int pname, IntBuffer params) {
		iOSGL.glGetRenderbufferParameteriv(
		                                                         target,
		                                                         pname,
		                                                         params);
	}

	public void getShaderiv(int shader, int pname, IntBuffer params) {
		iOSGL.glGetShaderiv(shader, pname, params);
	}

	public String getShaderInfoLog(int shader) {
		return iOSGL.glGetShaderInfoLog(shader);
	}

	public void getShaderPrecisionFormat(int shadertype, int precisiontype, IntBuffer range,
	        IntBuffer precision) {
		iOSGL.glGetShaderPrecisionFormat(
		                                                    shadertype,
		                                                    precisiontype,
		                                                    range,
		                                                    precision);
	}

	public void getShaderSource(int shader, int bufsize, Buffer length, String source) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public void getUniformfv(int program, int location, FloatBuffer params) {
		iOSGL.glGetUniformfv(program, location, params);
	}

	public void getUniformiv(int program, int location, IntBuffer params) {
		iOSGL.glGetUniformiv(program, location, params);
	}

	public int getUniformLocation(int program, String name) {
		return iOSGL.glGetUniformLocation(program, name);
	}

	public void getVertexAttribfv(int index, int pname, FloatBuffer params) {
		iOSGL.glGetVertexAttribfv(index, pname, params);
	}

	public void getVertexAttribiv(int index, int pname, IntBuffer params) {
		iOSGL.glGetVertexAttribiv(index, pname, params);
	}

	public void getVertexAttribPointerv(int index, int pname, Buffer pointer) {
		iOSGL.glGetVertexAttribPointerv(
		                                                   index,
		                                                   pname,
		                                                   pointer);
	}

	public boolean isFramebuffer(int framebuffer) {
		return iOSGL.glIsFramebuffer(framebuffer);
	}

	public boolean isProgram(int program) {
		return iOSGL.glIsProgram(program);
	}

	public boolean isRenderbuffer(int renderbuffer) {
		return iOSGL.glIsRenderbuffer(renderbuffer);
	}

	public boolean isShader(int shader) {
		return iOSGL.glIsShader(shader);
	}

	public void linkProgram(int program) {
		iOSGL.glLinkProgram(program);
	}

	public void releaseShaderCompiler() {
		iOSGL.glReleaseShaderCompiler();
	}

	public void renderbufferStorage(int target, int internalformat, int width, int height) {
		iOSGL.glRenderbufferStorage(target, internalformat, width, height);
	}

	public void sampleCoverage(float value, boolean invert) {
		iOSGL.glSampleCoverage(value, invert);
	}

	public void shaderBinary(int n, IntBuffer shaders, int binaryformat, Buffer binary, int length) {
		iOSGL.glShaderBinary(
		                                        n,
		                                        shaders,
		                                        binaryformat,
		                                        binary,
		                                        length);
	}

	public void shaderSource(int shader, String string) {
		iOSGL.glShaderSource(shader, string);
	}

	public void stencilFuncSeparate(int face, int func, int ref, int mask) {
		iOSGL.glStencilFuncSeparate(face, func, ref, mask);
	}

	public void stencilMaskSeparate(int face, int mask) {
		iOSGL.glStencilMaskSeparate(face, mask);
	}

	public void stencilOpSeparate(int face, int fail, int zfail, int zpass) {
		iOSGL.glStencilOpSeparate(face, fail, zfail, zpass);
	}

	public void uniform1f(int location, float x) {
		iOSGL.glUniform1f(location, x);
	}

	public void uniform1fv(int location, int count, FloatBuffer v) {
		iOSGL.glUniform1fv(location, count, v);
	}

	public void uniform1i(int location, int x) {
		iOSGL.glUniform1i(location, x);
	}

	public void uniform1iv(int location, int count, IntBuffer v) {
		iOSGL.glUniform1iv(location, count, v);
	}

	public void uniform2f(int location, float x, float y) {
		iOSGL.glUniform2f(location, x, y);
	}

	public void uniform2fv(int location, int count, FloatBuffer v) {
		iOSGL.glUniform2fv(location, count, v);
	}

	public void uniform2i(int location, int x, int y) {
		iOSGL.glUniform2i(location, x, y);
	}

	public void uniform2iv(int location, int count, IntBuffer v) {
		iOSGL.glUniform2iv(location, count, v);
	}

	public void uniform3f(int location, float x, float y, float z) {
		iOSGL.glUniform3f(location, x, y, z);
	}

	public void uniform3fv(int location, int count, FloatBuffer v) {
		iOSGL.glUniform3fv(location, count, v);
	}

	public void uniform3i(int location, int x, int y, int z) {
		iOSGL.glUniform3i(location, x, y, z);
	}

	public void uniform3iv(int location, int count, IntBuffer v) {
		iOSGL.glUniform3iv(location, count, v);
	}

	public void uniform4f(int location, float x, float y, float z, float w) {
		iOSGL.glUniform4f(location, x, y, z, w);
	}

	public void uniform4fv(int location, int count, FloatBuffer v) {
		iOSGL.glUniform4fv(location, count, v);
	}

	public void uniform4i(int location, int x, int y, int z, int w) {
		iOSGL.glUniform4i(location, x, y, z, w);
	}

	public void uniform4iv(int location, int count, IntBuffer v) {
		iOSGL.glUniform4iv(location, count, v);
	}

	public void uniformMatrix2fv(int location, int count, boolean transpose, FloatBuffer value) {
		iOSGL.glUniformMatrix2fv(
		                                            location,
		                                            count,
		                                            transpose,
		                                            value);
	}

	public void uniformMatrix3fv(int location, int count, boolean transpose, FloatBuffer value) {
		iOSGL.glUniformMatrix3fv(
		                                            location,
		                                            count,
		                                            transpose,
		                                            value);
	}

	public void uniformMatrix4fv(int location, int count, boolean transpose, FloatBuffer value) {
		iOSGL.glUniformMatrix4fv(
		                                            location,
		                                            count,
		                                            transpose,
		                                            value);
	}

	public void useProgram(int program) {
		iOSGL.glUseProgram(program);
	}

	public void validateProgram(int program) {
		iOSGL.glValidateProgram(program);
	}

	public void vertexAttrib1f(int indx, float x) {
		iOSGL.glVertexAttrib1f(indx, x);
	}

	public void vertexAttrib1fv(int indx, FloatBuffer values) {
		iOSGL.glVertexAttrib1fv(indx, values);
	}

	public void vertexAttrib2f(int indx, float x, float y) {
		iOSGL.glVertexAttrib2f(indx, x, y);
	}

	public void vertexAttrib2fv(int indx, FloatBuffer values) {
		iOSGL.glVertexAttrib2fv(indx, values);
	}

	public void vertexAttrib3f(int indx, float x, float y, float z) {
		iOSGL.glVertexAttrib3f(indx, x, y, z);
	}

	public void vertexAttrib3fv(int indx, FloatBuffer values) {
		iOSGL.glVertexAttrib3fv(indx, values);
	}

	public void vertexAttrib4f(int indx, float x, float y, float z, float w) {
		iOSGL.glVertexAttrib4f(indx, x, y, z, w);
	}

	public void vertexAttrib4fv(int indx, FloatBuffer values) {
		iOSGL.glVertexAttrib4fv(indx, values);
	}

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

	public void vertexAttribPointer(int indx, int size, int type, boolean normalized, int stride,
	        int ptr) {
		iOSGL.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
	}
}
