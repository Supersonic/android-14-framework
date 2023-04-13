package android.media.p007tv.tuner.frontend;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.frontend.DvbtFrontendCapabilities */
/* loaded from: classes2.dex */
public class DvbtFrontendCapabilities extends FrontendCapabilities {
    private final int mBandwidthCap;
    private final int mCodeRateCap;
    private final int mConstellationCap;
    private final int mGuardIntervalCap;
    private final int mHierarchyCap;
    private final boolean mIsMisoSupported;
    private final boolean mIsT2Supported;
    private final int mTransmissionModeCap;

    private DvbtFrontendCapabilities(int transmissionModeCap, int bandwidthCap, int constellationCap, int codeRateCap, int hierarchyCap, int guardIntervalCap, boolean isT2Supported, boolean isMisoSupported) {
        this.mTransmissionModeCap = transmissionModeCap;
        this.mBandwidthCap = bandwidthCap;
        this.mConstellationCap = constellationCap;
        this.mCodeRateCap = codeRateCap;
        this.mHierarchyCap = hierarchyCap;
        this.mGuardIntervalCap = guardIntervalCap;
        this.mIsT2Supported = isT2Supported;
        this.mIsMisoSupported = isMisoSupported;
    }

    public int getTransmissionModeCapability() {
        return this.mTransmissionModeCap;
    }

    public int getBandwidthCapability() {
        return this.mBandwidthCap;
    }

    public int getConstellationCapability() {
        return this.mConstellationCap;
    }

    public int getCodeRateCapability() {
        return this.mCodeRateCap;
    }

    public int getHierarchyCapability() {
        return this.mHierarchyCap;
    }

    public int getGuardIntervalCapability() {
        return this.mGuardIntervalCap;
    }

    public boolean isT2Supported() {
        return this.mIsT2Supported;
    }

    public boolean isMisoSupported() {
        return this.mIsMisoSupported;
    }
}
