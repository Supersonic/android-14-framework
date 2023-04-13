package com.android.ims.rcs.uce.request;

import android.os.RemoteException;
import android.telephony.ims.RcsContactUceCapability;
import android.telephony.ims.aidl.IOptionsRequestCallback;
import com.android.ims.rcs.uce.UceStatsWriter;
import com.android.ims.rcs.uce.request.RemoteOptionsRequest;
import com.android.ims.rcs.uce.request.UceRequestCoordinator;
import com.android.ims.rcs.uce.request.UceRequestManager;
import com.android.ims.rcs.uce.util.NetworkSipCode;
import java.util.Collection;
/* loaded from: classes.dex */
public class RemoteOptionsCoordinator extends UceRequestCoordinator {
    private static final RequestResultCreator sRemoteResponseCreator = new RequestResultCreator() { // from class: com.android.ims.rcs.uce.request.RemoteOptionsCoordinator$$ExternalSyntheticLambda0
        @Override // com.android.ims.rcs.uce.request.RemoteOptionsCoordinator.RequestResultCreator
        public final UceRequestCoordinator.RequestResult createRequestResult(long j, RemoteOptionsRequest.RemoteOptResponse remoteOptResponse) {
            return RemoteOptionsCoordinator.lambda$static$0(j, remoteOptResponse);
        }
    };
    private IOptionsRequestCallback mOptionsReqCallback;
    private final UceStatsWriter mUceStatsWriter;

    /* JADX INFO: Access modifiers changed from: private */
    @FunctionalInterface
    /* loaded from: classes.dex */
    public interface RequestResultCreator {
        UceRequestCoordinator.RequestResult createRequestResult(long j, RemoteOptionsRequest.RemoteOptResponse remoteOptResponse);
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        RemoteOptionsCoordinator mRemoteOptionsCoordinator;

        public Builder(int subId, Collection<UceRequest> requests, UceRequestManager.RequestManagerCallback c) {
            this.mRemoteOptionsCoordinator = new RemoteOptionsCoordinator(subId, requests, c, UceStatsWriter.getInstance());
        }

        public Builder(int subId, Collection<UceRequest> requests, UceRequestManager.RequestManagerCallback c, UceStatsWriter instance) {
            this.mRemoteOptionsCoordinator = new RemoteOptionsCoordinator(subId, requests, c, instance);
        }

        public Builder setOptionsRequestCallback(IOptionsRequestCallback callback) {
            this.mRemoteOptionsCoordinator.setOptionsRequestCallback(callback);
            return this;
        }

        public RemoteOptionsCoordinator build() {
            return this.mRemoteOptionsCoordinator;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ UceRequestCoordinator.RequestResult lambda$static$0(long taskId, RemoteOptionsRequest.RemoteOptResponse response) {
        RcsContactUceCapability capability = response.getRcsContactCapability();
        if (capability != null) {
            return UceRequestCoordinator.RequestResult.createSuccessResult(taskId);
        }
        int errorCode = response.getErrorSipCode().orElse(Integer.valueOf((int) NetworkSipCode.SIP_CODE_SERVER_INTERNAL_ERROR)).intValue();
        return UceRequestCoordinator.RequestResult.createFailedResult(taskId, errorCode, 0L);
    }

    private RemoteOptionsCoordinator(int subId, Collection<UceRequest> requests, UceRequestManager.RequestManagerCallback requestMgrCallback, UceStatsWriter instance) {
        super(subId, requests, requestMgrCallback);
        this.mUceStatsWriter = instance;
        logd("RemoteOptionsCoordinator: created");
    }

    public void setOptionsRequestCallback(IOptionsRequestCallback callback) {
        this.mOptionsReqCallback = callback;
    }

    @Override // com.android.ims.rcs.uce.request.UceRequestCoordinator
    public void onFinish() {
        logd("RemoteOptionsCoordinator: onFinish");
        this.mOptionsReqCallback = null;
        super.onFinish();
    }

    @Override // com.android.ims.rcs.uce.request.UceRequestCoordinator
    public void onRequestUpdated(long taskId, int event) {
        if (this.mIsFinished) {
            return;
        }
        RemoteOptionsRequest request = (RemoteOptionsRequest) getUceRequest(Long.valueOf(taskId));
        if (request == null) {
            logw("onRequestUpdated: Cannot find RemoteOptionsRequest taskId=" + taskId);
            return;
        }
        logd("onRequestUpdated: taskId=" + taskId + ", event=" + REQUEST_EVENT_DESC.get(Integer.valueOf(event)));
        switch (event) {
            case 8:
                handleRemoteRequestDone(request);
                break;
            default:
                logw("onRequestUpdated: invalid event " + event);
                break;
        }
        checkAndFinishRequestCoordinator();
    }

    private void handleRemoteRequestDone(RemoteOptionsRequest request) {
        RemoteOptionsRequest.RemoteOptResponse response = request.getRemoteOptResponse();
        RcsContactUceCapability capability = response.getRcsContactCapability();
        if (capability != null) {
            boolean isNumberBlocked = response.isNumberBlocked();
            triggerOptionsReqCallback(capability, isNumberBlocked);
        } else {
            int errorCode = response.getErrorSipCode().orElse(Integer.valueOf((int) NetworkSipCode.SIP_CODE_SERVER_INTERNAL_ERROR)).intValue();
            String reason = response.getErrorReason().orElse(NetworkSipCode.SIP_SERVICE_UNAVAILABLE);
            triggerOptionsReqWithErrorCallback(errorCode, reason);
        }
        request.onFinish();
        Long taskId = Long.valueOf(request.getTaskId());
        UceRequestCoordinator.RequestResult requestResult = sRemoteResponseCreator.createRequestResult(taskId.longValue(), response);
        moveRequestToFinishedCollection(taskId, requestResult);
    }

    private void triggerOptionsReqCallback(RcsContactUceCapability deviceCaps, boolean isRemoteNumberBlocked) {
        try {
            try {
                logd("triggerOptionsReqCallback: start");
                this.mUceStatsWriter.setUceEvent(this.mSubId, 2, true, 0, NetworkSipCode.SIP_CODE_OK);
                this.mOptionsReqCallback.respondToCapabilityRequest(deviceCaps, isRemoteNumberBlocked);
            } catch (RemoteException e) {
                logw("triggerOptionsReqCallback exception: " + e);
            }
        } finally {
            logd("triggerOptionsReqCallback: done");
        }
    }

    private void triggerOptionsReqWithErrorCallback(int errorCode, String reason) {
        try {
            try {
                logd("triggerOptionsReqWithErrorCallback: start");
                this.mUceStatsWriter.setUceEvent(this.mSubId, 2, true, 0, errorCode);
                this.mOptionsReqCallback.respondToCapabilityRequestWithError(errorCode, reason);
            } catch (RemoteException e) {
                logw("triggerOptionsReqWithErrorCallback exception: " + e);
            }
        } finally {
            logd("triggerOptionsReqWithErrorCallback: done");
        }
    }

    private void checkAndFinishRequestCoordinator() {
        synchronized (this.mCollectionLock) {
            if (this.mActivatedRequests.isEmpty()) {
                this.mRequestManagerCallback.notifyRequestCoordinatorFinished(this.mCoordinatorId);
                logd("checkAndFinishRequestCoordinator: id=" + this.mCoordinatorId);
            }
        }
    }

    public Collection<UceRequest> getActivatedRequest() {
        return this.mActivatedRequests.values();
    }

    public Collection<UceRequestCoordinator.RequestResult> getFinishedRequest() {
        return this.mFinishedRequests.values();
    }
}
