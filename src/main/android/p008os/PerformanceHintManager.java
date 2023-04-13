package android.p008os;

import android.content.Context;
import android.p008os.ServiceManager;
import com.android.internal.util.Preconditions;
import java.io.Closeable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.Reference;
/* renamed from: android.os.PerformanceHintManager */
/* loaded from: classes3.dex */
public final class PerformanceHintManager {
    private final long mNativeManagerPtr;

    private static native long nativeAcquireManager();

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeCloseSession(long j);

    private static native long nativeCreateSession(long j, int[] iArr, long j2);

    private static native long nativeGetPreferredUpdateRateNanos(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native int[] nativeGetThreadIds(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeReportActualWorkDuration(long j, long j2);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeSendHint(long j, int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeSetThreads(long j, int[] iArr);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeUpdateTargetWorkDuration(long j, long j2);

    public static PerformanceHintManager create() throws ServiceManager.ServiceNotFoundException {
        long nativeManagerPtr = nativeAcquireManager();
        if (nativeManagerPtr == 0) {
            throw new ServiceManager.ServiceNotFoundException(Context.PERFORMANCE_HINT_SERVICE);
        }
        return new PerformanceHintManager(nativeManagerPtr);
    }

    private PerformanceHintManager(long nativeManagerPtr) {
        this.mNativeManagerPtr = nativeManagerPtr;
    }

    public Session createHintSession(int[] tids, long initialTargetWorkDurationNanos) {
        Preconditions.checkNotNull(tids, "tids cannot be null");
        Preconditions.checkArgumentPositive((float) initialTargetWorkDurationNanos, "the hint target duration should be positive.");
        long nativeSessionPtr = nativeCreateSession(this.mNativeManagerPtr, tids, initialTargetWorkDurationNanos);
        if (nativeSessionPtr == 0) {
            return null;
        }
        return new Session(nativeSessionPtr);
    }

    public long getPreferredUpdateRateNanos() {
        return nativeGetPreferredUpdateRateNanos(this.mNativeManagerPtr);
    }

    /* renamed from: android.os.PerformanceHintManager$Session */
    /* loaded from: classes3.dex */
    public static class Session implements Closeable {
        public static final int CPU_LOAD_DOWN = 1;
        public static final int CPU_LOAD_RESET = 2;
        public static final int CPU_LOAD_RESUME = 3;
        public static final int CPU_LOAD_UP = 0;
        private long mNativeSessionPtr;

        @Retention(RetentionPolicy.SOURCE)
        /* renamed from: android.os.PerformanceHintManager$Session$Hint */
        /* loaded from: classes3.dex */
        public @interface Hint {
        }

        public Session(long nativeSessionPtr) {
            this.mNativeSessionPtr = nativeSessionPtr;
        }

        protected void finalize() throws Throwable {
            try {
                close();
            } finally {
                super.finalize();
            }
        }

        public void updateTargetWorkDuration(long targetDurationNanos) {
            Preconditions.checkArgumentPositive((float) targetDurationNanos, "the hint target duration should be positive.");
            PerformanceHintManager.nativeUpdateTargetWorkDuration(this.mNativeSessionPtr, targetDurationNanos);
        }

        public void reportActualWorkDuration(long actualDurationNanos) {
            Preconditions.checkArgumentPositive((float) actualDurationNanos, "the actual duration should be positive.");
            PerformanceHintManager.nativeReportActualWorkDuration(this.mNativeSessionPtr, actualDurationNanos);
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            long j = this.mNativeSessionPtr;
            if (j != 0) {
                PerformanceHintManager.nativeCloseSession(j);
                this.mNativeSessionPtr = 0L;
            }
        }

        public void sendHint(int hint) {
            Preconditions.checkArgumentNonNegative(hint, "the hint ID should be at least zero.");
            try {
                PerformanceHintManager.nativeSendHint(this.mNativeSessionPtr, hint);
            } finally {
                Reference.reachabilityFence(this);
            }
        }

        public void setThreads(int[] tids) {
            long j = this.mNativeSessionPtr;
            if (j == 0) {
                return;
            }
            if (tids.length == 0) {
                throw new IllegalArgumentException("Thread id list can't be empty.");
            }
            PerformanceHintManager.nativeSetThreads(j, tids);
        }

        public int[] getThreadIds() {
            return PerformanceHintManager.nativeGetThreadIds(this.mNativeSessionPtr);
        }
    }
}
