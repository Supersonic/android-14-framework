package android.security;

import android.content.Context;
import java.security.KeyStore;
@Deprecated
/* loaded from: classes3.dex */
public final class KeyStoreParameter implements KeyStore.ProtectionParameter {
    private KeyStoreParameter(int flags) {
    }

    public int getFlags() {
        return 0;
    }

    @Deprecated
    public boolean isEncryptionRequired() {
        return false;
    }

    @Deprecated
    /* loaded from: classes3.dex */
    public static final class Builder {
        public Builder(Context context) {
            if (context == null) {
                throw new NullPointerException("context == null");
            }
        }

        public Builder setEncryptionRequired(boolean required) {
            return this;
        }

        public KeyStoreParameter build() {
            return new KeyStoreParameter(0);
        }
    }
}
