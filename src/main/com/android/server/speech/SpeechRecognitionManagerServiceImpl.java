package com.android.server.speech;

import android.app.AppGlobals;
import android.content.AttributionSource;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.permission.PermissionManager;
import android.speech.IModelDownloadListener;
import android.speech.IRecognitionListener;
import android.speech.IRecognitionService;
import android.speech.IRecognitionServiceManagerCallback;
import android.speech.IRecognitionSupportCallback;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.server.infra.AbstractPerUserSystemService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
/* loaded from: classes2.dex */
public final class SpeechRecognitionManagerServiceImpl extends AbstractPerUserSystemService<SpeechRecognitionManagerServiceImpl, SpeechRecognitionManagerService> {
    public static final String TAG = "SpeechRecognitionManagerServiceImpl";
    public final Object mLock;
    @GuardedBy({"mLock"})
    public final Map<Integer, Set<RemoteSpeechRecognitionService>> mRemoteServicesByUid;

    public SpeechRecognitionManagerServiceImpl(SpeechRecognitionManagerService speechRecognitionManagerService, Object obj, int i) {
        super(speechRecognitionManagerService, obj, i);
        this.mLock = new Object();
        this.mRemoteServicesByUid = new HashMap();
    }

    @Override // com.android.server.infra.AbstractPerUserSystemService
    @GuardedBy({"mLock"})
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
        return super.updateLocked(z);
    }

    public void createSessionLocked(ComponentName componentName, final IBinder iBinder, boolean z, final IRecognitionServiceManagerCallback iRecognitionServiceManagerCallback) {
        if (((SpeechRecognitionManagerService) this.mMaster).debug) {
            Slog.i(TAG, String.format("#createSessionLocked, component=%s, onDevice=%s", componentName, Boolean.valueOf(z)));
        }
        if (z) {
            componentName = getOnDeviceComponentNameLocked();
        }
        if (!z && Process.isIsolated(Binder.getCallingUid())) {
            Slog.w(TAG, "Isolated process can only start on device speech recognizer.");
            tryRespondWithError(iRecognitionServiceManagerCallback, 5);
        } else if (componentName == null) {
            if (((SpeechRecognitionManagerService) this.mMaster).debug) {
                Slog.i(TAG, "Service component is undefined, responding with error.");
            }
            tryRespondWithError(iRecognitionServiceManagerCallback, 5);
        } else {
            final int callingUid = Binder.getCallingUid();
            final RemoteSpeechRecognitionService createService = createService(callingUid, componentName);
            if (createService == null) {
                tryRespondWithError(iRecognitionServiceManagerCallback, 10);
                return;
            }
            final IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() { // from class: com.android.server.speech.SpeechRecognitionManagerServiceImpl$$ExternalSyntheticLambda0
                @Override // android.os.IBinder.DeathRecipient
                public final void binderDied() {
                    SpeechRecognitionManagerServiceImpl.this.lambda$createSessionLocked$0(iBinder, callingUid, createService);
                }
            };
            try {
                iBinder.linkToDeath(deathRecipient, 0);
                createService.connect().thenAccept(new Consumer() { // from class: com.android.server.speech.SpeechRecognitionManagerServiceImpl$$ExternalSyntheticLambda1
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        SpeechRecognitionManagerServiceImpl.this.lambda$createSessionLocked$1(iRecognitionServiceManagerCallback, createService, iBinder, callingUid, deathRecipient, (IRecognitionService) obj);
                    }
                });
            } catch (RemoteException unused) {
                handleClientDeath(iBinder, callingUid, createService, true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createSessionLocked$0(IBinder iBinder, int i, RemoteSpeechRecognitionService remoteSpeechRecognitionService) {
        handleClientDeath(iBinder, i, remoteSpeechRecognitionService, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createSessionLocked$1(IRecognitionServiceManagerCallback iRecognitionServiceManagerCallback, final RemoteSpeechRecognitionService remoteSpeechRecognitionService, final IBinder iBinder, final int i, final IBinder.DeathRecipient deathRecipient, IRecognitionService iRecognitionService) {
        if (iRecognitionService != null) {
            try {
                iRecognitionServiceManagerCallback.onSuccess(new IRecognitionService.Stub() { // from class: com.android.server.speech.SpeechRecognitionManagerServiceImpl.1
                    public void startListening(Intent intent, IRecognitionListener iRecognitionListener, AttributionSource attributionSource) throws RemoteException {
                        attributionSource.enforceCallingUid();
                        if (!attributionSource.isTrusted(((SpeechRecognitionManagerService) SpeechRecognitionManagerServiceImpl.this.mMaster).getContext())) {
                            attributionSource = ((PermissionManager) ((SpeechRecognitionManagerService) SpeechRecognitionManagerServiceImpl.this.mMaster).getContext().getSystemService(PermissionManager.class)).registerAttributionSource(attributionSource);
                        }
                        remoteSpeechRecognitionService.startListening(intent, iRecognitionListener, attributionSource);
                        remoteSpeechRecognitionService.associateClientWithActiveListener(iBinder, iRecognitionListener);
                    }

                    public void stopListening(IRecognitionListener iRecognitionListener) throws RemoteException {
                        remoteSpeechRecognitionService.stopListening(iRecognitionListener);
                    }

                    public void cancel(IRecognitionListener iRecognitionListener, boolean z) throws RemoteException {
                        remoteSpeechRecognitionService.cancel(iRecognitionListener, z);
                        if (z) {
                            SpeechRecognitionManagerServiceImpl.this.handleClientDeath(iBinder, i, remoteSpeechRecognitionService, false);
                            iBinder.unlinkToDeath(deathRecipient, 0);
                        }
                    }

                    public void checkRecognitionSupport(Intent intent, AttributionSource attributionSource, IRecognitionSupportCallback iRecognitionSupportCallback) {
                        remoteSpeechRecognitionService.checkRecognitionSupport(intent, attributionSource, iRecognitionSupportCallback);
                    }

                    public void triggerModelDownload(Intent intent, AttributionSource attributionSource) {
                        remoteSpeechRecognitionService.triggerModelDownload(intent, attributionSource);
                    }

                    public void setModelDownloadListener(Intent intent, AttributionSource attributionSource, IModelDownloadListener iModelDownloadListener) throws RemoteException {
                        remoteSpeechRecognitionService.setModelDownloadListener(intent, attributionSource, iModelDownloadListener);
                    }

                    public void clearModelDownloadListener(Intent intent, AttributionSource attributionSource) throws RemoteException {
                        remoteSpeechRecognitionService.clearModelDownloadListener(intent, attributionSource);
                    }
                });
                return;
            } catch (RemoteException e) {
                Slog.e(TAG, "Error creating a speech recognition session", e);
                tryRespondWithError(iRecognitionServiceManagerCallback, 5);
                return;
            }
        }
        tryRespondWithError(iRecognitionServiceManagerCallback, 5);
    }

    public final void handleClientDeath(IBinder iBinder, int i, RemoteSpeechRecognitionService remoteSpeechRecognitionService, boolean z) {
        if (z) {
            remoteSpeechRecognitionService.shutdown(iBinder);
        }
        synchronized (this.mLock) {
            if (!remoteSpeechRecognitionService.hasActiveSessions()) {
                removeService(i, remoteSpeechRecognitionService);
            }
        }
    }

    @GuardedBy({"mLock"})
    public final ComponentName getOnDeviceComponentNameLocked() {
        String componentNameLocked = getComponentNameLocked();
        if (((SpeechRecognitionManagerService) this.mMaster).debug) {
            String str = TAG;
            Slog.i(str, "Resolved component name: " + componentNameLocked);
        }
        if (componentNameLocked == null) {
            if (((SpeechRecognitionManagerService) this.mMaster).verbose) {
                Slog.v(TAG, "ensureRemoteServiceLocked(): no service component name.");
                return null;
            }
            return null;
        }
        return ComponentName.unflattenFromString(componentNameLocked);
    }

    public final RemoteSpeechRecognitionService createService(int i, final ComponentName componentName) {
        synchronized (this.mLock) {
            Set<RemoteSpeechRecognitionService> set = this.mRemoteServicesByUid.get(Integer.valueOf(i));
            if (set == null || set.size() < 10) {
                if (set != null) {
                    Optional<RemoteSpeechRecognitionService> findFirst = set.stream().filter(new Predicate() { // from class: com.android.server.speech.SpeechRecognitionManagerServiceImpl$$ExternalSyntheticLambda2
                        @Override // java.util.function.Predicate
                        public final boolean test(Object obj) {
                            boolean lambda$createService$2;
                            lambda$createService$2 = SpeechRecognitionManagerServiceImpl.lambda$createService$2(componentName, (RemoteSpeechRecognitionService) obj);
                            return lambda$createService$2;
                        }
                    }).findFirst();
                    if (findFirst.isPresent()) {
                        if (((SpeechRecognitionManagerService) this.mMaster).debug) {
                            String str = TAG;
                            Slog.i(str, "Reused existing connection to " + componentName);
                        }
                        return findFirst.get();
                    }
                }
                if (componentName == null || componentMapsToRecognitionService(componentName)) {
                    RemoteSpeechRecognitionService remoteSpeechRecognitionService = new RemoteSpeechRecognitionService(getContext(), componentName, getUserId(), i);
                    this.mRemoteServicesByUid.computeIfAbsent(Integer.valueOf(i), new Function() { // from class: com.android.server.speech.SpeechRecognitionManagerServiceImpl$$ExternalSyntheticLambda3
                        @Override // java.util.function.Function
                        public final Object apply(Object obj) {
                            Set lambda$createService$3;
                            lambda$createService$3 = SpeechRecognitionManagerServiceImpl.lambda$createService$3((Integer) obj);
                            return lambda$createService$3;
                        }
                    }).add(remoteSpeechRecognitionService);
                    if (((SpeechRecognitionManagerService) this.mMaster).debug) {
                        String str2 = TAG;
                        Slog.i(str2, "Creating a new connection to " + componentName);
                    }
                    return remoteSpeechRecognitionService;
                }
                return null;
            }
            return null;
        }
    }

    public static /* synthetic */ boolean lambda$createService$2(ComponentName componentName, RemoteSpeechRecognitionService remoteSpeechRecognitionService) {
        return remoteSpeechRecognitionService.getServiceComponentName().equals(componentName);
    }

    public static /* synthetic */ Set lambda$createService$3(Integer num) {
        return new HashSet();
    }

    public final boolean componentMapsToRecognitionService(ComponentName componentName) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            List<ResolveInfo> queryIntentServicesAsUser = getContext().getPackageManager().queryIntentServicesAsUser(new Intent("android.speech.RecognitionService"), 0, getUserId());
            if (queryIntentServicesAsUser == null) {
                return false;
            }
            for (ResolveInfo resolveInfo : queryIntentServicesAsUser) {
                ServiceInfo serviceInfo = resolveInfo.serviceInfo;
                if (serviceInfo != null && componentName.equals(serviceInfo.getComponentName())) {
                    return true;
                }
            }
            String str = TAG;
            Slog.w(str, "serviceComponent is not RecognitionService: " + componentName);
            return false;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void removeService(int i, RemoteSpeechRecognitionService remoteSpeechRecognitionService) {
        synchronized (this.mLock) {
            Set<RemoteSpeechRecognitionService> set = this.mRemoteServicesByUid.get(Integer.valueOf(i));
            if (set != null) {
                set.remove(remoteSpeechRecognitionService);
            }
        }
    }

    public static void tryRespondWithError(IRecognitionServiceManagerCallback iRecognitionServiceManagerCallback, int i) {
        try {
            iRecognitionServiceManagerCallback.onError(i);
        } catch (RemoteException unused) {
            Slog.w(TAG, "Failed to respond with error");
        }
    }
}
