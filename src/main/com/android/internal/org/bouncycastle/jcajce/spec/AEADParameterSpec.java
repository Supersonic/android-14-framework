package com.android.internal.org.bouncycastle.jcajce.spec;

import com.android.internal.org.bouncycastle.util.Arrays;
import javax.crypto.spec.IvParameterSpec;
/* loaded from: classes4.dex */
public class AEADParameterSpec extends IvParameterSpec {
    private final byte[] associatedData;
    private final int macSizeInBits;

    public AEADParameterSpec(byte[] nonce, int macSizeInBits) {
        this(nonce, macSizeInBits, null);
    }

    public AEADParameterSpec(byte[] nonce, int macSizeInBits, byte[] associatedData) {
        super(nonce);
        this.macSizeInBits = macSizeInBits;
        this.associatedData = Arrays.clone(associatedData);
    }

    public int getMacSizeInBits() {
        return this.macSizeInBits;
    }

    public byte[] getAssociatedData() {
        return Arrays.clone(this.associatedData);
    }

    public byte[] getNonce() {
        return getIV();
    }
}
