package com.android.internal.org.bouncycastle.jcajce.spec;

import com.android.internal.org.bouncycastle.util.Arrays;
import java.security.spec.AlgorithmParameterSpec;
/* loaded from: classes4.dex */
public class UserKeyingMaterialSpec implements AlgorithmParameterSpec {
    private final byte[] userKeyingMaterial;

    public UserKeyingMaterialSpec(byte[] userKeyingMaterial) {
        this.userKeyingMaterial = Arrays.clone(userKeyingMaterial);
    }

    public byte[] getUserKeyingMaterial() {
        return Arrays.clone(this.userKeyingMaterial);
    }
}
