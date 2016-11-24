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
package org.oscim.android.input;

import org.oscim.event.MotionEvent;

public class AndroidMotionEvent extends MotionEvent {

    private android.view.MotionEvent mEvent;

    public MotionEvent wrap(android.view.MotionEvent e) {
        mEvent = android.view.MotionEvent.obtain(e);
        return this;
    }

    @Override
    public int getAction() {
        return mEvent.getAction();
    }

    @Override
    public float getX() {
        return mEvent.getX();
    }

    @Override
    public float getY() {
        return mEvent.getY();
    }

    @Override
    public float getX(int pointer) {
        return mEvent.getX(pointer);
    }

    @Override
    public float getY(int pointer) {
        return mEvent.getY(pointer);
    }

    @Override
    public int getPointerCount() {
        return mEvent.getPointerCount();
    }

    @Override
    public MotionEvent copy() {
        return new AndroidMotionEvent().wrap(mEvent);
    }

    @Override
    public void recycle() {
        mEvent.recycle();
    }

    @Override
    public long getTime() {
        return mEvent.getEventTime();
    }

}
