/*
 * Copyright 2012 osmdroid authors: Nicolas Gramlich, Theodore Hong, Fred Eisele
 *
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2020 devemux86
 * Copyright 2016 Stephan Leuschner
 * Copyright 2016 Pedinel
 * Copyright 2019 Carlos Alberto Martínez Gadea
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
package org.oscim.layers.marker;

import org.oscim.backend.CanvasAdapter;
import org.oscim.core.Box;
import org.oscim.core.GeoPoint;
import org.oscim.core.Point;
import org.oscim.core.Tile;
import org.oscim.event.Gesture;
import org.oscim.event.GestureListener;
import org.oscim.event.MotionEvent;
import org.oscim.map.Map;
import org.oscim.map.Viewport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemizedLayer extends MarkerLayer implements GestureListener {

    protected final List<MarkerInterface> mItemList;
    protected final Point mTmpPoint = new Point();
    protected OnItemGestureListener<MarkerInterface> mOnItemGestureListener;
    protected int mDrawnItemsLimit = Integer.MAX_VALUE;
    private MarkerItem dragItem;

    public ItemizedLayer(Map map, MarkerSymbol defaultMarker) {
        this(map, new ArrayList<MarkerInterface>(), defaultMarker, null);
    }

    public ItemizedLayer(Map map, List<MarkerInterface> list,
                         MarkerSymbol defaultMarker,
                         OnItemGestureListener<MarkerInterface> listener) {

        super(map, defaultMarker);

        mItemList = list;
        mOnItemGestureListener = listener;
        populate();
    }

    public ItemizedLayer(Map map, MarkerRendererFactory markerRendererFactory) {
        this(map, new ArrayList<MarkerInterface>(), markerRendererFactory, null);
    }

    public ItemizedLayer(Map map, List<MarkerInterface> list,
                         MarkerRendererFactory markerRendererFactory,
                         OnItemGestureListener<MarkerInterface> listener) {

        super(map, markerRendererFactory);

        mItemList = list;
        mOnItemGestureListener = listener;
        populate();
    }

    public void setOnItemGestureListener(OnItemGestureListener<MarkerInterface> listener) {
        mOnItemGestureListener = listener;
    }

    @Override
    protected synchronized MarkerInterface createItem(int index) {
        return mItemList.get(index);
    }

    @Override
    public synchronized int size() {
        return Math.min(mItemList.size(), mDrawnItemsLimit);
    }

    public synchronized boolean addItem(MarkerInterface item) {
        final boolean result = mItemList.add(item);
        populate();
        return result;
    }

    public synchronized void addItem(int location, MarkerInterface item) {
        mItemList.add(location, item);
        populate();
    }

    public synchronized boolean addItems(Collection<MarkerInterface> items) {
        final boolean result = mItemList.addAll(items);
        populate();
        return result;
    }

    public synchronized List<MarkerInterface> getItemList() {
        return mItemList;
    }

    public synchronized void removeAllItems() {
        removeAllItems(true);
    }

    public synchronized void removeAllItems(boolean withPopulate) {
        mItemList.clear();
        if (withPopulate) {
            populate();
        }
    }

    public synchronized boolean removeItem(MarkerInterface item) {
        final boolean result = mItemList.remove(item);
        populate();
        return result;
    }

    public synchronized MarkerInterface removeItem(int position) {
        final MarkerInterface result = mItemList.remove(position);
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
    //    @Override
    //    public boolean onTap(MotionEvent event, MapPosition pos) {
    //        return activateSelectedItems(event, mActiveItemSingleTap);
    //    }
    protected boolean onSingleTapUpHelper(int index, MarkerInterface item) {
        return mOnItemGestureListener.onItemSingleTapUp(index, item);
    }

    private final ActiveItem mActiveItemSingleTap = new ActiveItem() {
        @Override
        public boolean run(int index) {
            if (mOnItemGestureListener == null) {
                return false;
            }
            return onSingleTapUpHelper(index, mItemList.get(index));
        }
    };

    protected boolean onLongPressHelper(int index, MarkerInterface item) {
        return this.mOnItemGestureListener.onItemLongPress(index, item);
    }

    private final ActiveItem mActiveItemLongPress = new ActiveItem() {
        @Override
        public boolean run(final int index) {
            if (mOnItemGestureListener == null) {
                return false;
            }
            return onLongPressHelper(index, mItemList.get(index));
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

        Box box = mapPosition.getBBox(null, Tile.SIZE / 2);
        box.map2mercator();
        box.scale(1E6);

        int nearest = -1;
        int inside = -1;
        double insideY = -Double.MAX_VALUE;

        // squared dist: 50x50 px ~ 2mm on 400dpi
        // 20x20 px on baseline mdpi (160dpi)
        double dist = (20 * CanvasAdapter.getScale()) * (20 * CanvasAdapter.getScale());

        for (int i = 0; i < size; i++) {
            MarkerInterface item = mItemList.get(i);

            if (!box.contains(item.getPoint().longitudeE6,
                    item.getPoint().latitudeE6))
                continue;

            mapPosition.toScreenPoint(item.getPoint(), mTmpPoint);

            float dx = (float) (eventX - mTmpPoint.x);
            float dy = (float) (eventY - mTmpPoint.y);

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
    }

    public interface ActiveItem {
        boolean run(int aIndex);
    }

    @Override
    public boolean onGesture(Gesture g, MotionEvent e) {
        if (!isEnabled())
            return false;

        if (g instanceof Gesture.Tap)
            return activateSelectedItems(e, mActiveItemSingleTap);

        if (g instanceof Gesture.LongPress)
            return activateSelectedItems(e, mActiveItemLongPress);

        if (g == Gesture.START_DRAG) {
            return findDraggableItem(e);
        }

        if (g == Gesture.ONGOING_DRAG) {
            return dragItemTo(map().viewport().fromScreenPoint(e.getX(), e.getY()));
        }

        return false;
    }

    private boolean findDraggableItem(final MotionEvent e) {
        dragItem = null;
        return activateSelectedItems(
                e,
                new ActiveItem() {
                    @Override
                    public boolean run(final int index) {
                        final MarkerItem markerItem = (MarkerItem) mItemList.get(index);
                        dragItem = markerItem.isDraggable() ? markerItem : null;
                        return dragItem != null;
                    }
                });
    }

    private boolean dragItemTo(final GeoPoint geoPoint) {
        if (dragItem == null) {
            return false;
        }
        dragItem.geoPoint = geoPoint;
        populate();
        mMarkerRenderer.update();
        mMap.render();
        return true;
    }
}
