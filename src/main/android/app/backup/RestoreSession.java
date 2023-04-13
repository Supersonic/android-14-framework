package android.app.backup;

import android.annotation.SystemApi;
import android.app.backup.IRestoreObserver;
import android.content.Context;
import android.p008os.Handler;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.util.Log;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
@SystemApi
/* loaded from: classes.dex */
public class RestoreSession {
    static final String TAG = "RestoreSession";
    IRestoreSession mBinder;
    final Context mContext;
    RestoreObserverWrapper mObserver = null;

    public int getAvailableRestoreSets(RestoreObserver observer, BackupManagerMonitor monitor) {
        BackupManagerMonitorWrapper monitorWrapper;
        RestoreObserverWrapper obsWrapper = new RestoreObserverWrapper(this.mContext, observer);
        if (monitor == null) {
            monitorWrapper = null;
        } else {
            monitorWrapper = new BackupManagerMonitorWrapper(monitor);
        }
        try {
            int err = this.mBinder.getAvailableRestoreSets(obsWrapper, monitorWrapper);
            return err;
        } catch (RemoteException e) {
            Log.m112d(TAG, "Can't contact server to get available sets");
            return -1;
        }
    }

    public int getAvailableRestoreSets(RestoreObserver observer) {
        return getAvailableRestoreSets(observer, null);
    }

    public int restoreAll(long token, RestoreObserver observer, BackupManagerMonitor monitor) {
        BackupManagerMonitorWrapper monitorWrapper;
        if (this.mObserver != null) {
            Log.m112d(TAG, "restoreAll() called during active restore");
            return -1;
        }
        this.mObserver = new RestoreObserverWrapper(this.mContext, observer);
        if (monitor == null) {
            monitorWrapper = null;
        } else {
            monitorWrapper = new BackupManagerMonitorWrapper(monitor);
        }
        try {
            int err = this.mBinder.restoreAll(token, this.mObserver, monitorWrapper);
            return err;
        } catch (RemoteException e) {
            Log.m112d(TAG, "Can't contact server to restore");
            return -1;
        }
    }

    public int restoreAll(long token, RestoreObserver observer) {
        return restoreAll(token, observer, null);
    }

    public int restorePackages(long token, RestoreObserver observer, Set<String> packages, BackupManagerMonitor monitor) {
        BackupManagerMonitorWrapper monitorWrapper;
        if (this.mObserver != null) {
            Log.m112d(TAG, "restoreAll() called during active restore");
            return -1;
        }
        this.mObserver = new RestoreObserverWrapper(this.mContext, observer);
        if (monitor == null) {
            monitorWrapper = null;
        } else {
            monitorWrapper = new BackupManagerMonitorWrapper(monitor);
        }
        try {
            int err = this.mBinder.restorePackages(token, this.mObserver, (String[]) packages.toArray(new String[0]), monitorWrapper);
            return err;
        } catch (RemoteException e) {
            Log.m112d(TAG, "Can't contact server to restore packages");
            return -1;
        }
    }

    public int restorePackages(long token, RestoreObserver observer, Set<String> packages) {
        return restorePackages(token, observer, packages, null);
    }

    @Deprecated
    public int restoreSome(long token, RestoreObserver observer, BackupManagerMonitor monitor, String[] packages) {
        return restorePackages(token, observer, new HashSet(Arrays.asList(packages)), monitor);
    }

    @Deprecated
    public int restoreSome(long token, RestoreObserver observer, String[] packages) {
        return restoreSome(token, observer, null, packages);
    }

    public int restorePackage(String packageName, RestoreObserver observer, BackupManagerMonitor monitor) {
        BackupManagerMonitorWrapper monitorWrapper;
        if (this.mObserver != null) {
            Log.m112d(TAG, "restorePackage() called during active restore");
            return -1;
        }
        this.mObserver = new RestoreObserverWrapper(this.mContext, observer);
        if (monitor == null) {
            monitorWrapper = null;
        } else {
            monitorWrapper = new BackupManagerMonitorWrapper(monitor);
        }
        try {
            int err = this.mBinder.restorePackage(packageName, this.mObserver, monitorWrapper);
            return err;
        } catch (RemoteException e) {
            Log.m112d(TAG, "Can't contact server to restore package");
            return -1;
        }
    }

    public int restorePackage(String packageName, RestoreObserver observer) {
        return restorePackage(packageName, observer, null);
    }

    public void endRestoreSession() {
        try {
            try {
                this.mBinder.endRestoreSession();
            } catch (RemoteException e) {
                Log.m112d(TAG, "Can't contact server to get available sets");
            }
        } finally {
            this.mBinder = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RestoreSession(Context context, IRestoreSession binder) {
        this.mContext = context;
        this.mBinder = binder;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class RestoreObserverWrapper extends IRestoreObserver.Stub {
        static final int MSG_RESTORE_FINISHED = 3;
        static final int MSG_RESTORE_SETS_AVAILABLE = 4;
        static final int MSG_RESTORE_STARTING = 1;
        static final int MSG_UPDATE = 2;
        final RestoreObserver mAppObserver;
        final Handler mHandler;

        RestoreObserverWrapper(Context context, RestoreObserver appObserver) {
            this.mHandler = new Handler(context.getMainLooper()) { // from class: android.app.backup.RestoreSession.RestoreObserverWrapper.1
                @Override // android.p008os.Handler
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case 1:
                            RestoreObserverWrapper.this.mAppObserver.restoreStarting(msg.arg1);
                            return;
                        case 2:
                            RestoreObserverWrapper.this.mAppObserver.onUpdate(msg.arg1, (String) msg.obj);
                            return;
                        case 3:
                            RestoreObserverWrapper.this.mAppObserver.restoreFinished(msg.arg1);
                            return;
                        case 4:
                            RestoreObserverWrapper.this.mAppObserver.restoreSetsAvailable((RestoreSet[]) msg.obj);
                            return;
                        default:
                            return;
                    }
                }
            };
            this.mAppObserver = appObserver;
        }

        @Override // android.app.backup.IRestoreObserver
        public void restoreSetsAvailable(RestoreSet[] result) {
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(4, result));
        }

        @Override // android.app.backup.IRestoreObserver
        public void restoreStarting(int numPackages) {
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(1, numPackages, 0));
        }

        @Override // android.app.backup.IRestoreObserver
        public void onUpdate(int nowBeingRestored, String currentPackage) {
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(2, nowBeingRestored, 0, currentPackage));
        }

        @Override // android.app.backup.IRestoreObserver
        public void restoreFinished(int error) {
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(3, error, 0));
        }
    }
}
