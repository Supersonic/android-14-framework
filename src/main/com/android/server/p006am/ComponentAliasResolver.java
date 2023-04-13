package com.android.server.p006am;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Build;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.PackageMonitor;
import com.android.internal.os.BackgroundThread;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.compat.CompatChange;
import com.android.server.compat.PlatformCompat;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
/* renamed from: com.android.server.am.ComponentAliasResolver */
/* loaded from: classes.dex */
public class ComponentAliasResolver {
    public final ActivityManagerService mAm;
    public final Context mContext;
    @GuardedBy({"mLock"})
    public boolean mEnabled;
    @GuardedBy({"mLock"})
    public boolean mEnabledByDeviceConfig;
    @GuardedBy({"mLock"})
    public String mOverrideString;
    @GuardedBy({"mLock"})
    public PlatformCompat mPlatformCompat;
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public final ArrayMap<ComponentName, ComponentName> mFromTo = new ArrayMap<>();
    public final PackageMonitor mPackageMonitor = new PackageMonitor() { // from class: com.android.server.am.ComponentAliasResolver.1
        public void onPackageModified(String str) {
            ComponentAliasResolver.this.refresh();
        }

        public void onPackageAdded(String str, int i) {
            ComponentAliasResolver.this.refresh();
        }

        public void onPackageRemoved(String str, int i) {
            ComponentAliasResolver.this.refresh();
        }
    };
    public final CompatChange.ChangeListener mCompatChangeListener = new CompatChange.ChangeListener() { // from class: com.android.server.am.ComponentAliasResolver$$ExternalSyntheticLambda2
        @Override // com.android.server.compat.CompatChange.ChangeListener
        public final void onCompatChange(String str) {
            ComponentAliasResolver.this.lambda$new$0(str);
        }
    };

    public ComponentAliasResolver(ActivityManagerService activityManagerService) {
        this.mAm = activityManagerService;
        this.mContext = activityManagerService.mContext;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(String str) {
        Slog.d("ComponentAliasResolver", "USE_EXPERIMENTAL_COMPONENT_ALIAS changed.");
        BackgroundThread.getHandler().post(new Runnable() { // from class: com.android.server.am.ComponentAliasResolver$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                ComponentAliasResolver.this.refresh();
            }
        });
    }

    public void onSystemReady(boolean z, String str) {
        synchronized (this.mLock) {
            PlatformCompat platformCompat = (PlatformCompat) ServiceManager.getService("platform_compat");
            this.mPlatformCompat = platformCompat;
            platformCompat.registerListener(196254758L, this.mCompatChangeListener);
        }
        Slog.d("ComponentAliasResolver", "Compat listener set.");
        update(z, str);
    }

