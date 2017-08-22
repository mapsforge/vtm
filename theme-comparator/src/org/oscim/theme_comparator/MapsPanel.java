/*
 * Copyright 2017 Longri
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
package org.oscim.theme_comparator;

import org.oscim.theme_comparator.mapsforge.MapsforgeMapPanel;
import org.oscim.theme_comparator.vtm.VtmPanel;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Created by Longri on 16.08.2017.
 */
class MapsPanel extends JPanel {

    private final VtmPanel vtmPanel;
    private final MapsforgeMapPanel mapsforgeMapPanel;

    MapsPanel(VtmPanel vtm, MapsforgeMapPanel map) {
        this.setLayout(null);
        vtmPanel = vtm;
        mapsforgeMapPanel = map;
        this.add(vtmPanel);
        this.add(mapsforgeMapPanel);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int halfWidth = getWidth() / 2;
                vtmPanel.setBounds(0, 0, halfWidth, getHeight());
                mapsforgeMapPanel.setBounds(halfWidth, 0, halfWidth, getHeight());
            }
        });
    }
}
