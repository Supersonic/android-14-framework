package com.android.server.backup.params;

import android.app.backup.IBackupManagerMonitor;
import android.app.backup.IBackupObserver;
import com.android.server.backup.internal.OnTaskFinishedListener;
import com.android.server.backup.transport.TransportConnection;
import com.android.server.backup.utils.BackupEligibilityRules;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class BackupParams {
    public String dirName;
    public ArrayList<String> fullPackages;
    public ArrayList<String> kvPackages;
    public OnTaskFinishedListener listener;
    public BackupEligibilityRules mBackupEligibilityRules;
    public TransportConnection mTransportConnection;
    public IBackupManagerMonitor monitor;
    public boolean nonIncrementalBackup;
    public IBackupObserver observer;
    public boolean userInitiated;

    public BackupParams(TransportConnection transportConnection, String str, ArrayList<String> arrayList, ArrayList<String> arrayList2, IBackupObserver iBackupObserver, IBackupManagerMonitor iBackupManagerMonitor, OnTaskFinishedListener onTaskFinishedListener, boolean z, boolean z2, BackupEligibilityRules backupEligibilityRules) {
        this.mTransportConnection = transportConnection;
        this.dirName = str;
        this.kvPackages = arrayList;
        this.fullPackages = arrayList2;
        this.observer = iBackupObserver;
        this.monitor = iBackupManagerMonitor;
        this.listener = onTaskFinishedListener;
        this.userInitiated = z;
        this.nonIncrementalBackup = z2;
        this.mBackupEligibilityRules = backupEligibilityRules;
    }
}
