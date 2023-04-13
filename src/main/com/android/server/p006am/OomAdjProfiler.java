package com.android.server.p006am;

import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.ProcessCpuTracker;
import com.android.internal.util.RingBuffer;
import com.android.internal.util.function.QuadConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import java.io.PrintWriter;
/* renamed from: com.android.server.am.OomAdjProfiler */
/* loaded from: classes.dex */
public class OomAdjProfiler {
    @GuardedBy({"this"})
    public boolean mLastScheduledOnBattery;
    @GuardedBy({"this"})
    public boolean mLastScheduledScreenOff;
    @GuardedBy({"this"})
    public long mLastSystemServerCpuTimeMs;
    @GuardedBy({"this"})
    public boolean mOnBattery;
    @GuardedBy({"this"})
    public long mOomAdjStartTimeUs;
    @GuardedBy({"this"})
    public boolean mOomAdjStarted;
    @GuardedBy({"this"})
    public boolean mScreenOff;
    @GuardedBy({"this"})
    public boolean mSystemServerCpuTimeUpdateScheduled;
    @GuardedBy({"this"})
    public int mTotalOomAdjCalls;
    @GuardedBy({"this"})
    public long mTotalOomAdjRunTimeUs;
    @GuardedBy({"this"})
    public CpuTimes mOomAdjRunTime = new CpuTimes();
    @GuardedBy({"this"})
    public CpuTimes mSystemServerCpuTime = new CpuTimes();
    public final ProcessCpuTracker mProcessCpuTracker = new ProcessCpuTracker(false);
    @GuardedBy({"this"})
    public final RingBuffer<CpuTimes> mOomAdjRunTimesHist = new RingBuffer<>(CpuTimes.class, 10);
    @GuardedBy({"this"})
    public final RingBuffer<CpuTimes> mSystemServerCpuTimesHist = new RingBuffer<>(CpuTimes.class, 10);

    public void batteryPowerChanged(boolean z) {
        synchronized (this) {
            scheduleSystemServerCpuTimeUpdate();
            this.mOnBattery = z;
        }
    }

    public void onWakefulnessChanged(int i) {
        synchronized (this) {
            scheduleSystemServerCpuTimeUpdate();
            boolean z = true;
            if (i == 1) {
                z = false;
            }
            this.mScreenOff = z;
        }
    }

    public void oomAdjStarted() {
        synchronized (this) {
            this.mOomAdjStartTimeUs = SystemClock.currentThreadTimeMicro();
            this.mOomAdjStarted = true;
        }
    }

    public void oomAdjEnded() {
        synchronized (this) {
            if (this.mOomAdjStarted) {
                long currentThreadTimeMicro = SystemClock.currentThreadTimeMicro() - this.mOomAdjStartTimeUs;
                this.mOomAdjRunTime.addCpuTimeUs(currentThreadTimeMicro);
                this.mTotalOomAdjRunTimeUs += currentThreadTimeMicro;
                this.mTotalOomAdjCalls++;
            }
        }
    }

