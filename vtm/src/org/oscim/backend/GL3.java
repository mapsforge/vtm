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

/*
 **
 ** Copyright 2013, The Android Open Source Project
 **
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

// This source file is automatically generated
package org.oscim.backend;

import java.nio.Buffer;

/**
 * OpenGL ES 3.0
 * <p>
 * See: https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/graphics/GL30.java
 */
public interface GL3 extends GL {
    public final int READ_BUFFER = 0x0C02;
    public final int UNPACK_ROW_LENGTH = 0x0CF2;
    public final int UNPACK_SKIP_ROWS = 0x0CF3;
    public final int UNPACK_SKIP_PIXELS = 0x0CF4;
    public final int PACK_ROW_LENGTH = 0x0D02;
    public final int PACK_SKIP_ROWS = 0x0D03;
    public final int PACK_SKIP_PIXELS = 0x0D04;
    public final int COLOR = 0x1800;
    public final int DEPTH = 0x1801;
    public final int STENCIL = 0x1802;
    public final int RED = 0x1903;
    public final int RGB8 = 0x8051;
    public final int RGBA8 = 0x8058;
    public final int RGB10_A2 = 0x8059;
    public final int TEXTURE_BINDING_3D = 0x806A;
    public final int UNPACK_SKIP_IMAGES = 0x806D;
    public final int UNPACK_IMAGE_HEIGHT = 0x806E;
    public final int TEXTURE_3D = 0x806F;
    public final int TEXTURE_WRAP_R = 0x8072;
    public final int MAX_3D_TEXTURE_SIZE = 0x8073;
    public final int UNSIGNED_INT_2_10_10_10_REV = 0x8368;
    public final int MAX_ELEMENTS_VERTICES = 0x80E8;
    public final int MAX_ELEMENTS_INDICES = 0x80E9;
    public final int TEXTURE_MIN_LOD = 0x813A;
    public final int TEXTURE_MAX_LOD = 0x813B;
    public final int TEXTURE_BASE_LEVEL = 0x813C;
    public final int TEXTURE_MAX_LEVEL = 0x813D;
    public final int MIN = 0x8007;
    public final int MAX = 0x8008;
    public final int DEPTH_COMPONENT24 = 0x81A6;
    public final int MAX_TEXTURE_LOD_BIAS = 0x84FD;
    public final int TEXTURE_COMPARE_MODE = 0x884C;
    public final int TEXTURE_COMPARE_FUNC = 0x884D;
    public final int CURRENT_QUERY = 0x8865;
    public final int QUERY_RESULT = 0x8866;
    public final int QUERY_RESULT_AVAILABLE = 0x8867;
    public final int BUFFER_MAPPED = 0x88BC;
    public final int BUFFER_MAP_POINTER = 0x88BD;
    public final int STREAM_READ = 0x88E1;
    public final int STREAM_COPY = 0x88E2;
    public final int STATIC_READ = 0x88E5;
    public final int STATIC_COPY = 0x88E6;
    public final int DYNAMIC_READ = 0x88E9;
    public final int DYNAMIC_COPY = 0x88EA;
    public final int MAX_DRAW_BUFFERS = 0x8824;
    public final int DRAW_BUFFER0 = 0x8825;
    public final int DRAW_BUFFER1 = 0x8826;
    public final int DRAW_BUFFER2 = 0x8827;
    public final int DRAW_BUFFER3 = 0x8828;
    public final int DRAW_BUFFER4 = 0x8829;
    public final int DRAW_BUFFER5 = 0x882A;
    public final int DRAW_BUFFER6 = 0x882B;
    public final int DRAW_BUFFER7 = 0x882C;
    public final int DRAW_BUFFER8 = 0x882D;
    public final int DRAW_BUFFER9 = 0x882E;
    public final int DRAW_BUFFER10 = 0x882F;
    public final int DRAW_BUFFER11 = 0x8830;
    public final int DRAW_BUFFER12 = 0x8831;
    public final int DRAW_BUFFER13 = 0x8832;
    public final int DRAW_BUFFER14 = 0x8833;
    public final int DRAW_BUFFER15 = 0x8834;
    public final int MAX_FRAGMENT_UNIFORM_COMPONENTS = 0x8B49;
    public final int MAX_VERTEX_UNIFORM_COMPONENTS = 0x8B4A;
    public final int SAMPLER_3D = 0x8B5F;
    public final int SAMPLER_2D_SHADOW = 0x8B62;
    public final int FRAGMENT_SHADER_DERIVATIVE_HINT = 0x8B8B;
    public final int PIXEL_PACK_BUFFER = 0x88EB;
    public final int PIXEL_UNPACK_BUFFER = 0x88EC;
    public final int PIXEL_PACK_BUFFER_BINDING = 0x88ED;
    public final int PIXEL_UNPACK_BUFFER_BINDING = 0x88EF;
    public final int FLOAT_MAT2x3 = 0x8B65;
    public final int FLOAT_MAT2x4 = 0x8B66;
    public final int FLOAT_MAT3x2 = 0x8B67;
    public final int FLOAT_MAT3x4 = 0x8B68;
    public final int FLOAT_MAT4x2 = 0x8B69;
    public final int FLOAT_MAT4x3 = 0x8B6A;
    public final int SRGB = 0x8C40;
    public final int SRGB8 = 0x8C41;
    public final int SRGB8_ALPHA8 = 0x8C43;
    public final int COMPARE_REF_TO_TEXTURE = 0x884E;
    public final int MAJOR_VERSION = 0x821B;
    public final int MINOR_VERSION = 0x821C;
    public final int NUM_EXTENSIONS = 0x821D;
    public final int RGBA32F = 0x8814;
    public final int RGB32F = 0x8815;
    public final int RGBA16F = 0x881A;
    public final int RGB16F = 0x881B;
    public final int VERTEX_ATTRIB_ARRAY_INTEGER = 0x88FD;
    public final int MAX_ARRAY_TEXTURE_LAYERS = 0x88FF;
    public final int MIN_PROGRAM_TEXEL_OFFSET = 0x8904;
    public final int MAX_PROGRAM_TEXEL_OFFSET = 0x8905;
    public final int MAX_VARYING_COMPONENTS = 0x8B4B;
    public final int TEXTURE_2D_ARRAY = 0x8C1A;
    public final int TEXTURE_BINDING_2D_ARRAY = 0x8C1D;
    public final int R11F_G11F_B10F = 0x8C3A;
    public final int UNSIGNED_INT_10F_11F_11F_REV = 0x8C3B;
    public final int RGB9_E5 = 0x8C3D;
    public final int UNSIGNED_INT_5_9_9_9_REV = 0x8C3E;
    public final int TRANSFORM_FEEDBACK_VARYING_MAX_LENGTH = 0x8C76;
    public final int TRANSFORM_FEEDBACK_BUFFER_MODE = 0x8C7F;
    public final int MAX_TRANSFORM_FEEDBACK_SEPARATE_COMPONENTS = 0x8C80;
    public final int TRANSFORM_FEEDBACK_VARYINGS = 0x8C83;
    public final int TRANSFORM_FEEDBACK_BUFFER_START = 0x8C84;
    public final int TRANSFORM_FEEDBACK_BUFFER_SIZE = 0x8C85;
    public final int TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN = 0x8C88;
    public final int RASTERIZER_DISCARD = 0x8C89;
    public final int MAX_TRANSFORM_FEEDBACK_INTERLEAVED_COMPONENTS = 0x8C8A;
    public final int MAX_TRANSFORM_FEEDBACK_SEPARATE_ATTRIBS = 0x8C8B;
    public final int INTERLEAVED_ATTRIBS = 0x8C8C;
    public final int SEPARATE_ATTRIBS = 0x8C8D;
    public final int TRANSFORM_FEEDBACK_BUFFER = 0x8C8E;
    public final int TRANSFORM_FEEDBACK_BUFFER_BINDING = 0x8C8F;
    public final int RGBA32UI = 0x8D70;
    public final int RGB32UI = 0x8D71;
    public final int RGBA16UI = 0x8D76;
    public final int RGB16UI = 0x8D77;
    public final int RGBA8UI = 0x8D7C;
    public final int RGB8UI = 0x8D7D;
    public final int RGBA32I = 0x8D82;
    public final int RGB32I = 0x8D83;
    public final int RGBA16I = 0x8D88;
    public final int RGB16I = 0x8D89;
    public final int RGBA8I = 0x8D8E;
    public final int RGB8I = 0x8D8F;
    public final int RED_INTEGER = 0x8D94;
    public final int RGB_INTEGER = 0x8D98;
    public final int RGBA_INTEGER = 0x8D99;
    public final int SAMPLER_2D_ARRAY = 0x8DC1;
    public final int SAMPLER_2D_ARRAY_SHADOW = 0x8DC4;
    public final int SAMPLER_CUBE_SHADOW = 0x8DC5;
    public final int UNSIGNED_INT_VEC2 = 0x8DC6;
    public final int UNSIGNED_INT_VEC3 = 0x8DC7;
    public final int UNSIGNED_INT_VEC4 = 0x8DC8;
    public final int INT_SAMPLER_2D = 0x8DCA;
    public final int INT_SAMPLER_3D = 0x8DCB;
    public final int INT_SAMPLER_CUBE = 0x8DCC;
    public final int INT_SAMPLER_2D_ARRAY = 0x8DCF;
    public final int UNSIGNED_INT_SAMPLER_2D = 0x8DD2;
    public final int UNSIGNED_INT_SAMPLER_3D = 0x8DD3;
    public final int UNSIGNED_INT_SAMPLER_CUBE = 0x8DD4;
    public final int UNSIGNED_INT_SAMPLER_2D_ARRAY = 0x8DD7;
    public final int BUFFER_ACCESS_FLAGS = 0x911F;
    public final int BUFFER_MAP_LENGTH = 0x9120;
    public final int BUFFER_MAP_OFFSET = 0x9121;
    public final int DEPTH_COMPONENT32F = 0x8CAC;
    public final int DEPTH32F_STENCIL8 = 0x8CAD;
    public final int FLOAT_32_UNSIGNED_INT_24_8_REV = 0x8DAD;
    public final int FRAMEBUFFER_ATTACHMENT_COLOR_ENCODING = 0x8210;
    public final int FRAMEBUFFER_ATTACHMENT_COMPONENT_TYPE = 0x8211;
    public final int FRAMEBUFFER_ATTACHMENT_RED_SIZE = 0x8212;
    public final int FRAMEBUFFER_ATTACHMENT_GREEN_SIZE = 0x8213;
    public final int FRAMEBUFFER_ATTACHMENT_BLUE_SIZE = 0x8214;
    public final int FRAMEBUFFER_ATTACHMENT_ALPHA_SIZE = 0x8215;
    public final int FRAMEBUFFER_ATTACHMENT_DEPTH_SIZE = 0x8216;
    public final int FRAMEBUFFER_ATTACHMENT_STENCIL_SIZE = 0x8217;
    public final int FRAMEBUFFER_DEFAULT = 0x8218;
    public final int FRAMEBUFFER_UNDEFINED = 0x8219;
    public final int DEPTH_STENCIL_ATTACHMENT = 0x821A;
    public final int DEPTH_STENCIL = 0x84F9;
    public final int UNSIGNED_INT_24_8 = 0x84FA;
    public final int DEPTH24_STENCIL8 = 0x88F0;
    public final int UNSIGNED_NORMALIZED = 0x8C17;
    public final int DRAW_FRAMEBUFFER_BINDING = FRAMEBUFFER_BINDING;
    public final int READ_FRAMEBUFFER = 0x8CA8;
    public final int DRAW_FRAMEBUFFER = 0x8CA9;
    public final int READ_FRAMEBUFFER_BINDING = 0x8CAA;
    public final int RENDERBUFFER_SAMPLES = 0x8CAB;
    public final int FRAMEBUFFER_ATTACHMENT_TEXTURE_LAYER = 0x8CD4;
    public final int MAX_COLOR_ATTACHMENTS = 0x8CDF;
    public final int COLOR_ATTACHMENT1 = 0x8CE1;
    public final int COLOR_ATTACHMENT2 = 0x8CE2;
    public final int COLOR_ATTACHMENT3 = 0x8CE3;
    public final int COLOR_ATTACHMENT4 = 0x8CE4;
    public final int COLOR_ATTACHMENT5 = 0x8CE5;
    public final int COLOR_ATTACHMENT6 = 0x8CE6;
    public final int COLOR_ATTACHMENT7 = 0x8CE7;
    public final int COLOR_ATTACHMENT8 = 0x8CE8;
    public final int COLOR_ATTACHMENT9 = 0x8CE9;
    public final int COLOR_ATTACHMENT10 = 0x8CEA;
    public final int COLOR_ATTACHMENT11 = 0x8CEB;
    public final int COLOR_ATTACHMENT12 = 0x8CEC;
    public final int COLOR_ATTACHMENT13 = 0x8CED;
    public final int COLOR_ATTACHMENT14 = 0x8CEE;
    public final int COLOR_ATTACHMENT15 = 0x8CEF;
    public final int FRAMEBUFFER_INCOMPLETE_MULTISAMPLE = 0x8D56;
    public final int MAX_SAMPLES = 0x8D57;
    public final int HALF_FLOAT = 0x140B;
    public final int MAP_READ_BIT = 0x0001;
    public final int MAP_WRITE_BIT = 0x0002;
    public final int MAP_INVALIDATE_RANGE_BIT = 0x0004;
    public final int MAP_INVALIDATE_BUFFER_BIT = 0x0008;
    public final int MAP_FLUSH_EXPLICIT_BIT = 0x0010;
    public final int MAP_UNSYNCHRONIZED_BIT = 0x0020;
    public final int RG = 0x8227;
    public final int RG_INTEGER = 0x8228;
    public final int R8 = 0x8229;
    public final int RG8 = 0x822B;
    public final int R16F = 0x822D;
    public final int R32F = 0x822E;
    public final int RG16F = 0x822F;
    public final int RG32F = 0x8230;
    public final int R8I = 0x8231;
    public final int R8UI = 0x8232;
    public final int R16I = 0x8233;
    public final int R16UI = 0x8234;
    public final int R32I = 0x8235;
    public final int R32UI = 0x8236;
    public final int RG8I = 0x8237;
    public final int RG8UI = 0x8238;
    public final int RG16I = 0x8239;
    public final int RG16UI = 0x823A;
    public final int RG32I = 0x823B;
    public final int RG32UI = 0x823C;
    public final int VERTEX_ARRAY_BINDING = 0x85B5;
    public final int R8_SNORM = 0x8F94;
    public final int RG8_SNORM = 0x8F95;
    public final int RGB8_SNORM = 0x8F96;
    public final int RGBA8_SNORM = 0x8F97;
    public final int SIGNED_NORMALIZED = 0x8F9C;
    public final int PRIMITIVE_RESTART_FIXED_INDEX = 0x8D69;
    public final int COPY_READ_BUFFER = 0x8F36;
    public final int COPY_WRITE_BUFFER = 0x8F37;
    public final int COPY_READ_BUFFER_BINDING = COPY_READ_BUFFER;
    public final int COPY_WRITE_BUFFER_BINDING = COPY_WRITE_BUFFER;
    public final int UNIFORM_BUFFER = 0x8A11;
    public final int UNIFORM_BUFFER_BINDING = 0x8A28;
    public final int UNIFORM_BUFFER_START = 0x8A29;
    public final int UNIFORM_BUFFER_SIZE = 0x8A2A;
    public final int MAX_VERTEX_UNIFORM_BLOCKS = 0x8A2B;
    public final int MAX_FRAGMENT_UNIFORM_BLOCKS = 0x8A2D;
    public final int MAX_COMBINED_UNIFORM_BLOCKS = 0x8A2E;
    public final int MAX_UNIFORM_BUFFER_BINDINGS = 0x8A2F;
    public final int MAX_UNIFORM_BLOCK_SIZE = 0x8A30;
    public final int MAX_COMBINED_VERTEX_UNIFORM_COMPONENTS = 0x8A31;
    public final int MAX_COMBINED_FRAGMENT_UNIFORM_COMPONENTS = 0x8A33;
    public final int UNIFORM_BUFFER_OFFSET_ALIGNMENT = 0x8A34;
    public final int ACTIVE_UNIFORM_BLOCK_MAX_NAME_LENGTH = 0x8A35;
    public final int ACTIVE_UNIFORM_BLOCKS = 0x8A36;
    public final int UNIFORM_TYPE = 0x8A37;
    public final int UNIFORM_SIZE = 0x8A38;
    public final int UNIFORM_NAME_LENGTH = 0x8A39;
    public final int UNIFORM_BLOCK_INDEX = 0x8A3A;
    public final int UNIFORM_OFFSET = 0x8A3B;
    public final int UNIFORM_ARRAY_STRIDE = 0x8A3C;
    public final int UNIFORM_MATRIX_STRIDE = 0x8A3D;
    public final int UNIFORM_IS_ROW_MAJOR = 0x8A3E;
    public final int UNIFORM_BLOCK_BINDING = 0x8A3F;
    public final int UNIFORM_BLOCK_DATA_SIZE = 0x8A40;
    public final int UNIFORM_BLOCK_NAME_LENGTH = 0x8A41;
    public final int UNIFORM_BLOCK_ACTIVE_UNIFORMS = 0x8A42;
    public final int UNIFORM_BLOCK_ACTIVE_UNIFORM_INDICES = 0x8A43;
    public final int UNIFORM_BLOCK_REFERENCED_BY_VERTEX_SHADER = 0x8A44;
    public final int UNIFORM_BLOCK_REFERENCED_BY_FRAGMENT_SHADER = 0x8A46;
    // INVALID_INDEX is defined as 0xFFFFFFFFu in C.
    public final int INVALID_INDEX = -1;
    public final int MAX_VERTEX_OUTPUT_COMPONENTS = 0x9122;
    public final int MAX_FRAGMENT_INPUT_COMPONENTS = 0x9125;
    public final int MAX_SERVER_WAIT_TIMEOUT = 0x9111;
    public final int OBJECT_TYPE = 0x9112;
    public final int SYNC_CONDITION = 0x9113;
    public final int SYNC_STATUS = 0x9114;
    public final int SYNC_FLAGS = 0x9115;
    public final int SYNC_FENCE = 0x9116;
    public final int SYNC_GPU_COMMANDS_COMPLETE = 0x9117;
    public final int UNSIGNALED = 0x9118;
    public final int SIGNALED = 0x9119;
    public final int ALREADY_SIGNALED = 0x911A;
    public final int TIMEOUT_EXPIRED = 0x911B;
    public final int CONDITION_SATISFIED = 0x911C;
    public final int WAIT_FAILED = 0x911D;
    public final int SYNC_FLUSH_COMMANDS_BIT = 0x00000001;
    // TIMEOUT_IGNORED is defined as 0xFFFFFFFFFFFFFFFFull in C.
    public final long TIMEOUT_IGNORED = -1;
    public final int VERTEX_ATTRIB_ARRAY_DIVISOR = 0x88FE;
    public final int ANY_SAMPLES_PASSED = 0x8C2F;
    public final int ANY_SAMPLES_PASSED_CONSERVATIVE = 0x8D6A;
    public final int SAMPLER_BINDING = 0x8919;
    public final int RGB10_A2UI = 0x906F;
    public final int TEXTURE_SWIZZLE_R = 0x8E42;
    public final int TEXTURE_SWIZZLE_G = 0x8E43;
    public final int TEXTURE_SWIZZLE_B = 0x8E44;
    public final int TEXTURE_SWIZZLE_A = 0x8E45;
    public final int GREEN = 0x1904;
    public final int BLUE = 0x1905;
    public final int INT_2_10_10_10_REV = 0x8D9F;
    public final int TRANSFORM_FEEDBACK = 0x8E22;
    public final int TRANSFORM_FEEDBACK_PAUSED = 0x8E23;
    public final int TRANSFORM_FEEDBACK_ACTIVE = 0x8E24;
    public final int TRANSFORM_FEEDBACK_BINDING = 0x8E25;
    public final int PROGRAM_BINARY_RETRIEVABLE_HINT = 0x8257;
    public final int PROGRAM_BINARY_LENGTH = 0x8741;
    public final int NUM_PROGRAM_BINARY_FORMATS = 0x87FE;
    public final int PROGRAM_BINARY_FORMATS = 0x87FF;
    public final int COMPRESSED_R11_EAC = 0x9270;
    public final int COMPRESSED_SIGNED_R11_EAC = 0x9271;
    public final int COMPRESSED_RG11_EAC = 0x9272;
    public final int COMPRESSED_SIGNED_RG11_EAC = 0x9273;
    public final int COMPRESSED_RGB8_ETC2 = 0x9274;
    public final int COMPRESSED_SRGB8_ETC2 = 0x9275;
    public final int COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2 = 0x9276;
    public final int COMPRESSED_SRGB8_PUNCHTHROUGH_ALPHA1_ETC2 = 0x9277;
    public final int COMPRESSED_RGBA8_ETC2_EAC = 0x9278;
    public final int COMPRESSED_SRGB8_ALPHA8_ETC2_EAC = 0x9279;
    public final int TEXTURE_IMMUTABLE_FORMAT = 0x912F;
    public final int MAX_ELEMENT_INDEX = 0x8D6B;
    public final int NUM_SAMPLE_COUNTS = 0x9380;
    public final int TEXTURE_IMMUTABLE_LEVELS = 0x82DF;

