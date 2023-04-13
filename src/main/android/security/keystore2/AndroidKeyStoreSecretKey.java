package android.security.keystore2;

import android.security.KeyStoreSecurityLevel;
import android.system.keystore2.KeyDescriptor;
import android.system.keystore2.KeyMetadata;
import javax.crypto.SecretKey;
/* loaded from: classes3.dex */
public class AndroidKeyStoreSecretKey extends AndroidKeyStoreKey implements SecretKey {
    public AndroidKeyStoreSecretKey(KeyDescriptor descriptor, KeyMetadata metadata, String algorithm, KeyStoreSecurityLevel securityLevel) {
        super(descriptor, metadata.key.nspace, metadata.authorizations, algorithm, securityLevel);
    }
}
