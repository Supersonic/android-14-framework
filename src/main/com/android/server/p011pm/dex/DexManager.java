package com.android.server.p011pm.dex;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackagePartitions;
import android.os.BatteryManager;
import android.os.FileUtils;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.util.Slog;
import android.util.jar.StrictJarFile;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p011pm.Installer;
import com.android.server.p011pm.InstructionSets;
import com.android.server.p011pm.PackageDexOptimizer;
import com.android.server.p011pm.PackageManagerServiceUtils;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p011pm.dex.PackageDexUsage;
import dalvik.system.VMRuntime;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.zip.ZipEntry;
/* renamed from: com.android.server.pm.dex.DexManager */
/* loaded from: classes2.dex */
public class DexManager {
    public BatteryManager mBatteryManager;
    public final Context mContext;
    public final int mCriticalBatteryLevel;
    public final DynamicCodeLogger mDynamicCodeLogger;
    public final Object mInstallLock;
    @GuardedBy({"mInstallLock"})
    public final Installer mInstaller;
    @GuardedBy({"mPackageCodeLocationsCache"})
    public final Map<String, PackageCodeLocations> mPackageCodeLocationsCache;
    public final PackageDexOptimizer mPackageDexOptimizer;
    public final PackageDexUsage mPackageDexUsage;
    public IPackageManager mPackageManager;
    public PowerManager mPowerManager;
    public static final boolean DEBUG = Log.isLoggable("DexManager", 3);
    public static int DEX_SEARCH_NOT_FOUND = 0;
    public static int DEX_SEARCH_FOUND_PRIMARY = 1;
    public static int DEX_SEARCH_FOUND_SPLIT = 2;
    public static int DEX_SEARCH_FOUND_SECONDARY = 3;

    public DexManager(Context context, PackageDexOptimizer packageDexOptimizer, Installer installer, Object obj, DynamicCodeLogger dynamicCodeLogger) {
        this(context, packageDexOptimizer, installer, obj, dynamicCodeLogger, null);
    }

    @VisibleForTesting
    public DexManager(Context context, PackageDexOptimizer packageDexOptimizer, Installer installer, Object obj, DynamicCodeLogger dynamicCodeLogger, IPackageManager iPackageManager) {
        this.mBatteryManager = null;
        this.mPowerManager = null;
        this.mContext = context;
        this.mPackageCodeLocationsCache = new HashMap();
        this.mPackageDexUsage = new PackageDexUsage();
        this.mPackageDexOptimizer = packageDexOptimizer;
        this.mInstaller = installer;
        this.mInstallLock = obj;
        this.mDynamicCodeLogger = dynamicCodeLogger;
        this.mPackageManager = iPackageManager;
        if (context != null) {
            PowerManager powerManager = (PowerManager) context.getSystemService(PowerManager.class);
            this.mPowerManager = powerManager;
            if (powerManager == null) {
                Slog.wtf("DexManager", "Power Manager is unavailable at time of Dex Manager start");
            }
            this.mCriticalBatteryLevel = context.getResources().getInteger(17694778);
            return;
        }
        this.mCriticalBatteryLevel = 0;
    }

    public final IPackageManager getPackageManager() {
        if (this.mPackageManager == null) {
            this.mPackageManager = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
        }
        return this.mPackageManager;
    }

