/*
 * Copyright 2014 Hannes Janetzek
 * Copyright 2019 Izumi Kawashima
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
package org.oscim.utils;

import org.oscim.core.Box;
import org.oscim.core.Point;
import org.oscim.utils.RTree.Branch;
import org.oscim.utils.RTree.Node;
import org.oscim.utils.RTree.Rect;
import org.oscim.utils.pool.Inlist;
import org.oscim.utils.pool.SyncPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Implementation of RTree, a multidimensional bounding rectangle tree.
 *
 * @author 1983 Original algorithm and test code by Antonin Guttman and Michael Stonebraker, UC Berkely
 * @author 1994 ANCI C ported from original test code by Melinda Green - melinda@superliminal.com
 * @author 1995 Sphere volume fix for degeneracy problem submitted by Paul Brook
 * @author 2004 Templated C++ port by Greg Douglas
 * @author 2008 Portability issues fixed by Maxence Laurent
 * @author 2014 Ported to Java by Hannes Janetzek
 */
public class RTree<T> implements SpatialIndex<T>, Iterable<T> {

    static final Logger log = LoggerFactory.getLogger(RTree.class);

    static final int MAXNODES = 8;
    static final int MINNODES = 4;

    static final int NUMDIMS = 2;

    static final boolean DEBUG = true;

    /**
     * A Branch may be data or may be another subtree
     * The parents level determines this.
     * If the parents level is 0, then this is data
     */
    static final class Branch<E> extends Rect {
        /**
         * Child node or data item
         */
        E node;

        @Override
        public String toString() {
            return node.toString();
        }
    }

    class KnnItem implements Comparable<KnnItem> {
        Branch<?> branch;
        boolean isLeaf;
        double squareDistance;

        @Override
        public int compareTo(KnnItem o) {
            return Double.compare(squareDistance, o.squareDistance);
        }
    }

    /**
     * Node for each branch level
     */
    static final class Node {

        /**
         * Leaf is zero, others positive
         */
        int level = -1;

        /**
         * Fill count
         */
        int count;

        /**
         * Branches
         */
        final Branch<?> branch[];

        public Node(int maxnodes) {
            branch = new Branch[maxnodes];
        }

        /**
         * A leaf, contains data
         */
        boolean isLeaf() {
            return (level == 0);
        }

        @SuppressWarnings("unchecked")
        Branch<Node>[] children() {
            if (level == 0)
                throw new IllegalStateException();

            return (Branch<Node>[]) branch;
        }

        /**
         * Add a branch to a node. Split the node if necessary.
         *
         * @return <code>false</code> if node not split. Old node will be
         * updated.
         * Otherwise if node split, sets newNode to address of new node.
         * Old node updated, becomes one of two.
         */
        boolean addBranch(Branch<?> branch) {
            assert (branch != null);

            if (count < MAXNODES) {
                /* Split won't be necessary */
                this.branch[count] = branch;
                count++;
                return false;
            }

            return true;
        }

        @Override
        public String toString() {
            return count + "/" + Arrays.deepToString(branch) + '\n';
        }
    }

    /**
     * Minimal bounding rectangle (n-dimensional)
     */
    static class Rect {

        /**
         * dimensions of bounding box
         */
        double xmin, ymin, xmax, ymax;

        public Rect() {
        }

        public Rect(Box box) {
            if (DEBUG) {
                assert (xmin <= xmax);
                assert (ymin <= ymax);
            }

            xmin = box.xmin;
            ymin = box.ymin;
            xmax = box.xmax;
            ymax = box.ymax;
        }

        public Rect(double[] min, double[] max) {

            if (DEBUG) {
                for (int index = 0; index < NUMDIMS; index++)
                    assert (min[index] <= max[index]);
            }

            xmin = min[0];
            ymin = min[1];
            xmax = max[0];
            ymax = max[1];
        }

        double axisDistance(double k, double min, double max) {
            return k < min ? min - k : k <= max ? 0 : k - max;
        }

        /**
         * Calculate the n-dimensional volume of a rectangle
         */
        public double calcRectVolume() {
            return (xmax - xmin) * (ymax - ymin);
        }

        /**
         * Decide whether two rectangles overlap.
         */
        public boolean overlap(Rect other) {
            return !(xmin > other.xmax || xmax < other.xmin || ymin > other.ymax || ymax < other.ymin);
        }

        /**
         * Combine two rectangles into larger one containing both
         */
        public void combine(Rect rectA, Rect rectB) {
            xmin = Math.min(rectA.xmin, rectB.xmin);
            ymin = Math.min(rectA.ymin, rectB.ymin);
            xmax = Math.max(rectA.xmax, rectB.xmax);
            ymax = Math.max(rectA.ymax, rectB.ymax);
        }

