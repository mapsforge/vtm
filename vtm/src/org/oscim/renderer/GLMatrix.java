/*
 * Copyright 2013 Hannes Janetzek
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
package org.oscim.renderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static org.oscim.backend.GLAdapter.gl;

public class GLMatrix {

    static final Logger log = LoggerFactory.getLogger(GLMatrix.class);
    private final static boolean dbg = false;

    private final long pointer;
    private final FloatBuffer buffer;

    private final static String INVALID_INPUT = "Bad Array!";

    public GLMatrix() {
        pointer = alloc();
        buffer = (getBuffer(pointer)).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    /**
     * Set the Matrix from float array
     *
     * @param m float array to copy
     */
    public void set(float[] m) {
        if (m == null || m.length != 16)
            throw new IllegalArgumentException(INVALID_INPUT);

        set(pointer, m);
    }

    /**
     * Get the Matrix as float array
     *
     * @param m float array to store Matrix
     */
    public void get(float[] m) {
        if (m == null || m.length != 16)
            throw new IllegalArgumentException(INVALID_INPUT);

        get(pointer, m);
    }

    /**
     * Copy values from mat
     *
     * @param mat Matrix to copy
     */
    public void copy(GLMatrix mat) {
        copy(pointer, mat.pointer);
    }

    /**
     * Project Vector with Matrix
     *
     * @param vec3 Vector to project
     */
    public void prj(float[] vec3) {
        if (vec3 == null || vec3.length < 3)
            throw new IllegalArgumentException(INVALID_INPUT);

        prj(pointer, vec3);
    }

    /**
     * Project Vectors with Matrix
     *
     * @param vec3 Vector to project
     */
    public void prj3D(float[] vec3, int offset, int length) {
        if (vec3 == null || vec3.length / (offset + length) < 1)
            throw new IllegalArgumentException(INVALID_INPUT);

        prj3D(pointer, vec3, offset, length);
    }

    /**
     * Project Vectors with Matrix
     *
     * @param vec2 Vector to project
     */
    public void prj2D(float[] vec2, int offset, int length) {
        if (vec2 == null || offset < 0 || (length + offset) * 2 > vec2.length)
            throw new IllegalArgumentException(INVALID_INPUT);

        prj2D(pointer, vec2, offset, length);
    }

    /**
     * Project Vectors with Matrix
     *
     * @param vec2 Vector to project
     */
    public void prj2D(double[] src, int src_offset, double[] dst, int dst_offset, int length) {
        if (src == null || src_offset < 0 || length + src_offset * 2 > src.length)
            throw new IllegalArgumentException(INVALID_INPUT);

        prj2D2(pointer, src, src_offset, dst, dst_offset, length);
    }

    /**
     * Multiply rhs onto Matrix.
     *
     * @param rhs right hand side
     */
    public void multiplyRhs(GLMatrix rhs) {
        smulrhs(pointer, rhs.pointer);
    }

    /**
     * Use this matrix as rhs, multiply it on lhs and store result.
     *
     * @param lhs right hand side
     */
    public void multiplyLhs(GLMatrix lhs) {
        smullhs(pointer, lhs.pointer);
    }

    /**
     * Multiply rhs onto lhs and store result in Matrix.
     * <p/>
     * This matrix MUST be different from lhs and rhs!
     * <p/>
     * when combining matrices for vector projection this
     * has the same effect first as applying rhs then lhs.
     *
     * @param lhs left hand side
     * @param rhs right hand side
     */
    public void multiplyMM(GLMatrix lhs, GLMatrix rhs) {
        smul(pointer, lhs.pointer, rhs.pointer);
    }

    /**
     * Transpose mat and store result in Matrix
     *
     * @param mat to transpose
     */
    public void transposeM(GLMatrix mat) {
        strans(pointer, mat.pointer);
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
        setRotation(pointer, a, x, y, z);
    }

    /**
     * Set translation
     *
     * @param x along x-axis
     * @param y along y-axis
     * @param z along z-axis
     */
    public void setTranslation(float x, float y, float z) {
        setTranslation(pointer, x, y, z);
    }

    /**
     * Set scale factor
     *
     * @param x axis
     * @param y axis
     * @param z axis
     */
    public void setScale(float x, float y, float z) {
        setScale(pointer, x, y, z);
    }

    /**
     * Set translation and x,y scale
     *
     * @param tx    translate x
     * @param ty    translate y
     * @param scale factor x,y
     */
    public void setTransScale(float tx, float ty, float scale) {
        setTransScale(pointer, tx, ty, scale);
    }

    /**
     * Set Matrix with glUniformMatrix
     *
     * @param location GL location id
     */
    public void setAsUniform(int location) {
        gl.uniformMatrix4fv(location, 1, false, buffer);
        //setAsUniform(pointer, location);
    }

    /**
     * Set single value
     *
     * @param pos   at position
     * @param value value to set
     */
    public void setValue(int pos, float value) {
        setValueAt(pointer, pos, value);
    }

    /**
     * add some offset (similar to glDepthOffset)
     *
     * @param delta offset
     */
    public void addDepthOffset(int delta) {
        addDepthOffset(pointer, delta);
    }

    /**
     * Set identity matrix
     */
    public void setIdentity() {
        identity(pointer);
    }

    /**
     * Free native object
     */
    @Override
    public void finalize() {
        if (pointer != 0)
            delete(pointer);
    }

    private native static long alloc();

    private native static void delete(long self);

    private native static void set(long self, float[] m);

    private native static void copy(long self, long other);

    private native static void identity(long self);

    private native static void get(long self, float[] m);

    private native static void mul(long self, long lhs_ptr);

    private native static void smul(long self, long rhs_ptr, long lhs_ptr);

    private native static void smulrhs(long self, long rhs_ptr);

    private native static void smullhs(long self, long lhs_ptr);

    private native static void strans(long self, long rhs_ptr);

    private native static void prj(long self, float[] vec3);

    private native static void prj3D(long self, float[] vec3, int start, int cnt);

    private native static void prj2D(long self, float[] vec2, int start, int cnt);

    private native static void prj2D2(long self, double[] vec2, int src_offset,
                                      double[] dst_vec, int dst_offset, int length);

    private native static void setRotation(long self, float a, float x, float y, float z);

    private native static void setScale(long self, float x, float y, float z);

    private native static void setTranslation(long self, float x, float y, float z);

    private native static void setTransScale(long self, float tx, float ty, float scale);

    //private native static void setAsUniform(long self, int handle);

    private native static void setValueAt(long self, int pos, float value);

    private native static void addDepthOffset(long self, int delta);

    private native static ByteBuffer getBuffer(long self);

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
}
