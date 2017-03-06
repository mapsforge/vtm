package org.oscim.theme;

import org.oscim.renderer.atlas.TextureAtlas;
import org.oscim.renderer.atlas.TextureRegion;
import org.oscim.theme.rule.Rule;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Longri on 28.02.2017.
 */
public class AtlasRenderTheme extends RenderTheme {
    private final Map<Object, TextureRegion> textureRegionMap;
    private final List<TextureAtlas> atlasList;

    public AtlasRenderTheme(int mapBackground, float baseTextSize, Rule[] rules, int levels, Map<Object
            , TextureRegion> textureRegionMap, List<TextureAtlas> atlasList) {
        super(mapBackground, baseTextSize, rules, levels);
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