        public void add(Rect rect) {
            xmin = Math.min(xmin, rect.xmin);
            ymin = Math.min(ymin, rect.ymin);
            xmax = Math.max(xmax, rect.xmax);
            ymax = Math.max(ymax, rect.ymax);
        }

        public void set(Rect rect) {
            xmin = rect.xmin;
            ymin = rect.ymin;
            xmax = rect.xmax;
            ymax = rect.ymax;
        }

        public void set(double[] min, double[] max) {
            if (DEBUG) {
                for (int index = 0; index < NUMDIMS; index++) {
                    assert (min[index] <= max[index]);
                }
            }

            xmin = min[0];
            ymin = min[1];
            xmax = max[0];
            ymax = max[1];
        }

        public void set(Box box) {
            if (DEBUG) {
                assert (box.xmin <= box.xmax);
                assert (box.ymin <= box.ymax);
            }
            xmin = box.xmin;
            ymin = box.ymin;
            xmax = box.xmax;
            ymax = box.ymax;
        }

        /**
         * Find the smallest rectangle that includes all rectangles in branches
         * of a node.
         */
        void setCover(Node node) {
            assert (node != null);

            set(node.branch[0]);
            for (int idx = 1; idx < node.count; idx++) {
                add(node.branch[idx]);
            }
        }

        double squareDistance(Point xy) {
            double dx = axisDistance(xy.x, xmin, xmax);
            double dy = axisDistance(xy.y, ymin, ymax);
            return dx * dx + dy * dy;
        }
    }

    /**
     * Root of tree
     */
    protected Node mRoot;

    private final Partition mLocalVars = new Partition(MAXNODES, MINNODES);

    private Rect mTmpRect = new Rect();

    private Rect getRect() {
        if (mTmpRect == null) {
            return new Rect();
        }
        Rect r = mTmpRect;
        mTmpRect = null;
        return r;
    }

    private void releaseRect(Rect r) {
        mTmpRect = r;
    }

    public RTree() {
        mRoot = allocNode();
        mRoot.level = 0;
    }

    /**
     * Insert item.
     *
     * @param min  Min of bounding rect
     * @param max  Max of bounding rect
     * @param item data.
     */
    public void insert(double min[], double max[], T item) {
        Rect r = getRect();
        r.set(min, max);
        insertRect(r, item, 0);
        releaseRect(r);
    }

    @Override
    public void insert(Box box, T item) {
        Rect r = getRect();
        r.set(box);
        insertRect(r, item, 0);
        releaseRect(r);
    }

    /**
     * Remove item.
     *
     * @param min  Min of bounding rect
     * @param max  Max of bounding rect
     * @param item data.
     */
    public boolean remove(double min[], double max[], T item) {
        Rect r = getRect();
        r.set(min, max);
        boolean removed = removeRect(r, item);
        releaseRect(r);
        return removed;
    }

    @Override
    public boolean remove(Box box, T item) {
        Rect r = getRect();
        r.set(box);
        boolean removed = removeRect(r, item);
        releaseRect(r);
        return removed;
    }

    /**
     * Find all items within search rectangle.
     *
     * @param a_min            Min of search bounding rect
     * @param a_max            Max of search bounding rect
     * @param a_searchResult   Search result array. Caller should set grow size.
     *                         Function will reset, not append to array.
     * @param a_resultCallback Callback function to return result. Callback
     *                         should return 'true' to continue searching
     * @param a_context        User context to pass as parameter to a_resultCallback
     * @return Returns the number of entries found
     */
    public boolean search(double min[], double max[], SearchCb<T> cb, Object context) {
        Rect r = getRect();
        r.set(min, max);

        searchStack(r, cb, context);

        releaseRect(r);
        return true;
    }

    @Override
    public boolean search(Box bbox, SearchCb<T> cb, Object context) {
        Rect r = getRect();
        r.set(bbox);

        searchStack(r, cb, context);

        releaseRect(r);
        return true;
    }

    @Override
    public List<T> search(Box bbox, List<T> results) {
        if (results == null)
            results = new ArrayList<T>(16);

        Rect r = getRect();
        r.set(bbox);

        //search(mRoot, r, results);
        searchStack(r, results);

        releaseRect(r);
        return results;
    }

