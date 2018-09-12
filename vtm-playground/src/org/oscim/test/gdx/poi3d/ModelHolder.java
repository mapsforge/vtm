/*
 * Copyright 2018 Gustl22
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
package org.oscim.test.gdx.poi3d;

import com.badlogic.gdx.graphics.g3d.Model;

class ModelHolder {
    Model mModel;
    String mPath;

    ModelHolder(String path) {
        mPath = path;
    }

    public Model getModel() {
        return mModel;
    }

    public String getPath() {
        return mPath;
    }

    public void setModel(Model model) {
        mModel = model;
    }
}
