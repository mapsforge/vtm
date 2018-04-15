/*
 * Copyright 2012 Hannes Janetzek
 * Copyright 2018 Gustl22
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
package org.oscim.renderer.bucket;

import org.oscim.renderer.bucket.IntVertexData.Chunk;
import org.oscim.utils.pool.Inlist;
import org.oscim.utils.pool.SyncPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.Buffer;
import java.nio.IntBuffer;

/**
 * A linked list of array chunks to hold temporary vertex data.
 * <p/>
 * TODO override append() etc to update internal (cur) state.
 */
public class IntVertexData extends Inlist.List<Chunk> implements IVertexData {
    static final Logger log = LoggerFactory.getLogger(IntVertexData.class);

    /**
     * Size of array chunks. Must be multiple of:
     * 4 (LineLayer/PolygonLayer),
     * 24 (TexLineLayer - one block, i.e. two segments)
     * 24 (TextureLayer)
     */
    public static final int SIZE = 360;

    /**
     * Shared chunk pool size.
     */
    private static final int MAX_POOL = 500;

    public static class Chunk extends Inlist<Chunk> {
        public final int[] vertices = new int[SIZE];
        public int used;
    }

    private static class Pool extends SyncPool<Chunk> {
        public Pool() {
            super(MAX_POOL);
        }

        @Override
        protected Chunk createItem() {
            return new Chunk();
        }

        @Override
        protected boolean clearItem(Chunk it) {
            it.used = 0;
            return true;
        }
    }

    @Override
    public int countSize() {
        if (cur == null)
            return 0;

        cur.used = used;

        int size = 0;
        for (Chunk it = head(); it != null; it = it.next)
            size += it.used;

        return size;
    }

    @Override
    public Chunk clear() {
        if (cur == null)
            return null;

        cur.used = used;
        used = SIZE; /* set SIZE to get new item on add */
        cur = null;
        vertices = null;

        return super.clear();
    }

    private final static Pool pool = new Pool();

    @Override
    public void dispose() {
        pool.releaseAll(super.clear());
        used = SIZE; /* set SIZE to get new item on add */
        cur = null;
        vertices = null;
    }

    /**
     * @return sum of elements added
     */
    @Override
    public int compile(Buffer sbuf) {
        if (cur == null)
            return 0;

        cur.used = used;

        int size = 0;
        for (Chunk it = head(); it != null; it = it.next) {
            size += it.used;
            ((IntBuffer) sbuf).put(it.vertices, 0, it.used);
        }
        dispose();
        return size;
    }

    private Chunk cur;

    /* set SIZE to get new item on add */
    private int used = SIZE;

    private int[] vertices;

    private void getNext() {
        if (cur == null) {
            cur = pool.get();
            push(cur);
        } else {
            if (cur.next != null)
                throw new IllegalStateException("seeeked...");

            cur.used = SIZE;
            cur.next = pool.get();
            cur = cur.next;
        }
        vertices = cur.vertices;
        used = 0;
    }

    @Override
    public void add(float a) {
        add((int) a);
    }

    @Override
    public void add(int a) {
        if (used == SIZE)
            getNext();

        vertices[used++] = a;
    }

    @Override
    public void add(float a, float b) {
        add((int) a, (int) b);
    }

    @Override
    public void add(int a, int b) {
        if (used == SIZE)
            getNext();

        vertices[used + 0] = a;
        vertices[used + 1] = b;
        used += 2;
    }

    @Override
    public void add(float a, float b, float c) {
        add((int) a, (int) b, (int) c);
    }

    @Override
    public void add(int a, int b, int c) {
        if (used == SIZE)
            getNext();

        vertices[used + 0] = a;
        vertices[used + 1] = b;
        vertices[used + 2] = c;
        used += 3;
    }

    @Override
    public void add(float a, float b, float c, float d) {
        add((int) a, (int) b, (int) c, (int) d);
    }

    @Override
    public void add(int a, int b, int c, int d) {
        if (used == SIZE)
            getNext();

        vertices[used + 0] = a;
        vertices[used + 1] = b;
        vertices[used + 2] = c;
        vertices[used + 3] = d;
        used += 4;
    }

    @Override
    public void add(float a, float b, float c, float d, float e, float f) {
        add((int) a, (int) b, (int) c, (int) d, (int) e, (int) f);
    }

    @Override
    public void add(int a, int b, int c, int d, int e, int f) {
        if (used == SIZE)
            getNext();

        vertices[used + 0] = a;
        vertices[used + 1] = b;
        vertices[used + 2] = c;
        vertices[used + 3] = d;
        vertices[used + 4] = e;
        vertices[used + 5] = f;
        used += 6;
    }

    /**
     * Direct access to the current chunk of VertexData. Use with care!
     * <p/>
     * When changing the position use releaseChunk to update internal state
     */
    public Chunk obtainChunk() {
        if (used == SIZE)
            getNext();

        cur.used = used;

        return cur;
    }

    @Override
    public void releaseChunk() {
        used = cur.used;
    }

    @Override
    public void releaseChunk(int size) {
        cur.used = size;
        used = size;
    }

    /**
     * Do not use!
     */
    @Override
    public void seek(int offset) {
        used += offset;
        cur.used = used;

        if (used > SIZE || used < 0)
            throw new IllegalStateException("seeked too far: " + offset + "/" + used);
    }

    @Override
    public boolean empty() {
        return cur == null;
    }
}
