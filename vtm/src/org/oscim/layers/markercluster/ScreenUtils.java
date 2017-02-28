package org.oscim.layers.markercluster;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Canvas;
import org.oscim.backend.canvas.Paint;

/**
 * A simple utility class to make ClusteredItemizedLayer folder self-contained.
 * Includes a method to translate between DPs and PXs and a circular icon generator
 *
 * @author Rodolfo Lopez Pintor 2017 <rlp@nebular.tv>
 */

class ScreenUtils {

    /** https://developer.android.com/reference/android/util/DisplayMetrics.html#DENSITY_DEFAULT */
    private final static float REFERENCE_DPI = 160;

    /**
     * Get pixels from DPs
     *
     * @param dp Value in DPs
     * @return Value in PX according to screen density
     */

    static int getPixels(float dp) {
        return (int) (CanvasAdapter.dpi / REFERENCE_DPI * dp);
    }

    @SuppressWarnings("unused")
    static class ClusterDrawable {

        private Paint mPaintText = CanvasAdapter.newPaint();
        private Paint mPaintCircle = CanvasAdapter.newPaint(), mPaintBorder = CanvasAdapter.newPaint();
        private int mSize;
        private String mText;


        /**
         * Generates a circle with a number inside
         * @param sizedp Size in DPs
         * @param foregroundColor Foreground
         * @param backgroundColor Background
         * @param text Text inside. Will only work for a single character !
         */

        public ClusterDrawable(int sizedp, int foregroundColor, int backgroundColor, String text) {
            setup(sizedp, foregroundColor, backgroundColor);
            setText(text);
        }

        private void setup(int sizedp, int foregroundColor, int backgroundColor) {
            mSize = ScreenUtils.getPixels(sizedp);
            mPaintText.setTextSize(ScreenUtils.getPixels((int) (sizedp * 0.6666666)));
            mPaintText.setColor(foregroundColor);

            // NOT SUPPORTED on current backends (Feb 2017)
            // mPaintText.setTextAlign(Paint.Align.CENTER);

            mPaintCircle.setColor(backgroundColor);
            mPaintCircle.setStyle(Paint.Style.FILL);

            mPaintBorder.setColor(foregroundColor);
            mPaintBorder.setStyle(Paint.Style.STROKE);
            mPaintBorder.setStrokeWidth(2.0f);
        }

        private void setText(String text) {
            mText = text;
        }

        private void draw(Canvas canvas) {

            int halfsize = mSize >> 1;

            // outline
            canvas.drawCircle(halfsize, halfsize, halfsize, mPaintCircle);
            // fill
            canvas.drawCircle(halfsize, halfsize, halfsize, mPaintBorder);
            // draw the number ... the centering is ... pathetic I know .. however without a measureText or
            // alignment I don't know how to do it properly
            canvas.drawText(mText, halfsize * 0.6f, halfsize + (halfsize >> 1), mPaintText);

        }

        public Bitmap getBitmap() {

            int width = mSize, height = mSize;
            width = width > 0 ? width : 1;
            height = height > 0 ? height : 1;

            Bitmap bitmap = CanvasAdapter.newBitmap(width, height, 0);
            Canvas canvas = CanvasAdapter.newCanvas();
            canvas.setBitmap(bitmap);
            draw(canvas);

            return bitmap;
        }
    }
}
