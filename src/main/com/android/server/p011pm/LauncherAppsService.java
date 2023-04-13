package com.android.server.p011pm;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.ActivityOptions;
import android.app.AppGlobals;
import android.app.IApplicationThread;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyCache;
import android.app.admin.DevicePolicyManager;
import android.app.usage.UsageStatsManagerInternal;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ILauncherApps;
import android.content.pm.IOnAppsChangedListener;
import android.content.pm.IPackageInstallerCallback;
import android.content.pm.IPackageManager;
import android.content.pm.IShortcutChangeCallback;
import android.content.pm.IncrementalStatesInfo;
import android.content.pm.LauncherActivityInfoInternal;
import android.content.pm.LauncherApps;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutQueryWrapper;
import android.content.pm.ShortcutServiceInternal;
import android.content.pm.UserInfo;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IInterface;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.p005os.IInstalld;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.content.PackageMonitor;
import com.android.internal.infra.AndroidFuture;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.CollectionUtils;
import com.android.internal.util.Preconditions;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.p011pm.LauncherAppsService;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
/* renamed from: com.android.server.pm.LauncherAppsService */
/* loaded from: classes2.dex */
public class LauncherAppsService extends SystemService {
    public final LauncherAppsImpl mLauncherAppsImpl;

    /* renamed from: com.android.server.pm.LauncherAppsService$LauncherAppsServiceInternal */
    /* loaded from: classes2.dex */
    public static abstract class LauncherAppsServiceInternal {
        public abstract boolean startShortcut(int i, int i2, String str, String str2, String str3, String str4, Rect rect, Bundle bundle, int i3);
    }

    public LauncherAppsService(Context context) {
        super(context);
        this.mLauncherAppsImpl = new LauncherAppsImpl(context);
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("launcherapps", this.mLauncherAppsImpl);
        this.mLauncherAppsImpl.registerLoadingProgressForIncrementalApps();
        LocalServices.addService(LauncherAppsServiceInternal.class, this.mLauncherAppsImpl.mInternal);
    }

    /* renamed from: com.android.server.pm.LauncherAppsService$BroadcastCookie */
    /* loaded from: classes2.dex */
    public static class BroadcastCookie {
        public final int callingPid;
        public final int callingUid;
        public final String packageName;
        public final UserHandle user;

        public BroadcastCookie(UserHandle userHandle, String str, int i, int i2) {
            this.user = userHandle;
            this.packageName = str;
            this.callingUid = i2;
            this.callingPid = i;
        }
    }

    @VisibleForTesting
    /* renamed from: com.android.server.pm.LauncherAppsService$LauncherAppsImpl */
    /* loaded from: classes2.dex */
    public static class LauncherAppsImpl extends ILauncherApps.Stub {
        public final ActivityManagerInternal mActivityManagerInternal;
        public final ActivityTaskManagerInternal mActivityTaskManagerInternal;
        public final Handler mCallbackHandler;
        public final Context mContext;
        public final DevicePolicyManager mDpm;
        public final IPackageManager mIPM;
        public final LauncherAppsServiceInternal mInternal;
        @GuardedBy({"mListeners"})
        public boolean mIsWatchingPackageBroadcasts;
        public PackageInstallerService mPackageInstallerService;
        public final PackageManagerInternal mPackageManagerInternal;
        public final MyPackageMonitor mPackageMonitor;
        public final ShortcutChangeHandler mShortcutChangeHandler;
        public final ShortcutServiceInternal mShortcutServiceInternal;
        public final UserManager mUm;
        public final UsageStatsManagerInternal mUsageStatsManagerInternal;
        public final UserManagerInternal mUserManagerInternal;
        public final PackageCallbackList<IOnAppsChangedListener> mListeners = new PackageCallbackList<>();
        public final PackageRemovedListener mPackageRemovedListener = new PackageRemovedListener();

        public LauncherAppsImpl(Context context) {
            MyPackageMonitor myPackageMonitor = new MyPackageMonitor();
            this.mPackageMonitor = myPackageMonitor;
            this.mIsWatchingPackageBroadcasts = false;
            this.mContext = context;
            this.mIPM = AppGlobals.getPackageManager();
            this.mUm = (UserManager) context.getSystemService("user");
            UserManagerInternal userManagerInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
            Objects.requireNonNull(userManagerInternal);
            this.mUserManagerInternal = userManagerInternal;
            UsageStatsManagerInternal usageStatsManagerInternal = (UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class);
            Objects.requireNonNull(usageStatsManagerInternal);
            this.mUsageStatsManagerInternal = usageStatsManagerInternal;
            ActivityManagerInternal activityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
            Objects.requireNonNull(activityManagerInternal);
            this.mActivityManagerInternal = activityManagerInternal;
            ActivityTaskManagerInternal activityTaskManagerInternal = (ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class);
            Objects.requireNonNull(activityTaskManagerInternal);
            this.mActivityTaskManagerInternal = activityTaskManagerInternal;
            ShortcutServiceInternal shortcutServiceInternal = (ShortcutServiceInternal) LocalServices.getService(ShortcutServiceInternal.class);
            Objects.requireNonNull(shortcutServiceInternal);
            this.mShortcutServiceInternal = shortcutServiceInternal;
            PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
            Objects.requireNonNull(packageManagerInternal);
            this.mPackageManagerInternal = packageManagerInternal;
            shortcutServiceInternal.addListener(myPackageMonitor);
            ShortcutChangeHandler shortcutChangeHandler = new ShortcutChangeHandler(userManagerInternal);
            this.mShortcutChangeHandler = shortcutChangeHandler;
            shortcutServiceInternal.addShortcutChangeCallback(shortcutChangeHandler);
            this.mCallbackHandler = BackgroundThread.getHandler();
            this.mDpm = (DevicePolicyManager) context.getSystemService("device_policy");
            this.mInternal = new LocalService();
        }

        @VisibleForTesting
        public int injectBinderCallingUid() {
            return ILauncherApps.Stub.getCallingUid();
        }

        @VisibleForTesting
        public int injectBinderCallingPid() {
            return ILauncherApps.Stub.getCallingPid();
        }

        public final int injectCallingUserId() {
            return UserHandle.getUserId(injectBinderCallingUid());
        }

        @VisibleForTesting
        public long injectClearCallingIdentity() {
            return Binder.clearCallingIdentity();
        }

        @VisibleForTesting
        public void injectRestoreCallingIdentity(long j) {
            Binder.restoreCallingIdentity(j);
        }

        public final int getCallingUserId() {
            return UserHandle.getUserId(injectBinderCallingUid());
        }

        public void addOnAppsChangedListener(String str, IOnAppsChangedListener iOnAppsChangedListener) throws RemoteException {
            verifyCallingPackage(str);
            synchronized (this.mListeners) {
                if (this.mListeners.getRegisteredCallbackCount() == 0) {
                    startWatchingPackageBroadcasts();
                }
                this.mListeners.unregister(iOnAppsChangedListener);
                this.mListeners.register(iOnAppsChangedListener, new BroadcastCookie(UserHandle.of(getCallingUserId()), str, injectBinderCallingPid(), injectBinderCallingUid()));
            }
        }

        public void removeOnAppsChangedListener(IOnAppsChangedListener iOnAppsChangedListener) throws RemoteException {
            synchronized (this.mListeners) {
                this.mListeners.unregister(iOnAppsChangedListener);
                if (this.mListeners.getRegisteredCallbackCount() == 0) {
                    stopWatchingPackageBroadcasts();
                }
            }
        }

