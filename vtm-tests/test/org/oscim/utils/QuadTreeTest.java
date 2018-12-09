package org.oscim.utils;

import org.junit.Assert;
import org.junit.Test;
import org.oscim.core.Box;
import org.oscim.utils.SpatialIndex.SearchCb;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.out;

public class QuadTreeTest {
    static final Random rand = new Random((long) (Math.PI * 10000000));

    public class Item {
        final int val;
        final Box bbox;
        ;

        Item(Box bbox, int val) {
            this.val = val;
            this.bbox = new Box(bbox);
        }

        @Override
        public String toString() {
            return String.valueOf(val) + ' ' + bbox;
        }
    }

    ArrayList<Item> fillRandomTree(SpatialIndex<Item> q, int numItems) {
        Box box = new Box();
        ArrayList<Item> items = new ArrayList<Item>(numItems + 16);

        for (int i = 0; i < numItems; i++) {
            box.xmin = (int) (rand.nextDouble() * 10000 - 5000);
            box.ymin = (int) (rand.nextDouble() * 10000 - 5000);
            box.xmax = (int) (box.xmin + rand.nextDouble() * 500);
            box.ymax = (int) (box.ymin + rand.nextDouble() * 500);

            Item it = new Item(box, i);
            q.insert(box, it);

            items.add(it);

        }
        return items;
    }

    @Test
    public void shouldWork0() {

        SpatialIndex<Item> q = new QuadTree<Item>(Short.MAX_VALUE + 1, 16);
        //SpatialIndex<Item> q = new RTree<Item>();

        int numItems = 10000;

        List<Item> items = fillRandomTree(q, numItems);

        final int[] found = {0};
        final int[] matched = {0};

        for (Item it : items) {
            int f = matched[0];
            q.search(it.bbox, new SearchCb<Item>() {
                @Override
                public boolean call(Item item, Object context) {
                    found[0]++;
                    if (context == item) {
                        matched[0]++;
                        return false;
                    }
                    return true;
                }
            }, it);

            if (f == matched[0])
                out.println((it.bbox.xmax - it.bbox.xmin)
                        + " x " + (it.bbox.ymax - it.bbox.ymin)
                        + " ==> " + it);
        }

        //out.println("m:" + matched[0] + "  f:" + found[0]);
        Assert.assertEquals(numItems, matched[0]);
        Assert.assertEquals(numItems, q.size());
    }

    @Test
    public void shouldWork1() {
        long time = currentTimeMillis();

        for (int i = 0; i < 10; i++) {
            shouldWork0();
        }

        long now = currentTimeMillis();
        out.println("==>" + (now - time) / 10.0f + "ms");
    }

    @Test
    public void shouldWork6() {
        SpatialIndex<Item> q = new QuadTree<Item>(Short.MAX_VALUE + 1, 16);

        Box box = new Box(-4184.653317773969,
                3183.6174297948446,
                -4088.3197324911957,
                3222.7770427421046);

        Item it = new Item(box, 1);
        q.insert(box, it);

        q.search(it.bbox, new SearchCb<Item>() {
            @Override
            public boolean call(Item item, Object context) {
                out.println("==> " + item + " " + (context == item));
                return true;
            }
        }, it);
        Assert.assertEquals(1, q.size());
    }

    @Test
    public void shouldWork7() {
        SpatialIndex<Item> q = new QuadTree<Item>(Short.MAX_VALUE + 1, 14);
        //SpatialIndex<Item> q = new RTree<Item>();

        int numItems = 10000;

        List<Item> items = fillRandomTree(q, numItems);

        Assert.assertEquals(numItems, q.size());

        int cnt = numItems;
        for (Item it : items) {
            if (!q.remove(it.bbox, it)) {
                out.println((it.bbox.xmax - it.bbox.xmin)
                        + " x " + (it.bbox.ymax - it.bbox.ymin)
                        + " ==> " + it);

                q.search(it.bbox, new SearchCb<Item>() {
                    @Override
                    public boolean call(Item item, Object context) {
                        if (context == item) {
                            out.println("found...");
                            return false;
                        }
                        return true;
                    }
                }, it);
            }
            Assert.assertEquals(--cnt, q.size());
        }

        items = fillRandomTree(q, numItems);

        Assert.assertEquals(numItems, q.size());

        cnt = numItems;
        for (Item it : items) {
            if (!q.remove(it.bbox, it))
                out.println((it.bbox.xmax - it.bbox.xmin)
                        + " x " + (it.bbox.ymax - it.bbox.ymin)
                        + " => " + it);

            Assert.assertEquals(--cnt, q.size());
        }
        Assert.assertEquals(0, q.size());
        out.println("");
    }

    @Test
    public void shouldWork8() {

        SpatialIndex<Item> q = new QuadTree<Item>(Short.MAX_VALUE + 1, 16);
        //SpatialIndex<Item> q = new RTree<Item>();

        int numItems = 10000;

        List<Item> items = fillRandomTree(q, numItems);

        final int[] found = {0};
        final int[] matched = {0};

        for (Item it : items) {
            int f = matched[0];
            int cnt = 0;
            for (Item it2 : items) {
                if (it2.bbox.overlap(it.bbox))
                    cnt++;
            }
            found[0] = 0;
            q.search(it.bbox, new SearchCb<Item>() {
                @Override
                public boolean call(Item item, Object context) {
                    found[0]++;
                    if (context == item) {
                        matched[0]++;
                        //return false;
                    }
                    return true;
                }
            }, it);

            if (found[0] != cnt) {
                out.println("not found " + (found[0] - cnt));
            }
            //Assert.assertEquals(cnt, found[0]);
            if (f == matched[0])
                out.println((it.bbox.xmax - it.bbox.xmin)
                        + " x " + (it.bbox.ymax - it.bbox.ymin)
                        + " ==> " + it);
        }

        //out.println("m:" + matched[0] + "  f:" + found[0]);
        Assert.assertEquals(numItems, matched[0]);
        Assert.assertEquals(numItems, q.size());
    }

    public static void main(String[] args) {
        QuadTreeTest qt = new QuadTreeTest();

        long time = currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            qt.shouldWork0();
            long now = currentTimeMillis();

            out.println("==>" + (now - time));
            time = now;
        }
    }
}
