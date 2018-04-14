/*
 * Copyright 2012 Hannes Janetzek
 * Copyright 2016-2018 devemux86
 * Copyright 2016 Erik Duisters
 * Copyright 2017 Luca Osten
 * Copyright 2018 Izumi Kawashima
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
package org.oscim.map;

import org.oscim.core.BoundingBox;
import org.oscim.core.Box;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.core.MercatorProjection;
import org.oscim.core.Point;
import org.oscim.core.Tile;
import org.oscim.renderer.GLMatrix;
import org.oscim.utils.FastMath;

/**
 * The Viewport class contains a MapPosition and the projection matrices.
 * It provides functions to modify the MapPosition and translate between
 * map and screen coordinates.
 * <p>
 * Public methods are thread safe.
 */
public class Viewport {

    public final static int MAX_ZOOM_LEVEL = 20;
    public final static int MIN_ZOOM_LEVEL = 2;
    public final static float MIN_TILT = 0;

    /* Note: limited by initial value of 'numTiles' in 'TileManager' to 81-83 degree*/
    public final static float MAX_TILT = 65;

    protected double mMaxScale = (1 << MAX_ZOOM_LEVEL);
    protected double mMinScale = (1 << MIN_ZOOM_LEVEL);

    protected float mMinTilt = MIN_TILT;
    protected float mMaxTilt = MAX_TILT;

    protected float mMinBearing = -180;
    protected float mMaxBearing = 180;

    protected float mMinRoll = -180;
    protected float mMaxRoll = 180;

    protected double mMinX = 0;
    protected double mMaxX = 1;
    protected double mMinY = 0;
    protected double mMaxY = 1;

    protected final MapPosition mPos = new MapPosition();

    protected final GLMatrix mProjMatrix = new GLMatrix();
    protected final GLMatrix mProjMatrixUnscaled = new GLMatrix();
    protected final GLMatrix mProjMatrixInverse = new GLMatrix();
    protected final GLMatrix mRotationMatrix = new GLMatrix();
    protected final GLMatrix mViewMatrix = new GLMatrix();
    protected final GLMatrix mViewProjMatrix = new GLMatrix();
    protected final GLMatrix mUnprojMatrix = new GLMatrix();
    protected final GLMatrix mTmpMatrix = new GLMatrix();

    /* temporary vars: only use in synchronized functions! */
    protected final Point mMovePoint = new Point();
    protected final float[] mv = new float[4];
    protected final float[] mu = new float[4];
    protected final float[] mViewCoords = new float[8];

    protected float mHeight, mWidth;

    public final static float VIEW_DISTANCE = 3.0f;
    public final static float VIEW_NEAR = 1;
    public final static float VIEW_FAR = 8;
    /**
     * scale map plane at VIEW_DISTANCE to near plane
     */
    public final static float VIEW_SCALE = (VIEW_NEAR / VIEW_DISTANCE) * 0.5f;

    public Viewport() {
        mPos.scale = mMinScale;
        mPos.x = 0.5;
        mPos.y = 0.5;
        mPos.zoomLevel = MIN_ZOOM_LEVEL;
        mPos.bearing = 0;
        mPos.tilt = 0;
        mPos.roll = 0;
    }

    public double limitScale(double scale) {
        if (scale > mMaxScale)
            return mMaxScale;
        else if (scale < mMinScale)
            return mMinScale;

        return scale;
    }

    public float limitTilt(float tilt) {
        if (tilt > mMaxTilt)
            return mMaxTilt;
        else if (tilt < mMinTilt)
            return mMinTilt;

        return tilt;
    }

