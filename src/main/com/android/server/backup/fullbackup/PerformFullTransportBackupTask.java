package com.android.server.backup.fullbackup;

import android.app.IBackupAgent;
import android.app.backup.BackupProgress;
import android.app.backup.IBackupCallback;
import android.app.backup.IBackupManagerMonitor;
import android.app.backup.IBackupObserver;
import android.app.backup.IFullBackupRestoreObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.EventLog;
import android.util.Log;
import android.util.Slog;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.backup.BackupAndRestoreFeatureFlags;
import com.android.server.backup.BackupRestoreTask;
import com.android.server.backup.FullBackupJob;
import com.android.server.backup.OperationStorage;
import com.android.server.backup.TransportManager;
import com.android.server.backup.UserBackupManagerService;
import com.android.server.backup.fullbackup.PerformFullTransportBackupTask;
import com.android.server.backup.internal.OnTaskFinishedListener;
import com.android.server.backup.remote.RemoteCall;
import com.android.server.backup.remote.RemoteCallable;
import com.android.server.backup.transport.BackupTransportClient;
import com.android.server.backup.transport.TransportConnection;
import com.android.server.backup.transport.TransportNotAvailableException;
import com.android.server.backup.utils.BackupEligibilityRules;
import com.android.server.backup.utils.BackupManagerMonitorUtils;
import com.android.server.backup.utils.BackupObserverUtils;
import com.google.android.collect.Sets;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
/* loaded from: classes.dex */
public class PerformFullTransportBackupTask extends FullBackupTask implements BackupRestoreTask {
    public final BackupAgentTimeoutParameters mAgentTimeoutParameters;
    public final BackupEligibilityRules mBackupEligibilityRules;
    public IBackupObserver mBackupObserver;
    public SinglePackageBackupRunner mBackupRunner;
    public final int mBackupRunnerOpToken;
    public volatile boolean mCancelAll;
    public final Object mCancelLock;
    public final int mCurrentOpToken;
    public PackageInfo mCurrentPackage;
    public volatile boolean mIsDoingBackup;
    public FullBackupJob mJob;
    public CountDownLatch mLatch;
    public final OnTaskFinishedListener mListener;
    public IBackupManagerMonitor mMonitor;
    public OperationStorage mOperationStorage;
    public List<PackageInfo> mPackages;
    public final TransportConnection mTransportConnection;
    public boolean mUpdateSchedule;
    public UserBackupManagerService mUserBackupManagerService;
    public final int mUserId;
    public boolean mUserInitiated;

    @Override // com.android.server.backup.BackupRestoreTask
    public void execute() {
    }

    @Override // com.android.server.backup.BackupRestoreTask
    public void operationComplete(long j) {
    }

    public static PerformFullTransportBackupTask newWithCurrentTransport(UserBackupManagerService userBackupManagerService, OperationStorage operationStorage, IFullBackupRestoreObserver iFullBackupRestoreObserver, String[] strArr, boolean z, FullBackupJob fullBackupJob, CountDownLatch countDownLatch, IBackupObserver iBackupObserver, IBackupManagerMonitor iBackupManagerMonitor, boolean z2, String str, BackupEligibilityRules backupEligibilityRules) {
        final TransportManager transportManager = userBackupManagerService.getTransportManager();
        final TransportConnection currentTransportClient = transportManager.getCurrentTransportClient(str);
        if (currentTransportClient == null) {
            throw new IllegalStateException("No TransportConnection available");
        }
        return new PerformFullTransportBackupTask(userBackupManagerService, operationStorage, currentTransportClient, iFullBackupRestoreObserver, strArr, z, fullBackupJob, countDownLatch, iBackupObserver, iBackupManagerMonitor, new OnTaskFinishedListener() { // from class: com.android.server.backup.fullbackup.PerformFullTransportBackupTask$$ExternalSyntheticLambda0
            @Override // com.android.server.backup.internal.OnTaskFinishedListener
            public final void onFinished(String str2) {
                TransportManager.this.disposeOfTransportClient(currentTransportClient, str2);
            }
        }, z2, backupEligibilityRules);
    }

