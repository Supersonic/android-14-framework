package com.android.internal.graphics.palette;

import android.graphics.Color;
import com.android.internal.graphics.palette.Palette;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
/* loaded from: classes4.dex */
public final class WuQuantizer implements Quantizer {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final int BITS = 5;
    private static final int MAX_INDEX = 32;
    private static final int SIDE_LENGTH = 33;
    private static final int TOTAL_SIZE = 35937;
    private int[] mColors;
    private Box[] mCubes;
    private Map<Integer, Integer> mInputPixelToCount;
    private double[] mMoments;
    private int[] mMomentsB;
    private int[] mMomentsG;
    private int[] mMomentsR;
    private Palette mPalette;
    private int[] mWeights;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public enum Direction {
        RED,
        GREEN,
        BLUE
    }

    @Override // com.android.internal.graphics.palette.Quantizer
    public List<Palette.Swatch> getQuantizedColors() {
        return this.mPalette.getSwatches();
    }

    @Override // com.android.internal.graphics.palette.Quantizer
    public void quantize(int[] pixels, int colorCount) {
        int[] iArr;
        QuantizerMap quantizerMap = new QuantizerMap();
        quantizerMap.quantize(pixels, colorCount);
        Map<Integer, Integer> colorToCount = quantizerMap.getColorToCount();
        this.mInputPixelToCount = colorToCount;
        Set<Integer> uniqueColors = colorToCount.keySet();
        if (uniqueColors.size() <= colorCount) {
            this.mColors = new int[this.mInputPixelToCount.keySet().size()];
            int index = 0;
            for (Integer num : uniqueColors) {
                int color = num.intValue();
                this.mColors[index] = color;
                index++;
            }
        } else {
            constructHistogram(this.mInputPixelToCount);
            createMoments();
            CreateBoxesResult createBoxesResult = createBoxes(colorCount);
            this.mColors = createResult(createBoxesResult.mResultCount);
        }
        List<Palette.Swatch> swatches = new ArrayList<>();
        for (int color2 : this.mColors) {
            swatches.add(new Palette.Swatch(color2, 0));
        }
        this.mPalette = Palette.from(swatches);
    }

    public int[] getColors() {
        return this.mColors;
    }

    public Map<Integer, Integer> inputPixelToCount() {
        return this.mInputPixelToCount;
    }

    private static int getIndex(int r, int g, int b) {
        return (r << 10) + (r << 6) + (g << 5) + r + g + b;
    }

    private void constructHistogram(Map<Integer, Integer> pixels) {
        WuQuantizer wuQuantizer = this;
        wuQuantizer.mWeights = new int[TOTAL_SIZE];
        wuQuantizer.mMomentsR = new int[TOTAL_SIZE];
        wuQuantizer.mMomentsG = new int[TOTAL_SIZE];
        wuQuantizer.mMomentsB = new int[TOTAL_SIZE];
        wuQuantizer.mMoments = new double[TOTAL_SIZE];
        for (Iterator<Map.Entry<Integer, Integer>> it = pixels.entrySet().iterator(); it.hasNext(); it = it) {
            Map.Entry<Integer, Integer> pair = it.next();
            int pixel = pair.getKey().intValue();
            int count = pair.getValue().intValue();
            int red = Color.red(pixel);
            int green = Color.green(pixel);
            int blue = Color.blue(pixel);
            int iR = (red >> 3) + 1;
            int iG = (green >> 3) + 1;
            int iB = (blue >> 3) + 1;
            int index = getIndex(iR, iG, iB);
            int[] iArr = wuQuantizer.mWeights;
            iArr[index] = iArr[index] + count;
            int[] iArr2 = wuQuantizer.mMomentsR;
            iArr2[index] = iArr2[index] + (red * count);
            int[] iArr3 = wuQuantizer.mMomentsG;
            iArr3[index] = iArr3[index] + (green * count);
            int[] iArr4 = wuQuantizer.mMomentsB;
            iArr4[index] = iArr4[index] + (blue * count);
            double[] dArr = wuQuantizer.mMoments;
            dArr[index] = dArr[index] + (count * ((red * red) + (green * green) + (blue * blue)));
            wuQuantizer = this;
        }
    }

