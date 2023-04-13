package com.android.ims.rcs.uce.request;

import android.net.Uri;
import android.os.RemoteException;
import android.telephony.ims.RcsContactUceCapability;
import android.telephony.ims.SipDetails;
import android.telephony.ims.aidl.IRcsUceControllerCallback;
import com.android.ims.rcs.uce.UceDeviceState;
import com.android.ims.rcs.uce.UceStatsWriter;
import com.android.ims.rcs.uce.eab.EabCapabilityResult;
import com.android.ims.rcs.uce.presence.pidfparser.PidfParserUtils;
import com.android.ims.rcs.uce.request.SubscriptionTerminatedHelper;
import com.android.ims.rcs.uce.request.UceRequestCoordinator;
import com.android.ims.rcs.uce.request.UceRequestManager;
import com.android.ims.rcs.uce.util.NetworkSipCode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class SubscribeRequestCoordinator extends UceRequestCoordinator {
    private volatile IRcsUceControllerCallback mCapabilitiesCallback;
    private final UceStatsWriter mUceStatsWriter;
    private static final RequestResultCreator sRequestErrorCreator = new RequestResultCreator() { // from class: com.android.ims.rcs.uce.request.SubscribeRequestCoordinator$$ExternalSyntheticLambda4
        @Override // com.android.ims.rcs.uce.request.SubscribeRequestCoordinator.RequestResultCreator
        public final UceRequestCoordinator.RequestResult createRequestResult(long j, CapabilityRequestResponse capabilityRequestResponse, UceRequestManager.RequestManagerCallback requestManagerCallback) {
            return SubscribeRequestCoordinator.lambda$static$0(j, capabilityRequestResponse, requestManagerCallback);
        }
    };
    private static final RequestResultCreator sCommandErrorCreator = new RequestResultCreator() { // from class: com.android.ims.rcs.uce.request.SubscribeRequestCoordinator$$ExternalSyntheticLambda5
        @Override // com.android.ims.rcs.uce.request.SubscribeRequestCoordinator.RequestResultCreator
        public final UceRequestCoordinator.RequestResult createRequestResult(long j, CapabilityRequestResponse capabilityRequestResponse, UceRequestManager.RequestManagerCallback requestManagerCallback) {
            return SubscribeRequestCoordinator.lambda$static$1(j, capabilityRequestResponse, requestManagerCallback);
        }
    };
    private static final RequestResultCreator sNetworkRespErrorCreator = new RequestResultCreator() { // from class: com.android.ims.rcs.uce.request.SubscribeRequestCoordinator$$ExternalSyntheticLambda6
        @Override // com.android.ims.rcs.uce.request.SubscribeRequestCoordinator.RequestResultCreator
        public final UceRequestCoordinator.RequestResult createRequestResult(long j, CapabilityRequestResponse capabilityRequestResponse, UceRequestManager.RequestManagerCallback requestManagerCallback) {
            return SubscribeRequestCoordinator.lambda$static$2(j, capabilityRequestResponse, requestManagerCallback);
        }
    };
    private static final RequestResultCreator sNetworkRespSuccessfulCreator = new RequestResultCreator() { // from class: com.android.ims.rcs.uce.request.SubscribeRequestCoordinator$$ExternalSyntheticLambda7
        @Override // com.android.ims.rcs.uce.request.SubscribeRequestCoordinator.RequestResultCreator
        public final UceRequestCoordinator.RequestResult createRequestResult(long j, CapabilityRequestResponse capabilityRequestResponse, UceRequestManager.RequestManagerCallback requestManagerCallback) {
            return capabilityRequestResponse.getSipDetails().orElse(null);
        }
    };
    private static final RequestResultCreator sTerminatedCreator = new RequestResultCreator() { // from class: com.android.ims.rcs.uce.request.SubscribeRequestCoordinator$$ExternalSyntheticLambda8
        @Override // com.android.ims.rcs.uce.request.SubscribeRequestCoordinator.RequestResultCreator
        public final UceRequestCoordinator.RequestResult createRequestResult(long j, CapabilityRequestResponse capabilityRequestResponse, UceRequestManager.RequestManagerCallback requestManagerCallback) {
            return SubscribeRequestCoordinator.lambda$static$4(j, capabilityRequestResponse, requestManagerCallback);
        }
    };
    private static final RequestResultCreator sNotNeedRequestFromNetworkCreator = new RequestResultCreator() { // from class: com.android.ims.rcs.uce.request.SubscribeRequestCoordinator$$ExternalSyntheticLambda9
        @Override // com.android.ims.rcs.uce.request.SubscribeRequestCoordinator.RequestResultCreator
        public final UceRequestCoordinator.RequestResult createRequestResult(long j, CapabilityRequestResponse capabilityRequestResponse, UceRequestManager.RequestManagerCallback requestManagerCallback) {
            UceRequestCoordinator.RequestResult createSuccessResult;
            createSuccessResult = UceRequestCoordinator.RequestResult.createSuccessResult(j);
            return createSuccessResult;
        }
    };
    private static final RequestResultCreator sRequestTimeoutCreator = new RequestResultCreator() { // from class: com.android.ims.rcs.uce.request.SubscribeRequestCoordinator$$ExternalSyntheticLambda10
        @Override // com.android.ims.rcs.uce.request.SubscribeRequestCoordinator.RequestResultCreator
        public final UceRequestCoordinator.RequestResult createRequestResult(long j, CapabilityRequestResponse capabilityRequestResponse, UceRequestManager.RequestManagerCallback requestManagerCallback) {
            UceRequestCoordinator.RequestResult createFailedResult;
            createFailedResult = UceRequestCoordinator.RequestResult.createFailedResult(j, 9, 0L);
            return createFailedResult;
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    @FunctionalInterface
    /* loaded from: classes.dex */
    public interface RequestResultCreator {
        UceRequestCoordinator.RequestResult createRequestResult(long j, CapabilityRequestResponse capabilityRequestResponse, UceRequestManager.RequestManagerCallback requestManagerCallback);
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private SubscribeRequestCoordinator mRequestCoordinator;

        public Builder(int subId, Collection<UceRequest> requests, UceRequestManager.RequestManagerCallback c) {
            this.mRequestCoordinator = new SubscribeRequestCoordinator(subId, requests, c, UceStatsWriter.getInstance());
        }

        public Builder(int subId, Collection<UceRequest> requests, UceRequestManager.RequestManagerCallback c, UceStatsWriter instance) {
            this.mRequestCoordinator = new SubscribeRequestCoordinator(subId, requests, c, instance);
        }

        public Builder setCapabilitiesCallback(IRcsUceControllerCallback callback) {
            this.mRequestCoordinator.setCapabilitiesCallback(callback);
            return this;
        }

        public SubscribeRequestCoordinator build() {
            return this.mRequestCoordinator;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ UceRequestCoordinator.RequestResult lambda$static$0(long taskId, CapabilityRequestResponse response, UceRequestManager.RequestManagerCallback requestMgrCallback) {
        int errorCode = response.getRequestInternalError().orElse(1).intValue();
        long retryAfter = response.getRetryAfterMillis();
        return UceRequestCoordinator.RequestResult.createFailedResult(taskId, errorCode, retryAfter);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ UceRequestCoordinator.RequestResult lambda$static$1(long taskId, CapabilityRequestResponse response, UceRequestManager.RequestManagerCallback requestMgrCallback) {
        int cmdError = response.getCommandError().orElse(1).intValue();
        int errorCode = CapabilityRequestResponse.getCapabilityErrorFromCommandError(cmdError);
        long retryAfter = response.getRetryAfterMillis();
        return UceRequestCoordinator.RequestResult.createFailedResult(taskId, errorCode, retryAfter);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ UceRequestCoordinator.RequestResult lambda$static$2(long taskId, CapabilityRequestResponse response, UceRequestManager.RequestManagerCallback requestMgrCallback) {
        UceDeviceState.DeviceStateResult deviceState = requestMgrCallback.getDeviceState();
        SipDetails details = response.getSipDetails().orElse(null);
        if (deviceState.isRequestForbidden()) {
            int errorCode = deviceState.getErrorCode().orElse(6).intValue();
            long retryAfter = deviceState.getRequestRetryAfterMillis();
            return UceRequestCoordinator.RequestResult.createFailedResult(taskId, errorCode, retryAfter, details);
        }
        int errorCode2 = CapabilityRequestResponse.getCapabilityErrorFromSipCode(response);
        long retryAfter2 = response.getRetryAfterMillis();
        return UceRequestCoordinator.RequestResult.createFailedResult(taskId, errorCode2, retryAfter2, details);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ UceRequestCoordinator.RequestResult lambda$static$4(long taskId, CapabilityRequestResponse response, UceRequestManager.RequestManagerCallback requestMgrCallback) {
        SubscriptionTerminatedHelper.TerminatedResult terminatedResult = SubscriptionTerminatedHelper.getAnalysisResult(response.getTerminatedReason(), response.getRetryAfterMillis(), response.haveAllRequestCapsUpdatedBeenReceived());
        SipDetails details = response.getSipDetails().orElse(null);
        if (terminatedResult.getErrorCode().isPresent()) {
            int errorCode = terminatedResult.getErrorCode().get().intValue();
            long terminatedRetry = terminatedResult.getRetryAfterMillis();
            return UceRequestCoordinator.RequestResult.createFailedResult(taskId, errorCode, terminatedRetry);
        } else if (!response.isNetworkResponseOK() || response.getRetryAfterMillis() > 0) {
            long retryAfterMillis = response.getRetryAfterMillis();
            int errorCode2 = CapabilityRequestResponse.getCapabilityErrorFromSipCode(response);
            return UceRequestCoordinator.RequestResult.createFailedResult(taskId, errorCode2, retryAfterMillis, details);
        } else {
            return UceRequestCoordinator.RequestResult.createSuccessResult(taskId, details);
        }
    }

    private SubscribeRequestCoordinator(int subId, Collection<UceRequest> requests, UceRequestManager.RequestManagerCallback requestMgrCallback, UceStatsWriter instance) {
        super(subId, requests, requestMgrCallback);
        this.mUceStatsWriter = instance;
        logd("SubscribeRequestCoordinator: created");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCapabilitiesCallback(IRcsUceControllerCallback callback) {
        this.mCapabilitiesCallback = callback;
    }

    @Override // com.android.ims.rcs.uce.request.UceRequestCoordinator
    public void onFinish() {
        logd("SubscribeRequestCoordinator: onFinish");
        this.mCapabilitiesCallback = null;
        super.onFinish();
    }

    @Override // com.android.ims.rcs.uce.request.UceRequestCoordinator
    public void onRequestUpdated(long taskId, int event) {
        if (this.mIsFinished) {
            return;
        }
        SubscribeRequest request = (SubscribeRequest) getUceRequest(Long.valueOf(taskId));
        if (request == null) {
            logw("onRequestUpdated: Cannot find SubscribeRequest taskId=" + taskId);
            return;
        }
        logd("onRequestUpdated(SubscribeRequest): taskId=" + taskId + ", event=" + REQUEST_EVENT_DESC.get(Integer.valueOf(event)));
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
                handleCapabilitiesUpdated(request);
                break;
            case 4:
                handleResourceTerminated(request);
                break;
            case 5:
                handleCachedCapabilityUpdated(request);
                break;
            case 6:
                handleTerminated(request);
                break;
            case 7:
                handleNoNeedRequestFromNetwork(request);
                break;
            case 8:
            default:
                logw("onRequestUpdated(SubscribeRequest): invalid event " + event);
                break;
            case 9:
                handleRequestTimeout(request);
                break;
        }
        checkAndFinishRequestCoordinator();
    }

    private void handleRequestError(SubscribeRequest request) {
        CapabilityRequestResponse response = request.getRequestResponse();
        logd("handleRequestError: " + request.toString());
        request.onFinish();
        Long taskId = Long.valueOf(request.getTaskId());
        UceRequestCoordinator.RequestResult requestResult = sRequestErrorCreator.createRequestResult(taskId.longValue(), response, this.mRequestManagerCallback);
        moveRequestToFinishedCollection(taskId, requestResult);
    }

    private void handleCommandError(SubscribeRequest request) {
        CapabilityRequestResponse response = request.getRequestResponse();
        logd("handleCommandError: " + request.toString());
        request.onFinish();
        int commandErrorCode = response.getCommandError().orElse(0).intValue();
        this.mUceStatsWriter.setUceEvent(this.mSubId, 1, false, commandErrorCode, 0);
        Long taskId = Long.valueOf(request.getTaskId());
        UceRequestCoordinator.RequestResult requestResult = sCommandErrorCreator.createRequestResult(taskId.longValue(), response, this.mRequestManagerCallback);
        moveRequestToFinishedCollection(taskId, requestResult);
    }

    private void handleNetworkResponse(SubscribeRequest request) {
        final CapabilityRequestResponse response = request.getRequestResponse();
        logd("handleNetworkResponse: " + response.toString());
        int respCode = response.getNetworkRespSipCode().orElse(0).intValue();
        this.mUceStatsWriter.setSubscribeResponse(this.mSubId, request.getTaskId(), respCode);
        response.getResponseSipCode().ifPresent(new Consumer() { // from class: com.android.ims.rcs.uce.request.SubscribeRequestCoordinator$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                SubscribeRequestCoordinator.this.lambda$handleNetworkResponse$7(response, (Integer) obj);
            }
        });
        if (!response.isNetworkResponseOK()) {
            UceRequestCoordinator.RequestResult requestResult = handleNetworkResponseFailed(request);
            List<RcsContactUceCapability> updatedCapList = response.getUpdatedContactCapability();
            if (!updatedCapList.isEmpty()) {
                if (response.isNotFound()) {
                    this.mRequestManagerCallback.saveCapabilities(updatedCapList);
                }
                triggerCapabilitiesReceivedCallback(updatedCapList);
                response.removeUpdatedCapabilities(updatedCapList);
            }
            request.onFinish();
            moveRequestToFinishedCollection(Long.valueOf(request.getTaskId()), requestResult);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleNetworkResponse$7(CapabilityRequestResponse response, Integer sipCode) {
        String reason = response.getResponseReason().orElse("");
        this.mRequestManagerCallback.refreshDeviceState(sipCode.intValue(), reason);
    }

    private UceRequestCoordinator.RequestResult handleNetworkResponseFailed(SubscribeRequest request) {
        long taskId = request.getTaskId();
        CapabilityRequestResponse response = request.getRequestResponse();
        List<Uri> requestUris = response.getNotReceiveCapabilityUpdatedContact();
        if (response.isNotFound()) {
            List<RcsContactUceCapability> capabilityList = (List) requestUris.stream().map(new Function() { // from class: com.android.ims.rcs.uce.request.SubscribeRequestCoordinator$$ExternalSyntheticLambda11
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    RcsContactUceCapability notFoundContactCapabilities;
                    notFoundContactCapabilities = PidfParserUtils.getNotFoundContactCapabilities((Uri) obj);
                    return notFoundContactCapabilities;
                }
            }).collect(Collectors.toList());
            response.addUpdatedCapabilities(capabilityList);
            UceRequestCoordinator.RequestResult requestResult = sNetworkRespSuccessfulCreator.createRequestResult(taskId, response, this.mRequestManagerCallback);
            return requestResult;
        }
        List<RcsContactUceCapability> capabilitiesList = getCapabilitiesFromCacheIncludingExpired(requestUris);
        response.addUpdatedCapabilities(capabilitiesList);
        this.mRequestManagerCallback.addToThrottlingList(requestUris, response.getResponseSipCode().orElse(Integer.valueOf((int) NetworkSipCode.SIP_CODE_REQUEST_TIMEOUT)).intValue());
        UceRequestCoordinator.RequestResult requestResult2 = sNetworkRespErrorCreator.createRequestResult(taskId, response, this.mRequestManagerCallback);
        return requestResult2;
    }

    private List<RcsContactUceCapability> getCapabilitiesFromCacheIncludingExpired(List<Uri> uris) {
        final List<RcsContactUceCapability> resultList = new ArrayList<>();
        final List<RcsContactUceCapability> notFoundFromCacheList = new ArrayList<>();
        List<EabCapabilityResult> eabResultList = this.mRequestManagerCallback.getCapabilitiesFromCacheIncludingExpired(uris);
        eabResultList.forEach(new Consumer() { // from class: com.android.ims.rcs.uce.request.SubscribeRequestCoordinator$$ExternalSyntheticLambda12
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                SubscribeRequestCoordinator.lambda$getCapabilitiesFromCacheIncludingExpired$9(resultList, notFoundFromCacheList, (EabCapabilityResult) obj);
            }
        });
        if (!notFoundFromCacheList.isEmpty()) {
            resultList.addAll(notFoundFromCacheList);
        }
        logd("getCapabilitiesFromCacheIncludingExpired: requesting uris size=" + uris.size() + ", capabilities not found from cache size=" + notFoundFromCacheList.size());
        return resultList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$getCapabilitiesFromCacheIncludingExpired$9(List resultList, List notFoundFromCacheList, EabCapabilityResult eabResult) {
        if (eabResult.getStatus() == 0 || eabResult.getStatus() == 2) {
            resultList.add(eabResult.getContactCapabilities());
        } else {
            notFoundFromCacheList.add(PidfParserUtils.getNotFoundContactCapabilities(eabResult.getContact()));
        }
    }

    private void handleCapabilitiesUpdated(SubscribeRequest request) {
        CapabilityRequestResponse response = request.getRequestResponse();
        Long taskId = Long.valueOf(request.getTaskId());
        List<RcsContactUceCapability> updatedCapList = response.getUpdatedContactCapability();
        logd("handleCapabilitiesUpdated: taskId=" + taskId + ", size=" + updatedCapList.size());
        if (updatedCapList.isEmpty()) {
            return;
        }
        this.mUceStatsWriter.setPresenceNotifyEvent(this.mSubId, taskId.longValue(), updatedCapList);
        this.mRequestManagerCallback.saveCapabilities(updatedCapList);
        triggerCapabilitiesReceivedCallback(updatedCapList);
        response.removeUpdatedCapabilities(updatedCapList);
    }

    private void handleResourceTerminated(SubscribeRequest request) {
        CapabilityRequestResponse response = request.getRequestResponse();
        Long taskId = Long.valueOf(request.getTaskId());
        List<RcsContactUceCapability> terminatedResources = response.getTerminatedResources();
        logd("handleResourceTerminated: taskId=" + taskId + ", size=" + terminatedResources.size());
        if (terminatedResources.isEmpty()) {
            return;
        }
        this.mUceStatsWriter.setPresenceNotifyEvent(this.mSubId, taskId.longValue(), terminatedResources);
        this.mRequestManagerCallback.saveCapabilities(terminatedResources);
        triggerCapabilitiesReceivedCallback(terminatedResources);
        response.removeTerminatedResources(terminatedResources);
    }

    private void handleCachedCapabilityUpdated(SubscribeRequest request) {
        CapabilityRequestResponse response = request.getRequestResponse();
        Long taskId = Long.valueOf(request.getTaskId());
        List<RcsContactUceCapability> cachedCapList = response.getCachedContactCapability();
        logd("handleCachedCapabilityUpdated: taskId=" + taskId + ", size=" + cachedCapList.size());
        if (cachedCapList.isEmpty()) {
            return;
        }
        triggerCapabilitiesReceivedCallback(cachedCapList);
        response.removeCachedContactCapabilities();
    }

    private void handleTerminated(SubscribeRequest request) {
        CapabilityRequestResponse response = request.getRequestResponse();
        logd("handleTerminated: " + response.toString());
        request.onFinish();
        Long taskId = Long.valueOf(request.getTaskId());
        this.mUceStatsWriter.setSubscribeTerminated(this.mSubId, taskId.longValue(), response.getTerminatedReason());
        UceRequestCoordinator.RequestResult requestResult = sTerminatedCreator.createRequestResult(taskId.longValue(), response, this.mRequestManagerCallback);
        moveRequestToFinishedCollection(taskId, requestResult);
    }

    private void handleNoNeedRequestFromNetwork(SubscribeRequest request) {
        CapabilityRequestResponse response = request.getRequestResponse();
        logd("handleNoNeedRequestFromNetwork: " + response.toString());
        request.onFinish();
        long taskId = request.getTaskId();
        UceRequestCoordinator.RequestResult requestResult = sNotNeedRequestFromNetworkCreator.createRequestResult(taskId, response, this.mRequestManagerCallback);
        moveRequestToFinishedCollection(Long.valueOf(taskId), requestResult);
    }

    private void handleRequestTimeout(SubscribeRequest request) {
        CapabilityRequestResponse response = request.getRequestResponse();
        List<Uri> requestUris = response.getNotReceiveCapabilityUpdatedContact();
        logd("handleRequestTimeout: " + response);
        logd("handleRequestTimeout: not received updated uri size=" + requestUris.size());
        this.mRequestManagerCallback.addToThrottlingList(requestUris, NetworkSipCode.SIP_CODE_REQUEST_TIMEOUT);
        List<RcsContactUceCapability> capabilitiesList = getCapabilitiesFromCacheIncludingExpired(requestUris);
        response.addUpdatedCapabilities(capabilitiesList);
        List<RcsContactUceCapability> updatedCapList = response.getUpdatedContactCapability();
        if (!updatedCapList.isEmpty()) {
            triggerCapabilitiesReceivedCallback(updatedCapList);
            response.removeUpdatedCapabilities(updatedCapList);
        }
        long taskId = request.getTaskId();
        UceRequestCoordinator.RequestResult requestResult = sRequestTimeoutCreator.createRequestResult(taskId, response, this.mRequestManagerCallback);
        request.onFinish();
        moveRequestToFinishedCollection(Long.valueOf(taskId), requestResult);
    }

    private void checkAndFinishRequestCoordinator() {
        synchronized (this.mCollectionLock) {
            if (this.mActivatedRequests.isEmpty()) {
                Optional<UceRequestCoordinator.RequestResult> optRequestResult = this.mFinishedRequests.values().stream().filter(new Predicate() { // from class: com.android.ims.rcs.uce.request.SubscribeRequestCoordinator$$ExternalSyntheticLambda0
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        return SubscribeRequestCoordinator.lambda$checkAndFinishRequestCoordinator$10((UceRequestCoordinator.RequestResult) obj);
                    }
                }).max(Comparator.comparingLong(new ToLongFunction() { // from class: com.android.ims.rcs.uce.request.SubscribeRequestCoordinator$$ExternalSyntheticLambda1
                    @Override // java.util.function.ToLongFunction
                    public final long applyAsLong(Object obj) {
                        long longValue;
                        longValue = ((UceRequestCoordinator.RequestResult) obj).getRetryMillis().orElse(-1L).longValue();
                        return longValue;
                    }
                }));
                Optional<UceRequestCoordinator.RequestResult> optDebugInfoResult = this.mFinishedRequests.values().stream().filter(new Predicate() { // from class: com.android.ims.rcs.uce.request.SubscribeRequestCoordinator$$ExternalSyntheticLambda2
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        return SubscribeRequestCoordinator.lambda$checkAndFinishRequestCoordinator$12((UceRequestCoordinator.RequestResult) obj);
                    }
                }).findFirst();
                SipDetails details = null;
                if (optDebugInfoResult.isPresent()) {
                    details = optDebugInfoResult.get().getSipDetails().orElse(null);
                }
                if (optRequestResult.isPresent()) {
                    UceRequestCoordinator.RequestResult result = optRequestResult.get();
                    int errorCode = result.getErrorCode().orElse(1).intValue();
                    long retryAfter = result.getRetryMillis().orElse(0L).longValue();
                    triggerErrorCallback(errorCode, retryAfter, details);
                } else {
                    triggerCompletedCallback(details);
                }
                this.mRequestManagerCallback.notifyRequestCoordinatorFinished(this.mCoordinatorId);
                logd("checkAndFinishRequestCoordinator(SubscribeRequest) done, id=" + this.mCoordinatorId);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$checkAndFinishRequestCoordinator$10(UceRequestCoordinator.RequestResult result) {
        return !result.isRequestSuccess();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$checkAndFinishRequestCoordinator$12(UceRequestCoordinator.RequestResult result) {
        return !result.getSipDetails().isEmpty();
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

    private void triggerCompletedCallback(SipDetails details) {
        try {
            try {
                logd("triggerCompletedCallback");
                this.mCapabilitiesCallback.onComplete(details);
            } catch (RemoteException e) {
                logw("triggerCompletedCallback exception: " + e);
            }
        } finally {
            logd("triggerCompletedCallback: done");
        }
    }

    private void triggerErrorCallback(int errorCode, long retryAfterMillis, SipDetails details) {
        try {
            try {
                logd("triggerErrorCallback: errorCode=" + errorCode + ", retry=" + retryAfterMillis);
                this.mCapabilitiesCallback.onError(errorCode, retryAfterMillis, details);
            } catch (RemoteException e) {
                logw("triggerErrorCallback exception: " + e);
            }
        } finally {
            logd("triggerErrorCallback: done");
        }
    }

    public Collection<UceRequest> getActivatedRequest() {
        return this.mActivatedRequests.values();
    }

    public Collection<UceRequestCoordinator.RequestResult> getFinishedRequest() {
        return this.mFinishedRequests.values();
    }
}
