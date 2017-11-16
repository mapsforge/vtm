/*
 * Copyright 2017 Luca Osten
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
