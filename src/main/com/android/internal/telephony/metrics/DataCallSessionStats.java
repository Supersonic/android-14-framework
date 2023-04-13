package com.android.internal.telephony.metrics;

import android.os.SystemClock;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.data.DataCallResponse;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.ServiceStateTracker;
import com.android.internal.telephony.SubscriptionController;
import com.android.internal.telephony.nano.PersistAtomsProto$DataCallSession;
import com.android.internal.telephony.subscription.SubscriptionInfoInternal;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import com.android.telephony.Rlog;
import java.util.Arrays;
import java.util.Random;
/* loaded from: classes.dex */
public class DataCallSessionStats {
    private static final Random RANDOM = new Random();
    public static final int SIZE_LIMIT_HANDOVER_FAILURES = 15;
    private static final String TAG = "DataCallSessionStats";
    private final PersistAtomsStorage mAtomsStorage = PhoneFactory.getMetricsCollector().getAtomsStorage();
    private PersistAtomsProto$DataCallSession mDataCallSession;
    private final Phone mPhone;
    private long mStartTime;

    public DataCallSessionStats(Phone phone) {
        this.mPhone = phone;
    }

    public synchronized void onSetupDataCall(int i) {
        this.mDataCallSession = getDefaultProto(i);
        this.mStartTime = getTimeMillis();
        PhoneFactory.getMetricsCollector().registerOngoingDataCallStat(this);
    }

    public synchronized void onSetupDataCallResponse(DataCallResponse dataCallResponse, int i, int i2, int i3, int i4) {
        PersistAtomsProto$DataCallSession persistAtomsProto$DataCallSession = this.mDataCallSession;
        int i5 = 0;
        if (persistAtomsProto$DataCallSession == null) {
            loge("onSetupDataCallResponse: no DataCallSession atom has been initiated.", new Object[0]);
            return;
        }
        if (i != 0) {
            persistAtomsProto$DataCallSession.ratAtEnd = i;
            if (i != 18) {
                i5 = ServiceStateStats.getBand(this.mPhone);
            }
            persistAtomsProto$DataCallSession.bandAtEnd = i5;
        }
        PersistAtomsProto$DataCallSession persistAtomsProto$DataCallSession2 = this.mDataCallSession;
        if (persistAtomsProto$DataCallSession2.apnTypeBitmask == 0) {
            persistAtomsProto$DataCallSession2.apnTypeBitmask = i2;
        }
        persistAtomsProto$DataCallSession2.ipType = i3;
        persistAtomsProto$DataCallSession2.failureCause = i4;
        if (dataCallResponse != null) {
            persistAtomsProto$DataCallSession2.suggestedRetryMillis = (int) Math.min(dataCallResponse.getRetryDurationMillis(), 2147483647L);
            if (i4 != 0) {
                this.mDataCallSession.setupFailed = true;
                endDataCallSession();
            }
        }
    }

    public synchronized void setDeactivateDataCallReason(int i) {
        PersistAtomsProto$DataCallSession persistAtomsProto$DataCallSession = this.mDataCallSession;
        if (persistAtomsProto$DataCallSession == null) {
            loge("setDeactivateDataCallReason: no DataCallSession atom has been initiated.", new Object[0]);
            return;
        }
        if (i == 1) {
            persistAtomsProto$DataCallSession.deactivateReason = 1;
        } else if (i == 2) {
            persistAtomsProto$DataCallSession.deactivateReason = 2;
        } else if (i == 3) {
            persistAtomsProto$DataCallSession.deactivateReason = 3;
        } else {
            persistAtomsProto$DataCallSession.deactivateReason = 0;
        }
    }

    public synchronized void onDataCallDisconnected(int i) {
        PersistAtomsProto$DataCallSession persistAtomsProto$DataCallSession = this.mDataCallSession;
        if (persistAtomsProto$DataCallSession == null) {
            logi("onDataCallDisconnected: no DataCallSession atom has been initiated.", new Object[0]);
            return;
        }
        persistAtomsProto$DataCallSession.failureCause = i;
        persistAtomsProto$DataCallSession.durationMinutes = convertMillisToMinutes(getTimeMillis() - this.mStartTime);
        endDataCallSession();
    }

    public synchronized void onHandoverFailure(int i, int i2, int i3) {
        PersistAtomsProto$DataCallSession persistAtomsProto$DataCallSession = this.mDataCallSession;
        if (persistAtomsProto$DataCallSession != null) {
            int[] iArr = persistAtomsProto$DataCallSession.handoverFailureCauses;
            if (iArr.length < 15) {
                int[] iArr2 = persistAtomsProto$DataCallSession.handoverFailureRat;
                int i4 = i2 | (i3 << 16);
                for (int i5 = 0; i5 < iArr.length; i5++) {
                    if (iArr[i5] == i && iArr2[i5] == i4) {
                        return;
                    }
                }
                this.mDataCallSession.handoverFailureCauses = Arrays.copyOf(iArr, iArr.length + 1);
                PersistAtomsProto$DataCallSession persistAtomsProto$DataCallSession2 = this.mDataCallSession;
                persistAtomsProto$DataCallSession2.handoverFailureCauses[iArr.length] = i;
                persistAtomsProto$DataCallSession2.handoverFailureRat = Arrays.copyOf(iArr2, iArr2.length + 1);
                this.mDataCallSession.handoverFailureRat[iArr2.length] = i4;
            }
        }
    }

