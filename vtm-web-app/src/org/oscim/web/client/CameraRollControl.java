package org.oscim.web.client;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by xor on 18/01/08.
 */

public class CameraRollControl {
    private final String divQuerySelector;
    public final int MAX_VALUE = 65536;
    private Collection<BuildingSolutionControl.ValueChangeListener> listeners = new HashSet<>();

    public CameraRollControl(String divQuerySelector) {
        this.divQuerySelector = divQuerySelector;
    }

    public void init() {
        initNative(divQuerySelector);
        refresh();
    }

    private native void initNative(String divQuerySelector)/*-{
var crc = $doc.querySelector(divQuerySelector);
var that = this;
function onUpdate(val){
that.@org.oscim.web.client.CameraRollControl::fireValueChangeListeners(I)(val);
}
crc.addEventListener("input",function(){onUpdate(this.value);});
crc.addEventListener("change",function(){onUpdate(this.value);});
    }-*/;

    private native void refresh()/*-{

    }-*/;

    public void addValueChangeListener(BuildingSolutionControl.ValueChangeListener l) {
        this.listeners.add(l);
    }

    public void removeValueChangeListener(BuildingSolutionControl.ValueChangeListener l) {
        this.listeners.remove(l);
    }

    private void fireValueChangeListeners(int val) {
        for (BuildingSolutionControl.ValueChangeListener l : this.listeners) {
            l.onValueChange(val, MAX_VALUE);
        }
    }

    public interface ValueChangeListener {
        void onValueChange(int val, int max);
    }
}