    /**
     * See https://github.com/mourner/rbush-knn/blob/master/index.js
     */
    @Override
    public List<T> searchKNearestNeighbors(Point center, int k, double maxDistance, List<T> results) {
        if (results == null)
            results = new ArrayList<>(16);

        PriorityQueue<KnnItem> queue = new PriorityQueue<>();
        double maxSquareDistance = maxDistance * maxDistance;

        Node node = mRoot;
        while (node != null) {
            for (int idx = 0; idx < node.count; idx++) {
                Branch[] branch = node.branch;
                double squareDistance = branch[idx].squareDistance(center);
                if (squareDistance <= maxSquareDistance) {
                    KnnItem knnItem = new KnnItem();
                    knnItem.branch = branch[idx];
                    knnItem.isLeaf = node.level == 0;
                    knnItem.squareDistance = squareDistance;
                    queue.add(knnItem);
                }
            }

            while (!queue.isEmpty() && queue.peek().isLeaf) {
                KnnItem knnItem = queue.poll();
                results.add((T) knnItem.branch.node);
                if (results.size() == k)
                    return results;
            }

            KnnItem knnItem = queue.poll();
            if (knnItem != null)
                node = (Node) knnItem.branch.node;
            else
                node = null;
        }

        return results;
    }

    @Override
    public void searchKNearestNeighbors(Point center, int k, double maxDistance, SearchCb<T> cb, Object context) {
        List<T> results = searchKNearestNeighbors(center, k, maxDistance, null);
        for (T result : results)
            cb.call(result, context);
    }

    /**
     * Count the data elements in this container. This is slow as no internal
     * counter is maintained.
     */
    @Override
    public int size() {
        int[] count = {0};
        countRec(mRoot, count);

        return count[0];
    }

    private void countRec(Node node, int[] count) {
        if (node.isLeaf()) {
            count[0] += node.count;
            return;
        }

        /* not a leaf node */
        Branch<Node>[] children = node.children();
        for (int idx = 0; idx < node.count; idx++) {
            countRec(children[idx].node, count);
        }
    }

    /**
     * Remove all entries from tree.
     */
    @Override
    public void clear() {
        /* Delete all existing nodes */
        reset();

        mRoot = allocNode();
        mRoot.level = 0;
    }

    void reset() {
        removeAllRec(mRoot);
    }

    void removeAllRec(Node node) {
        assert (node != null);
        assert (node.level >= 0);

        if (!node.isLeaf()) {
            Branch<Node>[] children = node.children();
            /* This is an internal node in the tree */
            for (int idx = 0; idx < node.count; idx++) {
                removeAllRec(children[idx].node);
            }
        }
        freeNode(node);
    }

    public int nodesAlloc;
    public int nodesFree;

    public void printStats() {
        System.out.println("nodes alloc:\t" + nodesAlloc);
        System.out.println("nodes free:\t" + nodesFree);

    }

    Node allocNode() {
        nodesAlloc++;
        Node newNode;
        newNode = new Node(MAXNODES);
        return newNode;
    }

    void freeNode(Node node) {
        nodesFree++;
        assert (node != null);
    }

    /**
     * Inserts a new data rectangle into the index structure.
     * Recursively descends tree, propagates splits back up.
     *
     * @param level The level argument specifies the number of steps up from the
     *              leaf level to insert;
     *              e.g. a data rectangle goes in at level = 0.
     * @return <code>false</code> if node was not split, old node was updated.
     * If node was split, returns <code>true</code> and sets the pointer
     * pointed to by newNode to point to the new node. Old node is
     * updated to become one of two.
     */
    private Node insertRectRec(Rect rect, T item, Node node, int level) {
        //assert (rect != null && node != null && newNode != null);
        //assert (level >= 0 && level <= node.level);

        if (node.level > level) {
            /* Still above level for insertion, go down tree recursively */
            int idx = pickBranch(node, rect);
            Branch<Node>[] children = node.children();

            Node newNode = insertRectRec(rect, item, children[idx].node, level);
            if (newNode != null) {
                /* Child was split */
                node.branch[idx].setCover(children[idx].node);
                Branch<Node> branch = new Branch<Node>();
                branch.node = newNode;
                branch.setCover(newNode);
                if (node.addBranch(branch)) {
                    return splitNode(node, branch);
                }
                return null;
            } else {
                /* Child was not split */
                node.branch[idx].add(rect);
                return null;
            }
        }

        /* Have reached level for insertion. Add rect, split if necessary */
        assert (node.level == level);
        Branch<T> branch = new Branch<T>();
        branch.set(rect);
        branch.node = item;

        /* Child field of leaves contains id of data record */
        if (node.addBranch(branch)) {
            return splitNode(node, branch);
        }
        return null;
    }

    static final double mergedArea(Rect a, Rect b) {
        return ((a.xmax > b.xmax ? a.xmax : b.xmax) - (a.xmin < b.xmin ? a.xmin : b.xmin)
                * ((a.ymax > b.ymax ? a.ymax : b.ymax) - (a.ymin < b.ymin ? a.ymin : b.ymin)));
    }

