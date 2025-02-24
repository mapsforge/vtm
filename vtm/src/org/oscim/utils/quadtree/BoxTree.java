package org.oscim.utils.quadtree;

import org.oscim.utils.SpatialIndex.SearchCb;
import org.oscim.utils.pool.Inlist;
import org.oscim.utils.pool.Pool;
import org.oscim.utils.quadtree.BoxTree.BoxItem;
import org.oscim.utils.quadtree.BoxTree.BoxNode;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * A BoxTree is made of BoxNodes which hold a list of
 * generic BoxItems which can hold a custom data item.
 * <p/>
 * ... in case this generic isnt obvious at first sight.
 */
public class BoxTree<T extends BoxItem<E>, E> extends TileIndex<BoxNode<T>, T> {

    private static final Logger log = Logger.getLogger(BoxTree.class.getName());
    static boolean dbg = false;

    protected final int extents;
    protected final int maxDepth;
    private static final int MAX_STACK = 32;

    static class Stack<E> extends Inlist<Stack<E>> {
        /**
         * Top of stack index
         */
        int tos;

        final E[] nodes;

        @SuppressWarnings("unchecked")
        Stack() {
            nodes = (E[]) new BoxNode[MAX_STACK];
        }

        void push(E node) {
            nodes[tos] = node;
            tos++;
        }

        /**
         * Pop element off iteration stack (For internal use only)
         */
        E pop() {
            // assert (tos > 0);
            nodes[tos--] = null;
            return (E) nodes[tos];
        }

        E node() {
            return (E) nodes[tos];
        }

        boolean empty() {
            return tos <= 0;
        }
    }

    public static class BoxNode<T extends BoxItem<?>> extends TreeNode<BoxNode<T>, T> {
        // TODO this is redundant - use tile ids
        public int x1, x2, y1, y2;

        /* inherits BoxItem<E> item; */

        @Override
        public String toString() {
            return x1 + ":" + y1 + ":" + (x2 - x1);
        }
    }

    public static class BoxItem<T> extends Inlist<BoxItem<T>> {
        public int x1, x2, y1, y2;

        public T item;

        public BoxItem() {
        }

        public BoxItem(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        public BoxItem(float x1, float y1, float x2, float y2) {
            this.x1 = (int) x1;
            this.y1 = (int) y1;
            this.x2 = (int) x2;
            this.y2 = (int) y2;
        }

        public BoxItem(org.oscim.core.Box box, T item) {
            this.x1 = (int) box.xmin;
            this.y1 = (int) box.ymin;
            this.x2 = (int) box.xmax;
            this.y2 = (int) box.ymax;
            this.item = item;
        }

        public boolean overlaps(BoxItem<?> it) {
            return !((x1 > it.x2) || (it.x1 > x2) || (y1 > it.y2) || (it.y1 > y2));
        }

        public void setExtents(float[] obb, float add) {
            setExtents(obb, add, obb.length);
        }

        public void setExtents(float[] obb, float add, int length) {
            float x1, y1, x2, y2;
            x1 = x2 = obb[0];
            y1 = y2 = obb[1];

            for (int i = 2, n = length; i < n; i += 2) {
                float x = obb[i];
                if (x < x1)
                    x1 = x;
                else if (x > x2)
                    x2 = x;

                float y = obb[i + 1];
                if (y < y1)
                    y1 = y;
                else if (y > y2)
                    y2 = y;
            }
            this.x1 = (int) (x1 - add);
            this.y1 = (int) (y1 - add);
            this.x2 = (int) (x2 + add);
            this.y2 = (int) (y2 + add);
        }

        @Override
        public String toString() {
            return "[" + x1 + ',' + y1 + '/' + x2 + ',' + y2 + ']';
        }
    }

    public interface Visitor<T> {
        boolean process(T item);
    }

    boolean isPowerOfTwo(int x) {
        return ((x > 0) && (x & (x - 1)) == 0);
    }

