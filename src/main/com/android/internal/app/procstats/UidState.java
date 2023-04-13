package com.android.internal.app.procstats;

import android.p008os.Parcel;
import android.p008os.UserHandle;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.TimeUtils;
import java.io.PrintWriter;
import java.util.function.Predicate;
/* loaded from: classes4.dex */
public final class UidState {
    private static final String TAG = "ProcessStats";
    private final DurationsTable mDurations;
    private long mStartTime;
    private final ProcessStats mStats;
    private long mTotalRunningDuration;
    private long mTotalRunningStartTime;
    private final int mUid;
    private ArraySet<ProcessState> mProcesses = new ArraySet<>();
    private int mCurCombinedState = -1;

    public UidState(ProcessStats processStats, int uid) {
        this.mStats = processStats;
        this.mUid = uid;
        this.mDurations = new DurationsTable(processStats.mTableData);
    }

    /* renamed from: clone */
    public UidState m6675clone() {
        UidState unew = new UidState(this.mStats, this.mUid);
        unew.mDurations.addDurations(this.mDurations);
        unew.mCurCombinedState = this.mCurCombinedState;
        unew.mStartTime = this.mStartTime;
        unew.mTotalRunningStartTime = this.mTotalRunningStartTime;
        unew.mTotalRunningDuration = this.mTotalRunningDuration;
        return unew;
    }

    public void updateCombinedState(int state, long now) {
        if (this.mCurCombinedState != state) {
            updateCombinedState(now);
        }
    }

    public void updateCombinedState(long now) {
        setCombinedStateInner(calcCombinedState(), now);
    }

    private int calcCombinedState() {
        int minCombined = -1;
        int min = -1;
        int size = this.mProcesses.size();
        for (int i = 0; i < size; i++) {
            int combinedState = this.mProcesses.valueAt(i).getCombinedState();
            int state = combinedState % 16;
            if (combinedState != -1 && (min == -1 || state < min)) {
                minCombined = combinedState;
                min = state;
            }
        }
        return minCombined;
    }

    private void setCombinedStateInner(int state, long now) {
        if (this.mCurCombinedState != state) {
            if (now >= 0) {
                commitStateTime(now);
                if (state != -1) {
                    if (this.mCurCombinedState == -1) {
                        this.mTotalRunningDuration = 0L;
                    }
                } else {
                    this.mTotalRunningDuration += now - this.mTotalRunningStartTime;
                }
            }
            this.mCurCombinedState = state;
        }
    }

    public int getCombinedState() {
        return this.mCurCombinedState;
    }

    public void commitStateTime(long now) {
        int i = this.mCurCombinedState;
        if (i != -1) {
            long dur = now - this.mStartTime;
            if (dur > 0) {
                this.mDurations.addDuration(i, dur);
            }
            this.mTotalRunningDuration += now - this.mTotalRunningStartTime;
            this.mTotalRunningStartTime = now;
        }
        this.mStartTime = now;
    }

