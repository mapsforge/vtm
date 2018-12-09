package org.osmdroid.routing.provider;

import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.osmdroid.routing.Route;
import org.osmdroid.routing.RouteLeg;
import org.osmdroid.routing.RouteNode;
import org.osmdroid.routing.RouteProvider;
import org.osmdroid.utils.HttpConnection;
import org.osmdroid.utils.PolylineEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * class to get a route between a start and a destination point, going through a
 * list of waypoints. <br>
 * https://developers.google.com/maps/documentation/directions/<br>
 * Note that displaying a route provided by Google on a non-Google map (like
 * OSM) is not allowed by Google T&C.
 *
 * @author M.Kergall
 */
public class GoogleRouteProvider extends RouteProvider {

    static final Logger log = LoggerFactory.getLogger(GoogleRouteProvider.class);

    static final String GOOGLE_DIRECTIONS_SERVICE = "http://maps.googleapis.com/maps/api/directions/xml?";

    /**
     * Build the URL to Google Directions service returning a route in XML
     * format
     *
     * @param waypoints ...
     * @return ...
     */
    protected String getUrl(List<GeoPoint> waypoints) {
        StringBuffer urlString = new StringBuffer(GOOGLE_DIRECTIONS_SERVICE);
        urlString.append("origin=");
        GeoPoint p = waypoints.get(0);
        urlString.append(geoPointAsString(p));
        urlString.append("&destination=");
        int destinationIndex = waypoints.size() - 1;
        p = waypoints.get(destinationIndex);
        urlString.append(geoPointAsString(p));

        for (int i = 1; i < destinationIndex; i++) {
            if (i == 1)
                urlString.append("&waypoints=");
            else
                urlString.append("%7C"); // the pipe (|), url-encoded
            p = waypoints.get(i);
            urlString.append(geoPointAsString(p));
        }
        urlString.append("&units=metric&sensor=false");
        Locale locale = Locale.getDefault();
        urlString.append("&language=" + locale.getLanguage());
        urlString.append(mOptions);
        return urlString.toString();
    }

    /**
     * @param waypoints : list of GeoPoints. Must have at least 2 entries, start and
     *                  end points.
     * @return the route
     */
    @Override
    public Route getRoute(List<GeoPoint> waypoints) {
        String url = getUrl(waypoints);
        log.debug("GoogleRouteManager.getRoute:" + url);
        Route route = null;
        HttpConnection connection = new HttpConnection();
        connection.doGet(url);
        InputStream stream = connection.getStream();
        if (stream != null)
            route = getRouteXML(stream);
        connection.close();
        if (route == null || route.routeHigh.size() == 0) {
            //Create default route:
            route = new Route(waypoints);
        } else {
            //finalize route data update:
            for (RouteLeg leg : route.legs) {
                route.duration += leg.duration;
                route.length += leg.length;
            }
            route.status = Route.STATUS_OK;
        }
        log.debug("GoogleRouteManager.getRoute - finished");
        return route;
    }

    protected Route getRouteXML(InputStream is) {
        GoogleDirectionsHandler handler = new GoogleDirectionsHandler();
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(is, handler);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return handler.mRoute;
    }

}

class GoogleDirectionsHandler extends DefaultHandler {
    Route mRoute;
    RouteLeg mLeg;
    RouteNode mNode;
    boolean isPolyline, isOverviewPolyline, isLeg, isStep, isDuration, isDistance, isBB;
    int mValue;
    double mLat, mLng;
    double mNorth, mWest, mSouth, mEast;
    private String mString;

    public GoogleDirectionsHandler() {
        isOverviewPolyline = isBB = isPolyline = isLeg = isStep = isDuration = isDistance = false;
        mRoute = new Route();
    }

    @Override
    public void startElement(String uri, String localName, String name,
                             Attributes attributes) {
        if (localName.equals("polyline")) {
            isPolyline = true;
        } else if (localName.equals("overview_polyline")) {
            isOverviewPolyline = true;
        } else if (localName.equals("leg")) {
            mLeg = new RouteLeg();
            isLeg = true;
        } else if (localName.equals("step")) {
            mNode = new RouteNode();
            isStep = true;
        } else if (localName.equals("duration")) {
            isDuration = true;
        } else if (localName.equals("distance")) {
            isDistance = true;
        } else if (localName.equals("bounds")) {
            isBB = true;
        }
        mString = new String();
    }

    /**
     * Overrides org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    public
    @Override
    void characters(char[] ch, int start, int length) {
        String chars = new String(ch, start, length);
        mString = mString.concat(chars);
    }

    @Override
    public void endElement(String uri, String localName, String name) {
        if (localName.equals("points")) {
            if (isPolyline) {
                //detailed piece of route for the step, to add:
                ArrayList<GeoPoint> polyLine = PolylineEncoder.decode(mString, 10);
                mRoute.routeHigh.addAll(polyLine);
            } else if (isOverviewPolyline) {
                //low-def polyline for the whole route:
                mRoute.setRouteLow(PolylineEncoder.decode(mString, 10));
            }
        } else if (localName.equals("polyline")) {
            isPolyline = false;
        } else if (localName.equals("overview_polyline")) {
            isOverviewPolyline = false;
        } else if (localName.equals("value")) {
            mValue = Integer.parseInt(mString);
        } else if (localName.equals("duration")) {
            if (isStep)
                mNode.duration = mValue;
            else
                mLeg.duration = mValue;
            isDuration = false;
        } else if (localName.equals("distance")) {
            if (isStep)
                mNode.length = mValue / 1000.0;
            else
                mLeg.length = mValue / 1000.0;
            isDistance = false;
        } else if (localName.equals("html_instructions")) {
            if (isStep) {
                mString = mString.replaceAll("<[^>]*>", " "); //remove everything in <...>
                mString = mString.replaceAll("&nbsp;", " ");
                mNode.instructions = mString;
                //log.debug(mString);
            }
        } else if (localName.equals("start_location")) {
            if (isStep)
                mNode.location = new GeoPoint(mLat, mLng);
        } else if (localName.equals("step")) {
            mRoute.nodes.add(mNode);
            isStep = false;
        } else if (localName.equals("leg")) {
            mRoute.legs.add(mLeg);
            isLeg = false;
        } else if (localName.equals("lat")) {
            mLat = Double.parseDouble(mString);
        } else if (localName.equals("lng")) {
            mLng = Double.parseDouble(mString);
        } else if (localName.equals("northeast")) {
            if (isBB) {
                mNorth = mLat;
                mEast = mLng;
            }
        } else if (localName.equals("southwest")) {
            if (isBB) {
                mSouth = mLat;
                mWest = mLng;
            }
        } else if (localName.equals("bounds")) {
            mRoute.boundingBox = new BoundingBox(mNorth, mEast, mSouth, mWest);
            isBB = false;
        }
    }

}
