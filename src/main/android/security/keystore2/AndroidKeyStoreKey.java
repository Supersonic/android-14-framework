package android.security.keystore2;

import android.security.KeyStoreSecurityLevel;
import android.system.keystore2.Authorization;
import android.system.keystore2.KeyDescriptor;
import java.security.Key;
/* loaded from: classes3.dex */
public class AndroidKeyStoreKey implements Key {
    private final String mAlgorithm;
    private final Authorization[] mAuthorizations;
    private final KeyDescriptor mDescriptor;
    private final long mKeyId;
    private final KeyStoreSecurityLevel mSecurityLevel;

    public AndroidKeyStoreKey(KeyDescriptor descriptor, long keyId, Authorization[] authorizations, String algorithm, KeyStoreSecurityLevel securityLevel) {
        this.mDescriptor = descriptor;
        this.mKeyId = keyId;
        this.mAuthorizations = authorizations;
        this.mAlgorithm = algorithm;
        this.mSecurityLevel = securityLevel;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public KeyDescriptor getUserKeyDescriptor() {
        return this.mDescriptor;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public KeyDescriptor getKeyIdDescriptor() {
        KeyDescriptor descriptor = new KeyDescriptor();
        descriptor.nspace = this.mKeyId;
        descriptor.domain = 4;
        descriptor.alias = null;
        descriptor.blob = null;
        return descriptor;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Authorization[] getAuthorizations() {
        return this.mAuthorizations;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public KeyStoreSecurityLevel getSecurityLevel() {
        return this.mSecurityLevel;
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return this.mAlgorithm;
    }

    @Override // java.security.Key
    public String getFormat() {
        return null;
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        return null;
    }

    public int hashCode() {
        int result = (1 * 31) + getClass().hashCode();
        long j = this.mKeyId;
        return (((result * 31) + ((int) (j >>> 32))) * 31) + ((int) (j & (-1)));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AndroidKeyStoreKey other = (AndroidKeyStoreKey) obj;
        if (this.mKeyId == other.mKeyId) {
            return true;
        }
        return false;
    }
}
