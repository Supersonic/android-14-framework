package android.media.p007tv.tuner.frontend;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.frontend.Atsc3FrontendCapabilities */
/* loaded from: classes2.dex */
public class Atsc3FrontendCapabilities extends FrontendCapabilities {
    private final int mBandwidthCap;
    private final int mCodeRateCap;
    private final int mDemodOutputFormatCap;
    private final int mFecCap;
    private final int mModulationCap;
    private final int mTimeInterleaveModeCap;

    private Atsc3FrontendCapabilities(int bandwidthCap, int modulationCap, int timeInterleaveModeCap, int codeRateCap, int fecCap, int demodOutputFormatCap) {
        this.mBandwidthCap = bandwidthCap;
        this.mModulationCap = modulationCap;
        this.mTimeInterleaveModeCap = timeInterleaveModeCap;
        this.mCodeRateCap = codeRateCap;
        this.mFecCap = fecCap;
        this.mDemodOutputFormatCap = demodOutputFormatCap;
    }

    public int getBandwidthCapability() {
        return this.mBandwidthCap;
    }

    public int getModulationCapability() {
        return this.mModulationCap;
    }

    public int getTimeInterleaveModeCapability() {
        return this.mTimeInterleaveModeCap;
    }

    public int getPlpCodeRateCapability() {
        return this.mCodeRateCap;
    }

    public int getFecCapability() {
        return this.mFecCap;
    }

    public int getDemodOutputFormatCapability() {
        return this.mDemodOutputFormatCap;
    }
}
