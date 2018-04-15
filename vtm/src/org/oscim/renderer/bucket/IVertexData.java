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

import java.nio.Buffer;

public interface IVertexData {
    void add(float a);

    void add(int a);

    void add(float a, float b);

    void add(int a, int b);

    void add(float a, float b, float c);

    void add(int a, int b, int c);

    void add(float a, float b, float c, float d);

    void add(int a, int b, int c, int d);

    void add(float a, float b, float c, float d, float e, float f);

    void add(int a, int b, int c, int d, int e, int f);

    int compile(Buffer buffer);

    int countSize();

    void dispose();

    boolean empty();

    // IChunk obtainChunk(); // TODO interface for Chunk

    void releaseChunk();

    void releaseChunk(int size);

    void seek(int offset);
}
