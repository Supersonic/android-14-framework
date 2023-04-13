package android.security.identity;

import android.content.Context;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public abstract class IdentityCredentialStore {
    public static final int CIPHERSUITE_ECDHE_HKDF_ECDSA_WITH_AES_256_GCM_SHA256 = 1;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface Ciphersuite {
    }

    public abstract WritableIdentityCredential createCredential(String str, String str2) throws AlreadyPersonalizedException, DocTypeNotSupportedException;

    @Deprecated
    public abstract byte[] deleteCredentialByName(String str);

    public abstract IdentityCredential getCredentialByName(String str, int i) throws CipherSuiteNotSupportedException;

    public abstract String[] getSupportedDocTypes();

    public static IdentityCredentialStore getInstance(Context context) {
        return CredstoreIdentityCredentialStore.getInstance(context);
    }

    public static IdentityCredentialStore getDirectAccessInstance(Context context) {
        return CredstoreIdentityCredentialStore.getDirectAccessInstance(context);
    }

    public PresentationSession createPresentationSession(int cipherSuite) throws CipherSuiteNotSupportedException {
        throw new UnsupportedOperationException();
    }
}
