package com.android.ims.rcs.uce.request;

import android.util.Log;
import com.android.ims.rcs.uce.request.UceRequestDispatcher;
import com.android.ims.rcs.uce.request.UceRequestManager;
import com.android.ims.rcs.uce.util.UceUtils;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class UceRequestDispatcher {
    private static final String LOG_TAG = UceUtils.getLogPrefix() + "RequestDispatcher";
    private UceRequestManager.RequestManagerCallback mRequestManagerCallback;
    private final int mSubId;
    private long mIntervalTime = 100;
    private int mMaxConcurrentNum = 1;
    private final List<Request> mWaitingRequests = new ArrayList();
    private final List<Request> mExecutingRequests = new ArrayList();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Request {
        private final long mCoordinatorId;
        private Optional<Instant> mExecutingTime = Optional.empty();
        private final long mTaskId;

        public Request(long coordinatorId, long taskId) {
            this.mTaskId = taskId;
            this.mCoordinatorId = coordinatorId;
        }

        public long getCoordinatorId() {
            return this.mCoordinatorId;
        }

        public long getTaskId() {
            return this.mTaskId;
        }

        public void setExecutingTime(Instant instant) {
            this.mExecutingTime = Optional.of(instant);
        }

        public Optional<Instant> getExecutingTime() {
            return this.mExecutingTime;
        }
    }

    public UceRequestDispatcher(int subId, UceRequestManager.RequestManagerCallback callback) {
        this.mSubId = subId;
        this.mRequestManagerCallback = callback;
    }

    public synchronized void onDestroy() {
        this.mWaitingRequests.clear();
        this.mExecutingRequests.clear();
        this.mRequestManagerCallback = null;
    }

    public synchronized void addRequest(final long coordinatorId, List<Long> taskIds) {
        taskIds.stream().forEach(new Consumer() { // from class: com.android.ims.rcs.uce.request.UceRequestDispatcher$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                UceRequestDispatcher.this.lambda$addRequest$0(coordinatorId, (Long) obj);
            }
        });
        onRequestUpdated();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$addRequest$0(long coordinatorId, Long taskId) {
        Request request = new Request(coordinatorId, taskId.longValue());
        this.mWaitingRequests.add(request);
    }

    public synchronized void onRequestFinished(final Long taskId) {
        logd("onRequestFinished: taskId=" + taskId);
        this.mExecutingRequests.removeIf(new Predicate() { // from class: com.android.ims.rcs.uce.request.UceRequestDispatcher$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return UceRequestDispatcher.lambda$onRequestFinished$1(taskId, (UceRequestDispatcher.Request) obj);
            }
        });
        onRequestUpdated();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$onRequestFinished$1(Long taskId, Request request) {
        return request.getTaskId() == taskId.longValue();
    }

    private synchronized void onRequestUpdated() {
        logd("onRequestUpdated: waiting=" + this.mWaitingRequests.size() + ", executing=" + this.mExecutingRequests.size());
        if (this.mWaitingRequests.isEmpty()) {
            return;
        }
        int numCapacity = this.mMaxConcurrentNum - this.mExecutingRequests.size();
        if (numCapacity <= 0) {
            return;
        }
        List<Request> requestList = getRequestFromWaitingCollection(numCapacity);
        if (!requestList.isEmpty()) {
            notifyStartOfRequest(requestList);
        }
    }

    private List<Request> getRequestFromWaitingCollection(int numCapacity) {
        int numRequests = numCapacity < this.mWaitingRequests.size() ? numCapacity : this.mWaitingRequests.size();
        List<Request> requestList = new ArrayList<>();
        for (int i = 0; i < numRequests; i++) {
            requestList.add(this.mWaitingRequests.get(i));
        }
        this.mWaitingRequests.removeAll(requestList);
        return requestList;
    }

    private void notifyStartOfRequest(List<Request> requestList) {
        Instant baseTime;
        UceRequestManager.RequestManagerCallback callback = this.mRequestManagerCallback;
        if (callback == null) {
            logd("notifyStartOfRequest: The instance is destroyed");
            return;
        }
        Instant lastRequestTime = getLastRequestTime();
        if (lastRequestTime.plusMillis(this.mIntervalTime).isAfter(Instant.now())) {
            baseTime = lastRequestTime.plusMillis(this.mIntervalTime);
        } else {
            baseTime = Instant.now();
        }
        StringBuilder builder = new StringBuilder("notifyStartOfRequest: taskId=");
        for (int i = 0; i < requestList.size(); i++) {
            Instant startExecutingTime = baseTime.plusMillis(this.mIntervalTime * i);
            Request request = requestList.get(i);
            request.setExecutingTime(startExecutingTime);
            this.mExecutingRequests.add(request);
            long taskId = request.getTaskId();
            long coordId = request.getCoordinatorId();
            long delayTime = getDelayTime(startExecutingTime);
            this.mRequestManagerCallback.notifySendingRequest(coordId, taskId, delayTime);
            builder.append(request.getTaskId() + ", ");
        }
        builder.append("ExecutingRequests size=" + this.mExecutingRequests.size());
        logd(builder.toString());
    }

    private Instant getLastRequestTime() {
        if (this.mExecutingRequests.isEmpty()) {
            return Instant.MIN;
        }
        Instant lastTime = Instant.MIN;
        for (Request request : this.mExecutingRequests) {
            if (request.getExecutingTime().isPresent()) {
                Instant executingTime = request.getExecutingTime().get();
                if (executingTime.isAfter(lastTime)) {
                    lastTime = executingTime;
                }
            }
        }
        return lastTime;
    }

    private long getDelayTime(Instant executingTime) {
        long delayTime = Duration.between(executingTime, Instant.now()).toMillis();
        if (delayTime < 0) {
            return 0L;
        }
        return delayTime;
    }

    private void logd(String log) {
        Log.d(LOG_TAG, getLogPrefix().append(log).toString());
    }

    private StringBuilder getLogPrefix() {
        StringBuilder builder = new StringBuilder("[");
        builder.append(this.mSubId);
        builder.append("] ");
        return builder;
    }
}
