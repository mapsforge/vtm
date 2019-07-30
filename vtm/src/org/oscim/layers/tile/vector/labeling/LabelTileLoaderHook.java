/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2018 devemux86
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
import org.oscim.utils.Parameters;
import org.oscim.utils.geom.PolyLabel;

import static org.oscim.core.GeometryBuffer.GeometryType.LINE;
import static org.oscim.core.GeometryBuffer.GeometryType.POINT;
import static org.oscim.core.GeometryBuffer.GeometryType.POLY;
import static org.oscim.layers.tile.vector.labeling.LabelLayer.LABEL_DATA;

public class LabelTileLoaderHook implements TileLoaderThemeHook {

    //public static final LabelTileData EMPTY = new LabelTileData();

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
            TextStyle text = (TextStyle) style.current();

            String value = element.tags.getValue(text.textKey);
            if (value == null || value.length() == 0)
                return false;

            LabelTileData ld = get(tile);
            if (element.type == LINE) {
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
                PointF label = element.labelPosition;
                // skip unnecessary calculations if label is outside of visible area
                if (label != null && (label.x < 0 || label.x > Tile.SIZE || label.y < 0 || label.y > Tile.SIZE))
                    return false;

                if (text.areaSize > 0f) {
                    float area = element.area();
                    float ratio = area / (Tile.SIZE * Tile.SIZE); // we can't use static as it's recalculated based on dpi
                    if (ratio < text.areaSize)
                        return false;
                }

                float x = 0;
                float y = 0;
                if (label == null) {
                    if (Parameters.POLY_LABEL) {
                        label = PolyLabel.get(element);
                        x = label.x;
                        y = label.y;
                    } else if (element.centerPosition != null) {
                        label = element.centerPosition;
                        // skip unnecessary calculations if centroid is outside of visible area
                        if (label.x < 0 || label.x > Tile.SIZE || label.y < 0 || label.y > Tile.SIZE)
                            return false;
                        x = label.x;
                        y = label.y;
                    } else {
                        int n = element.index[0];
                        for (int i = 0; i < n; ) {
                            x += element.points[i++];
                            y += element.points[i++];
                        }
                        x /= (n / 2);
                        y /= (n / 2);
                    }
                } else {
                    x = label.x;
                    y = label.y;
                }

                ld.labels.push(TextItem.pool.get().set(x, y, value, text));
            } else if (element.type == POINT) {
                for (int i = 0, n = element.getNumPoints(); i < n; i++) {
                    PointF p = element.getPoint(i);
                    ld.labels.push(TextItem.pool.get().set(p.x, p.y, value, text));
                }
            }
        } else if (style instanceof SymbolStyle) {
            SymbolStyle symbol = (SymbolStyle) style.current();

            if (symbol.bitmap == null && symbol.texture == null)
                return false;

            LabelTileData ld = get(tile);
            if (element.type == LINE) {
                int offset = 0;
                for (int i = 0, n = element.index.length; i < n; i++) {
                    int length = element.index[i];
                    if (length < 4)
                        break;

                    WayDecorator.renderSymbol(null, element.points, symbol,
                            offset, length, ld);
                    offset += length;
                }
            } else if (element.type == POLY) {
                PointF centroid = element.labelPosition;
                if (!Parameters.POLY_SYMBOL) {
                    if (centroid == null)
                        return false;
                }
                // skip unnecessary calculations if centroid is outside of visible area
                if (centroid != null && (centroid.x < 0 || centroid.x > Tile.SIZE || centroid.y < 0 || centroid.y > Tile.SIZE))
                    return false;

                float x = 0;
                float y = 0;
                if (centroid == null) {
                    if (Parameters.POLY_LABEL) {
                        centroid = PolyLabel.get(element);
                        x = centroid.x;
                        y = centroid.y;
                    } else if(element.centerPosition != null) {
                        centroid = element.centerPosition;
                        // skip unnecessary calculations if centroid is outside of visible area
                        if (centroid.x < 0 || centroid.x > Tile.SIZE || centroid.y < 0 || centroid.y > Tile.SIZE)
                            return false;
                        x = centroid.x;
                        y = centroid.y;
                    } else {
                        int n = element.index[0];
                        for (int i = 0; i < n; ) {
                            x += element.points[i++];
                            y += element.points[i++];
                        }
                        x /= (n / 2);
                        y /= (n / 2);
                    }
                } else {
                    x = centroid.x;
                    y = centroid.y;
                }

                SymbolItem it = SymbolItem.pool.get();
                if (symbol.bitmap != null)
                    it.set(x, y, symbol.bitmap, true);
                else
                    it.set(x, y, symbol.texture, true);
                ld.symbols.push(it);
            } else if (element.type == POINT) {
                for (int i = 0, n = element.getNumPoints(); i < n; i++) {
                    PointF p = element.getPoint(i);

                    SymbolItem it = SymbolItem.pool.get();
                    if (symbol.bitmap != null)
                        it.set(p.x, p.y, symbol.bitmap, true);
                    else
                        it.set(p.x, p.y, symbol.texture, true);
                    ld.symbols.push(it);
                }
            }
        }
        return false;
    }

    @Override
    public void complete(MapTile tile, boolean success) {
    }

}