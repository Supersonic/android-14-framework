package com.android.server.wearable;

import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.PersistableBundle;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.SharedMemory;
import android.system.OsConstants;
import android.util.IndentingPrintWriter;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.infra.AbstractPerUserSystemService;
import java.io.PrintWriter;
/* loaded from: classes2.dex */
public final class WearableSensingManagerPerUserService extends AbstractPerUserSystemService<WearableSensingManagerPerUserService, WearableSensingManagerService> {
    public static final String TAG = "WearableSensingManagerPerUserService";
    public ComponentName mComponentName;
    @VisibleForTesting
    RemoteWearableSensingService mRemoteService;

    public WearableSensingManagerPerUserService(WearableSensingManagerService wearableSensingManagerService, Object obj, int i) {
        super(wearableSensingManagerService, obj, i);
    }

    public static void notifyStatusCallback(RemoteCallback remoteCallback, int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("android.app.wearable.WearableSensingStatusBundleKey", i);
        remoteCallback.sendResult(bundle);
    }

    public void destroyLocked() {
        Slog.d(TAG, "Trying to cancel the remote request. Reason: Service destroyed.");
        if (this.mRemoteService != null) {
            synchronized (this.mLock) {
                this.mRemoteService.unbind();
                this.mRemoteService = null;
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void ensureRemoteServiceInitiated() {
        if (this.mRemoteService == null) {
            this.mRemoteService = new RemoteWearableSensingService(getContext(), this.mComponentName, getUserId());
        }
    }

    @VisibleForTesting
    public ComponentName getComponentName() {
        return this.mComponentName;
    }

    @GuardedBy({"mLock"})
    @VisibleForTesting
    public boolean setUpServiceIfNeeded() {
        if (this.mComponentName == null) {
            this.mComponentName = updateServiceInfoLocked();
        }
        if (this.mComponentName == null) {
            return false;
        }
        try {
            return AppGlobals.getPackageManager().getServiceInfo(this.mComponentName, 0L, this.mUserId) != null;
        } catch (RemoteException unused) {
            Slog.w(TAG, "RemoteException while setting up service");
            return false;
        }
    }

    @Override // com.android.server.infra.AbstractPerUserSystemService
    public ServiceInfo newServiceInfoLocked(ComponentName componentName) throws PackageManager.NameNotFoundException {
        try {
            ServiceInfo serviceInfo = AppGlobals.getPackageManager().getServiceInfo(componentName, 0L, this.mUserId);
            if (serviceInfo != null && !"android.permission.BIND_WEARABLE_SENSING_SERVICE".equals(serviceInfo.permission)) {
                throw new SecurityException(String.format("Service %s requires %s permission. Found %s permission", serviceInfo.getComponentName(), "android.permission.BIND_WEARABLE_SENSING_SERVICE", serviceInfo.permission));
            }
            return serviceInfo;
        } catch (RemoteException unused) {
            throw new PackageManager.NameNotFoundException("Could not get service for " + componentName);
        }
    }

    @Override // com.android.server.infra.AbstractPerUserSystemService
    public void dumpLocked(String str, PrintWriter printWriter) {
        synchronized (this.mLock) {
            super.dumpLocked(str, printWriter);
        }
        RemoteWearableSensingService remoteWearableSensingService = this.mRemoteService;
        if (remoteWearableSensingService != null) {
            remoteWearableSensingService.dump("", new IndentingPrintWriter(printWriter, "  "));
        }
    }

    public void onProvideDataStream(ParcelFileDescriptor parcelFileDescriptor, RemoteCallback remoteCallback) {
        String str = TAG;
        Slog.i(str, "onProvideDataStream in per user service.");
        synchronized (this.mLock) {
            if (!setUpServiceIfNeeded()) {
                Slog.w(str, "Detection service is not available at this moment.");
                notifyStatusCallback(remoteCallback, 3);
                return;
            }
            Slog.i(str, "calling over to remote servvice.");
            ensureRemoteServiceInitiated();
            this.mRemoteService.provideDataStream(parcelFileDescriptor, remoteCallback);
        }
    }

    public void onProvidedData(PersistableBundle persistableBundle, SharedMemory sharedMemory, RemoteCallback remoteCallback) {
        synchronized (this.mLock) {
            if (!setUpServiceIfNeeded()) {
                Slog.w(TAG, "Detection service is not available at this moment.");
                notifyStatusCallback(remoteCallback, 3);
                return;
            }
            ensureRemoteServiceInitiated();
            if (sharedMemory != null) {
                sharedMemory.setProtect(OsConstants.PROT_READ);
            }
            this.mRemoteService.provideData(persistableBundle, sharedMemory, remoteCallback);
        }
    }
}
