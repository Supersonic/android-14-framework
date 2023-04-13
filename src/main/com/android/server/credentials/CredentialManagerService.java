package com.android.server.credentials;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.SigningInfo;
import android.credentials.ClearCredentialStateRequest;
import android.credentials.CreateCredentialRequest;
import android.credentials.CredentialDescription;
import android.credentials.CredentialOption;
import android.credentials.CredentialProviderInfo;
import android.credentials.GetCredentialRequest;
import android.credentials.IClearCredentialStateCallback;
import android.credentials.ICreateCredentialCallback;
import android.credentials.ICredentialManager;
import android.credentials.IGetCredentialCallback;
import android.credentials.ISetEnabledProvidersCallback;
import android.credentials.RegisterCredentialDescriptionRequest;
import android.credentials.UnregisterCredentialDescriptionRequest;
import android.credentials.ui.IntentFactory;
import android.os.Binder;
import android.os.CancellationSignal;
import android.os.ICancellationSignal;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.service.credentials.CallingAppInfo;
import android.service.credentials.CredentialProviderInfoFactory;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.SystemService;
import com.android.server.credentials.CredentialDescriptionRegistry;
import com.android.server.credentials.CredentialManagerService;
import com.android.server.credentials.metrics.ApiName;
import com.android.server.credentials.metrics.ApiStatus;
import com.android.server.credentials.metrics.InitialPhaseMetric;
import com.android.server.infra.AbstractMasterSystemService;
import com.android.server.infra.SecureSettingsServiceNameResolver;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/* loaded from: classes.dex */
public final class CredentialManagerService extends AbstractMasterSystemService<CredentialManagerService, CredentialManagerServiceImpl> {
    public final Context mContext;
    @GuardedBy({"mLock"})
    public final SparseArray<List<CredentialManagerServiceImpl>> mSystemServicesCacheList;

    @Override // com.android.server.infra.AbstractMasterSystemService
    public String getServiceSettingsProperty() {
        return "credential_service";
    }

    public CredentialManagerService(Context context) {
        super(context, new SecureSettingsServiceNameResolver(context, "credential_service", true), null, 4);
        this.mSystemServicesCacheList = new SparseArray<>();
        this.mContext = context;
    }

