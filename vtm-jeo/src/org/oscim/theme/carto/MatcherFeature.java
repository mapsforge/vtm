/*
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
package org.oscim.theme.carto;

import org.oscim.core.Tag;
import org.oscim.core.TagSet;

import java.util.List;
import java.util.Map;

import io.jeo.vector.BasicFeature;

import static java.lang.System.out;

//imitate Feature behaviour for tags and zoom-level
class MatcherFeature extends BasicFeature {
    TagSet mTags;
    Integer mZoom;

    void setTags(TagSet tags) {
        mTags = tags;
    }

    void setZoom(int zoom) {
        mZoom = Integer.valueOf(zoom);
    }

    protected MatcherFeature() {
        super("");
    }

    @Override
    public Object get(String key) {
        //out.println("get(" + key + ")");

        if (key.equals("zoom"))
            return mZoom;

        Tag t = mTags.get(key.intern());
        if (t == null)
            return null;

        //out.println("value: " + t.value);

        return t.value;
    }

    @Override
    public BasicFeature put(String key, Object val) {
        out.println("EEEK put()");
        return null;
    }

    @Override
    public List<Object> list() {
        out.println("EEEK list()");
        return null;
    }

    @Override
    public Map<String, Object> map() {
        out.println("EEEK map()");
        return null;
    }

    @Override
    public Object get(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BasicFeature set(int arg0, Object arg1) {
        // TODO Auto-generated method stub
        return null;
    }
}
