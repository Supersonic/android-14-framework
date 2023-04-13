package com.android.server.net.watchlist;

import com.android.internal.util.HexDump;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/* loaded from: classes2.dex */
public class HarmfulCrcs {
    public final Set<Integer> mCrcSet;

    public HarmfulCrcs(List<byte[]> list) {
        HashSet hashSet = new HashSet();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            byte[] bArr = list.get(i);
            if (bArr.length <= 4) {
                int i2 = 0;
                for (byte b : bArr) {
                    i2 = (i2 << 8) | (b & 255);
                }
                hashSet.add(Integer.valueOf(i2));
            }
        }
        this.mCrcSet = Collections.unmodifiableSet(hashSet);
    }

    public boolean contains(int i) {
        return this.mCrcSet.contains(Integer.valueOf(i));
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        for (Integer num : this.mCrcSet) {
            printWriter.println(HexDump.toHexString(num.intValue()));
        }
        printWriter.println("");
    }
}