    public boolean limitPosition(MapPosition pos) {
        boolean changed = false;
        if (pos.scale > mMaxScale) {
            pos.scale = mMaxScale;
            changed = true;
        } else if (pos.scale < mMinScale) {
            pos.scale = mMinScale;
            changed = true;
        }

        if (pos.tilt > mMaxTilt) {
            pos.tilt = mMaxTilt;
            changed = true;
        } else if (pos.tilt < mMinTilt) {
            pos.tilt = mMinTilt;
            changed = true;
        }

        if (pos.bearing > mMaxBearing) {
            pos.bearing = mMaxBearing;
            changed = true;
        } else if (pos.bearing < mMinBearing) {
            pos.bearing = mMinBearing;
            changed = true;
        }

        if (pos.roll > mMaxRoll) {
            pos.roll = mMaxRoll;
            changed = true;
        } else if (pos.roll < mMinRoll) {
            pos.roll = mMinRoll;
            changed = true;
        }

        if (pos.x > mMaxX) {
            pos.x = mMaxX;
            changed = true;
        } else if (pos.x < mMinX) {
            pos.x = mMinX;
            changed = true;
        }

        if (pos.y > mMaxY) {
            pos.y = mMaxY;
            changed = true;
        } else if (pos.y < mMinY) {
            pos.y = mMinY;
            changed = true;
        }

        return changed;
    }

    /**
     * Get the current MapPosition.
     *
     * @param pos MapPosition to be updated.
     * @return true iff current position is different from
     * passed position.
     */
    public boolean getMapPosition(MapPosition pos) {

        boolean changed = (pos.scale != mPos.scale
                || pos.x != mPos.x
                || pos.y != mPos.y
                || pos.bearing != mPos.bearing
                || pos.tilt != mPos.tilt
                || pos.roll != mPos.roll);

        pos.bearing = mPos.bearing;
        pos.tilt = mPos.tilt;
        pos.roll = mPos.roll;

        pos.x = mPos.x;
        pos.y = mPos.y;
        pos.scale = mPos.scale;
        pos.zoomLevel = FastMath.log2((int) mPos.scale);

        return changed;
    }

    /**
     * Get the inverse projection of the viewport, i.e. the
     * coordinates with z==0 that will be projected exactly
     * to screen corners by current view-projection-matrix.
     *
     * @param box float[8] will be set.
     * @param add increase extents of box
     */
    public void getMapExtents(float[] box, float add) {
        /* top-right */
        unproject(1, -1, box, 0);
        /* top-left */
        unproject(-1, -1, box, 2);
        /* bottom-left */
        unproject(-1, 1, box, 4);
        /* bottom-right */
        unproject(1, 1, box, 6);

        if (add == 0)
            return;

        for (int i = 0; i < 8; i += 2) {
            float x = box[i];
            float y = box[i + 1];
            float len = (float) Math.sqrt(x * x + y * y);
            box[i + 0] += x / len * add;
            box[i + 1] += y / len * add;
        }
    }

    protected synchronized void unproject(float x, float y, float[] coords, int position) {
        mv[0] = x;
        mv[1] = y;
        mv[2] = -1;
        mUnprojMatrix.prj(mv);
        double nx = mv[0];
        double ny = mv[1];
        double nz = mv[2];

        mv[0] = x;
        mv[1] = y;
        mv[2] = 1;
        mUnprojMatrix.prj(mv);
        double fx = mv[0];
        double fy = mv[1];
        double fz = mv[2];

        double dx = fx - nx;
        double dy = fy - ny;
        double dz = fz - nz;

        double dist = -nz / dz;

        coords[position + 0] = (float) (nx + dist * dx);
        coords[position + 1] = (float) (ny + dist * dy);
    }

    /**
     * Get the minimal axis-aligned BoundingBox that encloses
     * the visible part of the map. Sets box to map coordinates:
     * xmin,ymin,xmax,ymax
     */
    public synchronized Box getBBox(Box box, int expand) {
        if (box == null)
            box = new Box();

        float[] coords = mViewCoords;
        getMapExtents(coords, expand);

        box.xmin = coords[0];
        box.xmax = coords[0];
        box.ymin = coords[1];
        box.ymax = coords[1];

        for (int i = 2; i < 8; i += 2) {
            box.xmin = Math.min(box.xmin, coords[i]);
            box.xmax = Math.max(box.xmax, coords[i]);
            box.ymin = Math.min(box.ymin, coords[i + 1]);
            box.ymax = Math.max(box.ymax, coords[i + 1]);
        }

        double cs = mPos.scale * Tile.SIZE;
        double cx = mPos.x * cs;
        double cy = mPos.y * cs;

        box.xmin = (cx + box.xmin) / cs;
        box.xmax = (cx + box.xmax) / cs;
        box.ymin = (cy + box.ymin) / cs;
        box.ymax = (cy + box.ymax) / cs;

        return box;
    }

