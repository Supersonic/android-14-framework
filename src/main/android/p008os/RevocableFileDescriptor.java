package android.p008os;

import android.content.Context;
import android.p008os.ParcelFileDescriptor;
import android.p008os.storage.StorageManager;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.util.Slog;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InterruptedIOException;
import libcore.io.IoUtils;
/* renamed from: android.os.RevocableFileDescriptor */
/* loaded from: classes3.dex */
public class RevocableFileDescriptor {
    private static final boolean DEBUG = true;
    private static final String TAG = "RevocableFileDescriptor";
    private final ProxyFileDescriptorCallback mCallback = new ProxyFileDescriptorCallback() { // from class: android.os.RevocableFileDescriptor.1
        private void checkRevoked() throws ErrnoException {
            if (RevocableFileDescriptor.this.mRevoked) {
                throw new ErrnoException(RevocableFileDescriptor.TAG, OsConstants.EPERM);
            }
        }

        @Override // android.p008os.ProxyFileDescriptorCallback
        public long onGetSize() throws ErrnoException {
            checkRevoked();
            return Os.fstat(RevocableFileDescriptor.this.mInner).st_size;
        }

        @Override // android.p008os.ProxyFileDescriptorCallback
        public int onRead(long offset, int size, byte[] data) throws ErrnoException {
            checkRevoked();
            int n = 0;
            while (n < size) {
                try {
                    return n + Os.pread(RevocableFileDescriptor.this.mInner, data, n, size - n, offset + n);
                } catch (InterruptedIOException e) {
                    n += e.bytesTransferred;
                }
            }
            return n;
        }

        @Override // android.p008os.ProxyFileDescriptorCallback
        public int onWrite(long offset, int size, byte[] data) throws ErrnoException {
            checkRevoked();
            int n = 0;
            while (n < size) {
                try {
                    return n + Os.pwrite(RevocableFileDescriptor.this.mInner, data, n, size - n, offset + n);
                } catch (InterruptedIOException e) {
                    n += e.bytesTransferred;
                }
            }
            return n;
        }

        @Override // android.p008os.ProxyFileDescriptorCallback
        public void onFsync() throws ErrnoException {
            Slog.m92v(RevocableFileDescriptor.TAG, "onFsync()");
            checkRevoked();
            Os.fsync(RevocableFileDescriptor.this.mInner);
        }

        @Override // android.p008os.ProxyFileDescriptorCallback
        public void onRelease() {
            Slog.m92v(RevocableFileDescriptor.TAG, "onRelease()");
            RevocableFileDescriptor.this.mRevoked = true;
            IoUtils.closeQuietly(RevocableFileDescriptor.this.mInner);
            if (RevocableFileDescriptor.this.mOnCloseListener != null) {
                RevocableFileDescriptor.this.mOnCloseListener.onClose(null);
            }
        }
    };
    private FileDescriptor mInner;
    private ParcelFileDescriptor.OnCloseListener mOnCloseListener;
    private ParcelFileDescriptor mOuter;
    private volatile boolean mRevoked;

    public RevocableFileDescriptor() {
    }

    public RevocableFileDescriptor(Context context, File file) throws IOException {
        try {
            init(context, Os.open(file.getAbsolutePath(), OsConstants.O_CREAT | OsConstants.O_RDWR, 448));
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public RevocableFileDescriptor(Context context, FileDescriptor fd) throws IOException {
        init(context, fd);
    }

    public void init(Context context, FileDescriptor fd) throws IOException {
        this.mInner = fd;
        this.mOuter = ((StorageManager) context.getSystemService(StorageManager.class)).openProxyFileDescriptor(805306368, this.mCallback);
    }

    public ParcelFileDescriptor getRevocableFileDescriptor() {
        return this.mOuter;
    }

    public void revoke() {
        this.mRevoked = true;
        IoUtils.closeQuietly(this.mInner);
    }

    public void addOnCloseListener(ParcelFileDescriptor.OnCloseListener onCloseListener) {
        this.mOnCloseListener = onCloseListener;
    }

    public boolean isRevoked() {
        return this.mRevoked;
    }
}
