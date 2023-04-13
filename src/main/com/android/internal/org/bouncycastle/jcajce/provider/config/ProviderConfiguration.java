package com.android.internal.org.bouncycastle.jcajce.provider.config;

import com.android.internal.org.bouncycastle.jce.spec.ECParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.util.Map;
import java.util.Set;
import javax.crypto.spec.DHParameterSpec;
/* loaded from: classes4.dex */
public interface ProviderConfiguration {
    Set getAcceptableNamedCurves();

    Map getAdditionalECParameters();

    DHParameterSpec getDHDefaultParameters(int i);

    DSAParameterSpec getDSADefaultParameters(int i);

    ECParameterSpec getEcImplicitlyCa();
}