    /**
     * Pick a branch. Pick the one that will need the smallest increase
     * in area to accomodate the new rectangle. This will result in the
     * least total area for the covering rectangles in the current node.
     * In case of a tie, pick the one which was smaller before, to get
     * the best resolution when searching.
     */
    int pickBranch(Node node, Rect rect) {
        boolean firstTime = true;
        double increase;
        double bestIncr = (double) -1;
        double area;
        double bestArea = 0;
        int best = 0;

        for (int idx = 0; idx < node.count; idx++) {
            Rect curRect = node.branch[idx];
            area = curRect.calcRectVolume();

            increase = mergedArea(rect, curRect) - area;

            if ((increase < bestIncr) || firstTime) {
                best = idx;
                bestArea = area;
                bestIncr = increase;
                firstTime = false;
            } else if ((increase == bestIncr) && (area < bestArea)) {
                best = idx;
                bestArea = area;
                bestIncr = increase;
            }
        }
        return best;
    }

    /**
     * Insert a data rectangle into an index structure.
     * InsertRect provides for splitting the root;
     * returns 1 if root was split, 0 if it was not.
     * The level argument specifies the number of steps up from the leaf
     * level to insert; e.g. a data rectangle goes in at level = 0.
     * InsertRect2 does the recursion.
     */
    public boolean insertRect(Rect rect, T item, int level) {
        //        if (DEBUG) {
        //            assert (rect != null && root != null);
        //            assert (level >= 0 && level <= root.level);
        //        }

        Node root = mRoot;
        Node newNode = insertRectRec(rect, item, root, level);

        if (newNode != null) {
            /* Root split, Grow tree taller and new root */
            Node newRoot = allocNode();
            newRoot.level = root.level + 1;

            Branch<Node> branch = new Branch<Node>();
            branch.setCover(root);
            branch.node = root;
            newRoot.addBranch(branch);

            branch = new Branch<Node>();
            branch.setCover(newNode);
            branch.node = newNode;
            newRoot.addBranch(branch);

            mRoot = newRoot;

            return true;
        }

        return false;
    }

    /**
     * Disconnect a dependent node.
     * Caller must return (or stop using iteration index) after this as count
     * has changed.
     */
    void disconnectBranch(Node node, int idx) {
        assert (node != null && (idx >= 0) && (idx < MAXNODES));
        assert (node.count > 0);

        /* Remove element by swapping with the last element to
         * prevent gaps in array */
        node.count--;

        if (node.count != idx) {
            node.branch[idx] = node.branch[node.count];
        }
        node.branch[node.count] = null;
    }

    /**
     * Split a node.
     * Divides the nodes branches and the extra one between two nodes.
     * Old node is one of the new ones, and one really new one is created.
     * Tries more than one method for choosing a partition, uses best result.
     */
    Node splitNode(Node node, Branch<?> branch) {
        assert (node != null);
        assert (branch != null);

        Partition partition = mLocalVars.clear();

        /* Load all the branches into a buffer, initialize old node */
        int level = node.level;
        partition.getBranches(node, branch);

        /* Find partition */
        partition.choosePartition();

        /* Put branches from buffer into 2 nodes according to
         * chosen partition */
        Node newNode = allocNode();
        newNode.level = node.level = level;
        partition.loadNodes(node, newNode);

        assert ((node.count + newNode.count) == partition.total);

        for (int i = node.count; i < MAXNODES; i++)
            node.branch[i] = null;

        return newNode;
    }

    private final ArrayList<Node> mReinsertNodes = new ArrayList<Node>();

    /**
     * Delete a data rectangle from an index structure.
     * Pass in a pointer to a Rect, the tid of the record, ptr to ptr to root
     * node.
     *
     * @return <code>false</code> if record not found, <code>true</code> if
     * success.
     * RemoveRect provides for eliminating the root.
     */
    @SuppressWarnings("unchecked")
    public boolean removeRect(Rect rect, T item) {
        //assert (rect != null && root != null);
        //assert (root != null);

        Node root = mRoot;

        ArrayList<Node> reInsertList = mReinsertNodes;

        if (removeRectRec(rect, item, root, reInsertList)) {
            /* Found and deleted a data item
             * Reinsert any branches from eliminated nodes */
            for (Node node : reInsertList) {
                for (int idx = 0; idx < node.count; idx++) {
                    insertRect((node.branch[idx]),
                            (T) node.branch[idx].node,
                            node.level);
                }
                freeNode(node);
            }
            reInsertList.clear();

            /* Check for redundant root (not leaf, 1 child) and eliminate */
            if (root.count == 1 && !root.isLeaf()) {

                Node tempNode = root.children()[0].node;
                assert (tempNode != null);

                freeNode(root);
                mRoot = tempNode;
            }
            return true;
        }
        return false;
    }

