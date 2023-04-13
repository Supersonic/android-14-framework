package com.android.internal.org.bouncycastle.crypto.modes.gcm;

import com.android.internal.org.bouncycastle.util.Arrays;
/* loaded from: classes4.dex */
public class BasicGCMExponentiator implements GCMExponentiator {

    /* renamed from: x */
    private long[] f763x;

    @Override // com.android.internal.org.bouncycastle.crypto.modes.gcm.GCMExponentiator
    public void init(byte[] x) {
        this.f763x = GCMUtil.asLongs(x);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.modes.gcm.GCMExponentiator
    public void exponentiateX(long pow, byte[] output) {
        long[] y = GCMUtil.oneAsLongs();
        if (pow > 0) {
            long[] powX = Arrays.clone(this.f763x);
            do {
                if ((1 & pow) != 0) {
                    GCMUtil.multiply(y, powX);
                }
                GCMUtil.square(powX, powX);
                pow >>>= 1;
            } while (pow > 0);
            GCMUtil.asBytes(y, output);
        }
        GCMUtil.asBytes(y, output);
    }
}
