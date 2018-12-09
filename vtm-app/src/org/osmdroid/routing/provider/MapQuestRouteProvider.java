package org.osmdroid.routing.provider;

import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.osmdroid.routing.Route;
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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * class to get a route between a start and a destination point, going through a
 * list of waypoints. It uses MapQuest open, public and free API, based on
 * OpenStreetMap data. <br>
 * See http://open.mapquestapi.com/guidance
 *
 * @author M.Kergall
 */
public class MapQuestRouteProvider extends RouteProvider {

    static final Logger log = LoggerFactory.getLogger(MapQuestRouteProvider.class);

    static final String MAPQUEST_GUIDANCE_SERVICE = "http://open.mapquestapi.com/guidance/v0/route?";

    /**
     * Build the URL to MapQuest service returning a route in XML format
     *
     * @param waypoints : array of waypoints, as [lat, lng], from start point to end
     *                  point.
     * @return ...
     */
    protected String getUrl(List<GeoPoint> waypoints) {
        StringBuffer urlString = new StringBuffer(MAPQUEST_GUIDANCE_SERVICE);
        urlString.append("from=");
        GeoPoint p = waypoints.get(0);
        urlString.append(geoPointAsString(p));

        for (int i = 1; i < waypoints.size(); i++) {
            p = waypoints.get(i);
            urlString.append("&to=" + geoPointAsString(p));
        }

        urlString.append("&outFormat=xml");
        urlString.append("&shapeFormat=cmp"); // encoded polyline, much faster

        urlString.append("&narrativeType=text"); // or "none"
        // Locale locale = Locale.getDefault();
        // urlString.append("&locale="+locale.getLanguage()+"_"+locale.getCountry());

        urlString.append("&unit=k&fishbone=false");

        // urlString.append("&generalizeAfter=500" /*+&generalize=2"*/);
        // 500 points max, 2 meters tolerance

        // Warning: MapQuest Open API doc is sometimes WRONG:
        // - use unit, not units
        // - use fishbone, not enableFishbone
        // - locale (fr_FR, en_US) is supported but not documented.
        // - generalize and generalizeAfter are not properly implemented
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
        log.debug("MapQuestRouteManager.getRoute:" + url);
        Route route = null;
        HttpConnection connection = new HttpConnection();
        connection.doGet(url);
        InputStream stream = connection.getStream();
        if (stream != null)
            route = getRouteXML(stream, waypoints);
        if (route == null || route.routeHigh.size() == 0) {
            // Create default route:
            route = new Route(waypoints);
        }
        connection.close();
        log.debug("MapQuestRouteManager.getRoute - finished");
        return route;
    }

    /**
     * XML implementation
     *
     * @param is        : input stream to parse
     * @param waypoints ...
     * @return the route ...
     */
    protected Route getRouteXML(InputStream is, List<GeoPoint> waypoints) {
        XMLHandler handler = new XMLHandler();
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
        Route route = handler.mRoute;
        if (route != null && route.routeHigh.size() > 0) {
            route.nodes = finalizeNodes(route.nodes, handler.mLinks, route.routeHigh);
            route.buildLegs(waypoints);
            route.status = Route.STATUS_OK;
        }
        return route;
    }

    protected List<RouteNode> finalizeNodes(List<RouteNode> mNodes,
                                            List<RouteLink> mLinks, List<GeoPoint> polyline) {
        int n = mNodes.size();
        if (n == 0)
            return mNodes;
        ArrayList<RouteNode> newNodes = new ArrayList<RouteNode>(n);
        RouteNode lastNode = null;
        for (int i = 1; i < n - 1; i++) { // 1, n-1 => first and last MapQuest
            // nodes are irrelevant.
            RouteNode node = mNodes.get(i);
            RouteLink link = mLinks.get(node.nextRouteLink);
            if (lastNode != null && (node.instructions == null || node.maneuverType == 0)) {
                // this node is irrelevant, don't keep it,
                // but update values of last node:
                lastNode.length += link.mLength;
                lastNode.duration += (node.duration + link.mDuration);
            } else {
                node.length = link.mLength;
                node.duration += link.mDuration;
                int locationIndex = link.mShapeIndex;
                node.location = polyline.get(locationIndex);
                newNodes.add(node);
                lastNode = node;
            }
        }
        // switch to the new array of nodes:
        return newNodes;
    }
}

