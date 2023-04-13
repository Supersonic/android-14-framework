package com.android.server.integrity.parser;

import android.content.integrity.IntegrityUtils;
import com.android.server.integrity.model.BitInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class BinaryFileOperations {
    public static String getStringValue(BitInputStream bitInputStream) throws IOException {
        return getStringValue(bitInputStream, bitInputStream.getNext(8), bitInputStream.getNext(1) == 1);
    }

    public static String getStringValue(BitInputStream bitInputStream, int i, boolean z) throws IOException {
        if (!z) {
            StringBuilder sb = new StringBuilder();
            while (true) {
                int i2 = i - 1;
                if (i > 0) {
                    sb.append((char) bitInputStream.getNext(8));
                    i = i2;
                } else {
                    return sb.toString();
                }
            }
        } else {
            ByteBuffer allocate = ByteBuffer.allocate(i);
            while (true) {
                int i3 = i - 1;
                if (i > 0) {
                    allocate.put((byte) (bitInputStream.getNext(8) & 255));
                    i = i3;
                } else {
                    return IntegrityUtils.getHexDigest(allocate.array());
                }
            }
        }
    }

    public static int getIntValue(BitInputStream bitInputStream) throws IOException {
        return bitInputStream.getNext(32);
    }

    public static boolean getBooleanValue(BitInputStream bitInputStream) throws IOException {
        return bitInputStream.getNext(1) == 1;
    }
}
