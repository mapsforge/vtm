/*
 * Copyright 2013 Hannes Janetzek
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
package org.oscim.theme;

import org.oscim.core.Tag;
import org.oscim.core.TagSet;
import org.oscim.utils.Utils;

class MatchingCacheKey {
    int mHash;
    Tag[] mTags;

    MatchingCacheKey() {
    }

    MatchingCacheKey(MatchingCacheKey key) {
        mTags = key.mTags;
        mHash = key.mHash;
    }

    /**
     * set temporary values for comparison
     */
    boolean set(TagSet tags, MatchingCacheKey compare) {
        int numTags = tags.size();

        /* Test if tags are equal to previous query */
        if (compare != null && numTags == compare.mTags.length) {
            int i = 0;
            for (; i < numTags; i++) {
                Tag t1 = tags.get(i);
                Tag t2 = compare.mTags[i];

                if (!(t1 == t2 || (Utils.equals(t1.key, t2.key) && Utils.equals(t1.value, t2.value))))
                    break;
            }
            if (i == numTags)
                return true;
        }

        /* Clone tags as they belong to TileDataSource.
         * Also needed for comparison if previous tags
         * were equal. */
        mTags = new Tag[numTags];

        int result = 7;
        for (int i = 0; i < numTags; i++) {
            Tag t = tags.get(i);
            result = 31 * result + t.hashCode();
            mTags[i] = t;
        }

        mHash = 31 * result;

        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (this == obj)
            return true;

        MatchingCacheKey other = (MatchingCacheKey) obj;

        int length = mTags.length;
        if (length != other.mTags.length)
            return false;

        for (int i = 0; i < length; i++) {
            Tag t1 = mTags[i];
            Tag t2 = other.mTags[i];

            if (!(t1 == t2 || (Utils.equals(t1.key, t2.key) && Utils.equals(t1.value, t2.value))))
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return mHash;
    }
}
