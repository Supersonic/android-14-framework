package com.android.internal.telephony.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkCapabilities;
import android.net.NetworkPolicyManager;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.AccessNetworkConstants;
import android.telephony.AnomalyReporter;
import android.telephony.DataFailCause;
import android.telephony.DataSpecificRegistrationInfo;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.PcoData;
import android.telephony.ServiceState;
import android.telephony.SubscriptionManager;
import android.telephony.SubscriptionPlan;
import android.telephony.TelephonyManager;
import android.telephony.TelephonyRegistryManager;
import android.telephony.data.DataCallResponse;
import android.telephony.data.DataProfile;
import android.telephony.ims.ImsException;
import android.telephony.ims.ImsManager;
import android.telephony.ims.ImsReasonInfo;
import android.telephony.ims.ImsRegistrationAttributes;
import android.telephony.ims.ImsStateCallback;
import android.telephony.ims.RegistrationManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.AndroidUtilIndentingPrintWriter;
import com.android.internal.telephony.LocalLog;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.SlidingWindowEventCounter;
import com.android.internal.telephony.TelephonyComponentFactory;
import com.android.internal.telephony.data.AccessNetworksManager;
import com.android.internal.telephony.data.DataConfigManager;
import com.android.internal.telephony.data.DataEvaluation;
import com.android.internal.telephony.data.DataNetwork;
import com.android.internal.telephony.data.DataNetworkController;
import com.android.internal.telephony.data.DataProfileManager;
import com.android.internal.telephony.data.DataRetryManager;
import com.android.internal.telephony.data.DataSettingsManager;
import com.android.internal.telephony.data.DataStallRecoveryManager;
import com.android.internal.telephony.data.LinkBandwidthEstimator;
import com.android.internal.telephony.ims.ImsResolver;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
/* loaded from: classes.dex */
public class DataNetworkController extends Handler {

    /* renamed from: REEVALUATE_UNSATISFIED_NETWORK_REQUESTS_AFTER_CALL_END_DELAY_MILLIS */
    private static final long f10x94a23eb3;
    private static final long REEVALUATE_UNSATISFIED_NETWORK_REQUESTS_TAC_CHANGED_DELAY_MILLIS;
    private final AccessNetworksManager mAccessNetworksManager;
    private final NetworkRequestList mAllNetworkRequestList;
    private boolean mAnyDataNetworkExisting;
    private final Set<Integer> mCongestedOverrideNetworkTypes;
    private int mDataActivity;
    private final DataConfigManager mDataConfigManager;
    private final Set<DataNetworkControllerCallback> mDataNetworkControllerCallbacks;
    private final List<DataNetwork> mDataNetworkList;
    private final DataProfileManager mDataProfileManager;
    private final DataRetryManager mDataRetryManager;
    private final SparseBooleanArray mDataServiceBound;
    private final SparseArray<DataServiceManager> mDataServiceManagers;
    private final DataSettingsManager mDataSettingsManager;
    private final DataStallRecoveryManager mDataStallRecoveryManager;
    private int mImsDataNetworkState;
    private final SparseArray<String> mImsFeaturePackageName;
    private final SparseArray<RegistrationManager.RegistrationCallback> mImsFeatureRegistrationCallbacks;
    private final ImsManager mImsManager;
    private final SparseArray<ImsStateCallback> mImsStateCallbacks;
    private SlidingWindowEventCounter mImsThrottleCounter;
    private final BroadcastReceiver mIntentReceiver;
    private int mInternetDataNetworkState;
    private int mInternetLinkStatus;
    private boolean mIsSrvccHandoverInProcess;
    private boolean mLastImsOperationIsRelease;
    private int[] mLastReleasedImsRequestCapabilities;
    private final LocalLog mLocalLog;
    private final String mLogTag;
    private final NetworkPolicyManager mNetworkPolicyManager;
    private SlidingWindowEventCounter mNetworkUnwantedCounter;
    private boolean mNrAdvancedCapableByPco;
    private final Map<DataNetwork, Runnable> mPendingImsDeregDataNetworks;
    private boolean mPendingTearDownAllNetworks;
    private final Phone mPhone;
    private final List<DataNetwork> mPreviousConnectedDataNetworkList;
    private boolean mPsRestricted;
    private final Set<Integer> mRegisteredImsFeatures;
    private ServiceState mServiceState;
    private SlidingWindowEventCounter mSetupDataCallWlanFailureCounter;
    private SlidingWindowEventCounter mSetupDataCallWwanFailureCounter;
    @TelephonyManager.SimState
    private int mSimState;
    private int mSubId;
    private final List<SubscriptionPlan> mSubscriptionPlans;
    private final Set<Integer> mUnmeteredOverrideNetworkTypes;
    private static final Collection<Integer> SUPPORTED_IMS_FEATURES = List.of(1, 2);
    private static final long REEVALUATE_PREFERRED_TRANSPORT_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(3);

    private void logv(String str) {
    }

