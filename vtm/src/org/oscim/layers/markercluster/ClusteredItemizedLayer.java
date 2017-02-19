/*
 * Copyright 2012 osmdroid authors: Nicolas Gramlich, Theodore Hong, Fred Eisele
 *
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 devemux86
 * Copyright 2016 Stephan Leuschner
 * Copyright 2016 Pedinel
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

import org.oscim.core.Box;
import org.oscim.core.Point;
import org.oscim.event.Event;
import org.oscim.event.Gesture;
import org.oscim.event.GestureListener;
import org.oscim.event.MotionEvent;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.map.Map;
import org.oscim.map.Viewport;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * This is a copy of ItemizedLayer, with (my humble attempt to add) support for clustering and
 * "dynamic" items (that change their position).
 *
 * Also allows a trivial implementation of drag and drop in the parent.
 *
 *         Modifications to ItemizedLayer:
 *
 *         FOR ITEM CLUSTERING:
 *
 *         - added a new parameter "useCluster" to the constructor to
 *         enable Clustering for this layer's items.
 *
 *         - The beef is on ClusteredMarkerRenderer, the rest of classes are merely
 *         copies of ItemizedLayer**** equivalents with little tweaks here and there
 *
 *         TO ALLOW MOVING MARKERS AROUND
 *
 *         - Added method "notifyPositionsChanged" to be called when  item(s) position(s)
 *         are changed externally. It will detect changed items, reproject
 *         then update the map. For example, a game engine could move 40 markers, then
 *         call notifyPositionsChanged() to update all items at once
 *
 *         - Layer implements inputListener to send back the raw touches ACTIONUP, ACTIONDOWN
 *         so drag and drop can trivially be implemented on the listener.
 *
 *         - Extended OnItemGestureListener with a new method onRawEvent that gets raw touches
 *         (ACTION_UP/MOVE/DOWN) so drag and drop can be trivially implemented
 */

public class ClusteredItemizedLayer<Item extends ClusteredMarkerItem> extends ClusteredMarkerLayer<Item> implements Map.InputListener, GestureListener {

    private final static int DEFAULT_CLUSTER_BACKGROUND = 0xffffffff, DEFAULT_CLUSTER_FOREGROUND = 0xff000000;

    protected final List<Item> mItemList;
    protected final Point mTmpPoint = new Point();
    protected OnItemGestureListener<Item> mOnItemGestureListener;
    protected int mDrawnItemsLimit = Integer.MAX_VALUE;
    protected ClusteredMarkerRendererFactory mRenderFactory;

    /**
     * Constructs a clustered itemizedlayer with default cluster icon style
     *
     * @param map           The Map
     * @param defaultMarker The Default Marker
     */

    public ClusteredItemizedLayer(Map map, MarkerSymbol defaultMarker) {
        this(map, defaultMarker, new ClusteredMarkerRenderer.ClusterStyle(DEFAULT_CLUSTER_FOREGROUND, DEFAULT_CLUSTER_BACKGROUND));
    }

    /**
     * Constructs a clustered itemizedlayer with custom cluster icon style
     *
     * @param map           The Map
     * @param defaultMarker The Default Marker
     * @param style         The cluster icon style
     */

    public ClusteredItemizedLayer(Map map, MarkerSymbol defaultMarker, ClusteredMarkerRenderer.ClusterStyle style) {
        this(map, new ArrayList<Item>(), defaultMarker, style, null);
    }

    /**
     * Constructs a ClusteredItemizedLayer with default cluster icon style
     *
     * @param map           The Map
     * @param list          Items list
     * @param defaultMarker Defaultmarker to be used if a item has marker==null
     * @param listener      Listener for Tap, Long-Tap
     */

    public ClusteredItemizedLayer(Map map,
                                  List<Item> list,
                                  MarkerSymbol defaultMarker,
                                  OnItemGestureListener<Item> listener) {

        this(map, list, defaultMarker, new ClusteredMarkerRenderer.ClusterStyle(DEFAULT_CLUSTER_FOREGROUND, DEFAULT_CLUSTER_BACKGROUND), listener);

    }

    /**
     * Constructs a ClusteredItemizedLayer with optional clustering capabilities
     *
     * @param map           The Map
     * @param list          Items list
     * @param defaultMarker Default marker to be used if a item has marker==null
     * @param clusterStyle  Style for the cluster icons. If NULL, clustering will be turned off.
     * @param listener      Listener for Tap, Long-Tap
     */

    public ClusteredItemizedLayer(Map map,
                                  List<Item> list,
                                  MarkerSymbol defaultMarker,
                                  @Nullable ClusteredMarkerRenderer.ClusterStyle clusterStyle,
                                  OnItemGestureListener<Item> listener) {

        super(map, defaultMarker, clusterStyle);

        mItemList = list;
        mOnItemGestureListener = listener;
        populate();
    }


    /**
     * The cluster functionality could have been implemented on ItemizedLayer with this factory pattern if MarkerRenderer
     * extended an interface so ClusteredMarkerRenderer could implement it as well. Mind if you use this function, you
     * should set the render style yourself to the created renderer!
     */

    public ClusteredItemizedLayer(Map map, List<Item> list, ClusteredMarkerRendererFactory factory, OnItemGestureListener<Item> listener) {
        super(map, factory);

        mItemList = list;
        mOnItemGestureListener = listener;
        mRenderFactory = factory;
        populate();
    }

    public void setOnItemGestureListener(OnItemGestureListener<Item> listener) {
        mOnItemGestureListener = listener;
    }

    @Override
    protected Item createItem(int index) {
        return mItemList.get(index);
    }

