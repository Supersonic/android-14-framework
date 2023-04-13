package com.android.internal.util.jobs;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.IndentingPrintWriter;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public class StatLogger {
    public static final String TAG = "StatLogger";
    public final int SIZE;
    @GuardedBy({"mLock"})
    public final int[] mCallsPerSecond;
    @GuardedBy({"mLock"})
    public final int[] mCountStats;
    @GuardedBy({"mLock"})
    public final long[] mDurationPerSecond;
    @GuardedBy({"mLock"})
    public final long[] mDurationStats;
    public final String[] mLabels;
    public final Object mLock;
    @GuardedBy({"mLock"})
    public final int[] mMaxCallsPerSecond;
    @GuardedBy({"mLock"})
    public final long[] mMaxDurationPerSecond;
    @GuardedBy({"mLock"})
    public final long[] mMaxDurationStats;
    @GuardedBy({"mLock"})
    public long mNextTickTime;
    public final String mStatsTag;

    public StatLogger(String[] strArr) {
        this(null, strArr);
    }

    public StatLogger(String str, String[] strArr) {
        this.mLock = new Object();
        this.mNextTickTime = SystemClock.elapsedRealtime() + 1000;
        this.mStatsTag = str;
        int length = strArr.length;
        this.SIZE = length;
        this.mCountStats = new int[length];
        this.mDurationStats = new long[length];
        this.mCallsPerSecond = new int[length];
        this.mMaxCallsPerSecond = new int[length];
        this.mDurationPerSecond = new long[length];
        this.mMaxDurationPerSecond = new long[length];
        this.mMaxDurationStats = new long[length];
        this.mLabels = strArr;
    }

    public long getTime() {
        return SystemClock.uptimeNanos() / 1000;
    }

    public long logDurationStat(int i, long j) {
        synchronized (this.mLock) {
            long time = getTime() - j;
            if (i >= 0 && i < this.SIZE) {
                int[] iArr = this.mCountStats;
                iArr[i] = iArr[i] + 1;
                long[] jArr = this.mDurationStats;
                jArr[i] = jArr[i] + time;
                long[] jArr2 = this.mMaxDurationStats;
                if (jArr2[i] < time) {
                    jArr2[i] = time;
                }
                long elapsedRealtime = SystemClock.elapsedRealtime();
                if (elapsedRealtime > this.mNextTickTime) {
                    int[] iArr2 = this.mMaxCallsPerSecond;
                    int i2 = iArr2[i];
                    int[] iArr3 = this.mCallsPerSecond;
                    int i3 = iArr3[i];
                    if (i2 < i3) {
                        iArr2[i] = i3;
                    }
                    long[] jArr3 = this.mMaxDurationPerSecond;
                    long j2 = jArr3[i];
                    long[] jArr4 = this.mDurationPerSecond;
                    long j3 = jArr4[i];
                    if (j2 < j3) {
                        jArr3[i] = j3;
                    }
                    iArr3[i] = 0;
                    jArr4[i] = 0;
                    this.mNextTickTime = elapsedRealtime + 1000;
                }
                int[] iArr4 = this.mCallsPerSecond;
                iArr4[i] = iArr4[i] + 1;
                long[] jArr5 = this.mDurationPerSecond;
                jArr5[i] = jArr5[i] + time;
                return time;
            }
            Slog.wtf(TAG, "Invalid event ID: " + i);
            return time;
        }
    }

    public void dump(PrintWriter printWriter, String str) {
        dump(new IndentingPrintWriter(printWriter, "  ").setIndent(str));
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        synchronized (this.mLock) {
            if (TextUtils.isEmpty(this.mStatsTag)) {
                indentingPrintWriter.println("Stats:");
            } else {
                indentingPrintWriter.println(this.mStatsTag + XmlUtils.STRING_ARRAY_SEPARATOR);
            }
            indentingPrintWriter.increaseIndent();
            for (int i = 0; i < this.SIZE; i++) {
                int i2 = this.mCountStats[i];
                double d = this.mDurationStats[i] / 1000.0d;
                Object[] objArr = new Object[7];
                objArr[0] = this.mLabels[i];
                objArr[1] = Integer.valueOf(i2);
                objArr[2] = Double.valueOf(d);
                objArr[3] = Double.valueOf(i2 == 0 ? 0.0d : d / i2);
                objArr[4] = Integer.valueOf(this.mMaxCallsPerSecond[i]);
                objArr[5] = Double.valueOf(this.mMaxDurationPerSecond[i] / 1000.0d);
                objArr[6] = Double.valueOf(this.mMaxDurationStats[i] / 1000.0d);
                indentingPrintWriter.println(String.format("%s: count=%d, total=%.1fms, avg=%.3fms, max calls/s=%d max dur/s=%.1fms max time=%.1fms", objArr));
            }
            indentingPrintWriter.decreaseIndent();
        }
    }

    public void dumpProto(ProtoOutputStream protoOutputStream, long j) {
        synchronized (this.mLock) {
            long start = protoOutputStream.start(j);
            for (int i = 0; i < this.mLabels.length; i++) {
                long start2 = protoOutputStream.start(2246267895809L);
                protoOutputStream.write(1120986464257L, i);
                protoOutputStream.write(1138166333442L, this.mLabels[i]);
                protoOutputStream.write(1120986464259L, this.mCountStats[i]);
                protoOutputStream.write(1112396529668L, this.mDurationStats[i]);
                protoOutputStream.write(1120986464261L, this.mMaxCallsPerSecond[i]);
                protoOutputStream.write(1112396529670L, this.mMaxDurationPerSecond[i]);
                protoOutputStream.write(1112396529671L, this.mMaxDurationStats[i]);
                protoOutputStream.end(start2);
            }
            protoOutputStream.end(start);
        }
    }
}
