package org.osmdroid.location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.osmdroid.utils.BonusPackHelper;
import org.osmdroid.utils.HttpConnection;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * POI Provider using GeoNames services. Currently, "find Nearby Wikipedia" and
 * "Wikipedia Articles in Bounding Box" services.
 *
 * @author M.Kergall
 * @see "http://www.geonames.org"
 */
public class GeoNamesPOIProvider {

    private static final Logger log = Logger.getLogger(GeoNamesPOIProvider.class.getName());

    protected String mUserName;

    /**
     * @param account the registered "username" to give to GeoNames service.
     * @see "http://www.geonames.org/login"
     */
    public GeoNamesPOIProvider(String account) {
        mUserName = account;
    }

    private String getUrlCloseTo(GeoPoint p, int maxResults, double maxDistance) {
        StringBuffer url = new StringBuffer("http://api.geonames.org/findNearbyWikipediaJSON?");
        url.append("lat=" + p.getLatitude());
        url.append("&lng=" + p.getLongitude());
        url.append("&maxRows=" + maxResults);
        url.append("&radius=" + maxDistance); //km
        url.append("&lang=" + Locale.getDefault().getLanguage());
        url.append("&username=" + mUserName);
        return url.toString();
    }

    private String getUrlInside(BoundingBox boundingBox, int maxResults) {
        StringBuffer url = new StringBuffer("http://api.geonames.org/wikipediaBoundingBoxJSON?");
        url.append("south=" + boundingBox.getMinLatitude());
        url.append("&north=" + boundingBox.getMaxLatitude());
        url.append("&west=" + boundingBox.getMinLongitude());
        url.append("&east=" + boundingBox.getMaxLongitude());
        url.append("&maxRows=" + maxResults);
        url.append("&lang=" + Locale.getDefault().getLanguage());
        url.append("&username=" + mUserName);
        return url.toString();
    }

    /**
     * @param fullUrl ...
     * @return the list of POI
     */
    public ArrayList<POI> getThem(String fullUrl) {
        log.fine("GeoNamesPOIProvider:get:" + fullUrl);
        String jString = BonusPackHelper.requestStringFromUrl(fullUrl);
        if (jString == null) {
            log.severe("GeoNamesPOIProvider: request failed.");
            return null;
        }
        try {
            JSONObject jRoot = new JSONObject(jString);
            JSONArray jPlaceIds = jRoot.getJSONArray("geonames");
            int n = jPlaceIds.length();
            ArrayList<POI> pois = new ArrayList<POI>(n);
            for (int i = 0; i < n; i++) {
                JSONObject jPlace = jPlaceIds.getJSONObject(i);
                POI poi = new POI(POI.POI_SERVICE_GEONAMES_WIKIPEDIA);
                poi.location = new GeoPoint(jPlace.getDouble("lat"),
                        jPlace.getDouble("lng"));
                poi.category = jPlace.optString("feature");
                poi.type = jPlace.getString("title");
                poi.description = jPlace.optString("summary");
                poi.thumbnailPath = jPlace.optString("thumbnailImg", null);
                /* This makes loading too long. Thumbnail loading will be done
                 * only when needed, with POI.getThumbnail() if
                 * (poi.mThumbnailPath != null){ poi.mThumbnail =
                 * BonusPackHelper.loadBitmap(poi.mThumbnailPath); } */
                poi.url = jPlace.optString("wikipediaUrl", null);
                if (poi.url != null)
                    poi.url = "http://" + poi.url;
                poi.rank = jPlace.optInt("rank", 0);
                //other attributes: distance?
                pois.add(poi);
            }
            log.fine("done");
            return pois;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    //XML parsing seems 2 times slower than JSON parsing
    public ArrayList<POI> getThemXML(String fullUrl) {
        log.fine("GeoNamesPOIProvider:get:" + fullUrl);
        HttpConnection connection = new HttpConnection();
        connection.doGet(fullUrl);
        InputStream stream = connection.getStream();
        if (stream == null) {
            return null;
        }
        GeoNamesXMLHandler handler = new GeoNamesXMLHandler();
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(stream, handler);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.close();
        log.fine("done");
        return handler.mPOIs;
    }

    /**
     * @param position    ...
     * @param maxResults  ...
     * @param maxDistance ... in km. 20 km max for the free service.
     * @return list of POI, Wikipedia entries close to the position. Null if
     * technical issue.
     */
    public ArrayList<POI> getPOICloseTo(GeoPoint position,
                                        int maxResults, double maxDistance) {
        String url = getUrlCloseTo(position, maxResults, maxDistance);
        return getThem(url);
    }

    /**
     * @param boundingBox ...
     * @param maxResults  ...
     * @return list of POI, Wikipedia entries inside the bounding box. Null if
     * technical issue.
     */
    public ArrayList<POI> getPOIInside(BoundingBox boundingBox, int maxResults) {
        String url = getUrlInside(boundingBox, maxResults);
        return getThem(url);
    }
}

class GeoNamesXMLHandler extends DefaultHandler {

    private String mString;
    double mLat, mLng;
    POI mPOI;
    ArrayList<POI> mPOIs;

    public GeoNamesXMLHandler() {
        mPOIs = new ArrayList<POI>();
    }

    @Override
    public void startElement(String uri, String localName, String name,
                             Attributes attributes) {
        if (localName.equals("entry")) {
            mPOI = new POI(POI.POI_SERVICE_GEONAMES_WIKIPEDIA);
        }
        mString = new String();
    }

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
        } else if (localName.equals("feature")) {
            mPOI.category = mString;
        } else if (localName.equals("title")) {
            mPOI.type = mString;
        } else if (localName.equals("summary")) {
            mPOI.description = mString;
        } else if (localName.equals("thumbnailImg")) {
            if (mString != null && !mString.equals(""))
                mPOI.thumbnailPath = mString;
        } else if (localName.equals("wikipediaUrl")) {
            if (mString != null && !mString.equals(""))
                mPOI.url = "http://" + mString;
        } else if (localName.equals("rank")) {
            mPOI.rank = Integer.parseInt(mString);
        } else if (localName.equals("entry")) {
            mPOI.location = new GeoPoint(mLat, mLng);
            mPOIs.add(mPOI);
        }
    }

}
