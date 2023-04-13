package com.android.internal.telephony;

import android.os.AsyncResult;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.telephony.CarrierConfigManager;
import android.telephony.CellIdentity;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityNr;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SignalStrengthUpdateRequest;
import android.telephony.SignalThresholdInfo;
import android.telephony.SubscriptionInfo;
import android.telephony.TelephonyManager;
import android.util.Pair;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.SignalStrengthController;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import com.android.internal.telephony.util.ArrayUtils;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.PatternSyntaxException;
/* loaded from: classes.dex */
public class SignalStrengthController extends Handler {
    private static final long POLL_PERIOD_MILLIS;
    private static final long SIGNAL_STRENGTH_REFRESH_THRESHOLD_IN_MS;
    private PersistableBundle mCarrierConfig;
    private final CommandsInterface mCi;
    private final Phone mPhone;
    private SignalStrength mSignalStrength;
    private long mSignalStrengthUpdatedTime;
    private SignalStrength mLastSignalStrength = null;
    private ArrayList<Pair<Integer, Integer>> mEarfcnPairListForRsrpBoost = null;
    private int mLteRsrpBoost = 0;
    private ArrayList<Pair<Integer, Integer>> mNrarfcnRangeListForRsrpBoost = null;
    private int[] mNrRsrpBoost = null;
    private final Object mRsrpBoostLock = new Object();
    private final List<SignalRequestRecord> mSignalRequestRecords = new ArrayList();
    private final LocalLog mLocalLog = new LocalLog(64);

    private static void log(String str) {
    }

    static {
        TimeUnit timeUnit = TimeUnit.SECONDS;
        SIGNAL_STRENGTH_REFRESH_THRESHOLD_IN_MS = timeUnit.toMillis(10L);
        POLL_PERIOD_MILLIS = timeUnit.toMillis(20L);
    }

