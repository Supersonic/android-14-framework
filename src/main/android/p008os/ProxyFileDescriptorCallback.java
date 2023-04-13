package android.p008os;

import android.system.ErrnoException;
import android.system.OsConstants;
/* renamed from: android.os.ProxyFileDescriptorCallback */
/* loaded from: classes3.dex */
public abstract class ProxyFileDescriptorCallback {
    public abstract void onRelease();

    public long onGetSize() throws ErrnoException {
        throw new ErrnoException("onGetSize", OsConstants.EBADF);
    }

    public int onRead(long offset, int size, byte[] data) throws ErrnoException {
        throw new ErrnoException("onRead", OsConstants.EBADF);
    }

    public int onWrite(long offset, int size, byte[] data) throws ErrnoException {
        throw new ErrnoException("onWrite", OsConstants.EBADF);
    }

    public void onFsync() throws ErrnoException {
        throw new ErrnoException("onFsync", OsConstants.EINVAL);
    }
}
