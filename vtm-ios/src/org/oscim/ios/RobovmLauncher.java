package org.oscim.ios;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import org.oscim.backend.CanvasAdapter;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.glkit.GLKViewDrawableStencilFormat;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIDevice;
import org.robovm.apple.uikit.UIScreen;

public class RobovmLauncher extends IOSApplication.Delegate {

    @Override
    protected IOSApplication createApplication() {

        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        config.orientationLandscape = true;
        config.orientationPortrait = true;
        config.stencilFormat = GLKViewDrawableStencilFormat._8;

        float scale = (float) (getIosVersion() >= 8 ? UIScreen.getMainScreen().getNativeScale() : UIScreen.getMainScreen()
                .getScale());

        CanvasAdapter.dpi *= scale;

        IOSMapApp iosMapApp = new IOSMapApp();

        IOSMapApp.init();

        return new IOSApplication(iosMapApp, config);
    }


    private int getIosVersion() {
        String systemVersion = UIDevice.getCurrentDevice().getSystemVersion();
        return Integer.parseInt(systemVersion.split("\\.")[0]);
    }


    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
        IOSMapApp.init();

        UIApplication.main(argv, null, RobovmLauncher.class);
        pool.drain();
    }
}
