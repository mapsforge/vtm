/*
 * Copyright 2012 Hannes Janetzek
 * Copyright 2017 Longri
 * Copyright 2017 devemux86
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
package org.oscim.renderer.bucket;

import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.PointF;
import org.oscim.renderer.atlas.TextureRegion;
import org.oscim.utils.pool.Inlist;
import org.oscim.utils.pool.SyncPool;

public class SymbolItem extends Inlist<SymbolItem> {

    public final static SyncPool<SymbolItem> pool = new SyncPool<SymbolItem>(128) {

        @Override
        protected SymbolItem createItem() {
            return new SymbolItem();
        }

        @Override
        protected boolean clearItem(SymbolItem it) {
            // drop references
            it.bitmap = null;
            it.texRegion = null;
            it.offset = null;
            it.rotation = 0;
            return true;
        }
    };

    public boolean billboard;
    public float x;
    public float y;

    public TextureRegion texRegion;
    public Bitmap bitmap;
    public PointF offset;
    public float rotation;

    public void set(float x, float y, TextureRegion texture, boolean billboard) {
        set(x, y, texture, 0, billboard);
    }

    public void set(float x, float y, TextureRegion texture, float rotation, boolean billboard) {
        this.x = x;
        this.y = y;
        this.texRegion = texture;
        this.rotation = rotation;
        this.billboard = billboard;
    }

    public void set(float x, float y, Bitmap bitmap, boolean billboard) {
        set(x, y, bitmap, 0, billboard);
    }

    public void set(float x, float y, Bitmap bitmap, float rotation, boolean billboard) {
        this.x = x;
        this.y = y;
        this.bitmap = bitmap;
        this.rotation = rotation;
        this.billboard = billboard;
    }
}
