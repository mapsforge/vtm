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

import java.util.Calendar;

public class DateTime extends DateTimeAdapter {
    private Calendar cal;

    public DateTime() {
        cal = Calendar.getInstance();
    }

    public void setCalendar(Calendar cal) {
        this.cal = cal;
    }

    @Override
    public int getHour() {
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    @Override
    public int getMinute() {
        return cal.get(Calendar.MINUTE);
    }

    @Override
    public int getSecond() {
        return cal.get(Calendar.SECOND);
    }

    @Override
    public int getDayOfYear() {
        return cal.get(Calendar.DAY_OF_YEAR);
    }

    @Override
    public int getTimeZoneOffset() {
        return cal.getTimeZone().getOffset(cal.getTimeInMillis());
    }
}
