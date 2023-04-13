package com.android.ims.rcs.uce.request;

import android.net.Uri;
import android.telephony.ims.RcsContactUceCapability;
import android.util.Log;
import com.android.ims.rcs.uce.request.UceRequestManager;
import com.android.ims.rcs.uce.util.FeatureTags;
import com.android.ims.rcs.uce.util.NetworkSipCode;
import com.android.ims.rcs.uce.util.UceUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class RemoteOptionsRequest implements UceRequest {
    private static final String LOG_TAG = UceUtils.getLogPrefix() + "RemoteOptRequest";
    private volatile long mCoordinatorId;
    private volatile boolean mIsFinished;
    private volatile boolean mIsRemoteNumberBlocked;
    private final UceRequestManager.RequestManagerCallback mRequestManagerCallback;
    private final int mSubId;
    private List<Uri> mUriList;
    private final long mTaskId = UceUtils.generateTaskId();
    private final List<String> mRemoteFeatureTags = new ArrayList();
    private final RemoteOptResponse mRemoteOptResponse = new RemoteOptResponse();

    /* loaded from: classes.dex */
    public static class RemoteOptResponse {
        private boolean mIsNumberBlocked;
        private RcsContactUceCapability mRcsContactCapability;
        private Optional<Integer> mErrorSipCode = Optional.empty();
        private Optional<String> mErrorReason = Optional.empty();

        void setRespondToRequest(RcsContactUceCapability capability, boolean isBlocked) {
            this.mIsNumberBlocked = isBlocked;
            this.mRcsContactCapability = capability;
        }

        void setRespondToRequestWithError(int code, String reason) {
            this.mErrorSipCode = Optional.of(Integer.valueOf(code));
            this.mErrorReason = Optional.of(reason);
        }

        public boolean isNumberBlocked() {
            return this.mIsNumberBlocked;
        }

        public RcsContactUceCapability getRcsContactCapability() {
            return this.mRcsContactCapability;
        }

        public Optional<Integer> getErrorSipCode() {
            return this.mErrorSipCode;
        }

        public Optional<String> getErrorReason() {
            return this.mErrorReason;
        }
    }

    public RemoteOptionsRequest(int subId, UceRequestManager.RequestManagerCallback requestMgrCallback) {
        this.mSubId = subId;
        this.mRequestManagerCallback = requestMgrCallback;
        logd("created");
    }

    @Override // com.android.ims.rcs.uce.request.UceRequest
    public void setRequestCoordinatorId(long coordinatorId) {
        this.mCoordinatorId = coordinatorId;
    }

    @Override // com.android.ims.rcs.uce.request.UceRequest
    public long getRequestCoordinatorId() {
        return this.mCoordinatorId;
    }

    @Override // com.android.ims.rcs.uce.request.UceRequest
    public long getTaskId() {
        return this.mTaskId;
    }

    @Override // com.android.ims.rcs.uce.request.UceRequest
    public void onFinish() {
        this.mIsFinished = true;
    }

    @Override // com.android.ims.rcs.uce.request.UceRequest
    public void setContactUri(List<Uri> uris) {
        this.mUriList = uris;
    }

    public void setRemoteFeatureTags(List<String> remoteFeatureTags) {
        final List<String> list = this.mRemoteFeatureTags;
        Objects.requireNonNull(list);
        remoteFeatureTags.forEach(new Consumer() { // from class: com.android.ims.rcs.uce.request.RemoteOptionsRequest$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                list.add((String) obj);
            }
        });
    }

    public void setIsRemoteNumberBlocked(boolean isBlocked) {
        this.mIsRemoteNumberBlocked = isBlocked;
    }

    public RemoteOptResponse getRemoteOptResponse() {
        return this.mRemoteOptResponse;
    }

    @Override // com.android.ims.rcs.uce.request.UceRequest
    public void executeRequest() {
        logd("executeRequest");
        try {
            try {
                executeRequestInternal();
            } catch (Exception e) {
                logw("executeRequest: exception " + e);
                setResponseWithError(NetworkSipCode.SIP_CODE_SERVER_INTERNAL_ERROR, NetworkSipCode.SIP_INTERNAL_SERVER_ERROR);
            }
        } finally {
            this.mRequestManagerCallback.notifyRemoteRequestDone(this.mCoordinatorId, this.mTaskId);
        }
    }

    private void executeRequestInternal() {
        List<Uri> list = this.mUriList;
        if (list == null || list.isEmpty()) {
            logw("executeRequest: uri is empty");
            setResponseWithError(NetworkSipCode.SIP_CODE_BAD_REQUEST, NetworkSipCode.SIP_BAD_REQUEST);
        } else if (this.mIsFinished) {
            logw("executeRequest: This request is finished");
            setResponseWithError(NetworkSipCode.SIP_CODE_SERVICE_UNAVAILABLE, NetworkSipCode.SIP_SERVICE_UNAVAILABLE);
        } else {
            Uri contactUri = this.mUriList.get(0);
            RcsContactUceCapability remoteCaps = FeatureTags.getContactCapability(contactUri, 0, this.mRemoteFeatureTags);
            this.mRequestManagerCallback.saveCapabilities(Collections.singletonList(remoteCaps));
            RcsContactUceCapability deviceCaps = this.mRequestManagerCallback.getDeviceCapabilities(2);
            if (deviceCaps == null) {
                logw("executeRequest: The device's capabilities is empty");
                setResponseWithError(NetworkSipCode.SIP_CODE_SERVER_INTERNAL_ERROR, NetworkSipCode.SIP_INTERNAL_SERVER_ERROR);
                return;
            }
            logd("executeRequest: Respond to capability request, blocked=" + this.mIsRemoteNumberBlocked);
            setResponse(deviceCaps, this.mIsRemoteNumberBlocked);
        }
    }

    private void setResponse(RcsContactUceCapability deviceCaps, boolean isRemoteNumberBlocked) {
        this.mRemoteOptResponse.setRespondToRequest(deviceCaps, isRemoteNumberBlocked);
    }

    private void setResponseWithError(int errorCode, String reason) {
        this.mRemoteOptResponse.setRespondToRequestWithError(errorCode, reason);
    }

    private void logd(String log) {
        Log.d(LOG_TAG, getLogPrefix().append(log).toString());
    }

    private void logw(String log) {
        Log.d(LOG_TAG, getLogPrefix().append(log).toString());
    }

    private StringBuilder getLogPrefix() {
        StringBuilder builder = new StringBuilder("[");
        builder.append(this.mSubId).append("][taskId=").append(this.mTaskId).append("] ");
        return builder;
    }
}
