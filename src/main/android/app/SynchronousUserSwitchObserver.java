package android.app;

import android.p008os.IRemoteCallback;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public abstract class SynchronousUserSwitchObserver extends UserSwitchObserver {
    public abstract void onUserSwitching(int i) throws RemoteException;

    @Override // android.app.UserSwitchObserver, android.app.IUserSwitchObserver
    public final void onUserSwitching(int newUserId, IRemoteCallback reply) throws RemoteException {
        try {
            onUserSwitching(newUserId);
        } finally {
            if (reply != null) {
                reply.sendResult(null);
            }
        }
    }
}
