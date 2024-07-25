/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2016-2020 devemux86
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
package org.oscim.app.filefilter;

import org.oscim.theme.ThemeLoader;
import org.oscim.tiling.TileSource.OpenResult;

import java.io.File;

/**
 * Accepts all valid render theme XML files.
 */
public final class ValidRenderTheme implements ValidFileFilter {
    private OpenResult mOpenResult;

    @Override
    public boolean accept(File file) {
        try {
            ThemeLoader.load(file.getAbsolutePath());
            mOpenResult = OpenResult.SUCCESS;
        } catch (Exception e) {
            mOpenResult = new OpenResult(e.toString());
        }
        return mOpenResult.isSuccess();
    }

    @Override
    public OpenResult getFileOpenResult() {
        return mOpenResult;
    }
}
