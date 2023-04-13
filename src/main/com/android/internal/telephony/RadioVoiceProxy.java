package com.android.internal.telephony;

import android.hardware.radio.V1_0.Dial;
import android.hardware.radio.V1_6.IRadio;
import android.hardware.radio.voice.IRadioVoice;
import android.os.RemoteException;
import android.telephony.PhoneNumberUtils;
import android.telephony.Rlog;
import android.telephony.emergency.EmergencyNumber;
import java.util.ArrayList;
import java.util.function.IntFunction;
/* loaded from: classes.dex */
public class RadioVoiceProxy extends RadioServiceProxy {
    private volatile IRadioVoice mVoiceProxy = null;

    public HalVersion setAidl(HalVersion halVersion, IRadioVoice iRadioVoice) {
        try {
            halVersion = RIL.getServiceHalVersion(iRadioVoice.getInterfaceVersion());
        } catch (RemoteException e) {
            Rlog.e("RadioVoiceProxy", "setAidl: " + e);
        }
        this.mHalVersion = halVersion;
        this.mVoiceProxy = iRadioVoice;
        this.mIsAidl = true;
        Rlog.d("RadioVoiceProxy", "AIDL initialized mHalVersion=" + this.mHalVersion);
        return this.mHalVersion;
    }

    public IRadioVoice getAidl() {
        return this.mVoiceProxy;
    }

    @Override // com.android.internal.telephony.RadioServiceProxy
    public void clear() {
        super.clear();
        this.mVoiceProxy = null;
    }

    @Override // com.android.internal.telephony.RadioServiceProxy
    public boolean isEmpty() {
        return this.mRadioProxy == null && this.mVoiceProxy == null;
    }