    public synchronized void onDrsOrRatChanged(int i) {
        PersistAtomsProto$DataCallSession persistAtomsProto$DataCallSession = this.mDataCallSession;
        if (persistAtomsProto$DataCallSession != null && i != 0) {
            if (persistAtomsProto$DataCallSession.ratAtEnd != i) {
                persistAtomsProto$DataCallSession.ratSwitchCount++;
                persistAtomsProto$DataCallSession.ratAtEnd = i;
            }
            persistAtomsProto$DataCallSession.bandAtEnd = i == 18 ? 0 : ServiceStateStats.getBand(this.mPhone);
        }
    }

    public void onUnmeteredUpdate(int i) {
        this.mAtomsStorage.addUnmeteredNetworks(this.mPhone.getPhoneId(), this.mPhone.getCarrierId(), TelephonyManager.getBitMaskForNetworkType(i));
    }

    public synchronized void conclude() {
        PersistAtomsProto$DataCallSession persistAtomsProto$DataCallSession = this.mDataCallSession;
        if (persistAtomsProto$DataCallSession != null) {
            PersistAtomsProto$DataCallSession copyOf = copyOf(persistAtomsProto$DataCallSession);
            long timeMillis = getTimeMillis();
            copyOf.durationMinutes = convertMillisToMinutes(timeMillis - this.mStartTime);
            this.mStartTime = timeMillis;
            PersistAtomsProto$DataCallSession persistAtomsProto$DataCallSession2 = this.mDataCallSession;
            persistAtomsProto$DataCallSession2.ratSwitchCount = 0L;
            persistAtomsProto$DataCallSession2.handoverFailureCauses = new int[0];
            persistAtomsProto$DataCallSession2.handoverFailureRat = new int[0];
            this.mAtomsStorage.addDataCallSession(copyOf);
        }
    }

    private void endDataCallSession() {
        SubscriptionInfo subscriptionInfo;
        this.mDataCallSession.oosAtEnd = getIsOos();
        this.mDataCallSession.ongoing = false;
        if (this.mPhone.isSubscriptionManagerServiceEnabled()) {
            subscriptionInfo = SubscriptionManagerService.getInstance().getSubscriptionInfo(this.mPhone.getSubId());
        } else {
            subscriptionInfo = SubscriptionController.getInstance().getSubscriptionInfo(this.mPhone.getSubId());
        }
        if (this.mPhone.getSubId() != SubscriptionManager.getDefaultDataSubscriptionId() && (this.mDataCallSession.apnTypeBitmask & 17) == 17 && subscriptionInfo != null && !subscriptionInfo.isOpportunistic()) {
            this.mDataCallSession.isNonDds = true;
        }
        PhoneFactory.getMetricsCollector().unregisterOngoingDataCallStat(this);
        this.mAtomsStorage.addDataCallSession(this.mDataCallSession);
        this.mDataCallSession = null;
    }

    private static long convertMillisToMinutes(long j) {
        return Math.round(j / 60000.0d);
    }

    private static PersistAtomsProto$DataCallSession copyOf(PersistAtomsProto$DataCallSession persistAtomsProto$DataCallSession) {
        PersistAtomsProto$DataCallSession persistAtomsProto$DataCallSession2 = new PersistAtomsProto$DataCallSession();
        persistAtomsProto$DataCallSession2.dimension = persistAtomsProto$DataCallSession.dimension;
        persistAtomsProto$DataCallSession2.isMultiSim = persistAtomsProto$DataCallSession.isMultiSim;
        persistAtomsProto$DataCallSession2.isEsim = persistAtomsProto$DataCallSession.isEsim;
        persistAtomsProto$DataCallSession2.apnTypeBitmask = persistAtomsProto$DataCallSession.apnTypeBitmask;
        persistAtomsProto$DataCallSession2.carrierId = persistAtomsProto$DataCallSession.carrierId;
        persistAtomsProto$DataCallSession2.isRoaming = persistAtomsProto$DataCallSession.isRoaming;
        persistAtomsProto$DataCallSession2.ratAtEnd = persistAtomsProto$DataCallSession.ratAtEnd;
        persistAtomsProto$DataCallSession2.oosAtEnd = persistAtomsProto$DataCallSession.oosAtEnd;
        persistAtomsProto$DataCallSession2.ratSwitchCount = persistAtomsProto$DataCallSession.ratSwitchCount;
        persistAtomsProto$DataCallSession2.isOpportunistic = persistAtomsProto$DataCallSession.isOpportunistic;
        persistAtomsProto$DataCallSession2.ipType = persistAtomsProto$DataCallSession.ipType;
        persistAtomsProto$DataCallSession2.setupFailed = persistAtomsProto$DataCallSession.setupFailed;
        persistAtomsProto$DataCallSession2.failureCause = persistAtomsProto$DataCallSession.failureCause;
        persistAtomsProto$DataCallSession2.suggestedRetryMillis = persistAtomsProto$DataCallSession.suggestedRetryMillis;
        persistAtomsProto$DataCallSession2.deactivateReason = persistAtomsProto$DataCallSession.deactivateReason;
        persistAtomsProto$DataCallSession2.durationMinutes = persistAtomsProto$DataCallSession.durationMinutes;
        persistAtomsProto$DataCallSession2.ongoing = persistAtomsProto$DataCallSession.ongoing;
        persistAtomsProto$DataCallSession2.bandAtEnd = persistAtomsProto$DataCallSession.bandAtEnd;
        int[] iArr = persistAtomsProto$DataCallSession.handoverFailureCauses;
        persistAtomsProto$DataCallSession2.handoverFailureCauses = Arrays.copyOf(iArr, iArr.length);
        int[] iArr2 = persistAtomsProto$DataCallSession.handoverFailureRat;
        persistAtomsProto$DataCallSession2.handoverFailureRat = Arrays.copyOf(iArr2, iArr2.length);
        persistAtomsProto$DataCallSession2.isNonDds = persistAtomsProto$DataCallSession.isNonDds;
        return persistAtomsProto$DataCallSession2;
    }

