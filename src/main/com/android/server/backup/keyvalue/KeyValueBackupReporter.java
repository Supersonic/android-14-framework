package com.android.server.backup.keyvalue;

import android.app.backup.IBackupManagerMonitor;
import android.app.backup.IBackupObserver;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.EventLog;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.EventLogTags;
import com.android.server.backup.DataChangedJournal;
import com.android.server.backup.UserBackupManagerService;
import com.android.server.backup.remote.RemoteResult;
import com.android.server.backup.utils.BackupManagerMonitorUtils;
import com.android.server.backup.utils.BackupObserverUtils;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
@VisibleForTesting
/* loaded from: classes.dex */
public class KeyValueBackupReporter {
    @VisibleForTesting
    static final boolean MORE_DEBUG = false;
    @VisibleForTesting
    static final String TAG = "KeyValueBackupTask";
    public final UserBackupManagerService mBackupManagerService;
    public IBackupManagerMonitor mMonitor;
    public final IBackupObserver mObserver;

    public void onAgentFilesReady(File file) {
    }

    public void onCancel() {
    }

    public void onRemoteCallReturned(RemoteResult remoteResult, String str) {
    }

    public void onRevertTask() {
    }

    public void onTransportPerformBackup(String str) {
    }

    public void onWriteWidgetData(boolean z, byte[] bArr) {
    }

    public static void onNewThread(String str) {
        Slog.d(TAG, "Spinning thread " + str);
    }

    public KeyValueBackupReporter(UserBackupManagerService userBackupManagerService, IBackupObserver iBackupObserver, IBackupManagerMonitor iBackupManagerMonitor) {
        this.mBackupManagerService = userBackupManagerService;
        this.mObserver = iBackupObserver;
        this.mMonitor = iBackupManagerMonitor;
    }

    public IBackupManagerMonitor getMonitor() {
        return this.mMonitor;
    }

    public IBackupObserver getObserver() {
        return this.mObserver;
    }

    public void onSkipBackup() {
        Slog.d(TAG, "Skipping backup since one is already in progress");
    }

    public void onEmptyQueueAtStart() {
        Slog.w(TAG, "Backup begun with an empty queue, nothing to do");
    }

    public void onQueueReady(List<String> list) {
        Slog.v(TAG, "Beginning backup of " + list.size() + " targets");
    }

    public void onTransportReady(String str) {
        EventLog.writeEvent(2821, str);
    }

    public void onInitializeTransport(String str) {
        Slog.i(TAG, "Initializing transport and resetting backup state");
    }

    public void onTransportInitialized(int i) {
        if (i == 0) {
            EventLog.writeEvent(2827, new Object[0]);
            return;
        }
        EventLog.writeEvent(2822, "(initialize)");
        Slog.e(TAG, "Transport error in initializeDevice()");
    }

    public void onInitializeTransportError(Exception exc) {
        Slog.e(TAG, "Error during initialization", exc);
    }

    public void onSkipPm() {
        Slog.d(TAG, "Skipping backup of PM metadata");
    }

    public void onExtractPmAgentDataError(Exception exc) {
        Slog.e(TAG, "Error during PM metadata backup", exc);
    }

    public void onStartPackageBackup(String str) {
        Slog.d(TAG, "Starting key-value backup of " + str);
    }

    public void onPackageNotEligibleForBackup(String str) {
        Slog.i(TAG, "Package " + str + " no longer supports backup, skipping");
        BackupObserverUtils.sendBackupOnPackageResult(this.mObserver, str, -2001);
    }

    public void onPackageEligibleForFullBackup(String str) {
        Slog.i(TAG, "Package " + str + " performs full-backup rather than key-value, skipping");
        BackupObserverUtils.sendBackupOnPackageResult(this.mObserver, str, -2001);
    }

    public void onPackageStopped(String str) {
        BackupObserverUtils.sendBackupOnPackageResult(this.mObserver, str, -2001);
    }

    public void onAgentUnknown(String str) {
        Slog.d(TAG, "Package does not exist, skipping");
        BackupObserverUtils.sendBackupOnPackageResult(this.mObserver, str, -2002);
    }

    public void onBindAgentError(String str, SecurityException securityException) {
        Slog.d(TAG, "Error in bind/backup", securityException);
        BackupObserverUtils.sendBackupOnPackageResult(this.mObserver, str, -1003);
    }

    public void onAgentError(String str) {
        BackupObserverUtils.sendBackupOnPackageResult(this.mObserver, str, -1003);
    }

    public void onExtractAgentData(String str) {
        Slog.d(TAG, "Invoking agent on " + str);
    }

    public void onRestoreconFailed(File file) {
        Slog.e(TAG, "SELinux restorecon failed on " + file);
    }

    public void onCallAgentDoBackupError(String str, boolean z, Exception exc) {
        if (z) {
            Slog.e(TAG, "Error invoking agent on " + str + ": " + exc);
            BackupObserverUtils.sendBackupOnPackageResult(this.mObserver, str, -1003);
        } else {
            Slog.e(TAG, "Error before invoking agent on " + str + ": " + exc);
        }
        EventLog.writeEvent(2823, str, exc.toString());
    }

    public void onFailAgentError(String str) {
        Slog.w(TAG, "Error conveying failure to " + str);
    }

