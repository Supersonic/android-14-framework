package android.renderscript;

import dalvik.system.CloseGuard;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
@Deprecated
/* loaded from: classes3.dex */
public class BaseObj {
    final CloseGuard guard = CloseGuard.get();
    private boolean mDestroyed;
    private long mID;
    private String mName;
    RenderScript mRS;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BaseObj(long id, RenderScript rs) {
        rs.validate();
        this.mRS = rs;
        this.mID = id;
        this.mDestroyed = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setID(long id) {
        if (this.mID != 0) {
            throw new RSRuntimeException("Internal Error, reset of object ID.");
        }
        this.mID = id;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getID(RenderScript rs) {
        this.mRS.validate();
        if (this.mDestroyed) {
            throw new RSInvalidStateException("using a destroyed object.");
        }
        long j = this.mID;
        if (j == 0) {
            throw new RSRuntimeException("Internal error: Object id 0.");
        }
        if (rs != null && rs != this.mRS) {
            throw new RSInvalidStateException("using object with mismatched context.");
        }
        return j;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void checkValid() {
        if (this.mID == 0) {
            throw new RSIllegalArgumentException("Invalid object.");
        }
    }

    public void setName(String name) {
        if (name == null) {
            throw new RSIllegalArgumentException("setName requires a string of non-zero length.");
        }
        if (name.length() < 1) {
            throw new RSIllegalArgumentException("setName does not accept a zero length string.");
        }
        if (this.mName != null) {
            throw new RSIllegalArgumentException("setName object already has a name.");
        }
        try {
            byte[] bytes = name.getBytes("UTF-8");
            this.mRS.nAssignName(this.mID, bytes);
            this.mName = name;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return this.mName;
    }

    private void helpDestroy() {
        boolean shouldDestroy = false;
        synchronized (this) {
            if (!this.mDestroyed) {
                shouldDestroy = true;
                this.mDestroyed = true;
            }
        }
        if (shouldDestroy) {
            this.guard.close();
            ReentrantReadWriteLock.ReadLock rlock = this.mRS.mRWLock.readLock();
            rlock.lock();
            if (this.mRS.isAlive()) {
                long j = this.mID;
                if (j != 0) {
                    this.mRS.nObjDestroy(j);
                }
            }
            rlock.unlock();
            this.mRS = null;
            this.mID = 0L;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void finalize() throws Throwable {
        try {
            CloseGuard closeGuard = this.guard;
            if (closeGuard != null) {
                closeGuard.warnIfOpen();
            }
            helpDestroy();
        } finally {
            super.finalize();
        }
    }

    public void destroy() {
        if (this.mDestroyed) {
            throw new RSInvalidStateException("Object already destroyed.");
        }
        helpDestroy();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateFromNative() {
        this.mRS.validate();
        RenderScript renderScript = this.mRS;
        this.mName = renderScript.nGetName(getID(renderScript));
    }

    public int hashCode() {
        long j = this.mID;
        return (int) ((j >> 32) ^ (268435455 & j));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BaseObj b = (BaseObj) obj;
        if (this.mID == b.mID) {
            return true;
        }
        return false;
    }
}