    public void notifyDexLoad(ApplicationInfo applicationInfo, Map<String, String> map, String str, int i, boolean z) {
        try {
            notifyDexLoadInternal(applicationInfo, map, str, i, z);
        } catch (RuntimeException e) {
            Slog.w("DexManager", "Exception while notifying dex load for package " + applicationInfo.packageName, e);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:37:0x00db, code lost:
        r16.mDynamicCodeLogger.recordDex(r20, r4, r3.mOwningPackageName, r17.packageName);
     */
    @VisibleForTesting
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void notifyDexLoadInternal(ApplicationInfo applicationInfo, Map<String, String> map, String str, int i, boolean z) {
        if (map == null) {
            return;
        }
        if (map.isEmpty()) {
            Slog.wtf("DexManager", "Bad call to notifyDexLoad: class loaders list is empty");
        } else if (PackageManagerServiceUtils.checkISA(str)) {
            String str2 = applicationInfo.packageName;
            if (z) {
                str2 = str2 + "..isolated";
            }
            String str3 = str2;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                DexSearchResult dexPackage = getDexPackage(applicationInfo, key, i);
                boolean z2 = DEBUG;
                if (z2) {
                    Slog.i("DexManager", str3 + " loads from " + dexPackage + " : " + i + " : " + key);
                }
                if (dexPackage.mOutcome != DEX_SEARCH_NOT_FOUND) {
                    boolean z3 = true;
                    boolean z4 = !str3.equals(dexPackage.mOwningPackageName);
                    if (dexPackage.mOutcome != DEX_SEARCH_FOUND_PRIMARY && dexPackage.mOutcome != DEX_SEARCH_FOUND_SPLIT) {
                        z3 = false;
                    }
                    boolean z5 = z3;
                    if (!z5 || z4 || isPlatformPackage(dexPackage.mOwningPackageName)) {
                        String value = entry.getValue();
                        boolean isPlatformPackage = isPlatformPackage(dexPackage.mOwningPackageName);
                        if (value != null && VMRuntime.isValidClassLoaderContext(value) && this.mPackageDexUsage.record(dexPackage.mOwningPackageName, key, i, str, z5, str3, value, isPlatformPackage)) {
                            this.mPackageDexUsage.maybeWriteAsync();
                        }
                    }
                } else if (z2) {
                    Slog.i("DexManager", "Could not find owning package for dex file: " + key);
                }
            }
        } else {
            Slog.w("DexManager", "Loading dex files " + map.keySet() + " in unsupported ISA: " + str + "?");
        }
    }

    public final boolean isSystemServerDexPathSupportedForOdex(String str) {
        ArrayList orderedPartitions = PackagePartitions.getOrderedPartitions(Function.identity());
        if (str.startsWith("/apex/")) {
            return true;
        }
        for (int i = 0; i < orderedPartitions.size(); i++) {
            if (((PackagePartitions.SystemPartition) orderedPartitions.get(i)).containsPath(str)) {
                return true;
            }
        }
        return false;
    }

    public void load(Map<Integer, List<PackageInfo>> map) {
        try {
            loadInternal(map);
        } catch (RuntimeException e) {
            this.mPackageDexUsage.clear();
            Slog.w("DexManager", "Exception while loading. Starting with a fresh state.", e);
        }
    }

    public void notifyPackageInstalled(PackageInfo packageInfo, int i) {
        if (i == -1) {
            throw new IllegalArgumentException("notifyPackageInstalled called with USER_ALL");
        }
        cachePackageInfo(packageInfo, i);
    }

    public void notifyPackageUpdated(String str, String str2, String[] strArr) {
        cachePackageCodeLocation(str, str2, strArr, null, -1);
        if (this.mPackageDexUsage.clearUsedByOtherApps(str)) {
            this.mPackageDexUsage.maybeWriteAsync();
        }
    }

    public void notifyPackageDataDestroyed(String str, int i) {
        if (i == -1) {
            if (this.mPackageDexUsage.removePackage(str)) {
                this.mPackageDexUsage.maybeWriteAsync();
            }
        } else if (this.mPackageDexUsage.removeUserPackage(str, i)) {
            this.mPackageDexUsage.maybeWriteAsync();
        }
    }

    public final void cachePackageInfo(PackageInfo packageInfo, int i) {
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        cachePackageCodeLocation(packageInfo.packageName, applicationInfo.sourceDir, applicationInfo.splitSourceDirs, new String[]{applicationInfo.dataDir, applicationInfo.deviceProtectedDataDir, applicationInfo.credentialProtectedDataDir}, i);
    }

    public final void cachePackageCodeLocation(String str, String str2, String[] strArr, String[] strArr2, int i) {
        synchronized (this.mPackageCodeLocationsCache) {
            PackageCodeLocations packageCodeLocations = (PackageCodeLocations) putIfAbsent(this.mPackageCodeLocationsCache, str, new PackageCodeLocations(str, str2, strArr));
            packageCodeLocations.updateCodeLocation(str2, strArr);
            if (strArr2 != null) {
                for (String str3 : strArr2) {
                    if (str3 != null) {
                        packageCodeLocations.mergeAppDataDirs(str3, i);
                    }
                }
            }
        }
    }

    public final void loadInternal(Map<Integer, List<PackageInfo>> map) {
        HashMap hashMap = new HashMap();
        HashMap hashMap2 = new HashMap();
        for (Map.Entry<Integer, List<PackageInfo>> entry : map.entrySet()) {
            int intValue = entry.getKey().intValue();
            for (PackageInfo packageInfo : entry.getValue()) {
                cachePackageInfo(packageInfo, intValue);
                ((Set) putIfAbsent(hashMap, packageInfo.packageName, new HashSet())).add(Integer.valueOf(intValue));
                Set set = (Set) putIfAbsent(hashMap2, packageInfo.packageName, new HashSet());
                set.add(packageInfo.applicationInfo.sourceDir);
                String[] strArr = packageInfo.applicationInfo.splitSourceDirs;
                if (strArr != null) {
                    Collections.addAll(set, strArr);
                }
            }
        }
        try {
            this.mPackageDexUsage.read();
            this.mPackageDexUsage.syncData(hashMap, hashMap2, new ArrayList());
        } catch (RuntimeException e) {
            this.mPackageDexUsage.clear();
            Slog.w("DexManager", "Exception while loading package dex usage. Starting with a fresh state.", e);
        }
    }

    public PackageDexUsage.PackageUseInfo getPackageUseInfoOrDefault(String str) {
        PackageDexUsage.PackageUseInfo packageUseInfo = this.mPackageDexUsage.getPackageUseInfo(str);
        return packageUseInfo == null ? new PackageDexUsage.PackageUseInfo(str) : packageUseInfo;
    }

    @VisibleForTesting
    public boolean hasInfoOnPackage(String str) {
        return this.mPackageDexUsage.getPackageUseInfo(str) != null;
    }

    public boolean dexoptSecondaryDex(DexoptOptions dexoptOptions) throws Installer.LegacyDexoptDisabledException {
        if (isPlatformPackage(dexoptOptions.getPackageName())) {
            Slog.wtf("DexManager", "System server jars should be optimized with dexoptSystemServer");
            return false;
        }
        PackageDexOptimizer packageDexOptimizer = getPackageDexOptimizer(dexoptOptions);
        String packageName = dexoptOptions.getPackageName();
        PackageDexUsage.PackageUseInfo packageUseInfoOrDefault = getPackageUseInfoOrDefault(packageName);
        if (packageUseInfoOrDefault.getDexUseInfoMap().isEmpty()) {
            if (DEBUG) {
                Slog.d("DexManager", "No secondary dex use for package:" + packageName);
            }
            return true;
        }
        while (true) {
            boolean z = true;
            for (Map.Entry<String, PackageDexUsage.DexUseInfo> entry : packageUseInfoOrDefault.getDexUseInfoMap().entrySet()) {
                String key = entry.getKey();
                PackageDexUsage.DexUseInfo value = entry.getValue();
                try {
                    PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, 0L, value.getOwnerUserId());
                    if (packageInfo == null) {
                        Slog.d("DexManager", "Could not find package when compiling secondary dex " + packageName + " for user " + value.getOwnerUserId());
                        this.mPackageDexUsage.removeUserPackage(packageName, value.getOwnerUserId());
                    } else {
                        int dexOptSecondaryDexPath = packageDexOptimizer.dexOptSecondaryDexPath(packageInfo.applicationInfo, key, value, dexoptOptions);
                        if (!z || dexOptSecondaryDexPath == -1) {
                            z = false;
                        }
                    }
                } catch (RemoteException e) {
                    throw new AssertionError(e);
                }
            }
            return z;
        }
    }