    /**
     * BoxTreee for the range [-extents, extents]
     *
     * @param extents  must be power of two
     * @param maxDepth must be <= 30
     */
    public BoxTree(int extents, int maxDepth) {
        super();
        if (!isPowerOfTwo(extents))
            throw new IllegalArgumentException("Extents must be power of two!");

        /* size is -extents to +extents */
        this.root.x1 = -extents;
        this.root.y1 = -extents;
        this.root.x2 = extents;
        this.root.y2 = extents;

        this.extents = extents;
        this.maxDepth = maxDepth;
    }

    @Override
    public BoxNode<T> create() {
        return new BoxNode<T>();
    }

    @Override
    public void removeItem(T item) {

    }

    Pool<Stack<BoxNode<T>>> stackPool = new Pool<Stack<BoxNode<T>>>() {
        @Override
        protected Stack<BoxNode<T>> createItem() {
            return new Stack<BoxNode<T>>();
        }

        @Override
        protected boolean clearItem(Stack<BoxNode<T>> item) {
            if (item.tos != 0) {
                item.tos = 0;
                Arrays.fill(item.nodes, null);
            }
            return true;
        }

        ;
    };

    /**
     * @return false when search was aborted
     */
    public boolean search(BoxItem<?> box, SearchCb<E> cb, Object ctxt) {
        BoxNode<T> n;

        Stack<BoxNode<T>> stack = stackPool.get();
        stack.push(root);

        while (!stack.empty()) {

            n = stack.pop();

            /* process overlapping items from cur node */
            for (BoxItem<E> it = n.item; it != null; it = it.next) {
                if (it.overlaps(box)) {
                    if (!cb.call(it.item, ctxt)) {
                        stackPool.release(stack);
                        return false;
                    }
                }
            }

            BoxNode<T> p = n.parent;

            /* push next node on same level onto stack */
            switch (n.id) {
                case 0:
                    if (overlaps(p.child01, box)) {
                        stack.push(p.child01);
                        break;
                    }
                case 1:
                    if (overlaps(p.child10, box)) {
                        stack.push(p.child10);
                        break;
                    }
                case 2:
                    if (overlaps(p.child11, box)) {
                        stack.push(p.child11);
                        break;
                    }
                default:
                    break;
            }
            /* push next level child onto stack */
            if (overlaps(n.child00, box))
                stack.push(n.child00);
            else if (overlaps(n.child01, box))
                stack.push(n.child01);
            else if (overlaps(n.child10, box))
                stack.push(n.child10);
            else if (overlaps(n.child11, box))
                stack.push(n.child11);
        }

        stackPool.release(stack);
        return true;
    }

    public interface SearchBoxCb<T extends BoxItem<?>> {
        boolean call(T item);
    }

    public boolean search(BoxItem<?> box, SearchBoxCb<T> cb) {
        BoxNode<T> n;

        if (root.refs == 0) {
            //log.debug("emtpy");
            return true;
        }

        Stack<BoxNode<T>> stack = stackPool.get();
        stack.push(root);

        while (!stack.empty()) {
            n = stack.pop();

            /* process overlapping items from cur node */
            for (BoxItem<E> it = n.item; it != null; it = it.next) {
                if (it.overlaps(box)) {
                    @SuppressWarnings("unchecked")
                    T item = (T) it;
                    if (!cb.call(item)) {
                        stackPool.release(stack);
                        return false;
                    }
                }
            }

            BoxNode<T> p = n.parent;

            /* push next node on same level onto stack */
            switch (n.id) {
                case 0:
                    if (overlaps(p.child01, box)) {
                        stack.push(p.child01);
                        break;
                    }
                case 1:
                    if (overlaps(p.child10, box)) {
                        stack.push(p.child10);
                        break;
                    }
                case 2:
                    if (overlaps(p.child11, box)) {
                        stack.push(p.child11);
                        break;
                    }
                default:
                    break;
            }

            /* push next level child onto stack */
            if (overlaps(n.child00, box))
                stack.push(n.child00);

            else if (overlaps(n.child01, box))
                stack.push(n.child01);

            else if (overlaps(n.child10, box))
                stack.push(n.child10);

            else if (overlaps(n.child11, box))
                stack.push(n.child11);
        }
        stackPool.release(stack);

        return true;
    }

    private static boolean overlaps(BoxNode<?> a, BoxItem<?> b) {
        return a != null && !((a.x1 > b.x2) || (b.x1 > a.x2) || (a.y1 > b.y2) || (b.y1 > a.y2));
    }

