package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.internal.telephony.sysprop.TelephonyProperties;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.EventLog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.Call;
import com.android.internal.telephony.CommandException;
import com.android.internal.telephony.DriverCall;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.PhoneInternalInterface;
import com.android.internal.telephony.cdma.CdmaCallWaitingNotification;
import com.android.internal.telephony.domainselection.DomainSelectionResolver;
import com.android.internal.telephony.emergency.EmergencyStateTracker;
import com.android.internal.telephony.metrics.TelephonyMetrics;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class GsmCdmaCallTracker extends CallTracker {
    public static final int MAX_CONNECTIONS_GSM = 19;
    private int m3WayCallFlashDelay;
    @VisibleForTesting
    public GsmCdmaConnection[] mConnections;
    private boolean mHangupPendingMO;
    private boolean mIsInEmergencyCall;
    private int mPendingCallClirMode;
    private boolean mPendingCallInEcm;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private GsmCdmaConnection mPendingMO;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private GsmCdmaPhone mPhone;
    private RegistrantList mVoiceCallEndedRegistrants = new RegistrantList();
    private RegistrantList mVoiceCallStartedRegistrants = new RegistrantList();
    private ArrayList<GsmCdmaConnection> mDroppedDuringPoll = new ArrayList<>(19);
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public GsmCdmaCall mRingingCall = new GsmCdmaCall(this);
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public GsmCdmaCall mForegroundCall = new GsmCdmaCall(this);
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public GsmCdmaCall mBackgroundCall = new GsmCdmaCall(this);
    private boolean mDesiredMute = false;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public PhoneConstants.State mState = PhoneConstants.State.IDLE;
    private TelephonyMetrics mMetrics = TelephonyMetrics.getInstance();
    private RegistrantList mCallWaitingRegistrants = new RegistrantList();
    private BroadcastReceiver mEcmExitReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.GsmCdmaCallTracker.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.EMERGENCY_CALLBACK_MODE_CHANGED")) {
                boolean booleanExtra = intent.getBooleanExtra("android.telephony.extra.PHONE_IN_ECM_STATE", false);
                GsmCdmaCallTracker gsmCdmaCallTracker = GsmCdmaCallTracker.this;
                gsmCdmaCallTracker.log("Received ACTION_EMERGENCY_CALLBACK_MODE_CHANGED isInEcm = " + booleanExtra);
                if (booleanExtra) {
                    return;
                }
                ArrayList<Connection> arrayList = new ArrayList();
                arrayList.addAll(GsmCdmaCallTracker.this.mRingingCall.getConnections());
                arrayList.addAll(GsmCdmaCallTracker.this.mForegroundCall.getConnections());
                arrayList.addAll(GsmCdmaCallTracker.this.mBackgroundCall.getConnections());
                if (GsmCdmaCallTracker.this.mPendingMO != null) {
                    arrayList.add(GsmCdmaCallTracker.this.mPendingMO);
                }
                for (Connection connection : arrayList) {
                    if (connection != null) {
                        connection.onExitedEcmMode();
                    }
                }
            }
        }
    };

    public GsmCdmaCallTracker(GsmCdmaPhone gsmCdmaPhone) {
        this.mPhone = gsmCdmaPhone;
        CommandsInterface commandsInterface = gsmCdmaPhone.mCi;
        this.mCi = commandsInterface;
        commandsInterface.registerForCallStateChanged(this, 2, null);
        this.mCi.registerForOn(this, 9, null);
        this.mCi.registerForNotAvailable(this, 10, null);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.EMERGENCY_CALLBACK_MODE_CHANGED");
        this.mPhone.getContext().registerReceiver(this.mEcmExitReceiver, intentFilter);
        updatePhoneType(true);
    }

    public void updatePhoneType() {
        updatePhoneType(false);
    }

    private void updatePhoneType(boolean z) {
        if (!z) {
            reset();
            pollCallsWhenSafe();
        }
        if (this.mPhone.isPhoneTypeGsm()) {
            this.mConnections = new GsmCdmaConnection[19];
            this.mCi.unregisterForCallWaitingInfo(this);
            return;
        }
        this.mConnections = new GsmCdmaConnection[8];
        this.mPendingCallInEcm = false;
        this.mIsInEmergencyCall = false;
        this.mPendingCallClirMode = 0;
        this.mPhone.setEcmCanceledForEmergency(false);
        this.m3WayCallFlashDelay = 0;
        this.mCi.registerForCallWaitingInfo(this, 15, null);
    }

    private void reset() {
        GsmCdmaConnection[] gsmCdmaConnectionArr;
        Rlog.d("GsmCdmaCallTracker", "reset");
        for (GsmCdmaConnection gsmCdmaConnection : this.mConnections) {
            if (gsmCdmaConnection != null) {
                gsmCdmaConnection.onDisconnect(36);
                gsmCdmaConnection.dispose();
            }
        }
        GsmCdmaConnection gsmCdmaConnection2 = this.mPendingMO;
        if (gsmCdmaConnection2 != null) {
            gsmCdmaConnection2.onDisconnect(36);
            this.mPendingMO.dispose();
        }
        this.mConnections = null;
        this.mPendingMO = null;
        clearDisconnected();
    }

    protected void finalize() {
        Rlog.d("GsmCdmaCallTracker", "GsmCdmaCallTracker finalized");
    }

    @Override // com.android.internal.telephony.CallTracker
    public void registerForVoiceCallStarted(Handler handler, int i, Object obj) {
        Registrant registrant = new Registrant(handler, i, obj);
        this.mVoiceCallStartedRegistrants.add(registrant);
        if (this.mState != PhoneConstants.State.IDLE) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, (Object) null, (Throwable) null));
        }
    }

    @Override // com.android.internal.telephony.CallTracker
    public void unregisterForVoiceCallStarted(Handler handler) {
        this.mVoiceCallStartedRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.CallTracker
    public void registerForVoiceCallEnded(Handler handler, int i, Object obj) {
        this.mVoiceCallEndedRegistrants.add(new Registrant(handler, i, obj));
    }

    @Override // com.android.internal.telephony.CallTracker
    public void unregisterForVoiceCallEnded(Handler handler) {
        this.mVoiceCallEndedRegistrants.remove(handler);
    }

    public void registerForCallWaiting(Handler handler, int i, Object obj) {
        this.mCallWaitingRegistrants.add(new Registrant(handler, i, obj));
    }

    public void unregisterForCallWaiting(Handler handler) {
        this.mCallWaitingRegistrants.remove(handler);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void fakeHoldForegroundBeforeDial() {
        Iterator<Connection> it = this.mForegroundCall.getConnections().iterator();
        while (it.hasNext()) {
            ((GsmCdmaConnection) it.next()).fakeHoldBeforeDial();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:30:0x00fc A[Catch: all -> 0x0117, TryCatch #0 {, blocks: (B:4:0x0007, B:6:0x0011, B:8:0x0029, B:10:0x003f, B:12:0x0044, B:13:0x0047, B:14:0x004a, B:16:0x0054, B:18:0x0066, B:19:0x008d, B:21:0x00b0, B:23:0x00bc, B:26:0x00cb, B:28:0x00f8, B:30:0x00fc, B:31:0x0103, B:27:0x00f0, B:34:0x010f, B:35:0x0116), top: B:40:0x0007 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public synchronized Connection dialGsm(String str, PhoneInternalInterface.DialArgs dialArgs) throws CallStateException {
        int i;
        int i2 = dialArgs.clirMode;
        UUSInfo uUSInfo = dialArgs.uusInfo;
        Bundle bundle = dialArgs.intentExtras;
        boolean z = dialArgs.isEmergency;
        if (z) {
            log("dial gsm emergency call, set clirModIe=2");
            i = 2;
        } else {
            i = i2;
        }
        clearDisconnected();
        checkForDialIssues(z);
        String convertNumberIfNecessary = convertNumberIfNecessary(this.mPhone, str);
        if (this.mForegroundCall.getState() == Call.State.ACTIVE) {
            switchWaitingOrHoldingAndActive();
            try {
                Thread.sleep(500L);
            } catch (InterruptedException unused) {
            }
            fakeHoldForegroundBeforeDial();
        }
        if (this.mForegroundCall.getState() != Call.State.IDLE) {
            throw new CallStateException("cannot dial in current state");
        }
        this.mPendingMO = new GsmCdmaConnection(this.mPhone, convertNumberIfNecessary, this, this.mForegroundCall, dialArgs);
        if (bundle != null) {
            Rlog.d("GsmCdmaCallTracker", "dialGsm - emergency dialer: " + bundle.getBoolean("android.telecom.extra.IS_USER_INTENT_EMERGENCY_CALL"));
            this.mPendingMO.setHasKnownUserIntentEmergency(bundle.getBoolean("android.telecom.extra.IS_USER_INTENT_EMERGENCY_CALL"));
        }
        this.mHangupPendingMO = false;
        this.mMetrics.writeRilDial(this.mPhone.getPhoneId(), this.mPendingMO, i, uUSInfo);
        this.mPhone.getVoiceCallSessionStats().onRilDial(this.mPendingMO);
        if (this.mPendingMO.getAddress() != null && this.mPendingMO.getAddress().length() != 0 && this.mPendingMO.getAddress().indexOf(78) < 0) {
            setMute(false);
            this.mCi.dial(this.mPendingMO.getAddress(), this.mPendingMO.isEmergencyCall(), this.mPendingMO.getEmergencyNumberInfo(), this.mPendingMO.hasKnownUserIntentEmergency(), i, uUSInfo, obtainCompleteMessage());
            if (this.mNumberConverted) {
                this.mPendingMO.restoreDialedNumberAfterConversion(str);
                this.mNumberConverted = false;
            }
            updatePhoneState();
            this.mPhone.notifyPreciseCallStateChanged();
        }
        this.mPendingMO.mCause = 7;
        pollCallsWhenSafe();
        if (this.mNumberConverted) {
        }
        updatePhoneState();
        this.mPhone.notifyPreciseCallStateChanged();
        return this.mPendingMO;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void handleEcmTimer(int i) {
        this.mPhone.handleTimerInEmergencyCallbackMode(i);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void disableDataCallInEmergencyCall(String str) {
        if (((TelephonyManager) this.mPhone.getContext().getSystemService("phone")).isEmergencyNumber(str)) {
            log("disableDataCallInEmergencyCall");
            setIsInEmergencyCall();
        }
    }

    public void setIsInEmergencyCall() {
        this.mIsInEmergencyCall = true;
        this.mPhone.notifyEmergencyCallRegistrants(true);
        this.mPhone.sendEmergencyCallStateChange(true);
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x0077, code lost:
        if ("vi".equals(r0) == false) goto L17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0079, code lost:
        r2 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0089, code lost:
        if ("us".equals(r0) == false) goto L17;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private Connection dialCdma(String str, PhoneInternalInterface.DialArgs dialArgs) throws CallStateException {
        final int i;
        boolean z;
        int i2 = dialArgs.clirMode;
        Bundle bundle = dialArgs.intentExtras;
        boolean z2 = dialArgs.isEmergency;
        if (z2) {
            log("dial cdma emergency call, set clirModIe=2");
            i = 2;
        } else {
            i = i2;
        }
        clearDisconnected();
        checkForDialIssues(z2);
        String networkCountryIso = ((TelephonyManager) this.mPhone.getContext().getSystemService("phone")).getNetworkCountryIso(this.mPhone.getPhoneId());
        String simCountryIsoForPhone = TelephonyManager.getSimCountryIsoForPhone(this.mPhone.getPhoneId());
        boolean z3 = (TextUtils.isEmpty(networkCountryIso) || TextUtils.isEmpty(simCountryIsoForPhone) || simCountryIsoForPhone.equals(networkCountryIso)) ? false : true;
        if (z3) {
            if ("us".equals(simCountryIsoForPhone)) {
                if (z3) {
                }
                z3 = false;
            } else if ("vi".equals(simCountryIsoForPhone)) {
                if (z3) {
                }
                z3 = false;
            }
        }
        String convertNumberIfNecessary = z3 ? convertNumberIfNecessary(this.mPhone, str) : str;
        boolean isInEcm = this.mPhone.isInEcm();
        if (isInEcm && z2) {
            this.mPhone.handleTimerInEmergencyCallbackMode(1);
        }
        if (this.mForegroundCall.getState() == Call.State.ACTIVE) {
            return dialThreeWay(convertNumberIfNecessary, dialArgs);
        }
        this.mPendingMO = new GsmCdmaConnection(this.mPhone, convertNumberIfNecessary, this, this.mForegroundCall, dialArgs);
        if (bundle != null) {
            Rlog.d("GsmCdmaCallTracker", "dialGsm - emergency dialer: " + bundle.getBoolean("android.telecom.extra.IS_USER_INTENT_EMERGENCY_CALL"));
            this.mPendingMO.setHasKnownUserIntentEmergency(bundle.getBoolean("android.telecom.extra.IS_USER_INTENT_EMERGENCY_CALL"));
        }
        this.mHangupPendingMO = false;
        if (this.mPendingMO.getAddress() == null || this.mPendingMO.getAddress().length() == 0 || this.mPendingMO.getAddress().indexOf(78) >= 0) {
            z = false;
            this.mPendingMO.mCause = 7;
            pollCallsWhenSafe();
        } else {
            setMute(false);
            disableDataCallInEmergencyCall(convertNumberIfNecessary);
            if (!isInEcm || (isInEcm && z2)) {
                z = false;
                this.mCi.dial(this.mPendingMO.getAddress(), this.mPendingMO.isEmergencyCall(), this.mPendingMO.getEmergencyNumberInfo(), this.mPendingMO.hasKnownUserIntentEmergency(), i, obtainCompleteMessage());
            } else {
                if (DomainSelectionResolver.getInstance().isDomainSelectionSupported()) {
                    this.mPendingCallInEcm = true;
                    EmergencyStateTracker.getInstance().exitEmergencyCallbackMode(new Runnable() { // from class: com.android.internal.telephony.GsmCdmaCallTracker.2
                        @Override // java.lang.Runnable
                        public void run() {
                            GsmCdmaCallTracker gsmCdmaCallTracker = GsmCdmaCallTracker.this;
                            gsmCdmaCallTracker.mCi.dial(gsmCdmaCallTracker.mPendingMO.getAddress(), GsmCdmaCallTracker.this.mPendingMO.isEmergencyCall(), GsmCdmaCallTracker.this.mPendingMO.getEmergencyNumberInfo(), GsmCdmaCallTracker.this.mPendingMO.hasKnownUserIntentEmergency(), i, GsmCdmaCallTracker.this.obtainCompleteMessage());
                        }
                    });
                } else {
                    this.mPhone.exitEmergencyCallbackMode();
                    this.mPhone.setOnEcbModeExitResponse(this, 14, null);
                    this.mPendingCallClirMode = i;
                    this.mPendingCallInEcm = true;
                }
                z = false;
            }
        }
        if (this.mNumberConverted) {
            this.mPendingMO.restoreDialedNumberAfterConversion(str);
            this.mNumberConverted = z;
        }
        updatePhoneState();
        this.mPhone.notifyPreciseCallStateChanged();
        return this.mPendingMO;
    }

    private Connection dialThreeWay(String str, PhoneInternalInterface.DialArgs dialArgs) {
        Bundle bundle = dialArgs.intentExtras;
        if (this.mForegroundCall.isIdle()) {
            return null;
        }
        disableDataCallInEmergencyCall(str);
        this.mPendingMO = new GsmCdmaConnection(this.mPhone, str, this, this.mForegroundCall, dialArgs);
        if (bundle != null) {
            Rlog.d("GsmCdmaCallTracker", "dialThreeWay - emergency dialer " + bundle.getBoolean("android.telecom.extra.IS_USER_INTENT_EMERGENCY_CALL"));
            this.mPendingMO.setHasKnownUserIntentEmergency(bundle.getBoolean("android.telecom.extra.IS_USER_INTENT_EMERGENCY_CALL"));
        }
        PersistableBundle configForSubId = ((CarrierConfigManager) this.mPhone.getContext().getSystemService("carrier_config")).getConfigForSubId(this.mPhone.getSubId());
        if (configForSubId != null) {
            this.m3WayCallFlashDelay = configForSubId.getInt("cdma_3waycall_flash_delay_int");
        } else {
            this.m3WayCallFlashDelay = 0;
        }
        if (this.m3WayCallFlashDelay > 0) {
            this.mCi.sendCDMAFeatureCode(PhoneConfigurationManager.SSSS, obtainMessage(20));
        } else {
            this.mCi.sendCDMAFeatureCode(this.mPendingMO.getAddress(), obtainMessage(16));
        }
        return this.mPendingMO;
    }

    public Connection dial(String str, PhoneInternalInterface.DialArgs dialArgs) throws CallStateException {
        if (isPhoneTypeGsm()) {
            return dialGsm(str, dialArgs);
        }
        return dialCdma(str, dialArgs);
    }

    public Connection dialGsm(String str, UUSInfo uUSInfo, Bundle bundle) throws CallStateException {
        return dialGsm(str, new PhoneInternalInterface.DialArgs.Builder().setUusInfo(uUSInfo).setClirMode(0).setIntentExtras(bundle).build());
    }

    public Connection dialGsm(String str, int i, UUSInfo uUSInfo, Bundle bundle) throws CallStateException {
        return dialGsm(str, new PhoneInternalInterface.DialArgs.Builder().setClirMode(i).setUusInfo(uUSInfo).setIntentExtras(bundle).build());
    }

    public void acceptCall() throws CallStateException {
        if (this.mRingingCall.getState() == Call.State.INCOMING) {
            Rlog.i("phone", "acceptCall: incoming...");
            setMute(false);
            this.mPhone.getVoiceCallSessionStats().onRilAcceptCall(this.mRingingCall.getConnections());
            this.mCi.acceptCall(obtainCompleteMessage());
        } else if (this.mRingingCall.getState() == Call.State.WAITING) {
            if (isPhoneTypeGsm()) {
                setMute(false);
            } else {
                GsmCdmaConnection gsmCdmaConnection = (GsmCdmaConnection) this.mRingingCall.getLatestConnection();
                gsmCdmaConnection.updateParent(this.mRingingCall, this.mForegroundCall);
                gsmCdmaConnection.onConnectedInOrOut();
                updatePhoneState();
            }
            switchWaitingOrHoldingAndActive();
        } else {
            throw new CallStateException("phone not ringing");
        }
    }

    public void rejectCall() throws CallStateException {
        if (this.mRingingCall.getState().isRinging()) {
            this.mCi.rejectCall(obtainCompleteMessage());
            return;
        }
        throw new CallStateException("phone not ringing");
    }

    private void flashAndSetGenericTrue() {
        this.mCi.sendCDMAFeatureCode(PhoneConfigurationManager.SSSS, obtainMessage(8));
        this.mPhone.notifyPreciseCallStateChanged();
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void switchWaitingOrHoldingAndActive() throws CallStateException {
        if (this.mRingingCall.getState() == Call.State.INCOMING) {
            throw new CallStateException("cannot be in the incoming state");
        }
        if (isPhoneTypeGsm()) {
            this.mCi.switchWaitingOrHoldingAndActive(obtainCompleteMessage(8));
        } else if (this.mForegroundCall.getConnectionsCount() > 1) {
            flashAndSetGenericTrue();
        } else {
            this.mCi.sendCDMAFeatureCode(PhoneConfigurationManager.SSSS, obtainMessage(8));
        }
    }

    public void conference() {
        if (isPhoneTypeGsm()) {
            this.mCi.conference(obtainCompleteMessage(11));
        } else {
            flashAndSetGenericTrue();
        }
    }

    public void explicitCallTransfer() {
        this.mCi.explicitCallTransfer(obtainCompleteMessage(13));
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void clearDisconnected() {
        internalClearDisconnected();
        updatePhoneState();
        this.mPhone.notifyPreciseCallStateChanged();
    }

    public boolean canConference() {
        return this.mForegroundCall.getState() == Call.State.ACTIVE && this.mBackgroundCall.getState() == Call.State.HOLDING && !this.mBackgroundCall.isFull() && !this.mForegroundCall.isFull();
    }

    public void checkForDialIssues(boolean z) throws CallStateException {
        boolean booleanValue = TelephonyProperties.disable_call().orElse(Boolean.FALSE).booleanValue();
        if (this.mCi.getRadioState() != 1) {
            throw new CallStateException(2, "Modem not powered");
        }
        if (booleanValue) {
            throw new CallStateException(5, "Calling disabled via ro.telephony.disable-call property");
        }
        if (this.mPendingMO != null) {
            throw new CallStateException(3, "A call is already dialing.");
        }
        if (this.mRingingCall.isRinging()) {
            throw new CallStateException(4, "Can't call while a call is ringing.");
        }
        if (isPhoneTypeGsm() && this.mForegroundCall.getState().isAlive() && this.mBackgroundCall.getState().isAlive()) {
            throw new CallStateException(6, "There is already a foreground and background call.");
        }
        if (!isPhoneTypeGsm() && this.mForegroundCall.getState().isAlive() && this.mForegroundCall.getState() != Call.State.ACTIVE && this.mBackgroundCall.getState().isAlive()) {
            throw new CallStateException(6, "There is already a foreground and background call.");
        }
        if (!z && isInOtaspCall()) {
            throw new CallStateException(7, "OTASP provisioning is in process.");
        }
    }

    public boolean canTransfer() {
        if (isPhoneTypeGsm()) {
            return (this.mForegroundCall.getState() == Call.State.ACTIVE || this.mForegroundCall.getState() == Call.State.ALERTING || this.mForegroundCall.getState() == Call.State.DIALING) && this.mBackgroundCall.getState() == Call.State.HOLDING;
        }
        Rlog.e("GsmCdmaCallTracker", "canTransfer: not possible in CDMA");
        return false;
    }

    private void internalClearDisconnected() {
        this.mRingingCall.clearDisconnected();
        this.mForegroundCall.clearDisconnected();
        this.mBackgroundCall.clearDisconnected();
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public Message obtainCompleteMessage() {
        return obtainCompleteMessage(4);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private Message obtainCompleteMessage(int i) {
        this.mPendingOperations++;
        this.mLastRelevantPoll = null;
        this.mNeedsPoll = true;
        return obtainMessage(i);
    }

    private void operationComplete() {
        int i = this.mPendingOperations - 1;
        this.mPendingOperations = i;
        if (i == 0 && this.mNeedsPoll) {
            Message obtainMessage = obtainMessage(1);
            this.mLastRelevantPoll = obtainMessage;
            this.mCi.getCurrentCalls(obtainMessage);
        } else if (i < 0) {
            Rlog.e("GsmCdmaCallTracker", "GsmCdmaCallTracker.pendingOperations < 0");
            this.mPendingOperations = 0;
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void updatePhoneState() {
        PhoneConstants.State state = this.mState;
        if (this.mRingingCall.isRinging()) {
            this.mState = PhoneConstants.State.RINGING;
        } else if (this.mPendingMO != null || !this.mForegroundCall.isIdle() || !this.mBackgroundCall.isIdle()) {
            this.mState = PhoneConstants.State.OFFHOOK;
        } else {
            Phone imsPhone = this.mPhone.getImsPhone();
            if (this.mState == PhoneConstants.State.OFFHOOK && imsPhone != null) {
                imsPhone.callEndCleanupHandOverCallIfAny();
            }
            this.mState = PhoneConstants.State.IDLE;
        }
        PhoneConstants.State state2 = this.mState;
        PhoneConstants.State state3 = PhoneConstants.State.IDLE;
        if (state2 == state3 && state != state2) {
            this.mVoiceCallEndedRegistrants.notifyRegistrants(new AsyncResult((Object) null, (Object) null, (Throwable) null));
        } else if (state == state3 && state != state2) {
            this.mVoiceCallStartedRegistrants.notifyRegistrants(new AsyncResult((Object) null, (Object) null, (Throwable) null));
        }
        log("update phone state, old=" + state + " new=" + this.mState);
        if (this.mState != state) {
            this.mPhone.notifyPhoneStateChanged();
            this.mMetrics.writePhoneState(this.mPhone.getPhoneId(), this.mState);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:30:0x006d, code lost:
        r19.mHangupPendingMO = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0074, code lost:
        if (isPhoneTypeGsm() != false) goto L35;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x007c, code lost:
        if (r19.mPhone.isEcmCanceledForEmergency() == false) goto L35;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x007e, code lost:
        r19.mPhone.handleTimerInEmergencyCallbackMode(0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x0084, code lost:
        log("poll: hangupPendingMO, hangup conn " + r8);
        hangup(r19.mConnections[r8]);
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x00a0, code lost:
        com.android.telephony.Rlog.e("GsmCdmaCallTracker", "unexpected error on hangup");
     */
    /* JADX WARN: Removed duplicated region for block: B:109:0x029e A[Catch: all -> 0x04b5, TryCatch #1 {, blocks: (B:4:0x0005, B:6:0x0009, B:10:0x0019, B:11:0x002f, B:13:0x0034, B:15:0x0038, B:17:0x0044, B:24:0x0051, B:26:0x0055, B:28:0x005b, B:30:0x006d, B:32:0x0076, B:34:0x007e, B:35:0x0084, B:37:0x00a0, B:123:0x02dc, B:41:0x00ad, B:43:0x00f8, B:45:0x010a, B:47:0x010e, B:49:0x0114, B:51:0x0123, B:53:0x012e, B:54:0x0134, B:56:0x013a, B:58:0x0166, B:60:0x0187, B:50:0x011c, B:61:0x0193, B:63:0x01a0, B:65:0x01a6, B:66:0x01af, B:68:0x01b7, B:77:0x01c9, B:79:0x01cf, B:92:0x0255, B:80:0x01d6, B:81:0x01e0, B:83:0x01e6, B:84:0x020d, B:85:0x0217, B:87:0x021d, B:88:0x0244, B:90:0x024c, B:91:0x0252, B:95:0x0260, B:97:0x0266, B:99:0x026c, B:101:0x0288, B:107:0x0298, B:109:0x029e, B:113:0x02a9, B:116:0x02b6, B:117:0x02ba, B:118:0x02d1, B:124:0x02e2, B:127:0x02ea, B:128:0x02ed, B:130:0x02f1, B:132:0x0320, B:134:0x0324, B:135:0x0326, B:138:0x032e, B:139:0x0333, B:141:0x0344, B:143:0x0353, B:145:0x035d, B:149:0x0365, B:158:0x03b9, B:164:0x03c7, B:150:0x039d, B:156:0x03a8, B:165:0x03cc, B:167:0x03d2, B:168:0x03ea, B:169:0x03f0, B:171:0x03f6, B:173:0x0426, B:175:0x0430, B:174:0x042b, B:176:0x0434, B:178:0x043c, B:183:0x044f, B:185:0x0454, B:187:0x045a, B:188:0x045e, B:190:0x0464, B:191:0x0484, B:197:0x049b, B:199:0x04a3, B:201:0x04ab, B:195:0x048f, B:182:0x044c, B:7:0x000e, B:9:0x0014, B:204:0x04b0), top: B:212:0x0005, inners: #0 }] */
    @Override // com.android.internal.telephony.CallTracker
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    protected synchronized void handlePollCalls(AsyncResult asyncResult) {
        List arrayList;
        GsmCdmaConnection gsmCdmaConnection;
        boolean z;
        Phone imsPhone;
        boolean onDisconnect;
        boolean z2;
        boolean z3;
        DriverCall driverCall;
        List list;
        Connection connection;
        Throwable th = asyncResult.exception;
        if (th == null) {
            arrayList = (List) asyncResult.result;
        } else if (isCommandExceptionRadioNotAvailable(th)) {
            arrayList = new ArrayList();
        } else {
            pollCallsAfterDelay();
            return;
        }
        ArrayList arrayList2 = new ArrayList();
        int size = this.mHandoverConnections.size();
        int size2 = arrayList.size();
        int i = 0;
        boolean z4 = true;
        int i2 = 0;
        boolean z5 = false;
        Connection connection2 = null;
        GsmCdmaConnection gsmCdmaConnection2 = null;
        boolean z6 = false;
        while (true) {
            GsmCdmaConnection[] gsmCdmaConnectionArr = this.mConnections;
            if (i < gsmCdmaConnectionArr.length) {
                GsmCdmaConnection gsmCdmaConnection3 = gsmCdmaConnectionArr[i];
                if (i2 < size2) {
                    driverCall = (DriverCall) arrayList.get(i2);
                    if (driverCall.index == i + 1) {
                        i2++;
                        z4 = (gsmCdmaConnection3 == null || driverCall != null) ? false : false;
                        if (gsmCdmaConnection3 == null || driverCall == null) {
                            list = arrayList;
                            if (gsmCdmaConnection3 == null && driverCall == null) {
                                if (isPhoneTypeGsm()) {
                                    this.mDroppedDuringPoll.add(gsmCdmaConnection3);
                                } else {
                                    Iterator<Connection> it = this.mForegroundCall.getConnections().iterator();
                                    while (it.hasNext()) {
                                        Connection next = it.next();
                                        log("adding fgCall cn " + next + "to droppedDuringPoll");
                                        this.mDroppedDuringPoll.add((GsmCdmaConnection) next);
                                    }
                                    Iterator<Connection> it2 = this.mRingingCall.getConnections().iterator();
                                    while (it2.hasNext()) {
                                        Connection next2 = it2.next();
                                        log("adding rgCall cn " + next2 + "to droppedDuringPoll");
                                        this.mDroppedDuringPoll.add((GsmCdmaConnection) next2);
                                    }
                                    if (this.mPhone.isEcmCanceledForEmergency()) {
                                        this.mPhone.handleTimerInEmergencyCallbackMode(0);
                                    }
                                    checkAndEnableDataCallAfterEmergencyCallDropped();
                                }
                                this.mConnections[i] = null;
                            } else {
                                if (gsmCdmaConnection3 == null && driverCall != null && !gsmCdmaConnection3.compareTo(driverCall) && isPhoneTypeGsm()) {
                                    this.mDroppedDuringPoll.add(gsmCdmaConnection3);
                                    this.mConnections[i] = new GsmCdmaConnection(this.mPhone, driverCall, this, i);
                                    if (this.mConnections[i].getCall() == this.mRingingCall) {
                                        connection = this.mConnections[i];
                                        if (hangupWaitingCallSilently(i)) {
                                            return;
                                        }
                                        connection2 = connection;
                                    }
                                } else if (gsmCdmaConnection3 != null && driverCall != null) {
                                    if (!isPhoneTypeGsm()) {
                                        boolean isIncoming = gsmCdmaConnection3.isIncoming();
                                        boolean z7 = driverCall.isMT;
                                        if (isIncoming != z7) {
                                            if (z7) {
                                                this.mDroppedDuringPoll.add(gsmCdmaConnection3);
                                                connection2 = checkMtFindNewRinging(driverCall, i);
                                                if (connection2 == null) {
                                                    gsmCdmaConnection2 = gsmCdmaConnection3;
                                                    z6 = true;
                                                }
                                                checkAndEnableDataCallAfterEmergencyCallDropped();
                                            } else {
                                                Rlog.e("GsmCdmaCallTracker", "Error in RIL, Phantom call appeared " + driverCall);
                                            }
                                        }
                                    }
                                    boolean update = gsmCdmaConnection3.update(driverCall);
                                    if (!z5 && !update) {
                                        z5 = false;
                                    }
                                }
                                z5 = true;
                            }
                            i++;
                            arrayList = list;
                        } else {
                            GsmCdmaConnection gsmCdmaConnection4 = this.mPendingMO;
                            if (gsmCdmaConnection4 != null && gsmCdmaConnection4.compareTo(driverCall)) {
                                GsmCdmaConnection[] gsmCdmaConnectionArr2 = this.mConnections;
                                GsmCdmaConnection gsmCdmaConnection5 = this.mPendingMO;
                                gsmCdmaConnectionArr2[i] = gsmCdmaConnection5;
                                gsmCdmaConnection5.mIndex = i;
                                gsmCdmaConnection5.update(driverCall);
                                this.mPendingMO = null;
                                if (this.mHangupPendingMO) {
                                    break;
                                }
                                list = arrayList;
                            } else {
                                log("pendingMo=" + this.mPendingMO + ", dc=" + driverCall);
                                this.mConnections[i] = new GsmCdmaConnection(this.mPhone, driverCall, this, i);
                                StringBuilder sb = new StringBuilder();
                                sb.append("New connection is not mPendingMO. Creating new GsmCdmaConnection, objId=");
                                sb.append(System.identityHashCode(this.mConnections[i]));
                                log(sb.toString());
                                Connection hoConnection = getHoConnection(driverCall);
                                if (hoConnection != null) {
                                    log("Handover connection found.");
                                    this.mConnections[i].migrateFrom(hoConnection);
                                    Call.State state = hoConnection.mPreHandoverState;
                                    if (state != Call.State.ACTIVE && state != Call.State.HOLDING && driverCall.state == DriverCall.State.ACTIVE) {
                                        this.mConnections[i].onConnectedInOrOut();
                                    } else {
                                        this.mConnections[i].onConnectedConnectionMigrated();
                                    }
                                    this.mHandoverConnections.remove(hoConnection);
                                    if (isPhoneTypeGsm()) {
                                        Iterator<Connection> it3 = this.mHandoverConnections.iterator();
                                        while (it3.hasNext()) {
                                            Connection next3 = it3.next();
                                            StringBuilder sb2 = new StringBuilder();
                                            List list2 = arrayList;
                                            sb2.append("HO Conn state is ");
                                            sb2.append(next3.mPreHandoverState);
                                            Rlog.i("GsmCdmaCallTracker", sb2.toString());
                                            if (next3.mPreHandoverState == this.mConnections[i].getState()) {
                                                Rlog.i("GsmCdmaCallTracker", "Removing HO conn " + hoConnection + next3.mPreHandoverState);
                                                it3.remove();
                                            }
                                            arrayList = list2;
                                        }
                                    }
                                    list = arrayList;
                                    this.mPhone.notifyHandoverStateChanged(this.mConnections[i]);
                                } else {
                                    list = arrayList;
                                    log("New connection is not mPendingMO nor a pending handover.");
                                    connection = checkMtFindNewRinging(driverCall, i);
                                    if (connection == null) {
                                        if (isPhoneTypeGsm()) {
                                            arrayList2.add(this.mConnections[i]);
                                            connection2 = connection;
                                        } else {
                                            connection2 = connection;
                                            gsmCdmaConnection2 = this.mConnections[i];
                                        }
                                        z6 = true;
                                    } else {
                                        if (hangupWaitingCallSilently(i)) {
                                            return;
                                        }
                                        connection2 = connection;
                                    }
                                }
                            }
                            z5 = true;
                            i++;
                            arrayList = list;
                        }
                    }
                }
                driverCall = null;
                if (gsmCdmaConnection3 == null) {
                }
                if (gsmCdmaConnection3 == null) {
                }
                list = arrayList;
                if (gsmCdmaConnection3 == null) {
                }
                if (gsmCdmaConnection3 == null) {
                }
                if (gsmCdmaConnection3 != null) {
                    if (!isPhoneTypeGsm()) {
                    }
                    boolean update2 = gsmCdmaConnection3.update(driverCall);
                    if (!z5) {
                        z5 = false;
                    }
                    z5 = true;
                }
                i++;
                arrayList = list;
            } else {
                if (!isPhoneTypeGsm() && z4) {
                    checkAndEnableDataCallAfterEmergencyCallDropped();
                }
                if (this.mPendingMO != null) {
                    Rlog.d("GsmCdmaCallTracker", "Pending MO dropped before poll fg state:" + this.mForegroundCall.getState());
                    this.mDroppedDuringPoll.add(this.mPendingMO);
                    gsmCdmaConnection = null;
                    this.mPendingMO = null;
                    z = false;
                    this.mHangupPendingMO = false;
                    if (!isPhoneTypeGsm()) {
                        if (this.mPendingCallInEcm) {
                            this.mPendingCallInEcm = false;
                        }
                        checkAndEnableDataCallAfterEmergencyCallDropped();
                    }
                } else {
                    gsmCdmaConnection = null;
                    z = false;
                }
                if (connection2 != null) {
                    this.mPhone.notifyNewRingingConnection(connection2);
                }
                ArrayList<GsmCdmaConnection> arrayList3 = new ArrayList<>();
                int size3 = this.mDroppedDuringPoll.size() - 1;
                boolean z8 = z;
                while (size3 >= 0) {
                    GsmCdmaConnection gsmCdmaConnection6 = this.mDroppedDuringPoll.get(size3);
                    if (gsmCdmaConnection6.isIncoming() && gsmCdmaConnection6.getConnectTime() == 0) {
                        int i3 = gsmCdmaConnection6.mCause == 3 ? 16 : 1;
                        log("missed/rejected call, conn.cause=" + gsmCdmaConnection6.mCause);
                        log("setting cause to " + i3);
                        this.mDroppedDuringPoll.remove(size3);
                        onDisconnect = z8 | gsmCdmaConnection6.onDisconnect(i3);
                        arrayList3.add(gsmCdmaConnection6);
                    } else {
                        int i4 = gsmCdmaConnection6.mCause;
                        if (i4 != 3 && i4 != 7) {
                            z2 = z8;
                            z3 = z;
                            if (!isPhoneTypeGsm() && z3 && z6 && gsmCdmaConnection6 == gsmCdmaConnection2) {
                                gsmCdmaConnection2 = gsmCdmaConnection;
                                z6 = z;
                            }
                            size3--;
                            z8 = z2;
                        }
                        this.mDroppedDuringPoll.remove(size3);
                        onDisconnect = z8 | gsmCdmaConnection6.onDisconnect(gsmCdmaConnection6.mCause);
                        arrayList3.add(gsmCdmaConnection6);
                    }
                    z2 = onDisconnect;
                    z3 = true;
                    if (!isPhoneTypeGsm()) {
                        gsmCdmaConnection2 = gsmCdmaConnection;
                        z6 = z;
                    }
                    size3--;
                    z8 = z2;
                }
                if (arrayList3.size() > 0) {
                    this.mMetrics.writeRilCallList(this.mPhone.getPhoneId(), arrayList3, getNetworkCountryIso());
                    this.mPhone.getVoiceCallSessionStats().onRilCallListChanged(arrayList3);
                }
                Iterator<Connection> it4 = this.mHandoverConnections.iterator();
                while (it4.hasNext()) {
                    Connection next4 = it4.next();
                    log("handlePollCalls - disconnect hoConn= " + next4 + " hoConn.State= " + next4.getState());
                    if (next4.getState().isRinging()) {
                        next4.onDisconnect(1);
                    } else {
                        next4.onDisconnect(-1);
                    }
                    it4.remove();
                }
                if (this.mDroppedDuringPoll.size() > 0) {
                    this.mCi.getLastCallFailCause(obtainNoPollCompleteMessage(5));
                }
                if (connection2 != null || z5 || z8) {
                    internalClearDisconnected();
                }
                updatePhoneState();
                if (z6) {
                    if (isPhoneTypeGsm()) {
                        Iterator it5 = arrayList2.iterator();
                        while (it5.hasNext()) {
                            Connection connection3 = (Connection) it5.next();
                            log("Notify unknown for " + connection3);
                            this.mPhone.notifyUnknownConnection(connection3);
                        }
                    } else {
                        this.mPhone.notifyUnknownConnection(gsmCdmaConnection2);
                    }
                }
                if (z5 || connection2 != null || z8) {
                    this.mPhone.notifyPreciseCallStateChanged();
                    updateMetrics(this.mConnections);
                }
                if (size > 0 && this.mHandoverConnections.size() == 0 && (imsPhone = this.mPhone.getImsPhone()) != null) {
                    imsPhone.callEndCleanupHandOverCallIfAny();
                }
                return;
            }
        }
    }

    private void updateMetrics(GsmCdmaConnection[] gsmCdmaConnectionArr) {
        ArrayList<GsmCdmaConnection> arrayList = new ArrayList<>();
        for (GsmCdmaConnection gsmCdmaConnection : gsmCdmaConnectionArr) {
            if (gsmCdmaConnection != null) {
                arrayList.add(gsmCdmaConnection);
            }
        }
        this.mMetrics.writeRilCallList(this.mPhone.getPhoneId(), arrayList, getNetworkCountryIso());
        this.mPhone.getVoiceCallSessionStats().onRilCallListChanged(arrayList);
    }

    private void handleRadioNotAvailable() {
        pollCallsWhenSafe();
    }

    public void hangup(GsmCdmaConnection gsmCdmaConnection) throws CallStateException {
        if (gsmCdmaConnection.mOwner != this) {
            throw new CallStateException("GsmCdmaConnection " + gsmCdmaConnection + "does not belong to GsmCdmaCallTracker " + this);
        }
        if (gsmCdmaConnection == this.mPendingMO) {
            log("hangup: set hangupPendingMO to true");
            this.mHangupPendingMO = true;
        } else {
            if (!isPhoneTypeGsm()) {
                GsmCdmaCall call = gsmCdmaConnection.getCall();
                GsmCdmaCall gsmCdmaCall = this.mRingingCall;
                if (call == gsmCdmaCall && gsmCdmaCall.getState() == Call.State.WAITING) {
                    gsmCdmaConnection.onLocalDisconnect();
                    updatePhoneState();
                    this.mPhone.notifyPreciseCallStateChanged();
                    return;
                }
            }
            try {
                this.mMetrics.writeRilHangup(this.mPhone.getPhoneId(), gsmCdmaConnection, gsmCdmaConnection.getGsmCdmaIndex(), getNetworkCountryIso());
                this.mCi.hangupConnection(gsmCdmaConnection.getGsmCdmaIndex(), obtainCompleteMessage());
            } catch (CallStateException unused) {
                Rlog.w("GsmCdmaCallTracker", "GsmCdmaCallTracker WARN: hangup() on absent connection " + gsmCdmaConnection);
            }
        }
        gsmCdmaConnection.onHangupLocal();
    }

    public void separate(GsmCdmaConnection gsmCdmaConnection) throws CallStateException {
        if (gsmCdmaConnection.mOwner != this) {
            throw new CallStateException("GsmCdmaConnection " + gsmCdmaConnection + "does not belong to GsmCdmaCallTracker " + this);
        }
        try {
            this.mCi.separateConnection(gsmCdmaConnection.getGsmCdmaIndex(), obtainCompleteMessage(12));
        } catch (CallStateException unused) {
            Rlog.w("GsmCdmaCallTracker", "GsmCdmaCallTracker WARN: separate() on absent connection " + gsmCdmaConnection);
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void setMute(boolean z) {
        this.mDesiredMute = z;
        this.mCi.setMute(z, null);
    }

    public boolean getMute() {
        return this.mDesiredMute;
    }

    public void hangup(GsmCdmaCall gsmCdmaCall) throws CallStateException {
        if (gsmCdmaCall.getConnectionsCount() == 0) {
            throw new CallStateException("no connections in call");
        }
        GsmCdmaCall gsmCdmaCall2 = this.mRingingCall;
        if (gsmCdmaCall == gsmCdmaCall2) {
            log("(ringing) hangup waiting or background");
            logHangupEvent(gsmCdmaCall);
            this.mCi.hangupWaitingOrBackground(obtainCompleteMessage());
        } else if (gsmCdmaCall == this.mForegroundCall) {
            if (gsmCdmaCall.isDialingOrAlerting()) {
                log("(foregnd) hangup dialing or alerting...");
                hangup((GsmCdmaConnection) gsmCdmaCall.getConnections().get(0));
            } else if (isPhoneTypeGsm() && this.mRingingCall.isRinging()) {
                log("hangup all conns in active/background call, without affecting ringing call");
                hangupAllConnections(gsmCdmaCall);
            } else {
                logHangupEvent(gsmCdmaCall);
                hangupForegroundResumeBackground();
            }
        } else if (gsmCdmaCall == this.mBackgroundCall) {
            if (gsmCdmaCall2.isRinging()) {
                log("hangup all conns in background call");
                hangupAllConnections(gsmCdmaCall);
            } else {
                hangupWaitingOrBackground();
            }
        } else {
            throw new RuntimeException("GsmCdmaCall " + gsmCdmaCall + "does not belong to GsmCdmaCallTracker " + this);
        }
        gsmCdmaCall.onHangupLocal();
        this.mPhone.notifyPreciseCallStateChanged();
    }

    private void logHangupEvent(GsmCdmaCall gsmCdmaCall) {
        int i;
        Iterator<Connection> it = gsmCdmaCall.getConnections().iterator();
        while (it.hasNext()) {
            GsmCdmaConnection gsmCdmaConnection = (GsmCdmaConnection) it.next();
            try {
                i = gsmCdmaConnection.getGsmCdmaIndex();
            } catch (CallStateException unused) {
                i = -1;
            }
            this.mMetrics.writeRilHangup(this.mPhone.getPhoneId(), gsmCdmaConnection, i, getNetworkCountryIso());
        }
    }

    public void hangupWaitingOrBackground() {
        log("hangupWaitingOrBackground");
        logHangupEvent(this.mBackgroundCall);
        this.mCi.hangupWaitingOrBackground(obtainCompleteMessage());
    }

    public void hangupForegroundResumeBackground() {
        log("hangupForegroundResumeBackground");
        this.mCi.hangupForegroundResumeBackground(obtainCompleteMessage());
    }

    public void hangupConnectionByIndex(GsmCdmaCall gsmCdmaCall, int i) throws CallStateException {
        Iterator<Connection> it = gsmCdmaCall.getConnections().iterator();
        while (it.hasNext()) {
            GsmCdmaConnection gsmCdmaConnection = (GsmCdmaConnection) it.next();
            if (!gsmCdmaConnection.mDisconnected && gsmCdmaConnection.getGsmCdmaIndex() == i) {
                this.mMetrics.writeRilHangup(this.mPhone.getPhoneId(), gsmCdmaConnection, gsmCdmaConnection.getGsmCdmaIndex(), getNetworkCountryIso());
                this.mCi.hangupConnection(i, obtainCompleteMessage());
                return;
            }
        }
        throw new CallStateException("no GsmCdma index found");
    }

    public void hangupAllConnections(GsmCdmaCall gsmCdmaCall) {
        try {
            Iterator<Connection> it = gsmCdmaCall.getConnections().iterator();
            while (it.hasNext()) {
                GsmCdmaConnection gsmCdmaConnection = (GsmCdmaConnection) it.next();
                if (!gsmCdmaConnection.mDisconnected) {
                    this.mMetrics.writeRilHangup(this.mPhone.getPhoneId(), gsmCdmaConnection, gsmCdmaConnection.getGsmCdmaIndex(), getNetworkCountryIso());
                    this.mCi.hangupConnection(gsmCdmaConnection.getGsmCdmaIndex(), obtainCompleteMessage());
                }
            }
        } catch (CallStateException e) {
            Rlog.e("GsmCdmaCallTracker", "hangupConnectionByIndex caught " + e);
        }
    }

    public GsmCdmaConnection getConnectionByIndex(GsmCdmaCall gsmCdmaCall, int i) throws CallStateException {
        Iterator<Connection> it = gsmCdmaCall.getConnections().iterator();
        while (it.hasNext()) {
            GsmCdmaConnection gsmCdmaConnection = (GsmCdmaConnection) it.next();
            if (!gsmCdmaConnection.mDisconnected && gsmCdmaConnection.getGsmCdmaIndex() == i) {
                return gsmCdmaConnection;
            }
        }
        return null;
    }

    private void notifyCallWaitingInfo(CdmaCallWaitingNotification cdmaCallWaitingNotification) {
        RegistrantList registrantList = this.mCallWaitingRegistrants;
        if (registrantList != null) {
            registrantList.notifyRegistrants(new AsyncResult((Object) null, cdmaCallWaitingNotification, (Throwable) null));
        }
    }

    private void handleCallWaitingInfo(CdmaCallWaitingNotification cdmaCallWaitingNotification) {
        new GsmCdmaConnection(this.mPhone.getContext(), cdmaCallWaitingNotification, this, this.mRingingCall);
        updatePhoneState();
        notifyCallWaitingInfo(cdmaCallWaitingNotification);
    }

    private PhoneInternalInterface.SuppService getFailedService(int i) {
        if (i == 8) {
            return PhoneInternalInterface.SuppService.SWITCH;
        }
        switch (i) {
            case 11:
                return PhoneInternalInterface.SuppService.CONFERENCE;
            case 12:
                return PhoneInternalInterface.SuppService.SEPARATE;
            case 13:
                return PhoneInternalInterface.SuppService.TRANSFER;
            default:
                return PhoneInternalInterface.SuppService.UNKNOWN;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.android.internal.telephony.CallTracker, android.os.Handler
    public void handleMessage(Message message) {
        int i;
        int i2;
        Connection latestConnection;
        Connection latestConnection2;
        int i3 = message.what;
        String str = null;
        if (i3 == 1) {
            Rlog.d("GsmCdmaCallTracker", "Event EVENT_POLL_CALLS_RESULT Received");
            if (message == this.mLastRelevantPoll) {
                this.mNeedsPoll = false;
                this.mLastRelevantPoll = null;
                handlePollCalls((AsyncResult) message.obj);
            }
        } else if (i3 == 2 || i3 == 3) {
            pollCallsWhenSafe();
        } else if (i3 == 4) {
            operationComplete();
        } else if (i3 == 5) {
            AsyncResult asyncResult = (AsyncResult) message.obj;
            operationComplete();
            Throwable th = asyncResult.exception;
            if (th != null) {
                i = 16;
                if (th instanceof CommandException) {
                    CommandException commandException = (CommandException) th;
                    int i4 = C00304.$SwitchMap$com$android$internal$telephony$CommandException$Error[commandException.getCommandError().ordinal()];
                    if (i4 == 1 || i4 == 2 || i4 == 3 || i4 == 4) {
                        str = commandException.getCommandError().toString();
                        i = 65535;
                    }
                } else {
                    Rlog.i("GsmCdmaCallTracker", "Exception during getLastCallFailCause, assuming normal disconnect");
                }
            } else {
                LastCallFailCause lastCallFailCause = (LastCallFailCause) asyncResult.result;
                i = lastCallFailCause.causeCode;
                str = lastCallFailCause.vendorCause;
            }
            if (i == 34 || i == 41 || i == 42 || i == 44 || i == 49 || i == 58 || i == 65535) {
                CellLocation asCellLocation = this.mPhone.getCurrentCellIdentity().asCellLocation();
                if (asCellLocation != null) {
                    if (asCellLocation instanceof GsmCellLocation) {
                        i2 = ((GsmCellLocation) asCellLocation).getCid();
                    } else if (asCellLocation instanceof CdmaCellLocation) {
                        i2 = ((CdmaCellLocation) asCellLocation).getBaseStationId();
                    }
                    EventLog.writeEvent((int) EventLogTags.CALL_DROP, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(TelephonyManager.getDefault().getNetworkType()));
                }
                i2 = -1;
                EventLog.writeEvent((int) EventLogTags.CALL_DROP, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(TelephonyManager.getDefault().getNetworkType()));
            }
            if (isEmcRetryCause(i) && this.mPhone.useImsForEmergency()) {
                Iterator<Connection> it = this.mForegroundCall.mConnections.iterator();
                String str2 = PhoneConfigurationManager.SSSS;
                while (it.hasNext()) {
                    GsmCdmaConnection gsmCdmaConnection = (GsmCdmaConnection) it.next();
                    String origDialString = gsmCdmaConnection.getOrigDialString();
                    gsmCdmaConnection.getCall().detach(gsmCdmaConnection);
                    this.mDroppedDuringPoll.remove(gsmCdmaConnection);
                    str2 = origDialString;
                }
                this.mPhone.notifyVolteSilentRedial(str2, i);
                updatePhoneState();
                if (this.mDroppedDuringPoll.isEmpty()) {
                    log("LAST_CALL_FAIL_CAUSE - no Dropped normal Call");
                    return;
                }
            }
            int size = this.mDroppedDuringPoll.size();
            for (int i5 = 0; i5 < size; i5++) {
                this.mDroppedDuringPoll.get(i5).onRemoteDisconnect(i, str);
            }
            updatePhoneState();
            this.mPhone.notifyPreciseCallStateChanged();
            this.mMetrics.writeRilCallList(this.mPhone.getPhoneId(), this.mDroppedDuringPoll, getNetworkCountryIso());
            this.mPhone.getVoiceCallSessionStats().onRilCallListChanged(this.mDroppedDuringPoll);
            this.mDroppedDuringPoll.clear();
        } else if (i3 != 20) {
            switch (i3) {
                case 8:
                case 12:
                case 13:
                    break;
                case 9:
                    handleRadioAvailable();
                    return;
                case 10:
                    handleRadioNotAvailable();
                    return;
                case 11:
                    if (isPhoneTypeGsm() && ((AsyncResult) message.obj).exception != null && (latestConnection2 = this.mForegroundCall.getLatestConnection()) != null) {
                        latestConnection2.onConferenceMergeFailed();
                        break;
                    }
                    break;
                case 14:
                    if (!isPhoneTypeGsm()) {
                        if (this.mPendingCallInEcm) {
                            this.mCi.dial(this.mPendingMO.getAddress(), this.mPendingMO.isEmergencyCall(), this.mPendingMO.getEmergencyNumberInfo(), this.mPendingMO.hasKnownUserIntentEmergency(), this.mPendingCallClirMode, obtainCompleteMessage());
                            this.mPendingCallInEcm = false;
                        }
                        this.mPhone.unsetOnEcbModeExitResponse(this);
                        return;
                    }
                    throw new RuntimeException("unexpected event " + message.what + " not handled by phone type " + this.mPhone.getPhoneType());
                case 15:
                    if (!isPhoneTypeGsm()) {
                        AsyncResult asyncResult2 = (AsyncResult) message.obj;
                        if (asyncResult2.exception == null) {
                            handleCallWaitingInfo((CdmaCallWaitingNotification) asyncResult2.result);
                            Rlog.d("GsmCdmaCallTracker", "Event EVENT_CALL_WAITING_INFO_CDMA Received");
                            return;
                        }
                        return;
                    }
                    throw new RuntimeException("unexpected event " + message.what + " not handled by phone type " + this.mPhone.getPhoneType());
                case 16:
                    if (!isPhoneTypeGsm()) {
                        if (((AsyncResult) message.obj).exception == null) {
                            this.mPendingMO.onConnectedInOrOut();
                            this.mPendingMO = null;
                            return;
                        }
                        return;
                    }
                    throw new RuntimeException("unexpected event " + message.what + " not handled by phone type " + this.mPhone.getPhoneType());
                default:
                    throw new RuntimeException("unexpected event " + message.what + " not handled by phone type " + this.mPhone.getPhoneType());
            }
            if (isPhoneTypeGsm()) {
                if (((AsyncResult) message.obj).exception != null) {
                    if (message.what == 8 && (latestConnection = this.mForegroundCall.getLatestConnection()) != null) {
                        if (this.mBackgroundCall.getState() != Call.State.HOLDING) {
                            latestConnection.onConnectionEvent("android.telecom.event.CALL_HOLD_FAILED", null);
                        } else {
                            latestConnection.onConnectionEvent("android.telecom.event.CALL_SWITCH_FAILED", null);
                        }
                    }
                    this.mPhone.notifySuppServiceFailed(getFailedService(message.what));
                }
                operationComplete();
            } else if (message.what != 8) {
                throw new RuntimeException("unexpected event " + message.what + " not handled by phone type " + this.mPhone.getPhoneType());
            }
        } else if (!isPhoneTypeGsm()) {
            if (((AsyncResult) message.obj).exception == null) {
                postDelayed(new Runnable() { // from class: com.android.internal.telephony.GsmCdmaCallTracker.3
                    @Override // java.lang.Runnable
                    public void run() {
                        if (GsmCdmaCallTracker.this.mPendingMO != null) {
                            GsmCdmaCallTracker gsmCdmaCallTracker = GsmCdmaCallTracker.this;
                            gsmCdmaCallTracker.mCi.sendCDMAFeatureCode(gsmCdmaCallTracker.mPendingMO.getAddress(), GsmCdmaCallTracker.this.obtainMessage(16));
                        }
                    }
                }, this.m3WayCallFlashDelay);
                return;
            }
            this.mPendingMO = null;
            Rlog.w("GsmCdmaCallTracker", "exception happened on Blank Flash for 3-way call");
        } else {
            throw new RuntimeException("unexpected event " + message.what + " not handled by phone type " + this.mPhone.getPhoneType());
        }
    }

    /* renamed from: com.android.internal.telephony.GsmCdmaCallTracker$4 */
    /* loaded from: classes.dex */
    static /* synthetic */ class C00304 {
        static final /* synthetic */ int[] $SwitchMap$com$android$internal$telephony$CommandException$Error;

        static {
            int[] iArr = new int[CommandException.Error.values().length];
            $SwitchMap$com$android$internal$telephony$CommandException$Error = iArr;
            try {
                iArr[CommandException.Error.RADIO_NOT_AVAILABLE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.NO_MEMORY.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.INTERNAL_ERR.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.NO_RESOURCES.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    public void dispatchCsCallRadioTech(int i) {
        GsmCdmaConnection[] gsmCdmaConnectionArr = this.mConnections;
        if (gsmCdmaConnectionArr == null) {
            log("dispatchCsCallRadioTech: mConnections is null");
            return;
        }
        for (GsmCdmaConnection gsmCdmaConnection : gsmCdmaConnectionArr) {
            if (gsmCdmaConnection != null) {
                gsmCdmaConnection.setCallRadioTech(i);
            }
        }
    }

    private void checkAndEnableDataCallAfterEmergencyCallDropped() {
        if (this.mIsInEmergencyCall) {
            this.mIsInEmergencyCall = false;
            boolean isInEcm = this.mPhone.isInEcm();
            log("checkAndEnableDataCallAfterEmergencyCallDropped,inEcm=" + isInEcm);
            if (!isInEcm) {
                this.mPhone.notifyEmergencyCallRegistrants(false);
            }
            this.mPhone.sendEmergencyCallStateChange(false);
        }
    }

    private Connection checkMtFindNewRinging(DriverCall driverCall, int i) {
        if (this.mConnections[i].getCall() == this.mRingingCall) {
            GsmCdmaConnection gsmCdmaConnection = this.mConnections[i];
            log("Notify new ring " + driverCall);
            return gsmCdmaConnection;
        }
        Rlog.e("GsmCdmaCallTracker", "Phantom call appeared " + driverCall);
        DriverCall.State state = driverCall.state;
        if (state != DriverCall.State.ALERTING && state != DriverCall.State.DIALING) {
            this.mConnections[i].onConnectedInOrOut();
            if (driverCall.state == DriverCall.State.HOLDING) {
                this.mConnections[i].onStartedHolding();
            }
        }
        return null;
    }

    public boolean isInEmergencyCall() {
        return this.mIsInEmergencyCall;
    }

    public boolean isInOtaspCall() {
        GsmCdmaConnection gsmCdmaConnection = this.mPendingMO;
        return (gsmCdmaConnection != null && gsmCdmaConnection.isOtaspCall()) || this.mForegroundCall.getConnections().stream().filter(new Predicate() { // from class: com.android.internal.telephony.GsmCdmaCallTracker$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isInOtaspCall$0;
                lambda$isInOtaspCall$0 = GsmCdmaCallTracker.lambda$isInOtaspCall$0((Connection) obj);
                return lambda$isInOtaspCall$0;
            }
        }).count() > 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$isInOtaspCall$0(Connection connection) {
        return (connection instanceof GsmCdmaConnection) && ((GsmCdmaConnection) connection).isOtaspCall();
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean isPhoneTypeGsm() {
        return this.mPhone.getPhoneType() == 1;
    }

    @Override // com.android.internal.telephony.CallTracker
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public GsmCdmaPhone getPhone() {
        return this.mPhone;
    }

    private boolean isEmcRetryCause(int i) {
        if (!DomainSelectionResolver.getInstance().isDomainSelectionSupported()) {
            return i == 3001 || i == 3002;
        }
        log("isEmcRetryCause AP based domain selection ignores the cause");
        return false;
    }

    @Override // com.android.internal.telephony.CallTracker
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void log(String str) {
        Rlog.d("GsmCdmaCallTracker", "[" + this.mPhone.getPhoneId() + "] " + str);
    }

    @Override // com.android.internal.telephony.CallTracker
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("GsmCdmaCallTracker extends:");
        super.dump(fileDescriptor, printWriter, strArr);
        printWriter.println("mConnections: length=" + this.mConnections.length);
        for (int i = 0; i < this.mConnections.length; i++) {
            printWriter.printf("  mConnections[%d]=%s\n", Integer.valueOf(i), this.mConnections[i]);
        }
        printWriter.println(" mVoiceCallEndedRegistrants=" + this.mVoiceCallEndedRegistrants);
        printWriter.println(" mVoiceCallStartedRegistrants=" + this.mVoiceCallStartedRegistrants);
        if (!isPhoneTypeGsm()) {
            printWriter.println(" mCallWaitingRegistrants=" + this.mCallWaitingRegistrants);
        }
        printWriter.println(" mDroppedDuringPoll: size=" + this.mDroppedDuringPoll.size());
        for (int i2 = 0; i2 < this.mDroppedDuringPoll.size(); i2++) {
            printWriter.printf("  mDroppedDuringPoll[%d]=%s\n", Integer.valueOf(i2), this.mDroppedDuringPoll.get(i2));
        }
        printWriter.println(" mRingingCall=" + this.mRingingCall);
        printWriter.println(" mForegroundCall=" + this.mForegroundCall);
        printWriter.println(" mBackgroundCall=" + this.mBackgroundCall);
        printWriter.println(" mPendingMO=" + this.mPendingMO);
        printWriter.println(" mHangupPendingMO=" + this.mHangupPendingMO);
        printWriter.println(" mPhone=" + this.mPhone);
        printWriter.println(" mDesiredMute=" + this.mDesiredMute);
        printWriter.println(" mState=" + this.mState);
        if (isPhoneTypeGsm()) {
            return;
        }
        printWriter.println(" mPendingCallInEcm=" + this.mPendingCallInEcm);
        printWriter.println(" mIsInEmergencyCall=" + this.mIsInEmergencyCall);
        printWriter.println(" mPendingCallClirMode=" + this.mPendingCallClirMode);
    }

    @Override // com.android.internal.telephony.CallTracker
    public PhoneConstants.State getState() {
        return this.mState;
    }

    public int getMaxConnectionsPerCall() {
        return this.mPhone.isPhoneTypeGsm() ? 5 : 1;
    }

    private String getNetworkCountryIso() {
        ServiceStateTracker serviceStateTracker;
        LocaleTracker localeTracker;
        GsmCdmaPhone gsmCdmaPhone = this.mPhone;
        return (gsmCdmaPhone == null || (serviceStateTracker = gsmCdmaPhone.getServiceStateTracker()) == null || (localeTracker = serviceStateTracker.getLocaleTracker()) == null) ? PhoneConfigurationManager.SSSS : localeTracker.getCurrentCountry();
    }

    @Override // com.android.internal.telephony.CallTracker
    public void cleanupCalls() {
        pollCallsWhenSafe();
    }

    private boolean hangupWaitingCallSilently(int i) {
        GsmCdmaConnection gsmCdmaConnection;
        if (i >= 0) {
            GsmCdmaConnection[] gsmCdmaConnectionArr = this.mConnections;
            if (i < gsmCdmaConnectionArr.length && (gsmCdmaConnection = gsmCdmaConnectionArr[i]) != null && this.mPhone.getTerminalBasedCallWaitingState(true) == 0 && gsmCdmaConnection.getState() == Call.State.WAITING) {
                Rlog.d("GsmCdmaCallTracker", "hangupWaitingCallSilently");
                gsmCdmaConnection.dispose();
                this.mConnections[i] = null;
                this.mCi.hangupWaitingOrBackground(obtainCompleteMessage());
                return true;
            }
        }
        return false;
    }
}
