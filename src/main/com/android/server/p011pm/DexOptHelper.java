package com.android.server.p011pm;

import android.app.AppGlobals;
import android.app.role.RoleManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.content.pm.SharedLibraryInfo;
import android.content.pm.dex.ArtManager;
import android.os.Binder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.LocalManagerRegistry;
import com.android.server.LocalServices;
import com.android.server.PinnerService;
import com.android.server.SystemServerInitThreadPool$$ExternalSyntheticLambda0;
import com.android.server.art.ArtManagerLocal;
import com.android.server.art.DexUseManagerLocal;
import com.android.server.art.model.DexoptResult;
import com.android.server.p011pm.ApexManager;
import com.android.server.p011pm.CompilerStats;
import com.android.server.p011pm.Installer;
import com.android.server.p011pm.PackageDexOptimizer;
import com.android.server.p011pm.PackageManagerLocal;
import com.android.server.p011pm.dex.DexManager;
import com.android.server.p011pm.dex.DexoptOptions;
import com.android.server.p011pm.parsing.pkg.AndroidPackageInternal;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageState;
import com.android.server.p011pm.pkg.PackageStateInternal;
import dalvik.system.DexFile;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
/* renamed from: com.android.server.pm.DexOptHelper */
/* loaded from: classes2.dex */
public final class DexOptHelper {
    public volatile long mBootDexoptStartTime;
    public final PackageManagerService mPm;

    public static /* synthetic */ boolean lambda$getPackagesForDexopt$6(PackageStateInternal packageStateInternal) {
        return true;
    }

    public static /* synthetic */ boolean lambda$getPackagesForDexopt$7(PackageStateInternal packageStateInternal) {
        return true;
    }

    public DexOptHelper(PackageManagerService packageManagerService) {
        this.mPm = packageManagerService;
    }

    public static String getPrebuildProfilePath(AndroidPackage androidPackage) {
        return androidPackage.getBaseApkPath() + ".prof";
    }

