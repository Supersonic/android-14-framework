package com.android.internal.telephony.data;

import android.content.Intent;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkAgentConfig;
import android.net.NetworkCapabilities;
import android.net.NetworkProvider;
import android.net.NetworkScore;
import android.net.ProxyInfo;
import android.net.RouteInfo;
import android.net.TelephonyNetworkSpecifier;
import android.net.Uri;
import android.net.vcn.VcnManager;
import android.net.vcn.VcnNetworkPolicyResult;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.AccessNetworkConstants;
import android.telephony.AnomalyReporter;
import android.telephony.DataFailCause;
import android.telephony.DataSpecificRegistrationInfo;
import android.telephony.LinkCapacityEstimate;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.PcoData;
import android.telephony.PreciseDataConnectionState;
import android.telephony.ServiceState;
import android.telephony.SubscriptionPlan;
import android.telephony.TelephonyDisplayInfo;
import android.telephony.TelephonyManager;
import android.telephony.data.ApnSetting;
import android.telephony.data.DataCallResponse;
import android.telephony.data.DataProfile;
import android.telephony.data.DataServiceCallback;
import android.telephony.data.NetworkSliceInfo;
import android.telephony.data.QosBearerSession;
import android.telephony.data.TrafficDescriptor;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.telephony.AndroidUtilIndentingPrintWriter;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.IState;
import com.android.internal.telephony.LocalLog;
import com.android.internal.telephony.NetworkTypeController$$ExternalSyntheticLambda0;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.RIL;
import com.android.internal.telephony.State;
import com.android.internal.telephony.StateMachine;
import com.android.internal.telephony.data.DataConfigManager;
import com.android.internal.telephony.data.DataEvaluation;
import com.android.internal.telephony.data.DataNetwork;
import com.android.internal.telephony.data.DataNetworkController;
import com.android.internal.telephony.data.DataRetryManager;
import com.android.internal.telephony.data.LinkBandwidthEstimator;
import com.android.internal.telephony.data.TelephonyNetworkAgent;
import com.android.internal.telephony.metrics.DataCallSessionStats;
import com.android.internal.telephony.metrics.TelephonyMetrics;
import com.android.internal.telephony.util.LinkPropertiesUtils;
import com.android.internal.telephony.util.NetUtils;
import com.android.internal.telephony.util.NetworkCapabilitiesUtils;
import com.android.internal.util.ArrayUtils;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/* loaded from: classes.dex */
public class DataNetwork extends StateMachine {
    public static final int BANDWIDTH_SOURCE_BANDWIDTH_ESTIMATOR = 3;
    public static final int BANDWIDTH_SOURCE_CARRIER_CONFIG = 2;
    public static final int BANDWIDTH_SOURCE_MODEM = 1;
    public static final int BANDWIDTH_SOURCE_UNKNOWN = 0;
    private static final List<Integer> MUTABLE_CAPABILITIES = List.of((Object[]) new Integer[]{14, 16, 17, 18, 19, 20, 21, 24, 25, 28, 32, 11, 33});
    public static final int TEAR_DOWN_REASON_AIRPLANE_MODE_ON = 3;
    public static final int TEAR_DOWN_REASON_CDMA_EMERGENCY_CALLBACK_MODE = 22;
    public static final int TEAR_DOWN_REASON_CONCURRENT_VOICE_DATA_NOT_ALLOWED = 8;
    public static final int TEAR_DOWN_REASON_CONNECTIVITY_SERVICE_UNWANTED = 1;
    public static final int TEAR_DOWN_REASON_DATA_CONFIG_NOT_READY = 19;
    public static final int TEAR_DOWN_REASON_DATA_DISABLED = 4;
    public static final int TEAR_DOWN_REASON_DATA_PROFILE_INVALID = 25;
    public static final int TEAR_DOWN_REASON_DATA_PROFILE_NOT_PREFERRED = 26;
    public static final int TEAR_DOWN_REASON_DATA_SERVICE_NOT_READY = 10;
    public static final int TEAR_DOWN_REASON_DATA_STALL = 12;
    public static final int TEAR_DOWN_REASON_DATA_THROTTLED = 24;
    public static final int TEAR_DOWN_REASON_DEFAULT_DATA_UNSELECTED = 17;
    public static final int TEAR_DOWN_REASON_HANDOVER_FAILED = 13;
    public static final int TEAR_DOWN_REASON_HANDOVER_NOT_ALLOWED = 14;
    public static final int TEAR_DOWN_REASON_ILLEGAL_STATE = 28;
    public static final int TEAR_DOWN_REASON_NONE = 0;
    public static final int TEAR_DOWN_REASON_NOT_ALLOWED_BY_POLICY = 27;
    public static final int TEAR_DOWN_REASON_NOT_IN_SERVICE = 18;
    public static final int TEAR_DOWN_REASON_NO_LIVE_REQUEST = 5;
    public static final int TEAR_DOWN_REASON_NO_SUITABLE_DATA_PROFILE = 21;
    public static final int TEAR_DOWN_REASON_ONLY_ALLOWED_SINGLE_NETWORK = 29;
    public static final int TEAR_DOWN_REASON_PENDING_TEAR_DOWN_ALL = 20;
    public static final int TEAR_DOWN_REASON_POWER_OFF_BY_CARRIER = 11;
    public static final int TEAR_DOWN_REASON_PREFERRED_DATA_SWITCHED = 30;
    public static final int TEAR_DOWN_REASON_RAT_NOT_ALLOWED = 6;
    public static final int TEAR_DOWN_REASON_RETRY_SCHEDULED = 23;
    public static final int TEAR_DOWN_REASON_ROAMING_DISABLED = 7;
    public static final int TEAR_DOWN_REASON_SIM_REMOVAL = 2;
    public static final int TEAR_DOWN_REASON_VCN_REQUESTED = 15;
    public static final int TEAR_DOWN_REASON_VOPS_NOT_SUPPORTED = 16;
    private final AccessNetworksManager mAccessNetworksManager;
    private int[] mAdministratorUids;
    private final DataNetworkController.NetworkRequestList mAttachedNetworkRequestList;
    private TelephonyManager.CarrierPrivilegesCallback mCarrierPrivilegesCallback;
    private int mCarrierServicePackageUid;
    private final SparseIntArray mCid;
    private boolean mCongested;
    private final ConnectedState mConnectedState;
    private final ConnectingState mConnectingState;
    private DataEvaluation.DataAllowedReason mDataAllowedReason;
    private DataCallResponse mDataCallResponse;
    private final DataCallSessionStats mDataCallSessionStats;
    private final DataConfigManager mDataConfigManager;
    private DataConfigManager.DataConfigManagerCallback mDataConfigManagerCallback;
    private final DataNetworkCallback mDataNetworkCallback;
    private final DataNetworkController mDataNetworkController;
    private final DataNetworkController.DataNetworkControllerCallback mDataNetworkControllerCallback;
    private DataProfile mDataProfile;
    private final SparseArray<DataServiceManager> mDataServiceManagers;
    private final DefaultState mDefaultState;
    private final DisconnectedState mDisconnectedState;
    private final DisconnectingState mDisconnectingState;
    private boolean mEverConnected;
    private int mFailCause;
    private DataProfile mHandoverDataProfile;
    private final HandoverState mHandoverState;
    private int mInitialNetworkAgentId;
    private boolean mInvokedDataDeactivation;
    private KeepaliveTracker mKeepaliveTracker;
    private int mLastKnownDataNetworkType;
    private LinkBandwidthEstimator.LinkBandwidthEstimatorCallback mLinkBandwidthEstimatorCallback;
    private LinkProperties mLinkProperties;
    private int mLinkStatus;
    private final LocalLog mLocalLog;
    private String mLogTag;
    private TelephonyNetworkAgent mNetworkAgent;
    private NetworkBandwidth mNetworkBandwidth;
    private NetworkCapabilities mNetworkCapabilities;
    private int mNetworkScore;
    private NetworkSliceInfo mNetworkSliceInfo;
    private final Map<Integer, Map<Integer, PcoData>> mPcoData;
    private int mPduSessionId;
    private final Phone mPhone;
    private final List<QosBearerSession> mQosBearerSessions;
    private QosCallbackTracker mQosCallbackTracker;
    private long mRetryDelayMillis;
    private final CommandsInterface mRil;
    private final int mSubId;
    private boolean mSuspended;
    private String mTcpBufferSizes;
    private int mTearDownReason;
    private TelephonyDisplayInfo mTelephonyDisplayInfo;
    private boolean mTempNotMetered;
    private boolean mTempNotMeteredSupported;
    private final List<TrafficDescriptor> mTrafficDescriptors;
    private int mTransport;
    private final VcnManager mVcnManager;
    private VcnManager.VcnNetworkPolicyChangeListener mVcnPolicyChangeListener;

    @Override // com.android.internal.telephony.StateMachine
    protected void logv(String str) {
    }

    /* loaded from: classes.dex */
    public static class NetworkBandwidth {
        public final int downlinkBandwidthKbps;
        public final int uplinkBandwidthKbps;

        public NetworkBandwidth(int i, int i2) {
            this.downlinkBandwidthKbps = i;
            this.uplinkBandwidthKbps = i2;
        }

        public String toString() {
            return String.format("NetworkBandwidth=[downlink=%d, uplink=%d]", Integer.valueOf(this.downlinkBandwidthKbps), Integer.valueOf(this.uplinkBandwidthKbps));
        }
    }

    /* loaded from: classes.dex */
    public static abstract class DataNetworkCallback extends DataCallback {
        public abstract void onAttachFailed(DataNetwork dataNetwork, DataNetworkController.NetworkRequestList networkRequestList);

        public abstract void onConnected(DataNetwork dataNetwork);

        public abstract void onDisconnected(DataNetwork dataNetwork, int i, int i2);

        public abstract void onHandoverFailed(DataNetwork dataNetwork, int i, long j, int i2);

        public abstract void onHandoverSucceeded(DataNetwork dataNetwork);

        public abstract void onLinkStatusChanged(DataNetwork dataNetwork, int i);

        public abstract void onNetworkCapabilitiesChanged(DataNetwork dataNetwork);

        public abstract void onPcoDataChanged(DataNetwork dataNetwork);

        public abstract void onSetupDataFailed(DataNetwork dataNetwork, DataNetworkController.NetworkRequestList networkRequestList, int i, long j);

        public abstract void onSuspendedStateChanged(DataNetwork dataNetwork, boolean z);

        public abstract void onTrackNetworkUnwanted(DataNetwork dataNetwork);

        public abstract void onValidationStatusChanged(DataNetwork dataNetwork, int i, Uri uri);

        public DataNetworkCallback(Executor executor) {
            super(executor);
        }
    }

    public DataNetwork(Phone phone, Looper looper, SparseArray<DataServiceManager> sparseArray, DataProfile dataProfile, DataNetworkController.NetworkRequestList networkRequestList, int i, DataEvaluation.DataAllowedReason dataAllowedReason, DataNetworkCallback dataNetworkCallback) {
        super("DataNetwork", looper);
        this.mDefaultState = new DefaultState();
        this.mConnectingState = new ConnectingState();
        this.mConnectedState = new ConnectedState();
        this.mHandoverState = new HandoverState();
        this.mDisconnectingState = new DisconnectingState();
        this.mDisconnectedState = new DisconnectedState();
        this.mInvokedDataDeactivation = false;
        this.mEverConnected = false;
        this.mLocalLog = new LocalLog(128);
        this.mCid = new SparseIntArray(2);
        this.mPduSessionId = 0;
        ArrayList arrayList = new ArrayList();
        this.mTrafficDescriptors = arrayList;
        this.mLinkStatus = -1;
        this.mNetworkBandwidth = new NetworkBandwidth(14, 14);
        this.mTempNotMeteredSupported = false;
        this.mTempNotMetered = false;
        this.mCongested = false;
        DataNetworkController.NetworkRequestList networkRequestList2 = new DataNetworkController.NetworkRequestList();
        this.mAttachedNetworkRequestList = networkRequestList2;
        this.mDataCallResponse = null;
        this.mFailCause = 0;
        this.mTearDownReason = 0;
        this.mRetryDelayMillis = -1L;
        this.mSuspended = false;
        this.mPcoData = new ArrayMap();
        this.mQosBearerSessions = new ArrayList();
        this.mAdministratorUids = new int[0];
        this.mCarrierServicePackageUid = -1;
        initializeStateMachine();
        this.mPhone = phone;
        this.mSubId = phone.getSubId();
        this.mRil = phone.mCi;
        this.mLinkProperties = new LinkProperties();
        this.mDataServiceManagers = sparseArray;
        AccessNetworksManager accessNetworksManager = phone.getAccessNetworksManager();
        this.mAccessNetworksManager = accessNetworksManager;
        this.mVcnManager = (VcnManager) phone.getContext().getSystemService(VcnManager.class);
        DataNetworkController dataNetworkController = phone.getDataNetworkController();
        this.mDataNetworkController = dataNetworkController;
        Handler handler = getHandler();
        Objects.requireNonNull(handler);
        DataNetworkController.DataNetworkControllerCallback dataNetworkControllerCallback = new DataNetworkController.DataNetworkControllerCallback(new NetworkTypeController$$ExternalSyntheticLambda0(handler)) { // from class: com.android.internal.telephony.data.DataNetwork.1
            @Override // com.android.internal.telephony.data.DataNetworkController.DataNetworkControllerCallback
            public void onSubscriptionPlanOverride() {
                DataNetwork.this.sendMessage(16);
            }
        };
        this.mDataNetworkControllerCallback = dataNetworkControllerCallback;
        dataNetworkController.registerDataNetworkControllerCallback(dataNetworkControllerCallback);
        this.mDataConfigManager = dataNetworkController.getDataConfigManager();
        this.mDataCallSessionStats = new DataCallSessionStats(phone);
        this.mDataNetworkCallback = dataNetworkCallback;
        this.mDataProfile = dataProfile;
        if (dataProfile.getTrafficDescriptor() != null) {
            arrayList.add(dataProfile.getTrafficDescriptor());
        }
        this.mTransport = i;
        this.mLastKnownDataNetworkType = getDataNetworkType();
        this.mDataAllowedReason = dataAllowedReason;
        dataProfile.setLastSetupTimestamp(SystemClock.elapsedRealtime());
        networkRequestList2.addAll(networkRequestList);
        for (int i2 : accessNetworksManager.getAvailableTransports()) {
            this.mCid.put(i2, -1);
        }
        TelephonyDisplayInfo telephonyDisplayInfo = this.mPhone.getDisplayInfoController().getTelephonyDisplayInfo();
        this.mTelephonyDisplayInfo = telephonyDisplayInfo;
        this.mTcpBufferSizes = this.mDataConfigManager.getTcpConfigString(telephonyDisplayInfo);
        Iterator<TelephonyNetworkRequest> it = networkRequestList.iterator();
        while (it.hasNext()) {
            TelephonyNetworkRequest next = it.next();
            next.setAttachedNetwork(this);
            next.setState(1);
        }
        updateNetworkCapabilities();
    }

