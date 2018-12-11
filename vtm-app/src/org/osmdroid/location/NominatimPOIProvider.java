package org.osmdroid.location;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.osmdroid.utils.BonusPackHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * POI Provider using Nominatim service. <br>
 * See https://wiki.openstreetmap.org/wiki/Nominatim<br>
 * and http://open.mapquestapi.com/nominatim/<br>
 *
 * @author M.Kergall
 */
public class NominatimPOIProvider implements POIProvider {

    static final Logger log = LoggerFactory.getLogger(NominatimPOIProvider.class);

    /* As the doc lacks a lot of features, source code may help:
     * https://trac.openstreetmap
     * .org/browser/applications/utils/nominatim/website/search.php featuretype=
     * to select on feature type (country, city, state, settlement)<br>
     * format=jsonv2 to get a place_rank<br> offset= to offset the result ?...
     * <br> polygon=1 to get the border of the poi as a polygon<br> nearlat &
     * nearlon = ???<br> routewidth/69 and routewidth/30 ???<br> */
    public static final String MAPQUEST_POI_SERVICE = "http://open.mapquestapi.com/nominatim/v1/";
    public static final String NOMINATIM_POI_SERVICE = "http://nominatim.openstreetmap.org/";
    protected String mService;

    public NominatimPOIProvider() {
        mService = NOMINATIM_POI_SERVICE;
    }

    public void setService(String serviceUrl) {
        mService = serviceUrl;
    }

    @SuppressWarnings("deprecation")
    private StringBuffer getCommonUrl(String type, int maxResults) {
        StringBuffer urlString = new StringBuffer(mService);
        urlString.append("search?");
        urlString.append("format=json");
        urlString.append("&q=" + URLEncoder.encode(type));
        urlString.append("&limit=" + maxResults);
        //urlString.append("&bounded=1");
        //        urlString.append("&addressdetails=0");
        return urlString;
    }

    private String getUrlInside(BoundingBox bb, String type, int maxResults) {
        StringBuffer urlString = getCommonUrl(type, maxResults);
        urlString.append("&viewbox=" + bb.getMaxLongitude() + ","
                + bb.getMaxLatitude() + ","
                + bb.getMinLongitude() + ","
                + bb.getMinLatitude());
        return urlString.toString();
    }

    private String getUrlCloseTo(GeoPoint p, String type,
                                 int maxResults, double maxDistance) {
        int maxD = (int) (maxDistance * 1E6);
        BoundingBox bb = new BoundingBox(p.latitudeE6 + maxD,
                p.longitudeE6 + maxD,
                p.latitudeE6 - maxD,
                p.longitudeE6 - maxD);
        return getUrlInside(bb, type, maxResults);
    }

    /**
     * @param url full URL request
     * @return the list of POI, of null if technical issue.
     */
    public ArrayList<POI> getThem(String url) {
        log.debug("NominatimPOIProvider:get:" + url);
        String jString = BonusPackHelper.requestStringFromUrl(url);
        if (jString == null) {
            log.error("NominatimPOIProvider: request failed.");
            return null;
        }
        try {
            JSONArray jPlaceIds = new JSONArray(jString);
            int n = jPlaceIds.length();
            ArrayList<POI> pois = new ArrayList<POI>(n);
            Bitmap thumbnail = null;
            for (int i = 0; i < n; i++) {
                JSONObject jPlace = jPlaceIds.getJSONObject(i);
                POI poi = new POI(POI.POI_SERVICE_NOMINATIM);
                poi.id = jPlace.getString("osm_id");
                //    jPlace.optLong("osm_id");
                poi.location = new GeoPoint(jPlace.getDouble("lat"), jPlace.getDouble("lon"));
                JSONArray bbox = jPlace.optJSONArray("boundingbox");
                if (bbox != null) {
                    try {
                        poi.bbox = new BoundingBox(bbox.getDouble(0), bbox.getDouble(2),
                                bbox.getDouble(1), bbox.getDouble(3));
                    } catch (Exception e) {
                        log.debug("could not parse " + bbox);
                    }
                    //log.debug("bbox " + poi.bbox);
                }
                poi.category = jPlace.optString("class");
                poi.type = jPlace.getString("type");
                poi.description = jPlace.optString("display_name");
                poi.thumbnailPath = jPlace.optString("icon", null);

                if (i == 0 && poi.thumbnailPath != null) {
                    //first POI, and we have a thumbnail: load it
                    thumbnail = BonusPackHelper.loadBitmap(poi.thumbnailPath);
                }
                poi.thumbnail = thumbnail;
                pois.add(poi);
            }
            return pois;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param position    ...
     * @param type        an OpenStreetMap feature. See
     *                    http://wiki.openstreetmap.org/wiki/Map_Features or
     *                    http://code.google.com/p/osmbonuspack/source/browse/trunk/
     *                    OSMBonusPackDemo/res/values/poi_tags.xml
     * @param maxResults  the maximum number of POI returned. Note that in any case,
     *                    Nominatim will have an absolute maximum of 100.
     * @param maxDistance to the position, in degrees. Note that it is used to build a
     *                    bounding box around the position, not a circle.
     * @return the list of POI, null if technical issue.
     */
    public ArrayList<POI> getPOICloseTo(GeoPoint position, String type,
                                        int maxResults, double maxDistance) {
        String url = getUrlCloseTo(position, type, maxResults, maxDistance);
        return getThem(url);
    }

    /**
     * @param boundingBox ...
     * @param type        OpenStreetMap feature
     * @param maxResults  ...
     * @return list of POIs, null if technical issue.
     */
    @Override
    public ArrayList<POI> getPOIInside(BoundingBox boundingBox, String type, int maxResults) {
        String url = getUrlInside(boundingBox, type, maxResults);
        return getThem(url);
    }

    public ArrayList<POI> getPOI(String query, int maxResults) {
        String url = getCommonUrl(query, maxResults).toString();
        return getThem(url);
    }

    /**
     * @param path       Warning: a long path may cause a failure due to the url to be
     *                   too long. Using a simplified route may help (see
     *                   Road.getRouteLow()).
     * @param type       OpenStreetMap feature
     * @param maxResults ...
     * @param maxWidth   to the path. Certainly not in degrees. Probably in km.
     * @return list of POIs, null if technical issue.
     */
    public ArrayList<POI> getPOIAlong(ArrayList<GeoPoint> path, String type,
                                      int maxResults, double maxWidth) {
        StringBuffer urlString = getCommonUrl(type, maxResults);
        urlString.append("&routewidth=" + maxWidth);
        urlString.append("&route=");
        boolean isFirst = true;
        for (GeoPoint p : path) {
            if (isFirst)
                isFirst = false;
            else
                urlString.append(",");
            String lat = Double.toString(p.getLatitude());
            lat = lat.substring(0, Math.min(lat.length(), 7));
            String lon = Double.toString(p.getLongitude());
            lon = lon.substring(0, Math.min(lon.length(), 7));
            urlString.append(lat + "," + lon);
            //limit the size of url as much as possible, as post method is not supported.
        }
        return getThem(urlString.toString());
    }
}
