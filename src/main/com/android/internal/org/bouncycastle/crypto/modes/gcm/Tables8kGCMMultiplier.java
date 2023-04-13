package com.android.internal.org.bouncycastle.crypto.modes.gcm;

import com.android.internal.midi.MidiConstants;
import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.Pack;
import java.lang.reflect.Array;
/* loaded from: classes4.dex */
public class Tables8kGCMMultiplier implements GCMMultiplier {

    /* renamed from: H */
    private byte[] f767H;

    /* renamed from: T */
    private long[][][] f768T;

    @Override // com.android.internal.org.bouncycastle.crypto.modes.gcm.GCMMultiplier
    public void init(byte[] H) {
        if (this.f768T == null) {
            this.f768T = (long[][][]) Array.newInstance(Long.TYPE, 32, 16, 2);
        } else if (Arrays.areEqual(this.f767H, H)) {
            return;
        }
        this.f767H = Arrays.clone(H);
        for (int i = 0; i < 32; i++) {
            long[][][] jArr = this.f768T;
            long[][] t = jArr[i];
            if (i == 0) {
                GCMUtil.asLongs(this.f767H, t[1]);
                GCMUtil.multiplyP3(t[1], t[1]);
            } else {
                GCMUtil.multiplyP4(jArr[i - 1][1], t[1]);
            }
            for (int n = 2; n < 16; n += 2) {
                GCMUtil.divideP(t[n >> 1], t[n]);
                GCMUtil.xor(t[n], t[1], t[n + 1]);
            }
        }
    }

    @Override // com.android.internal.org.bouncycastle.crypto.modes.gcm.GCMMultiplier
    public void multiplyH(byte[] x) {
        long z0 = 0;
        long z1 = 0;
        for (int i = 15; i >= 0; i--) {
            long[][][] jArr = this.f768T;
            long[] u = jArr[i + i + 1][x[i] & MidiConstants.STATUS_CHANNEL_MASK];
            long[] v = jArr[i + i][(x[i] & 240) >>> 4];
            z0 ^= u[0] ^ v[0];
            z1 ^= u[1] ^ v[1];
        }
        Pack.longToBigEndian(z0, x, 0);
        Pack.longToBigEndian(z1, x, 8);
    }
}
