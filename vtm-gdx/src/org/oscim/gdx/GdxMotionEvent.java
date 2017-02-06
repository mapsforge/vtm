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
package org.oscim.gdx;

import com.badlogic.gdx.Input;

import org.oscim.event.MotionEvent;

public class GdxMotionEvent extends MotionEvent {

    private final int action;
    private final float x, y;
    private final int button;

    public GdxMotionEvent(int action, float x, float y) {
        this(action, x, y, Input.Buttons.LEFT);
    }

    public GdxMotionEvent(int action, float x, float y, int button) {
        this.action = action;
        this.x = x;
        this.y = y;
        this.button = button;
    }

    public int getButton() {
        return button;
    }

    @Override
    public int getAction() {
        return action;
    }

    @Override
    public int getPointerCount() {
        return 0;
    }

    @Override
    public MotionEvent copy() {
        return new GdxMotionEvent(action, x, y, button);
    }

    @Override
    public void recycle() {
    }

    @Override
    public long getTime() {
        return 0;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getX(int idx) {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getY(int idx) {
        return y;
    }
}
