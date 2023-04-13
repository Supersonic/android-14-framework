package com.android.internal.org.bouncycastle.crypto.digests;

import com.android.internal.org.bouncycastle.util.Memoable;
/* loaded from: classes4.dex */
public class MD4Digest extends GeneralDigest {
    private static final int DIGEST_LENGTH = 16;
    private static final int S11 = 3;
    private static final int S12 = 7;
    private static final int S13 = 11;
    private static final int S14 = 19;
    private static final int S21 = 3;
    private static final int S22 = 5;
    private static final int S23 = 9;
    private static final int S24 = 13;
    private static final int S31 = 3;
    private static final int S32 = 9;
    private static final int S33 = 11;
    private static final int S34 = 15;

    /* renamed from: H1 */
    private int f676H1;

    /* renamed from: H2 */
    private int f677H2;

    /* renamed from: H3 */
    private int f678H3;

    /* renamed from: H4 */
    private int f679H4;

    /* renamed from: X */
    private int[] f680X;
    private int xOff;

    public MD4Digest() {
        this.f680X = new int[16];
        reset();
    }

    public MD4Digest(MD4Digest t) {
        super(t);
        this.f680X = new int[16];
        copyIn(t);
    }

    private void copyIn(MD4Digest t) {
        super.copyIn((GeneralDigest) t);
        this.f676H1 = t.f676H1;
        this.f677H2 = t.f677H2;
        this.f678H3 = t.f678H3;
        this.f679H4 = t.f679H4;
        int[] iArr = t.f680X;
        System.arraycopy(iArr, 0, this.f680X, 0, iArr.length);
        this.xOff = t.xOff;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "MD4";
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return 16;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.digests.GeneralDigest
    protected void processWord(byte[] in, int inOff) {
        int[] iArr = this.f680X;
        int i = this.xOff;
        int i2 = i + 1;
        this.xOff = i2;
        iArr[i] = (in[inOff] & 255) | ((in[inOff + 1] & 255) << 8) | ((in[inOff + 2] & 255) << 16) | ((in[inOff + 3] & 255) << 24);
        if (i2 == 16) {
            processBlock();
        }
    }

    @Override // com.android.internal.org.bouncycastle.crypto.digests.GeneralDigest
    protected void processLength(long bitLength) {
        if (this.xOff > 14) {
            processBlock();
        }
        int[] iArr = this.f680X;
        iArr[14] = (int) ((-1) & bitLength);
        iArr[15] = (int) (bitLength >>> 32);
    }

    private void unpackWord(int word, byte[] out, int outOff) {
        out[outOff] = (byte) word;
        out[outOff + 1] = (byte) (word >>> 8);
        out[outOff + 2] = (byte) (word >>> 16);
        out[outOff + 3] = (byte) (word >>> 24);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public int doFinal(byte[] out, int outOff) {
        finish();
        unpackWord(this.f676H1, out, outOff);
        unpackWord(this.f677H2, out, outOff + 4);
        unpackWord(this.f678H3, out, outOff + 8);
        unpackWord(this.f679H4, out, outOff + 12);
        reset();
        return 16;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.digests.GeneralDigest, com.android.internal.org.bouncycastle.crypto.Digest
    public void reset() {
        super.reset();
        this.f676H1 = 1732584193;
        this.f677H2 = -271733879;
        this.f678H3 = -1732584194;
        this.f679H4 = 271733878;
        this.xOff = 0;
        int i = 0;
        while (true) {
            int[] iArr = this.f680X;
            if (i != iArr.length) {
                iArr[i] = 0;
                i++;
            } else {
                return;
            }
        }
    }

    private int rotateLeft(int x, int n) {
        return (x << n) | (x >>> (32 - n));
    }

    /* renamed from: F */
    private int m63F(int u, int v, int w) {
        return (u & v) | ((~u) & w);
    }

    /* renamed from: G */
    private int m62G(int u, int v, int w) {
        return (u & v) | (u & w) | (v & w);
    }

    /* renamed from: H */
    private int m61H(int u, int v, int w) {
        return (u ^ v) ^ w;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.digests.GeneralDigest
    protected void processBlock() {
        int a = this.f676H1;
        int b = this.f677H2;
        int c = this.f678H3;
        int d = this.f679H4;
        int a2 = rotateLeft(m63F(b, c, d) + a + this.f680X[0], 3);
        int d2 = rotateLeft(m63F(a2, b, c) + d + this.f680X[1], 7);
        int c2 = rotateLeft(m63F(d2, a2, b) + c + this.f680X[2], 11);
        int b2 = rotateLeft(m63F(c2, d2, a2) + b + this.f680X[3], 19);
        int a3 = rotateLeft(m63F(b2, c2, d2) + a2 + this.f680X[4], 3);
        int d3 = rotateLeft(m63F(a3, b2, c2) + d2 + this.f680X[5], 7);
        int c3 = rotateLeft(m63F(d3, a3, b2) + c2 + this.f680X[6], 11);
        int b3 = rotateLeft(m63F(c3, d3, a3) + b2 + this.f680X[7], 19);
        int a4 = rotateLeft(m63F(b3, c3, d3) + a3 + this.f680X[8], 3);
        int d4 = rotateLeft(m63F(a4, b3, c3) + d3 + this.f680X[9], 7);
        int c4 = rotateLeft(m63F(d4, a4, b3) + c3 + this.f680X[10], 11);
        int b4 = rotateLeft(m63F(c4, d4, a4) + b3 + this.f680X[11], 19);
        int a5 = rotateLeft(m63F(b4, c4, d4) + a4 + this.f680X[12], 3);
        int d5 = rotateLeft(m63F(a5, b4, c4) + d4 + this.f680X[13], 7);
        int c5 = rotateLeft(m63F(d5, a5, b4) + c4 + this.f680X[14], 11);
        int b5 = rotateLeft(m63F(c5, d5, a5) + b4 + this.f680X[15], 19);
        int a6 = rotateLeft(m62G(b5, c5, d5) + a5 + this.f680X[0] + 1518500249, 3);
        int d6 = rotateLeft(m62G(a6, b5, c5) + d5 + this.f680X[4] + 1518500249, 5);
        int c6 = rotateLeft(m62G(d6, a6, b5) + c5 + this.f680X[8] + 1518500249, 9);
        int b6 = rotateLeft(m62G(c6, d6, a6) + b5 + this.f680X[12] + 1518500249, 13);
        int a7 = rotateLeft(m62G(b6, c6, d6) + a6 + this.f680X[1] + 1518500249, 3);
        int d7 = rotateLeft(m62G(a7, b6, c6) + d6 + this.f680X[5] + 1518500249, 5);
        int c7 = rotateLeft(m62G(d7, a7, b6) + c6 + this.f680X[9] + 1518500249, 9);
        int b7 = rotateLeft(m62G(c7, d7, a7) + b6 + this.f680X[13] + 1518500249, 13);
        int a8 = rotateLeft(m62G(b7, c7, d7) + a7 + this.f680X[2] + 1518500249, 3);
        int d8 = rotateLeft(m62G(a8, b7, c7) + d7 + this.f680X[6] + 1518500249, 5);
        int c8 = rotateLeft(m62G(d8, a8, b7) + c7 + this.f680X[10] + 1518500249, 9);
        int b8 = rotateLeft(m62G(c8, d8, a8) + b7 + this.f680X[14] + 1518500249, 13);
        int a9 = rotateLeft(m62G(b8, c8, d8) + a8 + this.f680X[3] + 1518500249, 3);
        int d9 = rotateLeft(m62G(a9, b8, c8) + d8 + this.f680X[7] + 1518500249, 5);
        int c9 = rotateLeft(m62G(d9, a9, b8) + c8 + this.f680X[11] + 1518500249, 9);
        int b9 = rotateLeft(m62G(c9, d9, a9) + b8 + this.f680X[15] + 1518500249, 13);
        int a10 = rotateLeft(m61H(b9, c9, d9) + a9 + this.f680X[0] + 1859775393, 3);
        int d10 = rotateLeft(m61H(a10, b9, c9) + d9 + this.f680X[8] + 1859775393, 9);
        int c10 = rotateLeft(m61H(d10, a10, b9) + c9 + this.f680X[4] + 1859775393, 11);
        int b10 = rotateLeft(m61H(c10, d10, a10) + b9 + this.f680X[12] + 1859775393, 15);
        int a11 = rotateLeft(m61H(b10, c10, d10) + a10 + this.f680X[2] + 1859775393, 3);
        int d11 = rotateLeft(m61H(a11, b10, c10) + d10 + this.f680X[10] + 1859775393, 9);
        int c11 = rotateLeft(m61H(d11, a11, b10) + c10 + this.f680X[6] + 1859775393, 11);
        int b11 = rotateLeft(m61H(c11, d11, a11) + b10 + this.f680X[14] + 1859775393, 15);
        int a12 = rotateLeft(m61H(b11, c11, d11) + a11 + this.f680X[1] + 1859775393, 3);
        int d12 = rotateLeft(m61H(a12, b11, c11) + d11 + this.f680X[9] + 1859775393, 9);
        int c12 = rotateLeft(m61H(d12, a12, b11) + c11 + this.f680X[5] + 1859775393, 11);
        int b12 = rotateLeft(m61H(c12, d12, a12) + b11 + this.f680X[13] + 1859775393, 15);
        int a13 = rotateLeft(m61H(b12, c12, d12) + a12 + this.f680X[3] + 1859775393, 3);
        int d13 = rotateLeft(m61H(a13, b12, c12) + d12 + this.f680X[11] + 1859775393, 9);
        int c13 = rotateLeft(m61H(d13, a13, b12) + c12 + this.f680X[7] + 1859775393, 11);
        int b13 = rotateLeft(m61H(c13, d13, a13) + b12 + this.f680X[15] + 1859775393, 15);
        this.f676H1 += a13;
        this.f677H2 += b13;
        this.f678H3 += c13;
        this.f679H4 += d13;
        this.xOff = 0;
        int i = 0;
        while (true) {
            int[] iArr = this.f680X;
            if (i != iArr.length) {
                iArr[i] = 0;
                i++;
            } else {
                return;
            }
        }
    }

    @Override // com.android.internal.org.bouncycastle.util.Memoable
    public Memoable copy() {
        return new MD4Digest(this);
    }

    @Override // com.android.internal.org.bouncycastle.util.Memoable
    public void reset(Memoable other) {
        MD4Digest d = (MD4Digest) other;
        copyIn(d);
    }
}
