package com.android.internal.telephony;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.telephony.Rlog;
import java.io.PrintWriter;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class CallWaitingController extends Handler {
    @VisibleForTesting
    public static final String KEY_CS_SYNC = "cs_sync";
    @VisibleForTesting
    public static final String KEY_STATE = "state";
    @VisibleForTesting
    public static final String KEY_SUB_ID = "subId";
    public static final String LOG_TAG = "CallWaitingCtrl";
    @VisibleForTesting
    public static final String PREFERENCE_TBCW = "terminal_based_call_waiting";
    public static final int TERMINAL_BASED_ACTIVATED = 1;
    public static final int TERMINAL_BASED_NOT_ACTIVATED = 0;
    public static final int TERMINAL_BASED_NOT_SUPPORTED = -1;
    private final Context mContext;
    private final GsmCdmaPhone mPhone;
    private final ServiceStateTracker mSST;
    private final CarrierConfigManager.CarrierConfigChangeListener mCarrierConfigChangeListener = new CarrierConfigManager.CarrierConfigChangeListener() { // from class: com.android.internal.telephony.CallWaitingController$$ExternalSyntheticLambda1
        public final void onCarrierConfigChanged(int i, int i2, int i3, int i4) {
            CallWaitingController.this.lambda$new$0(i, i2, i3, i4);
        }
    };
    private boolean mSupportedByImsService = false;
    private boolean mValidSubscription = false;
    private int mCallWaitingState = -1;
    private int mSyncPreference = 0;
    private int mLastSubId = -1;
    private boolean mCsEnabled = false;
    private boolean mRegisteredForNetworkAttach = false;
    private boolean mImsRegistered = false;

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: com.android.internal.telephony.CallWaitingController$Cw */
    /* loaded from: classes.dex */
    public static class C0001Cw {
        final boolean mEnable;
        final boolean mImsRegistered;
        final Message mOnComplete;

        C0001Cw(boolean z, boolean z2, Message message) {
            this.mEnable = z;
            this.mOnComplete = message;
            this.mImsRegistered = z2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, int i2, int i3, int i4) {
        onCarrierConfigurationChanged(i);
    }

    public CallWaitingController(GsmCdmaPhone gsmCdmaPhone) {
        this.mPhone = gsmCdmaPhone;
        this.mSST = gsmCdmaPhone.getServiceStateTracker();
        this.mContext = gsmCdmaPhone.getContext();
    }

    private void initialize() {
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mContext.getSystemService(CarrierConfigManager.class);
        if (carrierConfigManager != null) {
            carrierConfigManager.registerCarrierConfigChangeListener(new Executor() { // from class: com.android.internal.telephony.CallWaitingController$$ExternalSyntheticLambda0
                @Override // java.util.concurrent.Executor
                public final void execute(Runnable runnable) {
                    CallWaitingController.this.post(runnable);
                }
            }, this.mCarrierConfigChangeListener);
        } else {
            loge("CarrierConfigLoader is not available.");
        }
        int phoneId = this.mPhone.getPhoneId();
        int subId = this.mPhone.getSubId();
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences(PREFERENCE_TBCW, 0);
        this.mLastSubId = sharedPreferences.getInt(KEY_SUB_ID + phoneId, -1);
        this.mCallWaitingState = sharedPreferences.getInt(KEY_STATE + subId, -1);
        this.mSyncPreference = sharedPreferences.getInt(KEY_CS_SYNC + phoneId, 0);
        logi("initialize phoneId=" + phoneId + ", lastSubId=" + this.mLastSubId + ", subId=" + subId + ", state=" + this.mCallWaitingState + ", sync=" + this.mSyncPreference + ", csEnabled=" + this.mCsEnabled);
    }

    @VisibleForTesting
    public synchronized int getTerminalBasedCallWaitingState(boolean z) {
        if (z) {
            if (!this.mImsRegistered && this.mSyncPreference == 4) {
                return -1;
            }
        }
        if (this.mValidSubscription) {
            return this.mCallWaitingState;
        }
        return -1;
    }

    @VisibleForTesting
    public synchronized boolean getCallWaiting(Message message) {
        if (this.mCallWaitingState == -1) {
            return false;
        }
        logi("getCallWaiting " + this.mCallWaitingState);
        if (this.mSyncPreference == 3 && !this.mCsEnabled && (isCircuitSwitchedNetworkAvailable() || !isImsRegistered())) {
            this.mPhone.mCi.queryCallWaiting(0, obtainMessage(2, 0, 0, new C0001Cw(false, isImsRegistered(), message)));
            return true;
        }
        int i = this.mSyncPreference;
        if (i != 0 && i != 3 && i != 2 && !isSyncImsOnly()) {
            int i2 = this.mSyncPreference;
            if (i2 == 1 || i2 == 4) {
                this.mPhone.mCi.queryCallWaiting(0, obtainMessage(2, 0, 0, new C0001Cw(false, isImsRegistered(), message)));
                return true;
            }
            return false;
        }
        sendGetCallWaitingResponse(message);
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:45:0x0090, code lost:
        r2 = 1;
     */
    @VisibleForTesting
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public synchronized boolean setCallWaiting(boolean z, int i, Message message) {
        int i2 = 0;
        if (this.mCallWaitingState == -1) {
            return false;
        }
        if ((i & 1) != 1) {
            return false;
        }
        logi("setCallWaiting enable=" + z + ", service=" + i);
        if (this.mSyncPreference == 3 && !this.mCsEnabled && z) {
            if (!isCircuitSwitchedNetworkAvailable() && isImsRegistered()) {
                registerForNetworkAttached();
            }
            this.mPhone.mCi.setCallWaiting(true, i, obtainMessage(1, 0, 0, new C0001Cw(true, isImsRegistered(), message)));
            return true;
        }
        int i3 = this.mSyncPreference;
        if (i3 != 0 && i3 != 3 && i3 != 2 && !isSyncImsOnly()) {
            int i4 = this.mSyncPreference;
            if (i4 == 1 || i4 == 4) {
                this.mPhone.mCi.setCallWaiting(z, i, obtainMessage(1, 0, 0, new C0001Cw(z, isImsRegistered(), message)));
                return true;
            }
            return false;
        }
        updateState(i2);
        sendToTarget(message, null, null);
        return true;
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 1) {
            onSetCallWaitingDone((AsyncResult) message.obj);
        } else if (i == 2) {
            onGetCallWaitingDone((AsyncResult) message.obj);
        } else if (i != 3) {
        } else {
            onRegisteredToNetwork();
        }
    }

    private synchronized void onSetCallWaitingDone(AsyncResult asyncResult) {
        Object obj = asyncResult.userObj;
        int i = 1;
        if (obj == null) {
            if (asyncResult.exception == null) {
                updateSyncState(true);
            } else {
                loge("onSetCallWaitingDone e=" + asyncResult.exception);
            }
        } else if (obj instanceof C0001Cw) {
            C0001Cw c0001Cw = (C0001Cw) obj;
            int i2 = this.mSyncPreference;
            if (i2 == 4) {
                sendToTarget(c0001Cw.mOnComplete, asyncResult.result, asyncResult.exception);
                return;
            }
            if (asyncResult.exception == null) {
                if (i2 == 3) {
                    updateSyncState(true);
                }
                if (!c0001Cw.mEnable) {
                    i = 0;
                }
                updateState(i);
            } else if (i2 == 3 && c0001Cw.mImsRegistered) {
                updateState(1);
                sendToTarget(c0001Cw.mOnComplete, null, null);
                return;
            }
            sendToTarget(c0001Cw.mOnComplete, asyncResult.result, asyncResult.exception);
        }
    }

    private synchronized void onGetCallWaitingDone(AsyncResult asyncResult) {
        Object obj = asyncResult.userObj;
        if (obj == null) {
            if (asyncResult.exception == null) {
                int[] iArr = (int[]) asyncResult.result;
                if (iArr != null && iArr.length > 1) {
                    if (iArr[0] == 1 && (iArr[1] & 1) == 1) {
                        r1 = 1;
                    }
                } else {
                    loge("onGetCallWaitingDone unexpected response");
                }
            } else {
                loge("onGetCallWaitingDone e=" + asyncResult.exception);
            }
            if (r1 != 0) {
                updateSyncState(true);
            } else {
                logi("onGetCallWaitingDone enabling CW service in CS network");
                this.mPhone.mCi.setCallWaiting(true, 1, obtainMessage(1));
            }
            unregisterForNetworkAttached();
        } else if (obj instanceof C0001Cw) {
            C0001Cw c0001Cw = (C0001Cw) obj;
            int i = this.mSyncPreference;
            if (i == 4) {
                sendToTarget(c0001Cw.mOnComplete, asyncResult.result, asyncResult.exception);
                return;
            }
            if (asyncResult.exception == null) {
                int[] iArr2 = (int[]) asyncResult.result;
                if (iArr2 != null && iArr2.length >= 2) {
                    boolean z = iArr2[0] == 1 && (iArr2[1] & 1) == 1;
                    if (i == 3) {
                        updateSyncState(z);
                        if (!z && !c0001Cw.mImsRegistered) {
                            logi("onGetCallWaitingDone CW in CS network is disabled.");
                            updateState(0);
                        }
                        sendGetCallWaitingResponse(c0001Cw.mOnComplete);
                        return;
                    }
                    updateState(z ? 1 : 0);
                }
                logi("onGetCallWaitingDone unexpected response");
                if (this.mSyncPreference == 3) {
                    sendGetCallWaitingResponse(c0001Cw.mOnComplete);
                } else {
                    sendToTarget(c0001Cw.mOnComplete, asyncResult.result, asyncResult.exception);
                }
                return;
            } else if (i == 3 && c0001Cw.mImsRegistered) {
                logi("onGetCallWaitingDone get an exception, but IMS is registered");
                sendGetCallWaitingResponse(c0001Cw.mOnComplete);
                return;
            }
            sendToTarget(c0001Cw.mOnComplete, asyncResult.result, asyncResult.exception);
        }
    }

    private void sendToTarget(Message message, Object obj, Throwable th) {
        if (message != null) {
            AsyncResult.forMessage(message, obj, th);
            message.sendToTarget();
        }
    }

    private void sendGetCallWaitingResponse(Message message) {
        if (message != null) {
            int i = this.mCallWaitingState;
            sendToTarget(message, new int[]{i, i != 1 ? 0 : 1}, null);
        }
    }

    private synchronized void onRegisteredToNetwork() {
        if (this.mCsEnabled) {
            return;
        }
        this.mPhone.mCi.queryCallWaiting(0, obtainMessage(2));
    }

    private synchronized void onCarrierConfigurationChanged(int i) {
        if (i != this.mPhone.getPhoneId()) {
            return;
        }
        int subId = this.mPhone.getSubId();
        if (!SubscriptionManager.isValidSubscriptionId(subId)) {
            logi("onCarrierConfigChanged invalid subId=" + subId);
            this.mValidSubscription = false;
            unregisterForNetworkAttached();
        } else if (updateCarrierConfig(subId, false)) {
            logi("onCarrierConfigChanged cs_enabled=" + this.mCsEnabled);
            if (this.mSyncPreference == 2 && !this.mCsEnabled) {
                registerForNetworkAttached();
            }
        }
    }

    @VisibleForTesting
    public boolean updateCarrierConfig(int i, boolean z) {
        boolean z2;
        this.mValidSubscription = true;
        PersistableBundle carrierConfigSubset = CarrierConfigManager.getCarrierConfigSubset(this.mContext, i, new String[]{"imsss.ut_terminal_based_services_int_array", "imsss.terminal_based_call_waiting_sync_type_int", "imsss.terminal_based_call_waiting_default_enabled_bool"});
        int i2 = 0;
        if (carrierConfigSubset.isEmpty()) {
            return false;
        }
        int[] intArray = carrierConfigSubset.getIntArray("imsss.ut_terminal_based_services_int_array");
        if (intArray != null) {
            z2 = false;
            for (int i3 : intArray) {
                if (i3 == 0) {
                    z2 = true;
                }
            }
        } else {
            z2 = false;
        }
        int i4 = carrierConfigSubset.getInt("imsss.terminal_based_call_waiting_sync_type_int", 3);
        boolean z3 = carrierConfigSubset.getBoolean("imsss.terminal_based_call_waiting_default_enabled_bool");
        int i5 = -1;
        if (!z2) {
            i2 = -1;
        } else if (z3) {
            i2 = 1;
        }
        int savedState = getSavedState(i);
        if (!z && (this.mLastSubId == i || (i4 != 2 && i4 != 3))) {
            if (i2 != -1) {
                if (savedState != -1) {
                    i5 = savedState;
                }
            }
            updateState(i5, i4, z);
            return true;
        }
        i5 = i2;
        updateState(i5, i4, z);
        return true;
    }

    private void updateState(int i) {
        updateState(i, this.mSyncPreference, false);
    }

    private void updateState(int i, int i2, boolean z) {
        int subId = this.mPhone.getSubId();
        if (this.mLastSubId == subId && this.mCallWaitingState == i && this.mSyncPreference == i2 && !z) {
            return;
        }
        int phoneId = this.mPhone.getPhoneId();
        logi("updateState phoneId=" + phoneId + ", subId=" + subId + ", state=" + i + ", sync=" + i2 + ", ignoreSavedState=" + z);
        SharedPreferences.Editor edit = this.mContext.getSharedPreferences(PREFERENCE_TBCW, 0).edit();
        StringBuilder sb = new StringBuilder();
        sb.append(KEY_SUB_ID);
        sb.append(phoneId);
        edit.putInt(sb.toString(), subId);
        StringBuilder sb2 = new StringBuilder();
        sb2.append(KEY_STATE);
        sb2.append(subId);
        edit.putInt(sb2.toString(), i);
        edit.putInt(KEY_CS_SYNC + phoneId, i2);
        edit.apply();
        this.mCallWaitingState = i;
        this.mLastSubId = subId;
        this.mSyncPreference = i2;
        this.mPhone.setTerminalBasedCallWaitingStatus(i);
    }

    private int getSavedState(int i) {
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences(PREFERENCE_TBCW, 0);
        int i2 = sharedPreferences.getInt(KEY_STATE + i, -1);
        logi("getSavedState subId=" + i + ", state=" + i2);
        return i2;
    }

    private void updateSyncState(boolean z) {
        int phoneId = this.mPhone.getPhoneId();
        logi("updateSyncState phoneId=" + phoneId + ", enabled=" + z);
        this.mCsEnabled = z;
    }

    @VisibleForTesting
    public boolean getSyncState() {
        return this.mCsEnabled;
    }

    private boolean isCircuitSwitchedNetworkAvailable() {
        StringBuilder sb = new StringBuilder();
        sb.append("isCircuitSwitchedNetworkAvailable=");
        sb.append(this.mSST.getServiceState().getState() == 0);
        logi(sb.toString());
        return this.mSST.getServiceState().getState() == 0;
    }

    private boolean isImsRegistered() {
        logi("isImsRegistered " + this.mImsRegistered);
        return this.mImsRegistered;
    }

    public synchronized void setImsRegistrationState(boolean z) {
        logi("setImsRegistrationState prev=" + this.mImsRegistered + ", new=" + z);
        this.mImsRegistered = z;
    }

    private void registerForNetworkAttached() {
        logi("registerForNetworkAttached");
        if (this.mRegisteredForNetworkAttach) {
            return;
        }
        this.mSST.registerForNetworkAttached(this, 3, null);
        this.mRegisteredForNetworkAttach = true;
    }

    private void unregisterForNetworkAttached() {
        logi("unregisterForNetworkAttached");
        if (this.mRegisteredForNetworkAttach) {
            this.mSST.unregisterForNetworkAttached(this);
            removeMessages(3);
            this.mRegisteredForNetworkAttach = false;
        }
    }

    @VisibleForTesting
    public synchronized void setTerminalBasedCallWaitingSupported(boolean z) {
        CarrierConfigManager.CarrierConfigChangeListener carrierConfigChangeListener;
        if (this.mSupportedByImsService == z) {
            return;
        }
        logi("setTerminalBasedCallWaitingSupported " + z);
        this.mSupportedByImsService = z;
        if (z) {
            initialize();
            onCarrierConfigurationChanged(this.mPhone.getPhoneId());
        } else {
            CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mContext.getSystemService(CarrierConfigManager.class);
            if (carrierConfigManager != null && (carrierConfigChangeListener = this.mCarrierConfigChangeListener) != null) {
                carrierConfigManager.unregisterCarrierConfigChangeListener(carrierConfigChangeListener);
            }
            updateState(-1);
        }
    }

    @VisibleForTesting
    public void notifyRegisteredToNetwork() {
        sendEmptyMessage(3);
    }

    private boolean isSyncImsOnly() {
        return this.mSyncPreference == 4 && this.mImsRegistered;
    }

    public void dump(PrintWriter printWriter) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("CallWaitingController:");
        indentingPrintWriter.println(" mSupportedByImsService=" + this.mSupportedByImsService);
        indentingPrintWriter.println(" mValidSubscription=" + this.mValidSubscription);
        indentingPrintWriter.println(" mCallWaitingState=" + this.mCallWaitingState);
        indentingPrintWriter.println(" mSyncPreference=" + this.mSyncPreference);
        indentingPrintWriter.println(" mLastSubId=" + this.mLastSubId);
        indentingPrintWriter.println(" mCsEnabled=" + this.mCsEnabled);
        indentingPrintWriter.println(" mRegisteredForNetworkAttach=" + this.mRegisteredForNetworkAttach);
        indentingPrintWriter.println(" mImsRegistered=" + this.mImsRegistered);
        indentingPrintWriter.decreaseIndent();
    }

    private void loge(String str) {
        Rlog.e(LOG_TAG, "[" + this.mPhone.getPhoneId() + "] " + str);
    }

    private void logi(String str) {
        Rlog.i(LOG_TAG, "[" + this.mPhone.getPhoneId() + "] " + str);
    }
}
