package com.android.server.backup;

import android.app.IBackupAgent;
import android.app.backup.FullBackup;
import android.app.backup.FullBackupDataOutput;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.SELinux;
import android.util.Slog;
import com.android.server.backup.fullbackup.AppMetadataBackupWriter;
import com.android.server.backup.keyvalue.KeyValueBackupTask;
import com.android.server.backup.remote.ServiceBackupCallback;
import com.android.server.backup.utils.FullBackupUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import libcore.io.IoUtils;
/* loaded from: classes.dex */
public class KeyValueAdbBackupEngine {
    public final BackupAgentTimeoutParameters mAgentTimeoutParameters;
    public ParcelFileDescriptor mBackupData;
    public final File mBackupDataName;
    public UserBackupManagerService mBackupManagerService;
    public final File mBlankStateName;
    public final PackageInfo mCurrentPackage;
    public final File mDataDir;
    public final File mManifestFile;
    public ParcelFileDescriptor mNewState;
    public final File mNewStateName;
    public final OutputStream mOutput;
    public final PackageManager mPackageManager;
    public ParcelFileDescriptor mSavedState;
    public final File mStateDir;

    public KeyValueAdbBackupEngine(OutputStream outputStream, PackageInfo packageInfo, UserBackupManagerService userBackupManagerService, PackageManager packageManager, File file, File file2) {
        this.mOutput = outputStream;
        this.mCurrentPackage = packageInfo;
        this.mBackupManagerService = userBackupManagerService;
        this.mPackageManager = packageManager;
        this.mDataDir = file2;
        File file3 = new File(file, "key_value_dir");
        this.mStateDir = file3;
        file3.mkdirs();
        String str = packageInfo.packageName;
        this.mBlankStateName = new File(file3, "blank_state");
        this.mBackupDataName = new File(file2, str + KeyValueBackupTask.STAGING_FILE_SUFFIX);
        this.mNewStateName = new File(file3, str + KeyValueBackupTask.NEW_STATE_FILE_SUFFIX);
        this.mManifestFile = new File(file2, "_manifest");
        BackupAgentTimeoutParameters agentTimeoutParameters = userBackupManagerService.getAgentTimeoutParameters();
        Objects.requireNonNull(agentTimeoutParameters, "Timeout parameters cannot be null");
        this.mAgentTimeoutParameters = agentTimeoutParameters;
    }

    public void backupOnePackage() throws IOException {
        IBackupAgent bindToAgent;
        PackageInfo packageInfo = this.mCurrentPackage;
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        try {
            try {
                prepareBackupFiles(packageInfo.packageName);
                bindToAgent = bindToAgent(applicationInfo);
            } catch (FileNotFoundException e) {
                Slog.e("KeyValueAdbBackupEngine", "Failed creating files for package " + this.mCurrentPackage.packageName + " will ignore package. " + e);
            }
            if (bindToAgent == null) {
                Slog.e("KeyValueAdbBackupEngine", "Failed binding to BackupAgent for package " + this.mCurrentPackage.packageName);
            } else if (invokeAgentForAdbBackup(this.mCurrentPackage.packageName, bindToAgent)) {
                writeBackupData();
            } else {
                Slog.e("KeyValueAdbBackupEngine", "Backup Failed for package " + this.mCurrentPackage.packageName);
            }
        } finally {
            cleanup();
        }
    }

    public final void prepareBackupFiles(String str) throws FileNotFoundException {
        this.mSavedState = ParcelFileDescriptor.open(this.mBlankStateName, 402653184);
        this.mBackupData = ParcelFileDescriptor.open(this.mBackupDataName, 1006632960);
        if (!SELinux.restorecon(this.mBackupDataName)) {
            Slog.e("KeyValueAdbBackupEngine", "SELinux restorecon failed on " + this.mBackupDataName);
        }
        this.mNewState = ParcelFileDescriptor.open(this.mNewStateName, 1006632960);
    }

    public final IBackupAgent bindToAgent(ApplicationInfo applicationInfo) {
        try {
            return this.mBackupManagerService.bindToAgentSynchronous(applicationInfo, 0, 0);
        } catch (SecurityException e) {
            Slog.e("KeyValueAdbBackupEngine", "error in binding to agent for package " + applicationInfo.packageName + ". " + e);
            return null;
        }
    }

    public final boolean invokeAgentForAdbBackup(String str, IBackupAgent iBackupAgent) {
        int generateRandomIntegerToken = this.mBackupManagerService.generateRandomIntegerToken();
        try {
            this.mBackupManagerService.prepareOperationTimeout(generateRandomIntegerToken, this.mAgentTimeoutParameters.getKvBackupAgentTimeoutMillis(), null, 0);
            iBackupAgent.doBackup(this.mSavedState, this.mBackupData, this.mNewState, Long.MAX_VALUE, new ServiceBackupCallback(this.mBackupManagerService.getBackupManagerBinder(), generateRandomIntegerToken), 0);
            if (this.mBackupManagerService.waitUntilOperationComplete(generateRandomIntegerToken)) {
                return true;
            }
            Slog.e("KeyValueAdbBackupEngine", "Key-value backup failed on package " + str);
            return false;
        } catch (RemoteException e) {
            Slog.e("KeyValueAdbBackupEngine", "Error invoking agent for backup on " + str + ". " + e);
            return false;
        }
    }

