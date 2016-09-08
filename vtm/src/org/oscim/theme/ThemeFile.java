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
package org.oscim.theme;

import org.oscim.theme.IRenderTheme.ThemeException;

import java.io.InputStream;
import java.io.Serializable;

public interface ThemeFile extends Serializable {
    /**
     * @return the interface callback to create a settings menu on the fly.
     */
    XmlRenderThemeMenuCallback getMenuCallback();

    /**
     * @return the prefix for all relative resource paths.
     */
    String getRelativePathPrefix();

    /**
     * @return an InputStream to read the render theme data from.
     * @throws ThemeException if an error occurs while reading the render theme XML.
     */
    InputStream getRenderThemeAsStream() throws ThemeException;
}
