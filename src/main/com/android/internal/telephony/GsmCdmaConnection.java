package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.telephony.CarrierConfigManager;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import com.android.internal.telephony.Call;
import com.android.internal.telephony.Connection;
import com.android.internal.telephony.DriverCall;
import com.android.internal.telephony.PhoneInternalInterface;
import com.android.internal.telephony.cdma.CdmaCallWaitingNotification;
import com.android.internal.telephony.emergency.EmergencyNumberTracker;
import com.android.internal.telephony.metrics.TelephonyMetrics;
import com.android.internal.telephony.uicc.IccCardApplicationStatus;
import com.android.internal.telephony.uicc.UiccCardApplication;
import com.android.telephony.Rlog;
import java.util.ArrayList;
import java.util.Collections;
/* loaded from: classes.dex */
public class GsmCdmaConnection extends Connection {
    public static final String OTASP_NUMBER = "*22899";
    long mDisconnectTime;
    boolean mDisconnected;
    private int mDtmfToneDelay;
    Handler mHandler;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    int mIndex;
    private TelephonyMetrics mMetrics;
    Connection mOrigConnection;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    GsmCdmaCallTracker mOwner;
    GsmCdmaCall mParent;
    private PowerManager.WakeLock mPartialWakeLock;
    int mPreciseCause;
    UUSInfo mUusInfo;
    String mVendorCause;

