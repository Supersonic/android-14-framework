package com.android.server.p011pm;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.SharedLibraryInfo;
import android.content.pm.dex.ArtManager;
import android.content.pm.dex.DexMetadataHelper;
import android.os.FileUtils;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.os.WorkSource;
import android.p005os.IInstalld;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.content.F2fsUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.LocalServices;
import com.android.server.apphibernation.AppHibernationManagerInternal;
import com.android.server.p011pm.CompilerStats;
import com.android.server.p011pm.Installer;
import com.android.server.p011pm.dex.ArtStatsLogUtils;
import com.android.server.p011pm.dex.DexoptOptions;
import com.android.server.p011pm.dex.DexoptUtils;
import com.android.server.p011pm.dex.PackageDexUsage;
import com.android.server.p011pm.parsing.pkg.AndroidPackageUtils;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageState;
import com.android.server.p011pm.pkg.PackageStateInternal;
import dalvik.system.DexFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
/* renamed from: com.android.server.pm.PackageDexOptimizer */
/* loaded from: classes2.dex */
public class PackageDexOptimizer {
    public static final Random sRandom = new Random();
    public final ArtStatsLogUtils.ArtStatsLogger mArtStatsLogger;
    public final Context mContext;
    @GuardedBy({"mInstallLock"})
    public final PowerManager.WakeLock mDexoptWakeLock;
    public final Injector mInjector;
    public final Object mInstallLock;
    public final Installer mInstaller;
    public volatile boolean mSystemReady;

    /* renamed from: com.android.server.pm.PackageDexOptimizer$Injector */
    /* loaded from: classes2.dex */
    public interface Injector {
        AppHibernationManagerInternal getAppHibernationManagerInternal();

        PowerManager getPowerManager(Context context);
    }

    public int adjustDexoptFlags(int i) {
        return i;
    }

    public int adjustDexoptNeeded(int i) {
        return i;
    }

    public PackageDexOptimizer(Installer installer, Object obj, Context context, String str) {
        this(new Injector() { // from class: com.android.server.pm.PackageDexOptimizer.1
            @Override // com.android.server.p011pm.PackageDexOptimizer.Injector
            public AppHibernationManagerInternal getAppHibernationManagerInternal() {
                return (AppHibernationManagerInternal) LocalServices.getService(AppHibernationManagerInternal.class);
            }

            @Override // com.android.server.p011pm.PackageDexOptimizer.Injector
            public PowerManager getPowerManager(Context context2) {
                return (PowerManager) context2.getSystemService(PowerManager.class);
            }
        }, installer, obj, context, str);
    }

    public PackageDexOptimizer(PackageDexOptimizer packageDexOptimizer) {
        this.mArtStatsLogger = new ArtStatsLogUtils.ArtStatsLogger();
        this.mContext = packageDexOptimizer.mContext;
        this.mInstaller = packageDexOptimizer.mInstaller;
        this.mInstallLock = packageDexOptimizer.mInstallLock;
        this.mDexoptWakeLock = packageDexOptimizer.mDexoptWakeLock;
        this.mSystemReady = packageDexOptimizer.mSystemReady;
        this.mInjector = packageDexOptimizer.mInjector;
    }

    @VisibleForTesting
    public PackageDexOptimizer(Injector injector, Installer installer, Object obj, Context context, String str) {
        this.mArtStatsLogger = new ArtStatsLogUtils.ArtStatsLogger();
        this.mContext = context;
        this.mInstaller = installer;
        this.mInstallLock = obj;
        this.mDexoptWakeLock = injector.getPowerManager(context).newWakeLock(1, str);
        this.mInjector = injector;
    }

    public boolean canOptimizePackage(AndroidPackage androidPackage) {
        if (PackageManagerShellCommandDataLoader.PACKAGE.equals(androidPackage.getPackageName()) || !androidPackage.isDeclaredHavingCode() || androidPackage.isApex()) {
            return false;
        }
        AppHibernationManagerInternal appHibernationManagerInternal = this.mInjector.getAppHibernationManagerInternal();
        return (appHibernationManagerInternal != null && appHibernationManagerInternal.isHibernatingGlobally(androidPackage.getPackageName()) && appHibernationManagerInternal.isOatArtifactDeletionEnabled()) ? false : true;
    }

