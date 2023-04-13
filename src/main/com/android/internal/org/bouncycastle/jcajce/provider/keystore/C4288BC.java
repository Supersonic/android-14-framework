package com.android.internal.org.bouncycastle.jcajce.provider.keystore;

import com.android.internal.org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import com.android.internal.org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
/* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.keystore.BC */
/* loaded from: classes4.dex */
public class C4288BC {
    private static final String PREFIX = "com.android.internal.org.bouncycastle.jcajce.provider.keystore.bc.";

    /* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.keystore.BC$Mappings */
    /* loaded from: classes4.dex */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // com.android.internal.org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("KeyStore.BKS", "com.android.internal.org.bouncycastle.jcajce.provider.keystore.bc.BcKeyStoreSpi$Std");
            provider.addAlgorithm("KeyStore.BouncyCastle", "com.android.internal.org.bouncycastle.jcajce.provider.keystore.bc.BcKeyStoreSpi$BouncyCastleStore");
            provider.addAlgorithm("Alg.Alias.KeyStore.UBER", "BouncyCastle");
            provider.addAlgorithm("Alg.Alias.KeyStore.BOUNCYCASTLE", "BouncyCastle");
            provider.addAlgorithm("Alg.Alias.KeyStore.bouncycastle", "BouncyCastle");
        }
    }
}
