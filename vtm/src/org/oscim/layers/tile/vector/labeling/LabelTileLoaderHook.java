/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 devemux86
 * Copyright 2016 Andrey Novikov
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

import org.oscim.core.MapElement;
import org.oscim.core.Point;
import org.oscim.core.PointF;
import org.oscim.core.Tile;
import org.oscim.layers.tile.MapTile;
import org.oscim.layers.tile.vector.VectorTileLayer.TileLoaderThemeHook;
import org.oscim.renderer.bucket.RenderBuckets;
import org.oscim.renderer.bucket.SymbolItem;
import org.oscim.renderer.bucket.TextItem;
import org.oscim.theme.styles.RenderStyle;
import org.oscim.theme.styles.SymbolStyle;
import org.oscim.theme.styles.TextStyle;
import org.oscim.utils.geom.PolyLabel;

import static org.oscim.core.GeometryBuffer.GeometryType.LINE;
import static org.oscim.core.GeometryBuffer.GeometryType.POINT;
import static org.oscim.core.GeometryBuffer.GeometryType.POLY;
import static org.oscim.layers.tile.vector.labeling.LabelLayer.LABEL_DATA;

public class LabelTileLoaderHook implements TileLoaderThemeHook {

    //public final static LabelTileData EMPTY = new LabelTileData();

    private LabelTileData get(MapTile tile) {
        // FIXME could be 'this'..
        LabelTileData ld = (LabelTileData) tile.getData(LABEL_DATA);
        if (ld == null) {
            ld = new LabelTileData();
            tile.addData(LABEL_DATA, ld);
        }
        return ld;
    }

    @Override
    public boolean process(MapTile tile, RenderBuckets buckets, MapElement element,
                           RenderStyle style, int level) {

        if (style instanceof TextStyle) {
            LabelTileData ld = get(tile);

            TextStyle text = (TextStyle) style.current();
            if (element.type == LINE) {
                String value = element.tags.getValue(text.textKey);
                if (value == null || value.length() == 0)
                    return false;

                int offset = 0;
                for (int i = 0, n = element.index.length; i < n; i++) {
                    int length = element.index[i];
                    if (length < 4)
                        break;

                    WayDecorator.renderText(null, element.points, value, text,
                            offset, length, ld);
                    offset += length;
                }
            } else if (element.type == POLY) {
                String value = element.tags.getValue(text.textKey);
                if (value == null || value.length() == 0)
                    return false;

                Point label = element.labelPosition;
                // skip unnecessary calculations if label is outside of visible area
                if (label != null && (label.x < 0 || label.x > Tile.SIZE || label.y < 0 || label.y > Tile.SIZE))
                    return false;

                if (text.areaSize > 0f) {
                    double area = element.area();
                    double ratio = area / (Tile.SIZE * Tile.SIZE); // we can't use static as it's recalculated based on dpi
                    if (ratio < text.areaSize)
                        return false;
                }

                if (label == null)
                    label = PolyLabel.get(element);

                ld.labels.push(TextItem.pool.get().set(label.x, label.y, value, text));
            } else if (element.type == POINT) {
                String value = element.tags.getValue(text.textKey);
                if (value == null || value.length() == 0)
                    return false;

                for (int i = 0, n = element.getNumPoints(); i < n; i++) {
                    Point p = element.getPoint(i);
                    ld.labels.push(TextItem.pool.get().set(p.x, p.y, value, text));
                }
            }
        } else if (style instanceof SymbolStyle) {
            SymbolStyle symbol = (SymbolStyle) style.current();

            if (symbol.bitmap == null && symbol.texture == null)
                return false;

            LabelTileData ld = get(tile);

            if (element.type == POINT) {
                for (int i = 0, n = element.getNumPoints(); i < n; i++) {
                    Point p = element.getPoint(i);

                    SymbolItem it = SymbolItem.pool.get();
                    if (symbol.bitmap != null)
                        it.set(p.x, p.y, symbol.bitmap, true);
                    else
                        it.set(p.x, p.y, symbol.texture, true);
                    ld.symbols.push(it);
                }
            } else if (element.type == LINE) {
                //TODO: implement
            } else if (element.type == POLY) {
                Point centroid = element.labelPosition;
                if (centroid == null)
                    return false;

                if (centroid.x < 0 || centroid.x > Tile.SIZE || centroid.y < 0 || centroid.y > Tile.SIZE)
                    return false;

                SymbolItem it = SymbolItem.pool.get();
                if (symbol.bitmap != null)
                    it.set(centroid.x, centroid.y, symbol.bitmap, true);
                else
                    it.set(centroid.x, centroid.y, symbol.texture, true);
                ld.symbols.push(it);
            }
        }
        return false;
    }

    @Override
    public void complete(MapTile tile, boolean success) {
    }

}
