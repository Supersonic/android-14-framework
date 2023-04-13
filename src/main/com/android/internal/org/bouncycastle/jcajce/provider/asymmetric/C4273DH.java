package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric;

import com.android.internal.org.bouncycastle.asn1.p018x9.X9ObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.p022dh.KeyFactorySpi;
import com.android.internal.org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import com.android.internal.org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import java.util.HashMap;
import java.util.Map;
/* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.DH */
/* loaded from: classes4.dex */
public class C4273DH {
    private static final String PREFIX = "com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.dh.";
    private static final Map<String, String> generalDhAttributes;

    static {
        HashMap hashMap = new HashMap();
        generalDhAttributes = hashMap;
        hashMap.put("SupportedKeyClasses", "javax.crypto.interfaces.DHPublicKey|javax.crypto.interfaces.DHPrivateKey");
        hashMap.put("SupportedKeyFormats", "PKCS#8|X.509");
    }

    /* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.DH$Mappings */
    /* loaded from: classes4.dex */
    public static class Mappings extends AsymmetricAlgorithmProvider {
        @Override // com.android.internal.org.bouncycastle.jcajce.provider.util.AlgorithmProvider
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("KeyPairGenerator.DH", "com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyPairGeneratorSpi");
            provider.addAlgorithm("Alg.Alias.KeyPairGenerator.DIFFIEHELLMAN", "DH");
            provider.addAttributes("KeyAgreement.DH", C4273DH.generalDhAttributes);
            provider.addAlgorithm("KeyAgreement.DH", "com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyAgreementSpi");
            provider.addAlgorithm("Alg.Alias.KeyAgreement.DIFFIEHELLMAN", "DH");
            provider.addAlgorithm("KeyFactory.DH", "com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.dh.KeyFactorySpi");
            provider.addAlgorithm("Alg.Alias.KeyFactory.DIFFIEHELLMAN", "DH");
            provider.addAlgorithm("AlgorithmParameters.DH", "com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.dh.AlgorithmParametersSpi");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.DIFFIEHELLMAN", "DH");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.DIFFIEHELLMAN", "DH");
            provider.addAlgorithm("AlgorithmParameterGenerator.DH", "com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.dh.AlgorithmParameterGeneratorSpi");
            registerOid(provider, PKCSObjectIdentifiers.dhKeyAgreement, "DH", new KeyFactorySpi());
            registerOid(provider, X9ObjectIdentifiers.dhpublicnumber, "DH", new KeyFactorySpi());
        }
    }
}
