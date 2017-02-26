/*
 * Copyright 2010, 2011, 2012 mapsforge.org
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
package org.oscim.tiling.source.mapfile;

import org.oscim.core.Tag;
import org.oscim.core.TagSet;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

/**
 * Reads from a {@link RandomAccessFile} into a buffer and decodes the data.
 */
public class ReadBuffer {
    /**
     * Maximum buffer size which is supported by this implementation.
     */
    static final int MAXIMUM_BUFFER_SIZE = 2500000;
    private static final String CHARSET_UTF8 = "UTF-8";
    private static final Logger LOGGER = Logger.getLogger(ReadBuffer.class.getName());

    private byte[] bufferData;
    private int bufferPosition;
    private final RandomAccessFile inputFile;

    ReadBuffer(RandomAccessFile inputFile) {
        this.inputFile = inputFile;
    }

    /**
     * Returns one signed byte from the read buffer.
     *
     * @return the byte value.
     */
    public synchronized byte readByte() {
        return this.bufferData[this.bufferPosition++];
    }

    /**
     * Reads the given amount of bytes from the file into the read buffer and
     * resets the internal buffer position. If
     * the capacity of the read buffer is too small, a larger one is created
     * automatically.
     *
     * @param length the amount of bytes to read from the file.
     * @return true if the whole data was read successfully, false otherwise.
     * @throws IOException if an error occurs while reading the file.
     */
    public synchronized boolean readFromFile(int length) throws IOException {
        // ensure that the read buffer is large enough
        if (this.bufferData == null || this.bufferData.length < length) {
            // ensure that the read buffer is not too large
            if (length > MAXIMUM_BUFFER_SIZE) {
                LOGGER.warning("invalid read length: " + length);
                return false;
            }
            this.bufferData = new byte[length];
        }

        // reset the buffer position and read the data into the buffer
        this.bufferPosition = 0;
        return this.inputFile.read(this.bufferData, 0, length) == length;
    }

    /**
     * Converts four bytes from the read buffer to a signed int.
     * <p/>
     * The byte order is big-endian.
     *
     * @return the int value.
     */
    public synchronized int readInt() {
        this.bufferPosition += 4;
        return Deserializer.getInt(this.bufferData, this.bufferPosition - 4);
    }

    /**
     * Converts eight bytes from the read buffer to a signed long.
     * <p/>
     * The byte order is big-endian.
     *
     * @return the long value.
     */
    public synchronized long readLong() {
        this.bufferPosition += 8;
        return Deserializer.getLong(this.bufferData, this.bufferPosition - 8);

    }

    /**
     * Converts two bytes from the read buffer to a signed int.
     * <p/>
     * The byte order is big-endian.
     *
     * @return the int value.
     */
    public synchronized int readShort() {
        this.bufferPosition += 2;
        return Deserializer.getShort(this.bufferData, this.bufferPosition - 2);
    }

    /**
     * Converts a variable amount of bytes from the read buffer to a signed int.
     * <p/>
     * The first bit is for continuation info, the other six (last byte) or
     * seven (all other bytes) bits are for data. The second bit in the last
     * byte indicates the sign of the number.
     *
     * @return the value.
     */
    public synchronized int readSignedInt() {
        int variableByteDecode = 0;
        byte variableByteShift = 0;

        // check if the continuation bit is set
        while ((this.bufferData[this.bufferPosition] & 0x80) != 0) {
            variableByteDecode |= (this.bufferData[this.bufferPosition++] & 0x7f) << variableByteShift;
            variableByteShift += 7;
        }

        // read the six data bits from the last byte
        if ((this.bufferData[this.bufferPosition] & 0x40) != 0) {
            // negative
            return -(variableByteDecode | ((this.bufferData[this.bufferPosition++] & 0x3f) << variableByteShift));
        }
        // positive
        return variableByteDecode | ((this.bufferData[this.bufferPosition++] & 0x3f) << variableByteShift);

    }

    /**
     * Converts a variable amount of bytes from the read buffer to a signed int
     * array.
     * <p/>
     * The first bit is for continuation info, the other six (last byte) or
     * seven (all other bytes) bits are for data. The second bit in the last
     * byte indicates the sign of the number.
     *
     * @param values result values
     * @param length number of values to read
     */
    public synchronized void readSignedInt(int[] values, int length) {
        for (int i = 0; i < length; i++) {
            values[i] = this.readSignedInt();
        }
    }

    /**
     * Converts a variable amount of bytes from the read buffer to an unsigned
     * int.
     * <p/>
     * The first bit is for continuation info, the other seven bits are for
     * data.
     *
     * @return the int value.
     */
    public synchronized int readUnsignedInt() {

        int variableByteDecode = 0;
        byte variableByteShift = 0;

        // check if the continuation bit is set
        while ((this.bufferData[this.bufferPosition] & 0x80) != 0) {
            variableByteDecode |= (this.bufferData[this.bufferPosition++] & 0x7f) << variableByteShift;
            variableByteShift += 7;
        }

        // read the seven data bits from the last byte
        return variableByteDecode | (this.bufferData[this.bufferPosition++] << variableByteShift);
    }

