package com.android.server.p006am;

import android.util.ArrayMap;
import android.util.TimeUtils;
import com.android.internal.annotations.GuardedBy;
import java.io.PrintWriter;
import java.util.ArrayList;
/* renamed from: com.android.server.am.ProcessProviderRecord */
/* loaded from: classes.dex */
public final class ProcessProviderRecord {
    public final ProcessRecord mApp;
    public long mLastProviderTime;
    public final ActivityManagerService mService;
    public final ArrayMap<String, ContentProviderRecord> mPubProviders = new ArrayMap<>();
    public final ArrayList<ContentProviderConnection> mConProviders = new ArrayList<>();

    public long getLastProviderTime() {
        return this.mLastProviderTime;
    }

    public void setLastProviderTime(long j) {
        this.mLastProviderTime = j;
    }

    public boolean hasProvider(String str) {
        return this.mPubProviders.containsKey(str);
    }

    public ContentProviderRecord getProvider(String str) {
        return this.mPubProviders.get(str);
    }

    public int numberOfProviders() {
        return this.mPubProviders.size();
    }

    public ContentProviderRecord getProviderAt(int i) {
        return this.mPubProviders.valueAt(i);
    }

    public void installProvider(String str, ContentProviderRecord contentProviderRecord) {
        this.mPubProviders.put(str, contentProviderRecord);
    }

    public void ensureProviderCapacity(int i) {
        this.mPubProviders.ensureCapacity(i);
    }

    public int numberOfProviderConnections() {
        return this.mConProviders.size();
    }

    public ContentProviderConnection getProviderConnectionAt(int i) {
        return this.mConProviders.get(i);
    }

    public void addProviderConnection(ContentProviderConnection contentProviderConnection) {
        this.mConProviders.add(contentProviderConnection);
    }

    public boolean removeProviderConnection(ContentProviderConnection contentProviderConnection) {
        return this.mConProviders.remove(contentProviderConnection);
    }

    public ProcessProviderRecord(ProcessRecord processRecord) {
        this.mApp = processRecord;
        this.mService = processRecord.mService;
    }

    @GuardedBy({"mService"})
    public boolean onCleanupApplicationRecordLocked(boolean z) {
        boolean z2 = false;
        for (int size = this.mPubProviders.size() - 1; size >= 0; size--) {
            ContentProviderRecord valueAt = this.mPubProviders.valueAt(size);
            ProcessRecord processRecord = valueAt.proc;
            ProcessRecord processRecord2 = this.mApp;
            if (processRecord == processRecord2) {
                boolean z3 = processRecord2.mErrorState.isBad() || !z;
                boolean removeDyingProviderLocked = this.mService.mCpHelper.removeDyingProviderLocked(this.mApp, valueAt, z3);
                if (!z3 && removeDyingProviderLocked && valueAt.hasConnectionOrHandle()) {
                    z2 = true;
                }
                valueAt.provider = null;
                valueAt.setProcess(null);
            }
        }
        this.mPubProviders.clear();
        if (this.mService.mCpHelper.cleanupAppInLaunchingProvidersLocked(this.mApp, false)) {
            this.mService.mProcessList.noteProcessDiedLocked(this.mApp);
            z2 = true;
        }
        if (!this.mConProviders.isEmpty()) {
            for (int size2 = this.mConProviders.size() - 1; size2 >= 0; size2--) {
                ContentProviderConnection contentProviderConnection = this.mConProviders.get(size2);
                contentProviderConnection.provider.connections.remove(contentProviderConnection);
                ActivityManagerService activityManagerService = this.mService;
                ProcessRecord processRecord3 = this.mApp;
                int i = processRecord3.uid;
                String str = processRecord3.processName;
                ContentProviderRecord contentProviderRecord = contentProviderConnection.provider;
                activityManagerService.stopAssociationLocked(i, str, contentProviderRecord.uid, contentProviderRecord.appInfo.longVersionCode, contentProviderRecord.name, contentProviderRecord.info.processName);
            }
            this.mConProviders.clear();
        }
        return z2;
    }

    public void dump(PrintWriter printWriter, String str, long j) {
        if (this.mLastProviderTime > 0) {
            printWriter.print(str);
            printWriter.print("lastProviderTime=");
            TimeUtils.formatDuration(this.mLastProviderTime, j, printWriter);
            printWriter.println();
        }
        if (this.mPubProviders.size() > 0) {
            printWriter.print(str);
            printWriter.println("Published Providers:");
            int size = this.mPubProviders.size();
            for (int i = 0; i < size; i++) {
                printWriter.print(str);
                printWriter.print("  - ");
                printWriter.println(this.mPubProviders.keyAt(i));
                printWriter.print(str);
                printWriter.print("    -> ");
                printWriter.println(this.mPubProviders.valueAt(i));
            }
        }
        if (this.mConProviders.size() > 0) {
            printWriter.print(str);
            printWriter.println("Connected Providers:");
            int size2 = this.mConProviders.size();
            for (int i2 = 0; i2 < size2; i2++) {
                printWriter.print(str);
                printWriter.print("  - ");
                printWriter.println(this.mConProviders.get(i2).toShortString());
            }
        }
    }
}