    public final PackageDexOptimizer getPackageDexOptimizer(DexoptOptions dexoptOptions) {
        if (dexoptOptions.isForce()) {
            return new PackageDexOptimizer.ForcedUpdatePackageDexOptimizer(this.mPackageDexOptimizer);
        }
        return this.mPackageDexOptimizer;
    }

    public void reconcileSecondaryDexFiles(String str) throws Installer.LegacyDexoptDisabledException {
        boolean z;
        PackageInfo packageInfo;
        int i;
        boolean z2;
        PackageDexUsage.PackageUseInfo packageUseInfoOrDefault = getPackageUseInfoOrDefault(str);
        if (packageUseInfoOrDefault.getDexUseInfoMap().isEmpty()) {
            if (DEBUG) {
                Slog.d("DexManager", "No secondary dex use for package:" + str);
                return;
            }
            return;
        }
        loop0: while (true) {
            z = false;
            for (Map.Entry<String, PackageDexUsage.DexUseInfo> entry : packageUseInfoOrDefault.getDexUseInfoMap().entrySet()) {
                String key = entry.getKey();
                PackageDexUsage.DexUseInfo value = entry.getValue();
                try {
                    packageInfo = getPackageManager().getPackageInfo(str, 0L, value.getOwnerUserId());
                } catch (RemoteException unused) {
                    packageInfo = null;
                }
                boolean z3 = true;
                if (packageInfo == null) {
                    Slog.d("DexManager", "Could not find package when compiling secondary dex " + str + " for user " + value.getOwnerUserId());
                    if (!this.mPackageDexUsage.removeUserPackage(str, value.getOwnerUserId()) && !z) {
                        break;
                    }
                    z = z3;
                } else if (isPlatformPackage(str)) {
                    if (Files.exists(Paths.get(key, new String[0]), new LinkOption[0])) {
                        continue;
                    } else {
                        if (DEBUG) {
                            Slog.w("DexManager", "A dex file previously loaded by System Server does not exist  anymore: " + key);
                        }
                        if (!this.mPackageDexUsage.removeUserPackage(str, value.getOwnerUserId()) && !z) {
                            break;
                        }
                        z = z3;
                    }
                } else {
                    ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                    String str2 = applicationInfo.deviceProtectedDataDir;
                    if (str2 == null || !FileUtils.contains(str2, key)) {
                        String str3 = applicationInfo.credentialProtectedDataDir;
                        if (str3 == null || !FileUtils.contains(str3, key)) {
                            Slog.e("DexManager", "Could not infer CE/DE storage for path " + key);
                            if (!this.mPackageDexUsage.removeDexFile(str, key, value.getOwnerUserId()) && !z) {
                                break;
                            }
                            z = z3;
                        } else {
                            i = 2;
                        }
                    } else {
                        i = 1;
                    }
                    synchronized (this.mInstallLock) {
                        try {
                            z2 = this.mInstaller.reconcileSecondaryDexFile(key, str, applicationInfo.uid, (String[]) value.getLoaderIsas().toArray(new String[0]), applicationInfo.volumeUuid, i);
                        } catch (Installer.InstallerException e) {
                            Slog.e("DexManager", "Got InstallerException when reconciling dex " + key + " : " + e.getMessage());
                            z2 = true;
                        }
                    }
                    if (!z2) {
                        if (!this.mPackageDexUsage.removeDexFile(str, key, value.getOwnerUserId()) && !z) {
                            z3 = false;
                        }
                        z = z3;
                    }
                }
            }
        }
        if (z) {
            this.mPackageDexUsage.maybeWriteAsync();
        }
    }