    /**
     * Get the GeoPoint for x,y in screen coordinates.
     *
     * @param x screen coordinate
     * @param y screen coordinate
     * @return the corresponding GeoPoint
     */
    public synchronized GeoPoint fromScreenPoint(float x, float y) {
        fromScreenPoint(x, y, mMovePoint);
        return new GeoPoint(
                MercatorProjection.toLatitude(mMovePoint.y),
                MercatorProjection.toLongitude(mMovePoint.x));
    }

    protected void unprojectScreen(double x, double y, float[] out) {
        /* scale to -1..1 */
        float mx = (float) (1 - (x / mWidth * 2));
        float my = (float) (1 - (y / mHeight * 2));

        unproject(-mx, my, out, 0);
    }

    /**
     * Get the map position for x,y in screen coordinates.
     *
     * @param x screen coordinate
     * @param y screen coordinate
     */
    public synchronized void fromScreenPoint(double x, double y, Point out) {
        unprojectScreen(x, y, mu);

        double cs = mPos.scale * Tile.SIZE;
        double cx = mPos.x * cs;
        double cy = mPos.y * cs;

        double dx = cx + mu[0];
        double dy = cy + mu[1];

        dx /= cs;
        dy /= cs;

        while (dx > 1)
            dx -= 1;
        while (dx < 0)
            dx += 1;

        if (dy > 1)
            dy = 1;
        else if (dy < 0)
            dy = 0;

        out.x = dx;
        out.y = dy;
    }

    /**
     * Get the screen pixel for a GeoPoint (relative to center)
     *
     * @param geoPoint the GeoPoint
     * @param out      Point projected to screen pixel relative to center
     */
    public void toScreenPoint(GeoPoint geoPoint, Point out) {
        toScreenPoint(geoPoint, true, out);
    }

    /**
     * Get the screen pixel for a GeoPoint
     *
     * @param geoPoint the GeoPoint
     * @param out      Point projected to screen pixel
     */
    public void toScreenPoint(GeoPoint geoPoint, boolean relativeToCenter, Point out) {
        MercatorProjection.project(geoPoint, out);
        toScreenPoint(out.x, out.y, relativeToCenter, out);
    }

    /**
     * Get the screen pixel for map coordinates (relative to center)
     *
     * @param out Point projected to screen coordinate relative to center
     */
    public void toScreenPoint(double x, double y, Point out) {
        toScreenPoint(x, y, true, out);
    }

    /**
     * Get the screen pixel for map coordinates
     *
     * @param out Point projected to screen coordinate
     */
    public synchronized void toScreenPoint(double x, double y, boolean relativeToCenter, Point out) {

        double cs = mPos.scale * Tile.SIZE;
        double cx = mPos.x * cs;
        double cy = mPos.y * cs;

        mv[0] = (float) (x * cs - cx);
        mv[1] = (float) (y * cs - cy);

        mv[2] = 0;
        mv[3] = 1;

        mViewProjMatrix.prj(mv);

        out.x = (mv[0] * (mWidth / 2));
        out.y = -(mv[1] * (mHeight / 2));

        if (!relativeToCenter) {
            out.x += mWidth / 2;
            out.y += mHeight / 2;
        }
    }

    boolean sizeChanged(Viewport viewport) {
        return mHeight != viewport.mHeight || mWidth != viewport.mWidth;
    }

