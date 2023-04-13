package android.media.p007tv.tuner.frontend;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.frontend.Atsc3PlpInfo */
/* loaded from: classes2.dex */
public class Atsc3PlpInfo {
    private final boolean mLlsFlag;
    private final int mPlpId;

    private Atsc3PlpInfo(int plpId, boolean llsFlag) {
        this.mPlpId = plpId;
        this.mLlsFlag = llsFlag;
    }

    public int getPlpId() {
        return this.mPlpId;
    }

    public boolean getLlsFlag() {
        return this.mLlsFlag;
    }
}
