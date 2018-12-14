package org.oscim.tiling.source.geojson;

import com.google.gwt.core.client.JavaScriptObject;

import java.util.AbstractCollection;
import java.util.Iterator;

/**
 * a Java Friendly way of working with Js Arrays using the Java.util.Collection
 * API
 * https://groups.google.com/forum/#!topic/google-web-toolkit/_8X9WPL6qwM
 *
 * @param <T>
 * @author sg
 */
public class JsArrayCollection<T> extends AbstractCollection<T> {

    private JsArr<T> _data;

    /**
     * creates an empty array
     */
    public JsArrayCollection() {
        _data = JsArr.create().cast();
    }

    /**
     * creates JsArrayCollection wrapping an existing js array
     */
    public JsArrayCollection(JavaScriptObject data) {
        this._data = data.cast();
    }

    public static <T> JsArrayCollection<T> create(JavaScriptObject data) {
        return new JsArrayCollection<T>(data);
    }

    @Override
    public Iterator<T> iterator() {
        return new JsArrayIterator<T>(this);
    }

    @Override
    public int size() {
        return _data.size();
    }

    public static class JsArrayIterator<T> implements Iterator<T> {

        private JsArrayCollection<T> arr;
        int currentIndex;

        public JsArrayIterator(JsArrayCollection<T> arr) {
            this.arr = arr;
            currentIndex = 0;
        }

        @Override
        public boolean hasNext() {
            //        System.out.println(currentIndex+" - "+arr.size());
            return currentIndex < arr.size();
        }

        @Override
        public T next() {
            currentIndex++;
            return arr._data.get(currentIndex - 1);
        }

        @Override
        public void remove() {
            arr._data.slice(currentIndex - 1, currentIndex);
        }

    }

    /**
     * untyped array
     */
    private static class JsArr<T> extends JavaScriptObject {
        protected JsArr() {
        }

        public final native JsArr<T> slice(int start, int end)/*-{
            return this.slice(start, end);
        }-*/;

        public static final native <T> JsArr<T> create() /*-{
            return [];
        }-*/;

        public final native int size() /*-{
            return this.length;
        }-*/;

        public final native T get(int i) /*-{
            return this[i];
        }-*/;
    }

}
