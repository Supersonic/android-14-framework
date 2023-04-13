package com.android.server.backup.params;

import android.app.backup.IBackupManagerMonitor;
import android.app.backup.IRestoreObserver;
import android.content.pm.PackageInfo;
import com.android.server.backup.internal.OnTaskFinishedListener;
import com.android.server.backup.transport.TransportConnection;
import com.android.server.backup.utils.BackupEligibilityRules;
/* loaded from: classes.dex */
public class RestoreParams {
    public final BackupEligibilityRules backupEligibilityRules;
    public final String[] filterSet;
    public final boolean isSystemRestore;
    public final OnTaskFinishedListener listener;
    public final TransportConnection mTransportConnection;
    public final IBackupManagerMonitor monitor;
    public final IRestoreObserver observer;
    public final PackageInfo packageInfo;
    public final int pmToken;
    public final long token;

    public static RestoreParams createForSinglePackage(TransportConnection transportConnection, IRestoreObserver iRestoreObserver, IBackupManagerMonitor iBackupManagerMonitor, long j, PackageInfo packageInfo, OnTaskFinishedListener onTaskFinishedListener, BackupEligibilityRules backupEligibilityRules) {
        return new RestoreParams(transportConnection, iRestoreObserver, iBackupManagerMonitor, j, packageInfo, 0, false, null, onTaskFinishedListener, backupEligibilityRules);
    }

    public static RestoreParams createForRestoreAtInstall(TransportConnection transportConnection, IRestoreObserver iRestoreObserver, IBackupManagerMonitor iBackupManagerMonitor, long j, String str, int i, OnTaskFinishedListener onTaskFinishedListener, BackupEligibilityRules backupEligibilityRules) {
        return new RestoreParams(transportConnection, iRestoreObserver, iBackupManagerMonitor, j, null, i, false, new String[]{str}, onTaskFinishedListener, backupEligibilityRules);
    }

    public static RestoreParams createForRestoreAll(TransportConnection transportConnection, IRestoreObserver iRestoreObserver, IBackupManagerMonitor iBackupManagerMonitor, long j, OnTaskFinishedListener onTaskFinishedListener, BackupEligibilityRules backupEligibilityRules) {
        return new RestoreParams(transportConnection, iRestoreObserver, iBackupManagerMonitor, j, null, 0, true, null, onTaskFinishedListener, backupEligibilityRules);
    }

    public static RestoreParams createForRestorePackages(TransportConnection transportConnection, IRestoreObserver iRestoreObserver, IBackupManagerMonitor iBackupManagerMonitor, long j, String[] strArr, boolean z, OnTaskFinishedListener onTaskFinishedListener, BackupEligibilityRules backupEligibilityRules) {
        return new RestoreParams(transportConnection, iRestoreObserver, iBackupManagerMonitor, j, null, 0, z, strArr, onTaskFinishedListener, backupEligibilityRules);
    }

    public RestoreParams(TransportConnection transportConnection, IRestoreObserver iRestoreObserver, IBackupManagerMonitor iBackupManagerMonitor, long j, PackageInfo packageInfo, int i, boolean z, String[] strArr, OnTaskFinishedListener onTaskFinishedListener, BackupEligibilityRules backupEligibilityRules) {
        this.mTransportConnection = transportConnection;
        this.observer = iRestoreObserver;
        this.monitor = iBackupManagerMonitor;
        this.token = j;
        this.packageInfo = packageInfo;
        this.pmToken = i;
        this.isSystemRestore = z;
        this.filterSet = strArr;
        this.listener = onTaskFinishedListener;
        this.backupEligibilityRules = backupEligibilityRules;
    }
}
