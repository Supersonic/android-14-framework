package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.os.WorkSource;
import android.telephony.ImsiEncryptionInfo;
import android.telephony.NetworkScanRequest;
import android.telephony.PreciseDataConnectionState;
import android.telephony.ServiceState;
import com.android.internal.telephony.PhoneConstants;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public interface PhoneInternalInterface {
    public static final int BM_10_800M_2 = 15;
    public static final int BM_4_450M = 10;
    public static final int BM_7_700M2 = 12;
    public static final int BM_8_1800M = 13;
    public static final int BM_9_900M = 14;
    public static final int BM_AUS2_BAND = 5;
    public static final int BM_AUS_BAND = 4;
    public static final int BM_AWS = 17;
    public static final int BM_CELL_800 = 6;
    public static final int BM_EURO_BAND = 1;
    public static final int BM_EURO_PAMR = 16;
    public static final int BM_IMT2000 = 11;
    public static final int BM_JPN_BAND = 3;
    public static final int BM_JTACS = 8;
    public static final int BM_KOREA_PCS = 9;
    public static final int BM_NUM_BAND_MODES = 19;
    public static final int BM_PCS = 7;
    public static final int BM_UNSPECIFIED = 0;
    public static final int BM_US_2500M = 18;
    public static final int BM_US_BAND = 2;
    public static final int CDMA_OTA_PROVISION_STATUS_A_KEY_EXCHANGED = 2;
    public static final int CDMA_OTA_PROVISION_STATUS_COMMITTED = 8;
    public static final int CDMA_OTA_PROVISION_STATUS_IMSI_DOWNLOADED = 6;
    public static final int CDMA_OTA_PROVISION_STATUS_MDN_DOWNLOADED = 5;
    public static final int CDMA_OTA_PROVISION_STATUS_NAM_DOWNLOADED = 4;
    public static final int CDMA_OTA_PROVISION_STATUS_OTAPA_ABORTED = 11;
    public static final int CDMA_OTA_PROVISION_STATUS_OTAPA_STARTED = 9;
    public static final int CDMA_OTA_PROVISION_STATUS_OTAPA_STOPPED = 10;
    public static final int CDMA_OTA_PROVISION_STATUS_PRL_DOWNLOADED = 7;
    public static final int CDMA_OTA_PROVISION_STATUS_SPC_RETRIES_EXCEEDED = 1;
    public static final int CDMA_OTA_PROVISION_STATUS_SPL_UNLOCKED = 0;
    public static final int CDMA_OTA_PROVISION_STATUS_SSD_UPDATED = 3;
    public static final int CDMA_RM_AFFILIATED = 1;
    public static final int CDMA_RM_ANY = 2;
    public static final int CDMA_RM_HOME = 0;
    public static final int CDMA_SUBSCRIPTION_NV = 1;
    public static final int CDMA_SUBSCRIPTION_RUIM_SIM = 0;
    public static final int CDMA_SUBSCRIPTION_UNKNOWN = -1;
    public static final boolean DEBUG_PHONE = true;
    public static final String FEATURE_ENABLE_CBS = "enableCBS";
    public static final String FEATURE_ENABLE_DUN = "enableDUN";
    public static final String FEATURE_ENABLE_DUN_ALWAYS = "enableDUNAlways";
    public static final String FEATURE_ENABLE_EMERGENCY = "enableEmergency";
    public static final String FEATURE_ENABLE_FOTA = "enableFOTA";
    public static final String FEATURE_ENABLE_HIPRI = "enableHIPRI";
    public static final String FEATURE_ENABLE_IMS = "enableIMS";
    public static final String FEATURE_ENABLE_MMS = "enableMMS";
    public static final String FEATURE_ENABLE_SUPL = "enableSUPL";
    public static final int PREFERRED_CDMA_SUBSCRIPTION = 0;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public static final int PREFERRED_NT_MODE = RILConstants.PREFERRED_NETWORK_MODE;
    public static final String REASON_APN_CHANGED = "apnChanged";
    public static final String REASON_APN_FAILED = "apnFailed";
    public static final String REASON_APN_SWITCHED = "apnSwitched";
    public static final String REASON_CARRIER_ACTION_DISABLE_METERED_APN = "carrierActionDisableMeteredApn";
    public static final String REASON_CARRIER_CHANGE = "carrierChange";
    public static final String REASON_CDMA_DATA_ATTACHED = "cdmaDataAttached";
    public static final String REASON_CDMA_DATA_DETACHED = "cdmaDataDetached";
    public static final String REASON_CONNECTED = "connected";
    public static final String REASON_CSS_INDICATOR_CHANGED = "cssIndicatorChanged";
    public static final String REASON_DATA_ATTACHED = "dataAttached";
    public static final String REASON_DATA_DEPENDENCY_MET = "dependencyMet";
    public static final String REASON_DATA_DEPENDENCY_UNMET = "dependencyUnmet";
    public static final String REASON_DATA_DETACHED = "dataDetached";
    public static final String REASON_DATA_DISABLED_INTERNAL = "dataDisabledInternal";
    public static final String REASON_DATA_ENABLED = "dataEnabled";
    public static final String REASON_DATA_ENABLED_OVERRIDE = "dataEnabledOverride";
    public static final String REASON_DATA_SPECIFIC_DISABLED = "specificDisabled";
    public static final String REASON_DATA_UNTHROTTLED = "dataUnthrottled";
    public static final String REASON_IWLAN_AVAILABLE = "iwlanAvailable";
    public static final String REASON_IWLAN_DATA_SERVICE_DIED = "iwlanDataServiceDied";
    public static final String REASON_LOST_DATA_CONNECTION = "lostDataConnection";
    public static final String REASON_NW_TYPE_CHANGED = "nwTypeChanged";
    public static final String REASON_PDP_RESET = "pdpReset";
    public static final String REASON_PS_RESTRICT_DISABLED = "psRestrictDisabled";
    public static final String REASON_PS_RESTRICT_ENABLED = "psRestrictEnabled";
    public static final String REASON_RADIO_TURNED_OFF = "radioTurnedOff";
    public static final String REASON_RELEASED_BY_CONNECTIVITY_SERVICE = "releasedByConnectivityService";
    public static final String REASON_RESTORE_DEFAULT_APN = "restoreDefaultApn";
    public static final String REASON_ROAMING_OFF = "roamingOff";
    public static final String REASON_ROAMING_ON = "roamingOn";
    public static final String REASON_SIM_LOADED = "simLoaded";
    public static final String REASON_SIM_NOT_READY = "simNotReady";
    public static final String REASON_SINGLE_PDN_ARBITRATION = "SinglePdnArbitration";
    public static final String REASON_TRAFFIC_DESCRIPTORS_UPDATED = "trafficDescriptorsUpdated";
    public static final String REASON_VCN_REQUESTED_TEARDOWN = "vcnRequestedTeardown";
    public static final String REASON_VOICE_CALL_ENDED = "2GVoiceCallEnded";
    public static final String REASON_VOICE_CALL_STARTED = "2GVoiceCallStarted";
    public static final int TTY_MODE_FULL = 1;
    public static final int TTY_MODE_HCO = 2;
    public static final int TTY_MODE_OFF = 0;
    public static final int TTY_MODE_VCO = 3;

    /* loaded from: classes.dex */
    public enum DataActivityState {
        NONE,
        DATAIN,
        DATAOUT,
        DATAINANDOUT,
        DORMANT
    }

    /* loaded from: classes.dex */
    public enum SuppService {
        UNKNOWN,
        SWITCH,
        SEPARATE,
        TRANSFER,
        CONFERENCE,
        REJECT,
        HANGUP,
        RESUME,
        HOLD
    }

    /* JADX INFO: Access modifiers changed from: private */
    static /* synthetic */ void lambda$dial$0(Phone phone) {
    }

    void acceptCall(int i) throws CallStateException;

    void activateCellBroadcastSms(int i, Message message);

    boolean canConference();

    boolean canTransfer();

    void clearDisconnected();

    void conference() throws CallStateException;

    Connection dial(String str, DialArgs dialArgs, Consumer<Phone> consumer) throws CallStateException;

    void disableLocationUpdates();

    void enableLocationUpdates();

    void explicitCallTransfer() throws CallStateException;

    void getAvailableNetworks(Message message);

    Call getBackgroundCall();

    void getCallBarring(String str, String str2, Message message, int i);

    void getCallForwardingOption(int i, int i2, Message message);

    void getCallForwardingOption(int i, Message message);

    void getCallWaiting(Message message);

    ImsiEncryptionInfo getCarrierInfoForImsiEncryption(int i, boolean z);

    void getCellBroadcastSmsConfig(Message message);

    int getDataActivityState();

    PhoneConstants.DataState getDataConnectionState(String str);

    boolean getDataRoamingEnabled();

    String getDeviceId();

    String getDeviceSvn();

    String getEsn();

    Call getForegroundCall();

    String getGroupIdLevel1();

    String getGroupIdLevel2();

    IccPhoneBookInterfaceManager getIccPhoneBookInterfaceManager();

    String getImei();

    int getImeiType();

    String getLine1AlphaTag();

    String getLine1Number();

    String getMeid();

    String getMobileProvisioningUrl();

    boolean getMute();

    void getOutgoingCallerIdDisplay(Message message);

    List<? extends MmiCode> getPendingMmiCodes();

    PreciseDataConnectionState getPreciseDataConnectionState(String str);

    Call getRingingCall();

    ServiceState getServiceState();

    String getSubscriberId();

    String getVoiceMailAlphaTag();

    String getVoiceMailNumber();

    boolean handleInCallMmiCommands(String str) throws CallStateException;

    boolean handlePinMmi(String str);

    boolean handleUssdRequest(String str, ResultReceiver resultReceiver) throws CallStateException;

    boolean isUserDataEnabled();

    void registerForSuppServiceNotification(Handler handler, int i, Object obj);

    void rejectCall() throws CallStateException;

    void resetCarrierKeysForImsiEncryption();

    void sendDtmf(char c);

    void sendUssdResponse(String str);

    void setCallBarring(String str, boolean z, String str2, Message message, int i);

    void setCallForwardingOption(int i, int i2, String str, int i3, int i4, Message message);

    void setCallForwardingOption(int i, int i2, String str, int i3, Message message);

    void setCallWaiting(boolean z, Message message);

    void setCarrierInfoForImsiEncryption(ImsiEncryptionInfo imsiEncryptionInfo);

    void setCellBroadcastSmsConfig(int[] iArr, Message message);

    void setDataRoamingEnabled(boolean z);

    boolean setLine1Number(String str, String str2, Message message);

    void setMute(boolean z);

    void setOutgoingCallerIdDisplay(int i, Message message);

    default void setRadioPowerForReason(boolean z, boolean z2, boolean z3, boolean z4, int i) {
    }

    default void setRadioPowerOnForTestEmergencyCall(boolean z) {
    }

    void setVoiceMailNumber(String str, String str2, Message message);

    Connection startConference(String[] strArr, DialArgs dialArgs) throws CallStateException;

    void startDtmf(char c);

    void startNetworkScan(NetworkScanRequest networkScanRequest, Message message);

    void stopDtmf();

    void stopNetworkScan(Message message);

    void switchHoldingAndActive() throws CallStateException;

    void unregisterForSuppServiceNotification(Handler handler);

    default void updateServiceLocation() {
    }

    default void updateServiceLocation(WorkSource workSource) {
    }

    boolean updateUsageSetting();

    /* loaded from: classes.dex */
    public static class DialArgs {
        public final int clirMode;
        public final int eccCategory;
        public final Bundle intentExtras;
        public final boolean isEmergency;
        public final UUSInfo uusInfo;
        public final int videoState;

        /* loaded from: classes.dex */
        public static class Builder<T extends Builder<T>> {
            protected Bundle mIntentExtras;
            protected boolean mIsEmergency;
            protected UUSInfo mUusInfo;
            protected int mClirMode = 0;
            protected int mVideoState = 0;
            protected int mEccCategory = 0;

            public static Builder from(DialArgs dialArgs) {
                return new Builder().setUusInfo(dialArgs.uusInfo).setClirMode(dialArgs.clirMode).setIsEmergency(dialArgs.isEmergency).setVideoState(dialArgs.videoState).setIntentExtras(dialArgs.intentExtras).setEccCategory(dialArgs.eccCategory);
            }

            public T setUusInfo(UUSInfo uUSInfo) {
                this.mUusInfo = uUSInfo;
                return this;
            }

            public T setClirMode(int i) {
                this.mClirMode = i;
                return this;
            }

            public T setIsEmergency(boolean z) {
                this.mIsEmergency = z;
                return this;
            }

            public T setVideoState(int i) {
                this.mVideoState = i;
                return this;
            }

            public T setIntentExtras(Bundle bundle) {
                this.mIntentExtras = bundle;
                return this;
            }

            public T setEccCategory(int i) {
                this.mEccCategory = i;
                return this;
            }

            public DialArgs build() {
                return new DialArgs(this);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public DialArgs(Builder builder) {
            this.uusInfo = builder.mUusInfo;
            this.clirMode = builder.mClirMode;
            this.isEmergency = builder.mIsEmergency;
            this.videoState = builder.mVideoState;
            this.intentExtras = builder.mIntentExtras;
            this.eccCategory = builder.mEccCategory;
        }
    }

    default Connection dial(String str, DialArgs dialArgs) throws CallStateException {
        return dial(str, dialArgs, new Consumer() { // from class: com.android.internal.telephony.PhoneInternalInterface$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PhoneInternalInterface.lambda$dial$0((Phone) obj);
            }
        });
    }

    default void setRadioPower(boolean z) {
        setRadioPower(z, false, false, false);
    }

    default void setRadioPower(boolean z, boolean z2, boolean z3, boolean z4) {
        setRadioPowerForReason(z, z2, z3, z4, 0);
    }

    default void setRadioPowerForReason(boolean z, int i) {
        setRadioPowerForReason(z, false, false, false, i);
    }

    default Set<Integer> getRadioPowerOffReasons() {
        return new HashSet();
    }
}
