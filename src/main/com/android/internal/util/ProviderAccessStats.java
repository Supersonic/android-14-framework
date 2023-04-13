package com.android.internal.util;

import android.p008os.SystemClock;
import android.util.SparseBooleanArray;
import android.util.SparseLongArray;
import java.io.PrintWriter;
import java.util.function.Supplier;
/* loaded from: classes3.dex */
public class ProviderAccessStats {
    private final Object mLock = new Object();
    private final long mStartUptime = SystemClock.uptimeMillis();
    private final SparseBooleanArray mAllCallingUids = new SparseBooleanArray();
    private final SparseLongArray mQueryStats = new SparseLongArray(16);
    private final SparseLongArray mBatchStats = new SparseLongArray(0);
    private final SparseLongArray mInsertStats = new SparseLongArray(0);
    private final SparseLongArray mUpdateStats = new SparseLongArray(0);
    private final SparseLongArray mDeleteStats = new SparseLongArray(0);
    private final SparseLongArray mInsertInBatchStats = new SparseLongArray(0);
    private final SparseLongArray mUpdateInBatchStats = new SparseLongArray(0);
    private final SparseLongArray mDeleteInBatchStats = new SparseLongArray(0);
    private final SparseLongArray mOperationDurationMillis = new SparseLongArray(16);
    private final ThreadLocal<PerThreadData> mThreadLocal = ThreadLocal.withInitial(new Supplier() { // from class: com.android.internal.util.ProviderAccessStats$$ExternalSyntheticLambda0
        @Override // java.util.function.Supplier
        public final Object get() {
            return ProviderAccessStats.lambda$new$0();
        }
    });

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class PerThreadData {
        public int nestCount;
        public long startUptimeMillis;

        private PerThreadData() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ PerThreadData lambda$new$0() {
        return new PerThreadData();
    }

    private void incrementStats(int callingUid, SparseLongArray stats) {
        synchronized (this.mLock) {
            stats.put(callingUid, stats.get(callingUid) + 1);
            this.mAllCallingUids.put(callingUid, true);
        }
        PerThreadData data = this.mThreadLocal.get();
        data.nestCount++;
        if (data.nestCount == 1) {
            data.startUptimeMillis = SystemClock.uptimeMillis();
        }
    }

    private void incrementStats(int callingUid, boolean inBatch, SparseLongArray statsNonBatch, SparseLongArray statsInBatch) {
        incrementStats(callingUid, inBatch ? statsInBatch : statsNonBatch);
    }

    public final void incrementInsertStats(int callingUid, boolean inBatch) {
        incrementStats(callingUid, inBatch, this.mInsertStats, this.mInsertInBatchStats);
    }

    public final void incrementUpdateStats(int callingUid, boolean inBatch) {
        incrementStats(callingUid, inBatch, this.mUpdateStats, this.mUpdateInBatchStats);
    }

    public final void incrementDeleteStats(int callingUid, boolean inBatch) {
        incrementStats(callingUid, inBatch, this.mDeleteStats, this.mDeleteInBatchStats);
    }

    public final void incrementQueryStats(int callingUid) {
        incrementStats(callingUid, this.mQueryStats);
    }

    public final void incrementBatchStats(int callingUid) {
        incrementStats(callingUid, this.mBatchStats);
    }

    public void finishOperation(int callingUid) {
        PerThreadData data = this.mThreadLocal.get();
        data.nestCount--;
        if (data.nestCount == 0) {
            long duration = Math.max(1L, SystemClock.uptimeMillis() - data.startUptimeMillis);
            synchronized (this.mLock) {
                SparseLongArray sparseLongArray = this.mOperationDurationMillis;
                sparseLongArray.put(callingUid, sparseLongArray.get(callingUid) + duration);
            }
        }
    }

    public void dump(PrintWriter pw, String prefix) {
        synchronized (this.mLock) {
            pw.print("  Process uptime: ");
            pw.print((SystemClock.uptimeMillis() - this.mStartUptime) / 60000);
            pw.println(" minutes");
            pw.println();
            pw.print(prefix);
            pw.println("Client activities:");
            pw.print(prefix);
            pw.println("  UID        Query  Insert Update Delete   Batch Insert Update Delete          Sec");
            for (int i = 0; i < this.mAllCallingUids.size(); i++) {
                int uid = this.mAllCallingUids.keyAt(i);
                pw.print(prefix);
                pw.println(String.format("  %-9d %6d  %6d %6d %6d  %6d %6d %6d %6d %12.3f", Integer.valueOf(uid), Long.valueOf(this.mQueryStats.get(uid)), Long.valueOf(this.mInsertStats.get(uid)), Long.valueOf(this.mUpdateStats.get(uid)), Long.valueOf(this.mDeleteStats.get(uid)), Long.valueOf(this.mBatchStats.get(uid)), Long.valueOf(this.mInsertInBatchStats.get(uid)), Long.valueOf(this.mUpdateInBatchStats.get(uid)), Long.valueOf(this.mDeleteInBatchStats.get(uid)), Double.valueOf(this.mOperationDurationMillis.get(uid) / 1000.0d)));
            }
            pw.println();
        }
    }
}
