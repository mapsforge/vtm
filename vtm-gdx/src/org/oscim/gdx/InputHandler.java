/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2017 devemux86
 * Copyright 2017 Longri
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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;

import org.oscim.layers.GenericLayer;
import org.oscim.layers.GroupLayer;
import org.oscim.layers.Layer;
import org.oscim.layers.TileGridLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.map.Map;
import org.oscim.map.ViewController;
import org.oscim.theme.VtmThemes;
import org.oscim.utils.animation.Easing;

import java.util.List;

public class InputHandler implements InputProcessor {

    private ViewController mViewport;
    private final Map mMap;
    private GenericLayer mGridLayer;
    private final GdxMap mGdxApp;

    public InputHandler(GdxMap map) {
        mMap = map.getMap();
        mViewport = mMap.viewport();
        mGdxApp = map;
    }

    private boolean mActiveScale;
    private boolean mActiveTilt;
    private boolean mActiveRotate;

    private int mPosX, mPosY;

    @Override
    public boolean keyDown(int keycode) {
        if (mGdxApp.onKeyDown(keycode))
            return true;

        switch (keycode) {
            case Input.Keys.ESCAPE:
                Gdx.app.exit();
                break;

            case Input.Keys.SHIFT_LEFT:
            case Input.Keys.SHIFT_RIGHT:
                mActiveScale = true;
                mPosY = Gdx.input.getY();
                break;

            case Input.Keys.CONTROL_LEFT:
            case Input.Keys.CONTROL_RIGHT:
                mActiveRotate = true;
                mActiveTilt = true;
                mPosX = Gdx.input.getX();
                mPosY = Gdx.input.getY();
                break;

            case Input.Keys.UP:
                mViewport.moveMap(0, -50);
                mMap.updateMap(true);
                break;
            case Input.Keys.DOWN:
                mViewport.moveMap(0, 50);
                mMap.updateMap(true);
                break;
            case Input.Keys.LEFT:
                mViewport.moveMap(-50, 0);
                mMap.updateMap(true);
                break;
            case Input.Keys.RIGHT:
                mViewport.moveMap(50, 0);
                mMap.updateMap(true);
                break;
            case Input.Keys.D:
                mViewport.scaleMap(1.05f, 0, 0);
                mMap.updateMap(true);
                break;
            case Input.Keys.A:
                mViewport.scaleMap(0.95f, 0, 0);
                mMap.updateMap(true);
                break;
            case Input.Keys.S:
                mMap.animator().animateZoom(500, 0.5, 0, 0);
                mMap.updateMap(false);
                break;
            case Input.Keys.W:
                mMap.animator().animateZoom(500, 2, 0, 0);
                mMap.updateMap(false);
                break;

            case Input.Keys.NUM_1:
                mMap.setTheme(VtmThemes.DEFAULT);
                mMap.updateMap(false);
                break;

            case Input.Keys.NUM_2:
                mMap.setTheme(VtmThemes.OSMARENDER);
                mMap.updateMap(false);
                break;

            case Input.Keys.NUM_3:
                mMap.setTheme(VtmThemes.OSMAGRAY);
                mMap.updateMap(false);
                break;

            case Input.Keys.NUM_4:
                mMap.setTheme(VtmThemes.TRONRENDER);
                mMap.updateMap(false);
                break;

            case Input.Keys.NUM_5:
                mMap.setTheme(VtmThemes.NEWTRON);
                mMap.updateMap(false);
                break;

            case Input.Keys.G:
                if (mGridLayer == null) {
                    mGridLayer = new TileGridLayer(mMap);
                    mGridLayer.setEnabled(true);
                    mMap.layers().add(mGridLayer);
                } else {
                    if (mGridLayer.isEnabled()) {
                        mGridLayer.setEnabled(false);
                        mMap.layers().remove(mGridLayer);
                    } else {
                        mGridLayer.setEnabled(true);
                        mMap.layers().add(mGridLayer);
                    }
                }
                mMap.render();
                break;

            case Input.Keys.B:
                toggleBuildingLayer(mMap.layers());
                mMap.render();
                break;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.SHIFT_LEFT:
            case Input.Keys.SHIFT_RIGHT:
                mActiveScale = false;
                break;
            case Input.Keys.CONTROL_LEFT:
            case Input.Keys.CONTROL_RIGHT:
                mActiveRotate = false;
                mActiveTilt = false;
                break;

        }

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Buttons.MIDDLE) {
            mActiveScale = true;
            mPosY = screenY;
        } else if (button == Buttons.RIGHT) {
            mActiveRotate = true;
            mPosX = screenX;
            mPosY = screenY;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        mActiveScale = false;
        mActiveRotate = false;
        mActiveTilt = false;

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        boolean changed = false;

        if (!(mActiveScale || mActiveRotate || mActiveTilt))
            return false;

        if (mActiveTilt) {
            changed = mViewport.tiltMap((screenY - mPosY) / 5f);
            mPosY = screenY;

        }

        if (mActiveScale) {
            changed = mViewport.scaleMap(1 - (screenY - mPosY) / 100f, 0, 0);
            mPosY = screenY;
        }

        if (mActiveRotate) {
            mViewport.rotateMap((screenX - mPosX) / 500f, 0, 0);
            mPosX = screenX;
            mViewport.tiltMap((screenY - mPosY) / 10f);
            mPosY = screenY;
            changed = true;
        }

        if (changed) {
            mMap.updateMap(true);
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mPosX = screenX;
        mPosY = screenY;
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        float fx = mPosX - mMap.getWidth() / 2;
        float fy = mPosY - mMap.getHeight() / 2;
        mMap.animator().animateZoom(250, amount > 0 ? 0.75f : 1.333f, fx, fy, Easing.Type.LINEAR);
        mMap.updateMap(false);
        return true;
    }

    private boolean toggleBuildingLayer(List<Layer> layers) {
        for (Layer layer : layers) {
            if (layer instanceof BuildingLayer) {
                layer.setEnabled(!layer.isEnabled());
                return true;
            } else if (layer instanceof GroupLayer) {
                if (toggleBuildingLayer(((GroupLayer) layer).layers)) {
                    return true;
                }
            }
        }
        return false;
    }
}
