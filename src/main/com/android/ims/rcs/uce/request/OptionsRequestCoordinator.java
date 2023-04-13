package com.android.ims.rcs.uce.request;

import android.os.RemoteException;
import android.telephony.ims.RcsContactUceCapability;
import android.telephony.ims.SipDetails;
import android.telephony.ims.aidl.IRcsUceControllerCallback;
import com.android.ims.rcs.uce.UceStatsWriter;
import com.android.ims.rcs.uce.request.UceRequestCoordinator;
import com.android.ims.rcs.uce.request.UceRequestManager;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
/* loaded from: classes.dex */
public class OptionsRequestCoordinator extends UceRequestCoordinator {
    private IRcsUceControllerCallback mCapabilitiesCallback;
    private final UceStatsWriter mUceStatsWriter;
    private static final RequestResultCreator sRequestErrorCreator = new RequestResultCreator() { // from class: com.android.ims.rcs.uce.request.OptionsRequestCoordinator$$ExternalSyntheticLambda0
        @Override // com.android.ims.rcs.uce.request.OptionsRequestCoordinator.RequestResultCreator
        public final UceRequestCoordinator.RequestResult createRequestResult(long j, CapabilityRequestResponse capabilityRequestResponse) {
            return OptionsRequestCoordinator.lambda$static$0(j, capabilityRequestResponse);
        }
    };
    private static final RequestResultCreator sCommandErrorCreator = new RequestResultCreator() { // from class: com.android.ims.rcs.uce.request.OptionsRequestCoordinator$$ExternalSyntheticLambda1
        @Override // com.android.ims.rcs.uce.request.OptionsRequestCoordinator.RequestResultCreator
        public final UceRequestCoordinator.RequestResult createRequestResult(long j, CapabilityRequestResponse capabilityRequestResponse) {
            return OptionsRequestCoordinator.lambda$static$1(j, capabilityRequestResponse);
        }
    };
    private static final RequestResultCreator sNetworkRespCreator = new RequestResultCreator() { // from class: com.android.ims.rcs.uce.request.OptionsRequestCoordinator$$ExternalSyntheticLambda2
        @Override // com.android.ims.rcs.uce.request.OptionsRequestCoordinator.RequestResultCreator
        public final UceRequestCoordinator.RequestResult createRequestResult(long j, CapabilityRequestResponse capabilityRequestResponse) {
            return OptionsRequestCoordinator.lambda$static$2(j, capabilityRequestResponse);
        }
    };
    private static final RequestResultCreator sNotNeedRequestFromNetworkCreator = new RequestResultCreator() { // from class: com.android.ims.rcs.uce.request.OptionsRequestCoordinator$$ExternalSyntheticLambda3
        @Override // com.android.ims.rcs.uce.request.OptionsRequestCoordinator.RequestResultCreator
        public final UceRequestCoordinator.RequestResult createRequestResult(long j, CapabilityRequestResponse capabilityRequestResponse) {
            UceRequestCoordinator.RequestResult createSuccessResult;
            createSuccessResult = UceRequestCoordinator.RequestResult.createSuccessResult(j);
            return createSuccessResult;
        }
    };
    private static final RequestResultCreator sRequestTimeoutCreator = new RequestResultCreator() { // from class: com.android.ims.rcs.uce.request.OptionsRequestCoordinator$$ExternalSyntheticLambda4
        @Override // com.android.ims.rcs.uce.request.OptionsRequestCoordinator.RequestResultCreator
        public final UceRequestCoordinator.RequestResult createRequestResult(long j, CapabilityRequestResponse capabilityRequestResponse) {
            UceRequestCoordinator.RequestResult createFailedResult;
            createFailedResult = UceRequestCoordinator.RequestResult.createFailedResult(j, 9, 0L);
            return createFailedResult;
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    @FunctionalInterface
    /* loaded from: classes.dex */
    public interface RequestResultCreator {
        UceRequestCoordinator.RequestResult createRequestResult(long j, CapabilityRequestResponse capabilityRequestResponse);
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private OptionsRequestCoordinator mRequestCoordinator;

        public Builder(int subId, Collection<UceRequest> requests, UceRequestManager.RequestManagerCallback callback) {
            this.mRequestCoordinator = new OptionsRequestCoordinator(subId, requests, callback, UceStatsWriter.getInstance());
        }

        public Builder(int subId, Collection<UceRequest> requests, UceRequestManager.RequestManagerCallback callback, UceStatsWriter instance) {
            this.mRequestCoordinator = new OptionsRequestCoordinator(subId, requests, callback, instance);
        }

        public Builder setCapabilitiesCallback(IRcsUceControllerCallback callback) {
            this.mRequestCoordinator.setCapabilitiesCallback(callback);
            return this;
        }

        public OptionsRequestCoordinator build() {
            return this.mRequestCoordinator;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ UceRequestCoordinator.RequestResult lambda$static$0(long taskId, CapabilityRequestResponse response) {
        int errorCode = response.getRequestInternalError().orElse(1).intValue();
        long retryAfter = response.getRetryAfterMillis();
        return UceRequestCoordinator.RequestResult.createFailedResult(taskId, errorCode, retryAfter);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ UceRequestCoordinator.RequestResult lambda$static$1(long taskId, CapabilityRequestResponse response) {
        int cmdError = response.getCommandError().orElse(1).intValue();
        int errorCode = CapabilityRequestResponse.getCapabilityErrorFromCommandError(cmdError);
        long retryAfter = response.getRetryAfterMillis();
        return UceRequestCoordinator.RequestResult.createFailedResult(taskId, errorCode, retryAfter);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ UceRequestCoordinator.RequestResult lambda$static$2(long taskId, CapabilityRequestResponse response) {
        if (response.isNetworkResponseOK()) {
            return UceRequestCoordinator.RequestResult.createSuccessResult(taskId);
        }
        int errorCode = CapabilityRequestResponse.getCapabilityErrorFromSipCode(response);
        long retryAfter = response.getRetryAfterMillis();
        return UceRequestCoordinator.RequestResult.createFailedResult(taskId, errorCode, retryAfter);
    }

    private OptionsRequestCoordinator(int subId, Collection<UceRequest> requests, UceRequestManager.RequestManagerCallback requestMgrCallback, UceStatsWriter instance) {
        super(subId, requests, requestMgrCallback);
        this.mUceStatsWriter = instance;
        logd("OptionsRequestCoordinator: created");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCapabilitiesCallback(IRcsUceControllerCallback callback) {
        this.mCapabilitiesCallback = callback;
    }

    @Override // com.android.ims.rcs.uce.request.UceRequestCoordinator
    public void onFinish() {
        logd("OptionsRequestCoordinator: onFinish");
        this.mCapabilitiesCallback = null;
        super.onFinish();
    }

    @Override // com.android.ims.rcs.uce.request.UceRequestCoordinator
    public void onRequestUpdated(long taskId, int event) {
        if (this.mIsFinished) {
            return;
        }
        OptionsRequest request = (OptionsRequest) getUceRequest(Long.valueOf(taskId));
        if (request == null) {
            logw("onRequestUpdated: Cannot find OptionsRequest taskId=" + taskId);
            return;
        }
        logd("onRequestUpdated(OptionsRequest): taskId=" + taskId + ", event=" + REQUEST_EVENT_DESC.get(Integer.valueOf(event)));
        switch (event) {
            case 0:
                handleRequestError(request);
                break;
            case 1:
                handleCommandError(request);
                break;
            case 2:
                handleNetworkResponse(request);
                break;
            case 3:
            case 4:
            case 6:
            case 8:
            default:
                logw("onRequestUpdated(OptionsRequest): invalid event " + event);
                break;
            case 5:
                handleCachedCapabilityUpdated(request);
                break;
            case 7:
                handleNoNeedRequestFromNetwork(request);
                break;
            case 9:
                handleRequestTimeout(request);
                break;
        }
        checkAndFinishRequestCoordinator();
    }

    private void handleRequestError(OptionsRequest request) {
        CapabilityRequestResponse response = request.getRequestResponse();
        logd("handleRequestError: " + request.toString());
        request.onFinish();
        Long taskId = Long.valueOf(request.getTaskId());
        UceRequestCoordinator.RequestResult requestResult = sRequestErrorCreator.createRequestResult(taskId.longValue(), response);
        moveRequestToFinishedCollection(taskId, requestResult);
    }

    private void handleCommandError(OptionsRequest request) {
        CapabilityRequestResponse response = request.getRequestResponse();
        logd("handleCommandError: " + request.toString());
        request.onFinish();
        int commandErrorCode = response.getCommandError().orElse(0).intValue();
        this.mUceStatsWriter.setUceEvent(this.mSubId, 3, false, commandErrorCode, 0);
        Long taskId = Long.valueOf(request.getTaskId());
        UceRequestCoordinator.RequestResult requestResult = sCommandErrorCreator.createRequestResult(taskId.longValue(), response);
        moveRequestToFinishedCollection(taskId, requestResult);
    }

    private void handleNetworkResponse(OptionsRequest request) {
        CapabilityRequestResponse response = request.getRequestResponse();
        logd("handleNetworkResponse: " + response.toString());
        int responseCode = response.getNetworkRespSipCode().orElse(0).intValue();
        this.mUceStatsWriter.setUceEvent(this.mSubId, 3, true, 0, responseCode);
        List<RcsContactUceCapability> updatedCapList = response.getUpdatedContactCapability();
        if (!updatedCapList.isEmpty()) {
            this.mRequestManagerCallback.saveCapabilities(updatedCapList);
            triggerCapabilitiesReceivedCallback(updatedCapList);
            response.removeUpdatedCapabilities(updatedCapList);
        }
        request.onFinish();
        Long taskId = Long.valueOf(request.getTaskId());
        UceRequestCoordinator.RequestResult requestResult = sNetworkRespCreator.createRequestResult(taskId.longValue(), response);
        moveRequestToFinishedCollection(taskId, requestResult);
    }

    private void handleCachedCapabilityUpdated(OptionsRequest request) {
        CapabilityRequestResponse response = request.getRequestResponse();
        Long taskId = Long.valueOf(request.getTaskId());
        List<RcsContactUceCapability> cachedCapList = response.getCachedContactCapability();
        logd("handleCachedCapabilityUpdated: taskId=" + taskId + ", CapRequestResp=" + response);
        if (cachedCapList.isEmpty()) {
            return;
        }
        triggerCapabilitiesReceivedCallback(cachedCapList);
        response.removeCachedContactCapabilities();
    }

    private void handleNoNeedRequestFromNetwork(OptionsRequest request) {
        CapabilityRequestResponse response = request.getRequestResponse();
        logd("handleNoNeedRequestFromNetwork: " + response.toString());
        request.onFinish();
        long taskId = request.getTaskId();
        UceRequestCoordinator.RequestResult requestResult = sNotNeedRequestFromNetworkCreator.createRequestResult(taskId, response);
        moveRequestToFinishedCollection(Long.valueOf(taskId), requestResult);
    }

    private void handleRequestTimeout(OptionsRequest request) {
        CapabilityRequestResponse response = request.getRequestResponse();
        logd("handleRequestTimeout: " + response.toString());
        request.onFinish();
        long taskId = request.getTaskId();
        UceRequestCoordinator.RequestResult requestResult = sRequestTimeoutCreator.createRequestResult(taskId, response);
        moveRequestToFinishedCollection(Long.valueOf(taskId), requestResult);
    }

    private void triggerCapabilitiesReceivedCallback(List<RcsContactUceCapability> capList) {
        try {
            try {
                logd("triggerCapabilitiesCallback: size=" + capList.size());
                this.mCapabilitiesCallback.onCapabilitiesReceived(capList);
            } catch (RemoteException e) {
                logw("triggerCapabilitiesCallback exception: " + e);
            }
        } finally {
            logd("triggerCapabilitiesCallback: done");
        }
    }

    private void triggerCompletedCallback() {
        try {
            try {
                logd("triggerCompletedCallback");
                this.mCapabilitiesCallback.onComplete((SipDetails) null);
            } catch (RemoteException e) {
                logw("triggerCompletedCallback exception: " + e);
            }
        } finally {
            logd("triggerCompletedCallback: done");
        }
    }

    private void triggerErrorCallback(int errorCode, long retryAfterMillis) {
        try {
            try {
                logd("triggerErrorCallback: errorCode=" + errorCode + ", retry=" + retryAfterMillis);
                this.mCapabilitiesCallback.onError(errorCode, retryAfterMillis, (SipDetails) null);
            } catch (RemoteException e) {
                logw("triggerErrorCallback exception: " + e);
            }
        } finally {
            logd("triggerErrorCallback: done");
        }
    }

    private void checkAndFinishRequestCoordinator() {
        synchronized (this.mCollectionLock) {
            if (this.mActivatedRequests.isEmpty()) {
                Optional<UceRequestCoordinator.RequestResult> optRequestResult = this.mFinishedRequests.values().stream().filter(new Predicate() { // from class: com.android.ims.rcs.uce.request.OptionsRequestCoordinator$$ExternalSyntheticLambda5
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        return OptionsRequestCoordinator.lambda$checkAndFinishRequestCoordinator$5((UceRequestCoordinator.RequestResult) obj);
                    }
                }).max(Comparator.comparingLong(new ToLongFunction() { // from class: com.android.ims.rcs.uce.request.OptionsRequestCoordinator$$ExternalSyntheticLambda6
                    @Override // java.util.function.ToLongFunction
                    public final long applyAsLong(Object obj) {
                        long longValue;
                        longValue = ((UceRequestCoordinator.RequestResult) obj).getRetryMillis().orElse(-1L).longValue();
                        return longValue;
                    }
                }));
                if (optRequestResult.isPresent()) {
                    UceRequestCoordinator.RequestResult result = optRequestResult.get();
                    int errorCode = result.getErrorCode().orElse(1).intValue();
                    long retryAfter = result.getRetryMillis().orElse(0L).longValue();
                    triggerErrorCallback(errorCode, retryAfter);
                } else {
                    triggerCompletedCallback();
                }
                this.mRequestManagerCallback.notifyRequestCoordinatorFinished(this.mCoordinatorId);
                logd("checkAndFinishRequestCoordinator(OptionsRequest) done, id=" + this.mCoordinatorId);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$checkAndFinishRequestCoordinator$5(UceRequestCoordinator.RequestResult result) {
        return !result.isRequestSuccess();
    }

    public Collection<UceRequest> getActivatedRequest() {
        return this.mActivatedRequests.values();
    }

    public Collection<UceRequestCoordinator.RequestResult> getFinishedRequest() {
        return this.mFinishedRequests.values();
    }
}
