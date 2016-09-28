/*
 * Copyright 2013 Hannes Janetzek
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
package org.oscim.core;

import java.util.Arrays;

/**
 * The Class TagSet holds a set of Tags.
 */
public class TagSet {

    /**
     * The Tags.
     */
    public Tag[] tags;

    /**
     * The number of current Tags in set.
     */
    public int numTags;

    /**
     * Instantiates a new TagSet with initial size of 10.
     */
    public TagSet() {
        tags = new Tag[10];
    }

    /**
     * Instantiates a new tag set initialized with the given size.
     *
     * @param size the initial size.
     */
    public TagSet(int size) {
        tags = new Tag[size];
    }

    /**
     * Reset the TagSet to contain 0 tags.
     */
    public void clear() {
        numTags = 0;
    }

    /**
     * Clear. Reset the TagSet to contain 0 tags and null out tags.
     */
    public void clearAndNullTags() {
        Arrays.fill(tags, null);
        numTags = 0;
    }

    /**
     * Return Tags contained in TagSet as new array.
     *
     * @return the tag[]
     */
    public Tag[] asArray() {
        Tag[] result = new Tag[numTags];
        System.arraycopy(tags, 0, result, 0, numTags);
        return result;
    }

    /**
     * Find Tag by given key.
     *
     * @param key the key as intern String.
     * @return the tag if found, null otherwise.
     */
    public Tag get(String key) {
        for (int i = 0; i < numTags; i++) {
            if (tags[i].key.equals(key))
                return tags[i];
        }
        return null;
    }

    /**
     * Checks if any tag has the key 'key'.
     *
     * @param key the key as intern String.
     * @return true, iff any tag has the given key
     */
    public boolean containsKey(String key) {
        for (int i = 0; i < numTags; i++) {
            if (tags[i].key.equals(key))
                return true;
        }
        return false;
    }

    /**
     * Get the value for a given key.
     *
     * @param key the key as intern String
     * @return the value when found, null otherwise
     */
    public String getValue(String key) {
        for (int i = 0; i < numTags; i++) {
            if (tags[i].key.equals(key))
                return tags[i].value;
        }
        return null;
    }

    /**
     * Adds the Tag tag to TagSet.
     *
     * @param tag the Tag to be added
     */
    public void add(Tag tag) {
        if (numTags >= tags.length) {
            Tag[] tmp = tags;
            tags = new Tag[numTags + 4];
            System.arraycopy(tmp, 0, tags, 0, numTags);
        }
        tags[numTags++] = tag;
    }

    /**
     * Sets the tags from 'tagArray'.
     *
     * @param tagArray the tag array
     */
    public void set(Tag[] tagArray) {
        int newTags = tagArray.length;
        if (newTags > tags.length)
            tags = new Tag[tagArray.length];
        System.arraycopy(tagArray, 0, tags, 0, newTags);

        numTags = newTags;
    }

    /**
     * Checks if 'tag' is contained in TagSet.
     *
     * @param tag the tag
     * @return true, iff tag is in TagSet
     */
    public boolean contains(Tag tag) {
        for (int i = 0; i < numTags; i++) {
            Tag t = tags[i];
            if ((t == tag) || (t.key.equals(tag.key) && t.value.equals(tag.value)))
                return true;
        }
        return false;
    }

    /**
     * Checks if a Tag with given key and value is contained in TagSet.
     *
     * @param key   the key as intern String
     * @param value the value as intern String
     * @return true, iff any tag has the given key and value
     */
    public boolean contains(String key, String value) {
        for (int i = 0; i < numTags; i++) {
            if (tags[i].key.equals(key))
                return value.equals(tags[i].value);
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numTags; i++)
            sb.append(tags[i]);

        return sb.toString();
    }
}
