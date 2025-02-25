/*
 * Copyright 2013 Hannes Janetzek
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
package org.oscim.web.client;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.http.client.URL;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.oscim.core.BoundingBox;
import org.oscim.core.GeometryBuffer;
import org.oscim.core.MapPosition;
import org.oscim.layers.PathLayer;
import org.oscim.map.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SearchBox {

    private static final Logger log = Logger.getLogger(SearchBox.class.getName());

    private static final String NOMINATIM_GLOBAL = "http://nominatim.openstreetmap.org/search?polygon_text=1&addressdetails=0&format=json&limit=25&q=";

    interface PoiData {
        public static final ProvidesKey<PoiData> KEY_PROVIDER = new ProvidesKey<PoiData>() {
            @Override
            public Object getKey(PoiData item) {
                return item == null ? null : item.getId();
            }
        };

        String getId();

        String getName();

        double getLatitude();

        double getLongitude();

        String getIcon();

        BoundingBox getBoundingBox();
    }

    static final class NominatimData extends JavaScriptObject implements
            PoiData {

        protected NominatimData() {
        }

        static final class BBox extends JsArrayNumber {
            protected BBox() {
            }
        }

        static final class Polygon extends JsArray<JsArrayNumber> {
            protected Polygon() {
            }

        }

        @Override
        public final native String getId()
        /*-{
            return this.osm_id;
        }-*/;

        public final native String name() /*-{
            return this.display_name;
        }-*/;

        public final native BBox getBBox() /*-{
            return this.boundingbox
        }-*/;

        public final native String getWkt() /*-{
            return this.geotext;
        }-*/;

        private final native String latitude() /*-{
            return this.lat;
        }-*/;

        private final native String longitude() /*-{
            return this.lon;
        }-*/;

        @Override
        public final native String getIcon() /*-{
            return this.icon;
        }-*/;

        @Override
        public double getLatitude() {
            try {
                return Double.parseDouble(latitude());
            } catch (Exception e) {

            }
            return 0;
        }

        @Override
        public double getLongitude() {
            try {
                return Double.parseDouble(longitude());
            } catch (Exception e) {
            }
            return 0;
        }

        @Override
        public BoundingBox getBoundingBox() {
            if (getBBox() != null) {
                BBox b = getBBox();
                return new BoundingBox(b.get(0), b.get(2), b.get(1), b.get(3));
            }
            return null;
        }

        @Override
        public String getName() {
            String[] n = name().split(", ");
            if (n != null && n.length > 2)
                return n[0] + ", " + n[1] + " " + n[2];
            else if (n != null && n.length > 1)
                return n[0] + ", " + n[1];

            return name();
        }
    }

    class PoiCell extends AbstractCell<PoiData> {

        @Override
        public void render(com.google.gwt.cell.client.Cell.Context context,
                           PoiData value, SafeHtmlBuilder sb) {

            // Value can be null, so do a null check..
            if (value == null) {
                return;
            }

            sb.appendHtmlConstant("<table>");

            if (value.getIcon() != null) {
                // Add the contact image.
                sb.appendHtmlConstant("<tr><td rowspan='3'>");
                sb.appendHtmlConstant("<img border=0 src=" + value.getIcon() + ">");
                sb.appendHtmlConstant("</td>");
            }

            // Add the name and address.
            sb.appendHtmlConstant("<td style='font-size:95%;'>");
            sb.appendEscaped(value.getName());
            sb.appendHtmlConstant("</td></tr>");
            //sb.appendEscaped("<tr><td>" + value.getId()+ "</td></tr>");
            sb.appendHtmlConstant("</table>");
            sb.appendHtmlConstant("<hline>");
        }
    }

    public SearchBox(final Map map) {
        final Button searchButton = new Button("Search");
        final TextBox searchField = new TextBox();
        //searchField.setText("Bremen");
        final PathLayer mOverlay = new PathLayer(map, 0xCC0000FF);
        map.layers().add(mOverlay);

        // We can add style names to widgets
        searchButton.addStyleName("sendButton");

        RootPanel.get("nameFieldContainer").add(searchField);
        RootPanel.get("sendButtonContainer").add(searchButton);

        // Focus the cursor on the name field when the app loads
        searchField.setFocus(true);
        searchField.selectAll();

        // Create a cell to render each value in the list.
        PoiCell poiCell = new PoiCell();

        // Create a CellList that uses the cell.
        final CellList<PoiData> cellList = new CellList<PoiData>(poiCell,
                PoiData.KEY_PROVIDER);

        final SingleSelectionModel<PoiData> selectionModel = new SingleSelectionModel<PoiData>(
                PoiData.KEY_PROVIDER);
        cellList.setSelectionModel(selectionModel);

        final ScrollPanel scroller = new ScrollPanel(cellList);
        RootPanel.get("listContainer").add(scroller);

        scroller.setSize("350px", "300px");

        RootPanel.get("search").getElement().getStyle().setVisibility(Visibility.VISIBLE);
        scroller.setVisible(false);

        searchField.addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                scroller.setVisible(true);
            }
        });
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                final PoiData d = selectionModel.getSelectedObject();

                mOverlay.clearPath();

                //log.debug("selected " + d.getName() + " " + d.getLatitude() + " "
                //        + d.getLongitude());

                BoundingBox b = d.getBoundingBox();
                if (b != null) {
                    if (b.maxLatitudeE6 - b.minLatitudeE6 < 100 &&
                            b.maxLongitudeE6 - b.minLongitudeE6 < 100)
                        // for small bbox use zoom=16 to get an overview
                        map.animator().animateTo(500, b.getCenterPoint(), 1 << 16, false);
                    else
                        map.animator().animateTo(b);
                    if (d instanceof NominatimData && ((NominatimData) d).getWkt() != null) {
                        String wkt = ((NominatimData) d).getWkt();

                        WKTReader r = new WKTReader();
                        GeometryBuffer g = new GeometryBuffer(1024, 10);
                        try {
                            r.parse(wkt, g);
                        } catch (Exception e) {
                            log.fine(wkt);
                        }
                        //FIXME mOverlay.setGeom(g);

                        //log.debug("add polygon " + p.length());
                    } else {
                        mOverlay.addPoint(b.maxLatitudeE6, b.minLongitudeE6);
                        mOverlay.addPoint(b.maxLatitudeE6, b.maxLongitudeE6);
                        mOverlay.addPoint(b.minLatitudeE6, b.maxLongitudeE6);
                        mOverlay.addPoint(b.minLatitudeE6, b.minLongitudeE6);
                        mOverlay.addPoint(b.maxLatitudeE6, b.minLongitudeE6);
                    }
                    // hide overlay after 5 seconds
                    map.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mOverlay.clearPath();
                        }
                    }, 5000);
                } else {
                    MapPosition pos = new MapPosition();

                    map.viewport().setTilt(0);
                    map.viewport().setRotation(0);

                    pos.setZoomLevel(13);
                    pos.setPosition(d.getLatitude(), d.getLongitude());
                    map.setMapPosition(pos);
                }

                scroller.setVisible(false);
            }

        });

        // Create a handler for the sendButton and nameField
        class MyHandler implements ClickHandler, KeyUpHandler {

            /**
             * Fired when the user clicks on the sendButton.
             */
            @Override
            public void onClick(ClickEvent event) {
                sendRequest();
            }

            /**
             * Fired when the user types in the nameField.
             */
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    sendRequest();
                }
            }

            /**
             * Send the name from the nameField to the server and wait for a
             * response.
             */
            private void sendRequest() {
                String textToServer = searchField.getText();
                searchButton.setEnabled(false);

                String url = URL
                        .encode(NOMINATIM_GLOBAL
                                + textToServer);

                JsonpRequestBuilder builder = new JsonpRequestBuilder();
                builder.setCallbackParam("json_callback");
                builder.requestObject(url, new AsyncCallback<JsArray<NominatimData>>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        log.fine("request failed");
                        searchButton.setEnabled(true);
                    }

                    @Override
                    public void onSuccess(JsArray<NominatimData> data) {
                        List<PoiData> items = new ArrayList<PoiData>();
                        for (int i = 0, n = data.length(); i < n; i++) {
                            NominatimData d = data.get(i);
                            items.add(d);
                        }

                        cellList.setRowCount(items.size(), true);
                        cellList.setRowData(0, items);
                        scroller.setVisible(true);
                        searchButton.setEnabled(true);
                    }
                });
            }
        }

        // Add a handler to send the name to the server
        MyHandler handler = new MyHandler();
        searchButton.addClickHandler(handler);
        searchField.addKeyUpHandler(handler);

    }

}
