package com.android.internal.org.bouncycastle.crypto.params;
/* loaded from: classes4.dex */
public class RC2Parameters extends KeyParameter {
    private int bits;

    public RC2Parameters(byte[] key) {
        this(key, key.length > 128 ? 1024 : key.length * 8);
    }

    public RC2Parameters(byte[] key, int bits) {
        super(key);
        this.bits = bits;
    }

    public int getEffectiveKeyBits() {
        return this.bits;
    }
}