    // C function void readBuffer  ( GLenum mode )

    public void readBuffer(int mode);

    // C function void drawRangeElements  ( GLenum mode, GLuint start, GLuint end, GLsizei count, GLenum type, const GLvoid
// *indices )

    public void drawRangeElements(int mode, int start, int end, int count, int type, java.nio.Buffer indices);

    // C function void drawRangeElements  ( GLenum mode, GLuint start, GLuint end, GLsizei count, GLenum type, GLsizei offset )

    public void drawRangeElements(int mode, int start, int end, int count, int type, int offset);

    // C function void texImage3D  ( GLenum target, GLint level, GLint internalformat, GLsizei width, GLsizei height, GLsizei
// depth, GLint border, GLenum format, GLenum type, const GLvoid *pixels )

    public void texImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format,
                           int type, java.nio.Buffer pixels);

    // C function void texImage3D  ( GLenum target, GLint level, GLint internalformat, GLsizei width, GLsizei height, GLsizei
// depth, GLint border, GLenum format, GLenum type, GLsizei offset )

    public void texImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format,
                           int type, int offset);

    // C function void texSubImage3D  ( GLenum target, GLint level, GLint xoffset, GLint yoffset, GLint zoffset, GLsizei width,
// GLsizei height, GLsizei depth, GLenum format, GLenum type, const GLvoid *pixels )

    public void texSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth,
                              int format, int type, java.nio.Buffer pixels);

    // C function void texSubImage3D  ( GLenum target, GLint level, GLint xoffset, GLint yoffset, GLint zoffset, GLsizei width,
