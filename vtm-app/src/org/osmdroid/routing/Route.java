package org.osmdroid.routing;

import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.osmdroid.routing.provider.GoogleRouteProvider;
import org.osmdroid.routing.provider.MapQuestRouteProvider;
import org.osmdroid.routing.provider.OSRMRouteProvider;
import org.osmdroid.utils.DouglasPeuckerReducer;

import java.util.ArrayList;
import java.util.List;

/**
 * describes the way to go from a position to an other. Normally returned by a
 * call to a Directions API (from MapQuest, GoogleMaps or other)
 *
 * @author M.Kergall
 * @see MapQuestRouteProvider
 * @see GoogleRouteProvider
 * @see OSRMRouteProvider
 */
public class Route {
    //static final Logger log = LoggerFactory.getLogger(Route.class);

    /**
     * @see #STATUS_INVALID STATUS_INVALID
     * @see #STATUS_OK STATUS_OK
     * @see #STATUS_DEFAULT STATUS_DEFAULT
     */
    public int status;

    /**
     * length of the whole route in km.
     */
    public double length;
    /**
     * duration of the whole trip in sec.
     */
    public double duration;
    public List<RouteNode> nodes;
    /** */
    /**
     * there is one leg between each waypoint
     */
    public List<RouteLeg> legs;
    /**
     * full shape: polyline, as an array of GeoPoints
     */
    public List<GeoPoint> routeHigh;
    /**
     * the same, in low resolution (less points)
     */
    private List<GeoPoint> routeLow;
    /**
     * route bounding box
     */
    public BoundingBox boundingBox;

    /**
     * STATUS_INVALID = route not built
     */
    public static final int STATUS_INVALID = 0;
    /**
     * STATUS_OK = route properly retrieved and built
     */
    public static final int STATUS_OK = 1;
    /**
     * STATUS_DEFAULT = any issue (technical issue, or no possible route) led to
     * build a default route
     */
    public static final int STATUS_DEFAULT = 2;

    private void init() {
        status = STATUS_INVALID;
        length = 0.0;
        duration = 0.0;
        nodes = new ArrayList<RouteNode>();
        routeHigh = new ArrayList<GeoPoint>();
        routeLow = null;
        legs = new ArrayList<RouteLeg>();
        boundingBox = null;
    }

    public Route() {
        init();
    }

    /**
     * default constructor when normal loading failed: the route shape only
     * contains the waypoints; All distances and times are at 0; there is no
     * node; status equals DEFAULT.
     *
     * @param waypoints ...
     */
    public Route(List<GeoPoint> waypoints) {
        init();
        int n = waypoints.size();
        for (int i = 0; i < n; i++) {
            GeoPoint p = waypoints.get(i);
            routeHigh.add(p);
        }
        for (int i = 0; i < n - 1; i++) {
            RouteLeg leg = new RouteLeg(/* i, i+1, mLinks */);
            legs.add(leg);
        }
        boundingBox = new BoundingBox(routeHigh);
        status = STATUS_DEFAULT;
    }

    /**
     * @return the route shape in "low resolution" = simplified by around 10
     * factor.
     */
    public List<GeoPoint> getRouteLow() {
        if (routeLow == null) {
            // Simplify the route (divide number of points by around 10):
            //int n = routeHigh.size();
            routeLow = DouglasPeuckerReducer.reduceWithTolerance(routeHigh, 1500.0);
            //log.debug("route reduced from " + n + " to " + routeLow.size()
            //        + " points");
        }
        return routeLow;
    }

    public void setRouteLow(ArrayList<GeoPoint> route) {
        routeLow = route;
    }

