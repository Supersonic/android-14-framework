package com.android.server.backup.restore;

import android.app.backup.IBackupManagerMonitor;
import android.app.backup.IRestoreObserver;
import android.app.backup.IRestoreSession;
import android.app.backup.RestoreSet;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.Message;
import android.util.Slog;
import com.android.server.backup.TransportManager;
import com.android.server.backup.UserBackupManagerService;
import com.android.server.backup.internal.OnTaskFinishedListener;
import com.android.server.backup.params.RestoreGetSetsParams;
import com.android.server.backup.params.RestoreParams;
import com.android.server.backup.transport.TransportConnection;
import com.android.server.backup.utils.BackupEligibilityRules;
import java.util.List;
import java.util.function.BiFunction;
/* loaded from: classes.dex */
public class ActiveRestoreSession extends IRestoreSession.Stub {
    public final BackupEligibilityRules mBackupEligibilityRules;
    public final UserBackupManagerService mBackupManagerService;
    public final String mPackageName;
    public final TransportManager mTransportManager;
    public final String mTransportName;
    public final int mUserId;
    public List<RestoreSet> mRestoreSets = null;
    public boolean mEnded = false;
    public boolean mTimedOut = false;

    public ActiveRestoreSession(UserBackupManagerService userBackupManagerService, String str, String str2, BackupEligibilityRules backupEligibilityRules) {
        this.mBackupManagerService = userBackupManagerService;
        this.mPackageName = str;
        this.mTransportManager = userBackupManagerService.getTransportManager();
        this.mTransportName = str2;
        this.mUserId = userBackupManagerService.getUserId();
        this.mBackupEligibilityRules = backupEligibilityRules;
    }

    public void markTimedOut() {
        this.mTimedOut = true;
    }

