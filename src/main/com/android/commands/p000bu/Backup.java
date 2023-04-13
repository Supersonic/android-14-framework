package com.android.commands.p000bu;

import android.app.backup.IBackupManager;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.system.OsConstants;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
/* renamed from: com.android.commands.bu.Backup */
/* loaded from: classes.dex */
public final class Backup {
    static final String TAG = "bu";
    static String[] mArgs;
    IBackupManager mBackupManager;
    int mNextArg;

    Backup(IBackupManager backupManager) {
        this.mBackupManager = backupManager;
    }

    Backup() {
        this.mBackupManager = IBackupManager.Stub.asInterface(ServiceManager.getService("backup"));
    }

    public static void main(String[] args) {
        try {
            new Backup().run(args);
        } catch (Exception e) {
            Log.e(TAG, "Error running backup/restore", e);
        }
        Log.d(TAG, "Finished.");
    }

    public void run(String[] args) {
        if (this.mBackupManager == null) {
            Log.e(TAG, "Can't obtain Backup Manager binder");
            return;
        }
        Log.d(TAG, "Beginning: " + args[0]);
        mArgs = args;
        int userId = parseUserId();
        if (!isBackupActiveForUser(userId)) {
            Log.e(TAG, "BackupManager is not available for user " + userId);
            return;
        }
        String arg = nextArg();
        if (arg.equals("backup")) {
            doBackup(OsConstants.STDOUT_FILENO, userId);
        } else if (arg.equals("restore")) {
            doRestore(OsConstants.STDIN_FILENO, userId);
        } else {
            showUsage();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:112:0x01bc A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void doBackup(int socketFd, int userId) {
        String str;
        Throwable th;
        ArrayList<String> packages = new ArrayList<>();
        boolean doKeyValue = false;
        boolean doCompress = true;
        boolean doCompress2 = true;
        boolean allIncludesSystem = false;
        boolean doWidgets = false;
        boolean doEverything = false;
        boolean saveShared = false;
        boolean saveObbs = false;
        while (true) {
            String arg = nextArg();
            if (arg == null) {
                break;
            } else if (!arg.startsWith("-")) {
                packages.add(arg);
            } else if ("-apk".equals(arg)) {
                saveObbs = true;
            } else if ("-noapk".equals(arg)) {
                saveObbs = false;
            } else if ("-obb".equals(arg)) {
                saveShared = true;
            } else if ("-noobb".equals(arg)) {
                saveShared = false;
            } else if ("-shared".equals(arg)) {
                doEverything = true;
            } else if ("-noshared".equals(arg)) {
                doEverything = false;
            } else if ("-system".equals(arg)) {
                doCompress2 = true;
            } else if ("-nosystem".equals(arg)) {
                doCompress2 = false;
            } else if ("-widgets".equals(arg)) {
                allIncludesSystem = true;
            } else if ("-nowidgets".equals(arg)) {
                allIncludesSystem = false;
            } else if ("-all".equals(arg)) {
                doWidgets = true;
            } else if ("-compress".equals(arg)) {
                doCompress = true;
            } else if ("-nocompress".equals(arg)) {
                doCompress = false;
            } else if ("-keyvalue".equals(arg)) {
                doKeyValue = true;
            } else if ("-nokeyvalue".equals(arg)) {
                doKeyValue = false;
            } else if ("-user".equals(arg)) {
                nextArg();
            } else {
                Log.w(TAG, "Unknown backup flag " + arg);
            }
        }
        if (doWidgets && packages.size() > 0) {
            Log.w(TAG, "-all passed for backup along with specific package names");
        }
        if (!doWidgets && !doEverything && packages.size() == 0) {
            Log.e(TAG, "no backup packages supplied and neither -shared nor -all given");
            return;
        }
        ParcelFileDescriptor fd = null;
        try {
            ParcelFileDescriptor fd2 = ParcelFileDescriptor.adoptFd(socketFd);
            try {
                String[] packArray = new String[packages.size()];
                IBackupManager iBackupManager = this.mBackupManager;
                String[] strArr = (String[]) packages.toArray(packArray);
                boolean z = saveObbs;
                str = TAG;
                try {
                    iBackupManager.adbBackup(userId, fd2, z, saveShared, doEverything, allIncludesSystem, doWidgets, doCompress2, doCompress, doKeyValue, strArr);
                    if (fd2 != null) {
                        try {
                            fd2.close();
                        } catch (IOException e) {
                            Log.e(str, "IO error closing output for backup: " + e.getMessage());
                        }
                    }
                } catch (RemoteException e2) {
                    fd = fd2;
                    try {
                        Log.e(str, "Unable to invoke backup manager for backup");
                        if (fd != null) {
                            try {
                                fd.close();
                            } catch (IOException e3) {
                                Log.e(str, "IO error closing output for backup: " + e3.getMessage());
                            }
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        if (fd != null) {
                            try {
                                fd.close();
                            } catch (IOException e4) {
                                Log.e(str, "IO error closing output for backup: " + e4.getMessage());
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    fd = fd2;
                    if (fd != null) {
                    }
                    throw th;
                }
            } catch (RemoteException e5) {
                str = TAG;
                fd = fd2;
            } catch (Throwable th4) {
                str = TAG;
                th = th4;
                fd = fd2;
            }
        } catch (RemoteException e6) {
            str = TAG;
        } catch (Throwable th5) {
            str = TAG;
            th = th5;
        }
    }

    private void doRestore(int socketFd, int userId) {
        ParcelFileDescriptor fd = null;
        try {
            try {
                try {
                    fd = ParcelFileDescriptor.adoptFd(socketFd);
                    this.mBackupManager.adbRestore(userId, fd);
                } catch (Throwable th) {
                    if (fd != null) {
                        try {
                            fd.close();
                        } catch (IOException e) {
                        }
                    }
                    throw th;
                }
            } catch (RemoteException e2) {
                Log.e(TAG, "Unable to invoke backup manager for restore");
                if (fd != null) {
                    fd.close();
                } else {
                    return;
                }
            }
            if (fd != null) {
                fd.close();
            }
        } catch (IOException e3) {
        }
    }

    private int parseUserId() {
        int argNumber = 0;
        while (true) {
            String[] strArr = mArgs;
            if (argNumber < strArr.length - 1) {
                if (!"-user".equals(strArr[argNumber])) {
                    argNumber++;
                } else {
                    return UserHandle.parseUserArg(mArgs[argNumber + 1]);
                }
            } else {
                return 0;
            }
        }
    }

    private boolean isBackupActiveForUser(int userId) {
        try {
            return this.mBackupManager.isBackupServiceActive(userId);
        } catch (RemoteException e) {
            Log.e(TAG, "Could not access BackupManager: " + e.toString());
            return false;
        }
    }

    private static void showUsage() {
        System.err.println(" backup [-user USER_ID] [-f FILE] [-apk|-noapk] [-obb|-noobb] [-shared|-noshared]");
        System.err.println("        [-all] [-system|-nosystem] [-keyvalue|-nokeyvalue] [PACKAGE...]");
        System.err.println("     write an archive of the device's data to FILE [default=backup.adb]");
        System.err.println("     package list optional if -all/-shared are supplied");
        System.err.println("     -user: user ID for which to perform the operation (default - system user)");
        System.err.println("     -apk/-noapk: do/don't back up .apk files (default -noapk)");
        System.err.println("     -obb/-noobb: do/don't back up .obb files (default -noobb)");
        System.err.println("     -shared|-noshared: do/don't back up shared storage (default -noshared)");
        System.err.println("     -all: back up all installed applications");
        System.err.println("     -system|-nosystem: include system apps in -all (default -system)");
        System.err.println("     -keyvalue|-nokeyvalue: include apps that perform key/value backups.");
        System.err.println("         (default -nokeyvalue)");
        System.err.println(" restore [-user USER_ID] FILE       restore device contents from FILE");
        System.err.println("     -user: user ID for which to perform the operation (default - system user)");
    }

    private String nextArg() {
        int i = this.mNextArg;
        String[] strArr = mArgs;
        if (i >= strArr.length) {
            return null;
        }
        String arg = strArr[i];
        this.mNextArg = i + 1;
        return arg;
    }
}
