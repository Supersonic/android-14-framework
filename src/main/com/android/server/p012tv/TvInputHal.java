package com.android.server.p012tv;

import android.media.tv.TvInputHardwareInfo;
import android.media.tv.TvStreamConfig;
import android.os.Handler;
import android.os.Message;
import android.os.MessageQueue;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Surface;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: com.android.server.tv.TvInputHal */
/* loaded from: classes2.dex */
public final class TvInputHal implements Handler.Callback {
    public static final boolean DEBUG = false;
    public static final int ERROR_NO_INIT = -1;
    public static final int ERROR_STALE_CONFIG = -2;
    public static final int ERROR_UNKNOWN = -3;
    public static final int EVENT_DEVICE_AVAILABLE = 1;
    public static final int EVENT_DEVICE_UNAVAILABLE = 2;
    public static final int EVENT_FIRST_FRAME_CAPTURED = 4;
    public static final int EVENT_STREAM_CONFIGURATION_CHANGED = 3;
    public static final int SUCCESS = 0;
    public static final String TAG = TvInputHal.class.getSimpleName();
    public final Callback mCallback;
    public final Object mLock = new Object();
    public long mPtr = 0;
    public final SparseIntArray mStreamConfigGenerations = new SparseIntArray();
    public final SparseArray<TvStreamConfig[]> mStreamConfigs = new SparseArray<>();
    public final Handler mHandler = new Handler(this);

    /* renamed from: com.android.server.tv.TvInputHal$Callback */
    /* loaded from: classes2.dex */
    public interface Callback {
        void onDeviceAvailable(TvInputHardwareInfo tvInputHardwareInfo, TvStreamConfig[] tvStreamConfigArr);

        void onDeviceUnavailable(int i);

        void onFirstFrameCaptured(int i, int i2);

        void onStreamConfigurationChanged(int i, TvStreamConfig[] tvStreamConfigArr, int i2);
    }

    private static native int nativeAddOrUpdateStream(long j, int i, int i2, Surface surface);

    private static native void nativeClose(long j);

    private static native TvStreamConfig[] nativeGetStreamConfigs(long j, int i, int i2);

    private native long nativeOpen(MessageQueue messageQueue);

    private static native int nativeRemoveStream(long j, int i, int i2);

    public TvInputHal(Callback callback) {
        this.mCallback = callback;
    }

    public void init() {
        synchronized (this.mLock) {
            this.mPtr = nativeOpen(this.mHandler.getLooper().getQueue());
        }
    }

    public int addOrUpdateStream(int i, Surface surface, TvStreamConfig tvStreamConfig) {
        synchronized (this.mLock) {
            if (this.mPtr == 0) {
                return -1;
            }
            if (this.mStreamConfigGenerations.get(i, 0) != tvStreamConfig.getGeneration()) {
                return -2;
            }
            return nativeAddOrUpdateStream(this.mPtr, i, tvStreamConfig.getStreamId(), surface) == 0 ? 0 : -3;
        }
    }

    public int removeStream(int i, TvStreamConfig tvStreamConfig) {
        synchronized (this.mLock) {
            if (this.mPtr == 0) {
                return -1;
            }
            if (this.mStreamConfigGenerations.get(i, 0) != tvStreamConfig.getGeneration()) {
                return -2;
            }
            return nativeRemoveStream(this.mPtr, i, tvStreamConfig.getStreamId()) == 0 ? 0 : -3;
        }
    }

    public void close() {
        synchronized (this.mLock) {
            long j = this.mPtr;
            if (j != 0) {
                nativeClose(j);
            }
        }
    }

    public final void retrieveStreamConfigsLocked(int i) {
        int i2 = this.mStreamConfigGenerations.get(i, 0) + 1;
        this.mStreamConfigs.put(i, nativeGetStreamConfigs(this.mPtr, i, i2));
        this.mStreamConfigGenerations.put(i, i2);
    }

    public final void deviceAvailableFromNative(TvInputHardwareInfo tvInputHardwareInfo) {
        this.mHandler.obtainMessage(1, tvInputHardwareInfo).sendToTarget();
    }

    public final void deviceUnavailableFromNative(int i) {
        this.mHandler.obtainMessage(2, i, 0).sendToTarget();
    }

    public final void streamConfigsChangedFromNative(int i, int i2) {
        this.mHandler.obtainMessage(3, i, i2).sendToTarget();
    }

    public final void firstFrameCapturedFromNative(int i, int i2) {
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(3, i, i2));
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        TvStreamConfig[] tvStreamConfigArr;
        TvStreamConfig[] tvStreamConfigArr2;
        int i = message.what;
        if (i == 1) {
            TvInputHardwareInfo tvInputHardwareInfo = (TvInputHardwareInfo) message.obj;
            synchronized (this.mLock) {
                retrieveStreamConfigsLocked(tvInputHardwareInfo.getDeviceId());
                tvStreamConfigArr = this.mStreamConfigs.get(tvInputHardwareInfo.getDeviceId());
            }
            this.mCallback.onDeviceAvailable(tvInputHardwareInfo, tvStreamConfigArr);
        } else if (i == 2) {
            this.mCallback.onDeviceUnavailable(message.arg1);
        } else if (i == 3) {
            int i2 = message.arg1;
            int i3 = message.arg2;
            synchronized (this.mLock) {
                retrieveStreamConfigsLocked(i2);
                tvStreamConfigArr2 = this.mStreamConfigs.get(i2);
            }
            this.mCallback.onStreamConfigurationChanged(i2, tvStreamConfigArr2, i3);
        } else if (i == 4) {
            this.mCallback.onFirstFrameCaptured(message.arg1, message.arg2);
        } else {
            String str = TAG;
            Slog.e(str, "Unknown event: " + message);
            return false;
        }
        return true;
    }
}
