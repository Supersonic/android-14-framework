package com.android.server.backup;

import android.app.IBackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FullBackup;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.backup.keyvalue.KeyValueBackupTask;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import libcore.io.IoUtils;
/* loaded from: classes.dex */
public class KeyValueAdbRestoreEngine implements Runnable {
    public final IBackupAgent mAgent;
    public final UserBackupManagerService mBackupManagerService;
    public final File mDataDir;
    public final ParcelFileDescriptor mInFD;
    public final FileMetadata mInfo;
    public final int mToken;

    public KeyValueAdbRestoreEngine(UserBackupManagerService userBackupManagerService, File file, FileMetadata fileMetadata, ParcelFileDescriptor parcelFileDescriptor, IBackupAgent iBackupAgent, int i) {
        this.mBackupManagerService = userBackupManagerService;
        this.mDataDir = file;
        this.mInfo = fileMetadata;
        this.mInFD = parcelFileDescriptor;
        this.mAgent = iBackupAgent;
        this.mToken = i;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            invokeAgentForAdbRestore(this.mAgent, this.mInfo, prepareRestoreData(this.mInfo, this.mInFD));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final File prepareRestoreData(FileMetadata fileMetadata, ParcelFileDescriptor parcelFileDescriptor) throws IOException {
        String str = fileMetadata.packageName;
        File file = this.mDataDir;
        File file2 = new File(file, str + ".restore");
        File file3 = this.mDataDir;
        File file4 = new File(file3, str + ".sorted");
        FullBackup.restoreFile(parcelFileDescriptor, fileMetadata.size, fileMetadata.type, fileMetadata.mode, fileMetadata.mtime, file2);
        sortKeyValueData(file2, file4);
        return file4;
    }

    public final void invokeAgentForAdbRestore(IBackupAgent iBackupAgent, FileMetadata fileMetadata, File file) throws IOException {
        String str = fileMetadata.packageName;
        File file2 = this.mDataDir;
        File file3 = new File(file2, str + KeyValueBackupTask.NEW_STATE_FILE_SUFFIX);
        try {
            iBackupAgent.doRestore(ParcelFileDescriptor.open(file, 268435456), fileMetadata.version, ParcelFileDescriptor.open(file3, 1006632960), this.mToken, this.mBackupManagerService.getBackupManagerBinder());
        } catch (RemoteException e) {
            Slog.e("KeyValueAdbRestoreEngine", "Exception calling doRestore on agent: " + e);
        } catch (IOException e2) {
            Slog.e("KeyValueAdbRestoreEngine", "Exception opening file. " + e2);
        }
    }

    public final void sortKeyValueData(File file, File file2) throws IOException {
        FileOutputStream fileOutputStream;
        FileInputStream fileInputStream = null;
        try {
            FileInputStream fileInputStream2 = new FileInputStream(file);
            try {
                fileOutputStream = new FileOutputStream(file2);
                try {
                    copyKeysInLexicalOrder(new BackupDataInput(fileInputStream2.getFD()), new BackupDataOutput(fileOutputStream.getFD()));
                    IoUtils.closeQuietly(fileInputStream2);
                    IoUtils.closeQuietly(fileOutputStream);
                } catch (Throwable th) {
                    th = th;
                    fileInputStream = fileInputStream2;
                    if (fileInputStream != null) {
                        IoUtils.closeQuietly(fileInputStream);
                    }
                    if (fileOutputStream != null) {
                        IoUtils.closeQuietly(fileOutputStream);
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                fileOutputStream = null;
            }
        } catch (Throwable th3) {
            th = th3;
            fileOutputStream = null;
        }
    }

    public final void copyKeysInLexicalOrder(BackupDataInput backupDataInput, BackupDataOutput backupDataOutput) throws IOException {
        HashMap hashMap = new HashMap();
        while (backupDataInput.readNextHeader()) {
            String key = backupDataInput.getKey();
            int dataSize = backupDataInput.getDataSize();
            if (dataSize < 0) {
                backupDataInput.skipEntityData();
            } else {
                byte[] bArr = new byte[dataSize];
                backupDataInput.readEntityData(bArr, 0, dataSize);
                hashMap.put(key, bArr);
            }
        }
        ArrayList<String> arrayList = new ArrayList(hashMap.keySet());
        Collections.sort(arrayList);
        for (String str : arrayList) {
            byte[] bArr2 = (byte[]) hashMap.get(str);
            backupDataOutput.writeEntityHeader(str, bArr2.length);
            backupDataOutput.writeEntityData(bArr2, bArr2.length);
        }
    }
}
