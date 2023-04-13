package com.android.server.p011pm.resolution;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Pair;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.p011pm.Computer;
import com.android.server.p011pm.DumpState;
import com.android.server.p011pm.UserManagerService;
import com.android.server.p011pm.parsing.PackageInfoUtils;
import com.android.server.p011pm.parsing.pkg.AndroidPackageInternal;
import com.android.server.p011pm.parsing.pkg.AndroidPackageUtils;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.pkg.PackageUserStateInternal;
import com.android.server.p011pm.pkg.component.ParsedActivity;
import com.android.server.p011pm.pkg.component.ParsedProvider;
import com.android.server.p011pm.pkg.component.ParsedService;
import com.android.server.p011pm.resolution.ComponentResolver;
import com.android.server.utils.WatchableImpl;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/* renamed from: com.android.server.pm.resolution.ComponentResolverBase */
/* loaded from: classes2.dex */
public abstract class ComponentResolverBase extends WatchableImpl implements ComponentResolverApi {
    public ComponentResolver.ActivityIntentResolver mActivities;
    public ComponentResolver.ProviderIntentResolver mProviders;
    public ArrayMap<String, ParsedProvider> mProvidersByAuthority;
    public ComponentResolver.ReceiverIntentResolver mReceivers;
    public ComponentResolver.ServiceIntentResolver mServices;
    public UserManagerService mUserManager;

    public ComponentResolverBase(UserManagerService userManagerService) {
        this.mUserManager = userManagerService;
    }

    @Override // com.android.server.p011pm.resolution.ComponentResolverApi
    public boolean componentExists(ComponentName componentName) {
        return (this.mActivities.mActivities.get(componentName) == null && this.mReceivers.mActivities.get(componentName) == null && this.mServices.mServices.get(componentName) == null && this.mProviders.mProviders.get(componentName) == null) ? false : true;
    }

    @Override // com.android.server.p011pm.resolution.ComponentResolverApi
    public ParsedActivity getActivity(ComponentName componentName) {
        return this.mActivities.mActivities.get(componentName);
    }

    @Override // com.android.server.p011pm.resolution.ComponentResolverApi
    public ParsedProvider getProvider(ComponentName componentName) {
        return this.mProviders.mProviders.get(componentName);
    }

    @Override // com.android.server.p011pm.resolution.ComponentResolverApi
    public ParsedActivity getReceiver(ComponentName componentName) {
        return this.mReceivers.mActivities.get(componentName);
    }

    @Override // com.android.server.p011pm.resolution.ComponentResolverApi
    public ParsedService getService(ComponentName componentName) {
        return this.mServices.mServices.get(componentName);
    }

    public boolean isActivityDefined(ComponentName componentName) {
        return this.mActivities.mActivities.get(componentName) != null;
    }

    @Override // com.android.server.p011pm.resolution.ComponentResolverApi
    public List<ResolveInfo> queryActivities(Computer computer, Intent intent, String str, long j, int i) {
        return this.mActivities.queryIntent(computer, intent, str, j, i);
    }

    @Override // com.android.server.p011pm.resolution.ComponentResolverApi
    public List<ResolveInfo> queryActivities(Computer computer, Intent intent, String str, long j, List<ParsedActivity> list, int i) {
        return this.mActivities.queryIntentForPackage(computer, intent, str, j, list, i);
    }

    @Override // com.android.server.p011pm.resolution.ComponentResolverApi
    public ProviderInfo queryProvider(Computer computer, String str, long j, int i) {
        PackageStateInternal packageStateInternal;
        AndroidPackageInternal pkg;
        PackageUserStateInternal userStateOrDefault;
        ApplicationInfo generateApplicationInfo;
        ParsedProvider parsedProvider = this.mProvidersByAuthority.get(str);
        if (parsedProvider == null || (packageStateInternal = computer.getPackageStateInternal(parsedProvider.getPackageName())) == null || (pkg = packageStateInternal.getPkg()) == null || (generateApplicationInfo = PackageInfoUtils.generateApplicationInfo(pkg, j, (userStateOrDefault = packageStateInternal.getUserStateOrDefault(i)), i, packageStateInternal)) == null) {
            return null;
        }
        return PackageInfoUtils.generateProviderInfo(pkg, parsedProvider, j, userStateOrDefault, generateApplicationInfo, i, packageStateInternal);
    }

    @Override // com.android.server.p011pm.resolution.ComponentResolverApi
    public List<ResolveInfo> queryProviders(Computer computer, Intent intent, String str, long j, int i) {
        return this.mProviders.queryIntent(computer, intent, str, j, i);
    }

