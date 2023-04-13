package com.android.server.p006am;

import android.app.ActivityManager;
import android.os.UserHandle;
import android.util.SparseArray;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.CompositeRWLock;
import com.android.internal.annotations.GuardedBy;
import java.io.PrintWriter;
import java.util.function.Consumer;
/* renamed from: com.android.server.am.ActiveUids */
/* loaded from: classes.dex */
public final class ActiveUids {
    @CompositeRWLock({"mService", "mProcLock"})
    public final SparseArray<UidRecord> mActiveUids = new SparseArray<>();
    public final boolean mPostChangesToAtm;
    public final ActivityManagerGlobalLock mProcLock;
    public final ActivityManagerService mService;

    public ActiveUids(ActivityManagerService activityManagerService, boolean z) {
        this.mService = activityManagerService;
        this.mProcLock = activityManagerService != null ? activityManagerService.mProcLock : null;
        this.mPostChangesToAtm = z;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void put(int i, UidRecord uidRecord) {
        this.mActiveUids.put(i, uidRecord);
        if (this.mPostChangesToAtm) {
            this.mService.mAtmInternal.onUidActive(i, uidRecord.getCurProcState());
        }
    }

    @GuardedBy({"mService", "mProcLock"})
    public void remove(int i) {
        this.mActiveUids.remove(i);
        if (this.mPostChangesToAtm) {
            this.mService.mAtmInternal.onUidInactive(i);
        }
    }

    @GuardedBy({"mService", "mProcLock"})
    public void clear() {
        this.mActiveUids.clear();
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public UidRecord get(int i) {
        return this.mActiveUids.get(i);
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int size() {
        return this.mActiveUids.size();
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public UidRecord valueAt(int i) {
        return this.mActiveUids.valueAt(i);
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int keyAt(int i) {
        return this.mActiveUids.keyAt(i);
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public boolean dump(final PrintWriter printWriter, String str, int i, String str2, boolean z) {
        boolean z2 = false;
        for (int i2 = 0; i2 < this.mActiveUids.size(); i2++) {
            UidRecord valueAt = this.mActiveUids.valueAt(i2);
            if (str == null || UserHandle.getAppId(valueAt.getUid()) == i) {
                if (!z2) {
                    if (z) {
                        printWriter.println();
                    }
                    printWriter.print("  ");
                    printWriter.println(str2);
                    z2 = true;
                }
                printWriter.print("    UID ");
                UserHandle.formatUid(printWriter, valueAt.getUid());
                printWriter.print(": ");
                printWriter.println(valueAt);
                printWriter.print("      curProcState=");
                printWriter.print(valueAt.getCurProcState());
                printWriter.print(" curCapability=");
                ActivityManager.printCapabilitiesFull(printWriter, valueAt.getCurCapability());
                printWriter.println();
                valueAt.forEachProcess(new Consumer() { // from class: com.android.server.am.ActiveUids$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ActiveUids.lambda$dump$0(printWriter, (ProcessRecord) obj);
                    }
                });
            }
        }
        return z2;
    }

    public static /* synthetic */ void lambda$dump$0(PrintWriter printWriter, ProcessRecord processRecord) {
        printWriter.print("      proc=");
        printWriter.println(processRecord);
    }

    public void dumpProto(ProtoOutputStream protoOutputStream, String str, int i, long j) {
        for (int i2 = 0; i2 < this.mActiveUids.size(); i2++) {
            UidRecord valueAt = this.mActiveUids.valueAt(i2);
            if (str == null || UserHandle.getAppId(valueAt.getUid()) == i) {
                valueAt.dumpDebug(protoOutputStream, j);
            }
        }
    }
}
