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
package org.oscim.event;

import org.oscim.utils.pool.LList;

/**
 * The Class EventDispatcher.
 * <p/>
 * Events MUST be dispatched from main-loop! To add events from other
 * threads use:
 * Map.post(new Runnable(){ public void run(tell(event,data);)};);
 *
 * @param <T> the event source type
 * @param <E> the event 'data' type
 */
public abstract class EventDispatcher<E extends EventListener, T> {

    /**
     * The list of listeners.
     */
    protected LList<E> mListeners;

    /**
     * Bind listener for event notifications.
     */
    public void bind(E listener) {
        if (LList.find(mListeners, listener) != null) {
            return;
        }
        mListeners = LList.push(mListeners, listener);
    }

    /**
     * Remove listener.
     */
    public void unbind(E listener) {
        mListeners = LList.remove(mListeners, listener);
    }

    /**
     * Tell listeners whats going on.
     *
     * @param event the event
     * @param data  the data
     */
    public abstract void tell(E listener, Event event, T data);

    public void fire(Event event, T data) {
        for (LList<E> l = mListeners; l != null; l = l.next) {
            tell(l.data, event, data);
        }
    }
}
