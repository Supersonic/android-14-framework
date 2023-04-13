package android.media.p007tv.tuner.frontend;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.frontend.IsdbtFrontendCapabilities */
/* loaded from: classes2.dex */
public class IsdbtFrontendCapabilities extends FrontendCapabilities {
    private final int mBandwidthCap;
    private final int mCodeRateCap;
    private final int mGuardIntervalCap;
    private final boolean mIsFullSegmentSupported;
    private final boolean mIsSegmentAutoSupported;
    private final int mModeCap;
    private final int mModulationCap;
    private final int mTimeInterleaveCap;

    private IsdbtFrontendCapabilities(int modeCap, int bandwidthCap, int modulationCap, int codeRateCap, int guardIntervalCap, int timeInterleaveCap, boolean isSegmentAutoSupported, boolean isFullSegmentSupported) {
        this.mModeCap = modeCap;
        this.mBandwidthCap = bandwidthCap;
        this.mModulationCap = modulationCap;
        this.mCodeRateCap = codeRateCap;
        this.mGuardIntervalCap = guardIntervalCap;
        this.mTimeInterleaveCap = timeInterleaveCap;
        this.mIsSegmentAutoSupported = isSegmentAutoSupported;
        this.mIsFullSegmentSupported = isFullSegmentSupported;
    }

    public int getModeCapability() {
        return this.mModeCap;
    }

    public int getBandwidthCapability() {
        return this.mBandwidthCap;
    }

    public int getModulationCapability() {
        return this.mModulationCap;
    }

    public int getCodeRateCapability() {
        return this.mCodeRateCap;
    }

    public int getGuardIntervalCapability() {
        return this.mGuardIntervalCap;
    }

    public int getTimeInterleaveModeCapability() {
        return this.mTimeInterleaveCap;
    }

    public boolean isSegmentAutoSupported() {
        return this.mIsSegmentAutoSupported;
    }

    public boolean isFullSegmentSupported() {
        return this.mIsFullSegmentSupported;
    }
}
