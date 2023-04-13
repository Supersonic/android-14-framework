package com.android.server.appprediction;

import android.app.ActivityManagerInternal;
import android.app.prediction.AppPredictionContext;
import android.app.prediction.AppPredictionSessionId;
import android.app.prediction.AppTargetEvent;
import android.app.prediction.IPredictionCallback;
import android.app.prediction.IPredictionManager;
import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.os.Binder;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.util.Slog;
import com.android.server.LocalServices;
import com.android.server.infra.AbstractMasterSystemService;
import com.android.server.infra.FrameworkResourcesServiceNameResolver;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import java.io.FileDescriptor;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class AppPredictionManagerService extends AbstractMasterSystemService<AppPredictionManagerService, AppPredictionPerUserService> {
    public static final String TAG = "AppPredictionManagerService";
    public ActivityTaskManagerInternal mActivityTaskManagerInternal;

    @Override // com.android.server.infra.AbstractMasterSystemService
    public int getMaximumTemporaryServiceDurationMs() {
        return 120000;
    }

    public AppPredictionManagerService(Context context) {
        super(context, new FrameworkResourcesServiceNameResolver(context, 17039871), null, 17);
        this.mActivityTaskManagerInternal = (ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.server.infra.AbstractMasterSystemService
    public AppPredictionPerUserService newServiceLocked(int i, boolean z) {
        return new AppPredictionPerUserService(this, this.mLock, i);
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("app_prediction", new PredictionManagerServiceStub());
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void enforceCallingPermissionForManagement() {
        getContext().enforceCallingPermission("android.permission.MANAGE_APP_PREDICTIONS", TAG);
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void onServicePackageUpdatedLocked(int i) {
        AppPredictionPerUserService peekServiceForUserLocked = peekServiceForUserLocked(i);
        if (peekServiceForUserLocked != null) {
            peekServiceForUserLocked.onPackageUpdatedLocked();
        }
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void onServicePackageRestartedLocked(int i) {
        AppPredictionPerUserService peekServiceForUserLocked = peekServiceForUserLocked(i);
        if (peekServiceForUserLocked != null) {
            peekServiceForUserLocked.onPackageRestartedLocked();
        }
    }

    /* loaded from: classes.dex */
    public class PredictionManagerServiceStub extends IPredictionManager.Stub {
        public PredictionManagerServiceStub() {
        }

        public void createPredictionSession(final AppPredictionContext appPredictionContext, final AppPredictionSessionId appPredictionSessionId, final IBinder iBinder) {
            runForUserLocked("createPredictionSession", appPredictionSessionId, new Consumer() { // from class: com.android.server.appprediction.AppPredictionManagerService$PredictionManagerServiceStub$$ExternalSyntheticLambda7
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((AppPredictionPerUserService) obj).onCreatePredictionSessionLocked(appPredictionContext, appPredictionSessionId, iBinder);
                }
            });
        }

        public void notifyAppTargetEvent(final AppPredictionSessionId appPredictionSessionId, final AppTargetEvent appTargetEvent) {
            runForUserLocked("notifyAppTargetEvent", appPredictionSessionId, new Consumer() { // from class: com.android.server.appprediction.AppPredictionManagerService$PredictionManagerServiceStub$$ExternalSyntheticLambda5
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((AppPredictionPerUserService) obj).notifyAppTargetEventLocked(appPredictionSessionId, appTargetEvent);
                }
            });
        }

        public void notifyLaunchLocationShown(final AppPredictionSessionId appPredictionSessionId, final String str, final ParceledListSlice parceledListSlice) {
            runForUserLocked("notifyLaunchLocationShown", appPredictionSessionId, new Consumer() { // from class: com.android.server.appprediction.AppPredictionManagerService$PredictionManagerServiceStub$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((AppPredictionPerUserService) obj).notifyLaunchLocationShownLocked(appPredictionSessionId, str, parceledListSlice);
                }
            });
        }

        public void sortAppTargets(final AppPredictionSessionId appPredictionSessionId, final ParceledListSlice parceledListSlice, final IPredictionCallback iPredictionCallback) {
            runForUserLocked("sortAppTargets", appPredictionSessionId, new Consumer() { // from class: com.android.server.appprediction.AppPredictionManagerService$PredictionManagerServiceStub$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((AppPredictionPerUserService) obj).sortAppTargetsLocked(appPredictionSessionId, parceledListSlice, iPredictionCallback);
                }
            });
        }

        public void registerPredictionUpdates(final AppPredictionSessionId appPredictionSessionId, final IPredictionCallback iPredictionCallback) {
            runForUserLocked("registerPredictionUpdates", appPredictionSessionId, new Consumer() { // from class: com.android.server.appprediction.AppPredictionManagerService$PredictionManagerServiceStub$$ExternalSyntheticLambda4
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((AppPredictionPerUserService) obj).registerPredictionUpdatesLocked(appPredictionSessionId, iPredictionCallback);
                }
            });
        }

        public void unregisterPredictionUpdates(final AppPredictionSessionId appPredictionSessionId, final IPredictionCallback iPredictionCallback) {
            runForUserLocked("unregisterPredictionUpdates", appPredictionSessionId, new Consumer() { // from class: com.android.server.appprediction.AppPredictionManagerService$PredictionManagerServiceStub$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((AppPredictionPerUserService) obj).unregisterPredictionUpdatesLocked(appPredictionSessionId, iPredictionCallback);
                }
            });
        }

        public void requestPredictionUpdate(final AppPredictionSessionId appPredictionSessionId) {
            runForUserLocked("requestPredictionUpdate", appPredictionSessionId, new Consumer() { // from class: com.android.server.appprediction.AppPredictionManagerService$PredictionManagerServiceStub$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((AppPredictionPerUserService) obj).requestPredictionUpdateLocked(appPredictionSessionId);
                }
            });
        }

        public void onDestroyPredictionSession(final AppPredictionSessionId appPredictionSessionId) {
            runForUserLocked("onDestroyPredictionSession", appPredictionSessionId, new Consumer() { // from class: com.android.server.appprediction.AppPredictionManagerService$PredictionManagerServiceStub$$ExternalSyntheticLambda6
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((AppPredictionPerUserService) obj).onDestroyPredictionSessionLocked(appPredictionSessionId);
                }
            });
        }

        /* JADX WARN: Multi-variable type inference failed */
        public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
            new AppPredictionManagerServiceShellCommand(AppPredictionManagerService.this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
        }

        public final void runForUserLocked(String str, AppPredictionSessionId appPredictionSessionId, Consumer<AppPredictionPerUserService> consumer) {
            int handleIncomingUser = ((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), appPredictionSessionId.getUserId(), false, 0, (String) null, (String) null);
            if (AppPredictionManagerService.this.getContext().checkCallingPermission("android.permission.PACKAGE_USAGE_STATS") != 0 && !AppPredictionManagerService.this.mServiceNameResolver.isTemporary(handleIncomingUser) && !AppPredictionManagerService.this.mActivityTaskManagerInternal.isCallerRecents(Binder.getCallingUid()) && Binder.getCallingUid() != 1000) {
                String str2 = "Permission Denial: " + str + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " expected caller to hold PACKAGE_USAGE_STATS permission";
                Slog.w(AppPredictionManagerService.TAG, str2);
                throw new SecurityException(str2);
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (AppPredictionManagerService.this.mLock) {
                    consumer.accept((AppPredictionPerUserService) AppPredictionManagerService.this.getServiceForUserLocked(handleIncomingUser));
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }
}
