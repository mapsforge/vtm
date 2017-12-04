/*
 * Copyright 2013 Hannes Janetzek
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
package org.oscim.tiling.source;

import org.oscim.core.GeometryBuffer;
import org.oscim.utils.UTF8Decoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public abstract class PbfDecoder implements ITileDecoder {
    static final Logger log = LoggerFactory.getLogger(PbfDecoder.class);

    private final static int S1 = 7;
    private final static int S2 = 14;
    private final static int S3 = 21;
    private final static int S4 = 28;

    private final static int M1 = (1 << S1) - 1;
    private final static int M2 = (1 << S2) - 1;
    private final static int M3 = (1 << S3) - 1;
    private final static int M4 = (1 << S4) - 1;

    protected static final boolean debug = false;

    static class ProtobufException extends IOException {
        private static final long serialVersionUID = 1L;

        public ProtobufException(String detailMessage) {
            super(detailMessage);
        }
    }

    final static ProtobufException TRUNCATED_MSG =
            new ProtobufException("truncated msg");

    protected final static ProtobufException INVALID_VARINT =
            new ProtobufException("invalid varint");

    protected final static ProtobufException INVALID_PACKED_SIZE =
            new ProtobufException("invalid message size");

    protected void error(String msg) throws IOException {
        throw new ProtobufException(msg);
    }

    private final static int BUFFER_SIZE = 1 << 15; // 32kb
    protected byte[] buffer = new byte[BUFFER_SIZE];

    // position in buffer
    protected int bufferPos;

    // bytes available in buffer
    protected int bufferFill;

    // offset of buffer in message
    private int mBufferOffset;

    // overall bytes of message read
    private int mMsgPos;

    private InputStream mInputStream;

    private final UTF8Decoder mStringDecoder;

    public PbfDecoder() {
        mStringDecoder = new UTF8Decoder();
    }

    public void setInputStream(InputStream is) {
        mInputStream = is;

        bufferFill = 0;
        bufferPos = 0;
        mBufferOffset = 0;

        mMsgPos = 0;
    }

    protected int decodeVarint32() throws IOException {

        int bytesLeft = 0;
        int val = 0;

        for (int shift = 0; shift < 32; shift += 7) {
            if (bytesLeft == 0)
                bytesLeft = fillBuffer(1);

            byte b = buffer[bufferPos++];
            val |= (b & 0x7f) << shift;

            if (b >= 0)
                return val;

            bytesLeft--;
        }

        throw INVALID_VARINT;
    }

    protected long decodeVarint64() throws IOException {

        int bytesLeft = 0;
        long val = 0;

        for (int shift = 0; shift < 64; shift += 7) {
            if (bytesLeft == 0)
                bytesLeft = fillBuffer(1);

            byte b = buffer[bufferPos++];
            val |= (long) (b & 0x7f) << shift;

            if (b >= 0)
                return val;

            bytesLeft--;
        }

        throw INVALID_VARINT;
    }

    protected String decodeString() throws IOException {
        String result;

        final int size = decodeVarint32();
        fillBuffer(size);

        if (mStringDecoder == null)
            result = new String(buffer, bufferPos, size, "UTF-8");
        else
            result = mStringDecoder.decode(buffer, bufferPos, size);

        bufferPos += size;

        return result;

    }

    protected float decodeFloat() throws IOException {
        if (bufferPos + 4 > bufferFill)
            fillBuffer(4);

        int val = (buffer[bufferPos++] & 0xFF
                | (buffer[bufferPos++] & 0xFF) << 8
                | (buffer[bufferPos++] & 0xFF) << 16
                | (buffer[bufferPos++] & 0xFF) << 24);

        return Float.intBitsToFloat(val);
    }

    protected double decodeDouble() throws IOException {
        if (bufferPos + 8 > bufferFill)
            fillBuffer(8);

        long val = ((long) buffer[bufferPos++] & 0xFF
                | ((long) buffer[bufferPos++] & 0xFF) << 8
                | ((long) buffer[bufferPos++] & 0xFF) << 16
                | ((long) buffer[bufferPos++] & 0xFF) << 24
                | ((long) buffer[bufferPos++] & 0xFF) << 32
                | ((long) buffer[bufferPos++] & 0xFF) << 40
                | ((long) buffer[bufferPos++] & 0xFF) << 48
                | ((long) buffer[bufferPos++] & 0xFF) << 56);

        return Double.longBitsToDouble(val);
    }

    protected boolean decodeBool() throws IOException {
        if (bufferPos + 1 > bufferFill)
            fillBuffer(1);

        return buffer[bufferPos++] != 0;
    }

    protected int decodeInterleavedPoints(GeometryBuffer geom, float scale)
            throws IOException {

        float[] points = geom.points;
        int bytes = decodeVarint32();
        fillBuffer(bytes);

        int cnt = 0;
        int lastX = 0;
        int lastY = 0;
        boolean even = true;

        byte[] buf = buffer;
        int pos = bufferPos;
        int end = pos + bytes;

        while (pos < end) {
            byte b = buf[pos++];
            int val = b;

            if (b < 0) {
                b = buf[pos++];
                val = (val & M1) | (b << S1);
                if (b < 0) {
                    b = buf[pos++];
                    val = (val & M2) | (b << S2);
                    if (b < 0) {
                        b = buf[pos++];
                        val = (val & M3) | (b << S3);
                        if (b < 0) {
                            b = buf[pos++];
                            val = (val & M4) | (b << S4);
                            if (b < 0)
                                throw INVALID_VARINT;
                        }
                    }
                }
            }
            // zigzag decoding
            int s = ((val >>> 1) ^ -(val & 1));

            if (even) {
                lastX = lastX + s;
                points[cnt++] = lastX / scale;
                even = false;
            } else {
                lastY = lastY + s;
                points[cnt++] = lastY / scale;
                even = true;
            }
        }

        if (pos != bufferPos + bytes)
            throw INVALID_PACKED_SIZE;

        bufferPos = pos;

        geom.pointNextPos = cnt;

        // return number of points read
        return (cnt >> 1);
    }

    protected int decodeInterleavedPoints3D(float[] coords, float scale)
            throws IOException {

        int bytes = decodeVarint32();
        fillBuffer(bytes);

        int cnt = 0;
        int lastX = 0;
        int lastY = 0;
        int lastZ = 0;

        int cur = 0;

        byte[] buf = buffer;
        int pos = bufferPos;
        int end = pos + bytes;

        while (pos < end) {
            byte b = buf[pos++];
            int val = b;

            if (b < 0) {
                b = buf[pos++];
                val = (val & M1) | (b << S1);
                if (b < 0) {
                    b = buf[pos++];
                    val = (val & M2) | (b << S2);
                    if (b < 0) {
                        b = buf[pos++];
                        val = (val & M3) | (b << S3);
                        if (b < 0) {
                            b = buf[pos++];
                            val = (val & M4) | (b << S4);
                            if (b < 0)
                                throw INVALID_VARINT;
                        }
                    }
                }
            }
            // zigzag decoding
            int s = ((val >>> 1) ^ -(val & 1));

            if (cur == 0) {
                lastX = lastX + s;
                coords[cnt++] = lastX / scale;
            } else if (cur == 1) {
                lastY = lastY + s;
                coords[cnt++] = lastY / scale;
            } else {
                lastZ = lastZ + s;
                coords[cnt++] = lastZ / scale;
            }
            cur = (cur + 1) % 3;
        }

        if (pos != bufferPos + bytes)
            throw INVALID_PACKED_SIZE;

        bufferPos = pos;

        // return number of points read
        //FIXME inconsitent with 3d version!
        return cnt;
    }

    protected static int deZigZag(int val) {
        return ((val >>> 1) ^ -(val & 1));
    }

    public void decodeVarintArray(int num, int[] array) throws IOException {
        int bytes = decodeVarint32();
        fillBuffer(bytes);

        final byte[] buf = buffer;
        int pos = bufferPos;
        int cnt = 0;

        for (int end = pos + bytes; pos < end; cnt++) {
            if (cnt == num)
                throw new ProtobufException("invalid array size " + num);

            byte b = buf[pos++];
            int val = b;

            if (b < 0) {
                b = buf[pos++];
                val = (val & M1) | (b << S1);
                if (b < 0) {
                    b = buf[pos++];
                    val = (val & M2) | (b << S2);
                    if (b < 0) {
                        b = buf[pos++];
                        val = (val & M3) | (b << S3);
                        if (b < 0) {
                            b = buf[pos++];
                            val = (val & M4) | (b << S4);
                            if (b < 0)
                                throw INVALID_VARINT;
                        }
                    }
                }
            }

            array[cnt] = val;
        }

        if (pos != bufferPos + bytes)
            throw INVALID_PACKED_SIZE;

        bufferPos = pos;
    }

    /**
     * fill short array from packed uint32. Array values must be positive
     * as the end will be marked by -1 if the resulting array is larger
     * than the input!
     */
    protected short[] decodeUnsignedVarintArray(short[] array) throws IOException {
        int bytes = decodeVarint32();

        int arrayLength = 0;
        if (array == null) {
            arrayLength = 32;
            array = new short[32];
        }

        fillBuffer(bytes);
        int cnt = 0;

        final byte[] buf = buffer;
        int pos = bufferPos;

        for (int end = pos + bytes; pos < end; cnt++) {

            byte b = buf[pos++];
            int val = b;

            if (b < 0) {
                b = buf[pos++];
                val = (val & M1) | (b << S1);
                if (b < 0) {
                    b = buf[pos++];
                    val = (val & M2) | (b << S2);
                    if (b < 0) {
                        b = buf[pos++];
                        val = (val & M3) | (b << S3);
                        if (b < 0) {
                            b = buf[pos++];
                            val = (val & M4) | (b << S4);
                            if (b < 0)
                                throw INVALID_VARINT;
                        }
                    }
                }
            }

            if (arrayLength <= cnt) {
                arrayLength = cnt + 16;
                short[] tmp = array;
                array = new short[arrayLength];
                System.arraycopy(tmp, 0, array, 0, cnt);
            }

            array[cnt] = (short) val;
        }

        if (pos != bufferPos + bytes)
            throw INVALID_PACKED_SIZE;

        bufferPos = pos;

        if (arrayLength > cnt)
            array[cnt] = -1;

        return array;
    }

    // for use int packed varint decoders
    protected int decodeVarint32Filled() throws IOException {

        byte[] buf = buffer;
        int pos = bufferPos;

        byte b = buf[pos++];
        int val = b;

        if (b < 0) {
            b = buf[pos++];
            val = (val & M1) | (b << S1);
            if (b < 0) {
                b = buf[pos++];
                val = (val & M2) | (b << S2);
                if (b < 0) {
                    b = buf[pos++];
                    val = (val & M3) | (b << S3);
                    if (b < 0) {
                        b = buf[pos++];
                        val = (val & M4) | (b << S4);
                        if (b < 0)
                            throw INVALID_VARINT;
                    }
                }
            }
        }

        bufferPos = pos;

        return val;
    }

    public boolean hasData() throws IOException {
        //if (mBufferOffset + bufferPos >= mMsgEnd)
        //    return false;

        return fillBuffer(1) > 0;
    }

    public int position() {
        return mBufferOffset + bufferPos;
    }

    public int fillBuffer(int size) throws IOException {
        int bytesLeft = bufferFill - bufferPos;

        // check if buffer already contains the request bytes
        if (bytesLeft >= size)
            return bytesLeft;

        int maxSize = buffer.length;

        if (size > maxSize) {

            if (debug)
                log.debug("increase read buffer to " + size + " bytes");

            maxSize = size;

            byte[] tmp = buffer;
            buffer = new byte[maxSize];
            System.arraycopy(tmp, bufferPos, buffer, 0, bytesLeft);

            mBufferOffset += bufferPos;
            bufferPos = 0;
            bufferFill = bytesLeft;

        } else if (bytesLeft == 0) {
            // just advance buffer offset and reset buffer
            mBufferOffset += bufferPos;
            bufferPos = 0;
            bufferFill = 0;

        } else if (bufferPos + size > maxSize) {
            // copy bytes left to the beginning of buffer
            if (debug)
                log.debug("shift " + bufferFill + " " + bufferPos + " " + size);

            System.arraycopy(buffer, bufferPos, buffer, 0, bytesLeft);

            mBufferOffset += bufferPos;
            bufferPos = 0;
            bufferFill = bytesLeft;
        }

        while ((bufferFill - bufferPos) < size) {
            int max = maxSize - bufferFill;

            if (max <= 0) {
                // should not be possible
                throw new IOException("burp");
            }

            // read until requested size is available in buffer
            int len = mInputStream.read(buffer, bufferFill, max);

            if (len < 0) {
                if (debug)
                    log.debug("finished reading {}", mMsgPos);

                // finished reading, mark end
                buffer[bufferFill] = 0;
                return bufferFill - bufferPos;
            }

            mMsgPos += len;
            bufferFill += len;
        }
        return bufferFill - bufferPos;
    }

    protected static int readUnsignedInt(InputStream is, byte[] buf) throws IOException {
        // check 4 bytes available..
        int read = 0;
        int len = 0;

        while (read < 4 && (len = is.read(buf, read, 4 - read)) >= 0)
            read += len;

        if (read < 4)
            return read < 0 ? (read * 10) : read;

        return decodeInt(buf, 0);
    }

    static int decodeInt(byte[] buffer, int offset) {
        return buffer[offset] << 24 | (buffer[offset + 1] & 0xff) << 16
                | (buffer[offset + 2] & 0xff) << 8
                | (buffer[offset + 3] & 0xff);
    }
}