    public RegisterDexModuleResult registerDexModule(ApplicationInfo applicationInfo, String str, boolean z, int i) throws Installer.LegacyDexoptDisabledException {
        DexSearchResult dexPackage = getDexPackage(applicationInfo, str, i);
        if (dexPackage.mOutcome == DEX_SEARCH_NOT_FOUND) {
            return new RegisterDexModuleResult(false, "Package not found");
        }
        if (!applicationInfo.packageName.equals(dexPackage.mOwningPackageName)) {
            return new RegisterDexModuleResult(false, "Dex path does not belong to package");
        }
        if (dexPackage.mOutcome == DEX_SEARCH_FOUND_PRIMARY || dexPackage.mOutcome == DEX_SEARCH_FOUND_SPLIT) {
            return new RegisterDexModuleResult(false, "Main apks cannot be registered");
        }
        String str2 = z ? ".shared.module" : dexPackage.mOwningPackageName;
        String[] appDexInstructionSets = InstructionSets.getAppDexInstructionSets(applicationInfo.primaryCpuAbi, applicationInfo.secondaryCpuAbi);
        int i2 = 0;
        boolean z2 = false;
        for (int length = appDexInstructionSets.length; i2 < length; length = length) {
            z2 |= this.mPackageDexUsage.record(dexPackage.mOwningPackageName, str, i, appDexInstructionSets[i2], false, str2, "=VariableClassLoaderContext=", false);
            i2++;
            appDexInstructionSets = appDexInstructionSets;
        }
        if (z2) {
            this.mPackageDexUsage.maybeWriteAsync();
        }
        if (this.mPackageDexOptimizer.dexOptSecondaryDexPath(applicationInfo, str, this.mPackageDexUsage.getPackageUseInfo(dexPackage.mOwningPackageName).getDexUseInfoMap().get(str), new DexoptOptions(applicationInfo.packageName, 3, 0)) != -1) {
            Slog.e("DexManager", "Failed to optimize dex module " + str);
        }
        return new RegisterDexModuleResult(true, "Dex module registered successfully");
    }

