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

import org.oscim.core.Tile;
import org.oscim.utils.ArrayUtils;
import org.oscim.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

/**
 * Lightweight HTTP connection for tile loading. Does not do redirects,
 * https, full header parsing or other stuff.
 */
public class LwHttp implements HttpEngine {
    static final Logger log = LoggerFactory.getLogger(LwHttp.class);
    static final boolean dbg = false;

    private final static byte[] HEADER_HTTP_OK = "200 OK".getBytes();
    private final static byte[] HEADER_CONTENT_LENGTH = "Content-Length".getBytes();
    private final static byte[] HEADER_CONNECTION_CLOSE = "Connection: close".getBytes();
    private final static byte[] HEADER_ENCODING_GZIP = "Content-Encoding: gzip".getBytes();

    private final static int RESPONSE_EXPECTED_LIVES = 100;
    private final static long RESPONSE_TIMEOUT = (long) 10E9; // 10 second in nanosecond

    private final static int CONNECT_TIMEOUT = 15000; // 15 seconds
    private final static int SOCKET_TIMEOUT = 8000; // 8 seconds

    private final static int BUFFER_SIZE = 8192;
    private final byte[] buffer = new byte[BUFFER_SIZE];

    private final String mHost;
    private final int mPort;

    private int mMaxRequests = 0;
    private Socket mSocket;
    private OutputStream mCommandStream;
    private Buffer mResponseStream;
    private long mLastRequest = 0;
    private InetSocketAddress mSockAddr;

    /**
     * Server requested to close the connection
     */
    private boolean mMustCloseConnection;

    private final byte[] REQUEST_GET_START;
    private final byte[] REQUEST_GET_END;
    private final byte[] mRequestBuffer;

    private final byte[][] mTilePath;
    private final UrlTileSource mTileSource;

    //private boolean mUseGZIP;

    private LwHttp(UrlTileSource tileSource, byte[][] tilePath) {
        mTilePath = tilePath;
        mTileSource = tileSource;

        URL url = tileSource.getUrl();
        int port = url.getPort();
        if (port < 0)
            port = 80;

        mHost = url.getHost();
        mPort = port;

        String path = url.getPath();

        REQUEST_GET_START = ("GET " + path).getBytes();

        StringBuilder sb = new StringBuilder()
                .append(" HTTP/1.1")
                .append("\r\nUser-Agent: vtm/0.5.9")
                .append("\r\nHost: ")
                .append(mHost)
                .append("\r\nConnection: Keep-Alive");

        for (Entry<String, String> l : tileSource.getRequestHeader().entrySet()) {
            String key = l.getKey();
            String val = l.getValue();
            //if ("Accept-Encoding".equals(key) && "gzip".equals(val))
            //    mUseGZIP = true;
            sb.append("\r\n").append(key).append(": ").append(val);
        }
        sb.append("\r\n\r\n");

        REQUEST_GET_END = sb.toString().getBytes();

        mRequestBuffer = new byte[1024];
        System.arraycopy(REQUEST_GET_START, 0,
                mRequestBuffer, 0,
                REQUEST_GET_START.length);
    }

    static final class Buffer extends BufferedInputStream {
        OutputStream cache;
        int bytesRead = 0;
        int bytesWrote;
        int marked = -1;
        int contentLength;

        public Buffer(InputStream is) {
            super(is, BUFFER_SIZE);
        }

        public void setCache(OutputStream cache) {
            this.cache = cache;
        }

        public void start(int length) {
            bytesRead = 0;
            bytesWrote = 0;
            contentLength = length;
        }

        public boolean finishedReading() {
            try {
                while (bytesRead < contentLength && read() >= 0) ;
            } catch (IOException e) {
                log.debug(e.getMessage());
            }

            return bytesRead == contentLength;
        }

        @Override
        public void close() throws IOException {
            if (dbg)
                log.debug("close()... ignored");
        }

        @Override
        public synchronized void mark(int readlimit) {
            if (dbg)
                log.debug("mark {}", readlimit);

            marked = bytesRead;
            super.mark(readlimit);
        }

        @Override
        public synchronized long skip(long n) throws IOException {
            /* Android(4.1.2) image decoder *requires* skip to
             * actually skip the requested amount.
             * https://code.google.com/p/android/issues/detail?id=6066 */
            long sumSkipped = 0L;
            while (sumSkipped < n) {
                long skipped = super.skip(n - sumSkipped);
                if (skipped != 0) {
                    sumSkipped += skipped;
                    continue;
                }
                if (read() < 0)
                    break; // EOF

                sumSkipped += 1;
                /* was incremented by read() */
                bytesRead -= 1;
            }

            if (dbg)
                log.debug("skip:{}/{} pos:{}", n, sumSkipped, bytesRead);

            bytesRead += sumSkipped;
            return sumSkipped;
        }

