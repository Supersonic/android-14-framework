package com.android.server.job;

import android.app.job.JobParameters;
import android.os.UserHandle;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.IndentingPrintWriter;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import com.android.internal.util.jobs.RingBufferIndices;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.job.controllers.JobStatus;
/* loaded from: classes.dex */
public final class JobPackageTracker {
    public final RingBufferIndices mEventIndices = new RingBufferIndices(100);
    public final int[] mEventCmds = new int[100];
    public final long[] mEventTimes = new long[100];
    public final int[] mEventUids = new int[100];
    public final String[] mEventTags = new String[100];
    public final int[] mEventJobIds = new int[100];
    public final String[] mEventReasons = new String[100];
    public DataSet mCurDataSet = new DataSet();
    public DataSet[] mLastDataSets = new DataSet[5];

    public void addEvent(int i, int i2, String str, int i3, int i4, String str2) {
        int add = this.mEventIndices.add();
        this.mEventCmds[add] = i | ((i4 << 8) & 65280);
        this.mEventTimes[add] = JobSchedulerService.sElapsedRealtimeClock.millis();
        this.mEventUids[add] = i2;
        this.mEventTags[add] = str;
        this.mEventJobIds[add] = i3;
        this.mEventReasons[add] = str2;
    }

    /* loaded from: classes.dex */
    public static final class PackageEntry {
        public int activeCount;
        public int activeNesting;
        public long activeStartTime;
        public int activeTopCount;
        public int activeTopNesting;
        public long activeTopStartTime;
        public boolean hadActive;
        public boolean hadActiveTop;
        public boolean hadPending;
        public long pastActiveTime;
        public long pastActiveTopTime;
        public long pastPendingTime;
        public int pendingCount;
        public int pendingNesting;
        public long pendingStartTime;
        public final SparseIntArray stopReasons = new SparseIntArray();

        public long getActiveTime(long j) {
            long j2 = this.pastActiveTime;
            return this.activeNesting > 0 ? j2 + (j - this.activeStartTime) : j2;
        }

        public long getActiveTopTime(long j) {
            long j2 = this.pastActiveTopTime;
            return this.activeTopNesting > 0 ? j2 + (j - this.activeTopStartTime) : j2;
        }

        public long getPendingTime(long j) {
            long j2 = this.pastPendingTime;
            return this.pendingNesting > 0 ? j2 + (j - this.pendingStartTime) : j2;
        }
    }

    /* loaded from: classes.dex */
    public static final class DataSet {
        public final SparseArray<ArrayMap<String, PackageEntry>> mEntries;
        public int mMaxFgActive;
        public int mMaxTotalActive;
        public final long mStartClockTime;
        public final long mStartElapsedTime;
        public final long mStartUptimeTime;
        public long mSummedTime;

        public DataSet(DataSet dataSet) {
            this.mEntries = new SparseArray<>();
            this.mStartUptimeTime = dataSet.mStartUptimeTime;
            this.mStartElapsedTime = dataSet.mStartElapsedTime;
            this.mStartClockTime = dataSet.mStartClockTime;
        }

        public DataSet() {
            this.mEntries = new SparseArray<>();
            this.mStartUptimeTime = JobSchedulerService.sUptimeMillisClock.millis();
            this.mStartElapsedTime = JobSchedulerService.sElapsedRealtimeClock.millis();
            this.mStartClockTime = JobSchedulerService.sSystemClock.millis();
        }

        public final PackageEntry getOrCreateEntry(int i, String str) {
            ArrayMap<String, PackageEntry> arrayMap = this.mEntries.get(i);
            if (arrayMap == null) {
                arrayMap = new ArrayMap<>();
                this.mEntries.put(i, arrayMap);
            }
            PackageEntry packageEntry = arrayMap.get(str);
            if (packageEntry == null) {
                PackageEntry packageEntry2 = new PackageEntry();
                arrayMap.put(str, packageEntry2);
                return packageEntry2;
            }
            return packageEntry;
        }

        public PackageEntry getEntry(int i, String str) {
            ArrayMap<String, PackageEntry> arrayMap = this.mEntries.get(i);
            if (arrayMap == null) {
                return null;
            }
            return arrayMap.get(str);
        }

