package org.oscim.ios;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import org.oscim.backend.GLAdapter;
import org.oscim.ios.backend.IosGL;
import org.oscim.ios.backend.IosGraphics;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.glkit.GLKViewDrawableStencilFormat;
import org.robovm.apple.uikit.UIApplication;

public class RobovmLauncher extends IOSApplication.Delegate {

    static {
        new SharedLibraryLoader().load("vtm-jni");
    }


    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        config.orientationLandscape = true;
        config.orientationPortrait = true;
        config.stencilFormat = GLKViewDrawableStencilFormat._8;

        return new IOSApplication(new IOSMapApp() {
            @Override
            public void createLayers() {
                TileSource tileSource = new OSciMap4TileSource();

                //initDefaultLayers(tileSource, false,true, false);
                VectorTileLayer l = mMap.setBaseMap(tileSource);
                mMap.setTheme(VtmThemes.NEWTRON);
                mMap.layers().add(new BuildingLayer(mMap, l));
                mMap.layers().add(new LabelLayer(mMap, l));

                // mMap.getLayers().add(new GenericLayer(mMap, new
                // GridRenderer(1,new Line(Color.LTGRAY, 1.2f),null)));

                mMap.setMapPosition(53.1, 8.8, 1 << 14);
            }

        }, config);
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
