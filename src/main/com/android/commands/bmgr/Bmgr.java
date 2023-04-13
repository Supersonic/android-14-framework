package com.android.commands.bmgr;

import android.app.backup.BackupProgress;
import android.app.backup.IBackupManager;
import android.app.backup.IBackupManagerMonitor;
import android.app.backup.IBackupObserver;
import android.app.backup.IRestoreObserver;
import android.app.backup.IRestoreSession;
import android.app.backup.ISelectBackupTransportCallback;
import android.app.backup.RestoreSet;
import android.content.ComponentName;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.Slog;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
import java.util.function.IntFunction;
/* loaded from: classes.dex */
public class Bmgr {
    private static final String BMGR_ERR_NO_RESTORESESSION_FOR_USER = "Error: Could not get restore session for user ";
    private static final String BMGR_NOT_ACTIVATED_FOR_USER = "Error: Backup Manager is not activated for user ";
    private static final String BMGR_NOT_RUNNING_ERR = "Error: Could not access the Backup Manager.  Is the system running?";
    private static final String PM_NOT_RUNNING_ERR = "Error: Could not access the Package Manager.  Is the system running?";
    public static final String TAG = "Bmgr";
    private static final String TRANSPORT_NOT_RUNNING_ERR = "Error: Could not access the backup transport.  Is the system running?";
    private String[] mArgs;
    private final IBackupManager mBmgr;
    private int mNextArg;
    private IRestoreSession mRestore;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    private @interface Monitor {
        public static final int NORMAL = 1;
        public static final int OFF = 0;
        public static final int VERBOSE = 2;
    }

    Bmgr(IBackupManager bmgr) {
        this.mBmgr = bmgr;
    }

    Bmgr() {
        this.mBmgr = IBackupManager.Stub.asInterface(ServiceManager.getService("backup"));
    }

    public static void main(String[] args) {
        try {
            new Bmgr().run(args);
        } catch (Exception e) {
            System.err.println("Exception caught:");
            e.printStackTrace();
        }
    }

    public void run(String[] args) {
        if (args.length < 1) {
            showUsage();
            return;
        }
        this.mArgs = args;
        this.mNextArg = 0;
        int userId = parseUserId();
        String op = nextArg();
        Slog.v(TAG, "Running " + op + " for user:" + userId);
        if (this.mBmgr == null) {
            System.err.println(BMGR_NOT_RUNNING_ERR);
        } else if ("activate".equals(op)) {
            doActivateService(userId);
        } else if ("activated".equals(op)) {
            doActivated(userId);
        } else if (!isBackupActive(userId)) {
        } else {
            if ("autorestore".equals(op)) {
                doAutoRestore(userId);
            } else if ("enabled".equals(op)) {
                doEnabled(userId);
            } else if ("enable".equals(op)) {
                doEnable(userId);
            } else if ("run".equals(op)) {
                doRun(userId);
            } else if ("backup".equals(op)) {
                doBackup(userId);
            } else if ("init".equals(op)) {
                doInit(userId);
            } else if ("list".equals(op)) {
                doList(userId);
            } else if ("restore".equals(op)) {
                doRestore(userId);
            } else if ("transport".equals(op)) {
                doTransport(userId);
            } else if ("wipe".equals(op)) {
                doWipe(userId);
            } else if ("fullbackup".equals(op)) {
                doFullTransportBackup(userId);
            } else if ("backupnow".equals(op)) {
                doBackupNow(userId);
            } else if ("cancel".equals(op)) {
                doCancel(userId);
            } else if ("whitelist".equals(op)) {
                doPrintWhitelist();
            } else if ("scheduling".equals(op)) {
                setSchedulingEnabled(userId);
            } else {
                System.err.println("Unknown command");
                showUsage();
            }
        }
    }

