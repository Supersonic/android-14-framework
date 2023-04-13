package com.android.server.p011pm;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.IUnsafeIntentStrictModeCallback;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.AuxiliaryResolveInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Trace;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.app.ResolverActivity;
import com.android.internal.util.ArrayUtils;
import com.android.server.LocalServices;
import com.android.server.compat.PlatformCompat;
import com.android.server.p006am.ActivityManagerUtils;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.resolution.ComponentResolverApi;
import com.android.server.p011pm.verify.domain.DomainVerificationManagerInternal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
/* renamed from: com.android.server.pm.ResolveIntentHelper */
/* loaded from: classes2.dex */
public final class ResolveIntentHelper {
    public final Context mContext;
    public final DomainVerificationManagerInternal mDomainVerificationManager;
    public final Handler mHandler;
    public final Supplier<ActivityInfo> mInstantAppInstallerActivitySupplier;
    public final PlatformCompat mPlatformCompat;
    public final PreferredActivityHelper mPreferredActivityHelper;
    public final Supplier<ResolveInfo> mResolveInfoSupplier;
    public final UserManagerService mUserManager;
    public final UserNeedsBadgingCache mUserNeedsBadging;

    public ResolveIntentHelper(Context context, PreferredActivityHelper preferredActivityHelper, PlatformCompat platformCompat, UserManagerService userManagerService, DomainVerificationManagerInternal domainVerificationManagerInternal, UserNeedsBadgingCache userNeedsBadgingCache, Supplier<ResolveInfo> supplier, Supplier<ActivityInfo> supplier2, Handler handler) {
        this.mContext = context;
        this.mPreferredActivityHelper = preferredActivityHelper;
        this.mPlatformCompat = platformCompat;
        this.mUserManager = userManagerService;
        this.mDomainVerificationManager = domainVerificationManagerInternal;
        this.mUserNeedsBadging = userNeedsBadgingCache;
        this.mResolveInfoSupplier = supplier;
        this.mInstantAppInstallerActivitySupplier = supplier2;
        this.mHandler = handler;
    }

