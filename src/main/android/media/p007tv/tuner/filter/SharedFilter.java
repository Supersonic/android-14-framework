package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
import android.media.p007tv.tuner.TunerUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.Executor;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.SharedFilter */
/* loaded from: classes2.dex */
public final class SharedFilter implements AutoCloseable {
    public static final int STATUS_INACCESSIBLE = 128;
    private static final String TAG = "SharedFilter";
    private SharedFilterCallback mCallback;
    private Object mCallbackLock;
    private Executor mExecutor;
    private Object mLock;
    private long mNativeContext;
    private boolean mIsClosed = false;
    private boolean mIsAccessible = true;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.filter.SharedFilter$Status */
    /* loaded from: classes2.dex */
    public @interface Status {
    }

    private native int nativeFlushSharedFilter();

    private native int nativeSharedClose();

    private native int nativeSharedRead(byte[] bArr, long j, long j2);

    private native int nativeStartSharedFilter();

    private native int nativeStopSharedFilter();

    private SharedFilter() {
        this.mCallbackLock = null;
        this.mLock = null;
        this.mCallbackLock = new Object();
        this.mLock = new Object();
    }

    private void onFilterStatus(final int status) {
        Executor executor;
        synchronized (this.mLock) {
            if (status == 128) {
                this.mIsAccessible = false;
            }
        }
        synchronized (this.mCallbackLock) {
            if (this.mCallback != null && (executor = this.mExecutor) != null) {
                executor.execute(new Runnable() { // from class: android.media.tv.tuner.filter.SharedFilter$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        SharedFilter.this.lambda$onFilterStatus$0(status);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onFilterStatus$0(int status) {
        synchronized (this.mCallbackLock) {
            SharedFilterCallback sharedFilterCallback = this.mCallback;
            if (sharedFilterCallback != null) {
                sharedFilterCallback.onFilterStatusChanged(this, status);
            }
        }
    }

    private void onFilterEvent(final FilterEvent[] events) {
        Executor executor;
        synchronized (this.mCallbackLock) {
            if (this.mCallback == null || (executor = this.mExecutor) == null) {
                for (FilterEvent event : events) {
                    if (event instanceof MediaEvent) {
                        ((MediaEvent) event).release();
                    }
                }
            } else {
                executor.execute(new Runnable() { // from class: android.media.tv.tuner.filter.SharedFilter$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SharedFilter.this.lambda$onFilterEvent$1(events);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onFilterEvent$1(FilterEvent[] events) {
        synchronized (this.mCallbackLock) {
            SharedFilterCallback sharedFilterCallback = this.mCallback;
            if (sharedFilterCallback != null) {
                sharedFilterCallback.onFilterEvent(this, events);
            } else {
                for (FilterEvent event : events) {
                    if (event instanceof MediaEvent) {
                        ((MediaEvent) event).release();
                    }
                }
            }
        }
    }

    public void setCallback(SharedFilterCallback cb, Executor executor) {
        synchronized (this.mCallbackLock) {
            this.mCallback = cb;
            this.mExecutor = executor;
        }
    }

    public SharedFilterCallback getCallback() {
        SharedFilterCallback sharedFilterCallback;
        synchronized (this.mCallbackLock) {
            sharedFilterCallback = this.mCallback;
        }
        return sharedFilterCallback;
    }

    public int start() {
        int nativeStartSharedFilter;
        synchronized (this.mLock) {
            TunerUtils.checkResourceAccessible(TAG, this.mIsAccessible);
            TunerUtils.checkResourceState(TAG, this.mIsClosed);
            nativeStartSharedFilter = nativeStartSharedFilter();
        }
        return nativeStartSharedFilter;
    }

    public int stop() {
        int nativeStopSharedFilter;
        synchronized (this.mLock) {
            TunerUtils.checkResourceAccessible(TAG, this.mIsAccessible);
            TunerUtils.checkResourceState(TAG, this.mIsClosed);
            nativeStopSharedFilter = nativeStopSharedFilter();
        }
        return nativeStopSharedFilter;
    }

    public int flush() {
        int nativeFlushSharedFilter;
        synchronized (this.mLock) {
            TunerUtils.checkResourceAccessible(TAG, this.mIsAccessible);
            TunerUtils.checkResourceState(TAG, this.mIsClosed);
            nativeFlushSharedFilter = nativeFlushSharedFilter();
        }
        return nativeFlushSharedFilter;
    }

    public int read(byte[] buffer, long offset, long size) {
        synchronized (this.mLock) {
            try {
                try {
                    TunerUtils.checkResourceAccessible(TAG, this.mIsAccessible);
                    TunerUtils.checkResourceState(TAG, this.mIsClosed);
                    return nativeSharedRead(buffer, offset, Math.min(size, buffer.length - offset));
                } catch (Throwable th) {
                    th = th;
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        synchronized (this.mLock) {
            if (this.mIsClosed) {
                return;
            }
            synchronized (this.mCallbackLock) {
                this.mCallback = null;
                this.mExecutor = null;
            }
            nativeSharedClose();
            this.mIsClosed = true;
            this.mCallbackLock = null;
        }
    }
}
