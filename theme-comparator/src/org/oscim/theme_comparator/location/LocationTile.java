
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
package org.oscim.theme_comparator.location;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Tile;
import org.mapsforge.core.util.MercatorProjection;
import org.oscim.theme_comparator.Main;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Longri on 18.08.2017.
 */
public class LocationTile extends JPanel implements LocationDataListener {

    private final JSpinner tileX = new JSpinner();
    private final JSpinner tileY = new JSpinner();
    private final JSpinner tileZ = new JSpinner();
    private final AtomicBoolean blockChangeListener = new AtomicBoolean(false);

    LocationTile() {
        LocationData.addChangeListener(this);

        JPanel labels = new JPanel(new GridLayout(3, 1));
        labels.add(new JLabel("Tile X:", JLabel.RIGHT));
        labels.add(new JLabel("Tile Y:", JLabel.RIGHT));
        labels.add(new JLabel("Tile Z:", JLabel.RIGHT));

        JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));

        p1.add(tileX);
        tileX.setPreferredSize(new Dimension(100, 20));
        {
            JLabel spacer = new JLabel("");
            spacer.setPreferredSize(new Dimension(50, 20));
            p1.add(spacer);
        }

        JPanel p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));

        p2.add(tileY);
        tileY.setPreferredSize(new Dimension(100, 20));
        {
            JLabel spacer = new JLabel("");
            spacer.setPreferredSize(new Dimension(50, 20));
            p2.add(spacer);
        }

        JPanel p3 = new JPanel();
        p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));

        p3.add(tileZ);
        tileZ.setPreferredSize(new Dimension(50, 20));


        ChangeListener tileNumberChangeListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (blockChangeListener.get()) return;
                try {
                    Tile tile = new Tile((int) tileX.getValue(), (int) tileY.getValue(), ((Integer) tileZ.getValue()).byteValue(), 256);
                    LatLong latLon = tile.getBoundingBox().getCenterPoint();

                    LocationData.setLatitude(latLon.latitude);
                    LocationData.setLongitude(latLon.longitude);
                    LocationData.setZoom((Integer) tileZ.getValue());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        };

        ChangeListener zoomChangeListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                LocationData.setZoom((Integer) tileZ.getValue());
            }
        };

        int maxTileNumber = Tile.getMaxTileNumber((byte) Main.MAX_ZOOM_LEVEL);

        tileX.setModel(new SpinnerNumberModel(0, 0, maxTileNumber, 1));
        tileY.setModel(new SpinnerNumberModel(0, 0, maxTileNumber, 1));
        tileZ.setModel(new SpinnerNumberModel(0, Main.MIN_ZOOM_LEVEL, Main.MAX_ZOOM_LEVEL, 1));


        tileX.addChangeListener(tileNumberChangeListener);
        tileY.addChangeListener(tileNumberChangeListener);
        tileZ.addChangeListener(zoomChangeListener);


        this.add(labels);
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        JPanel aroundThis = new JPanel();
        aroundThis.setLayout(new BoxLayout(aroundThis, BoxLayout.Y_AXIS));
        {
            JPanel jPanel = new JPanel();
            jPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            jPanel.add(p1);
            aroundThis.add(jPanel);
        }
        {
            JPanel jPanel = new JPanel();
            jPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            jPanel.add(p2);
            aroundThis.add(jPanel);
        }
        {
            JPanel jPanel = new JPanel();
            jPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            jPanel.add(p3);
            aroundThis.add(jPanel);
        }
        this.add(aroundThis);
    }

    @Override
    public void valueChanged() {
        blockChangeListener.set(true);
        tileX.setValue(MercatorProjection.longitudeToTileX(LocationData.getLongitude(), (byte) LocationData.getZoom()));
        tileY.setValue(MercatorProjection.latitudeToTileY(LocationData.getLatitude(), (byte) LocationData.getZoom()));
        tileZ.setValue(LocationData.getZoom());
        blockChangeListener.set(false);
    }
}
