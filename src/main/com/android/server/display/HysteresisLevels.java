package com.android.server.display;

import java.io.PrintWriter;
import java.util.Arrays;
/* loaded from: classes.dex */
public class HysteresisLevels {
    public final float[] mBrighteningThresholdLevels;
    public final float[] mBrighteningThresholdsPercentages;
    public final float[] mDarkeningThresholdLevels;
    public final float[] mDarkeningThresholdsPercentages;
    public final float mMinBrightening;
    public final float mMinDarkening;

    public HysteresisLevels(float[] fArr, float[] fArr2, float[] fArr3, float[] fArr4, float f, float f2, boolean z) {
        if (fArr.length != fArr3.length || fArr2.length != fArr4.length) {
            throw new IllegalArgumentException("Mismatch between hysteresis array lengths.");
        }
        this.mBrighteningThresholdsPercentages = setArrayFormat(fArr, 100.0f);
        this.mDarkeningThresholdsPercentages = setArrayFormat(fArr2, 100.0f);
        this.mBrighteningThresholdLevels = setArrayFormat(fArr3, 1.0f);
        this.mDarkeningThresholdLevels = setArrayFormat(fArr4, 1.0f);
        this.mMinDarkening = f;
        this.mMinBrightening = f2;
    }

    public HysteresisLevels(float[] fArr, float[] fArr2, float[] fArr3, float[] fArr4, float f, float f2) {
        this(fArr, fArr2, fArr3, fArr4, f, f2, false);
    }

    public float getBrighteningThreshold(float f) {
        return Math.max((getReferenceLevel(f, this.mBrighteningThresholdLevels, this.mBrighteningThresholdsPercentages) + 1.0f) * f, f + this.mMinBrightening);
    }

    public float getDarkeningThreshold(float f) {
        return Math.max(Math.min((1.0f - getReferenceLevel(f, this.mDarkeningThresholdLevels, this.mDarkeningThresholdsPercentages)) * f, f - this.mMinDarkening), 0.0f);
    }

    public final float getReferenceLevel(float f, float[] fArr, float[] fArr2) {
        if (fArr == null || fArr.length == 0) {
            return 0.0f;
        }
        int i = 0;
        if (f < fArr[0]) {
            return 0.0f;
        }
        while (i < fArr.length - 1) {
            int i2 = i + 1;
            if (f < fArr[i2]) {
                break;
            }
            i = i2;
        }
        return fArr2[i];
    }

    public final float[] setArrayFormat(float[] fArr, float f) {
        int length = fArr.length;
        float[] fArr2 = new float[length];
        for (int i = 0; length > i; i++) {
            fArr2[i] = fArr[i] / f;
        }
        return fArr2;
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("HysteresisLevels");
        printWriter.println("  mBrighteningThresholdLevels=" + Arrays.toString(this.mBrighteningThresholdLevels));
        printWriter.println("  mBrighteningThresholdsPercentages=" + Arrays.toString(this.mBrighteningThresholdsPercentages));
        printWriter.println("  mMinBrightening=" + this.mMinBrightening);
        printWriter.println("  mDarkeningThresholdLevels=" + Arrays.toString(this.mDarkeningThresholdLevels));
        printWriter.println("  mDarkeningThresholdsPercentages=" + Arrays.toString(this.mDarkeningThresholdsPercentages));
        printWriter.println("  mMinDarkening=" + this.mMinDarkening);
    }
}
