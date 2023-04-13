package com.android.internal.org.bouncycastle.crypto.digests;

import com.android.internal.org.bouncycastle.util.Memoable;
import com.android.internal.org.bouncycastle.util.Pack;
/* loaded from: classes4.dex */
public class SHA1Digest extends GeneralDigest implements EncodableDigest {
    private static final int DIGEST_LENGTH = 20;

    /* renamed from: Y1 */
    private static final int f686Y1 = 1518500249;

    /* renamed from: Y2 */
    private static final int f687Y2 = 1859775393;

    /* renamed from: Y3 */
    private static final int f688Y3 = -1894007588;

    /* renamed from: Y4 */
    private static final int f689Y4 = -899497514;

    /* renamed from: H1 */
    private int f690H1;

    /* renamed from: H2 */
    private int f691H2;

    /* renamed from: H3 */
    private int f692H3;

    /* renamed from: H4 */
    private int f693H4;

    /* renamed from: H5 */
    private int f694H5;

    /* renamed from: X */
    private int[] f695X;
    private int xOff;

    public SHA1Digest() {
        this.f695X = new int[80];
        reset();
    }

    public SHA1Digest(SHA1Digest t) {
        super(t);
        this.f695X = new int[80];
        copyIn(t);
    }

    public SHA1Digest(byte[] encodedState) {
        super(encodedState);
        this.f695X = new int[80];
        this.f690H1 = Pack.bigEndianToInt(encodedState, 16);
        this.f691H2 = Pack.bigEndianToInt(encodedState, 20);
        this.f692H3 = Pack.bigEndianToInt(encodedState, 24);
        this.f693H4 = Pack.bigEndianToInt(encodedState, 28);
        this.f694H5 = Pack.bigEndianToInt(encodedState, 32);
        this.xOff = Pack.bigEndianToInt(encodedState, 36);
        for (int i = 0; i != this.xOff; i++) {
            this.f695X[i] = Pack.bigEndianToInt(encodedState, (i * 4) + 40);
        }
    }

