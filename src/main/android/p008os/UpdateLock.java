package android.p008os;

import android.content.Context;
import android.p008os.IUpdateLock;
import android.util.Log;
/* renamed from: android.os.UpdateLock */
/* loaded from: classes3.dex */
public class UpdateLock {
    private static final boolean DEBUG = false;
    public static final String NOW_IS_CONVENIENT = "nowisconvenient";
    private static final String TAG = "UpdateLock";
    public static final String TIMESTAMP = "timestamp";
    public static final String UPDATE_LOCK_CHANGED = "android.os.UpdateLock.UPDATE_LOCK_CHANGED";
    private static IUpdateLock sService;
    final String mTag;
    int mCount = 0;
    boolean mRefCounted = true;
    boolean mHeld = false;
    IBinder mToken = new Binder();

    private static void checkService() {
        if (sService == null) {
            sService = IUpdateLock.Stub.asInterface(ServiceManager.getService(Context.UPDATE_LOCK_SERVICE));
        }
    }

    public UpdateLock(String tag) {
        this.mTag = tag;
    }

    public void setReferenceCounted(boolean isRefCounted) {
        this.mRefCounted = isRefCounted;
    }

    public boolean isHeld() {
        boolean z;
        synchronized (this.mToken) {
            z = this.mHeld;
        }
        return z;
    }

    public void acquire() {
        checkService();
        synchronized (this.mToken) {
            acquireLocked();
        }
    }

    private void acquireLocked() {
        if (this.mRefCounted) {
            int i = this.mCount;
            this.mCount = i + 1;
            if (i != 0) {
                return;
            }
        }
        IUpdateLock iUpdateLock = sService;
        if (iUpdateLock != null) {
            try {
                iUpdateLock.acquireUpdateLock(this.mToken, this.mTag);
            } catch (RemoteException e) {
                Log.m110e(TAG, "Unable to contact service to acquire");
            }
        }
        this.mHeld = true;
    }

    public void release() {
        checkService();
        synchronized (this.mToken) {
            releaseLocked();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:5:0x000a, code lost:
        if (r0 == 0) goto L11;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void releaseLocked() {
        if (this.mRefCounted) {
            int i = this.mCount - 1;
            this.mCount = i;
        }
        IUpdateLock iUpdateLock = sService;
        if (iUpdateLock != null) {
            try {
                iUpdateLock.releaseUpdateLock(this.mToken);
            } catch (RemoteException e) {
                Log.m110e(TAG, "Unable to contact service to release");
            }
        }
        this.mHeld = false;
        if (this.mCount < 0) {
            throw new RuntimeException("UpdateLock under-locked");
        }
    }

    protected void finalize() throws Throwable {
        synchronized (this.mToken) {
            if (this.mHeld) {
                Log.wtf(TAG, "UpdateLock finalized while still held");
                try {
                    sService.releaseUpdateLock(this.mToken);
                } catch (RemoteException e) {
                    Log.m110e(TAG, "Unable to contact service to release");
                }
            }
        }
    }
}
