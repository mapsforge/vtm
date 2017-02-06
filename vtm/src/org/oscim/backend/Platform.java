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
package org.oscim.backend;

public enum Platform {

    ANDROID,
    IOS,
    LINUX,
    MACOS,
    UNKNOWN,
    WEBGL,
    WINDOWS;

    /**
     * @return true if on desktop (Windows, macOS, Linux)
     */
    public boolean isDesktop() {
        switch (this) {
            case LINUX:
            case MACOS:
            case WINDOWS:
                return true;
        }
        return false;
    }
}
