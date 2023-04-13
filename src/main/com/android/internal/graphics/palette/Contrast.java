package com.android.internal.graphics.palette;
/* loaded from: classes4.dex */
public class Contrast {
    static final /* synthetic */ boolean $assertionsDisabled = false;

    public static float lighterY(float y, float contrast) {
        float answer = ((5.0f + y) * contrast) - 5.0f;
        if (answer > 100.0d) {
            return -1.0f;
        }
        return answer;
    }

    public static float darkerY(float y, float contrast) {
        float answer = ((5.0f - (contrast * 5.0f)) + y) / contrast;
        if (answer < 0.0d) {
            return -1.0f;
        }
        return answer;
    }

    public static float lstarToY(float lstar) {
        if (lstar > 8.0f) {
            return (float) (Math.pow((lstar + 16.0d) / 116.0d, 3.0d) * 100.0d);
        }
        return (float) ((lstar / 903.0f) * 100.0d);
    }

    public static float yToLstar(float y) {
        float y2 = y / 100.0f;
        if (y2 <= 0.008856452f) {
            float y_intermediate = 903.2963f * y2;
            return y_intermediate;
        }
        float y_intermediate2 = (float) Math.cbrt(y2);
        return (116.0f * y_intermediate2) - 16.0f;
    }

    public static float contrastYs(float y1, float y2) {
        float lighter = Math.max(y1, y2);
        float darker = lighter == y1 ? y2 : y1;
        return (lighter + 5.0f) / (5.0f + darker);
    }
}
