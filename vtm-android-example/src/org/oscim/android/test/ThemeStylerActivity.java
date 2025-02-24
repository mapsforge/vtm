package org.oscim.android.test;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;
import org.oscim.backend.canvas.Color;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.map.Layers;
import org.oscim.renderer.MapRenderer;
import org.oscim.theme.RenderTheme;
import org.oscim.theme.internal.VtmThemes;
import org.oscim.theme.rule.Rule;
import org.oscim.theme.rule.Rule.RuleVisitor;
import org.oscim.theme.styles.AreaStyle;
import org.oscim.theme.styles.AreaStyle.AreaBuilder;
import org.oscim.theme.styles.LineStyle;
import org.oscim.theme.styles.LineStyle.LineBuilder;
import org.oscim.theme.styles.RenderStyle;

import java.util.logging.Logger;

public class ThemeStylerActivity extends BaseMapActivity implements OnSeekBarChangeListener {
    final Logger log = Logger.getLogger(ThemeStylerActivity.class.getName());

    public ThemeStylerActivity() {
        super(R.layout.activity_map_styler);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((SeekBar) findViewById(R.id.seekBarH)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.seekBarS)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.seekBarV)).setOnSeekBarChangeListener(this);

        Layers layers = mMap.layers();
        layers.add(new BuildingLayer(mMap, mBaseLayer));
        layers.add(new LabelLayer(mMap, mBaseLayer));

        mMap.setTheme(VtmThemes.MOTORIDER);
    }

    class ModStyleVisitor extends RuleVisitor {
        private final LineBuilder<?> lineBuilder = LineStyle.builder();
        private final AreaBuilder<?> areaBuilder = AreaStyle.builder();

        @Override
        public void apply(Rule r) {
            for (RenderStyle style : r.styles) {

                if (style instanceof LineStyle) {
                    LineStyle s = (LineStyle) style;
                    HSV c = lineColor;
                    if (lineColor.changed && s.outline)
                        continue;

                    if (outlineColor.changed) {
                        if (!s.outline)
                            continue;
                        c = outlineColor;
                    }

                    s.set(lineBuilder.set(s)
                            .color(modColor(s.color, c))
                            .stippleColor(modColor(s.stippleColor, c))
                            .build());
                    continue;
                }

                if (areaColor.changed && style instanceof AreaStyle) {
                    AreaStyle s = (AreaStyle) style;

                    s.set(areaBuilder.set(s)
                            .color(modColor(s.color, areaColor))
                            .blendColor(modColor(s.blendColor, areaColor))
                            .strokeColor(modColor(s.strokeColor, areaColor))
                            .build());
                }
            }
            super.apply(r);
        }
    }

    int modColor(int color, HSV hsv) {
        return hsv.mod(color, true);
    }

    public static class HSV extends Color.HSV {
        public boolean changed;
    }

    HSV lineColor = new HSV();
    HSV outlineColor = new HSV();
    HSV areaColor = new HSV();

    ModStyleVisitor mStyleVisitor = new ModStyleVisitor();

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser)
            return;
        int id = seekBar.getId();

        boolean modLine = ((RadioButton) findViewById(R.id.checkBoxLine)).isChecked();
        boolean modArea = ((RadioButton) findViewById(R.id.checkBoxArea)).isChecked();

        HSV c;
        if (modArea)
            c = areaColor;
        else if (modLine)
            c = lineColor;
        else
            c = outlineColor;

        if (id == R.id.seekBarS)
            c.saturation = progress / 50f;
        else if (id == R.id.seekBarV)
            c.value = progress / 50f;
        else if (id == R.id.seekBarH)
            c.hue = progress / 100f;

        log.fine((modArea ? "area" : "line")
                + " h:" + c.hue
                + " s:" + c.saturation
                + " v:" + c.value);

        VectorTileLayer l = (VectorTileLayer) mMap.layers().get(1);
        RenderTheme t = (RenderTheme) l.getTheme();

        c.changed = true;
        t.traverseRules(mStyleVisitor);
        t.updateStyles();
        c.changed = false;

        if (modArea)
            MapRenderer.setBackgroundColor(modColor(t.getMapBackground(), c));

        mMap.render();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public void onToggleControls(View view) {
        findViewById(R.id.controls).setVisibility(((ToggleButton) view).isChecked() ?
                View.VISIBLE : View.GONE);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        if (!checked)
            return;

        HSV c = null;
        int id = view.getId();
        if (id == R.id.checkBoxArea) {
            c = areaColor;
        } else if (id == R.id.checkBoxLine) {
            c = lineColor;
        } else if (id == R.id.checkBoxOutline) {
            c = outlineColor;
        }
        if (c == null)
            return;
        ((SeekBar) findViewById(R.id.seekBarS)).setProgress((int) (c.saturation * 50));
        ((SeekBar) findViewById(R.id.seekBarV)).setProgress((int) (c.value * 50));
        ((SeekBar) findViewById(R.id.seekBarH)).setProgress((int) (c.hue * 100));
    }
}