// GLsizei height, GLsizei depth, GLenum format, GLenum type, GLsizei offset )

    public void texSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth,
                              int format, int type, int offset);

    // C function void copyTexSubImage3D  ( GLenum target, GLint level, GLint xoffset, GLint yoffset, GLint zoffset, GLint x,
// GLint y, GLsizei width, GLsizei height )

    public void copyTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int x, int y, int width,
                                  int height);

// // C function void compressedTexImage3D  ( GLenum target, GLint level, GLenum internalformat, GLsizei width, GLsizei height,
// GLsizei depth, GLint border, GLsizei imageSize, const GLvoid *data )
//
// public void compressedTexImage3D (
// int target,
// int level,
// int internalformat,
// int width,
// int height,
// int depth,
// int border,
// int imageSize,
// java.nio.Buffer data
// );
//
// // C function void compressedTexImage3D  ( GLenum target, GLint level, GLenum internalformat, GLsizei width, GLsizei height,
// GLsizei depth, GLint border, GLsizei imageSize, GLsizei offset )
//
// public void compressedTexImage3D (
// int target,
// int level,
// int internalformat,
// int width,
// int height,
// int depth,
// int border,
// int imageSize,
// int offset
// );
//
// // C function void compressedTexSubImage3D  ( GLenum target, GLint level, GLint xoffset, GLint yoffset, GLint zoffset, GLsizei
// width, GLsizei height, GLsizei depth, GLenum format, GLsizei imageSize, const GLvoid *data )
//
// public void compressedTexSubImage3D (
// int target,
// int level,
// int xoffset,
// int yoffset,
// int zoffset,
// int width,
// int height,
// int depth,
// int format,
// int imageSize,
// java.nio.Buffer data
// );
//
// // C function void compressedTexSubImage3D  ( GLenum target, GLint level, GLint xoffset, GLint yoffset, GLint zoffset, GLsizei
// width, GLsizei height, GLsizei depth, GLenum format, GLsizei imageSize, GLsizei offset )
//
// public void compressedTexSubImage3D (
// int target,
// int level,
// int xoffset,
// int yoffset,
// int zoffset,
// int width,
// int height,
// int depth,
// int format,
// int imageSize,
// int offset
// );

    // C function void genQueries  ( GLsizei n, GLuint *ids )

    public void genQueries(int n, int[] ids, int offset);

    // C function void genQueries  ( GLsizei n, GLuint *ids )

    public void genQueries(int n, java.nio.IntBuffer ids);

    // C function void deleteQueries  ( GLsizei n, const GLuint *ids )

    public void deleteQueries(int n, int[] ids, int offset);

    // C function void deleteQueries  ( GLsizei n, const GLuint *ids )

    public void deleteQueries(int n, java.nio.IntBuffer ids);

    // C function boolean glIsQuery  ( GLuint id )

    public boolean isQuery(int id);

    // C function void beginQuery  ( GLenum target, GLuint id )

    public void beginQuery(int target, int id);

    // C function void endQuery  ( GLenum target )

    public void endQuery(int target);

