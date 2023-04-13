package com.android.internal.org.bouncycastle.crypto.params;

import java.security.SecureRandom;
/* loaded from: classes4.dex */
public class DSAParameterGenerationParameters {
    public static final int DIGITAL_SIGNATURE_USAGE = 1;
    public static final int KEY_ESTABLISHMENT_USAGE = 2;
    private final int certainty;

    /* renamed from: l */
    private final int f777l;

    /* renamed from: n */
    private final int f778n;
    private final SecureRandom random;
    private final int usageIndex;

    public DSAParameterGenerationParameters(int L, int N, int certainty, SecureRandom random) {
        this(L, N, certainty, random, -1);
    }

    public DSAParameterGenerationParameters(int L, int N, int certainty, SecureRandom random, int usageIndex) {
        this.f777l = L;
        this.f778n = N;
        this.certainty = certainty;
        this.usageIndex = usageIndex;
        this.random = random;
    }

    public int getL() {
        return this.f777l;
    }

    public int getN() {
        return this.f778n;
    }

    public int getCertainty() {
        return this.certainty;
    }

    public SecureRandom getRandom() {
        return this.random;
    }

    public int getUsageIndex() {
        return this.usageIndex;
    }
}
