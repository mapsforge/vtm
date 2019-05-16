/*
 * Copyright 2016 devemux86
 * Copyright 2019 Izumi Kawashima
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
package org.oscim.utils;

import org.junit.Assert;
import org.junit.Test;
import org.oscim.core.Box;
import org.oscim.core.Point;
import org.oscim.utils.SpatialIndex.SearchCb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class RTreeTest {

    public class Item {
        final int val;
        final double[] min;
        final double[] max;

        Item(final double[] min, final double[] max, int val) {
            this.val = val;
            this.min = min.clone();
            this.max = max.clone();
        }

        Item(int xmin, int ymin, int xmax, int ymax, int val) {
            this.val = val;
            this.min = new double[]{xmin, ymin};
            this.max = new double[]{xmax, ymax};
        }

        @Override
        public String toString() {
//            return val + "/"
//                    + Arrays.toString(min) + "/"
//                    + Arrays.toString(max);
            return String.valueOf(val);
        }
    }

    @Test
    public void shouldRemove() {
        RTree<Item> t = new RTree<Item>();
        double[] min = {0, 0};
        double[] max = {1, 1};
        Item it = new Item(min, max, 1);
        t.insert(min, max, it);
        Assert.assertEquals(1, t.size());
        t.remove(min, max, it);
        Assert.assertEquals(0, t.size());
    }

    @Test
    public void shouldWork() {
        RTree<Item> t = new RTree<Item>();
        double[] min = {0, 0};
        double[] max = {1, 1};
        for (int i = 0; i < 1000; i++) {

            Item it = new Item(min, max, i);
            //out.println("insert: " + it.val);
            t.insert(min, max, it);

            min[0]++;
            min[1]++;

            max[0]++;
            max[1]++;

        }
        Assert.assertEquals(1000, t.size());

        min[0] = 0;
        min[1] = 0;
        //        max[0] = 4;
        //        max[1] = 4;

        final ArrayList<Item> results = new ArrayList<Item>();

        t.search(min, max, new SearchCb<RTreeTest.Item>() {
                    @Override
                    public boolean call(Item item, Object context) {
                        //out.println("found: " + item);
                        results.add(item);
                        return true;
                    }
                },
                null);

        for (int i = 999; i >= 0; i--) {
            Item it = results.remove(i);
            //boolean removed =
            t.remove(it.min, it.max, it);
            //out.println("REMOVED: " + it + " " + removed);

            Assert.assertEquals(i, t.size());
        }
        Assert.assertEquals(0, t.size());
    }

    @Test
    public void shouldStack1() {
        RTree<Item> t = new RTree<Item>();
        double[] min = {0, 0};
        double[] max = {1, 1};

        int numItems = 10000;
        for (int i = 0; i < numItems; i++) {

            Item it = new Item(min, max, i);
            //out.println("insert: " + it.val);
            t.insert(min, max, it);

            min[0]++;
            min[1]++;

            max[0]++;
            max[1]++;

        }
        assertEquals(numItems, t.size());

        min[0] = 0;
        min[1] = 0;

        final ArrayList<Item> results = new ArrayList<Item>();

        t.search(min, max, new SearchCb<RTreeTest.Item>() {
            @Override
            public boolean call(Item item, Object context) {
                //out.println("found: " + item);
                results.add(item);
                return true;
            }
        }, null);

        assertEquals(results.size(), numItems);

        //        for (int i = 999; i >= 0; i--) {
        //            Item it = results.remove(i);
        //            boolean removed = t.remove(it.min, it.max, it);
        //            //out.println("REMOVED: " + it + " " + removed);
        //
        //            Assert.assertEquals(i, t.count());
        //        }
        //        Assert.assertEquals(0, t.count());
    }

    @Test
    public void shouldStack2() {
        RTree<Item> t = new RTree<Item>();
        double[] min = {0, 0};
        double[] max = {1, 1};

        int numItems = 10000;
        for (int i = 0; i < numItems; i++) {

            Item it = new Item(min, max, i);
            //out.println("insert: " + it.val);
            t.insert(min, max, it);

            min[0]++;
            min[1]++;

            max[0]++;
            max[1]++;

        }
        assertEquals(numItems, t.size());

        min[0] = 0;
        min[1] = 0;

        final ArrayList<Item> results = new ArrayList<Item>();

        Box bbox = new Box(min[0], min[1], max[0], max[1]);

        t.search(bbox, results);

        assertEquals(numItems, results.size());

        //        for (int i = 999; i >= 0; i--) {
        //            Item it = results.remove(i);
        //            boolean removed = t.remove(it.min, it.max, it);
        //            //out.println("REMOVED: " + it + " " + removed);
        //
        //            Assert.assertEquals(i, t.count());
        //        }
        //        Assert.assertEquals(0, t.count());
    }

    @Test
    public void shouldWork0() {
        RTree<Item> t = new RTree<Item>();
        double[] min = {0, 0};
        double[] max = {0, 0};
        Random r = new Random((long) (Math.PI * 10000000));
        ArrayList<Item> items = new ArrayList<Item>();

        for (int i = 0; i < 10000; i++) {
            min[0] = r.nextDouble() * 10000 - 5000;
            min[1] = Math.random() * 10000 - 5000;
            max[0] = min[0] + Math.random() * 100;
            max[1] = min[1] + Math.random() * 100;

            Item it = new Item(min, max, i);
            t.insert(min, max, it);
            items.add(it);
        }

        Assert.assertEquals(10000, t.size());

        /*SearchCb<RTreeTest.Item> cb = new SearchCb<RTreeTest.Item>() {
            @Override
            public boolean call(Item item, Object context) {
                //out.println("found: " + item);
                //results.add(item);
                return true;
            }
        };

        int counter = 0;

        for (int i = 0; i < 10000; i++) {
            counter += t.search(min, max, cb, null);
        }

        System.out.println("found: " + counter);*/
    }

    @Test
    public void shouldWork1() {
        RTree<Item> t = new RTree<Item>();
        double[] min = {0, 0};
        double[] max = {0, 0};

        ArrayList<Item> items = new ArrayList<Item>();

        for (int i = 0; i < 10000; i++) {
            min[0] = Math.random() * 10000 - 5000;
            min[1] = Math.random() * 10000 - 5000;
            max[0] = min[0] + Math.random() * 1000;
            max[1] = min[1] + Math.random() * 1000;

            Item it = new Item(min, max, i);
            t.insert(min, max, it);
            items.add(it);
        }

        Assert.assertEquals(10000, t.size());

        for (Item it : items)
            t.remove(it.min, it.max, it);

        Assert.assertEquals(0, t.size());
    }

    @Test
    public void shouldWork2() {
        RTree<Item> t = new RTree<Item>();
        double[] min = {0, 0};
        double[] max = {0, 0};

        ArrayList<Item> items = new ArrayList<Item>();

        int numItems = 10000;
        for (int i = 0; i < numItems; i++) {
            min[0] = Math.random() * 10000 - 5000;
            min[1] = Math.random() * 10000 - 5000;
            max[0] = min[0] + Math.random() * 1000;
            max[1] = min[1] + Math.random() * 1000;

            Item it = new Item(min, max, i);
            t.insert(min, max, it);
            items.add(it);
        }

        Assert.assertEquals(numItems, t.size());

        for (Item it : items)
            t.remove(it.min, it.max, it);

        Assert.assertEquals(0, t.size());
    }

    @Test
    public void shouldWork3() {
        RTree<Item> t = new RTree<Item>();
        double[] min = {0, 0};
        double[] max = {0, 0};

        ArrayList<Item> items = new ArrayList<Item>();

        for (int i = 0; i < 1000; i++) {
            min[0] = Math.random() * 10000 - 5000;
            min[1] = Math.random() * 10000 - 5000;
            max[0] = min[0] + Math.random() * 1000;
            max[1] = min[1] + Math.random() * 1000;

            Item it = new Item(min, max, i);
            t.insert(min, max, it);
            items.add(it);
        }

        int cnt = 0;

        for (@SuppressWarnings("unused")
                Item it : t) {
            //System.out.println(it.val);
            cnt++;
        }

        Assert.assertEquals(1000, cnt);

        Assert.assertEquals(1000, t.size());

    }

    private ArrayList<Item> generateKnnTestFixture() {
        // Use values from https://github.com/mourner/rbush-knn/blob/master/test.js

        ArrayList<Item> items = new ArrayList<Item>();

        items.add(new Item(87, 55, 87, 56, items.size()));
        items.add(new Item(38, 13, 39, 16, items.size()));
        items.add(new Item(7, 47, 8, 47, items.size()));
        items.add(new Item(89, 9, 91, 12, items.size()));
        items.add(new Item(4, 58, 5, 60, items.size()));
        items.add(new Item(0, 11, 1, 12, items.size()));
        items.add(new Item(0, 5, 0, 6, items.size()));
        items.add(new Item(69, 78, 73, 78, items.size()));

        items.add(new Item(56, 77, 57, 81, items.size()));
        items.add(new Item(23, 7, 24, 9, items.size()));
        items.add(new Item(68, 24, 70, 26, items.size()));
        items.add(new Item(31, 47, 33, 50, items.size()));
        items.add(new Item(11, 13, 14, 15, items.size()));
        items.add(new Item(1, 80, 1, 80, items.size()));
        items.add(new Item(72, 90, 72, 91, items.size()));
        items.add(new Item(59, 79, 61, 83, items.size()));

        items.add(new Item(98, 77, 101, 77, items.size()));
        items.add(new Item(11, 55, 14, 56, items.size()));
        items.add(new Item(98, 4, 100, 6, items.size()));
        items.add(new Item(21, 54, 23, 58, items.size()));
        items.add(new Item(44, 74, 48, 74, items.size()));
        items.add(new Item(70, 57, 70, 61, items.size()));
        items.add(new Item(32, 9, 33, 12, items.size()));
        items.add(new Item(43, 87, 44, 91, items.size()));

        items.add(new Item(38, 60, 38, 60, items.size()));
        items.add(new Item(62, 48, 66, 50, items.size()));
        items.add(new Item(16, 87, 19, 91, items.size()));
        items.add(new Item(5, 98, 9, 99, items.size()));
        items.add(new Item(9, 89, 10, 90, items.size()));
        items.add(new Item(89, 2, 92, 6, items.size()));
        items.add(new Item(41, 95, 45, 98, items.size()));
        items.add(new Item(57, 36, 61, 40, items.size()));

        items.add(new Item(50, 1, 52, 1, items.size()));
        items.add(new Item(93, 87, 96, 88, items.size()));
        items.add(new Item(29, 42, 33, 42, items.size()));
        items.add(new Item(34, 43, 36, 44, items.size()));
        items.add(new Item(41, 64, 42, 65, items.size()));
        items.add(new Item(87, 3, 88, 4, items.size()));
        items.add(new Item(56, 50, 56, 52, items.size()));
        items.add(new Item(32, 13, 35, 15, items.size()));

        items.add(new Item(3, 8, 5, 11, items.size()));
        items.add(new Item(16, 33, 18, 33, items.size()));
        items.add(new Item(35, 39, 38, 40, items.size()));
        items.add(new Item(74, 54, 78, 56, items.size()));
        items.add(new Item(92, 87, 95, 90, items.size()));
        items.add(new Item(12, 97, 16, 98, items.size()));
        items.add(new Item(76, 39, 78, 40, items.size()));
        items.add(new Item(16, 93, 18, 95, items.size()));

        items.add(new Item(62, 40, 64, 42, items.size()));
        items.add(new Item(71, 87, 71, 88, items.size()));
        items.add(new Item(60, 85, 63, 86, items.size()));
        items.add(new Item(39, 52, 39, 56, items.size()));
        items.add(new Item(15, 18, 19, 18, items.size()));
        items.add(new Item(91, 62, 94, 63, items.size()));
        items.add(new Item(10, 16, 10, 18, items.size()));
        items.add(new Item(5, 86, 8, 87, items.size()));

        items.add(new Item(85, 85, 88, 86, items.size()));
        items.add(new Item(44, 84, 44, 88, items.size()));
        items.add(new Item(3, 94, 3, 97, items.size()));
        items.add(new Item(79, 74, 81, 78, items.size()));
        items.add(new Item(21, 63, 24, 66, items.size()));
        items.add(new Item(16, 22, 16, 22, items.size()));
        items.add(new Item(68, 97, 72, 97, items.size()));
        items.add(new Item(39, 65, 42, 65, items.size()));

        items.add(new Item(51, 68, 52, 69, items.size()));
        items.add(new Item(61, 38, 61, 42, items.size()));
        items.add(new Item(31, 65, 31, 65, items.size()));
        items.add(new Item(16, 6, 19, 6, items.size()));
        items.add(new Item(66, 39, 66, 41, items.size()));
        items.add(new Item(57, 32, 59, 35, items.size()));
        items.add(new Item(54, 80, 58, 84, items.size()));
        items.add(new Item(5, 67, 7, 71, items.size()));

        items.add(new Item(49, 96, 51, 98, items.size()));
        items.add(new Item(29, 45, 31, 47, items.size()));
        items.add(new Item(31, 72, 33, 74, items.size()));
        items.add(new Item(94, 25, 95, 26, items.size()));
        items.add(new Item(14, 7, 18, 8, items.size()));
        items.add(new Item(29, 0, 31, 1, items.size()));
        items.add(new Item(48, 38, 48, 40, items.size()));
        items.add(new Item(34, 29, 34, 32, items.size()));

        items.add(new Item(99, 21, 100, 25, items.size()));
        items.add(new Item(79, 3, 79, 4, items.size()));
        items.add(new Item(87, 1, 87, 5, items.size()));
        items.add(new Item(9, 77, 9, 81, items.size()));
        items.add(new Item(23, 25, 25, 29, items.size()));
        items.add(new Item(83, 48, 86, 51, items.size()));
        items.add(new Item(79, 94, 79, 95, items.size()));
        items.add(new Item(33, 95, 33, 99, items.size()));

        items.add(new Item(1, 14, 1, 14, items.size()));
        items.add(new Item(33, 77, 34, 77, items.size()));
        items.add(new Item(94, 56, 98, 59, items.size()));
        items.add(new Item(75, 25, 78, 26, items.size()));
        items.add(new Item(17, 73, 20, 74, items.size()));
        items.add(new Item(11, 3, 12, 4, items.size()));
        items.add(new Item(45, 12, 47, 12, items.size()));
        items.add(new Item(38, 39, 39, 39, items.size()));

        items.add(new Item(99, 3, 103, 5, items.size()));
        items.add(new Item(41, 92, 44, 96, items.size()));
        items.add(new Item(79, 40, 79, 41, items.size()));
        items.add(new Item(29, 2, 29, 4, items.size()));

        return items;
    }

    @Test
    public void shouldWorkKnn0() {
        ArrayList<Item> items = generateKnnTestFixture();

        RTree<Item> t = new RTree<Item>();
        for (Item item : items) {
            t.insert(item.min, item.max, item);
        }

        List<Item> result = t.searchKNearestNeighbors(new Point(40, 40), 10, null);
        Assert.assertEquals(10, result.size());

        result = t.searchKNearestNeighbors(new Point(40, 60), 90, result);
        Assert.assertEquals(90, result.size());
    }

    public static void main(String[] args) {
        RTreeTest t = new RTreeTest();
        t.shouldWork2();
    }
}
