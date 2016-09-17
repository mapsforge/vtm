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

import android.app.Activity;
import android.location.Address;
import android.os.AsyncTask;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.oscim.android.canvas.AndroidGraphics;
import org.oscim.core.GeoPoint;
import org.oscim.layers.PathLayer;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.marker.MarkerSymbol.HotspotPlace;
import org.osmdroid.location.GeocoderNominatim;
import org.osmdroid.overlays.DefaultInfoWindow;
import org.osmdroid.overlays.ExtendedMarkerItem;
import org.osmdroid.overlays.ItemizedOverlayWithBubble;
import org.osmdroid.routing.Route;
import org.osmdroid.routing.RouteProvider;
import org.osmdroid.routing.provider.OSRMRouteProvider;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class RouteSearch {
    private static int START_INDEX = -2, DEST_INDEX = -1;

    private final PathLayer mRouteOverlay;
    //private final ItemizedOverlayWithBubble<ExtendedOverlayItem> mRouteMarkers;
    private final ItemizedOverlayWithBubble<ExtendedMarkerItem> mItineraryMarkers;

    private final RouteBar mRouteBar;

    private GeoPoint mStartPoint, mDestinationPoint;
    private final ArrayList<GeoPoint> mViaPoints;

    private ExtendedMarkerItem markerStart, markerDestination;

    private UpdateRouteTask mRouteTask;

    RouteSearch() {
        mViaPoints = new ArrayList<GeoPoint>();

        // Itinerary markers:
        ArrayList<ExtendedMarkerItem> waypointsItems = new ArrayList<ExtendedMarkerItem>();

        mItineraryMarkers = new ItemizedOverlayWithBubble<ExtendedMarkerItem>(App.map,
                App.activity,
                null,
                waypointsItems,
                new ViaPointInfoWindow(R.layout.itinerary_bubble));

        //updateIternaryMarkers();

        //Route and Directions
        //ArrayList<ExtendedOverlayItem> routeItems = new ArrayList<ExtendedOverlayItem>();
        //mRouteMarkers = new ItemizedOverlayWithBubble<ExtendedOverlayItem>(App.map, App.activity,
        //        null, routeItems);

        mRouteOverlay = new PathLayer(App.map, 0xAA0000FF, 3);

        // TODO use LayerGroup
        App.map.layers().add(mRouteOverlay);
        //App.map.getOverlays().add(mRouteMarkers);
        App.map.layers().add(mItineraryMarkers);

        mRouteBar = new RouteBar(App.activity);
    }

    /**
     * Retrieve route between p1 and p2 and update overlays.
     */
    public void showRoute(GeoPoint p1, GeoPoint p2) {
        clearOverlays();

        mStartPoint = p1;
        markerStart = putMarkerItem(markerStart, mStartPoint, START_INDEX,
                R.string.departure, R.drawable.marker_departure, -1);

        mDestinationPoint = p2;
        markerDestination = putMarkerItem(markerDestination, mDestinationPoint, DEST_INDEX,
                R.string.destination,
                R.drawable.marker_destination, -1);

        getRouteAsync();
    }

    /**
     * Reverse Geocoding
     */
    public String getAddress(GeoPoint p) {
        GeocoderNominatim geocoder = new GeocoderNominatim(App.activity);
        String theAddress;
        try {
            double dLatitude = p.getLatitude();
            double dLongitude = p.getLongitude();
            List<Address> addresses = geocoder.getFromLocation(dLatitude, dLongitude, 1);
            StringBuilder sb = new StringBuilder();
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                int n = address.getMaxAddressLineIndex();
                for (int i = 0; i <= n; i++) {
                    if (i != 0)
                        sb.append(", ");
                    sb.append(address.getAddressLine(i));
                }
                theAddress = new String(sb.toString());
            } else {
                theAddress = null;
            }
        } catch (IOException e) {
            theAddress = null;
        }
        if (theAddress != null) {
            return theAddress;
        }
        return "";
    }

    // Async task to reverse-geocode the marker position in a separate thread:
    class GeocodingTask extends AsyncTask<Object, Void, String> {
        ExtendedMarkerItem marker;

        @Override
        protected String doInBackground(Object... params) {
            marker = (ExtendedMarkerItem) params[0];
            return getAddress(marker.getPoint());
        }

        @Override
        protected void onPostExecute(String result) {
            marker.setDescription(result);
        }
    }

    /* add (or replace) an item in markerOverlays. p position. */
    public ExtendedMarkerItem putMarkerItem(ExtendedMarkerItem item, GeoPoint p, int index,
                                            int titleResId, int markerResId, int iconResId) {

        if (item != null)
            mItineraryMarkers.removeItem(item);

        MarkerSymbol marker = AndroidGraphics.makeMarker(App.res.getDrawable(markerResId),
                HotspotPlace.BOTTOM_CENTER);

        ExtendedMarkerItem overlayItem =
                new ExtendedMarkerItem(App.res.getString(titleResId), "", p);

        overlayItem.setMarker(marker);

        if (iconResId != -1)
            overlayItem.setImage(App.res.getDrawable(iconResId));

        overlayItem.setRelatedObject(Integer.valueOf(index));

        mItineraryMarkers.addItem(overlayItem);

        App.map.updateMap(true);

        //Start geocoding task to update the description of the marker with its address:
        new GeocodingTask().execute(overlayItem);

        return overlayItem;
    }

    public void addViaPoint(GeoPoint p) {
        mViaPoints.add(p);
        putMarkerItem(null, p, mViaPoints.size() - 1,
                R.string.viapoint, R.drawable.marker_via, -1);
    }

    public void removePoint(int index) {
        if (index == START_INDEX) {
            mStartPoint = null;
        } else if (index == DEST_INDEX) {
            mDestinationPoint = null;
        } else
            mViaPoints.remove(index);

        getRouteAsync();
        updateIternaryMarkers();
    }

    public void updateIternaryMarkers() {
        mItineraryMarkers.removeAllItems();

        //Start marker:
        if (mStartPoint != null) {
            markerStart = putMarkerItem(null, mStartPoint, START_INDEX,
                    R.string.departure, R.drawable.marker_departure, -1);
        }
        //Via-points markers if any:
        for (int index = 0; index < mViaPoints.size(); index++) {
            putMarkerItem(null, mViaPoints.get(index), index,
                    R.string.viapoint, R.drawable.marker_via, -1);
        }
        //Destination marker if any:
        if (mDestinationPoint != null) {
            markerDestination = putMarkerItem(null, mDestinationPoint, DEST_INDEX,
                    R.string.destination,
                    R.drawable.marker_destination, -1);
        }
    }

    //------------ Route and Directions
    private void updateOverlays(Route route) {
        //mRouteMarkers.removeAllItems();

        mRouteOverlay.clearPath();

        if (route == null || route.status == Route.STATUS_DEFAULT) {
            App.activity.showToastOnUiThread(App.res.getString(R.string.route_lookup_error));
            return;
        }

        mRouteOverlay.setPoints(route.routeHigh);

        //OverlayMarker marker = AndroidGraphics.makeMarker(App.res, R.drawable.marker_node, null);

        //int n = route.nodes.size();
        //for (int i = 0; i < n; i++) {
        //    RouteNode node = route.nodes.get(i);
        //    String instructions = (node.instructions == null ? "" : node.instructions);
        //    ExtendedOverlayItem nodeMarker = new ExtendedOverlayItem(
        //            "Step " + (i + 1), instructions, node.location);
        //
        //    nodeMarker.setSubDescription(route.getLengthDurationText(node.length, node.duration));
        //    nodeMarker.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
        //    nodeMarker.setMarker(marker);
        //
        //    mRouteMarkers.addItem(nodeMarker);
        //}

        App.map.updateMap(true);
    }

    void clearOverlays() {
        //mRouteMarkers.removeAllItems(true);
        mItineraryMarkers.removeAllItems(true);

        mRouteOverlay.clearPath();
        mStartPoint = null;
        mDestinationPoint = null;
        mViaPoints.clear();

        App.map.updateMap(true);
    }

    /**
     * Async task to get the route in a separate thread.
     */
    class UpdateRouteTask extends AsyncTask<List<GeoPoint>, Void, Route> {
        @Override
        protected Route doInBackground(List<GeoPoint>... wp) {
            List<GeoPoint> waypoints = wp[0];

            //RouteProvider routeProvider = new MapQuestRouteProvider();
            //Locale locale = Locale.getDefault();
            //routeProvider.addRequestOption("locale=" + locale.getLanguage() + "_"
            //        + locale.getCountry());
            //routeProvider.addRequestOption("routeType=pedestrian");

            //RouteProvider routeProvider = new GoogleRouteProvider();
            RouteProvider routeProvider = new OSRMRouteProvider();
            return routeProvider.getRoute(waypoints);
        }

        @Override
        protected void onPostExecute(Route result) {

            updateOverlays(result);
            mRouteBar.set(result);

            mRouteTask = null;
        }
    }

    @SuppressWarnings("unchecked")
    public void getRouteAsync() {
        if (mRouteTask != null) {
            mRouteTask.cancel(true);
            mRouteTask = null;
        }

        if (mStartPoint == null || mDestinationPoint == null) {
            mRouteOverlay.clearPath();
            return;
        }

        List<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(mStartPoint);
        //add intermediate via points:
        for (GeoPoint p : mViaPoints) {
            waypoints.add(p);
        }
        waypoints.add(mDestinationPoint);

        mRouteTask = new UpdateRouteTask();
        mRouteTask.execute(waypoints);
    }

    boolean onContextItemSelected(MenuItem item, GeoPoint geoPoint) {
        switch (item.getItemId()) {
            case R.id.menu_route_departure:
                mStartPoint = geoPoint;

                markerStart = putMarkerItem(markerStart, mStartPoint, START_INDEX,
                        R.string.departure, R.drawable.marker_departure, -1);

                getRouteAsync();
                return true;

            case R.id.menu_route_destination:
                mDestinationPoint = geoPoint;

                markerDestination = putMarkerItem(markerDestination, mDestinationPoint, DEST_INDEX,
                        R.string.destination,
                        R.drawable.marker_destination, -1);

                getRouteAsync();
                return true;

            case R.id.menu_route_viapoint:
                GeoPoint viaPoint = geoPoint;
                addViaPoint(viaPoint);

                getRouteAsync();
                return true;

            case R.id.menu_route_clear:
                clearOverlays();
                return true;

            default:
        }
        return false;
    }

    public boolean isEmpty() {
        return (mItineraryMarkers.size() == 0);
    }

    class ViaPointInfoWindow extends DefaultInfoWindow {

        int mSelectedPoint;

        public ViaPointInfoWindow(int layoutResId) {
            super(layoutResId, App.view);

            Button btnDelete = (Button) (mView.findViewById(R.id.bubble_delete));
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removePoint(mSelectedPoint);
                    close();
                }
            });
        }

        @Override
        public void onOpen(ExtendedMarkerItem item) {
            mSelectedPoint = ((Integer) item.getRelatedObject()).intValue();
            super.onOpen(item);
        }
    }

    class RouteBar {

        TextView mDistance = null;
        TextView mRouteLength = null;
        TextView mTravelTime = null;
        ImageView mClearButton = null;
        RelativeLayout mRouteBarView = null;

        RouteBar(Activity activity) {

            mRouteBarView = (RelativeLayout) activity.findViewById(R.id.route_bar);
            mDistance = (TextView) activity.findViewById(R.id.route_bar_distance);
            mRouteLength = (TextView) activity.findViewById(R.id.route_bar_route_length);
            mTravelTime = (TextView) activity.findViewById(R.id.route_bar_travel_time);

            mClearButton = (ImageView) activity.findViewById(R.id.route_bar_clear);

            mRouteBarView.setVisibility(View.INVISIBLE);

            mClearButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRouteBarView.setVisibility(View.INVISIBLE);
                    clearOverlays();
                }
            });
        }

        public void set(Route result) {
            DecimalFormat twoDForm = new DecimalFormat("#.#");
            DecimalFormat oneDForm = new DecimalFormat("#");
            int hour = ((int) result.duration / 3600);
            int minute = ((int) result.duration % 3600) / 60;
            String time = "";
            if (hour == 0 && minute == 0) {
                time = "?";
            } else if (hour == 0 && minute != 0) {
                time = minute + "m";
            } else {
                time = hour + "h " + minute + "m";
            }

            double dis = ((double) (mStartPoint.distanceTo(mDestinationPoint))) / 1000;
            String distance;
            String shortpath;
            if (dis < 100) {
                distance = twoDForm.format(dis);
            } else {
                distance = oneDForm.format(dis);
            }
            if (result.length == 0) {
                shortpath = "?";
            } else if (result.length < 100) {
                shortpath = twoDForm.format(result.length);
            } else {
                shortpath = oneDForm.format(result.length);
            }

            mRouteBarView.setVisibility(View.VISIBLE);
            mDistance.setText(distance + " km");
            mTravelTime.setText(time);
            mRouteLength.setText(shortpath + " km");
        }
    }
}
