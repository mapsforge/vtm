package org.osmdroid.location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.osmdroid.utils.BonusPackHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * POI Provider using Flickr service to get geolocalized photos.
 *
 * @author M.Kergall
 * @see "http://www.flickr.com/services/api/flickr.photos.search.html"
 */
public class FlickrPOIProvider implements POIProvider {

    private static final Logger log = Logger.getLogger(FlickrPOIProvider.class.getName());

    protected String mApiKey;
    private static final String PHOTO_URL = "http://www.flickr.com/photos/%s/%s/sizes/o/in/photostream/";

    /**
     * @param apiKey the registered API key to give to Flickr service.
     * @see "http://www.flickr.com/help/api/"
     */
    public FlickrPOIProvider(String apiKey) {
        mApiKey = apiKey;
    }

    private String getUrlInside(BoundingBox boundingBox, int maxResults) {
        StringBuffer url = new StringBuffer(
                "http://api.flickr.com/services/rest/?method=flickr.photos.search");
        url.append("&api_key=" + mApiKey);
        url.append("&bbox=" + boundingBox.getMinLongitude());
        url.append("," + boundingBox.getMinLatitude());
        url.append("," + boundingBox.getMaxLongitude());
        url.append("," + boundingBox.getMaxLatitude());
        url.append("&has_geo=1");
        // url.append("&geo_context=2");
        // url.append("&is_commons=true");
        url.append("&format=json&nojsoncallback=1");
        url.append("&per_page=" + maxResults);
        // From Flickr doc:
        // "Geo queries require some sort of limiting agent in order to prevent the database from crying."
        // And min_date_upload is considered as a limiting agent. So:
        url.append("&min_upload_date=2005/01/01");

        // Ask to provide some additional attributes we will need:
        url.append("&extras=geo,url_sq");
        url.append("&sort=interestingness-desc");
        return url.toString();
    }

    /* public POI getPhoto(String photoId){ String url =
     * "http://api.flickr.com/services/rest/?method=flickr.photos.getInfo"
     * +
     * "&api_key=" + mApiKey + "&photo_id=" + photo Id +
     * "&format=json&nojsoncallback=1"; log.debug( * "getPhoto:"+url); String
     * jString =
     * BonusPackHelper.requestStringFromUrl(url); if (jString == null)
     * {
     * log.error( * "FlickrPOIProvider: request failed.");
     * return null; } try { POI poi = new POI(POI.POI_SERVICE_FLICKR);
     * JSONObject jRoot = new JSONObject(jString); JSONObject jPhoto =
     * jRoot.getJSONObject("photo"); JSONObject jLocation =
     * jPhoto.getJSONObject("location"); poi.mLocation = new GeoPoint(
     * jLocation.getDouble("latitude"),
     * jLocation.getDouble("longitude"));
     * poi.mId = Long.parseLong(photoId); JSONObject jTitle =
     * jPhoto.getJSONObject("title"); poi.mType =
     * jTitle.getString("_content");
     * JSONObject jDescription = jPhoto.getJSONObject("description");
     * poi.mDescription = jDescription.getString("_content");
     * //truncate
     * description if too long: if (poi.mDescription.length() > 300){
     * poi.mDescription = poi.mDescription.substring(0, 300) +
     * " (...)"; }
     * String farm = jPhoto.getString("farm"); String server =
     * jPhoto.getString("server"); String secret =
     * jPhoto.getString("secret");
     * JSONObject jOwner = jPhoto.getJSONObject("owner"); String nsid
     * =
     * jOwner.getString("nsid"); poi.mThumbnailPath =
     * "http://farm"+farm+".staticflickr.com/"
     * +server+"/"+photoId+"_"+secret+"_s.jpg"; poi.mUrl =
     * "http://www.flickr.com/photos/"+nsid+"/"+photoId; return poi;
     * }catch
     * (JSONException e) { e.printStackTrace(); return null; } } */

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
            JSONObject jPhotos = jRoot.getJSONObject("photos");
            JSONArray jPhotoArray = jPhotos.getJSONArray("photo");
            int n = jPhotoArray.length();
            ArrayList<POI> pois = new ArrayList<POI>(n);
            for (int i = 0; i < n; i++) {
                JSONObject jPhoto = jPhotoArray.getJSONObject(i);

                String photoId = jPhoto.getString("id");
                if (mPrevious != null && mPrevious.containsKey(photoId))
                    continue;

                POI poi = new POI(POI.POI_SERVICE_FLICKR);
                poi.location = new GeoPoint(
                        jPhoto.getDouble("latitude"),
                        jPhoto.getDouble("longitude"));
                poi.id = photoId; //Long.parseLong(photoId);
                poi.type = jPhoto.getString("title");
                poi.thumbnailPath = jPhoto.getString("url_sq");
                String owner = jPhoto.getString("owner");
                // the default flickr link viewer doesnt work with mobile browsers...
                //    poi.url = "http://www.flickr.com/photos/" + owner + "/" + photoId + "/sizes/o/in/photostream/";

                poi.url = String.format(PHOTO_URL, owner, photoId);

                pois.add(poi);
            }
            //            int total = jPhotos.getInt("total");
            //            log.debug(on a total of:" + total);
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
        String url = getUrlInside(boundingBox, maxResults);
        return getThem(url);
    }

    HashMap<String, POI> mPrevious;

    public void setPrevious(HashMap<String, POI> previous) {
        mPrevious = previous;
    }

}
