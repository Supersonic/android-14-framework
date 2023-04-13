package com.android.internal.org.bouncycastle.crypto.generators;

import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.Digest;
import com.android.internal.org.bouncycastle.crypto.ExtendedDigest;
import com.android.internal.org.bouncycastle.crypto.PBEParametersGenerator;
import com.android.internal.org.bouncycastle.crypto.params.KeyParameter;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithIV;
/* loaded from: classes4.dex */
public class PKCS12ParametersGenerator extends PBEParametersGenerator {
    public static final int IV_MATERIAL = 2;
    public static final int KEY_MATERIAL = 1;
    public static final int MAC_MATERIAL = 3;
    private Digest digest;

    /* renamed from: u */
    private int f754u;

    /* renamed from: v */
    private int f755v;

    public PKCS12ParametersGenerator(Digest digest) {
        this.digest = digest;
        if (digest instanceof ExtendedDigest) {
            this.f754u = digest.getDigestSize();
            this.f755v = ((ExtendedDigest) digest).getByteLength();
            return;
        }
        throw new IllegalArgumentException("Digest " + digest.getAlgorithmName() + " unsupported");
    }

    private void adjust(byte[] a, int aOff, byte[] b) {
        int x = (b[b.length - 1] & 255) + (a[(b.length + aOff) - 1] & 255) + 1;
        a[(b.length + aOff) - 1] = (byte) x;
        int x2 = x >>> 8;
        for (int i = b.length - 2; i >= 0; i--) {
            int x3 = x2 + (b[i] & 255) + (a[aOff + i] & 255);
            a[aOff + i] = (byte) x3;
            x2 = x3 >>> 8;
        }
    }

    private byte[] generateDerivedKey(int idByte, int n) {
        byte[] S;
        byte[] P;
        byte[] D = new byte[this.f755v];
        byte[] dKey = new byte[n];
        for (int i = 0; i != D.length; i++) {
            D[i] = (byte) idByte;
        }
        if (this.salt != null && this.salt.length != 0) {
            int i2 = this.f755v;
            int length = this.salt.length;
            int i3 = this.f755v;
            S = new byte[i2 * (((length + i3) - 1) / i3)];
            for (int i4 = 0; i4 != S.length; i4++) {
                S[i4] = this.salt[i4 % this.salt.length];
            }
        } else {
            S = new byte[0];
        }
        if (this.password != null && this.password.length != 0) {
            int i5 = this.f755v;
            int length2 = this.password.length;
            int i6 = this.f755v;
            P = new byte[i5 * (((length2 + i6) - 1) / i6)];
            for (int i7 = 0; i7 != P.length; i7++) {
                P[i7] = this.password[i7 % this.password.length];
            }
        } else {
            P = new byte[0];
        }
        byte[] I = new byte[S.length + P.length];
        System.arraycopy(S, 0, I, 0, S.length);
        System.arraycopy(P, 0, I, S.length, P.length);
        byte[] B = new byte[this.f755v];
        int i8 = this.f754u;
        int c = ((n + i8) - 1) / i8;
        byte[] A = new byte[i8];
        for (int i9 = 1; i9 <= c; i9++) {
            this.digest.update(D, 0, D.length);
            this.digest.update(I, 0, I.length);
            this.digest.doFinal(A, 0);
            for (int j = 1; j < this.iterationCount; j++) {
                this.digest.update(A, 0, A.length);
                this.digest.doFinal(A, 0);
            }
            for (int j2 = 0; j2 != B.length; j2++) {
                B[j2] = A[j2 % A.length];
            }
            int j3 = 0;
            while (true) {
                int length3 = I.length;
                int i10 = this.f755v;
                if (j3 == length3 / i10) {
                    break;
                }
                adjust(I, i10 * j3, B);
                j3++;
            }
            if (i9 == c) {
                int i11 = this.f754u;
                System.arraycopy(A, 0, dKey, (i9 - 1) * i11, dKey.length - ((i9 - 1) * i11));
            } else {
                System.arraycopy(A, 0, dKey, (i9 - 1) * this.f754u, A.length);
            }
        }
        return dKey;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.PBEParametersGenerator
    public CipherParameters generateDerivedParameters(int keySize) {
        int keySize2 = keySize / 8;
        byte[] dKey = generateDerivedKey(1, keySize2);
        return new KeyParameter(dKey, 0, keySize2);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.PBEParametersGenerator
    public CipherParameters generateDerivedParameters(int keySize, int ivSize) {
        int keySize2 = keySize / 8;
        int ivSize2 = ivSize / 8;
        byte[] dKey = generateDerivedKey(1, keySize2);
        byte[] iv = generateDerivedKey(2, ivSize2);
        return new ParametersWithIV(new KeyParameter(dKey, 0, keySize2), iv, 0, ivSize2);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.PBEParametersGenerator
    public CipherParameters generateDerivedMacParameters(int keySize) {
        int keySize2 = keySize / 8;
        byte[] dKey = generateDerivedKey(3, keySize2);
        return new KeyParameter(dKey, 0, keySize2);
    }
}
