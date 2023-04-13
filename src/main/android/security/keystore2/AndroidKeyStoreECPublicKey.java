package android.security.keystore2;

import android.security.KeyStoreSecurityLevel;
import android.security.keystore.KeyProperties;
import android.system.keystore2.Authorization;
import android.system.keystore2.KeyDescriptor;
import android.system.keystore2.KeyMetadata;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
/* loaded from: classes3.dex */
public class AndroidKeyStoreECPublicKey extends AndroidKeyStorePublicKey implements ECPublicKey {
    private final ECParameterSpec mParams;

    /* renamed from: mW */
    private final ECPoint f415mW;

    public AndroidKeyStoreECPublicKey(KeyDescriptor descriptor, KeyMetadata metadata, byte[] x509EncodedForm, KeyStoreSecurityLevel securityLevel, ECParameterSpec params, ECPoint w) {
        super(descriptor, metadata, x509EncodedForm, KeyProperties.KEY_ALGORITHM_EC, securityLevel);
        this.mParams = params;
        this.f415mW = w;
    }

    public AndroidKeyStoreECPublicKey(KeyDescriptor descriptor, KeyMetadata metadata, KeyStoreSecurityLevel securityLevel, ECPublicKey info) {
        this(descriptor, metadata, info.getEncoded(), securityLevel, info.getParams(), info.getW());
        if (!"X.509".equalsIgnoreCase(info.getFormat())) {
            throw new IllegalArgumentException("Unsupported key export format: " + info.getFormat());
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:8:0x0025, code lost:
        r0 = android.security.keystore2.KeymasterUtils.getCurveSpec(android.security.keystore2.KeymasterUtils.getEcCurveFromKeymaster(r4.keyParameter.value.getEcCurve()));
     */
    @Override // android.security.keystore2.AndroidKeyStorePublicKey
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public AndroidKeyStorePrivateKey getPrivateKey() {
        ECParameterSpec params = this.mParams;
        Authorization[] authorizations = getAuthorizations();
        int length = authorizations.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            Authorization a = authorizations[i];
            try {
                if (a.keyParameter.tag == 268435466) {
                    break;
                }
                i++;
            } catch (Exception e) {
                throw new RuntimeException("Unable to parse EC curve " + a.keyParameter.value.getEcCurve());
            }
        }
        return new AndroidKeyStoreECPrivateKey(getUserKeyDescriptor(), getKeyIdDescriptor().nspace, getAuthorizations(), getSecurityLevel(), params);
    }

    @Override // java.security.interfaces.ECKey
    public ECParameterSpec getParams() {
        return this.mParams;
    }

    @Override // java.security.interfaces.ECPublicKey
    public ECPoint getW() {
        return this.f415mW;
    }
}