    private void copyIn(SHA1Digest t) {
        this.f690H1 = t.f690H1;
        this.f691H2 = t.f691H2;
        this.f692H3 = t.f692H3;
        this.f693H4 = t.f693H4;
        this.f694H5 = t.f694H5;
        int[] iArr = t.f695X;
        System.arraycopy(iArr, 0, this.f695X, 0, iArr.length);
        this.xOff = t.xOff;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "SHA-1";
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return 20;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.digests.GeneralDigest
    protected void processWord(byte[] in, int inOff) {
        int n = in[inOff] << 24;
        int inOff2 = inOff + 1;
        int inOff3 = inOff2 + 1;
        int n2 = n | ((in[inOff2] & 255) << 16) | ((in[inOff3] & 255) << 8) | (in[inOff3 + 1] & 255);
        int[] iArr = this.f695X;
        int i = this.xOff;
        iArr[i] = n2;
        int i2 = i + 1;
        this.xOff = i2;
        if (i2 == 16) {
            processBlock();
        }
    }

    @Override // com.android.internal.org.bouncycastle.crypto.digests.GeneralDigest
    protected void processLength(long bitLength) {
        if (this.xOff > 14) {
            processBlock();
        }
        int[] iArr = this.f695X;
        iArr[14] = (int) (bitLength >>> 32);
        iArr[15] = (int) bitLength;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public int doFinal(byte[] out, int outOff) {
        finish();
        Pack.intToBigEndian(this.f690H1, out, outOff);
        Pack.intToBigEndian(this.f691H2, out, outOff + 4);
        Pack.intToBigEndian(this.f692H3, out, outOff + 8);
        Pack.intToBigEndian(this.f693H4, out, outOff + 12);
        Pack.intToBigEndian(this.f694H5, out, outOff + 16);
        reset();
        return 20;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.digests.GeneralDigest, com.android.internal.org.bouncycastle.crypto.Digest
    public void reset() {
        super.reset();
        this.f690H1 = 1732584193;
        this.f691H2 = -271733879;
        this.f692H3 = -1732584194;
        this.f693H4 = 271733878;
        this.f694H5 = -1009589776;
        this.xOff = 0;
        int i = 0;
        while (true) {
            int[] iArr = this.f695X;
            if (i != iArr.length) {
                iArr[i] = 0;
                i++;
            } else {
                return;
            }
        }
    }

    /* renamed from: f */
    private int m56f(int u, int v, int w) {
        return (u & v) | ((~u) & w);
    }

    /* renamed from: h */
    private int m54h(int u, int v, int w) {
        return (u ^ v) ^ w;
    }

    /* renamed from: g */
    private int m55g(int u, int v, int w) {
        return (u & v) | (u & w) | (v & w);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.digests.GeneralDigest
    protected void processBlock() {
        for (int i = 16; i < 80; i++) {
            int[] iArr = this.f695X;
            int t = ((iArr[i - 3] ^ iArr[i - 8]) ^ iArr[i - 14]) ^ iArr[i - 16];
            iArr[i] = (t << 1) | (t >>> 31);
        }
        int A = this.f690H1;
        int B = this.f691H2;
        int C = this.f692H3;
        int D = this.f693H4;
        int E = this.f694H5;
        int idx = 0;
        int j = 0;
        while (j < 4) {
            int idx2 = idx + 1;
            int E2 = E + ((A << 5) | (A >>> 27)) + m56f(B, C, D) + this.f695X[idx] + f686Y1;
            int B2 = (B << 30) | (B >>> 2);
            int idx3 = idx2 + 1;
            int D2 = D + ((E2 << 5) | (E2 >>> 27)) + m56f(A, B2, C) + this.f695X[idx2] + f686Y1;
            int A2 = (A << 30) | (A >>> 2);
            int idx4 = idx3 + 1;
            int C2 = C + ((D2 << 5) | (D2 >>> 27)) + m56f(E2, A2, B2) + this.f695X[idx3] + f686Y1;
            E = (E2 << 30) | (E2 >>> 2);
            int idx5 = idx4 + 1;
            B = B2 + ((C2 << 5) | (C2 >>> 27)) + m56f(D2, E, A2) + this.f695X[idx4] + f686Y1;
            D = (D2 << 30) | (D2 >>> 2);
            A = A2 + ((B << 5) | (B >>> 27)) + m56f(C2, D, E) + this.f695X[idx5] + f686Y1;
            C = (C2 << 30) | (C2 >>> 2);
            j++;
            idx = idx5 + 1;
        }
        int j2 = 0;
        while (j2 < 4) {
            int idx6 = idx + 1;
            int E3 = E + ((A << 5) | (A >>> 27)) + m54h(B, C, D) + this.f695X[idx] + f687Y2;
            int B3 = (B << 30) | (B >>> 2);
            int idx7 = idx6 + 1;
            int D3 = D + ((E3 << 5) | (E3 >>> 27)) + m54h(A, B3, C) + this.f695X[idx6] + f687Y2;
            int A3 = (A << 30) | (A >>> 2);
            int idx8 = idx7 + 1;
            int C3 = C + ((D3 << 5) | (D3 >>> 27)) + m54h(E3, A3, B3) + this.f695X[idx7] + f687Y2;
            E = (E3 << 30) | (E3 >>> 2);
            int idx9 = idx8 + 1;
            B = B3 + ((C3 << 5) | (C3 >>> 27)) + m54h(D3, E, A3) + this.f695X[idx8] + f687Y2;
            D = (D3 << 30) | (D3 >>> 2);
            A = A3 + ((B << 5) | (B >>> 27)) + m54h(C3, D, E) + this.f695X[idx9] + f687Y2;
            C = (C3 << 30) | (C3 >>> 2);
            j2++;
            idx = idx9 + 1;
        }
        int j3 = 0;
        while (j3 < 4) {
            int idx10 = idx + 1;
            int E4 = E + ((A << 5) | (A >>> 27)) + m55g(B, C, D) + this.f695X[idx] + f688Y3;
            int B4 = (B << 30) | (B >>> 2);
            int idx11 = idx10 + 1;
            int D4 = D + ((E4 << 5) | (E4 >>> 27)) + m55g(A, B4, C) + this.f695X[idx10] + f688Y3;
            int A4 = (A << 30) | (A >>> 2);
            int idx12 = idx11 + 1;
            int C4 = C + ((D4 << 5) | (D4 >>> 27)) + m55g(E4, A4, B4) + this.f695X[idx11] + f688Y3;
            E = (E4 << 30) | (E4 >>> 2);
            int idx13 = idx12 + 1;
            B = B4 + ((C4 << 5) | (C4 >>> 27)) + m55g(D4, E, A4) + this.f695X[idx12] + f688Y3;
            D = (D4 << 30) | (D4 >>> 2);
            A = A4 + ((B << 5) | (B >>> 27)) + m55g(C4, D, E) + this.f695X[idx13] + f688Y3;
            C = (C4 << 30) | (C4 >>> 2);
            j3++;
            idx = idx13 + 1;
        }
        int j4 = 0;
        while (j4 <= 3) {
            int idx14 = idx + 1;
            int E5 = E + ((A << 5) | (A >>> 27)) + m54h(B, C, D) + this.f695X[idx] + f689Y4;
            int B5 = (B << 30) | (B >>> 2);
            int idx15 = idx14 + 1;
            int D5 = D + ((E5 << 5) | (E5 >>> 27)) + m54h(A, B5, C) + this.f695X[idx14] + f689Y4;
            int A5 = (A << 30) | (A >>> 2);
            int idx16 = idx15 + 1;
            int C5 = C + ((D5 << 5) | (D5 >>> 27)) + m54h(E5, A5, B5) + this.f695X[idx15] + f689Y4;
            E = (E5 << 30) | (E5 >>> 2);
            int idx17 = idx16 + 1;
            B = B5 + ((C5 << 5) | (C5 >>> 27)) + m54h(D5, E, A5) + this.f695X[idx16] + f689Y4;
            D = (D5 << 30) | (D5 >>> 2);
            A = A5 + ((B << 5) | (B >>> 27)) + m54h(C5, D, E) + this.f695X[idx17] + f689Y4;
            C = (C5 << 30) | (C5 >>> 2);
            j4++;
            idx = idx17 + 1;
        }
        int j5 = this.f690H1;
        this.f690H1 = j5 + A;
        this.f691H2 += B;
        this.f692H3 += C;
        this.f693H4 += D;
        this.f694H5 += E;
        this.xOff = 0;
        for (int i2 = 0; i2 < 16; i2++) {
            this.f695X[i2] = 0;
        }
    }

    @Override // com.android.internal.org.bouncycastle.util.Memoable
    public Memoable copy() {
        return new SHA1Digest(this);
    }

    @Override // com.android.internal.org.bouncycastle.util.Memoable
    public void reset(Memoable other) {
        SHA1Digest d = (SHA1Digest) other;
        super.copyIn((GeneralDigest) d);
        copyIn(d);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.digests.EncodableDigest
    public byte[] getEncodedState() {
        byte[] state = new byte[(this.xOff * 4) + 40];
        super.populateState(state);
        Pack.intToBigEndian(this.f690H1, state, 16);
        Pack.intToBigEndian(this.f691H2, state, 20);
        Pack.intToBigEndian(this.f692H3, state, 24);
        Pack.intToBigEndian(this.f693H4, state, 28);
        Pack.intToBigEndian(this.f694H5, state, 32);
        Pack.intToBigEndian(this.xOff, state, 36);
        for (int i = 0; i != this.xOff; i++) {
            Pack.intToBigEndian(this.f695X[i], state, (i * 4) + 40);
        }
        return state;
    }
}