    public int performDexOpt(AndroidPackage androidPackage, PackageStateInternal packageStateInternal, String[] strArr, CompilerStats.PackageStats packageStats, PackageDexUsage.PackageUseInfo packageUseInfo, DexoptOptions dexoptOptions) throws Installer.LegacyDexoptDisabledException {
        int performDexOptLI;
        if (PackageManagerShellCommandDataLoader.PACKAGE.equals(androidPackage.getPackageName())) {
            throw new IllegalArgumentException("System server dexopting should be done via odrefresh");
        }
        if (androidPackage.getUid() == -1) {
            throw new IllegalArgumentException("Dexopt for " + androidPackage.getPackageName() + " has invalid uid.");
        } else if (canOptimizePackage(androidPackage)) {
            synchronized (this.mInstallLock) {
                long acquireWakeLockLI = acquireWakeLockLI(androidPackage.getUid());
                performDexOptLI = performDexOptLI(androidPackage, packageStateInternal, strArr, packageStats, packageUseInfo, dexoptOptions);
                releaseWakeLockLI(acquireWakeLockLI);
            }
            return performDexOptLI;
        } else {
            return 0;
        }
    }

    public void controlDexOptBlocking(boolean z) throws Installer.LegacyDexoptDisabledException {
        getInstallerWithoutLock().controlDexOptBlocking(z);
    }

