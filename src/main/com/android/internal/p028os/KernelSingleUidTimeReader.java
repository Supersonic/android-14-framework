package com.android.internal.p028os;

import android.util.SparseArray;
import com.android.internal.p028os.LongArrayMultiStateCounter;
import dalvik.annotation.optimization.CriticalNative;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
/* renamed from: com.android.internal.os.KernelSingleUidTimeReader */
/* loaded from: classes4.dex */
public class KernelSingleUidTimeReader {
    private static final boolean DBG = false;
    private static final String PROC_FILE_DIR = "/proc/uid/";
    private static final String PROC_FILE_NAME = "/time_in_state";
    private static final String TAG = KernelSingleUidTimeReader.class.getName();
    public static final int TOTAL_READ_ERROR_COUNT = 5;
    private static final String UID_TIMES_PROC_FILE = "/proc/uid_time_in_state";
    private boolean mBpfTimesAvailable;
    private final int mCpuFreqsCount;
    private boolean mCpuFreqsCountVerified;
    private final Injector mInjector;
    private SparseArray<long[]> mLastUidCpuTimeMs;
    private int mReadErrorCounter;
    private boolean mSingleUidCpuTimesAvailable;

    private static final native boolean canReadBpfTimes();

    public KernelSingleUidTimeReader(int cpuFreqsCount) {
        this(cpuFreqsCount, new Injector());
    }

    public KernelSingleUidTimeReader(int cpuFreqsCount, Injector injector) {
        this.mLastUidCpuTimeMs = new SparseArray<>();
        this.mSingleUidCpuTimesAvailable = true;
        this.mBpfTimesAvailable = true;
        this.mInjector = injector;
        this.mCpuFreqsCount = cpuFreqsCount;
        if (cpuFreqsCount == 0) {
            this.mSingleUidCpuTimesAvailable = false;
        }
    }

    public boolean singleUidCpuTimesAvailable() {
        return this.mSingleUidCpuTimesAvailable;
    }

    public long[] readDeltaMs(int uid) {
        synchronized (this) {
            if (this.mSingleUidCpuTimesAvailable) {
                if (this.mBpfTimesAvailable) {
                    long[] cpuTimesMs = this.mInjector.readBpfData(uid);
                    if (cpuTimesMs.length == 0) {
                        this.mBpfTimesAvailable = false;
                    } else if (!this.mCpuFreqsCountVerified && cpuTimesMs.length != this.mCpuFreqsCount) {
                        this.mSingleUidCpuTimesAvailable = false;
                        return null;
                    } else {
                        this.mCpuFreqsCountVerified = true;
                        return computeDelta(uid, cpuTimesMs);
                    }
                }
                String procFile = PROC_FILE_DIR + uid + PROC_FILE_NAME;
                try {
                    byte[] data = this.mInjector.readData(procFile);
                    if (!this.mCpuFreqsCountVerified) {
                        verifyCpuFreqsCount(data.length, procFile);
                    }
                    ByteBuffer buffer = ByteBuffer.wrap(data);
                    buffer.order(ByteOrder.nativeOrder());
                    return computeDelta(uid, readCpuTimesFromByteBuffer(buffer));
                } catch (Exception e) {
                    int i = this.mReadErrorCounter + 1;
                    this.mReadErrorCounter = i;
                    if (i >= 5) {
                        this.mSingleUidCpuTimesAvailable = false;
                    }
                    return null;
                }
            }
            return null;
        }
    }

    private void verifyCpuFreqsCount(int numBytes, String procFile) {
        int actualCount = numBytes / 8;
        if (this.mCpuFreqsCount != actualCount) {
            this.mSingleUidCpuTimesAvailable = false;
            throw new IllegalStateException("Freq count didn't match,count from /proc/uid_time_in_state=" + this.mCpuFreqsCount + ", butcount from " + procFile + "=" + actualCount);
        } else {
            this.mCpuFreqsCountVerified = true;
        }
    }

    private long[] readCpuTimesFromByteBuffer(ByteBuffer buffer) {
        long[] cpuTimesMs = new long[this.mCpuFreqsCount];
        for (int i = 0; i < this.mCpuFreqsCount; i++) {
            cpuTimesMs[i] = buffer.getLong() * 10;
        }
        return cpuTimesMs;
    }

