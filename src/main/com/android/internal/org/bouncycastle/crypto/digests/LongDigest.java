package com.android.internal.org.bouncycastle.crypto.digests;

import com.android.internal.org.bouncycastle.crypto.ExtendedDigest;
import com.android.internal.org.bouncycastle.util.Memoable;
import com.android.internal.org.bouncycastle.util.Pack;
/* loaded from: classes4.dex */
public abstract class LongDigest implements ExtendedDigest, Memoable, EncodableDigest {
    private static final int BYTE_LENGTH = 128;

    /* renamed from: K */
    static final long[] f666K = {4794697086780616226L, 8158064640168781261L, -5349999486874862801L, -1606136188198331460L, 4131703408338449720L, 6480981068601479193L, -7908458776815382629L, -6116909921290321640L, -2880145864133508542L, 1334009975649890238L, 2608012711638119052L, 6128411473006802146L, 8268148722764581231L, -9160688886553864527L, -7215885187991268811L, -4495734319001033068L, -1973867731355612462L, -1171420211273849373L, 1135362057144423861L, 2597628984639134821L, 3308224258029322869L, 5365058923640841347L, 6679025012923562964L, 8573033837759648693L, -7476448914759557205L, -6327057829258317296L, -5763719355590565569L, -4658551843659510044L, -4116276920077217854L, -3051310485924567259L, 489312712824947311L, 1452737877330783856L, 2861767655752347644L, 3322285676063803686L, 5560940570517711597L, 5996557281743188959L, 7280758554555802590L, 8532644243296465576L, -9096487096722542874L, -7894198246740708037L, -6719396339535248540L, -6333637450476146687L, -4446306890439682159L, -4076793802049405392L, -3345356375505022440L, -2983346525034927856L, -860691631967231958L, 1182934255886127544L, 1847814050463011016L, 2177327727835720531L, 2830643537854262169L, 3796741975233480872L, 4115178125766777443L, 5681478168544905931L, 6601373596472566643L, 7507060721942968483L, 8399075790359081724L, 8693463985226723168L, -8878714635349349518L, -8302665154208450068L, -8016688836872298968L, -6606660893046293015L, -4685533653050689259L, -4147400797238176981L, -3880063495543823972L, -3348786107499101689L, -1523767162380948706L, -757361751448694408L, 500013540394364858L, 748580250866718886L, 1242879168328830382L, 1977374033974150939L, 2944078676154940804L, 3659926193048069267L, 4368137639120453308L, 4836135668995329356L, 5532061633213252278L, 6448918945643986474L, 6902733635092675308L, 7801388544844847127L};

    /* renamed from: H1 */
    protected long f667H1;

    /* renamed from: H2 */
    protected long f668H2;

    /* renamed from: H3 */
    protected long f669H3;

    /* renamed from: H4 */
    protected long f670H4;

    /* renamed from: H5 */
    protected long f671H5;

    /* renamed from: H6 */
    protected long f672H6;

    /* renamed from: H7 */
    protected long f673H7;

    /* renamed from: H8 */
    protected long f674H8;

    /* renamed from: W */
    private long[] f675W;
    private long byteCount1;
    private long byteCount2;
    private int wOff;
    private byte[] xBuf;
    private int xBufOff;

