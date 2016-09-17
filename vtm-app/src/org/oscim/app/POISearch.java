/*
 * Copyright 2012 osmdroid: M.Kergall
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
package org.oscim.app;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.oscim.android.canvas.AndroidGraphics;
import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.marker.MarkerSymbol.HotspotPlace;
import org.oscim.map.Map;
import org.osmdroid.location.FlickrPOIProvider;
import org.osmdroid.location.FourSquareProvider;
import org.osmdroid.location.GeoNamesPOIProvider;
import org.osmdroid.location.NominatimPOIProvider;
import org.osmdroid.location.POI;
import org.osmdroid.location.PicasaPOIProvider;
import org.osmdroid.overlays.DefaultInfoWindow;
import org.osmdroid.overlays.ExtendedMarkerItem;
import org.osmdroid.overlays.ItemizedOverlayWithBubble;

import java.util.ArrayList;
import java.util.List;

public class POISearch {
    private final ArrayList<POI> mPOIs;
    ItemizedOverlayWithBubble<ExtendedMarkerItem> poiMarkers;
    MarkerSymbol[] mMarkers;

    private final static int MDEFAULT = 0;
    private final static int MFLICKR = 1;
    private final static int MPICASA = 2;
    private final static int MWIKI16 = 3;
    private final static int MWIKI32 = 4;

    POISearch() {
        mPOIs = new ArrayList<POI>();
        //POI markers:
        final ArrayList<ExtendedMarkerItem> poiItems = new ArrayList<ExtendedMarkerItem>();

        poiMarkers = new ItemizedOverlayWithBubble<ExtendedMarkerItem>(App.map,
                App.activity,
                null,
                poiItems,
                new POIInfoWindow(App.map));

        App.map.layers().add(poiMarkers);

        mMarkers = new MarkerSymbol[5];

        mMarkers[MDEFAULT] = AndroidGraphics
                .makeMarker(App.res.getDrawable(R.drawable.pin), HotspotPlace.BOTTOM_CENTER);

        mMarkers[MFLICKR] = AndroidGraphics
                .makeMarker(App.res.getDrawable(R.drawable.marker_poi_flickr), null);

        mMarkers[MPICASA] = AndroidGraphics
                .makeMarker(App.res.getDrawable(R.drawable.marker_poi_picasa_24), null);

        mMarkers[MWIKI16] = AndroidGraphics
                .makeMarker(App.res.getDrawable(R.drawable.marker_poi_wikipedia_16), null);

        mMarkers[MWIKI32] = AndroidGraphics
                .makeMarker(App.res.getDrawable(R.drawable.marker_poi_wikipedia_32), null);
    }

    public List<POI> getPOIs() {
        return mPOIs;
    }

    final static String TAG_WIKIPEDIA = "wikipedia";
    final static String TAG_FLICKR = "flickr";
    final static String TAG_PICASA = "picasa";
    final static String TAG_FOURSQUARE = "foursquare";

    //private final static String TAG_NOMINATIM = "nominatim";

    class POITask extends AsyncTask<Object, Void, List<POI>> {
        String mTag;

        @Override
        protected List<POI> doInBackground(Object... params) {
            mTag = (String) params[0];

            if (mTag == null || mTag.equals("")) {
                return null;
            }
            BoundingBox bb = App.map.getBoundingBox(0);

            if (mTag.equals(TAG_WIKIPEDIA)) {
                GeoNamesPOIProvider poiProvider = new GeoNamesPOIProvider("mkergall");
                //ArrayList<POI> pois = poiProvider.getPOICloseTo(point, 30, 20.0);
                //Get POI inside the bounding box of the current map view:

                return poiProvider.getPOIInside(bb, 30);

                //OverpassPOIProvider poiProvider = new OverpassPOIProvider();
                //return poiProvider.getPOIInside(bb, "", 0);
            } else if (mTag.equals(TAG_FLICKR)) {
                FlickrPOIProvider poiProvider = new FlickrPOIProvider("c39be46304a6c6efda8bc066c185cd7e");
                return poiProvider.getPOIInside(bb, null, 20);
            } else if (mTag.startsWith(TAG_PICASA)) {
                PicasaPOIProvider poiProvider = new PicasaPOIProvider(null);
                String q = mTag.substring(7);
                return poiProvider.getPOIInside(bb, q, 20);
            } else if (mTag.startsWith(TAG_FOURSQUARE)) {
                FourSquareProvider poiProvider = new FourSquareProvider(null, null);
                String q = mTag.substring(10);
                //                String q = mTag.substring("picasa".length());
                return poiProvider.getPOIInside(bb, q, 40);
            } else {
                NominatimPOIProvider poiProvider = new NominatimPOIProvider();
                //poiProvider.setService(NominatimPOIProvider.NOMINATIM_POI_SERVICE);

                poiProvider.setService(NominatimPOIProvider.MAPQUEST_POI_SERVICE);

                //pois = poiProvider.getPOIAlong(mRoute.getRouteLow(), mTag, 100, 2.0);
                return poiProvider.getPOIInside(bb, mTag, 10);
            }
        }

        @Override
        protected void onPostExecute(List<POI> pois) {
            if (mTag.equals("")) {
                //no search, no message
            } else if (pois == null) {
                Toast.makeText(App.activity,
                        "Technical issue when getting " + mTag + " POI.",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(App.activity,
                        pois.size() + " " + mTag + " entries found",
                        Toast.LENGTH_SHORT).show();

                //    if (mTag.equals("flickr") || mTag.startsWith("picasa") || mTag.equals("wikipedia"))
                //    startAsyncThumbnailsLoading(mPOIs);
            }

            updateUIWithPOI(pois);
        }
    }

    void updateUIWithPOI(List<POI> pois) {
        mPOIs.clear();
        if (pois == null) {
            showPOIActivity(true);
            App.map.updateMap(true);
            return;
        }

        mPOIs.addAll(pois);

        for (POI poi : pois) {
            String desc = null;
            String name = null;

            if (poi.serviceId == POI.POI_SERVICE_NOMINATIM) {
                name = poi.description;
                String[] split = name.split(", ");
                if (split != null && split.length > 1) {
                    name = split[0];
                    desc = split[1];

                    for (int i = 2; i < 3 && i < split.length; i++)
                        desc += "," + split[i];
                }

            } else {
                desc = poi.description;
            }

            ExtendedMarkerItem poiMarker =
                    new ExtendedMarkerItem(poi.type + (name == null ? "" : ": " + name), desc,
                            poi.location);
            MarkerSymbol marker = null;

            if (poi.serviceId == POI.POI_SERVICE_NOMINATIM) {

                marker = mMarkers[MDEFAULT];
            } else if (poi.serviceId == POI.POI_SERVICE_GEONAMES_WIKIPEDIA) {
                if (poi.rank < 90)
                    marker = mMarkers[MWIKI16];
                else
                    marker = mMarkers[MWIKI32];
            } else if (poi.serviceId == POI.POI_SERVICE_FLICKR) {
                marker = mMarkers[MFLICKR];
            } else if (poi.serviceId == POI.POI_SERVICE_PICASA) {
                marker = mMarkers[MPICASA];
                poiMarker.setSubDescription(poi.category);
            } else if (poi.serviceId == POI.POI_SERVICE_4SQUARE) {
                marker = mMarkers[MDEFAULT];
                poiMarker.setSubDescription(poi.category);
            }

            poiMarker.setMarker(marker);
            //thumbnail loading moved in POIInfoWindow.onOpen for better performances.
            poiMarker.setRelatedObject(poi);
            poiMarkers.addItem(poiMarker);
        }

        showPOIActivity(true);
        App.map.updateMap(true);
    }

    private void showPOIActivity(boolean setNew) {
        // show or update
        Intent intent = new Intent(App.activity, POIActivity.class);
        intent.putExtra("ID", poiMarkers.getBubbledItemId());
        if (setNew)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        App.activity.startActivityForResult(intent, TileMap.POIS_REQUEST);
    }

    void getPOIAsync(String tag) {
        poiMarkers.removeAllItems();
        new POITask().execute(tag);
    }

    class POIInfoWindow extends DefaultInfoWindow {

        private Button mButton;
        private ImageView mImage;

        public POIInfoWindow(Map map) {
            super(R.layout.bonuspack_bubble, App.view);

            mButton = (Button) mView.findViewById(R.id.bubble_moreinfo);
            mImage = (ImageView) mView.findViewById(R.id.bubble_image);

            //bonuspack_bubble layouts already contain a "more info" button.
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    POI poi = (POI) view.getTag();

                    if (poi == null)
                        return;

                    if (poi.serviceId == POI.POI_SERVICE_4SQUARE) {
                        FourSquareProvider.browse(view.getContext(), poi);
                    } else if (poi.url != null) {
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(poi.url));
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        view.getContext().startActivity(i);
                    }
                }
            });

            getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    POI poi = (POI) view.getTag();

                    if (poi != null)
                        showPOIActivity(false);
                }
            });
        }

        @Override
        public void onOpen(ExtendedMarkerItem item) {
            POI poi = (POI) item.getRelatedObject();

            super.onOpen(item);

            poi.fetchThumbnail(mImage);

            //Show or hide "more info" button:
            if (poi.url != null)
                mButton.setVisibility(View.VISIBLE);
            else
                mButton.setVisibility(View.GONE);

            mButton.setTag(poi);
            getView().setTag(poi);
        }
    }

    public boolean onContextItemSelected(MenuItem item, GeoPoint geoPoint) {
        switch (item.getItemId()) {
            case R.id.menu_poi_nearby:
                Intent intent = new Intent(App.activity, POIActivity.class);
                intent.putExtra("ID", poiMarkers.getBubbledItemId());
                App.activity.startActivityForResult(intent, TileMap.POIS_REQUEST);
                return true;

            case R.id.menu_poi_clear:
                poiMarkers.removeAllItems();
                mPOIs.clear();
                App.map.updateMap(true);

                return true;
            default:
        }
        return false;

    }

}
