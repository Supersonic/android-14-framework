package android.content.res;

import android.util.MathUtils;
import android.util.SparseArray;
/* loaded from: classes.dex */
public class FontScaleConverterFactory {
    static final SparseArray<FontScaleConverter> LOOKUP_TABLES = new SparseArray<>();
    private static final float SCALE_KEY_MULTIPLIER = 100.0f;

    static {
        put(1.15f, new FontScaleConverter(new float[]{8.0f, 10.0f, 12.0f, 14.0f, 18.0f, 20.0f, 24.0f, 30.0f, 100.0f}, new float[]{9.2f, 11.5f, 13.8f, 16.4f, 19.8f, 21.8f, 25.2f, 30.0f, 100.0f}));
        put(1.3f, new FontScaleConverter(new float[]{8.0f, 10.0f, 12.0f, 14.0f, 18.0f, 20.0f, 24.0f, 30.0f, 100.0f}, new float[]{10.4f, 13.0f, 15.6f, 18.8f, 21.6f, 23.6f, 26.4f, 30.0f, 100.0f}));
        put(1.5f, new FontScaleConverter(new float[]{8.0f, 10.0f, 12.0f, 14.0f, 18.0f, 20.0f, 24.0f, 30.0f, 100.0f}, new float[]{12.0f, 15.0f, 18.0f, 22.0f, 24.0f, 26.0f, 28.0f, 30.0f, 100.0f}));
        put(1.8f, new FontScaleConverter(new float[]{8.0f, 10.0f, 12.0f, 14.0f, 18.0f, 20.0f, 24.0f, 30.0f, 100.0f}, new float[]{14.4f, 18.0f, 21.6f, 24.4f, 27.6f, 30.8f, 32.8f, 34.8f, 100.0f}));
        put(2.0f, new FontScaleConverter(new float[]{8.0f, 10.0f, 12.0f, 14.0f, 18.0f, 20.0f, 24.0f, 30.0f, 100.0f}, new float[]{16.0f, 20.0f, 24.0f, 26.0f, 30.0f, 34.0f, 36.0f, 38.0f, 100.0f}));
    }

    private FontScaleConverterFactory() {
    }

    public static FontScaleConverter forScale(float fontScale) {
        if (fontScale <= 1.0f) {
            return null;
        }
        FontScaleConverter lookupTable = get(fontScale);
        if (lookupTable != null) {
            return lookupTable;
        }
        SparseArray<FontScaleConverter> sparseArray = LOOKUP_TABLES;
        int index = sparseArray.indexOfKey(getKey(fontScale));
        if (index >= 0) {
            return sparseArray.valueAt(index);
        }
        int lowerIndex = (-(index + 1)) - 1;
        int higherIndex = lowerIndex + 1;
        if (lowerIndex < 0 || higherIndex >= sparseArray.size()) {
            return new FontScaleConverter(new float[]{1.0f}, new float[]{fontScale});
        }
        float startScale = getScaleFromKey(sparseArray.keyAt(lowerIndex));
        float endScale = getScaleFromKey(sparseArray.keyAt(higherIndex));
        float interpolationPoint = MathUtils.constrainedMap(0.0f, 1.0f, startScale, endScale, fontScale);
        return createInterpolatedTableBetween(sparseArray.valueAt(lowerIndex), sparseArray.valueAt(higherIndex), interpolationPoint);
    }

    private static FontScaleConverter createInterpolatedTableBetween(FontScaleConverter start, FontScaleConverter end, float interpolationPoint) {
        float[] commonSpSizes = {8.0f, 10.0f, 12.0f, 14.0f, 18.0f, 20.0f, 24.0f, 30.0f, 100.0f};
        float[] dpInterpolated = new float[commonSpSizes.length];
        for (int i = 0; i < commonSpSizes.length; i++) {
            float sp = commonSpSizes[i];
            float startDp = start.convertSpToDp(sp);
            float endDp = end.convertSpToDp(sp);
            dpInterpolated[i] = MathUtils.lerp(startDp, endDp, interpolationPoint);
        }
        return new FontScaleConverter(commonSpSizes, dpInterpolated);
    }

    private static int getKey(float fontScale) {
        return (int) (100.0f * fontScale);
    }

    private static float getScaleFromKey(int key) {
        return key / 100.0f;
    }

    private static void put(float scaleKey, FontScaleConverter fontScaleConverter) {
        LOOKUP_TABLES.put(getKey(scaleKey), fontScaleConverter);
    }

    private static FontScaleConverter get(float scaleKey) {
        return LOOKUP_TABLES.get(getKey(scaleKey));
    }
}
