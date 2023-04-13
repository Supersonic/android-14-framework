package com.android.internal.org.bouncycastle.crypto.generators;

import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.Digest;
import com.android.internal.org.bouncycastle.crypto.Mac;
import com.android.internal.org.bouncycastle.crypto.PBEParametersGenerator;
import com.android.internal.org.bouncycastle.crypto.digests.AndroidDigestFactory;
import com.android.internal.org.bouncycastle.crypto.macs.HMac;
import com.android.internal.org.bouncycastle.crypto.params.KeyParameter;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithIV;
/* loaded from: classes4.dex */
public class PKCS5S2ParametersGenerator extends PBEParametersGenerator {
    private Mac hMac;
    private byte[] state;

    public PKCS5S2ParametersGenerator() {
        this(AndroidDigestFactory.getSHA1());
    }

    public PKCS5S2ParametersGenerator(Digest digest) {
        HMac hMac = new HMac(digest);
        this.hMac = hMac;
        this.state = new byte[hMac.getMacSize()];
    }

    /* renamed from: F */
    private void m46F(byte[] S, int c, byte[] iBuf, byte[] out, int outOff) {
        if (c == 0) {
            throw new IllegalArgumentException("iteration count must be at least 1.");
        }
        if (S != null) {
            this.hMac.update(S, 0, S.length);
        }
        this.hMac.update(iBuf, 0, iBuf.length);
        this.hMac.doFinal(this.state, 0);
        byte[] bArr = this.state;
        System.arraycopy(bArr, 0, out, outOff, bArr.length);
        for (int count = 1; count < c; count++) {
            Mac mac = this.hMac;
            byte[] bArr2 = this.state;
            mac.update(bArr2, 0, bArr2.length);
            this.hMac.doFinal(this.state, 0);
            int j = 0;
            while (true) {
                byte[] bArr3 = this.state;
                if (j != bArr3.length) {
                    int i = outOff + j;
                    out[i] = (byte) (bArr3[j] ^ out[i]);
                    j++;
                }
            }
        }
    }

    private byte[] generateDerivedKey(int dkLen) {
        int pos;
        int hLen = this.hMac.getMacSize();
        int l = ((dkLen + hLen) - 1) / hLen;
        byte[] iBuf = new byte[4];
        byte[] outBytes = new byte[l * hLen];
        CipherParameters param = new KeyParameter(this.password);
        this.hMac.init(param);
        int outPos = 0;
        for (int i = 1; i <= l; i++) {
            while (true) {
                byte b = (byte) (iBuf[pos] + 1);
                iBuf[pos] = b;
                pos = b == 0 ? pos - 1 : 3;
            }
            m46F(this.salt, this.iterationCount, iBuf, outBytes, outPos);
            outPos += hLen;
        }
        return outBytes;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.PBEParametersGenerator
    public CipherParameters generateDerivedParameters(int keySize) {
        int keySize2 = keySize / 8;
        byte[] dKey = generateDerivedKey(keySize2);
        return new KeyParameter(dKey, 0, keySize2);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.PBEParametersGenerator
    public CipherParameters generateDerivedParameters(int keySize, int ivSize) {
        int keySize2 = keySize / 8;
        int ivSize2 = ivSize / 8;
        byte[] dKey = generateDerivedKey(keySize2 + ivSize2);
        return new ParametersWithIV(new KeyParameter(dKey, 0, keySize2), dKey, keySize2, ivSize2);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.PBEParametersGenerator
    public CipherParameters generateDerivedMacParameters(int keySize) {
        return generateDerivedParameters(keySize);
    }
}