    /* JADX WARN: Removed duplicated region for block: B:36:0x0106  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x0102 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int[] performDexOptUpgrade(List<PackageStateInternal> list, int i, boolean z) throws Installer.LegacyDexoptDisabledException {
        boolean z2;
        Installer.checkLegacyDexoptDisabled();
        list.size();
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        for (PackageStateInternal packageStateInternal : list) {
            AndroidPackage androidPackage = packageStateInternal.getAndroidPackage();
            if ((this.mPm.isFirstBoot() || this.mPm.isDeviceUpgrading()) && packageStateInternal.isSystem()) {
                File file = new File(getPrebuildProfilePath(androidPackage));
                if (file.exists()) {
                    try {
                        if (!this.mPm.mInstaller.copySystemProfile(file.getAbsolutePath(), androidPackage.getUid(), androidPackage.getPackageName(), ArtManager.getProfileName((String) null))) {
                            Log.e("PackageManager", "Installer failed to copy system profile!");
                        }
                    } catch (Installer.InstallerException | RuntimeException e) {
                        Log.e("PackageManager", "Failed to copy profile " + file.getAbsolutePath() + " ", e);
                    }
                } else {
                    PackageSetting disabledSystemPkgLPr = this.mPm.mSettings.getDisabledSystemPkgLPr(androidPackage.getPackageName());
                    if (disabledSystemPkgLPr != null && disabledSystemPkgLPr.getPkg().isStub()) {
                        File file2 = new File(getPrebuildProfilePath(disabledSystemPkgLPr.getPkg()).replace("-Stub", ""));
                        if (file2.exists()) {
                            try {
                            } catch (Installer.InstallerException | RuntimeException e2) {
                                Log.e("PackageManager", "Failed to copy profile " + file2.getAbsolutePath() + " ", e2);
                            }
                            if (!this.mPm.mInstaller.copySystemProfile(file2.getAbsolutePath(), androidPackage.getUid(), androidPackage.getPackageName(), ArtManager.getProfileName((String) null))) {
                                Log.e("PackageManager", "Failed to copy system profile for stub package!");
                            } else {
                                z2 = true;
                                if (this.mPm.mPackageDexOptimizer.canOptimizePackage(androidPackage)) {
                                    int i5 = z2 ? 9 : i;
                                    if (SystemProperties.getBoolean("pm.precompile_layouts", false)) {
                                        this.mPm.mArtManagerService.compileLayouts(packageStateInternal, androidPackage);
                                    }
                                    int i6 = z ? 4 : 0;
                                    String compilerFilterForReason = PackageManagerServiceCompilerMapping.getCompilerFilterForReason(i5);
                                    if (DexFile.isProfileGuidedCompilerFilter(compilerFilterForReason)) {
                                        i6 |= 1;
                                    }
                                    if (i == 0) {
                                        i6 |= 1024;
                                    }
                                    int performDexOptTraced = performDexOptTraced(new DexoptOptions(androidPackage.getPackageName(), i5, compilerFilterForReason, null, i6));
                                    if (performDexOptTraced == -1) {
                                        i4++;
                                    } else if (performDexOptTraced != 0) {
                                        if (performDexOptTraced == 1) {
                                            i2++;
                                        } else if (performDexOptTraced != 2) {
                                            Log.e("PackageManager", "Unexpected dexopt return code " + performDexOptTraced);
                                        }
                                    }
                                }
                                i3++;
                            }
                        }
                    }
                }
            }
            z2 = false;
            if (this.mPm.mPackageDexOptimizer.canOptimizePackage(androidPackage)) {
            }
            i3++;
        }
        return new int[]{i2, i3, i4};
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:21:0x0096 -> B:22:0x0097). Please submit an issue!!! */
    public final void checkAndDexOptSystemUi(int i) throws Installer.LegacyDexoptDisabledException {
        Computer snapshotComputer = this.mPm.snapshotComputer();
        String string = this.mPm.mContext.getString(17039418);
        AndroidPackage androidPackage = snapshotComputer.getPackage(string);
        if (androidPackage == null) {
            Log.w("PackageManager", "System UI package " + string + " is not found for dexopting");
            return;
        }
        String str = SystemProperties.get("dalvik.vm.systemuicompilerfilter", PackageManagerServiceCompilerMapping.getCompilerFilterForReason(i));
        if (DexFile.isProfileGuidedCompilerFilter(str)) {
            String str2 = "verify";
            File file = new File(getPrebuildProfilePath(androidPackage));
            if (file.exists()) {
                try {
                    synchronized (this.mPm.mInstallLock) {
                        try {
                            if (!this.mPm.mInstaller.copySystemProfile(file.getAbsolutePath(), androidPackage.getUid(), androidPackage.getPackageName(), ArtManager.getProfileName((String) null))) {
                                Log.e("PackageManager", "Failed to copy profile " + file.getAbsolutePath());
                                str = "verify";
                            }
                            try {
                            } catch (Throwable th) {
                                th = th;
                                str2 = str;
                                throw th;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                        }
                    }
                } catch (Installer.InstallerException | RuntimeException e) {
                    Log.e("PackageManager", "Failed to copy profile " + file.getAbsolutePath(), e);
                }
            }
            str = str2;
        }
        performDexoptPackage(string, i, str);
    }

    public final void dexoptLauncher(int i) throws Installer.LegacyDexoptDisabledException {
        Computer snapshotComputer = this.mPm.snapshotComputer();
        for (String str : ((RoleManager) this.mPm.mContext.getSystemService(RoleManager.class)).getRoleHolders("android.app.role.HOME")) {
            if (snapshotComputer.getPackage(str) == null) {
                Log.w("PackageManager", "Launcher package " + str + " is not found for dexopting");
            } else {
                performDexoptPackage(str, i, "speed-profile");
            }
        }
    }

    public final void performDexoptPackage(String str, int i, String str2) throws Installer.LegacyDexoptDisabledException {
        Installer.checkLegacyDexoptDisabled();
        performDexOptTraced(new DexoptOptions(str, i, str2, null, DexFile.isProfileGuidedCompilerFilter(str2) ? 1 : 0));
    }

    public void performPackageDexOptUpgradeIfNeeded() {
        int i;
        PackageManagerServiceUtils.enforceSystemOrRoot("Only the system can request package update");
        if (this.mPm.isFirstBoot()) {
            i = 0;
        } else if (this.mPm.isDeviceUpgrading()) {
            i = 1;
        } else if (!hasBcpApexesChanged()) {
            return;
        } else {
            i = 13;
        }
        Log.i("PackageManager", "Starting boot dexopt for reason " + DexoptOptions.convertToArtServiceDexoptReason(i));
        long nanoTime = System.nanoTime();
        if (useArtService()) {
            this.mBootDexoptStartTime = nanoTime;
            getArtManagerLocal().onBoot(DexoptOptions.convertToArtServiceDexoptReason(i), (Executor) null, (Consumer) null);
            return;
        }
        try {
            checkAndDexOptSystemUi(i);
            dexoptLauncher(i);
            if (i == 1 || i == 0) {
                int[] performDexOptUpgrade = performDexOptUpgrade(getPackagesForDexopt(this.mPm.snapshotComputer().getPackageStates().values(), this.mPm), i, false);
                reportBootDexopt(nanoTime, performDexOptUpgrade[0], performDexOptUpgrade[1], performDexOptUpgrade[2]);
            }
        } catch (Installer.LegacyDexoptDisabledException e) {
            throw new RuntimeException(e);
        }
    }

    public final void reportBootDexopt(long j, int i, int i2, int i3) {
        Computer snapshotComputer = this.mPm.snapshotComputer();
        MetricsLogger.histogram(this.mPm.mContext, "opt_dialog_num_dexopted", i);
        MetricsLogger.histogram(this.mPm.mContext, "opt_dialog_num_skipped", i2);
        MetricsLogger.histogram(this.mPm.mContext, "opt_dialog_num_failed", i3);
        MetricsLogger.histogram(this.mPm.mContext, "opt_dialog_num_total", getOptimizablePackages(snapshotComputer).size());
        MetricsLogger.histogram(this.mPm.mContext, "opt_dialog_time_s", (int) TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - j));
    }