    public synchronized int getAvailableRestoreSets(IRestoreObserver iRestoreObserver, IBackupManagerMonitor iBackupManagerMonitor) {
        this.mBackupManagerService.getContext().enforceCallingOrSelfPermission("android.permission.BACKUP", "getAvailableRestoreSets");
        if (iRestoreObserver != null) {
            if (this.mEnded) {
                throw new IllegalStateException("Restore session already ended");
            }
            if (this.mTimedOut) {
                Slog.i("RestoreSession", "Session already timed out");
                return -1;
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                final TransportConnection transportClient = this.mTransportManager.getTransportClient(this.mTransportName, "RestoreSession.getAvailableRestoreSets()");
                if (transportClient == null) {
                    Slog.w("RestoreSession", "Null transport client getting restore sets");
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return -1;
                }
                this.mBackupManagerService.getBackupHandler().removeMessages(8);
                final UserBackupManagerService.BackupWakeLock wakelock = this.mBackupManagerService.getWakelock();
                wakelock.acquire();
                final TransportManager transportManager = this.mTransportManager;
                this.mBackupManagerService.getBackupHandler().sendMessage(this.mBackupManagerService.getBackupHandler().obtainMessage(6, new RestoreGetSetsParams(transportClient, this, iRestoreObserver, iBackupManagerMonitor, new OnTaskFinishedListener() { // from class: com.android.server.backup.restore.ActiveRestoreSession$$ExternalSyntheticLambda2
                    @Override // com.android.server.backup.internal.OnTaskFinishedListener
                    public final void onFinished(String str) {
                        ActiveRestoreSession.lambda$getAvailableRestoreSets$0(TransportManager.this, transportClient, wakelock, str);
                    }
                })));
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return 0;
            } catch (Exception e) {
                Slog.e("RestoreSession", "Error in getAvailableRestoreSets", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return -1;
            }
        }
        throw new IllegalArgumentException("Observer must not be null");
    }

    public static /* synthetic */ void lambda$getAvailableRestoreSets$0(TransportManager transportManager, TransportConnection transportConnection, UserBackupManagerService.BackupWakeLock backupWakeLock, String str) {
        transportManager.disposeOfTransportClient(transportConnection, str);
        backupWakeLock.release();
    }

    public synchronized int restoreAll(final long j, final IRestoreObserver iRestoreObserver, final IBackupManagerMonitor iBackupManagerMonitor) {
        this.mBackupManagerService.getContext().enforceCallingOrSelfPermission("android.permission.BACKUP", "performRestore");
        Slog.d("RestoreSession", "restoreAll token=" + Long.toHexString(j) + " observer=" + iRestoreObserver);
        if (this.mEnded) {
            throw new IllegalStateException("Restore session already ended");
        }
        if (this.mTimedOut) {
            Slog.i("RestoreSession", "Session already timed out");
            return -1;
        } else if (this.mRestoreSets == null) {
            Slog.e("RestoreSession", "Ignoring restoreAll() with no restore set");
            return -1;
        } else if (this.mPackageName != null) {
            Slog.e("RestoreSession", "Ignoring restoreAll() on single-package session");
            return -1;
        } else if (!this.mTransportManager.isTransportRegistered(this.mTransportName)) {
            Slog.e("RestoreSession", "Transport " + this.mTransportName + " not registered");
            return -1;
        } else {
            synchronized (this.mBackupManagerService.getQueueLock()) {
                for (int i = 0; i < this.mRestoreSets.size(); i++) {
                    if (j == this.mRestoreSets.get(i).token) {
                        long clearCallingIdentity = Binder.clearCallingIdentity();
                        final RestoreSet restoreSet = this.mRestoreSets.get(i);
                        try {
                            return sendRestoreToHandlerLocked(new BiFunction() { // from class: com.android.server.backup.restore.ActiveRestoreSession$$ExternalSyntheticLambda0
                                @Override // java.util.function.BiFunction
                                public final Object apply(Object obj, Object obj2) {
                                    RestoreParams lambda$restoreAll$1;
                                    lambda$restoreAll$1 = ActiveRestoreSession.this.lambda$restoreAll$1(iRestoreObserver, iBackupManagerMonitor, j, restoreSet, (TransportConnection) obj, (OnTaskFinishedListener) obj2);
                                    return lambda$restoreAll$1;
                                }
                            }, "RestoreSession.restoreAll()");
                        } finally {
                            Binder.restoreCallingIdentity(clearCallingIdentity);
                        }
                    }
                }
                Slog.w("RestoreSession", "Restore token " + Long.toHexString(j) + " not found");
                return -1;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ RestoreParams lambda$restoreAll$1(IRestoreObserver iRestoreObserver, IBackupManagerMonitor iBackupManagerMonitor, long j, RestoreSet restoreSet, TransportConnection transportConnection, OnTaskFinishedListener onTaskFinishedListener) {
        return RestoreParams.createForRestoreAll(transportConnection, iRestoreObserver, iBackupManagerMonitor, j, onTaskFinishedListener, getBackupEligibilityRules(restoreSet));
    }

    public synchronized int restorePackages(final long j, final IRestoreObserver iRestoreObserver, final String[] strArr, final IBackupManagerMonitor iBackupManagerMonitor) {
        this.mBackupManagerService.getContext().enforceCallingOrSelfPermission("android.permission.BACKUP", "performRestore");
        StringBuilder sb = new StringBuilder(128);
        sb.append("restorePackages token=");
        sb.append(Long.toHexString(j));
        sb.append(" observer=");
        if (iRestoreObserver == null) {
            sb.append("null");
        } else {
            sb.append(iRestoreObserver.toString());
        }
        sb.append(" monitor=");
        if (iBackupManagerMonitor == null) {
            sb.append("null");
        } else {
            sb.append(iBackupManagerMonitor.toString());
        }
        sb.append(" packages=");
        if (strArr == null) {
            sb.append("null");
        } else {
            sb.append('{');
            boolean z = true;
            for (String str : strArr) {
                if (z) {
                    z = false;
                } else {
                    sb.append(", ");
                }
                sb.append(str);
            }
            sb.append('}');
        }
        Slog.d("RestoreSession", sb.toString());
        if (this.mEnded) {
            throw new IllegalStateException("Restore session already ended");
        }
        if (this.mTimedOut) {
            Slog.i("RestoreSession", "Session already timed out");
            return -1;
        } else if (this.mRestoreSets == null) {
            Slog.e("RestoreSession", "Ignoring restoreAll() with no restore set");
            return -1;
        } else if (this.mPackageName != null) {
            Slog.e("RestoreSession", "Ignoring restoreAll() on single-package session");
            return -1;
        } else if (!this.mTransportManager.isTransportRegistered(this.mTransportName)) {
            Slog.e("RestoreSession", "Transport " + this.mTransportName + " not registered");
            return -1;
        } else {
            synchronized (this.mBackupManagerService.getQueueLock()) {
                for (int i = 0; i < this.mRestoreSets.size(); i++) {
                    if (j == this.mRestoreSets.get(i).token) {
                        long clearCallingIdentity = Binder.clearCallingIdentity();
                        final RestoreSet restoreSet = this.mRestoreSets.get(i);
                        int sendRestoreToHandlerLocked = sendRestoreToHandlerLocked(new BiFunction() { // from class: com.android.server.backup.restore.ActiveRestoreSession$$ExternalSyntheticLambda1
                            @Override // java.util.function.BiFunction
                            public final Object apply(Object obj, Object obj2) {
                                RestoreParams lambda$restorePackages$2;
                                lambda$restorePackages$2 = ActiveRestoreSession.this.lambda$restorePackages$2(iRestoreObserver, iBackupManagerMonitor, j, strArr, restoreSet, (TransportConnection) obj, (OnTaskFinishedListener) obj2);
                                return lambda$restorePackages$2;
                            }
                        }, "RestoreSession.restorePackages(" + strArr.length + " packages)");
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                        return sendRestoreToHandlerLocked;
                    }
                }
                Slog.w("RestoreSession", "Restore token " + Long.toHexString(j) + " not found");
                return -1;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ RestoreParams lambda$restorePackages$2(IRestoreObserver iRestoreObserver, IBackupManagerMonitor iBackupManagerMonitor, long j, String[] strArr, RestoreSet restoreSet, TransportConnection transportConnection, OnTaskFinishedListener onTaskFinishedListener) {
        return RestoreParams.createForRestorePackages(transportConnection, iRestoreObserver, iBackupManagerMonitor, j, strArr, strArr.length > 1, onTaskFinishedListener, getBackupEligibilityRules(restoreSet));
    }

    public final BackupEligibilityRules getBackupEligibilityRules(RestoreSet restoreSet) {
        return this.mBackupManagerService.getEligibilityRulesForOperation("D2D".equals(restoreSet.device) ? 1 : 0);
    }

    public synchronized int restorePackage(String str, final IRestoreObserver iRestoreObserver, final IBackupManagerMonitor iBackupManagerMonitor) {
        Slog.v("RestoreSession", "restorePackage pkg=" + str + " obs=" + iRestoreObserver + "monitor=" + iBackupManagerMonitor);
        if (this.mEnded) {
            throw new IllegalStateException("Restore session already ended");
        }
        if (this.mTimedOut) {
            Slog.i("RestoreSession", "Session already timed out");
            return -1;
        }
        String str2 = this.mPackageName;
        if (str2 != null && !str2.equals(str)) {
            Slog.e("RestoreSession", "Ignoring attempt to restore pkg=" + str + " on session for package " + this.mPackageName);
            return -1;
        }
        try {
            final PackageInfo packageInfoAsUser = this.mBackupManagerService.getPackageManager().getPackageInfoAsUser(str, 0, this.mUserId);
            if (this.mBackupManagerService.getContext().checkPermission("android.permission.BACKUP", Binder.getCallingPid(), Binder.getCallingUid()) == -1 && packageInfoAsUser.applicationInfo.uid != Binder.getCallingUid()) {
                Slog.w("RestoreSession", "restorePackage: bad packageName=" + str + " or calling uid=" + Binder.getCallingUid());
                throw new SecurityException("No permission to restore other packages");
            }
            if (!this.mTransportManager.isTransportRegistered(this.mTransportName)) {
                Slog.e("RestoreSession", "Transport " + this.mTransportName + " not registered");
                return -1;
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            final long availableRestoreToken = this.mBackupManagerService.getAvailableRestoreToken(str);
            Slog.v("RestoreSession", "restorePackage pkg=" + str + " token=" + Long.toHexString(availableRestoreToken));
            if (availableRestoreToken == 0) {
                Slog.w("RestoreSession", "No data available for this package; not restoring");
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return -1;
            }
            BiFunction<TransportConnection, OnTaskFinishedListener, RestoreParams> biFunction = new BiFunction() { // from class: com.android.server.backup.restore.ActiveRestoreSession$$ExternalSyntheticLambda3
                @Override // java.util.function.BiFunction
                public final Object apply(Object obj, Object obj2) {
                    RestoreParams lambda$restorePackage$3;
                    lambda$restorePackage$3 = ActiveRestoreSession.this.lambda$restorePackage$3(iRestoreObserver, iBackupManagerMonitor, availableRestoreToken, packageInfoAsUser, (TransportConnection) obj, (OnTaskFinishedListener) obj2);
                    return lambda$restorePackage$3;
                }
            };
            int sendRestoreToHandlerLocked = sendRestoreToHandlerLocked(biFunction, "RestoreSession.restorePackage(" + str + ")");
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return sendRestoreToHandlerLocked;
        } catch (PackageManager.NameNotFoundException unused) {
            Slog.w("RestoreSession", "Asked to restore nonexistent pkg " + str);
            return -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ RestoreParams lambda$restorePackage$3(IRestoreObserver iRestoreObserver, IBackupManagerMonitor iBackupManagerMonitor, long j, PackageInfo packageInfo, TransportConnection transportConnection, OnTaskFinishedListener onTaskFinishedListener) {
        return RestoreParams.createForSinglePackage(transportConnection, iRestoreObserver, iBackupManagerMonitor, j, packageInfo, onTaskFinishedListener, this.mBackupEligibilityRules);
    }

    public void setRestoreSets(List<RestoreSet> list) {
        this.mRestoreSets = list;
    }

    public final int sendRestoreToHandlerLocked(BiFunction<TransportConnection, OnTaskFinishedListener, RestoreParams> biFunction, String str) {
        final TransportConnection transportClient = this.mTransportManager.getTransportClient(this.mTransportName, str);
        if (transportClient == null) {
            Slog.e("RestoreSession", "Transport " + this.mTransportName + " got unregistered");
            return -1;
        }
        Handler backupHandler = this.mBackupManagerService.getBackupHandler();
        backupHandler.removeMessages(8);
        final UserBackupManagerService.BackupWakeLock wakelock = this.mBackupManagerService.getWakelock();
        wakelock.acquire();
        final TransportManager transportManager = this.mTransportManager;
        OnTaskFinishedListener onTaskFinishedListener = new OnTaskFinishedListener() { // from class: com.android.server.backup.restore.ActiveRestoreSession$$ExternalSyntheticLambda4
            @Override // com.android.server.backup.internal.OnTaskFinishedListener
            public final void onFinished(String str2) {
                ActiveRestoreSession.lambda$sendRestoreToHandlerLocked$4(TransportManager.this, transportClient, wakelock, str2);
            }
        };
        Message obtainMessage = backupHandler.obtainMessage(3);
        obtainMessage.obj = biFunction.apply(transportClient, onTaskFinishedListener);
        backupHandler.sendMessage(obtainMessage);
        return 0;
    }

    public static /* synthetic */ void lambda$sendRestoreToHandlerLocked$4(TransportManager transportManager, TransportConnection transportConnection, UserBackupManagerService.BackupWakeLock backupWakeLock, String str) {
        transportManager.disposeOfTransportClient(transportConnection, str);
        backupWakeLock.release();
    }

    /* loaded from: classes.dex */
    public class EndRestoreRunnable implements Runnable {
        public UserBackupManagerService mBackupManager;
        public ActiveRestoreSession mSession;

        public EndRestoreRunnable(UserBackupManagerService userBackupManagerService, ActiveRestoreSession activeRestoreSession) {
            this.mBackupManager = userBackupManagerService;
            this.mSession = activeRestoreSession;
        }

        @Override // java.lang.Runnable
        public void run() {
            ActiveRestoreSession activeRestoreSession;
            synchronized (this.mSession) {
                activeRestoreSession = this.mSession;
                activeRestoreSession.mEnded = true;
            }
            this.mBackupManager.clearRestoreSession(activeRestoreSession);
        }
    }

    public synchronized void endRestoreSession() {
        Slog.d("RestoreSession", "endRestoreSession");
        if (this.mTimedOut) {
            Slog.i("RestoreSession", "Session already timed out");
        } else if (this.mEnded) {
            throw new IllegalStateException("Restore session already ended");
        } else {
            this.mBackupManagerService.getBackupHandler().post(new EndRestoreRunnable(this.mBackupManagerService, this));
        }
    }
}
