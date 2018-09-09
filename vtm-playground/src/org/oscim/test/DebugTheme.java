package org.oscim.test;

import org.oscim.backend.canvas.Color;
import org.oscim.core.GeometryBuffer.GeometryType;
import org.oscim.core.Tag;
import org.oscim.core.TagSet;
import org.oscim.theme.IRenderTheme;
import org.oscim.theme.styles.AreaStyle;
import org.oscim.theme.styles.LineStyle;
import org.oscim.theme.styles.RenderStyle;

public class DebugTheme implements IRenderTheme {

    private final static LineStyle[] line = {new LineStyle(1, Color.MAGENTA, 2)};
    private final static AreaStyle[] area = {new AreaStyle(0, Color.CYAN)};

    @Override
    public RenderStyle[] matchElement(GeometryType type, TagSet tags, int zoomLevel) {
        if (type == GeometryType.LINE)
            return line;
        if (type == GeometryType.POLY)
            return area;

        return null;
    }

    @Override
    public void dispose() {
    }

    @Override
    public int getLevels() {
        return 0;
    }

    @Override
    public int getMapBackground() {
        return 0;
    }

    @Override
    public Tag getTag(Tag tag) {
        return null;
    }

    @Override
    public Tag getReplaceTag(Tag tag) {
        return null;
    }

    @Override
    public String getKey(String key) {
        return null;
    }

    @Override
    public String getReplaceKey(String key) {
        return null;
    }

    @Override
    public boolean hasReplaceTags() {
        return false;
    }

    @Override
    public boolean isMapsforgeTheme() {
        return false;
    }

    @Override
    public void scaleTextSize(float scaleFactor) {
    }

    @Override
    public void updateStyles() {
    }
}
