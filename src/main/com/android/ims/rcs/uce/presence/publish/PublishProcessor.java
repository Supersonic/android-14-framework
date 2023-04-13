package com.android.ims.rcs.uce.presence.publish;

import android.content.Context;
import android.os.RemoteException;
import android.telephony.ims.RcsContactUceCapability;
import android.telephony.ims.SipDetails;
import android.text.TextUtils;
import android.util.IndentingPrintWriter;
import android.util.LocalLog;
import android.util.Log;
import com.android.ims.RcsFeatureManager;
import com.android.ims.rcs.uce.UceStatsWriter;
import com.android.ims.rcs.uce.presence.pidfparser.PidfParser;
import com.android.ims.rcs.uce.presence.publish.PublishController;
import com.android.ims.rcs.uce.util.UceUtils;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class PublishProcessor {
    private static final String LOG_TAG = UceUtils.getLogPrefix() + "PublishProcessor";
    private static final long RESPONSE_CALLBACK_WAITING_TIME = 60000;
    private final Context mContext;
    private final DeviceCapabilityInfo mDeviceCapabilities;
    private volatile boolean mIsDestroyed;
    private final LocalLog mLocalLog;
    private final Object mPendingRequestLock;
    private PublishProcessorState mProcessorState;
    private final PublishController.PublishControllerCallback mPublishCtrlCallback;
    private volatile RcsFeatureManager mRcsFeatureManager;
    private final int mSubId;
    private final UceStatsWriter mUceStatsWriter;

    public PublishProcessor(Context context, int subId, DeviceCapabilityInfo capabilityInfo, PublishController.PublishControllerCallback publishCtrlCallback) {
        this.mPendingRequestLock = new Object();
        this.mLocalLog = new LocalLog(20);
        this.mSubId = subId;
        this.mContext = context;
        this.mDeviceCapabilities = capabilityInfo;
        this.mPublishCtrlCallback = publishCtrlCallback;
        this.mProcessorState = new PublishProcessorState(subId);
        this.mUceStatsWriter = UceStatsWriter.getInstance();
    }

    public PublishProcessor(Context context, int subId, DeviceCapabilityInfo capabilityInfo, PublishController.PublishControllerCallback publishCtrlCallback, UceStatsWriter instance) {
        this.mPendingRequestLock = new Object();
        this.mLocalLog = new LocalLog(20);
        this.mSubId = subId;
        this.mContext = context;
        this.mDeviceCapabilities = capabilityInfo;
        this.mPublishCtrlCallback = publishCtrlCallback;
        this.mProcessorState = new PublishProcessorState(subId);
        this.mUceStatsWriter = instance;
    }

    public void onRcsConnected(RcsFeatureManager featureManager) {
        this.mLocalLog.log("onRcsConnected");
        logi("onRcsConnected");
        this.mRcsFeatureManager = featureManager;
        checkAndSendPendingRequest();
    }

    public void onRcsDisconnected() {
        this.mLocalLog.log("onRcsDisconnected");
        logi("onRcsDisconnected");
        this.mRcsFeatureManager = null;
        this.mProcessorState.onRcsDisconnected();
        this.mDeviceCapabilities.resetPresenceCapability();
    }

    public void onDestroy() {
        this.mLocalLog.log("onDestroy");
        logi("onDestroy");
        this.mIsDestroyed = true;
    }

    public void doPublish(int triggerType) {
        this.mProcessorState.setPublishingFlag(true);
        if (!doPublishInternal(triggerType)) {
            this.mProcessorState.setPublishingFlag(false);
        }
    }

    private boolean doPublishInternal(int triggerType) {
        final RcsContactUceCapability deviceCapability;
        if (this.mIsDestroyed) {
            return false;
        }
        this.mLocalLog.log("doPublishInternal: trigger type=" + triggerType);
        logi("doPublishInternal: trigger type=" + triggerType);
        if (!isRequestAllowed(triggerType)) {
            this.mLocalLog.log("doPublishInternal: The request is not allowed.");
            return false;
        }
        if (triggerType == 1) {
            deviceCapability = this.mDeviceCapabilities.getDeviceCapabilities(1, this.mContext);
        } else {
            deviceCapability = this.mDeviceCapabilities.getChangedPresenceCapability(this.mContext);
        }
        if (deviceCapability == null) {
            logi("doPublishInternal: device capability hasn't changed or is null");
            return false;
        }
        String pidfXml = PidfParser.convertToPidf(deviceCapability);
        if (TextUtils.isEmpty(pidfXml)) {
            logw("doPublishInternal: pidfXml is empty");
            return false;
        }
        RcsFeatureManager featureManager = this.mRcsFeatureManager;
        if (featureManager == null) {
            logw("doPublishInternal: RCS is not connected.");
            setPendingRequest(triggerType);
            return false;
        }
        featureManager.getImsRegistrationTech(new Consumer() { // from class: com.android.ims.rcs.uce.presence.publish.PublishProcessor$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PublishProcessor.this.lambda$doPublishInternal$0(deviceCapability, (Integer) obj);
            }
        });
        return publishCapabilities(featureManager, pidfXml);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$doPublishInternal$0(RcsContactUceCapability deviceCapability, Integer tech) {
        int registrationTech = tech == null ? -1 : tech.intValue();
        this.mUceStatsWriter.setImsRegistrationServiceDescStats(this.mSubId, deviceCapability.getCapabilityTuples(), registrationTech);
    }

    private boolean isRequestAllowed(int triggerType) {
        if (this.mIsDestroyed) {
            logd("isPublishAllowed: This instance is already destroyed");
            return false;
        } else if (!isEabProvisioned()) {
            logd("isPublishAllowed: NOT provisioned");
            return false;
        } else if (!this.mDeviceCapabilities.isImsRegistered()) {
            logd("isPublishAllowed: IMS is not registered");
            return false;
        } else if (!this.mProcessorState.isPublishAllowedAtThisTime()) {
            logd("isPublishAllowed: Current time is not allowed, resend this request");
            this.mPublishCtrlCallback.requestPublishFromInternal(triggerType);
            return false;
        } else {
            return true;
        }
    }

    private boolean publishCapabilities(RcsFeatureManager featureManager, String pidfXml) {
        PublishRequestResponse requestResponse = null;
        try {
            clearPendingRequest();
            long taskId = this.mProcessorState.generatePublishTaskId();
            requestResponse = new PublishRequestResponse(this.mPublishCtrlCallback, taskId, pidfXml);
            this.mLocalLog.log("publish capabilities: taskId=" + taskId);
            logi("publishCapabilities: taskId=" + taskId);
            featureManager.requestPublication(pidfXml, requestResponse.getResponseCallback());
            this.mPublishCtrlCallback.setupRequestCanceledTimer(taskId, RESPONSE_CALLBACK_WAITING_TIME);
            this.mPublishCtrlCallback.notifyPendingPublishRequest();
            return true;
        } catch (RemoteException e) {
            this.mLocalLog.log("publish capability exception: " + e.getMessage());
            logw("publishCapabilities: exception=" + e.getMessage());
            setRequestEnded(requestResponse);
            checkAndSendPendingRequest();
            return false;
        }
    }

    public void onCommandError(PublishRequestResponse requestResponse) {
        boolean successful;
        if (!checkRequestRespValid(requestResponse)) {
            this.mLocalLog.log("Command error callback is invalid");
            logw("onCommandError: request response is invalid");
            setRequestEnded(requestResponse);
            checkAndSendPendingRequest();
            return;
        }
        this.mLocalLog.log("Receive command error code=" + requestResponse.getCmdErrorCode());
        logd("onCommandError: " + requestResponse.toString());
        int cmdError = requestResponse.getCmdErrorCode().orElse(0).intValue();
        if (cmdError != 10) {
            successful = false;
        } else {
            successful = true;
        }
        this.mUceStatsWriter.setUceEvent(this.mSubId, 0, successful, cmdError, 0);
        if (requestResponse.needRetry() && !this.mProcessorState.isReachMaximumRetries()) {
            handleRequestRespWithRetry(requestResponse);
        } else {
            handleRequestRespWithoutRetry(requestResponse);
        }
    }

    public void onNetworkResponse(PublishRequestResponse requestResponse) {
        if (!checkRequestRespValid(requestResponse)) {
            this.mLocalLog.log("Network response callback is invalid");
            logw("onNetworkResponse: request response is invalid");
            setRequestEnded(requestResponse);
            checkAndSendPendingRequest();
            return;
        }
        this.mLocalLog.log("Receive network response code=" + requestResponse.getNetworkRespSipCode());
        logd("onNetworkResponse: " + requestResponse.toString());
        int responseCode = requestResponse.getNetworkRespSipCode().orElse(0).intValue();
        this.mUceStatsWriter.setUceEvent(this.mSubId, 0, true, 0, responseCode);
        if (requestResponse.needRetry() && !this.mProcessorState.isReachMaximumRetries()) {
            handleRequestRespWithRetry(requestResponse);
        } else {
            handleRequestRespWithoutRetry(requestResponse);
        }
    }

    private boolean checkRequestRespValid(PublishRequestResponse requestResponse) {
        if (requestResponse == null) {
            logd("checkRequestRespValid: request response is null");
            return false;
        } else if (!this.mProcessorState.isPublishingNow()) {
            logd("checkRequestRespValid: the request is finished");
            return false;
        } else {
            long taskId = this.mProcessorState.getCurrentTaskId();
            long responseTaskId = requestResponse.getTaskId();
            if (taskId != responseTaskId) {
                logd("checkRequestRespValid: invalid taskId! current taskId=" + taskId + ", response callback taskId=" + responseTaskId);
                return false;
            } else if (this.mIsDestroyed) {
                logd("checkRequestRespValid: is already destroyed! taskId=" + taskId);
                return false;
            } else {
                return true;
            }
        }
    }

    private void handleRequestRespWithRetry(PublishRequestResponse requestResponse) {
        this.mProcessorState.increaseRetryCount();
        this.mDeviceCapabilities.setPresencePublishResult(false);
        clearPendingRequest();
        setRequestEnded(requestResponse);
        this.mPublishCtrlCallback.requestPublishFromInternal(2);
    }

    private void handleRequestRespWithoutRetry(PublishRequestResponse requestResponse) {
        updatePublishStateFromResponse(requestResponse);
        setRequestEnded(requestResponse);
        checkAndSendPendingRequest();
    }

    private void updatePublishStateFromResponse(final PublishRequestResponse response) {
        Instant responseTime = response.getResponseTimestamp();
        boolean publishSuccess = false;
        if (response.isRequestSuccess()) {
            this.mProcessorState.setLastPublishedTime(responseTime);
            this.mProcessorState.resetRetryCount();
            publishSuccess = true;
        }
        this.mDeviceCapabilities.setPresencePublishResult(publishSuccess);
        int publishState = response.getPublishState();
        String pidfXml = response.getPidfXml();
        SipDetails details = response.getSipDetails().orElse(null);
        this.mPublishCtrlCallback.updatePublishRequestResult(publishState, responseTime, pidfXml, details);
        response.getResponseSipCode().ifPresent(new Consumer() { // from class: com.android.ims.rcs.uce.presence.publish.PublishProcessor$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PublishProcessor.this.lambda$updatePublishStateFromResponse$1(response, (Integer) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updatePublishStateFromResponse$1(PublishRequestResponse response, Integer sipCode) {
        String reason = response.getResponseReason().orElse("");
        this.mPublishCtrlCallback.refreshDeviceState(sipCode.intValue(), reason);
    }

    public void cancelPublishRequest(long taskId) {
        this.mLocalLog.log("cancel publish request: taskId=" + taskId);
        logd("cancelPublishRequest: taskId=" + taskId);
        setRequestEnded(null);
        checkAndSendPendingRequest();
    }

    private void setRequestEnded(PublishRequestResponse requestResponse) {
        long taskId = -1;
        if (requestResponse != null) {
            requestResponse.onDestroy();
            taskId = requestResponse.getTaskId();
        }
        this.mProcessorState.setPublishingFlag(false);
        this.mPublishCtrlCallback.clearRequestCanceledTimer();
        this.mLocalLog.log("Set request ended: taskId=" + taskId);
        logd("setRequestEnded: taskId=" + taskId);
    }

    public void setPendingRequest(int triggerType) {
        synchronized (this.mPendingRequestLock) {
            this.mProcessorState.setPendingRequest(triggerType);
        }
    }

    public void checkAndSendPendingRequest() {
        synchronized (this.mPendingRequestLock) {
            if (this.mIsDestroyed) {
                return;
            }
            if (this.mProcessorState.hasPendingRequest()) {
                int type = this.mProcessorState.getPendingRequestTriggerType().orElse(2).intValue();
                logd("checkAndSendPendingRequest: send pending request, type=" + type);
                this.mProcessorState.clearPendingRequest();
                this.mPublishCtrlCallback.requestPublishFromInternal(type);
            }
        }
    }

    private void clearPendingRequest() {
        synchronized (this.mPendingRequestLock) {
            this.mProcessorState.clearPendingRequest();
        }
    }

    public void updatePublishingAllowedTime(int triggerType) {
        this.mProcessorState.updatePublishingAllowedTime(triggerType);
    }

    public Optional<Long> getPublishingDelayTime() {
        return this.mProcessorState.getPublishingDelayTime();
    }

    public void updatePublishThrottle(int publishThrottle) {
        this.mProcessorState.updatePublishThrottle(publishThrottle);
    }

    public boolean isPublishingNow() {
        return this.mProcessorState.isPublishingNow();
    }

    public void resetState() {
        this.mProcessorState.resetState();
        this.mDeviceCapabilities.resetPresenceCapability();
    }

    public void publishUpdated(PublishRequestResponse response) {
        updatePublishStateFromResponse(response);
        if (response != null) {
            response.onDestroy();
        }
    }

    public void setProcessorState(PublishProcessorState processorState) {
        this.mProcessorState = processorState;
    }

    protected boolean isEabProvisioned() {
        return UceUtils.isEabProvisioned(this.mContext, this.mSubId);
    }

    private void logd(String log) {
        Log.d(LOG_TAG, getLogPrefix().append(log).toString());
    }

    private void logi(String log) {
        Log.i(LOG_TAG, getLogPrefix().append(log).toString());
    }

    private void logw(String log) {
        Log.w(LOG_TAG, getLogPrefix().append(log).toString());
    }

    private StringBuilder getLogPrefix() {
        StringBuilder builder = new StringBuilder("[");
        builder.append(this.mSubId);
        builder.append("] ");
        return builder;
    }

    public void dump(PrintWriter printWriter) {
        IndentingPrintWriter pw = new IndentingPrintWriter(printWriter, "  ");
        pw.println("PublishProcessor[subId: " + this.mSubId + "]:");
        pw.increaseIndent();
        pw.print("ProcessorState: isPublishing=");
        pw.print(this.mProcessorState.isPublishingNow());
        pw.print(", hasReachedMaxRetries=");
        pw.print(this.mProcessorState.isReachMaximumRetries());
        pw.print(", delayTimeToAllowPublish=");
        pw.println(this.mProcessorState.getPublishingDelayTime().orElse(-1L));
        pw.println("Log:");
        pw.increaseIndent();
        this.mLocalLog.dump(pw);
        pw.decreaseIndent();
        pw.println("---");
        pw.decreaseIndent();
    }
}
