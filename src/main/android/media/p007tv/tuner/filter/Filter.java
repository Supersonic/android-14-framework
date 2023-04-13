package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
import android.media.p007tv.tuner.TunerUtils;
import android.media.p007tv.tuner.TunerVersionChecker;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.Executor;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.Filter */
/* loaded from: classes2.dex */
public class Filter implements AutoCloseable {
    public static final int MONITOR_EVENT_IP_CID_CHANGE = 2;
    public static final int MONITOR_EVENT_SCRAMBLING_STATUS = 1;
    public static final int SCRAMBLING_STATUS_NOT_SCRAMBLED = 2;
    public static final int SCRAMBLING_STATUS_SCRAMBLED = 4;
    public static final int SCRAMBLING_STATUS_UNKNOWN = 1;
    public static final int STATUS_DATA_READY = 1;
    public static final int STATUS_HIGH_WATER = 4;
    public static final int STATUS_LOW_WATER = 2;
    public static final int STATUS_NO_DATA = 16;
    public static final int STATUS_OVERFLOW = 8;
    public static final int SUBTYPE_AUDIO = 3;
    public static final int SUBTYPE_DOWNLOAD = 5;
    public static final int SUBTYPE_IP = 13;
    public static final int SUBTYPE_IP_PAYLOAD = 12;
    public static final int SUBTYPE_MMTP = 10;
    public static final int SUBTYPE_NTP = 11;
    public static final int SUBTYPE_PAYLOAD_THROUGH = 14;
    public static final int SUBTYPE_PCR = 8;
    public static final int SUBTYPE_PES = 2;
    public static final int SUBTYPE_PTP = 16;
    public static final int SUBTYPE_RECORD = 6;
    public static final int SUBTYPE_SECTION = 1;
    public static final int SUBTYPE_TEMI = 9;
    public static final int SUBTYPE_TLV = 15;
    public static final int SUBTYPE_TS = 7;
    public static final int SUBTYPE_UNDEFINED = 0;
    public static final int SUBTYPE_VIDEO = 4;
    private static final String TAG = "Filter";
    public static final int TYPE_ALP = 16;
    public static final int TYPE_IP = 4;
    public static final int TYPE_MMTP = 2;
    public static final int TYPE_TLV = 8;
    public static final int TYPE_TS = 1;
    public static final int TYPE_UNDEFINED = 0;
    private FilterCallback mCallback;
    private Executor mExecutor;
    private final long mId;
    private int mMainType;
    private long mNativeContext;
    private Filter mSource;
    private boolean mStarted;
    private int mSubtype;
    private final Object mCallbackLock = new Object();
    private boolean mIsClosed = false;
    private boolean mIsStarted = false;
    private boolean mIsShared = false;
    private final Object mLock = new Object();

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.filter.Filter$MonitorEventMask */
    /* loaded from: classes2.dex */
    public @interface MonitorEventMask {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.filter.Filter$ScramblingStatus */
    /* loaded from: classes2.dex */
    public @interface ScramblingStatus {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.filter.Filter$Status */
    /* loaded from: classes2.dex */
    public @interface Status {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.filter.Filter$Subtype */
    /* loaded from: classes2.dex */
    public @interface Subtype {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.filter.Filter$Type */
    /* loaded from: classes2.dex */
    public @interface Type {
    }

    private native String nativeAcquireSharedFilterToken();

    private native int nativeClose();

    private native int nativeConfigureFilter(int i, int i2, FilterConfiguration filterConfiguration);

    private native int nativeConfigureMonitorEvent(int i);

    private native int nativeFlushFilter();

    private native void nativeFreeSharedFilterToken(String str);

    private native int nativeGetId();

    private native long nativeGetId64Bit();

    private native int nativeRead(byte[] bArr, long j, long j2);

    private native int nativeSetDataSizeDelayHint(int i);

    private native int nativeSetDataSource(Filter filter);

    private native int nativeSetTimeDelayHint(int i);

