package com.android.internal.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.internal.telephony.sysprop.TelephonyProperties;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.telephony.CellInfo;
import android.telephony.ServiceState;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.MccTable;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class LocaleTracker extends Handler {
    private final BroadcastReceiver mBroadcastReceiver;
    private List<CellInfo> mCellInfoList;
    private String mCountryOverride;
    private String mCurrentCountryIso;
    private int mFailCellInfoCount;
    private boolean mIsTracking;
    private int mLastServiceState;
    private final LocalLog mLocalLog;
    private final NitzStateMachine mNitzStateMachine;
    private String mOperatorNumeric;
    private final Phone mPhone;
    private int mSimState;
    private String mTag;

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        boolean z = true;
        switch (message.what) {
            case 1:
                this.mPhone.requestCellInfoUpdate(null, obtainMessage(5));
                return;
            case 2:
                onServiceStateChanged((ServiceState) ((AsyncResult) message.obj).result);
                return;
            case 3:
                onSimCardStateChanged(message.arg1);
                return;
            case 4:
                processCellInfo((AsyncResult) message.obj);
                List<CellInfo> list = this.mCellInfoList;
                if (list == null || list.size() <= 0) {
                    return;
                }
                requestNextCellInfo(true);
                return;
            case 5:
                processCellInfo((AsyncResult) message.obj);
                List<CellInfo> list2 = this.mCellInfoList;
                requestNextCellInfo((list2 == null || list2.size() <= 0) ? false : false);
                return;
            case 6:
                updateOperatorNumericImmediate(PhoneConfigurationManager.SSSS);
                updateTrackingStatus();
                return;
            case 7:
                this.mCountryOverride = (String) message.obj;
                updateLocale();
                return;
            default:
                throw new IllegalStateException("Unexpected message arrives. msg = " + message.what);
        }
    }

    public LocaleTracker(Phone phone, NitzStateMachine nitzStateMachine, Looper looper) {
        super(looper);
        this.mLastServiceState = 3;
        this.mIsTracking = false;
        this.mLocalLog = new LocalLog(32, false);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.LocaleTracker.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if ("android.telephony.action.SIM_CARD_STATE_CHANGED".equals(intent.getAction())) {
                    if (intent.getIntExtra("phone", 0) == LocaleTracker.this.mPhone.getPhoneId()) {
                        LocaleTracker.this.obtainMessage(3, intent.getIntExtra("android.telephony.extra.SIM_STATE", 0), 0).sendToTarget();
                    }
                } else if ("com.android.internal.telephony.action.COUNTRY_OVERRIDE".equals(intent.getAction())) {
                    String stringExtra = intent.getStringExtra("country");
                    if (intent.getBooleanExtra("reset", false)) {
                        stringExtra = null;
                    }
                    LocaleTracker localeTracker = LocaleTracker.this;
                    localeTracker.log("Received country override: " + stringExtra);
                    LocaleTracker.this.obtainMessage(7, stringExtra).sendToTarget();
                }
            }
        };
        this.mBroadcastReceiver = broadcastReceiver;
        this.mPhone = phone;
        this.mNitzStateMachine = nitzStateMachine;
        this.mSimState = 0;
        this.mTag = LocaleTracker.class.getSimpleName() + "-" + phone.getPhoneId();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.telephony.action.SIM_CARD_STATE_CHANGED");
        if (TelephonyUtils.IS_DEBUGGABLE) {
            intentFilter.addAction("com.android.internal.telephony.action.COUNTRY_OVERRIDE");
        }
        phone.getContext().registerReceiver(broadcastReceiver, intentFilter);
        phone.registerForServiceStateChanged(this, 2, null);
        phone.registerForCellInfo(this, 4, null);
    }

    public String getCurrentCountry() {
        String str = this.mCurrentCountryIso;
        return str != null ? str : PhoneConfigurationManager.SSSS;
    }

    private String getMccFromCellInfo() {
        String str = null;
        if (this.mCellInfoList != null) {
            HashMap hashMap = new HashMap();
            int i = 0;
            for (CellInfo cellInfo : this.mCellInfoList) {
                String mccString = cellInfo.getCellIdentity().getMccString();
                if (mccString != null) {
                    int intValue = hashMap.containsKey(mccString) ? 1 + ((Integer) hashMap.get(mccString)).intValue() : 1;
                    hashMap.put(mccString, Integer.valueOf(intValue));
                    if (intValue > i) {
                        str = mccString;
                        i = intValue;
                    }
                }
            }
        }
        return str;
    }

    private MccTable.MccMnc getMccMncFromCellInfo(String str) {
        MccTable.MccMnc mccMnc = null;
        if (this.mCellInfoList != null) {
            HashMap hashMap = new HashMap();
            int i = 0;
            for (CellInfo cellInfo : this.mCellInfoList) {
                String mccString = cellInfo.getCellIdentity().getMccString();
                if (Objects.equals(mccString, str)) {
                    MccTable.MccMnc mccMnc2 = new MccTable.MccMnc(mccString, cellInfo.getCellIdentity().getMncString());
                    int intValue = hashMap.containsKey(mccMnc2) ? 1 + ((Integer) hashMap.get(mccMnc2)).intValue() : 1;
                    hashMap.put(mccMnc2, Integer.valueOf(intValue));
                    if (intValue > i) {
                        i = intValue;
                        mccMnc = mccMnc2;
                    }
                }
            }
        }
        return mccMnc;
    }

    private void onSimCardStateChanged(int i) {
        this.mSimState = i;
        updateLocale();
        updateTrackingStatus();
    }

    private void onServiceStateChanged(ServiceState serviceState) {
        this.mLastServiceState = serviceState.getState();
        updateLocale();
        updateTrackingStatus();
    }

    public void updateOperatorNumeric(String str) {
        if (TextUtils.isEmpty(str)) {
            sendMessageDelayed(obtainMessage(6), 600000L);
            return;
        }
        removeMessages(6);
        updateOperatorNumericImmediate(str);
    }

    private void updateOperatorNumericImmediate(String str) {
        if (str.equals(this.mOperatorNumeric)) {
            return;
        }
        String str2 = "Operator numeric changes to \"" + str + "\"";
        log(str2);
        this.mLocalLog.log(str2);
        this.mOperatorNumeric = str;
        updateLocale();
    }

    private void processCellInfo(AsyncResult asyncResult) {
        if (asyncResult == null || asyncResult.exception != null) {
            this.mCellInfoList = null;
            return;
        }
        List<CellInfo> list = (List) asyncResult.result;
        log("processCellInfo: cell info=" + list);
        this.mCellInfoList = list;
        updateLocale();
    }

    private void requestNextCellInfo(boolean z) {
        if (this.mIsTracking) {
            removeMessages(1);
            if (z) {
                resetCellInfoRetry();
                removeMessages(4);
                removeMessages(5);
                sendMessageDelayed(obtainMessage(1), 600000L);
                return;
            }
            int i = this.mFailCellInfoCount + 1;
            this.mFailCellInfoCount = i;
            long cellInfoDelayTime = getCellInfoDelayTime(i);
            log("Can't get cell info. Try again in " + (cellInfoDelayTime / 1000) + " secs.");
            sendMessageDelayed(obtainMessage(1), cellInfoDelayTime);
        }
    }

    @VisibleForTesting
    public static long getCellInfoDelayTime(int i) {
        return Math.min(Math.max(((long) Math.pow(2.0d, Math.min(i, 30) - 1)) * 2000, 2000L), 600000L);
    }

    private void resetCellInfoRetry() {
        this.mFailCellInfoCount = 0;
        removeMessages(1);
    }

    private void updateTrackingStatus() {
        int i;
        boolean z = true;
        if ((this.mSimState != 1 && !TextUtils.isEmpty(this.mOperatorNumeric)) || ((i = this.mLastServiceState) != 1 && i != 2)) {
            z = false;
        }
        if (z) {
            startTracking();
        } else {
            stopTracking();
        }
    }

    private void stopTracking() {
        if (this.mIsTracking) {
            this.mIsTracking = false;
            log("Stopping LocaleTracker");
            this.mLocalLog.log("Stopping LocaleTracker");
            this.mCellInfoList = null;
            resetCellInfoRetry();
        }
    }

    private void startTracking() {
        if (this.mIsTracking) {
            return;
        }
        this.mLocalLog.log("Starting LocaleTracker");
        log("Starting LocaleTracker");
        this.mIsTracking = true;
        sendMessage(obtainMessage(1));
    }

    private synchronized void updateLocale() {
        boolean z;
        String mccFromCellInfo;
        String str = PhoneConfigurationManager.SSSS;
        String str2 = "empty as default";
        if (!TextUtils.isEmpty(this.mOperatorNumeric)) {
            MccTable.MccMnc fromOperatorNumeric = MccTable.MccMnc.fromOperatorNumeric(this.mOperatorNumeric);
            if (fromOperatorNumeric != null) {
                str = MccTable.geoCountryCodeForMccMnc(fromOperatorNumeric);
                str2 = "OperatorNumeric(" + this.mOperatorNumeric + "): MccTable.geoCountryCodeForMccMnc(\"" + fromOperatorNumeric + "\")";
            } else {
                loge("updateLocale: Can't get country from operator numeric. mOperatorNumeric = " + this.mOperatorNumeric);
            }
        }
        if (TextUtils.isEmpty(str) && (mccFromCellInfo = getMccFromCellInfo()) != null) {
            str = MccTable.countryCodeForMcc(mccFromCellInfo);
            str2 = "CellInfo: MccTable.countryCodeForMcc(\"" + mccFromCellInfo + "\")";
            MccTable.MccMnc mccMncFromCellInfo = getMccMncFromCellInfo(mccFromCellInfo);
            if (mccMncFromCellInfo != null) {
                str = MccTable.geoCountryCodeForMccMnc(mccMncFromCellInfo);
                str2 = "CellInfo: MccTable.geoCountryCodeForMccMnc(" + mccMncFromCellInfo + ")";
            }
        }
        String str3 = this.mCountryOverride;
        if (str3 != null) {
            str2 = "mCountryOverride = \"" + this.mCountryOverride + "\"";
            str = str3;
        }
        if (!this.mPhone.isRadioOn()) {
            str = PhoneConfigurationManager.SSSS;
            str2 = "radio off";
        }
        log("updateLocale: countryIso = " + str + ", countryIsoDebugInfo = " + str2);
        if (!Objects.equals(str, this.mCurrentCountryIso)) {
            String str4 = "updateLocale: Change the current country to \"" + str + "\", countryIsoDebugInfo = " + str2 + ", mCellInfoList = " + this.mCellInfoList;
            log(str4);
            this.mLocalLog.log(str4);
            this.mCurrentCountryIso = str;
            if (!TextUtils.isEmpty(str)) {
                updateLastKnownCountryIso(this.mCurrentCountryIso);
            }
            int phoneId = this.mPhone.getPhoneId();
            if (SubscriptionManager.isValidPhoneId(phoneId)) {
                ArrayList arrayList = new ArrayList(TelephonyProperties.operator_iso_country());
                while (arrayList.size() <= phoneId) {
                    arrayList.add(null);
                }
                arrayList.set(phoneId, this.mCurrentCountryIso);
                TelephonyProperties.operator_iso_country(arrayList);
            }
            Intent intent = new Intent("android.telephony.action.NETWORK_COUNTRY_CHANGED");
            intent.putExtra("android.telephony.extra.NETWORK_COUNTRY", str);
            intent.putExtra("android.telephony.extra.LAST_KNOWN_NETWORK_COUNTRY", getLastKnownCountryIso());
            SubscriptionManager.putPhoneIdAndSubIdExtra(intent, this.mPhone.getPhoneId());
            this.mPhone.getContext().sendBroadcastAsUser(intent, UserHandle.ALL);
        }
        if (TextUtils.isEmpty(this.mOperatorNumeric) || !this.mOperatorNumeric.startsWith("001")) {
            z = false;
        } else {
            str = PhoneConfigurationManager.SSSS;
            str2 = "Test cell: " + this.mOperatorNumeric;
            z = true;
        }
        log("updateLocale: timeZoneCountryIso = " + str + ", timeZoneCountryIsoDebugInfo = " + str2);
        if (TextUtils.isEmpty(str) && !z) {
            this.mNitzStateMachine.handleCountryUnavailable();
        } else {
            this.mNitzStateMachine.handleCountryDetected(str);
        }
    }

    public boolean isTracking() {
        return this.mIsTracking;
    }

    private void updateLastKnownCountryIso(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        SharedPreferences.Editor edit = this.mPhone.getContext().getSharedPreferences("last_known_country_iso", 0).edit();
        edit.putString("last_known_country_iso", str);
        edit.commit();
        log("update country iso in sharedPrefs " + str);
    }

    public String getLastKnownCountryIso() {
        return this.mPhone.getContext().getSharedPreferences("last_known_country_iso", 0).getString("last_known_country_iso", PhoneConfigurationManager.SSSS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String str) {
        Rlog.d(this.mTag, str);
    }

    private void loge(String str) {
        Rlog.e(this.mTag, str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        printWriter.println("LocaleTracker-" + this.mPhone.getPhoneId() + ":");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("mIsTracking = " + this.mIsTracking);
        indentingPrintWriter.println("mOperatorNumeric = " + this.mOperatorNumeric);
        indentingPrintWriter.println("mSimState = " + this.mSimState);
        indentingPrintWriter.println("mCellInfoList = " + this.mCellInfoList);
        indentingPrintWriter.println("mCurrentCountryIso = " + this.mCurrentCountryIso);
        indentingPrintWriter.println("mFailCellInfoCount = " + this.mFailCellInfoCount);
        indentingPrintWriter.println("Local logs:");
        indentingPrintWriter.increaseIndent();
        this.mLocalLog.dump(fileDescriptor, indentingPrintWriter, strArr);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.flush();
    }

    @VisibleForTesting
    public String getCountryOverride() {
        return this.mCountryOverride;
    }
}
