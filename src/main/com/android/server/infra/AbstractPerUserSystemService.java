package com.android.server.infra;

import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.infra.AbstractMasterSystemService;
import com.android.server.infra.AbstractPerUserSystemService;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public abstract class AbstractPerUserSystemService<S extends AbstractPerUserSystemService<S, M>, M extends AbstractMasterSystemService<M, S>> {
    @GuardedBy({"mLock"})
    public boolean mDisabled;
    public final Object mLock;
    public final M mMaster;
    @GuardedBy({"mLock"})
    public ServiceInfo mServiceInfo;
    @GuardedBy({"mLock"})
    public boolean mSetupComplete;
    public final String mTag = getClass().getSimpleName();
    public final int mUserId;

    public void handlePackageUpdateLocked(String str) {
    }

    public AbstractPerUserSystemService(M m, Object obj, int i) {
        this.mMaster = m;
        this.mLock = obj;
        this.mUserId = i;
        updateIsSetupComplete(i);
    }

    public final void updateIsSetupComplete(int i) {
        this.mSetupComplete = "1".equals(Settings.Secure.getStringForUser(getContext().getContentResolver(), "user_setup_complete", i));
    }

    public ServiceInfo newServiceInfoLocked(ComponentName componentName) throws PackageManager.NameNotFoundException {
        throw new UnsupportedOperationException("not overridden");
    }

    @GuardedBy({"mLock"})
    public boolean isEnabledLocked() {
        return (!this.mSetupComplete || this.mServiceInfo == null || this.mDisabled) ? false : true;
    }

    public final boolean isDisabledByUserRestrictionsLocked() {
        return this.mDisabled;
    }

    @GuardedBy({"mLock"})
    public boolean updateLocked(boolean z) {
        boolean isEnabledLocked = isEnabledLocked();
        if (this.mMaster.verbose) {
            String str = this.mTag;
            Slog.v(str, "updateLocked(u=" + this.mUserId + "): wasEnabled=" + isEnabledLocked + ", mSetupComplete=" + this.mSetupComplete + ", disabled=" + z + ", mDisabled=" + this.mDisabled);
        }
        updateIsSetupComplete(this.mUserId);
        this.mDisabled = z;
        ServiceNameResolver serviceNameResolver = this.mMaster.mServiceNameResolver;
        if (serviceNameResolver != null && serviceNameResolver.isConfiguredInMultipleMode()) {
            if (this.mMaster.debug) {
                Slog.d(this.mTag, "Should not end up in updateLocked when isConfiguredInMultipleMode is true");
            }
        } else {
            updateServiceInfoLocked();
        }
        return isEnabledLocked != isEnabledLocked();
    }

    @GuardedBy({"mLock"})
    public final ComponentName updateServiceInfoLocked() {
        ComponentName[] updateServiceInfoListLocked = updateServiceInfoListLocked();
        if (updateServiceInfoListLocked == null || updateServiceInfoListLocked.length == 0) {
            return null;
        }
        return updateServiceInfoListLocked[0];
    }

    @GuardedBy({"mLock"})
    public final ComponentName[] updateServiceInfoListLocked() {
        ServiceNameResolver serviceNameResolver = this.mMaster.mServiceNameResolver;
        if (serviceNameResolver == null) {
            return null;
        }
        if (!serviceNameResolver.isConfiguredInMultipleMode()) {
            return new ComponentName[]{getServiceComponent(getComponentNameLocked())};
        }
        String[] serviceNameList = this.mMaster.mServiceNameResolver.getServiceNameList(this.mUserId);
        if (serviceNameList == null) {
            return null;
        }
        ComponentName[] componentNameArr = new ComponentName[serviceNameList.length];
        for (int i = 0; i < serviceNameList.length; i++) {
            componentNameArr[i] = getServiceComponent(serviceNameList[i]);
        }
        return componentNameArr;
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x005b A[Catch: Exception -> 0x00bb, all -> 0x00de, TRY_ENTER, TryCatch #0 {Exception -> 0x00bb, blocks: (B:20:0x005b, B:22:0x0067, B:23:0x0092, B:25:0x009a), top: B:35:0x0059, outer: #3 }] */
    /* JADX WARN: Removed duplicated region for block: B:23:0x0092 A[Catch: Exception -> 0x00bb, all -> 0x00de, TryCatch #0 {Exception -> 0x00bb, blocks: (B:20:0x005b, B:22:0x0067, B:23:0x0092, B:25:0x009a), top: B:35:0x0059, outer: #3 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final ComponentName getServiceComponent(String str) {
        ComponentName componentName;
        ServiceInfo serviceInfo;
        synchronized (this.mLock) {
            if (TextUtils.isEmpty(str)) {
                componentName = null;
                serviceInfo = null;
            } else {
                try {
                    componentName = ComponentName.unflattenFromString(str);
                    try {
                        serviceInfo = AppGlobals.getPackageManager().getServiceInfo(componentName, 0L, this.mUserId);
                        if (serviceInfo == null) {
                            Slog.e(this.mTag, "Bad service name: " + str);
                        }
                    } catch (RemoteException | RuntimeException e) {
                        e = e;
                        Slog.e(this.mTag, "Error getting service info for '" + str + "': " + e);
                        serviceInfo = null;
                        if (serviceInfo == null) {
                        }
                        return componentName;
                    }
                } catch (RemoteException | RuntimeException e2) {
                    e = e2;
                    componentName = null;
                }
            }
            try {
                if (serviceInfo == null) {
                    this.mServiceInfo = newServiceInfoLocked(componentName);
                    if (this.mMaster.debug) {
                        Slog.d(this.mTag, "Set component for user " + this.mUserId + " as " + componentName + " and info as " + this.mServiceInfo);
                    }
                } else {
                    this.mServiceInfo = null;
                    if (this.mMaster.debug) {
                        Slog.d(this.mTag, "Reset component for user " + this.mUserId + XmlUtils.STRING_ARRAY_SEPARATOR + str);
                    }
                }
            } catch (Exception e3) {
                Slog.e(this.mTag, "Bad ServiceInfo for '" + str + "': " + e3);
                this.mServiceInfo = null;
            }
        }
        return componentName;
    }

    public final int getUserId() {
        return this.mUserId;
    }

    public final M getMaster() {
        return this.mMaster;
    }

    @GuardedBy({"mLock"})
    public final int getServiceUidLocked() {
        ServiceInfo serviceInfo = this.mServiceInfo;
        if (serviceInfo == null) {
            if (this.mMaster.verbose) {
                Slog.v(this.mTag, "getServiceUidLocked(): no mServiceInfo");
                return -1;
            }
            return -1;
        }
        return serviceInfo.applicationInfo.uid;
    }

    @GuardedBy({"mLock"})
    public final String getComponentNameLocked() {
        return this.mMaster.mServiceNameResolver.getServiceName(this.mUserId);
    }

    @GuardedBy({"mLock"})
    public final boolean isTemporaryServiceSetLocked() {
        return this.mMaster.mServiceNameResolver.isTemporary(this.mUserId);
    }

    @GuardedBy({"mLock"})
    public final void resetTemporaryServiceLocked() {
        this.mMaster.mServiceNameResolver.resetTemporaryService(this.mUserId);
    }

    public final ServiceInfo getServiceInfo() {
        return this.mServiceInfo;
    }

    public final ComponentName getServiceComponentName() {
        ComponentName componentName;
        synchronized (this.mLock) {
            ServiceInfo serviceInfo = this.mServiceInfo;
            componentName = serviceInfo == null ? null : serviceInfo.getComponentName();
        }
        return componentName;
    }

    public final String getServicePackageName() {
        ComponentName serviceComponentName = getServiceComponentName();
        if (serviceComponentName == null) {
            return null;
        }
        return serviceComponentName.getPackageName();
    }

    @GuardedBy({"mLock"})
    public final CharSequence getServiceLabelLocked() {
        ServiceInfo serviceInfo = this.mServiceInfo;
        if (serviceInfo == null) {
            return null;
        }
        return serviceInfo.loadSafeLabel(getContext().getPackageManager(), 0.0f, 5);
    }

    @GuardedBy({"mLock"})
    public final Drawable getServiceIconLocked() {
        ServiceInfo serviceInfo = this.mServiceInfo;
        if (serviceInfo == null) {
            return null;
        }
        return serviceInfo.loadIcon(getContext().getPackageManager());
    }

    public final void removeSelfFromCache() {
        synchronized (this.mMaster.mLock) {
            this.mMaster.removeCachedServiceListLocked(this.mUserId);
        }
    }

    public final boolean isDebug() {
        return this.mMaster.debug;
    }

    public final boolean isVerbose() {
        return this.mMaster.verbose;
    }

    @GuardedBy({"mLock"})
    public final int getTargedSdkLocked() {
        ServiceInfo serviceInfo = this.mServiceInfo;
        if (serviceInfo == null) {
            return 0;
        }
        return serviceInfo.applicationInfo.targetSdkVersion;
    }

    @GuardedBy({"mLock"})
    public final boolean isSetupCompletedLocked() {
        return this.mSetupComplete;
    }

    public final Context getContext() {
        return this.mMaster.getContext();
    }

    @GuardedBy({"mLock"})
    public void dumpLocked(String str, PrintWriter printWriter) {
        printWriter.print(str);
        printWriter.print("User: ");
        printWriter.println(this.mUserId);
        if (this.mServiceInfo != null) {
            printWriter.print(str);
            printWriter.print("Service Label: ");
            printWriter.println(getServiceLabelLocked());
            printWriter.print(str);
            printWriter.print("Target SDK: ");
            printWriter.println(getTargedSdkLocked());
        }
        if (this.mMaster.mServiceNameResolver != null) {
            printWriter.print(str);
            printWriter.print("Name resolver: ");
            this.mMaster.mServiceNameResolver.dumpShort(printWriter, this.mUserId);
            printWriter.println();
        }
        printWriter.print(str);
        printWriter.print("Disabled by UserManager: ");
        printWriter.println(this.mDisabled);
        printWriter.print(str);
        printWriter.print("Setup complete: ");
        printWriter.println(this.mSetupComplete);
        if (this.mServiceInfo != null) {
            printWriter.print(str);
            printWriter.print("Service UID: ");
            printWriter.println(this.mServiceInfo.applicationInfo.uid);
        }
        printWriter.println();
    }
}
