/*
 * Copyright 2017 Izumi Kawashima
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

import java.util.Collection;
import java.util.HashSet;

public class BuildingSolutionControl {
    private final String divQuerySelector;
    public final int MAX_VALUE = 65536;
    private Collection<ValueChangeListener> listeners = new HashSet<>();

    public BuildingSolutionControl(String divQuerySelector) {
        this.divQuerySelector = divQuerySelector;
    }

    public void init() {
        initNative(divQuerySelector);
        refresh();
    }

    private native void initNative(String divQuerySelector)/*-{
var bsc = $doc.querySelector(divQuerySelector);
var that = this;
function onUpdate(val){
that.@org.oscim.web.client.BuildingSolutionControl::fireValueChangeListeners(I)(val);
}
bsc.addEventListener("input",function(){onUpdate(this.value);});
bsc.addEventListener("change",function(){onUpdate(this.value);});
    }-*/;

    private native void refresh()/*-{

    }-*/;

    public void addValueChangeListener(ValueChangeListener l) {
        this.listeners.add(l);
    }

    public void removeValueChangeListener(ValueChangeListener l) {
        this.listeners.remove(l);
    }

    private void fireValueChangeListeners(int val) {
        for (ValueChangeListener l : this.listeners) {
            l.onValueChange(val, MAX_VALUE);
        }
    }

    public interface ValueChangeListener {
        void onValueChange(int val, int max);
    }
}