    static {
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        f10x94a23eb3 = timeUnit.toMillis(500L);
        REEVALUATE_UNSATISFIED_NETWORK_REQUESTS_TAC_CHANGED_DELAY_MILLIS = timeUnit.toMillis(100L);
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class NetworkRequestList extends LinkedList<TelephonyNetworkRequest> {
        public NetworkRequestList() {
        }

        public NetworkRequestList(NetworkRequestList networkRequestList) {
            addAll(networkRequestList);
        }

        public NetworkRequestList(List<TelephonyNetworkRequest> list) {
            addAll(list);
        }

        public NetworkRequestList(TelephonyNetworkRequest telephonyNetworkRequest) {
            this();
            add(telephonyNetworkRequest);
        }

        @Override // java.util.LinkedList, java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List, java.util.Deque, java.util.Queue
        public boolean add(TelephonyNetworkRequest telephonyNetworkRequest) {
            int i = 0;
            while (i < size()) {
                TelephonyNetworkRequest telephonyNetworkRequest2 = get(i);
                if (telephonyNetworkRequest2.equals(telephonyNetworkRequest)) {
                    return false;
                }
                if (telephonyNetworkRequest.getPriority() > telephonyNetworkRequest2.getPriority()) {
                    break;
                }
                i++;
            }
            super.add(i, (int) telephonyNetworkRequest);
            return true;
        }

        @Override // java.util.LinkedList, java.util.AbstractSequentialList, java.util.AbstractList, java.util.List
        public void add(int i, TelephonyNetworkRequest telephonyNetworkRequest) {
            throw new UnsupportedOperationException("Insertion to certain position is illegal.");
        }

        @Override // java.util.LinkedList, java.util.AbstractCollection, java.util.Collection, java.util.List
        public boolean addAll(Collection<? extends TelephonyNetworkRequest> collection) {
            for (TelephonyNetworkRequest telephonyNetworkRequest : collection) {
                add(telephonyNetworkRequest);
            }
            return true;
        }

        public TelephonyNetworkRequest get(int[] iArr) {
            for (int i = 0; i < size(); i++) {
                TelephonyNetworkRequest telephonyNetworkRequest = get(i);
                if (((Set) Arrays.stream(telephonyNetworkRequest.getCapabilities()).boxed().collect(Collectors.toSet())).containsAll((Collection) Arrays.stream(iArr).boxed().collect(Collectors.toList()))) {
                    return telephonyNetworkRequest;
                }
            }
            return null;
        }

        public boolean hasNetworkRequestsFromPackage(String str) {
            Iterator<TelephonyNetworkRequest> it = iterator();
            while (it.hasNext()) {
                if (str.equals(it.next().getNativeNetworkRequest().getRequestorPackageName())) {
                    return true;
                }
            }
            return false;
        }

        @Override // java.util.AbstractCollection
        public String toString() {
            String str;
            StringBuilder sb = new StringBuilder();
            sb.append("[NetworkRequestList: size=");
            sb.append(size());
            if (size() > 0) {
                str = ", leading by " + get(0);
            } else {
                str = PhoneConfigurationManager.SSSS;
            }
            sb.append(str);
            sb.append("]");
            return sb.toString();
        }

        public void dump(AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter) {
            androidUtilIndentingPrintWriter.increaseIndent();
            Iterator<TelephonyNetworkRequest> it = iterator();
            while (it.hasNext()) {
                androidUtilIndentingPrintWriter.println(it.next());
            }
            androidUtilIndentingPrintWriter.decreaseIndent();
        }
    }

    /* loaded from: classes.dex */
    public static class DataNetworkControllerCallback extends DataCallback {
        public void onAnyDataNetworkExistingChanged(boolean z) {
        }

        public void onDataNetworkConnected(int i, DataProfile dataProfile) {
        }

        public void onDataServiceBound(int i) {
        }

        public void onInternetDataNetworkConnected(List<DataProfile> list) {
        }

        public void onInternetDataNetworkDisconnected() {
        }

        public void onInternetDataNetworkValidationStatusChanged(int i) {
        }

        public void onNrAdvancedCapableByPcoChanged(boolean z) {
        }

        public void onPhysicalLinkStatusChanged(int i) {
        }

        public void onSubscriptionPlanOverride() {
        }

        public DataNetworkControllerCallback(Executor executor) {
            super(executor);
        }
    }

    /* loaded from: classes.dex */
    public static class HandoverRule {
        public static final int RULE_TYPE_ALLOWED = 1;
        public static final int RULE_TYPE_DISALLOWED = 2;
        public final boolean isOnlyForRoaming;
        public final Set<Integer> networkCapabilities;
        public final Set<Integer> sourceAccessNetworks;
        public final Set<Integer> targetAccessNetworks;
        public final int type;

        public HandoverRule(String str) {
            char c;
            if (TextUtils.isEmpty(str)) {
                throw new IllegalArgumentException("illegal rule " + str);
            }
            Set<Integer> emptySet = Collections.emptySet();
            String lowerCase = str.trim().toLowerCase(Locale.ROOT);
            String[] split = lowerCase.split("\\s*,\\s*");
            int length = split.length;
            Set<Integer> set = null;
            char c2 = 0;
            Set<Integer> set2 = null;
            int i = 0;
            int i2 = 0;
            boolean z = false;
            while (i < length) {
                String[] split2 = split[i].trim().split("\\s*=\\s*");
                if (split2.length != 2) {
                    throw new IllegalArgumentException("illegal rule " + lowerCase + ", tokens=" + Arrays.toString(split2));
                }
                String trim = split2[c2].trim();
                String trim2 = split2[1].trim();
                try {
                    switch (trim.hashCode()) {
                        case -1487597642:
                            if (trim.equals("capabilities")) {
                                c = 3;
                                break;
                            }
                            c = 65535;
                            break;
                        case -896505829:
                            if (trim.equals("source")) {
                                c = 0;
                                break;
                            }
                            c = 65535;
                            break;
                        case -880905839:
                            if (trim.equals("target")) {
                                c = 1;
                                break;
                            }
                            c = 65535;
                            break;
                        case 3575610:
                            if (trim.equals("type")) {
                                c = 2;
                                break;
                            }
                            c = 65535;
                            break;
                        case 1366973465:
                            if (trim.equals("roaming")) {
                                c = 4;
                                break;
                            }
                            c = 65535;
                            break;
                        default:
                            c = 65535;
                            break;
                    }
                    if (c == 0) {
                        set = (Set) Arrays.stream(trim2.split("\\s*\\|\\s*")).map(new DataNetworkController$HandoverRule$$ExternalSyntheticLambda0()).map(new Function() { // from class: com.android.internal.telephony.data.DataNetworkController$HandoverRule$$ExternalSyntheticLambda1
                            @Override // java.util.function.Function
                            public final Object apply(Object obj) {
                                return Integer.valueOf(AccessNetworkConstants.AccessNetworkType.fromString((String) obj));
                            }
                        }).collect(Collectors.toSet());
                    } else if (c == 1) {
                        set2 = (Set) Arrays.stream(trim2.split("\\s*\\|\\s*")).map(new DataNetworkController$HandoverRule$$ExternalSyntheticLambda0()).map(new Function() { // from class: com.android.internal.telephony.data.DataNetworkController$HandoverRule$$ExternalSyntheticLambda1
                            @Override // java.util.function.Function
                            public final Object apply(Object obj) {
                                return Integer.valueOf(AccessNetworkConstants.AccessNetworkType.fromString((String) obj));
                            }
                        }).collect(Collectors.toSet());
                    } else if (c == 2) {
                        Locale locale = Locale.ROOT;
                        if (trim2.toLowerCase(locale).equals("allowed")) {
                            i2 = 1;
                        } else if (!trim2.toLowerCase(locale).equals("disallowed")) {
                            throw new IllegalArgumentException("unexpected rule type " + trim2);
                        } else {
                            i2 = 2;
                        }
                    } else if (c == 3) {
                        emptySet = DataUtils.getNetworkCapabilitiesFromString(trim2);
                    } else if (c == 4) {
                        z = Boolean.parseBoolean(trim2);
                    } else {
                        throw new IllegalArgumentException("unexpected key " + trim);
                    }
                    i++;
                    c2 = 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException("illegal rule \"" + lowerCase + "\", e=" + e);
                }
            }
            if (set == null || set2 == null || set.isEmpty() || set2.isEmpty()) {
                throw new IllegalArgumentException("Need to specify both source and target. \"" + lowerCase + "\"");
            } else if (set.contains(0) && i2 != 2) {
                throw new IllegalArgumentException("Unknown access network can be only specified in the disallowed rule. \"" + lowerCase + "\"");
            } else if (set2.contains(0)) {
                throw new IllegalArgumentException("Target access networks contains unknown. \"" + lowerCase + "\"");
            } else if (i2 == 0) {
                throw new IllegalArgumentException("Rule type is not specified correctly. \"" + lowerCase + "\"");
            } else if (emptySet != null && emptySet.contains(-1)) {
                throw new IllegalArgumentException("Network capabilities contains unknown. \"" + lowerCase + "\"");
            } else if (!set.contains(5) && !set2.contains(5)) {
                throw new IllegalArgumentException("IWLAN must be specified in either source or target access networks.\"" + lowerCase + "\"");
            } else {
                this.sourceAccessNetworks = set;
                this.targetAccessNetworks = set2;
                this.type = i2;
                this.networkCapabilities = emptySet;
                this.isOnlyForRoaming = z;
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[HandoverRule: type=");
            sb.append(this.type == 1 ? "allowed" : "disallowed");
            sb.append(", source=");
            sb.append((String) this.sourceAccessNetworks.stream().map(new Function() { // from class: com.android.internal.telephony.data.DataNetworkController$HandoverRule$$ExternalSyntheticLambda2
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return AccessNetworkConstants.AccessNetworkType.toString(((Integer) obj).intValue());
                }
            }).collect(Collectors.joining("|")));
            sb.append(", target=");
            sb.append((String) this.targetAccessNetworks.stream().map(new Function() { // from class: com.android.internal.telephony.data.DataNetworkController$HandoverRule$$ExternalSyntheticLambda2
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return AccessNetworkConstants.AccessNetworkType.toString(((Integer) obj).intValue());
                }
            }).collect(Collectors.joining("|")));
            sb.append(", isRoaming=");
            sb.append(this.isOnlyForRoaming);
            sb.append(", capabilities=");
            sb.append(DataUtils.networkCapabilitiesToString(this.networkCapabilities));
            sb.append("]");
            return sb.toString();
        }
    }

    public DataNetworkController(Phone phone, Looper looper) {
        super(looper);
        int[] availableTransports;
        this.mLocalLog = new LocalLog(128);
        this.mDataServiceManagers = new SparseArray<>();
        this.mSubId = -1;
        this.mSubscriptionPlans = new ArrayList();
        this.mUnmeteredOverrideNetworkTypes = new ArraySet();
        this.mCongestedOverrideNetworkTypes = new ArraySet();
        this.mAllNetworkRequestList = new NetworkRequestList();
        this.mDataNetworkList = new ArrayList();
        this.mPreviousConnectedDataNetworkList = new ArrayList();
        this.mInternetDataNetworkState = 0;
        this.mImsDataNetworkState = 0;
        this.mInternetLinkStatus = -1;
        this.mDataNetworkControllerCallbacks = new ArraySet();
        this.mPsRestricted = false;
        this.mNrAdvancedCapableByPco = false;
        this.mIsSrvccHandoverInProcess = false;
        this.mDataServiceBound = new SparseBooleanArray();
        this.mSimState = 0;
        this.mDataActivity = 0;
        this.mImsStateCallbacks = new SparseArray<>();
        this.mRegisteredImsFeatures = new ArraySet();
        this.mImsFeaturePackageName = new SparseArray<>();
        this.mPendingImsDeregDataNetworks = new ArrayMap();
        this.mImsFeatureRegistrationCallbacks = new SparseArray<>();
        this.mPendingTearDownAllNetworks = false;
        this.mIntentReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.data.DataNetworkController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                action.hashCode();
                if ((action.equals("android.telephony.action.SIM_APPLICATION_STATE_CHANGED") || action.equals("android.telephony.action.SIM_CARD_STATE_CHANGED")) && DataNetworkController.this.mPhone.getPhoneId() == intent.getIntExtra("android.telephony.extra.SLOT_INDEX", -1)) {
                    int intExtra = intent.getIntExtra("android.telephony.extra.SIM_STATE", 0);
                    DataNetworkController dataNetworkController = DataNetworkController.this;
                    dataNetworkController.sendMessage(dataNetworkController.obtainMessage(9, intExtra, 0));
                }
            }
        };
        this.mPhone = phone;
        this.mLogTag = "DNC-" + phone.getPhoneId();
        log("DataNetworkController created.");
        AccessNetworksManager accessNetworksManager = phone.getAccessNetworksManager();
        this.mAccessNetworksManager = accessNetworksManager;
        for (int i : accessNetworksManager.getAvailableTransports()) {
            this.mDataServiceManagers.put(i, new DataServiceManager(this.mPhone, looper, i));
        }
        DataConfigManager dataConfigManager = new DataConfigManager(this.mPhone, looper);
        this.mDataConfigManager = dataConfigManager;
        this.mImsThrottleCounter = new SlidingWindowEventCounter(dataConfigManager.getAnomalyImsReleaseRequestThreshold().timeWindow, dataConfigManager.getAnomalyImsReleaseRequestThreshold().eventNumOccurrence);
        this.mNetworkUnwantedCounter = new SlidingWindowEventCounter(dataConfigManager.getAnomalyNetworkUnwantedThreshold().timeWindow, dataConfigManager.getAnomalyNetworkUnwantedThreshold().eventNumOccurrence);
        this.mSetupDataCallWlanFailureCounter = new SlidingWindowEventCounter(dataConfigManager.getAnomalySetupDataCallThreshold().timeWindow, dataConfigManager.getAnomalySetupDataCallThreshold().eventNumOccurrence);
        this.mSetupDataCallWwanFailureCounter = new SlidingWindowEventCounter(dataConfigManager.getAnomalySetupDataCallThreshold().timeWindow, dataConfigManager.getAnomalySetupDataCallThreshold().eventNumOccurrence);
        this.mDataSettingsManager = TelephonyComponentFactory.getInstance().inject(DataSettingsManager.class.getName()).makeDataSettingsManager(this.mPhone, this, looper, new DataSettingsManager.DataSettingsManagerCallback(new DataNetworkController$$ExternalSyntheticLambda8(this)) { // from class: com.android.internal.telephony.data.DataNetworkController.2
            @Override // com.android.internal.telephony.data.DataSettingsManager.DataSettingsManagerCallback
            public void onDataEnabledChanged(boolean z, int i2, String str) {
                DataNetworkController dataNetworkController = DataNetworkController.this;
                dataNetworkController.logl("onDataEnabledChanged: enabled=" + z);
                DataNetworkController dataNetworkController2 = DataNetworkController.this;
                dataNetworkController2.sendMessage(dataNetworkController2.obtainMessage(z ? 5 : 16, DataEvaluation.DataEvaluationReason.DATA_ENABLED_CHANGED));
            }

            @Override // com.android.internal.telephony.data.DataSettingsManager.DataSettingsManagerCallback
            public void onDataEnabledOverrideChanged(boolean z, int i2) {
                DataNetworkController dataNetworkController = DataNetworkController.this;
                dataNetworkController.logl("onDataEnabledOverrideChanged: enabled=" + z);
                DataNetworkController dataNetworkController2 = DataNetworkController.this;
                dataNetworkController2.sendMessage(dataNetworkController2.obtainMessage(z ? 5 : 16, DataEvaluation.DataEvaluationReason.DATA_ENABLED_OVERRIDE_CHANGED));
            }

            @Override // com.android.internal.telephony.data.DataSettingsManager.DataSettingsManagerCallback
            public void onDataRoamingEnabledChanged(boolean z) {
                DataNetworkController dataNetworkController = DataNetworkController.this;
                dataNetworkController.logl("onDataRoamingEnabledChanged: enabled=" + z);
                DataNetworkController dataNetworkController2 = DataNetworkController.this;
                dataNetworkController2.sendMessage(dataNetworkController2.obtainMessage(z ? 5 : 16, DataEvaluation.DataEvaluationReason.ROAMING_ENABLED_CHANGED));
            }
        });
        this.mDataProfileManager = TelephonyComponentFactory.getInstance().inject(DataProfileManager.class.getName()).makeDataProfileManager(this.mPhone, this, this.mDataServiceManagers.get(1), looper, new DataProfileManager.DataProfileManagerCallback(new DataNetworkController$$ExternalSyntheticLambda8(this)) { // from class: com.android.internal.telephony.data.DataNetworkController.3
            @Override // com.android.internal.telephony.data.DataProfileManager.DataProfileManagerCallback
            public void onDataProfilesChanged() {
                DataNetworkController dataNetworkController = DataNetworkController.this;
                DataEvaluation.DataEvaluationReason dataEvaluationReason = DataEvaluation.DataEvaluationReason.DATA_PROFILES_CHANGED;
                dataNetworkController.sendMessage(dataNetworkController.obtainMessage(16, dataEvaluationReason));
                DataNetworkController dataNetworkController2 = DataNetworkController.this;
                dataNetworkController2.sendMessage(dataNetworkController2.obtainMessage(5, dataEvaluationReason));
            }
        });
        this.mDataStallRecoveryManager = new DataStallRecoveryManager(this.mPhone, this, this.mDataServiceManagers.get(1), looper, new DataStallRecoveryManager.DataStallRecoveryManagerCallback(new DataNetworkController$$ExternalSyntheticLambda8(this)) { // from class: com.android.internal.telephony.data.DataNetworkController.4
            @Override // com.android.internal.telephony.data.DataStallRecoveryManager.DataStallRecoveryManagerCallback
            public void onDataStallReestablishInternet() {
                DataNetworkController.this.onDataStallReestablishInternet();
            }
        });
        this.mDataRetryManager = new DataRetryManager(this.mPhone, this, this.mDataServiceManagers, looper, new DataRetryManager.DataRetryManagerCallback(new DataNetworkController$$ExternalSyntheticLambda8(this)) { // from class: com.android.internal.telephony.data.DataNetworkController.5
            @Override // com.android.internal.telephony.data.DataRetryManager.DataRetryManagerCallback
            public void onDataNetworkSetupRetry(DataRetryManager.DataSetupRetryEntry dataSetupRetryEntry) {
                Objects.requireNonNull(dataSetupRetryEntry);
                DataNetworkController.this.onDataNetworkSetupRetry(dataSetupRetryEntry);
            }

            @Override // com.android.internal.telephony.data.DataRetryManager.DataRetryManagerCallback
            public void onDataNetworkHandoverRetry(DataRetryManager.DataHandoverRetryEntry dataHandoverRetryEntry) {
                Objects.requireNonNull(dataHandoverRetryEntry);
                DataNetworkController.this.onDataNetworkHandoverRetry(dataHandoverRetryEntry);
            }

            @Override // com.android.internal.telephony.data.DataRetryManager.DataRetryManagerCallback
            public void onDataNetworkHandoverRetryStopped(DataNetwork dataNetwork) {
                Objects.requireNonNull(dataNetwork);
                DataNetworkController.this.onDataNetworkHandoverRetryStopped(dataNetwork);
            }
        });
        this.mImsManager = (ImsManager) this.mPhone.getContext().getSystemService(ImsManager.class);
        this.mNetworkPolicyManager = (NetworkPolicyManager) this.mPhone.getContext().getSystemService(NetworkPolicyManager.class);
        this.mServiceState = this.mPhone.getServiceStateTracker().getServiceState();
        sendEmptyMessage(19);
    }

    private void onRegisterAllEvents() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.telephony.action.SIM_CARD_STATE_CHANGED");
        intentFilter.addAction("android.telephony.action.SIM_APPLICATION_STATE_CHANGED");
        this.mPhone.getContext().registerReceiver(this.mIntentReceiver, intentFilter, null, this.mPhone);
        this.mAccessNetworksManager.registerCallback(new AccessNetworksManager.AccessNetworksManagerCallback(new DataNetworkController$$ExternalSyntheticLambda8(this)) { // from class: com.android.internal.telephony.data.DataNetworkController.6
            @Override // com.android.internal.telephony.data.AccessNetworksManager.AccessNetworksManagerCallback
            public void onPreferredTransportChanged(int i) {
                int preferredTransportByNetworkCapability = DataNetworkController.this.mAccessNetworksManager.getPreferredTransportByNetworkCapability(i);
                DataNetworkController dataNetworkController = DataNetworkController.this;
                dataNetworkController.logl("onPreferredTransportChanged: " + DataUtils.networkCapabilityToString(i) + " preferred on " + AccessNetworkConstants.transportTypeToString(preferredTransportByNetworkCapability));
                DataNetworkController.this.onEvaluatePreferredTransport(i);
                if (!DataNetworkController.this.hasMessages(5)) {
                    DataNetworkController dataNetworkController2 = DataNetworkController.this;
                    dataNetworkController2.sendMessage(dataNetworkController2.obtainMessage(5, DataEvaluation.DataEvaluationReason.PREFERRED_TRANSPORT_CHANGED));
                    return;
                }
                DataNetworkController.this.log("onPreferredTransportChanged: Skipped evaluating unsatisfied network requests because another evaluation was already scheduled.");
            }
        });
        this.mNetworkPolicyManager.registerSubscriptionCallback(new NetworkPolicyManager.SubscriptionCallback() { // from class: com.android.internal.telephony.data.DataNetworkController.7
            public void onSubscriptionPlansChanged(int i, SubscriptionPlan[] subscriptionPlanArr) {
                if (DataNetworkController.this.mSubId != i) {
                    return;
                }
                DataNetworkController.this.obtainMessage(22, subscriptionPlanArr).sendToTarget();
            }

            public void onSubscriptionOverride(int i, int i2, int i3, int[] iArr) {
                if (DataNetworkController.this.mSubId != i) {
                    return;
                }
                DataNetworkController.this.obtainMessage(23, i2, i3, iArr).sendToTarget();
            }
        });
        this.mPhone.getServiceStateTracker().registerForServiceStateChanged(this, 17, null);
        this.mDataConfigManager.registerCallback(new DataConfigManager.DataConfigManagerCallback(new DataNetworkController$$ExternalSyntheticLambda8(this)) { // from class: com.android.internal.telephony.data.DataNetworkController.8
            @Override // com.android.internal.telephony.data.DataConfigManager.DataConfigManagerCallback
            public void onCarrierConfigChanged() {
                DataNetworkController.this.onCarrierConfigUpdated();
            }

            @Override // com.android.internal.telephony.data.DataConfigManager.DataConfigManagerCallback
            public void onDeviceConfigChanged() {
                DataNetworkController.this.onDeviceConfigUpdated();
            }
        });
        this.mPhone.getServiceStateTracker().registerForPsRestrictedEnabled(this, 6, null);
        this.mPhone.getServiceStateTracker().registerForPsRestrictedDisabled(this, 7, null);
        this.mPhone.getServiceStateTracker().registerForAreaCodeChanged(this, 25, null);
        this.mPhone.registerForEmergencyCallToggle(this, 20, null);
        this.mDataServiceManagers.get(1).registerForServiceBindingChanged(this, 8);
        this.mPhone.getServiceStateTracker().registerForServiceStateChanged(this, 17, null);
        this.mDataServiceManagers.get(2).registerForServiceBindingChanged(this, 8);
        ((TelephonyRegistryManager) this.mPhone.getContext().getSystemService(TelephonyRegistryManager.class)).addOnSubscriptionsChangedListener(new SubscriptionManager.OnSubscriptionsChangedListener() { // from class: com.android.internal.telephony.data.DataNetworkController.9
            @Override // android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
            public void onSubscriptionsChanged() {
                DataNetworkController.this.sendEmptyMessage(15);
            }
        }, new DataNetworkController$$ExternalSyntheticLambda8(this));
        this.mPhone.getCallTracker().registerForVoiceCallEnded(this, 18, null);
        if (this.mPhone.getImsPhone() != null) {
            this.mPhone.getImsPhone().getCallTracker().registerForVoiceCallEnded(this, 18, null);
        }
        this.mPhone.mCi.registerForSlicingConfigChanged(this, 24, null);
        this.mPhone.mCi.registerForSrvccStateChanged(this, 4, null);
        this.mPhone.getLinkBandwidthEstimator().registerCallback(new LinkBandwidthEstimator.LinkBandwidthEstimatorCallback(new DataNetworkController$$ExternalSyntheticLambda8(this)) { // from class: com.android.internal.telephony.data.DataNetworkController.10
            @Override // com.android.internal.telephony.data.LinkBandwidthEstimator.LinkBandwidthEstimatorCallback
            public void onDataActivityChanged(int i) {
                DataNetworkController.this.updateDataActivity();
            }
        });
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = 0;
        switch (message.what) {
            case 2:
                onAddNetworkRequest((TelephonyNetworkRequest) message.obj);
                return;
            case 3:
                onRemoveNetworkRequest((TelephonyNetworkRequest) message.obj);
                return;
            case 4:
                AsyncResult asyncResult = (AsyncResult) message.obj;
                if (asyncResult.exception == null) {
                    onSrvccStateChanged((int[]) asyncResult.result);
                    return;
                }
                return;
            case 5:
                onReevaluateUnsatisfiedNetworkRequests((DataEvaluation.DataEvaluationReason) message.obj);
                return;
            case 6:
                this.mPsRestricted = true;
                return;
            case 7:
                this.mPsRestricted = false;
                sendMessage(obtainMessage(5, DataEvaluation.DataEvaluationReason.DATA_RESTRICTED_CHANGED));
                return;
            case 8:
                AsyncResult asyncResult2 = (AsyncResult) message.obj;
                onDataServiceBindingChanged(((Integer) asyncResult2.userObj).intValue(), ((Boolean) asyncResult2.result).booleanValue());
                return;
            case 9:
                onSimStateChanged(message.arg1);
                return;
            case 10:
            case 11:
            default:
                loge("Unexpected event " + message.what);
                return;
            case 12:
                onTearDownAllDataNetworks(message.arg1);
                return;
            case 13:
                final DataNetworkControllerCallback dataNetworkControllerCallback = (DataNetworkControllerCallback) message.obj;
                this.mDataNetworkControllerCallbacks.add(dataNetworkControllerCallback);
                if (this.mDataNetworkList.isEmpty()) {
                    dataNetworkControllerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            DataNetworkController.DataNetworkControllerCallback.this.onAnyDataNetworkExistingChanged(false);
                        }
                    });
                    return;
                }
                return;
            case 14:
                this.mDataNetworkControllerCallbacks.remove((DataNetworkControllerCallback) message.obj);
                return;
            case 15:
                onSubscriptionChanged();
                return;
            case 16:
                onReevaluateExistingDataNetworks((DataEvaluation.DataEvaluationReason) message.obj);
                return;
            case 17:
                onServiceStateChanged();
                return;
            case 18:
                DataEvaluation.DataEvaluationReason dataEvaluationReason = DataEvaluation.DataEvaluationReason.VOICE_CALL_ENDED;
                sendMessage(obtainMessage(16, dataEvaluationReason));
                sendMessageDelayed(obtainMessage(5, dataEvaluationReason), f10x94a23eb3);
                return;
            case 19:
                onRegisterAllEvents();
                return;
            case 20:
                if (this.mPhone.isInEcm()) {
                    sendMessage(obtainMessage(16, DataEvaluation.DataEvaluationReason.EMERGENCY_CALL_CHANGED));
                    return;
                } else {
                    sendMessage(obtainMessage(5, DataEvaluation.DataEvaluationReason.EMERGENCY_CALL_CHANGED));
                    return;
                }
            case 21:
                onEvaluatePreferredTransport(message.arg1);
                return;
            case 22:
                SubscriptionPlan[] subscriptionPlanArr = (SubscriptionPlan[]) message.obj;
                log("Subscription plans changed: " + Arrays.toString(subscriptionPlanArr));
                this.mSubscriptionPlans.clear();
                this.mSubscriptionPlans.addAll(Arrays.asList(subscriptionPlanArr));
                this.mDataNetworkControllerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda2
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        DataNetworkController.lambda$handleMessage$2((DataNetworkController.DataNetworkControllerCallback) obj);
                    }
                });
                return;
            case 23:
                int i2 = message.arg1;
                boolean z = message.arg2 != 0;
                int[] iArr = (int[]) message.obj;
                if (i2 == 1) {
                    log("Unmetered subscription override: override=" + z + ", networkTypes=" + ((String) Arrays.stream(iArr).mapToObj(new IntFunction() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda3
                        @Override // java.util.function.IntFunction
                        public final Object apply(int i3) {
                            return TelephonyManager.getNetworkTypeName(i3);
                        }
                    }).collect(Collectors.joining(","))));
                    int length = iArr.length;
                    while (i < length) {
                        int i3 = iArr[i];
                        if (z) {
                            this.mUnmeteredOverrideNetworkTypes.add(Integer.valueOf(i3));
                        } else {
                            this.mUnmeteredOverrideNetworkTypes.remove(Integer.valueOf(i3));
                        }
                        i++;
                    }
                    this.mDataNetworkControllerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda4
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            DataNetworkController.lambda$handleMessage$4((DataNetworkController.DataNetworkControllerCallback) obj);
                        }
                    });
                    return;
                } else if (i2 == 2) {
                    log("Congested subscription override: override=" + z + ", networkTypes=" + ((String) Arrays.stream(iArr).mapToObj(new IntFunction() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda3
                        @Override // java.util.function.IntFunction
                        public final Object apply(int i32) {
                            return TelephonyManager.getNetworkTypeName(i32);
                        }
                    }).collect(Collectors.joining(","))));
                    int length2 = iArr.length;
                    while (i < length2) {
                        int i4 = iArr[i];
                        if (z) {
                            this.mCongestedOverrideNetworkTypes.add(Integer.valueOf(i4));
                        } else {
                            this.mCongestedOverrideNetworkTypes.remove(Integer.valueOf(i4));
                        }
                        i++;
                    }
                    this.mDataNetworkControllerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda5
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            DataNetworkController.lambda$handleMessage$6((DataNetworkController.DataNetworkControllerCallback) obj);
                        }
                    });
                    return;
                } else {
                    loge("Unknown override mask: " + i2);
                    return;
                }
            case 24:
                sendMessage(obtainMessage(5, DataEvaluation.DataEvaluationReason.SLICE_CONFIG_CHANGED));
                return;
            case 25:
                sendMessageDelayed(obtainMessage(5, DataEvaluation.DataEvaluationReason.TAC_CHANGED), REEVALUATE_UNSATISFIED_NETWORK_REQUESTS_TAC_CHANGED_DELAY_MILLIS);
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$handleMessage$2(final DataNetworkControllerCallback dataNetworkControllerCallback) {
        dataNetworkControllerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda50
            @Override // java.lang.Runnable
            public final void run() {
                DataNetworkController.DataNetworkControllerCallback.this.onSubscriptionPlanOverride();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$handleMessage$4(final DataNetworkControllerCallback dataNetworkControllerCallback) {
        dataNetworkControllerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda19
            @Override // java.lang.Runnable
            public final void run() {
                DataNetworkController.DataNetworkControllerCallback.this.onSubscriptionPlanOverride();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$handleMessage$6(final DataNetworkControllerCallback dataNetworkControllerCallback) {
        dataNetworkControllerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda40
            @Override // java.lang.Runnable
            public final void run() {
                DataNetworkController.DataNetworkControllerCallback.this.onSubscriptionPlanOverride();
            }
        });
    }

    public void addNetworkRequest(TelephonyNetworkRequest telephonyNetworkRequest) {
        sendMessage(obtainMessage(2, telephonyNetworkRequest));
    }

    private void onAddNetworkRequest(TelephonyNetworkRequest telephonyNetworkRequest) {
        if (this.mLastImsOperationIsRelease) {
            this.mLastImsOperationIsRelease = false;
            if (Arrays.equals(this.mLastReleasedImsRequestCapabilities, telephonyNetworkRequest.getCapabilities()) && this.mImsThrottleCounter.addOccurrence()) {
                reportAnomaly(telephonyNetworkRequest.getNativeNetworkRequest().getRequestorPackageName() + " requested with same capabilities " + this.mImsThrottleCounter.getFrequencyString(), "ead6f8db-d2f2-4ed3-8da5-1d8560fe7daf");
            }
        }
        if (!this.mAllNetworkRequestList.add(telephonyNetworkRequest)) {
            loge("onAddNetworkRequest: Duplicate network request. " + telephonyNetworkRequest);
            return;
        }
        log("onAddNetworkRequest: added " + telephonyNetworkRequest);
        onSatisfyNetworkRequest(telephonyNetworkRequest);
    }

    private void onSatisfyNetworkRequest(TelephonyNetworkRequest telephonyNetworkRequest) {
        if (telephonyNetworkRequest.getState() == 1) {
            logv("Already satisfied. " + telephonyNetworkRequest);
        } else if (findCompatibleDataNetworkAndAttach(telephonyNetworkRequest)) {
        } else {
            DataEvaluation evaluateNetworkRequest = evaluateNetworkRequest(telephonyNetworkRequest, DataEvaluation.DataEvaluationReason.NEW_REQUEST);
            if (!evaluateNetworkRequest.containsDisallowedReasons()) {
                DataProfile candidateDataProfile = evaluateNetworkRequest.getCandidateDataProfile();
                if (candidateDataProfile != null) {
                    setupDataNetwork(candidateDataProfile, null, evaluateNetworkRequest.getDataAllowedReason());
                }
            } else if (evaluateNetworkRequest.contains(DataEvaluation.DataDisallowedReason.ONLY_ALLOWED_SINGLE_NETWORK)) {
                sendMessage(obtainMessage(16, DataEvaluation.DataEvaluationReason.SINGLE_DATA_NETWORK_ARBITRATION));
            }
        }
    }

    private boolean findCompatibleDataNetworkAndAttach(TelephonyNetworkRequest telephonyNetworkRequest) {
        return findCompatibleDataNetworkAndAttach(new NetworkRequestList(telephonyNetworkRequest));
    }

    private boolean findCompatibleDataNetworkAndAttach(NetworkRequestList networkRequestList) {
        for (final DataNetwork dataNetwork : this.mDataNetworkList) {
            if (((TelephonyNetworkRequest) networkRequestList.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda21
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$findCompatibleDataNetworkAndAttach$7;
                    lambda$findCompatibleDataNetworkAndAttach$7 = DataNetworkController.lambda$findCompatibleDataNetworkAndAttach$7(DataNetwork.this, (TelephonyNetworkRequest) obj);
                    return lambda$findCompatibleDataNetworkAndAttach$7;
                }
            }).findAny().orElse(null)) == null) {
                logv("Found a compatible data network " + dataNetwork + ". Attaching " + networkRequestList);
                return dataNetwork.attachNetworkRequests(networkRequestList);
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$findCompatibleDataNetworkAndAttach$7(DataNetwork dataNetwork, TelephonyNetworkRequest telephonyNetworkRequest) {
        return !telephonyNetworkRequest.canBeSatisfiedBy(dataNetwork.getNetworkCapabilities());
    }

    private boolean serviceStateAllowsPSAttach(ServiceState serviceState, int i) {
        int dataRegistrationState = getDataRegistrationState(serviceState, i);
        if (dataRegistrationState == 1 || dataRegistrationState == 5) {
            return true;
        }
        return serviceState.getVoiceRegState() == 0 && this.mPhone.getPhoneId() != PhoneSwitcher.getInstance().getPreferredDataPhoneId() && isLegacyCs(serviceState.getVoiceNetworkType());
    }

    private boolean isLegacyCs(int i) {
        int networkTypeToAccessNetworkType = DataUtils.networkTypeToAccessNetworkType(i);
        return networkTypeToAccessNetworkType == 1 || networkTypeToAccessNetworkType == 2 || networkTypeToAccessNetworkType == 4;
    }

    private boolean isOnlySingleDataNetworkAllowed(int i) {
        if (i == 2) {
            return false;
        }
        return this.mDataConfigManager.getNetworkTypesOnlySupportSingleDataNetwork().contains(Integer.valueOf(getDataNetworkType(i)));
    }

    private boolean hasCapabilityExemptsFromSinglePdnRule(int[] iArr) {
        final Set<Integer> capabilitiesExemptFromSingleDataNetwork = this.mDataConfigManager.getCapabilitiesExemptFromSingleDataNetwork();
        IntStream stream = Arrays.stream(iArr);
        Objects.requireNonNull(capabilitiesExemptFromSingleDataNetwork);
        return stream.anyMatch(new IntPredicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda23
            @Override // java.util.function.IntPredicate
            public final boolean test(int i) {
                return capabilitiesExemptFromSingleDataNetwork.contains(Integer.valueOf(i));
            }
        });
    }

    public boolean isInternetDataAllowed() {
        final TelephonyNetworkRequest telephonyNetworkRequest = new TelephonyNetworkRequest(new NetworkRequest.Builder().addCapability(12).addTransportType(0).build(), this.mPhone);
        if (this.mDataNetworkList.stream().anyMatch(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda12
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isInternetDataAllowed$8;
                lambda$isInternetDataAllowed$8 = DataNetworkController.lambda$isInternetDataAllowed$8(TelephonyNetworkRequest.this, (DataNetwork) obj);
                return lambda$isInternetDataAllowed$8;
            }
        })) {
            return true;
        }
        DataEvaluation evaluateNetworkRequest = evaluateNetworkRequest(telephonyNetworkRequest, DataEvaluation.DataEvaluationReason.EXTERNAL_QUERY);
        if (evaluateNetworkRequest.containsOnly(DataEvaluation.DataDisallowedReason.ONLY_ALLOWED_SINGLE_NETWORK)) {
            return telephonyNetworkRequest.getPriority() > ((Integer) this.mDataNetworkList.stream().map(new Function() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda13
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return Integer.valueOf(((DataNetwork) obj).getPriority());
                }
            }).max(Comparator.comparing(new Function() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda14
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return Integer.valueOf(((Integer) obj).intValue());
                }
            })).orElse(0)).intValue();
        }
        return !evaluateNetworkRequest.containsDisallowedReasons();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$isInternetDataAllowed$8(TelephonyNetworkRequest telephonyNetworkRequest, DataNetwork dataNetwork) {
        return telephonyNetworkRequest.canBeSatisfiedBy(dataNetwork.getNetworkCapabilities());
    }

    public boolean isInternetUnmetered() {
        return this.mDataNetworkList.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda9
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isInternetUnmetered$9;
                lambda$isInternetUnmetered$9 = DataNetworkController.lambda$isInternetUnmetered$9((DataNetwork) obj);
                return lambda$isInternetUnmetered$9;
            }
        }).filter(new DataNetworkController$$ExternalSyntheticLambda10()).allMatch(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda11
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isInternetUnmetered$10;
                lambda$isInternetUnmetered$10 = DataNetworkController.lambda$isInternetUnmetered$10((DataNetwork) obj);
                return lambda$isInternetUnmetered$10;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$isInternetUnmetered$9(DataNetwork dataNetwork) {
        return (dataNetwork.isConnecting() || dataNetwork.isDisconnected()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$isInternetUnmetered$10(DataNetwork dataNetwork) {
        return dataNetwork.getNetworkCapabilities().hasCapability(11) || dataNetwork.getNetworkCapabilities().hasCapability(25);
    }

    public boolean areAllDataDisconnected() {
        if (!this.mDataNetworkList.isEmpty()) {
            log("areAllDataDisconnected false due to: " + ((String) this.mDataNetworkList.stream().map(new Function() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((DataNetwork) obj).name();
                }
            }).collect(Collectors.joining(", "))));
        }
        return this.mDataNetworkList.isEmpty();
    }

    public List<DataEvaluation.DataDisallowedReason> getInternetDataDisallowedReasons() {
        return evaluateNetworkRequest(new TelephonyNetworkRequest(new NetworkRequest.Builder().addCapability(12).addTransportType(0).build(), this.mPhone), DataEvaluation.DataEvaluationReason.EXTERNAL_QUERY).getDataDisallowedReasons();
    }

    private DataEvaluation evaluateNetworkRequest(TelephonyNetworkRequest telephonyNetworkRequest, DataEvaluation.DataEvaluationReason dataEvaluationReason) {
        NetworkRegistrationInfo networkRegistrationInfo;
        DataSpecificRegistrationInfo dataSpecificInfo;
        DataEvaluation dataEvaluation = new DataEvaluation(dataEvaluationReason);
        int preferredTransportByNetworkCapability = this.mAccessNetworksManager.getPreferredTransportByNetworkCapability(telephonyNetworkRequest.getApnTypeNetworkCapability());
        if (telephonyNetworkRequest.hasCapability(10)) {
            dataEvaluation.addDataAllowedReason(DataEvaluation.DataAllowedReason.EMERGENCY_REQUEST);
            dataEvaluation.setCandidateDataProfile(this.mDataProfileManager.getDataProfileForNetworkRequest(telephonyNetworkRequest, getDataNetworkType(preferredTransportByNetworkCapability), true));
            telephonyNetworkRequest.setEvaluation(dataEvaluation);
            log(dataEvaluation.toString());
            return dataEvaluation;
        }
        if (!serviceStateAllowsPSAttach(this.mServiceState, preferredTransportByNetworkCapability)) {
            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.NOT_IN_SERVICE);
        }
        if (this.mSimState != 10) {
            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.SIM_NOT_READY);
        }
        if (!this.mDataConfigManager.isConfigCarrierSpecific()) {
            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.DATA_CONFIG_NOT_READY);
        }
        if (this.mPhone.getCallTracker().getState() != PhoneConstants.State.IDLE && !this.mPhone.getServiceStateTracker().isConcurrentVoiceAndDataAllowed()) {
            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.CONCURRENT_VOICE_DATA_NOT_ALLOWED);
        }
        if (preferredTransportByNetworkCapability == 1 && telephonyNetworkRequest.hasCapability(33) && (networkRegistrationInfo = this.mServiceState.getNetworkRegistrationInfo(2, 1)) != null && (dataSpecificInfo = networkRegistrationInfo.getDataSpecificInfo()) != null && dataSpecificInfo.getVopsSupportInfo() != null && !dataSpecificInfo.getVopsSupportInfo().isVopsSupported()) {
            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.VOPS_NOT_SUPPORTED);
        }
        if (!SubscriptionManager.isValidSubscriptionId(SubscriptionManager.getDefaultDataSubscriptionId())) {
            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.DEFAULT_DATA_UNSELECTED);
        }
        if (this.mServiceState.getDataRoaming() && !this.mDataSettingsManager.isDataRoamingEnabled()) {
            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.ROAMING_DISABLED);
        }
        if (this.mPsRestricted && preferredTransportByNetworkCapability == 1) {
            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.DATA_RESTRICTED_BY_NETWORK);
        }
        if (this.mPendingTearDownAllNetworks) {
            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.PENDING_TEAR_DOWN_ALL);
        }
        if (preferredTransportByNetworkCapability == 1 && (!this.mPhone.getServiceStateTracker().getDesiredPowerState() || this.mPhone.mCi.getRadioState() != 1)) {
            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.RADIO_POWER_OFF);
        }
        if (!this.mPhone.getServiceStateTracker().getPowerStateFromCarrier()) {
            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.RADIO_DISABLED_BY_CARRIER);
        }
        if (!this.mDataServiceBound.get(preferredTransportByNetworkCapability)) {
            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.DATA_SERVICE_NOT_READY);
        }
        if (this.mPhone.isInCdmaEcm()) {
            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.CDMA_EMERGENCY_CALLBACK_MODE);
        }
        if (isOnlySingleDataNetworkAllowed(preferredTransportByNetworkCapability) && !hasCapabilityExemptsFromSinglePdnRule(telephonyNetworkRequest.getCapabilities()) && this.mDataNetworkList.stream().anyMatch(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda15
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$evaluateNetworkRequest$11;
                lambda$evaluateNetworkRequest$11 = DataNetworkController.this.lambda$evaluateNetworkRequest$11((DataNetwork) obj);
                return lambda$evaluateNetworkRequest$11;
            }
        })) {
            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.ONLY_ALLOWED_SINGLE_NETWORK);
        }
        if (this.mDataSettingsManager.isDataInitialized()) {
            if (!this.mDataSettingsManager.isDataEnabled(DataUtils.networkCapabilityToApnType(telephonyNetworkRequest.getApnTypeNetworkCapability()))) {
                dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.DATA_DISABLED);
            }
        } else {
            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.DATA_SETTINGS_NOT_READY);
        }
        if (!dataEvaluation.containsDisallowedReasons()) {
            dataEvaluation.addDataAllowedReason(DataEvaluation.DataAllowedReason.NORMAL);
            if (!this.mDataSettingsManager.isDataEnabled() && telephonyNetworkRequest.hasCapability(0) && this.mDataSettingsManager.isMobileDataPolicyEnabled(2)) {
                dataEvaluation.addDataAllowedReason(DataEvaluation.DataAllowedReason.MMS_REQUEST);
            }
        } else if (!dataEvaluation.containsHardDisallowedReasons()) {
            if ((this.mPhone.isInEmergencyCall() || this.mPhone.isInEcm()) && telephonyNetworkRequest.hasCapability(1)) {
                dataEvaluation.addDataAllowedReason(DataEvaluation.DataAllowedReason.EMERGENCY_SUPL);
            } else if (!telephonyNetworkRequest.hasCapability(13) && !telephonyNetworkRequest.hasCapability(2)) {
                dataEvaluation.addDataAllowedReason(DataEvaluation.DataAllowedReason.RESTRICTED_REQUEST);
            } else if (preferredTransportByNetworkCapability == 2) {
                dataEvaluation.addDataAllowedReason(DataEvaluation.DataAllowedReason.UNMETERED_USAGE);
            } else if (preferredTransportByNetworkCapability == 1 && !telephonyNetworkRequest.isMeteredRequest()) {
                dataEvaluation.addDataAllowedReason(DataEvaluation.DataAllowedReason.UNMETERED_USAGE);
            }
        }
        int dataNetworkType = getDataNetworkType(preferredTransportByNetworkCapability);
        if (dataNetworkType == 0 && preferredTransportByNetworkCapability == 1) {
            dataNetworkType = this.mServiceState.getVoiceNetworkType();
        }
        DataProfile dataProfileForNetworkRequest = this.mDataProfileManager.getDataProfileForNetworkRequest(telephonyNetworkRequest, dataNetworkType, dataEvaluationReason.isConditionBased());
        if (dataProfileForNetworkRequest == null) {
            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.NO_SUITABLE_DATA_PROFILE);
        } else if (dataEvaluationReason == DataEvaluation.DataEvaluationReason.NEW_REQUEST && this.mDataRetryManager.isSimilarNetworkRequestRetryScheduled(telephonyNetworkRequest, preferredTransportByNetworkCapability)) {
            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.RETRY_SCHEDULED);
        } else if (this.mDataRetryManager.isDataProfileThrottled(dataProfileForNetworkRequest, preferredTransportByNetworkCapability)) {
            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.DATA_THROTTLED);
        }
        if (!dataEvaluation.containsDisallowedReasons()) {
            dataEvaluation.setCandidateDataProfile(dataProfileForNetworkRequest);
        }
        telephonyNetworkRequest.setEvaluation(dataEvaluation);
        if (dataEvaluationReason != DataEvaluation.DataEvaluationReason.EXTERNAL_QUERY) {
            log(dataEvaluation.toString() + ", network type=" + TelephonyManager.getNetworkTypeName(getDataNetworkType(preferredTransportByNetworkCapability)) + ", reg state=" + NetworkRegistrationInfo.registrationStateToString(getDataRegistrationState(this.mServiceState, preferredTransportByNetworkCapability)) + ", " + telephonyNetworkRequest);
        }
        return dataEvaluation;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$evaluateNetworkRequest$11(DataNetwork dataNetwork) {
        return !hasCapabilityExemptsFromSinglePdnRule(dataNetwork.getNetworkCapabilities().getCapabilities());
    }

    private List<NetworkRequestList> getGroupedUnsatisfiedNetworkRequests() {
        NetworkRequestList networkRequestList = new NetworkRequestList();
        Iterator<TelephonyNetworkRequest> it = this.mAllNetworkRequestList.iterator();
        while (it.hasNext()) {
            TelephonyNetworkRequest next = it.next();
            if (next.getState() == 0) {
                networkRequestList.add(next);
            }
        }
        return DataUtils.getGroupedNetworkRequestList(networkRequestList);
    }

    private void onReevaluateUnsatisfiedNetworkRequests(DataEvaluation.DataEvaluationReason dataEvaluationReason) {
        DataProfile candidateDataProfile;
        List<NetworkRequestList> groupedUnsatisfiedNetworkRequests = getGroupedUnsatisfiedNetworkRequests();
        log("Re-evaluating " + groupedUnsatisfiedNetworkRequests.stream().mapToInt(new ToIntFunction() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda26
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                return ((DataNetworkController.NetworkRequestList) obj).size();
            }
        }).sum() + " unsatisfied network requests in " + groupedUnsatisfiedNetworkRequests.size() + " groups, " + ((String) groupedUnsatisfiedNetworkRequests.stream().map(new Function() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda27
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$onReevaluateUnsatisfiedNetworkRequests$12;
                lambda$onReevaluateUnsatisfiedNetworkRequests$12 = DataNetworkController.lambda$onReevaluateUnsatisfiedNetworkRequests$12((DataNetworkController.NetworkRequestList) obj);
                return lambda$onReevaluateUnsatisfiedNetworkRequests$12;
            }
        }).collect(Collectors.joining(", "))) + " due to " + dataEvaluationReason);
        for (NetworkRequestList networkRequestList : groupedUnsatisfiedNetworkRequests) {
            if (!findCompatibleDataNetworkAndAttach(networkRequestList)) {
                DataEvaluation evaluateNetworkRequest = evaluateNetworkRequest(networkRequestList.get(0), dataEvaluationReason);
                if (!evaluateNetworkRequest.containsDisallowedReasons() && (candidateDataProfile = evaluateNetworkRequest.getCandidateDataProfile()) != null) {
                    setupDataNetwork(candidateDataProfile, null, evaluateNetworkRequest.getDataAllowedReason());
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$onReevaluateUnsatisfiedNetworkRequests$12(NetworkRequestList networkRequestList) {
        return DataUtils.networkCapabilitiesToString(networkRequestList.get(0).getCapabilities());
    }

    private DataEvaluation evaluateDataNetwork(final DataNetwork dataNetwork, DataEvaluation.DataEvaluationReason dataEvaluationReason) {
        NetworkRegistrationInfo networkRegistrationInfo;
        DataSpecificRegistrationInfo dataSpecificInfo;
        DataEvaluation dataEvaluation = new DataEvaluation(dataEvaluationReason);
        if (dataNetwork.getNetworkCapabilities().hasCapability(10)) {
            dataEvaluation.addDataAllowedReason(DataEvaluation.DataAllowedReason.EMERGENCY_REQUEST);
            log(dataEvaluation.toString());
            return dataEvaluation;
        }
        if (this.mSimState != 10) {
            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.SIM_NOT_READY);
        }
        if (this.mPhone.isInCdmaEcm()) {
            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.CDMA_EMERGENCY_CALLBACK_MODE);
        }
        if (isOnlySingleDataNetworkAllowed(dataNetwork.getTransport()) && !hasCapabilityExemptsFromSinglePdnRule(dataNetwork.getNetworkCapabilities().getCapabilities())) {
            if (this.mAllNetworkRequestList.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda37
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$evaluateDataNetwork$13;
                    lambda$evaluateDataNetwork$13 = DataNetworkController.this.lambda$evaluateDataNetwork$13(dataNetwork, (TelephonyNetworkRequest) obj);
                    return lambda$evaluateDataNetwork$13;
                }
            }).filter(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda38
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$evaluateDataNetwork$14;
                    lambda$evaluateDataNetwork$14 = DataNetworkController.this.lambda$evaluateDataNetwork$14((TelephonyNetworkRequest) obj);
                    return lambda$evaluateDataNetwork$14;
                }
            }).anyMatch(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda39
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$evaluateDataNetwork$15;
                    lambda$evaluateDataNetwork$15 = DataNetworkController.lambda$evaluateDataNetwork$15(DataNetwork.this, (TelephonyNetworkRequest) obj);
                    return lambda$evaluateDataNetwork$15;
                }
            })) {
                dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.ONLY_ALLOWED_SINGLE_NETWORK);
            } else {
                log("evaluateDataNetwork: " + dataNetwork + " has the highest priority. No need to tear down");
            }
        }
        boolean hasNetworkCapabilityInNetworkRequests = dataNetwork.hasNetworkCapabilityInNetworkRequests(33);
        boolean z = true;
        if (!dataNetwork.shouldDelayImsTearDownDueToInCall()) {
            if (hasNetworkCapabilityInNetworkRequests && dataNetwork.getTransport() == 1 && (networkRegistrationInfo = this.mServiceState.getNetworkRegistrationInfo(2, 1)) != null && (dataSpecificInfo = networkRegistrationInfo.getDataSpecificInfo()) != null && dataSpecificInfo.getVopsSupportInfo() != null && !dataSpecificInfo.getVopsSupportInfo().isVopsSupported() && !this.mDataConfigManager.shouldKeepNetworkUpInNonVops()) {
                dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.VOPS_NOT_SUPPORTED);
            }
            if (this.mAccessNetworksManager.getPreferredTransportByNetworkCapability(dataNetwork.getApnTypeNetworkCapability()) != dataNetwork.getTransport() && this.mDataRetryManager.isDataNetworkHandoverRetryStopped(dataNetwork)) {
                dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.HANDOVER_RETRY_STOPPED);
            }
        } else if (hasNetworkCapabilityInNetworkRequests) {
            log("Ignored VoPS check due to delay IMS tear down until call ends.");
        }
        boolean z2 = !this.mDataSettingsManager.isDataEnabled();
        if (this.mServiceState.getDataRoaming() && !this.mDataSettingsManager.isDataRoamingEnabled()) {
            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.ROAMING_DISABLED);
        }
        int dataNetworkType = getDataNetworkType(dataNetwork.getTransport());
        DataProfile dataProfile = dataNetwork.getDataProfile();
        if (dataProfile.getApnSetting() != null) {
            z2 = !this.mDataSettingsManager.isDataEnabled(DataUtils.networkCapabilityToApnType(DataUtils.getHighestPriorityNetworkCapabilityFromDataProfile(this.mDataConfigManager, dataProfile)));
            if (dataNetworkType != 0 && !dataProfile.getApnSetting().canSupportLingeringNetworkType(dataNetworkType)) {
                log("networkType=" + TelephonyManager.getNetworkTypeName(dataNetworkType) + ", networkTypeBitmask=" + TelephonyManager.convertNetworkTypeBitmaskToString(dataProfile.getApnSetting().getNetworkTypeBitmask()) + ", lingeringNetworkTypeBitmask=" + TelephonyManager.convertNetworkTypeBitmaskToString(dataProfile.getApnSetting().getLingeringNetworkTypeBitmask()));
                dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.DATA_NETWORK_TYPE_NOT_ALLOWED);
            }
        }
        if (z2) {
            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.DATA_DISABLED);
        }
        if (!this.mDataProfileManager.isDataProfileCompatible(dataProfile)) {
            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.DATA_PROFILE_INVALID);
        }
        if (dataNetwork.isInternetSupported() && !this.mDataProfileManager.isDataProfilePreferred(dataProfile) && this.mDataProfileManager.isAnyPreferredDataProfileExisting()) {
            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.DATA_PROFILE_NOT_PREFERRED);
        }
        if (!dataEvaluation.containsDisallowedReasons()) {
            dataEvaluation.addDataAllowedReason(DataEvaluation.DataAllowedReason.NORMAL);
        } else if (!dataEvaluation.containsHardDisallowedReasons()) {
            if ((this.mPhone.isInEmergencyCall() || this.mPhone.isInEcm()) && dataNetwork.isEmergencySupl()) {
                dataEvaluation.addDataAllowedReason(DataEvaluation.DataAllowedReason.EMERGENCY_SUPL);
            } else if (!dataNetwork.getNetworkCapabilities().hasCapability(13) && !dataNetwork.hasNetworkCapabilityInNetworkRequests(2)) {
                dataEvaluation.addDataAllowedReason(DataEvaluation.DataAllowedReason.RESTRICTED_REQUEST);
            } else if (dataNetwork.getTransport() == 2) {
                dataEvaluation.addDataAllowedReason(DataEvaluation.DataAllowedReason.UNMETERED_USAGE);
            } else if (!this.mDataConfigManager.isAnyMeteredCapability(dataNetwork.getNetworkCapabilities().getCapabilities(), this.mServiceState.getDataRoaming())) {
                dataEvaluation.addDataAllowedReason(DataEvaluation.DataAllowedReason.UNMETERED_USAGE);
            }
        }
        z = (hasNetworkCapabilityInNetworkRequests && this.mIsSrvccHandoverInProcess) ? false : false;
        if (dataEvaluation.containsOnly(DataEvaluation.DataDisallowedReason.DATA_NETWORK_TYPE_NOT_ALLOWED) && (dataNetwork.shouldDelayImsTearDownDueToInCall() || z)) {
            dataEvaluation.addDataAllowedReason(DataEvaluation.DataAllowedReason.IN_VOICE_CALL);
        }
        log("Evaluated " + dataNetwork + ", " + dataEvaluation);
        return dataEvaluation;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$evaluateDataNetwork$13(DataNetwork dataNetwork, TelephonyNetworkRequest telephonyNetworkRequest) {
        return dataNetwork.getTransport() == this.mAccessNetworksManager.getPreferredTransportByNetworkCapability(telephonyNetworkRequest.getApnTypeNetworkCapability());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$evaluateDataNetwork$14(TelephonyNetworkRequest telephonyNetworkRequest) {
        return !hasCapabilityExemptsFromSinglePdnRule(telephonyNetworkRequest.getCapabilities());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$evaluateDataNetwork$15(DataNetwork dataNetwork, TelephonyNetworkRequest telephonyNetworkRequest) {
        return telephonyNetworkRequest.getPriority() > dataNetwork.getPriority();
    }

    private void onReevaluateExistingDataNetworks(DataEvaluation.DataEvaluationReason dataEvaluationReason) {
        if (this.mDataNetworkList.isEmpty()) {
            log("onReevaluateExistingDataNetworks: No existing data networks to re-evaluate.");
            return;
        }
        log("Re-evaluating " + this.mDataNetworkList.size() + " existing data networks due to " + dataEvaluationReason);
        for (DataNetwork dataNetwork : this.mDataNetworkList) {
            if (dataNetwork.isConnecting() || dataNetwork.isConnected()) {
                DataEvaluation evaluateDataNetwork = evaluateDataNetwork(dataNetwork, dataEvaluationReason);
                if (evaluateDataNetwork.containsDisallowedReasons()) {
                    tearDownGracefully(dataNetwork, getTearDownReason(evaluateDataNetwork));
                }
            }
        }
    }

    private DataEvaluation evaluateDataNetworkHandover(DataNetwork dataNetwork) {
        DataSpecificRegistrationInfo dataSpecificInfo;
        DataEvaluation dataEvaluation = new DataEvaluation(DataEvaluation.DataEvaluationReason.DATA_HANDOVER);
        if (!dataNetwork.isConnecting() && !dataNetwork.isConnected()) {
            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.ILLEGAL_STATE);
            return dataEvaluation;
        }
        if (this.mDataConfigManager.isEnhancedIwlanHandoverCheckEnabled()) {
            NetworkRegistrationInfo networkRegistrationInfo = this.mServiceState.getNetworkRegistrationInfo(2, DataUtils.getTargetTransport(dataNetwork.getTransport()));
            if (networkRegistrationInfo != null) {
                if (!networkRegistrationInfo.isInService()) {
                    dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.NOT_IN_SERVICE);
                }
                if (dataNetwork.getAttachedNetworkRequestList().stream().anyMatch(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda17
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean hasCapability;
                        hasCapability = ((TelephonyNetworkRequest) obj).hasCapability(33);
                        return hasCapability;
                    }
                }) && (dataSpecificInfo = networkRegistrationInfo.getDataSpecificInfo()) != null && dataSpecificInfo.getVopsSupportInfo() != null && !dataSpecificInfo.getVopsSupportInfo().isVopsSupported() && !this.mDataConfigManager.shouldKeepNetworkUpInNonVops()) {
                    dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.VOPS_NOT_SUPPORTED);
                }
                if (dataEvaluation.containsDisallowedReasons()) {
                    return dataEvaluation;
                }
            }
        }
        if (this.mDataConfigManager.isIwlanHandoverPolicyEnabled()) {
            List<HandoverRule> handoverRules = this.mDataConfigManager.getHandoverRules();
            int dataNetworkType = getDataNetworkType(dataNetwork.getTransport());
            if (dataNetworkType == 0) {
                dataNetworkType = dataNetwork.getLastKnownDataNetworkType();
            }
            int networkTypeToAccessNetworkType = DataUtils.networkTypeToAccessNetworkType(dataNetworkType);
            int networkTypeToAccessNetworkType2 = DataUtils.networkTypeToAccessNetworkType(getDataNetworkType(DataUtils.getTargetTransport(dataNetwork.getTransport())));
            final NetworkCapabilities networkCapabilities = dataNetwork.getNetworkCapabilities();
            log("evaluateDataNetworkHandover: source=" + AccessNetworkConstants.AccessNetworkType.toString(networkTypeToAccessNetworkType) + ", target=" + AccessNetworkConstants.AccessNetworkType.toString(networkTypeToAccessNetworkType2) + ", ServiceState=" + this.mServiceState + ", capabilities=" + networkCapabilities);
            for (HandoverRule handoverRule : handoverRules) {
                if (!handoverRule.isOnlyForRoaming || this.mServiceState.getDataRoamingFromRegistration()) {
                    if (handoverRule.sourceAccessNetworks.contains(Integer.valueOf(networkTypeToAccessNetworkType)) && handoverRule.targetAccessNetworks.contains(Integer.valueOf(networkTypeToAccessNetworkType2))) {
                        if (!handoverRule.networkCapabilities.isEmpty()) {
                            Stream<Integer> stream = handoverRule.networkCapabilities.stream();
                            Objects.requireNonNull(networkCapabilities);
                            if (stream.anyMatch(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda18
                                @Override // java.util.function.Predicate
                                public final boolean test(Object obj) {
                                    return networkCapabilities.hasCapability(((Integer) obj).intValue());
                                }
                            })) {
                            }
                        }
                        log("evaluateDataNetworkHandover: Matched " + handoverRule);
                        if (handoverRule.type == 2) {
                            dataEvaluation.addDataDisallowedReason(DataEvaluation.DataDisallowedReason.NOT_ALLOWED_BY_POLICY);
                        } else {
                            dataEvaluation.addDataAllowedReason(DataEvaluation.DataAllowedReason.NORMAL);
                        }
                        log("evaluateDataNetworkHandover: " + dataEvaluation);
                        return dataEvaluation;
                    }
                }
            }
            log("evaluateDataNetworkHandover: Did not find matching rule.");
        } else {
            log("evaluateDataNetworkHandover: IWLAN handover policy not enabled.");
        }
        dataEvaluation.addDataAllowedReason(DataEvaluation.DataAllowedReason.NORMAL);
        return dataEvaluation;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.telephony.data.DataNetworkController$14 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C011314 {

        /* renamed from: $SwitchMap$com$android$internal$telephony$data$DataEvaluation$DataDisallowedReason */
        static final /* synthetic */ int[] f11x1ab30687;

        static {
            int[] iArr = new int[DataEvaluation.DataDisallowedReason.values().length];
            f11x1ab30687 = iArr;
            try {
                iArr[DataEvaluation.DataDisallowedReason.DATA_DISABLED.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                f11x1ab30687[DataEvaluation.DataDisallowedReason.ROAMING_DISABLED.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                f11x1ab30687[DataEvaluation.DataDisallowedReason.DEFAULT_DATA_UNSELECTED.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                f11x1ab30687[DataEvaluation.DataDisallowedReason.NOT_IN_SERVICE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                f11x1ab30687[DataEvaluation.DataDisallowedReason.DATA_CONFIG_NOT_READY.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                f11x1ab30687[DataEvaluation.DataDisallowedReason.SIM_NOT_READY.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                f11x1ab30687[DataEvaluation.DataDisallowedReason.CONCURRENT_VOICE_DATA_NOT_ALLOWED.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                f11x1ab30687[DataEvaluation.DataDisallowedReason.RADIO_POWER_OFF.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                f11x1ab30687[DataEvaluation.DataDisallowedReason.PENDING_TEAR_DOWN_ALL.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                f11x1ab30687[DataEvaluation.DataDisallowedReason.RADIO_DISABLED_BY_CARRIER.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                f11x1ab30687[DataEvaluation.DataDisallowedReason.DATA_SERVICE_NOT_READY.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                f11x1ab30687[DataEvaluation.DataDisallowedReason.NO_SUITABLE_DATA_PROFILE.ordinal()] = 12;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                f11x1ab30687[DataEvaluation.DataDisallowedReason.DATA_NETWORK_TYPE_NOT_ALLOWED.ordinal()] = 13;
            } catch (NoSuchFieldError unused13) {
            }
            try {
                f11x1ab30687[DataEvaluation.DataDisallowedReason.CDMA_EMERGENCY_CALLBACK_MODE.ordinal()] = 14;
            } catch (NoSuchFieldError unused14) {
            }
            try {
                f11x1ab30687[DataEvaluation.DataDisallowedReason.RETRY_SCHEDULED.ordinal()] = 15;
            } catch (NoSuchFieldError unused15) {
            }
            try {
                f11x1ab30687[DataEvaluation.DataDisallowedReason.DATA_THROTTLED.ordinal()] = 16;
            } catch (NoSuchFieldError unused16) {
            }
            try {
                f11x1ab30687[DataEvaluation.DataDisallowedReason.DATA_PROFILE_INVALID.ordinal()] = 17;
            } catch (NoSuchFieldError unused17) {
            }
            try {
                f11x1ab30687[DataEvaluation.DataDisallowedReason.DATA_PROFILE_NOT_PREFERRED.ordinal()] = 18;
            } catch (NoSuchFieldError unused18) {
            }
            try {
                f11x1ab30687[DataEvaluation.DataDisallowedReason.NOT_ALLOWED_BY_POLICY.ordinal()] = 19;
            } catch (NoSuchFieldError unused19) {
            }
            try {
                f11x1ab30687[DataEvaluation.DataDisallowedReason.ILLEGAL_STATE.ordinal()] = 20;
            } catch (NoSuchFieldError unused20) {
            }
            try {
                f11x1ab30687[DataEvaluation.DataDisallowedReason.VOPS_NOT_SUPPORTED.ordinal()] = 21;
            } catch (NoSuchFieldError unused21) {
            }
            try {
                f11x1ab30687[DataEvaluation.DataDisallowedReason.ONLY_ALLOWED_SINGLE_NETWORK.ordinal()] = 22;
            } catch (NoSuchFieldError unused22) {
            }
            try {
                f11x1ab30687[DataEvaluation.DataDisallowedReason.HANDOVER_RETRY_STOPPED.ordinal()] = 23;
            } catch (NoSuchFieldError unused23) {
            }
        }
    }

    private static int getTearDownReason(DataEvaluation dataEvaluation) {
        if (dataEvaluation.containsDisallowedReasons()) {
            switch (C011314.f11x1ab30687[dataEvaluation.getDataDisallowedReasons().get(0).ordinal()]) {
                case 1:
                    return 4;
                case 2:
                    return 7;
                case 3:
                    return 17;
                case 4:
                    return 18;
                case 5:
                    return 19;
                case 6:
                    return 2;
                case 7:
                    return 8;
                case 8:
                    return 3;
                case 9:
                    return 20;
                case 10:
                    return 11;
                case 11:
                    return 10;
                case 12:
                    return 21;
                case 13:
                    return 6;
                case 14:
                    return 22;
                case 15:
                    return 23;
                case 16:
                    return 24;
                case 17:
                    return 25;
                case 18:
                    return 26;
                case 19:
                    return 27;
                case 20:
                    return 28;
                case 21:
                    return 16;
                case 22:
                    return 29;
                case 23:
                    return 13;
            }
        }
        return 0;
    }

    public boolean isInternetNetwork(int i) {
        for (DataNetwork dataNetwork : this.mDataNetworkList) {
            if (dataNetwork.getId() == i && dataNetwork.isConnected() && dataNetwork.getNetworkCapabilities().hasCapability(12)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDataDormant() {
        return this.mDataNetworkList.stream().anyMatch(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda24
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isDataDormant$17;
                lambda$isDataDormant$17 = DataNetworkController.lambda$isDataDormant$17((DataNetwork) obj);
                return lambda$isDataDormant$17;
            }
        }) && this.mDataNetworkList.stream().noneMatch(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda25
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isDataDormant$18;
                lambda$isDataDormant$18 = DataNetworkController.lambda$isDataDormant$18((DataNetwork) obj);
                return lambda$isDataDormant$18;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$isDataDormant$17(DataNetwork dataNetwork) {
        return dataNetwork.getLinkStatus() == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$isDataDormant$18(DataNetwork dataNetwork) {
        return dataNetwork.getLinkStatus() == 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDataActivity() {
        int dataActivity;
        if (isDataDormant()) {
            dataActivity = 4;
        } else {
            dataActivity = this.mPhone.getLinkBandwidthEstimator() != null ? this.mPhone.getLinkBandwidthEstimator().getDataActivity() : 0;
        }
        if (this.mDataActivity != dataActivity) {
            logv("updateDataActivity: dataActivity=" + DataUtils.dataActivityToString(dataActivity));
            this.mDataActivity = dataActivity;
            this.mPhone.notifyDataActivity();
        }
    }

    public void removeNetworkRequest(TelephonyNetworkRequest telephonyNetworkRequest) {
        sendMessage(obtainMessage(3, telephonyNetworkRequest));
    }

    private void onRemoveNetworkRequest(final TelephonyNetworkRequest telephonyNetworkRequest) {
        TelephonyNetworkRequest telephonyNetworkRequest2 = (TelephonyNetworkRequest) this.mAllNetworkRequestList.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda36
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean equals;
                equals = ((TelephonyNetworkRequest) obj).equals(TelephonyNetworkRequest.this);
                return equals;
            }
        }).findFirst().orElse(null);
        if (telephonyNetworkRequest2 == null || !this.mAllNetworkRequestList.remove(telephonyNetworkRequest2)) {
            loge("onRemoveNetworkRequest: Network request does not exist. " + telephonyNetworkRequest2);
            return;
        }
        if (telephonyNetworkRequest2.hasCapability(4)) {
            this.mImsThrottleCounter.addOccurrence();
            this.mLastReleasedImsRequestCapabilities = telephonyNetworkRequest2.getCapabilities();
            this.mLastImsOperationIsRelease = true;
        }
        if (telephonyNetworkRequest2.getAttachedNetwork() != null) {
            telephonyNetworkRequest2.getAttachedNetwork().detachNetworkRequest(telephonyNetworkRequest2);
        }
        log("onRemoveNetworkRequest: Removed " + telephonyNetworkRequest2);
    }

    public boolean isNetworkRequestExisting(TelephonyNetworkRequest telephonyNetworkRequest) {
        return this.mAllNetworkRequestList.contains(telephonyNetworkRequest);
    }

    public DataNetwork getDataNetworkByInterface(final String str) {
        return this.mDataNetworkList.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda6
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getDataNetworkByInterface$20;
                lambda$getDataNetworkByInterface$20 = DataNetworkController.lambda$getDataNetworkByInterface$20((DataNetwork) obj);
                return lambda$getDataNetworkByInterface$20;
            }
        }).filter(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda7
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getDataNetworkByInterface$21;
                lambda$getDataNetworkByInterface$21 = DataNetworkController.lambda$getDataNetworkByInterface$21(str, (DataNetwork) obj);
                return lambda$getDataNetworkByInterface$21;
            }
        }).findFirst().orElse(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getDataNetworkByInterface$20(DataNetwork dataNetwork) {
        return !dataNetwork.isDisconnecting();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getDataNetworkByInterface$21(String str, DataNetwork dataNetwork) {
        return str.equals(dataNetwork.getLinkProperties().getInterfaceName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerImsFeatureRegistrationState(int i, final int i2) {
        RegistrationManager.RegistrationCallback registrationCallback = new RegistrationManager.RegistrationCallback() { // from class: com.android.internal.telephony.data.DataNetworkController.11
            @Override // android.telephony.ims.RegistrationManager.RegistrationCallback
            public void onRegistered(ImsRegistrationAttributes imsRegistrationAttributes) {
                DataNetworkController dataNetworkController = DataNetworkController.this;
                dataNetworkController.log("IMS " + DataUtils.imsFeatureToString(i2) + " registered. Attributes=" + imsRegistrationAttributes);
                DataNetworkController.this.mRegisteredImsFeatures.add(Integer.valueOf(i2));
            }

            @Override // android.telephony.ims.RegistrationManager.RegistrationCallback
            public void onUnregistered(ImsReasonInfo imsReasonInfo) {
                DataNetworkController dataNetworkController = DataNetworkController.this;
                dataNetworkController.log("IMS " + DataUtils.imsFeatureToString(i2) + " deregistered. Info=" + imsReasonInfo);
                DataNetworkController.this.mRegisteredImsFeatures.remove(Integer.valueOf(i2));
                DataNetworkController.this.evaluatePendingImsDeregDataNetworks();
            }
        };
        try {
            if (i2 == 1) {
                this.mImsManager.getImsMmTelManager(i).registerImsRegistrationCallback(new DataNetworkController$$ExternalSyntheticLambda8(this), registrationCallback);
            } else if (i2 == 2) {
                this.mImsManager.getImsRcsManager(i).registerImsRegistrationCallback(new DataNetworkController$$ExternalSyntheticLambda8(this), registrationCallback);
            }
            this.mImsFeatureRegistrationCallbacks.put(i2, registrationCallback);
            log("Successfully register " + DataUtils.imsFeatureToString(i2) + " registration state. subId=" + i);
        } catch (ImsException e) {
            loge("updateImsFeatureRegistrationStateListening: subId=" + i + ", imsFeature=" + DataUtils.imsFeatureToString(i2) + ", " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterImsFeatureRegistrationState(int i, int i2) {
        RegistrationManager.RegistrationCallback registrationCallback = this.mImsFeatureRegistrationCallbacks.get(i2);
        if (registrationCallback != null) {
            if (i2 == 1) {
                this.mImsManager.getImsMmTelManager(i).unregisterImsRegistrationCallback(registrationCallback);
            } else if (i2 == 2) {
                this.mImsManager.getImsRcsManager(i).unregisterImsRegistrationCallback(registrationCallback);
            }
            log("Successfully unregistered " + DataUtils.imsFeatureToString(i2) + " registration state. sudId=" + i);
            this.mImsFeatureRegistrationCallbacks.remove(i2);
        }
    }

    private void registerImsStateCallback(final int i) {
        Function function = new Function() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda49
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                ImsStateCallback lambda$registerImsStateCallback$22;
                lambda$registerImsStateCallback$22 = DataNetworkController.this.lambda$registerImsStateCallback$22(i, (Integer) obj);
                return lambda$registerImsStateCallback$22;
            }
        };
        try {
            ImsStateCallback imsStateCallback = (ImsStateCallback) function.apply(1);
            this.mImsManager.getImsMmTelManager(i).registerImsStateCallback(new DataNetworkController$$ExternalSyntheticLambda8(this), imsStateCallback);
            this.mImsStateCallbacks.put(1, imsStateCallback);
            log("Successfully register MMTEL state on sub " + i);
            ImsStateCallback imsStateCallback2 = (ImsStateCallback) function.apply(2);
            this.mImsManager.getImsRcsManager(i).registerImsStateCallback(new DataNetworkController$$ExternalSyntheticLambda8(this), imsStateCallback2);
            this.mImsStateCallbacks.put(2, imsStateCallback2);
            log("Successfully register RCS state on sub " + i);
        } catch (ImsException e) {
            loge("Exception when registering IMS state callback. " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ImsStateCallback lambda$registerImsStateCallback$22(final int i, final Integer num) {
        return new ImsStateCallback() { // from class: com.android.internal.telephony.data.DataNetworkController.12
            public void onError() {
            }

            public void onUnavailable(int i2) {
                DataNetworkController.this.unregisterImsFeatureRegistrationState(i, num.intValue());
            }

            public void onAvailable() {
                DataNetworkController.this.mImsFeaturePackageName.put(num.intValue(), ImsResolver.getInstance().getConfiguredImsServicePackageName(DataNetworkController.this.mPhone.getPhoneId(), num.intValue()));
                DataNetworkController.this.registerImsFeatureRegistrationState(i, num.intValue());
            }
        };
    }

    private void unregisterImsStateCallbacks(int i) {
        ImsStateCallback imsStateCallback = this.mImsStateCallbacks.get(1);
        if (imsStateCallback != null) {
            this.mImsManager.getImsMmTelManager(i).unregisterImsStateCallback(imsStateCallback);
            this.mImsStateCallbacks.remove(1);
            log("Unregister MMTEL state on sub " + i);
        }
        ImsStateCallback imsStateCallback2 = this.mImsStateCallbacks.get(2);
        if (imsStateCallback2 != null) {
            this.mImsManager.getImsRcsManager(i).unregisterImsStateCallback(imsStateCallback2);
            this.mImsStateCallbacks.remove(2);
            log("Unregister RCS state on sub " + i);
        }
    }

    private void onSubscriptionChanged() {
        if (this.mSubId != this.mPhone.getSubId()) {
            log("onDataConfigUpdated: mSubId changed from " + this.mSubId + " to " + this.mPhone.getSubId());
            if (isImsGracefulTearDownSupported()) {
                if (SubscriptionManager.isValidSubscriptionId(this.mPhone.getSubId())) {
                    registerImsStateCallback(this.mPhone.getSubId());
                } else {
                    unregisterImsStateCallbacks(this.mSubId);
                }
            }
            this.mSubId = this.mPhone.getSubId();
            updateSubscriptionPlans();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCarrierConfigUpdated() {
        StringBuilder sb = new StringBuilder();
        sb.append("onCarrierConfigUpdated: config is ");
        sb.append(this.mDataConfigManager.isConfigCarrierSpecific() ? PhoneConfigurationManager.SSSS : "not ");
        sb.append("carrier specific. mSimState=");
        sb.append(TelephonyManager.simStateToString(this.mSimState));
        log(sb.toString());
        updateNetworkRequestsPriority();
        onReevaluateUnsatisfiedNetworkRequests(DataEvaluation.DataEvaluationReason.DATA_CONFIG_CHANGED);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDeviceConfigUpdated() {
        log("onDeviceConfigUpdated: DeviceConfig updated.");
        updateAnomalySlidingWindowCounters();
    }

    private void updateNetworkRequestsPriority() {
        Iterator<TelephonyNetworkRequest> it = this.mAllNetworkRequestList.iterator();
        while (it.hasNext()) {
            it.next().updatePriority();
        }
    }

    private void updateAnomalySlidingWindowCounters() {
        this.mImsThrottleCounter = new SlidingWindowEventCounter(this.mDataConfigManager.getAnomalyImsReleaseRequestThreshold().timeWindow, this.mDataConfigManager.getAnomalyImsReleaseRequestThreshold().eventNumOccurrence);
        this.mNetworkUnwantedCounter = new SlidingWindowEventCounter(this.mDataConfigManager.getAnomalyNetworkUnwantedThreshold().timeWindow, this.mDataConfigManager.getAnomalyNetworkUnwantedThreshold().eventNumOccurrence);
        this.mSetupDataCallWwanFailureCounter = new SlidingWindowEventCounter(this.mDataConfigManager.getAnomalySetupDataCallThreshold().timeWindow, this.mDataConfigManager.getAnomalySetupDataCallThreshold().eventNumOccurrence);
        this.mSetupDataCallWlanFailureCounter = new SlidingWindowEventCounter(this.mDataConfigManager.getAnomalySetupDataCallThreshold().timeWindow, this.mDataConfigManager.getAnomalySetupDataCallThreshold().eventNumOccurrence);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTrackNetworkUnwanted() {
        if (this.mNetworkUnwantedCounter.addOccurrence()) {
            reportAnomaly("Network Unwanted called " + this.mNetworkUnwantedCounter.getFrequencyString(), "9f3bc55b-bfa6-4e26-afaa-5031426a66d3");
        }
    }

    private NetworkRequestList findSatisfiableNetworkRequests(final DataProfile dataProfile) {
        return new NetworkRequestList((List) this.mAllNetworkRequestList.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda57
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$findSatisfiableNetworkRequests$23;
                lambda$findSatisfiableNetworkRequests$23 = DataNetworkController.lambda$findSatisfiableNetworkRequests$23((TelephonyNetworkRequest) obj);
                return lambda$findSatisfiableNetworkRequests$23;
            }
        }).filter(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda58
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean canBeSatisfiedBy;
                canBeSatisfiedBy = ((TelephonyNetworkRequest) obj).canBeSatisfiedBy(dataProfile);
                return canBeSatisfiedBy;
            }
        }).collect(Collectors.toList()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$findSatisfiableNetworkRequests$23(TelephonyNetworkRequest telephonyNetworkRequest) {
        return telephonyNetworkRequest.getState() == 0;
    }

    /* JADX WARN: Removed duplicated region for block: B:5:0x003b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void setupDataNetwork(DataProfile dataProfile, final DataRetryManager.DataSetupRetryEntry dataSetupRetryEntry, DataEvaluation.DataAllowedReason dataAllowedReason) {
        log("onSetupDataNetwork: dataProfile=" + dataProfile + ", retryEntry=" + dataSetupRetryEntry + ", allowed reason=" + dataAllowedReason + ", service state=" + this.mServiceState);
        for (DataNetwork dataNetwork : this.mDataNetworkList) {
            DataProfile dataProfile2 = dataNetwork.getDataProfile();
            if (dataProfile.equals(dataProfile2) || this.mDataProfileManager.lambda$isDataProfileCompatible$13(dataProfile, dataProfile2)) {
                log("onSetupDataNetwork: Found existing data network " + dataNetwork + " using the same or a similar data profile.");
                if (dataSetupRetryEntry != null) {
                    dataSetupRetryEntry.setState(4);
                    return;
                }
                return;
            }
            while (r0.hasNext()) {
            }
        }
        NetworkRequestList findSatisfiableNetworkRequests = findSatisfiableNetworkRequests(dataProfile);
        if (findSatisfiableNetworkRequests.isEmpty()) {
            log("Can't find any unsatisfied network requests that can be satisfied by this data profile.");
            if (dataSetupRetryEntry != null) {
                dataSetupRetryEntry.setState(4);
                return;
            }
            return;
        }
        int preferredTransportByNetworkCapability = this.mAccessNetworksManager.getPreferredTransportByNetworkCapability(findSatisfiableNetworkRequests.get(0).getApnTypeNetworkCapability());
        logl("Creating data network on " + AccessNetworkConstants.transportTypeToString(preferredTransportByNetworkCapability) + " with " + dataProfile + ", and attaching " + findSatisfiableNetworkRequests.size() + " network requests to it.");
        this.mDataNetworkList.add(new DataNetwork(this.mPhone, getLooper(), this.mDataServiceManagers, dataProfile, findSatisfiableNetworkRequests, preferredTransportByNetworkCapability, dataAllowedReason, new DataNetwork.DataNetworkCallback(new DataNetworkController$$ExternalSyntheticLambda8(this)) { // from class: com.android.internal.telephony.data.DataNetworkController.13
            @Override // com.android.internal.telephony.data.DataNetwork.DataNetworkCallback
            public void onSetupDataFailed(DataNetwork dataNetwork2, NetworkRequestList networkRequestList, int i, long j) {
                DataRetryManager.DataSetupRetryEntry dataSetupRetryEntry2 = dataSetupRetryEntry;
                if (dataSetupRetryEntry2 != null) {
                    dataSetupRetryEntry2.setState(2);
                }
                DataNetworkController.this.onDataNetworkSetupFailed(dataNetwork2, networkRequestList, i, j);
            }

            @Override // com.android.internal.telephony.data.DataNetwork.DataNetworkCallback
            public void onConnected(DataNetwork dataNetwork2) {
                DataRetryManager.DataSetupRetryEntry dataSetupRetryEntry2 = dataSetupRetryEntry;
                if (dataSetupRetryEntry2 != null) {
                    dataSetupRetryEntry2.setState(3);
                }
                DataNetworkController.this.onDataNetworkConnected(dataNetwork2);
            }

            @Override // com.android.internal.telephony.data.DataNetwork.DataNetworkCallback
            public void onAttachFailed(DataNetwork dataNetwork2, NetworkRequestList networkRequestList) {
                DataNetworkController.this.onAttachNetworkRequestsFailed(dataNetwork2, networkRequestList);
            }

            @Override // com.android.internal.telephony.data.DataNetwork.DataNetworkCallback
            public void onValidationStatusChanged(DataNetwork dataNetwork2, int i, Uri uri) {
                DataNetworkController.this.onDataNetworkValidationStatusChanged(dataNetwork2, i, uri);
            }

            @Override // com.android.internal.telephony.data.DataNetwork.DataNetworkCallback
            public void onSuspendedStateChanged(DataNetwork dataNetwork2, boolean z) {
                DataNetworkController.this.onDataNetworkSuspendedStateChanged(dataNetwork2, z);
            }

            @Override // com.android.internal.telephony.data.DataNetwork.DataNetworkCallback
            public void onDisconnected(DataNetwork dataNetwork2, int i, int i2) {
                DataNetworkController.this.onDataNetworkDisconnected(dataNetwork2, i, i2);
            }

            @Override // com.android.internal.telephony.data.DataNetwork.DataNetworkCallback
            public void onHandoverSucceeded(DataNetwork dataNetwork2) {
                DataNetworkController.this.onDataNetworkHandoverSucceeded(dataNetwork2);
            }

            @Override // com.android.internal.telephony.data.DataNetwork.DataNetworkCallback
            public void onHandoverFailed(DataNetwork dataNetwork2, int i, long j, int i2) {
                DataNetworkController.this.onDataNetworkHandoverFailed(dataNetwork2, i, j, i2);
            }

            @Override // com.android.internal.telephony.data.DataNetwork.DataNetworkCallback
            public void onLinkStatusChanged(DataNetwork dataNetwork2, int i) {
                DataNetworkController.this.onLinkStatusChanged(dataNetwork2, i);
            }

            @Override // com.android.internal.telephony.data.DataNetwork.DataNetworkCallback
            public void onPcoDataChanged(DataNetwork dataNetwork2) {
                DataNetworkController.this.onPcoDataChanged(dataNetwork2);
            }

            @Override // com.android.internal.telephony.data.DataNetwork.DataNetworkCallback
            public void onNetworkCapabilitiesChanged(DataNetwork dataNetwork2) {
                DataNetworkController.this.onNetworkCapabilitiesChanged(dataNetwork2);
            }

            @Override // com.android.internal.telephony.data.DataNetwork.DataNetworkCallback
            public void onTrackNetworkUnwanted(DataNetwork dataNetwork2) {
                DataNetworkController.this.onTrackNetworkUnwanted();
            }
        }));
        if (this.mAnyDataNetworkExisting) {
            return;
        }
        this.mAnyDataNetworkExisting = true;
        this.mDataNetworkControllerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda22
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DataNetworkController.this.lambda$setupDataNetwork$26((DataNetworkController.DataNetworkControllerCallback) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setupDataNetwork$26(final DataNetworkControllerCallback dataNetworkControllerCallback) {
        dataNetworkControllerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda61
            @Override // java.lang.Runnable
            public final void run() {
                DataNetworkController.this.lambda$setupDataNetwork$25(dataNetworkControllerCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setupDataNetwork$25(DataNetworkControllerCallback dataNetworkControllerCallback) {
        dataNetworkControllerCallback.onAnyDataNetworkExistingChanged(this.mAnyDataNetworkExisting);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDataNetworkSetupFailed(DataNetwork dataNetwork, NetworkRequestList networkRequestList, int i, long j) {
        logl("onDataNetworkSetupDataFailed: " + dataNetwork + ", cause=" + DataFailCause.toString(i) + ", retryDelayMillis=" + j + "ms.");
        this.mDataNetworkList.remove(dataNetwork);
        trackSetupDataCallFailure(dataNetwork.getTransport(), i);
        if (this.mAnyDataNetworkExisting && this.mDataNetworkList.isEmpty()) {
            this.mPendingTearDownAllNetworks = false;
            this.mAnyDataNetworkExisting = false;
            this.mDataNetworkControllerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda42
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DataNetworkController.this.lambda$onDataNetworkSetupFailed$28((DataNetworkController.DataNetworkControllerCallback) obj);
                }
            });
        }
        networkRequestList.removeIf(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda43
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$onDataNetworkSetupFailed$29;
                lambda$onDataNetworkSetupFailed$29 = DataNetworkController.this.lambda$onDataNetworkSetupFailed$29((TelephonyNetworkRequest) obj);
                return lambda$onDataNetworkSetupFailed$29;
            }
        });
        if (networkRequestList.isEmpty()) {
            log("onDataNetworkSetupFailed: All requests have been released. Will not evaluate retry.");
        } else {
            this.mDataRetryManager.evaluateDataSetupRetry(dataNetwork.getDataProfile(), dataNetwork.getTransport(), networkRequestList, i, j);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDataNetworkSetupFailed$28(final DataNetworkControllerCallback dataNetworkControllerCallback) {
        dataNetworkControllerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda62
            @Override // java.lang.Runnable
            public final void run() {
                DataNetworkController.this.lambda$onDataNetworkSetupFailed$27(dataNetworkControllerCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDataNetworkSetupFailed$27(DataNetworkControllerCallback dataNetworkControllerCallback) {
        dataNetworkControllerCallback.onAnyDataNetworkExistingChanged(this.mAnyDataNetworkExisting);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onDataNetworkSetupFailed$29(TelephonyNetworkRequest telephonyNetworkRequest) {
        return !this.mAllNetworkRequestList.contains(telephonyNetworkRequest);
    }

    private void trackSetupDataCallFailure(int i, int i2) {
        if (i == 1) {
            if (this.mPhone.getSignalStrength().getLevel() <= 1) {
                return;
            }
            if (i2 == 65535 || i2 == 65536) {
                reportAnomaly("RIL set up data call fails: unknown/unspecified error", "ce7d1465-d8e4-404a-b76f-de2c60bee843");
            }
            if (this.mSetupDataCallWwanFailureCounter.addOccurrence()) {
                reportAnomaly("RIL fails setup data call request " + this.mSetupDataCallWwanFailureCounter.getFrequencyString(), "e6a98b97-9e34-4977-9a92-01d52a6691f6");
            }
        } else if (i == 2) {
            if (i2 == 65535 || i2 == 65536) {
                reportAnomaly("IWLAN set up data call fails: unknown/unspecified error", "a16fc15c-815b-4908-b8e6-5f3bc7cbc20b");
            }
            if (this.mSetupDataCallWlanFailureCounter.addOccurrence()) {
                reportAnomaly("IWLAN data service fails setup data call request " + this.mSetupDataCallWlanFailureCounter.getFrequencyString(), "e2248d8b-d55f-42bd-871c-0cfd80c3ddd1");
            }
        } else {
            loge("trackSetupDataCallFailure: INVALID transport.");
        }
    }

    private void reportAnomaly(String str, String str2) {
        logl(str);
        AnomalyReporter.reportAnomaly(UUID.fromString(str2), str, this.mPhone.getCarrierId());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDataNetworkConnected(final DataNetwork dataNetwork) {
        logl("onDataNetworkConnected: " + dataNetwork);
        this.mDataNetworkControllerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda52
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DataNetworkController.lambda$onDataNetworkConnected$31(DataNetwork.this, (DataNetworkController.DataNetworkControllerCallback) obj);
            }
        });
        this.mPreviousConnectedDataNetworkList.add(0, dataNetwork);
        if (this.mPreviousConnectedDataNetworkList.size() > 10) {
            this.mPreviousConnectedDataNetworkList.remove(10);
        }
        updateOverallInternetDataState();
        if (dataNetwork.getNetworkCapabilities().hasCapability(4)) {
            logl("IMS data state changed from " + TelephonyUtils.dataStateToString(this.mImsDataNetworkState) + " to CONNECTED.");
            this.mImsDataNetworkState = 2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onDataNetworkConnected$31(final DataNetwork dataNetwork, final DataNetworkControllerCallback dataNetworkControllerCallback) {
        dataNetworkControllerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda56
            @Override // java.lang.Runnable
            public final void run() {
                DataNetworkController.lambda$onDataNetworkConnected$30(DataNetworkController.DataNetworkControllerCallback.this, dataNetwork);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onDataNetworkConnected$30(DataNetworkControllerCallback dataNetworkControllerCallback, DataNetwork dataNetwork) {
        dataNetworkControllerCallback.onDataNetworkConnected(dataNetwork.getTransport(), dataNetwork.getDataProfile());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDataNetworkSetupRetry(DataRetryManager.DataSetupRetryEntry dataSetupRetryEntry) {
        final NetworkRequestList networkRequestList = new NetworkRequestList(dataSetupRetryEntry.networkRequestList);
        networkRequestList.removeIf(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda28
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$onDataNetworkSetupRetry$32;
                lambda$onDataNetworkSetupRetry$32 = DataNetworkController.this.lambda$onDataNetworkSetupRetry$32((TelephonyNetworkRequest) obj);
                return lambda$onDataNetworkSetupRetry$32;
            }
        });
        if (networkRequestList.isEmpty()) {
            final List<NetworkRequestList> groupedUnsatisfiedNetworkRequests = getGroupedUnsatisfiedNetworkRequests();
            dataSetupRetryEntry.networkRequestList.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda29
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$onDataNetworkSetupRetry$34;
                    lambda$onDataNetworkSetupRetry$34 = DataNetworkController.lambda$onDataNetworkSetupRetry$34(groupedUnsatisfiedNetworkRequests, (TelephonyNetworkRequest) obj);
                    return lambda$onDataNetworkSetupRetry$34;
                }
            }).forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda30
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DataNetworkController.NetworkRequestList.this.add((TelephonyNetworkRequest) obj);
                }
            });
        }
        if (networkRequestList.isEmpty()) {
            loge("onDataNetworkSetupRetry: Request list is empty. Abort retry.");
            dataSetupRetryEntry.setState(4);
            return;
        }
        log("onDataNetworkSetupRetry: Request list:" + networkRequestList);
        TelephonyNetworkRequest telephonyNetworkRequest = networkRequestList.get(0);
        int preferredTransportByNetworkCapability = this.mAccessNetworksManager.getPreferredTransportByNetworkCapability(telephonyNetworkRequest.getApnTypeNetworkCapability());
        if (preferredTransportByNetworkCapability != dataSetupRetryEntry.transport) {
            log("Cannot re-satisfy " + telephonyNetworkRequest + " on " + AccessNetworkConstants.transportTypeToString(dataSetupRetryEntry.transport) + ". The preferred transport has switched to " + AccessNetworkConstants.transportTypeToString(preferredTransportByNetworkCapability) + ". " + dataSetupRetryEntry);
            dataSetupRetryEntry.setState(4);
            sendMessage(obtainMessage(5, DataEvaluation.DataEvaluationReason.PREFERRED_TRANSPORT_CHANGED));
            return;
        }
        DataEvaluation evaluateNetworkRequest = evaluateNetworkRequest(telephonyNetworkRequest, DataEvaluation.DataEvaluationReason.DATA_RETRY);
        if (!evaluateNetworkRequest.containsDisallowedReasons()) {
            DataProfile dataProfile = dataSetupRetryEntry.dataProfile;
            if (dataProfile == null) {
                dataProfile = evaluateNetworkRequest.getCandidateDataProfile();
            }
            if (dataProfile != null) {
                setupDataNetwork(dataProfile, dataSetupRetryEntry, evaluateNetworkRequest.getDataAllowedReason());
                return;
            }
            loge("onDataNetworkSetupRetry: Not able to find a suitable data profile to retry.");
            dataSetupRetryEntry.setState(2);
            return;
        }
        dataSetupRetryEntry.setState(2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onDataNetworkSetupRetry$32(TelephonyNetworkRequest telephonyNetworkRequest) {
        return !this.mAllNetworkRequestList.contains(telephonyNetworkRequest);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onDataNetworkSetupRetry$34(List list, final TelephonyNetworkRequest telephonyNetworkRequest) {
        return list.stream().anyMatch(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda65
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$onDataNetworkSetupRetry$33;
                lambda$onDataNetworkSetupRetry$33 = DataNetworkController.lambda$onDataNetworkSetupRetry$33(TelephonyNetworkRequest.this, (DataNetworkController.NetworkRequestList) obj);
                return lambda$onDataNetworkSetupRetry$33;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onDataNetworkSetupRetry$33(TelephonyNetworkRequest telephonyNetworkRequest, NetworkRequestList networkRequestList) {
        return networkRequestList.get(telephonyNetworkRequest.getCapabilities()) != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDataNetworkHandoverRetry(DataRetryManager.DataHandoverRetryEntry dataHandoverRetryEntry) {
        DataNetwork dataNetwork = dataHandoverRetryEntry.dataNetwork;
        if (!this.mDataNetworkList.contains(dataNetwork)) {
            log("onDataNetworkHandoverRetry: " + dataNetwork + " no longer exists.");
            dataHandoverRetryEntry.setState(4);
        } else if (!dataNetwork.isConnected()) {
            log("onDataNetworkHandoverRetry: " + dataNetwork + " is not in the right state.");
            dataHandoverRetryEntry.setState(4);
        } else {
            int preferredTransportByNetworkCapability = this.mAccessNetworksManager.getPreferredTransportByNetworkCapability(dataNetwork.getApnTypeNetworkCapability());
            if (dataNetwork.getTransport() == preferredTransportByNetworkCapability) {
                log("onDataNetworkHandoverRetry: " + dataNetwork + " is already on the preferred transport " + AccessNetworkConstants.transportTypeToString(preferredTransportByNetworkCapability) + ".");
                dataHandoverRetryEntry.setState(4);
                return;
            }
            logl("onDataNetworkHandoverRetry: Start handover " + dataNetwork + " to " + AccessNetworkConstants.transportTypeToString(preferredTransportByNetworkCapability) + ", " + dataHandoverRetryEntry);
            tryHandoverDataNetwork(dataNetwork, preferredTransportByNetworkCapability, dataHandoverRetryEntry);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDataNetworkHandoverRetryStopped(DataNetwork dataNetwork) {
        int preferredTransportByNetworkCapability = this.mAccessNetworksManager.getPreferredTransportByNetworkCapability(dataNetwork.getApnTypeNetworkCapability());
        if (dataNetwork.getTransport() == preferredTransportByNetworkCapability) {
            log("onDataNetworkHandoverRetryStopped: " + dataNetwork + " is already on the preferred transport " + AccessNetworkConstants.transportTypeToString(preferredTransportByNetworkCapability));
        } else if (dataNetwork.shouldDelayImsTearDownDueToInCall()) {
            log("onDataNetworkHandoverRetryStopped: Delay IMS tear down until call ends. " + dataNetwork);
        } else {
            tearDownGracefully(dataNetwork, 13);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDataNetworkValidationStatusChanged(DataNetwork dataNetwork, final int i, Uri uri) {
        String str;
        StringBuilder sb = new StringBuilder();
        sb.append("onDataNetworkValidationStatusChanged: ");
        sb.append(dataNetwork);
        sb.append(", validation status=");
        sb.append(DataUtils.validationStatusToString(i));
        if (uri != null) {
            str = ", " + uri;
        } else {
            str = PhoneConfigurationManager.SSSS;
        }
        sb.append(str);
        log(sb.toString());
        if (!TextUtils.isEmpty(uri.toString())) {
            Intent intent = new Intent("android.telephony.action.CARRIER_SIGNAL_REDIRECTED");
            intent.putExtra("android.telephony.extra.REDIRECTION_URL", uri);
            this.mPhone.getCarrierSignalAgent().notifyCarrierSignalReceivers(intent);
            log("Notify carrier signal receivers with redirectUri: " + uri);
        }
        if (i != 1 && i != 2) {
            loge("Invalid validation status " + i + " received.");
        } else if (!this.mDataSettingsManager.isRecoveryOnBadNetworkEnabled()) {
            log("Ignore data network validation status changed because data stall recovery is disabled.");
        } else if (dataNetwork.isInternetSupported()) {
            if (i == 2 && (dataNetwork.getCurrentState() == null || dataNetwork.isDisconnected())) {
                log("Ignoring invalid validation status for disconnected DataNetwork");
            } else {
                this.mDataNetworkControllerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda51
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        DataNetworkController.lambda$onDataNetworkValidationStatusChanged$36(i, (DataNetworkController.DataNetworkControllerCallback) obj);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onDataNetworkValidationStatusChanged$36(final int i, final DataNetworkControllerCallback dataNetworkControllerCallback) {
        dataNetworkControllerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda59
            @Override // java.lang.Runnable
            public final void run() {
                DataNetworkController.DataNetworkControllerCallback.this.onInternetDataNetworkValidationStatusChanged(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDataNetworkSuspendedStateChanged(DataNetwork dataNetwork, boolean z) {
        updateOverallInternetDataState();
        if (dataNetwork.getNetworkCapabilities().hasCapability(4)) {
            StringBuilder sb = new StringBuilder();
            sb.append("IMS data state changed from ");
            sb.append(TelephonyUtils.dataStateToString(this.mImsDataNetworkState));
            sb.append(" to ");
            sb.append(z ? "SUSPENDED" : "CONNECTED");
            logl(sb.toString());
            this.mImsDataNetworkState = z ? 3 : 2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDataNetworkDisconnected(DataNetwork dataNetwork, int i, int i2) {
        logl("onDataNetworkDisconnected: " + dataNetwork + ", cause=" + DataFailCause.toString(i) + "(" + i + "), tearDownReason=" + DataNetwork.tearDownReasonToString(i2));
        this.mDataNetworkList.remove(dataNetwork);
        this.mPendingImsDeregDataNetworks.remove(dataNetwork);
        this.mDataRetryManager.cancelPendingHandoverRetry(dataNetwork);
        updateOverallInternetDataState();
        if (dataNetwork.getNetworkCapabilities().hasCapability(4)) {
            logl("IMS data state changed from " + TelephonyUtils.dataStateToString(this.mImsDataNetworkState) + " to DISCONNECTED.");
            this.mImsDataNetworkState = 0;
        }
        if (this.mAnyDataNetworkExisting && this.mDataNetworkList.isEmpty()) {
            log("All data networks disconnected now.");
            this.mPendingTearDownAllNetworks = false;
            this.mAnyDataNetworkExisting = false;
            this.mDataNetworkControllerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda20
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DataNetworkController.this.lambda$onDataNetworkDisconnected$38((DataNetworkController.DataNetworkControllerCallback) obj);
                }
            });
        }
        sendMessageDelayed(obtainMessage(5, DataEvaluation.DataEvaluationReason.RETRY_AFTER_DISCONNECTED), i2 == 14 ? 0L : this.mDataConfigManager.getRetrySetupAfterDisconnectMillis());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDataNetworkDisconnected$38(final DataNetworkControllerCallback dataNetworkControllerCallback) {
        dataNetworkControllerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda54
            @Override // java.lang.Runnable
            public final void run() {
                DataNetworkController.this.lambda$onDataNetworkDisconnected$37(dataNetworkControllerCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDataNetworkDisconnected$37(DataNetworkControllerCallback dataNetworkControllerCallback) {
        dataNetworkControllerCallback.onAnyDataNetworkExistingChanged(this.mAnyDataNetworkExisting);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDataNetworkHandoverSucceeded(DataNetwork dataNetwork) {
        logl("Handover successfully. " + dataNetwork + " to " + AccessNetworkConstants.transportTypeToString(dataNetwork.getTransport()));
        sendMessage(obtainMessage(21, dataNetwork.getApnTypeNetworkCapability(), 0));
        sendMessage(obtainMessage(16, DataEvaluation.DataEvaluationReason.DATA_HANDOVER));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDataNetworkHandoverFailed(DataNetwork dataNetwork, int i, long j, int i2) {
        logl("Handover failed. " + dataNetwork + ", cause=" + DataFailCause.toString(i) + ", retryDelayMillis=" + j + "ms, handoverFailureMode=" + DataCallResponse.failureModeToString(i2));
        sendMessage(obtainMessage(16, DataEvaluation.DataEvaluationReason.DATA_HANDOVER));
        if (dataNetwork.getAttachedNetworkRequestList().isEmpty()) {
            log("onDataNetworkHandoverFailed: No network requests attached to " + dataNetwork + ". No need to retry since the network will be torn down soon.");
        } else if (i2 == 1 || (i2 == 0 && i == 2251)) {
            sendMessageDelayed(obtainMessage(21, dataNetwork.getApnTypeNetworkCapability(), 0), REEVALUATE_PREFERRED_TRANSPORT_DELAY_MILLIS);
        } else if (i2 == 3 || i2 == 0) {
            int preferredTransportByNetworkCapability = this.mAccessNetworksManager.getPreferredTransportByNetworkCapability(dataNetwork.getApnTypeNetworkCapability());
            if (dataNetwork.getTransport() == preferredTransportByNetworkCapability) {
                log("onDataNetworkHandoverFailed: Already on preferred transport " + AccessNetworkConstants.transportTypeToString(preferredTransportByNetworkCapability) + ". No further actions needed.");
                return;
            }
            this.mDataRetryManager.evaluateDataSetupRetry(dataNetwork.getDataProfile(), DataUtils.getTargetTransport(dataNetwork.getTransport()), dataNetwork.getAttachedNetworkRequestList(), i, j);
            tearDownGracefully(dataNetwork, 13);
        } else {
            this.mDataRetryManager.evaluateDataHandoverRetry(dataNetwork, i, j);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAttachNetworkRequestsFailed(DataNetwork dataNetwork, NetworkRequestList networkRequestList) {
        log("Failed to attach " + networkRequestList + " to " + dataNetwork);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDataStallReestablishInternet() {
        log("onDataStallReestablishInternet: Tear down data networks that support internet.");
        this.mDataNetworkList.stream().filter(new DataNetworkController$$ExternalSyntheticLambda10()).forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda31
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((DataNetwork) obj).lambda$tearDownWhenConditionMet$9(12);
            }
        });
    }

    private void onSrvccStateChanged(int[] iArr) {
        if (iArr == null || iArr.length == 0) {
            return;
        }
        log("onSrvccStateChanged: " + TelephonyManager.srvccStateToString(iArr[0]));
        boolean z = iArr[0] == 0;
        this.mIsSrvccHandoverInProcess = z;
        if (z || hasMessages(16)) {
            return;
        }
        sendMessage(obtainMessage(16, DataEvaluation.DataEvaluationReason.SRVCC_STATE_CHANGED));
    }

    private void onDataServiceBindingChanged(final int i, boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append("onDataServiceBindingChanged: ");
        sb.append(AccessNetworkConstants.transportTypeToString(i));
        sb.append(" data service is ");
        sb.append(z ? "bound." : "unbound.");
        log(sb.toString());
        if (z) {
            this.mDataNetworkControllerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda41
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DataNetworkController.lambda$onDataServiceBindingChanged$41(i, (DataNetworkController.DataNetworkControllerCallback) obj);
                }
            });
        }
        this.mDataServiceBound.put(i, z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onDataServiceBindingChanged$41(final int i, final DataNetworkControllerCallback dataNetworkControllerCallback) {
        dataNetworkControllerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda60
            @Override // java.lang.Runnable
            public final void run() {
                DataNetworkController.DataNetworkControllerCallback.this.onDataServiceBound(i);
            }
        });
    }

    private void onSimAbsent() {
        log("onSimAbsent");
        sendMessage(obtainMessage(16, DataEvaluation.DataEvaluationReason.SIM_REMOVAL));
    }

    private void onSimStateChanged(@TelephonyManager.SimState int i) {
        log("onSimStateChanged: state=" + TelephonyManager.simStateToString(i));
        if (this.mSimState != i) {
            this.mSimState = i;
            if (i == 1) {
                onSimAbsent();
            } else if (i == 10) {
                sendMessage(obtainMessage(5, DataEvaluation.DataEvaluationReason.SIM_LOADED));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onEvaluatePreferredTransport(int i) {
        int preferredTransportByNetworkCapability = this.mAccessNetworksManager.getPreferredTransportByNetworkCapability(i);
        log("onEvaluatePreferredTransport: " + DataUtils.networkCapabilityToString(i) + " preferred on " + AccessNetworkConstants.transportTypeToString(preferredTransportByNetworkCapability));
        for (DataNetwork dataNetwork : this.mDataNetworkList) {
            if (dataNetwork.getApnTypeNetworkCapability() == i) {
                if (dataNetwork.getTransport() == preferredTransportByNetworkCapability) {
                    log("onEvaluatePreferredTransport:" + dataNetwork + " already on " + AccessNetworkConstants.transportTypeToString(preferredTransportByNetworkCapability));
                } else if (dataNetwork.isHandoverInProgress()) {
                    log("onEvaluatePreferredTransport: " + dataNetwork + " handover in progress.");
                } else {
                    tryHandoverDataNetwork(dataNetwork, preferredTransportByNetworkCapability, null);
                }
            }
        }
    }

    private void tryHandoverDataNetwork(DataNetwork dataNetwork, int i, DataRetryManager.DataHandoverRetryEntry dataHandoverRetryEntry) {
        if (dataHandoverRetryEntry == null && this.mDataRetryManager.isAnyHandoverRetryScheduled(dataNetwork)) {
            log("tryHandoverDataNetwork: retry scheduled for" + dataNetwork + ", ignore this attempt");
            return;
        }
        DataEvaluation evaluateDataNetworkHandover = evaluateDataNetworkHandover(dataNetwork);
        log("tryHandoverDataNetwork: " + evaluateDataNetworkHandover + ", " + dataNetwork);
        if (!evaluateDataNetworkHandover.containsDisallowedReasons()) {
            logl("Start handover " + dataNetwork + " to " + AccessNetworkConstants.transportTypeToString(i));
            dataNetwork.startHandover(i, dataHandoverRetryEntry);
            return;
        }
        DataEvaluation.DataDisallowedReason dataDisallowedReason = DataEvaluation.DataDisallowedReason.NOT_IN_SERVICE;
        if (evaluateDataNetworkHandover.containsOnly(dataDisallowedReason) && dataNetwork.shouldDelayImsTearDownDueToInCall()) {
            if (dataHandoverRetryEntry != null) {
                dataHandoverRetryEntry.setState(2);
            }
            this.mDataRetryManager.evaluateDataHandoverRetry(dataNetwork, 65542, -1L);
            logl("tryHandoverDataNetwork: Scheduled retry due to in voice call and target OOS");
        } else if (evaluateDataNetworkHandover.containsAny(DataEvaluation.DataDisallowedReason.NOT_ALLOWED_BY_POLICY, dataDisallowedReason, DataEvaluation.DataDisallowedReason.VOPS_NOT_SUPPORTED)) {
            logl("tryHandoverDataNetwork: Handover not allowed. Tear down" + dataNetwork + " so a new network can be setup on " + AccessNetworkConstants.transportTypeToString(i));
            tearDownGracefully(dataNetwork, 14);
        } else if (evaluateDataNetworkHandover.containsAny(DataEvaluation.DataDisallowedReason.ILLEGAL_STATE, DataEvaluation.DataDisallowedReason.RETRY_SCHEDULED)) {
            logl("tryHandoverDataNetwork: Handover not allowed. " + dataNetwork + " will remain on " + AccessNetworkConstants.transportTypeToString(dataNetwork.getTransport()));
        } else {
            loge("tryHandoverDataNetwork: Unexpected handover evaluation result.");
        }
    }

    private void updateSubscriptionPlans() {
        SubscriptionPlan[] subscriptionPlans = this.mNetworkPolicyManager.getSubscriptionPlans(this.mSubId, this.mPhone.getContext().getOpPackageName());
        this.mSubscriptionPlans.clear();
        this.mSubscriptionPlans.addAll(subscriptionPlans != null ? Arrays.asList(subscriptionPlans) : Collections.emptyList());
        this.mCongestedOverrideNetworkTypes.clear();
        this.mUnmeteredOverrideNetworkTypes.clear();
        log("Subscription plans initialized: " + this.mSubscriptionPlans);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onLinkStatusChanged(DataNetwork dataNetwork, int i) {
        int i2;
        if (this.mDataNetworkList.stream().anyMatch(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda32
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$onLinkStatusChanged$42;
                lambda$onLinkStatusChanged$42 = DataNetworkController.lambda$onLinkStatusChanged$42((DataNetwork) obj);
                return lambda$onLinkStatusChanged$42;
            }
        })) {
            i2 = this.mDataNetworkList.stream().anyMatch(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda33
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$onLinkStatusChanged$43;
                    lambda$onLinkStatusChanged$43 = DataNetworkController.lambda$onLinkStatusChanged$43((DataNetwork) obj);
                    return lambda$onLinkStatusChanged$43;
                }
            }) ? 2 : 1;
        } else {
            i2 = 0;
        }
        if (this.mInternetLinkStatus != i2) {
            log("Internet link status changed to " + DataUtils.linkStatusToString(i2));
            this.mInternetLinkStatus = i2;
            this.mDataNetworkControllerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda34
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DataNetworkController.this.lambda$onLinkStatusChanged$45((DataNetworkController.DataNetworkControllerCallback) obj);
                }
            });
        }
        updateDataActivity();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onLinkStatusChanged$42(DataNetwork dataNetwork) {
        return dataNetwork.isInternetSupported() && dataNetwork.isConnected();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onLinkStatusChanged$43(DataNetwork dataNetwork) {
        return dataNetwork.isInternetSupported() && dataNetwork.isConnected() && dataNetwork.getLinkStatus() == 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onLinkStatusChanged$45(final DataNetworkControllerCallback dataNetworkControllerCallback) {
        dataNetworkControllerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda53
            @Override // java.lang.Runnable
            public final void run() {
                DataNetworkController.this.lambda$onLinkStatusChanged$44(dataNetworkControllerCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onLinkStatusChanged$44(DataNetworkControllerCallback dataNetworkControllerCallback) {
        dataNetworkControllerCallback.onPhysicalLinkStatusChanged(this.mInternetLinkStatus);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPcoDataChanged(DataNetwork dataNetwork) {
        boolean z;
        int nrAdvancedCapablePcoId = this.mDataConfigManager.getNrAdvancedCapablePcoId();
        if (nrAdvancedCapablePcoId != 0) {
            Iterator<DataNetwork> it = this.mDataNetworkList.iterator();
            while (true) {
                if (!it.hasNext()) {
                    z = false;
                    break;
                }
                PcoData pcoData = it.next().getPcoData().get(Integer.valueOf(nrAdvancedCapablePcoId));
                if (pcoData != null) {
                    byte[] bArr = pcoData.contents;
                    if (bArr.length > 0) {
                        z = true;
                        if (bArr[bArr.length - 1] == 1) {
                            break;
                        }
                    } else {
                        continue;
                    }
                }
            }
            if (z != this.mNrAdvancedCapableByPco) {
                log("onPcoDataChanged: mNrAdvancedCapableByPco = " + z);
                this.mNrAdvancedCapableByPco = z;
                this.mDataNetworkControllerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda35
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        DataNetworkController.this.lambda$onPcoDataChanged$47((DataNetworkController.DataNetworkControllerCallback) obj);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onPcoDataChanged$47(final DataNetworkControllerCallback dataNetworkControllerCallback) {
        dataNetworkControllerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda63
            @Override // java.lang.Runnable
            public final void run() {
                DataNetworkController.this.lambda$onPcoDataChanged$46(dataNetworkControllerCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onPcoDataChanged$46(DataNetworkControllerCallback dataNetworkControllerCallback) {
        dataNetworkControllerCallback.onNrAdvancedCapableByPcoChanged(this.mNrAdvancedCapableByPco);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNetworkCapabilitiesChanged(DataNetwork dataNetwork) {
        NetworkRequestList networkRequestList = new NetworkRequestList();
        Iterator<TelephonyNetworkRequest> it = this.mAllNetworkRequestList.iterator();
        while (it.hasNext()) {
            TelephonyNetworkRequest next = it.next();
            if (next.getState() == 0 && next.canBeSatisfiedBy(dataNetwork.getNetworkCapabilities())) {
                networkRequestList.add(next);
            }
        }
        if (!networkRequestList.isEmpty()) {
            log("Found more network requests that can be satisfied. " + networkRequestList);
            dataNetwork.attachNetworkRequests(networkRequestList);
        }
        if (dataNetwork.getNetworkCapabilities().hasCapability(12)) {
            updateOverallInternetDataState();
        }
    }

    private boolean shouldReevaluateDataNetworks(NetworkRegistrationInfo networkRegistrationInfo, NetworkRegistrationInfo networkRegistrationInfo2) {
        if (networkRegistrationInfo == null || networkRegistrationInfo2 == null || networkRegistrationInfo2.getAccessNetworkTechnology() == 0) {
            return false;
        }
        if (networkRegistrationInfo.getAccessNetworkTechnology() != networkRegistrationInfo2.getAccessNetworkTechnology() || (!networkRegistrationInfo.isRoaming() && networkRegistrationInfo2.isRoaming())) {
            return true;
        }
        DataSpecificRegistrationInfo dataSpecificInfo = networkRegistrationInfo.getDataSpecificInfo();
        DataSpecificRegistrationInfo dataSpecificInfo2 = networkRegistrationInfo2.getDataSpecificInfo();
        if (dataSpecificInfo2 == null) {
            return false;
        }
        return ((dataSpecificInfo != null && dataSpecificInfo.getVopsSupportInfo() != null && !dataSpecificInfo.getVopsSupportInfo().isVopsSupported()) || dataSpecificInfo2.getVopsSupportInfo() == null || dataSpecificInfo2.getVopsSupportInfo().isVopsSupported()) ? false : true;
    }

    private boolean shouldReevaluateNetworkRequests(ServiceState serviceState, ServiceState serviceState2, int i) {
        NetworkRegistrationInfo networkRegistrationInfo = serviceState.getNetworkRegistrationInfo(2, i);
        NetworkRegistrationInfo networkRegistrationInfo2 = serviceState2.getNetworkRegistrationInfo(2, i);
        if (networkRegistrationInfo2 == null || networkRegistrationInfo2.getAccessNetworkTechnology() == 0) {
            return false;
        }
        if (networkRegistrationInfo == null || networkRegistrationInfo.getAccessNetworkTechnology() != networkRegistrationInfo2.getAccessNetworkTechnology() || (!networkRegistrationInfo.isInService() && networkRegistrationInfo2.isInService())) {
            return true;
        }
        if (serviceStateAllowsPSAttach(serviceState, i) || !serviceStateAllowsPSAttach(serviceState2, i)) {
            DataSpecificRegistrationInfo dataSpecificInfo = networkRegistrationInfo.getDataSpecificInfo();
            DataSpecificRegistrationInfo dataSpecificInfo2 = networkRegistrationInfo2.getDataSpecificInfo();
            if (dataSpecificInfo == null) {
                return false;
            }
            return ((dataSpecificInfo2 != null && dataSpecificInfo2.getVopsSupportInfo() != null && !dataSpecificInfo2.getVopsSupportInfo().isVopsSupported()) || dataSpecificInfo.getVopsSupportInfo() == null || dataSpecificInfo.getVopsSupportInfo().isVopsSupported()) ? false : true;
        }
        return true;
    }

    private void onServiceStateChanged() {
        boolean z;
        ServiceState serviceState = this.mPhone.getServiceStateTracker().getServiceState();
        StringBuilder sb = new StringBuilder("onServiceStateChanged: ");
        int i = 0;
        if (!this.mServiceState.equals(serviceState)) {
            log("onServiceStateChanged: changed to " + serviceState);
            int[] availableTransports = this.mAccessNetworksManager.getAvailableTransports();
            int length = availableTransports.length;
            int i2 = 0;
            z = false;
            while (i < length) {
                int i3 = availableTransports[i];
                NetworkRegistrationInfo networkRegistrationInfo = this.mServiceState.getNetworkRegistrationInfo(2, i3);
                NetworkRegistrationInfo networkRegistrationInfo2 = serviceState.getNetworkRegistrationInfo(2, i3);
                sb.append("[");
                sb.append(AccessNetworkConstants.transportTypeToString(i3));
                sb.append(": ");
                sb.append(networkRegistrationInfo != null ? TelephonyManager.getNetworkTypeName(networkRegistrationInfo.getAccessNetworkTechnology()) : null);
                sb.append("->");
                sb.append(networkRegistrationInfo2 != null ? TelephonyManager.getNetworkTypeName(networkRegistrationInfo2.getAccessNetworkTechnology()) : null);
                sb.append(", ");
                sb.append(networkRegistrationInfo != null ? NetworkRegistrationInfo.registrationStateToString(networkRegistrationInfo.getRegistrationState()) : null);
                sb.append("->");
                sb.append(networkRegistrationInfo2 != null ? NetworkRegistrationInfo.registrationStateToString(networkRegistrationInfo2.getRegistrationState()) : null);
                sb.append("] ");
                if (shouldReevaluateDataNetworks(networkRegistrationInfo, networkRegistrationInfo2) && !hasMessages(16)) {
                    sendMessage(obtainMessage(16, DataEvaluation.DataEvaluationReason.DATA_SERVICE_STATE_CHANGED));
                    z = true;
                }
                if (shouldReevaluateNetworkRequests(this.mServiceState, serviceState, i3) && !hasMessages(5)) {
                    sendMessage(obtainMessage(5, DataEvaluation.DataEvaluationReason.DATA_SERVICE_STATE_CHANGED));
                    i2 = 1;
                }
                i++;
            }
            this.mServiceState = serviceState;
            i = i2;
        } else {
            sb.append("not changed");
            z = false;
        }
        sb.append(". Evaluating network requests is ");
        String str = PhoneConfigurationManager.SSSS;
        sb.append(i != 0 ? PhoneConfigurationManager.SSSS : "not ");
        sb.append("needed, evaluating existing data networks is ");
        if (!z) {
            str = "not ";
        }
        sb.append(str);
        sb.append("needed.");
        log(sb.toString());
    }

    private void updateOverallInternetDataState() {
        boolean anyMatch = this.mDataNetworkList.stream().anyMatch(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda44
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updateOverallInternetDataState$48;
                lambda$updateOverallInternetDataState$48 = DataNetworkController.lambda$updateOverallInternetDataState$48((DataNetwork) obj);
                return lambda$updateOverallInternetDataState$48;
            }
        });
        final List list = (List) this.mDataNetworkList.stream().filter(new DataNetworkController$$ExternalSyntheticLambda10()).filter(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda45
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updateOverallInternetDataState$49;
                lambda$updateOverallInternetDataState$49 = DataNetworkController.lambda$updateOverallInternetDataState$49((DataNetwork) obj);
                return lambda$updateOverallInternetDataState$49;
            }
        }).collect(Collectors.toList());
        int i = 0;
        boolean z = !list.isEmpty() && list.stream().allMatch(new Predicate() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda46
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((DataNetwork) obj).isSuspended();
            }
        });
        logv("isSuspended=" + z + ", anyInternetConnected=" + anyMatch + ", mDataNetworkList=" + this.mDataNetworkList);
        if (z) {
            i = 3;
        } else if (anyMatch) {
            i = 2;
        }
        if (this.mInternetDataNetworkState != i) {
            logl("Internet data state changed from " + TelephonyUtils.dataStateToString(this.mInternetDataNetworkState) + " to " + TelephonyUtils.dataStateToString(i) + ".");
            if (i == 2 && this.mInternetDataNetworkState == 0) {
                this.mDataNetworkControllerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda47
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        DataNetworkController.lambda$updateOverallInternetDataState$51(list, (DataNetworkController.DataNetworkControllerCallback) obj);
                    }
                });
            } else if (i == 0 && this.mInternetDataNetworkState == 2) {
                this.mDataNetworkControllerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda48
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        DataNetworkController.lambda$updateOverallInternetDataState$52((DataNetworkController.DataNetworkControllerCallback) obj);
                    }
                });
            }
            this.mInternetDataNetworkState = i;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateOverallInternetDataState$48(DataNetwork dataNetwork) {
        return dataNetwork.isInternetSupported() && (dataNetwork.isConnected() || dataNetwork.isHandoverInProgress());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateOverallInternetDataState$49(DataNetwork dataNetwork) {
        return dataNetwork.isConnected() || dataNetwork.isHandoverInProgress();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateOverallInternetDataState$51(final List list, final DataNetworkControllerCallback dataNetworkControllerCallback) {
        dataNetworkControllerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda55
            @Override // java.lang.Runnable
            public final void run() {
                DataNetworkController.lambda$updateOverallInternetDataState$50(DataNetworkController.DataNetworkControllerCallback.this, list);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateOverallInternetDataState$50(DataNetworkControllerCallback dataNetworkControllerCallback, List list) {
        dataNetworkControllerCallback.onInternetDataNetworkConnected((List) list.stream().map(new Function() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda66
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((DataNetwork) obj).getDataProfile();
            }
        }).collect(Collectors.toList()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateOverallInternetDataState$52(final DataNetworkControllerCallback dataNetworkControllerCallback) {
        Objects.requireNonNull(dataNetworkControllerCallback);
        dataNetworkControllerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda64
            @Override // java.lang.Runnable
            public final void run() {
                DataNetworkController.DataNetworkControllerCallback.this.onInternetDataNetworkDisconnected();
            }
        });
    }

    public DataConfigManager getDataConfigManager() {
        return this.mDataConfigManager;
    }

    public DataProfileManager getDataProfileManager() {
        return this.mDataProfileManager;
    }

    public DataSettingsManager getDataSettingsManager() {
        return this.mDataSettingsManager;
    }

    public DataRetryManager getDataRetryManager() {
        return this.mDataRetryManager;
    }

    @VisibleForTesting
    public List<SubscriptionPlan> getSubscriptionPlans() {
        return this.mSubscriptionPlans;
    }

    @VisibleForTesting
    public Set<Integer> getUnmeteredOverrideNetworkTypes() {
        return this.mUnmeteredOverrideNetworkTypes;
    }

    @VisibleForTesting
    public Set<Integer> getCongestedOverrideNetworkTypes() {
        return this.mCongestedOverrideNetworkTypes;
    }

    private int getDataNetworkType(int i) {
        NetworkRegistrationInfo networkRegistrationInfo = this.mServiceState.getNetworkRegistrationInfo(2, i);
        if (networkRegistrationInfo != null) {
            return networkRegistrationInfo.getAccessNetworkTechnology();
        }
        return 0;
    }

    private int getDataRegistrationState(ServiceState serviceState, int i) {
        NetworkRegistrationInfo networkRegistrationInfo = serviceState.getNetworkRegistrationInfo(2, i);
        if (networkRegistrationInfo != null) {
            return networkRegistrationInfo.getRegistrationState();
        }
        return 4;
    }

    public int getDataActivity() {
        return this.mDataActivity;
    }

    public void registerDataNetworkControllerCallback(DataNetworkControllerCallback dataNetworkControllerCallback) {
        sendMessage(obtainMessage(13, dataNetworkControllerCallback));
    }

    public void unregisterDataNetworkControllerCallback(DataNetworkControllerCallback dataNetworkControllerCallback) {
        sendMessage(obtainMessage(14, dataNetworkControllerCallback));
    }

    public void tearDownAllDataNetworks(int i) {
        sendMessage(obtainMessage(12, i, 0));
    }

    public void onTearDownAllDataNetworks(int i) {
        log("onTearDownAllDataNetworks: reason=" + DataNetwork.tearDownReasonToString(i));
        if (this.mDataNetworkList.isEmpty()) {
            log("tearDownAllDataNetworks: No pending networks. All disconnected now.");
            return;
        }
        this.mPendingTearDownAllNetworks = true;
        for (DataNetwork dataNetwork : this.mDataNetworkList) {
            if (!dataNetwork.isDisconnecting()) {
                tearDownGracefully(dataNetwork, i);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void evaluatePendingImsDeregDataNetworks() {
        Iterator<Map.Entry<DataNetwork, Runnable>> it = this.mPendingImsDeregDataNetworks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<DataNetwork, Runnable> next = it.next();
            if (isSafeToTearDown(next.getKey())) {
                log("evaluatePendingImsDeregDataNetworks: Safe to tear down data network " + next.getKey() + " now.");
                next.getValue().run();
                it.remove();
            } else {
                log("Still not safe to tear down " + next.getKey() + ".");
            }
        }
    }

    private boolean isSafeToTearDown(DataNetwork dataNetwork) {
        for (Integer num : SUPPORTED_IMS_FEATURES) {
            int intValue = num.intValue();
            String str = this.mImsFeaturePackageName.get(intValue);
            if (str != null && dataNetwork.getAttachedNetworkRequestList().hasNetworkRequestsFromPackage(str) && this.mRegisteredImsFeatures.contains(Integer.valueOf(intValue))) {
                return false;
            }
        }
        return true;
    }

    private boolean isImsGracefulTearDownSupported() {
        return this.mDataConfigManager.getImsDeregistrationDelay() > 0;
    }

    private void tearDownGracefully(DataNetwork dataNetwork, int i) {
        long imsDeregistrationDelay = this.mDataConfigManager.getImsDeregistrationDelay();
        if (isImsGracefulTearDownSupported() && !isSafeToTearDown(dataNetwork)) {
            StringBuilder sb = new StringBuilder();
            sb.append("tearDownGracefully: Not safe to tear down ");
            sb.append(dataNetwork);
            sb.append(" at this point. Wait for IMS de-registration or timeout. MMTEL=");
            sb.append(this.mRegisteredImsFeatures.contains(1) ? "registered" : "not registered");
            sb.append(", RCS=");
            sb.append(this.mRegisteredImsFeatures.contains(2) ? "registered" : "not registered");
            log(sb.toString());
            Runnable tearDownWhenConditionMet = dataNetwork.tearDownWhenConditionMet(i, imsDeregistrationDelay);
            if (tearDownWhenConditionMet != null) {
                this.mPendingImsDeregDataNetworks.put(dataNetwork, tearDownWhenConditionMet);
                return;
            }
            log(dataNetwork + " is being torn down already.");
            return;
        }
        log("tearDownGracefully: Safe to tear down " + dataNetwork);
        dataNetwork.lambda$tearDownWhenConditionMet$9(i);
    }

    public int getInternetDataNetworkState() {
        return this.mInternetDataNetworkState;
    }

    public List<String> getDataServicePackages() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.mDataServiceManagers.size(); i++) {
            arrayList.add(this.mDataServiceManagers.valueAt(i).getDataServicePackageName());
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String str) {
        Rlog.d(this.mLogTag, str);
    }

    private void loge(String str) {
        Rlog.e(this.mLogTag, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logl(String str) {
        log(str);
        this.mLocalLog.log(str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        final AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter = new AndroidUtilIndentingPrintWriter(printWriter, "  ");
        androidUtilIndentingPrintWriter.println(DataNetworkController.class.getSimpleName() + "-" + this.mPhone.getPhoneId() + ":");
        androidUtilIndentingPrintWriter.increaseIndent();
        androidUtilIndentingPrintWriter.println("Current data networks:");
        androidUtilIndentingPrintWriter.increaseIndent();
        for (DataNetwork dataNetwork : this.mDataNetworkList) {
            dataNetwork.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        }
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println("Pending tear down data networks:");
        androidUtilIndentingPrintWriter.increaseIndent();
        for (DataNetwork dataNetwork2 : this.mPendingImsDeregDataNetworks.keySet()) {
            dataNetwork2.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        }
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println("Previously connected data networks: (up to 10)");
        androidUtilIndentingPrintWriter.increaseIndent();
        for (DataNetwork dataNetwork3 : this.mPreviousConnectedDataNetworkList) {
            if (!this.mDataNetworkList.contains(dataNetwork3)) {
                dataNetwork3.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
            }
        }
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println("All telephony network requests:");
        androidUtilIndentingPrintWriter.increaseIndent();
        Iterator<TelephonyNetworkRequest> it = this.mAllNetworkRequestList.iterator();
        while (it.hasNext()) {
            androidUtilIndentingPrintWriter.println(it.next());
        }
        androidUtilIndentingPrintWriter.decreaseIndent();
        StringBuilder sb = new StringBuilder();
        sb.append("IMS features registration state: MMTEL=");
        sb.append(this.mRegisteredImsFeatures.contains(1) ? "registered" : "not registered");
        sb.append(", RCS=");
        sb.append(this.mRegisteredImsFeatures.contains(2) ? "registered" : "not registered");
        androidUtilIndentingPrintWriter.println(sb.toString());
        androidUtilIndentingPrintWriter.println("mServiceState=" + this.mServiceState);
        androidUtilIndentingPrintWriter.println("mPsRestricted=" + this.mPsRestricted);
        androidUtilIndentingPrintWriter.println("mAnyDataNetworkExisting=" + this.mAnyDataNetworkExisting);
        androidUtilIndentingPrintWriter.println("mInternetDataNetworkState=" + TelephonyUtils.dataStateToString(this.mInternetDataNetworkState));
        androidUtilIndentingPrintWriter.println("mImsDataNetworkState=" + TelephonyUtils.dataStateToString(this.mImsDataNetworkState));
        androidUtilIndentingPrintWriter.println("mDataServiceBound=" + this.mDataServiceBound);
        androidUtilIndentingPrintWriter.println("mIsSrvccHandoverInProcess=" + this.mIsSrvccHandoverInProcess);
        androidUtilIndentingPrintWriter.println("mSimState=" + TelephonyManager.simStateToString(this.mSimState));
        androidUtilIndentingPrintWriter.println("mDataNetworkControllerCallbacks=" + this.mDataNetworkControllerCallbacks);
        androidUtilIndentingPrintWriter.println("Subscription plans:");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mSubscriptionPlans.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataNetworkController$$ExternalSyntheticLambda16
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AndroidUtilIndentingPrintWriter.this.println((SubscriptionPlan) obj);
            }
        });
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println("Unmetered override network types=" + ((String) this.mUnmeteredOverrideNetworkTypes.stream().map(new DataConfigManager$$ExternalSyntheticLambda8()).collect(Collectors.joining(","))));
        androidUtilIndentingPrintWriter.println("Congested override network types=" + ((String) this.mCongestedOverrideNetworkTypes.stream().map(new DataConfigManager$$ExternalSyntheticLambda8()).collect(Collectors.joining(","))));
        androidUtilIndentingPrintWriter.println("mImsThrottleCounter=" + this.mImsThrottleCounter);
        androidUtilIndentingPrintWriter.println("mNetworkUnwantedCounter=" + this.mNetworkUnwantedCounter);
        androidUtilIndentingPrintWriter.println("Local logs:");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mLocalLog.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println("-------------------------------------");
        this.mDataProfileManager.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        androidUtilIndentingPrintWriter.println("-------------------------------------");
        this.mDataRetryManager.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        androidUtilIndentingPrintWriter.println("-------------------------------------");
        this.mDataSettingsManager.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        androidUtilIndentingPrintWriter.println("-------------------------------------");
        this.mDataStallRecoveryManager.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        androidUtilIndentingPrintWriter.println("-------------------------------------");
        this.mDataConfigManager.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        androidUtilIndentingPrintWriter.decreaseIndent();
    }
}
