package android.util;

import dalvik.annotation.optimization.FastNative;
/* loaded from: classes3.dex */
public class CharsetUtils {
    @FastNative
    public static native String fromModifiedUtf8Bytes(long j, int i, int i2);

    @FastNative
    private static native int toModifiedUtf8Bytes(String str, int i, long j, int i2, int i3);

    public static int toModifiedUtf8Bytes(String src, long dest, int destOff, int destLen) {
        return toModifiedUtf8Bytes(src, src.length(), dest, destOff, destLen);
    }
}