    @Override
    public int size() {
        return Math.min(mItemList.size(), mDrawnItemsLimit);
    }

    public boolean addItem(Item item) {
        final boolean result = mItemList.add(item);
        populate();
        return result;
    }

    public void addItem(int location, Item item) {
        mItemList.add(location, item);
    }

    public boolean addItems(List<Item> items) {
        final boolean result = mItemList.addAll(items);
        populate();
        return result;
    }

    public void removeAllItems() {
        removeAllItems(true);
    }

    public void removeAllItems(boolean withPopulate) {
        mItemList.clear();
        if (withPopulate) {
            populate();
        }
    }

    public boolean removeItem(Item item) {
        final boolean result = mItemList.remove(item);
        populate();
        return result;
    }

    public Item removeItem(int position) {
        final Item result = mItemList.remove(position);
        populate();
        return result;
    }

    /**
     * Each of these methods performs a item sensitive check. If the item is
     * located its corresponding method is called. The result of the call is
     * returned. Helper methods are provided so that child classes may more
     * easily override behavior without resorting to overriding the
     * ItemGestureListener methods.
     */
    //	@Override
    //	public boolean onTap(MotionEvent event, MapPosition pos) {
    //		return activateSelectedItems(event, mActiveItemSingleTap);
    //	}
    protected boolean onSingleTapUpHelper(int index, Item item) {
        return mOnItemGestureListener.onItemSingleTapUp(index, item);
    }


    private final ActiveItem mActiveItemSingleTap = new ActiveItem() {
        @Override
        public boolean run(int index) {
            final ClusteredItemizedLayer<Item> that = ClusteredItemizedLayer.this;
            if (mOnItemGestureListener == null) {
                return false;
            }
            return onSingleTapUpHelper(index, that.mItemList.get(index));
        }
    };

    protected boolean onLongPressHelper(int index, Item item) {
        return this.mOnItemGestureListener.onItemLongPress(index, item);
    }

    private final ActiveItem mActiveItemLongPress = new ActiveItem() {
        @Override
        public boolean run(final int index) {
            final ClusteredItemizedLayer<Item> that = ClusteredItemizedLayer.this;
            if (that.mOnItemGestureListener == null) {
                return false;
            }
            return onLongPressHelper(index, that.mItemList.get(index));
        }
    };

    /**
     * When a content sensitive action is performed the content item needs to be
     * identified. This method does that and then performs the assigned task on
     * that item.
     *
     * @return true if event is handled false otherwise
     */
    protected boolean activateSelectedItems(MotionEvent event, ActiveItem task) {
        int size = mItemList.size();
        if (size == 0)
            return false;

        int eventX = (int) event.getX() - mMap.getWidth() / 2;
        int eventY = (int) event.getY() - mMap.getHeight() / 2;
        Viewport mapPosition = mMap.viewport();

        Box bbox = mapPosition.getBBox(null, 128);
        bbox.map2mercator();
        bbox.scale(1E6);

        int nearest = -1;
        int inside = -1;
        double insideY = -Double.MAX_VALUE;

		/* squared dist: 50*50 pixel ~ 2mm on 400dpi */
        double dist = 2500;

        for (int i = 0; i < size; i++) {
            Item item = mItemList.get(i);

            if (!bbox.contains(item.getPoint().longitudeE6,
                    item.getPoint().latitudeE6))
                continue;


            mapPosition.toScreenPoint(item.getPoint(), mTmpPoint);

            float dx = (float) (mTmpPoint.x - eventX);
            float dy = (float) (mTmpPoint.y - eventY);

            MarkerSymbol it = item.getMarker();
            if (it == null)
                it = mMarkerRenderer.mDefaultMarker;

            if (it.isInside(dx, dy)) {
                if (mTmpPoint.y > insideY) {
                    insideY = mTmpPoint.y;
                    inside = i;
                }
            }
            if (inside >= 0)
                continue;

            double d = dx * dx + dy * dy;
            if (d > dist)
                continue;

            dist = d;
            nearest = i;
        }

        if (inside >= 0)
            nearest = inside;

        if (nearest >= 0 && task.run(nearest)) {
            mMarkerRenderer.update();
            mMap.render();
            return true;
        }
        return false;
    }

    /**
     * When the item is touched one of these methods may be invoked depending on
     * the type of touch. Each of them returns true if the event was completely
     * handled.
     */
    public interface OnItemGestureListener<T> {

        boolean onItemSingleTapUp(int index, T item);

        boolean onItemLongPress(int index, T item);

        /** Added raw touch message to implement drag n drop */
        void onRawEvent(MotionEvent motionEvent);
    }

    public interface ActiveItem {
        boolean run(int aIndex);
    }

    @Override
    public void onInputEvent(Event e, MotionEvent motionEvent) {
        if (mOnItemGestureListener != null)
            mOnItemGestureListener.onRawEvent(motionEvent);
    }


    @Override
    public boolean onGesture(Gesture g, MotionEvent e) {

        if (g instanceof Gesture.Tap)
            return activateSelectedItems(e, mActiveItemSingleTap);

        if (g instanceof Gesture.LongPress)
            return activateSelectedItems(e, mActiveItemLongPress);

        return false;
    }


    public Item getByUid(Object uid) {
        for (Item it : mItemList)
            if (it.getUid() == uid)
                return it;

        return null;
    }


    //// NEW: repopulate to update position changes

    /**
     * Updates position changes: Recomputes changed items projections and calls update()
     */

    public final void repopulate() {
        mMarkerRenderer.notifyPositionsChanged();
    }

}
