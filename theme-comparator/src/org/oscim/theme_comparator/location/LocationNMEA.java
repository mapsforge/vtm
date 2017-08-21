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

import org.oscim.theme_comparator.Main;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Longri on 18.08.2017.
 */
public class LocationNMEA extends JPanel implements LocationDataListener {


    private final JSpinner latitude = new JSpinner();
    private final JSpinner longitude = new JSpinner();
    private final JSpinner zoom = new JSpinner();
    private final JComboBox<LocationData.Orientation> ew = new JComboBox<>();
    private final JComboBox<LocationData.Orientation> ns = new JComboBox<>();

    LocationNMEA() {
        LocationData.addChangeListener(this);

        JPanel labels = new JPanel(new GridLayout(3, 1));
        labels.add(new JLabel("Latitude:", JLabel.RIGHT));
        labels.add(new JLabel("Longitude:", JLabel.RIGHT));
        labels.add(new JLabel("Zoom:", JLabel.RIGHT));

        JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));

        p1.add(latitude);
        latitude.setPreferredSize(new Dimension(100, 20));
        p1.add(ns);
        ns.setPreferredSize(new Dimension(80, 20));
        {
            JLabel spacer = new JLabel("");
            spacer.setPreferredSize(new Dimension(50, 20));
            p1.add(spacer);
        }

        JPanel p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));

        p2.add(longitude);
        longitude.setPreferredSize(new Dimension(100, 20));
        p2.add(ew);
        ew.setPreferredSize(new Dimension(80, 20));
        {
            JLabel spacer = new JLabel("");
            spacer.setPreferredSize(new Dimension(50, 20));
            p2.add(spacer);
        }

        ChangeListener latitudeChangeListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                LocationData.setLatitude(nmea2degree((Double) latitude.getValue()));
            }
        };

        latitude.setModel(new SpinnerNumberModel(degree2nmea(LocationData.getLatitude()), 0, 9000, 0.002));
        latitude.addChangeListener(latitudeChangeListener);

        ns.addItem(LocationData.Orientation.NORTH);
        ns.addItem(LocationData.Orientation.SOUTH);
        ns.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LocationData.setNS((LocationData.Orientation) ns.getSelectedItem());
            }
        });

        ChangeListener longitudeChangeListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                LocationData.setLongitude(nmea2degree((Double) longitude.getValue()));
            }
        };

        longitude.setModel(new SpinnerNumberModel(degree2nmea(LocationData.getLongitude()), 0, 18000, 0.002));
        longitude.addChangeListener(longitudeChangeListener);

        ew.addItem(LocationData.Orientation.EAST);
        ew.addItem(LocationData.Orientation.WEST);
        ew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LocationData.setEW((LocationData.Orientation) ew.getSelectedItem());
            }
        });


        JPanel p3 = new JPanel();
        p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));

        p3.add(zoom);
        zoom.setPreferredSize(new Dimension(50, 20));
        zoom.setModel(new SpinnerNumberModel(LocationData.getZoom(), Main.MIN_ZOOM_LEVEL, Main.MAX_ZOOM_LEVEL, 1));
        zoom.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                LocationData.setZoom((int) zoom.getValue());
            }
        });

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
        latitude.setValue(degree2nmea(LocationData.getLatitude()));
        longitude.setValue(degree2nmea(LocationData.getLongitude()));

        ns.setSelectedItem(LocationData.getNS());
        ew.setSelectedItem(LocationData.getEW());

        zoom.setValue(LocationData.getZoom());

    }

    private double degree2nmea(double value) {
        double degree = (int) value;
        double minute = (int) (value * 60 - degree * 60);
        double second = value * 60 - (int) (value * 60);

        return (double) Math.round((degree * 100 + minute + second) * 1000) / 1000;
    }

    private double nmea2degree(double value) {
        int degree = (int) value / 100;
        int minute = (int) (value - degree * 100);
        double second = ((value - (int) value) * 60);

        return degree + ((double) minute / 60) + second / 60 / 60;
    }
}
