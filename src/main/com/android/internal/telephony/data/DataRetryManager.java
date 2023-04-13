package com.android.internal.telephony.data;

import android.os.AsyncResult;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.AccessNetworkConstants;
import android.telephony.AnomalyReporter;
import android.telephony.DataFailCause;
import android.telephony.data.DataProfile;
import android.telephony.data.ThrottleStatus;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.AndroidUtilIndentingPrintWriter;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.LocalLog;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.data.DataConfigManager;
import com.android.internal.telephony.data.DataNetworkController;
import com.android.internal.telephony.data.DataProfileManager;
import com.android.internal.telephony.data.DataRetryManager;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/* loaded from: classes.dex */
public class DataRetryManager extends Handler {
    private final DataConfigManager mDataConfigManager;
    private List<DataHandoverRetryRule> mDataHandoverRetryRuleList;
    private final DataProfileManager mDataProfileManager;
    private final List<DataRetryEntry> mDataRetryEntries;
    private Set<DataRetryManagerCallback> mDataRetryManagerCallbacks;
    private SparseArray<DataServiceManager> mDataServiceManagers;
    private List<DataSetupRetryRule> mDataSetupRetryRuleList;
    private final List<DataThrottlingEntry> mDataThrottlingEntries;
    private final LocalLog mLocalLog;
    private final String mLogTag;
    private final Phone mPhone;
    private final CommandsInterface mRil;

    private void logv(String str) {
    }

    /* loaded from: classes.dex */
    public static class DataThrottlingEntry {
        public final DataNetwork dataNetwork;
        public final DataProfile dataProfile;
        public final long expirationTimeMillis;
        public final DataNetworkController.NetworkRequestList networkRequestList;
        @ThrottleStatus.RetryType
        public final int retryType;
        public final int transport;

        public DataThrottlingEntry(DataProfile dataProfile, DataNetworkController.NetworkRequestList networkRequestList, DataNetwork dataNetwork, int i, @ThrottleStatus.RetryType int i2, long j) {
            this.dataProfile = dataProfile;
            this.networkRequestList = networkRequestList;
            this.dataNetwork = dataNetwork;
            this.transport = i;
            this.retryType = i2;
            this.expirationTimeMillis = j;
        }

        public String toString() {
            return "[DataThrottlingEntry: dataProfile=" + this.dataProfile + ", request list=" + this.networkRequestList + ", dataNetwork=" + this.dataNetwork + ", transport=" + AccessNetworkConstants.transportTypeToString(this.transport) + ", expiration time=" + DataUtils.elapsedTimeToString(this.expirationTimeMillis) + "]";
        }
    }

    /* loaded from: classes.dex */
    public static class DataRetryRule {
        protected Set<Integer> mFailCauses;
        protected int mMaxRetries;
        protected Set<Integer> mNetworkCapabilities = new ArraySet();
        protected List<Long> mRetryIntervalsMillis;

        /* JADX WARN: Removed duplicated region for block: B:28:0x0098  */
        /* JADX WARN: Removed duplicated region for block: B:33:0x00cb A[Catch: Exception -> 0x00f5, TRY_LEAVE, TryCatch #0 {Exception -> 0x00f5, blocks: (B:9:0x0061, B:31:0x009d, B:32:0x00a4, B:33:0x00cb, B:16:0x0075, B:19:0x007f, B:22:0x0089), top: B:51:0x0061 }] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public DataRetryRule(String str) {
            char c;
            this.mRetryIntervalsMillis = List.of(Long.valueOf(TimeUnit.SECONDS.toMillis(5L)));
            this.mMaxRetries = 10;
            this.mFailCauses = new ArraySet();
            if (TextUtils.isEmpty(str)) {
                throw new IllegalArgumentException("illegal rule " + str);
            }
            String lowerCase = str.trim().toLowerCase(Locale.ROOT);
            for (String str2 : lowerCase.split("\\s*,\\s*")) {
                String[] split = str2.trim().split("\\s*=\\s*");
                if (split.length != 2) {
                    throw new IllegalArgumentException("illegal rule " + lowerCase);
                }
                String trim = split[0].trim();
                String trim2 = split[1].trim();
                try {
                    int hashCode = trim.hashCode();
                    if (hashCode == -958847449) {
                        if (trim.equals("maximum_retries")) {
                            c = 2;
                            if (c != 0) {
                            }
                        }
                        c = 65535;
                        if (c != 0) {
                        }
                    } else if (hashCode != -705617557) {
                        if (hashCode == 384266364 && trim.equals("retry_interval")) {
                            c = 1;
                            if (c != 0) {
                                this.mFailCauses = (Set) Arrays.stream(trim2.split("\\s*\\|\\s*")).map(new DataNetworkController$HandoverRule$$ExternalSyntheticLambda0()).map(new DataRetryManager$DataRetryRule$$ExternalSyntheticLambda0()).collect(Collectors.toSet());
                            } else if (c == 1) {
                                this.mRetryIntervalsMillis = (List) Arrays.stream(trim2.split("\\s*\\|\\s*")).map(new DataNetworkController$HandoverRule$$ExternalSyntheticLambda0()).map(new Function() { // from class: com.android.internal.telephony.data.DataRetryManager$DataRetryRule$$ExternalSyntheticLambda1
                                    @Override // java.util.function.Function
                                    public final Object apply(Object obj) {
                                        return Long.valueOf((String) obj);
                                    }
                                }).collect(Collectors.toList());
                            } else if (c == 2) {
                                this.mMaxRetries = Integer.parseInt(trim2);
                            }
                        }
                        c = 65535;
                        if (c != 0) {
                        }
                    } else {
                        if (trim.equals("fail_causes")) {
                            c = 0;
                            if (c != 0) {
                            }
                        }
                        c = 65535;
                        if (c != 0) {
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException("illegal rule " + lowerCase + ", e=" + e);
                }
            }
            if (this.mMaxRetries < 0) {
                throw new IllegalArgumentException("Max retries should not be less than 0. mMaxRetries=" + this.mMaxRetries);
            } else if (this.mRetryIntervalsMillis.stream().anyMatch(new Predicate() { // from class: com.android.internal.telephony.data.DataRetryManager$DataRetryRule$$ExternalSyntheticLambda2
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$new$0;
                    lambda$new$0 = DataRetryManager.DataRetryRule.lambda$new$0((Long) obj);
                    return lambda$new$0;
                }
            })) {
                throw new IllegalArgumentException("Retry interval should not be less than 0. mRetryIntervalsMillis=" + this.mRetryIntervalsMillis);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$new$0(Long l) {
            return l.longValue() <= 0;
        }

        public List<Long> getRetryIntervalsMillis() {
            return this.mRetryIntervalsMillis;
        }

        public int getMaxRetries() {
            return this.mMaxRetries;
        }

        @VisibleForTesting
        public Set<Integer> getFailCauses() {
            return this.mFailCauses;
        }
    }

    /* loaded from: classes.dex */
    public static class DataSetupRetryRule extends DataRetryRule {
        private boolean mIsPermanentFailCauseRule;

