package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
import android.media.p007tv.tuner.TunerUtils;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.TimeFilter */
/* loaded from: classes2.dex */
public class TimeFilter implements AutoCloseable {
    private boolean mEnable = false;
    private long mNativeContext;

    private native int nativeClearTimestamp();

    private native int nativeClose();

    private native Long nativeGetSourceTime();

    private native Long nativeGetTimestamp();

    private native int nativeSetTimestamp(long j);

    private TimeFilter() {
    }

    public int setCurrentTimestamp(long timestamp) {
        int res = nativeSetTimestamp(timestamp);
        if (res == 0) {
            this.mEnable = true;
        }
        return res;
    }

    public int clearTimestamp() {
        int res = nativeClearTimestamp();
        if (res == 0) {
            this.mEnable = false;
        }
        return res;
    }

    public long getTimeStamp() {
        if (!this.mEnable) {
            return -1L;
        }
        return nativeGetTimestamp().longValue();
    }

    public long getSourceTime() {
        if (!this.mEnable) {
            return -1L;
        }
        return nativeGetSourceTime().longValue();
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        int res = nativeClose();
        if (res != 0) {
            TunerUtils.throwExceptionForResult(res, "Failed to close time filter.");
        }
    }
}