    public interface SearchNodeCb<E extends BoxNode<?>> {
        boolean call(E item);
    }

    public void collect(SearchNodeCb<BoxNode<T>> cb) {
        BoxNode<T> n;

        Stack<BoxNode<T>> stack = stackPool.get();

        stack.push(root);

        while (!stack.empty()) {
            n = stack.pop();

            /* process overlapping items from cur node */
            cb.call(n);

            BoxNode<T> p = n.parent;

            /* push next node on same level onto stack */
            switch (n.id) {
                case 0:
                    if (p.child01 != null) {
                        stack.push(p.child01);
                        break;
                    }
                case 1:
                    if (p.child10 != null) {
                        stack.push(p.child10);
                        break;
                    }
                case 2:
                    if (p.child11 != null) {
                        stack.push(p.child11);
                        break;
                    }
                default:
                    break;
            }

            /* push next level child onto stack */
            if (n.child00 != null)
                stack.push(n.child00);
            else if (n.child01 != null)
                stack.push(n.child01);
            else if (n.child10 != null)
                stack.push(n.child10);
            else if (n.child11 != null)
                stack.push(n.child11);
        }
        stackPool.release(stack);
    }

    public BoxNode<T> create(BoxNode<T> parent, int i) {
        BoxNode<T> node;
        if (pool != null) {
            node = pool;
            pool = pool.parent;
            node.refs = 0;
        } else
            node = new BoxNode<T>();

        node.parent = parent;

        int size = (parent.x2 - parent.x1) >> 1;
        node.x1 = parent.x1;
        node.y1 = parent.y1;

        if (i == 0) {
            parent.child00 = node;
        } else if (i == 1) {
            parent.child01 = node;
            node.y1 += size;
        } else if (i == 2) {
            parent.child10 = node;
            node.x1 += size;
        } else {
            parent.child11 = node;
            node.x1 += size;
            node.y1 += size;
        }

        node.x2 = node.x1 + size;
        node.y2 = node.y1 + size;
        node.id = (byte) i;

        return node;
    }

    public void insert(T box) {
        if (box.x1 > box.x2 || box.y1 > box.y2)
            throw new IllegalArgumentException();

        if (box.next != null)
            throw new IllegalStateException("BoxItem is list");

        BoxNode<T> cur = root;
        BoxNode<T> child = null;

        int x1 = box.x1;
        int x2 = box.x2;
        int y1 = box.y1;
        int y2 = box.y2;

        for (int level = 0; level <= maxDepth; level++) {
            cur.refs++;

            /* half size of tile at current level */
            int hsize = (cur.x2 - cur.x1) >> 1;

            /* center of tile */
            int cx = cur.x1 + hsize;
            int cy = cur.y1 + hsize;

            child = null;

            if (level < maxDepth) {
                if (x2 < cx) {
                    if (y2 < cy) {
                        if ((child = cur.child00) == null)
                            child = create(cur, 0);
                    } else if (y1 >= cy) {
                        if ((child = cur.child01) == null)
                            child = create(cur, 1);
                    }
                }
                if (x1 >= cx) {
                    if (y2 < cy) {
                        if ((child = cur.child10) == null)
                            child = create(cur, 2);
                    } else if (y1 >= cy) {
                        if ((child = cur.child11) == null)
                            child = create(cur, 3);
                    }
                }
            }

            if (child == null) {
                /* push item onto list of this node */
                box.next = cur.item;
                cur.item = box;

                if (dbg)
                    log.fine("insert: " + level
                            + " cnt:" + Inlist.size(cur.item) + " " + x1 + ":" + y1
                            + " /" + (x2) + "x" + (y2) + " " + box.item);
                break;
            }
            cur = child;
        }
    }

