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


/**
 * Takes a linear value in the range of 0-1 and outputs a (usually) non-linear,
 * interpolated value.
 */
public abstract class Interpolation {
    /**
     * @param a Alpha value between 0 and 1.
     */
    public abstract float apply(float a);

    /**
     * @param a Alpha value between 0 and 1.
     */
    public float apply(float start, float end, float a) {
        return start + (end - start) * apply(a);
    }

    public static final Interpolation linear = new Interpolation() {
        @Override
        public float apply(float a) {
            return a;
        }
    };

    public static final Interpolation fade = new Interpolation() {
        @Override
        public float apply(float a) {
            return MathUtils.clamp(a * a * a * (a * (a * 6 - 15) + 10), 0, 1);
        }
    };

    public static final Pow pow2 = new Pow(2);
    public static final PowIn pow2In = new PowIn(2);
    public static final PowOut pow2Out = new PowOut(2);

    public static final Pow pow3 = new Pow(3);
    public static final PowIn pow3In = new PowIn(3);
    public static final PowOut pow3Out = new PowOut(3);

    public static final Pow pow4 = new Pow(4);
    public static final PowIn pow4In = new PowIn(4);
    public static final PowOut pow4Out = new PowOut(4);

    public static final Pow pow5 = new Pow(5);
    public static final PowIn pow5In = new PowIn(5);
    public static final PowOut pow5Out = new PowOut(5);

    public static final Interpolation sine = new Interpolation() {
        @Override
        public float apply(float a) {
            return (1 - MathUtils.cos(a * MathUtils.PI)) / 2;
        }
    };

    public static final Interpolation sineIn = new Interpolation() {
        @Override
        public float apply(float a) {
            return 1 - MathUtils.cos(a * MathUtils.PI / 2);
        }
    };

    public static final Interpolation sineOut = new Interpolation() {
        @Override
        public float apply(float a) {
            return MathUtils.sin(a * MathUtils.PI / 2);
        }
    };

    public static final Interpolation exp10 = new Exp(2, 10);
    public static final Interpolation exp10In = new ExpIn(2, 10);
    public static final Interpolation exp10Out = new ExpOut(2, 10);

    public static final Interpolation exp5 = new Exp(2, 5);
    public static final Interpolation exp5In = new ExpIn(2, 5);
    public static final Interpolation exp5Out = new ExpOut(2, 5);

    public static final Interpolation circle = new Interpolation() {
        @Override
        public float apply(float a) {
            if (a <= 0.5f) {
                a *= 2;
                return (1 - (float) Math.sqrt(1 - a * a)) / 2;
            }
            a--;
            a *= 2;
            return ((float) Math.sqrt(1 - a * a) + 1) / 2;
        }
    };

    public static final Interpolation circleIn = new Interpolation() {
        @Override
        public float apply(float a) {
            return 1 - (float) Math.sqrt(1 - a * a);
        }
    };

    public static final Interpolation circleOut = new Interpolation() {
        @Override
        public float apply(float a) {
            a--;
            return (float) Math.sqrt(1 - a * a);
        }
    };

    public static final Elastic elastic = new Elastic(2, 10);
    public static final Elastic elasticIn = new ElasticIn(2, 10);
    public static final Elastic elasticOut = new ElasticOut(2, 10);

    public static final Interpolation swing = new Swing(1.5f);
    public static final Interpolation swingIn = new SwingIn(2f);
    public static final Interpolation swingOut = new SwingOut(2f);

    public static final Interpolation bounce = new Bounce(4);
    public static final Interpolation bounceIn = new BounceIn(4);
    public static final Interpolation bounceOut = new BounceOut(4);

    //

    public static class Pow extends Interpolation {
        final int power;

        public Pow(int power) {
            this.power = power;
        }

        @Override
        public float apply(float a) {
            if (a <= 0.5f)
                return (float) Math.pow(a * 2, power) / 2;
            return (float) Math.pow((a - 1) * 2, power) / (power % 2 == 0 ? -2 : 2) + 1;
        }
    }

    public static class PowIn extends Pow {
        public PowIn(int power) {
            super(power);
        }

        @Override
        public float apply(float a) {
            return (float) Math.pow(a, power);
        }
    }

    public static class PowOut extends Pow {
        public PowOut(int power) {
            super(power);
        }

        @Override
        public float apply(float a) {
            return (float) Math.pow(a - 1, power) * (power % 2 == 0 ? -1 : 1) + 1;
        }
    }

    //

    public static class Exp extends Interpolation {
        final float value, power, min, scale;

        public Exp(float value, float power) {
            this.value = value;
            this.power = power;
            min = (float) Math.pow(value, -power);
            scale = 1 / (1 - min);
        }

        @Override
        public float apply(float a) {
            if (a <= 0.5f)
                return ((float) Math.pow(value, power * (a * 2 - 1)) - min) * scale / 2;
            return (2 - ((float) Math.pow(value, -power * (a * 2 - 1)) - min) * scale) / 2;
        }
    }

    public static class ExpIn extends Exp {
        public ExpIn(float value, float power) {
            super(value, power);
        }

        @Override
        public float apply(float a) {
            return ((float) Math.pow(value, power * (a - 1)) - min) * scale;
        }
    }

