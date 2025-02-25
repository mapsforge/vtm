/*
 * Copyright 2012 Hannes Janetzek
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
package org.osmdroid.location;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.osmdroid.utils.BonusPackHelper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Logger;

public class FourSquareProvider implements POIProvider {

    private static final Logger log = Logger.getLogger(FourSquareProvider.class.getName());

    //    https://developer.foursquare.com/docs/venues/search
    //    https://developer.foursquare.com/docs/responses/venue
    //    https://apigee.com/console/foursquare

    protected String mApiKey;

    //    private static HashMap<String, Bitmap> mIcons =
    //            (HashMap<String,Bitmap>)Collections.synchronizedMap(new HashMap<String, Bitmap>());

    /**
     * @param clientSecret the registered API key to give to Flickr service.
     * @see "http://www.flickr.com/help/api/"
     */
    public FourSquareProvider(String clientId, String clientSecret) {
        mApiKey = "client_id=" + clientId + "&client_secret=" + clientSecret;
    }

    //"https://api.foursquare.com/v2/venues/search?v=20120321&intent=checkin&ll=53.06,8.8&client_id=ZUN4ZMNZUFT3Z5QQZNMQ3ACPL4OJMBFGO15TYX51D5MHCIL3&client_secret=X1RXCVF4VVSG1Y2FUDQJLKQUC1WF4XXKIMK2STXKACLPDGLY
    @SuppressWarnings("deprecation")
    private String getUrlInside(BoundingBox boundingBox, String query, int maxResults) {
        StringBuffer url = new StringBuffer(
                "https://api.foursquare.com/v2/venues/search?v=20120321"
                        + "&intent=browse"
                        + "&client_id=ZUN4ZMNZUFT3Z5QQZNMQ3ACPL4OJMBFGO15TYX51D5MHCIL3"
                        + "&client_secret=X1RXCVF4VVSG1Y2FUDQJLKQUC1WF4XXKIMK2STXKACLPDGLY");
        url.append("&sw=");
        url.append(boundingBox.getMinLatitude());
        url.append(',');
        url.append(boundingBox.getMinLongitude());
        url.append("&ne=");
        url.append(boundingBox.getMaxLatitude());
        url.append(',');
        url.append(boundingBox.getMaxLongitude());
        url.append("&limit=");
        url.append(maxResults);
        if (query != null)
            url.append("&query=" + URLEncoder.encode(query));
        return url.toString();
    }

    /**
     * @param fullUrl ...
     * @return the list of POI
     */
    public ArrayList<POI> getThem(String fullUrl) {
        // for local debug: fullUrl = "http://10.0.2.2/flickr_mockup.json";
        log.fine("FlickrPOIProvider:get:" + fullUrl);
        String jString = BonusPackHelper.requestStringFromUrl(fullUrl);
        if (jString == null) {
            log.severe("FlickrPOIProvider: request failed.");
            return null;
        }
        try {
            JSONObject jRoot = new JSONObject(jString);

            JSONObject jResponse = jRoot.getJSONObject("response");
            JSONArray jVenueArray = jResponse.getJSONArray("venues");
            int n = jVenueArray.length();
            ArrayList<POI> pois = new ArrayList<POI>(n);
            for (int i = 0; i < n; i++) {
                JSONObject jVenue = jVenueArray.getJSONObject(i);

                POI poi = new POI(POI.POI_SERVICE_4SQUARE);
                poi.id = jVenue.getString("id");
                poi.type = jVenue.getString("name");
                //                poi.url = jVenue.optString("url", null);
                poi.url = "https://foursquare.com/v/" + poi.id;

                JSONObject jLocation = jVenue.getJSONObject("location");
                poi.location = new GeoPoint(
                        jLocation.getDouble("lat"),
                        jLocation.getDouble("lng"));
                poi.description = jLocation.optString("address", null);

                JSONArray jCategories = jVenue.getJSONArray("categories");
                if (jCategories.length() > 0) {
                    JSONObject jCategory = jCategories.getJSONObject(0);
                    String icon = jCategory.getJSONObject("icon").getString("prefix");
                    poi.thumbnailPath = icon + 44 + ".png";
                    poi.category = jCategory.optString("name");
                }
                pois.add(poi);
            }

            return pois;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param boundingBox ...
     * @param maxResults  ...
     * @return list of POI, Flickr photos inside the bounding box.
     * Null if
     * technical issue.
     */
    @Override
    public ArrayList<POI> getPOIInside(BoundingBox boundingBox, String query, int maxResults) {
        String url = getUrlInside(boundingBox, query, maxResults);
        return getThem(url);
    }

    public static void browse(final Context context, POI poi) {
        // get the right url from redirect, could also parse the result from querying venueid...
        new AsyncTask<POI, Void, String>() {

            @Override
            protected String doInBackground(POI... params) {
                POI poi = params[0];
                if (poi == null)
                    return null;
                try {
                    URL url = new URL(poi.url);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setInstanceFollowRedirects(false);

                    String redirect = conn.getHeaderField("Location");
                    if (redirect != null) {
                        log.fine(redirect);
                        return redirect;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result == null)
                    return;

                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://foursquare.com"
                        + result));
                context.startActivity(myIntent);

            }
        }.execute(poi);

    }
}