    private void createMoments() {
        int r = 1;
        while (true) {
            int i = 33;
            if (r < 33) {
                int[] area = new int[33];
                int[] areaR = new int[33];
                int[] areaG = new int[33];
                int[] areaB = new int[33];
                double[] area2 = new double[33];
                int g = 1;
                while (g < i) {
                    int line = 0;
                    int lineR = 0;
                    int lineG = 0;
                    int lineB = 0;
                    double line2 = 0.0d;
                    int b = 1;
                    while (b < i) {
                        int index = getIndex(r, g, b);
                        int line3 = line + this.mWeights[index];
                        lineR += this.mMomentsR[index];
                        lineG += this.mMomentsG[index];
                        lineB += this.mMomentsB[index];
                        line2 += this.mMoments[index];
                        area[b] = area[b] + line3;
                        areaR[b] = areaR[b] + lineR;
                        areaG[b] = areaG[b] + lineG;
                        areaB[b] = areaB[b] + lineB;
                        area2[b] = area2[b] + line2;
                        int previousIndex = getIndex(r - 1, g, b);
                        int[] iArr = this.mWeights;
                        iArr[index] = iArr[previousIndex] + area[b];
                        int[] iArr2 = this.mMomentsR;
                        iArr2[index] = iArr2[previousIndex] + areaR[b];
                        int[] iArr3 = this.mMomentsG;
                        iArr3[index] = iArr3[previousIndex] + areaG[b];
                        int[] iArr4 = this.mMomentsB;
                        iArr4[index] = iArr4[previousIndex] + areaB[b];
                        double[] dArr = this.mMoments;
                        dArr[index] = dArr[previousIndex] + area2[b];
                        b++;
                        line = line3;
                        i = 33;
                    }
                    g++;
                    i = 33;
                }
                r++;
            } else {
                return;
            }
        }
    }

    private CreateBoxesResult createBoxes(int maxColorCount) {
        this.mCubes = new Box[maxColorCount];
        for (int i = 0; i < maxColorCount; i++) {
            this.mCubes[i] = new Box();
        }
        double[] volumeVariance = new double[maxColorCount];
        Box firstBox = this.mCubes[0];
        firstBox.f589r1 = 32;
        firstBox.f587g1 = 32;
        firstBox.f585b1 = 32;
        int generatedColorCount = 0;
        int next = 0;
        int i2 = 1;
        while (i2 < maxColorCount) {
            Box[] boxArr = this.mCubes;
            if (cut(boxArr[next], boxArr[i2])) {
                volumeVariance[next] = this.mCubes[next].vol > 1 ? variance(this.mCubes[next]) : 0.0d;
                volumeVariance[i2] = this.mCubes[i2].vol > 1 ? variance(this.mCubes[i2]) : 0.0d;
            } else {
                volumeVariance[next] = 0.0d;
                i2--;
            }
            next = 0;
            double temp = volumeVariance[0];
            for (int k = 1; k <= i2; k++) {
                if (volumeVariance[k] > temp) {
                    temp = volumeVariance[k];
                    next = k;
                }
            }
            generatedColorCount = i2 + 1;
            if (temp <= 0.0d) {
                break;
            }
            i2++;
        }
        return new CreateBoxesResult(maxColorCount, generatedColorCount);
    }

    private int[] createResult(int colorCount) {
        int[] colors = new int[colorCount];
        int nextAvailableIndex = 0;
        for (int i = 0; i < colorCount; i++) {
            Box cube = this.mCubes[i];
            int weight = volume(cube, this.mWeights);
            if (weight > 0) {
                int r = volume(cube, this.mMomentsR) / weight;
                int g = volume(cube, this.mMomentsG) / weight;
                int b = volume(cube, this.mMomentsB) / weight;
                int color = Color.rgb(r, g, b);
                colors[nextAvailableIndex] = color;
                nextAvailableIndex++;
            }
        }
        int[] resultArray = new int[nextAvailableIndex];
        System.arraycopy(colors, 0, resultArray, 0, nextAvailableIndex);
        return resultArray;
    }

    private double variance(Box cube) {
        int dr = volume(cube, this.mMomentsR);
        int dg = volume(cube, this.mMomentsG);
        int db = volume(cube, this.mMomentsB);
        double xx = ((((((this.mMoments[getIndex(cube.f589r1, cube.f587g1, cube.f585b1)] - this.mMoments[getIndex(cube.f589r1, cube.f587g1, cube.f584b0)]) - this.mMoments[getIndex(cube.f589r1, cube.f586g0, cube.f585b1)]) + this.mMoments[getIndex(cube.f589r1, cube.f586g0, cube.f584b0)]) - this.mMoments[getIndex(cube.f588r0, cube.f587g1, cube.f585b1)]) + this.mMoments[getIndex(cube.f588r0, cube.f587g1, cube.f584b0)]) + this.mMoments[getIndex(cube.f588r0, cube.f586g0, cube.f585b1)]) - this.mMoments[getIndex(cube.f588r0, cube.f586g0, cube.f584b0)];
        int hypotenuse = (dr * dr) + (dg * dg) + (db * db);
        int volume2 = volume(cube, this.mWeights);
        double variance2 = xx - (hypotenuse / volume2);
        return variance2;
    }

