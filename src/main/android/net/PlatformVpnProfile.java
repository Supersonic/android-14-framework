package android.net;

import com.android.internal.net.VpnProfile;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.GeneralSecurityException;
/* loaded from: classes2.dex */
public abstract class PlatformVpnProfile {
    public static final int MAX_MTU_DEFAULT = 1360;
    public static final int TYPE_IKEV2_IPSEC_PSK = 7;
    public static final int TYPE_IKEV2_IPSEC_RSA = 8;
    public static final int TYPE_IKEV2_IPSEC_USER_PASS = 6;
    protected final boolean mExcludeLocalRoutes;
    protected final boolean mRequiresInternetValidation;
    protected final int mType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface PlatformVpnType {
    }

    public abstract VpnProfile toVpnProfile() throws IOException, GeneralSecurityException;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PlatformVpnProfile(int type, boolean excludeLocalRoutes, boolean requiresValidation) {
        this.mType = type;
        this.mExcludeLocalRoutes = excludeLocalRoutes;
        this.mRequiresInternetValidation = requiresValidation;
    }

    public final int getType() {
        return this.mType;
    }

    public final boolean areLocalRoutesExcluded() {
        return this.mExcludeLocalRoutes;
    }

    public final boolean isInternetValidationRequired() {
        return this.mRequiresInternetValidation;
    }

    public final String getTypeString() {
        switch (this.mType) {
            case 6:
                return "IKEv2/IPsec Username/Password";
            case 7:
                return "IKEv2/IPsec Preshared key";
            case 8:
                return "IKEv2/IPsec RSA Digital Signature";
            default:
                return "Unknown VPN profile type";
        }
    }

    public static PlatformVpnProfile fromVpnProfile(VpnProfile profile) throws IOException, GeneralSecurityException {
        switch (profile.type) {
            case 6:
            case 7:
            case 8:
                return Ikev2VpnProfile.fromVpnProfile(profile);
            default:
                throw new IllegalArgumentException("Unknown VPN Profile type");
        }
    }
}
