package android.graphics;

import com.android.internal.util.ArrayUtils;
/* loaded from: classes.dex */
public class TemporaryBuffer {
    private static char[] sTemp = null;

    public static char[] obtain(int len) {
        char[] buf;
        synchronized (TemporaryBuffer.class) {
            buf = sTemp;
            sTemp = null;
        }
        if (buf == null || buf.length < len) {
            return ArrayUtils.newUnpaddedCharArray(len);
        }
        return buf;
    }

    public static void recycle(char[] temp) {
        if (temp.length > 1000) {
            return;
        }
        synchronized (TemporaryBuffer.class) {
            sTemp = temp;
        }
    }
}
