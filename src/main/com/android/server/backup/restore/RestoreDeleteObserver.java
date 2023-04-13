package com.android.server.backup.restore;

import android.content.pm.IPackageDeleteObserver;
import android.os.RemoteException;
import com.android.internal.annotations.GuardedBy;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes.dex */
public class RestoreDeleteObserver extends IPackageDeleteObserver.Stub {
    @GuardedBy({"mDone"})
    public final AtomicBoolean mDone = new AtomicBoolean();

    public void packageDeleted(String str, int i) throws RemoteException {
        synchronized (this.mDone) {
            this.mDone.set(true);
            this.mDone.notifyAll();
        }
    }
}
