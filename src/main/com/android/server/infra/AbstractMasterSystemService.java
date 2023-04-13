package com.android.server.infra;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.PackageMonitor;
import com.android.internal.os.BackgroundThread;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.infra.AbstractMasterSystemService;
import com.android.server.infra.AbstractPerUserSystemService;
import com.android.server.infra.ServiceNameResolver;
import com.android.server.p011pm.UserManagerInternal;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public abstract class AbstractMasterSystemService<M extends AbstractMasterSystemService<M, S>, S extends AbstractPerUserSystemService<S, M>> extends SystemService {
    public boolean debug;
    @GuardedBy({"mLock"})
    public boolean mAllowInstantService;
    @GuardedBy({"mLock"})
    public final SparseBooleanArray mDisabledByUserRestriction;
    public final Object mLock;
    public final ServiceNameResolver mServiceNameResolver;
    public final int mServicePackagePolicyFlags;
    @GuardedBy({"mLock"})
    public final SparseArray<List<S>> mServicesCacheList;
    public final String mTag;
    public UserManagerInternal mUm;
    @GuardedBy({"mLock"})
    public SparseArray<String> mUpdatingPackageNames;
    public boolean verbose;

    /* loaded from: classes.dex */
    public interface Visitor<S> {
        void visit(S s);
    }

    public String getServiceSettingsProperty() {
        return null;
    }

    public abstract S newServiceLocked(int i, boolean z);

    @GuardedBy({"mLock"})
    public void onServiceEnabledLocked(S s, int i) {
    }

    public void onServiceRemoved(S s, int i) {
    }

    public void onSettingsChanged(int i, String str) {
    }

    public void registerForExtraSettingsChanges(ContentResolver contentResolver, ContentObserver contentObserver) {
    }

    public AbstractMasterSystemService(Context context, ServiceNameResolver serviceNameResolver, String str) {
        this(context, serviceNameResolver, str, 34);
    }

    public AbstractMasterSystemService(Context context, ServiceNameResolver serviceNameResolver, final String str, int i) {
        super(context);
        this.mTag = getClass().getSimpleName();
        this.mLock = new Object();
        this.verbose = false;
        this.debug = false;
        this.mServicesCacheList = new SparseArray<>();
        i = (i & 7) == 0 ? i | 2 : i;
        this.mServicePackagePolicyFlags = (i & 112) == 0 ? i | 32 : i;
        this.mServiceNameResolver = serviceNameResolver;
        if (serviceNameResolver != null) {
            serviceNameResolver.setOnTemporaryServiceNameChangedCallback(new ServiceNameResolver.NameResolverListener() { // from class: com.android.server.infra.AbstractMasterSystemService$$ExternalSyntheticLambda0
                @Override // com.android.server.infra.ServiceNameResolver.NameResolverListener
                public final void onNameResolved(int i2, String str2, boolean z) {
                    AbstractMasterSystemService.this.onServiceNameChanged(i2, str2, z);
                }
            });
        }
        if (str == null) {
            this.mDisabledByUserRestriction = null;
        } else {
            this.mDisabledByUserRestriction = new SparseBooleanArray();
            UserManagerInternal userManagerInternal = getUserManagerInternal();
            List<UserInfo> supportedUsers = getSupportedUsers();
            for (int i2 = 0; i2 < supportedUsers.size(); i2++) {
                int i3 = supportedUsers.get(i2).id;
                boolean userRestriction = userManagerInternal.getUserRestriction(i3, str);
                if (userRestriction) {
                    String str2 = this.mTag;
                    Slog.i(str2, "Disabling by restrictions user " + i3);
                    this.mDisabledByUserRestriction.put(i3, userRestriction);
                }
            }
            userManagerInternal.addUserRestrictionsListener(new UserManagerInternal.UserRestrictionsListener() { // from class: com.android.server.infra.AbstractMasterSystemService$$ExternalSyntheticLambda1
                @Override // com.android.server.p011pm.UserManagerInternal.UserRestrictionsListener
                public final void onUserRestrictionsChanged(int i4, Bundle bundle, Bundle bundle2) {
                    AbstractMasterSystemService.this.lambda$new$0(str, i4, bundle, bundle2);
                }
            });
        }
        startTrackingPackageChanges();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(String str, int i, Bundle bundle, Bundle bundle2) {
        boolean z = bundle.getBoolean(str, false);
        synchronized (this.mLock) {
            if (this.mDisabledByUserRestriction.get(i) == z && this.debug) {
                String str2 = this.mTag;
                Slog.d(str2, "Restriction did not change for user " + i);
                return;
            }
            String str3 = this.mTag;
            Slog.i(str3, "Updating for user " + i + ": disabled=" + z);
            this.mDisabledByUserRestriction.put(i, z);
            updateCachedServiceLocked(i, z);
        }
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 600) {
            new SettingsObserver(BackgroundThread.getHandler());
        }
    }

    @Override // com.android.server.SystemService
    public void onUserUnlocking(SystemService.TargetUser targetUser) {
        synchronized (this.mLock) {
            updateCachedServiceLocked(targetUser.getUserIdentifier());
        }
    }

    @Override // com.android.server.SystemService
    public void onUserStopped(SystemService.TargetUser targetUser) {
        synchronized (this.mLock) {
            removeCachedServiceListLocked(targetUser.getUserIdentifier());
        }
    }

    public final boolean getAllowInstantService() {
        boolean z;
        enforceCallingPermissionForManagement();
        synchronized (this.mLock) {
            z = this.mAllowInstantService;
        }
        return z;
    }

    public final boolean isBindInstantServiceAllowed() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mAllowInstantService;
        }
        return z;
    }

    public final void setAllowInstantService(boolean z) {
        String str = this.mTag;
        Slog.i(str, "setAllowInstantService(): " + z);
        enforceCallingPermissionForManagement();
        synchronized (this.mLock) {
            this.mAllowInstantService = z;
        }
    }

    public final void setTemporaryService(int i, String str, int i2) {
        String str2 = this.mTag;
        Slog.i(str2, "setTemporaryService(" + i + ") to " + str + " for " + i2 + "ms");
        if (this.mServiceNameResolver == null) {
            return;
        }
        enforceCallingPermissionForManagement();
        Objects.requireNonNull(str);
        int maximumTemporaryServiceDurationMs = getMaximumTemporaryServiceDurationMs();
        if (i2 > maximumTemporaryServiceDurationMs) {
            throw new IllegalArgumentException("Max duration is " + maximumTemporaryServiceDurationMs + " (called with " + i2 + ")");
        }
        synchronized (this.mLock) {
            S peekServiceForUserLocked = peekServiceForUserLocked(i);
            if (peekServiceForUserLocked != null) {
                peekServiceForUserLocked.removeSelfFromCache();
            }
            this.mServiceNameResolver.setTemporaryService(i, str, i2);
        }
    }

    public final void setTemporaryServices(int i, String[] strArr, int i2) {
        String str = this.mTag;
        Slog.i(str, "setTemporaryService(" + i + ") to " + Arrays.toString(strArr) + " for " + i2 + "ms");
        if (this.mServiceNameResolver == null) {
            return;
        }
        enforceCallingPermissionForManagement();
        Objects.requireNonNull(strArr);
        int maximumTemporaryServiceDurationMs = getMaximumTemporaryServiceDurationMs();
        if (i2 > maximumTemporaryServiceDurationMs) {
            throw new IllegalArgumentException("Max duration is " + maximumTemporaryServiceDurationMs + " (called with " + i2 + ")");
        }
        synchronized (this.mLock) {
            S peekServiceForUserLocked = peekServiceForUserLocked(i);
            if (peekServiceForUserLocked != null) {
                peekServiceForUserLocked.removeSelfFromCache();
            }
            this.mServiceNameResolver.setTemporaryServices(i, strArr, i2);
        }
    }

    public final boolean setDefaultServiceEnabled(int i, boolean z) {
        String str = this.mTag;
        Slog.i(str, "setDefaultServiceEnabled() for userId " + i + ": " + z);
        enforceCallingPermissionForManagement();
        synchronized (this.mLock) {
            ServiceNameResolver serviceNameResolver = this.mServiceNameResolver;
            if (serviceNameResolver == null) {
                return false;
            }
            if (!serviceNameResolver.setDefaultServiceEnabled(i, z)) {
                if (this.verbose) {
                    String str2 = this.mTag;
                    Slog.v(str2, "setDefaultServiceEnabled(" + i + "): already " + z);
                }
                return false;
            }
            S peekServiceForUserLocked = peekServiceForUserLocked(i);
            if (peekServiceForUserLocked != null) {
                peekServiceForUserLocked.removeSelfFromCache();
            }
            updateCachedServiceLocked(i);
            return true;
        }
    }

    public final boolean isDefaultServiceEnabled(int i) {
        boolean isDefaultServiceEnabled;
        enforceCallingPermissionForManagement();
        if (this.mServiceNameResolver == null) {
            return false;
        }
        synchronized (this.mLock) {
            isDefaultServiceEnabled = this.mServiceNameResolver.isDefaultServiceEnabled(i);
        }
        return isDefaultServiceEnabled;
    }

    public int getMaximumTemporaryServiceDurationMs() {
        throw new UnsupportedOperationException("Not implemented by " + getClass());
    }

    public final void resetTemporaryService(int i) {
        String str = this.mTag;
        Slog.i(str, "resetTemporaryService(): " + i);
        enforceCallingPermissionForManagement();
        synchronized (this.mLock) {
            S serviceForUserLocked = getServiceForUserLocked(i);
            if (serviceForUserLocked != null) {
                serviceForUserLocked.resetTemporaryServiceLocked();
            }
        }
    }

    public void enforceCallingPermissionForManagement() {
        throw new UnsupportedOperationException("Not implemented by " + getClass());
    }

    @GuardedBy({"mLock"})
    public List<S> newServiceListLocked(int i, boolean z, String[] strArr) {
        throw new UnsupportedOperationException("newServiceListLocked not implemented. ");
    }

    @GuardedBy({"mLock"})
    public S getServiceForUserLocked(int i) {
        List<S> serviceListForUserLocked = getServiceListForUserLocked(i);
        if (serviceListForUserLocked == null || serviceListForUserLocked.size() == 0) {
            return null;
        }
        return serviceListForUserLocked.get(0);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @GuardedBy({"mLock"})
    public List<S> getServiceListForUserLocked(int i) {
        List<S> arrayList;
        int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, false, null, null);
        List<S> list = this.mServicesCacheList.get(handleIncomingUser);
        if (list == null || list.size() == 0) {
            boolean isDisabledLocked = isDisabledLocked(i);
            ServiceNameResolver serviceNameResolver = this.mServiceNameResolver;
            if (serviceNameResolver != null && serviceNameResolver.isConfiguredInMultipleMode()) {
                arrayList = newServiceListLocked(handleIncomingUser, isDisabledLocked, this.mServiceNameResolver.getServiceNameList(i));
            } else {
                arrayList = new ArrayList<>();
                arrayList.add(newServiceLocked(handleIncomingUser, isDisabledLocked));
            }
            if (!isDisabledLocked) {
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    onServiceEnabledLocked(arrayList.get(i2), handleIncomingUser);
                }
            }
            this.mServicesCacheList.put(i, arrayList);
            return arrayList;
        }
        return list;
    }

    @GuardedBy({"mLock"})
    public S peekServiceForUserLocked(int i) {
        List<S> peekServiceListForUserLocked = peekServiceListForUserLocked(i);
        if (peekServiceListForUserLocked == null || peekServiceListForUserLocked.size() == 0) {
            return null;
        }
        return peekServiceListForUserLocked.get(0);
    }

    @GuardedBy({"mLock"})
    public List<S> peekServiceListForUserLocked(int i) {
        return this.mServicesCacheList.get(ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, false, null, null));
    }

    @GuardedBy({"mLock"})
    public void updateCachedServiceLocked(int i) {
        updateCachedServiceListLocked(i, isDisabledLocked(i));
    }

    @GuardedBy({"mLock"})
    public boolean isDisabledLocked(int i) {
        SparseBooleanArray sparseBooleanArray = this.mDisabledByUserRestriction;
        return sparseBooleanArray != null && sparseBooleanArray.get(i);
    }

    @GuardedBy({"mLock"})
    public S updateCachedServiceLocked(int i, boolean z) {
        S serviceForUserLocked = getServiceForUserLocked(i);
        updateCachedServiceListLocked(i, z);
        return serviceForUserLocked;
    }

    @GuardedBy({"mLock"})
    public List<S> updateCachedServiceListLocked(int i, boolean z) {
        ServiceNameResolver serviceNameResolver = this.mServiceNameResolver;
        if (serviceNameResolver != null && serviceNameResolver.isConfiguredInMultipleMode()) {
            return updateCachedServiceListMultiModeLocked(i, z);
        }
        List<S> serviceListForUserLocked = getServiceListForUserLocked(i);
        if (serviceListForUserLocked == null) {
            return null;
        }
        for (int i2 = 0; i2 < serviceListForUserLocked.size(); i2++) {
            S s = serviceListForUserLocked.get(i2);
            if (s != null) {
                synchronized (s.mLock) {
                    s.updateLocked(z);
                    if (!s.isEnabledLocked()) {
                        removeCachedServiceListLocked(i);
                    } else {
                        onServiceEnabledLocked(serviceListForUserLocked.get(i2), i);
                    }
                }
            }
        }
        return serviceListForUserLocked;
    }

    @GuardedBy({"mLock"})
    public final List<S> updateCachedServiceListMultiModeLocked(int i, boolean z) {
        List<S> serviceListForUserLocked;
        int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, false, null, null);
        new ArrayList();
        synchronized (this.mLock) {
            removeCachedServiceListLocked(handleIncomingUser);
            serviceListForUserLocked = getServiceListForUserLocked(i);
        }
        return serviceListForUserLocked;
    }

    @GuardedBy({"mLock"})
    public final List<S> removeCachedServiceListLocked(int i) {
        List<S> peekServiceListForUserLocked = peekServiceListForUserLocked(i);
        if (peekServiceListForUserLocked != null) {
            this.mServicesCacheList.delete(i);
            for (int i2 = 0; i2 < peekServiceListForUserLocked.size(); i2++) {
                onServiceRemoved(peekServiceListForUserLocked.get(i2), i);
            }
        }
        return peekServiceListForUserLocked;
    }

    @GuardedBy({"mLock"})
    public void onServicePackageUpdatingLocked(int i) {
        if (this.verbose) {
            String str = this.mTag;
            Slog.v(str, "onServicePackageUpdatingLocked(" + i + ")");
        }
    }

    @GuardedBy({"mLock"})
    public void onServicePackageUpdatedLocked(int i) {
        if (this.verbose) {
            String str = this.mTag;
            Slog.v(str, "onServicePackageUpdated(" + i + ")");
        }
    }

    @GuardedBy({"mLock"})
    public void onServicePackageDataClearedLocked(int i) {
        if (this.verbose) {
            String str = this.mTag;
            Slog.v(str, "onServicePackageDataCleared(" + i + ")");
        }
    }

    @GuardedBy({"mLock"})
    public void onServicePackageRestartedLocked(int i) {
        if (this.verbose) {
            String str = this.mTag;
            Slog.v(str, "onServicePackageRestarted(" + i + ")");
        }
    }

    public void onServiceNameChanged(int i, String str, boolean z) {
        synchronized (this.mLock) {
            updateCachedServiceListLocked(i, isDisabledLocked(i));
        }
    }

    @GuardedBy({"mLock"})
    public void visitServicesLocked(Visitor<S> visitor) {
        int size = this.mServicesCacheList.size();
        for (int i = 0; i < size; i++) {
            List<S> valueAt = this.mServicesCacheList.valueAt(i);
            for (int i2 = 0; i2 < valueAt.size(); i2++) {
                visitor.visit(valueAt.get(i2));
            }
        }
    }

    @GuardedBy({"mLock"})
    public void clearCacheLocked() {
        this.mServicesCacheList.clear();
    }

    public UserManagerInternal getUserManagerInternal() {
        if (this.mUm == null) {
            if (this.verbose) {
                Slog.v(this.mTag, "lazy-loading UserManagerInternal");
            }
            this.mUm = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
        }
        return this.mUm;
    }

    public List<UserInfo> getSupportedUsers() {
        UserInfo[] userInfos = getUserManagerInternal().getUserInfos();
        ArrayList arrayList = new ArrayList(userInfos.length);
        for (UserInfo userInfo : userInfos) {
            if (isUserSupported(new SystemService.TargetUser(userInfo))) {
                arrayList.add(userInfo);
            }
        }
        return arrayList;
    }

    public void assertCalledByPackageOwner(String str) {
        Objects.requireNonNull(str);
        int callingUid = Binder.getCallingUid();
        String[] packagesForUid = getContext().getPackageManager().getPackagesForUid(callingUid);
        if (packagesForUid != null) {
            for (String str2 : packagesForUid) {
                if (str.equals(str2)) {
                    return;
                }
            }
        }
        throw new SecurityException("UID " + callingUid + " does not own " + str);
    }

    @GuardedBy({"mLock"})
    public void dumpLocked(String str, PrintWriter printWriter) {
        boolean z = this.debug;
        boolean z2 = this.verbose;
        try {
            this.verbose = true;
            this.debug = true;
            int size = this.mServicesCacheList.size();
            printWriter.print(str);
            printWriter.print("Debug: ");
            printWriter.print(z);
            printWriter.print(" Verbose: ");
            printWriter.println(z2);
            printWriter.print("Package policy flags: ");
            printWriter.println(this.mServicePackagePolicyFlags);
            if (this.mUpdatingPackageNames != null) {
                printWriter.print("Packages being updated: ");
                printWriter.println(this.mUpdatingPackageNames);
            }
            dumpSupportedUsers(printWriter, str);
            if (this.mServiceNameResolver != null) {
                printWriter.print(str);
                printWriter.print("Name resolver: ");
                this.mServiceNameResolver.dumpShort(printWriter);
                printWriter.println();
                List<UserInfo> supportedUsers = getSupportedUsers();
                for (int i = 0; i < supportedUsers.size(); i++) {
                    int i2 = supportedUsers.get(i).id;
                    printWriter.print("    ");
                    printWriter.print(i2);
                    printWriter.print(": ");
                    this.mServiceNameResolver.dumpShort(printWriter, i2);
                    printWriter.println();
                }
            }
            printWriter.print(str);
            printWriter.print("Users disabled by restriction: ");
            printWriter.println(this.mDisabledByUserRestriction);
            printWriter.print(str);
            printWriter.print("Allow instant service: ");
            printWriter.println(this.mAllowInstantService);
            String serviceSettingsProperty = getServiceSettingsProperty();
            if (serviceSettingsProperty != null) {
                printWriter.print(str);
                printWriter.print("Settings property: ");
                printWriter.println(serviceSettingsProperty);
            }
            printWriter.print(str);
            printWriter.print("Cached services: ");
            if (size == 0) {
                printWriter.println("none");
            } else {
                printWriter.println(size);
                for (int i3 = 0; i3 < size; i3++) {
                    printWriter.print(str);
                    printWriter.print("Service at ");
                    printWriter.print(i3);
                    printWriter.println(": ");
                    List<S> valueAt = this.mServicesCacheList.valueAt(i3);
                    for (int i4 = 0; i4 < valueAt.size(); i4++) {
                        S s = valueAt.get(i4);
                        synchronized (s.mLock) {
                            s.dumpLocked("    ", printWriter);
                        }
                    }
                    printWriter.println();
                }
            }
        } finally {
            this.debug = z;
            this.verbose = z2;
        }
    }

    /* renamed from: com.android.server.infra.AbstractMasterSystemService$1 */
    /* loaded from: classes.dex */
    public class C09861 extends PackageMonitor {
        public C09861() {
        }

        public void onPackageUpdateStarted(String str, int i) {
            AbstractMasterSystemService abstractMasterSystemService = AbstractMasterSystemService.this;
            if (abstractMasterSystemService.verbose) {
                String str2 = abstractMasterSystemService.mTag;
                Slog.v(str2, "onPackageUpdateStarted(): " + str);
            }
            String activeServicePackageNameLocked = getActiveServicePackageNameLocked();
            if (str.equals(activeServicePackageNameLocked)) {
                int changingUserId = getChangingUserId();
                synchronized (AbstractMasterSystemService.this.mLock) {
                    if (AbstractMasterSystemService.this.mUpdatingPackageNames == null) {
                        AbstractMasterSystemService.this.mUpdatingPackageNames = new SparseArray(AbstractMasterSystemService.this.mServicesCacheList.size());
                    }
                    AbstractMasterSystemService.this.mUpdatingPackageNames.put(changingUserId, str);
                    AbstractMasterSystemService.this.onServicePackageUpdatingLocked(changingUserId);
                    if ((AbstractMasterSystemService.this.mServicePackagePolicyFlags & 1) != 0) {
                        AbstractMasterSystemService abstractMasterSystemService2 = AbstractMasterSystemService.this;
                        if (abstractMasterSystemService2.debug) {
                            String str3 = abstractMasterSystemService2.mTag;
                            Slog.d(str3, "Holding service for user " + changingUserId + " while package " + activeServicePackageNameLocked + " is being updated");
                        }
                    } else {
                        AbstractMasterSystemService abstractMasterSystemService3 = AbstractMasterSystemService.this;
                        if (abstractMasterSystemService3.debug) {
                            String str4 = abstractMasterSystemService3.mTag;
                            Slog.d(str4, "Removing service for user " + changingUserId + " because package " + activeServicePackageNameLocked + " is being updated");
                        }
                        AbstractMasterSystemService.this.removeCachedServiceListLocked(changingUserId);
                        if ((AbstractMasterSystemService.this.mServicePackagePolicyFlags & 4) != 0) {
                            AbstractMasterSystemService abstractMasterSystemService4 = AbstractMasterSystemService.this;
                            if (abstractMasterSystemService4.debug) {
                                String str5 = abstractMasterSystemService4.mTag;
                                Slog.d(str5, "Eagerly recreating service for user " + changingUserId);
                            }
                            AbstractMasterSystemService.this.getServiceForUserLocked(changingUserId);
                        }
                    }
                }
            }
        }

        public void onPackageUpdateFinished(String str, int i) {
            AbstractMasterSystemService abstractMasterSystemService = AbstractMasterSystemService.this;
            if (abstractMasterSystemService.verbose) {
                String str2 = abstractMasterSystemService.mTag;
                Slog.v(str2, "onPackageUpdateFinished(): " + str);
            }
            int changingUserId = getChangingUserId();
            synchronized (AbstractMasterSystemService.this.mLock) {
                if (str.equals(AbstractMasterSystemService.this.mUpdatingPackageNames == null ? null : (String) AbstractMasterSystemService.this.mUpdatingPackageNames.get(changingUserId))) {
                    if (AbstractMasterSystemService.this.mUpdatingPackageNames != null) {
                        AbstractMasterSystemService.this.mUpdatingPackageNames.remove(changingUserId);
                        if (AbstractMasterSystemService.this.mUpdatingPackageNames.size() == 0) {
                            AbstractMasterSystemService.this.mUpdatingPackageNames = null;
                        }
                    }
                    AbstractMasterSystemService.this.onServicePackageUpdatedLocked(changingUserId);
                } else {
                    handlePackageUpdateLocked(str);
                }
            }
        }

        public void onPackageRemoved(String str, int i) {
            ComponentName serviceComponentName;
            ServiceNameResolver serviceNameResolver = AbstractMasterSystemService.this.mServiceNameResolver;
            if (serviceNameResolver != null && serviceNameResolver.isConfiguredInMultipleMode()) {
                int changingUserId = getChangingUserId();
                synchronized (AbstractMasterSystemService.this.mLock) {
                    AbstractMasterSystemService.this.handlePackageRemovedMultiModeLocked(str, changingUserId);
                }
                return;
            }
            synchronized (AbstractMasterSystemService.this.mLock) {
                int changingUserId2 = getChangingUserId();
                AbstractPerUserSystemService peekServiceForUserLocked = AbstractMasterSystemService.this.peekServiceForUserLocked(changingUserId2);
                if (peekServiceForUserLocked != null && (serviceComponentName = peekServiceForUserLocked.getServiceComponentName()) != null && str.equals(serviceComponentName.getPackageName())) {
                    handleActiveServiceRemoved(changingUserId2);
                }
            }
        }

        public boolean onHandleForceStop(Intent intent, String[] strArr, int i, boolean z) {
            synchronized (AbstractMasterSystemService.this.mLock) {
                String activeServicePackageNameLocked = getActiveServicePackageNameLocked();
                for (String str : strArr) {
                    if (!str.equals(activeServicePackageNameLocked)) {
                        handlePackageUpdateLocked(str);
                    } else if (!z) {
                        return true;
                    } else {
                        String action = intent.getAction();
                        int changingUserId = getChangingUserId();
                        if ("android.intent.action.PACKAGE_RESTARTED".equals(action)) {
                            handleActiveServiceRestartedLocked(activeServicePackageNameLocked, changingUserId);
                        } else {
                            AbstractMasterSystemService.this.removeCachedServiceListLocked(changingUserId);
                        }
                    }
                }
                return false;
            }
        }

        public void onPackageDataCleared(String str, int i) {
            ComponentName serviceComponentName;
            AbstractMasterSystemService abstractMasterSystemService = AbstractMasterSystemService.this;
            if (abstractMasterSystemService.verbose) {
                String str2 = abstractMasterSystemService.mTag;
                Slog.v(str2, "onPackageDataCleared(): " + str);
            }
            int changingUserId = getChangingUserId();
            ServiceNameResolver serviceNameResolver = AbstractMasterSystemService.this.mServiceNameResolver;
            if (serviceNameResolver != null && serviceNameResolver.isConfiguredInMultipleMode()) {
                synchronized (AbstractMasterSystemService.this.mLock) {
                    AbstractMasterSystemService.this.onServicePackageDataClearedMultiModeLocked(str, changingUserId);
                }
                return;
            }
            synchronized (AbstractMasterSystemService.this.mLock) {
                AbstractPerUserSystemService peekServiceForUserLocked = AbstractMasterSystemService.this.peekServiceForUserLocked(changingUserId);
                if (peekServiceForUserLocked != null && (serviceComponentName = peekServiceForUserLocked.getServiceComponentName()) != null && str.equals(serviceComponentName.getPackageName())) {
                    AbstractMasterSystemService.this.onServicePackageDataClearedLocked(changingUserId);
                }
            }
        }

        public final void handleActiveServiceRemoved(int i) {
            synchronized (AbstractMasterSystemService.this.mLock) {
                AbstractMasterSystemService.this.removeCachedServiceListLocked(i);
            }
            String serviceSettingsProperty = AbstractMasterSystemService.this.getServiceSettingsProperty();
            if (serviceSettingsProperty != null) {
                Settings.Secure.putStringForUser(AbstractMasterSystemService.this.getContext().getContentResolver(), serviceSettingsProperty, null, i);
            }
        }

        @GuardedBy({"mLock"})
        public final void handleActiveServiceRestartedLocked(String str, int i) {
            if ((AbstractMasterSystemService.this.mServicePackagePolicyFlags & 16) != 0) {
                AbstractMasterSystemService abstractMasterSystemService = AbstractMasterSystemService.this;
                if (abstractMasterSystemService.debug) {
                    String str2 = abstractMasterSystemService.mTag;
                    Slog.d(str2, "Holding service for user " + i + " while package " + str + " is being restarted");
                }
            } else {
                AbstractMasterSystemService abstractMasterSystemService2 = AbstractMasterSystemService.this;
                if (abstractMasterSystemService2.debug) {
                    String str3 = abstractMasterSystemService2.mTag;
                    Slog.d(str3, "Removing service for user " + i + " because package " + str + " is being restarted");
                }
                AbstractMasterSystemService.this.removeCachedServiceListLocked(i);
                if ((AbstractMasterSystemService.this.mServicePackagePolicyFlags & 64) != 0) {
                    AbstractMasterSystemService abstractMasterSystemService3 = AbstractMasterSystemService.this;
                    if (abstractMasterSystemService3.debug) {
                        String str4 = abstractMasterSystemService3.mTag;
                        Slog.d(str4, "Eagerly recreating service for user " + i);
                    }
                    AbstractMasterSystemService.this.updateCachedServiceLocked(i);
                }
            }
            AbstractMasterSystemService.this.onServicePackageRestartedLocked(i);
        }

        public void onPackageModified(String str) {
            synchronized (AbstractMasterSystemService.this.mLock) {
                AbstractMasterSystemService abstractMasterSystemService = AbstractMasterSystemService.this;
                if (abstractMasterSystemService.verbose) {
                    Slog.v(abstractMasterSystemService.mTag, "onPackageModified(): " + str);
                }
                if (AbstractMasterSystemService.this.mServiceNameResolver == null) {
                    return;
                }
                int changingUserId = getChangingUserId();
                String[] defaultServiceNameList = AbstractMasterSystemService.this.mServiceNameResolver.getDefaultServiceNameList(changingUserId);
                if (defaultServiceNameList != null) {
                    for (String str2 : defaultServiceNameList) {
                        peekAndUpdateCachedServiceLocked(str, changingUserId, str2);
                    }
                }
            }
        }

        @GuardedBy({"mLock"})
        public final void peekAndUpdateCachedServiceLocked(String str, int i, String str2) {
            ComponentName unflattenFromString;
            AbstractPerUserSystemService peekServiceForUserLocked;
            if (str2 == null || (unflattenFromString = ComponentName.unflattenFromString(str2)) == null || !unflattenFromString.getPackageName().equals(str) || (peekServiceForUserLocked = AbstractMasterSystemService.this.peekServiceForUserLocked(i)) == null || peekServiceForUserLocked.getServiceComponentName() != null) {
                return;
            }
            AbstractMasterSystemService abstractMasterSystemService = AbstractMasterSystemService.this;
            if (abstractMasterSystemService.verbose) {
                Slog.v(abstractMasterSystemService.mTag, "update cached");
            }
            AbstractMasterSystemService.this.updateCachedServiceLocked(i);
        }

        @GuardedBy({"mLock"})
        public final String getActiveServicePackageNameLocked() {
            ComponentName serviceComponentName;
            AbstractPerUserSystemService peekServiceForUserLocked = AbstractMasterSystemService.this.peekServiceForUserLocked(getChangingUserId());
            if (peekServiceForUserLocked == null || (serviceComponentName = peekServiceForUserLocked.getServiceComponentName()) == null) {
                return null;
            }
            return serviceComponentName.getPackageName();
        }

        @GuardedBy({"mLock"})
        public final void handlePackageUpdateLocked(final String str) {
            AbstractMasterSystemService.this.visitServicesLocked(new Visitor() { // from class: com.android.server.infra.AbstractMasterSystemService$1$$ExternalSyntheticLambda0
                @Override // com.android.server.infra.AbstractMasterSystemService.Visitor
                public final void visit(Object obj) {
                    ((AbstractPerUserSystemService) obj).handlePackageUpdateLocked(str);
                }
            });
        }
    }

    public final void startTrackingPackageChanges() {
        new C09861().register(getContext(), (Looper) null, UserHandle.ALL, true);
    }

    @GuardedBy({"mLock"})
    public void onServicePackageDataClearedMultiModeLocked(String str, int i) {
        if (this.verbose) {
            String str2 = this.mTag;
            Slog.v(str2, "onServicePackageDataClearedMultiModeLocked(" + i + ")");
        }
    }

    @GuardedBy({"mLock"})
    public void handlePackageRemovedMultiModeLocked(String str, int i) {
        if (this.verbose) {
            String str2 = this.mTag;
            Slog.v(str2, "handlePackageRemovedMultiModeLocked(" + i + ")");
        }
    }

    @GuardedBy({"mLock"})
    public void removeServiceFromCache(S s, int i) {
        if (this.mServicesCacheList.get(i) != null) {
            this.mServicesCacheList.get(i).remove(s);
        }
    }

    @GuardedBy({"mLock"})
    public void removeServiceFromMultiModeSettings(String str, int i) {
        ServiceNameResolver serviceNameResolver;
        if (getServiceSettingsProperty() == null || (serviceNameResolver = this.mServiceNameResolver) == null || !serviceNameResolver.isConfiguredInMultipleMode()) {
            if (this.verbose) {
                Slog.v(this.mTag, "removeServiceFromSettings not implemented  for single backend implementation");
                return;
            }
            return;
        }
        String[] serviceNameList = this.mServiceNameResolver.getServiceNameList(i);
        ArrayList arrayList = new ArrayList();
        for (String str2 : serviceNameList) {
            if (!str2.equals(str)) {
                arrayList.add(str2);
            }
        }
        this.mServiceNameResolver.setServiceNameList(arrayList, i);
    }

    /* loaded from: classes.dex */
    public final class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
            ContentResolver contentResolver = AbstractMasterSystemService.this.getContext().getContentResolver();
            String serviceSettingsProperty = AbstractMasterSystemService.this.getServiceSettingsProperty();
            if (serviceSettingsProperty != null) {
                contentResolver.registerContentObserver(Settings.Secure.getUriFor(serviceSettingsProperty), false, this, -1);
            }
            contentResolver.registerContentObserver(Settings.Secure.getUriFor("user_setup_complete"), false, this, -1);
            AbstractMasterSystemService.this.registerForExtraSettingsChanges(contentResolver, this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri, int i) {
            AbstractMasterSystemService abstractMasterSystemService = AbstractMasterSystemService.this;
            if (abstractMasterSystemService.verbose) {
                String str = abstractMasterSystemService.mTag;
                Slog.v(str, "onChange(): uri=" + uri + ", userId=" + i);
            }
            String lastPathSegment = uri.getLastPathSegment();
            if (lastPathSegment == null) {
                return;
            }
            if (lastPathSegment.equals(AbstractMasterSystemService.this.getServiceSettingsProperty()) || lastPathSegment.equals("user_setup_complete")) {
                synchronized (AbstractMasterSystemService.this.mLock) {
                    AbstractMasterSystemService.this.updateCachedServiceLocked(i);
                }
                return;
            }
            AbstractMasterSystemService.this.onSettingsChanged(i, lastPathSegment);
        }
    }
}
