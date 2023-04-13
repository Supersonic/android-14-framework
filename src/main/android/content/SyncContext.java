package android.content;

import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.p008os.SystemClock;
/* loaded from: classes.dex */
public class SyncContext {
    private static final long HEARTBEAT_SEND_INTERVAL_IN_MS = 1000;
    private long mLastHeartbeatSendTime = 0;
    private ISyncContext mSyncContext;

    public SyncContext(ISyncContext syncContextInterface) {
        this.mSyncContext = syncContextInterface;
    }

    public void setStatusText(String message) {
        updateHeartbeat();
    }

    private void updateHeartbeat() {
        long now = SystemClock.elapsedRealtime();
        if (now < this.mLastHeartbeatSendTime + 1000) {
            return;
        }
        try {
            this.mLastHeartbeatSendTime = now;
            ISyncContext iSyncContext = this.mSyncContext;
            if (iSyncContext != null) {
                iSyncContext.sendHeartbeat();
            }
        } catch (RemoteException e) {
        }
    }

    public void onFinished(SyncResult result) {
        try {
            ISyncContext iSyncContext = this.mSyncContext;
            if (iSyncContext != null) {
                iSyncContext.onFinished(result);
            }
        } catch (RemoteException e) {
        }
    }

    public IBinder getSyncContextBinder() {
        ISyncContext iSyncContext = this.mSyncContext;
        if (iSyncContext == null) {
            return null;
        }
        return iSyncContext.asBinder();
    }
}
