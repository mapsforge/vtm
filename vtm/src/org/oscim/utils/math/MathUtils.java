/*******************************************************************************
 * Copyright 2011 Mario Zechner <badlogicgames@gmail.com>
 * Copyright 2011 Nathan Sweet <nathan.sweet@gmail.com>
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

package org.oscim.utils.math;

import java.util.Random;

/**
 * Utility and fast math functions.
 * <p/>
 * Thanks to Riven on JavaGaming.org for the basis of sin/cos/atan2/floor/ceil.
 *
 * @author Nathan Sweet
 */
public class MathUtils {
    public static final float nanoToSec = 1 / 1000000000f;

    public static final float PI = 3.1415927f;
    public static final float PI2 = PI * 2;

    private static final int SIN_BITS = 13; // Adjust for accuracy.
    private static final int SIN_MASK = ~(-1 << SIN_BITS);
    private static final int SIN_COUNT = SIN_MASK + 1;

    private static final float radFull = PI * 2;
    private static final float degFull = 360;
    private static final float radToIndex = SIN_COUNT / radFull;
    private static final float degToIndex = SIN_COUNT / degFull;

    public static final float radiansToDegrees = 180f / PI;
    public static final float radDeg = radiansToDegrees;
    public static final float degreesToRadians = PI / 180;
    public static final float degRad = degreesToRadians;

    private static class Sin {
        static final float[] table = new float[SIN_COUNT];

        static {
            for (int i = 0; i < SIN_COUNT; i++)
                table[i] = (float) Math.sin((i + 0.5f) / SIN_COUNT * radFull);
            for (int i = 0; i < 360; i += 90)
                table[(int) (i * degToIndex) & SIN_MASK] = (float) Math.sin(i * degreesToRadians);
        }
    }

    private static class Cos {
        static final float[] table = new float[SIN_COUNT];

        static {
            for (int i = 0; i < SIN_COUNT; i++)
                table[i] = (float) Math.cos((i + 0.5f) / SIN_COUNT * radFull);
            for (int i = 0; i < 360; i += 90)
                table[(int) (i * degToIndex) & SIN_MASK] = (float) Math.cos(i * degreesToRadians);
        }
    }

    /**
     * Returns the sine in radians.
     */
    public static final float sin(float radians) {
        return Sin.table[(int) (radians * radToIndex) & SIN_MASK];
    }

    /**
     * Returns the cosine in radians.
     */
    public static final float cos(float radians) {
        return Cos.table[(int) (radians * radToIndex) & SIN_MASK];
    }

    /**
     * Returns the sine in radians.
     */
    public static final float sinDeg(float degrees) {
        return Sin.table[(int) (degrees * degToIndex) & SIN_MASK];
    }

    /**
     * Returns the cosine in radians.
     */
    public static final float cosDeg(float degrees) {
        return Cos.table[(int) (degrees * degToIndex) & SIN_MASK];
    }

    // ---

    private static final int ATAN2_BITS = 7; // Adjust for accuracy.
    private static final int ATAN2_BITS2 = ATAN2_BITS << 1;
    private static final int ATAN2_MASK = ~(-1 << ATAN2_BITS2);
    private static final int ATAN2_COUNT = ATAN2_MASK + 1;
    static final int ATAN2_DIM = (int) Math.sqrt(ATAN2_COUNT);
    private static final float INV_ATAN2_DIM_MINUS_1 = 1.0f / (ATAN2_DIM - 1);

    private static class Atan2 {
        static final float[] table = new float[ATAN2_COUNT];

        static {
            for (int i = 0; i < ATAN2_DIM; i++) {
                for (int j = 0; j < ATAN2_DIM; j++) {
                    float x0 = (float) i / ATAN2_DIM;
                    float y0 = (float) j / ATAN2_DIM;
                    table[j * ATAN2_DIM + i] = (float) Math.atan2(y0, x0);
                }
            }
        }
    }

