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

import org.oscim.theme.IRenderTheme.ThemeException;
import org.oscim.theme.rule.Rule.Element;
import org.oscim.theme.rule.Rule.NegativeRule;
import org.oscim.theme.rule.Rule.PositiveRuleK;
import org.oscim.theme.rule.Rule.PositiveRuleKV;
import org.oscim.theme.rule.Rule.PositiveRuleMultiKV;
import org.oscim.theme.rule.Rule.PositiveRuleV;
import org.oscim.theme.styles.RenderStyle;
import org.oscim.theme.styles.RenderStyle.StyleBuilder;

import java.util.ArrayList;
import java.util.Collections;

public class RuleBuilder {

    private final static String[] EMPTY_KV = {};

    public enum RuleType {
        POSITIVE,
        NEGATIVE,
        EXCLUDE
    }

    public String cat;
    int zoom;
    int element;
    int selector;
    RuleType type;

    String keys[];
    String values[];

    ArrayList<RenderStyle> renderStyles = new ArrayList<>(4);
    ArrayList<RuleBuilder> subRules = new ArrayList<>(4);
    StyleBuilder<?>[] styleBuilder;

    private static final String STRING_NEGATION = "~";
    private static final String STRING_EXCLUSIVE = "-";
    private static final String SEPARATOR = "\\|";

    //private static final String STRING_WILDCARD = "*";

    public RuleBuilder(RuleType type, int element, int zoom, int selector,
                       String[] keys, String[] values) {
        this.type = type;
        this.element = element;
        this.zoom = zoom;
        this.selector = selector;
        this.keys = keys;
        this.values = values;
    }

    public RuleBuilder(RuleType type, String[] keys, String[] values) {
        this.element = Element.ANY;
        this.zoom = ~0;
        this.type = type;
        this.keys = keys;
        this.values = values;
    }

    public RuleBuilder() {
        this.type = RuleType.POSITIVE;
        this.element = Element.ANY;
        this.zoom = ~0;
        this.keys = EMPTY_KV;
        this.values = EMPTY_KV;
    }

    public static RuleBuilder create(String keys, String values) {

        String[] keyList = EMPTY_KV;
        String[] valueList = EMPTY_KV;
        RuleType type = RuleType.POSITIVE;

        if (values != null) {
            if (values.startsWith(STRING_NEGATION)) {
                type = RuleType.NEGATIVE;
                if (values.length() > 2)
                    valueList = values.substring(2)
                            .split(SEPARATOR);
            } else if (values.startsWith(STRING_EXCLUSIVE)) {
                type = RuleType.EXCLUDE;
                if (values.length() > 2)
                    valueList = values.substring(2)
                            .split(SEPARATOR);
            } else {
                valueList = values.split(SEPARATOR);
            }
        }

        if (keys != null) {
            keyList = keys.split(SEPARATOR);
        }

        if (type != RuleType.POSITIVE) {
            if (keyList == null || keyList.length == 0)
                throw new ThemeException("negative rule requires key");
        }

        return new RuleBuilder(type, keyList, valueList);
    }

    public RuleBuilder zoom(byte zoomMin, byte zoomMax) {
        // zoom-level bitmask
        zoom = 0;
        for (int z = zoomMin; z <= zoomMax && z < 32; z++)
            zoom |= (1 << z);

        return this;
    }

    public Rule onComplete(int[] level) {

        RenderStyle[] styles = null;
        Rule[] rules = null;

        if (styleBuilder != null)
            for (StyleBuilder<?> style : styleBuilder) {
                renderStyles.add(style.level(level[0]).build());
                level[0] += 2;
            }

        if (renderStyles.size() > 0) {
            styles = new RenderStyle[renderStyles.size()];
            renderStyles.toArray(styles);
        }

        if (subRules.size() > 0) {
            rules = new Rule[subRules.size()];
            for (int i = 0; i < rules.length; i++)
                rules[i] = subRules.get(i).onComplete(level);
        }

        int numKeys = keys.length;
        int numVals = values.length;

        if (numKeys == 0 && numVals == 0)
            return new Rule(element, zoom, selector, rules, styles)
                    .setCat(cat);

        for (int i = 0; i < numVals; i++)
            values[i] = values[i].intern();

        for (int i = 0; i < numKeys; i++)
            keys[i] = keys[i].intern();

        if (type != RuleType.POSITIVE)
            return new NegativeRule(type, element, zoom, selector, keys, values, rules, styles)
                    .setCat(cat);

        if (numKeys == 1 && numVals == 0)
            return new PositiveRuleK(element, zoom, selector, keys[0], rules, styles)
                    .setCat(cat);

        if (numKeys == 0 && numVals == 1)
            return new PositiveRuleV(element, zoom, selector, values[0], rules, styles)
                    .setCat(cat);

        if (numKeys == 1 && numVals == 1)
            return new PositiveRuleKV(element, zoom, selector, keys[0], values[0], rules, styles)
                    .setCat(cat);

        return new PositiveRuleMultiKV(element, zoom, selector, keys, values, rules, styles)
                .setCat(cat);
    }

    public RuleBuilder addStyle(RenderStyle style) {
        renderStyles.add(style);
        return this;
    }

    public RuleBuilder addSubRule(RuleBuilder rule) {
        subRules.add(rule);
        return this;
    }

    public RuleBuilder style(StyleBuilder<?>... styles) {
        styleBuilder = styles;
        return this;
    }

    public RuleBuilder rules(RuleBuilder... rules) {
        Collections.addAll(subRules, rules);
        return this;
    }

    public RuleBuilder select(int selector) {
        this.selector = selector;
        return this;
    }

    public RuleBuilder zoom(int zoom) {
        this.zoom = zoom;
        return this;
    }

    public RuleBuilder element(int element) {
        this.element = element;
        return this;
    }

    public RuleBuilder cat(String cat) {
        this.cat = cat;
        return this;
    }
}
