package com.android.internal.org.bouncycastle.crypto.params;

import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.util.Arrays;
/* loaded from: classes4.dex */
public class AEADParameters implements CipherParameters {
    private byte[] associatedText;
    private KeyParameter key;
    private int macSize;
    private byte[] nonce;

    public AEADParameters(KeyParameter key, int macSize, byte[] nonce) {
        this(key, macSize, nonce, null);
    }

    public AEADParameters(KeyParameter key, int macSize, byte[] nonce, byte[] associatedText) {
        this.key = key;
        this.nonce = Arrays.clone(nonce);
        this.macSize = macSize;
        this.associatedText = Arrays.clone(associatedText);
    }

    public KeyParameter getKey() {
        return this.key;
    }

    public int getMacSize() {
        return this.macSize;
    }

    public byte[] getAssociatedText() {
        return Arrays.clone(this.associatedText);
    }

    public byte[] getNonce() {
        return Arrays.clone(this.nonce);
    }
}
