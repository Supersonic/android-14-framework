package com.android.server.p014wm;

import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.window.AddToSurfaceSyncGroupResult;
import android.window.ISurfaceSyncGroupCompletedListener;
import android.window.ITransactionReadyCallback;
import android.window.SurfaceSyncGroup;
import com.android.internal.annotations.GuardedBy;
import com.android.server.SystemServerInitThreadPool$$ExternalSyntheticLambda0;
/* renamed from: com.android.server.wm.SurfaceSyncGroupController */
/* loaded from: classes2.dex */
public class SurfaceSyncGroupController {
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public final ArrayMap<IBinder, SurfaceSyncGroupData> mSurfaceSyncGroups = new ArrayMap<>();

    public boolean addToSyncGroup(IBinder iBinder, boolean z, final ISurfaceSyncGroupCompletedListener iSurfaceSyncGroupCompletedListener, AddToSurfaceSyncGroupResult addToSurfaceSyncGroupResult) {
        SurfaceSyncGroup surfaceSyncGroup;
        synchronized (this.mLock) {
            SurfaceSyncGroupData surfaceSyncGroupData = this.mSurfaceSyncGroups.get(iBinder);
            if (surfaceSyncGroupData == null) {
                surfaceSyncGroup = new SurfaceSyncGroup("SurfaceSyncGroupController-" + iBinder.hashCode());
                if (iSurfaceSyncGroupCompletedListener != null) {
                    surfaceSyncGroup.addSyncCompleteCallback(new SystemServerInitThreadPool$$ExternalSyntheticLambda0(), new Runnable() { // from class: com.android.server.wm.SurfaceSyncGroupController$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            SurfaceSyncGroupController.lambda$addToSyncGroup$0(iSurfaceSyncGroupCompletedListener);
                        }
                    });
                }
                this.mSurfaceSyncGroups.put(iBinder, new SurfaceSyncGroupData(Binder.getCallingUid(), surfaceSyncGroup));
            } else {
                surfaceSyncGroup = surfaceSyncGroupData.mSurfaceSyncGroup;
            }
        }
        ITransactionReadyCallback createTransactionReadyCallback = surfaceSyncGroup.createTransactionReadyCallback(z);
        if (createTransactionReadyCallback == null) {
            return false;
        }
        addToSurfaceSyncGroupResult.mParentSyncGroup = surfaceSyncGroup.mISurfaceSyncGroup;
        addToSurfaceSyncGroupResult.mTransactionReadyCallback = createTransactionReadyCallback;
        return true;
    }

    public static /* synthetic */ void lambda$addToSyncGroup$0(ISurfaceSyncGroupCompletedListener iSurfaceSyncGroupCompletedListener) {
        try {
            iSurfaceSyncGroupCompletedListener.onSurfaceSyncGroupComplete();
        } catch (RemoteException unused) {
        }
    }

    public void markSyncGroupReady(IBinder iBinder) {
        SurfaceSyncGroup surfaceSyncGroup;
        synchronized (this.mLock) {
            SurfaceSyncGroupData surfaceSyncGroupData = this.mSurfaceSyncGroups.get(iBinder);
            if (surfaceSyncGroupData == null) {
                throw new IllegalArgumentException("SurfaceSyncGroup Token has not been set up or has already been marked as ready");
            }
            if (surfaceSyncGroupData.mOwningUid != Binder.getCallingUid()) {
                throw new IllegalArgumentException("Only process that created the SurfaceSyncGroup can call markSyncGroupReady");
            }
            surfaceSyncGroup = surfaceSyncGroupData.mSurfaceSyncGroup;
            this.mSurfaceSyncGroups.remove(iBinder);
        }
        surfaceSyncGroup.markSyncReady();
    }

    /* renamed from: com.android.server.wm.SurfaceSyncGroupController$SurfaceSyncGroupData */
    /* loaded from: classes2.dex */
    public static class SurfaceSyncGroupData {
        public final int mOwningUid;
        public final SurfaceSyncGroup mSurfaceSyncGroup;

        public SurfaceSyncGroupData(int i, SurfaceSyncGroup surfaceSyncGroup) {
            this.mOwningUid = i;
            this.mSurfaceSyncGroup = surfaceSyncGroup;
        }
    }
}
