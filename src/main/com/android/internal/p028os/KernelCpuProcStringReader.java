package com.android.internal.p028os;

import android.p008os.StrictMode;
import android.util.Slog;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/* renamed from: com.android.internal.os.KernelCpuProcStringReader */
/* loaded from: classes4.dex */
public class KernelCpuProcStringReader {
    private static final int ERROR_THRESHOLD = 5;
    private static final long FRESHNESS = 500;
    private static final int MAX_BUFFER_SIZE = 1048576;
    private char[] mBuf;
    private final Clock mClock;
    private int mErrors;
    private final Path mFile;
    private long mLastReadTime;
    private final ReentrantReadWriteLock mLock;
    private final ReentrantReadWriteLock.ReadLock mReadLock;
    private int mSize;
    private final ReentrantReadWriteLock.WriteLock mWriteLock;
    private static final String TAG = KernelCpuProcStringReader.class.getSimpleName();
    private static final String PROC_UID_FREQ_TIME = "/proc/uid_time_in_state";
    private static final KernelCpuProcStringReader FREQ_TIME_READER = new KernelCpuProcStringReader(PROC_UID_FREQ_TIME);
    private static final String PROC_UID_ACTIVE_TIME = "/proc/uid_concurrent_active_time";
    private static final KernelCpuProcStringReader ACTIVE_TIME_READER = new KernelCpuProcStringReader(PROC_UID_ACTIVE_TIME);
    private static final String PROC_UID_CLUSTER_TIME = "/proc/uid_concurrent_policy_time";
    private static final KernelCpuProcStringReader CLUSTER_TIME_READER = new KernelCpuProcStringReader(PROC_UID_CLUSTER_TIME);
    private static final String PROC_UID_USER_SYS_TIME = "/proc/uid_cputime/show_uid_stat";
    private static final KernelCpuProcStringReader USER_SYS_TIME_READER = new KernelCpuProcStringReader(PROC_UID_USER_SYS_TIME);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static KernelCpuProcStringReader getFreqTimeReaderInstance() {
        return FREQ_TIME_READER;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static KernelCpuProcStringReader getActiveTimeReaderInstance() {
        return ACTIVE_TIME_READER;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static KernelCpuProcStringReader getClusterTimeReaderInstance() {
        return CLUSTER_TIME_READER;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static KernelCpuProcStringReader getUserSysTimeReaderInstance() {
        return USER_SYS_TIME_READER;
    }

    public KernelCpuProcStringReader(String file) {
        this(file, Clock.SYSTEM_CLOCK);
    }

    public KernelCpuProcStringReader(String file, Clock clock) {
        this.mErrors = 0;
        this.mLastReadTime = 0L;
        ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
        this.mLock = reentrantReadWriteLock;
        this.mReadLock = reentrantReadWriteLock.readLock();
        this.mWriteLock = reentrantReadWriteLock.writeLock();
        this.mFile = Paths.get(file, new String[0]);
        this.mClock = clock;
    }

    public ProcFileIterator open() {
        return open(false);
    }

    public ProcFileIterator open(boolean ignoreCache) {
        if (this.mErrors >= 5) {
            return null;
        }
        if (ignoreCache) {
            this.mWriteLock.lock();
        } else {
            this.mReadLock.lock();
            if (dataValid()) {
                return new ProcFileIterator(this.mSize);
            }
            this.mReadLock.unlock();
            this.mWriteLock.lock();
            if (dataValid()) {
                this.mReadLock.lock();
                this.mWriteLock.unlock();
                return new ProcFileIterator(this.mSize);
            }
        }
        int total = 0;
        this.mSize = 0;
        int oldMask = StrictMode.allowThreadDiskReadsMask();
        try {
            try {
                BufferedReader r = Files.newBufferedReader(this.mFile);
                try {
                    if (this.mBuf == null) {
                        this.mBuf = new char[1024];
                    }
                    while (true) {
                        char[] cArr = this.mBuf;
                        int curr = r.read(cArr, total, cArr.length - total);
                        if (curr < 0) {
                            this.mSize = total;
                            this.mLastReadTime = this.mClock.elapsedRealtime();
                            this.mReadLock.lock();
                            ProcFileIterator procFileIterator = new ProcFileIterator(total);
                            if (r != null) {
                                r.close();
                            }
                            return procFileIterator;
                        }
                        total += curr;
                        char[] cArr2 = this.mBuf;
                        if (total == cArr2.length) {
                            if (cArr2.length == 1048576) {
                                this.mErrors++;
                                Slog.m96e(TAG, "Proc file too large: " + this.mFile);
                                if (r != null) {
                                    r.close();
                                }
                                return null;
                            }
                            this.mBuf = Arrays.copyOf(cArr2, Math.min(cArr2.length << 1, 1048576));
                        }
                    }
                } catch (Throwable th) {
                    if (r != null) {
                        try {
                            r.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (FileNotFoundException | NoSuchFileException e) {
                this.mErrors++;
                Slog.m90w(TAG, "File not found. It's normal if not implemented: " + this.mFile);
                return null;
            } catch (IOException e2) {
                this.mErrors++;
                Slog.m95e(TAG, "Error reading " + this.mFile, e2);
                return null;
            }
        } finally {
            StrictMode.setThreadPolicyMask(oldMask);
            this.mWriteLock.unlock();
        }
    }

    private boolean dataValid() {
        return this.mSize > 0 && this.mClock.elapsedRealtime() - this.mLastReadTime < FRESHNESS;
    }

    /* renamed from: com.android.internal.os.KernelCpuProcStringReader$ProcFileIterator */
    /* loaded from: classes4.dex */
    public class ProcFileIterator implements AutoCloseable {
        private int mPos;
        private final int mSize;

        public ProcFileIterator(int size) {
            this.mSize = size;
        }

        public boolean hasNextLine() {
            return this.mPos < this.mSize;
        }

        public CharBuffer nextLine() {
            if (this.mPos >= this.mSize) {
                return null;
            }
            int i = this.mPos;
            while (i < this.mSize && KernelCpuProcStringReader.this.mBuf[i] != '\n') {
                i++;
            }
            int start = this.mPos;
            this.mPos = i + 1;
            return CharBuffer.wrap(KernelCpuProcStringReader.this.mBuf, start, i - start);
        }

        public int size() {
            return this.mSize;
        }

        @Override // java.lang.AutoCloseable
        public void close() {
            KernelCpuProcStringReader.this.mReadLock.unlock();
        }
    }

    public static int asLongs(CharBuffer buf, long[] array) {
        if (buf == null) {
            return -1;
        }
        int initialPos = buf.position();
        int count = 0;
        long num = -1;
        while (buf.remaining() > 0 && count < array.length) {
            char c = buf.get();
            if (!isNumber(c) && c != ' ' && c != ':') {
                buf.position(initialPos);
                return -2;
            } else if (num < 0) {
                if (isNumber(c)) {
                    num = c - '0';
                }
            } else if (isNumber(c)) {
                num = ((10 * num) + c) - 48;
                if (num < 0) {
                    buf.position(initialPos);
                    return -3;
                }
            } else {
                array[count] = num;
                num = -1;
                count++;
            }
        }
        if (num >= 0) {
            array[count] = num;
            count++;
        }
        buf.position(initialPos);
        return count;
    }

    private static boolean isNumber(char c) {
        return c >= '0' && c <= '9';
    }
}
