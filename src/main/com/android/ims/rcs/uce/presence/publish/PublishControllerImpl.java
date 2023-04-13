package com.android.ims.rcs.uce.presence.publish;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.telephony.CarrierConfigManager;
import android.telephony.ims.ImsException;
import android.telephony.ims.PublishAttributes;
import android.telephony.ims.RcsContactPresenceTuple;
import android.telephony.ims.RcsContactUceCapability;
import android.telephony.ims.SipDetails;
import android.telephony.ims.aidl.IImsCapabilityCallback;
import android.telephony.ims.aidl.IRcsUcePublishStateCallback;
import android.telephony.ims.feature.RcsFeature;
import android.util.IndentingPrintWriter;
import android.util.LocalLog;
import android.util.Log;
import com.android.ims.RcsFeatureManager;
import com.android.ims.SomeArgs;
import com.android.ims.rcs.uce.UceController;
import com.android.ims.rcs.uce.UceDeviceState;
import com.android.ims.rcs.uce.UceStatsWriter;
import com.android.ims.rcs.uce.presence.publish.PublishController;
import com.android.ims.rcs.uce.util.UceUtils;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class PublishControllerImpl implements PublishController {
    private static final String LOG_TAG = UceUtils.getLogPrefix() + "PublishController";
    private int mCapabilityType;
    private final Context mContext;
    public int mCurrentPublishState;
    private DeviceCapabilityListener mDeviceCapListener;
    private DeviceCapListenerFactory mDeviceCapListenerFactory;
    private DeviceCapabilityInfo mDeviceCapabilityInfo;
    private volatile boolean mIsDestroyedFlag;
    public int mLastPublishState;
    private final LocalLog mLocalLog;
    private String mPidfXml;
    private final PublishController.PublishControllerCallback mPublishControllerCallback;
    private PublishHandler mPublishHandler;
    private PublishProcessor mPublishProcessor;
    private PublishProcessorFactory mPublishProcessorFactory;
    private RemoteCallbackList<IRcsUcePublishStateCallback> mPublishStateCallbacks;
    private final Object mPublishStateLock;
    private Instant mPublishStateUpdatedTime;
    private final IImsCapabilityCallback mRcsCapabilitiesCallback;
    private volatile RcsFeatureManager mRcsFeatureManager;
    private volatile boolean mReceivePublishFromService;
    private final int mSubId;
    private final UceController.UceControllerCallback mUceCtrlCallback;
    private final UceStatsWriter mUceStatsWriter;

    /* loaded from: classes.dex */
    public interface DeviceCapListenerFactory {
        DeviceCapabilityListener createDeviceCapListener(Context context, int i, DeviceCapabilityInfo deviceCapabilityInfo, PublishController.PublishControllerCallback publishControllerCallback, UceStatsWriter uceStatsWriter);
    }

    /* loaded from: classes.dex */
    public interface PublishProcessorFactory {
        PublishProcessor createPublishProcessor(Context context, int i, DeviceCapabilityInfo deviceCapabilityInfo, PublishController.PublishControllerCallback publishControllerCallback);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ PublishProcessor lambda$new$0(Context context, int subId, DeviceCapabilityInfo capInfo, PublishController.PublishControllerCallback callback) {
        return new PublishProcessor(context, subId, capInfo, callback);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ DeviceCapabilityListener lambda$new$1(Context context, int subId, DeviceCapabilityInfo capInfo, PublishController.PublishControllerCallback callback, UceStatsWriter uceStatsWriter) {
        return new DeviceCapabilityListener(context, subId, capInfo, callback, uceStatsWriter);
    }

    public PublishControllerImpl(Context context, int subId, UceController.UceControllerCallback callback, Looper looper) {
        this.mLocalLog = new LocalLog(20);
        this.mPublishStateUpdatedTime = Instant.now();
        this.mPublishStateLock = new Object();
        this.mPublishProcessorFactory = new PublishProcessorFactory() { // from class: com.android.ims.rcs.uce.presence.publish.PublishControllerImpl$$ExternalSyntheticLambda1
            @Override // com.android.ims.rcs.uce.presence.publish.PublishControllerImpl.PublishProcessorFactory
            public final PublishProcessor createPublishProcessor(Context context2, int i, DeviceCapabilityInfo deviceCapabilityInfo, PublishController.PublishControllerCallback publishControllerCallback) {
                return PublishControllerImpl.lambda$new$0(context2, i, deviceCapabilityInfo, publishControllerCallback);
            }
        };
        this.mDeviceCapListenerFactory = new DeviceCapListenerFactory() { // from class: com.android.ims.rcs.uce.presence.publish.PublishControllerImpl$$ExternalSyntheticLambda2
            @Override // com.android.ims.rcs.uce.presence.publish.PublishControllerImpl.DeviceCapListenerFactory
            public final DeviceCapabilityListener createDeviceCapListener(Context context2, int i, DeviceCapabilityInfo deviceCapabilityInfo, PublishController.PublishControllerCallback publishControllerCallback, UceStatsWriter uceStatsWriter) {
                return PublishControllerImpl.lambda$new$1(context2, i, deviceCapabilityInfo, publishControllerCallback, uceStatsWriter);
            }
        };
        this.mRcsCapabilitiesCallback = new IImsCapabilityCallback.Stub() { // from class: com.android.ims.rcs.uce.presence.publish.PublishControllerImpl.1
            public void onQueryCapabilityConfiguration(int resultCapability, int resultRadioTech, boolean enabled) {
            }

            public void onCapabilitiesStatusChanged(int capabilities) {
                PublishControllerImpl.this.logd("onCapabilitiesStatusChanged: " + capabilities);
                PublishControllerImpl.this.mPublishHandler.sendRcsCapabilitiesStatusChangedMsg(capabilities);
            }

            public void onChangeCapabilityConfigurationError(int capability, int radioTech, int reason) {
            }
        };
        this.mPublishControllerCallback = new PublishController.PublishControllerCallback() { // from class: com.android.ims.rcs.uce.presence.publish.PublishControllerImpl.2
            @Override // com.android.ims.rcs.uce.presence.publish.PublishController.PublishControllerCallback
            public void requestPublishFromInternal(int type) {
                PublishControllerImpl.this.logd("requestPublishFromInternal: type=" + type);
                PublishControllerImpl.this.mPublishHandler.sendPublishMessage(type);
            }

            @Override // com.android.ims.rcs.uce.presence.publish.PublishController.PublishControllerCallback
            public void onRequestCommandError(PublishRequestResponse requestResponse) {
                PublishControllerImpl.this.logd("onRequestCommandError: taskId=" + requestResponse.getTaskId() + ", time=" + requestResponse.getResponseTimestamp());
                PublishControllerImpl.this.mPublishHandler.sendRequestCommandErrorMessage(requestResponse);
            }

            @Override // com.android.ims.rcs.uce.presence.publish.PublishController.PublishControllerCallback
            public void onRequestNetworkResp(PublishRequestResponse requestResponse) {
                PublishControllerImpl.this.logd("onRequestNetworkResp: taskId=" + requestResponse.getTaskId() + ", time=" + requestResponse.getResponseTimestamp());
                PublishControllerImpl.this.mPublishHandler.sendRequestNetworkRespMessage(requestResponse);
            }

            @Override // com.android.ims.rcs.uce.presence.publish.PublishController.PublishControllerCallback
            public void setupRequestCanceledTimer(long taskId, long delay) {
                PublishControllerImpl.this.logd("setupRequestCanceledTimer: taskId=" + taskId + ", delay=" + delay);
                PublishControllerImpl.this.mPublishHandler.sendRequestCanceledTimerMessage(taskId, delay);
            }

            @Override // com.android.ims.rcs.uce.presence.publish.PublishController.PublishControllerCallback
            public void clearRequestCanceledTimer() {
                PublishControllerImpl.this.logd("clearRequestCanceledTimer");
                PublishControllerImpl.this.mPublishHandler.clearRequestCanceledTimer();
            }

            @Override // com.android.ims.rcs.uce.presence.publish.PublishController.PublishControllerCallback
            public void updatePublishRequestResult(int state, Instant updatedTime, String pidfXml, SipDetails details) {
                PublishControllerImpl.this.logd("updatePublishRequestResult: " + state + ", time=" + updatedTime);
                PublishControllerImpl.this.mPublishHandler.sendPublishStateChangedMessage(state, updatedTime, pidfXml, details);
            }

            @Override // com.android.ims.rcs.uce.presence.publish.PublishController.PublishControllerCallback
            public void updatePublishThrottle(int value) {
                PublishControllerImpl.this.logd("updatePublishThrottle: value=" + value);
                PublishControllerImpl.this.mPublishProcessor.updatePublishThrottle(value);
            }

            @Override // com.android.ims.rcs.uce.presence.publish.PublishController.PublishControllerCallback
            public void refreshDeviceState(int sipCode, String reason) {
                PublishControllerImpl.this.mUceCtrlCallback.refreshDeviceState(sipCode, reason, 1);
            }

            @Override // com.android.ims.rcs.uce.presence.publish.PublishController.PublishControllerCallback
            public void notifyPendingPublishRequest() {
                PublishControllerImpl.this.logd("notifyPendingPublishRequest");
                PublishControllerImpl.this.mPublishHandler.sendPublishSentMessage();
            }

            @Override // com.android.ims.rcs.uce.presence.publish.PublishController.PublishControllerCallback
            public void updateImsUnregistered() {
                PublishControllerImpl.this.logd("updateImsUnregistered");
                PublishControllerImpl.this.mPublishHandler.sendImsUnregisteredMessage();
            }
        };
        this.mSubId = subId;
        this.mContext = context;
        this.mUceCtrlCallback = callback;
        this.mUceStatsWriter = UceStatsWriter.getInstance();
        logi("create");
        initPublishController(looper);
    }

    public PublishControllerImpl(Context context, int subId, UceController.UceControllerCallback c, Looper looper, DeviceCapListenerFactory deviceCapFactory, PublishProcessorFactory processorFactory, UceStatsWriter instance) {
        this.mLocalLog = new LocalLog(20);
        this.mPublishStateUpdatedTime = Instant.now();
        this.mPublishStateLock = new Object();
        this.mPublishProcessorFactory = new PublishProcessorFactory() { // from class: com.android.ims.rcs.uce.presence.publish.PublishControllerImpl$$ExternalSyntheticLambda1
            @Override // com.android.ims.rcs.uce.presence.publish.PublishControllerImpl.PublishProcessorFactory
            public final PublishProcessor createPublishProcessor(Context context2, int i, DeviceCapabilityInfo deviceCapabilityInfo, PublishController.PublishControllerCallback publishControllerCallback) {
                return PublishControllerImpl.lambda$new$0(context2, i, deviceCapabilityInfo, publishControllerCallback);
            }
        };
        this.mDeviceCapListenerFactory = new DeviceCapListenerFactory() { // from class: com.android.ims.rcs.uce.presence.publish.PublishControllerImpl$$ExternalSyntheticLambda2
            @Override // com.android.ims.rcs.uce.presence.publish.PublishControllerImpl.DeviceCapListenerFactory
            public final DeviceCapabilityListener createDeviceCapListener(Context context2, int i, DeviceCapabilityInfo deviceCapabilityInfo, PublishController.PublishControllerCallback publishControllerCallback, UceStatsWriter uceStatsWriter) {
                return PublishControllerImpl.lambda$new$1(context2, i, deviceCapabilityInfo, publishControllerCallback, uceStatsWriter);
            }
        };
        this.mRcsCapabilitiesCallback = new IImsCapabilityCallback.Stub() { // from class: com.android.ims.rcs.uce.presence.publish.PublishControllerImpl.1
            public void onQueryCapabilityConfiguration(int resultCapability, int resultRadioTech, boolean enabled) {
            }

            public void onCapabilitiesStatusChanged(int capabilities) {
                PublishControllerImpl.this.logd("onCapabilitiesStatusChanged: " + capabilities);
                PublishControllerImpl.this.mPublishHandler.sendRcsCapabilitiesStatusChangedMsg(capabilities);
            }

            public void onChangeCapabilityConfigurationError(int capability, int radioTech, int reason) {
            }
        };
        this.mPublishControllerCallback = new PublishController.PublishControllerCallback() { // from class: com.android.ims.rcs.uce.presence.publish.PublishControllerImpl.2
            @Override // com.android.ims.rcs.uce.presence.publish.PublishController.PublishControllerCallback
            public void requestPublishFromInternal(int type) {
                PublishControllerImpl.this.logd("requestPublishFromInternal: type=" + type);
                PublishControllerImpl.this.mPublishHandler.sendPublishMessage(type);
            }

            @Override // com.android.ims.rcs.uce.presence.publish.PublishController.PublishControllerCallback
            public void onRequestCommandError(PublishRequestResponse requestResponse) {
                PublishControllerImpl.this.logd("onRequestCommandError: taskId=" + requestResponse.getTaskId() + ", time=" + requestResponse.getResponseTimestamp());
                PublishControllerImpl.this.mPublishHandler.sendRequestCommandErrorMessage(requestResponse);
            }

            @Override // com.android.ims.rcs.uce.presence.publish.PublishController.PublishControllerCallback
            public void onRequestNetworkResp(PublishRequestResponse requestResponse) {
                PublishControllerImpl.this.logd("onRequestNetworkResp: taskId=" + requestResponse.getTaskId() + ", time=" + requestResponse.getResponseTimestamp());
                PublishControllerImpl.this.mPublishHandler.sendRequestNetworkRespMessage(requestResponse);
            }

            @Override // com.android.ims.rcs.uce.presence.publish.PublishController.PublishControllerCallback
            public void setupRequestCanceledTimer(long taskId, long delay) {
                PublishControllerImpl.this.logd("setupRequestCanceledTimer: taskId=" + taskId + ", delay=" + delay);
                PublishControllerImpl.this.mPublishHandler.sendRequestCanceledTimerMessage(taskId, delay);
            }

            @Override // com.android.ims.rcs.uce.presence.publish.PublishController.PublishControllerCallback
            public void clearRequestCanceledTimer() {
                PublishControllerImpl.this.logd("clearRequestCanceledTimer");
                PublishControllerImpl.this.mPublishHandler.clearRequestCanceledTimer();
            }

            @Override // com.android.ims.rcs.uce.presence.publish.PublishController.PublishControllerCallback
            public void updatePublishRequestResult(int state, Instant updatedTime, String pidfXml, SipDetails details) {
                PublishControllerImpl.this.logd("updatePublishRequestResult: " + state + ", time=" + updatedTime);
                PublishControllerImpl.this.mPublishHandler.sendPublishStateChangedMessage(state, updatedTime, pidfXml, details);
            }

            @Override // com.android.ims.rcs.uce.presence.publish.PublishController.PublishControllerCallback
            public void updatePublishThrottle(int value) {
                PublishControllerImpl.this.logd("updatePublishThrottle: value=" + value);
                PublishControllerImpl.this.mPublishProcessor.updatePublishThrottle(value);
            }

            @Override // com.android.ims.rcs.uce.presence.publish.PublishController.PublishControllerCallback
            public void refreshDeviceState(int sipCode, String reason) {
                PublishControllerImpl.this.mUceCtrlCallback.refreshDeviceState(sipCode, reason, 1);
            }

            @Override // com.android.ims.rcs.uce.presence.publish.PublishController.PublishControllerCallback
            public void notifyPendingPublishRequest() {
                PublishControllerImpl.this.logd("notifyPendingPublishRequest");
                PublishControllerImpl.this.mPublishHandler.sendPublishSentMessage();
            }

            @Override // com.android.ims.rcs.uce.presence.publish.PublishController.PublishControllerCallback
            public void updateImsUnregistered() {
                PublishControllerImpl.this.logd("updateImsUnregistered");
                PublishControllerImpl.this.mPublishHandler.sendImsUnregisteredMessage();
            }
        };
        this.mSubId = subId;
        this.mContext = context;
        this.mUceCtrlCallback = c;
        this.mDeviceCapListenerFactory = deviceCapFactory;
        this.mPublishProcessorFactory = processorFactory;
        this.mUceStatsWriter = instance;
        initPublishController(looper);
    }

    private void initPublishController(Looper looper) {
        int capabilityType = PublishUtils.getCapabilityType(this.mContext, this.mSubId);
        this.mCapabilityType = capabilityType;
        int initialPublishState = getInitialPublishState(capabilityType);
        this.mCurrentPublishState = initialPublishState;
        this.mLastPublishState = initialPublishState;
        this.mPublishStateCallbacks = new RemoteCallbackList<>();
        this.mPublishHandler = new PublishHandler(this, looper);
        String[] serviceDescFeatureTagMap = getCarrierServiceDescriptionFeatureTagMap();
        this.mDeviceCapabilityInfo = new DeviceCapabilityInfo(this.mSubId, serviceDescFeatureTagMap);
        initPublishProcessor();
        initDeviceCapabilitiesListener();
        this.mDeviceCapListener.initialize();
        logd("initPublishController completed: capabilityType=" + this.mCapabilityType + ", publishState=" + this.mCurrentPublishState);
    }

    private int getInitialPublishState(int capabilityType) {
        if (capabilityType == 2) {
            return 2;
        }
        if (capabilityType == 1) {
            return 1;
        }
        return 6;
    }

    private void initPublishProcessor() {
        this.mPublishProcessor = this.mPublishProcessorFactory.createPublishProcessor(this.mContext, this.mSubId, this.mDeviceCapabilityInfo, this.mPublishControllerCallback);
    }

    private void initDeviceCapabilitiesListener() {
        this.mDeviceCapListener = this.mDeviceCapListenerFactory.createDeviceCapListener(this.mContext, this.mSubId, this.mDeviceCapabilityInfo, this.mPublishControllerCallback, this.mUceStatsWriter);
    }

    @Override // com.android.ims.rcs.uce.ControllerBase
    public void onRcsConnected(RcsFeatureManager manager) {
        logd("onRcsConnected");
        this.mPublishHandler.sendRcsConnectedMsg(manager);
    }

    @Override // com.android.ims.rcs.uce.ControllerBase
    public void onRcsDisconnected() {
        logd("onRcsDisconnected");
        this.mPublishHandler.sendRcsDisconnectedMsg();
    }

    @Override // com.android.ims.rcs.uce.ControllerBase
    public void onDestroy() {
        logi("onDestroy");
        this.mPublishHandler.sendDestroyedMsg();
    }

    @Override // com.android.ims.rcs.uce.ControllerBase
    public void onCarrierConfigChanged() {
        logi("onCarrierConfigChanged");
        this.mPublishHandler.sendCarrierConfigChangedMsg();
    }

    @Override // com.android.ims.rcs.uce.presence.publish.PublishController
    public int getUcePublishState(boolean isSupportPublishingState) {
        synchronized (this.mPublishStateLock) {
            if (this.mIsDestroyedFlag) {
                return 6;
            }
            if (isSupportPublishingState) {
                return this.mCurrentPublishState;
            }
            int i = this.mCurrentPublishState;
            if (i == 7) {
                return this.mLastPublishState;
            }
            return i;
        }
    }

    @Override // com.android.ims.rcs.uce.presence.publish.PublishController
    public RcsContactUceCapability addRegistrationOverrideCapabilities(Set<String> featureTags) {
        if (this.mDeviceCapabilityInfo.addRegistrationOverrideCapabilities(featureTags)) {
            this.mPublishHandler.sendPublishMessage(14);
        }
        return this.mDeviceCapabilityInfo.getDeviceCapabilities(1, this.mContext);
    }

    @Override // com.android.ims.rcs.uce.presence.publish.PublishController
    public RcsContactUceCapability removeRegistrationOverrideCapabilities(Set<String> featureTags) {
        if (this.mDeviceCapabilityInfo.removeRegistrationOverrideCapabilities(featureTags)) {
            this.mPublishHandler.sendPublishMessage(14);
        }
        return this.mDeviceCapabilityInfo.getDeviceCapabilities(1, this.mContext);
    }

    @Override // com.android.ims.rcs.uce.presence.publish.PublishController
    public RcsContactUceCapability clearRegistrationOverrideCapabilities() {
        if (this.mDeviceCapabilityInfo.clearRegistrationOverrideCapabilities()) {
            this.mPublishHandler.sendPublishMessage(14);
        }
        return this.mDeviceCapabilityInfo.getDeviceCapabilities(1, this.mContext);
    }

    @Override // com.android.ims.rcs.uce.presence.publish.PublishController
    public RcsContactUceCapability getLatestRcsContactUceCapability() {
        return this.mDeviceCapabilityInfo.getDeviceCapabilities(1, this.mContext);
    }

    @Override // com.android.ims.rcs.uce.presence.publish.PublishController
    public String getLastPidfXml() {
        return this.mPidfXml;
    }

    @Override // com.android.ims.rcs.uce.presence.publish.PublishController
    public void registerPublishStateCallback(IRcsUcePublishStateCallback c, boolean supportPublishingState) {
        synchronized (this.mPublishStateLock) {
            if (this.mIsDestroyedFlag) {
                return;
            }
            this.mPublishStateCallbacks.register(c, Boolean.valueOf(supportPublishingState));
            logd("registerPublishStateCallback: size=" + this.mPublishStateCallbacks.getRegisteredCallbackCount());
            this.mPublishHandler.sendNotifyCurrentPublishStateMessage(c, supportPublishingState);
        }
    }

    @Override // com.android.ims.rcs.uce.presence.publish.PublishController
    public void unregisterPublishStateCallback(IRcsUcePublishStateCallback c) {
        synchronized (this.mPublishStateLock) {
            if (this.mIsDestroyedFlag) {
                return;
            }
            this.mPublishStateCallbacks.unregister(c);
            logd("unregisterPublishStateCallback:mPublishStateCallbacks: size=" + this.mPublishStateCallbacks.getRegisteredCallbackCount());
        }
    }

    @Override // com.android.ims.rcs.uce.presence.publish.PublishController
    public void setupResetDeviceStateTimer(long resetAfterSec) {
        logd("setupResetDeviceStateTimer: resetAfterSec=" + resetAfterSec);
        this.mPublishHandler.sendResetDeviceStateTimerMessage(resetAfterSec);
    }

    @Override // com.android.ims.rcs.uce.presence.publish.PublishController
    public void clearResetDeviceStateTimer() {
        logd("clearResetDeviceStateTimer");
        this.mPublishHandler.clearResetDeviceStateTimer();
    }

    private void clearPublishStateCallbacks() {
        synchronized (this.mPublishStateLock) {
            logd("clearPublishStateCallbacks");
            int lastIndex = this.mPublishStateCallbacks.getRegisteredCallbackCount() - 1;
            for (int index = lastIndex; index >= 0; index--) {
                IRcsUcePublishStateCallback callback = this.mPublishStateCallbacks.getRegisteredCallbackItem(index);
                this.mPublishStateCallbacks.unregister(callback);
            }
        }
    }

    @Override // com.android.ims.rcs.uce.presence.publish.PublishController
    public void onUnpublish() {
        logd("onUnpublish");
        if (this.mIsDestroyedFlag) {
            return;
        }
        this.mPublishHandler.sendUnpublishedMessage(2);
    }

    @Override // com.android.ims.rcs.uce.presence.publish.PublishController
    public void onPublishUpdated(SipDetails details) {
        if (this.mIsDestroyedFlag) {
            return;
        }
        this.mPublishHandler.sendPublishUpdatedMessage(details);
    }

    @Override // com.android.ims.rcs.uce.presence.publish.PublishController
    public RcsContactUceCapability getDeviceCapabilities(int mechanism) {
        return this.mDeviceCapabilityInfo.getDeviceCapabilities(mechanism, this.mContext);
    }

    @Override // com.android.ims.rcs.uce.presence.publish.PublishController
    public void requestPublishCapabilitiesFromService(int triggerType) {
        logi("Receive the publish request from service: service trigger type=" + triggerType);
        this.mPublishHandler.sendPublishMessage(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PublishHandler extends Handler {
        private static Map<Integer, String> EVENT_DESCRIPTION = null;
        private static final int MSG_CARRIER_CONFIG_CHANGED = 4;
        private static final int MSG_DESTROYED = 3;
        private static final int MSG_IMS_UNREGISTERED = 16;
        private static final int MSG_NOTIFY_CURRENT_PUBLISH_STATE = 7;
        private static final int MSG_PUBLISH_SENT = 14;
        private static final int MSG_PUBLISH_STATE_CHANGED = 6;
        private static final int MSG_PUBLISH_UPDATED = 15;
        private static final int MSG_RCS_CAPABILITIES_CHANGED = 5;
        private static final int MSG_RCS_CONNECTED = 1;
        private static final int MSG_RCS_DISCONNECTED = 2;
        private static final int MSG_REQUEST_CANCELED = 11;
        private static final int MSG_REQUEST_CMD_ERROR = 9;
        private static final int MSG_REQUEST_NETWORK_RESPONSE = 10;
        private static final int MSG_REQUEST_PUBLISH = 8;
        private static final int MSG_RESET_DEVICE_STATE = 12;
        private static final int MSG_UNPUBLISHED = 13;
        private final WeakReference<PublishControllerImpl> mPublishControllerRef;

        public PublishHandler(PublishControllerImpl publishController, Looper looper) {
            super(looper);
            this.mPublishControllerRef = new WeakReference<>(publishController);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            PublishControllerImpl publishCtrl = this.mPublishControllerRef.get();
            if (publishCtrl == null || publishCtrl.mIsDestroyedFlag) {
                return;
            }
            publishCtrl.logd("handleMessage: " + EVENT_DESCRIPTION.get(Integer.valueOf(message.what)));
            switch (message.what) {
                case 1:
                    SomeArgs args = (SomeArgs) message.obj;
                    RcsFeatureManager manager = (RcsFeatureManager) args.arg1;
                    args.recycle();
                    publishCtrl.handleRcsConnectedMessage(manager);
                    break;
                case 2:
                    publishCtrl.handleRcsDisconnectedMessage();
                    break;
                case 3:
                    publishCtrl.handleDestroyedMessage();
                    break;
                case 4:
                    publishCtrl.handleCarrierConfigChangedMessage();
                    break;
                case 5:
                    int RcsCapabilities = message.arg1;
                    publishCtrl.handleRcsCapabilitiesChangedMessage(RcsCapabilities);
                    break;
                case 6:
                    SomeArgs args2 = (SomeArgs) message.obj;
                    int newPublishState = ((Integer) args2.arg1).intValue();
                    Instant updatedTimestamp = (Instant) args2.arg2;
                    String pidfXml = (String) args2.arg3;
                    SipDetails details = (SipDetails) args2.arg4;
                    args2.recycle();
                    publishCtrl.handlePublishStateChangedMessage(newPublishState, updatedTimestamp, pidfXml, details);
                    break;
                case 7:
                    IRcsUcePublishStateCallback c = (IRcsUcePublishStateCallback) message.obj;
                    boolean supportPublishingState = false;
                    if (message.arg1 == 1) {
                        supportPublishingState = true;
                    }
                    publishCtrl.handleNotifyCurrentPublishStateMessage(c, supportPublishingState);
                    break;
                case 8:
                    int type = message.arg1;
                    publishCtrl.handleRequestPublishMessage(type);
                    break;
                case 9:
                    PublishRequestResponse cmdErrorResponse = (PublishRequestResponse) message.obj;
                    publishCtrl.mPublishProcessor.onCommandError(cmdErrorResponse);
                    break;
                case 10:
                    PublishRequestResponse networkResponse = (PublishRequestResponse) message.obj;
                    publishCtrl.mPublishProcessor.onNetworkResponse(networkResponse);
                    break;
                case 11:
                    long taskId = ((Long) message.obj).longValue();
                    publishCtrl.handleRequestCanceledMessage(taskId);
                    break;
                case 12:
                    publishCtrl.handleResetDeviceStateMessage();
                    break;
                case 13:
                    SomeArgs args3 = (SomeArgs) message.obj;
                    int newPublishState2 = ((Integer) args3.arg1).intValue();
                    Instant updatedTimestamp2 = (Instant) args3.arg2;
                    args3.recycle();
                    publishCtrl.handleUnpublishedMessage(newPublishState2, updatedTimestamp2);
                    break;
                case 14:
                    publishCtrl.handlePublishSentMessage();
                    break;
                case 15:
                    SipDetails details2 = (SipDetails) message.obj;
                    publishCtrl.handlePublishUpdatedMessage(details2);
                    break;
                case 16:
                    publishCtrl.handleUnpublishedMessage(2, Instant.now());
                    break;
                default:
                    publishCtrl.logd("invalid message: " + message.what);
                    break;
            }
            publishCtrl.logd("handleMessage done: " + EVENT_DESCRIPTION.get(Integer.valueOf(message.what)));
        }

        public void onDestroy() {
            removeCallbacksAndMessages(null);
        }

        public void sendRcsConnectedMsg(RcsFeatureManager manager) {
            PublishControllerImpl publishCtrl = this.mPublishControllerRef.get();
            if (publishCtrl == null || publishCtrl.mIsDestroyedFlag) {
                return;
            }
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = manager;
            Message message = obtainMessage();
            message.what = 1;
            message.obj = args;
            sendMessage(message);
        }

        public void sendRcsDisconnectedMsg() {
            PublishControllerImpl publishCtrl = this.mPublishControllerRef.get();
            if (publishCtrl == null || publishCtrl.mIsDestroyedFlag) {
                return;
            }
            Message message = obtainMessage();
            message.what = 2;
            sendMessage(message);
        }

        public void sendDestroyedMsg() {
            PublishControllerImpl publishCtrl = this.mPublishControllerRef.get();
            if (publishCtrl == null || publishCtrl.mIsDestroyedFlag) {
                return;
            }
            Message message = obtainMessage();
            message.what = 3;
            sendMessage(message);
        }

        public void sendCarrierConfigChangedMsg() {
            PublishControllerImpl publishCtrl = this.mPublishControllerRef.get();
            if (publishCtrl == null || publishCtrl.mIsDestroyedFlag) {
                return;
            }
            Message message = obtainMessage();
            message.what = 4;
            sendMessage(message);
        }

        public void sendRcsCapabilitiesStatusChangedMsg(int capabilities) {
            PublishControllerImpl publishCtrl = this.mPublishControllerRef.get();
            if (publishCtrl == null || publishCtrl.mIsDestroyedFlag) {
                return;
            }
            Message message = obtainMessage();
            message.what = 5;
            message.arg1 = capabilities;
            sendMessage(message);
        }

        public void sendPublishStateChangedMessage(int publishState, Instant updatedTimestamp, String pidfXml, SipDetails details) {
            PublishControllerImpl publishCtrl = this.mPublishControllerRef.get();
            if (publishCtrl == null || publishCtrl.mIsDestroyedFlag) {
                return;
            }
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = Integer.valueOf(publishState);
            args.arg2 = updatedTimestamp;
            args.arg3 = pidfXml;
            args.arg4 = details;
            Message message = obtainMessage();
            message.what = 6;
            message.obj = args;
            sendMessage(message);
        }

        public void sendUnpublishedMessage(int publishState) {
            PublishControllerImpl publishCtrl = this.mPublishControllerRef.get();
            if (publishCtrl == null || publishCtrl.mIsDestroyedFlag) {
                return;
            }
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = Integer.valueOf(publishState);
            args.arg2 = Instant.now();
            Message message = obtainMessage();
            message.what = 13;
            message.obj = args;
            sendMessage(message);
        }

        public void sendPublishUpdatedMessage(SipDetails details) {
            PublishControllerImpl publishCtrl = this.mPublishControllerRef.get();
            if (publishCtrl == null || publishCtrl.mIsDestroyedFlag) {
                return;
            }
            Message message = obtainMessage();
            message.what = 15;
            message.obj = details;
            sendMessage(message);
        }

        public void sendNotifyCurrentPublishStateMessage(IRcsUcePublishStateCallback callback, boolean supportPublishingState) {
            PublishControllerImpl publishCtrl = this.mPublishControllerRef.get();
            if (publishCtrl == null || publishCtrl.mIsDestroyedFlag) {
                return;
            }
            Message message = obtainMessage();
            message.what = 7;
            message.arg1 = supportPublishingState ? 1 : 0;
            message.obj = callback;
            sendMessage(message);
        }

        public void sendPublishSentMessage() {
            PublishControllerImpl publishCtrl = this.mPublishControllerRef.get();
            if (publishCtrl == null || publishCtrl.mIsDestroyedFlag) {
                return;
            }
            Message message = obtainMessage();
            message.what = 14;
            sendMessage(message);
        }

        public void sendPublishMessage(int type) {
            sendPublishMessage(type, 0L);
        }

        public void sendPublishMessage(int type, long delay) {
            PublishControllerImpl publishCtrl = this.mPublishControllerRef.get();
            if (publishCtrl == null || publishCtrl.mIsDestroyedFlag) {
                return;
            }
            if (!publishCtrl.isPresencePublishEnabled() && type != 1) {
                publishCtrl.logd("sendPublishMessage: disallowed type=" + type);
                return;
            }
            Message message = obtainMessage();
            message.what = 8;
            message.arg1 = type;
            sendMessageDelayed(message, delay);
        }

        public void sendRequestCommandErrorMessage(PublishRequestResponse response) {
            PublishControllerImpl publishCtrl = this.mPublishControllerRef.get();
            if (publishCtrl == null || publishCtrl.mIsDestroyedFlag) {
                return;
            }
            Message message = obtainMessage();
            message.what = 9;
            message.obj = response;
            sendMessage(message);
        }

        public void sendRequestNetworkRespMessage(PublishRequestResponse response) {
            PublishControllerImpl publishCtrl = this.mPublishControllerRef.get();
            if (publishCtrl == null || publishCtrl.mIsDestroyedFlag) {
                return;
            }
            Message message = obtainMessage();
            message.what = 10;
            message.obj = response;
            sendMessage(message);
        }

        public void sendRequestCanceledTimerMessage(long taskId, long delay) {
            PublishControllerImpl publishCtrl = this.mPublishControllerRef.get();
            if (publishCtrl == null || publishCtrl.mIsDestroyedFlag) {
                return;
            }
            removeMessages(11, Long.valueOf(taskId));
            Message message = obtainMessage();
            message.what = 11;
            message.obj = Long.valueOf(taskId);
            sendMessageDelayed(message, delay);
        }

        public void clearRequestCanceledTimer() {
            PublishControllerImpl publishCtrl = this.mPublishControllerRef.get();
            if (publishCtrl == null || publishCtrl.mIsDestroyedFlag) {
                return;
            }
            removeMessages(11);
        }

        public void sendResetDeviceStateTimerMessage(long resetAfterSec) {
            PublishControllerImpl publishCtrl = this.mPublishControllerRef.get();
            if (publishCtrl == null || publishCtrl.mIsDestroyedFlag) {
                return;
            }
            removeMessages(12);
            Message message = obtainMessage();
            message.what = 12;
            sendMessageDelayed(message, TimeUnit.SECONDS.toMillis(resetAfterSec));
        }

        public void clearResetDeviceStateTimer() {
            PublishControllerImpl publishCtrl = this.mPublishControllerRef.get();
            if (publishCtrl == null || publishCtrl.mIsDestroyedFlag) {
                return;
            }
            removeMessages(12);
        }

        public void sendImsUnregisteredMessage() {
            PublishControllerImpl publishCtrl = this.mPublishControllerRef.get();
            if (publishCtrl == null || publishCtrl.mIsDestroyedFlag) {
                return;
            }
            Message message = obtainMessage();
            message.what = 16;
            sendMessage(message);
        }

        static {
            HashMap hashMap = new HashMap();
            EVENT_DESCRIPTION = hashMap;
            hashMap.put(1, "RCS_CONNECTED");
            EVENT_DESCRIPTION.put(2, "RCS_DISCONNECTED");
            EVENT_DESCRIPTION.put(3, "DESTROYED");
            EVENT_DESCRIPTION.put(4, "CARRIER_CONFIG_CHANGED");
            EVENT_DESCRIPTION.put(5, "RCS_CAPABILITIES_CHANGED");
            EVENT_DESCRIPTION.put(6, "PUBLISH_STATE_CHANGED");
            EVENT_DESCRIPTION.put(7, "NOTIFY_PUBLISH_STATE");
            EVENT_DESCRIPTION.put(8, "REQUEST_PUBLISH");
            EVENT_DESCRIPTION.put(9, "REQUEST_CMD_ERROR");
            EVENT_DESCRIPTION.put(10, "REQUEST_NETWORK_RESPONSE");
            EVENT_DESCRIPTION.put(11, "REQUEST_CANCELED");
            EVENT_DESCRIPTION.put(12, "RESET_DEVICE_STATE");
            EVENT_DESCRIPTION.put(13, "MSG_UNPUBLISHED");
            EVENT_DESCRIPTION.put(14, "MSG_PUBLISH_SENT");
            EVENT_DESCRIPTION.put(15, "MSG_PUBLISH_UPDATED");
            EVENT_DESCRIPTION.put(16, "MSG_IMS_UNREGISTERED");
        }
    }

    private boolean isPublishRequestAllowed() {
        if (!this.mDeviceCapabilityInfo.isPresenceCapable()) {
            logd("isPublishRequestAllowed: capability presence uce is not enabled.");
            return false;
        } else if (!this.mReceivePublishFromService) {
            logd("isPublishRequestAllowed: The first PUBLISH request from the server has not been received.");
            return false;
        } else {
            UceDeviceState.DeviceStateResult deviceState = this.mUceCtrlCallback.getDeviceState();
            if (deviceState.isRequestForbidden() || deviceState.isPublishRequestBlocked()) {
                logd("isPublishRequestAllowed: The device state is disallowed. " + deviceState.getDeviceState());
                return false;
            } else if (this.mPublishProcessor.isPublishingNow()) {
                logd("isPublishRequestAllowed: There is already a publish request running now.");
                return false;
            } else {
                return true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPresencePublishEnabled() {
        boolean z;
        synchronized (this.mPublishStateLock) {
            z = this.mCapabilityType == 2;
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRcsConnectedMessage(RcsFeatureManager manager) {
        if (this.mIsDestroyedFlag) {
            return;
        }
        this.mRcsFeatureManager = manager;
        this.mDeviceCapListener.onRcsConnected();
        this.mPublishProcessor.onRcsConnected(manager);
        registerRcsAvailabilityChanged(manager);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRcsDisconnectedMessage() {
        if (this.mIsDestroyedFlag) {
            return;
        }
        this.mRcsFeatureManager = null;
        this.mDeviceCapabilityInfo.updatePresenceCapable(false);
        this.mDeviceCapListener.onRcsDisconnected();
        this.mPublishProcessor.onRcsDisconnected();
        if (isPresencePublishEnabled()) {
            handlePublishStateChangedMessage(2, Instant.now(), null, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDestroyedMessage() {
        this.mIsDestroyedFlag = true;
        this.mDeviceCapabilityInfo.updatePresenceCapable(false);
        unregisterRcsAvailabilityChanged();
        this.mDeviceCapListener.onDestroy();
        this.mPublishHandler.onDestroy();
        this.mPublishProcessor.onDestroy();
        synchronized (this.mPublishStateLock) {
            clearPublishStateCallbacks();
        }
    }

    private void registerRcsAvailabilityChanged(RcsFeatureManager manager) {
        try {
            manager.registerRcsAvailabilityCallback(this.mSubId, this.mRcsCapabilitiesCallback);
        } catch (ImsException e) {
            logw("registerRcsAvailabilityChanged exception " + e);
        }
    }

    private void unregisterRcsAvailabilityChanged() {
        RcsFeatureManager manager = this.mRcsFeatureManager;
        if (manager == null) {
            return;
        }
        try {
            manager.unregisterRcsAvailabilityCallback(this.mSubId, this.mRcsCapabilitiesCallback);
        } catch (Exception e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleCarrierConfigChangedMessage() {
        if (this.mIsDestroyedFlag) {
            return;
        }
        updateCapabilityTypeAndPublishStateIfNeeded();
        String[] newMap = getCarrierServiceDescriptionFeatureTagMap();
        if (this.mDeviceCapabilityInfo.updateCapabilityRegistrationTrackerMap(newMap)) {
            this.mPublishHandler.sendPublishMessage(15);
        }
    }

    private void updateCapabilityTypeAndPublishStateIfNeeded() {
        synchronized (this.mPublishStateLock) {
            int originalMechanism = this.mCapabilityType;
            int capabilityType = PublishUtils.getCapabilityType(this.mContext, this.mSubId);
            this.mCapabilityType = capabilityType;
            if (originalMechanism == capabilityType) {
                logd("updateCapTypeAndPublishStateIfNeeded: The capability type is not changed=" + this.mCapabilityType);
                return;
            }
            int updatedPublishState = getInitialPublishState(capabilityType);
            logd("updateCapTypeAndPublishStateIfNeeded from " + originalMechanism + " to " + this.mCapabilityType + ", new publish state=" + updatedPublishState);
            handlePublishStateChangedMessage(updatedPublishState, Instant.now(), null, null);
        }
    }

    private String[] getCarrierServiceDescriptionFeatureTagMap() {
        CarrierConfigManager manager = (CarrierConfigManager) this.mContext.getSystemService(CarrierConfigManager.class);
        PersistableBundle bundle = manager != null ? manager.getConfigForSubId(this.mSubId) : CarrierConfigManager.getDefaultConfig();
        return bundle.getStringArray("ims.publish_service_desc_feature_tag_map_override_string_array");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRcsCapabilitiesChangedMessage(int capabilities) {
        logd("handleRcsCapabilitiesChangedMessage: " + capabilities);
        if (this.mIsDestroyedFlag) {
            return;
        }
        RcsFeature.RcsImsCapabilities RcsImsCapabilities = new RcsFeature.RcsImsCapabilities(capabilities);
        this.mDeviceCapabilityInfo.updatePresenceCapable(RcsImsCapabilities.isCapable(2));
        if (this.mDeviceCapabilityInfo.isPresenceCapable()) {
            this.mPublishProcessor.checkAndSendPendingRequest();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePublishStateChangedMessage(int newPublishState, Instant updatedTimestamp, String pidfXml, final SipDetails details) {
        synchronized (this.mPublishStateLock) {
            if (this.mIsDestroyedFlag) {
                return;
            }
            if (updatedTimestamp != null && updatedTimestamp.isAfter(this.mPublishStateUpdatedTime)) {
                logd("publish state changes from " + this.mCurrentPublishState + " to " + newPublishState + ", time=" + updatedTimestamp);
                this.mPublishStateUpdatedTime = updatedTimestamp;
                this.mPidfXml = pidfXml;
                int i = this.mCurrentPublishState;
                if (i == newPublishState) {
                    return;
                }
                this.mLastPublishState = i;
                this.mCurrentPublishState = newPublishState;
                if (newPublishState == 2) {
                    this.mUceStatsWriter.setUnPublish(this.mSubId);
                }
                logd("Notify publish state changed: " + this.mCurrentPublishState);
                this.mPublishStateCallbacks.broadcast(new Consumer() { // from class: com.android.ims.rcs.uce.presence.publish.PublishControllerImpl$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        PublishControllerImpl.this.lambda$handlePublishStateChangedMessage$2(details, (IRcsUcePublishStateCallback) obj);
                    }
                });
                logd("Notify publish state changed: completed");
                return;
            }
            logd("handlePublishStateChangedMessage: updatedTimestamp is not allowed: " + this.mPublishStateUpdatedTime + " to " + updatedTimestamp + ", publishState=" + newPublishState);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handlePublishStateChangedMessage$2(SipDetails details, IRcsUcePublishStateCallback c) {
        try {
            c.onPublishUpdated(getPublishAttributes(this.mCurrentPublishState, details));
        } catch (RemoteException e) {
            logw("Notify publish state changed error: " + e);
        }
    }

    private PublishAttributes getPublishAttributes(int mCurrentPublishState, SipDetails details) {
        List<RcsContactPresenceTuple> tuples = null;
        if (mCurrentPublishState == 1) {
            tuples = this.mDeviceCapabilityInfo.getLastSuccessfulPresenceTuplesWithoutContactUri();
        }
        if (tuples != null && !tuples.isEmpty()) {
            return new PublishAttributes.Builder(mCurrentPublishState).setSipDetails(details).setPresenceTuples(tuples).build();
        }
        return new PublishAttributes.Builder(mCurrentPublishState).setSipDetails(details).build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleNotifyCurrentPublishStateMessage(IRcsUcePublishStateCallback callback, boolean supportPublishingState) {
        if (this.mIsDestroyedFlag || callback == null) {
            return;
        }
        try {
            int publishState = getUcePublishState(supportPublishingState);
            callback.onPublishUpdated(getPublishAttributes(publishState, null));
        } catch (RemoteException e) {
            logw("handleCurrentPublishStateUpdateMessage exception: " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRequestPublishMessage(int type) {
        if (this.mIsDestroyedFlag) {
            return;
        }
        logd("handleRequestPublishMessage: type=" + type);
        if (type == 1) {
            if (!this.mReceivePublishFromService) {
                this.mReceivePublishFromService = true;
            }
            UceDeviceState.DeviceStateResult deviceState = this.mUceCtrlCallback.getDeviceState();
            if (deviceState.isRequestForbidden() || deviceState.isPublishRequestBlocked()) {
                this.mUceCtrlCallback.resetDeviceState();
            }
        }
        if (!isPublishRequestAllowed()) {
            logd("handleRequestPublishMessage: SKIP. The request is not allowed. type=" + type);
            this.mPublishProcessor.setPendingRequest(type);
            return;
        }
        this.mPublishProcessor.updatePublishingAllowedTime(type);
        Optional<Long> delay = this.mPublishProcessor.getPublishingDelayTime();
        if (!delay.isPresent()) {
            logd("handleRequestPublishMessage: SKIP. The delay is empty. type=" + type);
            this.mPublishProcessor.setPendingRequest(type);
            return;
        }
        logd("handleRequestPublishMessage: " + type + ", delay=" + delay.get());
        if (delay.get().longValue() == 0) {
            this.mPublishProcessor.doPublish(type);
        } else {
            this.mPublishHandler.sendPublishMessage(type, delay.get().longValue());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRequestCanceledMessage(long taskId) {
        if (this.mIsDestroyedFlag) {
            return;
        }
        this.mPublishProcessor.cancelPublishRequest(taskId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleResetDeviceStateMessage() {
        if (this.mIsDestroyedFlag) {
            return;
        }
        this.mUceCtrlCallback.resetDeviceState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleUnpublishedMessage(int newPublishState, Instant updatedTimestamp) {
        if (this.mIsDestroyedFlag) {
            return;
        }
        this.mPublishProcessor.resetState();
        handlePublishStateChangedMessage(newPublishState, updatedTimestamp, null, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePublishSentMessage() {
        synchronized (this.mPublishStateLock) {
            if (this.mIsDestroyedFlag) {
                return;
            }
            int lastIndex = this.mPublishStateCallbacks.getRegisteredCallbackCount() - 1;
            int tempPublishState = this.mCurrentPublishState;
            for (int index = lastIndex; index >= 0; index--) {
                IRcsUcePublishStateCallback callback = this.mPublishStateCallbacks.getRegisteredCallbackItem(index);
                boolean isSupportPublishingState = false;
                try {
                    Object object = this.mPublishStateCallbacks.getRegisteredCallbackCookie(index);
                    if (object != null) {
                        isSupportPublishingState = ((Boolean) object).booleanValue();
                    }
                } catch (Exception e) {
                }
                try {
                    this.mCurrentPublishState = 7;
                    if (isSupportPublishingState) {
                        if (callback != null) {
                            callback.onPublishUpdated(getPublishAttributes(7, null));
                        }
                    } else if (tempPublishState != 1 && tempPublishState != 2) {
                        this.mLastPublishState = 2;
                        if (callback != null) {
                            callback.onPublishUpdated(getPublishAttributes(2, null));
                        }
                    }
                } catch (RemoteException e2) {
                    logw("Notify publish state changed error: " + e2);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePublishUpdatedMessage(SipDetails details) {
        if (this.mIsDestroyedFlag) {
            return;
        }
        PublishRequestResponse updatedPublish = new PublishRequestResponse(getLastPidfXml(), details);
        this.mPublishProcessor.publishUpdated(updatedPublish);
    }

    public void setCapabilityType(int type) {
        this.mCapabilityType = type;
        int initialPublishState = getInitialPublishState(type);
        this.mCurrentPublishState = initialPublishState;
        this.mLastPublishState = initialPublishState;
    }

    public void setPublishStateCallback(RemoteCallbackList<IRcsUcePublishStateCallback> list) {
        this.mPublishStateCallbacks = list;
    }

    public PublishHandler getPublishHandler() {
        return this.mPublishHandler;
    }

    public IImsCapabilityCallback getRcsCapabilitiesCallback() {
        return this.mRcsCapabilitiesCallback;
    }

    public PublishController.PublishControllerCallback getPublishControllerCallback() {
        return this.mPublishControllerCallback;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logd(String log) {
        Log.d(LOG_TAG, getLogPrefix().append(log).toString());
        this.mLocalLog.log("[D] " + log);
    }

    private void logi(String log) {
        Log.i(LOG_TAG, getLogPrefix().append(log).toString());
        this.mLocalLog.log("[I] " + log);
    }

    private void logw(String log) {
        Log.w(LOG_TAG, getLogPrefix().append(log).toString());
        this.mLocalLog.log("[W] " + log);
    }

    private StringBuilder getLogPrefix() {
        StringBuilder builder = new StringBuilder("[");
        builder.append(this.mSubId);
        builder.append("] ");
        return builder;
    }

    @Override // com.android.ims.rcs.uce.presence.publish.PublishController
    public void dump(PrintWriter printWriter) {
        PrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println("PublishControllerImpl[subId: " + this.mSubId + "]:");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.print("isPresenceCapable=");
        indentingPrintWriter.println(this.mDeviceCapabilityInfo.isPresenceCapable());
        indentingPrintWriter.print("mCurrentPublishState=");
        indentingPrintWriter.print(this.mCurrentPublishState);
        indentingPrintWriter.print("mLastPublishState=");
        indentingPrintWriter.print(this.mLastPublishState);
        indentingPrintWriter.print(" at time ");
        indentingPrintWriter.println(this.mPublishStateUpdatedTime);
        indentingPrintWriter.println("Last PIDF XML:");
        indentingPrintWriter.increaseIndent();
        if (Build.IS_ENG) {
            indentingPrintWriter.println(this.mPidfXml);
        } else {
            if (Build.IS_DEBUGGABLE) {
                String str = this.mPidfXml;
                String pidfXml = str != null ? str : "null";
                indentingPrintWriter.println(PublishUtils.removeNumbersFromUris(pidfXml));
            } else {
                indentingPrintWriter.println(this.mPidfXml != null ? "***" : "null");
            }
        }
        indentingPrintWriter.decreaseIndent();
        PublishProcessor publishProcessor = this.mPublishProcessor;
        if (publishProcessor != null) {
            publishProcessor.dump(indentingPrintWriter);
        } else {
            indentingPrintWriter.println("mPublishProcessor is null");
        }
        indentingPrintWriter.println();
        this.mDeviceCapListener.dump(indentingPrintWriter);
        indentingPrintWriter.println("Log:");
        indentingPrintWriter.increaseIndent();
        this.mLocalLog.dump(indentingPrintWriter);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("---");
        indentingPrintWriter.decreaseIndent();
    }
}
