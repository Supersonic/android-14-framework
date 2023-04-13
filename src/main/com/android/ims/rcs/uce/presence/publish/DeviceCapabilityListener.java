package com.android.ims.rcs.uce.presence.publish;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.AccessNetworkConstants;
import android.telephony.ims.ImsException;
import android.telephony.ims.ImsManager;
import android.telephony.ims.ImsMmTelManager;
import android.telephony.ims.ImsRcsManager;
import android.telephony.ims.ImsReasonInfo;
import android.telephony.ims.ImsRegistrationAttributes;
import android.telephony.ims.ProvisioningManager;
import android.telephony.ims.RegistrationManager;
import android.telephony.ims.feature.MmTelFeature;
import android.util.IndentingPrintWriter;
import android.util.LocalLog;
import android.util.Log;
import com.android.ims.rcs.uce.UceStatsWriter;
import com.android.ims.rcs.uce.presence.publish.PublishController;
import com.android.ims.rcs.uce.util.UceUtils;
import com.android.internal.telephony.util.HandlerExecutor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class DeviceCapabilityListener {
    private static final String LOG_TAG = UceUtils.getLogPrefix() + "DeviceCapListener";
    private static final long REGISTER_IMS_CHANGED_DELAY = 15000;
    private final PublishController.PublishControllerCallback mCallback;
    private final DeviceCapabilityInfo mCapabilityInfo;
    private final Context mContext;
    private final DeviceCapabilityHandler mHandler;
    private final HandlerExecutor mHandlerExecutor;
    private final HandlerThread mHandlerThread;
    private ImsMmTelManager mImsMmTelManager;
    private ImsRcsManager mImsRcsManager;
    private volatile boolean mInitialized;
    private volatile boolean mIsDestroyed;
    private volatile boolean mIsImsCallbackRegistered;
    private volatile boolean mIsRcsConnected;
    private ProvisioningManager mProvisioningManager;
    private final int mSubId;
    private final UceStatsWriter mUceStatsWriter;
    private final LocalLog mLocalLog = new LocalLog(20);
    private ImsMmTelManagerFactory mImsMmTelManagerFactory = new ImsMmTelManagerFactory() { // from class: com.android.ims.rcs.uce.presence.publish.DeviceCapabilityListener$$ExternalSyntheticLambda0
        @Override // com.android.ims.rcs.uce.presence.publish.DeviceCapabilityListener.ImsMmTelManagerFactory
        public final ImsMmTelManager getImsMmTelManager(int i) {
            ImsMmTelManager lambda$new$0;
            lambda$new$0 = DeviceCapabilityListener.this.lambda$new$0(i);
            return lambda$new$0;
        }
    };
    private ImsRcsManagerFactory mImsRcsManagerFactory = new ImsRcsManagerFactory() { // from class: com.android.ims.rcs.uce.presence.publish.DeviceCapabilityListener$$ExternalSyntheticLambda1
        @Override // com.android.ims.rcs.uce.presence.publish.DeviceCapabilityListener.ImsRcsManagerFactory
        public final ImsRcsManager getImsRcsManager(int i) {
            ImsRcsManager lambda$new$1;
            lambda$new$1 = DeviceCapabilityListener.this.lambda$new$1(i);
            return lambda$new$1;
        }
    };
    private ProvisioningManagerFactory mProvisioningMgrFactory = new ProvisioningManagerFactory() { // from class: com.android.ims.rcs.uce.presence.publish.DeviceCapabilityListener$$ExternalSyntheticLambda2
        @Override // com.android.ims.rcs.uce.presence.publish.DeviceCapabilityListener.ProvisioningManagerFactory
        public final ProvisioningManager getProvisioningManager(int i) {
            ProvisioningManager createForSubscriptionId;
            createForSubscriptionId = ProvisioningManager.createForSubscriptionId(i);
            return createForSubscriptionId;
        }
    };
    private ContentObserver mMobileDataObserver = null;
    private ContentObserver mSimInfoContentObserver = null;
    private final Object mLock = new Object();
    public final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.ims.rcs.uce.presence.publish.DeviceCapabilityListener.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            char c;
            if (intent == null || intent.getAction() == null) {
                return;
            }
            String action = intent.getAction();
            switch (action.hashCode()) {
                case -2003364962:
                    if (action.equals("android.telecom.action.TTY_PREFERRED_MODE_CHANGED")) {
                        c = 0;
                        break;
                    }
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    int preferredMode = intent.getIntExtra("android.telecom.extra.TTY_PREFERRED_MODE", 0);
                    DeviceCapabilityListener.this.handleTtyPreferredModeChanged(preferredMode);
                    return;
                default:
                    return;
            }
        }
    };
    public final RegistrationManager.RegistrationCallback mRcsRegistrationCallback = new RegistrationManager.RegistrationCallback() { // from class: com.android.ims.rcs.uce.presence.publish.DeviceCapabilityListener.4
        @Override // android.telephony.ims.RegistrationManager.RegistrationCallback
        public void onRegistered(ImsRegistrationAttributes attributes) {
            synchronized (DeviceCapabilityListener.this.mLock) {
                DeviceCapabilityListener.this.logi("onRcsRegistered: " + attributes);
                if (DeviceCapabilityListener.this.mIsImsCallbackRegistered) {
                    List<String> featureTagList = new ArrayList<>(attributes.getFeatureTags());
                    int registrationTech = attributes.getRegistrationTechnology();
                    DeviceCapabilityListener.this.mUceStatsWriter.setImsRegistrationFeatureTagStats(DeviceCapabilityListener.this.mSubId, featureTagList, registrationTech);
                    DeviceCapabilityListener.this.handleImsRcsRegistered(attributes);
                }
            }
        }

        @Override // android.telephony.ims.RegistrationManager.RegistrationCallback
        public void onUnregistered(ImsReasonInfo info) {
            synchronized (DeviceCapabilityListener.this.mLock) {
                DeviceCapabilityListener.this.logi("onRcsUnregistered: " + info);
                if (DeviceCapabilityListener.this.mIsImsCallbackRegistered) {
                    DeviceCapabilityListener.this.mUceStatsWriter.setStoreCompleteImsRegistrationFeatureTagStats(DeviceCapabilityListener.this.mSubId);
                    DeviceCapabilityListener.this.handleImsRcsUnregistered();
                }
            }
        }

        public void onSubscriberAssociatedUriChanged(Uri[] uris) {
            synchronized (DeviceCapabilityListener.this.mLock) {
                DeviceCapabilityListener.this.logi("onRcsSubscriberAssociatedUriChanged");
                DeviceCapabilityListener.this.handleRcsSubscriberAssociatedUriChanged(uris, false);
            }
        }
    };
    public final RegistrationManager.RegistrationCallback mMmtelRegistrationCallback = new RegistrationManager.RegistrationCallback() { // from class: com.android.ims.rcs.uce.presence.publish.DeviceCapabilityListener.5
        @Override // android.telephony.ims.RegistrationManager.RegistrationCallback
        public void onRegistered(int transportType) {
            synchronized (DeviceCapabilityListener.this.mLock) {
                String type = AccessNetworkConstants.transportTypeToString(transportType);
                DeviceCapabilityListener.this.logi("onMmTelRegistered: " + type);
                if (DeviceCapabilityListener.this.mIsImsCallbackRegistered) {
                    DeviceCapabilityListener.this.handleImsMmtelRegistered(transportType);
                }
            }
        }

        @Override // android.telephony.ims.RegistrationManager.RegistrationCallback
        public void onUnregistered(ImsReasonInfo info) {
            synchronized (DeviceCapabilityListener.this.mLock) {
                DeviceCapabilityListener.this.logi("onMmTelUnregistered: " + info);
                if (DeviceCapabilityListener.this.mIsImsCallbackRegistered) {
                    DeviceCapabilityListener.this.handleImsMmtelUnregistered();
                }
            }
        }

        public void onSubscriberAssociatedUriChanged(Uri[] uris) {
            synchronized (DeviceCapabilityListener.this.mLock) {
                DeviceCapabilityListener.this.logi("onMmTelSubscriberAssociatedUriChanged");
                DeviceCapabilityListener.this.handleMmTelSubscriberAssociatedUriChanged(uris, false);
            }
        }
    };
    public final ImsMmTelManager.CapabilityCallback mMmtelCapabilityCallback = new ImsMmTelManager.CapabilityCallback() { // from class: com.android.ims.rcs.uce.presence.publish.DeviceCapabilityListener.6
        @Override // android.telephony.ims.ImsMmTelManager.CapabilityCallback
        public void onCapabilitiesStatusChanged(MmTelFeature.MmTelCapabilities capabilities) {
            if (capabilities == null) {
                DeviceCapabilityListener.this.logw("onCapabilitiesStatusChanged: parameter is null");
                return;
            }
            synchronized (DeviceCapabilityListener.this.mLock) {
                DeviceCapabilityListener.this.handleMmtelCapabilitiesStatusChanged(capabilities);
            }
        }
    };
    public final ProvisioningManager.Callback mProvisionChangedCallback = new ProvisioningManager.Callback() { // from class: com.android.ims.rcs.uce.presence.publish.DeviceCapabilityListener.7
        public void onProvisioningIntChanged(int item, int value) {
            DeviceCapabilityListener.this.logi("onProvisioningIntChanged: item=" + item + ", value=" + value);
            switch (item) {
                case PublishController.PUBLISH_TRIGGER_RCS_REGISTERED /* 10 */:
                case PublishController.PUBLISH_TRIGGER_RCS_UNREGISTERED /* 11 */:
                case 25:
                    DeviceCapabilityListener.this.handleProvisioningChanged();
                    return;
                case 21:
                    DeviceCapabilityListener.this.handlePublishThrottleChanged(value);
                    return;
                default:
                    return;
            }
        }
    };

    /* loaded from: classes.dex */
    public interface ImsMmTelManagerFactory {
        ImsMmTelManager getImsMmTelManager(int i);
    }

    /* loaded from: classes.dex */
    public interface ImsRcsManagerFactory {
        ImsRcsManager getImsRcsManager(int i);
    }

    /* loaded from: classes.dex */
    public interface ProvisioningManagerFactory {
        ProvisioningManager getProvisioningManager(int i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DeviceCapabilityHandler extends Handler {
        private static final int EVENT_IMS_UNREGISTERED = 4;
        private static final int EVENT_REGISTER_IMS_CONTENT_CHANGE = 1;
        private static final int EVENT_REQUEST_PUBLISH = 3;
        private static final int EVENT_UNREGISTER_IMS_CHANGE = 2;
        private static final long TRIGGER_PUBLISH_REQUEST_DELAY_MS = 500;

        DeviceCapabilityHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            DeviceCapabilityListener.this.logd("handleMessage: " + msg.what);
            if (DeviceCapabilityListener.this.mIsDestroyed) {
                return;
            }
            switch (msg.what) {
                case 1:
                    DeviceCapabilityListener.this.registerImsProvisionCallback();
                    return;
                case 2:
                    DeviceCapabilityListener.this.unregisterImsProvisionCallback();
                    return;
                case 3:
                    int triggerType = msg.arg1;
                    DeviceCapabilityListener.this.mCallback.requestPublishFromInternal(triggerType);
                    return;
                case 4:
                    DeviceCapabilityListener.this.mCallback.updateImsUnregistered();
                    return;
                default:
                    return;
            }
        }

        public void sendRegisterImsContentChangedMessage(long delay) {
            removeMessages(1);
            Message msg = obtainMessage(1);
            sendMessageDelayed(msg, delay);
        }

        public void removeRegisterImsContentChangedMessage() {
            removeMessages(1);
        }

        public void sendUnregisterImsCallbackMessage() {
            removeMessages(1);
            sendEmptyMessage(2);
        }

        public void sendTriggeringPublishMessage(int type) {
            DeviceCapabilityListener.this.logd("sendTriggeringPublishMessage: type=" + type);
            removeMessages(3);
            Message message = obtainMessage();
            message.what = 3;
            message.arg1 = type;
            sendMessageDelayed(message, TRIGGER_PUBLISH_REQUEST_DELAY_MS);
        }

        public void sendImsUnregisteredMessage() {
            DeviceCapabilityListener.this.logd("sendImsUnregisteredMessage");
            removeMessages(3);
            removeMessages(4);
            Message msg = obtainMessage(4);
            sendMessageDelayed(msg, TRIGGER_PUBLISH_REQUEST_DELAY_MS);
        }
    }

    public DeviceCapabilityListener(Context context, int subId, DeviceCapabilityInfo info, PublishController.PublishControllerCallback callback, UceStatsWriter uceStatsWriter) {
        this.mSubId = subId;
        logi("create");
        this.mContext = context;
        this.mCallback = callback;
        this.mCapabilityInfo = info;
        this.mInitialized = false;
        this.mUceStatsWriter = uceStatsWriter;
        HandlerThread handlerThread = new HandlerThread("DeviceCapListenerThread");
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        DeviceCapabilityHandler deviceCapabilityHandler = new DeviceCapabilityHandler(handlerThread.getLooper());
        this.mHandler = deviceCapabilityHandler;
        this.mHandlerExecutor = new HandlerExecutor(deviceCapabilityHandler);
    }

    public void initialize() {
        synchronized (this.mLock) {
            if (this.mIsDestroyed) {
                logw("initialize: This instance is already destroyed");
            } else if (this.mInitialized) {
            } else {
                logi("initialize");
                this.mImsMmTelManager = this.mImsMmTelManagerFactory.getImsMmTelManager(this.mSubId);
                this.mImsRcsManager = this.mImsRcsManagerFactory.getImsRcsManager(this.mSubId);
                this.mProvisioningManager = this.mProvisioningMgrFactory.getProvisioningManager(this.mSubId);
                registerReceivers();
                registerImsProvisionCallback();
                this.mInitialized = true;
            }
        }
    }

    public void onRcsConnected() {
        this.mIsRcsConnected = true;
        this.mHandler.sendRegisterImsContentChangedMessage(0L);
    }

    public void onRcsDisconnected() {
        this.mIsRcsConnected = false;
        this.mHandler.sendUnregisterImsCallbackMessage();
    }

    public void onDestroy() {
        logi("onDestroy");
        this.mIsDestroyed = true;
        synchronized (this.mLock) {
            if (this.mInitialized) {
                logi("turnOffListener");
                this.mInitialized = false;
                unregisterReceivers();
                unregisterImsProvisionCallback();
                this.mHandlerThread.quit();
            }
        }
    }

    private void registerReceivers() {
        logd("registerReceivers");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.telecom.action.TTY_PREFERRED_MODE_CHANGED");
        this.mContext.registerReceiver(this.mReceiver, filter, "android.permission.MODIFY_PHONE_STATE", null, 2);
        ContentResolver resolver = this.mContext.getContentResolver();
        if (resolver != null) {
            resolver.registerContentObserver(Settings.Global.getUriFor("mobile_data"), false, getMobileDataObserver());
            resolver.registerContentObserver(Telephony.SimInfo.CONTENT_URI, false, getSimInfoContentObserver());
        }
    }

    private void unregisterReceivers() {
        logd("unregisterReceivers");
        this.mContext.unregisterReceiver(this.mReceiver);
        ContentResolver resolver = this.mContext.getContentResolver();
        if (resolver != null) {
            resolver.unregisterContentObserver(getMobileDataObserver());
            resolver.unregisterContentObserver(getSimInfoContentObserver());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerImsProvisionCallback() {
        if (this.mIsImsCallbackRegistered) {
            logd("registerImsProvisionCallback: already registered.");
            return;
        }
        logd("registerImsProvisionCallback");
        try {
            ImsMmTelManager imsMmTelManager = this.mImsMmTelManager;
            if (imsMmTelManager != null) {
                imsMmTelManager.registerImsRegistrationCallback(this.mHandlerExecutor, this.mMmtelRegistrationCallback);
                this.mImsMmTelManager.registerMmTelCapabilityCallback(this.mHandlerExecutor, this.mMmtelCapabilityCallback);
            }
            ImsRcsManager imsRcsManager = this.mImsRcsManager;
            if (imsRcsManager != null) {
                imsRcsManager.registerImsRegistrationCallback(this.mHandlerExecutor, this.mRcsRegistrationCallback);
            }
            this.mProvisioningManager.registerProvisioningChangedCallback(this.mHandlerExecutor, this.mProvisionChangedCallback);
            this.mIsImsCallbackRegistered = true;
        } catch (ImsException e) {
            logw("registerImsProvisionCallback error: " + e);
            unregisterImsProvisionCallback();
            if (this.mIsRcsConnected) {
                this.mHandler.sendRegisterImsContentChangedMessage(REGISTER_IMS_CHANGED_DELAY);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterImsProvisionCallback() {
        logd("unregisterImsProvisionCallback");
        this.mHandler.removeRegisterImsContentChangedMessage();
        ImsMmTelManager imsMmTelManager = this.mImsMmTelManager;
        if (imsMmTelManager != null) {
            try {
                imsMmTelManager.unregisterImsRegistrationCallback(this.mMmtelRegistrationCallback);
            } catch (RuntimeException e) {
                logw("unregister MMTel registration error: " + e.getMessage());
            }
            try {
                this.mImsMmTelManager.unregisterMmTelCapabilityCallback(this.mMmtelCapabilityCallback);
            } catch (RuntimeException e2) {
                logw("unregister MMTel capability error: " + e2.getMessage());
            }
        }
        ImsRcsManager imsRcsManager = this.mImsRcsManager;
        if (imsRcsManager != null) {
            try {
                imsRcsManager.unregisterImsRegistrationCallback(this.mRcsRegistrationCallback);
            } catch (RuntimeException e3) {
                logw("unregister rcs capability error: " + e3.getMessage());
            }
        }
        try {
            this.mProvisioningManager.unregisterProvisioningChangedCallback(this.mProvisionChangedCallback);
        } catch (RuntimeException e4) {
            logw("unregister provisioning callback error: " + e4.getMessage());
        }
        this.mIsImsCallbackRegistered = false;
    }

    private ContentObserver getMobileDataObserver() {
        ContentObserver contentObserver;
        synchronized (this.mLock) {
            if (this.mMobileDataObserver == null) {
                this.mMobileDataObserver = new ContentObserver(new Handler(this.mHandler.getLooper())) { // from class: com.android.ims.rcs.uce.presence.publish.DeviceCapabilityListener.2
                    @Override // android.database.ContentObserver
                    public void onChange(boolean selfChange) {
                        boolean isEnabled = Settings.Global.getInt(DeviceCapabilityListener.this.mContext.getContentResolver(), "mobile_data", 1) == 1;
                        DeviceCapabilityListener.this.handleMobileDataChanged(isEnabled);
                    }
                };
            }
            contentObserver = this.mMobileDataObserver;
        }
        return contentObserver;
    }

    private ContentObserver getSimInfoContentObserver() {
        ContentObserver contentObserver;
        synchronized (this.mLock) {
            if (this.mSimInfoContentObserver == null) {
                this.mSimInfoContentObserver = new ContentObserver(new Handler(this.mHandler.getLooper())) { // from class: com.android.ims.rcs.uce.presence.publish.DeviceCapabilityListener.3
                    @Override // android.database.ContentObserver
                    public void onChange(boolean selfChange) {
                        if (DeviceCapabilityListener.this.mImsMmTelManager == null) {
                            DeviceCapabilityListener.this.logw("SimInfo change error: MmTelManager is null");
                            return;
                        }
                        try {
                            boolean isEnabled = DeviceCapabilityListener.this.mImsMmTelManager.isVtSettingEnabled();
                            DeviceCapabilityListener.this.handleVtSettingChanged(isEnabled);
                        } catch (RuntimeException e) {
                            DeviceCapabilityListener.this.logw("SimInfo change error: " + e);
                        }
                    }
                };
            }
            contentObserver = this.mSimInfoContentObserver;
        }
        return contentObserver;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: getImsMmTelManager */
    public ImsMmTelManager lambda$new$0(int subId) {
        try {
            ImsManager imsManager = (ImsManager) this.mContext.getSystemService(ImsManager.class);
            if (imsManager == null) {
                return null;
            }
            return imsManager.getImsMmTelManager(subId);
        } catch (IllegalArgumentException e) {
            logw("getImsMmTelManager error: " + e.getMessage());
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: getImsRcsManager */
    public ImsRcsManager lambda$new$1(int subId) {
        try {
            ImsManager imsManager = (ImsManager) this.mContext.getSystemService(ImsManager.class);
            if (imsManager == null) {
                return null;
            }
            return imsManager.getImsRcsManager(subId);
        } catch (IllegalArgumentException e) {
            logw("getImsRcsManager error: " + e.getMessage());
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleTtyPreferredModeChanged(int preferredMode) {
        boolean isChanged = this.mCapabilityInfo.updateTtyPreferredMode(preferredMode);
        logi("TTY preferred mode changed: " + preferredMode + ", isChanged=" + isChanged);
        if (isChanged) {
            this.mHandler.sendTriggeringPublishMessage(3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMobileDataChanged(boolean isEnabled) {
        boolean isChanged = this.mCapabilityInfo.updateMobileData(isEnabled);
        logi("Mobile data changed: " + isEnabled + ", isChanged=" + isChanged);
        if (isChanged) {
            this.mHandler.sendTriggeringPublishMessage(4);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleVtSettingChanged(boolean isEnabled) {
        boolean isChanged = this.mCapabilityInfo.updateVtSetting(isEnabled);
        logi("VT setting changed: " + isEnabled + ", isChanged=" + isChanged);
        if (isChanged) {
            this.mHandler.sendTriggeringPublishMessage(5);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleImsMmtelRegistered(int imsTransportType) {
        this.mCapabilityInfo.updateImsMmtelRegistered(imsTransportType);
        this.mHandler.sendTriggeringPublishMessage(6);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleImsMmtelUnregistered() {
        boolean hasChanged = this.mCapabilityInfo.updateImsMmtelUnregistered();
        handleMmTelSubscriberAssociatedUriChanged(null, hasChanged);
        if (!this.mCapabilityInfo.isImsRegistered()) {
            this.mHandler.sendImsUnregisteredMessage();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMmTelSubscriberAssociatedUriChanged(Uri[] uris, boolean regiChanged) {
        Uri originalUri = this.mCapabilityInfo.getMmtelAssociatedUri();
        this.mCapabilityInfo.updateMmTelAssociatedUri(uris);
        Uri currentUri = this.mCapabilityInfo.getMmtelAssociatedUri();
        boolean hasChanged = regiChanged || !Objects.equals(originalUri, currentUri);
        logi("handleMmTelSubscriberAssociatedUriChanged: hasChanged=" + hasChanged);
        if (this.mCapabilityInfo.isImsRegistered() && hasChanged) {
            this.mHandler.sendTriggeringPublishMessage(9);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMmtelCapabilitiesStatusChanged(MmTelFeature.MmTelCapabilities capabilities) {
        boolean isChanged = this.mCapabilityInfo.updateMmtelCapabilitiesChanged(capabilities);
        logi("MMTel capabilities status changed: isChanged=" + isChanged);
        if (isChanged) {
            this.mHandler.sendTriggeringPublishMessage(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleImsRcsRegistered(ImsRegistrationAttributes attr) {
        if (this.mCapabilityInfo.updateImsRcsRegistered(attr)) {
            this.mHandler.sendTriggeringPublishMessage(10);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleImsRcsUnregistered() {
        boolean hasChanged = this.mCapabilityInfo.updateImsRcsUnregistered();
        handleRcsSubscriberAssociatedUriChanged(null, hasChanged);
        if (!this.mCapabilityInfo.isImsRegistered()) {
            this.mHandler.sendImsUnregisteredMessage();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRcsSubscriberAssociatedUriChanged(Uri[] uris, boolean regiChanged) {
        Uri originalUri = this.mCapabilityInfo.getRcsAssociatedUri();
        this.mCapabilityInfo.updateRcsAssociatedUri(uris);
        Uri currentUri = this.mCapabilityInfo.getRcsAssociatedUri();
        boolean hasChanged = regiChanged || !Objects.equals(originalUri, currentUri);
        logi("handleRcsSubscriberAssociatedUriChanged: hasChanged=" + hasChanged);
        if (this.mCapabilityInfo.isImsRegistered() && hasChanged) {
            this.mHandler.sendTriggeringPublishMessage(12);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleProvisioningChanged() {
        this.mHandler.sendTriggeringPublishMessage(13);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePublishThrottleChanged(int value) {
        this.mCallback.updatePublishThrottle(value);
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    public void setImsMmTelManagerFactory(ImsMmTelManagerFactory factory) {
        this.mImsMmTelManagerFactory = factory;
    }

    public void setImsRcsManagerFactory(ImsRcsManagerFactory factory) {
        this.mImsRcsManagerFactory = factory;
    }

    public void setProvisioningMgrFactory(ProvisioningManagerFactory factory) {
        this.mProvisioningMgrFactory = factory;
    }

    public void setImsCallbackRegistered(boolean registered) {
        this.mIsImsCallbackRegistered = registered;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logd(String log) {
        Log.d(LOG_TAG, getLogPrefix().append(log).toString());
        this.mLocalLog.log("[D] " + log);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logi(String log) {
        Log.i(LOG_TAG, getLogPrefix().append(log).toString());
        this.mLocalLog.log("[I] " + log);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logw(String log) {
        Log.w(LOG_TAG, getLogPrefix().append(log).toString());
        this.mLocalLog.log("[W] " + log);
    }

    private StringBuilder getLogPrefix() {
        StringBuilder builder = new StringBuilder("[");
        builder.append(this.mSubId);
        builder.append("] ");
        return builder;
    }

    public void dump(PrintWriter printWriter) {
        PrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println("DeviceCapListener[subId: " + this.mSubId + "]:");
        indentingPrintWriter.increaseIndent();
        this.mCapabilityInfo.dump(indentingPrintWriter);
        indentingPrintWriter.println("Log:");
        indentingPrintWriter.increaseIndent();
        this.mLocalLog.dump(indentingPrintWriter);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("---");
        indentingPrintWriter.decreaseIndent();
    }
}
