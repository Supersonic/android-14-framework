package com.android.server.p011pm;

import android.content.Context;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.IOtaDexopt;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ShellCallback;
import android.os.storage.StorageManager;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import com.android.internal.logging.MetricsLogger;
import com.android.server.LocalServices;
import com.android.server.p011pm.Installer;
import com.android.server.p011pm.PackageDexOptimizer;
import com.android.server.p011pm.dex.DexoptOptions;
import com.android.server.p011pm.parsing.pkg.AndroidPackageInternal;
import com.android.server.p011pm.parsing.pkg.AndroidPackageUtils;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageStateInternal;
import java.io.File;
import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
/* renamed from: com.android.server.pm.OtaDexoptService */
/* loaded from: classes2.dex */
public class OtaDexoptService extends IOtaDexopt.Stub {
    public long availableSpaceAfterBulkDelete;
    public long availableSpaceAfterDexopt;
    public long availableSpaceBefore;
    public int completeSize;
    public int dexoptCommandCountExecuted;
    public int dexoptCommandCountTotal;
    public int importantPackageCount;
    public final Context mContext;
    public List<String> mDexoptCommands;
    public final PackageManagerService mPackageManagerService;
    public final MetricsLogger metricsLogger = new MetricsLogger();
    public long otaDexoptTimeStart;
    public int otherPackageCount;

    public OtaDexoptService(Context context, PackageManagerService packageManagerService) {
        this.mContext = context;
        this.mPackageManagerService = packageManagerService;
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.android.server.pm.OtaDexoptService, android.os.IBinder] */
    public static OtaDexoptService main(Context context, PackageManagerService packageManagerService) {
        ?? otaDexoptService = new OtaDexoptService(context, packageManagerService);
        ServiceManager.addService("otadexopt", (IBinder) otaDexoptService);
        otaDexoptService.moveAbArtifacts(packageManagerService.mInstaller);
        return otaDexoptService;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
        new OtaDexoptShellCommand(this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
    }

    public synchronized void prepare() throws RemoteException {
        if (this.mDexoptCommands != null) {
            throw new IllegalStateException("already called prepare()");
        }
        Predicate<? super PackageStateInternal> predicate = new Predicate() { // from class: com.android.server.pm.OtaDexoptService$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$prepare$0;
                lambda$prepare$0 = OtaDexoptService.lambda$prepare$0((PackageStateInternal) obj);
                return lambda$prepare$0;
            }
        };
        Computer snapshotComputer = this.mPackageManagerService.snapshotComputer();
        Collection<? extends PackageStateInternal> values = snapshotComputer.getPackageStates().values();
        List<PackageStateInternal> packagesForDexopt = DexOptHelper.getPackagesForDexopt(values, this.mPackageManagerService, true);
        packagesForDexopt.removeIf(predicate);
        ArrayList<PackageStateInternal> arrayList = new ArrayList(values);
        arrayList.removeAll(packagesForDexopt);
        arrayList.removeIf(PackageManagerServiceUtils.REMOVE_IF_NULL_PKG);
        arrayList.removeIf(PackageManagerServiceUtils.REMOVE_IF_APEX_PKG);
        arrayList.removeIf(predicate);
        this.mDexoptCommands = new ArrayList((values.size() * 3) / 2);
        for (PackageStateInternal packageStateInternal : packagesForDexopt) {
            this.mDexoptCommands.addAll(generatePackageDexopts(packageStateInternal.getPkg(), packageStateInternal, 10));
        }
        for (PackageStateInternal packageStateInternal2 : arrayList) {
            if (packageStateInternal2.getPkg().isCoreApp()) {
                throw new IllegalStateException("Found a core app that's not important");
            }
            this.mDexoptCommands.addAll(generatePackageDexopts(packageStateInternal2.getPkg(), packageStateInternal2, 0));
        }
        this.completeSize = this.mDexoptCommands.size();
        long availableSpace = getAvailableSpace();
        if (availableSpace < 1073741824) {
            Log.i("OTADexopt", "Low on space, deleting oat files in an attempt to free up space: " + DexOptHelper.packagesToString(arrayList));
            for (PackageStateInternal packageStateInternal3 : arrayList) {
                this.mPackageManagerService.deleteOatArtifactsOfPackage(snapshotComputer, packageStateInternal3.getPackageName());
            }
        }
        prepareMetricsLogging(packagesForDexopt.size(), arrayList.size(), availableSpace, getAvailableSpace());
        try {
            Log.d("OTADexopt", "A/B OTA: lastUsed time = " + ((PackageStateInternal) Collections.max(packagesForDexopt, Comparator.comparingLong(new ToLongFunction() { // from class: com.android.server.pm.OtaDexoptService$$ExternalSyntheticLambda1
                @Override // java.util.function.ToLongFunction
                public final long applyAsLong(Object obj) {
                    long lambda$prepare$1;
                    lambda$prepare$1 = OtaDexoptService.lambda$prepare$1((PackageStateInternal) obj);
                    return lambda$prepare$1;
                }
            }))).getTransientState().getLatestForegroundPackageUseTimeInMills());
            Log.d("OTADexopt", "A/B OTA: deprioritized packages:");
            for (PackageStateInternal packageStateInternal4 : arrayList) {
                Log.d("OTADexopt", "  " + packageStateInternal4.getPackageName() + " - " + packageStateInternal4.getTransientState().getLatestForegroundPackageUseTimeInMills());
            }
        } catch (RuntimeException unused) {
        }
    }

