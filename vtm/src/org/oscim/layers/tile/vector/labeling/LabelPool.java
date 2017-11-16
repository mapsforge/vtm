package org.oscim.layers.tile.vector.labeling;

import org.oscim.renderer.bucket.TextItem;
import org.oscim.utils.pool.Pool;

final class LabelPool extends Pool<TextItem> {
    Label releaseAndGetNext(Label l) {
        if (l.item != null)
            l.item = TextItem.pool.release(l.item);

        // drop references
        l.item = null;
        l.label = null;
        Label ret = (Label) l.next;

        // ignore warning
        super.release(l);
        return ret;
    }

    @Override
    protected Label createItem() {
        return new Label();
    }
}