        public long getTotalTime(long j) {
            long j2 = this.mSummedTime;
            return j2 > 0 ? j2 : j - this.mStartUptimeTime;
        }

        public void incPending(int i, String str, long j) {
            PackageEntry orCreateEntry = getOrCreateEntry(i, str);
            int i2 = orCreateEntry.pendingNesting;
            if (i2 == 0) {
                orCreateEntry.pendingStartTime = j;
                orCreateEntry.pendingCount++;
            }
            orCreateEntry.pendingNesting = i2 + 1;
        }

        public void decPending(int i, String str, long j) {
            PackageEntry orCreateEntry = getOrCreateEntry(i, str);
            int i2 = orCreateEntry.pendingNesting;
            if (i2 == 1) {
                orCreateEntry.pastPendingTime += j - orCreateEntry.pendingStartTime;
            }
            orCreateEntry.pendingNesting = i2 - 1;
        }

        public void incActive(int i, String str, long j) {
            PackageEntry orCreateEntry = getOrCreateEntry(i, str);
            int i2 = orCreateEntry.activeNesting;
            if (i2 == 0) {
                orCreateEntry.activeStartTime = j;
                orCreateEntry.activeCount++;
            }
            orCreateEntry.activeNesting = i2 + 1;
        }

        public void decActive(int i, String str, long j, int i2) {
            PackageEntry orCreateEntry = getOrCreateEntry(i, str);
            int i3 = orCreateEntry.activeNesting;
            if (i3 == 1) {
                orCreateEntry.pastActiveTime += j - orCreateEntry.activeStartTime;
            }
            orCreateEntry.activeNesting = i3 - 1;
            orCreateEntry.stopReasons.put(i2, orCreateEntry.stopReasons.get(i2, 0) + 1);
        }

        public void incActiveTop(int i, String str, long j) {
            PackageEntry orCreateEntry = getOrCreateEntry(i, str);
            int i2 = orCreateEntry.activeTopNesting;
            if (i2 == 0) {
                orCreateEntry.activeTopStartTime = j;
                orCreateEntry.activeTopCount++;
            }
            orCreateEntry.activeTopNesting = i2 + 1;
        }

        public void decActiveTop(int i, String str, long j, int i2) {
            PackageEntry orCreateEntry = getOrCreateEntry(i, str);
            int i3 = orCreateEntry.activeTopNesting;
            if (i3 == 1) {
                orCreateEntry.pastActiveTopTime += j - orCreateEntry.activeTopStartTime;
            }
            orCreateEntry.activeTopNesting = i3 - 1;
            orCreateEntry.stopReasons.put(i2, orCreateEntry.stopReasons.get(i2, 0) + 1);
        }