    /**
     * Returns atan2 in radians from a lookup table.
     */
    public static final float atan2(float y, float x) {
        float add, mul;
        if (x < 0) {
            if (y < 0) {
                y = -y;
                mul = 1;
            } else
                mul = -1;
            x = -x;
            add = -PI;
        } else {
            if (y < 0) {
                y = -y;
                mul = -1;
            } else
                mul = 1;
            add = 0;
        }
        float invDiv = 1 / ((x < y ? y : x) * INV_ATAN2_DIM_MINUS_1);
        int xi = (int) (x * invDiv);
        int yi = (int) (y * invDiv);
        return (Atan2.table[yi * ATAN2_DIM + xi] + add) * mul;
    }

    // ---

    public static Random random = new Random();

    /**
     * Returns a random number between 0 (inclusive) and the specified value
     * (inclusive).
     */
    public static final int random(int range) {
        return random.nextInt(range + 1);
    }

    /**
     * Returns a random number between start (inclusive) and end (inclusive).
     */
    public static final int random(int start, int end) {
        return start + random.nextInt(end - start + 1);
    }

    /**
     * Returns a random boolean value.
     */
    public static final boolean randomBoolean() {
        return random.nextBoolean();
    }

    /**
     * Returns random number between 0.0 (inclusive) and 1.0 (exclusive).
     */
    public static final float random() {
        return random.nextFloat();
    }

    /**
     * Returns a random number between 0 (inclusive) and the specified value
     * (exclusive).
     */
    public static final float random(float range) {
        return random.nextFloat() * range;
    }

    /**
     * Returns a random number between start (inclusive) and end (exclusive).
     */
    public static final float random(float start, float end) {
        return start + random.nextFloat() * (end - start);
    }

    // ---

    /**
     * Returns the next power of two. Returns the specified value if the value
     * is already a power of two.
     */
    public static int nextPowerOfTwo(int value) {
        if (value == 0)
            return 1;
        value--;
        value |= value >> 1;
        value |= value >> 2;
        value |= value >> 4;
        value |= value >> 8;
        value |= value >> 16;
        return value + 1;
    }

    public static boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }

    // ---

    public static int clamp(int value, int min, int max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    public static short clamp(short value, short min, short max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    public static float clamp(float value, float min, float max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    // ---

    private static final int BIG_ENOUGH_INT = 16 * 1024;
    private static final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;
    private static final double CEIL = 0.9999999;
    private static final double BIG_ENOUGH_CEIL = Double.longBitsToDouble(Double.doubleToLongBits(BIG_ENOUGH_INT + 1) - 1);
    private static final double BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5f;

    /**
     * Returns the largest integer less than or equal to the specified float.
     * This method will only properly floor floats from
     * -(2^14) to (Float.MAX_VALUE - 2^14).
     */
    public static int floor(float x) {
        return (int) (x + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
    }

    /**
     * Returns the largest integer less than or equal to the specified float.
     * This method will only properly floor floats that are
     * positive. Note this method simply casts the float to int.
     */
    public static int floorPositive(float x) {
        return (int) x;
    }

    /**
     * Returns the smallest integer greater than or equal to the specified
     * float. This method will only properly ceil floats from
     * -(2^14) to (Float.MAX_VALUE - 2^14).
     */
    public static int ceil(float x) {
        return (int) (x + BIG_ENOUGH_CEIL) - BIG_ENOUGH_INT;
    }

    /**
     * Returns the smallest integer greater than or equal to the specified
     * float. This method will only properly ceil floats that
     * are positive.
     */
    public static int ceilPositive(float x) {
        return (int) (x + CEIL);
    }

    /**
     * Returns the closest integer to the specified float. This method will only
     * properly round floats from -(2^14) to
     * (Float.MAX_VALUE - 2^14).
     */
    public static int round(float x) {
        return (int) (x + BIG_ENOUGH_ROUND) - BIG_ENOUGH_INT;
    }

    /**
     * Returns the closest integer to the specified float. This method will only
     * properly round floats that are positive.
     */
    public static int roundPositive(float x) {
        return (int) (x + 0.5f);
    }
}