        @Override
        public synchronized void reset() throws IOException {
            if (dbg)
                log.debug("reset");

            if (marked >= 0)
                bytesRead = marked;

            /* TODO could check if the mark is already invalid */
            super.reset();
        }

        @Override
        public int read() throws IOException {
            if (bytesRead >= contentLength)
                return -1;

            int data = super.read();

            if (data >= 0)
                bytesRead += 1;

            if (cache != null && bytesRead > bytesWrote) {
                bytesWrote = bytesRead;
                cache.write(data);
            }

            return data;
        }

        @Override
        public int read(byte[] buffer, int offset, int byteCount)
                throws IOException {

            if (bytesRead >= contentLength)
                return -1;

            int len = super.read(buffer, offset, byteCount);

            if (dbg)
                log.debug("read {} {} {}", len, bytesRead, contentLength);

            if (len <= 0)
                return len;

            bytesRead += len;

            if (cache != null && bytesRead > bytesWrote) {
                int add = bytesRead - bytesWrote;
                bytesWrote = bytesRead;
                cache.write(buffer, offset + (len - add), add);
            }

            return len;
        }
    }

    private void checkSocket() throws IOException {
        if (mSocket == null)
            throw new IOException("No Socket");
    }

    public synchronized InputStream read() throws IOException {
        checkSocket();

        Buffer is = mResponseStream;
        is.mark(BUFFER_SIZE);
        is.start(BUFFER_SIZE);

        byte[] buf = buffer;
        boolean first = true;
        boolean gzip = false;

        int read = 0;
        int pos = 0;
        int end = 0;
        int len = 0;

        int contentLength = -1;

        /* header may not be larger than BUFFER_SIZE for this to work */
        for (; (pos < read) || ((read < BUFFER_SIZE) &&
                (len = is.read(buf, read, BUFFER_SIZE - read)) >= 0); len = 0) {

            read += len;
            /* end of header lines */
            while (end < read && (buf[end] != '\n'))
                end++;

            if (end == BUFFER_SIZE) {
                throw new IOException("Header too large!");
            }

            if (buf[end] != '\n')
                continue;

            /* empty line (header end) */
            if (end - pos == 1) {
                end += 1;
                break;
            }

            if (first) {
                first = false;
                /* check only for OK ("HTTP/1.? ".length == 9) */
                if (!check(HEADER_HTTP_OK, buf, pos + 9, end)) {
                    throw new IOException("HTTP Error: "
                            + new String(buf, pos, end - pos - 1));
                }
            } else if (check(HEADER_CONTENT_LENGTH, buf, pos, end)) {
                /* parse Content-Length */
                contentLength = parseInt(buf, pos +
                        HEADER_CONTENT_LENGTH.length + 2, end - 1);
            } else if (check(HEADER_ENCODING_GZIP, buf, pos, end)) {
                gzip = true;
            } else if (check(HEADER_CONNECTION_CLOSE, buf, pos, end)) {
                mMustCloseConnection = true;
            }

            if (dbg) {
                String line = new String(buf, pos, end - pos - 1);
                log.debug("> {} <", line);
            }

            pos += (end - pos) + 1;
            end = pos;
        }

        /* back to start of content */
        is.reset();
        is.mark(0);
        is.skip(end);
        is.start(contentLength);

        if (gzip) {
            return new GZIPInputStream(is);
        }
        return is;
    }

    @Override
    public synchronized void sendRequest(Tile tile) throws IOException {

        if (mSocket != null) {
            if (--mMaxRequests < 0)
                close();
            else if (System.nanoTime() - mLastRequest > RESPONSE_TIMEOUT)
                close();
            else {
                try {
                    int n = mResponseStream.available();
                    if (n > 0) {
                        log.debug("left over bytes {} ", n);
                        close();
                    }
                } catch (IOException e) {
                    log.debug(e.getMessage());
                    close();
                }
            }
        }

        if (mSocket == null) {
            /* might throw IOException */
            lwHttpConnect();

            /* TODO parse from header */
            mMaxRequests = RESPONSE_EXPECTED_LIVES;
        }

        int pos = REQUEST_GET_START.length;
        int len = REQUEST_GET_END.length;

        pos = formatTilePath(tile, mRequestBuffer, pos);
        System.arraycopy(REQUEST_GET_END, 0, mRequestBuffer, pos, len);
        len += pos;

        if (dbg)
            log.debug("request: {}", new String(mRequestBuffer, 0, len));

        try {
            writeRequest(len);
        } catch (IOException e) {
            log.debug("recreate connection");
            close();

            lwHttpConnect();
            writeRequest(len);
        }
    }

