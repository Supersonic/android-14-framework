package com.android.internal.telephony.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.TelephonyNetworkSpecifier;
import android.os.Handler;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.CellIdentity;
import android.telephony.CellIdentityLte;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.SubscriptionController;
import com.android.internal.telephony.data.CellularNetworkValidator;
import com.android.internal.telephony.metrics.TelephonyMetrics;
import com.android.internal.telephony.subscription.SubscriptionInfoInternal;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class CellularNetworkValidator {
    @VisibleForTesting
    public static final long MAX_VALIDATION_CACHE_TTL = TimeUnit.DAYS.toMillis(1);
    private static CellularNetworkValidator sInstance = null;
    private static boolean sWaitForNetworkAvailableWhenCacheHit = true;
    private ConnectivityManager mConnectivityManager;
    private Context mContext;
    @VisibleForTesting
    public ConnectivityNetworkCallback mNetworkCallback;
    private NetworkRequest mNetworkRequest;
    private boolean mReleaseAfterValidation;
    private int mSubId;
    private long mTimeoutInMs;
    private ValidationCallback mValidationCallback;
    private int mState = 0;
    @VisibleForTesting
    public Handler mHandler = new Handler();
    private final ValidatedNetworkCache mValidatedNetworkCache = new ValidatedNetworkCache();

    /* loaded from: classes.dex */
    public interface ValidationCallback {
        void onNetworkAvailable(Network network, int i);

        void onValidationDone(boolean z, int i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ValidatedNetworkCache {
        private final Map<String, ValidatedNetwork> mValidatedNetworkMap;
        private final PriorityQueue<ValidatedNetwork> mValidatedNetworkPQ;

        private ValidatedNetworkCache() {
            this.mValidatedNetworkPQ = new PriorityQueue<>(new Comparator() { // from class: com.android.internal.telephony.data.CellularNetworkValidator$ValidatedNetworkCache$$ExternalSyntheticLambda0
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    int lambda$new$0;
                    lambda$new$0 = CellularNetworkValidator.ValidatedNetworkCache.lambda$new$0((CellularNetworkValidator.ValidatedNetworkCache.ValidatedNetwork) obj, (CellularNetworkValidator.ValidatedNetworkCache.ValidatedNetwork) obj2);
                    return lambda$new$0;
                }
            });
            this.mValidatedNetworkMap = new HashMap();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ int lambda$new$0(ValidatedNetwork validatedNetwork, ValidatedNetwork validatedNetwork2) {
            long j = validatedNetwork.mValidationTimeStamp;
            long j2 = validatedNetwork2.mValidationTimeStamp;
            if (j < j2) {
                return -1;
            }
            return j > j2 ? 1 : 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public final class ValidatedNetwork {
            final String mValidationIdentity;
            long mValidationTimeStamp;

            ValidatedNetwork(String str, long j) {
                this.mValidationIdentity = str;
                this.mValidationTimeStamp = j;
            }

            void update(long j) {
                this.mValidationTimeStamp = j;
            }
        }

        synchronized boolean isRecentlyValidated(int i) {
            long validationCacheTtl = getValidationCacheTtl(i);
            String validationNetworkIdentity = getValidationNetworkIdentity(i);
            if (validationNetworkIdentity != null && this.mValidatedNetworkMap.containsKey(validationNetworkIdentity)) {
                boolean z = System.currentTimeMillis() - this.mValidatedNetworkMap.get(validationNetworkIdentity).mValidationTimeStamp < validationCacheTtl;
                CellularNetworkValidator.logd("isRecentlyValidated on subId " + i + " ? " + z);
                return z;
            }
            return false;
        }

        synchronized void storeLastValidationResult(int i, boolean z) {
            String validationNetworkIdentity = getValidationNetworkIdentity(i);
            StringBuilder sb = new StringBuilder();
            sb.append("storeLastValidationResult for subId ");
            sb.append(i);
            sb.append(z ? " validated." : " not validated.");
            CellularNetworkValidator.logd(sb.toString());
            if (validationNetworkIdentity == null) {
                return;
            }
            if (!z) {
                this.mValidatedNetworkPQ.remove(this.mValidatedNetworkMap.get(validationNetworkIdentity));
                this.mValidatedNetworkMap.remove(validationNetworkIdentity);
                return;
            }
            long currentTimeMillis = System.currentTimeMillis();
            ValidatedNetwork validatedNetwork = this.mValidatedNetworkMap.get(validationNetworkIdentity);
            if (validatedNetwork != null) {
                validatedNetwork.update(currentTimeMillis);
                this.mValidatedNetworkPQ.remove(validatedNetwork);
                this.mValidatedNetworkPQ.add(validatedNetwork);
            } else {
                ValidatedNetwork validatedNetwork2 = new ValidatedNetwork(validationNetworkIdentity, currentTimeMillis);
                this.mValidatedNetworkMap.put(validationNetworkIdentity, validatedNetwork2);
                this.mValidatedNetworkPQ.add(validatedNetwork2);
            }
            if (this.mValidatedNetworkPQ.size() > 10) {
                this.mValidatedNetworkMap.remove(this.mValidatedNetworkPQ.poll().mValidationIdentity);
            }
        }

        private String getValidationNetworkIdentity(int i) {
            Phone phone;
            NetworkRegistrationInfo networkRegistrationInfo;
            if (SubscriptionManager.isUsableSubscriptionId(i)) {
                if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                    if (SubscriptionManagerService.getInstance() == null) {
                        return null;
                    }
                    phone = PhoneFactory.getPhone(SubscriptionManagerService.getInstance().getPhoneId(i));
                } else {
                    SubscriptionController subscriptionController = SubscriptionController.getInstance();
                    if (subscriptionController == null) {
                        return null;
                    }
                    phone = PhoneFactory.getPhone(subscriptionController.getPhoneId(i));
                }
                if (phone != null && phone.getServiceState() != null && (networkRegistrationInfo = phone.getServiceState().getNetworkRegistrationInfo(2, 1)) != null && networkRegistrationInfo.getCellIdentity() != null) {
                    CellIdentity cellIdentity = networkRegistrationInfo.getCellIdentity();
                    if (cellIdentity.getType() == 3 && cellIdentity.getMccString() != null && cellIdentity.getMncString() != null) {
                        CellIdentityLte cellIdentityLte = (CellIdentityLte) cellIdentity;
                        if (cellIdentityLte.getTac() != Integer.MAX_VALUE) {
                            return cellIdentity.getMccString() + cellIdentity.getMncString() + "_" + cellIdentityLte.getTac() + "_" + i;
                        }
                    }
                }
                return null;
            }
            return null;
        }

        private long getValidationCacheTtl(int i) {
            PersistableBundle configForSubId;
            CarrierConfigManager carrierConfigManager = (CarrierConfigManager) CellularNetworkValidator.this.mContext.getSystemService("carrier_config");
            return Math.min((carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(i)) == null) ? 0L : configForSubId.getLong("data_switch_validation_min_gap_long"), CellularNetworkValidator.MAX_VALIDATION_CACHE_TTL);
        }
    }

    public static CellularNetworkValidator make(Context context) {
        if (sInstance != null) {
            logd("createCellularNetworkValidator failed. Instance already exists.");
        } else {
            sInstance = new CellularNetworkValidator(context);
        }
        return sInstance;
    }

    public static CellularNetworkValidator getInstance() {
        return sInstance;
    }

    public boolean isValidationFeatureSupported() {
        return PhoneConfigurationManager.getInstance().getCurrentPhoneCapability().isNetworkValidationBeforeSwitchSupported();
    }

    @VisibleForTesting
    public CellularNetworkValidator(Context context) {
        this.mContext = context;
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
    }

    public synchronized void validate(final int i, long j, boolean z, ValidationCallback validationCallback) {
        if (i == this.mSubId) {
            return;
        }
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            SubscriptionInfoInternal subscriptionInfoInternal = SubscriptionManagerService.getInstance().getSubscriptionInfoInternal(i);
            if (subscriptionInfoInternal == null || !subscriptionInfoInternal.isActive()) {
                logd("Failed to start validation. Inactive subId " + i);
                validationCallback.onValidationDone(false, i);
                return;
            }
        } else if (!SubscriptionController.getInstance().isActiveSubId(i)) {
            logd("Failed to start validation. Inactive subId " + i);
            validationCallback.onValidationDone(false, i);
            return;
        }
        if (isValidating()) {
            lambda$reportValidationResult$1();
        }
        if (!sWaitForNetworkAvailableWhenCacheHit && this.mValidatedNetworkCache.isRecentlyValidated(i)) {
            validationCallback.onValidationDone(true, i);
            return;
        }
        this.mState = 1;
        this.mSubId = i;
        this.mTimeoutInMs = j;
        this.mValidationCallback = validationCallback;
        this.mReleaseAfterValidation = z;
        this.mNetworkRequest = createNetworkRequest();
        logd("Start validating subId " + this.mSubId + " mTimeoutInMs " + this.mTimeoutInMs + " mReleaseAfterValidation " + this.mReleaseAfterValidation);
        ConnectivityNetworkCallback connectivityNetworkCallback = new ConnectivityNetworkCallback(i);
        this.mNetworkCallback = connectivityNetworkCallback;
        this.mConnectivityManager.requestNetwork(this.mNetworkRequest, connectivityNetworkCallback, this.mHandler);
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.internal.telephony.data.CellularNetworkValidator$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                CellularNetworkValidator.this.lambda$validate$0(i);
            }
        }, this.mTimeoutInMs);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onValidationTimeout */
    public synchronized void lambda$validate$0(int i) {
        logd("timeout on subId " + i + " validation.");
        this.mValidatedNetworkCache.storeLastValidationResult(i, false);
        reportValidationResult(false, i);
    }

    /* renamed from: stopValidation */
    public synchronized void lambda$reportValidationResult$1() {
        if (!isValidating()) {
            logd("No need to stop validation.");
            return;
        }
        ConnectivityNetworkCallback connectivityNetworkCallback = this.mNetworkCallback;
        if (connectivityNetworkCallback != null) {
            this.mConnectivityManager.unregisterNetworkCallback(connectivityNetworkCallback);
        }
        this.mState = 0;
        this.mHandler.removeCallbacksAndMessages(null);
        this.mSubId = -1;
    }

    public synchronized int getSubIdInValidation() {
        return this.mSubId;
    }

    public synchronized boolean isValidating() {
        return this.mState != 0;
    }

    private NetworkRequest createNetworkRequest() {
        return new NetworkRequest.Builder().addCapability(12).addTransportType(0).setNetworkSpecifier(new TelephonyNetworkSpecifier.Builder().setSubscriptionId(this.mSubId).build()).build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void reportValidationResult(boolean z, int i) {
        if (this.mSubId != i) {
            return;
        }
        this.mHandler.removeCallbacksAndMessages(null);
        if (this.mState == 1) {
            this.mValidationCallback.onValidationDone(z, this.mSubId);
            this.mState = 2;
            if (!this.mReleaseAfterValidation && z) {
                this.mHandler.postDelayed(new Runnable() { // from class: com.android.internal.telephony.data.CellularNetworkValidator$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        CellularNetworkValidator.this.lambda$reportValidationResult$1();
                    }
                }, 500L);
            } else {
                lambda$reportValidationResult$1();
            }
            TelephonyMetrics.getInstance().writeNetworkValidate(z ? 3 : 2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void reportNetworkAvailable(Network network, int i) {
        if (this.mSubId != i) {
            return;
        }
        this.mValidationCallback.onNetworkAvailable(network, i);
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public class ConnectivityNetworkCallback extends ConnectivityManager.NetworkCallback {
        private final int mSubId;

        ConnectivityNetworkCallback(int i) {
            this.mSubId = i;
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onAvailable(Network network) {
            CellularNetworkValidator.logd("network onAvailable " + network);
            TelephonyMetrics.getInstance().writeNetworkValidate(1);
            if (CellularNetworkValidator.this.mValidatedNetworkCache.isRecentlyValidated(this.mSubId)) {
                CellularNetworkValidator.this.reportValidationResult(true, this.mSubId);
            } else {
                CellularNetworkValidator.this.reportNetworkAvailable(network, this.mSubId);
            }
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onLosing(Network network, int i) {
            CellularNetworkValidator.logd("network onLosing " + network + " maxMsToLive " + i);
            CellularNetworkValidator.this.mValidatedNetworkCache.storeLastValidationResult(this.mSubId, false);
            CellularNetworkValidator.this.reportValidationResult(false, this.mSubId);
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onLost(Network network) {
            CellularNetworkValidator.logd("network onLost " + network);
            CellularNetworkValidator.this.mValidatedNetworkCache.storeLastValidationResult(this.mSubId, false);
            CellularNetworkValidator.this.reportValidationResult(false, this.mSubId);
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onUnavailable() {
            CellularNetworkValidator.logd("onUnavailable");
            CellularNetworkValidator.this.mValidatedNetworkCache.storeLastValidationResult(this.mSubId, false);
            CellularNetworkValidator.this.reportValidationResult(false, this.mSubId);
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            if (networkCapabilities.hasCapability(16)) {
                CellularNetworkValidator.logd("onValidated");
                CellularNetworkValidator.this.mValidatedNetworkCache.storeLastValidationResult(this.mSubId, true);
                CellularNetworkValidator.this.reportValidationResult(true, this.mSubId);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void logd(String str) {
        Log.d("NetworkValidator", str);
    }
}
