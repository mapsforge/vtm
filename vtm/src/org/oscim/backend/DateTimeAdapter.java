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

public abstract class DateTimeAdapter {

    public static final long MILLIS_PER_DAY = 86400000L;

    /**
     * The instance provided by backend
     */
    public static DateTimeAdapter instance;

    public static void init(DateTimeAdapter adapter) {
        DateTimeAdapter.instance = adapter;
    }

    public abstract int getHour();

    public abstract int getMinute();

    public abstract int getSecond();

    /**
     * Indicates the day number within the current year. The first day of the year has value 1.
     */
    public abstract int getDayOfYear();

    /**
     * @return the time zone offset in minutes
     */
    public abstract int getTimeZoneOffset();
}
