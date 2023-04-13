package com.android.internal.org.bouncycastle.crypto.digests;
/* loaded from: classes4.dex */
public class XofUtils {
    public static byte[] leftEncode(long strLen) {
        byte n = 1;
        long v = strLen;
        while (true) {
            long j = v >> 8;
            v = j;
            if (j == 0) {
                break;
            }
            n = (byte) (n + 1);
        }
        byte[] b = new byte[n + 1];
        b[0] = n;
        for (int i = 1; i <= n; i++) {
            b[i] = (byte) (strLen >> ((n - i) * 8));
        }
        return b;
    }

    public static byte[] rightEncode(long strLen) {
        byte n = 1;
        long v = strLen;
        while (true) {
            long j = v >> 8;
            v = j;
            if (j == 0) {
                break;
            }
            n = (byte) (n + 1);
        }
        byte[] b = new byte[n + 1];
        b[n] = n;
        for (int i = 0; i < n; i++) {
            b[i] = (byte) (strLen >> (((n - i) - 1) * 8));
        }
        return b;
    }
}
