/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2013 Hannes Janetzek
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
import org.oscim.theme.styles.RenderStyle;

public interface IRenderTheme {

    /**
     * Matches a MapElement with the given parameters against this RenderTheme.
     *
     * @param zoomLevel the zoom level at which the way should be matched.
     * @return matching render instructions
     */
    RenderStyle[] matchElement(GeometryType type, TagSet tags, int zoomLevel);

    /**
     * Must be called when this RenderTheme gets destroyed to clean up and free
     * resources.
     */
    void dispose();

    /**
     * @return the number of distinct drawing levels required by this
     * RenderTheme.
     */
    int getLevels();

    /**
     * @return the map background color of this RenderTheme.
     */
    int getMapBackground();

    /**
     * Is Mapsforge or VTM theme.
     */
    boolean isMapsforgeTheme();

    void updateStyles();

    /**
     * Scales the text size of this RenderTheme by the given factor.
     *
     * @param scaleFactor the factor by which the text size should be scaled.
     */
    void scaleTextSize(float scaleFactor);

    /**
     * Used to transform tile source key to internal key.
     *
     * @return the forwards transformed tag key.
     */
    String transformForwardKey(String key);

    /**
     * Used to transform internal key to tile source key.
     * E.g. for lazy fetch needed tag values via tile source key.
     * Only use when tile source key and internal key have a 1 to 1 relation.
     *
     * @return the backwards transformed tag key.
     */
    String transformBackwardKey(String key);

    /**
     * Used to transform tile source tag to internal tag.
     *
     * @return the forwards transformed tag of this RenderTheme.
     */
    Tag transformForwardTag(Tag tag);

    /**
     * Used to transform internal tag to tile source tag.
     * Only use when tile source tag and internal tag have a 1 to 1 relation.
     *
     * @return the forwards transformed tag of this RenderTheme.
     */
    Tag transformBackwardTag(Tag tag);

    class ThemeException extends IllegalArgumentException {
        public ThemeException(String string) {
            super(string);
        }

        private static final long serialVersionUID = 1L;
    }
}
