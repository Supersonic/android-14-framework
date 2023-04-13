package com.android.server.p014wm;

import android.util.SparseIntArray;
import com.android.internal.util.jobs.XmlUtils;
import java.io.PrintWriter;
/* renamed from: com.android.server.wm.MirrorActiveUids */
/* loaded from: classes2.dex */
public class MirrorActiveUids {
    public final SparseIntArray mUidStates = new SparseIntArray();
    public final SparseIntArray mNumNonAppVisibleWindowMap = new SparseIntArray();

    public synchronized void onUidActive(int i, int i2) {
        this.mUidStates.put(i, i2);
    }

    public synchronized void onUidInactive(int i) {
        this.mUidStates.delete(i);
    }

    public synchronized void onUidProcStateChanged(int i, int i2) {
        int indexOfKey = this.mUidStates.indexOfKey(i);
        if (indexOfKey >= 0) {
            this.mUidStates.setValueAt(indexOfKey, i2);
        }
    }

    public synchronized int getUidState(int i) {
        return this.mUidStates.get(i, 20);
    }

    public synchronized void onNonAppSurfaceVisibilityChanged(int i, boolean z) {
        int indexOfKey = this.mNumNonAppVisibleWindowMap.indexOfKey(i);
        int i2 = 1;
        if (indexOfKey >= 0) {
            int valueAt = this.mNumNonAppVisibleWindowMap.valueAt(indexOfKey);
            if (!z) {
                i2 = -1;
            }
            int i3 = valueAt + i2;
            if (i3 > 0) {
                this.mNumNonAppVisibleWindowMap.setValueAt(indexOfKey, i3);
            } else {
                this.mNumNonAppVisibleWindowMap.removeAt(indexOfKey);
            }
        } else if (z) {
            this.mNumNonAppVisibleWindowMap.append(i, 1);
        }
    }

    public synchronized boolean hasNonAppVisibleWindow(int i) {
        return this.mNumNonAppVisibleWindowMap.get(i) > 0;
    }

    public synchronized void dump(PrintWriter printWriter, String str) {
        printWriter.print(str + "NumNonAppVisibleWindowUidMap:[");
        for (int size = this.mNumNonAppVisibleWindowMap.size() + (-1); size >= 0; size += -1) {
            printWriter.print(" " + this.mNumNonAppVisibleWindowMap.keyAt(size) + XmlUtils.STRING_ARRAY_SEPARATOR + this.mNumNonAppVisibleWindowMap.valueAt(size));
        }
        printWriter.println("]");
    }
}
