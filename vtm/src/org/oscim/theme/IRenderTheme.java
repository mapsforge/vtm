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
     * Transform internal key to tile source key.
     * e.g. for lazy fetched tag values via tile source key.
     * Use when tile source and internal keys have 1-1 relation.
     *
     * @return the backwards transformed tag key.
     */
    String transformBackwardKey(String key);

    /**
     * Transform tile source key to internal key.
     *
     * @return the forward transformed tag key.
     */
    String transformForwardKey(String key);

    /**
     * Transform internal tag to tile source tag.
     * Use when tile source and internal tags have 1-1 relation.
     *
     * @return the backwards transformed tag.
     */
    Tag transformBackwardTag(Tag tag);

    /**
     * Transform tile source tag to internal tag.
     *
     * @return the forward transformed tag.
     */
    Tag transformForwardTag(Tag tag);

    class ThemeException extends IllegalArgumentException {
        public ThemeException(String message) {
            super(message);
        }

        public ThemeException(String message, Throwable cause) {
            super(message, cause);
        }

        private static final long serialVersionUID = 1L;
    }
}