    public final void scheduleSystemServerCpuTimeUpdate() {
        synchronized (this) {
            if (this.mSystemServerCpuTimeUpdateScheduled) {
                return;
            }
            this.mLastScheduledOnBattery = this.mOnBattery;
            this.mLastScheduledScreenOff = this.mScreenOff;
            this.mSystemServerCpuTimeUpdateScheduled = true;
            Message obtainMessage = PooledLambda.obtainMessage(new QuadConsumer() { // from class: com.android.server.am.OomAdjProfiler$$ExternalSyntheticLambda0
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4) {
                    ((OomAdjProfiler) obj).updateSystemServerCpuTime(((Boolean) obj2).booleanValue(), ((Boolean) obj3).booleanValue(), ((Boolean) obj4).booleanValue());
                }
            }, this, Boolean.valueOf(this.mLastScheduledOnBattery), Boolean.valueOf(this.mLastScheduledScreenOff), Boolean.TRUE);
            obtainMessage.setWhat(42);
            BackgroundThread.getHandler().sendMessage(obtainMessage);
        }
    }

    public final void updateSystemServerCpuTime(boolean z, boolean z2, boolean z3) {
        long cpuTimeForPid = this.mProcessCpuTracker.getCpuTimeForPid(Process.myPid());
        synchronized (this) {
            if (z3) {
                if (!this.mSystemServerCpuTimeUpdateScheduled) {
                    return;
                }
            }
            this.mSystemServerCpuTime.addCpuTimeMs(cpuTimeForPid - this.mLastSystemServerCpuTimeMs, z, z2);
            this.mLastSystemServerCpuTimeMs = cpuTimeForPid;
            this.mSystemServerCpuTimeUpdateScheduled = false;
        }
    }

    public void reset() {
        synchronized (this) {
            if (this.mSystemServerCpuTime.isEmpty()) {
                return;
            }
            this.mOomAdjRunTimesHist.append(this.mOomAdjRunTime);
            this.mSystemServerCpuTimesHist.append(this.mSystemServerCpuTime);
            this.mOomAdjRunTime = new CpuTimes();
            this.mSystemServerCpuTime = new CpuTimes();
        }
    }

    public void dump(PrintWriter printWriter) {
        synchronized (this) {
            if (this.mSystemServerCpuTimeUpdateScheduled) {
                BackgroundThread.getHandler().removeMessages(42);
                updateSystemServerCpuTime(this.mLastScheduledOnBattery, this.mLastScheduledScreenOff, false);
            } else {
                updateSystemServerCpuTime(this.mOnBattery, this.mScreenOff, false);
            }
            printWriter.println("System server and oomAdj runtimes (ms) in recent battery sessions (most recent first):");
            if (!this.mSystemServerCpuTime.isEmpty()) {
                printWriter.print("  ");
                printWriter.print("system_server=");
                printWriter.print(this.mSystemServerCpuTime);
                printWriter.print("  ");
                printWriter.print("oom_adj=");
                printWriter.println(this.mOomAdjRunTime);
            }
            CpuTimes[] cpuTimesArr = (CpuTimes[]) this.mSystemServerCpuTimesHist.toArray();
            CpuTimes[] cpuTimesArr2 = (CpuTimes[]) this.mOomAdjRunTimesHist.toArray();
            for (int length = cpuTimesArr2.length - 1; length >= 0; length--) {
                printWriter.print("  ");
                printWriter.print("system_server=");
                printWriter.print(cpuTimesArr[length]);
                printWriter.print("  ");
                printWriter.print("oom_adj=");
                printWriter.println(cpuTimesArr2[length]);
            }
            if (this.mTotalOomAdjCalls != 0) {
                printWriter.println("System server total oomAdj runtimes (us) since boot:");
                printWriter.print("  cpu time spent=");
                printWriter.print(this.mTotalOomAdjRunTimeUs);
                printWriter.print("  number of calls=");
                printWriter.print(this.mTotalOomAdjCalls);
                printWriter.print("  average=");
                printWriter.println(this.mTotalOomAdjRunTimeUs / this.mTotalOomAdjCalls);
            }
        }
    }

    /* renamed from: com.android.server.am.OomAdjProfiler$CpuTimes */
    /* loaded from: classes.dex */
    public class CpuTimes {
        public long mOnBatteryScreenOffTimeUs;
        public long mOnBatteryTimeUs;

        public CpuTimes() {
        }

        public void addCpuTimeMs(long j, boolean z, boolean z2) {
            addCpuTimeUs(j * 1000, z, z2);
        }

        public void addCpuTimeUs(long j) {
            addCpuTimeUs(j, OomAdjProfiler.this.mOnBattery, OomAdjProfiler.this.mScreenOff);
        }

        public void addCpuTimeUs(long j, boolean z, boolean z2) {
            if (z) {
                this.mOnBatteryTimeUs += j;
                if (z2) {
                    this.mOnBatteryScreenOffTimeUs += j;
                }
            }
        }

        public boolean isEmpty() {
            return this.mOnBatteryTimeUs == 0 && this.mOnBatteryScreenOffTimeUs == 0;
        }

        public String toString() {
            return "[" + (this.mOnBatteryTimeUs / 1000) + "," + (this.mOnBatteryScreenOffTimeUs / 1000) + "]";
        }
    }
}
