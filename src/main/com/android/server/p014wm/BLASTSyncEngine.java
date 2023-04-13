package com.android.server.p014wm;

import android.os.Trace;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseArray;
import android.view.SurfaceControl;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.server.p014wm.BLASTSyncEngine;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executor;
/* renamed from: com.android.server.wm.BLASTSyncEngine */
/* loaded from: classes2.dex */
public class BLASTSyncEngine {
    public final WindowManagerService mWm;
    public int mNextSyncId = 0;
    public final SparseArray<SyncGroup> mActiveSyncs = new SparseArray<>();
    public final ArrayList<PendingSyncSet> mPendingSyncSets = new ArrayList<>();

    /* renamed from: com.android.server.wm.BLASTSyncEngine$TransactionReadyListener */
    /* loaded from: classes2.dex */
    public interface TransactionReadyListener {
        void onTransactionReady(int i, SurfaceControl.Transaction transaction);
    }

    /* renamed from: com.android.server.wm.BLASTSyncEngine$PendingSyncSet */
    /* loaded from: classes2.dex */
    public static class PendingSyncSet {
        public Runnable mApplySync;
        public Runnable mStartSync;

        public PendingSyncSet() {
        }
    }

    /* renamed from: com.android.server.wm.BLASTSyncEngine$SyncGroup */
    /* loaded from: classes2.dex */
    public class SyncGroup {
        public final TransactionReadyListener mListener;
        public final Runnable mOnTimeout;
        public SurfaceControl.Transaction mOrphanTransaction;
        public boolean mReady;
        public final ArraySet<WindowContainer> mRootMembers;
        public final int mSyncId;
        public final int mSyncMethod;
        public String mTraceName;

