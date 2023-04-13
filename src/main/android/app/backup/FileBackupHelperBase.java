package android.app.backup;

import android.content.Context;
import android.p008os.ParcelFileDescriptor;
import android.util.Log;
import java.io.File;
import java.io.FileDescriptor;
/* loaded from: classes.dex */
class FileBackupHelperBase {
    private static final String TAG = "FileBackupHelperBase";
    Context mContext;
    boolean mExceptionLogged;
    long mPtr = ctor();

    private static native long ctor();

    private static native void dtor(long j);

    private static native int performBackup_native(FileDescriptor fileDescriptor, long j, FileDescriptor fileDescriptor2, String[] strArr, String[] strArr2);

    private static native int writeFile_native(long j, String str, long j2);

    private static native int writeSnapshot_native(long j, FileDescriptor fileDescriptor);

    /* JADX INFO: Access modifiers changed from: package-private */
    public FileBackupHelperBase(Context context) {
        this.mContext = context;
    }

    protected void finalize() throws Throwable {
        try {
            dtor(this.mPtr);
        } finally {
            super.finalize();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void performBackup_checked(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState, String[] files, String[] keys) {
        if (files.length == 0) {
            return;
        }
        for (String f : files) {
            if (f.charAt(0) != '/') {
                throw new RuntimeException("files must have all absolute paths: " + f);
            }
        }
        if (files.length != keys.length) {
            throw new RuntimeException("files.length=" + files.length + " keys.length=" + keys.length);
        }
        FileDescriptor oldStateFd = oldState != null ? oldState.getFileDescriptor() : null;
        FileDescriptor newStateFd = newState.getFileDescriptor();
        if (newStateFd == null) {
            throw new NullPointerException();
        }
        int err = performBackup_native(oldStateFd, data.mBackupWriter, newStateFd, files, keys);
        if (err != 0) {
            throw new RuntimeException("Backup failed 0x" + Integer.toHexString(err));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean writeFile(File f, BackupDataInputStream in) {
        File parent = f.getParentFile();
        parent.mkdirs();
        int result = writeFile_native(this.mPtr, f.getAbsolutePath(), in.mData.mBackupReader);
        if (result != 0 && !this.mExceptionLogged) {
            Log.m110e(TAG, "Failed restoring file '" + f + "' for app '" + this.mContext.getPackageName() + "' result=0x" + Integer.toHexString(result));
            this.mExceptionLogged = true;
        }
        return result == 0;
    }

    public void writeNewStateDescription(ParcelFileDescriptor fd) {
        writeSnapshot_native(this.mPtr, fd.getFileDescriptor());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isKeyInList(String key, String[] list) {
        for (String s : list) {
            if (s.equals(key)) {
                return true;
            }
        }
        return false;
    }
}
