package com.android.internal.org.bouncycastle.crypto.digests;

import android.security.keystore.KeyProperties;
import com.android.internal.org.bouncycastle.util.Memoable;
import com.android.internal.org.bouncycastle.util.Pack;
/* loaded from: classes4.dex */
public class SHA224Digest extends GeneralDigest implements EncodableDigest {
    private static final int DIGEST_LENGTH = 28;

    /* renamed from: K */
    static final int[] f696K = {1116352408, 1899447441, -1245643825, -373957723, 961987163, 1508970993, -1841331548, -1424204075, -670586216, 310598401, 607225278, 1426881987, 1925078388, -2132889090, -1680079193, -1046744716, -459576895, -272742522, 264347078, 604807628, 770255983, 1249150122, 1555081692, 1996064986, -1740746414, -1473132947, -1341970488, -1084653625, -958395405, -710438585, 113926993, 338241895, 666307205, 773529912, 1294757372, 1396182291, 1695183700, 1986661051, -2117940946, -1838011259, -1564481375, -1474664885, -1035236496, -949202525, -778901479, -694614492, -200395387, 275423344, 430227734, 506948616, 659060556, 883997877, 958139571, 1322822218, 1537002063, 1747873779, 1955562222, 2024104815, -2067236844, -1933114872, -1866530822, -1538233109, -1090935817, -965641998};

    /* renamed from: H1 */
    private int f697H1;

    /* renamed from: H2 */
    private int f698H2;

    /* renamed from: H3 */
    private int f699H3;

    /* renamed from: H4 */
    private int f700H4;

    /* renamed from: H5 */
    private int f701H5;

    /* renamed from: H6 */
    private int f702H6;

    /* renamed from: H7 */
    private int f703H7;

    /* renamed from: H8 */
    private int f704H8;

    /* renamed from: X */
    private int[] f705X;
    private int xOff;

    public SHA224Digest() {
        this.f705X = new int[64];
        reset();
    }

    public SHA224Digest(SHA224Digest t) {
        super(t);
        this.f705X = new int[64];
        doCopy(t);
    }

    private void doCopy(SHA224Digest t) {
        super.copyIn(t);
        this.f697H1 = t.f697H1;
        this.f698H2 = t.f698H2;
        this.f699H3 = t.f699H3;
        this.f700H4 = t.f700H4;
        this.f701H5 = t.f701H5;
        this.f702H6 = t.f702H6;
        this.f703H7 = t.f703H7;
        this.f704H8 = t.f704H8;
        int[] iArr = t.f705X;
        System.arraycopy(iArr, 0, this.f705X, 0, iArr.length);
        this.xOff = t.xOff;
    }

