package com.android.internal.org.bouncycastle.jcajce.provider.keystore;

import com.android.internal.org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import com.android.internal.org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
/* loaded from: classes4.dex */
public class PKCS12 {
    private static final String PREFIX = "com.android.internal.org.bouncycastle.jcajce.provider.keystore.pkcs12.";

    /* loaded from: classes4.dex */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // com.android.internal.org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("KeyStore.PKCS12", "com.android.internal.org.bouncycastle.jcajce.provider.keystore.pkcs12.PKCS12KeyStoreSpi$BCPKCS12KeyStore");
        }
    }
}
