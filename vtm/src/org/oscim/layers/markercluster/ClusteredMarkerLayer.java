/*
 * Copyright 2012 osmdroid authors:
 * Copyright 2012 Nicolas Gramlich
 * Copyright 2012 Theodore Hong
 * Copyright 2012 Fred Eisele
 *
 * Copyright 2013 Hannes Janetzek
 * Copyright 2017 Rodolfo Lopez Pintor
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

package org.oscim.layers.markercluster;

import org.oscim.core.Point;
import org.oscim.layers.Layer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.map.Map;

import javax.annotation.Nullable;

/**
 * MarkerLayer:
 *
 * Draws a list of {@link MarkerItem} as markers to a map. The item with the
 * lowest index is drawn as last and therefore the 'topmost' marker. It also
 * gets checked for onTap first. This class is generic, because you then you get
 * your custom item-class passed back in onTap(). << TODO
 *
 * ClusteredMarkerLayer:
 * A copy of MarkerLayer with clustering support. Basically added a parameter to the constructor that is passed to the renderer
 */

public abstract class ClusteredMarkerLayer<Item extends ClusteredMarkerItem> extends Layer {

    protected final ClusteredMarkerRenderer mMarkerRenderer;
    protected Item mFocusedItem;

    /**
     * Method by which subclasses create the actual Items. This will only be
     * called from populate() we'll cache them for later use.
     */
    protected abstract Item createItem(int i);

    /**
     * The number of items in this overlay.
     */
    public abstract int size();

    /**
     * Constructs a clustered marker layer with default icon cluster style
     *
     * @param map           The map
     * @param defaultSymbol Symbol to use if item has no marker
     */

    @SuppressWarnings("unchecked")
    public ClusteredMarkerLayer(Map map, MarkerSymbol defaultSymbol) {
        this(map, defaultSymbol, new ClusteredMarkerRenderer.ClusterStyle(0xff000000, 0xffffffff));
    }

    /**
     * Constructs a clustered marker layer
     *
     * @param map           The map
     * @param defaultSymbol Symbol to use if item has no marker
     * @param clusterStyle  ClusterStyle. If NULL, clustering is turned off.
     */

    public ClusteredMarkerLayer(Map map, MarkerSymbol defaultSymbol, @Nullable ClusteredMarkerRenderer.ClusterStyle clusterStyle) {
        super(map);
        mMarkerRenderer = new ClusteredMarkerRenderer((ClusteredMarkerLayer<ClusteredMarkerItem>) this, defaultSymbol, clusterStyle);
        mRenderer = mMarkerRenderer;
    }

    /**
     * Constructs a clustered marker layer creating its rendererer from a render factory
     *
     * @param map     The map
     * @param factory The render factory
     */

    public ClusteredMarkerLayer(Map map, ClusteredMarkerRendererFactory factory) {
        super(map);
        mMarkerRenderer = factory.create(this);
        mRenderer = mMarkerRenderer;
    }

    /**
     * Utility method to perform all processing on a new ItemizedOverlay.
     * Subclasses provide Items through the createItem(int) method. The subclass
     * should call this as soon as it has data, before anything else gets
     * called.
     */
    protected final void populate() {
        mMarkerRenderer.populate(size());
    }

    /**
     * TODO
     * If the given Item is found in the overlay, force it to be the current
     * focus-bearer. Any registered {link ItemizedLayer#OnFocusChangeListener}
     * will be notified. This does not move the map, so if the Item isn't
     * already centered, the user may get confused. If the Item is not found,
     * this is a no-op. You can also pass null to remove focus.
     *
     * @param item
     */
    public void setFocus(Item item) {
        mFocusedItem = item;
    }

    /**
     * @return the currently-focused item, or null if no item is currently
     * focused.
     */
    public Item getFocus() {
        return mFocusedItem;
    }

    /**
     * TODO
     * Interface definition for overlays that contain items that can be snapped
     * to (for example, when the user invokes a zoom, this could be called
     * allowing the user to snap the zoom to an interesting point.)
     */
    public interface Snappable {

        /**
         * Checks to see if the given x and y are close enough to an item
         * resulting in snapping the current action (e.g. zoom) to the item.
         *
         * @param x         The x in screen coordinates.
         * @param y         The y in screen coordinates.
         * @param snapPoint To be filled with the the interesting point (in screen
         *                  coordinates) that is closest to the given x and y. Can be
         *                  untouched if not snapping.
         * @return Whether or not to snap to the interesting point.
         */
        boolean onSnapToItem(int x, int y, Point snapPoint);
    }
}
