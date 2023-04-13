package com.android.internal.org.bouncycastle.crypto.paddings;

import com.android.internal.org.bouncycastle.crypto.InvalidCipherTextException;
import java.security.SecureRandom;
/* loaded from: classes4.dex */
public class ISO7816d4Padding implements BlockCipherPadding {
    @Override // com.android.internal.org.bouncycastle.crypto.paddings.BlockCipherPadding
    public void init(SecureRandom random) throws IllegalArgumentException {
    }

    @Override // com.android.internal.org.bouncycastle.crypto.paddings.BlockCipherPadding
    public String getPaddingName() {
        return "ISO7816-4";
    }

    @Override // com.android.internal.org.bouncycastle.crypto.paddings.BlockCipherPadding
    public int addPadding(byte[] in, int inOff) {
        int added = in.length - inOff;
        in[inOff] = Byte.MIN_VALUE;
        while (true) {
            inOff++;
            if (inOff < in.length) {
                in[inOff] = 0;
            } else {
                return added;
            }
        }
    }

    @Override // com.android.internal.org.bouncycastle.crypto.paddings.BlockCipherPadding
    public int padCount(byte[] in) throws InvalidCipherTextException {
        int count = in.length - 1;
        while (count > 0 && in[count] == 0) {
            count--;
        }
        if (in[count] != Byte.MIN_VALUE) {
            throw new InvalidCipherTextException("pad block corrupted");
        }
        return in.length - count;
    }
}
