package com.android.server.backup.fullbackup;

import android.app.IBackupAgent;
import android.app.backup.FullBackupDataOutput;
import android.app.backup.IBackupCallback;
import android.app.backup.IBackupManagerMonitor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.AppWidgetBackupBridge;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.backup.BackupRestoreTask;
import com.android.server.backup.UserBackupManagerService;
import com.android.server.backup.remote.RemoteCall;
import com.android.server.backup.remote.RemoteCallable;
import com.android.server.backup.utils.BackupEligibilityRules;
import com.android.server.backup.utils.BackupManagerMonitorUtils;
import com.android.server.backup.utils.FullBackupUtils;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
/* loaded from: classes.dex */
public class FullBackupEngine {
    public UserBackupManagerService backupManagerService;
    public IBackupAgent mAgent;
    public final BackupAgentTimeoutParameters mAgentTimeoutParameters;
    public final BackupEligibilityRules mBackupEligibilityRules;
    public boolean mIncludeApks;
    public final IBackupManagerMonitor mMonitor;
    public final int mOpToken;
    public OutputStream mOutput;
    public final PackageInfo mPkg;
    public FullBackupPreflight mPreflightHook;
    public final long mQuota;
    public BackupRestoreTask mTimeoutMonitor;
    public final int mTransportFlags;

    /* loaded from: classes.dex */
    public class FullBackupRunner implements Runnable {
        public final IBackupAgent mAgent;
        public final File mFilesDir;
        public final boolean mIncludeApks;
        public final PackageInfo mPackage;
        public final PackageManager mPackageManager;
        public final ParcelFileDescriptor mPipe;
        public final int mToken;
        public final int mUserId;

        public FullBackupRunner(UserBackupManagerService userBackupManagerService, PackageInfo packageInfo, IBackupAgent iBackupAgent, ParcelFileDescriptor parcelFileDescriptor, int i, boolean z) throws IOException {
            this.mUserId = userBackupManagerService.getUserId();
            this.mPackageManager = FullBackupEngine.this.backupManagerService.getPackageManager();
            this.mPackage = packageInfo;
            this.mAgent = iBackupAgent;
            this.mPipe = ParcelFileDescriptor.dup(parcelFileDescriptor.getFileDescriptor());
            this.mToken = i;
            this.mIncludeApks = z;
            this.mFilesDir = userBackupManagerService.getDataDir();
        }

        @Override // java.lang.Runnable
        public void run() {
            long fullBackupAgentTimeoutMillis;
            try {
                try {
                    try {
                        AppMetadataBackupWriter appMetadataBackupWriter = new AppMetadataBackupWriter(new FullBackupDataOutput(this.mPipe, -1L, FullBackupEngine.this.mTransportFlags), this.mPackageManager);
                        String str = this.mPackage.packageName;
                        boolean equals = "com.android.sharedstoragebackup".equals(str);
                        boolean shouldWriteApk = shouldWriteApk(this.mPackage.applicationInfo, this.mIncludeApks, equals);
                        if (!equals) {
                            File file = new File(this.mFilesDir, "_manifest");
                            appMetadataBackupWriter.backupManifest(this.mPackage, file, this.mFilesDir, shouldWriteApk);
                            file.delete();
                            byte[] widgetState = AppWidgetBackupBridge.getWidgetState(str, this.mUserId);
                            if (widgetState != null && widgetState.length > 0) {
                                File file2 = new File(this.mFilesDir, "_meta");
                                appMetadataBackupWriter.backupWidget(this.mPackage, file2, this.mFilesDir, widgetState);
                                file2.delete();
                            }
                        }
                        if (shouldWriteApk) {
                            appMetadataBackupWriter.backupApk(this.mPackage);
                            appMetadataBackupWriter.backupObb(this.mUserId, this.mPackage);
                        }
                        Slog.d("BackupManagerService", "Calling doFullBackup() on " + str);
                        if (equals) {
                            fullBackupAgentTimeoutMillis = FullBackupEngine.this.mAgentTimeoutParameters.getSharedBackupAgentTimeoutMillis();
                        } else {
                            fullBackupAgentTimeoutMillis = FullBackupEngine.this.mAgentTimeoutParameters.getFullBackupAgentTimeoutMillis();
                        }
                        FullBackupEngine.this.backupManagerService.prepareOperationTimeout(this.mToken, fullBackupAgentTimeoutMillis, FullBackupEngine.this.mTimeoutMonitor, 0);
                        this.mAgent.doFullBackup(this.mPipe, FullBackupEngine.this.mQuota, this.mToken, FullBackupEngine.this.backupManagerService.getBackupManagerBinder(), FullBackupEngine.this.mTransportFlags);
                    } catch (Throwable th) {
                        try {
                            this.mPipe.close();
                        } catch (IOException unused) {
                        }
                        throw th;
                    }
                } catch (IOException e) {
                    Slog.e("BackupManagerService", "Error running full backup for " + this.mPackage.packageName, e);
                }
            } catch (RemoteException e2) {
                Slog.e("BackupManagerService", "Remote agent vanished during full backup of " + this.mPackage.packageName, e2);
            }
            try {
                this.mPipe.close();
            } catch (IOException unused2) {
            }
        }

