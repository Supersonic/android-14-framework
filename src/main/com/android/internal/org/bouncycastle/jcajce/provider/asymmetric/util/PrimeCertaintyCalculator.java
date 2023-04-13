package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util;
/* loaded from: classes4.dex */
public class PrimeCertaintyCalculator {
    private PrimeCertaintyCalculator() {
    }

    public static int getDefaultCertainty(int keySizeInBits) {
        if (keySizeInBits <= 1024) {
            return 80;
        }
        return (((keySizeInBits - 1) / 1024) * 16) + 96;
    }
}
