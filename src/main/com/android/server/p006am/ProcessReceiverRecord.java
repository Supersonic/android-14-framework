package com.android.server.p006am;

import android.util.ArraySet;
import com.android.internal.annotations.GuardedBy;
import java.io.PrintWriter;
/* renamed from: com.android.server.am.ProcessReceiverRecord */
/* loaded from: classes.dex */
public final class ProcessReceiverRecord {
    public final ProcessRecord mApp;
    public int mCurReceiversSize;
    public final ActivityManagerService mService;
    public final ArraySet<BroadcastRecord> mCurReceivers = new ArraySet<>();
    public final ArraySet<ReceiverList> mReceivers = new ArraySet<>();

    public int numberOfCurReceivers() {
        return this.mCurReceiversSize;
    }

    public void incrementCurReceivers() {
        this.mCurReceiversSize++;
    }

    public void decrementCurReceivers() {
        this.mCurReceiversSize--;
    }

    @Deprecated
    public boolean hasCurReceiver(BroadcastRecord broadcastRecord) {
        return this.mCurReceivers.contains(broadcastRecord);
    }

    @Deprecated
    public void addCurReceiver(BroadcastRecord broadcastRecord) {
        this.mCurReceivers.add(broadcastRecord);
        this.mCurReceiversSize = this.mCurReceivers.size();
    }

    @Deprecated
    public void removeCurReceiver(BroadcastRecord broadcastRecord) {
        this.mCurReceivers.remove(broadcastRecord);
        this.mCurReceiversSize = this.mCurReceivers.size();
    }

    public int numberOfReceivers() {
        return this.mReceivers.size();
    }

    public void addReceiver(ReceiverList receiverList) {
        this.mReceivers.add(receiverList);
    }

    public void removeReceiver(ReceiverList receiverList) {
        this.mReceivers.remove(receiverList);
    }

    public ProcessReceiverRecord(ProcessRecord processRecord) {
        this.mApp = processRecord;
        this.mService = processRecord.mService;
    }

    @GuardedBy({"mService"})
    public void onCleanupApplicationRecordLocked() {
        for (int size = this.mReceivers.size() - 1; size >= 0; size--) {
            this.mService.removeReceiverLocked(this.mReceivers.valueAt(size));
        }
        this.mReceivers.clear();
    }

    public void dump(PrintWriter printWriter, String str, long j) {
        if (!this.mCurReceivers.isEmpty()) {
            printWriter.print(str);
            printWriter.println("Current mReceivers:");
            int size = this.mCurReceivers.size();
            for (int i = 0; i < size; i++) {
                printWriter.print(str);
                printWriter.print("  - ");
                printWriter.println(this.mCurReceivers.valueAt(i));
            }
        }
        if (this.mReceivers.size() > 0) {
            printWriter.print(str);
            printWriter.println("mReceivers:");
            int size2 = this.mReceivers.size();
            for (int i2 = 0; i2 < size2; i2++) {
                printWriter.print(str);
                printWriter.print("  - ");
                printWriter.println(this.mReceivers.valueAt(i2));
            }
        }
    }
}