        public final boolean shouldWriteApk(ApplicationInfo applicationInfo, boolean z, boolean z2) {
            int i = applicationInfo.flags;
            boolean z3 = (i & 1) != 0;
            boolean z4 = (i & 128) != 0;
            if (!z || z2) {
                return false;
            }
            return !z3 || z4;
        }
    }

    public FullBackupEngine(UserBackupManagerService userBackupManagerService, OutputStream outputStream, FullBackupPreflight fullBackupPreflight, PackageInfo packageInfo, boolean z, BackupRestoreTask backupRestoreTask, long j, int i, int i2, BackupEligibilityRules backupEligibilityRules, IBackupManagerMonitor iBackupManagerMonitor) {
        this.backupManagerService = userBackupManagerService;
        this.mOutput = outputStream;
        this.mPreflightHook = fullBackupPreflight;
        this.mPkg = packageInfo;
        this.mIncludeApks = z;
        this.mTimeoutMonitor = backupRestoreTask;
        this.mQuota = j;
        this.mOpToken = i;
        this.mTransportFlags = i2;
        BackupAgentTimeoutParameters agentTimeoutParameters = userBackupManagerService.getAgentTimeoutParameters();
        Objects.requireNonNull(agentTimeoutParameters, "Timeout parameters cannot be null");
        this.mAgentTimeoutParameters = agentTimeoutParameters;
        this.mBackupEligibilityRules = backupEligibilityRules;
        this.mMonitor = iBackupManagerMonitor;
    }

    public int preflightCheck() throws RemoteException {
        if (this.mPreflightHook == null) {
            return 0;
        }
        if (initializeAgent()) {
            int preflightFullBackup = this.mPreflightHook.preflightFullBackup(this.mPkg, this.mAgent);
            this.mAgent.clearBackupRestoreEventLogger();
            return preflightFullBackup;
        }
        Slog.w("BackupManagerService", "Unable to bind to full agent for " + this.mPkg.packageName);
        return -1003;
    }

