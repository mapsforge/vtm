/*
 * Copyright 2014 Hannes Janetzek
 * Copyright 2017 Longri
 * Copyright 2017 devemux86
 * Copyright 2018-2019 Gustl22
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
package org.oscim.theme;

import org.oscim.core.GeometryBuffer.GeometryType;
import org.oscim.core.Tag;
import org.oscim.core.TagSet;
import org.oscim.theme.rule.Rule;
import org.oscim.theme.rule.Rule.Element;
import org.oscim.theme.rule.Rule.RuleVisitor;
import org.oscim.theme.styles.RenderStyle;
import org.oscim.utils.ArrayUtils;
import org.oscim.utils.LRUCache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class RenderTheme implements IRenderTheme {
    private static final Logger log = Logger.getLogger(RenderTheme.class.getName());

    private static final int MATCHING_CACHE_SIZE = 8192;

    private final float mBaseTextSize;
    private final int mMapBackground;
    private final int mMapBackgroundOutside;

    private final int mLevels;
    private final Rule[] mRules;
    private final boolean mMapsforgeTheme;

    private final Map<String, String> mTransformBackwardKeyMap, mTransformForwardKeyMap;
    private final Map<Tag, Tag> mTransformBackwardTagMap, mTransformForwardTagMap;

    static class RenderStyleCache {
        final int matchType;
        final LRUCache<MatchingCacheKey, RenderStyleItem> cache;
        final MatchingCacheKey cacheKey;

        /* temporary matching instructions list */
        final List<RenderStyle> instructionList;

        RenderStyleItem prevItem;

        public RenderStyleCache(int type) {
            cache = new LRUCache<>(MATCHING_CACHE_SIZE);
            instructionList = new ArrayList<>(4);
            cacheKey = new MatchingCacheKey();
            matchType = type;
        }

        RenderStyleItem getRenderInstructions() {
            return cache.get(cacheKey);
        }
    }

    static class RenderStyleItem {
        RenderStyleItem next;
        int zoom;
        RenderStyle[] list;
        MatchingCacheKey key;
    }

    private final RenderStyleCache[] mStyleCache;

    public RenderTheme(int mapBackground, int mapBackgroundOutside, float baseTextSize, Rule[] rules, int levels) {
        this(mapBackground, mapBackgroundOutside, baseTextSize, rules, levels, false);
    }

    public RenderTheme(int mapBackground, int mapBackgroundOutside, float baseTextSize, Rule[] rules, int levels,
                       Map<String, String> transformKeyMap, Map<Tag, Tag> transformTagMap) {
        this(mapBackground, mapBackgroundOutside, baseTextSize, rules, levels, transformKeyMap, transformTagMap, false);
    }

    public RenderTheme(int mapBackground, int mapBackgroundOutside, float baseTextSize, Rule[] rules, int levels, boolean mapsforgeTheme) {
        this(mapBackground, mapBackgroundOutside, baseTextSize, rules, levels, null, null, mapsforgeTheme);
    }

    public RenderTheme(int mapBackground, int mapBackgroundOutside, float baseTextSize, Rule[] rules, int levels,
                       Map<String, String> transformKeyMap, Map<Tag, Tag> transformTagMap, boolean mapsforgeTheme) {
        if (rules == null)
            throw new IllegalArgumentException("rules missing");

        mMapBackground = mapBackground;
        mMapBackgroundOutside = mapBackgroundOutside;
        mBaseTextSize = baseTextSize;
        mLevels = levels;
        mRules = rules;
        mMapsforgeTheme = mapsforgeTheme;

        mTransformForwardKeyMap = transformKeyMap;
        mTransformBackwardKeyMap = ArrayUtils.swap(transformKeyMap);
        mTransformForwardTagMap = transformTagMap;
        mTransformBackwardTagMap = ArrayUtils.swap(transformTagMap);

        mStyleCache = new RenderStyleCache[3];
        mStyleCache[0] = new RenderStyleCache(Element.NODE);
        mStyleCache[1] = new RenderStyleCache(Element.LINE);
        mStyleCache[2] = new RenderStyleCache(Element.POLY);
    }

    @Override
    public void dispose() {

        for (int i = 0; i < 3; i++)
            mStyleCache[i].cache.clear();

        for (Rule rule : mRules)
            rule.dispose();
    }

    @Override
    public int getLevels() {
        return mLevels;
    }

    @Override
    public int getMapBackground() {
        return mMapBackground;
    }

    @Override
    public int getMapBackgroundOutside() {
        return mMapBackgroundOutside;
    }

    Rule[] getRules() {
        return mRules;
    }

    @Override
    public boolean isMapsforgeTheme() {
        return mMapsforgeTheme;
    }

    //AtomicInteger hitCount = new AtomicInteger(0);
    //AtomicInteger missCount = new AtomicInteger(0);
    //AtomicInteger sameCount = new AtomicInteger(0);

    @Override
    public RenderStyle[] matchElement(GeometryType geometryType, TagSet tags, int zoomLevel) {

        /* list of items in cache */
        RenderStyleItem ris = null;

        /* the item matching tags and zoomlevel */
        RenderStyleItem ri = null;

        int type = geometryType.nativeInt;
        if (type < 1 || type > 3) {
            log.fine("invalid geometry type for RenderTheme " + geometryType.name());
            return null;
        }

        RenderStyleCache cache = mStyleCache[type - 1];

        /* NOTE: maximum zoom level supported is 32 */
        int zoomMask = 1 << zoomLevel;

        synchronized (cache) {

            if ((cache.prevItem == null) || (cache.prevItem.zoom & zoomMask) == 0) {
                /* previous instructions zoom does not match */
                cache.cacheKey.set(tags, null);
            } else {
                /* compare if tags match previous instructions */
                if (cache.cacheKey.set(tags, cache.prevItem.key)) {
                    ri = cache.prevItem;
                    //log.debug(hitCount + "/" + sameCount.incrementAndGet()
                    //        + "/" + missCount + "same hit " + tags);
                }
            }

            if (ri == null) {
                /* get instruction for current cacheKey */
                ris = cache.getRenderInstructions();

                for (ri = ris; ri != null; ri = ri.next) {
                    if ((ri.zoom & zoomMask) != 0) {
                        /* cache hit */

                        //log.debug(hitCount.incrementAndGet()
                        //       + "/" + sameCount + "/" + missCount
                        //       + " cache hit " + tags);
                        break;
                    }
                }
            }

            if (ri == null) {
                /* cache miss */
                //missCount.incrementAndGet();

                List<RenderStyle> matches = cache.instructionList;
                matches.clear();

                for (Rule rule : mRules)
                    rule.matchElement(cache.matchType, cache.cacheKey.mTags, zoomMask, matches);

                int size = matches.size();
                if (size > 1) {
                    for (int i = 0; i < size - 1; i++) {
                        RenderStyle r = matches.get(i);
                        for (int j = i + 1; j < size; j++) {
                            if (matches.get(j) == r) {
                                log.fine("fix duplicate instruction! "
                                        + Arrays.deepToString(cache.cacheKey.mTags)
                                        + " zoom:" + zoomLevel + " "
                                        + r.getClass().getName());
                                matches.remove(j--);
                                size--;
                            }
                        }
                    }
                }
                /* check if same instructions are used in another level */
                for (ri = ris; ri != null; ri = ri.next) {
                    if (size == 0) {
                        if (ri.list != null)
                            continue;

                        /* both matchinglists are empty */
                        break;
                    }

                    if (ri.list == null)
                        continue;

                    if (ri.list.length != size)
                        continue;

                    int i = 0;
                    for (RenderStyle r : ri.list) {
                        if (r != matches.get(i))
                            break;
                        i++;
                    }
                    if (i == size)
                        /* both matching lists contain the same items */
                        break;
                }

                if (ri != null) {
                    /* we found a same matchting list on another zoomlevel add
                     * this zoom level to the existing RenderInstructionItem. */
                    ri.zoom |= zoomMask;

                    //log.debug(zoomLevel + " same instructions " + size + " "
                    //                + Arrays.deepToString(tags));
                } else {
                    //log.debug(zoomLevel + " new instructions " + size + " "
                    //                + Arrays.deepToString(tags));

                    ri = new RenderStyleItem();
                    ri.zoom = zoomMask;

                    if (size > 0) {
                        ri.list = new RenderStyle[size];
                        matches.toArray(ri.list);
                    }

                    /* attach this list to the one found for MatchingKey */
                    if (ris != null) {
                        ri.next = ris.next;
                        ri.key = ris.key;
                        ris.next = ri;
                    } else {
                        ri.key = new MatchingCacheKey(cache.cacheKey);
                        cache.cache.put(ri.key, ri);
                    }
                }
            }
            cache.prevItem = ri;
        }
        return ri.list;
    }

    @Override
    public void scaleTextSize(float scaleFactor) {
        for (Rule rule : mRules)
            rule.scaleTextSize(scaleFactor * mBaseTextSize);
    }

    @Override
    public String transformBackwardKey(String key) {
        if (mTransformBackwardKeyMap != null)
            return mTransformBackwardKeyMap.get(key);
        return null;
    }

    @Override
    public String transformForwardKey(String key) {
        if (mTransformForwardKeyMap != null)
            return mTransformForwardKeyMap.get(key);
        return null;
    }

    @Override
    public Tag transformBackwardTag(Tag tag) {
        if (mTransformBackwardTagMap != null)
            return mTransformBackwardTagMap.get(tag);
        return null;
    }

    @Override
    public Tag transformForwardTag(Tag tag) {
        if (mTransformForwardTagMap != null)
            return mTransformForwardTagMap.get(tag);
        return null;
    }

    @Override
    public void updateStyles() {
        for (Rule rule : mRules)
            rule.updateStyles();
    }

    public void traverseRules(RuleVisitor visitor) {
        for (Rule rule : mRules)
            rule.apply(visitor);
    }

}
