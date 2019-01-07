/*******************************************************************************
 * Copyright 2011 See libgdx AUTHORS file.
 * Copyright 2013 Hannes Janetzek
 * Copyright 2019 schedul-xor
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.oscim.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static org.oscim.backend.GLAdapter.gl;

public class GLMatrix {

    public static final int M00 = 0;// 0;
    public static final int M01 = 4;// 1;
    public static final int M02 = 8;// 2;
    public static final int M03 = 12;// 3;
    public static final int M10 = 1;// 4;
    public static final int M11 = 5;// 5;
    public static final int M12 = 9;// 6;
    public static final int M13 = 13;// 7;
    public static final int M20 = 2;// 8;
    public static final int M21 = 6;// 9;
    public static final int M22 = 10;// 10;
    public static final int M23 = 14;// 11;
    public static final int M30 = 3;// 12;
    public static final int M31 = 7;// 13;
    public static final int M32 = 11;// 14;
    public static final int M33 = 15;// 15;

    private final FloatBuffer buffer = ByteBuffer.allocateDirect(16 * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();

    private static final String INVALID_INPUT = "Bad Array!";

    public final float tmp[] = new float[16];
    public final float val[] = new float[16];

    /**
     * Sets the matrix to the given matrix as a float array. The float array
     * must have at least 16 elements; the first 16 will be
     * copied.
     *
     * @param values The matrix, in float form, that is to be copied. Remember
     *               that this matrix is in <a
     *               href="http://en.wikipedia.org/wiki/Row-major_order">column
     *               major</a> order.
     * @return This matrix for the purpose of chaining methods together.
     */
    public void set(float[] values) {
        val[M00] = values[M00];
        val[M10] = values[M10];
        val[M20] = values[M20];
        val[M30] = values[M30];
        val[M01] = values[M01];
        val[M11] = values[M11];
        val[M21] = values[M21];
        val[M31] = values[M31];
        val[M02] = values[M02];
        val[M12] = values[M12];
        val[M22] = values[M22];
        val[M32] = values[M32];
        val[M03] = values[M03];
        val[M13] = values[M13];
        val[M23] = values[M23];
        val[M33] = values[M33];
    }

    /**
     * Get the Matrix as float array
     *
     * @param m float array to store Matrix
     */
    public void get(float[] m) {

        if (m == null || m.length != 16)
            throw new IllegalArgumentException(INVALID_INPUT);

        System.arraycopy(val, 0, m, 0, 16);
    }

    /**
     * Copy values from mat
     *
     * @param mat Matrix to copy
     */
    public void copy(GLMatrix m) {
        if (m == null || m.val.length != 16)
            throw new IllegalArgumentException(INVALID_INPUT);

        System.arraycopy(m.val, 0, val, 0, 16);
    }

    /**
     * Project Vector with Matrix
     *
     * @param vec3 Vector to project
     */
    public void prj(float[] vec3) {
        if (vec3 == null || vec3.length < 3)
            throw new IllegalArgumentException(INVALID_INPUT);

        matrix4_proj(val, vec3);
    }

    static void matrix4_proj(float[] mat, float[] vec) {
        float inv_w = 1.0f / (vec[0] * mat[M30] + vec[1] * mat[M31] + vec[2] * mat[M32] + mat[M33]);
        float x = (vec[0] * mat[M00] + vec[1] * mat[M01] + vec[2] * mat[M02] + mat[M03]) * inv_w;
        float y = (vec[0] * mat[M10] + vec[1] * mat[M11] + vec[2] * mat[M12] + mat[M13]) * inv_w;
        float z = (vec[0] * mat[M20] + vec[1] * mat[M21] + vec[2] * mat[M22] + mat[M23]) * inv_w;
        vec[0] = x;
        vec[1] = y;
        vec[2] = z;
    }

    public void prj3D(float[] vec, int offset, int cnt) {
        throw new RuntimeException("unimplemented");
    }

    public void prj2D(float[] vec, int offset, int cnt) {
        offset <<= 1;
        cnt <<= 1;

        for (int x = offset, y = x + 1; x < cnt + offset; x += 2, y += 2) {
            float inv_w = 1.0f / (vec[x] * val[M30] + vec[y] * val[M31] + val[M33]);
            float ox = (vec[x] * val[M00] + vec[y] * val[M01] + val[M03]) * inv_w;
            vec[y] = (vec[x] * val[M10] + vec[y] * val[M11] + val[M13]) * inv_w;
            vec[x] = ox;
        }
    }

    /**
     * Project Vectors with Matrix
     *
     * @param vec2 Vector to project
     */
    public void prj2D(float[] src, int src_offset, float[] dst, int dst_offset, int length) {
        if (src == null || src_offset < 0 || length + src_offset * 2 > src.length)
            throw new IllegalArgumentException(INVALID_INPUT);

        int x = (src_offset << 1);
        int y = x + 1;

        int end = x + (length << 1);

        dst_offset <<= 1;

        while (x < end) {
            float inv_w = 1.0f / (src[x] * val[M30] + src[y] * val[M31] + val[M33]);

            dst[dst_offset++] = (src[x] * val[M00] + src[y] * val[M01] + val[M03]) * inv_w;
            dst[dst_offset++] = (src[x] * val[M10] + src[y] * val[M11] + val[M13]) * inv_w;
            x += 2;
            y += 2;
        }
    }

    /**
     * Multiply rhs onto Matrix.
     *
     * @param rhs right hand side
     */
    public void multiplyRhs(GLMatrix rhs) {
        matrix4_mul(val, rhs.val);
    }

    /**
     * Use this matrix as rhs, multiply it on lhs and store result.
     *
     * @param lhs right hand side
     */
    public void multiplyLhs(GLMatrix lhs) {
        System.arraycopy(lhs.val, 0, tmp, 0, 16);
        matrix4_mul(tmp, val);
        System.arraycopy(tmp, 0, val, 0, 16);
    }

    /**
     * Multiply rhs onto lhs and store result in Matrix.
     * <p/>
     * This matrix MUST be different from lhs and rhs!
     * <p/>
     * As you know, when combining matrices for vector projection
     * this has the same effect first as applying rhs then lhs.
     *
     * @param lhs left hand side
     * @param rhs right hand side
     */
    public void multiplyMM(GLMatrix lhs, GLMatrix rhs) {
        System.arraycopy(lhs.val, 0, tmp, 0, 16);
        matrix4_mul(tmp, rhs.val);
        System.arraycopy(tmp, 0, val, 0, 16);
    }

    /**
     * Transpose mat and store result in Matrix
     *
     * @param mat to transpose
     */
    public void transposeM(GLMatrix mat) {
        val[M00] = mat.val[M00];
        val[M01] = mat.val[M10];
        val[M02] = mat.val[M20];
        val[M03] = mat.val[M30];
        val[M10] = mat.val[M01];
        val[M11] = mat.val[M11];
        val[M12] = mat.val[M21];
        val[M13] = mat.val[M31];
        val[M20] = mat.val[M02];
        val[M21] = mat.val[M12];
        val[M22] = mat.val[M22];
        val[M23] = mat.val[M32];
        val[M30] = mat.val[M03];
        val[M31] = mat.val[M13];
        val[M32] = mat.val[M23];
        val[M33] = mat.val[M33];
    }

    /**
     * Set rotation
     *
     * @param a angle in degree
     * @param x around x-axis
     * @param y around y-axis
     * @param z around z-axis
     */
    public void setRotation(float a, float x, float y, float z) {
        setRotateM(val, 0, a, x, y, z);
    }

    /**
     * Set translation
     *
     * @param x along x-axis
     * @param y along y-axis
     * @param z along z-axis
     */
    public void setTranslation(float x, float y, float z) {
        setIdentity();
        val[M03] = x;
        val[M13] = y;
        val[M23] = z;
    }

    /**
     * Set scale factor
     *
     * @param x axis
     * @param y axis
     * @param z axis
     */
    public void setScale(float x, float y, float z) {
        setIdentity();
        val[M00] = x;
        val[M11] = y;
        val[M22] = z;
    }

    /**
     * Set translation and x,y scale
     *
     * @param tx    translate x
     * @param ty    translate y
     * @param scale factor x,y
     */
    public void setTransScale(float tx, float ty, float scale) {
        setIdentity();
        val[M03] = tx;
        val[M13] = ty;

        val[M00] = scale;
        val[M11] = scale;
    }

    /**
     * Set Matrix with glUniformMatrix
     *
     * @param location GL location id
     */
    public void setAsUniform(int location) {
        buffer.clear();
        buffer.put(val, 0, 16);
        buffer.position(0);
        gl.uniformMatrix4fv(location, 1, false, buffer);
    }

    /**
     * Set single value
     *
     * @param pos   at position
     * @param value value to set
     */
    public void setValue(int pos, float value) {
        val[pos] = value;
    }

    static float PiTimesThumb = 1.0f / (1 << 11);

    /**
     * add some offset (similar to glDepthOffset)
     *
     * @param delta offset
     */
    public void addDepthOffset(int delta) {
        val[10] *= 1.0f + PiTimesThumb * delta;
    }

    /**
     * Set identity matrix
     */
    public void setIdentity() {
        val[M00] = 1;
        val[M01] = 0;
        val[M02] = 0;
        val[M03] = 0;
        val[M10] = 0;
        val[M11] = 1;
        val[M12] = 0;
        val[M13] = 0;
        val[M20] = 0;
        val[M21] = 0;
        val[M22] = 1;
        val[M23] = 0;
        val[M30] = 0;
        val[M31] = 0;
        val[M32] = 0;
        val[M33] = 1;
    }

    static void matrix4_mul(float[] mata, float[] matb) {
        float tmp[] = new float[16];
        tmp[M00] = mata[M00] * matb[M00] + mata[M01] * matb[M10] + mata[M02] * matb[M20]
                + mata[M03] * matb[M30];
        tmp[M01] = mata[M00] * matb[M01] + mata[M01] * matb[M11] + mata[M02] * matb[M21]
                + mata[M03] * matb[M31];
        tmp[M02] = mata[M00] * matb[M02] + mata[M01] * matb[M12] + mata[M02] * matb[M22]
                + mata[M03] * matb[M32];
        tmp[M03] = mata[M00] * matb[M03] + mata[M01] * matb[M13] + mata[M02] * matb[M23]
                + mata[M03] * matb[M33];
        tmp[M10] = mata[M10] * matb[M00] + mata[M11] * matb[M10] + mata[M12] * matb[M20]
                + mata[M13] * matb[M30];
        tmp[M11] = mata[M10] * matb[M01] + mata[M11] * matb[M11] + mata[M12] * matb[M21]
                + mata[M13] * matb[M31];
        tmp[M12] = mata[M10] * matb[M02] + mata[M11] * matb[M12] + mata[M12] * matb[M22]
                + mata[M13] * matb[M32];
        tmp[M13] = mata[M10] * matb[M03] + mata[M11] * matb[M13] + mata[M12] * matb[M23]
                + mata[M13] * matb[M33];
        tmp[M20] = mata[M20] * matb[M00] + mata[M21] * matb[M10] + mata[M22] * matb[M20]
                + mata[M23] * matb[M30];
        tmp[M21] = mata[M20] * matb[M01] + mata[M21] * matb[M11] + mata[M22] * matb[M21]
                + mata[M23] * matb[M31];
        tmp[M22] = mata[M20] * matb[M02] + mata[M21] * matb[M12] + mata[M22] * matb[M22]
                + mata[M23] * matb[M32];
        tmp[M23] = mata[M20] * matb[M03] + mata[M21] * matb[M13] + mata[M22] * matb[M23]
                + mata[M23] * matb[M33];
        tmp[M30] = mata[M30] * matb[M00] + mata[M31] * matb[M10] + mata[M32] * matb[M20]
                + mata[M33] * matb[M30];
        tmp[M31] = mata[M30] * matb[M01] + mata[M31] * matb[M11] + mata[M32] * matb[M21]
                + mata[M33] * matb[M31];
        tmp[M32] = mata[M30] * matb[M02] + mata[M31] * matb[M12] + mata[M32] * matb[M22]
                + mata[M33] * matb[M32];
        tmp[M33] = mata[M30] * matb[M03] + mata[M31] * matb[M13] + mata[M32] * matb[M23]
                + mata[M33] * matb[M33];
        System.arraycopy(tmp, 0, mata, 0, 16);
    }

    //    @Override
    //    public void finalize() {
    //        if (pointer != 0)
    //            delete(pointer);
    //    }

    /* Copyright (C) 2007 The Android Open Source Project
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
     * limitations under the License. */

    /**
     * Define a projection matrix in terms of six clip planes
     *
     * @param m      the float array that holds the perspective matrix
     * @param offset the offset into float array m where the perspective
     *               matrix data is written
     */
    public static void frustumM(float[] m, int offset,
                                float left, float right, float bottom, float top,
                                float near, float far) {
        if (left == right) {
            throw new IllegalArgumentException("left == right");
        }
        if (top == bottom) {
            throw new IllegalArgumentException("top == bottom");
        }
        if (near == far) {
            throw new IllegalArgumentException("near == far");
        }
        if (near <= 0.0f) {
            throw new IllegalArgumentException("near <= 0.0f");
        }
        if (far <= 0.0f) {
            throw new IllegalArgumentException("far <= 0.0f");
        }
        final float r_width = 1.0f / (right - left);
        final float r_height = 1.0f / (top - bottom);
        final float r_depth = 1.0f / (near - far);
        final float x = 2.0f * (near * r_width);
        final float y = 2.0f * (near * r_height);
        final float A = (right + left) * r_width;
        final float B = (top + bottom) * r_height;
        final float C = (far + near) * r_depth;
        final float D = 2.0f * (far * near * r_depth);
        m[offset + 0] = x;
        m[offset + 5] = y;
        m[offset + 8] = A;
        m[offset + 9] = B;
        m[offset + 10] = C;
        m[offset + 14] = D;
        m[offset + 11] = -1.0f;
        m[offset + 1] = 0.0f;
        m[offset + 2] = 0.0f;
        m[offset + 3] = 0.0f;
        m[offset + 4] = 0.0f;
        m[offset + 6] = 0.0f;
        m[offset + 7] = 0.0f;
        m[offset + 12] = 0.0f;
        m[offset + 13] = 0.0f;
        m[offset + 15] = 0.0f;
    }

    /**
     * Inverts a 4 x 4 matrix.
     *
     * @param mInv       the array that holds the output inverted matrix
     * @param mInvOffset an offset into mInv where the inverted matrix is
     *                   stored.
     * @param m          the input array
     * @param mOffset    an offset into m where the matrix is stored.
     * @return true if the matrix could be inverted, false if it could not.
     */
    public static boolean invertM(float[] mInv, int mInvOffset, float[] m,
                                  int mOffset) {
        // Invert a 4 x 4 matrix using Cramer's Rule

        // transpose matrix
        final float src0 = m[mOffset + 0];
        final float src4 = m[mOffset + 1];
        final float src8 = m[mOffset + 2];
        final float src12 = m[mOffset + 3];

        final float src1 = m[mOffset + 4];
        final float src5 = m[mOffset + 5];
        final float src9 = m[mOffset + 6];
        final float src13 = m[mOffset + 7];

        final float src2 = m[mOffset + 8];
        final float src6 = m[mOffset + 9];
        final float src10 = m[mOffset + 10];
        final float src14 = m[mOffset + 11];

        final float src3 = m[mOffset + 12];
        final float src7 = m[mOffset + 13];
        final float src11 = m[mOffset + 14];
        final float src15 = m[mOffset + 15];

        // calculate pairs for first 8 elements (cofactors)
        final float atmp0 = src10 * src15;
        final float atmp1 = src11 * src14;
        final float atmp2 = src9 * src15;
        final float atmp3 = src11 * src13;
        final float atmp4 = src9 * src14;
        final float atmp5 = src10 * src13;
        final float atmp6 = src8 * src15;
        final float atmp7 = src11 * src12;
        final float atmp8 = src8 * src14;
        final float atmp9 = src10 * src12;
        final float atmp10 = src8 * src13;
        final float atmp11 = src9 * src12;

        // calculate first 8 elements (cofactors)
        final float dst0 = (atmp0 * src5 + atmp3 * src6 + atmp4 * src7)
                - (atmp1 * src5 + atmp2 * src6 + atmp5 * src7);
        final float dst1 = (atmp1 * src4 + atmp6 * src6 + atmp9 * src7)
                - (atmp0 * src4 + atmp7 * src6 + atmp8 * src7);
        final float dst2 = (atmp2 * src4 + atmp7 * src5 + atmp10 * src7)
                - (atmp3 * src4 + atmp6 * src5 + atmp11 * src7);
        final float dst3 = (atmp5 * src4 + atmp8 * src5 + atmp11 * src6)
                - (atmp4 * src4 + atmp9 * src5 + atmp10 * src6);
        final float dst4 = (atmp1 * src1 + atmp2 * src2 + atmp5 * src3)
                - (atmp0 * src1 + atmp3 * src2 + atmp4 * src3);
        final float dst5 = (atmp0 * src0 + atmp7 * src2 + atmp8 * src3)
                - (atmp1 * src0 + atmp6 * src2 + atmp9 * src3);
        final float dst6 = (atmp3 * src0 + atmp6 * src1 + atmp11 * src3)
                - (atmp2 * src0 + atmp7 * src1 + atmp10 * src3);
        final float dst7 = (atmp4 * src0 + atmp9 * src1 + atmp10 * src2)
                - (atmp5 * src0 + atmp8 * src1 + atmp11 * src2);

        // calculate pairs for second 8 elements (cofactors)
        final float btmp0 = src2 * src7;
        final float btmp1 = src3 * src6;
        final float btmp2 = src1 * src7;
        final float btmp3 = src3 * src5;
        final float btmp4 = src1 * src6;
        final float btmp5 = src2 * src5;
        final float btmp6 = src0 * src7;
        final float btmp7 = src3 * src4;
        final float btmp8 = src0 * src6;
        final float btmp9 = src2 * src4;
        final float btmp10 = src0 * src5;
        final float btmp11 = src1 * src4;

        // calculate second 8 elements (cofactors)
        final float dst8 = (btmp0 * src13 + btmp3 * src14 + btmp4 * src15)
                - (btmp1 * src13 + btmp2 * src14 + btmp5 * src15);
        final float dst9 = (btmp1 * src12 + btmp6 * src14 + btmp9 * src15)
                - (btmp0 * src12 + btmp7 * src14 + btmp8 * src15);
        final float dst10 = (btmp2 * src12 + btmp7 * src13 + btmp10 * src15)
                - (btmp3 * src12 + btmp6 * src13 + btmp11 * src15);
        final float dst11 = (btmp5 * src12 + btmp8 * src13 + btmp11 * src14)
                - (btmp4 * src12 + btmp9 * src13 + btmp10 * src14);
        final float dst12 = (btmp2 * src10 + btmp5 * src11 + btmp1 * src9)
                - (btmp4 * src11 + btmp0 * src9 + btmp3 * src10);
        final float dst13 = (btmp8 * src11 + btmp0 * src8 + btmp7 * src10)
                - (btmp6 * src10 + btmp9 * src11 + btmp1 * src8);
        final float dst14 = (btmp6 * src9 + btmp11 * src11 + btmp3 * src8)
                - (btmp10 * src11 + btmp2 * src8 + btmp7 * src9);
        final float dst15 = (btmp10 * src10 + btmp4 * src8 + btmp9 * src9)
                - (btmp8 * src9 + btmp11 * src10 + btmp5 * src8);

        // calculate determinant
        final float det =
                src0 * dst0 + src1 * dst1 + src2 * dst2 + src3 * dst3;

        if (det == 0.0f) {
            return false;
        }

        // calculate matrix inverse
        final float invdet = 1.0f / det;
        mInv[mInvOffset] = dst0 * invdet;
        mInv[1 + mInvOffset] = dst1 * invdet;
        mInv[2 + mInvOffset] = dst2 * invdet;
        mInv[3 + mInvOffset] = dst3 * invdet;

        mInv[4 + mInvOffset] = dst4 * invdet;
        mInv[5 + mInvOffset] = dst5 * invdet;
        mInv[6 + mInvOffset] = dst6 * invdet;
        mInv[7 + mInvOffset] = dst7 * invdet;

        mInv[8 + mInvOffset] = dst8 * invdet;
        mInv[9 + mInvOffset] = dst9 * invdet;
        mInv[10 + mInvOffset] = dst10 * invdet;
        mInv[11 + mInvOffset] = dst11 * invdet;

        mInv[12 + mInvOffset] = dst12 * invdet;
        mInv[13 + mInvOffset] = dst13 * invdet;
        mInv[14 + mInvOffset] = dst14 * invdet;
        mInv[15 + mInvOffset] = dst15 * invdet;

        return true;
    }

    public static void lookAt(float[] m, int offset,
                              float eyex, float eyey, float eyez,
                              float centerx, float centery, float centerz,
                              float upx, float upy, float upz) {
        float z0 = eyex - centerx;
        float z1 = eyey - centery;
        float z2 = eyez - centerz;
        double len = 1.0 / Math.sqrt(z0 * z0 + z1 * z1 + z2 * z2);
        z0 *= len;
        z1 *= len;
        z2 *= len;

        float x0 = upy * z2 - upz * z1;
        float x1 = upz * z0 - upx * z2;
        float x2 = upx * z1 - upy * z0;
        len = Math.sqrt(x0 * x0 + x1 * x1 + x2 * x2);
        if (len == 0) {
            x0 = x1 = x2 = 0;
        } else {
            len = 1.0 / len;
            x0 *= len;
            x1 *= len;
            x2 *= len;
        }

        float y0 = z1 * x2 - z2 * x1;
        float y1 = z2 * x0 - z0 * x2;
        float y2 = z0 * x1 - z1 * x0;
        len = Math.sqrt(y0 * y0 + y1 * y1 + y2 * y2);
        if (len == 0) {
            y0 = y1 = y2 = 0;
        } else {
            len = 1.0 / len;
            y0 *= len;
            y1 *= len;
            y2 *= len;
        }

        m[offset + 0] = x0;
        m[offset + 1] = y0;
        m[offset + 2] = z0;
        m[offset + 3] = 0.0f;
        m[offset + 4] = x1;
        m[offset + 5] = y1;
        m[offset + 6] = z1;
        m[offset + 7] = 0.0f;
        m[offset + 8] = x2;
        m[offset + 9] = y2;
        m[offset + 10] = z2;
        m[offset + 11] = 0.0f;
        m[offset + 12] = -(x0 * eyex + x1 * eyey + x2 * eyez);
        m[offset + 13] = -(y0 * eyex + y1 * eyey + y2 * eyez);
        m[offset + 14] = -(z0 * eyex + z1 * eyey + z2 * eyez);
        m[offset + 15] = 1.0f;
    }

    public static void orthoM(float[] m, int offset,
                              float left, float right, float bottom, float top,
                              float near, float far) {
        final float r_width = 1.0f / (right - left);
        final float r_height = 1.0f / (top - bottom);
        final float r_depth = 1.0f / (near - far);
        m[offset + 0] = -2 * r_width;
        m[offset + 5] = -2 * r_height;
        m[offset + 10] = 2 * r_depth;
        m[offset + 12] = (left + right) * r_width;
        m[offset + 13] = (top + bottom) * r_height;
        m[offset + 14] = (far + near) * r_depth;
        m[offset + 1] = 0.0f;
        m[offset + 2] = 0.0f;
        m[offset + 3] = 0.0f;
        m[offset + 4] = 0.0f;
        m[offset + 6] = 0.0f;
        m[offset + 7] = 0.0f;
        m[offset + 8] = 0.0f;
        m[offset + 9] = 0.0f;
        m[offset + 11] = 0.0f;
        m[offset + 15] = 1.0f;
    }

    void setRotateM(float[] rm, int rmOffset, float a, float x, float y, float z) {
        rm[rmOffset + 3] = 0;
        rm[rmOffset + 7] = 0;
        rm[rmOffset + 11] = 0;
        rm[rmOffset + 12] = 0;
        rm[rmOffset + 13] = 0;
        rm[rmOffset + 14] = 0;
        rm[rmOffset + 15] = 1;
        a *= (float) (Math.PI / 180.0f);
        float s = (float) Math.sin(a);
        float c = (float) Math.cos(a);
        if (1.0f == x && 0.0f == y && 0.0f == z) {
            rm[rmOffset + 5] = c;
            rm[rmOffset + 10] = c;
            rm[rmOffset + 6] = s;
            rm[rmOffset + 9] = -s;
            rm[rmOffset + 1] = 0;
            rm[rmOffset + 2] = 0;
            rm[rmOffset + 4] = 0;
            rm[rmOffset + 8] = 0;
            rm[rmOffset + 0] = 1;
        } else if (0.0f == x && 1.0f == y && 0.0f == z) {
            rm[rmOffset + 0] = c;
            rm[rmOffset + 10] = c;
            rm[rmOffset + 8] = s;
            rm[rmOffset + 2] = -s;
            rm[rmOffset + 1] = 0;
            rm[rmOffset + 4] = 0;
            rm[rmOffset + 6] = 0;
            rm[rmOffset + 9] = 0;
            rm[rmOffset + 5] = 1;
        } else if (0.0f == x && 0.0f == y && 1.0f == z) {
            rm[rmOffset + 0] = c;
            rm[rmOffset + 5] = c;
            rm[rmOffset + 1] = s;
            rm[rmOffset + 4] = -s;
            rm[rmOffset + 2] = 0;
            rm[rmOffset + 6] = 0;
            rm[rmOffset + 8] = 0;
            rm[rmOffset + 9] = 0;
            rm[rmOffset + 10] = 1;
        } else {
            float len = (float) Math.sqrt(x * x + y * y + z * z);
            if (1.0f != len) {
                float recipLen = 1.0f / len;
                x *= recipLen;
                y *= recipLen;
                z *= recipLen;
            }
            float nc = 1.0f - c;
            float xy = x * y;
            float yz = y * z;
            float zx = z * x;
            float xs = x * s;
            float ys = y * s;
            float zs = z * s;
            rm[rmOffset + 0] = x * x * nc + c;
            rm[rmOffset + 4] = xy * nc - zs;
            rm[rmOffset + 8] = zx * nc + ys;
            rm[rmOffset + 1] = xy * nc + zs;
            rm[rmOffset + 5] = y * y * nc + c;
            rm[rmOffset + 9] = yz * nc - xs;
            rm[rmOffset + 2] = zx * nc - ys;
            rm[rmOffset + 6] = yz * nc + xs;
            rm[rmOffset + 10] = z * z * nc + c;
        }
    }
}
