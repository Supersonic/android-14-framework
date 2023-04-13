package com.android.internal.org.bouncycastle.crypto.digests;

import com.android.internal.org.bouncycastle.util.Memoable;
import com.android.internal.org.bouncycastle.util.Pack;
/* loaded from: classes4.dex */
public class SHA256Digest extends GeneralDigest implements EncodableDigest {
    private static final int DIGEST_LENGTH = 32;

    /* renamed from: K */
    static final int[] f706K = {1116352408, 1899447441, -1245643825, -373957723, 961987163, 1508970993, -1841331548, -1424204075, -670586216, 310598401, 607225278, 1426881987, 1925078388, -2132889090, -1680079193, -1046744716, -459576895, -272742522, 264347078, 604807628, 770255983, 1249150122, 1555081692, 1996064986, -1740746414, -1473132947, -1341970488, -1084653625, -958395405, -710438585, 113926993, 338241895, 666307205, 773529912, 1294757372, 1396182291, 1695183700, 1986661051, -2117940946, -1838011259, -1564481375, -1474664885, -1035236496, -949202525, -778901479, -694614492, -200395387, 275423344, 430227734, 506948616, 659060556, 883997877, 958139571, 1322822218, 1537002063, 1747873779, 1955562222, 2024104815, -2067236844, -1933114872, -1866530822, -1538233109, -1090935817, -965641998};

    /* renamed from: H1 */
    private int f707H1;

    /* renamed from: H2 */
    private int f708H2;

    /* renamed from: H3 */
    private int f709H3;

    /* renamed from: H4 */
    private int f710H4;

    /* renamed from: H5 */
    private int f711H5;

    /* renamed from: H6 */
    private int f712H6;

    /* renamed from: H7 */
    private int f713H7;

    /* renamed from: H8 */
    private int f714H8;

    /* renamed from: X */
    private int[] f715X;
    private int xOff;

    public SHA256Digest() {
        this.f715X = new int[64];
        reset();
    }

    public SHA256Digest(SHA256Digest t) {
        super(t);
        this.f715X = new int[64];
        copyIn(t);
    }

    private void copyIn(SHA256Digest t) {
        super.copyIn((GeneralDigest) t);
        this.f707H1 = t.f707H1;
        this.f708H2 = t.f708H2;
        this.f709H3 = t.f709H3;
        this.f710H4 = t.f710H4;
        this.f711H5 = t.f711H5;
        this.f712H6 = t.f712H6;
        this.f713H7 = t.f713H7;
        this.f714H8 = t.f714H8;
        int[] iArr = t.f715X;
        System.arraycopy(iArr, 0, this.f715X, 0, iArr.length);
        this.xOff = t.xOff;
    }

