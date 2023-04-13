package android.security;
/* loaded from: classes3.dex */
public class FrameworkNetworkSecurityPolicy extends libcore.net.NetworkSecurityPolicy {
    private final boolean mCleartextTrafficPermitted;

    public FrameworkNetworkSecurityPolicy(boolean cleartextTrafficPermitted) {
        this.mCleartextTrafficPermitted = cleartextTrafficPermitted;
    }

    public boolean isCleartextTrafficPermitted() {
        return this.mCleartextTrafficPermitted;
    }

    public boolean isCleartextTrafficPermitted(String hostname) {
        return isCleartextTrafficPermitted();
    }

    public boolean isCertificateTransparencyVerificationRequired(String hostname) {
        return false;
    }
}
