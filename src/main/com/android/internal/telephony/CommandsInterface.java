package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
import android.net.KeepalivePacketData;
import android.net.LinkProperties;
import android.os.Handler;
import android.os.Message;
import android.os.WorkSource;
import android.telephony.BarringInfo;
import android.telephony.CarrierRestrictionRules;
import android.telephony.ClientRequestStats;
import android.telephony.ImsiEncryptionInfo;
import android.telephony.NetworkScanRequest;
import android.telephony.RadioAccessSpecifier;
import android.telephony.SignalThresholdInfo;
import android.telephony.data.DataProfile;
import android.telephony.data.NetworkSliceInfo;
import android.telephony.data.TrafficDescriptor;
import android.telephony.emergency.EmergencyNumber;
import com.android.internal.telephony.cdma.CdmaSmsBroadcastConfigInfo;
import com.android.internal.telephony.gsm.SmsBroadcastConfigInfo;
import com.android.internal.telephony.imsphone.ImsCallInfo;
import com.android.internal.telephony.uicc.IccCardApplicationStatus;
import com.android.internal.telephony.uicc.SimPhonebookRecord;
import java.util.List;
/* loaded from: classes.dex */
public interface CommandsInterface {
    public static final String CB_FACILITY_BAIC = "AI";
    public static final String CB_FACILITY_BAICr = "IR";
    public static final String CB_FACILITY_BAOC = "AO";
    public static final String CB_FACILITY_BAOIC = "OI";
    public static final String CB_FACILITY_BAOICxH = "OX";
    public static final String CB_FACILITY_BA_ALL = "AB";
    public static final String CB_FACILITY_BA_FD = "FD";
    public static final String CB_FACILITY_BA_MO = "AG";
    public static final String CB_FACILITY_BA_MT = "AC";
    public static final String CB_FACILITY_BA_SIM = "SC";
    public static final String CB_FACILITY_BIC_ACR = "AR";
    public static final int CDMA_SMS_FAIL_CAUSE_ENCODING_PROBLEM = 96;
    public static final int CDMA_SMS_FAIL_CAUSE_INVALID_TELESERVICE_ID = 4;
    public static final int CDMA_SMS_FAIL_CAUSE_OTHER_TERMINAL_PROBLEM = 39;
    public static final int CDMA_SMS_FAIL_CAUSE_RESOURCE_SHORTAGE = 35;
    public static final int CF_ACTION_DISABLE = 0;
    public static final int CF_ACTION_ENABLE = 1;
    public static final int CF_ACTION_ERASURE = 4;
    public static final int CF_ACTION_REGISTRATION = 3;
    public static final int CF_REASON_ALL = 4;
    public static final int CF_REASON_ALL_CONDITIONAL = 5;
    public static final int CF_REASON_BUSY = 1;
    public static final int CF_REASON_NOT_REACHABLE = 3;
    public static final int CF_REASON_NO_REPLY = 2;
    public static final int CF_REASON_UNCONDITIONAL = 0;
    public static final int CLIR_DEFAULT = 0;
    public static final int CLIR_INVOCATION = 1;
    public static final int CLIR_SUPPRESSION = 2;
    public static final int GSM_SMS_FAIL_CAUSE_MEMORY_CAPACITY_EXCEEDED = 211;
    public static final int GSM_SMS_FAIL_CAUSE_UNSPECIFIED_ERROR = 255;
    public static final int GSM_SMS_FAIL_CAUSE_USIM_APP_TOOLKIT_BUSY = 212;
    public static final int GSM_SMS_FAIL_CAUSE_USIM_DATA_DOWNLOAD_ERROR = 213;
    public static final int IMS_MMTEL_CAPABILITY_SMS = 4;
    public static final int IMS_MMTEL_CAPABILITY_VIDEO = 2;
    public static final int IMS_MMTEL_CAPABILITY_VOICE = 1;
    public static final int IMS_RCS_CAPABILITIES = 8;
    public static final int SERVICE_CLASS_DATA = 2;
    public static final int SERVICE_CLASS_DATA_ASYNC = 32;
    public static final int SERVICE_CLASS_DATA_SYNC = 16;
    public static final int SERVICE_CLASS_FAX = 4;
    public static final int SERVICE_CLASS_MAX = 128;
    public static final int SERVICE_CLASS_NONE = 0;
    public static final int SERVICE_CLASS_PACKET = 64;
    public static final int SERVICE_CLASS_PAD = 128;
    public static final int SERVICE_CLASS_SMS = 8;
    public static final int SERVICE_CLASS_VOICE = 1;
    public static final int SS_STATUS_UNKNOWN = 255;
    public static final int USSD_MODE_LOCAL_CLIENT = 3;
    public static final int USSD_MODE_NOTIFY = 0;
    public static final int USSD_MODE_NOT_SUPPORTED = 4;
    public static final int USSD_MODE_NW_RELEASE = 2;
    public static final int USSD_MODE_NW_TIMEOUT = 5;
    public static final int USSD_MODE_REQUEST = 1;

