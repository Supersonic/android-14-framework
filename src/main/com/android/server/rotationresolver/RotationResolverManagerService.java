package com.android.server.rotationresolver;

import android.content.ComponentName;
import android.content.Context;
import android.hardware.SensorPrivacyManager;
import android.os.Binder;
import android.os.CancellationSignal;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.os.UserHandle;
import android.provider.DeviceConfig;
import android.rotationresolver.RotationResolverInternal;
import android.service.rotationresolver.RotationResolutionRequest;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.infra.AbstractMasterSystemService;
import com.android.server.infra.FrameworkResourcesServiceNameResolver;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes2.dex */
public class RotationResolverManagerService extends AbstractMasterSystemService<RotationResolverManagerService, RotationResolverManagerPerUserService> {
    public static final String TAG = "RotationResolverManagerService";
    public final Context mContext;
    public boolean mIsServiceEnabled;
    public final SensorPrivacyManager mPrivacyManager;

    public static int errorCodeToProto(int i) {
        if (i == 0 || i == 1 || i == 2) {
            return 0;
        }
        return i != 4 ? 8 : 7;
    }

    public static int surfaceRotationToProto(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    return i != 3 ? 8 : 5;
                }
                return 4;
            }
            return 3;
        }
        return 2;
    }

    public RotationResolverManagerService(Context context) {
        super(context, new FrameworkResourcesServiceNameResolver(context, 17039898), null, 68);
        this.mContext = context;
        this.mPrivacyManager = SensorPrivacyManager.getInstance(context);
    }

    @Override // com.android.server.infra.AbstractMasterSystemService, com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 500) {
            DeviceConfig.addOnPropertiesChangedListener("rotation_resolver", getContext().getMainExecutor(), new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.rotationresolver.RotationResolverManagerService$$ExternalSyntheticLambda0
                public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                    RotationResolverManagerService.this.lambda$onBootPhase$0(properties);
                }
            });
            this.mIsServiceEnabled = DeviceConfig.getBoolean("rotation_resolver", "service_enabled", true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBootPhase$0(DeviceConfig.Properties properties) {
        onDeviceConfigChange(properties.getKeyset());
    }

    public final void onDeviceConfigChange(Set<String> set) {
        if (set.contains("service_enabled")) {
            this.mIsServiceEnabled = DeviceConfig.getBoolean("rotation_resolver", "service_enabled", true);
        }
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("resolver", new BinderService());
        publishLocalService(RotationResolverInternal.class, new LocalService());
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public RotationResolverManagerPerUserService newServiceLocked(int i, boolean z) {
        return new RotationResolverManagerPerUserService(this, this.mLock, i);
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void onServiceRemoved(RotationResolverManagerPerUserService rotationResolverManagerPerUserService, int i) {
        synchronized (this.mLock) {
            rotationResolverManagerPerUserService.destroyLocked();
        }
    }

    public static boolean isServiceConfigured(Context context) {
        return !TextUtils.isEmpty(getServiceConfigPackage(context));
    }

    public ComponentName getComponentNameShellCommand(int i) {
        synchronized (this.mLock) {
            RotationResolverManagerPerUserService serviceForUserLocked = getServiceForUserLocked(i);
            if (serviceForUserLocked != null) {
                return serviceForUserLocked.getComponentName();
            }
            return null;
        }
    }

    public void resolveRotationShellCommand(int i, RotationResolverInternal.RotationResolverCallbackInternal rotationResolverCallbackInternal, RotationResolutionRequest rotationResolutionRequest) {
        synchronized (this.mLock) {
            RotationResolverManagerPerUserService serviceForUserLocked = getServiceForUserLocked(i);
            if (serviceForUserLocked != null) {
                serviceForUserLocked.resolveRotationLocked(rotationResolverCallbackInternal, rotationResolutionRequest, new CancellationSignal());
            } else {
                String str = TAG;
                Slog.i(str, "service not available for user_id: " + i);
            }
        }
    }

    public static String getServiceConfigPackage(Context context) {
        return context.getPackageManager().getRotationResolverPackageName();
    }

    /* loaded from: classes2.dex */
    public final class LocalService extends RotationResolverInternal {
        public LocalService() {
        }

        public boolean isRotationResolverSupported() {
            boolean z;
            synchronized (RotationResolverManagerService.this.mLock) {
                z = RotationResolverManagerService.this.mIsServiceEnabled;
            }
            return z;
        }

        public void resolveRotation(RotationResolverInternal.RotationResolverCallbackInternal rotationResolverCallbackInternal, String str, int i, int i2, long j, CancellationSignal cancellationSignal) {
            RotationResolutionRequest rotationResolutionRequest;
            Objects.requireNonNull(rotationResolverCallbackInternal);
            Objects.requireNonNull(cancellationSignal);
            synchronized (RotationResolverManagerService.this.mLock) {
                boolean z = !RotationResolverManagerService.this.mPrivacyManager.isSensorPrivacyEnabled(2);
                RotationResolverManagerService rotationResolverManagerService = RotationResolverManagerService.this;
                if (rotationResolverManagerService.mIsServiceEnabled && z) {
                    RotationResolverManagerPerUserService rotationResolverManagerPerUserService = (RotationResolverManagerPerUserService) rotationResolverManagerService.getServiceForUserLocked(UserHandle.getCallingUserId());
                    if (str == null) {
                        rotationResolutionRequest = new RotationResolutionRequest("", i2, i, true, j);
                    } else {
                        rotationResolutionRequest = new RotationResolutionRequest(str, i2, i, true, j);
                    }
                    rotationResolverManagerPerUserService.resolveRotationLocked(rotationResolverCallbackInternal, rotationResolutionRequest, cancellationSignal);
                } else {
                    if (z) {
                        Slog.w(RotationResolverManagerService.TAG, "Rotation Resolver service is disabled.");
                    } else {
                        Slog.w(RotationResolverManagerService.TAG, "Camera is locked by a toggle.");
                    }
                    rotationResolverCallbackInternal.onFailure(0);
                    RotationResolverManagerService.logRotationStats(i, i2, 6);
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    public final class BinderService extends Binder {
        public BinderService() {
        }

        @Override // android.os.Binder
        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            if (DumpUtils.checkDumpPermission(RotationResolverManagerService.this.mContext, RotationResolverManagerService.TAG, printWriter)) {
                synchronized (RotationResolverManagerService.this.mLock) {
                    RotationResolverManagerService.this.dumpLocked("", printWriter);
                }
            }
        }

        public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
            RotationResolverManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_ROTATION_RESOLVER", RotationResolverManagerService.TAG);
            new RotationResolverShellCommand(RotationResolverManagerService.this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
        }
    }

    public static void logRotationStatsWithTimeToCalculate(int i, int i2, int i3, long j) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.AUTO_ROTATE_REPORTED, surfaceRotationToProto(i2), surfaceRotationToProto(i), i3, j);
    }

    public static void logRotationStats(int i, int i2, int i3) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.AUTO_ROTATE_REPORTED, surfaceRotationToProto(i2), surfaceRotationToProto(i), i3);
    }
}