    protected boolean copy(Viewport viewport) {
        boolean sizeChanged = sizeChanged(viewport);

        mHeight = viewport.mHeight;
        mWidth = viewport.mWidth;
        mProjMatrix.copy(viewport.mProjMatrix);
        mProjMatrixUnscaled.copy(viewport.mProjMatrixUnscaled);
        mProjMatrixInverse.copy(viewport.mProjMatrixInverse);

        mUnprojMatrix.copy(viewport.mUnprojMatrix);
        mRotationMatrix.copy(viewport.mRotationMatrix);
        mViewMatrix.copy(viewport.mViewMatrix);
        mViewProjMatrix.copy(viewport.mViewProjMatrix);
        return viewport.getMapPosition(mPos) || sizeChanged;
    }

    public double getMaxScale() {
        return mMaxScale;
    }

    public void setMaxScale(double maxScale) {
        this.mMaxScale = maxScale;
    }

    public double getMinScale() {
        return mMinScale;
    }

    public void setMinScale(double minScale) {
        this.mMinScale = minScale;
    }

    public int getMaxZoomLevel() {
        return FastMath.log2((int) mMaxScale);
    }

    public void setMaxZoomLevel(int maxZoomLevel) {
        this.mMaxScale = (1 << maxZoomLevel);
    }

    public int getMinZoomLevel() {
        return FastMath.log2((int) mMinScale);
    }

    public void setMinZoomLevel(int minZoomLevel) {
        this.mMinScale = (1 << minZoomLevel);
    }

    public float getMaxTilt() {
        return mMaxTilt;
    }

    public void setMaxTilt(float maxTilt) {
        this.mMaxTilt = maxTilt;
    }

    public float getMinTilt() {
        return mMinTilt;
    }

    public void setMinTilt(float minTilt) {
        this.mMinTilt = minTilt;
    }

    public float getMaxBearing() {
        return mMaxBearing;
    }

    public void setMaxBearing(float maxBearing) {
        this.mMaxBearing = maxBearing;
    }

    public float getMinBearing() {
        return mMinBearing;
    }

    public void setMinBearing(float minBearing) {
        this.mMinBearing = minBearing;
    }

    public float getMaxRoll() {
        return mMaxRoll;
    }

    public void setMaxRoll(float maxRoll) {
        this.mMaxRoll = maxRoll;
    }

    public float getMinRoll() {
        return mMinRoll;
    }

    public void setMinRoll(float minRoll) {
        this.mMinRoll = minRoll;
    }

    public double getMaxX() {
        return mMaxX;
    }

    public void setMaxX(double maxX) {
        this.mMaxX = maxX;
    }

    public double getMinX() {
        return mMinX;
    }

    public void setMinX(double minX) {
        this.mMinX = minX;
    }

    public double getMaxY() {
        return mMaxY;
    }

    public void setMaxY(double maxY) {
        this.mMaxY = maxY;
    }

    public double getMinY() {
        return mMinY;
    }

    public void setMinY(double minY) {
        this.mMinY = minY;
    }

    public void setMapLimit(double minX, double minY, double maxX, double maxY) {
        this.mMinX = minX;
        this.mMinY = minY;
        this.mMaxX = maxX;
        this.mMaxY = maxY;
    }

    public BoundingBox getMapLimit() {
        return new BoundingBox(
                MercatorProjection.toLatitude(mMaxY), MercatorProjection.toLongitude(mMinX),
                MercatorProjection.toLatitude(mMinY), MercatorProjection.toLongitude(mMaxX)
        );
    }

    public void setMapLimit(BoundingBox mapLimit) {
        this.mMinX = MercatorProjection.longitudeToX(mapLimit.getMinLongitude());
        this.mMinY = MercatorProjection.latitudeToY(mapLimit.getMaxLatitude());
        this.mMaxX = MercatorProjection.longitudeToX(mapLimit.getMaxLongitude());
        this.mMaxY = MercatorProjection.latitudeToY(mapLimit.getMinLatitude());
    }
}