/**
 * Route Link is a portion of route between 2 "nodes" or intersections
 */
class RouteLink {
    /**
     * in km/h
     */
    public double mSpeed;
    /**
     * in km
     */
    public double mLength;
    /**
     * in sec
     */
    public double mDuration;
    /**
     * starting point of the link, as index in initial polyline
     */
    public int mShapeIndex;
}

/**
 * XMLHandler: class to handle XML generated by MapQuest "guidance" open API.
 */
class XMLHandler extends DefaultHandler {
    public Route mRoute;
    public ArrayList<RouteLink> mLinks;

    boolean isBB;
    boolean isGuidanceNodeCollection;
    private String mString;
    double mLat, mLng;
    double mNorth, mWest, mSouth, mEast;
    RouteLink mLink;
    RouteNode mNode;

    public XMLHandler() {
        isBB = isGuidanceNodeCollection = false;
        mRoute = new Route();
        mLinks = new ArrayList<RouteLink>();
    }

    @Override
    public void startElement(String uri, String localName, String name,
                             Attributes attributes) {
        if (localName.equals("boundingBox"))
            isBB = true;
        else if (localName.equals("link"))
            mLink = new RouteLink();
        else if (localName.equals("node"))
            mNode = new RouteNode();
        else if (localName.equals("GuidanceNodeCollection"))
            isGuidanceNodeCollection = true;
        mString = new String();
    }

    /**
     * Overrides org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char[] ch, int start, int length) {
        String chars = new String(ch, start, length);
        mString = mString.concat(chars);
    }

    @Override
    public void endElement(String uri, String localName, String name) {
        if (localName.equals("lat")) {
            mLat = Double.parseDouble(mString);
        } else if (localName.equals("lng")) {
            mLng = Double.parseDouble(mString);
        } else if (localName.equals("shapePoints")) {
            mRoute.routeHigh = PolylineEncoder.decode(mString, 10);
            // log.debug("High="+mRoute.mRouteHigh.size());
        } else if (localName.equals("generalizedShape")) {
            mRoute.setRouteLow(PolylineEncoder.decode(mString, 10));
            // log.debug("Low="+mRoute.mRouteLow.size());
        } else if (localName.equals("length")) {
            mLink.mLength = Double.parseDouble(mString);
        } else if (localName.equals("speed")) {
            mLink.mSpeed = Double.parseDouble(mString);
        } else if (localName.equals("shapeIndex")) {
            mLink.mShapeIndex = Integer.parseInt(mString);
        } else if (localName.equals("link")) {
            // End of a link: update route attributes:
            // GuidanceLinkCollection could in theory contain additional unused
            // links,
            // but normally not with fishbone set to false.
            mLink.mDuration = mLink.mLength / mLink.mSpeed * 3600.0;
            mLinks.add(mLink);
            mRoute.length += mLink.mLength;
            mRoute.duration += mLink.mDuration;
            mLink = null;
        } else if (localName.equals("turnCost")) {
            int turnCost = Integer.parseInt(mString);
            mNode.duration += turnCost;
            mRoute.duration += turnCost;
        } else if (localName.equals("maneuverType")) {
            mNode.maneuverType = Integer.parseInt(mString);
        } else if (localName.equals("info")) {
            if (isGuidanceNodeCollection) {
                if (mNode.instructions == null)
                    // this is first "info" value for this node, keep it:
                    mNode.instructions = mString;
            }
        } else if (localName.equals("linkId")) {
            if (isGuidanceNodeCollection)
                mNode.nextRouteLink = Integer.parseInt(mString);
        } else if (localName.equals("node")) {
            mRoute.nodes.add(mNode);
            mNode = null;
        } else if (localName.equals("GuidanceNodeCollection")) {
            isGuidanceNodeCollection = false;
        } else if (localName.equals("ul")) {
            if (isBB) {
                mNorth = mLat;
                mWest = mLng;
            }
        } else if (localName.equals("lr")) {
            if (isBB) {
                mSouth = mLat;
                mEast = mLng;
            }
        } else if (localName.equals("boundingBox")) {
            mRoute.boundingBox = new BoundingBox(mNorth, mEast, mSouth, mWest);
            isBB = false;
        }
    }
}
