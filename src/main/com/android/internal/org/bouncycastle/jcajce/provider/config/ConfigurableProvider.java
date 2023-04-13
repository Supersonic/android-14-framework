package com.android.internal.org.bouncycastle.jcajce.provider.config;

import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import java.util.Map;
/* loaded from: classes4.dex */
public interface ConfigurableProvider {
    public static final String ACCEPTABLE_EC_CURVES = "acceptableEcCurves";
    public static final String ADDITIONAL_EC_PARAMETERS = "additionalEcParameters";
    public static final String DH_DEFAULT_PARAMS = "DhDefaultParams";
    public static final String EC_IMPLICITLY_CA = "ecImplicitlyCa";
    public static final String THREAD_LOCAL_DH_DEFAULT_PARAMS = "threadLocalDhDefaultParams";
    public static final String THREAD_LOCAL_EC_IMPLICITLY_CA = "threadLocalEcImplicitlyCa";

    void addAlgorithm(String str, ASN1ObjectIdentifier aSN1ObjectIdentifier, String str2);

    void addAlgorithm(String str, String str2);

    void addAttributes(String str, Map<String, String> map);

    void addKeyInfoConverter(ASN1ObjectIdentifier aSN1ObjectIdentifier, AsymmetricKeyInfoConverter asymmetricKeyInfoConverter);

    void addPrivateAlgorithm(String str, ASN1ObjectIdentifier aSN1ObjectIdentifier, String str2);

    void addPrivateAlgorithm(String str, String str2);

    AsymmetricKeyInfoConverter getKeyInfoConverter(ASN1ObjectIdentifier aSN1ObjectIdentifier);

    boolean hasAlgorithm(String str, String str2);

    void setParameter(String str, Object obj);
}
