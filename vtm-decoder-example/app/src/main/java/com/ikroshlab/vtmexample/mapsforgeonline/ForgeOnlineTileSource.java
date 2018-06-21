/*
 * Copyright 2018 ikroshlab.com
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
package com.ikroshlab.vtmexample.mapsforgeonline;

import android.util.Log;

import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.source.UrlTileDataSource;
import org.oscim.tiling.source.UrlTileSource;

import java.util.HashMap;
import java.util.Map;


public class ForgeOnlineTileSource extends UrlTileSource {

    private final static String DEFAULT_URL  = "http://vps348222.ovh.net:8081/OVT_service/ovt";
    private final static String DEFAULT_PATH = "/?z={Z}&x={X}&y={Y}";


	// constructors
    public ForgeOnlineTileSource() {
        super(DEFAULT_URL, DEFAULT_PATH);
        Map<String, String> opt = new HashMap<>();
        opt.put("User-Agent",      "vtm/0.5.9");
        opt.put("Accept",          "*/*");
        opt.put("Accept-Encoding", "*");
        opt.put("Connection",      "Keep-Alive");
        setHttpRequestHeaders(opt);
    }

    public ForgeOnlineTileSource(int zoomMin, int zoomMax) {
        super(DEFAULT_URL, DEFAULT_PATH, zoomMin, zoomMax);
        Map<String, String> opt = new HashMap<>();
        opt.put("User-Agent",      "vtm/0.5.9");
        opt.put("Accept",          "*/*");
        opt.put("Accept-Encoding", "*");
        opt.put("Connection",      "Keep-Alive");
        setHttpRequestHeaders(opt);
    }



	@Override
	public ITileDataSource getDataSource() {
		return new UrlTileDataSource(this, new TileDecoder(), getHttpEngine());
	}



    // handling Logging:
    private void logDebug(String msg) { Log.d("VTM", " ### " + getClass().getName() + " ###  " + msg); }
}