    public static /* synthetic */ boolean lambda$prepare$0(PackageStateInternal packageStateInternal) {
        return PackageManagerShellCommandDataLoader.PACKAGE.equals(packageStateInternal.getPkg().getPackageName());
    }

    public static /* synthetic */ long lambda$prepare$1(PackageStateInternal packageStateInternal) {
        return packageStateInternal.getTransientState().getLatestForegroundPackageUseTimeInMills();
    }

    public synchronized void cleanup() throws RemoteException {
        Log.i("OTADexopt", "Cleaning up OTA Dexopt state.");
        this.mDexoptCommands = null;
        this.availableSpaceAfterDexopt = getAvailableSpace();
        performMetricsLogging();
    }

    public synchronized boolean isDone() throws RemoteException {
        List<String> list;
        list = this.mDexoptCommands;
        if (list == null) {
            throw new IllegalStateException("done() called before prepare()");
        }
        return list.isEmpty();
    }

    public synchronized float getProgress() throws RemoteException {
        if (this.completeSize == 0) {
            return 1.0f;
        }
        int size = this.mDexoptCommands.size();
        int i = this.completeSize;
        return (i - size) / i;
    }

    public synchronized String nextDexoptCommand() throws RemoteException {
        List<String> list = this.mDexoptCommands;
        if (list == null) {
            throw new IllegalStateException("dexoptNextPackage() called before prepare()");
        }
        if (list.isEmpty()) {
            return "(all done)";
        }
        String remove = this.mDexoptCommands.remove(0);
        if (getAvailableSpace() > 0) {
            this.dexoptCommandCountExecuted++;
            Log.d("OTADexopt", "Next command: " + remove);
            return remove;
        }
        Log.w("OTADexopt", "Not enough space for OTA dexopt, stopping with " + (this.mDexoptCommands.size() + 1) + " commands left.");
        this.mDexoptCommands.clear();
        return "(no free space)";
    }

    public final long getMainLowSpaceThreshold() {
        long storageLowBytes = StorageManager.from(this.mContext).getStorageLowBytes(Environment.getDataDirectory());
        if (storageLowBytes != 0) {
            return storageLowBytes;
        }
        throw new IllegalStateException("Invalid low memory threshold");
    }

    public final long getAvailableSpace() {
        return Environment.getDataDirectory().getUsableSpace() - getMainLowSpaceThreshold();
    }

    public final synchronized List<String> generatePackageDexopts(AndroidPackage androidPackage, PackageStateInternal packageStateInternal, int i) {
        final ArrayList arrayList;
        arrayList = new ArrayList();
        try {
            new OTADexoptPackageDexOptimizer(new Installer(this.mContext, true) { // from class: com.android.server.pm.OtaDexoptService.1
                @Override // com.android.server.p011pm.Installer
                public boolean dexopt(String str, int i2, String str2, String str3, int i3, String str4, int i4, String str5, String str6, String str7, String str8, boolean z, int i5, String str9, String str10, String str11) throws Installer.InstallerException {
                    StringBuilder sb = new StringBuilder();
                    if (DexOptHelper.useArtService() && (i4 & 32) != 0) {
                        throw new IllegalArgumentException("Invalid OTA dexopt call for secondary dex");
                    }
                    sb.append("10 ");
                    sb.append("dexopt");
                    encodeParameter(sb, str);
                    encodeParameter(sb, Integer.valueOf(i2));
                    encodeParameter(sb, str2);
                    encodeParameter(sb, str3);
                    encodeParameter(sb, Integer.valueOf(i3));
                    encodeParameter(sb, str4);
                    encodeParameter(sb, Integer.valueOf(i4));
                    encodeParameter(sb, str5);
                    encodeParameter(sb, str6);
                    encodeParameter(sb, str7);
                    encodeParameter(sb, str8);
                    encodeParameter(sb, Boolean.valueOf(z));
                    encodeParameter(sb, Integer.valueOf(i5));
                    encodeParameter(sb, str9);
                    encodeParameter(sb, str10);
                    encodeParameter(sb, str11);
                    arrayList.add(sb.toString());
                    return true;
                }

                public final void encodeParameter(StringBuilder sb, Object obj) {
                    sb.append(' ');
                    if (obj == null) {
                        sb.append('!');
                        return;
                    }
                    String valueOf = String.valueOf(obj);
                    if (valueOf.indexOf(0) != -1 || valueOf.indexOf(32) != -1 || "!".equals(valueOf)) {
                        throw new IllegalArgumentException("Invalid argument while executing " + obj);
                    }
                    sb.append(valueOf);
                }
            }, this.mPackageManagerService.mInstallLock, this.mContext).performDexOpt(androidPackage, packageStateInternal, null, null, this.mPackageManagerService.getDexManager().getPackageUseInfoOrDefault(androidPackage.getPackageName()), new DexoptOptions(androidPackage.getPackageName(), i, 4));
        } catch (Installer.LegacyDexoptDisabledException e) {
            Slog.wtf("OTADexopt", e);
        }
        return arrayList;
    }