// // C function void getQueryiv  ( GLenum target, GLenum pname, GLint *params )
//
// public void getQueryiv (
// int target,
// int pname,
// int[] params,
// int offset
// );

    // C function void getQueryiv  ( GLenum target, GLenum pname, GLint *params )

    public void getQueryiv(int target, int pname, java.nio.IntBuffer params);

// // C function void getQueryObjectuiv  ( GLuint id, GLenum pname, GLuint *params )
//
// public void getQueryObjectuiv (
// int id,
// int pname,
// int[] params,
// int offset
// );

    // C function void getQueryObjectuiv  ( GLuint id, GLenum pname, GLuint *params )

    public void getQueryObjectuiv(int id, int pname, java.nio.IntBuffer params);

    // C function boolean glUnmapBuffer  ( GLenum target )

    public boolean unmapBuffer(int target);

    // C function void getBufferPointerv  ( GLenum target, GLenum pname, GLvoid** params )

    public java.nio.Buffer getBufferPointerv(int target, int pname);

// // C function void drawBuffers  ( GLsizei n, const GLenum *bufs )
//
// public void drawBuffers (
// int n,
// int[] bufs,
// int offset
// );

    // C function void drawBuffers  ( GLsizei n, const GLenum *bufs )

    public void drawBuffers(int n, java.nio.IntBuffer bufs);