    private PersistAtomsProto$DataCallSession getDefaultProto(int i) {
        PersistAtomsProto$DataCallSession persistAtomsProto$DataCallSession = new PersistAtomsProto$DataCallSession();
        persistAtomsProto$DataCallSession.dimension = RANDOM.nextInt();
        persistAtomsProto$DataCallSession.isMultiSim = SimSlotState.isMultiSim();
        persistAtomsProto$DataCallSession.isEsim = SimSlotState.isEsim(this.mPhone.getPhoneId());
        persistAtomsProto$DataCallSession.apnTypeBitmask = i;
        persistAtomsProto$DataCallSession.carrierId = this.mPhone.getCarrierId();
        persistAtomsProto$DataCallSession.isRoaming = getIsRoaming();
        persistAtomsProto$DataCallSession.oosAtEnd = false;
        persistAtomsProto$DataCallSession.ratSwitchCount = 0L;
        persistAtomsProto$DataCallSession.isOpportunistic = getIsOpportunistic();
        persistAtomsProto$DataCallSession.ipType = 0;
        persistAtomsProto$DataCallSession.setupFailed = false;
        persistAtomsProto$DataCallSession.failureCause = 0;
        persistAtomsProto$DataCallSession.suggestedRetryMillis = 0;
        persistAtomsProto$DataCallSession.deactivateReason = 0;
        persistAtomsProto$DataCallSession.durationMinutes = 0L;
        persistAtomsProto$DataCallSession.ongoing = true;
        persistAtomsProto$DataCallSession.handoverFailureCauses = new int[0];
        persistAtomsProto$DataCallSession.handoverFailureRat = new int[0];
        persistAtomsProto$DataCallSession.isNonDds = false;
        return persistAtomsProto$DataCallSession;
    }

    private boolean getIsRoaming() {
        ServiceStateTracker serviceStateTracker = this.mPhone.getServiceStateTracker();
        ServiceState serviceState = serviceStateTracker != null ? serviceStateTracker.getServiceState() : null;
        return serviceState != null && serviceState.getRoaming();
    }

    private boolean getIsOpportunistic() {
        if (this.mPhone.isSubscriptionManagerServiceEnabled()) {
            SubscriptionInfoInternal subscriptionInfoInternal = SubscriptionManagerService.getInstance().getSubscriptionInfoInternal(this.mPhone.getSubId());
            return subscriptionInfoInternal != null && subscriptionInfoInternal.isOpportunistic();
        }
        SubscriptionController subscriptionController = SubscriptionController.getInstance();
        return subscriptionController != null && subscriptionController.isOpportunistic(this.mPhone.getSubId());
    }

    private boolean getIsOos() {
        ServiceStateTracker serviceStateTracker = this.mPhone.getServiceStateTracker();
        ServiceState serviceState = serviceStateTracker != null ? serviceStateTracker.getServiceState() : null;
        return serviceState != null && serviceState.getDataRegistrationState() == 1;
    }

    private void logi(String str, Object... objArr) {
        String str2 = TAG;
        Rlog.i(str2, "[" + this.mPhone.getPhoneId() + "]" + String.format(str, objArr));
    }

    private void loge(String str, Object... objArr) {
        String str2 = TAG;
        Rlog.e(str2, "[" + this.mPhone.getPhoneId() + "]" + String.format(str, objArr));
    }

    @VisibleForTesting
    protected long getTimeMillis() {
        return SystemClock.elapsedRealtime();
    }
}
