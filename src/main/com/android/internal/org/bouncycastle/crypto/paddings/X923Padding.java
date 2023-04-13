package com.android.internal.org.bouncycastle.crypto.paddings;

import com.android.internal.org.bouncycastle.crypto.InvalidCipherTextException;
import java.security.SecureRandom;
/* loaded from: classes4.dex */
public class X923Padding implements BlockCipherPadding {
    SecureRandom random = null;

    @Override // com.android.internal.org.bouncycastle.crypto.paddings.BlockCipherPadding
    public void init(SecureRandom random) throws IllegalArgumentException {
        this.random = random;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.paddings.BlockCipherPadding
    public String getPaddingName() {
        return "X9.23";
    }

    @Override // com.android.internal.org.bouncycastle.crypto.paddings.BlockCipherPadding
    public int addPadding(byte[] in, int inOff) {
        byte code = (byte) (in.length - inOff);
        while (inOff < in.length - 1) {
            SecureRandom secureRandom = this.random;
            if (secureRandom == null) {
                in[inOff] = 0;
            } else {
                in[inOff] = (byte) secureRandom.nextInt();
            }
            inOff++;
        }
        in[inOff] = code;
        return code;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.paddings.BlockCipherPadding
    public int padCount(byte[] in) throws InvalidCipherTextException {
        int count = in[in.length - 1] & 255;
        if (count > in.length) {
            throw new InvalidCipherTextException("pad block corrupted");
        }
        return count;
    }
}
