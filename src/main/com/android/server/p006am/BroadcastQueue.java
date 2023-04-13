package com.android.server.p006am;

import android.content.ContentResolver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Trace;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.server.DropBoxManagerInternal;
import com.android.server.LocalServices;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.Set;
/* renamed from: com.android.server.am.BroadcastQueue */
/* loaded from: classes.dex */
public abstract class BroadcastQueue {
    public final Handler mHandler;
    public final BroadcastHistory mHistory;
    public final String mQueueName;
    public final ActivityManagerService mService;
    public final BroadcastSkipPolicy mSkipPolicy;

    @GuardedBy({"mService"})
    public abstract void backgroundServicesFinishedLocked(int i);

    @GuardedBy({"mService"})
    public abstract boolean cleanupDisabledPackageReceiversLocked(String str, Set<String> set, int i);

    @GuardedBy({"mService"})
    public abstract String describeStateLocked();

    @GuardedBy({"mService"})
    public abstract void dumpDebug(ProtoOutputStream protoOutputStream, long j);

    @GuardedBy({"mService"})
    public abstract boolean dumpLocked(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, int i, boolean z, boolean z2, boolean z3, String str, boolean z4);

    @GuardedBy({"mService"})
    public abstract void enqueueBroadcastLocked(BroadcastRecord broadcastRecord);

    @GuardedBy({"mService"})
    public abstract boolean finishReceiverLocked(ProcessRecord processRecord, int i, String str, Bundle bundle, boolean z, boolean z2);

    public void forceDelayBroadcastDelivery(String str, long j) {
    }

    @GuardedBy({"mService"})
    public abstract int getPreferredSchedulingGroupLocked(ProcessRecord processRecord);

    public abstract boolean isDelayBehindServices();

    @GuardedBy({"mService"})
    public abstract boolean isIdleLocked();

    @GuardedBy({"mService"})
    public abstract boolean onApplicationAttachedLocked(ProcessRecord processRecord);

    @GuardedBy({"mService"})
    public abstract void onApplicationCleanupLocked(ProcessRecord processRecord);

    @GuardedBy({"mService"})
    public abstract void onApplicationProblemLocked(ProcessRecord processRecord);

    @GuardedBy({"mService"})
    public abstract void onApplicationTimeoutLocked(ProcessRecord processRecord);

    public abstract void start(ContentResolver contentResolver);

    public abstract void waitForBarrier(PrintWriter printWriter);

    public abstract void waitForIdle(PrintWriter printWriter);

    public BroadcastQueue(ActivityManagerService activityManagerService, Handler handler, String str, BroadcastSkipPolicy broadcastSkipPolicy, BroadcastHistory broadcastHistory) {
        Objects.requireNonNull(activityManagerService);
        this.mService = activityManagerService;
        Objects.requireNonNull(handler);
        this.mHandler = handler;
        Objects.requireNonNull(str);
        this.mQueueName = str;
        Objects.requireNonNull(broadcastSkipPolicy);
        this.mSkipPolicy = broadcastSkipPolicy;
        Objects.requireNonNull(broadcastHistory);
        this.mHistory = broadcastHistory;
    }

    public static void logw(String str) {
        Slog.w("BroadcastQueue", str);
    }

    public static void logv(String str) {
        Slog.v("BroadcastQueue", str);
    }

    public static void logv(String str, PrintWriter printWriter) {
        logv(str);
        if (printWriter != null) {
            printWriter.println(str);
            printWriter.flush();
        }
    }

    public static void checkState(boolean z, String str) {
        if (!z) {
            throw new IllegalStateException(str);
        }
    }

    public static int traceBegin(String str) {
        int hashCode = str.hashCode();
        Trace.asyncTraceForTrackBegin(64L, "BroadcastQueue", str, hashCode);
        return hashCode;
    }

    public static void traceEnd(int i) {
        Trace.asyncTraceForTrackEnd(64L, "BroadcastQueue", i);
    }

    public String toString() {
        return this.mQueueName;
    }

    public void dumpToDropBoxLocked(final String str) {
        ((DropBoxManagerInternal) LocalServices.getService(DropBoxManagerInternal.class)).addEntry("broadcast_queue_dump", new DropBoxManagerInternal.EntrySource() { // from class: com.android.server.am.BroadcastQueue$$ExternalSyntheticLambda0
            @Override // com.android.server.DropBoxManagerInternal.EntrySource
            public final void writeTo(FileDescriptor fileDescriptor) {
                BroadcastQueue.this.lambda$dumpToDropBoxLocked$0(str, fileDescriptor);
            }
        }, 2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$dumpToDropBoxLocked$0(String str, FileDescriptor fileDescriptor) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(fileDescriptor);
        try {
            PrintWriter printWriter = new PrintWriter(fileOutputStream);
            printWriter.print("Message: ");
            printWriter.println(str);
            dumpLocked(fileDescriptor, printWriter, null, 0, false, false, false, null, false);
            printWriter.flush();
            printWriter.close();
            fileOutputStream.close();
        } catch (Throwable th) {
            try {
                fileOutputStream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }
}
