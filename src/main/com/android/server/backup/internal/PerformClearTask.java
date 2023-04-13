package com.android.server.backup.internal;

import android.content.pm.PackageInfo;
import android.util.Slog;
import com.android.server.backup.TransportManager;
import com.android.server.backup.UserBackupManagerService;
import com.android.server.backup.transport.BackupTransportClient;
import com.android.server.backup.transport.TransportConnection;
import java.io.File;
/* loaded from: classes.dex */
public class PerformClearTask implements Runnable {
    public final UserBackupManagerService mBackupManagerService;
    public final OnTaskFinishedListener mListener;
    public final PackageInfo mPackage;
    public final TransportConnection mTransportConnection;
    public final TransportManager mTransportManager;

    public PerformClearTask(UserBackupManagerService userBackupManagerService, TransportConnection transportConnection, PackageInfo packageInfo, OnTaskFinishedListener onTaskFinishedListener) {
        this.mBackupManagerService = userBackupManagerService;
        this.mTransportManager = userBackupManagerService.getTransportManager();
        this.mTransportConnection = transportConnection;
        this.mPackage = packageInfo;
        this.mListener = onTaskFinishedListener;
    }

    @Override // java.lang.Runnable
    public void run() {
        StringBuilder sb;
        BackupTransportClient backupTransportClient = null;
        try {
            try {
                new File(new File(this.mBackupManagerService.getBaseStateDir(), this.mTransportManager.getTransportDirName(this.mTransportConnection.getTransportComponent())), this.mPackage.packageName).delete();
                backupTransportClient = this.mTransportConnection.connectOrThrow("PerformClearTask.run()");
                backupTransportClient.clearBackupData(this.mPackage);
                try {
                    backupTransportClient.finishBackup();
                } catch (Exception e) {
                    e = e;
                    sb = new StringBuilder();
                    sb.append("Unable to mark clear operation finished: ");
                    sb.append(e.getMessage());
                    Slog.e("BackupManagerService", sb.toString());
                    this.mListener.onFinished("PerformClearTask.run()");
                    this.mBackupManagerService.getWakelock().release();
                }
            } catch (Throwable th) {
                if (backupTransportClient != null) {
                    try {
                        backupTransportClient.finishBackup();
                    } catch (Exception e2) {
                        Slog.e("BackupManagerService", "Unable to mark clear operation finished: " + e2.getMessage());
                    }
                }
                this.mListener.onFinished("PerformClearTask.run()");
                this.mBackupManagerService.getWakelock().release();
                throw th;
            }
        } catch (Exception e3) {
            Slog.e("BackupManagerService", "Transport threw clearing data for " + this.mPackage + ": " + e3.getMessage());
            if (backupTransportClient != null) {
                try {
                    backupTransportClient.finishBackup();
                } catch (Exception e4) {
                    e = e4;
                    sb = new StringBuilder();
                    sb.append("Unable to mark clear operation finished: ");
                    sb.append(e.getMessage());
                    Slog.e("BackupManagerService", sb.toString());
                    this.mListener.onFinished("PerformClearTask.run()");
                    this.mBackupManagerService.getWakelock().release();
                }
            }
        }
        this.mListener.onFinished("PerformClearTask.run()");
        this.mBackupManagerService.getWakelock().release();
    }
}
