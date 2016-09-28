/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 Andrey Novikov
 *
 * This file is part of the OpenScienceMap project (http://www.opensciencemap.org).
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
package org.oscim.utils.pool;

public class LList<T> extends Inlist<LList<T>> {
    public LList(T l) {
        data = l;
    }

    public final T data;

    public static <E extends LList<T>, T> LList<T> find(LList<T> list, T item) {
        for (LList<T> l = list; l != null; l = l.next)
            if (l.data == item)
                return l;

        return null;
    }

    public static <E extends LList<T>, T> LList<T> remove(LList<T> list, T item) {
        if (list.data == item)
            return list.next;

        LList<T> prev = list;
        for (LList<T> l = list.next; l != null; l = l.next) {
            if (l.data == item) {
                prev.next = l.next;
                break;
            }
            prev = l;
        }
        return list;
    }

    public static <E extends LList<T>, T> LList<T> push(LList<T> list, T item) {
        LList<T> prev = new LList<T>(item);
        prev.next = list;
        return prev;
    }
}
