package org.oscim.ios;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import org.oscim.backend.GLAdapter;
import org.oscim.ios.backend.IosGL;
import org.oscim.ios.backend.IosGraphics;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.map.Map;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.glkit.GLKViewDrawableStencilFormat;
import org.robovm.apple.uikit.UIApplication;

public class RobovmLauncher extends IOSApplication.Delegate {


    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        config.orientationLandscape = true;
        config.orientationPortrait = true;
        config.stencilFormat = GLKViewDrawableStencilFormat._8;


        IOSMapApp iosMapApp = new IOSMapApp() {
            @Override
            public void createLayers() {
                Map map = getMap();

                VectorTileLayer l = map.setBaseMap(new OSciMap4TileSource());

                map.layers().add(new BuildingLayer(map, l));
                map.layers().add(new LabelLayer(map, l));

                map.setTheme(VtmThemes.DEFAULT);
                map.setMapPosition(53.075, 8.808, 1 << 17);
            }

        };

        iosMapApp.init();

        return new IOSApplication(iosMapApp, config);
    }

    public static void main(String[] argv) {

        NSAutoreleasePool pool = new NSAutoreleasePool();
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
        IosGraphics.init();
        GLAdapter.init(new IosGL());

        UIApplication.main(argv, null, RobovmLauncher.class);
        pool.drain();
    }
}
