/*
 * Copyright 2017 Longri
 * Copyright 2017 devemux86
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

import org.oscim.backend.XMLReaderAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A utility class with theme specific helper methods.
 */
public final class ThemeUtils {

    private static final Logger log = LoggerFactory.getLogger(ThemeUtils.class);

    /**
     * Check if the given theme is a Mapsforge one.
     */
    public static boolean isMapsforgeTheme(ThemeFile theme) {
        try {
            final AtomicBoolean isMapsforgeTheme = new AtomicBoolean(false);
            try {
                new XMLReaderAdapter().parse(new DefaultHandler() {
                    @Override
                    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                        if (localName.equals("rendertheme")) {
                            isMapsforgeTheme.set(uri.equals("http://mapsforge.org/renderTheme"));
                            // We have all info, break parsing
                            throw new SAXTerminationException();
                        }
                    }
                }, theme.getRenderThemeAsStream());
            } catch (SAXTerminationException e) {
                // Do nothing
            }
            return isMapsforgeTheme.get();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    private ThemeUtils() {
    }
}