// // C function void uniformMatrix2x3fv  ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )
//
// public void uniformMatrix2x3fv (
// int location,
// int count,
// boolean transpose,
// float[] value,
// int offset
// );

    // C function void uniformMatrix2x3fv  ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    public void uniformMatrix2x3fv(int location, int count, boolean transpose, java.nio.FloatBuffer value);

// // C function void uniformMatrix3x2fv  ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )
//
// public void uniformMatrix3x2fv (
// int location,
// int count,
// boolean transpose,
// float[] value,
// int offset
// );

    // C function void uniformMatrix3x2fv  ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    public void uniformMatrix3x2fv(int location, int count, boolean transpose, java.nio.FloatBuffer value);

// // C function void uniformMatrix2x4fv  ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )
//
// public void uniformMatrix2x4fv (
// int location,
// int count,
// boolean transpose,
// float[] value,
// int offset
// );

    // C function void uniformMatrix2x4fv  ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    public void uniformMatrix2x4fv(int location, int count, boolean transpose, java.nio.FloatBuffer value);

// // C function void uniformMatrix4x2fv  ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )
//
// public void uniformMatrix4x2fv (
// int location,
// int count,
// boolean transpose,
// float[] value,
// int offset
// );

    // C function void uniformMatrix4x2fv  ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    public void uniformMatrix4x2fv(int location, int count, boolean transpose, java.nio.FloatBuffer value);

// // C function void uniformMatrix3x4fv  ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )
//
// public void uniformMatrix3x4fv (
// int location,
// int count,
// boolean transpose,
// float[] value,
// int offset
// );

    // C function void uniformMatrix3x4fv  ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    public void uniformMatrix3x4fv(int location, int count, boolean transpose, java.nio.FloatBuffer value);

// // C function void uniformMatrix4x3fv  ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )
//
// public void uniformMatrix4x3fv (
// int location,
// int count,
// boolean transpose,
// float[] value,
// int offset
// );

    // C function void uniformMatrix4x3fv  ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    public void uniformMatrix4x3fv(int location, int count, boolean transpose, java.nio.FloatBuffer value);

    // C function void blitFramebuffer  ( GLint srcX0, GLint srcY0, GLint srcX1, GLint srcY1, GLint dstX0, GLint dstY0, GLint
// dstX1, GLint dstY1, GLbitfield mask, GLenum filter )

    public void blitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1,
                                int mask, int filter);

    // C function void renderbufferStorageMultisample  ( GLenum target, GLsizei samples, GLenum internalformat, GLsizei width,
// GLsizei height )

    public void renderbufferStorageMultisample(int target, int samples, int internalformat, int width, int height);

    // C function void framebufferTextureLayer  ( GLenum target, GLenum attachment, GLuint texture, GLint level, GLint layer )

    public void framebufferTextureLayer(int target, int attachment, int texture, int level, int layer);

// // C function void * glMapBufferRange  ( GLenum target, GLintptr offset, GLsizeiptr length, GLbitfield access )
//
// public java.nio.Buffer mapBufferRange (
// int target,
// int offset,
// int length,
// int access
// );

    // C function void flushMappedBufferRange  ( GLenum target, GLintptr offset, GLsizeiptr length )

    public void flushMappedBufferRange(int target, int offset, int length);

    // C function void bindVertexArray  ( GLuint array )

    public void bindVertexArray(int array);

    // C function void deleteVertexArrays  ( GLsizei n, const GLuint *arrays )

    public void deleteVertexArrays(int n, int[] arrays, int offset);

    // C function void deleteVertexArrays  ( GLsizei n, const GLuint *arrays )

    public void deleteVertexArrays(int n, java.nio.IntBuffer arrays);

    // C function void genVertexArrays  ( GLsizei n, GLuint *arrays )

    public void genVertexArrays(int n, int[] arrays, int offset);

    // C function void genVertexArrays  ( GLsizei n, GLuint *arrays )

    public void genVertexArrays(int n, java.nio.IntBuffer arrays);

    // C function boolean glIsVertexArray  ( GLuint array )

    public boolean isVertexArray(int array);

//
// // C function void getIntegeri_v  ( GLenum target, GLuint index, GLint *data )
//
// public void getIntegeri_v (
// int target,
// int index,
// int[] data,
// int offset
// );
//
// // C function void getIntegeri_v  ( GLenum target, GLuint index, GLint *data )
//
// public void getIntegeri_v (
// int target,
// int index,
// java.nio.IntBuffer data
// );

    // C function void beginTransformFeedback  ( GLenum primitiveMode )

    public void beginTransformFeedback(int primitiveMode);

    // C function void endTransformFeedback  ( void )

    public void endTransformFeedback();

    // C function void bindBufferRange  ( GLenum target, GLuint index, GLuint buffer, GLintptr offset, GLsizeiptr size )

    public void bindBufferRange(int target, int index, int buffer, int offset, int size);

    // C function void bindBufferBase  ( GLenum target, GLuint index, GLuint buffer )

    public void bindBufferBase(int target, int index, int buffer);

    // C function void transformFeedbackVaryings  ( GLuint program, GLsizei count, const GLchar *varyings, GLenum bufferMode )

    public void transformFeedbackVaryings(int program, String[] varyings, int bufferMode);

// // C function void getTransformFeedbackVarying  ( GLuint program, GLuint index, GLsizei bufSize, GLsizei *length, GLint *size,
// GLenum *type, GLchar *name )
//
// public void getTransformFeedbackVarying (
// int program,
// int index,
// int bufsize,
// int[] length,
// int lengthOffset,
// int[] size,
// int sizeOffset,
// int[] type,
// int typeOffset,
// byte[] name,
// int nameOffset
// );
//
// // C function void getTransformFeedbackVarying  ( GLuint program, GLuint index, GLsizei bufSize, GLsizei *length, GLint *size,
// GLenum *type, GLchar *name )
//
// public void getTransformFeedbackVarying (
// int program,
// int index,
// int bufsize,
// java.nio.IntBuffer length,
// java.nio.IntBuffer size,
// java.nio.IntBuffer type,
// byte name
// );
//
// // C function void getTransformFeedbackVarying  ( GLuint program, GLuint index, GLsizei bufSize, GLsizei *length, GLint *size,
// GLenum *type, GLchar *name )
//
// public String getTransformFeedbackVarying (
// int program,
// int index,
// int[] size,
// int sizeOffset,
// int[] type,
// int typeOffset
// );
//
// // C function void getTransformFeedbackVarying  ( GLuint program, GLuint index, GLsizei bufSize, GLsizei *length, GLint *size,
// GLenum *type, GLchar *name )
//
// public String getTransformFeedbackVarying (
// int program,
// int index,
// java.nio.IntBuffer size,
// java.nio.IntBuffer type
// );

    // C function void vertexAttribIPointer  ( GLuint index, GLint size, GLenum type, GLsizei stride, GLsizei offset )

    public void vertexAttribIPointer(int index, int size, int type, int stride, int offset);

