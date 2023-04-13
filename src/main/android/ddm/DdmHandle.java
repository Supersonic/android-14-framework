package android.ddm;

import java.nio.ByteBuffer;
import org.apache.harmony.dalvik.ddmc.ChunkHandler;
/* loaded from: classes.dex */
public abstract class DdmHandle extends ChunkHandler {
    public static String getString(ByteBuffer buf, int len) {
        char[] data = new char[len];
        for (int i = 0; i < len; i++) {
            data[i] = buf.getChar();
        }
        return new String(data);
    }

    public static void putString(ByteBuffer buf, String str) {
        int len = str.length();
        for (int i = 0; i < len; i++) {
            buf.putChar(str.charAt(i));
        }
    }
}
