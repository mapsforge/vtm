/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 devemux86
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
package org.oscim.test.renderer;

import org.oscim.backend.CanvasAdapter;
import org.oscim.renderer.BucketRenderer;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.bucket.SymbolBucket;
import org.oscim.renderer.bucket.SymbolItem;

public class SymbolRenderLayer extends BucketRenderer {
    boolean initialize = true;

    public SymbolRenderLayer() {
        SymbolBucket l = new SymbolBucket();
        buckets.set(l);

        SymbolItem it = SymbolItem.pool.get();
        it.billboard = false;

        try {
            it.bitmap = CanvasAdapter.getBitmapAsset("", "symbols/food/cafe.svg", null);
        } catch (Exception e) {
            e.printStackTrace();

        }
        l.addSymbol(it);
    }

    @Override
    public void update(GLViewport v) {
        if (initialize) {
            initialize = false;
            mMapPosition.copy(v.pos);
            compile();
        }
    }
}