        public void finish(DataSet dataSet, long j) {
            for (int size = this.mEntries.size() - 1; size >= 0; size--) {
                ArrayMap<String, PackageEntry> valueAt = this.mEntries.valueAt(size);
                for (int size2 = valueAt.size() - 1; size2 >= 0; size2--) {
                    PackageEntry valueAt2 = valueAt.valueAt(size2);
                    if (valueAt2.activeNesting > 0 || valueAt2.activeTopNesting > 0 || valueAt2.pendingNesting > 0) {
                        PackageEntry orCreateEntry = dataSet.getOrCreateEntry(this.mEntries.keyAt(size), valueAt.keyAt(size2));
                        orCreateEntry.activeStartTime = j;
                        orCreateEntry.activeNesting = valueAt2.activeNesting;
                        orCreateEntry.activeTopStartTime = j;
                        orCreateEntry.activeTopNesting = valueAt2.activeTopNesting;
                        orCreateEntry.pendingStartTime = j;
                        orCreateEntry.pendingNesting = valueAt2.pendingNesting;
                        if (valueAt2.activeNesting > 0) {
                            valueAt2.pastActiveTime += j - valueAt2.activeStartTime;
                            valueAt2.activeNesting = 0;
                        }
                        if (valueAt2.activeTopNesting > 0) {
                            valueAt2.pastActiveTopTime += j - valueAt2.activeTopStartTime;
                            valueAt2.activeTopNesting = 0;
                        }
                        if (valueAt2.pendingNesting > 0) {
                            valueAt2.pastPendingTime += j - valueAt2.pendingStartTime;
                            valueAt2.pendingNesting = 0;
                        }
                    }
                }
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        public void addTo(DataSet dataSet, long j) {
            boolean z;
            dataSet.mSummedTime += getTotalTime(j);
            int i = 1;
            int size = this.mEntries.size() - 1;
            while (size >= 0) {
                ArrayMap<String, PackageEntry> valueAt = this.mEntries.valueAt(size);
                int size2 = valueAt.size() - i;
                int i2 = i;
                while (size2 >= 0) {
                    PackageEntry valueAt2 = valueAt.valueAt(size2);
                    PackageEntry orCreateEntry = dataSet.getOrCreateEntry(this.mEntries.keyAt(size), valueAt.keyAt(size2));
                    long j2 = orCreateEntry.pastActiveTime + valueAt2.pastActiveTime;
                    orCreateEntry.pastActiveTime = j2;
                    orCreateEntry.activeCount += valueAt2.activeCount;
                    long j3 = orCreateEntry.pastActiveTopTime + valueAt2.pastActiveTopTime;
                    orCreateEntry.pastActiveTopTime = j3;
                    orCreateEntry.activeTopCount += valueAt2.activeTopCount;
                    ArrayMap<String, PackageEntry> arrayMap = valueAt;
                    long j4 = orCreateEntry.pastPendingTime + valueAt2.pastPendingTime;
                    orCreateEntry.pastPendingTime = j4;
                    orCreateEntry.pendingCount += valueAt2.pendingCount;
                    if (valueAt2.activeNesting > 0) {
                        orCreateEntry.pastActiveTime = j2 + (j - valueAt2.activeStartTime);
                        z = 1;
                        orCreateEntry.hadActive = true;
                    } else {
                        z = 1;
                    }
                    if (valueAt2.activeTopNesting > 0) {
                        orCreateEntry.pastActiveTopTime = j3 + (j - valueAt2.activeTopStartTime);
                        orCreateEntry.hadActiveTop = z;
                    }
                    if (valueAt2.pendingNesting > 0) {
                        orCreateEntry.pastPendingTime = j4 + (j - valueAt2.pendingStartTime);
                        orCreateEntry.hadPending = z;
                    }
                    for (int size3 = valueAt2.stopReasons.size() - (z ? 1 : 0); size3 >= 0; size3--) {
                        int keyAt = valueAt2.stopReasons.keyAt(size3);
                        SparseIntArray sparseIntArray = orCreateEntry.stopReasons;
                        sparseIntArray.put(keyAt, sparseIntArray.get(keyAt, 0) + valueAt2.stopReasons.valueAt(size3));
                    }
                    size2--;
                    valueAt = arrayMap;
                    i2 = z;
                }
                size--;
                i = i2;
            }
            int i3 = this.mMaxTotalActive;
            if (i3 > dataSet.mMaxTotalActive) {
                dataSet.mMaxTotalActive = i3;
            }
            int i4 = this.mMaxFgActive;
            if (i4 > dataSet.mMaxFgActive) {
                dataSet.mMaxFgActive = i4;
            }
        }

        public boolean printDuration(IndentingPrintWriter indentingPrintWriter, long j, long j2, int i, String str) {
            int i2 = (int) (((((float) j2) / ((float) j)) * 100.0f) + 0.5f);
            if (i2 > 0) {
                indentingPrintWriter.print(i2);
                indentingPrintWriter.print("% ");
                indentingPrintWriter.print(i);
                indentingPrintWriter.print("x ");
                indentingPrintWriter.print(str);
                return true;
            } else if (i > 0) {
                indentingPrintWriter.print(i);
                indentingPrintWriter.print("x ");
                indentingPrintWriter.print(str);
                return true;
            } else {
                return false;
            }
        }

        public void dump(IndentingPrintWriter indentingPrintWriter, String str, long j, long j2, int i) {
            DataSet dataSet = this;
            int i2 = i;
            long totalTime = dataSet.getTotalTime(j);
            indentingPrintWriter.print(str);
            indentingPrintWriter.print(" at ");
            indentingPrintWriter.print(DateFormat.format("yyyy-MM-dd-HH-mm-ss", dataSet.mStartClockTime).toString());
            indentingPrintWriter.print(" (");
            TimeUtils.formatDuration(dataSet.mStartElapsedTime, j2, indentingPrintWriter);
            indentingPrintWriter.print(") over ");
            TimeUtils.formatDuration(totalTime, indentingPrintWriter);
            indentingPrintWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.print("Max concurrency: ");
            indentingPrintWriter.print(dataSet.mMaxTotalActive);
            indentingPrintWriter.print(" total, ");
            indentingPrintWriter.print(dataSet.mMaxFgActive);
            indentingPrintWriter.println(" foreground");
            indentingPrintWriter.println();
            int size = dataSet.mEntries.size();
            int i3 = 0;
            while (i3 < size) {
                int keyAt = dataSet.mEntries.keyAt(i3);
                if (i2 == -1 || i2 == UserHandle.getAppId(keyAt)) {
                    ArrayMap<String, PackageEntry> valueAt = dataSet.mEntries.valueAt(i3);
                    int size2 = valueAt.size();
                    int i4 = 0;
                    while (i4 < size2) {
                        PackageEntry valueAt2 = valueAt.valueAt(i4);
                        UserHandle.formatUid(indentingPrintWriter, keyAt);
                        indentingPrintWriter.print(" / ");
                        indentingPrintWriter.print(valueAt.keyAt(i4));
                        indentingPrintWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
                        indentingPrintWriter.increaseIndent();
                        int i5 = size2;
                        int i6 = i4;
                        int i7 = keyAt;
                        ArrayMap<String, PackageEntry> arrayMap = valueAt;
                        int i8 = i3;
                        int i9 = size;
                        if (printDuration(indentingPrintWriter, totalTime, valueAt2.getPendingTime(j), valueAt2.pendingCount, "pending")) {
                            indentingPrintWriter.print(" ");
                        }
                        if (printDuration(indentingPrintWriter, totalTime, valueAt2.getActiveTime(j), valueAt2.activeCount, "active")) {
                            indentingPrintWriter.print(" ");
                        }
                        printDuration(indentingPrintWriter, totalTime, valueAt2.getActiveTopTime(j), valueAt2.activeTopCount, "active-top");
                        if (valueAt2.pendingNesting > 0 || valueAt2.hadPending) {
                            indentingPrintWriter.print(" (pending)");
                        }
                        if (valueAt2.activeNesting > 0 || valueAt2.hadActive) {
                            indentingPrintWriter.print(" (active)");
                        }
                        if (valueAt2.activeTopNesting > 0 || valueAt2.hadActiveTop) {
                            indentingPrintWriter.print(" (active-top)");
                        }
                        indentingPrintWriter.println();
                        if (valueAt2.stopReasons.size() > 0) {
                            for (int i10 = 0; i10 < valueAt2.stopReasons.size(); i10++) {
                                if (i10 > 0) {
                                    indentingPrintWriter.print(", ");
                                }
                                indentingPrintWriter.print(valueAt2.stopReasons.valueAt(i10));
                                indentingPrintWriter.print("x ");
                                indentingPrintWriter.print(JobParameters.getInternalReasonCodeDescription(valueAt2.stopReasons.keyAt(i10)));
                            }
                            indentingPrintWriter.println();
                        }
                        indentingPrintWriter.decreaseIndent();
                        i4 = i6 + 1;
                        i3 = i8;
                        size = i9;
                        size2 = i5;
                        keyAt = i7;
                        valueAt = arrayMap;
                    }
                }
                i3++;
                dataSet = this;
                i2 = i;
                size = size;
            }
            indentingPrintWriter.decreaseIndent();
        }

        public final void printPackageEntryState(ProtoOutputStream protoOutputStream, long j, long j2, int i) {
            long start = protoOutputStream.start(j);
            protoOutputStream.write(1112396529665L, j2);
            protoOutputStream.write(1120986464258L, i);
            protoOutputStream.end(start);
        }

        public void dump(ProtoOutputStream protoOutputStream, long j, long j2, long j3, int i) {
            long start = protoOutputStream.start(j);
            long totalTime = getTotalTime(j2);
            protoOutputStream.write(1112396529665L, this.mStartClockTime);
            protoOutputStream.write(1112396529666L, j3 - this.mStartElapsedTime);
            protoOutputStream.write(1112396529667L, totalTime);
            int size = this.mEntries.size();
            int i2 = 0;
            while (i2 < size) {
                int keyAt = this.mEntries.keyAt(i2);
                if (i == -1 || i == UserHandle.getAppId(keyAt)) {
                    ArrayMap<String, PackageEntry> valueAt = this.mEntries.valueAt(i2);
                    int size2 = valueAt.size();
                    int i3 = 0;
                    while (i3 < size2) {
                        long start2 = protoOutputStream.start(2246267895812L);
                        PackageEntry valueAt2 = valueAt.valueAt(i3);
                        protoOutputStream.write(1120986464257L, keyAt);
                        int i4 = size2;
                        protoOutputStream.write(1138166333442L, valueAt.keyAt(i3));
                        int i5 = i3;
                        int i6 = keyAt;
                        ArrayMap<String, PackageEntry> arrayMap = valueAt;
                        int i7 = i2;
                        printPackageEntryState(protoOutputStream, 1146756268035L, valueAt2.getPendingTime(j2), valueAt2.pendingCount);
                        printPackageEntryState(protoOutputStream, 1146756268036L, valueAt2.getActiveTime(j2), valueAt2.activeCount);
                        printPackageEntryState(protoOutputStream, 1146756268037L, valueAt2.getActiveTopTime(j2), valueAt2.activeTopCount);
                        boolean z = true;
                        protoOutputStream.write(1133871366150L, valueAt2.pendingNesting > 0 || valueAt2.hadPending);
                        protoOutputStream.write(1133871366151L, valueAt2.activeNesting > 0 || valueAt2.hadActive);
                        if (valueAt2.activeTopNesting <= 0 && !valueAt2.hadActiveTop) {
                            z = false;
                        }
                        protoOutputStream.write(1133871366152L, z);
                        for (int i8 = 0; i8 < valueAt2.stopReasons.size(); i8++) {
                            long start3 = protoOutputStream.start(2246267895817L);
                            protoOutputStream.write(1159641169921L, valueAt2.stopReasons.keyAt(i8));
                            protoOutputStream.write(1120986464258L, valueAt2.stopReasons.valueAt(i8));
                            protoOutputStream.end(start3);
                        }
                        protoOutputStream.end(start2);
                        i3 = i5 + 1;
                        keyAt = i6;
                        valueAt = arrayMap;
                        i2 = i7;
                        size2 = i4;
                    }
                }
                i2++;
            }
            protoOutputStream.write(1120986464261L, this.mMaxTotalActive);
            protoOutputStream.write(1120986464262L, this.mMaxFgActive);
            protoOutputStream.end(start);
        }
    }

    public void rebatchIfNeeded(long j) {
        long totalTime = this.mCurDataSet.getTotalTime(j);
        if (totalTime > 1800000) {
            DataSet dataSet = this.mCurDataSet;
            dataSet.mSummedTime = totalTime;
            DataSet dataSet2 = new DataSet();
            this.mCurDataSet = dataSet2;
            dataSet.finish(dataSet2, j);
            DataSet[] dataSetArr = this.mLastDataSets;
            System.arraycopy(dataSetArr, 0, dataSetArr, 1, dataSetArr.length - 1);
            this.mLastDataSets[0] = dataSet;
        }
    }

    public void notePending(JobStatus jobStatus) {
        long millis = JobSchedulerService.sUptimeMillisClock.millis();
        jobStatus.madePending = millis;
        rebatchIfNeeded(millis);
        this.mCurDataSet.incPending(jobStatus.getSourceUid(), jobStatus.getSourcePackageName(), millis);
    }

    public void noteNonpending(JobStatus jobStatus) {
        long millis = JobSchedulerService.sUptimeMillisClock.millis();
        this.mCurDataSet.decPending(jobStatus.getSourceUid(), jobStatus.getSourcePackageName(), millis);
        rebatchIfNeeded(millis);
    }

    public void noteActive(JobStatus jobStatus) {
        long millis = JobSchedulerService.sUptimeMillisClock.millis();
        jobStatus.madeActive = millis;
        rebatchIfNeeded(millis);
        if (jobStatus.lastEvaluatedBias >= 40) {
            this.mCurDataSet.incActiveTop(jobStatus.getSourceUid(), jobStatus.getSourcePackageName(), millis);
        } else {
            this.mCurDataSet.incActive(jobStatus.getSourceUid(), jobStatus.getSourcePackageName(), millis);
        }
        addEvent(jobStatus.getJob().isPeriodic() ? 3 : 1, jobStatus.getSourceUid(), jobStatus.getBatteryName(), jobStatus.getJobId(), 0, null);
    }

    public void noteInactive(JobStatus jobStatus, int i, String str) {
        long millis = JobSchedulerService.sUptimeMillisClock.millis();
        if (jobStatus.lastEvaluatedBias >= 40) {
            this.mCurDataSet.decActiveTop(jobStatus.getSourceUid(), jobStatus.getSourcePackageName(), millis, i);
        } else {
            this.mCurDataSet.decActive(jobStatus.getSourceUid(), jobStatus.getSourcePackageName(), millis, i);
        }
        rebatchIfNeeded(millis);
        addEvent(jobStatus.getJob().isPeriodic() ? 4 : 2, jobStatus.getSourceUid(), jobStatus.getBatteryName(), jobStatus.getJobId(), i, str);
    }

    public void noteConcurrency(int i, int i2) {
        DataSet dataSet = this.mCurDataSet;
        if (i > dataSet.mMaxTotalActive) {
            dataSet.mMaxTotalActive = i;
        }
        if (i2 > dataSet.mMaxFgActive) {
            dataSet.mMaxFgActive = i2;
        }
    }

    public float getLoadFactor(JobStatus jobStatus) {
        int sourceUid = jobStatus.getSourceUid();
        String sourcePackageName = jobStatus.getSourcePackageName();
        PackageEntry entry = this.mCurDataSet.getEntry(sourceUid, sourcePackageName);
        DataSet dataSet = this.mLastDataSets[0];
        PackageEntry entry2 = dataSet != null ? dataSet.getEntry(sourceUid, sourcePackageName) : null;
        if (entry == null && entry2 == null) {
            return 0.0f;
        }
        long millis = JobSchedulerService.sUptimeMillisClock.millis();
        long activeTime = entry != null ? 0 + entry.getActiveTime(millis) + entry.getPendingTime(millis) : 0L;
        long totalTime = this.mCurDataSet.getTotalTime(millis);
        if (entry2 != null) {
            activeTime += entry2.getActiveTime(millis) + entry2.getPendingTime(millis);
            totalTime += this.mLastDataSets[0].getTotalTime(millis);
        }
        return ((float) activeTime) / ((float) totalTime);
    }

    public void dump(IndentingPrintWriter indentingPrintWriter, int i) {
        DataSet dataSet;
        long millis = JobSchedulerService.sUptimeMillisClock.millis();
        long millis2 = JobSchedulerService.sElapsedRealtimeClock.millis();
        DataSet dataSet2 = this.mLastDataSets[0];
        if (dataSet2 != null) {
            DataSet dataSet3 = new DataSet(dataSet2);
            this.mLastDataSets[0].addTo(dataSet3, millis);
            dataSet = dataSet3;
        } else {
            dataSet = new DataSet(this.mCurDataSet);
        }
        this.mCurDataSet.addTo(dataSet, millis);
        int i2 = 1;
        while (true) {
            DataSet[] dataSetArr = this.mLastDataSets;
            if (i2 < dataSetArr.length) {
                DataSet dataSet4 = dataSetArr[i2];
                if (dataSet4 != null) {
                    dataSet4.dump(indentingPrintWriter, "Historical stats", millis, millis2, i);
                    indentingPrintWriter.println();
                }
                i2++;
            } else {
                dataSet.dump(indentingPrintWriter, "Current stats", millis, millis2, i);
                return;
            }
        }
    }

    public void dump(ProtoOutputStream protoOutputStream, long j, int i) {
        DataSet dataSet;
        int i2;
        long start = protoOutputStream.start(j);
        long millis = JobSchedulerService.sUptimeMillisClock.millis();
        long millis2 = JobSchedulerService.sElapsedRealtimeClock.millis();
        DataSet dataSet2 = this.mLastDataSets[0];
        if (dataSet2 != null) {
            DataSet dataSet3 = new DataSet(dataSet2);
            this.mLastDataSets[0].addTo(dataSet3, millis);
            dataSet = dataSet3;
        } else {
            dataSet = new DataSet(this.mCurDataSet);
        }
        this.mCurDataSet.addTo(dataSet, millis);
        int i3 = 1;
        while (true) {
            DataSet[] dataSetArr = this.mLastDataSets;
            if (i3 < dataSetArr.length) {
                DataSet dataSet4 = dataSetArr[i3];
                if (dataSet4 != null) {
                    i2 = i3;
                    dataSet4.dump(protoOutputStream, 2246267895809L, millis, millis2, i);
                } else {
                    i2 = i3;
                }
                i3 = i2 + 1;
            } else {
                dataSet.dump(protoOutputStream, 1146756268034L, millis, millis2, i);
                protoOutputStream.end(start);
                return;
            }
        }
    }

    public boolean dumpHistory(IndentingPrintWriter indentingPrintWriter, int i) {
        int i2;
        int size = this.mEventIndices.size();
        if (size <= 0) {
            return false;
        }
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("Job history:");
        indentingPrintWriter.decreaseIndent();
        long millis = JobSchedulerService.sElapsedRealtimeClock.millis();
        for (int i3 = 0; i3 < size; i3++) {
            int indexOf = this.mEventIndices.indexOf(i3);
            int i4 = this.mEventUids[indexOf];
            if ((i == -1 || i == UserHandle.getAppId(i4)) && (i2 = this.mEventCmds[indexOf] & 255) != 0) {
                String str = i2 != 1 ? i2 != 2 ? i2 != 3 ? i2 != 4 ? "     ??" : " STOP-P" : "START-P" : "   STOP" : "  START";
                TimeUtils.formatDuration(this.mEventTimes[indexOf] - millis, indentingPrintWriter, 19);
                indentingPrintWriter.print(" ");
                indentingPrintWriter.print(str);
                indentingPrintWriter.print(": #");
                UserHandle.formatUid(indentingPrintWriter, i4);
                indentingPrintWriter.print("/");
                indentingPrintWriter.print(this.mEventJobIds[indexOf]);
                indentingPrintWriter.print(" ");
                indentingPrintWriter.print(this.mEventTags[indexOf]);
                if (i2 == 2 || i2 == 4) {
                    indentingPrintWriter.print(" ");
                    String str2 = this.mEventReasons[indexOf];
                    if (str2 != null) {
                        indentingPrintWriter.print(str2);
                    } else {
                        indentingPrintWriter.print(JobParameters.getInternalReasonCodeDescription((this.mEventCmds[indexOf] & 65280) >> 8));
                    }
                }
                indentingPrintWriter.println();
            }
        }
        return true;
    }

    public void dumpHistory(ProtoOutputStream protoOutputStream, long j, int i) {
        int i2;
        int i3;
        int i4 = i;
        int size = this.mEventIndices.size();
        if (size == 0) {
            return;
        }
        long start = protoOutputStream.start(j);
        long millis = JobSchedulerService.sElapsedRealtimeClock.millis();
        int i5 = 0;
        while (i5 < size) {
            int indexOf = this.mEventIndices.indexOf(i5);
            int i6 = this.mEventUids[indexOf];
            if ((i4 == -1 || i4 == UserHandle.getAppId(i6)) && (i2 = this.mEventCmds[indexOf] & 255) != 0) {
                long start2 = protoOutputStream.start(2246267895809L);
                protoOutputStream.write(1159641169921L, i2);
                i3 = size;
                protoOutputStream.write(1112396529666L, millis - this.mEventTimes[indexOf]);
                protoOutputStream.write(1120986464259L, i6);
                protoOutputStream.write(1120986464260L, this.mEventJobIds[indexOf]);
                protoOutputStream.write(1138166333445L, this.mEventTags[indexOf]);
                if (i2 == 2 || i2 == 4) {
                    protoOutputStream.write(1159641169926L, (this.mEventCmds[indexOf] & 65280) >> 8);
                }
                protoOutputStream.end(start2);
            } else {
                i3 = size;
            }
            i5++;
            i4 = i;
            size = i3;
        }
        protoOutputStream.end(start);
    }
}
