/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oscim.utils.animation;

/**
 * See: https://developer.android.com/reference/android/support/animation/FlingAnimation.html
 * Package: android.support.animation.FlingAnimation
 */
public final class DragForce {

    private static final float DEFAULT_FRICTION = -4.2f;
    private static final float DEFAULT_MIN_VISIBLE_CHANGE_PIXELS = 0.5f;

    // This multiplier is used to calculate the velocity threshold given a certain value
    // threshold. The idea is that if it takes >= 1 frame to move the value threshold amount,
    // then the velocity is a reasonable threshold.
    private static final float VELOCITY_THRESHOLD_MULTIPLIER = 1000f / 16f; // 1 frame ≙ 16 ms (62.5 fps)
    private float mFriction = DEFAULT_FRICTION;
    private float mVelocityThreshold = DEFAULT_MIN_VISIBLE_CHANGE_PIXELS * VELOCITY_THRESHOLD_MULTIPLIER;

    // Internal state to hold a value/velocity pair.
    private float mValue;
    private float mVelocity;

    public void setFrictionScalar(float frictionScalar) {
        mFriction = frictionScalar * DEFAULT_FRICTION;
    }

    public float getFrictionScalar() {
        return mFriction / DEFAULT_FRICTION;
    }

    /**
     * Updates the animation state (i.e. value and velocity).
     *
     * @param deltaT time elapsed in millisecond since last frame
     * @return the value delta since last frame
     */
    public float updateValueAndVelocity(long deltaT) {
        float velocity = mVelocity;
        mVelocity = (float) (velocity * Math.exp((deltaT / 1000f) * mFriction));
        float valueDelta = (mVelocity - velocity);
        mValue += valueDelta;
        if (isAtEquilibrium(mValue, mVelocity)) {
            mVelocity = 0f;
        }
        return valueDelta;
    }

    public void setValueAndVelocity(float value, float velocity) {
        mValue = value;
        mVelocity = velocity;
    }

    public float getValue() {
        return mValue;
    }

    public float getVelocity() {
        return mVelocity;
    }

    public float getAcceleration(float position, float velocity) {
        return velocity * mFriction;
    }

    public boolean isAtEquilibrium(float value, float velocity) {
        return Math.abs(velocity) < mVelocityThreshold;
    }

    public void setValueThreshold(float threshold) {
        mVelocityThreshold = threshold * VELOCITY_THRESHOLD_MULTIPLIER;
    }
}