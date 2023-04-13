package android.security.keystore2;

import android.security.KeyStoreSecurityLevel;
import android.security.keystore.ArrayUtils;
import android.system.keystore2.KeyDescriptor;
import android.system.keystore2.KeyMetadata;
import java.security.PublicKey;
import java.util.Arrays;
/* loaded from: classes3.dex */
public abstract class AndroidKeyStorePublicKey extends AndroidKeyStoreKey implements PublicKey {
    private final byte[] mCertificate;
    private final byte[] mCertificateChain;
    private final byte[] mEncoded;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract AndroidKeyStorePrivateKey getPrivateKey();

    public AndroidKeyStorePublicKey(KeyDescriptor descriptor, KeyMetadata metadata, byte[] x509EncodedForm, String algorithm, KeyStoreSecurityLevel securityLevel) {
        super(descriptor, metadata.key.nspace, metadata.authorizations, algorithm, securityLevel);
        this.mCertificate = metadata.certificate;
        this.mCertificateChain = metadata.certificateChain;
        this.mEncoded = x509EncodedForm;
    }

    @Override // android.security.keystore2.AndroidKeyStoreKey, java.security.Key
    public String getFormat() {
        return "X.509";
    }

    @Override // android.security.keystore2.AndroidKeyStoreKey, java.security.Key
    public byte[] getEncoded() {
        return ArrayUtils.cloneIfNotEmpty(this.mEncoded);
    }

    @Override // android.security.keystore2.AndroidKeyStoreKey
    public int hashCode() {
        int result = (1 * 31) + super.hashCode();
        return (((result * 31) + Arrays.hashCode(this.mCertificate)) * 31) + Arrays.hashCode(this.mCertificateChain);
    }

    @Override // android.security.keystore2.AndroidKeyStoreKey
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (super.equals(obj)) {
            AndroidKeyStorePublicKey other = (AndroidKeyStorePublicKey) obj;
            return Arrays.equals(this.mCertificate, other.mCertificate) && Arrays.equals(this.mCertificateChain, other.mCertificateChain);
        }
        return false;
    }
}