    public long[] computeDelta(int uid, long[] latestCpuTimesMs) {
        synchronized (this) {
            if (this.mSingleUidCpuTimesAvailable) {
                long[] lastCpuTimesMs = this.mLastUidCpuTimeMs.get(uid);
                long[] deltaTimesMs = getDeltaLocked(lastCpuTimesMs, latestCpuTimesMs);
                if (deltaTimesMs == null) {
                    return null;
                }
                boolean hasNonZero = false;
                int i = deltaTimesMs.length - 1;
                while (true) {
                    if (i < 0) {
                        break;
                    } else if (deltaTimesMs[i] <= 0) {
                        i--;
                    } else {
                        hasNonZero = true;
                        break;
                    }
                }
                if (hasNonZero) {
                    this.mLastUidCpuTimeMs.put(uid, latestCpuTimesMs);
                    return deltaTimesMs;
                }
                return null;
            }
            return null;
        }
    }

    public long[] getDeltaLocked(long[] lastCpuTimesMs, long[] latestCpuTimesMs) {
        int i = latestCpuTimesMs.length;
        while (true) {
            i--;
            if (i >= 0) {
                if (latestCpuTimesMs[i] < 0) {
                    return null;
                }
            } else if (lastCpuTimesMs == null) {
                return latestCpuTimesMs;
            } else {
                long[] deltaTimesMs = new long[latestCpuTimesMs.length];
                for (int i2 = latestCpuTimesMs.length - 1; i2 >= 0; i2--) {
                    deltaTimesMs[i2] = latestCpuTimesMs[i2] - lastCpuTimesMs[i2];
                    if (deltaTimesMs[i2] < 0) {
                        return null;
                    }
                }
                return deltaTimesMs;
            }
        }
    }

    public void setAllUidsCpuTimesMs(SparseArray<long[]> allUidsCpuTimesMs) {
        synchronized (this) {
            this.mLastUidCpuTimeMs.clear();
            for (int i = allUidsCpuTimesMs.size() - 1; i >= 0; i--) {
                long[] cpuTimesMs = allUidsCpuTimesMs.valueAt(i);
                if (cpuTimesMs != null) {
                    this.mLastUidCpuTimeMs.put(allUidsCpuTimesMs.keyAt(i), (long[]) cpuTimesMs.clone());
                }
            }
        }
    }

    public void removeUid(int uid) {
        synchronized (this) {
            this.mLastUidCpuTimeMs.delete(uid);
        }
    }

    public void removeUidsInRange(int startUid, int endUid) {
        if (endUid < startUid) {
            return;
        }
        synchronized (this) {
            this.mLastUidCpuTimeMs.put(startUid, null);
            this.mLastUidCpuTimeMs.put(endUid, null);
            int startIdx = this.mLastUidCpuTimeMs.indexOfKey(startUid);
            int endIdx = this.mLastUidCpuTimeMs.indexOfKey(endUid);
            this.mLastUidCpuTimeMs.removeAtRange(startIdx, (endIdx - startIdx) + 1);
        }
    }

    public void addDelta(int uid, LongArrayMultiStateCounter counter, long timestampMs) {
        this.mInjector.addDelta(uid, counter, timestampMs, null);
    }

    public void addDelta(int uid, LongArrayMultiStateCounter counter, long timestampMs, LongArrayMultiStateCounter.LongArrayContainer deltaContainer) {
        this.mInjector.addDelta(uid, counter, timestampMs, deltaContainer);
    }

    /* renamed from: com.android.internal.os.KernelSingleUidTimeReader$Injector */
    /* loaded from: classes4.dex */
    public static class Injector {
        private static native boolean addDeltaForTest(int i, long j, long j2, long[][] jArr, long j3);

        @CriticalNative
        private static native boolean addDeltaFromBpf(int i, long j, long j2, long j3);

        public native long[] readBpfData(int i);

        public byte[] readData(String procFile) throws IOException {
            return Files.readAllBytes(Paths.get(procFile, new String[0]));
        }

        public boolean addDelta(int uid, LongArrayMultiStateCounter counter, long timestampMs, LongArrayMultiStateCounter.LongArrayContainer deltaOut) {
            return addDeltaFromBpf(uid, counter.mNativeObject, timestampMs, deltaOut != null ? deltaOut.mNativeObject : 0L);
        }

        public boolean addDeltaForTest(int uid, LongArrayMultiStateCounter counter, long timestampMs, long[][] timeInFreqDataNanos, LongArrayMultiStateCounter.LongArrayContainer deltaOut) {
            return addDeltaForTest(uid, counter.mNativeObject, timestampMs, timeInFreqDataNanos, deltaOut != null ? deltaOut.mNativeObject : 0L);
        }
    }

    public SparseArray<long[]> getLastUidCpuTimeMs() {
        return this.mLastUidCpuTimeMs;
    }

    public void setSingleUidCpuTimesAvailable(boolean singleUidCpuTimesAvailable) {
        this.mSingleUidCpuTimesAvailable = singleUidCpuTimesAvailable;
    }
}