    /* loaded from: classes.dex */
    public class KeyValueAdbBackupDataCopier implements Runnable {
        public final PackageInfo mPackage;
        public final ParcelFileDescriptor mPipe;
        public final int mToken;

        public KeyValueAdbBackupDataCopier(PackageInfo packageInfo, ParcelFileDescriptor parcelFileDescriptor, int i) throws IOException {
            this.mPackage = packageInfo;
            this.mPipe = ParcelFileDescriptor.dup(parcelFileDescriptor.getFileDescriptor());
            this.mToken = i;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                try {
                    FullBackupDataOutput fullBackupDataOutput = new FullBackupDataOutput(this.mPipe);
                    new AppMetadataBackupWriter(fullBackupDataOutput, KeyValueAdbBackupEngine.this.mPackageManager).backupManifest(this.mPackage, KeyValueAdbBackupEngine.this.mManifestFile, KeyValueAdbBackupEngine.this.mDataDir, "k", null, false);
                    KeyValueAdbBackupEngine.this.mManifestFile.delete();
                    FullBackup.backupToTar(this.mPackage.packageName, "k", (String) null, KeyValueAdbBackupEngine.this.mDataDir.getAbsolutePath(), KeyValueAdbBackupEngine.this.mBackupDataName.getAbsolutePath(), fullBackupDataOutput);
                    try {
                        new FileOutputStream(this.mPipe.getFileDescriptor()).write(new byte[4]);
                    } catch (IOException unused) {
                        Slog.e("KeyValueAdbBackupEngine", "Unable to finalize backup stream!");
                    }
                    try {
                        KeyValueAdbBackupEngine.this.mBackupManagerService.getBackupManagerBinder().opComplete(this.mToken, 0L);
                    } catch (RemoteException unused2) {
                    }
                } catch (IOException e) {
                    Slog.e("KeyValueAdbBackupEngine", "Error running full backup for " + this.mPackage.packageName + ". " + e);
                }
            } finally {
                IoUtils.closeQuietly(this.mPipe);
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r12v1 */
    /* JADX WARN: Type inference failed for: r12v13 */
    /* JADX WARN: Type inference failed for: r12v14 */
    /* JADX WARN: Type inference failed for: r12v9, types: [java.lang.AutoCloseable] */
    public final void writeBackupData() throws IOException {
        ParcelFileDescriptor[] createPipe;
        int generateRandomIntegerToken = this.mBackupManagerService.generateRandomIntegerToken();
        long kvBackupAgentTimeoutMillis = this.mAgentTimeoutParameters.getKvBackupAgentTimeoutMillis();
        ParcelFileDescriptor[] parcelFileDescriptorArr = null;
        parcelFileDescriptorArr = null;
        ParcelFileDescriptor[] parcelFileDescriptorArr2 = null;
        try {
            try {
                createPipe = ParcelFileDescriptor.createPipe();
            } catch (Throwable th) {
                th = th;
            }
        } catch (IOException e) {
            e = e;
        }
        try {
            this.mBackupManagerService.prepareOperationTimeout(generateRandomIntegerToken, kvBackupAgentTimeoutMillis, null, 0);
            KeyValueAdbBackupDataCopier keyValueAdbBackupDataCopier = new KeyValueAdbBackupDataCopier(this.mCurrentPackage, createPipe[1], generateRandomIntegerToken);
            createPipe[1].close();
            createPipe[1] = null;
            new Thread(keyValueAdbBackupDataCopier, "key-value-app-data-runner").start();
            FullBackupUtils.routeSocketDataToOutput(createPipe[0], this.mOutput);
            if (!this.mBackupManagerService.waitUntilOperationComplete(generateRandomIntegerToken)) {
                Slog.e("KeyValueAdbBackupEngine", "Full backup failed on package " + this.mCurrentPackage.packageName);
            }
            this.mOutput.flush();
            IoUtils.closeQuietly(createPipe[0]);
            this = createPipe[1];
        } catch (IOException e2) {
            e = e2;
            parcelFileDescriptorArr2 = createPipe;
            Slog.e("KeyValueAdbBackupEngine", "Error backing up " + this.mCurrentPackage.packageName + ": " + e);
            this.mOutput.flush();
            if (parcelFileDescriptorArr2 != null) {
                IoUtils.closeQuietly(parcelFileDescriptorArr2[0]);
                ParcelFileDescriptor parcelFileDescriptor = parcelFileDescriptorArr2[1];
                parcelFileDescriptorArr = parcelFileDescriptorArr2;
                this = parcelFileDescriptor;
                IoUtils.closeQuietly((AutoCloseable) this);
            }
            return;
        } catch (Throwable th2) {
            th = th2;
            parcelFileDescriptorArr = createPipe;
            this.mOutput.flush();
            if (parcelFileDescriptorArr != null) {
                IoUtils.closeQuietly(parcelFileDescriptorArr[0]);
                IoUtils.closeQuietly(parcelFileDescriptorArr[1]);
            }
            throw th;
        }
        IoUtils.closeQuietly((AutoCloseable) this);
    }

    public final void cleanup() {
        this.mBackupManagerService.tearDownAgentAndKill(this.mCurrentPackage.applicationInfo);
        this.mBlankStateName.delete();
        this.mNewStateName.delete();
        this.mBackupDataName.delete();
    }
}
