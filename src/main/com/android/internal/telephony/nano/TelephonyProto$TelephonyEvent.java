package com.android.internal.telephony.nano;

import android.telephony.gsm.SmsMessage;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.RadioNVItems;
import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import com.android.internal.telephony.protobuf.nano.WireFormatNano;
import com.android.internal.telephony.util.DnsPacket;
import java.io.IOException;
/* loaded from: classes.dex */
public final class TelephonyProto$TelephonyEvent extends ExtendableMessageNano<TelephonyProto$TelephonyEvent> {
    private static volatile TelephonyProto$TelephonyEvent[] _emptyArray;
    public TelephonyProto$ActiveSubscriptionInfo activeSubscriptionInfo;
    public CarrierIdMatching carrierIdMatching;
    public CarrierKeyChange carrierKeyChange;
    public TelephonyProto$RilDataCall[] dataCalls;
    public int dataStallAction;
    public DataSwitch dataSwitch;
    public RilDeactivateDataCall deactivateDataCall;
    public int emergencyNumberDatabaseVersion;
    public int enabledModemBitmap;
    public int error;
    public TelephonyProto$ImsCapabilities imsCapabilities;
    public TelephonyProto$ImsConnectionState imsConnectionState;
    public ModemRestart modemRestart;
    public NetworkCapabilitiesInfo networkCapabilities;
    public int networkValidationState;
    public long nitzTimestampMillis;
    public OnDemandDataSwitch onDemandDataSwitch;
    public int phoneId;
    public int radioState;
    public TelephonyProto$TelephonyServiceState serviceState;
    public TelephonyProto$TelephonySettings settings;
    public RilSetupDataCall setupDataCall;
    public RilSetupDataCallResponse setupDataCallResponse;
    public int signalStrength;
    public int[] simState;
    public long timestampMillis;
    public int type;
    public TelephonyProto$EmergencyNumberInfo updatedEmergencyNumber;

    /* loaded from: classes.dex */
    public interface ApnType {
        public static final int APN_TYPE_CBS = 8;
        public static final int APN_TYPE_DEFAULT = 1;
        public static final int APN_TYPE_DUN = 4;
        public static final int APN_TYPE_EMERGENCY = 10;
        public static final int APN_TYPE_FOTA = 6;
        public static final int APN_TYPE_HIPRI = 5;
        public static final int APN_TYPE_IA = 9;
        public static final int APN_TYPE_IMS = 7;
        public static final int APN_TYPE_MMS = 2;
        public static final int APN_TYPE_SUPL = 3;
        public static final int APN_TYPE_UNKNOWN = 0;
    }

    /* loaded from: classes.dex */
    public interface EventState {
        public static final int EVENT_STATE_END = 2;
        public static final int EVENT_STATE_START = 1;
        public static final int EVENT_STATE_UNKNOWN = 0;
    }

    /* loaded from: classes.dex */
    public interface NetworkValidationState {
        public static final int NETWORK_VALIDATION_STATE_AVAILABLE = 1;
        public static final int NETWORK_VALIDATION_STATE_FAILED = 2;
        public static final int NETWORK_VALIDATION_STATE_PASSED = 3;
        public static final int NETWORK_VALIDATION_STATE_UNKNOWN = 0;
    }

    /* loaded from: classes.dex */
    public interface RadioState {
        public static final int RADIO_STATE_OFF = 1;
        public static final int RADIO_STATE_ON = 2;
        public static final int RADIO_STATE_UNAVAILABLE = 3;
        public static final int RADIO_STATE_UNKNOWN = 0;
    }

    /* loaded from: classes.dex */
    public interface Type {
        public static final int ACTIVE_SUBSCRIPTION_INFO_CHANGED = 19;
        public static final int CARRIER_ID_MATCHING = 13;
        public static final int CARRIER_KEY_CHANGED = 14;
        public static final int DATA_CALL_DEACTIVATE = 8;
        public static final int DATA_CALL_DEACTIVATE_RESPONSE = 9;
        public static final int DATA_CALL_LIST_CHANGED = 7;
        public static final int DATA_CALL_SETUP = 5;
        public static final int DATA_CALL_SETUP_RESPONSE = 6;
        public static final int DATA_STALL_ACTION = 10;
        public static final int DATA_SWITCH = 15;
        public static final int EMERGENCY_NUMBER_REPORT = 21;
        public static final int ENABLED_MODEM_CHANGED = 20;
        public static final int IMS_CAPABILITIES_CHANGED = 4;
        public static final int IMS_CONNECTION_STATE_CHANGED = 3;
        public static final int MODEM_RESTART = 11;
        public static final int NETWORK_CAPABILITIES_CHANGED = 22;
        public static final int NETWORK_VALIDATE = 16;
        public static final int NITZ_TIME = 12;
        public static final int ON_DEMAND_DATA_SWITCH = 17;
        public static final int RADIO_STATE_CHANGED = 24;
        public static final int RIL_SERVICE_STATE_CHANGED = 2;
        public static final int SETTINGS_CHANGED = 1;
        public static final int SIGNAL_STRENGTH = 23;
        public static final int SIM_STATE_CHANGED = 18;
        public static final int UNKNOWN = 0;
    }

    /* loaded from: classes.dex */
    public static final class DataSwitch extends ExtendableMessageNano<DataSwitch> {
        private static volatile DataSwitch[] _emptyArray;
        public int reason;
        public int state;

        /* loaded from: classes.dex */
        public interface Reason {
            public static final int DATA_SWITCH_REASON_AUTO = 4;
            public static final int DATA_SWITCH_REASON_CBRS = 3;
            public static final int DATA_SWITCH_REASON_IN_CALL = 2;
            public static final int DATA_SWITCH_REASON_MANUAL = 1;
            public static final int DATA_SWITCH_REASON_UNKNOWN = 0;
        }