        public SyncGroup(TransactionReadyListener transactionReadyListener, int i, String str, int i2) {
            this.mReady = false;
            this.mRootMembers = new ArraySet<>();
            this.mOrphanTransaction = null;
            this.mSyncId = i;
            this.mSyncMethod = i2;
            this.mListener = transactionReadyListener;
            this.mOnTimeout = new Runnable() { // from class: com.android.server.wm.BLASTSyncEngine$SyncGroup$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    BLASTSyncEngine.SyncGroup.this.lambda$new$0();
                }
            };
            if (Trace.isTagEnabled(32L)) {
                String str2 = str + "SyncGroupReady";
                this.mTraceName = str2;
                Trace.asyncTraceBegin(32L, str2, i);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0() {
            Slog.w("BLASTSyncEngine", "Sync group " + this.mSyncId + " timeout");
            synchronized (BLASTSyncEngine.this.mWm.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    onTimeout();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public SurfaceControl.Transaction getOrphanTransaction() {
            if (this.mOrphanTransaction == null) {
                this.mOrphanTransaction = BLASTSyncEngine.this.mWm.mTransactionFactory.get();
            }
            return this.mOrphanTransaction;
        }

        public final void onSurfacePlacement() {
            if (this.mReady) {
                if (ProtoLogCache.WM_DEBUG_SYNC_ENGINE_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_SYNC_ENGINE, 966569777, 1, (String) null, new Object[]{Long.valueOf(this.mSyncId), String.valueOf(this.mRootMembers)});
                }
                for (int size = this.mRootMembers.size() - 1; size >= 0; size--) {
                    WindowContainer valueAt = this.mRootMembers.valueAt(size);
                    if (!valueAt.isSyncFinished()) {
                        if (ProtoLogCache.WM_DEBUG_SYNC_ENGINE_enabled) {
                            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_SYNC_ENGINE, -230587670, 1, (String) null, new Object[]{Long.valueOf(this.mSyncId), String.valueOf(valueAt)});
                            return;
                        }
                        return;
                    }
                }
                finishNow();
            }
        }

        public final void finishNow() {
            String str = this.mTraceName;
            if (str != null) {
                Trace.asyncTraceEnd(32L, str, this.mSyncId);
            }
            if (ProtoLogCache.WM_DEBUG_SYNC_ENGINE_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_SYNC_ENGINE, -1905191109, 1, (String) null, new Object[]{Long.valueOf(this.mSyncId)});
            }
            SurfaceControl.Transaction transaction = BLASTSyncEngine.this.mWm.mTransactionFactory.get();
            SurfaceControl.Transaction transaction2 = this.mOrphanTransaction;
            if (transaction2 != null) {
                transaction.merge(transaction2);
            }
            Iterator<WindowContainer> it = this.mRootMembers.iterator();
            while (it.hasNext()) {
                it.next().finishSync(transaction, false);
            }
            ArraySet<WindowContainer> arraySet = new ArraySet<>();
            Iterator<WindowContainer> it2 = this.mRootMembers.iterator();
            while (it2.hasNext()) {
                it2.next().waitForSyncTransactionCommit(arraySet);
            }
            final C1CommitCallback c1CommitCallback = new C1CommitCallback(arraySet);
            transaction.addTransactionCommittedListener(new Executor() { // from class: com.android.server.wm.BLASTSyncEngine$SyncGroup$$ExternalSyntheticLambda0
                @Override // java.util.concurrent.Executor
                public final void execute(Runnable runnable) {
                    runnable.run();
                }
            }, new SurfaceControl.TransactionCommittedListener() { // from class: com.android.server.wm.BLASTSyncEngine$SyncGroup$$ExternalSyntheticLambda1
                public final void onTransactionCommitted() {
                    BLASTSyncEngine.SyncGroup.C1CommitCallback.this.onCommitted();
                }
            });
            BLASTSyncEngine.this.mWm.f1164mH.postDelayed(c1CommitCallback, 5000L);
            Trace.traceBegin(32L, "onTransactionReady");
            this.mListener.onTransactionReady(this.mSyncId, transaction);
            Trace.traceEnd(32L);
            BLASTSyncEngine.this.mActiveSyncs.remove(this.mSyncId);
            BLASTSyncEngine.this.mWm.f1164mH.removeCallbacks(this.mOnTimeout);
            if (BLASTSyncEngine.this.mActiveSyncs.size() != 0 || BLASTSyncEngine.this.mPendingSyncSets.isEmpty()) {
                return;
            }
            if (ProtoLogCache.WM_DEBUG_SYNC_ENGINE_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_SYNC_ENGINE, 1730300180, 0, (String) null, (Object[]) null);
            }
            final PendingSyncSet pendingSyncSet = (PendingSyncSet) BLASTSyncEngine.this.mPendingSyncSets.remove(0);
            pendingSyncSet.mStartSync.run();
            if (BLASTSyncEngine.this.mActiveSyncs.size() == 0) {
                throw new IllegalStateException("Pending Sync Set didn't start a sync.");
            }
            BLASTSyncEngine.this.mWm.f1164mH.post(new Runnable() { // from class: com.android.server.wm.BLASTSyncEngine$SyncGroup$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    BLASTSyncEngine.SyncGroup.this.lambda$finishNow$2(pendingSyncSet);
                }
            });
        }

        /* renamed from: com.android.server.wm.BLASTSyncEngine$SyncGroup$1CommitCallback  reason: invalid class name */
        /* loaded from: classes2.dex */
        public class C1CommitCallback implements Runnable {
            public boolean ran = false;
            public final /* synthetic */ ArraySet val$wcAwaitingCommit;

            public C1CommitCallback(ArraySet arraySet) {
                this.val$wcAwaitingCommit = arraySet;
            }

            public void onCommitted() {
                synchronized (BLASTSyncEngine.this.mWm.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        if (this.ran) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return;
                        }
                        BLASTSyncEngine.this.mWm.f1164mH.removeCallbacks(this);
                        this.ran = true;
                        SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
                        Iterator it = this.val$wcAwaitingCommit.iterator();
                        while (it.hasNext()) {
                            ((WindowContainer) it.next()).onSyncTransactionCommitted(transaction);
                        }
                        transaction.apply();
                        this.val$wcAwaitingCommit.clear();
                        WindowManagerService.resetPriorityAfterLockedSection();
                    } catch (Throwable th) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }

            @Override // java.lang.Runnable
            public void run() {
                Trace.traceBegin(32L, "onTransactionCommitTimeout");
                Slog.e("BLASTSyncEngine", "WM sent Transaction to organized, but never received commit callback. Application ANR likely to follow.");
                Trace.traceEnd(32L);
                onCommitted();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$finishNow$2(PendingSyncSet pendingSyncSet) {
            synchronized (BLASTSyncEngine.this.mWm.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    pendingSyncSet.mApplySync.run();
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public final void setReady(boolean z) {
            if (this.mReady == z) {
                return;
            }
            if (ProtoLogCache.WM_DEBUG_SYNC_ENGINE_enabled) {
                ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_SYNC_ENGINE, 1689989893, 1, (String) null, new Object[]{Long.valueOf(this.mSyncId)});
            }
            this.mReady = z;
            if (z) {
                BLASTSyncEngine.this.mWm.mWindowPlacerLocked.requestTraversal();
            }
        }

        public final void addToSync(WindowContainer windowContainer) {
            if (this.mRootMembers.add(windowContainer)) {
                if (ProtoLogCache.WM_DEBUG_SYNC_ENGINE_enabled) {
                    ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_SYNC_ENGINE, -1973119651, 1, (String) null, new Object[]{Long.valueOf(this.mSyncId), String.valueOf(windowContainer)});
                }
                windowContainer.setSyncGroup(this);
                windowContainer.prepareSync();
                if (this.mReady) {
                    BLASTSyncEngine.this.mWm.mWindowPlacerLocked.requestTraversal();
                }
            }
        }

        public void onCancelSync(WindowContainer windowContainer) {
            this.mRootMembers.remove(windowContainer);
        }

        public final void onTimeout() {
            if (BLASTSyncEngine.this.mActiveSyncs.contains(this.mSyncId)) {
                boolean z = true;
                for (int size = this.mRootMembers.size() - 1; size >= 0; size--) {
                    WindowContainer valueAt = this.mRootMembers.valueAt(size);
                    if (!valueAt.isSyncFinished()) {
                        Slog.i("BLASTSyncEngine", "Unfinished container: " + valueAt);
                        z = false;
                    }
                }
                if (z && !this.mReady) {
                    Slog.w("BLASTSyncEngine", "Sync group " + this.mSyncId + " timed-out because not ready. If you see this, please file a bug.");
                }
                finishNow();
            }
        }
    }

    public BLASTSyncEngine(WindowManagerService windowManagerService) {
        this.mWm = windowManagerService;
    }

    public SyncGroup prepareSyncSet(TransactionReadyListener transactionReadyListener, String str, int i) {
        int i2 = this.mNextSyncId;
        this.mNextSyncId = i2 + 1;
        return new SyncGroup(transactionReadyListener, i2, str, i);
    }

    public int startSyncSet(TransactionReadyListener transactionReadyListener, long j, String str, int i) {
        SyncGroup prepareSyncSet = prepareSyncSet(transactionReadyListener, str, i);
        startSyncSet(prepareSyncSet, j);
        return prepareSyncSet.mSyncId;
    }

    public void startSyncSet(SyncGroup syncGroup) {
        startSyncSet(syncGroup, 5000L);
    }

    public void startSyncSet(SyncGroup syncGroup, long j) {
        if (this.mActiveSyncs.size() != 0 && ProtoLogCache.WM_DEBUG_SYNC_ENGINE_enabled) {
            ProtoLogImpl.w(ProtoLogGroup.WM_DEBUG_SYNC_ENGINE, 800698875, 1, (String) null, new Object[]{Long.valueOf(syncGroup.mSyncId)});
        }
        this.mActiveSyncs.put(syncGroup.mSyncId, syncGroup);
        if (ProtoLogCache.WM_DEBUG_SYNC_ENGINE_enabled) {
            ProtoLogImpl.v(ProtoLogGroup.WM_DEBUG_SYNC_ENGINE, 550717438, 1, (String) null, new Object[]{Long.valueOf(syncGroup.mSyncId), String.valueOf(syncGroup.mListener)});
        }
        scheduleTimeout(syncGroup, j);
    }

    public SyncGroup getSyncSet(int i) {
        return this.mActiveSyncs.get(i);
    }

    public boolean hasActiveSync() {
        return this.mActiveSyncs.size() != 0;
    }

    @VisibleForTesting
    public void scheduleTimeout(SyncGroup syncGroup, long j) {
        this.mWm.f1164mH.postDelayed(syncGroup.mOnTimeout, j);
    }

    public void addToSyncSet(int i, WindowContainer windowContainer) {
        getSyncGroup(i).addToSync(windowContainer);
    }

    public void setReady(int i, boolean z) {
        getSyncGroup(i).setReady(z);
    }

    public void setReady(int i) {
        setReady(i, true);
    }

    public void abort(int i) {
        getSyncGroup(i).finishNow();
    }

    public final SyncGroup getSyncGroup(int i) {
        SyncGroup syncGroup = this.mActiveSyncs.get(i);
        if (syncGroup != null) {
            return syncGroup;
        }
        throw new IllegalStateException("SyncGroup is not started yet id=" + i);
    }

    public void onSurfacePlacement() {
        for (int size = this.mActiveSyncs.size() - 1; size >= 0; size--) {
            this.mActiveSyncs.valueAt(size).onSurfacePlacement();
        }
    }

    public void queueSyncSet(Runnable runnable, Runnable runnable2) {
        PendingSyncSet pendingSyncSet = new PendingSyncSet();
        pendingSyncSet.mStartSync = runnable;
        pendingSyncSet.mApplySync = runnable2;
        this.mPendingSyncSets.add(pendingSyncSet);
    }

    public boolean hasPendingSyncSets() {
        return !this.mPendingSyncSets.isEmpty();
    }
}
