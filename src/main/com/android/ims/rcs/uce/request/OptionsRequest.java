package com.android.ims.rcs.uce.request;

import android.net.Uri;
import android.os.RemoteException;
import android.telephony.ims.RcsContactUceCapability;
import android.telephony.ims.aidl.IOptionsResponseCallback;
import com.android.ims.rcs.uce.options.OptionsController;
import com.android.ims.rcs.uce.request.UceRequestManager;
import com.android.ims.rcs.uce.util.NetworkSipCode;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/* loaded from: classes.dex */
public class OptionsRequest extends CapabilityRequest {
    private Uri mContactUri;
    private OptionsController mOptionsController;
    private IOptionsResponseCallback mResponseCallback;

    public OptionsRequest(int subId, int requestType, UceRequestManager.RequestManagerCallback taskMgrCallback, OptionsController optionsController) {
        super(subId, requestType, taskMgrCallback);
        this.mResponseCallback = new IOptionsResponseCallback.Stub() { // from class: com.android.ims.rcs.uce.request.OptionsRequest.1
            public void onCommandError(int code) {
                OptionsRequest.this.onCommandError(code);
            }

            public void onNetworkResponse(int sipCode, String reason, List<String> remoteCaps) {
                OptionsRequest.this.onNetworkResponse(sipCode, reason, remoteCaps);
            }
        };
        this.mOptionsController = optionsController;
        logd("OptionsRequest created");
    }

    public OptionsRequest(int subId, int requestType, UceRequestManager.RequestManagerCallback taskMgrCallback, OptionsController optionsController, CapabilityRequestResponse requestResponse) {
        super(subId, requestType, taskMgrCallback, requestResponse);
        this.mResponseCallback = new IOptionsResponseCallback.Stub() { // from class: com.android.ims.rcs.uce.request.OptionsRequest.1
            public void onCommandError(int code) {
                OptionsRequest.this.onCommandError(code);
            }

            public void onNetworkResponse(int sipCode, String reason, List<String> remoteCaps) {
                OptionsRequest.this.onNetworkResponse(sipCode, reason, remoteCaps);
            }
        };
        this.mOptionsController = optionsController;
    }

    @Override // com.android.ims.rcs.uce.request.CapabilityRequest, com.android.ims.rcs.uce.request.UceRequest
    public void onFinish() {
        this.mOptionsController = null;
        super.onFinish();
        logd("OptionsRequest finish");
    }

    @Override // com.android.ims.rcs.uce.request.CapabilityRequest
    public void requestCapabilities(List<Uri> requestCapUris) {
        OptionsController optionsController = this.mOptionsController;
        if (optionsController == null) {
            logw("requestCapabilities: request is finished");
            this.mRequestResponse.setRequestInternalError(1);
            this.mRequestManagerCallback.notifyRequestError(this.mCoordinatorId, this.mTaskId);
            return;
        }
        RcsContactUceCapability deviceCap = this.mRequestManagerCallback.getDeviceCapabilities(2);
        if (deviceCap == null) {
            logw("requestCapabilities: Cannot get device capabilities");
            this.mRequestResponse.setRequestInternalError(1);
            this.mRequestManagerCallback.notifyRequestError(this.mCoordinatorId, this.mTaskId);
            return;
        }
        this.mContactUri = requestCapUris.get(0);
        Set<String> featureTags = deviceCap.getFeatureTags();
        logi("requestCapabilities: featureTag size=" + featureTags.size());
        try {
            optionsController.sendCapabilitiesRequest(this.mContactUri, featureTags, this.mResponseCallback);
            setupRequestTimeoutTimer();
        } catch (RemoteException e) {
            logw("requestCapabilities exception: " + e);
            this.mRequestResponse.setRequestInternalError(1);
            this.mRequestManagerCallback.notifyRequestError(this.mCoordinatorId, this.mTaskId);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCommandError(int cmdError) {
        logd("onCommandError: error code=" + cmdError);
        if (this.mIsFinished) {
            logw("onCommandError: The request is already finished");
            return;
        }
        this.mRequestResponse.setCommandError(cmdError);
        this.mRequestManagerCallback.notifyCommandError(this.mCoordinatorId, this.mTaskId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNetworkResponse(int sipCode, String reason, List<String> remoteCaps) {
        logd("onNetworkResponse: sipCode=" + sipCode + ", reason=" + reason + ", remoteCap size=" + (remoteCaps == null ? "null" : Integer.valueOf(remoteCaps.size())));
        if (this.mIsFinished) {
            logw("onNetworkResponse: The request is already finished");
            return;
        }
        if (remoteCaps == null) {
            remoteCaps = Collections.EMPTY_LIST;
        }
        this.mRequestResponse.setNetworkResponseCode(sipCode, reason);
        this.mRequestResponse.setRemoteCapabilities(new HashSet(remoteCaps));
        RcsContactUceCapability contactCapabilities = getContactCapabilities(this.mContactUri, sipCode, new HashSet(remoteCaps));
        this.mRequestResponse.addUpdatedCapabilities(Collections.singletonList(contactCapabilities));
        this.mRequestManagerCallback.notifyNetworkResponse(this.mCoordinatorId, this.mTaskId);
    }

    private RcsContactUceCapability getContactCapabilities(Uri contact, int sipCode, Set<String> featureTags) {
        int requestResult = 3;
        if (!this.mRequestResponse.isNetworkResponseOK()) {
            switch (sipCode) {
                case NetworkSipCode.SIP_CODE_NOT_FOUND /* 404 */:
                case NetworkSipCode.SIP_CODE_DOES_NOT_EXIST_ANYWHERE /* 604 */:
                    requestResult = 2;
                    break;
                case NetworkSipCode.SIP_CODE_REQUEST_TIMEOUT /* 408 */:
                case NetworkSipCode.SIP_CODE_TEMPORARILY_UNAVAILABLE /* 480 */:
                    requestResult = 1;
                    break;
                default:
                    requestResult = 2;
                    break;
            }
        }
        RcsContactUceCapability.OptionsBuilder optionsBuilder = new RcsContactUceCapability.OptionsBuilder(contact, 0);
        optionsBuilder.setRequestResult(requestResult);
        optionsBuilder.addFeatureTags(featureTags);
        return optionsBuilder.build();
    }

    public IOptionsResponseCallback getResponseCallback() {
        return this.mResponseCallback;
    }
}