    @UnsupportedAppUsage
    void acceptCall(Message message);

    void acknowledgeIncomingGsmSmsWithPdu(boolean z, String str, Message message);

    @UnsupportedAppUsage
    void acknowledgeLastIncomingCdmaSms(boolean z, int i, Message message);

    @UnsupportedAppUsage
    void acknowledgeLastIncomingGsmSms(boolean z, int i, Message message);

    default void addAllowedSatelliteContacts(Message message, String[] strArr) {
    }

    default void allocatePduSessionId(Message message) {
    }

    default void areUiccApplicationsEnabled(Message message) {
    }

    default boolean canToggleUiccApplicationsEnablement() {
        return false;
    }

    default void cancelEmergencyNetworkScan(boolean z, Message message) {
    }

    default void cancelHandover(Message message, int i) {
    }

    void cancelPendingUssd(Message message);

    @UnsupportedAppUsage
    void changeBarringPassword(String str, String str2, String str3, Message message);

    void changeIccPin(String str, String str2, Message message);

    void changeIccPin2(String str, String str2, Message message);

    void changeIccPin2ForApp(String str, String str2, String str3, Message message);

    void changeIccPinForApp(String str, String str2, String str3, Message message);

    void conference(Message message);

    void deactivateDataCall(int i, int i2, Message message);

    @UnsupportedAppUsage
    void deleteSmsOnRuim(int i, Message message);

    @UnsupportedAppUsage
    void deleteSmsOnSim(int i, Message message);

    void dial(String str, boolean z, EmergencyNumber emergencyNumber, boolean z2, int i, Message message);

    void dial(String str, boolean z, EmergencyNumber emergencyNumber, boolean z2, int i, UUSInfo uUSInfo, Message message);

    default void enableModem(boolean z, Message message) {
    }

    default void enableUiccApplications(boolean z, Message message) {
    }

    @UnsupportedAppUsage
    void exitEmergencyCallbackMode(Message message);

    default void exitEmergencyMode(Message message) {
    }

    void explicitCallTransfer(Message message);

    default void getAllowedCarriers(Message message, WorkSource workSource) {
    }

    void getAllowedNetworkTypesBitmap(Message message);

    void getAvailableNetworks(Message message);

    default void getBarringInfo(Message message) {
    }

    @UnsupportedAppUsage
    void getBasebandVersion(Message message);

    @UnsupportedAppUsage
    void getCDMASubscription(Message message);

    void getCLIR(Message message);

    @UnsupportedAppUsage
    void getCdmaBroadcastConfig(Message message);

    void getCdmaSubscriptionSource(Message message);

    default void getCellInfoList(Message message, WorkSource workSource) {
    }

    default List<ClientRequestStats> getClientRequestStats() {
        return null;
    }

    void getCurrentCalls(Message message);

    @UnsupportedAppUsage
    void getDataCallList(Message message);

    void getDataRegistrationState(Message message);

    void getDeviceIdentity(Message message);

    void getGsmBroadcastConfig(Message message);

    void getHardwareConfig(Message message);

    void getIMEI(Message message);

    @UnsupportedAppUsage
    void getIMEISV(Message message);

    @UnsupportedAppUsage
    void getIMSI(Message message);

    void getIMSIForApp(String str, Message message);

    @UnsupportedAppUsage
    void getIccCardStatus(Message message);

    void getIccSlotsStatus(Message message);

    void getImei(Message message);

    void getImsRegistrationState(Message message);

