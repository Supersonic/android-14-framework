package com.android.internal.org.bouncycastle.crypto.params;

import com.android.internal.org.bouncycastle.crypto.CipherParameters;
/* loaded from: classes4.dex */
public class ParametersWithIV implements CipherParameters {

    /* renamed from: iv */
    private byte[] f791iv;
    private CipherParameters parameters;

    public ParametersWithIV(CipherParameters parameters, byte[] iv) {
        this(parameters, iv, 0, iv.length);
    }

    public ParametersWithIV(CipherParameters parameters, byte[] iv, int ivOff, int ivLen) {
        byte[] bArr = new byte[ivLen];
        this.f791iv = bArr;
        this.parameters = parameters;
        System.arraycopy(iv, ivOff, bArr, 0, ivLen);
    }

    public byte[] getIV() {
        return this.f791iv;
    }

    public CipherParameters getParameters() {
        return this.parameters;
    }
}
