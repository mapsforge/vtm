/*
 * Copyright 2016-2018 devemux86
 * Copyright 2019 telemaxx
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
package org.oscim.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Paint;
import org.oscim.core.GeoPoint;
import org.oscim.event.Gesture;
import org.oscim.event.GestureListener;
import org.oscim.event.MotionEvent;
import org.oscim.gdx.GdxMapApp;
import org.oscim.layers.Layer;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerInterface;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.marker.MarkerSymbol.HotspotPlace;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.map.Map;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.UrlTileSource;
import org.oscim.tiling.source.bitmap.DefaultSources;

public class MarkerLayerWithLabelsTest extends GdxMapApp
		implements ItemizedLayer.OnItemGestureListener<MarkerInterface> {

	private BitmapTileLayer mLayer = null;
	private BitmapTileLayer mShaded = null;

	ItemizedLayer mMarkerLayer;
	private int _fgColor = 0xFF000000; // 100 percent black. AARRGGBB
	private int _bgColor = 0x80FF69B4; // 50 percent pink. AARRGGBB

	class MapEventsReceiver extends Layer implements GestureListener {

		MapEventsReceiver(Map map) {
			super(map);
		}

		@Override
		public boolean onGesture(Gesture g, MotionEvent e) {
			if (g instanceof Gesture.Tap) {
				GeoPoint p = mMap.viewport().fromScreenPoint(e.getX(), e.getY());
				System.out.println("Map tap " + p);
				return true;
			}
			if (g instanceof Gesture.LongPress) {
				GeoPoint p = mMap.viewport().fromScreenPoint(e.getX(), e.getY());
				System.out.println("Map long press " + p);
				return true;
			}
			if (g instanceof Gesture.TripleTap) {
				GeoPoint p = mMap.viewport().fromScreenPoint(e.getX(), e.getY());
				System.out.println("Map triple tap " + p);
				return true;
			}
			return false;
		}
	}

	public static void main(String[] args) {
		GdxMapApp.init();
		GdxMapApp.run(new MarkerLayerWithLabelsTest());
	}

	/**
	 * creates a transparent symbol with text and description.
	 *
	 * @param mItem     -> the MarkerItem to process, containing title and
	 *                  description if description starts with a '#' the first line
	 *                  of the description is drawn.
	 * @param poiBitmap -> poi bitmap for the center
	 * @return MarkerSymbol with title, description and symbol
	 *
	 */
	public MarkerSymbol createAdvanceSymbol(MarkerItem mItem, Bitmap poiBitmap) {

		final Paint textPainter = CanvasAdapter.newPaint();
		textPainter.setStyle(Paint.Style.STROKE);
		textPainter.setColor(_fgColor);

		final Paint fillPainter = CanvasAdapter.newPaint();
		fillPainter.setStyle(Paint.Style.FILL);
		fillPainter.setColor(_bgColor);

		int margin = 3;
		int dist2symbol = 30;

		int titleWidth = ((int) textPainter.getTextWidth(mItem.title) + 2 * margin);
		int titleHeight = ((int) textPainter.getTextHeight(mItem.title) + 2 * margin);

		int symbolWidth = poiBitmap.getWidth();

		int subtitleWidth = 0;
		int subtitleHeight = 0;
		String subtitle = "";
		boolean hasSubtitle = false;
		if (mItem.description.length() > 1) {
			if (mItem.description.startsWith("#")) {
				subtitle = mItem.description.substring(1); // not the first # char
				subtitle = subtitle.split("\\R", 2)[0]; // only first line
				subtitleWidth = ((int) textPainter.getTextWidth(subtitle)) + 2 * margin;
				subtitleHeight = ((int) textPainter.getTextHeight(subtitle)) + 2 * margin;
				hasSubtitle = true;
			}
		}

		int xSize = java.lang.Math.max(titleWidth, subtitleWidth);
		xSize = java.lang.Math.max(xSize, symbolWidth);

		int ySize = titleHeight + symbolWidth + dist2symbol;

		// markerCanvas, the drawing area for all: title, description and symbol
		Bitmap markerBitmap = CanvasAdapter.newBitmap(xSize, ySize, 0);
		org.oscim.backend.canvas.Canvas markerCanvas = CanvasAdapter.newCanvas();
		markerCanvas.setBitmap(markerBitmap);

		// titleCanvas for the title text
		Bitmap titleBitmap = CanvasAdapter.newBitmap(titleWidth + margin, titleHeight + margin, 0);
		org.oscim.backend.canvas.Canvas titleCanvas = CanvasAdapter.newCanvas();
		titleCanvas.setBitmap(titleBitmap);

		{ // testing block
			/*
			 * the following three lines displaying a transparent box. only for testing
			 * purposes, normally uncommented
			 */
			// fillPainter.setColor(0x60ffffff);
			// markerCanvas.drawCircle(0, 0, xSize*2, fillPainter);
			// fillPainter.setColor(_bgColor);
		}

		// draw an oversized transparent circle, so the canvas is completely filled with
		// a transparent color
		// titleCanvas.fillRectangle() does not support transparency
		titleCanvas.drawCircle(0, 0, xSize * 2, fillPainter);

		titleCanvas.drawText(mItem.title, margin, titleHeight - margin, textPainter);

		if (hasSubtitle) {
			Bitmap subtitleBitmap = CanvasAdapter.newBitmap(subtitleWidth + margin, subtitleHeight + margin, 0);
			org.oscim.backend.canvas.Canvas subtitleCanvas = CanvasAdapter.newCanvas();
			subtitleCanvas.setBitmap(subtitleBitmap);
			subtitleCanvas.drawCircle(0, 0, xSize * 2, fillPainter);
			subtitleCanvas.drawText(subtitle, margin, titleHeight - margin, textPainter);
			markerCanvas.drawBitmap(subtitleBitmap, xSize / 2 - (subtitleWidth / 2), ySize - (subtitleHeight + margin));
		} else {

		}

		markerCanvas.drawBitmap(titleBitmap, xSize / 2 - (titleWidth / 2), 0);
		markerCanvas.drawBitmap(poiBitmap, xSize / 2 - (symbolWidth / 2), ySize / 2 - (symbolWidth / 2));

		return (new MarkerSymbol(markerBitmap, HotspotPlace.CENTER, true));
	}

	@Override
	public void createLayers() {
		try {
			// Map events receiver
			mMap.layers().add(new MapEventsReceiver(mMap));

			UrlTileSource tileSource = DefaultSources.OPENSTREETMAP.httpFactory(new OkHttpEngine.OkHttpFactory())
					.build();
			tileSource.setHttpRequestHeaders(Collections.singletonMap("User-Agent", "vtm-playground"));
			mMap.layers().add(new BitmapTileLayer(mMap, tileSource));

			// goto berlin
			mMap.setMapPosition(52.513452, 13.363791, 1 << 13);

			// pink dot
			Bitmap bitmapPoi = CanvasAdapter.decodeBitmap(getClass().getResourceAsStream("/res/marker_poi.png"));
			String markerRecource = "/res/marker_poi.png";
			if (getClass().getResourceAsStream(markerRecource) != null) {
				bitmapPoi = CanvasAdapter.decodeBitmap(getClass().getResourceAsStream(markerRecource));
			} else {
				int DefaultIconSize = 10;

				final Paint fillPainter = CanvasAdapter.newPaint();
				fillPainter.setStyle(Paint.Style.FILL);
				fillPainter.setColor(0xFFFF69B4); // 100percent pink

				bitmapPoi = CanvasAdapter.newBitmap(DefaultIconSize, DefaultIconSize, 0);
				org.oscim.backend.canvas.Canvas defaultMarkerCanvas = CanvasAdapter.newCanvas();
				defaultMarkerCanvas.setBitmap(bitmapPoi);

				defaultMarkerCanvas.drawCircle(DefaultIconSize / 2, DefaultIconSize / 2, DefaultIconSize / 2,
						fillPainter);

			}

			// Bitmap bitmapPoi =
			// CanvasAdapter.decodeBitmap(getClass().getResourceAsStream("/res/marker_poi2.png"));
			MarkerSymbol symbol = new MarkerSymbol(bitmapPoi, HotspotPlace.CENTER, false);

			mMarkerLayer = new ItemizedLayer(mMap, new ArrayList<MarkerInterface>(), symbol, this);
			mMap.layers().add(mMarkerLayer);

			// creating some poi's
			List<MarkerInterface> pts = new ArrayList<>();
			pts.add(new MarkerItem("Brandenburger Tor", "#1789-1793", new GeoPoint(52.516275, 13.377704)));
			pts.add(new MarkerItem("Siegessaeule, hidden description", "this is a hidden Description without a #",
					new GeoPoint(52.514543, 13.350119)));
			pts.add(new MarkerItem("Gleisdreieck, without description", "", new GeoPoint(52.499562, 13.374063)));
			pts.add(new MarkerItem(
					"Potsdamer Platz", "#this is multiline description\n" + "demonstrating that only\n"
							+ "the first line\n" + "is drawn on the map\n" + "the rest is surpressed",
					new GeoPoint(52.509352, 13.375739)));

			for (MarkerInterface mi : pts) {
				MarkerItem markerItem = (MarkerItem) mi; // casting
				System.out.println("title: " + markerItem.getTitle());
				markerItem.setMarker(createAdvanceSymbol(markerItem, bitmapPoi));
			}

			mMarkerLayer.addItems(pts);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onItemLongPress(int index, MarkerInterface item) {
		MarkerItem markerItem = (MarkerItem) item;
		try {
			if (markerItem.getMarker() == null) {
				String markerRecource = "/res/marker_poi.png";
				markerItem.setMarker(createAdvanceSymbol(markerItem,
						CanvasAdapter.decodeBitmap(getClass().getResourceAsStream(markerRecource))));
			} else {
				markerItem.setMarker(null);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Marker long press " + markerItem.getTitle());
		return true;
	}

	@Override
	public boolean onItemSingleTapUp(int index, MarkerInterface item) {
		MarkerItem markerItem = (MarkerItem) item;
		try {
			if (markerItem.getMarker() == null) {
				String markerRecource = "/res/marker_poi.png";
				markerItem.setMarker(createAdvanceSymbol(markerItem,
						CanvasAdapter.decodeBitmap(getClass().getResourceAsStream(markerRecource))));
			} else {
				markerItem.setMarker(null);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Marker tap " + markerItem.getTitle());
		return true;
	}
}