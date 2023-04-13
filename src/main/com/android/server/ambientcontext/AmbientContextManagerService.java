package com.android.server.ambientcontext;

import android.app.PendingIntent;
import android.app.ambientcontext.AmbientContextEvent;
import android.app.ambientcontext.AmbientContextEventRequest;
import android.app.ambientcontext.IAmbientContextManager;
import android.app.ambientcontext.IAmbientContextObserver;
import android.content.ComponentName;
import android.content.Context;
import android.content.p000pm.PackageManagerInternal;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.os.UserHandle;
import android.provider.DeviceConfig;
import android.util.ArraySet;
import android.util.Slog;
import com.android.internal.util.DumpUtils;
import com.android.server.LocalServices;
import com.android.server.ambientcontext.AmbientContextManagerPerUserService;
import com.android.server.infra.AbstractMasterSystemService;
import com.android.server.infra.FrameworkResourcesServiceNameResolver;
import com.google.android.collect.Sets;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes.dex */
public class AmbientContextManagerService extends AbstractMasterSystemService<AmbientContextManagerService, AmbientContextManagerPerUserService> {
    public static final Set<Integer> DEFAULT_EVENT_SET = Sets.newHashSet(new Integer[]{1, 2, 3});
    public static final String TAG = "AmbientContextManagerService";
    public final Context mContext;
    public Set<ClientRequest> mExistingClientRequests;
    public boolean mIsServiceEnabled;
    public boolean mIsWearableServiceEnabled;