        public static DataSwitch[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new DataSwitch[0];
                    }
                }
            }
            return _emptyArray;
        }

        public DataSwitch() {
            clear();
        }

        public DataSwitch clear() {
            this.reason = 0;
            this.state = 0;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.reason;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            int i2 = this.state;
            if (i2 != 0) {
                codedOutputByteBufferNano.writeInt32(2, i2);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            int i = this.reason;
            if (i != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.state;
            return i2 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(2, i2) : computeSerializedSize;
        }

        @Override // com.android.internal.telephony.protobuf.nano.MessageNano
        public DataSwitch mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag == 8) {
                    int readInt32 = codedInputByteBufferNano.readInt32();
                    if (readInt32 == 0 || readInt32 == 1 || readInt32 == 2 || readInt32 == 3 || readInt32 == 4) {
                        this.reason = readInt32;
                    }
                } else if (readTag != 16) {
                    if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                        return this;
                    }
                } else {
                    int readInt322 = codedInputByteBufferNano.readInt32();
                    if (readInt322 == 0 || readInt322 == 1 || readInt322 == 2) {
                        this.state = readInt322;
                    }
                }
            }
        }

        public static DataSwitch parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (DataSwitch) MessageNano.mergeFrom(new DataSwitch(), bArr);
        }

        public static DataSwitch parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new DataSwitch().mergeFrom(codedInputByteBufferNano);
        }
    }

    /* loaded from: classes.dex */
    public static final class OnDemandDataSwitch extends ExtendableMessageNano<OnDemandDataSwitch> {
        private static volatile OnDemandDataSwitch[] _emptyArray;
        public int apn;
        public int state;

        public static OnDemandDataSwitch[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new OnDemandDataSwitch[0];
                    }
                }
            }
            return _emptyArray;
        }

        public OnDemandDataSwitch() {
            clear();
        }

        public OnDemandDataSwitch clear() {
            this.apn = 0;
            this.state = 0;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.apn;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            int i2 = this.state;
            if (i2 != 0) {
                codedOutputByteBufferNano.writeInt32(2, i2);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            int i = this.apn;
            if (i != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.state;
            return i2 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(2, i2) : computeSerializedSize;
        }

        @Override // com.android.internal.telephony.protobuf.nano.MessageNano
        public OnDemandDataSwitch mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag == 8) {
                    int readInt32 = codedInputByteBufferNano.readInt32();
                    switch (readInt32) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                            this.apn = readInt32;
                            continue;
                    }
                } else if (readTag != 16) {
                    if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                        return this;
                    }
                } else {
                    int readInt322 = codedInputByteBufferNano.readInt32();
                    if (readInt322 == 0 || readInt322 == 1 || readInt322 == 2) {
                        this.state = readInt322;
                    }
                }
            }
        }

        public static OnDemandDataSwitch parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (OnDemandDataSwitch) MessageNano.mergeFrom(new OnDemandDataSwitch(), bArr);
        }

        public static OnDemandDataSwitch parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new OnDemandDataSwitch().mergeFrom(codedInputByteBufferNano);
        }
    }

    /* loaded from: classes.dex */
    public static final class RilSetupDataCall extends ExtendableMessageNano<RilSetupDataCall> {
        private static volatile RilSetupDataCall[] _emptyArray;
        public String apn;
        public int dataProfile;
        public int rat;
        public int type;

        /* loaded from: classes.dex */
        public interface RilDataProfile {
            public static final int RIL_DATA_PROFILE_CBS = 5;
            public static final int RIL_DATA_PROFILE_DEFAULT = 1;
            public static final int RIL_DATA_PROFILE_FOTA = 4;
            public static final int RIL_DATA_PROFILE_IMS = 3;
            public static final int RIL_DATA_PROFILE_INVALID = 7;
            public static final int RIL_DATA_PROFILE_OEM_BASE = 6;
            public static final int RIL_DATA_PROFILE_TETHERED = 2;
            public static final int RIL_DATA_UNKNOWN = 0;
        }

        public static RilSetupDataCall[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new RilSetupDataCall[0];
                    }
                }
            }
            return _emptyArray;
        }

        public RilSetupDataCall() {
            clear();
        }

        public RilSetupDataCall clear() {
            this.rat = -1;
            this.dataProfile = 0;
            this.apn = PhoneConfigurationManager.SSSS;
            this.type = 0;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.rat;
            if (i != -1) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            int i2 = this.dataProfile;
            if (i2 != 0) {
                codedOutputByteBufferNano.writeInt32(2, i2);
            }
            if (!this.apn.equals(PhoneConfigurationManager.SSSS)) {
                codedOutputByteBufferNano.writeString(3, this.apn);
            }
            int i3 = this.type;
            if (i3 != 0) {
                codedOutputByteBufferNano.writeInt32(4, i3);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            int i = this.rat;
            if (i != -1) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.dataProfile;
            if (i2 != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
            }
            if (!this.apn.equals(PhoneConfigurationManager.SSSS)) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(3, this.apn);
            }
            int i3 = this.type;
            return i3 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(4, i3) : computeSerializedSize;
        }

        @Override // com.android.internal.telephony.protobuf.nano.MessageNano
        public RilSetupDataCall mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag == 8) {
                    int readInt32 = codedInputByteBufferNano.readInt32();
                    switch (readInt32) {
                        case -1:
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                        case 14:
                        case 15:
                        case 16:
                        case 17:
                        case 18:
                        case 19:
                        case 20:
                            this.rat = readInt32;
                            continue;
                    }
                } else if (readTag == 16) {
                    int readInt322 = codedInputByteBufferNano.readInt32();
                    switch (readInt322) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                            this.dataProfile = readInt322;
                            continue;
                    }
                } else if (readTag == 26) {
                    this.apn = codedInputByteBufferNano.readString();
                } else if (readTag != 32) {
                    if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                        return this;
                    }
                } else {
                    int readInt323 = codedInputByteBufferNano.readInt32();
                    switch (readInt323) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                            this.type = readInt323;
                            continue;
                    }
                }
            }
        }

        public static RilSetupDataCall parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (RilSetupDataCall) MessageNano.mergeFrom(new RilSetupDataCall(), bArr);
        }

        public static RilSetupDataCall parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new RilSetupDataCall().mergeFrom(codedInputByteBufferNano);
        }
    }

    /* loaded from: classes.dex */
    public static final class RilSetupDataCallResponse extends ExtendableMessageNano<RilSetupDataCallResponse> {
        private static volatile RilSetupDataCallResponse[] _emptyArray;
        public TelephonyProto$RilDataCall call;
        public int status;
        public int suggestedRetryTimeMillis;

        /* loaded from: classes.dex */
        public interface RilDataCallFailCause {
            public static final int PDP_FAIL_ACCESS_ATTEMPT_ALREADY_IN_PROGRESS = 2219;
            public static final int PDP_FAIL_ACCESS_BLOCK = 2087;
            public static final int PDP_FAIL_ACCESS_BLOCK_ALL = 2088;
            public static final int PDP_FAIL_ACCESS_CLASS_DSAC_REJECTION = 2108;
            public static final int PDP_FAIL_ACCESS_CONTROL_LIST_CHECK_FAILURE = 2128;
            public static final int PDP_FAIL_ACTIVATION_REJECTED_BCM_VIOLATION = 48;
            public static final int PDP_FAIL_ACTIVATION_REJECT_GGSN = 30;
            public static final int PDP_FAIL_ACTIVATION_REJECT_UNSPECIFIED = 31;
            public static final int PDP_FAIL_APN_DISABLED = 2045;
            public static final int PDP_FAIL_APN_DISALLOWED_ON_ROAMING = 2059;
            public static final int PDP_FAIL_APN_MISMATCH = 2054;
            public static final int PDP_FAIL_APN_PARAMETERS_CHANGED = 2060;
            public static final int PDP_FAIL_APN_PENDING_HANDOVER = 2041;
            public static final int PDP_FAIL_APN_TYPE_CONFLICT = 112;
            public static final int PDP_FAIL_AUTH_FAILURE_ON_EMERGENCY_CALL = 122;
            public static final int PDP_FAIL_BEARER_HANDLING_NOT_SUPPORTED = 60;
            public static final int PDP_FAIL_CALL_DISALLOWED_IN_ROAMING = 2068;
            public static final int PDP_FAIL_CALL_PREEMPT_BY_EMERGENCY_APN = 127;
            public static final int PDP_FAIL_CANNOT_ENCODE_OTA_MESSAGE = 2159;
            public static final int PDP_FAIL_CDMA_ALERT_STOP = 2077;
            public static final int PDP_FAIL_CDMA_INCOMING_CALL = 2076;
            public static final int PDP_FAIL_CDMA_INTERCEPT = 2073;
            public static final int PDP_FAIL_CDMA_LOCK = 2072;
            public static final int PDP_FAIL_CDMA_RELEASE_DUE_TO_SO_REJECTION = 2075;
            public static final int PDP_FAIL_CDMA_REORDER = 2074;
            public static final int PDP_FAIL_CDMA_RETRY_ORDER = 2086;
            public static final int PDP_FAIL_CHANNEL_ACQUISITION_FAILURE = 2078;
            public static final int PDP_FAIL_CLOSE_IN_PROGRESS = 2030;
            public static final int PDP_FAIL_COLLISION_WITH_NETWORK_INITIATED_REQUEST = 56;
            public static final int PDP_FAIL_COMPANION_IFACE_IN_USE = 118;
            public static final int PDP_FAIL_CONCURRENT_SERVICES_INCOMPATIBLE = 2083;
            public static final int PDP_FAIL_CONCURRENT_SERVICES_NOT_ALLOWED = 2091;
            public static final int PDP_FAIL_CONCURRENT_SERVICE_NOT_SUPPORTED_BY_BASE_STATION = 2080;
            public static final int PDP_FAIL_CONDITIONAL_IE_ERROR = 100;
            public static final int PDP_FAIL_CONGESTION = 2106;
            public static final int PDP_FAIL_CONNECTION_RELEASED = 2113;
            public static final int PDP_FAIL_CS_DOMAIN_NOT_AVAILABLE = 2181;
            public static final int PDP_FAIL_CS_FALLBACK_CALL_ESTABLISHMENT_NOT_ALLOWED = 2188;
            public static final int PDP_FAIL_DATA_PLAN_EXPIRED = 2198;
            public static final int PDP_FAIL_DATA_REGISTRATION_FAIL = -2;
            public static final int PDP_FAIL_DATA_ROAMING_SETTINGS_DISABLED = 2064;
            public static final int PDP_FAIL_DATA_SETTINGS_DISABLED = 2063;
            public static final int PDP_FAIL_DBM_OR_SMS_IN_PROGRESS = 2211;
            public static final int PDP_FAIL_DDS_SWITCHED = 2065;
            public static final int PDP_FAIL_DDS_SWITCH_IN_PROGRESS = 2067;
            public static final int PDP_FAIL_DRB_RELEASED_BY_RRC = 2112;
            public static final int PDP_FAIL_DS_EXPLICIT_DEACTIVATION = 2125;
            public static final int PDP_FAIL_DUAL_SWITCH = 2227;
            public static final int PDP_FAIL_DUN_CALL_DISALLOWED = 2056;
            public static final int PDP_FAIL_DUPLICATE_BEARER_ID = 2118;
            public static final int PDP_FAIL_EHRPD_TO_HRPD_FALLBACK = 2049;
            public static final int PDP_FAIL_EMBMS_NOT_ENABLED = 2193;
            public static final int PDP_FAIL_EMBMS_REGULAR_DEACTIVATION = 2195;
            public static final int PDP_FAIL_EMERGENCY_IFACE_ONLY = 116;
            public static final int PDP_FAIL_EMERGENCY_MODE = 2221;
            public static final int PDP_FAIL_EMM_ACCESS_BARRED = 115;
            public static final int PDP_FAIL_EMM_ACCESS_BARRED_INFINITE_RETRY = 121;
            public static final int PDP_FAIL_EMM_ATTACH_FAILED = 2115;
            public static final int PDP_FAIL_EMM_ATTACH_STARTED = 2116;
            public static final int PDP_FAIL_EMM_DETACHED = 2114;
            public static final int PDP_FAIL_EMM_T3417_EXPIRED = 2130;
            public static final int PDP_FAIL_EMM_T3417_EXT_EXPIRED = 2131;
            public static final int PDP_FAIL_EPS_SERVICES_AND_NON_EPS_SERVICES_NOT_ALLOWED = 2178;
            public static final int PDP_FAIL_EPS_SERVICES_NOT_ALLOWED_IN_PLMN = 2179;
            public static final int PDP_FAIL_ERROR_UNSPECIFIED = 65535;
            public static final int PDP_FAIL_ESM_BAD_OTA_MESSAGE = 2122;
            public static final int PDP_FAIL_ESM_BEARER_DEACTIVATED_TO_SYNC_WITH_NETWORK = 2120;
            public static final int PDP_FAIL_ESM_COLLISION_SCENARIOS = 2119;
            public static final int PDP_FAIL_ESM_CONTEXT_TRANSFERRED_DUE_TO_IRAT = 2124;
            public static final int PDP_FAIL_ESM_DOWNLOAD_SERVER_REJECTED_THE_CALL = 2123;
            public static final int PDP_FAIL_ESM_FAILURE = 2182;
            public static final int PDP_FAIL_ESM_INFO_NOT_RECEIVED = 53;
            public static final int PDP_FAIL_ESM_LOCAL_CAUSE_NONE = 2126;
            public static final int PDP_FAIL_ESM_NW_ACTIVATED_DED_BEARER_WITH_ID_OF_DEF_BEARER = 2121;
            public static final int PDP_FAIL_ESM_PROCEDURE_TIME_OUT = 2155;
            public static final int PDP_FAIL_ESM_UNKNOWN_EPS_BEARER_CONTEXT = 2111;

            /* renamed from: PDP_FAIL_EVDO_CONNECTION_DENY_BY_BILLING_OR_AUTHENTICATION_FAILURE */
            public static final int f16xb1f092a = 2201;
            public static final int PDP_FAIL_EVDO_CONNECTION_DENY_BY_GENERAL_OR_NETWORK_BUSY = 2200;
            public static final int PDP_FAIL_EVDO_HDR_CHANGED = 2202;
            public static final int PDP_FAIL_EVDO_HDR_CONNECTION_SETUP_TIMEOUT = 2206;
            public static final int PDP_FAIL_EVDO_HDR_EXITED = 2203;
            public static final int PDP_FAIL_EVDO_HDR_NO_SESSION = 2204;
            public static final int PDP_FAIL_EVDO_USING_GPS_FIX_INSTEAD_OF_HDR_CALL = 2205;
            public static final int PDP_FAIL_FADE = 2217;
            public static final int PDP_FAIL_FAILED_TO_ACQUIRE_COLOCATED_HDR = 2207;
            public static final int PDP_FAIL_FEATURE_NOT_SUPP = 40;
            public static final int PDP_FAIL_FILTER_SEMANTIC_ERROR = 44;
            public static final int PDP_FAIL_FILTER_SYTAX_ERROR = 45;
            public static final int PDP_FAIL_FORBIDDEN_APN_NAME = 2066;
            public static final int PDP_FAIL_GPRS_SERVICES_AND_NON_GPRS_SERVICES_NOT_ALLOWED = 2097;
            public static final int PDP_FAIL_GPRS_SERVICES_NOT_ALLOWED = 2098;
            public static final int PDP_FAIL_GPRS_SERVICES_NOT_ALLOWED_IN_THIS_PLMN = 2103;
            public static final int PDP_FAIL_HANDOFF_PREFERENCE_CHANGED = 2251;
            public static final int PDP_FAIL_HDR_ACCESS_FAILURE = 2213;
            public static final int PDP_FAIL_HDR_FADE = 2212;
            public static final int PDP_FAIL_HDR_NO_LOCK_GRANTED = 2210;
            public static final int PDP_FAIL_IFACE_AND_POL_FAMILY_MISMATCH = 120;
            public static final int PDP_FAIL_IFACE_MISMATCH = 117;
            public static final int PDP_FAIL_ILLEGAL_ME = 2096;
            public static final int PDP_FAIL_ILLEGAL_MS = 2095;
            public static final int PDP_FAIL_IMEI_NOT_ACCEPTED = 2177;
            public static final int PDP_FAIL_IMPLICITLY_DETACHED = 2100;
            public static final int PDP_FAIL_IMSI_UNKNOWN_IN_HOME_SUBSCRIBER_SERVER = 2176;
            public static final int PDP_FAIL_INCOMING_CALL_REJECTED = 2092;
            public static final int PDP_FAIL_INSUFFICIENT_RESOURCES = 26;
            public static final int PDP_FAIL_INTERFACE_IN_USE = 2058;
            public static final int PDP_FAIL_INTERNAL_CALL_PREEMPT_BY_HIGH_PRIO_APN = 114;
            public static final int PDP_FAIL_INTERNAL_EPC_NONEPC_TRANSITION = 2057;
            public static final int PDP_FAIL_INVALID_CONNECTION_ID = 2156;
            public static final int PDP_FAIL_INVALID_DNS_ADDR = 123;
            public static final int PDP_FAIL_INVALID_EMM_STATE = 2190;
            public static final int PDP_FAIL_INVALID_MANDATORY_INFO = 96;
            public static final int PDP_FAIL_INVALID_MODE = 2223;
            public static final int PDP_FAIL_INVALID_PCSCF_ADDR = 113;
            public static final int PDP_FAIL_INVALID_PCSCF_OR_DNS_ADDRESS = 124;
            public static final int PDP_FAIL_INVALID_PRIMARY_NSAPI = 2158;
            public static final int PDP_FAIL_INVALID_SIM_STATE = 2224;
            public static final int PDP_FAIL_INVALID_TRANSACTION_ID = 81;
            public static final int PDP_FAIL_IPV6_ADDRESS_TRANSFER_FAILED = 2047;
            public static final int PDP_FAIL_IPV6_PREFIX_UNAVAILABLE = 2250;
            public static final int PDP_FAIL_IP_ADDRESS_MISMATCH = 119;
            public static final int PDP_FAIL_IP_VERSION_MISMATCH = 2055;
            public static final int PDP_FAIL_IRAT_HANDOVER_FAILED = 2194;
            public static final int PDP_FAIL_IS707B_MAX_ACCESS_PROBES = 2089;
            public static final int PDP_FAIL_LIMITED_TO_IPV4 = 2234;
            public static final int PDP_FAIL_LIMITED_TO_IPV6 = 2235;
            public static final int PDP_FAIL_LLC_SNDCP = 25;
            public static final int PDP_FAIL_LOCAL_END = 2215;
            public static final int PDP_FAIL_LOCATION_AREA_NOT_ALLOWED = 2102;
            public static final int PDP_FAIL_LOWER_LAYER_REGISTRATION_FAILURE = 2197;
            public static final int PDP_FAIL_LOW_POWER_MODE_OR_POWERING_DOWN = 2044;
            public static final int PDP_FAIL_LTE_NAS_SERVICE_REQUEST_FAILED = 2117;
            public static final int PDP_FAIL_LTE_THROTTLING_NOT_REQUIRED = 2127;
            public static final int PDP_FAIL_MAC_FAILURE = 2183;
            public static final int PDP_FAIL_MAXIMIUM_NSAPIS_EXCEEDED = 2157;
            public static final int PDP_FAIL_MAXINUM_SIZE_OF_L2_MESSAGE_EXCEEDED = 2166;
            public static final int PDP_FAIL_MAX_ACCESS_PROBE = 2079;
            public static final int PDP_FAIL_MAX_ACTIVE_PDP_CONTEXT_REACHED = 65;
            public static final int PDP_FAIL_MAX_IPV4_CONNECTIONS = 2052;
            public static final int PDP_FAIL_MAX_IPV6_CONNECTIONS = 2053;
            public static final int PDP_FAIL_MAX_PPP_INACTIVITY_TIMER_EXPIRED = 2046;
            public static final int PDP_FAIL_MESSAGE_INCORRECT_SEMANTIC = 95;
            public static final int PDP_FAIL_MESSAGE_TYPE_UNSUPPORTED = 97;
            public static final int PDP_FAIL_MIP_CONFIG_FAILURE = 2050;
            public static final int PDP_FAIL_MIP_FA_ADMIN_PROHIBITED = 2001;
            public static final int PDP_FAIL_MIP_FA_DELIVERY_STYLE_NOT_SUPPORTED = 2012;
            public static final int PDP_FAIL_MIP_FA_ENCAPSULATION_UNAVAILABLE = 2008;
            public static final int PDP_FAIL_MIP_FA_HOME_AGENT_AUTHENTICATION_FAILURE = 2004;
            public static final int PDP_FAIL_MIP_FA_INSUFFICIENT_RESOURCES = 2002;
            public static final int PDP_FAIL_MIP_FA_MALFORMED_REPLY = 2007;
            public static final int PDP_FAIL_MIP_FA_MALFORMED_REQUEST = 2006;
            public static final int PDP_FAIL_MIP_FA_MISSING_CHALLENGE = 2017;
            public static final int PDP_FAIL_MIP_FA_MISSING_HOME_ADDRESS = 2015;
            public static final int PDP_FAIL_MIP_FA_MISSING_HOME_AGENT = 2014;
            public static final int PDP_FAIL_MIP_FA_MISSING_NAI = 2013;
            public static final int PDP_FAIL_MIP_FA_MOBILE_NODE_AUTHENTICATION_FAILURE = 2003;
            public static final int PDP_FAIL_MIP_FA_REASON_UNSPECIFIED = 2000;
            public static final int PDP_FAIL_MIP_FA_REQUESTED_LIFETIME_TOO_LONG = 2005;
            public static final int PDP_FAIL_MIP_FA_REVERSE_TUNNEL_IS_MANDATORY = 2011;
            public static final int PDP_FAIL_MIP_FA_REVERSE_TUNNEL_UNAVAILABLE = 2010;
            public static final int PDP_FAIL_MIP_FA_STALE_CHALLENGE = 2018;
            public static final int PDP_FAIL_MIP_FA_UNKNOWN_CHALLENGE = 2016;
            public static final int PDP_FAIL_MIP_FA_VJ_HEADER_COMPRESSION_UNAVAILABLE = 2009;
            public static final int PDP_FAIL_MIP_HA_ADMIN_PROHIBITED = 2020;
            public static final int PDP_FAIL_MIP_HA_ENCAPSULATION_UNAVAILABLE = 2029;
            public static final int PDP_FAIL_MIP_HA_FOREIGN_AGENT_AUTHENTICATION_FAILURE = 2023;
            public static final int PDP_FAIL_MIP_HA_INSUFFICIENT_RESOURCES = 2021;
            public static final int PDP_FAIL_MIP_HA_MALFORMED_REQUEST = 2025;
            public static final int PDP_FAIL_MIP_HA_MOBILE_NODE_AUTHENTICATION_FAILURE = 2022;
            public static final int PDP_FAIL_MIP_HA_REASON_UNSPECIFIED = 2019;
            public static final int PDP_FAIL_MIP_HA_REGISTRATION_ID_MISMATCH = 2024;
            public static final int PDP_FAIL_MIP_HA_REVERSE_TUNNEL_IS_MANDATORY = 2028;
            public static final int PDP_FAIL_MIP_HA_REVERSE_TUNNEL_UNAVAILABLE = 2027;
            public static final int PDP_FAIL_MIP_HA_UNKNOWN_HOME_AGENT_ADDRESS = 2026;
            public static final int PDP_FAIL_MISSING_UKNOWN_APN = 27;
            public static final int PDP_FAIL_MODEM_APP_PREEMPTED = 2032;
            public static final int PDP_FAIL_MODEM_RESTART = 2037;
            public static final int PDP_FAIL_MSC_TEMPORARILY_NOT_REACHABLE = 2180;
            public static final int PDP_FAIL_MSG_AND_PROTOCOL_STATE_UNCOMPATIBLE = 101;
            public static final int PDP_FAIL_MSG_TYPE_NONCOMPATIBLE_STATE = 98;
            public static final int PDP_FAIL_MS_IDENTITY_CANNOT_BE_DERIVED_BY_THE_NETWORK = 2099;
            public static final int PDP_FAIL_MULTIPLE_PDP_CALL_NOT_ALLOWED = 2192;
            public static final int PDP_FAIL_MULTI_CONN_TO_SAME_PDN_NOT_ALLOWED = 55;
            public static final int PDP_FAIL_NAS_LAYER_FAILURE = 2191;
            public static final int PDP_FAIL_NAS_REQUEST_REJECTED_BY_NETWORK = 2167;
            public static final int PDP_FAIL_NAS_SIGNALLING = 14;
            public static final int PDP_FAIL_NETWORK_FAILURE = 38;
            public static final int PDP_FAIL_NETWORK_INITIATED_DETACH_NO_AUTO_REATTACH = 2154;
            public static final int PDP_FAIL_NETWORK_INITIATED_DETACH_WITH_AUTO_REATTACH = 2153;
            public static final int PDP_FAIL_NETWORK_INITIATED_TERMINATION = 2031;
            public static final int PDP_FAIL_NONE = 1;
            public static final int PDP_FAIL_NON_IP_NOT_SUPPORTED = 2069;
            public static final int PDP_FAIL_NORMAL_RELEASE = 2218;
            public static final int PDP_FAIL_NO_CDMA_SERVICE = 2084;
            public static final int PDP_FAIL_NO_COLLOCATED_HDR = 2225;
            public static final int PDP_FAIL_NO_EPS_BEARER_CONTEXT_ACTIVATED = 2189;
            public static final int PDP_FAIL_NO_GPRS_CONTEXT = 2094;
            public static final int PDP_FAIL_NO_HYBRID_HDR_SERVICE = 2209;
            public static final int PDP_FAIL_NO_PDP_CONTEXT_ACTIVATED = 2107;
            public static final int PDP_FAIL_NO_RESPONSE_FROM_BASE_STATION = 2081;
            public static final int PDP_FAIL_NO_SERVICE = 2216;
            public static final int PDP_FAIL_NO_SERVICE_ON_GATEWAY = 2093;
            public static final int PDP_FAIL_NSAPI_IN_USE = 35;
            public static final int PDP_FAIL_NULL_APN_DISALLOWED = 2061;
            public static final int PDP_FAIL_ONLY_IPV4V6_ALLOWED = 57;
            public static final int PDP_FAIL_ONLY_IPV4_ALLOWED = 50;
            public static final int PDP_FAIL_ONLY_IPV6_ALLOWED = 51;
            public static final int PDP_FAIL_ONLY_NON_IP_ALLOWED = 58;
            public static final int PDP_FAIL_ONLY_SINGLE_BEARER_ALLOWED = 52;
            public static final int PDP_FAIL_OPERATOR_BARRED = 8;
            public static final int PDP_FAIL_OTASP_COMMIT_IN_PROGRESS = 2208;
            public static final int PDP_FAIL_PDN_CONN_DOES_NOT_EXIST = 54;
            public static final int PDP_FAIL_PDN_INACTIVITY_TIMER_EXPIRED = 2051;
            public static final int PDP_FAIL_PDN_IPV4_CALL_DISALLOWED = 2033;
            public static final int PDP_FAIL_PDN_IPV4_CALL_THROTTLED = 2034;
            public static final int PDP_FAIL_PDN_IPV6_CALL_DISALLOWED = 2035;
            public static final int PDP_FAIL_PDN_IPV6_CALL_THROTTLED = 2036;
            public static final int PDP_FAIL_PDN_NON_IP_CALL_DISALLOWED = 2071;
            public static final int PDP_FAIL_PDN_NON_IP_CALL_THROTTLED = 2070;
            public static final int PDP_FAIL_PDP_ACTIVATE_MAX_RETRY_FAILED = 2109;
            public static final int PDP_FAIL_PDP_DUPLICATE = 2104;
            public static final int PDP_FAIL_PDP_ESTABLISH_TIMEOUT_EXPIRED = 2161;
            public static final int PDP_FAIL_PDP_INACTIVE_TIMEOUT_EXPIRED = 2163;
            public static final int PDP_FAIL_PDP_LOWERLAYER_ERROR = 2164;
            public static final int PDP_FAIL_PDP_MODIFY_COLLISION = 2165;
            public static final int PDP_FAIL_PDP_MODIFY_TIMEOUT_EXPIRED = 2162;
            public static final int PDP_FAIL_PDP_PPP_NOT_SUPPORTED = 2038;
            public static final int PDP_FAIL_PDP_WITHOUT_ACTIVE_TFT = 46;
            public static final int PDP_FAIL_PHONE_IN_USE = 2222;
            public static final int PDP_FAIL_PHYSICAL_LINK_CLOSE_IN_PROGRESS = 2040;
            public static final int PDP_FAIL_PLMN_NOT_ALLOWED = 2101;
            public static final int PDP_FAIL_PPP_AUTH_FAILURE = 2229;
            public static final int PDP_FAIL_PPP_CHAP_FAILURE = 2232;
            public static final int PDP_FAIL_PPP_CLOSE_IN_PROGRESS = 2233;
            public static final int PDP_FAIL_PPP_OPTION_MISMATCH = 2230;
            public static final int PDP_FAIL_PPP_PAP_FAILURE = 2231;
            public static final int PDP_FAIL_PPP_TIMEOUT = 2228;
            public static final int PDP_FAIL_PREF_RADIO_TECH_CHANGED = -4;
            public static final int PDP_FAIL_PROFILE_BEARER_INCOMPATIBLE = 2042;
            public static final int PDP_FAIL_PROTOCOL_ERRORS = 111;
            public static final int PDP_FAIL_QOS_NOT_ACCEPTED = 37;
            public static final int PDP_FAIL_RADIO_ACCESS_BEARER_FAILURE = 2110;
            public static final int PDP_FAIL_RADIO_ACCESS_BEARER_SETUP_FAILURE = 2160;
            public static final int PDP_FAIL_RADIO_POWER_OFF = -5;
            public static final int PDP_FAIL_REDIRECTION_OR_HANDOFF_IN_PROGRESS = 2220;
            public static final int PDP_FAIL_REGULAR_DEACTIVATION = 36;
            public static final int PDP_FAIL_REJECTED_BY_BASE_STATION = 2082;
            public static final int PDP_FAIL_RRC_CONNECTION_ABORTED_AFTER_HANDOVER = 2173;
            public static final int PDP_FAIL_RRC_CONNECTION_ABORTED_AFTER_IRAT_CELL_CHANGE = 2174;
            public static final int PDP_FAIL_RRC_CONNECTION_ABORTED_DUE_TO_IRAT_CHANGE = 2171;
            public static final int PDP_FAIL_RRC_CONNECTION_ABORTED_DURING_IRAT_CELL_CHANGE = 2175;
            public static final int PDP_FAIL_RRC_CONNECTION_ABORT_REQUEST = 2151;
            public static final int PDP_FAIL_RRC_CONNECTION_ACCESS_BARRED = 2139;
            public static final int PDP_FAIL_RRC_CONNECTION_ACCESS_STRATUM_FAILURE = 2137;
            public static final int PDP_FAIL_RRC_CONNECTION_ANOTHER_PROCEDURE_IN_PROGRESS = 2138;
            public static final int PDP_FAIL_RRC_CONNECTION_CELL_NOT_CAMPED = 2144;
            public static final int PDP_FAIL_RRC_CONNECTION_CELL_RESELECTION = 2140;
            public static final int PDP_FAIL_RRC_CONNECTION_CONFIG_FAILURE = 2141;
            public static final int PDP_FAIL_RRC_CONNECTION_INVALID_REQUEST = 2168;
            public static final int PDP_FAIL_RRC_CONNECTION_LINK_FAILURE = 2143;
            public static final int PDP_FAIL_RRC_CONNECTION_NORMAL_RELEASE = 2147;
            public static final int PDP_FAIL_RRC_CONNECTION_OUT_OF_SERVICE_DURING_CELL_REGISTER = 2150;
            public static final int PDP_FAIL_RRC_CONNECTION_RADIO_LINK_FAILURE = 2148;
            public static final int PDP_FAIL_RRC_CONNECTION_REESTABLISHMENT_FAILURE = 2149;
            public static final int PDP_FAIL_RRC_CONNECTION_REJECT_BY_NETWORK = 2146;
            public static final int PDP_FAIL_RRC_CONNECTION_RELEASED_SECURITY_NOT_ACTIVE = 2172;
            public static final int PDP_FAIL_RRC_CONNECTION_RF_UNAVAILABLE = 2170;
            public static final int PDP_FAIL_RRC_CONNECTION_SYSTEM_INFORMATION_BLOCK_READ_ERROR = 2152;
            public static final int PDP_FAIL_RRC_CONNECTION_SYSTEM_INTERVAL_FAILURE = 2145;
            public static final int PDP_FAIL_RRC_CONNECTION_TIMER_EXPIRED = 2142;
            public static final int PDP_FAIL_RRC_CONNECTION_TRACKING_AREA_ID_CHANGED = 2169;
            public static final int PDP_FAIL_RRC_UPLINK_CONNECTION_RELEASE = 2134;
            public static final int PDP_FAIL_RRC_UPLINK_DATA_TRANSMISSION_FAILURE = 2132;
            public static final int PDP_FAIL_RRC_UPLINK_DELIVERY_FAILED_DUE_TO_HANDOVER = 2133;
            public static final int PDP_FAIL_RRC_UPLINK_ERROR_REQUEST_FROM_NAS = 2136;
            public static final int PDP_FAIL_RRC_UPLINK_RADIO_LINK_FAILURE = 2135;
            public static final int PDP_FAIL_RUIM_NOT_PRESENT = 2085;
            public static final int PDP_FAIL_SECURITY_MODE_REJECTED = 2186;
            public static final int PDP_FAIL_SERVICE_NOT_ALLOWED_ON_PLMN = 2129;
            public static final int PDP_FAIL_SERVICE_OPTION_NOT_SUBSCRIBED = 33;
            public static final int PDP_FAIL_SERVICE_OPTION_NOT_SUPPORTED = 32;
            public static final int PDP_FAIL_SERVICE_OPTION_OUT_OF_ORDER = 34;
            public static final int PDP_FAIL_SIGNAL_LOST = -3;
            public static final int PDP_FAIL_SIM_CARD_CHANGED = 2043;
            public static final int PDP_FAIL_SYNCHRONIZATION_FAILURE = 2184;
            public static final int PDP_FAIL_TEST_LOOPBACK_REGULAR_DEACTIVATION = 2196;
            public static final int PDP_FAIL_TETHERED_CALL_ACTIVE = -6;
            public static final int PDP_FAIL_TFT_SEMANTIC_ERROR = 41;
            public static final int PDP_FAIL_TFT_SYTAX_ERROR = 42;
            public static final int PDP_FAIL_THERMAL_EMERGENCY = 2090;
            public static final int PDP_FAIL_THERMAL_MITIGATION = 2062;
            public static final int PDP_FAIL_TRAT_SWAP_FAILED = 2048;
            public static final int PDP_FAIL_UE_INITIATED_DETACH_OR_DISCONNECT = 128;
            public static final int PDP_FAIL_UE_IS_ENTERING_POWERSAVE_MODE = 2226;
            public static final int PDP_FAIL_UE_RAT_CHANGE = 2105;
            public static final int PDP_FAIL_UE_SECURITY_CAPABILITIES_MISMATCH = 2185;
            public static final int PDP_FAIL_UMTS_HANDOVER_TO_IWLAN = 2199;
            public static final int PDP_FAIL_UMTS_REACTIVATION_REQ = 39;
            public static final int PDP_FAIL_UNACCEPTABLE_NON_EPS_AUTHENTICATION = 2187;
            public static final int PDP_FAIL_UNKNOWN = 0;
            public static final int PDP_FAIL_UNKNOWN_INFO_ELEMENT = 99;
            public static final int PDP_FAIL_UNKNOWN_PDP_ADDRESS_TYPE = 28;
            public static final int PDP_FAIL_UNKNOWN_PDP_CONTEXT = 43;
            public static final int PDP_FAIL_UNPREFERRED_RAT = 2039;
            public static final int PDP_FAIL_UNSUPPORTED_1X_PREV = 2214;
            public static final int PDP_FAIL_UNSUPPORTED_APN_IN_CURRENT_PLMN = 66;
            public static final int PDP_FAIL_UNSUPPORTED_QCI_VALUE = 59;
            public static final int PDP_FAIL_USER_AUTHENTICATION = 29;
            public static final int PDP_FAIL_VOICE_REGISTRATION_FAIL = -1;
            public static final int PDP_FAIL_VSNCP_ADMINISTRATIVELY_PROHIBITED = 2245;
            public static final int PDP_FAIL_VSNCP_APN_UNATHORIZED = 2238;
            public static final int PDP_FAIL_VSNCP_GEN_ERROR = 2237;
            public static final int PDP_FAIL_VSNCP_INSUFFICIENT_PARAMETERS = 2243;
            public static final int PDP_FAIL_VSNCP_NO_PDN_GATEWAY_ADDRESS = 2240;
            public static final int PDP_FAIL_VSNCP_PDN_EXISTS_FOR_THIS_APN = 2248;
            public static final int PDP_FAIL_VSNCP_PDN_GATEWAY_REJECT = 2242;
            public static final int PDP_FAIL_VSNCP_PDN_GATEWAY_UNREACHABLE = 2241;
            public static final int PDP_FAIL_VSNCP_PDN_ID_IN_USE = 2246;
            public static final int PDP_FAIL_VSNCP_PDN_LIMIT_EXCEEDED = 2239;
            public static final int PDP_FAIL_VSNCP_RECONNECT_NOT_ALLOWED = 2249;
            public static final int PDP_FAIL_VSNCP_RESOURCE_UNAVAILABLE = 2244;
            public static final int PDP_FAIL_VSNCP_SUBSCRIBER_LIMITATION = 2247;
            public static final int PDP_FAIL_VSNCP_TIMEOUT = 2236;
        }

        public static RilSetupDataCallResponse[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new RilSetupDataCallResponse[0];
                    }
                }
            }
            return _emptyArray;
        }

        public RilSetupDataCallResponse() {
            clear();
        }

        public RilSetupDataCallResponse clear() {
            this.status = 0;
            this.suggestedRetryTimeMillis = 0;
            this.call = null;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.status;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            int i2 = this.suggestedRetryTimeMillis;
            if (i2 != 0) {
                codedOutputByteBufferNano.writeInt32(2, i2);
            }
            TelephonyProto$RilDataCall telephonyProto$RilDataCall = this.call;
            if (telephonyProto$RilDataCall != null) {
                codedOutputByteBufferNano.writeMessage(3, telephonyProto$RilDataCall);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            int i = this.status;
            if (i != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.suggestedRetryTimeMillis;
            if (i2 != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
            }
            TelephonyProto$RilDataCall telephonyProto$RilDataCall = this.call;
            return telephonyProto$RilDataCall != null ? computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(3, telephonyProto$RilDataCall) : computeSerializedSize;
        }

        @Override // com.android.internal.telephony.protobuf.nano.MessageNano
        public RilSetupDataCallResponse mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag == 8) {
                    int readInt32 = codedInputByteBufferNano.readInt32();
                    if (readInt32 != 8 && readInt32 != 14 && readInt32 != 48 && readInt32 != 81 && readInt32 != 65535 && readInt32 != 65 && readInt32 != 66 && readInt32 != 127 && readInt32 != 128) {
                        switch (readInt32) {
                            case RilDataCallFailCause.PDP_FAIL_TETHERED_CALL_ACTIVE /* -6 */:
                            case RilDataCallFailCause.PDP_FAIL_RADIO_POWER_OFF /* -5 */:
                            case RilDataCallFailCause.PDP_FAIL_PREF_RADIO_TECH_CHANGED /* -4 */:
                            case RilDataCallFailCause.PDP_FAIL_SIGNAL_LOST /* -3 */:
                            case -2:
                            case -1:
                            case 0:
                            case 1:
                                break;
                            default:
                                switch (readInt32) {
                                    case 25:
                                    case 26:
                                    case 27:
                                    case 28:
                                    case 29:
                                    case 30:
                                    case 31:
                                    case 32:
                                    case 33:
                                    case 34:
                                    case 35:
                                    case 36:
                                    case 37:
                                    case 38:
                                    case 39:
                                    case 40:
                                    case 41:
                                    case 42:
                                    case 43:
                                    case 44:
                                    case 45:
                                    case 46:
                                        break;
                                    default:
                                        switch (readInt32) {
                                            case 50:
                                            case 51:
                                            case 52:
                                            case 53:
                                            case 54:
                                            case 55:
                                            case 56:
                                            case 57:
                                            case 58:
                                            case 59:
                                            case 60:
                                                break;
                                            default:
                                                switch (readInt32) {
                                                    case 95:
                                                    case 96:
                                                    case 97:
                                                    case 98:
                                                    case 99:
                                                    case 100:
                                                    case 101:
                                                        break;
                                                    default:
                                                        switch (readInt32) {
                                                            case 111:
                                                            case RilDataCallFailCause.PDP_FAIL_APN_TYPE_CONFLICT /* 112 */:
                                                            case RilDataCallFailCause.PDP_FAIL_INVALID_PCSCF_ADDR /* 113 */:
                                                            case RilDataCallFailCause.PDP_FAIL_INTERNAL_CALL_PREEMPT_BY_HIGH_PRIO_APN /* 114 */:
                                                            case RilDataCallFailCause.PDP_FAIL_EMM_ACCESS_BARRED /* 115 */:
                                                            case RilDataCallFailCause.PDP_FAIL_EMERGENCY_IFACE_ONLY /* 116 */:
                                                            case RilDataCallFailCause.PDP_FAIL_IFACE_MISMATCH /* 117 */:
                                                            case RilDataCallFailCause.PDP_FAIL_COMPANION_IFACE_IN_USE /* 118 */:
                                                            case RilDataCallFailCause.PDP_FAIL_IP_ADDRESS_MISMATCH /* 119 */:
                                                            case RilDataCallFailCause.PDP_FAIL_IFACE_AND_POL_FAMILY_MISMATCH /* 120 */:
                                                            case RilDataCallFailCause.PDP_FAIL_EMM_ACCESS_BARRED_INFINITE_RETRY /* 121 */:
                                                            case RilDataCallFailCause.PDP_FAIL_AUTH_FAILURE_ON_EMERGENCY_CALL /* 122 */:
                                                            case RilDataCallFailCause.PDP_FAIL_INVALID_DNS_ADDR /* 123 */:
                                                            case RilDataCallFailCause.PDP_FAIL_INVALID_PCSCF_OR_DNS_ADDRESS /* 124 */:
                                                                break;
                                                            default:
                                                                switch (readInt32) {
                                                                }
                                                        }
                                                }
                                        }
                                }
                        }
                    }
                    this.status = readInt32;
                } else if (readTag == 16) {
                    this.suggestedRetryTimeMillis = codedInputByteBufferNano.readInt32();
                } else if (readTag != 26) {
                    if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                        return this;
                    }
                } else {
                    if (this.call == null) {
                        this.call = new TelephonyProto$RilDataCall();
                    }
                    codedInputByteBufferNano.readMessage(this.call);
                }
            }
        }

        public static RilSetupDataCallResponse parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (RilSetupDataCallResponse) MessageNano.mergeFrom(new RilSetupDataCallResponse(), bArr);
        }

        public static RilSetupDataCallResponse parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new RilSetupDataCallResponse().mergeFrom(codedInputByteBufferNano);
        }
    }

    /* loaded from: classes.dex */
    public static final class CarrierKeyChange extends ExtendableMessageNano<CarrierKeyChange> {
        private static volatile CarrierKeyChange[] _emptyArray;
        public boolean isDownloadSuccessful;
        public int keyType;

        /* loaded from: classes.dex */
        public interface KeyType {
            public static final int EPDG = 2;
            public static final int UNKNOWN = 0;
            public static final int WLAN = 1;
        }

        public static CarrierKeyChange[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new CarrierKeyChange[0];
                    }
                }
            }
            return _emptyArray;
        }

        public CarrierKeyChange() {
            clear();
        }

        public CarrierKeyChange clear() {
            this.keyType = 0;
            this.isDownloadSuccessful = false;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.keyType;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            boolean z = this.isDownloadSuccessful;
            if (z) {
                codedOutputByteBufferNano.writeBool(2, z);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            int i = this.keyType;
            if (i != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            boolean z = this.isDownloadSuccessful;
            return z ? computeSerializedSize + CodedOutputByteBufferNano.computeBoolSize(2, z) : computeSerializedSize;
        }

        @Override // com.android.internal.telephony.protobuf.nano.MessageNano
        public CarrierKeyChange mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag == 8) {
                    int readInt32 = codedInputByteBufferNano.readInt32();
                    if (readInt32 == 0 || readInt32 == 1 || readInt32 == 2) {
                        this.keyType = readInt32;
                    }
                } else if (readTag != 16) {
                    if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                        return this;
                    }
                } else {
                    this.isDownloadSuccessful = codedInputByteBufferNano.readBool();
                }
            }
        }

        public static CarrierKeyChange parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (CarrierKeyChange) MessageNano.mergeFrom(new CarrierKeyChange(), bArr);
        }

        public static CarrierKeyChange parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new CarrierKeyChange().mergeFrom(codedInputByteBufferNano);
        }
    }

    /* loaded from: classes.dex */
    public static final class RilDeactivateDataCall extends ExtendableMessageNano<RilDeactivateDataCall> {
        private static volatile RilDeactivateDataCall[] _emptyArray;
        public int cid;
        public int reason;

        /* loaded from: classes.dex */
        public interface DeactivateReason {
            public static final int DEACTIVATE_REASON_HANDOVER = 4;
            public static final int DEACTIVATE_REASON_NONE = 1;
            public static final int DEACTIVATE_REASON_PDP_RESET = 3;
            public static final int DEACTIVATE_REASON_RADIO_OFF = 2;
            public static final int DEACTIVATE_REASON_UNKNOWN = 0;
        }

        public static RilDeactivateDataCall[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new RilDeactivateDataCall[0];
                    }
                }
            }
            return _emptyArray;
        }

        public RilDeactivateDataCall() {
            clear();
        }

        public RilDeactivateDataCall clear() {
            this.cid = 0;
            this.reason = 0;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.cid;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            int i2 = this.reason;
            if (i2 != 0) {
                codedOutputByteBufferNano.writeInt32(2, i2);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            int i = this.cid;
            if (i != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.reason;
            return i2 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(2, i2) : computeSerializedSize;
        }

        @Override // com.android.internal.telephony.protobuf.nano.MessageNano
        public RilDeactivateDataCall mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag == 8) {
                    this.cid = codedInputByteBufferNano.readInt32();
                } else if (readTag != 16) {
                    if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                        return this;
                    }
                } else {
                    int readInt32 = codedInputByteBufferNano.readInt32();
                    if (readInt32 == 0 || readInt32 == 1 || readInt32 == 2 || readInt32 == 3 || readInt32 == 4) {
                        this.reason = readInt32;
                    }
                }
            }
        }

        public static RilDeactivateDataCall parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (RilDeactivateDataCall) MessageNano.mergeFrom(new RilDeactivateDataCall(), bArr);
        }

        public static RilDeactivateDataCall parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new RilDeactivateDataCall().mergeFrom(codedInputByteBufferNano);
        }
    }

    /* loaded from: classes.dex */
    public static final class ModemRestart extends ExtendableMessageNano<ModemRestart> {
        private static volatile ModemRestart[] _emptyArray;
        public String basebandVersion;
        public String reason;

        public static ModemRestart[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new ModemRestart[0];
                    }
                }
            }
            return _emptyArray;
        }

        public ModemRestart() {
            clear();
        }

        public ModemRestart clear() {
            this.basebandVersion = PhoneConfigurationManager.SSSS;
            this.reason = PhoneConfigurationManager.SSSS;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (!this.basebandVersion.equals(PhoneConfigurationManager.SSSS)) {
                codedOutputByteBufferNano.writeString(1, this.basebandVersion);
            }
            if (!this.reason.equals(PhoneConfigurationManager.SSSS)) {
                codedOutputByteBufferNano.writeString(2, this.reason);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (!this.basebandVersion.equals(PhoneConfigurationManager.SSSS)) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.basebandVersion);
            }
            return !this.reason.equals(PhoneConfigurationManager.SSSS) ? computeSerializedSize + CodedOutputByteBufferNano.computeStringSize(2, this.reason) : computeSerializedSize;
        }

        @Override // com.android.internal.telephony.protobuf.nano.MessageNano
        public ModemRestart mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag == 10) {
                    this.basebandVersion = codedInputByteBufferNano.readString();
                } else if (readTag != 18) {
                    if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                        return this;
                    }
                } else {
                    this.reason = codedInputByteBufferNano.readString();
                }
            }
        }

        public static ModemRestart parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (ModemRestart) MessageNano.mergeFrom(new ModemRestart(), bArr);
        }

        public static ModemRestart parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new ModemRestart().mergeFrom(codedInputByteBufferNano);
        }
    }

    /* loaded from: classes.dex */
    public static final class CarrierIdMatching extends ExtendableMessageNano<CarrierIdMatching> {
        private static volatile CarrierIdMatching[] _emptyArray;
        public int cidTableVersion;
        public CarrierIdMatchingResult result;

        public static CarrierIdMatching[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new CarrierIdMatching[0];
                    }
                }
            }
            return _emptyArray;
        }

        public CarrierIdMatching() {
            clear();
        }

        public CarrierIdMatching clear() {
            this.cidTableVersion = 0;
            this.result = null;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.cidTableVersion;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            CarrierIdMatchingResult carrierIdMatchingResult = this.result;
            if (carrierIdMatchingResult != null) {
                codedOutputByteBufferNano.writeMessage(2, carrierIdMatchingResult);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            int i = this.cidTableVersion;
            if (i != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            CarrierIdMatchingResult carrierIdMatchingResult = this.result;
            return carrierIdMatchingResult != null ? computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(2, carrierIdMatchingResult) : computeSerializedSize;
        }

        @Override // com.android.internal.telephony.protobuf.nano.MessageNano
        public CarrierIdMatching mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag == 8) {
                    this.cidTableVersion = codedInputByteBufferNano.readInt32();
                } else if (readTag != 18) {
                    if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                        return this;
                    }
                } else {
                    if (this.result == null) {
                        this.result = new CarrierIdMatchingResult();
                    }
                    codedInputByteBufferNano.readMessage(this.result);
                }
            }
        }

        public static CarrierIdMatching parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (CarrierIdMatching) MessageNano.mergeFrom(new CarrierIdMatching(), bArr);
        }

        public static CarrierIdMatching parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new CarrierIdMatching().mergeFrom(codedInputByteBufferNano);
        }
    }

    /* loaded from: classes.dex */
    public static final class CarrierIdMatchingResult extends ExtendableMessageNano<CarrierIdMatchingResult> {
        private static volatile CarrierIdMatchingResult[] _emptyArray;
        public int carrierId;
        public String gid1;
        public String gid2;
        public String iccidPrefix;
        public String imsiPrefix;
        public String mccmnc;
        public String pnn;
        public String preferApn;
        public String[] privilegeAccessRule;
        public String spn;
        public String unknownGid1;
        public String unknownMccmnc;

        public static CarrierIdMatchingResult[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new CarrierIdMatchingResult[0];
                    }
                }
            }
            return _emptyArray;
        }

        public CarrierIdMatchingResult() {
            clear();
        }

        public CarrierIdMatchingResult clear() {
            this.carrierId = 0;
            this.unknownGid1 = PhoneConfigurationManager.SSSS;
            this.unknownMccmnc = PhoneConfigurationManager.SSSS;
            this.mccmnc = PhoneConfigurationManager.SSSS;
            this.gid1 = PhoneConfigurationManager.SSSS;
            this.gid2 = PhoneConfigurationManager.SSSS;
            this.spn = PhoneConfigurationManager.SSSS;
            this.pnn = PhoneConfigurationManager.SSSS;
            this.iccidPrefix = PhoneConfigurationManager.SSSS;
            this.imsiPrefix = PhoneConfigurationManager.SSSS;
            this.privilegeAccessRule = WireFormatNano.EMPTY_STRING_ARRAY;
            this.preferApn = PhoneConfigurationManager.SSSS;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.carrierId;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            if (!this.unknownGid1.equals(PhoneConfigurationManager.SSSS)) {
                codedOutputByteBufferNano.writeString(2, this.unknownGid1);
            }
            if (!this.unknownMccmnc.equals(PhoneConfigurationManager.SSSS)) {
                codedOutputByteBufferNano.writeString(3, this.unknownMccmnc);
            }
            if (!this.mccmnc.equals(PhoneConfigurationManager.SSSS)) {
                codedOutputByteBufferNano.writeString(4, this.mccmnc);
            }
            if (!this.gid1.equals(PhoneConfigurationManager.SSSS)) {
                codedOutputByteBufferNano.writeString(5, this.gid1);
            }
            if (!this.gid2.equals(PhoneConfigurationManager.SSSS)) {
                codedOutputByteBufferNano.writeString(6, this.gid2);
            }
            if (!this.spn.equals(PhoneConfigurationManager.SSSS)) {
                codedOutputByteBufferNano.writeString(7, this.spn);
            }
            if (!this.pnn.equals(PhoneConfigurationManager.SSSS)) {
                codedOutputByteBufferNano.writeString(8, this.pnn);
            }
            if (!this.iccidPrefix.equals(PhoneConfigurationManager.SSSS)) {
                codedOutputByteBufferNano.writeString(9, this.iccidPrefix);
            }
            if (!this.imsiPrefix.equals(PhoneConfigurationManager.SSSS)) {
                codedOutputByteBufferNano.writeString(10, this.imsiPrefix);
            }
            String[] strArr = this.privilegeAccessRule;
            if (strArr != null && strArr.length > 0) {
                int i2 = 0;
                while (true) {
                    String[] strArr2 = this.privilegeAccessRule;
                    if (i2 >= strArr2.length) {
                        break;
                    }
                    String str = strArr2[i2];
                    if (str != null) {
                        codedOutputByteBufferNano.writeString(11, str);
                    }
                    i2++;
                }
            }
            if (!this.preferApn.equals(PhoneConfigurationManager.SSSS)) {
                codedOutputByteBufferNano.writeString(12, this.preferApn);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            int i = this.carrierId;
            if (i != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            if (!this.unknownGid1.equals(PhoneConfigurationManager.SSSS)) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(2, this.unknownGid1);
            }
            if (!this.unknownMccmnc.equals(PhoneConfigurationManager.SSSS)) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(3, this.unknownMccmnc);
            }
            if (!this.mccmnc.equals(PhoneConfigurationManager.SSSS)) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(4, this.mccmnc);
            }
            if (!this.gid1.equals(PhoneConfigurationManager.SSSS)) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(5, this.gid1);
            }
            if (!this.gid2.equals(PhoneConfigurationManager.SSSS)) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(6, this.gid2);
            }
            if (!this.spn.equals(PhoneConfigurationManager.SSSS)) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(7, this.spn);
            }
            if (!this.pnn.equals(PhoneConfigurationManager.SSSS)) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(8, this.pnn);
            }
            if (!this.iccidPrefix.equals(PhoneConfigurationManager.SSSS)) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(9, this.iccidPrefix);
            }
            if (!this.imsiPrefix.equals(PhoneConfigurationManager.SSSS)) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(10, this.imsiPrefix);
            }
            String[] strArr = this.privilegeAccessRule;
            if (strArr != null && strArr.length > 0) {
                int i2 = 0;
                int i3 = 0;
                int i4 = 0;
                while (true) {
                    String[] strArr2 = this.privilegeAccessRule;
                    if (i2 >= strArr2.length) {
                        break;
                    }
                    String str = strArr2[i2];
                    if (str != null) {
                        i4++;
                        i3 += CodedOutputByteBufferNano.computeStringSizeNoTag(str);
                    }
                    i2++;
                }
                computeSerializedSize = computeSerializedSize + i3 + (i4 * 1);
            }
            return !this.preferApn.equals(PhoneConfigurationManager.SSSS) ? computeSerializedSize + CodedOutputByteBufferNano.computeStringSize(12, this.preferApn) : computeSerializedSize;
        }

        @Override // com.android.internal.telephony.protobuf.nano.MessageNano
        public CarrierIdMatchingResult mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                switch (readTag) {
                    case 0:
                        return this;
                    case 8:
                        this.carrierId = codedInputByteBufferNano.readInt32();
                        break;
                    case 18:
                        this.unknownGid1 = codedInputByteBufferNano.readString();
                        break;
                    case 26:
                        this.unknownMccmnc = codedInputByteBufferNano.readString();
                        break;
                    case 34:
                        this.mccmnc = codedInputByteBufferNano.readString();
                        break;
                    case 42:
                        this.gid1 = codedInputByteBufferNano.readString();
                        break;
                    case 50:
                        this.gid2 = codedInputByteBufferNano.readString();
                        break;
                    case 58:
                        this.spn = codedInputByteBufferNano.readString();
                        break;
                    case 66:
                        this.pnn = codedInputByteBufferNano.readString();
                        break;
                    case RadioNVItems.RIL_NV_LTE_SCAN_PRIORITY_25 /* 74 */:
                        this.iccidPrefix = codedInputByteBufferNano.readString();
                        break;
                    case RadioNVItems.RIL_NV_LTE_BSR_MAX_TIME /* 82 */:
                        this.imsiPrefix = codedInputByteBufferNano.readString();
                        break;
                    case 90:
                        int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 90);
                        String[] strArr = this.privilegeAccessRule;
                        int length = strArr == null ? 0 : strArr.length;
                        int i = repeatedFieldArrayLength + length;
                        String[] strArr2 = new String[i];
                        if (length != 0) {
                            System.arraycopy(strArr, 0, strArr2, 0, length);
                        }
                        while (length < i - 1) {
                            strArr2[length] = codedInputByteBufferNano.readString();
                            codedInputByteBufferNano.readTag();
                            length++;
                        }
                        strArr2[length] = codedInputByteBufferNano.readString();
                        this.privilegeAccessRule = strArr2;
                        break;
                    case 98:
                        this.preferApn = codedInputByteBufferNano.readString();
                        break;
                    default:
                        if (storeUnknownField(codedInputByteBufferNano, readTag)) {
                            break;
                        } else {
                            return this;
                        }
                }
            }
        }

        public static CarrierIdMatchingResult parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (CarrierIdMatchingResult) MessageNano.mergeFrom(new CarrierIdMatchingResult(), bArr);
        }

        public static CarrierIdMatchingResult parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new CarrierIdMatchingResult().mergeFrom(codedInputByteBufferNano);
        }
    }

    /* loaded from: classes.dex */
    public static final class NetworkCapabilitiesInfo extends ExtendableMessageNano<NetworkCapabilitiesInfo> {
        private static volatile NetworkCapabilitiesInfo[] _emptyArray;
        public boolean isNetworkUnmetered;

        public static NetworkCapabilitiesInfo[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new NetworkCapabilitiesInfo[0];
                    }
                }
            }
            return _emptyArray;
        }

        public NetworkCapabilitiesInfo() {
            clear();
        }

        public NetworkCapabilitiesInfo clear() {
            this.isNetworkUnmetered = false;
            this.unknownFieldData = null;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            boolean z = this.isNetworkUnmetered;
            if (z) {
                codedOutputByteBufferNano.writeBool(1, z);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            boolean z = this.isNetworkUnmetered;
            return z ? computeSerializedSize + CodedOutputByteBufferNano.computeBoolSize(1, z) : computeSerializedSize;
        }

        @Override // com.android.internal.telephony.protobuf.nano.MessageNano
        public NetworkCapabilitiesInfo mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag != 8) {
                    if (!storeUnknownField(codedInputByteBufferNano, readTag)) {
                        return this;
                    }
                } else {
                    this.isNetworkUnmetered = codedInputByteBufferNano.readBool();
                }
            }
        }

        public static NetworkCapabilitiesInfo parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (NetworkCapabilitiesInfo) MessageNano.mergeFrom(new NetworkCapabilitiesInfo(), bArr);
        }

        public static NetworkCapabilitiesInfo parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new NetworkCapabilitiesInfo().mergeFrom(codedInputByteBufferNano);
        }
    }

    public static TelephonyProto$TelephonyEvent[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new TelephonyProto$TelephonyEvent[0];
                }
            }
        }
        return _emptyArray;
    }

    public TelephonyProto$TelephonyEvent() {
        clear();
    }

    public TelephonyProto$TelephonyEvent clear() {
        this.timestampMillis = 0L;
        this.phoneId = 0;
        this.type = 0;
        this.settings = null;
        this.serviceState = null;
        this.imsConnectionState = null;
        this.imsCapabilities = null;
        this.dataCalls = TelephonyProto$RilDataCall.emptyArray();
        this.error = 0;
        this.setupDataCall = null;
        this.setupDataCallResponse = null;
        this.deactivateDataCall = null;
        this.dataStallAction = 0;
        this.modemRestart = null;
        this.nitzTimestampMillis = 0L;
        this.carrierIdMatching = null;
        this.carrierKeyChange = null;
        this.dataSwitch = null;
        this.networkValidationState = 0;
        this.onDemandDataSwitch = null;
        this.simState = WireFormatNano.EMPTY_INT_ARRAY;
        this.activeSubscriptionInfo = null;
        this.enabledModemBitmap = 0;
        this.updatedEmergencyNumber = null;
        this.networkCapabilities = null;
        this.signalStrength = 0;
        this.emergencyNumberDatabaseVersion = 0;
        this.radioState = 0;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        long j = this.timestampMillis;
        if (j != 0) {
            codedOutputByteBufferNano.writeInt64(1, j);
        }
        int i = this.phoneId;
        if (i != 0) {
            codedOutputByteBufferNano.writeInt32(2, i);
        }
        int i2 = this.type;
        if (i2 != 0) {
            codedOutputByteBufferNano.writeInt32(3, i2);
        }
        TelephonyProto$TelephonySettings telephonyProto$TelephonySettings = this.settings;
        if (telephonyProto$TelephonySettings != null) {
            codedOutputByteBufferNano.writeMessage(4, telephonyProto$TelephonySettings);
        }
        TelephonyProto$TelephonyServiceState telephonyProto$TelephonyServiceState = this.serviceState;
        if (telephonyProto$TelephonyServiceState != null) {
            codedOutputByteBufferNano.writeMessage(5, telephonyProto$TelephonyServiceState);
        }
        TelephonyProto$ImsConnectionState telephonyProto$ImsConnectionState = this.imsConnectionState;
        if (telephonyProto$ImsConnectionState != null) {
            codedOutputByteBufferNano.writeMessage(6, telephonyProto$ImsConnectionState);
        }
        TelephonyProto$ImsCapabilities telephonyProto$ImsCapabilities = this.imsCapabilities;
        if (telephonyProto$ImsCapabilities != null) {
            codedOutputByteBufferNano.writeMessage(7, telephonyProto$ImsCapabilities);
        }
        TelephonyProto$RilDataCall[] telephonyProto$RilDataCallArr = this.dataCalls;
        int i3 = 0;
        if (telephonyProto$RilDataCallArr != null && telephonyProto$RilDataCallArr.length > 0) {
            int i4 = 0;
            while (true) {
                TelephonyProto$RilDataCall[] telephonyProto$RilDataCallArr2 = this.dataCalls;
                if (i4 >= telephonyProto$RilDataCallArr2.length) {
                    break;
                }
                TelephonyProto$RilDataCall telephonyProto$RilDataCall = telephonyProto$RilDataCallArr2[i4];
                if (telephonyProto$RilDataCall != null) {
                    codedOutputByteBufferNano.writeMessage(8, telephonyProto$RilDataCall);
                }
                i4++;
            }
        }
        int i5 = this.error;
        if (i5 != 0) {
            codedOutputByteBufferNano.writeInt32(9, i5);
        }
        RilSetupDataCall rilSetupDataCall = this.setupDataCall;
        if (rilSetupDataCall != null) {
            codedOutputByteBufferNano.writeMessage(10, rilSetupDataCall);
        }
        RilSetupDataCallResponse rilSetupDataCallResponse = this.setupDataCallResponse;
        if (rilSetupDataCallResponse != null) {
            codedOutputByteBufferNano.writeMessage(11, rilSetupDataCallResponse);
        }
        RilDeactivateDataCall rilDeactivateDataCall = this.deactivateDataCall;
        if (rilDeactivateDataCall != null) {
            codedOutputByteBufferNano.writeMessage(12, rilDeactivateDataCall);
        }
        int i6 = this.dataStallAction;
        if (i6 != 0) {
            codedOutputByteBufferNano.writeInt32(13, i6);
        }
        ModemRestart modemRestart = this.modemRestart;
        if (modemRestart != null) {
            codedOutputByteBufferNano.writeMessage(14, modemRestart);
        }
        long j2 = this.nitzTimestampMillis;
        if (j2 != 0) {
            codedOutputByteBufferNano.writeInt64(15, j2);
        }
        CarrierIdMatching carrierIdMatching = this.carrierIdMatching;
        if (carrierIdMatching != null) {
            codedOutputByteBufferNano.writeMessage(16, carrierIdMatching);
        }
        CarrierKeyChange carrierKeyChange = this.carrierKeyChange;
        if (carrierKeyChange != null) {
            codedOutputByteBufferNano.writeMessage(17, carrierKeyChange);
        }
        DataSwitch dataSwitch = this.dataSwitch;
        if (dataSwitch != null) {
            codedOutputByteBufferNano.writeMessage(19, dataSwitch);
        }
        int i7 = this.networkValidationState;
        if (i7 != 0) {
            codedOutputByteBufferNano.writeInt32(20, i7);
        }
        OnDemandDataSwitch onDemandDataSwitch = this.onDemandDataSwitch;
        if (onDemandDataSwitch != null) {
            codedOutputByteBufferNano.writeMessage(21, onDemandDataSwitch);
        }
        int[] iArr = this.simState;
        if (iArr != null && iArr.length > 0) {
            while (true) {
                int[] iArr2 = this.simState;
                if (i3 >= iArr2.length) {
                    break;
                }
                codedOutputByteBufferNano.writeInt32(22, iArr2[i3]);
                i3++;
            }
        }
        TelephonyProto$ActiveSubscriptionInfo telephonyProto$ActiveSubscriptionInfo = this.activeSubscriptionInfo;
        if (telephonyProto$ActiveSubscriptionInfo != null) {
            codedOutputByteBufferNano.writeMessage(23, telephonyProto$ActiveSubscriptionInfo);
        }
        int i8 = this.enabledModemBitmap;
        if (i8 != 0) {
            codedOutputByteBufferNano.writeInt32(24, i8);
        }
        TelephonyProto$EmergencyNumberInfo telephonyProto$EmergencyNumberInfo = this.updatedEmergencyNumber;
        if (telephonyProto$EmergencyNumberInfo != null) {
            codedOutputByteBufferNano.writeMessage(25, telephonyProto$EmergencyNumberInfo);
        }
        NetworkCapabilitiesInfo networkCapabilitiesInfo = this.networkCapabilities;
        if (networkCapabilitiesInfo != null) {
            codedOutputByteBufferNano.writeMessage(26, networkCapabilitiesInfo);
        }
        int i9 = this.signalStrength;
        if (i9 != 0) {
            codedOutputByteBufferNano.writeInt32(27, i9);
        }
        int i10 = this.emergencyNumberDatabaseVersion;
        if (i10 != 0) {
            codedOutputByteBufferNano.writeInt32(28, i10);
        }
        int i11 = this.radioState;
        if (i11 != 0) {
            codedOutputByteBufferNano.writeInt32(29, i11);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int[] iArr;
        int computeSerializedSize = super.computeSerializedSize();
        long j = this.timestampMillis;
        if (j != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(1, j);
        }
        int i = this.phoneId;
        if (i != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i);
        }
        int i2 = this.type;
        if (i2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i2);
        }
        TelephonyProto$TelephonySettings telephonyProto$TelephonySettings = this.settings;
        if (telephonyProto$TelephonySettings != null) {
            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(4, telephonyProto$TelephonySettings);
        }
        TelephonyProto$TelephonyServiceState telephonyProto$TelephonyServiceState = this.serviceState;
        if (telephonyProto$TelephonyServiceState != null) {
            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(5, telephonyProto$TelephonyServiceState);
        }
        TelephonyProto$ImsConnectionState telephonyProto$ImsConnectionState = this.imsConnectionState;
        if (telephonyProto$ImsConnectionState != null) {
            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(6, telephonyProto$ImsConnectionState);
        }
        TelephonyProto$ImsCapabilities telephonyProto$ImsCapabilities = this.imsCapabilities;
        if (telephonyProto$ImsCapabilities != null) {
            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(7, telephonyProto$ImsCapabilities);
        }
        TelephonyProto$RilDataCall[] telephonyProto$RilDataCallArr = this.dataCalls;
        int i3 = 0;
        if (telephonyProto$RilDataCallArr != null && telephonyProto$RilDataCallArr.length > 0) {
            int i4 = 0;
            while (true) {
                TelephonyProto$RilDataCall[] telephonyProto$RilDataCallArr2 = this.dataCalls;
                if (i4 >= telephonyProto$RilDataCallArr2.length) {
                    break;
                }
                TelephonyProto$RilDataCall telephonyProto$RilDataCall = telephonyProto$RilDataCallArr2[i4];
                if (telephonyProto$RilDataCall != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(8, telephonyProto$RilDataCall);
                }
                i4++;
            }
        }
        int i5 = this.error;
        if (i5 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(9, i5);
        }
        RilSetupDataCall rilSetupDataCall = this.setupDataCall;
        if (rilSetupDataCall != null) {
            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(10, rilSetupDataCall);
        }
        RilSetupDataCallResponse rilSetupDataCallResponse = this.setupDataCallResponse;
        if (rilSetupDataCallResponse != null) {
            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(11, rilSetupDataCallResponse);
        }
        RilDeactivateDataCall rilDeactivateDataCall = this.deactivateDataCall;
        if (rilDeactivateDataCall != null) {
            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(12, rilDeactivateDataCall);
        }
        int i6 = this.dataStallAction;
        if (i6 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(13, i6);
        }
        ModemRestart modemRestart = this.modemRestart;
        if (modemRestart != null) {
            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(14, modemRestart);
        }
        long j2 = this.nitzTimestampMillis;
        if (j2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(15, j2);
        }
        CarrierIdMatching carrierIdMatching = this.carrierIdMatching;
        if (carrierIdMatching != null) {
            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(16, carrierIdMatching);
        }
        CarrierKeyChange carrierKeyChange = this.carrierKeyChange;
        if (carrierKeyChange != null) {
            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(17, carrierKeyChange);
        }
        DataSwitch dataSwitch = this.dataSwitch;
        if (dataSwitch != null) {
            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(19, dataSwitch);
        }
        int i7 = this.networkValidationState;
        if (i7 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(20, i7);
        }
        OnDemandDataSwitch onDemandDataSwitch = this.onDemandDataSwitch;
        if (onDemandDataSwitch != null) {
            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(21, onDemandDataSwitch);
        }
        int[] iArr2 = this.simState;
        if (iArr2 != null && iArr2.length > 0) {
            int i8 = 0;
            while (true) {
                iArr = this.simState;
                if (i3 >= iArr.length) {
                    break;
                }
                i8 += CodedOutputByteBufferNano.computeInt32SizeNoTag(iArr[i3]);
                i3++;
            }
            computeSerializedSize = computeSerializedSize + i8 + (iArr.length * 2);
        }
        TelephonyProto$ActiveSubscriptionInfo telephonyProto$ActiveSubscriptionInfo = this.activeSubscriptionInfo;
        if (telephonyProto$ActiveSubscriptionInfo != null) {
            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(23, telephonyProto$ActiveSubscriptionInfo);
        }
        int i9 = this.enabledModemBitmap;
        if (i9 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(24, i9);
        }
        TelephonyProto$EmergencyNumberInfo telephonyProto$EmergencyNumberInfo = this.updatedEmergencyNumber;
        if (telephonyProto$EmergencyNumberInfo != null) {
            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(25, telephonyProto$EmergencyNumberInfo);
        }
        NetworkCapabilitiesInfo networkCapabilitiesInfo = this.networkCapabilities;
        if (networkCapabilitiesInfo != null) {
            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(26, networkCapabilitiesInfo);
        }
        int i10 = this.signalStrength;
        if (i10 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(27, i10);
        }
        int i11 = this.emergencyNumberDatabaseVersion;
        if (i11 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(28, i11);
        }
        int i12 = this.radioState;
        return i12 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(29, i12) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public TelephonyProto$TelephonyEvent mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            switch (readTag) {
                case 0:
                    return this;
                case 8:
                    this.timestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 16:
                    this.phoneId = codedInputByteBufferNano.readInt32();
                    break;
                case 24:
                    int readInt32 = codedInputByteBufferNano.readInt32();
                    switch (readInt32) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                        case 14:
                        case 15:
                        case 16:
                        case 17:
                        case 18:
                        case 19:
                        case 20:
                        case 21:
                        case 22:
                        case 23:
                        case 24:
                            this.type = readInt32;
                            continue;
                    }
                case 34:
                    if (this.settings == null) {
                        this.settings = new TelephonyProto$TelephonySettings();
                    }
                    codedInputByteBufferNano.readMessage(this.settings);
                    break;
                case 42:
                    if (this.serviceState == null) {
                        this.serviceState = new TelephonyProto$TelephonyServiceState();
                    }
                    codedInputByteBufferNano.readMessage(this.serviceState);
                    break;
                case 50:
                    if (this.imsConnectionState == null) {
                        this.imsConnectionState = new TelephonyProto$ImsConnectionState();
                    }
                    codedInputByteBufferNano.readMessage(this.imsConnectionState);
                    break;
                case 58:
                    if (this.imsCapabilities == null) {
                        this.imsCapabilities = new TelephonyProto$ImsCapabilities();
                    }
                    codedInputByteBufferNano.readMessage(this.imsCapabilities);
                    break;
                case 66:
                    int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 66);
                    TelephonyProto$RilDataCall[] telephonyProto$RilDataCallArr = this.dataCalls;
                    int length = telephonyProto$RilDataCallArr == null ? 0 : telephonyProto$RilDataCallArr.length;
                    int i = repeatedFieldArrayLength + length;
                    TelephonyProto$RilDataCall[] telephonyProto$RilDataCallArr2 = new TelephonyProto$RilDataCall[i];
                    if (length != 0) {
                        System.arraycopy(telephonyProto$RilDataCallArr, 0, telephonyProto$RilDataCallArr2, 0, length);
                    }
                    while (length < i - 1) {
                        TelephonyProto$RilDataCall telephonyProto$RilDataCall = new TelephonyProto$RilDataCall();
                        telephonyProto$RilDataCallArr2[length] = telephonyProto$RilDataCall;
                        codedInputByteBufferNano.readMessage(telephonyProto$RilDataCall);
                        codedInputByteBufferNano.readTag();
                        length++;
                    }
                    TelephonyProto$RilDataCall telephonyProto$RilDataCall2 = new TelephonyProto$RilDataCall();
                    telephonyProto$RilDataCallArr2[length] = telephonyProto$RilDataCall2;
                    codedInputByteBufferNano.readMessage(telephonyProto$RilDataCall2);
                    this.dataCalls = telephonyProto$RilDataCallArr2;
                    break;
                case RadioNVItems.RIL_NV_LTE_BAND_ENABLE_26 /* 72 */:
                    int readInt322 = codedInputByteBufferNano.readInt32();
                    switch (readInt322) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                        case 14:
                        case 15:
                        case 16:
                        case 17:
                        case 18:
                        case 19:
                        case 20:
                        case 21:
                        case 22:
                        case 23:
                        case 24:
                        case 25:
                        case 26:
                        case 27:
                        case 28:
                            this.error = readInt322;
                            break;
                        default:
                            switch (readInt322) {
                                case 36:
                                case 37:
                                case 38:
                                case 39:
                                case 40:
                                case 41:
                                case 42:
                                case 43:
                                case 44:
                                case 45:
                                case 46:
                                case 47:
                                case 48:
                                case 49:
                                case 50:
                                case 51:
                                case 52:
                                case 53:
                                case 54:
                                case 55:
                                case 56:
                                case 57:
                                case 58:
                                case 59:
                                case 60:
                                case TelephonyProto$RilErrno.RIL_E_NETWORK_NOT_READY /* 61 */:
                                case TelephonyProto$RilErrno.RIL_E_NOT_PROVISIONED /* 62 */:
                                case 63:
                                case 64:
                                case 65:
                                case 66:
                                case TelephonyProto$RilErrno.RIL_E_INVALID_RESPONSE /* 67 */:
                                    this.error = readInt322;
                                    break;
                            }
                    }
                case RadioNVItems.RIL_NV_LTE_BSR_MAX_TIME /* 82 */:
                    if (this.setupDataCall == null) {
                        this.setupDataCall = new RilSetupDataCall();
                    }
                    codedInputByteBufferNano.readMessage(this.setupDataCall);
                    break;
                case 90:
                    if (this.setupDataCallResponse == null) {
                        this.setupDataCallResponse = new RilSetupDataCallResponse();
                    }
                    codedInputByteBufferNano.readMessage(this.setupDataCallResponse);
                    break;
                case 98:
                    if (this.deactivateDataCall == null) {
                        this.deactivateDataCall = new RilDeactivateDataCall();
                    }
                    codedInputByteBufferNano.readMessage(this.deactivateDataCall);
                    break;
                case 104:
                    this.dataStallAction = codedInputByteBufferNano.readInt32();
                    break;
                case RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INTERNAL_CALL_PREEMPT_BY_HIGH_PRIO_APN /* 114 */:
                    if (this.modemRestart == null) {
                        this.modemRestart = new ModemRestart();
                    }
                    codedInputByteBufferNano.readMessage(this.modemRestart);
                    break;
                case RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IFACE_AND_POL_FAMILY_MISMATCH /* 120 */:
                    this.nitzTimestampMillis = codedInputByteBufferNano.readInt64();
                    break;
                case 130:
                    if (this.carrierIdMatching == null) {
                        this.carrierIdMatching = new CarrierIdMatching();
                    }
                    codedInputByteBufferNano.readMessage(this.carrierIdMatching);
                    break;
                case 138:
                    if (this.carrierKeyChange == null) {
                        this.carrierKeyChange = new CarrierKeyChange();
                    }
                    codedInputByteBufferNano.readMessage(this.carrierKeyChange);
                    break;
                case 154:
                    if (this.dataSwitch == null) {
                        this.dataSwitch = new DataSwitch();
                    }
                    codedInputByteBufferNano.readMessage(this.dataSwitch);
                    break;
                case SmsMessage.MAX_USER_DATA_SEPTETS /* 160 */:
                    int readInt323 = codedInputByteBufferNano.readInt32();
                    if (readInt323 != 0 && readInt323 != 1 && readInt323 != 2 && readInt323 != 3) {
                        break;
                    } else {
                        this.networkValidationState = readInt323;
                        break;
                    }
                case 170:
                    if (this.onDemandDataSwitch == null) {
                        this.onDemandDataSwitch = new OnDemandDataSwitch();
                    }
                    codedInputByteBufferNano.readMessage(this.onDemandDataSwitch);
                    break;
                case 176:
                    int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 176);
                    int[] iArr = new int[repeatedFieldArrayLength2];
                    int i2 = 0;
                    for (int i3 = 0; i3 < repeatedFieldArrayLength2; i3++) {
                        if (i3 != 0) {
                            codedInputByteBufferNano.readTag();
                        }
                        int readInt324 = codedInputByteBufferNano.readInt32();
                        if (readInt324 == 0 || readInt324 == 1 || readInt324 == 2) {
                            iArr[i2] = readInt324;
                            i2++;
                        }
                    }
                    if (i2 == 0) {
                        break;
                    } else {
                        int[] iArr2 = this.simState;
                        int length2 = iArr2 == null ? 0 : iArr2.length;
                        if (length2 == 0 && i2 == repeatedFieldArrayLength2) {
                            this.simState = iArr;
                            break;
                        } else {
                            int[] iArr3 = new int[length2 + i2];
                            if (length2 != 0) {
                                System.arraycopy(iArr2, 0, iArr3, 0, length2);
                            }
                            System.arraycopy(iArr, 0, iArr3, length2, i2);
                            this.simState = iArr3;
                            break;
                        }
                    }
                case 178:
                    int pushLimit = codedInputByteBufferNano.pushLimit(codedInputByteBufferNano.readRawVarint32());
                    int position = codedInputByteBufferNano.getPosition();
                    int i4 = 0;
                    while (codedInputByteBufferNano.getBytesUntilLimit() > 0) {
                        int readInt325 = codedInputByteBufferNano.readInt32();
                        if (readInt325 == 0 || readInt325 == 1 || readInt325 == 2) {
                            i4++;
                        }
                    }
                    if (i4 != 0) {
                        codedInputByteBufferNano.rewindToPosition(position);
                        int[] iArr4 = this.simState;
                        int length3 = iArr4 == null ? 0 : iArr4.length;
                        int[] iArr5 = new int[i4 + length3];
                        if (length3 != 0) {
                            System.arraycopy(iArr4, 0, iArr5, 0, length3);
                        }
                        while (codedInputByteBufferNano.getBytesUntilLimit() > 0) {
                            int readInt326 = codedInputByteBufferNano.readInt32();
                            if (readInt326 == 0 || readInt326 == 1 || readInt326 == 2) {
                                iArr5[length3] = readInt326;
                                length3++;
                            }
                        }
                        this.simState = iArr5;
                    }
                    codedInputByteBufferNano.popLimit(pushLimit);
                    break;
                case 186:
                    if (this.activeSubscriptionInfo == null) {
                        this.activeSubscriptionInfo = new TelephonyProto$ActiveSubscriptionInfo();
                    }
                    codedInputByteBufferNano.readMessage(this.activeSubscriptionInfo);
                    break;
                case DnsPacket.DnsRecord.NAME_COMPRESSION /* 192 */:
                    this.enabledModemBitmap = codedInputByteBufferNano.readInt32();
                    break;
                case 202:
                    if (this.updatedEmergencyNumber == null) {
                        this.updatedEmergencyNumber = new TelephonyProto$EmergencyNumberInfo();
                    }
                    codedInputByteBufferNano.readMessage(this.updatedEmergencyNumber);
                    break;
                case 210:
                    if (this.networkCapabilities == null) {
                        this.networkCapabilities = new NetworkCapabilitiesInfo();
                    }
                    codedInputByteBufferNano.readMessage(this.networkCapabilities);
                    break;
                case 216:
                    this.signalStrength = codedInputByteBufferNano.readInt32();
                    break;
                case 224:
                    this.emergencyNumberDatabaseVersion = codedInputByteBufferNano.readInt32();
                    break;
                case 232:
                    int readInt327 = codedInputByteBufferNano.readInt32();
                    if (readInt327 != 0 && readInt327 != 1 && readInt327 != 2 && readInt327 != 3) {
                        break;
                    } else {
                        this.radioState = readInt327;
                        break;
                    }
                default:
                    if (storeUnknownField(codedInputByteBufferNano, readTag)) {
                        break;
                    } else {
                        return this;
                    }
            }
        }
    }

    public static TelephonyProto$TelephonyEvent parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (TelephonyProto$TelephonyEvent) MessageNano.mergeFrom(new TelephonyProto$TelephonyEvent(), bArr);
    }

    public static TelephonyProto$TelephonyEvent parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new TelephonyProto$TelephonyEvent().mergeFrom(codedInputByteBufferNano);
    }
}