    public static class ExpOut extends Exp {
        public ExpOut(float value, float power) {
            super(value, power);
        }

        @Override
        public float apply(float a) {
            return 1 - ((float) Math.pow(value, -power * a) - min) * scale;
        }
    }

    //

    public static class Elastic extends Interpolation {
        final float value, power;

        public Elastic(float value, float power) {
            this.value = value;
            this.power = power;
        }

        @Override
        public float apply(float a) {
            if (a <= 0.5f) {
                a *= 2;
                return (float) Math.pow(value, power * (a - 1)) * MathUtils.sin(a * 20) * 1.0955f
                        / 2;
            }
            a = 1 - a;
            a *= 2;
            return 1 - (float) Math.pow(value, power * (a - 1)) * MathUtils.sin((a) * 20) * 1.0955f
                    / 2;
        }
    }

    public static class ElasticIn extends Elastic {
        public ElasticIn(float value, float power) {
            super(value, power);
        }

        @Override
        public float apply(float a) {
            return (float) Math.pow(value, power * (a - 1)) * MathUtils.sin(a * 20) * 1.0955f;
        }
    }

    public static class ElasticOut extends Elastic {
        public ElasticOut(float value, float power) {
            super(value, power);
        }

        @Override
        public float apply(float a) {
            a = 1 - a;
            return (1 - (float) Math.pow(value, power * (a - 1)) * MathUtils.sin(a * 20) * 1.0955f);
        }
    }

    //

    public static class Bounce extends BounceOut {
        public Bounce(float[] widths, float[] heights) {
            super(widths, heights);
        }

        public Bounce(int bounces) {
            super(bounces);
        }

        private float out(float a) {
            float test = a + widths[0] / 2;
            if (test < widths[0])
                return test / (widths[0] / 2) - 1;
            return super.apply(a);
        }

        @Override
        public float apply(float a) {
            if (a <= 0.5f)
                return (1 - out(1 - a * 2)) / 2;
            return out(a * 2 - 1) / 2 + 0.5f;
        }
    }

    public static class BounceOut extends Interpolation {
        final float[] widths, heights;

        public BounceOut(float[] widths, float[] heights) {
            if (widths.length != heights.length)
                throw new IllegalArgumentException("Must be the same number of widths and heights.");
            this.widths = widths;
            this.heights = heights;
        }

        public BounceOut(int bounces) {
            if (bounces < 2 || bounces > 5)
                throw new IllegalArgumentException("bounces cannot be < 2 or > 5: " + bounces);
            widths = new float[bounces];
            heights = new float[bounces];
            heights[0] = 1;
            switch (bounces) {
                case 2:
                    widths[0] = 0.6f;
                    widths[1] = 0.4f;
                    heights[1] = 0.33f;
                    break;
                case 3:
                    widths[0] = 0.4f;
                    widths[1] = 0.4f;
                    widths[2] = 0.2f;
                    heights[1] = 0.33f;
                    heights[2] = 0.1f;
                    break;
                case 4:
                    widths[0] = 0.34f;
                    widths[1] = 0.34f;
                    widths[2] = 0.2f;
                    widths[3] = 0.15f;
                    heights[1] = 0.26f;
                    heights[2] = 0.11f;
                    heights[3] = 0.03f;
                    break;
                case 5:
                    widths[0] = 0.3f;
                    widths[1] = 0.3f;
                    widths[2] = 0.2f;
                    widths[3] = 0.1f;
                    widths[4] = 0.1f;
                    heights[1] = 0.45f;
                    heights[2] = 0.3f;
                    heights[3] = 0.15f;
                    heights[4] = 0.06f;
                    break;
            }
            widths[0] *= 2;
        }

        @Override
        public float apply(float a) {
            a += widths[0] / 2;
            float width = 0, height = 0;
            for (int i = 0, n = widths.length; i < n; i++) {
                width = widths[i];
                if (a <= width) {
                    height = heights[i];
                    break;
                }
                a -= width;
            }
            a /= width;
            float z = 4 / width * height * a;
            return 1 - (z - z * a) * width;
        }
    }

    public static class BounceIn extends BounceOut {
        public BounceIn(float[] widths, float[] heights) {
            super(widths, heights);
        }

        public BounceIn(int bounces) {
            super(bounces);
        }

        @Override
        public float apply(float a) {
            return 1 - super.apply(1 - a);
        }
    }

    //

    public static class Swing extends Interpolation {
        private final float scale;

        public Swing(float scale) {
            this.scale = scale * 2;
        }

        @Override
        public float apply(float a) {
            if (a <= 0.5f) {
                a *= 2;
                return a * a * ((scale + 1) * a - scale) / 2;
            }
            a--;
            a *= 2;
            return a * a * ((scale + 1) * a + scale) / 2 + 1;
        }
    }

    public static class SwingOut extends Interpolation {
        private final float scale;

        public SwingOut(float scale) {
            this.scale = scale;
        }

        @Override
        public float apply(float a) {
            a--;
            return a * a * ((scale + 1) * a + scale) + 1;
        }
    }

    public static class SwingIn extends Interpolation {
        private final float scale;

        public SwingIn(float scale) {
            this.scale = scale;
        }

        @Override
        public float apply(float a) {
            return a * a * ((scale + 1) * a - scale);
        }
    }
}