    private void setSchedulingEnabled(int userId) {
        String arg = nextArg();
        if (arg == null) {
            showUsage();
            return;
        }
        try {
            boolean enable = Boolean.parseBoolean(arg);
            this.mBmgr.setFrameworkSchedulingEnabledForUser(userId, enable);
            System.out.println("Backup scheduling is now " + (enable ? "enabled" : "disabled") + " for user " + userId);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    private void handleRemoteException(RemoteException e) {
        System.err.println(e.toString());
        System.err.println(BMGR_NOT_RUNNING_ERR);
    }

    private boolean isBackupActive(int userId) {
        try {
            if (!this.mBmgr.isBackupServiceActive(userId)) {
                System.err.println(BMGR_NOT_ACTIVATED_FOR_USER + userId);
                return false;
            }
            return true;
        } catch (RemoteException e) {
            handleRemoteException(e);
            return false;
        }
    }

    private void doAutoRestore(int userId) {
        String arg = nextArg();
        if (arg == null) {
            showUsage();
            return;
        }
        try {
            boolean enable = Boolean.parseBoolean(arg);
            this.mBmgr.setAutoRestore(enable);
            System.out.println("Auto restore is now " + (enable ? "enabled" : "disabled") + " for user " + userId);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    private String activatedToString(boolean activated) {
        return activated ? "activated" : "deactivated";
    }

    private void doActivated(int userId) {
        try {
            System.out.println("Backup Manager currently " + activatedToString(this.mBmgr.isBackupServiceActive(userId)));
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    private String enableToString(boolean enabled) {
        return enabled ? "enabled" : "disabled";
    }

    private void doEnabled(int userId) {
        try {
            boolean isEnabled = this.mBmgr.isBackupEnabledForUser(userId);
            System.out.println("Backup Manager currently " + enableToString(isEnabled));
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    private void doEnable(int userId) {
        String arg = nextArg();
        if (arg == null) {
            showUsage();
            return;
        }
        try {
            boolean enable = Boolean.parseBoolean(arg);
            this.mBmgr.setBackupEnabledForUser(userId, enable);
            System.out.println("Backup Manager now " + enableToString(enable));
        } catch (RemoteException e) {
            handleRemoteException(e);
        } catch (NumberFormatException e2) {
            showUsage();
        }
    }

    void doRun(int userId) {
        try {
            this.mBmgr.backupNowForUser(userId);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    private void doBackup(int userId) {
        String pkg = nextArg();
        if (pkg == null) {
            showUsage();
            return;
        }
        try {
            this.mBmgr.dataChangedForUser(userId, pkg);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    private void doFullTransportBackup(int userId) {
        System.out.println("Performing full transport backup");
        ArraySet<String> allPkgs = new ArraySet<>();
        while (true) {
            String pkg = nextArg();
            if (pkg == null) {
                break;
            }
            allPkgs.add(pkg);
        }
        if (allPkgs.size() > 0) {
            try {
                this.mBmgr.fullTransportBackupForUser(userId, (String[]) allPkgs.toArray(new String[allPkgs.size()]));
            } catch (RemoteException e) {
                handleRemoteException(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static abstract class Observer extends IBackupObserver.Stub {
        private volatile boolean done;
        private final Object trigger;

        private Observer() {
            this.trigger = new Object();
            this.done = false;
        }

        public void onUpdate(String currentPackage, BackupProgress backupProgress) {
        }

        public void onResult(String currentPackage, int status) {
        }

        public void backupFinished(int status) {
            synchronized (this.trigger) {
                this.done = true;
                this.trigger.notify();
            }
        }

        public boolean done() {
            return this.done;
        }

        public void waitForCompletion() {
            waitForCompletion(0L);
        }

        public void waitForCompletion(long timeout) {
            long targetTime = SystemClock.elapsedRealtime() + timeout;
            synchronized (this.trigger) {
                while (!this.done && (timeout <= 0 || SystemClock.elapsedRealtime() < targetTime)) {
                    try {
                        this.trigger.wait(1000L);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class BackupObserver extends Observer {
        private BackupObserver() {
            super();
        }

        @Override // com.android.commands.bmgr.Bmgr.Observer
        public void onUpdate(String currentPackage, BackupProgress backupProgress) {
            super.onUpdate(currentPackage, backupProgress);
            System.out.println("Package " + currentPackage + " with progress: " + backupProgress.bytesTransferred + "/" + backupProgress.bytesExpected);
        }

        @Override // com.android.commands.bmgr.Bmgr.Observer
        public void onResult(String currentPackage, int status) {
            super.onResult(currentPackage, status);
            System.out.println("Package " + currentPackage + " with result: " + Bmgr.convertBackupStatusToString(status));
        }

        @Override // com.android.commands.bmgr.Bmgr.Observer
        public void backupFinished(int status) {
            super.backupFinished(status);
            System.out.println("Backup finished with result: " + Bmgr.convertBackupStatusToString(status));
            if (status == -2003) {
                System.out.println("Backups can be cancelled if a backup is already running, check backup dumpsys");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String convertBackupStatusToString(int errorCode) {
        switch (errorCode) {
            case -2003:
                return "Backup cancelled";
            case -2002:
                return "Package not found";
            case -2001:
                return "Backup is not allowed";
            case -1005:
                return "Size quota exceeded";
            case -1003:
                return "Agent error";
            case -1002:
                return "Transport rejected package because it wasn't able to process it at the time";
            case -1000:
                return "Transport error";
            case Monitor.OFF /* 0 */:
                return "Success";
            default:
                return "Unknown error";
        }
    }

    private void backupNowAllPackages(int userId, boolean nonIncrementalBackup, int monitorState) {
        IPackageManager mPm = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
        if (mPm == null) {
            System.err.println(PM_NOT_RUNNING_ERR);
            return;
        }
        List<PackageInfo> installedPackages = null;
        try {
            installedPackages = mPm.getInstalledPackages(0L, userId).getList();
        } catch (RemoteException e) {
            System.err.println(e.toString());
            System.err.println(PM_NOT_RUNNING_ERR);
        }
        if (installedPackages != null) {
            String[] packages = (String[]) installedPackages.stream().map(new Function() { // from class: com.android.commands.bmgr.Bmgr$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    String str;
                    str = ((PackageInfo) obj).packageName;
                    return str;
                }
            }).toArray(new IntFunction() { // from class: com.android.commands.bmgr.Bmgr$$ExternalSyntheticLambda1
                @Override // java.util.function.IntFunction
                public final Object apply(int i) {
                    return Bmgr.lambda$backupNowAllPackages$1(i);
                }
            });
            String[] filteredPackages = new String[0];
            try {
                filteredPackages = this.mBmgr.filterAppsEligibleForBackupForUser(userId, packages);
            } catch (RemoteException e2) {
                handleRemoteException(e2);
            }
            backupNowPackages(userId, Arrays.asList(filteredPackages), nonIncrementalBackup, monitorState);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ String[] lambda$backupNowAllPackages$1(int x$0) {
        return new String[x$0];
    }

    private void backupNowPackages(int userId, List<String> packages, boolean nonIncrementalBackup, int monitorState) {
        IBackupManagerMonitor iBackupManagerMonitor;
        int flags = 0;
        if (nonIncrementalBackup) {
            flags = 0 | 1;
        }
        try {
            BackupObserver observer = new BackupObserver();
            if (monitorState != 0) {
                iBackupManagerMonitor = new BackupMonitor(monitorState == 2);
            } else {
                iBackupManagerMonitor = null;
            }
            int err = this.mBmgr.requestBackupForUser(userId, (String[]) packages.toArray(new String[packages.size()]), observer, iBackupManagerMonitor, flags);
            if (err == 0) {
                observer.waitForCompletion();
            } else {
                System.err.println("Unable to run backup");
            }
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    private void doBackupNow(int userId) {
        boolean backupAll = false;
        boolean nonIncrementalBackup = false;
        int monitor = 0;
        ArrayList<String> allPkgs = new ArrayList<>();
        while (true) {
            String pkg = nextArg();
            if (pkg == null) {
                break;
            } else if (pkg.equals("--all")) {
                backupAll = true;
            } else if (pkg.equals("--non-incremental")) {
                nonIncrementalBackup = true;
            } else if (pkg.equals("--incremental")) {
                nonIncrementalBackup = false;
            } else if (pkg.equals("--monitor")) {
                monitor = 1;
            } else if (pkg.equals("--monitor-verbose")) {
                monitor = 2;
            } else if (!allPkgs.contains(pkg)) {
                allPkgs.add(pkg);
            }
        }
        if (backupAll) {
            if (allPkgs.size() == 0) {
                System.out.println("Running " + (nonIncrementalBackup ? "non-" : "") + "incremental backup for all packages.");
                backupNowAllPackages(userId, nonIncrementalBackup, monitor);
                return;
            }
            System.err.println("Provide only '--all' flag or list of packages.");
        } else if (allPkgs.size() > 0) {
            System.out.println("Running " + (nonIncrementalBackup ? "non-" : "") + "incremental backup for " + allPkgs.size() + " requested packages.");
            backupNowPackages(userId, allPkgs, nonIncrementalBackup, monitor);
        } else {
            System.err.println("Provide '--all' flag or list of packages.");
        }
    }

    private void doCancel(int userId) {
        String arg = nextArg();
        if ("backups".equals(arg)) {
            try {
                this.mBmgr.cancelBackupsForUser(userId);
                return;
            } catch (RemoteException e) {
                handleRemoteException(e);
                return;
            }
        }
        System.err.println("Unknown command.");
    }

    private void doTransport(int userId) {
        try {
            String which = nextArg();
            if (which == null) {
                showUsage();
            } else if ("-c".equals(which)) {
                doTransportByComponent(userId);
            } else {
                String old = this.mBmgr.selectBackupTransportForUser(userId, which);
                if (old == null) {
                    System.out.println("Unknown transport '" + which + "' specified; no changes made.");
                } else {
                    System.out.println("Selected transport " + which + " (formerly " + old + ")");
                }
            }
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    private void doTransportByComponent(int userId) {
        String which = nextArg();
        if (which == null) {
            showUsage();
            return;
        }
        final CountDownLatch latch = new CountDownLatch(1);
        try {
            this.mBmgr.selectBackupTransportAsyncForUser(userId, ComponentName.unflattenFromString(which), new ISelectBackupTransportCallback.Stub() { // from class: com.android.commands.bmgr.Bmgr.1
                public void onSuccess(String transportName) {
                    System.out.println("Success. Selected transport: " + transportName);
                    latch.countDown();
                }

                public void onFailure(int reason) {
                    System.err.println("Failure. error=" + reason);
                    latch.countDown();
                }
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                System.err.println("Operation interrupted.");
            }
        } catch (RemoteException e2) {
            handleRemoteException(e2);
        }
    }

    private void doWipe(int userId) {
        String transport = nextArg();
        if (transport == null) {
            showUsage();
            return;
        }
        String pkg = nextArg();
        if (pkg == null) {
            showUsage();
            return;
        }
        try {
            this.mBmgr.clearBackupDataForUser(userId, transport, pkg);
            System.out.println("Wiped backup data for " + pkg + " on " + transport);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class InitObserver extends Observer {
        public int result;

        InitObserver() {
            super();
            this.result = -1000;
        }

        @Override // com.android.commands.bmgr.Bmgr.Observer
        public void backupFinished(int status) {
            super.backupFinished(status);
            this.result = status;
        }
    }

    private void doInit(int userId) {
        ArraySet<String> transports = new ArraySet<>();
        while (true) {
            String transport = nextArg();
            if (transport == null) {
                break;
            }
            transports.add(transport);
        }
        if (transports.size() == 0) {
            showUsage();
            return;
        }
        InitObserver observer = new InitObserver();
        try {
            System.out.println("Initializing transports: " + transports);
            this.mBmgr.initializeTransportsForUser(userId, (String[]) transports.toArray(new String[transports.size()]), observer);
            observer.waitForCompletion(30000L);
            System.out.println("Initialization result: " + observer.result);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    private void doList(int userId) {
        String arg = nextArg();
        if ("transports".equals(arg)) {
            doListTransports(userId);
            return;
        }
        try {
            IRestoreSession beginRestoreSessionForUser = this.mBmgr.beginRestoreSessionForUser(userId, (String) null, (String) null);
            this.mRestore = beginRestoreSessionForUser;
            if (beginRestoreSessionForUser == null) {
                System.err.println(BMGR_ERR_NO_RESTORESESSION_FOR_USER + userId);
                return;
            }
            if ("sets".equals(arg)) {
                doListRestoreSets();
            }
            this.mRestore.endRestoreSession();
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    private void doListTransports(int userId) {
        String arg = nextArg();
        try {
            int i = 0;
            if ("-c".equals(arg)) {
                ComponentName[] listAllTransportComponentsForUser = this.mBmgr.listAllTransportComponentsForUser(userId);
                int length = listAllTransportComponentsForUser.length;
                while (i < length) {
                    ComponentName transport = listAllTransportComponentsForUser[i];
                    System.out.println(transport.flattenToShortString());
                    i++;
                }
                return;
            }
            String current = this.mBmgr.getCurrentTransportForUser(userId);
            String[] transports = this.mBmgr.listAllTransportsForUser(userId);
            if (transports != null && transports.length != 0) {
                int length2 = transports.length;
                while (i < length2) {
                    String t = transports[i];
                    String pad = t.equals(current) ? "  * " : "    ";
                    System.out.println(pad + t);
                    i++;
                }
                return;
            }
            System.out.println("No transports available.");
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    private void doListRestoreSets() {
        try {
            RestoreObserver observer = new RestoreObserver();
            int err = this.mRestore.getAvailableRestoreSets(observer, (IBackupManagerMonitor) null);
            if (err != 0) {
                System.out.println("Unable to request restore sets");
            } else {
                observer.waitForCompletion();
                printRestoreSets(observer.sets);
            }
        } catch (RemoteException e) {
            System.err.println(e.toString());
            System.err.println(TRANSPORT_NOT_RUNNING_ERR);
        }
    }

    private void printRestoreSets(RestoreSet[] sets) {
        if (sets == null || sets.length == 0) {
            System.out.println("No restore sets");
            return;
        }
        for (RestoreSet s : sets) {
            System.out.println("  " + Long.toHexString(s.token) + " : " + s.name);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class RestoreObserver extends IRestoreObserver.Stub {
        boolean done;
        RestoreSet[] sets = null;

        RestoreObserver() {
        }

        public void restoreSetsAvailable(RestoreSet[] result) {
            synchronized (this) {
                this.sets = result;
                this.done = true;
                notify();
            }
        }

        public void restoreStarting(int numPackages) {
            System.out.println("restoreStarting: " + numPackages + " packages");
        }

        public void onUpdate(int nowBeingRestored, String currentPackage) {
            System.out.println("onUpdate: " + nowBeingRestored + " = " + currentPackage);
        }

        public void restoreFinished(int error) {
            System.out.println("restoreFinished: " + error);
            synchronized (this) {
                this.done = true;
                notify();
            }
        }

        public void waitForCompletion() {
            synchronized (this) {
                while (!this.done) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
                this.done = false;
            }
        }
    }

    private void doRestore(int userId) {
        String arg = nextArg();
        if (arg == null) {
            showUsage();
        } else if (arg.indexOf(46) >= 0 || arg.equals("android")) {
            doRestorePackage(arg);
        } else {
            try {
                long token = Long.parseLong(arg, 16);
                HashSet<String> filter = null;
                while (true) {
                    String arg2 = nextArg();
                    if (arg2 != null) {
                        if (filter == null) {
                            filter = new HashSet<>();
                        }
                        filter.add(arg2);
                    } else {
                        doRestoreAll(userId, token, filter);
                        return;
                    }
                }
            } catch (NumberFormatException e) {
                showUsage();
            }
        }
    }

    private void doRestorePackage(String pkg) {
        System.err.println("The syntax 'restore <package>' is no longer supported, please use ");
        System.err.println("'restore <token> <package>'.");
    }

    private void doRestoreAll(int userId, long token, HashSet<String> filter) {
        RestoreObserver observer = new RestoreObserver();
        boolean didRestore = false;
        try {
            IRestoreSession beginRestoreSessionForUser = this.mBmgr.beginRestoreSessionForUser(userId, (String) null, (String) null);
            this.mRestore = beginRestoreSessionForUser;
            if (beginRestoreSessionForUser == null) {
                System.err.println(BMGR_ERR_NO_RESTORESESSION_FOR_USER + userId);
                return;
            }
            RestoreSet[] sets = null;
            int err = beginRestoreSessionForUser.getAvailableRestoreSets(observer, (IBackupManagerMonitor) null);
            if (err == 0) {
                observer.waitForCompletion();
                RestoreSet[] sets2 = observer.sets;
                if (sets2 != null) {
                    for (RestoreSet s : sets2) {
                        if (s.token == token) {
                            System.out.println("Scheduling restore: " + s.name);
                            if (filter == null) {
                                didRestore = this.mRestore.restoreAll(token, observer, (IBackupManagerMonitor) null) == 0;
                                sets = sets2;
                            } else {
                                String[] names = new String[filter.size()];
                                filter.toArray(names);
                                didRestore = this.mRestore.restorePackages(token, observer, names, (IBackupManagerMonitor) null) == 0;
                                sets = sets2;
                            }
                        }
                    }
                }
                sets = sets2;
            }
            if (!didRestore) {
                if (sets != null && sets.length != 0) {
                    System.out.println("No matching restore set token.  Available sets:");
                    printRestoreSets(sets);
                }
                System.out.println("No available restore sets; no restore performed");
            }
            if (didRestore) {
                observer.waitForCompletion();
            }
            this.mRestore.endRestoreSession();
            System.out.println("done");
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    private void doPrintWhitelist() {
        try {
            String[] whitelist = this.mBmgr.getTransportWhitelist();
            if (whitelist != null) {
                for (String transport : whitelist) {
                    System.out.println(transport);
                }
            }
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    private void doActivateService(int userId) {
        String arg = nextArg();
        if (arg == null) {
            showUsage();
            return;
        }
        try {
            boolean activate = Boolean.parseBoolean(arg);
            this.mBmgr.setBackupServiceActive(userId, activate);
            System.out.println("Backup service now " + (activate ? "activated" : "deactivated") + " for user " + userId);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    private String nextArg() {
        int i = this.mNextArg;
        String[] strArr = this.mArgs;
        if (i >= strArr.length) {
            return null;
        }
        String arg = strArr[i];
        this.mNextArg = i + 1;
        return arg;
    }

    private int parseUserId() {
        String arg = nextArg();
        if ("--user".equals(arg)) {
            return UserHandle.parseUserArg(nextArg());
        }
        this.mNextArg--;
        return 0;
    }

    private static void showUsage() {
        System.err.println("usage: bmgr [--user <userId>] [backup|restore|list|transport|run]");
        System.err.println("       bmgr backup PACKAGE");
        System.err.println("       bmgr enable BOOL");
        System.err.println("       bmgr enabled");
        System.err.println("       bmgr list transports [-c]");
        System.err.println("       bmgr list sets");
        System.err.println("       bmgr transport WHICH|-c WHICH_COMPONENT");
        System.err.println("       bmgr restore TOKEN");
        System.err.println("       bmgr restore TOKEN PACKAGE...");
        System.err.println("       bmgr run");
        System.err.println("       bmgr wipe TRANSPORT PACKAGE");
        System.err.println("       bmgr fullbackup PACKAGE...");
        System.err.println("       bmgr backupnow [--monitor|--monitor-verbose] --all|PACKAGE...");
        System.err.println("       bmgr cancel backups");
        System.err.println("       bmgr init TRANSPORT...");
        System.err.println("       bmgr activate BOOL");
        System.err.println("       bmgr activated");
        System.err.println("       bmgr autorestore BOOL");
        System.err.println("       bmgr scheduling BOOL");
        System.err.println("");
        System.err.println("The '--user' option specifies the user on which the operation is run.");
        System.err.println("It must be the first argument before the operation.");
        System.err.println("The default value is 0 which is the system user.");
        System.err.println("");
        System.err.println("The 'backup' command schedules a backup pass for the named package.");
        System.err.println("Note that the backup pass will effectively be a no-op if the package");
        System.err.println("does not actually have changed data to store.");
        System.err.println("");
        System.err.println("The 'enable' command enables or disables the entire backup mechanism.");
        System.err.println("If the argument is 'true' it will be enabled, otherwise it will be");
        System.err.println("disabled.  When disabled, neither backup or restore operations will");
        System.err.println("be performed.");
        System.err.println("");
        System.err.println("The 'enabled' command reports the current enabled/disabled state of");
        System.err.println("the backup mechanism.");
        System.err.println("");
        System.err.println("The 'list transports' command reports the names of the backup transports");
        System.err.println("BackupManager is currently bound to. These names can be passed as arguments");
        System.err.println("to the 'transport' and 'wipe' commands.  The currently active transport");
        System.err.println("is indicated with a '*' character. If -c flag is used, all available");
        System.err.println("transport components on the device are listed. These can be used with");
        System.err.println("the component variant of 'transport' command.");
        System.err.println("");
        System.err.println("The 'list sets' command reports the token and name of each restore set");
        System.err.println("available to the device via the currently active transport.");
        System.err.println("");
        System.err.println("The 'transport' command designates the named transport as the currently");
        System.err.println("active one.  This setting is persistent across reboots. If -c flag is");
        System.err.println("specified, the following string is treated as a component name.");
        System.err.println("");
        System.err.println("The 'restore' command when given just a restore token initiates a full-system");
        System.err.println("restore operation from the currently active transport.  It will deliver");
        System.err.println("the restore set designated by the TOKEN argument to each application");
        System.err.println("that had contributed data to that restore set.");
        System.err.println("");
        System.err.println("The 'restore' command when given a token and one or more package names");
        System.err.println("initiates a restore operation of just those given packages from the restore");
        System.err.println("set designated by the TOKEN argument.  It is effectively the same as the");
        System.err.println("'restore' operation supplying only a token, but applies a filter to the");
        System.err.println("set of applications to be restored.");
        System.err.println("");
        System.err.println("The 'run' command causes any scheduled backup operation to be initiated");
        System.err.println("immediately, without the usual waiting period for batching together");
        System.err.println("data changes.");
        System.err.println("");
        System.err.println("The 'wipe' command causes all backed-up data for the given package to be");
        System.err.println("erased from the given transport's storage.  The next backup operation");
        System.err.println("that the given application performs will rewrite its entire data set.");
        System.err.println("Transport names to use here are those reported by 'list transports'.");
        System.err.println("");
        System.err.println("The 'fullbackup' command induces a full-data stream backup for one or more");
        System.err.println("packages.  The data is sent via the currently active transport.");
        System.err.println("");
        System.err.println("The 'backupnow' command runs an immediate backup for one or more packages.");
        System.err.println("    --all flag runs backup for all eligible packages.");
        System.err.println("    --monitor flag prints monitor events.");
        System.err.println("    --monitor-verbose flag prints monitor events with all keys.");
        System.err.println("For each package it will run key/value or full data backup ");
        System.err.println("depending on the package's manifest declarations.");
        System.err.println("The data is sent via the currently active transport.");
        System.err.println("");
        System.err.println("The 'cancel backups' command cancels all running backups.");
        System.err.println("");
        System.err.println("The 'init' command initializes the given transports, wiping all data");
        System.err.println("from their backing data stores.");
        System.err.println("");
        System.err.println("The 'activate' command activates or deactivates the backup service.");
        System.err.println("If the argument is 'true' it will be activated, otherwise it will be");
        System.err.println("deactivated. When deactivated, the service will not be running and no");
        System.err.println("operations can be performed until activation.");
        System.err.println("");
        System.err.println("The 'activated' command reports the current activated/deactivated");
        System.err.println("state of the backup mechanism.");
        System.err.println("");
        System.err.println("The 'autorestore' command enables or disables automatic restore when");
        System.err.println("a new package is installed.");
        System.err.println("");
        System.err.println("The 'scheduling' command enables or disables backup scheduling in the");
        System.err.println("framework.");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class BackupMonitor extends IBackupManagerMonitor.Stub {
        private final boolean mVerbose;

        private BackupMonitor(boolean verbose) {
            this.mVerbose = verbose;
        }

        public void onEvent(Bundle event) throws RemoteException {
            StringBuilder out = new StringBuilder();
            int id = event.getInt("android.app.backup.extra.LOG_EVENT_ID");
            int category = event.getInt("android.app.backup.extra.LOG_EVENT_CATEGORY");
            out.append("=> Event{").append(Bmgr.eventCategoryToString(category));
            out.append(" / ").append(Bmgr.eventIdToString(id));
            String packageName = event.getString("android.app.backup.extra.LOG_EVENT_PACKAGE_NAME");
            if (packageName != null) {
                out.append(" : package = ").append(packageName);
                if (event.containsKey("android.app.backup.extra.LOG_EVENT_PACKAGE_FULL_VERSION")) {
                    long version = event.getLong("android.app.backup.extra.LOG_EVENT_PACKAGE_FULL_VERSION");
                    out.append("(v").append(version).append(")");
                }
            }
            if (this.mVerbose) {
                Set<String> remainingKeys = new ArraySet<>(event.keySet());
                remainingKeys.remove("android.app.backup.extra.LOG_EVENT_ID");
                remainingKeys.remove("android.app.backup.extra.LOG_EVENT_CATEGORY");
                remainingKeys.remove("android.app.backup.extra.LOG_EVENT_PACKAGE_NAME");
                remainingKeys.remove("android.app.backup.extra.LOG_EVENT_PACKAGE_FULL_VERSION");
                remainingKeys.remove("android.app.backup.extra.LOG_EVENT_PACKAGE_VERSION");
                if (!remainingKeys.isEmpty()) {
                    out.append(", other keys =");
                    for (String key : remainingKeys) {
                        out.append(" ").append(key);
                    }
                }
            }
            out.append("}");
            System.out.println(out.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String eventCategoryToString(int eventCategory) {
        switch (eventCategory) {
            case Monitor.NORMAL /* 1 */:
                return "TRANSPORT";
            case Monitor.VERBOSE /* 2 */:
                return "AGENT";
            case 3:
                return "BACKUP_MANAGER_POLICY";
            default:
                return "UNKNOWN_CATEGORY";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String eventIdToString(int eventId) {
        switch (eventId) {
            case 4:
                return "FULL_BACKUP_CANCEL";
            case 5:
                return "ILLEGAL_KEY";
            case 6:
            case 8:
            case 17:
            case 20:
            case 32:
            case 33:
            default:
                return "UNKNOWN_ID";
            case 7:
                return "NO_DATA_TO_SEND";
            case 9:
                return "PACKAGE_INELIGIBLE";
            case 10:
                return "PACKAGE_KEY_VALUE_PARTICIPANT";
            case 11:
                return "PACKAGE_STOPPED";
            case 12:
                return "PACKAGE_NOT_FOUND";
            case 13:
                return "BACKUP_DISABLED";
            case 14:
                return "DEVICE_NOT_PROVISIONED";
            case 15:
                return "PACKAGE_TRANSPORT_NOT_PRESENT";
            case 16:
                return "ERROR_PREFLIGHT";
            case 18:
                return "QUOTA_HIT_PREFLIGHT";
            case 19:
                return "EXCEPTION_FULL_BACKUP";
            case 21:
                return "KEY_VALUE_BACKUP_CANCEL";
            case 22:
                return "NO_RESTORE_METADATA_AVAILABLE";
            case 23:
                return "NO_PM_METADATA_RECEIVED";
            case 24:
                return "PM_AGENT_HAS_NO_METADATA";
            case 25:
                return "LOST_TRANSPORT";
            case 26:
                return "PACKAGE_NOT_PRESENT";
            case 27:
                return "RESTORE_VERSION_HIGHER";
            case 28:
                return "APP_HAS_NO_AGENT";
            case 29:
                return "SIGNATURE_MISMATCH";
            case 30:
                return "CANT_FIND_AGENT";
            case 31:
                return "KEY_VALUE_RESTORE_TIMEOUT";
            case 34:
                return "RESTORE_ANY_VERSION";
            case 35:
                return "VERSIONS_MATCH";
            case 36:
                return "VERSION_OF_BACKUP_OLDER";
            case 37:
                return "FULL_RESTORE_SIGNATURE_MISMATCH";
            case 38:
                return "SYSTEM_APP_NO_AGENT";
            case 39:
                return "FULL_RESTORE_ALLOW_BACKUP_FALSE";
            case 40:
                return "APK_NOT_INSTALLED";
            case 41:
                return "CANNOT_RESTORE_WITHOUT_APK";
            case 42:
                return "MISSING_SIGNATURE";
            case 43:
                return "EXPECTED_DIFFERENT_PACKAGE";
            case 44:
                return "UNKNOWN_VERSION";
            case 45:
                return "FULL_RESTORE_TIMEOUT";
            case 46:
                return "CORRUPT_MANIFEST";
            case 47:
                return "WIDGET_METADATA_MISMATCH";
            case 48:
                return "WIDGET_UNKNOWN_VERSION";
            case 49:
                return "NO_PACKAGES";
            case 50:
                return "TRANSPORT_IS_NULL";
        }
    }
}
