package org.oscim.test;

import org.oscim.awt.AwtGraphics;
import org.oscim.backend.AssetAdapter;
import org.oscim.core.GeometryBuffer.GeometryType;
import org.oscim.core.Tag;
import org.oscim.core.TagSet;
import org.oscim.theme.IRenderTheme;
import org.oscim.theme.ThemeLoader;
import org.oscim.theme.VtmThemes;
import org.oscim.theme.styles.RenderStyle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ThemeTest {

    public static void main(String[] args) {
        AwtGraphics.init();
        AssetAdapter.init(new AssetAdapter() {
            @Override
            public InputStream openFileAsStream(String name) {
                try {
                    return new FileInputStream("/home/jeff/src/vtm/OpenScienceMap/vtm/assets/"
                            + name);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });

        IRenderTheme t = ThemeLoader.load(VtmThemes.DEFAULT);

        TagSet tags = new TagSet();
        tags.add(new Tag("highway", "trunk_link"));
        tags.add(new Tag("brigde", "yes"));
        tags.add(new Tag("oneway", "yes"));

        RenderStyle[] ri = t.matchElement(GeometryType.LINE, tags, 16);

        for (RenderStyle r : ri) {
            System.out.println("class: " + r.getClass().getName());
        }
    }

}
