/*
 * Copyright 2017 Longri
 * Copyright 2017 devemux86
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
package org.oscim.theme;

import org.oscim.renderer.atlas.TextureAtlas;
import org.oscim.renderer.atlas.TextureRegion;
import org.oscim.theme.rule.Rule;

import java.util.List;
import java.util.Map;

public class AtlasRenderTheme extends RenderTheme {

    private final Map<Object, TextureRegion> textureRegionMap;
    private final List<TextureAtlas> atlasList;

    public AtlasRenderTheme(int mapBackground, int mapBackgroundOutside, float baseTextSize, Rule[] rules, int levels,
                            Map<Object, TextureRegion> textureRegionMap, List<TextureAtlas> atlasList) {
        this(mapBackground, mapBackgroundOutside, baseTextSize, rules, levels, false, textureRegionMap, atlasList);
    }

    public AtlasRenderTheme(int mapBackground, int mapBackgroundOutside, float baseTextSize, Rule[] rules, int levels, boolean mapsforgeTheme,
                            Map<Object, TextureRegion> textureRegionMap, List<TextureAtlas> atlasList) {
        super(mapBackground, mapBackgroundOutside, baseTextSize, rules, levels, mapsforgeTheme);
        this.textureRegionMap = textureRegionMap;
        this.atlasList = atlasList;
    }

    @Override
    public void dispose() {
        super.dispose();
        for (TextureAtlas atlas : atlasList) {
            atlas.clear();
            atlas.texture.dispose();
        }
        textureRegionMap.clear();
    }
}