    /**
     * @param pLength   in km
     * @param pDuration in sec
     * @return a human-readable length&duration text.
     */
    public String getLengthDurationText(double pLength, double pDuration) {
        String result;
        if (pLength >= 100.0) {
            result = (int) (pLength) + " km, ";
        } else if (pLength >= 1.0) {
            result = Math.round(pLength * 10) / 10.0 + " km, ";
        } else {
            result = (int) (pLength * 1000) + " m, ";
        }
        int totalSeconds = (int) pDuration;
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds / 60) - (hours * 60);
        int seconds = (totalSeconds % 60);
        if (hours != 0) {
            result += hours + " h ";
        }
        if (minutes != 0) {
            result += minutes + " min";
        }
        if (hours == 0 && minutes == 0) {
            result += seconds + " s";
        }
        return result;
    }

    /**
     * @param leg leg index, starting from 0. -1 for the whole route
     * @return length and duration of the whole route, or of a leg of the route,
     * as a String, in a readable format.
     */
    public String getLengthDurationText(int leg) {
        double len = (leg == -1 ? this.length : legs.get(leg).length);
        double dur = (leg == -1 ? this.duration : legs.get(leg).duration);
        return getLengthDurationText(len, dur);
    }

    protected double distanceLLSquared(GeoPoint p1, GeoPoint p2) {
        double deltaLat = p2.latitudeE6 - p1.latitudeE6;
        double deltaLon = p2.longitudeE6 - p1.longitudeE6;
        return (deltaLat * deltaLat + deltaLon * deltaLon);
    }

    /**
     * As MapQuest and OSRM doesn't provide legs information, we have to rebuild
     * it, using the waypoints and the route nodes. <br>
     * Note that MapQuest legs fit well with waypoints, as there is a
     * "dedicated" node for each waypoint. But OSRM legs are not precise, as
     * there is no node "dedicated" to waypoints.
     *
     * @param waypoints ...
     */
    public void buildLegs(List<GeoPoint> waypoints) {
        legs = new ArrayList<RouteLeg>();
        int firstNodeIndex = 0;
        // For all intermediate waypoints, search the node closest to the
        // waypoint
        int w = waypoints.size();
        int n = nodes.size();
        for (int i = 1; i < w - 1; i++) {
            GeoPoint waypoint = waypoints.get(i);
            double distanceMin = -1.0;
            int nodeIndexMin = -1;
            for (int j = firstNodeIndex; j < n; j++) {
                GeoPoint routePoint = nodes.get(j).location;
                double dSquared = distanceLLSquared(routePoint, waypoint);
                if (nodeIndexMin == -1 || dSquared < distanceMin) {
                    distanceMin = dSquared;
                    nodeIndexMin = j;
                }
            }
            // Build the leg as ending with this closest node:
            RouteLeg leg = new RouteLeg(firstNodeIndex, nodeIndexMin, nodes);
            legs.add(leg);
            firstNodeIndex = nodeIndexMin + 1; // restart next leg from end
        }
        // Build last leg ending with last node:
        RouteLeg lastLeg = new RouteLeg(firstNodeIndex, n - 1, nodes);
        legs.add(lastLeg);
    }

    // --- Parcelable implementation

    //    @Override
    //    public int describeContents() {
    //        return 0;
    //    }
    //
    //    @Override
    //    public void writeToParcel(Parcel out, int flags) {
    //        out.writeInt(status);
    //        out.writeDouble(length);
    //        out.writeDouble(duration);
    //        out.writeList(nodes);
    //        out.writeList(legs);
    //        out.writeList(routeHigh);
    //        out.writeParcelable(boundingBox, 0);
    //    }
    //
    //    public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {
    //        @Override
    //        public Route createFromParcel(Parcel source) {
    //            return new Route(source);
    //        }
    //
    //        @Override
    //        public Route[] newArray(int size) {
    //            return new Route[size];
    //        }
    //    };
    //
    //    @SuppressWarnings("unchecked")
    //    private Route(Parcel in) {
    //        status = in.readInt();
    //        length = in.readDouble();
    //        duration = in.readDouble();
    //
    //        nodes = in.readArrayList(RouteNode.class.getClassLoader());
    //        legs = in.readArrayList(RouteLeg.class.getClassLoader());
    //        routeHigh = in.readArrayList(GeoPoint.class.getClassLoader());
    //        boundingBox = in.readParcelable(BoundingBox.class.getClassLoader());
    //    }
}
