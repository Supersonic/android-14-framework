package com.android.server.tare;

import android.app.ActivityManager;
import android.app.IUidObserver;
import android.os.RemoteException;
import android.util.IndentingPrintWriter;
import android.util.Slog;
import android.util.SparseArrayMap;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
/* loaded from: classes2.dex */
public class ProcessStateModifier extends Modifier {
    public static final String TAG = "TARE-" + ProcessStateModifier.class.getSimpleName();
    public final InternalResourceService mIrs;
    public final Object mLock = new Object();
    public final SparseArrayMap<String, Integer> mPackageToUidCache = new SparseArrayMap<>();
    @GuardedBy({"mLock"})
    public final SparseIntArray mUidProcStateBucketCache = new SparseIntArray();
    public final IUidObserver mUidObserver = new IUidObserver.Stub() { // from class: com.android.server.tare.ProcessStateModifier.1
        public void onUidActive(int i) {
        }

        public void onUidCachedChanged(int i, boolean z) {
        }

        public void onUidIdle(int i, boolean z) {
        }

        public void onUidProcAdjChanged(int i) {
        }

        public void onUidStateChanged(int i, int i2, long j, int i3) {
            int procStateBucket = ProcessStateModifier.this.getProcStateBucket(i2);
            synchronized (ProcessStateModifier.this.mLock) {
                if (ProcessStateModifier.this.mUidProcStateBucketCache.get(i) != procStateBucket) {
                    ProcessStateModifier.this.mUidProcStateBucketCache.put(i, procStateBucket);
                }
                ProcessStateModifier.this.notifyStateChangedLocked(i);
            }
        }

        public void onUidGone(int i, boolean z) {
            synchronized (ProcessStateModifier.this.mLock) {
                if (ProcessStateModifier.this.mUidProcStateBucketCache.indexOfKey(i) < 0) {
                    String str = ProcessStateModifier.TAG;
                    Slog.e(str, "UID " + i + " marked gone but wasn't in cache.");
                    return;
                }
                ProcessStateModifier.this.mUidProcStateBucketCache.delete(i);
                ProcessStateModifier.this.notifyStateChangedLocked(i);
            }
        }
    };

    public final int getProcStateBucket(int i) {
        if (i <= 2) {
            return 1;
        }
        if (i <= 4) {
            return 2;
        }
        return i <= 5 ? 3 : 4;
    }

    public ProcessStateModifier(InternalResourceService internalResourceService) {
        this.mIrs = internalResourceService;
    }

    @Override // com.android.server.tare.Modifier
    @GuardedBy({"mLock"})
    public void setup() {
        try {
            ActivityManager.getService().registerUidObserver(this.mUidObserver, 3, -1, (String) null);
        } catch (RemoteException unused) {
        }
    }

    @Override // com.android.server.tare.Modifier
    @GuardedBy({"mLock"})
    public void tearDown() {
        try {
            ActivityManager.getService().unregisterUidObserver(this.mUidObserver);
        } catch (RemoteException unused) {
        }
        this.mPackageToUidCache.clear();
        this.mUidProcStateBucketCache.clear();
    }

    public long getModifiedPrice(int i, String str, long j, long j2) {
        int i2;
        synchronized (this.mLock) {
            i2 = this.mUidProcStateBucketCache.get(this.mIrs.getUid(i, str), 0);
        }
        if (i2 != 1) {
            if (i2 != 2) {
                return (i2 == 3 && j2 > j) ? (long) (j + ((j2 - j) * 0.5d)) : j2;
            }
            return Math.min(j, j2);
        }
        return 0L;
    }

    @Override // com.android.server.tare.Modifier
    @GuardedBy({"mLock"})
    public void dump(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.print("Proc state bucket cache = ");
        indentingPrintWriter.println(this.mUidProcStateBucketCache);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyStateChangedLocked$0(int i) {
        this.mIrs.onUidStateChanged(i);
    }

    @GuardedBy({"mLock"})
    public final void notifyStateChangedLocked(final int i) {
        TareHandlerThread.getHandler().post(new Runnable() { // from class: com.android.server.tare.ProcessStateModifier$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ProcessStateModifier.this.lambda$notifyStateChangedLocked$0(i);
            }
        });
    }
}