    /**
     * Delete a rectangle from non-root part of an index structure.
     * Called by RemoveRect. Descends tree recursively, merges
     * branches on the way back up.
     *
     * @return <code>true</code> iff record found.
     */
    private boolean removeRectRec(Rect rect, T item, Node node, ArrayList<Node> removed) {
        assert (rect != null && node != null && removed != null);
        assert (node.level >= 0);

        if (node.isLeaf()) {
            for (int idx = 0; idx < node.count; idx++) {
                if (node.branch[idx].node == item) {
                    /* Must return after this call as count has changed */
                    disconnectBranch(node, idx);
                    return true;
                }
            }
            return false;
        }

        /* not a leaf node */
        for (int idx = 0; idx < node.count; idx++) {

            if (!rect.overlap(node.branch[idx]))
                continue;

            Branch<Node>[] children = node.children();
            if (removeRectRec(rect, item, children[idx].node, removed)) {
                if (children[idx].node.count >= MINNODES) {
                    /* child removed, just resize parent rect */
                    children[idx].setCover(children[idx].node);
                } else {
                    /* child removed, not enough entries in node,
                     * eliminate node */
                    removed.add(children[idx].node);

                    /* Must return after this call as count has changed */
                    disconnectBranch(node, idx);
                }
                return true;
            }
        }
        return false;
    }

    //    /**
    //     * Search in an index tree or subtree for all data retangles that overlap
    //     * the argument rectangle.
    //     */
    //    public boolean search(Node node, Rect rect, int[] found, SearchCallback<T> cb,
    //            Object context) {
    //        if (DEBUG) {
    //            assert (node != null);
    //            assert (node.level >= 0);
    //            assert (rect != null);
    //        }
    //
    //        if (!node.isLeaf()) {
    //            Branch<Node>[] children = node.children();
    //            /* This is an internal node in the tree */
    //            for (int idx = 0; idx < node.count; idx++) {
    //
    //                if (rect.overlap(children[idx])) {
    //                    if (!search(children[idx].node, rect, found, cb, context)) {
    //                        /* Stop searching */
    //                        return false;
    //                    }
    //                }
    //            }
    //            /* Continue searching */
    //            return true;
    //        }
    //
    //        /* This is a leaf node */
    //        for (int idx = 0; idx < node.count; idx++) {
    //            if (rect.overlap(node.branch[idx])) {
    //                @SuppressWarnings("unchecked")
    //                T item = (T) node.branch[idx].node;
    //
    //                /* NOTE: There are different ways to return results. Here's
    //                 * where to modify */
    //                found[0]++;
    //                if (!cb.call(item, context)) {
    //                    /* Stop searching */
    //                    return false;
    //                }
    //            }
    //        }
    //
    //        /* Continue searching */
    //        return true;
    //    }
    //
    //    public boolean search(Node node, Rect rect, List<T> results) {
    //        if (DEBUG) {
    //            assert (node != null);
    //            assert (node.level >= 0);
    //            assert (rect != null);
    //        }
    //
    //        if (!node.isLeaf()) {
    //            Branch<Node>[] children = node.children();
    //            /* This is an internal node in the tree */
    //            for (int idx = 0; idx < node.count; idx++) {
    //
    //                if (rect.overlap(children[idx])) {
    //                    if (!search(children[idx].node, rect, results)) {
    //                        /* Stop searching */
    //                        return false;
    //                    }
    //                }
    //            }
    //            /* Continue searching */
    //            return true;
    //        }
    //
    //        /* This is a leaf node */
    //        for (int idx = 0; idx < node.count; idx++) {
    //            if (rect.overlap(node.branch[idx])) {
    //                @SuppressWarnings("unchecked")
    //                T item = (T) node.branch[idx].node;
    //
    //                results.add(item);
    //            }
    //        }
    //
    //        /* Continue searching */
    //        return true;
    //    }

    public void searchStack(Rect rect, SearchCb<T> cb, Object context) {
        Stack stack = stackPool.get();
        stack.push(mRoot, 0);

        O:
        while (!stack.empty()) {
            stack.pop();
            Node node = stack.node();

            if (node.level == 0) {
                /* is leaf node */
                for (int idx = 0; idx < node.count; idx++) {
                    @SuppressWarnings("unchecked")
                    Branch<T> branch[] = (Branch<T>[]) node.branch;

                    if (rect.overlap(branch[idx])) {
                        if (!cb.call(branch[idx].node, context))
                            break O;
                    }
                }
            } else {
                int idx = stack.branchIndex();

                /* Push sibling on stack for future tree walk.
                 * This is the 'fall back' node when we finish with
                 * the current level */
                for (int i = idx + 1; i < node.count; i++) {
                    if (rect.overlap(node.branch[i])) {
                        stack.push(node, i);
                        break;
                    }
                }

                /* Push first of next level to get deeper into
                 * the tree */
                node = (Node) node.branch[idx].node;
                for (int i = 0; i < node.count; i++) {
                    if (rect.overlap(node.branch[i])) {
                        stack.push(node, i);
                        break;
                    }
                }
            }
        }
        stackPool.release(stack);
    }

