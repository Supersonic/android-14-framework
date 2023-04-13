package android.media.p007tv.tuner.dvr;

import android.annotation.SystemApi;
import android.media.p007tv.tuner.TunerUtils;
import android.media.p007tv.tuner.TunerVersionChecker;
import android.media.p007tv.tuner.filter.Filter;
import android.p008os.ParcelFileDescriptor;
import android.p008os.Process;
import android.util.Log;
import com.android.internal.util.FrameworkStatsLog;
import java.util.concurrent.Executor;
@SystemApi
/* renamed from: android.media.tv.tuner.dvr.DvrRecorder */
/* loaded from: classes2.dex */
public class DvrRecorder implements AutoCloseable {
    private static final String TAG = "TvTunerRecord";
    private static int sInstantId = 0;
    private Executor mExecutor;
    private OnRecordStatusChangedListener mListener;
    private long mNativeContext;
    private int mOverflow;
    private int mSegmentId;
    private final Object mIsStoppedLock = new Object();
    private boolean mIsStopped = true;
    private final Object mListenerLock = new Object();
    private int mUserId = Process.myUid();

    private native int nativeAttachFilter(Filter filter);

    private native int nativeClose();

    private native int nativeConfigureDvr(DvrSettings dvrSettings);

    private native int nativeDetachFilter(Filter filter);

    private native int nativeFlushDvr();

    private native void nativeSetFileDescriptor(int i);

    private native int nativeSetStatusCheckIntervalHint(long j);

    private native int nativeStartDvr();

    private native int nativeStopDvr();

    private native long nativeWrite(long j);

    private native long nativeWrite(byte[] bArr, long j, long j2);

    private DvrRecorder() {
        this.mSegmentId = 0;
        int i = sInstantId;
        this.mSegmentId = (65535 & i) << 16;
        sInstantId = i + 1;
    }

    public void setListener(Executor executor, OnRecordStatusChangedListener listener) {
        synchronized (this.mListenerLock) {
            this.mExecutor = executor;
            this.mListener = listener;
        }
    }

    private void onRecordStatusChanged(final int status) {
        if (status == 8) {
            this.mOverflow++;
        }
        synchronized (this.mListenerLock) {
            Executor executor = this.mExecutor;
            if (executor != null && this.mListener != null) {
                executor.execute(new Runnable() { // from class: android.media.tv.tuner.dvr.DvrRecorder$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        DvrRecorder.this.lambda$onRecordStatusChanged$0(status);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onRecordStatusChanged$0(int status) {
        synchronized (this.mListenerLock) {
            OnRecordStatusChangedListener onRecordStatusChangedListener = this.mListener;
            if (onRecordStatusChangedListener != null) {
                onRecordStatusChangedListener.onRecordStatusChanged(status);
            }
        }
    }

    public int attachFilter(Filter filter) {
        return nativeAttachFilter(filter);
    }

    public int detachFilter(Filter filter) {
        return nativeDetachFilter(filter);
    }

    public int configure(DvrSettings settings) {
        return nativeConfigureDvr(settings);
    }

    public int setRecordBufferStatusCheckIntervalHint(long durationInMs) {
        if (!TunerVersionChecker.checkHigherOrEqualVersionTo(196608, "Set status check interval hint")) {
            return 1;
        }
        return nativeSetStatusCheckIntervalHint(durationInMs);
    }

    public int start() {
        int result;
        int i = this.mSegmentId;
        this.mSegmentId = (((i & 65535) + 1) & 65535) | ((-65536) & i);
        this.mOverflow = 0;
        Log.m112d(TAG, "Write Stats Log for Record.");
        FrameworkStatsLog.write(279, this.mUserId, 2, 1, this.mSegmentId, 0);
        synchronized (this.mIsStoppedLock) {
            result = nativeStartDvr();
            if (result == 0) {
                this.mIsStopped = false;
            }
        }
        return result;
    }

    public int stop() {
        int result;
        Log.m112d(TAG, "Write Stats Log for Playback.");
        FrameworkStatsLog.write(279, this.mUserId, 2, 2, this.mSegmentId, this.mOverflow);
        synchronized (this.mIsStoppedLock) {
            result = nativeStopDvr();
            if (result == 0) {
                this.mIsStopped = true;
            }
        }
        return result;
    }

    public int flush() {
        synchronized (this.mIsStoppedLock) {
            if (this.mIsStopped) {
                return nativeFlushDvr();
            }
            Log.m104w(TAG, "Cannot flush non-stopped Record DVR.");
            return 3;
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        int res = nativeClose();
        if (res != 0) {
            TunerUtils.throwExceptionForResult(res, "failed to close DVR recorder");
        }
    }

    public void setFileDescriptor(ParcelFileDescriptor fd) {
        nativeSetFileDescriptor(fd.getFd());
    }

    public long write(long size) {
        return nativeWrite(size);
    }

    public long write(byte[] buffer, long offset, long size) {
        if (size + offset > buffer.length) {
            throw new ArrayIndexOutOfBoundsException("Array length=" + buffer.length + ", offset=" + offset + ", size=" + size);
        }
        return nativeWrite(buffer, offset, size);
    }
}