    public Set<String> getAllPackagesWithSecondaryDexFiles() {
        return this.mPackageDexUsage.getAllPackagesWithSecondaryDexFiles();
    }

    public final DexSearchResult getDexPackage(ApplicationInfo applicationInfo, String str, int i) {
        PackageCodeLocations packageCodeLocations = new PackageCodeLocations(applicationInfo, i);
        int searchDex = packageCodeLocations.searchDex(str, i);
        if (searchDex != DEX_SEARCH_NOT_FOUND) {
            return new DexSearchResult(packageCodeLocations.mPackageName, searchDex);
        }
        synchronized (this.mPackageCodeLocationsCache) {
            for (PackageCodeLocations packageCodeLocations2 : this.mPackageCodeLocationsCache.values()) {
                int searchDex2 = packageCodeLocations2.searchDex(str, i);
                if (searchDex2 != DEX_SEARCH_NOT_FOUND) {
                    return new DexSearchResult(packageCodeLocations2.mPackageName, searchDex2);
                }
            }
            if (isPlatformPackage(applicationInfo.packageName)) {
                if (isSystemServerDexPathSupportedForOdex(str)) {
                    return new DexSearchResult(PackageManagerShellCommandDataLoader.PACKAGE, DEX_SEARCH_FOUND_SECONDARY);
                }
                Slog.wtf("DexManager", "System server loads dex files outside paths supported for odex: " + str);
            }
            if (DEBUG) {
                try {
                    String realpath = PackageManagerServiceUtils.realpath(new File(str));
                    if (!str.equals(realpath)) {
                        Slog.d("DexManager", "Dex loaded with symlink. dexPath=" + str + " dexPathReal=" + realpath);
                    }
                } catch (IOException unused) {
                }
            }
            return new DexSearchResult(null, DEX_SEARCH_NOT_FOUND);
        }
    }

    public static boolean isPlatformPackage(String str) {
        return PackageManagerShellCommandDataLoader.PACKAGE.equals(str);
    }

    public static <K, V> V putIfAbsent(Map<K, V> map, K k, V v) {
        V putIfAbsent = map.putIfAbsent(k, v);
        return putIfAbsent == null ? v : putIfAbsent;
    }

    public void writePackageDexUsageNow() {
        this.mPackageDexUsage.writeNow();
    }

    public static boolean auditUncompressedDexInApk(String str) {
        StrictJarFile strictJarFile;
        StrictJarFile strictJarFile2 = null;
        try {
            try {
                strictJarFile = new StrictJarFile(str, false, false);
            } catch (Throwable th) {
                th = th;
            }
        } catch (IOException unused) {
        }
        try {
            Iterator it = strictJarFile.iterator();
            boolean z = true;
            while (it.hasNext()) {
                ZipEntry zipEntry = (ZipEntry) it.next();
                if (zipEntry.getName().endsWith(".dex")) {
                    if (zipEntry.getMethod() != 0) {
                        Slog.w("DexManager", "APK " + str + " has compressed dex code " + zipEntry.getName());
                    } else if ((zipEntry.getDataOffset() & 3) != 0) {
                        Slog.w("DexManager", "APK " + str + " has unaligned dex code " + zipEntry.getName());
                    }
                    z = false;
                }
            }
            try {
                strictJarFile.close();
            } catch (IOException unused2) {
            }
            return z;
        } catch (IOException unused3) {
            strictJarFile2 = strictJarFile;
            Slog.wtf("DexManager", "Error when parsing APK " + str);
            if (strictJarFile2 != null) {
                try {
                    strictJarFile2.close();
                } catch (IOException unused4) {
                }
            }
            return false;
        } catch (Throwable th2) {
            th = th2;
            strictJarFile2 = strictJarFile;
            if (strictJarFile2 != null) {
                try {
                    strictJarFile2.close();
                } catch (IOException unused5) {
                }
            }
            throw th;
        }
    }

    public int getCompilationReasonForInstallScenario(int i) {
        boolean areBatteryThermalOrMemoryCritical = areBatteryThermalOrMemoryCritical();
        if (i != 0) {
            if (i != 1) {
                if (i == 2) {
                    return areBatteryThermalOrMemoryCritical ? 7 : 5;
                } else if (i == 3) {
                    return areBatteryThermalOrMemoryCritical ? 8 : 6;
                } else {
                    throw new IllegalArgumentException("Invalid installation scenario");
                }
            }
            return 4;
        }
        return 3;
    }