    public PerformFullTransportBackupTask(UserBackupManagerService userBackupManagerService, OperationStorage operationStorage, TransportConnection transportConnection, IFullBackupRestoreObserver iFullBackupRestoreObserver, String[] strArr, boolean z, FullBackupJob fullBackupJob, CountDownLatch countDownLatch, IBackupObserver iBackupObserver, IBackupManagerMonitor iBackupManagerMonitor, OnTaskFinishedListener onTaskFinishedListener, boolean z2, BackupEligibilityRules backupEligibilityRules) {
        super(iFullBackupRestoreObserver);
        this.mCancelLock = new Object();
        this.mUserBackupManagerService = userBackupManagerService;
        this.mOperationStorage = operationStorage;
        this.mTransportConnection = transportConnection;
        this.mUpdateSchedule = z;
        this.mLatch = countDownLatch;
        this.mJob = fullBackupJob;
        this.mPackages = new ArrayList(strArr.length);
        this.mBackupObserver = iBackupObserver;
        this.mMonitor = iBackupManagerMonitor;
        this.mListener = onTaskFinishedListener == null ? OnTaskFinishedListener.NOP : onTaskFinishedListener;
        this.mUserInitiated = z2;
        this.mCurrentOpToken = userBackupManagerService.generateRandomIntegerToken();
        this.mBackupRunnerOpToken = userBackupManagerService.generateRandomIntegerToken();
        BackupAgentTimeoutParameters agentTimeoutParameters = userBackupManagerService.getAgentTimeoutParameters();
        Objects.requireNonNull(agentTimeoutParameters, "Timeout parameters cannot be null");
        this.mAgentTimeoutParameters = agentTimeoutParameters;
        this.mUserId = userBackupManagerService.getUserId();
        this.mBackupEligibilityRules = backupEligibilityRules;
        if (userBackupManagerService.isBackupOperationInProgress()) {
            Slog.d("PFTBT", "Skipping full backup. A backup is already in progress.");
            this.mCancelAll = true;
            return;
        }
        for (String str : strArr) {
            try {
                PackageInfo packageInfoAsUser = userBackupManagerService.getPackageManager().getPackageInfoAsUser(str, 134217728, this.mUserId);
                this.mCurrentPackage = packageInfoAsUser;
                if (!this.mBackupEligibilityRules.appIsEligibleForBackup(packageInfoAsUser.applicationInfo)) {
                    this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 9, this.mCurrentPackage, 3, null);
                    BackupObserverUtils.sendBackupOnPackageResult(this.mBackupObserver, str, -2001);
                } else if (!this.mBackupEligibilityRules.appGetsFullBackup(packageInfoAsUser)) {
                    this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 10, this.mCurrentPackage, 3, null);
                    BackupObserverUtils.sendBackupOnPackageResult(this.mBackupObserver, str, -2001);
                } else if (this.mBackupEligibilityRules.appIsStopped(packageInfoAsUser.applicationInfo)) {
                    this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 11, this.mCurrentPackage, 3, null);
                    BackupObserverUtils.sendBackupOnPackageResult(this.mBackupObserver, str, -2001);
                } else {
                    this.mPackages.add(packageInfoAsUser);
                }
            } catch (PackageManager.NameNotFoundException unused) {
                Slog.i("PFTBT", "Requested package " + str + " not found; ignoring");
                this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 12, this.mCurrentPackage, 3, null);
            }
        }
        this.mPackages = userBackupManagerService.filterUserFacingPackages(this.mPackages);
        HashSet newHashSet = Sets.newHashSet();
        for (PackageInfo packageInfo : this.mPackages) {
            newHashSet.add(packageInfo.packageName);
        }
        Slog.d("PFTBT", "backupmanager pftbt token=" + Integer.toHexString(this.mCurrentOpToken));
        this.mOperationStorage.registerOperationForPackages(this.mCurrentOpToken, 0, newHashSet, this, 2);
    }

    public void unregisterTask() {
        this.mOperationStorage.removeOperation(this.mCurrentOpToken);
    }

    @Override // com.android.server.backup.BackupRestoreTask
    public void handleCancel(boolean z) {
        synchronized (this.mCancelLock) {
            if (!z) {
                Slog.wtf("PFTBT", "Expected cancelAll to be true.");
            }
            if (this.mCancelAll) {
                Slog.d("PFTBT", "Ignoring duplicate cancel call.");
                return;
            }
            this.mCancelAll = true;
            if (this.mIsDoingBackup) {
                this.mUserBackupManagerService.handleCancel(this.mBackupRunnerOpToken, z);
                try {
                    this.mTransportConnection.getConnectedTransport("PFTBT.handleCancel()").cancelFullBackup();
                } catch (RemoteException | TransportNotAvailableException e) {
                    Slog.w("PFTBT", "Error calling cancelFullBackup() on transport: " + e);
                }
            }
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(6:(13:(3:99|100|(1:102)(6:103|104|105|(11:107|108|109|110|(1:112)(1:129)|113|(1:115)|116|72|121|(1:123))(15:133|(2:401|402)|135|136|(1:(7:138|139|141|142|(1:144)(1:389)|145|124)(1:399))|365|(1:367)(1:384)|368|(1:370)|371|5ad|376|(1:378)|86|87)|27|28))|12|13|14|(1:16)(1:36)|17|(1:19)|20|672|25|(1:30)|27|28)|6|7|(1:9)(1:65)|10|11) */
    /* JADX WARN: Code restructure failed: missing block: B:174:0x042d, code lost:
        r7 = -1000;
     */
    /* JADX WARN: Code restructure failed: missing block: B:175:0x042f, code lost:
        com.android.server.backup.utils.BackupObserverUtils.sendBackupOnPackageResult(r32.mBackupObserver, r5, -1000);
        android.util.Slog.w("PFTBT", "Transport failed; aborting backup: " + r13);
        android.util.EventLog.writeEvent(2842, new java.lang.Object[0]);
     */
    /* JADX WARN: Code restructure failed: missing block: B:176:0x0450, code lost:
        r32.mUserBackupManagerService.tearDownAgentAndKill(r15.applicationInfo);
     */
    /* JADX WARN: Code restructure failed: missing block: B:178:0x0459, code lost:
        if (r32.mCancelAll == false) goto L246;
     */
    /* JADX WARN: Code restructure failed: missing block: B:179:0x045b, code lost:
        r12 = -2003;
     */
    /* JADX WARN: Code restructure failed: missing block: B:180:0x045d, code lost:
        r12 = -1000;
     */
    /* JADX WARN: Code restructure failed: missing block: B:181:0x045e, code lost:
        android.util.Slog.i("PFTBT", "Full backup completed with status: " + r12);
        com.android.server.backup.utils.BackupObserverUtils.sendBackupFinished(r32.mBackupObserver, r12);
        cleanUpPipes(r25);
        cleanUpPipes(r1);
        unregisterTask();
        r1 = r32.mJob;
     */
    /* JADX WARN: Code restructure failed: missing block: B:182:0x0486, code lost:
        if (r1 == null) goto L233;
     */
    /* JADX WARN: Code restructure failed: missing block: B:183:0x0488, code lost:
        r1.finishBackupPass(r32.mUserId);
     */
    /* JADX WARN: Code restructure failed: missing block: B:184:0x048d, code lost:
        r4 = r32.mUserBackupManagerService.getQueueLock();
     */
    /* JADX WARN: Code restructure failed: missing block: B:185:0x0493, code lost:
        monitor-enter(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:186:0x0494, code lost:
        r32.mUserBackupManagerService.setRunningFullBackupTask(null);
     */
    /* JADX WARN: Code restructure failed: missing block: B:187:0x049a, code lost:
        monitor-exit(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:188:0x049b, code lost:
        r32.mListener.onFinished("PFTBT.run()");
        r32.mLatch.countDown();
     */
    /* JADX WARN: Code restructure failed: missing block: B:189:0x04a9, code lost:
        if (r32.mUpdateSchedule == false) goto L27;
     */
    /* JADX WARN: Code restructure failed: missing block: B:190:0x04ab, code lost:
        r32.mUserBackupManagerService.scheduleNextFullBackupJob(r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:195:0x04b6, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:196:0x04b7, code lost:
        r9 = r25;
        r12 = -1000;
     */
    /* JADX WARN: Code restructure failed: missing block: B:197:0x04bc, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:198:0x04bd, code lost:
        r9 = r25;
     */
    /* JADX WARN: Code restructure failed: missing block: B:302:0x06a0, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:303:0x06a1, code lost:
        r8 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:310:0x06b6, code lost:
        r1 = null;
        r2 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:315:0x06dc, code lost:
        r12 = r6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:316:0x06de, code lost:
        r12 = r7;
     */
    /* JADX WARN: Code restructure failed: missing block: B:319:0x0707, code lost:
        r1.finishBackupPass(r32.mUserId);
     */
    /* JADX WARN: Code restructure failed: missing block: B:322:0x0713, code lost:
        r32.mUserBackupManagerService.setRunningFullBackupTask(null);
     */
    /* JADX WARN: Code restructure failed: missing block: B:324:0x071a, code lost:
        r32.mListener.onFinished("PFTBT.run()");
        r32.mLatch.countDown();
     */
    /* JADX WARN: Code restructure failed: missing block: B:325:0x0728, code lost:
        if (r32.mUpdateSchedule != false) goto L85;
     */
    /* JADX WARN: Code restructure failed: missing block: B:326:0x072a, code lost:
        r32.mUserBackupManagerService.scheduleNextFullBackupJob(r8);
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x012a, code lost:
        r2 = r11;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:315:0x06dc  */
    /* JADX WARN: Removed duplicated region for block: B:316:0x06de  */
    /* JADX WARN: Removed duplicated region for block: B:319:0x0707  */
    /* JADX WARN: Removed duplicated region for block: B:337:0x074b  */
    /* JADX WARN: Removed duplicated region for block: B:338:0x074d  */
    /* JADX WARN: Removed duplicated region for block: B:341:0x0776  */
    /* JADX WARN: Removed duplicated region for block: B:361:0x0782 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:373:0x0713 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:409:0x04e8 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:425:0x0509 A[SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r11v25 */
    /* JADX WARN: Type inference failed for: r11v28 */
    /* JADX WARN: Type inference failed for: r11v9 */
    /* JADX WARN: Type inference failed for: r7v34 */
    /* JADX WARN: Type inference failed for: r7v5 */
    /* JADX WARN: Type inference failed for: r7v7, types: [boolean] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:227:0x0546 -> B:389:0x0547). Please submit an issue!!! */
    @Override // java.lang.Runnable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void run() {
        int i;
        int i2;
        int i3;
        Exception exc;
        ParcelFileDescriptor[] parcelFileDescriptorArr;
        ParcelFileDescriptor[] parcelFileDescriptorArr2;
        Throwable th;
        int i4;
        FullBackupJob fullBackupJob;
        FullBackupJob fullBackupJob2;
        ParcelFileDescriptor[] parcelFileDescriptorArr3;
        Throwable th2;
        int i5;
        byte[] bArr;
        int i6;
        BackupTransportClient backupTransportClient;
        ParcelFileDescriptor[] parcelFileDescriptorArr4;
        String str;
        PackageInfo packageInfo;
        long j;
        ?? r11;
        BackupTransportClient backupTransportClient2;
        String str2;
        long j2;
        ParcelFileDescriptor[] parcelFileDescriptorArr5;
        boolean z;
        FileInputStream fileInputStream;
        FileOutputStream fileOutputStream;
        long j3;
        long j4;
        int i7;
        int i8;
        SinglePackageBackupRunner singlePackageBackupRunner = null;
        long j5 = 0;
        int i9 = 0;
        try {
        } catch (Exception e) {
            e = e;
            i = -2003;
            i3 = -1000;
        } catch (Throwable th3) {
            th = th3;
            i = -2003;
            i2 = 0;
        }
        try {
            try {
                if (this.mUserBackupManagerService.isEnabled()) {
                    try {
                    } catch (Exception e2) {
                        i = -2003;
                        i3 = -1000;
                        exc = e2;
                        parcelFileDescriptorArr2 = null;
                        parcelFileDescriptorArr = null;
                        j5 = 0;
                    } catch (Throwable th4) {
                        i = -2003;
                        th = th4;
                        parcelFileDescriptorArr2 = null;
                        parcelFileDescriptorArr = null;
                        j5 = 0;
                    }
                    if (this.mUserBackupManagerService.isSetupComplete()) {
                        BackupTransportClient connect = this.mTransportConnection.connect("PFTBT.run()");
                        ?? r7 = 1;
                        try {
                            try {
                            } catch (Exception e3) {
                                exc = e3;
                                parcelFileDescriptorArr2 = null;
                                parcelFileDescriptorArr = null;
                            }
                            if (connect != null) {
                                if (this.mMonitor == null) {
                                    try {
                                        this.mMonitor = connect.getBackupManagerMonitor();
                                    } catch (RemoteException unused) {
                                        Slog.i("PFTBT", "Failed to retrieve monitor from transport");
                                    }
                                }
                                int size = this.mPackages.size();
                                byte[] bArr2 = new byte[BackupAndRestoreFeatureFlags.getFullBackupWriteToTransportBufferSizeBytes()];
                                long j6 = 0;
                                parcelFileDescriptorArr2 = null;
                                parcelFileDescriptorArr = null;
                                int i10 = 0;
                                while (true) {
                                    if (i10 >= size) {
                                        break;
                                    }
                                    try {
                                        this.mBackupRunner = singlePackageBackupRunner;
                                        PackageInfo packageInfo2 = this.mPackages.get(i10);
                                        String str3 = packageInfo2.packageName;
                                        Slog.i("PFTBT", "Initiating full-data transport backup of " + str3 + " token: " + this.mCurrentOpToken);
                                        EventLog.writeEvent(2840, str3);
                                        ParcelFileDescriptor[] createPipe = ParcelFileDescriptor.createPipe();
                                        try {
                                            int i11 = this.mUserInitiated ? r7 : i9;
                                            synchronized (this.mCancelLock) {
                                                try {
                                                    if (this.mCancelAll) {
                                                        break;
                                                    }
                                                    int performFullBackup = connect.performFullBackup(packageInfo2, createPipe[i9], i11);
                                                    if (performFullBackup == 0) {
                                                        try {
                                                            long backupQuota = connect.getBackupQuota(packageInfo2.packageName, r7);
                                                            ParcelFileDescriptor[] createPipe2 = ParcelFileDescriptor.createPipe();
                                                            try {
                                                                i5 = i10;
                                                                bArr = bArr2;
                                                                i6 = size;
                                                                backupTransportClient = connect;
                                                                parcelFileDescriptorArr4 = createPipe;
                                                                boolean z2 = r7;
                                                                str = str3;
                                                                packageInfo = packageInfo2;
                                                                try {
                                                                    this.mBackupRunner = new SinglePackageBackupRunner(createPipe2[r7], packageInfo2, this.mTransportConnection, backupQuota, this.mBackupRunnerOpToken, connect.getTransportFlags());
                                                                    createPipe2[z2 ? 1 : 0].close();
                                                                    createPipe2[z2 ? 1 : 0] = null;
                                                                    this.mIsDoingBackup = z2;
                                                                    j = backupQuota;
                                                                    parcelFileDescriptorArr2 = createPipe2;
                                                                    r11 = z2;
                                                                } catch (Throwable th5) {
                                                                    th2 = th5;
                                                                    parcelFileDescriptorArr2 = createPipe2;
                                                                    parcelFileDescriptorArr3 = parcelFileDescriptorArr4;
                                                                }
                                                            } catch (Throwable th6) {
                                                                th2 = th6;
                                                                parcelFileDescriptorArr3 = createPipe;
                                                                parcelFileDescriptorArr2 = createPipe2;
                                                            }
                                                        } catch (Throwable th7) {
                                                            th2 = th7;
                                                            parcelFileDescriptorArr3 = createPipe;
                                                        }
                                                    } else {
                                                        i5 = i10;
                                                        bArr = bArr2;
                                                        i6 = size;
                                                        backupTransportClient = connect;
                                                        str = str3;
                                                        packageInfo = packageInfo2;
                                                        parcelFileDescriptorArr4 = createPipe;
                                                        r11 = r7;
                                                        j = Long.MAX_VALUE;
                                                    }
                                                    try {
                                                        if (performFullBackup == 0) {
                                                            try {
                                                                parcelFileDescriptorArr4[0].close();
                                                                parcelFileDescriptorArr4[0] = null;
                                                                new Thread(this.mBackupRunner, "package-backup-bridge").start();
                                                                FileInputStream fileInputStream2 = new FileInputStream(parcelFileDescriptorArr2[0].getFileDescriptor());
                                                                FileOutputStream fileOutputStream2 = new FileOutputStream(parcelFileDescriptorArr4[r11].getFileDescriptor());
                                                                long preflightResultBlocking = this.mBackupRunner.getPreflightResultBlocking();
                                                                int i12 = (preflightResultBlocking > 0L ? 1 : (preflightResultBlocking == 0L ? 0 : -1));
                                                                if (i12 < 0) {
                                                                    this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 16, this.mCurrentPackage, 3, BackupManagerMonitorUtils.putMonitoringExtra((Bundle) null, "android.app.backup.extra.LOG_PREFLIGHT_ERROR", preflightResultBlocking));
                                                                    i7 = (int) preflightResultBlocking;
                                                                    backupTransportClient2 = backupTransportClient;
                                                                    str2 = str;
                                                                } else {
                                                                    long j7 = 0;
                                                                    byte[] bArr3 = bArr;
                                                                    while (true) {
                                                                        int read = fileInputStream2.read(bArr3);
                                                                        if (read > 0) {
                                                                            fileOutputStream2.write(bArr3, 0, read);
                                                                            synchronized (this.mCancelLock) {
                                                                                if (this.mCancelAll) {
                                                                                    backupTransportClient2 = backupTransportClient;
                                                                                } else {
                                                                                    backupTransportClient2 = backupTransportClient;
                                                                                    performFullBackup = backupTransportClient2.sendBackupData(read);
                                                                                }
                                                                            }
                                                                            fileInputStream = fileInputStream2;
                                                                            fileOutputStream = fileOutputStream2;
                                                                            long j8 = j7 + read;
                                                                            IBackupObserver iBackupObserver = this.mBackupObserver;
                                                                            if (iBackupObserver != null && i12 > 0) {
                                                                                bArr = bArr3;
                                                                                BackupProgress backupProgress = new BackupProgress(preflightResultBlocking, j8);
                                                                                j7 = j8;
                                                                                str2 = str;
                                                                                BackupObserverUtils.sendBackupOnUpdate(iBackupObserver, str2, backupProgress);
                                                                                j3 = preflightResultBlocking;
                                                                                j4 = j7;
                                                                                if (read > 0 || performFullBackup != 0) {
                                                                                    break;
                                                                                }
                                                                                backupTransportClient = backupTransportClient2;
                                                                                str = str2;
                                                                                fileInputStream2 = fileInputStream;
                                                                                bArr3 = bArr;
                                                                                fileOutputStream2 = fileOutputStream;
                                                                                preflightResultBlocking = j3;
                                                                                j7 = j4;
                                                                            } else {
                                                                                j7 = j8;
                                                                                bArr = bArr3;
                                                                            }
                                                                        } else {
                                                                            fileInputStream = fileInputStream2;
                                                                            fileOutputStream = fileOutputStream2;
                                                                            bArr = bArr3;
                                                                            backupTransportClient2 = backupTransportClient;
                                                                        }
                                                                        str2 = str;
                                                                        j3 = preflightResultBlocking;
                                                                        j4 = j7;
                                                                        if (read > 0) {
                                                                            break;
                                                                            break;
                                                                        }
                                                                        backupTransportClient = backupTransportClient2;
                                                                        str = str2;
                                                                        fileInputStream2 = fileInputStream;
                                                                        bArr3 = bArr;
                                                                        fileOutputStream2 = fileOutputStream;
                                                                        preflightResultBlocking = j3;
                                                                        j7 = j4;
                                                                    }
                                                                    if (performFullBackup == -1005) {
                                                                        Slog.w("PFTBT", "Package hit quota limit in-flight " + str2 + ": " + j4 + " of " + j);
                                                                        this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 18, this.mCurrentPackage, 1, null);
                                                                        this.mBackupRunner.sendQuotaExceeded(j4, j);
                                                                    }
                                                                    i7 = performFullBackup;
                                                                }
                                                                int backupResultBlocking = this.mBackupRunner.getBackupResultBlocking();
                                                                synchronized (this.mCancelLock) {
                                                                    this.mIsDoingBackup = false;
                                                                    if (!this.mCancelAll) {
                                                                        if (backupResultBlocking == 0) {
                                                                            int finishBackup = backupTransportClient2.finishBackup();
                                                                            if (i7 == 0) {
                                                                                i7 = finishBackup;
                                                                            }
                                                                        } else {
                                                                            backupTransportClient2.cancelFullBackup();
                                                                        }
                                                                    }
                                                                }
                                                                performFullBackup = (i7 != 0 || backupResultBlocking == 0) ? i7 : backupResultBlocking;
                                                                if (performFullBackup != 0) {
                                                                    Slog.w("PFTBT", "Error " + performFullBackup + " backing up " + str2);
                                                                }
                                                                j2 = backupTransportClient2.requestFullBackupTime();
                                                                try {
                                                                    Slog.i("PFTBT", "Transport suggested backoff=" + j2);
                                                                } catch (Exception e4) {
                                                                    e = e4;
                                                                    j5 = j2;
                                                                    parcelFileDescriptorArr = parcelFileDescriptorArr4;
                                                                    i = -2003;
                                                                    i3 = -1000;
                                                                    exc = e;
                                                                    try {
                                                                        Slog.w("PFTBT", "Exception trying full transport backup", exc);
                                                                        this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 19, this.mCurrentPackage, 3, BackupManagerMonitorUtils.putMonitoringExtra((Bundle) null, "android.app.backup.extra.LOG_EXCEPTION_FULL_BACKUP", Log.getStackTraceString(exc)));
                                                                        if (!this.mCancelAll) {
                                                                        }
                                                                        Slog.i("PFTBT", "Full backup completed with status: " + i4);
                                                                        BackupObserverUtils.sendBackupFinished(this.mBackupObserver, i4);
                                                                        cleanUpPipes(parcelFileDescriptorArr);
                                                                        cleanUpPipes(parcelFileDescriptorArr2);
                                                                        unregisterTask();
                                                                        fullBackupJob = this.mJob;
                                                                        if (fullBackupJob != null) {
                                                                        }
                                                                        synchronized (this.mUserBackupManagerService.getQueueLock()) {
                                                                        }
                                                                    } catch (Throwable th8) {
                                                                        th = th8;
                                                                        i2 = i3;
                                                                        if (!this.mCancelAll) {
                                                                        }
                                                                        Slog.i("PFTBT", "Full backup completed with status: " + r13);
                                                                        BackupObserverUtils.sendBackupFinished(this.mBackupObserver, r13);
                                                                        cleanUpPipes(parcelFileDescriptorArr);
                                                                        cleanUpPipes(parcelFileDescriptorArr2);
                                                                        unregisterTask();
                                                                        fullBackupJob2 = this.mJob;
                                                                        if (fullBackupJob2 != null) {
                                                                        }
                                                                        synchronized (this.mUserBackupManagerService.getQueueLock()) {
                                                                        }
                                                                    }
                                                                } catch (Throwable th9) {
                                                                    th = th9;
                                                                    j5 = j2;
                                                                    parcelFileDescriptorArr = parcelFileDescriptorArr4;
                                                                    i = -2003;
                                                                    i2 = 0;
                                                                    th = th;
                                                                    if (!this.mCancelAll) {
                                                                    }
                                                                    Slog.i("PFTBT", "Full backup completed with status: " + r13);
                                                                    BackupObserverUtils.sendBackupFinished(this.mBackupObserver, r13);
                                                                    cleanUpPipes(parcelFileDescriptorArr);
                                                                    cleanUpPipes(parcelFileDescriptorArr2);
                                                                    unregisterTask();
                                                                    fullBackupJob2 = this.mJob;
                                                                    if (fullBackupJob2 != null) {
                                                                    }
                                                                    synchronized (this.mUserBackupManagerService.getQueueLock()) {
                                                                    }
                                                                }
                                                            } catch (Exception e5) {
                                                                exc = e5;
                                                                j5 = j6;
                                                                parcelFileDescriptorArr = parcelFileDescriptorArr4;
                                                                i = -2003;
                                                                i3 = -1000;
                                                                Slog.w("PFTBT", "Exception trying full transport backup", exc);
                                                                this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 19, this.mCurrentPackage, 3, BackupManagerMonitorUtils.putMonitoringExtra((Bundle) null, "android.app.backup.extra.LOG_EXCEPTION_FULL_BACKUP", Log.getStackTraceString(exc)));
                                                                if (!this.mCancelAll) {
                                                                }
                                                                Slog.i("PFTBT", "Full backup completed with status: " + i4);
                                                                BackupObserverUtils.sendBackupFinished(this.mBackupObserver, i4);
                                                                cleanUpPipes(parcelFileDescriptorArr);
                                                                cleanUpPipes(parcelFileDescriptorArr2);
                                                                unregisterTask();
                                                                fullBackupJob = this.mJob;
                                                                if (fullBackupJob != null) {
                                                                }
                                                                synchronized (this.mUserBackupManagerService.getQueueLock()) {
                                                                }
                                                            } catch (Throwable th10) {
                                                                th = th10;
                                                                j5 = j6;
                                                                parcelFileDescriptorArr = parcelFileDescriptorArr4;
                                                                i = -2003;
                                                                i2 = 0;
                                                                if (!this.mCancelAll) {
                                                                }
                                                                Slog.i("PFTBT", "Full backup completed with status: " + r13);
                                                                BackupObserverUtils.sendBackupFinished(this.mBackupObserver, r13);
                                                                cleanUpPipes(parcelFileDescriptorArr);
                                                                cleanUpPipes(parcelFileDescriptorArr2);
                                                                unregisterTask();
                                                                fullBackupJob2 = this.mJob;
                                                                if (fullBackupJob2 != null) {
                                                                }
                                                                synchronized (this.mUserBackupManagerService.getQueueLock()) {
                                                                }
                                                            }
                                                        } else {
                                                            backupTransportClient2 = backupTransportClient;
                                                            str2 = str;
                                                            j2 = j6;
                                                        }
                                                        try {
                                                            if (this.mUpdateSchedule) {
                                                                this.mUserBackupManagerService.enqueueFullBackup(str2, System.currentTimeMillis());
                                                            }
                                                            if (performFullBackup == -1002) {
                                                                BackupObserverUtils.sendBackupOnPackageResult(this.mBackupObserver, str2, -1002);
                                                                Slog.i("PFTBT", "Transport rejected backup of " + str2 + ", skipping");
                                                                z = true;
                                                                EventLog.writeEvent(2841, str2, "transport rejected");
                                                                if (this.mBackupRunner != null) {
                                                                    this.mUserBackupManagerService.tearDownAgentAndKill(packageInfo.applicationInfo);
                                                                }
                                                            } else {
                                                                z = true;
                                                                if (performFullBackup == -1005) {
                                                                    BackupObserverUtils.sendBackupOnPackageResult(this.mBackupObserver, str2, -1005);
                                                                    Slog.i("PFTBT", "Transport quota exceeded for package: " + str2);
                                                                    EventLog.writeEvent(2845, str2);
                                                                    this.mUserBackupManagerService.tearDownAgentAndKill(packageInfo.applicationInfo);
                                                                } else if (performFullBackup == -1003) {
                                                                    BackupObserverUtils.sendBackupOnPackageResult(this.mBackupObserver, str2, -1003);
                                                                    Slog.w("PFTBT", "Application failure for package: " + str2);
                                                                    EventLog.writeEvent(2823, str2);
                                                                    this.mUserBackupManagerService.tearDownAgentAndKill(packageInfo.applicationInfo);
                                                                } else {
                                                                    i = -2003;
                                                                    if (performFullBackup == -2003) {
                                                                        try {
                                                                            BackupObserverUtils.sendBackupOnPackageResult(this.mBackupObserver, str2, -2003);
                                                                            Slog.w("PFTBT", "Backup cancelled. package=" + str2 + ", cancelAll=" + this.mCancelAll);
                                                                            EventLog.writeEvent(2846, str2);
                                                                            this.mUserBackupManagerService.tearDownAgentAndKill(packageInfo.applicationInfo);
                                                                            parcelFileDescriptorArr5 = parcelFileDescriptorArr4;
                                                                            i3 = -1000;
                                                                            cleanUpPipes(parcelFileDescriptorArr5);
                                                                            cleanUpPipes(parcelFileDescriptorArr2);
                                                                            if (packageInfo.applicationInfo != null) {
                                                                                try {
                                                                                    Slog.i("PFTBT", "Unbinding agent in " + str2);
                                                                                    try {
                                                                                        this.mUserBackupManagerService.getActivityManager().unbindBackupAgent(packageInfo.applicationInfo);
                                                                                    } catch (RemoteException unused2) {
                                                                                    }
                                                                                } catch (Exception e6) {
                                                                                    e = e6;
                                                                                    long j9 = j2;
                                                                                    exc = e;
                                                                                    parcelFileDescriptorArr = parcelFileDescriptorArr5;
                                                                                    j5 = j9;
                                                                                    Slog.w("PFTBT", "Exception trying full transport backup", exc);
                                                                                    this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 19, this.mCurrentPackage, 3, BackupManagerMonitorUtils.putMonitoringExtra((Bundle) null, "android.app.backup.extra.LOG_EXCEPTION_FULL_BACKUP", Log.getStackTraceString(exc)));
                                                                                    if (!this.mCancelAll) {
                                                                                    }
                                                                                    Slog.i("PFTBT", "Full backup completed with status: " + i4);
                                                                                    BackupObserverUtils.sendBackupFinished(this.mBackupObserver, i4);
                                                                                    cleanUpPipes(parcelFileDescriptorArr);
                                                                                    cleanUpPipes(parcelFileDescriptorArr2);
                                                                                    unregisterTask();
                                                                                    fullBackupJob = this.mJob;
                                                                                    if (fullBackupJob != null) {
                                                                                    }
                                                                                    synchronized (this.mUserBackupManagerService.getQueueLock()) {
                                                                                    }
                                                                                } catch (Throwable th11) {
                                                                                    th = th11;
                                                                                    i2 = 0;
                                                                                    long j10 = j2;
                                                                                    th = th;
                                                                                    parcelFileDescriptorArr = parcelFileDescriptorArr5;
                                                                                    j5 = j10;
                                                                                    if (!this.mCancelAll) {
                                                                                    }
                                                                                    Slog.i("PFTBT", "Full backup completed with status: " + r13);
                                                                                    BackupObserverUtils.sendBackupFinished(this.mBackupObserver, r13);
                                                                                    cleanUpPipes(parcelFileDescriptorArr);
                                                                                    cleanUpPipes(parcelFileDescriptorArr2);
                                                                                    unregisterTask();
                                                                                    fullBackupJob2 = this.mJob;
                                                                                    if (fullBackupJob2 != null) {
                                                                                    }
                                                                                    synchronized (this.mUserBackupManagerService.getQueueLock()) {
                                                                                    }
                                                                                }
                                                                            }
                                                                            j6 = j2;
                                                                            connect = backupTransportClient2;
                                                                            i10 = i5 + 1;
                                                                            r7 = z;
                                                                            parcelFileDescriptorArr = parcelFileDescriptorArr5;
                                                                            size = i6;
                                                                            bArr2 = bArr;
                                                                            singlePackageBackupRunner = null;
                                                                            i9 = 0;
                                                                        } catch (Exception e7) {
                                                                            e = e7;
                                                                            j5 = j2;
                                                                            parcelFileDescriptorArr = parcelFileDescriptorArr4;
                                                                            i3 = -1000;
                                                                            exc = e;
                                                                            Slog.w("PFTBT", "Exception trying full transport backup", exc);
                                                                            this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 19, this.mCurrentPackage, 3, BackupManagerMonitorUtils.putMonitoringExtra((Bundle) null, "android.app.backup.extra.LOG_EXCEPTION_FULL_BACKUP", Log.getStackTraceString(exc)));
                                                                            if (!this.mCancelAll) {
                                                                            }
                                                                            Slog.i("PFTBT", "Full backup completed with status: " + i4);
                                                                            BackupObserverUtils.sendBackupFinished(this.mBackupObserver, i4);
                                                                            cleanUpPipes(parcelFileDescriptorArr);
                                                                            cleanUpPipes(parcelFileDescriptorArr2);
                                                                            unregisterTask();
                                                                            fullBackupJob = this.mJob;
                                                                            if (fullBackupJob != null) {
                                                                            }
                                                                            synchronized (this.mUserBackupManagerService.getQueueLock()) {
                                                                            }
                                                                        } catch (Throwable th12) {
                                                                            th = th12;
                                                                            j5 = j2;
                                                                            parcelFileDescriptorArr = parcelFileDescriptorArr4;
                                                                            i2 = 0;
                                                                            th = th;
                                                                            if (!this.mCancelAll) {
                                                                            }
                                                                            Slog.i("PFTBT", "Full backup completed with status: " + r13);
                                                                            BackupObserverUtils.sendBackupFinished(this.mBackupObserver, r13);
                                                                            cleanUpPipes(parcelFileDescriptorArr);
                                                                            cleanUpPipes(parcelFileDescriptorArr2);
                                                                            unregisterTask();
                                                                            fullBackupJob2 = this.mJob;
                                                                            if (fullBackupJob2 != null) {
                                                                            }
                                                                            synchronized (this.mUserBackupManagerService.getQueueLock()) {
                                                                            }
                                                                        }
                                                                    } else if (performFullBackup != 0) {
                                                                        try {
                                                                            try {
                                                                                break;
                                                                            } catch (Throwable th13) {
                                                                                th = th13;
                                                                                parcelFileDescriptorArr5 = parcelFileDescriptorArr4;
                                                                                i2 = 0;
                                                                                long j102 = j2;
                                                                                th = th;
                                                                                parcelFileDescriptorArr = parcelFileDescriptorArr5;
                                                                                j5 = j102;
                                                                                if (!this.mCancelAll) {
                                                                                }
                                                                                Slog.i("PFTBT", "Full backup completed with status: " + r13);
                                                                                BackupObserverUtils.sendBackupFinished(this.mBackupObserver, r13);
                                                                                cleanUpPipes(parcelFileDescriptorArr);
                                                                                cleanUpPipes(parcelFileDescriptorArr2);
                                                                                unregisterTask();
                                                                                fullBackupJob2 = this.mJob;
                                                                                if (fullBackupJob2 != null) {
                                                                                }
                                                                                synchronized (this.mUserBackupManagerService.getQueueLock()) {
                                                                                }
                                                                            }
                                                                        } catch (Exception e8) {
                                                                            e = e8;
                                                                            parcelFileDescriptorArr5 = parcelFileDescriptorArr4;
                                                                            i3 = -1000;
                                                                            long j92 = j2;
                                                                            exc = e;
                                                                            parcelFileDescriptorArr = parcelFileDescriptorArr5;
                                                                            j5 = j92;
                                                                            Slog.w("PFTBT", "Exception trying full transport backup", exc);
                                                                            this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 19, this.mCurrentPackage, 3, BackupManagerMonitorUtils.putMonitoringExtra((Bundle) null, "android.app.backup.extra.LOG_EXCEPTION_FULL_BACKUP", Log.getStackTraceString(exc)));
                                                                            if (!this.mCancelAll) {
                                                                            }
                                                                            Slog.i("PFTBT", "Full backup completed with status: " + i4);
                                                                            BackupObserverUtils.sendBackupFinished(this.mBackupObserver, i4);
                                                                            cleanUpPipes(parcelFileDescriptorArr);
                                                                            cleanUpPipes(parcelFileDescriptorArr2);
                                                                            unregisterTask();
                                                                            fullBackupJob = this.mJob;
                                                                            if (fullBackupJob != null) {
                                                                            }
                                                                            synchronized (this.mUserBackupManagerService.getQueueLock()) {
                                                                            }
                                                                        }
                                                                    } else {
                                                                        parcelFileDescriptorArr5 = parcelFileDescriptorArr4;
                                                                        i3 = -1000;
                                                                        BackupObserverUtils.sendBackupOnPackageResult(this.mBackupObserver, str2, 0);
                                                                        EventLog.writeEvent(2843, str2);
                                                                        this.mUserBackupManagerService.logBackupComplete(str2);
                                                                        cleanUpPipes(parcelFileDescriptorArr5);
                                                                        cleanUpPipes(parcelFileDescriptorArr2);
                                                                        if (packageInfo.applicationInfo != null) {
                                                                        }
                                                                        j6 = j2;
                                                                        connect = backupTransportClient2;
                                                                        i10 = i5 + 1;
                                                                        r7 = z;
                                                                        parcelFileDescriptorArr = parcelFileDescriptorArr5;
                                                                        size = i6;
                                                                        bArr2 = bArr;
                                                                        singlePackageBackupRunner = null;
                                                                        i9 = 0;
                                                                    }
                                                                }
                                                            }
                                                            parcelFileDescriptorArr5 = parcelFileDescriptorArr4;
                                                            i = -2003;
                                                            i3 = -1000;
                                                            cleanUpPipes(parcelFileDescriptorArr5);
                                                            cleanUpPipes(parcelFileDescriptorArr2);
                                                            if (packageInfo.applicationInfo != null) {
                                                            }
                                                            j6 = j2;
                                                            connect = backupTransportClient2;
                                                            i10 = i5 + 1;
                                                            r7 = z;
                                                            parcelFileDescriptorArr = parcelFileDescriptorArr5;
                                                            size = i6;
                                                            bArr2 = bArr;
                                                            singlePackageBackupRunner = null;
                                                            i9 = 0;
                                                        } catch (Exception e9) {
                                                            e = e9;
                                                            parcelFileDescriptorArr5 = parcelFileDescriptorArr4;
                                                            i = -2003;
                                                        } catch (Throwable th14) {
                                                            th = th14;
                                                            parcelFileDescriptorArr5 = parcelFileDescriptorArr4;
                                                            i = -2003;
                                                        }
                                                    } catch (Throwable th15) {
                                                        th = th15;
                                                        parcelFileDescriptorArr3 = parcelFileDescriptorArr4;
                                                        i = -2003;
                                                        i3 = -1000;
                                                        th2 = th;
                                                        throw th2;
                                                    }
                                                    th2 = th7;
                                                    parcelFileDescriptorArr3 = createPipe;
                                                    i = -2003;
                                                    i3 = -1000;
                                                } catch (Throwable th16) {
                                                    th = th16;
                                                    parcelFileDescriptorArr3 = createPipe;
                                                }
                                                try {
                                                } catch (Throwable th17) {
                                                    th = th17;
                                                    th2 = th;
                                                    throw th2;
                                                }
                                                try {
                                                    throw th2;
                                                } catch (Exception e10) {
                                                    e = e10;
                                                    exc = e;
                                                    parcelFileDescriptorArr = parcelFileDescriptorArr3;
                                                    j5 = j6;
                                                    Slog.w("PFTBT", "Exception trying full transport backup", exc);
                                                    this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 19, this.mCurrentPackage, 3, BackupManagerMonitorUtils.putMonitoringExtra((Bundle) null, "android.app.backup.extra.LOG_EXCEPTION_FULL_BACKUP", Log.getStackTraceString(exc)));
                                                    if (!this.mCancelAll) {
                                                    }
                                                    Slog.i("PFTBT", "Full backup completed with status: " + i4);
                                                    BackupObserverUtils.sendBackupFinished(this.mBackupObserver, i4);
                                                    cleanUpPipes(parcelFileDescriptorArr);
                                                    cleanUpPipes(parcelFileDescriptorArr2);
                                                    unregisterTask();
                                                    fullBackupJob = this.mJob;
                                                    if (fullBackupJob != null) {
                                                    }
                                                    synchronized (this.mUserBackupManagerService.getQueueLock()) {
                                                    }
                                                } catch (Throwable th18) {
                                                    th = th18;
                                                    th = th;
                                                    parcelFileDescriptorArr = parcelFileDescriptorArr3;
                                                    j5 = j6;
                                                    i2 = 0;
                                                    if (!this.mCancelAll) {
                                                    }
                                                    Slog.i("PFTBT", "Full backup completed with status: " + r13);
                                                    BackupObserverUtils.sendBackupFinished(this.mBackupObserver, r13);
                                                    cleanUpPipes(parcelFileDescriptorArr);
                                                    cleanUpPipes(parcelFileDescriptorArr2);
                                                    unregisterTask();
                                                    fullBackupJob2 = this.mJob;
                                                    if (fullBackupJob2 != null) {
                                                    }
                                                    synchronized (this.mUserBackupManagerService.getQueueLock()) {
                                                    }
                                                }
                                            }
                                        } catch (Exception e11) {
                                            e = e11;
                                            parcelFileDescriptorArr3 = createPipe;
                                            i = -2003;
                                            i3 = -1000;
                                        } catch (Throwable th19) {
                                            th = th19;
                                            parcelFileDescriptorArr3 = createPipe;
                                            i = -2003;
                                        }
                                    } catch (Exception e12) {
                                        i = -2003;
                                        i3 = -1000;
                                        exc = e12;
                                    } catch (Throwable th20) {
                                        i = -2003;
                                        th = th20;
                                    }
                                }
                                int i13 = this.mCancelAll ? -2003 : 0;
                                Slog.i("PFTBT", "Full backup completed with status: " + i13);
                                BackupObserverUtils.sendBackupFinished(this.mBackupObserver, i13);
                                cleanUpPipes(parcelFileDescriptorArr);
                                cleanUpPipes(parcelFileDescriptorArr2);
                                unregisterTask();
                                FullBackupJob fullBackupJob3 = this.mJob;
                                if (fullBackupJob3 != null) {
                                    fullBackupJob3.finishBackupPass(this.mUserId);
                                }
                                synchronized (this.mUserBackupManagerService.getQueueLock()) {
                                    this.mUserBackupManagerService.setRunningFullBackupTask(null);
                                }
                                this.mListener.onFinished("PFTBT.run()");
                                this.mLatch.countDown();
                                if (this.mUpdateSchedule) {
                                    this.mUserBackupManagerService.scheduleNextFullBackupJob(j6);
                                }
                                Slog.i("PFTBT", "Full data backup pass finished.");
                                this.mUserBackupManagerService.getWakelock().release();
                                return;
                            }
                            Slog.w("PFTBT", "Transport not present; full data backup not performed");
                            try {
                                this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 15, this.mCurrentPackage, 1, null);
                                int i14 = this.mCancelAll ? -2003 : -1000;
                                Slog.i("PFTBT", "Full backup completed with status: " + i14);
                                BackupObserverUtils.sendBackupFinished(this.mBackupObserver, i14);
                                cleanUpPipes(null);
                                cleanUpPipes(null);
                                unregisterTask();
                                FullBackupJob fullBackupJob4 = this.mJob;
                                if (fullBackupJob4 != null) {
                                    fullBackupJob4.finishBackupPass(this.mUserId);
                                }
                                synchronized (this.mUserBackupManagerService.getQueueLock()) {
                                    this.mUserBackupManagerService.setRunningFullBackupTask(null);
                                }
                                this.mListener.onFinished("PFTBT.run()");
                                this.mLatch.countDown();
                                if (this.mUpdateSchedule) {
                                    this.mUserBackupManagerService.scheduleNextFullBackupJob(0L);
                                }
                            } catch (Throwable th21) {
                                th = th21;
                                parcelFileDescriptorArr2 = null;
                                parcelFileDescriptorArr = null;
                                i = -2003;
                                i2 = -1000;
                                int i15 = !this.mCancelAll ? i : i2;
                                Slog.i("PFTBT", "Full backup completed with status: " + i15);
                                BackupObserverUtils.sendBackupFinished(this.mBackupObserver, i15);
                                cleanUpPipes(parcelFileDescriptorArr);
                                cleanUpPipes(parcelFileDescriptorArr2);
                                unregisterTask();
                                fullBackupJob2 = this.mJob;
                                if (fullBackupJob2 != null) {
                                    fullBackupJob2.finishBackupPass(this.mUserId);
                                }
                                synchronized (this.mUserBackupManagerService.getQueueLock()) {
                                    this.mUserBackupManagerService.setRunningFullBackupTask(null);
                                }
                                this.mListener.onFinished("PFTBT.run()");
                                this.mLatch.countDown();
                                if (this.mUpdateSchedule) {
                                    this.mUserBackupManagerService.scheduleNextFullBackupJob(j5);
                                }
                                Slog.i("PFTBT", "Full data backup pass finished.");
                                this.mUserBackupManagerService.getWakelock().release();
                                throw th;
                            }
                            Slog.i("PFTBT", "Full data backup pass finished.");
                            this.mUserBackupManagerService.getWakelock().release();
                            return;
                        } catch (Throwable th22) {
                            th = th22;
                            parcelFileDescriptorArr2 = null;
                            parcelFileDescriptorArr = null;
                            i2 = 0;
                            i = -2003;
                        }
                    }
                }
                this.mUpdateSchedule = false;
                int i16 = this.mCancelAll ? -2003 : -2001;
                Slog.i("PFTBT", "Full backup completed with status: " + i16);
                BackupObserverUtils.sendBackupFinished(this.mBackupObserver, i16);
                cleanUpPipes(null);
                cleanUpPipes(null);
                unregisterTask();
                FullBackupJob fullBackupJob5 = this.mJob;
                if (fullBackupJob5 != null) {
                    fullBackupJob5.finishBackupPass(this.mUserId);
                }
                synchronized (this.mUserBackupManagerService.getQueueLock()) {
                    this.mUserBackupManagerService.setRunningFullBackupTask(null);
                }
                this.mListener.onFinished("PFTBT.run()");
                this.mLatch.countDown();
                if (this.mUpdateSchedule) {
                    this.mUserBackupManagerService.scheduleNextFullBackupJob(0L);
                }
                Slog.i("PFTBT", "Full data backup pass finished.");
                this.mUserBackupManagerService.getWakelock().release();
                return;
            } catch (Throwable th23) {
                th = th23;
                i2 = i8;
                j5 = 0;
                parcelFileDescriptorArr2 = null;
                parcelFileDescriptorArr = null;
                th = th;
                if (!this.mCancelAll) {
                }
                Slog.i("PFTBT", "Full backup completed with status: " + i15);
                BackupObserverUtils.sendBackupFinished(this.mBackupObserver, i15);
                cleanUpPipes(parcelFileDescriptorArr);
                cleanUpPipes(parcelFileDescriptorArr2);
                unregisterTask();
                fullBackupJob2 = this.mJob;
                if (fullBackupJob2 != null) {
                }
                synchronized (this.mUserBackupManagerService.getQueueLock()) {
                }
            }
            Slog.i("PFTBT", "full backup requested but enabled=" + this.mUserBackupManagerService.isEnabled() + " setupComplete=" + this.mUserBackupManagerService.isSetupComplete() + "; ignoring");
            this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, this.mUserBackupManagerService.isSetupComplete() ? 13 : 14, null, 3, null);
            i8 = 0;
        } catch (Throwable th24) {
            th = th24;
            i8 = 0;
        }
        i = -2003;
        i3 = -1000;
    }

    public void cleanUpPipes(ParcelFileDescriptor[] parcelFileDescriptorArr) {
        if (parcelFileDescriptorArr != null) {
            ParcelFileDescriptor parcelFileDescriptor = parcelFileDescriptorArr[0];
            if (parcelFileDescriptor != null) {
                parcelFileDescriptorArr[0] = null;
                try {
                    parcelFileDescriptor.close();
                } catch (IOException unused) {
                    Slog.w("PFTBT", "Unable to close pipe!");
                }
            }
            ParcelFileDescriptor parcelFileDescriptor2 = parcelFileDescriptorArr[1];
            if (parcelFileDescriptor2 != null) {
                parcelFileDescriptorArr[1] = null;
                try {
                    parcelFileDescriptor2.close();
                } catch (IOException unused2) {
                    Slog.w("PFTBT", "Unable to close pipe!");
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public class SinglePackageBackupPreflight implements BackupRestoreTask, FullBackupPreflight {
        public final int mCurrentOpToken;
        public final long mQuota;
        public final TransportConnection mTransportConnection;
        public final int mTransportFlags;
        public final AtomicLong mResult = new AtomicLong(-1003);
        public final CountDownLatch mLatch = new CountDownLatch(1);

        @Override // com.android.server.backup.BackupRestoreTask
        public void execute() {
        }

        public SinglePackageBackupPreflight(TransportConnection transportConnection, long j, int i, int i2) {
            this.mTransportConnection = transportConnection;
            this.mQuota = j;
            this.mCurrentOpToken = i;
            this.mTransportFlags = i2;
        }

        @Override // com.android.server.backup.fullbackup.FullBackupPreflight
        public int preflightFullBackup(PackageInfo packageInfo, final IBackupAgent iBackupAgent) {
            long fullBackupAgentTimeoutMillis = PerformFullTransportBackupTask.this.mAgentTimeoutParameters.getFullBackupAgentTimeoutMillis();
            try {
                PerformFullTransportBackupTask.this.mUserBackupManagerService.prepareOperationTimeout(this.mCurrentOpToken, fullBackupAgentTimeoutMillis, this, 0);
                iBackupAgent.doMeasureFullBackup(this.mQuota, this.mCurrentOpToken, PerformFullTransportBackupTask.this.mUserBackupManagerService.getBackupManagerBinder(), this.mTransportFlags);
                this.mLatch.await(fullBackupAgentTimeoutMillis, TimeUnit.MILLISECONDS);
                final long j = this.mResult.get();
                if (j < 0) {
                    return (int) j;
                }
                int checkFullBackupSize = this.mTransportConnection.connectOrThrow("PFTBT$SPBP.preflightFullBackup()").checkFullBackupSize(j);
                if (checkFullBackupSize == -1005) {
                    RemoteCall.execute(new RemoteCallable() { // from class: com.android.server.backup.fullbackup.PerformFullTransportBackupTask$SinglePackageBackupPreflight$$ExternalSyntheticLambda0
                        @Override // com.android.server.backup.remote.RemoteCallable
                        public final void call(Object obj) {
                            PerformFullTransportBackupTask.SinglePackageBackupPreflight.this.lambda$preflightFullBackup$0(iBackupAgent, j, (IBackupCallback) obj);
                        }
                    }, PerformFullTransportBackupTask.this.mAgentTimeoutParameters.getQuotaExceededTimeoutMillis());
                    return checkFullBackupSize;
                }
                return checkFullBackupSize;
            } catch (Exception e) {
                Slog.w("PFTBT", "Exception preflighting " + packageInfo.packageName + ": " + e.getMessage());
                return -1003;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$preflightFullBackup$0(IBackupAgent iBackupAgent, long j, IBackupCallback iBackupCallback) throws RemoteException {
            iBackupAgent.doQuotaExceeded(j, this.mQuota, iBackupCallback);
        }

        @Override // com.android.server.backup.BackupRestoreTask
        public void operationComplete(long j) {
            this.mResult.set(j);
            this.mLatch.countDown();
            PerformFullTransportBackupTask.this.mOperationStorage.removeOperation(this.mCurrentOpToken);
        }

        @Override // com.android.server.backup.BackupRestoreTask
        public void handleCancel(boolean z) {
            this.mResult.set(-1003L);
            this.mLatch.countDown();
            PerformFullTransportBackupTask.this.mOperationStorage.removeOperation(this.mCurrentOpToken);
        }

        public long getExpectedSizeOrErrorCode() {
            try {
                this.mLatch.await(PerformFullTransportBackupTask.this.mAgentTimeoutParameters.getFullBackupAgentTimeoutMillis(), TimeUnit.MILLISECONDS);
                return this.mResult.get();
            } catch (InterruptedException unused) {
                return -1L;
            }
        }
    }

    /* loaded from: classes.dex */
    public class SinglePackageBackupRunner implements Runnable, BackupRestoreTask {
        public final CountDownLatch mBackupLatch;
        public volatile int mBackupResult;
        public final int mCurrentOpToken;
        public FullBackupEngine mEngine;
        public final int mEphemeralToken;
        public volatile boolean mIsCancelled;
        public final ParcelFileDescriptor mOutput;
        public final SinglePackageBackupPreflight mPreflight;
        public final CountDownLatch mPreflightLatch;
        public volatile int mPreflightResult;
        public final long mQuota;
        public final PackageInfo mTarget;
        public final int mTransportFlags;

        @Override // com.android.server.backup.BackupRestoreTask
        public void execute() {
        }

        @Override // com.android.server.backup.BackupRestoreTask
        public void operationComplete(long j) {
        }

        public SinglePackageBackupRunner(ParcelFileDescriptor parcelFileDescriptor, PackageInfo packageInfo, TransportConnection transportConnection, long j, int i, int i2) throws IOException {
            this.mOutput = ParcelFileDescriptor.dup(parcelFileDescriptor.getFileDescriptor());
            this.mTarget = packageInfo;
            this.mCurrentOpToken = i;
            int generateRandomIntegerToken = PerformFullTransportBackupTask.this.mUserBackupManagerService.generateRandomIntegerToken();
            this.mEphemeralToken = generateRandomIntegerToken;
            this.mPreflight = new SinglePackageBackupPreflight(transportConnection, j, generateRandomIntegerToken, i2);
            this.mPreflightLatch = new CountDownLatch(1);
            this.mBackupLatch = new CountDownLatch(1);
            this.mPreflightResult = -1003;
            this.mBackupResult = -1003;
            this.mQuota = j;
            this.mTransportFlags = i2;
            registerTask(packageInfo.packageName);
        }

        public void registerTask(String str) {
            PerformFullTransportBackupTask.this.mOperationStorage.registerOperationForPackages(this.mCurrentOpToken, 0, Sets.newHashSet(new String[]{str}), this, 0);
        }

        public void unregisterTask() {
            PerformFullTransportBackupTask.this.mOperationStorage.removeOperation(this.mCurrentOpToken);
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r1v3, types: [com.android.server.backup.fullbackup.FullBackupEngine] */
        /* JADX WARN: Type inference failed for: r1v8 */
        @Override // java.lang.Runnable
        public void run() {
            String str;
            String fullBackupEngine = new FullBackupEngine(PerformFullTransportBackupTask.this.mUserBackupManagerService, new FileOutputStream(this.mOutput.getFileDescriptor()), this.mPreflight, this.mTarget, false, this, this.mQuota, this.mCurrentOpToken, this.mTransportFlags, PerformFullTransportBackupTask.this.mBackupEligibilityRules, PerformFullTransportBackupTask.this.mMonitor);
            this.mEngine = fullBackupEngine;
            try {
                try {
                    if (!this.mIsCancelled) {
                        this.mPreflightResult = this.mEngine.preflightCheck();
                    }
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    this.mPreflightLatch.countDown();
                    if (this.mPreflightResult == 0 && !this.mIsCancelled) {
                        this.mBackupResult = this.mEngine.backupOnePackage();
                    }
                    unregisterTask();
                    this.mBackupLatch.countDown();
                    try {
                        this.mOutput.close();
                    } catch (IOException unused) {
                        str = "PFTBT";
                        Slog.w(str, "Error closing transport pipe in runner");
                    }
                } catch (Exception e) {
                    e = e;
                    str = "PFTBT";
                    Slog.w(str, "Exception during full package backup of " + this.mTarget.packageName, e);
                    unregisterTask();
                    this.mBackupLatch.countDown();
                    try {
                        this.mOutput.close();
                    } catch (IOException unused2) {
                        Slog.w(str, "Error closing transport pipe in runner");
                    }
                } catch (Throwable th2) {
                    th = th2;
                    fullBackupEngine = "PFTBT";
                    unregisterTask();
                    this.mBackupLatch.countDown();
                    try {
                        this.mOutput.close();
                    } catch (IOException unused3) {
                        Slog.w(fullBackupEngine, "Error closing transport pipe in runner");
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                str = "PFTBT";
                try {
                    this.mPreflightLatch.countDown();
                    throw th3;
                } catch (Exception e2) {
                    e = e2;
                    Slog.w(str, "Exception during full package backup of " + this.mTarget.packageName, e);
                    unregisterTask();
                    this.mBackupLatch.countDown();
                    this.mOutput.close();
                }
            }
        }

        public void sendQuotaExceeded(long j, long j2) {
            this.mEngine.sendQuotaExceeded(j, j2);
        }

        public long getPreflightResultBlocking() {
            try {
                this.mPreflightLatch.await(PerformFullTransportBackupTask.this.mAgentTimeoutParameters.getFullBackupAgentTimeoutMillis(), TimeUnit.MILLISECONDS);
                if (this.mIsCancelled) {
                    return -2003L;
                }
                if (this.mPreflightResult == 0) {
                    return this.mPreflight.getExpectedSizeOrErrorCode();
                }
                return this.mPreflightResult;
            } catch (InterruptedException unused) {
                return -1003L;
            }
        }

        public int getBackupResultBlocking() {
            try {
                this.mBackupLatch.await(PerformFullTransportBackupTask.this.mAgentTimeoutParameters.getFullBackupAgentTimeoutMillis(), TimeUnit.MILLISECONDS);
                if (this.mIsCancelled) {
                    return -2003;
                }
                return this.mBackupResult;
            } catch (InterruptedException unused) {
                return -1003;
            }
        }

        @Override // com.android.server.backup.BackupRestoreTask
        public void handleCancel(boolean z) {
            Slog.w("PFTBT", "Full backup cancel of " + this.mTarget.packageName);
            PerformFullTransportBackupTask performFullTransportBackupTask = PerformFullTransportBackupTask.this;
            performFullTransportBackupTask.mMonitor = BackupManagerMonitorUtils.monitorEvent(performFullTransportBackupTask.mMonitor, 4, PerformFullTransportBackupTask.this.mCurrentPackage, 2, null);
            this.mIsCancelled = true;
            PerformFullTransportBackupTask.this.mUserBackupManagerService.handleCancel(this.mEphemeralToken, z);
            PerformFullTransportBackupTask.this.mUserBackupManagerService.tearDownAgentAndKill(this.mTarget.applicationInfo);
            this.mPreflightLatch.countDown();
            this.mBackupLatch.countDown();
            PerformFullTransportBackupTask.this.mOperationStorage.removeOperation(this.mCurrentOpToken);
        }
    }
}