    @Override // com.android.server.p011pm.resolution.ComponentResolverApi
    public List<ResolveInfo> queryProviders(Computer computer, Intent intent, String str, long j, List<ParsedProvider> list, int i) {
        return this.mProviders.queryIntentForPackage(computer, intent, str, j, list, i);
    }

    @Override // com.android.server.p011pm.resolution.ComponentResolverApi
    public List<ProviderInfo> queryProviders(Computer computer, String str, String str2, int i, long j, int i2) {
        PackageStateInternal packageStateInternal;
        AndroidPackageInternal pkg;
        ProviderInfo generateProviderInfo;
        PackageInfoUtils.CachedApplicationInfoGenerator cachedApplicationInfoGenerator = null;
        if (this.mUserManager.exists(i2)) {
            ArrayList arrayList = null;
            for (int size = this.mProviders.mProviders.size() - 1; size >= 0; size--) {
                ParsedProvider valueAt = this.mProviders.mProviders.valueAt(size);
                if (valueAt.getAuthority() != null && (packageStateInternal = computer.getPackageStateInternal(valueAt.getPackageName())) != null && (pkg = packageStateInternal.getPkg()) != null) {
                    if (str != null) {
                        if (valueAt.getProcessName().equals(str)) {
                            if (!UserHandle.isSameApp(pkg.getUid(), i)) {
                            }
                        }
                    }
                    if (str2 == null || valueAt.getMetaData().containsKey(str2)) {
                        if (cachedApplicationInfoGenerator == null) {
                            cachedApplicationInfoGenerator = new PackageInfoUtils.CachedApplicationInfoGenerator();
                        }
                        PackageInfoUtils.CachedApplicationInfoGenerator cachedApplicationInfoGenerator2 = cachedApplicationInfoGenerator;
                        PackageUserStateInternal userStateOrDefault = packageStateInternal.getUserStateOrDefault(i2);
                        ApplicationInfo generate = cachedApplicationInfoGenerator2.generate(pkg, j, userStateOrDefault, i2, packageStateInternal);
                        if (generate != null && (generateProviderInfo = PackageInfoUtils.generateProviderInfo(pkg, valueAt, j, userStateOrDefault, generate, i2, packageStateInternal)) != null) {
                            if (arrayList == null) {
                                arrayList = new ArrayList(size + 1);
                            }
                            arrayList.add(generateProviderInfo);
                        }
                        cachedApplicationInfoGenerator = cachedApplicationInfoGenerator2;
                    }
                }
            }
            return arrayList;
        }
        return null;
    }

    @Override // com.android.server.p011pm.resolution.ComponentResolverApi
    public List<ResolveInfo> queryReceivers(Computer computer, Intent intent, String str, long j, int i) {
        return this.mReceivers.queryIntent(computer, intent, str, j, i);
    }

    @Override // com.android.server.p011pm.resolution.ComponentResolverApi
    public List<ResolveInfo> queryReceivers(Computer computer, Intent intent, String str, long j, List<ParsedActivity> list, int i) {
        return this.mReceivers.queryIntentForPackage(computer, intent, str, j, list, i);
    }

    @Override // com.android.server.p011pm.resolution.ComponentResolverApi
    public List<ResolveInfo> queryServices(Computer computer, Intent intent, String str, long j, int i) {
        return this.mServices.queryIntent(computer, intent, str, j, i);
    }

    @Override // com.android.server.p011pm.resolution.ComponentResolverApi
    public List<ResolveInfo> queryServices(Computer computer, Intent intent, String str, long j, List<ParsedService> list, int i) {
        return this.mServices.queryIntentForPackage(computer, intent, str, j, list, i);
    }

    @Override // com.android.server.p011pm.resolution.ComponentResolverApi
    public void querySyncProviders(Computer computer, List<String> list, List<ProviderInfo> list2, boolean z, int i) {
        PackageStateInternal packageStateInternal;
        AndroidPackageInternal pkg;
        ProviderInfo generateProviderInfo;
        PackageInfoUtils.CachedApplicationInfoGenerator cachedApplicationInfoGenerator = null;
        for (int size = this.mProvidersByAuthority.size() - 1; size >= 0; size--) {
            ParsedProvider valueAt = this.mProvidersByAuthority.valueAt(size);
            if (valueAt.isSyncable() && (packageStateInternal = computer.getPackageStateInternal(valueAt.getPackageName())) != null && (pkg = packageStateInternal.getPkg()) != null && (!z || packageStateInternal.isSystem())) {
                if (cachedApplicationInfoGenerator == null) {
                    cachedApplicationInfoGenerator = new PackageInfoUtils.CachedApplicationInfoGenerator();
                }
                PackageUserStateInternal userStateOrDefault = packageStateInternal.getUserStateOrDefault(i);
                ApplicationInfo generate = cachedApplicationInfoGenerator.generate(pkg, 0L, userStateOrDefault, i, packageStateInternal);
                if (generate != null && (generateProviderInfo = PackageInfoUtils.generateProviderInfo(pkg, valueAt, 0L, userStateOrDefault, generate, i, packageStateInternal)) != null) {
                    list.add(this.mProvidersByAuthority.keyAt(size));
                    list2.add(generateProviderInfo);
                }
            }
        }
    }

