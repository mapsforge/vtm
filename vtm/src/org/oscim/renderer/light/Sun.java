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
package org.oscim.renderer.light;

import org.oscim.backend.DateTimeAdapter;
import org.oscim.backend.canvas.Color;
import org.oscim.utils.ColorUtil;
import org.oscim.utils.geom.GeometryUtils;
import org.oscim.utils.math.MathUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * See https://lexikon.astronomie.info/zeitgleichung
 */
public class Sun {

    public static float SHADOW_ALPHA = 0.3f;

    private static final DateTimeAdapter date = DateTimeAdapter.instance;
    private float mSunrise; // in hours
    private float mSunset; // in hours
    private float mLatitude; // in degree
    private float mLongitude; // in degree
    private int mDayOfYear; // up to 366
    private float mProgress; // 0 to 2

    private float[] mSunPos = new float[3];
    private int mLightColor;
    private Map<Float, Integer> mColorMap;

    /**
     * Track sun position (accuracy of ~1 minute).
     */
    public Sun() {
        // Init defaults
        mDayOfYear = date.getDayOfYear();
        setCoordinates(0, 0);
        mLightColor = Color.get(SHADOW_ALPHA, 255, 255, 255);
        setProgress(0.4f);
        updatePosition();
    }

    /**
     * @return the latitude where the sun is in zenith
     */
    private float declination() {
        return (float) (0.4095 * Math.sin(0.016906 * (mDayOfYear - 80.086)));
    }

    /**
     * The discrepancy of mean time and sun time.
     *
     * @return discrepancy in hours
     */
    private float discrepancyMeanTime() {
        return (float) (-0.171 * Math.sin(0.0337 * mDayOfYear + 0.465) - 0.1299 * Math.sin(0.01787 * mDayOfYear - 0.168));
    }

    /**
     * RGB - the color of sun.
     * A - the diffuse of shadow.
     */
    public int getColor() {
        return mLightColor;
    }

    /**
     * Get the colors of day cycle.
     *
     * @return the color map
     */
    public Map<Float, Integer> getColorMap() {
        return mColorMap;
    }

    public float getLatitude() {
        return mLatitude;
    }

    public float getLongitude() {
        return mLongitude;
    }

    public float[] getPosition() {
        return mSunPos;
    }

    public float getProgress() {
        return mProgress;
    }

    /**
     * @return the local sunrise time in hours.
     */
    public float getSunrise() {
        return mSunrise;
    }

    /**
     * @return the local sunset time in hours.
     */
    public float getSunset() {
        return mSunset;
    }

    private void initDefaultColorMap() {
        mColorMap = new HashMap<>();
        mColorMap.put(0.0f, Color.get((int) (255 * SHADOW_ALPHA), 150, 120, 140)); // Sunrise
        mColorMap.put(0.04f, Color.get((int) (255 * SHADOW_ALPHA), 205, 170, 160));
        mColorMap.put(0.1f, Color.get((int) (255 * SHADOW_ALPHA), 245, 240, 215));
        mColorMap.put(0.2f, Color.get((int) (255 * SHADOW_ALPHA), 255, 255, 255)); // Forenoon
        mColorMap.put(0.8f, Color.get((int) (255 * SHADOW_ALPHA), 255, 255, 255)); // Afternoon
        mColorMap.put(0.99f, Color.get((int) (255 * SHADOW_ALPHA), 255, 220, 230));
        mColorMap.put(1.0f, Color.get((int) (255 * SHADOW_ALPHA), 100, 100, 130)); // Sunset
        mColorMap.put(1.9f, Color.get((int) (255 * SHADOW_ALPHA), 100, 100, 130)); // Night
    }

    /**
     * @param color RGB - the color of sun, A - the diffuse of shadow
     */
    public void setColor(int color) {
        mLightColor = color;
    }

    /**
     * Set the colors of day cycle.
     */
    public void setColorMap(Map<Float, Integer> colorMap) {
        mColorMap = colorMap;
    }

    public void setCoordinates(float latitude, float longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
        updateToDay();
    }

