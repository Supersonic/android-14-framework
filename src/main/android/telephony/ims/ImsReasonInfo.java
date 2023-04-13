package android.telephony.ims;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes3.dex */
public final class ImsReasonInfo implements Parcelable {
    public static final int CODE_ACCESS_CLASS_BLOCKED = 1512;
    public static final int CODE_ANSWERED_ELSEWHERE = 1014;
    public static final int CODE_BLACKLISTED_CALL_ID = 506;
    public static final int CODE_CALL_BARRED = 240;
    public static final int CODE_CALL_DROP_IWLAN_TO_LTE_UNAVAILABLE = 1100;
    public static final int CODE_CALL_END_CAUSE_CALL_PULL = 1016;
    public static final int CODE_CALL_PULL_OUT_OF_SYNC = 1015;
    public static final int CODE_DATA_DISABLED = 1406;
    public static final int CODE_DATA_LIMIT_REACHED = 1405;
    public static final int CODE_DIAL_MODIFIED_TO_DIAL = 246;
    public static final int CODE_DIAL_MODIFIED_TO_DIAL_VIDEO = 247;
    public static final int CODE_DIAL_MODIFIED_TO_SS = 245;
    public static final int CODE_DIAL_MODIFIED_TO_USSD = 244;
    public static final int CODE_DIAL_VIDEO_MODIFIED_TO_DIAL = 248;
    public static final int CODE_DIAL_VIDEO_MODIFIED_TO_DIAL_VIDEO = 249;
    public static final int CODE_DIAL_VIDEO_MODIFIED_TO_SS = 250;
    public static final int CODE_DIAL_VIDEO_MODIFIED_TO_USSD = 251;
    public static final int CODE_ECBM_NOT_SUPPORTED = 901;
    public static final int CODE_EMERGENCY_CALL_OVER_WFC_NOT_AVAILABLE = 1622;
    public static final int CODE_EMERGENCY_PERM_FAILURE = 364;
    public static final int CODE_EMERGENCY_TEMP_FAILURE = 363;
    public static final int CODE_EPDG_TUNNEL_ESTABLISH_FAILURE = 1400;
    public static final int CODE_EPDG_TUNNEL_LOST_CONNECTION = 1402;
    public static final int CODE_EPDG_TUNNEL_REKEY_FAILURE = 1401;
    public static final int CODE_FDN_BLOCKED = 241;
    public static final int CODE_IKEV2_AUTH_FAILURE = 1408;
    public static final int CODE_IMEI_NOT_ACCEPTED = 243;
    public static final int CODE_IWLAN_DPD_FAILURE = 1300;
    public static final int CODE_LOCAL_CALL_BUSY = 142;
    public static final int CODE_LOCAL_CALL_CS_RETRY_REQUIRED = 146;
    public static final int CODE_LOCAL_CALL_DECLINE = 143;
    public static final int CODE_LOCAL_CALL_EXCEEDED = 141;
    public static final int CODE_LOCAL_CALL_RESOURCE_RESERVATION_FAILED = 145;
    public static final int CODE_LOCAL_CALL_TERMINATED = 148;
    public static final int CODE_LOCAL_CALL_VCC_ON_PROGRESSING = 144;
    public static final int CODE_LOCAL_CALL_VOLTE_RETRY_REQUIRED = 147;
    public static final int CODE_LOCAL_ENDED_BY_CONFERENCE_MERGE = 108;
    public static final int CODE_LOCAL_HO_NOT_FEASIBLE = 149;
    public static final int CODE_LOCAL_ILLEGAL_ARGUMENT = 101;
    public static final int CODE_LOCAL_ILLEGAL_STATE = 102;
    public static final int CODE_LOCAL_IMS_NOT_SUPPORTED_ON_DEVICE = 150;
    public static final int CODE_LOCAL_IMS_SERVICE_DOWN = 106;
    public static final int CODE_LOCAL_INTERNAL_ERROR = 103;
    public static final int CODE_LOCAL_LOW_BATTERY = 112;
    public static final int CODE_LOCAL_NETWORK_IP_CHANGED = 124;
    public static final int CODE_LOCAL_NETWORK_NO_LTE_COVERAGE = 122;
    public static final int CODE_LOCAL_NETWORK_NO_SERVICE = 121;
    public static final int CODE_LOCAL_NETWORK_ROAMING = 123;
    public static final int CODE_LOCAL_NOT_REGISTERED = 132;
    public static final int CODE_LOCAL_NO_PENDING_CALL = 107;
    public static final int CODE_LOCAL_POWER_OFF = 111;
    public static final int CODE_LOCAL_SERVICE_UNAVAILABLE = 131;
    public static final int CODE_LOW_BATTERY = 505;
    public static final int CODE_MAXIMUM_NUMBER_OF_CALLS_REACHED = 1403;
    public static final int CODE_MEDIA_INIT_FAILED = 401;
    public static final int CODE_MEDIA_NOT_ACCEPTABLE = 403;
    public static final int CODE_MEDIA_NO_DATA = 402;
    public static final int CODE_MEDIA_UNSPECIFIED = 404;
    public static final int CODE_MULTIENDPOINT_NOT_SUPPORTED = 902;
    public static final int CODE_NETWORK_CONGESTION = 1624;
    public static final int CODE_NETWORK_DETACH = 1513;
    public static final int CODE_NETWORK_REJECT = 1504;
    public static final int CODE_NETWORK_RESP_TIMEOUT = 1503;
    public static final int CODE_NO_CSFB_IN_CS_ROAM = 1516;
    public static final int CODE_NO_VALID_SIM = 1501;
    public static final int CODE_OEM_CAUSE_1 = 61441;
    public static final int CODE_OEM_CAUSE_10 = 61450;
    public static final int CODE_OEM_CAUSE_11 = 61451;
    public static final int CODE_OEM_CAUSE_12 = 61452;
    public static final int CODE_OEM_CAUSE_13 = 61453;
    public static final int CODE_OEM_CAUSE_14 = 61454;
    public static final int CODE_OEM_CAUSE_15 = 61455;
    public static final int CODE_OEM_CAUSE_2 = 61442;
    public static final int CODE_OEM_CAUSE_3 = 61443;
    public static final int CODE_OEM_CAUSE_4 = 61444;
    public static final int CODE_OEM_CAUSE_5 = 61445;
    public static final int CODE_OEM_CAUSE_6 = 61446;
    public static final int CODE_OEM_CAUSE_7 = 61447;
    public static final int CODE_OEM_CAUSE_8 = 61448;
    public static final int CODE_OEM_CAUSE_9 = 61449;
    public static final int CODE_RADIO_ACCESS_FAILURE = 1505;
    public static final int CODE_RADIO_INTERNAL_ERROR = 1502;
    public static final int CODE_RADIO_LINK_FAILURE = 1506;
    public static final int CODE_RADIO_LINK_LOST = 1507;
    public static final int CODE_RADIO_OFF = 1500;
    public static final int CODE_RADIO_RELEASE_ABNORMAL = 1511;
    public static final int CODE_RADIO_RELEASE_NORMAL = 1510;
    public static final int CODE_RADIO_SETUP_FAILURE = 1509;
    public static final int CODE_RADIO_UPLINK_FAILURE = 1508;
    public static final int CODE_REGISTRATION_ERROR = 1000;
    public static final int CODE_REJECTED_ELSEWHERE = 1017;
    public static final int CODE_REJECT_1X_COLLISION = 1603;
    public static final int CODE_REJECT_CALL_ON_OTHER_SUB = 1602;
    public static final int CODE_REJECT_CALL_TYPE_NOT_ALLOWED = 1605;
    public static final int CODE_REJECT_CONFERENCE_TTY_NOT_ALLOWED = 1617;
    public static final int CODE_REJECT_INTERNAL_ERROR = 1612;
    public static final int CODE_REJECT_MAX_CALL_LIMIT_REACHED = 1608;
    public static final int CODE_REJECT_ONGOING_CALL_SETUP = 1607;
    public static final int CODE_REJECT_ONGOING_CALL_TRANSFER = 1611;
    public static final int CODE_REJECT_ONGOING_CALL_UPGRADE = 1616;
    public static final int CODE_REJECT_ONGOING_CALL_WAITING_DISABLED = 1601;
    public static final int CODE_REJECT_ONGOING_CONFERENCE_CALL = 1618;
    public static final int CODE_REJECT_ONGOING_CS_CALL = 1621;
    public static final int CODE_REJECT_ONGOING_E911_CALL = 1606;
    public static final int CODE_REJECT_ONGOING_ENCRYPTED_CALL = 1620;
    public static final int CODE_REJECT_ONGOING_HANDOVER = 1614;
    public static final int CODE_REJECT_QOS_FAILURE = 1613;
    public static final int CODE_REJECT_SERVICE_NOT_REGISTERED = 1604;
    public static final int CODE_REJECT_UNKNOWN = 1600;
    public static final int CODE_REJECT_UNSUPPORTED_SDP_HEADERS = 1610;
    public static final int CODE_REJECT_UNSUPPORTED_SIP_HEADERS = 1609;
    public static final int CODE_REJECT_VT_AVPF_NOT_ALLOWED = 1619;
    public static final int CODE_REJECT_VT_TTY_NOT_ALLOWED = 1615;
    public static final int CODE_REMOTE_CALL_DECLINE = 1404;
    public static final int CODE_RETRY_ON_IMS_WITHOUT_RTT = 3001;
    public static final int CODE_SESSION_MODIFICATION_FAILED = 1517;
    public static final int CODE_SIP_ALTERNATE_EMERGENCY_CALL = 1514;
    public static final int CODE_SIP_AMBIGUOUS = 376;
    public static final int CODE_SIP_BAD_ADDRESS = 337;
    public static final int CODE_SIP_BAD_REQUEST = 331;
    public static final int CODE_SIP_BUSY = 338;
    public static final int CODE_SIP_CALL_OR_TRANS_DOES_NOT_EXIST = 372;
    public static final int CODE_SIP_CLIENT_ERROR = 342;
    public static final int CODE_SIP_EXTENSION_REQUIRED = 370;
    public static final int CODE_SIP_FORBIDDEN = 332;
    public static final int CODE_SIP_GLOBAL_ERROR = 362;
    public static final int CODE_SIP_INTERVAL_TOO_BRIEF = 371;
    public static final int CODE_SIP_LOOP_DETECTED = 373;
    public static final int CODE_SIP_METHOD_NOT_ALLOWED = 366;
    public static final int CODE_SIP_NOT_ACCEPTABLE = 340;
    public static final int CODE_SIP_NOT_FOUND = 333;
    public static final int CODE_SIP_NOT_REACHABLE = 341;
    public static final int CODE_SIP_NOT_SUPPORTED = 334;
    public static final int CODE_SIP_PROXY_AUTHENTICATION_REQUIRED = 367;
    public static final int CODE_SIP_REDIRECTED = 321;
    public static final int CODE_SIP_REQUEST_CANCELLED = 339;
    public static final int CODE_SIP_REQUEST_ENTITY_TOO_LARGE = 368;
    public static final int CODE_SIP_REQUEST_PENDING = 377;
    public static final int CODE_SIP_REQUEST_TIMEOUT = 335;
    public static final int CODE_SIP_REQUEST_URI_TOO_LARGE = 369;
    public static final int CODE_SIP_SERVER_ERROR = 354;
    public static final int CODE_SIP_SERVER_INTERNAL_ERROR = 351;
    public static final int CODE_SIP_SERVER_TIMEOUT = 353;
    public static final int CODE_SIP_SERVICE_UNAVAILABLE = 352;
    public static final int CODE_SIP_TEMPRARILY_UNAVAILABLE = 336;
    public static final int CODE_SIP_TOO_MANY_HOPS = 374;
    public static final int CODE_SIP_TRANSACTION_DOES_NOT_EXIST = 343;
    public static final int CODE_SIP_UNDECIPHERABLE = 378;
    public static final int CODE_SIP_USER_MARKED_UNWANTED = 365;
    public static final int CODE_SIP_USER_REJECTED = 361;
    public static final int CODE_SUPP_SVC_CANCELLED = 1202;
    public static final int CODE_SUPP_SVC_FAILED = 1201;
    public static final int CODE_SUPP_SVC_REINVITE_COLLISION = 1203;
    public static final int CODE_TIMEOUT_1XX_WAITING = 201;
    public static final int CODE_TIMEOUT_NO_ANSWER = 202;
    public static final int CODE_TIMEOUT_NO_ANSWER_CALL_UPDATE = 203;
    public static final int CODE_UNOBTAINABLE_NUMBER = 1515;
    public static final int CODE_UNSPECIFIED = 0;
    public static final int CODE_USER_CANCELLED_SESSION_MODIFICATION = 512;
    public static final int CODE_USER_DECLINE = 504;
    public static final int CODE_USER_IGNORE = 503;
    public static final int CODE_USER_NOANSWER = 502;
    public static final int CODE_USER_REJECTED_SESSION_MODIFICATION = 511;
    public static final int CODE_USER_TERMINATED = 501;
    public static final int CODE_USER_TERMINATED_BY_REMOTE = 510;
    public static final int CODE_UT_CB_PASSWORD_MISMATCH = 821;
    public static final int CODE_UT_NETWORK_ERROR = 804;
    public static final int CODE_UT_NOT_SUPPORTED = 801;
    public static final int CODE_UT_OPERATION_NOT_ALLOWED = 803;
    public static final int CODE_UT_SERVICE_UNAVAILABLE = 802;
    public static final int CODE_UT_SS_MODIFIED_TO_DIAL = 822;
    public static final int CODE_UT_SS_MODIFIED_TO_DIAL_VIDEO = 825;
    public static final int CODE_UT_SS_MODIFIED_TO_SS = 824;
    public static final int CODE_UT_SS_MODIFIED_TO_USSD = 823;
    public static final int CODE_WFC_SERVICE_NOT_AVAILABLE_IN_THIS_LOCATION = 1623;
    public static final int CODE_WIFI_LOST = 1407;
    public static final Parcelable.Creator<ImsReasonInfo> CREATOR;
    public static final int EXTRA_CODE_CALL_RETRY_BY_SETTINGS = 3;
    public static final int EXTRA_CODE_CALL_RETRY_EMERGENCY = 4;
    public static final int EXTRA_CODE_CALL_RETRY_NORMAL = 1;
    public static final int EXTRA_CODE_CALL_RETRY_SILENT_REDIAL = 2;
    @SystemApi
    public static final String EXTRA_MSG_SERVICE_NOT_AUTHORIZED = "Forbidden. Not Authorized for Service";
    private static final Map<Integer, String> sImsCodeMap;
    public int mCode;
    public int mExtraCode;
    public String mExtraMessage;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ImsCode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface UtReason {
    }