    public SignalStrengthController(Phone phone) {
        this.mPhone = phone;
        CommandsInterface commandsInterface = phone.mCi;
        this.mCi = commandsInterface;
        commandsInterface.registerForRilConnected(this, 4, null);
        commandsInterface.registerForAvailable(this, 5, null);
        commandsInterface.setOnSignalStrengthUpdate(this, 8, null);
        setSignalStrengthDefaultValues();
        this.mCarrierConfig = getCarrierConfig();
        ((CarrierConfigManager) phone.getContext().getSystemService(CarrierConfigManager.class)).registerCarrierConfigChangeListener(new Executor() { // from class: com.android.internal.telephony.SignalStrengthController$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                SignalStrengthController.this.post(runnable);
            }
        }, new CarrierConfigManager.CarrierConfigChangeListener() { // from class: com.android.internal.telephony.SignalStrengthController$$ExternalSyntheticLambda1
            public final void onCarrierConfigChanged(int i, int i2, int i3, int i4) {
                SignalStrengthController.this.lambda$new$0(i, i2, i3, i4);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, int i2, int i3, int i4) {
        onCarrierConfigurationChanged(i);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        switch (message.what) {
            case 1:
                Pair pair = (Pair) message.obj;
                final SignalRequestRecord signalRequestRecord = (SignalRequestRecord) pair.first;
                Message message2 = (Message) pair.second;
                AsyncResult forMessage = AsyncResult.forMessage(message2);
                if (this.mSignalRequestRecords.stream().anyMatch(new Predicate() { // from class: com.android.internal.telephony.SignalStrengthController$$ExternalSyntheticLambda3
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$handleMessage$1;
                        lambda$handleMessage$1 = SignalStrengthController.lambda$handleMessage$1(SignalStrengthController.SignalRequestRecord.this, (SignalStrengthController.SignalRequestRecord) obj);
                        return lambda$handleMessage$1;
                    }
                })) {
                    forMessage.exception = new IllegalStateException("setSignalStrengthUpdateRequest called again with same subId");
                    message2.sendToTarget();
                    return;
                }
                try {
                    signalRequestRecord.mRequest.getLiveToken().linkToDeath(signalRequestRecord, 0);
                    this.mSignalRequestRecords.add(signalRequestRecord);
                    updateAlwaysReportSignalStrength();
                    updateReportingCriteria();
                    message2.sendToTarget();
                    return;
                } catch (RemoteException | NullPointerException unused) {
                    forMessage.exception = new IllegalStateException("Signal request client is already dead.");
                    message2.sendToTarget();
                    return;
                }
            case 2:
                Pair pair2 = (Pair) message.obj;
                SignalRequestRecord signalRequestRecord2 = (SignalRequestRecord) pair2.first;
                Message message3 = (Message) pair2.second;
                Iterator<SignalRequestRecord> it = this.mSignalRequestRecords.iterator();
                while (it.hasNext()) {
                    SignalRequestRecord next = it.next();
                    if (next.mRequest.getLiveToken().equals(signalRequestRecord2.mRequest.getLiveToken())) {
                        try {
                            next.mRequest.getLiveToken().unlinkToDeath(next, 0);
                        } catch (NoSuchElementException unused2) {
                        }
                        it.remove();
                    }
                }
                updateAlwaysReportSignalStrength();
                updateReportingCriteria();
                if (message3 != null) {
                    AsyncResult.forMessage(message3);
                    message3.sendToTarget();
                    return;
                }
                return;
            case 3:
                updateReportingCriteria();
                return;
            case 4:
            case 5:
                onReset();
                return;
            case 6:
            case 9:
                if (this.mCi.getRadioState() != 1) {
                    return;
                }
                onSignalStrengthResult((AsyncResult) message.obj);
                return;
            case 7:
                this.mCi.getSignalStrength(obtainMessage(9));
                return;
            case 8:
                onSignalStrengthResult((AsyncResult) message.obj);
                return;
            default:
                log("Unhandled message with number: " + message.what);
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$handleMessage$1(SignalRequestRecord signalRequestRecord, SignalRequestRecord signalRequestRecord2) {
        return signalRequestRecord2.mCallingUid == signalRequestRecord.mCallingUid && signalRequestRecord2.mSubId == signalRequestRecord.mSubId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispose() {
        this.mCi.unSetOnSignalStrengthUpdate(this);
    }

    private void onReset() {
        setDefaultSignalStrengthReportingCriteria();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getSignalStrengthFromCi() {
        this.mCi.getSignalStrength(obtainMessage(6));
    }

    private boolean onSignalStrengthResult(AsyncResult asyncResult) {
        Object obj;
        if (asyncResult.exception == null && (obj = asyncResult.result) != null) {
            this.mSignalStrength = (SignalStrength) obj;
            if (this.mPhone.getServiceStateTracker() != null) {
                this.mSignalStrength.updateLevel(this.mCarrierConfig, this.mPhone.getServiceStateTracker().mSS);
            }
        } else {
            log("onSignalStrengthResult() Exception from RIL : " + asyncResult.exception);
            this.mSignalStrength = new SignalStrength();
        }
        this.mSignalStrengthUpdatedTime = System.currentTimeMillis();
        return notifySignalStrength();
    }

    public SignalStrength getSignalStrength() {
        if (shouldRefreshSignalStrength()) {
            log("getSignalStrength() refreshing signal strength.");
            obtainMessage(7).sendToTarget();
        }
        return this.mSignalStrength;
    }

    private boolean shouldRefreshSignalStrength() {
        List<SubscriptionInfo> activeSubscriptionInfoList;
        ServiceState serviceState;
        long currentTimeMillis = System.currentTimeMillis();
        long j = this.mSignalStrengthUpdatedTime;
        if (j > currentTimeMillis || currentTimeMillis - j > SIGNAL_STRENGTH_REFRESH_THRESHOLD_IN_MS) {
            if (this.mPhone.isSubscriptionManagerServiceEnabled()) {
                activeSubscriptionInfoList = SubscriptionManagerService.getInstance().getActiveSubscriptionInfoList(this.mPhone.getContext().getOpPackageName(), this.mPhone.getContext().getAttributionTag());
            } else {
                activeSubscriptionInfoList = SubscriptionController.getInstance().getActiveSubscriptionInfoList(this.mPhone.getContext().getOpPackageName(), this.mPhone.getContext().getAttributionTag());
            }
            if (!ArrayUtils.isEmpty(activeSubscriptionInfoList)) {
                for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                    if (subscriptionInfo.isOpportunistic() && (serviceState = TelephonyManager.from(this.mPhone.getContext()).createForSubscriptionId(subscriptionInfo.getSubscriptionId()).getServiceState()) != null && serviceState.getDataRegistrationState() == 0) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    @VisibleForTesting
    public void updateReportingCriteria() {
        ArrayList arrayList = new ArrayList();
        int[] intArray = this.mCarrierConfig.getIntArray("gsm_rssi_thresholds_int_array");
        if (intArray != null) {
            arrayList.add(createSignalThresholdsInfo(1, intArray, 1, true));
        }
        int[] intArray2 = this.mCarrierConfig.getIntArray("wcdma_rscp_thresholds_int_array");
        if (intArray2 != null) {
            arrayList.add(createSignalThresholdsInfo(2, intArray2, 2, true));
        }
        int i = this.mCarrierConfig.getInt("parameters_used_for_lte_signal_bar_int", 1);
        int[] intArray3 = this.mCarrierConfig.getIntArray("lte_rsrp_thresholds_int_array");
        if (intArray3 != null) {
            arrayList.add(createSignalThresholdsInfo(3, intArray3, 3, (i & 1) != 0));
        }
        if (this.mPhone.getHalVersion(4).greaterOrEqual(RIL.RADIO_HAL_VERSION_1_5)) {
            int[] intArray4 = this.mCarrierConfig.getIntArray("lte_rsrq_thresholds_int_array");
            if (intArray4 != null) {
                arrayList.add(createSignalThresholdsInfo(4, intArray4, 3, (i & 2) != 0));
            }
            int[] intArray5 = this.mCarrierConfig.getIntArray("lte_rssnr_thresholds_int_array");
            if (intArray5 != null) {
                arrayList.add(createSignalThresholdsInfo(5, intArray5, 3, (i & 4) != 0));
            }
            int i2 = this.mCarrierConfig.getInt("parameters_use_for_5g_nr_signal_bar_int", 1);
            int[] intArray6 = this.mCarrierConfig.getIntArray("5g_nr_ssrsrp_thresholds_int_array");
            if (intArray6 != null) {
                arrayList.add(createSignalThresholdsInfo(6, intArray6, 6, (i2 & 1) != 0));
            }
            int[] intArray7 = this.mCarrierConfig.getIntArray("5g_nr_ssrsrq_thresholds_int_array");
            if (intArray7 != null) {
                arrayList.add(createSignalThresholdsInfo(7, intArray7, 6, (i2 & 2) != 0));
            }
            int[] intArray8 = this.mCarrierConfig.getIntArray("5g_nr_sssinr_thresholds_int_array");
            if (intArray8 != null) {
                arrayList.add(createSignalThresholdsInfo(8, intArray8, 6, (i2 & 4) != 0));
            }
            int[] intArray9 = this.mCarrierConfig.getIntArray("wcdma_ecno_thresholds_int_array");
            if (intArray9 != null) {
                arrayList.add(createSignalThresholdsInfo(9, intArray9, 2, false));
            }
        }
        consolidatedAndSetReportingCriteria(arrayList);
    }

    private void setDefaultSignalStrengthReportingCriteria() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(createSignalThresholdsInfo(1, AccessNetworkThresholds.GERAN, 1, true));
        arrayList.add(createSignalThresholdsInfo(2, AccessNetworkThresholds.UTRAN, 2, true));
        arrayList.add(createSignalThresholdsInfo(3, AccessNetworkThresholds.EUTRAN_RSRP, 3, true));
        arrayList.add(createSignalThresholdsInfo(1, AccessNetworkThresholds.CDMA2000, 4, true));
        if (this.mPhone.getHalVersion(4).greaterOrEqual(RIL.RADIO_HAL_VERSION_1_5)) {
            arrayList.add(createSignalThresholdsInfo(4, AccessNetworkThresholds.EUTRAN_RSRQ, 3, false));
            arrayList.add(createSignalThresholdsInfo(5, AccessNetworkThresholds.EUTRAN_RSSNR, 3, true));
            arrayList.add(createSignalThresholdsInfo(6, AccessNetworkThresholds.NGRAN_SSRSRP, 6, true));
            arrayList.add(createSignalThresholdsInfo(7, AccessNetworkThresholds.NGRAN_SSRSRQ, 6, false));
            arrayList.add(createSignalThresholdsInfo(8, AccessNetworkThresholds.NGRAN_SSSINR, 6, false));
            arrayList.add(createSignalThresholdsInfo(9, AccessNetworkThresholds.UTRAN_ECNO, 2, false));
        }
        consolidatedAndSetReportingCriteria(arrayList);
    }

    private void consolidatedAndSetReportingCriteria(List<SignalThresholdInfo> list) {
        ArrayList arrayList = new ArrayList(list.size());
        for (SignalThresholdInfo signalThresholdInfo : list) {
            int radioAccessNetworkType = signalThresholdInfo.getRadioAccessNetworkType();
            int signalMeasurementType = signalThresholdInfo.getSignalMeasurementType();
            boolean z = false;
            boolean z2 = signalThresholdInfo.isEnabled() && shouldHonorSystemThresholds();
            int[] consolidatedSignalThresholds = getConsolidatedSignalThresholds(radioAccessNetworkType, signalMeasurementType, z2 ? signalThresholdInfo.getThresholds() : new int[0], 1);
            boolean shouldEnableSignalThresholdForAppRequest = shouldEnableSignalThresholdForAppRequest(radioAccessNetworkType, signalMeasurementType, this.mPhone.getSubId(), this.mPhone.isDeviceIdle());
            SignalThresholdInfo.Builder thresholds = new SignalThresholdInfo.Builder().setRadioAccessNetworkType(radioAccessNetworkType).setSignalMeasurementType(signalMeasurementType).setHysteresisMs(GbaManager.RETRY_TIME_MS).setHysteresisDb(getMinimumHysteresisDb(shouldEnableSignalThresholdForAppRequest, radioAccessNetworkType, signalMeasurementType, consolidatedSignalThresholds)).setThresholds(consolidatedSignalThresholds, true);
            if (z2 || shouldEnableSignalThresholdForAppRequest) {
                z = true;
            }
            arrayList.add(thresholds.setIsEnabled(z).build());
        }
        this.mCi.setSignalStrengthReportingCriteria(arrayList, null);
        localLog("setSignalStrengthReportingCriteria consolidatedSignalThresholdInfos=" + arrayList);
    }

    @VisibleForTesting
    public int getMinimumHysteresisDb(boolean z, int i, int i2, int[] iArr) {
        int hysteresisDbFromCarrierConfig = getHysteresisDbFromCarrierConfig(i, i2);
        return z ? computeHysteresisDbOnSmallestThresholdDelta(Math.min(hysteresisDbFromCarrierConfig, getHysteresisDbFromSignalThresholdInfoRequests(i, i2)), iArr) : hysteresisDbFromCarrierConfig;
    }

    private int getHysteresisDbFromSignalThresholdInfoRequests(int i, int i2) {
        int i3 = 2;
        for (SignalRequestRecord signalRequestRecord : this.mSignalRequestRecords) {
            for (SignalThresholdInfo signalThresholdInfo : signalRequestRecord.mRequest.getSignalThresholdInfos()) {
                if (isRanAndSignalMeasurementTypeMatch(i, i2, signalThresholdInfo) && signalThresholdInfo.getHysteresisDb() >= 0) {
                    i3 = signalThresholdInfo.getHysteresisDb();
                }
            }
        }
        return i3;
    }

    private int getHysteresisDbFromCarrierConfig(int i, int i2) {
        String str;
        if (i == 1) {
            if (i2 == 1) {
                str = "geran_rssi_hysteresis_db_int";
            }
            str = null;
        } else if (i != 2) {
            if (i != 3) {
                if (i != 6) {
                    localLog("No matching configuration");
                } else if (i2 == 6) {
                    str = "ngran_ssrsrp_hysteresis_db_int";
                } else if (i2 == 7) {
                    str = "ngran_ssrsrq_hysteresis_db_int";
                } else if (i2 == 8) {
                    str = "ngran_sssinr_hysteresis_db_int";
                }
                str = null;
            } else if (i2 == 3) {
                str = "eutran_rsrp_hysteresis_db_int";
            } else if (i2 == 4) {
                str = "eutran_rsrq_hysteresis_db_int";
            } else {
                if (i2 == 5) {
                    str = "eutran_rssnr_hysteresis_db_int";
                }
                str = null;
            }
        } else if (i2 == 2) {
            str = "utran_rscp_hysteresis_db_int";
        } else {
            if (i2 == 9) {
                str = "utran_ecno_hysteresis_db_int";
            }
            str = null;
        }
        int i3 = str != null ? this.mCarrierConfig.getInt(str, 2) : 2;
        if (i3 >= 0) {
            return i3;
        }
        return 2;
    }

    private int computeHysteresisDbOnSmallestThresholdDelta(int i, int[] iArr) {
        if (iArr.length > 1) {
            int i2 = 0;
            while (i2 != iArr.length - 1) {
                int i3 = i2 + 1;
                int i4 = iArr[i3];
                int i5 = iArr[i2];
                if (i4 - i5 < i) {
                    i = i4 - i5;
                }
                i2 = i3;
            }
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSignalStrengthDefaultValues() {
        this.mSignalStrength = new SignalStrength();
        this.mSignalStrengthUpdatedTime = System.currentTimeMillis();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean notifySignalStrength() {
        boolean z = false;
        if (this.mSignalStrength.equals(this.mLastSignalStrength)) {
            return false;
        }
        try {
            this.mPhone.notifySignalStrength();
            z = true;
            this.mLastSignalStrength = this.mSignalStrength;
            return true;
        } catch (NullPointerException e) {
            log("updateSignalStrength() Phone already destroyed: " + e + "SignalStrength not notified");
            return z;
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("SignalStrengthController - phoneId: " + this.mPhone.getPhoneId());
        printWriter.println("SignalStrengthController - Log Begin ----");
        this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
        printWriter.println("SignalStrengthController - Log End ----");
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.increaseIndent();
        printWriter.println("mSignalRequestRecords=" + this.mSignalRequestRecords);
        printWriter.println(" mLastSignalStrength=" + this.mLastSignalStrength);
        printWriter.println(" mSignalStrength=" + this.mSignalStrength);
        printWriter.println(" mLteRsrpBoost=" + this.mLteRsrpBoost);
        printWriter.println(" mNrRsrpBoost=" + Arrays.toString(this.mNrRsrpBoost));
        printWriter.println(" mEarfcnPairListForRsrpBoost=" + this.mEarfcnPairListForRsrpBoost);
        printWriter.println(" mNrarfcnRangeListForRsrpBoost=" + this.mNrarfcnRangeListForRsrpBoost);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.flush();
    }

    public void setSignalStrengthUpdateRequest(int i, int i2, SignalStrengthUpdateRequest signalStrengthUpdateRequest, Message message) {
        sendMessage(obtainMessage(1, new Pair(new SignalRequestRecord(i, i2, signalStrengthUpdateRequest), message)));
        localLog("setSignalStrengthUpdateRequest subId=" + i + " callingUid=" + i2 + " request=" + signalStrengthUpdateRequest);
    }

    public void clearSignalStrengthUpdateRequest(int i, int i2, SignalStrengthUpdateRequest signalStrengthUpdateRequest, Message message) {
        sendMessage(obtainMessage(2, new Pair(new SignalRequestRecord(i, i2, signalStrengthUpdateRequest), message)));
        localLog("clearSignalStrengthUpdateRequest subId=" + i + " callingUid=" + i2 + " request=" + signalStrengthUpdateRequest);
    }

    @VisibleForTesting
    public int[] getConsolidatedSignalThresholds(int i, int i2, int[] iArr, final int i3) {
        TreeSet<Integer> treeSet = new TreeSet(new Comparator() { // from class: com.android.internal.telephony.SignalStrengthController$$ExternalSyntheticLambda4
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int lambda$getConsolidatedSignalThresholds$2;
                lambda$getConsolidatedSignalThresholds$2 = SignalStrengthController.lambda$getConsolidatedSignalThresholds$2(i3, (Integer) obj, (Integer) obj2);
                return lambda$getConsolidatedSignalThresholds$2;
            }
        });
        int i4 = 0;
        if (iArr != null) {
            for (int i5 : iArr) {
                treeSet.add(Integer.valueOf(i5));
            }
        }
        boolean isDeviceIdle = this.mPhone.isDeviceIdle();
        int subId = this.mPhone.getSubId();
        for (SignalRequestRecord signalRequestRecord : this.mSignalRequestRecords) {
            if (subId == signalRequestRecord.mSubId && (!isDeviceIdle || signalRequestRecord.mRequest.isReportingRequestedWhileIdle())) {
                for (SignalThresholdInfo signalThresholdInfo : signalRequestRecord.mRequest.getSignalThresholdInfos()) {
                    if (isRanAndSignalMeasurementTypeMatch(i, i2, signalThresholdInfo)) {
                        for (int i6 : signalThresholdInfo.getThresholds()) {
                            treeSet.add(Integer.valueOf(i6));
                        }
                    }
                }
            }
        }
        int[] iArr2 = new int[treeSet.size()];
        for (Integer num : treeSet) {
            iArr2[i4] = num.intValue();
            i4++;
        }
        return iArr2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$getConsolidatedSignalThresholds$2(int i, Integer num, Integer num2) {
        if (num2.intValue() < num.intValue() - i || num2.intValue() > num.intValue() + i) {
            return Integer.compare(num.intValue(), num2.intValue());
        }
        return 0;
    }

    @VisibleForTesting
    public boolean shouldHonorSystemThresholds() {
        if (this.mPhone.isDeviceIdle()) {
            final int subId = this.mPhone.getSubId();
            return this.mSignalRequestRecords.stream().anyMatch(new Predicate() { // from class: com.android.internal.telephony.SignalStrengthController$$ExternalSyntheticLambda2
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$shouldHonorSystemThresholds$3;
                    lambda$shouldHonorSystemThresholds$3 = SignalStrengthController.lambda$shouldHonorSystemThresholds$3(subId, (SignalStrengthController.SignalRequestRecord) obj);
                    return lambda$shouldHonorSystemThresholds$3;
                }
            });
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$shouldHonorSystemThresholds$3(int i, SignalRequestRecord signalRequestRecord) {
        return i == signalRequestRecord.mSubId && signalRequestRecord.mRequest.isSystemThresholdReportingRequestedWhileIdle();
    }

    @VisibleForTesting
    public void onDeviceIdleStateChanged(boolean z) {
        sendMessage(obtainMessage(3, Boolean.valueOf(z)));
        localLog("onDeviceIdleStateChanged isDeviceIdle=" + z);
    }

    @VisibleForTesting
    public boolean shouldEnableSignalThresholdForAppRequest(int i, int i2, int i3, boolean z) {
        for (SignalRequestRecord signalRequestRecord : this.mSignalRequestRecords) {
            if (i3 == signalRequestRecord.mSubId) {
                for (SignalThresholdInfo signalThresholdInfo : signalRequestRecord.mRequest.getSignalThresholdInfos()) {
                    if (isRanAndSignalMeasurementTypeMatch(i, i2, signalThresholdInfo) && (!z || isSignalReportRequestedWhileIdle(signalRequestRecord.mRequest))) {
                        return true;
                    }
                }
                continue;
            }
        }
        return false;
    }

    private static boolean isRanAndSignalMeasurementTypeMatch(int i, int i2, SignalThresholdInfo signalThresholdInfo) {
        return i == signalThresholdInfo.getRadioAccessNetworkType() && i2 == signalThresholdInfo.getSignalMeasurementType();
    }

    private static boolean isSignalReportRequestedWhileIdle(SignalStrengthUpdateRequest signalStrengthUpdateRequest) {
        return signalStrengthUpdateRequest.isSystemThresholdReportingRequestedWhileIdle() || signalStrengthUpdateRequest.isReportingRequestedWhileIdle();
    }

    private PersistableBundle getCarrierConfig() {
        PersistableBundle configForSubId;
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mPhone.getContext().getSystemService("carrier_config");
        return (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(this.mPhone.getSubId())) == null) ? CarrierConfigManager.getDefaultConfig() : configForSubId;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SignalRequestRecord implements IBinder.DeathRecipient {
        final int mCallingUid;
        final SignalStrengthUpdateRequest mRequest;
        final int mSubId;

        SignalRequestRecord(int i, int i2, SignalStrengthUpdateRequest signalStrengthUpdateRequest) {
            this.mCallingUid = i2;
            this.mSubId = i;
            this.mRequest = signalStrengthUpdateRequest;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            SignalStrengthController signalStrengthController = SignalStrengthController.this;
            signalStrengthController.localLog("binderDied record=" + this);
            SignalStrengthController.this.clearSignalStrengthUpdateRequest(this.mSubId, this.mCallingUid, this.mRequest, null);
        }

        public String toString() {
            StringBuffer stringBuffer = new StringBuffer("SignalRequestRecord {");
            stringBuffer.append("mSubId=");
            stringBuffer.append(this.mSubId);
            stringBuffer.append(" mCallingUid=");
            stringBuffer.append(this.mCallingUid);
            stringBuffer.append(" mRequest=");
            stringBuffer.append(this.mRequest);
            stringBuffer.append("}");
            return stringBuffer.toString();
        }
    }

    private void updateAlwaysReportSignalStrength() {
        final int subId = this.mPhone.getSubId();
        this.mPhone.setAlwaysReportSignalStrength(this.mSignalRequestRecords.stream().anyMatch(new Predicate() { // from class: com.android.internal.telephony.SignalStrengthController$$ExternalSyntheticLambda5
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updateAlwaysReportSignalStrength$4;
                lambda$updateAlwaysReportSignalStrength$4 = SignalStrengthController.lambda$updateAlwaysReportSignalStrength$4(subId, (SignalStrengthController.SignalRequestRecord) obj);
                return lambda$updateAlwaysReportSignalStrength$4;
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateAlwaysReportSignalStrength$4(int i, SignalRequestRecord signalRequestRecord) {
        return signalRequestRecord.mSubId == i && isSignalReportRequestedWhileIdle(signalRequestRecord.mRequest);
    }

    void updateArfcnLists() {
        synchronized (this.mRsrpBoostLock) {
            this.mLteRsrpBoost = this.mCarrierConfig.getInt("lte_earfcns_rsrp_boost_int", 0);
            this.mEarfcnPairListForRsrpBoost = convertEarfcnStringArrayToPairList(this.mCarrierConfig.getStringArray("boosted_lte_earfcns_string_array"));
            this.mNrRsrpBoost = this.mCarrierConfig.getIntArray("nrarfcns_rsrp_boost_int_array");
            ArrayList<Pair<Integer, Integer>> convertEarfcnStringArrayToPairList = convertEarfcnStringArrayToPairList(this.mCarrierConfig.getStringArray("boosted_nrarfcns_string_array"));
            this.mNrarfcnRangeListForRsrpBoost = convertEarfcnStringArrayToPairList;
            int[] iArr = this.mNrRsrpBoost;
            if ((iArr == null && convertEarfcnStringArrayToPairList != null) || ((iArr != null && convertEarfcnStringArrayToPairList == null) || (iArr != null && convertEarfcnStringArrayToPairList != null && iArr.length != convertEarfcnStringArrayToPairList.size()))) {
                loge("Invalid parameters for NR RSRP boost");
                this.mNrRsrpBoost = null;
                this.mNrarfcnRangeListForRsrpBoost = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateServiceStateArfcnRsrpBoost(ServiceState serviceState, CellIdentity cellIdentity) {
        int i;
        int nrarfcn;
        int containsEarfcnInEarfcnRange;
        int[] iArr;
        if (cellIdentity == null) {
            return;
        }
        synchronized (this.mRsrpBoostLock) {
            int type = cellIdentity.getType();
            i = 0;
            if (type == 3) {
                int earfcn = ((CellIdentityLte) cellIdentity).getEarfcn();
                if (earfcn != -1 && containsEarfcnInEarfcnRange(this.mEarfcnPairListForRsrpBoost, earfcn) != -1) {
                    i = this.mLteRsrpBoost;
                }
            } else if (type == 6 && (nrarfcn = ((CellIdentityNr) cellIdentity).getNrarfcn()) != -1 && (containsEarfcnInEarfcnRange = containsEarfcnInEarfcnRange(this.mNrarfcnRangeListForRsrpBoost, nrarfcn)) != -1 && (iArr = this.mNrRsrpBoost) != null) {
                i = iArr[containsEarfcnInEarfcnRange];
            }
        }
        serviceState.setArfcnRsrpBoost(i);
    }

    private static int containsEarfcnInEarfcnRange(ArrayList<Pair<Integer, Integer>> arrayList, int i) {
        if (arrayList != null) {
            Iterator<Pair<Integer, Integer>> it = arrayList.iterator();
            int i2 = 0;
            while (it.hasNext()) {
                Pair<Integer, Integer> next = it.next();
                if (i >= ((Integer) next.first).intValue() && i <= ((Integer) next.second).intValue()) {
                    return i2;
                }
                i2++;
            }
            return -1;
        }
        return -1;
    }

    private static ArrayList<Pair<Integer, Integer>> convertEarfcnStringArrayToPairList(String[] strArr) {
        int parseInt;
        int parseInt2;
        ArrayList<Pair<Integer, Integer>> arrayList = new ArrayList<>();
        if (strArr != null) {
            for (String str : strArr) {
                try {
                    String[] split = str.split("-");
                    if (split.length != 2 || (parseInt = Integer.parseInt(split[0])) > (parseInt2 = Integer.parseInt(split[1]))) {
                        return null;
                    }
                    arrayList.add(new Pair<>(Integer.valueOf(parseInt), Integer.valueOf(parseInt2)));
                } catch (NumberFormatException | PatternSyntaxException unused) {
                    return null;
                }
            }
        }
        return arrayList;
    }

    private void onCarrierConfigurationChanged(int i) {
        if (i != this.mPhone.getPhoneId()) {
            return;
        }
        this.mCarrierConfig = getCarrierConfig();
        log("Carrier Config changed.");
        updateArfcnLists();
        updateReportingCriteria();
    }

    private static SignalThresholdInfo createSignalThresholdsInfo(int i, int[] iArr, int i2, boolean z) {
        return new SignalThresholdInfo.Builder().setSignalMeasurementType(i).setThresholds(iArr).setRadioAccessNetworkType(i2).setIsEnabled(z).build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class AccessNetworkThresholds {
        public static final int[] GERAN = {-109, -103, -97, -89};
        public static final int[] UTRAN = {-114, -104, -94, -84};
        public static final int[] EUTRAN_RSRP = {-128, -118, -108, -98};
        public static final int[] EUTRAN_RSRQ = {-20, -17, -14, -11};
        public static final int[] EUTRAN_RSSNR = {-3, 1, 5, 13};
        public static final int[] CDMA2000 = {-105, -90, -75, -65};
        public static final int[] NGRAN_SSRSRP = {-110, -90, -80, -65};
        public static final int[] NGRAN_SSRSRQ = {-31, -19, -7, 6};
        public static final int[] NGRAN_SSSINR = {-5, 5, 15, 30};
        public static final int[] UTRAN_ECNO = {-24, -14, -6, 1};

        private AccessNetworkThresholds() {
        }
    }

    private static void loge(String str) {
        Rlog.e("SSCtr", str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void localLog(String str) {
        Rlog.d("SSCtr", str);
        LocalLog localLog = this.mLocalLog;
        localLog.log("SSCtr: " + str);
    }
}
