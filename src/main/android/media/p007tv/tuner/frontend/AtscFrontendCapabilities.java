package android.media.p007tv.tuner.frontend;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.frontend.AtscFrontendCapabilities */
/* loaded from: classes2.dex */
public class AtscFrontendCapabilities extends FrontendCapabilities {
    private final int mModulationCap;

    private AtscFrontendCapabilities(int modulationCap) {
        this.mModulationCap = modulationCap;
    }

    public int getModulationCapability() {
        return this.mModulationCap;
    }
}