    public static void filterNonExportedComponents(final Intent intent, int i, final int i2, List<ResolveInfo> list, PlatformCompat platformCompat, String str, Computer computer, Handler handler) {
        if (list == null || intent.getPackage() != null || intent.getComponent() != null || ActivityManager.canAccessUnexportedComponents(i)) {
            return;
        }
        AndroidPackage androidPackage = computer.getPackage(i);
        if (androidPackage != null) {
            androidPackage.getPackageName();
        }
        final ActivityManagerInternal activityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        final IUnsafeIntentStrictModeCallback registeredStrictModeCallback = activityManagerInternal.getRegisteredStrictModeCallback(i2);
        for (int size = list.size() - 1; size >= 0; size--) {
            if (!list.get(size).getComponentInfo().exported) {
                boolean isChangeEnabledByUid = platformCompat.isChangeEnabledByUid(229362273L, i);
                ActivityManagerUtils.logUnsafeIntentEvent(2, i, intent, str, isChangeEnabledByUid);
                if (registeredStrictModeCallback != null) {
                    handler.post(new Runnable() { // from class: com.android.server.pm.ResolveIntentHelper$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            ResolveIntentHelper.lambda$filterNonExportedComponents$0(registeredStrictModeCallback, intent, activityManagerInternal, i2);
                        }
                    });
                }
                if (!isChangeEnabledByUid) {
                    return;
                }
                list.remove(size);
            }
        }
    }

    public static /* synthetic */ void lambda$filterNonExportedComponents$0(IUnsafeIntentStrictModeCallback iUnsafeIntentStrictModeCallback, Intent intent, ActivityManagerInternal activityManagerInternal, int i) {
        try {
            iUnsafeIntentStrictModeCallback.onImplicitIntentMatchedInternalComponent(intent.cloneFilter());
        } catch (RemoteException unused) {
            activityManagerInternal.unregisterStrictModeCallback(i);
        }
    }

    public ResolveInfo resolveIntentInternal(Computer computer, Intent intent, String str, long j, long j2, int i, boolean z, int i2) {
        return resolveIntentInternal(computer, intent, str, j, j2, i, z, i2, false, 0);
    }

    public ResolveInfo resolveIntentInternal(Computer computer, Intent intent, String str, long j, long j2, int i, boolean z, int i2, boolean z2, int i3) {
        try {
            Trace.traceBegin(262144L, "resolveIntent");
            if (this.mUserManager.exists(i)) {
                int callingUid = Binder.getCallingUid();
                long updateFlagsForResolve = computer.updateFlagsForResolve(j, i, i2, z, computer.isImplicitImageCaptureIntentAndNotSetByDpc(intent, i, str, j));
                computer.enforceCrossUserPermission(callingUid, i, false, false, "resolve intent");
                Trace.traceBegin(262144L, "queryIntentActivities");
                List<ResolveInfo> queryIntentActivitiesInternal = computer.queryIntentActivitiesInternal(intent, str, updateFlagsForResolve, j2, i2, i, z, true);
                if (z2) {
                    filterNonExportedComponents(intent, i2, i3, queryIntentActivitiesInternal, this.mPlatformCompat, str, computer, this.mHandler);
                }
                Trace.traceEnd(262144L);
                boolean z3 = true;
                ResolveInfo chooseBestActivity = chooseBestActivity(computer, intent, str, updateFlagsForResolve, j2, queryIntentActivitiesInternal, i, UserHandle.getAppId(i2) >= 10000 && !z);
                if ((j2 & 1) == 0) {
                    z3 = false;
                }
                if (z3 && chooseBestActivity != null) {
                    if (chooseBestActivity.handleAllWebDataURI) {
                        return null;
                    }
                }
                return chooseBestActivity;
            }
            return null;
        } finally {
            Trace.traceEnd(262144L);
        }
    }

    public final ResolveInfo chooseBestActivity(Computer computer, Intent intent, String str, long j, long j2, List<ResolveInfo> list, int i, boolean z) {
        boolean z2;
        PackageStateInternal packageStateInternal;
        if (list != null) {
            int size = list.size();
            if (size == 1) {
                return list.get(0);
            }
            if (size > 1) {
                boolean z3 = (intent.getFlags() & 8) != 0;
                ResolveInfo resolveInfo = list.get(0);
                ResolveInfo resolveInfo2 = list.get(1);
                if (z3) {
                    Slog.v("PackageManager", resolveInfo.activityInfo.name + "=" + resolveInfo.priority + " vs " + resolveInfo2.activityInfo.name + "=" + resolveInfo2.priority);
                }
                if (resolveInfo.priority != resolveInfo2.priority || resolveInfo.preferredOrder != resolveInfo2.preferredOrder || resolveInfo.isDefault != resolveInfo2.isDefault) {
                    return list.get(0);
                }
                ResolveInfo findPreferredActivityNotLocked = this.mPreferredActivityHelper.findPreferredActivityNotLocked(computer, intent, str, j, list, true, false, z3, i, z);
                if (findPreferredActivityNotLocked != null) {
                    return findPreferredActivityNotLocked;
                }
                int i2 = 0;
                int i3 = 0;
                while (i3 < size) {
                    ResolveInfo resolveInfo3 = list.get(i3);
                    if (resolveInfo3.handleAllWebDataURI) {
                        i2++;
                    }
                    int i4 = i2;
                    if (resolveInfo3.activityInfo.applicationInfo.isInstantApp() && (packageStateInternal = computer.getPackageStateInternal(resolveInfo3.activityInfo.packageName)) != null && PackageManagerServiceUtils.hasAnyDomainApproval(this.mDomainVerificationManager, packageStateInternal, intent, j, i)) {
                        return resolveInfo3;
                    }
                    i3++;
                    i2 = i4;
                }
                if ((j2 & 2) != 0) {
                    return null;
                }
                ResolveInfo resolveInfo4 = new ResolveInfo(this.mResolveInfoSupplier.get());
                resolveInfo4.handleAllWebDataURI = i2 == size;
                ActivityInfo activityInfo = new ActivityInfo(resolveInfo4.activityInfo);
                resolveInfo4.activityInfo = activityInfo;
                activityInfo.labelRes = ResolverActivity.getLabelRes(intent.getAction());
                if (resolveInfo4.userHandle == null) {
                    resolveInfo4.userHandle = UserHandle.of(i);
                }
                String str2 = intent.getPackage();
                if (TextUtils.isEmpty(str2) || !allHavePackage(list, str2)) {
                    z2 = true;
                } else {
                    ApplicationInfo applicationInfo = list.get(0).activityInfo.applicationInfo;
                    resolveInfo4.resolvePackageName = str2;
                    if (this.mUserNeedsBadging.get(i)) {
                        z2 = true;
                        resolveInfo4.noResourceId = true;
                    } else {
                        z2 = true;
                        resolveInfo4.icon = applicationInfo.icon;
                    }
                    resolveInfo4.iconResourceId = applicationInfo.icon;
                    resolveInfo4.labelRes = applicationInfo.labelRes;
                }
                resolveInfo4.activityInfo.applicationInfo = new ApplicationInfo(resolveInfo4.activityInfo.applicationInfo);
                if (i != 0) {
                    ApplicationInfo applicationInfo2 = resolveInfo4.activityInfo.applicationInfo;
                    applicationInfo2.uid = UserHandle.getUid(i, UserHandle.getAppId(applicationInfo2.uid));
                }
                ActivityInfo activityInfo2 = resolveInfo4.activityInfo;
                if (activityInfo2.metaData == null) {
                    activityInfo2.metaData = new Bundle();
                }
                resolveInfo4.activityInfo.metaData.putBoolean("android.dock_home", z2);
                return resolveInfo4;
            }
            return null;
        }
        return null;
    }

    public final boolean allHavePackage(List<ResolveInfo> list, String str) {
        if (ArrayUtils.isEmpty(list)) {
            return false;
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            ResolveInfo resolveInfo = list.get(i);
            ActivityInfo activityInfo = resolveInfo != null ? resolveInfo.activityInfo : null;
            if (activityInfo == null || !str.equals(activityInfo.packageName)) {
                return false;
            }
        }
        return true;
    }

    public IntentSender getLaunchIntentSenderForPackage(Computer computer, String str, String str2, String str3, int i) throws RemoteException {
        Objects.requireNonNull(str);
        int callingUid = Binder.getCallingUid();
        computer.enforceCrossUserPermission(callingUid, i, false, false, "get launch intent sender for package");
        if (!UserHandle.isSameApp(callingUid, computer.getPackageUid(str2, 0L, i))) {
            throw new SecurityException("getLaunchIntentSenderForPackage() from calling uid: " + callingUid + " does not own package: " + str2);
        }
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.INFO");
        intent.setPackage(str);
        ContentResolver contentResolver = this.mContext.getContentResolver();
        String resolveTypeIfNeeded = intent.resolveTypeIfNeeded(contentResolver);
        List<ResolveInfo> queryIntentActivitiesInternal = computer.queryIntentActivitiesInternal(intent, resolveTypeIfNeeded, 0L, 0L, callingUid, i, true, false);
        if (queryIntentActivitiesInternal == null || queryIntentActivitiesInternal.size() <= 0) {
            intent.removeCategory("android.intent.category.INFO");
            intent.addCategory("android.intent.category.LAUNCHER");
            intent.setPackage(str);
            resolveTypeIfNeeded = intent.resolveTypeIfNeeded(contentResolver);
            queryIntentActivitiesInternal = computer.queryIntentActivitiesInternal(intent, resolveTypeIfNeeded, 0L, 0L, callingUid, i, true, false);
        }
        Intent intent2 = new Intent(intent);
        intent2.setFlags(268435456);
        if (queryIntentActivitiesInternal != null && !queryIntentActivitiesInternal.isEmpty()) {
            intent2.setPackage(null);
            intent2.setClassName(queryIntentActivitiesInternal.get(0).activityInfo.packageName, queryIntentActivitiesInternal.get(0).activityInfo.name);
        }
        return new IntentSender(ActivityManager.getService().getIntentSenderWithFeature(2, str2, str3, (IBinder) null, (String) null, 1, new Intent[]{intent2}, resolveTypeIfNeeded != null ? new String[]{resolveTypeIfNeeded} : null, 67108864, (Bundle) null, i));
    }

    public List<ResolveInfo> queryIntentReceiversInternal(Computer computer, Intent intent, String str, long j, int i, int i2) {
        return queryIntentReceiversInternal(computer, intent, str, j, i, i2, false);
    }

    /* JADX WARN: Code restructure failed: missing block: B:73:0x0139, code lost:
        if (r1 != null) goto L61;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public List<ResolveInfo> queryIntentReceiversInternal(Computer computer, Intent intent, String str, long j, int i, int i2, boolean z) {
        Intent intent2;
        Intent intent3;
        List<ResolveInfo> list;
        List<ResolveInfo> queryReceivers;
        if (this.mUserManager.exists(i)) {
            int i3 = z ? 1000 : i2;
            computer.enforceCrossUserPermission(i3, i, false, false, "query intent receivers");
            String instantAppPackageName = computer.getInstantAppPackageName(i3);
            long updateFlagsForResolve = computer.updateFlagsForResolve(j, i, i3, false, computer.isImplicitImageCaptureIntentAndNotSetByDpc(intent, i, str, j));
            ComponentName component = intent.getComponent();
            if (component != null || intent.getSelector() == null) {
                intent2 = intent;
                intent3 = null;
            } else {
                Intent selector = intent.getSelector();
                intent3 = intent;
                intent2 = selector;
                component = selector.getComponent();
            }
            ComponentResolverApi componentResolver = computer.getComponentResolver();
            List<ResolveInfo> emptyList = Collections.emptyList();
            if (component != null) {
                ActivityInfo receiverInfo = computer.getReceiverInfo(component, updateFlagsForResolve, i);
                if (receiverInfo != null) {
                    boolean z2 = false;
                    boolean z3 = (8388608 & updateFlagsForResolve) != 0;
                    boolean z4 = (updateFlagsForResolve & 16777216) != 0;
                    boolean z5 = (updateFlagsForResolve & 33554432) != 0;
                    boolean z6 = instantAppPackageName != null;
                    boolean equals = component.getPackageName().equals(instantAppPackageName);
                    boolean z7 = (receiverInfo.applicationInfo.privateFlags & 128) != 0;
                    int i4 = receiverInfo.flags;
                    boolean z8 = (i4 & 1048576) != 0;
                    boolean z9 = !z8 || (z5 && !(z8 && (i4 & 2097152) == 0));
                    if (!equals && ((!z3 && !z6 && z7) || (z4 && z6 && z9))) {
                        z2 = true;
                    }
                    if (!z2) {
                        ResolveInfo resolveInfo = new ResolveInfo();
                        resolveInfo.activityInfo = receiverInfo;
                        ArrayList arrayList = new ArrayList(1);
                        arrayList.add(resolveInfo);
                        PackageManagerServiceUtils.applyEnforceIntentFilterMatching(this.mPlatformCompat, componentResolver, arrayList, true, intent2, str, i2);
                        emptyList = arrayList;
                    }
                }
                list = emptyList;
            } else {
                String str2 = intent2.getPackage();
                List<ResolveInfo> list2 = (str2 != null || (queryReceivers = componentResolver.queryReceivers(computer, intent2, str, updateFlagsForResolve, i)) == null) ? emptyList : queryReceivers;
                AndroidPackage androidPackage = computer.getPackage(str2);
                if (androidPackage != null) {
                    list = componentResolver.queryReceivers(computer, intent2, str, updateFlagsForResolve, androidPackage.getReceivers(), i);
                }
                list = list2;
            }
            if (intent3 != null) {
                PackageManagerServiceUtils.applyEnforceIntentFilterMatching(this.mPlatformCompat, componentResolver, list, true, intent3, str, i2);
            }
            return computer.applyPostResolutionFilter(list, instantAppPackageName, false, i3, false, i, intent2);
        }
        return Collections.emptyList();
    }

    public ResolveInfo resolveServiceInternal(Computer computer, Intent intent, String str, long j, int i, int i2) {
        List<ResolveInfo> queryIntentServicesInternal;
        if (this.mUserManager.exists(i) && (queryIntentServicesInternal = computer.queryIntentServicesInternal(intent, str, computer.updateFlagsForResolve(j, i, i2, false, false), i, i2, false)) != null && queryIntentServicesInternal.size() >= 1) {
            return queryIntentServicesInternal.get(0);
        }
        return null;
    }

    public List<ResolveInfo> queryIntentContentProvidersInternal(Computer computer, Intent intent, String str, long j, int i) {
        Intent intent2;
        if (this.mUserManager.exists(i)) {
            int callingUid = Binder.getCallingUid();
            String instantAppPackageName = computer.getInstantAppPackageName(callingUid);
            long updateFlagsForResolve = computer.updateFlagsForResolve(j, i, callingUid, false, false);
            ComponentName component = intent.getComponent();
            if (component != null || intent.getSelector() == null) {
                intent2 = intent;
            } else {
                Intent selector = intent.getSelector();
                intent2 = selector;
                component = selector.getComponent();
            }
            if (component != null) {
                boolean z = true;
                ArrayList arrayList = new ArrayList(1);
                ProviderInfo providerInfo = computer.getProviderInfo(component, updateFlagsForResolve, i);
                if (providerInfo != null) {
                    boolean z2 = (8388608 & updateFlagsForResolve) != 0;
                    boolean z3 = (updateFlagsForResolve & 16777216) != 0;
                    boolean z4 = instantAppPackageName != null;
                    boolean equals = component.getPackageName().equals(instantAppPackageName);
                    ApplicationInfo applicationInfo = providerInfo.applicationInfo;
                    boolean z5 = (applicationInfo.privateFlags & 128) != 0;
                    boolean z6 = !equals && (!(z2 || z4 || !z5) || (z3 && z4 && ((providerInfo.flags & 1048576) == 0)));
                    if (z5 || z4 || !computer.shouldFilterApplication(computer.getPackageStateInternal(applicationInfo.packageName, 1000), callingUid, i)) {
                        z = false;
                    }
                    if (!z6 && !z) {
                        ResolveInfo resolveInfo = new ResolveInfo();
                        resolveInfo.providerInfo = providerInfo;
                        arrayList.add(resolveInfo);
                    }
                }
                return arrayList;
            }
            ComponentResolverApi componentResolver = computer.getComponentResolver();
            String str2 = intent2.getPackage();
            if (str2 == null) {
                List<ResolveInfo> queryProviders = componentResolver.queryProviders(computer, intent2, str, updateFlagsForResolve, i);
                if (queryProviders == null) {
                    return Collections.emptyList();
                }
                return applyPostContentProviderResolutionFilter(computer, queryProviders, instantAppPackageName, i, callingUid);
            }
            AndroidPackage androidPackage = computer.getPackage(str2);
            if (androidPackage != null) {
                List<ResolveInfo> queryProviders2 = componentResolver.queryProviders(computer, intent2, str, updateFlagsForResolve, androidPackage.getProviders(), i);
                if (queryProviders2 == null) {
                    return Collections.emptyList();
                }
                return applyPostContentProviderResolutionFilter(computer, queryProviders2, instantAppPackageName, i, callingUid);
            }
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }

    public final List<ResolveInfo> applyPostContentProviderResolutionFilter(Computer computer, List<ResolveInfo> list, String str, int i, int i2) {
        for (int size = list.size() - 1; size >= 0; size--) {
            ResolveInfo resolveInfo = list.get(size);
            if (str != null || computer.shouldFilterApplication(computer.getPackageStateInternal(resolveInfo.providerInfo.packageName, 0), i2, i)) {
                boolean isInstantApp = resolveInfo.providerInfo.applicationInfo.isInstantApp();
                if (isInstantApp && str.equals(resolveInfo.providerInfo.packageName)) {
                    ProviderInfo providerInfo = resolveInfo.providerInfo;
                    String str2 = providerInfo.splitName;
                    if (str2 != null && !ArrayUtils.contains(providerInfo.applicationInfo.splitNames, str2)) {
                        if (this.mInstantAppInstallerActivitySupplier.get() == null) {
                            if (PackageManagerService.DEBUG_INSTANT) {
                                Slog.v("PackageManager", "No installer - not adding it to the ResolveInfo list");
                            }
                            list.remove(size);
                        } else {
                            if (PackageManagerService.DEBUG_INSTANT) {
                                Slog.v("PackageManager", "Adding ephemeral installer to the ResolveInfo list");
                            }
                            ResolveInfo resolveInfo2 = new ResolveInfo(computer.getInstantAppInstallerInfo());
                            ProviderInfo providerInfo2 = resolveInfo.providerInfo;
                            resolveInfo2.auxiliaryInfo = new AuxiliaryResolveInfo((ComponentName) null, providerInfo2.packageName, providerInfo2.applicationInfo.longVersionCode, providerInfo2.splitName);
                            resolveInfo2.filter = new IntentFilter();
                            resolveInfo2.resolvePackageName = resolveInfo.getComponentInfo().packageName;
                            list.set(size, resolveInfo2);
                        }
                    }
                } else if (isInstantApp || (resolveInfo.providerInfo.flags & 1048576) == 0) {
                    list.remove(size);
                }
            }
        }
        return list;
    }

    /* JADX WARN: Removed duplicated region for block: B:35:0x00e8  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x0133  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public List<ResolveInfo> queryIntentActivityOptionsInternal(Computer computer, ComponentName componentName, Intent[] intentArr, String[] strArr, Intent intent, String str, long j, int i) {
        List<ResolveInfo> list;
        String str2;
        int i2;
        Iterator<String> actionsIterator;
        Object obj;
        int i3;
        int i4;
        List<ResolveInfo> list2;
        String str3;
        ResolveInfo resolveInfo;
        ComponentName componentName2;
        ActivityInfo activityInfo;
        int size;
        int i5;
        List<ResolveInfo> list3;
        int i6;
        String str4;
        Intent[] intentArr2 = intentArr;
        if (this.mUserManager.exists(i)) {
            int callingUid = Binder.getCallingUid();
            long updateFlagsForResolve = computer.updateFlagsForResolve(j, i, callingUid, false, computer.isImplicitImageCaptureIntentAndNotSetByDpc(intent, i, str, j));
            computer.enforceCrossUserPermission(callingUid, i, false, false, "query intent activity options");
            String action = intent.getAction();
            List<ResolveInfo> queryIntentActivitiesInternal = computer.queryIntentActivitiesInternal(intent, str, updateFlagsForResolve | 64, i);
            String str5 = null;
            if (intentArr2 != null) {
                int i7 = 0;
                i2 = 0;
                while (i7 < intentArr2.length) {
                    Intent intent2 = intentArr2[i7];
                    if (intent2 == null) {
                        i3 = i7;
                        i4 = i2;
                        list2 = queryIntentActivitiesInternal;
                        str3 = action;
                    } else {
                        String action2 = intent2.getAction();
                        String str6 = (action == null || !action.equals(action2)) ? action2 : str5;
                        ComponentName component = intent2.getComponent();
                        if (component == null) {
                            obj = str6;
                            i3 = i7;
                            i4 = i2;
                            list2 = queryIntentActivitiesInternal;
                            str3 = action;
                            resolveInfo = resolveIntentInternal(computer, intent2, strArr != null ? strArr[i7] : str5, updateFlagsForResolve, 0L, i, false, Binder.getCallingUid());
                            if (resolveInfo != null) {
                                this.mResolveInfoSupplier.get();
                                activityInfo = resolveInfo.activityInfo;
                                componentName2 = new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name);
                                size = list2.size();
                                i5 = i4;
                                while (i5 < size) {
                                    List<ResolveInfo> list4 = list2;
                                    ResolveInfo resolveInfo2 = list4.get(i5);
                                    if (resolveInfo2.activityInfo.name.equals(componentName2.getClassName()) && resolveInfo2.activityInfo.applicationInfo.packageName.equals(componentName2.getPackageName())) {
                                        str4 = obj;
                                    } else {
                                        str4 = obj;
                                        if (str4 != null) {
                                            if (!resolveInfo2.filter.matchAction(str4)) {
                                            }
                                        }
                                        i5++;
                                        list2 = list4;
                                        obj = str4;
                                    }
                                    list4.remove(i5);
                                    if (resolveInfo == null) {
                                        resolveInfo = resolveInfo2;
                                    }
                                    i5--;
                                    size--;
                                    i5++;
                                    list2 = list4;
                                    obj = str4;
                                }
                                list3 = list2;
                                if (resolveInfo == null) {
                                    resolveInfo = new ResolveInfo();
                                    resolveInfo.activityInfo = activityInfo;
                                }
                                int i8 = i4;
                                list3.add(i8, resolveInfo);
                                i6 = i3;
                                resolveInfo.specificIndex = i6;
                                i2 = i8 + 1;
                            }
                        } else {
                            obj = str6;
                            i3 = i7;
                            i4 = i2;
                            list2 = queryIntentActivitiesInternal;
                            str3 = action;
                            ActivityInfo activityInfo2 = computer.getActivityInfo(component, updateFlagsForResolve, i);
                            if (activityInfo2 != null) {
                                resolveInfo = null;
                                componentName2 = component;
                                activityInfo = activityInfo2;
                                size = list2.size();
                                i5 = i4;
                                while (i5 < size) {
                                }
                                list3 = list2;
                                if (resolveInfo == null) {
                                }
                                int i82 = i4;
                                list3.add(i82, resolveInfo);
                                i6 = i3;
                                resolveInfo.specificIndex = i6;
                                i2 = i82 + 1;
                            }
                        }
                        i7 = i6 + 1;
                        queryIntentActivitiesInternal = list3;
                        action = str3;
                        str5 = null;
                        intentArr2 = intentArr;
                    }
                    list3 = list2;
                    i2 = i4;
                    i6 = i3;
                    i7 = i6 + 1;
                    queryIntentActivitiesInternal = list3;
                    action = str3;
                    str5 = null;
                    intentArr2 = intentArr;
                }
                list = queryIntentActivitiesInternal;
                str2 = action;
            } else {
                list = queryIntentActivitiesInternal;
                str2 = action;
                i2 = 0;
            }
            int size2 = list.size();
            while (i2 < size2 - 1) {
                ResolveInfo resolveInfo3 = list.get(i2);
                IntentFilter intentFilter = resolveInfo3.filter;
                if (intentFilter != null && (actionsIterator = intentFilter.actionsIterator()) != null) {
                    while (actionsIterator.hasNext()) {
                        String next = actionsIterator.next();
                        if (str2 == null || !str2.equals(next)) {
                            int i9 = i2 + 1;
                            while (i9 < size2) {
                                IntentFilter intentFilter2 = list.get(i9).filter;
                                if (intentFilter2 != null && intentFilter2.hasAction(next)) {
                                    list.remove(i9);
                                    i9--;
                                    size2--;
                                }
                                i9++;
                            }
                        }
                    }
                    if ((updateFlagsForResolve & 64) == 0) {
                        resolveInfo3.filter = null;
                        i2++;
                    }
                }
                i2++;
            }
            if (componentName != null) {
                int size3 = list.size();
                int i10 = 0;
                while (true) {
                    if (i10 >= size3) {
                        break;
                    }
                    ActivityInfo activityInfo3 = list.get(i10).activityInfo;
                    if (componentName.getPackageName().equals(activityInfo3.applicationInfo.packageName) && componentName.getClassName().equals(activityInfo3.name)) {
                        list.remove(i10);
                        break;
                    }
                    i10++;
                }
            }
            if ((updateFlagsForResolve & 64) == 0) {
                int size4 = list.size();
                for (int i11 = 0; i11 < size4; i11++) {
                    list.get(i11).filter = null;
                }
            }
            return list;
        }
        return Collections.emptyList();
    }
}