    /**
     * Customize day of the year.
     */
    public void setDayOfYear(int day) {
        mDayOfYear = day;
    }

    public void setPosition(float x, float y, float z) {
        mSunPos[0] = x;
        mSunPos[1] = y;
        mSunPos[2] = z;
        mSunPos = GeometryUtils.normalize(mSunPos);
    }

    /**
     * Customize progress.
     */
    public void setProgress(float progress) {
        mProgress = progress;
    }

    /**
     * Customize progress with specified time.
     *
     * @param hour   the hour [0..23]
     * @param minute the minute [0..59]
     * @param second the second [0..59]
     * @return the progress in range 0 to 2
     */
    public float setProgress(int hour, int minute, int second) {
        float time = hour;
        time += minute / 60f;
        time += second / 3600f;

        float progress = (time - mSunrise) / (mSunset - mSunrise);
        if (progress > 1f || progress < 0f) {
            progress = ((time + 24 - mSunset) % 24) / (mSunrise + 24 - mSunset);
            progress += 1;
        }
        mProgress = MathUtils.clamp(progress, 0f, 2f);
        return mProgress;
    }

    /**
     * @param h the offset of horizon (in radians) e.g. bend of earth / atmosphere etc.
     * @return the difference of sunrise or sunset to noon
     */
    private float timeDiff(float h) {
        float lat = mLatitude * MathUtils.degreesToRadians;
        float declination = declination();
        return (float) (12 * Math.acos((Math.sin(h) - Math.sin(lat) * Math.sin(declination)) / (Math.cos(lat) * Math.cos(declination))) / Math.PI);
    }

    /**
     * Update sun progress, position and color to current date time.
     */
    public void update() {
        updateProgress();
        updatePosition();
        updateColor();
    }

    public int updateColor() {
        if (mColorMap == null)
            initDefaultColorMap();

        float progressStart, progressEnd;
        Iterator<Float> prIter = mColorMap.keySet().iterator();
        progressStart = progressEnd = prIter.next();

        while (prIter.hasNext()) {
            float progress = prIter.next();
            if (((mProgress + 2f - progress) % 2f) < ((mProgress + 2f - progressStart) % 2f))
                progressStart = progress;
            else if (((progress + 2f - mProgress) % 2f) < ((progressEnd + 2f - mProgress) % 2f))
                progressEnd = progress;
        }

        if (progressStart == progressEnd) {
            mLightColor = mColorMap.get(progressStart);
            return mLightColor;
        }

        float fraction = ((mProgress + 2f - progressStart) % 2f) / ((progressEnd + 2f - progressStart) % 2f);

        int colorStart = mColorMap.get(progressStart);
        int colorEnd = mColorMap.get(progressEnd);
        mLightColor = ColorUtil.blend(colorStart, colorEnd, fraction);
        return mLightColor;
    }

    /**
     * Very simple normalized sun coordinates.
     */
    public float[] updatePosition() {
        mSunPos[0] = (float) Math.cos(mProgress * Math.PI);
        mSunPos[1] = (float) Math.sin(mProgress * Math.PI);
        mSunPos[2] = 3 * mSunPos[1];
        mSunPos = GeometryUtils.normalize(mSunPos);
        return mSunPos;
    }

    /**
     * The progress
     * of the daylight in range 0 (sunrise) to 1 (sunset) and
     * of the night in range 1 (sunset) to 2 (sunrise).
     *
     * @return the progress in range 0 to 2
     */
    public float updateProgress() {
        return setProgress(date.getHour(), date.getMinute(), date.getSecond());
    }

    /**
     * Calculate the sunrise and sunset of set day (local time).
     */
    public void updateToDay() {
        float h = -0.0145f; // -50 latitude minutes

        float diff = timeDiff(h);
        float discp = discrepancyMeanTime();
        float calc = 12 - discp - (mLongitude / 15f) + (date.getTimeZoneOffset() / (60f * 60f * 1000f));
        mSunrise = calc - diff;
        mSunset = calc + diff;
    }
}
