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
package org.oscim.backend;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateTimeTest {

    private static final Logger log = LoggerFactory.getLogger(DateTimeTest.class);

    @Test
    public void testDateTime() {
        DateTimeAdapter.init(new DateTime());
        log.info("Day of Year\t" + DateTimeAdapter.instance.getDayOfYear());
        log.info("Hour of Day\t" + DateTimeAdapter.instance.getHour());
        log.info("Minute of Day\t" + DateTimeAdapter.instance.getMinute());
        log.info("Second of Day\t" + DateTimeAdapter.instance.getSecond());
        Assert.assertTrue(true);
    }
}
