/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2016 devemux86
 * Copyright 2017 Longri
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
package org.oscim.android.filepicker;

import org.oscim.theme.ExternalRenderTheme;
import org.oscim.theme.IRenderTheme;
import org.oscim.theme.ThemeFile;
import org.oscim.theme.ThemeLoader;
import org.oscim.theme.XmlMapsforgeThemeBuilder;
import org.oscim.theme.XmlThemeBuilder;
import org.oscim.tiling.TileSource.OpenResult;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import static android.R.attr.theme;

/**
 * Accepts all valid render theme XML files.
 */
public final class ValidRenderTheme implements ValidFileFilter {
    private OpenResult mOpenResult;

    @Override
    public boolean accept(File file) {

        final AtomicBoolean isMapsforgeTheme = new AtomicBoolean(false);
        InputStream is=null;
        try {
            is= new FileInputStream(file);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XMLReader xmlReader = factory.newSAXParser().getXMLReader();
            xmlReader.setContentHandler(new DefaultHandler() {

                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

                    if (localName.equals("rendertheme")) {
                        isMapsforgeTheme.set(uri.equals("http://mapsforge.org/renderTheme"));
                        //we have all info's, break parsing
                        throw new ThemeLoader.SAXTerminatorException();
                    }
                }
            });
            xmlReader.parse(new InputSource(is));
        } catch (ThemeLoader.SAXTerminatorException e) {
            //do nothing;
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }

        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ThemeFile theme = new ExternalRenderTheme(file.getAbsolutePath());
            DefaultHandler renderThemeHandler;
            if(isMapsforgeTheme.get()) {
                renderThemeHandler = new XmlMapsforgeThemeBuilder(theme);
            }else{
                renderThemeHandler = new XmlThemeBuilder(theme);
            }
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            xmlReader.setContentHandler(renderThemeHandler);
            xmlReader.parse(new InputSource(theme.getRenderThemeAsStream()));
            mOpenResult = OpenResult.SUCCESS;
        } catch (Exception e) {
            mOpenResult = new OpenResult(e.getMessage());
        }
        return mOpenResult.isSuccess();
    }

    @Override
    public OpenResult getFileOpenResult() {
        return mOpenResult;
    }
}
