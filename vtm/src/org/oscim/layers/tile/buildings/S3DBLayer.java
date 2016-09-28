package org.oscim.layers.tile.buildings;

import org.oscim.backend.canvas.Color;
import org.oscim.layers.tile.TileLayer;
import org.oscim.layers.tile.TileManager;
import org.oscim.layers.tile.TileRenderer;
import org.oscim.map.Map;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.LayerRenderer;
import org.oscim.renderer.OffscreenRenderer;
import org.oscim.renderer.OffscreenRenderer.Mode;
import org.oscim.tiling.TileSource;
import org.oscim.utils.ColorUtil;
import org.oscim.utils.ColorsCSS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S3DBLayer extends TileLayer {
    static final Logger log = LoggerFactory.getLogger(S3DBLayer.class);

    private final static int MAX_CACHE = 32;
    private final static int SRC_ZOOM = 16;

    /* TODO get from theme */
    private final static double HSV_S = 0.7;
    private final static double HSV_V = 1.2;

    private final TileSource mTileSource;

    public S3DBLayer(Map map, TileSource tileSource) {
        this(map, tileSource, true, false);
    }

    public S3DBLayer(Map map, TileSource tileSource, boolean fxaa, boolean ssao) {
        super(map, new TileManager(map, MAX_CACHE));
        setRenderer(new S3DBRenderer(fxaa, ssao));

        mTileManager.setZoomLevel(SRC_ZOOM, SRC_ZOOM);
        mTileSource = tileSource;
        initLoader(2);
    }

    @Override
    protected S3DBTileLoader createLoader() {
        return new S3DBTileLoader(getManager(), mTileSource);
    }

    public static class S3DBRenderer extends TileRenderer {
        LayerRenderer mRenderer;

        public S3DBRenderer(boolean fxaa, boolean ssao) {
            mRenderer = new BuildingRenderer(this, SRC_ZOOM, SRC_ZOOM, true, false);

            if (fxaa || ssao) {
                Mode mode = Mode.FXAA;
                if (fxaa && ssao)
                    mode = Mode.SSAO_FXAA;
                else if (ssao)
                    mode = Mode.SSAO;
                mRenderer = new OffscreenRenderer(mode, mRenderer);
            }
        }

        @Override
        public synchronized void update(GLViewport v) {
            super.update(v);
            mRenderer.update(v);
            setReady(mRenderer.isReady());
        }

        @Override
        public synchronized void render(GLViewport v) {
            mRenderer.render(v);
        }

        @Override
        public boolean setup() {
            mRenderer.setup();
            return super.setup();
        }
    }

    static int getColor(String color, boolean roof) {

        if (color.charAt(0) == '#') {
            int c = Color.parseColor(color, Color.CYAN);
            /* hardcoded colors are way too saturated for my taste */
            return ColorUtil.modHsv(c, 1.0, 0.4, HSV_V, true);
        }

        if (roof) {
            if ("brown".equals(color))
                return Color.get(120, 110, 110);
            if ("red".equals(color))
                return Color.get(235, 140, 130);
            if ("green".equals(color))
                return Color.get(150, 200, 130);
            if ("blue".equals(color))
                return Color.get(100, 50, 200);
        }
        if ("white".equals(color))
            return Color.get(240, 240, 240);
        if ("black".equals(color))
            return Color.get(86, 86, 86);
        if ("grey".equals(color) || "gray".equals(color))
            return Color.get(120, 120, 120);
        if ("red".equals(color))
            return Color.get(255, 190, 190);
        if ("green".equals(color))
            return Color.get(190, 255, 190);
        if ("blue".equals(color))
            return Color.get(190, 190, 255);
        if ("yellow".equals(color))
            return Color.get(255, 255, 175);
        if ("darkgray".equals(color) || "darkgrey".equals(color))
            return Color.DKGRAY;
        if ("lightgray".equals(color) || "lightgrey".equals(color))
            return Color.LTGRAY;

        if ("transparent".equals(color))
            return Color.get(0, 1, 1, 1);

        Integer css = ColorsCSS.get(color);

        if (css != null)
            return ColorUtil.modHsv(css, 1.0, HSV_S, HSV_V, true);

        log.debug("unknown color:{}", color);
        return 0;
    }

    static int getMaterialColor(String material, boolean roof) {

        if (roof) {
            if ("glass".equals(material))
                return Color.fade(Color.get(130, 224, 255), 0.9f);
        }
        if ("roof_tiles".equals(material))
            return Color.get(216, 167, 111);
        if ("tile".equals(material))
            return Color.get(216, 167, 111);

        if ("concrete".equals(material) ||
                "cement_block".equals(material))
            return Color.get(210, 212, 212);

        if ("metal".equals(material))
            return 0xFFC0C0C0;
        if ("tar_paper".equals(material))
            return 0xFF969998;
        if ("eternit".equals(material))
            return Color.get(216, 167, 111);
        if ("tin".equals(material))
            return 0xFFC0C0C0;
        if ("asbestos".equals(material))
            return Color.get(160, 152, 141);
        if ("glass".equals(material))
            return Color.get(130, 224, 255);
        if ("slate".equals(material))
            return 0xFF605960;
        if ("zink".equals(material))
            return Color.get(180, 180, 180);
        if ("gravel".equals(material))
            return Color.get(170, 130, 80);
        if ("copper".equals(material))
            // same as roof color:green
            return Color.get(150, 200, 130);
        if ("wood".equals(material))
            return Color.get(170, 130, 80);
        if ("grass".equals(material))
            return 0xFF50AA50;
        if ("stone".equals(material))
            return Color.get(206, 207, 181);
        if ("plaster".equals(material))
            return Color.get(236, 237, 181);
        if ("brick".equals(material))
            return Color.get(255, 217, 191);
        if ("stainless_steel".equals(material))
            return Color.get(153, 157, 160);
        if ("gold".equals(material))
            return 0xFFFFD700;

        log.debug("unknown material:{}", material);

        return 0;
    }
}