    public boolean searchStack(Rect rect, List<T> results) {

        @SuppressWarnings("unchecked")
        List<Object> out = (List<Object>) results;

        Stack stack = stackPool.get();
        stack.push(mRoot, 0);

        while (!stack.empty()) {
            stack.pop();
            Node node = stack.node();

            if (node.level == 0) {
                /* is leaf node */
                for (int idx = 0; idx < node.count; idx++) {
                    if (rect.overlap(node.branch[idx])) {
                        out.add(node.branch[idx].node);
                    }
                }
            } else {
                int idx = stack.branchIndex();
                /* Push sibling on stack for future tree walk.
                 * This is the 'fall back' node when we finish with
                 * the current level */
                for (int i = idx + 1; i < node.count; i++) {
                    if (rect.overlap(node.branch[i])) {
                        stack.push(node, i);
                        break;
                    }
                }

                /* Push first of next level* to get deeper into
                 * the tree */
                node = (Node) node.branch[idx].node;
                for (int i = 0; i < node.count; i++) {
                    if (rect.overlap(node.branch[i])) {
                        stack.push(node, i);
                        break;
                    }
                }
            }
        }
        stackPool.release(stack);
        return true;
    }

    /* Max stack size. Allows almost n^32 where n is number of branches in
     * node */
    static final int MAX_STACK = 32;

    static class StackElement {
        Node node;
        int branchIndex;
    }

    ;

    SyncPool<Stack> stackPool = new SyncPool<Stack>(10) {
        @Override
        protected Stack createItem() {
            return new Stack();
        }

        @Override
        protected boolean clearItem(Stack item) {
            if (item.tos != 0) {
                item.tos = 0;
                Arrays.fill(item.nodes, null);
            }
            return true;
        }

        ;
    };

    static class Stack extends Inlist<Stack> {
        /**
         * Top Of Stack index
         */
        int tos;

        final Node[] nodes;
        final int[] branchIndex;

        Stack() {
            nodes = new Node[MAX_STACK];
            branchIndex = new int[MAX_STACK];
        }

        void push(Node node, int pos) {
            nodes[tos] = node;
            branchIndex[tos] = pos;
            tos++;
            assert (tos <= MAX_STACK);
        }

        /**
         * Pop element off iteration stack (For internal use only)
         */
        boolean pop() {
            assert (tos > 0);
            nodes[tos] = null;
            tos--;
            //return stack[tos];
            return tos >= 0;
        }

        Node node() {
            return nodes[tos];
        }

        int branchIndex() {
            return branchIndex[tos];
        }

        boolean empty() {
            return tos <= 0;
        }
    }

    /* Iterator is not remove safe. */
    public static class Iterator<T> implements java.util.Iterator<T> {

        /**
         * Stack as we are doing iteration instead of recursion
         */
        final StackElement stack[] = new StackElement[MAX_STACK];

        /**
         * Top Of Stack index
         */
        int tos;

        Iterator(Node root) {
            for (int i = 0; i < MAX_STACK; i++)
                stack[i] = new StackElement();

            push(root, 0);

            findNextData();
        }

        /**
         * Is iterator invalid
         */
        boolean isNull() {
            return (tos <= 0);
        }

        /**
         * Is iterator pointing to valid data
         */
        boolean isNotNull() {
            return (tos > 0);
        }

        /* Find the next data element */
        @SuppressWarnings("unchecked")
        @Override
        public T next() {
            assert (isNotNull());
            StackElement curTos = stack[tos - 1];
            T result = (T) curTos.node.branch[curTos.branchIndex].node;
            curTos.branchIndex++;
            findNextData();
            return result;
        }

        /**
         * Find the next data element in the tree (For internal use only)
         */
        boolean findNextData() {
            for (; ; ) {
                if (tos <= 0)
                    return false;

                /* Copy stack top cause it may change as we use it */
                StackElement curTos = pop();

                if (curTos.node.isLeaf()) {
                    /* Keep walking through data while we can */
                    if (curTos.branchIndex < curTos.node.count) {

                        /* There is more data, just point to the next one */
                        push(curTos.node, curTos.branchIndex);
                        return true;
                    }
                    continue;
                }
                int idx = curTos.branchIndex;
                /* No more data, so it will fall back to previous level */
                if (idx + 1 < curTos.node.count) {
                    /* Push sibling on stack for future tree walk.
                     * This is the 'fall back' node when we finish with
                     * the current level */
                    push(curTos.node, idx + 1);
                }

                /* Since cur node is not a leaf, push first of next level
                 * to get deeper into the tree */
                Node nextLevelnode = (Node) curTos.node.branch[idx].node;

                push(nextLevelnode, 0);

                /* If we pushed on a new leaf, exit as the data is ready
                 * at TOS */
                if (nextLevelnode.isLeaf())
                    return true;
            }
        }

