package android.graphics;

import android.p008os.Handler;
import com.android.internal.util.VirtualRefBasePtr;
import java.lang.ref.WeakReference;
/* loaded from: classes.dex */
public final class HardwareRendererObserver {
    private final long[] mFrameMetrics;
    private final Handler mHandler;
    private final OnFrameMetricsAvailableListener mListener;
    private VirtualRefBasePtr mNativePtr;

    /* loaded from: classes.dex */
    public interface OnFrameMetricsAvailableListener {
        void onFrameMetricsAvailable(int i);
    }

    private static native long nCreateObserver(WeakReference<HardwareRendererObserver> weakReference, boolean z);

    private static native int nGetNextBuffer(long j, long[] jArr);

    public HardwareRendererObserver(OnFrameMetricsAvailableListener listener, long[] frameMetrics, Handler handler, boolean waitForPresentTime) {
        if (handler == null || handler.getLooper() == null) {
            throw new NullPointerException("handler and its looper cannot be null");
        }
        if (handler.getLooper().getQueue() == null) {
            throw new IllegalStateException("invalid looper, null message queue\n");
        }
        this.mFrameMetrics = frameMetrics;
        this.mHandler = handler;
        this.mListener = listener;
        this.mNativePtr = new VirtualRefBasePtr(nCreateObserver(new WeakReference(this), waitForPresentTime));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getNativeInstance() {
        return this.mNativePtr.get();
    }

    private void notifyDataAvailable() {
        this.mHandler.post(new Runnable() { // from class: android.graphics.HardwareRendererObserver$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                HardwareRendererObserver.this.lambda$notifyDataAvailable$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyDataAvailable$0() {
        boolean hasMoreData = true;
        while (hasMoreData) {
            int dropCount = nGetNextBuffer(this.mNativePtr.get(), this.mFrameMetrics);
            if (dropCount >= 0) {
                this.mListener.onFrameMetricsAvailable(dropCount);
            } else {
                hasMoreData = false;
            }
        }
    }

    static boolean invokeDataAvailable(WeakReference<HardwareRendererObserver> weakObserver) {
        HardwareRendererObserver observer = weakObserver.get();
        if (observer != null) {
            observer.notifyDataAvailable();
            return true;
        }
        return false;
    }
}
