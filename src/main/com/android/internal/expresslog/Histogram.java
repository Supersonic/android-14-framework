package com.android.internal.expresslog;

import com.android.internal.util.FrameworkStatsLog;
/* loaded from: classes4.dex */
public final class Histogram {
    private final BinOptions mBinOptions;
    private final long mMetricIdHash;

    /* loaded from: classes4.dex */
    public interface BinOptions {
        int getBinForSample(float f);

        int getBinsCount();
    }

    public Histogram(String metricId, BinOptions binOptions) {
        this.mMetricIdHash = Utils.hashString(metricId);
        this.mBinOptions = binOptions;
    }

    public void logSample(float sample) {
        int binIndex = this.mBinOptions.getBinForSample(sample);
        FrameworkStatsLog.write(593, this.mMetricIdHash, 1, binIndex);
    }

    /* loaded from: classes4.dex */
    public static final class UniformOptions implements BinOptions {
        private final int mBinCount;
        private final float mBinSize;
        private final float mExclusiveMaxValue;
        private final float mMinValue;

        public UniformOptions(int binCount, float minValue, float exclusiveMaxValue) {
            if (binCount < 1) {
                throw new IllegalArgumentException("Bin count should be positive number");
            }
            if (exclusiveMaxValue <= minValue) {
                throw new IllegalArgumentException("Bins range invalid (maxValue < minValue)");
            }
            this.mMinValue = minValue;
            this.mExclusiveMaxValue = exclusiveMaxValue;
            this.mBinSize = (exclusiveMaxValue - minValue) / binCount;
            this.mBinCount = binCount + 2;
        }

        @Override // com.android.internal.expresslog.Histogram.BinOptions
        public int getBinsCount() {
            return this.mBinCount;
        }

        @Override // com.android.internal.expresslog.Histogram.BinOptions
        public int getBinForSample(float sample) {
            float f = this.mMinValue;
            if (sample < f) {
                return 0;
            }
            if (sample >= this.mExclusiveMaxValue) {
                return this.mBinCount - 1;
            }
            return (int) (((sample - f) / this.mBinSize) + 1.0f);
        }
    }
}