// // C function void getVertexAttribIiv  ( GLuint index, GLenum pname, GLint *params )
//
// public void getVertexAttribIiv (
// int index,
// int pname,
// int[] params,
// int offset
// );

    // C function void getVertexAttribIiv  ( GLuint index, GLenum pname, GLint *params )

    public void getVertexAttribIiv(int index, int pname, java.nio.IntBuffer params);

// // C function void getVertexAttribIuiv  ( GLuint index, GLenum pname, GLuint *params )
//
// public void getVertexAttribIuiv (
// int index,
// int pname,
// int[] params,
// int offset
// );

    // C function void getVertexAttribIuiv  ( GLuint index, GLenum pname, GLuint *params )

    public void getVertexAttribIuiv(int index, int pname, java.nio.IntBuffer params);

    // C function void vertexAttribI4i  ( GLuint index, GLint x, GLint y, GLint z, GLint w )

    public void vertexAttribI4i(int index, int x, int y, int z, int w);

    // C function void vertexAttribI4ui  ( GLuint index, GLuint x, GLuint y, GLuint z, GLuint w )

    public void vertexAttribI4ui(int index, int x, int y, int z, int w);

// // C function void vertexAttribI4iv  ( GLuint index, const GLint *v )
//
// public void vertexAttribI4iv (
// int index,
// int[] v,
// int offset
// );
//
// // C function void vertexAttribI4iv  ( GLuint index, const GLint *v )
//
// public void vertexAttribI4iv (
// int index,
// java.nio.IntBuffer v
// );
//
// // C function void vertexAttribI4uiv  ( GLuint index, const GLuint *v )
//
// public void vertexAttribI4uiv (
// int index,
// int[] v,
// int offset
// );
//
// // C function void vertexAttribI4uiv  ( GLuint index, const GLuint *v )
//
// public void vertexAttribI4uiv (
// int index,
// java.nio.IntBuffer v
// );
//
// // C function void getUniformuiv  ( GLuint program, GLint location, GLuint *params )
//
// public void getUniformuiv (
// int program,
// int location,
// int[] params,
// int offset
// );

    // C function void getUniformuiv  ( GLuint program, GLint location, GLuint *params )

    public void getUniformuiv(int program, int location, java.nio.IntBuffer params);

    // C function int glGetFragDataLocation  ( GLuint program, const GLchar *name )

    public int getFragDataLocation(int program, String name);

// // C function void uniform1ui  ( GLint location, GLuint v0 )
//
// public void uniform1ui (
// int location,
// int v0
// );
//
// // C function void uniform2ui  ( GLint location, GLuint v0, GLuint v1 )
//
// public void uniform2ui (
// int location,
// int v0,
// int v1
// );
//
// // C function void uniform3ui  ( GLint location, GLuint v0, GLuint v1, GLuint v2 )
//
// public void uniform3ui (
// int location,
// int v0,
// int v1,
// int v2
// );
//
// // C function void uniform4ui  ( GLint location, GLuint v0, GLuint v1, GLuint v2, GLuint v3 )
//
// public void uniform4ui (
// int location,
// int v0,
// int v1,
// int v2,
// int v3
// );
//
// // C function void uniform1uiv  ( GLint location, GLsizei count, const GLuint *value )
//
// public void uniform1uiv (
// int location,
// int count,
// int[] value,
// int offset
// );

    // C function void uniform1uiv  ( GLint location, GLsizei count, const GLuint *value )

    public void uniform1uiv(int location, int count, java.nio.IntBuffer value);

// // C function void uniform2uiv  ( GLint location, GLsizei count, const GLuint *value )
//
// public void uniform2uiv (
// int location,
// int count,
// int[] value,
// int offset
// );
//
// // C function void uniform2uiv  ( GLint location, GLsizei count, const GLuint *value )
//
// public void uniform2uiv (
// int location,
// int count,
// java.nio.IntBuffer value
// );
//
// // C function void uniform3uiv  ( GLint location, GLsizei count, const GLuint *value )
//
// public void uniform3uiv (
// int location,
// int count,
// int[] value,
// int offset
// );

    // C function void uniform3uiv  ( GLint location, GLsizei count, const GLuint *value )

    public void uniform3uiv(int location, int count, java.nio.IntBuffer value);

// // C function void uniform4uiv  ( GLint location, GLsizei count, const GLuint *value )
//
// public void uniform4uiv (
// int location,
// int count,
// int[] value,
// int offset
// );

    // C function void uniform4uiv  ( GLint location, GLsizei count, const GLuint *value )

    public void uniform4uiv(int location, int count, java.nio.IntBuffer value);

// // C function void clearBufferiv  ( GLenum buffer, GLint drawbuffer, const GLint *value )
//
// public void clearBufferiv (
// int buffer,
// int drawbuffer,
// int[] value,
// int offset
// );

    // C function void clearBufferiv  ( GLenum buffer, GLint drawbuffer, const GLint *value )

    public void clearBufferiv(int buffer, int drawbuffer, java.nio.IntBuffer value);

// // C function void clearBufferuiv  ( GLenum buffer, GLint drawbuffer, const GLuint *value )
//
// public void clearBufferuiv (
// int buffer,
// int drawbuffer,
// int[] value,
// int offset
// );

    // C function void clearBufferuiv  ( GLenum buffer, GLint drawbuffer, const GLuint *value )

    public void clearBufferuiv(int buffer, int drawbuffer, java.nio.IntBuffer value);

// // C function void clearBufferfv  ( GLenum buffer, GLint drawbuffer, const GLfloat *value )
//
// public void clearBufferfv (
// int buffer,
// int drawbuffer,
// float[] value,
// int offset
// );

    // C function void clearBufferfv  ( GLenum buffer, GLint drawbuffer, const GLfloat *value )

    public void clearBufferfv(int buffer, int drawbuffer, java.nio.FloatBuffer value);

    // C function void clearBufferfi  ( GLenum buffer, GLint drawbuffer, GLfloat depth, GLint stencil )

    public void clearBufferfi(int buffer, int drawbuffer, float depth, int stencil);

    // C function const ubyte * glGetStringi  ( GLenum name, GLuint index )

    public String getStringi(int name, int index);

    // C function void copyBufferSubData  ( GLenum readTarget, GLenum writeTarget, GLintptr readOffset, GLintptr writeOffset,
// GLsizeiptr size )

    public void copyBufferSubData(int readTarget, int writeTarget, int readOffset, int writeOffset, int size);