    public void acceptCall(int i) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.acceptCall(i);
        } else {
            this.mRadioProxy.acceptCall(i);
        }
    }

    public void cancelPendingUssd(int i) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.cancelPendingUssd(i);
        } else {
            this.mRadioProxy.cancelPendingUssd(i);
        }
    }

    public void conference(int i) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.conference(i);
        } else {
            this.mRadioProxy.conference(i);
        }
    }

    public void dial(int i, String str, int i2, UUSInfo uUSInfo) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.dial(i, RILUtils.convertToHalDialAidl(str, i2, uUSInfo));
        } else {
            this.mRadioProxy.dial(i, RILUtils.convertToHalDial(str, i2, uUSInfo));
        }
    }

    public void emergencyDial(int i, String str, EmergencyNumber emergencyNumber, boolean z, int i2, UUSInfo uUSInfo) throws RemoteException {
        ArrayList arrayList;
        ArrayList arrayList2;
        if (isEmpty() || this.mHalVersion.less(RIL.RADIO_HAL_VERSION_1_4)) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.emergencyDial(i, RILUtils.convertToHalDialAidl(str, i2, uUSInfo), emergencyNumber.getEmergencyServiceCategoryBitmaskInternalDial(), emergencyNumber.getEmergencyUrns() != null ? (String[]) emergencyNumber.getEmergencyUrns().stream().toArray(new IntFunction() { // from class: com.android.internal.telephony.RadioVoiceProxy$$ExternalSyntheticLambda0
                @Override // java.util.function.IntFunction
                public final Object apply(int i3) {
                    String[] lambda$emergencyDial$0;
                    lambda$emergencyDial$0 = RadioVoiceProxy.lambda$emergencyDial$0(i3);
                    return lambda$emergencyDial$0;
                }
            }) : new String[0], emergencyNumber.getEmergencyCallRouting(), z, emergencyNumber.getEmergencyNumberSourceBitmask() == 32);
        } else if (this.mHalVersion.greaterOrEqual(RIL.RADIO_HAL_VERSION_1_6)) {
            IRadio iRadio = this.mRadioProxy;
            Dial convertToHalDial = RILUtils.convertToHalDial(str, i2, uUSInfo);
            int emergencyServiceCategoryBitmaskInternalDial = emergencyNumber.getEmergencyServiceCategoryBitmaskInternalDial();
            if (emergencyNumber.getEmergencyUrns() != null) {
                arrayList2 = new ArrayList(emergencyNumber.getEmergencyUrns());
            } else {
                arrayList2 = new ArrayList();
            }
            iRadio.emergencyDial_1_6(i, convertToHalDial, emergencyServiceCategoryBitmaskInternalDial, arrayList2, emergencyNumber.getEmergencyCallRouting(), z, emergencyNumber.getEmergencyNumberSourceBitmask() == 32);
        } else {
            android.hardware.radio.V1_4.IRadio iRadio2 = this.mRadioProxy;
            Dial convertToHalDial2 = RILUtils.convertToHalDial(str, i2, uUSInfo);
            int emergencyServiceCategoryBitmaskInternalDial2 = emergencyNumber.getEmergencyServiceCategoryBitmaskInternalDial();
            if (emergencyNumber.getEmergencyUrns() != null) {
                arrayList = new ArrayList(emergencyNumber.getEmergencyUrns());
            } else {
                arrayList = new ArrayList();
            }
            iRadio2.emergencyDial(i, convertToHalDial2, emergencyServiceCategoryBitmaskInternalDial2, arrayList, emergencyNumber.getEmergencyCallRouting(), z, emergencyNumber.getEmergencyNumberSourceBitmask() == 32);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String[] lambda$emergencyDial$0(int i) {
        return new String[i];
    }

    public void exitEmergencyCallbackMode(int i) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.exitEmergencyCallbackMode(i);
        } else {
            this.mRadioProxy.exitEmergencyCallbackMode(i);
        }
    }

    public void explicitCallTransfer(int i) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.explicitCallTransfer(i);
        } else {
            this.mRadioProxy.explicitCallTransfer(i);
        }
    }

    public void getCallForwardStatus(int i, int i2, int i3, String str) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            android.hardware.radio.voice.CallForwardInfo callForwardInfo = new android.hardware.radio.voice.CallForwardInfo();
            callForwardInfo.reason = i2;
            callForwardInfo.serviceClass = i3;
            callForwardInfo.toa = PhoneNumberUtils.toaFromString(str);
            callForwardInfo.number = RILUtils.convertNullToEmptyString(str);
            callForwardInfo.timeSeconds = 0;
            this.mVoiceProxy.getCallForwardStatus(i, callForwardInfo);
            return;
        }
        android.hardware.radio.V1_0.CallForwardInfo callForwardInfo2 = new android.hardware.radio.V1_0.CallForwardInfo();
        callForwardInfo2.reason = i2;
        callForwardInfo2.serviceClass = i3;
        callForwardInfo2.toa = PhoneNumberUtils.toaFromString(str);
        callForwardInfo2.number = RILUtils.convertNullToEmptyString(str);
        callForwardInfo2.timeSeconds = 0;
        this.mRadioProxy.getCallForwardStatus(i, callForwardInfo2);
    }

    public void getCallWaiting(int i, int i2) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.getCallWaiting(i, i2);
        } else {
            this.mRadioProxy.getCallWaiting(i, i2);
        }
    }

    public void getClip(int i) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.getClip(i);
        } else {
            this.mRadioProxy.getClip(i);
        }
    }

    public void getClir(int i) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.getClir(i);
        } else {
            this.mRadioProxy.getClir(i);
        }
    }

    public void getCurrentCalls(int i) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.getCurrentCalls(i);
        } else if (this.mHalVersion.greaterOrEqual(RIL.RADIO_HAL_VERSION_1_6)) {
            this.mRadioProxy.getCurrentCalls_1_6(i);
        } else {
            this.mRadioProxy.getCurrentCalls(i);
        }
    }

    public void getLastCallFailCause(int i) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.getLastCallFailCause(i);
        } else {
            this.mRadioProxy.getLastCallFailCause(i);
        }
    }

    public void getMute(int i) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.getMute(i);
        } else {
            this.mRadioProxy.getMute(i);
        }
    }

    public void getPreferredVoicePrivacy(int i) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.getPreferredVoicePrivacy(i);
        } else {
            this.mRadioProxy.getPreferredVoicePrivacy(i);
        }
    }

    public void getTtyMode(int i) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.getTtyMode(i);
        } else {
            this.mRadioProxy.getTTYMode(i);
        }
    }

    public void handleStkCallSetupRequestFromSim(int i, boolean z) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.handleStkCallSetupRequestFromSim(i, z);
        } else {
            this.mRadioProxy.handleStkCallSetupRequestFromSim(i, z);
        }
    }

    public void hangup(int i, int i2) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.hangup(i, i2);
        } else {
            this.mRadioProxy.hangup(i, i2);
        }
    }

    public void hangupForegroundResumeBackground(int i) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.hangupForegroundResumeBackground(i);
        } else {
            this.mRadioProxy.hangupForegroundResumeBackground(i);
        }
    }

    public void hangupWaitingOrBackground(int i) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.hangupWaitingOrBackground(i);
        } else {
            this.mRadioProxy.hangupWaitingOrBackground(i);
        }
    }

    public void isVoNrEnabled(int i) throws RemoteException {
        if (!isEmpty() && isAidl()) {
            this.mVoiceProxy.isVoNrEnabled(i);
        }
    }

    public void rejectCall(int i) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.rejectCall(i);
        } else {
            this.mRadioProxy.rejectCall(i);
        }
    }

    @Override // com.android.internal.telephony.RadioServiceProxy
    public void responseAcknowledgement() throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.responseAcknowledgement();
        } else {
            this.mRadioProxy.responseAcknowledgement();
        }
    }

    public void sendBurstDtmf(int i, String str, int i2, int i3) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.sendBurstDtmf(i, str, i2, i3);
        } else {
            this.mRadioProxy.sendBurstDtmf(i, str, i2, i3);
        }
    }

    public void sendCdmaFeatureCode(int i, String str) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.sendCdmaFeatureCode(i, str);
        } else {
            this.mRadioProxy.sendCDMAFeatureCode(i, str);
        }
    }

    public void sendDtmf(int i, String str) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.sendDtmf(i, str);
        } else {
            this.mRadioProxy.sendDtmf(i, str);
        }
    }

    public void sendUssd(int i, String str) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.sendUssd(i, str);
        } else {
            this.mRadioProxy.sendUssd(i, str);
        }
    }

    public void separateConnection(int i, int i2) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.separateConnection(i, i2);
        } else {
            this.mRadioProxy.separateConnection(i, i2);
        }
    }

    public void setCallForward(int i, int i2, int i3, int i4, String str, int i5) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            android.hardware.radio.voice.CallForwardInfo callForwardInfo = new android.hardware.radio.voice.CallForwardInfo();
            callForwardInfo.status = i2;
            callForwardInfo.reason = i3;
            callForwardInfo.serviceClass = i4;
            callForwardInfo.toa = PhoneNumberUtils.toaFromString(str);
            callForwardInfo.number = RILUtils.convertNullToEmptyString(str);
            callForwardInfo.timeSeconds = i5;
            this.mVoiceProxy.setCallForward(i, callForwardInfo);
            return;
        }
        android.hardware.radio.V1_0.CallForwardInfo callForwardInfo2 = new android.hardware.radio.V1_0.CallForwardInfo();
        callForwardInfo2.status = i2;
        callForwardInfo2.reason = i3;
        callForwardInfo2.serviceClass = i4;
        callForwardInfo2.toa = PhoneNumberUtils.toaFromString(str);
        callForwardInfo2.number = RILUtils.convertNullToEmptyString(str);
        callForwardInfo2.timeSeconds = i5;
        this.mRadioProxy.setCallForward(i, callForwardInfo2);
    }

    public void setCallWaiting(int i, boolean z, int i2) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.setCallWaiting(i, z, i2);
        } else {
            this.mRadioProxy.setCallWaiting(i, z, i2);
        }
    }

    public void setClir(int i, int i2) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.setClir(i, i2);
        } else {
            this.mRadioProxy.setClir(i, i2);
        }
    }

    public void setMute(int i, boolean z) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.setMute(i, z);
        } else {
            this.mRadioProxy.setMute(i, z);
        }
    }

    public void setPreferredVoicePrivacy(int i, boolean z) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.setPreferredVoicePrivacy(i, z);
        } else {
            this.mRadioProxy.setPreferredVoicePrivacy(i, z);
        }
    }

    public void setTtyMode(int i, int i2) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.setTtyMode(i, i2);
        } else {
            this.mRadioProxy.setTTYMode(i, i2);
        }
    }

    public void setVoNrEnabled(int i, boolean z) throws RemoteException {
        if (!isEmpty() && isAidl()) {
            this.mVoiceProxy.setVoNrEnabled(i, z);
        }
    }

    public void startDtmf(int i, String str) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.startDtmf(i, str);
        } else {
            this.mRadioProxy.startDtmf(i, str);
        }
    }

    public void stopDtmf(int i) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.stopDtmf(i);
        } else {
            this.mRadioProxy.stopDtmf(i);
        }
    }

    public void switchWaitingOrHoldingAndActive(int i) throws RemoteException {
        if (isEmpty()) {
            return;
        }
        if (isAidl()) {
            this.mVoiceProxy.switchWaitingOrHoldingAndActive(i);
        } else {
            this.mRadioProxy.switchWaitingOrHoldingAndActive(i);
        }
    }
}
