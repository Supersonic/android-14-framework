package com.android.server.ambientcontext;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.ActivityTaskManager;
import android.app.AppGlobals;
import android.app.BroadcastOptions;
import android.app.PendingIntent;
import android.app.ambientcontext.AmbientContextEvent;
import android.app.ambientcontext.AmbientContextEventRequest;
import android.app.ambientcontext.AmbientContextManager;
import android.app.ambientcontext.IAmbientContextObserver;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.service.ambientcontext.AmbientContextDetectionResult;
import android.service.ambientcontext.AmbientContextDetectionServiceStatus;
import android.text.TextUtils;
import android.util.IndentingPrintWriter;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.infra.AbstractPerUserSystemService;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public abstract class AmbientContextManagerPerUserService extends AbstractPerUserSystemService<AmbientContextManagerPerUserService, AmbientContextManagerService> {
    public static final String TAG = "AmbientContextManagerPerUserService";

    /* loaded from: classes.dex */
    public enum ServiceType {
        DEFAULT,
        WEARABLE
    }

    public abstract void clearRemoteService();

    public abstract void ensureRemoteServiceInitiated();

    public abstract int getAmbientContextEventArrayExtraKeyConfig();

    public abstract int getAmbientContextPackageNameExtraKeyConfig();

    public abstract ComponentName getComponentName();

    public abstract int getConsentComponentConfig();

    public abstract String getProtectedBindPermission();

    public abstract RemoteAmbientDetectionService getRemoteService();

    public abstract ServiceType getServiceType();

    public abstract void setComponentName(ComponentName componentName);

    public AmbientContextManagerPerUserService(AmbientContextManagerService ambientContextManagerService, Object obj, int i) {
        super(ambientContextManagerService, obj, i);
    }

    public void onQueryServiceStatus(int[] iArr, String str, final RemoteCallback remoteCallback) {
        String str2 = TAG;
        Slog.d(str2, "Query event status of " + Arrays.toString(iArr) + " for " + str);
        synchronized (this.mLock) {
            if (!setUpServiceIfNeeded()) {
                Slog.w(str2, "Detection service is not available at this moment.");
                sendStatusCallback(remoteCallback, 3);
                return;
            }
            ensureRemoteServiceInitiated();
            getRemoteService().queryServiceStatus(iArr, str, getServerStatusCallback(new Consumer() { // from class: com.android.server.ambientcontext.AmbientContextManagerPerUserService$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    AmbientContextManagerPerUserService.this.lambda$onQueryServiceStatus$0(remoteCallback, (Integer) obj);
                }
            }));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onQueryServiceStatus$0(RemoteCallback remoteCallback, Integer num) {
        sendStatusCallback(remoteCallback, num.intValue());
    }

    public void onUnregisterObserver(String str) {
        synchronized (this.mLock) {
            stopDetection(str);
            ((AmbientContextManagerService) this.mMaster).clientRemoved(this.mUserId, str);
        }
    }

    public void onStartConsentActivity(int[] iArr, String str) {
        String str2 = TAG;
        Slog.d(str2, "Opening consent activity of " + Arrays.toString(iArr) + " for " + str);
        try {
            ParceledListSlice recentTasks = ActivityTaskManager.getService().getRecentTasks(1, 0, getUserId());
            if (recentTasks == null || recentTasks.getList().isEmpty()) {
                Slog.e(str2, "Recent task list is empty!");
                return;
            }
            ActivityManager.RecentTaskInfo recentTaskInfo = (ActivityManager.RecentTaskInfo) recentTasks.getList().get(0);
            if (!str.equals(recentTaskInfo.topActivityInfo.packageName)) {
                Slog.e(str2, "Recent task package name: " + recentTaskInfo.topActivityInfo.packageName + " doesn't match with client package name: " + str);
                return;
            }
            ComponentName consentComponent = getConsentComponent();
            if (consentComponent == null) {
                Slog.e(str2, "Consent component not found!");
                return;
            }
            Slog.d(str2, "Starting consent activity for " + str);
            Intent intent = new Intent();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                try {
                    Context context = getContext();
                    String string = context.getResources().getString(getAmbientContextPackageNameExtraKeyConfig());
                    String string2 = context.getResources().getString(getAmbientContextEventArrayExtraKeyConfig());
                    intent.setComponent(consentComponent);
                    if (string != null) {
                        intent.putExtra(string, str);
                    } else {
                        Slog.d(str2, "Missing packageNameExtraKey for consent activity");
                    }
                    if (string2 != null) {
                        intent.putExtra(string2, iArr);
                    } else {
                        Slog.d(str2, "Missing eventArrayExtraKey for consent activity");
                    }
                    ActivityOptions makeBasic = ActivityOptions.makeBasic();
                    makeBasic.setLaunchTaskId(recentTaskInfo.taskId);
                    context.startActivityAsUser(intent, makeBasic.toBundle(), context.getUser());
                } catch (ActivityNotFoundException unused) {
                    Slog.e(TAG, "unable to start consent activity");
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        } catch (RemoteException unused2) {
            Slog.e(TAG, "Failed to query recent tasks!");
        }
    }

    public void onRegisterObserver(AmbientContextEventRequest ambientContextEventRequest, String str, IAmbientContextObserver iAmbientContextObserver) {
        synchronized (this.mLock) {
            if (!setUpServiceIfNeeded()) {
                Slog.w(TAG, "Detection service is not available at this moment.");
                completeRegistration(iAmbientContextObserver, 3);
                return;
            }
            startDetection(ambientContextEventRequest, str, iAmbientContextObserver);
            ((AmbientContextManagerService) this.mMaster).newClientAdded(this.mUserId, ambientContextEventRequest, str, iAmbientContextObserver);
        }
    }

    @Override // com.android.server.infra.AbstractPerUserSystemService
    public ServiceInfo newServiceInfoLocked(ComponentName componentName) throws PackageManager.NameNotFoundException {
        String str = TAG;
        Slog.d(str, "newServiceInfoLocked with component name: " + componentName.getClassName());
        if (getComponentName() == null || !componentName.getClassName().equals(getComponentName().getClassName())) {
            Slog.d(str, "service name does not match this per user, returning...");
            return null;
        }
        try {
            ServiceInfo serviceInfo = AppGlobals.getPackageManager().getServiceInfo(componentName, 0L, this.mUserId);
            if (serviceInfo != null && !getProtectedBindPermission().equals(serviceInfo.permission)) {
                throw new SecurityException(String.format("Service %s requires %s permission. Found %s permission", serviceInfo.getComponentName(), getProtectedBindPermission(), serviceInfo.permission));
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
        RemoteAmbientDetectionService remoteService = getRemoteService();
        if (remoteService != null) {
            remoteService.dump("", new IndentingPrintWriter(printWriter, "  "));
        }
    }

    @VisibleForTesting
    public void stopDetection(String str) {
        String str2 = TAG;
        Slog.d(str2, "Stop detection for " + str);
        synchronized (this.mLock) {
            if (getComponentName() != null) {
                ensureRemoteServiceInitiated();
                getRemoteService().stopDetection(str);
            }
        }
    }

    public void destroyLocked() {
        Slog.d(TAG, "Trying to cancel the remote request. Reason: Service destroyed.");
        RemoteAmbientDetectionService remoteService = getRemoteService();
        if (remoteService != null) {
            synchronized (this.mLock) {
                remoteService.unbind();
                clearRemoteService();
            }
        }
    }

    public void startDetection(AmbientContextEventRequest ambientContextEventRequest, String str, final IAmbientContextObserver iAmbientContextObserver) {
        String str2 = TAG;
        Slog.d(str2, "Requested detection of " + ambientContextEventRequest.getEventTypes());
        synchronized (this.mLock) {
            if (setUpServiceIfNeeded()) {
                ensureRemoteServiceInitiated();
                getRemoteService().startDetection(ambientContextEventRequest, str, createDetectionResultRemoteCallback(), getServerStatusCallback(new Consumer() { // from class: com.android.server.ambientcontext.AmbientContextManagerPerUserService$$ExternalSyntheticLambda1
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        AmbientContextManagerPerUserService.this.lambda$startDetection$1(iAmbientContextObserver, (Integer) obj);
                    }
                }));
            } else {
                Slog.w(str2, "No valid component found for AmbientContextDetectionService");
                completeRegistration(iAmbientContextObserver, 2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startDetection$1(IAmbientContextObserver iAmbientContextObserver, Integer num) {
        completeRegistration(iAmbientContextObserver, num.intValue());
    }

    public void completeRegistration(IAmbientContextObserver iAmbientContextObserver, int i) {
        try {
            iAmbientContextObserver.onRegistrationComplete(i);
        } catch (RemoteException e) {
            String str = TAG;
            Slog.w(str, "Failed to call IAmbientContextObserver.onRegistrationComplete: " + e.getMessage());
        }
    }

    public void sendStatusCallback(RemoteCallback remoteCallback, @AmbientContextManager.StatusCode int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("android.app.ambientcontext.AmbientContextStatusBundleKey", i);
        remoteCallback.sendResult(bundle);
    }

    public void sendDetectionResultIntent(PendingIntent pendingIntent, List<AmbientContextEvent> list) {
        Intent intent = new Intent();
        intent.putExtra("android.app.ambientcontext.extra.AMBIENT_CONTEXT_EVENTS", new ArrayList(list));
        BroadcastOptions makeBasic = BroadcastOptions.makeBasic();
        makeBasic.setPendingIntentBackgroundActivityLaunchAllowed(false);
        try {
            pendingIntent.send(getContext(), 0, intent, null, null, null, makeBasic.toBundle());
            String str = TAG;
            Slog.i(str, "Sending PendingIntent to " + pendingIntent.getCreatorPackage() + ": " + list);
        } catch (PendingIntent.CanceledException unused) {
            String str2 = TAG;
            Slog.w(str2, "Couldn't deliver pendingIntent:" + pendingIntent);
        }
    }

    public RemoteCallback createDetectionResultRemoteCallback() {
        return new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: com.android.server.ambientcontext.AmbientContextManagerPerUserService$$ExternalSyntheticLambda3
            public final void onResult(Bundle bundle) {
                AmbientContextManagerPerUserService.this.lambda$createDetectionResultRemoteCallback$2(bundle);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createDetectionResultRemoteCallback$2(Bundle bundle) {
        AmbientContextDetectionResult ambientContextDetectionResult = (AmbientContextDetectionResult) bundle.get("android.app.ambientcontext.AmbientContextDetectionResultBundleKey");
        String packageName = ambientContextDetectionResult.getPackageName();
        IAmbientContextObserver clientRequestObserver = ((AmbientContextManagerService) this.mMaster).getClientRequestObserver(this.mUserId, packageName);
        if (clientRequestObserver == null) {
            return;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                clientRequestObserver.onEvents(ambientContextDetectionResult.getEvents());
                String str = TAG;
                Slog.i(str, "Got detection result of " + ambientContextDetectionResult.getEvents() + " for " + packageName);
            } catch (RemoteException e) {
                String str2 = TAG;
                Slog.w(str2, "Failed to call IAmbientContextObserver.onEvents: " + e.getMessage());
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @GuardedBy({"mLock"})
    @VisibleForTesting
    private boolean setUpServiceIfNeeded() {
        if (getComponentName() == null) {
            ComponentName[] updateServiceInfoListLocked = updateServiceInfoListLocked();
            if (updateServiceInfoListLocked == null || updateServiceInfoListLocked.length != 2) {
                Slog.d(TAG, "updateServiceInfoListLocked returned incorrect componentNames");
                return false;
            }
            int i = C04281.f1123x6db1e84a[getServiceType().ordinal()];
            if (i == 1) {
                setComponentName(updateServiceInfoListLocked[0]);
            } else if (i == 2) {
                setComponentName(updateServiceInfoListLocked[1]);
            } else {
                Slog.d(TAG, "updateServiceInfoListLocked returned unknown service types.");
                return false;
            }
        }
        if (getComponentName() == null) {
            return false;
        }
        try {
            return AppGlobals.getPackageManager().getServiceInfo(getComponentName(), 0L, this.mUserId) != null;
        } catch (RemoteException unused) {
            Slog.w(TAG, "RemoteException while setting up service");
            return false;
        }
    }

    /* renamed from: com.android.server.ambientcontext.AmbientContextManagerPerUserService$1 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C04281 {

        /* renamed from: $SwitchMap$com$android$server$ambientcontext$AmbientContextManagerPerUserService$ServiceType */
        public static final /* synthetic */ int[] f1123x6db1e84a;

        static {
            int[] iArr = new int[ServiceType.values().length];
            f1123x6db1e84a = iArr;
            try {
                iArr[ServiceType.DEFAULT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                f1123x6db1e84a[ServiceType.WEARABLE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    public final RemoteCallback getServerStatusCallback(final Consumer<Integer> consumer) {
        return new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: com.android.server.ambientcontext.AmbientContextManagerPerUserService$$ExternalSyntheticLambda2
            public final void onResult(Bundle bundle) {
                AmbientContextManagerPerUserService.lambda$getServerStatusCallback$3(consumer, bundle);
            }
        });
    }

    public static /* synthetic */ void lambda$getServerStatusCallback$3(Consumer consumer, Bundle bundle) {
        AmbientContextDetectionServiceStatus ambientContextDetectionServiceStatus = (AmbientContextDetectionServiceStatus) bundle.get("android.app.ambientcontext.AmbientContextServiceStatusBundleKey");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            int statusCode = ambientContextDetectionServiceStatus.getStatusCode();
            consumer.accept(Integer.valueOf(statusCode));
            String str = TAG;
            Slog.i(str, "Got detection status of " + statusCode + " for " + ambientContextDetectionServiceStatus.getPackageName());
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final ComponentName getConsentComponent() {
        String string = getContext().getResources().getString(getConsentComponentConfig());
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        String str = TAG;
        Slog.i(str, "Consent component name: " + string);
        return ComponentName.unflattenFromString(string);
    }
}
