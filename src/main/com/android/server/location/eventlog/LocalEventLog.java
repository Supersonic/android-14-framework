package com.android.server.location.eventlog;

import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.Preconditions;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
/* loaded from: classes.dex */
public class LocalEventLog<T> {
    @GuardedBy({"this"})
    public final int[] mEntries;
    @GuardedBy({"this"})
    public long mLastLogTime;
    @GuardedBy({"this"})
    public int mLogEndIndex;
    @GuardedBy({"this"})
    public final T[] mLogEvents;
    @GuardedBy({"this"})
    public int mLogSize;
    @GuardedBy({"this"})
    public long mModificationCount;
    @GuardedBy({"this"})
    public long mStartTime;
    public static final int IS_FILLER_OFFSET = countTrailingZeros(Integer.MIN_VALUE);
    public static final int TIME_DELTA_OFFSET = countTrailingZeros(Integer.MAX_VALUE);
    @VisibleForTesting
    static final int MAX_TIME_DELTA = (1 << Integer.bitCount(Integer.MAX_VALUE)) - 1;

    /* loaded from: classes.dex */
    public interface LogConsumer<T> {
        void acceptLog(long j, T t);
    }

    public static int countTrailingZeros(int i) {
        int i2 = 0;
        while (i != 0 && (i & 1) == 0) {
            i2++;
            i >>>= 1;
        }
        return i2;
    }

    public static boolean isFiller(int i) {
        return (i & Integer.MIN_VALUE) != 0;
    }

    public static int createEntry(boolean z, int i) {
        Preconditions.checkArgument(i >= 0 && i <= MAX_TIME_DELTA);
        return (((z ? 1 : 0) << IS_FILLER_OFFSET) & Integer.MIN_VALUE) | ((i << TIME_DELTA_OFFSET) & Integer.MAX_VALUE);
    }

    public static int getTimeDelta(int i) {
        return (i & Integer.MAX_VALUE) >>> TIME_DELTA_OFFSET;
    }

    public LocalEventLog(int i, Class<T> cls) {
        Preconditions.checkArgument(i > 0);
        this.mEntries = new int[i];
        this.mLogEvents = (T[]) ((Object[]) Array.newInstance((Class<?>) cls, i));
        this.mLogSize = 0;
        this.mLogEndIndex = 0;
        this.mStartTime = -1L;
        this.mLastLogTime = -1L;
    }

    public synchronized void addLog(long j, T t) {
        Preconditions.checkArgument(t != null);
        long j2 = 0;
        if (!isEmpty()) {
            long j3 = j - this.mLastLogTime;
            if (j3 >= 0 && j3 / MAX_TIME_DELTA < this.mEntries.length - 1) {
                j2 = j3;
                while (true) {
                    int i = MAX_TIME_DELTA;
                    if (j2 < i) {
                        break;
                    }
                    addLogEventInternal(true, i, null);
                    j2 -= i;
                }
            }
            clear();
        }
        if (isEmpty()) {
            this.mStartTime = j;
            this.mLastLogTime = j;
            this.mModificationCount++;
        }
        addLogEventInternal(false, (int) j2, t);
    }

    @GuardedBy({"this"})
    public final void addLogEventInternal(boolean z, int i, T t) {
        int[] iArr;
        boolean z2 = false;
        Preconditions.checkArgument(z || t != null);
        if (this.mStartTime != -1 && this.mLastLogTime != -1) {
            z2 = true;
        }
        Preconditions.checkState(z2);
        int i2 = this.mLogSize;
        if (i2 == this.mEntries.length) {
            this.mStartTime += getTimeDelta(iArr[startIndex()]);
            this.mModificationCount++;
        } else {
            this.mLogSize = i2 + 1;
        }
        this.mEntries[this.mLogEndIndex] = createEntry(z, i);
        T[] tArr = this.mLogEvents;
        int i3 = this.mLogEndIndex;
        tArr[i3] = t;
        this.mLogEndIndex = incrementIndex(i3);
        this.mLastLogTime += i;
    }

    public synchronized void clear() {
        Arrays.fill(this.mLogEvents, (Object) null);
        this.mLogEndIndex = 0;
        this.mLogSize = 0;
        this.mModificationCount++;
        this.mStartTime = -1L;
        this.mLastLogTime = -1L;
    }

