package com.android.internal.org.bouncycastle.crypto.paddings;

import com.android.internal.org.bouncycastle.crypto.InvalidCipherTextException;
import java.security.SecureRandom;
/* loaded from: classes4.dex */
public class TBCPadding implements BlockCipherPadding {
    @Override // com.android.internal.org.bouncycastle.crypto.paddings.BlockCipherPadding
    public void init(SecureRandom random) throws IllegalArgumentException {
    }

    @Override // com.android.internal.org.bouncycastle.crypto.paddings.BlockCipherPadding
    public String getPaddingName() {
        return "TBC";
    }

    @Override // com.android.internal.org.bouncycastle.crypto.paddings.BlockCipherPadding
    public int addPadding(byte[] in, int inOff) {
        byte code;
        int count = in.length - inOff;
        if (inOff > 0) {
            code = (byte) ((in[inOff + (-1)] & 1) != 0 ? 0 : 255);
        } else {
            code = (byte) ((in[in.length + (-1)] & 1) != 0 ? 0 : 255);
        }
        while (inOff < in.length) {
            in[inOff] = code;
            inOff++;
        }
        return count;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.paddings.BlockCipherPadding
    public int padCount(byte[] in) throws InvalidCipherTextException {
        byte code = in[in.length - 1];
        int index = in.length - 1;
        while (index > 0 && in[index - 1] == code) {
            index--;
        }
        return in.length - index;
    }
}
