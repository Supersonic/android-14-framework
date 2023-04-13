package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric;

import com.android.internal.org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import com.android.internal.org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import java.util.HashMap;
import java.util.Map;
/* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.EC */
/* loaded from: classes4.dex */
public class C4274EC {
    private static final String PREFIX = "com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.";
    private static final Map<String, String> generalEcAttributes;

    static {
        HashMap hashMap = new HashMap();
        generalEcAttributes = hashMap;
        hashMap.put("SupportedKeyClasses", "java.security.interfaces.ECPublicKey|java.security.interfaces.ECPrivateKey");
        hashMap.put("SupportedKeyFormats", "PKCS#8|X.509");
    }

    /* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.EC$Mappings */
    /* loaded from: classes4.dex */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // com.android.internal.org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
        }
    }
}
