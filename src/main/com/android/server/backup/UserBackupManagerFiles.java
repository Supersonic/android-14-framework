package com.android.server.backup;

import android.os.Environment;
import java.io.File;
/* loaded from: classes.dex */
public final class UserBackupManagerFiles {
    public static File getBaseDir(int i) {
        return Environment.getDataSystemCeDirectory(i);
    }

    public static File getBaseStateDir(int i) {
        if (i != 0) {
            return new File(getBaseDir(i), "backup");
        }
        return new File(Environment.getDataDirectory(), "backup");
    }

    public static File getDataDir(int i) {
        if (i != 0) {
            return new File(getBaseDir(i), "backup_stage");
        }
        return new File(Environment.getDownloadCacheDirectory(), "backup_stage");
    }

    public static File getStateDirInSystemDir(int i) {
        File baseStateDir = getBaseStateDir(0);
        return new File(baseStateDir, "" + i);
    }

    public static File getStateFileInSystemDir(String str, int i) {
        return new File(getStateDirInSystemDir(i), str);
    }
}
