/*
 * Copyright 2010, 2011, 2012 mapsforge.org
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
package org.oscim.core;

import org.oscim.utils.Utils;

/**
 * A tag represents an immutable key-value pair. Keys are always intern().
 */

public class Tag {
    /**
     * The key of the house number OpenStreetMap tag.
     */
    public static final String KEY_HOUSE_NUMBER = "addr:housenumber";

    /**
     * The key of the name OpenStreetMap tag.
     */
    public static final String KEY_NAME = "name";

    /**
     * The key of the reference OpenStreetMap tag.
     */
    public static final String KEY_REF = "ref";

    /**
     * The key of the elevation OpenStreetMap tag.
     */
    public static final String KEY_ELE = "ele";

    public static final String KEY_AMENITY = "amenity";
    public static final String KEY_BUILDING = "building";
    public static final String KEY_HIGHWAY = "highway";
    public static final String KEY_LANDUSE = "landuse";
    public static final String KEY_HEIGHT = "height";
    public static final String KEY_MIN_HEIGHT = "min_height";
    public static final String KEY_LEVELS = "levels";

    public static final String VALUE_YES = "yes";
    public static final String VALUE_NO = "no";

    /**
     * The key of this tag.
     */
    public final String key;

    /**
     * The value of this tag.
     */
    public String value;

    /**
     * true when value is intern().
     */
    private final boolean intern;

    private int hashCodeValue = 0;

    /**
     * @param key   the key of the tag.
     * @param value the value of the tag.
     */
    public Tag(String key, String value) {
        this.key = key == null ? null : key.intern();
        this.value = value == null ? null : value.intern();
        this.intern = true;
    }

    /**
     * Create Tag with interned Key.
     *
     * @param key         the key of the tag.
     * @param value       the value of the tag.
     * @param internValue true when value string should be intern()alized.
     */
    public Tag(String key, String value, boolean internValue) {
        this.key = key;
        this.value = (value == null || !internValue) ? value : value.intern();
        this.intern = internValue;
    }

    public Tag(String key, String value, boolean internKey, boolean internValue) {
        this.key = (key == null || !internKey) ? key : key.intern();
        this.value = (value == null || !internValue) ? value : value.intern();
        this.intern = internValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Tag)) {
            return false;
        }
        Tag other = (Tag) obj;

        if (!Utils.equals(key, other.key))
            return false;

        if (intern && other.intern) {
            if (Utils.equals(value, other.value))
                return true;

        } else if (!intern && value.equals(other.value)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (hashCodeValue == 0)
            hashCodeValue = calculateHashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Tag[")
                .append(key)
                .append(',')
                .append(value)
                .append(']')
                .toString();
    }

    /**
     * @return the hash code of this object.
     */
    private int calculateHashCode() {
        int result = 7;
        result = 31 * result + ((key == null) ? 0 : key.hashCode());
        result = 31 * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    /**
     * @param tag the textual representation of the tag.
     */
    public static Tag parse(String tag) {
        int splitPosition = tag.indexOf('=');
        if (splitPosition < 0) {
            return new Tag(tag, "");
        }

        return new Tag(tag.substring(0, splitPosition), tag.substring(splitPosition + 1));
    }
}
