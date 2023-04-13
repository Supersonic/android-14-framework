package com.android.internal.util;

import android.p008os.SystemClock;
import android.text.TextUtils;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import java.io.PrintWriter;
/* loaded from: classes3.dex */
public class StatLogger {
    private static final String TAG = "StatLogger";
    private final int SIZE;
    private final int[] mCallsPerSecond;
    private final int[] mCountStats;
    private final long[] mDurationPerSecond;
    private final long[] mDurationStats;
    private final String[] mLabels;
    private final Object mLock;
    private final int[] mMaxCallsPerSecond;
    private final long[] mMaxDurationPerSecond;
    private final long[] mMaxDurationStats;
    private long mNextTickTime;
    private final String mStatsTag;

    public StatLogger(String[] eventLabels) {
        this(null, eventLabels);
    }

    public StatLogger(String statsTag, String[] eventLabels) {
        this.mLock = new Object();
        this.mNextTickTime = SystemClock.elapsedRealtime() + 1000;
        this.mStatsTag = statsTag;
        int length = eventLabels.length;
        this.SIZE = length;
        this.mCountStats = new int[length];
        this.mDurationStats = new long[length];
        this.mCallsPerSecond = new int[length];
        this.mMaxCallsPerSecond = new int[length];
        this.mDurationPerSecond = new long[length];
        this.mMaxDurationPerSecond = new long[length];
        this.mMaxDurationStats = new long[length];
        this.mLabels = eventLabels;
    }

    public long getTime() {
        return SystemClock.uptimeNanos() / 1000;
    }

    public long logDurationStat(int eventId, long start) {
        synchronized (this.mLock) {
            long duration = getTime() - start;
            if (eventId >= 0 && eventId < this.SIZE) {
                int[] iArr = this.mCountStats;
                iArr[eventId] = iArr[eventId] + 1;
                long[] jArr = this.mDurationStats;
                jArr[eventId] = jArr[eventId] + duration;
                long[] jArr2 = this.mMaxDurationStats;
                if (jArr2[eventId] < duration) {
                    jArr2[eventId] = duration;
                }
                long nowRealtime = SystemClock.elapsedRealtime();
                if (nowRealtime > this.mNextTickTime) {
                    int[] iArr2 = this.mMaxCallsPerSecond;
                    int i = iArr2[eventId];
                    int[] iArr3 = this.mCallsPerSecond;
                    int i2 = iArr3[eventId];
                    if (i < i2) {
                        iArr2[eventId] = i2;
                    }
                    long[] jArr3 = this.mMaxDurationPerSecond;
                    long j = jArr3[eventId];
                    long[] jArr4 = this.mDurationPerSecond;
                    long j2 = jArr4[eventId];
                    if (j < j2) {
                        jArr3[eventId] = j2;
                    }
                    iArr3[eventId] = 0;
                    jArr4[eventId] = 0;
                    this.mNextTickTime = 1000 + nowRealtime;
                }
                int[] iArr4 = this.mCallsPerSecond;
                iArr4[eventId] = iArr4[eventId] + 1;
                long[] jArr5 = this.mDurationPerSecond;
                jArr5[eventId] = jArr5[eventId] + duration;
                return duration;
            }
            Slog.wtf(TAG, "Invalid event ID: " + eventId);
            return duration;
        }
    }

    public void dump(PrintWriter pw, String prefix) {
        dump(new android.util.IndentingPrintWriter(pw, "  ").setIndent(prefix));
    }

    public void dump(android.util.IndentingPrintWriter pw) {
        synchronized (this.mLock) {
            if (!TextUtils.isEmpty(this.mStatsTag)) {
                pw.println(this.mStatsTag + ":");
            } else {
                pw.println("Stats:");
            }
            pw.increaseIndent();
            for (int i = 0; i < this.SIZE; i++) {
                int count = this.mCountStats[i];
                double durationMs = this.mDurationStats[i] / 1000.0d;
                Object[] objArr = new Object[7];
                objArr[0] = this.mLabels[i];
                objArr[1] = Integer.valueOf(count);
                objArr[2] = Double.valueOf(durationMs);
                objArr[3] = Double.valueOf(count == 0 ? 0.0d : durationMs / count);
                objArr[4] = Integer.valueOf(this.mMaxCallsPerSecond[i]);
                objArr[5] = Double.valueOf(this.mMaxDurationPerSecond[i] / 1000.0d);
                objArr[6] = Double.valueOf(this.mMaxDurationStats[i] / 1000.0d);
                pw.println(String.format("%s: count=%d, total=%.1fms, avg=%.3fms, max calls/s=%d max dur/s=%.1fms max time=%.1fms", objArr));
            }
            pw.decreaseIndent();
        }
    }

    public void dumpProto(ProtoOutputStream proto, long fieldId) {
        synchronized (this.mLock) {
            long outer = proto.start(fieldId);
            for (int i = 0; i < this.mLabels.length; i++) {
                long inner = proto.start(2246267895809L);
                proto.write(1120986464257L, i);
                proto.write(1138166333442L, this.mLabels[i]);
                proto.write(1120986464259L, this.mCountStats[i]);
                proto.write(1112396529668L, this.mDurationStats[i]);
                proto.write(1120986464261L, this.mMaxCallsPerSecond[i]);
                proto.write(1112396529670L, this.mMaxDurationPerSecond[i]);
                proto.write(1112396529671L, this.mMaxDurationStats[i]);
                proto.end(inner);
            }
            proto.end(outer);
        }
    }
}