    public SHA256Digest(byte[] encodedState) {
        super(encodedState);
        this.f715X = new int[64];
        this.f707H1 = Pack.bigEndianToInt(encodedState, 16);
        this.f708H2 = Pack.bigEndianToInt(encodedState, 20);
        this.f709H3 = Pack.bigEndianToInt(encodedState, 24);
        this.f710H4 = Pack.bigEndianToInt(encodedState, 28);
        this.f711H5 = Pack.bigEndianToInt(encodedState, 32);
        this.f712H6 = Pack.bigEndianToInt(encodedState, 36);
        this.f713H7 = Pack.bigEndianToInt(encodedState, 40);
        this.f714H8 = Pack.bigEndianToInt(encodedState, 44);
        this.xOff = Pack.bigEndianToInt(encodedState, 48);
        for (int i = 0; i != this.xOff; i++) {
            this.f715X[i] = Pack.bigEndianToInt(encodedState, (i * 4) + 52);
        }
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return "SHA-256";
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return 32;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.digests.GeneralDigest
    protected void processWord(byte[] in, int inOff) {
        int n = in[inOff] << 24;
        int inOff2 = inOff + 1;
        int inOff3 = inOff2 + 1;
        int n2 = n | ((in[inOff2] & 255) << 16) | ((in[inOff3] & 255) << 8) | (in[inOff3 + 1] & 255);
        int[] iArr = this.f715X;
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
        int[] iArr = this.f715X;
        iArr[14] = (int) (bitLength >>> 32);
        iArr[15] = (int) ((-1) & bitLength);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public int doFinal(byte[] out, int outOff) {
        finish();
        Pack.intToBigEndian(this.f707H1, out, outOff);
        Pack.intToBigEndian(this.f708H2, out, outOff + 4);
        Pack.intToBigEndian(this.f709H3, out, outOff + 8);
        Pack.intToBigEndian(this.f710H4, out, outOff + 12);
        Pack.intToBigEndian(this.f711H5, out, outOff + 16);
        Pack.intToBigEndian(this.f712H6, out, outOff + 20);
        Pack.intToBigEndian(this.f713H7, out, outOff + 24);
        Pack.intToBigEndian(this.f714H8, out, outOff + 28);
        reset();
        return 32;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.digests.GeneralDigest, com.android.internal.org.bouncycastle.crypto.Digest
    public void reset() {
        super.reset();
        this.f707H1 = 1779033703;
        this.f708H2 = -1150833019;
        this.f709H3 = 1013904242;
        this.f710H4 = -1521486534;
        this.f711H5 = 1359893119;
        this.f712H6 = -1694144372;
        this.f713H7 = 528734635;
        this.f714H8 = 1541459225;
        this.xOff = 0;
        int i = 0;
        while (true) {
            int[] iArr = this.f715X;
            if (i != iArr.length) {
                iArr[i] = 0;
                i++;
            } else {
                return;
            }
        }
    }

    @Override // com.android.internal.org.bouncycastle.crypto.digests.GeneralDigest
    protected void processBlock() {
        for (int t = 16; t <= 63; t++) {
            int[] iArr = this.f715X;
            int Theta1 = Theta1(iArr[t - 2]);
            int[] iArr2 = this.f715X;
            iArr[t] = Theta1 + iArr2[t - 7] + Theta0(iArr2[t - 15]) + this.f715X[t - 16];
        }
        int a = this.f707H1;
        int b = this.f708H2;
        int c = this.f709H3;
        int d = this.f710H4;
        int e = this.f711H5;
        int f = this.f712H6;
        int g = this.f713H7;
        int h = this.f714H8;
        int t2 = 0;
        for (int i = 0; i < 8; i++) {
            int Sum1 = Sum1(e) + m52Ch(e, f, g);
            int[] iArr3 = f706K;
            int h2 = h + Sum1 + iArr3[t2] + this.f715X[t2];
            int d2 = d + h2;
            int h3 = h2 + Sum0(a) + Maj(a, b, c);
            int t3 = t2 + 1;
            int g2 = g + Sum1(d2) + m52Ch(d2, e, f) + iArr3[t3] + this.f715X[t3];
            int c2 = c + g2;
            int g3 = g2 + Sum0(h3) + Maj(h3, a, b);
            int t4 = t3 + 1;
            int f2 = f + Sum1(c2) + m52Ch(c2, d2, e) + iArr3[t4] + this.f715X[t4];
            int b2 = b + f2;
            int f3 = f2 + Sum0(g3) + Maj(g3, h3, a);
            int t5 = t4 + 1;
            int e2 = e + Sum1(b2) + m52Ch(b2, c2, d2) + iArr3[t5] + this.f715X[t5];
            int a2 = a + e2;
            int e3 = e2 + Sum0(f3) + Maj(f3, g3, h3);
            int t6 = t5 + 1;
            int d3 = d2 + Sum1(a2) + m52Ch(a2, b2, c2) + iArr3[t6] + this.f715X[t6];
            h = h3 + d3;
            d = d3 + Sum0(e3) + Maj(e3, f3, g3);
            int t7 = t6 + 1;
            int c3 = c2 + Sum1(h) + m52Ch(h, a2, b2) + iArr3[t7] + this.f715X[t7];
            g = g3 + c3;
            c = c3 + Sum0(d) + Maj(d, e3, f3);
            int t8 = t7 + 1;
            int b3 = b2 + Sum1(g) + m52Ch(g, h, a2) + iArr3[t8] + this.f715X[t8];
            f = f3 + b3;
            b = b3 + Sum0(c) + Maj(c, d, e3);
            int t9 = t8 + 1;
            int a3 = a2 + Sum1(f) + m52Ch(f, g, h) + iArr3[t9] + this.f715X[t9];
            e = e3 + a3;
            a = a3 + Sum0(b) + Maj(b, c, d);
            t2 = t9 + 1;
        }
        int i2 = this.f707H1;
        this.f707H1 = i2 + a;
        this.f708H2 += b;
        this.f709H3 += c;
        this.f710H4 += d;
        this.f711H5 += e;
        this.f712H6 += f;
        this.f713H7 += g;
        this.f714H8 += h;
        this.xOff = 0;
        for (int i3 = 0; i3 < 16; i3++) {
            this.f715X[i3] = 0;
        }
    }

    /* renamed from: Ch */
    private static int m52Ch(int x, int y, int z) {
        return (x & y) ^ ((~x) & z);
    }

    private static int Maj(int x, int y, int z) {
        return (x & y) | ((x ^ y) & z);
    }

    private static int Sum0(int x) {
        return (((x >>> 2) | (x << 30)) ^ ((x >>> 13) | (x << 19))) ^ ((x >>> 22) | (x << 10));
    }

    private static int Sum1(int x) {
        return (((x >>> 6) | (x << 26)) ^ ((x >>> 11) | (x << 21))) ^ ((x >>> 25) | (x << 7));
    }

    private static int Theta0(int x) {
        return (((x >>> 7) | (x << 25)) ^ ((x >>> 18) | (x << 14))) ^ (x >>> 3);
    }

    private static int Theta1(int x) {
        return (((x >>> 17) | (x << 15)) ^ ((x >>> 19) | (x << 13))) ^ (x >>> 10);
    }

    @Override // com.android.internal.org.bouncycastle.util.Memoable
    public Memoable copy() {
        return new SHA256Digest(this);
    }

    @Override // com.android.internal.org.bouncycastle.util.Memoable
    public void reset(Memoable other) {
        SHA256Digest d = (SHA256Digest) other;
        copyIn(d);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.digests.EncodableDigest
    public byte[] getEncodedState() {
        byte[] state = new byte[(this.xOff * 4) + 52];
        super.populateState(state);
        Pack.intToBigEndian(this.f707H1, state, 16);
        Pack.intToBigEndian(this.f708H2, state, 20);
        Pack.intToBigEndian(this.f709H3, state, 24);
        Pack.intToBigEndian(this.f710H4, state, 28);
        Pack.intToBigEndian(this.f711H5, state, 32);
        Pack.intToBigEndian(this.f712H6, state, 36);
        Pack.intToBigEndian(this.f713H7, state, 40);
        Pack.intToBigEndian(this.f714H8, state, 44);
        Pack.intToBigEndian(this.xOff, state, 48);
        for (int i = 0; i != this.xOff; i++) {
            Pack.intToBigEndian(this.f715X[i], state, (i * 4) + 52);
        }
        return state;
    }
}
