/*
 * Copyright 2017 Longri
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

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Longri on 30.08.2017.
 */

public class ThemeUtils {

    public static class SAXTerminatorException extends SAXException {
        public SAXTerminatorException() {
            super();
        }
    }


    /**
     * Return true, if the given InputStream a Mapsforge render theme!
     *
     * @param stream
     * @return TRUE or FALSE
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public static boolean isMapsforgeTheme(InputStream stream) throws IOException, SAXException, ParserConfigurationException {
        final AtomicBoolean isMapsforgeTheme = new AtomicBoolean(false);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);

        XMLReader xmlReader = factory.newSAXParser().getXMLReader();
        xmlReader.setContentHandler(new DefaultHandler() {
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if (localName.equals("rendertheme")) {
                    isMapsforgeTheme.set(uri.equals("http://mapsforge.org/renderTheme"));
                    //we have all info's, break parsing
                    throw new SAXTerminatorException();
                }
            }
        });
        try {
            xmlReader.parse(new InputSource(stream));
        } catch (SAXTerminatorException e) {
            // do nothing
        }
        stream.close();
        return isMapsforgeTheme.get();
    }

}
