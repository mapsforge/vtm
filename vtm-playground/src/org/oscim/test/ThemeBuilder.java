package org.oscim.test;

import org.oscim.theme.RenderTheme;
import org.oscim.theme.rule.Rule;
import org.oscim.theme.rule.Rule.Element;
import org.oscim.theme.rule.RuleBuilder;
import org.oscim.theme.rule.RuleBuilder.RuleType;
import org.oscim.theme.styles.AreaStyle;
import org.oscim.theme.styles.AreaStyle.AreaBuilder;
import org.oscim.theme.styles.LineStyle;
import org.oscim.theme.styles.LineStyle.LineBuilder;
import org.oscim.theme.styles.RenderStyle;
import org.oscim.theme.styles.TextStyle;
import org.oscim.theme.styles.TextStyle.TextBuilder;

import java.util.ArrayList;
import java.util.Stack;

public class ThemeBuilder {
    protected final ArrayList<RuleBuilder> mRulesList = new ArrayList<RuleBuilder>();
    protected final Stack<RuleBuilder> mRuleStack = new Stack<RuleBuilder>();

    protected int mLevels = 0;
    protected int mMapBackground = 0xffffffff;
    protected int mMapBackgroundOutside = 0xffffffff;
    protected float mBaseTextSize = 1;

    protected RuleBuilder mCurrentRule;

    public RenderTheme build() {
        int[] layer = new int[1];

        Rule[] rules = new Rule[mRulesList.size()];
        for (int i = 0, n = rules.length; i < n; i++)
            rules[i] = mRulesList.get(i).onComplete(layer);

        RenderTheme theme = new RenderTheme(mMapBackground, mMapBackgroundOutside, mBaseTextSize, rules, mLevels);

        mRulesList.clear();
        mRuleStack.clear();

        return theme;
    }

    public ThemeBuilder pop() {

        mRuleStack.pop();
        if (mRuleStack.empty()) {
            mRulesList.add(mCurrentRule);
        } else {
            mCurrentRule = mRuleStack.peek();
        }
        return this;
    }

    public ThemeBuilder push(RuleBuilder rule) {
        if (!mRuleStack.empty())
            mCurrentRule.addSubRule(rule);

        mCurrentRule = rule;
        mRuleStack.push(mCurrentRule);

        return this;
    }

    public ThemeBuilder rules(RuleBuilder... rb) {
        for (RuleBuilder r : rb) {
            mRulesList.add(r);
        }
        return this;
    }

    public RuleBuilder pushParse(String keys, String values) {

        return RuleBuilder.create(keys, values)
                .zoom(~0)
                .element(Element.ANY);
    }

    public ThemeBuilder addStyle(RenderStyle style) {
        mCurrentRule.addStyle(style);
        return this;
    }

    protected void rules() {

    }

    ;

    public static LineBuilder<?> line(int color, float width) {
        return LineStyle.builder()
                .color(color)
                .strokeWidth(width);
    }

    public static AreaBuilder<?> area(int color) {
        return AreaStyle.builder()
                .color(color);
    }

    public static TextBuilder<?> wayText(float size, int color) {
        return TextStyle.builder()
                .fontSize(size)
                .color(color);
    }

    public static TextBuilder<?> nodeText(float size, int color) {
        return TextStyle.builder()
                .fontSize(size)
                .color(color)
                .isCaption(true);
    }

    public static RuleBuilder matchKey(String key) {
        return new RuleBuilder(RuleType.POSITIVE,
                new String[]{key},
                new String[]{});
    }

    public static RuleBuilder matchValue(String value) {
        return new RuleBuilder(RuleType.POSITIVE,
                new String[]{},
                new String[]{value});
    }

    public static RuleBuilder matchKeyValue(String key, String value) {
        return new RuleBuilder(RuleType.POSITIVE,
                new String[]{key},
                new String[]{value});
    }
}
