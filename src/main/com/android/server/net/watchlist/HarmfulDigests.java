package com.android.server.net.watchlist;

import com.android.internal.util.HexDump;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/* loaded from: classes2.dex */
public class HarmfulDigests {
    public final Set<String> mDigestSet;

    public HarmfulDigests(List<byte[]> list) {
        HashSet hashSet = new HashSet();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            hashSet.add(HexDump.toHexString(list.get(i)));
        }
        this.mDigestSet = Collections.unmodifiableSet(hashSet);
    }

    public boolean contains(byte[] bArr) {
        return this.mDigestSet.contains(HexDump.toHexString(bArr));
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        for (String str : this.mDigestSet) {
            printWriter.println(str);
        }
        printWriter.println("");
    }
}