    static {
        HashMap hashMap = new HashMap();
        sImsCodeMap = hashMap;
        hashMap.put(0, "CODE_UNSPECIFIED");
        hashMap.put(101, "CODE_LOCAL_ILLEGAL_ARGUMENT");
        hashMap.put(102, "CODE_LOCAL_ILLEGAL_STATE");
        hashMap.put(103, "CODE_LOCAL_INTERNAL_ERROR");
        hashMap.put(106, "CODE_LOCAL_IMS_SERVICE_DOWN");
        hashMap.put(107, "CODE_LOCAL_NO_PENDING_CALL");
        hashMap.put(108, "CODE_LOCAL_ENDED_BY_CONFERENCE_MERGE");
        hashMap.put(111, "CODE_LOCAL_POWER_OFF");
        hashMap.put(112, "CODE_LOCAL_LOW_BATTERY");
        hashMap.put(121, "CODE_LOCAL_NETWORK_NO_SERVICE");
        hashMap.put(122, "CODE_LOCAL_NETWORK_NO_LTE_COVERAGE");
        hashMap.put(123, "CODE_LOCAL_NETWORK_ROAMING");
        hashMap.put(124, "CODE_LOCAL_NETWORK_IP_CHANGED");
        hashMap.put(131, "CODE_LOCAL_SERVICE_UNAVAILABLE");
        hashMap.put(132, "CODE_LOCAL_NOT_REGISTERED");
        hashMap.put(141, "CODE_LOCAL_CALL_EXCEEDED");
        hashMap.put(142, "CODE_LOCAL_CALL_BUSY");
        hashMap.put(143, "CODE_LOCAL_CALL_DECLINE");
        hashMap.put(144, "CODE_LOCAL_CALL_VCC_ON_PROGRESSING");
        hashMap.put(145, "CODE_LOCAL_CALL_RESOURCE_RESERVATION_FAILED");
        hashMap.put(146, "CODE_LOCAL_CALL_CS_RETRY_REQUIRED");
        hashMap.put(147, "CODE_LOCAL_CALL_VOLTE_RETRY_REQUIRED");
        hashMap.put(148, "CODE_LOCAL_CALL_TERMINATED");
        hashMap.put(149, "CODE_LOCAL_HO_NOT_FEASIBLE");
        hashMap.put(201, "CODE_TIMEOUT_1XX_WAITING");
        hashMap.put(202, "CODE_TIMEOUT_NO_ANSWER");
        hashMap.put(203, "CODE_TIMEOUT_NO_ANSWER_CALL_UPDATE");
        hashMap.put(240, "CODE_CALL_BARRED");
        hashMap.put(241, "CODE_FDN_BLOCKED");
        hashMap.put(243, "CODE_IMEI_NOT_ACCEPTED");
        hashMap.put(244, "CODE_DIAL_MODIFIED_TO_USSD");
        hashMap.put(245, "CODE_DIAL_MODIFIED_TO_SS");
        hashMap.put(246, "CODE_DIAL_MODIFIED_TO_DIAL");
        hashMap.put(247, "CODE_DIAL_MODIFIED_TO_DIAL_VIDEO");
        hashMap.put(248, "CODE_DIAL_VIDEO_MODIFIED_TO_DIAL");
        hashMap.put(249, "CODE_DIAL_VIDEO_MODIFIED_TO_DIAL_VIDEO");
        hashMap.put(250, "CODE_DIAL_VIDEO_MODIFIED_TO_SS");
        hashMap.put(251, "CODE_DIAL_VIDEO_MODIFIED_TO_USSD");
        hashMap.put(321, "CODE_SIP_REDIRECTED");
        hashMap.put(331, "CODE_SIP_BAD_REQUEST");
        hashMap.put(332, "CODE_SIP_FORBIDDEN");
        hashMap.put(333, "CODE_SIP_NOT_FOUND");
        hashMap.put(334, "CODE_SIP_NOT_SUPPORTED");
        hashMap.put(335, "CODE_SIP_REQUEST_TIMEOUT");
        hashMap.put(336, "CODE_SIP_TEMPRARILY_UNAVAILABLE");
        hashMap.put(337, "CODE_SIP_BAD_ADDRESS");
        hashMap.put(338, "CODE_SIP_BUSY");
        hashMap.put(339, "CODE_SIP_REQUEST_CANCELLED");
        hashMap.put(340, "CODE_SIP_NOT_ACCEPTABLE");
        hashMap.put(341, "CODE_SIP_NOT_REACHABLE");
        hashMap.put(342, "CODE_SIP_CLIENT_ERROR");
        hashMap.put(343, "CODE_SIP_TRANSACTION_DOES_NOT_EXIST");
        hashMap.put(351, "CODE_SIP_SERVER_INTERNAL_ERROR");
        hashMap.put(352, "CODE_SIP_SERVICE_UNAVAILABLE");
        hashMap.put(353, "CODE_SIP_SERVER_TIMEOUT");
        hashMap.put(354, "CODE_SIP_SERVER_ERROR");
        hashMap.put(361, "CODE_SIP_USER_REJECTED");
        hashMap.put(362, "CODE_SIP_GLOBAL_ERROR");
        hashMap.put(363, "CODE_EMERGENCY_TEMP_FAILURE");
        hashMap.put(364, "CODE_EMERGENCY_PERM_FAILURE");
        hashMap.put(365, "CODE_SIP_USER_MARKED_UNWANTED");
        hashMap.put(366, "CODE_SIP_METHOD_NOT_ALLOWED");
        hashMap.put(367, "CODE_SIP_PROXY_AUTHENTICATION_REQUIRED");
        hashMap.put(368, "CODE_SIP_REQUEST_ENTITY_TOO_LARGE");
        hashMap.put(369, "CODE_SIP_REQUEST_URI_TOO_LARGE");
        hashMap.put(370, "CODE_SIP_EXTENSION_REQUIRED");
        hashMap.put(371, "CODE_SIP_INTERVAL_TOO_BRIEF");
        hashMap.put(372, "CODE_SIP_CALL_OR_TRANS_DOES_NOT_EXIST");
        hashMap.put(373, "CODE_SIP_LOOP_DETECTED");
        hashMap.put(374, "CODE_SIP_TOO_MANY_HOPS");
        hashMap.put(376, "CODE_SIP_AMBIGUOUS");
        hashMap.put(377, "CODE_SIP_REQUEST_PENDING");
        hashMap.put(378, "CODE_SIP_UNDECIPHERABLE");
        hashMap.put(401, "CODE_MEDIA_INIT_FAILED");
        hashMap.put(402, "CODE_MEDIA_NO_DATA");
        hashMap.put(403, "CODE_MEDIA_NOT_ACCEPTABLE");
        hashMap.put(404, "CODE_MEDIA_UNSPECIFIED");
        hashMap.put(501, "CODE_USER_TERMINATED");
        hashMap.put(502, "CODE_USER_NOANSWER");
        hashMap.put(503, "CODE_USER_IGNORE");
        hashMap.put(504, "CODE_USER_DECLINE");
        hashMap.put(505, "CODE_LOW_BATTERY");
        hashMap.put(506, "CODE_BLACKLISTED_CALL_ID");
        hashMap.put(510, "CODE_USER_TERMINATED_BY_REMOTE");
        hashMap.put(511, "CODE_USER_REJECTED_SESSION_MODIFICATION");
        hashMap.put(512, "CODE_USER_CANCELLED_SESSION_MODIFICATION");
        hashMap.put(1517, "CODE_SESSION_MODIFICATION_FAILED");
        hashMap.put(801, "CODE_UT_NOT_SUPPORTED");
        hashMap.put(802, "CODE_UT_SERVICE_UNAVAILABLE");
        hashMap.put(803, "CODE_UT_OPERATION_NOT_ALLOWED");
        hashMap.put(804, "CODE_UT_NETWORK_ERROR");
        hashMap.put(821, "CODE_UT_CB_PASSWORD_MISMATCH");
        hashMap.put(822, "CODE_UT_SS_MODIFIED_TO_DIAL");
        hashMap.put(823, "CODE_UT_SS_MODIFIED_TO_USSD");
        hashMap.put(824, "CODE_UT_SS_MODIFIED_TO_SS");
        hashMap.put(825, "CODE_UT_SS_MODIFIED_TO_DIAL_VIDEO");
        hashMap.put(901, "CODE_ECBM_NOT_SUPPORTED");
        hashMap.put(902, "CODE_MULTIENDPOINT_NOT_SUPPORTED");
        hashMap.put(1000, "CODE_REGISTRATION_ERROR");
        hashMap.put(1014, "CODE_ANSWERED_ELSEWHERE");
        hashMap.put(1015, "CODE_CALL_PULL_OUT_OF_SYNC");
        hashMap.put(1016, "CODE_CALL_END_CAUSE_CALL_PULL");
        hashMap.put(1100, "CODE_CALL_DROP_IWLAN_TO_LTE_UNAVAILABLE");
        hashMap.put(1017, "CODE_REJECTED_ELSEWHERE");
        hashMap.put(1201, "CODE_SUPP_SVC_FAILED");
        hashMap.put(1202, "CODE_SUPP_SVC_CANCELLED");
        hashMap.put(1203, "CODE_SUPP_SVC_REINVITE_COLLISION");
        hashMap.put(1300, "CODE_IWLAN_DPD_FAILURE");
        hashMap.put(1400, "CODE_EPDG_TUNNEL_ESTABLISH_FAILURE");
        hashMap.put(1401, "CODE_EPDG_TUNNEL_REKEY_FAILURE");
        hashMap.put(1402, "CODE_EPDG_TUNNEL_LOST_CONNECTION");
        hashMap.put(1403, "CODE_MAXIMUM_NUMBER_OF_CALLS_REACHED");
        hashMap.put(1404, "CODE_REMOTE_CALL_DECLINE");
        hashMap.put(1405, "CODE_DATA_LIMIT_REACHED");
        hashMap.put(1406, "CODE_DATA_DISABLED");
        hashMap.put(1407, "CODE_WIFI_LOST");
        hashMap.put(1408, "CODE_IKEV2_AUTH_FAILURE");
        hashMap.put(1500, "CODE_RADIO_OFF");
        hashMap.put(1501, "CODE_NO_VALID_SIM");
        hashMap.put(1502, "CODE_RADIO_INTERNAL_ERROR");
        hashMap.put(1503, "CODE_NETWORK_RESP_TIMEOUT");
        hashMap.put(1504, "CODE_NETWORK_REJECT");
        hashMap.put(1505, "CODE_RADIO_ACCESS_FAILURE");
        hashMap.put(1506, "CODE_RADIO_LINK_FAILURE");
        hashMap.put(1507, "CODE_RADIO_LINK_LOST");
        hashMap.put(1508, "CODE_RADIO_UPLINK_FAILURE");
        hashMap.put(1509, "CODE_RADIO_SETUP_FAILURE");
        hashMap.put(1510, "CODE_RADIO_RELEASE_NORMAL");
        hashMap.put(1511, "CODE_RADIO_RELEASE_ABNORMAL");
        hashMap.put(1512, "CODE_ACCESS_CLASS_BLOCKED");
        hashMap.put(1513, "CODE_NETWORK_DETACH");
        hashMap.put(1514, "CODE_SIP_ALTERNATE_EMERGENCY_CALL");
        hashMap.put(1515, "CODE_UNOBTAINABLE_NUMBER");
        hashMap.put(1516, "CODE_NO_CSFB_IN_CS_ROAM");
        hashMap.put(1600, "CODE_REJECT_UNKNOWN");
        hashMap.put(1601, "CODE_REJECT_ONGOING_CALL_WAITING_DISABLED");
        hashMap.put(1602, "CODE_REJECT_CALL_ON_OTHER_SUB");
        hashMap.put(1603, "CODE_REJECT_1X_COLLISION");
        hashMap.put(1604, "CODE_REJECT_SERVICE_NOT_REGISTERED");
        hashMap.put(1605, "CODE_REJECT_CALL_TYPE_NOT_ALLOWED");
        hashMap.put(1606, "CODE_REJECT_ONGOING_E911_CALL");
        hashMap.put(1607, "CODE_REJECT_ONGOING_CALL_SETUP");
        hashMap.put(1608, "CODE_REJECT_MAX_CALL_LIMIT_REACHED");
        hashMap.put(1609, "CODE_REJECT_UNSUPPORTED_SIP_HEADERS");
        hashMap.put(1610, "CODE_REJECT_UNSUPPORTED_SDP_HEADERS");
        hashMap.put(1611, "CODE_REJECT_ONGOING_CALL_TRANSFER");
        hashMap.put(1612, "CODE_REJECT_INTERNAL_ERROR");
        hashMap.put(1613, "CODE_REJECT_QOS_FAILURE");
        hashMap.put(1614, "CODE_REJECT_ONGOING_HANDOVER");
        hashMap.put(1615, "CODE_REJECT_VT_TTY_NOT_ALLOWED");
        hashMap.put(1616, "CODE_REJECT_ONGOING_CALL_UPGRADE");
        hashMap.put(1617, "CODE_REJECT_CONFERENCE_TTY_NOT_ALLOWED");
        hashMap.put(1618, "CODE_REJECT_ONGOING_CONFERENCE_CALL");
        hashMap.put(1619, "CODE_REJECT_VT_AVPF_NOT_ALLOWED");
        hashMap.put(1620, "CODE_REJECT_ONGOING_ENCRYPTED_CALL");
        hashMap.put(1621, "CODE_REJECT_ONGOING_CS_CALL");
        hashMap.put(1624, "CODE_NETWORK_CONGESTION");
        hashMap.put(Integer.valueOf((int) CODE_RETRY_ON_IMS_WITHOUT_RTT), "CODE_RETRY_ON_IMS_WITHOUT_RTT");
        hashMap.put(61441, "CODE_OEM_CAUSE_1");
        hashMap.put(61442, "CODE_OEM_CAUSE_2");
        hashMap.put(61443, "CODE_OEM_CAUSE_3");
        hashMap.put(61444, "CODE_OEM_CAUSE_4");
        hashMap.put(61445, "CODE_OEM_CAUSE_5");
        hashMap.put(61446, "CODE_OEM_CAUSE_6");
        hashMap.put(61447, "CODE_OEM_CAUSE_7");
        hashMap.put(61448, "CODE_OEM_CAUSE_8");
        hashMap.put(61449, "CODE_OEM_CAUSE_9");
        hashMap.put(61450, "CODE_OEM_CAUSE_10");
        hashMap.put(61451, "CODE_OEM_CAUSE_11");
        hashMap.put(61452, "CODE_OEM_CAUSE_12");
        hashMap.put(61453, "CODE_OEM_CAUSE_13");
        hashMap.put(61454, "CODE_OEM_CAUSE_14");
        hashMap.put(61455, "CODE_OEM_CAUSE_15");
        CREATOR = new Parcelable.Creator<ImsReasonInfo>() { // from class: android.telephony.ims.ImsReasonInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ImsReasonInfo createFromParcel(Parcel in) {
                return new ImsReasonInfo(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ImsReasonInfo[] newArray(int size) {
                return new ImsReasonInfo[size];
            }
        };
    }

    public ImsReasonInfo() {
        this.mCode = 0;
        this.mExtraCode = 0;
        this.mExtraMessage = null;
    }

    private ImsReasonInfo(Parcel in) {
        this.mCode = in.readInt();
        this.mExtraCode = in.readInt();
        this.mExtraMessage = in.readString();
    }

    public ImsReasonInfo(int code, int extraCode) {
        this.mCode = code;
        this.mExtraCode = extraCode;
        this.mExtraMessage = null;
    }

    public ImsReasonInfo(int code, int extraCode, String extraMessage) {
        this.mCode = code;
        this.mExtraCode = extraCode;
        this.mExtraMessage = extraMessage;
    }

    public int getCode() {
        return this.mCode;
    }

    public int getExtraCode() {
        return this.mExtraCode;
    }

    public String getExtraMessage() {
        return this.mExtraMessage;
    }

    public String toString() {
        Map<Integer, String> map = sImsCodeMap;
        String imsCode = map.containsKey(Integer.valueOf(this.mCode)) ? map.get(Integer.valueOf(this.mCode)) : "UNKNOWN_CODE";
        return "ImsReasonInfo :: {" + this.mCode + " : " + imsCode + ", " + this.mExtraCode + ", " + this.mExtraMessage + "}";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mCode);
        out.writeInt(this.mExtraCode);
        out.writeString(this.mExtraMessage);
    }
}
