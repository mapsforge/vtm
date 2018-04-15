/*
 * Copyright 2012 osmdroid authors: Viesturs Zarins, Martin Pearman
 * Copyright 2012 Hannes Janetzek
 * Copyright 2016-2017 devemux86
 * Copyright 2016 Pedinel
 * Copyright 2017 Andrey Novikov
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
package org.oscim.layers.vector;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.oscim.backend.CanvasAdapter;
import org.oscim.core.GeoPoint;
import org.oscim.core.Point;
import org.oscim.layers.vector.geometries.LineDrawable;
import org.oscim.layers.vector.geometries.Style;
import org.oscim.map.Map;
import org.oscim.utils.GeoPointUtils;
import org.oscim.utils.geom.GeomBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class draws a path line in given color or texture.
 */
public class PathLayer extends VectorLayer {

    private static final boolean USE_INT = true;

    protected final ArrayList<GeoPoint> mPoints;

    protected Style mStyle;
    protected LineDrawable mDrawable;

    private final Point mPoint1 = new Point();
    private final Point mPoint2 = new Point();

    public PathLayer(Map map, Style style) {
        super(map, USE_INT);
        mStyle = style;

        mPoints = new ArrayList<>();
    }

    public PathLayer(Map map, int lineColor, float lineWidth) {
        this(map, Style.builder()
                .fixed(true)
                .strokeColor(lineColor)
                .strokeWidth(lineWidth)
                .build());
    }

    public PathLayer(Map map, int lineColor) {
        this(map, lineColor, 2);
    }

    public void setStyle(Style style) {
        mStyle = style;
    }

    public void clearPath() {
        if (!mPoints.isEmpty())
            mPoints.clear();

        updatePoints();
    }

    public void setPoints(Collection<? extends GeoPoint> pts) {
        mPoints.clear();
        mPoints.addAll(pts);
        updatePoints();
    }

    public void addPoint(GeoPoint pt) {
        mPoints.add(pt);
        updatePoints();
    }

    public void addPoint(int latitudeE6, int longitudeE6) {
        mPoints.add(new GeoPoint(latitudeE6, longitudeE6));
        updatePoints();
    }

    public void addPoints(Collection<? extends GeoPoint> pts) {
        mPoints.addAll(pts);
        updatePoints();
    }

    private void updatePoints() {
        synchronized (this) {

            if (mDrawable != null) {
                remove(mDrawable);
                mDrawable = null;
            }

            if (!mPoints.isEmpty()) {
                mDrawable = new LineDrawable(mPoints, mStyle);
                if (mDrawable.getGeometry() == null)
                    mDrawable = null;
                else
                    add(mDrawable);
            }
        }
        mWorker.submit(0);
    }

    public List<GeoPoint> getPoints() {
        return mPoints;
    }

    /**
     * Draw a great circle. Calculate a point for every 100km along the path.
     *
     * @param startPoint start point of the great circle
     * @param endPoint   end point of the great circle
     */
    public void addGreatCircle(GeoPoint startPoint, GeoPoint endPoint) {
        synchronized (mPoints) {

            /* get the great circle path length in meters */
            double length = startPoint.sphericalDistance(endPoint);

            /* add one point for every 100kms of the great circle path */
            int numberOfPoints = (int) (length / 100000);
            if (numberOfPoints == 0)
                return;

            addGreatCircle(startPoint, endPoint, numberOfPoints);
        }
    }

    /**
     * Draw a great circle.
     *
     * @param startPoint     start point of the great circle
     * @param endPoint       end point of the great circle
     * @param numberOfPoints number of points to calculate along the path
     */
    public void addGreatCircle(GeoPoint startPoint, GeoPoint endPoint,
                               final int numberOfPoints) {
        /* adapted from page
         * http://compastic.blogspot.co.uk/2011/07/how-to-draw-great-circle-on-map-in.html
         * which was adapted from page http://maps.forum.nu/gm_flight_path.html */

        GeomBuilder gb = new GeomBuilder();

        /* convert to radians */
        double lat1 = startPoint.getLatitude() * Math.PI / 180;
        double lon1 = startPoint.getLongitude() * Math.PI / 180;
        double lat2 = endPoint.getLatitude() * Math.PI / 180;
        double lon2 = endPoint.getLongitude() * Math.PI / 180;

        double d = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin((lat1 - lat2) / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin((lon1 - lon2) / 2), 2)));
        double bearing = Math.atan2(
                Math.sin(lon1 - lon2) * Math.cos(lat2),
                Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                        * Math.cos(lat2)
                        * Math.cos(lon1 - lon2))
                / -(Math.PI / 180);
        bearing = bearing < 0 ? 360 + bearing : bearing;

        for (int i = 0, j = numberOfPoints + 1; i < j; i++) {
            double f = 1.0 / numberOfPoints * i;
            double A = Math.sin((1 - f) * d) / Math.sin(d);
            double B = Math.sin(f * d) / Math.sin(d);
            double x = A * Math.cos(lat1) * Math.cos(lon1) + B * Math.cos(lat2)
                    * Math.cos(lon2);
            double y = A * Math.cos(lat1) * Math.sin(lon1) + B * Math.cos(lat2)
                    * Math.sin(lon2);
            double z = A * Math.sin(lat1) + B * Math.sin(lat2);

            double latN = Math.atan2(z, Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
            double lonN = Math.atan2(y, x);

            gb.point(latN / (Math.PI / 180), lonN / (Math.PI / 180));
        }

        setLineString(gb.toLineString());
    }

    public void setLineString(LineString path) {
        synchronized (this) {
            if (mDrawable != null)
                remove(mDrawable);
            mDrawable = new LineDrawable(path, mStyle);
            add(mDrawable);

            mPoints.clear();
            for (int i = 0; i < path.getNumPoints(); i++) {
                Coordinate c = path.getCoordinateN(i);
                mPoints.add(new GeoPoint(c.y, c.x));
            }
        }
        mWorker.submit(0);
    }

    public void setLineString(double[] lonLat) {
        synchronized (this) {
            if (mDrawable != null)
                remove(mDrawable);
            mDrawable = new LineDrawable(lonLat, mStyle);
            add(mDrawable);

            mPoints.clear();
            for (int i = 0; i < lonLat.length; i += 2)
                mPoints.add(new GeoPoint(lonLat[i + 1], lonLat[i]));
        }
        mWorker.submit(0);
    }

    @Override
    public synchronized boolean contains(float x, float y) {
        // Touch min 20 px at baseline mdpi (160dpi)
        double distance = Math.max(20 / 2 * CanvasAdapter.getScale(), mStyle.strokeWidth);
        for (int i = 0; i < mPoints.size() - 1; i++) {
            if (i == 0)
                mMap.viewport().toScreenPoint(mPoints.get(i), false, mPoint1);
            else {
                mPoint1.x = mPoint2.x;
                mPoint1.y = mPoint2.y;
            }
            mMap.viewport().toScreenPoint(mPoints.get(i + 1), false, mPoint2);
            if (GeoPointUtils.distanceSegmentPoint(mPoint1.x, mPoint1.y, mPoint2.x, mPoint2.y, x, y) <= distance)
                return true;
        }
        return false;
    }
}
