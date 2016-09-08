/*
 * Copyright 2014 Hannes Janetzek
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
package org.oscim.theme.rule;

import org.oscim.core.Tag;
import org.oscim.theme.rule.RuleBuilder.RuleType;
import org.oscim.theme.styles.RenderStyle;

import java.util.List;

public class Rule {
    public final class Element {
        public static final int NODE = 1 << 0;
        public static final int LINE = 1 << 1;
        public static final int POLY = 1 << 2;
        public static final int WAY = LINE | POLY;
        public static final int ANY = NODE | WAY;
    }

    public final class Closed {
        public static final int NO = 1 << 0;
        public static final int YES = 1 << 1;
        public static final int ANY = NO | YES;
    }

    public final class Selector {
        public static final int ANY = 0;
        public static final int FIRST = 1 << 0;
        public static final int WHEN_MATCHED = 1 << 1;
    }

    public final static RenderStyle[] EMPTY_STYLE = new RenderStyle[0];
    public final static Rule[] EMPTY_RULES = new Rule[0];

    public final Rule[] subRules;
    public final RenderStyle[] styles;

    public String cat;
    public final int zoom;
    public final int element;
    public final boolean selectFirstMatch;
    public final boolean selectWhenMatched;

    Rule(int element, int zoom, int selector, Rule[] subRules, RenderStyle[] styles) {
        this.element = element;
        this.zoom = zoom;

        this.subRules = (subRules == null) ? EMPTY_RULES : subRules;
        this.styles = (styles == null) ? EMPTY_STYLE : styles;

        selectFirstMatch = (selector & Selector.FIRST) != 0;
        selectWhenMatched = (selector & Selector.WHEN_MATCHED) != 0;
    }

    public boolean matchesTags(Tag[] tags) {
        return true;
    }

    public boolean matchElement(int type, Tag[] tags, int zoomLevel, List<RenderStyle> result) {
        if (((element & type) == 0) || ((zoom & zoomLevel) == 0) || !matchesTags(tags))
            return false;

        boolean matched = false;
        if (subRules != EMPTY_RULES) {
            if (selectFirstMatch) {
                /* only add first matching rule and when-matched rules iff a
                 * previous rule matched */
                for (Rule r : subRules) {
                    /* continue if matched xor selectWhenMatch */
                    if (matched ^ r.selectWhenMatched)
                        continue;

                    if (r.matchElement(type, tags, zoomLevel, result))
                        matched = true;
                }
            } else {
                /* add all rules and when-matched rules iff a previous rule
                 * matched */
                for (Rule r : subRules) {
                    if (r.selectWhenMatched && !matched)
                        continue;

                    if (r.matchElement(type, tags, zoomLevel, result))
                        matched = true;
                }
            }
        }

        if (styles == EMPTY_STYLE)
            /* matched if styles where added */
            return matched;

        /* add instructions for this rule */
        for (RenderStyle ri : styles)
            result.add(ri);

        /* this rule did match */
        return true;
    }

    public void dispose() {
        for (RenderStyle ri : styles)
            ri.dispose();

        for (Rule subRule : subRules)
            subRule.dispose();
    }

    public void scaleTextSize(float scaleFactor) {
        for (RenderStyle ri : styles)
            ri.scaleTextSize(scaleFactor);

        for (Rule subRule : subRules)
            subRule.scaleTextSize(scaleFactor);
    }

    public Rule setCat(String cat) {
        this.cat = cat;
        return this;
    }

    public void updateStyles() {
        for (RenderStyle ri : styles)
            ri.update();

        for (Rule subRule : subRules)
            subRule.updateStyles();
    }

    public static class RuleVisitor {
        public void apply(Rule r) {
            for (Rule subRule : r.subRules)
                this.apply(subRule);
        }
    }

    public static class TextSizeVisitor extends RuleVisitor {
        float scaleFactor = 1;

        public void setScaleFactor(float scaleFactor) {
            this.scaleFactor = scaleFactor;
        }

        @Override
        public void apply(Rule r) {
            for (RenderStyle ri : r.styles)
                ri.scaleTextSize(scaleFactor);
            super.apply(r);
        }
    }

    public static class UpdateVisitor extends RuleVisitor {
        @Override
        public void apply(Rule r) {
            for (RenderStyle ri : r.styles)
                ri.update();
            super.apply(r);
        }
    }

    public void apply(RuleVisitor v) {
        v.apply(this);
    }

    static class PositiveRuleK extends Rule {
        private final String mKey;

        PositiveRuleK(int element, int zoom, int selector, String key,
                      Rule[] subRules, RenderStyle[] styles) {

            super(element, zoom, selector, subRules, styles);
            mKey = key;
        }

        @Override
        public boolean matchesTags(Tag[] tags) {
            for (Tag tag : tags)
                if (mKey == tag.key)
                    return true;

            return false;
        }
    }

    static class PositiveRuleV extends Rule {
        private final String mValue;

        PositiveRuleV(int element, int zoom, int selector, String value,
                      Rule[] subRules, RenderStyle[] styles) {
            super(element, zoom, selector, subRules, styles);
            mValue = value;
        }

        @Override
        public boolean matchesTags(Tag[] tags) {
            for (Tag tag : tags)
                if (mValue == tag.value)
                    return true;

            return false;
        }
    }

    static class PositiveRuleKV extends Rule {
        private final String mKey;
        private final String mValue;

        PositiveRuleKV(int element, int zoom, int selector,
                       String key, String value,
                       Rule[] subRules, RenderStyle[] styles) {
            super(element, zoom, selector, subRules, styles);
            mKey = key;
            mValue = value;
        }

        @Override
        public boolean matchesTags(Tag[] tags) {
            for (Tag tag : tags)
                if (mKey == tag.key)
                    return (mValue == tag.value);

            return false;
        }
    }

    static class PositiveRuleMultiKV extends Rule {
        private final String mKeys[];
        private final String mValues[];

        PositiveRuleMultiKV(int element, int zoom, int selector,
                            String keys[], String values[],
                            Rule[] subRules, RenderStyle[] styles) {

            super(element, zoom, selector, subRules, styles);
            if (keys.length == 0)
                mKeys = null;
            else
                mKeys = keys;

            if (values.length == 0)
                mValues = null;
            else
                mValues = values;
        }

        @Override
        public boolean matchesTags(Tag[] tags) {
            if (mKeys == null) {
                for (Tag tag : tags) {
                    for (String value : mValues) {
                        if (value == tag.value)
                            return true;
                    }
                }
                return false;
            }

            for (Tag tag : tags)
                for (String key : mKeys) {
                    if (key == tag.key) {
                        if (mValues == null)
                            return true;

                        for (String value : mValues) {
                            if (value == tag.value)
                                return true;
                        }
                    }
                }
            return false;
        }
    }

    static class NegativeRule extends Rule {
        public final String[] keys;
        public final String[] values;

        /* (-) 'exclusive negation' matches when either KEY is not present
         * or KEY is present and any VALUE is NOT present
         *
         * (\) 'except negation' matches when KEY is present
         * none items of VALUE is present (TODO).
         * (can be emulated by <m k="a"><m k=a v="-|b|c">...</m></m>)
         *
         * (~) 'non-exclusive negation' matches when either KEY is not present
         * or KEY is present and any VALUE is present */

        public final boolean exclusive;

        NegativeRule(RuleType type, int element, int zoom, int selector,
                     String[] keys, String[] values,
                     Rule[] subRules, RenderStyle[] styles) {
            super(element, zoom, selector, subRules, styles);

            this.keys = keys;
            this.values = values;
            this.exclusive = type == RuleType.EXCLUDE;
        }

        @Override
        public boolean matchesTags(Tag[] tags) {
            if (!containsKeys(tags))
                return true;

            for (Tag tag : tags)
                for (String value : values)
                    if (value == tag.value)
                        return !exclusive;

            return exclusive;
        }

        private boolean containsKeys(Tag[] tags) {
            for (Tag tag : tags)
                for (String key : keys)
                    if (key == tag.key)
                        return true;

            return false;
        }
    }

    public static RuleBuilder builder() {
        return new RuleBuilder();
    }
}
