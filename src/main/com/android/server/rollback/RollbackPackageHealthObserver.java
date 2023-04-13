package com.android.server.rollback;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.VersionedPackage;
import android.content.rollback.PackageRollbackInfo;
import android.content.rollback.RollbackInfo;
import android.content.rollback.RollbackManager;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.Preconditions;
import com.android.server.PackageWatchdog;
import com.android.server.SystemConfig;
import com.android.server.p011pm.ApexManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public final class RollbackPackageHealthObserver implements PackageWatchdog.PackageHealthObserver {
    public final ApexManager mApexManager;
    public final Context mContext;
    public final Handler mHandler;
    public final File mLastStagedRollbackIdsFile;
    public final Set<Integer> mPendingStagedRollbackIds = new ArraySet();
    public boolean mTwoPhaseRollbackEnabled;
    public final File mTwoPhaseRollbackEnabledFile;

    @Override // com.android.server.PackageWatchdog.PackageHealthObserver
    public String getName() {
        return "rollback-observer";
    }

    public RollbackPackageHealthObserver(Context context) {
        this.mContext = context;
        HandlerThread handlerThread = new HandlerThread("RollbackPackageHealthObserver");
        handlerThread.start();
        this.mHandler = new Handler(handlerThread.getLooper());
        File file = new File(Environment.getDataDirectory(), "rollback-observer");
        file.mkdirs();
        this.mLastStagedRollbackIdsFile = new File(file, "last-staged-rollback-ids");
        File file2 = new File(file, "two-phase-rollback-enabled");
        this.mTwoPhaseRollbackEnabledFile = file2;
        PackageWatchdog.getInstance(context).registerHealthObserver(this);
        this.mApexManager = ApexManager.getInstance();
        if (SystemProperties.getBoolean("sys.boot_completed", false)) {
            this.mTwoPhaseRollbackEnabled = readBoolean(file2);
            return;
        }
        this.mTwoPhaseRollbackEnabled = false;
        writeBoolean(file2, false);
    }

    @Override // com.android.server.PackageWatchdog.PackageHealthObserver
    public int onHealthCheckFailed(VersionedPackage versionedPackage, int i, int i2) {
        return ((i != 1 || ((RollbackManager) this.mContext.getSystemService(RollbackManager.class)).getAvailableRollbacks().isEmpty()) && getAvailableRollback(versionedPackage) == null) ? 0 : 3;
    }

    @Override // com.android.server.PackageWatchdog.PackageHealthObserver
    public boolean execute(final VersionedPackage versionedPackage, final int i, int i2) {
        if (i == 1) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.rollback.RollbackPackageHealthObserver$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    RollbackPackageHealthObserver.this.lambda$execute$0();
                }
            });
            return true;
        }
        final RollbackInfo availableRollback = getAvailableRollback(versionedPackage);
        if (availableRollback == null) {
            Slog.w("RollbackPackageHealthObserver", "Expected rollback but no valid rollback found for " + versionedPackage);
            return false;
        }
        this.mHandler.post(new Runnable() { // from class: com.android.server.rollback.RollbackPackageHealthObserver$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                RollbackPackageHealthObserver.this.lambda$execute$1(availableRollback, versionedPackage, i);
            }
        });
        return true;
    }

    public final void assertInWorkerThread() {
        Preconditions.checkState(this.mHandler.getLooper().isCurrentThread());
    }

    public void startObservingHealth(List<String> list, long j) {
        PackageWatchdog.getInstance(this.mContext).startObservingHealth(this, list, j);
    }

    public void notifyRollbackAvailable(final RollbackInfo rollbackInfo) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.rollback.RollbackPackageHealthObserver$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                RollbackPackageHealthObserver.this.lambda$notifyRollbackAvailable$2(rollbackInfo);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyRollbackAvailable$2(RollbackInfo rollbackInfo) {
        if (isRebootlessApex(rollbackInfo)) {
            this.mTwoPhaseRollbackEnabled = true;
            writeBoolean(this.mTwoPhaseRollbackEnabledFile, true);
        }
    }

    public static boolean isRebootlessApex(RollbackInfo rollbackInfo) {
        if (rollbackInfo.isStaged()) {
            return false;
        }
        for (PackageRollbackInfo packageRollbackInfo : rollbackInfo.getPackages()) {
            if (packageRollbackInfo.isApex()) {
                return true;
            }
        }
        return false;
    }

    public void onBootCompletedAsync() {
        this.mHandler.post(new Runnable() { // from class: com.android.server.rollback.RollbackPackageHealthObserver$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                RollbackPackageHealthObserver.this.lambda$onBootCompletedAsync$3();
            }
        });
    }

    /* renamed from: onBootCompleted */
    public final void lambda$onBootCompletedAsync$3() {
        assertInWorkerThread();
        RollbackManager rollbackManager = (RollbackManager) this.mContext.getSystemService(RollbackManager.class);
        if (!rollbackManager.getAvailableRollbacks().isEmpty()) {
            PackageWatchdog.getInstance(this.mContext).scheduleCheckAndMitigateNativeCrashes();
        }
        SparseArray<String> popLastStagedRollbackIds = popLastStagedRollbackIds();
        for (int i = 0; i < popLastStagedRollbackIds.size(); i++) {
            WatchdogRollbackLogger.logRollbackStatusOnBoot(this.mContext, popLastStagedRollbackIds.keyAt(i), popLastStagedRollbackIds.valueAt(i), rollbackManager.getRecentlyCommittedRollbacks());
        }
    }

    public final RollbackInfo getAvailableRollback(VersionedPackage versionedPackage) {
        for (RollbackInfo rollbackInfo : ((RollbackManager) this.mContext.getSystemService(RollbackManager.class)).getAvailableRollbacks()) {
            for (PackageRollbackInfo packageRollbackInfo : rollbackInfo.getPackages()) {
                if (packageRollbackInfo.getVersionRolledBackFrom().equals(versionedPackage)) {
                    return rollbackInfo;
                }
                if (packageRollbackInfo.isApkInApex() && packageRollbackInfo.getVersionRolledBackFrom().getPackageName().equals(versionedPackage.getPackageName())) {
                    return rollbackInfo;
                }
            }
        }
        return null;
    }

    public final boolean markStagedSessionHandled(int i) {
        assertInWorkerThread();
        return this.mPendingStagedRollbackIds.remove(Integer.valueOf(i));
    }

    public final boolean isPendingStagedSessionsEmpty() {
        assertInWorkerThread();
        return this.mPendingStagedRollbackIds.isEmpty();
    }

    public static boolean readBoolean(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            boolean z = fileInputStream.read() == 1;
            fileInputStream.close();
            return z;
        } catch (IOException unused) {
            return false;
        }
    }

    public static void writeBoolean(File file, boolean z) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(z ? 1 : 0);
            fileOutputStream.flush();
            FileUtils.sync(fileOutputStream);
            fileOutputStream.close();
        } catch (IOException unused) {
        }
    }

    public final void saveStagedRollbackId(int i, VersionedPackage versionedPackage) {
        assertInWorkerThread();
        writeStagedRollbackId(this.mLastStagedRollbackIdsFile, i, versionedPackage);
    }

    public static void writeStagedRollbackId(File file, int i, VersionedPackage versionedPackage) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            PrintWriter printWriter = new PrintWriter(fileOutputStream);
            printWriter.append((CharSequence) String.valueOf(i)).append((CharSequence) ",").append((CharSequence) (versionedPackage != null ? versionedPackage.getPackageName() : ""));
            printWriter.println();
            printWriter.flush();
            FileUtils.sync(fileOutputStream);
            printWriter.close();
        } catch (IOException e) {
            Slog.e("RollbackPackageHealthObserver", "Failed to save last staged rollback id", e);
            file.delete();
        }
    }

    public final SparseArray<String> popLastStagedRollbackIds() {
        assertInWorkerThread();
        try {
            return readStagedRollbackIds(this.mLastStagedRollbackIdsFile);
        } finally {
            this.mLastStagedRollbackIdsFile.delete();
        }
    }

    public static SparseArray<String> readStagedRollbackIds(File file) {
        SparseArray<String> sparseArray = new SparseArray<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    return sparseArray;
                }
                String[] split = readLine.trim().split(",");
                String str = split[0];
                String str2 = "";
                if (split.length > 1) {
                    str2 = split[1];
                }
                sparseArray.put(Integer.parseInt(str), str2);
            }
        } catch (Exception unused) {
            return new SparseArray<>();
        }
    }

    public final boolean isModule(String str) {
        String activeApexPackageNameContainingPackage = this.mApexManager.getActiveApexPackageNameContainingPackage(str);
        if (activeApexPackageNameContainingPackage != null) {
            str = activeApexPackageNameContainingPackage;
        }
        try {
            return this.mContext.getPackageManager().getModuleInfo(str, 0) != null;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    /* renamed from: rollbackPackage */
    public final void lambda$execute$1(final RollbackInfo rollbackInfo, VersionedPackage versionedPackage, int i) {
        String packageName;
        assertInWorkerThread();
        if (isAutomaticRollbackDenied(SystemConfig.getInstance(), versionedPackage)) {
            Slog.d("RollbackPackageHealthObserver", "Automatic rollback not allowed for package " + versionedPackage.getPackageName());
            return;
        }
        RollbackManager rollbackManager = (RollbackManager) this.mContext.getSystemService(RollbackManager.class);
        final int mapFailureReasonToMetric = WatchdogRollbackLogger.mapFailureReasonToMetric(i);
        if (i == 1) {
            packageName = SystemProperties.get("sys.init.updatable_crashing_process_name", "");
        } else {
            packageName = versionedPackage.getPackageName();
        }
        final String str = packageName;
        final VersionedPackage logPackage = isModule(versionedPackage.getPackageName()) ? WatchdogRollbackLogger.getLogPackage(this.mContext, versionedPackage) : null;
        WatchdogRollbackLogger.logEvent(logPackage, 1, mapFailureReasonToMetric, str);
        final Consumer consumer = new Consumer() { // from class: com.android.server.rollback.RollbackPackageHealthObserver$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RollbackPackageHealthObserver.this.lambda$rollbackPackage$4(rollbackInfo, logPackage, mapFailureReasonToMetric, str, (Intent) obj);
            }
        };
        rollbackManager.commitRollback(rollbackInfo.getRollbackId(), Collections.singletonList(versionedPackage), new LocalIntentReceiver(new Consumer() { // from class: com.android.server.rollback.RollbackPackageHealthObserver$$ExternalSyntheticLambda5
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RollbackPackageHealthObserver.this.lambda$rollbackPackage$6(consumer, (Intent) obj);
            }
        }).getIntentSender());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$rollbackPackage$4(RollbackInfo rollbackInfo, VersionedPackage versionedPackage, int i, String str, Intent intent) {
        assertInWorkerThread();
        if (intent.getIntExtra("android.content.rollback.extra.STATUS", 1) == 0) {
            if (rollbackInfo.isStaged()) {
                saveStagedRollbackId(rollbackInfo.getRollbackId(), versionedPackage);
                WatchdogRollbackLogger.logEvent(versionedPackage, 4, i, str);
            } else {
                WatchdogRollbackLogger.logEvent(versionedPackage, 2, i, str);
            }
        } else {
            WatchdogRollbackLogger.logEvent(versionedPackage, 3, i, str);
        }
        if (rollbackInfo.isStaged()) {
            markStagedSessionHandled(rollbackInfo.getRollbackId());
            if (isPendingStagedSessionsEmpty()) {
                ((PowerManager) this.mContext.getSystemService(PowerManager.class)).reboot("Rollback staged install");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$rollbackPackage$6(final Consumer consumer, final Intent intent) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.rollback.RollbackPackageHealthObserver$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                consumer.accept(intent);
            }
        });
    }

    @VisibleForTesting
    public static boolean isAutomaticRollbackDenied(SystemConfig systemConfig, VersionedPackage versionedPackage) {
        return systemConfig.getAutomaticRollbackDenylistedPackages().contains(versionedPackage.getPackageName());
    }

    public final boolean useTwoPhaseRollback(List<RollbackInfo> list) {
        assertInWorkerThread();
        if (this.mTwoPhaseRollbackEnabled) {
            Slog.i("RollbackPackageHealthObserver", "Rolling back all rebootless APEX rollbacks");
            boolean z = false;
            for (RollbackInfo rollbackInfo : list) {
                if (isRebootlessApex(rollbackInfo)) {
                    lambda$execute$1(rollbackInfo, ((PackageRollbackInfo) rollbackInfo.getPackages().get(0)).getVersionRolledBackFrom(), 1);
                    z = true;
                }
            }
            return z;
        }
        return false;
    }

    /* renamed from: rollbackAll */
    public final void lambda$execute$0() {
        assertInWorkerThread();
        List<RollbackInfo> availableRollbacks = ((RollbackManager) this.mContext.getSystemService(RollbackManager.class)).getAvailableRollbacks();
        if (useTwoPhaseRollback(availableRollbacks)) {
            return;
        }
        Slog.i("RollbackPackageHealthObserver", "Rolling back all available rollbacks");
        for (RollbackInfo rollbackInfo : availableRollbacks) {
            if (rollbackInfo.isStaged()) {
                this.mPendingStagedRollbackIds.add(Integer.valueOf(rollbackInfo.getRollbackId()));
            }
        }
        for (RollbackInfo rollbackInfo2 : availableRollbacks) {
            lambda$execute$1(rollbackInfo2, ((PackageRollbackInfo) rollbackInfo2.getPackages().get(0)).getVersionRolledBackFrom(), 1);
        }
    }
}
