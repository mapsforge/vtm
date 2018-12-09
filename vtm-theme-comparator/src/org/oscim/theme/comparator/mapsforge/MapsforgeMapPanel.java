/*
 * Copyright 2017 Longri
 * Copyright 2017 devemux86
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
package org.oscim.theme.comparator.mapsforge;

import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.awt.graphics.AwtGraphicFactory;
import org.mapsforge.map.awt.input.MapViewComponentListener;
import org.mapsforge.map.awt.util.JavaPreferences;
import org.mapsforge.map.awt.view.MapView;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.LayerManager;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.cache.FileSystemTileCache;
import org.mapsforge.map.layer.cache.InMemoryTileCache;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.cache.TwoLevelTileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.IMapViewPosition;
import org.mapsforge.map.model.Model;
import org.mapsforge.map.model.common.PreferencesFacade;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.ExternalRenderTheme;
import org.mapsforge.map.rendertheme.InternalRenderTheme;
import org.mapsforge.map.rendertheme.XmlRenderTheme;
import org.oscim.core.Tile;
import org.oscim.theme.comparator.BothMapPositionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.UUID;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class MapsforgeMapPanel extends JPanel {
    private static final Logger log = LoggerFactory.getLogger(MapsforgeMapPanel.class);
    private final GraphicFactory GRAPHIC_FACTORY = AwtGraphicFactory.INSTANCE;
    private AwtMapView mapView;

    private File mapFile, themeFile;

    public MapsforgeMapPanel() {
        this.setLayout(null);
        mapView = createMapView();

        // Use same tile size with VTM
        mapView.getModel().displayModel.setFixedTileSize(Tile.SIZE);

        PreferencesFacade preferencesFacade = new JavaPreferences(Preferences.userNodeForPackage(MapView.class));
        final Model model = mapView.getModel();
        model.init(preferencesFacade);

        this.setBorder(BorderFactory.createTitledBorder("MAPSFORGE-Map"));
        this.add(mapView);
        mapView.setVisible(true);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                mapView.setBounds(10, 20, getWidth() - 20, getHeight() - 30);
            }

            public void componentMoved(ComponentEvent e) {
                super.componentMoved(e);
                mapView.setBounds(10, 20, getWidth() - 20, getHeight() - 30);
            }
        });

    }

    private void addLayers(MapView mapView, File mapPath, File themePath) {
        LayerManager layerManager = mapView.getLayerManager();
        Layers layers = layerManager.getLayers();
        TileCache tileCache = createTileCache();
        layers.clear();
        layers.add(createTileRendererLayer(tileCache, mapView.getModel().mapViewPosition, mapPath, themePath));
    }

    private Layer createTileRendererLayer(TileCache tileCache, IMapViewPosition mapViewPosition, File mapFile, File themeFile) {

        TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, new MapFile(mapFile), mapViewPosition, false, true, false, GRAPHIC_FACTORY);

        if (themeFile != null) {

            XmlRenderTheme renderTheme;
            try {
                renderTheme = new ExternalRenderTheme(themeFile);
            } catch (FileNotFoundException e) {
                renderTheme = InternalRenderTheme.OSMARENDER;
            }
            tileRendererLayer.setXmlRenderTheme(renderTheme);

        } else {
            tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
        }

        return tileRendererLayer;
    }

    private AwtMapView createMapView() {
        AwtMapView mapView = new AwtMapView();
        mapView.getFpsCounter().setVisible(false);
        mapView.addComponentListener(new MapViewComponentListener(mapView));
        return mapView;
    }

    private TileCache createTileCache() {
        TileCache firstLevelTileCache = new InMemoryTileCache(64);
        File cacheDirectory = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
        TileCache secondLevelTileCache = new FileSystemTileCache(1024, cacheDirectory, GRAPHIC_FACTORY);
        return new TwoLevelTileCache(firstLevelTileCache, secondLevelTileCache);
    }

    public void destroy() {
        mapView.destroyAll();
        AwtGraphicFactory.clearResourceMemoryCache();
    }

    public void loadMap(File mapFile, File themeFile) {
        log.debug("reload MAP:{} THEME:{}", mapFile, themeFile);
        this.mapFile = mapFile;
        this.themeFile = themeFile;
        addLayers(mapView, this.mapFile, this.themeFile);
    }

    public void setCoordinate(double latidude, double longitude, byte zoomLevel) {
        mapView.setCenter(new LatLong(latidude, longitude));
        mapView.setZoomLevel(zoomLevel);
    }

    public void setMapPositionHandler(BothMapPositionHandler mapPositionHandler) {
        mapView.setMapPositionHandler(mapPositionHandler);
    }

    public void setTheme(String themePath) {
        loadMap(this.mapFile, new File(themePath));
    }
}