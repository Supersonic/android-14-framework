package com.android.internal.telephony.imsphone;

import android.app.usage.NetworkStatsManager;
import android.compat.annotation.UnsupportedAppUsage;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.internal.telephony.sysprop.TelephonyProperties;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.NetworkStats;
import android.net.netstats.provider.NetworkStatsProvider;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telecom.Connection;
import android.telecom.TelecomManager;
import android.telecom.VideoProfile;
import android.telephony.CallQuality;
import android.telephony.CarrierConfigManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyLocalConnection;
import android.telephony.emergency.EmergencyNumber;
import android.telephony.ims.ImsCallProfile;
import android.telephony.ims.ImsCallSession;
import android.telephony.ims.ImsConferenceState;
import android.telephony.ims.ImsMmTelManager;
import android.telephony.ims.ImsReasonInfo;
import android.telephony.ims.ImsStreamMediaProfile;
import android.telephony.ims.ImsSuppServiceNotification;
import android.telephony.ims.MediaQualityStatus;
import android.telephony.ims.MediaThreshold;
import android.telephony.ims.ProvisioningManager;
import android.telephony.ims.RtpHeaderExtension;
import android.telephony.ims.SrvccCall;
import android.telephony.ims.aidl.IImsCallSessionListener;
import android.telephony.ims.aidl.IImsTrafficSessionCallback;
import android.telephony.ims.aidl.ISrvccStartedCallback;
import android.telephony.ims.feature.ConnectionFailureInfo;
import android.telephony.ims.feature.ImsFeature;
import android.telephony.ims.feature.MmTelFeature;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import android.util.SparseIntArray;
import com.android.ims.FeatureConnector;
import com.android.ims.ImsCall;
import com.android.ims.ImsEcbm;
import com.android.ims.ImsException;
import com.android.ims.ImsManager;
import com.android.ims.ImsUtInterface;
import com.android.ims.internal.ConferenceParticipant;
import com.android.ims.internal.IImsCallSession;
import com.android.ims.internal.IImsVideoCallProvider;
import com.android.ims.internal.ImsVideoCallProviderWrapper;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.Call;
import com.android.internal.telephony.CallFailCause;
import com.android.internal.telephony.CallStateException;
import com.android.internal.telephony.CallTracker;
import com.android.internal.telephony.CommandException;
import com.android.internal.telephony.IccCardConstants;
import com.android.internal.telephony.IndentingPrintWriter;
import com.android.internal.telephony.LocalLog;
import com.android.internal.telephony.LocaleTracker;
import com.android.internal.telephony.NetworkTypeController$$ExternalSyntheticLambda1;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.PhoneInternalInterface;
import com.android.internal.telephony.Registrant;
import com.android.internal.telephony.RegistrantList;
import com.android.internal.telephony.ServiceStateTracker;
import com.android.internal.telephony.SomeArgs;
import com.android.internal.telephony.SrvccConnection;
import com.android.internal.telephony.SubscriptionController;
import com.android.internal.telephony.d2d.RtpTransport;
import com.android.internal.telephony.data.DataSettingsManager;
import com.android.internal.telephony.domainselection.DomainSelectionResolver;
import com.android.internal.telephony.emergency.EmergencyNumberTracker;
import com.android.internal.telephony.emergency.EmergencyStateTracker;
import com.android.internal.telephony.gsm.SuppServiceNotification;
import com.android.internal.telephony.imsphone.ImsPhone;
import com.android.internal.telephony.imsphone.ImsPhoneCallTracker;
import com.android.internal.telephony.metrics.CallQualityMetrics;
import com.android.internal.telephony.metrics.TelephonyMetrics;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyEvent;
import com.android.internal.telephony.subscription.SubscriptionInfoInternal;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.internal.telephony.uicc.UiccCardApplication;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class ImsPhoneCallTracker extends CallTracker implements ImsPullCall {
    private static final SparseIntArray PRECISE_CAUSE_MAP;
    private static final boolean VERBOSE_STATE_LOGGING = Rlog.isLoggable("IPCTState", 2);
    private boolean mAllowAddCallDuringVideoCall;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean mAllowEmergencyVideoCalls;
    private boolean mAllowHoldingVideoCall;
    private boolean mAlwaysPlayRemoteHoldTone;
    private boolean mAutoRetryFailedWifiEmergencyCall;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public ImsPhoneCall mBackgroundCall;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private ImsCall mCallExpectedToResume;
    private final Map<String, CallQualityMetrics> mCallQualityMetrics;
    private final ConcurrentLinkedQueue<CallQualityMetrics> mCallQualityMetricsHistory;
    private CarrierConfigManager.CarrierConfigChangeListener mCarrierConfigChangeListener;
    private Pair<Integer, PersistableBundle> mCarrierConfigForSubId;
    private boolean mCarrierConfigLoadedForSubscription;
    private int mClirMode;
    private Config mConfig;
    private final ProvisioningManager.Callback mConfigCallback;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private ArrayList<ImsPhoneConnection> mConnections;
    private final ConnectorFactory mConnectorFactory;
    private Runnable mConnectorRunnable;
    private Optional<Integer> mCurrentlyConnectedSubId;
    private final AtomicInteger mDefaultDialerUid;
    private boolean mDesiredMute;
    private boolean mDeviceToDeviceForceEnabled;
    private boolean mDropVideoCallWhenAnsweringAudioCall;
    private Executor mExecutor;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public ImsPhoneCall mForegroundCall;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public ImsPhoneCall mHandoverCall;
    private boolean mHasAttemptedStartOfCallHandover;
    private HoldSwapState mHoldSwitchingState;
    private boolean mIgnoreDataEnabledChangedForVideoCalls;
    private final ImsCallInfoTracker mImsCallInfoTracker;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private ImsCall.Listener mImsCallListener;
    private final ImsMmTelManager.CapabilityCallback mImsCapabilityCallback;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private ImsManager mImsManager;
    private final FeatureConnector<ImsManager> mImsManagerConnector;
    private Map<ImsReasonInfoKeyPair, Integer> mImsReasonCodeMap;
    private final ImsManager.ImsStatsCallback mImsStatsCallback;
    private final ConcurrentHashMap<Integer, ImsTrafficSession> mImsTrafficSessions;
    private ImsCall.Listener mImsUssdListener;
    private boolean mIsConferenceEventPackageEnabled;
    private boolean mIsDataEnabled;
    private boolean mIsInEmergencyCall;
    private boolean mIsMonitoringConnectivity;
    private boolean mIsViLteDataMetered;
    private ImsPhone.ImsDialArgs mLastDialArgs;
    private String mLastDialString;
    private MediaThreshold mMediaThreshold;
    private TelephonyMetrics mMetrics;
    private MmTelFeature.MmTelCapabilities mMmTelCapabilities;
    private final MmTelFeatureListener mMmTelFeatureListener;
    private ConnectivityManager.NetworkCallback mNetworkCallback;
    private boolean mNotifyHandoverVideoFromLTEToWifi;
    private boolean mNotifyHandoverVideoFromWifiToLTE;
    private boolean mNotifyVtHandoverToWifiFail;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private int mOnHoldToneId;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean mOnHoldToneStarted;
    private final LocalLog mOperationLocalLog;
    private int mPendingCallVideoState;
    private Bundle mPendingIntentExtras;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private ImsPhoneConnection mPendingMO;
    private Pair<Boolean, Integer> mPendingSilentRedialInfo;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private Message mPendingUssd;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    ImsPhone mPhone;
    private final Map<String, CacheEntry> mPhoneNumAndConnTime;
    private List<PhoneStateListener> mPhoneStateListeners;
    private final BroadcastReceiver mReceiver;
    private final LocalLog mRegLocalLog;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public ImsPhoneCall mRingingCall;
    private DataSettingsManager.DataSettingsManagerCallback mSettingsCallback;
    private SharedPreferenceProxy mSharedPreferenceProxy;
    private boolean mShouldUpdateImsConfigOnDisconnect;
    private final SrvccStartedCallback mSrvccStartedCallback;
    private Call.SrvccState mSrvccState;
    private final List<Integer> mSrvccTypeSupported;
    private PhoneConstants.State mState;
    private boolean mSupportCepOnPeer;
    private boolean mSupportD2DUsingRtp;
    private boolean mSupportDowngradeVtToAudio;
    private boolean mSupportPauseVideo;
    private boolean mSupportSdpForRtpHeaderExtensions;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean mSwitchingFgAndBgCalls;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private Object mSyncHold;
    private long mThresholdRtpInactivityTime;
    private int mThresholdRtpJitter;
    private int mThresholdRtpPacketLoss;
    private boolean mTreatDowngradedVideoCallsAsVideoCalls;
    private final Queue<CacheEntry> mUnknownPeerConnTime;
    private int mUssdMethod;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private ImsCall mUssdSession;
    private ImsUtInterface mUtInterface;
    private RegistrantList mVoiceCallEndedRegistrants;
    private RegistrantList mVoiceCallStartedRegistrants;
    private final HashMap<Integer, Long> mVtDataUsageMap;
    private final VtDataUsageProvider mVtDataUsageProvider;
    private volatile NetworkStats mVtDataUsageSnapshot;
    private volatile NetworkStats mVtDataUsageUidSnapshot;
    private int pendingCallClirMode;
    private boolean pendingCallInEcm;

    /* loaded from: classes.dex */
    public static class Config {
        public boolean isD2DCommunicationSupported;
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface ConnectorFactory {
        FeatureConnector<ImsManager> create(Context context, int i, String str, FeatureConnector.Listener<ImsManager> listener, Executor executor);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum HoldSwapState {
        INACTIVE,
        PENDING_SINGLE_CALL_HOLD,
        PENDING_SINGLE_CALL_UNHOLD,
        SWAPPING_ACTIVE_AND_HELD,
        HOLDING_TO_ANSWER_INCOMING,
        PENDING_RESUME_FOREGROUND_AFTER_FAILURE,
        HOLDING_TO_DIAL_OUTGOING,
        PENDING_RESUME_FOREGROUND_AFTER_HOLD
    }

    /* loaded from: classes.dex */
    public interface PhoneStateListener {
        void onPhoneStateChanged(PhoneConstants.State state, PhoneConstants.State state2);
    }

    /* loaded from: classes.dex */
    public interface SharedPreferenceProxy {
        SharedPreferences getDefaultSharedPreferences(Context context);
    }

    private static boolean isAlive(int i) {
        if (i != 9) {
            switch (i) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    @Override // com.android.internal.telephony.CallTracker
    protected void handlePollCalls(AsyncResult asyncResult) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ImsTrafficSession {
        private final IImsTrafficSessionCallback mCallback;
        private final int mTrafficDirection;
        private final int mTrafficType;

        ImsTrafficSession(int i, int i2, IImsTrafficSessionCallback iImsTrafficSessionCallback) {
            this.mTrafficType = i;
            this.mTrafficDirection = i2;
            this.mCallback = iImsTrafficSessionCallback;
        }
    }

    static {
        SparseIntArray sparseIntArray = new SparseIntArray();
        PRECISE_CAUSE_MAP = sparseIntArray;
        sparseIntArray.append(101, CallFailCause.LOCAL_ILLEGAL_ARGUMENT);
        sparseIntArray.append(CallFailCause.RECOVERY_ON_TIMER_EXPIRY, CallFailCause.LOCAL_ILLEGAL_STATE);
        sparseIntArray.append(103, CallFailCause.LOCAL_INTERNAL_ERROR);
        sparseIntArray.append(106, CallFailCause.LOCAL_IMS_SERVICE_DOWN);
        sparseIntArray.append(107, CallFailCause.LOCAL_NO_PENDING_CALL);
        sparseIntArray.append(108, 16);
        sparseIntArray.append(111, CallFailCause.LOCAL_POWER_OFF);
        sparseIntArray.append(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_APN_TYPE_CONFLICT, CallFailCause.LOCAL_LOW_BATTERY);
        sparseIntArray.append(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMM_ACCESS_BARRED_INFINITE_RETRY, CallFailCause.LOCAL_NETWORK_NO_SERVICE);
        sparseIntArray.append(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_AUTH_FAILURE_ON_EMERGENCY_CALL, CallFailCause.LOCAL_NETWORK_NO_LTE_COVERAGE);
        sparseIntArray.append(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INVALID_DNS_ADDR, CallFailCause.LOCAL_NETWORK_ROAMING);
        sparseIntArray.append(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INVALID_PCSCF_OR_DNS_ADDRESS, CallFailCause.LOCAL_NETWORK_IP_CHANGED);
        sparseIntArray.append(131, CallFailCause.LOCAL_SERVICE_UNAVAILABLE);
        sparseIntArray.append(UiccCardApplication.AUTH_CONTEXT_GBA_BOOTSTRAP, CallFailCause.LOCAL_NOT_REGISTERED);
        sparseIntArray.append(141, CallFailCause.LOCAL_MAX_CALL_EXCEEDED);
        sparseIntArray.append(143, CallFailCause.LOCAL_CALL_DECLINE);
        sparseIntArray.append(144, CallFailCause.LOCAL_CALL_VCC_ON_PROGRESSING);
        sparseIntArray.append(145, CallFailCause.LOCAL_CALL_RESOURCE_RESERVATION_FAILED);
        sparseIntArray.append(146, CallFailCause.LOCAL_CALL_CS_RETRY_REQUIRED);
        sparseIntArray.append(147, CallFailCause.LOCAL_CALL_VOLTE_RETRY_REQUIRED);
        sparseIntArray.append(148, CallFailCause.LOCAL_CALL_TERMINATED);
        sparseIntArray.append(149, CallFailCause.LOCAL_HO_NOT_FEASIBLE);
        sparseIntArray.append(IccRecords.EVENT_SET_SMSS_RECORD_DONE, CallFailCause.TIMEOUT_1XX_WAITING);
        sparseIntArray.append(202, CallFailCause.TIMEOUT_NO_ANSWER);
        sparseIntArray.append(203, CallFailCause.TIMEOUT_NO_ANSWER_CALL_UPDATE);
        sparseIntArray.append(CallFailCause.FDN_BLOCKED, CallFailCause.FDN_BLOCKED);
        sparseIntArray.append(321, CallFailCause.SIP_REDIRECTED);
        sparseIntArray.append(331, CallFailCause.SIP_BAD_REQUEST);
        sparseIntArray.append(332, CallFailCause.SIP_FORBIDDEN);
        sparseIntArray.append(333, CallFailCause.SIP_NOT_FOUND);
        sparseIntArray.append(334, CallFailCause.SIP_NOT_SUPPORTED);
        sparseIntArray.append(335, CallFailCause.SIP_REQUEST_TIMEOUT);
        sparseIntArray.append(336, CallFailCause.SIP_TEMPRARILY_UNAVAILABLE);
        sparseIntArray.append(337, CallFailCause.SIP_BAD_ADDRESS);
        sparseIntArray.append(338, CallFailCause.SIP_BUSY);
        sparseIntArray.append(339, CallFailCause.SIP_REQUEST_CANCELLED);
        sparseIntArray.append(340, CallFailCause.SIP_NOT_ACCEPTABLE);
        sparseIntArray.append(341, CallFailCause.SIP_NOT_REACHABLE);
        sparseIntArray.append(342, CallFailCause.SIP_CLIENT_ERROR);
        sparseIntArray.append(343, CallFailCause.SIP_TRANSACTION_DOES_NOT_EXIST);
        sparseIntArray.append(351, CallFailCause.SIP_SERVER_INTERNAL_ERROR);
        sparseIntArray.append(352, CallFailCause.SIP_SERVICE_UNAVAILABLE);
        sparseIntArray.append(353, CallFailCause.SIP_SERVER_TIMEOUT);
        sparseIntArray.append(354, CallFailCause.SIP_SERVER_ERROR);
        sparseIntArray.append(361, CallFailCause.SIP_USER_REJECTED);
        sparseIntArray.append(362, CallFailCause.SIP_GLOBAL_ERROR);
        sparseIntArray.append(363, CallFailCause.IMS_EMERGENCY_TEMP_FAILURE);
        sparseIntArray.append(364, CallFailCause.IMS_EMERGENCY_PERM_FAILURE);
        sparseIntArray.append(401, CallFailCause.MEDIA_INIT_FAILED);
        sparseIntArray.append(402, CallFailCause.MEDIA_NO_DATA);
        sparseIntArray.append(403, CallFailCause.MEDIA_NOT_ACCEPTABLE);
        sparseIntArray.append(404, CallFailCause.MEDIA_UNSPECIFIED);
        sparseIntArray.append(501, 1500);
        sparseIntArray.append(502, CallFailCause.USER_NOANSWER);
        sparseIntArray.append(503, CallFailCause.USER_IGNORE);
        sparseIntArray.append(504, CallFailCause.USER_DECLINE);
        sparseIntArray.append(505, CallFailCause.LOW_BATTERY);
        sparseIntArray.append(506, CallFailCause.BLACKLISTED_CALL_ID);
        sparseIntArray.append(510, CallFailCause.USER_TERMINATED_BY_REMOTE);
        sparseIntArray.append(801, CallFailCause.UT_NOT_SUPPORTED);
        sparseIntArray.append(802, CallFailCause.UT_SERVICE_UNAVAILABLE);
        sparseIntArray.append(803, CallFailCause.UT_OPERATION_NOT_ALLOWED);
        sparseIntArray.append(804, CallFailCause.UT_NETWORK_ERROR);
        sparseIntArray.append(821, CallFailCause.UT_CB_PASSWORD_MISMATCH);
        sparseIntArray.append(901, CallFailCause.ECBM_NOT_SUPPORTED);
        sparseIntArray.append(902, CallFailCause.MULTIENDPOINT_NOT_SUPPORTED);
        sparseIntArray.append(1100, 2000);
        sparseIntArray.append(1014, 2100);
        sparseIntArray.append(1015, 2101);
        sparseIntArray.append(1016, 2102);
        sparseIntArray.append(CallFailCause.LOCAL_ILLEGAL_STATE, CallFailCause.SUPP_SVC_FAILED);
        sparseIntArray.append(CallFailCause.LOCAL_INTERNAL_ERROR, CallFailCause.SUPP_SVC_CANCELLED);
        sparseIntArray.append(CallFailCause.LOCAL_IMS_SERVICE_DOWN, CallFailCause.SUPP_SVC_REINVITE_COLLISION);
        sparseIntArray.append(CallFailCause.SIP_REDIRECTED, CallFailCause.IWLAN_DPD_FAILURE);
        sparseIntArray.append(CallFailCause.MEDIA_INIT_FAILED, CallFailCause.EPDG_TUNNEL_ESTABLISH_FAILURE);
        sparseIntArray.append(CallFailCause.MEDIA_NO_DATA, CallFailCause.EPDG_TUNNEL_REKEY_FAILURE);
        sparseIntArray.append(CallFailCause.MEDIA_NOT_ACCEPTABLE, CallFailCause.EPDG_TUNNEL_LOST_CONNECTION);
        sparseIntArray.append(CallFailCause.MEDIA_UNSPECIFIED, CallFailCause.MAXIMUM_NUMBER_OF_CALLS_REACHED);
        sparseIntArray.append(1404, CallFailCause.REMOTE_CALL_DECLINE);
        sparseIntArray.append(1405, CallFailCause.DATA_LIMIT_REACHED);
        sparseIntArray.append(1406, CallFailCause.DATA_DISABLED);
        sparseIntArray.append(1407, CallFailCause.WIFI_LOST);
        sparseIntArray.append(1500, CallFailCause.RADIO_OFF);
        sparseIntArray.append(CallFailCause.USER_NOANSWER, CallFailCause.NO_VALID_SIM);
        sparseIntArray.append(CallFailCause.USER_IGNORE, CallFailCause.RADIO_INTERNAL_ERROR);
        sparseIntArray.append(CallFailCause.USER_DECLINE, CallFailCause.NETWORK_RESP_TIMEOUT);
        sparseIntArray.append(CallFailCause.LOW_BATTERY, CallFailCause.NETWORK_REJECT);
        sparseIntArray.append(CallFailCause.BLACKLISTED_CALL_ID, CallFailCause.RADIO_ACCESS_FAILURE);
        sparseIntArray.append(1506, CallFailCause.RADIO_LINK_FAILURE);
        sparseIntArray.append(1507, 255);
        sparseIntArray.append(1508, CallFailCause.RADIO_UPLINK_FAILURE);
        sparseIntArray.append(1509, CallFailCause.RADIO_SETUP_FAILURE);
        sparseIntArray.append(CallFailCause.USER_TERMINATED_BY_REMOTE, CallFailCause.RADIO_RELEASE_NORMAL);
        sparseIntArray.append(1511, CallFailCause.RADIO_RELEASE_ABNORMAL);
        sparseIntArray.append(1512, CallFailCause.ACCESS_CLASS_BLOCKED);
        sparseIntArray.append(1513, CallFailCause.NETWORK_DETACH);
        sparseIntArray.append(1515, 1);
        sparseIntArray.append(CallFailCause.OEM_CAUSE_1, CallFailCause.OEM_CAUSE_1);
        sparseIntArray.append(CallFailCause.OEM_CAUSE_2, CallFailCause.OEM_CAUSE_2);
        sparseIntArray.append(CallFailCause.OEM_CAUSE_3, CallFailCause.OEM_CAUSE_3);
        sparseIntArray.append(CallFailCause.OEM_CAUSE_4, CallFailCause.OEM_CAUSE_4);
        sparseIntArray.append(CallFailCause.OEM_CAUSE_5, CallFailCause.OEM_CAUSE_5);
        sparseIntArray.append(CallFailCause.OEM_CAUSE_6, CallFailCause.OEM_CAUSE_6);
        sparseIntArray.append(CallFailCause.OEM_CAUSE_7, CallFailCause.OEM_CAUSE_7);
        sparseIntArray.append(CallFailCause.OEM_CAUSE_8, CallFailCause.OEM_CAUSE_8);
        sparseIntArray.append(CallFailCause.OEM_CAUSE_9, CallFailCause.OEM_CAUSE_9);
        sparseIntArray.append(CallFailCause.OEM_CAUSE_10, CallFailCause.OEM_CAUSE_10);
        sparseIntArray.append(CallFailCause.OEM_CAUSE_11, CallFailCause.OEM_CAUSE_11);
        sparseIntArray.append(CallFailCause.OEM_CAUSE_12, CallFailCause.OEM_CAUSE_12);
        sparseIntArray.append(CallFailCause.OEM_CAUSE_13, CallFailCause.OEM_CAUSE_13);
        sparseIntArray.append(CallFailCause.OEM_CAUSE_14, CallFailCause.OEM_CAUSE_14);
        sparseIntArray.append(CallFailCause.OEM_CAUSE_15, CallFailCause.OEM_CAUSE_15);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MmTelFeatureListener extends MmTelFeature.Listener {
        private MmTelFeatureListener() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: processIncomingCall */
        public IImsCallSessionListener lambda$onIncomingCall$0(IImsCallSession iImsCallSession, String str, Bundle bundle) {
            String callExtra;
            ImsCall imsCall;
            ImsPhoneCallTracker.this.log("processIncomingCall: incoming call intent");
            if (bundle == null) {
                bundle = new Bundle();
            }
            if (ImsPhoneCallTracker.this.mImsManager == null) {
                return null;
            }
            try {
                if (bundle.getBoolean("android.telephony.ims.feature.extra.IS_USSD", false) | bundle.getBoolean("android:ussd", false)) {
                    ImsPhoneCallTracker.this.log("processIncomingCall: USSD");
                    ImsPhoneCallTracker.this.mOperationLocalLog.log("processIncomingCall: USSD");
                    ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                    imsPhoneCallTracker.mUssdSession = imsPhoneCallTracker.mImsManager.takeCall(iImsCallSession, ImsPhoneCallTracker.this.mImsUssdListener);
                    if (ImsPhoneCallTracker.this.mUssdSession != null) {
                        ImsPhoneCallTracker.this.mUssdSession.accept(2);
                    }
                    if (str != null) {
                        ImsPhoneCallTracker.this.mUssdSession.getCallSession().setCallId(str);
                    }
                    return ImsPhoneCallTracker.this.mUssdSession.getCallSession().getIImsCallSessionListenerProxy();
                }
                boolean z = bundle.getBoolean("android:isUnknown", false) | bundle.getBoolean("android.telephony.ims.feature.extra.IS_UNKNOWN_CALL", false);
                ImsPhoneCallTracker imsPhoneCallTracker2 = ImsPhoneCallTracker.this;
                imsPhoneCallTracker2.log("processIncomingCall: isUnknown = " + z + " fg = " + ImsPhoneCallTracker.this.mForegroundCall.getState() + " bg = " + ImsPhoneCallTracker.this.mBackgroundCall.getState());
                ImsCall takeCall = ImsPhoneCallTracker.this.mImsManager.takeCall(iImsCallSession, ImsPhoneCallTracker.this.mImsCallListener);
                if (str != null) {
                    takeCall.getCallSession().setCallId(str);
                }
                ImsCallSession.IImsCallSessionListenerProxy iImsCallSessionListenerProxy = takeCall.getCallSession().getIImsCallSessionListenerProxy();
                ImsPhoneCallTracker imsPhoneCallTracker3 = ImsPhoneCallTracker.this;
                ImsPhoneConnection imsPhoneConnection = new ImsPhoneConnection(imsPhoneCallTracker3.mPhone, takeCall, imsPhoneCallTracker3, z ? imsPhoneCallTracker3.mForegroundCall : imsPhoneCallTracker3.mRingingCall, z);
                if (ImsPhoneCallTracker.this.mForegroundCall.hasConnections() && (imsCall = ImsPhoneCallTracker.this.mForegroundCall.getFirstConnection().getImsCall()) != null) {
                    imsPhoneConnection.setActiveCallDisconnectedOnAnswer(ImsPhoneCallTracker.this.shouldDisconnectActiveCallOnAnswer(imsCall, takeCall));
                }
                imsPhoneConnection.setAllowAddCallDuringVideoCall(ImsPhoneCallTracker.this.mAllowAddCallDuringVideoCall);
                imsPhoneConnection.setAllowHoldingVideoCall(ImsPhoneCallTracker.this.mAllowHoldingVideoCall);
                if (iImsCallSession != null && iImsCallSession.getCallProfile() != null && iImsCallSession.getCallProfile().getCallExtras() != null && iImsCallSession.getCallProfile().getCallExtras().containsKey("android.telephony.ims.extra.CALL_DISCONNECT_CAUSE") && (callExtra = iImsCallSession.getCallProfile().getCallExtra("android.telephony.ims.extra.CALL_DISCONNECT_CAUSE", (String) null)) != null) {
                    try {
                        int disconnectCauseFromReasonInfo = ImsPhoneCallTracker.this.getDisconnectCauseFromReasonInfo(new ImsReasonInfo(Integer.parseInt(callExtra), 0, null), imsPhoneConnection.getState());
                        if (disconnectCauseFromReasonInfo == 81) {
                            imsPhoneConnection.setDisconnectCause(disconnectCauseFromReasonInfo);
                            ImsPhoneCallTracker.this.log("onIncomingCall : incoming call auto rejected");
                            ImsPhoneCallTracker.this.mOperationLocalLog.log("processIncomingCall: auto rejected");
                        }
                    } catch (NumberFormatException e) {
                        Rlog.e("ImsPhoneCallTracker", "Exception in parsing Integer Data: " + e);
                    }
                }
                LocalLog localLog = ImsPhoneCallTracker.this.mOperationLocalLog;
                localLog.log("onIncomingCall: isUnknown=" + z + ", connId=" + System.identityHashCode(imsPhoneConnection));
                ImsPhoneCallTracker.this.addConnection(imsPhoneConnection);
                ImsPhoneCallTracker.this.setVideoCallProvider(imsPhoneConnection, takeCall);
                TelephonyMetrics.getInstance().writeOnImsCallReceive(ImsPhoneCallTracker.this.mPhone.getPhoneId(), takeCall.getSession());
                ImsPhoneCallTracker.this.mPhone.getVoiceCallSessionStats().onImsCallReceived(imsPhoneConnection);
                if (z) {
                    if (ImsPhoneCallTracker.this.mPendingMO != null && Objects.equals(ImsPhoneCallTracker.this.mPendingMO.getAddress(), imsPhoneConnection.getAddress())) {
                        LocalLog localLog2 = ImsPhoneCallTracker.this.mOperationLocalLog;
                        localLog2.log("onIncomingCall: unknown call " + imsPhoneConnection + " replaces " + ImsPhoneCallTracker.this.mPendingMO);
                    }
                    ImsPhoneCallTracker.this.mPhone.notifyUnknownConnection(imsPhoneConnection);
                } else {
                    Call.State state = ImsPhoneCallTracker.this.mForegroundCall.getState();
                    Call.State state2 = Call.State.IDLE;
                    if (state != state2 || ImsPhoneCallTracker.this.mBackgroundCall.getState() != state2) {
                        imsPhoneConnection.update(takeCall, Call.State.WAITING);
                    }
                    ImsPhoneCallTracker.this.mPhone.notifyNewRingingConnection(imsPhoneConnection);
                    ImsPhoneCallTracker.this.mPhone.notifyIncomingRing();
                }
                ImsPhoneCallTracker.this.updatePhoneState();
                ImsPhoneCallTracker.this.mPhone.notifyPreciseCallStateChanged();
                ImsPhoneCallTracker.this.mImsCallInfoTracker.addImsCallStatus(imsPhoneConnection);
                return iImsCallSessionListenerProxy;
            } catch (ImsException | RemoteException e2) {
                ImsPhoneCallTracker imsPhoneCallTracker4 = ImsPhoneCallTracker.this;
                imsPhoneCallTracker4.loge("processIncomingCall: exception " + e2);
                LocalLog localLog3 = ImsPhoneCallTracker.this.mOperationLocalLog;
                localLog3.log("onIncomingCall: exception processing: " + e2);
                return null;
            }
        }

        public IImsCallSessionListener onIncomingCall(final IImsCallSession iImsCallSession, final String str, final Bundle bundle) {
            return (IImsCallSessionListener) executeAndWaitForReturn(new Supplier() { // from class: com.android.internal.telephony.imsphone.ImsPhoneCallTracker$MmTelFeatureListener$$ExternalSyntheticLambda0
                @Override // java.util.function.Supplier
                public final Object get() {
                    IImsCallSessionListener lambda$onIncomingCall$0;
                    lambda$onIncomingCall$0 = ImsPhoneCallTracker.MmTelFeatureListener.this.lambda$onIncomingCall$0(iImsCallSession, str, bundle);
                    return lambda$onIncomingCall$0;
                }
            });
        }

        public void onVoiceMessageCountUpdate(final int i) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: com.android.internal.telephony.imsphone.ImsPhoneCallTracker$MmTelFeatureListener$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    ImsPhoneCallTracker.MmTelFeatureListener.this.lambda$onVoiceMessageCountUpdate$1(i);
                }
            }, ImsPhoneCallTracker.this.mExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onVoiceMessageCountUpdate$1(int i) {
            ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
            ImsPhone imsPhone = imsPhoneCallTracker.mPhone;
            if (imsPhone != null && imsPhone.mDefaultPhone != null) {
                imsPhoneCallTracker.log("onVoiceMessageCountChanged :: count=" + i);
                ImsPhoneCallTracker.this.mPhone.mDefaultPhone.setVoiceMessageCount(i);
                return;
            }
            imsPhoneCallTracker.loge("onVoiceMessageCountUpdate: null phone");
        }

        public void onAudioModeIsVoipChanged(final int i) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: com.android.internal.telephony.imsphone.ImsPhoneCallTracker$MmTelFeatureListener$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    ImsPhoneCallTracker.MmTelFeatureListener.this.lambda$onAudioModeIsVoipChanged$2(i);
                }
            }, ImsPhoneCallTracker.this.mExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAudioModeIsVoipChanged$2(int i) {
            ImsCall imsCall;
            if (ImsPhoneCallTracker.this.mForegroundCall.hasConnections()) {
                imsCall = ImsPhoneCallTracker.this.mForegroundCall.getImsCall();
            } else if (ImsPhoneCallTracker.this.mBackgroundCall.hasConnections()) {
                imsCall = ImsPhoneCallTracker.this.mBackgroundCall.getImsCall();
            } else if (ImsPhoneCallTracker.this.mRingingCall.hasConnections()) {
                imsCall = ImsPhoneCallTracker.this.mRingingCall.getImsCall();
            } else if (ImsPhoneCallTracker.this.mHandoverCall.hasConnections()) {
                imsCall = ImsPhoneCallTracker.this.mHandoverCall.getImsCall();
            } else {
                Rlog.e("ImsPhoneCallTracker", "onAudioModeIsVoipChanged: no Call");
                imsCall = null;
            }
            if (imsCall != null) {
                ImsPhoneConnection findConnection = ImsPhoneCallTracker.this.findConnection(imsCall);
                if (findConnection != null) {
                    findConnection.onAudioModeIsVoipChanged(i);
                    return;
                }
                return;
            }
            Rlog.e("ImsPhoneCallTracker", "onAudioModeIsVoipChanged: no ImsCall");
        }

        public void onTriggerEpsFallback(final int i) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: com.android.internal.telephony.imsphone.ImsPhoneCallTracker$MmTelFeatureListener$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ImsPhoneCallTracker.MmTelFeatureListener.this.lambda$onTriggerEpsFallback$3(i);
                }
            }, ImsPhoneCallTracker.this.mExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onTriggerEpsFallback$3(int i) {
            ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
            imsPhoneCallTracker.log("onTriggerEpsFallback reason=" + i);
            ImsPhoneCallTracker.this.mPhone.triggerEpsFallback(i, null);
        }

        public void onStartImsTrafficSession(final int i, final int i2, final int i3, final int i4, final IImsTrafficSessionCallback iImsTrafficSessionCallback) {
            ImsPhoneCallTracker.this.registerImsTrafficSession(i, i2, i4, iImsTrafficSessionCallback);
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: com.android.internal.telephony.imsphone.ImsPhoneCallTracker$MmTelFeatureListener$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    ImsPhoneCallTracker.MmTelFeatureListener.this.lambda$onStartImsTrafficSession$4(i, i2, i3, i4, iImsTrafficSessionCallback);
                }
            }, ImsPhoneCallTracker.this.mExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onStartImsTrafficSession$4(int i, int i2, int i3, int i4, IImsTrafficSessionCallback iImsTrafficSessionCallback) {
            ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
            imsPhoneCallTracker.log("onStartImsTrafficSession token=" + i + ", traffic=" + i2 + ", networkType=" + i3 + ", direction=" + i4);
            ImsPhoneCallTracker imsPhoneCallTracker2 = ImsPhoneCallTracker.this;
            imsPhoneCallTracker2.mPhone.startImsTraffic(i, i2, i3, i4, imsPhoneCallTracker2.obtainMessage(33, iImsTrafficSessionCallback));
        }

        public void onModifyImsTrafficSession(final int i, final int i2) {
            final ImsTrafficSession imsTrafficSession = ImsPhoneCallTracker.this.getImsTrafficSession(i);
            if (imsTrafficSession == null) {
                ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                imsPhoneCallTracker.loge("onModifyImsTrafficSession unknown session, token=" + i);
                return;
            }
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: com.android.internal.telephony.imsphone.ImsPhoneCallTracker$MmTelFeatureListener$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    ImsPhoneCallTracker.MmTelFeatureListener.this.lambda$onModifyImsTrafficSession$5(i, i2, imsTrafficSession);
                }
            }, ImsPhoneCallTracker.this.mExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onModifyImsTrafficSession$5(int i, int i2, ImsTrafficSession imsTrafficSession) {
            ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
            imsPhoneCallTracker.log("onModifyImsTrafficSession token=" + i + ", networkType=" + i2);
            ImsPhoneCallTracker.this.mPhone.startImsTraffic(i, imsTrafficSession.mTrafficType, i2, imsTrafficSession.mTrafficDirection, ImsPhoneCallTracker.this.obtainMessage(33, imsTrafficSession.mCallback));
        }

        public void onStopImsTrafficSession(final int i) {
            ImsPhoneCallTracker.this.unregisterImsTrafficSession(i);
            if (i == -1) {
                return;
            }
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: com.android.internal.telephony.imsphone.ImsPhoneCallTracker$MmTelFeatureListener$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ImsPhoneCallTracker.MmTelFeatureListener.this.lambda$onStopImsTrafficSession$6(i);
                }
            }, ImsPhoneCallTracker.this.mExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onStopImsTrafficSession$6(int i) {
            ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
            imsPhoneCallTracker.log("onStopImsTrafficSession token=" + i);
            ImsPhoneCallTracker.this.mPhone.stopImsTraffic(i, null);
        }

        public void onMediaQualityStatusChanged(final MediaQualityStatus mediaQualityStatus) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: com.android.internal.telephony.imsphone.ImsPhoneCallTracker$MmTelFeatureListener$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ImsPhoneCallTracker.MmTelFeatureListener.this.lambda$onMediaQualityStatusChanged$7(mediaQualityStatus);
                }
            }, ImsPhoneCallTracker.this.mExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onMediaQualityStatusChanged$7(MediaQualityStatus mediaQualityStatus) {
            ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
            ImsPhone imsPhone = imsPhoneCallTracker.mPhone;
            if (imsPhone != null && imsPhone.mDefaultPhone != null) {
                imsPhoneCallTracker.log("onMediaQualityStatusChanged " + mediaQualityStatus);
                ImsPhoneCallTracker.this.mPhone.onMediaQualityStatusChanged(mediaQualityStatus);
                return;
            }
            imsPhoneCallTracker.loge("onMediaQualityStatusChanged: null phone");
        }

        private <T> T executeAndWaitForReturn(final Supplier<T> supplier) {
            try {
                return (T) CompletableFuture.supplyAsync(new Supplier() { // from class: com.android.internal.telephony.imsphone.ImsPhoneCallTracker$MmTelFeatureListener$$ExternalSyntheticLambda8
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        Object runWithCleanCallingIdentity;
                        runWithCleanCallingIdentity = TelephonyUtils.runWithCleanCallingIdentity(supplier);
                        return runWithCleanCallingIdentity;
                    }
                }, ImsPhoneCallTracker.this.mExecutor).get();
            } catch (InterruptedException | ExecutionException e) {
                Log.w("ImsPhoneCallTracker", "ImsPhoneCallTracker : executeAndWaitForReturn exception: " + e.getMessage());
                return null;
            }
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    /* loaded from: classes.dex */
    public class VtDataUsageProvider extends NetworkStatsProvider {
        private int mToken = 0;
        private NetworkStats mIfaceSnapshot = new NetworkStats(0, 0);
        private NetworkStats mUidSnapshot = new NetworkStats(0, 0);

        public void onSetAlert(long j) {
        }

        public void onSetLimit(String str, long j) {
        }

        public VtDataUsageProvider() {
        }

        public void onRequestStatsUpdate(int i) {
            if (ImsPhoneCallTracker.this.mState != PhoneConstants.State.IDLE) {
                Iterator it = ImsPhoneCallTracker.this.mConnections.iterator();
                while (it.hasNext()) {
                    Connection.VideoProvider videoProvider = ((ImsPhoneConnection) it.next()).getVideoProvider();
                    if (videoProvider != null) {
                        videoProvider.onRequestConnectionDataUsage();
                    }
                }
            }
            NetworkStats subtract = ImsPhoneCallTracker.this.mVtDataUsageSnapshot.subtract(this.mIfaceSnapshot);
            NetworkStats subtract2 = ImsPhoneCallTracker.this.mVtDataUsageUidSnapshot.subtract(this.mUidSnapshot);
            ImsPhoneCallTracker.this.mVtDataUsageProvider.notifyStatsUpdated(this.mToken, subtract, subtract2);
            this.mIfaceSnapshot = this.mIfaceSnapshot.add(subtract);
            this.mUidSnapshot = this.mUidSnapshot.add(subtract2);
            this.mToken = i;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class CacheEntry {
        private long mCachedTime;
        private int mCallDirection;
        private long mConnectElapsedTime;
        private long mConnectTime;

        CacheEntry(long j, long j2, long j3, int i) {
            this.mCachedTime = j;
            this.mConnectTime = j2;
            this.mConnectElapsedTime = j3;
            this.mCallDirection = i;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SrvccStartedCallback extends ISrvccStartedCallback.Stub {
        private SrvccStartedCallback() {
        }

        public void onSrvccCallNotified(List<SrvccCall> list) {
            ImsPhoneCallTracker.this.handleSrvccConnectionInfo(list);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ImsReasonInfoKeyPair extends Pair<Integer, String> {
        private ImsReasonInfoKeyPair(Integer num, String str) {
            super(num, str);
        }

        @Override // android.util.Pair
        public boolean equals(Object obj) {
            if (obj instanceof ImsReasonInfoKeyPair) {
                ImsReasonInfoKeyPair imsReasonInfoKeyPair = (ImsReasonInfoKeyPair) obj;
                return Objects.equals(((Pair) imsReasonInfoKeyPair).first, ((Pair) this).first) && Objects.toString(((Pair) this).second).startsWith(Objects.toString(((Pair) imsReasonInfoKeyPair).second));
            }
            return false;
        }

        @Override // android.util.Pair
        public int hashCode() {
            Object obj = ((Pair) this).first;
            if (obj == null) {
                return 0;
            }
            return ((Integer) obj).hashCode();
        }
    }

    public ImsPhoneCallTracker(ImsPhone imsPhone, ConnectorFactory connectorFactory) {
        this(imsPhone, connectorFactory, imsPhone.getContext().getMainExecutor());
    }

    @VisibleForTesting
    public ImsPhoneCallTracker(ImsPhone imsPhone, ConnectorFactory connectorFactory, Executor executor) {
        this.mMmTelCapabilities = new MmTelFeature.MmTelCapabilities();
        this.mCallQualityMetrics = new ConcurrentHashMap();
        this.mCallQualityMetricsHistory = new ConcurrentLinkedQueue<>();
        this.mCarrierConfigLoadedForSubscription = false;
        this.mCarrierConfigForSubId = null;
        this.mCurrentlyConnectedSubId = Optional.empty();
        this.mMmTelFeatureListener = new MmTelFeatureListener();
        this.mCarrierConfigChangeListener = new CarrierConfigManager.CarrierConfigChangeListener() { // from class: com.android.internal.telephony.imsphone.ImsPhoneCallTracker.1
            public void onCarrierConfigChanged(int i, int i2, int i3, int i4) {
                if (ImsPhoneCallTracker.this.mPhone.getPhoneId() != i) {
                    ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                    imsPhoneCallTracker.log("onReceive: Skipping indication for other phoneId: " + i);
                    return;
                }
                PersistableBundle carrierConfigBundle = ImsPhoneCallTracker.this.getCarrierConfigBundle(i2);
                ImsPhoneCallTracker.this.mCarrierConfigForSubId = new Pair(Integer.valueOf(i2), carrierConfigBundle);
                if (!ImsPhoneCallTracker.this.mCurrentlyConnectedSubId.isEmpty() && i2 == ((Integer) ImsPhoneCallTracker.this.mCurrentlyConnectedSubId.get()).intValue()) {
                    ImsPhoneCallTracker imsPhoneCallTracker2 = ImsPhoneCallTracker.this;
                    imsPhoneCallTracker2.log("onReceive: Applying carrier config for subId: " + i2);
                    ImsPhoneCallTracker.this.updateCarrierConfiguration(i2, carrierConfigBundle);
                    return;
                }
                ImsPhoneCallTracker imsPhoneCallTracker3 = ImsPhoneCallTracker.this;
                imsPhoneCallTracker3.log("onReceive: caching carrier config until ImsService connects for subId: " + i2);
            }
        };
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.imsphone.ImsPhoneCallTracker.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if ("android.telecom.action.DEFAULT_DIALER_CHANGED".equals(intent.getAction())) {
                    ImsPhoneCallTracker.this.mDefaultDialerUid.set(ImsPhoneCallTracker.this.getPackageUid(context, intent.getStringExtra("android.telecom.extra.CHANGE_DEFAULT_DIALER_PACKAGE_NAME")));
                }
            }
        };
        this.mReceiver = broadcastReceiver;
        this.mIsMonitoringConnectivity = false;
        this.mIsConferenceEventPackageEnabled = true;
        this.mConfig = null;
        this.mDeviceToDeviceForceEnabled = false;
        this.mNetworkCallback = new ConnectivityManager.NetworkCallback() { // from class: com.android.internal.telephony.imsphone.ImsPhoneCallTracker.3
            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onAvailable(Network network) {
                Rlog.i("ImsPhoneCallTracker", "Network available: " + network);
                ImsPhoneCallTracker.this.scheduleHandoverCheck();
            }
        };
        this.mConnections = new ArrayList<>();
        this.mVoiceCallEndedRegistrants = new RegistrantList();
        this.mVoiceCallStartedRegistrants = new RegistrantList();
        this.mRingingCall = new ImsPhoneCall(this, ImsPhoneCall.CONTEXT_RINGING);
        this.mForegroundCall = new ImsPhoneCall(this, ImsPhoneCall.CONTEXT_FOREGROUND);
        this.mBackgroundCall = new ImsPhoneCall(this, ImsPhoneCall.CONTEXT_BACKGROUND);
        this.mHandoverCall = new ImsPhoneCall(this, ImsPhoneCall.CONTEXT_HANDOVER);
        this.mVtDataUsageMap = new HashMap<>();
        this.mPhoneNumAndConnTime = new ConcurrentHashMap();
        this.mUnknownPeerConnTime = new LinkedBlockingQueue();
        this.mVtDataUsageSnapshot = null;
        this.mVtDataUsageUidSnapshot = null;
        VtDataUsageProvider vtDataUsageProvider = new VtDataUsageProvider();
        this.mVtDataUsageProvider = vtDataUsageProvider;
        AtomicInteger atomicInteger = new AtomicInteger(-1);
        this.mDefaultDialerUid = atomicInteger;
        this.mClirMode = 0;
        this.mSyncHold = new Object();
        this.mUssdSession = null;
        this.mPendingUssd = null;
        this.mDesiredMute = false;
        this.mOnHoldToneStarted = false;
        this.mOnHoldToneId = -1;
        this.mState = PhoneConstants.State.IDLE;
        this.mSrvccState = Call.SrvccState.NONE;
        this.mIsInEmergencyCall = false;
        this.mIsDataEnabled = false;
        this.pendingCallInEcm = false;
        this.mSwitchingFgAndBgCalls = false;
        this.mCallExpectedToResume = null;
        this.mAllowEmergencyVideoCalls = false;
        this.mIgnoreDataEnabledChangedForVideoCalls = false;
        this.mIsViLteDataMetered = false;
        this.mAlwaysPlayRemoteHoldTone = false;
        this.mAutoRetryFailedWifiEmergencyCall = false;
        this.mSupportCepOnPeer = true;
        this.mSupportD2DUsingRtp = false;
        this.mSupportSdpForRtpHeaderExtensions = false;
        this.mSrvccTypeSupported = new ArrayList();
        this.mSrvccStartedCallback = new SrvccStartedCallback();
        this.mHoldSwitchingState = HoldSwapState.INACTIVE;
        this.mLastDialString = null;
        this.mLastDialArgs = null;
        this.mExecutor = new NetworkTypeController$$ExternalSyntheticLambda1();
        this.mPhoneStateListeners = new ArrayList();
        this.mTreatDowngradedVideoCallsAsVideoCalls = false;
        this.mDropVideoCallWhenAnsweringAudioCall = false;
        this.mAllowAddCallDuringVideoCall = true;
        this.mAllowHoldingVideoCall = true;
        this.mNotifyVtHandoverToWifiFail = false;
        this.mSupportDowngradeVtToAudio = false;
        this.mNotifyHandoverVideoFromWifiToLTE = false;
        this.mNotifyHandoverVideoFromLTEToWifi = false;
        this.mHasAttemptedStartOfCallHandover = false;
        this.mSupportPauseVideo = false;
        this.mImsReasonCodeMap = new ArrayMap();
        this.mUssdMethod = 0;
        this.mShouldUpdateImsConfigOnDisconnect = false;
        this.mPendingSilentRedialInfo = null;
        this.mSharedPreferenceProxy = new SharedPreferenceProxy() { // from class: com.android.internal.telephony.imsphone.ImsPhoneCallTracker$$ExternalSyntheticLambda1
            @Override // com.android.internal.telephony.imsphone.ImsPhoneCallTracker.SharedPreferenceProxy
            public final SharedPreferences getDefaultSharedPreferences(Context context) {
                SharedPreferences defaultSharedPreferences;
                defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                return defaultSharedPreferences;
            }
        };
        this.mConnectorRunnable = new Runnable() { // from class: com.android.internal.telephony.imsphone.ImsPhoneCallTracker.4
            @Override // java.lang.Runnable
            public void run() {
                ImsPhoneCallTracker.this.mImsManagerConnector.connect();
            }
        };
        this.mRegLocalLog = new LocalLog(64);
        this.mOperationLocalLog = new LocalLog(64);
        this.mImsTrafficSessions = new ConcurrentHashMap<>();
        this.mImsCallListener = new ImsCall.Listener() { // from class: com.android.internal.telephony.imsphone.ImsPhoneCallTracker.8
            public void onCallInitiating(ImsCall imsCall) {
                ImsPhoneCallTracker.this.log("onCallInitiating");
                ImsPhoneCallTracker.this.mPendingMO = null;
                ImsPhoneCallTracker.this.processCallStateChange(imsCall, Call.State.DIALING, 0, true);
                ImsPhoneCallTracker.this.mMetrics.writeOnImsCallInitiating(ImsPhoneCallTracker.this.mPhone.getPhoneId(), imsCall.getCallSession());
            }

            public void onCallProgressing(ImsCall imsCall) {
                ImsPhoneCallTracker.this.log("onCallProgressing");
                ImsPhoneCallTracker.this.mPendingMO = null;
                ImsPhoneCallTracker.this.processCallStateChange(imsCall, Call.State.ALERTING, 0);
                ImsPhoneCallTracker.this.mMetrics.writeOnImsCallProgressing(ImsPhoneCallTracker.this.mPhone.getPhoneId(), imsCall.getCallSession());
            }

            public void onCallStarted(ImsCall imsCall) {
                ImsPhoneCallTracker.this.log("onCallStarted");
                if (ImsPhoneCallTracker.this.mHoldSwitchingState == HoldSwapState.HOLDING_TO_ANSWER_INCOMING && ImsPhoneCallTracker.this.mCallExpectedToResume != null && ImsPhoneCallTracker.this.mCallExpectedToResume == imsCall) {
                    ImsPhoneCallTracker.this.log("onCallStarted: starting a call as a result of a switch.");
                    ImsPhoneCallTracker.this.mHoldSwitchingState = HoldSwapState.INACTIVE;
                    ImsPhoneCallTracker.this.mCallExpectedToResume = null;
                    ImsPhoneCallTracker.this.logHoldSwapState("onCallStarted");
                }
                ImsPhoneCallTracker.this.mPendingMO = null;
                ImsPhoneCallTracker.this.processCallStateChange(imsCall, Call.State.ACTIVE, 0);
                if (ImsPhoneCallTracker.this.mNotifyVtHandoverToWifiFail && imsCall.isVideoCall() && !imsCall.isWifiCall()) {
                    if (ImsPhoneCallTracker.this.isWifiConnected()) {
                        ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                        imsPhoneCallTracker.sendMessageDelayed(imsPhoneCallTracker.obtainMessage(25, imsCall), 60000L);
                        ImsPhoneCallTracker.this.mHasAttemptedStartOfCallHandover = false;
                    } else {
                        ImsPhoneCallTracker.this.registerForConnectivityChanges();
                        ImsPhoneCallTracker.this.mHasAttemptedStartOfCallHandover = true;
                    }
                }
                ImsPhoneCallTracker.this.mMetrics.writeOnImsCallStarted(ImsPhoneCallTracker.this.mPhone.getPhoneId(), imsCall.getCallSession());
            }

            public void onCallUpdated(ImsCall imsCall) {
                ImsPhoneConnection findConnection;
                ImsPhoneCallTracker.this.log("onCallUpdated");
                if (imsCall == null || (findConnection = ImsPhoneCallTracker.this.findConnection(imsCall)) == null) {
                    return;
                }
                ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                imsPhoneCallTracker.log("onCallUpdated: profile is " + imsCall.getCallProfile());
                ImsPhoneCallTracker.this.processCallStateChange(imsCall, findConnection.getCall().mState, 0, true);
                ImsPhoneCallTracker.this.mMetrics.writeImsCallState(ImsPhoneCallTracker.this.mPhone.getPhoneId(), imsCall.getCallSession(), findConnection.getCall().mState);
            }

            public void onCallStartFailed(ImsCall imsCall, ImsReasonInfo imsReasonInfo) {
                ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                imsPhoneCallTracker.log("onCallStartFailed reasonCode=" + imsReasonInfo.getCode());
                int emergencyServiceCategories = (imsCall == null || imsCall.getCallProfile() == null) ? 0 : imsCall.getCallProfile().getEmergencyServiceCategories();
                if (ImsPhoneCallTracker.this.mHoldSwitchingState == HoldSwapState.HOLDING_TO_ANSWER_INCOMING && ImsPhoneCallTracker.this.mCallExpectedToResume != null && ImsPhoneCallTracker.this.mCallExpectedToResume == imsCall) {
                    ImsPhoneCallTracker.this.log("onCallStarted: starting a call as a result of a switch.");
                    ImsPhoneCallTracker.this.mHoldSwitchingState = HoldSwapState.INACTIVE;
                    ImsPhoneCallTracker.this.mCallExpectedToResume = null;
                    ImsPhoneCallTracker.this.logHoldSwapState("onCallStartFailed");
                }
                ImsPhoneCallTracker.this.mPhone.getVoiceCallSessionStats().onImsCallStartFailed(ImsPhoneCallTracker.this.findConnection(imsCall), new ImsReasonInfo(ImsPhoneCallTracker.this.maybeRemapReasonCode(imsReasonInfo), imsReasonInfo.mExtraCode, imsReasonInfo.mExtraMessage));
                if (DomainSelectionResolver.getInstance().isDomainSelectionSupported()) {
                    ImsPhoneConnection findConnection = ImsPhoneCallTracker.this.findConnection(imsCall);
                    if ((imsReasonInfo.getCode() == 1514 || imsReasonInfo.getCode() == 146) && findConnection != null) {
                        ImsPhoneCallTracker imsPhoneCallTracker2 = ImsPhoneCallTracker.this;
                        imsPhoneCallTracker2.logi("onCallStartFailed eccCategory=" + emergencyServiceCategories);
                        if (imsReasonInfo.getCode() == 1514 || imsReasonInfo.getExtraCode() == 4) {
                            findConnection.setNonDetectableEmergencyCallInfo(emergencyServiceCategories);
                        }
                        findConnection.setImsReasonInfo(imsReasonInfo);
                        ImsPhoneCallTracker.this.sendCallStartFailedDisconnect(imsCall, imsReasonInfo);
                        ImsPhoneCallTracker.this.mMetrics.writeOnImsCallStartFailed(ImsPhoneCallTracker.this.mPhone.getPhoneId(), imsCall.getCallSession(), imsReasonInfo);
                        return;
                    }
                }
                if (ImsPhoneCallTracker.this.mPendingMO != null) {
                    if (imsReasonInfo.getCode() == 146 && ImsPhoneCallTracker.this.mRingingCall.getState() == Call.State.IDLE && ImsPhoneCallTracker.this.isForegroundHigherPriority()) {
                        ImsPhoneCallTracker imsPhoneCallTracker3 = ImsPhoneCallTracker.this;
                        imsPhoneCallTracker3.mForegroundCall.detach(imsPhoneCallTracker3.mPendingMO);
                        ImsPhoneCallTracker imsPhoneCallTracker4 = ImsPhoneCallTracker.this;
                        imsPhoneCallTracker4.removeConnection(imsPhoneCallTracker4.mPendingMO);
                        ImsPhoneCallTracker.this.mImsCallInfoTracker.updateImsCallStatus(ImsPhoneCallTracker.this.mPendingMO);
                        ImsPhoneCallTracker.this.mPendingMO.finalize();
                        ImsPhoneCallTracker.this.mPendingMO = null;
                        if (ImsPhoneCallTracker.this.mBackgroundCall.getState().isAlive()) {
                            try {
                                ImsPhoneCallTracker imsPhoneCallTracker5 = ImsPhoneCallTracker.this;
                                imsPhoneCallTracker5.hangup(imsPhoneCallTracker5.mBackgroundCall);
                                ImsPhoneCallTracker.this.mPendingSilentRedialInfo = new Pair(Boolean.valueOf(imsReasonInfo.getExtraCode() == 4), Integer.valueOf(emergencyServiceCategories));
                                return;
                            } catch (CallStateException unused) {
                                ImsPhoneCallTracker.this.mPendingSilentRedialInfo = null;
                                return;
                            }
                        }
                        ImsPhoneCallTracker.this.updatePhoneState();
                        ImsPhoneCallTracker.this.mPhone.initiateSilentRedial(imsReasonInfo.getExtraCode() == 4, emergencyServiceCategories);
                        return;
                    }
                    ImsPhoneCallTracker.this.sendCallStartFailedDisconnect(imsCall, imsReasonInfo);
                    ImsPhoneCallTracker.this.mMetrics.writeOnImsCallStartFailed(ImsPhoneCallTracker.this.mPhone.getPhoneId(), imsCall.getCallSession(), imsReasonInfo);
                } else if (imsReasonInfo.getCode() == 146 && ImsPhoneCallTracker.this.mForegroundCall.getState() == Call.State.ALERTING) {
                    ImsPhoneCallTracker.this.log("onCallStartFailed: Initiated call by silent redial under ALERTING state.");
                    ImsPhoneConnection findConnection2 = ImsPhoneCallTracker.this.findConnection(imsCall);
                    if (findConnection2 != null) {
                        ImsPhoneCallTracker.this.mForegroundCall.detach(findConnection2);
                        ImsPhoneCallTracker.this.removeConnection(findConnection2);
                        ImsPhoneCallTracker.this.mImsCallInfoTracker.updateImsCallStatus(findConnection2);
                    }
                    ImsPhoneCallTracker.this.updatePhoneState();
                    ImsPhoneCallTracker.this.mPhone.initiateSilentRedial(imsReasonInfo.getExtraCode() == 4, emergencyServiceCategories);
                }
            }

            public void onCallTerminated(ImsCall imsCall, ImsReasonInfo imsReasonInfo) {
                Call.State state;
                ImsPhone imsPhone2;
                ImsPhoneCallTracker.this.log("onCallTerminated reasonCode=" + imsReasonInfo.getCode());
                ImsPhoneConnection findConnection = ImsPhoneCallTracker.this.findConnection(imsCall);
                if (findConnection != null) {
                    state = findConnection.getState();
                } else {
                    state = Call.State.ACTIVE;
                }
                int disconnectCauseFromReasonInfo = ImsPhoneCallTracker.this.getDisconnectCauseFromReasonInfo(imsReasonInfo, state);
                ImsPhoneCallTracker.this.log("cause = " + disconnectCauseFromReasonInfo + " conn = " + findConnection);
                if (findConnection != null) {
                    ImsVideoCallProviderWrapper videoProvider = findConnection.getVideoProvider();
                    if (videoProvider instanceof ImsVideoCallProviderWrapper) {
                        ImsVideoCallProviderWrapper imsVideoCallProviderWrapper = videoProvider;
                        imsVideoCallProviderWrapper.unregisterForDataUsageUpdate(ImsPhoneCallTracker.this);
                        imsVideoCallProviderWrapper.removeImsVideoProviderCallback(findConnection);
                    }
                }
                int i = 0;
                if (ImsPhoneCallTracker.this.mOnHoldToneId == System.identityHashCode(findConnection)) {
                    if (findConnection != null && ImsPhoneCallTracker.this.mOnHoldToneStarted) {
                        ImsPhoneCallTracker.this.mPhone.stopOnHoldTone(findConnection);
                    }
                    ImsPhoneCallTracker.this.mOnHoldToneStarted = false;
                    ImsPhoneCallTracker.this.mOnHoldToneId = -1;
                }
                if (findConnection != null) {
                    if (findConnection.isPulledCall() && ((imsReasonInfo.getCode() == 1015 || imsReasonInfo.getCode() == 336 || imsReasonInfo.getCode() == 332) && (imsPhone2 = ImsPhoneCallTracker.this.mPhone) != null && imsPhone2.getExternalCallTracker() != null)) {
                        ImsPhoneCallTracker.this.log("Call pull failed.");
                        findConnection.onCallPullFailed(ImsPhoneCallTracker.this.mPhone.getExternalCallTracker().getConnectionById(findConnection.getPulledDialogId()));
                        disconnectCauseFromReasonInfo = 0;
                    } else if (findConnection.isIncoming() && findConnection.getConnectTime() == 0 && disconnectCauseFromReasonInfo != 52) {
                        disconnectCauseFromReasonInfo = (findConnection.getDisconnectCause() == 3 || disconnectCauseFromReasonInfo == 16) ? 16 : 1;
                        ImsPhoneCallTracker.this.log("Incoming connection of 0 connect time detected - translated cause = " + disconnectCauseFromReasonInfo);
                    }
                }
                if (disconnectCauseFromReasonInfo == 2 && findConnection != null && findConnection.getImsCall().isMerged()) {
                    disconnectCauseFromReasonInfo = 45;
                }
                int i2 = disconnectCauseFromReasonInfo;
                if (findConnection != null && imsCall.getSession() != null) {
                    String callId = imsCall.getSession().getCallId();
                    EmergencyNumberTracker emergencyNumberTracker = findConnection.getEmergencyNumberTracker();
                    ImsPhoneCallTracker.this.mMetrics.writeOnImsCallTerminated(ImsPhoneCallTracker.this.mPhone.getPhoneId(), imsCall.getCallSession(), imsReasonInfo, (CallQualityMetrics) ImsPhoneCallTracker.this.mCallQualityMetrics.get(callId), findConnection.getEmergencyNumberInfo(), ImsPhoneCallTracker.this.getNetworkCountryIso(), emergencyNumberTracker != null ? emergencyNumberTracker.getEmergencyNumberDbVersion() : -1);
                    ImsPhoneCallTracker.this.mPhone.getVoiceCallSessionStats().onImsCallTerminated(findConnection, new ImsReasonInfo(ImsPhoneCallTracker.this.maybeRemapReasonCode(imsReasonInfo), imsReasonInfo.mExtraCode, imsReasonInfo.mExtraMessage));
                    CallQualityMetrics callQualityMetrics = (CallQualityMetrics) ImsPhoneCallTracker.this.mCallQualityMetrics.remove(callId);
                    if (callQualityMetrics != null) {
                        ImsPhoneCallTracker.this.mCallQualityMetricsHistory.add(callQualityMetrics);
                    }
                    ImsPhoneCallTracker.this.pruneCallQualityMetricsHistory();
                }
                ImsPhoneCallTracker.this.mPhone.notifyImsReason(imsReasonInfo);
                if (findConnection != null) {
                    findConnection.setPreciseDisconnectCause(ImsPhoneCallTracker.this.getPreciseDisconnectCauseFromReasonInfo(imsReasonInfo));
                    findConnection.setImsReasonInfo(imsReasonInfo);
                }
                if (imsReasonInfo.getCode() == 1514 && DomainSelectionResolver.getInstance().isDomainSelectionSupported()) {
                    if (findConnection != null) {
                        if (imsCall != null && imsCall.getCallProfile() != null) {
                            i = imsCall.getCallProfile().getEmergencyServiceCategories();
                            ImsPhoneCallTracker.this.logi("onCallTerminated eccCategory=" + i);
                        }
                        findConnection.setNonDetectableEmergencyCallInfo(i);
                    }
                    ImsPhoneCallTracker.this.processCallStateChange(imsCall, Call.State.DISCONNECTED, i2);
                } else if (imsReasonInfo.getCode() == 1514 && ImsPhoneCallTracker.this.mAutoRetryFailedWifiEmergencyCall) {
                    Pair pair = new Pair(imsCall, imsReasonInfo);
                    ImsPhoneCallTracker.this.mPhone.getDefaultPhone().mCi.registerForOn(ImsPhoneCallTracker.this, 28, pair);
                    ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                    imsPhoneCallTracker.sendMessageDelayed(imsPhoneCallTracker.obtainMessage(29, pair), 10000L);
                    ((ConnectivityManager) ImsPhoneCallTracker.this.mPhone.getContext().getSystemService("connectivity")).setAirplaneMode(false);
                } else if (imsReasonInfo.getCode() == 3001) {
                    Pair pair2 = new Pair(imsCall, imsReasonInfo);
                    ImsPhoneCallTracker imsPhoneCallTracker2 = ImsPhoneCallTracker.this;
                    imsPhoneCallTracker2.sendMessage(imsPhoneCallTracker2.obtainMessage(32, pair2));
                } else {
                    ImsPhoneCallTracker.this.processCallStateChange(imsCall, Call.State.DISCONNECTED, i2);
                    if (ImsPhoneCallTracker.this.mForegroundCall.getState() != Call.State.ACTIVE && ImsPhoneCallTracker.this.mRingingCall.getState().isRinging()) {
                        ImsPhoneCallTracker.this.mPendingMO = null;
                    }
                    if (ImsPhoneCallTracker.this.mHoldSwitchingState == HoldSwapState.SWAPPING_ACTIVE_AND_HELD) {
                        ImsPhoneCallTracker.this.log("onCallTerminated: Call terminated in the midst of Switching Fg and Bg calls.");
                        if (imsCall == ImsPhoneCallTracker.this.mCallExpectedToResume) {
                            ImsPhoneCallTracker.this.log("onCallTerminated: switching " + ImsPhoneCallTracker.this.mForegroundCall + " with " + ImsPhoneCallTracker.this.mBackgroundCall);
                            ImsPhoneCallTracker imsPhoneCallTracker3 = ImsPhoneCallTracker.this;
                            imsPhoneCallTracker3.mForegroundCall.switchWith(imsPhoneCallTracker3.mBackgroundCall);
                        }
                        ImsPhoneCallTracker imsPhoneCallTracker4 = ImsPhoneCallTracker.this;
                        StringBuilder sb = new StringBuilder();
                        sb.append("onCallTerminated: foreground call in state ");
                        sb.append(ImsPhoneCallTracker.this.mForegroundCall.getState());
                        sb.append(" and ringing call in state ");
                        ImsPhoneCall imsPhoneCall = ImsPhoneCallTracker.this.mRingingCall;
                        sb.append(imsPhoneCall == null ? "null" : imsPhoneCall.getState().toString());
                        imsPhoneCallTracker4.log(sb.toString());
                        ImsPhoneCallTracker.this.sendEmptyMessage(31);
                        ImsPhoneCallTracker.this.mHoldSwitchingState = HoldSwapState.INACTIVE;
                        ImsPhoneCallTracker.this.mCallExpectedToResume = null;
                        ImsPhoneCallTracker.this.logHoldSwapState("onCallTerminated swap active and hold case");
                    } else if (ImsPhoneCallTracker.this.mHoldSwitchingState == HoldSwapState.PENDING_SINGLE_CALL_UNHOLD || ImsPhoneCallTracker.this.mHoldSwitchingState == HoldSwapState.PENDING_SINGLE_CALL_HOLD) {
                        ImsPhoneCallTracker.this.mCallExpectedToResume = null;
                        ImsPhoneCallTracker.this.mHoldSwitchingState = HoldSwapState.INACTIVE;
                        ImsPhoneCallTracker.this.logHoldSwapState("onCallTerminated single call case");
                    } else if (ImsPhoneCallTracker.this.mHoldSwitchingState == HoldSwapState.HOLDING_TO_ANSWER_INCOMING) {
                        if (imsCall == ImsPhoneCallTracker.this.mCallExpectedToResume) {
                            ImsPhoneCallTracker imsPhoneCallTracker5 = ImsPhoneCallTracker.this;
                            imsPhoneCallTracker5.mForegroundCall.switchWith(imsPhoneCallTracker5.mBackgroundCall);
                            ImsPhoneCallTracker.this.mCallExpectedToResume = null;
                            ImsPhoneCallTracker.this.mHoldSwitchingState = HoldSwapState.INACTIVE;
                            ImsPhoneCallTracker.this.logHoldSwapState("onCallTerminated hold to answer case");
                            ImsPhoneCallTracker.this.sendEmptyMessage(31);
                        }
                    } else if (ImsPhoneCallTracker.this.mHoldSwitchingState == HoldSwapState.HOLDING_TO_DIAL_OUTGOING) {
                        if (ImsPhoneCallTracker.this.mPendingMO == null || ImsPhoneCallTracker.this.mPendingMO.getDisconnectCause() != 0) {
                            ImsPhoneCallTracker.this.mHoldSwitchingState = HoldSwapState.INACTIVE;
                            ImsPhoneCallTracker.this.logHoldSwapState("onCallTerminated hold to dial but no pendingMo");
                        } else if (imsCall != ImsPhoneCallTracker.this.mPendingMO.getImsCall()) {
                            ImsPhoneCallTracker.this.sendEmptyMessage(20);
                            ImsPhoneCallTracker.this.mHoldSwitchingState = HoldSwapState.INACTIVE;
                            ImsPhoneCallTracker.this.logHoldSwapState("onCallTerminated hold to dial, dial pendingMo");
                        }
                    }
                    if (ImsPhoneCallTracker.this.mShouldUpdateImsConfigOnDisconnect) {
                        ImsPhoneCallTracker.this.updateImsServiceConfig();
                        ImsPhoneCallTracker.this.mShouldUpdateImsConfigOnDisconnect = false;
                    }
                    if (ImsPhoneCallTracker.this.mPendingSilentRedialInfo != null) {
                        ImsPhoneCallTracker imsPhoneCallTracker6 = ImsPhoneCallTracker.this;
                        imsPhoneCallTracker6.mPhone.initiateSilentRedial(((Boolean) imsPhoneCallTracker6.mPendingSilentRedialInfo.first).booleanValue(), ((Integer) ImsPhoneCallTracker.this.mPendingSilentRedialInfo.second).intValue());
                        ImsPhoneCallTracker.this.mPendingSilentRedialInfo = null;
                    }
                }
            }

            public void onCallHeld(ImsCall imsCall) {
                if (ImsPhoneCallTracker.this.mForegroundCall.getImsCall() == imsCall) {
                    ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                    imsPhoneCallTracker.log("onCallHeld (fg) " + imsCall);
                } else if (ImsPhoneCallTracker.this.mBackgroundCall.getImsCall() == imsCall) {
                    ImsPhoneCallTracker imsPhoneCallTracker2 = ImsPhoneCallTracker.this;
                    imsPhoneCallTracker2.log("onCallHeld (bg) " + imsCall);
                }
                synchronized (ImsPhoneCallTracker.this.mSyncHold) {
                    Call.State state = ImsPhoneCallTracker.this.mBackgroundCall.getState();
                    ImsPhoneCallTracker imsPhoneCallTracker3 = ImsPhoneCallTracker.this;
                    Call.State state2 = Call.State.HOLDING;
                    imsPhoneCallTracker3.processCallStateChange(imsCall, state2, 0);
                    if (ImsPhoneCallTracker.this.mHoldSwitchingState == HoldSwapState.PENDING_RESUME_FOREGROUND_AFTER_HOLD) {
                        ImsPhoneCallTracker.this.sendEmptyMessage(31);
                        ImsPhoneCallTracker.this.mHoldSwitchingState = HoldSwapState.INACTIVE;
                        ImsPhoneCallTracker.this.mCallExpectedToResume = null;
                    } else if (state == Call.State.ACTIVE) {
                        if (ImsPhoneCallTracker.this.mForegroundCall.getState() == state2 && ImsPhoneCallTracker.this.mHoldSwitchingState == HoldSwapState.SWAPPING_ACTIVE_AND_HELD) {
                            ImsPhoneCallTracker.this.sendEmptyMessage(31);
                        } else if (ImsPhoneCallTracker.this.mRingingCall.getState() == Call.State.WAITING && ImsPhoneCallTracker.this.mHoldSwitchingState == HoldSwapState.HOLDING_TO_ANSWER_INCOMING) {
                            ImsPhoneCallTracker.this.sendEmptyMessage(30);
                        } else if (ImsPhoneCallTracker.this.mPendingMO != null && ImsPhoneCallTracker.this.mHoldSwitchingState == HoldSwapState.HOLDING_TO_DIAL_OUTGOING) {
                            ImsPhoneCallTracker.this.dialPendingMO();
                            ImsPhoneCallTracker.this.mHoldSwitchingState = HoldSwapState.INACTIVE;
                            ImsPhoneCallTracker.this.logHoldSwapState("onCallHeld hold to dial");
                        } else {
                            ImsPhoneCallTracker.this.mHoldSwitchingState = HoldSwapState.INACTIVE;
                            ImsPhoneCallTracker.this.logHoldSwapState("onCallHeld normal case");
                        }
                    } else if (state == Call.State.IDLE && ((ImsPhoneCallTracker.this.mHoldSwitchingState == HoldSwapState.SWAPPING_ACTIVE_AND_HELD || ImsPhoneCallTracker.this.mHoldSwitchingState == HoldSwapState.HOLDING_TO_ANSWER_INCOMING) && ImsPhoneCallTracker.this.mForegroundCall.getState() == state2)) {
                        ImsPhoneCallTracker.this.sendEmptyMessage(31);
                        ImsPhoneCallTracker.this.mHoldSwitchingState = HoldSwapState.INACTIVE;
                        ImsPhoneCallTracker.this.mCallExpectedToResume = null;
                        ImsPhoneCallTracker.this.logHoldSwapState("onCallHeld premature termination of other call");
                    }
                }
                ImsPhoneCallTracker.this.mMetrics.writeOnImsCallHeld(ImsPhoneCallTracker.this.mPhone.getPhoneId(), imsCall.getCallSession());
            }

            public void onCallHoldFailed(ImsCall imsCall, ImsReasonInfo imsReasonInfo) {
                ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                imsPhoneCallTracker.log("onCallHoldFailed reasonCode=" + imsReasonInfo.getCode());
                synchronized (ImsPhoneCallTracker.this.mSyncHold) {
                    Call.State state = ImsPhoneCallTracker.this.mBackgroundCall.getState();
                    if (ImsPhoneCallTracker.this.mHoldSwitchingState == HoldSwapState.PENDING_RESUME_FOREGROUND_AFTER_HOLD) {
                        ImsPhoneCallTracker.this.mHoldSwitchingState = HoldSwapState.INACTIVE;
                    } else if (imsReasonInfo.getCode() == 148) {
                        if (ImsPhoneCallTracker.this.mPendingMO != null) {
                            ImsPhoneCallTracker.this.dialPendingMO();
                        } else if (ImsPhoneCallTracker.this.mRingingCall.getState() == Call.State.WAITING && ImsPhoneCallTracker.this.mHoldSwitchingState == HoldSwapState.HOLDING_TO_ANSWER_INCOMING) {
                            ImsPhoneCallTracker.this.sendEmptyMessage(30);
                        }
                        ImsPhoneCallTracker.this.mHoldSwitchingState = HoldSwapState.INACTIVE;
                    } else if (ImsPhoneCallTracker.this.mPendingMO != null && ImsPhoneCallTracker.this.mPendingMO.isEmergency()) {
                        ImsPhoneCallTracker.this.mBackgroundCall.getImsCall().terminate(0);
                        if (imsCall != ImsPhoneCallTracker.this.mCallExpectedToResume) {
                            ImsPhoneCallTracker.this.mCallExpectedToResume = null;
                        }
                    } else if (ImsPhoneCallTracker.this.mRingingCall.getState() == Call.State.WAITING && ImsPhoneCallTracker.this.mHoldSwitchingState == HoldSwapState.HOLDING_TO_ANSWER_INCOMING) {
                        ImsPhoneCallTracker.this.mHoldSwitchingState = HoldSwapState.INACTIVE;
                        ImsPhoneCallTracker imsPhoneCallTracker2 = ImsPhoneCallTracker.this;
                        imsPhoneCallTracker2.mForegroundCall.switchWith(imsPhoneCallTracker2.mBackgroundCall);
                        ImsPhoneCallTracker.this.logHoldSwapState("onCallHoldFailed unable to answer waiting call");
                    } else if (state == Call.State.ACTIVE) {
                        ImsPhoneCallTracker imsPhoneCallTracker3 = ImsPhoneCallTracker.this;
                        imsPhoneCallTracker3.mForegroundCall.switchWith(imsPhoneCallTracker3.mBackgroundCall);
                        if (ImsPhoneCallTracker.this.mPendingMO != null) {
                            ImsPhoneCallTracker.this.mPendingMO.setDisconnectCause(36);
                            ImsPhoneCallTracker.this.sendEmptyMessageDelayed(18, 500L);
                        }
                        if (imsCall != ImsPhoneCallTracker.this.mCallExpectedToResume) {
                            ImsPhoneCallTracker.this.mCallExpectedToResume = null;
                        }
                        ImsPhoneCallTracker.this.mHoldSwitchingState = HoldSwapState.INACTIVE;
                    }
                    ImsPhoneConnection findConnection = ImsPhoneCallTracker.this.findConnection(imsCall);
                    if (findConnection != null && findConnection.getState() != Call.State.DISCONNECTED) {
                        findConnection.onConnectionEvent("android.telecom.event.CALL_HOLD_FAILED", null);
                    }
                    ImsPhoneCallTracker.this.mPhone.notifySuppServiceFailed(PhoneInternalInterface.SuppService.HOLD);
                }
                ImsPhoneCallTracker.this.mMetrics.writeOnImsCallHoldFailed(ImsPhoneCallTracker.this.mPhone.getPhoneId(), imsCall.getCallSession(), imsReasonInfo);
            }

            public void onCallResumed(ImsCall imsCall) {
                ImsPhoneCallTracker.this.log("onCallResumed");
                if (ImsPhoneCallTracker.this.mHoldSwitchingState == HoldSwapState.SWAPPING_ACTIVE_AND_HELD || ImsPhoneCallTracker.this.mHoldSwitchingState == HoldSwapState.PENDING_RESUME_FOREGROUND_AFTER_FAILURE || ImsPhoneCallTracker.this.mHoldSwitchingState == HoldSwapState.PENDING_SINGLE_CALL_UNHOLD) {
                    if (imsCall != ImsPhoneCallTracker.this.mCallExpectedToResume) {
                        ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                        imsPhoneCallTracker.log("onCallResumed : switching " + ImsPhoneCallTracker.this.mForegroundCall + " with " + ImsPhoneCallTracker.this.mBackgroundCall);
                        ImsPhoneCallTracker imsPhoneCallTracker2 = ImsPhoneCallTracker.this;
                        imsPhoneCallTracker2.mForegroundCall.switchWith(imsPhoneCallTracker2.mBackgroundCall);
                    } else {
                        ImsPhoneCallTracker.this.log("onCallResumed : expected call resumed.");
                    }
                    ImsPhoneCallTracker.this.mHoldSwitchingState = HoldSwapState.INACTIVE;
                    ImsPhoneCallTracker.this.mCallExpectedToResume = null;
                    ImsPhoneCallTracker.this.logHoldSwapState("onCallResumed");
                }
                ImsPhoneCallTracker.this.processCallStateChange(imsCall, Call.State.ACTIVE, 0);
                ImsPhoneCallTracker.this.mMetrics.writeOnImsCallResumed(ImsPhoneCallTracker.this.mPhone.getPhoneId(), imsCall.getCallSession());
            }

            public void onCallResumeFailed(ImsCall imsCall, ImsReasonInfo imsReasonInfo) {
                if (ImsPhoneCallTracker.this.mHoldSwitchingState == HoldSwapState.SWAPPING_ACTIVE_AND_HELD || ImsPhoneCallTracker.this.mHoldSwitchingState == HoldSwapState.PENDING_RESUME_FOREGROUND_AFTER_FAILURE) {
                    if (imsCall == ImsPhoneCallTracker.this.mCallExpectedToResume) {
                        ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                        imsPhoneCallTracker.log("onCallResumeFailed : switching " + ImsPhoneCallTracker.this.mForegroundCall + " with " + ImsPhoneCallTracker.this.mBackgroundCall);
                        ImsPhoneCallTracker imsPhoneCallTracker2 = ImsPhoneCallTracker.this;
                        imsPhoneCallTracker2.mForegroundCall.switchWith(imsPhoneCallTracker2.mBackgroundCall);
                        if (ImsPhoneCallTracker.this.mForegroundCall.getState() == Call.State.HOLDING) {
                            ImsPhoneCallTracker.this.sendEmptyMessage(31);
                        }
                    }
                    ImsPhoneCallTracker.this.mCallExpectedToResume = null;
                    ImsPhoneCallTracker.this.mHoldSwitchingState = HoldSwapState.INACTIVE;
                    ImsPhoneCallTracker.this.logHoldSwapState("onCallResumeFailed: multi calls");
                } else if (ImsPhoneCallTracker.this.mHoldSwitchingState == HoldSwapState.PENDING_SINGLE_CALL_UNHOLD) {
                    if (imsCall == ImsPhoneCallTracker.this.mCallExpectedToResume) {
                        ImsPhoneCallTracker.this.log("onCallResumeFailed: single call unhold case");
                        ImsPhoneCallTracker imsPhoneCallTracker3 = ImsPhoneCallTracker.this;
                        imsPhoneCallTracker3.mForegroundCall.switchWith(imsPhoneCallTracker3.mBackgroundCall);
                        ImsPhoneCallTracker.this.mCallExpectedToResume = null;
                        ImsPhoneCallTracker.this.mHoldSwitchingState = HoldSwapState.INACTIVE;
                        ImsPhoneCallTracker.this.logHoldSwapState("onCallResumeFailed: single call");
                    } else {
                        Rlog.w("ImsPhoneCallTracker", "onCallResumeFailed: got a resume failed for a different call in the single call unhold case");
                    }
                }
                ImsPhoneCallTracker.this.mPhone.notifySuppServiceFailed(PhoneInternalInterface.SuppService.RESUME);
                ImsPhoneCallTracker.this.mMetrics.writeOnImsCallResumeFailed(ImsPhoneCallTracker.this.mPhone.getPhoneId(), imsCall.getCallSession(), imsReasonInfo);
            }

            public void onCallResumeReceived(ImsCall imsCall) {
                ImsPhoneCallTracker.this.log("onCallResumeReceived");
                ImsPhoneConnection findConnection = ImsPhoneCallTracker.this.findConnection(imsCall);
                if (findConnection != null) {
                    if (ImsPhoneCallTracker.this.mOnHoldToneStarted) {
                        ImsPhoneCallTracker.this.mPhone.stopOnHoldTone(findConnection);
                        ImsPhoneCallTracker.this.mOnHoldToneStarted = false;
                    }
                    findConnection.onConnectionEvent("android.telecom.event.CALL_REMOTELY_UNHELD", null);
                    ImsPhoneCallTracker.this.mImsCallInfoTracker.updateImsCallStatus(findConnection, false, true);
                }
                if (ImsPhoneCallTracker.this.mPhone.getContext().getResources().getBoolean(17891866) && ImsPhoneCallTracker.this.mSupportPauseVideo && VideoProfile.isVideo(findConnection.getVideoState())) {
                    findConnection.changeToUnPausedState();
                }
                SuppServiceNotification suppServiceNotification = new SuppServiceNotification();
                suppServiceNotification.notificationType = 1;
                suppServiceNotification.code = 3;
                ImsPhoneCallTracker.this.mPhone.notifySuppSvcNotification(suppServiceNotification);
                ImsPhoneCallTracker.this.mMetrics.writeOnImsCallResumeReceived(ImsPhoneCallTracker.this.mPhone.getPhoneId(), imsCall.getCallSession());
            }

            public void onCallHoldReceived(ImsCall imsCall) {
                ImsPhoneCallTracker.this.onCallHoldReceived(imsCall);
            }

            public void onCallSuppServiceReceived(ImsCall imsCall, ImsSuppServiceNotification imsSuppServiceNotification) {
                ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                imsPhoneCallTracker.log("onCallSuppServiceReceived: suppServiceInfo=" + imsSuppServiceNotification);
                SuppServiceNotification suppServiceNotification = new SuppServiceNotification();
                suppServiceNotification.notificationType = imsSuppServiceNotification.notificationType;
                suppServiceNotification.code = imsSuppServiceNotification.code;
                suppServiceNotification.index = imsSuppServiceNotification.index;
                suppServiceNotification.number = imsSuppServiceNotification.number;
                suppServiceNotification.history = imsSuppServiceNotification.history;
                ImsPhoneCallTracker.this.mPhone.notifySuppSvcNotification(suppServiceNotification);
            }

            public void onCallMerged(ImsCall imsCall, ImsCall imsCall2, boolean z) {
                ImsPhoneCallTracker.this.log("onCallMerged");
                ImsPhoneCall call = ImsPhoneCallTracker.this.findConnection(imsCall).getCall();
                ImsPhoneConnection findConnection = ImsPhoneCallTracker.this.findConnection(imsCall2);
                ImsPhoneCall call2 = findConnection == null ? null : findConnection.getCall();
                if (z) {
                    ImsPhoneCallTracker.this.switchAfterConferenceSuccess();
                }
                call.merge(call2, Call.State.ACTIVE);
                ImsPhoneConnection findConnection2 = ImsPhoneCallTracker.this.findConnection(imsCall);
                try {
                    ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                    imsPhoneCallTracker.log("onCallMerged: ImsPhoneConnection=" + findConnection2);
                    ImsPhoneCallTracker imsPhoneCallTracker2 = ImsPhoneCallTracker.this;
                    imsPhoneCallTracker2.log("onCallMerged: CurrentVideoProvider=" + findConnection2.getVideoProvider());
                    ImsPhoneCallTracker.this.setVideoCallProvider(findConnection2, imsCall);
                    ImsPhoneCallTracker imsPhoneCallTracker3 = ImsPhoneCallTracker.this;
                    imsPhoneCallTracker3.log("onCallMerged: CurrentVideoProvider=" + findConnection2.getVideoProvider());
                } catch (Exception e) {
                    ImsPhoneCallTracker imsPhoneCallTracker4 = ImsPhoneCallTracker.this;
                    imsPhoneCallTracker4.loge("onCallMerged: exception " + e);
                }
                ImsPhoneCallTracker imsPhoneCallTracker5 = ImsPhoneCallTracker.this;
                imsPhoneCallTracker5.processCallStateChange(imsPhoneCallTracker5.mForegroundCall.getImsCall(), Call.State.ACTIVE, 0);
                if (findConnection != null) {
                    ImsPhoneCallTracker imsPhoneCallTracker6 = ImsPhoneCallTracker.this;
                    imsPhoneCallTracker6.processCallStateChange(imsPhoneCallTracker6.mBackgroundCall.getImsCall(), Call.State.HOLDING, 0);
                }
                if (!imsCall.isMergeRequestedByConf()) {
                    ImsPhoneCallTracker.this.log("onCallMerged :: calling onMultipartyStateChanged()");
                    onMultipartyStateChanged(imsCall, true);
                } else {
                    ImsPhoneCallTracker.this.log("onCallMerged :: Merge requested by existing conference.");
                    imsCall.resetIsMergeRequestedByConf(false);
                }
                if (findConnection2 != null) {
                    findConnection2.handleMergeComplete();
                }
                ImsPhoneCallTracker.this.logState();
            }

            public void onCallMergeFailed(ImsCall imsCall, ImsReasonInfo imsReasonInfo) {
                ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                imsPhoneCallTracker.log("onCallMergeFailed reasonInfo=" + imsReasonInfo);
                ImsPhoneCallTracker.this.mPhone.notifySuppServiceFailed(PhoneInternalInterface.SuppService.CONFERENCE);
                imsCall.resetIsMergeRequestedByConf(false);
                ImsPhoneConnection firstConnection = ImsPhoneCallTracker.this.mForegroundCall.getFirstConnection();
                if (firstConnection != null) {
                    firstConnection.onConferenceMergeFailed();
                    firstConnection.handleMergeComplete();
                }
                ImsPhoneConnection firstConnection2 = ImsPhoneCallTracker.this.mBackgroundCall.getFirstConnection();
                if (firstConnection2 != null) {
                    firstConnection2.onConferenceMergeFailed();
                    firstConnection2.handleMergeComplete();
                }
            }

            private void updateConferenceParticipantsTiming(List<ConferenceParticipant> list) {
                for (ConferenceParticipant conferenceParticipant : list) {
                    CacheEntry findConnectionTimeUsePhoneNumber = ImsPhoneCallTracker.this.findConnectionTimeUsePhoneNumber(conferenceParticipant);
                    if (findConnectionTimeUsePhoneNumber != null) {
                        conferenceParticipant.setConnectTime(findConnectionTimeUsePhoneNumber.mConnectTime);
                        conferenceParticipant.setConnectElapsedTime(findConnectionTimeUsePhoneNumber.mConnectElapsedTime);
                        conferenceParticipant.setCallDirection(findConnectionTimeUsePhoneNumber.mCallDirection);
                    }
                }
            }

            public void onConferenceParticipantsStateChanged(ImsCall imsCall, List<ConferenceParticipant> list) {
                ImsPhoneCallTracker.this.log("onConferenceParticipantsStateChanged");
                if (!ImsPhoneCallTracker.this.mIsConferenceEventPackageEnabled) {
                    ImsPhoneCallTracker.this.logi("onConferenceParticipantsStateChanged - CEP handling disabled");
                } else if (!ImsPhoneCallTracker.this.mSupportCepOnPeer && !imsCall.isConferenceHost()) {
                    ImsPhoneCallTracker.this.logi("onConferenceParticipantsStateChanged - ignore CEP on peer");
                } else {
                    ImsPhoneConnection findConnection = ImsPhoneCallTracker.this.findConnection(imsCall);
                    if (findConnection != null) {
                        updateConferenceParticipantsTiming(list);
                        findConnection.updateConferenceParticipants(list);
                    }
                }
            }

            public void onCallSessionTtyModeReceived(ImsCall imsCall, int i) {
                ImsPhoneCallTracker.this.mPhone.onTtyModeReceived(i);
            }

            public void onCallHandover(ImsCall imsCall, int i, int i2, ImsReasonInfo imsReasonInfo) {
                boolean isDataEnabled = ImsPhoneCallTracker.this.mPhone.getDefaultPhone().getDataSettingsManager().isDataEnabled();
                ImsPhoneCallTracker.this.log("onCallHandover ::  srcAccessTech=" + i + ", targetAccessTech=" + i2 + ", reasonInfo=" + imsReasonInfo + ", dataEnabled=" + ImsPhoneCallTracker.this.mIsDataEnabled + "/" + isDataEnabled + ", dataMetered=" + ImsPhoneCallTracker.this.mIsViLteDataMetered);
                if (ImsPhoneCallTracker.this.mIsDataEnabled != isDataEnabled) {
                    ImsPhoneCallTracker.this.loge("onCallHandover: data enabled state doesn't match! (was=" + ImsPhoneCallTracker.this.mIsDataEnabled + ", actually=" + isDataEnabled);
                    ImsPhoneCallTracker.this.mIsDataEnabled = isDataEnabled;
                }
                boolean z = false;
                boolean z2 = (i == 0 || i == 18 || i2 != 18) ? false : true;
                if (i == 18 && i2 != 0 && i2 != 18) {
                    z = true;
                }
                ImsPhoneConnection findConnection = ImsPhoneCallTracker.this.findConnection(imsCall);
                if (findConnection != null) {
                    if (findConnection.getDisconnectCause() == 0) {
                        if (z2) {
                            ImsPhoneCallTracker.this.removeMessages(25);
                            if (ImsPhoneCallTracker.this.mNotifyHandoverVideoFromLTEToWifi && ImsPhoneCallTracker.this.mHasAttemptedStartOfCallHandover) {
                                findConnection.onConnectionEvent("android.telephony.event.EVENT_HANDOVER_VIDEO_FROM_LTE_TO_WIFI", null);
                            }
                            ImsPhoneCallTracker.this.unregisterForConnectivityChanges();
                        } else if (z && imsCall.isVideoCall()) {
                            ImsPhoneCallTracker.this.registerForConnectivityChanges();
                        }
                    }
                    if (z2 && ImsPhoneCallTracker.this.mIsViLteDataMetered) {
                        findConnection.setLocalVideoCapable(true);
                    }
                    if (z && imsCall.isVideoCall()) {
                        if (ImsPhoneCallTracker.this.mIsViLteDataMetered) {
                            findConnection.setLocalVideoCapable(ImsPhoneCallTracker.this.mIsDataEnabled);
                        }
                        if (ImsPhoneCallTracker.this.mNotifyHandoverVideoFromWifiToLTE && ImsPhoneCallTracker.this.mIsDataEnabled) {
                            if (findConnection.getDisconnectCause() == 0) {
                                ImsPhoneCallTracker.this.log("onCallHandover :: notifying of WIFI to LTE handover.");
                                findConnection.onConnectionEvent("android.telephony.event.EVENT_HANDOVER_VIDEO_FROM_WIFI_TO_LTE", null);
                            } else {
                                ImsPhoneCallTracker.this.log("onCallHandover :: skip notify of WIFI to LTE handover for disconnected call.");
                            }
                        }
                        if (!ImsPhoneCallTracker.this.mIsDataEnabled && ImsPhoneCallTracker.this.mIsViLteDataMetered) {
                            ImsPhoneCallTracker.this.log("onCallHandover :: data is not enabled; attempt to downgrade.");
                            ImsPhoneCallTracker.this.downgradeVideoCall(1407, findConnection);
                        }
                    }
                } else {
                    ImsPhoneCallTracker.this.loge("onCallHandover :: connection null.");
                }
                if (!ImsPhoneCallTracker.this.mHasAttemptedStartOfCallHandover) {
                    ImsPhoneCallTracker.this.mHasAttemptedStartOfCallHandover = true;
                }
                ImsPhoneCallTracker.this.mMetrics.writeOnImsCallHandoverEvent(ImsPhoneCallTracker.this.mPhone.getPhoneId(), 18, imsCall.getCallSession(), i, i2, imsReasonInfo);
            }

            public void onCallHandoverFailed(ImsCall imsCall, int i, int i2, ImsReasonInfo imsReasonInfo) {
                ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                imsPhoneCallTracker.log("onCallHandoverFailed :: srcAccessTech=" + i + ", targetAccessTech=" + i2 + ", reasonInfo=" + imsReasonInfo);
                ImsPhoneCallTracker.this.mMetrics.writeOnImsCallHandoverEvent(ImsPhoneCallTracker.this.mPhone.getPhoneId(), 19, imsCall.getCallSession(), i, i2, imsReasonInfo);
                boolean z = i != 18 && i2 == 18;
                ImsPhoneConnection findConnection = ImsPhoneCallTracker.this.findConnection(imsCall);
                if (findConnection != null && z) {
                    ImsPhoneCallTracker.this.log("onCallHandoverFailed - handover to WIFI Failed");
                    ImsPhoneCallTracker.this.removeMessages(25);
                    if (imsCall.isVideoCall() && findConnection.getDisconnectCause() == 0) {
                        ImsPhoneCallTracker.this.registerForConnectivityChanges();
                    }
                    if (ImsPhoneCallTracker.this.mNotifyVtHandoverToWifiFail) {
                        findConnection.onHandoverToWifiFailed();
                    }
                }
                if (ImsPhoneCallTracker.this.mHasAttemptedStartOfCallHandover) {
                    return;
                }
                ImsPhoneCallTracker.this.mHasAttemptedStartOfCallHandover = true;
            }

            public void onRttModifyRequestReceived(ImsCall imsCall) {
                ImsPhoneConnection findConnection = ImsPhoneCallTracker.this.findConnection(imsCall);
                if (findConnection != null) {
                    findConnection.onRttModifyRequestReceived();
                }
            }

            public void onRttModifyResponseReceived(ImsCall imsCall, int i) {
                ImsPhoneConnection findConnection = ImsPhoneCallTracker.this.findConnection(imsCall);
                if (findConnection != null) {
                    findConnection.onRttModifyResponseReceived(i);
                }
            }

            public void onRttMessageReceived(ImsCall imsCall, String str) {
                ImsPhoneConnection findConnection = ImsPhoneCallTracker.this.findConnection(imsCall);
                if (findConnection != null) {
                    findConnection.onRttMessageReceived(str);
                }
            }

            public void onRttAudioIndicatorChanged(ImsCall imsCall, ImsStreamMediaProfile imsStreamMediaProfile) {
                ImsPhoneConnection findConnection = ImsPhoneCallTracker.this.findConnection(imsCall);
                if (findConnection != null) {
                    findConnection.onRttAudioIndicatorChanged(imsStreamMediaProfile);
                }
            }

            public void onCallSessionTransferred(ImsCall imsCall) {
                ImsPhoneCallTracker.this.log("onCallSessionTransferred success");
            }

            public void onCallSessionTransferFailed(ImsCall imsCall, ImsReasonInfo imsReasonInfo) {
                ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                imsPhoneCallTracker.log("onCallSessionTransferFailed reasonInfo=" + imsReasonInfo);
                ImsPhoneCallTracker.this.mPhone.notifySuppServiceFailed(PhoneInternalInterface.SuppService.TRANSFER);
            }

            public void onCallSessionDtmfReceived(ImsCall imsCall, char c) {
                ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                imsPhoneCallTracker.log("onCallSessionDtmfReceived digit=" + c);
                ImsPhoneConnection findConnection = ImsPhoneCallTracker.this.findConnection(imsCall);
                if (findConnection != null) {
                    findConnection.receivedDtmfDigit(c);
                }
            }

            public void onMultipartyStateChanged(ImsCall imsCall, boolean z) {
                ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                StringBuilder sb = new StringBuilder();
                sb.append("onMultipartyStateChanged to ");
                sb.append(z ? "Y" : "N");
                imsPhoneCallTracker.log(sb.toString());
                ImsPhoneConnection findConnection = ImsPhoneCallTracker.this.findConnection(imsCall);
                if (findConnection != null) {
                    findConnection.updateMultipartyState(z);
                    ImsPhoneCallTracker.this.mPhone.getVoiceCallSessionStats().onMultipartyChange(findConnection, z);
                }
            }

            public void onCallQualityChanged(ImsCall imsCall, CallQuality callQuality) {
                ImsPhoneCallTracker.this.mPhone.onCallQualityChanged(callQuality, imsCall.getNetworkType());
                String callId = imsCall.getSession().getCallId();
                CallQualityMetrics callQualityMetrics = (CallQualityMetrics) ImsPhoneCallTracker.this.mCallQualityMetrics.get(callId);
                if (callQualityMetrics == null) {
                    callQualityMetrics = new CallQualityMetrics(ImsPhoneCallTracker.this.mPhone);
                }
                callQualityMetrics.saveCallQuality(callQuality);
                ImsPhoneCallTracker.this.mCallQualityMetrics.put(callId, callQualityMetrics);
                ImsPhoneConnection findConnection = ImsPhoneCallTracker.this.findConnection(imsCall);
                if (findConnection != null) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("android.telecom.extra.CALL_QUALITY_REPORT", callQuality);
                    findConnection.onConnectionEvent("android.telecom.event.CALL_QUALITY_REPORT", bundle);
                }
            }

            public void onCallSessionRtpHeaderExtensionsReceived(ImsCall imsCall, Set<RtpHeaderExtension> set) {
                ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                imsPhoneCallTracker.log("onCallSessionRtpHeaderExtensionsReceived numExtensions=" + set.size());
                ImsPhoneConnection findConnection = ImsPhoneCallTracker.this.findConnection(imsCall);
                if (findConnection != null) {
                    findConnection.receivedRtpHeaderExtensions(set);
                }
            }

            public void onCallSessionSendAnbrQuery(ImsCall imsCall, int i, int i2, int i3) {
                ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                imsPhoneCallTracker.log("onCallSessionSendAnbrQuery mediaType=" + i + ", direction=" + i2 + ", bitPerSecond=" + i3);
                ImsPhoneCallTracker.this.handleSendAnbrQuery(i, i2, i3);
            }
        };
        this.mImsUssdListener = new ImsCall.Listener() { // from class: com.android.internal.telephony.imsphone.ImsPhoneCallTracker.9
            public void onCallStarted(ImsCall imsCall) {
                ImsPhoneCallTracker.this.log("mImsUssdListener onCallStarted");
                if (imsCall != ImsPhoneCallTracker.this.mUssdSession || ImsPhoneCallTracker.this.mPendingUssd == null) {
                    return;
                }
                AsyncResult.forMessage(ImsPhoneCallTracker.this.mPendingUssd);
                ImsPhoneCallTracker.this.mPendingUssd.sendToTarget();
                ImsPhoneCallTracker.this.mPendingUssd = null;
            }

            public void onCallStartFailed(ImsCall imsCall, ImsReasonInfo imsReasonInfo) {
                ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                imsPhoneCallTracker.log("mImsUssdListener onCallStartFailed reasonCode=" + imsReasonInfo.getCode());
                if (ImsPhoneCallTracker.this.mUssdSession != null) {
                    ImsPhoneCallTracker.this.log("mUssdSession is not null");
                    if (imsReasonInfo.getCode() == 146 && ImsPhoneCallTracker.this.mUssdMethod != 3) {
                        ImsPhoneCallTracker.this.mUssdSession = null;
                        ImsPhoneCallTracker.this.mPhone.getPendingMmiCodes().clear();
                        ImsPhoneCallTracker.this.mPhone.initiateSilentRedial();
                        ImsPhoneCallTracker.this.log("Initiated sending ussd by using silent redial.");
                        return;
                    }
                    ImsPhoneCallTracker.this.log("Failed to start sending ussd by using silent resendUssd.!!");
                }
                onCallTerminated(imsCall, imsReasonInfo);
            }

            public void onCallTerminated(ImsCall imsCall, ImsReasonInfo imsReasonInfo) {
                ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                imsPhoneCallTracker.log("mImsUssdListener onCallTerminated reasonCode=" + imsReasonInfo.getCode());
                ImsPhoneCallTracker.this.removeMessages(25);
                ImsPhoneCallTracker.this.mHasAttemptedStartOfCallHandover = false;
                ImsPhoneCallTracker.this.unregisterForConnectivityChanges();
                if (imsCall == ImsPhoneCallTracker.this.mUssdSession) {
                    ImsPhoneCallTracker.this.mUssdSession = null;
                    if (ImsPhoneCallTracker.this.mPendingUssd != null) {
                        AsyncResult.forMessage(ImsPhoneCallTracker.this.mPendingUssd, (Object) null, new CommandException(CommandException.Error.GENERIC_FAILURE));
                        ImsPhoneCallTracker.this.mPendingUssd.sendToTarget();
                        ImsPhoneCallTracker.this.mPendingUssd = null;
                    }
                }
                imsCall.close();
            }

            public void onCallUssdMessageReceived(ImsCall imsCall, int i, String str) {
                int i2;
                ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                imsPhoneCallTracker.log("mImsUssdListener onCallUssdMessageReceived mode=" + i);
                if (i != 0) {
                    i2 = 1;
                    if (i != 1) {
                        i2 = -1;
                    }
                } else {
                    i2 = 0;
                }
                ImsPhoneCallTracker.this.mPhone.onIncomingUSSD(i2, str);
            }
        };
        this.mImsCapabilityCallback = new ImsMmTelManager.CapabilityCallback() { // from class: com.android.internal.telephony.imsphone.ImsPhoneCallTracker.10
            @Override // android.telephony.ims.ImsMmTelManager.CapabilityCallback
            public void onCapabilitiesStatusChanged(MmTelFeature.MmTelCapabilities mmTelCapabilities) {
                ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                imsPhoneCallTracker.log("onCapabilitiesStatusChanged: " + mmTelCapabilities);
                SomeArgs obtain = SomeArgs.obtain();
                obtain.arg1 = mmTelCapabilities;
                ImsPhoneCallTracker.this.removeMessages(26);
                ImsPhoneCallTracker.this.obtainMessage(26, obtain).sendToTarget();
            }
        };
        this.mImsStatsCallback = new ImsManager.ImsStatsCallback() { // from class: com.android.internal.telephony.imsphone.ImsPhoneCallTracker.11
            public void onEnabledMmTelCapabilitiesChanged(int i, int i2, boolean z) {
                ImsPhoneCallTracker.this.mMetrics.writeImsSetFeatureValue(ImsPhoneCallTracker.this.mPhone.getPhoneId(), i, i2, z ? 1 : 0);
                ImsPhoneCallTracker.this.mPhone.getImsStats().onSetFeatureResponse(i, i2, z ? 1 : 0);
            }
        };
        this.mConfigCallback = new ProvisioningManager.Callback() { // from class: com.android.internal.telephony.imsphone.ImsPhoneCallTracker.12
            public void onProvisioningIntChanged(int i, int i2) {
                sendConfigChangedIntent(i, Integer.toString(i2));
                if (ImsPhoneCallTracker.this.mImsManager != null) {
                    if (i == 28 || i == 10 || i == 11) {
                        ImsPhoneCallTracker.this.updateImsServiceConfig();
                    }
                }
            }

            public void onProvisioningStringChanged(int i, String str) {
                sendConfigChangedIntent(i, str);
            }

            private void sendConfigChangedIntent(int i, String str) {
                ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                imsPhoneCallTracker.log("sendConfigChangedIntent - [" + i + ", " + str + "]");
                Intent intent = new Intent("com.android.intent.action.IMS_CONFIG_CHANGED");
                intent.putExtra("item", i);
                intent.putExtra("value", str);
                ImsPhone imsPhone2 = ImsPhoneCallTracker.this.mPhone;
                if (imsPhone2 == null || imsPhone2.getContext() == null) {
                    return;
                }
                ImsPhoneCallTracker.this.mPhone.getContext().sendBroadcast(intent, "android.permission.READ_PRIVILEGED_PHONE_STATE");
            }
        };
        this.mPhone = imsPhone;
        this.mConnectorFactory = connectorFactory;
        if (executor != null) {
            this.mExecutor = executor;
        }
        this.mMetrics = TelephonyMetrics.getInstance();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.telecom.action.DEFAULT_DIALER_CHANGED");
        this.mPhone.getContext().registerReceiver(broadcastReceiver, intentFilter);
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mPhone.getContext().getSystemService(CarrierConfigManager.class);
        if (carrierConfigManager != null) {
            carrierConfigManager.registerCarrierConfigChangeListener(this.mPhone.getContext().getMainExecutor(), this.mCarrierConfigChangeListener);
            updateCarrierConfiguration(this.mPhone.getSubId(), getCarrierConfigBundle(this.mPhone.getSubId()));
        } else {
            loge("CarrierConfigManager is not available.");
        }
        this.mSettingsCallback = new DataSettingsManager.DataSettingsManagerCallback(new ImsPhoneCallTracker$$ExternalSyntheticLambda0(this)) { // from class: com.android.internal.telephony.imsphone.ImsPhoneCallTracker.5
            @Override // com.android.internal.telephony.data.DataSettingsManager.DataSettingsManagerCallback
            public void onDataEnabledChanged(boolean z, int i, String str) {
                ImsPhoneCallTracker.this.onDataEnabledChanged(z, i);
            }
        };
        this.mPhone.getDefaultPhone().getDataSettingsManager().registerCallback(this.mSettingsCallback);
        atomicInteger.set(getPackageUid(this.mPhone.getContext(), ((TelecomManager) this.mPhone.getContext().getSystemService("telecom")).getDefaultDialerPackage()));
        long elapsedRealtime = SystemClock.elapsedRealtime();
        this.mVtDataUsageSnapshot = new NetworkStats(elapsedRealtime, 1);
        this.mVtDataUsageUidSnapshot = new NetworkStats(elapsedRealtime, 1);
        ((NetworkStatsManager) this.mPhone.getContext().getSystemService("netstats")).registerNetworkStatsProvider("ImsPhoneCallTracker", vtDataUsageProvider);
        this.mImsManagerConnector = connectorFactory.create(this.mPhone.getContext(), this.mPhone.getPhoneId(), "ImsPhoneCallTracker", new FeatureConnector.Listener<ImsManager>() { // from class: com.android.internal.telephony.imsphone.ImsPhoneCallTracker.6
            public void connectionReady(ImsManager imsManager, int i) throws ImsException {
                ImsPhoneCallTracker.this.mImsManager = imsManager;
                ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                imsPhoneCallTracker.log("connectionReady for subId = " + i);
                ImsPhoneCallTracker.this.startListeningForCalls(i);
            }

            public void connectionUnavailable(int i) {
                ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                imsPhoneCallTracker.logi("connectionUnavailable: " + i);
                if (i == 3) {
                    ImsPhoneCallTracker imsPhoneCallTracker2 = ImsPhoneCallTracker.this;
                    imsPhoneCallTracker2.postDelayed(imsPhoneCallTracker2.mConnectorRunnable, 5000L);
                }
                ImsPhoneCallTracker.this.stopListeningForCalls();
                ImsPhoneCallTracker.this.stopAllImsTrafficTypes();
            }
        }, executor);
        post(this.mConnectorRunnable);
        this.mImsCallInfoTracker = new ImsCallInfoTracker(imsPhone);
        this.mPhone.registerForConnectionSetupFailure(this, 34, null);
    }

    @VisibleForTesting
    public void setSharedPreferenceProxy(SharedPreferenceProxy sharedPreferenceProxy) {
        this.mSharedPreferenceProxy = sharedPreferenceProxy;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getPackageUid(Context context, String str) {
        if (str == null) {
            return -1;
        }
        try {
            return context.getPackageManager().getPackageUid(str, 0);
        } catch (PackageManager.NameNotFoundException unused) {
            loge("Cannot find package uid. pkg = " + str);
            return -1;
        }
    }

    @VisibleForTesting
    public void startListeningForCalls(int i) throws ImsException {
        log("startListeningForCalls");
        this.mOperationLocalLog.log("startListeningForCalls - Connecting to ImsService");
        ImsExternalCallTracker externalCallTracker = this.mPhone.getExternalCallTracker();
        this.mImsManager.open(this.mMmTelFeatureListener, this.mPhone.getImsEcbmStateListener(), externalCallTracker != null ? externalCallTracker.getExternalCallStateListener() : null);
        this.mImsManager.addRegistrationCallback(this.mPhone.getImsMmTelRegistrationCallback(), new ImsPhoneCallTracker$$ExternalSyntheticLambda0(this));
        this.mImsManager.addCapabilitiesCallback(this.mImsCapabilityCallback, new ImsPhoneCallTracker$$ExternalSyntheticLambda0(this));
        ImsManager.setImsStatsCallback(this.mPhone.getPhoneId(), this.mImsStatsCallback);
        this.mImsManager.getConfigInterface().addConfigCallback(this.mConfigCallback);
        if (this.mPhone.isInEcm()) {
            this.mPhone.exitEmergencyCallbackMode();
        }
        this.mImsManager.setUiTTYMode(this.mPhone.getContext(), Settings.Secure.getInt(this.mPhone.getContext().getContentResolver(), "preferred_tty_mode", 0), (Message) null);
        ImsUtInterface utInterface = getUtInterface();
        this.mUtInterface = utInterface;
        if (utInterface != null) {
            utInterface.registerForSuppServiceIndication(this, 27, (Object) null);
        }
        Pair<Integer, PersistableBundle> pair = this.mCarrierConfigForSubId;
        if (pair != null && ((Integer) pair.first).intValue() == i) {
            updateCarrierConfiguration(i, (PersistableBundle) this.mCarrierConfigForSubId.second);
        } else {
            log("startListeningForCalls - waiting for the first carrier config indication for this subscription");
        }
        sendImsServiceStateIntent("com.android.ims.IMS_SERVICE_UP");
        this.mCurrentlyConnectedSubId = Optional.of(Integer.valueOf(i));
        initializeTerminalBasedCallWaiting();
    }

    private void maybeConfigureRtpHeaderExtensions() {
        Config config;
        if (this.mDeviceToDeviceForceEnabled || ((config = this.mConfig) != null && config.isD2DCommunicationSupported && this.mSupportD2DUsingRtp)) {
            ArraySet arraySet = new ArraySet();
            if (this.mSupportSdpForRtpHeaderExtensions) {
                arraySet.add(RtpTransport.CALL_STATE_RTP_HEADER_EXTENSION_TYPE);
                arraySet.add(RtpTransport.DEVICE_STATE_RTP_HEADER_EXTENSION_TYPE);
                logi("maybeConfigureRtpHeaderExtensions: set offered RTP header extension types");
            } else {
                logi("maybeConfigureRtpHeaderExtensions: SDP negotiation not supported; not setting offered RTP header extension types");
            }
            try {
                this.mImsManager.setOfferedRtpHeaderExtensionTypes(arraySet);
            } catch (ImsException e) {
                loge("maybeConfigureRtpHeaderExtensions: failed to set extensions; " + e);
            }
        }
    }

    public void setDeviceToDeviceForceEnabled(boolean z) {
        this.mDeviceToDeviceForceEnabled = z;
        maybeConfigureRtpHeaderExtensions();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopListeningForCalls() {
        log("stopListeningForCalls");
        this.mOperationLocalLog.log("stopListeningForCalls - Disconnecting from ImsService");
        ImsManager imsManager = this.mImsManager;
        if (imsManager != null) {
            imsManager.removeRegistrationListener(this.mPhone.getImsMmTelRegistrationCallback());
            this.mImsManager.removeCapabilitiesCallback(this.mImsCapabilityCallback);
            try {
                ImsManager.setImsStatsCallback(this.mPhone.getPhoneId(), (ImsManager.ImsStatsCallback) null);
                this.mImsManager.getConfigInterface().removeConfigCallback(this.mConfigCallback.getBinder());
            } catch (ImsException unused) {
                Log.w("ImsPhoneCallTracker", "stopListeningForCalls: unable to remove config callback.");
            }
            this.mImsManager.close();
        }
        ImsUtInterface imsUtInterface = this.mUtInterface;
        if (imsUtInterface != null) {
            imsUtInterface.unregisterForSuppServiceIndication(this);
            this.mUtInterface = null;
        }
        this.mCurrentlyConnectedSubId = Optional.empty();
        this.mMediaThreshold = null;
        resetImsCapabilities();
        hangupAllOrphanedConnections(14);
        sendImsServiceStateIntent("com.android.ims.IMS_SERVICE_DOWN");
    }

    @VisibleForTesting
    public void hangupAllOrphanedConnections(int i) {
        Log.w("ImsPhoneCallTracker", "hangupAllOngoingConnections called for cause " + i);
        int size = getConnections().size();
        while (true) {
            size--;
            if (size <= -1) {
                break;
            }
            try {
                getConnections().get(size).hangup();
            } catch (CallStateException unused) {
                loge("Failed to disconnet call...");
            }
        }
        Iterator<ImsPhoneConnection> it = this.mConnections.iterator();
        while (it.hasNext()) {
            ImsPhoneConnection next = it.next();
            next.update(next.getImsCall(), Call.State.DISCONNECTED);
            next.onDisconnect(i);
            next.getCall().detach(next);
        }
        this.mConnections.clear();
        this.mPendingMO = null;
        updatePhoneState();
        this.mImsCallInfoTracker.clearAllOrphanedConnections();
    }

    public void hangupAllConnections() {
        getConnections().stream().forEach(new Consumer() { // from class: com.android.internal.telephony.imsphone.ImsPhoneCallTracker$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ImsPhoneCallTracker.this.lambda$hangupAllConnections$1((ImsPhoneConnection) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$hangupAllConnections$1(ImsPhoneConnection imsPhoneConnection) {
        logi("Disconnecting callId = " + imsPhoneConnection.getTelecomCallId());
        try {
            imsPhoneConnection.hangup();
        } catch (CallStateException unused) {
            loge("Failed to disconnet call...");
        }
    }

    private void sendImsServiceStateIntent(String str) {
        Intent intent = new Intent(str);
        intent.putExtra("android:phone_id", this.mPhone.getPhoneId());
        ImsPhone imsPhone = this.mPhone;
        if (imsPhone == null || imsPhone.getContext() == null) {
            return;
        }
        this.mPhone.getContext().sendBroadcast(intent);
    }

    public void dispose() {
        CarrierConfigManager.CarrierConfigChangeListener carrierConfigChangeListener;
        log("dispose");
        this.mRingingCall.dispose();
        this.mBackgroundCall.dispose();
        this.mForegroundCall.dispose();
        this.mHandoverCall.dispose();
        clearDisconnected();
        this.mPhone.getContext().unregisterReceiver(this.mReceiver);
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mPhone.getContext().getSystemService(CarrierConfigManager.class);
        if (carrierConfigManager != null && (carrierConfigChangeListener = this.mCarrierConfigChangeListener) != null) {
            carrierConfigManager.unregisterCarrierConfigChangeListener(carrierConfigChangeListener);
        }
        this.mPhone.getDefaultPhone().getDataSettingsManager().unregisterCallback(this.mSettingsCallback);
        this.mImsManagerConnector.disconnect();
        ((NetworkStatsManager) this.mPhone.getContext().getSystemService("netstats")).unregisterNetworkStatsProvider(this.mVtDataUsageProvider);
        this.mPhone.unregisterForConnectionSetupFailure(this);
    }

    protected void finalize() {
        log("ImsPhoneCallTracker finalized");
    }

    @Override // com.android.internal.telephony.CallTracker
    public void registerForVoiceCallStarted(Handler handler, int i, Object obj) {
        this.mVoiceCallStartedRegistrants.add(new Registrant(handler, i, obj));
    }

    @Override // com.android.internal.telephony.CallTracker
    public void unregisterForVoiceCallStarted(Handler handler) {
        this.mVoiceCallStartedRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CallTracker
    public void registerForVoiceCallEnded(Handler handler, int i, Object obj) {
        this.mVoiceCallEndedRegistrants.add(new Registrant(handler, i, obj));
    }

    @Override // com.android.internal.telephony.CallTracker
    public void unregisterForVoiceCallEnded(Handler handler) {
        this.mVoiceCallEndedRegistrants.remove(handler);
    }

    public int getClirMode() {
        if (this.mSharedPreferenceProxy != null && this.mPhone.getDefaultPhone() != null) {
            SharedPreferences defaultSharedPreferences = this.mSharedPreferenceProxy.getDefaultSharedPreferences(this.mPhone.getContext());
            return defaultSharedPreferences.getInt(Phone.CLIR_KEY + this.mPhone.getSubId(), 0);
        }
        loge("dial; could not get default CLIR mode.");
        return 0;
    }

    private boolean prepareForDialing(ImsPhone.ImsDialArgs imsDialArgs) throws CallStateException {
        boolean z;
        clearDisconnected();
        if (this.mImsManager == null) {
            throw new CallStateException("service not available");
        }
        checkForDialIssues();
        int i = imsDialArgs.videoState;
        if (!canAddVideoCallDuringImsAudioCall(i)) {
            throw new CallStateException("cannot dial in current state");
        }
        Call.State state = this.mForegroundCall.getState();
        Call.State state2 = Call.State.ACTIVE;
        boolean z2 = false;
        if (state != state2) {
            z = false;
        } else if (this.mBackgroundCall.getState() != Call.State.IDLE) {
            throw new CallStateException(6, "Already too many ongoing calls.");
        } else {
            this.mPendingCallVideoState = i;
            this.mPendingIntentExtras = imsDialArgs.intentExtras;
            holdActiveCallForPendingMo();
            z = true;
        }
        synchronized (this.mSyncHold) {
            if (z) {
                Call.State state3 = this.mForegroundCall.getState();
                Call.State state4 = this.mBackgroundCall.getState();
                if (state3 == state2) {
                    throw new CallStateException("cannot dial in current state");
                }
                if (state4 == Call.State.HOLDING) {
                }
            }
            z2 = z;
        }
        return z2;
    }

    public com.android.internal.telephony.Connection startConference(String[] strArr, ImsPhone.ImsDialArgs imsDialArgs) throws CallStateException {
        ImsPhoneConnection imsPhoneConnection;
        int i = imsDialArgs.clirMode;
        int i2 = imsDialArgs.videoState;
        log("dial clirMode=" + i);
        boolean prepareForDialing = prepareForDialing(imsDialArgs);
        this.mClirMode = i;
        synchronized (this.mSyncHold) {
            this.mLastDialArgs = imsDialArgs;
            imsPhoneConnection = new ImsPhoneConnection((Phone) this.mPhone, strArr, this, this.mForegroundCall, false);
            this.mPendingMO = imsPhoneConnection;
            imsPhoneConnection.setVideoState(i2);
            if (imsDialArgs.rttTextStream != null) {
                log("startConference: setting RTT stream on mPendingMO");
                imsPhoneConnection.setCurrentRttTextStream(imsDialArgs.rttTextStream);
            }
        }
        addConnection(imsPhoneConnection);
        if (!prepareForDialing) {
            dialInternal(imsPhoneConnection, i, i2, imsDialArgs.intentExtras);
        }
        updatePhoneState();
        this.mPhone.notifyPreciseCallStateChanged();
        return imsPhoneConnection;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public com.android.internal.telephony.Connection dial(String str, int i, Bundle bundle) throws CallStateException {
        return dial(str, new ImsPhone.ImsDialArgs.Builder().setIntentExtras(bundle).setVideoState(i).setClirMode(getClirMode()).build());
    }

    public synchronized com.android.internal.telephony.Connection dial(String str, final ImsPhone.ImsDialArgs imsDialArgs) throws CallStateException {
        String convertNumberIfNecessary;
        final int i;
        int i2;
        boolean isPhoneInEcbMode = isPhoneInEcbMode();
        boolean z = imsDialArgs.isEmergency;
        boolean z2 = imsDialArgs.isWpsCall;
        if (!shouldNumberBePlacedOnIms(z, str)) {
            Rlog.i("ImsPhoneCallTracker", "dial: shouldNumberBePlacedOnIms = false");
            this.mOperationLocalLog.log("dial: shouldNumberBePlacedOnIms = false");
            throw new CallStateException(Phone.CS_FALLBACK);
        }
        int i3 = imsDialArgs.clirMode;
        int i4 = imsDialArgs.videoState;
        log("dial clirMode=" + i3);
        if (z) {
            log("dial emergency call, set clirModIe=2");
            i = 2;
            convertNumberIfNecessary = str;
        } else {
            convertNumberIfNecessary = convertNumberIfNecessary(this.mPhone, str);
            i = i3;
        }
        this.mClirMode = i;
        boolean prepareForDialing = prepareForDialing(imsDialArgs);
        if (isPhoneInEcbMode && z) {
            this.mPhone.handleTimerInEmergencyCallbackMode(1);
        }
        if (z && VideoProfile.isVideo(i4) && !this.mAllowEmergencyVideoCalls) {
            loge("dial: carrier does not support video emergency calls; downgrade to audio-only");
            i2 = 0;
        } else {
            i2 = i4;
        }
        this.mPendingCallVideoState = i2;
        try {
            synchronized (this.mSyncHold) {
                try {
                    this.mLastDialString = convertNumberIfNecessary;
                    this.mLastDialArgs = imsDialArgs;
                    final int i5 = i2;
                    this.mPendingMO = new ImsPhoneConnection(this.mPhone, convertNumberIfNecessary, this, this.mForegroundCall, z, z2, imsDialArgs);
                    this.mOperationLocalLog.log("dial requested. connId=" + System.identityHashCode(this.mPendingMO));
                    if (z && imsDialArgs.intentExtras != null) {
                        Rlog.i("ImsPhoneCallTracker", "dial ims emergency dialer: " + imsDialArgs.intentExtras.getBoolean("android.telecom.extra.IS_USER_INTENT_EMERGENCY_CALL"));
                        this.mPendingMO.setHasKnownUserIntentEmergency(imsDialArgs.intentExtras.getBoolean("android.telecom.extra.IS_USER_INTENT_EMERGENCY_CALL"));
                    }
                    this.mPendingMO.setVideoState(i5);
                    if (imsDialArgs.rttTextStream != null) {
                        log("dial: setting RTT stream on mPendingMO");
                        this.mPendingMO.setCurrentRttTextStream(imsDialArgs.rttTextStream);
                    }
                    addConnection(this.mPendingMO);
                    if (!prepareForDialing) {
                        if (isPhoneInEcbMode && (!isPhoneInEcbMode || !z)) {
                            if (DomainSelectionResolver.getInstance().isDomainSelectionSupported()) {
                                EmergencyStateTracker.getInstance().exitEmergencyCallbackMode(new Runnable() { // from class: com.android.internal.telephony.imsphone.ImsPhoneCallTracker.7
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        ImsPhoneCallTracker imsPhoneCallTracker = ImsPhoneCallTracker.this;
                                        ImsPhoneConnection imsPhoneConnection = imsPhoneCallTracker.mPendingMO;
                                        int i6 = i;
                                        int i7 = i5;
                                        ImsPhone.ImsDialArgs imsDialArgs2 = imsDialArgs;
                                        imsPhoneCallTracker.dialInternal(imsPhoneConnection, i6, i7, imsDialArgs2.retryCallFailCause, imsDialArgs2.retryCallFailNetworkType, imsDialArgs2.intentExtras);
                                    }
                                });
                            } else {
                                try {
                                    getEcbmInterface().exitEmergencyCallbackMode();
                                    this.mPhone.setOnEcbModeExitResponse(this, 14, null);
                                    this.pendingCallClirMode = i;
                                    this.mPendingCallVideoState = i5;
                                    this.mPendingIntentExtras = imsDialArgs.intentExtras;
                                    this.pendingCallInEcm = true;
                                } catch (ImsException e) {
                                    e.printStackTrace();
                                    throw new CallStateException("service not available");
                                }
                            }
                        }
                        dialInternal(this.mPendingMO, i, i5, imsDialArgs.retryCallFailCause, imsDialArgs.retryCallFailNetworkType, imsDialArgs.intentExtras);
                    }
                    if (this.mNumberConverted) {
                        this.mPendingMO.restoreDialedNumberAfterConversion(str);
                        this.mNumberConverted = false;
                    }
                    updatePhoneState();
                    this.mPhone.notifyPreciseCallStateChanged();
                } catch (Throwable th) {
                    th = th;
                    throw th;
                }
            }
        } catch (Throwable th2) {
            th = th2;
        }
        return this.mPendingMO;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isImsServiceReady() {
        ImsManager imsManager = this.mImsManager;
        if (imsManager == null) {
            return false;
        }
        return imsManager.isServiceReady();
    }

    private boolean shouldNumberBePlacedOnIms(boolean z, String str) {
        try {
            ImsManager imsManager = this.mImsManager;
            if (imsManager != null) {
                int shouldProcessCall = imsManager.shouldProcessCall(z, new String[]{str});
                Rlog.i("ImsPhoneCallTracker", "shouldProcessCall: number: " + Rlog.pii("ImsPhoneCallTracker", str) + ", result: " + shouldProcessCall);
                if (shouldProcessCall != 0) {
                    if (shouldProcessCall == 1) {
                        Rlog.i("ImsPhoneCallTracker", "shouldProcessCall: place over CSFB instead.");
                        return false;
                    }
                    Rlog.w("ImsPhoneCallTracker", "shouldProcessCall returned unknown result.");
                    return false;
                }
                return true;
            }
            Rlog.w("ImsPhoneCallTracker", "ImsManager unavailable, shouldProcessCall returning false.");
            return false;
        } catch (ImsException unused) {
            Rlog.w("ImsPhoneCallTracker", "ImsService unavailable, shouldProcessCall returning false.");
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCarrierConfiguration(int i, PersistableBundle persistableBundle) {
        IccCardConstants.State state;
        this.mCarrierConfigLoadedForSubscription = false;
        if (persistableBundle == null) {
            loge("updateCarrierConfiguration: carrier config is null, skipping.");
            return;
        }
        updateCarrierConfigCache(persistableBundle);
        log("updateCarrierConfiguration: Updating mAllowEmergencyVideoCalls = " + this.mAllowEmergencyVideoCalls);
        maybeConfigureRtpHeaderExtensions();
        if (this.mPhone.isSubscriptionManagerServiceEnabled()) {
            SubscriptionInfoInternal subscriptionInfoInternal = SubscriptionManagerService.getInstance().getSubscriptionInfoInternal(i);
            if (subscriptionInfoInternal == null || !subscriptionInfoInternal.isActive()) {
                loge("updateCarrierConfiguration: skipping notification to ImsService, nonactive subId = " + i);
                return;
            }
        } else if (!SubscriptionController.getInstance().isActiveSubId(i)) {
            loge("updateCarrierConfiguration: skipping notification to ImsService, nonactive subId = " + i);
            return;
        }
        Phone defaultPhone = getPhone().getDefaultPhone();
        if (defaultPhone != null && defaultPhone.getIccCard() != null && (state = defaultPhone.getIccCard().getState()) != null && (!state.iccCardExist() || state.isPinLocked())) {
            loge("updateCarrierConfiguration: card state is not ready, skipping notification to ImsService. State= " + state);
        } else if (!CarrierConfigManager.isConfigForIdentifiedCarrier(persistableBundle)) {
            logi("updateCarrierConfiguration: Empty or default carrier config, skipping notification to ImsService.");
        } else {
            logi("updateCarrierConfiguration: Updating ImsService configs.");
            this.mCarrierConfigLoadedForSubscription = true;
            updateImsServiceConfig();
            updateMediaThreshold(this.mThresholdRtpPacketLoss, this.mThresholdRtpJitter, this.mThresholdRtpInactivityTime);
        }
    }

    @VisibleForTesting
    public void updateCarrierConfigCache(PersistableBundle persistableBundle) {
        this.mAllowEmergencyVideoCalls = persistableBundle.getBoolean("allow_emergency_video_calls_bool");
        this.mTreatDowngradedVideoCallsAsVideoCalls = persistableBundle.getBoolean("treat_downgraded_video_calls_as_video_calls_bool");
        this.mDropVideoCallWhenAnsweringAudioCall = persistableBundle.getBoolean("drop_video_call_when_answering_audio_call_bool");
        this.mAllowAddCallDuringVideoCall = persistableBundle.getBoolean("allow_add_call_during_video_call");
        this.mAllowHoldingVideoCall = persistableBundle.getBoolean("allow_hold_video_call_bool");
        this.mNotifyVtHandoverToWifiFail = persistableBundle.getBoolean("notify_vt_handover_to_wifi_failure_bool");
        this.mSupportDowngradeVtToAudio = persistableBundle.getBoolean("support_downgrade_vt_to_audio_bool");
        this.mNotifyHandoverVideoFromWifiToLTE = persistableBundle.getBoolean("notify_handover_video_from_wifi_to_lte_bool");
        this.mNotifyHandoverVideoFromLTEToWifi = persistableBundle.getBoolean("notify_handover_video_from_lte_to_wifi_bool");
        this.mIgnoreDataEnabledChangedForVideoCalls = persistableBundle.getBoolean("ignore_data_enabled_changed_for_video_calls");
        this.mIsViLteDataMetered = persistableBundle.getBoolean("vilte_data_is_metered_bool");
        this.mSupportPauseVideo = persistableBundle.getBoolean("support_pause_ims_video_calls_bool");
        this.mAlwaysPlayRemoteHoldTone = persistableBundle.getBoolean("always_play_remote_hold_tone_bool");
        this.mAutoRetryFailedWifiEmergencyCall = persistableBundle.getBoolean("auto_retry_failed_wifi_emergency_call");
        this.mSupportCepOnPeer = persistableBundle.getBoolean("support_ims_conference_event_package_on_peer_bool");
        this.mSupportD2DUsingRtp = persistableBundle.getBoolean("supports_device_to_device_communication_using_rtp_bool");
        this.mSupportSdpForRtpHeaderExtensions = persistableBundle.getBoolean("supports_sdp_negotiation_of_d2d_rtp_header_extensions_bool");
        this.mThresholdRtpPacketLoss = persistableBundle.getInt("imsvoice.rtp_packet_loss_rate_threshold_int");
        this.mThresholdRtpInactivityTime = persistableBundle.getLong("imsvoice.rtp_inactivity_time_threshold_millis_long");
        this.mThresholdRtpJitter = persistableBundle.getInt("imsvoice.rtp_jitter_threshold_millis_int");
        if (this.mPhone.getContext().getResources().getBoolean(17891364)) {
            this.mUssdMethod = persistableBundle.getInt("carrier_ussd_method_int");
        }
        if (!this.mImsReasonCodeMap.isEmpty()) {
            this.mImsReasonCodeMap.clear();
        }
        String[] stringArray = persistableBundle.getStringArray("ims_reasoninfo_mapping_string_array");
        if (stringArray != null && stringArray.length > 0) {
            for (String str : stringArray) {
                String[] split = str.split(Pattern.quote("|"));
                if (split.length == 3) {
                    try {
                        String str2 = null;
                        Integer valueOf = split[0].equals("*") ? null : Integer.valueOf(Integer.parseInt(split[0]));
                        String str3 = split[1];
                        if (str3 == null) {
                            str2 = PhoneConfigurationManager.SSSS;
                        } else if (!str3.equals("*")) {
                            str2 = str3;
                        }
                        int parseInt = Integer.parseInt(split[2]);
                        addReasonCodeRemapping(valueOf, str2, Integer.valueOf(parseInt));
                        StringBuilder sb = new StringBuilder();
                        sb.append("Loaded ImsReasonInfo mapping : fromCode = ");
                        if (valueOf == null) {
                            valueOf = "any";
                        }
                        sb.append(valueOf);
                        sb.append(" ; message = ");
                        if (str2 == null) {
                            str2 = "any";
                        }
                        sb.append(str2);
                        sb.append(" ; toCode = ");
                        sb.append(parseInt);
                        log(sb.toString());
                    } catch (NumberFormatException unused) {
                        loge("Invalid ImsReasonInfo mapping found: " + str);
                    }
                }
            }
        } else {
            log("No carrier ImsReasonInfo mappings defined.");
        }
        this.mSrvccTypeSupported.clear();
        int[] intArray = persistableBundle.getIntArray("imsvoice.srvcc_type_int_array");
        if (intArray == null || intArray.length <= 0) {
            return;
        }
        this.mSrvccTypeSupported.addAll((Collection) Arrays.stream(intArray).boxed().collect(Collectors.toList()));
    }

    private void updateMediaThreshold(int i, int i2, long j) {
        if (!MediaThreshold.isValidRtpInactivityTimeMillis(j) && !MediaThreshold.isValidJitterMillis(i2) && !MediaThreshold.isValidRtpPacketLossRate(i)) {
            logi("There is no valid RTP threshold value");
            return;
        }
        MediaThreshold build = new MediaThreshold.Builder().setThresholdsRtpPacketLossRate(new int[]{i}).setThresholdsRtpInactivityTimeMillis(new long[]{j}).setThresholdsRtpJitterMillis(new int[]{i2}).build();
        MediaThreshold mediaThreshold = this.mMediaThreshold;
        if (mediaThreshold == null || !mediaThreshold.equals(build)) {
            logi("setMediaThreshold :" + build);
            try {
                this.mImsManager.setMediaThreshold(1, build);
                this.mMediaThreshold = build;
            } catch (ImsException e) {
                loge("setMediaThreshold Failed: " + e);
            }
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void handleEcmTimer(int i) {
        this.mPhone.handleTimerInEmergencyCallbackMode(i);
    }

    private void dialInternal(ImsPhoneConnection imsPhoneConnection, int i, int i2, Bundle bundle) {
        dialInternal(imsPhoneConnection, i, i2, 0, 0, bundle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dialInternal(ImsPhoneConnection imsPhoneConnection, int i, int i2, int i3, int i4, Bundle bundle) {
        if (imsPhoneConnection == null) {
            return;
        }
        if (!imsPhoneConnection.isAdhocConference() && (imsPhoneConnection.getAddress() == null || imsPhoneConnection.getAddress().length() == 0 || imsPhoneConnection.getAddress().indexOf(78) >= 0)) {
            imsPhoneConnection.setDisconnectCause(7);
            sendEmptyMessageDelayed(18, 500L);
            return;
        }
        setMute(false);
        boolean isEmergency = imsPhoneConnection.isEmergency();
        int i5 = isEmergency ? 2 : 1;
        int callTypeFromVideoState = ImsCallProfile.getCallTypeFromVideoState(i2);
        imsPhoneConnection.setVideoState(i2);
        try {
            String[] strArr = {imsPhoneConnection.getAddress()};
            ImsCallProfile createCallProfile = this.mImsManager.createCallProfile(i5, callTypeFromVideoState);
            if (imsPhoneConnection.isAdhocConference()) {
                createCallProfile.setCallExtraBoolean("android.telephony.ims.extra.CONFERENCE", true);
                createCallProfile.setCallExtraBoolean("conference", true);
            }
            createCallProfile.setCallExtraInt("oir", i);
            createCallProfile.setCallExtraInt("android.telephony.ims.extra.RETRY_CALL_FAIL_REASON", i3);
            createCallProfile.setCallExtraInt("android.telephony.ims.extra.RETRY_CALL_FAIL_NETWORKTYPE", i4);
            if (isEmergency) {
                setEmergencyCallInfo(createCallProfile, imsPhoneConnection);
            }
            if (bundle != null) {
                if (bundle.containsKey("android.telecom.extra.CALL_SUBJECT")) {
                    bundle.putString("DisplayText", cleanseInstantLetteringMessage(bundle.getString("android.telecom.extra.CALL_SUBJECT")));
                    createCallProfile.setCallExtra("android.telephony.ims.extra.CALL_SUBJECT", bundle.getString("android.telecom.extra.CALL_SUBJECT"));
                }
                if (bundle.containsKey("android.telecom.extra.PRIORITY")) {
                    createCallProfile.setCallExtraInt("android.telephony.ims.extra.PRIORITY", bundle.getInt("android.telecom.extra.PRIORITY"));
                }
                if (bundle.containsKey("android.telecom.extra.LOCATION")) {
                    createCallProfile.setCallExtraParcelable("android.telephony.ims.extra.LOCATION", bundle.getParcelable("android.telecom.extra.LOCATION"));
                }
                if (bundle.containsKey("android.telecom.extra.OUTGOING_PICTURE")) {
                    createCallProfile.setCallExtra("android.telephony.ims.extra.PICTURE_URL", TelephonyLocalConnection.getCallComposerServerUrlForHandle(this.mPhone.getSubId(), ((ParcelUuid) bundle.getParcelable("android.telecom.extra.OUTGOING_PICTURE")).getUuid()));
                }
                if (imsPhoneConnection.hasRttTextStream()) {
                    createCallProfile.mMediaProfile.mRttMode = 1;
                }
                if (bundle.containsKey("CallPull")) {
                    createCallProfile.mCallExtras.putBoolean("CallPull", bundle.getBoolean("CallPull"));
                    int i6 = bundle.getInt(ImsExternalCallTracker.EXTRA_IMS_EXTERNAL_CALL_ID);
                    imsPhoneConnection.setIsPulledCall(true);
                    imsPhoneConnection.setPulledDialogId(i6);
                }
                if (bundle.containsKey("CallRadioTech")) {
                    logi("dialInternal containing EXTRA_CALL_RAT_TYPE, " + bundle.getString("CallRadioTech"));
                }
                createCallProfile.mCallExtras.putBundle("android.telephony.ims.extra.OEM_EXTRAS", bundle);
            }
            this.mPhone.getVoiceCallSessionStats().onImsDial(imsPhoneConnection);
            ImsManager imsManager = this.mImsManager;
            if (imsPhoneConnection.isAdhocConference()) {
                strArr = imsPhoneConnection.getParticipantsToDial();
            }
            ImsCall makeCall = imsManager.makeCall(createCallProfile, strArr, this.mImsCallListener);
            imsPhoneConnection.setImsCall(makeCall);
            this.mMetrics.writeOnImsCallStart(this.mPhone.getPhoneId(), makeCall.getSession());
            setVideoCallProvider(imsPhoneConnection, makeCall);
            imsPhoneConnection.setAllowAddCallDuringVideoCall(this.mAllowAddCallDuringVideoCall);
            imsPhoneConnection.setAllowHoldingVideoCall(this.mAllowHoldingVideoCall);
            this.mImsCallInfoTracker.addImsCallStatus(imsPhoneConnection);
        } catch (RemoteException unused) {
        } catch (ImsException e) {
            loge("dialInternal : " + e);
            LocalLog localLog = this.mOperationLocalLog;
            localLog.log("dialInternal exception: " + e);
            imsPhoneConnection.setDisconnectCause(36);
            sendEmptyMessageDelayed(18, 500L);
        }
    }

    public void acceptCall(int i) throws CallStateException {
        log("acceptCall");
        this.mOperationLocalLog.log("accepted incoming call");
        if (this.mForegroundCall.getState().isAlive() && this.mBackgroundCall.getState().isAlive()) {
            throw new CallStateException("cannot accept call");
        }
        boolean z = false;
        if (this.mRingingCall.getState() == Call.State.WAITING && this.mForegroundCall.getState().isAlive()) {
            setMute(false);
            ImsCall imsCall = this.mForegroundCall.getImsCall();
            ImsCall imsCall2 = this.mRingingCall.getImsCall();
            if (this.mForegroundCall.hasConnections() && this.mRingingCall.hasConnections()) {
                z = shouldDisconnectActiveCallOnAnswer(imsCall, imsCall2);
            }
            this.mPendingCallVideoState = i;
            if (z) {
                this.mForegroundCall.hangup();
                this.mPhone.getVoiceCallSessionStats().onImsAcceptCall(this.mRingingCall.getConnections());
                try {
                    imsCall2.accept(ImsCallProfile.getCallTypeFromVideoState(i));
                    return;
                } catch (ImsException unused) {
                    throw new CallStateException("cannot accept call");
                }
            }
            holdActiveCallForWaitingCall();
        } else if (this.mRingingCall.getState().isRinging()) {
            log("acceptCall: incoming...");
            setMute(false);
            try {
                ImsCall imsCall3 = this.mRingingCall.getImsCall();
                if (imsCall3 != null) {
                    this.mPhone.getVoiceCallSessionStats().onImsAcceptCall(this.mRingingCall.getConnections());
                    imsCall3.accept(ImsCallProfile.getCallTypeFromVideoState(i));
                    this.mMetrics.writeOnImsCommand(this.mPhone.getPhoneId(), imsCall3.getSession(), 2);
                    return;
                }
                throw new CallStateException("no valid ims call");
            } catch (ImsException unused2) {
                throw new CallStateException("cannot accept call");
            }
        } else {
            throw new CallStateException("phone not ringing");
        }
    }

    public void rejectCall() throws CallStateException {
        log("rejectCall");
        this.mOperationLocalLog.log("rejected incoming call");
        if (this.mRingingCall.getState().isRinging()) {
            hangup(this.mRingingCall);
            return;
        }
        throw new CallStateException("phone not ringing");
    }

    private void setEmergencyCallInfo(ImsCallProfile imsCallProfile, com.android.internal.telephony.Connection connection) {
        EmergencyNumber emergencyNumberInfo = connection.getEmergencyNumberInfo();
        if (emergencyNumberInfo != null) {
            imsCallProfile.setEmergencyCallInfo(emergencyNumberInfo, connection.hasKnownUserIntentEmergency());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void switchAfterConferenceSuccess() {
        log("switchAfterConferenceSuccess fg =" + this.mForegroundCall.getState() + ", bg = " + this.mBackgroundCall.getState());
        if (this.mBackgroundCall.getState() == Call.State.HOLDING) {
            log("switchAfterConferenceSuccess");
            this.mForegroundCall.switchWith(this.mBackgroundCall);
        }
    }

    private void holdActiveCallForPendingMo() throws CallStateException {
        HoldSwapState holdSwapState = this.mHoldSwitchingState;
        if (holdSwapState == HoldSwapState.PENDING_SINGLE_CALL_HOLD || holdSwapState == HoldSwapState.SWAPPING_ACTIVE_AND_HELD) {
            logi("Ignoring hold request while already holding or swapping");
            return;
        }
        ImsCall imsCall = this.mForegroundCall.getImsCall();
        this.mHoldSwitchingState = HoldSwapState.HOLDING_TO_DIAL_OUTGOING;
        logHoldSwapState("holdActiveCallForPendingMo");
        this.mForegroundCall.switchWith(this.mBackgroundCall);
        try {
            imsCall.hold();
            this.mMetrics.writeOnImsCommand(this.mPhone.getPhoneId(), imsCall.getSession(), 5);
        } catch (ImsException e) {
            this.mForegroundCall.switchWith(this.mBackgroundCall);
            this.mHoldSwitchingState = holdSwapState;
            logHoldSwapState("holdActiveCallForPendingMo - fail");
            throw new CallStateException(e.getMessage());
        }
    }

    public void holdActiveCall() throws CallStateException {
        HoldSwapState holdSwapState;
        if (this.mForegroundCall.getState() == Call.State.ACTIVE) {
            HoldSwapState holdSwapState2 = this.mHoldSwitchingState;
            HoldSwapState holdSwapState3 = HoldSwapState.PENDING_SINGLE_CALL_HOLD;
            if (holdSwapState2 == holdSwapState3 || holdSwapState2 == (holdSwapState = HoldSwapState.SWAPPING_ACTIVE_AND_HELD)) {
                logi("Ignoring hold request while already holding or swapping");
                return;
            }
            ImsCall imsCall = this.mForegroundCall.getImsCall();
            if (this.mBackgroundCall.getState().isAlive()) {
                this.mCallExpectedToResume = this.mBackgroundCall.getImsCall();
                this.mHoldSwitchingState = holdSwapState;
            } else {
                this.mHoldSwitchingState = holdSwapState3;
            }
            logHoldSwapState("holdActiveCall");
            this.mForegroundCall.switchWith(this.mBackgroundCall);
            try {
                imsCall.hold();
                this.mMetrics.writeOnImsCommand(this.mPhone.getPhoneId(), imsCall.getSession(), 5);
            } catch (ImsException | NullPointerException e) {
                this.mForegroundCall.switchWith(this.mBackgroundCall);
                this.mHoldSwitchingState = holdSwapState2;
                logHoldSwapState("holdActiveCall - fail");
                throw new CallStateException(e.getMessage());
            }
        }
    }

    public void holdActiveCallForWaitingCall() throws CallStateException {
        if (!this.mBackgroundCall.getState().isAlive() && this.mRingingCall.getState() == Call.State.WAITING) {
            ImsCall imsCall = this.mForegroundCall.getImsCall();
            HoldSwapState holdSwapState = this.mHoldSwitchingState;
            this.mHoldSwitchingState = HoldSwapState.HOLDING_TO_ANSWER_INCOMING;
            ImsCall imsCall2 = this.mCallExpectedToResume;
            this.mCallExpectedToResume = this.mRingingCall.getImsCall();
            this.mForegroundCall.switchWith(this.mBackgroundCall);
            logHoldSwapState("holdActiveCallForWaitingCall");
            try {
                imsCall.hold();
                this.mMetrics.writeOnImsCommand(this.mPhone.getPhoneId(), imsCall.getSession(), 5);
            } catch (ImsException e) {
                this.mForegroundCall.switchWith(this.mBackgroundCall);
                this.mHoldSwitchingState = holdSwapState;
                this.mCallExpectedToResume = imsCall2;
                logHoldSwapState("holdActiveCallForWaitingCall - fail");
                throw new CallStateException(e.getMessage());
            }
        }
    }

    public void unholdHeldCall() throws CallStateException {
        ImsCall imsCall = this.mBackgroundCall.getImsCall();
        HoldSwapState holdSwapState = this.mHoldSwitchingState;
        HoldSwapState holdSwapState2 = HoldSwapState.PENDING_SINGLE_CALL_UNHOLD;
        if (holdSwapState == holdSwapState2 || holdSwapState == HoldSwapState.SWAPPING_ACTIVE_AND_HELD) {
            logi("Ignoring unhold request while already unholding or swapping");
        } else if (imsCall != null) {
            this.mCallExpectedToResume = imsCall;
            this.mHoldSwitchingState = holdSwapState2;
            this.mForegroundCall.switchWith(this.mBackgroundCall);
            logHoldSwapState("unholdCurrentCall");
            try {
                imsCall.resume();
                this.mMetrics.writeOnImsCommand(this.mPhone.getPhoneId(), imsCall.getSession(), 6);
            } catch (ImsException e) {
                this.mForegroundCall.switchWith(this.mBackgroundCall);
                this.mHoldSwitchingState = holdSwapState;
                logHoldSwapState("unholdCurrentCall - fail");
                throw new CallStateException(e.getMessage());
            }
        }
    }

    private void resumeForegroundCall() throws ImsException {
        ImsCall imsCall = this.mForegroundCall.getImsCall();
        if (imsCall != null) {
            if (!imsCall.isPendingHold()) {
                imsCall.resume();
                this.mMetrics.writeOnImsCommand(this.mPhone.getPhoneId(), imsCall.getSession(), 6);
                return;
            }
            this.mHoldSwitchingState = HoldSwapState.PENDING_RESUME_FOREGROUND_AFTER_HOLD;
            logHoldSwapState("resumeForegroundCall - unhold pending; resume request again");
        }
    }

    private void answerWaitingCall() throws ImsException {
        ImsCall imsCall = this.mRingingCall.getImsCall();
        if (imsCall != null) {
            this.mPhone.getVoiceCallSessionStats().onImsAcceptCall(this.mRingingCall.getConnections());
            imsCall.accept(ImsCallProfile.getCallTypeFromVideoState(this.mPendingCallVideoState));
            this.mMetrics.writeOnImsCommand(this.mPhone.getPhoneId(), imsCall.getSession(), 2);
        }
    }

    private void maintainConnectTimeCache() {
        final long elapsedRealtime = SystemClock.elapsedRealtime() - 60000;
        this.mPhoneNumAndConnTime.entrySet().removeIf(new Predicate() { // from class: com.android.internal.telephony.imsphone.ImsPhoneCallTracker$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$maintainConnectTimeCache$2;
                lambda$maintainConnectTimeCache$2 = ImsPhoneCallTracker.lambda$maintainConnectTimeCache$2(elapsedRealtime, (Map.Entry) obj);
                return lambda$maintainConnectTimeCache$2;
            }
        });
        while (!this.mUnknownPeerConnTime.isEmpty() && this.mUnknownPeerConnTime.peek().mCachedTime < elapsedRealtime) {
            this.mUnknownPeerConnTime.poll();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$maintainConnectTimeCache$2(long j, Map.Entry entry) {
        return ((CacheEntry) entry.getValue()).mCachedTime < j;
    }

    private void cacheConnectionTimeWithPhoneNumber(ImsPhoneConnection imsPhoneConnection) {
        CacheEntry cacheEntry = new CacheEntry(SystemClock.elapsedRealtime(), imsPhoneConnection.getConnectTime(), imsPhoneConnection.getConnectTimeReal(), !imsPhoneConnection.isIncoming());
        maintainConnectTimeCache();
        if (1 == imsPhoneConnection.getNumberPresentation()) {
            String formattedPhoneNumber = getFormattedPhoneNumber(imsPhoneConnection.getAddress());
            if (!this.mPhoneNumAndConnTime.containsKey(formattedPhoneNumber) || imsPhoneConnection.getConnectTime() > this.mPhoneNumAndConnTime.get(formattedPhoneNumber).mConnectTime) {
                this.mPhoneNumAndConnTime.put(formattedPhoneNumber, cacheEntry);
                return;
            }
            return;
        }
        this.mUnknownPeerConnTime.add(cacheEntry);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public CacheEntry findConnectionTimeUsePhoneNumber(ConferenceParticipant conferenceParticipant) {
        maintainConnectTimeCache();
        if (1 == conferenceParticipant.getParticipantPresentation()) {
            if (conferenceParticipant.getHandle() == null || conferenceParticipant.getHandle().getSchemeSpecificPart() == null) {
                return null;
            }
            String schemeSpecificPart = ConferenceParticipant.getParticipantAddress(conferenceParticipant.getHandle(), getCountryIso()).getSchemeSpecificPart();
            if (TextUtils.isEmpty(schemeSpecificPart)) {
                return null;
            }
            return this.mPhoneNumAndConnTime.get(getFormattedPhoneNumber(schemeSpecificPart));
        }
        return this.mUnknownPeerConnTime.poll();
    }

    private String getFormattedPhoneNumber(String str) {
        String formatNumberToE164;
        String countryIso = getCountryIso();
        return (countryIso == null || (formatNumberToE164 = PhoneNumberUtils.formatNumberToE164(str, countryIso)) == null) ? str : formatNumberToE164;
    }

    private String getCountryIso() {
        SubscriptionInfo activeSubscriptionInfo = SubscriptionManager.from(this.mPhone.getContext()).getActiveSubscriptionInfo(this.mPhone.getSubId());
        if (activeSubscriptionInfo == null) {
            return null;
        }
        return activeSubscriptionInfo.getCountryIso();
    }

    public void conference() {
        String str;
        ImsCall imsCall = this.mForegroundCall.getImsCall();
        if (imsCall == null) {
            log("conference no foreground ims call");
            return;
        }
        ImsCall imsCall2 = this.mBackgroundCall.getImsCall();
        if (imsCall2 == null) {
            log("conference no background ims call");
        } else if (imsCall.isCallSessionMergePending()) {
            log("conference: skip; foreground call already in process of merging.");
        } else if (imsCall2.isCallSessionMergePending()) {
            log("conference: skip; background call already in process of merging.");
        } else {
            long earliestConnectTime = this.mForegroundCall.getEarliestConnectTime();
            long earliestConnectTime2 = this.mBackgroundCall.getEarliestConnectTime();
            int i = (earliestConnectTime > 0L ? 1 : (earliestConnectTime == 0L ? 0 : -1));
            if (i > 0 && earliestConnectTime2 > 0) {
                earliestConnectTime = Math.min(this.mForegroundCall.getEarliestConnectTime(), this.mBackgroundCall.getEarliestConnectTime());
                log("conference - using connect time = " + earliestConnectTime);
            } else if (i > 0) {
                log("conference - bg call connect time is 0; using fg = " + earliestConnectTime);
            } else {
                log("conference - fg call connect time is 0; using bg = " + earliestConnectTime2);
                earliestConnectTime = earliestConnectTime2;
            }
            ImsPhoneConnection firstConnection = this.mForegroundCall.getFirstConnection();
            String str2 = PhoneConfigurationManager.SSSS;
            if (firstConnection != null) {
                firstConnection.setConferenceConnectTime(earliestConnectTime);
                firstConnection.handleMergeStart();
                str = firstConnection.getTelecomCallId();
                cacheConnectionTimeWithPhoneNumber(firstConnection);
            } else {
                str = PhoneConfigurationManager.SSSS;
            }
            ImsPhoneConnection findConnection = findConnection(imsCall2);
            if (findConnection != null) {
                findConnection.handleMergeStart();
                str2 = findConnection.getTelecomCallId();
                cacheConnectionTimeWithPhoneNumber(findConnection);
            }
            log("conference: fgCallId=" + str + ", bgCallId=" + str2);
            LocalLog localLog = this.mOperationLocalLog;
            localLog.log("conference: fgCallId=" + str + ", bgCallId=" + str2);
            try {
                imsCall.merge(imsCall2);
            } catch (ImsException e) {
                log("conference " + e.getMessage());
                handleConferenceFailed(firstConnection, findConnection);
            }
        }
    }

    public void explicitCallTransfer() throws CallStateException {
        ImsCall imsCall = this.mForegroundCall.getImsCall();
        ImsCall imsCall2 = this.mBackgroundCall.getImsCall();
        if (imsCall == null || imsCall2 == null || !canTransfer()) {
            throw new CallStateException("cannot transfer");
        }
        try {
            imsCall2.consultativeTransfer(imsCall);
        } catch (ImsException e) {
            throw new CallStateException(e.getMessage());
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void clearDisconnected() {
        log("clearDisconnected");
        internalClearDisconnected();
        updatePhoneState();
        this.mPhone.notifyPreciseCallStateChanged();
    }

    public boolean canConference() {
        return this.mForegroundCall.getState() == Call.State.ACTIVE && this.mBackgroundCall.getState() == Call.State.HOLDING && !this.mBackgroundCall.isFull() && !this.mForegroundCall.isFull();
    }

    private boolean canAddVideoCallDuringImsAudioCall(int i) {
        if (this.mAllowHoldingVideoCall) {
            return true;
        }
        ImsCall imsCall = this.mForegroundCall.getImsCall();
        if (imsCall == null) {
            imsCall = this.mBackgroundCall.getImsCall();
        }
        return (((this.mForegroundCall.getState() == Call.State.ACTIVE || this.mBackgroundCall.getState() == Call.State.HOLDING) && imsCall != null && !imsCall.isVideoCall()) && VideoProfile.isVideo(i)) ? false : true;
    }

    public void checkForDialIssues() throws CallStateException {
        if (TelephonyProperties.disable_call().orElse(Boolean.FALSE).booleanValue()) {
            throw new CallStateException(5, "ro.telephony.disable-call has been used to disable calling.");
        }
        if (this.mPendingMO != null) {
            throw new CallStateException(3, "Another outgoing call is already being dialed.");
        }
        if (this.mRingingCall.isRinging()) {
            throw new CallStateException(4, "Can't place a call while another is ringing.");
        }
        if (this.mBackgroundCall.getState().isAlive() && this.mForegroundCall.getState().isAlive()) {
            throw new CallStateException(6, "Already an active foreground and background call.");
        }
    }

    public boolean canTransfer() {
        return this.mForegroundCall.getState() == Call.State.ACTIVE && this.mBackgroundCall.getState() == Call.State.HOLDING;
    }

    private void internalClearDisconnected() {
        this.mRingingCall.clearDisconnected();
        this.mForegroundCall.clearDisconnected();
        this.mBackgroundCall.clearDisconnected();
        this.mHandoverCall.clearDisconnected();
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void updatePhoneState() {
        String str;
        PhoneConstants.State state = this.mState;
        ImsPhoneConnection imsPhoneConnection = this.mPendingMO;
        boolean z = imsPhoneConnection == null || !imsPhoneConnection.getState().isAlive();
        if (this.mRingingCall.isRinging()) {
            this.mState = PhoneConstants.State.RINGING;
        } else if (!z || !this.mForegroundCall.isIdle() || !this.mBackgroundCall.isIdle()) {
            this.mState = PhoneConstants.State.OFFHOOK;
        } else {
            this.mState = PhoneConstants.State.IDLE;
        }
        PhoneConstants.State state2 = this.mState;
        PhoneConstants.State state3 = PhoneConstants.State.IDLE;
        if (state2 == state3 && state != state2) {
            this.mVoiceCallEndedRegistrants.notifyRegistrants(new AsyncResult((Object) null, (Object) null, (Throwable) null));
        } else if (state == state3 && state != state2) {
            this.mVoiceCallStartedRegistrants.notifyRegistrants(new AsyncResult((Object) null, (Object) null, (Throwable) null));
        }
        StringBuilder sb = new StringBuilder();
        sb.append("updatePhoneState pendingMo = ");
        if (this.mPendingMO == null) {
            str = "null";
        } else {
            str = this.mPendingMO.getState() + "(" + this.mPendingMO.getTelecomCallId() + "/objId:" + System.identityHashCode(this.mPendingMO) + ")";
        }
        sb.append(str);
        sb.append(", rng= ");
        sb.append(this.mRingingCall.getState());
        sb.append("(");
        sb.append(this.mRingingCall.getConnectionSummary());
        sb.append("), fg= ");
        sb.append(this.mForegroundCall.getState());
        sb.append("(");
        sb.append(this.mForegroundCall.getConnectionSummary());
        sb.append("), bg= ");
        sb.append(this.mBackgroundCall.getState());
        sb.append("(");
        sb.append(this.mBackgroundCall.getConnectionSummary());
        sb.append(")");
        log(sb.toString());
        log("updatePhoneState oldState=" + state + ", newState=" + this.mState);
        if (this.mState != state) {
            this.mPhone.notifyPhoneStateChanged();
            this.mMetrics.writePhoneState(this.mPhone.getPhoneId(), this.mState);
            notifyPhoneStateChanged(state, this.mState);
        }
    }

    public void setTtyMode(int i) {
        ImsManager imsManager = this.mImsManager;
        if (imsManager == null) {
            Log.w("ImsPhoneCallTracker", "ImsManager is null when setting TTY mode");
            return;
        }
        try {
            imsManager.setTtyMode(i);
        } catch (ImsException e) {
            loge("setTtyMode : " + e);
        }
    }

    public void setUiTTYMode(int i, Message message) {
        ImsManager imsManager = this.mImsManager;
        if (imsManager == null) {
            this.mPhone.sendErrorResponse(message, getImsManagerIsNullException());
            return;
        }
        try {
            imsManager.setUiTTYMode(this.mPhone.getContext(), i, message);
        } catch (ImsException e) {
            loge("setUITTYMode : " + e);
            this.mPhone.sendErrorResponse(message, e);
        }
    }

    public void setMute(boolean z) {
        this.mDesiredMute = z;
        this.mForegroundCall.setMute(z);
    }

    public boolean getMute() {
        return this.mDesiredMute;
    }

    public void sendDtmf(char c, Message message) {
        log("sendDtmf");
        ImsCall imsCall = this.mForegroundCall.getImsCall();
        if (imsCall != null) {
            imsCall.sendDtmf(c, message);
        }
    }

    public void startDtmf(char c) {
        log("startDtmf");
        ImsCall imsCall = this.mForegroundCall.getImsCall();
        if (imsCall != null) {
            imsCall.startDtmf(c);
        } else {
            loge("startDtmf : no foreground call");
        }
    }

    public void stopDtmf() {
        log("stopDtmf");
        ImsCall imsCall = this.mForegroundCall.getImsCall();
        if (imsCall != null) {
            imsCall.stopDtmf();
        } else {
            loge("stopDtmf : no foreground call");
        }
    }

    public void hangup(ImsPhoneConnection imsPhoneConnection) throws CallStateException {
        log("hangup connection");
        if (imsPhoneConnection.getOwner() != this) {
            throw new CallStateException("ImsPhoneConnection " + imsPhoneConnection + "does not belong to ImsPhoneCallTracker " + this);
        }
        hangup(imsPhoneConnection.getCall());
    }

    public void hangup(ImsPhoneCall imsPhoneCall) throws CallStateException {
        hangup(imsPhoneCall, 1);
    }

    public void hangup(ImsPhoneCall imsPhoneCall, int i) throws CallStateException {
        boolean z;
        String str;
        log("hangup call - reason=" + i);
        if (imsPhoneCall.getConnectionsCount() == 0) {
            throw new CallStateException("no connections");
        }
        ImsCall imsCall = imsPhoneCall.getImsCall();
        ImsPhoneConnection findConnection = findConnection(imsCall);
        if (imsPhoneCall == this.mRingingCall) {
            str = "(ringing) hangup incoming";
            z = true;
        } else {
            z = false;
            if (imsPhoneCall == this.mForegroundCall) {
                str = imsPhoneCall.isDialingOrAlerting() ? "(foregnd) hangup dialing or alerting..." : "(foregnd) hangup foreground";
            } else if (imsPhoneCall == this.mBackgroundCall) {
                str = "(backgnd) hangup waiting or background";
            } else if (imsPhoneCall != this.mHandoverCall) {
                LocalLog localLog = this.mOperationLocalLog;
                localLog.log("hangup: ImsPhoneCall " + System.identityHashCode(findConnection) + " does not belong to ImsPhoneCallTracker " + this);
                throw new CallStateException("ImsPhoneCall " + imsPhoneCall + "does not belong to ImsPhoneCallTracker " + this);
            } else {
                str = "(handover) hangup handover (SRVCC) call";
            }
        }
        log(str);
        LocalLog localLog2 = this.mOperationLocalLog;
        localLog2.log("hangup: " + str + ", connId=" + System.identityHashCode(findConnection));
        imsPhoneCall.onHangupLocal();
        this.mImsCallInfoTracker.updateImsCallStatus(findConnection);
        try {
            if (imsCall == null) {
                ImsPhoneConnection imsPhoneConnection = this.mPendingMO;
                if (imsPhoneConnection != null && imsPhoneCall == this.mForegroundCall) {
                    imsPhoneConnection.update(null, Call.State.DISCONNECTED);
                    this.mPendingMO.onDisconnect();
                    removeConnection(this.mPendingMO);
                    this.mPendingMO = null;
                    updatePhoneState();
                    removeMessages(20);
                }
            } else if (z) {
                if (i == 2) {
                    imsCall.reject(365);
                } else {
                    imsCall.reject(504);
                }
                this.mMetrics.writeOnImsCommand(this.mPhone.getPhoneId(), imsCall.getSession(), 3);
            } else {
                imsCall.terminate(501);
                this.mMetrics.writeOnImsCommand(this.mPhone.getPhoneId(), imsCall.getSession(), 4);
            }
            this.mPhone.notifyPreciseCallStateChanged();
        } catch (ImsException e) {
            LocalLog localLog3 = this.mOperationLocalLog;
            localLog3.log("hangup: ImsException=" + e);
            throw new CallStateException(e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void callEndCleanupHandOverCallIfAny() {
        if (this.mHandoverCall.getConnections().size() > 0) {
            log("callEndCleanupHandOverCallIfAny, mHandoverCall.mConnections=" + this.mHandoverCall.getConnections());
            this.mHandoverCall.clearConnections();
            this.mConnections.clear();
            this.mState = PhoneConstants.State.IDLE;
        }
    }

    public void sendUSSD(String str, Message message) {
        log("sendUSSD");
        try {
            ImsCall imsCall = this.mUssdSession;
            if (imsCall != null) {
                this.mPendingUssd = null;
                imsCall.sendUssd(str);
                AsyncResult.forMessage(message, (Object) null, (Throwable) null);
                message.sendToTarget();
                return;
            }
            ImsManager imsManager = this.mImsManager;
            if (imsManager == null) {
                this.mPhone.sendErrorResponse(message, getImsManagerIsNullException());
                return;
            }
            ImsCallProfile createCallProfile = imsManager.createCallProfile(1, 2);
            createCallProfile.setCallExtraInt("dialstring", 2);
            this.mUssdSession = this.mImsManager.makeCall(createCallProfile, new String[]{str}, this.mImsUssdListener);
            this.mPendingUssd = message;
            log("pending ussd updated, " + this.mPendingUssd);
        } catch (ImsException e) {
            loge("sendUSSD : " + e);
            this.mPhone.sendErrorResponse(message, e);
        }
    }

    public void cancelUSSD(Message message) {
        ImsCall imsCall = this.mUssdSession;
        if (imsCall == null) {
            return;
        }
        this.mPendingUssd = message;
        imsCall.terminate(501);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public synchronized ImsPhoneConnection findConnection(ImsCall imsCall) {
        Iterator<ImsPhoneConnection> it = this.mConnections.iterator();
        while (it.hasNext()) {
            ImsPhoneConnection next = it.next();
            if (next.getImsCall() == imsCall) {
                return next;
            }
        }
        return null;
    }

    public synchronized void cleanupAndRemoveConnection(ImsPhoneConnection imsPhoneConnection) {
        LocalLog localLog = this.mOperationLocalLog;
        localLog.log("cleanupAndRemoveConnection: " + imsPhoneConnection);
        if (imsPhoneConnection.getCall() != null) {
            imsPhoneConnection.getCall().detach(imsPhoneConnection);
        }
        removeConnection(imsPhoneConnection);
        ImsPhoneConnection imsPhoneConnection2 = this.mPendingMO;
        if (imsPhoneConnection == imsPhoneConnection2) {
            imsPhoneConnection2.finalize();
            this.mPendingMO = null;
        }
        updatePhoneState();
        this.mPhone.notifyPreciseCallStateChanged();
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public synchronized void removeConnection(ImsPhoneConnection imsPhoneConnection) {
        boolean z;
        this.mConnections.remove(imsPhoneConnection);
        if (this.mIsInEmergencyCall) {
            Iterator<ImsPhoneConnection> it = this.mConnections.iterator();
            while (true) {
                if (!it.hasNext()) {
                    z = false;
                    break;
                }
                ImsPhoneConnection next = it.next();
                if (next != null) {
                    z = true;
                    if (next.isEmergency()) {
                        break;
                    }
                }
            }
            if (!z) {
                if (this.mPhone.isEcmCanceledForEmergency()) {
                    this.mPhone.handleTimerInEmergencyCallbackMode(0);
                }
                this.mIsInEmergencyCall = false;
                this.mPhone.sendEmergencyCallStateChange(false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public synchronized void addConnection(ImsPhoneConnection imsPhoneConnection) {
        this.mConnections.add(imsPhoneConnection);
        if (imsPhoneConnection.isEmergency()) {
            this.mIsInEmergencyCall = true;
            this.mPhone.sendEmergencyCallStateChange(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void processCallStateChange(ImsCall imsCall, Call.State state, int i) {
        log("processCallStateChange " + imsCall + " state=" + state + " cause=" + i);
        processCallStateChange(imsCall, state, i, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void processCallStateChange(ImsCall imsCall, Call.State state, int i, boolean z) {
        ImsPhoneConnection findConnection;
        log("processCallStateChange state=" + state + " cause=" + i + " ignoreState=" + z);
        if (imsCall == null || (findConnection = findConnection(imsCall)) == null) {
            return;
        }
        findConnection.updateMediaCapabilities(imsCall);
        if (z) {
            findConnection.updateAddressDisplay(imsCall);
            findConnection.updateExtras(imsCall);
            findConnection.maybeChangeRingbackState();
            maybeSetVideoCallProvider(findConnection, imsCall);
            this.mPhone.notifyPreciseCallStateToNotifier();
            return;
        }
        this.mOperationLocalLog.log("processCallStateChange: state=" + state + " cause=" + i + " connId=" + System.identityHashCode(findConnection));
        Call.State state2 = this.mForegroundCall.getState();
        Call.State state3 = Call.State.ACTIVE;
        boolean z2 = true;
        boolean z3 = (state2 == state3 || this.mBackgroundCall.getState() == state3) ? false : true;
        boolean update = findConnection.update(imsCall, state);
        if (z3 && update && state == state3) {
            sendMessage(obtainMessage(35));
        }
        if (state == Call.State.DISCONNECTED) {
            if (!findConnection.onDisconnect(i) && !update) {
                z2 = false;
            }
            findConnection.getCall().detach(findConnection);
            removeConnection(findConnection);
            ImsPhoneConnection imsPhoneConnection = this.mPendingMO;
            if (imsPhoneConnection == findConnection) {
                imsPhoneConnection.finalize();
                this.mPendingMO = null;
            }
            List<ConferenceParticipant> conferenceParticipants = imsCall.getConferenceParticipants();
            if (conferenceParticipants != null) {
                for (ConferenceParticipant conferenceParticipant : conferenceParticipants) {
                    String schemeSpecificPart = ConferenceParticipant.getParticipantAddress(conferenceParticipant.getHandle(), getCountryIso()).getSchemeSpecificPart();
                    if (!TextUtils.isEmpty(schemeSpecificPart)) {
                        this.mPhoneNumAndConnTime.remove(getFormattedPhoneNumber(schemeSpecificPart));
                    }
                }
            }
            update = z2;
        } else {
            this.mPhone.getVoiceCallSessionStats().onCallStateChanged(findConnection.getCall());
        }
        if (update) {
            this.mImsCallInfoTracker.updateImsCallStatus(findConnection);
            if (findConnection.getCall() == this.mHandoverCall) {
                return;
            }
            updatePhoneState();
            this.mPhone.notifyPreciseCallStateChanged();
        }
    }

    private void maybeSetVideoCallProvider(ImsPhoneConnection imsPhoneConnection, ImsCall imsCall) {
        Connection.VideoProvider videoProvider = imsPhoneConnection.getVideoProvider();
        ImsCallSession callSession = imsCall.getCallSession();
        if (videoProvider != null || callSession == null || callSession.getVideoCallProvider() == null) {
            return;
        }
        try {
            setVideoCallProvider(imsPhoneConnection, imsCall);
        } catch (RemoteException e) {
            loge("maybeSetVideoCallProvider: exception " + e);
        }
    }

    @VisibleForTesting
    public void addReasonCodeRemapping(Integer num, String str, Integer num2) {
        if (str != null) {
            str = str.toLowerCase(Locale.ROOT);
        }
        this.mImsReasonCodeMap.put(new ImsReasonInfoKeyPair(num, str), num2);
    }

    @VisibleForTesting
    public int maybeRemapReasonCode(ImsReasonInfo imsReasonInfo) {
        int code = imsReasonInfo.getCode();
        String extraMessage = imsReasonInfo.getExtraMessage();
        String lowerCase = extraMessage == null ? PhoneConfigurationManager.SSSS : extraMessage.toLowerCase(Locale.ROOT);
        log("maybeRemapReasonCode : fromCode = " + imsReasonInfo.getCode() + " ; message = " + lowerCase);
        ImsReasonInfoKeyPair imsReasonInfoKeyPair = new ImsReasonInfoKeyPair(Integer.valueOf(code), lowerCase);
        ImsReasonInfoKeyPair imsReasonInfoKeyPair2 = new ImsReasonInfoKeyPair(null, lowerCase);
        ImsReasonInfoKeyPair imsReasonInfoKeyPair3 = new ImsReasonInfoKeyPair(Integer.valueOf(code), null);
        if (this.mImsReasonCodeMap.containsKey(imsReasonInfoKeyPair)) {
            int intValue = this.mImsReasonCodeMap.get(imsReasonInfoKeyPair).intValue();
            log("maybeRemapReasonCode : fromCode = " + imsReasonInfo.getCode() + " ; message = " + lowerCase + " ; toCode = " + intValue);
            return intValue;
        } else if (!lowerCase.isEmpty() && this.mImsReasonCodeMap.containsKey(imsReasonInfoKeyPair2)) {
            int intValue2 = this.mImsReasonCodeMap.get(imsReasonInfoKeyPair2).intValue();
            log("maybeRemapReasonCode : fromCode(wildcard) = " + imsReasonInfo.getCode() + " ; message = " + lowerCase + " ; toCode = " + intValue2);
            return intValue2;
        } else if (this.mImsReasonCodeMap.containsKey(imsReasonInfoKeyPair3)) {
            int intValue3 = this.mImsReasonCodeMap.get(imsReasonInfoKeyPair3).intValue();
            log("maybeRemapReasonCode : fromCode = " + imsReasonInfo.getCode() + " ; message(wildcard) = " + lowerCase + " ; toCode = " + intValue3);
            return intValue3;
        } else {
            return code;
        }
    }

    @VisibleForTesting
    public int getDisconnectCauseFromReasonInfo(ImsReasonInfo imsReasonInfo, Call.State state) {
        switch (maybeRemapReasonCode(imsReasonInfo)) {
            case 0:
                if (this.mPhone.getDefaultPhone().getServiceStateTracker().mRestrictedState.isCsRestricted()) {
                    return 22;
                }
                if (this.mPhone.getDefaultPhone().getServiceStateTracker().mRestrictedState.isCsEmergencyRestricted()) {
                    return 24;
                }
                return this.mPhone.getDefaultPhone().getServiceStateTracker().mRestrictedState.isCsNormalRestricted() ? 23 : 36;
            case 106:
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMM_ACCESS_BARRED_INFINITE_RETRY /* 121 */:
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_AUTH_FAILURE_ON_EMERGENCY_CALL /* 122 */:
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INVALID_DNS_ADDR /* 123 */:
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INVALID_PCSCF_OR_DNS_ADDRESS /* 124 */:
            case 131:
            case UiccCardApplication.AUTH_CONTEXT_GBA_BOOTSTRAP /* 132 */:
            case 144:
                return 18;
            case 108:
                return 45;
            case 111:
            case 1500:
                return 17;
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_APN_TYPE_CONFLICT /* 112 */:
            case 505:
                return state == Call.State.DIALING ? 62 : 61;
            case 143:
            case 1017:
            case 1404:
                return 16;
            case IccRecords.EVENT_SET_SMSS_RECORD_DONE /* 201 */:
            case 202:
            case 203:
            case 335:
                return 13;
            case 240:
                return 20;
            case CallFailCause.FDN_BLOCKED /* 241 */:
                return 21;
            case CallFailCause.IMEI_NOT_ACCEPTED /* 243 */:
                return 58;
            case CallFailCause.DIAL_MODIFIED_TO_USSD /* 244 */:
                return 46;
            case CallFailCause.DIAL_MODIFIED_TO_SS /* 245 */:
                return 47;
            case CallFailCause.DIAL_MODIFIED_TO_DIAL /* 246 */:
                return 48;
            case CallFailCause.RADIO_OFF /* 247 */:
                return 66;
            case 248:
                return 69;
            case CallFailCause.NO_VALID_SIM /* 249 */:
                return 70;
            case CallFailCause.RADIO_INTERNAL_ERROR /* 250 */:
                return 67;
            case CallFailCause.NETWORK_RESP_TIMEOUT /* 251 */:
                return 68;
            case 321:
            case 340:
            case 362:
                return 12;
            case 331:
            case 1602:
            case 1606:
            case 1607:
            case 1608:
            case 1611:
            case 1614:
            case 1616:
            case 1618:
                return 81;
            case 332:
                return 12;
            case 333:
                return 7;
            case 337:
            case 341:
                return 8;
            case 338:
                return 4;
            case 352:
            case 354:
                return 9;
            case 361:
            case 510:
                return 2;
            case 363:
                return 63;
            case 364:
                return 64;
            case 402:
                return 77;
            case 501:
                return 3;
            case 1014:
                return 52;
            case 1016:
                return 51;
            case CallFailCause.MEDIA_UNSPECIFIED /* 1403 */:
                return 53;
            case 1405:
                return 55;
            case 1406:
                return 54;
            case 1407:
                return 59;
            case 1512:
                return 60;
            case 1514:
                return 71;
            case 1515:
                return 25;
            case 1622:
                return 78;
            case 1623:
                return 79;
            default:
                return 36;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getPreciseDisconnectCauseFromReasonInfo(ImsReasonInfo imsReasonInfo) {
        return PRECISE_CAUSE_MAP.get(maybeRemapReasonCode(imsReasonInfo), 65535);
    }

    private boolean isPhoneInEcbMode() {
        ImsPhone imsPhone = this.mPhone;
        return imsPhone != null && imsPhone.isInEcm();
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void dialPendingMO() {
        boolean isPhoneInEcbMode = isPhoneInEcbMode();
        boolean isEmergency = this.mPendingMO.isEmergency();
        if (!isPhoneInEcbMode || (isPhoneInEcbMode && isEmergency)) {
            sendEmptyMessage(20);
        } else {
            sendEmptyMessage(21);
        }
    }

    public void sendCallStartFailedDisconnect(ImsCall imsCall, ImsReasonInfo imsReasonInfo) {
        Call.State state;
        this.mPendingMO = null;
        ImsPhoneConnection findConnection = findConnection(imsCall);
        if (findConnection != null) {
            state = findConnection.getState();
        } else {
            state = Call.State.DIALING;
        }
        processCallStateChange(imsCall, Call.State.DISCONNECTED, getDisconnectCauseFromReasonInfo(imsReasonInfo, state));
        if (findConnection != null) {
            findConnection.setPreciseDisconnectCause(getPreciseDisconnectCauseFromReasonInfo(imsReasonInfo));
        }
        this.mPhone.notifyImsReason(imsReasonInfo);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public ImsUtInterface getUtInterface() throws ImsException {
        ImsManager imsManager = this.mImsManager;
        if (imsManager == null) {
            throw getImsManagerIsNullException();
        }
        return imsManager.createOrGetSupplementaryServiceConfiguration();
    }

    private void transferHandoverConnections(ImsPhoneCall imsPhoneCall) {
        if (imsPhoneCall.getConnections() != null) {
            Iterator<com.android.internal.telephony.Connection> it = imsPhoneCall.getConnections().iterator();
            while (it.hasNext()) {
                com.android.internal.telephony.Connection next = it.next();
                next.mPreHandoverState = imsPhoneCall.mState;
                log("Connection state before handover is " + next.getStateBeforeHandover());
            }
        }
        if (this.mHandoverCall.getConnections() == null) {
            this.mHandoverCall.mConnections = imsPhoneCall.mConnections;
        } else {
            this.mHandoverCall.mConnections.addAll(imsPhoneCall.mConnections);
        }
        this.mHandoverCall.copyConnectionFrom(imsPhoneCall);
        if (this.mHandoverCall.getConnections() != null) {
            if (imsPhoneCall.getImsCall() != null) {
                imsPhoneCall.getImsCall().close();
            }
            Iterator<com.android.internal.telephony.Connection> it2 = this.mHandoverCall.getConnections().iterator();
            while (it2.hasNext()) {
                ImsPhoneConnection imsPhoneConnection = (ImsPhoneConnection) it2.next();
                imsPhoneConnection.changeParent(this.mHandoverCall);
                imsPhoneConnection.releaseWakeLock();
            }
        }
        if (imsPhoneCall.getState().isAlive()) {
            log("Call is alive and state is " + imsPhoneCall.mState);
            this.mHandoverCall.mState = imsPhoneCall.mState;
        }
        imsPhoneCall.clearConnections();
        imsPhoneCall.mState = Call.State.IDLE;
        if (this.mPendingMO != null) {
            logi("pending MO on handover, clearing...");
            this.mPendingMO = null;
        }
    }

    public void notifySrvccState(int i) {
        log("notifySrvccState state=" + i);
        ImsManager imsManager = this.mImsManager;
        if (imsManager != null) {
            try {
                if (i == 0) {
                    imsManager.notifySrvccStarted(this.mSrvccStartedCallback);
                } else if (i == 1) {
                    imsManager.notifySrvccCompleted();
                } else if (i == 2) {
                    imsManager.notifySrvccFailed();
                } else if (i == 3) {
                    imsManager.notifySrvccCanceled();
                }
            } catch (ImsException e) {
                loge("notifySrvccState : exception " + e);
            }
        }
        if (i == 0) {
            this.mSrvccState = Call.SrvccState.STARTED;
        } else if (i != 1) {
            if (i == 2) {
                this.mSrvccState = Call.SrvccState.FAILED;
            } else if (i != 3) {
            } else {
                this.mSrvccState = Call.SrvccState.CANCELED;
            }
        } else {
            this.mSrvccState = Call.SrvccState.COMPLETED;
            this.mForegroundCall.maybeStopRingback();
            resetState();
            transferHandoverConnections(this.mForegroundCall);
            transferHandoverConnections(this.mBackgroundCall);
            transferHandoverConnections(this.mRingingCall);
            updatePhoneState();
            this.mImsCallInfoTracker.notifySrvccCompleted();
        }
    }

    private void resetState() {
        this.mIsInEmergencyCall = false;
        this.mPhone.setEcmCanceledForEmergency(false);
        this.mHoldSwitchingState = HoldSwapState.INACTIVE;
    }

    @VisibleForTesting
    public boolean isHoldOrSwapInProgress() {
        return this.mHoldSwitchingState != HoldSwapState.INACTIVE;
    }

    @Override // com.android.internal.telephony.CallTracker, android.os.Handler
    public void handleMessage(Message message) {
        Object[] objArr;
        ImsTrafficSession imsTrafficSession;
        log("handleMessage what=" + message.what);
        switch (message.what) {
            case 14:
                if (this.pendingCallInEcm) {
                    dialInternal(this.mPendingMO, this.pendingCallClirMode, this.mPendingCallVideoState, this.mPendingIntentExtras);
                    this.mPendingIntentExtras = null;
                    this.pendingCallInEcm = false;
                }
                this.mPhone.unsetOnEcbModeExitResponse(this);
                return;
            case 15:
            case 16:
            case 17:
            case 19:
            case 24:
            default:
                return;
            case 18:
                ImsPhoneConnection imsPhoneConnection = this.mPendingMO;
                if (imsPhoneConnection != null) {
                    imsPhoneConnection.onDisconnect();
                    removeConnection(this.mPendingMO);
                    this.mPendingMO = null;
                }
                this.mPendingIntentExtras = null;
                updatePhoneState();
                this.mPhone.notifyPreciseCallStateChanged();
                return;
            case 20:
                dialInternal(this.mPendingMO, this.mClirMode, this.mPendingCallVideoState, this.mPendingIntentExtras);
                this.mPendingIntentExtras = null;
                return;
            case 21:
                if (this.mPendingMO != null) {
                    try {
                        getEcbmInterface().exitEmergencyCallbackMode();
                        this.mPhone.setOnEcbModeExitResponse(this, 14, null);
                        this.pendingCallClirMode = this.mClirMode;
                        this.pendingCallInEcm = true;
                        return;
                    } catch (ImsException e) {
                        e.printStackTrace();
                        this.mPendingMO.setDisconnectCause(36);
                        sendEmptyMessageDelayed(18, 500L);
                        return;
                    }
                }
                return;
            case 22:
                AsyncResult asyncResult = (AsyncResult) message.obj;
                ImsCall imsCall = (ImsCall) asyncResult.userObj;
                Long valueOf = Long.valueOf(((Long) asyncResult.result).longValue());
                log("VT data usage update. usage = " + valueOf + ", imsCall = " + imsCall);
                if (valueOf.longValue() > 0) {
                    updateVtDataUsage(imsCall, valueOf.longValue());
                    return;
                }
                return;
            case 23:
                Object obj = ((AsyncResult) message.obj).result;
                if (obj instanceof Pair) {
                    Pair pair = (Pair) obj;
                    onDataEnabledChanged(((Boolean) pair.first).booleanValue(), ((Integer) pair.second).intValue());
                    return;
                }
                return;
            case 25:
                Object obj2 = message.obj;
                if (obj2 instanceof ImsCall) {
                    ImsCall imsCall2 = (ImsCall) obj2;
                    if (imsCall2 != this.mForegroundCall.getImsCall()) {
                        Rlog.i("ImsPhoneCallTracker", "handoverCheck: no longer FG; check skipped.");
                        unregisterForConnectivityChanges();
                        return;
                    }
                    if (!this.mHasAttemptedStartOfCallHandover) {
                        this.mHasAttemptedStartOfCallHandover = true;
                    }
                    if (imsCall2.isWifiCall()) {
                        return;
                    }
                    ImsPhoneConnection findConnection = findConnection(imsCall2);
                    if (findConnection != null) {
                        Rlog.i("ImsPhoneCallTracker", "handoverCheck: handover failed.");
                        findConnection.onHandoverToWifiFailed();
                    }
                    if (imsCall2.isVideoCall() && findConnection.getDisconnectCause() == 0) {
                        registerForConnectivityChanges();
                        return;
                    }
                    return;
                }
                return;
            case 26:
                SomeArgs someArgs = (SomeArgs) message.obj;
                try {
                    handleFeatureCapabilityChanged((ImsFeature.Capabilities) someArgs.arg1);
                    updateImsRegistrationInfo();
                    return;
                } finally {
                    someArgs.recycle();
                }
            case 27:
                AsyncResult asyncResult2 = (AsyncResult) message.obj;
                ImsPhoneMmiCode imsPhoneMmiCode = new ImsPhoneMmiCode(this.mPhone);
                try {
                    imsPhoneMmiCode.setIsSsInfo(true);
                    imsPhoneMmiCode.processImsSsData(asyncResult2);
                    return;
                } catch (ImsException e2) {
                    Rlog.e("ImsPhoneCallTracker", "Exception in parsing SS Data: " + e2);
                    return;
                }
            case 28:
                Pair pair2 = (Pair) ((AsyncResult) message.obj).userObj;
                removeMessages(29);
                this.mPhone.getDefaultPhone().mCi.unregisterForOn(this);
                ImsPhoneConnection findConnection2 = findConnection((ImsCall) pair2.first);
                if (findConnection2 == null) {
                    sendCallStartFailedDisconnect((ImsCall) pair2.first, (ImsReasonInfo) pair2.second);
                    return;
                }
                this.mForegroundCall.detach(findConnection2);
                removeConnection(findConnection2);
                try {
                    findConnection2.onOriginalConnectionReplaced(this.mPhone.getDefaultPhone().dial(this.mLastDialString, this.mLastDialArgs));
                    ImsCall imsCall3 = this.mForegroundCall.getImsCall();
                    imsCall3.getCallProfile().setCallExtraBoolean("e_call", true);
                    findConnection(imsCall3).updateExtras(imsCall3);
                    return;
                } catch (CallStateException unused) {
                    sendCallStartFailedDisconnect((ImsCall) pair2.first, (ImsReasonInfo) pair2.second);
                    return;
                }
            case 29:
                Pair pair3 = (Pair) message.obj;
                this.mPhone.getDefaultPhone().mCi.unregisterForOn(this);
                removeMessages(28);
                sendCallStartFailedDisconnect((ImsCall) pair3.first, (ImsReasonInfo) pair3.second);
                return;
            case 30:
                try {
                    answerWaitingCall();
                    return;
                } catch (ImsException e3) {
                    loge("handleMessage EVENT_ANSWER_WAITING_CALL exception=" + e3);
                    return;
                }
            case 31:
                try {
                    resumeForegroundCall();
                    return;
                } catch (ImsException e4) {
                    loge("handleMessage EVENT_RESUME_NOW_FOREGROUND_CALL exception=" + e4);
                    return;
                }
            case 32:
                Pair pair4 = (Pair) message.obj;
                removeMessages(32);
                ImsPhoneConnection findConnection3 = findConnection((ImsCall) pair4.first);
                if (findConnection3 == null) {
                    sendCallStartFailedDisconnect((ImsCall) pair4.first, (ImsReasonInfo) pair4.second);
                    return;
                }
                this.mForegroundCall.detach(findConnection3);
                removeConnection(findConnection3);
                try {
                    this.mPendingMO = null;
                    findConnection3.onOriginalConnectionReplaced(this.mPhone.getDefaultPhone().dial(this.mLastDialString, ImsPhone.ImsDialArgs.Builder.from((PhoneInternalInterface.DialArgs) this.mLastDialArgs).setRttTextStream(null).setRetryCallFailCause(CallFailCause.EMC_REDIAL_ON_IMS).setRetryCallFailNetworkType(ServiceState.rilRadioTechnologyToNetworkType(findConnection3.getCallRadioTech())).build()));
                    return;
                } catch (CallStateException unused2) {
                    sendCallStartFailedDisconnect((ImsCall) pair4.first, (ImsReasonInfo) pair4.second);
                    return;
                }
            case 33:
            case 34:
                AsyncResult asyncResult3 = (AsyncResult) message.obj;
                IImsTrafficSessionCallback iImsTrafficSessionCallback = (IImsTrafficSessionCallback) asyncResult3.userObj;
                try {
                    if (asyncResult3.exception != null || (objArr = (Object[]) asyncResult3.result) == null || objArr.length <= 1) {
                        if (iImsTrafficSessionCallback != null) {
                            iImsTrafficSessionCallback.onError(new ConnectionFailureInfo(65535, 0, -1));
                            return;
                        }
                        return;
                    }
                    if (iImsTrafficSessionCallback == null && (imsTrafficSession = getImsTrafficSession(((Integer) objArr[0]).intValue())) != null) {
                        iImsTrafficSessionCallback = imsTrafficSession.mCallback;
                    }
                    if (iImsTrafficSessionCallback == null) {
                        return;
                    }
                    Object obj3 = objArr[1];
                    if (obj3 == null) {
                        iImsTrafficSessionCallback.onReady();
                        return;
                    } else {
                        iImsTrafficSessionCallback.onError((ConnectionFailureInfo) obj3);
                        return;
                    }
                } catch (RemoteException e5) {
                    Rlog.e("ImsPhoneCallTracker", "Exception: " + e5);
                    return;
                }
            case 35:
                try {
                    MediaQualityStatus queryMediaQualityStatus = this.mImsManager.queryMediaQualityStatus(1);
                    if (queryMediaQualityStatus != null) {
                        ImsPhone imsPhone = this.mPhone;
                        if (imsPhone != null && imsPhone.mDefaultPhone != null) {
                            log("notify media quality status: " + queryMediaQualityStatus);
                            this.mPhone.onMediaQualityStatusChanged(queryMediaQualityStatus);
                        } else {
                            loge("onMediaQualityStatusChanged: null phone");
                        }
                    }
                    return;
                } catch (ImsException e6) {
                    Rlog.e("ImsPhoneCallTracker", "Exception in queryMediaQualityStatus: " + e6);
                    return;
                }
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public void updateVtDataUsage(ImsCall imsCall, long j) {
        long longValue = j - (this.mVtDataUsageMap.containsKey(Integer.valueOf(imsCall.uniqueId)) ? this.mVtDataUsageMap.get(Integer.valueOf(imsCall.uniqueId)).longValue() : 0L);
        this.mVtDataUsageMap.put(Integer.valueOf(imsCall.uniqueId), Long.valueOf(j));
        log("updateVtDataUsage: call=" + imsCall + ", delta=" + longValue);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        boolean dataRoaming = this.mPhone.getServiceState().getDataRoaming();
        long j2 = longValue / 2;
        this.mVtDataUsageSnapshot = new NetworkStats(elapsedRealtime, 1).add(this.mVtDataUsageSnapshot).addEntry(new NetworkStats.Entry(getVtInterface(), -1, 1, 0, 1, dataRoaming ? 1 : 0, 1, j2, 0L, j2, 0L, 0L));
        NetworkStats add = new NetworkStats(elapsedRealtime, 1).add(this.mVtDataUsageUidSnapshot);
        if (this.mDefaultDialerUid.get() == -1) {
            this.mDefaultDialerUid.set(getPackageUid(this.mPhone.getContext(), ((TelecomManager) this.mPhone.getContext().getSystemService("telecom")).getDefaultDialerPackage()));
        }
        this.mVtDataUsageUidSnapshot = add.addEntry(new NetworkStats.Entry(getVtInterface(), this.mDefaultDialerUid.get(), 1, 0, 1, dataRoaming ? 1 : 0, 1, j2, 0L, j2, 0L, 0L));
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public String getVtInterface() {
        return "vt_data0" + this.mPhone.getSubId();
    }

    @Override // com.android.internal.telephony.CallTracker
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void log(String str) {
        Rlog.d("ImsPhoneCallTracker", "[" + this.mPhone.getPhoneId() + "] " + str);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void loge(String str) {
        Rlog.e("ImsPhoneCallTracker", "[" + this.mPhone.getPhoneId() + "] " + str);
    }

    void logi(String str) {
        Rlog.i("ImsPhoneCallTracker", "[" + this.mPhone.getPhoneId() + "] " + str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.telephony.imsphone.ImsPhoneCallTracker$13 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C024513 {

        /* renamed from: $SwitchMap$com$android$internal$telephony$imsphone$ImsPhoneCallTracker$HoldSwapState */
        static final /* synthetic */ int[] f12x24203e2c;

        static {
            int[] iArr = new int[HoldSwapState.values().length];
            f12x24203e2c = iArr;
            try {
                iArr[HoldSwapState.INACTIVE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                f12x24203e2c[HoldSwapState.PENDING_SINGLE_CALL_HOLD.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                f12x24203e2c[HoldSwapState.PENDING_SINGLE_CALL_UNHOLD.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                f12x24203e2c[HoldSwapState.SWAPPING_ACTIVE_AND_HELD.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                f12x24203e2c[HoldSwapState.HOLDING_TO_ANSWER_INCOMING.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                f12x24203e2c[HoldSwapState.PENDING_RESUME_FOREGROUND_AFTER_FAILURE.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                f12x24203e2c[HoldSwapState.HOLDING_TO_DIAL_OUTGOING.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                f12x24203e2c[HoldSwapState.PENDING_RESUME_FOREGROUND_AFTER_HOLD.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
        }
    }

    void logHoldSwapState(String str) {
        String str2;
        switch (C024513.f12x24203e2c[this.mHoldSwitchingState.ordinal()]) {
            case 1:
                str2 = "INACTIVE";
                break;
            case 2:
                str2 = "PENDING_SINGLE_CALL_HOLD";
                break;
            case 3:
                str2 = "PENDING_SINGLE_CALL_UNHOLD";
                break;
            case 4:
                str2 = "SWAPPING_ACTIVE_AND_HELD";
                break;
            case 5:
                str2 = "HOLDING_TO_ANSWER_INCOMING";
                break;
            case 6:
                str2 = "PENDING_RESUME_FOREGROUND_AFTER_FAILURE";
                break;
            case 7:
                str2 = "HOLDING_TO_DIAL_OUTGOING";
                break;
            case 8:
                str2 = "PENDING_RESUME_FOREGROUND_AFTER_HOLD";
                break;
            default:
                str2 = "???";
                break;
        }
        logi("holdSwapState set to " + str2 + " at " + str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void logState() {
        if (VERBOSE_STATE_LOGGING) {
            Rlog.v("ImsPhoneCallTracker", "Current IMS PhoneCall State:\n Foreground: " + this.mForegroundCall + "\n Background: " + this.mBackgroundCall + "\n Ringing: " + this.mRingingCall + "\n Handover: " + this.mHandoverCall + "\n");
        }
    }

    @Override // com.android.internal.telephony.CallTracker
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println("ImsPhoneCallTracker extends:");
        indentingPrintWriter.increaseIndent();
        super.dump(fileDescriptor, indentingPrintWriter, strArr);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println(" mVoiceCallEndedRegistrants=" + this.mVoiceCallEndedRegistrants);
        indentingPrintWriter.println(" mVoiceCallStartedRegistrants=" + this.mVoiceCallStartedRegistrants);
        indentingPrintWriter.println(" mRingingCall=" + this.mRingingCall);
        indentingPrintWriter.println(" mForegroundCall=" + this.mForegroundCall);
        indentingPrintWriter.println(" mBackgroundCall=" + this.mBackgroundCall);
        indentingPrintWriter.println(" mHandoverCall=" + this.mHandoverCall);
        indentingPrintWriter.println(" mPendingMO=" + this.mPendingMO);
        indentingPrintWriter.println(" mPhone=" + this.mPhone);
        indentingPrintWriter.println(" mDesiredMute=" + this.mDesiredMute);
        indentingPrintWriter.println(" mState=" + this.mState);
        indentingPrintWriter.println(" mMmTelCapabilities=" + this.mMmTelCapabilities);
        indentingPrintWriter.println(" mDefaultDialerUid=" + this.mDefaultDialerUid.get());
        indentingPrintWriter.println(" mVtDataUsageSnapshot=" + this.mVtDataUsageSnapshot);
        indentingPrintWriter.println(" mVtDataUsageUidSnapshot=" + this.mVtDataUsageUidSnapshot);
        indentingPrintWriter.println(" mCallQualityMetrics=" + this.mCallQualityMetrics);
        indentingPrintWriter.println(" mCallQualityMetricsHistory=" + this.mCallQualityMetricsHistory);
        indentingPrintWriter.println(" mIsConferenceEventPackageHandlingEnabled=" + this.mIsConferenceEventPackageEnabled);
        indentingPrintWriter.println(" mSupportCepOnPeer=" + this.mSupportCepOnPeer);
        if (this.mConfig != null) {
            indentingPrintWriter.print(" isDeviceToDeviceCommsSupported= " + this.mConfig.isD2DCommunicationSupported);
            indentingPrintWriter.println("(forceEnabled=" + this.mDeviceToDeviceForceEnabled + ")");
            if (this.mConfig.isD2DCommunicationSupported) {
                indentingPrintWriter.println(" mSupportD2DUsingRtp= " + this.mSupportD2DUsingRtp);
                indentingPrintWriter.println(" mSupportSdpForRtpHeaderExtensions= " + this.mSupportSdpForRtpHeaderExtensions);
            }
        }
        indentingPrintWriter.println(" mSrvccTypeSupported=" + this.mSrvccTypeSupported);
        indentingPrintWriter.println(" Event Log:");
        indentingPrintWriter.increaseIndent();
        this.mOperationLocalLog.dump(indentingPrintWriter);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.flush();
        indentingPrintWriter.println("++++++++++++++++++++++++++++++++");
        try {
            ImsManager imsManager = this.mImsManager;
            if (imsManager != null) {
                imsManager.dump(fileDescriptor, indentingPrintWriter, strArr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<ImsPhoneConnection> arrayList = this.mConnections;
        if (arrayList == null || arrayList.size() <= 0) {
            return;
        }
        indentingPrintWriter.println("mConnections:");
        for (int i = 0; i < this.mConnections.size(); i++) {
            indentingPrintWriter.println("  [" + i + "]: " + this.mConnections.get(i));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public ImsEcbm getEcbmInterface() throws ImsException {
        ImsManager imsManager = this.mImsManager;
        if (imsManager == null) {
            throw getImsManagerIsNullException();
        }
        return imsManager.getEcbmInterface();
    }

    public boolean isInEmergencyCall() {
        return this.mIsInEmergencyCall;
    }

    public boolean isImsCapabilityAvailable(int i, int i2) throws ImsException {
        ImsManager imsManager = this.mImsManager;
        if (imsManager != null) {
            return imsManager.queryMmTelCapabilityStatus(i, i2);
        }
        return false;
    }

    public boolean isVoiceOverCellularImsEnabled() {
        return isImsCapabilityInCacheAvailable(1, 0) || isImsCapabilityInCacheAvailable(1, 3);
    }

    public boolean isVowifiEnabled() {
        return isImsCapabilityInCacheAvailable(1, 1) || isImsCapabilityInCacheAvailable(1, 2);
    }

    public boolean isVideoCallEnabled() {
        return this.mMmTelCapabilities.isCapable(2);
    }

    private boolean isImsCapabilityInCacheAvailable(int i, int i2) {
        return getImsRegistrationTech() == i2 && this.mMmTelCapabilities.isCapable(i);
    }

    @Override // com.android.internal.telephony.CallTracker
    public PhoneConstants.State getState() {
        return this.mState;
    }

    public int getImsRegistrationTech() {
        ImsManager imsManager = this.mImsManager;
        if (imsManager != null) {
            return imsManager.getRegistrationTech();
        }
        return -1;
    }

    public void getImsRegistrationTech(Consumer<Integer> consumer) {
        ImsManager imsManager = this.mImsManager;
        if (imsManager != null) {
            imsManager.getRegistrationTech(consumer);
        } else {
            consumer.accept(-1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void setVideoCallProvider(ImsPhoneConnection imsPhoneConnection, ImsCall imsCall) throws RemoteException {
        IImsVideoCallProvider videoCallProvider = imsCall.getCallSession().getVideoCallProvider();
        if (videoCallProvider != null) {
            boolean z = this.mPhone.getContext().getResources().getBoolean(17891866);
            ImsVideoCallProviderWrapper imsVideoCallProviderWrapper = new ImsVideoCallProviderWrapper(videoCallProvider);
            if (z) {
                imsVideoCallProviderWrapper.setUseVideoPauseWorkaround(z);
            }
            imsPhoneConnection.setVideoProvider(imsVideoCallProviderWrapper);
            imsVideoCallProviderWrapper.registerForDataUsageUpdate(this, 22, imsCall);
            imsVideoCallProviderWrapper.addImsVideoProviderCallback(imsPhoneConnection);
        }
    }

    public boolean isUtEnabled() {
        return this.mMmTelCapabilities.isCapable(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public PersistableBundle getCarrierConfigBundle(int i) {
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mPhone.getContext().getSystemService("carrier_config");
        if (carrierConfigManager == null) {
            loge("getCarrierConfigBundle: No carrier config service found");
            return null;
        }
        PersistableBundle configForSubId = carrierConfigManager.getConfigForSubId(i);
        if (configForSubId == null) {
            loge("getCarrierConfigBundle: carrier config is null, skipping.");
            return null;
        }
        return configForSubId;
    }

    private String cleanseInstantLetteringMessage(String str) {
        PersistableBundle carrierConfigBundle;
        if (TextUtils.isEmpty(str) || (carrierConfigBundle = getCarrierConfigBundle(this.mPhone.getSubId())) == null) {
            return str;
        }
        String string = carrierConfigBundle.getString("carrier_instant_lettering_invalid_chars_string");
        if (!TextUtils.isEmpty(string)) {
            str = str.replaceAll(string, PhoneConfigurationManager.SSSS);
        }
        String string2 = carrierConfigBundle.getString("carrier_instant_lettering_escaped_chars_string");
        return !TextUtils.isEmpty(string2) ? escapeChars(string2, str) : str;
    }

    private String escapeChars(String str, String str2) {
        char[] charArray;
        StringBuilder sb = new StringBuilder();
        for (char c : str2.toCharArray()) {
            if (str.contains(Character.toString(c))) {
                sb.append("\\");
            }
            sb.append(c);
        }
        return sb.toString();
    }

    @Override // com.android.internal.telephony.imsphone.ImsPullCall
    public void pullExternalCall(String str, int i, int i2) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("CallPull", true);
        bundle.putInt(ImsExternalCallTracker.EXTRA_IMS_EXTERNAL_CALL_ID, i2);
        try {
            this.mPhone.notifyUnknownConnection(dial(str, i, bundle));
        } catch (CallStateException e) {
            loge("pullExternalCall failed - " + e);
        }
    }

    private ImsException getImsManagerIsNullException() {
        return new ImsException("no ims manager", (int) CallFailCause.RECOVERY_ON_TIMER_EXPIRY);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldDisconnectActiveCallOnAnswer(ImsCall imsCall, ImsCall imsCall2) {
        if (imsCall == null || imsCall2 == null || !this.mDropVideoCallWhenAnsweringAudioCall) {
            return false;
        }
        boolean z = imsCall.isVideoCall() || (this.mTreatDowngradedVideoCallsAsVideoCalls && imsCall.wasVideoCall());
        boolean isWifiCall = imsCall.isWifiCall();
        boolean z2 = this.mImsManager.isWfcEnabledByPlatform() && this.mImsManager.isWfcEnabledByUser();
        boolean z3 = !imsCall2.isVideoCall();
        log("shouldDisconnectActiveCallOnAnswer : isActiveCallVideo=" + z + " isActiveCallOnWifi=" + isWifiCall + " isIncomingCallAudio=" + z3 + " isVowifiEnabled=" + z2);
        return z && isWifiCall && z3 && !z2;
    }

    public void registerPhoneStateListener(PhoneStateListener phoneStateListener) {
        this.mPhoneStateListeners.add(phoneStateListener);
    }

    public void unregisterPhoneStateListener(PhoneStateListener phoneStateListener) {
        this.mPhoneStateListeners.remove(phoneStateListener);
    }

    private void notifyPhoneStateChanged(PhoneConstants.State state, PhoneConstants.State state2) {
        for (PhoneStateListener phoneStateListener : this.mPhoneStateListeners) {
            phoneStateListener.onPhoneStateChanged(state, state2);
        }
    }

    private void modifyVideoCall(ImsCall imsCall, int i) {
        ImsPhoneConnection findConnection = findConnection(imsCall);
        if (findConnection != null) {
            int videoState = findConnection.getVideoState();
            if (findConnection.getVideoProvider() != null) {
                findConnection.getVideoProvider().onSendSessionModifyRequest(new VideoProfile(videoState), new VideoProfile(i));
            }
        }
    }

    public boolean isViLteDataMetered() {
        return this.mIsViLteDataMetered;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDataEnabledChanged(boolean z, int i) {
        log("onDataEnabledChanged: enabled=" + z + ", reason=" + i);
        this.mIsDataEnabled = z;
        if (!this.mIsViLteDataMetered) {
            StringBuilder sb = new StringBuilder();
            sb.append("Ignore data ");
            sb.append(z ? "enabled" : "disabled");
            sb.append(" - carrier policy indicates that data is not metered for ViLTE calls.");
            log(sb.toString());
            return;
        }
        Iterator<ImsPhoneConnection> it = this.mConnections.iterator();
        while (true) {
            boolean z2 = true;
            if (!it.hasNext()) {
                break;
            }
            ImsPhoneConnection next = it.next();
            ImsCall imsCall = next.getImsCall();
            if (!z && (imsCall == null || !imsCall.isWifiCall())) {
                z2 = false;
            }
            next.setLocalVideoCapable(z2);
        }
        int i2 = i == 1 ? 1405 : 1406;
        maybeNotifyDataDisabled(z, i2);
        handleDataEnabledChange(z, i2);
        if (this.mShouldUpdateImsConfigOnDisconnect || i == -1 || !this.mCarrierConfigLoadedForSubscription) {
            return;
        }
        updateImsServiceConfig();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateImsServiceConfig() {
        ImsManager imsManager = this.mImsManager;
        if (imsManager == null || !this.mCarrierConfigLoadedForSubscription) {
            return;
        }
        imsManager.updateImsServiceConfig();
    }

    private void maybeNotifyDataDisabled(boolean z, int i) {
        if (z) {
            return;
        }
        Iterator<ImsPhoneConnection> it = this.mConnections.iterator();
        while (it.hasNext()) {
            ImsPhoneConnection next = it.next();
            ImsCall imsCall = next.getImsCall();
            if (imsCall != null && imsCall.isVideoCall() && !imsCall.isWifiCall() && next.hasCapabilities(3)) {
                if (i == 1406) {
                    next.onConnectionEvent("android.telephony.event.EVENT_DOWNGRADE_DATA_DISABLED", null);
                } else if (i == 1405) {
                    next.onConnectionEvent("android.telephony.event.EVENT_DOWNGRADE_DATA_LIMIT_REACHED", null);
                }
            }
        }
    }

    private void handleDataEnabledChange(boolean z, int i) {
        if (!z) {
            Iterator<ImsPhoneConnection> it = this.mConnections.iterator();
            while (it.hasNext()) {
                ImsPhoneConnection next = it.next();
                ImsCall imsCall = next.getImsCall();
                if (imsCall != null && imsCall.isVideoCall() && !imsCall.isWifiCall()) {
                    log("handleDataEnabledChange - downgrading " + next);
                    downgradeVideoCall(i, next);
                }
            }
        } else if (this.mSupportPauseVideo) {
            Iterator<ImsPhoneConnection> it2 = this.mConnections.iterator();
            while (it2.hasNext()) {
                ImsPhoneConnection next2 = it2.next();
                log("handleDataEnabledChange - resuming " + next2);
                if (VideoProfile.isPaused(next2.getVideoState()) && next2.wasVideoPausedFromSource(2)) {
                    next2.resumeVideo(2);
                }
            }
            this.mShouldUpdateImsConfigOnDisconnect = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void downgradeVideoCall(int i, ImsPhoneConnection imsPhoneConnection) {
        ImsCall imsCall = imsPhoneConnection.getImsCall();
        if (imsCall != null) {
            if (imsPhoneConnection.hasCapabilities(3) && !this.mSupportPauseVideo) {
                log("downgradeVideoCall :: callId=" + imsPhoneConnection.getTelecomCallId() + " Downgrade to audio");
                modifyVideoCall(imsCall, 0);
            } else if (this.mSupportPauseVideo && i != 1407) {
                log("downgradeVideoCall :: callId=" + imsPhoneConnection.getTelecomCallId() + " Pause audio");
                this.mShouldUpdateImsConfigOnDisconnect = true;
                imsPhoneConnection.pauseVideo(2);
            } else {
                log("downgradeVideoCall :: callId=" + imsPhoneConnection.getTelecomCallId() + " Disconnect call.");
                imsCall.terminate(501, i);
            }
        }
    }

    private void resetImsCapabilities() {
        log("Resetting Capabilities...");
        boolean isVideoCallEnabled = isVideoCallEnabled();
        this.mMmTelCapabilities = new MmTelFeature.MmTelCapabilities();
        this.mPhone.setServiceState(1);
        this.mPhone.resetImsRegistrationState();
        this.mPhone.processDisconnectReason(new ImsReasonInfo(106, 0));
        boolean isVideoCallEnabled2 = isVideoCallEnabled();
        if (isVideoCallEnabled != isVideoCallEnabled2) {
            this.mPhone.notifyForVideoCapabilityChanged(isVideoCallEnabled2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isWifiConnected() {
        NetworkInfo activeNetworkInfo;
        ConnectivityManager connectivityManager = (ConnectivityManager) this.mPhone.getContext().getSystemService("connectivity");
        return connectivityManager != null && (activeNetworkInfo = connectivityManager.getActiveNetworkInfo()) != null && activeNetworkInfo.isConnected() && activeNetworkInfo.getType() == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerForConnectivityChanges() {
        ConnectivityManager connectivityManager;
        if (this.mIsMonitoringConnectivity || !this.mNotifyVtHandoverToWifiFail || (connectivityManager = (ConnectivityManager) this.mPhone.getContext().getSystemService("connectivity")) == null) {
            return;
        }
        Rlog.i("ImsPhoneCallTracker", "registerForConnectivityChanges");
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        builder.addTransportType(1);
        connectivityManager.registerNetworkCallback(builder.build(), this.mNetworkCallback);
        this.mIsMonitoringConnectivity = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterForConnectivityChanges() {
        ConnectivityManager connectivityManager;
        if (this.mIsMonitoringConnectivity && this.mNotifyVtHandoverToWifiFail && (connectivityManager = (ConnectivityManager) this.mPhone.getContext().getSystemService("connectivity")) != null) {
            Rlog.i("ImsPhoneCallTracker", "unregisterForConnectivityChanges");
            connectivityManager.unregisterNetworkCallback(this.mNetworkCallback);
            this.mIsMonitoringConnectivity = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scheduleHandoverCheck() {
        ImsCall imsCall = this.mForegroundCall.getImsCall();
        ImsPhoneConnection firstConnection = this.mForegroundCall.getFirstConnection();
        if (!this.mNotifyVtHandoverToWifiFail || imsCall == null || !imsCall.isVideoCall() || firstConnection == null || firstConnection.getDisconnectCause() != 0 || hasMessages(25)) {
            return;
        }
        Rlog.i("ImsPhoneCallTracker", "scheduleHandoverCheck: schedule");
        sendMessageDelayed(obtainMessage(25, imsCall), 60000L);
    }

    public boolean isCarrierDowngradeOfVtCallSupported() {
        return this.mSupportDowngradeVtToAudio;
    }

    @VisibleForTesting
    public void setDataEnabled(boolean z) {
        this.mIsDataEnabled = z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pruneCallQualityMetricsHistory() {
        if (this.mCallQualityMetricsHistory.size() > 10) {
            this.mCallQualityMetricsHistory.poll();
        }
    }

    private void handleFeatureCapabilityChanged(ImsFeature.Capabilities capabilities) {
        boolean isVideoCallEnabled = isVideoCallEnabled();
        StringBuilder sb = new StringBuilder((int) TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IFACE_AND_POL_FAMILY_MISMATCH);
        sb.append("handleFeatureCapabilityChanged: ");
        sb.append(capabilities);
        this.mMmTelCapabilities = new MmTelFeature.MmTelCapabilities(capabilities);
        boolean isVideoCallEnabled2 = isVideoCallEnabled();
        boolean z = isVideoCallEnabled != isVideoCallEnabled2;
        sb.append(" isVideoEnabledStateChanged=");
        sb.append(z);
        if (z) {
            log("handleFeatureCapabilityChanged - notifyForVideoCapabilityChanged=" + isVideoCallEnabled2);
            this.mPhone.notifyForVideoCapabilityChanged(isVideoCallEnabled2);
        }
        log(sb.toString());
        String str = "handleFeatureCapabilityChanged: isVolteEnabled=" + isVoiceOverCellularImsEnabled() + ", isVideoCallEnabled=" + isVideoCallEnabled() + ", isVowifiEnabled=" + isVowifiEnabled() + ", isUtEnabled=" + isUtEnabled();
        log(str);
        this.mRegLocalLog.log(str);
        this.mPhone.onFeatureCapabilityChanged();
        int imsRegistrationTech = getImsRegistrationTech();
        this.mMetrics.writeOnImsCapabilities(this.mPhone.getPhoneId(), imsRegistrationTech, this.mMmTelCapabilities);
        this.mPhone.getImsStats().onImsCapabilitiesChanged(imsRegistrationTech, this.mMmTelCapabilities);
    }

    @VisibleForTesting
    public void onCallHoldReceived(ImsCall imsCall) {
        log("onCallHoldReceived");
        ImsPhoneConnection findConnection = findConnection(imsCall);
        if (findConnection != null) {
            if (!this.mOnHoldToneStarted && ((ImsPhoneCall.isLocalTone(imsCall) || this.mAlwaysPlayRemoteHoldTone) && findConnection.getState() == Call.State.ACTIVE)) {
                this.mPhone.startOnHoldTone(findConnection);
                this.mOnHoldToneStarted = true;
                this.mOnHoldToneId = System.identityHashCode(findConnection);
            }
            findConnection.onConnectionEvent("android.telecom.event.CALL_REMOTELY_HELD", null);
            this.mImsCallInfoTracker.updateImsCallStatus(findConnection, true, false);
            if (this.mPhone.getContext().getResources().getBoolean(17891866) && this.mSupportPauseVideo && VideoProfile.isVideo(findConnection.getVideoState())) {
                findConnection.changeToPausedState();
            }
        }
        SuppServiceNotification suppServiceNotification = new SuppServiceNotification();
        suppServiceNotification.notificationType = 1;
        suppServiceNotification.code = 2;
        this.mPhone.notifySuppSvcNotification(suppServiceNotification);
        this.mMetrics.writeOnImsCallHoldReceived(this.mPhone.getPhoneId(), imsCall.getCallSession());
    }

    @VisibleForTesting
    public void setAlwaysPlayRemoteHoldTone(boolean z) {
        this.mAlwaysPlayRemoteHoldTone = z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getNetworkCountryIso() {
        ServiceStateTracker serviceStateTracker;
        LocaleTracker localeTracker;
        ImsPhone imsPhone = this.mPhone;
        return (imsPhone == null || (serviceStateTracker = imsPhone.getServiceStateTracker()) == null || (localeTracker = serviceStateTracker.getLocaleTracker()) == null) ? PhoneConfigurationManager.SSSS : localeTracker.getCurrentCountry();
    }

    @Override // com.android.internal.telephony.CallTracker
    public ImsPhone getPhone() {
        return this.mPhone;
    }

    @VisibleForTesting
    public void setSupportCepOnPeer(boolean z) {
        this.mSupportCepOnPeer = z;
    }

    public void injectTestConferenceState(ImsConferenceState imsConferenceState) {
        List<ConferenceParticipant> parseConferenceState = ImsCall.parseConferenceState(imsConferenceState);
        Iterator<ImsPhoneConnection> it = getConnections().iterator();
        while (it.hasNext()) {
            it.next().updateConferenceParticipants(parseConferenceState);
        }
    }

    public void setConferenceEventPackageEnabled(boolean z) {
        log("setConferenceEventPackageEnabled isEnabled=" + z);
        this.mIsConferenceEventPackageEnabled = z;
    }

    public boolean isConferenceEventPackageEnabled() {
        return this.mIsConferenceEventPackageEnabled;
    }

    @VisibleForTesting
    public ImsCall.Listener getImsCallListener() {
        return this.mImsCallListener;
    }

    @VisibleForTesting
    public ArrayList<ImsPhoneConnection> getConnections() {
        return this.mConnections;
    }

    @VisibleForTesting
    public ImsPhoneConnection getPendingMO() {
        return this.mPendingMO;
    }

    public void setConfig(Config config) {
        this.mConfig = config;
    }

    private void handleConferenceFailed(ImsPhoneConnection imsPhoneConnection, ImsPhoneConnection imsPhoneConnection2) {
        if (imsPhoneConnection != null) {
            imsPhoneConnection.handleMergeComplete();
        }
        if (imsPhoneConnection2 != null) {
            imsPhoneConnection2.handleMergeComplete();
        }
        this.mPhone.notifySuppServiceFailed(PhoneInternalInterface.SuppService.CONFERENCE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isForegroundHigherPriority() {
        if (this.mBackgroundCall.getState().isAlive()) {
            return this.mForegroundCall.getFirstConnection().getCallPriority() > this.mBackgroundCall.getFirstConnection().getCallPriority();
        }
        return true;
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x003d  */
    /* JADX WARN: Removed duplicated region for block: B:16:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void initializeTerminalBasedCallWaiting() {
        boolean z;
        ImsManager imsManager = this.mImsManager;
        if (imsManager != null) {
            try {
                z = imsManager.isCapable(4L);
            } catch (ImsException e) {
                loge("initializeTerminalBasedCallWaiting : exception " + e);
            }
            logi("initializeTerminalBasedCallWaiting capable=" + z);
            this.mPhone.setTerminalBasedCallWaitingSupported(z);
            if (z) {
                return;
            }
            setTerminalBasedCallWaitingStatus(this.mPhone.getTerminalBasedCallWaitingState(false));
            return;
        }
        z = false;
        logi("initializeTerminalBasedCallWaiting capable=" + z);
        this.mPhone.setTerminalBasedCallWaitingSupported(z);
        if (z) {
        }
    }

    public void setTerminalBasedCallWaitingStatus(int i) {
        if (i == -1 || this.mImsManager == null) {
            return;
        }
        try {
            log("setTerminalBasedCallWaitingStatus state=" + i);
            ImsManager imsManager = this.mImsManager;
            boolean z = true;
            if (i != 1) {
                z = false;
            }
            imsManager.setTerminalBasedCallWaitingStatus(z);
        } catch (ImsException e) {
            loge("setTerminalBasedCallWaitingStatus : exception " + e);
        }
    }

    public void handleSrvccConnectionInfo(List<SrvccCall> list) {
        this.mPhone.getDefaultPhone().mCi.setSrvccCallInfo(convertToSrvccConnectionInfo(list), null);
    }

    @VisibleForTesting
    public SrvccConnection[] convertToSrvccConnectionInfo(List<SrvccCall> list) {
        if (this.mSrvccTypeSupported.isEmpty() || list == null || list.isEmpty()) {
            loge("convertToSrvccConnectionInfo empty list");
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (SrvccCall srvccCall : list) {
            if (isCallProfileSupported(srvccCall)) {
                addConnection(arrayList, srvccCall, findConnection(srvccCall.getCallId()));
            } else {
                logi("convertToSrvccConnectionInfo not supported state=" + srvccCall.getPreciseCallState());
            }
        }
        logi("convertToSrvccConnectionInfo size=" + arrayList.size());
        if (arrayList.isEmpty()) {
            return null;
        }
        return (SrvccConnection[]) arrayList.toArray(new SrvccConnection[0]);
    }

    public void handleSendAnbrQuery(int i, int i2, int i3) {
        log("handleSendAnbrQuery - mediaType=" + i);
        this.mPhone.getDefaultPhone().mCi.sendAnbrQuery(i, i2, i3, null);
    }

    public void triggerNotifyAnbr(int i, int i2, int i3) {
        ImsCall imsCall = this.mForegroundCall.getFirstConnection().getImsCall();
        if (imsCall != null) {
            log("triggerNotifyAnbr - mediaType=" + i);
            imsCall.callSessionNotifyAnbr(i, i2, i3);
        }
    }

    private boolean isCallProfileSupported(SrvccCall srvccCall) {
        if (srvccCall == null) {
            loge("isCallProfileSupported null profile");
            return false;
        }
        int preciseCallState = srvccCall.getPreciseCallState();
        if (preciseCallState != 9) {
            switch (preciseCallState) {
                case 1:
                    return this.mSrvccTypeSupported.contains(0);
                case 2:
                    return this.mSrvccTypeSupported.contains(3);
                case 3:
                    return this.mSrvccTypeSupported.contains(2);
                case 4:
                    return this.mSrvccTypeSupported.contains(1);
                case 5:
                    return this.mSrvccTypeSupported.contains(1);
                case 6:
                    return this.mSrvccTypeSupported.contains(1);
                default:
                    loge("isCallProfileSupported invalid state=" + srvccCall.getPreciseCallState());
                    return false;
            }
        }
        return this.mSrvccTypeSupported.contains(2);
    }

    private synchronized ImsPhoneConnection findConnection(String str) {
        ImsCallSession callSession;
        Iterator<ImsPhoneConnection> it = this.mConnections.iterator();
        while (it.hasNext()) {
            ImsPhoneConnection next = it.next();
            ImsCall imsCall = next.getImsCall();
            if (imsCall != null && (callSession = imsCall.getCallSession()) != null && TextUtils.equals(callSession.getCallId(), str)) {
                return next;
            }
        }
        return null;
    }

    private void addConnection(List<SrvccConnection> list, SrvccCall srvccCall, ImsPhoneConnection imsPhoneConnection) {
        if (list == null || srvccCall == null) {
            return;
        }
        int preciseCallState = srvccCall.getPreciseCallState();
        if (!isAlive(preciseCallState)) {
            Rlog.i("ImsPhoneCallTracker", "addConnection not alive, " + preciseCallState);
            return;
        }
        List<ConferenceParticipant> conferenceParticipants = getConferenceParticipants(imsPhoneConnection);
        if (conferenceParticipants != null) {
            for (ConferenceParticipant conferenceParticipant : conferenceParticipants) {
                if (conferenceParticipant.getState() == 6) {
                    Rlog.i("ImsPhoneCallTracker", "addConnection participant is disconnected");
                } else {
                    SrvccConnection srvccConnection = new SrvccConnection(conferenceParticipant, preciseCallState);
                    Rlog.i("ImsPhoneCallTracker", "addConnection " + srvccConnection);
                    list.add(srvccConnection);
                }
            }
            return;
        }
        SrvccConnection srvccConnection2 = new SrvccConnection(srvccCall.getImsCallProfile(), imsPhoneConnection, preciseCallState);
        Rlog.i("ImsPhoneCallTracker", "addConnection " + srvccConnection2);
        list.add(srvccConnection2);
    }

    private List<ConferenceParticipant> getConferenceParticipants(ImsPhoneConnection imsPhoneConnection) {
        ImsCall imsCall;
        List<ConferenceParticipant> conferenceParticipants;
        if (!this.mSrvccTypeSupported.contains(3) || (imsCall = imsPhoneConnection.getImsCall()) == null || (conferenceParticipants = imsCall.getConferenceParticipants()) == null || conferenceParticipants.isEmpty()) {
            return null;
        }
        return conferenceParticipants;
    }

    public void triggerImsDeregistration(int i) {
        ImsManager imsManager = this.mImsManager;
        if (imsManager == null) {
            return;
        }
        try {
            imsManager.triggerDeregistration(i);
        } catch (ImsException e) {
            loge("triggerImsDeregistration: exception " + e);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void updateImsRegistrationInfo() {
        boolean isCapable = this.mMmTelCapabilities.isCapable(1);
        boolean z = isCapable;
        if (this.mMmTelCapabilities.isCapable(2)) {
            z = isCapable | true;
        }
        int i = z;
        if (this.mMmTelCapabilities.isCapable(8)) {
            i = (z ? 1 : 0) | true;
        }
        this.mPhone.updateImsRegistrationInfo(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerImsTrafficSession(int i, int i2, int i3, IImsTrafficSessionCallback iImsTrafficSessionCallback) {
        this.mImsTrafficSessions.put(Integer.valueOf(i), new ImsTrafficSession(i2, i3, iImsTrafficSessionCallback));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterImsTrafficSession(int i) {
        this.mImsTrafficSessions.remove(Integer.valueOf(i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ImsTrafficSession getImsTrafficSession(int i) {
        return this.mImsTrafficSessions.get(Integer.valueOf(i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopAllImsTrafficTypes() {
        boolean isEmpty = this.mImsTrafficSessions.isEmpty();
        logi("stopAllImsTrafficTypes empty=" + isEmpty);
        if (isEmpty) {
            return;
        }
        this.mImsTrafficSessions.forEachKey(1L, new Consumer() { // from class: com.android.internal.telephony.imsphone.ImsPhoneCallTracker$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ImsPhoneCallTracker.this.lambda$stopAllImsTrafficTypes$3((Integer) obj);
            }
        });
        this.mImsTrafficSessions.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$stopAllImsTrafficTypes$3(Integer num) {
        this.mPhone.stopImsTraffic(num.intValue(), null);
    }
}
