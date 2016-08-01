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
package org.oscim.map;

import org.oscim.event.Gesture;
import org.oscim.event.GestureListener;
import org.oscim.event.MotionEvent;
import org.oscim.layers.Layer;
import org.oscim.map.Map.InputListener;
import org.oscim.map.Map.UpdateListener;
import org.oscim.renderer.LayerRenderer;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Layers extends AbstractList<Layer> {

    private final CopyOnWriteArrayList<Layer> mLayerList;
    private final ArrayList<Integer> mGroupList;
    private final HashMap<Integer, Integer> mGroupIndex;
    private final Map mMap;

    private boolean mDirtyLayers;
    private LayerRenderer[] mLayerRenderer;
    private Layer[] mLayers;

    Layers(Map map) {
        mMap = map;
        mLayerList = new CopyOnWriteArrayList<>();
        mGroupList = new ArrayList<>();
        mGroupIndex = new HashMap<>();
    }

    @Override
    public synchronized Layer get(int index) {
        return mLayerList.get(index);
    }

    @Override
    public synchronized int size() {
        return mLayerList.size();
    }

    @Override
    public synchronized void add(int index, Layer layer) {
        if (mLayerList.contains(layer))
            throw new IllegalArgumentException("layer added twice");

        if (layer instanceof UpdateListener)
            mMap.events.bind((UpdateListener) layer);

        if (layer instanceof InputListener)
            mMap.input.bind((InputListener) layer);

        mLayerList.add(index, layer);
        mDirtyLayers = true;
    }

    public synchronized void add(Layer layer, int group) {
        int index = mGroupList.indexOf(group);
        if (index < 0)
            throw new IllegalArgumentException("unknown layer group");
        if (mLayerList.contains(layer))
            throw new IllegalArgumentException("layer added twice");
        index++;
        if (index == mGroupList.size()) {
            add(layer);
        } else {
            add(mGroupIndex.get(mGroupList.get(index)), layer);
            for (int i = index; i < mGroupList.size(); i++) {
                group = mGroupList.get(i);
                mGroupIndex.put(group, mGroupIndex.get(group) + 1);
            }
        }
    }

    @Override
    public synchronized Layer remove(int index) {
        mDirtyLayers = true;

        Layer remove = mLayerList.remove(index);

        if (remove instanceof UpdateListener)
            mMap.events.unbind((UpdateListener) remove);
        if (remove instanceof InputListener)
            mMap.input.unbind((InputListener) remove);

        for (Integer group : mGroupIndex.keySet()) {
            int pointer = mGroupIndex.get(group);
            if (pointer > index)
                mGroupIndex.put(group, pointer - 1);
        }

        return remove;
    }

    @Override
    public synchronized Layer set(int index, Layer layer) {
        if (mLayerList.contains(layer))
            throw new IllegalArgumentException("layer added twice");

        mDirtyLayers = true;
        Layer remove = mLayerList.set(index, layer);

        // unbind replaced layer
        if (remove instanceof UpdateListener)
            mMap.events.unbind((UpdateListener) remove);
        if (remove instanceof InputListener)
            mMap.input.unbind((InputListener) remove);

        return remove;
    }

    public synchronized void addGroup(int group) {
        if (mGroupList.contains(group))
            throw new IllegalArgumentException("group added twice");
        mGroupList.add(group);
        mGroupIndex.put(group, mLayerList.size());
    }

    /**
     * Should only be used by MapRenderer.
     *
     * @return the current LayerRenderer as array.
     */
    public LayerRenderer[] getLayerRenderer() {
        if (mDirtyLayers)
            updateLayers();

        return mLayerRenderer;
    }

    void destroy() {
        if (mDirtyLayers)
            updateLayers();

        for (Layer o : mLayers)
            o.onDetach();
    }

    boolean handleGesture(Gesture g, MotionEvent e) {
        if (mDirtyLayers)
            updateLayers();

        for (Layer o : mLayers)
            if (o instanceof GestureListener)
                if (((GestureListener) o).onGesture(g, e))
                    return true;

        return false;
    }

    private synchronized void updateLayers() {
        mLayers = new Layer[mLayerList.size()];
        int numRenderLayers = 0;

        for (int i = 0, n = mLayerList.size(); i < n; i++) {
            Layer o = mLayerList.get(i);

            if (o.getRenderer() != null)
                numRenderLayers++;

            mLayers[n - i - 1] = o;
        }

        mLayerRenderer = new LayerRenderer[numRenderLayers];

        for (int i = 0, cnt = 0, n = mLayerList.size(); i < n; i++) {
            Layer o = mLayerList.get(i);
            LayerRenderer l = o.getRenderer();
            if (l != null)
                mLayerRenderer[cnt++] = l;
        }

        mDirtyLayers = false;
    }
}
