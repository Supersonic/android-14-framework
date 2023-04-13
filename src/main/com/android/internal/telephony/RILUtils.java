package com.android.internal.telephony;

import android.hardware.radio.V1_0.AppStatus;
import android.hardware.radio.V1_0.Carrier;
import android.hardware.radio.V1_0.CdmaSignalStrength;
import android.hardware.radio.V1_0.CdmaSmsMessage;
import android.hardware.radio.V1_0.CdmaSmsSubaddress;
import android.hardware.radio.V1_0.CellInfoCdma;
import android.hardware.radio.V1_0.CellInfoGsm;
import android.hardware.radio.V1_0.CellInfoLte;
import android.hardware.radio.V1_0.CellInfoTdscdma;
import android.hardware.radio.V1_0.CellInfoWcdma;
import android.hardware.radio.V1_0.DataProfileInfo;
import android.hardware.radio.V1_0.Dial;
import android.hardware.radio.V1_0.EvdoSignalStrength;
import android.hardware.radio.V1_0.GsmSignalStrength;
import android.hardware.radio.V1_0.GsmSmsMessage;
import android.hardware.radio.V1_0.HardwareConfigModem;
import android.hardware.radio.V1_0.HardwareConfigSim;
import android.hardware.radio.V1_0.LceDataInfo;
import android.hardware.radio.V1_0.LteSignalStrength;
import android.hardware.radio.V1_0.SetupDataCallResult;
import android.hardware.radio.V1_0.SimApdu;
import android.hardware.radio.V1_0.TdScdmaSignalStrength;
import android.hardware.radio.V1_0.UusInfo;
import android.hardware.radio.V1_0.WcdmaSignalStrength;
import android.hardware.radio.V1_2.CellIdentityOperatorNames;
import android.hardware.radio.V1_2.TdscdmaSignalStrength;
import android.hardware.radio.V1_4.CellInfoNr;
import android.hardware.radio.V1_4.NrSignalStrength;
import android.hardware.radio.V1_5.BarringInfo;
import android.hardware.radio.V1_5.CardStatus;
import android.hardware.radio.V1_5.LinkAddress;
import android.hardware.radio.V1_5.OptionalCsgInfo;
import android.hardware.radio.V1_5.RadioAccessSpecifier;
import android.hardware.radio.V1_5.SignalThresholdInfo;
import android.hardware.radio.V1_6.EpsQos;
import android.hardware.radio.V1_6.MaybePort;
import android.hardware.radio.V1_6.NrQos;
import android.hardware.radio.V1_6.OptionalDnn;
import android.hardware.radio.V1_6.OptionalOsAppId;
import android.hardware.radio.V1_6.OptionalSliceInfo;
import android.hardware.radio.V1_6.OptionalTrafficDescriptor;
import android.hardware.radio.V1_6.OsAppId;
import android.hardware.radio.V1_6.PhonebookCapacity;
import android.hardware.radio.V1_6.PhonebookRecordInfo;
import android.hardware.radio.V1_6.PortRange;
import android.hardware.radio.V1_6.QosBandwidth;
import android.hardware.radio.V1_6.QosFilter;
import android.hardware.radio.V1_6.QosSession;
import android.hardware.radio.V1_6.SliceInfo;
import android.hardware.radio.V1_6.SlicingConfig;
import android.hardware.radio.config.SimPortInfo;
import android.hardware.radio.config.SimSlotStatus;
import android.hardware.radio.config.SlotPortMapping;
import android.hardware.radio.data.QosFilterIpsecSpi;
import android.hardware.radio.data.QosFilterIpv6FlowLabel;
import android.hardware.radio.data.QosFilterTypeOfService;
import android.hardware.radio.ims.ImsCall;
import android.hardware.radio.ims.SrvccCall;
import android.hardware.radio.messaging.CdmaSmsAddress;
import android.hardware.radio.network.BarringTypeSpecificInfo;
import android.hardware.radio.network.EmergencyNetworkScanTrigger;
import android.hardware.radio.network.OperatorInfo;
import android.hardware.radio.network.RadioAccessSpecifierBands;
import android.net.InetAddresses;
import android.net.LinkProperties;
import android.os.SystemClock;
import android.service.carrier.CarrierIdentifier;
import android.telephony.BarringInfo;
import android.telephony.CellConfigLte;
import android.telephony.CellIdentity;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityNr;
import android.telephony.CellIdentityTdscdma;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthNr;
import android.telephony.CellSignalStrengthTdscdma;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.ClosedSubscriberGroupInfo;
import android.telephony.EmergencyRegResult;
import android.telephony.LinkCapacityEstimate;
import android.telephony.ModemInfo;
import android.telephony.PhoneCapability;
import android.telephony.PhoneNumberUtils;
import android.telephony.RadioAccessSpecifier;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.UiccSlotMapping;
import android.telephony.data.ApnSetting;
import android.telephony.data.DataCallResponse;
import android.telephony.data.DataProfile;
import android.telephony.data.NetworkSliceInfo;
import android.telephony.data.NetworkSlicingConfig;
import android.telephony.data.Qos;
import android.telephony.data.QosBearerFilter;
import android.telephony.data.QosBearerSession;
import android.telephony.data.RouteSelectionDescriptor;
import android.telephony.data.TrafficDescriptor;
import android.telephony.data.UrspRule;
import android.telephony.satellite.PointingInfo;
import android.telephony.satellite.SatelliteCapabilities;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.Call;
import com.android.internal.telephony.CommandException;
import com.android.internal.telephony.MccTable;
import com.android.internal.telephony.cat.BerTlv;
import com.android.internal.telephony.cat.ComprehensionTlv;
import com.android.internal.telephony.cat.ComprehensionTlvTag;
import com.android.internal.telephony.cdma.SmsMessage;
import com.android.internal.telephony.cdma.sms.SmsEnvelope;
import com.android.internal.telephony.data.KeepaliveStatus;
import com.android.internal.telephony.imsphone.ImsCallInfo;
import com.android.internal.telephony.imsphone.ImsRttTextHandler;
import com.android.internal.telephony.nano.TelephonyProto$RilErrno;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyEvent;
import com.android.internal.telephony.uicc.AdnCapacity;
import com.android.internal.telephony.uicc.IccCardApplicationStatus;
import com.android.internal.telephony.uicc.IccCardStatus;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.internal.telephony.uicc.IccSimPortInfo;
import com.android.internal.telephony.uicc.IccSlotPortMapping;
import com.android.internal.telephony.uicc.IccSlotStatus;
import com.android.internal.telephony.uicc.IccUtils;
import com.android.internal.telephony.uicc.PortUtils;
import com.android.internal.telephony.uicc.SimPhonebookRecord;
import com.android.internal.telephony.uicc.UiccCardApplication;
import com.android.internal.telephony.util.NetworkStackConstants;
import com.android.telephony.Rlog;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class RILUtils {
    public static final int CDMA_BROADCAST_SMS_NO_OF_SERVICE_CATEGORIES = 31;
    public static final int CDMA_BSI_NO_OF_INTS_STRUCT = 3;
    public static final String RADIO_POWER_FAILURE_BUGREPORT_UUID = "316f3801-fa21-4954-a42f-0041eada3b31";
    public static final String RADIO_POWER_FAILURE_NO_RF_CALIBRATION_UUID = "316f3801-fa21-4954-a42f-0041eada3b33";
    public static final String RADIO_POWER_FAILURE_RF_HARDWARE_ISSUE_UUID = "316f3801-fa21-4954-a42f-0041eada3b32";
    private static final Set<Class> WRAPPER_CLASSES = new HashSet(Arrays.asList(Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class));

    public static int convertEmergencyScanType(int i) {
        int i2 = 1;
        if (i != 1) {
            i2 = 2;
            if (i != 2) {
                return 0;
            }
        }
        return i2;
    }

    public static int convertHalCellConnectionStatus(int i) {
        int i2 = 1;
        if (i != 1) {
            i2 = 2;
            if (i != 2) {
                return -1;
            }
        }
        return i2;
    }

    public static int convertHalConnectionFailureReason(int i) {
        switch (i) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 6;
            case 7:
                return 7;
            case 8:
                return 8;
            case 9:
                return 9;
            default:
                return 65535;
        }
    }

    public static int convertHalDeregistrationReason(int i) {
        int i2 = 1;
        if (i != 1) {
            i2 = 2;
            if (i != 2) {
                i2 = 3;
                if (i != 3) {
                    return 0;
                }
            }
        }
        return i2;
    }

    public static int convertHalKeepaliveStatusCode(int i) {
        if (i != 0) {
            int i2 = 1;
            if (i != 1) {
                i2 = 2;
                if (i != 2) {
                    return -1;
                }
            }
            return i2;
        }
        return 0;
    }

    public static int convertHalNetworkTypeBitMask(int i) {
        int i2 = (65536 & i) != 0 ? (int) (0 | 32768) : 0;
        if ((i & 2) != 0) {
            i2 = (int) (i2 | 1);
        }
        if ((i & 4) != 0) {
            i2 = (int) (i2 | 2);
        }
        if ((i & 16) != 0) {
            i2 = (int) (i2 | 8);
        }
        if ((i & 32) != 0) {
            i2 = (int) (i2 | 8);
        }
        if ((i & 64) != 0) {
            i2 = (int) (i2 | 64);
        }
        if ((i & 128) != 0) {
            i2 = (int) (i2 | 16);
        }
        if ((i & CallFailCause.RADIO_UPLINK_FAILURE) != 0) {
            i2 = (int) (i2 | 32);
        }
        if ((i & 4096) != 0) {
            i2 = (int) (i2 | 2048);
        }
        if ((i & NetworkStackConstants.IPV4_FLAG_MF) != 0) {
            i2 = (int) (i2 | 8192);
        }
        if ((i & 1024) != 0) {
            i2 = (int) (i2 | 256);
        }
        if ((i & 512) != 0) {
            i2 = (int) (i2 | 128);
        }
        if ((i & 2048) != 0) {
            i2 = (int) (i2 | 512);
        }
        if ((32768 & i) != 0) {
            i2 = (int) (i2 | 16384);
        }
        if ((i & 8) != 0) {
            i2 = (int) (i2 | 4);
        }
        if ((131072 & i) != 0) {
            i2 = (int) (i2 | 65536);
        }
        if ((i & 16384) != 0) {
            i2 = (int) (i2 | 4096);
        }
        if ((524288 & i) != 0) {
            i2 = (int) (i2 | 262144);
        }
        if ((1048576 & i) != 0) {
            i2 = (int) (i2 | 524288);
        }
        if ((i & InboundSmsTracker.DEST_PORT_FLAG_3GPP2) != 0) {
            i2 = (int) (i2 | 131072);
        }
        if (i2 == 0) {
            return 0;
        }
        return i2;
    }

    public static String convertHalOperatorStatus(int i) {
        return i == 0 ? "unknown" : i == 1 ? "available" : i == 2 ? "current" : i == 3 ? "forbidden" : PhoneConfigurationManager.SSSS;
    }

    public static int convertHalRadioAccessNetworks(int i) {
        int i2 = 1;
        if (i != 1) {
            i2 = 2;
            if (i != 2) {
                i2 = 3;
                if (i != 3) {
                    if (i != 4) {
                        return i != 5 ? 0 : 4;
                    }
                    return 6;
                }
            }
        }
        return i2;
    }

    public static int convertHalRegState(int i) {
        int i2 = 1;
        if (i != 1) {
            i2 = 2;
            if (i != 2) {
                if (i != 3) {
                    if (i != 4) {
                        if (i != 5) {
                            switch (i) {
                                case 12:
                                    break;
                                case 13:
                                    break;
                                case 14:
                                    break;
                                default:
                                    return 0;
                            }
                        } else {
                            return 5;
                        }
                    }
                    return 4;
                }
                return 3;
            }
        }
        return i2;
    }

    public static int convertImsCapability(int i) {
        int i2 = (i & 1) > 0 ? 1 : 0;
        if ((i & 2) > 0) {
            i2 |= 2;
        }
        if ((i & 4) > 0) {
            i2 |= 4;
        }
        return (i & 8) > 0 ? i2 | 8 : i2;
    }

    public static int convertImsRegistrationTech(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 3) {
                    return i != 4 ? 0 : 2;
                }
                return 6;
            }
            return 5;
        }
        return 3;
    }

    public static int convertImsTrafficDirection(int i) {
        return i != 0 ? 1 : 0;
    }

    public static int convertImsTrafficType(int i) {
        if (i != 0) {
            int i2 = 1;
            if (i != 1) {
                i2 = 2;
                if (i != 2) {
                    i2 = 3;
                    if (i != 3) {
                        i2 = 4;
                        if (i != 4) {
                            i2 = 5;
                            if (i != 5) {
                                return 6;
                            }
                        }
                    }
                }
            }
            return i2;
        }
        return 0;
    }

    public static String convertNullToEmptyString(String str) {
        return str != null ? str : PhoneConfigurationManager.SSSS;
    }

    public static int convertToHalAccessNetwork(int i) {
        switch (i) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 6;
            default:
                return 0;
        }
    }

    public static int convertToHalAccessNetworkAidl(int i) {
        switch (i) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 6;
            default:
                return 0;
        }
    }

    public static int convertToHalRadioAccessFamily(int i) {
        long j = i;
        int i2 = (32768 & j) != 0 ? InboundSmsTracker.DEST_PORT_FLAG_NO_PORT : 0;
        if ((1 & j) != 0) {
            i2 |= 2;
        }
        if ((2 & j) != 0) {
            i2 |= 4;
        }
        if ((8 & j) != 0) {
            i2 |= 16;
        }
        if ((64 & j) != 0) {
            i2 |= 64;
        }
        if ((16 & j) != 0) {
            i2 |= 128;
        }
        if ((32 & j) != 0) {
            i2 |= CallFailCause.RADIO_UPLINK_FAILURE;
        }
        if ((2048 & j) != 0) {
            i2 |= 4096;
        }
        if ((8192 & j) != 0) {
            i2 |= NetworkStackConstants.IPV4_FLAG_MF;
        }
        if ((256 & j) != 0) {
            i2 |= 1024;
        }
        if ((128 & j) != 0) {
            i2 |= 512;
        }
        if ((512 & j) != 0) {
            i2 |= 2048;
        }
        if ((16384 & j) != 0) {
            i2 |= 32768;
        }
        if ((4 & j) != 0) {
            i2 |= 8;
        }
        if ((65536 & j) != 0) {
            i2 |= 131072;
        }
        if ((131072 & j) != 0) {
            i2 |= InboundSmsTracker.DEST_PORT_FLAG_3GPP2;
        }
        if ((4096 & j) != 0) {
            i2 |= 16384;
        }
        if ((262144 & j) != 0) {
            i2 |= InboundSmsTracker.DEST_PORT_FLAG_3GPP2_WAP_PDU;
        }
        if ((j & 524288) != 0) {
            i2 |= 1048576;
        }
        if (i2 == 0) {
            return 1;
        }
        return i2;
    }

    public static int convertToHalRadioAccessFamilyAidl(int i) {
        long j = i;
        int i2 = (32768 & j) != 0 ? InboundSmsTracker.DEST_PORT_FLAG_NO_PORT : 0;
        if ((1 & j) != 0) {
            i2 |= 2;
        }
        if ((2 & j) != 0) {
            i2 |= 4;
        }
        if ((8 & j) != 0) {
            i2 |= 16;
        }
        if ((64 & j) != 0) {
            i2 |= 64;
        }
        if ((16 & j) != 0) {
            i2 |= 128;
        }
        if ((32 & j) != 0) {
            i2 |= CallFailCause.RADIO_UPLINK_FAILURE;
        }
        if ((2048 & j) != 0) {
            i2 |= 4096;
        }
        if ((8192 & j) != 0) {
            i2 |= NetworkStackConstants.IPV4_FLAG_MF;
        }
        if ((256 & j) != 0) {
            i2 |= 1024;
        }
        if ((128 & j) != 0) {
            i2 |= 512;
        }
        if ((512 & j) != 0) {
            i2 |= 2048;
        }
        if ((16384 & j) != 0) {
            i2 |= 32768;
        }
        if ((4 & j) != 0) {
            i2 |= 8;
        }
        if ((65536 & j) != 0) {
            i2 |= 131072;
        }
        if ((131072 & j) != 0) {
            i2 |= InboundSmsTracker.DEST_PORT_FLAG_3GPP2;
        }
        if ((4096 & j) != 0 || (262144 & j) != 0) {
            i2 |= 16384;
        }
        if ((j & 524288) != 0) {
            i2 |= 1048576;
        }
        if (i2 == 0) {
            return 1;
        }
        return i2;
    }

    public static int convertToHalRadioAccessNetworks(int i) {
        int i2 = 1;
        if (i != 1) {
            i2 = 2;
            if (i != 2) {
                i2 = 3;
                if (i != 3) {
                    if (i != 4) {
                        return i != 6 ? 0 : 4;
                    }
                    return 5;
                }
            }
        }
        return i2;
    }

    public static int convertToHalResetNvType(int i) {
        if (i != 1) {
            if (i != 2) {
                return i != 3 ? -1 : 2;
            }
            return 1;
        }
        return 0;
    }

    public static int convertToHalResetNvTypeAidl(int i) {
        if (i != 1) {
            if (i != 2) {
                return i != 3 ? -1 : 2;
            }
            return 1;
        }
        return 0;
    }

    public static int convertToHalSimLockMultiSimPolicy(int i) {
        return i != 1 ? 0 : 1;
    }

    public static int convertToHalSimLockMultiSimPolicyAidl(int i) {
        return i != 1 ? 0 : 1;
    }

    public static int convertToHalSmsWriteArgsStatus(int i) {
        int i2 = i & 7;
        if (i2 != 3) {
            if (i2 != 5) {
                return i2 != 7 ? 1 : 2;
            }
            return 3;
        }
        return 0;
    }

    public static int convertToHalSmsWriteArgsStatusAidl(int i) {
        int i2 = i & 7;
        if (i2 != 3) {
            if (i2 != 5) {
                return i2 != 7 ? 1 : 2;
            }
            return 3;
        }
        return 0;
    }

    public static String responseToString(int i) {
        switch (i) {
            case 1000:
                return "UNSOL_RESPONSE_RADIO_STATE_CHANGED";
            case 1001:
                return "UNSOL_RESPONSE_CALL_STATE_CHANGED";
            case 1002:
                return "UNSOL_RESPONSE_NETWORK_STATE_CHANGED";
            case 1003:
                return "UNSOL_RESPONSE_NEW_SMS";
            case 1004:
                return "UNSOL_RESPONSE_NEW_SMS_STATUS_REPORT";
            case 1005:
                return "UNSOL_RESPONSE_NEW_SMS_ON_SIM";
            case 1006:
                return "UNSOL_ON_USSD";
            case CallFailCause.CDMA_PREEMPTED /* 1007 */:
                return "UNSOL_ON_USSD_REQUEST";
            case CallFailCause.CDMA_NOT_EMERGENCY /* 1008 */:
                return "UNSOL_NITZ_TIME_RECEIVED";
            case CallFailCause.CDMA_ACCESS_BLOCKED /* 1009 */:
                return "UNSOL_SIGNAL_STRENGTH";
            case 1010:
                return "UNSOL_DATA_CALL_LIST_CHANGED";
            case 1011:
                return "UNSOL_SUPP_SVC_NOTIFICATION";
            case 1012:
                return "UNSOL_STK_SESSION_END";
            case 1013:
                return "UNSOL_STK_PROACTIVE_COMMAND";
            case 1014:
                return "UNSOL_STK_EVENT_NOTIFY";
            case 1015:
                return "UNSOL_STK_CALL_SETUP";
            case 1016:
                return "UNSOL_SIM_SMS_STORAGE_FULL";
            case 1017:
                return "UNSOL_SIM_REFRESH";
            case 1018:
                return "UNSOL_CALL_RING";
            case 1019:
                return "UNSOL_RESPONSE_SIM_STATUS_CHANGED";
            case 1020:
                return "UNSOL_RESPONSE_CDMA_NEW_SMS";
            case 1021:
                return "UNSOL_RESPONSE_NEW_BROADCAST_SMS";
            case 1022:
                return "UNSOL_CDMA_RUIM_SMS_STORAGE_FULL";
            case 1023:
                return "UNSOL_RESTRICTED_STATE_CHANGED";
            case 1024:
                return "UNSOL_ENTER_EMERGENCY_CALLBACK_MODE";
            case 1025:
                return "UNSOL_CDMA_CALL_WAITING";
            case 1026:
                return "UNSOL_CDMA_OTA_PROVISION_STATUS";
            case 1027:
                return "UNSOL_CDMA_INFO_REC";
            case 1028:
                return "UNSOL_OEM_HOOK_RAW";
            case 1029:
                return "UNSOL_RINGBACK_TONE";
            case 1030:
                return "UNSOL_RESEND_INCALL_MUTE";
            case 1031:
                return "UNSOL_CDMA_SUBSCRIPTION_SOURCE_CHANGED";
            case 1032:
                return "UNSOL_CDMA_PRL_CHANGED";
            case 1033:
                return "UNSOL_EXIT_EMERGENCY_CALLBACK_MODE";
            case 1034:
                return "UNSOL_RIL_CONNECTED";
            case 1035:
                return "UNSOL_VOICE_RADIO_TECH_CHANGED";
            case 1036:
                return "UNSOL_CELL_INFO_LIST";
            case 1037:
                return "UNSOL_RESPONSE_IMS_NETWORK_STATE_CHANGED";
            case 1038:
                return "UNSOL_UICC_SUBSCRIPTION_STATUS_CHANGED";
            case 1039:
                return "UNSOL_SRVCC_STATE_NOTIFY";
            case 1040:
                return "UNSOL_HARDWARE_CONFIG_CHANGED";
            case 1041:
                return "UNSOL_DC_RT_INFO_CHANGED";
            case 1042:
                return "UNSOL_RADIO_CAPABILITY";
            case 1043:
                return "UNSOL_ON_SS";
            case 1044:
                return "UNSOL_STK_CC_ALPHA_NOTIFY";
            case 1045:
                return "UNSOL_LCE_INFO_RECV";
            case 1046:
                return "UNSOL_PCO_DATA";
            case 1047:
                return "UNSOL_MODEM_RESTART";
            case 1048:
                return "UNSOL_CARRIER_INFO_IMSI_ENCRYPTION";
            case 1049:
                return "UNSOL_NETWORK_SCAN_RESULT";
            case 1050:
                return "UNSOL_KEEPALIVE_STATUS";
            default:
                switch (i) {
                    case 1052:
                        return "UNSOL_UNTHROTTLE_APN";
                    case 1053:
                        return "UNSOL_RESPONSE_SIM_PHONEBOOK_CHANGED";
                    case 1054:
                        return "UNSOL_RESPONSE_SIM_PHONEBOOK_RECORDS_RECEIVED";
                    default:
                        switch (i) {
                            case 1056:
                                return "UNSOL_PENDING_SATELLITE_MESSAGE_COUNT";
                            case 1057:
                                return "UNSOL_NEW_SATELLITE_MESSAGES";
                            case 1058:
                                return "UNSOL_SATELLITE_MESSAGES_TRANSFER_COMPLETE";
                            case 1059:
                                return "UNSOL_SATELLITE_POINTING_INFO_CHANGED";
                            case 1060:
                                return "UNSOL_SATELLITE_MODE_CHANGED";
                            case 1061:
                                return "UNSOL_SATELLITE_RADIO_TECHNOLOGY_CHANGED";
                            case 1062:
                                return "UNSOL_SATELLITE_PROVISION_STATE_CHANGED";
                            default:
                                switch (i) {
                                    case 1100:
                                        return "UNSOL_ICC_SLOT_STATUS";
                                    case 1101:
                                        return "UNSOL_PHYSICAL_CHANNEL_CONFIG";
                                    case 1102:
                                        return "UNSOL_EMERGENCY_NUMBER_LIST";
                                    case 1103:
                                        return "UNSOL_UICC_APPLICATIONS_ENABLEMENT_CHANGED";
                                    case 1104:
                                        return "UNSOL_REGISTRATION_FAILED";
                                    case 1105:
                                        return "UNSOL_BARRING_INFO_CHANGED";
                                    case 1106:
                                        return "UNSOL_EMERGENCY_NETWORK_SCAN_RESULT";
                                    case 1107:
                                        return "UNSOL_TRIGGER_IMS_DEREGISTRATION";
                                    case 1108:
                                        return "UNSOL_CONNECTION_SETUP_FAILURE";
                                    case 1109:
                                        return "UNSOL_NOTIFY_ANBR";
                                    default:
                                        return "<unknown response>";
                                }
                        }
                }
        }
    }

    public static String scanTypeToString(int i) {
        return i != 1 ? i != 2 ? "NO_PREFERENCE" : "FULL_SERVICE" : "LIMITED_SERVICE";
    }

    public static int convertToHalPersoType(IccCardApplicationStatus.PersoSubState persoSubState) {
        switch (C00571.f0x1c108904[persoSubState.ordinal()]) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 6;
            case 7:
                return 7;
            case 8:
                return 8;
            case 9:
                return 9;
            case 10:
                return 10;
            case 11:
                return 11;
            case 12:
                return 12;
            case 13:
                return 13;
            case 14:
                return 14;
            case 15:
                return 15;
            case 16:
                return 16;
            case 17:
                return 17;
            case 18:
                return 18;
            case 19:
                return 19;
            case 20:
                return 20;
            case 21:
                return 21;
            case 22:
                return 22;
            case 23:
                return 23;
            case 24:
                return 24;
            case 25:
                return 25;
            case 26:
                return 26;
            case 27:
                return 27;
            case 28:
                return 28;
            case 29:
                return 29;
            case 30:
                return 30;
            case 31:
                return 31;
            case 32:
                return 32;
            case 33:
                return 33;
            case 34:
                return 34;
            default:
                return 0;
        }
    }

    public static int convertToHalPersoTypeAidl(IccCardApplicationStatus.PersoSubState persoSubState) {
        switch (C00571.f0x1c108904[persoSubState.ordinal()]) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 6;
            case 7:
                return 7;
            case 8:
                return 8;
            case 9:
                return 9;
            case 10:
                return 10;
            case 11:
                return 11;
            case 12:
                return 12;
            case 13:
                return 13;
            case 14:
                return 14;
            case 15:
                return 15;
            case 16:
                return 16;
            case 17:
                return 17;
            case 18:
                return 18;
            case 19:
                return 19;
            case 20:
                return 20;
            case 21:
                return 21;
            case 22:
                return 22;
            case 23:
                return 23;
            case 24:
                return 24;
            case 25:
                return 25;
            case 26:
                return 26;
            case 27:
                return 27;
            case 28:
                return 28;
            case 29:
                return 29;
            case 30:
                return 30;
            case 31:
                return 31;
            case 32:
                return 32;
            case 33:
                return 33;
            case 34:
                return 34;
            default:
                return 0;
        }
    }

    public static GsmSmsMessage convertToHalGsmSmsMessage(String str, String str2) {
        GsmSmsMessage gsmSmsMessage = new GsmSmsMessage();
        if (str == null) {
            str = PhoneConfigurationManager.SSSS;
        }
        gsmSmsMessage.smscPdu = str;
        if (str2 == null) {
            str2 = PhoneConfigurationManager.SSSS;
        }
        gsmSmsMessage.pdu = str2;
        return gsmSmsMessage;
    }

    public static android.hardware.radio.messaging.GsmSmsMessage convertToHalGsmSmsMessageAidl(String str, String str2) {
        android.hardware.radio.messaging.GsmSmsMessage gsmSmsMessage = new android.hardware.radio.messaging.GsmSmsMessage();
        gsmSmsMessage.smscPdu = convertNullToEmptyString(str);
        gsmSmsMessage.pdu = convertNullToEmptyString(str2);
        return gsmSmsMessage;
    }

    public static CdmaSmsMessage convertToHalCdmaSmsMessage(byte[] bArr) {
        CdmaSmsMessage cdmaSmsMessage = new CdmaSmsMessage();
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bArr));
        try {
            cdmaSmsMessage.teleserviceId = dataInputStream.readInt();
            boolean z = true;
            cdmaSmsMessage.isServicePresent = ((byte) dataInputStream.readInt()) == 1;
            cdmaSmsMessage.serviceCategory = dataInputStream.readInt();
            cdmaSmsMessage.address.digitMode = dataInputStream.read();
            cdmaSmsMessage.address.numberMode = dataInputStream.read();
            cdmaSmsMessage.address.numberType = dataInputStream.read();
            cdmaSmsMessage.address.numberPlan = dataInputStream.read();
            byte read = (byte) dataInputStream.read();
            for (int i = 0; i < read; i++) {
                cdmaSmsMessage.address.digits.add(Byte.valueOf(dataInputStream.readByte()));
            }
            cdmaSmsMessage.subAddress.subaddressType = dataInputStream.read();
            CdmaSmsSubaddress cdmaSmsSubaddress = cdmaSmsMessage.subAddress;
            if (((byte) dataInputStream.read()) != 1) {
                z = false;
            }
            cdmaSmsSubaddress.odd = z;
            byte read2 = (byte) dataInputStream.read();
            for (int i2 = 0; i2 < read2; i2++) {
                cdmaSmsMessage.subAddress.digits.add(Byte.valueOf(dataInputStream.readByte()));
            }
            int read3 = dataInputStream.read();
            for (int i3 = 0; i3 < read3; i3++) {
                cdmaSmsMessage.bearerData.add(Byte.valueOf(dataInputStream.readByte()));
            }
        } catch (IOException unused) {
        }
        return cdmaSmsMessage;
    }

    public static android.hardware.radio.messaging.CdmaSmsMessage convertToHalCdmaSmsMessageAidl(byte[] bArr) {
        android.hardware.radio.messaging.CdmaSmsMessage cdmaSmsMessage = new android.hardware.radio.messaging.CdmaSmsMessage();
        cdmaSmsMessage.address = new CdmaSmsAddress();
        cdmaSmsMessage.subAddress = new android.hardware.radio.messaging.CdmaSmsSubaddress();
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bArr));
        try {
            cdmaSmsMessage.teleserviceId = dataInputStream.readInt();
            boolean z = true;
            cdmaSmsMessage.isServicePresent = ((byte) dataInputStream.readInt()) == 1;
            cdmaSmsMessage.serviceCategory = dataInputStream.readInt();
            cdmaSmsMessage.address.digitMode = dataInputStream.read();
            cdmaSmsMessage.address.isNumberModeDataNetwork = dataInputStream.read() == 1;
            cdmaSmsMessage.address.numberType = dataInputStream.read();
            cdmaSmsMessage.address.numberPlan = dataInputStream.read();
            int read = (byte) dataInputStream.read();
            byte[] bArr2 = new byte[read];
            for (int i = 0; i < read; i++) {
                bArr2[i] = dataInputStream.readByte();
            }
            cdmaSmsMessage.address.digits = bArr2;
            cdmaSmsMessage.subAddress.subaddressType = dataInputStream.read();
            android.hardware.radio.messaging.CdmaSmsSubaddress cdmaSmsSubaddress = cdmaSmsMessage.subAddress;
            if (((byte) dataInputStream.read()) != 1) {
                z = false;
            }
            cdmaSmsSubaddress.odd = z;
            int read2 = (byte) dataInputStream.read();
            byte[] bArr3 = new byte[read2];
            for (int i2 = 0; i2 < read2; i2++) {
                bArr3[i2] = dataInputStream.readByte();
            }
            cdmaSmsMessage.subAddress.digits = bArr3;
            int read3 = dataInputStream.read();
            byte[] bArr4 = new byte[read3];
            for (int i3 = 0; i3 < read3; i3++) {
                bArr4[i3] = dataInputStream.readByte();
            }
            cdmaSmsMessage.bearerData = bArr4;
        } catch (IOException unused) {
        }
        return cdmaSmsMessage;
    }

    public static SmsMessage convertHalCdmaSmsMessage(CdmaSmsMessage cdmaSmsMessage) {
        SmsEnvelope smsEnvelope = new SmsEnvelope();
        com.android.internal.telephony.cdma.sms.CdmaSmsAddress cdmaSmsAddress = new com.android.internal.telephony.cdma.sms.CdmaSmsAddress();
        com.android.internal.telephony.cdma.sms.CdmaSmsSubaddress cdmaSmsSubaddress = new com.android.internal.telephony.cdma.sms.CdmaSmsSubaddress();
        int i = cdmaSmsMessage.teleserviceId;
        smsEnvelope.teleService = i;
        if (cdmaSmsMessage.isServicePresent) {
            smsEnvelope.messageType = 1;
        } else if (i == 0) {
            smsEnvelope.messageType = 2;
        } else {
            smsEnvelope.messageType = 0;
        }
        smsEnvelope.serviceCategory = cdmaSmsMessage.serviceCategory;
        android.hardware.radio.V1_0.CdmaSmsAddress cdmaSmsAddress2 = cdmaSmsMessage.address;
        int i2 = cdmaSmsAddress2.digitMode;
        cdmaSmsAddress.digitMode = (byte) (i2 & 255);
        cdmaSmsAddress.numberMode = (byte) (cdmaSmsAddress2.numberMode & 255);
        cdmaSmsAddress.ton = cdmaSmsAddress2.numberType;
        cdmaSmsAddress.numberPlan = (byte) (cdmaSmsAddress2.numberPlan & 255);
        int size = (byte) cdmaSmsAddress2.digits.size();
        cdmaSmsAddress.numberOfDigits = size;
        byte[] bArr = new byte[size];
        for (int i3 = 0; i3 < size; i3++) {
            byte byteValue = ((Byte) cdmaSmsMessage.address.digits.get(i3)).byteValue();
            bArr[i3] = byteValue;
            if (i2 == 0) {
                bArr[i3] = SmsMessage.convertDtmfToAscii(byteValue);
            }
        }
        cdmaSmsAddress.origBytes = bArr;
        CdmaSmsSubaddress cdmaSmsSubaddress2 = cdmaSmsMessage.subAddress;
        cdmaSmsSubaddress.type = cdmaSmsSubaddress2.subaddressType;
        cdmaSmsSubaddress.odd = cdmaSmsSubaddress2.odd ? (byte) 1 : (byte) 0;
        int size2 = (byte) cdmaSmsSubaddress2.digits.size();
        if (size2 < 0) {
            size2 = 0;
        }
        byte[] bArr2 = new byte[size2];
        for (int i4 = 0; i4 < size2; i4++) {
            bArr2[i4] = ((Byte) cdmaSmsMessage.subAddress.digits.get(i4)).byteValue();
        }
        cdmaSmsSubaddress.origBytes = bArr2;
        int size3 = cdmaSmsMessage.bearerData.size();
        if (size3 < 0) {
            size3 = 0;
        }
        byte[] bArr3 = new byte[size3];
        for (int i5 = 0; i5 < size3; i5++) {
            bArr3[i5] = ((Byte) cdmaSmsMessage.bearerData.get(i5)).byteValue();
        }
        smsEnvelope.bearerData = bArr3;
        smsEnvelope.origAddress = cdmaSmsAddress;
        smsEnvelope.origSubaddress = cdmaSmsSubaddress;
        return new SmsMessage(cdmaSmsAddress, smsEnvelope);
    }

    public static SmsMessage convertHalCdmaSmsMessage(android.hardware.radio.messaging.CdmaSmsMessage cdmaSmsMessage) {
        SmsEnvelope smsEnvelope = new SmsEnvelope();
        com.android.internal.telephony.cdma.sms.CdmaSmsAddress cdmaSmsAddress = new com.android.internal.telephony.cdma.sms.CdmaSmsAddress();
        com.android.internal.telephony.cdma.sms.CdmaSmsSubaddress cdmaSmsSubaddress = new com.android.internal.telephony.cdma.sms.CdmaSmsSubaddress();
        CdmaSmsAddress cdmaSmsAddress2 = cdmaSmsMessage.address;
        int i = cdmaSmsAddress2.digitMode;
        cdmaSmsAddress.digitMode = (byte) (i & 255);
        cdmaSmsAddress.numberMode = (byte) ((cdmaSmsAddress2.isNumberModeDataNetwork ? 1 : 0) & 255);
        cdmaSmsAddress.ton = cdmaSmsAddress2.numberType;
        cdmaSmsAddress.numberPlan = (byte) (cdmaSmsAddress2.numberPlan & 255);
        byte[] bArr = cdmaSmsAddress2.digits;
        cdmaSmsAddress.numberOfDigits = bArr.length;
        int length = bArr.length;
        byte[] bArr2 = new byte[length];
        for (int i2 = 0; i2 < length; i2++) {
            byte b = cdmaSmsMessage.address.digits[i2];
            bArr2[i2] = b;
            if (i == 0) {
                bArr2[i2] = SmsMessage.convertDtmfToAscii(b);
            }
        }
        cdmaSmsAddress.origBytes = bArr2;
        android.hardware.radio.messaging.CdmaSmsSubaddress cdmaSmsSubaddress2 = cdmaSmsMessage.subAddress;
        cdmaSmsSubaddress.type = cdmaSmsSubaddress2.subaddressType;
        cdmaSmsSubaddress.odd = cdmaSmsSubaddress2.odd ? (byte) 1 : (byte) 0;
        cdmaSmsSubaddress.origBytes = cdmaSmsSubaddress2.digits;
        int i3 = cdmaSmsMessage.teleserviceId;
        smsEnvelope.teleService = i3;
        if (cdmaSmsMessage.isServicePresent) {
            smsEnvelope.messageType = 1;
        } else if (i3 == 0) {
            smsEnvelope.messageType = 2;
        } else {
            smsEnvelope.messageType = 0;
        }
        smsEnvelope.serviceCategory = cdmaSmsMessage.serviceCategory;
        smsEnvelope.bearerData = cdmaSmsMessage.bearerData;
        smsEnvelope.origAddress = cdmaSmsAddress;
        smsEnvelope.origSubaddress = cdmaSmsSubaddress;
        return new SmsMessage(cdmaSmsAddress, smsEnvelope);
    }

    public static DataProfileInfo convertToHalDataProfile10(DataProfile dataProfile) {
        DataProfileInfo dataProfileInfo = new DataProfileInfo();
        dataProfileInfo.profileId = dataProfile.getProfileId();
        dataProfileInfo.apn = dataProfile.getApn();
        dataProfileInfo.protocol = ApnSetting.getProtocolStringFromInt(dataProfile.getProtocolType());
        dataProfileInfo.roamingProtocol = ApnSetting.getProtocolStringFromInt(dataProfile.getRoamingProtocolType());
        dataProfileInfo.authType = dataProfile.getAuthType();
        dataProfileInfo.user = TextUtils.emptyIfNull(dataProfile.getUserName());
        dataProfileInfo.password = TextUtils.emptyIfNull(dataProfile.getPassword());
        dataProfileInfo.type = dataProfile.getType();
        dataProfileInfo.maxConnsTime = dataProfile.getMaxConnectionsTime();
        dataProfileInfo.maxConns = dataProfile.getMaxConnections();
        dataProfileInfo.waitTime = dataProfile.getWaitTime();
        dataProfileInfo.enabled = dataProfile.isEnabled();
        dataProfileInfo.supportedApnTypesBitmap = dataProfile.getSupportedApnTypesBitmask();
        dataProfileInfo.bearerBitmap = ServiceState.convertNetworkTypeBitmaskToBearerBitmask(dataProfile.getBearerBitmask()) << 1;
        dataProfileInfo.mtu = dataProfile.getMtuV4();
        dataProfileInfo.mvnoType = 0;
        dataProfileInfo.mvnoMatchData = PhoneConfigurationManager.SSSS;
        return dataProfileInfo;
    }

    public static android.hardware.radio.V1_4.DataProfileInfo convertToHalDataProfile14(DataProfile dataProfile) {
        android.hardware.radio.V1_4.DataProfileInfo dataProfileInfo = new android.hardware.radio.V1_4.DataProfileInfo();
        dataProfileInfo.apn = dataProfile.getApn();
        dataProfileInfo.protocol = dataProfile.getProtocolType();
        dataProfileInfo.roamingProtocol = dataProfile.getRoamingProtocolType();
        dataProfileInfo.authType = dataProfile.getAuthType();
        dataProfileInfo.user = TextUtils.emptyIfNull(dataProfile.getUserName());
        dataProfileInfo.password = TextUtils.emptyIfNull(dataProfile.getPassword());
        dataProfileInfo.type = dataProfile.getType();
        dataProfileInfo.maxConnsTime = dataProfile.getMaxConnectionsTime();
        dataProfileInfo.maxConns = dataProfile.getMaxConnections();
        dataProfileInfo.waitTime = dataProfile.getWaitTime();
        dataProfileInfo.enabled = dataProfile.isEnabled();
        dataProfileInfo.supportedApnTypesBitmap = dataProfile.getSupportedApnTypesBitmask();
        dataProfileInfo.bearerBitmap = ServiceState.convertNetworkTypeBitmaskToBearerBitmask(dataProfile.getBearerBitmask()) << 1;
        dataProfileInfo.mtu = dataProfile.getMtuV4();
        dataProfileInfo.persistent = dataProfile.isPersistent();
        dataProfileInfo.preferred = dataProfile.isPreferred();
        dataProfileInfo.profileId = dataProfileInfo.persistent ? dataProfile.getProfileId() : -1;
        return dataProfileInfo;
    }

    public static android.hardware.radio.V1_5.DataProfileInfo convertToHalDataProfile15(DataProfile dataProfile) {
        android.hardware.radio.V1_5.DataProfileInfo dataProfileInfo = new android.hardware.radio.V1_5.DataProfileInfo();
        dataProfileInfo.apn = dataProfile.getApn();
        dataProfileInfo.protocol = dataProfile.getProtocolType();
        dataProfileInfo.roamingProtocol = dataProfile.getRoamingProtocolType();
        dataProfileInfo.authType = dataProfile.getAuthType();
        dataProfileInfo.user = TextUtils.emptyIfNull(dataProfile.getUserName());
        dataProfileInfo.password = TextUtils.emptyIfNull(dataProfile.getPassword());
        dataProfileInfo.type = dataProfile.getType();
        dataProfileInfo.maxConnsTime = dataProfile.getMaxConnectionsTime();
        dataProfileInfo.maxConns = dataProfile.getMaxConnections();
        dataProfileInfo.waitTime = dataProfile.getWaitTime();
        dataProfileInfo.enabled = dataProfile.isEnabled();
        dataProfileInfo.supportedApnTypesBitmap = dataProfile.getSupportedApnTypesBitmask();
        dataProfileInfo.bearerBitmap = ServiceState.convertNetworkTypeBitmaskToBearerBitmask(dataProfile.getBearerBitmask()) << 1;
        dataProfileInfo.mtuV4 = dataProfile.getMtuV4();
        dataProfileInfo.mtuV6 = dataProfile.getMtuV6();
        dataProfileInfo.persistent = dataProfile.isPersistent();
        dataProfileInfo.preferred = dataProfile.isPreferred();
        dataProfileInfo.profileId = dataProfileInfo.persistent ? dataProfile.getProfileId() : -1;
        return dataProfileInfo;
    }

    public static android.hardware.radio.data.DataProfileInfo convertToHalDataProfile(DataProfile dataProfile) {
        android.hardware.radio.data.DataProfileInfo dataProfileInfo = new android.hardware.radio.data.DataProfileInfo();
        dataProfileInfo.apn = dataProfile.getApn();
        dataProfileInfo.protocol = dataProfile.getProtocolType();
        dataProfileInfo.roamingProtocol = dataProfile.getRoamingProtocolType();
        dataProfileInfo.authType = dataProfile.getAuthType();
        dataProfileInfo.user = convertNullToEmptyString(dataProfile.getUserName());
        dataProfileInfo.password = convertNullToEmptyString(dataProfile.getPassword());
        dataProfileInfo.type = dataProfile.getType();
        dataProfileInfo.maxConnsTime = dataProfile.getMaxConnectionsTime();
        dataProfileInfo.maxConns = dataProfile.getMaxConnections();
        dataProfileInfo.waitTime = dataProfile.getWaitTime();
        dataProfileInfo.enabled = dataProfile.isEnabled();
        dataProfileInfo.supportedApnTypesBitmap = dataProfile.getSupportedApnTypesBitmask();
        dataProfileInfo.bearerBitmap = ServiceState.convertNetworkTypeBitmaskToBearerBitmask(dataProfile.getBearerBitmask()) << 1;
        dataProfileInfo.mtuV4 = dataProfile.getMtuV4();
        dataProfileInfo.mtuV6 = dataProfile.getMtuV6();
        dataProfileInfo.persistent = dataProfile.isPersistent();
        dataProfileInfo.preferred = dataProfile.isPreferred();
        dataProfileInfo.alwaysOn = false;
        if (dataProfile.getApnSetting() != null) {
            dataProfileInfo.alwaysOn = dataProfile.getApnSetting().isAlwaysOn();
        }
        dataProfileInfo.trafficDescriptor = convertToHalTrafficDescriptorAidl(dataProfile.getTrafficDescriptor());
        dataProfileInfo.profileId = dataProfileInfo.persistent ? dataProfile.getProfileId() : -1;
        return dataProfileInfo;
    }

    public static DataProfile convertToDataProfile(android.hardware.radio.data.DataProfileInfo dataProfileInfo) {
        TrafficDescriptor trafficDescriptor;
        ApnSetting build = new ApnSetting.Builder().setEntryName(dataProfileInfo.apn).setApnName(dataProfileInfo.apn).setApnTypeBitmask(dataProfileInfo.supportedApnTypesBitmap).setAuthType(dataProfileInfo.authType).setMaxConnsTime(dataProfileInfo.maxConnsTime).setMaxConns(dataProfileInfo.maxConns).setWaitTime(dataProfileInfo.waitTime).setCarrierEnabled(dataProfileInfo.enabled).setModemCognitive(dataProfileInfo.persistent).setMtuV4(dataProfileInfo.mtuV4).setMtuV6(dataProfileInfo.mtuV6).setNetworkTypeBitmask(ServiceState.convertBearerBitmaskToNetworkTypeBitmask(dataProfileInfo.bearerBitmap) >> 1).setProfileId(dataProfileInfo.profileId).setPassword(dataProfileInfo.password).setProtocol(dataProfileInfo.protocol).setRoamingProtocol(dataProfileInfo.roamingProtocol).setUser(dataProfileInfo.user).setAlwaysOn(dataProfileInfo.alwaysOn).build();
        try {
            trafficDescriptor = convertHalTrafficDescriptor(dataProfileInfo.trafficDescriptor);
        } catch (IllegalArgumentException e) {
            loge("convertToDataProfile: Failed to convert traffic descriptor. e=" + e);
            trafficDescriptor = null;
        }
        return new DataProfile.Builder().setType(dataProfileInfo.type).setPreferred(dataProfileInfo.preferred).setTrafficDescriptor(trafficDescriptor).setApnSetting(build).build();
    }

    public static OptionalSliceInfo convertToHalSliceInfo(NetworkSliceInfo networkSliceInfo) {
        OptionalSliceInfo optionalSliceInfo = new OptionalSliceInfo();
        if (networkSliceInfo == null) {
            return optionalSliceInfo;
        }
        SliceInfo sliceInfo = new SliceInfo();
        sliceInfo.sst = (byte) networkSliceInfo.getSliceServiceType();
        sliceInfo.mappedHplmnSst = (byte) networkSliceInfo.getMappedHplmnSliceServiceType();
        sliceInfo.sliceDifferentiator = networkSliceInfo.getSliceDifferentiator();
        sliceInfo.mappedHplmnSD = networkSliceInfo.getMappedHplmnSliceDifferentiator();
        optionalSliceInfo.value(sliceInfo);
        return optionalSliceInfo;
    }

    public static android.hardware.radio.data.SliceInfo convertToHalSliceInfoAidl(NetworkSliceInfo networkSliceInfo) {
        if (networkSliceInfo == null) {
            return null;
        }
        android.hardware.radio.data.SliceInfo sliceInfo = new android.hardware.radio.data.SliceInfo();
        sliceInfo.sliceServiceType = (byte) networkSliceInfo.getSliceServiceType();
        sliceInfo.mappedHplmnSst = (byte) networkSliceInfo.getMappedHplmnSliceServiceType();
        sliceInfo.sliceDifferentiator = networkSliceInfo.getSliceDifferentiator();
        sliceInfo.mappedHplmnSd = networkSliceInfo.getMappedHplmnSliceDifferentiator();
        return sliceInfo;
    }

    public static OptionalTrafficDescriptor convertToHalTrafficDescriptor(TrafficDescriptor trafficDescriptor) {
        OptionalTrafficDescriptor optionalTrafficDescriptor = new OptionalTrafficDescriptor();
        if (trafficDescriptor == null) {
            return optionalTrafficDescriptor;
        }
        android.hardware.radio.V1_6.TrafficDescriptor trafficDescriptor2 = new android.hardware.radio.V1_6.TrafficDescriptor();
        OptionalDnn optionalDnn = new OptionalDnn();
        if (trafficDescriptor.getDataNetworkName() != null) {
            optionalDnn.value(trafficDescriptor.getDataNetworkName());
        }
        trafficDescriptor2.dnn = optionalDnn;
        OptionalOsAppId optionalOsAppId = new OptionalOsAppId();
        if (trafficDescriptor.getOsAppId() != null) {
            OsAppId osAppId = new OsAppId();
            osAppId.osAppId = primitiveArrayToArrayList(trafficDescriptor.getOsAppId());
            optionalOsAppId.value(osAppId);
        }
        trafficDescriptor2.osAppId = optionalOsAppId;
        optionalTrafficDescriptor.value(trafficDescriptor2);
        return optionalTrafficDescriptor;
    }

    public static android.hardware.radio.data.TrafficDescriptor convertToHalTrafficDescriptorAidl(TrafficDescriptor trafficDescriptor) {
        if (trafficDescriptor == null) {
            return new android.hardware.radio.data.TrafficDescriptor();
        }
        android.hardware.radio.data.TrafficDescriptor trafficDescriptor2 = new android.hardware.radio.data.TrafficDescriptor();
        trafficDescriptor2.dnn = trafficDescriptor.getDataNetworkName();
        if (trafficDescriptor.getOsAppId() == null) {
            trafficDescriptor2.osAppId = null;
        } else {
            android.hardware.radio.data.OsAppId osAppId = new android.hardware.radio.data.OsAppId();
            osAppId.osAppId = trafficDescriptor.getOsAppId();
            trafficDescriptor2.osAppId = osAppId;
        }
        return trafficDescriptor2;
    }

    public static ArrayList<LinkAddress> convertToHalLinkProperties15(LinkProperties linkProperties) {
        ArrayList<LinkAddress> arrayList = new ArrayList<>();
        if (linkProperties != null) {
            for (android.net.LinkAddress linkAddress : linkProperties.getAllLinkAddresses()) {
                LinkAddress linkAddress2 = new LinkAddress();
                linkAddress2.address = linkAddress.getAddress().getHostAddress();
                linkAddress2.properties = linkAddress.getFlags();
                linkAddress2.deprecationTime = linkAddress.getDeprecationTime();
                linkAddress2.expirationTime = linkAddress.getExpirationTime();
                arrayList.add(linkAddress2);
            }
        }
        return arrayList;
    }

    public static android.hardware.radio.data.LinkAddress[] convertToHalLinkProperties(LinkProperties linkProperties) {
        if (linkProperties == null) {
            return new android.hardware.radio.data.LinkAddress[0];
        }
        android.hardware.radio.data.LinkAddress[] linkAddressArr = new android.hardware.radio.data.LinkAddress[linkProperties.getAllLinkAddresses().size()];
        for (int i = 0; i < linkProperties.getAllLinkAddresses().size(); i++) {
            android.net.LinkAddress linkAddress = (android.net.LinkAddress) linkProperties.getAllLinkAddresses().get(i);
            android.hardware.radio.data.LinkAddress linkAddress2 = new android.hardware.radio.data.LinkAddress();
            linkAddress2.address = linkAddress.getAddress().getHostAddress();
            linkAddress2.addressProperties = linkAddress.getFlags();
            linkAddress2.deprecationTime = linkAddress.getDeprecationTime();
            linkAddress2.expirationTime = linkAddress.getExpirationTime();
            linkAddressArr[i] = linkAddress2;
        }
        return linkAddressArr;
    }

    public static RadioAccessSpecifier convertHalRadioAccessSpecifier(android.hardware.radio.V1_5.RadioAccessSpecifier radioAccessSpecifier) {
        if (radioAccessSpecifier == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        byte discriminator = radioAccessSpecifier.bands.getDiscriminator();
        if (discriminator == 0) {
            arrayList = radioAccessSpecifier.bands.geranBands();
        } else if (discriminator == 1) {
            arrayList = radioAccessSpecifier.bands.utranBands();
        } else if (discriminator == 2) {
            arrayList = radioAccessSpecifier.bands.eutranBands();
        } else if (discriminator == 3) {
            arrayList = radioAccessSpecifier.bands.ngranBands();
        }
        return new RadioAccessSpecifier(convertHalRadioAccessNetworks(radioAccessSpecifier.radioAccessNetwork), arrayList.stream().mapToInt(new RILUtils$$ExternalSyntheticLambda6()).toArray(), radioAccessSpecifier.channels.stream().mapToInt(new RILUtils$$ExternalSyntheticLambda6()).toArray());
    }

    public static RadioAccessSpecifier convertHalRadioAccessSpecifier(android.hardware.radio.network.RadioAccessSpecifier radioAccessSpecifier) {
        int[] iArr = null;
        if (radioAccessSpecifier == null) {
            return null;
        }
        int tag = radioAccessSpecifier.bands.getTag();
        if (tag == 1) {
            iArr = radioAccessSpecifier.bands.getGeranBands();
        } else if (tag == 2) {
            iArr = radioAccessSpecifier.bands.getUtranBands();
        } else if (tag == 3) {
            iArr = radioAccessSpecifier.bands.getEutranBands();
        } else if (tag == 4) {
            iArr = radioAccessSpecifier.bands.getNgranBands();
        }
        return new RadioAccessSpecifier(radioAccessSpecifier.accessNetwork, iArr, radioAccessSpecifier.channels);
    }

    public static android.hardware.radio.V1_1.RadioAccessSpecifier convertToHalRadioAccessSpecifier11(RadioAccessSpecifier radioAccessSpecifier) {
        android.hardware.radio.V1_1.RadioAccessSpecifier radioAccessSpecifier2 = new android.hardware.radio.V1_1.RadioAccessSpecifier();
        radioAccessSpecifier2.radioAccessNetwork = radioAccessSpecifier.getRadioAccessNetwork();
        ArrayList arrayList = new ArrayList();
        if (radioAccessSpecifier.getBands() != null) {
            for (int i : radioAccessSpecifier.getBands()) {
                arrayList.add(Integer.valueOf(i));
            }
        }
        int radioAccessNetwork = radioAccessSpecifier.getRadioAccessNetwork();
        if (radioAccessNetwork == 1) {
            radioAccessSpecifier2.geranBands = arrayList;
        } else if (radioAccessNetwork == 2) {
            radioAccessSpecifier2.utranBands = arrayList;
        } else if (radioAccessNetwork != 3) {
            return null;
        } else {
            radioAccessSpecifier2.eutranBands = arrayList;
        }
        if (radioAccessSpecifier.getChannels() != null) {
            for (int i2 : radioAccessSpecifier.getChannels()) {
                radioAccessSpecifier2.channels.add(Integer.valueOf(i2));
            }
        }
        return radioAccessSpecifier2;
    }

    public static android.hardware.radio.V1_5.RadioAccessSpecifier convertToHalRadioAccessSpecifier15(RadioAccessSpecifier radioAccessSpecifier) {
        android.hardware.radio.V1_5.RadioAccessSpecifier radioAccessSpecifier2 = new android.hardware.radio.V1_5.RadioAccessSpecifier();
        RadioAccessSpecifier.Bands bands = new RadioAccessSpecifier.Bands();
        radioAccessSpecifier2.radioAccessNetwork = convertToHalRadioAccessNetworks(radioAccessSpecifier.getRadioAccessNetwork());
        ArrayList arrayList = new ArrayList();
        if (radioAccessSpecifier.getBands() != null) {
            for (int i : radioAccessSpecifier.getBands()) {
                arrayList.add(Integer.valueOf(i));
            }
        }
        int radioAccessNetwork = radioAccessSpecifier.getRadioAccessNetwork();
        if (radioAccessNetwork == 1) {
            bands.geranBands(arrayList);
        } else if (radioAccessNetwork == 2) {
            bands.utranBands(arrayList);
        } else if (radioAccessNetwork == 3) {
            bands.eutranBands(arrayList);
        } else if (radioAccessNetwork != 6) {
            return null;
        } else {
            bands.ngranBands(arrayList);
        }
        radioAccessSpecifier2.bands = bands;
        if (radioAccessSpecifier.getChannels() != null) {
            for (int i2 : radioAccessSpecifier.getChannels()) {
                radioAccessSpecifier2.channels.add(Integer.valueOf(i2));
            }
        }
        return radioAccessSpecifier2;
    }

    public static android.hardware.radio.network.RadioAccessSpecifier convertToHalRadioAccessSpecifierAidl(android.telephony.RadioAccessSpecifier radioAccessSpecifier) {
        int[] iArr;
        int[] iArr2;
        android.hardware.radio.network.RadioAccessSpecifier radioAccessSpecifier2 = new android.hardware.radio.network.RadioAccessSpecifier();
        RadioAccessSpecifierBands radioAccessSpecifierBands = new RadioAccessSpecifierBands();
        radioAccessSpecifier2.accessNetwork = convertToHalAccessNetworkAidl(radioAccessSpecifier.getRadioAccessNetwork());
        if (radioAccessSpecifier.getBands() != null) {
            iArr = new int[radioAccessSpecifier.getBands().length];
            for (int i = 0; i < radioAccessSpecifier.getBands().length; i++) {
                iArr[i] = radioAccessSpecifier.getBands()[i];
            }
        } else {
            iArr = new int[0];
        }
        int radioAccessNetwork = radioAccessSpecifier.getRadioAccessNetwork();
        if (radioAccessNetwork == 1) {
            radioAccessSpecifierBands.setGeranBands(iArr);
        } else if (radioAccessNetwork == 2) {
            radioAccessSpecifierBands.setUtranBands(iArr);
        } else if (radioAccessNetwork == 3) {
            radioAccessSpecifierBands.setEutranBands(iArr);
        } else if (radioAccessNetwork != 6) {
            return null;
        } else {
            radioAccessSpecifierBands.setNgranBands(iArr);
        }
        radioAccessSpecifier2.bands = radioAccessSpecifierBands;
        if (radioAccessSpecifier.getChannels() != null) {
            iArr2 = new int[radioAccessSpecifier.getChannels().length];
            for (int i2 = 0; i2 < radioAccessSpecifier.getChannels().length; i2++) {
                iArr2[i2] = radioAccessSpecifier.getChannels()[i2];
            }
        } else {
            iArr2 = new int[0];
        }
        radioAccessSpecifier2.channels = iArr2;
        return radioAccessSpecifier2;
    }

    public static String convertToCensoredTerminalResponse(String str) {
        try {
            byte[] hexStringToBytes = IccUtils.hexStringToBytes(str);
            if (hexStringToBytes != null) {
                int i = 0;
                for (ComprehensionTlv comprehensionTlv : ComprehensionTlv.decodeMany(hexStringToBytes, 0)) {
                    if (ComprehensionTlvTag.TEXT_STRING.value() == comprehensionTlv.getTag()) {
                        byte[] copyOfRange = Arrays.copyOfRange(comprehensionTlv.getRawValue(), i, comprehensionTlv.getValueIndex() + comprehensionTlv.getLength());
                        Locale locale = Locale.ROOT;
                        str = str.toLowerCase(locale).replace(IccUtils.bytesToHexString(copyOfRange).toLowerCase(locale), "********");
                    }
                    i = comprehensionTlv.getValueIndex() + comprehensionTlv.getLength();
                }
                return str;
            }
            return str;
        } catch (Exception unused) {
            return null;
        }
    }

    public static SimApdu convertToHalSimApdu(int i, int i2, int i3, int i4, int i5, int i6, String str) {
        SimApdu simApdu = new SimApdu();
        simApdu.sessionId = i;
        simApdu.cla = i2;
        simApdu.instruction = i3;
        simApdu.p1 = i4;
        simApdu.p2 = i5;
        simApdu.p3 = i6;
        simApdu.data = convertNullToEmptyString(str);
        return simApdu;
    }

    public static android.hardware.radio.sim.SimApdu convertToHalSimApduAidl(int i, int i2, int i3, int i4, int i5, int i6, String str, boolean z, HalVersion halVersion) {
        android.hardware.radio.sim.SimApdu simApdu = new android.hardware.radio.sim.SimApdu();
        simApdu.sessionId = i;
        simApdu.cla = i2;
        simApdu.instruction = i3;
        simApdu.p1 = i4;
        simApdu.p2 = i5;
        simApdu.p3 = i6;
        simApdu.data = convertNullToEmptyString(str);
        if (halVersion.greaterOrEqual(RIL.RADIO_HAL_VERSION_2_1)) {
            simApdu.isEs10 = z;
        }
        return simApdu;
    }

    public static ArrayList<Carrier> convertToHalCarrierRestrictionList(List<CarrierIdentifier> list) {
        int i;
        String str;
        ArrayList<Carrier> arrayList = new ArrayList<>();
        for (CarrierIdentifier carrierIdentifier : list) {
            Carrier carrier = new Carrier();
            carrier.mcc = convertNullToEmptyString(carrierIdentifier.getMcc());
            carrier.mnc = convertNullToEmptyString(carrierIdentifier.getMnc());
            if (!TextUtils.isEmpty(carrierIdentifier.getSpn())) {
                str = carrierIdentifier.getSpn();
                i = 1;
            } else if (!TextUtils.isEmpty(carrierIdentifier.getImsi())) {
                str = carrierIdentifier.getImsi();
                i = 2;
            } else if (!TextUtils.isEmpty(carrierIdentifier.getGid1())) {
                str = carrierIdentifier.getGid1();
                i = 3;
            } else if (TextUtils.isEmpty(carrierIdentifier.getGid2())) {
                i = 0;
                str = null;
            } else {
                str = carrierIdentifier.getGid2();
                i = 4;
            }
            carrier.matchType = i;
            carrier.matchData = convertNullToEmptyString(str);
            arrayList.add(carrier);
        }
        return arrayList;
    }

    public static android.hardware.radio.sim.Carrier[] convertToHalCarrierRestrictionListAidl(List<CarrierIdentifier> list) {
        String str;
        int i;
        android.hardware.radio.sim.Carrier[] carrierArr = new android.hardware.radio.sim.Carrier[list.size()];
        for (int i2 = 0; i2 < list.size(); i2++) {
            CarrierIdentifier carrierIdentifier = list.get(i2);
            android.hardware.radio.sim.Carrier carrier = new android.hardware.radio.sim.Carrier();
            carrier.mcc = convertNullToEmptyString(carrierIdentifier.getMcc());
            carrier.mnc = convertNullToEmptyString(carrierIdentifier.getMnc());
            if (!TextUtils.isEmpty(carrierIdentifier.getSpn())) {
                str = carrierIdentifier.getSpn();
                i = 1;
            } else if (!TextUtils.isEmpty(carrierIdentifier.getImsi())) {
                str = carrierIdentifier.getImsi();
                i = 2;
            } else if (!TextUtils.isEmpty(carrierIdentifier.getGid1())) {
                str = carrierIdentifier.getGid1();
                i = 3;
            } else if (TextUtils.isEmpty(carrierIdentifier.getGid2())) {
                str = null;
                i = 0;
            } else {
                str = carrierIdentifier.getGid2();
                i = 4;
            }
            carrier.matchType = i;
            carrier.matchData = convertNullToEmptyString(str);
            carrierArr[i2] = carrier;
        }
        return carrierArr;
    }

    public static Dial convertToHalDial(String str, int i, UUSInfo uUSInfo) {
        Dial dial = new Dial();
        dial.address = convertNullToEmptyString(str);
        dial.clir = i;
        if (uUSInfo != null) {
            UusInfo uusInfo = new UusInfo();
            uusInfo.uusType = uUSInfo.getType();
            uusInfo.uusDcs = uUSInfo.getDcs();
            uusInfo.uusData = new String(uUSInfo.getUserData());
            dial.uusInfo.add(uusInfo);
        }
        return dial;
    }

    public static android.hardware.radio.voice.Dial convertToHalDialAidl(String str, int i, UUSInfo uUSInfo) {
        android.hardware.radio.voice.Dial dial = new android.hardware.radio.voice.Dial();
        dial.address = convertNullToEmptyString(str);
        dial.clir = i;
        if (uUSInfo != null) {
            android.hardware.radio.voice.UusInfo uusInfo = new android.hardware.radio.voice.UusInfo();
            uusInfo.uusType = uUSInfo.getType();
            uusInfo.uusDcs = uUSInfo.getDcs();
            uusInfo.uusData = new String(uUSInfo.getUserData());
            dial.uusInfo = new android.hardware.radio.voice.UusInfo[]{uusInfo};
        } else {
            dial.uusInfo = new android.hardware.radio.voice.UusInfo[0];
        }
        return dial;
    }

    public static SignalThresholdInfo convertToHalSignalThresholdInfo(android.telephony.SignalThresholdInfo signalThresholdInfo) {
        SignalThresholdInfo signalThresholdInfo2 = new SignalThresholdInfo();
        signalThresholdInfo2.signalMeasurement = signalThresholdInfo.getSignalMeasurementType();
        signalThresholdInfo2.hysteresisMs = signalThresholdInfo.getHysteresisMs();
        signalThresholdInfo2.hysteresisDb = signalThresholdInfo.getHysteresisDb();
        signalThresholdInfo2.thresholds = primitiveArrayToArrayList(signalThresholdInfo.getThresholds());
        signalThresholdInfo2.isEnabled = signalThresholdInfo.isEnabled();
        return signalThresholdInfo2;
    }

    public static android.hardware.radio.network.SignalThresholdInfo convertToHalSignalThresholdInfoAidl(android.telephony.SignalThresholdInfo signalThresholdInfo) {
        android.hardware.radio.network.SignalThresholdInfo signalThresholdInfo2 = new android.hardware.radio.network.SignalThresholdInfo();
        signalThresholdInfo2.signalMeasurement = signalThresholdInfo.getSignalMeasurementType();
        signalThresholdInfo2.hysteresisMs = signalThresholdInfo.getHysteresisMs();
        signalThresholdInfo2.hysteresisDb = signalThresholdInfo.getHysteresisDb();
        signalThresholdInfo2.thresholds = signalThresholdInfo.getThresholds();
        signalThresholdInfo2.isEnabled = signalThresholdInfo.isEnabled();
        signalThresholdInfo2.ran = signalThresholdInfo.getRadioAccessNetworkType();
        return signalThresholdInfo2;
    }

    public static ArrayList<HardwareConfig> convertHalHardwareConfigList(ArrayList<android.hardware.radio.V1_0.HardwareConfig> arrayList) {
        HardwareConfig hardwareConfig;
        ArrayList<HardwareConfig> arrayList2 = new ArrayList<>(arrayList.size());
        Iterator<android.hardware.radio.V1_0.HardwareConfig> it = arrayList.iterator();
        while (it.hasNext()) {
            android.hardware.radio.V1_0.HardwareConfig next = it.next();
            int i = next.type;
            if (i == 0) {
                HardwareConfig hardwareConfig2 = new HardwareConfig(i);
                HardwareConfigModem hardwareConfigModem = (HardwareConfigModem) next.modem.get(0);
                hardwareConfig2.assignModem(next.uuid, next.state, hardwareConfigModem.rilModel, hardwareConfigModem.rat, hardwareConfigModem.maxVoice, hardwareConfigModem.maxData, hardwareConfigModem.maxStandby);
                hardwareConfig = hardwareConfig2;
            } else if (i == 1) {
                hardwareConfig = new HardwareConfig(i);
                hardwareConfig.assignSim(next.uuid, next.state, ((HardwareConfigSim) next.sim.get(0)).modemUuid);
            } else {
                throw new RuntimeException("RIL_REQUEST_GET_HARDWARE_CONFIG invalid hardware type:" + i);
            }
            arrayList2.add(hardwareConfig);
        }
        return arrayList2;
    }

    public static ArrayList<HardwareConfig> convertHalHardwareConfigList(android.hardware.radio.modem.HardwareConfig[] hardwareConfigArr) {
        HardwareConfig hardwareConfig;
        ArrayList<HardwareConfig> arrayList = new ArrayList<>(hardwareConfigArr.length);
        for (android.hardware.radio.modem.HardwareConfig hardwareConfig2 : hardwareConfigArr) {
            int i = hardwareConfig2.type;
            if (i == 0) {
                HardwareConfig hardwareConfig3 = new HardwareConfig(i);
                android.hardware.radio.modem.HardwareConfigModem hardwareConfigModem = hardwareConfig2.modem[0];
                hardwareConfig3.assignModem(hardwareConfig2.uuid, hardwareConfig2.state, hardwareConfigModem.rilModel, hardwareConfigModem.rat, hardwareConfigModem.maxVoiceCalls, hardwareConfigModem.maxDataCalls, hardwareConfigModem.maxStandby);
                hardwareConfig = hardwareConfig3;
            } else if (i == 1) {
                hardwareConfig = new HardwareConfig(i);
                hardwareConfig.assignSim(hardwareConfig2.uuid, hardwareConfig2.state, hardwareConfig2.sim[0].modemUuid);
            } else {
                throw new RuntimeException("RIL_REQUEST_GET_HARDWARE_CONFIG invalid hardware type:" + i);
            }
            arrayList.add(hardwareConfig);
        }
        return arrayList;
    }

    public static RadioCapability convertHalRadioCapability(android.hardware.radio.V1_0.RadioCapability radioCapability, RIL ril) {
        int i = radioCapability.session;
        int i2 = radioCapability.phase;
        int convertHalNetworkTypeBitMask = convertHalNetworkTypeBitMask(radioCapability.raf);
        String str = radioCapability.logicalModemUuid;
        int i3 = radioCapability.status;
        ril.riljLog("convertHalRadioCapability: session=" + i + ", phase=" + i2 + ", rat=" + convertHalNetworkTypeBitMask + ", logicModemUuid=" + str + ", status=" + i3 + ", rcRil.raf=" + radioCapability.raf);
        return new RadioCapability(ril.mPhoneId.intValue(), i, i2, convertHalNetworkTypeBitMask, str, i3);
    }

    public static RadioCapability convertHalRadioCapability(android.hardware.radio.modem.RadioCapability radioCapability, RIL ril) {
        int i = radioCapability.session;
        int i2 = radioCapability.phase;
        int convertHalNetworkTypeBitMask = convertHalNetworkTypeBitMask(radioCapability.raf);
        String str = radioCapability.logicalModemUuid;
        int i3 = radioCapability.status;
        ril.riljLog("convertHalRadioCapability: session=" + i + ", phase=" + i2 + ", rat=" + convertHalNetworkTypeBitMask + ", logicModemUuid=" + str + ", status=" + i3 + ", rcRil.raf=" + radioCapability.raf);
        return new RadioCapability(ril.mPhoneId.intValue(), i, i2, convertHalNetworkTypeBitMask, str, i3);
    }

    public static List<LinkCapacityEstimate> convertHalLceData(Object obj) {
        int i;
        int i2;
        ArrayList arrayList = new ArrayList();
        if (obj == null) {
            return arrayList;
        }
        if (obj instanceof LceDataInfo) {
            arrayList.add(new LinkCapacityEstimate(2, ((LceDataInfo) obj).lastHopCapacityKbps, -1));
        } else if (obj instanceof android.hardware.radio.V1_2.LinkCapacityEstimate) {
            android.hardware.radio.V1_2.LinkCapacityEstimate linkCapacityEstimate = (android.hardware.radio.V1_2.LinkCapacityEstimate) obj;
            arrayList.add(new LinkCapacityEstimate(2, linkCapacityEstimate.downlinkCapacityKbps, linkCapacityEstimate.uplinkCapacityKbps));
        } else if (obj instanceof android.hardware.radio.V1_6.LinkCapacityEstimate) {
            android.hardware.radio.V1_6.LinkCapacityEstimate linkCapacityEstimate2 = (android.hardware.radio.V1_6.LinkCapacityEstimate) obj;
            int i3 = linkCapacityEstimate2.downlinkCapacityKbps;
            int i4 = linkCapacityEstimate2.uplinkCapacityKbps;
            if (i3 != -1 && (i2 = linkCapacityEstimate2.secondaryDownlinkCapacityKbps) != -1) {
                i3 -= i2;
            }
            if (i4 != -1 && (i = linkCapacityEstimate2.secondaryUplinkCapacityKbps) != -1) {
                i4 -= i;
            }
            arrayList.add(new LinkCapacityEstimate(0, i3, i4));
            arrayList.add(new LinkCapacityEstimate(1, linkCapacityEstimate2.secondaryDownlinkCapacityKbps, linkCapacityEstimate2.secondaryUplinkCapacityKbps));
        }
        return arrayList;
    }

    public static List<LinkCapacityEstimate> convertHalLceData(android.hardware.radio.network.LceDataInfo lceDataInfo) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new LinkCapacityEstimate(2, lceDataInfo.lastHopCapacityKbps, -1));
        return arrayList;
    }

    public static List<LinkCapacityEstimate> convertHalLceData(android.hardware.radio.network.LinkCapacityEstimate linkCapacityEstimate) {
        int i;
        int i2;
        ArrayList arrayList = new ArrayList();
        int i3 = linkCapacityEstimate.downlinkCapacityKbps;
        int i4 = linkCapacityEstimate.uplinkCapacityKbps;
        if (i3 != -1 && (i2 = linkCapacityEstimate.secondaryDownlinkCapacityKbps) != -1) {
            i3 -= i2;
        }
        if (i4 != -1 && (i = linkCapacityEstimate.secondaryUplinkCapacityKbps) != -1) {
            i4 -= i;
        }
        arrayList.add(new LinkCapacityEstimate(0, i3, i4));
        arrayList.add(new LinkCapacityEstimate(1, linkCapacityEstimate.secondaryDownlinkCapacityKbps, linkCapacityEstimate.secondaryUplinkCapacityKbps));
        return arrayList;
    }

    public static ArrayList<CellInfo> convertHalCellInfoList(ArrayList<Object> arrayList) {
        ArrayList<CellInfo> arrayList2 = new ArrayList<>(arrayList.size());
        if (arrayList.isEmpty()) {
            return arrayList2;
        }
        long elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        Iterator<Object> it = arrayList.iterator();
        while (it.hasNext()) {
            arrayList2.add(convertHalCellInfo(it.next(), elapsedRealtimeNanos));
        }
        return arrayList2;
    }

    public static ArrayList<CellInfo> convertHalCellInfoList(android.hardware.radio.network.CellInfo[] cellInfoArr) {
        ArrayList<CellInfo> arrayList = new ArrayList<>(cellInfoArr.length);
        if (cellInfoArr.length == 0) {
            return arrayList;
        }
        long elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        for (android.hardware.radio.network.CellInfo cellInfo : cellInfoArr) {
            arrayList.add(convertHalCellInfo(cellInfo, elapsedRealtimeNanos));
        }
        return arrayList;
    }

    private static CellInfo convertHalCellInfo(Object obj, long j) {
        int i;
        boolean z;
        CellIdentityGsm convertHalCellIdentityGsm;
        CellSignalStrengthGsm convertHalGsmSignalStrength;
        CellIdentityWcdma convertHalCellIdentityWcdma;
        CellSignalStrengthWcdma convertHalWcdmaSignalStrength;
        CellSignalStrengthTdscdma convertHalTdscdmaSignalStrength;
        CellSignalStrengthLte cellSignalStrengthLte;
        CellSignalStrengthWcdma cellSignalStrengthWcdma;
        CellIdentityGsm cellIdentityGsm;
        CellIdentityNr cellIdentityNr;
        CellIdentityCdma cellIdentityCdma;
        CellIdentityLte cellIdentityLte;
        CellIdentityWcdma cellIdentityWcdma;
        CellConfigLte cellConfigLte;
        CellIdentityTdscdma cellIdentityTdscdma;
        CellConfigLte cellConfigLte2;
        CellIdentityNr convertHalCellIdentityNr;
        CellConfigLte convertHalNrSignalStrength;
        CellIdentityCdma convertHalCellIdentityCdma;
        CellConfigLte convertHalCdmaSignalStrength;
        CellConfigLte cellConfigLte3;
        CellSignalStrengthGsm cellSignalStrengthGsm;
        CellIdentityGsm cellIdentityGsm2;
        CellIdentityWcdma cellIdentityWcdma2;
        boolean z2;
        CellIdentityNr cellIdentityNr2;
        int i2;
        CellIdentityLte cellIdentityLte2;
        char c;
        CellSignalStrengthWcdma cellSignalStrengthWcdma2;
        CellIdentityCdma cellIdentityCdma2;
        CellConfigLte cellConfigLte4;
        CellSignalStrengthTdscdma cellSignalStrengthTdscdma;
        CellSignalStrengthGsm convertHalGsmSignalStrength2;
        CellConfigLte cellConfigLte5;
        CellIdentityNr cellIdentityNr3;
        CellSignalStrengthTdscdma cellSignalStrengthTdscdma2;
        CellIdentityCdma cellIdentityCdma3;
        CellIdentityLte cellIdentityLte3;
        CellIdentityWcdma cellIdentityWcdma3;
        CellIdentityGsm cellIdentityGsm3;
        CellConfigLte cellConfigLte6;
        CellSignalStrengthWcdma cellSignalStrengthWcdma3;
        CellSignalStrengthGsm convertHalGsmSignalStrength3;
        CellSignalStrengthLte cellSignalStrengthLte2;
        CellSignalStrengthWcdma cellSignalStrengthWcdma4;
        CellSignalStrengthTdscdma cellSignalStrengthTdscdma3;
        CellIdentityCdma cellIdentityCdma4;
        CellIdentityLte cellIdentityLte4;
        CellIdentityWcdma cellIdentityWcdma4;
        CellIdentityTdscdma cellIdentityTdscdma2;
        CellConfigLte cellConfigLte7;
        CellIdentityGsm cellIdentityGsm4;
        CellConfigLte cellConfigLte8;
        CellSignalStrengthGsm convertHalGsmSignalStrength4;
        CellSignalStrengthLte cellSignalStrengthLte3;
        CellSignalStrengthTdscdma cellSignalStrengthTdscdma4;
        CellIdentityLte cellIdentityLte5;
        CellIdentityWcdma cellIdentityWcdma5;
        CellIdentityTdscdma cellIdentityTdscdma3;
        CellConfigLte cellConfigLte9;
        CellIdentityGsm cellIdentityGsm5;
        CellConfigLte cellConfigLte10;
        CellIdentityCdma cellIdentityCdma5;
        if (obj == null) {
            return null;
        }
        char c2 = 5;
        char c3 = 4;
        if (obj instanceof android.hardware.radio.V1_0.CellInfo) {
            android.hardware.radio.V1_0.CellInfo cellInfo = (android.hardware.radio.V1_0.CellInfo) obj;
            boolean z3 = cellInfo.registered;
            int i3 = cellInfo.cellInfoType;
            if (i3 == 1) {
                CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo.gsm.get(0);
                CellIdentityGsm convertHalCellIdentityGsm2 = convertHalCellIdentityGsm(cellInfoGsm.cellIdentityGsm);
                convertHalGsmSignalStrength4 = convertHalGsmSignalStrength(cellInfoGsm.signalStrengthGsm);
                cellSignalStrengthLte3 = null;
                cellSignalStrengthWcdma2 = null;
                cellSignalStrengthTdscdma4 = null;
                cellIdentityLte5 = null;
                cellIdentityWcdma5 = null;
                cellIdentityTdscdma3 = null;
                cellConfigLte9 = null;
                cellIdentityGsm5 = convertHalCellIdentityGsm2;
                c = 1;
                cellConfigLte10 = null;
                cellIdentityCdma5 = null;
            } else if (i3 == 2) {
                CellInfoCdma cellInfoCdma = (CellInfoCdma) cellInfo.cdma.get(0);
                cellSignalStrengthLte3 = null;
                cellSignalStrengthWcdma2 = null;
                cellSignalStrengthTdscdma4 = null;
                cellIdentityLte5 = null;
                cellIdentityWcdma5 = null;
                cellIdentityTdscdma3 = null;
                cellConfigLte9 = null;
                cellIdentityCdma5 = convertHalCellIdentityCdma(cellInfoCdma.cellIdentityCdma);
                c = 2;
                cellConfigLte10 = convertHalCdmaSignalStrength(cellInfoCdma.signalStrengthCdma, cellInfoCdma.signalStrengthEvdo);
                convertHalGsmSignalStrength4 = null;
                cellIdentityGsm5 = null;
            } else if (i3 == 3) {
                CellInfoLte cellInfoLte = (CellInfoLte) cellInfo.lte.get(0);
                CellIdentityLte convertHalCellIdentityLte = convertHalCellIdentityLte(cellInfoLte.cellIdentityLte);
                CellSignalStrengthLte convertHalLteSignalStrength = convertHalLteSignalStrength(cellInfoLte.signalStrengthLte);
                cellSignalStrengthWcdma2 = null;
                cellIdentityGsm5 = null;
                cellIdentityCdma5 = null;
                cellIdentityWcdma5 = null;
                cellIdentityTdscdma3 = null;
                cellIdentityLte5 = convertHalCellIdentityLte;
                cellConfigLte9 = new CellConfigLte();
                c = 3;
                cellSignalStrengthLte3 = convertHalLteSignalStrength;
                convertHalGsmSignalStrength4 = null;
                cellConfigLte10 = null;
                cellSignalStrengthTdscdma4 = null;
            } else if (i3 == 4) {
                CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo.wcdma.get(0);
                cellSignalStrengthLte3 = null;
                cellSignalStrengthTdscdma4 = null;
                cellIdentityGsm5 = null;
                cellIdentityCdma5 = null;
                cellIdentityLte5 = null;
                cellIdentityTdscdma3 = null;
                cellConfigLte9 = null;
                cellIdentityWcdma5 = convertHalCellIdentityWcdma(cellInfoWcdma.cellIdentityWcdma);
                c = 4;
                cellSignalStrengthWcdma2 = convertHalWcdmaSignalStrength(cellInfoWcdma.signalStrengthWcdma);
                convertHalGsmSignalStrength4 = null;
                cellConfigLte10 = null;
            } else if (i3 != 5) {
                return null;
            } else {
                CellInfoTdscdma cellInfoTdscdma = (CellInfoTdscdma) cellInfo.tdscdma.get(0);
                CellIdentityTdscdma convertHalCellIdentityTdscdma = convertHalCellIdentityTdscdma(cellInfoTdscdma.cellIdentityTdscdma);
                cellSignalStrengthTdscdma4 = convertHalTdscdmaSignalStrength(cellInfoTdscdma.signalStrengthTdscdma);
                convertHalGsmSignalStrength4 = null;
                cellSignalStrengthWcdma2 = null;
                cellIdentityGsm5 = null;
                cellIdentityCdma5 = null;
                cellIdentityLte5 = null;
                cellIdentityWcdma5 = null;
                cellConfigLte9 = null;
                cellIdentityTdscdma3 = convertHalCellIdentityTdscdma;
                c = 5;
                cellConfigLte10 = null;
                cellSignalStrengthLte3 = null;
            }
            cellSignalStrengthGsm = convertHalGsmSignalStrength4;
            cellIdentityGsm2 = cellIdentityGsm5;
            cellIdentityCdma2 = cellIdentityCdma5;
            cellIdentityWcdma2 = cellIdentityWcdma5;
            cellIdentityTdscdma = cellIdentityTdscdma3;
            cellConfigLte = cellConfigLte9;
            i2 = Integer.MAX_VALUE;
            z2 = z3;
            cellConfigLte4 = cellConfigLte10;
            cellSignalStrengthTdscdma = cellSignalStrengthTdscdma4;
            cellIdentityLte2 = cellIdentityLte5;
            cellIdentityNr2 = null;
            cellSignalStrengthLte = cellSignalStrengthLte3;
            cellConfigLte3 = null;
        } else if (obj instanceof android.hardware.radio.V1_2.CellInfo) {
            android.hardware.radio.V1_2.CellInfo cellInfo2 = (android.hardware.radio.V1_2.CellInfo) obj;
            int i4 = cellInfo2.connectionStatus;
            boolean z4 = cellInfo2.registered;
            int i5 = cellInfo2.cellInfoType;
            if (i5 != 1) {
                if (i5 == 2) {
                    android.hardware.radio.V1_2.CellInfoCdma cellInfoCdma2 = (android.hardware.radio.V1_2.CellInfoCdma) cellInfo2.cdma.get(0);
                    cellSignalStrengthLte2 = null;
                    cellSignalStrengthWcdma4 = null;
                    cellIdentityGsm4 = null;
                    cellIdentityLte4 = null;
                    cellIdentityWcdma4 = null;
                    cellIdentityTdscdma2 = null;
                    cellConfigLte7 = null;
                    cellIdentityCdma4 = convertHalCellIdentityCdma(cellInfoCdma2.cellIdentityCdma);
                    c2 = 2;
                    cellConfigLte8 = convertHalCdmaSignalStrength(cellInfoCdma2.signalStrengthCdma, cellInfoCdma2.signalStrengthEvdo);
                    convertHalGsmSignalStrength3 = null;
                    cellSignalStrengthTdscdma3 = null;
                } else if (i5 == 3) {
                    android.hardware.radio.V1_2.CellInfoLte cellInfoLte2 = (android.hardware.radio.V1_2.CellInfoLte) cellInfo2.lte.get(0);
                    CellIdentityLte convertHalCellIdentityLte2 = convertHalCellIdentityLte(cellInfoLte2.cellIdentityLte);
                    cellSignalStrengthLte2 = convertHalLteSignalStrength(cellInfoLte2.signalStrengthLte);
                    convertHalGsmSignalStrength3 = null;
                    cellSignalStrengthTdscdma3 = null;
                    cellIdentityGsm4 = null;
                    cellIdentityCdma4 = null;
                    cellIdentityWcdma4 = null;
                    cellIdentityTdscdma2 = null;
                    cellIdentityLte4 = convertHalCellIdentityLte2;
                    cellConfigLte7 = new CellConfigLte();
                    c2 = 3;
                    cellConfigLte8 = null;
                    cellSignalStrengthWcdma4 = null;
                } else if (i5 == 4) {
                    android.hardware.radio.V1_2.CellInfoWcdma cellInfoWcdma2 = (android.hardware.radio.V1_2.CellInfoWcdma) cellInfo2.wcdma.get(0);
                    CellIdentityWcdma convertHalCellIdentityWcdma2 = convertHalCellIdentityWcdma(cellInfoWcdma2.cellIdentityWcdma);
                    cellSignalStrengthWcdma4 = convertHalWcdmaSignalStrength(cellInfoWcdma2.signalStrengthWcdma);
                    convertHalGsmSignalStrength3 = null;
                    cellSignalStrengthTdscdma3 = null;
                    cellIdentityGsm4 = null;
                    cellIdentityCdma4 = null;
                    cellIdentityLte4 = null;
                    cellIdentityTdscdma2 = null;
                    cellConfigLte7 = null;
                    cellIdentityWcdma4 = convertHalCellIdentityWcdma2;
                    c2 = 4;
                    cellConfigLte8 = null;
                    cellSignalStrengthLte2 = null;
                } else if (i5 != 5) {
                    return null;
                } else {
                    android.hardware.radio.V1_2.CellInfoTdscdma cellInfoTdscdma2 = (android.hardware.radio.V1_2.CellInfoTdscdma) cellInfo2.tdscdma.get(0);
                    CellIdentityTdscdma convertHalCellIdentityTdscdma2 = convertHalCellIdentityTdscdma(cellInfoTdscdma2.cellIdentityTdscdma);
                    cellSignalStrengthTdscdma3 = convertHalTdscdmaSignalStrength(cellInfoTdscdma2.signalStrengthTdscdma);
                    convertHalGsmSignalStrength3 = null;
                    cellSignalStrengthLte2 = null;
                    cellSignalStrengthWcdma4 = null;
                    cellIdentityGsm4 = null;
                    cellIdentityCdma4 = null;
                    cellIdentityLte4 = null;
                    cellIdentityWcdma4 = null;
                    cellConfigLte7 = null;
                    cellIdentityTdscdma2 = convertHalCellIdentityTdscdma2;
                }
                cellSignalStrengthGsm = convertHalGsmSignalStrength3;
                cellIdentityGsm2 = cellIdentityGsm4;
                cellIdentityWcdma2 = cellIdentityWcdma4;
                cellIdentityTdscdma = cellIdentityTdscdma2;
                cellConfigLte = cellConfigLte7;
                z2 = z4;
                cellConfigLte4 = cellConfigLte8;
                c = c2;
                cellSignalStrengthTdscdma = cellSignalStrengthTdscdma3;
                cellIdentityCdma2 = cellIdentityCdma4;
                cellIdentityNr2 = null;
                cellConfigLte3 = null;
                CellSignalStrengthWcdma cellSignalStrengthWcdma5 = cellSignalStrengthWcdma4;
                cellSignalStrengthLte = cellSignalStrengthLte2;
                cellSignalStrengthWcdma2 = cellSignalStrengthWcdma5;
                cellIdentityLte2 = cellIdentityLte4;
                i2 = i4;
            } else {
                android.hardware.radio.V1_2.CellInfoGsm cellInfoGsm2 = (android.hardware.radio.V1_2.CellInfoGsm) cellInfo2.gsm.get(0);
                CellIdentityGsm convertHalCellIdentityGsm3 = convertHalCellIdentityGsm(cellInfoGsm2.cellIdentityGsm);
                convertHalGsmSignalStrength3 = convertHalGsmSignalStrength(cellInfoGsm2.signalStrengthGsm);
                cellSignalStrengthLte2 = null;
                cellSignalStrengthWcdma4 = null;
                cellSignalStrengthTdscdma3 = null;
                cellIdentityCdma4 = null;
                cellIdentityLte4 = null;
                cellIdentityWcdma4 = null;
                cellIdentityTdscdma2 = null;
                cellConfigLte7 = null;
                c2 = 1;
                cellIdentityGsm4 = convertHalCellIdentityGsm3;
            }
            cellConfigLte8 = cellConfigLte7;
            cellSignalStrengthGsm = convertHalGsmSignalStrength3;
            cellIdentityGsm2 = cellIdentityGsm4;
            cellIdentityWcdma2 = cellIdentityWcdma4;
            cellIdentityTdscdma = cellIdentityTdscdma2;
            cellConfigLte = cellConfigLte7;
            z2 = z4;
            cellConfigLte4 = cellConfigLte8;
            c = c2;
            cellSignalStrengthTdscdma = cellSignalStrengthTdscdma3;
            cellIdentityCdma2 = cellIdentityCdma4;
            cellIdentityNr2 = null;
            cellConfigLte3 = null;
            CellSignalStrengthWcdma cellSignalStrengthWcdma52 = cellSignalStrengthWcdma4;
            cellSignalStrengthLte = cellSignalStrengthLte2;
            cellSignalStrengthWcdma2 = cellSignalStrengthWcdma52;
            cellIdentityLte2 = cellIdentityLte4;
            i2 = i4;
        } else if (obj instanceof android.hardware.radio.V1_4.CellInfo) {
            android.hardware.radio.V1_4.CellInfo cellInfo3 = (android.hardware.radio.V1_4.CellInfo) obj;
            int i6 = cellInfo3.connectionStatus;
            boolean z5 = cellInfo3.isRegistered;
            byte discriminator = cellInfo3.info.getDiscriminator();
            if (discriminator == 0) {
                android.hardware.radio.V1_2.CellInfoGsm gsm = cellInfo3.info.gsm();
                CellIdentityGsm convertHalCellIdentityGsm4 = convertHalCellIdentityGsm(gsm.cellIdentityGsm);
                convertHalGsmSignalStrength2 = convertHalGsmSignalStrength(gsm.signalStrengthGsm);
                cellConfigLte5 = null;
                cellSignalStrengthLte = null;
                cellIdentityNr3 = null;
                cellSignalStrengthTdscdma2 = null;
                cellIdentityCdma3 = null;
                cellIdentityLte3 = null;
                cellIdentityWcdma3 = null;
                cellIdentityTdscdma = null;
                cellConfigLte = null;
                cellIdentityGsm3 = convertHalCellIdentityGsm4;
                c3 = 1;
                cellConfigLte6 = null;
                cellSignalStrengthWcdma3 = null;
            } else if (discriminator == 1) {
                android.hardware.radio.V1_2.CellInfoCdma cdma = cellInfo3.info.cdma();
                CellIdentityCdma convertHalCellIdentityCdma2 = convertHalCellIdentityCdma(cdma.cellIdentityCdma);
                cellConfigLte5 = convertHalCdmaSignalStrength(cdma.signalStrengthCdma, cdma.signalStrengthEvdo);
                convertHalGsmSignalStrength2 = null;
                cellSignalStrengthLte = null;
                cellSignalStrengthWcdma3 = null;
                cellSignalStrengthTdscdma2 = null;
                cellIdentityGsm3 = null;
                cellIdentityLte3 = null;
                cellIdentityWcdma3 = null;
                cellIdentityTdscdma = null;
                cellConfigLte = null;
                cellIdentityCdma3 = convertHalCellIdentityCdma2;
                c3 = 2;
                cellConfigLte6 = null;
                cellIdentityNr3 = null;
            } else if (discriminator != 2) {
                if (discriminator == 3) {
                    android.hardware.radio.V1_2.CellInfoTdscdma tdscdma = cellInfo3.info.tdscdma();
                    CellIdentityTdscdma convertHalCellIdentityTdscdma3 = convertHalCellIdentityTdscdma(tdscdma.cellIdentityTdscdma);
                    cellSignalStrengthTdscdma2 = convertHalTdscdmaSignalStrength(tdscdma.signalStrengthTdscdma);
                    convertHalGsmSignalStrength2 = null;
                    cellSignalStrengthLte = null;
                    cellIdentityNr3 = null;
                    cellSignalStrengthWcdma3 = null;
                    cellIdentityGsm3 = null;
                    cellIdentityCdma3 = null;
                    cellIdentityLte3 = null;
                    cellIdentityWcdma3 = null;
                    cellConfigLte = null;
                    cellIdentityTdscdma = convertHalCellIdentityTdscdma3;
                    c3 = 5;
                    cellConfigLte6 = null;
                } else if (discriminator == 4) {
                    android.hardware.radio.V1_4.CellInfoLte lte = cellInfo3.info.lte();
                    CellIdentityLte convertHalCellIdentityLte3 = convertHalCellIdentityLte(lte.base.cellIdentityLte);
                    CellSignalStrengthLte convertHalLteSignalStrength2 = convertHalLteSignalStrength(lte.base.signalStrengthLte);
                    CellConfigLte cellConfigLte11 = new CellConfigLte(lte.cellConfig.isEndcAvailable);
                    convertHalGsmSignalStrength2 = null;
                    cellIdentityNr3 = null;
                    cellSignalStrengthWcdma3 = null;
                    cellSignalStrengthTdscdma2 = null;
                    cellIdentityGsm3 = null;
                    cellIdentityCdma3 = null;
                    cellIdentityWcdma3 = null;
                    cellIdentityTdscdma = null;
                    cellIdentityLte3 = convertHalCellIdentityLte3;
                    cellConfigLte = cellConfigLte11;
                    c3 = 3;
                    cellConfigLte6 = null;
                    cellSignalStrengthLte = convertHalLteSignalStrength2;
                } else if (discriminator != 5) {
                    return null;
                } else {
                    CellInfoNr nr = cellInfo3.info.nr();
                    cellSignalStrengthLte = null;
                    cellSignalStrengthWcdma3 = null;
                    cellSignalStrengthTdscdma2 = null;
                    cellIdentityGsm3 = null;
                    cellIdentityCdma3 = null;
                    cellIdentityLte3 = null;
                    cellIdentityWcdma3 = null;
                    cellIdentityTdscdma = null;
                    cellConfigLte = null;
                    c3 = 6;
                    cellIdentityNr3 = convertHalCellIdentityNr(nr.cellidentity);
                    cellConfigLte6 = convertHalNrSignalStrength(nr.signalStrength);
                    convertHalGsmSignalStrength2 = null;
                    cellConfigLte5 = null;
                }
                cellConfigLte5 = cellConfigLte6;
            } else {
                android.hardware.radio.V1_2.CellInfoWcdma wcdma = cellInfo3.info.wcdma();
                CellIdentityWcdma convertHalCellIdentityWcdma3 = convertHalCellIdentityWcdma(wcdma.cellIdentityWcdma);
                cellSignalStrengthWcdma3 = convertHalWcdmaSignalStrength(wcdma.signalStrengthWcdma);
                convertHalGsmSignalStrength2 = null;
                cellConfigLte5 = null;
                cellSignalStrengthLte = null;
                cellIdentityNr3 = null;
                cellSignalStrengthTdscdma2 = null;
                cellIdentityGsm3 = null;
                cellIdentityCdma3 = null;
                cellIdentityLte3 = null;
                cellIdentityTdscdma = null;
                cellConfigLte = null;
                cellIdentityWcdma3 = convertHalCellIdentityWcdma3;
                cellConfigLte6 = null;
            }
            cellSignalStrengthGsm = convertHalGsmSignalStrength2;
            cellIdentityGsm2 = cellIdentityGsm3;
            cellIdentityWcdma2 = cellIdentityWcdma3;
            i2 = i6;
            cellIdentityLte2 = cellIdentityLte3;
            c = c3;
            cellSignalStrengthWcdma2 = cellSignalStrengthWcdma3;
            cellConfigLte4 = cellConfigLte5;
            cellConfigLte3 = cellConfigLte6;
            cellSignalStrengthTdscdma = cellSignalStrengthTdscdma2;
            CellIdentityCdma cellIdentityCdma6 = cellIdentityCdma3;
            z2 = z5;
            cellIdentityNr2 = cellIdentityNr3;
            cellIdentityCdma2 = cellIdentityCdma6;
        } else {
            if (obj instanceof android.hardware.radio.V1_5.CellInfo) {
                android.hardware.radio.V1_5.CellInfo cellInfo4 = (android.hardware.radio.V1_5.CellInfo) obj;
                i = cellInfo4.connectionStatus;
                z = cellInfo4.registered;
                byte discriminator2 = cellInfo4.ratSpecificInfo.getDiscriminator();
                if (discriminator2 == 0) {
                    android.hardware.radio.V1_5.CellInfoGsm gsm2 = cellInfo4.ratSpecificInfo.gsm();
                    convertHalCellIdentityGsm = convertHalCellIdentityGsm(gsm2.cellIdentityGsm);
                    convertHalGsmSignalStrength = convertHalGsmSignalStrength(gsm2.signalStrengthGsm);
                    cellConfigLte3 = null;
                    cellSignalStrengthLte = null;
                    cellSignalStrengthWcdma = null;
                    cellIdentityNr = null;
                    cellIdentityCdma = null;
                    cellIdentityLte = null;
                    cellIdentityWcdma = null;
                    cellIdentityTdscdma = null;
                    cellConfigLte = null;
                    cellIdentityGsm = convertHalCellIdentityGsm;
                    c3 = 1;
                    cellConfigLte2 = null;
                    convertHalTdscdmaSignalStrength = null;
                } else if (discriminator2 != 1) {
                    if (discriminator2 == 2) {
                        android.hardware.radio.V1_5.CellInfoTdscdma tdscdma2 = cellInfo4.ratSpecificInfo.tdscdma();
                        CellIdentityTdscdma convertHalCellIdentityTdscdma4 = convertHalCellIdentityTdscdma(tdscdma2.cellIdentityTdscdma);
                        convertHalTdscdmaSignalStrength = convertHalTdscdmaSignalStrength(tdscdma2.signalStrengthTdscdma);
                        convertHalGsmSignalStrength = null;
                        cellSignalStrengthLte = null;
                        cellSignalStrengthWcdma = null;
                        cellIdentityGsm = null;
                        cellIdentityNr = null;
                        cellIdentityCdma = null;
                        cellIdentityLte = null;
                        cellIdentityWcdma = null;
                        cellConfigLte = null;
                        cellIdentityTdscdma = convertHalCellIdentityTdscdma4;
                        c3 = 5;
                        cellConfigLte2 = null;
                    } else if (discriminator2 == 3) {
                        android.hardware.radio.V1_5.CellInfoLte lte2 = cellInfo4.ratSpecificInfo.lte();
                        CellIdentityLte convertHalCellIdentityLte4 = convertHalCellIdentityLte(lte2.cellIdentityLte);
                        CellSignalStrengthLte convertHalLteSignalStrength3 = convertHalLteSignalStrength(lte2.signalStrengthLte);
                        cellSignalStrengthWcdma = null;
                        convertHalTdscdmaSignalStrength = null;
                        cellIdentityGsm = null;
                        cellIdentityNr = null;
                        cellIdentityCdma = null;
                        cellIdentityWcdma = null;
                        cellIdentityTdscdma = null;
                        cellIdentityLte = convertHalCellIdentityLte4;
                        cellConfigLte = new CellConfigLte();
                        c3 = 3;
                        cellSignalStrengthLte = convertHalLteSignalStrength3;
                        convertHalGsmSignalStrength = null;
                        cellConfigLte2 = null;
                    } else if (discriminator2 == 4) {
                        android.hardware.radio.V1_5.CellInfoNr nr2 = cellInfo4.ratSpecificInfo.nr();
                        convertHalCellIdentityNr = convertHalCellIdentityNr(nr2.cellIdentityNr);
                        convertHalNrSignalStrength = convertHalNrSignalStrength(nr2.signalStrengthNr);
                        cellSignalStrengthLte = null;
                        cellSignalStrengthWcdma = null;
                        convertHalTdscdmaSignalStrength = null;
                        cellIdentityGsm = null;
                        cellIdentityCdma = null;
                        cellIdentityLte = null;
                        cellIdentityWcdma = null;
                        cellIdentityTdscdma = null;
                        cellConfigLte = null;
                        c3 = 6;
                        cellIdentityNr = convertHalCellIdentityNr;
                        cellConfigLte3 = convertHalNrSignalStrength;
                        convertHalGsmSignalStrength = null;
                        cellConfigLte2 = null;
                    } else if (discriminator2 != 5) {
                        return null;
                    } else {
                        android.hardware.radio.V1_2.CellInfoCdma cdma2 = cellInfo4.ratSpecificInfo.cdma();
                        convertHalCellIdentityCdma = convertHalCellIdentityCdma(cdma2.cellIdentityCdma);
                        convertHalCdmaSignalStrength = convertHalCdmaSignalStrength(cdma2.signalStrengthCdma, cdma2.signalStrengthEvdo);
                        cellConfigLte3 = null;
                        cellSignalStrengthLte = null;
                        convertHalTdscdmaSignalStrength = null;
                        cellIdentityGsm = null;
                        cellIdentityNr = null;
                        cellIdentityLte = null;
                        cellIdentityWcdma = null;
                        cellIdentityTdscdma = null;
                        cellConfigLte = null;
                        cellIdentityCdma = convertHalCellIdentityCdma;
                        c3 = 2;
                        cellConfigLte2 = convertHalCdmaSignalStrength;
                        convertHalGsmSignalStrength = null;
                        cellSignalStrengthWcdma = null;
                    }
                    cellConfigLte3 = cellConfigLte2;
                } else {
                    android.hardware.radio.V1_5.CellInfoWcdma wcdma2 = cellInfo4.ratSpecificInfo.wcdma();
                    convertHalCellIdentityWcdma = convertHalCellIdentityWcdma(wcdma2.cellIdentityWcdma);
                    convertHalWcdmaSignalStrength = convertHalWcdmaSignalStrength(wcdma2.signalStrengthWcdma);
                    cellSignalStrengthWcdma = convertHalWcdmaSignalStrength;
                    convertHalGsmSignalStrength = null;
                    cellConfigLte3 = null;
                    cellSignalStrengthLte = null;
                    convertHalTdscdmaSignalStrength = null;
                    cellIdentityGsm = null;
                    cellIdentityNr = null;
                    cellIdentityCdma = null;
                    cellIdentityLte = null;
                    cellIdentityTdscdma = null;
                    cellConfigLte = null;
                    cellIdentityWcdma = convertHalCellIdentityWcdma;
                    cellConfigLte2 = null;
                }
            } else if (!(obj instanceof android.hardware.radio.V1_6.CellInfo)) {
                return null;
            } else {
                android.hardware.radio.V1_6.CellInfo cellInfo5 = (android.hardware.radio.V1_6.CellInfo) obj;
                i = cellInfo5.connectionStatus;
                z = cellInfo5.registered;
                byte discriminator3 = cellInfo5.ratSpecificInfo.getDiscriminator();
                if (discriminator3 == 0) {
                    android.hardware.radio.V1_5.CellInfoGsm gsm3 = cellInfo5.ratSpecificInfo.gsm();
                    convertHalCellIdentityGsm = convertHalCellIdentityGsm(gsm3.cellIdentityGsm);
                    convertHalGsmSignalStrength = convertHalGsmSignalStrength(gsm3.signalStrengthGsm);
                    cellConfigLte3 = null;
                    cellSignalStrengthLte = null;
                    cellSignalStrengthWcdma = null;
                    cellIdentityNr = null;
                    cellIdentityCdma = null;
                    cellIdentityLte = null;
                    cellIdentityWcdma = null;
                    cellIdentityTdscdma = null;
                    cellConfigLte = null;
                    cellIdentityGsm = convertHalCellIdentityGsm;
                    c3 = 1;
                    cellConfigLte2 = null;
                    convertHalTdscdmaSignalStrength = null;
                } else if (discriminator3 != 1) {
                    if (discriminator3 == 2) {
                        android.hardware.radio.V1_5.CellInfoTdscdma tdscdma3 = cellInfo5.ratSpecificInfo.tdscdma();
                        CellIdentityTdscdma convertHalCellIdentityTdscdma5 = convertHalCellIdentityTdscdma(tdscdma3.cellIdentityTdscdma);
                        convertHalTdscdmaSignalStrength = convertHalTdscdmaSignalStrength(tdscdma3.signalStrengthTdscdma);
                        convertHalGsmSignalStrength = null;
                        cellSignalStrengthLte = null;
                        cellSignalStrengthWcdma = null;
                        cellIdentityGsm = null;
                        cellIdentityNr = null;
                        cellIdentityCdma = null;
                        cellIdentityLte = null;
                        cellIdentityWcdma = null;
                        cellConfigLte = null;
                        cellIdentityTdscdma = convertHalCellIdentityTdscdma5;
                        c3 = 5;
                        cellConfigLte2 = null;
                    } else if (discriminator3 == 3) {
                        android.hardware.radio.V1_6.CellInfoLte lte3 = cellInfo5.ratSpecificInfo.lte();
                        CellIdentityLte convertHalCellIdentityLte5 = convertHalCellIdentityLte(lte3.cellIdentityLte);
                        CellSignalStrengthLte convertHalLteSignalStrength4 = convertHalLteSignalStrength(lte3.signalStrengthLte);
                        cellSignalStrengthWcdma = null;
                        convertHalTdscdmaSignalStrength = null;
                        cellIdentityGsm = null;
                        cellIdentityNr = null;
                        cellIdentityCdma = null;
                        cellIdentityWcdma = null;
                        cellIdentityTdscdma = null;
                        cellIdentityLte = convertHalCellIdentityLte5;
                        cellConfigLte = new CellConfigLte();
                        c3 = 3;
                        cellSignalStrengthLte = convertHalLteSignalStrength4;
                        convertHalGsmSignalStrength = null;
                        cellConfigLte2 = null;
                    } else if (discriminator3 == 4) {
                        android.hardware.radio.V1_6.CellInfoNr nr3 = cellInfo5.ratSpecificInfo.nr();
                        convertHalCellIdentityNr = convertHalCellIdentityNr(nr3.cellIdentityNr);
                        convertHalNrSignalStrength = convertHalNrSignalStrength(nr3.signalStrengthNr);
                        cellSignalStrengthLte = null;
                        cellSignalStrengthWcdma = null;
                        convertHalTdscdmaSignalStrength = null;
                        cellIdentityGsm = null;
                        cellIdentityCdma = null;
                        cellIdentityLte = null;
                        cellIdentityWcdma = null;
                        cellIdentityTdscdma = null;
                        cellConfigLte = null;
                        c3 = 6;
                        cellIdentityNr = convertHalCellIdentityNr;
                        cellConfigLte3 = convertHalNrSignalStrength;
                        convertHalGsmSignalStrength = null;
                        cellConfigLte2 = null;
                    } else if (discriminator3 != 5) {
                        return null;
                    } else {
                        android.hardware.radio.V1_2.CellInfoCdma cdma3 = cellInfo5.ratSpecificInfo.cdma();
                        convertHalCellIdentityCdma = convertHalCellIdentityCdma(cdma3.cellIdentityCdma);
                        convertHalCdmaSignalStrength = convertHalCdmaSignalStrength(cdma3.signalStrengthCdma, cdma3.signalStrengthEvdo);
                        cellConfigLte3 = null;
                        cellSignalStrengthLte = null;
                        convertHalTdscdmaSignalStrength = null;
                        cellIdentityGsm = null;
                        cellIdentityNr = null;
                        cellIdentityLte = null;
                        cellIdentityWcdma = null;
                        cellIdentityTdscdma = null;
                        cellConfigLte = null;
                        cellIdentityCdma = convertHalCellIdentityCdma;
                        c3 = 2;
                        cellConfigLte2 = convertHalCdmaSignalStrength;
                        convertHalGsmSignalStrength = null;
                        cellSignalStrengthWcdma = null;
                    }
                    cellConfigLte3 = cellConfigLte2;
                } else {
                    android.hardware.radio.V1_5.CellInfoWcdma wcdma3 = cellInfo5.ratSpecificInfo.wcdma();
                    convertHalCellIdentityWcdma = convertHalCellIdentityWcdma(wcdma3.cellIdentityWcdma);
                    convertHalWcdmaSignalStrength = convertHalWcdmaSignalStrength(wcdma3.signalStrengthWcdma);
                    cellSignalStrengthWcdma = convertHalWcdmaSignalStrength;
                    convertHalGsmSignalStrength = null;
                    cellConfigLte3 = null;
                    cellSignalStrengthLte = null;
                    convertHalTdscdmaSignalStrength = null;
                    cellIdentityGsm = null;
                    cellIdentityNr = null;
                    cellIdentityCdma = null;
                    cellIdentityLte = null;
                    cellIdentityTdscdma = null;
                    cellConfigLte = null;
                    cellIdentityWcdma = convertHalCellIdentityWcdma;
                    cellConfigLte2 = null;
                }
            }
            cellSignalStrengthGsm = convertHalGsmSignalStrength;
            cellIdentityGsm2 = cellIdentityGsm;
            cellIdentityWcdma2 = cellIdentityWcdma;
            CellIdentityCdma cellIdentityCdma7 = cellIdentityCdma;
            z2 = z;
            cellIdentityNr2 = cellIdentityNr;
            i2 = i;
            cellIdentityLte2 = cellIdentityLte;
            c = c3;
            cellSignalStrengthWcdma2 = cellSignalStrengthWcdma;
            cellIdentityCdma2 = cellIdentityCdma7;
            CellSignalStrengthTdscdma cellSignalStrengthTdscdma5 = convertHalTdscdmaSignalStrength;
            cellConfigLte4 = cellConfigLte2;
            cellSignalStrengthTdscdma = cellSignalStrengthTdscdma5;
        }
        switch (c) {
            case 1:
                return new android.telephony.CellInfoGsm(i2, z2, j, cellIdentityGsm2, cellSignalStrengthGsm);
            case 2:
                return new android.telephony.CellInfoCdma(i2, z2, j, cellIdentityCdma2, cellConfigLte4);
            case 3:
                return new android.telephony.CellInfoLte(i2, z2, j, cellIdentityLte2, cellSignalStrengthLte, cellConfigLte);
            case 4:
                return new android.telephony.CellInfoWcdma(i2, z2, j, cellIdentityWcdma2, cellSignalStrengthWcdma2);
            case 5:
                return new android.telephony.CellInfoTdscdma(i2, z2, j, cellIdentityTdscdma, cellSignalStrengthTdscdma);
            case 6:
                return new android.telephony.CellInfoNr(i2, z2, j, cellIdentityNr2, cellConfigLte3);
            default:
                return null;
        }
    }

    private static CellInfo convertHalCellInfo(android.hardware.radio.network.CellInfo cellInfo, long j) {
        if (cellInfo == null) {
            return null;
        }
        int i = cellInfo.connectionStatus;
        boolean z = cellInfo.registered;
        int tag = cellInfo.ratSpecificInfo.getTag();
        if (tag == 0) {
            android.hardware.radio.network.CellInfoGsm gsm = cellInfo.ratSpecificInfo.getGsm();
            return new android.telephony.CellInfoGsm(i, z, j, convertHalCellIdentityGsm(gsm.cellIdentityGsm), convertHalGsmSignalStrength(gsm.signalStrengthGsm));
        } else if (tag == 1) {
            android.hardware.radio.network.CellInfoWcdma wcdma = cellInfo.ratSpecificInfo.getWcdma();
            return new android.telephony.CellInfoWcdma(i, z, j, convertHalCellIdentityWcdma(wcdma.cellIdentityWcdma), convertHalWcdmaSignalStrength(wcdma.signalStrengthWcdma));
        } else if (tag == 2) {
            android.hardware.radio.network.CellInfoTdscdma tdscdma = cellInfo.ratSpecificInfo.getTdscdma();
            return new android.telephony.CellInfoTdscdma(i, z, j, convertHalCellIdentityTdscdma(tdscdma.cellIdentityTdscdma), convertHalTdscdmaSignalStrength(tdscdma.signalStrengthTdscdma));
        } else if (tag == 3) {
            android.hardware.radio.network.CellInfoLte lte = cellInfo.ratSpecificInfo.getLte();
            return new android.telephony.CellInfoLte(i, z, j, convertHalCellIdentityLte(lte.cellIdentityLte), convertHalLteSignalStrength(lte.signalStrengthLte), new CellConfigLte());
        } else if (tag == 4) {
            android.hardware.radio.network.CellInfoNr nr = cellInfo.ratSpecificInfo.getNr();
            return new android.telephony.CellInfoNr(i, z, j, convertHalCellIdentityNr(nr.cellIdentityNr), convertHalNrSignalStrength(nr.signalStrengthNr));
        } else if (tag != 5) {
            return null;
        } else {
            android.hardware.radio.network.CellInfoCdma cdma = cellInfo.ratSpecificInfo.getCdma();
            return new android.telephony.CellInfoCdma(i, z, j, convertHalCellIdentityCdma(cdma.cellIdentityCdma), convertHalCdmaSignalStrength(cdma.signalStrengthCdma, cdma.signalStrengthEvdo));
        }
    }

    public static CellIdentity convertHalCellIdentity(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof android.hardware.radio.V1_0.CellIdentity) {
            android.hardware.radio.V1_0.CellIdentity cellIdentity = (android.hardware.radio.V1_0.CellIdentity) obj;
            int i = cellIdentity.cellInfoType;
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i == 4) {
                            if (cellIdentity.cellIdentityWcdma.size() == 1) {
                                return convertHalCellIdentityWcdma(cellIdentity.cellIdentityWcdma.get(0));
                            }
                        } else if (i == 5 && cellIdentity.cellIdentityTdscdma.size() == 1) {
                            return convertHalCellIdentityTdscdma(cellIdentity.cellIdentityTdscdma.get(0));
                        }
                    } else if (cellIdentity.cellIdentityLte.size() == 1) {
                        return convertHalCellIdentityLte(cellIdentity.cellIdentityLte.get(0));
                    }
                } else if (cellIdentity.cellIdentityCdma.size() == 1) {
                    return convertHalCellIdentityCdma(cellIdentity.cellIdentityCdma.get(0));
                }
            } else if (cellIdentity.cellIdentityGsm.size() == 1) {
                return convertHalCellIdentityGsm(cellIdentity.cellIdentityGsm.get(0));
            }
        } else if (obj instanceof android.hardware.radio.V1_2.CellIdentity) {
            android.hardware.radio.V1_2.CellIdentity cellIdentity2 = (android.hardware.radio.V1_2.CellIdentity) obj;
            int i2 = cellIdentity2.cellInfoType;
            if (i2 != 1) {
                if (i2 != 2) {
                    if (i2 != 3) {
                        if (i2 == 4) {
                            if (cellIdentity2.cellIdentityWcdma.size() == 1) {
                                return convertHalCellIdentityWcdma(cellIdentity2.cellIdentityWcdma.get(0));
                            }
                        } else if (i2 == 5 && cellIdentity2.cellIdentityTdscdma.size() == 1) {
                            return convertHalCellIdentityTdscdma(cellIdentity2.cellIdentityTdscdma.get(0));
                        }
                    } else if (cellIdentity2.cellIdentityLte.size() == 1) {
                        return convertHalCellIdentityLte(cellIdentity2.cellIdentityLte.get(0));
                    }
                } else if (cellIdentity2.cellIdentityCdma.size() == 1) {
                    return convertHalCellIdentityCdma(cellIdentity2.cellIdentityCdma.get(0));
                }
            } else if (cellIdentity2.cellIdentityGsm.size() == 1) {
                return convertHalCellIdentityGsm(cellIdentity2.cellIdentityGsm.get(0));
            }
        } else if (obj instanceof android.hardware.radio.V1_5.CellIdentity) {
            android.hardware.radio.V1_5.CellIdentity cellIdentity3 = (android.hardware.radio.V1_5.CellIdentity) obj;
            switch (cellIdentity3.getDiscriminator()) {
                case 1:
                    return convertHalCellIdentityGsm(cellIdentity3.gsm());
                case 2:
                    return convertHalCellIdentityWcdma(cellIdentity3.wcdma());
                case 3:
                    return convertHalCellIdentityTdscdma(cellIdentity3.tdscdma());
                case 4:
                    return convertHalCellIdentityCdma(cellIdentity3.cdma());
                case 5:
                    return convertHalCellIdentityLte(cellIdentity3.lte());
                case 6:
                    return convertHalCellIdentityNr(cellIdentity3.nr());
            }
        }
        return null;
    }

    public static CellIdentity convertHalCellIdentity(android.hardware.radio.network.CellIdentity cellIdentity) {
        if (cellIdentity == null) {
            return null;
        }
        switch (cellIdentity.getTag()) {
            case 1:
                return convertHalCellIdentityGsm(cellIdentity.getGsm());
            case 2:
                return convertHalCellIdentityWcdma(cellIdentity.getWcdma());
            case 3:
                return convertHalCellIdentityTdscdma(cellIdentity.getTdscdma());
            case 4:
                return convertHalCellIdentityCdma(cellIdentity.getCdma());
            case 5:
                return convertHalCellIdentityLte(cellIdentity.getLte());
            case 6:
                return convertHalCellIdentityNr(cellIdentity.getNr());
            default:
                return null;
        }
    }

    public static CellIdentityGsm convertHalCellIdentityGsm(Object obj) {
        CellIdentityGsm cellIdentityGsm = null;
        if (obj == null) {
            return null;
        }
        if (obj instanceof android.hardware.radio.V1_0.CellIdentityGsm) {
            android.hardware.radio.V1_0.CellIdentityGsm cellIdentityGsm2 = (android.hardware.radio.V1_0.CellIdentityGsm) obj;
            int i = cellIdentityGsm2.lac;
            int i2 = cellIdentityGsm2.cid;
            int i3 = cellIdentityGsm2.arfcn;
            byte b = cellIdentityGsm2.bsic;
            return new CellIdentityGsm(i, i2, i3, b == -1 ? (byte) 2147483647 : b, cellIdentityGsm2.mcc, cellIdentityGsm2.mnc, PhoneConfigurationManager.SSSS, PhoneConfigurationManager.SSSS, new ArraySet());
        } else if (obj instanceof android.hardware.radio.V1_2.CellIdentityGsm) {
            android.hardware.radio.V1_2.CellIdentityGsm cellIdentityGsm3 = (android.hardware.radio.V1_2.CellIdentityGsm) obj;
            android.hardware.radio.V1_0.CellIdentityGsm cellIdentityGsm4 = cellIdentityGsm3.base;
            int i4 = cellIdentityGsm4.lac;
            int i5 = cellIdentityGsm4.cid;
            int i6 = cellIdentityGsm4.arfcn;
            byte b2 = cellIdentityGsm4.bsic;
            byte b3 = b2 == -1 ? (byte) 2147483647 : b2;
            String str = cellIdentityGsm4.mcc;
            String str2 = cellIdentityGsm4.mnc;
            CellIdentityOperatorNames cellIdentityOperatorNames = cellIdentityGsm3.operatorNames;
            return new CellIdentityGsm(i4, i5, i6, b3, str, str2, cellIdentityOperatorNames.alphaLong, cellIdentityOperatorNames.alphaShort, new ArraySet());
        } else {
            if (obj instanceof android.hardware.radio.V1_5.CellIdentityGsm) {
                android.hardware.radio.V1_5.CellIdentityGsm cellIdentityGsm5 = (android.hardware.radio.V1_5.CellIdentityGsm) obj;
                android.hardware.radio.V1_2.CellIdentityGsm cellIdentityGsm6 = cellIdentityGsm5.base;
                android.hardware.radio.V1_0.CellIdentityGsm cellIdentityGsm7 = cellIdentityGsm6.base;
                int i7 = cellIdentityGsm7.lac;
                int i8 = cellIdentityGsm7.cid;
                int i9 = cellIdentityGsm7.arfcn;
                byte b4 = cellIdentityGsm7.bsic;
                if (b4 == -1) {
                    b4 = 2147483647;
                }
                String str3 = cellIdentityGsm7.mcc;
                String str4 = cellIdentityGsm7.mnc;
                CellIdentityOperatorNames cellIdentityOperatorNames2 = cellIdentityGsm6.operatorNames;
                cellIdentityGsm = new CellIdentityGsm(i7, i8, i9, b4, str3, str4, cellIdentityOperatorNames2.alphaLong, cellIdentityOperatorNames2.alphaShort, cellIdentityGsm5.additionalPlmns);
            }
            return cellIdentityGsm;
        }
    }

    public static CellIdentityGsm convertHalCellIdentityGsm(android.hardware.radio.network.CellIdentityGsm cellIdentityGsm) {
        int i = cellIdentityGsm.lac;
        int i2 = cellIdentityGsm.cid;
        int i3 = cellIdentityGsm.arfcn;
        byte b = cellIdentityGsm.bsic;
        if (b == -1) {
            b = 2147483647;
        }
        String str = cellIdentityGsm.mcc;
        String str2 = cellIdentityGsm.mnc;
        OperatorInfo operatorInfo = cellIdentityGsm.operatorNames;
        return new CellIdentityGsm(i, i2, i3, b, str, str2, operatorInfo.alphaLong, operatorInfo.alphaShort, new ArraySet());
    }

    public static CellIdentityCdma convertHalCellIdentityCdma(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof android.hardware.radio.V1_0.CellIdentityCdma) {
            android.hardware.radio.V1_0.CellIdentityCdma cellIdentityCdma = (android.hardware.radio.V1_0.CellIdentityCdma) obj;
            return new CellIdentityCdma(cellIdentityCdma.networkId, cellIdentityCdma.systemId, cellIdentityCdma.baseStationId, cellIdentityCdma.longitude, cellIdentityCdma.latitude, PhoneConfigurationManager.SSSS, PhoneConfigurationManager.SSSS);
        } else if (obj instanceof android.hardware.radio.V1_2.CellIdentityCdma) {
            android.hardware.radio.V1_2.CellIdentityCdma cellIdentityCdma2 = (android.hardware.radio.V1_2.CellIdentityCdma) obj;
            android.hardware.radio.V1_0.CellIdentityCdma cellIdentityCdma3 = cellIdentityCdma2.base;
            int i = cellIdentityCdma3.networkId;
            int i2 = cellIdentityCdma3.systemId;
            int i3 = cellIdentityCdma3.baseStationId;
            int i4 = cellIdentityCdma3.longitude;
            int i5 = cellIdentityCdma3.latitude;
            CellIdentityOperatorNames cellIdentityOperatorNames = cellIdentityCdma2.operatorNames;
            return new CellIdentityCdma(i, i2, i3, i4, i5, cellIdentityOperatorNames.alphaLong, cellIdentityOperatorNames.alphaShort);
        } else {
            return null;
        }
    }

    public static CellIdentityCdma convertHalCellIdentityCdma(android.hardware.radio.network.CellIdentityCdma cellIdentityCdma) {
        int i = cellIdentityCdma.networkId;
        int i2 = cellIdentityCdma.systemId;
        int i3 = cellIdentityCdma.baseStationId;
        int i4 = cellIdentityCdma.longitude;
        int i5 = cellIdentityCdma.latitude;
        OperatorInfo operatorInfo = cellIdentityCdma.operatorNames;
        return new CellIdentityCdma(i, i2, i3, i4, i5, operatorInfo.alphaLong, operatorInfo.alphaShort);
    }

    public static CellIdentityLte convertHalCellIdentityLte(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof android.hardware.radio.V1_0.CellIdentityLte) {
            android.hardware.radio.V1_0.CellIdentityLte cellIdentityLte = (android.hardware.radio.V1_0.CellIdentityLte) obj;
            return new CellIdentityLte(cellIdentityLte.ci, cellIdentityLte.pci, cellIdentityLte.tac, cellIdentityLte.earfcn, new int[0], KeepaliveStatus.INVALID_HANDLE, cellIdentityLte.mcc, cellIdentityLte.mnc, PhoneConfigurationManager.SSSS, PhoneConfigurationManager.SSSS, new ArraySet(), null);
        } else if (obj instanceof android.hardware.radio.V1_2.CellIdentityLte) {
            android.hardware.radio.V1_2.CellIdentityLte cellIdentityLte2 = (android.hardware.radio.V1_2.CellIdentityLte) obj;
            android.hardware.radio.V1_0.CellIdentityLte cellIdentityLte3 = cellIdentityLte2.base;
            int i = cellIdentityLte2.bandwidth;
            String str = cellIdentityLte3.mcc;
            String str2 = cellIdentityLte3.mnc;
            CellIdentityOperatorNames cellIdentityOperatorNames = cellIdentityLte2.operatorNames;
            return new CellIdentityLte(cellIdentityLte3.ci, cellIdentityLte3.pci, cellIdentityLte3.tac, cellIdentityLte3.earfcn, new int[0], i, str, str2, cellIdentityOperatorNames.alphaLong, cellIdentityOperatorNames.alphaShort, new ArraySet(), null);
        } else if (obj instanceof android.hardware.radio.V1_5.CellIdentityLte) {
            android.hardware.radio.V1_5.CellIdentityLte cellIdentityLte4 = (android.hardware.radio.V1_5.CellIdentityLte) obj;
            android.hardware.radio.V1_0.CellIdentityLte cellIdentityLte5 = cellIdentityLte4.base.base;
            int i2 = cellIdentityLte5.ci;
            int i3 = cellIdentityLte5.pci;
            int i4 = cellIdentityLte5.tac;
            int i5 = cellIdentityLte5.earfcn;
            int[] array = cellIdentityLte4.bands.stream().mapToInt(new RILUtils$$ExternalSyntheticLambda6()).toArray();
            android.hardware.radio.V1_2.CellIdentityLte cellIdentityLte6 = cellIdentityLte4.base;
            int i6 = cellIdentityLte6.bandwidth;
            android.hardware.radio.V1_0.CellIdentityLte cellIdentityLte7 = cellIdentityLte6.base;
            String str3 = cellIdentityLte7.mcc;
            String str4 = cellIdentityLte7.mnc;
            CellIdentityOperatorNames cellIdentityOperatorNames2 = cellIdentityLte6.operatorNames;
            return new CellIdentityLte(i2, i3, i4, i5, array, i6, str3, str4, cellIdentityOperatorNames2.alphaLong, cellIdentityOperatorNames2.alphaShort, cellIdentityLte4.additionalPlmns, convertHalClosedSubscriberGroupInfo(cellIdentityLte4.optionalCsgInfo));
        } else {
            return null;
        }
    }

    public static CellIdentityLte convertHalCellIdentityLte(android.hardware.radio.network.CellIdentityLte cellIdentityLte) {
        int i = cellIdentityLte.ci;
        int i2 = cellIdentityLte.pci;
        int i3 = cellIdentityLte.tac;
        int i4 = cellIdentityLte.earfcn;
        int[] iArr = cellIdentityLte.bands;
        int i5 = cellIdentityLte.bandwidth;
        String str = cellIdentityLte.mcc;
        String str2 = cellIdentityLte.mnc;
        OperatorInfo operatorInfo = cellIdentityLte.operatorNames;
        return new CellIdentityLte(i, i2, i3, i4, iArr, i5, str, str2, operatorInfo.alphaLong, operatorInfo.alphaShort, primitiveArrayToArrayList(cellIdentityLte.additionalPlmns), convertHalClosedSubscriberGroupInfo(cellIdentityLte.csgInfo));
    }

    public static CellIdentityWcdma convertHalCellIdentityWcdma(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof android.hardware.radio.V1_0.CellIdentityWcdma) {
            android.hardware.radio.V1_0.CellIdentityWcdma cellIdentityWcdma = (android.hardware.radio.V1_0.CellIdentityWcdma) obj;
            return new CellIdentityWcdma(cellIdentityWcdma.lac, cellIdentityWcdma.cid, cellIdentityWcdma.psc, cellIdentityWcdma.uarfcn, cellIdentityWcdma.mcc, cellIdentityWcdma.mnc, PhoneConfigurationManager.SSSS, PhoneConfigurationManager.SSSS, new ArraySet(), null);
        } else if (obj instanceof android.hardware.radio.V1_2.CellIdentityWcdma) {
            android.hardware.radio.V1_2.CellIdentityWcdma cellIdentityWcdma2 = (android.hardware.radio.V1_2.CellIdentityWcdma) obj;
            android.hardware.radio.V1_0.CellIdentityWcdma cellIdentityWcdma3 = cellIdentityWcdma2.base;
            int i = cellIdentityWcdma3.lac;
            int i2 = cellIdentityWcdma3.cid;
            int i3 = cellIdentityWcdma3.psc;
            int i4 = cellIdentityWcdma3.uarfcn;
            String str = cellIdentityWcdma3.mcc;
            String str2 = cellIdentityWcdma3.mnc;
            CellIdentityOperatorNames cellIdentityOperatorNames = cellIdentityWcdma2.operatorNames;
            return new CellIdentityWcdma(i, i2, i3, i4, str, str2, cellIdentityOperatorNames.alphaLong, cellIdentityOperatorNames.alphaShort, new ArraySet(), null);
        } else if (obj instanceof android.hardware.radio.V1_5.CellIdentityWcdma) {
            android.hardware.radio.V1_5.CellIdentityWcdma cellIdentityWcdma4 = (android.hardware.radio.V1_5.CellIdentityWcdma) obj;
            android.hardware.radio.V1_2.CellIdentityWcdma cellIdentityWcdma5 = cellIdentityWcdma4.base;
            android.hardware.radio.V1_0.CellIdentityWcdma cellIdentityWcdma6 = cellIdentityWcdma5.base;
            int i5 = cellIdentityWcdma6.lac;
            int i6 = cellIdentityWcdma6.cid;
            int i7 = cellIdentityWcdma6.psc;
            int i8 = cellIdentityWcdma6.uarfcn;
            String str3 = cellIdentityWcdma6.mcc;
            String str4 = cellIdentityWcdma6.mnc;
            CellIdentityOperatorNames cellIdentityOperatorNames2 = cellIdentityWcdma5.operatorNames;
            return new CellIdentityWcdma(i5, i6, i7, i8, str3, str4, cellIdentityOperatorNames2.alphaLong, cellIdentityOperatorNames2.alphaShort, cellIdentityWcdma4.additionalPlmns, convertHalClosedSubscriberGroupInfo(cellIdentityWcdma4.optionalCsgInfo));
        } else {
            return null;
        }
    }

    public static CellIdentityWcdma convertHalCellIdentityWcdma(android.hardware.radio.network.CellIdentityWcdma cellIdentityWcdma) {
        int i = cellIdentityWcdma.lac;
        int i2 = cellIdentityWcdma.cid;
        int i3 = cellIdentityWcdma.psc;
        int i4 = cellIdentityWcdma.uarfcn;
        String str = cellIdentityWcdma.mcc;
        String str2 = cellIdentityWcdma.mnc;
        OperatorInfo operatorInfo = cellIdentityWcdma.operatorNames;
        return new CellIdentityWcdma(i, i2, i3, i4, str, str2, operatorInfo.alphaLong, operatorInfo.alphaShort, primitiveArrayToArrayList(cellIdentityWcdma.additionalPlmns), convertHalClosedSubscriberGroupInfo(cellIdentityWcdma.csgInfo));
    }

    public static CellIdentityTdscdma convertHalCellIdentityTdscdma(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof android.hardware.radio.V1_0.CellIdentityTdscdma) {
            android.hardware.radio.V1_0.CellIdentityTdscdma cellIdentityTdscdma = (android.hardware.radio.V1_0.CellIdentityTdscdma) obj;
            return new CellIdentityTdscdma(cellIdentityTdscdma.mcc, cellIdentityTdscdma.mnc, cellIdentityTdscdma.lac, cellIdentityTdscdma.cid, cellIdentityTdscdma.cpid, KeepaliveStatus.INVALID_HANDLE, PhoneConfigurationManager.SSSS, PhoneConfigurationManager.SSSS, Collections.emptyList(), null);
        } else if (obj instanceof android.hardware.radio.V1_2.CellIdentityTdscdma) {
            android.hardware.radio.V1_2.CellIdentityTdscdma cellIdentityTdscdma2 = (android.hardware.radio.V1_2.CellIdentityTdscdma) obj;
            android.hardware.radio.V1_0.CellIdentityTdscdma cellIdentityTdscdma3 = cellIdentityTdscdma2.base;
            String str = cellIdentityTdscdma3.mcc;
            String str2 = cellIdentityTdscdma3.mnc;
            int i = cellIdentityTdscdma3.lac;
            int i2 = cellIdentityTdscdma3.cid;
            int i3 = cellIdentityTdscdma3.cpid;
            int i4 = cellIdentityTdscdma2.uarfcn;
            CellIdentityOperatorNames cellIdentityOperatorNames = cellIdentityTdscdma2.operatorNames;
            return new CellIdentityTdscdma(str, str2, i, i2, i3, i4, cellIdentityOperatorNames.alphaLong, cellIdentityOperatorNames.alphaShort, Collections.emptyList(), null);
        } else if (obj instanceof android.hardware.radio.V1_5.CellIdentityTdscdma) {
            android.hardware.radio.V1_5.CellIdentityTdscdma cellIdentityTdscdma4 = (android.hardware.radio.V1_5.CellIdentityTdscdma) obj;
            android.hardware.radio.V1_2.CellIdentityTdscdma cellIdentityTdscdma5 = cellIdentityTdscdma4.base;
            android.hardware.radio.V1_0.CellIdentityTdscdma cellIdentityTdscdma6 = cellIdentityTdscdma5.base;
            String str3 = cellIdentityTdscdma6.mcc;
            String str4 = cellIdentityTdscdma6.mnc;
            int i5 = cellIdentityTdscdma6.lac;
            int i6 = cellIdentityTdscdma6.cid;
            int i7 = cellIdentityTdscdma6.cpid;
            int i8 = cellIdentityTdscdma5.uarfcn;
            CellIdentityOperatorNames cellIdentityOperatorNames2 = cellIdentityTdscdma5.operatorNames;
            return new CellIdentityTdscdma(str3, str4, i5, i6, i7, i8, cellIdentityOperatorNames2.alphaLong, cellIdentityOperatorNames2.alphaShort, cellIdentityTdscdma4.additionalPlmns, convertHalClosedSubscriberGroupInfo(cellIdentityTdscdma4.optionalCsgInfo));
        } else {
            return null;
        }
    }

    public static CellIdentityTdscdma convertHalCellIdentityTdscdma(android.hardware.radio.network.CellIdentityTdscdma cellIdentityTdscdma) {
        String str = cellIdentityTdscdma.mcc;
        String str2 = cellIdentityTdscdma.mnc;
        int i = cellIdentityTdscdma.lac;
        int i2 = cellIdentityTdscdma.cid;
        int i3 = cellIdentityTdscdma.cpid;
        int i4 = cellIdentityTdscdma.uarfcn;
        OperatorInfo operatorInfo = cellIdentityTdscdma.operatorNames;
        return new CellIdentityTdscdma(str, str2, i, i2, i3, i4, operatorInfo.alphaLong, operatorInfo.alphaShort, primitiveArrayToArrayList(cellIdentityTdscdma.additionalPlmns), convertHalClosedSubscriberGroupInfo(cellIdentityTdscdma.csgInfo));
    }

    public static CellIdentityNr convertHalCellIdentityNr(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof android.hardware.radio.V1_4.CellIdentityNr) {
            android.hardware.radio.V1_4.CellIdentityNr cellIdentityNr = (android.hardware.radio.V1_4.CellIdentityNr) obj;
            String str = cellIdentityNr.mcc;
            String str2 = cellIdentityNr.mnc;
            long j = cellIdentityNr.nci;
            CellIdentityOperatorNames cellIdentityOperatorNames = cellIdentityNr.operatorNames;
            return new CellIdentityNr(cellIdentityNr.pci, cellIdentityNr.tac, cellIdentityNr.nrarfcn, new int[0], str, str2, j, cellIdentityOperatorNames.alphaLong, cellIdentityOperatorNames.alphaShort, new ArraySet());
        } else if (obj instanceof android.hardware.radio.V1_5.CellIdentityNr) {
            android.hardware.radio.V1_5.CellIdentityNr cellIdentityNr2 = (android.hardware.radio.V1_5.CellIdentityNr) obj;
            android.hardware.radio.V1_4.CellIdentityNr cellIdentityNr3 = cellIdentityNr2.base;
            int i = cellIdentityNr3.pci;
            int i2 = cellIdentityNr3.tac;
            int i3 = cellIdentityNr3.nrarfcn;
            int[] array = cellIdentityNr2.bands.stream().mapToInt(new RILUtils$$ExternalSyntheticLambda6()).toArray();
            android.hardware.radio.V1_4.CellIdentityNr cellIdentityNr4 = cellIdentityNr2.base;
            String str3 = cellIdentityNr4.mcc;
            String str4 = cellIdentityNr4.mnc;
            long j2 = cellIdentityNr4.nci;
            CellIdentityOperatorNames cellIdentityOperatorNames2 = cellIdentityNr4.operatorNames;
            return new CellIdentityNr(i, i2, i3, array, str3, str4, j2, cellIdentityOperatorNames2.alphaLong, cellIdentityOperatorNames2.alphaShort, cellIdentityNr2.additionalPlmns);
        } else {
            return null;
        }
    }

    public static CellIdentityNr convertHalCellIdentityNr(android.hardware.radio.network.CellIdentityNr cellIdentityNr) {
        int i = cellIdentityNr.pci;
        int i2 = cellIdentityNr.tac;
        int i3 = cellIdentityNr.nrarfcn;
        int[] iArr = cellIdentityNr.bands;
        String str = cellIdentityNr.mcc;
        String str2 = cellIdentityNr.mnc;
        long j = cellIdentityNr.nci;
        OperatorInfo operatorInfo = cellIdentityNr.operatorNames;
        return new CellIdentityNr(i, i2, i3, iArr, str, str2, j, operatorInfo.alphaLong, operatorInfo.alphaShort, primitiveArrayToArrayList(cellIdentityNr.additionalPlmns));
    }

    public static SignalStrength convertHalSignalStrength(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof android.hardware.radio.V1_0.SignalStrength) {
            android.hardware.radio.V1_0.SignalStrength signalStrength = (android.hardware.radio.V1_0.SignalStrength) obj;
            return new SignalStrength(convertHalCdmaSignalStrength(signalStrength.cdma, signalStrength.evdo), convertHalGsmSignalStrength(signalStrength.gw), new CellSignalStrengthWcdma(), convertHalTdscdmaSignalStrength(signalStrength.tdScdma), convertHalLteSignalStrength(signalStrength.lte), new CellSignalStrengthNr());
        } else if (obj instanceof android.hardware.radio.V1_2.SignalStrength) {
            android.hardware.radio.V1_2.SignalStrength signalStrength2 = (android.hardware.radio.V1_2.SignalStrength) obj;
            return new SignalStrength(convertHalCdmaSignalStrength(signalStrength2.cdma, signalStrength2.evdo), convertHalGsmSignalStrength(signalStrength2.gsm), convertHalWcdmaSignalStrength(signalStrength2.wcdma), convertHalTdscdmaSignalStrength(signalStrength2.tdScdma), convertHalLteSignalStrength(signalStrength2.lte), new CellSignalStrengthNr());
        } else if (obj instanceof android.hardware.radio.V1_4.SignalStrength) {
            android.hardware.radio.V1_4.SignalStrength signalStrength3 = (android.hardware.radio.V1_4.SignalStrength) obj;
            return new SignalStrength(convertHalCdmaSignalStrength(signalStrength3.cdma, signalStrength3.evdo), convertHalGsmSignalStrength(signalStrength3.gsm), convertHalWcdmaSignalStrength(signalStrength3.wcdma), convertHalTdscdmaSignalStrength(signalStrength3.tdscdma), convertHalLteSignalStrength(signalStrength3.lte), convertHalNrSignalStrength(signalStrength3.nr));
        } else if (obj instanceof android.hardware.radio.V1_6.SignalStrength) {
            android.hardware.radio.V1_6.SignalStrength signalStrength4 = (android.hardware.radio.V1_6.SignalStrength) obj;
            return new SignalStrength(convertHalCdmaSignalStrength(signalStrength4.cdma, signalStrength4.evdo), convertHalGsmSignalStrength(signalStrength4.gsm), convertHalWcdmaSignalStrength(signalStrength4.wcdma), convertHalTdscdmaSignalStrength(signalStrength4.tdscdma), convertHalLteSignalStrength(signalStrength4.lte), convertHalNrSignalStrength(signalStrength4.nr));
        } else {
            return null;
        }
    }

    public static SignalStrength convertHalSignalStrength(android.hardware.radio.network.SignalStrength signalStrength) {
        return new SignalStrength(convertHalCdmaSignalStrength(signalStrength.cdma, signalStrength.evdo), convertHalGsmSignalStrength(signalStrength.gsm), convertHalWcdmaSignalStrength(signalStrength.wcdma), convertHalTdscdmaSignalStrength(signalStrength.tdscdma), convertHalLteSignalStrength(signalStrength.lte), convertHalNrSignalStrength(signalStrength.nr));
    }

    public static CellSignalStrengthGsm convertHalGsmSignalStrength(GsmSignalStrength gsmSignalStrength) {
        CellSignalStrengthGsm cellSignalStrengthGsm = new CellSignalStrengthGsm(CellSignalStrength.getRssiDbmFromAsu(gsmSignalStrength.signalStrength), gsmSignalStrength.bitErrorRate, gsmSignalStrength.timingAdvance);
        if (cellSignalStrengthGsm.getRssi() == Integer.MAX_VALUE) {
            cellSignalStrengthGsm.setDefaultValues();
            cellSignalStrengthGsm.updateLevel(null, null);
        }
        return cellSignalStrengthGsm;
    }

    public static CellSignalStrengthGsm convertHalGsmSignalStrength(android.hardware.radio.network.GsmSignalStrength gsmSignalStrength) {
        CellSignalStrengthGsm cellSignalStrengthGsm = new CellSignalStrengthGsm(CellSignalStrength.getRssiDbmFromAsu(gsmSignalStrength.signalStrength), gsmSignalStrength.bitErrorRate, gsmSignalStrength.timingAdvance);
        if (cellSignalStrengthGsm.getRssi() == Integer.MAX_VALUE) {
            cellSignalStrengthGsm.setDefaultValues();
            cellSignalStrengthGsm.updateLevel(null, null);
        }
        return cellSignalStrengthGsm;
    }

    public static CellSignalStrengthCdma convertHalCdmaSignalStrength(CdmaSignalStrength cdmaSignalStrength, EvdoSignalStrength evdoSignalStrength) {
        return new CellSignalStrengthCdma(-cdmaSignalStrength.dbm, -cdmaSignalStrength.ecio, -evdoSignalStrength.dbm, -evdoSignalStrength.ecio, evdoSignalStrength.signalNoiseRatio);
    }

    public static CellSignalStrengthCdma convertHalCdmaSignalStrength(android.hardware.radio.network.CdmaSignalStrength cdmaSignalStrength, android.hardware.radio.network.EvdoSignalStrength evdoSignalStrength) {
        return new CellSignalStrengthCdma(-cdmaSignalStrength.dbm, -cdmaSignalStrength.ecio, -evdoSignalStrength.dbm, -evdoSignalStrength.ecio, evdoSignalStrength.signalNoiseRatio);
    }

    public static CellSignalStrengthLte convertHalLteSignalStrength(Object obj) {
        CellSignalStrengthLte cellSignalStrengthLte = null;
        if (obj == null) {
            return null;
        }
        if (obj instanceof LteSignalStrength) {
            LteSignalStrength lteSignalStrength = (LteSignalStrength) obj;
            int convertRssiAsuToDBm = CellSignalStrengthLte.convertRssiAsuToDBm(lteSignalStrength.signalStrength);
            int i = lteSignalStrength.rsrp;
            if (i != Integer.MAX_VALUE) {
                i = -i;
            }
            int i2 = i;
            int i3 = lteSignalStrength.rsrq;
            if (i3 != Integer.MAX_VALUE) {
                i3 = -i3;
            }
            return new CellSignalStrengthLte(convertRssiAsuToDBm, i2, i3, CellSignalStrengthLte.convertRssnrUnitFromTenDbToDB(lteSignalStrength.rssnr), lteSignalStrength.cqi, lteSignalStrength.timingAdvance);
        }
        if (obj instanceof android.hardware.radio.V1_6.LteSignalStrength) {
            android.hardware.radio.V1_6.LteSignalStrength lteSignalStrength2 = (android.hardware.radio.V1_6.LteSignalStrength) obj;
            int convertRssiAsuToDBm2 = CellSignalStrengthLte.convertRssiAsuToDBm(lteSignalStrength2.base.signalStrength);
            LteSignalStrength lteSignalStrength3 = lteSignalStrength2.base;
            int i4 = lteSignalStrength3.rsrp;
            if (i4 != Integer.MAX_VALUE) {
                i4 = -i4;
            }
            int i5 = i4;
            int i6 = lteSignalStrength3.rsrq;
            int i7 = i6 != Integer.MAX_VALUE ? -i6 : i6;
            int convertRssnrUnitFromTenDbToDB = CellSignalStrengthLte.convertRssnrUnitFromTenDbToDB(lteSignalStrength3.rssnr);
            int i8 = lteSignalStrength2.cqiTableIndex;
            LteSignalStrength lteSignalStrength4 = lteSignalStrength2.base;
            cellSignalStrengthLte = new CellSignalStrengthLte(convertRssiAsuToDBm2, i5, i7, convertRssnrUnitFromTenDbToDB, i8, lteSignalStrength4.cqi, lteSignalStrength4.timingAdvance);
        }
        return cellSignalStrengthLte;
    }

    public static CellSignalStrengthLte convertHalLteSignalStrength(android.hardware.radio.network.LteSignalStrength lteSignalStrength) {
        int convertRssiAsuToDBm = CellSignalStrengthLte.convertRssiAsuToDBm(lteSignalStrength.signalStrength);
        int i = lteSignalStrength.rsrp;
        if (i != Integer.MAX_VALUE) {
            i = -i;
        }
        int i2 = i;
        int i3 = lteSignalStrength.rsrq;
        if (i3 != Integer.MAX_VALUE) {
            i3 = -i3;
        }
        return new CellSignalStrengthLte(convertRssiAsuToDBm, i2, i3, CellSignalStrengthLte.convertRssnrUnitFromTenDbToDB(lteSignalStrength.rssnr), lteSignalStrength.cqiTableIndex, lteSignalStrength.cqi, lteSignalStrength.timingAdvance);
    }

    public static CellSignalStrengthWcdma convertHalWcdmaSignalStrength(Object obj) {
        CellSignalStrengthWcdma cellSignalStrengthWcdma;
        if (obj == null) {
            return null;
        }
        if (obj instanceof WcdmaSignalStrength) {
            WcdmaSignalStrength wcdmaSignalStrength = (WcdmaSignalStrength) obj;
            cellSignalStrengthWcdma = new CellSignalStrengthWcdma(CellSignalStrength.getRssiDbmFromAsu(wcdmaSignalStrength.signalStrength), wcdmaSignalStrength.bitErrorRate, KeepaliveStatus.INVALID_HANDLE, KeepaliveStatus.INVALID_HANDLE);
        } else if (obj instanceof android.hardware.radio.V1_2.WcdmaSignalStrength) {
            android.hardware.radio.V1_2.WcdmaSignalStrength wcdmaSignalStrength2 = (android.hardware.radio.V1_2.WcdmaSignalStrength) obj;
            cellSignalStrengthWcdma = new CellSignalStrengthWcdma(CellSignalStrength.getRssiDbmFromAsu(wcdmaSignalStrength2.base.signalStrength), wcdmaSignalStrength2.base.bitErrorRate, CellSignalStrength.getRscpDbmFromAsu(wcdmaSignalStrength2.rscp), CellSignalStrength.getEcNoDbFromAsu(wcdmaSignalStrength2.ecno));
        } else {
            cellSignalStrengthWcdma = null;
        }
        if (cellSignalStrengthWcdma != null && cellSignalStrengthWcdma.getRssi() == Integer.MAX_VALUE && cellSignalStrengthWcdma.getRscp() == Integer.MAX_VALUE) {
            cellSignalStrengthWcdma.setDefaultValues();
            cellSignalStrengthWcdma.updateLevel(null, null);
        }
        return cellSignalStrengthWcdma;
    }

    public static CellSignalStrengthWcdma convertHalWcdmaSignalStrength(android.hardware.radio.network.WcdmaSignalStrength wcdmaSignalStrength) {
        CellSignalStrengthWcdma cellSignalStrengthWcdma = new CellSignalStrengthWcdma(CellSignalStrength.getRssiDbmFromAsu(wcdmaSignalStrength.signalStrength), wcdmaSignalStrength.bitErrorRate, CellSignalStrength.getRscpDbmFromAsu(wcdmaSignalStrength.rscp), CellSignalStrength.getEcNoDbFromAsu(wcdmaSignalStrength.ecno));
        if (cellSignalStrengthWcdma.getRssi() == Integer.MAX_VALUE && cellSignalStrengthWcdma.getRscp() == Integer.MAX_VALUE) {
            cellSignalStrengthWcdma.setDefaultValues();
            cellSignalStrengthWcdma.updateLevel(null, null);
        }
        return cellSignalStrengthWcdma;
    }

    public static CellSignalStrengthTdscdma convertHalTdscdmaSignalStrength(Object obj) {
        CellSignalStrengthTdscdma cellSignalStrengthTdscdma;
        if (obj == null) {
            return null;
        }
        if (obj instanceof TdScdmaSignalStrength) {
            int i = ((TdScdmaSignalStrength) obj).rscp;
            if (i != Integer.MAX_VALUE) {
                i = -i;
            }
            cellSignalStrengthTdscdma = new CellSignalStrengthTdscdma(KeepaliveStatus.INVALID_HANDLE, KeepaliveStatus.INVALID_HANDLE, i);
        } else if (obj instanceof TdscdmaSignalStrength) {
            TdscdmaSignalStrength tdscdmaSignalStrength = (TdscdmaSignalStrength) obj;
            cellSignalStrengthTdscdma = new CellSignalStrengthTdscdma(CellSignalStrength.getRssiDbmFromAsu(tdscdmaSignalStrength.signalStrength), tdscdmaSignalStrength.bitErrorRate, CellSignalStrength.getRscpDbmFromAsu(tdscdmaSignalStrength.rscp));
        } else {
            cellSignalStrengthTdscdma = null;
        }
        if (cellSignalStrengthTdscdma != null && cellSignalStrengthTdscdma.getRssi() == Integer.MAX_VALUE && cellSignalStrengthTdscdma.getRscp() == Integer.MAX_VALUE) {
            cellSignalStrengthTdscdma.setDefaultValues();
            cellSignalStrengthTdscdma.updateLevel(null, null);
        }
        return cellSignalStrengthTdscdma;
    }

    public static CellSignalStrengthTdscdma convertHalTdscdmaSignalStrength(android.hardware.radio.network.TdscdmaSignalStrength tdscdmaSignalStrength) {
        CellSignalStrengthTdscdma cellSignalStrengthTdscdma = new CellSignalStrengthTdscdma(CellSignalStrength.getRssiDbmFromAsu(tdscdmaSignalStrength.signalStrength), tdscdmaSignalStrength.bitErrorRate, CellSignalStrength.getRscpDbmFromAsu(tdscdmaSignalStrength.rscp));
        if (cellSignalStrengthTdscdma.getRssi() == Integer.MAX_VALUE && cellSignalStrengthTdscdma.getRscp() == Integer.MAX_VALUE) {
            cellSignalStrengthTdscdma.setDefaultValues();
            cellSignalStrengthTdscdma.updateLevel(null, null);
        }
        return cellSignalStrengthTdscdma;
    }

    public static CellSignalStrengthNr convertHalNrSignalStrength(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof NrSignalStrength) {
            NrSignalStrength nrSignalStrength = (NrSignalStrength) obj;
            return new CellSignalStrengthNr(CellSignalStrengthNr.flip(nrSignalStrength.csiRsrp), CellSignalStrengthNr.flip(nrSignalStrength.csiRsrq), nrSignalStrength.csiSinr, CellSignalStrengthNr.flip(nrSignalStrength.ssRsrp), CellSignalStrengthNr.flip(nrSignalStrength.ssRsrq), nrSignalStrength.ssSinr);
        } else if (obj instanceof android.hardware.radio.V1_6.NrSignalStrength) {
            android.hardware.radio.V1_6.NrSignalStrength nrSignalStrength2 = (android.hardware.radio.V1_6.NrSignalStrength) obj;
            int flip = CellSignalStrengthNr.flip(nrSignalStrength2.base.csiRsrp);
            int flip2 = CellSignalStrengthNr.flip(nrSignalStrength2.base.csiRsrq);
            NrSignalStrength nrSignalStrength3 = nrSignalStrength2.base;
            return new CellSignalStrengthNr(flip, flip2, nrSignalStrength3.csiSinr, nrSignalStrength2.csiCqiTableIndex, nrSignalStrength2.csiCqiReport, CellSignalStrengthNr.flip(nrSignalStrength3.ssRsrp), CellSignalStrengthNr.flip(nrSignalStrength2.base.ssRsrq), nrSignalStrength2.base.ssSinr, KeepaliveStatus.INVALID_HANDLE);
        } else {
            return null;
        }
    }

    public static CellSignalStrengthNr convertHalNrSignalStrength(android.hardware.radio.network.NrSignalStrength nrSignalStrength) {
        return new CellSignalStrengthNr(CellSignalStrengthNr.flip(nrSignalStrength.csiRsrp), CellSignalStrengthNr.flip(nrSignalStrength.csiRsrq), nrSignalStrength.csiSinr, nrSignalStrength.csiCqiTableIndex, primitiveArrayToArrayList(nrSignalStrength.csiCqiReport), CellSignalStrengthNr.flip(nrSignalStrength.ssRsrp), CellSignalStrengthNr.flip(nrSignalStrength.ssRsrq), nrSignalStrength.ssSinr, nrSignalStrength.timingAdvance);
    }

    private static ClosedSubscriberGroupInfo convertHalClosedSubscriberGroupInfo(OptionalCsgInfo optionalCsgInfo) {
        android.hardware.radio.V1_5.ClosedSubscriberGroupInfo csgInfo = optionalCsgInfo.getDiscriminator() == 1 ? optionalCsgInfo.csgInfo() : null;
        if (csgInfo == null) {
            return null;
        }
        return new ClosedSubscriberGroupInfo(csgInfo.csgIndication, csgInfo.homeNodebName, csgInfo.csgIdentity);
    }

    private static ClosedSubscriberGroupInfo convertHalClosedSubscriberGroupInfo(android.hardware.radio.network.ClosedSubscriberGroupInfo closedSubscriberGroupInfo) {
        if (closedSubscriberGroupInfo == null) {
            return null;
        }
        return new ClosedSubscriberGroupInfo(closedSubscriberGroupInfo.csgIndication, closedSubscriberGroupInfo.homeNodebName, closedSubscriberGroupInfo.csgIdentity);
    }

    public static SparseArray<BarringInfo.BarringServiceInfo> convertHalBarringInfoList(List<android.hardware.radio.V1_5.BarringInfo> list) {
        SparseArray<BarringInfo.BarringServiceInfo> sparseArray = new SparseArray<>();
        for (android.hardware.radio.V1_5.BarringInfo barringInfo : list) {
            if (barringInfo.barringType == 1) {
                if (barringInfo.barringTypeSpecificInfo.getDiscriminator() == 1) {
                    BarringInfo.BarringTypeSpecificInfo.Conditional conditional = barringInfo.barringTypeSpecificInfo.conditional();
                    sparseArray.put(barringInfo.serviceType, new BarringInfo.BarringServiceInfo(barringInfo.barringType, conditional.isBarred, conditional.factor, conditional.timeSeconds));
                }
            } else {
                sparseArray.put(barringInfo.serviceType, new BarringInfo.BarringServiceInfo(barringInfo.barringType, false, 0, 0));
            }
        }
        return sparseArray;
    }

    public static SparseArray<BarringInfo.BarringServiceInfo> convertHalBarringInfoList(android.hardware.radio.network.BarringInfo[] barringInfoArr) {
        SparseArray<BarringInfo.BarringServiceInfo> sparseArray = new SparseArray<>();
        for (android.hardware.radio.network.BarringInfo barringInfo : barringInfoArr) {
            if (barringInfo.barringType == 1) {
                if (barringInfo.barringTypeSpecificInfo != null) {
                    int i = barringInfo.serviceType;
                    int i2 = barringInfo.barringType;
                    BarringTypeSpecificInfo barringTypeSpecificInfo = barringInfo.barringTypeSpecificInfo;
                    sparseArray.put(i, new BarringInfo.BarringServiceInfo(i2, barringTypeSpecificInfo.isBarred, barringTypeSpecificInfo.factor, barringTypeSpecificInfo.timeSeconds));
                }
            } else {
                sparseArray.put(barringInfo.serviceType, new BarringInfo.BarringServiceInfo(barringInfo.barringType, false, 0, 0));
            }
        }
        return sparseArray;
    }

    private static android.net.LinkAddress convertToLinkAddress(String str) {
        return convertToLinkAddress(str, 0, -1L, -1L);
    }

    private static android.net.LinkAddress convertToLinkAddress(String str, int i, long j, long j2) {
        InetAddress inetAddress;
        int i2;
        int parseInt;
        String trim = str.trim();
        try {
            String[] split = trim.split("/", 2);
            InetAddress parseNumericAddress = InetAddresses.parseNumericAddress(split[0]);
            if (split.length == 1) {
                parseInt = parseNumericAddress instanceof Inet4Address ? 32 : 128;
            } else {
                parseInt = split.length == 2 ? Integer.parseInt(split[1]) : -1;
            }
            inetAddress = parseNumericAddress;
            i2 = parseInt;
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException | NullPointerException | NumberFormatException unused) {
            inetAddress = null;
            i2 = -1;
        }
        if (inetAddress == null || i2 == -1) {
            throw new IllegalArgumentException("Invalid link address " + trim);
        }
        return new android.net.LinkAddress(inetAddress, i2, i, 0, j, j2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:176:0x0289  */
    /* JADX WARN: Removed duplicated region for block: B:185:0x02ca  */
    /* JADX WARN: Removed duplicated region for block: B:188:0x02d7  */
    /* JADX WARN: Removed duplicated region for block: B:198:0x0314  */
    /* JADX WARN: Type inference failed for: r1v23 */
    @VisibleForTesting
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static DataCallResponse convertHalDataCallResult(Object obj) {
        int i;
        Qos convertHalQos;
        NetworkSliceInfo convertHalSliceInfo;
        String[] strArr;
        String str;
        String[] strArr2;
        String[] strArr3;
        int i2;
        int i3;
        long j;
        int i4;
        List list;
        int i5;
        ArrayList arrayList;
        byte b;
        ArrayList arrayList2;
        int i6;
        int i7;
        int i8;
        Object obj2;
        Iterator it;
        long j2;
        int i9;
        String str2;
        String[] strArr4;
        int i10;
        ArrayList arrayList3;
        int i11;
        String[] strArr5;
        int i12;
        String[] strArr6;
        int i13;
        int i14;
        ArrayList arrayList4;
        int i15;
        if (obj == null) {
            return null;
        }
        List arrayList5 = new ArrayList();
        ArrayList arrayList6 = new ArrayList();
        ArrayList arrayList7 = new ArrayList();
        if (obj instanceof SetupDataCallResult) {
            SetupDataCallResult setupDataCallResult = (SetupDataCallResult) obj;
            int i16 = setupDataCallResult.status;
            j2 = setupDataCallResult.suggestedRetryTime;
            int i17 = setupDataCallResult.cid;
            int i18 = setupDataCallResult.active;
            i9 = ApnSetting.getProtocolIntFromString(setupDataCallResult.type);
            str2 = setupDataCallResult.ifname;
            String[] split = !TextUtils.isEmpty(setupDataCallResult.addresses) ? setupDataCallResult.addresses.split("\\s+") : null;
            strArr4 = !TextUtils.isEmpty(setupDataCallResult.dnses) ? setupDataCallResult.dnses.split("\\s+") : null;
            String[] split2 = !TextUtils.isEmpty(setupDataCallResult.gateways) ? setupDataCallResult.gateways.split("\\s+") : null;
            String[] split3 = !TextUtils.isEmpty(setupDataCallResult.pcscf) ? setupDataCallResult.pcscf.split("\\s+") : null;
            int i19 = setupDataCallResult.mtu;
            if (split != null) {
                int length = split.length;
                i15 = i19;
                int i20 = 0;
                while (i20 < length) {
                    arrayList5.add(convertToLinkAddress(split[i20]));
                    i20++;
                    split2 = split2;
                }
            } else {
                i15 = i19;
            }
            i3 = i15;
            arrayList2 = arrayList7;
            i6 = i17;
            strArr = split2;
            convertHalSliceInfo = null;
            convertHalQos = null;
            i5 = 0;
            b = 0;
            arrayList = arrayList6;
            i7 = i16;
            i8 = i18;
            i2 = i3;
            strArr3 = split3;
            i = i2;
        } else if (obj instanceof android.hardware.radio.V1_4.SetupDataCallResult) {
            android.hardware.radio.V1_4.SetupDataCallResult setupDataCallResult2 = (android.hardware.radio.V1_4.SetupDataCallResult) obj;
            int i21 = setupDataCallResult2.cause;
            j2 = setupDataCallResult2.suggestedRetryTime;
            int i22 = setupDataCallResult2.cid;
            int i23 = setupDataCallResult2.active;
            i9 = setupDataCallResult2.type;
            str2 = setupDataCallResult2.ifname;
            String[] strArr7 = (String[]) setupDataCallResult2.addresses.toArray(new String[0]);
            strArr4 = (String[]) setupDataCallResult2.dnses.toArray(new String[0]);
            String[] strArr8 = (String[]) setupDataCallResult2.gateways.toArray(new String[0]);
            String[] strArr9 = (String[]) setupDataCallResult2.pcscf.toArray(new String[0]);
            int i24 = setupDataCallResult2.mtu;
            if (strArr7 != null) {
                int length2 = strArr7.length;
                int i25 = 0;
                while (i25 < length2) {
                    arrayList5.add(convertToLinkAddress(strArr7[i25]));
                    i25++;
                    i24 = i24;
                }
            }
            i2 = i24;
            strArr = strArr8;
            arrayList = arrayList6;
            convertHalSliceInfo = null;
            convertHalQos = null;
            i5 = 0;
            b = 0;
            i3 = i2;
            arrayList2 = arrayList7;
            i6 = i22;
            i7 = i21;
            i8 = i23;
            strArr3 = strArr9;
            i = i3;
        } else if (obj instanceof android.hardware.radio.V1_5.SetupDataCallResult) {
            android.hardware.radio.V1_5.SetupDataCallResult setupDataCallResult3 = (android.hardware.radio.V1_5.SetupDataCallResult) obj;
            int i26 = setupDataCallResult3.cause;
            j2 = setupDataCallResult3.suggestedRetryTime;
            int i27 = setupDataCallResult3.cid;
            int i28 = setupDataCallResult3.active;
            i9 = setupDataCallResult3.type;
            str2 = setupDataCallResult3.ifname;
            arrayList5 = (List) setupDataCallResult3.addresses.stream().map(new Function() { // from class: com.android.internal.telephony.RILUtils$$ExternalSyntheticLambda3
                @Override // java.util.function.Function
                public final Object apply(Object obj3) {
                    android.net.LinkAddress lambda$convertHalDataCallResult$0;
                    lambda$convertHalDataCallResult$0 = RILUtils.lambda$convertHalDataCallResult$0((LinkAddress) obj3);
                    return lambda$convertHalDataCallResult$0;
                }
            }).collect(Collectors.toList());
            strArr4 = (String[]) setupDataCallResult3.dnses.toArray(new String[0]);
            strArr = (String[]) setupDataCallResult3.gateways.toArray(new String[0]);
            String[] strArr10 = (String[]) setupDataCallResult3.pcscf.toArray(new String[0]);
            i2 = Math.max(setupDataCallResult3.mtuV4, setupDataCallResult3.mtuV6);
            i3 = setupDataCallResult3.mtuV4;
            arrayList = arrayList6;
            convertHalSliceInfo = null;
            convertHalQos = null;
            i5 = 0;
            b = 0;
            arrayList2 = arrayList7;
            i6 = i27;
            i7 = i26;
            i8 = i28;
            strArr3 = strArr10;
            i = setupDataCallResult3.mtuV6;
        } else if (obj instanceof android.hardware.radio.V1_6.SetupDataCallResult) {
            android.hardware.radio.V1_6.SetupDataCallResult setupDataCallResult4 = (android.hardware.radio.V1_6.SetupDataCallResult) obj;
            int i29 = setupDataCallResult4.cause;
            long j3 = setupDataCallResult4.suggestedRetryTime;
            int i30 = setupDataCallResult4.cid;
            int i31 = setupDataCallResult4.active;
            int i32 = setupDataCallResult4.type;
            String str3 = setupDataCallResult4.ifname;
            List list2 = (List) setupDataCallResult4.addresses.stream().map(new Function() { // from class: com.android.internal.telephony.RILUtils$$ExternalSyntheticLambda4
                @Override // java.util.function.Function
                public final Object apply(Object obj3) {
                    android.net.LinkAddress lambda$convertHalDataCallResult$1;
                    lambda$convertHalDataCallResult$1 = RILUtils.lambda$convertHalDataCallResult$1((LinkAddress) obj3);
                    return lambda$convertHalDataCallResult$1;
                }
            }).collect(Collectors.toList());
            String[] strArr11 = (String[]) setupDataCallResult4.dnses.toArray(new String[0]);
            String[] strArr12 = (String[]) setupDataCallResult4.gateways.toArray(new String[0]);
            String[] strArr13 = (String[]) setupDataCallResult4.pcscf.toArray(new String[0]);
            int max = Math.max(setupDataCallResult4.mtuV4, setupDataCallResult4.mtuV6);
            int i33 = setupDataCallResult4.mtuV4;
            i = setupDataCallResult4.mtuV6;
            byte b2 = setupDataCallResult4.handoverFailureMode;
            int i34 = setupDataCallResult4.pduSessionId;
            convertHalQos = convertHalQos(setupDataCallResult4.defaultQos);
            Object obj3 = (List) setupDataCallResult4.qosSessions.stream().map(new Function() { // from class: com.android.internal.telephony.RILUtils$$ExternalSyntheticLambda5
                @Override // java.util.function.Function
                public final Object apply(Object obj4) {
                    QosBearerSession convertHalQosBearerSession;
                    convertHalQosBearerSession = RILUtils.convertHalQosBearerSession((QosSession) obj4);
                    return convertHalQosBearerSession;
                }
            }).collect(Collectors.toList());
            convertHalSliceInfo = setupDataCallResult4.sliceInfo.getDiscriminator() == 0 ? null : convertHalSliceInfo(setupDataCallResult4.sliceInfo.value());
            Iterator it2 = setupDataCallResult4.trafficDescriptors.iterator();
            while (it2.hasNext()) {
                try {
                    arrayList7.add(convertHalTrafficDescriptor((android.hardware.radio.V1_6.TrafficDescriptor) it2.next()));
                    obj2 = obj3;
                    it = it2;
                } catch (IllegalArgumentException e) {
                    obj2 = obj3;
                    StringBuilder sb = new StringBuilder();
                    it = it2;
                    sb.append("convertHalDataCallResult: Failed to convert traffic descriptor. e=");
                    sb.append(e);
                    loge(sb.toString());
                }
                obj3 = obj2;
                it2 = it;
            }
            ArrayList arrayList8 = obj3;
            strArr = strArr12;
            str = str3;
            strArr2 = strArr11;
            strArr3 = strArr13;
            i2 = max;
            i3 = i33;
            j = j3;
            i4 = i32;
            list = list2;
            i5 = i34;
            arrayList = arrayList8;
            b = b2;
            arrayList2 = arrayList7;
            i6 = i30;
            i7 = i29;
            i8 = i31;
            ArrayList arrayList9 = new ArrayList();
            int i35 = i;
            if (strArr2 == null) {
                i11 = i3;
                int length3 = strArr2.length;
                i10 = i2;
                int i36 = 0;
                while (i36 < length3) {
                    String[] strArr14 = strArr2;
                    String trim = strArr2[i36].trim();
                    try {
                        arrayList9.add(InetAddresses.parseNumericAddress(trim));
                        arrayList4 = arrayList9;
                        i14 = length3;
                    } catch (IllegalArgumentException e2) {
                        i14 = length3;
                        StringBuilder sb2 = new StringBuilder();
                        arrayList4 = arrayList9;
                        sb2.append("Unknown dns: ");
                        sb2.append(trim);
                        Rlog.e("RILUtils", sb2.toString(), e2);
                    }
                    i36++;
                    strArr2 = strArr14;
                    length3 = i14;
                    arrayList9 = arrayList4;
                }
                arrayList3 = arrayList9;
            } else {
                i10 = i2;
                arrayList3 = arrayList9;
                i11 = i3;
            }
            ArrayList arrayList10 = new ArrayList();
            if (strArr != null) {
                int length4 = strArr.length;
                int i37 = 0;
                while (i37 < length4) {
                    String trim2 = strArr[i37].trim();
                    try {
                        arrayList10.add(InetAddresses.parseNumericAddress(trim2));
                        strArr6 = strArr;
                        i13 = length4;
                    } catch (IllegalArgumentException e3) {
                        strArr6 = strArr;
                        StringBuilder sb3 = new StringBuilder();
                        i13 = length4;
                        sb3.append("Unknown gateway: ");
                        sb3.append(trim2);
                        Rlog.e("RILUtils", sb3.toString(), e3);
                    }
                    i37++;
                    strArr = strArr6;
                    length4 = i13;
                }
            }
            ArrayList arrayList11 = new ArrayList();
            if (strArr3 != null) {
                int length5 = strArr3.length;
                int i38 = 0;
                while (i38 < length5) {
                    String trim3 = strArr3[i38].trim();
                    try {
                        arrayList11.add(InetAddresses.parseNumericAddress(trim3));
                        strArr5 = strArr3;
                        i12 = length5;
                    } catch (IllegalArgumentException e4) {
                        strArr5 = strArr3;
                        StringBuilder sb4 = new StringBuilder();
                        i12 = length5;
                        sb4.append("Unknown pcscf: ");
                        sb4.append(trim3);
                        Rlog.e("RILUtils", sb4.toString(), e4);
                    }
                    i38++;
                    strArr3 = strArr5;
                    length5 = i12;
                }
            }
            return new DataCallResponse.Builder().setCause(i7).setRetryDurationMillis(j).setId(i6).setLinkStatus(i8).setProtocolType(i4).setInterfaceName(str).setAddresses(list).setDnsAddresses(arrayList3).setGatewayAddresses(arrayList10).setPcscfAddresses(arrayList11).setMtu(i10).setMtuV4(i11).setMtuV6(i35).setHandoverFailureMode(b).setPduSessionId(i5).setDefaultQos(convertHalQos).setQosBearerSessions(arrayList).setSliceInfo(convertHalSliceInfo).setTrafficDescriptors(arrayList2).build();
        } else {
            loge("Unsupported SetupDataCallResult " + obj);
            return null;
        }
        long j4 = j2;
        list = arrayList5;
        i4 = i9;
        str = str2;
        strArr2 = strArr4;
        j = j4;
        ArrayList arrayList92 = new ArrayList();
        int i352 = i;
        if (strArr2 == null) {
        }
        ArrayList arrayList102 = new ArrayList();
        if (strArr != null) {
        }
        ArrayList arrayList112 = new ArrayList();
        if (strArr3 != null) {
        }
        return new DataCallResponse.Builder().setCause(i7).setRetryDurationMillis(j).setId(i6).setLinkStatus(i8).setProtocolType(i4).setInterfaceName(str).setAddresses(list).setDnsAddresses(arrayList3).setGatewayAddresses(arrayList102).setPcscfAddresses(arrayList112).setMtu(i10).setMtuV4(i11).setMtuV6(i352).setHandoverFailureMode(b).setPduSessionId(i5).setDefaultQos(convertHalQos).setQosBearerSessions(arrayList).setSliceInfo(convertHalSliceInfo).setTrafficDescriptors(arrayList2).build();
    }

    public static /* synthetic */ android.net.LinkAddress lambda$convertHalDataCallResult$0(LinkAddress linkAddress) {
        return convertToLinkAddress(linkAddress.address, linkAddress.properties, linkAddress.deprecationTime, linkAddress.expirationTime);
    }

    public static /* synthetic */ android.net.LinkAddress lambda$convertHalDataCallResult$1(LinkAddress linkAddress) {
        return convertToLinkAddress(linkAddress.address, linkAddress.properties, linkAddress.deprecationTime, linkAddress.expirationTime);
    }

    @VisibleForTesting
    public static DataCallResponse convertHalDataCallResult(android.hardware.radio.data.SetupDataCallResult setupDataCallResult) {
        android.hardware.radio.data.LinkAddress[] linkAddressArr;
        if (setupDataCallResult == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (android.hardware.radio.data.LinkAddress linkAddress : setupDataCallResult.addresses) {
            arrayList.add(convertToLinkAddress(linkAddress.address, linkAddress.addressProperties, linkAddress.deprecationTime, linkAddress.expirationTime));
        }
        ArrayList arrayList2 = new ArrayList();
        String[] strArr = setupDataCallResult.dnses;
        if (strArr != null) {
            for (String str : strArr) {
                String trim = str.trim();
                try {
                    arrayList2.add(InetAddresses.parseNumericAddress(trim));
                } catch (IllegalArgumentException e) {
                    Rlog.e("RILUtils", "Unknown dns: " + trim, e);
                }
            }
        }
        ArrayList arrayList3 = new ArrayList();
        String[] strArr2 = setupDataCallResult.gateways;
        if (strArr2 != null) {
            for (String str2 : strArr2) {
                String trim2 = str2.trim();
                try {
                    arrayList3.add(InetAddresses.parseNumericAddress(trim2));
                } catch (IllegalArgumentException e2) {
                    Rlog.e("RILUtils", "Unknown gateway: " + trim2, e2);
                }
            }
        }
        ArrayList arrayList4 = new ArrayList();
        String[] strArr3 = setupDataCallResult.pcscf;
        if (strArr3 != null) {
            for (String str3 : strArr3) {
                String trim3 = str3.trim();
                try {
                    arrayList4.add(InetAddresses.parseNumericAddress(trim3));
                } catch (IllegalArgumentException e3) {
                    Rlog.e("RILUtils", "Unknown pcscf: " + trim3, e3);
                }
            }
        }
        ArrayList arrayList5 = new ArrayList();
        for (android.hardware.radio.data.QosSession qosSession : setupDataCallResult.qosSessions) {
            arrayList5.add(convertHalQosBearerSession(qosSession));
        }
        ArrayList arrayList6 = new ArrayList();
        for (android.hardware.radio.data.TrafficDescriptor trafficDescriptor : setupDataCallResult.trafficDescriptors) {
            try {
                arrayList6.add(convertHalTrafficDescriptor(trafficDescriptor));
            } catch (IllegalArgumentException e4) {
                loge("convertHalDataCallResult: Failed to convert traffic descriptor. e=" + e4);
            }
        }
        DataCallResponse.Builder qosBearerSessions = new DataCallResponse.Builder().setCause(setupDataCallResult.cause).setRetryDurationMillis(setupDataCallResult.suggestedRetryTime).setId(setupDataCallResult.cid).setLinkStatus(setupDataCallResult.active).setProtocolType(setupDataCallResult.type).setInterfaceName(setupDataCallResult.ifname).setAddresses(arrayList).setDnsAddresses(arrayList2).setGatewayAddresses(arrayList3).setPcscfAddresses(arrayList4).setMtu(Math.max(setupDataCallResult.mtuV4, setupDataCallResult.mtuV6)).setMtuV4(setupDataCallResult.mtuV4).setMtuV6(setupDataCallResult.mtuV6).setHandoverFailureMode(setupDataCallResult.handoverFailureMode).setPduSessionId(setupDataCallResult.pduSessionId).setDefaultQos(convertHalQos(setupDataCallResult.defaultQos)).setQosBearerSessions(arrayList5);
        android.hardware.radio.data.SliceInfo sliceInfo = setupDataCallResult.sliceInfo;
        return qosBearerSessions.setSliceInfo(sliceInfo != null ? convertHalSliceInfo(sliceInfo) : null).setTrafficDescriptors(arrayList6).build();
    }

    public static NetworkSliceInfo convertHalSliceInfo(SliceInfo sliceInfo) {
        NetworkSliceInfo.Builder mappedHplmnSliceServiceType = new NetworkSliceInfo.Builder().setSliceServiceType(sliceInfo.sst).setMappedHplmnSliceServiceType(sliceInfo.mappedHplmnSst);
        int i = sliceInfo.sliceDifferentiator;
        if (i != -1) {
            mappedHplmnSliceServiceType.setSliceDifferentiator(i).setMappedHplmnSliceDifferentiator(sliceInfo.mappedHplmnSD);
        }
        return mappedHplmnSliceServiceType.build();
    }

    private static NetworkSliceInfo convertHalSliceInfo(android.hardware.radio.data.SliceInfo sliceInfo) {
        NetworkSliceInfo.Builder mappedHplmnSliceServiceType = new NetworkSliceInfo.Builder().setSliceServiceType(sliceInfo.sliceServiceType).setMappedHplmnSliceServiceType(sliceInfo.mappedHplmnSst);
        int i = sliceInfo.sliceDifferentiator;
        if (i != -1) {
            mappedHplmnSliceServiceType.setSliceDifferentiator(i).setMappedHplmnSliceDifferentiator(sliceInfo.mappedHplmnSd);
        }
        return mappedHplmnSliceServiceType.build();
    }

    private static TrafficDescriptor convertHalTrafficDescriptor(android.hardware.radio.V1_6.TrafficDescriptor trafficDescriptor) throws IllegalArgumentException {
        String value = trafficDescriptor.dnn.getDiscriminator() == 0 ? null : trafficDescriptor.dnn.value();
        byte[] arrayListToPrimitiveArray = trafficDescriptor.osAppId.getDiscriminator() != 0 ? arrayListToPrimitiveArray(trafficDescriptor.osAppId.value().osAppId) : null;
        TrafficDescriptor.Builder builder = new TrafficDescriptor.Builder();
        if (value != null) {
            builder.setDataNetworkName(value);
        }
        if (arrayListToPrimitiveArray != null) {
            builder.setOsAppId(arrayListToPrimitiveArray);
        }
        return builder.build();
    }

    private static TrafficDescriptor convertHalTrafficDescriptor(android.hardware.radio.data.TrafficDescriptor trafficDescriptor) throws IllegalArgumentException {
        String str = trafficDescriptor.dnn;
        android.hardware.radio.data.OsAppId osAppId = trafficDescriptor.osAppId;
        byte[] bArr = osAppId == null ? null : osAppId.osAppId;
        TrafficDescriptor.Builder builder = new TrafficDescriptor.Builder();
        if (str != null) {
            builder.setDataNetworkName(str);
        }
        if (bArr != null) {
            builder.setOsAppId(bArr);
        }
        return builder.build();
    }

    public static NetworkSlicingConfig convertHalSlicingConfig(SlicingConfig slicingConfig) {
        return new NetworkSlicingConfig((List) slicingConfig.urspRules.stream().map(new Function() { // from class: com.android.internal.telephony.RILUtils$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                UrspRule lambda$convertHalSlicingConfig$4;
                lambda$convertHalSlicingConfig$4 = RILUtils.lambda$convertHalSlicingConfig$4((android.hardware.radio.V1_6.UrspRule) obj);
                return lambda$convertHalSlicingConfig$4;
            }
        }).collect(Collectors.toList()), (List) slicingConfig.sliceInfo.stream().map(new RILUtils$$ExternalSyntheticLambda1()).collect(Collectors.toList()));
    }

    public static /* synthetic */ UrspRule lambda$convertHalSlicingConfig$4(android.hardware.radio.V1_6.UrspRule urspRule) {
        return new UrspRule(urspRule.precedence, (List) urspRule.trafficDescriptors.stream().map(new Function() { // from class: com.android.internal.telephony.RILUtils$$ExternalSyntheticLambda7
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                TrafficDescriptor lambda$convertHalSlicingConfig$2;
                lambda$convertHalSlicingConfig$2 = RILUtils.lambda$convertHalSlicingConfig$2((android.hardware.radio.V1_6.TrafficDescriptor) obj);
                return lambda$convertHalSlicingConfig$2;
            }
        }).filter(new Predicate() { // from class: com.android.internal.telephony.RILUtils$$ExternalSyntheticLambda8
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Objects.nonNull((TrafficDescriptor) obj);
            }
        }).collect(Collectors.toList()), (List) urspRule.routeSelectionDescriptor.stream().map(new Function() { // from class: com.android.internal.telephony.RILUtils$$ExternalSyntheticLambda9
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                RouteSelectionDescriptor lambda$convertHalSlicingConfig$3;
                lambda$convertHalSlicingConfig$3 = RILUtils.lambda$convertHalSlicingConfig$3((android.hardware.radio.V1_6.RouteSelectionDescriptor) obj);
                return lambda$convertHalSlicingConfig$3;
            }
        }).collect(Collectors.toList()));
    }

    public static /* synthetic */ TrafficDescriptor lambda$convertHalSlicingConfig$2(android.hardware.radio.V1_6.TrafficDescriptor trafficDescriptor) {
        try {
            return convertHalTrafficDescriptor(trafficDescriptor);
        } catch (IllegalArgumentException e) {
            loge("convertHalSlicingConfig: Failed to convert traffic descriptor. e=" + e);
            return null;
        }
    }

    public static /* synthetic */ RouteSelectionDescriptor lambda$convertHalSlicingConfig$3(android.hardware.radio.V1_6.RouteSelectionDescriptor routeSelectionDescriptor) {
        return new RouteSelectionDescriptor(routeSelectionDescriptor.precedence, routeSelectionDescriptor.sessionType.value(), routeSelectionDescriptor.sscMode.value(), (List) routeSelectionDescriptor.sliceInfo.stream().map(new RILUtils$$ExternalSyntheticLambda1()).collect(Collectors.toList()), routeSelectionDescriptor.dnn);
    }

    public static NetworkSlicingConfig convertHalSlicingConfig(android.hardware.radio.data.SlicingConfig slicingConfig) {
        ArrayList arrayList = new ArrayList();
        android.hardware.radio.data.UrspRule[] urspRuleArr = slicingConfig.urspRules;
        int length = urspRuleArr.length;
        int i = 0;
        while (i < length) {
            android.hardware.radio.data.UrspRule urspRule = urspRuleArr[i];
            ArrayList arrayList2 = new ArrayList();
            for (android.hardware.radio.data.TrafficDescriptor trafficDescriptor : urspRule.trafficDescriptors) {
                try {
                    arrayList2.add(convertHalTrafficDescriptor(trafficDescriptor));
                } catch (IllegalArgumentException e) {
                    loge("convertHalTrafficDescriptor: " + e);
                }
            }
            ArrayList arrayList3 = new ArrayList();
            android.hardware.radio.data.RouteSelectionDescriptor[] routeSelectionDescriptorArr = urspRule.routeSelectionDescriptor;
            int length2 = routeSelectionDescriptorArr.length;
            int i2 = 0;
            while (i2 < length2) {
                android.hardware.radio.data.RouteSelectionDescriptor routeSelectionDescriptor = routeSelectionDescriptorArr[i2];
                ArrayList arrayList4 = new ArrayList();
                android.hardware.radio.data.SliceInfo[] sliceInfoArr = routeSelectionDescriptor.sliceInfo;
                int length3 = sliceInfoArr.length;
                int i3 = 0;
                while (i3 < length3) {
                    arrayList4.add(convertHalSliceInfo(sliceInfoArr[i3]));
                    i3++;
                    urspRuleArr = urspRuleArr;
                }
                arrayList3.add(new RouteSelectionDescriptor(routeSelectionDescriptor.precedence, routeSelectionDescriptor.sessionType, routeSelectionDescriptor.sscMode, arrayList4, primitiveArrayToArrayList(routeSelectionDescriptor.dnn)));
                i2++;
                urspRuleArr = urspRuleArr;
            }
            arrayList.add(new UrspRule(urspRule.precedence, arrayList2, arrayList3));
            i++;
            urspRuleArr = urspRuleArr;
        }
        ArrayList arrayList5 = new ArrayList();
        for (android.hardware.radio.data.SliceInfo sliceInfo : slicingConfig.sliceInfo) {
            arrayList5.add(convertHalSliceInfo(sliceInfo));
        }
        return new NetworkSlicingConfig(arrayList, arrayList5);
    }

    private static Qos.QosBandwidth convertHalQosBandwidth(QosBandwidth qosBandwidth) {
        return new Qos.QosBandwidth(qosBandwidth.maxBitrateKbps, qosBandwidth.guaranteedBitrateKbps);
    }

    private static Qos.QosBandwidth convertHalQosBandwidth(android.hardware.radio.data.QosBandwidth qosBandwidth) {
        return new Qos.QosBandwidth(qosBandwidth.maxBitrateKbps, qosBandwidth.guaranteedBitrateKbps);
    }

    private static Qos convertHalQos(android.hardware.radio.V1_6.Qos qos) {
        byte discriminator = qos.getDiscriminator();
        if (discriminator == 1) {
            EpsQos eps = qos.eps();
            return new android.telephony.data.EpsQos(convertHalQosBandwidth(eps.downlink), convertHalQosBandwidth(eps.uplink), eps.qci);
        } else if (discriminator != 2) {
            return null;
        } else {
            NrQos nr = qos.nr();
            return new android.telephony.data.NrQos(convertHalQosBandwidth(nr.downlink), convertHalQosBandwidth(nr.uplink), nr.qfi, nr.fiveQi, nr.averagingWindowMs);
        }
    }

    private static Qos convertHalQos(android.hardware.radio.data.Qos qos) {
        int tag = qos.getTag();
        if (tag == 1) {
            android.hardware.radio.data.EpsQos eps = qos.getEps();
            return new android.telephony.data.EpsQos(convertHalQosBandwidth(eps.downlink), convertHalQosBandwidth(eps.uplink), eps.qci);
        } else if (tag != 2) {
            return null;
        } else {
            android.hardware.radio.data.NrQos nr = qos.getNr();
            int i = nr.averagingWindowMillis;
            if (i == -1) {
                i = nr.averagingWindowMs;
            }
            return new android.telephony.data.NrQos(convertHalQosBandwidth(nr.downlink), convertHalQosBandwidth(nr.uplink), nr.qfi, nr.fiveQi, i);
        }
    }

    private static QosBearerFilter convertHalQosBearerFilter(QosFilter qosFilter) {
        QosBearerFilter.PortRange portRange;
        ArrayList arrayList = new ArrayList();
        String[] strArr = (String[]) qosFilter.localAddresses.toArray(new String[0]);
        if (strArr != null) {
            for (String str : strArr) {
                arrayList.add(convertToLinkAddress(str));
            }
        }
        ArrayList arrayList2 = new ArrayList();
        String[] strArr2 = (String[]) qosFilter.remoteAddresses.toArray(new String[0]);
        if (strArr2 != null) {
            for (String str2 : strArr2) {
                arrayList2.add(convertToLinkAddress(str2));
            }
        }
        MaybePort maybePort = qosFilter.localPort;
        QosBearerFilter.PortRange portRange2 = null;
        if (maybePort == null || maybePort.getDiscriminator() != 1) {
            portRange = null;
        } else {
            PortRange range = qosFilter.localPort.range();
            portRange = new QosBearerFilter.PortRange(range.start, range.end);
        }
        MaybePort maybePort2 = qosFilter.remotePort;
        if (maybePort2 != null && maybePort2.getDiscriminator() == 1) {
            PortRange range2 = qosFilter.remotePort.range();
            portRange2 = new QosBearerFilter.PortRange(range2.start, range2.end);
        }
        QosFilter.TypeOfService typeOfService = qosFilter.tos;
        byte value = (typeOfService == null || typeOfService.getDiscriminator() != 1) ? (byte) -1 : qosFilter.tos.value();
        QosFilter.Ipv6FlowLabel ipv6FlowLabel = qosFilter.flowLabel;
        long value2 = (ipv6FlowLabel == null || ipv6FlowLabel.getDiscriminator() != 1) ? -1L : qosFilter.flowLabel.value();
        QosFilter.IpsecSpi ipsecSpi = qosFilter.spi;
        return new QosBearerFilter(arrayList, arrayList2, portRange, portRange2, qosFilter.protocol, value, value2, (ipsecSpi == null || ipsecSpi.getDiscriminator() != 1) ? -1L : qosFilter.spi.value(), qosFilter.direction, qosFilter.precedence);
    }

    private static QosBearerFilter convertHalQosBearerFilter(android.hardware.radio.data.QosFilter qosFilter) {
        QosBearerFilter.PortRange portRange;
        QosBearerFilter.PortRange portRange2;
        ArrayList arrayList = new ArrayList();
        String[] strArr = qosFilter.localAddresses;
        if (strArr != null) {
            for (String str : strArr) {
                arrayList.add(convertToLinkAddress(str));
            }
        }
        ArrayList arrayList2 = new ArrayList();
        String[] strArr2 = qosFilter.remoteAddresses;
        if (strArr2 != null) {
            for (String str2 : strArr2) {
                arrayList2.add(convertToLinkAddress(str2));
            }
        }
        if (qosFilter.localPort != null) {
            android.hardware.radio.data.PortRange portRange3 = qosFilter.localPort;
            portRange = new QosBearerFilter.PortRange(portRange3.start, portRange3.end);
        } else {
            portRange = null;
        }
        if (qosFilter.remotePort != null) {
            android.hardware.radio.data.PortRange portRange4 = qosFilter.remotePort;
            portRange2 = new QosBearerFilter.PortRange(portRange4.start, portRange4.end);
        } else {
            portRange2 = null;
        }
        QosFilterTypeOfService qosFilterTypeOfService = qosFilter.tos;
        int i = (qosFilterTypeOfService == null || qosFilterTypeOfService.getTag() != 1) ? -1 : 1;
        QosFilterIpv6FlowLabel qosFilterIpv6FlowLabel = qosFilter.flowLabel;
        long j = -1;
        long j2 = (qosFilterIpv6FlowLabel == null || qosFilterIpv6FlowLabel.getTag() != 1) ? -1L : 1L;
        QosFilterIpsecSpi qosFilterIpsecSpi = qosFilter.spi;
        if (qosFilterIpsecSpi != null && qosFilterIpsecSpi.getTag() == 1) {
            j = 1;
        }
        return new QosBearerFilter(arrayList, arrayList2, portRange, portRange2, qosFilter.protocol, i, j2, j, qosFilter.direction, qosFilter.precedence);
    }

    public static QosBearerSession convertHalQosBearerSession(QosSession qosSession) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = qosSession.qosFilters;
        if (arrayList2 != null) {
            Iterator it = arrayList2.iterator();
            while (it.hasNext()) {
                arrayList.add(convertHalQosBearerFilter((QosFilter) it.next()));
            }
        }
        return new QosBearerSession(qosSession.qosSessionId, convertHalQos(qosSession.qos), arrayList);
    }

    private static QosBearerSession convertHalQosBearerSession(android.hardware.radio.data.QosSession qosSession) {
        ArrayList arrayList = new ArrayList();
        android.hardware.radio.data.QosFilter[] qosFilterArr = qosSession.qosFilters;
        if (qosFilterArr != null) {
            for (android.hardware.radio.data.QosFilter qosFilter : qosFilterArr) {
                arrayList.add(convertHalQosBearerFilter(qosFilter));
            }
        }
        return new QosBearerSession(qosSession.qosSessionId, convertHalQos(qosSession.qos), arrayList);
    }

    @VisibleForTesting
    public static ArrayList<DataCallResponse> convertHalDataCallResultList(List<? extends Object> list) {
        ArrayList<DataCallResponse> arrayList = new ArrayList<>(list.size());
        for (Object obj : list) {
            arrayList.add(convertHalDataCallResult(obj));
        }
        return arrayList;
    }

    @VisibleForTesting
    public static ArrayList<DataCallResponse> convertHalDataCallResultList(android.hardware.radio.data.SetupDataCallResult[] setupDataCallResultArr) {
        ArrayList<DataCallResponse> arrayList = new ArrayList<>(setupDataCallResultArr.length);
        for (android.hardware.radio.data.SetupDataCallResult setupDataCallResult : setupDataCallResultArr) {
            arrayList.add(convertHalDataCallResult(setupDataCallResult));
        }
        return arrayList;
    }

    public static int convertHalRadioState(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i == 10) {
                    return 1;
                }
                throw new RuntimeException("Unrecognized RadioState: " + i);
            }
            return 2;
        }
        return 0;
    }

    public static DriverCall convertToDriverCall(Object obj) {
        android.hardware.radio.V1_0.Call call;
        android.hardware.radio.V1_6.Call call2;
        android.hardware.radio.V1_2.Call call3;
        DriverCall driverCall = new DriverCall();
        if (obj instanceof android.hardware.radio.V1_6.Call) {
            call2 = (android.hardware.radio.V1_6.Call) obj;
            call3 = call2.base;
            call = call3.base;
        } else {
            call = null;
            if (obj instanceof android.hardware.radio.V1_2.Call) {
                call3 = (android.hardware.radio.V1_2.Call) obj;
                call = call3.base;
                call2 = null;
            } else if (obj instanceof android.hardware.radio.V1_0.Call) {
                call3 = null;
                call = (android.hardware.radio.V1_0.Call) obj;
                call2 = null;
            } else {
                call2 = null;
                call3 = null;
            }
        }
        if (call != null) {
            driverCall.state = DriverCall.stateFromCLCC(call.state);
            driverCall.index = call.index;
            driverCall.TOA = call.toa;
            driverCall.isMpty = call.isMpty;
            driverCall.isMT = call.isMT;
            driverCall.als = call.als;
            driverCall.isVoice = call.isVoice;
            driverCall.isVoicePrivacy = call.isVoicePrivacy;
            driverCall.number = call.number;
            driverCall.numberPresentation = DriverCall.presentationFromCLIP(call.numberPresentation);
            driverCall.name = call.name;
            driverCall.namePresentation = DriverCall.presentationFromCLIP(call.namePresentation);
            if (call.uusInfo.size() == 1) {
                UUSInfo uUSInfo = new UUSInfo();
                driverCall.uusInfo = uUSInfo;
                uUSInfo.setType(((UusInfo) call.uusInfo.get(0)).uusType);
                driverCall.uusInfo.setDcs(((UusInfo) call.uusInfo.get(0)).uusDcs);
                if (!TextUtils.isEmpty(((UusInfo) call.uusInfo.get(0)).uusData)) {
                    driverCall.uusInfo.setUserData(((UusInfo) call.uusInfo.get(0)).uusData.getBytes());
                }
            }
            driverCall.number = PhoneNumberUtils.stringFromStringAndTOA(driverCall.number, driverCall.TOA);
        }
        if (call3 != null) {
            driverCall.audioQuality = call3.audioQuality;
        }
        if (call2 != null) {
            driverCall.forwardedNumber = call2.forwardedNumber;
        }
        return driverCall;
    }

    public static DriverCall convertToDriverCall(android.hardware.radio.voice.Call call) {
        DriverCall driverCall = new DriverCall();
        driverCall.state = DriverCall.stateFromCLCC(call.state);
        driverCall.index = call.index;
        driverCall.TOA = call.toa;
        driverCall.isMpty = call.isMpty;
        driverCall.isMT = call.isMT;
        driverCall.als = call.als;
        driverCall.isVoice = call.isVoice;
        driverCall.isVoicePrivacy = call.isVoicePrivacy;
        driverCall.number = call.number;
        driverCall.numberPresentation = DriverCall.presentationFromCLIP(call.numberPresentation);
        driverCall.name = call.name;
        driverCall.namePresentation = DriverCall.presentationFromCLIP(call.namePresentation);
        if (call.uusInfo.length == 1) {
            UUSInfo uUSInfo = new UUSInfo();
            driverCall.uusInfo = uUSInfo;
            uUSInfo.setType(call.uusInfo[0].uusType);
            driverCall.uusInfo.setDcs(call.uusInfo[0].uusDcs);
            if (!TextUtils.isEmpty(call.uusInfo[0].uusData)) {
                driverCall.uusInfo.setUserData(call.uusInfo[0].uusData.getBytes());
            }
        }
        driverCall.number = PhoneNumberUtils.stringFromStringAndTOA(driverCall.number, driverCall.TOA);
        driverCall.audioQuality = call.audioQuality;
        driverCall.forwardedNumber = call.forwardedNumber;
        return driverCall;
    }

    public static List<CarrierIdentifier> convertHalCarrierList(List<Carrier> list) {
        String str;
        String str2;
        String str3;
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            String str4 = list.get(i).mcc;
            String str5 = list.get(i).mnc;
            int i2 = list.get(i).matchType;
            String str6 = list.get(i).matchData;
            String str7 = null;
            if (i2 == 1) {
                str = str6;
                str2 = null;
            } else {
                if (i2 == 2) {
                    str = null;
                    str2 = null;
                    str3 = null;
                    str7 = str6;
                } else if (i2 == 3) {
                    str2 = str6;
                    str = null;
                    str3 = null;
                } else if (i2 == 4) {
                    str3 = str6;
                    str = null;
                    str2 = null;
                } else {
                    str = null;
                    str2 = null;
                }
                arrayList.add(new CarrierIdentifier(str4, str5, str, str7, str2, str3));
            }
            str3 = str2;
            arrayList.add(new CarrierIdentifier(str4, str5, str, str7, str2, str3));
        }
        return arrayList;
    }

    public static List<CarrierIdentifier> convertHalCarrierList(android.hardware.radio.sim.Carrier[] carrierArr) {
        String str;
        String str2;
        String str3;
        ArrayList arrayList = new ArrayList();
        for (android.hardware.radio.sim.Carrier carrier : carrierArr) {
            String str4 = carrier.mcc;
            String str5 = carrier.mnc;
            int i = carrier.matchType;
            String str6 = carrier.matchData;
            String str7 = null;
            if (i == 1) {
                str = str6;
                str2 = null;
            } else {
                if (i == 2) {
                    str = null;
                    str2 = null;
                    str3 = null;
                    str7 = str6;
                } else if (i == 3) {
                    str2 = str6;
                    str = null;
                    str3 = null;
                } else if (i == 4) {
                    str3 = str6;
                    str = null;
                    str2 = null;
                } else {
                    str = null;
                    str2 = null;
                }
                arrayList.add(new CarrierIdentifier(str4, str5, str, str7, str2, str3));
            }
            str3 = str2;
            arrayList.add(new CarrierIdentifier(str4, str5, str, str7, str2, str3));
        }
        return arrayList;
    }

    public static IccCardStatus convertHalCardStatus(Object obj) {
        CardStatus cardStatus;
        android.hardware.radio.V1_0.CardStatus cardStatus2;
        if (obj instanceof CardStatus) {
            cardStatus = (CardStatus) obj;
            cardStatus2 = cardStatus.base.base.base;
        } else if (obj instanceof android.hardware.radio.V1_0.CardStatus) {
            cardStatus2 = (android.hardware.radio.V1_0.CardStatus) obj;
            cardStatus = null;
        } else {
            cardStatus = null;
            cardStatus2 = null;
        }
        IccCardStatus iccCardStatus = new IccCardStatus();
        if (cardStatus2 != null) {
            iccCardStatus.setCardState(cardStatus2.cardState);
            iccCardStatus.setUniversalPinState(cardStatus2.universalPinState);
            iccCardStatus.mGsmUmtsSubscriptionAppIndex = cardStatus2.gsmUmtsSubscriptionAppIndex;
            iccCardStatus.mCdmaSubscriptionAppIndex = cardStatus2.cdmaSubscriptionAppIndex;
            iccCardStatus.mImsSubscriptionAppIndex = cardStatus2.imsSubscriptionAppIndex;
            int size = cardStatus2.applications.size();
            if (size > 8) {
                size = 8;
            }
            iccCardStatus.mApplications = new IccCardApplicationStatus[size];
            for (int i = 0; i < size; i++) {
                AppStatus appStatus = (AppStatus) cardStatus2.applications.get(i);
                IccCardApplicationStatus iccCardApplicationStatus = new IccCardApplicationStatus();
                iccCardApplicationStatus.app_type = iccCardApplicationStatus.AppTypeFromRILInt(appStatus.appType);
                iccCardApplicationStatus.app_state = iccCardApplicationStatus.AppStateFromRILInt(appStatus.appState);
                iccCardApplicationStatus.perso_substate = iccCardApplicationStatus.PersoSubstateFromRILInt(appStatus.persoSubstate);
                iccCardApplicationStatus.aid = appStatus.aidPtr;
                iccCardApplicationStatus.app_label = appStatus.appLabelPtr;
                iccCardApplicationStatus.pin1_replaced = appStatus.pin1Replaced != 0;
                iccCardApplicationStatus.pin1 = iccCardApplicationStatus.PinStateFromRILInt(appStatus.pin1);
                iccCardApplicationStatus.pin2 = iccCardApplicationStatus.PinStateFromRILInt(appStatus.pin2);
                iccCardStatus.mApplications[i] = iccCardApplicationStatus;
            }
        }
        if (cardStatus != null) {
            IccSlotPortMapping iccSlotPortMapping = new IccSlotPortMapping();
            android.hardware.radio.V1_4.CardStatus cardStatus3 = cardStatus.base;
            android.hardware.radio.V1_2.CardStatus cardStatus4 = cardStatus3.base;
            iccSlotPortMapping.mPhysicalSlotIndex = cardStatus4.physicalSlotId;
            iccCardStatus.mSlotPortMapping = iccSlotPortMapping;
            iccCardStatus.atr = cardStatus4.atr;
            iccCardStatus.iccid = cardStatus4.iccid;
            iccCardStatus.eid = cardStatus3.eid;
            int size2 = cardStatus.applications.size();
            int i2 = size2 <= 8 ? size2 : 8;
            iccCardStatus.mApplications = new IccCardApplicationStatus[i2];
            for (int i3 = 0; i3 < i2; i3++) {
                android.hardware.radio.V1_5.AppStatus appStatus2 = (android.hardware.radio.V1_5.AppStatus) cardStatus.applications.get(i3);
                IccCardApplicationStatus iccCardApplicationStatus2 = new IccCardApplicationStatus();
                iccCardApplicationStatus2.app_type = iccCardApplicationStatus2.AppTypeFromRILInt(appStatus2.base.appType);
                iccCardApplicationStatus2.app_state = iccCardApplicationStatus2.AppStateFromRILInt(appStatus2.base.appState);
                iccCardApplicationStatus2.perso_substate = iccCardApplicationStatus2.PersoSubstateFromRILInt(appStatus2.persoSubstate);
                AppStatus appStatus3 = appStatus2.base;
                iccCardApplicationStatus2.aid = appStatus3.aidPtr;
                iccCardApplicationStatus2.app_label = appStatus3.appLabelPtr;
                iccCardApplicationStatus2.pin1_replaced = appStatus3.pin1Replaced != 0;
                iccCardApplicationStatus2.pin1 = iccCardApplicationStatus2.PinStateFromRILInt(appStatus3.pin1);
                iccCardApplicationStatus2.pin2 = iccCardApplicationStatus2.PinStateFromRILInt(appStatus2.base.pin2);
                iccCardStatus.mApplications[i3] = iccCardApplicationStatus2;
            }
        }
        return iccCardStatus;
    }

    public static IccCardStatus convertHalCardStatus(android.hardware.radio.sim.CardStatus cardStatus) {
        IccCardStatus iccCardStatus = new IccCardStatus();
        iccCardStatus.setCardState(cardStatus.cardState);
        iccCardStatus.setMultipleEnabledProfilesMode(cardStatus.supportedMepMode);
        iccCardStatus.setUniversalPinState(cardStatus.universalPinState);
        iccCardStatus.mGsmUmtsSubscriptionAppIndex = cardStatus.gsmUmtsSubscriptionAppIndex;
        iccCardStatus.mCdmaSubscriptionAppIndex = cardStatus.cdmaSubscriptionAppIndex;
        iccCardStatus.mImsSubscriptionAppIndex = cardStatus.imsSubscriptionAppIndex;
        iccCardStatus.atr = cardStatus.atr;
        iccCardStatus.iccid = cardStatus.iccid;
        iccCardStatus.eid = cardStatus.eid;
        int min = Math.min(cardStatus.applications.length, 8);
        iccCardStatus.mApplications = new IccCardApplicationStatus[min];
        for (int i = 0; i < min; i++) {
            android.hardware.radio.sim.AppStatus appStatus = cardStatus.applications[i];
            IccCardApplicationStatus iccCardApplicationStatus = new IccCardApplicationStatus();
            iccCardApplicationStatus.app_type = iccCardApplicationStatus.AppTypeFromRILInt(appStatus.appType);
            iccCardApplicationStatus.app_state = iccCardApplicationStatus.AppStateFromRILInt(appStatus.appState);
            iccCardApplicationStatus.perso_substate = iccCardApplicationStatus.PersoSubstateFromRILInt(appStatus.persoSubstate);
            iccCardApplicationStatus.aid = appStatus.aidPtr;
            iccCardApplicationStatus.app_label = appStatus.appLabelPtr;
            iccCardApplicationStatus.pin1_replaced = appStatus.pin1Replaced;
            iccCardApplicationStatus.pin1 = iccCardApplicationStatus.PinStateFromRILInt(appStatus.pin1);
            iccCardApplicationStatus.pin2 = iccCardApplicationStatus.PinStateFromRILInt(appStatus.pin2);
            iccCardStatus.mApplications[i] = iccCardApplicationStatus;
        }
        IccSlotPortMapping iccSlotPortMapping = new IccSlotPortMapping();
        SlotPortMapping slotPortMapping = cardStatus.slotMap;
        int i2 = slotPortMapping.physicalSlotId;
        iccSlotPortMapping.mPhysicalSlotIndex = i2;
        iccSlotPortMapping.mPortIndex = PortUtils.convertFromHalPortIndex(i2, slotPortMapping.portId, iccCardStatus.mCardState, iccCardStatus.mSupportedMepMode);
        iccCardStatus.mSlotPortMapping = iccSlotPortMapping;
        return iccCardStatus;
    }

    public static AdnCapacity convertHalPhonebookCapacity(PhonebookCapacity phonebookCapacity) {
        if (phonebookCapacity != null) {
            return new AdnCapacity(phonebookCapacity.maxAdnRecords, phonebookCapacity.usedAdnRecords, phonebookCapacity.maxEmailRecords, phonebookCapacity.usedEmailRecords, phonebookCapacity.maxAdditionalNumberRecords, phonebookCapacity.usedAdditionalNumberRecords, phonebookCapacity.maxNameLen, phonebookCapacity.maxNumberLen, phonebookCapacity.maxEmailLen, phonebookCapacity.maxAdditionalNumberLen);
        }
        return null;
    }

    public static AdnCapacity convertHalPhonebookCapacity(android.hardware.radio.sim.PhonebookCapacity phonebookCapacity) {
        if (phonebookCapacity != null) {
            return new AdnCapacity(phonebookCapacity.maxAdnRecords, phonebookCapacity.usedAdnRecords, phonebookCapacity.maxEmailRecords, phonebookCapacity.usedEmailRecords, phonebookCapacity.maxAdditionalNumberRecords, phonebookCapacity.usedAdditionalNumberRecords, phonebookCapacity.maxNameLen, phonebookCapacity.maxNumberLen, phonebookCapacity.maxEmailLen, phonebookCapacity.maxAdditionalNumberLen);
        }
        return null;
    }

    public static SimPhonebookRecord convertHalPhonebookRecordInfo(PhonebookRecordInfo phonebookRecordInfo) {
        ArrayList arrayList = phonebookRecordInfo.emails;
        String[] strArr = arrayList == null ? null : (String[]) arrayList.toArray(new String[arrayList.size()]);
        ArrayList arrayList2 = phonebookRecordInfo.additionalNumbers;
        return new SimPhonebookRecord(phonebookRecordInfo.recordId, phonebookRecordInfo.name, phonebookRecordInfo.number, strArr, arrayList2 != null ? (String[]) arrayList2.toArray(new String[arrayList2.size()]) : null);
    }

    public static SimPhonebookRecord convertHalPhonebookRecordInfo(android.hardware.radio.sim.PhonebookRecordInfo phonebookRecordInfo) {
        return new SimPhonebookRecord(phonebookRecordInfo.recordId, phonebookRecordInfo.name, phonebookRecordInfo.number, phonebookRecordInfo.emails, phonebookRecordInfo.additionalNumbers);
    }

    public static PhonebookRecordInfo convertToHalPhonebookRecordInfo(SimPhonebookRecord simPhonebookRecord) {
        if (simPhonebookRecord != null) {
            return simPhonebookRecord.toPhonebookRecordInfo();
        }
        return null;
    }

    public static android.hardware.radio.sim.PhonebookRecordInfo convertToHalPhonebookRecordInfoAidl(SimPhonebookRecord simPhonebookRecord) {
        if (simPhonebookRecord != null) {
            return simPhonebookRecord.toPhonebookRecordInfoAidl();
        }
        return new android.hardware.radio.sim.PhonebookRecordInfo();
    }

    public static ArrayList<IccSlotStatus> convertHalSlotStatus(Object obj) {
        SimSlotStatus[] simSlotStatusArr;
        ArrayList<IccSlotStatus> arrayList = new ArrayList<>();
        try {
            for (SimSlotStatus simSlotStatus : (SimSlotStatus[]) obj) {
                IccSlotStatus iccSlotStatus = new IccSlotStatus();
                iccSlotStatus.setCardState(simSlotStatus.cardState);
                int length = simSlotStatus.portInfo.length;
                iccSlotStatus.mSimPortInfos = new IccSimPortInfo[length];
                for (int i = 0; i < length; i++) {
                    IccSimPortInfo iccSimPortInfo = new IccSimPortInfo();
                    SimPortInfo simPortInfo = simSlotStatus.portInfo[i];
                    iccSimPortInfo.mIccId = simPortInfo.iccId;
                    boolean z = simPortInfo.portActive;
                    iccSimPortInfo.mLogicalSlotIndex = z ? simPortInfo.logicalSlotId : -1;
                    iccSimPortInfo.mPortActive = z;
                    iccSlotStatus.mSimPortInfos[i] = iccSimPortInfo;
                }
                iccSlotStatus.atr = simSlotStatus.atr;
                iccSlotStatus.eid = simSlotStatus.eid;
                iccSlotStatus.setMultipleEnabledProfilesMode(simSlotStatus.supportedMepMode);
                arrayList.add(iccSlotStatus);
            }
            return arrayList;
        } catch (ClassCastException unused) {
            try {
                try {
                    Iterator it = ((ArrayList) obj).iterator();
                    while (it.hasNext()) {
                        android.hardware.radio.config.V1_2.SimSlotStatus simSlotStatus2 = (android.hardware.radio.config.V1_2.SimSlotStatus) it.next();
                        IccSlotStatus iccSlotStatus2 = new IccSlotStatus();
                        iccSlotStatus2.setCardState(simSlotStatus2.base.cardState);
                        iccSlotStatus2.mSimPortInfos = new IccSimPortInfo[1];
                        IccSimPortInfo iccSimPortInfo2 = new IccSimPortInfo();
                        android.hardware.radio.config.V1_0.SimSlotStatus simSlotStatus3 = simSlotStatus2.base;
                        iccSimPortInfo2.mIccId = simSlotStatus3.iccid;
                        boolean z2 = simSlotStatus3.slotState == 1;
                        iccSimPortInfo2.mPortActive = z2;
                        iccSimPortInfo2.mLogicalSlotIndex = z2 ? simSlotStatus3.logicalSlotId : -1;
                        iccSlotStatus2.mSimPortInfos[0] = iccSimPortInfo2;
                        iccSlotStatus2.atr = simSlotStatus3.atr;
                        iccSlotStatus2.eid = simSlotStatus2.eid;
                        arrayList.add(iccSlotStatus2);
                    }
                    return arrayList;
                } catch (ClassCastException unused2) {
                    Iterator it2 = ((ArrayList) obj).iterator();
                    while (it2.hasNext()) {
                        android.hardware.radio.config.V1_0.SimSlotStatus simSlotStatus4 = (android.hardware.radio.config.V1_0.SimSlotStatus) it2.next();
                        IccSlotStatus iccSlotStatus3 = new IccSlotStatus();
                        iccSlotStatus3.setCardState(simSlotStatus4.cardState);
                        iccSlotStatus3.mSimPortInfos = new IccSimPortInfo[1];
                        IccSimPortInfo iccSimPortInfo3 = new IccSimPortInfo();
                        iccSimPortInfo3.mIccId = simSlotStatus4.iccid;
                        boolean z3 = simSlotStatus4.slotState == 1;
                        iccSimPortInfo3.mPortActive = z3;
                        iccSimPortInfo3.mLogicalSlotIndex = z3 ? simSlotStatus4.logicalSlotId : -1;
                        iccSlotStatus3.mSimPortInfos[0] = iccSimPortInfo3;
                        iccSlotStatus3.atr = simSlotStatus4.atr;
                        arrayList.add(iccSlotStatus3);
                    }
                    return arrayList;
                }
            } catch (ClassCastException unused3) {
                return arrayList;
            }
        }
    }

    public static SlotPortMapping[] convertSimSlotsMapping(List<UiccSlotMapping> list) {
        SlotPortMapping[] slotPortMappingArr = new SlotPortMapping[list.size()];
        for (UiccSlotMapping uiccSlotMapping : list) {
            int logicalSlotIndex = uiccSlotMapping.getLogicalSlotIndex();
            SlotPortMapping slotPortMapping = new SlotPortMapping();
            slotPortMappingArr[logicalSlotIndex] = slotPortMapping;
            slotPortMapping.physicalSlotId = uiccSlotMapping.getPhysicalSlotIndex();
            slotPortMappingArr[logicalSlotIndex].portId = PortUtils.convertToHalPortIndex(uiccSlotMapping.getPhysicalSlotIndex(), uiccSlotMapping.getPortIndex());
        }
        return slotPortMappingArr;
    }

    public static ArrayList<Integer> convertSlotMappingToList(List<UiccSlotMapping> list) {
        int[] iArr = new int[list.size()];
        for (UiccSlotMapping uiccSlotMapping : list) {
            iArr[uiccSlotMapping.getLogicalSlotIndex()] = uiccSlotMapping.getPhysicalSlotIndex();
        }
        return primitiveArrayToArrayList(iArr);
    }

    public static PhoneCapability convertHalPhoneCapability(int[] iArr, Object obj) {
        byte b;
        boolean z;
        ArrayList arrayList = new ArrayList();
        if (obj instanceof android.hardware.radio.config.PhoneCapability) {
            android.hardware.radio.config.PhoneCapability phoneCapability = (android.hardware.radio.config.PhoneCapability) obj;
            byte b2 = phoneCapability.maxActiveData;
            boolean z2 = phoneCapability.isInternetLingeringSupported;
            for (byte b3 : phoneCapability.logicalModemIds) {
                arrayList.add(new ModemInfo(b3));
            }
            z = z2;
            b = b2;
        } else if (obj instanceof android.hardware.radio.config.V1_1.PhoneCapability) {
            android.hardware.radio.config.V1_1.PhoneCapability phoneCapability2 = (android.hardware.radio.config.V1_1.PhoneCapability) obj;
            byte b4 = phoneCapability2.maxActiveData;
            boolean z3 = phoneCapability2.isInternetLingeringSupported;
            Iterator<android.hardware.radio.config.V1_1.ModemInfo> it = phoneCapability2.logicalModemList.iterator();
            while (it.hasNext()) {
                arrayList.add(new ModemInfo(it.next().modemId));
            }
            b = b4;
            z = z3;
        } else {
            b = 0;
            z = false;
        }
        return new PhoneCapability(b, b, arrayList, z, iArr);
    }

    public static EmergencyNetworkScanTrigger convertEmergencyNetworkScanTrigger(int[] iArr, int i) {
        int[] iArr2 = new int[iArr.length];
        for (int i2 = 0; i2 < iArr.length; i2++) {
            iArr2[i2] = convertToHalAccessNetworkAidl(iArr[i2]);
        }
        EmergencyNetworkScanTrigger emergencyNetworkScanTrigger = new EmergencyNetworkScanTrigger();
        emergencyNetworkScanTrigger.accessNetwork = iArr2;
        emergencyNetworkScanTrigger.scanType = convertEmergencyScanType(i);
        return emergencyNetworkScanTrigger;
    }

    public static EmergencyRegResult convertHalEmergencyRegResult(android.hardware.radio.network.EmergencyRegResult emergencyRegResult) {
        int i = emergencyRegResult.accessNetwork;
        int convertHalRegState = convertHalRegState(emergencyRegResult.regState);
        int i2 = emergencyRegResult.emcDomain;
        boolean z = emergencyRegResult.isVopsSupported;
        boolean z2 = emergencyRegResult.isEmcBearerSupported;
        byte b = emergencyRegResult.nwProvidedEmc;
        byte b2 = emergencyRegResult.nwProvidedEmf;
        String str = emergencyRegResult.mcc;
        String str2 = emergencyRegResult.mnc;
        return new EmergencyRegResult(i, convertHalRegState, i2, z, z2, b, b2, str, str2, getCountryCodeForMccMnc(str, str2));
    }

    private static String getCountryCodeForMccMnc(String str, String str2) {
        if (TextUtils.isEmpty(str)) {
            return PhoneConfigurationManager.SSSS;
        }
        if (TextUtils.isEmpty(str2)) {
            str2 = ServiceStateTracker.INVALID_MCC;
        }
        return MccTable.geoCountryCodeForMccMnc(MccTable.MccMnc.fromOperatorNumeric(TextUtils.concat(str, str2).toString()));
    }

    public static String accessNetworkTypesToString(int[] iArr) {
        int length = iArr.length;
        StringBuilder sb = new StringBuilder("{");
        if (length > 0) {
            sb.append((String) Arrays.stream(iArr).mapToObj(new IntFunction() { // from class: com.android.internal.telephony.RILUtils$$ExternalSyntheticLambda2
                @Override // java.util.function.IntFunction
                public final Object apply(int i) {
                    String accessNetworkTypeToString;
                    accessNetworkTypeToString = RILUtils.accessNetworkTypeToString(i);
                    return accessNetworkTypeToString;
                }
            }).collect(Collectors.joining(",")));
        }
        sb.append("}");
        return sb.toString();
    }

    public static String accessNetworkTypeToString(int i) {
        switch (i) {
            case 0:
                return "UNKNOWN";
            case 1:
                return "GERAN";
            case 2:
                return "UTRAN";
            case 3:
                return "EUTRAN";
            case 4:
                return "CDMA2000";
            case 5:
                return "IWLAN";
            case 6:
                return "NGRAN";
            default:
                return Integer.toString(i);
        }
    }

    public static void appendPrimitiveArrayToArrayList(byte[] bArr, ArrayList<Byte> arrayList) {
        for (byte b : bArr) {
            arrayList.add(Byte.valueOf(b));
        }
    }

    public static ArrayList<Byte> primitiveArrayToArrayList(byte[] bArr) {
        ArrayList<Byte> arrayList = new ArrayList<>(bArr.length);
        for (byte b : bArr) {
            arrayList.add(Byte.valueOf(b));
        }
        return arrayList;
    }

    public static ArrayList<Integer> primitiveArrayToArrayList(int[] iArr) {
        ArrayList<Integer> arrayList = new ArrayList<>(iArr.length);
        for (int i : iArr) {
            arrayList.add(Integer.valueOf(i));
        }
        return arrayList;
    }

    public static ArrayList<String> primitiveArrayToArrayList(String[] strArr) {
        return new ArrayList<>(Arrays.asList(strArr));
    }

    public static byte[] arrayListToPrimitiveArray(ArrayList<Byte> arrayList) {
        int size = arrayList.size();
        byte[] bArr = new byte[size];
        for (int i = 0; i < size; i++) {
            bArr[i] = arrayList.get(i).byteValue();
        }
        return bArr;
    }

    public static String setupDataReasonToString(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 3) {
                    return "UNKNOWN(" + i + ")";
                }
                return "HANDOVER";
            }
            return "NORMAL";
        }
        return "UNKNOWN";
    }

    public static String deactivateDataReasonToString(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        return "UNKNOWN(" + i + ")";
                    }
                    return "HANDOVER";
                }
                return "SHUTDOWN";
            }
            return "NORMAL";
        }
        return "UNKNOWN";
    }

    public static String requestToString(int i) {
        switch (i) {
            case 1:
                return "GET_SIM_STATUS";
            case 2:
                return "ENTER_SIM_PIN";
            case 3:
                return "ENTER_SIM_PUK";
            case 4:
                return "ENTER_SIM_PIN2";
            case 5:
                return "ENTER_SIM_PUK2";
            case 6:
                return "CHANGE_SIM_PIN";
            case 7:
                return "CHANGE_SIM_PIN2";
            case 8:
                return "ENTER_NETWORK_DEPERSONALIZATION";
            case 9:
                return "GET_CURRENT_CALLS";
            case 10:
                return "DIAL";
            case 11:
                return "GET_IMSI";
            case 12:
                return "HANGUP";
            case 13:
                return "HANGUP_WAITING_OR_BACKGROUND";
            case 14:
                return "HANGUP_FOREGROUND_RESUME_BACKGROUND";
            case 15:
                return "REQUEST_SWITCH_WAITING_OR_HOLDING_AND_ACTIVE";
            case 16:
                return "CONFERENCE";
            case 17:
                return "UDUB";
            case 18:
                return "LAST_CALL_FAIL_CAUSE";
            case 19:
                return "SIGNAL_STRENGTH";
            case 20:
                return "VOICE_REGISTRATION_STATE";
            case 21:
                return "DATA_REGISTRATION_STATE";
            case 22:
                return "OPERATOR";
            case 23:
                return "RADIO_POWER";
            case 24:
                return "DTMF";
            case 25:
                return "SEND_SMS";
            case 26:
                return "SEND_SMS_EXPECT_MORE";
            case 27:
                return "SETUP_DATA_CALL";
            case 28:
                return "SIM_IO";
            case 29:
                return "SEND_USSD";
            case 30:
                return "CANCEL_USSD";
            case 31:
                return "GET_CLIR";
            case 32:
                return "SET_CLIR";
            case 33:
                return "QUERY_CALL_FORWARD_STATUS";
            case 34:
                return "SET_CALL_FORWARD";
            case 35:
                return "QUERY_CALL_WAITING";
            case 36:
                return "SET_CALL_WAITING";
            case 37:
                return "SMS_ACKNOWLEDGE";
            case 38:
                return "GET_IMEI";
            case 39:
                return "GET_IMEISV";
            case 40:
                return "ANSWER";
            case 41:
                return "DEACTIVATE_DATA_CALL";
            case 42:
                return "QUERY_FACILITY_LOCK";
            case 43:
                return "SET_FACILITY_LOCK";
            case 44:
                return "CHANGE_BARRING_PASSWORD";
            case 45:
                return "QUERY_NETWORK_SELECTION_MODE";
            case 46:
                return "SET_NETWORK_SELECTION_AUTOMATIC";
            case 47:
                return "SET_NETWORK_SELECTION_MANUAL";
            case 48:
                return "QUERY_AVAILABLE_NETWORKS ";
            case 49:
                return "DTMF_START";
            case 50:
                return "DTMF_STOP";
            case 51:
                return "BASEBAND_VERSION";
            case 52:
                return "SEPARATE_CONNECTION";
            case 53:
                return "SET_MUTE";
            case 54:
                return "GET_MUTE";
            case 55:
                return "QUERY_CLIP";
            case 56:
                return "LAST_DATA_CALL_FAIL_CAUSE";
            case 57:
                return "DATA_CALL_LIST";
            case 58:
                return "RESET_RADIO";
            case 59:
                return "OEM_HOOK_RAW";
            case 60:
                return "OEM_HOOK_STRINGS";
            case TelephonyProto$RilErrno.RIL_E_NETWORK_NOT_READY /* 61 */:
                return "SCREEN_STATE";
            case TelephonyProto$RilErrno.RIL_E_NOT_PROVISIONED /* 62 */:
                return "SET_SUPP_SVC_NOTIFICATION";
            case 63:
                return "WRITE_SMS_TO_SIM";
            case 64:
                return "DELETE_SMS_ON_SIM";
            case 65:
                return "SET_BAND_MODE";
            case 66:
                return "QUERY_AVAILABLE_BAND_MODE";
            case TelephonyProto$RilErrno.RIL_E_INVALID_RESPONSE /* 67 */:
                return "STK_GET_PROFILE";
            case 68:
                return "STK_SET_PROFILE";
            case CallFailCause.REQUESTED_FACILITY_NOT_IMPLEMENTED /* 69 */:
                return "STK_SEND_ENVELOPE_COMMAND";
            case CallFailCause.ONLY_RESTRICTED_DIGITAL_INFO_BC_AVAILABLE /* 70 */:
                return "STK_SEND_TERMINAL_RESPONSE";
            case RadioNVItems.RIL_NV_LTE_BAND_ENABLE_25 /* 71 */:
                return "STK_HANDLE_CALL_SETUP_REQUESTED_FROM_SIM";
            case RadioNVItems.RIL_NV_LTE_BAND_ENABLE_26 /* 72 */:
                return "EXPLICIT_CALL_TRANSFER";
            case RadioNVItems.RIL_NV_LTE_BAND_ENABLE_41 /* 73 */:
                return "SET_PREFERRED_NETWORK_TYPE";
            case RadioNVItems.RIL_NV_LTE_SCAN_PRIORITY_25 /* 74 */:
                return "GET_PREFERRED_NETWORK_TYPE";
            case RadioNVItems.RIL_NV_LTE_SCAN_PRIORITY_26 /* 75 */:
                return "GET_NEIGHBORING_CELL_IDS";
            case 76:
                return "SET_LOCATION_UPDATES";
            case RadioNVItems.RIL_NV_LTE_HIDDEN_BAND_PRIORITY_25 /* 77 */:
                return "CDMA_SET_SUBSCRIPTION_SOURCE";
            case RadioNVItems.RIL_NV_LTE_HIDDEN_BAND_PRIORITY_26 /* 78 */:
                return "CDMA_SET_ROAMING_PREFERENCE";
            case 79:
                return "CDMA_QUERY_ROAMING_PREFERENCE";
            case RadioNVItems.RIL_NV_LTE_NEXT_SCAN /* 80 */:
                return "SET_TTY_MODE";
            case 81:
                return "QUERY_TTY_MODE";
            case RadioNVItems.RIL_NV_LTE_BSR_MAX_TIME /* 82 */:
                return "CDMA_SET_PREFERRED_VOICE_PRIVACY_MODE";
            case 83:
                return "CDMA_QUERY_PREFERRED_VOICE_PRIVACY_MODE";
            case 84:
                return "CDMA_FLASH";
            case 85:
                return "CDMA_BURST_DTMF";
            case 86:
                return "CDMA_VALIDATE_AND_WRITE_AKEY";
            case CallFailCause.USER_NOT_MEMBER_OF_CUG /* 87 */:
                return "CDMA_SEND_SMS";
            case CallFailCause.INCOMPATIBLE_DESTINATION /* 88 */:
                return "CDMA_SMS_ACKNOWLEDGE";
            case 89:
                return "GSM_GET_BROADCAST_CONFIG";
            case 90:
                return "GSM_SET_BROADCAST_CONFIG";
            case CallFailCause.INVALID_TRANSIT_NETWORK_SELECTION /* 91 */:
                return "GSM_BROADCAST_ACTIVATION";
            case 92:
                return "CDMA_GET_BROADCAST_CONFIG";
            case 93:
                return "CDMA_SET_BROADCAST_CONFIG";
            case 94:
                return "CDMA_BROADCAST_ACTIVATION";
            case 95:
                return "CDMA_SUBSCRIPTION";
            case 96:
                return "CDMA_WRITE_SMS_TO_RUIM";
            case 97:
                return "CDMA_DELETE_SMS_ON_RUIM";
            case 98:
                return "DEVICE_IDENTITY";
            case 99:
                return "EXIT_EMERGENCY_CALLBACK_MODE";
            case 100:
                return "GET_SMSC_ADDRESS";
            case 101:
                return "SET_SMSC_ADDRESS";
            case CallFailCause.RECOVERY_ON_TIMER_EXPIRY /* 102 */:
                return "REPORT_SMS_MEMORY_STATUS";
            case 103:
                return "REPORT_STK_SERVICE_IS_RUNNING";
            case 104:
                return "CDMA_GET_SUBSCRIPTION_SOURCE";
            case 105:
                return "ISIM_AUTHENTICATION";
            case 106:
                return "ACKNOWLEDGE_INCOMING_GSM_SMS_WITH_PDU";
            case 107:
                return "STK_SEND_ENVELOPE_WITH_STATUS";
            case 108:
                return "VOICE_RADIO_TECH";
            case 109:
                return "GET_CELL_INFO_LIST";
            case 110:
                return "SET_CELL_INFO_LIST_RATE";
            case 111:
                return "SET_INITIAL_ATTACH_APN";
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_APN_TYPE_CONFLICT /* 112 */:
                return "IMS_REGISTRATION_STATE";
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INVALID_PCSCF_ADDR /* 113 */:
                return "IMS_SEND_SMS";
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INTERNAL_CALL_PREEMPT_BY_HIGH_PRIO_APN /* 114 */:
                return "SIM_TRANSMIT_APDU_BASIC";
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMM_ACCESS_BARRED /* 115 */:
                return "SIM_OPEN_CHANNEL";
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMERGENCY_IFACE_ONLY /* 116 */:
                return "SIM_CLOSE_CHANNEL";
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IFACE_MISMATCH /* 117 */:
                return "SIM_TRANSMIT_APDU_CHANNEL";
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_COMPANION_IFACE_IN_USE /* 118 */:
                return "NV_READ_ITEM";
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IP_ADDRESS_MISMATCH /* 119 */:
                return "NV_WRITE_ITEM";
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IFACE_AND_POL_FAMILY_MISMATCH /* 120 */:
                return "NV_WRITE_CDMA_PRL";
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMM_ACCESS_BARRED_INFINITE_RETRY /* 121 */:
                return "NV_RESET_CONFIG";
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_AUTH_FAILURE_ON_EMERGENCY_CALL /* 122 */:
                return "SET_UICC_SUBSCRIPTION";
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INVALID_DNS_ADDR /* 123 */:
                return "ALLOW_DATA";
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INVALID_PCSCF_OR_DNS_ADDRESS /* 124 */:
                return "GET_HARDWARE_CONFIG";
            case 125:
                return "SIM_AUTHENTICATION";
            case 126:
                return "GET_DC_RT_INFO";
            case 127:
                return "SET_DC_RT_INFO_RATE";
            case 128:
                return "SET_DATA_PROFILE";
            case 129:
                return "SHUTDOWN";
            case 130:
                return "GET_RADIO_CAPABILITY";
            case 131:
                return "SET_RADIO_CAPABILITY";
            case UiccCardApplication.AUTH_CONTEXT_GBA_BOOTSTRAP /* 132 */:
                return "START_LCE";
            case 133:
                return "STOP_LCE";
            case 134:
                return "PULL_LCEDATA";
            case NetworkStackConstants.ICMPV6_NEIGHBOR_SOLICITATION /* 135 */:
                return "GET_ACTIVITY_INFO";
            case NetworkStackConstants.ICMPV6_NEIGHBOR_ADVERTISEMENT /* 136 */:
                return "SET_ALLOWED_CARRIERS";
            case 137:
                return "GET_ALLOWED_CARRIERS";
            case 138:
                return "SEND_DEVICE_STATE";
            case 139:
                return "SET_UNSOLICITED_RESPONSE_FILTER";
            case android.telephony.gsm.SmsMessage.MAX_USER_DATA_BYTES /* 140 */:
                return "SET_SIM_CARD_POWER";
            case 141:
                return "SET_CARRIER_INFO_IMSI_ENCRYPTION";
            case 142:
                return "START_NETWORK_SCAN";
            case 143:
                return "STOP_NETWORK_SCAN";
            case 144:
                return "START_KEEPALIVE";
            case 145:
                return "STOP_KEEPALIVE";
            case 146:
                return "ENABLE_MODEM";
            case 147:
                return "GET_MODEM_STATUS";
            case 148:
                return "CDMA_SEND_SMS_EXPECT_MORE";
            case 149:
                return "GET_SIM_PHONEBOOK_CAPACITY";
            case 150:
                return "GET_SIM_PHONEBOOK_RECORDS";
            case 151:
                return "UPDATE_SIM_PHONEBOOK_RECORD";
            default:
                switch (i) {
                    case ImsRttTextHandler.MAX_BUFFERING_DELAY_MILLIS /* 200 */:
                        return "GET_SLOT_STATUS";
                    case IccRecords.EVENT_SET_SMSS_RECORD_DONE /* 201 */:
                        return "SET_LOGICAL_TO_PHYSICAL_SLOT_MAPPING";
                    case 202:
                        return "SET_SIGNAL_STRENGTH_REPORTING_CRITERIA";
                    case 203:
                        return "SET_LINK_CAPACITY_REPORTING_CRITERIA";
                    case 204:
                        return "SET_PREFERRED_DATA_MODEM";
                    case 205:
                        return "EMERGENCY_DIAL";
                    case 206:
                        return "GET_PHONE_CAPABILITY";
                    case 207:
                        return "SWITCH_DUAL_SIM_CONFIG";
                    case BerTlv.BER_PROACTIVE_COMMAND_TAG /* 208 */:
                        return "ENABLE_UICC_APPLICATIONS";
                    case 209:
                        return "GET_UICC_APPLICATIONS_ENABLEMENT";
                    case 210:
                        return "SET_SYSTEM_SELECTION_CHANNELS";
                    case 211:
                        return "GET_BARRING_INFO";
                    case CommandsInterface.GSM_SMS_FAIL_CAUSE_USIM_APP_TOOLKIT_BUSY /* 212 */:
                        return "ENTER_SIM_DEPERSONALIZATION";
                    case CommandsInterface.GSM_SMS_FAIL_CAUSE_USIM_DATA_DOWNLOAD_ERROR /* 213 */:
                        return "ENABLE_NR_DUAL_CONNECTIVITY";
                    case BerTlv.BER_EVENT_DOWNLOAD_TAG /* 214 */:
                        return "IS_NR_DUAL_CONNECTIVITY_ENABLED";
                    case 215:
                        return "ALLOCATE_PDU_SESSION_ID";
                    case 216:
                        return "RELEASE_PDU_SESSION_ID";
                    case 217:
                        return "START_HANDOVER";
                    case 218:
                        return "CANCEL_HANDOVER";
                    case 219:
                        return "GET_SYSTEM_SELECTION_CHANNELS";
                    case 220:
                        return "GET_HAL_DEVICE_CAPABILITIES";
                    case NetworkStackConstants.VENDOR_SPECIFIC_IE_ID /* 221 */:
                        return "SET_DATA_THROTTLING";
                    case 222:
                        return "SET_ALLOWED_NETWORK_TYPES_BITMAP";
                    case 223:
                        return "GET_ALLOWED_NETWORK_TYPES_BITMAP";
                    case 224:
                        return "GET_SLICING_CONFIG";
                    case 225:
                        return "ENABLE_VONR";
                    case 226:
                        return "IS_VONR_ENABLED";
                    case 227:
                        return "SET_USAGE_SETTING";
                    case 228:
                        return "GET_USAGE_SETTING";
                    case 229:
                        return "SET_EMERGENCY_MODE";
                    case 230:
                        return "TRIGGER_EMERGENCY_NETWORK_SCAN";
                    case 231:
                        return "CANCEL_EMERGENCY_NETWORK_SCAN";
                    case 232:
                        return "EXIT_EMERGENCY_MODE";
                    case 233:
                        return "SET_SRVCC_CALL_INFO";
                    case 234:
                        return "UPDATE_IMS_REGISTRATION_INFO";
                    case 235:
                        return "START_IMS_TRAFFIC";
                    case 236:
                        return "STOP_IMS_TRAFFIC";
                    case 237:
                        return "SEND_ANBR_QUERY";
                    case 238:
                        return "TRIGGER_EPS_FALLBACK";
                    case 239:
                        return "SET_NULL_CIPHER_AND_INTEGRITY_ENABLED";
                    case 240:
                        return "UPDATE_IMS_CALL_STATUS";
                    case CallFailCause.FDN_BLOCKED /* 241 */:
                        return "SET_N1_MODE_ENABLED";
                    case 242:
                        return "IS_N1_MODE_ENABLED";
                    default:
                        switch (i) {
                            case CallFailCause.DIAL_MODIFIED_TO_SS /* 245 */:
                                return "GET_SATELLITE_CAPABILITIES";
                            case CallFailCause.DIAL_MODIFIED_TO_DIAL /* 246 */:
                                return "SET_SATELLITE_POWER";
                            case CallFailCause.RADIO_OFF /* 247 */:
                                return "GET_SATELLITE_POWER";
                            case 248:
                                return "PROVISION_SATELLITE_SERVICE";
                            case CallFailCause.NO_VALID_SIM /* 249 */:
                                return "ADD_ALLOWED_SATELLITE_CONTACTS";
                            case CallFailCause.RADIO_INTERNAL_ERROR /* 250 */:
                                return "REMOVE_ALLOWED_SATELLITE_CONTACTS";
                            case CallFailCause.NETWORK_RESP_TIMEOUT /* 251 */:
                                return "SEND_SATELLITE_MESSAGES";
                            case CallFailCause.NETWORK_REJECT /* 252 */:
                                return "GET_PENDING_SATELLITE_MESSAGES";
                            case CallFailCause.RADIO_ACCESS_FAILURE /* 253 */:
                                return "GET_SATELLITE_MODE";
                            case CallFailCause.RADIO_LINK_FAILURE /* 254 */:
                                return "SET_SATELLITE_INDICATION_FILTER";
                            case 255:
                                return "START_SENDING_SATELLITE_POINTING_INFO";
                            case CallFailCause.RADIO_UPLINK_FAILURE /* 256 */:
                                return "STOP_SENDING_SATELLITE_POINTING_INFO";
                            case CallFailCause.RADIO_SETUP_FAILURE /* 257 */:
                                return "GET_MAX_CHARACTERS_PER_SATELLITE_TEXT_MESSAGE";
                            case CallFailCause.RADIO_RELEASE_NORMAL /* 258 */:
                                return "GET_TIME_FOR_NEXT_SATELLITE_VISIBILITY";
                            default:
                                return "<unknown request " + i + ">";
                        }
                }
        }
    }

    @VisibleForTesting
    public static Set<String> getCaps(HalVersion halVersion, boolean z) {
        HashSet hashSet = new HashSet();
        if (halVersion.equals(RIL.RADIO_HAL_VERSION_UNKNOWN)) {
            loge("Radio Hal Version is UNKNOWN!");
        }
        logd("Radio Hal Version = " + halVersion.toString());
        if (halVersion.greaterOrEqual(RIL.RADIO_HAL_VERSION_1_6)) {
            hashSet.add("CAPABILITY_USES_ALLOWED_NETWORK_TYPES_BITMASK");
            logd("CAPABILITY_USES_ALLOWED_NETWORK_TYPES_BITMASK");
            if (!z) {
                hashSet.add("CAPABILITY_SECONDARY_LINK_BANDWIDTH_VISIBLE");
                logd("CAPABILITY_SECONDARY_LINK_BANDWIDTH_VISIBLE");
                hashSet.add("CAPABILITY_NR_DUAL_CONNECTIVITY_CONFIGURATION_AVAILABLE");
                logd("CAPABILITY_NR_DUAL_CONNECTIVITY_CONFIGURATION_AVAILABLE");
                hashSet.add("CAPABILITY_THERMAL_MITIGATION_DATA_THROTTLING");
                logd("CAPABILITY_THERMAL_MITIGATION_DATA_THROTTLING");
                hashSet.add("CAPABILITY_SLICING_CONFIG_SUPPORTED");
                logd("CAPABILITY_SLICING_CONFIG_SUPPORTED");
                hashSet.add("CAPABILITY_PHYSICAL_CHANNEL_CONFIG_1_6_SUPPORTED");
                logd("CAPABILITY_PHYSICAL_CHANNEL_CONFIG_1_6_SUPPORTED");
            } else {
                hashSet.add("CAPABILITY_SIM_PHONEBOOK_IN_MODEM");
                logd("CAPABILITY_SIM_PHONEBOOK_IN_MODEM");
            }
        }
        return hashSet;
    }

    private static boolean isPrimitiveOrWrapper(Class cls) {
        return cls.isPrimitive() || WRAPPER_CLASSES.contains(cls);
    }

    /* JADX WARN: Removed duplicated region for block: B:139:0x0157  */
    /* JADX WARN: Removed duplicated region for block: B:166:0x00df A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static String convertToString(Object obj) {
        boolean z;
        int i;
        String str;
        Object obj2;
        boolean z2;
        int i2 = 0;
        try {
        } catch (NoSuchMethodException e) {
            loge(e.toString());
        }
        if (obj.getClass().getMethod("toString", new Class[0]).getDeclaringClass() != Object.class) {
            z = true;
            if (!z || isPrimitiveOrWrapper(obj.getClass()) || (obj instanceof ArrayList)) {
                return obj.toString();
            }
            if (obj.getClass().isArray()) {
                StringBuilder sb = new StringBuilder("[");
                if (isPrimitiveOrWrapper(obj.getClass().getComponentType())) {
                    z2 = false;
                    while (i2 < Array.getLength(obj)) {
                        sb.append(convertToString(Array.get(obj, i2)));
                        sb.append(", ");
                        i2++;
                        z2 = true;
                    }
                } else {
                    Object[] objArr = (Object[]) obj;
                    int length = objArr.length;
                    boolean z3 = false;
                    while (i2 < length) {
                        sb.append(convertToString(objArr[i2]));
                        sb.append(", ");
                        i2++;
                        z3 = true;
                    }
                    z2 = z3;
                }
                if (z2) {
                    sb.delete(sb.length() - 2, sb.length());
                }
                sb.append("]");
                return sb.toString();
            }
            StringBuilder sb2 = new StringBuilder(obj.getClass().getSimpleName());
            sb2.append("{");
            Field[] declaredFields = obj.getClass().getDeclaredFields();
            try {
                i = ((Integer) obj.getClass().getDeclaredMethod("getTag", new Class[0]).invoke(obj, new Object[0])).intValue();
            } catch (IllegalAccessException | InvocationTargetException e2) {
                loge(e2.toString());
                i = -1;
                Object obj3 = null;
                if (i != -1) {
                }
                sb2.append("}");
                return sb2.toString();
            } catch (NoSuchMethodException unused) {
                i = -1;
                Object obj32 = null;
                if (i != -1) {
                }
                sb2.append("}");
                return sb2.toString();
            }
            Object obj322 = null;
            if (i != -1) {
                try {
                    Method declaredMethod = obj.getClass().getDeclaredMethod("_tagString", Integer.TYPE);
                    declaredMethod.setAccessible(true);
                    str = (String) declaredMethod.invoke(obj, Integer.valueOf(i));
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e3) {
                    loge(e3.toString());
                    str = null;
                }
                if (str != null) {
                    sb2.append(str);
                    sb2.append("=");
                    try {
                        obj322 = obj.getClass().getDeclaredMethod("get" + str.substring(0, 1).toUpperCase(Locale.ROOT) + str.substring(1), new Class[0]).invoke(obj, new Object[0]);
                    } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e4) {
                        loge(e4.toString());
                    }
                    if (obj322 != null) {
                        sb2.append(convertToString(obj322));
                    }
                }
            } else {
                int length2 = declaredFields.length;
                boolean z4 = false;
                while (i2 < length2) {
                    Field field = declaredFields[i2];
                    if (!Modifier.isStatic(field.getModifiers())) {
                        sb2.append(field.getName());
                        sb2.append("=");
                        try {
                            obj2 = field.get(obj);
                        } catch (IllegalAccessException e5) {
                            loge(e5.toString());
                            obj2 = null;
                        }
                        if (obj2 != null) {
                            sb2.append(convertToString(obj2));
                            sb2.append(", ");
                            z4 = true;
                        }
                    }
                    i2++;
                }
                if (z4) {
                    sb2.delete(sb2.length() - 2, sb2.length());
                }
            }
            sb2.append("}");
            return sb2.toString();
        }
        z = false;
        if (!z) {
        }
        return obj.toString();
    }

    public static SrvccCall[] convertToHalSrvccCall(SrvccConnection[] srvccConnectionArr) {
        int i = 0;
        if (srvccConnectionArr == null) {
            return new SrvccCall[0];
        }
        int length = srvccConnectionArr.length;
        SrvccCall[] srvccCallArr = new SrvccCall[length];
        while (i < length) {
            SrvccCall srvccCall = new SrvccCall();
            srvccCallArr[i] = srvccCall;
            int i2 = i + 1;
            srvccCall.index = i2;
            srvccCall.callType = convertSrvccCallType(srvccConnectionArr[i].getType());
            srvccCallArr[i].callState = convertCallState(srvccConnectionArr[i].getState());
            srvccCallArr[i].callSubstate = convertSrvccCallSubState(srvccConnectionArr[i].getSubState());
            srvccCallArr[i].ringbackToneType = convertSrvccCallRingbackToneType(srvccConnectionArr[i].getRingbackToneType());
            srvccCallArr[i].isMpty = srvccConnectionArr[i].isMultiParty();
            srvccCallArr[i].isMT = srvccConnectionArr[i].isIncoming();
            srvccCallArr[i].number = TextUtils.emptyIfNull(srvccConnectionArr[i].getNumber());
            srvccCallArr[i].numPresentation = convertPresentation(srvccConnectionArr[i].getNumberPresentation());
            srvccCallArr[i].name = TextUtils.emptyIfNull(srvccConnectionArr[i].getName());
            srvccCallArr[i].namePresentation = convertPresentation(srvccConnectionArr[i].getNamePresentation());
            i = i2;
        }
        return srvccCallArr;
    }

    public static int convertSrvccCallType(int i) {
        if (i != 0) {
            if (i == 1) {
                return 1;
            }
            throw new RuntimeException("illegal call type " + i);
        }
        return 0;
    }

    public static int convertCallState(Call.State state) {
        switch (C00571.$SwitchMap$com$android$internal$telephony$Call$State[state.ordinal()]) {
            case 1:
                return 0;
            case 2:
                return 1;
            case 3:
                return 2;
            case 4:
                return 3;
            case 5:
                return 4;
            case 6:
                return 5;
            default:
                throw new RuntimeException("illegal state " + state);
        }
    }

    public static int convertSrvccCallSubState(int i) {
        if (i != 0) {
            if (i == 1) {
                return 1;
            }
            throw new RuntimeException("illegal substate " + i);
        }
        return 0;
    }

    public static int convertSrvccCallRingbackToneType(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i == 2) {
                    return 2;
                }
                throw new RuntimeException("illegal ringback tone type " + i);
            }
            return 1;
        }
        return 0;
    }

    public static int convertPresentation(int i) {
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i == 4) {
                        return 3;
                    }
                    throw new RuntimeException("illegal presentation " + i);
                }
                return 2;
            }
            return 1;
        }
        return 0;
    }

    public static int convertImsRegistrationState(int i) {
        if (i != 0) {
            if (i == 2) {
                return 1;
            }
            throw new RuntimeException("illegal state " + i);
        }
        return 0;
    }

    public static ImsCall[] convertImsCallInfo(List<ImsCallInfo> list) {
        if (list == null) {
            return new ImsCall[0];
        }
        int i = 0;
        for (int i2 = 0; i2 < list.size(); i2++) {
            if (list.get(i2) != null) {
                i++;
            }
        }
        if (i == 0) {
            return new ImsCall[0];
        }
        ImsCall[] imsCallArr = new ImsCall[i];
        int i3 = 0;
        for (int i4 = 0; i4 < list.size(); i4++) {
            ImsCallInfo imsCallInfo = list.get(i4);
            if (imsCallInfo != null) {
                ImsCall imsCall = new ImsCall();
                imsCallArr[i3] = imsCall;
                imsCall.index = imsCallInfo.getIndex();
                imsCallArr[i3].callState = convertToHalImsCallState(imsCallInfo.getCallState());
                imsCallArr[i3].callType = imsCallInfo.isEmergencyCall() ? 1 : 0;
                imsCallArr[i3].accessNetwork = convertToHalAccessNetworkAidl(imsCallInfo.getCallRadioTech());
                imsCallArr[i3].direction = !imsCallInfo.isIncoming();
                imsCallArr[i3].isHeldByRemote = imsCallInfo.isHeldByRemote();
                i3++;
            }
        }
        return imsCallArr;
    }

    public static SatelliteCapabilities convertHalSatelliteCapabilities(android.hardware.radio.satellite.SatelliteCapabilities satelliteCapabilities) {
        HashSet hashSet = new HashSet();
        int[] iArr = satelliteCapabilities.supportedRadioTechnologies;
        if (iArr != null && iArr.length > 0) {
            for (int i : iArr) {
                hashSet.add(Integer.valueOf(i));
            }
        }
        return new SatelliteCapabilities(hashSet, satelliteCapabilities.isAlwaysOn, satelliteCapabilities.needsPointingToSatellite, satelliteCapabilities.needsSeparateSimProfile);
    }

    public static PointingInfo convertHalSatellitePointingInfo(android.hardware.radio.satellite.PointingInfo pointingInfo) {
        return new PointingInfo(pointingInfo.satelliteAzimuthDegrees, pointingInfo.satelliteElevationDegrees, pointingInfo.antennaAzimuthDegrees, pointingInfo.antennaPitchDegrees, pointingInfo.antennaRollDegrees);
    }

    public static android.hardware.radio.satellite.PointingInfo convertToHalSatellitePointingInfo(PointingInfo pointingInfo) {
        android.hardware.radio.satellite.PointingInfo pointingInfo2 = new android.hardware.radio.satellite.PointingInfo();
        pointingInfo2.satelliteAzimuthDegrees = pointingInfo.getSatelliteAzimuthDegrees();
        pointingInfo2.satelliteElevationDegrees = pointingInfo.getSatelliteElevationDegrees();
        pointingInfo2.antennaAzimuthDegrees = pointingInfo.getAntennaAzimuthDegrees();
        pointingInfo2.antennaPitchDegrees = pointingInfo.getAntennaPitchDegrees();
        pointingInfo2.antennaRollDegrees = pointingInfo.getAntennaRollDegrees();
        return pointingInfo2;
    }

    /* renamed from: com.android.internal.telephony.RILUtils$1 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C00571 {
        static final /* synthetic */ int[] $SwitchMap$com$android$internal$telephony$Call$State;
        static final /* synthetic */ int[] $SwitchMap$com$android$internal$telephony$CommandException$Error;

        /* renamed from: $SwitchMap$com$android$internal$telephony$uicc$IccCardApplicationStatus$PersoSubState */
        static final /* synthetic */ int[] f0x1c108904;

        static {
            int[] iArr = new int[CommandException.Error.values().length];
            $SwitchMap$com$android$internal$telephony$CommandException$Error = iArr;
            try {
                iArr[CommandException.Error.INTERNAL_ERR.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.MODEM_ERR.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.SYSTEM_ERR.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.INVALID_ARGUMENTS.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.INVALID_MODEM_STATE.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.RADIO_NOT_AVAILABLE.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.REQUEST_NOT_SUPPORTED.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.NO_MEMORY.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.NO_RESOURCES.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.NETWORK_ERR.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.NETWORK_TIMEOUT.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.NO_NETWORK_FOUND.ordinal()] = 12;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.NO_SATELLITE_SIGNAL.ordinal()] = 13;
            } catch (NoSuchFieldError unused13) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.ABORTED.ordinal()] = 14;
            } catch (NoSuchFieldError unused14) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.ACCESS_BARRED.ordinal()] = 15;
            } catch (NoSuchFieldError unused15) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.SUBSCRIBER_NOT_AUTHORIZED.ordinal()] = 16;
            } catch (NoSuchFieldError unused16) {
            }
            int[] iArr2 = new int[Call.State.values().length];
            $SwitchMap$com$android$internal$telephony$Call$State = iArr2;
            try {
                iArr2[Call.State.ACTIVE.ordinal()] = 1;
            } catch (NoSuchFieldError unused17) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$Call$State[Call.State.HOLDING.ordinal()] = 2;
            } catch (NoSuchFieldError unused18) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$Call$State[Call.State.DIALING.ordinal()] = 3;
            } catch (NoSuchFieldError unused19) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$Call$State[Call.State.ALERTING.ordinal()] = 4;
            } catch (NoSuchFieldError unused20) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$Call$State[Call.State.INCOMING.ordinal()] = 5;
            } catch (NoSuchFieldError unused21) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$Call$State[Call.State.WAITING.ordinal()] = 6;
            } catch (NoSuchFieldError unused22) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$Call$State[Call.State.DISCONNECTING.ordinal()] = 7;
            } catch (NoSuchFieldError unused23) {
            }
            int[] iArr3 = new int[IccCardApplicationStatus.PersoSubState.values().length];
            f0x1c108904 = iArr3;
            try {
                iArr3[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_IN_PROGRESS.ordinal()] = 1;
            } catch (NoSuchFieldError unused24) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_READY.ordinal()] = 2;
            } catch (NoSuchFieldError unused25) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_SIM_NETWORK.ordinal()] = 3;
            } catch (NoSuchFieldError unused26) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_SIM_NETWORK_SUBSET.ordinal()] = 4;
            } catch (NoSuchFieldError unused27) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_SIM_CORPORATE.ordinal()] = 5;
            } catch (NoSuchFieldError unused28) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_SIM_SERVICE_PROVIDER.ordinal()] = 6;
            } catch (NoSuchFieldError unused29) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_SIM_SIM.ordinal()] = 7;
            } catch (NoSuchFieldError unused30) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_SIM_NETWORK_PUK.ordinal()] = 8;
            } catch (NoSuchFieldError unused31) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_SIM_NETWORK_SUBSET_PUK.ordinal()] = 9;
            } catch (NoSuchFieldError unused32) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_SIM_CORPORATE_PUK.ordinal()] = 10;
            } catch (NoSuchFieldError unused33) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_SIM_SERVICE_PROVIDER_PUK.ordinal()] = 11;
            } catch (NoSuchFieldError unused34) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_SIM_SIM_PUK.ordinal()] = 12;
            } catch (NoSuchFieldError unused35) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_RUIM_NETWORK1.ordinal()] = 13;
            } catch (NoSuchFieldError unused36) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_RUIM_NETWORK2.ordinal()] = 14;
            } catch (NoSuchFieldError unused37) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_RUIM_HRPD.ordinal()] = 15;
            } catch (NoSuchFieldError unused38) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_RUIM_CORPORATE.ordinal()] = 16;
            } catch (NoSuchFieldError unused39) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_RUIM_SERVICE_PROVIDER.ordinal()] = 17;
            } catch (NoSuchFieldError unused40) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_RUIM_RUIM.ordinal()] = 18;
            } catch (NoSuchFieldError unused41) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_RUIM_NETWORK1_PUK.ordinal()] = 19;
            } catch (NoSuchFieldError unused42) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_RUIM_NETWORK2_PUK.ordinal()] = 20;
            } catch (NoSuchFieldError unused43) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_RUIM_HRPD_PUK.ordinal()] = 21;
            } catch (NoSuchFieldError unused44) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_RUIM_CORPORATE_PUK.ordinal()] = 22;
            } catch (NoSuchFieldError unused45) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_RUIM_SERVICE_PROVIDER_PUK.ordinal()] = 23;
            } catch (NoSuchFieldError unused46) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_RUIM_RUIM_PUK.ordinal()] = 24;
            } catch (NoSuchFieldError unused47) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_SIM_SPN.ordinal()] = 25;
            } catch (NoSuchFieldError unused48) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_SIM_SPN_PUK.ordinal()] = 26;
            } catch (NoSuchFieldError unused49) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_SIM_SP_EHPLMN.ordinal()] = 27;
            } catch (NoSuchFieldError unused50) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_SIM_SP_EHPLMN_PUK.ordinal()] = 28;
            } catch (NoSuchFieldError unused51) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_SIM_ICCID.ordinal()] = 29;
            } catch (NoSuchFieldError unused52) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_SIM_ICCID_PUK.ordinal()] = 30;
            } catch (NoSuchFieldError unused53) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_SIM_IMPI.ordinal()] = 31;
            } catch (NoSuchFieldError unused54) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_SIM_IMPI_PUK.ordinal()] = 32;
            } catch (NoSuchFieldError unused55) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_SIM_NS_SP.ordinal()] = 33;
            } catch (NoSuchFieldError unused56) {
            }
            try {
                f0x1c108904[IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_SIM_NS_SP_PUK.ordinal()] = 34;
            } catch (NoSuchFieldError unused57) {
            }
        }
    }

    public static int convertToSatelliteError(CommandException.Error error) {
        switch (C00571.$SwitchMap$com$android$internal$telephony$CommandException$Error[error.ordinal()]) {
            case 1:
            case 2:
            case 3:
                return 4;
            case 4:
                return 8;
            case 5:
                return 7;
            case 6:
                return 10;
            case 7:
                return 11;
            case 8:
            case 9:
                return 12;
            case 10:
                return 5;
            case 11:
                return 17;
            case 12:
            case 13:
                return 18;
            case 14:
                return 15;
            case 15:
                return 16;
            case 16:
                return 19;
            default:
                return 1;
        }
    }

    private static int convertToHalImsCallState(Call.State state) {
        switch (C00571.$SwitchMap$com$android$internal$telephony$Call$State[state.ordinal()]) {
            case 1:
                return 0;
            case 2:
                return 1;
            case 3:
                return 2;
            case 4:
                return 3;
            case 5:
                return 4;
            case 6:
                return 5;
            case 7:
                return 6;
            default:
                return 7;
        }
    }

    private static void logd(String str) {
        Rlog.d("RILUtils", str);
    }

    private static void loge(String str) {
        Rlog.e("RILUtils", str);
    }
}