    public void resetSafely(long now) {
        this.mDurations.resetTable();
        this.mStartTime = now;
        this.mProcesses.removeIf(new Predicate() { // from class: com.android.internal.app.procstats.UidState$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return UidState.lambda$resetSafely$0((ProcessState) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$resetSafely$0(ProcessState p) {
        return !p.isInUse();
    }

    public boolean isInUse() {
        int size = this.mProcesses.size();
        for (int i = 0; i < size; i++) {
            if (this.mProcesses.valueAt(i).isInUse()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPackage(String packageName) {
        int size = this.mProcesses.size();
        for (int i = 0; i < size; i++) {
            ProcessState proc = this.mProcesses.valueAt(i);
            if (TextUtils.equals(packageName, proc.getName()) && TextUtils.equals(packageName, proc.getPackage())) {
                return true;
            }
        }
        return false;
    }

    public void add(UidState other) {
        this.mDurations.addDurations(other.mDurations);
        this.mTotalRunningDuration += other.mTotalRunningDuration;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addProcess(ProcessState proc) {
        this.mProcesses.add(proc);
    }

    void addProcess(ProcessState proc, long now) {
        this.mProcesses.add(proc);
        setCombinedStateInner(proc.getCombinedState(), now);
    }

    void removeProcess(ProcessState proc, long now) {
        this.mProcesses.remove(proc);
        setCombinedStateInner(proc.getCombinedState(), now);
    }

    public int getDurationsBucketCount() {
        return this.mDurations.getKeyCount();
    }

    public long getTotalRunningDuration(long now) {
        long j = this.mTotalRunningDuration;
        long j2 = this.mTotalRunningStartTime;
        return j + (j2 != 0 ? now - j2 : 0L);
    }

    public long getDuration(int state, long now) {
        long time = this.mDurations.getValueForId((byte) state);
        if (this.mCurCombinedState == state) {
            return time + (now - this.mStartTime);
        }
        return time;
    }

    public long[] getAggregatedDurationsInStates() {
        long[] states = new long[16];
        int numOfBuckets = getDurationsBucketCount();
        for (int i = 0; i < numOfBuckets; i++) {
            int key = this.mDurations.getKeyAt(i);
            int combinedState = SparseMappingTable.getIdFromKey(key);
            int i2 = combinedState % 16;
            states[i2] = states[i2] + this.mDurations.getValue(key);
        }
        return states;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeToParcel(Parcel out, long now) {
        this.mDurations.writeToParcel(out);
        out.writeLong(getTotalRunningDuration(now));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean readFromParcel(Parcel in) {
        if (!this.mDurations.readFromParcel(in)) {
            return false;
        }
        this.mTotalRunningDuration = in.readLong();
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("UidState{").append(Integer.toHexString(System.identityHashCode(this))).append(" ").append(UserHandle.formatUid(this.mUid)).append("}");
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dumpState(PrintWriter pw, String prefix, int[] screenStates, int[] memStates, int[] procStates, long now) {
        int i;
        long time;
        UidState uidState = this;
        int[] iArr = screenStates;
        long totalTime = 0;
        int printedScreen = -1;
        int is = 0;
        while (is < iArr.length) {
            int printedMem = -1;
            int im = 0;
            while (im < memStates.length) {
                int ip = 0;
                while (ip < procStates.length) {
                    int iscreen = iArr[is];
                    int imem = memStates[im];
                    int bucket = ((iscreen + imem) * 16) + procStates[ip];
                    int is2 = is;
                    int im2 = im;
                    long time2 = uidState.mDurations.getValueForId((byte) bucket);
                    String running = "";
                    if (uidState.mCurCombinedState == bucket) {
                        running = " (running)";
                        time = time2 + (now - uidState.mStartTime);
                    } else {
                        time = time2;
                    }
                    if (time != 0) {
                        pw.print(prefix);
                        if (iArr.length > 1) {
                            DumpUtils.printScreenLabel(pw, printedScreen != iscreen ? iscreen : -1);
                            printedScreen = iscreen;
                        }
                        if (memStates.length > 1) {
                            DumpUtils.printMemLabel(pw, printedMem != imem ? imem : -1, '/');
                            printedMem = imem;
                        }
                        pw.print(DumpUtils.STATE_LABELS[procStates[ip]]);
                        pw.print(": ");
                        TimeUtils.formatDuration(time, pw);
                        pw.println(running);
                        totalTime += time;
                    }
                    ip++;
                    uidState = this;
                    iArr = screenStates;
                    is = is2;
                    im = im2;
                }
                im++;
                uidState = this;
                iArr = screenStates;
            }
            is++;
            uidState = this;
            iArr = screenStates;
        }
        if (totalTime != 0) {
            pw.print(prefix);
            if (screenStates.length <= 1) {
                i = -1;
            } else {
                i = -1;
                DumpUtils.printScreenLabel(pw, -1);
            }
            if (memStates.length > 1) {
                DumpUtils.printMemLabel(pw, i, '/');
            }
            pw.print(DumpUtils.STATE_LABEL_TOTAL);
            pw.print(": ");
            TimeUtils.formatDuration(totalTime, pw);
            pw.println();
        }
    }
}
