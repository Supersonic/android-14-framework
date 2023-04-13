package com.android.server.backup.internal;

import android.app.backup.IBackupObserver;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.EventLog;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.backup.TransportManager;
import com.android.server.backup.UserBackupManagerService;
import com.android.server.backup.transport.BackupTransportClient;
import com.android.server.backup.transport.TransportConnection;
import java.io.File;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class PerformInitializeTask implements Runnable {
    public final UserBackupManagerService mBackupManagerService;
    public final File mBaseStateDir;
    public final OnTaskFinishedListener mListener;
    public IBackupObserver mObserver;
    public final String[] mQueue;
    public final TransportManager mTransportManager;

    public PerformInitializeTask(UserBackupManagerService userBackupManagerService, String[] strArr, IBackupObserver iBackupObserver, OnTaskFinishedListener onTaskFinishedListener) {
        this(userBackupManagerService, userBackupManagerService.getTransportManager(), strArr, iBackupObserver, onTaskFinishedListener, userBackupManagerService.getBaseStateDir());
    }

    @VisibleForTesting
    public PerformInitializeTask(UserBackupManagerService userBackupManagerService, TransportManager transportManager, String[] strArr, IBackupObserver iBackupObserver, OnTaskFinishedListener onTaskFinishedListener, File file) {
        this.mBackupManagerService = userBackupManagerService;
        this.mTransportManager = transportManager;
        this.mQueue = strArr;
        this.mObserver = iBackupObserver;
        this.mListener = onTaskFinishedListener;
        this.mBaseStateDir = file;
    }

    public final void notifyResult(String str, int i) {
        try {
            IBackupObserver iBackupObserver = this.mObserver;
            if (iBackupObserver != null) {
                iBackupObserver.onResult(str, i);
            }
        } catch (RemoteException unused) {
            this.mObserver = null;
        }
    }

    public final void notifyFinished(int i) {
        try {
            IBackupObserver iBackupObserver = this.mObserver;
            if (iBackupObserver != null) {
                iBackupObserver.backupFinished(i);
            }
        } catch (RemoteException unused) {
            this.mObserver = null;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.lang.Runnable
    public void run() {
        String[] strArr;
        ArrayList<TransportConnection> arrayList = new ArrayList(this.mQueue.length);
        int i = 0;
        try {
            try {
                int i2 = 0;
                for (String str : this.mQueue) {
                    try {
                        TransportConnection transportClient = this.mTransportManager.getTransportClient(str, "PerformInitializeTask.run()");
                        if (transportClient == null) {
                            Slog.e("BackupManagerService", "Requested init for " + str + " but not found");
                        } else {
                            arrayList.add(transportClient);
                            Slog.i("BackupManagerService", "Initializing (wiping) backup transport storage: " + str);
                            String transportDirName = this.mTransportManager.getTransportDirName(transportClient.getTransportComponent());
                            EventLog.writeEvent(2821, transportDirName);
                            long elapsedRealtime = SystemClock.elapsedRealtime();
                            BackupTransportClient connectOrThrow = transportClient.connectOrThrow("PerformInitializeTask.run()");
                            int initializeDevice = connectOrThrow.initializeDevice();
                            if (initializeDevice != 0) {
                                Slog.e("BackupManagerService", "Transport error in initializeDevice()");
                            } else {
                                initializeDevice = connectOrThrow.finishBackup();
                                if (initializeDevice != 0) {
                                    Slog.e("BackupManagerService", "Transport error in finishBackup()");
                                }
                            }
                            if (initializeDevice == 0) {
                                Slog.i("BackupManagerService", "Device init successful");
                                int elapsedRealtime2 = (int) (SystemClock.elapsedRealtime() - elapsedRealtime);
                                EventLog.writeEvent(2827, new Object[0]);
                                this.mBackupManagerService.resetBackupState(new File(this.mBaseStateDir, transportDirName));
                                EventLog.writeEvent(2825, 0, Integer.valueOf(elapsedRealtime2));
                                this.mBackupManagerService.recordInitPending(false, str, transportDirName);
                                notifyResult(str, 0);
                            } else {
                                EventLog.writeEvent(2822, "(initialize)");
                                this.mBackupManagerService.recordInitPending(true, str, transportDirName);
                                notifyResult(str, initializeDevice);
                                try {
                                    long requestBackupTime = connectOrThrow.requestBackupTime();
                                    Slog.w("BackupManagerService", "Init failed on " + str + " resched in " + requestBackupTime);
                                    this.mBackupManagerService.getAlarmManager().set(0, System.currentTimeMillis() + requestBackupTime, this.mBackupManagerService.getRunInitIntent());
                                    i2 = initializeDevice;
                                } catch (Exception e) {
                                    e = e;
                                    i = initializeDevice;
                                    Slog.e("BackupManagerService", "Unexpected error performing init", e);
                                    ArrayList arrayList2 = arrayList;
                                    for (TransportConnection transportConnection : arrayList) {
                                        TransportManager transportManager = this.mTransportManager;
                                        transportManager.disposeOfTransportClient(transportConnection, "PerformInitializeTask.run()");
                                        arrayList2 = transportManager;
                                    }
                                    notifyFinished(-1000);
                                    arrayList = arrayList2;
                                    this.mListener.onFinished("PerformInitializeTask.run()");
                                } catch (Throwable th) {
                                    th = th;
                                    i = initializeDevice;
                                    for (TransportConnection transportConnection2 : arrayList) {
                                        this.mTransportManager.disposeOfTransportClient(transportConnection2, "PerformInitializeTask.run()");
                                    }
                                    notifyFinished(i);
                                    this.mListener.onFinished("PerformInitializeTask.run()");
                                    throw th;
                                }
                            }
                        }
                    } catch (Exception e2) {
                        e = e2;
                        i = i2;
                    } catch (Throwable th2) {
                        th = th2;
                        i = i2;
                    }
                }
                ArrayList arrayList3 = arrayList;
                for (TransportConnection transportConnection3 : arrayList) {
                    TransportManager transportManager2 = this.mTransportManager;
                    transportManager2.disposeOfTransportClient(transportConnection3, "PerformInitializeTask.run()");
                    arrayList3 = transportManager2;
                }
                notifyFinished(i2);
                arrayList = arrayList3;
            } catch (Throwable th3) {
                th = th3;
            }
        } catch (Exception e3) {
            e = e3;
        }
        this.mListener.onFinished("PerformInitializeTask.run()");
    }
}