    private int getAudioQualityFromDC(int i) {
        return (i == 2 || i == 9) ? 2 : 1;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private static boolean isPause(char c) {
        return c == ',';
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private static boolean isWait(char c) {
        return c == ';';
    }

    private static boolean isWild(char c) {
        return c == 'N';
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private String maskDialString(String str) {
        return "<MASKED>";
    }

    /* loaded from: classes.dex */
    class MyHandler extends Handler {
        MyHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i != 1) {
                if (i != 2 && i != 3) {
                    if (i == 4) {
                        GsmCdmaConnection.this.releaseWakeLock();
                        return;
                    } else if (i != 5) {
                        return;
                    }
                }
                GsmCdmaConnection.this.processNextPostDialChar();
                return;
            }
            Handler handler = GsmCdmaConnection.this.mHandler;
            handler.sendMessageDelayed(handler.obtainMessage(5), GsmCdmaConnection.this.mDtmfToneDelay);
        }
    }

    public GsmCdmaConnection(GsmCdmaPhone gsmCdmaPhone, DriverCall driverCall, GsmCdmaCallTracker gsmCdmaCallTracker, int i) {
        super(gsmCdmaPhone.getPhoneType());
        this.mPreciseCause = 0;
        this.mDtmfToneDelay = 0;
        this.mMetrics = TelephonyMetrics.getInstance();
        createWakeLock(gsmCdmaPhone.getContext());
        acquireWakeLock();
        this.mOwner = gsmCdmaCallTracker;
        this.mHandler = new MyHandler(this.mOwner.getLooper());
        this.mAddress = driverCall.number;
        setEmergencyCallInfo(this.mOwner);
        String str = TextUtils.isEmpty(driverCall.forwardedNumber) ? null : driverCall.forwardedNumber;
        Rlog.i("GsmCdmaConnection", "create, forwardedNumber=" + Rlog.pii("GsmCdmaConnection", str));
        this.mForwardedNumber = str != null ? new ArrayList<>(Collections.singletonList(driverCall.forwardedNumber)) : null;
        this.mIsIncoming = driverCall.isMT;
        this.mCreateTime = System.currentTimeMillis();
        this.mCnapName = driverCall.name;
        this.mCnapNamePresentation = driverCall.namePresentation;
        this.mNumberPresentation = driverCall.numberPresentation;
        this.mUusInfo = driverCall.uusInfo;
        this.mIndex = i;
        GsmCdmaCall parentFromDCState = parentFromDCState(driverCall.state);
        this.mParent = parentFromDCState;
        parentFromDCState.attach(this, driverCall);
        fetchDtmfToneDelay(gsmCdmaPhone);
        setAudioQuality(getAudioQualityFromDC(driverCall.audioQuality));
        setCallRadioTech(this.mOwner.getPhone().getCsCallRadioTech());
    }

    public GsmCdmaConnection(GsmCdmaPhone gsmCdmaPhone, String str, GsmCdmaCallTracker gsmCdmaCallTracker, GsmCdmaCall gsmCdmaCall, PhoneInternalInterface.DialArgs dialArgs) {
        super(gsmCdmaPhone.getPhoneType());
        this.mPreciseCause = 0;
        this.mDtmfToneDelay = 0;
        this.mMetrics = TelephonyMetrics.getInstance();
        createWakeLock(gsmCdmaPhone.getContext());
        acquireWakeLock();
        this.mOwner = gsmCdmaCallTracker;
        this.mHandler = new MyHandler(this.mOwner.getLooper());
        this.mDialString = str;
        if (!isPhoneTypeGsm()) {
            Rlog.d("GsmCdmaConnection", "[GsmCdmaConn] GsmCdmaConnection: dialString=" + maskDialString(str));
            str = formatDialString(str);
            Rlog.d("GsmCdmaConnection", "[GsmCdmaConn] GsmCdmaConnection:formated dialString=" + maskDialString(str));
        }
        this.mAddress = PhoneNumberUtils.extractNetworkPortionAlt(str);
        if (dialArgs.isEmergency) {
            setEmergencyCallInfo(this.mOwner);
            if (getEmergencyNumberInfo() == null) {
                setNonDetectableEmergencyCallInfo(dialArgs.eccCategory);
            }
        }
        this.mPostDialString = PhoneNumberUtils.extractPostDialPortion(str);
        this.mIndex = -1;
        this.mIsIncoming = false;
        this.mCnapName = null;
        this.mCnapNamePresentation = 1;
        this.mNumberPresentation = 1;
        this.mCreateTime = System.currentTimeMillis();
        if (gsmCdmaCall != null) {
            this.mParent = gsmCdmaCall;
            if (isPhoneTypeGsm()) {
                gsmCdmaCall.attachFake(this, Call.State.DIALING);
            } else {
                Call.State state = gsmCdmaCall.mState;
                Call.State state2 = Call.State.ACTIVE;
                if (state == state2) {
                    gsmCdmaCall.attachFake(this, state2);
                } else {
                    gsmCdmaCall.attachFake(this, Call.State.DIALING);
                }
            }
        }
        fetchDtmfToneDelay(gsmCdmaPhone);
        setCallRadioTech(this.mOwner.getPhone().getCsCallRadioTech());
    }

    public GsmCdmaConnection(Context context, CdmaCallWaitingNotification cdmaCallWaitingNotification, GsmCdmaCallTracker gsmCdmaCallTracker, GsmCdmaCall gsmCdmaCall) {
        super(gsmCdmaCall.getPhone().getPhoneType());
        this.mPreciseCause = 0;
        this.mDtmfToneDelay = 0;
        this.mMetrics = TelephonyMetrics.getInstance();
        createWakeLock(context);
        acquireWakeLock();
        this.mOwner = gsmCdmaCallTracker;
        this.mHandler = new MyHandler(this.mOwner.getLooper());
        this.mAddress = cdmaCallWaitingNotification.number;
        this.mNumberPresentation = cdmaCallWaitingNotification.numberPresentation;
        this.mCnapName = cdmaCallWaitingNotification.name;
        this.mCnapNamePresentation = cdmaCallWaitingNotification.namePresentation;
        this.mIndex = -1;
        this.mIsIncoming = true;
        this.mCreateTime = System.currentTimeMillis();
        this.mConnectTime = 0L;
        this.mParent = gsmCdmaCall;
        gsmCdmaCall.attachFake(this, Call.State.WAITING);
        setCallRadioTech(this.mOwner.getPhone().getCsCallRadioTech());
    }

    public void dispose() {
        clearPostDialListeners();
        GsmCdmaCall gsmCdmaCall = this.mParent;
        if (gsmCdmaCall != null) {
            gsmCdmaCall.detach(this);
        }
        releaseAllWakeLocks();
    }

    static boolean equalsHandlesNulls(Object obj, Object obj2) {
        if (obj == null) {
            return obj2 == null;
        }
        return obj.equals(obj2);
    }

    static boolean equalsBaseDialString(String str, String str2) {
        if (str == null) {
            if (str2 == null) {
                return true;
            }
        } else if (str2 != null && str.startsWith(str2)) {
            return true;
        }
        return false;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public static String formatDialString(String str) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < length) {
            char charAt = str.charAt(i);
            if (isPause(charAt) || isWait(charAt)) {
                int i2 = length - 1;
                if (i < i2) {
                    int findNextPCharOrNonPOrNonWCharIndex = findNextPCharOrNonPOrNonWCharIndex(str, i);
                    if (findNextPCharOrNonPOrNonWCharIndex < length) {
                        sb.append(findPOrWCharToAppend(str, i, findNextPCharOrNonPOrNonWCharIndex));
                        if (findNextPCharOrNonPOrNonWCharIndex > i + 1) {
                            i = findNextPCharOrNonPOrNonWCharIndex - 1;
                        }
                    } else if (findNextPCharOrNonPOrNonWCharIndex == length) {
                        i = i2;
                    }
                }
            } else {
                sb.append(charAt);
            }
            i++;
        }
        return PhoneNumberUtils.cdmaCheckAndProcessPlusCode(sb.toString());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean compareTo(DriverCall driverCall) {
        if (this.mIsIncoming || driverCall.isMT) {
            if (!isPhoneTypeGsm() || this.mOrigConnection == null) {
                return this.mIsIncoming == driverCall.isMT && equalsHandlesNulls(this.mAddress, PhoneNumberUtils.stringFromStringAndTOA(driverCall.number, driverCall.TOA));
            }
            return true;
        }
        return true;
    }

    @Override // com.android.internal.telephony.Connection
    public String getOrigDialString() {
        return this.mDialString;
    }

    @Override // com.android.internal.telephony.Connection
    public GsmCdmaCall getCall() {
        return this.mParent;
    }

    @Override // com.android.internal.telephony.Connection
    public long getDisconnectTime() {
        return this.mDisconnectTime;
    }

    @Override // com.android.internal.telephony.Connection
    public long getHoldDurationMillis() {
        if (getState() != Call.State.HOLDING) {
            return 0L;
        }
        return SystemClock.elapsedRealtime() - this.mHoldingStartTime;
    }

    @Override // com.android.internal.telephony.Connection
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public Call.State getState() {
        if (this.mDisconnected) {
            return Call.State.DISCONNECTED;
        }
        return super.getState();
    }

    @Override // com.android.internal.telephony.Connection
    public void hangup() throws CallStateException {
        if (!this.mDisconnected) {
            this.mOwner.hangup(this);
            return;
        }
        throw new CallStateException("disconnected");
    }

    @Override // com.android.internal.telephony.Connection
    public void deflect(String str) throws CallStateException {
        throw new CallStateException("deflect is not supported for CS");
    }

    @Override // com.android.internal.telephony.Connection
    public void transfer(String str, boolean z) throws CallStateException {
        throw new CallStateException("Transfer is not supported for CS");
    }

    @Override // com.android.internal.telephony.Connection
    public void consultativeTransfer(Connection connection) throws CallStateException {
        throw new CallStateException("Transfer is not supported for CS");
    }

    @Override // com.android.internal.telephony.Connection
    public void separate() throws CallStateException {
        if (!this.mDisconnected) {
            this.mOwner.separate(this);
            return;
        }
        throw new CallStateException("disconnected");
    }

    @Override // com.android.internal.telephony.Connection
    public void proceedAfterWaitChar() {
        if (this.mPostDialState != Connection.PostDialState.WAIT) {
            Rlog.w("GsmCdmaConnection", "GsmCdmaConnection.proceedAfterWaitChar(): Expected getPostDialState() to be WAIT but was " + this.mPostDialState);
            return;
        }
        setPostDialState(Connection.PostDialState.STARTED);
        processNextPostDialChar();
    }

    @Override // com.android.internal.telephony.Connection
    public void proceedAfterWildChar(String str) {
        if (this.mPostDialState != Connection.PostDialState.WILD) {
            Rlog.w("GsmCdmaConnection", "GsmCdmaConnection.proceedAfterWaitChar(): Expected getPostDialState() to be WILD but was " + this.mPostDialState);
            return;
        }
        setPostDialState(Connection.PostDialState.STARTED);
        this.mPostDialString = str + this.mPostDialString.substring(this.mNextPostDialChar);
        this.mNextPostDialChar = 0;
        log("proceedAfterWildChar: new postDialString is " + this.mPostDialString);
        processNextPostDialChar();
    }

    @Override // com.android.internal.telephony.Connection
    public void cancelPostDial() {
        setPostDialState(Connection.PostDialState.CANCELLED);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onHangupLocal() {
        this.mCause = 3;
        this.mPreciseCause = 0;
        this.mVendorCause = null;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int disconnectCauseFromCode(int i) {
        IccCardApplicationStatus.AppState appState;
        if (i == 41 || i == 42) {
            return 5;
        }
        if (i != 240) {
            if (i != 241) {
                if (i != 325) {
                    if (i != 326) {
                        switch (i) {
                            case 1:
                                return 25;
                            case 8:
                                return 20;
                            case 17:
                                return 4;
                            case 19:
                                return 13;
                            case 31:
                                return 65;
                            case 34:
                            case 44:
                            case 49:
                            case 58:
                                return 5;
                            case 68:
                                return 15;
                            case CallFailCause.NO_VALID_SIM /* 249 */:
                                return 19;
                            case CallFailCause.LOCAL_NETWORK_NO_SERVICE /* 1207 */:
                            case CallFailCause.LOCAL_SERVICE_UNAVAILABLE /* 1211 */:
                                return 18;
                            default:
                                switch (i) {
                                    case CallFailCause.IMEI_NOT_ACCEPTED /* 243 */:
                                        return 58;
                                    case CallFailCause.DIAL_MODIFIED_TO_USSD /* 244 */:
                                        return 46;
                                    case CallFailCause.DIAL_MODIFIED_TO_SS /* 245 */:
                                        return 47;
                                    case CallFailCause.DIAL_MODIFIED_TO_DIAL /* 246 */:
                                        return 48;
                                    case CallFailCause.RADIO_OFF /* 247 */:
                                        return 17;
                                    default:
                                        switch (i) {
                                            case 1000:
                                                return 26;
                                            case 1001:
                                                return 27;
                                            case 1002:
                                                return 28;
                                            case 1003:
                                                return 29;
                                            case 1004:
                                                return 30;
                                            case 1005:
                                                return 31;
                                            case 1006:
                                                return 32;
                                            case CallFailCause.CDMA_PREEMPTED /* 1007 */:
                                                return 33;
                                            case CallFailCause.CDMA_NOT_EMERGENCY /* 1008 */:
                                                return 34;
                                            case CallFailCause.CDMA_ACCESS_BLOCKED /* 1009 */:
                                                return 35;
                                            default:
                                                GsmCdmaPhone phone = this.mOwner.getPhone();
                                                int state = phone.getServiceState().getState();
                                                UiccCardApplication uiccCardApplication = phone.getUiccCardApplication();
                                                if (uiccCardApplication != null) {
                                                    appState = uiccCardApplication.getState();
                                                } else {
                                                    appState = IccCardApplicationStatus.AppState.APPSTATE_UNKNOWN;
                                                }
                                                if (state == 3) {
                                                    return 17;
                                                }
                                                if (!isEmergencyCall()) {
                                                    if (state == 1 || state == 2) {
                                                        return 18;
                                                    }
                                                    if (appState != IccCardApplicationStatus.AppState.APPSTATE_READY && (isPhoneTypeGsm() || phone.mCdmaSubscriptionSource == 0)) {
                                                        return 19;
                                                    }
                                                }
                                                if (isPhoneTypeGsm() && (i == 65535 || i == 260)) {
                                                    if (phone.mSST.mRestrictedState.isCsRestricted()) {
                                                        return 22;
                                                    }
                                                    if (phone.mSST.mRestrictedState.isCsEmergencyRestricted()) {
                                                        return 24;
                                                    }
                                                    if (phone.mSST.mRestrictedState.isCsNormalRestricted()) {
                                                        return 23;
                                                    }
                                                }
                                                return i == 16 ? 2 : 36;
                                        }
                                }
                        }
                    }
                    return 64;
                }
                return 63;
            }
            return 21;
        }
        return 20;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onRemoteDisconnect(int i, String str) {
        this.mPreciseCause = i;
        this.mVendorCause = str;
        onDisconnect(disconnectCauseFromCode(i));
    }

    @Override // com.android.internal.telephony.Connection
    public boolean onDisconnect(int i) {
        this.mCause = i;
        if (!this.mDisconnected) {
            doDisconnect();
            Rlog.d("GsmCdmaConnection", "onDisconnect: cause=" + i);
            this.mOwner.getPhone().notifyDisconnect(this);
            notifyDisconnect(i);
            GsmCdmaCall gsmCdmaCall = this.mParent;
            r1 = gsmCdmaCall != null ? gsmCdmaCall.connectionDisconnected(this) : false;
            this.mOrigConnection = null;
        }
        clearPostDialListeners();
        releaseWakeLock();
        return r1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onLocalDisconnect() {
        if (!this.mDisconnected) {
            doDisconnect();
            GsmCdmaCall gsmCdmaCall = this.mParent;
            if (gsmCdmaCall != null) {
                gsmCdmaCall.detach(this);
            }
        }
        releaseWakeLock();
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x007b  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x00a1  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x00cc  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00ce  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x00ed  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x0100  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x0110  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x011d  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x014e  */
    /* JADX WARN: Removed duplicated region for block: B:57:0x015a  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x017d  */
    /* JADX WARN: Removed duplicated region for block: B:68:0x01ae  */
    /* JADX WARN: Removed duplicated region for block: B:72:0x01b9 A[ADDED_TO_REGION] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean update(DriverCall driverCall) {
        boolean z;
        int audioQualityFromDC;
        int i;
        ArrayList<String> arrayList;
        GsmCdmaCall gsmCdmaCall;
        boolean z2;
        boolean isConnectingInOrOut = isConnectingInOrOut();
        Call.State state = getState();
        Call.State state2 = Call.State.HOLDING;
        boolean z3 = state == state2;
        GsmCdmaCall parentFromDCState = parentFromDCState(driverCall.state);
        log("parent= " + this.mParent + ", newParent= " + parentFromDCState);
        if (isPhoneTypeGsm() && this.mOrigConnection != null) {
            log("update: mOrigConnection is not null");
        } else if (isIncoming() && !equalsBaseDialString(this.mAddress, driverCall.number) && (!this.mNumberConverted || !equalsBaseDialString(this.mConvertedNumber, driverCall.number))) {
            log("update: phone # changed!");
            this.mAddress = driverCall.number;
            z = true;
            audioQualityFromDC = getAudioQualityFromDC(driverCall.audioQuality);
            if (getAudioQuality() != audioQualityFromDC) {
                StringBuilder sb = new StringBuilder();
                sb.append("update: audioQuality # changed!:  ");
                sb.append(audioQualityFromDC == 2 ? "high" : "standard");
                log(sb.toString());
                setAudioQuality(audioQualityFromDC);
                z = true;
            }
            i = driverCall.audioQuality;
            if (i != this.mAudioCodec) {
                this.mAudioCodec = i;
                this.mMetrics.writeAudioCodecGsmCdma(this.mOwner.getPhone().getPhoneId(), driverCall.audioQuality);
                this.mOwner.getPhone().getVoiceCallSessionStats().onAudioCodecChanged(this, driverCall.audioQuality);
            }
            String str = !TextUtils.isEmpty(driverCall.forwardedNumber) ? null : driverCall.forwardedNumber;
            Rlog.i("GsmCdmaConnection", "update: forwardedNumber=" + Rlog.pii("GsmCdmaConnection", str));
            arrayList = str != null ? new ArrayList<>(Collections.singletonList(driverCall.forwardedNumber)) : null;
            if (!equalsHandlesNulls(this.mForwardedNumber, arrayList)) {
                log("update: mForwardedNumber, # changed");
                this.mForwardedNumber = arrayList;
                z = true;
            }
            if (!TextUtils.isEmpty(driverCall.name)) {
                if (!TextUtils.isEmpty(this.mCnapName)) {
                    this.mCnapName = PhoneConfigurationManager.SSSS;
                    z = true;
                }
            } else if (!driverCall.name.equals(this.mCnapName)) {
                this.mCnapName = driverCall.name;
                z = true;
            }
            log("--dssds----" + this.mCnapName);
            this.mCnapNamePresentation = driverCall.namePresentation;
            this.mNumberPresentation = driverCall.numberPresentation;
            gsmCdmaCall = this.mParent;
            if (parentFromDCState == gsmCdmaCall) {
                if (gsmCdmaCall != null) {
                    gsmCdmaCall.detach(this);
                }
                parentFromDCState.attach(this, driverCall);
                this.mParent = parentFromDCState;
            } else {
                boolean update = gsmCdmaCall.update(this, driverCall);
                if (!z && !update) {
                    z2 = false;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("update: parent=");
                    sb2.append(this.mParent);
                    sb2.append(", hasNewParent=");
                    sb2.append(parentFromDCState != this.mParent);
                    sb2.append(", wasConnectingInOrOut=");
                    sb2.append(isConnectingInOrOut);
                    sb2.append(", wasHolding=");
                    sb2.append(z3);
                    sb2.append(", isConnectingInOrOut=");
                    sb2.append(isConnectingInOrOut());
                    sb2.append(", changed=");
                    sb2.append(z2);
                    log(sb2.toString());
                    if (isConnectingInOrOut && !isConnectingInOrOut()) {
                        onConnectedInOrOut();
                    }
                    if (z2 && !z3 && getState() == state2) {
                        onStartedHolding();
                    }
                    return z2;
                }
            }
            z2 = true;
            StringBuilder sb22 = new StringBuilder();
            sb22.append("update: parent=");
            sb22.append(this.mParent);
            sb22.append(", hasNewParent=");
            sb22.append(parentFromDCState != this.mParent);
            sb22.append(", wasConnectingInOrOut=");
            sb22.append(isConnectingInOrOut);
            sb22.append(", wasHolding=");
            sb22.append(z3);
            sb22.append(", isConnectingInOrOut=");
            sb22.append(isConnectingInOrOut());
            sb22.append(", changed=");
            sb22.append(z2);
            log(sb22.toString());
            if (isConnectingInOrOut) {
                onConnectedInOrOut();
            }
            if (z2) {
                onStartedHolding();
            }
            return z2;
        }
        z = false;
        audioQualityFromDC = getAudioQualityFromDC(driverCall.audioQuality);
        if (getAudioQuality() != audioQualityFromDC) {
        }
        i = driverCall.audioQuality;
        if (i != this.mAudioCodec) {
        }
        if (!TextUtils.isEmpty(driverCall.forwardedNumber)) {
        }
        Rlog.i("GsmCdmaConnection", "update: forwardedNumber=" + Rlog.pii("GsmCdmaConnection", str));
        if (str != null) {
        }
        if (!equalsHandlesNulls(this.mForwardedNumber, arrayList)) {
        }
        if (!TextUtils.isEmpty(driverCall.name)) {
        }
        log("--dssds----" + this.mCnapName);
        this.mCnapNamePresentation = driverCall.namePresentation;
        this.mNumberPresentation = driverCall.numberPresentation;
        gsmCdmaCall = this.mParent;
        if (parentFromDCState == gsmCdmaCall) {
        }
        z2 = true;
        StringBuilder sb222 = new StringBuilder();
        sb222.append("update: parent=");
        sb222.append(this.mParent);
        sb222.append(", hasNewParent=");
        sb222.append(parentFromDCState != this.mParent);
        sb222.append(", wasConnectingInOrOut=");
        sb222.append(isConnectingInOrOut);
        sb222.append(", wasHolding=");
        sb222.append(z3);
        sb222.append(", isConnectingInOrOut=");
        sb222.append(isConnectingInOrOut());
        sb222.append(", changed=");
        sb222.append(z2);
        log(sb222.toString());
        if (isConnectingInOrOut) {
        }
        if (z2) {
        }
        return z2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void fakeHoldBeforeDial() {
        GsmCdmaCall gsmCdmaCall = this.mParent;
        if (gsmCdmaCall != null) {
            gsmCdmaCall.detach(this);
        }
        GsmCdmaCall gsmCdmaCall2 = this.mOwner.mBackgroundCall;
        this.mParent = gsmCdmaCall2;
        gsmCdmaCall2.attachFake(this, Call.State.HOLDING);
        onStartedHolding();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getGsmCdmaIndex() throws CallStateException {
        int i = this.mIndex;
        if (i >= 0) {
            return i + 1;
        }
        throw new CallStateException("GsmCdma index not yet assigned");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void onConnectedInOrOut() {
        this.mConnectTime = System.currentTimeMillis();
        this.mConnectTimeReal = SystemClock.elapsedRealtime();
        this.mDuration = 0L;
        log("onConnectedInOrOut: connectTime=" + this.mConnectTime);
        if (!this.mIsIncoming) {
            processNextPostDialChar();
        } else {
            releaseWakeLock();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onConnectedConnectionMigrated() {
        releaseWakeLock();
    }

    private void doDisconnect() {
        this.mIndex = -1;
        this.mDisconnectTime = System.currentTimeMillis();
        this.mDuration = SystemClock.elapsedRealtime() - this.mConnectTimeReal;
        this.mDisconnected = true;
        clearPostDialListeners();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onStartedHolding() {
        this.mHoldingStartTime = SystemClock.elapsedRealtime();
    }

    private boolean processPostDialChar(char c) {
        if (PhoneNumberUtils.is12Key(c)) {
            this.mOwner.mCi.sendDtmf(c, this.mHandler.obtainMessage(1));
        } else if (isPause(c)) {
            if (!isPhoneTypeGsm()) {
                setPostDialState(Connection.PostDialState.PAUSE);
            }
            Handler handler = this.mHandler;
            handler.sendMessageDelayed(handler.obtainMessage(2), isPhoneTypeGsm() ? 3000L : 2000L);
        } else if (isWait(c)) {
            setPostDialState(Connection.PostDialState.WAIT);
        } else if (!isWild(c)) {
            return false;
        } else {
            setPostDialState(Connection.PostDialState.WILD);
        }
        return true;
    }

    @Override // com.android.internal.telephony.Connection
    public String getRemainingPostDialString() {
        String remainingPostDialString = super.getRemainingPostDialString();
        if (isPhoneTypeGsm() || TextUtils.isEmpty(remainingPostDialString)) {
            return remainingPostDialString;
        }
        int indexOf = remainingPostDialString.indexOf(59);
        int indexOf2 = remainingPostDialString.indexOf(44);
        if (indexOf <= 0 || (indexOf >= indexOf2 && indexOf2 > 0)) {
            return indexOf2 > 0 ? remainingPostDialString.substring(0, indexOf2) : remainingPostDialString;
        }
        return remainingPostDialString.substring(0, indexOf);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void updateParent(GsmCdmaCall gsmCdmaCall, GsmCdmaCall gsmCdmaCall2) {
        if (gsmCdmaCall2 != gsmCdmaCall) {
            if (gsmCdmaCall != null) {
                gsmCdmaCall.detach(this);
            }
            gsmCdmaCall2.attachFake(this, Call.State.ACTIVE);
            this.mParent = gsmCdmaCall2;
        }
    }

    protected void finalize() {
        PowerManager.WakeLock wakeLock = this.mPartialWakeLock;
        if (wakeLock != null && wakeLock.isHeld()) {
            Rlog.e("GsmCdmaConnection", "UNEXPECTED; mPartialWakeLock is held when finalizing.");
        }
        clearPostDialListeners();
        releaseWakeLock();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processNextPostDialChar() {
        char c;
        Message messageForRegistrant;
        if (this.mPostDialState == Connection.PostDialState.CANCELLED) {
            releaseWakeLock();
            return;
        }
        String str = this.mPostDialString;
        if (str == null || str.length() <= this.mNextPostDialChar) {
            setPostDialState(Connection.PostDialState.COMPLETE);
            releaseWakeLock();
            c = 0;
        } else {
            setPostDialState(Connection.PostDialState.STARTED);
            String str2 = this.mPostDialString;
            int i = this.mNextPostDialChar;
            this.mNextPostDialChar = i + 1;
            c = str2.charAt(i);
            if (!processPostDialChar(c)) {
                this.mHandler.obtainMessage(3).sendToTarget();
                Rlog.e("GsmCdmaConnection", "processNextPostDialChar: c=" + c + " isn't valid!");
                return;
            }
        }
        notifyPostDialListenersNextChar(c);
        Registrant postDialHandler = this.mOwner.getPhone().getPostDialHandler();
        if (postDialHandler == null || (messageForRegistrant = postDialHandler.messageForRegistrant()) == null) {
            return;
        }
        Connection.PostDialState postDialState = this.mPostDialState;
        AsyncResult forMessage = AsyncResult.forMessage(messageForRegistrant);
        forMessage.result = this;
        forMessage.userObj = postDialState;
        messageForRegistrant.arg1 = c;
        messageForRegistrant.sendToTarget();
    }

    private boolean isConnectingInOrOut() {
        Call.State state;
        GsmCdmaCall gsmCdmaCall = this.mParent;
        return gsmCdmaCall == null || gsmCdmaCall == this.mOwner.mRingingCall || (state = gsmCdmaCall.mState) == Call.State.DIALING || state == Call.State.ALERTING;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.telephony.GsmCdmaConnection$1 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C00311 {
        static final /* synthetic */ int[] $SwitchMap$com$android$internal$telephony$DriverCall$State;

        static {
            int[] iArr = new int[DriverCall.State.values().length];
            $SwitchMap$com$android$internal$telephony$DriverCall$State = iArr;
            try {
                iArr[DriverCall.State.ACTIVE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$DriverCall$State[DriverCall.State.DIALING.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$DriverCall$State[DriverCall.State.ALERTING.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$DriverCall$State[DriverCall.State.HOLDING.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$DriverCall$State[DriverCall.State.INCOMING.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$DriverCall$State[DriverCall.State.WAITING.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
        }
    }

    private GsmCdmaCall parentFromDCState(DriverCall.State state) {
        switch (C00311.$SwitchMap$com$android$internal$telephony$DriverCall$State[state.ordinal()]) {
            case 1:
            case 2:
            case 3:
                return this.mOwner.mForegroundCall;
            case 4:
                return this.mOwner.mBackgroundCall;
            case 5:
            case 6:
                return this.mOwner.mRingingCall;
            default:
                throw new RuntimeException("illegal call state: " + state);
        }
    }

    private void setPostDialState(Connection.PostDialState postDialState) {
        if (postDialState == Connection.PostDialState.STARTED || postDialState == Connection.PostDialState.PAUSE) {
            synchronized (this.mPartialWakeLock) {
                if (this.mPartialWakeLock.isHeld()) {
                    this.mHandler.removeMessages(4);
                } else {
                    acquireWakeLock();
                }
                this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(4), 60000L);
            }
        } else {
            this.mHandler.removeMessages(4);
            releaseWakeLock();
        }
        this.mPostDialState = postDialState;
        notifyPostDialListeners();
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void createWakeLock(Context context) {
        this.mPartialWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(1, "GsmCdmaConnection");
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void acquireWakeLock() {
        PowerManager.WakeLock wakeLock = this.mPartialWakeLock;
        if (wakeLock != null) {
            synchronized (wakeLock) {
                log("acquireWakeLock");
                this.mPartialWakeLock.acquire();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseWakeLock() {
        PowerManager.WakeLock wakeLock = this.mPartialWakeLock;
        if (wakeLock != null) {
            synchronized (wakeLock) {
                if (this.mPartialWakeLock.isHeld()) {
                    log("releaseWakeLock");
                    this.mPartialWakeLock.release();
                }
            }
        }
    }

    private void releaseAllWakeLocks() {
        PowerManager.WakeLock wakeLock = this.mPartialWakeLock;
        if (wakeLock != null) {
            synchronized (wakeLock) {
                while (this.mPartialWakeLock.isHeld()) {
                    this.mPartialWakeLock.release();
                }
            }
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private static int findNextPCharOrNonPOrNonWCharIndex(String str, int i) {
        boolean isWait = isWait(str.charAt(i));
        int i2 = i + 1;
        int length = str.length();
        int i3 = i2;
        while (i3 < length) {
            char charAt = str.charAt(i3);
            if (isWait(charAt)) {
                isWait = true;
            }
            if (!isWait(charAt) && !isPause(charAt)) {
                break;
            }
            i3++;
        }
        return (i3 >= length || i3 <= i2 || isWait || !isPause(str.charAt(i))) ? i3 : i2;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private static char findPOrWCharToAppend(String str, int i, int i2) {
        char c = isPause(str.charAt(i)) ? ',' : ';';
        if (i2 > i + 1) {
            return ';';
        }
        return c;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void fetchDtmfToneDelay(GsmCdmaPhone gsmCdmaPhone) {
        PersistableBundle configForSubId = ((CarrierConfigManager) gsmCdmaPhone.getContext().getSystemService("carrier_config")).getConfigForSubId(gsmCdmaPhone.getSubId());
        if (configForSubId != null) {
            this.mDtmfToneDelay = configForSubId.getInt(gsmCdmaPhone.getDtmfToneDelayKey());
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean isPhoneTypeGsm() {
        return this.mOwner.getPhone().getPhoneType() == 1;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void log(String str) {
        Rlog.d("GsmCdmaConnection", "[GsmCdmaConn] " + str);
    }

    @Override // com.android.internal.telephony.Connection
    public int getNumberPresentation() {
        return this.mNumberPresentation;
    }

    @Override // com.android.internal.telephony.Connection
    public UUSInfo getUUSInfo() {
        return this.mUusInfo;
    }

    @Override // com.android.internal.telephony.Connection
    public int getPreciseDisconnectCause() {
        return this.mPreciseCause;
    }

    @Override // com.android.internal.telephony.Connection
    public String getVendorDisconnectCause() {
        return this.mVendorCause;
    }

    @Override // com.android.internal.telephony.Connection
    public void migrateFrom(Connection connection) {
        if (connection == null) {
            return;
        }
        super.migrateFrom(connection);
        this.mUusInfo = connection.getUUSInfo();
        setUserData(connection.getUserData());
    }

    @Override // com.android.internal.telephony.Connection
    public Connection getOrigConnection() {
        return this.mOrigConnection;
    }

    @Override // com.android.internal.telephony.Connection
    public boolean isMultiparty() {
        Connection connection = this.mOrigConnection;
        if (connection != null) {
            return connection.isMultiparty();
        }
        return false;
    }

    public EmergencyNumberTracker getEmergencyNumberTracker() {
        GsmCdmaPhone phone;
        GsmCdmaCallTracker gsmCdmaCallTracker = this.mOwner;
        if (gsmCdmaCallTracker == null || (phone = gsmCdmaCallTracker.getPhone()) == null) {
            return null;
        }
        return phone.getEmergencyNumberTracker();
    }

    public boolean isOtaspCall() {
        String str = this.mAddress;
        return str != null && OTASP_NUMBER.equals(str);
    }
}