    private boolean cut(Box one, Box two) {
        Direction cutDirection;
        int wholeR = volume(one, this.mMomentsR);
        int wholeG = volume(one, this.mMomentsG);
        int wholeB = volume(one, this.mMomentsB);
        int wholeW = volume(one, this.mWeights);
        MaximizeResult maxRResult = maximize(one, Direction.RED, one.f588r0 + 1, one.f589r1, wholeR, wholeG, wholeB, wholeW);
        MaximizeResult maxGResult = maximize(one, Direction.GREEN, one.f586g0 + 1, one.f587g1, wholeR, wholeG, wholeB, wholeW);
        MaximizeResult maxBResult = maximize(one, Direction.BLUE, one.f584b0 + 1, one.f585b1, wholeR, wholeG, wholeB, wholeW);
        double maxR = maxRResult.mMaximum;
        double maxG = maxGResult.mMaximum;
        double maxB = maxBResult.mMaximum;
        if (maxR >= maxG && maxR >= maxB) {
            if (maxRResult.mCutLocation < 0) {
                return false;
            }
            cutDirection = Direction.RED;
        } else if (maxG >= maxR && maxG >= maxB) {
            cutDirection = Direction.GREEN;
        } else {
            cutDirection = Direction.BLUE;
        }
        two.f589r1 = one.f589r1;
        two.f587g1 = one.f587g1;
        two.f585b1 = one.f585b1;
        switch (C41621.f583xfe7d3ccb[cutDirection.ordinal()]) {
            case 1:
                one.f589r1 = maxRResult.mCutLocation;
                two.f588r0 = one.f589r1;
                two.f586g0 = one.f586g0;
                two.f584b0 = one.f584b0;
                break;
            case 2:
                one.f587g1 = maxGResult.mCutLocation;
                two.f588r0 = one.f588r0;
                two.f586g0 = one.f587g1;
                two.f584b0 = one.f584b0;
                break;
            case 3:
                one.f585b1 = maxBResult.mCutLocation;
                two.f588r0 = one.f588r0;
                two.f586g0 = one.f586g0;
                two.f584b0 = one.f585b1;
                break;
            default:
                throw new IllegalArgumentException("unexpected direction " + cutDirection);
        }
        one.vol = (one.f589r1 - one.f588r0) * (one.f587g1 - one.f586g0) * (one.f585b1 - one.f584b0);
        two.vol = (two.f589r1 - two.f588r0) * (two.f587g1 - two.f586g0) * (two.f585b1 - two.f584b0);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.graphics.palette.WuQuantizer$1 */
    /* loaded from: classes4.dex */
    public static /* synthetic */ class C41621 {

        /* renamed from: $SwitchMap$com$android$internal$graphics$palette$WuQuantizer$Direction */
        static final /* synthetic */ int[] f583xfe7d3ccb;

        static {
            int[] iArr = new int[Direction.values().length];
            f583xfe7d3ccb = iArr;
            try {
                iArr[Direction.RED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f583xfe7d3ccb[Direction.GREEN.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f583xfe7d3ccb[Direction.BLUE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private MaximizeResult maximize(Box cube, Direction direction, int first, int last, int wholeR, int wholeG, int wholeB, int wholeW) {
        int baseR;
        WuQuantizer wuQuantizer = this;
        Box box = cube;
        Direction direction2 = direction;
        int baseR2 = bottom(box, direction2, wuQuantizer.mMomentsR);
        int baseG = bottom(box, direction2, wuQuantizer.mMomentsG);
        int baseB = bottom(box, direction2, wuQuantizer.mMomentsB);
        int baseW = bottom(box, direction2, wuQuantizer.mWeights);
        double max = 0.0d;
        int cut = -1;
        int i = first;
        while (i < last) {
            int halfR = top(box, direction2, i, wuQuantizer.mMomentsR) + baseR2;
            int halfG = top(box, direction2, i, wuQuantizer.mMomentsG) + baseG;
            int halfB = top(box, direction2, i, wuQuantizer.mMomentsB) + baseB;
            int halfW = top(box, direction2, i, wuQuantizer.mWeights) + baseW;
            if (halfW == 0) {
                baseR = baseR2;
            } else {
                double tempNumerator = (halfR * halfR) + (halfG * halfG) + (halfB * halfB);
                baseR = baseR2;
                double tempDenominator = halfW;
                double temp = tempNumerator / tempDenominator;
                int halfR2 = wholeR - halfR;
                int halfG2 = wholeG - halfG;
                int halfB2 = wholeB - halfB;
                int halfW2 = wholeW - halfW;
                if (halfW2 != 0) {
                    double tempNumerator2 = (halfR2 * halfR2) + (halfG2 * halfG2) + (halfB2 * halfB2);
                    double tempDenominator2 = halfW2;
                    double temp2 = temp + (tempNumerator2 / tempDenominator2);
                    if (temp2 > max) {
                        max = temp2;
                        cut = i;
                    }
                }
            }
            i++;
            wuQuantizer = this;
            box = cube;
            direction2 = direction;
            baseR2 = baseR;
        }
        return new MaximizeResult(cut, max);
    }

    private static int volume(Box cube, int[] moment) {
        return ((((((moment[getIndex(cube.f589r1, cube.f587g1, cube.f585b1)] - moment[getIndex(cube.f589r1, cube.f587g1, cube.f584b0)]) - moment[getIndex(cube.f589r1, cube.f586g0, cube.f585b1)]) + moment[getIndex(cube.f589r1, cube.f586g0, cube.f584b0)]) - moment[getIndex(cube.f588r0, cube.f587g1, cube.f585b1)]) + moment[getIndex(cube.f588r0, cube.f587g1, cube.f584b0)]) + moment[getIndex(cube.f588r0, cube.f586g0, cube.f585b1)]) - moment[getIndex(cube.f588r0, cube.f586g0, cube.f584b0)];
    }

    private static int bottom(Box cube, Direction direction, int[] moment) {
        switch (C41621.f583xfe7d3ccb[direction.ordinal()]) {
            case 1:
                return (((-moment[getIndex(cube.f588r0, cube.f587g1, cube.f585b1)]) + moment[getIndex(cube.f588r0, cube.f587g1, cube.f584b0)]) + moment[getIndex(cube.f588r0, cube.f586g0, cube.f585b1)]) - moment[getIndex(cube.f588r0, cube.f586g0, cube.f584b0)];
            case 2:
                return (((-moment[getIndex(cube.f589r1, cube.f586g0, cube.f585b1)]) + moment[getIndex(cube.f589r1, cube.f586g0, cube.f584b0)]) + moment[getIndex(cube.f588r0, cube.f586g0, cube.f585b1)]) - moment[getIndex(cube.f588r0, cube.f586g0, cube.f584b0)];
            case 3:
                return (((-moment[getIndex(cube.f589r1, cube.f587g1, cube.f584b0)]) + moment[getIndex(cube.f589r1, cube.f586g0, cube.f584b0)]) + moment[getIndex(cube.f588r0, cube.f587g1, cube.f584b0)]) - moment[getIndex(cube.f588r0, cube.f586g0, cube.f584b0)];
            default:
                throw new IllegalArgumentException("unexpected direction " + direction);
        }
    }

    private static int top(Box cube, Direction direction, int position, int[] moment) {
        switch (C41621.f583xfe7d3ccb[direction.ordinal()]) {
            case 1:
                return ((moment[getIndex(position, cube.f587g1, cube.f585b1)] - moment[getIndex(position, cube.f587g1, cube.f584b0)]) - moment[getIndex(position, cube.f586g0, cube.f585b1)]) + moment[getIndex(position, cube.f586g0, cube.f584b0)];
            case 2:
                return ((moment[getIndex(cube.f589r1, position, cube.f585b1)] - moment[getIndex(cube.f589r1, position, cube.f584b0)]) - moment[getIndex(cube.f588r0, position, cube.f585b1)]) + moment[getIndex(cube.f588r0, position, cube.f584b0)];
            case 3:
                return ((moment[getIndex(cube.f589r1, cube.f587g1, position)] - moment[getIndex(cube.f589r1, cube.f586g0, position)]) - moment[getIndex(cube.f588r0, cube.f587g1, position)]) + moment[getIndex(cube.f588r0, cube.f586g0, position)];
            default:
                throw new IllegalArgumentException("unexpected direction " + direction);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class MaximizeResult {
        final int mCutLocation;
        final double mMaximum;

        MaximizeResult(int cut, double max) {
            this.mCutLocation = cut;
            this.mMaximum = max;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class CreateBoxesResult {
        final int mRequestedCount;
        final int mResultCount;

        CreateBoxesResult(int requestedCount, int resultCount) {
            this.mRequestedCount = requestedCount;
            this.mResultCount = resultCount;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class Box {

        /* renamed from: b0 */
        public int f584b0;

        /* renamed from: b1 */
        public int f585b1;

        /* renamed from: g0 */
        public int f586g0;

        /* renamed from: g1 */
        public int f587g1;

        /* renamed from: r0 */
        public int f588r0;

        /* renamed from: r1 */
        public int f589r1;
        public int vol;

        private Box() {
            this.f588r0 = 0;
            this.f589r1 = 0;
            this.f586g0 = 0;
            this.f587g1 = 0;
            this.f584b0 = 0;
            this.f585b1 = 0;
            this.vol = 0;
        }
    }
}
