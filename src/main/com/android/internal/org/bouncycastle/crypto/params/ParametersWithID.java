package com.android.internal.org.bouncycastle.crypto.params;

import com.android.internal.org.bouncycastle.crypto.CipherParameters;
/* loaded from: classes4.dex */
public class ParametersWithID implements CipherParameters {

    /* renamed from: id */
    private byte[] f790id;
    private CipherParameters parameters;

    public ParametersWithID(CipherParameters parameters, byte[] id) {
        this.parameters = parameters;
        this.f790id = id;
    }

    public byte[] getID() {
        return this.f790id;
    }

    public CipherParameters getParameters() {
        return this.parameters;
    }
}