// // C function void getUniformIndices  ( GLuint program, GLsizei uniformCount, const GLchar *const *uniformNames, GLuint
// *uniformIndices )
//
// public void getUniformIndices (
// int program,
// String[] uniformNames,
// int[] uniformIndices,
// int uniformIndicesOffset
// );

    // C function void getUniformIndices  ( GLuint program, GLsizei uniformCount, const GLchar *const *uniformNames, GLuint
// *uniformIndices )

    public void getUniformIndices(int program, String[] uniformNames, java.nio.IntBuffer uniformIndices);

// // C function void getActiveUniformsiv  ( GLuint program, GLsizei uniformCount, const GLuint *uniformIndices, GLenum pname,
// GLint *params )
//
// public void getActiveUniformsiv (
// int program,
// int uniformCount,
// int[] uniformIndices,
// int uniformIndicesOffset,
// int pname,
// int[] params,
// int paramsOffset
// );

    // C function void getActiveUniformsiv  ( GLuint program, GLsizei uniformCount, const GLuint *uniformIndices, GLenum pname,
// GLint *params )

    public void getActiveUniformsiv(int program, int uniformCount, java.nio.IntBuffer uniformIndices, int pname,
                                    java.nio.IntBuffer params);

    // C function uint glGetUniformBlockIndex  ( GLuint program, const GLchar *uniformBlockName )

    public int getUniformBlockIndex(int program, String uniformBlockName);

// // C function void getActiveUniformBlockiv  ( GLuint program, GLuint uniformBlockIndex, GLenum pname, GLint *params )
//
// public void getActiveUniformBlockiv (
// int program,
// int uniformBlockIndex,
// int pname,
// int[] params,
// int offset
// );

    // C function void getActiveUniformBlockiv  ( GLuint program, GLuint uniformBlockIndex, GLenum pname, GLint *params )

    public void getActiveUniformBlockiv(int program, int uniformBlockIndex, int pname, java.nio.IntBuffer params);

// // C function void getActiveUniformBlockName  ( GLuint program, GLuint uniformBlockIndex, GLsizei bufSize, GLsizei *length,
// GLchar *uniformBlockName )
//
// public void getActiveUniformBlockName (
// int program,
// int uniformBlockIndex,
// int bufSize,
// int[] length,
// int lengthOffset,
// byte[] uniformBlockName,
// int uniformBlockNameOffset
// );

    // C function void getActiveUniformBlockName  ( GLuint program, GLuint uniformBlockIndex, GLsizei bufSize, GLsizei *length,
// GLchar *uniformBlockName )

    public void getActiveUniformBlockName(int program, int uniformBlockIndex, java.nio.Buffer length,
                                          java.nio.Buffer uniformBlockName);

    // C function void getActiveUniformBlockName  ( GLuint program, GLuint uniformBlockIndex, GLsizei bufSize, GLsizei *length,
// GLchar *uniformBlockName )

    public String getActiveUniformBlockName(int program, int uniformBlockIndex);

    // C function void uniformBlockBinding  ( GLuint program, GLuint uniformBlockIndex, GLuint uniformBlockBinding )

    public void uniformBlockBinding(int program, int uniformBlockIndex, int uniformBlockBinding);

    // C function void drawArraysInstanced  ( GLenum mode, GLint first, GLsizei count, GLsizei instanceCount )

    public void drawArraysInstanced(int mode, int first, int count, int instanceCount);

// // C function void drawElementsInstanced  ( GLenum mode, GLsizei count, GLenum type, const GLvoid *indices, GLsizei
// instanceCount )
//
// public void drawElementsInstanced (
// int mode,
// int count,
// int type,
// java.nio.Buffer indices,
// int instanceCount
// );

    // C function void drawElementsInstanced  ( GLenum mode, GLsizei count, GLenum type, const GLvoid *indices, GLsizei
// instanceCount )

    public void drawElementsInstanced(int mode, int count, int type, int indicesOffset, int instanceCount);

// // C function sync glFenceSync  ( GLenum condition, GLbitfield flags )
//
// public long fenceSync (
// int condition,
// int flags
// );
//
// // C function boolean glIsSync  ( GLsync sync )
//
// public boolean isSync (
// long sync
// );
//
// // C function void deleteSync  ( GLsync sync )
//
// public void deleteSync (
// long sync
// );
//
// // C function enum glClientWaitSync  ( GLsync sync, GLbitfield flags, GLuint64 timeout )
//
// public int clientWaitSync (
// long sync,
// int flags,
// long timeout
// );
//
// // C function void waitSync  ( GLsync sync, GLbitfield flags, GLuint64 timeout )
//
// public void waitSync (
// long sync,
// int flags,
// long timeout
// );

// // C function void getInteger64v  ( GLenum pname, GLint64 *params )
//
// public void getInteger64v (
// int pname,
// long[] params,
// int offset
// );

    // C function void getInteger64v  ( GLenum pname, GLint64 *params )

    public void getInteger64v(int pname, java.nio.LongBuffer params);

// // C function void getSynciv  ( GLsync sync, GLenum pname, GLsizei bufSize, GLsizei *length, GLint *values )
//
// public void getSynciv (
// long sync,
// int pname,
// int bufSize,
// int[] length,
// int lengthOffset,
// int[] values,
// int valuesOffset
// );
//
// // C function void getSynciv  ( GLsync sync, GLenum pname, GLsizei bufSize, GLsizei *length, GLint *values )
//
// public void getSynciv (
// long sync,
// int pname,
// int bufSize,
// java.nio.IntBuffer length,
// java.nio.IntBuffer values
// );
//
// // C function void getInteger64i_v  ( GLenum target, GLuint index, GLint64 *data )
//
// public void getInteger64i_v (
// int target,
// int index,
// long[] data,
// int offset
// );
//
// // C function void getInteger64i_v  ( GLenum target, GLuint index, GLint64 *data )
//
// public void getInteger64i_v (
// int target,
// int index,
// java.nio.LongBuffer data
// );
//
// // C function void getBufferParameteri64v  ( GLenum target, GLenum pname, GLint64 *params )
//
// public void getBufferParameteri64v (
// int target,
// int pname,
// long[] params,
// int offset
// );

    // C function void getBufferParameteri64v  ( GLenum target, GLenum pname, GLint64 *params )

    public void getBufferParameteri64v(int target, int pname, java.nio.LongBuffer params);

    // C function void genSamplers  ( GLsizei count, GLuint *samplers )

    public void genSamplers(int count, int[] samplers, int offset);

    // C function void genSamplers  ( GLsizei count, GLuint *samplers )

    public void genSamplers(int count, java.nio.IntBuffer samplers);

    // C function void deleteSamplers  ( GLsizei count, const GLuint *samplers )

    public void deleteSamplers(int count, int[] samplers, int offset);

    // C function void deleteSamplers  ( GLsizei count, const GLuint *samplers )

    public void deleteSamplers(int count, java.nio.IntBuffer samplers);

    // C function boolean glIsSampler  ( GLuint sampler )

    public boolean isSampler(int sampler);

    // C function void bindSampler  ( GLuint unit, GLuint sampler )

    public void bindSampler(int unit, int sampler);

    // C function void samplerParameteri  ( GLuint sampler, GLenum pname, GLint param )

    public void samplerParameteri(int sampler, int pname, int param);

