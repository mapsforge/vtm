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
import org.oscim.theme_comparator.location.LocationData.Orientation;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Longri on 18.08.2017.
 */
public class LocationNormal extends JPanel implements LocationDataListener {

    private final JSpinner latitudeDegree = new JSpinner();
    private final JSpinner longitudeDegree = new JSpinner();
    private final JSpinner latitudeMinute = new JSpinner();
    private final JSpinner longitudeMinute = new JSpinner();
    private final JSpinner latitudeSecond = new JSpinner();
    private final JSpinner longitudeSecond = new JSpinner();
    private final JSpinner zoom = new JSpinner();
    private final JComboBox<Orientation> ew = new JComboBox<>();
    private final JComboBox<Orientation> ns = new JComboBox<>();
    private final AtomicBoolean blockChangeListener = new AtomicBoolean(false);

    LocationNormal() {
        LocationData.addChangeListener(this);

        JPanel labels = new JPanel(new GridLayout(3, 1));
        labels.add(new JLabel("Latitude:", JLabel.RIGHT));
        labels.add(new JLabel("Longitude:", JLabel.RIGHT));
        labels.add(new JLabel("Zoom:", JLabel.RIGHT));

        JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));

        latitudeDegree.setPreferredSize(new Dimension(50, 20));
        p1.add(latitudeDegree);
        latitudeDegree.setPreferredSize(new Dimension(50, 20));
        p1.add(latitudeMinute);
        latitudeMinute.setPreferredSize(new Dimension(50, 20));
        p1.add(latitudeSecond);
        latitudeSecond.setPreferredSize(new Dimension(50, 20));
        p1.add(ns);
        ns.setPreferredSize(new Dimension(80, 20));

        JPanel p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));

        p2.add(longitudeDegree);
        longitudeDegree.setPreferredSize(new Dimension(50, 20));
        p2.add(longitudeMinute);
        longitudeMinute.setPreferredSize(new Dimension(50, 20));
        p2.add(longitudeSecond);
        longitudeSecond.setPreferredSize(new Dimension(50, 20));
        p2.add(ew);
        ew.setPreferredSize(new Dimension(80, 20));

        ChangeListener latitudeChangeListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (blockChangeListener.get()) {
                    double latitude = LocationData.getLatitude();
                    assert latitude >= 0;
                    int lat = (int) Math.round(latitude * 3600);
                    latitudeSecond.setValue(lat % 60);
                    lat = lat / 60;
                    latitudeMinute.setValue(lat % 60);
                    lat = lat / 60;
                    latitudeDegree.setValue(lat);
                } else {
                    Integer degree = (Integer) latitudeDegree.getValue();
                    Integer minute = (Integer) latitudeMinute.getValue();
                    Integer second = (Integer) latitudeSecond.getValue();

                    if (degree == 90) {
                        if (minute == -1 && second == 0) {
                            latitudeSecond.setValue(59);
                        } else if (minute == 0 && second == -1) {
                            latitudeMinute.setValue(59);
                        } else if (minute == -1 && second == 59) {
                            latitudeMinute.setValue(59);
                        } else if (minute == 59 && second == -1) {
                            latitudeSecond.setValue(59);
                        } else if (minute == 59 && second == 59) {
                            latitudeDegree.setValue(89);
                        } else if (minute == 0 && second == 0) {
                            Double tmp = allInOne(degree, minute, second);
                            LocationData.setLatitude(tmp);
                        } else {
                            latitudeSecond.setValue(0);
                            latitudeMinute.setValue(0);
                        }
                    } else if (second == 60) {
                        latitudeSecond.setValue(0);
                        latitudeMinute.setValue((Integer) latitudeMinute.getValue() + 1);
                    } else if (second == -1) {
                        if (minute == 0) {
                            latitudeSecond.setValue(0);
                        } else {
                            latitudeSecond.setValue(59);
                            latitudeMinute.setValue((Integer) latitudeMinute.getValue() - 1);
                        }
                    } else if (minute == 60) {
                        latitudeMinute.setValue(0);
                        latitudeDegree.setValue((Integer) latitudeDegree.getValue() + 1);
                    } else if (minute == -1) {
                        if (degree == 0) {
                            latitudeMinute.setValue(0);
                        } else {
                            latitudeMinute.setValue(59);
                            latitudeDegree.setValue((Integer) latitudeDegree.getValue() - 1);
                        }
                    } else {
                        LocationData.setLatitude(allInOne(degree, minute, second));
                    }
                }
            }
        };

        latitudeDegree.setModel(new SpinnerNumberModel(degree(LocationData.getLatitude()), 0, 90, 1));
        latitudeDegree.addChangeListener(latitudeChangeListener);

        latitudeMinute.setModel(new SpinnerNumberModel(minute(LocationData.getLatitude()), -1, 60, 1));
        latitudeMinute.addChangeListener(latitudeChangeListener);

        latitudeSecond.setModel(new SpinnerNumberModel(second(LocationData.getLatitude()), -1, 60, 1));
        latitudeSecond.addChangeListener(latitudeChangeListener);

        ns.addItem(Orientation.NORTH);
        ns.addItem(Orientation.SOUTH);
        ns.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LocationData.setNS((Orientation) ns.getSelectedItem());
            }
        });

        ChangeListener longitudeChangeListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (blockChangeListener.get()) {
                    double longitude = LocationData.getLongitude();
                    assert longitude >= 0;
                    int lng = (int) Math.round(longitude * 3600);
                    longitudeSecond.setValue(lng % 60);
                    lng = lng / 60;
                    longitudeMinute.setValue(lng % 60);
                    lng = lng / 60;
                    longitudeDegree.setValue(lng);
                } else {
                    Integer degree = (Integer) longitudeDegree.getValue();
                    Integer minute = (Integer) longitudeMinute.getValue();
                    Integer second = (Integer) longitudeSecond.getValue();

                    if (degree == 180) {
                        if (minute == -1 && second == 0) {
                            longitudeSecond.setValue(59);
                        } else if (minute == 0 && second == -1) {
                            longitudeMinute.setValue(59);
                        } else if (minute == -1 && second == 59) {
                            longitudeMinute.setValue(59);
                        } else if (minute == 59 && second == -1) {
                            longitudeSecond.setValue(59);
                        } else if (minute == 59 && second == 59) {
                            longitudeDegree.setValue(179);
                        } else if (minute == 0 && second == 0) {
                            Double tmp = allInOne(degree, minute, second);
                            LocationData.setLongitude(tmp);
                        } else {
                            longitudeSecond.setValue(0);
                            longitudeMinute.setValue(0);
                        }
                    } else if (second == 60) {
                        longitudeSecond.setValue(0);
                        longitudeMinute.setValue((Integer) longitudeMinute.getValue() + 1);
                    } else if (second == -1) {
                        if (minute == 0) {
                            longitudeSecond.setValue(0);
                        } else {
                            longitudeSecond.setValue(59);
                            longitudeMinute.setValue((Integer) longitudeMinute.getValue() - 1);
                        }
                    } else if (minute == 60) {
                        longitudeMinute.setValue(0);
                        longitudeDegree.setValue((Integer) longitudeDegree.getValue() + 1);
                    } else if (minute == -1) {
                        if (degree == 0) {
                            longitudeMinute.setValue(0);
                        } else {
                            longitudeMinute.setValue(59);
                            longitudeDegree.setValue((Integer) longitudeDegree.getValue() - 1);
                        }
                    } else {
                        LocationData.setLongitude(allInOne(degree, minute, second));
                    }
                }
            }
        };

        longitudeDegree.setModel(new SpinnerNumberModel(degree(LocationData.getLongitude()), 0, 180, 1));
        longitudeDegree.addChangeListener(longitudeChangeListener);

        longitudeMinute.setModel(new SpinnerNumberModel(minute(LocationData.getLongitude()), -1, 60, 1));
        longitudeMinute.addChangeListener(longitudeChangeListener);

        longitudeSecond.setModel(new SpinnerNumberModel(second(LocationData.getLongitude()), -1, 60, 1));
        longitudeSecond.addChangeListener(longitudeChangeListener);

        ew.addItem(Orientation.EAST);
        ew.addItem(Orientation.WEST);
        ew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LocationData.setEW((Orientation) ew.getSelectedItem());
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
                if (blockChangeListener.get()) return;
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
        blockChangeListener.set(true);
        latitudeDegree.setValue(degree(LocationData.getLatitude()));
        longitudeDegree.setValue(degree(LocationData.getLongitude()));

        latitudeMinute.setValue(minute(LocationData.getLatitude()));
        longitudeMinute.setValue(minute(LocationData.getLongitude()));

        latitudeSecond.setValue(second(LocationData.getLatitude()));
        longitudeSecond.setValue(second(LocationData.getLongitude()));

        ns.setSelectedItem(LocationData.getNS());
        ew.setSelectedItem(LocationData.getEW());

        zoom.setValue(LocationData.getZoom());

        blockChangeListener.set(false);
    }

    private double allInOne(int degree, int minute, int second) {
        return degree + ((double) minute / 60) + (double) second / 60 / 60;
    }

    private int degree(double value) {
        return (int) (Math.round(value * 3600) / 3600);
    }

    private int minute(double value) {
        return (int) (Math.round(value * 3600) / 60 % 60);
    }

    private int second(double value) {
        return (int) (Math.round(value * 3600) % 60);
    }

}
