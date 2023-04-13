package com.android.server.storage;

import android.os.FileUtils;
import android.os.ParcelFileDescriptor;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.FuseUnavailableMountException;
import com.android.internal.util.Preconditions;
import com.android.server.AppFuseMountException;
import java.util.concurrent.CountDownLatch;
import libcore.io.IoUtils;
/* loaded from: classes2.dex */
public class AppFuseBridge implements Runnable {
    public static final String APPFUSE_MOUNT_NAME_TEMPLATE = "/mnt/appfuse/%d_%d";
    public static final String TAG = "AppFuseBridge";
    @GuardedBy({"this"})
    public final SparseArray<MountScope> mScopes = new SparseArray<>();
    @GuardedBy({"this"})
    public long mNativeLoop = native_new();

    private native int native_add_bridge(long j, int i, int i2);

    private native void native_delete(long j);

    private native void native_lock();

    private native long native_new();

    private native void native_start_loop(long j);

    private native void native_unlock();

    public ParcelFileDescriptor addBridge(MountScope mountScope) throws FuseUnavailableMountException, AppFuseMountException {
        ParcelFileDescriptor adoptFd;
        native_lock();
        try {
            synchronized (this) {
                Preconditions.checkArgument(this.mScopes.indexOfKey(mountScope.mountId) < 0);
                long j = this.mNativeLoop;
                if (j == 0) {
                    throw new FuseUnavailableMountException(mountScope.mountId);
                }
                int native_add_bridge = native_add_bridge(j, mountScope.mountId, mountScope.open().detachFd());
                if (native_add_bridge == -1) {
                    throw new FuseUnavailableMountException(mountScope.mountId);
                }
                adoptFd = ParcelFileDescriptor.adoptFd(native_add_bridge);
                this.mScopes.put(mountScope.mountId, mountScope);
            }
            native_unlock();
            IoUtils.closeQuietly((AutoCloseable) null);
            return adoptFd;
        } catch (Throwable th) {
            native_unlock();
            IoUtils.closeQuietly(mountScope);
            throw th;
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        native_start_loop(this.mNativeLoop);
        synchronized (this) {
            native_delete(this.mNativeLoop);
            this.mNativeLoop = 0L;
        }
    }

    public ParcelFileDescriptor openFile(int i, int i2, int i3) throws FuseUnavailableMountException, InterruptedException {
        MountScope mountScope;
        synchronized (this) {
            mountScope = this.mScopes.get(i);
            if (mountScope == null) {
                throw new FuseUnavailableMountException(i);
            }
        }
        if (!mountScope.waitForMount()) {
            throw new FuseUnavailableMountException(i);
        }
        try {
            return mountScope.openFile(i, i2, FileUtils.translateModePfdToPosix(i3));
        } catch (AppFuseMountException unused) {
            throw new FuseUnavailableMountException(i);
        }
    }

    public final synchronized void onMount(int i) {
        MountScope mountScope = this.mScopes.get(i);
        if (mountScope != null) {
            mountScope.setMountResultLocked(true);
        }
    }

    public final synchronized void onClosed(int i) {
        MountScope mountScope = this.mScopes.get(i);
        if (mountScope != null) {
            mountScope.setMountResultLocked(false);
            IoUtils.closeQuietly(mountScope);
            this.mScopes.remove(i);
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class MountScope implements AutoCloseable {
        public final int mountId;
        public final int uid;
        public final CountDownLatch mMounted = new CountDownLatch(1);
        public boolean mMountResult = false;

        public abstract ParcelFileDescriptor open() throws AppFuseMountException;

        public abstract ParcelFileDescriptor openFile(int i, int i2, int i3) throws AppFuseMountException;

        public MountScope(int i, int i2) {
            this.uid = i;
            this.mountId = i2;
        }

        @GuardedBy({"AppFuseBridge.this"})
        public void setMountResultLocked(boolean z) {
            if (this.mMounted.getCount() == 0) {
                return;
            }
            this.mMountResult = z;
            this.mMounted.countDown();
        }

        public boolean waitForMount() throws InterruptedException {
            this.mMounted.await();
            return this.mMountResult;
        }
    }
}
