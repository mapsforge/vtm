/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2017 devemux86
 * Copyright 2017 Andrey Novikov
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
package org.oscim.tiling;

import org.oscim.layers.tile.bitmap.BitmapTileLayer.FadeStep;

import java.util.HashMap;

public abstract class TileSource {

    public abstract static class Builder<T extends Builder<T>> {
        protected int zoomMin, zoomMax;
        protected FadeStep[] fadeSteps;
        protected String name;
        protected int tileSize;

        public T zoomMin(int zoom) {
            zoomMin = zoom;
            return self();
        }

        public T zoomMax(int zoom) {
            zoomMax = zoom;
            return self();
        }

        public T fadeSteps(FadeStep[] fadeSteps) {
            this.fadeSteps = fadeSteps;
            return self();
        }

        public T name(String name) {
            this.name = name;
            return self();
        }

        public T tileSize(int tileSize) {
            this.tileSize = tileSize;
            return self();
        }

        @SuppressWarnings("unchecked")
        protected T self() {
            return (T) this;
        }

        public abstract TileSource build();
    }

    protected int mZoomMin = 0;
    protected int mZoomMax = 20;
    protected String mName;
    protected int mTileSize = 256;

    protected TileSource() {
    }

    protected TileSource(int zoomMin, int zoomMax) {
        mZoomMin = zoomMin;
        mZoomMax = zoomMax;
    }

    public TileSource(Builder<?> builder) {
        mZoomMin = builder.zoomMin;
        mZoomMax = builder.zoomMax;
        mFadeSteps = builder.fadeSteps;
        mName = builder.name;
        mTileSize = builder.tileSize;
    }

    public abstract ITileDataSource getDataSource();

    public abstract OpenResult open();

    public abstract void close();

    protected final Options options = new Options();

    public ITileCache tileCache;

    private FadeStep[] mFadeSteps;

    /**
     * Cache MUST be set before TileSource is added to a TileLayer!
     */
    public void setCache(ITileCache cache) {
        tileCache = cache;
    }

    public int getZoomLevelMax() {
        return mZoomMax;
    }

    public int getZoomLevelMin() {
        return mZoomMin;
    }

    public void setFadeSteps(FadeStep[] fadeSteps) {
        mFadeSteps = fadeSteps;
    }

    public FadeStep[] getFadeSteps() {
        return mFadeSteps;
    }

    public String getName() {
        return mName;
    }

    public int getTileSize() {
        return mTileSize;
    }

    public TileSource setOption(String key, String value) {
        options.put(key, value);
        return this;
    }

    public String getOption(String key) {
        return options.get(key);
    }

    public static class Options extends HashMap<String, String> {

        private static final long serialVersionUID = 1L;

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Options))
                return false;

            //if (this.db != ((MapOptions) other).db)
            //    return false;

            // FIXME test if this is correct!
            if (!this.entrySet().equals(((Options) other).entrySet()))
                return false;

            return true;
        }
    }

    /**
     * A FileOpenResult is a simple DTO which is returned by
     * IMapDatabase#open().
     */
    public static class OpenResult {
        /**
         * Singleton for a FileOpenResult instance with {@code success=true}.
         */
        public static final OpenResult SUCCESS = new OpenResult();

        private final String errorMessage;
        private final boolean success;

        /**
         * @param errorMessage a textual message describing the error, must not be null.
         */
        public OpenResult(String errorMessage) {
            if (errorMessage == null) {
                throw new IllegalArgumentException("error message must not be null");
            }

            this.success = false;
            this.errorMessage = errorMessage;
        }

        public OpenResult() {
            this.success = true;
            this.errorMessage = null;
        }

        /**
         * @return a textual error description (might be null).
         */
        public String getErrorMessage() {
            return this.errorMessage;
        }

        /**
         * @return true if the file could be opened successfully, false
         * otherwise.
         */
        public boolean isSuccess() {
            return this.success;
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append("FileOpenResult [success=")
                    .append(this.success)
                    .append(", errorMessage=")
                    .append(this.errorMessage)
                    .append("]")
                    .toString();
        }
    }
}