    @Override // com.android.server.infra.AbstractMasterSystemService
    public int getMaximumTemporaryServiceDurationMs() {
        return 30000;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.server.infra.AbstractMasterSystemService
    public AmbientContextManagerPerUserService newServiceLocked(int i, boolean z) {
        return null;
    }

    /* loaded from: classes.dex */
    public static class ClientRequest {
        public final IAmbientContextObserver mObserver;
        public final String mPackageName;
        public final AmbientContextEventRequest mRequest;
        public final int mUserId;

        public ClientRequest(int i, AmbientContextEventRequest ambientContextEventRequest, String str, IAmbientContextObserver iAmbientContextObserver) {
            this.mUserId = i;
            this.mRequest = ambientContextEventRequest;
            this.mPackageName = str;
            this.mObserver = iAmbientContextObserver;
        }

        public String getPackageName() {
            return this.mPackageName;
        }

        public AmbientContextEventRequest getRequest() {
            return this.mRequest;
        }

        public IAmbientContextObserver getObserver() {
            return this.mObserver;
        }

        public boolean hasUserId(int i) {
            return this.mUserId == i;
        }

        public boolean hasUserIdAndPackageName(int i, String str) {
            return i == this.mUserId && str.equals(getPackageName());
        }
    }

    public AmbientContextManagerService(Context context) {
        super(context, new FrameworkResourcesServiceNameResolver(context, 17236017, true), null, 68);
        this.mContext = context;
        this.mExistingClientRequests = new ArraySet();
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("ambient_context", new AmbientContextManagerInternal());
    }

    @Override // com.android.server.infra.AbstractMasterSystemService, com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 500) {
            DeviceConfig.addOnPropertiesChangedListener("ambient_context_manager_service", getContext().getMainExecutor(), new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.ambientcontext.AmbientContextManagerService$$ExternalSyntheticLambda0
                public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                    AmbientContextManagerService.this.lambda$onBootPhase$0(properties);
                }
            });
            this.mIsServiceEnabled = DeviceConfig.getBoolean("ambient_context_manager_service", "service_enabled", true);
            this.mIsWearableServiceEnabled = DeviceConfig.getBoolean("wearable_sensing", "service_enabled", true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBootPhase$0(DeviceConfig.Properties properties) {
        onDeviceConfigChange(properties.getKeyset());
    }

    public void newClientAdded(int i, AmbientContextEventRequest ambientContextEventRequest, String str, IAmbientContextObserver iAmbientContextObserver) {
        String str2 = TAG;
        Slog.d(str2, "New client added: " + str);
        this.mExistingClientRequests.removeAll(findExistingRequests(i, str));
        this.mExistingClientRequests.add(new ClientRequest(i, ambientContextEventRequest, str, iAmbientContextObserver));
    }

    public void clientRemoved(int i, String str) {
        String str2 = TAG;
        Slog.d(str2, "Remove client: " + str);
        this.mExistingClientRequests.removeAll(findExistingRequests(i, str));
    }

    public final Set<ClientRequest> findExistingRequests(int i, String str) {
        ArraySet arraySet = new ArraySet();
        for (ClientRequest clientRequest : this.mExistingClientRequests) {
            if (clientRequest.hasUserIdAndPackageName(i, str)) {
                arraySet.add(clientRequest);
            }
        }
        return arraySet;
    }

    public IAmbientContextObserver getClientRequestObserver(int i, String str) {
        for (ClientRequest clientRequest : this.mExistingClientRequests) {
            if (clientRequest.hasUserIdAndPackageName(i, str)) {
                return clientRequest.getObserver();
            }
        }
        return null;
    }

    public final void onDeviceConfigChange(Set<String> set) {
        if (set.contains("service_enabled")) {
            this.mIsServiceEnabled = DeviceConfig.getBoolean("ambient_context_manager_service", "service_enabled", true);
            this.mIsWearableServiceEnabled = DeviceConfig.getBoolean("wearable_sensing", "service_enabled", true);
        }
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public List<AmbientContextManagerPerUserService> newServiceListLocked(int i, boolean z, String[] strArr) {
        if (strArr == null || strArr.length == 0) {
            Slog.i(TAG, "serviceNames sent in newServiceListLocked is null, or empty");
            return new ArrayList();
        }
        ArrayList arrayList = new ArrayList(strArr.length);
        if (strArr.length == 2) {
            Slog.i(TAG, "Not using default services, services provided for testing should be exactly two services.");
            if (!isDefaultService(strArr[0]) && !isDefaultWearableService(strArr[1])) {
                arrayList.add(new DefaultAmbientContextManagerPerUserService(this, this.mLock, i, AmbientContextManagerPerUserService.ServiceType.DEFAULT, strArr[0]));
                arrayList.add(new WearableAmbientContextManagerPerUserService(this, this.mLock, i, AmbientContextManagerPerUserService.ServiceType.WEARABLE, strArr[1]));
            }
            return arrayList;
        }
        Slog.i(TAG, "Incorrect number of services provided for testing.");
        for (String str : strArr) {
            Slog.d(TAG, "newServicesListLocked with service name: " + str);
            AmbientContextManagerPerUserService.ServiceType serviceType = getServiceType(str);
            AmbientContextManagerPerUserService.ServiceType serviceType2 = AmbientContextManagerPerUserService.ServiceType.WEARABLE;
            if (serviceType == serviceType2) {
                arrayList.add(new WearableAmbientContextManagerPerUserService(this, this.mLock, i, serviceType2, str));
            } else {
                arrayList.add(new DefaultAmbientContextManagerPerUserService(this, this.mLock, i, AmbientContextManagerPerUserService.ServiceType.DEFAULT, str));
            }
        }
        return arrayList;
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void onServiceRemoved(AmbientContextManagerPerUserService ambientContextManagerPerUserService, int i) {
        Slog.d(TAG, "onServiceRemoved");
        ambientContextManagerPerUserService.destroyLocked();
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void onServicePackageRestartedLocked(int i) {
        Slog.d(TAG, "Restoring remote request. Reason: Service package restarted.");
        restorePreviouslyEnabledClients(i);
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void onServicePackageUpdatedLocked(int i) {
        Slog.d(TAG, "Restoring remote request. Reason: Service package updated.");
        restorePreviouslyEnabledClients(i);
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void enforceCallingPermissionForManagement() {
        getContext().enforceCallingPermission("android.permission.ACCESS_AMBIENT_CONTEXT_EVENT", TAG);
    }

    public static boolean isDetectionServiceConfigured() {
        boolean z = ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).getKnownPackageNames(18, 0).length != 0;
        String str = TAG;
        Slog.i(str, "Detection service configured: " + z);
        return z;
    }

    public void startDetection(int i, AmbientContextEventRequest ambientContextEventRequest, String str, IAmbientContextObserver iAmbientContextObserver) {
        Context context = this.mContext;
        String str2 = TAG;
        context.enforceCallingOrSelfPermission("android.permission.ACCESS_AMBIENT_CONTEXT_EVENT", str2);
        synchronized (this.mLock) {
            AmbientContextManagerPerUserService ambientContextManagerPerUserServiceForEventTypes = getAmbientContextManagerPerUserServiceForEventTypes(i, ambientContextEventRequest.getEventTypes());
            if (ambientContextManagerPerUserServiceForEventTypes != null) {
                ambientContextManagerPerUserServiceForEventTypes.startDetection(ambientContextEventRequest, str, iAmbientContextObserver);
            } else {
                Slog.i(str2, "service not available for user_id: " + i);
            }
        }
    }

    public void stopAmbientContextEvent(int i, String str) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_AMBIENT_CONTEXT_EVENT", TAG);
        synchronized (this.mLock) {
            for (ClientRequest clientRequest : this.mExistingClientRequests) {
                String str2 = TAG;
                Slog.i(str2, "Looping through clients");
                if (clientRequest.hasUserIdAndPackageName(i, str)) {
                    Slog.i(str2, "we have an existing client");
                    AmbientContextManagerPerUserService ambientContextManagerPerUserServiceForEventTypes = getAmbientContextManagerPerUserServiceForEventTypes(i, clientRequest.getRequest().getEventTypes());
                    if (ambientContextManagerPerUserServiceForEventTypes != null) {
                        ambientContextManagerPerUserServiceForEventTypes.stopDetection(str);
                    } else {
                        Slog.i(str2, "service not available for user_id: " + i);
                    }
                }
            }
        }
    }

    public void queryServiceStatus(int i, String str, int[] iArr, RemoteCallback remoteCallback) {
        Context context = this.mContext;
        String str2 = TAG;
        context.enforceCallingOrSelfPermission("android.permission.ACCESS_AMBIENT_CONTEXT_EVENT", str2);
        synchronized (this.mLock) {
            AmbientContextManagerPerUserService ambientContextManagerPerUserServiceForEventTypes = getAmbientContextManagerPerUserServiceForEventTypes(i, intArrayToIntegerSet(iArr));
            if (ambientContextManagerPerUserServiceForEventTypes != null) {
                ambientContextManagerPerUserServiceForEventTypes.onQueryServiceStatus(iArr, str, remoteCallback);
            } else {
                Slog.i(str2, "query service not available for user_id: " + i);
            }
        }
    }

    public final void restorePreviouslyEnabledClients(int i) {
        synchronized (this.mLock) {
            for (AmbientContextManagerPerUserService ambientContextManagerPerUserService : getServiceListForUserLocked(i)) {
                for (ClientRequest clientRequest : this.mExistingClientRequests) {
                    if (clientRequest.hasUserId(i)) {
                        String str = TAG;
                        Slog.d(str, "Restoring detection for " + clientRequest.getPackageName());
                        ambientContextManagerPerUserService.startDetection(clientRequest.getRequest(), clientRequest.getPackageName(), clientRequest.getObserver());
                    }
                }
            }
        }
    }

    public ComponentName getComponentName(int i, AmbientContextManagerPerUserService.ServiceType serviceType) {
        synchronized (this.mLock) {
            AmbientContextManagerPerUserService serviceForType = getServiceForType(i, serviceType);
            if (serviceForType != null) {
                return serviceForType.getComponentName();
            }
            return null;
        }
    }

    public final AmbientContextManagerPerUserService getAmbientContextManagerPerUserServiceForEventTypes(int i, Set<Integer> set) {
        if (isWearableEventTypesOnly(set)) {
            return getServiceForType(i, AmbientContextManagerPerUserService.ServiceType.WEARABLE);
        }
        return getServiceForType(i, AmbientContextManagerPerUserService.ServiceType.DEFAULT);
    }

    public final AmbientContextManagerPerUserService.ServiceType getServiceType(String str) {
        String string = this.mContext.getResources().getString(17039909);
        if (string != null && string.equals(str)) {
            return AmbientContextManagerPerUserService.ServiceType.WEARABLE;
        }
        return AmbientContextManagerPerUserService.ServiceType.DEFAULT;
    }

    public final boolean isDefaultService(String str) {
        String string = this.mContext.getResources().getString(17039870);
        return string != null && string.equals(str);
    }

    public final boolean isDefaultWearableService(String str) {
        String string = this.mContext.getResources().getString(17039909);
        return string != null && string.equals(str);
    }

    public final AmbientContextManagerPerUserService getServiceForType(int i, AmbientContextManagerPerUserService.ServiceType serviceType) {
        String str = TAG;
        Slog.d(str, "getServiceForType with userid: " + i + " service type: " + serviceType.name());
        synchronized (this.mLock) {
            List<AmbientContextManagerPerUserService> serviceListForUserLocked = getServiceListForUserLocked(i);
            StringBuilder sb = new StringBuilder();
            sb.append("Services that are available: ");
            sb.append(serviceListForUserLocked == null ? "null services" : serviceListForUserLocked.size() + " number of services");
            Slog.d(str, sb.toString());
            if (serviceListForUserLocked == null) {
                return null;
            }
            for (AmbientContextManagerPerUserService ambientContextManagerPerUserService : serviceListForUserLocked) {
                if (ambientContextManagerPerUserService.getServiceType() == serviceType) {
                    return ambientContextManagerPerUserService;
                }
            }
            return null;
        }
    }

    public final boolean isWearableEventTypesOnly(Set<Integer> set) {
        if (set.isEmpty()) {
            Slog.d(TAG, "empty event types.");
            return false;
        }
        for (Integer num : set) {
            if (num.intValue() < 100000) {
                Slog.d(TAG, "Not all events types are wearable events.");
                return false;
            }
        }
        Slog.d(TAG, "only wearable events.");
        return true;
    }

    public final boolean isWearableEventTypesOnly(int[] iArr) {
        return isWearableEventTypesOnly(new HashSet(Arrays.asList(intArrayToIntegerArray(iArr))));
    }

    public final boolean containsMixedEvents(int[] iArr) {
        if (isWearableEventTypesOnly(iArr)) {
            return false;
        }
        for (int i : iArr) {
            if (!DEFAULT_EVENT_SET.contains(Integer.valueOf(i))) {
                Slog.w(TAG, "Received mixed event types, this is not supported.");
                return true;
            }
        }
        return false;
    }

    public static int[] integerSetToIntArray(Set<Integer> set) {
        int[] iArr = new int[set.size()];
        int i = 0;
        for (Integer num : set) {
            iArr[i] = num.intValue();
            i++;
        }
        return iArr;
    }

    public final Set<Integer> intArrayToIntegerSet(int[] iArr) {
        HashSet hashSet = new HashSet();
        for (int i : iArr) {
            hashSet.add(Integer.valueOf(i));
        }
        return hashSet;
    }

    public static Integer[] intArrayToIntegerArray(int[] iArr) {
        Integer[] numArr = new Integer[iArr.length];
        int length = iArr.length;
        int i = 0;
        int i2 = 0;
        while (i < length) {
            numArr[i2] = Integer.valueOf(iArr[i]);
            i++;
            i2++;
        }
        return numArr;
    }

    /* loaded from: classes.dex */
    public final class AmbientContextManagerInternal extends IAmbientContextManager.Stub {
        public AmbientContextManagerInternal() {
        }

        public void registerObserver(AmbientContextEventRequest ambientContextEventRequest, final PendingIntent pendingIntent, final RemoteCallback remoteCallback) {
            Objects.requireNonNull(ambientContextEventRequest);
            Objects.requireNonNull(pendingIntent);
            Objects.requireNonNull(remoteCallback);
            final AmbientContextManagerPerUserService ambientContextManagerPerUserServiceForEventTypes = AmbientContextManagerService.this.getAmbientContextManagerPerUserServiceForEventTypes(UserHandle.getCallingUserId(), ambientContextEventRequest.getEventTypes());
            registerObserverWithCallback(ambientContextEventRequest, pendingIntent.getCreatorPackage(), new IAmbientContextObserver.Stub() { // from class: com.android.server.ambientcontext.AmbientContextManagerService.AmbientContextManagerInternal.1
                public void onEvents(List<AmbientContextEvent> list) throws RemoteException {
                    ambientContextManagerPerUserServiceForEventTypes.sendDetectionResultIntent(pendingIntent, list);
                }

                public void onRegistrationComplete(int i) throws RemoteException {
                    ambientContextManagerPerUserServiceForEventTypes.sendStatusCallback(remoteCallback, i);
                }
            });
        }

        public void registerObserverWithCallback(AmbientContextEventRequest ambientContextEventRequest, String str, IAmbientContextObserver iAmbientContextObserver) {
            Slog.i(AmbientContextManagerService.TAG, "AmbientContextManagerService registerObserverWithCallback.");
            Objects.requireNonNull(ambientContextEventRequest);
            Objects.requireNonNull(str);
            Objects.requireNonNull(iAmbientContextObserver);
            AmbientContextManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_AMBIENT_CONTEXT_EVENT", AmbientContextManagerService.TAG);
            AmbientContextManagerService.this.assertCalledByPackageOwner(str);
            AmbientContextManagerPerUserService ambientContextManagerPerUserServiceForEventTypes = AmbientContextManagerService.this.getAmbientContextManagerPerUserServiceForEventTypes(UserHandle.getCallingUserId(), ambientContextEventRequest.getEventTypes());
            if (ambientContextManagerPerUserServiceForEventTypes == null) {
                String str2 = AmbientContextManagerService.TAG;
                Slog.w(str2, "onRegisterObserver unavailable user_id: " + UserHandle.getCallingUserId());
                return;
            }
            int checkStatusCode = checkStatusCode(ambientContextManagerPerUserServiceForEventTypes, AmbientContextManagerService.integerSetToIntArray(ambientContextEventRequest.getEventTypes()));
            if (checkStatusCode == 1) {
                ambientContextManagerPerUserServiceForEventTypes.onRegisterObserver(ambientContextEventRequest, str, iAmbientContextObserver);
            } else {
                ambientContextManagerPerUserServiceForEventTypes.completeRegistration(iAmbientContextObserver, checkStatusCode);
            }
        }

        public void unregisterObserver(String str) {
            AmbientContextManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_AMBIENT_CONTEXT_EVENT", AmbientContextManagerService.TAG);
            AmbientContextManagerService.this.assertCalledByPackageOwner(str);
            synchronized (AmbientContextManagerService.this.mLock) {
                for (ClientRequest clientRequest : AmbientContextManagerService.this.mExistingClientRequests) {
                    if (clientRequest.getPackageName().equals(str)) {
                        AmbientContextManagerPerUserService ambientContextManagerPerUserServiceForEventTypes = AmbientContextManagerService.this.getAmbientContextManagerPerUserServiceForEventTypes(UserHandle.getCallingUserId(), clientRequest.getRequest().getEventTypes());
                        if (ambientContextManagerPerUserServiceForEventTypes != null) {
                            ambientContextManagerPerUserServiceForEventTypes.onUnregisterObserver(str);
                        } else {
                            String str2 = AmbientContextManagerService.TAG;
                            Slog.w(str2, "onUnregisterObserver unavailable user_id: " + UserHandle.getCallingUserId());
                        }
                    }
                }
            }
        }

        public void queryServiceStatus(int[] iArr, String str, RemoteCallback remoteCallback) {
            Objects.requireNonNull(iArr);
            Objects.requireNonNull(str);
            Objects.requireNonNull(remoteCallback);
            AmbientContextManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_AMBIENT_CONTEXT_EVENT", AmbientContextManagerService.TAG);
            AmbientContextManagerService.this.assertCalledByPackageOwner(str);
            synchronized (AmbientContextManagerService.this.mLock) {
                AmbientContextManagerPerUserService ambientContextManagerPerUserServiceForEventTypes = AmbientContextManagerService.this.getAmbientContextManagerPerUserServiceForEventTypes(UserHandle.getCallingUserId(), AmbientContextManagerService.this.intArrayToIntegerSet(iArr));
                if (ambientContextManagerPerUserServiceForEventTypes == null) {
                    String str2 = AmbientContextManagerService.TAG;
                    Slog.w(str2, "queryServiceStatus unavailable user_id: " + UserHandle.getCallingUserId());
                    return;
                }
                int checkStatusCode = checkStatusCode(ambientContextManagerPerUserServiceForEventTypes, iArr);
                if (checkStatusCode == 1) {
                    ambientContextManagerPerUserServiceForEventTypes.onQueryServiceStatus(iArr, str, remoteCallback);
                } else {
                    ambientContextManagerPerUserServiceForEventTypes.sendStatusCallback(remoteCallback, checkStatusCode);
                }
            }
        }

        public void startConsentActivity(int[] iArr, String str) {
            Objects.requireNonNull(iArr);
            Objects.requireNonNull(str);
            AmbientContextManagerService.this.assertCalledByPackageOwner(str);
            AmbientContextManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_AMBIENT_CONTEXT_EVENT", AmbientContextManagerService.TAG);
            if (AmbientContextManagerService.this.containsMixedEvents(iArr)) {
                Slog.d(AmbientContextManagerService.TAG, "AmbientContextEventRequest contains mixed events, this is not supported.");
                return;
            }
            AmbientContextManagerPerUserService ambientContextManagerPerUserServiceForEventTypes = AmbientContextManagerService.this.getAmbientContextManagerPerUserServiceForEventTypes(UserHandle.getCallingUserId(), AmbientContextManagerService.this.intArrayToIntegerSet(iArr));
            if (ambientContextManagerPerUserServiceForEventTypes != null) {
                ambientContextManagerPerUserServiceForEventTypes.onStartConsentActivity(iArr, str);
                return;
            }
            String str2 = AmbientContextManagerService.TAG;
            Slog.w(str2, "startConsentActivity unavailable user_id: " + UserHandle.getCallingUserId());
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            if (DumpUtils.checkDumpPermission(AmbientContextManagerService.this.mContext, AmbientContextManagerService.TAG, printWriter)) {
                synchronized (AmbientContextManagerService.this.mLock) {
                    AmbientContextManagerService.this.dumpLocked("", printWriter);
                }
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
            new AmbientContextShellCommand(AmbientContextManagerService.this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
        }

        public final int checkStatusCode(AmbientContextManagerPerUserService ambientContextManagerPerUserService, int[] iArr) {
            if (ambientContextManagerPerUserService.getServiceType() == AmbientContextManagerPerUserService.ServiceType.DEFAULT && !AmbientContextManagerService.this.mIsServiceEnabled) {
                Slog.d(AmbientContextManagerService.TAG, "Service not enabled.");
                return 3;
            } else if (ambientContextManagerPerUserService.getServiceType() == AmbientContextManagerPerUserService.ServiceType.WEARABLE && !AmbientContextManagerService.this.mIsWearableServiceEnabled) {
                Slog.d(AmbientContextManagerService.TAG, "Wearable Service not available.");
                return 3;
            } else if (AmbientContextManagerService.this.containsMixedEvents(iArr)) {
                Slog.d(AmbientContextManagerService.TAG, "AmbientContextEventRequest contains mixed events, this is not supported.");
                return 2;
            } else {
                return 1;
            }
        }
    }
}
