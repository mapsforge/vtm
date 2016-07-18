/*
 * Copyright 2016 devemux86
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
package org.oscim.awt;

import com.kitfox.svg.SVGCache;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.app.beans.SVGIcon;

import org.oscim.backend.CanvasAdapter;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class AwtSvgBitmap extends AwtBitmap {

    private static BufferedImage getResourceBitmap(InputStream inputStream) throws IOException {
        synchronized (SVGCache.getSVGUniverse()) {
            try {
                URI uri = SVGCache.getSVGUniverse().loadSVG(inputStream, Integer.toString(inputStream.hashCode()));
                SVGDiagram diagram = SVGCache.getSVGUniverse().getDiagram(uri);

                float scaleFactor = CanvasAdapter.dpi / 240;
                double scale = 1/(13 * scaleFactor);

                float bitmapWidth = (float) (diagram.getWidth() * scale);
                float bitmapHeight = (float) (diagram.getHeight() * scale);

                SVGIcon icon = new SVGIcon();
                icon.setAntiAlias(true);
                icon.setPreferredSize(new Dimension((int) bitmapWidth, (int) bitmapHeight));
                icon.setScaleToFit(true);
                icon.setSvgURI(uri);
                BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                icon.paintIcon(null, bufferedImage.createGraphics(), 0, 0);

                return bufferedImage;
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }

    AwtSvgBitmap(InputStream inputStream) throws IOException {
        super(getResourceBitmap(inputStream));
    }
}