        public void registerPackageInstallerCallback(String str, IPackageInstallerCallback iPackageInstallerCallback) {
            verifyCallingPackage(str);
            final UserHandle userHandle = new UserHandle(getCallingUserId());
            getPackageInstallerService().registerCallback(iPackageInstallerCallback, new IntPredicate() { // from class: com.android.server.pm.LauncherAppsService$LauncherAppsImpl$$ExternalSyntheticLambda1
                @Override // java.util.function.IntPredicate
                public final boolean test(int i) {
                    boolean lambda$registerPackageInstallerCallback$0;
                    lambda$registerPackageInstallerCallback$0 = LauncherAppsService.LauncherAppsImpl.this.lambda$registerPackageInstallerCallback$0(userHandle, i);
                    return lambda$registerPackageInstallerCallback$0;
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ boolean lambda$registerPackageInstallerCallback$0(UserHandle userHandle, int i) {
            return isEnabledProfileOf(userHandle, new UserHandle(i), "shouldReceiveEvent");
        }

        public ParceledListSlice<PackageInstaller.SessionInfo> getAllSessions(String str) {
            verifyCallingPackage(str);
            ArrayList arrayList = new ArrayList();
            int[] enabledProfileIds = this.mUm.getEnabledProfileIds(getCallingUserId());
            final int callingUid = Binder.getCallingUid();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                for (int i : enabledProfileIds) {
                    arrayList.addAll(getPackageInstallerService().getAllSessions(i).getList());
                }
                Binder.restoreCallingIdentity(clearCallingIdentity);
                arrayList.removeIf(new Predicate() { // from class: com.android.server.pm.LauncherAppsService$LauncherAppsImpl$$ExternalSyntheticLambda3
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$getAllSessions$1;
                        lambda$getAllSessions$1 = LauncherAppsService.LauncherAppsImpl.this.lambda$getAllSessions$1(callingUid, (PackageInstaller.SessionInfo) obj);
                        return lambda$getAllSessions$1;
                    }
                });
                return new ParceledListSlice<>(arrayList);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(clearCallingIdentity);
                throw th;
            }
        }

        /* renamed from: shouldFilterSession */
        public final boolean lambda$getAllSessions$1(int i, PackageInstaller.SessionInfo sessionInfo) {
            return (sessionInfo == null || i == sessionInfo.getInstallerUid() || this.mPackageManagerInternal.canQueryPackage(i, sessionInfo.getAppPackageName())) ? false : true;
        }

        public final PackageInstallerService getPackageInstallerService() {
            if (this.mPackageInstallerService == null) {
                try {
                    this.mPackageInstallerService = ServiceManager.getService("package").getPackageInstaller();
                } catch (RemoteException e) {
                    Slog.wtf("LauncherAppsService", "Error gettig IPackageInstaller", e);
                }
            }
            return this.mPackageInstallerService;
        }

        public final void startWatchingPackageBroadcasts() {
            if (this.mIsWatchingPackageBroadcasts) {
                return;
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.PACKAGE_REMOVED_INTERNAL");
            intentFilter.addDataScheme("package");
            this.mContext.registerReceiverAsUser(this.mPackageRemovedListener, UserHandle.ALL, intentFilter, null, this.mCallbackHandler);
            this.mPackageMonitor.register(this.mContext, UserHandle.ALL, true, this.mCallbackHandler);
            this.mIsWatchingPackageBroadcasts = true;
        }

        public final void stopWatchingPackageBroadcasts() {
            if (this.mIsWatchingPackageBroadcasts) {
                this.mContext.unregisterReceiver(this.mPackageRemovedListener);
                this.mPackageMonitor.unregister();
                this.mIsWatchingPackageBroadcasts = false;
            }
        }

        public void checkCallbackCount() {
            synchronized (this.mListeners) {
                if (this.mListeners.getRegisteredCallbackCount() == 0) {
                    stopWatchingPackageBroadcasts();
                }
            }
        }

        public final boolean canAccessProfile(int i, String str) {
            return canAccessProfile(injectBinderCallingUid(), injectCallingUserId(), injectBinderCallingPid(), i, str);
        }

        public final boolean canAccessProfile(int i, int i2, int i3, int i4, String str) {
            if (i4 == i2 || injectHasInteractAcrossUsersFullPermission(i3, i)) {
                return true;
            }
            long injectClearCallingIdentity = injectClearCallingIdentity();
            try {
                UserInfo userInfo = this.mUm.getUserInfo(i2);
                if (userInfo != null && userInfo.isProfile()) {
                    Slog.w("LauncherAppsService", str + " for another profile " + i4 + " from " + i2 + " not allowed");
                    injectRestoreCallingIdentity(injectClearCallingIdentity);
                    return false;
                }
                injectRestoreCallingIdentity(injectClearCallingIdentity);
                return this.mUserManagerInternal.isProfileAccessible(i2, i4, str, true);
            } catch (Throwable th) {
                injectRestoreCallingIdentity(injectClearCallingIdentity);
                throw th;
            }
        }

        public final void verifyCallingPackage(String str) {
            verifyCallingPackage(str, injectBinderCallingUid());
        }

        @VisibleForTesting
        public void verifyCallingPackage(String str, int i) {
            int i2;
            try {
                i2 = this.mIPM.getPackageUid(str, 794624L, UserHandle.getUserId(i));
            } catch (RemoteException unused) {
                i2 = -1;
            }
            if (i2 < 0) {
                Log.e("LauncherAppsService", "Package not found: " + str);
            }
            if (i2 != i) {
                throw new SecurityException("Calling package name mismatch");
            }
        }

        public final LauncherActivityInfoInternal getHiddenAppActivityInfo(String str, int i, UserHandle userHandle) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(str, PackageManager.APP_DETAILS_ACTIVITY_CLASS_NAME));
            List<LauncherActivityInfoInternal> queryIntentLauncherActivities = queryIntentLauncherActivities(intent, i, userHandle);
            if (queryIntentLauncherActivities.size() > 0) {
                return queryIntentLauncherActivities.get(0);
            }
            return null;
        }

        public boolean shouldHideFromSuggestions(String str, UserHandle userHandle) {
            int identifier = userHandle.getIdentifier();
            return (!canAccessProfile(identifier, "cannot get shouldHideFromSuggestions") || this.mPackageManagerInternal.filterAppAccess(str, Binder.getCallingUid(), identifier) || (this.mPackageManagerInternal.getDistractingPackageRestrictions(str, identifier) & 1) == 0) ? false : true;
        }

        public ParceledListSlice<LauncherActivityInfoInternal> getLauncherActivities(String str, String str2, UserHandle userHandle) throws RemoteException {
            LauncherActivityInfoInternal hiddenAppActivityInfo;
            LauncherActivityInfoInternal hiddenAppActivityInfo2;
            ParceledListSlice<LauncherActivityInfoInternal> queryActivitiesForUser = queryActivitiesForUser(str, new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER").setPackage(str2), userHandle);
            if (Settings.Global.getInt(this.mContext.getContentResolver(), "show_hidden_icon_apps_enabled", 1) == 0) {
                return queryActivitiesForUser;
            }
            if (queryActivitiesForUser == null) {
                return null;
            }
            int injectBinderCallingUid = injectBinderCallingUid();
            long injectClearCallingIdentity = injectClearCallingIdentity();
            try {
                if (this.mUm.getUserInfo(userHandle.getIdentifier()).isManagedProfile()) {
                    return queryActivitiesForUser;
                }
                if (this.mDpm.getDeviceOwnerComponentOnAnyUser() != null) {
                    return queryActivitiesForUser;
                }
                ArrayList arrayList = new ArrayList(queryActivitiesForUser.getList());
                if (str2 != null) {
                    if (arrayList.size() > 0) {
                        return queryActivitiesForUser;
                    }
                    if (shouldShowSyntheticActivity(userHandle, this.mPackageManagerInternal.getApplicationInfo(str2, 0L, injectBinderCallingUid, userHandle.getIdentifier())) && (hiddenAppActivityInfo2 = getHiddenAppActivityInfo(str2, injectBinderCallingUid, userHandle)) != null) {
                        arrayList.add(hiddenAppActivityInfo2);
                    }
                    return new ParceledListSlice<>(arrayList);
                }
                HashSet hashSet = new HashSet();
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    hashSet.add(((LauncherActivityInfoInternal) it.next()).getActivityInfo().packageName);
                }
                for (ApplicationInfo applicationInfo : this.mPackageManagerInternal.getInstalledApplications(0L, userHandle.getIdentifier(), injectBinderCallingUid)) {
                    if (!hashSet.contains(applicationInfo.packageName) && shouldShowSyntheticActivity(userHandle, applicationInfo) && (hiddenAppActivityInfo = getHiddenAppActivityInfo(applicationInfo.packageName, injectBinderCallingUid, userHandle)) != null) {
                        arrayList.add(hiddenAppActivityInfo);
                    }
                }
                return new ParceledListSlice<>(arrayList);
            } finally {
                injectRestoreCallingIdentity(injectClearCallingIdentity);
            }
        }

        public final boolean shouldShowSyntheticActivity(UserHandle userHandle, ApplicationInfo applicationInfo) {
            AndroidPackage androidPackage;
            return (applicationInfo == null || applicationInfo.isSystemApp() || applicationInfo.isUpdatedSystemApp() || isManagedProfileAdmin(userHandle, applicationInfo.packageName) || (androidPackage = this.mPackageManagerInternal.getPackage(applicationInfo.packageName)) == null || !requestsPermissions(androidPackage) || !hasDefaultEnableLauncherActivity(applicationInfo.packageName)) ? false : true;
        }

        public final boolean requestsPermissions(AndroidPackage androidPackage) {
            return !ArrayUtils.isEmpty(androidPackage.getRequestedPermissions());
        }

        public final boolean hasDefaultEnableLauncherActivity(String str) {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.LAUNCHER");
            intent.setPackage(str);
            List<ResolveInfo> queryIntentActivities = this.mPackageManagerInternal.queryIntentActivities(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), 512L, Binder.getCallingUid(), getCallingUserId());
            int size = queryIntentActivities.size();
            for (int i = 0; i < size; i++) {
                if (queryIntentActivities.get(i).activityInfo.enabled) {
                    return true;
                }
            }
            return false;
        }

        public final boolean isManagedProfileAdmin(UserHandle userHandle, String str) {
            ComponentName profileOwnerAsUser;
            List profiles = this.mUm.getProfiles(userHandle.getIdentifier());
            for (int i = 0; i < profiles.size(); i++) {
                UserInfo userInfo = (UserInfo) profiles.get(i);
                if (userInfo.isManagedProfile() && (profileOwnerAsUser = this.mDpm.getProfileOwnerAsUser(userInfo.getUserHandle())) != null && profileOwnerAsUser.getPackageName().equals(str)) {
                    return true;
                }
            }
            return false;
        }