    public void update(boolean z, String str) {
        synchronized (this.mLock) {
            if (this.mPlatformCompat == null) {
                return;
            }
            final boolean z2 = false;
            if (Build.isDebuggable() && (z || this.mPlatformCompat.isChangeEnabledByPackageName(196254758L, PackageManagerShellCommandDataLoader.PACKAGE, 0))) {
                z2 = true;
            }
            if (z2 != this.mEnabled) {
                StringBuilder sb = new StringBuilder();
                sb.append(z2 ? "Enabling" : "Disabling");
                sb.append(" component aliases...");
                Slog.i("ComponentAliasResolver", sb.toString());
                FgThread.getHandler().post(new Runnable() { // from class: com.android.server.am.ComponentAliasResolver$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ComponentAliasResolver.this.lambda$update$1(z2);
                    }
                });
            }
            this.mEnabled = z2;
            this.mEnabledByDeviceConfig = z;
            this.mOverrideString = str;
            if (z2) {
                refreshLocked();
            } else {
                this.mFromTo.clear();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$update$1(boolean z) {
        if (z) {
            this.mPackageMonitor.register(this.mAm.mContext, UserHandle.ALL, false, BackgroundThread.getHandler());
        } else {
            this.mPackageMonitor.unregister();
        }
    }

    public final void refresh() {
        synchronized (this.mLock) {
            update(this.mEnabledByDeviceConfig, this.mOverrideString);
        }
    }

    @GuardedBy({"mLock"})
    public final void refreshLocked() {
        Slog.d("ComponentAliasResolver", "Refreshing aliases...");
        this.mFromTo.clear();
        loadFromMetadataLocked();
        loadOverridesLocked();
    }

    @GuardedBy({"mLock"})
    public final void loadFromMetadataLocked() {
        Slog.d("ComponentAliasResolver", "Scanning service aliases...");
        loadFromMetadataLockedInner(new Intent("android.intent.action.EXPERIMENTAL_IS_ALIAS"));
        loadFromMetadataLockedInner(new Intent("com.android.intent.action.EXPERIMENTAL_IS_ALIAS"));
    }

    public final void loadFromMetadataLockedInner(Intent intent) {
        extractAliasesLocked(this.mContext.getPackageManager().queryIntentServicesAsUser(intent, 4989056, 0));
        Slog.d("ComponentAliasResolver", "Scanning receiver aliases...");
        extractAliasesLocked(this.mContext.getPackageManager().queryBroadcastReceiversAsUser(intent, 4989056, 0));
    }

    @GuardedBy({"mLock"})
    public final boolean isEnabledForPackageLocked(String str) {
        boolean z;
        try {
            z = this.mContext.getPackageManager().getProperty("com.android.EXPERIMENTAL_COMPONENT_ALIAS_OPT_IN", str).getBoolean();
        } catch (PackageManager.NameNotFoundException unused) {
            z = false;
        }
        if (!z) {
            Slog.w("ComponentAliasResolver", "USE_EXPERIMENTAL_COMPONENT_ALIAS not enabled for " + str);
        }
        return z;
    }

    public static boolean validateAlias(ComponentName componentName, ComponentName componentName2) {
        String packageName = componentName.getPackageName();
        String packageName2 = componentName2.getPackageName();
        if (Objects.equals(packageName, packageName2)) {
            return true;
        }
        if (packageName2.startsWith(packageName + ".")) {
            return true;
        }
        Slog.w("ComponentAliasResolver", "Invalid alias: " + componentName.flattenToShortString() + " -> " + componentName2.flattenToShortString());
        return false;
    }

    @GuardedBy({"mLock"})
    public final void validateAndAddAliasLocked(ComponentName componentName, ComponentName componentName2) {
        Slog.d("ComponentAliasResolver", "" + componentName.flattenToShortString() + " -> " + componentName2.flattenToShortString());
        if (validateAlias(componentName, componentName2) && isEnabledForPackageLocked(componentName.getPackageName()) && isEnabledForPackageLocked(componentName2.getPackageName())) {
            this.mFromTo.put(componentName, componentName2);
        }
    }

    @GuardedBy({"mLock"})
    public final void extractAliasesLocked(List<ResolveInfo> list) {
        for (ResolveInfo resolveInfo : list) {
            ComponentInfo componentInfo = resolveInfo.getComponentInfo();
            ComponentName componentName = componentInfo.getComponentName();
            ComponentName unflatten = unflatten(componentInfo.metaData.getString("alias_target"));
            if (unflatten != null) {
                validateAndAddAliasLocked(componentName, unflatten);
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void loadOverridesLocked() {
        ComponentName unflatten;
        Slog.d("ComponentAliasResolver", "Loading aliases overrides ...");
        for (String str : this.mOverrideString.split("\\,+")) {
            String[] split = str.split("\\:+", 2);
            if (!TextUtils.isEmpty(split[0]) && (unflatten = unflatten(split[0])) != null) {
                if (split.length == 1) {
                    Slog.d("ComponentAliasResolver", "" + unflatten.flattenToShortString() + " [removed]");
                    this.mFromTo.remove(unflatten);
                } else {
                    ComponentName unflatten2 = unflatten(split[1]);
                    if (unflatten2 != null) {
                        validateAndAddAliasLocked(unflatten, unflatten2);
                    }
                }
            }
        }
    }

    public static ComponentName unflatten(String str) {
        ComponentName unflattenFromString = ComponentName.unflattenFromString(str);
        if (unflattenFromString != null) {
            return unflattenFromString;
        }
        Slog.e("ComponentAliasResolver", "Invalid component name detected: " + str);
        return null;
    }

    public void dump(PrintWriter printWriter) {
        synchronized (this.mLock) {
            printWriter.println("ACTIVITY MANAGER COMPONENT-ALIAS (dumpsys activity component-alias)");
            printWriter.print("  Enabled: ");
            printWriter.println(this.mEnabled);
            printWriter.println("  Aliases:");
            for (int i = 0; i < this.mFromTo.size(); i++) {
                printWriter.print("    ");
                printWriter.print(this.mFromTo.keyAt(i).flattenToShortString());
                printWriter.print(" -> ");
                printWriter.print(this.mFromTo.valueAt(i).flattenToShortString());
                printWriter.println();
            }
            printWriter.println();
        }
    }

    /* renamed from: com.android.server.am.ComponentAliasResolver$Resolution */
    /* loaded from: classes.dex */
    public static class Resolution<T> {
        public final T resolved;
        public final T source;

        public Resolution(T t, T t2) {
            this.source = t;
            this.resolved = t2;
        }

        public boolean isAlias() {
            return this.resolved != null;
        }

        public T getAlias() {
            if (isAlias()) {
                return this.source;
            }
            return null;
        }

        public T getTarget() {
            if (isAlias()) {
                return this.resolved;
            }
            return null;
        }
    }

    public Resolution<ComponentName> resolveComponentAlias(Supplier<ComponentName> supplier) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mLock) {
                if (!this.mEnabled) {
                    return new Resolution<>(null, null);
                }
                ComponentName componentName = supplier.get();
                ComponentName componentName2 = this.mFromTo.get(componentName);
                if (componentName2 != null) {
                    RuntimeException runtimeException = Log.isLoggable("ComponentAliasResolver", 2) ? new RuntimeException("STACKTRACE") : null;
                    Slog.d("ComponentAliasResolver", "Alias resolved: " + componentName.flattenToShortString() + " -> " + componentName2.flattenToShortString(), runtimeException);
                }
                return new Resolution<>(componentName, componentName2);
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public Resolution<ComponentName> resolveService(final Intent intent, final String str, final int i, final int i2, final int i3) {
        Resolution<ComponentName> resolveComponentAlias = resolveComponentAlias(new Supplier() { // from class: com.android.server.am.ComponentAliasResolver$$ExternalSyntheticLambda3
            @Override // java.util.function.Supplier
            public final Object get() {
                ComponentName lambda$resolveService$2;
                lambda$resolveService$2 = ComponentAliasResolver.lambda$resolveService$2(intent, str, i, i2, i3);
                return lambda$resolveService$2;
            }
        });
        if (resolveComponentAlias != null && resolveComponentAlias.isAlias()) {
            intent.setOriginalIntent(new Intent(intent));
            intent.setPackage(null);
            intent.setComponent(resolveComponentAlias.getTarget());
        }
        return resolveComponentAlias;
    }

    public static /* synthetic */ ComponentName lambda$resolveService$2(Intent intent, String str, int i, int i2, int i3) {
        ResolveInfo resolveService = ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).resolveService(intent, str, i, i2, i3);
        ServiceInfo serviceInfo = resolveService != null ? resolveService.serviceInfo : null;
        if (serviceInfo == null) {
            return null;
        }
        return new ComponentName(serviceInfo.applicationInfo.packageName, serviceInfo.name);
    }

    public Resolution<ResolveInfo> resolveReceiver(Intent intent, final ResolveInfo resolveInfo, String str, int i, int i2, int i3, boolean z) {
        Resolution<ComponentName> resolveComponentAlias = resolveComponentAlias(new Supplier() { // from class: com.android.server.am.ComponentAliasResolver$$ExternalSyntheticLambda1
            @Override // java.util.function.Supplier
            public final Object get() {
                ComponentName lambda$resolveReceiver$3;
                lambda$resolveReceiver$3 = ComponentAliasResolver.lambda$resolveReceiver$3(resolveInfo);
                return lambda$resolveReceiver$3;
            }
        });
        ComponentName target = resolveComponentAlias.getTarget();
        if (target == null) {
            return new Resolution<>(resolveInfo, null);
        }
        Intent intent2 = new Intent(intent);
        intent2.setPackage(null);
        intent2.setComponent(resolveComponentAlias.getTarget());
        List<ResolveInfo> queryIntentReceivers = ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).queryIntentReceivers(intent2, str, i, i3, i2, z);
        if (queryIntentReceivers == null || queryIntentReceivers.size() == 0) {
            Slog.w("ComponentAliasResolver", "Alias target " + target.flattenToShortString() + " not found");
            return null;
        }
        return new Resolution<>(resolveInfo, queryIntentReceivers.get(0));
    }

    public static /* synthetic */ ComponentName lambda$resolveReceiver$3(ResolveInfo resolveInfo) {
        return resolveInfo.activityInfo.getComponentName();
    }
}
