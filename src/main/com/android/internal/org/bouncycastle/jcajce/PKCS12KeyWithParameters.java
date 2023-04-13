package com.android.internal.org.bouncycastle.jcajce;

import com.android.internal.org.bouncycastle.util.Arrays;
import javax.crypto.interfaces.PBEKey;
/* loaded from: classes4.dex */
public class PKCS12KeyWithParameters extends PKCS12Key implements PBEKey {
    private final int iterationCount;
    private final byte[] salt;

    public PKCS12KeyWithParameters(char[] password, byte[] salt, int iterationCount) {
        super(password);
        this.salt = Arrays.clone(salt);
        this.iterationCount = iterationCount;
    }

    public PKCS12KeyWithParameters(char[] password, boolean useWrongZeroLengthConversion, byte[] salt, int iterationCount) {
        super(password, useWrongZeroLengthConversion);
        this.salt = Arrays.clone(salt);
        this.iterationCount = iterationCount;
    }

    @Override // javax.crypto.interfaces.PBEKey
    public byte[] getSalt() {
        return this.salt;
    }

    @Override // javax.crypto.interfaces.PBEKey
    public int getIterationCount() {
        return this.iterationCount;
    }
}