        /* JADX WARN: Code restructure failed: missing block: B:20:0x005c, code lost:
            if (r4 == 1) goto L18;
         */
        /* JADX WARN: Code restructure failed: missing block: B:22:0x005f, code lost:
            r11.mNetworkCapabilities = com.android.internal.telephony.data.DataUtils.getNetworkCapabilitiesFromString(r6);
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public DataSetupRetryRule(String str) {
            super(str);
            String lowerCase = str.trim().toLowerCase(Locale.ROOT);
            String[] split = lowerCase.split("\\s*,\\s*");
            int length = split.length;
            int i = 0;
            while (true) {
                char c = 65535;
                if (i < length) {
                    String[] split2 = split[i].trim().split("\\s*=\\s*");
                    if (split2.length != 2) {
                        throw new IllegalArgumentException("illegal rule " + lowerCase);
                    }
                    String trim = split2[0].trim();
                    String trim2 = split2[1].trim();
                    try {
                        int hashCode = trim.hashCode();
                        if (hashCode != -1487597642) {
                            if (hashCode == -270123174 && trim.equals("permanent_fail_causes")) {
                                c = 0;
                            }
                        } else if (trim.equals("capabilities")) {
                            c = 1;
                        }
                        this.mFailCauses = (Set) Arrays.stream(trim2.split("\\s*\\|\\s*")).map(new DataNetworkController$HandoverRule$$ExternalSyntheticLambda0()).map(new DataRetryManager$DataRetryRule$$ExternalSyntheticLambda0()).collect(Collectors.toSet());
                        this.mIsPermanentFailCauseRule = true;
                        i++;
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new IllegalArgumentException("illegal rule " + lowerCase + ", e=" + e);
                    }
                } else if (this.mFailCauses.isEmpty()) {
                    if (this.mNetworkCapabilities.isEmpty() || this.mNetworkCapabilities.contains(-1)) {
                        throw new IllegalArgumentException("illegal rule " + lowerCase + ". Should have either valid network capabilities or fail causes.");
                    }
                    return;
                } else {
                    return;
                }
            }
        }

        @VisibleForTesting
        public Set<Integer> getNetworkCapabilities() {
            return this.mNetworkCapabilities;
        }

        public boolean isPermanentFailCauseRule() {
            return this.mIsPermanentFailCauseRule;
        }

        public boolean canBeMatched(int i, int i2) {
            if (this.mFailCauses.isEmpty() || this.mFailCauses.contains(Integer.valueOf(i2))) {
                return this.mNetworkCapabilities.isEmpty() || this.mNetworkCapabilities.contains(Integer.valueOf(i));
            }
            return false;
        }

        public String toString() {
            return "[DataSetupRetryRule: Network capabilities:" + DataUtils.networkCapabilitiesToString(this.mNetworkCapabilities.stream().mapToInt(new ToIntFunction() { // from class: com.android.internal.telephony.data.DataRetryManager$DataSetupRetryRule$$ExternalSyntheticLambda0
                @Override // java.util.function.ToIntFunction
                public final int applyAsInt(Object obj) {
                    return ((Integer) obj).intValue();
                }
            }).toArray()) + ", Fail causes=" + this.mFailCauses + ", Retry intervals=" + this.mRetryIntervalsMillis + ", Maximum retries=" + this.mMaxRetries + "]";
        }
    }

    /* loaded from: classes.dex */
    public static class DataHandoverRetryRule extends DataRetryRule {
        public DataHandoverRetryRule(String str) {
            super(str);
        }

        public String toString() {
            return "[DataHandoverRetryRule: Retry intervals=" + this.mRetryIntervalsMillis + ", Fail causes=" + this.mFailCauses + ", Maximum retries=" + this.mMaxRetries + "]";
        }
    }

    /* loaded from: classes.dex */
    public static class DataRetryEntry {
        public static final int RETRY_STATE_CANCELLED = 4;
        public static final int RETRY_STATE_FAILED = 2;
        public static final int RETRY_STATE_NOT_RETRIED = 1;
        public static final int RETRY_STATE_SUCCEEDED = 3;
        public final DataRetryRule appliedDataRetryRule;
        protected int mRetryState = 1;
        protected long mRetryStateTimestamp;
        public final long retryDelayMillis;
        public final long retryElapsedTime;

        public DataRetryEntry(DataRetryRule dataRetryRule, long j) {
            this.mRetryStateTimestamp = 0L;
            this.appliedDataRetryRule = dataRetryRule;
            this.retryDelayMillis = j;
            long elapsedRealtime = SystemClock.elapsedRealtime();
            this.mRetryStateTimestamp = elapsedRealtime;
            this.retryElapsedTime = elapsedRealtime + j;
        }

        public void setState(int i) {
            this.mRetryState = i;
            this.mRetryStateTimestamp = SystemClock.elapsedRealtime();
        }

        public int getState() {
            return this.mRetryState;
        }

