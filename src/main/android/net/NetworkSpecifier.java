package android.net;

import android.annotation.SystemApi;
/* loaded from: classes2.dex */
public abstract class NetworkSpecifier {
    @SystemApi
    public boolean canBeSatisfiedBy(NetworkSpecifier other) {
        return false;
    }

    @SystemApi
    public NetworkSpecifier redact() {
        return this;
    }
}
