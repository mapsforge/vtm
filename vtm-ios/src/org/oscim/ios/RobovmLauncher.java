package org.oscim.ios;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.GLAdapter;
import org.oscim.ios.backend.IosGL;
import org.oscim.ios.backend.IosGraphics;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.map.Map;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.glkit.GLKViewDrawableColorFormat;
import org.robovm.apple.glkit.GLKViewDrawableMultisample;
import org.robovm.apple.glkit.GLKViewDrawableStencilFormat;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIDevice;
import org.robovm.apple.uikit.UIScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RobovmLauncher extends IOSApplication.Delegate {

    static final Logger log = LoggerFactory.getLogger(RobovmLauncher.class);


    @Override
    protected IOSApplication createApplication() {


        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        config.orientationLandscape = true;
        config.orientationPortrait = true;
        config.stencilFormat = GLKViewDrawableStencilFormat._8;
        config.multisample = GLKViewDrawableMultisample._4X;
        config.colorFormat = GLKViewDrawableColorFormat.RGBA8888;
//        config.displayScaleLargeScreenIfRetina = 2.0f;
//        config.displayScaleLargeScreenIfNonRetina = 2.0f;
//        config.displayScaleSmallScreenIfNonRetina = 2.0f;
//        config.displayScaleSmallScreenIfRetina = 2.0f;


        float scale = (float)(getIosVersion() >= 8 ? UIScreen.getMainScreen().getNativeScale() : UIScreen.getMainScreen()
                .getScale());

       // CanvasAdapter.dpi *= scale;

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


    private int getIosVersion () {
        String systemVersion = UIDevice.getCurrentDevice().getSystemVersion();
        int version = Integer.parseInt(systemVersion.split("\\.")[0]);
        return version;
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