        /**
         * Push node and branch onto iteration stack (For internal use only)
         */
        void push(Node node, int branchIndex) {
            stack[tos].node = node;
            stack[tos].branchIndex = branchIndex;
            tos++;
            assert (tos <= MAX_STACK);
        }

        /**
         * Pop element off iteration stack (For internal use only)
         */
        StackElement pop() {
            assert (tos > 0);
            tos--;
            return stack[tos];
        }

        @Override
        public boolean hasNext() {
            return isNotNull();
        }

        @Override
        public void remove() {

        }
    }

    @Override
    public java.util.Iterator<T> iterator() {
        return new Iterator<T>(mRoot);
    }

}

/**
 * Temporary data and methods to split partition
 */
class Partition {
    int total;
    int minFill;

    final int partition[];
    final boolean taken[];
    final int count[];
    final Rect cover[];
    final double area[];

    final Rect coverSplit;
    double coverSplitArea;

    final Branch<Object> branchBuf[];

    final double mTmpArea[];

    public Partition clear() {
        int maxnodes = branchBuf.length;
        for (int i = 0; i < maxnodes; i++) {
            taken[i] = false;
            partition[i] = -1;
        }

        count[0] = count[1] = 0;
        area[0] = area[1] = 0;

        total = maxnodes;

        return this;
    }

    @SuppressWarnings("unchecked")
    public Partition(int maxnodes, int minnodes) {
        partition = new int[maxnodes + 1];
        taken = new boolean[maxnodes + 1];
        count = new int[2];
        area = new double[2];
        branchBuf = new Branch[maxnodes + 1];
        cover = new Rect[]{new Rect(), new Rect()};
        coverSplit = new Rect();

        minFill = minnodes;

        mTmpArea = new double[maxnodes + 1];
    }

    /**
     * Copy branches from the buffer into two nodes according to the
     * partition.
     * <p/>
     * FIXME this actually *moves* items from buffer!
     */
    void loadNodes(Node nodeA, Node nodeB) {
        assert (nodeA != null);
        assert (nodeB != null);

        for (int idx = 0; idx < total; idx++) {
            switch (partition[idx]) {
                case 0:
                    nodeA.addBranch(branchBuf[idx]);
                    break;
                case 1:
                    nodeB.addBranch(branchBuf[idx]);
                    break;
            }
        }
    }

    /**
     * Load branch buffer with branches from full node plus the extra
     * branch.
     */
    @SuppressWarnings("unchecked")
    void getBranches(Node node, Branch<?> branch) {
        assert (node != null);
        assert (branch != null);
        assert (node.count == node.branch.length);

        /* Load the branch buffer */
        for (int idx = 0; idx < node.count; idx++)
            branchBuf[idx] = (Branch<Object>) node.branch[idx];

        branchBuf[node.count] = (Branch<Object>) branch;

        /* Calculate rect containing all in the set */
        coverSplit.set(branchBuf[0]);
        for (int idx = 1, n = branchBuf.length; idx < n; idx++) {
            coverSplit.add(branchBuf[idx]);
        }
        coverSplitArea = coverSplit.calcRectVolume();

        /* init node - FIXME needed? */
        node.count = 0;
        node.level = -1;
    }

    private void classify(int idx, int group) {
        if (taken[idx])
            throw new IllegalStateException("Index already used!"
                    + idx + " " + Arrays.toString(taken));

        partition[idx] = group;
        taken[idx] = true;

        if (count[group] == 0) {
            cover[group].set(branchBuf[idx]);
        } else {
            cover[group].add(branchBuf[idx]);
        }

        area[group] = cover[group].calcRectVolume();
        count[group]++;
    }

    private void pickSeeds() {
        int seed0 = 0, seed1 = 1;

        double tmpArea[] = mTmpArea;
        ;

        for (int idx = 0; idx < total; idx++) {
            tmpArea[idx] = branchBuf[idx].calcRectVolume();
        }

        double worst = -coverSplitArea - 1;

        for (int idxA = 0; idxA < total - 1; idxA++) {
            for (int idxB = idxA + 1; idxB < total; idxB++) {

                double waste = RTree.mergedArea(branchBuf[idxA], branchBuf[idxB])
                        - (tmpArea[idxA] + tmpArea[idxB]);

                if (waste > worst) {
                    worst = waste;
                    seed0 = idxA;
                    seed1 = idxB;
                }
            }
        }
        classify(seed0, 0);
        classify(seed1, 1);
    }

