/*
 * Copyright 2013 Hannes Janetzek
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
import org.oscim.utils.geom.OBB2D;

final class Label extends TextItem {
    TextItem item;

    //Link blocking;
    //Link blockedBy;
    // shared list of all label for a tile
    //Link siblings;

    int tileX;
    int tileY;
    int tileZ;

    public int active;
    public OBB2D bbox;

    public Label clone(TextItem ti) {
        this.label = ti.label;
        this.text = ti.text;
        this.width = ti.width;
        this.length = ti.length;
        return this;
    }

    static int comparePriority(Label l1, Label l2) {

        return 0;
    }

    public static boolean shareText(Label l, Label ll) {
        if (l.text != ll.text)
            return false;

        if (l.label == ll.label)
            return true;

        if (l.label.equals(ll.label)) {
            // make strings unique, should be done only once..
            l.label = ll.label;
            return true;
        }

        return false;
    }

    public static boolean bboxOverlaps(TextItem it1, TextItem it2, float add) {
        if (it1.y1 < it1.y2) {
            if (it2.y1 < it2.y2)
                return (it1.x1 - add < it2.x2)
                        && (it2.x1 < it1.x2 + add)
                        && (it1.y1 - add < it2.y2)
                        && (it2.y1 < it1.y2 + add);

            // flip it2
            return (it1.x1 - add < it2.x2)
                    && (it2.x1 < it1.x2 + add)
                    && (it1.y1 - add < it2.y1)
                    && (it2.y2 < it1.y2 + add);
        }

        // flip it1
        if (it2.y1 < it2.y2)
            return (it1.x1 - add < it2.x2)
                    && (it2.x1 < it1.x2 + add)
                    && (it1.y2 - add < it2.y2)
                    && (it2.y1 < it1.y1 + add);

        // flip both
        return (it1.x1 - add < it2.x2)
                && (it2.x1 < it1.x2 + add)
                && (it1.y2 - add < it2.y1)
                && (it2.y2 < it1.y1 + add);
    }

    public void setAxisAlignedBBox() {
        this.x1 = (int) (x - width / 2);
        this.y1 = (int) (y - text.fontHeight / 2);
        this.x2 = (int) (x + width / 2);
        this.y2 = (int) (y + text.fontHeight / 2);
    }
}
