package com.android.server.job;

import android.util.Pools;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.job.PendingJobQueue;
import com.android.server.job.controllers.JobStatus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
/* loaded from: classes.dex */
public class PendingJobQueue {
    public final Pools.Pool<AppJobQueue> mAppJobQueuePool = new Pools.SimplePool(8);
    public final SparseArray<AppJobQueue> mCurrentQueues = new SparseArray<>();
    public final PriorityQueue<AppJobQueue> mOrderedQueues = new PriorityQueue<>(new Comparator() { // from class: com.android.server.job.PendingJobQueue$$ExternalSyntheticLambda0
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            int lambda$new$0;
            lambda$new$0 = PendingJobQueue.lambda$new$0((PendingJobQueue.AppJobQueue) obj, (PendingJobQueue.AppJobQueue) obj2);
            return lambda$new$0;
        }
    });
    public int mSize = 0;
    public boolean mOptimizeIteration = true;
    public int mPullCount = 0;
    public boolean mNeedToResetIterators = false;

    public static /* synthetic */ int lambda$new$0(AppJobQueue appJobQueue, AppJobQueue appJobQueue2) {
        long peekNextTimestamp = appJobQueue.peekNextTimestamp();
        long peekNextTimestamp2 = appJobQueue2.peekNextTimestamp();
        if (peekNextTimestamp == -1) {
            return peekNextTimestamp2 == -1 ? 0 : 1;
        } else if (peekNextTimestamp2 == -1) {
            return -1;
        } else {
            int peekNextOverrideState = appJobQueue.peekNextOverrideState();
            int peekNextOverrideState2 = appJobQueue2.peekNextOverrideState();
            if (peekNextOverrideState != peekNextOverrideState2) {
                return Integer.compare(peekNextOverrideState2, peekNextOverrideState);
            }
            return Long.compare(peekNextTimestamp, peekNextTimestamp2);
        }
    }

    public void add(JobStatus jobStatus) {
        AppJobQueue appJobQueue = getAppJobQueue(jobStatus.getSourceUid(), true);
        long peekNextTimestamp = appJobQueue.peekNextTimestamp();
        appJobQueue.add(jobStatus);
        this.mSize++;
        if (peekNextTimestamp != appJobQueue.peekNextTimestamp()) {
            this.mOrderedQueues.remove(appJobQueue);
            this.mOrderedQueues.offer(appJobQueue);
        }
    }

    public void addAll(List<JobStatus> list) {
        SparseArray sparseArray = new SparseArray();
        for (int size = list.size() - 1; size >= 0; size--) {
            JobStatus jobStatus = list.get(size);
            List list2 = (List) sparseArray.get(jobStatus.getSourceUid());
            if (list2 == null) {
                list2 = new ArrayList();
                sparseArray.put(jobStatus.getSourceUid(), list2);
            }
            list2.add(jobStatus);
        }
        for (int size2 = sparseArray.size() - 1; size2 >= 0; size2--) {
            getAppJobQueue(sparseArray.keyAt(size2), true).addAll((List) sparseArray.valueAt(size2));
        }
        this.mSize += list.size();
        this.mOrderedQueues.clear();
    }

    public void clear() {
        this.mSize = 0;
        for (int size = this.mCurrentQueues.size() - 1; size >= 0; size--) {
            AppJobQueue valueAt = this.mCurrentQueues.valueAt(size);
            valueAt.clear();
            this.mAppJobQueuePool.release(valueAt);
        }
        this.mCurrentQueues.clear();
        this.mOrderedQueues.clear();
    }

    public boolean contains(JobStatus jobStatus) {
        AppJobQueue appJobQueue = this.mCurrentQueues.get(jobStatus.getSourceUid());
        if (appJobQueue == null) {
            return false;
        }
        return appJobQueue.contains(jobStatus);
    }

    public final AppJobQueue getAppJobQueue(int i, boolean z) {
        AppJobQueue appJobQueue = this.mCurrentQueues.get(i);
        if (appJobQueue == null && z) {
            AppJobQueue appJobQueue2 = (AppJobQueue) this.mAppJobQueuePool.acquire();
            if (appJobQueue2 == null) {
                appJobQueue2 = new AppJobQueue();
            }
            AppJobQueue appJobQueue3 = appJobQueue2;
            this.mCurrentQueues.put(i, appJobQueue3);
            return appJobQueue3;
        }
        return appJobQueue;
    }

    public JobStatus next() {
        if (this.mNeedToResetIterators) {
            this.mOrderedQueues.clear();
            for (int size = this.mCurrentQueues.size() - 1; size >= 0; size--) {
                AppJobQueue valueAt = this.mCurrentQueues.valueAt(size);
                valueAt.resetIterator(0L);
                this.mOrderedQueues.offer(valueAt);
            }
            this.mNeedToResetIterators = false;
            this.mPullCount = 0;
        } else if (this.mOrderedQueues.size() == 0) {
            for (int size2 = this.mCurrentQueues.size() - 1; size2 >= 0; size2--) {
                this.mOrderedQueues.offer(this.mCurrentQueues.valueAt(size2));
            }
            this.mPullCount = 0;
        }
        int size3 = this.mOrderedQueues.size();
        JobStatus jobStatus = null;
        if (size3 == 0) {
            return null;
        }
        int min = this.mOptimizeIteration ? Math.min(3, ((size3 - 1) >>> 2) + 1) : 1;
        AppJobQueue peek = this.mOrderedQueues.peek();
        if (peek != null) {
            jobStatus = peek.next();
            int i = this.mPullCount + 1;
            this.mPullCount = i;
            if (i >= min || ((jobStatus != null && peek.peekNextOverrideState() != jobStatus.overrideState) || peek.peekNextTimestamp() == -1)) {
                this.mOrderedQueues.poll();
                if (peek.peekNextTimestamp() != -1) {
                    this.mOrderedQueues.offer(peek);
                }
                this.mPullCount = 0;
            }
        }
        return jobStatus;
    }

    public boolean remove(JobStatus jobStatus) {
        AppJobQueue appJobQueue = getAppJobQueue(jobStatus.getSourceUid(), false);
        if (appJobQueue == null) {
            return false;
        }
        long peekNextTimestamp = appJobQueue.peekNextTimestamp();
        if (appJobQueue.remove(jobStatus)) {
            this.mSize--;
            if (appJobQueue.size() == 0) {
                this.mCurrentQueues.remove(jobStatus.getSourceUid());
                this.mOrderedQueues.remove(appJobQueue);
                appJobQueue.clear();
                this.mAppJobQueuePool.release(appJobQueue);
            } else if (peekNextTimestamp != appJobQueue.peekNextTimestamp()) {
                this.mOrderedQueues.remove(appJobQueue);
                this.mOrderedQueues.offer(appJobQueue);
            }
            return true;
        }
        return false;
    }

    public void resetIterator() {
        this.mNeedToResetIterators = true;
    }

    @VisibleForTesting
    public void setOptimizeIteration(boolean z) {
        this.mOptimizeIteration = z;
    }

    public int size() {
        return this.mSize;
    }

    /* loaded from: classes.dex */
    public static final class AppJobQueue {
        public int mCurIndex;
        public final List<AdjustedJobStatus> mJobs;
        public static final Comparator<AdjustedJobStatus> sJobComparator = new Comparator() { // from class: com.android.server.job.PendingJobQueue$AppJobQueue$$ExternalSyntheticLambda0
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int lambda$static$0;
                lambda$static$0 = PendingJobQueue.AppJobQueue.lambda$static$0((PendingJobQueue.AppJobQueue.AdjustedJobStatus) obj, (PendingJobQueue.AppJobQueue.AdjustedJobStatus) obj2);
                return lambda$static$0;
            }
        };
        public static final Pools.Pool<AdjustedJobStatus> mAdjustedJobStatusPool = new Pools.SimplePool(16);

        public AppJobQueue() {
            this.mJobs = new ArrayList();
            this.mCurIndex = 0;
        }

        /* loaded from: classes.dex */
        public static class AdjustedJobStatus {
            public long adjustedEnqueueTime;
            public JobStatus job;

            public AdjustedJobStatus() {
            }

            public void clear() {
                this.adjustedEnqueueTime = 0L;
                this.job = null;
            }
        }

        public static /* synthetic */ int lambda$static$0(AdjustedJobStatus adjustedJobStatus, AdjustedJobStatus adjustedJobStatus2) {
            JobStatus jobStatus;
            JobStatus jobStatus2;
            int effectivePriority;
            int effectivePriority2;
            if (adjustedJobStatus == adjustedJobStatus2 || (jobStatus = adjustedJobStatus.job) == (jobStatus2 = adjustedJobStatus2.job)) {
                return 0;
            }
            int i = jobStatus.overrideState;
            int i2 = jobStatus2.overrideState;
            if (i != i2) {
                return Integer.compare(i2, i);
            }
            boolean isUserInitiated = jobStatus.getJob().isUserInitiated();
            if (isUserInitiated != jobStatus2.getJob().isUserInitiated()) {
                return isUserInitiated ? -1 : 1;
            }
            boolean isRequestedExpeditedJob = jobStatus.isRequestedExpeditedJob();
            if (isRequestedExpeditedJob != jobStatus2.isRequestedExpeditedJob()) {
                return isRequestedExpeditedJob ? -1 : 1;
            } else if (Objects.equals(jobStatus.getNamespace(), jobStatus2.getNamespace()) && (effectivePriority = jobStatus.getEffectivePriority()) != (effectivePriority2 = jobStatus2.getEffectivePriority())) {
                return Integer.compare(effectivePriority2, effectivePriority);
            } else {
                int i3 = jobStatus.lastEvaluatedBias;
                int i4 = jobStatus2.lastEvaluatedBias;
                if (i3 != i4) {
                    return Integer.compare(i4, i3);
                }
                return Long.compare(jobStatus.enqueueTime, jobStatus2.enqueueTime);
            }
        }

        public void add(JobStatus jobStatus) {
            AdjustedJobStatus adjustedJobStatus = (AdjustedJobStatus) mAdjustedJobStatusPool.acquire();
            if (adjustedJobStatus == null) {
                adjustedJobStatus = new AdjustedJobStatus();
            }
            adjustedJobStatus.adjustedEnqueueTime = jobStatus.enqueueTime;
            adjustedJobStatus.job = jobStatus;
            int binarySearch = Collections.binarySearch(this.mJobs, adjustedJobStatus, sJobComparator);
            if (binarySearch < 0) {
                binarySearch = ~binarySearch;
            }
            this.mJobs.add(binarySearch, adjustedJobStatus);
            if (binarySearch < this.mCurIndex) {
                this.mCurIndex = binarySearch;
            }
            if (binarySearch > 0) {
                adjustedJobStatus.adjustedEnqueueTime = Math.max(this.mJobs.get(binarySearch - 1).adjustedEnqueueTime, adjustedJobStatus.adjustedEnqueueTime);
            }
            int size = this.mJobs.size();
            if (binarySearch < size - 1) {
                while (binarySearch < size) {
                    AdjustedJobStatus adjustedJobStatus2 = this.mJobs.get(binarySearch);
                    long j = adjustedJobStatus.adjustedEnqueueTime;
                    if (j < adjustedJobStatus2.adjustedEnqueueTime) {
                        return;
                    }
                    adjustedJobStatus2.adjustedEnqueueTime = j;
                    binarySearch++;
                }
            }
        }

        public void addAll(List<JobStatus> list) {
            int i = Integer.MAX_VALUE;
            for (int size = list.size() - 1; size >= 0; size--) {
                JobStatus jobStatus = list.get(size);
                AdjustedJobStatus adjustedJobStatus = (AdjustedJobStatus) mAdjustedJobStatusPool.acquire();
                if (adjustedJobStatus == null) {
                    adjustedJobStatus = new AdjustedJobStatus();
                }
                adjustedJobStatus.adjustedEnqueueTime = jobStatus.enqueueTime;
                adjustedJobStatus.job = jobStatus;
                int binarySearch = Collections.binarySearch(this.mJobs, adjustedJobStatus, sJobComparator);
                if (binarySearch < 0) {
                    binarySearch = ~binarySearch;
                }
                this.mJobs.add(binarySearch, adjustedJobStatus);
                if (binarySearch < this.mCurIndex) {
                    this.mCurIndex = binarySearch;
                }
                i = Math.min(i, binarySearch);
            }
            int size2 = this.mJobs.size();
            for (int max = Math.max(i, 1); max < size2; max++) {
                AdjustedJobStatus adjustedJobStatus2 = this.mJobs.get(max);
                adjustedJobStatus2.adjustedEnqueueTime = Math.max(adjustedJobStatus2.adjustedEnqueueTime, this.mJobs.get(max - 1).adjustedEnqueueTime);
            }
        }

        public void clear() {
            this.mJobs.clear();
            this.mCurIndex = 0;
        }

        public boolean contains(JobStatus jobStatus) {
            return indexOf(jobStatus) >= 0;
        }

        public final int indexOf(JobStatus jobStatus) {
            int size = this.mJobs.size();
            for (int i = 0; i < size; i++) {
                if (this.mJobs.get(i).job == jobStatus) {
                    return i;
                }
            }
            return -1;
        }

        public JobStatus next() {
            if (this.mCurIndex >= this.mJobs.size()) {
                return null;
            }
            List<AdjustedJobStatus> list = this.mJobs;
            int i = this.mCurIndex;
            this.mCurIndex = i + 1;
            return list.get(i).job;
        }

        public int peekNextOverrideState() {
            if (this.mCurIndex >= this.mJobs.size()) {
                return -1;
            }
            return this.mJobs.get(this.mCurIndex).job.overrideState;
        }

        public long peekNextTimestamp() {
            if (this.mCurIndex >= this.mJobs.size()) {
                return -1L;
            }
            return this.mJobs.get(this.mCurIndex).adjustedEnqueueTime;
        }

        public boolean remove(JobStatus jobStatus) {
            int indexOf = indexOf(jobStatus);
            if (indexOf < 0) {
                return false;
            }
            AdjustedJobStatus remove = this.mJobs.remove(indexOf);
            remove.clear();
            mAdjustedJobStatusPool.release(remove);
            int i = this.mCurIndex;
            if (indexOf < i) {
                this.mCurIndex = i - 1;
            }
            return true;
        }

        public void resetIterator(long j) {
            int i = 0;
            if (j == 0 || this.mJobs.size() == 0) {
                this.mCurIndex = 0;
                return;
            }
            int size = this.mJobs.size() - 1;
            while (i < size) {
                int i2 = (i + size) >>> 1;
                long j2 = this.mJobs.get(i2).adjustedEnqueueTime;
                if (j2 < j) {
                    i = i2 + 1;
                } else {
                    if (j2 > j) {
                        i2--;
                    }
                    size = i2;
                }
            }
            this.mCurIndex = size;
        }

        public int size() {
            return this.mJobs.size();
        }
    }
}
