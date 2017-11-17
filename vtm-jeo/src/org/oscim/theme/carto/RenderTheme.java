package org.oscim.theme.carto;

import org.jeo.carto.Carto;
import org.jeo.map.CartoCSS;
import org.jeo.map.RGB;
import org.jeo.map.Rule;
import org.jeo.map.RuleList;
import org.jeo.map.Style;
import org.oscim.core.GeometryBuffer.GeometryType;
import org.oscim.core.MapElement;
import org.oscim.core.Tag;
import org.oscim.core.TagSet;
import org.oscim.theme.IRenderTheme;
import org.oscim.theme.styles.AreaStyle;
import org.oscim.theme.styles.LineStyle;
import org.oscim.theme.styles.RenderStyle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.out;
import static org.jeo.map.CartoCSS.BACKGROUND_COLOR;
import static org.jeo.map.CartoCSS.OPACITY;

public class RenderTheme implements IRenderTheme {

    final String STYLE = "" +

            "[building = 'yes'] {" +
            " z: 1;" +
            "  polygon-fill: #eee;" +
            "  [zoom >= 16] {" +
            "    polygon-fill: #c00;" +
            "  }" +
            "}" +

            "[admin_level = '2'] {" +
            " line-color: #000;" +
            " line-width: 1;" +
            " z: 1;" +
            "}" +

            "[admin_level = '2'] {" +
            " line-color: #000;" +
            " line-width: 1;" +
            " z: 1;" +
            "}" +

            "[admin_level = '4'] {" +
            " line-color: #aaa;" +
            " line-width: 1;" +
            " z: 2;" +
            "}" +

            "[highway = 'motorway'] {" +
            " line-color: #a00;" +
            " z: 10;" +
            "}" +

            "[highway = 'primary'] {" +
            " line-color: #aa0;" +
            " z: 11;" +
            "}" +

            "[highway = 'residential'],[highway = 'road'],[highway = 'secondary'] {" +
            " line-color: #fff;" +
            " z: 12;" +
            "}" +

            " [landuse = 'forest'] {" +
            " polygon-fill: #0a0;" +
            " z: 2;" +
            "}" +

            "[natural = 'water'] {" +
            " polygon-fill: #00a;" +
            " z: 3;" +
            "}";

    private Style mStyle;
    private RuleList mRules;

    MatcherFeature mMatchFeature = new MatcherFeature();
    private int mBackground;

    public RenderTheme() {

        try {
            mStyle = loadStyle();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // get map background
        RuleList rules = mStyle.getRules().selectByName("Map", false, false);
        if (!rules.isEmpty()) {
            Rule rule = rules.collapse();
            RGB bgColor = rule.color(null, BACKGROUND_COLOR, null);
            if (bgColor != null) {
                bgColor = bgColor.alpha(rule.number(null, OPACITY, 1f));
                mBackground = color(bgColor);
            }
        }

        mRules = mStyle.getRules();

        //out.println(mRules);
        //out.println();
        if (mRules.get(1).equals(mRules.get(2)))
            out.println("ok");

        for (Rule r : mRules)
            out.println(formatRule(r, 0));
    }

    class StyleSet {
        int level;
        RenderStyle[] ri = new RenderStyle[2];
    }

    Map<Rule, StyleSet> mStyleSets = new HashMap<Rule, StyleSet>();
    int mCurLevel = 0;

    public String formatRule(Rule r, int indent) {
        StringBuilder sb = new StringBuilder();

        String pad = "";
        for (int i = 0; i < indent; i++)
            pad += " ";

        sb.append(pad);

        if (sb.length() > 0)
            sb.setLength(sb.length() - 1);

        sb.append(pad).append(" {").append("\n");

        StyleSet s = new StyleSet();
        RGB l = null;
        RGB p = null;
        if (r.properties().containsKey(CartoCSS.LINE_COLOR)) {
            l = r.color(null, CartoCSS.LINE_COLOR, RGB.black);
        }
        if (r.properties().containsKey(CartoCSS.POLYGON_FILL)) {
            p = r.color(null, CartoCSS.POLYGON_FILL, RGB.black);
        }

        if (p != null) {
            s.ri[0] = new AreaStyle(mCurLevel++, color(p));
        }

        if (l != null) {
            s.ri[1] = new LineStyle(mCurLevel++, color(l), 1);
        }

        if (p != null || l != null) {
            mStyleSets.put(r, s);
            out.println("put " + s.ri[0] + s.ri[1]);
        }

        for (Map.Entry<String, Object> e : r.properties().entrySet()) {
            sb.append(pad).append("  ").append(e.getKey()).append(": ").append(e.getValue())
                    .append(";\n");
        }

        for (Rule nested : r.nested()) {
            sb.append(formatRule(nested, indent + 2)).append("\n");
        }

        sb.append(pad).append("}");
        return sb.toString();
    }

    Style loadStyle() throws IOException {
        return Carto.parse(STYLE);
    }

    @Override
    public synchronized RenderStyle[] matchElement(GeometryType type, TagSet tags,
                                                   int zoomLevel) {
        MatcherFeature f = mMatchFeature;

        f.setTags(tags);
        f.setZoom(zoomLevel);

        RuleList rules = mRules.match(f);

        Rule r = rules.collapse();

        //out.println(r);
        if (rules.isEmpty())
            return null;

        int z = r.number(f, "z", 0f).intValue();

        if (type == GeometryType.POLY) {
            RGB c = r.color(f, CartoCSS.POLYGON_FILL, RGB.black);
            out.println(z + " " + c);
            return new RenderStyle[]{
                    new AreaStyle(z, color(c))
            };

        } else if (type == GeometryType.LINE) {
            RGB c = r.color(f, CartoCSS.LINE_COLOR, RGB.black);
            float width = r.number(f, CartoCSS.LINE_WIDTH, 2f);
            //out.println(z + " " + c);

            return new RenderStyle[]{
                    new LineStyle(100 + z, color(c), width)
            };

        } else if (type == GeometryType.POINT) {
            //RGB c = r.color(f, CartoCSS.MARKER_FILL, RGB.black);
            //out.println(c);
            //return new RenderInstruction[] {
            //        new Caption(color(c), width)
            //};
        }

        return null;
    }

    public static int color(RGB rgb) {
        return rgb.getAlpha() << 24
                | rgb.getRed() << 16
                | rgb.getGreen() << 8
                | rgb.getBlue();
    }

    @Override
    public void dispose() {
    }

    @Override
    public int getLevels() {
        return 1;
    }

    @Override
    public int getMapBackground() {
        return mBackground;
    }

    @Override
    public boolean isMapsforgeTheme() {
        return false;
    }

    @Override
    public void scaleTextSize(float scaleFactor) {
    }

    public static void main(String[] args) {
        RenderTheme t = new RenderTheme();

        MapElement e = new MapElement();
        e.startPolygon();
        e.tags.add(new Tag(Tag.KEY_BUILDING, Tag.VALUE_YES));

        t.matchElement(GeometryType.POLY, e.tags, 16);
        t.matchElement(GeometryType.POLY, e.tags, 15);
    }

    @Override
    public void updateStyles() {
        // TODO Auto-generated method stub

    }

}
