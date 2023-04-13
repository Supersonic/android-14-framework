package com.android.ims.rcs.uce.request;

import android.telephony.ims.SipDetails;
import android.util.Log;
import com.android.ims.rcs.uce.request.UceRequestManager;
import com.android.ims.rcs.uce.util.UceUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public abstract class UceRequestCoordinator {
    protected static final int DEFAULT_ERROR_CODE = 1;
    private static final String LOG_TAG = UceUtils.getLogPrefix() + "ReqCoordinator";
    protected static Map<Integer, String> REQUEST_EVENT_DESC = null;
    public static final int REQUEST_UPDATE_CACHED_CAPABILITY_UPDATE = 5;
    public static final int REQUEST_UPDATE_CAPABILITY_UPDATE = 3;
    public static final int REQUEST_UPDATE_COMMAND_ERROR = 1;
    public static final int REQUEST_UPDATE_ERROR = 0;
    public static final int REQUEST_UPDATE_NETWORK_RESPONSE = 2;
    public static final int REQUEST_UPDATE_NO_NEED_REQUEST_FROM_NETWORK = 7;
    public static final int REQUEST_UPDATE_REMOTE_REQUEST_DONE = 8;
    public static final int REQUEST_UPDATE_RESOURCE_TERMINATED = 4;
    public static final int REQUEST_UPDATE_TERMINATED = 6;
    public static final int REQUEST_UPDATE_TIMEOUT = 9;
    protected final Map<Long, UceRequest> mActivatedRequests;
    protected final Object mCollectionLock = new Object();
    protected final long mCoordinatorId = UceUtils.generateRequestCoordinatorId();
    protected final Map<Long, RequestResult> mFinishedRequests;
    protected volatile boolean mIsFinished;
    protected final UceRequestManager.RequestManagerCallback mRequestManagerCallback;
    protected final int mSubId;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    @interface UceRequestUpdate {
    }

    public abstract void onRequestUpdated(long j, int i);

    static {
        HashMap hashMap = new HashMap();
        REQUEST_EVENT_DESC = hashMap;
        hashMap.put(0, "REQUEST_ERROR");
        REQUEST_EVENT_DESC.put(1, "RETRIEVE_COMMAND_ERROR");
        REQUEST_EVENT_DESC.put(2, "REQUEST_NETWORK_RESPONSE");
        REQUEST_EVENT_DESC.put(6, "REQUEST_TERMINATED");
        REQUEST_EVENT_DESC.put(4, "REQUEST_RESOURCE_TERMINATED");
        REQUEST_EVENT_DESC.put(3, "REQUEST_CAPABILITY_UPDATE");
        REQUEST_EVENT_DESC.put(5, "REQUEST_CACHE_CAP_UPDATE");
        REQUEST_EVENT_DESC.put(7, "NO_NEED_REQUEST");
        REQUEST_EVENT_DESC.put(8, "REMOTE_REQUEST_DONE");
        REQUEST_EVENT_DESC.put(9, "REQUEST_TIMEOUT");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class RequestResult {
        private final Optional<Integer> mErrorCode;
        private final Boolean mIsSuccess;
        private final Optional<Long> mRetryMillis;
        private final Optional<SipDetails> mSipDetails;
        private final Long mTaskId;

        public static RequestResult createSuccessResult(long taskId) {
            return new RequestResult(taskId);
        }

        public static RequestResult createSuccessResult(long taskId, SipDetails details) {
            return new RequestResult(taskId, details);
        }

        public static RequestResult createFailedResult(long taskId, int errorCode, long retry) {
            return new RequestResult(taskId, errorCode, retry);
        }

        public static RequestResult createFailedResult(long taskId, int errorCode, long retry, SipDetails details) {
            return new RequestResult(taskId, errorCode, retry, details);
        }

        private RequestResult(long taskId) {
            this(taskId, null);
        }

        private RequestResult(long taskId, SipDetails details) {
            this.mTaskId = Long.valueOf(taskId);
            this.mIsSuccess = true;
            this.mErrorCode = Optional.empty();
            this.mRetryMillis = Optional.empty();
            if (details == null) {
                this.mSipDetails = Optional.empty();
            } else {
                this.mSipDetails = Optional.ofNullable(details);
            }
        }

        private RequestResult(long taskId, int errorCode, long retryMillis) {
            this(taskId, errorCode, retryMillis, null);
        }

        private RequestResult(long taskId, int errorCode, long retryMillis, SipDetails details) {
            this.mTaskId = Long.valueOf(taskId);
            this.mIsSuccess = false;
            this.mErrorCode = Optional.of(Integer.valueOf(errorCode));
            this.mRetryMillis = Optional.of(Long.valueOf(retryMillis));
            if (details == null) {
                this.mSipDetails = Optional.empty();
            } else {
                this.mSipDetails = Optional.ofNullable(details);
            }
        }

        public long getTaskId() {
            return this.mTaskId.longValue();
        }

        public boolean isRequestSuccess() {
            return this.mIsSuccess.booleanValue();
        }

        public Optional<Integer> getErrorCode() {
            return this.mErrorCode;
        }

        public Optional<Long> getRetryMillis() {
            return this.mRetryMillis;
        }

        public Optional<SipDetails> getSipDetails() {
            return this.mSipDetails;
        }
    }

    public UceRequestCoordinator(int subId, Collection<UceRequest> requests, UceRequestManager.RequestManagerCallback requestMgrCallback) {
        this.mSubId = subId;
        this.mRequestManagerCallback = requestMgrCallback;
        requests.forEach(new Consumer() { // from class: com.android.ims.rcs.uce.request.UceRequestCoordinator$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                UceRequestCoordinator.this.lambda$new$0((UceRequest) obj);
            }
        });
        this.mFinishedRequests = new HashMap();
        this.mActivatedRequests = (Map) requests.stream().collect(Collectors.toMap(new Function() { // from class: com.android.ims.rcs.uce.request.UceRequestCoordinator$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Long.valueOf(((UceRequest) obj).getTaskId());
            }
        }, new Function() { // from class: com.android.ims.rcs.uce.request.UceRequestCoordinator$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return UceRequestCoordinator.lambda$new$1((UceRequest) obj);
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(UceRequest request) {
        request.setRequestCoordinatorId(this.mCoordinatorId);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ UceRequest lambda$new$1(UceRequest request) {
        return request;
    }

    public long getCoordinatorId() {
        return this.mCoordinatorId;
    }

    public List<Long> getActivatedRequestTaskIds() {
        List<Long> list;
        synchronized (this.mCollectionLock) {
            list = (List) this.mActivatedRequests.values().stream().map(new Function() { // from class: com.android.ims.rcs.uce.request.UceRequestCoordinator$$ExternalSyntheticLambda4
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Long valueOf;
                    valueOf = Long.valueOf(((UceRequest) obj).getTaskId());
                    return valueOf;
                }
            }).collect(Collectors.toList());
        }
        return list;
    }

    public UceRequest getUceRequest(Long taskId) {
        UceRequest uceRequest;
        synchronized (this.mCollectionLock) {
            uceRequest = this.mActivatedRequests.get(taskId);
        }
        return uceRequest;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void moveRequestToFinishedCollection(Long taskId, RequestResult requestResult) {
        synchronized (this.mCollectionLock) {
            this.mActivatedRequests.remove(taskId);
            this.mFinishedRequests.put(taskId, requestResult);
            this.mRequestManagerCallback.notifyUceRequestFinished(getCoordinatorId(), taskId.longValue());
        }
    }

    public void onFinish() {
        this.mIsFinished = true;
        synchronized (this.mCollectionLock) {
            this.mActivatedRequests.forEach(new BiConsumer() { // from class: com.android.ims.rcs.uce.request.UceRequestCoordinator$$ExternalSyntheticLambda3
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    Long l = (Long) obj;
                    ((UceRequest) obj2).onFinish();
                }
            });
            this.mActivatedRequests.clear();
            this.mFinishedRequests.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void logd(String log) {
        Log.d(LOG_TAG, getLogPrefix().append(log).toString());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void logw(String log) {
        Log.w(LOG_TAG, getLogPrefix().append(log).toString());
    }

    private StringBuilder getLogPrefix() {
        StringBuilder builder = new StringBuilder("[");
        builder.append(this.mSubId).append("][coordId=").append(this.mCoordinatorId).append("] ");
        return builder;
    }
}
