package com.android.internal.org.bouncycastle.crypto.modes.gcm;

import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.Pack;
import java.lang.reflect.Array;
/* loaded from: classes4.dex */
public class Tables4kGCMMultiplier implements GCMMultiplier {

    /* renamed from: H */
    private byte[] f765H;

    /* renamed from: T */
    private long[][] f766T;

    @Override // com.android.internal.org.bouncycastle.crypto.modes.gcm.GCMMultiplier
    public void init(byte[] H) {
        if (this.f766T == null) {
            this.f766T = (long[][]) Array.newInstance(Long.TYPE, 256, 2);
        } else if (Arrays.areEqual(this.f765H, H)) {
            return;
        }
        byte[] clone = Arrays.clone(H);
        this.f765H = clone;
        GCMUtil.asLongs(clone, this.f766T[1]);
        long[] jArr = this.f766T[1];
        GCMUtil.multiplyP7(jArr, jArr);
        for (int n = 2; n < 256; n += 2) {
            long[][] jArr2 = this.f766T;
            GCMUtil.divideP(jArr2[n >> 1], jArr2[n]);
            long[][] jArr3 = this.f766T;
            GCMUtil.xor(jArr3[n], jArr3[1], jArr3[n + 1]);
        }
    }

    @Override // com.android.internal.org.bouncycastle.crypto.modes.gcm.GCMMultiplier
    public void multiplyH(byte[] x) {
        long[] t = this.f766T[x[15] & 255];
        long z0 = t[0];
        long z1 = t[1];
        for (int i = 14; i >= 0; i--) {
            long[] t2 = this.f766T[x[i] & 255];
            long c = z1 << 56;
            z1 = t2[1] ^ ((z1 >>> 8) | (z0 << 56));
            z0 = (((((z0 >>> 8) ^ t2[0]) ^ c) ^ (c >>> 1)) ^ (c >>> 2)) ^ (c >>> 7);
        }
        Pack.longToBigEndian(z0, x, 0);
        Pack.longToBigEndian(z1, x, 8);
    }
}
