/*
 * Copyright 2013 Hannes Janetzek
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
package org.oscim.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;

import org.oscim.event.MotionEvent;
import org.oscim.map.Map;

import java.util.Arrays;

public class MotionHandler extends MotionEvent implements InputProcessor {
    private final Map mMap;

    public MotionHandler(Map map) {
        mMap = map;
    }

    int mPointerDown;
    long mDownTime;

    int mType;

    int mPointer;
    int mCurX;
    int mCurY;
    int mPointerX[] = new int[10];
    int mPointerY[] = new int[10];

    @Override
    public int getAction() {
        return mType;
    }

    @Override
    public float getX() {
        return mCurX;
    }

    @Override
    public float getY() {
        return mCurY;
    }

    @Override
    public float getX(int idx) {
        if (idx >= 10)
            return 0;

        return mPointerX[idx];
    }

    @Override
    public float getY(int idx) {
        if (idx >= 10)
            return 0;

        return mPointerY[idx];
    }

    @Override
    public int getPointerCount() {
        return mPointerDown;
    }

    @Override
    public MotionEvent copy() {
        MotionHandler handler = new MotionHandler(mMap);
        handler.mPointerDown = mPointerDown;
        handler.mDownTime = mDownTime;
        handler.mType = mType;
        handler.mPointer = mPointer;
        handler.mCurX = mCurX;
        handler.mCurY = mCurY;
        handler.mPointerX = Arrays.copyOf(mPointerX, 10);
        handler.mPointerY = Arrays.copyOf(mPointerY, 10);
        handler.mTime = mTime;
        return handler;
    }

    @Override
    public void recycle() {
    }

    @Override
    public long getTime() {
        return (long) (mTime / 1000000d);
    }

    // -------- InputProcessor ----------
    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    long mTime = System.currentTimeMillis();

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (pointer >= 10)
            return true;

        if (button != Buttons.LEFT)
            return false;

        mTime = Gdx.input.getCurrentEventTime();
        if (mPointerDown++ == 0) {
            mDownTime = getTime();
            mType = MotionEvent.ACTION_DOWN;
        } else {
            mType = MotionEvent.ACTION_POINTER_DOWN;
        }

        mPointerX[pointer] = mCurX = screenX;
        mPointerY[pointer] = mCurY = screenY;
        mPointer = pointer;
        //GdxMap.log.debug("down " + screenX + ":" + screenY
        //        + " / " + pointer + " " + mPointerDown
        //        + "  " + (getTime() - mDownTime));

        mMap.input.fire(null, this);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (pointer >= 10)
            return true;

        if (button != Buttons.LEFT)
            return false;

        if (mPointerDown == 0)
            return true;

        mTime = Gdx.input.getCurrentEventTime();
        mType = (--mPointerDown == 0) ?
                MotionEvent.ACTION_UP :
                MotionEvent.ACTION_POINTER_UP;

        mPointerX[pointer] = mCurX = screenX;
        mPointerY[pointer] = mCurY = screenY;
        mPointer = pointer;

        //GdxMap.log.debug("up  " + screenX + ":" + screenY
        //        + " / " + pointer + " " + mPointerDown
        //        + "  " + (getTime() - mDownTime));

        mMap.input.fire(null, this);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (pointer >= 10)
            return true;

        mTime = Gdx.input.getCurrentEventTime();
        mType = MotionEvent.ACTION_MOVE;

        mPointerX[pointer] = mCurX = screenX;
        mPointerY[pointer] = mCurY = screenY;
        mPointer = pointer;

        //GdxMap.log.debug("dragged  " + screenX + ":" + screenY
        //        + " / " + pointer + "  " + (getTime() - mDownTime));

        mMap.input.fire(null, this);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mTime = Gdx.input.getCurrentEventTime();
        mType = MotionEvent.ACTION_MOVE;

        mPointerX[Buttons.LEFT] = mCurX = screenX;
        mPointerY[Buttons.LEFT] = mCurY = screenY;
        mPointer = Buttons.LEFT;

        //GdxMap.log.debug("moved " + screenX + ":" + screenY);

        mMap.input.fire(null, this);
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        mTime = Gdx.input.getCurrentEventTime();

        return false;
    }

}