    public boolean remove(T box, E item) {
        if (box.x1 > box.x2 || box.y1 > box.y2)
            throw new IllegalArgumentException();

        BoxNode<T> cur = root;
        BoxNode<T> child = null;

        int x1 = box.x1;
        int x2 = box.x2;
        int y1 = box.y1;
        int y2 = box.y2;

        for (int level = 0; level <= maxDepth; level++) {
            /* half size of tile at current level */
            int hsize = (cur.x2 - cur.x1) >> 1;

            /* center of tile */
            int cx = cur.x1 + hsize;
            int cy = cur.y1 + hsize;

            child = null;
            if (level < maxDepth) {
                if (x2 < cx) {
                    if (y2 < cy) {
                        child = cur.child00;
                    } else if (y1 >= cy) {
                        child = cur.child01;
                    }
                } else if (x1 >= cx) {
                    if (y2 < cy) {
                        child = cur.child10;
                    } else if (y1 >= cy) {
                        child = cur.child11;
                    }
                }
            }
            if (child == null) {

                BoxItem<E> prev = cur.item;

                for (BoxItem<E> it = cur.item; it != null; it = it.next) {
                    if (it.item == item) {
                        if (dbg)
                            log.fine("remove: " + level
                                    + " cnt:" + Inlist.size(cur.item) + " " + x1 + ":" + y1
                                    + " /" + (x2) + "x" + (y2) + " " + item);

                        if (cur.item == it) {
                            // FUNKY GENERICS...
                            @SuppressWarnings("unchecked")
                            T b = (T) it.next;
                            cur.item = b;
                        } else
                            prev.next = it.next;

                        it.next = null;
                        remove(cur);

                        return true;
                    }
                    prev = it;
                }
                return false;
            }

            cur = child;
        }
        return false;
    }

    public BoxNode<T> getNode(T box, boolean create) {
        if (box.x1 > box.x2 || box.y1 > box.y2)
            throw new IllegalArgumentException();

        BoxNode<T> cur = root;
        BoxNode<T> child = null;

        int x1 = box.x1;
        int x2 = box.x2;
        int y1 = box.y1;
        int y2 = box.y2;

        for (int level = 0; level <= maxDepth; level++) {
            cur.refs++;

            /* half size of tile at current z */
            int hsize = (cur.x2 - cur.x1) >> 1;

            /* center of tile (shift by -extents) */
            int cx = cur.x1 + hsize;
            int cy = cur.y1 + hsize;

            child = null;

            if (x2 < cx) {
                if (y2 < cy) {
                    if ((child = cur.child00) == null && create)
                        child = create(cur, 0);
                } else if (y1 >= cy) {
                    if ((child = cur.child01) == null && create)
                        child = create(cur, 1);
                }
            }
            if (x1 >= cx) {
                if (y2 < cy) {
                    if ((child = cur.child10) == null && create)
                        child = create(cur, 2);
                } else if (y1 >= cy) {
                    if ((child = cur.child11) == null && create)
                        child = create(cur, 3);
                }
            }

            if (child == null || level == maxDepth)
                return cur;

            cur = child;
        }
        return null;
    }

    public void clear() {
        root.child00 = null;
        root.child01 = null;
        root.child10 = null;
        root.child11 = null;
        root.item = null;
        root.refs = 0;
    }

    public void clearToPool() {
        BoxNode<T> node = root;

        while (true) {
            /* traverse down */
            if (node.child00 != null) {
                node = node.child00;
                continue;
            }
            if (node.child01 != null) {
                node = node.child01;
                continue;
            }
            if (node.child10 != null) {
                node = node.child10;
                continue;
            }
            if (node.child11 != null) {
                node = node.child11;
                continue;
            }

            if (node == root)
                break;

            /* traverse up */
            BoxNode<T> parent = node.parent;

            /* unlink from parent */
            switch (node.id) {
                case 0:
                    parent.child00 = null;
                    break;
                case 1:
                    parent.child01 = null;
                    break;
                case 2:
                    parent.child10 = null;
                    break;
                case 3:
                    parent.child11 = null;
                    break;
            }

            /* release items */
            node.item = null;
            node.refs = 0;

            /* add n back to pool */
            node.parent = pool;
            pool = node;

            /* continue with parent node */
            node = parent;
        }

        root.child00 = null;
        root.child01 = null;
        root.child10 = null;
        root.child11 = null;
        root.item = null;
        root.refs = 0;
    }

    @Override
    public int size() {
        return root.refs;
    }
}
