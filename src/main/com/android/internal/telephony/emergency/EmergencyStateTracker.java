package com.android.internal.telephony.emergency;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.internal.telephony.sysprop.TelephonyProperties;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.telephony.CarrierConfigManager;
import android.telephony.EmergencyRegResult;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.emergency.EmergencyNumber;
import android.util.ArraySet;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.Call;
import com.android.internal.telephony.GsmCdmaPhone;
import com.android.internal.telephony.NetworkTypeController$$ExternalSyntheticLambda0;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.data.PhoneSwitcher;
import com.android.internal.telephony.emergency.RadioOnStateListener;
import com.android.telephony.Rlog;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class EmergencyStateTracker {
    public static final int EMERGENCY_TYPE_CALL = 1;
    public static final int EMERGENCY_TYPE_SMS = 2;
    private static EmergencyStateTracker INSTANCE = null;
    @VisibleForTesting
    public static final int MSG_EXIT_EMERGENCY_MODE_DONE = 2;
    @VisibleForTesting
    public static final int MSG_SET_EMERGENCY_CALLBACK_MODE_DONE = 3;
    @VisibleForTesting
    public static final int MSG_SET_EMERGENCY_MODE_DONE = 1;
    private CompletableFuture<Integer> mCallEmergencyModeFuture;
    private final CarrierConfigManager mConfigManager;
    private final Context mContext;
    private final BroadcastReceiver mEcmExitReceiver;
    private final long mEcmExitTimeoutMs;
    private final Handler mHandler;
    private boolean mIsEmergencyCallStartedDuringEmergencySms;
    private boolean mIsEmergencyModeInProgress;
    private boolean mIsInEcm;
    private boolean mIsInEmergencyCall;
    private final boolean mIsSuplDdsSwitchRequiredForEmergencyCall;
    private boolean mIsTestEmergencyNumber;
    private boolean mIsTestEmergencyNumberForSms;
    private EmergencyRegResult mLastEmergencyRegResult;
    private Runnable mOnEcmExitCompleteRunnable;
    private String mOngoingCallId;
    private Phone mPhone;
    private PhoneFactoryProxy mPhoneFactoryProxy;
    private PhoneSwitcherProxy mPhoneSwitcherProxy;
    private RadioOnHelper mRadioOnHelper;
    private CompletableFuture<Integer> mSmsEmergencyModeFuture;
    private Phone mSmsPhone;
    private final TelephonyManagerProxy mTelephonyManagerProxy;
    private final PowerManager.WakeLock mWakeLock;
    private boolean mWasEmergencyModeSetOnModem;
    private int mEmergencyMode = 0;
    private final Runnable mExitEcmRunnable = new Runnable() { // from class: com.android.internal.telephony.emergency.EmergencyStateTracker$$ExternalSyntheticLambda1
        @Override // java.lang.Runnable
        public final void run() {
            EmergencyStateTracker.this.exitEmergencyCallbackMode();
        }
    };
    private final Set<String> mActiveEmergencyCalls = new ArraySet();
    private int mEmergencyCallDomain = 0;
    private final Set<String> mOngoingEmergencySmsIds = new ArraySet();

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface PhoneFactoryProxy {
        Phone[] getPhones();
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface PhoneSwitcherProxy {
        PhoneSwitcher getPhoneSwitcher();
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface TelephonyManagerProxy {
        int getPhoneCount();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String emergencyTypeToString(int i) {
        return i != 1 ? i != 2 ? "UNKNOWN" : "SMS" : "CALL";
    }

    /* loaded from: classes.dex */
    private static class TelephonyManagerProxyImpl implements TelephonyManagerProxy {
        private final TelephonyManager mTelephonyManager;

        TelephonyManagerProxyImpl(Context context) {
            this.mTelephonyManager = new TelephonyManager(context);
        }

        @Override // com.android.internal.telephony.emergency.EmergencyStateTracker.TelephonyManagerProxy
        public int getPhoneCount() {
            return this.mTelephonyManager.getActiveModemCount();
        }
    }

    @VisibleForTesting
    public Handler getHandler() {
        return this.mHandler;
    }

    /* loaded from: classes.dex */
    private class MyHandler extends Handler {
        MyHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        return;
                    }
                    Rlog.v("EmergencyStateTracker", "MSG_SET_EMERGENCY_CALLBACK_MODE_DONE for " + EmergencyStateTracker.emergencyTypeToString(((Integer) ((AsyncResult) message.obj).userObj).intValue()));
                    EmergencyStateTracker.this.setEmergencyModeInProgress(false);
                    if (EmergencyStateTracker.this.mSmsPhone != null) {
                        EmergencyStateTracker.this.completeEmergencyMode(2);
                        return;
                    }
                    return;
                }
                Integer num = (Integer) ((AsyncResult) message.obj).userObj;
                Rlog.v("EmergencyStateTracker", "MSG_EXIT_EMERGENCY_MODE_DONE for " + EmergencyStateTracker.emergencyTypeToString(num.intValue()));
                EmergencyStateTracker.this.setEmergencyModeInProgress(false);
                if (num.intValue() == 1) {
                    EmergencyStateTracker.this.setIsInEmergencyCall(false);
                    if (EmergencyStateTracker.this.mOnEcmExitCompleteRunnable != null) {
                        EmergencyStateTracker.this.mOnEcmExitCompleteRunnable.run();
                        EmergencyStateTracker.this.mOnEcmExitCompleteRunnable = null;
                        return;
                    }
                    return;
                } else if (num.intValue() == 2 && EmergencyStateTracker.this.mIsEmergencyCallStartedDuringEmergencySms) {
                    EmergencyStateTracker.this.mIsEmergencyCallStartedDuringEmergencySms = false;
                    EmergencyStateTracker emergencyStateTracker = EmergencyStateTracker.this;
                    emergencyStateTracker.turnOnRadioAndSwitchDds(emergencyStateTracker.mPhone, 1, EmergencyStateTracker.this.mIsTestEmergencyNumber);
                    return;
                } else {
                    return;
                }
            }
            AsyncResult asyncResult = (AsyncResult) message.obj;
            Integer num2 = (Integer) asyncResult.userObj;
            Rlog.v("EmergencyStateTracker", "MSG_SET_EMERGENCY_MODE_DONE for " + EmergencyStateTracker.emergencyTypeToString(num2.intValue()));
            if (asyncResult.exception == null) {
                EmergencyStateTracker.this.mLastEmergencyRegResult = (EmergencyRegResult) asyncResult.result;
            } else {
                EmergencyStateTracker.this.mLastEmergencyRegResult = null;
                Rlog.w("EmergencyStateTracker", "LastEmergencyRegResult not set. AsyncResult.exception: " + asyncResult.exception);
            }
            EmergencyStateTracker.this.setEmergencyModeInProgress(false);
            if (num2.intValue() == 1) {
                EmergencyStateTracker.this.setIsInEmergencyCall(true);
                EmergencyStateTracker.this.completeEmergencyMode(num2.intValue());
                if (EmergencyStateTracker.this.mSmsPhone != null) {
                    EmergencyStateTracker.this.completeEmergencyMode(2);
                }
            } else if (num2.intValue() == 2) {
                if (EmergencyStateTracker.this.mPhone != null && EmergencyStateTracker.this.mSmsPhone != null) {
                    if (EmergencyStateTracker.this.mIsEmergencyCallStartedDuringEmergencySms) {
                        Phone phone = EmergencyStateTracker.this.mPhone;
                        EmergencyStateTracker.this.mPhone = null;
                        EmergencyStateTracker emergencyStateTracker2 = EmergencyStateTracker.this;
                        emergencyStateTracker2.exitEmergencyMode(emergencyStateTracker2.mSmsPhone, num2.intValue());
                        EmergencyStateTracker.this.mPhone = phone;
                        if (EmergencyStateTracker.isSamePhone(EmergencyStateTracker.this.mPhone, EmergencyStateTracker.this.mSmsPhone)) {
                            return;
                        }
                        EmergencyStateTracker.this.completeEmergencyMode(num2.intValue(), 80);
                        return;
                    }
                    EmergencyStateTracker.this.completeEmergencyMode(num2.intValue());
                    return;
                }
                EmergencyStateTracker.this.completeEmergencyMode(num2.intValue());
                if (EmergencyStateTracker.this.mIsEmergencyCallStartedDuringEmergencySms) {
                    EmergencyStateTracker.this.mIsEmergencyCallStartedDuringEmergencySms = false;
                    EmergencyStateTracker emergencyStateTracker3 = EmergencyStateTracker.this;
                    emergencyStateTracker3.turnOnRadioAndSwitchDds(emergencyStateTracker3.mPhone, 1, EmergencyStateTracker.this.mIsTestEmergencyNumber);
                }
            }
        }
    }

    public static void make(Context context, boolean z) {
        if (INSTANCE == null) {
            INSTANCE = new EmergencyStateTracker(context, Looper.myLooper(), z);
        }
    }

    public static EmergencyStateTracker getInstance() {
        EmergencyStateTracker emergencyStateTracker = INSTANCE;
        if (emergencyStateTracker != null) {
            return emergencyStateTracker;
        }
        throw new IllegalStateException("EmergencyStateTracker is not ready!");
    }

    private EmergencyStateTracker(Context context, Looper looper, boolean z) {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.emergency.EmergencyStateTracker.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (intent.getAction().equals("android.intent.action.EMERGENCY_CALLBACK_MODE_CHANGED")) {
                    boolean booleanExtra = intent.getBooleanExtra("android.telephony.extra.PHONE_IN_ECM_STATE", false);
                    Rlog.d("EmergencyStateTracker", "Received ACTION_EMERGENCY_CALLBACK_MODE_CHANGED isInEcm = " + booleanExtra);
                    if (booleanExtra) {
                        return;
                    }
                    EmergencyStateTracker.this.exitEmergencyCallbackMode();
                }
            }
        };
        this.mEcmExitReceiver = broadcastReceiver;
        this.mPhoneFactoryProxy = new PhoneFactoryProxy() { // from class: com.android.internal.telephony.emergency.EmergencyStateTracker$$ExternalSyntheticLambda2
            @Override // com.android.internal.telephony.emergency.EmergencyStateTracker.PhoneFactoryProxy
            public final Phone[] getPhones() {
                return PhoneFactory.getPhones();
            }
        };
        this.mPhoneSwitcherProxy = new PhoneSwitcherProxy() { // from class: com.android.internal.telephony.emergency.EmergencyStateTracker$$ExternalSyntheticLambda3
            @Override // com.android.internal.telephony.emergency.EmergencyStateTracker.PhoneSwitcherProxy
            public final PhoneSwitcher getPhoneSwitcher() {
                return PhoneSwitcher.getInstance();
            }
        };
        this.mEcmExitTimeoutMs = 300000L;
        this.mContext = context;
        MyHandler myHandler = new MyHandler(looper);
        this.mHandler = myHandler;
        this.mIsSuplDdsSwitchRequiredForEmergencyCall = z;
        PowerManager powerManager = (PowerManager) context.getSystemService(PowerManager.class);
        this.mWakeLock = powerManager != null ? powerManager.newWakeLock(1, "telephony:EmergencyStateTracker") : null;
        this.mConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.EMERGENCY_CALLBACK_MODE_CHANGED");
        context.registerReceiver(broadcastReceiver, intentFilter, null, myHandler);
        this.mTelephonyManagerProxy = new TelephonyManagerProxyImpl(context);
    }

    @VisibleForTesting
    public EmergencyStateTracker(Context context, Looper looper, boolean z, PhoneFactoryProxy phoneFactoryProxy, PhoneSwitcherProxy phoneSwitcherProxy, TelephonyManagerProxy telephonyManagerProxy, RadioOnHelper radioOnHelper, long j) {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.emergency.EmergencyStateTracker.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (intent.getAction().equals("android.intent.action.EMERGENCY_CALLBACK_MODE_CHANGED")) {
                    boolean booleanExtra = intent.getBooleanExtra("android.telephony.extra.PHONE_IN_ECM_STATE", false);
                    Rlog.d("EmergencyStateTracker", "Received ACTION_EMERGENCY_CALLBACK_MODE_CHANGED isInEcm = " + booleanExtra);
                    if (booleanExtra) {
                        return;
                    }
                    EmergencyStateTracker.this.exitEmergencyCallbackMode();
                }
            }
        };
        this.mEcmExitReceiver = broadcastReceiver;
        this.mPhoneFactoryProxy = new PhoneFactoryProxy() { // from class: com.android.internal.telephony.emergency.EmergencyStateTracker$$ExternalSyntheticLambda2
            @Override // com.android.internal.telephony.emergency.EmergencyStateTracker.PhoneFactoryProxy
            public final Phone[] getPhones() {
                return PhoneFactory.getPhones();
            }
        };
        this.mPhoneSwitcherProxy = new PhoneSwitcherProxy() { // from class: com.android.internal.telephony.emergency.EmergencyStateTracker$$ExternalSyntheticLambda3
            @Override // com.android.internal.telephony.emergency.EmergencyStateTracker.PhoneSwitcherProxy
            public final PhoneSwitcher getPhoneSwitcher() {
                return PhoneSwitcher.getInstance();
            }
        };
        this.mContext = context;
        MyHandler myHandler = new MyHandler(looper);
        this.mHandler = myHandler;
        this.mIsSuplDdsSwitchRequiredForEmergencyCall = z;
        this.mPhoneFactoryProxy = phoneFactoryProxy;
        this.mPhoneSwitcherProxy = phoneSwitcherProxy;
        this.mTelephonyManagerProxy = telephonyManagerProxy;
        this.mRadioOnHelper = radioOnHelper;
        this.mEcmExitTimeoutMs = j;
        this.mWakeLock = null;
        this.mConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.EMERGENCY_CALLBACK_MODE_CHANGED");
        context.registerReceiver(broadcastReceiver, intentFilter, null, myHandler);
    }

    public CompletableFuture<Integer> startEmergencyCall(Phone phone, String str, boolean z) {
        Rlog.i("EmergencyStateTracker", "startEmergencyCall: phoneId=" + phone.getPhoneId() + ", callId=" + str);
        Phone phone2 = this.mPhone;
        if (phone2 != null) {
            if (isSamePhone(phone2, phone) && (!this.mActiveEmergencyCalls.isEmpty() || isInEcm())) {
                this.mOngoingCallId = str;
                this.mIsTestEmergencyNumber = z;
                return CompletableFuture.completedFuture(0);
            }
            Rlog.e("EmergencyStateTracker", "startEmergencyCall failed. Existing emergency call in progress.");
            return CompletableFuture.completedFuture(36);
        }
        this.mCallEmergencyModeFuture = new CompletableFuture<>();
        if (this.mSmsPhone != null) {
            this.mIsEmergencyCallStartedDuringEmergencySms = true;
            if (isInEmergencyMode() && !isEmergencyModeInProgress()) {
                exitEmergencyMode(this.mSmsPhone, 2);
            }
            this.mPhone = phone;
            this.mOngoingCallId = str;
            this.mIsTestEmergencyNumber = z;
            return this.mCallEmergencyModeFuture;
        }
        this.mPhone = phone;
        this.mOngoingCallId = str;
        this.mIsTestEmergencyNumber = z;
        turnOnRadioAndSwitchDds(phone, 1, z);
        return this.mCallEmergencyModeFuture;
    }

    public void endCall(String str) {
        boolean remove = this.mActiveEmergencyCalls.remove(str);
        if (Objects.equals(this.mOngoingCallId, str)) {
            this.mOngoingCallId = null;
        }
        if (remove && this.mActiveEmergencyCalls.isEmpty() && isEmergencyCallbackModeSupported()) {
            enterEmergencyCallbackMode();
            if (this.mOngoingCallId == null) {
                this.mIsEmergencyCallStartedDuringEmergencySms = false;
                this.mCallEmergencyModeFuture = null;
            }
        } else if (this.mOngoingCallId == null) {
            if (isInEcm()) {
                this.mIsEmergencyCallStartedDuringEmergencySms = false;
                this.mCallEmergencyModeFuture = null;
                if (this.mActiveEmergencyCalls.isEmpty()) {
                    setEmergencyMode(this.mPhone, 1, 3, 3);
                    return;
                }
                return;
            }
            exitEmergencyMode(this.mPhone, 1);
            clearEmergencyCallInfo();
        }
    }

    private void clearEmergencyCallInfo() {
        this.mEmergencyCallDomain = 0;
        this.mIsTestEmergencyNumber = false;
        this.mIsEmergencyCallStartedDuringEmergencySms = false;
        this.mCallEmergencyModeFuture = null;
        this.mOngoingCallId = null;
        this.mPhone = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void switchDdsAndSetEmergencyMode(final Phone phone, final int i) {
        switchDdsDelayed(phone, new Consumer() { // from class: com.android.internal.telephony.emergency.EmergencyStateTracker$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                EmergencyStateTracker.this.lambda$switchDdsAndSetEmergencyMode$0(phone, i, (Boolean) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$switchDdsAndSetEmergencyMode$0(Phone phone, int i, Boolean bool) {
        Rlog.i("EmergencyStateTracker", "switchDdsDelayed: result = " + bool);
        if (!bool.booleanValue()) {
            Rlog.e("EmergencyStateTracker", "DDS Switch failed.");
        }
        setEmergencyMode(phone, i, 1, 1);
    }

    private void setEmergencyMode(Phone phone, int i, int i2, int i3) {
        Rlog.i("EmergencyStateTracker", "setEmergencyMode from " + this.mEmergencyMode + " to " + i2 + " for " + emergencyTypeToString(i));
        if (this.mEmergencyMode == i2) {
            return;
        }
        this.mEmergencyMode = i2;
        setEmergencyModeInProgress(true);
        Message obtainMessage = this.mHandler.obtainMessage(i3, Integer.valueOf(i));
        if ((this.mIsTestEmergencyNumber && i == 1) || (this.mIsTestEmergencyNumberForSms && i == 2)) {
            Rlog.d("EmergencyStateTracker", "TestEmergencyNumber for " + emergencyTypeToString(i) + ": Skipping setting emergency mode on modem.");
            AsyncResult.forMessage(obtainMessage, (Object) null, (Throwable) null);
            obtainMessage.sendToTarget();
            return;
        }
        this.mWasEmergencyModeSetOnModem = true;
        phone.setEmergencyMode(i2, obtainMessage);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void completeEmergencyMode(int i) {
        completeEmergencyMode(i, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void completeEmergencyMode(int i, int i2) {
        if (i == 1) {
            CompletableFuture<Integer> completableFuture = this.mCallEmergencyModeFuture;
            if (completableFuture != null && !completableFuture.isDone()) {
                this.mCallEmergencyModeFuture.complete(Integer.valueOf(i2));
            }
            if (i2 != 0) {
                clearEmergencyCallInfo();
            }
        } else if (i == 2) {
            CompletableFuture<Integer> completableFuture2 = this.mSmsEmergencyModeFuture;
            if (completableFuture2 != null && !completableFuture2.isDone()) {
                this.mSmsEmergencyModeFuture.complete(Integer.valueOf(i2));
            }
            if (i2 != 0) {
                clearEmergencySmsInfo();
            }
        }
    }

    @VisibleForTesting
    public boolean isInEmergencyMode() {
        return this.mEmergencyMode != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setEmergencyModeInProgress(boolean z) {
        this.mIsEmergencyModeInProgress = z;
    }

    private boolean isEmergencyModeInProgress() {
        return this.mIsEmergencyModeInProgress;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setIsInEmergencyCall(boolean z) {
        this.mIsInEmergencyCall = z;
    }

    public boolean isInEmergencyCall() {
        return this.mIsInEmergencyCall;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void exitEmergencyMode(Phone phone, int i) {
        Phone phone2;
        Rlog.i("EmergencyStateTracker", "exitEmergencyMode for " + emergencyTypeToString(i));
        if (i == 1) {
            Phone phone3 = this.mSmsPhone;
            if (phone3 != null && isSamePhone(phone, phone3)) {
                Rlog.i("EmergencyStateTracker", "exitEmergencyMode: waits for emergency SMS end.");
                setIsInEmergencyCall(false);
                return;
            }
        } else if (i == 2 && (phone2 = this.mPhone) != null && isSamePhone(phone, phone2)) {
            Rlog.i("EmergencyStateTracker", "exitEmergencyMode: waits for emergency call end.");
            return;
        }
        if (this.mEmergencyMode == 0) {
            return;
        }
        this.mEmergencyMode = 0;
        setEmergencyModeInProgress(true);
        Message obtainMessage = this.mHandler.obtainMessage(2, Integer.valueOf(i));
        if (!this.mWasEmergencyModeSetOnModem) {
            Rlog.d("EmergencyStateTracker", "Emergency mode was not set on modem: Skipping exiting emergency mode.");
            AsyncResult.forMessage(obtainMessage, (Object) null, (Throwable) null);
            obtainMessage.sendToTarget();
            return;
        }
        this.mWasEmergencyModeSetOnModem = false;
        phone.exitEmergencyMode(obtainMessage);
    }

    public EmergencyRegResult getEmergencyRegResult() {
        return this.mLastEmergencyRegResult;
    }

    /* renamed from: onEmergencyTransportChanged */
    public void lambda$onEmergencyTransportChanged$1(final int i, final int i2) {
        Phone phone;
        if (!this.mHandler.getLooper().isCurrentThread()) {
            this.mHandler.post(new Runnable() { // from class: com.android.internal.telephony.emergency.EmergencyStateTracker$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    EmergencyStateTracker.this.lambda$onEmergencyTransportChanged$1(i, i2);
                }
            });
            return;
        }
        if (i == 1) {
            phone = this.mPhone;
        } else {
            phone = i == 2 ? this.mSmsPhone : null;
        }
        if (phone != null) {
            setEmergencyMode(phone, i, i2, 1);
        }
    }

    public void onEmergencyCallDomainUpdated(int i, String str) {
        Rlog.d("EmergencyStateTracker", "domain update for callId: " + str);
        int i2 = 1;
        if (i != 1 && i != 2) {
            if (i == 5) {
                i2 = 2;
            } else if (i != 6) {
                Rlog.w("EmergencyStateTracker", "domain updated: Unexpected phoneType:" + i);
                i2 = -1;
            }
        }
        if (this.mEmergencyCallDomain == i2) {
            return;
        }
        Rlog.i("EmergencyStateTracker", "domain updated: from " + this.mEmergencyCallDomain + " to " + i2);
        this.mEmergencyCallDomain = i2;
    }

    public void onEmergencyCallStateChanged(Call.State state, String str) {
        if (state == Call.State.ACTIVE) {
            this.mActiveEmergencyCalls.add(str);
        }
    }

    private boolean isEmergencyCallbackModeSupported() {
        return getConfig(this.mPhone.getSubId(), "imsemergency.emergency_callback_mode_supported_bool", true);
    }

    private void enterEmergencyCallbackMode() {
        Rlog.d("EmergencyStateTracker", "enter ECBM");
        setIsInEmergencyCall(false);
        if (isInEcm()) {
            return;
        }
        setIsInEcm(true);
        if (!this.mPhone.getUnitTestMode()) {
            TelephonyProperties.in_ecm_mode(Boolean.TRUE);
        }
        sendEmergencyCallbackModeChange();
        if (isInImsEcm()) {
            ((GsmCdmaPhone) this.mPhone).notifyEmergencyCallRegistrants(true);
        }
        setEmergencyMode(this.mPhone, 1, 3, 3);
        long longValue = TelephonyProperties.ecm_exit_timer().orElse(Long.valueOf(this.mEcmExitTimeoutMs)).longValue();
        this.mHandler.postDelayed(this.mExitEcmRunnable, longValue);
        PowerManager.WakeLock wakeLock = this.mWakeLock;
        if (wakeLock != null) {
            wakeLock.acquire(longValue);
        }
    }

    public void exitEmergencyCallbackMode() {
        Rlog.d("EmergencyStateTracker", "exit ECBM");
        this.mHandler.removeCallbacks(this.mExitEcmRunnable);
        if (isInEcm()) {
            setIsInEcm(false);
            if (!this.mPhone.getUnitTestMode()) {
                TelephonyProperties.in_ecm_mode(Boolean.FALSE);
            }
            PowerManager.WakeLock wakeLock = this.mWakeLock;
            if (wakeLock != null && wakeLock.isHeld()) {
                try {
                    this.mWakeLock.release();
                } catch (Exception e) {
                    Rlog.d("EmergencyStateTracker", "WakeLock already released: " + e.toString());
                }
            }
            GsmCdmaPhone gsmCdmaPhone = (GsmCdmaPhone) this.mPhone;
            sendEmergencyCallbackModeChange();
            gsmCdmaPhone.notifyEmergencyCallRegistrants(false);
            exitEmergencyMode(gsmCdmaPhone, 1);
        }
        this.mEmergencyCallDomain = 0;
        this.mIsTestEmergencyNumber = false;
        this.mPhone = null;
    }

    public void exitEmergencyCallbackMode(Runnable runnable) {
        this.mOnEcmExitCompleteRunnable = runnable;
        exitEmergencyCallbackMode();
    }

    private void sendEmergencyCallbackModeChange() {
        Rlog.d("EmergencyStateTracker", "sendEmergencyCallbackModeChange: isInEcm=" + isInEcm());
        Intent intent = new Intent("android.intent.action.EMERGENCY_CALLBACK_MODE_CHANGED");
        intent.putExtra("android.telephony.extra.PHONE_IN_ECM_STATE", isInEcm());
        SubscriptionManager.putPhoneIdAndSubIdExtra(intent, this.mPhone.getPhoneId());
        this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
    }

    public boolean isInEcm() {
        return this.mIsInEcm;
    }

    private void setIsInEcm(boolean z) {
        this.mIsInEcm = z;
    }

    public boolean isInImsEcm() {
        return this.mEmergencyCallDomain == 2 && isInEcm();
    }

    public boolean isInCdmaEcm() {
        Phone phone = this.mPhone;
        return phone != null && phone.getPhoneType() == 2 && this.mEmergencyCallDomain == 1 && isInEcm();
    }

    public CompletableFuture<Integer> startEmergencySms(Phone phone, String str, boolean z) {
        Rlog.i("EmergencyStateTracker", "startEmergencySms: phoneId=" + phone.getPhoneId() + ", smsId=" + str);
        Phone phone2 = this.mPhone;
        if (phone2 != null && !isSamePhone(phone2, phone)) {
            Rlog.e("EmergencyStateTracker", "Emergency call is in progress on the other slot.");
            return CompletableFuture.completedFuture(36);
        }
        Phone phone3 = this.mSmsPhone;
        if (phone3 != null && !isSamePhone(phone3, phone)) {
            Rlog.e("EmergencyStateTracker", "Emergency SMS is in progress on the other slot.");
            return CompletableFuture.completedFuture(36);
        } else if (this.mSmsPhone != null && isInEmergencyMode() && isEmergencyModeInProgress()) {
            Rlog.e("EmergencyStateTracker", "Existing emergency SMS is in progress.");
            return CompletableFuture.completedFuture(36);
        } else {
            this.mSmsPhone = phone;
            this.mIsTestEmergencyNumberForSms = z;
            this.mOngoingEmergencySmsIds.add(str);
            if (isInEmergencyMode() && !isEmergencyModeInProgress()) {
                return CompletableFuture.completedFuture(0);
            }
            this.mSmsEmergencyModeFuture = new CompletableFuture<>();
            if (!isInEmergencyMode()) {
                setEmergencyMode(this.mSmsPhone, 2, 1, 1);
            }
            return this.mSmsEmergencyModeFuture;
        }
    }

    public void endSms(String str, EmergencyNumber emergencyNumber) {
        this.mOngoingEmergencySmsIds.remove(str);
        if (this.mOngoingEmergencySmsIds.isEmpty()) {
            if (isInEcm()) {
                if (this.mActiveEmergencyCalls.isEmpty() && this.mOngoingCallId == null) {
                    setEmergencyMode(this.mPhone, 1, 3, 3);
                }
            } else {
                exitEmergencyMode(this.mSmsPhone, 2);
            }
            clearEmergencySmsInfo();
        }
    }

    private void clearEmergencySmsInfo() {
        this.mOngoingEmergencySmsIds.clear();
        this.mIsTestEmergencyNumberForSms = false;
        this.mSmsEmergencyModeFuture = null;
        this.mSmsPhone = null;
    }

    private boolean isRadioOn() {
        boolean z = false;
        for (Phone phone : this.mPhoneFactoryProxy.getPhones()) {
            z |= phone.isRadioOn();
        }
        return z;
    }

    private boolean isAirplaneModeOn(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "airplane_mode_on", 0) > 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void turnOnRadioAndSwitchDds(final Phone phone, final int i, boolean z) {
        if (!isRadioOn() || isAirplaneModeOn(this.mContext)) {
            Rlog.i("EmergencyStateTracker", "turnOnRadioAndSwitchDds: phoneId=" + phone.getPhoneId() + " for " + emergencyTypeToString(i));
            if (this.mRadioOnHelper == null) {
                this.mRadioOnHelper = new RadioOnHelper(this.mContext);
            }
            this.mRadioOnHelper.triggerRadioOnAndListen(new RadioOnStateListener.Callback() { // from class: com.android.internal.telephony.emergency.EmergencyStateTracker.2
                @Override // com.android.internal.telephony.emergency.RadioOnStateListener.Callback
                public void onComplete(RadioOnStateListener radioOnStateListener, boolean z2) {
                    if (!z2) {
                        Rlog.e("EmergencyStateTracker", "Failed to turn on radio.");
                        EmergencyStateTracker.this.completeEmergencyMode(i, 17);
                        return;
                    }
                    EmergencyStateTracker.this.switchDdsAndSetEmergencyMode(phone, i);
                }

                @Override // com.android.internal.telephony.emergency.RadioOnStateListener.Callback
                public boolean isOkToCall(Phone phone2, int i2) {
                    return phone2.getServiceStateTracker().isRadioOn();
                }
            }, !z, phone, z);
            return;
        }
        switchDdsAndSetEmergencyMode(phone, i);
    }

    @VisibleForTesting
    public void switchDdsDelayed(Phone phone, Consumer<Boolean> consumer) {
        if (phone == null) {
            consumer.accept(Boolean.FALSE);
        }
        try {
            CompletableFuture<Boolean> possiblyOverrideDefaultDataForEmergencyCall = possiblyOverrideDefaultDataForEmergencyCall(phone);
            final CompletableFuture completableFuture = new CompletableFuture();
            this.mHandler.postDelayed(new Runnable() { // from class: com.android.internal.telephony.emergency.EmergencyStateTracker$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    EmergencyStateTracker.lambda$switchDdsDelayed$2(completableFuture);
                }
            }, 1000L);
            Handler handler = this.mHandler;
            Objects.requireNonNull(handler);
            possiblyOverrideDefaultDataForEmergencyCall.acceptEitherAsync((CompletionStage<? extends Boolean>) completableFuture, (Consumer<? super Boolean>) consumer, (Executor) new NetworkTypeController$$ExternalSyntheticLambda0(handler));
        } catch (Exception e) {
            Rlog.w("EmergencyStateTracker", "switchDdsDelayed - exception= " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$switchDdsDelayed$2(CompletableFuture completableFuture) {
        completableFuture.complete(Boolean.FALSE);
    }

    private CompletableFuture<Boolean> possiblyOverrideDefaultDataForEmergencyCall(Phone phone) {
        if (this.mTelephonyManagerProxy.getPhoneCount() <= 1) {
            return CompletableFuture.completedFuture(Boolean.TRUE);
        }
        if (!this.mIsSuplDdsSwitchRequiredForEmergencyCall) {
            Rlog.d("EmergencyStateTracker", "possiblyOverrideDefaultDataForEmergencyCall: not switching DDS, does not require DDS switch.");
            return CompletableFuture.completedFuture(Boolean.TRUE);
        } else if (!isAvailableForEmergencyCalls(phone)) {
            Rlog.d("EmergencyStateTracker", "possiblyOverrideDefaultDataForEmergencyCall: not switching DDS");
            return CompletableFuture.completedFuture(Boolean.TRUE);
        } else {
            boolean voiceRoaming = phone.getServiceState().getVoiceRoaming();
            String[] config = getConfig(phone.getSubId(), "gps.es_supl_data_plane_only_roaming_plmn_string_array");
            int i = 0;
            boolean z = config == null || !Arrays.asList(config).contains(phone.getServiceState().getOperatorNumeric());
            if (voiceRoaming && z) {
                Rlog.d("EmergencyStateTracker", "possiblyOverrideDefaultDataForEmergencyCall: roaming network is assumed to support CP fallback, not switching DDS.");
                return CompletableFuture.completedFuture(Boolean.TRUE);
            }
            if ((getConfig(phone.getSubId(), "gps.es_supl_control_plane_support_int", 0) != 2) && z) {
                Rlog.d("EmergencyStateTracker", "possiblyOverrideDefaultDataForEmergencyCall: not switching DDS, carrier supports CP fallback.");
                return CompletableFuture.completedFuture(Boolean.TRUE);
            }
            try {
                i = Integer.parseInt(getConfig(phone.getSubId(), "gps.es_extension_sec", "0"));
            } catch (NumberFormatException unused) {
            }
            CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
            try {
                Rlog.d("EmergencyStateTracker", "possiblyOverrideDefaultDataForEmergencyCall: overriding DDS for " + i + "seconds");
                this.mPhoneSwitcherProxy.getPhoneSwitcher().overrideDefaultDataForEmergency(phone.getPhoneId(), i, completableFuture);
                return completableFuture;
            } catch (Exception e) {
                Rlog.w("EmergencyStateTracker", "possiblyOverrideDefaultDataForEmergencyCall: exception = " + e.getMessage());
                return CompletableFuture.completedFuture(Boolean.FALSE);
            }
        }
    }

    private String getConfig(int i, String str, String str2) {
        return getConfigBundle(i, str).getString(str, str2);
    }

    private int getConfig(int i, String str, int i2) {
        return getConfigBundle(i, str).getInt(str, i2);
    }

    private String[] getConfig(int i, String str) {
        return getConfigBundle(i, str).getStringArray(str);
    }

    private boolean getConfig(int i, String str, boolean z) {
        return getConfigBundle(i, str).getBoolean(str, z);
    }

    private PersistableBundle getConfigBundle(int i, String str) {
        CarrierConfigManager carrierConfigManager = this.mConfigManager;
        return carrierConfigManager == null ? new PersistableBundle() : carrierConfigManager.getConfigForSubId(i, new String[]{str});
    }

    private boolean isAvailableForEmergencyCalls(Phone phone) {
        return phone.getServiceState().getState() == 0 || phone.getServiceState().isEmergencyOnly();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isSamePhone(Phone phone, Phone phone2) {
        return (phone == null || phone2 == null || phone.getPhoneId() != phone2.getPhoneId()) ? false : true;
    }
}
