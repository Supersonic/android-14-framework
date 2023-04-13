package android.media.p007tv.tuner;

import android.annotation.SystemApi;
import android.media.p007tv.tuner.filter.Filter;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
@SystemApi
/* renamed from: android.media.tv.tuner.Descrambler */
/* loaded from: classes2.dex */
public class Descrambler implements AutoCloseable {
    public static final int PID_TYPE_MMTP = 2;
    public static final int PID_TYPE_T = 1;
    private static final String TAG = "Descrambler";
    private boolean mIsClosed = false;
    private final Object mLock = new Object();
    private long mNativeContext;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.Descrambler$PidType */
    /* loaded from: classes2.dex */
    public @interface PidType {
    }

    private native int nativeAddPid(int i, int i2, Filter filter);

    private native int nativeClose();

    private native int nativeRemovePid(int i, int i2, Filter filter);

    private native int nativeSetKeyToken(byte[] bArr);

    private Descrambler() {
    }

    public int addPid(int pidType, int pid, Filter filter) {
        int nativeAddPid;
        synchronized (this.mLock) {
            TunerUtils.checkResourceState(TAG, this.mIsClosed);
            nativeAddPid = nativeAddPid(pidType, pid, filter);
        }
        return nativeAddPid;
    }

    public int removePid(int pidType, int pid, Filter filter) {
        int nativeRemovePid;
        synchronized (this.mLock) {
            TunerUtils.checkResourceState(TAG, this.mIsClosed);
            nativeRemovePid = nativeRemovePid(pidType, pid, filter);
        }
        return nativeRemovePid;
    }

    public int setKeyToken(byte[] keyToken) {
        synchronized (this.mLock) {
            TunerUtils.checkResourceState(TAG, this.mIsClosed);
            Objects.requireNonNull(keyToken, "key token must not be null");
            if (isValidKeyToken(keyToken)) {
                return nativeSetKeyToken(keyToken);
            }
            return 4;
        }
    }

    public static boolean isValidKeyToken(byte[] keyToken) {
        if (keyToken.length == 0 || keyToken.length > 16) {
            Log.m112d(TAG, "Invalid key token size: " + (keyToken.length * 8) + " bit.");
            return false;
        }
        return true;
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        synchronized (this.mLock) {
            if (this.mIsClosed) {
                return;
            }
            int res = nativeClose();
            if (res != 0) {
                TunerUtils.throwExceptionForResult(res, "Failed to close descrambler");
            } else {
                this.mIsClosed = true;
            }
        }
    }
}
