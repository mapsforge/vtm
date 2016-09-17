package org.osmdroid.overlays;

import android.graphics.drawable.Drawable;

import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.map.Map;

/**
 * An OverlayItem to use in ItemizedOverlayWithBubble<br>
 * - more complete: can contain an image and a sub-description that will be
 * displayed in the bubble, <br>
 * - and flexible: attributes are modifiable<br>
 * Known Issues:<br>
 * - Bubble offset is not perfect on h&xhdpi resolutions, due to an osmdroid
 * issue on marker drawing<br>
 * - Bubble offset is at 0 when using the default marker => set the marker on
 * each item!<br>
 *
 * @author M.Kergall
 * @see ItemizedOverlayWithBubble
 */
public class ExtendedMarkerItem extends MarkerItem {
    // a third field that can be displayed in
    // the infowindow, on a third line
    // that will be shown in the infowindow.
    //unfortunately, this is not so simple...
    private String mSubDescription;
    private Drawable mImage;
    private Object mRelatedObject; // reference to an object (of any kind)
    // linked to this item.

    public ExtendedMarkerItem(String aTitle, String aDescription, GeoPoint aGeoPoint) {
        super(aTitle, aDescription, aGeoPoint);
        mSubDescription = null;
        mImage = null;
        mRelatedObject = null;
    }

    public void setTitle(String aTitle) {
        title = aTitle;
    }

    public void setDescription(String aDescription) {
        description = aDescription;
    }

    public void setSubDescription(String aSubDescription) {
        mSubDescription = aSubDescription;
    }

    public void setImage(Drawable anImage) {
        mImage = anImage;
    }

    public void setRelatedObject(Object o) {
        mRelatedObject = o;
    }

    public String getDescription() {
        return description;
    }

    public String getSubDescription() {
        return mSubDescription;
    }

    public Drawable getImage() {
        return mImage;
    }

    public Object getRelatedObject() {
        return mRelatedObject;
    }

    /**
     * Populates this bubble with all item info:
     * <ul>
     * title and description in any case,
     * </ul>
     * <ul>
     * image and sub-description if any.
     * </ul>
     * and centers the map on the item. <br>
     *
     * @param bubble ...
     * @param map    ...
     */
    public void showBubble(InfoWindow bubble, Map map) {
        // offset the bubble to be top-centered on the marker:
        //        Drawable marker = getMarker(0 /* OverlayItem.ITEM_STATE_FOCUSED_MASK */);
        //        int markerWidth = 0, markerHeight = 0;
        //        if (marker != null) {
        //            markerWidth = marker.getIntrinsicWidth();
        //            markerHeight = marker.getIntrinsicHeight();
        //        } // else... we don't have the default marker size => don't user default
        //            // markers!!!
        //        Point markerH = getHotspot(getMarkerHotspot(), markerWidth, markerHeight);
        //        Point bubbleH = getHotspot(HotspotPlace.TOP_CENTER, markerWidth, markerHeight);
        //        bubbleH.offset(-markerH.x, -markerH.y);
        //
        //        bubble.open(this, bubbleH.x, bubbleH.y);
        //        OverlayMarker marker = getMarker();
        //        PointF hotspot = marker.getHotspot();
        //        Bitmap b = marker.getBitmap();

        //bubble.open(this, (int)(-b.getWidth() * hotspot.x), (int)(-b.getHeight()));
        //bubble.open(this, 0, (int)(b.getHeight()));

        bubble.open(this, 0, 0);
    }
}