    @GuardedBy({"this"})
    public final boolean isEmpty() {
        return this.mLogSize == 0;
    }

    @SafeVarargs
    public static <T> void iterate(LogConsumer<? super T> logConsumer, LocalEventLog<T>... localEventLogArr) {
        ArrayList arrayList = new ArrayList(localEventLogArr.length);
        for (LocalEventLog<T> localEventLog : localEventLogArr) {
            Objects.requireNonNull(localEventLog);
            LogIterator logIterator = new LogIterator();
            if (logIterator.hasNext()) {
                arrayList.add(logIterator);
                logIterator.next();
            }
        }
        while (true) {
            Iterator it = arrayList.iterator();
            LogIterator logIterator2 = null;
            while (it.hasNext()) {
                LogIterator logIterator3 = (LogIterator) it.next();
                if (logIterator3 != null && (logIterator2 == null || logIterator3.getTime() < logIterator2.getTime())) {
                    logIterator2 = logIterator3;
                }
            }
            if (logIterator2 == null) {
                return;
            }
            logConsumer.acceptLog(logIterator2.getTime(), (Object) logIterator2.getLog());
            if (logIterator2.hasNext()) {
                logIterator2.next();
            } else {
                arrayList.remove(logIterator2);
            }
        }
    }

    @GuardedBy({"this"})
    public int startIndex() {
        return wrapIndex(this.mLogEndIndex - this.mLogSize);
    }

    @GuardedBy({"this"})
    public int incrementIndex(int i) {
        if (i == -1) {
            return startIndex();
        }
        if (i >= 0) {
            return wrapIndex(i + 1);
        }
        throw new IllegalArgumentException();
    }

    @GuardedBy({"this"})
    public int wrapIndex(int i) {
        int[] iArr = this.mEntries;
        return ((i % iArr.length) + iArr.length) % iArr.length;
    }

    /* loaded from: classes.dex */
    public final class LogIterator {
        public int mCount;
        public T mCurrentLogEvent;
        public long mCurrentTime;
        public int mIndex;
        public long mLogTime;
        public final long mModificationCount;

        public LogIterator() {
            synchronized (LocalEventLog.this) {
                this.mModificationCount = LocalEventLog.this.mModificationCount;
                this.mLogTime = LocalEventLog.this.mStartTime;
                this.mIndex = -1;
                this.mCount = -1;
                increment();
            }
        }

        public boolean hasNext() {
            boolean z;
            synchronized (LocalEventLog.this) {
                checkModifications();
                z = this.mCount < LocalEventLog.this.mLogSize;
            }
            return z;
        }

        public void next() {
            synchronized (LocalEventLog.this) {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                this.mCurrentTime = this.mLogTime + LocalEventLog.getTimeDelta(LocalEventLog.this.mEntries[this.mIndex]);
                T t = LocalEventLog.this.mLogEvents[this.mIndex];
                Objects.requireNonNull(t);
                this.mCurrentLogEvent = t;
                increment();
            }
        }

        public long getTime() {
            return this.mCurrentTime;
        }

        public T getLog() {
            return this.mCurrentLogEvent;
        }

        @GuardedBy({"LocalEventLog.this"})
        public final void increment() {
            int i;
            LocalEventLog localEventLog;
            long timeDelta = this.mIndex == -1 ? 0L : LocalEventLog.getTimeDelta(LocalEventLog.this.mEntries[i]);
            do {
                this.mLogTime += timeDelta;
                int incrementIndex = LocalEventLog.this.incrementIndex(this.mIndex);
                this.mIndex = incrementIndex;
                int i2 = this.mCount + 1;
                this.mCount = i2;
                LocalEventLog localEventLog2 = LocalEventLog.this;
                if (i2 < localEventLog2.mLogSize) {
                    timeDelta = LocalEventLog.getTimeDelta(localEventLog2.mEntries[incrementIndex]);
                }
                int i3 = this.mCount;
                localEventLog = LocalEventLog.this;
                if (i3 >= localEventLog.mLogSize) {
                    return;
                }
            } while (LocalEventLog.isFiller(localEventLog.mEntries[this.mIndex]));
        }

        @GuardedBy({"LocalEventLog.this"})
        public final void checkModifications() {
            if (this.mModificationCount != LocalEventLog.this.mModificationCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
}