    /**
     * Decodes a variable amount of bytes from the read buffer to a string.
     *
     * @return the UTF-8 decoded string (may be null).
     */
    public String readUTF8EncodedString() {
        return readUTF8EncodedString(readUnsignedInt());
    }

    /**
     * @return ...
     */
    public int getPositionAndSkip() {
        int pos = this.bufferPosition;
        int length = readUnsignedInt();
        skipBytes(length);
        return pos;
    }

    /**
     * Decodes the given amount of bytes from the read buffer to a string.
     *
     * @param stringLength the length of the string in bytes.
     * @return the UTF-8 decoded string (may be null).
     */
    public String readUTF8EncodedString(int stringLength) {
        if (stringLength > 0 && this.bufferPosition + stringLength <= this.bufferData.length) {
            this.bufferPosition += stringLength;
            try {
                return new String(this.bufferData, this.bufferPosition - stringLength, stringLength, CHARSET_UTF8);
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e);
            }
        }
        LOGGER.warning("invalid string length: " + stringLength);
        return null;
    }

    /**
     * Decodes a variable amount of bytes from the read buffer to a string.
     *
     * @param position buffer offset position of string
     * @return the UTF-8 decoded string (may be null).
     */
    public String readUTF8EncodedStringAt(int position) {
        int curPosition = this.bufferPosition;
        this.bufferPosition = position;
        String result = readUTF8EncodedString(readUnsignedInt());
        this.bufferPosition = curPosition;
        return result;
    }

    /**
     * @return the current buffer position.
     */
    int getBufferPosition() {
        return this.bufferPosition;
    }

    /**
     * @return the current size of the read buffer.
     */
    int getBufferSize() {
        return this.bufferData.length;
    }

    /**
     * Sets the buffer position to the given offset.
     *
     * @param bufferPosition the buffer position.
     */
    void setBufferPosition(int bufferPosition) {
        this.bufferPosition = bufferPosition;
    }

    /**
     * Skips the given number of bytes in the read buffer.
     *
     * @param bytes the number of bytes to skip.
     */
    void skipBytes(int bytes) {
        this.bufferPosition += bytes;
    }

    boolean readTags(TagSet tags, Tag[] wayTags, byte numberOfTags) {
        tags.clear();

        int maxTag = wayTags.length;

        for (byte i = 0; i < numberOfTags; i++) {
            int tagId = readUnsignedInt();
            if (tagId < 0 || tagId >= maxTag) {
                return true;
            }
            tags.add(wayTags[tagId]);
        }
        return true;
    }

    private static final int WAY_NUMBER_OF_TAGS_BITMASK = 0x0f;
    int lastTagPosition;

    synchronized int skipWays(int queryTileBitmask, int elements) {
        int pos = this.bufferPosition;
        byte[] data = this.bufferData;
        int cnt = elements;
        int skip;

        lastTagPosition = -1;

        while (cnt > 0) {
            // read way size (unsigned int)
            if ((data[pos] & 0x80) == 0) {
                skip = (data[pos] & 0x7f);
                pos += 1;
            } else if ((data[pos + 1] & 0x80) == 0) {
                skip = (data[pos] & 0x7f)
                        | (data[pos + 1] & 0x7f) << 7;
                pos += 2;
            } else if ((data[pos + 2] & 0x80) == 0) {
                skip = (data[pos] & 0x7f)
                        | ((data[pos + 1] & 0x7f) << 7)
                        | ((data[pos + 2] & 0x7f) << 14);
                pos += 3;
            } else if ((data[pos + 3] & 0x80) == 0) {
                skip = (data[pos] & 0x7f)
                        | ((data[pos + 1] & 0x7f) << 7)
                        | ((data[pos + 2] & 0x7f) << 14)
                        | ((data[pos + 3] & 0x7f) << 21);
                pos += 4;
            } else {
                skip = (data[pos] & 0x7f)
                        | ((data[pos + 1] & 0x7f) << 7)
                        | ((data[pos + 2] & 0x7f) << 14)
                        | ((data[pos + 3] & 0x7f) << 21)
                        | ((data[pos + 4] & 0x7f) << 28);
                pos += 5;
            }
            // invalid way size
            if (skip < 0) {
                this.bufferPosition = pos;
                return -1;
            }

            // check if way matches queryTileBitmask
            if ((((data[pos] << 8) | (data[pos + 1] & 0xff)) & queryTileBitmask) == 0) {

                // remember last tags position
                if ((data[pos + 2] & WAY_NUMBER_OF_TAGS_BITMASK) != 0)
                    lastTagPosition = pos + 2;

                pos += skip;
                cnt--;
            } else {
                pos += 2;
                break;
            }
        }
        this.bufferPosition = pos;
        return cnt;
    }
}
