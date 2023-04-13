package com.android.internal.graphics.palette;

import android.graphics.Color;
import android.util.TimingLogger;
import com.android.internal.graphics.palette.Palette;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public final class ColorCutQuantizer implements Quantizer {
    static final int COMPONENT_BLUE = -1;
    static final int COMPONENT_GREEN = -2;
    static final int COMPONENT_RED = -3;
    private static final String LOG_TAG = "ColorCutQuantizer";
    private static final boolean LOG_TIMINGS = false;
    private static final int QUANTIZE_WORD_MASK = 31;
    private static final int QUANTIZE_WORD_WIDTH = 5;
    private static final Comparator<Vbox> VBOX_COMPARATOR_VOLUME = new Comparator<Vbox>() { // from class: com.android.internal.graphics.palette.ColorCutQuantizer.1
        @Override // java.util.Comparator
        public int compare(Vbox lhs, Vbox rhs) {
            return rhs.getVolume() - lhs.getVolume();
        }
    };
    int[] mColors;
    int[] mHistogram;
    List<Palette.Swatch> mQuantizedColors;
    private final float[] mTempHsl = new float[3];
    TimingLogger mTimingLogger;

    @Override // com.android.internal.graphics.palette.Quantizer
    public void quantize(int[] pixels, int maxColors) {
        this.mTimingLogger = null;
        int[] hist = new int[32768];
        this.mHistogram = hist;
        for (int i = 0; i < pixels.length; i++) {
            int quantizedColor = quantizeFromRgb888(pixels[i]);
            pixels[i] = quantizedColor;
            hist[quantizedColor] = hist[quantizedColor] + 1;
        }
        int distinctColorCount = 0;
        for (int i2 : hist) {
            if (i2 > 0) {
                distinctColorCount++;
            }
        }
        int[] colors = new int[distinctColorCount];
        this.mColors = colors;
        int distinctColorIndex = 0;
        for (int color = 0; color < hist.length; color++) {
            if (hist[color] > 0) {
                colors[distinctColorIndex] = color;
                distinctColorIndex++;
            }
        }
        if (distinctColorCount <= maxColors) {
            this.mQuantizedColors = new ArrayList();
            for (int color2 : colors) {
                this.mQuantizedColors.add(new Palette.Swatch(approximateToRgb888(color2), hist[color2]));
            }
            return;
        }
        this.mQuantizedColors = quantizePixels(maxColors);
    }

    @Override // com.android.internal.graphics.palette.Quantizer
    public List<Palette.Swatch> getQuantizedColors() {
        return this.mQuantizedColors;
    }

    private List<Palette.Swatch> quantizePixels(int maxColors) {
        PriorityQueue<Vbox> pq = new PriorityQueue<>(maxColors, VBOX_COMPARATOR_VOLUME);
        pq.offer(new Vbox(0, this.mColors.length - 1));
        splitBoxes(pq, maxColors);
        return generateAverageColors(pq);
    }

    private void splitBoxes(PriorityQueue<Vbox> queue, int maxSize) {
        Vbox vbox;
        while (queue.size() < maxSize && (vbox = queue.poll()) != null && vbox.canSplit()) {
            queue.offer(vbox.splitBox());
            queue.offer(vbox);
        }
    }

    private List<Palette.Swatch> generateAverageColors(Collection<Vbox> vboxes) {
        ArrayList<Palette.Swatch> colors = new ArrayList<>(vboxes.size());
        for (Vbox vbox : vboxes) {
            Palette.Swatch swatch = vbox.getAverageColor();
            colors.add(swatch);
        }
        return colors;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class Vbox {
        private final int mLowerIndex;
        private int mMaxBlue;
        private int mMaxGreen;
        private int mMaxRed;
        private int mMinBlue;
        private int mMinGreen;
        private int mMinRed;
        private int mPopulation;
        private int mUpperIndex;

        Vbox(int lowerIndex, int upperIndex) {
            this.mLowerIndex = lowerIndex;
            this.mUpperIndex = upperIndex;
            fitBox();
        }

        final int getVolume() {
            return ((this.mMaxRed - this.mMinRed) + 1) * ((this.mMaxGreen - this.mMinGreen) + 1) * ((this.mMaxBlue - this.mMinBlue) + 1);
        }

        final boolean canSplit() {
            return getColorCount() > 1;
        }

        final int getColorCount() {
            return (this.mUpperIndex + 1) - this.mLowerIndex;
        }

        final void fitBox() {
            int[] colors = ColorCutQuantizer.this.mColors;
            int[] hist = ColorCutQuantizer.this.mHistogram;
            int minRed = Integer.MAX_VALUE;
            int minBlue = Integer.MAX_VALUE;
            int minGreen = Integer.MAX_VALUE;
            int maxRed = Integer.MIN_VALUE;
            int maxBlue = Integer.MIN_VALUE;
            int maxGreen = Integer.MIN_VALUE;
            int count = 0;
            for (int i = this.mLowerIndex; i <= this.mUpperIndex; i++) {
                int color = colors[i];
                count += hist[color];
                int r = ColorCutQuantizer.quantizedRed(color);
                int g = ColorCutQuantizer.quantizedGreen(color);
                int b = ColorCutQuantizer.quantizedBlue(color);
                if (r > maxRed) {
                    maxRed = r;
                }
                if (r < minRed) {
                    minRed = r;
                }
                if (g > maxGreen) {
                    maxGreen = g;
                }
                if (g < minGreen) {
                    minGreen = g;
                }
                if (b > maxBlue) {
                    maxBlue = b;
                }
                if (b < minBlue) {
                    minBlue = b;
                }
            }
            this.mMinRed = minRed;
            this.mMaxRed = maxRed;
            this.mMinGreen = minGreen;
            this.mMaxGreen = maxGreen;
            this.mMinBlue = minBlue;
            this.mMaxBlue = maxBlue;
            this.mPopulation = count;
        }

        final Vbox splitBox() {
            if (!canSplit()) {
                throw new IllegalStateException("Can not split a box with only 1 color");
            }
            int splitPoint = findSplitPoint();
            Vbox newBox = new Vbox(splitPoint + 1, this.mUpperIndex);
            this.mUpperIndex = splitPoint;
            fitBox();
            return newBox;
        }

        final int getLongestColorDimension() {
            int redLength = this.mMaxRed - this.mMinRed;
            int greenLength = this.mMaxGreen - this.mMinGreen;
            int blueLength = this.mMaxBlue - this.mMinBlue;
            if (redLength >= greenLength && redLength >= blueLength) {
                return -3;
            }
            if (greenLength >= redLength && greenLength >= blueLength) {
                return -2;
            }
            return -1;
        }

        final int findSplitPoint() {
            int longestDimension = getLongestColorDimension();
            int[] colors = ColorCutQuantizer.this.mColors;
            int[] hist = ColorCutQuantizer.this.mHistogram;
            ColorCutQuantizer.modifySignificantOctet(colors, longestDimension, this.mLowerIndex, this.mUpperIndex);
            Arrays.sort(colors, this.mLowerIndex, this.mUpperIndex + 1);
            ColorCutQuantizer.modifySignificantOctet(colors, longestDimension, this.mLowerIndex, this.mUpperIndex);
            int midPoint = this.mPopulation / 2;
            int i = this.mLowerIndex;
            int count = 0;
            while (true) {
                int i2 = this.mUpperIndex;
                if (i <= i2) {
                    count += hist[colors[i]];
                    if (count < midPoint) {
                        i++;
                    } else {
                        return Math.min(i2 - 1, i);
                    }
                } else {
                    int i3 = this.mLowerIndex;
                    return i3;
                }
            }
        }

        final Palette.Swatch getAverageColor() {
            int[] colors = ColorCutQuantizer.this.mColors;
            int[] hist = ColorCutQuantizer.this.mHistogram;
            int redSum = 0;
            int greenSum = 0;
            int blueSum = 0;
            int totalPopulation = 0;
            for (int i = this.mLowerIndex; i <= this.mUpperIndex; i++) {
                int color = colors[i];
                int colorPopulation = hist[color];
                totalPopulation += colorPopulation;
                redSum += ColorCutQuantizer.quantizedRed(color) * colorPopulation;
                greenSum += ColorCutQuantizer.quantizedGreen(color) * colorPopulation;
                blueSum += ColorCutQuantizer.quantizedBlue(color) * colorPopulation;
            }
            int redMean = Math.round(redSum / totalPopulation);
            int greenMean = Math.round(greenSum / totalPopulation);
            int blueMean = Math.round(blueSum / totalPopulation);
            return new Palette.Swatch(ColorCutQuantizer.approximateToRgb888(redMean, greenMean, blueMean), totalPopulation);
        }
    }

    static void modifySignificantOctet(int[] a, int dimension, int lower, int upper) {
        switch (dimension) {
            case -3:
            default:
                return;
            case -2:
                for (int i = lower; i <= upper; i++) {
                    int color = a[i];
                    a[i] = (quantizedGreen(color) << 10) | (quantizedRed(color) << 5) | quantizedBlue(color);
                }
                return;
            case -1:
                for (int i2 = lower; i2 <= upper; i2++) {
                    int color2 = a[i2];
                    a[i2] = (quantizedBlue(color2) << 10) | (quantizedGreen(color2) << 5) | quantizedRed(color2);
                }
                return;
        }
    }

    private static int quantizeFromRgb888(int color) {
        int r = modifyWordWidth(Color.red(color), 8, 5);
        int g = modifyWordWidth(Color.green(color), 8, 5);
        int b = modifyWordWidth(Color.blue(color), 8, 5);
        return (r << 10) | (g << 5) | b;
    }

    static int approximateToRgb888(int r, int g, int b) {
        return Color.rgb(modifyWordWidth(r, 5, 8), modifyWordWidth(g, 5, 8), modifyWordWidth(b, 5, 8));
    }

    private static int approximateToRgb888(int color) {
        return approximateToRgb888(quantizedRed(color), quantizedGreen(color), quantizedBlue(color));
    }

    static int quantizedRed(int color) {
        return (color >> 10) & 31;
    }

    static int quantizedGreen(int color) {
        return (color >> 5) & 31;
    }

    static int quantizedBlue(int color) {
        return color & 31;
    }

    private static int modifyWordWidth(int value, int currentWidth, int targetWidth) {
        int newValue;
        if (targetWidth > currentWidth) {
            newValue = value << (targetWidth - currentWidth);
        } else {
            int newValue2 = currentWidth - targetWidth;
            newValue = value >> newValue2;
        }
        return newValue & ((1 << targetWidth) - 1);
    }
}
