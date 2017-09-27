/*
 * Copyright 2016 Longri
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
package org.oscim.ios.backend;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.Platform;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Canvas;
import org.oscim.backend.canvas.Paint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * iOS specific implementation of {@link CanvasAdapter}.
 */
public class IosGraphics extends CanvasAdapter {

    private static final Logger log = LoggerFactory.getLogger(IosGraphics.class);

    public static void init() {
        CanvasAdapter.init(new IosGraphics());
        CanvasAdapter.platform = Platform.IOS;
    }

    @Override
    protected Canvas newCanvasImpl() {
        return new IosCanvas();
    }

    @Override
    protected Paint newPaintImpl() {
        return new IosPaint();
    }

    @Override
    protected Bitmap newBitmapImpl(int width, int height, int format) {
        return new IosBitmap(width, height, format);
    }

    @Override
    protected Bitmap decodeBitmapImpl(InputStream inputStream) throws IOException {
        return new IosBitmap(inputStream);
    }

    @Override
    protected Bitmap decodeSvgBitmapImpl(InputStream inputStream, int width, int height, int percent) throws IOException {
        return new IosSvgBitmap(inputStream, width, height, percent);
    }

    @Override
    protected Bitmap loadBitmapAssetImpl(String relativePathPrefix, String src, int width, int height, int percent) throws IOException {
        return createBitmap(relativePathPrefix, src, width, height, percent);
    }
}
