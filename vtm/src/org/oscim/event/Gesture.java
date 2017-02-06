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

public interface Gesture {

    final class Press implements Gesture {
    }

    final class LongPress implements Gesture {
    }

    final class Tap implements Gesture {
    }

    final class DoubleTap implements Gesture {
    }

    final class TripleTap implements Gesture {
    }

    final class TwoFingerTap implements Gesture {
    }

    Gesture PRESS = new Press();
    Gesture LONG_PRESS = new LongPress();
    Gesture TAP = new Tap();
    Gesture DOUBLE_TAP = new DoubleTap();
    Gesture TRIPLE_TAP = new TripleTap();
    Gesture TWO_FINGER_TAP = new TwoFingerTap();
}
