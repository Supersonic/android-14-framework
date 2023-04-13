package com.android.server.credentials;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.credentials.CredentialProviderInfo;
import android.service.credentials.CredentialProviderInfoFactory;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.server.infra.AbstractPerUserSystemService;
import java.util.List;
/* loaded from: classes.dex */
public final class CredentialManagerServiceImpl extends AbstractPerUserSystemService<CredentialManagerServiceImpl, CredentialManagerService> {
    @GuardedBy({"mLock"})
    public CredentialProviderInfo mInfo;

    public CredentialManagerServiceImpl(CredentialManagerService credentialManagerService, Object obj, int i, String str) throws PackageManager.NameNotFoundException {
        super(credentialManagerService, obj, i);
        Log.i("CredManSysServiceImpl", "in CredentialManagerServiceImpl constructed with: " + str);
        synchronized (this.mLock) {
            newServiceInfoLocked(ComponentName.unflattenFromString(str));
        }
    }

    public CredentialManagerServiceImpl(CredentialManagerService credentialManagerService, Object obj, int i, CredentialProviderInfo credentialProviderInfo) {
        super(credentialManagerService, obj, i);
        StringBuilder sb = new StringBuilder();
        sb.append("in CredentialManagerServiceImpl constructed with system constructor: ");
        sb.append(credentialProviderInfo.isSystemProvider());
        sb.append(" , ");
        sb.append(credentialProviderInfo.getServiceInfo());
        Log.i("CredManSysServiceImpl", sb.toString() == null ? "" : credentialProviderInfo.getServiceInfo().getComponentName().flattenToString());
        this.mInfo = credentialProviderInfo;
    }

    @Override // com.android.server.infra.AbstractPerUserSystemService
    @GuardedBy({"mLock"})
    public ServiceInfo newServiceInfoLocked(ComponentName componentName) throws PackageManager.NameNotFoundException {
        if (this.mInfo != null) {
            Log.i("CredManSysServiceImpl", "newServiceInfoLocked with : " + this.mInfo.getServiceInfo().getComponentName().flattenToString() + " , " + componentName.getPackageName());
        } else {
            Log.i("CredManSysServiceImpl", "newServiceInfoLocked with null mInfo , " + componentName.getPackageName());
        }
        CredentialProviderInfo create = CredentialProviderInfoFactory.create(getContext(), componentName, this.mUserId, false);
        this.mInfo = create;
        return create.getServiceInfo();
    }

    @GuardedBy({"mLock"})
    public ProviderSession initiateProviderSessionForRequestLocked(RequestSession requestSession, List<String> list) {
        if (!list.isEmpty() && !isServiceCapableLocked(list)) {
            Log.i("CredManSysServiceImpl", "Service is not capable");
            return null;
        }
        Slog.i("CredManSysServiceImpl", "in initiateProviderSessionForRequest in CredManServiceImpl");
        if (this.mInfo == null) {
            Slog.i("CredManSysServiceImpl", "in initiateProviderSessionForRequest in CredManServiceImpl, but mInfo is null. This shouldn't happen");
            return null;
        }
        return requestSession.initiateProviderSession(this.mInfo, new RemoteCredentialService(getContext(), this.mInfo.getServiceInfo().getComponentName(), this.mUserId));
    }

    @GuardedBy({"mLock"})
    public boolean isServiceCapableLocked(List<String> list) {
        if (this.mInfo == null) {
            Slog.i("CredManSysServiceImpl", "in isServiceCapable, mInfo is null");
            return false;
        }
        for (String str : list) {
            if (this.mInfo.hasCapability(str)) {
                Slog.i("CredManSysServiceImpl", "Provider can handle: " + str);
                return true;
            }
            Slog.i("CredManSysServiceImpl", "Provider cannot handle: " + str);
        }
        return false;
    }

    @GuardedBy({"mLock"})
    public CredentialProviderInfo getCredentialProviderInfo() {
        return this.mInfo;
    }

    @Override // com.android.server.infra.AbstractPerUserSystemService
    @GuardedBy({"mLock"})
    public void handlePackageUpdateLocked(String str) {
        CredentialProviderInfo credentialProviderInfo = this.mInfo;
        if (credentialProviderInfo == null || credentialProviderInfo.getServiceInfo() == null || !this.mInfo.getServiceInfo().getComponentName().getPackageName().equals(str)) {
            return;
        }
        try {
            newServiceInfoLocked(this.mInfo.getServiceInfo().getComponentName());
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("CredManSysServiceImpl", "Issue while updating serviceInfo: " + e.getMessage());
        }
    }
}