    public int backupOnePackage() throws RemoteException {
        ParcelFileDescriptor[] createPipe;
        int i;
        int i2 = -1003;
        if (initializeAgent()) {
            ParcelFileDescriptor[] parcelFileDescriptorArr = null;
            try {
                try {
                    try {
                        createPipe = ParcelFileDescriptor.createPipe();
                    } catch (Throwable th) {
                        th = th;
                    }
                } catch (IOException e) {
                    e = e;
                }
            } catch (IOException unused) {
                Slog.w("BackupManagerService", "Error bringing down backup stack");
                i2 = -1000;
            }
            try {
                FullBackupRunner fullBackupRunner = new FullBackupRunner(this.backupManagerService, this.mPkg, this.mAgent, createPipe[1], this.mOpToken, this.mIncludeApks);
                createPipe[1].close();
                createPipe[1] = null;
                new Thread(fullBackupRunner, "app-data-runner").start();
                FullBackupUtils.routeSocketDataToOutput(createPipe[0], this.mOutput);
                if (this.backupManagerService.waitUntilOperationComplete(this.mOpToken)) {
                    i = 0;
                } else {
                    Slog.e("BackupManagerService", "Full backup failed on package " + this.mPkg.packageName);
                    i = -1003;
                }
                BackupManagerMonitorUtils.monitorAgentLoggingResults(this.mMonitor, this.mPkg, this.mAgent);
                this.mOutput.flush();
                ParcelFileDescriptor parcelFileDescriptor = createPipe[0];
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
                ParcelFileDescriptor parcelFileDescriptor2 = createPipe[1];
                if (parcelFileDescriptor2 != null) {
                    parcelFileDescriptor2.close();
                }
                i2 = i;
            } catch (IOException e2) {
                e = e2;
                parcelFileDescriptorArr = createPipe;
                Slog.e("BackupManagerService", "Error backing up " + this.mPkg.packageName + ": " + e.getMessage());
                this.mOutput.flush();
                if (parcelFileDescriptorArr != null) {
                    ParcelFileDescriptor parcelFileDescriptor3 = parcelFileDescriptorArr[0];
                    if (parcelFileDescriptor3 != null) {
                        parcelFileDescriptor3.close();
                    }
                    ParcelFileDescriptor parcelFileDescriptor4 = parcelFileDescriptorArr[1];
                    if (parcelFileDescriptor4 != null) {
                        parcelFileDescriptor4.close();
                    }
                }
                tearDown();
                return i2;
            } catch (Throwable th2) {
                th = th2;
                parcelFileDescriptorArr = createPipe;
                try {
                    this.mOutput.flush();
                    if (parcelFileDescriptorArr != null) {
                        ParcelFileDescriptor parcelFileDescriptor5 = parcelFileDescriptorArr[0];
                        if (parcelFileDescriptor5 != null) {
                            parcelFileDescriptor5.close();
                        }
                        ParcelFileDescriptor parcelFileDescriptor6 = parcelFileDescriptorArr[1];
                        if (parcelFileDescriptor6 != null) {
                            parcelFileDescriptor6.close();
                        }
                    }
                } catch (IOException unused2) {
                    Slog.w("BackupManagerService", "Error bringing down backup stack");
                }
                throw th;
            }
        } else {
            Slog.w("BackupManagerService", "Unable to bind to full agent for " + this.mPkg.packageName);
        }
        tearDown();
        return i2;
    }

    public void sendQuotaExceeded(final long j, final long j2) {
        if (initializeAgent()) {
            try {
                RemoteCall.execute(new RemoteCallable() { // from class: com.android.server.backup.fullbackup.FullBackupEngine$$ExternalSyntheticLambda0
                    @Override // com.android.server.backup.remote.RemoteCallable
                    public final void call(Object obj) {
                        FullBackupEngine.this.lambda$sendQuotaExceeded$0(j, j2, (IBackupCallback) obj);
                    }
                }, this.mAgentTimeoutParameters.getQuotaExceededTimeoutMillis());
            } catch (RemoteException unused) {
                Slog.e("BackupManagerService", "Remote exception while telling agent about quota exceeded");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$sendQuotaExceeded$0(long j, long j2, IBackupCallback iBackupCallback) throws RemoteException {
        this.mAgent.doQuotaExceeded(j, j2, iBackupCallback);
    }

    public final boolean initializeAgent() {
        if (this.mAgent == null) {
            this.mAgent = this.backupManagerService.bindToAgentSynchronous(this.mPkg.applicationInfo, 1, this.mBackupEligibilityRules.getBackupDestination());
        }
        return this.mAgent != null;
    }

    public final void tearDown() {
        PackageInfo packageInfo = this.mPkg;
        if (packageInfo != null) {
            this.backupManagerService.tearDownAgentAndKill(packageInfo.applicationInfo);
        }
    }
}
