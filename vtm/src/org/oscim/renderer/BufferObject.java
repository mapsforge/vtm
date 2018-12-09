/*
 * Copyright 2012 Hannes Janetzek
 *
 * This file is part of the OpenScienceMap project (http://www.opensciencemap.org).
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General License for more details.
 *
 * You should have received a copy of the GNU Lesser General License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.oscim.renderer;

import org.oscim.backend.GL;
import org.oscim.backend.GLAdapter;
import org.oscim.utils.pool.Inlist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.Buffer;

import javax.annotation.CheckReturnValue;

import static org.oscim.backend.GLAdapter.gl;

public final class BufferObject extends Inlist<BufferObject> {
    static final Logger log = LoggerFactory.getLogger(BufferObject.class);
    private static final int MB = 1024 * 1024;
    private static final int LIMIT_BUFFERS = 16 * MB;

    /**
     * GL identifier
     */
    private int id;

    /**
     * allocated bytes
     */
    private int size;

    /**
     * GL_ARRAY_BUFFER or GL_ELEMENT_ARRAY_BUFFER
     */
    private int target;

    private BufferObject(int target, int id) {
        this.id = id;
        this.target = target;
    }

    /**
     * @param newSize size required in bytes
     */
    public void loadBufferData(Buffer buf, int newSize) {
        boolean clear = false;

        if (buf.position() != 0) {
            log.debug("flip your buffer!");
            buf.flip();
        }

        GLState.bindBuffer(target, id);

        /* reuse memory allocated for vbo when possible and allocated
         * memory is less then four times the new data */
        if (!GLAdapter.NO_BUFFER_SUB_DATA && !clear &&
                (size > newSize) && (size < newSize * 4)) {
            gl.bufferSubData(target, 0, newSize, buf);
        } else {
            mBufferMemoryUsage += newSize - size;
            size = newSize;
            //GL.bufferData(target, size, buf, GL20.DYNAMIC_DRAW);
            gl.bufferData(target, size, buf, GL.STATIC_DRAW);
        }
    }

    public void bind() {
        GLState.bindBuffer(target, id);
    }

    public void unbind() {
        GLState.bindBuffer(target, GLState.UNBIND);
    }

    // ---------------------------- pool ----------------------------
    // bytes currently loaded in VBOs
    private static int mBufferMemoryUsage;

    public static void checkBufferUsage(boolean force) {
        // try to clear some unused vbo when exceding limit
        if (mBufferMemoryUsage < LIMIT_BUFFERS)
            return;

        log.debug("use: " + mBufferMemoryUsage / MB + "MB");
        mBufferMemoryUsage -= BufferObject.limitUsage(MB);
        log.debug("now: " + mBufferMemoryUsage / MB + "MB");
    }

    private static final BufferObject pool[] = new BufferObject[2];
    private static final int counter[] = new int[2];

    /**
     * @param target can be GL20.ARRAY_BUFFER or GL20.ELEMENT_ARRAY_BUFFER
     * @param size   requested size in bytes. optional - can be 0.
     */
    public static synchronized BufferObject get(int target, int size) {

        int t = (target == GL.ARRAY_BUFFER) ? 0 : 1;

        if (pool[t] == null) {
            if (counter[t] != 0)
                throw new IllegalStateException("lost objects: " + counter[t]);

            createBuffers(target, 10);
            counter[t] += 10;
        }
        counter[t]--;

        if (size != 0) {
            /* find the item with minimal size greater 'size' bytes. */
            BufferObject bo = pool[t];
            /* actually points to BufferObject before min */
            BufferObject min = null;
            BufferObject prev = null;

            for (; bo != null; bo = bo.next) {
                if (bo.size > size) {
                    if (min == null || min.next.size > bo.size)
                        min = prev;
                }
                prev = bo;
            }

            if (min != null && min != pool[t]) {
                bo = min.next;
                min.next = bo.next;
                bo.next = null;
                return bo;
            }
        }

        BufferObject bo = pool[t];
        pool[t] = pool[t].next;
        bo.next = null;
        return bo;
    }

    @CheckReturnValue
    public static synchronized BufferObject release(BufferObject bo) {
        if (bo == null)
            return null;

        // if (counter > 200) {
        // log.debug("should clear some buffers " + counter);
        // }
        int t = (bo.target == GL.ARRAY_BUFFER) ? 0 : 1;

        bo.next = pool[t];
        pool[t] = bo;
        counter[t]++;

        return null;
    }

    // Note: only call from GL-Thread
    static synchronized int limitUsage(int reduce) {

        int vboIds[] = new int[10];
        int freed = 0;

        for (int t = 0; t < 2; t++) {

            int removed = 0;
            BufferObject prev = pool[t];

            if (prev == null) {
                log.debug("nothing to free");
                continue;
            }

            for (BufferObject bo = pool[t].next; bo != null; ) {
                if (bo.size > 0) {
                    freed += bo.size;
                    bo.size = 0;

                    vboIds[removed++] = bo.id;
                    prev.next = bo.next;
                    bo = bo.next;

                    if (removed == 10 || reduce < freed)
                        break;

                } else {
                    prev = bo;
                    bo = bo.next;
                }
            }

            if (removed > 0) {
                GLUtils.glDeleteBuffers(removed, vboIds);
                counter[t] -= removed;
            }

        }

        return freed;
    }

    static void createBuffers(int target, int num) {
        int[] mVboIds = GLUtils.glGenBuffers(num);

        int t = (target == GL.ARRAY_BUFFER) ? 0 : 1;

        for (int i = 0; i < num; i++) {
            BufferObject bo = new BufferObject(target, mVboIds[i]);
            bo.next = pool[t];
            pool[t] = bo;
        }
    }

    static synchronized void clear() {
        mBufferMemoryUsage = 0;

        pool[0] = null;
        pool[1] = null;
        counter[0] = 0;
        counter[1] = 0;
    }

    static synchronized void init(int num) {
        createBuffers(GL.ARRAY_BUFFER, num);
        counter[0] += num;
    }

    public static boolean isMaxFill() {
        return mBufferMemoryUsage > LIMIT_BUFFERS;
    }
}