    /**
     * Method #0 for choosing a partition:
     * As the seeds for the two groups, pick the two rects that would waste
     * the most area if covered by a single rectangle, i.e. evidently the worst
     * pair to have in the same group.
     * Of the remaining, one at a time is chosen to be put in one of the two
     * groups.
     * The one chosen is the one with the greatest difference in area
     * expansion
     * depending on which group - the rect most strongly attracted to one
     * group and repelled from the other.
     * If one group gets too full (more would force other group to violate
     * min fill requirement) then other group gets the rest.
     * These last are the ones that can go in either group most easily.
     */
    void choosePartition() {
        double biggestDiff;
        int group, chosen = 0, betterGroup = 0;

        pickSeeds();

        while (((count[0] + count[1]) < total)
                && (count[0] < (total - minFill))
                && (count[1] < (total - minFill))) {

            biggestDiff = (double) -1;
            for (int idx = 0; idx < total; idx++) {
                if (taken[idx])
                    continue;

                double growth0 = RTree.mergedArea(branchBuf[idx], cover[0]) - area[0];
                double growth1 = RTree.mergedArea(branchBuf[idx], cover[1]) - area[1];

                double diff = growth1 - growth0;
                if (diff >= 0) {
                    group = 0;
                } else {
                    group = 1;
                    diff = -diff;
                }

                if (diff > biggestDiff) {
                    biggestDiff = diff;
                    chosen = idx;
                    betterGroup = group;
                } else if ((diff == biggestDiff)
                        && (count[group] < count[betterGroup])) {
                    chosen = idx;
                    betterGroup = group;
                }
            }
            classify(chosen, betterGroup);
        }

        /* If one group too full, put remaining rects in the other */
        if ((count[0] + count[1]) < total) {
            if (count[0] >= total - minFill) {
                group = 1;
            } else {
                group = 0;
            }
            for (int index = 0; index < total; index++) {
                if (!taken[index]) {
                    classify(index, group);
                }
            }
        }

        assert ((count[0] + count[1]) == total);
        assert ((count[0] >= minFill) && (count[1] >= minFill));
    }

}

//    /** Minimal bounding rectangle (n-dimensional) */
//    static class Rect {
//
//        /** dimensions of bounding box */
//        double bounds[] = new double[NUMDIMS * 2];
//
//        //double xmin, ymin, xmax, ymax;
//
//        public Rect() {
//        }
//
//        public Rect(double[] min, double[] max) {
//
//            if (DEBUG) {
//                for (int index = 0; index < NUMDIMS; index++) {
//                    assert (min[index] <= max[index]);
//                }
//            }
//
//            for (int axis = 0; axis < NUMDIMS; axis++) {
//                this.bounds[axis] = min[axis];
//                this.bounds[NUMDIMS + axis] = max[axis];
//            }
//        }
//
//        /**
//         * Calculate the n-dimensional volume of a rectangle
//         */
//        public double calcRectVolume() {
//            double volume = (double) 1;
//
//            for (int idx = 0; idx < NUMDIMS; idx++) {
//                volume *= bounds[NUMDIMS + idx] - bounds[idx];
//            }
//
//            assert (volume >= 0);
//            return volume;
//        }
//
//        /**
//         * Decide whether two rectangles overlap.
//         */
//        public boolean overlap(Rect other) {
//            assert (other != null);
//
//            for (int idx = 0; idx < NUMDIMS; idx++) {
//                if (bounds[idx] > other.bounds[NUMDIMS + idx] ||
//                        other.bounds[idx] > bounds[NUMDIMS + idx]) {
//                    return false;
//                }
//            }
//            return true;
//        }
//
//        /**
//         * Combine two rectangles into larger one containing both
//         */
//        public Rect(Rect rectA, Rect rectB) {
//            assert (rectA != null && rectB != null);
//            for (int min = 0, max = NUMDIMS; min < NUMDIMS; min++, max++) {
//                bounds[min] = Math.min(rectA.bounds[min], rectB.bounds[min]);
//                bounds[max] = Math.max(rectA.bounds[max], rectB.bounds[max]);
//            }
//        }
//
//        public void add(Rect rect) {
//            for (int min = 0, max = NUMDIMS; min < NUMDIMS; min++, max++) {
//                bounds[min] = Math.min(bounds[min], rect.bounds[min]);
//                bounds[max] = Math.max(bounds[max], rect.bounds[max]);
//            }
//        }
//
//        public void set(Rect rect) {
//            for (int idx = 0; idx < NUMDIMS * 2; idx++) {
//                bounds[idx] = rect.bounds[idx];
//            }
//        }
//
//        /**
//         * Find the smallest rectangle that includes all rectangles in branches
//         * of a node.
//         */
//        void setCover(Node node) {
//            assert (node != null);
//
//            set(node.branch[0]);
//            for (int idx = 1; idx < node.count; idx++) {
//                add(node.branch[idx]);
//            }
//        }
//    }