    private void writeRequest(int length) throws IOException {
        mCommandStream.write(mRequestBuffer, 0, length);
        //mCommandStream.flush();
    }

    private synchronized void lwHttpConnect() throws IOException {
        if (mSockAddr == null || mSockAddr.isUnresolved()) {
            mSockAddr = new InetSocketAddress(mHost, mPort);
            if (mSockAddr.isUnresolved())
                throw new UnknownHostException(mHost);
        }

        try {
            mSocket = new Socket();
            mSocket.setTcpNoDelay(true);
            mSocket.setSoTimeout(SOCKET_TIMEOUT);
            mSocket.connect(mSockAddr, CONNECT_TIMEOUT);
            mCommandStream = mSocket.getOutputStream();
            mResponseStream = new Buffer(mSocket.getInputStream());

            mMustCloseConnection = false;
        } catch (IOException e) {
            close();
            throw e;
        }
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(mSocket);
        synchronized (this) {
            mSocket = null;
            mCommandStream = null;
            mResponseStream = null;
        }
    }

    @Override
    public synchronized void setCache(OutputStream os) {
        if (mSocket == null)
            return;

        mResponseStream.setCache(os);
    }

    @Override
    public synchronized boolean requestCompleted(boolean ok) {
        if (mSocket == null)
            return false;

        mLastRequest = System.nanoTime();
        mResponseStream.setCache(null);

        if (!ok || mMustCloseConnection || !mResponseStream.finishedReading())
            close();

        return ok;
    }

    /**
     * write (positive) integer to byte array
     */
    private static int writeInt(int val, int pos, byte[] buf) {
        if (val == 0) {
            buf[pos] = '0';
            return pos + 1;
        }

        int i = 0;
        for (int n = val; n > 0; n = n / 10, i++)
            buf[pos + i] = (byte) ('0' + n % 10);

        ArrayUtils.reverse(buf, pos, pos + i);

        return pos + i;
    }

    /**
     * parse (positive) integer from byte array
     */
    private static int parseInt(byte[] buf, int pos, int end) {
        int val = 0;
        for (; pos < end; pos++)
            val = val * 10 + (buf[pos]) - '0';

        return val;
    }

    private static boolean check(byte[] string, byte[] buffer,
                                 int position, int available) {

        int length = string.length;

        if (available - position < length)
            return false;

        for (int i = 0; i < length; i++)
            if (buffer[position + i] != string[i])
                return false;

        return true;
    }

    /**
     * @param tile the Tile
     * @param buf  to write url string
     * @param pos  current position
     * @return new position
     */
    private int formatTilePath(Tile tile, byte[] buf, int pos) {
        if (mTilePath == null) {
            String url = mTileSource.getUrlFormatter()
                    .formatTilePath(mTileSource, tile);
            byte[] b = url.getBytes();
            System.arraycopy(b, 0, buf, pos, b.length);
            return pos + b.length;
        }

        for (byte[] b : mTilePath) {
            if (b.length == 1) {
                if (b[0] == '/') {
                    buf[pos++] = '/';
                    continue;
                } else if (b[0] == 'X') {
                    pos = writeInt(tile.tileX, pos, buf);
                    continue;
                } else if (b[0] == 'Y') {
                    pos = writeInt(tile.tileY, pos, buf);
                    continue;
                } else if (b[0] == 'Z') {
                    pos = writeInt(tile.zoomLevel, pos, buf);
                    continue;
                }
            }
            System.arraycopy(b, 0, buf, pos, b.length);
            pos += b.length;
        }
        return pos;

    }

    public static class LwHttpFactory implements HttpEngine.Factory {
        private byte[][] mTilePath;

        @Override
        public HttpEngine create(UrlTileSource tileSource) {
            if (tileSource.getUrlFormatter() != UrlTileSource.URL_FORMATTER)
                return new LwHttp(tileSource, null);

            /* use optimized formatter replacing the default */
            if (mTilePath == null) {
                String[] path = tileSource.getTilePath();
                mTilePath = new byte[path.length][];
                for (int i = 0; i < path.length; i++)
                    mTilePath[i] = path[i].getBytes();
            }
            return new LwHttp(tileSource, mTilePath);
        }
    }
}
