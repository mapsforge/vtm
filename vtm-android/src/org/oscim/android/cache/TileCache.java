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
package org.oscim.android.cache;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.os.ParcelFileDescriptor;

import org.oscim.core.Tile;
import org.oscim.tiling.ITileCache;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class TileCache implements ITileCache {

    final static org.slf4j.Logger log = LoggerFactory.getLogger(TileCache.class);
    final static boolean dbg = false;

    class CacheTileReader implements TileReader {
        final InputStream mInputStream;
        final Tile mTile;

        public CacheTileReader(Tile tile, InputStream is) {
            mTile = tile;
            mInputStream = is;
        }

        @Override
        public Tile getTile() {
            return mTile;
        }

        @Override
        public InputStream getInputStream() {
            return mInputStream;
        }
    }

    class CacheTileWriter implements TileWriter {
        final ByteArrayOutputStream mOutputStream;
        final Tile mTile;

        CacheTileWriter(Tile tile, ByteArrayOutputStream os) {
            mTile = tile;
            mOutputStream = os;
        }

        @Override
        public Tile getTile() {
            return mTile;
        }

        @Override
        public OutputStream getOutputStream() {
            return mOutputStream;
        }

        @Override
        public void complete(boolean success) {
            saveTile(mTile, mOutputStream, success);
        }
    }

    private final ArrayList<ByteArrayOutputStream> mCacheBuffers;
    private final SQLiteHelper dbHelper;
    private final SQLiteDatabase mDatabase;
    private final SQLiteStatement mStmtGetTile;
    private final SQLiteStatement mStmtPutTile;

    //private final SQLiteStatement mStmtUpdateTile;

    public void dispose() {
        if (mDatabase.isOpen())
            mDatabase.close();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public TileCache(Context context, String cacheDirectory, String dbName) {
        if (dbg)
            log.debug("open cache {}, {}", cacheDirectory, dbName);

        dbHelper = new SQLiteHelper(context, new File(cacheDirectory, dbName).getAbsolutePath());

        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
            dbHelper.setWriteAheadLoggingEnabled(true);

        mDatabase = dbHelper.getWritableDatabase();

        mStmtGetTile = mDatabase.compileStatement("" +
                "SELECT " + COLUMN_DATA +
                " FROM " + TABLE_NAME +
                " WHERE x=? AND y=? AND z = ?");

        mStmtPutTile = mDatabase.compileStatement("" +
                "INSERT INTO " + TABLE_NAME +
                " (x, y, z, time, last_access, data)" +
                " VALUES(?,?,?,?,?,?)");

        //mStmtUpdateTile = mDatabase.compileStatement("" +
        //        "UPDATE " + TABLE_NAME +
        //        "  SET last_access=?" +
        //        "  WHERE x=? AND y=? AND z=?");

        mCacheBuffers = new ArrayList<ByteArrayOutputStream>();
    }

    @Override
    public TileWriter writeTile(Tile tile) {
        ByteArrayOutputStream os;

        synchronized (mCacheBuffers) {
            if (mCacheBuffers.size() == 0)
                os = new ByteArrayOutputStream(32 * 1024);
            else
                os = mCacheBuffers.remove(mCacheBuffers.size() - 1);
        }
        return new CacheTileWriter(tile, os);
    }

    static final String TABLE_NAME = "tiles";
    static final String COLUMN_TIME = "time";
    static final String COLUMN_ACCESS = "last_access";
    static final String COLUMN_DATA = "data";

    //static final String COLUMN_SIZE = "size";

    class SQLiteHelper extends SQLiteOpenHelper {

        //private static final String DATABASE_NAME = "tile.db";
        private static final int DATABASE_VERSION = 1;

        private static final String TILE_SCHEMA =
                "CREATE TABLE "
                        + TABLE_NAME + "("
                        + "x INTEGER NOT NULL,"
                        + "y INTEGER NOT NULL,"
                        + "z INTEGER NOT NULL,"
                        + COLUMN_TIME + " LONG NOT NULL,"
                        //+ COLUMN_SIZE + " LONG NOT NULL,"
                        + COLUMN_ACCESS + " LONG NOT NULL,"
                        + COLUMN_DATA + " BLOB,"
                        + "PRIMARY KEY(x,y,z));";

        public SQLiteHelper(Context context, String dbName) {
            super(context, dbName, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            log.debug("create table");
            db.execSQL(TILE_SCHEMA);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            log.debug("drop table");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    public void saveTile(Tile tile, ByteArrayOutputStream data, boolean success) {
        byte[] bytes = null;

        if (success)
            bytes = data.toByteArray();

        synchronized (mCacheBuffers) {
            data.reset();
            mCacheBuffers.add(data);
        }

        if (dbg)
            log.debug("store tile {} {}", tile, Boolean.valueOf(success));

        if (!success)
            return;

        synchronized (mStmtPutTile) {
            mStmtPutTile.bindLong(1, tile.tileX);
            mStmtPutTile.bindLong(2, tile.tileY);
            mStmtPutTile.bindLong(3, tile.zoomLevel);
            mStmtPutTile.bindLong(4, 0);
            mStmtPutTile.bindLong(5, 0);
            mStmtPutTile.bindBlob(6, bytes);

            mStmtPutTile.execute();
            mStmtPutTile.clearBindings();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public TileReader getTileApi11(Tile tile) {
        InputStream in = null;

        mStmtGetTile.bindLong(1, tile.tileX);
        mStmtGetTile.bindLong(2, tile.tileY);
        mStmtGetTile.bindLong(3, tile.zoomLevel);

        try {
            ParcelFileDescriptor result = mStmtGetTile.simpleQueryForBlobFileDescriptor();
            in = new FileInputStream(result.getFileDescriptor());
        } catch (SQLiteDoneException e) {
            log.debug("not in cache {}", tile);
            return null;
        } finally {
            mStmtGetTile.clearBindings();
        }

        if (dbg)
            log.debug("load tile {}", tile);

        return new CacheTileReader(tile, in);
    }

    private final String[] mQueryVals = new String[3];

    @Override
    public synchronized TileReader getTile(Tile tile) {

        //if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
        //    return getTileApi11(tile);

        mQueryVals[0] = String.valueOf(tile.zoomLevel);
        mQueryVals[1] = String.valueOf(tile.tileX);
        mQueryVals[2] = String.valueOf(tile.tileY);

        Cursor cursor = mDatabase.rawQuery("SELECT " + COLUMN_DATA +
                " FROM " + TABLE_NAME +
                " WHERE z=? AND x=? AND y=?", mQueryVals);

        if (!cursor.moveToFirst()) {
            if (dbg)
                log.debug("not in cache {}", tile);

            cursor.close();
            return null;
        }

        InputStream in = new ByteArrayInputStream(cursor.getBlob(0));
        cursor.close();

        if (dbg)
            log.debug("load tile {}", tile);

        return new CacheTileReader(tile, in);
    }

    @Override
    public void setCacheSize(long size) {
    }
}
