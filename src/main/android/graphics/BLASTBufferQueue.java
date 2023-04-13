package android.graphics;

import android.view.Surface;
import android.view.SurfaceControl;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public final class BLASTBufferQueue {
    public long mNativeObject;

    /* loaded from: classes.dex */
    public interface TransactionHangCallback {
        void onTransactionHang(String str);
    }

    private static native void nativeApplyPendingTransactions(long j, long j2);

    private static native long nativeCreate(String str, boolean z);

    private static native void nativeDestroy(long j);

    private static native SurfaceControl.Transaction nativeGatherPendingTransactions(long j, long j2);

    private static native long nativeGetLastAcquiredFrameNum(long j);

    private static native Surface nativeGetSurface(long j, boolean z);

    private static native boolean nativeIsSameSurfaceControl(long j, long j2);

    private static native void nativeMergeWithNextTransaction(long j, long j2, long j3);

    private static native void nativeSetTransactionHangCallback(long j, TransactionHangCallback transactionHangCallback);

    private static native void nativeStopContinuousSyncTransaction(long j);

    private static native void nativeSyncNextTransaction(long j, Consumer<SurfaceControl.Transaction> consumer, boolean z);

    private static native void nativeUpdate(long j, long j2, long j3, long j4, int i);

    public BLASTBufferQueue(String name, SurfaceControl sc, int width, int height, int format) {
        this(name, true);
        update(sc, width, height, format);
    }

    public BLASTBufferQueue(String name, boolean updateDestinationFrame) {
        this.mNativeObject = nativeCreate(name, updateDestinationFrame);
    }

    public void destroy() {
        nativeDestroy(this.mNativeObject);
        this.mNativeObject = 0L;
    }

    public Surface createSurface() {
        return nativeGetSurface(this.mNativeObject, false);
    }

    public Surface createSurfaceWithHandle() {
        return nativeGetSurface(this.mNativeObject, true);
    }

    public void syncNextTransaction(boolean acquireSingleBuffer, Consumer<SurfaceControl.Transaction> callback) {
        nativeSyncNextTransaction(this.mNativeObject, callback, acquireSingleBuffer);
    }

    public void syncNextTransaction(Consumer<SurfaceControl.Transaction> callback) {
        syncNextTransaction(true, callback);
    }

    public void stopContinuousSyncTransaction() {
        nativeStopContinuousSyncTransaction(this.mNativeObject);
    }

    public void update(SurfaceControl sc, int width, int height, int format) {
        nativeUpdate(this.mNativeObject, sc.mNativeObject, width, height, format);
    }

    protected void finalize() throws Throwable {
        try {
            long j = this.mNativeObject;
            if (j != 0) {
                nativeDestroy(j);
            }
        } finally {
            super.finalize();
        }
    }

    public void mergeWithNextTransaction(SurfaceControl.Transaction t, long frameNumber) {
        nativeMergeWithNextTransaction(this.mNativeObject, t.mNativeObject, frameNumber);
    }

    public void mergeWithNextTransaction(long nativeTransaction, long frameNumber) {
        nativeMergeWithNextTransaction(this.mNativeObject, nativeTransaction, frameNumber);
    }

    public void applyPendingTransactions(long frameNumber) {
        nativeApplyPendingTransactions(this.mNativeObject, frameNumber);
    }

    public long getLastAcquiredFrameNum() {
        return nativeGetLastAcquiredFrameNum(this.mNativeObject);
    }

    public boolean isSameSurfaceControl(SurfaceControl sc) {
        return nativeIsSameSurfaceControl(this.mNativeObject, sc.mNativeObject);
    }

    public SurfaceControl.Transaction gatherPendingTransactions(long frameNumber) {
        return nativeGatherPendingTransactions(this.mNativeObject, frameNumber);
    }

    public void setTransactionHangCallback(TransactionHangCallback hangCallback) {
        nativeSetTransactionHangCallback(this.mNativeObject, hangCallback);
    }
}
