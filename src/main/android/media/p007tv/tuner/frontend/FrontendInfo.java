package android.media.p007tv.tuner.frontend;

import android.annotation.SystemApi;
import android.util.Range;
import java.util.Arrays;
import java.util.Objects;
@SystemApi
/* renamed from: android.media.tv.tuner.frontend.FrontendInfo */
/* loaded from: classes2.dex */
public class FrontendInfo {
    private final long mAcquireRange;
    private final int mExclusiveGroupId;
    private final Range<Long> mFrequencyRange;
    private final FrontendCapabilities mFrontendCap;
    private final int mId;
    private final int[] mStatusCaps;
    private final Range<Integer> mSymbolRateRange;
    private final int mType;

    private FrontendInfo(int id, int type, long minFrequency, long maxFrequency, int minSymbolRate, int maxSymbolRate, long acquireRange, int exclusiveGroupId, int[] statusCaps, FrontendCapabilities frontendCap) {
        long maxFrequency2;
        this.mId = id;
        this.mType = type;
        if (maxFrequency >= 0) {
            maxFrequency2 = maxFrequency;
        } else {
            maxFrequency2 = 2147483647L;
        }
        this.mFrequencyRange = new Range<>(Long.valueOf(minFrequency), Long.valueOf(maxFrequency2));
        this.mSymbolRateRange = new Range<>(Integer.valueOf(minSymbolRate), Integer.valueOf(maxSymbolRate));
        this.mAcquireRange = acquireRange;
        this.mExclusiveGroupId = exclusiveGroupId;
        this.mStatusCaps = statusCaps;
        this.mFrontendCap = frontendCap;
    }

    public int getId() {
        return this.mId;
    }

    public int getType() {
        return this.mType;
    }

    @Deprecated
    public Range<Integer> getFrequencyRange() {
        return new Range<>(Integer.valueOf((int) this.mFrequencyRange.getLower().longValue()), Integer.valueOf((int) this.mFrequencyRange.getUpper().longValue()));
    }

    public Range<Long> getFrequencyRangeLong() {
        return this.mFrequencyRange;
    }

    public Range<Integer> getSymbolRateRange() {
        return this.mSymbolRateRange;
    }

    @Deprecated
    public int getAcquireRange() {
        return (int) getAcquireRangeLong();
    }

    public long getAcquireRangeLong() {
        return this.mAcquireRange;
    }

    public int getExclusiveGroupId() {
        return this.mExclusiveGroupId;
    }

    public int[] getStatusCapabilities() {
        return this.mStatusCaps;
    }

    public FrontendCapabilities getFrontendCapabilities() {
        return this.mFrontendCap;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof FrontendInfo)) {
            return false;
        }
        FrontendInfo info = (FrontendInfo) o;
        if (this.mId == info.getId() && this.mType == info.getType() && Objects.equals(this.mFrequencyRange, info.getFrequencyRangeLong()) && Objects.equals(this.mSymbolRateRange, info.getSymbolRateRange()) && this.mAcquireRange == info.getAcquireRangeLong() && this.mExclusiveGroupId == info.getExclusiveGroupId() && Arrays.equals(this.mStatusCaps, info.getStatusCapabilities())) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.mId;
    }
}
