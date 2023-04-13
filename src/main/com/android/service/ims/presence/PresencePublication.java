package com.android.service.ims.presence;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.ims.RcsContactPresenceTuple;
import android.telephony.ims.RcsContactUceCapability;
import android.telephony.ims.feature.MmTelFeature;
import android.text.TextUtils;
import com.android.ims.internal.ContactNumberUtils;
import com.android.ims.internal.Logger;
import com.android.ims.rcs.uce.presence.pidfparser.PidfParserConstant;
import com.android.ims.rcs.uce.presence.pidfparser.capabilities.Duplex;
import com.android.ims.rcs.uce.presence.pidfparser.pidf.Basic;
import com.android.ims.rcs.uce.util.NetworkSipCode;
import com.android.service.ims.RcsSettingUtils;
import com.android.service.ims.Task;
import com.android.service.ims.TaskManager;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public class PresencePublication extends PresenceBase {
    public static final String ACTION_RETRY_PUBLISH_ALARM = "com.android.service.ims.presence.retry.publish";
    private static final String DOMAIN_SEPARATOR = "@";
    private static final int MESSAGE_DEFAULT_SUBSCRIPTION_CHANGED = 2;
    private static final int MESSAGE_RCS_PUBLISH_REQUEST = 1;
    private static final String SIP_SCHEME = "sip";
    private static final String TEL_SCHEME = "tel";
    private static final int TIMEOUT_CHECK_SUBSCRIPTION_READY_MS = 5000;
    public static final int UCE_PRES_PUBLISH_TRIGGER_ETAG_EXPIRED = 0;
    public static final int UCE_PRES_PUBLISH_TRIGGER_MOVE_TO_2G = 6;
    public static final int UCE_PRES_PUBLISH_TRIGGER_MOVE_TO_3G = 5;
    public static final int UCE_PRES_PUBLISH_TRIGGER_MOVE_TO_EHRPD = 3;
    public static final int UCE_PRES_PUBLISH_TRIGGER_MOVE_TO_HSPAPLUS = 4;
    public static final int UCE_PRES_PUBLISH_TRIGGER_MOVE_TO_IWLAN = 8;
    public static final int UCE_PRES_PUBLISH_TRIGGER_MOVE_TO_LTE_VOPS_DISABLED = 1;
    public static final int UCE_PRES_PUBLISH_TRIGGER_MOVE_TO_LTE_VOPS_ENABLED = 2;
    public static final int UCE_PRES_PUBLISH_TRIGGER_MOVE_TO_WLAN = 7;
    public static final int UCE_PRES_PUBLISH_TRIGGER_UNKNOWN = 9;
    private static PresencePublication sPresencePublication = null;
    private Logger logger;
    private AlarmManager mAlarmManager;
    private int mAssociatedSubscription;
    boolean mCancelRetry;
    private final String[] mConfigRcsProvisionErrorOnPublishResponse;
    private final String[] mConfigVolteProvisionErrorOnPublishResponse;
    private boolean mDataEnabled;
    private boolean mDonotRetryUntilPowerCycle;
    private boolean mGotTriggerFromStack;
    private boolean mHasCachedTrigger;
    private boolean mImsRegistered;
    boolean mIsViWifiAvailable;
    boolean mIsVoWifiAvailable;
    boolean mIsVolteAvailable;
    boolean mIsVtAvailable;
    boolean mMovedToIWLAN;
    boolean mMovedToLTE;
    private Handler mMsgHandler;
    volatile PublishRequest mPendingRequest;
    boolean mPendingRetry;
    private int mPreferredTtyMode;
    private PresencePublisher mPresencePublisher;
    volatile PublishRequest mPublishedRequest;
    volatile PublishRequest mPublishingRequest;
    private PendingIntent mRetryAlarmIntent;
    private boolean mSimLoaded;
    private PresenceSubscriber mSubscriber;
    private final Object mSyncObj;
    boolean mVoPSEnabled;
    private boolean mVtEnabled;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface StackPublishTriggerType {
    }

    /* loaded from: classes.dex */
    public class PublishType {
        public static final int PRES_PUBLISH_TRIGGER_CACHED_TRIGGER = 2;
        public static final int PRES_PUBLISH_TRIGGER_DATA_CHANGED = 0;
        public static final int PRES_PUBLISH_TRIGGER_DEFAULT_SUB_CHANGED = 6;
        public static final int PRES_PUBLISH_TRIGGER_FEATURE_AVAILABILITY_CHANGED = 5;
        public static final int PRES_PUBLISH_TRIGGER_RETRY = 4;
        public static final int PRES_PUBLISH_TRIGGER_TTY_ENABLE_STATUS = 3;
        public static final int PRES_PUBLISH_TRIGGER_VTCALL_CHANGED = 1;

        public PublishType() {
        }
    }

    public PresencePublication(PresencePublisher presencePublisher, Context context, String[] configVolteProvisionErrorOnPublishResponse, String[] configRcsProvisionErrorOnPublishResponse) {
        super(context);
        this.logger = Logger.getLogger(getClass().getName());
        Object obj = new Object();
        this.mSyncObj = obj;
        this.mMovedToIWLAN = false;
        this.mMovedToLTE = false;
        this.mVoPSEnabled = false;
        this.mIsVolteAvailable = false;
        this.mIsVtAvailable = false;
        this.mIsVoWifiAvailable = false;
        this.mIsViWifiAvailable = false;
        this.mPendingRequest = null;
        this.mPublishingRequest = null;
        this.mPublishedRequest = null;
        this.mMsgHandler = new Handler(Looper.getMainLooper()) { // from class: com.android.service.ims.presence.PresencePublication.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                PresencePublication.this.logger.debug("Thread=" + Thread.currentThread().getName() + " received " + msg);
                if (msg == null) {
                    PresencePublication.this.logger.error("msg=null");
                    return;
                }
                switch (msg.what) {
                    case 1:
                        PresencePublication.this.logger.debug("handleMessage  msg=RCS_PUBLISH_REQUEST:");
                        PublishRequest publishRequest = (PublishRequest) msg.obj;
                        synchronized (PresencePublication.this.mSyncObj) {
                            PresencePublication.this.mPendingRequest = null;
                        }
                        PresencePublication.this.doPublish(publishRequest);
                        return;
                    case 2:
                        PresencePublication.this.requestPublishIfSubscriptionReady();
                        return;
                    default:
                        PresencePublication.this.logger.debug("handleMessage unknown msg=" + msg.what);
                        return;
                }
            }
        };
        this.mSubscriber = null;
        this.mHasCachedTrigger = false;
        this.mGotTriggerFromStack = false;
        this.mDonotRetryUntilPowerCycle = false;
        this.mSimLoaded = false;
        this.mPreferredTtyMode = 0;
        this.mImsRegistered = false;
        this.mVtEnabled = false;
        this.mDataEnabled = false;
        this.mAssociatedSubscription = -1;
        this.mRetryAlarmIntent = null;
        this.mAlarmManager = null;
        this.mCancelRetry = true;
        this.mPendingRetry = false;
        this.logger.debug("PresencePublication constrcuct");
        synchronized (obj) {
            this.mPresencePublisher = presencePublisher;
        }
        this.mConfigVolteProvisionErrorOnPublishResponse = configVolteProvisionErrorOnPublishResponse;
        this.mConfigRcsProvisionErrorOnPublishResponse = configRcsProvisionErrorOnPublishResponse;
        this.mVtEnabled = RcsSettingUtils.isVtEnabledByUser(this.mAssociatedSubscription);
        this.mDataEnabled = Settings.Global.getInt(this.mContext.getContentResolver(), "mobile_data", 1) == 1;
        this.logger.debug("The current mobile data is: " + (this.mDataEnabled ? "enabled" : "disabled"));
        TelecomManager tm = (TelecomManager) this.mContext.getSystemService(TelecomManager.class);
        this.mPreferredTtyMode = tm.getCurrentTtyMode();
        this.logger.debug("The current TTY mode is: " + this.mPreferredTtyMode);
        sPresencePublication = this;
    }

    public void updatePresencePublisher(PresencePublisher presencePublisher) {
        synchronized (this.mSyncObj) {
            this.logger.debug("Update PresencePublisher");
            this.mPresencePublisher = presencePublisher;
        }
    }

    public void removePresencePublisher() {
        synchronized (this.mSyncObj) {
            this.logger.debug("Remove PresencePublisher");
            this.mPresencePublisher = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestPublishIfSubscriptionReady() {
        if (!SubscriptionManager.isValidSubscriptionId(this.mAssociatedSubscription)) {
            this.logger.print("subscription changed to invalid, setting to not published");
            reset();
            this.mSimLoaded = false;
            setPublishState(1);
        } else if (isSimLoaded()) {
            this.logger.print("subscription ready, requesting publish");
            this.mSimLoaded = true;
            this.mDonotRetryUntilPowerCycle = false;
            requestLocalPublish(6);
        } else {
            this.mMsgHandler.removeMessages(2);
            Handler handler = this.mMsgHandler;
            handler.sendMessageDelayed(handler.obtainMessage(2), 5000L);
        }
    }

    private boolean isSimLoaded() {
        TelephonyManager teleMgr = (TelephonyManager) this.mContext.getSystemService("phone");
        if (teleMgr == null) {
            return false;
        }
        TelephonyManager teleMgr2 = teleMgr.createForSubscriptionId(this.mAssociatedSubscription);
        String[] myImpu = teleMgr2.getIsimImpu();
        String myDomain = teleMgr2.getIsimDomain();
        String line1Number = teleMgr2.getLine1Number();
        return (TextUtils.isEmpty(line1Number) && (TextUtils.isEmpty(myDomain) || myImpu == null || myImpu.length == 0)) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isIPVoiceSupported(boolean volteAvailable, boolean voWifiAvailable) {
        boolean z = false;
        if (!RcsSettingUtils.isVoLteSupported(this.mAssociatedSubscription) && !RcsSettingUtils.isVoWiFiSupported(this.mAssociatedSubscription)) {
            this.logger.print("Disabled by platform, voiceSupported=false");
            return false;
        } else if (!RcsSettingUtils.isVoLteProvisioned(this.mAssociatedSubscription) && !RcsSettingUtils.isVowifiProvisioned(this.mAssociatedSubscription)) {
            this.logger.print("Wasn't provisioned, voiceSupported=false");
            return false;
        } else if (!RcsSettingUtils.isAdvancedCallingEnabledByUser(this.mAssociatedSubscription) && !RcsSettingUtils.isWfcEnabledByUser(this.mAssociatedSubscription)) {
            this.logger.print("User didn't enable volte or wfc, voiceSupported=false");
            return false;
        } else if (isOnIWLAN()) {
            if (volteAvailable || voWifiAvailable) {
                z = true;
            }
            boolean voiceSupported = z;
            this.logger.print("on IWLAN, voiceSupported=" + voiceSupported);
            return voiceSupported;
        } else if (!isOnLTE()) {
            this.logger.print("isOnLTE=false, voiceSupported=false");
            return false;
        } else if (!this.mVoPSEnabled) {
            this.logger.print("mVoPSEnabled=false, voiceSupported=false");
            return false;
        } else {
            this.logger.print("voiceSupported=true");
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isIPVideoSupported(boolean vtAvailable, boolean viWifiAvailable) {
        boolean z = false;
        if (!RcsSettingUtils.isVoLteSupported(this.mAssociatedSubscription) || !RcsSettingUtils.isVtSupported(this.mAssociatedSubscription)) {
            this.logger.print("Disabled by platform, videoSupported=false");
            return false;
        } else if (!RcsSettingUtils.isVoLteProvisioned(this.mAssociatedSubscription) || !RcsSettingUtils.isLvcProvisioned(this.mAssociatedSubscription)) {
            this.logger.print("Not provisioned. videoSupported=false");
            return false;
        } else if (!RcsSettingUtils.isAdvancedCallingEnabledByUser(this.mAssociatedSubscription) || !this.mVtEnabled) {
            this.logger.print("User disabled volte or vt, videoSupported=false");
            return false;
        } else if (isTtyOn()) {
            this.logger.print("isTtyOn=true, videoSupported=false");
            return false;
        } else if (isOnIWLAN()) {
            if (vtAvailable || viWifiAvailable) {
                z = true;
            }
            boolean videoSupported = z;
            this.logger.print("on IWLAN, videoSupported=" + videoSupported);
            return videoSupported;
        } else if (!isDataEnabled()) {
            this.logger.print("isDataEnabled()=false, videoSupported=false");
            return false;
        } else if (!isOnLTE()) {
            this.logger.print("isOnLTE=false, videoSupported=false");
            return false;
        } else if (this.mVoPSEnabled) {
            return true;
        } else {
            this.logger.print("mVoPSEnabled=false, videoSupported=false");
            return false;
        }
    }

    public void onTtyPreferredModeChanged(int newTtyPreferredMode) {
        this.logger.debug("Tty mode changed from " + this.mPreferredTtyMode + " to " + newTtyPreferredMode);
        boolean mIsTtyEnabled = isTtyEnabled(this.mPreferredTtyMode);
        boolean isTtyEnabled = isTtyEnabled(newTtyPreferredMode);
        this.mPreferredTtyMode = newTtyPreferredMode;
        if (mIsTtyEnabled != isTtyEnabled) {
            this.logger.print("ttyEnabled status changed from " + mIsTtyEnabled + " to " + isTtyEnabled);
            requestLocalPublish(3);
        }
    }

    public void onAirplaneModeChanged(boolean isAirplaneModeEnabled) {
        if (isAirplaneModeEnabled) {
            this.logger.print("Airplane mode, set to PUBLISH_STATE_NOT_PUBLISHED");
            reset();
            setPublishState(1);
        }
    }

    public boolean isTtyOn() {
        this.logger.debug("isTtyOn settingsTtyMode=" + this.mPreferredTtyMode);
        return isTtyEnabled(this.mPreferredTtyMode);
    }

    public void onImsConnected() {
        this.mImsRegistered = true;
    }

    public void onImsDisconnected() {
        this.logger.debug("reset PUBLISH status for IMS had been disconnected");
        this.mImsRegistered = false;
        reset();
    }

    private void reset() {
        this.mIsVolteAvailable = false;
        this.mIsVtAvailable = false;
        this.mIsVoWifiAvailable = false;
        this.mIsViWifiAvailable = false;
        synchronized (this.mSyncObj) {
            this.mPendingRequest = null;
            this.mPublishingRequest = null;
            this.mPublishedRequest = null;
        }
    }

    public void handleAssociatedSubscriptionChanged(int newSubId) {
        if (this.mAssociatedSubscription == newSubId) {
            return;
        }
        reset();
        this.mAssociatedSubscription = newSubId;
        this.mMsgHandler.removeMessages(2);
        Handler handler = this.mMsgHandler;
        handler.sendMessage(handler.obtainMessage(2));
    }

    public void handleProvisioningChanged() {
        if (RcsSettingUtils.isEabProvisioned(this.mContext, this.mAssociatedSubscription)) {
            this.logger.debug("provisioned, set mDonotRetryUntilPowerCycle to false");
            this.mDonotRetryUntilPowerCycle = false;
            if (this.mHasCachedTrigger) {
                requestLocalPublish(2);
            }
        }
    }

    public static PresencePublication getPresencePublication() {
        return sPresencePublication;
    }

    public void setSubscriber(PresenceSubscriber subscriber) {
        this.mSubscriber = subscriber;
    }

    public boolean isDataEnabled() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "mobile_data", 1) == 1;
    }

    public void onMobileDataChanged(boolean value) {
        this.logger.print("onMobileDataChanged, mDataEnabled=" + this.mDataEnabled + " value=" + value);
        if (this.mDataEnabled != value) {
            this.mDataEnabled = value;
            requestLocalPublish(0);
        }
    }

    public void onVtEnabled(boolean enabled) {
        this.logger.debug("onVtEnabled mVtEnabled=" + this.mVtEnabled + " enabled=" + enabled);
        if (this.mVtEnabled != enabled) {
            this.mVtEnabled = enabled;
            requestLocalPublish(1);
        }
    }

    @Override // com.android.service.ims.presence.PresenceBase
    public void onCommandStatusUpdated(int taskId, int requestId, int resultCode) {
        this.logger.info("onCommandStatusUpdated: resultCode= " + resultCode);
        super.onCommandStatusUpdated(taskId, requestId, resultCode);
    }

    private boolean isPublishedOrPublishing() {
        long publishThreshold = RcsSettingUtils.getPublishThrottle(this.mAssociatedSubscription);
        boolean publishing = this.mPublishingRequest != null && System.currentTimeMillis() - this.mPublishingRequest.getTimestamp() <= publishThreshold;
        return getPublishState() == 0 || publishing;
    }

    public int getPublishState() {
        PresencePublisher presencePublisher;
        synchronized (this.mSyncObj) {
            presencePublisher = this.mPresencePublisher;
        }
        if (presencePublisher != null) {
            return presencePublisher.getPublisherState();
        }
        return 1;
    }

    public void setPublishState(int publishState) {
        PresencePublisher presencePublisher;
        synchronized (this.mSyncObj) {
            presencePublisher = this.mPresencePublisher;
        }
        if (presencePublisher != null) {
            presencePublisher.updatePublisherState(publishState);
        }
    }

    private void requestLocalPublish(int trigger) {
        boolean bForceToNetwork = true;
        switch (trigger) {
            case 0:
                this.logger.print("PRES_PUBLISH_TRIGGER_DATA_CHANGED");
                bForceToNetwork = false;
                break;
            case 1:
                this.logger.print("PRES_PUBLISH_TRIGGER_VTCALL_CHANGED");
                bForceToNetwork = true;
                break;
            case 2:
                this.logger.print("PRES_PUBLISH_TRIGGER_CACHED_TRIGGER");
                break;
            case 3:
                this.logger.print("PRES_PUBLISH_TRIGGER_TTY_ENABLE_STATUS");
                bForceToNetwork = true;
                break;
            case 4:
                this.logger.print("PRES_PUBLISH_TRIGGER_RETRY");
                break;
            case 5:
                bForceToNetwork = false;
                this.logger.print("PRES_PUBLISH_TRIGGER_FEATURE_AVAILABILITY_CHANGED");
                break;
            case 6:
                this.logger.print("PRES_PUBLISH_TRIGGER_DEFAULT_SUB_CHANGED");
                bForceToNetwork = true;
                break;
            default:
                this.logger.print("Unknown publish trigger from AP");
                break;
        }
        if (!this.mGotTriggerFromStack) {
            this.logger.print("Didn't get trigger from stack yet, discard framework trigger.");
        } else if (this.mDonotRetryUntilPowerCycle) {
            this.logger.print("Don't publish until next power cycle");
        } else if (!this.mSimLoaded) {
            this.logger.print("invokePublish cache the trigger since the SIM is not ready");
            this.mHasCachedTrigger = true;
        } else if (!RcsSettingUtils.isEabProvisioned(this.mContext, this.mAssociatedSubscription)) {
            this.logger.print("invokePublish cache the trigger, not provision yet");
            this.mHasCachedTrigger = true;
        } else {
            PublishRequest publishRequest = new PublishRequest(bForceToNetwork, System.currentTimeMillis());
            requestPublication(publishRequest);
        }
    }

    public void onStackPublishRequested(int publishTriggerType) {
        this.mGotTriggerFromStack = true;
        switch (publishTriggerType) {
            case 0:
                this.logger.print("PUBLISH_TRIGGER_ETAG_EXPIRED");
                break;
            case 1:
                this.logger.print("PUBLISH_TRIGGER_MOVE_TO_LTE_VOPS_DISABLED");
                this.mMovedToLTE = true;
                this.mVoPSEnabled = false;
                this.mMovedToIWLAN = false;
                this.mImsRegistered = true;
                break;
            case 2:
                this.logger.print("PUBLISH_TRIGGER_MOVE_TO_LTE_VOPS_ENABLED");
                this.mMovedToLTE = true;
                this.mVoPSEnabled = true;
                this.mMovedToIWLAN = false;
                this.mImsRegistered = true;
                break;
            case 3:
                this.logger.print("PUBLISH_TRIGGER_MOVE_TO_EHRPD");
                this.mMovedToLTE = false;
                this.mVoPSEnabled = false;
                this.mMovedToIWLAN = false;
                this.mImsRegistered = true;
                break;
            case 4:
                this.logger.print("PUBLISH_TRIGGER_MOVE_TO_HSPAPLUS");
                this.mMovedToLTE = false;
                this.mVoPSEnabled = false;
                this.mMovedToIWLAN = false;
                break;
            case 5:
                this.logger.print("PUBLISH_TRIGGER_MOVE_TO_3G");
                this.mMovedToLTE = false;
                this.mVoPSEnabled = false;
                this.mMovedToIWLAN = false;
                break;
            case 6:
                this.logger.print("PUBLISH_TRIGGER_MOVE_TO_2G");
                this.mMovedToLTE = false;
                this.mVoPSEnabled = false;
                this.mMovedToIWLAN = false;
                break;
            case 7:
            default:
                this.logger.print("Unknow Publish Trigger Type");
                break;
            case 8:
                this.logger.print("QRCS_PRES_PUBLISH_TRIGGER_MOVE_TO_IWLAN");
                this.mMovedToLTE = false;
                this.mVoPSEnabled = false;
                this.mMovedToIWLAN = true;
                this.mImsRegistered = true;
                break;
        }
        if (this.mDonotRetryUntilPowerCycle) {
            this.logger.print("Don't publish until next power cycle");
        } else if (!this.mSimLoaded) {
            this.logger.print("invokePublish cache the trigger since the SIM is not ready");
            this.mHasCachedTrigger = true;
        } else if (!RcsSettingUtils.isEabProvisioned(this.mContext, this.mAssociatedSubscription)) {
            this.logger.print("invokePublish cache the trigger, not provision yet");
            this.mHasCachedTrigger = true;
        } else {
            PublishRequest publishRequest = new PublishRequest(true, System.currentTimeMillis());
            requestPublication(publishRequest);
        }
    }

    public void onStackAvailable() {
        if (this.mHasCachedTrigger) {
            requestLocalPublish(2);
        }
    }

    /* loaded from: classes.dex */
    public class PublishRequest {
        private long mCurrentTime;
        private boolean mForceToNetwork;
        private boolean mVolteCapable = false;
        private boolean mVtCapable = false;

        PublishRequest(boolean bForceToNetwork, long currentTime) {
            refreshPublishContent();
            this.mForceToNetwork = bForceToNetwork;
            this.mCurrentTime = currentTime;
        }

        public void refreshPublishContent() {
            PresencePublication presencePublication = PresencePublication.this;
            setVolteCapable(presencePublication.isIPVoiceSupported(presencePublication.mIsVolteAvailable, PresencePublication.this.mIsVoWifiAvailable));
            PresencePublication presencePublication2 = PresencePublication.this;
            setVtCapable(presencePublication2.isIPVideoSupported(presencePublication2.mIsVtAvailable, PresencePublication.this.mIsViWifiAvailable));
        }

        public boolean getForceToNetwork() {
            return this.mForceToNetwork;
        }

        public void setForceToNetwork(boolean bForceToNetwork) {
            this.mForceToNetwork = bForceToNetwork;
        }

        public long getTimestamp() {
            return this.mCurrentTime;
        }

        public void setTimestamp(long currentTime) {
            this.mCurrentTime = currentTime;
        }

        public void setVolteCapable(boolean capable) {
            this.mVolteCapable = capable;
        }

        public void setVtCapable(boolean capable) {
            this.mVtCapable = capable;
        }

        public boolean getVolteCapable() {
            return this.mVolteCapable;
        }

        public boolean getVtCapable() {
            return this.mVtCapable;
        }

        public boolean hasSamePublishContent(PublishRequest request) {
            if (request != null) {
                return this.mVolteCapable == request.getVolteCapable() && this.mVtCapable == request.getVtCapable();
            }
            PresencePublication.this.logger.error("request == null");
            return false;
        }

        public String toString() {
            return "mForceToNetwork=" + this.mForceToNetwork + " mCurrentTime=" + this.mCurrentTime + " mVolteCapable=" + this.mVolteCapable + " mVtCapable=" + this.mVtCapable;
        }
    }

    private void requestPublication(PublishRequest publishRequest) {
        if (publishRequest == null) {
            this.logger.error("Invalid parameter publishRequest == null");
            return;
        }
        long currentTime = System.currentTimeMillis();
        synchronized (this.mSyncObj) {
            if (this.mPendingRequest != null && currentTime - this.mPendingRequest.getTimestamp() <= 2000) {
                this.logger.print("A publish is pending, update the pending request and discard this one");
                if (publishRequest.getForceToNetwork() && !this.mPendingRequest.getForceToNetwork()) {
                    this.mPendingRequest.setForceToNetwork(true);
                }
                this.mPendingRequest.setTimestamp(publishRequest.getTimestamp());
                return;
            }
            this.mPendingRequest = publishRequest;
            Message publishMessage = this.mMsgHandler.obtainMessage(1, this.mPendingRequest);
            this.mMsgHandler.sendMessageDelayed(publishMessage, 2000L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doPublish(PublishRequest publishRequest) {
        PresencePublisher presencePublisher;
        if (publishRequest == null) {
            this.logger.error("publishRequest == null");
            return;
        }
        synchronized (this.mSyncObj) {
            presencePublisher = this.mPresencePublisher;
        }
        if (presencePublisher == null) {
            this.logger.error("mPresencePublisher == null");
        } else if (!this.mImsRegistered) {
            this.logger.error("IMS wasn't registered");
        } else {
            if (this.mPendingRetry) {
                this.mPendingRetry = false;
                this.mAlarmManager.cancel(this.mRetryAlarmIntent);
            }
            synchronized (this.mSyncObj) {
                publishRequest.refreshPublishContent();
            }
            this.logger.print("publishRequest=" + publishRequest);
            if (!publishRequest.getForceToNetwork() && isPublishedOrPublishing() && ((publishRequest.hasSamePublishContent(this.mPublishingRequest) || (this.mPublishingRequest == null && publishRequest.hasSamePublishContent(this.mPublishedRequest))) && getPublishState() != 1)) {
                this.logger.print("Don't need publish since the capability didn't change publishRequest " + publishRequest + " getPublishState()=" + getPublishState());
                return;
            }
            if (isPublishedOrPublishing()) {
                if (this.mPendingRetry) {
                    this.logger.print("Pending a retry");
                    return;
                }
                long publishThreshold = RcsSettingUtils.getPublishThrottle(this.mAssociatedSubscription);
                long passed = publishThreshold;
                if (this.mPublishingRequest != null) {
                    passed = System.currentTimeMillis() - this.mPublishingRequest.getTimestamp();
                } else if (this.mPublishedRequest != null) {
                    passed = System.currentTimeMillis() - this.mPublishedRequest.getTimestamp();
                }
                long left = publishThreshold - (passed >= 0 ? passed : publishThreshold);
                long left2 = left <= 120000 ? left : 120000L;
                if (left2 > 0) {
                    scheduleRetryPublish(left2);
                    return;
                }
            }
            if (!RcsSettingUtils.isAdvancedCallingEnabledByUser(this.mAssociatedSubscription) && getPublishState() != 1) {
                reset();
                return;
            }
            TelephonyManager teleMgr = (TelephonyManager) this.mContext.getSystemService("phone");
            if (teleMgr.createForSubscriptionId(this.mAssociatedSubscription) == null) {
                this.logger.error("TelephonyManager not available.");
                return;
            }
            Uri myUri = getUriForPublication();
            if (myUri == null) {
                this.logger.error("doPublish, myUri is null");
                return;
            }
            boolean isVolteCapble = publishRequest.getVolteCapable();
            boolean isVtCapable = publishRequest.getVtCapable();
            RcsContactUceCapability presenceInfo = getRcsContactUceCapability(myUri, isVolteCapble, isVtCapable);
            synchronized (this.mSyncObj) {
                this.mPublishingRequest = publishRequest;
                this.mPublishingRequest.setTimestamp(System.currentTimeMillis());
            }
            String myNumber = getNumberFromUri(myUri);
            int taskId = TaskManager.getDefault().addPublishTask(myNumber);
            this.logger.print("doPublish, uri=" + myUri + ", myNumber=" + myNumber + ", taskId=" + taskId);
            int ret = presencePublisher.requestPublication(presenceInfo, myUri.toString(), taskId);
            if (ret != 0) {
                this.logger.print("doPublish, task=" + taskId + " failed with code=" + ret);
                TaskManager.getDefault().removeTask(taskId);
            }
            this.mHasCachedTrigger = ret == -3;
        }
    }

    private RcsContactUceCapability getRcsContactUceCapability(Uri contact, boolean isVolteCapable, boolean isVtCapable) {
        RcsContactPresenceTuple.ServiceCapabilities.Builder servCapsBuilder = new RcsContactPresenceTuple.ServiceCapabilities.Builder(isVolteCapable, isVtCapable);
        servCapsBuilder.addSupportedDuplexMode(Duplex.DUPLEX_FULL);
        RcsContactPresenceTuple.Builder tupleBuilder = new RcsContactPresenceTuple.Builder(Basic.OPEN, PidfParserConstant.SERVICE_ID_IpCall, "1.0");
        tupleBuilder.setContactUri(contact).setServiceCapabilities(servCapsBuilder.build());
        RcsContactUceCapability.PresenceBuilder presenceBuilder = new RcsContactUceCapability.PresenceBuilder(contact, 1, 3);
        presenceBuilder.addCapabilityTuple(tupleBuilder.build());
        return presenceBuilder.build();
    }

    private String getNumberFromUri(Uri uri) {
        if (uri == null) {
            return null;
        }
        String number = uri.getSchemeSpecificPart();
        String[] numberParts = number.split("[@;:]");
        if (numberParts.length == 0) {
            this.logger.error("getNumberFromUri: invalid uri=" + uri);
            return null;
        }
        return numberParts[0];
    }

    private Uri getUriForPublication() {
        String[] impu;
        TelephonyManager teleMgr = (TelephonyManager) this.mContext.getSystemService("phone");
        if (teleMgr == null) {
            this.logger.error("getUriForPublication, teleMgr = null");
            return null;
        }
        TelephonyManager teleMgr2 = teleMgr.createForSubscriptionId(this.mAssociatedSubscription);
        Uri myNumUri = null;
        String myDomain = teleMgr2.getIsimDomain();
        this.logger.debug("myDomain=" + myDomain);
        if (!TextUtils.isEmpty(myDomain) && (impu = teleMgr2.getIsimImpu()) != null) {
            int i = 0;
            while (true) {
                if (i >= impu.length) {
                    break;
                }
                this.logger.debug("impu[" + i + "]=" + impu[i]);
                if (TextUtils.isEmpty(impu[i])) {
                    i++;
                } else {
                    Uri impuUri = Uri.parse(impu[i]);
                    if (SIP_SCHEME.equals(impuUri.getScheme()) && impuUri.getSchemeSpecificPart().endsWith(myDomain)) {
                        myNumUri = impuUri;
                        this.logger.debug("impu[" + i + "] -> uri:" + myNumUri);
                    }
                }
            }
        }
        if (!TextUtils.isEmpty(myNumUri == null ? null : myNumUri.getSchemeSpecificPart())) {
            return myNumUri;
        }
        String myNumber = ContactNumberUtils.getDefault().format(teleMgr2.getLine1Number());
        if (myNumber == null) {
            return null;
        }
        return !TextUtils.isEmpty(myDomain) ? Uri.fromParts(SIP_SCHEME, myNumber + DOMAIN_SEPARATOR + myDomain, null) : Uri.fromParts(TEL_SCHEME, myNumber, null);
    }

    private void scheduleRetryPublish(long timeSpan) {
        this.logger.print("timeSpan=" + timeSpan + " mPendingRetry=" + this.mPendingRetry + " mCancelRetry=" + this.mCancelRetry);
        if (this.mPendingRetry) {
            this.logger.debug("There was a retry already");
            return;
        }
        this.mPendingRetry = true;
        this.mCancelRetry = false;
        Intent intent = new Intent(ACTION_RETRY_PUBLISH_ALARM);
        intent.setPackage(this.mContext.getPackageName());
        this.mRetryAlarmIntent = PendingIntent.getBroadcast(this.mContext, 0, intent, 201326592);
        if (this.mAlarmManager == null) {
            this.mAlarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        }
        this.mAlarmManager.setExact(2, SystemClock.elapsedRealtime() + timeSpan, this.mRetryAlarmIntent);
    }

    public void retryPublish() {
        this.logger.print("mCancelRetry=" + this.mCancelRetry);
        this.mPendingRetry = false;
        if (this.mCancelRetry) {
            return;
        }
        requestLocalPublish(4);
    }

    public void onSipResponse(int requestId, int responseCode, String reasonPhrase) {
        this.logger.print("Publish response code = " + responseCode + "Publish response reason phrase = " + reasonPhrase);
        synchronized (this.mSyncObj) {
            this.mPublishedRequest = this.mPublishingRequest;
            this.mPublishingRequest = null;
        }
        if (isInConfigList(responseCode, reasonPhrase, this.mConfigVolteProvisionErrorOnPublishResponse)) {
            this.logger.print("volte provision error. sipCode=" + responseCode + " phrase=" + reasonPhrase);
            setPublishState(2);
            this.mDonotRetryUntilPowerCycle = true;
            notifyDm();
        } else if (isInConfigList(responseCode, reasonPhrase, this.mConfigRcsProvisionErrorOnPublishResponse)) {
            this.logger.print("rcs provision error.sipCode=" + responseCode + " phrase=" + reasonPhrase);
            setPublishState(3);
            this.mDonotRetryUntilPowerCycle = true;
        } else {
            switch (responseCode) {
                case NetworkSipCode.SIP_CODE_OK /* 200 */:
                    setPublishState(0);
                    PresenceSubscriber presenceSubscriber = this.mSubscriber;
                    if (presenceSubscriber != null) {
                        presenceSubscriber.retryToGetAvailability();
                        break;
                    }
                    break;
                case NetworkSipCode.SIP_CODE_REQUEST_TIMEOUT /* 408 */:
                    setPublishState(4);
                    break;
                case 999:
                    this.logger.debug("Publish ignored - No capability change");
                    break;
                default:
                    if (responseCode < 100 || responseCode > 699) {
                        this.logger.debug("Ignore internal response code, sipCode=" + responseCode);
                        if (responseCode == 888) {
                            scheduleRetryPublish(120000L);
                            break;
                        } else {
                            this.logger.debug("Ignore internal response code, sipCode=" + responseCode);
                            break;
                        }
                    } else {
                        this.logger.debug("Generic Failure");
                        setPublishState(5);
                        if (responseCode >= 400 && responseCode <= 699) {
                            this.logger.debug("No Retry in OEM");
                            break;
                        }
                    }
                    break;
            }
            Task task = TaskManager.getDefault().getTaskByRequestId(requestId);
            if (task != null) {
                task.mSipResponseCode = responseCode;
                task.mSipReasonPhrase = reasonPhrase;
            }
            handleCallback(task, getPublishState(), false);
        }
    }

    private static boolean isTtyEnabled(int mode) {
        return mode != 0;
    }

    public void onFeatureCapabilityChanged(final int networkType, final MmTelFeature.MmTelCapabilities capabilities) {
        this.logger.debug("onFeatureCapabilityChanged networkType=" + networkType + ", capabilities=" + capabilities);
        Thread thread = new Thread(new Runnable() { // from class: com.android.service.ims.presence.PresencePublication$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                PresencePublication.this.lambda$onFeatureCapabilityChanged$0(networkType, capabilities);
            }
        }, "onFeatureCapabilityChangedInternal thread");
        thread.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onFeatureCapabilityChangedInternal */
    public synchronized void lambda$onFeatureCapabilityChanged$0(int networkType, MmTelFeature.MmTelCapabilities capabilities) {
        boolean oldIsVolteAvailable = this.mIsVolteAvailable;
        boolean oldIsVtAvailable = this.mIsVtAvailable;
        boolean oldIsVoWifiAvailable = this.mIsVoWifiAvailable;
        boolean oldIsViWifiAvailable = this.mIsViWifiAvailable;
        this.mIsVolteAvailable = networkType == 1 && capabilities.isCapable(1);
        this.mIsVoWifiAvailable = networkType == 2 && capabilities.isCapable(1);
        this.mIsVtAvailable = networkType == 1 && capabilities.isCapable(2);
        this.mIsViWifiAvailable = networkType == 2 && capabilities.isCapable(2);
        this.logger.print("mIsVolteAvailable=" + this.mIsVolteAvailable + " mIsVoWifiAvailable=" + this.mIsVoWifiAvailable + " mIsVtAvailable=" + this.mIsVtAvailable + " mIsViWifiAvailable=" + this.mIsViWifiAvailable + " oldIsVolteAvailable=" + oldIsVolteAvailable + " oldIsVoWifiAvailable=" + oldIsVoWifiAvailable + " oldIsVtAvailable=" + oldIsVtAvailable + " oldIsViWifiAvailable=" + oldIsViWifiAvailable);
        if (oldIsVolteAvailable != this.mIsVolteAvailable || oldIsVtAvailable != this.mIsVtAvailable || oldIsVoWifiAvailable != this.mIsVoWifiAvailable || oldIsViWifiAvailable != this.mIsViWifiAvailable) {
            if (this.mGotTriggerFromStack) {
                if (Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) != 0 && !this.mIsVoWifiAvailable && !this.mIsViWifiAvailable) {
                    this.logger.print("Airplane mode was on and no vowifi and viwifi. Don't need publish. Stack will unpublish");
                } else if (isOnIWLAN()) {
                    requestLocalPublish(5);
                }
            } else {
                this.mHasCachedTrigger = true;
            }
        }
    }

    private boolean isOnLTE() {
        TelephonyManager teleMgr = (TelephonyManager) this.mContext.getSystemService("phone");
        int networkType = teleMgr.getDataNetworkType();
        this.logger.debug("mMovedToLTE=" + this.mMovedToLTE + " networkType=" + networkType);
        return this.mMovedToLTE && networkType != 0;
    }

    private boolean isOnIWLAN() {
        TelephonyManager teleMgr = (TelephonyManager) this.mContext.getSystemService("phone");
        int networkType = teleMgr.getDataNetworkType();
        this.logger.debug("mMovedToIWLAN=" + this.mMovedToIWLAN + " networkType=" + networkType);
        return this.mMovedToIWLAN && networkType != 0;
    }
}