// // C function void samplerParameteriv  ( GLuint sampler, GLenum pname, const GLint *param )
//
// public void samplerParameteriv (
// int sampler,
// int pname,
// int[] param,
// int offset
// );

    // C function void samplerParameteriv  ( GLuint sampler, GLenum pname, const GLint *param )

    public void samplerParameteriv(int sampler, int pname, java.nio.IntBuffer param);

    // C function void samplerParameterf  ( GLuint sampler, GLenum pname, GLfloat param )

    public void samplerParameterf(int sampler, int pname, float param);

// // C function void samplerParameterfv  ( GLuint sampler, GLenum pname, const GLfloat *param )
//
// public void samplerParameterfv (
// int sampler,
// int pname,
// float[] param,
// int offset
// );

    // C function void samplerParameterfv  ( GLuint sampler, GLenum pname, const GLfloat *param )

    public void samplerParameterfv(int sampler, int pname, java.nio.FloatBuffer param);

// // C function void getSamplerParameteriv  ( GLuint sampler, GLenum pname, GLint *params )
//
// public void getSamplerParameteriv (
// int sampler,
// int pname,
// int[] params,
// int offset
// );

    // C function void getSamplerParameteriv  ( GLuint sampler, GLenum pname, GLint *params )

    public void getSamplerParameteriv(int sampler, int pname, java.nio.IntBuffer params);

// // C function void getSamplerParameterfv  ( GLuint sampler, GLenum pname, GLfloat *params )
//
// public void getSamplerParameterfv (
// int sampler,
// int pname,
// float[] params,
// int offset
// );

    // C function void getSamplerParameterfv  ( GLuint sampler, GLenum pname, GLfloat *params )

    public void getSamplerParameterfv(int sampler, int pname, java.nio.FloatBuffer params);

    // C function void vertexAttribDivisor  ( GLuint index, GLuint divisor )

    public void vertexAttribDivisor(int index, int divisor);

    // C function void bindTransformFeedback  ( GLenum target, GLuint id )

    public void bindTransformFeedback(int target, int id);

    // C function void deleteTransformFeedbacks  ( GLsizei n, const GLuint *ids )

    public void deleteTransformFeedbacks(int n, int[] ids, int offset);

    // C function void deleteTransformFeedbacks  ( GLsizei n, const GLuint *ids )

    public void deleteTransformFeedbacks(int n, java.nio.IntBuffer ids);

    // C function void genTransformFeedbacks  ( GLsizei n, GLuint *ids )

    public void genTransformFeedbacks(int n, int[] ids, int offset);

    // C function void genTransformFeedbacks  ( GLsizei n, GLuint *ids )

    public void genTransformFeedbacks(int n, java.nio.IntBuffer ids);

    // C function boolean glIsTransformFeedback  ( GLuint id )

    public boolean isTransformFeedback(int id);

    // C function void pauseTransformFeedback  ( void )

    public void pauseTransformFeedback();

    // C function void resumeTransformFeedback  ( void )

    public void resumeTransformFeedback();

// // C function void getProgramBinary  ( GLuint program, GLsizei bufSize, GLsizei *length, GLenum *binaryFormat, GLvoid *binary
// )
//
// public void getProgramBinary (
// int program,
// int bufSize,
// int[] length,
// int lengthOffset,
// int[] binaryFormat,
// int binaryFormatOffset,
// java.nio.Buffer binary
// );
//
// // C function void getProgramBinary  ( GLuint program, GLsizei bufSize, GLsizei *length, GLenum *binaryFormat, GLvoid *binary
// )
//
// public void getProgramBinary (
// int program,
// int bufSize,
// java.nio.IntBuffer length,
// java.nio.IntBuffer binaryFormat,
// java.nio.Buffer binary
// );
//
// // C function void programBinary  ( GLuint program, GLenum binaryFormat, const GLvoid *binary, GLsizei length )
//
// public void programBinary (
// int program,
// int binaryFormat,
// java.nio.Buffer binary,
// int length
// );

    // C function void programParameteri  ( GLuint program, GLenum pname, GLint value )

    public void programParameteri(int program, int pname, int value);

// // C function void invalidateFramebuffer  ( GLenum target, GLsizei numAttachments, const GLenum *attachments )
//
// public void invalidateFramebuffer (
// int target,
// int numAttachments,
// int[] attachments,
// int offset
// );

    // C function void invalidateFramebuffer  ( GLenum target, GLsizei numAttachments, const GLenum *attachments )

    public void invalidateFramebuffer(int target, int numAttachments, java.nio.IntBuffer attachments);

// // C function void invalidateSubFramebuffer  ( GLenum target, GLsizei numAttachments, const GLenum *attachments, GLint x,
// GLint y, GLsizei width, GLsizei height )
//
// public void invalidateSubFramebuffer (
// int target,
// int numAttachments,
// int[] attachments,
// int offset,
// int x,
// int y,
// int width,
// int height
// );

    // C function void invalidateSubFramebuffer  ( GLenum target, GLsizei numAttachments, const GLenum *attachments, GLint x,
// GLint y, GLsizei width, GLsizei height )

    public void invalidateSubFramebuffer(int target, int numAttachments, java.nio.IntBuffer attachments, int x, int y,
                                         int width, int height);

// // C function void texStorage2D  ( GLenum target, GLsizei levels, GLenum internalformat, GLsizei width, GLsizei height )
//
// public void texStorage2D (
// int target,
// int levels,
// int internalformat,
// int width,
// int height
// );
//
// // C function void texStorage3D  ( GLenum target, GLsizei levels, GLenum internalformat, GLsizei width, GLsizei height,
// GLsizei depth )
//
// public void texStorage3D (
// int target,
// int levels,
// int internalformat,
// int width,
// int height,
// int depth
// );
//
// // C function void getInternalformativ  ( GLenum target, GLenum internalformat, GLenum pname, GLsizei bufSize, GLint *params )
//
// public void getInternalformativ (
// int target,
// int internalformat,
// int pname,
// int bufSize,
// int[] params,
// int offset
// );
//
// // C function void getInternalformativ  ( GLenum target, GLenum internalformat, GLenum pname, GLsizei bufSize, GLint *params )
//
// public void getInternalformativ (
// int target,
// int internalformat,
// int pname,
// int bufSize,
// java.nio.IntBuffer params
// );

    @Override
    @Deprecated
    /**
     * In Open core profiles  (3.1+), passing a pointer to client memory is not valid.
     * Use the other version of this function instead, pass a zero-based offset which references
     * the buffer currently bound to ARRAY_BUFFER.
     */
    void vertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, Buffer ptr);
}

