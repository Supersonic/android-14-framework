package com.android.ims.rcs.uce.request;

import android.net.Uri;
import android.os.RemoteException;
import android.telephony.ims.RcsContactTerminatedReason;
import android.telephony.ims.RcsContactUceCapability;
import android.telephony.ims.SipDetails;
import android.telephony.ims.aidl.ISubscribeResponseCallback;
import com.android.ims.rcs.uce.eab.EabCapabilityResult;
import com.android.ims.rcs.uce.presence.pidfparser.PidfParser;
import com.android.ims.rcs.uce.presence.pidfparser.PidfParserUtils;
import com.android.ims.rcs.uce.presence.pidfparser.RcsContactUceCapabilityWrapper;
import com.android.ims.rcs.uce.presence.subscribe.SubscribeController;
import com.android.ims.rcs.uce.request.UceRequestManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class SubscribeRequest extends CapabilityRequest {
    private final ISubscribeResponseCallback mResponseCallback;
    private SubscribeController mSubscribeController;

    public SubscribeRequest(int subId, int requestType, UceRequestManager.RequestManagerCallback taskMgrCallback, SubscribeController subscribeController) {
        super(subId, requestType, taskMgrCallback);
        this.mResponseCallback = new ISubscribeResponseCallback.Stub() { // from class: com.android.ims.rcs.uce.request.SubscribeRequest.1
            public void onCommandError(int code) {
                SubscribeRequest.this.onCommandError(code);
            }

            public void onNetworkResponse(SipDetails details) {
                SubscribeRequest.this.onNetworkResponse(details);
            }

            public void onNotifyCapabilitiesUpdate(List<String> pidfXmls) {
                SubscribeRequest.this.onCapabilitiesUpdate(pidfXmls);
            }

            public void onResourceTerminated(List<RcsContactTerminatedReason> terminatedList) {
                SubscribeRequest.this.onResourceTerminated(terminatedList);
            }

            public void onTerminated(String reason, long retryAfterMillis) {
                SubscribeRequest.this.onTerminated(reason, retryAfterMillis);
            }
        };
        this.mSubscribeController = subscribeController;
        logd("SubscribeRequest created");
    }

    public SubscribeRequest(int subId, int requestType, UceRequestManager.RequestManagerCallback taskMgrCallback, SubscribeController subscribeController, CapabilityRequestResponse requestResponse) {
        super(subId, requestType, taskMgrCallback, requestResponse);
        this.mResponseCallback = new ISubscribeResponseCallback.Stub() { // from class: com.android.ims.rcs.uce.request.SubscribeRequest.1
            public void onCommandError(int code) {
                SubscribeRequest.this.onCommandError(code);
            }

            public void onNetworkResponse(SipDetails details) {
                SubscribeRequest.this.onNetworkResponse(details);
            }

            public void onNotifyCapabilitiesUpdate(List<String> pidfXmls) {
                SubscribeRequest.this.onCapabilitiesUpdate(pidfXmls);
            }

            public void onResourceTerminated(List<RcsContactTerminatedReason> terminatedList) {
                SubscribeRequest.this.onResourceTerminated(terminatedList);
            }

            public void onTerminated(String reason, long retryAfterMillis) {
                SubscribeRequest.this.onTerminated(reason, retryAfterMillis);
            }
        };
        this.mSubscribeController = subscribeController;
    }

    @Override // com.android.ims.rcs.uce.request.CapabilityRequest, com.android.ims.rcs.uce.request.UceRequest
    public void onFinish() {
        this.mSubscribeController = null;
        super.onFinish();
        logd("SubscribeRequest finish");
    }

    @Override // com.android.ims.rcs.uce.request.CapabilityRequest
    public void requestCapabilities(List<Uri> requestCapUris) {
        SubscribeController subscribeController = this.mSubscribeController;
        if (subscribeController == null) {
            logw("requestCapabilities: request is finished");
            this.mRequestResponse.setRequestInternalError(1);
            this.mRequestManagerCallback.notifyRequestError(this.mCoordinatorId, this.mTaskId);
            return;
        }
        logi("requestCapabilities: size=" + requestCapUris.size());
        try {
            subscribeController.requestCapabilities(requestCapUris, this.mResponseCallback);
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
            logw("onCommandError: request is already finished");
            return;
        }
        this.mRequestResponse.setCommandError(cmdError);
        this.mRequestManagerCallback.notifyCommandError(this.mCoordinatorId, this.mTaskId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNetworkResponse(SipDetails details) {
        logd("onNetworkResponse: sip details=" + details.toString());
        if (this.mIsFinished) {
            logw("onNetworkResponse: request is already finished");
            return;
        }
        this.mRequestResponse.setSipDetails(details);
        this.mRequestManagerCallback.notifyNetworkResponse(this.mCoordinatorId, this.mTaskId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onResourceTerminated(List<RcsContactTerminatedReason> terminatedResource) {
        if (this.mIsFinished) {
            logw("onResourceTerminated: request is already finished");
            return;
        }
        if (terminatedResource == null) {
            logw("onResourceTerminated: the parameter is null");
            terminatedResource = Collections.emptyList();
        }
        logd("onResourceTerminated: size=" + terminatedResource.size());
        this.mRequestResponse.addTerminatedResource(terminatedResource);
        this.mRequestManagerCallback.notifyResourceTerminated(this.mCoordinatorId, this.mTaskId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCapabilitiesUpdate(List<String> pidfXml) {
        if (this.mIsFinished) {
            logw("onCapabilitiesUpdate: request is already finished");
            return;
        }
        if (pidfXml == null) {
            logw("onCapabilitiesUpdate: The parameter is null");
            pidfXml = Collections.EMPTY_LIST;
        }
        List<RcsContactUceCapabilityWrapper> capabilityList = (List) pidfXml.stream().map(new Function() { // from class: com.android.ims.rcs.uce.request.SubscribeRequest$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                RcsContactUceCapabilityWrapper rcsContactUceCapabilityWrapper;
                rcsContactUceCapabilityWrapper = PidfParser.getRcsContactUceCapabilityWrapper((String) obj);
                return rcsContactUceCapabilityWrapper;
            }
        }).filter(new Predicate() { // from class: com.android.ims.rcs.uce.request.SubscribeRequest$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Objects.nonNull((RcsContactUceCapabilityWrapper) obj);
            }
        }).collect(Collectors.toList());
        List<RcsContactUceCapability> notReceivedCapabilityList = new ArrayList<>();
        if (capabilityList.isEmpty()) {
            logd("onCapabilitiesUpdate: The capabilities list is empty, Set to non-RCS user.");
            List<Uri> notReceiveCapUpdatedContactList = this.mRequestResponse.getNotReceiveCapabilityUpdatedContact();
            notReceivedCapabilityList = (List) notReceiveCapUpdatedContactList.stream().map(new Function() { // from class: com.android.ims.rcs.uce.request.SubscribeRequest$$ExternalSyntheticLambda2
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return PidfParserUtils.getNotFoundContactCapabilities((Uri) obj);
                }
            }).filter(new CapabilityRequest$$ExternalSyntheticLambda3()).collect(Collectors.toList());
        }
        List<RcsContactUceCapability> updateCapabilityList = new ArrayList<>();
        List<Uri> malformedListWithEntityURI = new ArrayList<>();
        for (RcsContactUceCapabilityWrapper capability : capabilityList) {
            if (!capability.isMalformed()) {
                updateCapabilityList.add(capability.toRcsContactUceCapability());
            } else {
                logw("onCapabilitiesUpdate: malformed capability was found and not saved.");
                malformedListWithEntityURI.add(capability.getEntityUri());
            }
        }
        logd("onCapabilitiesUpdate: PIDF size=" + pidfXml.size() + ", not received capability size=" + notReceivedCapabilityList.size() + ", normal capability size=" + updateCapabilityList.size() + ", malformed but entity uri is valid capability size=" + malformedListWithEntityURI.size());
        for (RcsContactUceCapability emptyCapability : notReceivedCapabilityList) {
            updateCapabilityList.add(emptyCapability);
        }
        List<EabCapabilityResult> cachedCapabilityList = this.mRequestManagerCallback.getCapabilitiesFromCache(malformedListWithEntityURI);
        for (EabCapabilityResult cacheEabCapability : cachedCapabilityList) {
            RcsContactUceCapability cachedCapability = cacheEabCapability.getContactCapabilities();
            if (cachedCapability != null) {
                updateCapabilityList.add(cachedCapability);
            }
        }
        logd("onCapabilitiesUpdate: updatedCapability size=" + updateCapabilityList.size());
        this.mRequestResponse.addUpdatedCapabilities(updateCapabilityList);
        this.mRequestManagerCallback.notifyCapabilitiesUpdated(this.mCoordinatorId, this.mTaskId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTerminated(String reason, long retryAfterMillis) {
        logd("onTerminated: reason=" + reason + ", retryAfter=" + retryAfterMillis);
        if (this.mIsFinished) {
            logd("onTerminated: This request is already finished");
            return;
        }
        this.mRequestResponse.setTerminated(reason, retryAfterMillis);
        this.mRequestManagerCallback.notifyTerminated(this.mCoordinatorId, this.mTaskId);
    }

    public ISubscribeResponseCallback getResponseCallback() {
        return this.mResponseCallback;
    }
}
