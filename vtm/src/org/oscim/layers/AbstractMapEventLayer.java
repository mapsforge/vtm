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
package org.oscim.layers;

import org.oscim.map.Map;

public abstract class AbstractMapEventLayer extends Layer {

    public AbstractMapEventLayer(Map map) {
        super(map);
    }

    public abstract void enableRotation(boolean enable);

    public abstract boolean rotationEnabled();

    public abstract void enableTilt(boolean enable);

    public abstract boolean tiltEnabled();

    public abstract void enableMove(boolean enable);

    public abstract boolean moveEnabled();

    public abstract void enableZoom(boolean enable);

    public abstract boolean zoomEnabled();

    public abstract void setFixOnCenter(boolean enable);
}
