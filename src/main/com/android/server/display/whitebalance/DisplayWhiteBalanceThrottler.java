package com.android.server.display.whitebalance;

import android.util.Slog;
import java.io.PrintWriter;
import java.util.Arrays;
/* loaded from: classes.dex */
public class DisplayWhiteBalanceThrottler {
    public float[] mBaseThresholds;
    public int mDecreaseDebounce;
    public float mDecreaseThreshold;
    public float[] mDecreaseThresholds;
    public int mIncreaseDebounce;
    public float mIncreaseThreshold;
    public float[] mIncreaseThresholds;
    public long mLastTime;
    public float mLastValue;
    public boolean mLoggingEnabled;

    public DisplayWhiteBalanceThrottler(int i, int i2, float[] fArr, float[] fArr2, float[] fArr3) {
        validateArguments(i, i2, fArr, fArr2, fArr3);
        this.mLoggingEnabled = false;
        this.mIncreaseDebounce = i;
        this.mDecreaseDebounce = i2;
        this.mBaseThresholds = fArr;
        this.mIncreaseThresholds = fArr2;
        this.mDecreaseThresholds = fArr3;
        clear();
    }

    public boolean throttle(float f) {
        if (this.mLastTime == -1 || !(tooSoon(f) || tooClose(f))) {
            computeThresholds(f);
            this.mLastTime = System.currentTimeMillis();
            this.mLastValue = f;
            return false;
        }
        return true;
    }

    public void clear() {
        this.mLastTime = -1L;
        this.mIncreaseThreshold = -1.0f;
        this.mDecreaseThreshold = -1.0f;
        this.mLastValue = -1.0f;
    }

    public boolean setLoggingEnabled(boolean z) {
        if (this.mLoggingEnabled == z) {
            return false;
        }
        this.mLoggingEnabled = z;
        return true;
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("  DisplayWhiteBalanceThrottler");
        printWriter.println("    mLoggingEnabled=" + this.mLoggingEnabled);
        printWriter.println("    mIncreaseDebounce=" + this.mIncreaseDebounce);
        printWriter.println("    mDecreaseDebounce=" + this.mDecreaseDebounce);
        printWriter.println("    mLastTime=" + this.mLastTime);
        printWriter.println("    mBaseThresholds=" + Arrays.toString(this.mBaseThresholds));
        printWriter.println("    mIncreaseThresholds=" + Arrays.toString(this.mIncreaseThresholds));
        printWriter.println("    mDecreaseThresholds=" + Arrays.toString(this.mDecreaseThresholds));
        printWriter.println("    mIncreaseThreshold=" + this.mIncreaseThreshold);
        printWriter.println("    mDecreaseThreshold=" + this.mDecreaseThreshold);
        printWriter.println("    mLastValue=" + this.mLastValue);
    }

    public final void validateArguments(float f, float f2, float[] fArr, float[] fArr2, float[] fArr3) {
        if (Float.isNaN(f) || f < 0.0f) {
            throw new IllegalArgumentException("increaseDebounce must be a non-negative number.");
        }
        if (Float.isNaN(f2) || f2 < 0.0f) {
            throw new IllegalArgumentException("decreaseDebounce must be a non-negative number.");
        }
        if (!isValidMapping(fArr, fArr2)) {
            throw new IllegalArgumentException("baseThresholds to increaseThresholds is not a valid mapping.");
        }
        if (!isValidMapping(fArr, fArr3)) {
            throw new IllegalArgumentException("baseThresholds to decreaseThresholds is not a valid mapping.");
        }
    }

    public static boolean isValidMapping(float[] fArr, float[] fArr2) {
        if (fArr == null || fArr2 == null || fArr.length == 0 || fArr2.length == 0 || fArr.length != fArr2.length) {
            return false;
        }
        float f = -1.0f;
        int i = 0;
        while (i < fArr.length) {
            if (!Float.isNaN(fArr[i]) && !Float.isNaN(fArr2[i])) {
                float f2 = fArr[i];
                if (f2 >= 0.0f && f < f2) {
                    i++;
                    f = f2;
                }
            }
            return false;
        }
        return true;
    }

    public final boolean tooSoon(float f) {
        long j;
        int i;
        long currentTimeMillis = System.currentTimeMillis();
        if (f > this.mLastValue) {
            j = this.mLastTime;
            i = this.mIncreaseDebounce;
        } else {
            j = this.mLastTime;
            i = this.mDecreaseDebounce;
        }
        long j2 = j + i;
        boolean z = currentTimeMillis < j2;
        if (this.mLoggingEnabled) {
            StringBuilder sb = new StringBuilder();
            sb.append(z ? "too soon: " : "late enough: ");
            sb.append(currentTimeMillis);
            sb.append(z ? " < " : " > ");
            sb.append(j2);
            Slog.d("DisplayWhiteBalanceThrottler", sb.toString());
        }
        return z;
    }

    /* JADX WARN: Code restructure failed: missing block: B:5:0x000c, code lost:
        if (r5 < r0) goto L6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x000f, code lost:
        r1 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0015, code lost:
        if (r5 > r0) goto L6;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final boolean tooClose(float f) {
        float f2;
        boolean z = true;
        if (f > this.mLastValue) {
            f2 = this.mIncreaseThreshold;
        } else {
            f2 = this.mDecreaseThreshold;
        }
        if (this.mLoggingEnabled) {
            StringBuilder sb = new StringBuilder();
            sb.append(z ? "too close: " : "far enough: ");
            sb.append(f);
            sb.append(f > f2 ? " > " : " < ");
            sb.append(f2);
            Slog.d("DisplayWhiteBalanceThrottler", sb.toString());
        }
        return z;
    }

    public final void computeThresholds(float f) {
        int highestIndexBefore = getHighestIndexBefore(f, this.mBaseThresholds);
        this.mIncreaseThreshold = (this.mIncreaseThresholds[highestIndexBefore] + 1.0f) * f;
        this.mDecreaseThreshold = f * (1.0f - this.mDecreaseThresholds[highestIndexBefore]);
    }

    public final int getHighestIndexBefore(float f, float[] fArr) {
        for (int i = 0; i < fArr.length; i++) {
            if (fArr[i] >= f) {
                return i;
            }
        }
        return fArr.length - 1;
    }
}
