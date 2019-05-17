package org.oscim.utils;

import org.oscim.core.Box;
import org.oscim.core.Point;

import java.util.List;

public interface SpatialIndex<T> {
    public interface SearchCb<T> {
        /**
         * TODO should be able to return 'continue', 'stop',
         * 'remove-current'
         *
         * @return false to stop search
         */

        boolean call(T item, Object context);
    }

    public void insert(Box box, T item);

    public boolean remove(Box box, T item);

    public List<T> search(Box bbox, List<T> results);

    public boolean search(Box bbox, SearchCb<T> cb, Object context);

    public List<T> searchKNearestNeighbors(Point center, int k, double maxDistance, List<T> results);

    public void searchKNearestNeighbors(Point center, int k, double maxDistance, SearchCb<T> cb, Object context);

    public int size();

    public void clear();
}
