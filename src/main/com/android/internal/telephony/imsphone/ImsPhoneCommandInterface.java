package com.android.internal.telephony.imsphone;

import android.content.Context;
import android.net.KeepalivePacketData;
import android.net.LinkProperties;
import android.os.Handler;
import android.os.Message;
import android.telephony.ImsiEncryptionInfo;
import android.telephony.NetworkScanRequest;
import android.telephony.SignalThresholdInfo;
import android.telephony.data.DataProfile;
import android.telephony.data.NetworkSliceInfo;
import android.telephony.data.TrafficDescriptor;
import android.telephony.emergency.EmergencyNumber;
import com.android.internal.telephony.BaseCommands;
import com.android.internal.telephony.RadioCapability;
import com.android.internal.telephony.UUSInfo;
import com.android.internal.telephony.cdma.CdmaSmsBroadcastConfigInfo;
import com.android.internal.telephony.gsm.SmsBroadcastConfigInfo;
import com.android.internal.telephony.uicc.IccCardApplicationStatus;
import java.util.List;
/* loaded from: classes.dex */
class ImsPhoneCommandInterface extends BaseCommands {
    @Override // com.android.internal.telephony.CommandsInterface
    public void acceptCall(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void acknowledgeIncomingGsmSmsWithPdu(boolean z, String str, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void acknowledgeLastIncomingCdmaSms(boolean z, int i, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void acknowledgeLastIncomingGsmSms(boolean z, int i, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void allocatePduSessionId(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void cancelHandover(Message message, int i) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void cancelPendingUssd(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void changeBarringPassword(String str, String str2, String str3, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void changeIccPin(String str, String str2, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void changeIccPin2(String str, String str2, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void changeIccPin2ForApp(String str, String str2, String str3, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void changeIccPinForApp(String str, String str2, String str3, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void conference(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void deactivateDataCall(int i, int i2, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void deleteSmsOnRuim(int i, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void deleteSmsOnSim(int i, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void dial(String str, boolean z, EmergencyNumber emergencyNumber, boolean z2, int i, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void dial(String str, boolean z, EmergencyNumber emergencyNumber, boolean z2, int i, UUSInfo uUSInfo, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void exitEmergencyCallbackMode(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void explicitCallTransfer(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getAllowedNetworkTypesBitmap(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getAvailableNetworks(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getBasebandVersion(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getCDMASubscription(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getCLIR(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getCdmaBroadcastConfig(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getCdmaSubscriptionSource(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getCurrentCalls(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getDataCallList(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getDataRegistrationState(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getDeviceIdentity(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getGsmBroadcastConfig(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getHardwareConfig(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getIMEI(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getIMEISV(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getIMSI(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getIMSIForApp(String str, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getIccCardStatus(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getIccSlotsStatus(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getImei(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getImsRegistrationState(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getLastCallFailCause(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getLastDataCallFailCause(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    @Deprecated
    public void getLastPdpFailCause(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getMute(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getNetworkSelectionMode(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getOperator(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    @Deprecated
    public void getPDPContextList(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getPreferredNetworkType(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getPreferredVoicePrivacy(Message message) {
    }

    @Override // com.android.internal.telephony.BaseCommands, com.android.internal.telephony.CommandsInterface
    public void getRadioCapability(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getSignalStrength(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getSmscAddress(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getVoiceRadioTechnology(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getVoiceRegistrationState(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void handleCallSetupRequestFromSim(boolean z, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void hangupConnection(int i, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void hangupForegroundResumeBackground(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void hangupWaitingOrBackground(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void iccCloseLogicalChannel(int i, boolean z, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void iccIO(int i, int i2, String str, int i3, int i4, int i5, String str2, String str3, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void iccIOForApp(int i, int i2, String str, int i3, int i4, int i5, String str2, String str3, String str4, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void iccOpenLogicalChannel(String str, int i, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void iccTransmitApduBasicChannel(int i, int i2, int i3, int i4, int i5, String str, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void iccTransmitApduLogicalChannel(int i, int i2, int i3, int i4, int i5, int i6, String str, boolean z, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void invokeOemRilRequestRaw(byte[] bArr, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void invokeOemRilRequestStrings(String[] strArr, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void nvResetConfig(int i, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void nvWriteCdmaPrl(byte[] bArr, Message message) {
    }

    @Override // com.android.internal.telephony.BaseCommands, com.android.internal.telephony.CommandsInterface
    public void pullLceData(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void queryAvailableBandMode(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void queryCLIP(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void queryCallForwardStatus(int i, int i2, String str, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void queryCallWaiting(int i, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void queryCdmaRoamingPreference(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void queryFacilityLock(String str, String str2, int i, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void queryFacilityLockForApp(String str, String str2, int i, String str3, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void queryTTYMode(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void rejectCall(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void releasePduSessionId(Message message, int i) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void reportSmsMemoryStatus(boolean z, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void reportStkServiceIsRunning(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void requestIccSimAuthentication(int i, String str, String str2, Message message) {
    }

    @Override // com.android.internal.telephony.BaseCommands, com.android.internal.telephony.CommandsInterface
    public void requestShutdown(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void resetRadio(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendBurstDtmf(String str, int i, int i2, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendCDMAFeatureCode(String str, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendCdmaSMSExpectMore(byte[] bArr, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendCdmaSms(byte[] bArr, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendDeviceState(int i, boolean z, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendDtmf(char c, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendEnvelope(String str, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendEnvelopeWithStatus(String str, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendImsCdmaSms(byte[] bArr, int i, int i2, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendImsGsmSms(String str, String str2, int i, int i2, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendSMS(String str, String str2, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendSMSExpectMore(String str, String str2, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendTerminalResponse(String str, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendUSSD(String str, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void separateConnection(int i, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setAllowedNetworkTypesBitmap(int i, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setBandMode(int i, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setCLIR(int i, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setCallForward(int i, int i2, int i3, String str, int i4, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setCallWaiting(boolean z, int i, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setCarrierInfoForImsiEncryption(ImsiEncryptionInfo imsiEncryptionInfo, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setCdmaBroadcastActivation(boolean z, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setCdmaBroadcastConfig(CdmaSmsBroadcastConfigInfo[] cdmaSmsBroadcastConfigInfoArr, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setCdmaRoamingPreference(int i, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setCdmaSubscriptionSource(int i, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setDataProfile(DataProfile[] dataProfileArr, boolean z, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setFacilityLock(String str, boolean z, String str2, int i, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setFacilityLockForApp(String str, boolean z, String str2, int i, String str3, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setGsmBroadcastActivation(boolean z, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setGsmBroadcastConfig(SmsBroadcastConfigInfo[] smsBroadcastConfigInfoArr, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setInitialAttachApn(DataProfile dataProfile, boolean z, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setLinkCapacityReportingCriteria(int i, int i2, int i3, int[] iArr, int[] iArr2, int i4, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setLocationUpdates(boolean z, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setLogicalToPhysicalSlotMapping(int[] iArr, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setMute(boolean z, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setNetworkSelectionModeAutomatic(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setNetworkSelectionModeManual(String str, int i, Message message) {
    }

    @Override // com.android.internal.telephony.BaseCommands, com.android.internal.telephony.CommandsInterface
    public void setOnNITZTime(Handler handler, int i, Object obj) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setPhoneType(int i) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setPreferredNetworkType(int i, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setPreferredVoicePrivacy(boolean z, Message message) {
    }

    @Override // com.android.internal.telephony.BaseCommands, com.android.internal.telephony.CommandsInterface
    public void setRadioCapability(RadioCapability radioCapability, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setRadioPower(boolean z, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setSignalStrengthReportingCriteria(List<SignalThresholdInfo> list, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setSmscAddress(String str, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setSuppServiceNotifications(boolean z, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setTTYMode(int i, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setUnsolResponseFilter(int i, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setupDataCall(int i, DataProfile dataProfile, boolean z, boolean z2, int i2, LinkProperties linkProperties, int i3, NetworkSliceInfo networkSliceInfo, TrafficDescriptor trafficDescriptor, boolean z3, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void startDtmf(char c, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void startHandover(Message message, int i) {
    }

    @Override // com.android.internal.telephony.BaseCommands, com.android.internal.telephony.CommandsInterface
    public void startLceService(int i, boolean z, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void startNattKeepalive(int i, KeepalivePacketData keepalivePacketData, int i2, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void startNetworkScan(NetworkScanRequest networkScanRequest, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void stopDtmf(Message message) {
    }

    @Override // com.android.internal.telephony.BaseCommands, com.android.internal.telephony.CommandsInterface
    public void stopLceService(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void stopNattKeepalive(int i, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void stopNetworkScan(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void supplyIccPin(String str, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void supplyIccPin2(String str, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void supplyIccPin2ForApp(String str, String str2, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void supplyIccPinForApp(String str, String str2, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void supplyIccPuk(String str, String str2, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void supplyIccPuk2(String str, String str2, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void supplyIccPuk2ForApp(String str, String str2, String str3, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void supplyIccPukForApp(String str, String str2, String str3, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void supplyNetworkDepersonalization(String str, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void supplySimDepersonalization(IccCardApplicationStatus.PersoSubState persoSubState, String str, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void switchWaitingOrHoldingAndActive(Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void writeSmsToRuim(int i, byte[] bArr, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void writeSmsToSim(int i, String str, String str2, Message message) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ImsPhoneCommandInterface(Context context) {
        super(context);
    }
}