        public static String retryStateToString(int i) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 4) {
                            return "Unknown(" + i + ")";
                        }
                        return "CANCELLED";
                    }
                    return "SUCCEEDED";
                }
                return "FAILED";
            }
            return "NOT_RETRIED";
        }

        /* loaded from: classes.dex */
        public static class Builder<T extends Builder<T>> {
            protected DataRetryRule mAppliedDataRetryRule;
            protected long mRetryDelayMillis = TimeUnit.SECONDS.toMillis(5);

            public T setRetryDelay(long j) {
                this.mRetryDelayMillis = j;
                return this;
            }

            public T setAppliedRetryRule(DataRetryRule dataRetryRule) {
                this.mAppliedDataRetryRule = dataRetryRule;
                return this;
            }
        }
    }

    /* loaded from: classes.dex */
    public static class DataSetupRetryEntry extends DataRetryEntry {
        public static final int RETRY_TYPE_DATA_PROFILE = 1;
        public static final int RETRY_TYPE_NETWORK_REQUESTS = 2;
        public static final int RETRY_TYPE_UNKNOWN = 0;
        public final DataProfile dataProfile;
        public final DataNetworkController.NetworkRequestList networkRequestList;
        public final int setupRetryType;
        public final int transport;

        private DataSetupRetryEntry(int i, DataNetworkController.NetworkRequestList networkRequestList, DataProfile dataProfile, int i2, DataSetupRetryRule dataSetupRetryRule, long j) {
            super(dataSetupRetryRule, j);
            this.setupRetryType = i;
            this.networkRequestList = networkRequestList;
            this.dataProfile = dataProfile;
            this.transport = i2;
        }

        private static String retryTypeToString(int i) {
            if (i != 1) {
                if (i != 2) {
                    return "Unknown(" + i + ")";
                }
                return "BY_NETWORK_REQUESTS";
            }
            return "BY_PROFILE";
        }

        public String toString() {
            return "[DataSetupRetryEntry: delay=" + this.retryDelayMillis + "ms, retry time:" + DataUtils.elapsedTimeToString(this.retryElapsedTime) + ", " + this.dataProfile + ", transport=" + AccessNetworkConstants.transportTypeToString(this.transport) + ", retry type=" + retryTypeToString(this.setupRetryType) + ", retry requests=" + this.networkRequestList + ", applied rule=" + this.appliedDataRetryRule + ", state=" + DataRetryEntry.retryStateToString(this.mRetryState) + ", timestamp=" + DataUtils.elapsedTimeToString(this.mRetryStateTimestamp) + "]";
        }

        /* loaded from: classes.dex */
        public static class Builder<T extends Builder<T>> extends DataRetryEntry.Builder<T> {
            private DataProfile mDataProfile;
            private DataNetworkController.NetworkRequestList mNetworkRequestList;
            private int mSetupRetryType = 0;
            private int mTransport = -1;

            public Builder<T> setSetupRetryType(int i) {
                this.mSetupRetryType = i;
                return this;
            }

            public Builder<T> setNetworkRequestList(DataNetworkController.NetworkRequestList networkRequestList) {
                this.mNetworkRequestList = networkRequestList;
                return this;
            }

            public Builder<T> setDataProfile(DataProfile dataProfile) {
                this.mDataProfile = dataProfile;
                return this;
            }

            public Builder<T> setTransport(int i) {
                this.mTransport = i;
                return this;
            }

            public DataSetupRetryEntry build() {
                DataNetworkController.NetworkRequestList networkRequestList = this.mNetworkRequestList;
                if (networkRequestList == null) {
                    throw new IllegalArgumentException("network request list is not specified.");
                }
                int i = this.mTransport;
                if (i != 1 && i != 2) {
                    throw new IllegalArgumentException("Invalid transport type " + this.mTransport);
                }
                int i2 = this.mSetupRetryType;
                if (i2 != 1 && i2 != 2) {
                    throw new IllegalArgumentException("Invalid setup retry type " + this.mSetupRetryType);
                }
                return new DataSetupRetryEntry(i2, networkRequestList, this.mDataProfile, i, (DataSetupRetryRule) this.mAppliedDataRetryRule, this.mRetryDelayMillis);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class DataHandoverRetryEntry extends DataRetryEntry {
        public final DataNetwork dataNetwork;

        public DataHandoverRetryEntry(DataNetwork dataNetwork, DataHandoverRetryRule dataHandoverRetryRule, long j) {
            super(dataHandoverRetryRule, j);
            this.dataNetwork = dataNetwork;
        }

        public String toString() {
            return "[DataHandoverRetryEntry: delay=" + this.retryDelayMillis + "ms, retry time:" + DataUtils.elapsedTimeToString(this.retryElapsedTime) + ", " + this.dataNetwork + ", applied rule=" + this.appliedDataRetryRule + ", state=" + DataRetryEntry.retryStateToString(this.mRetryState) + ", timestamp=" + DataUtils.elapsedTimeToString(this.mRetryStateTimestamp) + "]";
        }

        /* loaded from: classes.dex */
        public static class Builder<T extends Builder<T>> extends DataRetryEntry.Builder<T> {
            public DataNetwork mDataNetwork;

            public Builder<T> setDataNetwork(DataNetwork dataNetwork) {
                this.mDataNetwork = dataNetwork;
                return this;
            }

            public DataHandoverRetryEntry build() {
                return new DataHandoverRetryEntry(this.mDataNetwork, (DataHandoverRetryRule) this.mAppliedDataRetryRule, this.mRetryDelayMillis);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class DataRetryManagerCallback extends DataCallback {
        public void onDataNetworkHandoverRetry(DataHandoverRetryEntry dataHandoverRetryEntry) {
        }

        public void onDataNetworkHandoverRetryStopped(DataNetwork dataNetwork) {
        }

        public void onDataNetworkSetupRetry(DataSetupRetryEntry dataSetupRetryEntry) {
        }

        public void onThrottleStatusChanged(List<ThrottleStatus> list) {
        }

        public DataRetryManagerCallback(Executor executor) {
            super(executor);
        }
    }

    public DataRetryManager(Phone phone, DataNetworkController dataNetworkController, SparseArray<DataServiceManager> sparseArray, Looper looper, DataRetryManagerCallback dataRetryManagerCallback) {
        super(looper);
        this.mLocalLog = new LocalLog(128);
        this.mDataRetryManagerCallbacks = new ArraySet();
        this.mDataSetupRetryRuleList = new ArrayList();
        this.mDataHandoverRetryRuleList = new ArrayList();
        this.mDataRetryEntries = new ArrayList();
        this.mDataThrottlingEntries = new ArrayList();
        this.mPhone = phone;
        this.mRil = phone.mCi;
        this.mLogTag = "DRM-" + phone.getPhoneId();
        this.mDataRetryManagerCallbacks.add(dataRetryManagerCallback);
        this.mDataServiceManagers = sparseArray;
        DataConfigManager dataConfigManager = dataNetworkController.getDataConfigManager();
        this.mDataConfigManager = dataConfigManager;
        this.mDataProfileManager = dataNetworkController.getDataProfileManager();
        dataConfigManager.registerCallback(new DataConfigManager.DataConfigManagerCallback(new Executor() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda13
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                DataRetryManager.this.post(runnable);
            }
        }) { // from class: com.android.internal.telephony.data.DataRetryManager.1
            @Override // com.android.internal.telephony.data.DataConfigManager.DataConfigManagerCallback
            public void onCarrierConfigChanged() {
                DataRetryManager.this.onCarrierConfigUpdated();
            }
        });
        for (int i : phone.getAccessNetworksManager().getAvailableTransports()) {
            this.mDataServiceManagers.get(i).registerForApnUnthrottled(this, 6);
        }
        this.mDataProfileManager.registerCallback(new DataProfileManager.DataProfileManagerCallback(new Executor() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda13
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                DataRetryManager.this.post(runnable);
            }
        }) { // from class: com.android.internal.telephony.data.DataRetryManager.2
            @Override // com.android.internal.telephony.data.DataProfileManager.DataProfileManagerCallback
            public void onDataProfilesChanged() {
                DataRetryManager.this.onReset(1);
            }
        });
        dataNetworkController.registerDataNetworkControllerCallback(new DataNetworkController.DataNetworkControllerCallback(new Executor() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda13
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                DataRetryManager.this.post(runnable);
            }
        }) { // from class: com.android.internal.telephony.data.DataRetryManager.3
            @Override // com.android.internal.telephony.data.DataNetworkController.DataNetworkControllerCallback
            public void onDataServiceBound(int i2) {
                DataRetryManager.this.onReset(4);
            }

            @Override // com.android.internal.telephony.data.DataNetworkController.DataNetworkControllerCallback
            public void onDataNetworkConnected(int i2, DataProfile dataProfile) {
                DataRetryManager.this.onDataNetworkConnected(i2, dataProfile);
            }
        });
        this.mRil.registerForOn(this, 8, null);
        this.mRil.registerForModemReset(this, 9, null);
        if (this.mDataConfigManager.shouldResetDataThrottlingWhenTacChanges()) {
            this.mPhone.getServiceStateTracker().registerForAreaCodeChanged(this, 10, null);
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        DataProfile dataProfile;
        String str;
        switch (message.what) {
            case 3:
                final DataSetupRetryEntry dataSetupRetryEntry = (DataSetupRetryEntry) message.obj;
                if (isRetryCancelled(dataSetupRetryEntry)) {
                    return;
                }
                this.mDataRetryManagerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda14
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        DataRetryManager.lambda$handleMessage$1(DataRetryManager.DataSetupRetryEntry.this, (DataRetryManager.DataRetryManagerCallback) obj);
                    }
                });
                return;
            case 4:
                final DataHandoverRetryEntry dataHandoverRetryEntry = (DataHandoverRetryEntry) message.obj;
                if (isRetryCancelled(dataHandoverRetryEntry)) {
                    return;
                }
                this.mDataRetryManagerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda15
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        DataRetryManager.lambda$handleMessage$3(DataRetryManager.DataHandoverRetryEntry.this, (DataRetryManager.DataRetryManagerCallback) obj);
                    }
                });
                return;
            case 5:
            default:
                loge("Unexpected message " + message.what);
                return;
            case 6:
                AsyncResult asyncResult = (AsyncResult) message.obj;
                int intValue = ((Integer) asyncResult.userObj).intValue();
                Object obj = asyncResult.result;
                if (obj instanceof String) {
                    str = (String) obj;
                    dataProfile = null;
                } else if (obj instanceof DataProfile) {
                    dataProfile = (DataProfile) obj;
                    str = null;
                } else {
                    dataProfile = null;
                    str = null;
                }
                onDataProfileUnthrottled(dataProfile, str, intValue, true, true);
                return;
            case 7:
                onCancelPendingHandoverRetry((DataNetwork) message.obj);
                return;
            case 8:
                onReset(2);
                return;
            case 9:
                onReset(3);
                return;
            case 10:
                onReset(6);
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$handleMessage$1(final DataSetupRetryEntry dataSetupRetryEntry, final DataRetryManagerCallback dataRetryManagerCallback) {
        dataRetryManagerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda22
            @Override // java.lang.Runnable
            public final void run() {
                DataRetryManager.DataRetryManagerCallback.this.onDataNetworkSetupRetry(dataSetupRetryEntry);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$handleMessage$3(final DataHandoverRetryEntry dataHandoverRetryEntry, final DataRetryManagerCallback dataRetryManagerCallback) {
        dataRetryManagerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda28
            @Override // java.lang.Runnable
            public final void run() {
                DataRetryManager.DataRetryManagerCallback.this.onDataNetworkHandoverRetry(dataHandoverRetryEntry);
            }
        });
    }

    private boolean isRetryCancelled(DataRetryEntry dataRetryEntry) {
        if (dataRetryEntry == null || dataRetryEntry.getState() != 1) {
            log("Retry was removed earlier. " + dataRetryEntry);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCarrierConfigUpdated() {
        onReset(5);
        this.mDataSetupRetryRuleList = this.mDataConfigManager.getDataSetupRetryRules();
        this.mDataHandoverRetryRuleList = this.mDataConfigManager.getDataHandoverRetryRules();
        log("onDataConfigUpdated: mDataSetupRetryRuleList=" + this.mDataSetupRetryRuleList + ", mDataHandoverRetryRuleList=" + this.mDataHandoverRetryRuleList);
    }

    public void onDataNetworkConnected(int i, DataProfile dataProfile) {
        if (dataProfile.getApnSetting() != null) {
            dataProfile.getApnSetting().setPermanentFailed(false);
        }
        onDataProfileUnthrottled(dataProfile, null, i, true, false);
    }

    public void evaluateDataSetupRetry(final DataProfile dataProfile, final int i, final DataNetworkController.NetworkRequestList networkRequestList, final int i2, final long j) {
        post(new Runnable() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                DataRetryManager.this.lambda$evaluateDataSetupRetry$4(dataProfile, i, networkRequestList, i2, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onEvaluateDataSetupRetry */
    public void lambda$evaluateDataSetupRetry$4(DataProfile dataProfile, int i, DataNetworkController.NetworkRequestList networkRequestList, int i2, long j) {
        logl("onEvaluateDataSetupRetry: " + dataProfile + ", transport=" + AccessNetworkConstants.transportTypeToString(i) + ", cause=" + DataFailCause.toString(i2) + ", retryDelayMillis=" + j + "ms, " + networkRequestList);
        if (j == Long.MAX_VALUE || j == 2147483647L) {
            logl("Network suggested never retry for " + dataProfile);
            updateThrottleStatus(dataProfile, networkRequestList, null, 2, i, Long.MAX_VALUE);
        } else if (j != -1) {
            DataSetupRetryEntry build = ((DataSetupRetryEntry.Builder) new DataSetupRetryEntry.Builder().setRetryDelay(j)).setSetupRetryType(1).setNetworkRequestList(networkRequestList).setDataProfile(dataProfile).setTransport(i).build();
            updateThrottleStatus(dataProfile, networkRequestList, null, 2, i, build.retryElapsedTime);
            schedule(build);
        } else {
            logv("mDataSetupRetryRuleList=" + this.mDataSetupRetryRuleList);
            List<DataNetworkController.NetworkRequestList> groupedNetworkRequestList = DataUtils.getGroupedNetworkRequestList(networkRequestList);
            boolean z = false;
            for (DataSetupRetryRule dataSetupRetryRule : this.mDataSetupRetryRuleList) {
                if (dataSetupRetryRule.isPermanentFailCauseRule() && dataSetupRetryRule.getFailCauses().contains(Integer.valueOf(i2))) {
                    if (dataProfile.getApnSetting() != null) {
                        dataProfile.getApnSetting().setPermanentFailed(true);
                        log("Marked " + dataProfile.getApnSetting().getApnName() + " permanently failed, but still schedule retry to see if another data profile can be used for setup data.");
                        schedule(((DataSetupRetryEntry.Builder) ((DataSetupRetryEntry.Builder) new DataSetupRetryEntry.Builder().setRetryDelay(dataSetupRetryRule.getRetryIntervalsMillis().get(0).longValue())).setAppliedRetryRule(dataSetupRetryRule)).setSetupRetryType(2).setTransport(i).setNetworkRequestList(networkRequestList).build());
                        return;
                    }
                    log("Stopped timer-based retry for TD-based data profile. Will retry only when environment changes.");
                    return;
                }
                Iterator<DataNetworkController.NetworkRequestList> it = groupedNetworkRequestList.iterator();
                while (true) {
                    if (it.hasNext()) {
                        DataNetworkController.NetworkRequestList next = it.next();
                        int apnTypeNetworkCapability = next.get(0).getApnTypeNetworkCapability();
                        if (dataSetupRetryRule.canBeMatched(apnTypeNetworkCapability, i2)) {
                            if (isSimilarNetworkRequestRetryScheduled(next.get(0), i)) {
                                log(next.get(0) + " already had similar retry scheduled.");
                                return;
                            }
                            int retryFailedCount = getRetryFailedCount(apnTypeNetworkCapability, dataSetupRetryRule);
                            log("For capability " + DataUtils.networkCapabilityToString(apnTypeNetworkCapability) + ", found matching rule " + dataSetupRetryRule + ", failed count=" + retryFailedCount);
                            if (retryFailedCount == dataSetupRetryRule.getMaxRetries()) {
                                log("Data retry failed for " + retryFailedCount + " times. Stopped timer-based data retry for " + DataUtils.networkCapabilityToString(apnTypeNetworkCapability) + ". Condition-based retry will still happen when condition changes.");
                                return;
                            }
                            schedule(((DataSetupRetryEntry.Builder) ((DataSetupRetryEntry.Builder) new DataSetupRetryEntry.Builder().setRetryDelay(dataSetupRetryRule.getRetryIntervalsMillis().get(Math.min(retryFailedCount, dataSetupRetryRule.getRetryIntervalsMillis().size() - 1)).longValue())).setAppliedRetryRule(dataSetupRetryRule)).setSetupRetryType(2).setTransport(i).setNetworkRequestList(next).build());
                            z = true;
                        }
                    }
                }
            }
            if (z) {
                return;
            }
            log("onEvaluateDataSetupRetry: Did not match any retry rule. Stop timer-based retry.");
        }
    }

    public void evaluateDataHandoverRetry(final DataNetwork dataNetwork, final int i, final long j) {
        post(new Runnable() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DataRetryManager.this.lambda$evaluateDataHandoverRetry$5(dataNetwork, i, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onEvaluateDataHandoverRetry */
    public void lambda$evaluateDataHandoverRetry$5(final DataNetwork dataNetwork, int i, long j) {
        logl("onEvaluateDataHandoverRetry: " + dataNetwork + ", cause=" + DataFailCause.toString(i) + ", retryDelayMillis=" + j + "ms");
        int targetTransport = DataUtils.getTargetTransport(dataNetwork.getTransport());
        if (j == Long.MAX_VALUE || j == 2147483647L) {
            logl("Network suggested never retry handover for " + dataNetwork);
            updateThrottleStatus(dataNetwork.getDataProfile(), dataNetwork.getAttachedNetworkRequestList(), dataNetwork, 3, targetTransport, Long.MAX_VALUE);
        } else if (j != -1) {
            DataHandoverRetryEntry build = ((DataHandoverRetryEntry.Builder) new DataHandoverRetryEntry.Builder().setRetryDelay(j)).setDataNetwork(dataNetwork).build();
            updateThrottleStatus(dataNetwork.getDataProfile(), dataNetwork.getAttachedNetworkRequestList(), dataNetwork, 3, targetTransport, build.retryElapsedTime);
            schedule(build);
        } else {
            for (DataHandoverRetryRule dataHandoverRetryRule : this.mDataHandoverRetryRuleList) {
                if (dataHandoverRetryRule.getFailCauses().isEmpty() || dataHandoverRetryRule.getFailCauses().contains(Integer.valueOf(i))) {
                    int retryFailedCount = getRetryFailedCount(dataNetwork, dataHandoverRetryRule);
                    log("Found matching rule " + dataHandoverRetryRule + ", failed count=" + retryFailedCount);
                    if (retryFailedCount == dataHandoverRetryRule.getMaxRetries()) {
                        log("Data handover retry failed for " + retryFailedCount + " times. Stopped handover retry.");
                        this.mDataRetryManagerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda27
                            @Override // java.util.function.Consumer
                            public final void accept(Object obj) {
                                DataRetryManager.lambda$onEvaluateDataHandoverRetry$7(DataNetwork.this, (DataRetryManager.DataRetryManagerCallback) obj);
                            }
                        });
                        return;
                    }
                    schedule(((DataHandoverRetryEntry.Builder) ((DataHandoverRetryEntry.Builder) new DataHandoverRetryEntry.Builder().setRetryDelay(dataHandoverRetryRule.getRetryIntervalsMillis().get(Math.min(retryFailedCount, dataHandoverRetryRule.getRetryIntervalsMillis().size() - 1)).longValue())).setDataNetwork(dataNetwork).setAppliedRetryRule(dataHandoverRetryRule)).build());
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onEvaluateDataHandoverRetry$7(final DataNetwork dataNetwork, final DataRetryManagerCallback dataRetryManagerCallback) {
        dataRetryManagerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda29
            @Override // java.lang.Runnable
            public final void run() {
                DataRetryManager.DataRetryManagerCallback.this.onDataNetworkHandoverRetryStopped(dataNetwork);
            }
        });
    }

    public boolean isDataNetworkHandoverRetryStopped(DataNetwork dataNetwork) {
        for (DataHandoverRetryRule dataHandoverRetryRule : this.mDataHandoverRetryRuleList) {
            int retryFailedCount = getRetryFailedCount(dataNetwork, dataHandoverRetryRule);
            if (retryFailedCount == dataHandoverRetryRule.getMaxRetries()) {
                log("Data handover retry failed for " + retryFailedCount + " times. Stopped handover retry.");
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onReset(int i) {
        logl("Remove all retry and throttling entries, reason=" + resetReasonToString(i));
        removeMessages(3);
        removeMessages(4);
        this.mDataRetryEntries.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda20
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$onReset$8;
                lambda$onReset$8 = DataRetryManager.lambda$onReset$8((DataRetryManager.DataRetryEntry) obj);
                return lambda$onReset$8;
            }
        }).forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda21
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((DataRetryManager.DataRetryEntry) obj).setState(4);
            }
        });
        for (DataThrottlingEntry dataThrottlingEntry : this.mDataThrottlingEntries) {
            DataProfile dataProfile = dataThrottlingEntry.dataProfile;
            onDataProfileUnthrottled(dataProfile, dataProfile.getApnSetting() != null ? dataProfile.getApnSetting().getApnName() : null, dataThrottlingEntry.transport, false, true);
        }
        this.mDataThrottlingEntries.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onReset$8(DataRetryEntry dataRetryEntry) {
        return dataRetryEntry.getState() == 1;
    }

    private int getRetryFailedCount(DataNetwork dataNetwork, DataHandoverRetryRule dataHandoverRetryRule) {
        int i = 0;
        for (int size = this.mDataRetryEntries.size() - 1; size >= 0; size--) {
            if (this.mDataRetryEntries.get(size) instanceof DataHandoverRetryEntry) {
                DataHandoverRetryEntry dataHandoverRetryEntry = (DataHandoverRetryEntry) this.mDataRetryEntries.get(size);
                if (dataHandoverRetryEntry.dataNetwork == dataNetwork && dataHandoverRetryRule.equals(dataHandoverRetryEntry.appliedDataRetryRule)) {
                    if (dataHandoverRetryEntry.getState() == 3 || dataHandoverRetryEntry.getState() == 4) {
                        break;
                    }
                    i++;
                }
            }
        }
        return i;
    }

    private int getRetryFailedCount(int i, DataSetupRetryRule dataSetupRetryRule) {
        int i2 = 0;
        for (int size = this.mDataRetryEntries.size() - 1; size >= 0; size--) {
            if (this.mDataRetryEntries.get(size) instanceof DataSetupRetryEntry) {
                DataSetupRetryEntry dataSetupRetryEntry = (DataSetupRetryEntry) this.mDataRetryEntries.get(size);
                if (dataSetupRetryEntry.setupRetryType != 2) {
                    continue;
                } else if (dataSetupRetryEntry.networkRequestList.isEmpty()) {
                    logl("Invalid data retry entry detected");
                    loge("mDataRetryEntries=" + this.mDataRetryEntries);
                    AnomalyReporter.reportAnomaly(UUID.fromString("afeab78c-c0b0-49fc-a51f-f766814d7aa6"), "Invalid data retry entry detected", this.mPhone.getCarrierId());
                } else if (dataSetupRetryEntry.networkRequestList.get(0).getApnTypeNetworkCapability() == i && dataSetupRetryEntry.appliedDataRetryRule.equals(dataSetupRetryRule)) {
                    if (dataSetupRetryEntry.getState() == 3 || dataSetupRetryEntry.getState() == 4) {
                        break;
                    }
                    i2++;
                }
            }
        }
        return i2;
    }

    private void schedule(DataRetryEntry dataRetryEntry) {
        logl("Scheduled data retry: " + dataRetryEntry);
        this.mDataRetryEntries.add(dataRetryEntry);
        if (this.mDataRetryEntries.size() >= 100) {
            this.mDataRetryEntries.remove(0);
        }
        sendMessageDelayed(obtainMessage(dataRetryEntry instanceof DataSetupRetryEntry ? 3 : 4, dataRetryEntry), dataRetryEntry.retryDelayMillis);
    }

    private void updateThrottleStatus(final DataProfile dataProfile, DataNetworkController.NetworkRequestList networkRequestList, DataNetwork dataNetwork, @ThrottleStatus.RetryType int i, final int i2, final long j) {
        DataThrottlingEntry dataThrottlingEntry = new DataThrottlingEntry(dataProfile, networkRequestList, dataNetwork, i2, i, j);
        this.mDataThrottlingEntries.removeIf(new Predicate() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda23
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updateThrottleStatus$10;
                lambda$updateThrottleStatus$10 = DataRetryManager.lambda$updateThrottleStatus$10(dataProfile, (DataRetryManager.DataThrottlingEntry) obj);
                return lambda$updateThrottleStatus$10;
            }
        });
        if (this.mDataThrottlingEntries.size() >= 100) {
            AnomalyReporter.reportAnomaly(UUID.fromString("24fd4d46-1d0f-4b13-b7d6-7bad70b8289b"), "DataRetryManager throttling more than 100 data profiles", this.mPhone.getCarrierId());
            this.mDataThrottlingEntries.remove(0);
        }
        logl("Add throttling entry " + dataThrottlingEntry);
        this.mDataThrottlingEntries.add(dataThrottlingEntry);
        final int i3 = j == Long.MAX_VALUE ? 1 : i;
        final ArrayList arrayList = new ArrayList();
        if (dataProfile.getApnSetting() != null) {
            arrayList.addAll((Collection) dataProfile.getApnSetting().getApnTypes().stream().map(new Function() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda24
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    ThrottleStatus lambda$updateThrottleStatus$11;
                    lambda$updateThrottleStatus$11 = DataRetryManager.this.lambda$updateThrottleStatus$11(i3, j, i2, (Integer) obj);
                    return lambda$updateThrottleStatus$11;
                }
            }).collect(Collectors.toList()));
        }
        this.mDataRetryManagerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda25
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DataRetryManager.lambda$updateThrottleStatus$13(arrayList, (DataRetryManager.DataRetryManagerCallback) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateThrottleStatus$10(DataProfile dataProfile, DataThrottlingEntry dataThrottlingEntry) {
        return dataProfile.equals(dataThrottlingEntry.dataProfile);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ThrottleStatus lambda$updateThrottleStatus$11(int i, long j, int i2, Integer num) {
        return new ThrottleStatus.Builder().setApnType(num.intValue()).setRetryType(i).setSlotIndex(this.mPhone.getPhoneId()).setThrottleExpiryTimeMillis(j).setTransportType(i2).build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateThrottleStatus$13(final List list, final DataRetryManagerCallback dataRetryManagerCallback) {
        dataRetryManagerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda30
            @Override // java.lang.Runnable
            public final void run() {
                DataRetryManager.DataRetryManagerCallback.this.onThrottleStatusChanged(list);
            }
        });
    }

    private void onDataProfileUnthrottled(final DataProfile dataProfile, final String str, final int i, boolean z, boolean z2) {
        DataProfile dataProfile2;
        final int i2;
        log("onDataProfileUnthrottled: dataProfile=" + dataProfile + ", apn=" + str + ", transport=" + AccessNetworkConstants.transportTypeToString(i) + ", remove=" + z);
        final long elapsedRealtime = SystemClock.elapsedRealtime();
        List<DataThrottlingEntry> arrayList = new ArrayList();
        if (dataProfile != null) {
            Stream<DataThrottlingEntry> filter = this.mDataThrottlingEntries.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda6
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$onDataProfileUnthrottled$14;
                    lambda$onDataProfileUnthrottled$14 = DataRetryManager.lambda$onDataProfileUnthrottled$14(elapsedRealtime, (DataRetryManager.DataThrottlingEntry) obj);
                    return lambda$onDataProfileUnthrottled$14;
                }
            });
            if (dataProfile.getApnSetting() != null) {
                dataProfile.getApnSetting().setPermanentFailed(false);
                filter = filter.filter(new Predicate() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda7
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$onDataProfileUnthrottled$15;
                        lambda$onDataProfileUnthrottled$15 = DataRetryManager.lambda$onDataProfileUnthrottled$15((DataRetryManager.DataThrottlingEntry) obj);
                        return lambda$onDataProfileUnthrottled$15;
                    }
                }).filter(new Predicate() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda8
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$onDataProfileUnthrottled$16;
                        lambda$onDataProfileUnthrottled$16 = DataRetryManager.lambda$onDataProfileUnthrottled$16(dataProfile, (DataRetryManager.DataThrottlingEntry) obj);
                        return lambda$onDataProfileUnthrottled$16;
                    }
                });
            }
            arrayList = (List) filter.filter(new Predicate() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda9
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$onDataProfileUnthrottled$17;
                    lambda$onDataProfileUnthrottled$17 = DataRetryManager.lambda$onDataProfileUnthrottled$17(dataProfile, (DataRetryManager.DataThrottlingEntry) obj);
                    return lambda$onDataProfileUnthrottled$17;
                }
            }).collect(Collectors.toList());
        } else if (str != null) {
            arrayList = (List) this.mDataThrottlingEntries.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda10
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$onDataProfileUnthrottled$18;
                    lambda$onDataProfileUnthrottled$18 = DataRetryManager.lambda$onDataProfileUnthrottled$18(elapsedRealtime, str, i, (DataRetryManager.DataThrottlingEntry) obj);
                    return lambda$onDataProfileUnthrottled$18;
                }
            }).collect(Collectors.toList());
        }
        if (arrayList.isEmpty()) {
            log("onDataProfileUnthrottled: Nothing to unthrottle.");
            return;
        }
        final ArrayList arrayList2 = new ArrayList();
        if (((DataThrottlingEntry) arrayList.get(0)).retryType == 2) {
            dataProfile2 = ((DataThrottlingEntry) arrayList.get(0)).dataProfile;
            i2 = 2;
        } else if (((DataThrottlingEntry) arrayList.get(0)).retryType == 3) {
            dataProfile2 = ((DataThrottlingEntry) arrayList.get(0)).dataNetwork.getDataProfile();
            i2 = 3;
        } else {
            dataProfile2 = null;
            i2 = 1;
        }
        if (dataProfile2 != null && dataProfile2.getApnSetting() != null) {
            arrayList2.addAll((Collection) dataProfile2.getApnSetting().getApnTypes().stream().map(new Function() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda11
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    ThrottleStatus lambda$onDataProfileUnthrottled$19;
                    lambda$onDataProfileUnthrottled$19 = DataRetryManager.this.lambda$onDataProfileUnthrottled$19(i2, i, (Integer) obj);
                    return lambda$onDataProfileUnthrottled$19;
                }
            }).collect(Collectors.toList()));
        }
        this.mDataRetryManagerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda12
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DataRetryManager.lambda$onDataProfileUnthrottled$21(arrayList2, (DataRetryManager.DataRetryManagerCallback) obj);
            }
        });
        if (dataProfile2 != null) {
            cancelRetriesForDataProfile(dataProfile2, i);
        }
        logl("onDataProfileUnthrottled: Removing the following throttling entries. " + arrayList);
        if (z2) {
            for (DataThrottlingEntry dataThrottlingEntry : arrayList) {
                int i3 = dataThrottlingEntry.retryType;
                if (i3 == 2) {
                    schedule(((DataSetupRetryEntry.Builder) new DataSetupRetryEntry.Builder().setDataProfile(dataThrottlingEntry.dataProfile).setTransport(dataThrottlingEntry.transport).setSetupRetryType(1).setNetworkRequestList(dataThrottlingEntry.networkRequestList).setRetryDelay(0L)).build());
                } else if (i3 == 3) {
                    schedule(((DataHandoverRetryEntry.Builder) new DataHandoverRetryEntry.Builder().setDataNetwork(dataThrottlingEntry.dataNetwork).setRetryDelay(0L)).build());
                }
            }
        }
        if (z) {
            this.mDataThrottlingEntries.removeAll(arrayList);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onDataProfileUnthrottled$14(long j, DataThrottlingEntry dataThrottlingEntry) {
        return dataThrottlingEntry.expirationTimeMillis > j;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onDataProfileUnthrottled$15(DataThrottlingEntry dataThrottlingEntry) {
        return dataThrottlingEntry.dataProfile.getApnSetting() != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onDataProfileUnthrottled$16(DataProfile dataProfile, DataThrottlingEntry dataThrottlingEntry) {
        return dataThrottlingEntry.dataProfile.getApnSetting().getApnName().equals(dataProfile.getApnSetting().getApnName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onDataProfileUnthrottled$17(DataProfile dataProfile, DataThrottlingEntry dataThrottlingEntry) {
        return Objects.equals(dataThrottlingEntry.dataProfile.getTrafficDescriptor(), dataProfile.getTrafficDescriptor());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onDataProfileUnthrottled$18(long j, String str, int i, DataThrottlingEntry dataThrottlingEntry) {
        return dataThrottlingEntry.expirationTimeMillis > j && dataThrottlingEntry.dataProfile.getApnSetting() != null && str.equals(dataThrottlingEntry.dataProfile.getApnSetting().getApnName()) && dataThrottlingEntry.transport == i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ThrottleStatus lambda$onDataProfileUnthrottled$19(int i, int i2, Integer num) {
        return new ThrottleStatus.Builder().setApnType(num.intValue()).setSlotIndex(this.mPhone.getPhoneId()).setNoThrottle().setRetryType(i).setTransportType(i2).build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onDataProfileUnthrottled$21(final List list, final DataRetryManagerCallback dataRetryManagerCallback) {
        dataRetryManagerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda26
            @Override // java.lang.Runnable
            public final void run() {
                DataRetryManager.DataRetryManagerCallback.this.onThrottleStatusChanged(list);
            }
        });
    }

    private void cancelRetriesForDataProfile(final DataProfile dataProfile, final int i) {
        logl("cancelRetriesForDataProfile: Canceling pending retries for " + dataProfile);
        this.mDataRetryEntries.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda16
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$cancelRetriesForDataProfile$22;
                lambda$cancelRetriesForDataProfile$22 = DataRetryManager.lambda$cancelRetriesForDataProfile$22(dataProfile, i, (DataRetryManager.DataRetryEntry) obj);
                return lambda$cancelRetriesForDataProfile$22;
            }
        }).forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda17
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((DataRetryManager.DataRetryEntry) obj).setState(4);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$cancelRetriesForDataProfile$22(DataProfile dataProfile, int i, DataRetryEntry dataRetryEntry) {
        if (dataRetryEntry.getState() == 1) {
            if (dataRetryEntry instanceof DataSetupRetryEntry) {
                DataSetupRetryEntry dataSetupRetryEntry = (DataSetupRetryEntry) dataRetryEntry;
                return dataProfile.equals(dataSetupRetryEntry.dataProfile) && i == dataSetupRetryEntry.transport;
            } else if (dataRetryEntry instanceof DataHandoverRetryEntry) {
                return dataProfile.equals(((DataHandoverRetryEntry) dataRetryEntry).dataNetwork.getDataProfile());
            }
        }
        return false;
    }

    public boolean isSimilarNetworkRequestRetryScheduled(TelephonyNetworkRequest telephonyNetworkRequest, int i) {
        for (int size = this.mDataRetryEntries.size() - 1; size >= 0; size--) {
            if (this.mDataRetryEntries.get(size) instanceof DataSetupRetryEntry) {
                DataSetupRetryEntry dataSetupRetryEntry = (DataSetupRetryEntry) this.mDataRetryEntries.get(size);
                if (dataSetupRetryEntry.getState() == 1 && dataSetupRetryEntry.setupRetryType == 2) {
                    if (dataSetupRetryEntry.networkRequestList.isEmpty()) {
                        logl("Invalid data retry entry detected");
                        loge("mDataRetryEntries=" + this.mDataRetryEntries);
                        AnomalyReporter.reportAnomaly(UUID.fromString("781af571-f55d-476d-b510-7a5381f633dc"), "Invalid data retry entry detected", this.mPhone.getCarrierId());
                    } else if (dataSetupRetryEntry.networkRequestList.get(0).getApnTypeNetworkCapability() == telephonyNetworkRequest.getApnTypeNetworkCapability() && dataSetupRetryEntry.transport == i) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isDataProfileThrottled(final DataProfile dataProfile, final int i) {
        final long elapsedRealtime = SystemClock.elapsedRealtime();
        return this.mDataThrottlingEntries.stream().anyMatch(new Predicate() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isDataProfileThrottled$24;
                lambda$isDataProfileThrottled$24 = DataRetryManager.lambda$isDataProfileThrottled$24(dataProfile, elapsedRealtime, i, (DataRetryManager.DataThrottlingEntry) obj);
                return lambda$isDataProfileThrottled$24;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$isDataProfileThrottled$24(DataProfile dataProfile, long j, int i, DataThrottlingEntry dataThrottlingEntry) {
        return dataThrottlingEntry.dataProfile.equals(dataProfile) && dataThrottlingEntry.expirationTimeMillis > j && dataThrottlingEntry.transport == i;
    }

    public void cancelPendingHandoverRetry(DataNetwork dataNetwork) {
        sendMessage(obtainMessage(7, dataNetwork));
    }

    private void onCancelPendingHandoverRetry(final DataNetwork dataNetwork) {
        this.mDataRetryEntries.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda18
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$onCancelPendingHandoverRetry$25;
                lambda$onCancelPendingHandoverRetry$25 = DataRetryManager.lambda$onCancelPendingHandoverRetry$25(DataNetwork.this, (DataRetryManager.DataRetryEntry) obj);
                return lambda$onCancelPendingHandoverRetry$25;
            }
        }).forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda19
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((DataRetryManager.DataRetryEntry) obj).setState(4);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onCancelPendingHandoverRetry$25(DataNetwork dataNetwork, DataRetryEntry dataRetryEntry) {
        return (dataRetryEntry instanceof DataHandoverRetryEntry) && ((DataHandoverRetryEntry) dataRetryEntry).dataNetwork == dataNetwork && dataRetryEntry.getState() == 1;
    }

    public boolean isAnyHandoverRetryScheduled(final DataNetwork dataNetwork) {
        return this.mDataRetryEntries.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return r1.isInstance((DataRetryManager.DataRetryEntry) obj);
            }
        }).map(new Function() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return (DataRetryManager.DataHandoverRetryEntry) r1.cast((DataRetryManager.DataRetryEntry) obj);
            }
        }).anyMatch(new Predicate() { // from class: com.android.internal.telephony.data.DataRetryManager$$ExternalSyntheticLambda5
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isAnyHandoverRetryScheduled$27;
                lambda$isAnyHandoverRetryScheduled$27 = DataRetryManager.lambda$isAnyHandoverRetryScheduled$27(DataNetwork.this, (DataRetryManager.DataHandoverRetryEntry) obj);
                return lambda$isAnyHandoverRetryScheduled$27;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$isAnyHandoverRetryScheduled$27(DataNetwork dataNetwork, DataHandoverRetryEntry dataHandoverRetryEntry) {
        return dataHandoverRetryEntry.getState() == 1 && dataHandoverRetryEntry.dataNetwork == dataNetwork;
    }

    public void registerCallback(DataRetryManagerCallback dataRetryManagerCallback) {
        this.mDataRetryManagerCallbacks.add(dataRetryManagerCallback);
    }

    public void unregisterCallback(DataRetryManagerCallback dataRetryManagerCallback) {
        this.mDataRetryManagerCallbacks.remove(dataRetryManagerCallback);
    }

    private static String resetReasonToString(int i) {
        switch (i) {
            case 1:
                return "DATA_PROFILES_CHANGED";
            case 2:
                return "RADIO_ON";
            case 3:
                return "MODEM_RESTART";
            case 4:
                return "DATA_SERVICE_BOUND";
            case 5:
                return "DATA_CONFIG_CHANGED";
            case 6:
                return "TAC_CHANGED";
            default:
                return "UNKNOWN(" + i + ")";
        }
    }

    private void log(String str) {
        Rlog.d(this.mLogTag, str);
    }

    private void loge(String str) {
        Rlog.e(this.mLogTag, str);
    }

    private void logl(String str) {
        log(str);
        this.mLocalLog.log(str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter = new AndroidUtilIndentingPrintWriter(printWriter, "  ");
        androidUtilIndentingPrintWriter.println(DataRetryManager.class.getSimpleName() + "-" + this.mPhone.getPhoneId() + ":");
        androidUtilIndentingPrintWriter.increaseIndent();
        androidUtilIndentingPrintWriter.println("Data Setup Retry rules:");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mDataSetupRetryRuleList.forEach(new DataConfigManager$$ExternalSyntheticLambda5(androidUtilIndentingPrintWriter));
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println("Data Handover Retry rules:");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mDataHandoverRetryRuleList.forEach(new DataConfigManager$$ExternalSyntheticLambda6(androidUtilIndentingPrintWriter));
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println("Retry entries:");
        androidUtilIndentingPrintWriter.increaseIndent();
        for (DataRetryEntry dataRetryEntry : this.mDataRetryEntries) {
            androidUtilIndentingPrintWriter.println(dataRetryEntry);
        }
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println("Throttling entries:");
        androidUtilIndentingPrintWriter.increaseIndent();
        for (DataThrottlingEntry dataThrottlingEntry : this.mDataThrottlingEntries) {
            androidUtilIndentingPrintWriter.println(dataThrottlingEntry);
        }
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println("Local logs:");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mLocalLog.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.decreaseIndent();
    }
}
