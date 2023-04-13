package com.android.internal.p028os;

import android.p008os.Binder;
import android.p008os.Handler;
import android.p008os.SystemClock;
import android.util.ArrayMap;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.internal.p028os.BinderInternal;
import com.android.internal.util.FrameworkStatsLog;
import java.util.Random;
/* renamed from: com.android.internal.os.BinderLatencyObserver */
/* loaded from: classes4.dex */
public class BinderLatencyObserver {
    public static final int BUCKET_COUNT_DEFAULT = 100;
    public static final float BUCKET_SCALE_FACTOR_DEFAULT = 1.125f;
    public static final int FIRST_BUCKET_SIZE_DEFAULT = 5;
    private static final int LAST_HISTOGRAM_BUFFER_SIZE_BYTES = 1000;
    private static final int MAX_ATOM_SIZE_BYTES = 4064;
    public static final int PERIODIC_SAMPLING_INTERVAL_DEFAULT = 10;
    public static final int SHARDING_MODULO_DEFAULT = 1;
    public static final int STATSD_PUSH_INTERVAL_MINUTES_DEFAULT = 360;
    private static final String TAG = "BinderLatencyObserver";
    private BinderLatencyBuckets mLatencyBuckets;
    private final Handler mLatencyObserverHandler;
    private final int mProcessSource;
    private final Random mRandom;
    private int mShardingOffset;
    private final ArrayMap<LatencyDims, int[]> mLatencyHistograms = new ArrayMap<>();
    private final Object mLock = new Object();
    private int mPeriodicSamplingInterval = 10;
    private int mShardingModulo = 1;
    private int mBucketCount = 100;
    private int mFirstBucketSize = 5;
    private float mBucketScaleFactor = 1.125f;
    private int mStatsdPushIntervalMinutes = 360;
    private Runnable mLatencyObserverRunnable = new Runnable() { // from class: com.android.internal.os.BinderLatencyObserver.1
        @Override // java.lang.Runnable
        public void run() {
            ArrayMap<LatencyDims, int[]> histogramMap;
            BinderLatencyObserver.this.noteLatencyDelayed();
            synchronized (BinderLatencyObserver.this.mLock) {
                histogramMap = new ArrayMap<>(BinderLatencyObserver.this.mLatencyHistograms);
                BinderLatencyObserver.this.mLatencyHistograms.clear();
            }
            BinderTransactionNameResolver resolver = new BinderTransactionNameResolver();
            ProtoOutputStream proto = new ProtoOutputStream();
            int histogramsWritten = 0;
            for (LatencyDims dims : histogramMap.keySet()) {
                if (proto.getRawSize() + 1000 > BinderLatencyObserver.this.getMaxAtomSizeBytes()) {
                    if (histogramsWritten > 0) {
                        BinderLatencyObserver.this.writeAtomToStatsd(proto);
                    }
                    proto = new ProtoOutputStream();
                    histogramsWritten = 0;
                }
                String transactionName = resolver.getMethodName(dims.getBinderClass(), dims.getTransactionCode());
                BinderLatencyObserver.this.fillApiStatsProto(proto, dims, transactionName, histogramMap.get(dims));
                histogramsWritten++;
            }
            if (histogramsWritten > 0) {
                BinderLatencyObserver.this.writeAtomToStatsd(proto);
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public void fillApiStatsProto(ProtoOutputStream proto, LatencyDims dims, String transactionName, int[] histogram) {
        int i;
        int firstNonEmptyBucket = 0;
        int i2 = 0;
        while (true) {
            i = this.mBucketCount;
            if (i2 < i) {
                if (histogram[i2] == 0) {
                    i2++;
                } else {
                    firstNonEmptyBucket = i2;
                    break;
                }
            } else {
                break;
            }
        }
        int lastNonEmptyBucket = i - 1;
        int i3 = i - 1;
        while (true) {
            if (i3 >= 0) {
                if (histogram[i3] != 0) {
                    lastNonEmptyBucket = i3;
                    break;
                } else {
                    i3--;
                }
            } else {
                break;
            }
        }
        long apiStatsToken = proto.start(2246267895809L);
        long dimsToken = proto.start(1146756268033L);
        proto.write(1159641169921L, this.mProcessSource);
        proto.write(1138166333443L, dims.getBinderClass().getName());
        proto.write(1138166333445L, transactionName);
        proto.end(dimsToken);
        proto.write(1120986464258L, firstNonEmptyBucket);
        for (int i4 = firstNonEmptyBucket; i4 <= lastNonEmptyBucket; i4++) {
            proto.write(2220498092035L, histogram[i4]);
        }
        proto.end(apiStatsToken);
    }

    protected int getMaxAtomSizeBytes() {
        return MAX_ATOM_SIZE_BYTES;
    }

    protected void writeAtomToStatsd(ProtoOutputStream atom) {
        FrameworkStatsLog.write(342, atom.getBytes(), this.mPeriodicSamplingInterval, this.mShardingModulo, this.mBucketCount, this.mFirstBucketSize, this.mBucketScaleFactor);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void noteLatencyDelayed() {
        this.mLatencyObserverHandler.removeCallbacks(this.mLatencyObserverRunnable);
        this.mLatencyObserverHandler.postDelayed(this.mLatencyObserverRunnable, this.mStatsdPushIntervalMinutes * 60 * 1000);
    }

    /* renamed from: com.android.internal.os.BinderLatencyObserver$Injector */
    /* loaded from: classes4.dex */
    public static class Injector {
        public Random getRandomGenerator() {
            return new Random();
        }

        public Handler getHandler() {
            return BackgroundThread.getHandler();
        }
    }

    public BinderLatencyObserver(Injector injector, int processSource) {
        Random randomGenerator = injector.getRandomGenerator();
        this.mRandom = randomGenerator;
        this.mLatencyObserverHandler = injector.getHandler();
        this.mLatencyBuckets = new BinderLatencyBuckets(this.mBucketCount, this.mFirstBucketSize, this.mBucketScaleFactor);
        this.mProcessSource = processSource;
        this.mShardingOffset = randomGenerator.nextInt(this.mShardingModulo);
        noteLatencyDelayed();
    }

    public void callEnded(BinderInternal.CallSession s) {
        if (s == null || s.exceptionThrown || !shouldKeepSample()) {
            return;
        }
        LatencyDims dims = LatencyDims.create(s.binderClass, s.transactionCode);
        if (!shouldCollect(dims)) {
            return;
        }
        long elapsedTimeMicro = getElapsedRealtimeMicro();
        long callDuration = elapsedTimeMicro - s.timeStarted;
        int bucketIdx = this.mLatencyBuckets.sampleToBucket(callDuration > 2147483647L ? Integer.MAX_VALUE : (int) callDuration);
        synchronized (this.mLock) {
            int[] buckets = this.mLatencyHistograms.get(dims);
            if (buckets == null) {
                buckets = new int[this.mBucketCount];
                this.mLatencyHistograms.put(dims, buckets);
            }
            if (buckets[bucketIdx] < Integer.MAX_VALUE) {
                buckets[bucketIdx] = buckets[bucketIdx] + 1;
            }
        }
    }

    protected long getElapsedRealtimeMicro() {
        return SystemClock.elapsedRealtimeNanos() / 1000;
    }

    protected boolean shouldCollect(LatencyDims dims) {
        return (dims.hashCode() + this.mShardingOffset) % this.mShardingModulo == 0;
    }

    protected boolean shouldKeepSample() {
        return this.mRandom.nextInt(this.mPeriodicSamplingInterval) == 0;
    }

    public void setSamplingInterval(int samplingInterval) {
        if (samplingInterval <= 0) {
            Slog.m90w(TAG, "Ignored invalid sampling interval (value must be positive): " + samplingInterval);
            return;
        }
        synchronized (this.mLock) {
            if (samplingInterval != this.mPeriodicSamplingInterval) {
                this.mPeriodicSamplingInterval = samplingInterval;
                reset();
            }
        }
    }

    public void setShardingModulo(int shardingModulo) {
        if (shardingModulo <= 0) {
            Slog.m90w(TAG, "Ignored invalid sharding modulo (value must be positive): " + shardingModulo);
            return;
        }
        synchronized (this.mLock) {
            if (shardingModulo != this.mShardingModulo) {
                this.mShardingModulo = shardingModulo;
                this.mShardingOffset = this.mRandom.nextInt(shardingModulo);
                reset();
            }
        }
    }

    public void setPushInterval(int pushIntervalMinutes) {
        if (pushIntervalMinutes <= 0) {
            Slog.m90w(TAG, "Ignored invalid push interval (value must be positive): " + pushIntervalMinutes);
            return;
        }
        synchronized (this.mLock) {
            if (pushIntervalMinutes != this.mStatsdPushIntervalMinutes) {
                this.mStatsdPushIntervalMinutes = pushIntervalMinutes;
                reset();
            }
        }
    }

    public void setHistogramBucketsParams(int bucketCount, int firstBucketSize, float bucketScaleFactor) {
        synchronized (this.mLock) {
            if (bucketCount != this.mBucketCount || firstBucketSize != this.mFirstBucketSize || bucketScaleFactor != this.mBucketScaleFactor) {
                this.mBucketCount = bucketCount;
                this.mFirstBucketSize = firstBucketSize;
                this.mBucketScaleFactor = bucketScaleFactor;
                this.mLatencyBuckets = new BinderLatencyBuckets(bucketCount, firstBucketSize, bucketScaleFactor);
                reset();
            }
        }
    }

    public void reset() {
        synchronized (this.mLock) {
            this.mLatencyHistograms.clear();
        }
        noteLatencyDelayed();
    }

    /* renamed from: com.android.internal.os.BinderLatencyObserver$LatencyDims */
    /* loaded from: classes4.dex */
    public static class LatencyDims {
        private Class<? extends Binder> mBinderClass;
        private int mHashCode = 0;
        private int mTransactionCode;

        public static LatencyDims create(Class<? extends Binder> binderClass, int transactionCode) {
            return new LatencyDims(binderClass, transactionCode);
        }

        private LatencyDims(Class<? extends Binder> binderClass, int transactionCode) {
            this.mBinderClass = binderClass;
            this.mTransactionCode = transactionCode;
        }

        public Class<? extends Binder> getBinderClass() {
            return this.mBinderClass;
        }

        public int getTransactionCode() {
            return this.mTransactionCode;
        }

        public boolean equals(Object other) {
            if (other == null || !(other instanceof LatencyDims)) {
                return false;
            }
            LatencyDims o = (LatencyDims) other;
            return this.mTransactionCode == o.getTransactionCode() && this.mBinderClass == o.getBinderClass();
        }

        public int hashCode() {
            int i = this.mHashCode;
            if (i != 0) {
                return i;
            }
            int hash = (this.mTransactionCode * 31) + this.mBinderClass.getName().hashCode();
            this.mHashCode = hash;
            return hash;
        }
    }

    public ArrayMap<LatencyDims, int[]> getLatencyHistograms() {
        return this.mLatencyHistograms;
    }

    public Runnable getStatsdPushRunnable() {
        return this.mLatencyObserverRunnable;
    }

    public int getProcessSource() {
        return this.mProcessSource;
    }
}