    public final BatteryManager getBatteryManager() {
        Context context;
        if (this.mBatteryManager == null && (context = this.mContext) != null) {
            this.mBatteryManager = (BatteryManager) context.getSystemService(BatteryManager.class);
        }
        return this.mBatteryManager;
    }

    public final boolean areBatteryThermalOrMemoryCritical() {
        PowerManager powerManager;
        BatteryManager batteryManager = getBatteryManager();
        return (batteryManager != null && batteryManager.getIntProperty(6) == 3 && batteryManager.getIntProperty(4) <= this.mCriticalBatteryLevel) || ((powerManager = this.mPowerManager) != null && powerManager.getCurrentThermalStatus() >= 3);
    }

    public long deleteOptimizedFiles(ArtPackageInfo artPackageInfo) throws Installer.LegacyDexoptDisabledException {
        String packageName = artPackageInfo.getPackageName();
        long j = 0;
        boolean z = false;
        for (String str : artPackageInfo.getCodePaths()) {
            for (String str2 : artPackageInfo.getInstructionSets()) {
                try {
                    j += this.mInstaller.deleteOdex(packageName, str, str2, artPackageInfo.getOatDir());
                } catch (Installer.InstallerException e) {
                    Log.e("DexManager", "Failed deleting oat files for " + str, e);
                    z = true;
                }
            }
        }
        if (z) {
            return -1L;
        }
        return j;
    }

    /* renamed from: com.android.server.pm.dex.DexManager$RegisterDexModuleResult */
    /* loaded from: classes2.dex */
    public static class RegisterDexModuleResult {
        public final String message;
        public final boolean success;

        public RegisterDexModuleResult(boolean z, String str) {
            this.success = z;
            this.message = str;
        }
    }

    /* renamed from: com.android.server.pm.dex.DexManager$PackageCodeLocations */
    /* loaded from: classes2.dex */
    public static class PackageCodeLocations {
        public final Map<Integer, Set<String>> mAppDataDirs;
        public String mBaseCodePath;
        public final String mPackageName;
        public final Set<String> mSplitCodePaths;

        public PackageCodeLocations(ApplicationInfo applicationInfo, int i) {
            this(applicationInfo.packageName, applicationInfo.sourceDir, applicationInfo.splitSourceDirs);
            mergeAppDataDirs(applicationInfo.dataDir, i);
        }

        public PackageCodeLocations(String str, String str2, String[] strArr) {
            this.mPackageName = str;
            this.mSplitCodePaths = new HashSet();
            this.mAppDataDirs = new HashMap();
            updateCodeLocation(str2, strArr);
        }

        public void updateCodeLocation(String str, String[] strArr) {
            this.mBaseCodePath = str;
            this.mSplitCodePaths.clear();
            if (strArr != null) {
                for (String str2 : strArr) {
                    this.mSplitCodePaths.add(str2);
                }
            }
        }

        public void mergeAppDataDirs(String str, int i) {
            ((Set) DexManager.putIfAbsent(this.mAppDataDirs, Integer.valueOf(i), new HashSet())).add(str);
        }

        public int searchDex(String str, int i) {
            Set<String> set = this.mAppDataDirs.get(Integer.valueOf(i));
            if (set == null) {
                return DexManager.DEX_SEARCH_NOT_FOUND;
            }
            if (this.mBaseCodePath.equals(str)) {
                return DexManager.DEX_SEARCH_FOUND_PRIMARY;
            }
            if (this.mSplitCodePaths.contains(str)) {
                return DexManager.DEX_SEARCH_FOUND_SPLIT;
            }
            for (String str2 : set) {
                if (str.startsWith(str2)) {
                    return DexManager.DEX_SEARCH_FOUND_SECONDARY;
                }
            }
            return DexManager.DEX_SEARCH_NOT_FOUND;
        }
    }

    /* renamed from: com.android.server.pm.dex.DexManager$DexSearchResult */
    /* loaded from: classes2.dex */
    public class DexSearchResult {
        public int mOutcome;
        public String mOwningPackageName;

        public DexSearchResult(String str, int i) {
            this.mOwningPackageName = str;
            this.mOutcome = i;
        }

        public String toString() {
            return this.mOwningPackageName + PackageManagerShellCommandDataLoader.STDIN_PATH + this.mOutcome;
        }
    }
}
