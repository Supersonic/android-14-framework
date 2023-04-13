package com.android.server.wearable;

import android.app.wearable.IWearableSensingManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.p000pm.PackageManagerInternal;
import android.os.ParcelFileDescriptor;
import android.os.PersistableBundle;
import android.os.RemoteCallback;
import android.os.ResultReceiver;
import android.os.SharedMemory;
import android.os.ShellCallback;
import android.os.UserHandle;
import android.provider.DeviceConfig;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.LocalServices;
import com.android.server.infra.AbstractMasterSystemService;
import com.android.server.infra.FrameworkResourcesServiceNameResolver;
import java.io.FileDescriptor;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes2.dex */
public class WearableSensingManagerService extends AbstractMasterSystemService<WearableSensingManagerService, WearableSensingManagerPerUserService> {
    public static final String TAG = "WearableSensingManagerService";
    public final Context mContext;
    public volatile boolean mIsServiceEnabled;

    @Override // com.android.server.infra.AbstractMasterSystemService
    public int getMaximumTemporaryServiceDurationMs() {
        return 30000;
    }

    public WearableSensingManagerService(Context context) {
        super(context, new FrameworkResourcesServiceNameResolver(context, 17039909), null, 68);
        this.mContext = context;
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("wearable_sensing", new WearableSensingManagerInternal());
    }

    @Override // com.android.server.infra.AbstractMasterSystemService, com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 500) {
            DeviceConfig.addOnPropertiesChangedListener("wearable_sensing", getContext().getMainExecutor(), new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.wearable.WearableSensingManagerService$$ExternalSyntheticLambda0
                public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                    WearableSensingManagerService.this.lambda$onBootPhase$0(properties);
                }
            });
            this.mIsServiceEnabled = DeviceConfig.getBoolean("wearable_sensing", "service_enabled", true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBootPhase$0(DeviceConfig.Properties properties) {
        onDeviceConfigChange(properties.getKeyset());
    }

    public final void onDeviceConfigChange(Set<String> set) {
        if (set.contains("service_enabled")) {
            this.mIsServiceEnabled = DeviceConfig.getBoolean("wearable_sensing", "service_enabled", true);
        }
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public WearableSensingManagerPerUserService newServiceLocked(int i, boolean z) {
        return new WearableSensingManagerPerUserService(this, this.mLock, i);
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void onServiceRemoved(WearableSensingManagerPerUserService wearableSensingManagerPerUserService, int i) {
        Slog.d(TAG, "onServiceRemoved");
        wearableSensingManagerPerUserService.destroyLocked();
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void onServicePackageRestartedLocked(int i) {
        Slog.d(TAG, "onServicePackageRestartedLocked.");
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void onServicePackageUpdatedLocked(int i) {
        Slog.d(TAG, "onServicePackageUpdatedLocked.");
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void enforceCallingPermissionForManagement() {
        getContext().enforceCallingPermission("android.permission.ACCESS_AMBIENT_CONTEXT_EVENT", TAG);
    }

    public static boolean isDetectionServiceConfigured() {
        boolean z = ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).getKnownPackageNames(19, 0).length != 0;
        String str = TAG;
        Slog.i(str, "Wearable sensing service configured: " + z);
        return z;
    }

    public ComponentName getComponentName(int i) {
        synchronized (this.mLock) {
            WearableSensingManagerPerUserService serviceForUserLocked = getServiceForUserLocked(i);
            if (serviceForUserLocked != null) {
                return serviceForUserLocked.getComponentName();
            }
            return null;
        }
    }

    @VisibleForTesting
    public void provideDataStream(int i, ParcelFileDescriptor parcelFileDescriptor, RemoteCallback remoteCallback) {
        synchronized (this.mLock) {
            WearableSensingManagerPerUserService serviceForUserLocked = getServiceForUserLocked(i);
            if (serviceForUserLocked != null) {
                serviceForUserLocked.onProvideDataStream(parcelFileDescriptor, remoteCallback);
            } else {
                Slog.w(TAG, "Service not available.");
            }
        }
    }

    @VisibleForTesting
    public void provideData(int i, PersistableBundle persistableBundle, SharedMemory sharedMemory, RemoteCallback remoteCallback) {
        synchronized (this.mLock) {
            WearableSensingManagerPerUserService serviceForUserLocked = getServiceForUserLocked(i);
            if (serviceForUserLocked != null) {
                serviceForUserLocked.onProvidedData(persistableBundle, sharedMemory, remoteCallback);
            } else {
                Slog.w(TAG, "Service not available.");
            }
        }
    }

    /* loaded from: classes2.dex */
    public final class WearableSensingManagerInternal extends IWearableSensingManager.Stub {
        public final WearableSensingManagerPerUserService mService;

        public WearableSensingManagerInternal() {
            this.mService = (WearableSensingManagerPerUserService) WearableSensingManagerService.this.getServiceForUserLocked(UserHandle.getCallingUserId());
        }

        public void provideDataStream(ParcelFileDescriptor parcelFileDescriptor, RemoteCallback remoteCallback) {
            Slog.i(WearableSensingManagerService.TAG, "WearableSensingManagerInternal provideDataStream.");
            Objects.requireNonNull(parcelFileDescriptor);
            Objects.requireNonNull(remoteCallback);
            WearableSensingManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_WEARABLE_SENSING_SERVICE", WearableSensingManagerService.TAG);
            if (!WearableSensingManagerService.this.mIsServiceEnabled) {
                Slog.w(WearableSensingManagerService.TAG, "Service not available.");
                WearableSensingManagerPerUserService.notifyStatusCallback(remoteCallback, 3);
                return;
            }
            this.mService.onProvideDataStream(parcelFileDescriptor, remoteCallback);
        }

        public void provideData(PersistableBundle persistableBundle, SharedMemory sharedMemory, RemoteCallback remoteCallback) {
            Slog.i(WearableSensingManagerService.TAG, "WearableSensingManagerInternal provideData.");
            Objects.requireNonNull(persistableBundle);
            Objects.requireNonNull(remoteCallback);
            WearableSensingManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_WEARABLE_SENSING_SERVICE", WearableSensingManagerService.TAG);
            if (!WearableSensingManagerService.this.mIsServiceEnabled) {
                Slog.w(WearableSensingManagerService.TAG, "Service not available.");
                WearableSensingManagerPerUserService.notifyStatusCallback(remoteCallback, 3);
                return;
            }
            this.mService.onProvidedData(persistableBundle, sharedMemory, remoteCallback);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
            new WearableSensingShellCommand(WearableSensingManagerService.this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
        }
    }
}