    private native int nativeStartFilter();

    private native int nativeStopFilter();

    private Filter(long id) {
        this.mId = id;
    }

    private void onFilterStatus(final int status) {
        Executor executor;
        synchronized (this.mCallbackLock) {
            if (this.mCallback != null && (executor = this.mExecutor) != null) {
                executor.execute(new Runnable() { // from class: android.media.tv.tuner.filter.Filter$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        Filter.this.lambda$onFilterStatus$0(status);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onFilterStatus$0(int status) {
        synchronized (this.mCallbackLock) {
            FilterCallback filterCallback = this.mCallback;
            if (filterCallback != null) {
                try {
                    filterCallback.onFilterStatusChanged(this, status);
                } catch (NullPointerException e) {
                    Log.m112d(TAG, "catch exception:" + e);
                }
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
                executor.execute(new Runnable() { // from class: android.media.tv.tuner.filter.Filter$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        Filter.this.lambda$onFilterEvent$1(events);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onFilterEvent$1(FilterEvent[] events) {
        synchronized (this.mCallbackLock) {
            FilterCallback filterCallback = this.mCallback;
            if (filterCallback != null) {
                try {
                    filterCallback.onFilterEvent(this, events);
                } catch (NullPointerException e) {
                    Log.m112d(TAG, "catch exception:" + e);
                }
            } else {
                for (FilterEvent event : events) {
                    if (event instanceof MediaEvent) {
                        ((MediaEvent) event).release();
                    }
                }
            }
        }
    }

    public void setType(int mainType, int subtype) {
        this.mMainType = mainType;
        this.mSubtype = TunerUtils.getFilterSubtype(mainType, subtype);
    }

    public void setCallback(FilterCallback cb, Executor executor) {
        synchronized (this.mCallbackLock) {
            this.mCallback = cb;
            this.mExecutor = executor;
        }
    }

    public FilterCallback getCallback() {
        FilterCallback filterCallback;
        synchronized (this.mCallbackLock) {
            filterCallback = this.mCallback;
        }
        return filterCallback;
    }

    public int configure(FilterConfiguration config) {
        synchronized (this.mLock) {
            TunerUtils.checkResourceState(TAG, this.mIsClosed);
            if (this.mIsShared) {
                return 3;
            }
            Settings s = config.getSettings();
            int subType = s == null ? this.mSubtype : s.getType();
            if (this.mMainType != config.getType() || this.mSubtype != subType) {
                throw new IllegalArgumentException("Invalid filter config. filter main type=" + this.mMainType + ", filter subtype=" + this.mSubtype + ". config main type=" + config.getType() + ", config subtype=" + subType);
            }
            if ((s instanceof RecordSettings) && ((RecordSettings) s).getScIndexType() == 4 && !TunerVersionChecker.isHigherOrEqualVersionTo(196608)) {
                Log.m110e(TAG, "Tuner version " + TunerVersionChecker.getTunerVersion() + " does not support VVC");
                return 1;
            }
            return nativeConfigureFilter(config.getType(), subType, config);
        }
    }

    public int getId() {
        int nativeGetId;
        synchronized (this.mLock) {
            TunerUtils.checkResourceState(TAG, this.mIsClosed);
            nativeGetId = nativeGetId();
        }
        return nativeGetId;
    }

    public long getIdLong() {
        long nativeGetId64Bit;
        synchronized (this.mLock) {
            TunerUtils.checkResourceState(TAG, this.mIsClosed);
            nativeGetId64Bit = nativeGetId64Bit();
        }
        return nativeGetId64Bit;
    }

    public int setMonitorEventMask(int monitorEventMask) {
        synchronized (this.mLock) {
            TunerUtils.checkResourceState(TAG, this.mIsClosed);
            if (this.mIsShared) {
                return 3;
            }
            if (TunerVersionChecker.checkHigherOrEqualVersionTo(65537, "setMonitorEventMask")) {
                return nativeConfigureMonitorEvent(monitorEventMask);
            }
            return 1;
        }
    }

    public int setDataSource(Filter source) {
        synchronized (this.mLock) {
            TunerUtils.checkResourceState(TAG, this.mIsClosed);
            if (this.mIsShared) {
                return 3;
            }
            if (this.mSource != null) {
                throw new IllegalStateException("Data source is existing");
            }
            int res = nativeSetDataSource(source);
            if (res == 0) {
                this.mSource = source;
            }
            return res;
        }
    }

    public int start() {
        synchronized (this.mLock) {
            TunerUtils.checkResourceState(TAG, this.mIsClosed);
            if (this.mIsShared) {
                return 3;
            }
            int res = nativeStartFilter();
            if (res == 0) {
                this.mIsStarted = true;
            }
            return res;
        }
    }

    public int stop() {
        synchronized (this.mLock) {
            TunerUtils.checkResourceState(TAG, this.mIsClosed);
            if (this.mIsShared) {
                return 3;
            }
            int res = nativeStopFilter();
            if (res == 0) {
                this.mIsStarted = false;
            }
            return res;
        }
    }

    public int flush() {
        synchronized (this.mLock) {
            TunerUtils.checkResourceState(TAG, this.mIsClosed);
            if (this.mIsShared) {
                return 3;
            }
            return nativeFlushFilter();
        }
    }

    public int read(byte[] buffer, long offset, long size) {
        synchronized (this.mLock) {
            try {
                TunerUtils.checkResourceState(TAG, this.mIsClosed);
                if (this.mIsShared) {
                    return 0;
                }
                try {
                    return nativeRead(buffer, offset, Math.min(size, buffer.length - offset));
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
        synchronized (this.mCallbackLock) {
            this.mCallback = null;
            this.mExecutor = null;
        }
        synchronized (this.mLock) {
            if (this.mIsClosed) {
                return;
            }
            int res = nativeClose();
            if (res != 0) {
                TunerUtils.throwExceptionForResult(res, "Failed to close filter.");
            } else {
                this.mIsStarted = false;
                this.mIsClosed = true;
            }
        }
    }

    public String acquireSharedFilterToken() {
        synchronized (this.mLock) {
            TunerUtils.checkResourceState(TAG, this.mIsClosed);
            if (!this.mIsStarted && !this.mIsShared) {
                String token = nativeAcquireSharedFilterToken();
                if (token != null) {
                    this.mIsShared = true;
                }
                return token;
            }
            Log.m112d(TAG, "Acquire shared filter in a wrong state, started: " + this.mIsStarted + "shared: " + this.mIsShared);
            return null;
        }
    }

    public void freeSharedFilterToken(String filterToken) {
        synchronized (this.mLock) {
            TunerUtils.checkResourceState(TAG, this.mIsClosed);
            if (this.mIsShared) {
                nativeFreeSharedFilterToken(filterToken);
                this.mIsShared = false;
            }
        }
    }

    public int delayCallbackForDurationMillis(long durationInMs) {
        int nativeSetTimeDelayHint;
        if (!TunerVersionChecker.checkHigherOrEqualVersionTo(131072, "setTimeDelayHint")) {
            return 1;
        }
        if (durationInMs >= 0 && durationInMs <= 2147483647L) {
            synchronized (this.mLock) {
                nativeSetTimeDelayHint = nativeSetTimeDelayHint((int) durationInMs);
            }
            return nativeSetTimeDelayHint;
        }
        return 4;
    }

    public int delayCallbackUntilBytesAccumulated(int bytesAccumulated) {
        int nativeSetDataSizeDelayHint;
        if (!TunerVersionChecker.checkHigherOrEqualVersionTo(131072, "setTimeDelayHint")) {
            return 1;
        }
        synchronized (this.mLock) {
            nativeSetDataSizeDelayHint = nativeSetDataSizeDelayHint(bytesAccumulated);
        }
        return nativeSetDataSizeDelayHint;
    }
}
