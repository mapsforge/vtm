/*
 * Copyright 2010, 2011, 2012 mapsforge.org
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
package org.oscim.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

/**
 * A utility class with IO-specific helper methods.
 */
public final class IOUtils {

    /**
     * Invokes the {@link Closeable#close()} method on the given object. If an
     * {@link IOException} occurs during the
     * method call, it will be caught and logged on level {@link Level#WARNING}.
     *
     * @param closeable the data source which should be closed (may be null).
     */
    public static void closeQuietly(OutputStream closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            //log.debug(e.toString() + " " + e);
        }
    }

    public static void closeQuietly(InputStream closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            //log.debug(e.toString() + " " + e);
        }
    }

    private IOUtils() {
    }
}
