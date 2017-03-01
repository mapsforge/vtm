package org.oscim.layers.marker;

/**
 * The internal representation of a marker
 */

class InternalItem {

    MarkerInterface item;
    boolean visible;
    boolean changes;
    float x, y;
    double px, py;
    float dy;

    @Override
    public String toString() {
        return "\n" + x + ":" + y + " / " + dy + " " + visible;
    }


    /**
     * Extension to the above class for clustered items. This could be a separate 1st level class, but it is included here
     * not to pollute the source tree with tiny new files. It only adds a couple properties to InternalItem, and
     * the semantics "InternalItem.Clustered" are not bad
     */

    static class Clustered extends InternalItem {

        /* If this is > 0, this item will be displayed as a cluster circle, with size clusterSize+1 */
        int clusterSize;

        /* If this is true, this item is hidden (because it's represented by another InternalItem acting as cluster */
        boolean clusteredOut;

    }

}