    default BarringInfo getLastBarringInfo() {
        return null;
    }

    void getLastCallFailCause(Message message);

    @UnsupportedAppUsage
    void getLastDataCallFailCause(Message message);

    @UnsupportedAppUsage
    @Deprecated
    void getLastPdpFailCause(Message message);

    default void getMaxCharactersPerSatelliteTextMessage(Message message) {
    }

    default void getModemActivityInfo(Message message, WorkSource workSource) {
    }

    default String getModemService() {
        return "default";
    }

    default void getModemStatus(Message message) {
    }

    void getMute(Message message);

    @UnsupportedAppUsage
    void getNetworkSelectionMode(Message message);

    @UnsupportedAppUsage
    void getOperator(Message message);

    @UnsupportedAppUsage
    @Deprecated
    void getPDPContextList(Message message);

    default void getPendingSatelliteMessages(Message message) {
    }

    @UnsupportedAppUsage
    void getPreferredNetworkType(Message message);

    void getPreferredVoicePrivacy(Message message);

    void getRadioCapability(Message message);

    int getRadioState();

    int getRilVersion();

    default void getSatelliteCapabilities(Message message) {
    }

    default void getSatelliteMode(Message message) {
    }

    default void getSatellitePowerState(Message message) {
    }

    @UnsupportedAppUsage
    void getSignalStrength(Message message);

    void getSimPhonebookCapacity(Message message);

    void getSimPhonebookRecords(Message message);

    default void getSlicingConfig(Message message) {
    }

    @UnsupportedAppUsage
    void getSmscAddress(Message message);

    default void getSystemSelectionChannels(Message message) {
    }

    default void getTimeForNextSatelliteVisibility(Message message) {
    }

    default void getUsageSetting(Message message) {
    }

    void getVoiceRadioTechnology(Message message);

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    void getVoiceRegistrationState(Message message);

    @UnsupportedAppUsage
    void handleCallSetupRequestFromSim(boolean z, Message message);

    void hangupConnection(int i, Message message);

    void hangupForegroundResumeBackground(Message message);

    void hangupWaitingOrBackground(Message message);

    void iccCloseLogicalChannel(int i, boolean z, Message message);

    @UnsupportedAppUsage
    void iccIO(int i, int i2, String str, int i3, int i4, int i5, String str2, String str3, Message message);

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    void iccIOForApp(int i, int i2, String str, int i3, int i4, int i5, String str2, String str3, String str4, Message message);

    void iccOpenLogicalChannel(String str, int i, Message message);

    void iccTransmitApduBasicChannel(int i, int i2, int i3, int i4, int i5, String str, Message message);

    void iccTransmitApduLogicalChannel(int i, int i2, int i3, int i4, int i5, int i6, String str, boolean z, Message message);

    @UnsupportedAppUsage
    void invokeOemRilRequestRaw(byte[] bArr, Message message);

    void invokeOemRilRequestStrings(String[] strArr, Message message);

    default void isN1ModeEnabled(Message message) {
    }

    default void isNrDualConnectivityEnabled(Message message, WorkSource workSource) {
    }

    default void isSatelliteCommunicationAllowedForCurrentLocation(Message message) {
    }

    default void isSatelliteSupported(Message message) {
    }

    default void isVoNrEnabled(Message message, WorkSource workSource) {
    }

    default void nvReadItem(int i, Message message, WorkSource workSource) {
    }

    void nvResetConfig(int i, Message message);

    void nvWriteCdmaPrl(byte[] bArr, Message message);

    default void nvWriteItem(int i, String str, Message message, WorkSource workSource) {
    }

    default void onSlotActiveStatusChange(boolean z) {
    }

    default void provisionSatelliteService(Message message, String str, String str2, String str3, int[] iArr) {
    }

    void pullLceData(Message message);

    void queryAvailableBandMode(Message message);

    void queryCLIP(Message message);

    @UnsupportedAppUsage
    void queryCallForwardStatus(int i, int i2, String str, Message message);

    @UnsupportedAppUsage
    void queryCallWaiting(int i, Message message);

    void queryCdmaRoamingPreference(Message message);

    @UnsupportedAppUsage
    void queryFacilityLock(String str, String str2, int i, Message message);

    void queryFacilityLockForApp(String str, String str2, int i, String str3, Message message);

