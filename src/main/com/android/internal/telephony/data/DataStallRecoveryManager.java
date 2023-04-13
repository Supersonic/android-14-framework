package com.android.internal.telephony.data;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.telephony.data.DataProfile;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.AndroidUtilIndentingPrintWriter;
import com.android.internal.telephony.LocalLog;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.data.DataConfigManager;
import com.android.internal.telephony.data.DataNetworkController;
import com.android.internal.telephony.data.DataSettingsManager;
import com.android.internal.telephony.metrics.DataStallRecoveryStats;
import com.android.internal.telephony.metrics.TelephonyMetrics;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class DataStallRecoveryManager extends Handler {
    public static final int RECOVERY_ACTION_CLEANUP = 1;
    public static final int RECOVERY_ACTION_GET_DATA_CALL_LIST = 0;
    public static final int RECOVERY_ACTION_RADIO_RESTART = 3;
    @Deprecated
    public static final int RECOVERY_ACTION_REREGISTER = 2;
    public static final int RECOVERY_ACTION_RESET_MODEM = 4;
    private final DataConfigManager mDataConfigManager;
    private final DataNetworkController mDataNetworkController;
    private long[] mDataStallRecoveryDelayMillisArray;
    private DataStallRecoveryManagerCallback mDataStallRecoveryManagerCallback;
    @VisibleForTesting
    public long mDataStallStartMs;
    private boolean mDataStalled;
    private boolean mIsAirPlaneModeEnableDuringDataStall;
    private boolean mIsAttemptedAllSteps;
    private boolean mIsInternetNetworkConnected;
    private boolean mIsValidNetwork;
    private int mLastAction;
    private boolean mLastActionReported;
    private final LocalLog mLocalLog;
    private final String mLogTag;
    private boolean mMobileDataChangedToEnabledDuringDataStall;
    private boolean mNetworkCheckTimerStarted;
    private final Phone mPhone;
    private int mRadioPowerState;
    private boolean mRadioStateChangedDuringDataStall;
    private int mRecoveryAction;
    private boolean mRecoveryTriggered;
    private boolean[] mSkipRecoveryActionArray;
    private long mTimeLastRecoveryStartMs;
    private final DataServiceManager mWwanDataServiceManager;

    private void logv(String str) {
    }

    /* loaded from: classes.dex */
    public static abstract class DataStallRecoveryManagerCallback extends DataCallback {
        public abstract void onDataStallReestablishInternet();

        public DataStallRecoveryManagerCallback(Executor executor) {
            super(executor);
        }
    }

    public DataStallRecoveryManager(Phone phone, DataNetworkController dataNetworkController, DataServiceManager dataServiceManager, Looper looper, DataStallRecoveryManagerCallback dataStallRecoveryManagerCallback) {
        super(looper);
        this.mLocalLog = new LocalLog(128);
        this.mRecoveryTriggered = false;
        this.mNetworkCheckTimerStarted = false;
        this.mPhone = phone;
        this.mLogTag = "DSRM-" + phone.getPhoneId();
        log("DataStallRecoveryManager created.");
        this.mDataNetworkController = dataNetworkController;
        this.mWwanDataServiceManager = dataServiceManager;
        this.mDataConfigManager = dataNetworkController.getDataConfigManager();
        dataNetworkController.getDataSettingsManager().registerCallback(new DataSettingsManager.DataSettingsManagerCallback(new DataStallRecoveryManager$$ExternalSyntheticLambda1(this)) { // from class: com.android.internal.telephony.data.DataStallRecoveryManager.1
            @Override // com.android.internal.telephony.data.DataSettingsManager.DataSettingsManagerCallback
            public void onDataEnabledChanged(boolean z, int i, String str) {
                DataStallRecoveryManager.this.onMobileDataEnabledChanged(z);
            }
        });
        this.mDataStallRecoveryManagerCallback = dataStallRecoveryManagerCallback;
        this.mRadioPowerState = phone.getRadioPowerState();
        updateDataStallRecoveryConfigs();
        registerAllEvents();
    }

    private void registerAllEvents() {
        this.mDataConfigManager.registerCallback(new DataConfigManager.DataConfigManagerCallback(new DataStallRecoveryManager$$ExternalSyntheticLambda1(this)) { // from class: com.android.internal.telephony.data.DataStallRecoveryManager.2
            @Override // com.android.internal.telephony.data.DataConfigManager.DataConfigManagerCallback
            public void onCarrierConfigChanged() {
                DataStallRecoveryManager.this.onCarrierConfigUpdated();
            }
        });
        this.mDataNetworkController.registerDataNetworkControllerCallback(new DataNetworkController.DataNetworkControllerCallback(new DataStallRecoveryManager$$ExternalSyntheticLambda1(this)) { // from class: com.android.internal.telephony.data.DataStallRecoveryManager.3
            @Override // com.android.internal.telephony.data.DataNetworkController.DataNetworkControllerCallback
            public void onInternetDataNetworkValidationStatusChanged(int i) {
                DataStallRecoveryManager.this.onInternetValidationStatusChanged(i);
            }

            @Override // com.android.internal.telephony.data.DataNetworkController.DataNetworkControllerCallback
            public void onInternetDataNetworkConnected(List<DataProfile> list) {
                DataStallRecoveryManager.this.mIsInternetNetworkConnected = true;
                DataStallRecoveryManager.this.logl("onInternetDataNetworkConnected");
            }

            @Override // com.android.internal.telephony.data.DataNetworkController.DataNetworkControllerCallback
            public void onInternetDataNetworkDisconnected() {
                DataStallRecoveryManager.this.mIsInternetNetworkConnected = false;
                DataStallRecoveryManager.this.logl("onInternetDataNetworkDisconnected");
            }
        });
        this.mPhone.mCi.registerForRadioStateChanged(this, 3, null);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        logv("handleMessage = " + message);
        int i = message.what;
        if (i == 2) {
            doRecovery();
        } else if (i == 3) {
            this.mRadioPowerState = this.mPhone.getRadioPowerState();
            if (this.mDataStalled) {
                this.mRadioStateChangedDuringDataStall = true;
                if (Settings.Global.getInt(this.mPhone.getContext().getContentResolver(), "airplane_mode_on", 0) != 0) {
                    this.mIsAirPlaneModeEnableDuringDataStall = true;
                }
            }
        } else {
            loge("Unexpected message = " + message);
        }
    }

    private void updateDataStallRecoveryConfigs() {
        this.mDataStallRecoveryDelayMillisArray = this.mDataConfigManager.getDataStallRecoveryDelayMillis();
        this.mSkipRecoveryActionArray = this.mDataConfigManager.getDataStallRecoveryShouldSkipArray();
    }

    private long getDataStallRecoveryDelayMillis(int i) {
        return this.mDataStallRecoveryDelayMillisArray[i];
    }

    private boolean shouldSkipRecoveryAction(int i) {
        return this.mSkipRecoveryActionArray[i];
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCarrierConfigUpdated() {
        updateDataStallRecoveryConfigs();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onMobileDataEnabledChanged(boolean z) {
        logl("onMobileDataEnabledChanged: DataEnabled:" + z + ",DataStalled:" + this.mDataStalled);
        if (this.mDataStalled && z) {
            this.mMobileDataChangedToEnabledDuringDataStall = true;
        }
    }

    private void reset() {
        this.mIsValidNetwork = true;
        this.mRecoveryTriggered = false;
        this.mIsAttemptedAllSteps = false;
        this.mRadioStateChangedDuringDataStall = false;
        this.mIsAirPlaneModeEnableDuringDataStall = false;
        this.mMobileDataChangedToEnabledDuringDataStall = false;
        cancelNetworkCheckTimer();
        this.mTimeLastRecoveryStartMs = 0L;
        this.mLastAction = 0;
        this.mRecoveryAction = 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onInternetValidationStatusChanged(int i) {
        logl("onInternetValidationStatusChanged: " + DataUtils.validationStatusToString(i));
        boolean z = i == 1;
        setNetworkValidationState(z);
        if (z) {
            reset();
        } else if (isRecoveryNeeded(true)) {
            this.mIsValidNetwork = false;
            log("trigger data stall recovery");
            this.mTimeLastRecoveryStartMs = SystemClock.elapsedRealtime();
            sendMessage(obtainMessage(2));
        }
    }

    private void resetAction() {
        this.mTimeLastRecoveryStartMs = 0L;
        this.mMobileDataChangedToEnabledDuringDataStall = false;
        this.mRadioStateChangedDuringDataStall = false;
        this.mIsAirPlaneModeEnableDuringDataStall = false;
        setRecoveryAction(0);
    }

    @VisibleForTesting
    public int getRecoveryAction() {
        log("getRecoveryAction: " + recoveryActionToString(this.mRecoveryAction));
        return this.mRecoveryAction;
    }

    @VisibleForTesting
    public void setRecoveryAction(int i) {
        this.mRecoveryAction = i;
        if (this.mMobileDataChangedToEnabledDuringDataStall && i < 3) {
            this.mRecoveryAction = 3;
        }
        if (this.mRadioStateChangedDuringDataStall && this.mRadioPowerState == 1) {
            this.mRecoveryAction = 4;
        }
        if (shouldSkipRecoveryAction(this.mRecoveryAction)) {
            int i2 = this.mRecoveryAction;
            if (i2 == 0) {
                setRecoveryAction(1);
            } else if (i2 == 1) {
                setRecoveryAction(3);
            } else if (i2 == 3) {
                setRecoveryAction(4);
            } else if (i2 == 4) {
                resetAction();
            }
        }
        log("setRecoveryAction: " + recoveryActionToString(this.mRecoveryAction));
    }

    private boolean isRecoveryAlreadyStarted() {
        return getRecoveryAction() != 0 || this.mRecoveryTriggered;
    }

    private long getElapsedTimeSinceRecoveryMs() {
        return SystemClock.elapsedRealtime() - this.mTimeLastRecoveryStartMs;
    }

    private void broadcastDataStallDetected(int i) {
        log("broadcastDataStallDetected recoveryAction: " + i);
        Intent intent = new Intent("android.intent.action.DATA_STALL_DETECTED");
        SubscriptionManager.putPhoneIdAndSubIdExtra(intent, this.mPhone.getPhoneId());
        intent.putExtra("recoveryAction", i);
        this.mPhone.getContext().sendBroadcast(intent);
    }

    private void getDataCallList() {
        log("getDataCallList: request data call list");
        this.mWwanDataServiceManager.requestDataCallList(null);
    }

    private void cleanUpDataNetwork() {
        log("cleanUpDataNetwork: notify clean up data network");
        this.mDataStallRecoveryManagerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataStallRecoveryManager$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DataStallRecoveryManager.this.lambda$cleanUpDataNetwork$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$cleanUpDataNetwork$0() {
        this.mDataStallRecoveryManagerCallback.onDataStallReestablishInternet();
    }

    private void powerOffRadio() {
        log("powerOffRadio: Restart radio");
        this.mPhone.getServiceStateTracker().powerOffRadioSafely();
    }

    private void rebootModem() {
        log("rebootModem: reboot modem");
        this.mPhone.rebootModem(null);
    }

    private void startNetworkCheckTimer(int i) {
        if (i == 4) {
            return;
        }
        log("startNetworkCheckTimer(): " + getDataStallRecoveryDelayMillis(i) + "ms");
        if (this.mNetworkCheckTimerStarted) {
            return;
        }
        this.mNetworkCheckTimerStarted = true;
        this.mTimeLastRecoveryStartMs = SystemClock.elapsedRealtime();
        sendMessageDelayed(obtainMessage(2), getDataStallRecoveryDelayMillis(i));
    }

    private void cancelNetworkCheckTimer() {
        log("cancelNetworkCheckTimer()");
        if (this.mNetworkCheckTimerStarted) {
            this.mNetworkCheckTimerStarted = false;
            removeMessages(2);
        }
    }

    private boolean isRecoveryNeeded(boolean z) {
        logv("enter: isRecoveryNeeded()");
        if (!this.mIsValidNetwork && !isRecoveryAlreadyStarted()) {
            logl("skip when network still remains invalid and recovery was not started yet");
            return false;
        } else if (this.mIsAttemptedAllSteps) {
            logl("skip retrying continue recovery action");
            return false;
        } else if (getElapsedTimeSinceRecoveryMs() < getDataStallRecoveryDelayMillis(this.mLastAction) && z) {
            logl("skip back to back data stall recovery");
            return false;
        } else if (this.mPhone.getState() != PhoneConstants.State.IDLE && getRecoveryAction() > 1) {
            logl("skip data stall recovery as there is an active call");
            return false;
        } else if (this.mPhone.getSignalStrength().getLevel() <= 1) {
            logl("skip data stall recovery as in poor signal condition");
            return false;
        } else if (!this.mDataNetworkController.isInternetDataAllowed()) {
            logl("skip data stall recovery as data not allowed.");
            return false;
        } else if (this.mIsInternetNetworkConnected) {
            return true;
        } else {
            logl("skip data stall recovery as data not connected");
            return false;
        }
    }

    private void setNetworkValidationState(boolean z) {
        int i;
        boolean z2;
        boolean z3;
        boolean z4;
        int i2;
        int recoveredReason = getRecoveredReason(z);
        if (!z || this.mDataStalled) {
            if (!this.mDataStalled) {
                this.mDataStalled = true;
                this.mDataStallStartMs = SystemClock.elapsedRealtime();
                logl("data stall: start time = " + DataUtils.elapsedTimeToString(this.mDataStallStartMs));
                i = 0;
                z3 = false;
                z2 = true;
                z4 = true;
            } else if (this.mLastActionReported) {
                i = 0;
                z2 = false;
                z3 = false;
                z4 = false;
            } else {
                i = (int) (SystemClock.elapsedRealtime() - this.mDataStallStartMs);
                this.mLastActionReported = true;
                z4 = false;
                z2 = true;
                z3 = true;
            }
            if (z) {
                this.mLastActionReported = false;
                this.mDataStalled = false;
                i2 = (int) (SystemClock.elapsedRealtime() - this.mDataStallStartMs);
                z2 = true;
            } else {
                i2 = i;
            }
            if (z2) {
                DataStallRecoveryStats.onDataStallEvent(this.mLastAction, this.mPhone, z, i2, recoveredReason, z3);
                StringBuilder sb = new StringBuilder();
                sb.append("data stall: ");
                sb.append(z4 ? "start" : !z ? "in process" : "end");
                sb.append(", lastaction=");
                sb.append(recoveryActionToString(this.mLastAction));
                sb.append(", isRecovered=");
                sb.append(z);
                sb.append(", reason=");
                sb.append(recoveredReasonToString(recoveredReason));
                sb.append(", isFirstValidationAfterDoRecovery=");
                sb.append(z3);
                sb.append(", TimeDuration=");
                sb.append(i2);
                logl(sb.toString());
            }
        }
    }

    private int getRecoveredReason(boolean z) {
        if (z) {
            if (this.mRadioStateChangedDuringDataStall) {
                int i = this.mLastAction;
                r1 = i <= 1 ? i <= 1 ? 2 : 1 : 1;
                if (this.mIsAirPlaneModeEnableDuringDataStall) {
                    return 3;
                }
            } else if (this.mMobileDataChangedToEnabledDuringDataStall) {
                return 3;
            }
            return r1;
        }
        return 0;
    }

    private void doRecovery() {
        int recoveryAction = getRecoveryAction();
        int level = this.mPhone.getSignalStrength().getLevel();
        this.mRecoveryTriggered = true;
        if (!isRecoveryNeeded(false)) {
            cancelNetworkCheckTimer();
            startNetworkCheckTimer(recoveryAction);
            return;
        }
        TelephonyMetrics.getInstance().writeSignalStrengthEvent(this.mPhone.getPhoneId(), level);
        TelephonyMetrics.getInstance().writeDataStallEvent(this.mPhone.getPhoneId(), recoveryAction);
        this.mLastAction = recoveryAction;
        this.mLastActionReported = false;
        broadcastDataStallDetected(recoveryAction);
        this.mNetworkCheckTimerStarted = false;
        if (recoveryAction == 0) {
            logl("doRecovery(): get data call list");
            getDataCallList();
            setRecoveryAction(1);
        } else if (recoveryAction == 1) {
            logl("doRecovery(): cleanup all connections");
            cleanUpDataNetwork();
            setRecoveryAction(3);
        } else if (recoveryAction == 3) {
            logl("doRecovery(): restarting radio");
            setRecoveryAction(4);
            powerOffRadio();
        } else if (recoveryAction == 4) {
            logl("doRecovery(): modem reset");
            rebootModem();
            resetAction();
            this.mIsAttemptedAllSteps = true;
        } else {
            throw new RuntimeException("doRecovery: Invalid recoveryAction = " + recoveryActionToString(recoveryAction));
        }
        startNetworkCheckTimer(this.mLastAction);
    }

    private static String recoveredReasonToString(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        return "Unknown(" + i + ")";
                    }
                    return "RECOVERED_REASON_USER";
                }
                return "RECOVERED_REASON_MODEM";
            }
            return "RECOVERED_REASON_DSRM";
        }
        return "RECOVERED_REASON_NONE";
    }

    private static String radioPowerStateToString(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    return "Unknown(" + i + ")";
                }
                return "RADIO_POWER_UNAVAILABLE";
            }
            return "RADIO_POWER_ON";
        }
        return "RADIO_POWER_OFF";
    }

    private static String recoveryActionToString(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 3) {
                    if (i != 4) {
                        return "Unknown(" + i + ")";
                    }
                    return "RECOVERY_ACTION_RESET_MODEM";
                }
                return "RECOVERY_ACTION_RADIO_RESTART";
            }
            return "RECOVERY_ACTION_CLEANUP";
        }
        return "RECOVERY_ACTION_GET_DATA_CALL_LIST";
    }

    private void log(String str) {
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
        AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter = new AndroidUtilIndentingPrintWriter(printWriter, "  ");
        androidUtilIndentingPrintWriter.println(DataStallRecoveryManager.class.getSimpleName() + "-" + this.mPhone.getPhoneId() + ":");
        androidUtilIndentingPrintWriter.increaseIndent();
        StringBuilder sb = new StringBuilder();
        sb.append("mIsValidNetwork=");
        sb.append(this.mIsValidNetwork);
        androidUtilIndentingPrintWriter.println(sb.toString());
        androidUtilIndentingPrintWriter.println("mIsInternetNetworkConnected=" + this.mIsInternetNetworkConnected);
        androidUtilIndentingPrintWriter.println("mIsAirPlaneModeEnableDuringDataStall=" + this.mIsAirPlaneModeEnableDuringDataStall);
        androidUtilIndentingPrintWriter.println("mDataStalled=" + this.mDataStalled);
        androidUtilIndentingPrintWriter.println("mLastAction=" + recoveryActionToString(this.mLastAction));
        androidUtilIndentingPrintWriter.println("mIsAttemptedAllSteps=" + this.mIsAttemptedAllSteps);
        androidUtilIndentingPrintWriter.println("mDataStallStartMs=" + DataUtils.elapsedTimeToString(this.mDataStallStartMs));
        androidUtilIndentingPrintWriter.println("mRadioPowerState=" + radioPowerStateToString(this.mRadioPowerState));
        androidUtilIndentingPrintWriter.println("mLastActionReported=" + this.mLastActionReported);
        androidUtilIndentingPrintWriter.println("mTimeLastRecoveryStartMs=" + DataUtils.elapsedTimeToString(this.mTimeLastRecoveryStartMs));
        androidUtilIndentingPrintWriter.println("getRecoveryAction()=" + recoveryActionToString(getRecoveryAction()));
        androidUtilIndentingPrintWriter.println("mRadioStateChangedDuringDataStall=" + this.mRadioStateChangedDuringDataStall);
        androidUtilIndentingPrintWriter.println("mMobileDataChangedToEnabledDuringDataStall=" + this.mMobileDataChangedToEnabledDuringDataStall);
        androidUtilIndentingPrintWriter.println("DataStallRecoveryDelayMillisArray=" + Arrays.toString(this.mDataStallRecoveryDelayMillisArray));
        androidUtilIndentingPrintWriter.println("SkipRecoveryActionArray=" + Arrays.toString(this.mSkipRecoveryActionArray));
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println(PhoneConfigurationManager.SSSS);
        androidUtilIndentingPrintWriter.println("Local logs:");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mLocalLog.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.decreaseIndent();
    }
}
