package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneNumberUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.Call;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.PhoneInternalInterface;
import com.android.internal.telephony.imsphone.ImsPhoneConnection;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyEvent;
import com.android.telephony.Rlog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class CallManager {
    @VisibleForTesting
    static final int EVENT_CALL_WAITING = 108;
    @VisibleForTesting
    static final int EVENT_PRECISE_CALL_STATE_CHANGED = 101;
    @VisibleForTesting
    static final int EVENT_RINGBACK_TONE = 105;
    private static final CallManager INSTANCE = new CallManager();
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private final ArrayList<Connection> mEmptyConnections = new ArrayList<>();
    private final HashMap<Phone, CallManagerHandler> mHandlerMap = new HashMap<>();
    private boolean mSpeedUpAudioForMtCall = false;
    private Object mRegistrantidentifier = new Object();
    protected final RegistrantList mPreciseCallStateRegistrants = new RegistrantList();
    protected final RegistrantList mNewRingingConnectionRegistrants = new RegistrantList();
    protected final RegistrantList mIncomingRingRegistrants = new RegistrantList();
    protected final RegistrantList mDisconnectRegistrants = new RegistrantList();
    protected final RegistrantList mMmiRegistrants = new RegistrantList();
    protected final RegistrantList mUnknownConnectionRegistrants = new RegistrantList();
    protected final RegistrantList mRingbackToneRegistrants = new RegistrantList();
    protected final RegistrantList mOnHoldToneRegistrants = new RegistrantList();
    protected final RegistrantList mInCallVoicePrivacyOnRegistrants = new RegistrantList();
    protected final RegistrantList mInCallVoicePrivacyOffRegistrants = new RegistrantList();
    protected final RegistrantList mCallWaitingRegistrants = new RegistrantList();
    protected final RegistrantList mDisplayInfoRegistrants = new RegistrantList();
    protected final RegistrantList mSignalInfoRegistrants = new RegistrantList();
    protected final RegistrantList mCdmaOtaStatusChangeRegistrants = new RegistrantList();
    protected final RegistrantList mResendIncallMuteRegistrants = new RegistrantList();
    protected final RegistrantList mMmiInitiateRegistrants = new RegistrantList();
    protected final RegistrantList mMmiCompleteRegistrants = new RegistrantList();
    protected final RegistrantList mEcmTimerResetRegistrants = new RegistrantList();
    protected final RegistrantList mSubscriptionInfoReadyRegistrants = new RegistrantList();
    protected final RegistrantList mSuppServiceFailedRegistrants = new RegistrantList();
    protected final RegistrantList mServiceStateChangedRegistrants = new RegistrantList();
    protected final RegistrantList mPostDialCharacterRegistrants = new RegistrantList();
    protected final RegistrantList mTtyModeReceivedRegistrants = new RegistrantList();
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private final ArrayList<Phone> mPhones = new ArrayList<>();
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private final ArrayList<Call> mRingingCalls = new ArrayList<>();
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private final ArrayList<Call> mBackgroundCalls = new ArrayList<>();
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private final ArrayList<Call> mForegroundCalls = new ArrayList<>();
    private Phone mDefaultPhone = null;

    private CallManager() {
    }

    @UnsupportedAppUsage
    public static CallManager getInstance() {
        return INSTANCE;
    }

    private Phone getPhone(int i) {
        Iterator<Phone> it = this.mPhones.iterator();
        while (it.hasNext()) {
            Phone next = it.next();
            if (next.getSubId() == i && next.getPhoneType() != 5) {
                return next;
            }
        }
        return null;
    }

    @UnsupportedAppUsage
    public PhoneConstants.State getState() {
        PhoneConstants.State state = PhoneConstants.State.IDLE;
        Iterator<Phone> it = this.mPhones.iterator();
        while (it.hasNext()) {
            Phone next = it.next();
            PhoneConstants.State state2 = next.getState();
            PhoneConstants.State state3 = PhoneConstants.State.RINGING;
            if (state2 == state3) {
                state = state3;
            } else if (next.getState() == PhoneConstants.State.OFFHOOK && state == PhoneConstants.State.IDLE) {
                state = PhoneConstants.State.OFFHOOK;
            }
        }
        return state;
    }

    @UnsupportedAppUsage
    public PhoneConstants.State getState(int i) {
        PhoneConstants.State state = PhoneConstants.State.IDLE;
        Iterator<Phone> it = this.mPhones.iterator();
        while (it.hasNext()) {
            Phone next = it.next();
            if (next.getSubId() == i) {
                PhoneConstants.State state2 = next.getState();
                PhoneConstants.State state3 = PhoneConstants.State.RINGING;
                if (state2 == state3) {
                    state = state3;
                } else if (next.getState() == PhoneConstants.State.OFFHOOK && state == PhoneConstants.State.IDLE) {
                    state = PhoneConstants.State.OFFHOOK;
                }
            }
        }
        return state;
    }

    public int getServiceState() {
        Iterator<Phone> it = this.mPhones.iterator();
        while (it.hasNext()) {
            int state = it.next().getServiceState().getState();
            if (state == 0) {
                return state;
            }
            if (state == 1) {
            }
        }
        return 1;
    }

    public int getServiceState(int i) {
        Iterator<Phone> it = this.mPhones.iterator();
        while (it.hasNext()) {
            Phone next = it.next();
            if (next.getSubId() == i) {
                int state = next.getServiceState().getState();
                if (state == 0) {
                    return state;
                }
                if (state == 1) {
                }
            }
        }
        return 1;
    }

    @UnsupportedAppUsage
    public Phone getPhoneInCall() {
        if (!getFirstActiveRingingCall().isIdle()) {
            return getFirstActiveRingingCall().getPhone();
        }
        if (!getActiveFgCall().isIdle()) {
            return getActiveFgCall().getPhone();
        }
        return getFirstActiveBgCall().getPhone();
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean registerPhone(Phone phone) {
        if (phone == null || this.mPhones.contains(phone)) {
            return false;
        }
        Rlog.d("CallManager", "registerPhone(" + phone.getPhoneName() + " " + phone + ")");
        if (this.mPhones.isEmpty()) {
            this.mDefaultPhone = phone;
        }
        this.mPhones.add(phone);
        this.mRingingCalls.add(phone.getRingingCall());
        this.mBackgroundCalls.add(phone.getBackgroundCall());
        this.mForegroundCalls.add(phone.getForegroundCall());
        registerForPhoneStates(phone);
        return true;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void unregisterPhone(Phone phone) {
        if (phone == null || !this.mPhones.contains(phone)) {
            return;
        }
        Rlog.d("CallManager", "unregisterPhone(" + phone.getPhoneName() + " " + phone + ")");
        Phone imsPhone = phone.getImsPhone();
        if (imsPhone != null) {
            unregisterPhone(imsPhone);
        }
        this.mPhones.remove(phone);
        this.mRingingCalls.remove(phone.getRingingCall());
        this.mBackgroundCalls.remove(phone.getBackgroundCall());
        this.mForegroundCalls.remove(phone.getForegroundCall());
        unregisterForPhoneStates(phone);
        if (phone == this.mDefaultPhone) {
            if (this.mPhones.isEmpty()) {
                this.mDefaultPhone = null;
            } else {
                this.mDefaultPhone = this.mPhones.get(0);
            }
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public Phone getDefaultPhone() {
        return this.mDefaultPhone;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public Phone getFgPhone() {
        return getActiveFgCall().getPhone();
    }

    @UnsupportedAppUsage
    public Phone getFgPhone(int i) {
        return getActiveFgCall(i).getPhone();
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public Phone getBgPhone() {
        return getFirstActiveBgCall().getPhone();
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public Phone getRingingPhone() {
        return getFirstActiveRingingCall().getPhone();
    }

    public Phone getRingingPhone(int i) {
        return getFirstActiveRingingCall(i).getPhone();
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private Context getContext() {
        Phone defaultPhone = getDefaultPhone();
        if (defaultPhone == null) {
            return null;
        }
        return defaultPhone.getContext();
    }

    public Object getRegistrantIdentifier() {
        return this.mRegistrantidentifier;
    }

    private void registerForPhoneStates(Phone phone) {
        if (this.mHandlerMap.get(phone) != null) {
            Rlog.d("CallManager", "This phone has already been registered.");
            return;
        }
        CallManagerHandler callManagerHandler = new CallManagerHandler();
        this.mHandlerMap.put(phone, callManagerHandler);
        phone.registerForPreciseCallStateChanged(callManagerHandler, 101, this.mRegistrantidentifier);
        phone.registerForDisconnect(callManagerHandler, 100, this.mRegistrantidentifier);
        phone.registerForNewRingingConnection(callManagerHandler, CallFailCause.RECOVERY_ON_TIMER_EXPIRY, this.mRegistrantidentifier);
        phone.registerForUnknownConnection(callManagerHandler, 103, this.mRegistrantidentifier);
        phone.registerForIncomingRing(callManagerHandler, 104, this.mRegistrantidentifier);
        phone.registerForRingbackTone(callManagerHandler, EVENT_RINGBACK_TONE, this.mRegistrantidentifier);
        phone.registerForInCallVoicePrivacyOn(callManagerHandler, 106, this.mRegistrantidentifier);
        phone.registerForInCallVoicePrivacyOff(callManagerHandler, 107, this.mRegistrantidentifier);
        phone.registerForDisplayInfo(callManagerHandler, 109, this.mRegistrantidentifier);
        phone.registerForSignalInfo(callManagerHandler, 110, this.mRegistrantidentifier);
        phone.registerForResendIncallMute(callManagerHandler, TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_APN_TYPE_CONFLICT, this.mRegistrantidentifier);
        phone.registerForMmiInitiate(callManagerHandler, TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INVALID_PCSCF_ADDR, this.mRegistrantidentifier);
        phone.registerForMmiComplete(callManagerHandler, TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INTERNAL_CALL_PREEMPT_BY_HIGH_PRIO_APN, this.mRegistrantidentifier);
        phone.registerForSuppServiceFailed(callManagerHandler, TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IFACE_MISMATCH, this.mRegistrantidentifier);
        phone.registerForServiceStateChanged(callManagerHandler, TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_COMPANION_IFACE_IN_USE, this.mRegistrantidentifier);
        phone.setOnPostDialCharacter(callManagerHandler, TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IP_ADDRESS_MISMATCH, null);
        phone.registerForCdmaOtaStatusChange(callManagerHandler, 111, null);
        phone.registerForSubscriptionInfoReady(callManagerHandler, TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMERGENCY_IFACE_ONLY, null);
        phone.registerForCallWaiting(callManagerHandler, EVENT_CALL_WAITING, null);
        phone.registerForEcmTimerReset(callManagerHandler, TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMM_ACCESS_BARRED, null);
        phone.registerForOnHoldTone(callManagerHandler, TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IFACE_AND_POL_FAMILY_MISMATCH, null);
        phone.registerForSuppServiceFailed(callManagerHandler, TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IFACE_MISMATCH, null);
        phone.registerForTtyModeReceived(callManagerHandler, TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_AUTH_FAILURE_ON_EMERGENCY_CALL, null);
    }

    private void unregisterForPhoneStates(Phone phone) {
        CallManagerHandler callManagerHandler = this.mHandlerMap.get(phone);
        if (callManagerHandler == null) {
            Rlog.e("CallManager", "Could not find Phone handler for unregistration");
            return;
        }
        this.mHandlerMap.remove(phone);
        phone.unregisterForPreciseCallStateChanged(callManagerHandler);
        phone.unregisterForDisconnect(callManagerHandler);
        phone.unregisterForNewRingingConnection(callManagerHandler);
        phone.unregisterForUnknownConnection(callManagerHandler);
        phone.unregisterForIncomingRing(callManagerHandler);
        phone.unregisterForRingbackTone(callManagerHandler);
        phone.unregisterForInCallVoicePrivacyOn(callManagerHandler);
        phone.unregisterForInCallVoicePrivacyOff(callManagerHandler);
        phone.unregisterForDisplayInfo(callManagerHandler);
        phone.unregisterForSignalInfo(callManagerHandler);
        phone.unregisterForResendIncallMute(callManagerHandler);
        phone.unregisterForMmiInitiate(callManagerHandler);
        phone.unregisterForMmiComplete(callManagerHandler);
        phone.unregisterForSuppServiceFailed(callManagerHandler);
        phone.unregisterForServiceStateChanged(callManagerHandler);
        phone.unregisterForTtyModeReceived(callManagerHandler);
        phone.setOnPostDialCharacter(null, TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IP_ADDRESS_MISMATCH, null);
        phone.unregisterForCdmaOtaStatusChange(callManagerHandler);
        phone.unregisterForSubscriptionInfoReady(callManagerHandler);
        phone.unregisterForCallWaiting(callManagerHandler);
        phone.unregisterForEcmTimerReset(callManagerHandler);
        phone.unregisterForOnHoldTone(callManagerHandler);
        phone.unregisterForSuppServiceFailed(callManagerHandler);
    }

    public void rejectCall(Call call) throws CallStateException {
        call.getPhone().rejectCall();
    }

    public boolean canConference(Call call) {
        return (call != null ? call.getPhone() : null).getClass().equals((hasActiveFgCall() ? getActiveFgCall().getPhone() : null).getClass());
    }

    @UnsupportedAppUsage
    public boolean canConference(Call call, int i) {
        return (call != null ? call.getPhone() : null).getClass().equals((hasActiveFgCall(i) ? getActiveFgCall(i).getPhone() : null).getClass());
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void conference(Call call) throws CallStateException {
        Phone fgPhone = getFgPhone(call.getPhone().getSubId());
        if (fgPhone != null) {
            if (canConference(call)) {
                fgPhone.conference();
                return;
            }
            throw new CallStateException("Can't conference foreground and selected background call");
        }
        Rlog.d("CallManager", "conference: fgPhone=null");
    }

    public Connection dial(Phone phone, String str, int i) throws CallStateException {
        int subId = phone.getSubId();
        if (!canDial(phone)) {
            if (phone.handleInCallMmiCommands(PhoneNumberUtils.stripSeparators(str))) {
                return null;
            }
            throw new CallStateException("cannot dial in current state");
        }
        if (hasActiveFgCall(subId)) {
            Phone phone2 = getActiveFgCall(subId).getPhone();
            boolean z = !phone2.getBackgroundCall().isIdle();
            StringBuilder sb = new StringBuilder();
            sb.append("hasBgCall: ");
            sb.append(z);
            sb.append(" sameChannel:");
            sb.append(phone2 == phone);
            Rlog.d("CallManager", sb.toString());
            Phone imsPhone = phone.getImsPhone();
            if (phone2 != phone && (imsPhone == null || imsPhone != phone2)) {
                if (z) {
                    Rlog.d("CallManager", "Hangup");
                    getActiveFgCall(subId).hangup();
                } else {
                    Rlog.d("CallManager", "Switch");
                    phone2.switchHoldingAndActive();
                }
            }
        }
        return phone.dial(str, new PhoneInternalInterface.DialArgs.Builder().setVideoState(i).build());
    }

    public Connection dial(Phone phone, String str, UUSInfo uUSInfo, int i) throws CallStateException {
        return phone.dial(str, new PhoneInternalInterface.DialArgs.Builder().setUusInfo(uUSInfo).setVideoState(i).build());
    }

    public void clearDisconnected() {
        Iterator<Phone> it = this.mPhones.iterator();
        while (it.hasNext()) {
            it.next().clearDisconnected();
        }
    }

    public void clearDisconnected(int i) {
        Iterator<Phone> it = this.mPhones.iterator();
        while (it.hasNext()) {
            Phone next = it.next();
            if (next.getSubId() == i) {
                next.clearDisconnected();
            }
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean canDial(Phone phone) {
        int state = phone.getServiceState().getState();
        int subId = phone.getSubId();
        boolean hasActiveRingingCall = hasActiveRingingCall();
        Call.State activeFgCallState = getActiveFgCallState(subId);
        boolean z = (state == 3 || hasActiveRingingCall || (activeFgCallState != Call.State.ACTIVE && activeFgCallState != Call.State.IDLE && activeFgCallState != Call.State.DISCONNECTED && activeFgCallState != Call.State.ALERTING)) ? false : true;
        if (!z) {
            Rlog.d("CallManager", "canDial serviceState=" + state + " hasRingingCall=" + hasActiveRingingCall + " fgCallState=" + activeFgCallState);
        }
        return z;
    }

    public boolean canTransfer(Call call) {
        Phone phone = hasActiveFgCall() ? getActiveFgCall().getPhone() : null;
        return (call != null ? call.getPhone() : null) == phone && phone.canTransfer();
    }

    public boolean canTransfer(Call call, int i) {
        Phone phone = hasActiveFgCall(i) ? getActiveFgCall(i).getPhone() : null;
        return (call != null ? call.getPhone() : null) == phone && phone.canTransfer();
    }

    public void explicitCallTransfer(Call call) throws CallStateException {
        if (canTransfer(call)) {
            call.getPhone().explicitCallTransfer();
        }
    }

    public List<? extends MmiCode> getPendingMmiCodes(Phone phone) {
        Rlog.e("CallManager", "getPendingMmiCodes not implemented");
        return null;
    }

    public boolean sendUssdResponse(Phone phone, String str) {
        Rlog.e("CallManager", "sendUssdResponse not implemented");
        return false;
    }

    public void setMute(boolean z) {
        if (hasActiveFgCall()) {
            getActiveFgCall().getPhone().setMute(z);
        }
    }

    public boolean getMute() {
        if (hasActiveFgCall()) {
            return getActiveFgCall().getPhone().getMute();
        }
        if (hasActiveBgCall()) {
            return getFirstActiveBgCall().getPhone().getMute();
        }
        return false;
    }

    public void setEchoSuppressionEnabled() {
        if (hasActiveFgCall()) {
            getActiveFgCall().getPhone().setEchoSuppressionEnabled();
        }
    }

    public boolean sendDtmf(char c) {
        if (hasActiveFgCall()) {
            getActiveFgCall().getPhone().sendDtmf(c);
            return true;
        }
        return false;
    }

    public boolean startDtmf(char c) {
        if (hasActiveFgCall()) {
            getActiveFgCall().getPhone().startDtmf(c);
            return true;
        }
        return false;
    }

    public void stopDtmf() {
        if (hasActiveFgCall()) {
            getFgPhone().stopDtmf();
        }
    }

    public boolean sendBurstDtmf(String str, int i, int i2, Message message) {
        if (hasActiveFgCall()) {
            getActiveFgCall().getPhone().sendBurstDtmf(str, i, i2, message);
            return true;
        }
        return false;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void registerForDisconnect(Handler handler, int i, Object obj) {
        this.mDisconnectRegistrants.addUnique(handler, i, obj);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void unregisterForDisconnect(Handler handler) {
        this.mDisconnectRegistrants.remove(handler);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void registerForPreciseCallStateChanged(Handler handler, int i, Object obj) {
        this.mPreciseCallStateRegistrants.addUnique(handler, i, obj);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void unregisterForPreciseCallStateChanged(Handler handler) {
        this.mPreciseCallStateRegistrants.remove(handler);
    }

    public void registerForUnknownConnection(Handler handler, int i, Object obj) {
        this.mUnknownConnectionRegistrants.addUnique(handler, i, obj);
    }

    public void unregisterForUnknownConnection(Handler handler) {
        this.mUnknownConnectionRegistrants.remove(handler);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void registerForNewRingingConnection(Handler handler, int i, Object obj) {
        this.mNewRingingConnectionRegistrants.addUnique(handler, i, obj);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void unregisterForNewRingingConnection(Handler handler) {
        this.mNewRingingConnectionRegistrants.remove(handler);
    }

    public void registerForIncomingRing(Handler handler, int i, Object obj) {
        this.mIncomingRingRegistrants.addUnique(handler, i, obj);
    }

    public void unregisterForIncomingRing(Handler handler) {
        this.mIncomingRingRegistrants.remove(handler);
    }

    public void registerForRingbackTone(Handler handler, int i, Object obj) {
        this.mRingbackToneRegistrants.addUnique(handler, i, obj);
    }

    public void unregisterForRingbackTone(Handler handler) {
        this.mRingbackToneRegistrants.remove(handler);
    }

    public void registerForOnHoldTone(Handler handler, int i, Object obj) {
        this.mOnHoldToneRegistrants.addUnique(handler, i, obj);
    }

    public void unregisterForOnHoldTone(Handler handler) {
        this.mOnHoldToneRegistrants.remove(handler);
    }

    public void registerForResendIncallMute(Handler handler, int i, Object obj) {
        this.mResendIncallMuteRegistrants.addUnique(handler, i, obj);
    }

    public void unregisterForResendIncallMute(Handler handler) {
        this.mResendIncallMuteRegistrants.remove(handler);
    }

    public void registerForMmiInitiate(Handler handler, int i, Object obj) {
        this.mMmiInitiateRegistrants.addUnique(handler, i, obj);
    }

    public void unregisterForMmiInitiate(Handler handler) {
        this.mMmiInitiateRegistrants.remove(handler);
    }

    public void registerForMmiComplete(Handler handler, int i, Object obj) {
        Rlog.d("CallManager", "registerForMmiComplete");
        this.mMmiCompleteRegistrants.addUnique(handler, i, obj);
    }

    public void unregisterForMmiComplete(Handler handler) {
        this.mMmiCompleteRegistrants.remove(handler);
    }

    public void registerForEcmTimerReset(Handler handler, int i, Object obj) {
        this.mEcmTimerResetRegistrants.addUnique(handler, i, obj);
    }

    public void unregisterForEcmTimerReset(Handler handler) {
        this.mEcmTimerResetRegistrants.remove(handler);
    }

    public void registerForServiceStateChanged(Handler handler, int i, Object obj) {
        this.mServiceStateChangedRegistrants.addUnique(handler, i, obj);
    }

    public void unregisterForServiceStateChanged(Handler handler) {
        this.mServiceStateChangedRegistrants.remove(handler);
    }

    public void registerForSuppServiceFailed(Handler handler, int i, Object obj) {
        this.mSuppServiceFailedRegistrants.addUnique(handler, i, obj);
    }

    public void unregisterForSuppServiceFailed(Handler handler) {
        this.mSuppServiceFailedRegistrants.remove(handler);
    }

    public void registerForInCallVoicePrivacyOn(Handler handler, int i, Object obj) {
        this.mInCallVoicePrivacyOnRegistrants.addUnique(handler, i, obj);
    }

    public void unregisterForInCallVoicePrivacyOn(Handler handler) {
        this.mInCallVoicePrivacyOnRegistrants.remove(handler);
    }

    public void registerForInCallVoicePrivacyOff(Handler handler, int i, Object obj) {
        this.mInCallVoicePrivacyOffRegistrants.addUnique(handler, i, obj);
    }

    public void unregisterForInCallVoicePrivacyOff(Handler handler) {
        this.mInCallVoicePrivacyOffRegistrants.remove(handler);
    }

    public void registerForCallWaiting(Handler handler, int i, Object obj) {
        this.mCallWaitingRegistrants.addUnique(handler, i, obj);
    }

    public void unregisterForCallWaiting(Handler handler) {
        this.mCallWaitingRegistrants.remove(handler);
    }

    public void registerForSignalInfo(Handler handler, int i, Object obj) {
        this.mSignalInfoRegistrants.addUnique(handler, i, obj);
    }

    public void unregisterForSignalInfo(Handler handler) {
        this.mSignalInfoRegistrants.remove(handler);
    }

    public void registerForDisplayInfo(Handler handler, int i, Object obj) {
        this.mDisplayInfoRegistrants.addUnique(handler, i, obj);
    }

    public void unregisterForDisplayInfo(Handler handler) {
        this.mDisplayInfoRegistrants.remove(handler);
    }

    public void registerForCdmaOtaStatusChange(Handler handler, int i, Object obj) {
        this.mCdmaOtaStatusChangeRegistrants.addUnique(handler, i, obj);
    }

    public void unregisterForCdmaOtaStatusChange(Handler handler) {
        this.mCdmaOtaStatusChangeRegistrants.remove(handler);
    }

    public void registerForSubscriptionInfoReady(Handler handler, int i, Object obj) {
        this.mSubscriptionInfoReadyRegistrants.addUnique(handler, i, obj);
    }

    public void unregisterForSubscriptionInfoReady(Handler handler) {
        this.mSubscriptionInfoReadyRegistrants.remove(handler);
    }

    public void registerForPostDialCharacter(Handler handler, int i, Object obj) {
        this.mPostDialCharacterRegistrants.addUnique(handler, i, obj);
    }

    public void unregisterForPostDialCharacter(Handler handler) {
        this.mPostDialCharacterRegistrants.remove(handler);
    }

    public void registerForTtyModeReceived(Handler handler, int i, Object obj) {
        this.mTtyModeReceivedRegistrants.addUnique(handler, i, obj);
    }

    public void unregisterForTtyModeReceived(Handler handler) {
        this.mTtyModeReceivedRegistrants.remove(handler);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public List<Call> getRingingCalls() {
        return Collections.unmodifiableList(this.mRingingCalls);
    }

    public List<Call> getForegroundCalls() {
        return Collections.unmodifiableList(this.mForegroundCalls);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public List<Call> getBackgroundCalls() {
        return Collections.unmodifiableList(this.mBackgroundCalls);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean hasActiveFgCall() {
        return getFirstActiveCall(this.mForegroundCalls) != null;
    }

    @UnsupportedAppUsage
    public boolean hasActiveFgCall(int i) {
        return getFirstActiveCall(this.mForegroundCalls, i) != null;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean hasActiveBgCall() {
        return getFirstActiveCall(this.mBackgroundCalls) != null;
    }

    @UnsupportedAppUsage
    public boolean hasActiveBgCall(int i) {
        return getFirstActiveCall(this.mBackgroundCalls, i) != null;
    }

    public boolean hasActiveRingingCall() {
        return getFirstActiveCall(this.mRingingCalls) != null;
    }

    @UnsupportedAppUsage
    public boolean hasActiveRingingCall(int i) {
        return getFirstActiveCall(this.mRingingCalls, i) != null;
    }

    public Call getActiveFgCall() {
        Call firstNonIdleCall = getFirstNonIdleCall(this.mForegroundCalls);
        if (firstNonIdleCall == null) {
            Phone phone = this.mDefaultPhone;
            return phone == null ? null : phone.getForegroundCall();
        }
        return firstNonIdleCall;
    }

    @UnsupportedAppUsage
    public Call getActiveFgCall(int i) {
        Call firstNonIdleCall = getFirstNonIdleCall(this.mForegroundCalls, i);
        if (firstNonIdleCall == null) {
            Phone phone = getPhone(i);
            return phone == null ? null : phone.getForegroundCall();
        }
        return firstNonIdleCall;
    }

    private Call getFirstNonIdleCall(List<Call> list) {
        Call call = null;
        for (Call call2 : list) {
            if (!call2.isIdle()) {
                return call2;
            }
            if (call2.getState() != Call.State.IDLE && call == null) {
                call = call2;
            }
        }
        return call;
    }

    private Call getFirstNonIdleCall(List<Call> list, int i) {
        Call call = null;
        for (Call call2 : list) {
            if (call2.getPhone().getSubId() == i) {
                if (!call2.isIdle()) {
                    return call2;
                }
                if (call2.getState() != Call.State.IDLE && call == null) {
                    call = call2;
                }
            }
        }
        return call;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public Call getFirstActiveBgCall() {
        Call firstNonIdleCall = getFirstNonIdleCall(this.mBackgroundCalls);
        if (firstNonIdleCall == null) {
            Phone phone = this.mDefaultPhone;
            return phone == null ? null : phone.getBackgroundCall();
        }
        return firstNonIdleCall;
    }

    @UnsupportedAppUsage
    public Call getFirstActiveBgCall(int i) {
        Phone phone = getPhone(i);
        if (hasMoreThanOneHoldingCall(i)) {
            return phone.getBackgroundCall();
        }
        Call firstNonIdleCall = getFirstNonIdleCall(this.mBackgroundCalls, i);
        if (firstNonIdleCall == null) {
            if (phone == null) {
                return null;
            }
            return phone.getBackgroundCall();
        }
        return firstNonIdleCall;
    }

    @UnsupportedAppUsage
    public Call getFirstActiveRingingCall() {
        Call firstNonIdleCall = getFirstNonIdleCall(this.mRingingCalls);
        if (firstNonIdleCall == null) {
            Phone phone = this.mDefaultPhone;
            return phone == null ? null : phone.getRingingCall();
        }
        return firstNonIdleCall;
    }

    @UnsupportedAppUsage
    public Call getFirstActiveRingingCall(int i) {
        Phone phone = getPhone(i);
        Call firstNonIdleCall = getFirstNonIdleCall(this.mRingingCalls, i);
        if (firstNonIdleCall == null) {
            if (phone == null) {
                return null;
            }
            return phone.getRingingCall();
        }
        return firstNonIdleCall;
    }

    public Call.State getActiveFgCallState() {
        Call activeFgCall = getActiveFgCall();
        if (activeFgCall != null) {
            return activeFgCall.getState();
        }
        return Call.State.IDLE;
    }

    @UnsupportedAppUsage
    public Call.State getActiveFgCallState(int i) {
        Call activeFgCall = getActiveFgCall(i);
        if (activeFgCall != null) {
            return activeFgCall.getState();
        }
        return Call.State.IDLE;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public List<Connection> getFgCallConnections() {
        Call activeFgCall = getActiveFgCall();
        if (activeFgCall != null) {
            return activeFgCall.getConnections();
        }
        return this.mEmptyConnections;
    }

    public List<Connection> getFgCallConnections(int i) {
        Call activeFgCall = getActiveFgCall(i);
        if (activeFgCall != null) {
            return activeFgCall.getConnections();
        }
        return this.mEmptyConnections;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public List<Connection> getBgCallConnections() {
        Call firstActiveBgCall = getFirstActiveBgCall();
        if (firstActiveBgCall != null) {
            return firstActiveBgCall.getConnections();
        }
        return this.mEmptyConnections;
    }

    public boolean hasDisconnectedFgCall() {
        return getFirstCallOfState(this.mForegroundCalls, Call.State.DISCONNECTED) != null;
    }

    public boolean hasDisconnectedFgCall(int i) {
        return getFirstCallOfState(this.mForegroundCalls, Call.State.DISCONNECTED, i) != null;
    }

    public boolean hasDisconnectedBgCall() {
        return getFirstCallOfState(this.mBackgroundCalls, Call.State.DISCONNECTED) != null;
    }

    public boolean hasDisconnectedBgCall(int i) {
        return getFirstCallOfState(this.mBackgroundCalls, Call.State.DISCONNECTED, i) != null;
    }

    private Call getFirstActiveCall(ArrayList<Call> arrayList) {
        Iterator<Call> it = arrayList.iterator();
        while (it.hasNext()) {
            Call next = it.next();
            if (!next.isIdle()) {
                return next;
            }
        }
        return null;
    }

    private Call getFirstActiveCall(ArrayList<Call> arrayList, int i) {
        Iterator<Call> it = arrayList.iterator();
        while (it.hasNext()) {
            Call next = it.next();
            if (!next.isIdle() && next.getPhone().getSubId() == i) {
                return next;
            }
        }
        return null;
    }

    private Call getFirstCallOfState(ArrayList<Call> arrayList, Call.State state) {
        Iterator<Call> it = arrayList.iterator();
        while (it.hasNext()) {
            Call next = it.next();
            if (next.getState() == state) {
                return next;
            }
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:5:0x000a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private Call getFirstCallOfState(ArrayList<Call> arrayList, Call.State state, int i) {
        Iterator<Call> it = arrayList.iterator();
        while (it.hasNext()) {
            Call next = it.next();
            if (next.getState() == state || next.getPhone().getSubId() == i) {
                return next;
            }
            while (it.hasNext()) {
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean hasMoreThanOneRingingCall() {
        Iterator<Call> it = this.mRingingCalls.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (it.next().getState().isRinging() && (i = i + 1) > 1) {
                return true;
            }
        }
        return false;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean hasMoreThanOneRingingCall(int i) {
        Iterator<Call> it = this.mRingingCalls.iterator();
        int i2 = 0;
        while (it.hasNext()) {
            Call next = it.next();
            if (next.getState().isRinging() && next.getPhone().getSubId() == i && (i2 = i2 + 1) > 1) {
                return true;
            }
        }
        return false;
    }

    private boolean hasMoreThanOneHoldingCall(int i) {
        Iterator<Call> it = this.mBackgroundCalls.iterator();
        int i2 = 0;
        while (it.hasNext()) {
            Call next = it.next();
            if (next.getState() == Call.State.HOLDING && next.getPhone().getSubId() == i && (i2 = i2 + 1) > 1) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class CallManagerHandler extends Handler {
        private CallManagerHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = 0;
            switch (message.what) {
                case 100:
                    CallManager.this.mDisconnectRegistrants.notifyRegistrants((AsyncResult) message.obj);
                    return;
                case 101:
                    CallManager.this.mPreciseCallStateRegistrants.notifyRegistrants((AsyncResult) message.obj);
                    return;
                case CallFailCause.RECOVERY_ON_TIMER_EXPIRY /* 102 */:
                    Connection connection = (Connection) ((AsyncResult) message.obj).result;
                    int subId = connection.getCall().getPhone().getSubId();
                    if (connection.getPhoneType() == 5 && ((ImsPhoneConnection) connection).isIncomingCallAutoRejected()) {
                        i = 1;
                    }
                    if ((CallManager.this.getActiveFgCallState(subId).isDialing() || CallManager.this.hasMoreThanOneRingingCall()) && i == 0) {
                        try {
                            Rlog.d("CallManager", "silently drop incoming call: " + connection.getCall());
                            connection.getCall().hangup();
                            return;
                        } catch (CallStateException e) {
                            Rlog.w("CallManager", "new ringing connection", e);
                            return;
                        }
                    }
                    CallManager.this.mNewRingingConnectionRegistrants.notifyRegistrants((AsyncResult) message.obj);
                    return;
                case 103:
                    CallManager.this.mUnknownConnectionRegistrants.notifyRegistrants((AsyncResult) message.obj);
                    return;
                case 104:
                    if (CallManager.this.hasActiveFgCall()) {
                        return;
                    }
                    CallManager.this.mIncomingRingRegistrants.notifyRegistrants((AsyncResult) message.obj);
                    return;
                case CallManager.EVENT_RINGBACK_TONE /* 105 */:
                    CallManager.this.mRingbackToneRegistrants.notifyRegistrants((AsyncResult) message.obj);
                    return;
                case 106:
                    CallManager.this.mInCallVoicePrivacyOnRegistrants.notifyRegistrants((AsyncResult) message.obj);
                    return;
                case 107:
                    CallManager.this.mInCallVoicePrivacyOffRegistrants.notifyRegistrants((AsyncResult) message.obj);
                    return;
                case CallManager.EVENT_CALL_WAITING /* 108 */:
                    CallManager.this.mCallWaitingRegistrants.notifyRegistrants((AsyncResult) message.obj);
                    return;
                case 109:
                    CallManager.this.mDisplayInfoRegistrants.notifyRegistrants((AsyncResult) message.obj);
                    return;
                case 110:
                    CallManager.this.mSignalInfoRegistrants.notifyRegistrants((AsyncResult) message.obj);
                    return;
                case 111:
                    CallManager.this.mCdmaOtaStatusChangeRegistrants.notifyRegistrants((AsyncResult) message.obj);
                    return;
                case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_APN_TYPE_CONFLICT /* 112 */:
                    CallManager.this.mResendIncallMuteRegistrants.notifyRegistrants((AsyncResult) message.obj);
                    return;
                case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INVALID_PCSCF_ADDR /* 113 */:
                    CallManager.this.mMmiInitiateRegistrants.notifyRegistrants((AsyncResult) message.obj);
                    return;
                case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INTERNAL_CALL_PREEMPT_BY_HIGH_PRIO_APN /* 114 */:
                    Rlog.d("CallManager", "CallManager: handleMessage (EVENT_MMI_COMPLETE)");
                    CallManager.this.mMmiCompleteRegistrants.notifyRegistrants((AsyncResult) message.obj);
                    return;
                case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMM_ACCESS_BARRED /* 115 */:
                    CallManager.this.mEcmTimerResetRegistrants.notifyRegistrants((AsyncResult) message.obj);
                    return;
                case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMERGENCY_IFACE_ONLY /* 116 */:
                    CallManager.this.mSubscriptionInfoReadyRegistrants.notifyRegistrants((AsyncResult) message.obj);
                    return;
                case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IFACE_MISMATCH /* 117 */:
                    CallManager.this.mSuppServiceFailedRegistrants.notifyRegistrants((AsyncResult) message.obj);
                    return;
                case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_COMPANION_IFACE_IN_USE /* 118 */:
                    CallManager.this.mServiceStateChangedRegistrants.notifyRegistrants((AsyncResult) message.obj);
                    return;
                case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IP_ADDRESS_MISMATCH /* 119 */:
                    break;
                case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IFACE_AND_POL_FAMILY_MISMATCH /* 120 */:
                    CallManager.this.mOnHoldToneRegistrants.notifyRegistrants((AsyncResult) message.obj);
                    return;
                case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMM_ACCESS_BARRED_INFINITE_RETRY /* 121 */:
                default:
                    return;
                case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_AUTH_FAILURE_ON_EMERGENCY_CALL /* 122 */:
                    CallManager.this.mTtyModeReceivedRegistrants.notifyRegistrants((AsyncResult) message.obj);
                    return;
            }
            while (i < CallManager.this.mPostDialCharacterRegistrants.size()) {
                Message messageForRegistrant = ((Registrant) CallManager.this.mPostDialCharacterRegistrants.get(i)).messageForRegistrant();
                messageForRegistrant.obj = message.obj;
                messageForRegistrant.arg1 = message.arg1;
                messageForRegistrant.sendToTarget();
                i++;
            }
        }
    }
}
