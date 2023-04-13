package com.android.server.backup;

import android.util.Slog;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
/* loaded from: classes.dex */
public final class UserBackupManagerFilePersistedSettings {
    public static boolean readBackupEnableState(int i) {
        boolean readBackupEnableState = readBackupEnableState(UserBackupManagerFiles.getBaseStateDir(i));
        Slog.d("BackupManagerService", "user:" + i + " readBackupEnableState enabled:" + readBackupEnableState);
        return readBackupEnableState;
    }

    public static void writeBackupEnableState(int i, boolean z) {
        Slog.d("BackupManagerService", "user:" + i + " writeBackupEnableState enable:" + z);
        writeBackupEnableState(UserBackupManagerFiles.getBaseStateDir(i), z);
    }

    public static boolean readBackupEnableState(File file) {
        File file2 = new File(file, "backup_enabled");
        if (file2.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file2);
                int read = fileInputStream.read();
                if (read != 0 && read != 1) {
                    Slog.e("BackupManagerService", "Unexpected enabled state:" + read);
                }
                boolean z = read != 0;
                fileInputStream.close();
                return z;
            } catch (IOException unused) {
                Slog.e("BackupManagerService", "Cannot read enable state; assuming disabled");
            }
        } else {
            Slog.i("BackupManagerService", "isBackupEnabled() => false due to absent settings file");
        }
        return false;
    }

    public static void writeBackupEnableState(File file, boolean z) {
        File file2 = new File(file, "backup_enabled");
        File file3 = new File(file, "backup_enabled-stage");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file3);
            fileOutputStream.write(z ? 1 : 0);
            fileOutputStream.close();
            if (!file3.renameTo(file2)) {
                Slog.e("BackupManagerService", "Write enable failed as could not rename staging file to actual");
            }
            fileOutputStream.close();
        } catch (IOException | RuntimeException e) {
            Slog.e("BackupManagerService", "Unable to record backup enable state; reverting to disabled: " + e.getMessage());
            file2.delete();
            file3.delete();
        }
    }
}
