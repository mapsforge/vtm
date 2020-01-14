/*
 * Copyright 2019 Gustl22
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
package org.oscim.core;

import org.junit.Assert;
import org.junit.Test;

public class GeometryBufferTest {

    @Test
    public void removeLastPointPolyTest() {
        GeometryBuffer buffer = new GeometryBuffer(4, 1);
        buffer.startPolygon();
        buffer.addPoint(0, 0);
        buffer.addPoint(10, 0);
        buffer.addPoint(10, 10);
        buffer.addPoint(0, 10);

        buffer.removeLastPoint();
        System.out.println(buffer.toString());
        Assert.assertEquals(buffer.getNumPoints(), 3);
    }
}