    private void initializeStateMachine() {
        addState(this.mDefaultState);
        addState(this.mConnectingState, this.mDefaultState);
        addState(this.mConnectedState, this.mDefaultState);
        addState(this.mHandoverState, this.mDefaultState);
        addState(this.mDisconnectingState, this.mDefaultState);
        addState(this.mDisconnectedState, this.mDefaultState);
        setInitialState(this.mConnectingState);
        start();
    }

    private boolean shouldSkip464Xlat() {
        if (this.mDataProfile.getApnSetting() != null) {
            int skip464Xlat = this.mDataProfile.getApnSetting().getSkip464Xlat();
            if (skip464Xlat == 0) {
                return false;
            }
            if (skip464Xlat == 1) {
                return true;
            }
        }
        NetworkCapabilities networkCapabilities = getNetworkCapabilities();
        return networkCapabilities.hasCapability(4) && !networkCapabilities.hasCapability(12);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public TelephonyNetworkAgent createNetworkAgent() {
        NetworkAgentConfig.Builder builder = new NetworkAgentConfig.Builder();
        builder.setLegacyType(0);
        builder.setLegacyTypeName("MOBILE");
        int dataNetworkType = getDataNetworkType();
        builder.setLegacySubType(dataNetworkType);
        builder.setLegacySubTypeName(TelephonyManager.getNetworkTypeName(dataNetworkType));
        if (this.mDataProfile.getApnSetting() != null) {
            builder.setLegacyExtraInfo(this.mDataProfile.getApnSetting().getApnName());
        }
        if (this.mPhone.getCarrierSignalAgent().hasRegisteredReceivers("android.telephony.action.CARRIER_SIGNAL_REDIRECTED")) {
            builder.setProvisioningNotificationEnabled(false);
        }
        String subscriberId = this.mPhone.getSubscriberId();
        if (!TextUtils.isEmpty(subscriberId)) {
            builder.setSubscriberId(subscriberId);
        }
        if (shouldSkip464Xlat()) {
            builder.setNat64DetectionEnabled(false);
        }
        TelephonyNetworkFactory networkFactory = PhoneFactory.getNetworkFactory(this.mPhone.getPhoneId());
        NetworkProvider provider = networkFactory == null ? null : networkFactory.getProvider();
        this.mNetworkScore = getNetworkScore();
        Phone phone = this.mPhone;
        Looper looper = getHandler().getLooper();
        NetworkScore build = new NetworkScore.Builder().setLegacyInt(this.mNetworkScore).build();
        NetworkAgentConfig build2 = builder.build();
        Handler handler = getHandler();
        Objects.requireNonNull(handler);
        return new TelephonyNetworkAgent(phone, looper, this, build, build2, provider, new C01052(new NetworkTypeController$$ExternalSyntheticLambda0(handler)));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.telephony.data.DataNetwork$2 */
    /* loaded from: classes.dex */
    public class C01052 extends TelephonyNetworkAgent.TelephonyNetworkAgentCallback {
        C01052(Executor executor) {
            super(executor);
        }

        @Override // com.android.internal.telephony.data.TelephonyNetworkAgent.TelephonyNetworkAgentCallback
        public void onValidationStatus(final int i, final Uri uri) {
            DataNetwork.this.mDataNetworkCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetwork$2$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DataNetwork.C01052.this.lambda$onValidationStatus$0(i, uri);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onValidationStatus$0(int i, Uri uri) {
            DataNetwork.this.mDataNetworkCallback.onValidationStatusChanged(DataNetwork.this, i, uri);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class DefaultState extends State {
        private DefaultState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void enter() {
            DataNetwork.this.logv("Registering all events.");
            DataNetwork dataNetwork = DataNetwork.this;
            Handler handler = dataNetwork.getHandler();
            Objects.requireNonNull(handler);
            dataNetwork.mDataConfigManagerCallback = new DataConfigManager.DataConfigManagerCallback(new NetworkTypeController$$ExternalSyntheticLambda0(handler)) { // from class: com.android.internal.telephony.data.DataNetwork.DefaultState.1
                @Override // com.android.internal.telephony.data.DataConfigManager.DataConfigManagerCallback
                public void onCarrierConfigChanged() {
                    DataNetwork.this.sendMessage(1);
                }
            };
            DataNetwork.this.mRil.registerForPcoData(DataNetwork.this.getHandler(), 17, null);
            DataNetwork.this.mDataConfigManager.registerCallback(DataNetwork.this.mDataConfigManagerCallback);
            DataNetwork.this.mPhone.getDisplayInfoController().registerForTelephonyDisplayInfoChanged(DataNetwork.this.getHandler(), 13, null);
            DataNetwork.this.mPhone.getServiceStateTracker().registerForServiceStateChanged(DataNetwork.this.getHandler(), 9, null);
            for (int i : DataNetwork.this.mAccessNetworksManager.getAvailableTransports()) {
                ((DataServiceManager) DataNetwork.this.mDataServiceManagers.get(i)).registerForDataCallListChanged(DataNetwork.this.getHandler(), 8);
            }
            DataNetwork.this.mCarrierPrivilegesCallback = new TelephonyManager.CarrierPrivilegesCallback() { // from class: com.android.internal.telephony.data.DataNetwork$DefaultState$$ExternalSyntheticLambda0
                public final void onCarrierPrivilegesChanged(Set set, Set set2) {
                    DataNetwork.DefaultState.this.lambda$enter$1(set, set2);
                }
            };
            TelephonyManager telephonyManager = (TelephonyManager) DataNetwork.this.mPhone.getContext().getSystemService(TelephonyManager.class);
            if (telephonyManager != null) {
                int phoneId = DataNetwork.this.mPhone.getPhoneId();
                Handler handler2 = DataNetwork.this.getHandler();
                Objects.requireNonNull(handler2);
                telephonyManager.registerCarrierPrivilegesCallback(phoneId, new NetworkTypeController$$ExternalSyntheticLambda0(handler2), DataNetwork.this.mCarrierPrivilegesCallback);
            }
            DataNetwork.this.mPhone.getServiceStateTracker().registerForCssIndicatorChanged(DataNetwork.this.getHandler(), 24, null);
            DataNetwork.this.mPhone.getCallTracker().registerForVoiceCallStarted(DataNetwork.this.getHandler(), 22, null);
            DataNetwork.this.mPhone.getCallTracker().registerForVoiceCallEnded(DataNetwork.this.getHandler(), 23, null);
            if (DataNetwork.this.mPhone.getImsPhone() != null) {
                DataNetwork.this.mPhone.getImsPhone().getCallTracker().registerForVoiceCallStarted(DataNetwork.this.getHandler(), 22, null);
                DataNetwork.this.mPhone.getImsPhone().getCallTracker().registerForVoiceCallEnded(DataNetwork.this.getHandler(), 23, null);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$enter$1(Set set, Set set2) {
            DataNetwork dataNetwork = DataNetwork.this;
            dataNetwork.log("onCarrierPrivilegesChanged, Uids=" + set2.toString());
            Message obtainMessage = DataNetwork.this.obtainMessage(18);
            AsyncResult.forMessage(obtainMessage, set2.stream().mapToInt(new ToIntFunction() { // from class: com.android.internal.telephony.data.DataNetwork$DefaultState$$ExternalSyntheticLambda1
                @Override // java.util.function.ToIntFunction
                public final int applyAsInt(Object obj) {
                    int intValue;
                    intValue = ((Integer) obj).intValue();
                    return intValue;
                }
            }).toArray(), (Throwable) null);
            DataNetwork.this.sendMessage(obtainMessage);
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void exit() {
            DataNetwork.this.logv("Unregistering all events.");
            if (DataNetwork.this.mPhone.getImsPhone() != null) {
                DataNetwork.this.mPhone.getImsPhone().getCallTracker().unregisterForVoiceCallStarted(DataNetwork.this.getHandler());
                DataNetwork.this.mPhone.getImsPhone().getCallTracker().unregisterForVoiceCallEnded(DataNetwork.this.getHandler());
            }
            DataNetwork.this.mPhone.getCallTracker().unregisterForVoiceCallStarted(DataNetwork.this.getHandler());
            DataNetwork.this.mPhone.getCallTracker().unregisterForVoiceCallEnded(DataNetwork.this.getHandler());
            DataNetwork.this.mPhone.getServiceStateTracker().unregisterForCssIndicatorChanged(DataNetwork.this.getHandler());
            TelephonyManager telephonyManager = (TelephonyManager) DataNetwork.this.mPhone.getContext().getSystemService(TelephonyManager.class);
            if (telephonyManager != null && DataNetwork.this.mCarrierPrivilegesCallback != null) {
                telephonyManager.unregisterCarrierPrivilegesCallback(DataNetwork.this.mCarrierPrivilegesCallback);
            }
            for (int i : DataNetwork.this.mAccessNetworksManager.getAvailableTransports()) {
                ((DataServiceManager) DataNetwork.this.mDataServiceManagers.get(i)).unregisterForDataCallListChanged(DataNetwork.this.getHandler());
            }
            DataNetwork.this.mPhone.getServiceStateTracker().unregisterForServiceStateChanged(DataNetwork.this.getHandler());
            DataNetwork.this.mPhone.getDisplayInfoController().unregisterForTelephonyDisplayInfoChanged(DataNetwork.this.getHandler());
            DataNetwork.this.mRil.unregisterForPcoData(DataNetwork.this.getHandler());
            DataNetwork.this.mDataConfigManager.unregisterCallback(DataNetwork.this.mDataConfigManagerCallback);
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case 1:
                    DataNetwork.this.onCarrierConfigUpdated();
                    return true;
                case 2:
                    DataNetwork.this.onAttachNetworkRequests((DataNetworkController.NetworkRequestList) message.obj);
                    DataNetwork.this.updateNetworkScore();
                    return true;
                case 3:
                    DataNetwork.this.onDetachNetworkRequest((TelephonyNetworkRequest) message.obj);
                    DataNetwork.this.updateNetworkScore();
                    return true;
                case 4:
                    DataNetwork.this.mFailCause = 65537;
                    DataNetwork dataNetwork = DataNetwork.this;
                    dataNetwork.loge(DataNetwork.eventToString(message.what) + ": transition to disconnected state");
                    DataNetwork dataNetwork2 = DataNetwork.this;
                    dataNetwork2.transitionTo(dataNetwork2.mDisconnectedState);
                    return true;
                case 5:
                case 6:
                case 12:
                case 14:
                case 15:
                case 16:
                case 19:
                default:
                    DataNetwork dataNetwork3 = DataNetwork.this;
                    dataNetwork3.loge("Unhandled event " + DataNetwork.eventToString(message.what));
                    return true;
                case 7:
                case 11:
                case 13:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                    DataNetwork dataNetwork4 = DataNetwork.this;
                    dataNetwork4.log("Ignored " + DataNetwork.eventToString(message.what));
                    return true;
                case 8:
                    AsyncResult asyncResult = (AsyncResult) message.obj;
                    DataNetwork.this.onDataStateChanged(((Integer) asyncResult.userObj).intValue(), (List) asyncResult.result);
                    return true;
                case 9:
                    int dataNetworkType = DataNetwork.this.getDataNetworkType();
                    DataNetwork.this.mDataCallSessionStats.onDrsOrRatChanged(dataNetworkType);
                    if (dataNetworkType != 0) {
                        DataNetwork.this.mLastKnownDataNetworkType = dataNetworkType;
                    }
                    DataNetwork.this.updateSuspendState();
                    DataNetwork.this.updateNetworkCapabilities();
                    return true;
                case 10:
                    Iterator<TelephonyNetworkRequest> it = DataNetwork.this.mAttachedNetworkRequestList.iterator();
                    while (it.hasNext()) {
                        TelephonyNetworkRequest next = it.next();
                        next.setState(0);
                        next.setAttachedNetwork(null);
                    }
                    DataNetwork.this.log("All network requests detached.");
                    DataNetwork.this.mAttachedNetworkRequestList.clear();
                    return true;
                case 17:
                    DataNetwork.this.onPcoDataReceived((PcoData) ((AsyncResult) message.obj).result);
                    return true;
                case 18:
                    int[] iArr = (int[]) ((AsyncResult) message.obj).result;
                    DataNetwork.this.mAdministratorUids = Arrays.copyOf(iArr, iArr.length);
                    DataNetwork.this.updateNetworkCapabilities();
                    return true;
                case 25:
                case 26:
                    Object obj = message.obj;
                    if (obj != null) {
                        ((DataRetryManager.DataHandoverRetryEntry) obj).setState(4);
                    }
                    DataNetwork dataNetwork5 = DataNetwork.this;
                    dataNetwork5.log("Ignore handover to " + AccessNetworkConstants.transportTypeToString(message.arg1) + " request.");
                    return true;
                case 27:
                    DataNetwork.this.log("Notified handover cancelled.");
                    return true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class ConnectingState extends State {
        private ConnectingState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void enter() {
            DataNetwork dataNetwork = DataNetwork.this;
            dataNetwork.sendMessageDelayed(20, dataNetwork.mDataConfigManager.getAnomalyNetworkConnectingTimeoutMs());
            DataNetwork dataNetwork2 = DataNetwork.this;
            dataNetwork2.mNetworkAgent = dataNetwork2.createNetworkAgent();
            DataNetwork dataNetwork3 = DataNetwork.this;
            dataNetwork3.mInitialNetworkAgentId = dataNetwork3.mNetworkAgent.getId();
            DataNetwork dataNetwork4 = DataNetwork.this;
            StringBuilder sb = new StringBuilder();
            sb.append("DN-");
            sb.append(DataNetwork.this.mInitialNetworkAgentId);
            sb.append("-");
            sb.append(DataNetwork.this.mTransport == 1 ? "C" : "I");
            dataNetwork4.mLogTag = sb.toString();
            DataNetwork dataNetwork5 = DataNetwork.this;
            dataNetwork5.mCarrierServicePackageUid = dataNetwork5.mPhone.getCarrierPrivilegesTracker().getCarrierServicePackageUid();
            DataNetwork.this.notifyPreciseDataConnectionState();
            if (DataNetwork.this.mTransport == 2) {
                DataNetwork.this.allocatePduSessionId();
            } else {
                DataNetwork.this.setupData();
            }
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void exit() {
            DataNetwork.this.removeMessages(20);
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            DataNetwork dataNetwork = DataNetwork.this;
            dataNetwork.logv("event=" + DataNetwork.eventToString(message.what));
            int i = message.what;
            if (i == 5) {
                AsyncResult asyncResult = (AsyncResult) message.obj;
                if (asyncResult.exception == null) {
                    DataNetwork.this.mPduSessionId = ((Integer) asyncResult.result).intValue();
                    DataNetwork dataNetwork2 = DataNetwork.this;
                    dataNetwork2.log("Set PDU session id to " + DataNetwork.this.mPduSessionId);
                } else {
                    DataNetwork dataNetwork3 = DataNetwork.this;
                    dataNetwork3.loge("Failed to allocate PDU session id. e=" + asyncResult.exception);
                }
                DataNetwork.this.setupData();
                return true;
            } else if (i == 6) {
                DataNetwork.this.onSetupResponse(message.arg1, message.getData().getParcelable("data_call_response"));
                return true;
            } else {
                if (i != 7) {
                    if (i == 20) {
                        DataNetwork dataNetwork4 = DataNetwork.this;
                        dataNetwork4.reportAnomaly("Data network stuck in connecting state for " + TimeUnit.MILLISECONDS.toSeconds(DataNetwork.this.mDataConfigManager.getAnomalyNetworkConnectingTimeoutMs()) + " seconds.", "58c56403-7ea7-4e56-a0c7-e467114d09b8");
                        DataNetwork.this.mRetryDelayMillis = -1L;
                        DataNetwork.this.mFailCause = 65547;
                        DataNetwork dataNetwork5 = DataNetwork.this;
                        dataNetwork5.transitionTo(dataNetwork5.mDisconnectedState);
                        return true;
                    } else if (i != 21 && i != 25) {
                        return false;
                    }
                }
                DataNetwork dataNetwork6 = DataNetwork.this;
                dataNetwork6.log("Defer message " + DataNetwork.eventToString(message.what));
                DataNetwork.this.deferMessage(message);
                return true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class ConnectedState extends State {
        private ConnectedState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void enter() {
            if (!DataNetwork.this.mEverConnected) {
                DataNetwork.this.log("network connected.");
                DataNetwork.this.mEverConnected = true;
                DataNetwork.this.mNetworkAgent.markConnected();
                DataNetwork.this.mDataNetworkCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetwork$ConnectedState$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        DataNetwork.ConnectedState.this.lambda$enter$0();
                    }
                });
                DataNetwork.this.mQosCallbackTracker = new QosCallbackTracker(DataNetwork.this.mNetworkAgent, DataNetwork.this.mPhone);
                DataNetwork.this.mQosCallbackTracker.updateSessions(DataNetwork.this.mQosBearerSessions);
                DataNetwork dataNetwork = DataNetwork.this;
                Phone phone = DataNetwork.this.mPhone;
                Looper looper = DataNetwork.this.getHandler().getLooper();
                DataNetwork dataNetwork2 = DataNetwork.this;
                dataNetwork.mKeepaliveTracker = new KeepaliveTracker(phone, looper, dataNetwork2, dataNetwork2.mNetworkAgent);
                if (DataNetwork.this.mTransport == 1) {
                    DataNetwork.this.registerForWwanEvents();
                }
                if (DataNetwork.this.mVcnManager != null) {
                    DataNetwork.this.mVcnPolicyChangeListener = new VcnManager.VcnNetworkPolicyChangeListener() { // from class: com.android.internal.telephony.data.DataNetwork$ConnectedState$$ExternalSyntheticLambda1
                        public final void onPolicyChanged() {
                            DataNetwork.ConnectedState.this.lambda$enter$1();
                        }
                    };
                    VcnManager vcnManager = DataNetwork.this.mVcnManager;
                    Handler handler = DataNetwork.this.getHandler();
                    Objects.requireNonNull(handler);
                    vcnManager.addVcnNetworkPolicyChangeListener(new NetworkTypeController$$ExternalSyntheticLambda0(handler), DataNetwork.this.mVcnPolicyChangeListener);
                }
            }
            ((Map) DataNetwork.this.mPcoData.getOrDefault(Integer.valueOf(DataNetwork.this.mCid.get(DataNetwork.this.mTransport)), Collections.emptyMap())).forEach(new BiConsumer() { // from class: com.android.internal.telephony.data.DataNetwork$ConnectedState$$ExternalSyntheticLambda2
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    DataNetwork.ConnectedState.this.lambda$enter$2((Integer) obj, (PcoData) obj2);
                }
            });
            DataNetwork.this.notifyPreciseDataConnectionState();
            DataNetwork.this.updateSuspendState();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$enter$0() {
            DataNetwork.this.mDataNetworkCallback.onConnected(DataNetwork.this);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$enter$1() {
            DataNetwork.this.log("VCN policy changed.");
            if (DataNetwork.this.mVcnManager.applyVcnNetworkPolicy(DataNetwork.this.mNetworkCapabilities, DataNetwork.this.mLinkProperties).isTeardownRequested()) {
                DataNetwork.this.lambda$tearDownWhenConditionMet$9(15);
            } else {
                DataNetwork.this.updateNetworkCapabilities();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$enter$2(Integer num, PcoData pcoData) {
            DataNetwork.this.onPcoDataChanged(pcoData);
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            DataNetwork dataNetwork = DataNetwork.this;
            dataNetwork.logv("event=" + DataNetwork.eventToString(message.what));
            int i = message.what;
            if (i == 7) {
                if (DataNetwork.this.mInvokedDataDeactivation) {
                    DataNetwork.this.log("Ignore tear down request because network is being torn down.");
                    return true;
                }
                int i2 = message.arg1;
                DataNetwork.this.removeMessages(7);
                DataNetwork.this.removeDeferredMessages(7);
                DataNetwork dataNetwork2 = DataNetwork.this;
                dataNetwork2.transitionTo(dataNetwork2.mDisconnectingState);
                DataNetwork.this.onTearDown(i2);
                return true;
            } else if (i == 11) {
                AsyncResult asyncResult = (AsyncResult) message.obj;
                if (asyncResult.exception != null) {
                    DataNetwork dataNetwork3 = DataNetwork.this;
                    dataNetwork3.log("EVENT_BANDWIDTH_ESTIMATE_FROM_MODEM_CHANGED: error ignoring, e=" + asyncResult.exception);
                    return true;
                }
                DataNetwork.this.onBandwidthUpdatedFromModem((List) asyncResult.result);
                return true;
            } else if (i == 13) {
                DataNetwork.this.onDisplayInfoChanged();
                return true;
            } else if (i == 16) {
                DataNetwork.this.updateMeteredAndCongested();
                return true;
            } else if (i == 19) {
                DataNetwork.this.onDeactivateResponse(message.arg1);
                return true;
            } else {
                switch (i) {
                    case 21:
                        DataNetwork dataNetwork4 = DataNetwork.this;
                        dataNetwork4.transitionTo(dataNetwork4.mDisconnectingState);
                        DataNetwork.this.sendMessageDelayed(7, message.arg1, message.arg2);
                        return true;
                    case 22:
                    case 23:
                    case 24:
                        DataNetwork.this.updateSuspendState();
                        DataNetwork.this.updateNetworkCapabilities();
                        return true;
                    case 25:
                        DataNetwork dataNetwork5 = DataNetwork.this;
                        dataNetwork5.log("Notifying source transport " + AccessNetworkConstants.transportTypeToString(DataNetwork.this.mTransport) + " that handover is about to start.");
                        ((DataServiceManager) DataNetwork.this.mDataServiceManagers.get(DataNetwork.this.mTransport)).startHandover(DataNetwork.this.mCid.get(DataNetwork.this.mTransport), DataNetwork.this.obtainMessage(26, 0, message.arg2, message.obj));
                        DataNetwork dataNetwork6 = DataNetwork.this;
                        dataNetwork6.transitionTo(dataNetwork6.mHandoverState);
                        return true;
                    default:
                        return false;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class HandoverState extends State {
        private HandoverState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void enter() {
            DataNetwork dataNetwork = DataNetwork.this;
            dataNetwork.sendMessageDelayed(20, dataNetwork.mDataConfigManager.getNetworkHandoverTimeoutMs());
            DataNetwork.this.notifyPreciseDataConnectionState();
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void exit() {
            DataNetwork.this.removeMessages(20);
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            DataNetwork dataNetwork = DataNetwork.this;
            dataNetwork.logv("event=" + DataNetwork.eventToString(message.what));
            int i = message.what;
            if (i != 7) {
                if (i == 8) {
                    AsyncResult asyncResult = (AsyncResult) message.obj;
                    int intValue = ((Integer) asyncResult.userObj).intValue();
                    List list = (List) asyncResult.result;
                    if (intValue != DataNetwork.this.mTransport) {
                        DataNetwork dataNetwork2 = DataNetwork.this;
                        dataNetwork2.log("Dropped unrelated " + AccessNetworkConstants.transportTypeToString(intValue) + " data call list changed event. " + list);
                        return true;
                    }
                    DataNetwork dataNetwork3 = DataNetwork.this;
                    dataNetwork3.log("Defer message " + DataNetwork.eventToString(message.what) + ":" + list);
                    DataNetwork.this.deferMessage(message);
                    return true;
                } else if (i != 13) {
                    if (i == 15) {
                        DataNetwork.this.onHandoverResponse(message.arg1, message.getData().getParcelable("data_call_response"), (DataRetryManager.DataHandoverRetryEntry) message.obj);
                        return true;
                    } else if (i == 26) {
                        DataNetwork.this.onStartHandover(message.arg2, (DataRetryManager.DataHandoverRetryEntry) message.obj);
                        return true;
                    } else {
                        switch (i) {
                            case 20:
                                DataNetwork dataNetwork4 = DataNetwork.this;
                                dataNetwork4.reportAnomaly("Data service did not respond the handover request within " + TimeUnit.MILLISECONDS.toSeconds(DataNetwork.this.mDataConfigManager.getNetworkHandoverTimeoutMs()) + " seconds.", "1afe68cb-8b41-4964-a737-4f34372429ea");
                                DataNetwork.this.mFailCause = 65535;
                                DataNetwork.this.mDataNetworkCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetwork$HandoverState$$ExternalSyntheticLambda0
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        DataNetwork.HandoverState.this.lambda$processMessage$0(r2, r4);
                                    }
                                });
                                DataNetwork dataNetwork5 = DataNetwork.this;
                                dataNetwork5.transitionTo(dataNetwork5.mConnectedState);
                                return true;
                            case 21:
                            case 22:
                            case 23:
                            case 24:
                                break;
                            default:
                                return false;
                        }
                    }
                }
            }
            DataNetwork dataNetwork6 = DataNetwork.this;
            dataNetwork6.log("Defer message " + DataNetwork.eventToString(message.what));
            DataNetwork.this.deferMessage(message);
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$processMessage$0(long j, int i) {
            DataNetworkCallback dataNetworkCallback = DataNetwork.this.mDataNetworkCallback;
            DataNetwork dataNetwork = DataNetwork.this;
            dataNetworkCallback.onHandoverFailed(dataNetwork, dataNetwork.mFailCause, j, i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class DisconnectingState extends State {
        private DisconnectingState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void enter() {
            DataNetwork dataNetwork = DataNetwork.this;
            dataNetwork.sendMessageDelayed(20, dataNetwork.mDataConfigManager.getAnomalyNetworkDisconnectingTimeoutMs());
            DataNetwork.this.notifyPreciseDataConnectionState();
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void exit() {
            DataNetwork.this.removeMessages(20);
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            DataNetwork dataNetwork = DataNetwork.this;
            dataNetwork.logv("event=" + DataNetwork.eventToString(message.what));
            int i = message.what;
            if (i == 7) {
                if (!DataNetwork.this.mInvokedDataDeactivation) {
                    DataNetwork.this.removeMessages(7);
                    DataNetwork.this.removeDeferredMessages(7);
                    DataNetwork.this.onTearDown(message.arg1);
                    return true;
                }
                DataNetwork.this.log("Ignore tear down request because network is being torn down.");
                return true;
            } else if (i == 13) {
                DataNetwork.this.onDisplayInfoChanged();
                return true;
            } else if (i == 19) {
                DataNetwork.this.onDeactivateResponse(message.arg1);
                return true;
            } else if (i == 20) {
                DataNetwork dataNetwork2 = DataNetwork.this;
                dataNetwork2.reportAnomaly("RIL did not send data call list changed event after deactivate data call request within " + TimeUnit.MILLISECONDS.toSeconds(DataNetwork.this.mDataConfigManager.getAnomalyNetworkDisconnectingTimeoutMs()) + " seconds.", "d0e4fa1c-c57b-4ba5-b4b6-8955487012cc");
                DataNetwork.this.mFailCause = 65540;
                DataNetwork dataNetwork3 = DataNetwork.this;
                dataNetwork3.transitionTo(dataNetwork3.mDisconnectedState);
                return true;
            } else {
                switch (i) {
                    case 22:
                    case 23:
                    case 24:
                        DataNetwork.this.updateSuspendState();
                        DataNetwork.this.updateNetworkCapabilities();
                        return true;
                    default:
                        return false;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class DisconnectedState extends State {
        private DisconnectedState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void enter() {
            DataNetwork dataNetwork = DataNetwork.this;
            dataNetwork.logl("Data network disconnected. mEverConnected=" + DataNetwork.this.mEverConnected);
            final DataNetworkController.NetworkRequestList networkRequestList = new DataNetworkController.NetworkRequestList(DataNetwork.this.mAttachedNetworkRequestList);
            DataNetwork.this.sendMessage(10);
            DataNetwork.this.quit();
            if (DataNetwork.this.mEverConnected) {
                DataNetwork.this.mDataNetworkCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetwork$DisconnectedState$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        DataNetwork.DisconnectedState.this.lambda$enter$0();
                    }
                });
                if (DataNetwork.this.mTransport == 1) {
                    DataNetwork.this.unregisterForWwanEvents();
                }
            } else {
                DataNetwork.this.mDataNetworkCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetwork$DisconnectedState$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        DataNetwork.DisconnectedState.this.lambda$enter$1(networkRequestList);
                    }
                });
            }
            DataNetwork.this.notifyPreciseDataConnectionState();
            DataNetwork.this.mNetworkAgent.unregister();
            DataNetwork.this.mDataNetworkController.unregisterDataNetworkControllerCallback(DataNetwork.this.mDataNetworkControllerCallback);
            DataNetwork.this.mDataCallSessionStats.onDataCallDisconnected(DataNetwork.this.mFailCause);
            if (DataNetwork.this.mTransport == 2 && DataNetwork.this.mPduSessionId != 0) {
                DataNetwork.this.mRil.releasePduSessionId(null, DataNetwork.this.mPduSessionId);
            }
            if (DataNetwork.this.mVcnManager == null || DataNetwork.this.mVcnPolicyChangeListener == null) {
                return;
            }
            DataNetwork.this.mVcnManager.removeVcnNetworkPolicyChangeListener(DataNetwork.this.mVcnPolicyChangeListener);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$enter$0() {
            DataNetworkCallback dataNetworkCallback = DataNetwork.this.mDataNetworkCallback;
            DataNetwork dataNetwork = DataNetwork.this;
            dataNetworkCallback.onDisconnected(dataNetwork, dataNetwork.mFailCause, DataNetwork.this.mTearDownReason);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$enter$1(DataNetworkController.NetworkRequestList networkRequestList) {
            DataNetworkCallback dataNetworkCallback = DataNetwork.this.mDataNetworkCallback;
            DataNetwork dataNetwork = DataNetwork.this;
            dataNetworkCallback.onSetupDataFailed(dataNetwork, networkRequestList, dataNetwork.mFailCause, DataNetwork.this.mRetryDelayMillis);
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            DataNetwork dataNetwork = DataNetwork.this;
            dataNetwork.logv("event=" + DataNetwork.eventToString(message.what));
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerForWwanEvents() {
        registerForBandwidthUpdate();
        this.mKeepaliveTracker.registerForKeepaliveStatus();
        this.mRil.registerForNotAvailable(getHandler(), 4, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterForWwanEvents() {
        unregisterForBandwidthUpdate();
        this.mKeepaliveTracker.unregisterForKeepaliveStatus();
        this.mRil.unregisterForNotAvailable(getHandler());
    }

    @Override // com.android.internal.telephony.StateMachine
    protected void unhandledMessage(Message message) {
        IState currentState = getCurrentState();
        StringBuilder sb = new StringBuilder();
        sb.append("Unhandled message ");
        sb.append(message.what);
        sb.append(" in state ");
        sb.append(currentState == null ? "null" : currentState.getName());
        loge(sb.toString());
    }

    public boolean attachNetworkRequests(DataNetworkController.NetworkRequestList networkRequestList) {
        if (getCurrentState() == null || isDisconnected()) {
            return false;
        }
        sendMessage(obtainMessage(2, networkRequestList));
        return true;
    }

    public void onAttachNetworkRequests(DataNetworkController.NetworkRequestList networkRequestList) {
        final DataNetworkController.NetworkRequestList networkRequestList2 = new DataNetworkController.NetworkRequestList();
        Iterator<TelephonyNetworkRequest> it = networkRequestList.iterator();
        while (it.hasNext()) {
            TelephonyNetworkRequest next = it.next();
            if (!this.mDataNetworkController.isNetworkRequestExisting(next)) {
                networkRequestList2.add(next);
                log("Attached failed. Network request was already removed. " + next);
            } else if (!next.canBeSatisfiedBy(getNetworkCapabilities())) {
                networkRequestList2.add(next);
                log("Attached failed. Cannot satisfy the network request " + next);
            } else {
                this.mAttachedNetworkRequestList.add(next);
                next.setAttachedNetwork(this);
                next.setState(1);
                log("Successfully attached network request " + next);
            }
        }
        if (networkRequestList2.size() > 0) {
            this.mDataNetworkCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetwork$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    DataNetwork.this.lambda$onAttachNetworkRequests$0(networkRequestList2);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttachNetworkRequests$0(DataNetworkController.NetworkRequestList networkRequestList) {
        this.mDataNetworkCallback.onAttachFailed(this, networkRequestList);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDetachNetworkRequest(TelephonyNetworkRequest telephonyNetworkRequest) {
        this.mAttachedNetworkRequestList.remove(telephonyNetworkRequest);
        telephonyNetworkRequest.setState(0);
        telephonyNetworkRequest.setAttachedNetwork(null);
        if (this.mAttachedNetworkRequestList.isEmpty()) {
            log("All network requests are detached.");
            int preferredDataPhoneId = PhoneSwitcher.getInstance().getPreferredDataPhoneId();
            if (preferredDataPhoneId == -1 || preferredDataPhoneId == this.mPhone.getPhoneId()) {
                return;
            }
            lambda$tearDownWhenConditionMet$9(30);
        }
    }

    public void detachNetworkRequest(TelephonyNetworkRequest telephonyNetworkRequest) {
        if (getCurrentState() == null || isDisconnected()) {
            return;
        }
        sendMessage(obtainMessage(3, telephonyNetworkRequest));
    }

    private void registerForBandwidthUpdate() {
        int bandwidthEstimateSource = this.mDataConfigManager.getBandwidthEstimateSource();
        if (bandwidthEstimateSource == 1) {
            this.mPhone.mCi.registerForLceInfo(getHandler(), 11, null);
        } else if (bandwidthEstimateSource == 3) {
            if (this.mLinkBandwidthEstimatorCallback == null) {
                Handler handler = getHandler();
                Objects.requireNonNull(handler);
                this.mLinkBandwidthEstimatorCallback = new LinkBandwidthEstimator.LinkBandwidthEstimatorCallback(new NetworkTypeController$$ExternalSyntheticLambda0(handler)) { // from class: com.android.internal.telephony.data.DataNetwork.3
                    @Override // com.android.internal.telephony.data.LinkBandwidthEstimator.LinkBandwidthEstimatorCallback
                    public void onBandwidthChanged(int i, int i2) {
                        if (DataNetwork.this.isConnected()) {
                            DataNetwork.this.onBandwidthUpdated(i, i2);
                        }
                    }
                };
                this.mPhone.getLinkBandwidthEstimator().registerCallback(this.mLinkBandwidthEstimatorCallback);
            }
        } else {
            loge("Invalid bandwidth source configuration: " + bandwidthEstimateSource);
        }
    }

    private void unregisterForBandwidthUpdate() {
        int bandwidthEstimateSource = this.mDataConfigManager.getBandwidthEstimateSource();
        if (bandwidthEstimateSource == 1) {
            this.mPhone.mCi.unregisterForLceInfo(getHandler());
        } else if (bandwidthEstimateSource == 3) {
            if (this.mLinkBandwidthEstimatorCallback != null) {
                this.mPhone.getLinkBandwidthEstimator().unregisterCallback(this.mLinkBandwidthEstimatorCallback);
                this.mLinkBandwidthEstimatorCallback = null;
            }
        } else {
            loge("Invalid bandwidth source configuration: " + bandwidthEstimateSource);
        }
    }

    private void removeUnsatisfiedNetworkRequests() {
        Iterator<TelephonyNetworkRequest> it = this.mAttachedNetworkRequestList.iterator();
        while (it.hasNext()) {
            TelephonyNetworkRequest next = it.next();
            if (!next.canBeSatisfiedBy(this.mNetworkCapabilities)) {
                log("removeUnsatisfiedNetworkRequests: " + next + " can't be satisfied anymore. Will be detached.");
                detachNetworkRequest(next);
            }
        }
    }

    private boolean isLinkPropertiesCompatible(LinkProperties linkProperties, LinkProperties linkProperties2) {
        if (!Objects.equals(linkProperties, linkProperties2) && !LinkPropertiesUtils.isIdenticalAddresses(linkProperties, linkProperties2)) {
            LinkPropertiesUtils.CompareOrUpdateResult compareOrUpdateResult = new LinkPropertiesUtils.CompareOrUpdateResult(linkProperties.getLinkAddresses(), linkProperties2.getLinkAddresses(), new Function() { // from class: com.android.internal.telephony.data.DataNetwork$$ExternalSyntheticLambda21
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Integer lambda$isLinkPropertiesCompatible$1;
                    lambda$isLinkPropertiesCompatible$1 = DataNetwork.lambda$isLinkPropertiesCompatible$1((LinkAddress) obj);
                    return lambda$isLinkPropertiesCompatible$1;
                }
            });
            log("isLinkPropertiesCompatible: old=" + linkProperties + " new=" + linkProperties2 + " result=" + compareOrUpdateResult);
            for (T t : compareOrUpdateResult.added) {
                for (T t2 : compareOrUpdateResult.removed) {
                    if (NetUtils.addressTypeMatches(t2.getAddress(), t.getAddress())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Integer lambda$isLinkPropertiesCompatible$1(LinkAddress linkAddress) {
        return Integer.valueOf(Objects.hash(linkAddress.getAddress(), Integer.valueOf(linkAddress.getPrefixLength()), Integer.valueOf(linkAddress.getScope())));
    }

    private static boolean areImmutableCapabilitiesChanged(NetworkCapabilities networkCapabilities, NetworkCapabilities networkCapabilities2) {
        if (networkCapabilities == null || ArrayUtils.isEmpty(networkCapabilities.getCapabilities())) {
            return false;
        }
        List list = (List) Arrays.stream(networkCapabilities.getCapabilities()).boxed().collect(Collectors.toList());
        List<Integer> list2 = MUTABLE_CAPABILITIES;
        list.removeAll(list2);
        List list3 = (List) Arrays.stream(networkCapabilities2.getCapabilities()).boxed().collect(Collectors.toList());
        list3.removeAll(list2);
        return (list.size() == list3.size() && list.containsAll(list3)) ? false : true;
    }

    private void recreateNetworkAgent() {
        if (isConnecting() || isDisconnected() || isDisconnecting()) {
            loge("Incorrect state for re-creating the network agent.");
            return;
        }
        this.mNetworkAgent.abandon();
        TelephonyNetworkAgent createNetworkAgent = createNetworkAgent();
        this.mNetworkAgent = createNetworkAgent;
        createNetworkAgent.markConnected();
        if (this.mSuspended) {
            log("recreateNetworkAgent: The network is in suspended state. Update the network capability again. nc=" + this.mNetworkCapabilities);
            this.mNetworkAgent.sendNetworkCapabilities(this.mNetworkCapabilities);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNetworkCapabilities() {
        NetworkRegistrationInfo networkRegistrationInfo;
        final NetworkCapabilities.Builder addTransportType = new NetworkCapabilities.Builder().addTransportType(0);
        boolean dataRoaming = this.mPhone.getServiceState().getDataRoaming();
        addTransportType.setNetworkSpecifier(new TelephonyNetworkSpecifier.Builder().setSubscriptionId(this.mSubId).build());
        addTransportType.setSubscriptionIds(Collections.singleton(Integer.valueOf(this.mSubId)));
        ApnSetting apnSetting = this.mDataProfile.getApnSetting();
        if (apnSetting != null) {
            apnSetting.getApnTypes().stream().map(new DataConfigManager$$ExternalSyntheticLambda15()).filter(new Predicate() { // from class: com.android.internal.telephony.data.DataNetwork$$ExternalSyntheticLambda13
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$updateNetworkCapabilities$2;
                    lambda$updateNetworkCapabilities$2 = DataNetwork.lambda$updateNetworkCapabilities$2((Integer) obj);
                    return lambda$updateNetworkCapabilities$2;
                }
            }).forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataNetwork$$ExternalSyntheticLambda14
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    addTransportType.addCapability(((Integer) obj).intValue());
                }
            });
            if (apnSetting.getApnTypes().contains(16384)) {
                addTransportType.addCapability(12);
            }
        }
        NetworkCapabilities networkCapabilities = this.mNetworkCapabilities;
        if (networkCapabilities != null && networkCapabilities.hasCapability(33)) {
            addTransportType.addCapability(33);
        } else if (this.mDataProfile.canSatisfy(4)) {
            addTransportType.addCapability(33);
            if (this.mTransport == 1 && (networkRegistrationInfo = getNetworkRegistrationInfo()) != null) {
                DataSpecificRegistrationInfo dataSpecificInfo = networkRegistrationInfo.getDataSpecificInfo();
                if (dataSpecificInfo != null && dataSpecificInfo.getVopsSupportInfo() != null && !dataSpecificInfo.getVopsSupportInfo().isVopsSupported() && !this.mDataConfigManager.shouldKeepNetworkUpInNonVops()) {
                    addTransportType.removeCapability(33);
                }
                log("updateNetworkCapabilities: dsri=" + dataSpecificInfo);
            }
        }
        for (TrafficDescriptor trafficDescriptor : this.mTrafficDescriptors) {
            try {
                if (trafficDescriptor.getOsAppId() != null) {
                    TrafficDescriptor.OsAppId osAppId = new TrafficDescriptor.OsAppId(trafficDescriptor.getOsAppId());
                    if (osAppId.getOsId().equals(TrafficDescriptor.OsAppId.ANDROID_OS_ID)) {
                        int networkCapabilityFromString = DataUtils.getNetworkCapabilityFromString(osAppId.getAppId());
                        if (networkCapabilityFromString != 5) {
                            if (networkCapabilityFromString == 29) {
                                addTransportType.addCapability(networkCapabilityFromString);
                                addTransportType.addCapability(12);
                                addTransportType.addEnterpriseId(osAppId.getDifferentiator());
                            } else if (networkCapabilityFromString != 34 && networkCapabilityFromString != 35) {
                                loge("Invalid app id " + osAppId.getAppId());
                            }
                        }
                        addTransportType.addCapability(networkCapabilityFromString);
                    } else {
                        loge("Received non-Android OS id " + osAppId.getOsId());
                    }
                }
            } catch (Exception e) {
                loge("Exception: " + e + ". Failed to create osAppId from " + new BigInteger(1, trafficDescriptor.getOsAppId()).toString(16));
            }
        }
        if (!this.mCongested) {
            addTransportType.addCapability(20);
        }
        if (this.mTempNotMeteredSupported && this.mTempNotMetered) {
            addTransportType.addCapability(25);
        }
        addTransportType.addCapability(28);
        VcnNetworkPolicyResult vcnPolicy = getVcnPolicy(addTransportType.build());
        if (vcnPolicy != null && !vcnPolicy.getNetworkCapabilities().hasCapability(28)) {
            addTransportType.removeCapability(28);
        }
        if (!dataRoaming) {
            addTransportType.addCapability(18);
        }
        if (!this.mSuspended) {
            addTransportType.addCapability(21);
        }
        int i = this.mCarrierServicePackageUid;
        if (i != -1 && ArrayUtils.contains(this.mAdministratorUids, i)) {
            addTransportType.setOwnerUid(this.mCarrierServicePackageUid);
            addTransportType.setAllowedUids(Collections.singleton(Integer.valueOf(this.mCarrierServicePackageUid)));
        }
        addTransportType.setAdministratorUids(this.mAdministratorUids);
        Set<Integer> set = (Set) this.mDataConfigManager.getMeteredNetworkCapabilities(dataRoaming).stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataNetwork$$ExternalSyntheticLambda15
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updateNetworkCapabilities$3;
                lambda$updateNetworkCapabilities$3 = DataNetwork.this.lambda$updateNetworkCapabilities$3((Integer) obj);
                return lambda$updateNetworkCapabilities$3;
            }
        }).collect(Collectors.toSet());
        Stream stream = set.stream();
        final Set set2 = (Set) Arrays.stream(addTransportType.build().getCapabilities()).boxed().collect(Collectors.toSet());
        Objects.requireNonNull(set2);
        if (stream.noneMatch(new Predicate() { // from class: com.android.internal.telephony.data.DataNetwork$$ExternalSyntheticLambda16
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return set2.contains((Integer) obj);
            }
        })) {
            addTransportType.addCapability(11);
        }
        addTransportType.addCapability(13);
        if (!this.mDataNetworkController.getDataSettingsManager().isDataEnabled() || (this.mPhone.getServiceState().getDataRoaming() && !this.mDataNetworkController.getDataSettingsManager().isDataRoamingEnabled())) {
            DataEvaluation.DataAllowedReason dataAllowedReason = this.mDataAllowedReason;
            if (dataAllowedReason == DataEvaluation.DataAllowedReason.RESTRICTED_REQUEST) {
                addTransportType.removeCapability(13);
            } else if (dataAllowedReason == DataEvaluation.DataAllowedReason.UNMETERED_USAGE || dataAllowedReason == DataEvaluation.DataAllowedReason.MMS_REQUEST || dataAllowedReason == DataEvaluation.DataAllowedReason.EMERGENCY_SUPL) {
                for (Integer num : set) {
                    int intValue = num.intValue();
                    if (intValue != 0 || this.mDataAllowedReason != DataEvaluation.DataAllowedReason.MMS_REQUEST) {
                        if (intValue != 1 || this.mDataAllowedReason != DataEvaluation.DataAllowedReason.EMERGENCY_SUPL) {
                            addTransportType.removeCapability(intValue);
                        }
                    }
                }
            }
        }
        if (NetworkCapabilitiesUtils.inferRestrictedCapability(addTransportType.build()) || (vcnPolicy != null && !vcnPolicy.getNetworkCapabilities().hasCapability(13))) {
            addTransportType.removeCapability(13);
        }
        addTransportType.setLinkDownstreamBandwidthKbps(this.mNetworkBandwidth.downlinkBandwidthKbps);
        addTransportType.setLinkUpstreamBandwidthKbps(this.mNetworkBandwidth.uplinkBandwidthKbps);
        NetworkCapabilities build = addTransportType.build();
        NetworkCapabilities networkCapabilities2 = this.mNetworkCapabilities;
        if (networkCapabilities2 == null || this.mNetworkAgent == null) {
            this.mNetworkCapabilities = build;
            logl("Initial capabilities " + this.mNetworkCapabilities);
        } else if (!build.equals(networkCapabilities2)) {
            if (this.mEverConnected && areImmutableCapabilitiesChanged(this.mNetworkCapabilities, build) && (isConnected() || isHandoverInProgress())) {
                logl("updateNetworkCapabilities: Immutable capabilities changed. Re-create the network agent. Attempted to change from " + this.mNetworkCapabilities + " to " + build);
                this.mNetworkCapabilities = build;
                recreateNetworkAgent();
            } else {
                this.mNetworkCapabilities = build;
                log("Capabilities changed to " + this.mNetworkCapabilities);
                this.mNetworkAgent.sendNetworkCapabilities(this.mNetworkCapabilities);
            }
            removeUnsatisfiedNetworkRequests();
            this.mDataNetworkCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetwork$$ExternalSyntheticLambda17
                @Override // java.lang.Runnable
                public final void run() {
                    DataNetwork.this.lambda$updateNetworkCapabilities$4();
                }
            });
        } else {
            log("updateNetworkCapabilities: Capabilities not changed.");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateNetworkCapabilities$2(Integer num) {
        return num.intValue() >= 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateNetworkCapabilities$3(Integer num) {
        return this.mAccessNetworksManager.getPreferredTransportByNetworkCapability(num.intValue()) == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateNetworkCapabilities$4() {
        this.mDataNetworkCallback.onNetworkCapabilitiesChanged(this);
    }

    public NetworkCapabilities getNetworkCapabilities() {
        return this.mNetworkCapabilities;
    }

    public LinkProperties getLinkProperties() {
        return this.mLinkProperties;
    }

    public DataProfile getDataProfile() {
        return this.mDataProfile;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSuspendState() {
        NetworkRegistrationInfo networkRegistrationInfo;
        if (isConnecting() || isDisconnected() || (networkRegistrationInfo = getNetworkRegistrationInfo()) == null) {
            return;
        }
        boolean z = false;
        if (!this.mNetworkCapabilities.hasCapability(10) && ((networkRegistrationInfo.getRegistrationState() != 1 && networkRegistrationInfo.getRegistrationState() != 5) || (!this.mPhone.getServiceStateTracker().isConcurrentVoiceAndDataAllowed() && this.mTransport == 1 && this.mPhone.getCallTracker().getState() != PhoneConstants.State.IDLE))) {
            z = true;
        }
        if (this.mSuspended != z) {
            this.mSuspended = z;
            StringBuilder sb = new StringBuilder();
            sb.append("Network becomes ");
            sb.append(this.mSuspended ? "suspended" : "unsuspended");
            logl(sb.toString());
            updateNetworkCapabilities();
            notifyPreciseDataConnectionState();
            this.mDataNetworkCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetwork$$ExternalSyntheticLambda19
                @Override // java.lang.Runnable
                public final void run() {
                    DataNetwork.this.lambda$updateSuspendState$5();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSuspendState$5() {
        this.mDataNetworkCallback.onSuspendedStateChanged(this, this.mSuspended);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void allocatePduSessionId() {
        this.mRil.allocatePduSessionId(obtainMessage(5));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setupData() {
        int dataNetworkType = getDataNetworkType();
        boolean dataRoamingFromRegistration = this.mPhone.getServiceState().getDataRoamingFromRegistration();
        boolean z = this.mPhone.getDataRoamingEnabled() || (dataRoamingFromRegistration && !this.mPhone.getServiceState().getDataRoaming());
        TrafficDescriptor trafficDescriptor = this.mDataProfile.getTrafficDescriptor();
        boolean z2 = trafficDescriptor == null || !TextUtils.isEmpty(trafficDescriptor.getDataNetworkName());
        int networkTypeToAccessNetworkType = DataUtils.networkTypeToAccessNetworkType(dataNetworkType);
        boolean z3 = z2;
        boolean z4 = z;
        this.mDataServiceManagers.get(this.mTransport).setupDataCall(networkTypeToAccessNetworkType, this.mDataProfile, dataRoamingFromRegistration, z, 1, null, this.mPduSessionId, null, trafficDescriptor, z3, obtainMessage(6));
        this.mDataCallSessionStats.onSetupDataCall(this.mDataProfile.getApnSetting() != null ? this.mDataProfile.getApnSetting().getApnTypeBitmask() : 0);
        logl("setupData: accessNetwork=" + AccessNetworkConstants.AccessNetworkType.toString(networkTypeToAccessNetworkType) + ", " + this.mDataProfile + ", isModemRoaming=" + dataRoamingFromRegistration + ", allowRoaming=" + z4 + ", PDU session id=" + this.mPduSessionId + ", matchAllRuleAllowed=" + z3);
        TelephonyMetrics.getInstance().writeSetupDataCall(this.mPhone.getPhoneId(), ServiceState.networkTypeToRilRadioTechnology(dataNetworkType), this.mDataProfile.getProfileId(), this.mDataProfile.getApn(), this.mDataProfile.getProtocolType());
    }

    private int getFailCauseFromDataCallResponse(int i, DataCallResponse dataCallResponse) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i == 4) {
                            return 65537;
                        }
                        if (i != 5) {
                        }
                    }
                    return 65545;
                }
                return 65538;
            }
            return 65546;
        } else if (dataCallResponse != null) {
            return DataFailCause.getFailCause(dataCallResponse.getCause());
        }
        return 0;
    }

    private void updateDataNetwork(DataCallResponse dataCallResponse) {
        this.mCid.put(this.mTransport, dataCallResponse.getId());
        LinkProperties linkProperties = new LinkProperties();
        linkProperties.setInterfaceName(dataCallResponse.getInterfaceName());
        if (this.mPduSessionId != dataCallResponse.getPduSessionId()) {
            this.mPduSessionId = dataCallResponse.getPduSessionId();
            log("PDU session id updated to " + this.mPduSessionId);
        }
        if (this.mLinkStatus != dataCallResponse.getLinkStatus()) {
            this.mLinkStatus = dataCallResponse.getLinkStatus();
            log("Link status updated to " + DataUtils.linkStatusToString(this.mLinkStatus));
            this.mDataNetworkCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetwork$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    DataNetwork.this.lambda$updateDataNetwork$6();
                }
            });
        }
        if (dataCallResponse.getAddresses().size() > 0) {
            for (LinkAddress linkAddress : dataCallResponse.getAddresses()) {
                if (!linkAddress.getAddress().isAnyLocalAddress()) {
                    logv("addr/pl=" + linkAddress.getAddress() + "/" + linkAddress.getPrefixLength());
                    linkProperties.addLinkAddress(linkAddress);
                }
            }
        } else {
            loge("no address for ifname=" + dataCallResponse.getInterfaceName());
        }
        if (dataCallResponse.getDnsAddresses().size() > 0) {
            for (InetAddress inetAddress : dataCallResponse.getDnsAddresses()) {
                if (!inetAddress.isAnyLocalAddress()) {
                    linkProperties.addDnsServer(inetAddress);
                }
            }
        } else {
            loge("Empty dns response");
        }
        if (dataCallResponse.getPcscfAddresses().size() > 0) {
            for (InetAddress inetAddress2 : dataCallResponse.getPcscfAddresses()) {
                linkProperties.addPcscfServer(inetAddress2);
            }
        }
        int mtuV4 = dataCallResponse.getMtuV4() > 0 ? dataCallResponse.getMtuV4() : dataCallResponse.getMtu();
        if (mtuV4 <= 0) {
            if (this.mDataProfile.getApnSetting() != null) {
                mtuV4 = this.mDataProfile.getApnSetting().getMtuV4();
            }
            if (mtuV4 <= 0) {
                mtuV4 = this.mDataConfigManager.getDefaultMtu();
            }
        }
        int mtuV6 = dataCallResponse.getMtuV6() > 0 ? dataCallResponse.getMtuV6() : dataCallResponse.getMtu();
        if (mtuV6 <= 0) {
            if (this.mDataProfile.getApnSetting() != null) {
                mtuV6 = this.mDataProfile.getApnSetting().getMtuV6();
            }
            if (mtuV6 <= 0) {
                mtuV6 = this.mDataConfigManager.getDefaultMtu();
            }
        }
        for (InetAddress inetAddress3 : dataCallResponse.getGatewayAddresses()) {
            linkProperties.addRoute(new RouteInfo(null, inetAddress3, null, 1, inetAddress3 instanceof Inet6Address ? mtuV6 : mtuV4));
        }
        linkProperties.setMtu(Math.max(mtuV4, mtuV6));
        if (this.mDataProfile.getApnSetting() != null && !TextUtils.isEmpty(this.mDataProfile.getApnSetting().getProxyAddressAsString())) {
            int proxyPort = this.mDataProfile.getApnSetting().getProxyPort();
            if (proxyPort == -1) {
                proxyPort = 8080;
            }
            linkProperties.setHttpProxy(ProxyInfo.buildDirectProxy(this.mDataProfile.getApnSetting().getProxyAddressAsString(), proxyPort));
        }
        linkProperties.setTcpBufferSizes(this.mTcpBufferSizes);
        this.mNetworkSliceInfo = dataCallResponse.getSliceInfo();
        this.mTrafficDescriptors.clear();
        this.mTrafficDescriptors.addAll(dataCallResponse.getTrafficDescriptors());
        this.mQosBearerSessions.clear();
        this.mQosBearerSessions.addAll(dataCallResponse.getQosBearerSessions());
        QosCallbackTracker qosCallbackTracker = this.mQosCallbackTracker;
        if (qosCallbackTracker != null) {
            qosCallbackTracker.updateSessions(this.mQosBearerSessions);
        }
        if (!linkProperties.equals(this.mLinkProperties)) {
            if ((isConnected() || isHandoverInProgress()) && !isLinkPropertiesCompatible(this.mLinkProperties, linkProperties)) {
                logl("updateDataNetwork: Incompatible link properties detected. Re-create the network agent. Changed from " + this.mLinkProperties + " to " + linkProperties);
                this.mLinkProperties = linkProperties;
                recreateNetworkAgent();
            } else {
                this.mLinkProperties = linkProperties;
                log("sendLinkProperties " + this.mLinkProperties);
                this.mNetworkAgent.sendLinkProperties(this.mLinkProperties);
            }
        }
        updateNetworkCapabilities();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateDataNetwork$6() {
        this.mDataNetworkCallback.onLinkStatusChanged(this, this.mLinkStatus);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSetupResponse(int i, DataCallResponse dataCallResponse) {
        int i2;
        int i3;
        logl("onSetupResponse: resultCode=" + DataServiceCallback.resultCodeToString(i) + ", response=" + dataCallResponse);
        this.mFailCause = getFailCauseFromDataCallResponse(i, dataCallResponse);
        validateDataCallResponse(dataCallResponse, true);
        if (this.mFailCause == 0) {
            DataNetwork dataNetworkByInterface = this.mDataNetworkController.getDataNetworkByInterface(dataCallResponse.getInterfaceName());
            if (dataNetworkByInterface != null) {
                logl("Interface " + dataCallResponse.getInterfaceName() + " has been already used by " + dataNetworkByInterface + ". Silently tear down now.");
                if (dataCallResponse.getTrafficDescriptors().isEmpty() && dataNetworkByInterface.isConnected()) {
                    reportAnomaly("Duplicate network interface " + dataCallResponse.getInterfaceName() + " detected.", "62f66e7e-8d71-45de-a57b-dc5c78223fd5");
                }
                this.mRetryDelayMillis = -1L;
                this.mFailCause = 65547;
                transitionTo(this.mDisconnectedState);
                return;
            }
            updateDataNetwork(dataCallResponse);
            if (this.mAttachedNetworkRequestList.size() == 0) {
                log("Tear down the network since there is no live network request.");
                onTearDown(5);
                return;
            }
            VcnManager vcnManager = this.mVcnManager;
            if (vcnManager != null && vcnManager.applyVcnNetworkPolicy(this.mNetworkCapabilities, this.mLinkProperties).isTeardownRequested()) {
                log("VCN service requested to tear down the network.");
                onTearDown(15);
                return;
            }
            transitionTo(this.mConnectedState);
        } else {
            this.mRetryDelayMillis = dataCallResponse != null ? dataCallResponse.getRetryDurationMillis() : -1L;
            transitionTo(this.mDisconnectedState);
        }
        if (this.mDataProfile.getApnSetting() != null) {
            i2 = this.mDataProfile.getApnSetting().getApnTypeBitmask();
            i3 = this.mDataProfile.getApnSetting().getProtocol();
        } else {
            i2 = 0;
            i3 = -1;
        }
        this.mDataCallSessionStats.onSetupDataCallResponse(dataCallResponse, getDataNetworkType(), i2, i3, dataCallResponse != null ? dataCallResponse.getCause() : this.mFailCause);
    }

    private void validateDataCallResponse(DataCallResponse dataCallResponse, boolean z) {
        int protocol;
        if (dataCallResponse == null || dataCallResponse.getLinkStatus() == 0) {
            return;
        }
        int cause = dataCallResponse.getCause();
        if (cause == 0) {
            if (TextUtils.isEmpty(dataCallResponse.getInterfaceName()) || dataCallResponse.getAddresses().isEmpty() || dataCallResponse.getLinkStatus() < -1 || dataCallResponse.getLinkStatus() > 2 || dataCallResponse.getProtocolType() < -1 || dataCallResponse.getProtocolType() > 5 || dataCallResponse.getHandoverFailureMode() < -1 || dataCallResponse.getHandoverFailureMode() > 3) {
                loge("Invalid DataCallResponse:" + dataCallResponse);
                reportAnomaly("Invalid DataCallResponse detected", "1f273e9d-b09c-46eb-ad1c-421d01f61164");
            }
            NetworkRegistrationInfo networkRegistrationInfo = getNetworkRegistrationInfo();
            if (!z || this.mDataProfile.getApnSetting() == null || networkRegistrationInfo == null || !networkRegistrationInfo.isInService()) {
                return;
            }
            if (networkRegistrationInfo.getNetworkRegistrationState() == 5) {
                protocol = this.mDataProfile.getApnSetting().getRoamingProtocol();
            } else {
                protocol = this.mDataProfile.getApnSetting().getProtocol();
            }
            String str = this.mTransport == 1 ? "RIL" : "IWLAN data service";
            if (protocol == 0) {
                if (dataCallResponse.getAddresses().stream().anyMatch(new Predicate() { // from class: com.android.internal.telephony.data.DataNetwork$$ExternalSyntheticLambda9
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$validateDataCallResponse$7;
                        lambda$validateDataCallResponse$7 = DataNetwork.lambda$validateDataCallResponse$7((LinkAddress) obj);
                        return lambda$validateDataCallResponse$7;
                    }
                })) {
                    loge("Invalid DataCallResponse. Requested IPv4 but got IPv6 address. " + dataCallResponse);
                    reportAnomaly(str + " reported mismatched IP type. Requested IPv4 but got IPv6 address.", "7744f920-fb64-4db0-ba47-de0eae485a80");
                }
            } else if (protocol == 1 && dataCallResponse.getAddresses().stream().anyMatch(new Predicate() { // from class: com.android.internal.telephony.data.DataNetwork$$ExternalSyntheticLambda10
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$validateDataCallResponse$8;
                    lambda$validateDataCallResponse$8 = DataNetwork.lambda$validateDataCallResponse$8((LinkAddress) obj);
                    return lambda$validateDataCallResponse$8;
                }
            })) {
                loge("Invalid DataCallResponse. Requested IPv6 but got IPv4 address. " + dataCallResponse);
                reportAnomaly(str + " reported mismatched IP type. Requested IPv6 but got IPv4 address.", "7744f920-fb64-4db0-ba47-de0eae485a80");
            }
        } else if (DataFailCause.isFailCauseExisting(cause)) {
        } else {
            loge("Invalid DataFailCause in " + dataCallResponse);
            reportAnomaly("Invalid DataFailCause: (0x" + Integer.toHexString(cause) + ")", "6b264f28-9f58-4cbd-9e0e-d7624ba30879");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$validateDataCallResponse$7(LinkAddress linkAddress) {
        return linkAddress.getAddress() instanceof Inet6Address;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$validateDataCallResponse$8(LinkAddress linkAddress) {
        return linkAddress.getAddress() instanceof Inet4Address;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDeactivateResponse(int i) {
        logl("onDeactivateResponse: resultCode=" + DataServiceCallback.resultCodeToString(i));
        if (i == 4) {
            log("Remove network since deactivate request returned an error.");
            this.mFailCause = 65537;
            transitionTo(this.mDisconnectedState);
        } else if (this.mPhone.getHalVersion(1).less(RIL.RADIO_HAL_VERSION_2_0)) {
            log("Remove network on deactivate data response on old HAL " + this.mPhone.getHalVersion(1));
            this.mFailCause = 65540;
            transitionTo(this.mDisconnectedState);
        }
    }

    /* renamed from: tearDown */
    public void lambda$tearDownWhenConditionMet$9(int i) {
        if (getCurrentState() == null || isDisconnected()) {
            return;
        }
        this.mTearDownReason = i;
        sendMessage(obtainMessage(7, i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTearDown(int i) {
        logl("onTearDown: reason=" + tearDownReasonToString(i));
        if (i == 1 && isConnected() && (this.mNetworkCapabilities.hasCapability(4) || this.mNetworkCapabilities.hasCapability(12))) {
            this.mDataNetworkCallback.onTrackNetworkUnwanted(this);
        }
        this.mDataServiceManagers.get(this.mTransport).deactivateDataCall(this.mCid.get(this.mTransport), i == 3 ? 2 : 1, obtainMessage(19));
        this.mDataCallSessionStats.setDeactivateDataCallReason(1);
        this.mInvokedDataDeactivation = true;
    }

    public boolean shouldDelayImsTearDownDueToInCall() {
        NetworkCapabilities networkCapabilities;
        return (!this.mDataConfigManager.isImsDelayTearDownUntilVoiceCallEndEnabled() || (networkCapabilities = this.mNetworkCapabilities) == null || !networkCapabilities.hasCapability(33) || this.mPhone.getImsPhone() == null || this.mPhone.getImsPhone().getCallTracker().getState() == PhoneConstants.State.IDLE) ? false : true;
    }

    public Runnable tearDownWhenConditionMet(final int i, long j) {
        if (getCurrentState() == null || isDisconnected() || isDisconnecting()) {
            loge("tearDownWhenConditionMet: Not in the right state. State=" + getCurrentState());
            return null;
        }
        logl("tearDownWhenConditionMet: reason=" + tearDownReasonToString(i) + ", timeout=" + j + "ms.");
        sendMessage(21, i, (int) j);
        return new Runnable() { // from class: com.android.internal.telephony.data.DataNetwork$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                DataNetwork.this.lambda$tearDownWhenConditionMet$9(i);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDataStateChanged(int i, List<DataCallResponse> list) {
        logv("onDataStateChanged: " + list);
        int i2 = this.mTransport;
        if (i != i2 || this.mCid.get(i2) == -1 || isDisconnected()) {
            return;
        }
        DataCallResponse orElse = list.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataNetwork$$ExternalSyntheticLambda18
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$onDataStateChanged$10;
                lambda$onDataStateChanged$10 = DataNetwork.this.lambda$onDataStateChanged$10((DataCallResponse) obj);
                return lambda$onDataStateChanged$10;
            }
        }).findFirst().orElse(null);
        if (orElse != null) {
            if (orElse.equals(this.mDataCallResponse)) {
                return;
            }
            log("onDataStateChanged: " + orElse);
            validateDataCallResponse(orElse, false);
            this.mDataCallResponse = orElse;
            if (orElse.getLinkStatus() != 0) {
                updateDataNetwork(orElse);
                return;
            }
            log("onDataStateChanged: PDN inactive reported by " + AccessNetworkConstants.transportTypeToString(this.mTransport) + " data service.");
            this.mFailCause = this.mEverConnected ? orElse.getCause() : 65547;
            this.mRetryDelayMillis = -1L;
            transitionTo(this.mDisconnectedState);
            return;
        }
        log("onDataStateChanged: PDN disconnected reported by " + AccessNetworkConstants.transportTypeToString(this.mTransport) + " data service.");
        this.mFailCause = this.mEverConnected ? 65540 : 65547;
        this.mRetryDelayMillis = -1L;
        transitionTo(this.mDisconnectedState);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onDataStateChanged$10(DataCallResponse dataCallResponse) {
        return this.mCid.get(this.mTransport) == dataCallResponse.getId();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCarrierConfigUpdated() {
        log("onCarrierConfigUpdated");
        updateBandwidthFromDataConfig();
        updateTcpBufferSizes();
        updateMeteredAndCongested();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onBandwidthUpdatedFromModem(List<LinkCapacityEstimate> list) {
        Objects.requireNonNull(list);
        if (list.isEmpty()) {
            return;
        }
        Iterator<LinkCapacityEstimate> it = list.iterator();
        int i = 0;
        int i2 = 0;
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            LinkCapacityEstimate next = it.next();
            if (next.getType() == 2) {
                i = next.getUplinkCapacityKbps();
                i2 = next.getDownlinkCapacityKbps();
                break;
            } else if (next.getType() == 0 || next.getType() == 1) {
                i += next.getUplinkCapacityKbps();
                i2 += next.getDownlinkCapacityKbps();
            } else {
                loge("Invalid LinkCapacityEstimate type " + next.getType());
            }
        }
        onBandwidthUpdated(i, i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onBandwidthUpdated(int i, int i2) {
        log("onBandwidthUpdated: downlinkBandwidthKbps=" + i2 + ", uplinkBandwidthKbps=" + i);
        NetworkBandwidth bandwidthForNetworkType = this.mDataConfigManager.getBandwidthForNetworkType(this.mTelephonyDisplayInfo);
        if (i2 == -1 && bandwidthForNetworkType != null) {
            i2 = bandwidthForNetworkType.downlinkBandwidthKbps;
        }
        if (i == -1 && bandwidthForNetworkType != null) {
            i = bandwidthForNetworkType.uplinkBandwidthKbps;
        }
        this.mNetworkBandwidth = new NetworkBandwidth(i2, Math.min(i, i2));
        updateNetworkCapabilities();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDisplayInfoChanged() {
        this.mTelephonyDisplayInfo = this.mPhone.getDisplayInfoController().getTelephonyDisplayInfo();
        updateBandwidthFromDataConfig();
        updateTcpBufferSizes();
        updateMeteredAndCongested();
    }

    private void updateBandwidthFromDataConfig() {
        if (this.mDataConfigManager.getBandwidthEstimateSource() != 2) {
            return;
        }
        log("updateBandwidthFromDataConfig");
        this.mNetworkBandwidth = this.mDataConfigManager.getBandwidthForNetworkType(this.mTelephonyDisplayInfo);
        updateNetworkCapabilities();
    }

    private void updateTcpBufferSizes() {
        log("updateTcpBufferSizes");
        this.mTcpBufferSizes = this.mDataConfigManager.getTcpConfigString(this.mTelephonyDisplayInfo);
        LinkProperties linkProperties = new LinkProperties(this.mLinkProperties);
        linkProperties.setTcpBufferSizes(this.mTcpBufferSizes);
        if (linkProperties.equals(this.mLinkProperties)) {
            return;
        }
        this.mLinkProperties = linkProperties;
        log("sendLinkProperties " + this.mLinkProperties);
        this.mNetworkAgent.sendLinkProperties(this.mLinkProperties);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMeteredAndCongested() {
        boolean z;
        int networkType = this.mTelephonyDisplayInfo.getNetworkType();
        int overrideNetworkType = this.mTelephonyDisplayInfo.getOverrideNetworkType();
        boolean z2 = true;
        if (overrideNetworkType == 1 || overrideNetworkType == 2) {
            networkType = 19;
        } else if (overrideNetworkType == 3 || overrideNetworkType == 5) {
            networkType = 20;
        }
        log("updateMeteredAndCongested: networkType=" + TelephonyManager.getNetworkTypeName(networkType));
        boolean isTempNotMeteredSupportedByCarrier = this.mDataConfigManager.isTempNotMeteredSupportedByCarrier();
        boolean z3 = this.mTempNotMeteredSupported;
        boolean z4 = false;
        if (isTempNotMeteredSupportedByCarrier != z3) {
            this.mTempNotMeteredSupported = !z3;
            log("updateMeteredAndCongested: mTempNotMeteredSupported changed to " + this.mTempNotMeteredSupported);
            z = true;
        } else {
            z = false;
        }
        if (this.mDataConfigManager.isNetworkTypeUnmetered(this.mTelephonyDisplayInfo, this.mPhone.getServiceState()) && (this.mDataNetworkController.getUnmeteredOverrideNetworkTypes().contains(Integer.valueOf(networkType)) || isNetworkTypeUnmetered(networkType))) {
            z4 = true;
        }
        if (z4 != this.mTempNotMetered) {
            this.mTempNotMetered = z4;
            log("updateMeteredAndCongested: mTempNotMetered changed to " + this.mTempNotMetered);
            z = true;
        }
        boolean contains = this.mDataNetworkController.getCongestedOverrideNetworkTypes().contains(Integer.valueOf(networkType));
        boolean z5 = this.mCongested;
        if (contains != z5) {
            this.mCongested = !z5;
            log("updateMeteredAndCongested: mCongested changed to " + this.mCongested);
        } else {
            z2 = z;
        }
        if (z2) {
            updateNetworkCapabilities();
        }
        if (this.mTempNotMetered && isInternetSupported()) {
            this.mDataCallSessionStats.onUnmeteredUpdate(networkType);
        }
    }

    private boolean isNetworkTypeUnmetered(int i) {
        List<SubscriptionPlan> subscriptionPlans = this.mDataNetworkController.getSubscriptionPlans();
        if (subscriptionPlans.isEmpty()) {
            return false;
        }
        Set set = (Set) Arrays.stream(TelephonyManager.getAllNetworkTypes()).boxed().collect(Collectors.toSet());
        boolean z = true;
        for (SubscriptionPlan subscriptionPlan : subscriptionPlans) {
            if (((Set) Arrays.stream(subscriptionPlan.getNetworkTypes()).boxed().collect(Collectors.toSet())).containsAll(set)) {
                if (subscriptionPlan.getDataLimitBytes() != Long.MAX_VALUE) {
                    z = false;
                }
            } else if (i != 0) {
                for (int i2 : subscriptionPlan.getNetworkTypes()) {
                    if (i2 == i) {
                        return subscriptionPlan.getDataLimitBytes() == Long.MAX_VALUE;
                    }
                }
                continue;
            } else {
                continue;
            }
        }
        return z;
    }

    public int getId() {
        return this.mCid.get(this.mTransport);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getDataNetworkType() {
        return getDataNetworkType(this.mTransport);
    }

    private int getDataNetworkType(int i) {
        if (i == 2) {
            return 18;
        }
        NetworkRegistrationInfo networkRegistrationInfo = this.mPhone.getServiceState().getNetworkRegistrationInfo(2, i);
        if (networkRegistrationInfo != null) {
            return networkRegistrationInfo.getAccessNetworkTechnology();
        }
        return 0;
    }

    public int getLinkStatus() {
        return this.mLinkStatus;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNetworkScore() {
        int networkScore = getNetworkScore();
        if (networkScore != this.mNetworkScore) {
            logl("Updating score from " + this.mNetworkScore + " to " + networkScore);
            this.mNetworkScore = networkScore;
            this.mNetworkAgent.sendNetworkScore(networkScore);
        }
    }

    private int getNetworkScore() {
        Iterator<TelephonyNetworkRequest> it = this.mAttachedNetworkRequestList.iterator();
        int i = 45;
        while (it.hasNext()) {
            TelephonyNetworkRequest next = it.next();
            if (next.hasCapability(12) && next.getNetworkSpecifier() == null) {
                i = 50;
            }
        }
        return i;
    }

    private NetworkRegistrationInfo getNetworkRegistrationInfo() {
        NetworkRegistrationInfo networkRegistrationInfo = this.mPhone.getServiceStateTracker().getServiceState().getNetworkRegistrationInfo(2, this.mTransport);
        if (networkRegistrationInfo == null) {
            loge("Can't get network registration info for " + AccessNetworkConstants.transportTypeToString(this.mTransport));
            return null;
        }
        return networkRegistrationInfo;
    }

    public int getApnTypeNetworkCapability() {
        if (!this.mAttachedNetworkRequestList.isEmpty()) {
            return this.mAttachedNetworkRequestList.get(0).getApnTypeNetworkCapability();
        }
        Stream<Integer> filter = Arrays.stream(getNetworkCapabilities().getCapabilities()).boxed().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataNetwork$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getApnTypeNetworkCapability$11;
                lambda$getApnTypeNetworkCapability$11 = DataNetwork.lambda$getApnTypeNetworkCapability$11((Integer) obj);
                return lambda$getApnTypeNetworkCapability$11;
            }
        });
        DataConfigManager dataConfigManager = this.mDataConfigManager;
        Objects.requireNonNull(dataConfigManager);
        return filter.max(Comparator.comparingInt(new DataNetwork$$ExternalSyntheticLambda5(dataConfigManager))).orElse(-1).intValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getApnTypeNetworkCapability$11(Integer num) {
        return DataUtils.networkCapabilityToApnType(num.intValue()) != 0;
    }

    public int getPriority() {
        if (!this.mAttachedNetworkRequestList.isEmpty()) {
            return this.mAttachedNetworkRequestList.get(0).getPriority();
        }
        Stream<Integer> boxed = Arrays.stream(getNetworkCapabilities().getCapabilities()).boxed();
        DataConfigManager dataConfigManager = this.mDataConfigManager;
        Objects.requireNonNull(dataConfigManager);
        return ((Integer) boxed.map(new DataNetwork$$ExternalSyntheticLambda0(dataConfigManager)).max(new Comparator() { // from class: com.android.internal.telephony.data.DataNetwork$$ExternalSyntheticLambda1
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return Integer.compare(((Integer) obj).intValue(), ((Integer) obj2).intValue());
            }
        }).orElse(0)).intValue();
    }

    public DataNetworkController.NetworkRequestList getAttachedNetworkRequestList() {
        return this.mAttachedNetworkRequestList;
    }

    public boolean isConnecting() {
        return getCurrentState() == this.mConnectingState;
    }

    public boolean isConnected() {
        return getCurrentState() == this.mConnectedState;
    }

    public boolean isDisconnecting() {
        return getCurrentState() == this.mDisconnectingState;
    }

    public boolean isDisconnected() {
        return getCurrentState() == this.mDisconnectedState;
    }

    public boolean isHandoverInProgress() {
        return getCurrentState() == this.mHandoverState;
    }

    public boolean isSuspended() {
        return getState() == 3;
    }

    public int getTransport() {
        return this.mTransport;
    }

    private int getState() {
        if (getCurrentState() == null || isDisconnected()) {
            return 0;
        }
        if (isConnecting()) {
            return 1;
        }
        if (isConnected()) {
            return this.mSuspended ? 3 : 2;
        } else if (isDisconnecting()) {
            return 4;
        } else {
            return isHandoverInProgress() ? 5 : -1;
        }
    }

    public boolean isInternetSupported() {
        return this.mNetworkCapabilities.hasCapability(12) && this.mNetworkCapabilities.hasCapability(13) && this.mNetworkCapabilities.hasCapability(14) && this.mNetworkCapabilities.hasCapability(15);
    }

    public boolean isEmergencySupl() {
        return this.mDataAllowedReason == DataEvaluation.DataAllowedReason.EMERGENCY_SUPL;
    }

    private PreciseDataConnectionState getPreciseDataConnectionState() {
        return new PreciseDataConnectionState.Builder().setTransportType(this.mTransport).setId(this.mCid.get(this.mTransport)).setState(getState()).setApnSetting(this.mDataProfile.getApnSetting()).setLinkProperties(this.mLinkProperties).setNetworkType(getDataNetworkType()).setFailCause(this.mFailCause).build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyPreciseDataConnectionState() {
        PreciseDataConnectionState preciseDataConnectionState = getPreciseDataConnectionState();
        logv("notifyPreciseDataConnectionState=" + preciseDataConnectionState);
        this.mPhone.notifyDataConnection(preciseDataConnectionState);
    }

    public boolean startHandover(int i, DataRetryManager.DataHandoverRetryEntry dataHandoverRetryEntry) {
        if (getCurrentState() != null && !isDisconnected() && !isDisconnecting()) {
            sendMessage(obtainMessage(25, 0, i, dataHandoverRetryEntry));
            return true;
        }
        if (dataHandoverRetryEntry != null) {
            dataHandoverRetryEntry.setState(4);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onStartHandover(int i, DataRetryManager.DataHandoverRetryEntry dataHandoverRetryEntry) {
        if (this.mTransport == i) {
            log("onStartHandover: The network is already on " + AccessNetworkConstants.transportTypeToString(this.mTransport) + ", handover is not needed.");
            if (dataHandoverRetryEntry != null) {
                dataHandoverRetryEntry.setState(4);
                return;
            }
            return;
        }
        boolean dataRoamingFromRegistration = this.mPhone.getServiceState().getDataRoamingFromRegistration();
        boolean z = this.mPhone.getDataRoamingEnabled() || (dataRoamingFromRegistration && !this.mPhone.getServiceState().getDataRoaming());
        this.mHandoverDataProfile = this.mDataProfile;
        int dataNetworkType = getDataNetworkType(i);
        if (dataNetworkType != 0 && !this.mAttachedNetworkRequestList.isEmpty()) {
            DataProfile dataProfileForNetworkRequest = this.mDataNetworkController.getDataProfileManager().getDataProfileForNetworkRequest(this.mAttachedNetworkRequestList.get(0), dataNetworkType, false);
            if (dataProfileForNetworkRequest != null && dataProfileForNetworkRequest.getApnSetting() != null && this.mDataProfile.getApnSetting() != null && TextUtils.equals(dataProfileForNetworkRequest.getApnSetting().getApnName(), this.mDataProfile.getApnSetting().getApnName()) && !dataProfileForNetworkRequest.equals(this.mDataProfile)) {
                this.mHandoverDataProfile = dataProfileForNetworkRequest;
                log("Used different data profile for handover. " + this.mDataProfile);
            }
        }
        logl("Start handover from " + AccessNetworkConstants.transportTypeToString(this.mTransport) + " to " + AccessNetworkConstants.transportTypeToString(i));
        int networkTypeToAccessNetworkType = DataUtils.networkTypeToAccessNetworkType(getDataNetworkType(i));
        DataProfile dataProfile = this.mHandoverDataProfile;
        this.mDataServiceManagers.get(i).setupDataCall(networkTypeToAccessNetworkType, dataProfile, dataRoamingFromRegistration, z, 3, this.mLinkProperties, this.mPduSessionId, this.mNetworkSliceInfo, dataProfile.getTrafficDescriptor(), true, obtainMessage(15, dataHandoverRetryEntry));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onHandoverResponse(int i, DataCallResponse dataCallResponse, DataRetryManager.DataHandoverRetryEntry dataHandoverRetryEntry) {
        logl("onHandoverResponse: resultCode=" + DataServiceCallback.resultCodeToString(i) + ", response=" + dataCallResponse);
        this.mFailCause = getFailCauseFromDataCallResponse(i, dataCallResponse);
        validateDataCallResponse(dataCallResponse, false);
        if (this.mFailCause == 0) {
            this.mDataServiceManagers.get(this.mTransport).deactivateDataCall(this.mCid.get(this.mTransport), 3, null);
            this.mTransport = DataUtils.getTargetTransport(this.mTransport);
            StringBuilder sb = new StringBuilder();
            sb.append("DN-");
            sb.append(this.mInitialNetworkAgentId);
            sb.append("-");
            sb.append(this.mTransport == 1 ? "C" : "I");
            this.mLogTag = sb.toString();
            this.mDataProfile = this.mHandoverDataProfile;
            updateDataNetwork(dataCallResponse);
            if (this.mTransport != 1) {
                unregisterForWwanEvents();
            } else {
                registerForWwanEvents();
            }
            if (dataHandoverRetryEntry != null) {
                dataHandoverRetryEntry.setState(3);
            }
            this.mDataNetworkCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetwork$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    DataNetwork.this.lambda$onHandoverResponse$12();
                }
            });
        } else {
            this.mDataServiceManagers.get(this.mTransport).cancelHandover(this.mCid.get(this.mTransport), obtainMessage(27));
            final long retryDurationMillis = dataCallResponse != null ? dataCallResponse.getRetryDurationMillis() : -1L;
            final int handoverFailureMode = dataCallResponse != null ? dataCallResponse.getHandoverFailureMode() : -1;
            if (dataHandoverRetryEntry != null) {
                dataHandoverRetryEntry.setState(2);
            }
            this.mDataNetworkCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetwork$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    DataNetwork.this.lambda$onHandoverResponse$13(retryDurationMillis, handoverFailureMode);
                }
            });
            trackHandoverFailure(dataCallResponse != null ? dataCallResponse.getCause() : this.mFailCause);
        }
        transitionTo(this.mConnectedState);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onHandoverResponse$12() {
        this.mDataNetworkCallback.onHandoverSucceeded(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onHandoverResponse$13(long j, int i) {
        this.mDataNetworkCallback.onHandoverFailed(this, this.mFailCause, j, i);
    }

    private void trackHandoverFailure(int i) {
        this.mDataCallSessionStats.onHandoverFailure(i, getDataNetworkType(), getDataNetworkType(DataUtils.getTargetTransport(this.mTransport)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPcoDataChanged(PcoData pcoData) {
        log("onPcoDataChanged: " + pcoData);
        this.mDataNetworkCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetwork$$ExternalSyntheticLambda20
            @Override // java.lang.Runnable
            public final void run() {
                DataNetwork.this.lambda$onPcoDataChanged$14();
            }
        });
        if (this.mDataProfile.getApnSetting() != null) {
            for (Integer num : this.mDataProfile.getApnSetting().getApnTypes()) {
                int intValue = num.intValue();
                Intent intent = new Intent("android.telephony.action.CARRIER_SIGNAL_PCO_VALUE");
                intent.putExtra("android.telephony.extra.APN_TYPE", intValue);
                intent.putExtra("android.telephony.extra.APN_PROTOCOL", ApnSetting.getProtocolIntFromString(pcoData.bearerProto));
                intent.putExtra("android.telephony.extra.PCO_ID", pcoData.pcoId);
                intent.putExtra("android.telephony.extra.PCO_VALUE", pcoData.contents);
                this.mPhone.getCarrierSignalAgent().notifyCarrierSignalReceivers(intent);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onPcoDataChanged$14() {
        this.mDataNetworkCallback.onPcoDataChanged(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPcoDataReceived(PcoData pcoData) {
        log("onPcoDataReceived: " + pcoData);
        PcoData put = this.mPcoData.computeIfAbsent(Integer.valueOf(pcoData.cid), new Function() { // from class: com.android.internal.telephony.data.DataNetwork$$ExternalSyntheticLambda11
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Map lambda$onPcoDataReceived$15;
                lambda$onPcoDataReceived$15 = DataNetwork.lambda$onPcoDataReceived$15((Integer) obj);
                return lambda$onPcoDataReceived$15;
            }
        }).put(Integer.valueOf(pcoData.pcoId), pcoData);
        if (getId() == -1 || pcoData.cid != getId() || Objects.equals(put, pcoData)) {
            return;
        }
        onPcoDataChanged(pcoData);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Map lambda$onPcoDataReceived$15(Integer num) {
        return new ArrayMap();
    }

    public int getLastKnownDataNetworkType() {
        return this.mLastKnownDataNetworkType;
    }

    public Map<Integer, PcoData> getPcoData() {
        int i = this.mTransport;
        if (i == 2 || this.mCid.get(i) == -1) {
            return Collections.emptyMap();
        }
        return this.mPcoData.getOrDefault(Integer.valueOf(this.mCid.get(this.mTransport)), Collections.emptyMap());
    }

    private VcnNetworkPolicyResult getVcnPolicy(NetworkCapabilities networkCapabilities) {
        VcnManager vcnManager = this.mVcnManager;
        if (vcnManager == null) {
            return null;
        }
        return vcnManager.applyVcnNetworkPolicy(networkCapabilities, getLinkProperties());
    }

    public boolean hasNetworkCapabilityInNetworkRequests(final int i) {
        return this.mAttachedNetworkRequestList.stream().anyMatch(new Predicate() { // from class: com.android.internal.telephony.data.DataNetwork$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean hasCapability;
                hasCapability = ((TelephonyNetworkRequest) obj).hasCapability(i);
                return hasCapability;
            }
        });
    }

    public static String tearDownReasonToString(int i) {
        switch (i) {
            case 0:
                return "NONE";
            case 1:
                return "CONNECTIVITY_SERVICE_UNWANTED";
            case 2:
                return "SIM_REMOVAL";
            case 3:
                return "AIRPLANE_MODE_ON";
            case 4:
                return "DATA_DISABLED";
            case 5:
                return "TEAR_DOWN_REASON_NO_LIVE_REQUEST";
            case 6:
                return "TEAR_DOWN_REASON_RAT_NOT_ALLOWED";
            case 7:
                return "TEAR_DOWN_REASON_ROAMING_DISABLED";
            case 8:
                return "TEAR_DOWN_REASON_CONCURRENT_VOICE_DATA_NOT_ALLOWED";
            case 9:
            default:
                return "UNKNOWN(" + i + ")";
            case 10:
                return "TEAR_DOWN_REASON_DATA_SERVICE_NOT_READY";
            case 11:
                return "TEAR_DOWN_REASON_POWER_OFF_BY_CARRIER";
            case 12:
                return "TEAR_DOWN_REASON_DATA_STALL";
            case 13:
                return "TEAR_DOWN_REASON_HANDOVER_FAILED";
            case 14:
                return "TEAR_DOWN_REASON_HANDOVER_NOT_ALLOWED";
            case 15:
                return "TEAR_DOWN_REASON_VCN_REQUESTED";
            case 16:
                return "TEAR_DOWN_REASON_VOPS_NOT_SUPPORTED";
            case 17:
                return "TEAR_DOWN_REASON_DEFAULT_DATA_UNSELECTED";
            case 18:
                return "TEAR_DOWN_REASON_NOT_IN_SERVICE";
            case 19:
                return "TEAR_DOWN_REASON_DATA_CONFIG_NOT_READY";
            case 20:
                return "TEAR_DOWN_REASON_PENDING_TEAR_DOWN_ALL";
            case 21:
                return "TEAR_DOWN_REASON_NO_SUITABLE_DATA_PROFILE";
            case 22:
                return "TEAR_DOWN_REASON_CDMA_EMERGENCY_CALLBACK_MODE";
            case 23:
                return "TEAR_DOWN_REASON_RETRY_SCHEDULED";
            case 24:
                return "TEAR_DOWN_REASON_DATA_THROTTLED";
            case 25:
                return "TEAR_DOWN_REASON_DATA_PROFILE_INVALID";
            case 26:
                return "TEAR_DOWN_REASON_DATA_PROFILE_NOT_PREFERRED";
            case 27:
                return "TEAR_DOWN_REASON_NOT_ALLOWED_BY_POLICY";
            case 28:
                return "TEAR_DOWN_REASON_ILLEGAL_STATE";
            case 29:
                return "TEAR_DOWN_REASON_ONLY_ALLOWED_SINGLE_NETWORK";
            case 30:
                return "TEAR_DOWN_REASON_PREFERRED_DATA_SWITCHED";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String eventToString(int i) {
        switch (i) {
            case 1:
                return "EVENT_DATA_CONFIG_UPDATED";
            case 2:
                return "EVENT_ATTACH_NETWORK_REQUEST";
            case 3:
                return "EVENT_DETACH_NETWORK_REQUEST";
            case 4:
                return "EVENT_RADIO_NOT_AVAILABLE";
            case 5:
                return "EVENT_ALLOCATE_PDU_SESSION_ID_RESPONSE";
            case 6:
                return "EVENT_SETUP_DATA_NETWORK_RESPONSE";
            case 7:
                return "EVENT_TEAR_DOWN_NETWORK";
            case 8:
                return "EVENT_DATA_STATE_CHANGED";
            case 9:
                return "EVENT_DATA_NETWORK_TYPE_REG_STATE_CHANGED";
            case 10:
                return "EVENT_DETACH_ALL_NETWORK_REQUESTS";
            case 11:
                return "EVENT_BANDWIDTH_ESTIMATE_FROM_MODEM_CHANGED";
            case 12:
            case 14:
            default:
                return "Unknown(" + i + ")";
            case 13:
                return "EVENT_DISPLAY_INFO_CHANGED";
            case 15:
                return "EVENT_HANDOVER_RESPONSE";
            case 16:
                return "EVENT_SUBSCRIPTION_PLAN_OVERRIDE";
            case 17:
                return "EVENT_PCO_DATA_RECEIVED";
            case 18:
                return "EVENT_CARRIER_PRIVILEGED_UIDS_CHANGED";
            case 19:
                return "EVENT_DEACTIVATE_DATA_NETWORK_RESPONSE";
            case 20:
                return "EVENT_STUCK_IN_TRANSIENT_STATE";
            case 21:
                return "EVENT_WAITING_FOR_TEARING_DOWN_CONDITION_MET";
            case 22:
                return "EVENT_VOICE_CALL_STARTED";
            case 23:
                return "EVENT_VOICE_CALL_ENDED";
            case 24:
                return "EVENT_CSS_INDICATOR_CHANGED";
            case 25:
                return "EVENT_NOTIFY_HANDOVER_STARTED";
            case 26:
                return "EVENT_NOTIFY_HANDOVER_STARTED_RESPONSE";
            case 27:
                return "EVENT_NOTIFY_HANDOVER_CANCELLED_RESPONSE";
        }
    }

    @Override // com.android.internal.telephony.StateMachine
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[DataNetwork: ");
        sb.append(this.mLogTag);
        sb.append(", ");
        sb.append(this.mDataProfile.getApnSetting() != null ? this.mDataProfile.getApnSetting().getApnName() : null);
        sb.append(", state=");
        sb.append(getCurrentState() != null ? getCurrentState().getName() : null);
        sb.append("]");
        return sb.toString();
    }

    public String name() {
        return this.mLogTag;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reportAnomaly(String str, String str2) {
        logl(str);
        AnomalyReporter.reportAnomaly(UUID.fromString(str2), str, this.mPhone.getCarrierId());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.StateMachine
    public void log(String str) {
        String str2;
        String str3 = this.mLogTag;
        StringBuilder sb = new StringBuilder();
        if (getCurrentState() != null) {
            str2 = getCurrentState().getName() + ": ";
        } else {
            str2 = PhoneConfigurationManager.SSSS;
        }
        sb.append(str2);
        sb.append(str);
        Rlog.d(str3, sb.toString());
    }

    @Override // com.android.internal.telephony.StateMachine
    protected void loge(String str) {
        String str2;
        String str3 = this.mLogTag;
        StringBuilder sb = new StringBuilder();
        if (getCurrentState() != null) {
            str2 = getCurrentState().getName() + ": ";
        } else {
            str2 = PhoneConfigurationManager.SSSS;
        }
        sb.append(str2);
        sb.append(str);
        Rlog.e(str3, sb.toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logl(String str) {
        String str2;
        log(str);
        LocalLog localLog = this.mLocalLog;
        StringBuilder sb = new StringBuilder();
        if (getCurrentState() != null) {
            str2 = getCurrentState().getName() + ": ";
        } else {
            str2 = PhoneConfigurationManager.SSSS;
        }
        sb.append(str2);
        sb.append(str);
        localLog.log(sb.toString());
    }

    @Override // com.android.internal.telephony.StateMachine
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter = new AndroidUtilIndentingPrintWriter(printWriter, "  ");
        super.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        androidUtilIndentingPrintWriter.println("Tag: " + name());
        androidUtilIndentingPrintWriter.increaseIndent();
        androidUtilIndentingPrintWriter.println("mSubId=" + this.mSubId);
        androidUtilIndentingPrintWriter.println("mTransport=" + AccessNetworkConstants.transportTypeToString(this.mTransport));
        androidUtilIndentingPrintWriter.println("mLastKnownDataNetworkType=" + TelephonyManager.getNetworkTypeName(this.mLastKnownDataNetworkType));
        androidUtilIndentingPrintWriter.println("WWAN cid=" + this.mCid.get(1));
        androidUtilIndentingPrintWriter.println("WLAN cid=" + this.mCid.get(2));
        androidUtilIndentingPrintWriter.println("mNetworkScore=" + this.mNetworkScore);
        androidUtilIndentingPrintWriter.println("mDataAllowedReason=" + this.mDataAllowedReason);
        androidUtilIndentingPrintWriter.println("mPduSessionId=" + this.mPduSessionId);
        androidUtilIndentingPrintWriter.println("mDataProfile=" + this.mDataProfile);
        androidUtilIndentingPrintWriter.println("mNetworkCapabilities=" + this.mNetworkCapabilities);
        androidUtilIndentingPrintWriter.println("mLinkProperties=" + this.mLinkProperties);
        androidUtilIndentingPrintWriter.println("mNetworkSliceInfo=" + this.mNetworkSliceInfo);
        androidUtilIndentingPrintWriter.println("mNetworkBandwidth=" + this.mNetworkBandwidth);
        androidUtilIndentingPrintWriter.println("mTcpBufferSizes=" + this.mTcpBufferSizes);
        androidUtilIndentingPrintWriter.println("mTelephonyDisplayInfo=" + this.mTelephonyDisplayInfo);
        androidUtilIndentingPrintWriter.println("mTempNotMeteredSupported=" + this.mTempNotMeteredSupported);
        androidUtilIndentingPrintWriter.println("mTempNotMetered=" + this.mTempNotMetered);
        androidUtilIndentingPrintWriter.println("mCongested=" + this.mCongested);
        androidUtilIndentingPrintWriter.println("mSuspended=" + this.mSuspended);
        androidUtilIndentingPrintWriter.println("mDataCallResponse=" + this.mDataCallResponse);
        androidUtilIndentingPrintWriter.println("mFailCause=" + DataFailCause.toString(this.mFailCause));
        androidUtilIndentingPrintWriter.println("mAdministratorUids=" + Arrays.toString(this.mAdministratorUids));
        androidUtilIndentingPrintWriter.println("mCarrierServicePackageUid=" + this.mCarrierServicePackageUid);
        androidUtilIndentingPrintWriter.println("mEverConnected=" + this.mEverConnected);
        androidUtilIndentingPrintWriter.println("mInvokedDataDeactivation=" + this.mInvokedDataDeactivation);
        androidUtilIndentingPrintWriter.println("Attached network requests:");
        androidUtilIndentingPrintWriter.increaseIndent();
        Iterator<TelephonyNetworkRequest> it = this.mAttachedNetworkRequestList.iterator();
        while (it.hasNext()) {
            androidUtilIndentingPrintWriter.println(it.next());
        }
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println("mQosBearerSessions=" + this.mQosBearerSessions);
        this.mNetworkAgent.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        androidUtilIndentingPrintWriter.println("Local logs:");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mLocalLog.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println("---------------");
    }
}