    public List<String> getOptimizablePackages(Computer computer) {
        final ArrayList arrayList = new ArrayList();
        this.mPm.forEachPackageState(computer, new Consumer() { // from class: com.android.server.pm.DexOptHelper$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DexOptHelper.this.lambda$getOptimizablePackages$0(arrayList, (PackageStateInternal) obj);
            }
        });
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getOptimizablePackages$0(ArrayList arrayList, PackageStateInternal packageStateInternal) {
        AndroidPackageInternal pkg = packageStateInternal.getPkg();
        if (pkg == null || !this.mPm.mPackageDexOptimizer.canOptimizePackage(pkg)) {
            return;
        }
        arrayList.add(packageStateInternal.getPackageName());
    }

    public boolean performDexOpt(DexoptOptions dexoptOptions) {
        int performDexOptWithStatus;
        Computer snapshotComputer = this.mPm.snapshotComputer();
        if (snapshotComputer.getInstantAppPackageName(Binder.getCallingUid()) == null && !snapshotComputer.isInstantApp(dexoptOptions.getPackageName(), UserHandle.getCallingUserId())) {
            AndroidPackage androidPackage = snapshotComputer.getPackage(dexoptOptions.getPackageName());
            if (androidPackage == null || !androidPackage.isApex()) {
                if (dexoptOptions.isDexoptOnlySecondaryDex()) {
                    if (useArtService()) {
                        performDexOptWithStatus = performDexOptWithArtService(dexoptOptions, 0);
                    } else {
                        try {
                            return this.mPm.getDexManager().dexoptSecondaryDex(dexoptOptions);
                        } catch (Installer.LegacyDexoptDisabledException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    performDexOptWithStatus = performDexOptWithStatus(dexoptOptions);
                }
                return performDexOptWithStatus != -1;
            }
            return true;
        }
        return false;
    }

    public int performDexOptWithStatus(DexoptOptions dexoptOptions) {
        return performDexOptTraced(dexoptOptions);
    }

    public final int performDexOptTraced(DexoptOptions dexoptOptions) {
        Trace.traceBegin(16384L, "dexopt");
        try {
            return performDexOptInternal(dexoptOptions);
        } finally {
            Trace.traceEnd(16384L);
        }
    }

    public final int performDexOptInternal(DexoptOptions dexoptOptions) {
        if (useArtService()) {
            return performDexOptWithArtService(dexoptOptions, 4);
        }
        synchronized (this.mPm.mLock) {
            AndroidPackage androidPackage = this.mPm.mPackages.get(dexoptOptions.getPackageName());
            PackageSetting packageLPr = this.mPm.mSettings.getPackageLPr(dexoptOptions.getPackageName());
            if (androidPackage != null && packageLPr != null) {
                if (androidPackage.isApex()) {
                    return 0;
                }
                this.mPm.getPackageUsage().maybeWriteAsync(this.mPm.mSettings.getPackagesLocked());
                this.mPm.mCompilerStats.maybeWriteAsync();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    try {
                        return performDexOptInternalWithDependenciesLI(androidPackage, packageLPr, dexoptOptions);
                    } catch (Installer.LegacyDexoptDisabledException e) {
                        throw new RuntimeException(e);
                    }
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            return -1;
        }
    }

    public final int performDexOptWithArtService(DexoptOptions dexoptOptions, int i) {
        PackageManagerLocal.FilteredSnapshot withFilteredSnapshot = PackageManagerServiceUtils.getPackageManagerLocal().withFilteredSnapshot();
        try {
            PackageState packageState = withFilteredSnapshot.getPackageState(dexoptOptions.getPackageName());
            if (packageState != null) {
                if (packageState.getAndroidPackage() != null) {
                    int convertToDexOptResult = convertToDexOptResult(getArtManagerLocal().dexoptPackage(withFilteredSnapshot, dexoptOptions.getPackageName(), dexoptOptions.convertToDexoptParams(i)));
                    withFilteredSnapshot.close();
                    return convertToDexOptResult;
                }
                withFilteredSnapshot.close();
                return -1;
            }
            withFilteredSnapshot.close();
            return -1;
        } catch (Throwable th) {
            if (withFilteredSnapshot != null) {
                try {
                    withFilteredSnapshot.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public final int performDexOptInternalWithDependenciesLI(AndroidPackage androidPackage, PackageStateInternal packageStateInternal, DexoptOptions dexoptOptions) throws Installer.LegacyDexoptDisabledException {
        PackageDexOptimizer packageDexOptimizer;
        if (PackageManagerShellCommandDataLoader.PACKAGE.equals(androidPackage.getPackageName())) {
            throw new IllegalArgumentException("Cannot dexopt the system server");
        }
        if (dexoptOptions.isForce()) {
            packageDexOptimizer = new PackageDexOptimizer.ForcedUpdatePackageDexOptimizer(this.mPm.mPackageDexOptimizer);
        } else {
            packageDexOptimizer = this.mPm.mPackageDexOptimizer;
        }
        List<SharedLibraryInfo> findSharedLibraries = SharedLibraryUtils.findSharedLibraries(packageStateInternal);
        String[] appDexInstructionSets = InstructionSets.getAppDexInstructionSets(packageStateInternal.getPrimaryCpuAbi(), packageStateInternal.getSecondaryCpuAbi());
        if (!findSharedLibraries.isEmpty()) {
            DexoptOptions dexoptOptions2 = new DexoptOptions(dexoptOptions.getPackageName(), dexoptOptions.getCompilationReason(), dexoptOptions.getCompilerFilter(), dexoptOptions.getSplitName(), dexoptOptions.getFlags() | 64);
            for (SharedLibraryInfo sharedLibraryInfo : findSharedLibraries) {
                Computer snapshotComputer = this.mPm.snapshotComputer();
                AndroidPackage androidPackage2 = snapshotComputer.getPackage(sharedLibraryInfo.getPackageName());
                PackageStateInternal packageStateInternal2 = snapshotComputer.getPackageStateInternal(sharedLibraryInfo.getPackageName());
                if (androidPackage2 != null && packageStateInternal2 != null) {
                    packageDexOptimizer.performDexOpt(androidPackage2, packageStateInternal2, appDexInstructionSets, this.mPm.getOrCreateCompilerPackageStats(androidPackage2), this.mPm.getDexManager().getPackageUseInfoOrDefault(androidPackage2.getPackageName()), dexoptOptions2);
                }
            }
        }
        return packageDexOptimizer.performDexOpt(androidPackage, packageStateInternal, appDexInstructionSets, this.mPm.getOrCreateCompilerPackageStats(androidPackage), this.mPm.getDexManager().getPackageUseInfoOrDefault(androidPackage.getPackageName()), dexoptOptions);
    }

    @Deprecated
    public void forceDexOpt(Computer computer, String str) throws Installer.LegacyDexoptDisabledException {
        PackageManagerServiceUtils.enforceSystemOrRoot("forceDexOpt");
        PackageStateInternal packageStateInternal = computer.getPackageStateInternal(str);
        AndroidPackageInternal pkg = packageStateInternal == null ? null : packageStateInternal.getPkg();
        if (packageStateInternal == null || pkg == null) {
            throw new IllegalArgumentException("Unknown package: " + str);
        } else if (pkg.isApex()) {
            throw new IllegalArgumentException("Can't dexopt APEX package: " + str);
        } else {
            Trace.traceBegin(16384L, "dexopt");
            int performDexOptInternalWithDependenciesLI = performDexOptInternalWithDependenciesLI(pkg, packageStateInternal, new DexoptOptions(str, 12, PackageManagerServiceCompilerMapping.getDefaultCompilerFilter(), null, 6));
            Trace.traceEnd(16384L);
            if (performDexOptInternalWithDependenciesLI == 1) {
                return;
            }
            throw new IllegalStateException("Failed to dexopt: " + performDexOptInternalWithDependenciesLI);
        }
    }

    public boolean performDexOptMode(Computer computer, String str, String str2, boolean z, boolean z2, String str3) {
        if (!PackageManagerServiceUtils.isSystemOrRootOrShell() && !isCallerInstallerForPackage(computer, str)) {
            throw new SecurityException("performDexOptMode");
        }
        int i = (z2 ? 4 : 0) | (z ? 2 : 0);
        if (DexFile.isProfileGuidedCompilerFilter(str2)) {
            i |= 1;
        }
        return performDexOpt(new DexoptOptions(str, 12, str2, str3, i));
    }

    public final boolean isCallerInstallerForPackage(Computer computer, String str) {
        PackageStateInternal packageStateInternal;
        PackageStateInternal packageStateInternal2 = computer.getPackageStateInternal(str);
        return (packageStateInternal2 == null || (packageStateInternal = computer.getPackageStateInternal(packageStateInternal2.getInstallSource().mInstallerPackageName)) == null || packageStateInternal.getPkg().getUid() != Binder.getCallingUid()) ? false : true;
    }

    public boolean performDexOptSecondary(String str, String str2, boolean z) {
        return performDexOpt(new DexoptOptions(str, 12, str2, null, (z ? 2 : 0) | 13));
    }

    public static List<PackageStateInternal> getPackagesForDexopt(Collection<? extends PackageStateInternal> collection, PackageManagerService packageManagerService) {
        return getPackagesForDexopt(collection, packageManagerService, false);
    }

    public static List<PackageStateInternal> getPackagesForDexopt(Collection<? extends PackageStateInternal> collection, PackageManagerService packageManagerService, boolean z) {
        Predicate predicate;
        Predicate predicate2;
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList(collection);
        arrayList2.removeIf(PackageManagerServiceUtils.REMOVE_IF_NULL_PKG);
        arrayList2.removeIf(PackageManagerServiceUtils.REMOVE_IF_APEX_PKG);
        ArrayList arrayList3 = new ArrayList(arrayList2.size());
        Computer snapshotComputer = packageManagerService.snapshotComputer();
        applyPackageFilter(snapshotComputer, new Predicate() { // from class: com.android.server.pm.DexOptHelper$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getPackagesForDexopt$1;
                lambda$getPackagesForDexopt$1 = DexOptHelper.lambda$getPackagesForDexopt$1((PackageStateInternal) obj);
                return lambda$getPackagesForDexopt$1;
            }
        }, arrayList, arrayList2, arrayList3, packageManagerService);
        final ArraySet<String> packageNamesForIntent = getPackageNamesForIntent(new Intent("android.intent.action.PRE_BOOT_COMPLETED"), 0);
        applyPackageFilter(snapshotComputer, new Predicate() { // from class: com.android.server.pm.DexOptHelper$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getPackagesForDexopt$2;
                lambda$getPackagesForDexopt$2 = DexOptHelper.lambda$getPackagesForDexopt$2(packageNamesForIntent, (PackageStateInternal) obj);
                return lambda$getPackagesForDexopt$2;
            }
        }, arrayList, arrayList2, arrayList3, packageManagerService);
        final DexManager dexManager = packageManagerService.getDexManager();
        applyPackageFilter(snapshotComputer, new Predicate() { // from class: com.android.server.pm.DexOptHelper$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getPackagesForDexopt$3;
                lambda$getPackagesForDexopt$3 = DexOptHelper.lambda$getPackagesForDexopt$3(DexManager.this, (PackageStateInternal) obj);
                return lambda$getPackagesForDexopt$3;
            }
        }, arrayList, arrayList2, arrayList3, packageManagerService);
        if (!arrayList2.isEmpty() && packageManagerService.isHistoricalPackageUsageAvailable()) {
            if (z) {
                Log.i("PackageManager", "Looking at historical package use");
            }
            PackageStateInternal packageStateInternal = (PackageStateInternal) Collections.max(arrayList2, Comparator.comparingLong(new ToLongFunction() { // from class: com.android.server.pm.DexOptHelper$$ExternalSyntheticLambda4
                @Override // java.util.function.ToLongFunction
                public final long applyAsLong(Object obj) {
                    long lambda$getPackagesForDexopt$4;
                    lambda$getPackagesForDexopt$4 = DexOptHelper.lambda$getPackagesForDexopt$4((PackageStateInternal) obj);
                    return lambda$getPackagesForDexopt$4;
                }
            }));
            if (z) {
                Log.i("PackageManager", "Taking package " + packageStateInternal.getPackageName() + " as reference in time use");
            }
            long latestForegroundPackageUseTimeInMills = packageStateInternal.getTransientState().getLatestForegroundPackageUseTimeInMills();
            if (latestForegroundPackageUseTimeInMills != 0) {
                final long j = latestForegroundPackageUseTimeInMills - 604800000;
                predicate2 = new Predicate() { // from class: com.android.server.pm.DexOptHelper$$ExternalSyntheticLambda5
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$getPackagesForDexopt$5;
                        lambda$getPackagesForDexopt$5 = DexOptHelper.lambda$getPackagesForDexopt$5(j, (PackageStateInternal) obj);
                        return lambda$getPackagesForDexopt$5;
                    }
                };
            } else {
                predicate2 = new Predicate() { // from class: com.android.server.pm.DexOptHelper$$ExternalSyntheticLambda6
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$getPackagesForDexopt$6;
                        lambda$getPackagesForDexopt$6 = DexOptHelper.lambda$getPackagesForDexopt$6((PackageStateInternal) obj);
                        return lambda$getPackagesForDexopt$6;
                    }
                };
            }
            sortPackagesByUsageDate(arrayList2, packageManagerService);
            predicate = predicate2;
        } else {
            predicate = new Predicate() { // from class: com.android.server.pm.DexOptHelper$$ExternalSyntheticLambda7
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getPackagesForDexopt$7;
                    lambda$getPackagesForDexopt$7 = DexOptHelper.lambda$getPackagesForDexopt$7((PackageStateInternal) obj);
                    return lambda$getPackagesForDexopt$7;
                }
            };
        }
        applyPackageFilter(snapshotComputer, predicate, arrayList, arrayList2, arrayList3, packageManagerService);
        if (z) {
            Log.i("PackageManager", "Packages to be dexopted: " + packagesToString(arrayList));
            Log.i("PackageManager", "Packages skipped from dexopt: " + packagesToString(arrayList2));
        }
        return arrayList;
    }

    public static /* synthetic */ boolean lambda$getPackagesForDexopt$1(PackageStateInternal packageStateInternal) {
        return packageStateInternal.getPkg().isCoreApp();
    }

    public static /* synthetic */ boolean lambda$getPackagesForDexopt$2(ArraySet arraySet, PackageStateInternal packageStateInternal) {
        return arraySet.contains(packageStateInternal.getPackageName());
    }

    public static /* synthetic */ boolean lambda$getPackagesForDexopt$3(DexManager dexManager, PackageStateInternal packageStateInternal) {
        return dexManager.getPackageUseInfoOrDefault(packageStateInternal.getPackageName()).isAnyCodePathUsedByOtherApps();
    }

    public static /* synthetic */ long lambda$getPackagesForDexopt$4(PackageStateInternal packageStateInternal) {
        return packageStateInternal.getTransientState().getLatestForegroundPackageUseTimeInMills();
    }

    public static /* synthetic */ boolean lambda$getPackagesForDexopt$5(long j, PackageStateInternal packageStateInternal) {
        return packageStateInternal.getTransientState().getLatestForegroundPackageUseTimeInMills() >= j;
    }

    public static void applyPackageFilter(Computer computer, Predicate<PackageStateInternal> predicate, Collection<PackageStateInternal> collection, Collection<PackageStateInternal> collection2, List<PackageStateInternal> list, PackageManagerService packageManagerService) {
        for (PackageStateInternal packageStateInternal : collection2) {
            if (predicate.test(packageStateInternal)) {
                list.add(packageStateInternal);
            }
        }
        sortPackagesByUsageDate(list, packageManagerService);
        collection2.removeAll(list);
        for (PackageStateInternal packageStateInternal2 : list) {
            collection.add(packageStateInternal2);
            List<PackageStateInternal> findSharedNonSystemLibraries = computer.findSharedNonSystemLibraries(packageStateInternal2);
            if (!findSharedNonSystemLibraries.isEmpty()) {
                findSharedNonSystemLibraries.removeAll(collection);
                collection.addAll(findSharedNonSystemLibraries);
                collection2.removeAll(findSharedNonSystemLibraries);
            }
        }
        list.clear();
    }

    public static void sortPackagesByUsageDate(List<PackageStateInternal> list, PackageManagerService packageManagerService) {
        if (packageManagerService.isHistoricalPackageUsageAvailable()) {
            Collections.sort(list, new Comparator() { // from class: com.android.server.pm.DexOptHelper$$ExternalSyntheticLambda8
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    int lambda$sortPackagesByUsageDate$8;
                    lambda$sortPackagesByUsageDate$8 = DexOptHelper.lambda$sortPackagesByUsageDate$8((PackageStateInternal) obj, (PackageStateInternal) obj2);
                    return lambda$sortPackagesByUsageDate$8;
                }
            });
        }
    }

