package org.oscim.web.js;

import org.oscim.core.MapPosition;
import org.oscim.layers.Layer;
import org.oscim.map.Layers;
import org.oscim.map.Map;
import org.oscim.theme.IRenderTheme;
import org.oscim.theme.ThemeLoader;
import org.oscim.theme.internal.VtmThemes;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.NoExport;

@ExportPackage("")
@Export("map")
public class JsMap implements Exportable {

    static Map mMap;

    @Export
    public static Map map() {
        return mMap;
    }

    @Export
    public static Layers layers() {
        return mMap.layers();
    }

    @Export
    public static boolean addLayer(Layer l) {
        return mMap.layers().add(l);
    }

    @Export
    public static boolean getPosition(MapPosition pos) {
        return mMap.getMapPosition(pos);
    }

    @Export
    public static void setPosition(MapPosition pos) {
        mMap.setMapPosition(pos);
    }

    @Export
    public static IRenderTheme loadTheme(String theme) {
        return ThemeLoader.load(VtmThemes.valueOf(theme));
    }

    @NoExport
    public static void init(Map map) {
        mMap = map;
    }

    //    @ExportInstanceMethod("foo")
    //    public static String instanceMethod(Map instance, String surname) {
    //      return instance.getName() + "-" + surname;
    //    }
}