    /* JADX WARN: Removed duplicated region for block: B:174:0x0336 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    @GuardedBy({"mInstallLock"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int performDexOptLI(AndroidPackage androidPackage, PackageStateInternal packageStateInternal, String[] strArr, CompilerStats.PackageStats packageStats, PackageDexUsage.PackageUseInfo packageUseInfo, DexoptOptions dexoptOptions) throws Installer.LegacyDexoptDisabledException {
        int i;
        String[] strArr2;
        boolean[] zArr;
        int i2;
        char c;
        List<String> list;
        String[] strArr3;
        CompilerStats.PackageStats packageStats2;
        PackageDexOptimizer packageDexOptimizer;
        String str;
        int i3;
        File findDexMetadataForFile;
        Throwable th;
        String str2;
        String str3;
        PackageDexOptimizer packageDexOptimizer2;
        String str4;
        String compilerFilterForReason;
        String str5;
        String str6;
        int i4;
        String str7;
        String str8;
        String str9;
        PackageDexOptimizer packageDexOptimizer3 = this;
        AndroidPackage androidPackage2 = androidPackage;
        CompilerStats.PackageStats packageStats3 = packageStats;
        List<SharedLibraryInfo> nonNativeUsesLibraryInfos = packageStateInternal.getTransientState().getNonNativeUsesLibraryInfos();
        String[] dexCodeInstructionSets = InstructionSets.getDexCodeInstructionSets(strArr != null ? strArr : InstructionSets.getAppDexInstructionSets(packageStateInternal.getPrimaryCpuAbi(), packageStateInternal.getSecondaryCpuAbi()));
        List<String> allCodePaths = AndroidPackageUtils.getAllCodePaths(androidPackage);
        int sharedAppGid = UserHandle.getSharedAppGid(androidPackage.getUid());
        char c2 = 65535;
        String str10 = "PackageDexOptimizer";
        if (sharedAppGid == -1) {
            Slog.wtf("PackageDexOptimizer", "Well this is awkward; package " + androidPackage.getPackageName() + " had UID " + androidPackage.getUid(), new Throwable());
            sharedAppGid = 9999;
        }
        int i5 = sharedAppGid;
        boolean[] zArr2 = new boolean[allCodePaths.size()];
        zArr2[0] = androidPackage.isDeclaredHavingCode();
        for (int i6 = 1; i6 < allCodePaths.size(); i6++) {
            zArr2[i6] = (androidPackage.getSplitFlags()[i6 + (-1)] & 4) != 0;
        }
        String[] classLoaderContexts = DexoptUtils.getClassLoaderContexts(androidPackage2, nonNativeUsesLibraryInfos, zArr2);
        if (allCodePaths.size() != classLoaderContexts.length) {
            String[] splitCodePaths = androidPackage.getSplitCodePaths();
            StringBuilder sb = new StringBuilder();
            sb.append("Inconsistent information between AndroidPackage and its ApplicationInfo. pkg.getAllCodePaths=");
            sb.append(allCodePaths);
            sb.append(" pkg.getBaseCodePath=");
            sb.append(androidPackage.getBaseApkPath());
            sb.append(" pkg.getSplitCodePaths=");
            sb.append(splitCodePaths == null ? "null" : Arrays.toString(splitCodePaths));
            throw new IllegalStateException(sb.toString());
        }
        int i7 = 0;
        int i8 = 0;
        while (i8 < allCodePaths.size()) {
            if (zArr2[i8]) {
                if (classLoaderContexts[i8] == null) {
                    throw new IllegalStateException("Inconsistent information in the package structure. A split is marked to contain code but has no dependency listed. Index=" + i8 + " path=" + allCodePaths.get(i3));
                }
                String str11 = allCodePaths.get(i8);
                if (dexoptOptions.getSplitName() == null || dexoptOptions.getSplitName().equals(new File(str11).getName())) {
                    String profileName = ArtManager.getProfileName(i8 == 0 ? null : androidPackage.getSplitNames()[i8 - 1]);
                    boolean isUsedByOtherApps = dexoptOptions.isDexoptAsSharedLibrary() ? true : DexOptHelper.useArtService() ? false : packageUseInfo.isUsedByOtherApps(str11);
                    String realCompilerFilter = packageDexOptimizer3.getRealCompilerFilter(androidPackage2, dexoptOptions.getCompilerFilter());
                    boolean z = DexFile.isProfileGuidedCompilerFilter(realCompilerFilter) && isUsedByOtherApps && dexoptOptions.getCompilationReason() != 3;
                    String absolutePath = ((dexoptOptions.isDexoptInstallWithDexMetadata() || z) && (findDexMetadataForFile = DexMetadataHelper.findDexMetadataForFile(new File(str11))) != null) ? findDexMetadataForFile.getAbsolutePath() : null;
                    int i9 = i7;
                    int analyseProfiles = dexoptOptions.isCheckForProfileUpdates() ? packageDexOptimizer3.analyseProfiles(androidPackage2, i5, profileName, realCompilerFilter) : 2;
                    String str12 = "Failed to cleanup cloud profile";
                    if (z) {
                        try {
                            str4 = "cloud-" + profileName;
                            try {
                                if (packageDexOptimizer3.prepareCloudProfile(androidPackage2, str4, str11, absolutePath)) {
                                    str5 = str4;
                                    compilerFilterForReason = realCompilerFilter;
                                } else {
                                    compilerFilterForReason = PackageManagerServiceCompilerMapping.getCompilerFilterForReason(14);
                                    str5 = null;
                                }
                                str6 = str5;
                                i4 = 2;
                                str7 = str4;
                            } catch (Throwable th2) {
                                th = th2;
                                str2 = str10;
                                str3 = "Failed to cleanup cloud profile";
                                packageDexOptimizer2 = packageDexOptimizer3;
                                if (str4 != null) {
                                }
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            str2 = str10;
                            str3 = "Failed to cleanup cloud profile";
                            packageDexOptimizer2 = packageDexOptimizer3;
                            str4 = null;
                        }
                    } else {
                        str6 = profileName;
                        i4 = analyseProfiles;
                        compilerFilterForReason = realCompilerFilter;
                        str7 = null;
                    }
                    String str13 = str7;
                    String str14 = absolutePath;
                    String str15 = str11;
                    int i10 = i8;
                    strArr2 = classLoaderContexts;
                    try {
                        int dexFlags = getDexFlags(androidPackage, packageStateInternal, compilerFilterForReason, z, dexoptOptions);
                        int length = dexCodeInstructionSets.length;
                        int i11 = 0;
                        int i12 = i9;
                        while (i11 < length) {
                            String str16 = dexCodeInstructionSets[i11];
                            int i13 = i11;
                            int i14 = i12;
                            boolean[] zArr3 = zArr2;
                            int i15 = i5;
                            String str17 = str10;
                            int i16 = length;
                            List<String> list2 = allCodePaths;
                            String[] strArr4 = dexCodeInstructionSets;
                            String str18 = str12;
                            int i17 = i10;
                            try {
                                i12 = dexOptPath(androidPackage, packageStateInternal, str15, str16, compilerFilterForReason, i4, strArr2[i10], dexFlags, i15, packageStats, dexoptOptions.isDowngrade(), str6, str14, dexoptOptions.getCompilationReason());
                                if (packageStats != null) {
                                    try {
                                        Trace.traceBegin(16384L, "dex2oat-metrics");
                                        try {
                                            packageDexOptimizer2 = this;
                                            try {
                                                str9 = str15;
                                                ArtStatsLogUtils.writeStatsLog(packageDexOptimizer2.mArtStatsLogger, sRandom.nextLong(), compilerFilterForReason, androidPackage.getUid(), packageStats.getCompileTime(str9), str14, dexoptOptions.getCompilationReason(), i12, ArtStatsLogUtils.getApkType(str9, androidPackage.getBaseApkPath(), androidPackage.getSplitCodePaths()), str16, str9);
                                                try {
                                                    Trace.traceEnd(16384L);
                                                } catch (Throwable th4) {
                                                    th = th4;
                                                    th = th;
                                                    str4 = str13;
                                                    str2 = str17;
                                                    str3 = str18;
                                                    if (str4 != null) {
                                                        try {
                                                            packageDexOptimizer2.mInstaller.deleteReferenceProfile(androidPackage.getPackageName(), str4);
                                                        } catch (Installer.InstallerException e) {
                                                            Slog.w(str2, str3, e);
                                                        }
                                                    }
                                                    throw th;
                                                }
                                            } catch (Throwable th5) {
                                                th = th5;
                                                Trace.traceEnd(16384L);
                                                throw th;
                                            }
                                        } catch (Throwable th6) {
                                            th = th6;
                                            packageDexOptimizer2 = this;
                                        }
                                    } catch (Throwable th7) {
                                        th = th7;
                                        packageDexOptimizer2 = this;
                                    }
                                } else {
                                    packageDexOptimizer2 = this;
                                    str9 = str15;
                                }
                                if (i12 == 2) {
                                    if (i14 == -1) {
                                        String str19 = str13;
                                        if (str19 != null) {
                                            try {
                                                packageDexOptimizer2.mInstaller.deleteReferenceProfile(androidPackage.getPackageName(), str19);
                                            } catch (Installer.InstallerException e2) {
                                                Slog.w(str17, str18, e2);
                                            }
                                        }
                                        return i14;
                                    }
                                    String str20 = str13;
                                    if (str20 != null) {
                                        try {
                                            packageDexOptimizer2.mInstaller.deleteReferenceProfile(androidPackage.getPackageName(), str20);
                                        } catch (Installer.InstallerException e3) {
                                            Slog.w(str17, str18, e3);
                                        }
                                    }
                                    return i12;
                                }
                                String str21 = str13;
                                if (i14 == -1 || i12 == 0) {
                                    i12 = i14;
                                }
                                i11 = i13 + 1;
                                str15 = str9;
                                str13 = str21;
                                str12 = str18;
                                str10 = str17;
                                zArr2 = zArr3;
                                i5 = i15;
                                length = i16;
                                allCodePaths = list2;
                                dexCodeInstructionSets = strArr4;
                                i10 = i17;
                            } catch (Throwable th8) {
                                th = th8;
                                packageDexOptimizer2 = this;
                                str8 = str13;
                                str2 = str17;
                                str3 = str18;
                                th = th;
                                str4 = str8;
                                if (str4 != null) {
                                }
                                throw th;
                            }
                        }
                        packageDexOptimizer = this;
                        packageStats2 = packageStats;
                        int i18 = i12;
                        zArr = zArr2;
                        i2 = i5;
                        str = str10;
                        list = allCodePaths;
                        strArr3 = dexCodeInstructionSets;
                        String str22 = str12;
                        i = i10;
                        String str23 = str13;
                        c = 65535;
                        if (str23 != null) {
                            try {
                                packageDexOptimizer.mInstaller.deleteReferenceProfile(androidPackage.getPackageName(), str23);
                            } catch (Installer.InstallerException e4) {
                                Slog.w(str, str22, e4);
                            }
                        }
                        i7 = i18;
                        androidPackage2 = androidPackage;
                        packageStats3 = packageStats2;
                        packageDexOptimizer3 = packageDexOptimizer;
                        str10 = str;
                        classLoaderContexts = strArr2;
                        zArr2 = zArr;
                        i5 = i2;
                        allCodePaths = list;
                        dexCodeInstructionSets = strArr3;
                        c2 = c;
                        i8 = i + 1;
                    } catch (Throwable th9) {
                        th = th9;
                        packageDexOptimizer2 = this;
                        str2 = str10;
                        str3 = "Failed to cleanup cloud profile";
                        str8 = str13;
                    }
                }
            }
            i = i8;
            strArr2 = classLoaderContexts;
            zArr = zArr2;
            i2 = i5;
            c = c2;
            list = allCodePaths;
            strArr3 = dexCodeInstructionSets;
            packageStats2 = packageStats3;
            packageDexOptimizer = packageDexOptimizer3;
            str = str10;
            androidPackage2 = androidPackage;
            packageStats3 = packageStats2;
            packageDexOptimizer3 = packageDexOptimizer;
            str10 = str;
            classLoaderContexts = strArr2;
            zArr2 = zArr;
            i5 = i2;
            allCodePaths = list;
            dexCodeInstructionSets = strArr3;
            c2 = c;
            i8 = i + 1;
        }
        return i7;
    }

    @GuardedBy({"mInstallLock"})
    public final boolean prepareCloudProfile(AndroidPackage androidPackage, String str, String str2, String str3) throws Installer.LegacyDexoptDisabledException {
        if (str3 != null) {
            if (this.mInstaller.isIsolated()) {
                return true;
            }
            try {
                this.mInstaller.deleteReferenceProfile(androidPackage.getPackageName(), str);
                this.mInstaller.prepareAppProfile(androidPackage.getPackageName(), -10000, UserHandle.getAppId(androidPackage.getUid()), str, str2, str3);
                return true;
            } catch (Installer.InstallerException e) {
                Slog.w("PackageDexOptimizer", "Failed to prepare cloud profile", e);
            }
        }
        return false;
    }

    @GuardedBy({"mInstallLock"})
    public final int dexOptPath(AndroidPackage androidPackage, PackageStateInternal packageStateInternal, String str, String str2, String str3, int i, String str4, int i2, int i3, CompilerStats.PackageStats packageStats, boolean z, String str5, String str6, int i4) throws Installer.LegacyDexoptDisabledException {
        String str7;
        String packageOatDirIfSupported = getPackageOatDirIfSupported(packageStateInternal, androidPackage);
        int dexoptNeeded = getDexoptNeeded(androidPackage.getPackageName(), str, str2, str3, str4, i, z, i2, packageOatDirIfSupported);
        if (Math.abs(dexoptNeeded) == 0) {
            return 0;
        }
        Log.i("PackageDexOptimizer", "Running dexopt (dexoptNeeded=" + dexoptNeeded + ") on: " + str + " pkg=" + androidPackage.getPackageName() + " isa=" + str2 + " dexoptFlags=" + printDexoptFlags(i2) + " targetFilter=" + str3 + " oatDir=" + packageOatDirIfSupported + " classLoaderContext=" + str4);
        try {
            long currentTimeMillis = System.currentTimeMillis();
            str7 = "PackageDexOptimizer";
            try {
                if (getInstallerLI().dexopt(str, i3, androidPackage.getPackageName(), str2, dexoptNeeded, packageOatDirIfSupported, i2, str3, androidPackage.getVolumeUuid(), str4, packageStateInternal.getSeInfo(), false, androidPackage.getTargetSdkVersion(), str5, str6, getAugmentedReasonName(i4, str6 != null))) {
                    if (packageStats != null) {
                        packageStats.setCompileTime(str, (int) (System.currentTimeMillis() - currentTimeMillis));
                    }
                    if (packageOatDirIfSupported != null) {
                        F2fsUtils.releaseCompressedBlocks(this.mContext.getContentResolver(), new File(packageOatDirIfSupported));
                    }
                    return 1;
                }
                return 2;
            } catch (Installer.InstallerException e) {
                e = e;
                Slog.w(str7, "Failed to dexopt", e);
                return -1;
            }
        } catch (Installer.InstallerException e2) {
            e = e2;
            str7 = "PackageDexOptimizer";
        }
    }

    public final String getAugmentedReasonName(int i, boolean z) {
        String str = z ? "-dm" : "";
        return PackageManagerServiceCompilerMapping.getReasonName(i) + str;
    }

    public int dexOptSecondaryDexPath(ApplicationInfo applicationInfo, String str, PackageDexUsage.DexUseInfo dexUseInfo, DexoptOptions dexoptOptions) throws Installer.LegacyDexoptDisabledException {
        int dexOptSecondaryDexPathLI;
        if (applicationInfo.uid == -1) {
            throw new IllegalArgumentException("Dexopt for path " + str + " has invalid uid.");
        }
        synchronized (this.mInstallLock) {
            long acquireWakeLockLI = acquireWakeLockLI(applicationInfo.uid);
            dexOptSecondaryDexPathLI = dexOptSecondaryDexPathLI(applicationInfo, str, dexUseInfo, dexoptOptions);
            releaseWakeLockLI(acquireWakeLockLI);
        }
        return dexOptSecondaryDexPathLI;
    }

    @GuardedBy({"mInstallLock"})
    public final long acquireWakeLockLI(int i) {
        if (this.mSystemReady) {
            this.mDexoptWakeLock.setWorkSource(new WorkSource(i));
            this.mDexoptWakeLock.acquire(660000L);
            return SystemClock.elapsedRealtime();
        }
        return -1L;
    }

    @GuardedBy({"mInstallLock"})
    public final void releaseWakeLockLI(long j) {
        if (j < 0) {
            return;
        }
        try {
            if (this.mDexoptWakeLock.isHeld()) {
                this.mDexoptWakeLock.release();
            }
            long elapsedRealtime = SystemClock.elapsedRealtime() - j;
            if (elapsedRealtime >= 660000) {
                Slog.wtf("PackageDexOptimizer", "WakeLock " + this.mDexoptWakeLock.getTag() + " time out. Operation took " + elapsedRealtime + " ms. Thread: " + Thread.currentThread().getName());
            }
        } catch (RuntimeException e) {
            Slog.wtf("PackageDexOptimizer", "Error while releasing " + this.mDexoptWakeLock.getTag() + " lock", e);
        }
    }

    @GuardedBy({"mInstallLock"})
    public final int dexOptSecondaryDexPathLI(ApplicationInfo applicationInfo, String str, PackageDexUsage.DexUseInfo dexUseInfo, DexoptOptions dexoptOptions) throws Installer.LegacyDexoptDisabledException {
        int i;
        String str2;
        String str3;
        String realCompilerFilter = getRealCompilerFilter(applicationInfo, dexoptOptions.getCompilerFilter(), dexUseInfo.isUsedByOtherApps());
        int dexFlags = getDexFlags(applicationInfo, realCompilerFilter, dexoptOptions) | 32;
        String str4 = applicationInfo.deviceProtectedDataDir;
        String str5 = "PackageDexOptimizer";
        if (str4 == null || !FileUtils.contains(str4, str)) {
            String str6 = applicationInfo.credentialProtectedDataDir;
            if (str6 == null || !FileUtils.contains(str6, str)) {
                Slog.e("PackageDexOptimizer", "Could not infer CE/DE storage for package " + applicationInfo.packageName);
                return -1;
            }
            i = dexFlags | 128;
        } else {
            i = dexFlags | 256;
        }
        int i2 = i;
        if (dexUseInfo.isUnsupportedClassLoaderContext() || dexUseInfo.isVariableClassLoaderContext()) {
            str2 = null;
            realCompilerFilter = "verify";
        } else {
            str2 = dexUseInfo.getClassLoaderContext();
        }
        String str7 = realCompilerFilter;
        String str8 = str2;
        int compilationReason = dexoptOptions.getCompilationReason();
        Log.d("PackageDexOptimizer", "Running dexopt on: " + str + " pkg=" + applicationInfo.packageName + " isa=" + dexUseInfo.getLoaderIsas() + " reason=" + PackageManagerServiceCompilerMapping.getReasonName(compilationReason) + " dexoptFlags=" + printDexoptFlags(i2) + " target-filter=" + str7 + " class-loader-context=" + str8);
        try {
            for (String str9 : dexUseInfo.getLoaderIsas()) {
                String str10 = str8;
                String str11 = str7;
                int i3 = i2;
                str3 = str5;
                try {
                    if (!getInstallerLI().dexopt(str, applicationInfo.uid, applicationInfo.packageName, str9, 0, null, i2, str7, applicationInfo.volumeUuid, str10, applicationInfo.seInfo, dexoptOptions.isDowngrade(), applicationInfo.targetSdkVersion, null, null, PackageManagerServiceCompilerMapping.getReasonName(compilationReason))) {
                        return 2;
                    }
                    i2 = i3;
                    str8 = str10;
                    str7 = str11;
                    str5 = str3;
                } catch (Installer.InstallerException e) {
                    e = e;
                    Slog.w(str3, "Failed to dexopt", e);
                    return -1;
                }
            }
            return 1;
        } catch (Installer.InstallerException e2) {
            e = e2;
            str3 = str5;
        }
    }

    public void dumpDexoptState(IndentingPrintWriter indentingPrintWriter, AndroidPackage androidPackage, PackageStateInternal packageStateInternal, PackageDexUsage.PackageUseInfo packageUseInfo) throws Installer.LegacyDexoptDisabledException {
        String[] dexCodeInstructionSets = InstructionSets.getDexCodeInstructionSets(InstructionSets.getAppDexInstructionSets(packageStateInternal.getPrimaryCpuAbi(), packageStateInternal.getSecondaryCpuAbi()));
        for (String str : AndroidPackageUtils.getAllCodePathsExcludingResourceOnly(androidPackage)) {
            indentingPrintWriter.println("path: " + str);
            indentingPrintWriter.increaseIndent();
            for (String str2 : dexCodeInstructionSets) {
                try {
                    DexFile.OptimizationInfo dexFileOptimizationInfo = DexFile.getDexFileOptimizationInfo(str, str2);
                    indentingPrintWriter.println(str2 + ": [status=" + dexFileOptimizationInfo.getStatus() + "] [reason=" + dexFileOptimizationInfo.getReason() + "]");
                } catch (IOException e) {
                    indentingPrintWriter.println(str2 + ": [Exception]: " + e.getMessage());
                }
            }
            if (packageUseInfo.isUsedByOtherApps(str)) {
                indentingPrintWriter.println("used by other apps: " + packageUseInfo.getLoadingPackages(str));
            }
            Map<String, PackageDexUsage.DexUseInfo> dexUseInfoMap = packageUseInfo.getDexUseInfoMap();
            if (!dexUseInfoMap.isEmpty()) {
                indentingPrintWriter.println("known secondary dex files:");
                indentingPrintWriter.increaseIndent();
                for (Map.Entry<String, PackageDexUsage.DexUseInfo> entry : dexUseInfoMap.entrySet()) {
                    PackageDexUsage.DexUseInfo value = entry.getValue();
                    indentingPrintWriter.println(entry.getKey());
                    indentingPrintWriter.increaseIndent();
                    indentingPrintWriter.println("class loader context: " + value.getClassLoaderContext());
                    if (value.isUsedByOtherApps()) {
                        indentingPrintWriter.println("used by other apps: " + value.getLoadingPackages());
                    }
                    indentingPrintWriter.decreaseIndent();
                }
                indentingPrintWriter.decreaseIndent();
            }
            indentingPrintWriter.decreaseIndent();
        }
    }

    public final String getRealCompilerFilter(ApplicationInfo applicationInfo, String str, boolean z) {
        if (applicationInfo.isEmbeddedDexUsed()) {
            return DexFile.isOptimizedCompilerFilter(str) ? "verify" : str;
        }
        int i = applicationInfo.flags;
        if (((i & 16384) == 0 && (i & 2) == 0) ? false : true) {
            return DexFile.getSafeModeCompilerFilter(str);
        }
        return (DexFile.isProfileGuidedCompilerFilter(str) && z) ? PackageManagerServiceCompilerMapping.getCompilerFilterForReason(14) : str;
    }

    public final String getRealCompilerFilter(AndroidPackage androidPackage, String str) {
        if (androidPackage.isUseEmbeddedDex()) {
            return DexFile.isOptimizedCompilerFilter(str) ? "verify" : str;
        }
        return androidPackage.isVmSafeMode() || androidPackage.isDebuggable() ? DexFile.getSafeModeCompilerFilter(str) : str;
    }

    public final boolean isAppImageEnabled() {
        return SystemProperties.get("dalvik.vm.appimageformat", "").length() > 0;
    }

    public final int getDexFlags(ApplicationInfo applicationInfo, String str, DexoptOptions dexoptOptions) {
        return getDexFlags((applicationInfo.flags & 2) != 0, applicationInfo.getHiddenApiEnforcementPolicy(), applicationInfo.splitDependencies, applicationInfo.requestsIsolatedSplitLoading(), str, false, dexoptOptions);
    }

    public final int getDexFlags(AndroidPackage androidPackage, PackageStateInternal packageStateInternal, String str, boolean z, DexoptOptions dexoptOptions) {
        return getDexFlags(androidPackage.isDebuggable(), AndroidPackageUtils.getHiddenApiEnforcementPolicy(androidPackage, packageStateInternal), androidPackage.getSplitDependencies(), androidPackage.isIsolatedSplitLoading(), str, z, dexoptOptions);
    }

    public final int getDexFlags(boolean z, int i, SparseArray<int[]> sparseArray, boolean z2, String str, boolean z3, DexoptOptions dexoptOptions) {
        boolean isProfileGuidedCompilerFilter = DexFile.isProfileGuidedCompilerFilter(str);
        boolean z4 = true;
        boolean z5 = !isProfileGuidedCompilerFilter || dexoptOptions.isDexoptInstallWithDexMetadata() || z3;
        int i2 = isProfileGuidedCompilerFilter ? 16 : 0;
        int i3 = i == 0 ? 0 : 1024;
        int compilationReason = dexoptOptions.getCompilationReason();
        boolean z6 = (compilationReason == 0 || compilationReason == 1 || compilationReason == 2 || compilationReason == 3) ? false : true;
        if (!isProfileGuidedCompilerFilter || ((sparseArray != null && z2) || !isAppImageEnabled())) {
            z4 = false;
        }
        return adjustDexoptFlags((z ? 4 : 0) | (z5 ? 2 : 0) | i2 | (dexoptOptions.isBootComplete() ? 8 : 0) | (dexoptOptions.isDexoptIdleBackgroundJob() ? 512 : 0) | (z6 ? IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES : 0) | (z4 ? IInstalld.FLAG_USE_QUOTA : 0) | (dexoptOptions.isDexoptInstallForRestore() ? IInstalld.FLAG_FORCE : 0) | i3);
    }

    @GuardedBy({"mInstallLock"})
    public final int getDexoptNeeded(String str, String str2, String str3, String str4, String str5, int i, boolean z, int i2, String str6) throws Installer.LegacyDexoptDisabledException {
        boolean z2;
        if (!this.mInstaller.isIsolated()) {
            Installer.checkLegacyDexoptDisabled();
        }
        boolean z3 = (i2 & 2) != 0;
        boolean z4 = (i2 & 16) != 0;
        boolean z5 = i == 1;
        try {
            if (!z5 && z4 && z3) {
                if (isOdexPrivate(str, str2, str3, str6)) {
                    z2 = true;
                    return adjustDexoptNeeded(DexFile.getDexOptNeeded(str2, str3, (compilerFilterDependsOnProfiles(str4) || i != 3) ? str4 : "verify", str5, z2, z));
                }
            }
            return adjustDexoptNeeded(DexFile.getDexOptNeeded(str2, str3, (compilerFilterDependsOnProfiles(str4) || i != 3) ? str4 : "verify", str5, z2, z));
        } catch (IOException e) {
            Slog.w("PackageDexOptimizer", "IOException reading apk: " + str2, e);
            return -1;
        } catch (RuntimeException e2) {
            Slog.wtf("PackageDexOptimizer", "Unexpected exception when calling dexoptNeeded on " + str2, e2);
            return -1;
        }
        z2 = z5;
    }

    public final boolean compilerFilterDependsOnProfiles(String str) {
        return str.endsWith("-profile");
    }

    @GuardedBy({"mInstallLock"})
    public final boolean isOdexPrivate(String str, String str2, String str3, String str4) throws Installer.LegacyDexoptDisabledException {
        try {
            return this.mInstaller.getOdexVisibility(str, str2, str3, str4) == 2;
        } catch (Installer.InstallerException e) {
            Slog.w("PackageDexOptimizer", "Failed to get odex visibility for " + str2, e);
            return false;
        }
    }

    public final int analyseProfiles(AndroidPackage androidPackage, int i, String str, String str2) throws Installer.LegacyDexoptDisabledException {
        int mergeProfiles;
        Installer.checkLegacyDexoptDisabled();
        if (DexFile.isProfileGuidedCompilerFilter(str2)) {
            try {
                synchronized (this.mInstallLock) {
                    mergeProfiles = getInstallerLI().mergeProfiles(i, androidPackage.getPackageName(), str);
                }
                return mergeProfiles;
            } catch (Installer.InstallerException e) {
                Slog.w("PackageDexOptimizer", "Failed to merge profiles", e);
                return 2;
            }
        }
        return 2;
    }

    public final String getPackageOatDirIfSupported(PackageState packageState, AndroidPackage androidPackage) {
        if (AndroidPackageUtils.canHaveOatDir(packageState, androidPackage)) {
            File file = new File(androidPackage.getPath());
            if (file.isDirectory()) {
                return getOatDir(file).getAbsolutePath();
            }
            return null;
        }
        return null;
    }

    public static File getOatDir(File file) {
        return new File(file, "oat");
    }

    public void systemReady() {
        this.mSystemReady = true;
    }

    public final String printDexoptFlags(int i) {
        ArrayList arrayList = new ArrayList();
        if ((i & 8) == 8) {
            arrayList.add("boot_complete");
        }
        if ((i & 4) == 4) {
            arrayList.add("debuggable");
        }
        if ((i & 16) == 16) {
            arrayList.add("profile_guided");
        }
        if ((i & 2) == 2) {
            arrayList.add("public");
        }
        if ((i & 32) == 32) {
            arrayList.add("secondary");
        }
        if ((i & 64) == 64) {
            arrayList.add("force");
        }
        if ((i & 128) == 128) {
            arrayList.add("storage_ce");
        }
        if ((i & 256) == 256) {
            arrayList.add("storage_de");
        }
        if ((i & 512) == 512) {
            arrayList.add("idle_background_job");
        }
        if ((i & 1024) == 1024) {
            arrayList.add("enable_hidden_api_checks");
        }
        return String.join(",", arrayList);
    }

    /* renamed from: com.android.server.pm.PackageDexOptimizer$ForcedUpdatePackageDexOptimizer */
    /* loaded from: classes2.dex */
    public static class ForcedUpdatePackageDexOptimizer extends PackageDexOptimizer {
        @Override // com.android.server.p011pm.PackageDexOptimizer
        public int adjustDexoptFlags(int i) {
            return i | 64;
        }

        @Override // com.android.server.p011pm.PackageDexOptimizer
        public int adjustDexoptNeeded(int i) {
            if (i == 0) {
                return -3;
            }
            return i;
        }

        public ForcedUpdatePackageDexOptimizer(Installer installer, Object obj, Context context, String str) {
            super(installer, obj, context, str);
        }

        public ForcedUpdatePackageDexOptimizer(PackageDexOptimizer packageDexOptimizer) {
            super(packageDexOptimizer);
        }
    }

    @GuardedBy({"mInstallLock"})
    public final Installer getInstallerLI() {
        return this.mInstaller;
    }

    public final Installer getInstallerWithoutLock() {
        return this.mInstaller;
    }
}