    public synchronized void dexoptNextPackage() throws RemoteException {
        throw new UnsupportedOperationException();
    }

    public final void moveAbArtifacts(Installer installer) {
        OtaDexoptService otaDexoptService = this;
        if (otaDexoptService.mDexoptCommands != null) {
            throw new IllegalStateException("Should not be ota-dexopting when trying to move.");
        }
        if (!otaDexoptService.mPackageManagerService.isDeviceUpgrading()) {
            Slog.d("OTADexopt", "No upgrade, skipping A/B artifacts check.");
            return;
        }
        ArrayMap<String, ? extends PackageStateInternal> packageStates = ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).getPackageStates();
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        while (i < packageStates.size()) {
            PackageStateInternal valueAt = packageStates.valueAt(i);
            AndroidPackageInternal pkg = valueAt.getPkg();
            if (pkg != null && otaDexoptService.mPackageManagerService.mPackageDexOptimizer.canOptimizePackage(pkg)) {
                if (pkg.getPath() == null) {
                    Slog.w("OTADexopt", "Package " + pkg + " can be optimized but has null codePath");
                } else if (!pkg.getPath().startsWith("/system") && !pkg.getPath().startsWith("/vendor") && !pkg.getPath().startsWith("/product") && !pkg.getPath().startsWith("/system_ext")) {
                    String[] appDexInstructionSets = InstructionSets.getAppDexInstructionSets(valueAt.getPrimaryCpuAbi(), valueAt.getSecondaryCpuAbi());
                    List<String> allCodePathsExcludingResourceOnly = AndroidPackageUtils.getAllCodePathsExcludingResourceOnly(pkg);
                    String[] dexCodeInstructionSets = InstructionSets.getDexCodeInstructionSets(appDexInstructionSets);
                    String packageName = pkg.getPackageName();
                    for (String str : dexCodeInstructionSets) {
                        for (String str2 : allCodePathsExcludingResourceOnly) {
                            i3++;
                            try {
                                installer.moveAb(packageName, str2, str, PackageDexOptimizer.getOatDir(new File(pkg.getPath())).getAbsolutePath());
                                i2++;
                            } catch (Installer.InstallerException unused) {
                            }
                        }
                    }
                }
            }
            i++;
            otaDexoptService = this;
        }
        Slog.i("OTADexopt", "Moved " + i2 + "/" + i3);
    }

    public final void prepareMetricsLogging(int i, int i2, long j, long j2) {
        this.availableSpaceBefore = j;
        this.availableSpaceAfterBulkDelete = j2;
        this.availableSpaceAfterDexopt = 0L;
        this.importantPackageCount = i;
        this.otherPackageCount = i2;
        this.dexoptCommandCountTotal = this.mDexoptCommands.size();
        this.dexoptCommandCountExecuted = 0;
        this.otaDexoptTimeStart = System.nanoTime();
    }

    public static int inMegabytes(long j) {
        long j2 = j / 1048576;
        if (j2 > 2147483647L) {
            Log.w("OTADexopt", "Recording " + j2 + "MB of free space, overflowing range");
            return Integer.MAX_VALUE;
        }
        return (int) j2;
    }

    public final void performMetricsLogging() {
        long nanoTime = System.nanoTime();
        this.metricsLogger.histogram("ota_dexopt_available_space_before_mb", inMegabytes(this.availableSpaceBefore));
        this.metricsLogger.histogram("ota_dexopt_available_space_after_bulk_delete_mb", inMegabytes(this.availableSpaceAfterBulkDelete));
        this.metricsLogger.histogram("ota_dexopt_available_space_after_dexopt_mb", inMegabytes(this.availableSpaceAfterDexopt));
        this.metricsLogger.histogram("ota_dexopt_num_important_packages", this.importantPackageCount);
        this.metricsLogger.histogram("ota_dexopt_num_other_packages", this.otherPackageCount);
        this.metricsLogger.histogram("ota_dexopt_num_commands", this.dexoptCommandCountTotal);
        this.metricsLogger.histogram("ota_dexopt_num_commands_executed", this.dexoptCommandCountExecuted);
        this.metricsLogger.histogram("ota_dexopt_time_s", (int) TimeUnit.NANOSECONDS.toSeconds(nanoTime - this.otaDexoptTimeStart));
    }

    /* renamed from: com.android.server.pm.OtaDexoptService$OTADexoptPackageDexOptimizer */
    /* loaded from: classes2.dex */
    public static class OTADexoptPackageDexOptimizer extends PackageDexOptimizer.ForcedUpdatePackageDexOptimizer {
        public OTADexoptPackageDexOptimizer(Installer installer, Object obj, Context context) {
            super(installer, obj, context, "*otadexopt*");
        }
    }
}
