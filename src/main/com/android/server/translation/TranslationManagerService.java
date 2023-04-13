package com.android.server.translation;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.os.UserHandle;
import android.util.Slog;
import android.view.autofill.AutofillId;
import android.view.translation.ITranslationManager;
import android.view.translation.TranslationContext;
import android.view.translation.TranslationSpec;
import android.view.translation.UiTranslationSpec;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.IResultReceiver;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.SyncResultReceiver;
import com.android.server.infra.AbstractMasterSystemService;
import com.android.server.infra.FrameworkResourcesServiceNameResolver;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
/* loaded from: classes2.dex */
public final class TranslationManagerService extends AbstractMasterSystemService<TranslationManagerService, TranslationManagerServiceImpl> {
    @Override // com.android.server.infra.AbstractMasterSystemService
    public int getMaximumTemporaryServiceDurationMs() {
        return 120000;
    }

    public TranslationManagerService(Context context) {
        super(context, new FrameworkResourcesServiceNameResolver(context, 17039905), null, 4);
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public TranslationManagerServiceImpl newServiceLocked(int i, boolean z) {
        return new TranslationManagerServiceImpl(this, this.mLock, i, z);
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void enforceCallingPermissionForManagement() {
        getContext().enforceCallingPermission("android.permission.MANAGE_UI_TRANSLATION", "TranslationManagerService");
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void dumpLocked(String str, PrintWriter printWriter) {
        super.dumpLocked(str, printWriter);
    }

    public final void enforceCallerHasPermission(String str) {
        getContext().enforceCallingPermission(str, "Permission Denial from pid =" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " doesn't hold " + str);
    }

    @GuardedBy({"mLock"})
    public final boolean isDefaultServiceLocked(int i) {
        String defaultServiceName = this.mServiceNameResolver.getDefaultServiceName(i);
        if (defaultServiceName == null) {
            return false;
        }
        return defaultServiceName.equals(this.mServiceNameResolver.getServiceName(i));
    }

    @GuardedBy({"mLock"})
    public final boolean isCalledByServiceAppLocked(int i, String str) {
        int callingUid = Binder.getCallingUid();
        String serviceName = this.mServiceNameResolver.getServiceName(i);
        if (serviceName == null) {
            Slog.e("TranslationManagerService", str + ": called by UID " + callingUid + ", but there's no service set for user " + i);
            return false;
        }
        ComponentName unflattenFromString = ComponentName.unflattenFromString(serviceName);
        if (unflattenFromString == null) {
            Slog.w("TranslationManagerService", str + ": invalid service name: " + serviceName);
            return false;
        }
        try {
            int packageUidAsUser = getContext().getPackageManager().getPackageUidAsUser(unflattenFromString.getPackageName(), i);
            if (callingUid != packageUidAsUser) {
                Slog.e("TranslationManagerService", str + ": called by UID " + callingUid + ", but service UID is " + packageUidAsUser);
                return false;
            }
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            Slog.w("TranslationManagerService", str + ": could not verify UID for " + serviceName);
            return false;
        }
    }

    /* loaded from: classes2.dex */
    public final class TranslationManagerServiceStub extends ITranslationManager.Stub {
        public TranslationManagerServiceStub() {
        }

        public void onTranslationCapabilitiesRequest(int i, int i2, ResultReceiver resultReceiver, int i3) throws RemoteException {
            synchronized (TranslationManagerService.this.mLock) {
                TranslationManagerServiceImpl translationManagerServiceImpl = (TranslationManagerServiceImpl) TranslationManagerService.this.getServiceForUserLocked(i3);
                if (translationManagerServiceImpl != null && (TranslationManagerService.this.isDefaultServiceLocked(i3) || TranslationManagerService.this.isCalledByServiceAppLocked(i3, "getTranslationCapabilities"))) {
                    translationManagerServiceImpl.onTranslationCapabilitiesRequestLocked(i, i2, resultReceiver);
                } else {
                    Slog.v("TranslationManagerService", "onGetTranslationCapabilitiesLocked(): no service for " + i3);
                    resultReceiver.send(2, null);
                }
            }
        }

        public void registerTranslationCapabilityCallback(IRemoteCallback iRemoteCallback, int i) {
            TranslationManagerServiceImpl translationManagerServiceImpl;
            synchronized (TranslationManagerService.this.mLock) {
                translationManagerServiceImpl = (TranslationManagerServiceImpl) TranslationManagerService.this.getServiceForUserLocked(i);
            }
            if (translationManagerServiceImpl != null) {
                translationManagerServiceImpl.registerTranslationCapabilityCallback(iRemoteCallback, Binder.getCallingUid());
            }
        }

        public void unregisterTranslationCapabilityCallback(IRemoteCallback iRemoteCallback, int i) {
            TranslationManagerServiceImpl translationManagerServiceImpl;
            synchronized (TranslationManagerService.this.mLock) {
                translationManagerServiceImpl = (TranslationManagerServiceImpl) TranslationManagerService.this.getServiceForUserLocked(i);
            }
            if (translationManagerServiceImpl != null) {
                translationManagerServiceImpl.unregisterTranslationCapabilityCallback(iRemoteCallback);
            }
        }

        public void onSessionCreated(TranslationContext translationContext, int i, IResultReceiver iResultReceiver, int i2) throws RemoteException {
            synchronized (TranslationManagerService.this.mLock) {
                TranslationManagerServiceImpl translationManagerServiceImpl = (TranslationManagerServiceImpl) TranslationManagerService.this.getServiceForUserLocked(i2);
                if (translationManagerServiceImpl != null && (TranslationManagerService.this.isDefaultServiceLocked(i2) || TranslationManagerService.this.isCalledByServiceAppLocked(i2, "onSessionCreated"))) {
                    translationManagerServiceImpl.onSessionCreatedLocked(translationContext, i, iResultReceiver);
                } else {
                    Slog.v("TranslationManagerService", "onSessionCreated(): no service for " + i2);
                    iResultReceiver.send(2, (Bundle) null);
                }
            }
        }

        public void updateUiTranslationState(int i, TranslationSpec translationSpec, TranslationSpec translationSpec2, List<AutofillId> list, IBinder iBinder, int i2, UiTranslationSpec uiTranslationSpec, int i3) {
            TranslationManagerService.this.enforceCallerHasPermission("android.permission.MANAGE_UI_TRANSLATION");
            synchronized (TranslationManagerService.this.mLock) {
                TranslationManagerServiceImpl translationManagerServiceImpl = (TranslationManagerServiceImpl) TranslationManagerService.this.getServiceForUserLocked(i3);
                if (translationManagerServiceImpl != null && (TranslationManagerService.this.isDefaultServiceLocked(i3) || TranslationManagerService.this.isCalledByServiceAppLocked(i3, "updateUiTranslationState"))) {
                    translationManagerServiceImpl.updateUiTranslationStateLocked(i, translationSpec, translationSpec2, list, iBinder, i2, uiTranslationSpec);
                }
            }
        }

        public void registerUiTranslationStateCallback(IRemoteCallback iRemoteCallback, int i) {
            synchronized (TranslationManagerService.this.mLock) {
                TranslationManagerServiceImpl translationManagerServiceImpl = (TranslationManagerServiceImpl) TranslationManagerService.this.getServiceForUserLocked(i);
                if (translationManagerServiceImpl != null) {
                    translationManagerServiceImpl.registerUiTranslationStateCallbackLocked(iRemoteCallback, Binder.getCallingUid());
                }
            }
        }

        public void unregisterUiTranslationStateCallback(IRemoteCallback iRemoteCallback, int i) {
            TranslationManagerServiceImpl translationManagerServiceImpl;
            synchronized (TranslationManagerService.this.mLock) {
                translationManagerServiceImpl = (TranslationManagerServiceImpl) TranslationManagerService.this.getServiceForUserLocked(i);
            }
            if (translationManagerServiceImpl != null) {
                translationManagerServiceImpl.unregisterUiTranslationStateCallback(iRemoteCallback);
            }
        }

        public void onTranslationFinished(boolean z, IBinder iBinder, ComponentName componentName, int i) {
            synchronized (TranslationManagerService.this.mLock) {
                ((TranslationManagerServiceImpl) TranslationManagerService.this.getServiceForUserLocked(i)).onTranslationFinishedLocked(z, iBinder, componentName);
            }
        }

        public void getServiceSettingsActivity(IResultReceiver iResultReceiver, int i) {
            TranslationManagerServiceImpl translationManagerServiceImpl;
            synchronized (TranslationManagerService.this.mLock) {
                translationManagerServiceImpl = (TranslationManagerServiceImpl) TranslationManagerService.this.getServiceForUserLocked(i);
            }
            if (translationManagerServiceImpl != null) {
                ComponentName serviceSettingsActivityLocked = translationManagerServiceImpl.getServiceSettingsActivityLocked();
                if (serviceSettingsActivityLocked == null) {
                    try {
                        iResultReceiver.send(1, (Bundle) null);
                    } catch (RemoteException e) {
                        Slog.w("TranslationManagerService", "Unable to send getServiceSettingsActivity(): " + e);
                    }
                }
                Intent intent = new Intent();
                intent.setComponent(serviceSettingsActivityLocked);
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    try {
                        iResultReceiver.send(1, SyncResultReceiver.bundleFor(PendingIntent.getActivityAsUser(TranslationManagerService.this.getContext(), 0, intent, 67108864, null, new UserHandle(i))));
                    } catch (RemoteException e2) {
                        Slog.w("TranslationManagerService", "Unable to send getServiceSettingsActivity(): " + e2);
                    }
                    return;
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            try {
                iResultReceiver.send(2, (Bundle) null);
            } catch (RemoteException e3) {
                Slog.w("TranslationManagerService", "Unable to send getServiceSettingsActivity(): " + e3);
            }
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            if (DumpUtils.checkDumpPermission(TranslationManagerService.this.getContext(), "TranslationManagerService", printWriter)) {
                synchronized (TranslationManagerService.this.mLock) {
                    TranslationManagerService.this.dumpLocked("", printWriter);
                    TranslationManagerServiceImpl translationManagerServiceImpl = (TranslationManagerServiceImpl) TranslationManagerService.this.getServiceForUserLocked(UserHandle.getCallingUserId());
                    if (translationManagerServiceImpl != null) {
                        translationManagerServiceImpl.dumpLocked("  ", fileDescriptor, printWriter);
                    }
                }
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) throws RemoteException {
            new TranslationManagerServiceShellCommand(TranslationManagerService.this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
        }
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("translation", new TranslationManagerServiceStub());
    }
}
