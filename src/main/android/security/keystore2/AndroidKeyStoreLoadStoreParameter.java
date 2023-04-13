package android.security.keystore2;

import java.security.KeyStore;
/* loaded from: classes3.dex */
public class AndroidKeyStoreLoadStoreParameter implements KeyStore.LoadStoreParameter {
    private final int mNamespace;

    public AndroidKeyStoreLoadStoreParameter(int namespace) {
        this.mNamespace = namespace;
    }

    @Override // java.security.KeyStore.LoadStoreParameter
    public KeyStore.ProtectionParameter getProtectionParameter() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getNamespace() {
        return this.mNamespace;
    }
}