    /* JADX INFO: Access modifiers changed from: protected */
    public LongDigest() {
        this.xBuf = new byte[8];
        this.f675W = new long[80];
        this.xBufOff = 0;
        reset();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public LongDigest(LongDigest t) {
        this.xBuf = new byte[8];
        this.f675W = new long[80];
        copyIn(t);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void copyIn(LongDigest t) {
        byte[] bArr = t.xBuf;
        System.arraycopy(bArr, 0, this.xBuf, 0, bArr.length);
        this.xBufOff = t.xBufOff;
        this.byteCount1 = t.byteCount1;
        this.byteCount2 = t.byteCount2;
        this.f667H1 = t.f667H1;
        this.f668H2 = t.f668H2;
        this.f669H3 = t.f669H3;
        this.f670H4 = t.f670H4;
        this.f671H5 = t.f671H5;
        this.f672H6 = t.f672H6;
        this.f673H7 = t.f673H7;
        this.f674H8 = t.f674H8;
        long[] jArr = t.f675W;
        System.arraycopy(jArr, 0, this.f675W, 0, jArr.length);
        this.wOff = t.wOff;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void populateState(byte[] state) {
        System.arraycopy(this.xBuf, 0, state, 0, this.xBufOff);
        Pack.intToBigEndian(this.xBufOff, state, 8);
        Pack.longToBigEndian(this.byteCount1, state, 12);
        Pack.longToBigEndian(this.byteCount2, state, 20);
        Pack.longToBigEndian(this.f667H1, state, 28);
        Pack.longToBigEndian(this.f668H2, state, 36);
        Pack.longToBigEndian(this.f669H3, state, 44);
        Pack.longToBigEndian(this.f670H4, state, 52);
        Pack.longToBigEndian(this.f671H5, state, 60);
        Pack.longToBigEndian(this.f672H6, state, 68);
        Pack.longToBigEndian(this.f673H7, state, 76);
        Pack.longToBigEndian(this.f674H8, state, 84);
        Pack.intToBigEndian(this.wOff, state, 92);
        for (int i = 0; i < this.wOff; i++) {
            Pack.longToBigEndian(this.f675W[i], state, (i * 8) + 96);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void restoreState(byte[] encodedState) {
        int bigEndianToInt = Pack.bigEndianToInt(encodedState, 8);
        this.xBufOff = bigEndianToInt;
        System.arraycopy(encodedState, 0, this.xBuf, 0, bigEndianToInt);
        this.byteCount1 = Pack.bigEndianToLong(encodedState, 12);
        this.byteCount2 = Pack.bigEndianToLong(encodedState, 20);
        this.f667H1 = Pack.bigEndianToLong(encodedState, 28);
        this.f668H2 = Pack.bigEndianToLong(encodedState, 36);
        this.f669H3 = Pack.bigEndianToLong(encodedState, 44);
        this.f670H4 = Pack.bigEndianToLong(encodedState, 52);
        this.f671H5 = Pack.bigEndianToLong(encodedState, 60);
        this.f672H6 = Pack.bigEndianToLong(encodedState, 68);
        this.f673H7 = Pack.bigEndianToLong(encodedState, 76);
        this.f674H8 = Pack.bigEndianToLong(encodedState, 84);
        this.wOff = Pack.bigEndianToInt(encodedState, 92);
        for (int i = 0; i < this.wOff; i++) {
            this.f675W[i] = Pack.bigEndianToLong(encodedState, (i * 8) + 96);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getEncodedStateSize() {
        return (this.wOff * 8) + 96;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public void update(byte in) {
        byte[] bArr = this.xBuf;
        int i = this.xBufOff;
        int i2 = i + 1;
        this.xBufOff = i2;
        bArr[i] = in;
        if (i2 == bArr.length) {
            processWord(bArr, 0);
            this.xBufOff = 0;
        }
        this.byteCount1++;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public void update(byte[] in, int inOff, int len) {
        while (this.xBufOff != 0 && len > 0) {
            update(in[inOff]);
            inOff++;
            len--;
        }
        while (len > this.xBuf.length) {
            processWord(in, inOff);
            byte[] bArr = this.xBuf;
            inOff += bArr.length;
            len -= bArr.length;
            this.byteCount1 += bArr.length;
        }
        while (len > 0) {
            update(in[inOff]);
            inOff++;
            len--;
        }
    }

    public void finish() {
        adjustByteCounts();
        long lowBitLength = this.byteCount1 << 3;
        long hiBitLength = this.byteCount2;
        update(Byte.MIN_VALUE);
        while (this.xBufOff != 0) {
            update((byte) 0);
        }
        processLength(lowBitLength, hiBitLength);
        processBlock();
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public void reset() {
        this.byteCount1 = 0L;
        this.byteCount2 = 0L;
        this.xBufOff = 0;
        int i = 0;
        while (true) {
            byte[] bArr = this.xBuf;
            if (i >= bArr.length) {
                break;
            }
            bArr[i] = 0;
            i++;
        }
        this.wOff = 0;
        int i2 = 0;
        while (true) {
            long[] jArr = this.f675W;
            if (i2 != jArr.length) {
                jArr[i2] = 0;
                i2++;
            } else {
                return;
            }
        }
    }

    @Override // com.android.internal.org.bouncycastle.crypto.ExtendedDigest
    public int getByteLength() {
        return 128;
    }

    protected void processWord(byte[] in, int inOff) {
        this.f675W[this.wOff] = Pack.bigEndianToLong(in, inOff);
        int i = this.wOff + 1;
        this.wOff = i;
        if (i == 16) {
            processBlock();
        }
    }

    private void adjustByteCounts() {
        long j = this.byteCount1;
        if (j > 2305843009213693951L) {
            this.byteCount2 += j >>> 61;
            this.byteCount1 = j & 2305843009213693951L;
        }
    }

    protected void processLength(long lowW, long hiW) {
        if (this.wOff > 14) {
            processBlock();
        }
        long[] jArr = this.f675W;
        jArr[14] = hiW;
        jArr[15] = lowW;
    }

    protected void processBlock() {
        adjustByteCounts();
        for (int t = 16; t <= 79; t++) {
            long[] jArr = this.f675W;
            long Sigma1 = Sigma1(jArr[t - 2]);
            long[] jArr2 = this.f675W;
            jArr[t] = Sigma1 + jArr2[t - 7] + Sigma0(jArr2[t - 15]) + this.f675W[t - 16];
        }
        long a = this.f667H1;
        long b = this.f668H2;
        long c = this.f669H3;
        long d = this.f670H4;
        long e = this.f671H5;
        long f = this.f672H6;
        long g = this.f673H7;
        long a2 = this.f674H8;
        long f2 = f;
        long g2 = g;
        long g3 = b;
        long h = c;
        int i = 0;
        int t2 = 0;
        long d2 = d;
        long e2 = e;
        long b2 = a;
        long e3 = a2;
        while (i < 10) {
            long Sum1 = Sum1(e2);
            long e4 = e2;
            long e5 = g2;
            long[] jArr3 = f666K;
            int t3 = t2 + 1;
            long h2 = e3 + Sum1 + m64Ch(e2, f2, e5) + jArr3[t2] + this.f675W[t2];
            long d3 = d2 + h2;
            long b3 = g3;
            long d4 = h;
            long h3 = h2 + Sum0(b2) + Maj(b2, g3, d4);
            int t4 = t3 + 1;
            long g4 = g2 + Sum1(d3) + m64Ch(d3, e4, f2) + jArr3[t3] + this.f675W[t3];
            long c2 = h + g4;
            long g5 = g4 + Sum0(h3) + Maj(h3, b2, b3);
            int t5 = t4 + 1;
            long f3 = f2 + Sum1(c2) + m64Ch(c2, d3, e4) + jArr3[t4] + this.f675W[t4];
            long b4 = b3 + f3;
            long f4 = f3 + Sum0(g5) + Maj(g5, h3, b2);
            int t6 = t5 + 1;
            long e6 = e4 + Sum1(b4) + m64Ch(b4, c2, d3) + jArr3[t5] + this.f675W[t5];
            long a3 = b2 + e6;
            long e7 = e6 + Sum0(f4) + Maj(f4, g5, h3);
            long e8 = Sum1(a3);
            int t7 = t6 + 1;
            long d5 = d3 + e8 + m64Ch(a3, b4, c2) + jArr3[t6] + this.f675W[t6];
            long h4 = h3 + d5;
            long d6 = d5 + Sum0(e7) + Maj(e7, f4, g5);
            long d7 = Sum1(h4);
            int t8 = t7 + 1;
            long c3 = c2 + d7 + m64Ch(h4, a3, b4) + jArr3[t7] + this.f675W[t7];
            long g6 = g5 + c3;
            long c4 = c3 + Sum0(d6) + Maj(d6, e7, f4);
            long c5 = Sum1(g6);
            h = c4;
            int t9 = t8 + 1;
            long b5 = b4 + c5 + m64Ch(g6, h4, a3) + jArr3[t8] + this.f675W[t8];
            long f5 = f4 + b5;
            long b6 = b5 + Sum0(h) + Maj(h, d6, e7);
            long b7 = Sum1(f5);
            int t10 = t9 + 1;
            long a4 = a3 + b7 + m64Ch(f5, g6, h4) + jArr3[t9] + this.f675W[t9];
            long a5 = a4 + Sum0(b6) + Maj(b6, h, d6);
            i++;
            e2 = e7 + a4;
            g2 = g6;
            t2 = t10;
            e3 = h4;
            g3 = b6;
            f2 = f5;
            d2 = d6;
            b2 = a5;
        }
        long b8 = g3;
        this.f667H1 += b2;
        this.f668H2 += b8;
        this.f669H3 += h;
        this.f670H4 += d2;
        this.f671H5 += e2;
        this.f672H6 += f2;
        this.f673H7 += g2;
        this.f674H8 += e3;
        this.wOff = 0;
        for (int i2 = 0; i2 < 16; i2++) {
            this.f675W[i2] = 0;
        }
    }

    /* renamed from: Ch */
    private long m64Ch(long x, long y, long z) {
        return (x & y) ^ ((~x) & z);
    }

    private long Maj(long x, long y, long z) {
        return ((x & y) ^ (x & z)) ^ (y & z);
    }

    private long Sum0(long x) {
        return (((x << 36) | (x >>> 28)) ^ ((x << 30) | (x >>> 34))) ^ ((x << 25) | (x >>> 39));
    }

    private long Sum1(long x) {
        return (((x << 50) | (x >>> 14)) ^ ((x << 46) | (x >>> 18))) ^ ((x << 23) | (x >>> 41));
    }

    private long Sigma0(long x) {
        return (((x << 63) | (x >>> 1)) ^ ((x << 56) | (x >>> 8))) ^ (x >>> 7);
    }

    private long Sigma1(long x) {
        return (((x << 45) | (x >>> 19)) ^ ((x << 3) | (x >>> 61))) ^ (x >>> 6);
    }
}
