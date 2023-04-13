package com.android.server.appprediction;

import android.app.AppGlobals;
import android.app.prediction.AppPredictionContext;
import android.app.prediction.AppPredictionSessionId;
import android.app.prediction.AppTargetEvent;
import android.app.prediction.IPredictionCallback;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.provider.DeviceConfig;
import android.service.appprediction.IPredictionService;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.infra.AbstractRemoteService;
import com.android.server.LocalServices;
import com.android.server.appprediction.AppPredictionPerUserService;
import com.android.server.appprediction.RemoteAppPredictionService;
import com.android.server.infra.AbstractPerUserSystemService;
import com.android.server.people.PeopleServiceInternal;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class AppPredictionPerUserService extends AbstractPerUserSystemService<AppPredictionPerUserService, AppPredictionManagerService> implements RemoteAppPredictionService.RemoteAppPredictionServiceCallbacks {
    public static final String TAG = AppPredictionPerUserService.class.getSimpleName();
    @GuardedBy({"mLock"})
    public RemoteAppPredictionService mRemoteService;
    @GuardedBy({"mLock"})
    public final ArrayMap<AppPredictionSessionId, AppPredictionSessionInfo> mSessionInfos;
    @GuardedBy({"mLock"})
    public boolean mZombie;

    public AppPredictionPerUserService(AppPredictionManagerService appPredictionManagerService, Object obj, int i) {
        super(appPredictionManagerService, obj, i);
        this.mSessionInfos = new ArrayMap<>();
    }

    @Override // com.android.server.infra.AbstractPerUserSystemService
    public ServiceInfo newServiceInfoLocked(ComponentName componentName) throws PackageManager.NameNotFoundException {
        try {
            return AppGlobals.getPackageManager().getServiceInfo(componentName, 128L, this.mUserId);
        } catch (RemoteException unused) {
            throw new PackageManager.NameNotFoundException("Could not get service for " + componentName);
        }
    }

    @Override // com.android.server.infra.AbstractPerUserSystemService
    @GuardedBy({"mLock"})
    public boolean updateLocked(boolean z) {
        boolean updateLocked = super.updateLocked(z);
        if (updateLocked && !isEnabledLocked()) {
            this.mRemoteService = null;
        }
        return updateLocked;
    }

    @GuardedBy({"mLock"})
    public void onCreatePredictionSessionLocked(final AppPredictionContext appPredictionContext, final AppPredictionSessionId appPredictionSessionId, IBinder iBinder) {
        boolean z = (appPredictionContext.getExtras() != null && appPredictionContext.getExtras().getBoolean("remote_app_predictor", false) && DeviceConfig.getBoolean("systemui", "dark_launch_remote_prediction_service_enabled", false)) ? false : DeviceConfig.getBoolean("systemui", "predict_using_people_service_" + appPredictionContext.getUiSurface(), false);
        if (!resolveService(appPredictionSessionId, true, z, new AbstractRemoteService.AsyncRequest() { // from class: com.android.server.appprediction.AppPredictionPerUserService$$ExternalSyntheticLambda5
            public final void run(IInterface iInterface) {
                ((IPredictionService) iInterface).onCreatePredictionSession(appPredictionContext, appPredictionSessionId);
            }
        }) || this.mSessionInfos.containsKey(appPredictionSessionId)) {
            return;
        }
        AppPredictionSessionInfo appPredictionSessionInfo = new AppPredictionSessionInfo(appPredictionSessionId, appPredictionContext, z, iBinder, new IBinder.DeathRecipient() { // from class: com.android.server.appprediction.AppPredictionPerUserService$$ExternalSyntheticLambda6
            @Override // android.os.IBinder.DeathRecipient
            public final void binderDied() {
                AppPredictionPerUserService.this.lambda$onCreatePredictionSessionLocked$1(appPredictionSessionId);
            }
        });
        if (appPredictionSessionInfo.linkToDeath()) {
            this.mSessionInfos.put(appPredictionSessionId, appPredictionSessionInfo);
        } else {
            onDestroyPredictionSessionLocked(appPredictionSessionId);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreatePredictionSessionLocked$1(AppPredictionSessionId appPredictionSessionId) {
        synchronized (this.mLock) {
            onDestroyPredictionSessionLocked(appPredictionSessionId);
        }
    }

    @GuardedBy({"mLock"})
    public void notifyAppTargetEventLocked(final AppPredictionSessionId appPredictionSessionId, final AppTargetEvent appTargetEvent) {
        AppPredictionSessionInfo appPredictionSessionInfo = this.mSessionInfos.get(appPredictionSessionId);
        if (appPredictionSessionInfo == null) {
            return;
        }
        resolveService(appPredictionSessionId, false, appPredictionSessionInfo.mUsesPeopleService, new AbstractRemoteService.AsyncRequest() { // from class: com.android.server.appprediction.AppPredictionPerUserService$$ExternalSyntheticLambda1
            public final void run(IInterface iInterface) {
                ((IPredictionService) iInterface).notifyAppTargetEvent(appPredictionSessionId, appTargetEvent);
            }
        });
    }

    @GuardedBy({"mLock"})
    public void notifyLaunchLocationShownLocked(final AppPredictionSessionId appPredictionSessionId, final String str, final ParceledListSlice parceledListSlice) {
        AppPredictionSessionInfo appPredictionSessionInfo = this.mSessionInfos.get(appPredictionSessionId);
        if (appPredictionSessionInfo == null) {
            return;
        }
        resolveService(appPredictionSessionId, false, appPredictionSessionInfo.mUsesPeopleService, new AbstractRemoteService.AsyncRequest() { // from class: com.android.server.appprediction.AppPredictionPerUserService$$ExternalSyntheticLambda3
            public final void run(IInterface iInterface) {
                ((IPredictionService) iInterface).notifyLaunchLocationShown(appPredictionSessionId, str, parceledListSlice);
            }
        });
    }

    @GuardedBy({"mLock"})
    public void sortAppTargetsLocked(final AppPredictionSessionId appPredictionSessionId, final ParceledListSlice parceledListSlice, final IPredictionCallback iPredictionCallback) {
        AppPredictionSessionInfo appPredictionSessionInfo = this.mSessionInfos.get(appPredictionSessionId);
        if (appPredictionSessionInfo == null) {
            return;
        }
        resolveService(appPredictionSessionId, true, appPredictionSessionInfo.mUsesPeopleService, new AbstractRemoteService.AsyncRequest() { // from class: com.android.server.appprediction.AppPredictionPerUserService$$ExternalSyntheticLambda8
            public final void run(IInterface iInterface) {
                ((IPredictionService) iInterface).sortAppTargets(appPredictionSessionId, parceledListSlice, iPredictionCallback);
            }
        });
    }

    @GuardedBy({"mLock"})
    public void registerPredictionUpdatesLocked(final AppPredictionSessionId appPredictionSessionId, final IPredictionCallback iPredictionCallback) {
        AppPredictionSessionInfo appPredictionSessionInfo = this.mSessionInfos.get(appPredictionSessionId);
        if (appPredictionSessionInfo != null && resolveService(appPredictionSessionId, true, appPredictionSessionInfo.mUsesPeopleService, new AbstractRemoteService.AsyncRequest() { // from class: com.android.server.appprediction.AppPredictionPerUserService$$ExternalSyntheticLambda2
            public final void run(IInterface iInterface) {
                ((IPredictionService) iInterface).registerPredictionUpdates(appPredictionSessionId, iPredictionCallback);
            }
        })) {
            appPredictionSessionInfo.addCallbackLocked(iPredictionCallback);
        }
    }

    @GuardedBy({"mLock"})
    public void unregisterPredictionUpdatesLocked(final AppPredictionSessionId appPredictionSessionId, final IPredictionCallback iPredictionCallback) {
        AppPredictionSessionInfo appPredictionSessionInfo = this.mSessionInfos.get(appPredictionSessionId);
        if (appPredictionSessionInfo != null && resolveService(appPredictionSessionId, false, appPredictionSessionInfo.mUsesPeopleService, new AbstractRemoteService.AsyncRequest() { // from class: com.android.server.appprediction.AppPredictionPerUserService$$ExternalSyntheticLambda0
            public final void run(IInterface iInterface) {
                ((IPredictionService) iInterface).unregisterPredictionUpdates(appPredictionSessionId, iPredictionCallback);
            }
        })) {
            appPredictionSessionInfo.removeCallbackLocked(iPredictionCallback);
        }
    }

    @GuardedBy({"mLock"})
    public void requestPredictionUpdateLocked(final AppPredictionSessionId appPredictionSessionId) {
        AppPredictionSessionInfo appPredictionSessionInfo = this.mSessionInfos.get(appPredictionSessionId);
        if (appPredictionSessionInfo == null) {
            return;
        }
        resolveService(appPredictionSessionId, true, appPredictionSessionInfo.mUsesPeopleService, new AbstractRemoteService.AsyncRequest() { // from class: com.android.server.appprediction.AppPredictionPerUserService$$ExternalSyntheticLambda7
            public final void run(IInterface iInterface) {
                ((IPredictionService) iInterface).requestPredictionUpdate(appPredictionSessionId);
            }
        });
    }

    @GuardedBy({"mLock"})
    public void onDestroyPredictionSessionLocked(final AppPredictionSessionId appPredictionSessionId) {
        if (isDebug()) {
            String str = TAG;
            Slog.d(str, "onDestroyPredictionSessionLocked(): sessionId=" + appPredictionSessionId);
        }
        AppPredictionSessionInfo remove = this.mSessionInfos.remove(appPredictionSessionId);
        if (remove == null) {
            return;
        }
        resolveService(appPredictionSessionId, false, remove.mUsesPeopleService, new AbstractRemoteService.AsyncRequest() { // from class: com.android.server.appprediction.AppPredictionPerUserService$$ExternalSyntheticLambda4
            public final void run(IInterface iInterface) {
                ((IPredictionService) iInterface).onDestroyPredictionSession(appPredictionSessionId);
            }
        });
        remove.destroy();
    }

    @Override // com.android.server.appprediction.RemoteAppPredictionService.RemoteAppPredictionServiceCallbacks
    public void onConnectedStateChanged(boolean z) {
        if (isDebug()) {
            String str = TAG;
            Slog.d(str, "onConnectedStateChanged(): connected=" + z);
        }
        if (z) {
            synchronized (this.mLock) {
                if (this.mZombie) {
                    if (this.mRemoteService == null) {
                        Slog.w(TAG, "Cannot resurrect sessions because remote service is null");
                    } else {
                        this.mZombie = false;
                        resurrectSessionsLocked();
                    }
                }
            }
        }
    }

    public void onServiceDied(RemoteAppPredictionService remoteAppPredictionService) {
        if (isDebug()) {
            String str = TAG;
            Slog.w(str, "onServiceDied(): service=" + remoteAppPredictionService);
        }
        synchronized (this.mLock) {
            this.mZombie = true;
        }
    }

    public void onPackageUpdatedLocked() {
        if (isDebug()) {
            Slog.v(TAG, "onPackageUpdatedLocked()");
        }
        destroyAndRebindRemoteService();
    }

    public void onPackageRestartedLocked() {
        if (isDebug()) {
            Slog.v(TAG, "onPackageRestartedLocked()");
        }
        destroyAndRebindRemoteService();
    }

    public final void destroyAndRebindRemoteService() {
        if (this.mRemoteService == null) {
            return;
        }
        if (isDebug()) {
            Slog.d(TAG, "Destroying the old remote service.");
        }
        this.mRemoteService.destroy();
        this.mRemoteService = null;
        synchronized (this.mLock) {
            this.mZombie = true;
        }
        RemoteAppPredictionService remoteServiceLocked = getRemoteServiceLocked();
        this.mRemoteService = remoteServiceLocked;
        if (remoteServiceLocked != null) {
            if (isDebug()) {
                Slog.d(TAG, "Rebinding to the new remote service.");
            }
            this.mRemoteService.reconnect();
        }
    }

    public final void resurrectSessionsLocked() {
        int size = this.mSessionInfos.size();
        if (isDebug()) {
            String str = TAG;
            Slog.d(str, "Resurrecting remote service (" + this.mRemoteService + ") on " + size + " sessions.");
        }
        for (AppPredictionSessionInfo appPredictionSessionInfo : this.mSessionInfos.values()) {
            appPredictionSessionInfo.resurrectSessionLocked(this, appPredictionSessionInfo.mToken);
        }
    }

    @GuardedBy({"mLock"})
    public boolean resolveService(AppPredictionSessionId appPredictionSessionId, boolean z, boolean z2, AbstractRemoteService.AsyncRequest<IPredictionService> asyncRequest) {
        if (z2) {
            IPredictionService iPredictionService = (IPredictionService) LocalServices.getService(PeopleServiceInternal.class);
            if (iPredictionService != null) {
                try {
                    asyncRequest.run(iPredictionService);
                } catch (RemoteException e) {
                    String str = TAG;
                    Slog.w(str, "Failed to invoke service:" + iPredictionService, e);
                }
            }
            return iPredictionService != null;
        }
        RemoteAppPredictionService remoteServiceLocked = getRemoteServiceLocked();
        if (remoteServiceLocked != null) {
            if (z) {
                remoteServiceLocked.executeOnResolvedService(asyncRequest);
            } else {
                remoteServiceLocked.scheduleOnResolvedService(asyncRequest);
            }
        }
        return remoteServiceLocked != null;
    }

    @GuardedBy({"mLock"})
    public final RemoteAppPredictionService getRemoteServiceLocked() {
        if (this.mRemoteService == null) {
            String componentNameLocked = getComponentNameLocked();
            if (componentNameLocked == null) {
                if (((AppPredictionManagerService) this.mMaster).verbose) {
                    Slog.v(TAG, "getRemoteServiceLocked(): not set");
                    return null;
                }
                return null;
            }
            this.mRemoteService = new RemoteAppPredictionService(getContext(), "android.service.appprediction.AppPredictionService", ComponentName.unflattenFromString(componentNameLocked), this.mUserId, this, ((AppPredictionManagerService) this.mMaster).isBindInstantServiceAllowed(), ((AppPredictionManagerService) this.mMaster).verbose);
        }
        return this.mRemoteService;
    }

    /* loaded from: classes.dex */
    public static final class AppPredictionSessionInfo {
        public final RemoteCallbackList<IPredictionCallback> mCallbacks = new RemoteCallbackList<>();
        public final IBinder.DeathRecipient mDeathRecipient;
        public final AppPredictionContext mPredictionContext;
        public final AppPredictionSessionId mSessionId;
        public final IBinder mToken;
        public final boolean mUsesPeopleService;

        public AppPredictionSessionInfo(AppPredictionSessionId appPredictionSessionId, AppPredictionContext appPredictionContext, boolean z, IBinder iBinder, IBinder.DeathRecipient deathRecipient) {
            this.mSessionId = appPredictionSessionId;
            this.mPredictionContext = appPredictionContext;
            this.mUsesPeopleService = z;
            this.mToken = iBinder;
            this.mDeathRecipient = deathRecipient;
        }

        public void addCallbackLocked(IPredictionCallback iPredictionCallback) {
            this.mCallbacks.register(iPredictionCallback);
        }

        public void removeCallbackLocked(IPredictionCallback iPredictionCallback) {
            this.mCallbacks.unregister(iPredictionCallback);
        }

        public boolean linkToDeath() {
            try {
                this.mToken.linkToDeath(this.mDeathRecipient, 0);
                return true;
            } catch (RemoteException unused) {
                return false;
            }
        }

        public void destroy() {
            IBinder iBinder = this.mToken;
            if (iBinder != null) {
                iBinder.unlinkToDeath(this.mDeathRecipient, 0);
            }
            this.mCallbacks.kill();
        }

        public void resurrectSessionLocked(final AppPredictionPerUserService appPredictionPerUserService, IBinder iBinder) {
            this.mCallbacks.getRegisteredCallbackCount();
            appPredictionPerUserService.onCreatePredictionSessionLocked(this.mPredictionContext, this.mSessionId, iBinder);
            this.mCallbacks.broadcast(new Consumer() { // from class: com.android.server.appprediction.AppPredictionPerUserService$AppPredictionSessionInfo$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    AppPredictionPerUserService.AppPredictionSessionInfo.this.lambda$resurrectSessionLocked$0(appPredictionPerUserService, (IPredictionCallback) obj);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$resurrectSessionLocked$0(AppPredictionPerUserService appPredictionPerUserService, IPredictionCallback iPredictionCallback) {
            appPredictionPerUserService.registerPredictionUpdatesLocked(this.mSessionId, iPredictionCallback);
        }
    }
}