    @UnsupportedAppUsage
    void queryTTYMode(Message message);

    void registerFoT53ClirlInfo(Handler handler, int i, Object obj);

    void registerForApnUnthrottled(Handler handler, int i, Object obj);

    @UnsupportedAppUsage
    void registerForAvailable(Handler handler, int i, Object obj);

    default void registerForBarringInfoChanged(Handler handler, int i, Object obj) {
    }

    void registerForCallStateChanged(Handler handler, int i, Object obj);

    void registerForCallWaitingInfo(Handler handler, int i, Object obj);

    void registerForCarrierInfoForImsiEncryption(Handler handler, int i, Object obj);

    @UnsupportedAppUsage
    void registerForCdmaOtaProvision(Handler handler, int i, Object obj);

    void registerForCdmaPrlChanged(Handler handler, int i, Object obj);

    void registerForCdmaSubscriptionChanged(Handler handler, int i, Object obj);

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    void registerForCellInfoList(Handler handler, int i, Object obj);

    default void registerForConnectionSetupFailure(Handler handler, int i, Object obj) {
    }

    void registerForDataCallListChanged(Handler handler, int i, Object obj);

    void registerForDisplayInfo(Handler handler, int i, Object obj);

    default void registerForEmergencyNetworkScan(Handler handler, int i, Object obj) {
    }

    void registerForEmergencyNumberList(Handler handler, int i, Object obj);

    void registerForExitEmergencyCallbackMode(Handler handler, int i, Object obj);

    void registerForHardwareConfigChanged(Handler handler, int i, Object obj);

    @UnsupportedAppUsage
    void registerForIccRefresh(Handler handler, int i, Object obj);

    void registerForIccSlotStatusChanged(Handler handler, int i, Object obj);

    void registerForIccStatusChanged(Handler handler, int i, Object obj);

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    void registerForImsNetworkStateChanged(Handler handler, int i, Object obj);

    void registerForInCallVoicePrivacyOff(Handler handler, int i, Object obj);

    void registerForInCallVoicePrivacyOn(Handler handler, int i, Object obj);

    void registerForLceInfo(Handler handler, int i, Object obj);

    void registerForLineControlInfo(Handler handler, int i, Object obj);

    void registerForModemReset(Handler handler, int i, Object obj);

    void registerForNattKeepaliveStatus(Handler handler, int i, Object obj);

    void registerForNetworkScanResult(Handler handler, int i, Object obj);

    void registerForNetworkStateChanged(Handler handler, int i, Object obj);

    default void registerForNewSatelliteMessages(Handler handler, int i, Object obj) {
    }

    @UnsupportedAppUsage
    void registerForNotAvailable(Handler handler, int i, Object obj);

    default void registerForNotifyAnbr(Handler handler, int i, Object obj) {
    }

    void registerForNumberInfo(Handler handler, int i, Object obj);

    @UnsupportedAppUsage
    void registerForOffOrNotAvailable(Handler handler, int i, Object obj);

    @UnsupportedAppUsage
    void registerForOn(Handler handler, int i, Object obj);

    void registerForPcoData(Handler handler, int i, Object obj);

    default void registerForPendingSatelliteMessageCount(Handler handler, int i, Object obj) {
    }

    void registerForPhysicalChannelConfiguration(Handler handler, int i, Object obj);

    void registerForRadioCapabilityChanged(Handler handler, int i, Object obj);

    @UnsupportedAppUsage
    void registerForRadioStateChanged(Handler handler, int i, Object obj);

    void registerForRedirectedNumberInfo(Handler handler, int i, Object obj);

    void registerForResendIncallMute(Handler handler, int i, Object obj);

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    void registerForRilConnected(Handler handler, int i, Object obj);

    void registerForRingbackTone(Handler handler, int i, Object obj);

    default void registerForSatelliteMessagesTransferComplete(Handler handler, int i, Object obj) {
    }

    default void registerForSatelliteModeChanged(Handler handler, int i, Object obj) {
    }

    default void registerForSatellitePointingInfoChanged(Handler handler, int i, Object obj) {
    }

    default void registerForSatelliteProvisionStateChanged(Handler handler, int i, Object obj) {
    }