    public SHA224Digest(byte[] encodedState) {
        super(encodedState);
        this.f705X = new int[64];
        this.f697H1 = Pack.bigEndianToInt(encodedState, 16);
        this.f698H2 = Pack.bigEndianToInt(encodedState, 20);
        this.f699H3 = Pack.bigEndianToInt(encodedState, 24);
        this.f700H4 = Pack.bigEndianToInt(encodedState, 28);
        this.f701H5 = Pack.bigEndianToInt(encodedState, 32);
        this.f702H6 = Pack.bigEndianToInt(encodedState, 36);
        this.f703H7 = Pack.bigEndianToInt(encodedState, 40);
        this.f704H8 = Pack.bigEndianToInt(encodedState, 44);
        this.xOff = Pack.bigEndianToInt(encodedState, 48);
        for (int i = 0; i != this.xOff; i++) {
            this.f705X[i] = Pack.bigEndianToInt(encodedState, (i * 4) + 52);
        }
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return KeyProperties.DIGEST_SHA224;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return 28;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.digests.GeneralDigest
    protected void processWord(byte[] in, int inOff) {
        int n = in[inOff] << 24;
        int inOff2 = inOff + 1;
        int inOff3 = inOff2 + 1;
        int n2 = n | ((in[inOff2] & 255) << 16) | ((in[inOff3] & 255) << 8) | (in[inOff3 + 1] & 255);
        int[] iArr = this.f705X;
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
        int[] iArr = this.f705X;
        iArr[14] = (int) (bitLength >>> 32);
        iArr[15] = (int) ((-1) & bitLength);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public int doFinal(byte[] out, int outOff) {
        finish();
        Pack.intToBigEndian(this.f697H1, out, outOff);
        Pack.intToBigEndian(this.f698H2, out, outOff + 4);
        Pack.intToBigEndian(this.f699H3, out, outOff + 8);
        Pack.intToBigEndian(this.f700H4, out, outOff + 12);
        Pack.intToBigEndian(this.f701H5, out, outOff + 16);
        Pack.intToBigEndian(this.f702H6, out, outOff + 20);
        Pack.intToBigEndian(this.f703H7, out, outOff + 24);
        reset();
        return 28;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.digests.GeneralDigest, com.android.internal.org.bouncycastle.crypto.Digest
    public void reset() {
        super.reset();
        this.f697H1 = -1056596264;
        this.f698H2 = 914150663;
        this.f699H3 = 812702999;
        this.f700H4 = -150054599;
        this.f701H5 = -4191439;
        this.f702H6 = 1750603025;
        this.f703H7 = 1694076839;
        this.f704H8 = -1090891868;
        this.xOff = 0;
        int i = 0;
        while (true) {
            int[] iArr = this.f705X;
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
            int[] iArr = this.f705X;
            int Theta1 = Theta1(iArr[t - 2]);
            int[] iArr2 = this.f705X;
            iArr[t] = Theta1 + iArr2[t - 7] + Theta0(iArr2[t - 15]) + this.f705X[t - 16];
        }
        int a = this.f697H1;
        int b = this.f698H2;
        int c = this.f699H3;
        int d = this.f700H4;
        int e = this.f701H5;
        int f = this.f702H6;
        int g = this.f703H7;
        int h = this.f704H8;
        int t2 = 0;
        for (int i = 0; i < 8; i++) {
            int Sum1 = Sum1(e) + m53Ch(e, f, g);
            int[] iArr3 = f696K;
            int h2 = h + Sum1 + iArr3[t2] + this.f705X[t2];
            int d2 = d + h2;
            int h3 = h2 + Sum0(a) + Maj(a, b, c);
            int t3 = t2 + 1;
            int g2 = g + Sum1(d2) + m53Ch(d2, e, f) + iArr3[t3] + this.f705X[t3];
            int c2 = c + g2;
            int g3 = g2 + Sum0(h3) + Maj(h3, a, b);
            int t4 = t3 + 1;
            int f2 = f + Sum1(c2) + m53Ch(c2, d2, e) + iArr3[t4] + this.f705X[t4];
            int b2 = b + f2;
            int f3 = f2 + Sum0(g3) + Maj(g3, h3, a);
            int t5 = t4 + 1;
            int e2 = e + Sum1(b2) + m53Ch(b2, c2, d2) + iArr3[t5] + this.f705X[t5];
            int a2 = a + e2;
            int e3 = e2 + Sum0(f3) + Maj(f3, g3, h3);
            int t6 = t5 + 1;
            int d3 = d2 + Sum1(a2) + m53Ch(a2, b2, c2) + iArr3[t6] + this.f705X[t6];
            h = h3 + d3;
            d = d3 + Sum0(e3) + Maj(e3, f3, g3);
            int t7 = t6 + 1;
            int c3 = c2 + Sum1(h) + m53Ch(h, a2, b2) + iArr3[t7] + this.f705X[t7];
            g = g3 + c3;
            c = c3 + Sum0(d) + Maj(d, e3, f3);
            int t8 = t7 + 1;
            int b3 = b2 + Sum1(g) + m53Ch(g, h, a2) + iArr3[t8] + this.f705X[t8];
            f = f3 + b3;
            b = b3 + Sum0(c) + Maj(c, d, e3);
            int t9 = t8 + 1;
            int a3 = a2 + Sum1(f) + m53Ch(f, g, h) + iArr3[t9] + this.f705X[t9];
            e = e3 + a3;
            a = a3 + Sum0(b) + Maj(b, c, d);
            t2 = t9 + 1;
        }
        int i2 = this.f697H1;
        this.f697H1 = i2 + a;
        this.f698H2 += b;
        this.f699H3 += c;
        this.f700H4 += d;
        this.f701H5 += e;
        this.f702H6 += f;
        this.f703H7 += g;
        this.f704H8 += h;
        this.xOff = 0;
        for (int i3 = 0; i3 < 16; i3++) {
            this.f705X[i3] = 0;
        }
    }

    /* renamed from: Ch */
    private int m53Ch(int x, int y, int z) {
        return (x & y) ^ ((~x) & z);
    }

    private int Maj(int x, int y, int z) {
        return ((x & y) ^ (x & z)) ^ (y & z);
    }

    private int Sum0(int x) {
        return (((x >>> 2) | (x << 30)) ^ ((x >>> 13) | (x << 19))) ^ ((x >>> 22) | (x << 10));
    }

    private int Sum1(int x) {
        return (((x >>> 6) | (x << 26)) ^ ((x >>> 11) | (x << 21))) ^ ((x >>> 25) | (x << 7));
    }

    private int Theta0(int x) {
        return (((x >>> 7) | (x << 25)) ^ ((x >>> 18) | (x << 14))) ^ (x >>> 3);
    }

    private int Theta1(int x) {
        return (((x >>> 17) | (x << 15)) ^ ((x >>> 19) | (x << 13))) ^ (x >>> 10);
    }

    @Override // com.android.internal.org.bouncycastle.util.Memoable
    public Memoable copy() {
        return new SHA224Digest(this);
    }

    @Override // com.android.internal.org.bouncycastle.util.Memoable
    public void reset(Memoable other) {
        SHA224Digest d = (SHA224Digest) other;
        doCopy(d);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.digests.EncodableDigest
    public byte[] getEncodedState() {
        byte[] state = new byte[(this.xOff * 4) + 52];
        super.populateState(state);
        Pack.intToBigEndian(this.f697H1, state, 16);
        Pack.intToBigEndian(this.f698H2, state, 20);
        Pack.intToBigEndian(this.f699H3, state, 24);
        Pack.intToBigEndian(this.f700H4, state, 28);
        Pack.intToBigEndian(this.f701H5, state, 32);
        Pack.intToBigEndian(this.f702H6, state, 36);
        Pack.intToBigEndian(this.f703H7, state, 40);
        Pack.intToBigEndian(this.f704H8, state, 44);
        Pack.intToBigEndian(this.xOff, state, 48);
        for (int i = 0; i != this.xOff; i++) {
            Pack.intToBigEndian(this.f705X[i], state, (i * 4) + 52);
        }
        return state;
    }
}