        public LauncherActivityInfoInternal resolveLauncherActivityInternal(String str, ComponentName componentName, UserHandle userHandle) throws RemoteException {
            if (canAccessProfile(userHandle.getIdentifier(), "Cannot resolve activity")) {
                int injectBinderCallingUid = injectBinderCallingUid();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    ActivityInfo activityInfo = this.mPackageManagerInternal.getActivityInfo(componentName, 786432L, injectBinderCallingUid, userHandle.getIdentifier());
                    if (activityInfo == null) {
                        return null;
                    }
                    if (componentName != null && componentName.getPackageName() != null) {
                        IncrementalStatesInfo incrementalStatesInfo = this.mPackageManagerInternal.getIncrementalStatesInfo(componentName.getPackageName(), injectBinderCallingUid, userHandle.getIdentifier());
                        if (incrementalStatesInfo == null) {
                            return null;
                        }
                        return new LauncherActivityInfoInternal(activityInfo, incrementalStatesInfo, userHandle);
                    }
                    return null;
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            return null;
        }

        public ParceledListSlice getShortcutConfigActivities(String str, String str2, UserHandle userHandle) throws RemoteException {
            return queryActivitiesForUser(str, new Intent("android.intent.action.CREATE_SHORTCUT").setPackage(str2), userHandle);
        }

        public final ParceledListSlice<LauncherActivityInfoInternal> queryActivitiesForUser(String str, Intent intent, UserHandle userHandle) {
            if (canAccessProfile(userHandle.getIdentifier(), "Cannot retrieve activities")) {
                int injectBinderCallingUid = injectBinderCallingUid();
                long injectClearCallingIdentity = injectClearCallingIdentity();
                try {
                    return new ParceledListSlice<>(queryIntentLauncherActivities(intent, injectBinderCallingUid, userHandle));
                } finally {
                    injectRestoreCallingIdentity(injectClearCallingIdentity);
                }
            }
            return null;
        }

        public final List<LauncherActivityInfoInternal> queryIntentLauncherActivities(Intent intent, int i, UserHandle userHandle) {
            IncrementalStatesInfo incrementalStatesInfo;
            List<ResolveInfo> queryIntentActivities = this.mPackageManagerInternal.queryIntentActivities(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), 786432L, i, userHandle.getIdentifier());
            int size = queryIntentActivities.size();
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < size; i2++) {
                ResolveInfo resolveInfo = queryIntentActivities.get(i2);
                String str = resolveInfo.activityInfo.packageName;
                if (str != null && (incrementalStatesInfo = this.mPackageManagerInternal.getIncrementalStatesInfo(str, i, userHandle.getIdentifier())) != null) {
                    arrayList.add(new LauncherActivityInfoInternal(resolveInfo.activityInfo, incrementalStatesInfo, userHandle));
                }
            }
            return arrayList;
        }

        public IntentSender getShortcutConfigActivityIntent(String str, final ComponentName componentName, UserHandle userHandle) throws RemoteException {
            ensureShortcutPermission(str);
            IntentSender intentSender = null;
            if (canAccessProfile(userHandle.getIdentifier(), "Cannot check package")) {
                Objects.requireNonNull(componentName);
                int injectBinderCallingUid = injectBinderCallingUid();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    Intent intent = new Intent("android.intent.action.CREATE_SHORTCUT").setPackage(componentName.getPackageName());
                    if (this.mPackageManagerInternal.queryIntentActivities(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), 786432L, injectBinderCallingUid, userHandle.getIdentifier()).stream().anyMatch(new Predicate() { // from class: com.android.server.pm.LauncherAppsService$LauncherAppsImpl$$ExternalSyntheticLambda0
                        @Override // java.util.function.Predicate
                        public final boolean test(Object obj) {
                            boolean lambda$getShortcutConfigActivityIntent$2;
                            lambda$getShortcutConfigActivityIntent$2 = LauncherAppsService.LauncherAppsImpl.lambda$getShortcutConfigActivityIntent$2(componentName, (ResolveInfo) obj);
                            return lambda$getShortcutConfigActivityIntent$2;
                        }
                    })) {
                        PendingIntent activityAsUser = PendingIntent.getActivityAsUser(this.mContext, 0, new Intent("android.intent.action.CREATE_SHORTCUT").setComponent(componentName), 1409286144, null, userHandle);
                        if (activityAsUser != null) {
                            intentSender = activityAsUser.getIntentSender();
                        }
                        return intentSender;
                    }
                    return null;
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            return null;
        }

        public static /* synthetic */ boolean lambda$getShortcutConfigActivityIntent$2(ComponentName componentName, ResolveInfo resolveInfo) {
            return componentName.getClassName().equals(resolveInfo.activityInfo.name);
        }

        public PendingIntent getShortcutIntent(String str, String str2, String str3, Bundle bundle, UserHandle userHandle) throws RemoteException {
            Objects.requireNonNull(str);
            Objects.requireNonNull(str2);
            Objects.requireNonNull(str3);
            Objects.requireNonNull(userHandle);
            ensureShortcutPermission(str);
            if (canAccessProfile(userHandle.getIdentifier(), "Cannot get shortcuts")) {
                AndroidFuture androidFuture = new AndroidFuture();
                this.mShortcutServiceInternal.createShortcutIntentsAsync(getCallingUserId(), str, str2, str3, userHandle.getIdentifier(), injectBinderCallingPid(), injectBinderCallingUid(), androidFuture);
                try {
                    Intent[] intentArr = (Intent[]) androidFuture.get();
                    if (intentArr != null && intentArr.length != 0) {
                        long clearCallingIdentity = Binder.clearCallingIdentity();
                        try {
                            return injectCreatePendingIntent(0, intentArr, 201326592, bundle, str2, this.mPackageManagerInternal.getPackageUid(str2, 268435456L, userHandle.getIdentifier()));
                        } finally {
                            Binder.restoreCallingIdentity(clearCallingIdentity);
                        }
                    }
                } catch (InterruptedException | ExecutionException unused) {
                }
                return null;
            }
            return null;
        }