    default void registerForSatelliteRadioTechnologyChanged(Handler handler, int i, Object obj) {
    }

    void registerForSignalInfo(Handler handler, int i, Object obj);

    void registerForSimPhonebookChanged(Handler handler, int i, Object obj);

    void registerForSimPhonebookRecordsReceived(Handler handler, int i, Object obj);

    void registerForSlicingConfigChanged(Handler handler, int i, Object obj);

    void registerForSrvccStateChanged(Handler handler, int i, Object obj);

    void registerForSubscriptionStatusChanged(Handler handler, int i, Object obj);

    void registerForT53AudioControlInfo(Handler handler, int i, Object obj);

    default void registerForTriggerImsDeregistration(Handler handler, int i, Object obj) {
    }

    void registerForVoiceRadioTechChanged(Handler handler, int i, Object obj);

    default void registerUiccApplicationEnablementChanged(Handler handler, int i, Object obj) {
    }

    void rejectCall(Message message);

    default void releasePduSessionId(Message message, int i) {
    }

    default void removeAllowedSatelliteContacts(Message message, String[] strArr) {
    }

    @UnsupportedAppUsage
    void reportSmsMemoryStatus(boolean z, Message message);

    @UnsupportedAppUsage
    void reportStkServiceIsRunning(Message message);

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    void requestIccSimAuthentication(int i, String str, String str2, Message message);

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    void requestShutdown(Message message);

    void resetRadio(Message message);

    default void sendAnbrQuery(int i, int i2, int i3, Message message) {
    }

    void sendBurstDtmf(String str, int i, int i2, Message message);

    void sendCDMAFeatureCode(String str, Message message);

    void sendCdmaSMSExpectMore(byte[] bArr, Message message);

    void sendCdmaSms(byte[] bArr, Message message);

    void sendDeviceState(int i, boolean z, Message message);

    @UnsupportedAppUsage
    void sendDtmf(char c, Message message);

    @UnsupportedAppUsage
    void sendEnvelope(String str, Message message);

    void sendEnvelopeWithStatus(String str, Message message);

    void sendImsCdmaSms(byte[] bArr, int i, int i2, Message message);

    void sendImsGsmSms(String str, String str2, int i, int i2, Message message);

    void sendSMS(String str, String str2, Message message);

    void sendSMSExpectMore(String str, String str2, Message message);

    default void sendSatelliteMessages(Message message, String[] strArr, String str, double d, double d2) {
    }

    @UnsupportedAppUsage
    void sendTerminalResponse(String str, Message message);

    void sendUSSD(String str, Message message);

    void separateConnection(int i, Message message);

    default void setAllowedCarriers(CarrierRestrictionRules carrierRestrictionRules, Message message, WorkSource workSource) {
    }

    void setAllowedNetworkTypesBitmap(int i, Message message);

    void setBandMode(int i, Message message);

    void setCLIR(int i, Message message);

    @UnsupportedAppUsage
    void setCallForward(int i, int i2, int i3, String str, int i4, Message message);

    @UnsupportedAppUsage
    void setCallWaiting(boolean z, int i, Message message);

    void setCarrierInfoForImsiEncryption(ImsiEncryptionInfo imsiEncryptionInfo, Message message);

    @UnsupportedAppUsage
    void setCdmaBroadcastActivation(boolean z, Message message);

    void setCdmaBroadcastConfig(CdmaSmsBroadcastConfigInfo[] cdmaSmsBroadcastConfigInfoArr, Message message);

    void setCdmaRoamingPreference(int i, Message message);

    void setCdmaSubscriptionSource(int i, Message message);

