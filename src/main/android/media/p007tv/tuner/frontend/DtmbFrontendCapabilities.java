package android.media.p007tv.tuner.frontend;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.frontend.DtmbFrontendCapabilities */
/* loaded from: classes2.dex */
public final class DtmbFrontendCapabilities extends FrontendCapabilities {
    private final int mBandwidthCap;
    private final int mCodeRateCap;
    private final int mGuardIntervalCap;
    private final int mModulationCap;
    private final int mTimeInterleaveModeCap;
    private final int mTransmissionModeCap;

    private DtmbFrontendCapabilities(int modulationCap, int transmissionModeCap, int guardIntervalCap, int timeInterleaveModeCap, int codeRateCap, int bandwidthCap) {
        this.mModulationCap = modulationCap;
        this.mTransmissionModeCap = transmissionModeCap;
        this.mGuardIntervalCap = guardIntervalCap;
        this.mTimeInterleaveModeCap = timeInterleaveModeCap;
        this.mCodeRateCap = codeRateCap;
        this.mBandwidthCap = bandwidthCap;
    }

    public int getModulationCapability() {
        return this.mModulationCap;
    }

    public int getTransmissionModeCapability() {
        return this.mTransmissionModeCap;
    }

    public int getGuardIntervalCapability() {
        return this.mGuardIntervalCap;
    }

    public int getTimeInterleaveModeCapability() {
        return this.mTimeInterleaveModeCap;
    }

    public int getCodeRateCapability() {
        return this.mCodeRateCap;
    }

    public int getBandwidthCapability() {
        return this.mBandwidthCap;
    }
}