    @Override // com.android.server.p011pm.resolution.ComponentResolverApi
    public void dumpActivityResolvers(PrintWriter printWriter, DumpState dumpState, String str) {
        if (this.mActivities.dump(printWriter, dumpState.getTitlePrinted() ? "\nActivity Resolver Table:" : "Activity Resolver Table:", "  ", str, dumpState.isOptionEnabled(1), true)) {
            dumpState.setTitlePrinted(true);
        }
    }

    @Override // com.android.server.p011pm.resolution.ComponentResolverApi
    public void dumpProviderResolvers(PrintWriter printWriter, DumpState dumpState, String str) {
        if (this.mProviders.dump(printWriter, dumpState.getTitlePrinted() ? "\nProvider Resolver Table:" : "Provider Resolver Table:", "  ", str, dumpState.isOptionEnabled(1), true)) {
            dumpState.setTitlePrinted(true);
        }
    }

    @Override // com.android.server.p011pm.resolution.ComponentResolverApi
    public void dumpReceiverResolvers(PrintWriter printWriter, DumpState dumpState, String str) {
        if (this.mReceivers.dump(printWriter, dumpState.getTitlePrinted() ? "\nReceiver Resolver Table:" : "Receiver Resolver Table:", "  ", str, dumpState.isOptionEnabled(1), true)) {
            dumpState.setTitlePrinted(true);
        }
    }

    @Override // com.android.server.p011pm.resolution.ComponentResolverApi
    public void dumpServiceResolvers(PrintWriter printWriter, DumpState dumpState, String str) {
        if (this.mServices.dump(printWriter, dumpState.getTitlePrinted() ? "\nService Resolver Table:" : "Service Resolver Table:", "  ", str, dumpState.isOptionEnabled(1), true)) {
            dumpState.setTitlePrinted(true);
        }
    }

    @Override // com.android.server.p011pm.resolution.ComponentResolverApi
    public void dumpContentProviders(Computer computer, PrintWriter printWriter, DumpState dumpState, String str) {
        boolean z = false;
        boolean z2 = false;
        for (ParsedProvider parsedProvider : this.mProviders.mProviders.values()) {
            if (str == null || str.equals(parsedProvider.getPackageName())) {
                if (!z2) {
                    if (dumpState.onTitlePrinted()) {
                        printWriter.println();
                    }
                    printWriter.println("Registered ContentProviders:");
                    z2 = true;
                }
                printWriter.print("  ");
                ComponentName.printShortString(printWriter, parsedProvider.getPackageName(), parsedProvider.getName());
                printWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
                printWriter.print("    ");
                printWriter.println(parsedProvider.toString());
            }
        }
        for (Map.Entry<String, ParsedProvider> entry : this.mProvidersByAuthority.entrySet()) {
            ParsedProvider value = entry.getValue();
            if (str == null || str.equals(value.getPackageName())) {
                if (!z) {
                    if (dumpState.onTitlePrinted()) {
                        printWriter.println();
                    }
                    printWriter.println("ContentProvider Authorities:");
                    z = true;
                }
                printWriter.print("  [");
                printWriter.print(entry.getKey());
                printWriter.println("]:");
                printWriter.print("    ");
                printWriter.println(value.toString());
                AndroidPackage androidPackage = computer.getPackage(value.getPackageName());
                if (androidPackage != null) {
                    printWriter.print("      applicationInfo=");
                    printWriter.println(AndroidPackageUtils.generateAppInfoWithoutState(androidPackage));
                }
            }
        }
    }

    @Override // com.android.server.p011pm.resolution.ComponentResolverApi
    public void dumpServicePermissions(PrintWriter printWriter, DumpState dumpState) {
        if (dumpState.onTitlePrinted()) {
            printWriter.println();
        }
        printWriter.println("Service permissions:");
        Iterator<F> filterIterator = this.mServices.filterIterator();
        while (filterIterator.hasNext()) {
            ParsedService parsedService = (ParsedService) ((Pair) filterIterator.next()).first;
            String permission = parsedService.getPermission();
            if (permission != null) {
                printWriter.print("    ");
                printWriter.print(parsedService.getComponentName().flattenToShortString());
                printWriter.print(": ");
                printWriter.println(permission);
            }
        }
    }
}
