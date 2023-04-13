package com.android.server;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.IUpdateLock;
import android.os.RemoteException;
import android.os.TokenWatcher;
import android.os.UserHandle;
import com.android.internal.util.DumpUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public class UpdateLockService extends IUpdateLock.Stub {
    public Context mContext;
    public LockWatcher mLocks = new LockWatcher(new Handler(), "UpdateLocks");

    /* loaded from: classes.dex */
    public class LockWatcher extends TokenWatcher {
        public LockWatcher(Handler handler, String str) {
            super(handler, str);
        }

        @Override // android.os.TokenWatcher
        public void acquired() {
            UpdateLockService.this.sendLockChangedBroadcast(false);
        }

        @Override // android.os.TokenWatcher
        public void released() {
            UpdateLockService.this.sendLockChangedBroadcast(true);
        }
    }

    public UpdateLockService(Context context) {
        this.mContext = context;
        sendLockChangedBroadcast(true);
    }

    public void sendLockChangedBroadcast(boolean z) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mContext.sendStickyBroadcastAsUser(new Intent("android.os.UpdateLock.UPDATE_LOCK_CHANGED").putExtra("nowisconvenient", z).putExtra("timestamp", System.currentTimeMillis()).addFlags(67108864), UserHandle.ALL);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void acquireUpdateLock(IBinder iBinder, String str) throws RemoteException {
        this.mContext.enforceCallingOrSelfPermission("android.permission.UPDATE_LOCK", "acquireUpdateLock");
        this.mLocks.acquire(iBinder, makeTag(str));
    }

    public void releaseUpdateLock(IBinder iBinder) throws RemoteException {
        this.mContext.enforceCallingOrSelfPermission("android.permission.UPDATE_LOCK", "releaseUpdateLock");
        this.mLocks.release(iBinder);
    }

    public final String makeTag(String str) {
        return "{tag=" + str + " uid=" + Binder.getCallingUid() + " pid=" + Binder.getCallingPid() + '}';
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpPermission(this.mContext, "UpdateLockService", printWriter)) {
            this.mLocks.dump(printWriter);
        }
    }
}