        public boolean isPackageEnabled(String str, String str2, UserHandle userHandle) throws RemoteException {
            boolean z = false;
            if (canAccessProfile(userHandle.getIdentifier(), "Cannot check package")) {
                int injectBinderCallingUid = injectBinderCallingUid();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    PackageInfo packageInfo = this.mPackageManagerInternal.getPackageInfo(str2, 786432L, injectBinderCallingUid, userHandle.getIdentifier());
                    if (packageInfo != null) {
                        if (packageInfo.applicationInfo.enabled) {
                            z = true;
                        }
                    }
                    return z;
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            return false;
        }

        public Bundle getSuspendedPackageLauncherExtras(String str, UserHandle userHandle) {
            int injectBinderCallingUid = injectBinderCallingUid();
            int identifier = userHandle.getIdentifier();
            if (canAccessProfile(identifier, "Cannot get launcher extras") && !this.mPackageManagerInternal.filterAppAccess(str, injectBinderCallingUid, identifier)) {
                return this.mPackageManagerInternal.getSuspendedPackageLauncherExtras(str, identifier);
            }
            return null;
        }

        public ApplicationInfo getApplicationInfo(String str, String str2, int i, UserHandle userHandle) throws RemoteException {
            if (canAccessProfile(userHandle.getIdentifier(), "Cannot check package")) {
                int injectBinderCallingUid = injectBinderCallingUid();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    return this.mPackageManagerInternal.getApplicationInfo(str2, i, injectBinderCallingUid, userHandle.getIdentifier());
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            return null;
        }

        public LauncherApps.AppUsageLimit getAppUsageLimit(String str, String str2, UserHandle userHandle) {
            verifyCallingPackage(str);
            if (canAccessProfile(userHandle.getIdentifier(), "Cannot access usage limit")) {
                if (!this.mActivityTaskManagerInternal.isCallerRecents(Binder.getCallingUid())) {
                    throw new SecurityException("Caller is not the recents app");
                }
                UsageStatsManagerInternal.AppUsageLimitData appUsageLimit = this.mUsageStatsManagerInternal.getAppUsageLimit(str2, userHandle);
                if (appUsageLimit == null) {
                    return null;
                }
                return new LauncherApps.AppUsageLimit(appUsageLimit.getTotalUsageLimit(), appUsageLimit.getUsageRemaining());
            }
            return null;
        }

        public final void ensureShortcutPermission(String str) {
            ensureShortcutPermission(injectBinderCallingUid(), injectBinderCallingPid(), str);
        }

        public final void ensureShortcutPermission(int i, int i2, String str) {
            verifyCallingPackage(str, i);
            if (!this.mShortcutServiceInternal.hasShortcutHostPermission(UserHandle.getUserId(i), str, i2, i)) {
                throw new SecurityException("Caller can't access shortcut information");
            }
        }

        public final void ensureStrictAccessShortcutsPermission(String str) {
            verifyCallingPackage(str);
            if (!injectHasAccessShortcutsPermission(injectBinderCallingPid(), injectBinderCallingUid())) {
                throw new SecurityException("Caller can't access shortcut information");
            }
        }

        @VisibleForTesting
        public boolean injectHasAccessShortcutsPermission(int i, int i2) {
            return this.mContext.checkPermission("android.permission.ACCESS_SHORTCUTS", i, i2) == 0;
        }

        @VisibleForTesting
        public boolean injectHasInteractAcrossUsersFullPermission(int i, int i2) {
            return this.mContext.checkPermission("android.permission.INTERACT_ACROSS_USERS_FULL", i, i2) == 0;
        }

        @VisibleForTesting
        public PendingIntent injectCreatePendingIntent(int i, Intent[] intentArr, int i2, Bundle bundle, String str, int i3) {
            return this.mActivityManagerInternal.getPendingIntentActivityAsApp(i, intentArr, i2, (Bundle) null, str, i3);
        }

        public ParceledListSlice getShortcuts(String str, ShortcutQueryWrapper shortcutQueryWrapper, UserHandle userHandle) {
            ensureShortcutPermission(str);
            if (!canAccessProfile(userHandle.getIdentifier(), "Cannot get shortcuts")) {
                return new ParceledListSlice(Collections.EMPTY_LIST);
            }
            long changedSince = shortcutQueryWrapper.getChangedSince();
            String str2 = shortcutQueryWrapper.getPackage();
            List shortcutIds = shortcutQueryWrapper.getShortcutIds();
            List locusIds = shortcutQueryWrapper.getLocusIds();
            ComponentName activity = shortcutQueryWrapper.getActivity();
            int queryFlags = shortcutQueryWrapper.getQueryFlags();
            if (shortcutIds == null || str2 != null) {
                if (locusIds != null && str2 == null) {
                    throw new IllegalArgumentException("To query by locus ID, package name must also be set");
                }
                if ((shortcutQueryWrapper.getQueryFlags() & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) != 0) {
                    ensureStrictAccessShortcutsPermission(str);
                }
                return new ParceledListSlice(this.mShortcutServiceInternal.getShortcuts(getCallingUserId(), str, changedSince, str2, shortcutIds, locusIds, activity, queryFlags, userHandle.getIdentifier(), injectBinderCallingPid(), injectBinderCallingUid()));
            }
            throw new IllegalArgumentException("To query by shortcut ID, package name must also be set");
        }

        public void getShortcutsAsync(String str, ShortcutQueryWrapper shortcutQueryWrapper, UserHandle userHandle, AndroidFuture<List<ShortcutInfo>> androidFuture) {
            ensureShortcutPermission(str);
            if (!canAccessProfile(userHandle.getIdentifier(), "Cannot get shortcuts")) {
                androidFuture.complete(Collections.EMPTY_LIST);
                return;
            }
            long changedSince = shortcutQueryWrapper.getChangedSince();
            String str2 = shortcutQueryWrapper.getPackage();
            List shortcutIds = shortcutQueryWrapper.getShortcutIds();
            List locusIds = shortcutQueryWrapper.getLocusIds();
            ComponentName activity = shortcutQueryWrapper.getActivity();
            int queryFlags = shortcutQueryWrapper.getQueryFlags();
            if (shortcutIds != null && str2 == null) {
                throw new IllegalArgumentException("To query by shortcut ID, package name must also be set");
            }
            if (locusIds != null && str2 == null) {
                throw new IllegalArgumentException("To query by locus ID, package name must also be set");
            }
            if ((shortcutQueryWrapper.getQueryFlags() & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) != 0) {
                ensureStrictAccessShortcutsPermission(str);
            }
            this.mShortcutServiceInternal.getShortcutsAsync(getCallingUserId(), str, changedSince, str2, shortcutIds, locusIds, activity, queryFlags, userHandle.getIdentifier(), injectBinderCallingPid(), injectBinderCallingUid(), androidFuture);
        }

        public void registerShortcutChangeCallback(String str, ShortcutQueryWrapper shortcutQueryWrapper, IShortcutChangeCallback iShortcutChangeCallback) {
            ensureShortcutPermission(str);
            if (shortcutQueryWrapper.getShortcutIds() != null && shortcutQueryWrapper.getPackage() == null) {
                throw new IllegalArgumentException("To query by shortcut ID, package name must also be set");
            }
            if (shortcutQueryWrapper.getLocusIds() != null && shortcutQueryWrapper.getPackage() == null) {
                throw new IllegalArgumentException("To query by locus ID, package name must also be set");
            }
            UserHandle of = UserHandle.of(injectCallingUserId());
            if (injectHasInteractAcrossUsersFullPermission(injectBinderCallingPid(), injectBinderCallingUid())) {
                of = null;
            }
            this.mShortcutChangeHandler.addShortcutChangeCallback(iShortcutChangeCallback, shortcutQueryWrapper, of);
        }

        public void unregisterShortcutChangeCallback(String str, IShortcutChangeCallback iShortcutChangeCallback) {
            ensureShortcutPermission(str);
            this.mShortcutChangeHandler.removeShortcutChangeCallback(iShortcutChangeCallback);
        }

        public void pinShortcuts(String str, String str2, List<String> list, UserHandle userHandle) {
            ensureShortcutPermission(str);
            if (canAccessProfile(userHandle.getIdentifier(), "Cannot pin shortcuts")) {
                this.mShortcutServiceInternal.pinShortcuts(getCallingUserId(), str, str2, list, userHandle.getIdentifier());
            }
        }

        public void cacheShortcuts(String str, String str2, List<String> list, UserHandle userHandle, int i) {
            ensureStrictAccessShortcutsPermission(str);
            if (canAccessProfile(userHandle.getIdentifier(), "Cannot cache shortcuts")) {
                this.mShortcutServiceInternal.cacheShortcuts(getCallingUserId(), str, str2, list, userHandle.getIdentifier(), toShortcutsCacheFlags(i));
            }
        }

        public void uncacheShortcuts(String str, String str2, List<String> list, UserHandle userHandle, int i) {
            ensureStrictAccessShortcutsPermission(str);
            if (canAccessProfile(userHandle.getIdentifier(), "Cannot uncache shortcuts")) {
                this.mShortcutServiceInternal.uncacheShortcuts(getCallingUserId(), str, str2, list, userHandle.getIdentifier(), toShortcutsCacheFlags(i));
            }
        }

        public int getShortcutIconResId(String str, String str2, String str3, int i) {
            ensureShortcutPermission(str);
            if (canAccessProfile(i, "Cannot access shortcuts")) {
                return this.mShortcutServiceInternal.getShortcutIconResId(getCallingUserId(), str, str2, str3, i);
            }
            return 0;
        }

        public ParcelFileDescriptor getShortcutIconFd(String str, String str2, String str3, int i) {
            ensureShortcutPermission(str);
            if (canAccessProfile(i, "Cannot access shortcuts")) {
                AndroidFuture androidFuture = new AndroidFuture();
                this.mShortcutServiceInternal.getShortcutIconFdAsync(getCallingUserId(), str, str2, str3, i, androidFuture);
                try {
                    return (ParcelFileDescriptor) androidFuture.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        }

        public String getShortcutIconUri(String str, String str2, String str3, int i) {
            ensureShortcutPermission(str);
            if (canAccessProfile(i, "Cannot access shortcuts")) {
                AndroidFuture androidFuture = new AndroidFuture();
                this.mShortcutServiceInternal.getShortcutIconUriAsync(getCallingUserId(), str, str2, str3, i, androidFuture);
                try {
                    return (String) androidFuture.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        }

        public boolean hasShortcutHostPermission(String str) {
            verifyCallingPackage(str);
            return this.mShortcutServiceInternal.hasShortcutHostPermission(getCallingUserId(), str, injectBinderCallingPid(), injectBinderCallingUid());
        }

        public Map<String, LauncherActivityInfoInternal> getActivityOverrides(String str, int i) {
            ensureShortcutPermission(str);
            int callingUid = Binder.getCallingUid();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                ArrayMap arrayMap = new ArrayMap();
                UserHandle managedProfile = getManagedProfile(i);
                if (managedProfile == null) {
                    return arrayMap;
                }
                for (String str2 : DevicePolicyCache.getInstance().getLauncherShortcutOverrides()) {
                    List<LauncherActivityInfoInternal> queryIntentLauncherActivities = queryIntentLauncherActivities(new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER").setPackage(str2), callingUid, managedProfile);
                    if (!queryIntentLauncherActivities.isEmpty()) {
                        arrayMap.put(str2, queryIntentLauncherActivities.get(0));
                    }
                }
                return arrayMap;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public final UserHandle getManagedProfile(int i) {
            for (UserInfo userInfo : this.mUm.getProfiles(i)) {
                if (userInfo.isManagedProfile()) {
                    return userInfo.getUserHandle();
                }
            }
            return null;
        }

        public boolean startShortcut(String str, String str2, String str3, String str4, Rect rect, Bundle bundle, int i) {
            return startShortcutInner(injectBinderCallingUid(), injectBinderCallingPid(), injectCallingUserId(), str, str2, str3, str4, rect, bundle, i);
        }

        public final boolean startShortcutInner(int i, int i2, int i3, String str, String str2, String str3, String str4, Rect rect, Bundle bundle, int i4) {
            Bundle bundle2;
            verifyCallingPackage(str, i);
            if (canAccessProfile(i4, "Cannot start activity")) {
                if (!this.mShortcutServiceInternal.isPinnedByCaller(i3, str, str2, str4, i4)) {
                    ensureShortcutPermission(i, i2, str);
                }
                AndroidFuture androidFuture = new AndroidFuture();
                this.mShortcutServiceInternal.createShortcutIntentsAsync(getCallingUserId(), str, str2, str4, i4, injectBinderCallingPid(), injectBinderCallingUid(), androidFuture);
                try {
                    Intent[] intentArr = (Intent[]) androidFuture.get();
                    if (intentArr != null && intentArr.length != 0) {
                        ActivityOptions fromBundle = ActivityOptions.fromBundle(bundle);
                        if (fromBundle != null) {
                            if (fromBundle.isApplyActivityFlagsForBubbles()) {
                                intentArr[0].addFlags(524288);
                                intentArr[0].addFlags(134217728);
                            }
                            if (fromBundle.isApplyMultipleTaskFlagForShortcut()) {
                                intentArr[0].addFlags(134217728);
                            }
                            if (fromBundle.isApplyNoUserActionFlagForShortcut()) {
                                intentArr[0].addFlags(262144);
                            }
                        }
                        intentArr[0].addFlags(268435456);
                        intentArr[0].setSourceBounds(rect);
                        String shortcutStartingThemeResName = this.mShortcutServiceInternal.getShortcutStartingThemeResName(i3, str, str2, str4, i4);
                        if (shortcutStartingThemeResName == null || shortcutStartingThemeResName.isEmpty()) {
                            bundle2 = bundle;
                        } else {
                            Bundle bundle3 = bundle == null ? new Bundle() : bundle;
                            bundle3.putString("android.activity.splashScreenTheme", shortcutStartingThemeResName);
                            bundle2 = bundle3;
                        }
                        return startShortcutIntentsAsPublisher(intentArr, str2, str3, bundle2, i4);
                    }
                } catch (InterruptedException | ExecutionException unused) {
                }
                return false;
            }
            return false;
        }

        public final boolean startShortcutIntentsAsPublisher(Intent[] intentArr, String str, String str2, Bundle bundle, int i) {
            int startActivitiesAsPackage;
            try {
                startActivitiesAsPackage = this.mActivityTaskManagerInternal.startActivitiesAsPackage(str, str2, i, intentArr, getActivityOptionsForLauncher(bundle));
            } catch (SecurityException unused) {
            }
            if (ActivityManager.isStartResultSuccessful(startActivitiesAsPackage)) {
                return true;
            }
            Log.e("LauncherAppsService", "Couldn't start activity, code=" + startActivitiesAsPackage);
            return false;
        }

        public final Bundle getActivityOptionsForLauncher(Bundle bundle) {
            if (bundle == null) {
                return ActivityOptions.makeBasic().setPendingIntentBackgroundActivityStartMode(1).toBundle();
            }
            ActivityOptions fromBundle = ActivityOptions.fromBundle(bundle);
            return fromBundle.getPendingIntentBackgroundActivityStartMode() == 0 ? fromBundle.setPendingIntentBackgroundActivityStartMode(1).toBundle() : bundle;
        }

        public boolean isActivityEnabled(String str, ComponentName componentName, UserHandle userHandle) throws RemoteException {
            boolean z = false;
            if (canAccessProfile(userHandle.getIdentifier(), "Cannot check component")) {
                int injectBinderCallingUid = injectBinderCallingUid();
                int componentEnabledSetting = this.mPackageManagerInternal.getComponentEnabledSetting(componentName, injectBinderCallingUid, userHandle.getIdentifier());
                if (componentEnabledSetting != 1) {
                    if (componentEnabledSetting == 2 || componentEnabledSetting == 3 || componentEnabledSetting == 4) {
                        return false;
                    }
                    long clearCallingIdentity = Binder.clearCallingIdentity();
                    try {
                        ActivityInfo activityInfo = this.mPackageManagerInternal.getActivityInfo(componentName, 786432L, injectBinderCallingUid, userHandle.getIdentifier());
                        if (activityInfo != null) {
                            if (activityInfo.isEnabled()) {
                                z = true;
                            }
                        }
                        return z;
                    } finally {
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                    }
                }
                return true;
            }
            return false;
        }

        public void startSessionDetailsActivityAsUser(IApplicationThread iApplicationThread, String str, String str2, PackageInstaller.SessionInfo sessionInfo, Rect rect, Bundle bundle, UserHandle userHandle) throws RemoteException {
            int identifier = userHandle.getIdentifier();
            if (canAccessProfile(identifier, "Cannot start details activity")) {
                Intent putExtra = new Intent("android.intent.action.VIEW").setData(new Uri.Builder().scheme("market").authority("details").appendQueryParameter("id", sessionInfo.appPackageName).build()).putExtra("android.intent.extra.REFERRER", new Uri.Builder().scheme("android-app").authority(str).build());
                putExtra.setSourceBounds(rect);
                this.mActivityTaskManagerInternal.startActivityAsUser(iApplicationThread, str, str2, putExtra, null, 268435456, getActivityOptionsForLauncher(bundle), identifier);
            }
        }

        public PendingIntent getActivityLaunchIntent(String str, ComponentName componentName, UserHandle userHandle) {
            ensureShortcutPermission(str);
            if (!canAccessProfile(userHandle.getIdentifier(), "Cannot start activity")) {
                throw new ActivityNotFoundException("Activity could not be found");
            }
            Intent mainActivityLaunchIntent = getMainActivityLaunchIntent(componentName, userHandle);
            if (mainActivityLaunchIntent == null) {
                throw new SecurityException("Attempt to launch activity without  category Intent.CATEGORY_LAUNCHER " + componentName);
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PendingIntent.getActivityAsUser(this.mContext, 0, mainActivityLaunchIntent, 33554432, null, userHandle);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void startActivityAsUser(IApplicationThread iApplicationThread, String str, String str2, ComponentName componentName, Rect rect, Bundle bundle, UserHandle userHandle) throws RemoteException {
            if (canAccessProfile(userHandle.getIdentifier(), "Cannot start activity")) {
                Intent mainActivityLaunchIntent = getMainActivityLaunchIntent(componentName, userHandle);
                if (mainActivityLaunchIntent == null) {
                    throw new SecurityException("Attempt to launch activity without  category Intent.CATEGORY_LAUNCHER " + componentName);
                }
                mainActivityLaunchIntent.setSourceBounds(rect);
                this.mActivityTaskManagerInternal.startActivityAsUser(iApplicationThread, str, str2, mainActivityLaunchIntent, null, 268435456, getActivityOptionsForLauncher(bundle), userHandle.getIdentifier());
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:16:0x0087, code lost:
            if (r0 != false) goto L18;
         */
        /* JADX WARN: Code restructure failed: missing block: B:18:0x008c, code lost:
            return null;
         */
        /* JADX WARN: Code restructure failed: missing block: B:20:0x0090, code lost:
            return r7;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public final Intent getMainActivityLaunchIntent(ComponentName componentName, UserHandle userHandle) {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.LAUNCHER");
            intent.addFlags(270532608);
            intent.setPackage(componentName.getPackageName());
            int injectBinderCallingUid = injectBinderCallingUid();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                List<ResolveInfo> queryIntentActivities = this.mPackageManagerInternal.queryIntentActivities(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), 786432L, injectBinderCallingUid, userHandle.getIdentifier());
                int size = queryIntentActivities.size();
                boolean z = false;
                int i = 0;
                while (true) {
                    if (i >= size) {
                        break;
                    }
                    ActivityInfo activityInfo = queryIntentActivities.get(i).activityInfo;
                    if (!activityInfo.packageName.equals(componentName.getPackageName()) || !activityInfo.name.equals(componentName.getClassName())) {
                        i++;
                    } else if (!activityInfo.exported) {
                        throw new SecurityException("Cannot launch non-exported components " + componentName);
                    } else {
                        intent.setPackage(null);
                        intent.setComponent(componentName);
                        z = true;
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void showAppDetailsAsUser(IApplicationThread iApplicationThread, String str, String str2, ComponentName componentName, Rect rect, Bundle bundle, UserHandle userHandle) throws RemoteException {
            int i;
            if (canAccessProfile(userHandle.getIdentifier(), "Cannot show app details")) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    String packageName = componentName.getPackageName();
                    try {
                        i = this.mContext.getPackageManager().getApplicationInfo(packageName, 4194304).uid;
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.d("LauncherAppsService", "package not found: " + e);
                        i = -1;
                    }
                    Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.fromParts("package", packageName, null));
                    intent.putExtra("uId", i);
                    intent.setFlags(268468224);
                    intent.setSourceBounds(rect);
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    this.mActivityTaskManagerInternal.startActivityAsUser(iApplicationThread, str, str2, intent, null, 268435456, getActivityOptionsForLauncher(bundle), userHandle.getIdentifier());
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    throw th;
                }
            }
        }

        public final boolean isEnabledProfileOf(UserHandle userHandle, UserHandle userHandle2, String str) {
            return this.mUserManagerInternal.isProfileAccessible(userHandle.getIdentifier(), userHandle2.getIdentifier(), str, false);
        }

        public final boolean isPackageVisibleToListener(String str, BroadcastCookie broadcastCookie, UserHandle userHandle) {
            return !this.mPackageManagerInternal.filterAppAccess(str, broadcastCookie.callingUid, userHandle.getIdentifier(), false);
        }

        public static boolean isCallingAppIdAllowed(int[] iArr, int i) {
            return iArr == null || i < 10000 || Arrays.binarySearch(iArr, i) > -1;
        }

        public final String[] getFilteredPackageNames(String[] strArr, BroadcastCookie broadcastCookie, UserHandle userHandle) {
            ArrayList arrayList = new ArrayList();
            for (String str : strArr) {
                if (isPackageVisibleToListener(str, broadcastCookie, userHandle)) {
                    arrayList.add(str);
                }
            }
            return (String[]) arrayList.toArray(new String[arrayList.size()]);
        }

        public final int toShortcutsCacheFlags(int i) {
            int i2 = i == 0 ? 16384 : i == 1 ? 1073741824 : i == 2 ? 536870912 : 0;
            Preconditions.checkArgumentPositive(i2, "Invalid cache owner");
            return i2;
        }

        @VisibleForTesting
        public void postToPackageMonitorHandler(Runnable runnable) {
            this.mCallbackHandler.post(runnable);
        }

        public void registerLoadingProgressForIncrementalApps() {
            List<UserHandle> userProfiles = this.mUm.getUserProfiles();
            if (userProfiles == null) {
                return;
            }
            for (final UserHandle userHandle : userProfiles) {
                this.mPackageManagerInternal.forEachInstalledPackage(new Consumer() { // from class: com.android.server.pm.LauncherAppsService$LauncherAppsImpl$$ExternalSyntheticLambda2
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        LauncherAppsService.LauncherAppsImpl.this.lambda$registerLoadingProgressForIncrementalApps$3(userHandle, (AndroidPackage) obj);
                    }
                }, userHandle.getIdentifier());
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$registerLoadingProgressForIncrementalApps$3(UserHandle userHandle, AndroidPackage androidPackage) {
            String packageName = androidPackage.getPackageName();
            if (this.mPackageManagerInternal.getIncrementalStatesInfo(packageName, Process.myUid(), userHandle.getIdentifier()).isLoading()) {
                this.mPackageManagerInternal.registerInstalledLoadingProgressCallback(packageName, new PackageLoadingProgressCallback(packageName, userHandle), userHandle.getIdentifier());
            }
        }

        /* renamed from: com.android.server.pm.LauncherAppsService$LauncherAppsImpl$ShortcutChangeHandler */
        /* loaded from: classes2.dex */
        public static class ShortcutChangeHandler implements LauncherApps.ShortcutChangeCallback {
            public final RemoteCallbackList<IShortcutChangeCallback> mCallbacks = new RemoteCallbackList<>();
            public final UserManagerInternal mUserManagerInternal;

            public ShortcutChangeHandler(UserManagerInternal userManagerInternal) {
                this.mUserManagerInternal = userManagerInternal;
            }

            public synchronized void addShortcutChangeCallback(IShortcutChangeCallback iShortcutChangeCallback, ShortcutQueryWrapper shortcutQueryWrapper, UserHandle userHandle) {
                this.mCallbacks.unregister(iShortcutChangeCallback);
                this.mCallbacks.register(iShortcutChangeCallback, new Pair(shortcutQueryWrapper, userHandle));
            }

            public synchronized void removeShortcutChangeCallback(IShortcutChangeCallback iShortcutChangeCallback) {
                this.mCallbacks.unregister(iShortcutChangeCallback);
            }

            public void onShortcutsAddedOrUpdated(String str, List<ShortcutInfo> list, UserHandle userHandle) {
                onShortcutEvent(str, list, userHandle, false);
            }

            public void onShortcutsRemoved(String str, List<ShortcutInfo> list, UserHandle userHandle) {
                onShortcutEvent(str, list, userHandle, true);
            }

            public final void onShortcutEvent(String str, List<ShortcutInfo> list, UserHandle userHandle, boolean z) {
                int beginBroadcast = this.mCallbacks.beginBroadcast();
                for (int i = 0; i < beginBroadcast; i++) {
                    IShortcutChangeCallback broadcastItem = this.mCallbacks.getBroadcastItem(i);
                    Pair pair = (Pair) this.mCallbacks.getBroadcastCookie(i);
                    UserHandle userHandle2 = (UserHandle) pair.second;
                    if (userHandle2 == null || hasUserAccess(userHandle2, userHandle)) {
                        List<ShortcutInfo> filterShortcutsByQuery = filterShortcutsByQuery(str, list, (ShortcutQueryWrapper) pair.first, z);
                        if (!CollectionUtils.isEmpty(filterShortcutsByQuery)) {
                            if (z) {
                                try {
                                    broadcastItem.onShortcutsRemoved(str, filterShortcutsByQuery, userHandle);
                                } catch (RemoteException unused) {
                                }
                            } else {
                                broadcastItem.onShortcutsAddedOrUpdated(str, filterShortcutsByQuery, userHandle);
                            }
                        }
                    }
                }
                this.mCallbacks.finishBroadcast();
            }

            public static List<ShortcutInfo> filterShortcutsByQuery(String str, List<ShortcutInfo> list, ShortcutQueryWrapper shortcutQueryWrapper, boolean z) {
                long changedSince = shortcutQueryWrapper.getChangedSince();
                String str2 = shortcutQueryWrapper.getPackage();
                List shortcutIds = shortcutQueryWrapper.getShortcutIds();
                List locusIds = shortcutQueryWrapper.getLocusIds();
                ComponentName activity = shortcutQueryWrapper.getActivity();
                int queryFlags = shortcutQueryWrapper.getQueryFlags();
                if (str2 == null || str2.equals(str)) {
                    ArrayList arrayList = new ArrayList();
                    int i = ((queryFlags & 2) != 0 ? 2 : 0) | ((queryFlags & 1) != 0 ? 1 : 0) | ((queryFlags & 8) != 0 ? 32 : 0) | ((queryFlags & 16) != 0 ? 1610629120 : 0);
                    for (int i2 = 0; i2 < list.size(); i2++) {
                        ShortcutInfo shortcutInfo = list.get(i2);
                        if ((activity == null || activity.equals(shortcutInfo.getActivity())) && ((changedSince == 0 || changedSince <= shortcutInfo.getLastChangedTimestamp()) && ((shortcutIds == null || shortcutIds.contains(shortcutInfo.getId())) && ((locusIds == null || locusIds.contains(shortcutInfo.getLocusId())) && (z || (shortcutInfo.getFlags() & i) != 0))))) {
                            arrayList.add(shortcutInfo);
                        }
                    }
                    return arrayList;
                }
                return null;
            }

            public final boolean hasUserAccess(UserHandle userHandle, UserHandle userHandle2) {
                int identifier = userHandle.getIdentifier();
                int identifier2 = userHandle2.getIdentifier();
                if (userHandle2 == userHandle) {
                    return true;
                }
                return this.mUserManagerInternal.isProfileAccessible(identifier, identifier2, null, false);
            }
        }

        /* renamed from: com.android.server.pm.LauncherAppsService$LauncherAppsImpl$PackageRemovedListener */
        /* loaded from: classes2.dex */
        public class PackageRemovedListener extends BroadcastReceiver {
            public PackageRemovedListener() {
            }

            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                int intExtra = intent.getIntExtra("android.intent.extra.user_handle", -10000);
                if (intExtra == -10000) {
                    Slog.w("LauncherAppsService", "Intent broadcast does not contain user handle: " + intent);
                } else if ("android.intent.action.PACKAGE_REMOVED_INTERNAL".equals(intent.getAction())) {
                    String packageName = getPackageName(intent);
                    int[] intArrayExtra = intent.getIntArrayExtra("android.intent.extra.VISIBILITY_ALLOW_LIST");
                    if (packageName != null) {
                        if (intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                            return;
                        }
                        UserHandle userHandle = new UserHandle(intExtra);
                        int beginBroadcast = LauncherAppsImpl.this.mListeners.beginBroadcast();
                        for (int i = 0; i < beginBroadcast; i++) {
                            try {
                                IOnAppsChangedListener iOnAppsChangedListener = (IOnAppsChangedListener) LauncherAppsImpl.this.mListeners.getBroadcastItem(i);
                                BroadcastCookie broadcastCookie = (BroadcastCookie) LauncherAppsImpl.this.mListeners.getBroadcastCookie(i);
                                if (LauncherAppsImpl.this.isEnabledProfileOf(broadcastCookie.user, userHandle, "onPackageRemoved") && LauncherAppsImpl.isCallingAppIdAllowed(intArrayExtra, UserHandle.getAppId(broadcastCookie.callingUid))) {
                                    try {
                                        iOnAppsChangedListener.onPackageRemoved(userHandle, packageName);
                                    } catch (RemoteException e) {
                                        Slog.d("LauncherAppsService", "Callback failed ", e);
                                    }
                                }
                            } finally {
                                LauncherAppsImpl.this.mListeners.finishBroadcast();
                            }
                        }
                    }
                }
            }

            public final String getPackageName(Intent intent) {
                Uri data = intent.getData();
                if (data != null) {
                    return data.getSchemeSpecificPart();
                }
                return null;
            }
        }

        /* renamed from: com.android.server.pm.LauncherAppsService$LauncherAppsImpl$MyPackageMonitor */
        /* loaded from: classes2.dex */
        public class MyPackageMonitor extends PackageMonitor implements ShortcutServiceInternal.ShortcutChangeListener {
            public MyPackageMonitor() {
            }

            public void onPackageAdded(String str, int i) {
                UserHandle userHandle = new UserHandle(getChangingUserId());
                int beginBroadcast = LauncherAppsImpl.this.mListeners.beginBroadcast();
                for (int i2 = 0; i2 < beginBroadcast; i2++) {
                    try {
                        IOnAppsChangedListener iOnAppsChangedListener = (IOnAppsChangedListener) LauncherAppsImpl.this.mListeners.getBroadcastItem(i2);
                        BroadcastCookie broadcastCookie = (BroadcastCookie) LauncherAppsImpl.this.mListeners.getBroadcastCookie(i2);
                        if (LauncherAppsImpl.this.isEnabledProfileOf(broadcastCookie.user, userHandle, "onPackageAdded") && LauncherAppsImpl.this.isPackageVisibleToListener(str, broadcastCookie, userHandle)) {
                            try {
                                iOnAppsChangedListener.onPackageAdded(userHandle, str);
                            } catch (RemoteException e) {
                                Slog.d("LauncherAppsService", "Callback failed ", e);
                            }
                        }
                    } catch (Throwable th) {
                        LauncherAppsImpl.this.mListeners.finishBroadcast();
                        throw th;
                    }
                }
                LauncherAppsImpl.this.mListeners.finishBroadcast();
                super.onPackageAdded(str, i);
                LauncherAppsImpl.this.mPackageManagerInternal.registerInstalledLoadingProgressCallback(str, new PackageLoadingProgressCallback(str, userHandle), userHandle.getIdentifier());
            }

            public void onPackageModified(String str) {
                onPackageChanged(str);
                super.onPackageModified(str);
            }

            public final void onPackageChanged(String str) {
                UserHandle userHandle = new UserHandle(getChangingUserId());
                int beginBroadcast = LauncherAppsImpl.this.mListeners.beginBroadcast();
                for (int i = 0; i < beginBroadcast; i++) {
                    try {
                        IOnAppsChangedListener iOnAppsChangedListener = (IOnAppsChangedListener) LauncherAppsImpl.this.mListeners.getBroadcastItem(i);
                        BroadcastCookie broadcastCookie = (BroadcastCookie) LauncherAppsImpl.this.mListeners.getBroadcastCookie(i);
                        if (LauncherAppsImpl.this.isEnabledProfileOf(broadcastCookie.user, userHandle, "onPackageModified") && LauncherAppsImpl.this.isPackageVisibleToListener(str, broadcastCookie, userHandle)) {
                            try {
                                iOnAppsChangedListener.onPackageChanged(userHandle, str);
                            } catch (RemoteException e) {
                                Slog.d("LauncherAppsService", "Callback failed ", e);
                            }
                        }
                    } finally {
                        LauncherAppsImpl.this.mListeners.finishBroadcast();
                    }
                }
            }

            public void onPackagesAvailable(String[] strArr) {
                UserHandle userHandle = new UserHandle(getChangingUserId());
                int beginBroadcast = LauncherAppsImpl.this.mListeners.beginBroadcast();
                for (int i = 0; i < beginBroadcast; i++) {
                    try {
                        IOnAppsChangedListener iOnAppsChangedListener = (IOnAppsChangedListener) LauncherAppsImpl.this.mListeners.getBroadcastItem(i);
                        BroadcastCookie broadcastCookie = (BroadcastCookie) LauncherAppsImpl.this.mListeners.getBroadcastCookie(i);
                        if (LauncherAppsImpl.this.isEnabledProfileOf(broadcastCookie.user, userHandle, "onPackagesAvailable")) {
                            String[] filteredPackageNames = LauncherAppsImpl.this.getFilteredPackageNames(strArr, broadcastCookie, userHandle);
                            if (!ArrayUtils.isEmpty(filteredPackageNames)) {
                                try {
                                    iOnAppsChangedListener.onPackagesAvailable(userHandle, filteredPackageNames, isReplacing());
                                } catch (RemoteException e) {
                                    Slog.d("LauncherAppsService", "Callback failed ", e);
                                }
                            }
                        }
                    } catch (Throwable th) {
                        LauncherAppsImpl.this.mListeners.finishBroadcast();
                        throw th;
                    }
                }
                LauncherAppsImpl.this.mListeners.finishBroadcast();
                super.onPackagesAvailable(strArr);
            }

            public void onPackagesUnavailable(String[] strArr) {
                UserHandle userHandle = new UserHandle(getChangingUserId());
                int beginBroadcast = LauncherAppsImpl.this.mListeners.beginBroadcast();
                for (int i = 0; i < beginBroadcast; i++) {
                    try {
                        IOnAppsChangedListener iOnAppsChangedListener = (IOnAppsChangedListener) LauncherAppsImpl.this.mListeners.getBroadcastItem(i);
                        BroadcastCookie broadcastCookie = (BroadcastCookie) LauncherAppsImpl.this.mListeners.getBroadcastCookie(i);
                        if (LauncherAppsImpl.this.isEnabledProfileOf(broadcastCookie.user, userHandle, "onPackagesUnavailable")) {
                            String[] filteredPackageNames = LauncherAppsImpl.this.getFilteredPackageNames(strArr, broadcastCookie, userHandle);
                            if (!ArrayUtils.isEmpty(filteredPackageNames)) {
                                try {
                                    iOnAppsChangedListener.onPackagesUnavailable(userHandle, filteredPackageNames, isReplacing());
                                } catch (RemoteException e) {
                                    Slog.d("LauncherAppsService", "Callback failed ", e);
                                }
                            }
                        }
                    } catch (Throwable th) {
                        LauncherAppsImpl.this.mListeners.finishBroadcast();
                        throw th;
                    }
                }
                LauncherAppsImpl.this.mListeners.finishBroadcast();
                super.onPackagesUnavailable(strArr);
            }

            public void onPackagesSuspended(String[] strArr) {
                UserHandle userHandle = new UserHandle(getChangingUserId());
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                for (String str : strArr) {
                    Bundle suspendedPackageLauncherExtras = LauncherAppsImpl.this.mPackageManagerInternal.getSuspendedPackageLauncherExtras(str, userHandle.getIdentifier());
                    if (suspendedPackageLauncherExtras != null) {
                        arrayList.add(new Pair(str, suspendedPackageLauncherExtras));
                    } else {
                        arrayList2.add(str);
                    }
                }
                String[] strArr2 = (String[]) arrayList2.toArray(new String[arrayList2.size()]);
                int beginBroadcast = LauncherAppsImpl.this.mListeners.beginBroadcast();
                for (int i = 0; i < beginBroadcast; i++) {
                    try {
                        IOnAppsChangedListener iOnAppsChangedListener = (IOnAppsChangedListener) LauncherAppsImpl.this.mListeners.getBroadcastItem(i);
                        BroadcastCookie broadcastCookie = (BroadcastCookie) LauncherAppsImpl.this.mListeners.getBroadcastCookie(i);
                        if (LauncherAppsImpl.this.isEnabledProfileOf(broadcastCookie.user, userHandle, "onPackagesSuspended")) {
                            String[] filteredPackageNames = LauncherAppsImpl.this.getFilteredPackageNames(strArr2, broadcastCookie, userHandle);
                            try {
                                if (!ArrayUtils.isEmpty(filteredPackageNames)) {
                                    iOnAppsChangedListener.onPackagesSuspended(userHandle, filteredPackageNames, (Bundle) null);
                                }
                                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                                    Pair pair = (Pair) arrayList.get(i2);
                                    if (LauncherAppsImpl.this.isPackageVisibleToListener((String) pair.first, broadcastCookie, userHandle)) {
                                        iOnAppsChangedListener.onPackagesSuspended(userHandle, new String[]{(String) pair.first}, (Bundle) pair.second);
                                    }
                                }
                            } catch (RemoteException e) {
                                Slog.d("LauncherAppsService", "Callback failed ", e);
                            }
                        }
                    } finally {
                        LauncherAppsImpl.this.mListeners.finishBroadcast();
                    }
                }
            }

            public void onPackagesUnsuspended(String[] strArr) {
                UserHandle userHandle = new UserHandle(getChangingUserId());
                int beginBroadcast = LauncherAppsImpl.this.mListeners.beginBroadcast();
                for (int i = 0; i < beginBroadcast; i++) {
                    try {
                        IOnAppsChangedListener iOnAppsChangedListener = (IOnAppsChangedListener) LauncherAppsImpl.this.mListeners.getBroadcastItem(i);
                        BroadcastCookie broadcastCookie = (BroadcastCookie) LauncherAppsImpl.this.mListeners.getBroadcastCookie(i);
                        if (LauncherAppsImpl.this.isEnabledProfileOf(broadcastCookie.user, userHandle, "onPackagesUnsuspended")) {
                            String[] filteredPackageNames = LauncherAppsImpl.this.getFilteredPackageNames(strArr, broadcastCookie, userHandle);
                            if (!ArrayUtils.isEmpty(filteredPackageNames)) {
                                try {
                                    iOnAppsChangedListener.onPackagesUnsuspended(userHandle, filteredPackageNames);
                                } catch (RemoteException e) {
                                    Slog.d("LauncherAppsService", "Callback failed ", e);
                                }
                            }
                        }
                    } catch (Throwable th) {
                        LauncherAppsImpl.this.mListeners.finishBroadcast();
                        throw th;
                    }
                }
                LauncherAppsImpl.this.mListeners.finishBroadcast();
                super.onPackagesUnsuspended(strArr);
            }

            public void onShortcutChanged(final String str, final int i) {
                LauncherAppsImpl.this.postToPackageMonitorHandler(new Runnable() { // from class: com.android.server.pm.LauncherAppsService$LauncherAppsImpl$MyPackageMonitor$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        LauncherAppsService.LauncherAppsImpl.MyPackageMonitor.this.lambda$onShortcutChanged$0(str, i);
                    }
                });
            }

            /* renamed from: onShortcutChangedInner */
            public final void lambda$onShortcutChanged$0(String str, int i) {
                String str2;
                int i2;
                int i3;
                String str3;
                UserHandle userHandle;
                MyPackageMonitor myPackageMonitor = this;
                String str4 = "LauncherAppsService";
                int beginBroadcast = LauncherAppsImpl.this.mListeners.beginBroadcast();
                try {
                    try {
                        UserHandle of = UserHandle.of(i);
                        int i4 = 0;
                        while (i4 < beginBroadcast) {
                            try {
                                try {
                                    IOnAppsChangedListener iOnAppsChangedListener = (IOnAppsChangedListener) LauncherAppsImpl.this.mListeners.getBroadcastItem(i4);
                                    BroadcastCookie broadcastCookie = (BroadcastCookie) LauncherAppsImpl.this.mListeners.getBroadcastCookie(i4);
                                    if (LauncherAppsImpl.this.isEnabledProfileOf(broadcastCookie.user, of, "onShortcutChanged") && LauncherAppsImpl.this.isPackageVisibleToListener(str, broadcastCookie, of)) {
                                        int identifier = broadcastCookie.user.getIdentifier();
                                        if (LauncherAppsImpl.this.mShortcutServiceInternal.hasShortcutHostPermission(identifier, broadcastCookie.packageName, broadcastCookie.callingPid, broadcastCookie.callingUid)) {
                                            ShortcutServiceInternal shortcutServiceInternal = LauncherAppsImpl.this.mShortcutServiceInternal;
                                            i2 = i4;
                                            UserHandle userHandle2 = of;
                                            i3 = beginBroadcast;
                                            str3 = str4;
                                            try {
                                                try {
                                                    ParceledListSlice parceledListSlice = new ParceledListSlice(shortcutServiceInternal.getShortcuts(identifier, broadcastCookie.packageName, 0L, str, (List) null, (List) null, (ComponentName) null, 1055, i, broadcastCookie.callingPid, broadcastCookie.callingUid));
                                                    userHandle = userHandle2;
                                                    try {
                                                        iOnAppsChangedListener.onShortcutChanged(userHandle, str, parceledListSlice);
                                                    } catch (RemoteException e) {
                                                        e = e;
                                                        Slog.d(str3, "Callback failed ", e);
                                                        i4 = i2 + 1;
                                                        str4 = str3;
                                                        of = userHandle;
                                                        beginBroadcast = i3;
                                                        myPackageMonitor = this;
                                                    }
                                                } catch (RemoteException e2) {
                                                    e = e2;
                                                    userHandle = userHandle2;
                                                }
                                                i4 = i2 + 1;
                                                str4 = str3;
                                                of = userHandle;
                                                beginBroadcast = i3;
                                                myPackageMonitor = this;
                                            } catch (RuntimeException e3) {
                                                e = e3;
                                                str2 = str3;
                                                myPackageMonitor = this;
                                                Log.w(str2, e.getMessage(), e);
                                                LauncherAppsImpl.this.mListeners.finishBroadcast();
                                            }
                                        }
                                    }
                                    i2 = i4;
                                    userHandle = of;
                                    i3 = beginBroadcast;
                                    str3 = str4;
                                    i4 = i2 + 1;
                                    str4 = str3;
                                    of = userHandle;
                                    beginBroadcast = i3;
                                    myPackageMonitor = this;
                                } catch (RuntimeException e4) {
                                    e = e4;
                                    myPackageMonitor = this;
                                    str2 = str4;
                                    Log.w(str2, e.getMessage(), e);
                                    LauncherAppsImpl.this.mListeners.finishBroadcast();
                                }
                            } catch (Throwable th) {
                                th = th;
                                myPackageMonitor = this;
                                LauncherAppsImpl.this.mListeners.finishBroadcast();
                                throw th;
                            }
                        }
                    } catch (RuntimeException e5) {
                        e = e5;
                    }
                    LauncherAppsImpl.this.mListeners.finishBroadcast();
                } catch (Throwable th2) {
                    th = th2;
                }
            }

            public void onPackageStateChanged(String str, int i) {
                onPackageChanged(str);
                super.onPackageStateChanged(str, i);
            }
        }

        /* renamed from: com.android.server.pm.LauncherAppsService$LauncherAppsImpl$PackageCallbackList */
        /* loaded from: classes2.dex */
        public class PackageCallbackList<T extends IInterface> extends RemoteCallbackList<T> {
            public PackageCallbackList() {
            }

            @Override // android.os.RemoteCallbackList
            public void onCallbackDied(T t, Object obj) {
                LauncherAppsImpl.this.checkCallbackCount();
            }
        }

        /* renamed from: com.android.server.pm.LauncherAppsService$LauncherAppsImpl$PackageLoadingProgressCallback */
        /* loaded from: classes2.dex */
        public class PackageLoadingProgressCallback extends PackageManagerInternal.InstalledLoadingProgressCallback {
            public String mPackageName;
            public UserHandle mUser;

            public PackageLoadingProgressCallback(String str, UserHandle userHandle) {
                super(LauncherAppsImpl.this.mCallbackHandler);
                this.mPackageName = str;
                this.mUser = userHandle;
            }

            @Override // android.content.p000pm.PackageManagerInternal.InstalledLoadingProgressCallback
            public void onLoadingProgressChanged(float f) {
                int beginBroadcast = LauncherAppsImpl.this.mListeners.beginBroadcast();
                for (int i = 0; i < beginBroadcast; i++) {
                    try {
                        IOnAppsChangedListener iOnAppsChangedListener = (IOnAppsChangedListener) LauncherAppsImpl.this.mListeners.getBroadcastItem(i);
                        BroadcastCookie broadcastCookie = (BroadcastCookie) LauncherAppsImpl.this.mListeners.getBroadcastCookie(i);
                        if (LauncherAppsImpl.this.isEnabledProfileOf(broadcastCookie.user, this.mUser, "onLoadingProgressChanged") && LauncherAppsImpl.this.isPackageVisibleToListener(this.mPackageName, broadcastCookie, this.mUser)) {
                            try {
                                iOnAppsChangedListener.onPackageLoadingProgressChanged(this.mUser, this.mPackageName, f);
                            } catch (RemoteException e) {
                                Slog.d("LauncherAppsService", "Callback failed ", e);
                            }
                        }
                    } finally {
                        LauncherAppsImpl.this.mListeners.finishBroadcast();
                    }
                }
            }
        }

        /* renamed from: com.android.server.pm.LauncherAppsService$LauncherAppsImpl$LocalService */
        /* loaded from: classes2.dex */
        public final class LocalService extends LauncherAppsServiceInternal {
            public LocalService() {
            }

            @Override // com.android.server.p011pm.LauncherAppsService.LauncherAppsServiceInternal
            public boolean startShortcut(int i, int i2, String str, String str2, String str3, String str4, Rect rect, Bundle bundle, int i3) {
                return LauncherAppsImpl.this.startShortcutInner(i, i2, UserHandle.getUserId(i), str, str2, str3, str4, rect, bundle, i3);
            }
        }
    }
}
