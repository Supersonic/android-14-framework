package com.android.server.display.utils;

import android.util.Slog;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public abstract class AmbientFilter {
    public final RollingBuffer mBuffer;
    public final int mHorizon;
    public boolean mLoggingEnabled;
    public final String mTag;

    public abstract float filter(long j, RollingBuffer rollingBuffer);

    public AmbientFilter(String str, int i) {
        validateArguments(i);
        this.mTag = str;
        this.mLoggingEnabled = false;
        this.mHorizon = i;
        this.mBuffer = new RollingBuffer();
    }

    public boolean addValue(long j, float f) {
        if (f < 0.0f) {
            return false;
        }
        truncateOldValues(j);
        if (this.mLoggingEnabled) {
            String str = this.mTag;
            Slog.d(str, "add value: " + f + " @ " + j);
        }
        this.mBuffer.add(j, f);
        return true;
    }

    public float getEstimate(long j) {
        truncateOldValues(j);
        float filter = filter(j, this.mBuffer);
        if (this.mLoggingEnabled) {
            String str = this.mTag;
            Slog.d(str, "get estimate: " + filter + " @ " + j);
        }
        return filter;
    }

    public void clear() {
        this.mBuffer.clear();
    }

    public boolean setLoggingEnabled(boolean z) {
        if (this.mLoggingEnabled == z) {
            return false;
        }
        this.mLoggingEnabled = z;
        return true;
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("  " + this.mTag);
        printWriter.println("    mLoggingEnabled=" + this.mLoggingEnabled);
        printWriter.println("    mHorizon=" + this.mHorizon);
        printWriter.println("    mBuffer=" + this.mBuffer);
    }

    public final void validateArguments(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("horizon must be positive");
        }
    }

    public final void truncateOldValues(long j) {
        this.mBuffer.truncate(j - this.mHorizon);
    }

    /* loaded from: classes.dex */
    public static class WeightedMovingAverageAmbientFilter extends AmbientFilter {
        public final float mIntercept;

        public WeightedMovingAverageAmbientFilter(String str, int i, float f) {
            super(str, i);
            validateArguments(f);
            this.mIntercept = f;
        }

        @Override // com.android.server.display.utils.AmbientFilter
        public void dump(PrintWriter printWriter) {
            super.dump(printWriter);
            printWriter.println("    mIntercept=" + this.mIntercept);
        }

        @Override // com.android.server.display.utils.AmbientFilter
        public float filter(long j, RollingBuffer rollingBuffer) {
            if (rollingBuffer.isEmpty()) {
                return -1.0f;
            }
            float[] weights = getWeights(j, rollingBuffer);
            float f = 0.0f;
            float f2 = 0.0f;
            for (int i = 0; i < weights.length; i++) {
                float value = rollingBuffer.getValue(i);
                float f3 = weights[i];
                f2 += value * f3;
                f += f3;
            }
            return f == 0.0f ? rollingBuffer.getValue(rollingBuffer.size() - 1) : f2 / f;
        }

        public final void validateArguments(float f) {
            if (Float.isNaN(f) || f < 0.0f) {
                throw new IllegalArgumentException("intercept must be a non-negative number");
            }
        }

        public final float[] getWeights(long j, RollingBuffer rollingBuffer) {
            int size = rollingBuffer.size();
            float[] fArr = new float[size];
            long time = rollingBuffer.getTime(0);
            float f = 0.0f;
            int i = 1;
            while (i < size) {
                float time2 = ((float) (rollingBuffer.getTime(i) - time)) / 1000.0f;
                fArr[i - 1] = calculateIntegral(f, time2);
                i++;
                f = time2;
            }
            fArr[size - 1] = calculateIntegral(f, ((float) ((j + 100) - time)) / 1000.0f);
            return fArr;
        }

        public final float calculateIntegral(float f, float f2) {
            return antiderivative(f2) - antiderivative(f);
        }

        public final float antiderivative(float f) {
            return (0.5f * f * f) + (this.mIntercept * f);
        }
    }
}
