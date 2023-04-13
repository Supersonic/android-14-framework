package com.android.internal.org.bouncycastle.jcajce;

import android.security.KeyChain;
import com.android.internal.org.bouncycastle.crypto.PBEParametersGenerator;
/* loaded from: classes4.dex */
public class PKCS12Key implements PBKDFKey {
    private final char[] password;
    private final boolean useWrongZeroLengthConversion;

    public PKCS12Key(char[] password) {
        this(password, false);
    }

    public PKCS12Key(char[] password, boolean useWrongZeroLengthConversion) {
        password = password == null ? new char[0] : password;
        char[] cArr = new char[password.length];
        this.password = cArr;
        this.useWrongZeroLengthConversion = useWrongZeroLengthConversion;
        System.arraycopy(password, 0, cArr, 0, password.length);
    }

    public char[] getPassword() {
        return this.password;
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return KeyChain.EXTRA_PKCS12;
    }

    @Override // java.security.Key
    public String getFormat() {
        return KeyChain.EXTRA_PKCS12;
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        if (this.useWrongZeroLengthConversion && this.password.length == 0) {
            return new byte[2];
        }
        return PBEParametersGenerator.PKCS12PasswordToBytes(this.password);
    }
}
