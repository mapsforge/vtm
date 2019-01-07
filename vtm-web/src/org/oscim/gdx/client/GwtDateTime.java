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
package org.oscim.gdx.client;

import com.google.gwt.core.client.JsDate;

import org.oscim.backend.DateTimeAdapter;

public class GwtDateTime extends DateTimeAdapter {

    @Override
    public int getHour() {
        return JsDate.create().getHours();
    }

    @Override
    public int getMinute() {
        return JsDate.create().getMinutes();
    }

    @Override
    public int getSecond() {
        return JsDate.create().getSeconds();
    }

    @Override
    public int getDayOfYear() {
        JsDate year = JsDate.create();
        JsDate start = JsDate.create(year.getFullYear(), 0);
        double diff = year.getTime() - start.getTime();
        return (int) (diff / (DateTimeAdapter.MILLIS_PER_DAY)) + 1;
    }

    @Override
    public int getTimeZoneOffset() {
        JsDate date = JsDate.create();
        return -date.getTimezoneOffset() * 60 * 1000;
    }
}
