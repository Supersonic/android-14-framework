package android.media.p007tv.tuner.frontend;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.frontend.IptvFrontendCapabilities */
/* loaded from: classes2.dex */
public class IptvFrontendCapabilities extends FrontendCapabilities {
    private final int mProtocolCap;

    private IptvFrontendCapabilities(int protocolCap) {
        this.mProtocolCap = protocolCap;
    }

    public int getProtocolCapability() {
        return this.mProtocolCap;
    }
}
