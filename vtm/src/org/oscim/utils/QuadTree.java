package org.oscim.utils;

import org.oscim.core.Box;
import org.oscim.core.Point;
import org.oscim.utils.pool.Pool;
import org.oscim.utils.quadtree.BoxTree;
import org.oscim.utils.quadtree.BoxTree.BoxItem;

import java.util.List;

/**
 * Quad-tree with fixed extents.
 * This implementation uses int bounding-boxes internally,
 * so items extents should be greater than 1. FIXME tests this case
 */
public class QuadTree<T> extends BoxTree<BoxItem<T>, T> implements SpatialIndex<T> {

    public QuadTree(int extents, int maxDepth) {
        super(extents, maxDepth);
    }

    final Pool<BoxItem<T>> boxPool = new Pool<BoxItem<T>>() {
        @Override
        protected BoxItem<T> createItem() {
            return new BoxItem<T>();
        }
    };

    private BoxItem<T> getBox(Box box) {
        BoxItem<T> it = boxPool.get();
        it.x1 = (int) box.xmin;
        it.y1 = (int) box.ymin;
        it.x2 = (int) box.xmax;
        it.y2 = (int) box.ymax;
        return it;
    }

    @Override
    public void insert(Box box, T item) {
        insert(new BoxItem<T>(box, item));
    }

    @Override
    public boolean remove(Box box, T item) {
        BoxItem<T> bbox = getBox(box);
        boolean ok = remove(bbox, item);
        boxPool.release(bbox);
        return ok;
    }

    static class CollectCb<T> implements SearchCb<T> {
        @SuppressWarnings("unchecked")
        @Override
        public boolean call(T item, Object context) {
            List<T> l = (List<T>) context;
            l.add(item);
            return true;
        }
    }

    final CollectCb<T> collectCb = new CollectCb<T>();

    @Override
    public List<T> search(Box bbox, List<T> results) {
        BoxItem<T> box = getBox(bbox);
        search(box, collectCb, results);
        boxPool.release(box);
        return results;
    }

    @Override
    public boolean search(Box bbox, SearchCb<T> cb, Object context) {
        BoxItem<T> box = getBox(bbox);
        boolean finished = search(box, cb, context);
        boxPool.release(box);
        return finished;
    }

    @Override
    public List<T> searchKNearestNeighbors(Point center, int k, double maxDistance, List<T> results) {
        // TODO
        return results;
    }

    @Override
    public void searchKNearestNeighbors(Point center, int k, double maxDistance, SearchCb<T> cb, Object context) {
        // TODO
    }
}