    public void onAgentIllegalKey(PackageInfo packageInfo, String str) {
        String str2 = packageInfo.packageName;
        EventLog.writeEvent(2823, str2, "bad key");
        this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 5, packageInfo, 3, BackupManagerMonitorUtils.putMonitoringExtra((Bundle) null, "android.app.backup.extra.LOG_ILLEGAL_KEY", str));
        BackupObserverUtils.sendBackupOnPackageResult(this.mObserver, str2, -1003);
    }

    public void onAgentDataError(String str, IOException iOException) {
        Slog.w(TAG, "Unable to read/write agent data for " + str + ": " + iOException);
    }

    public void onDigestError(NoSuchAlgorithmException noSuchAlgorithmException) {
        Slog.e(TAG, "Unable to use SHA-1!");
    }

    public void onEmptyData(PackageInfo packageInfo) {
        this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 7, packageInfo, 3, null);
    }

    public void onPackageBackupComplete(String str, long j) {
        BackupObserverUtils.sendBackupOnPackageResult(this.mObserver, str, 0);
        EventLog.writeEvent(2824, str, Long.valueOf(j));
        this.mBackupManagerService.logBackupComplete(str);
    }

    public void onPackageBackupRejected(String str) {
        BackupObserverUtils.sendBackupOnPackageResult(this.mObserver, str, -1002);
        EventLogTags.writeBackupAgentFailure(str, "Transport rejected");
    }

    public void onPackageBackupQuotaExceeded(String str) {
        BackupObserverUtils.sendBackupOnPackageResult(this.mObserver, str, -1005);
        EventLog.writeEvent(2829, str);
    }

    public void onAgentDoQuotaExceededError(Exception exc) {
        Slog.e(TAG, "Unable to notify about quota exceeded: " + exc);
    }

    public void onPackageBackupNonIncrementalRequired(PackageInfo packageInfo) {
        Slog.i(TAG, "Transport lost data, retrying package");
        BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 51, packageInfo, 1, null);
    }

    public void onPackageBackupNonIncrementalAndNonIncrementalRequired(String str) {
        Slog.e(TAG, "Transport requested non-incremental but already the case");
        BackupObserverUtils.sendBackupOnPackageResult(this.mObserver, str, -1000);
        EventLog.writeEvent(2822, str);
    }

    public void onPackageBackupTransportFailure(String str) {
        BackupObserverUtils.sendBackupOnPackageResult(this.mObserver, str, -1000);
        EventLog.writeEvent(2822, str);
    }

    public void onPackageBackupTransportError(String str, Exception exc) {
        Slog.e(TAG, "Transport error backing up " + str, exc);
        BackupObserverUtils.sendBackupOnPackageResult(this.mObserver, str, -1000);
        EventLog.writeEvent(2822, str);
    }

    public void onCloseFileDescriptorError(String str) {
        Slog.w(TAG, "Error closing " + str + " file-descriptor");
    }

    public void onAgentTimedOut(PackageInfo packageInfo) {
        String packageName = getPackageName(packageInfo);
        Slog.i(TAG, "Agent " + packageName + " timed out");
        EventLog.writeEvent(2823, packageName);
        this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 21, packageInfo, 2, BackupManagerMonitorUtils.putMonitoringExtra((Bundle) null, "android.app.backup.extra.LOG_CANCEL_ALL", false));
    }

    public void onAgentCancelled(PackageInfo packageInfo) {
        String packageName = getPackageName(packageInfo);
        Slog.i(TAG, "Cancel backing up " + packageName);
        EventLog.writeEvent(2823, packageName);
        this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 21, packageInfo, 2, BackupManagerMonitorUtils.putMonitoringExtra((Bundle) null, "android.app.backup.extra.LOG_CANCEL_ALL", true));
    }

    public void onAgentResultError(PackageInfo packageInfo) {
        String packageName = getPackageName(packageInfo);
        BackupObserverUtils.sendBackupOnPackageResult(this.mObserver, packageName, -1003);
        EventLog.writeEvent(2823, packageName, "result error");
        Slog.w(TAG, "Agent " + packageName + " error in onBackup()");
    }

    public final String getPackageName(PackageInfo packageInfo) {
        return packageInfo != null ? packageInfo.packageName : "no_package_yet";
    }

    public void onTransportRequestBackupTimeError(Exception exc) {
        Slog.w(TAG, "Unable to contact transport for recommended backoff: " + exc);
    }

    public void onJournalDeleteFailed(DataChangedJournal dataChangedJournal) {
        Slog.e(TAG, "Unable to remove backup journal file " + dataChangedJournal);
    }

    public void onSetCurrentTokenError(Exception exc) {
        Slog.e(TAG, "Transport threw reporting restore set: " + exc);
    }

    public void onTransportNotInitialized(String str) {
        EventLog.writeEvent(2826, str);
    }

    public void onPendingInitializeTransportError(Exception exc) {
        Slog.w(TAG, "Failed to query transport name for pending init: " + exc);
    }

    public void onBackupFinished(int i) {
        BackupObserverUtils.sendBackupFinished(this.mObserver, i);
    }

    public void onStartFullBackup(List<String> list) {
        Slog.d(TAG, "Starting full backups for: " + list);
    }

    public void onTaskFinished() {
        Slog.i(TAG, "K/V backup pass finished");
    }
}