    @GuardedBy({"mLock"})
    public final List<CredentialManagerServiceImpl> constructSystemServiceListLocked(final int i) {
        final ArrayList arrayList = new ArrayList();
        CredentialProviderInfoFactory.getAvailableSystemServices(this.mContext, i, false, new HashSet()).forEach(new Consumer() { // from class: com.android.server.credentials.CredentialManagerService$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                CredentialManagerService.this.lambda$constructSystemServiceListLocked$0(arrayList, i, (CredentialProviderInfo) obj);
            }
        });
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$constructSystemServiceListLocked$0(List list, int i, CredentialProviderInfo credentialProviderInfo) {
        list.add(new CredentialManagerServiceImpl(this, this.mLock, i, credentialProviderInfo));
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.server.infra.AbstractMasterSystemService
    public CredentialManagerServiceImpl newServiceLocked(int i, boolean z) {
        Slog.w("CredManSysService", "Should not be here - CredentialManagerService is configured to use multiple services");
        return null;
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("credential", new CredentialManagerServiceStub());
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    @GuardedBy({"mLock"})
    public List<CredentialManagerServiceImpl> newServiceListLocked(int i, boolean z, String[] strArr) {
        getOrConstructSystemServiceListLock(i);
        if (strArr == null || strArr.length == 0) {
            Slog.i("CredManSysService", "serviceNames sent in newServiceListLocked is null, or empty");
            return new ArrayList();
        }
        ArrayList arrayList = new ArrayList(strArr.length);
        for (String str : strArr) {
            Log.i("CredManSysService", "in newServiceListLocked, service: " + str);
            if (!TextUtils.isEmpty(str)) {
                try {
                    arrayList.add(new CredentialManagerServiceImpl(this, this.mLock, i, str));
                } catch (PackageManager.NameNotFoundException | SecurityException e) {
                    Log.i("CredManSysService", "Unable to add serviceInfo : " + e.getMessage());
                }
            }
        }
        return arrayList;
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    @GuardedBy({"mLock"})
    public void handlePackageRemovedMultiModeLocked(String str, int i) {
        CredentialManagerServiceImpl credentialManagerServiceImpl;
        List<CredentialManagerServiceImpl> peekServiceListForUserLocked = peekServiceListForUserLocked(i);
        if (peekServiceListForUserLocked == null) {
            return;
        }
        Iterator<CredentialManagerServiceImpl> it = peekServiceListForUserLocked.iterator();
        while (true) {
            if (!it.hasNext()) {
                credentialManagerServiceImpl = null;
                break;
            }
            credentialManagerServiceImpl = it.next();
            if (credentialManagerServiceImpl != null) {
                ComponentName componentName = credentialManagerServiceImpl.getCredentialProviderInfo().getServiceInfo().getComponentName();
                if (str.equals(componentName.getPackageName())) {
                    removeServiceFromMultiModeSettings(componentName.flattenToString(), i);
                    break;
                }
            }
        }
        if (credentialManagerServiceImpl != null) {
            removeServiceFromCache(credentialManagerServiceImpl, i);
            CredentialDescriptionRegistry.forUser(i).evictProviderWithPackageName(credentialManagerServiceImpl.getServicePackageName());
        }
    }

    @GuardedBy({"mLock"})
    public final List<CredentialManagerServiceImpl> getOrConstructSystemServiceListLock(int i) {
        List<CredentialManagerServiceImpl> list = this.mSystemServicesCacheList.get(i);
        if (list == null || list.size() == 0) {
            List<CredentialManagerServiceImpl> constructSystemServiceListLocked = constructSystemServiceListLocked(i);
            this.mSystemServicesCacheList.put(i, constructSystemServiceListLocked);
            return constructSystemServiceListLocked;
        }
        return list;
    }

    public final boolean hasWriteSecureSettingsPermission() {
        return hasPermission("android.permission.WRITE_SECURE_SETTINGS");
    }

    public final void verifyGetProvidersPermission() throws SecurityException {
        if (!hasPermission("android.permission.QUERY_ALL_PACKAGES") && !hasPermission("android.permission.LIST_ENABLED_CREDENTIAL_PROVIDERS")) {
            throw new SecurityException("Caller is missing permission: QUERY_ALL_PACKAGES or LIST_ENABLED_CREDENTIAL_PROVIDERS");
        }
    }

    public final boolean hasPermission(String str) {
        boolean z = this.mContext.checkCallingOrSelfPermission(str) == 0;
        if (!z) {
            Slog.e("CredManSysService", "Caller does not have permission: " + str);
        }
        return z;
    }

    public final void runForUser(Consumer<CredentialManagerServiceImpl> consumer) {
        int callingUserId = UserHandle.getCallingUserId();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mLock) {
                for (CredentialManagerServiceImpl credentialManagerServiceImpl : getCredentialProviderServicesLocked(callingUserId)) {
                    consumer.accept(credentialManagerServiceImpl);
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @GuardedBy({"mLock"})
    public final List<CredentialManagerServiceImpl> getCredentialProviderServicesLocked(int i) {
        ArrayList arrayList = new ArrayList();
        List<CredentialManagerServiceImpl> serviceListForUserLocked = getServiceListForUserLocked(i);
        if (serviceListForUserLocked != null && !serviceListForUserLocked.isEmpty()) {
            arrayList.addAll(serviceListForUserLocked);
        }
        arrayList.addAll(getOrConstructSystemServiceListLock(i));
        return arrayList;
    }

    public static boolean isCredentialDescriptionApiEnabled() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return DeviceConfig.getBoolean("credential_manager", "enable_credential_description_api", false);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final List<ProviderSession> initiateProviderSessionsWithActiveContainers(GetRequestSession getRequestSession, Set<Pair<CredentialOption, CredentialDescriptionRegistry.FilterResult>> set) {
        ArrayList arrayList = new ArrayList();
        for (Pair<CredentialOption, CredentialDescriptionRegistry.FilterResult> pair : set) {
            arrayList.add(ProviderRegistryGetSession.createNewSession(this.mContext, UserHandle.getCallingUserId(), getRequestSession, CredentialProviderInfoFactory.getCredentialProviderFromPackageName(this.mContext, UserHandle.getCallingUserId(), ((CredentialDescriptionRegistry.FilterResult) pair.second).mPackageName, 0, new HashSet()), getRequestSession.mClientAppInfo, ((CredentialDescriptionRegistry.FilterResult) pair.second).mPackageName, (CredentialOption) pair.first));
        }
        return arrayList;
    }

    public final Set<Pair<CredentialOption, CredentialDescriptionRegistry.FilterResult>> getFilteredResultFromRegistry(List<CredentialOption> list) {
        Set<CredentialDescriptionRegistry.FilterResult> matchingProviders = CredentialDescriptionRegistry.forUser(UserHandle.getCallingUserId()).getMatchingProviders((Set) list.stream().map(new Function() { // from class: com.android.server.credentials.CredentialManagerService$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$getFilteredResultFromRegistry$1;
                lambda$getFilteredResultFromRegistry$1 = CredentialManagerService.lambda$getFilteredResultFromRegistry$1((CredentialOption) obj);
                return lambda$getFilteredResultFromRegistry$1;
            }
        }).collect(Collectors.toSet()));
        HashSet hashSet = new HashSet();
        for (CredentialDescriptionRegistry.FilterResult filterResult : matchingProviders) {
            for (CredentialOption credentialOption : list) {
                if (filterResult.mFlattenedRequest.equals(credentialOption.getCredentialRetrievalData().getString("android.credentials.GetCredentialOption.FLATTENED_REQUEST_STRING"))) {
                    hashSet.add(new Pair(credentialOption, filterResult));
                }
            }
        }
        return hashSet;
    }

    public static /* synthetic */ String lambda$getFilteredResultFromRegistry$1(CredentialOption credentialOption) {
        return credentialOption.getCredentialRetrievalData().getString("android.credentials.GetCredentialOption.FLATTENED_REQUEST_STRING");
    }

    public final List<ProviderSession> initiateProviderSessions(final RequestSession requestSession, final List<String> list) {
        final ArrayList arrayList = new ArrayList();
        runForUser(new Consumer() { // from class: com.android.server.credentials.CredentialManagerService$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                CredentialManagerService.this.lambda$initiateProviderSessions$2(requestSession, list, arrayList, (CredentialManagerServiceImpl) obj);
            }
        });
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initiateProviderSessions$2(RequestSession requestSession, List list, List list2, CredentialManagerServiceImpl credentialManagerServiceImpl) {
        synchronized (this.mLock) {
            ProviderSession initiateProviderSessionForRequestLocked = credentialManagerServiceImpl.initiateProviderSessionForRequestLocked(requestSession, list);
            if (initiateProviderSessionForRequestLocked != null) {
                list2.add(initiateProviderSessionForRequestLocked);
            }
        }
    }

    public final List<CredentialProviderInfo> getServicesForCredentialDescription(int i) {
        return CredentialProviderInfoFactory.getCredentialProviderServices(this.mContext, i, 0, new HashSet());
    }

    @Override // com.android.server.infra.AbstractMasterSystemService, com.android.server.SystemService
    @GuardedBy({"CredentialDescriptionRegistry.sLock"})
    public void onUserStopped(SystemService.TargetUser targetUser) {
        super.onUserStopped(targetUser);
        CredentialDescriptionRegistry.clearUserSession(targetUser.getUserIdentifier());
    }

    public final CallingAppInfo constructCallingAppInfo(String str, int i, String str2) {
        try {
            return new CallingAppInfo(str, getContext().getPackageManager().getPackageInfoAsUser(str, PackageManager.PackageInfoFlags.of(134217728L), i).signingInfo, str2);
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("CredManSysService", "Issue while retrieving signatureInfo : " + e.getMessage());
            return new CallingAppInfo(str, (SigningInfo) null, str2);
        }
    }

    /* loaded from: classes.dex */
    public final class CredentialManagerServiceStub extends ICredentialManager.Stub {
        public CredentialManagerServiceStub() {
        }

        public ICancellationSignal executeGetCredential(GetCredentialRequest getCredentialRequest, IGetCredentialCallback iGetCredentialCallback, String str) {
            long nanoTime = System.nanoTime();
            Log.i("CredManSysService", "starting executeGetCredential with callingPackage: " + str);
            ICancellationSignal createTransport = CancellationSignal.createTransport();
            if (getCredentialRequest.getOrigin() != null) {
                CredentialManagerService.this.mContext.enforceCallingPermission("android.permission.CREDENTIAL_MANAGER_SET_ORIGIN", null);
            }
            int callingUserId = UserHandle.getCallingUserId();
            int callingUid = Binder.getCallingUid();
            CredentialManagerService.this.enforceCallingPackage(str, callingUid);
            processGetCredential(getCredentialRequest, iGetCredentialCallback, new GetRequestSession(CredentialManagerService.this.getContext(), callingUserId, callingUid, iGetCredentialCallback, getCredentialRequest, CredentialManagerService.this.constructCallingAppInfo(str, callingUserId, getCredentialRequest.getOrigin()), CancellationSignal.fromTransport(createTransport), nanoTime));
            return createTransport;
        }

        public final void processGetCredential(GetCredentialRequest getCredentialRequest, IGetCredentialCallback iGetCredentialCallback, GetRequestSession getRequestSession) {
            List initiateProviderSessions;
            if (CredentialManagerService.isCredentialDescriptionApiEnabled()) {
                List list = getCredentialRequest.getCredentialOptions().stream().filter(new Predicate() { // from class: com.android.server.credentials.CredentialManagerService$CredentialManagerServiceStub$$ExternalSyntheticLambda6
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$processGetCredential$0;
                        lambda$processGetCredential$0 = CredentialManagerService.CredentialManagerServiceStub.lambda$processGetCredential$0((CredentialOption) obj);
                        return lambda$processGetCredential$0;
                    }
                }).toList();
                List list2 = getCredentialRequest.getCredentialOptions().stream().filter(new Predicate() { // from class: com.android.server.credentials.CredentialManagerService$CredentialManagerServiceStub$$ExternalSyntheticLambda7
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$processGetCredential$1;
                        lambda$processGetCredential$1 = CredentialManagerService.CredentialManagerServiceStub.lambda$processGetCredential$1((CredentialOption) obj);
                        return lambda$processGetCredential$1;
                    }
                }).toList();
                CredentialManagerService credentialManagerService = CredentialManagerService.this;
                List initiateProviderSessionsWithActiveContainers = credentialManagerService.initiateProviderSessionsWithActiveContainers(getRequestSession, credentialManagerService.getFilteredResultFromRegistry(list));
                List initiateProviderSessions2 = CredentialManagerService.this.initiateProviderSessions(getRequestSession, (List) list2.stream().map(new C0725x1051f629()).collect(Collectors.toList()));
                LinkedHashSet linkedHashSet = new LinkedHashSet();
                linkedHashSet.addAll(initiateProviderSessions2);
                linkedHashSet.addAll(initiateProviderSessionsWithActiveContainers);
                initiateProviderSessions = new ArrayList(linkedHashSet);
            } else {
                initiateProviderSessions = CredentialManagerService.this.initiateProviderSessions(getRequestSession, (List) getCredentialRequest.getCredentialOptions().stream().map(new C0725x1051f629()).collect(Collectors.toList()));
            }
            if (initiateProviderSessions.isEmpty()) {
                try {
                    iGetCredentialCallback.onError("android.credentials.GetCredentialException.TYPE_NO_CREDENTIAL", "No credentials available on this device.");
                } catch (RemoteException e) {
                    Log.i("CredManSysService", "Issue invoking onError on IGetCredentialCallback callback: " + e.getMessage());
                }
            }
            finalizeAndEmitInitialPhaseMetric(getRequestSession);
            initiateProviderSessions.forEach(new C0717x1051f621());
        }

        public static /* synthetic */ boolean lambda$processGetCredential$0(CredentialOption credentialOption) {
            return !TextUtils.isEmpty(credentialOption.getCredentialRetrievalData().getString("android.credentials.GetCredentialOption.FLATTENED_REQUEST_STRING", null));
        }

        public static /* synthetic */ boolean lambda$processGetCredential$1(CredentialOption credentialOption) {
            return TextUtils.isEmpty(credentialOption.getCredentialRetrievalData().getString("android.credentials.GetCredentialOption.FLATTENED_REQUEST_STRING", null));
        }

        public ICancellationSignal executeCreateCredential(CreateCredentialRequest createCredentialRequest, ICreateCredentialCallback iCreateCredentialCallback, String str) {
            long nanoTime = System.nanoTime();
            Log.i("CredManSysService", "starting executeCreateCredential with callingPackage: " + str);
            ICancellationSignal createTransport = CancellationSignal.createTransport();
            if (createCredentialRequest.getOrigin() != null) {
                CredentialManagerService.this.mContext.enforceCallingPermission("android.permission.CREDENTIAL_MANAGER_SET_ORIGIN", null);
            }
            int callingUserId = UserHandle.getCallingUserId();
            int callingUid = Binder.getCallingUid();
            CredentialManagerService.this.enforceCallingPackage(str, callingUid);
            processCreateCredential(createCredentialRequest, iCreateCredentialCallback, new CreateRequestSession(CredentialManagerService.this.getContext(), callingUserId, callingUid, createCredentialRequest, iCreateCredentialCallback, CredentialManagerService.this.constructCallingAppInfo(str, callingUserId, createCredentialRequest.getOrigin()), CancellationSignal.fromTransport(createTransport), nanoTime));
            return createTransport;
        }

        public final void processCreateCredential(CreateCredentialRequest createCredentialRequest, ICreateCredentialCallback iCreateCredentialCallback, CreateRequestSession createRequestSession) {
            List initiateProviderSessions = CredentialManagerService.this.initiateProviderSessions(createRequestSession, List.of(createCredentialRequest.getType()));
            if (initiateProviderSessions.isEmpty()) {
                try {
                    iCreateCredentialCallback.onError("android.credentials.CreateCredentialException.TYPE_NO_CREATE_OPTIONS", "No create options available.");
                } catch (RemoteException e) {
                    Log.i("CredManSysService", "Issue invoking onError on ICreateCredentialCallback callback: " + e.getMessage());
                }
            }
            finalizeAndEmitInitialPhaseMetric(createRequestSession);
            initiateProviderSessions.forEach(new C0717x1051f621());
        }

        public final void finalizeAndEmitInitialPhaseMetric(RequestSession requestSession) {
            try {
                InitialPhaseMetric initialPhaseMetric = requestSession.mInitialPhaseMetric;
                initialPhaseMetric.setCredentialServiceBeginQueryTimeNanoseconds(System.nanoTime());
                int i = requestSession.mSequenceCounter + 1;
                requestSession.mSequenceCounter = i;
                MetricUtilities.logApiCalled(initialPhaseMetric, i);
            } catch (Exception e) {
                Log.w("CredManSysService", "Unexpected error during metric logging: " + e);
            }
        }

        public void setEnabledProviders(List<String> list, int i, ISetEnabledProvidersCallback iSetEnabledProvidersCallback) {
            Log.i("CredManSysService", "setEnabledProviders");
            if (!CredentialManagerService.this.hasWriteSecureSettingsPermission()) {
                try {
                    iSetEnabledProvidersCallback.onError("permission_denied", "Caller is missing WRITE_SECURE_SETTINGS permission");
                    return;
                } catch (RemoteException e) {
                    Log.e("CredManSysService", "Issue with invoking response: " + e.getMessage());
                    return;
                }
            }
            int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, false, "setEnabledProviders", null);
            if (!Settings.Secure.putStringForUser(CredentialManagerService.this.getContext().getContentResolver(), "credential_service", String.join(XmlUtils.STRING_ARRAY_SEPARATOR, list), handleIncomingUser)) {
                Log.e("CredManSysService", "Failed to store setting containing enabled providers");
                try {
                    iSetEnabledProvidersCallback.onError("failed_setting_store", "Failed to store setting containing enabled providers");
                } catch (RemoteException e2) {
                    Log.i("CredManSysService", "Issue with invoking error response: " + e2.getMessage());
                    return;
                }
            }
            try {
                iSetEnabledProvidersCallback.onResponse();
            } catch (RemoteException e3) {
                Log.i("CredManSysService", "Issue with invoking response: " + e3.getMessage());
            }
            CredentialManagerService.this.getContext().sendBroadcast(IntentFactory.createProviderUpdateIntent(), "android.permission.LAUNCH_CREDENTIAL_SELECTOR");
        }

        public boolean isEnabledCredentialProviderService(ComponentName componentName, String str) {
            Log.i("CredManSysService", "isEnabledCredentialProviderService");
            int callingUserId = UserHandle.getCallingUserId();
            int callingUid = Binder.getCallingUid();
            CredentialManagerService.this.enforceCallingPackage(str, callingUid);
            synchronized (CredentialManagerService.this.mLock) {
                for (CredentialManagerServiceImpl credentialManagerServiceImpl : CredentialManagerService.this.getServiceListForUserLocked(callingUserId)) {
                    if (credentialManagerServiceImpl.getServiceComponentName().equals(componentName)) {
                        if (!credentialManagerServiceImpl.getServicePackageName().equals(str)) {
                            MetricUtilities.logApiCalled(ApiName.IS_ENABLED_CREDENTIAL_PROVIDER_SERVICE, ApiStatus.FAILURE, callingUid);
                            Log.w("CredManSysService", "isEnabledCredentialProviderService: Component name does not match package name.");
                            return false;
                        }
                        MetricUtilities.logApiCalled(ApiName.IS_ENABLED_CREDENTIAL_PROVIDER_SERVICE, ApiStatus.SUCCESS, callingUid);
                        return true;
                    }
                }
                return false;
            }
        }

        public List<CredentialProviderInfo> getCredentialProviderServices(int i, int i2) {
            Log.i("CredManSysService", "getCredentialProviderServices");
            CredentialManagerService.this.verifyGetProvidersPermission();
            return CredentialProviderInfoFactory.getCredentialProviderServices(CredentialManagerService.this.mContext, i, i2, getEnabledProviders());
        }

        public List<CredentialProviderInfo> getCredentialProviderServicesForTesting(int i) {
            Log.i("CredManSysService", "getCredentialProviderServicesForTesting");
            CredentialManagerService.this.verifyGetProvidersPermission();
            return CredentialProviderInfoFactory.getCredentialProviderServicesForTesting(CredentialManagerService.this.mContext, UserHandle.getCallingUserId(), i, getEnabledProviders());
        }

        public final Set<ComponentName> getEnabledProviders() {
            final HashSet hashSet = new HashSet();
            synchronized (CredentialManagerService.this.mLock) {
                CredentialManagerService.this.runForUser(new Consumer() { // from class: com.android.server.credentials.CredentialManagerService$CredentialManagerServiceStub$$ExternalSyntheticLambda9
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        CredentialManagerService.CredentialManagerServiceStub.lambda$getEnabledProviders$2(hashSet, (CredentialManagerServiceImpl) obj);
                    }
                });
            }
            return hashSet;
        }

        public static /* synthetic */ void lambda$getEnabledProviders$2(Set set, CredentialManagerServiceImpl credentialManagerServiceImpl) {
            try {
                set.add(credentialManagerServiceImpl.getCredentialProviderInfo().getServiceInfo().getComponentName());
            } catch (NullPointerException unused) {
                Log.i("CredManSysService", "Skipping provider as either the providerInfoor serviceInfo is null - weird");
            }
        }

        public ICancellationSignal clearCredentialState(ClearCredentialStateRequest clearCredentialStateRequest, IClearCredentialStateCallback iClearCredentialStateCallback, String str) {
            long nanoTime = System.nanoTime();
            Log.i("CredManSysService", "starting clearCredentialState with callingPackage: " + str);
            int callingUserId = UserHandle.getCallingUserId();
            int callingUid = Binder.getCallingUid();
            CredentialManagerService.this.enforceCallingPackage(str, callingUid);
            ICancellationSignal createTransport = CancellationSignal.createTransport();
            ClearRequestSession clearRequestSession = new ClearRequestSession(CredentialManagerService.this.getContext(), callingUserId, callingUid, iClearCredentialStateCallback, clearCredentialStateRequest, CredentialManagerService.this.constructCallingAppInfo(str, callingUserId, null), CancellationSignal.fromTransport(createTransport), nanoTime);
            List initiateProviderSessions = CredentialManagerService.this.initiateProviderSessions(clearRequestSession, List.of());
            if (initiateProviderSessions.isEmpty()) {
                try {
                    iClearCredentialStateCallback.onError("UNKNOWN", "No crdentials available on this device");
                } catch (RemoteException e) {
                    Log.i("CredManSysService", "Issue invoking onError on IClearCredentialStateCallback callback: " + e.getMessage());
                }
            }
            finalizeAndEmitInitialPhaseMetric(clearRequestSession);
            initiateProviderSessions.forEach(new C0717x1051f621());
            return createTransport;
        }

        public void registerCredentialDescription(RegisterCredentialDescriptionRequest registerCredentialDescriptionRequest, final String str) throws IllegalArgumentException, NonCredentialProviderCallerException {
            Log.i("CredManSysService", "registerCredentialDescription");
            if (!CredentialManagerService.isCredentialDescriptionApiEnabled()) {
                throw new UnsupportedOperationException();
            }
            CredentialManagerService.this.enforceCallingPackage(str, Binder.getCallingUid());
            List servicesForCredentialDescription = CredentialManagerService.this.getServicesForCredentialDescription(UserHandle.getCallingUserId());
            if (!servicesForCredentialDescription.stream().map(new Function() { // from class: com.android.server.credentials.CredentialManagerService$CredentialManagerServiceStub$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    String lambda$registerCredentialDescription$3;
                    lambda$registerCredentialDescription$3 = CredentialManagerService.CredentialManagerServiceStub.lambda$registerCredentialDescription$3((CredentialProviderInfo) obj);
                    return lambda$registerCredentialDescription$3;
                }
            }).toList().contains(str)) {
                throw new NonCredentialProviderCallerException(str);
            }
            final CredentialProviderInfo credentialProviderInfo = (CredentialProviderInfo) servicesForCredentialDescription.stream().filter(new Predicate() { // from class: com.android.server.credentials.CredentialManagerService$CredentialManagerServiceStub$$ExternalSyntheticLambda2
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$registerCredentialDescription$4;
                    lambda$registerCredentialDescription$4 = CredentialManagerService.CredentialManagerServiceStub.lambda$registerCredentialDescription$4(str, (CredentialProviderInfo) obj);
                    return lambda$registerCredentialDescription$4;
                }
            }).toList().get(0);
            Stream map = registerCredentialDescriptionRequest.getCredentialDescriptions().stream().map(new Function() { // from class: com.android.server.credentials.CredentialManagerService$CredentialManagerServiceStub$$ExternalSyntheticLambda3
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((CredentialDescription) obj).getType();
                }
            });
            Objects.requireNonNull(credentialProviderInfo);
            if (((Set) map.filter(new Predicate() { // from class: com.android.server.credentials.CredentialManagerService$CredentialManagerServiceStub$$ExternalSyntheticLambda4
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return credentialProviderInfo.hasCapability((String) obj);
                }
            }).collect(Collectors.toSet())).size() != registerCredentialDescriptionRequest.getCredentialDescriptions().size()) {
                throw new IllegalArgumentException("CredentialProvider does not support one or moreof the registered types. Check your XML entry.");
            }
            CredentialDescriptionRegistry.forUser(UserHandle.getCallingUserId()).executeRegisterRequest(registerCredentialDescriptionRequest, str);
        }

        public static /* synthetic */ String lambda$registerCredentialDescription$3(CredentialProviderInfo credentialProviderInfo) {
            return credentialProviderInfo.getServiceInfo().packageName;
        }

        public static /* synthetic */ boolean lambda$registerCredentialDescription$4(String str, CredentialProviderInfo credentialProviderInfo) {
            return credentialProviderInfo.getServiceInfo().packageName.equals(str);
        }

        public void unregisterCredentialDescription(UnregisterCredentialDescriptionRequest unregisterCredentialDescriptionRequest, String str) throws IllegalArgumentException {
            Log.i("CredManSysService", "registerCredentialDescription");
            if (!CredentialManagerService.isCredentialDescriptionApiEnabled()) {
                throw new UnsupportedOperationException();
            }
            CredentialManagerService.this.enforceCallingPackage(str, Binder.getCallingUid());
            if (!CredentialManagerService.this.getServicesForCredentialDescription(UserHandle.getCallingUserId()).stream().map(new Function() { // from class: com.android.server.credentials.CredentialManagerService$CredentialManagerServiceStub$$ExternalSyntheticLambda5
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    String lambda$unregisterCredentialDescription$5;
                    lambda$unregisterCredentialDescription$5 = CredentialManagerService.CredentialManagerServiceStub.lambda$unregisterCredentialDescription$5((CredentialProviderInfo) obj);
                    return lambda$unregisterCredentialDescription$5;
                }
            }).toList().contains(str)) {
                throw new NonCredentialProviderCallerException(str);
            }
            CredentialDescriptionRegistry.forUser(UserHandle.getCallingUserId()).executeUnregisterRequest(unregisterCredentialDescriptionRequest, str);
        }

        public static /* synthetic */ String lambda$unregisterCredentialDescription$5(CredentialProviderInfo credentialProviderInfo) {
            return credentialProviderInfo.getServiceInfo().packageName;
        }
    }

    public final void enforceCallingPackage(String str, int i) {
        try {
            if (this.mContext.createContextAsUser(UserHandle.getUserHandleForUid(i), 0).getPackageManager().getPackageUid(str, PackageManager.PackageInfoFlags.of(0L)) == i) {
                return;
            }
            throw new SecurityException(str + " does not belong to uid " + i);
        } catch (PackageManager.NameNotFoundException unused) {
            throw new SecurityException(str + " not found");
        }
    }
}
