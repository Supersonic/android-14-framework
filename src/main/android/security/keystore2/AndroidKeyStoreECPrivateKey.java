package android.security.keystore2;

import android.security.KeyStoreSecurityLevel;
import android.security.keystore.KeyProperties;
import android.system.keystore2.Authorization;
import android.system.keystore2.KeyDescriptor;
import java.security.interfaces.ECKey;
import java.security.spec.ECParameterSpec;
/* loaded from: classes3.dex */
public class AndroidKeyStoreECPrivateKey extends AndroidKeyStorePrivateKey implements ECKey {
    private final ECParameterSpec mParams;

    public AndroidKeyStoreECPrivateKey(KeyDescriptor descriptor, long keyId, Authorization[] authorizations, KeyStoreSecurityLevel securityLevel, ECParameterSpec params) {
        super(descriptor, keyId, authorizations, KeyProperties.KEY_ALGORITHM_EC, securityLevel);
        this.mParams = params;
    }

    @Override // java.security.interfaces.ECKey
    public ECParameterSpec getParams() {
        return this.mParams;
    }
}