    default void setCellInfoListRate(int i, Message message, WorkSource workSource) {
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    void setDataAllowed(boolean z, Message message);

    void setDataProfile(DataProfile[] dataProfileArr, boolean z, Message message);

    default void setDataThrottling(Message message, WorkSource workSource, int i, long j) {
    }

    @UnsupportedAppUsage
    void setEmergencyCallbackMode(Handler handler, int i, Object obj);

    default void setEmergencyMode(int i, Message message) {
    }

    @UnsupportedAppUsage
    void setFacilityLock(String str, boolean z, String str2, int i, Message message);

    void setFacilityLockForApp(String str, boolean z, String str2, int i, String str3, Message message);

    void setGsmBroadcastActivation(boolean z, Message message);

    void setGsmBroadcastConfig(SmsBroadcastConfigInfo[] smsBroadcastConfigInfoArr, Message message);

    void setInitialAttachApn(DataProfile dataProfile, boolean z, Message message);

    void setLinkCapacityReportingCriteria(int i, int i2, int i3, int[] iArr, int[] iArr2, int i4, Message message);

    default void setLocationUpdates(boolean z, Message message) {
    }

    default void setLocationUpdates(boolean z, WorkSource workSource, Message message) {
    }

    void setLogicalToPhysicalSlotMapping(int[] iArr, Message message);

    default boolean setModemService(String str) {
        return true;
    }

    void setMute(boolean z, Message message);

    default void setN1ModeEnabled(boolean z, Message message) {
    }

    @UnsupportedAppUsage
    void setNetworkSelectionModeAutomatic(Message message);

    @UnsupportedAppUsage
    void setNetworkSelectionModeManual(String str, int i, Message message);

    default void setNrDualConnectivityState(int i, Message message, WorkSource workSource) {
    }

    default void setNullCipherAndIntegrityEnabled(boolean z, Message message) {
    }

    @UnsupportedAppUsage
    void setOnCallRing(Handler handler, int i, Object obj);

    @UnsupportedAppUsage
    void setOnCatCallSetUp(Handler handler, int i, Object obj);

    @UnsupportedAppUsage
    void setOnCatCcAlphaNotify(Handler handler, int i, Object obj);

    @UnsupportedAppUsage
    void setOnCatEvent(Handler handler, int i, Object obj);

    @UnsupportedAppUsage
    void setOnCatProactiveCmd(Handler handler, int i, Object obj);

    @UnsupportedAppUsage
    void setOnCatSessionEnd(Handler handler, int i, Object obj);

    @UnsupportedAppUsage
    void setOnIccRefresh(Handler handler, int i, Object obj);

    @UnsupportedAppUsage
    void setOnIccSmsFull(Handler handler, int i, Object obj);

    @UnsupportedAppUsage
    void setOnNITZTime(Handler handler, int i, Object obj);

    void setOnNewCdmaSms(Handler handler, int i, Object obj);

    @UnsupportedAppUsage
    void setOnNewGsmBroadcastSms(Handler handler, int i, Object obj);

    void setOnNewGsmSms(Handler handler, int i, Object obj);

    default void setOnRegistrationFailed(Handler handler, int i, Object obj) {
    }

    void setOnRestrictedStateChanged(Handler handler, int i, Object obj);

    @UnsupportedAppUsage
    void setOnSignalStrengthUpdate(Handler handler, int i, Object obj);

    @UnsupportedAppUsage
    void setOnSmsOnSim(Handler handler, int i, Object obj);

    @UnsupportedAppUsage
    void setOnSmsStatus(Handler handler, int i, Object obj);

    void setOnSs(Handler handler, int i, Object obj);

    @UnsupportedAppUsage
    void setOnSuppServiceNotification(Handler handler, int i, Object obj);

    void setOnUSSD(Handler handler, int i, Object obj);

    void setOnUnsolOemHookRaw(Handler handler, int i, Object obj);

    @UnsupportedAppUsage
    void setPhoneType(int i);

    @UnsupportedAppUsage
    void setPreferredNetworkType(int i, Message message);

    void setPreferredVoicePrivacy(boolean z, Message message);

    void setRadioCapability(RadioCapability radioCapability, Message message);

    default void setRadioPower(boolean z, boolean z2, boolean z3, Message message) {
    }

    default void setSatelliteIndicationFilter(Message message, int i) {
    }

    default void setSatellitePower(Message message, boolean z) {
    }

    void setSignalStrengthReportingCriteria(List<SignalThresholdInfo> list, Message message);

    default void setSimCardPower(int i, Message message, WorkSource workSource) {
    }

    @UnsupportedAppUsage
    void setSmscAddress(String str, Message message);

    default void setSrvccCallInfo(SrvccConnection[] srvccConnectionArr, Message message) {
    }

    void setSuppServiceNotifications(boolean z, Message message);

    default void setSystemSelectionChannels(List<RadioAccessSpecifier> list, Message message) {
    }

    @UnsupportedAppUsage
    void setTTYMode(int i, Message message);

    @UnsupportedAppUsage
    void setUiccSubscription(int i, int i2, int i3, int i4, Message message);

    void setUnsolResponseFilter(int i, Message message);

    default void setUsageSetting(Message message, int i) {
    }

    default void setVoNrEnabled(boolean z, Message message, WorkSource workSource) {
    }

    void setupDataCall(int i, DataProfile dataProfile, boolean z, boolean z2, int i2, LinkProperties linkProperties, int i3, NetworkSliceInfo networkSliceInfo, TrafficDescriptor trafficDescriptor, boolean z3, Message message);

    void startDtmf(char c, Message message);

    default void startHandover(Message message, int i) {
    }

    default void startImsTraffic(int i, int i2, int i3, int i4, Message message) {
    }

    void startLceService(int i, boolean z, Message message);

    void startNattKeepalive(int i, KeepalivePacketData keepalivePacketData, int i2, Message message);

    void startNetworkScan(NetworkScanRequest networkScanRequest, Message message);

    default void startSendingSatellitePointingInfo(Message message) {
    }

    void stopDtmf(Message message);

    default void stopImsTraffic(int i, Message message) {
    }

    void stopLceService(Message message);

    void stopNattKeepalive(int i, Message message);

    void stopNetworkScan(Message message);

    default void stopSendingSatellitePointingInfo(Message message) {
    }

    @UnsupportedAppUsage
    void supplyIccPin(String str, Message message);

    void supplyIccPin2(String str, Message message);

    void supplyIccPin2ForApp(String str, String str2, Message message);

    void supplyIccPinForApp(String str, String str2, Message message);

    void supplyIccPuk(String str, String str2, Message message);

    void supplyIccPuk2(String str, String str2, Message message);

    void supplyIccPuk2ForApp(String str, String str2, String str3, Message message);

    void supplyIccPukForApp(String str, String str2, String str3, Message message);

    void supplyNetworkDepersonalization(String str, Message message);

    void supplySimDepersonalization(IccCardApplicationStatus.PersoSubState persoSubState, String str, Message message);

    default boolean supportsEid() {
        return false;
    }

    @UnsupportedAppUsage
    void switchWaitingOrHoldingAndActive(Message message);

    void testingEmergencyCall();

    default void triggerEmergencyNetworkScan(int[] iArr, int i, Message message) {
    }

    default void triggerEpsFallback(int i, Message message) {
    }

    void unSetOnCallRing(Handler handler);

    void unSetOnCatCallSetUp(Handler handler);

    void unSetOnCatCcAlphaNotify(Handler handler);

    void unSetOnCatEvent(Handler handler);

    void unSetOnCatProactiveCmd(Handler handler);

    void unSetOnCatSessionEnd(Handler handler);

    void unSetOnIccSmsFull(Handler handler);

    void unSetOnNITZTime(Handler handler);

    void unSetOnNewCdmaSms(Handler handler);

    void unSetOnNewGsmBroadcastSms(Handler handler);

    void unSetOnNewGsmSms(Handler handler);

    default void unSetOnRegistrationFailed(Handler handler) {
    }

    void unSetOnRestrictedStateChanged(Handler handler);

    void unSetOnSignalStrengthUpdate(Handler handler);

    void unSetOnSmsOnSim(Handler handler);

    void unSetOnSmsStatus(Handler handler);

    void unSetOnSs(Handler handler);

    void unSetOnSuppServiceNotification(Handler handler);

    void unSetOnUSSD(Handler handler);

    void unSetOnUnsolOemHookRaw(Handler handler);

    void unregisterForApnUnthrottled(Handler handler);

    @UnsupportedAppUsage
    void unregisterForAvailable(Handler handler);

    default void unregisterForBarringInfoChanged(Handler handler) {
    }

    void unregisterForCallStateChanged(Handler handler);

    void unregisterForCallWaitingInfo(Handler handler);

    void unregisterForCarrierInfoForImsiEncryption(Handler handler);

    @UnsupportedAppUsage
    void unregisterForCdmaOtaProvision(Handler handler);

    void unregisterForCdmaPrlChanged(Handler handler);

    void unregisterForCdmaSubscriptionChanged(Handler handler);

    void unregisterForCellInfoList(Handler handler);

    default void unregisterForConnectionSetupFailure(Handler handler) {
    }

    void unregisterForDataCallListChanged(Handler handler);

    void unregisterForDisplayInfo(Handler handler);

    default void unregisterForEmergencyNetworkScan(Handler handler) {
    }

    void unregisterForEmergencyNumberList(Handler handler);

    void unregisterForExitEmergencyCallbackMode(Handler handler);

    void unregisterForHardwareConfigChanged(Handler handler);

    void unregisterForIccRefresh(Handler handler);

    void unregisterForIccSlotStatusChanged(Handler handler);

    void unregisterForIccStatusChanged(Handler handler);

    void unregisterForImsNetworkStateChanged(Handler handler);

    void unregisterForInCallVoicePrivacyOff(Handler handler);

    void unregisterForInCallVoicePrivacyOn(Handler handler);

    void unregisterForLceInfo(Handler handler);

    void unregisterForLineControlInfo(Handler handler);

    void unregisterForModemReset(Handler handler);

    void unregisterForNattKeepaliveStatus(Handler handler);

    void unregisterForNetworkScanResult(Handler handler);

    void unregisterForNetworkStateChanged(Handler handler);

    default void unregisterForNewSatelliteMessages(Handler handler) {
    }

    void unregisterForNotAvailable(Handler handler);

    default void unregisterForNotifyAnbr(Handler handler) {
    }

    void unregisterForNumberInfo(Handler handler);

    @UnsupportedAppUsage
    void unregisterForOffOrNotAvailable(Handler handler);

    @UnsupportedAppUsage
    void unregisterForOn(Handler handler);

    void unregisterForPcoData(Handler handler);

    default void unregisterForPendingSatelliteMessageCount(Handler handler) {
    }

    void unregisterForPhysicalChannelConfiguration(Handler handler);

    void unregisterForRadioCapabilityChanged(Handler handler);

    void unregisterForRadioStateChanged(Handler handler);

    void unregisterForRedirectedNumberInfo(Handler handler);

    void unregisterForResendIncallMute(Handler handler);

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    void unregisterForRilConnected(Handler handler);

    void unregisterForRingbackTone(Handler handler);

    default void unregisterForSatelliteMessagesTransferComplete(Handler handler) {
    }

    default void unregisterForSatelliteModeChanged(Handler handler) {
    }

    default void unregisterForSatellitePointingInfoChanged(Handler handler) {
    }

    default void unregisterForSatelliteProvisionStateChanged(Handler handler) {
    }

    default void unregisterForSatelliteRadioTechnologyChanged(Handler handler) {
    }

    void unregisterForSignalInfo(Handler handler);

    void unregisterForSimPhonebookChanged(Handler handler);

    void unregisterForSimPhonebookRecordsReceived(Handler handler);

    void unregisterForSlicingConfigChanged(Handler handler);

    void unregisterForSrvccStateChanged(Handler handler);

    void unregisterForSubscriptionStatusChanged(Handler handler);

    void unregisterForT53AudioControlInfo(Handler handler);

    void unregisterForT53ClirInfo(Handler handler);

    default void unregisterForTriggerImsDeregistration(Handler handler) {
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    void unregisterForVoiceRadioTechChanged(Handler handler);

    default void unregisterUiccApplicationEnablementChanged(Handler handler) {
    }

    void unsetOnIccRefresh(Handler handler);

    default void updateImsCallStatus(List<ImsCallInfo> list, Message message) {
    }

    default void updateImsRegistrationInfo(int i, int i2, int i3, int i4, Message message) {
    }

    void updateSimPhonebookRecord(SimPhonebookRecord simPhonebookRecord, Message message);

    @UnsupportedAppUsage
    void writeSmsToRuim(int i, byte[] bArr, Message message);

    @UnsupportedAppUsage
    void writeSmsToSim(int i, String str, String str2, Message message);

    @UnsupportedAppUsage
    default void setRadioPower(boolean z, Message message) {
        setRadioPower(z, false, false, message);
    }

    @Deprecated
    default HalVersion getHalVersion() {
        return HalVersion.UNKNOWN;
    }

    default HalVersion getHalVersion(int i) {
        return HalVersion.UNKNOWN;
    }
}