    public static /* synthetic */ int lambda$sortPackagesByUsageDate$8(PackageStateInternal packageStateInternal, PackageStateInternal packageStateInternal2) {
        return Long.compare(packageStateInternal2.getTransientState().getLatestForegroundPackageUseTimeInMills(), packageStateInternal.getTransientState().getLatestForegroundPackageUseTimeInMills());
    }

    public static ArraySet<String> getPackageNamesForIntent(Intent intent, int i) {
        List<ResolveInfo> list;
        try {
            list = AppGlobals.getPackageManager().queryIntentReceivers(intent, (String) null, 0L, i).getList();
        } catch (RemoteException unused) {
            list = null;
        }
        ArraySet<String> arraySet = new ArraySet<>();
        if (list != null) {
            for (ResolveInfo resolveInfo : list) {
                arraySet.add(resolveInfo.activityInfo.packageName);
            }
        }
        return arraySet;
    }

    public static String packagesToString(List<PackageStateInternal> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(list.get(i).getPackageName());
        }
        return sb.toString();
    }

    public static void requestCopyPreoptedFiles() {
        if (SystemProperties.getInt("ro.cp_system_other_odex", 0) == 1) {
            SystemProperties.set("sys.cppreopt", "requested");
            long uptimeMillis = SystemClock.uptimeMillis();
            long j = 100000 + uptimeMillis;
            long j2 = uptimeMillis;
            while (true) {
                if (!SystemProperties.get("sys.cppreopt").equals("finished")) {
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException unused) {
                    }
                    j2 = SystemClock.uptimeMillis();
                    if (j2 > j) {
                        SystemProperties.set("sys.cppreopt", "timed-out");
                        Slog.wtf("PackageManager", "cppreopt did not finish!");
                        break;
                    }
                } else {
                    break;
                }
            }
            Slog.i("PackageManager", "cppreopts took " + (j2 - uptimeMillis) + " ms");
        }
    }

    public void controlDexOptBlocking(boolean z) throws Installer.LegacyDexoptDisabledException {
        this.mPm.mPackageDexOptimizer.controlDexOptBlocking(z);
    }

    public static void dumpDexoptState(IndentingPrintWriter indentingPrintWriter, String str) {
        PackageManagerLocal.FilteredSnapshot withFilteredSnapshot = PackageManagerServiceUtils.getPackageManagerLocal().withFilteredSnapshot();
        try {
            if (str != null) {
                try {
                    getArtManagerLocal().dumpPackage(indentingPrintWriter, withFilteredSnapshot, str);
                } catch (IllegalArgumentException e) {
                    indentingPrintWriter.println(e);
                }
            } else {
                getArtManagerLocal().dump(indentingPrintWriter, withFilteredSnapshot);
            }
            if (withFilteredSnapshot != null) {
                withFilteredSnapshot.close();
            }
        } catch (Throwable th) {
            if (withFilteredSnapshot != null) {
                try {
                    withFilteredSnapshot.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static List<String> getBcpApexes() {
        String str = System.getenv("BOOTCLASSPATH");
        if (TextUtils.isEmpty(str)) {
            Log.e("PackageManager", "Unable to get BOOTCLASSPATH");
            return List.of();
        }
        ArrayList arrayList = new ArrayList();
        for (String str2 : str.split(XmlUtils.STRING_ARRAY_SEPARATOR)) {
            Path path = Paths.get(str2, new String[0]);
            if (path.getNameCount() >= 2 && path.getName(0).toString().equals("apex")) {
                arrayList.add(path.getName(1).toString());
            }
        }
        return arrayList;
    }

    public static boolean hasBcpApexesChanged() {
        HashSet hashSet = new HashSet(getBcpApexes());
        for (ApexManager.ActiveApexInfo activeApexInfo : ApexManager.getInstance().getActiveApexInfos()) {
            if (hashSet.contains(activeApexInfo.apexModuleName) && activeApexInfo.activeApexChanged) {
                return true;
            }
        }
        return false;
    }

    public static boolean useArtService() {
        return SystemProperties.getBoolean("dalvik.vm.useartservice", false);
    }

    public static DexUseManagerLocal getDexUseManagerLocal() {
        if (useArtService()) {
            try {
                return (DexUseManagerLocal) LocalManagerRegistry.getManagerOrThrow(DexUseManagerLocal.class);
            } catch (LocalManagerRegistry.ManagerNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /* renamed from: com.android.server.pm.DexOptHelper$DexoptDoneHandler */
    /* loaded from: classes2.dex */
    public class DexoptDoneHandler implements ArtManagerLocal.DexoptDoneCallback {
        public DexoptDoneHandler() {
        }

        public void onDexoptDone(DexoptResult dexoptResult) {
            String reason = dexoptResult.getReason();
            reason.hashCode();
            char c = 65535;
            switch (reason.hashCode()) {
                case -1205769507:
                    if (reason.equals("boot-after-mainline-update")) {
                        c = 0;
                        break;
                    }
                    break;
                case -587828592:
                    if (reason.equals("boot-after-ota")) {
                        c = 1;
                        break;
                    }
                    break;
                case -207505425:
                    if (reason.equals("first-boot")) {
                        c = 2;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                case 1:
                case 2:
                    int i = 0;
                    int i2 = 0;
                    int i3 = 0;
                    for (DexoptResult.PackageDexoptResult packageDexoptResult : dexoptResult.getPackageDexoptResults()) {
                        int status = packageDexoptResult.getStatus();
                        if (status == 10) {
                            i2++;
                        } else if (status == 20) {
                            i++;
                        } else if (status == 30) {
                            i3++;
                        }
                    }
                    DexOptHelper dexOptHelper = DexOptHelper.this;
                    dexOptHelper.reportBootDexopt(dexOptHelper.mBootDexoptStartTime, i, i2, i3);
                    break;
            }
            for (DexoptResult.PackageDexoptResult packageDexoptResult2 : dexoptResult.getPackageDexoptResults()) {
                CompilerStats.PackageStats orCreateCompilerPackageStats = DexOptHelper.this.mPm.getOrCreateCompilerPackageStats(packageDexoptResult2.getPackageName());
                for (DexoptResult.DexContainerFileDexoptResult dexContainerFileDexoptResult : packageDexoptResult2.getDexContainerFileDexoptResults()) {
                    orCreateCompilerPackageStats.setCompileTime(dexContainerFileDexoptResult.getDexContainerFile(), dexContainerFileDexoptResult.getDex2oatWallTimeMillis());
                }
            }
            synchronized (DexOptHelper.this.mPm.mLock) {
                DexOptHelper.this.mPm.getPackageUsage().maybeWriteAsync(DexOptHelper.this.mPm.mSettings.getPackagesLocked());
                DexOptHelper.this.mPm.mCompilerStats.maybeWriteAsync();
            }
            if (dexoptResult.getReason().equals("inactive")) {
                for (DexoptResult.PackageDexoptResult packageDexoptResult3 : dexoptResult.getPackageDexoptResults()) {
                    if (packageDexoptResult3.getStatus() == 20) {
                        long j = 0;
                        long j2 = 0;
                        for (DexoptResult.DexContainerFileDexoptResult dexContainerFileDexoptResult2 : packageDexoptResult3.getDexContainerFileDexoptResults()) {
                            long length = new File(dexContainerFileDexoptResult2.getDexContainerFile()).length();
                            j2 += dexContainerFileDexoptResult2.getSizeBytes() + length;
                            j += dexContainerFileDexoptResult2.getSizeBeforeBytes() + length;
                        }
                        FrameworkStatsLog.write(128, packageDexoptResult3.getPackageName(), j, j2, false);
                    }
                }
            }
            ArraySet<String> arraySet = new ArraySet<>();
            for (DexoptResult.PackageDexoptResult packageDexoptResult4 : dexoptResult.getPackageDexoptResults()) {
                if (packageDexoptResult4.hasUpdatedArtifacts()) {
                    arraySet.add(packageDexoptResult4.getPackageName());
                }
            }
            if (arraySet.isEmpty()) {
                return;
            }
            ((PinnerService) LocalServices.getService(PinnerService.class)).update(arraySet, false);
        }
    }

    public static void initializeArtManagerLocal(Context context, PackageManagerService packageManagerService) {
        if (useArtService()) {
            final ArtManagerLocal artManagerLocal = new ArtManagerLocal(context);
            SystemServerInitThreadPool$$ExternalSyntheticLambda0 systemServerInitThreadPool$$ExternalSyntheticLambda0 = new SystemServerInitThreadPool$$ExternalSyntheticLambda0();
            DexOptHelper dexOptHelper = packageManagerService.getDexOptHelper();
            Objects.requireNonNull(dexOptHelper);
            artManagerLocal.addDexoptDoneCallback(false, systemServerInitThreadPool$$ExternalSyntheticLambda0, new DexoptDoneHandler());
            LocalManagerRegistry.addManager(ArtManagerLocal.class, artManagerLocal);
            context.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.pm.DexOptHelper.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context2, Intent intent) {
                    context2.unregisterReceiver(this);
                    artManagerLocal.scheduleBackgroundDexoptJob();
                }
            }, new IntentFilter("android.intent.action.BOOT_COMPLETED"));
        }
    }

    public static ArtManagerLocal getArtManagerLocal() {
        try {
            return (ArtManagerLocal) LocalManagerRegistry.getManagerOrThrow(ArtManagerLocal.class);
        } catch (LocalManagerRegistry.ManagerNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static int convertToDexOptResult(DexoptResult dexoptResult) {
        int finalStatus = dexoptResult.getFinalStatus();
        if (finalStatus != 10) {
            if (finalStatus != 20) {
                if (finalStatus != 30) {
                    if (finalStatus == 40) {
                        return 2;
                    }
                    throw new IllegalArgumentException("DexoptResult for " + ((DexoptResult.PackageDexoptResult) dexoptResult.getPackageDexoptResults().get(0)).getPackageName() + " has unsupported status " + finalStatus);
                }
                return -1;
            }
            return 1;
        }
        return 0;
    }
}
