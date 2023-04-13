package android.hardware.radio.voice;

import android.database.sqlite.SQLiteDatabase;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import java.lang.reflect.Array;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public interface LastCallFailCause$$ {
    static String toString(int _aidl_v) {
        return _aidl_v == 1 ? "UNOBTAINABLE_NUMBER" : _aidl_v == 3 ? "NO_ROUTE_TO_DESTINATION" : _aidl_v == 6 ? "CHANNEL_UNACCEPTABLE" : _aidl_v == 8 ? "OPERATOR_DETERMINED_BARRING" : _aidl_v == 16 ? SQLiteDatabase.SYNC_MODE_NORMAL : _aidl_v == 17 ? "BUSY" : _aidl_v == 18 ? "NO_USER_RESPONDING" : _aidl_v == 19 ? "NO_ANSWER_FROM_USER" : _aidl_v == 21 ? "CALL_REJECTED" : _aidl_v == 22 ? "NUMBER_CHANGED" : _aidl_v == 25 ? "PREEMPTION" : _aidl_v == 27 ? "DESTINATION_OUT_OF_ORDER" : _aidl_v == 28 ? "INVALID_NUMBER_FORMAT" : _aidl_v == 29 ? "FACILITY_REJECTED" : _aidl_v == 30 ? "RESP_TO_STATUS_ENQUIRY" : _aidl_v == 31 ? "NORMAL_UNSPECIFIED" : _aidl_v == 34 ? "CONGESTION" : _aidl_v == 38 ? "NETWORK_OUT_OF_ORDER" : _aidl_v == 41 ? "TEMPORARY_FAILURE" : _aidl_v == 42 ? "SWITCHING_EQUIPMENT_CONGESTION" : _aidl_v == 43 ? "ACCESS_INFORMATION_DISCARDED" : _aidl_v == 44 ? "REQUESTED_CIRCUIT_OR_CHANNEL_NOT_AVAILABLE" : _aidl_v == 47 ? "RESOURCES_UNAVAILABLE_OR_UNSPECIFIED" : _aidl_v == 49 ? "QOS_UNAVAILABLE" : _aidl_v == 50 ? "REQUESTED_FACILITY_NOT_SUBSCRIBED" : _aidl_v == 55 ? "INCOMING_CALLS_BARRED_WITHIN_CUG" : _aidl_v == 57 ? "BEARER_CAPABILITY_NOT_AUTHORIZED" : _aidl_v == 58 ? "BEARER_CAPABILITY_UNAVAILABLE" : _aidl_v == 63 ? "SERVICE_OPTION_NOT_AVAILABLE" : _aidl_v == 65 ? "BEARER_SERVICE_NOT_IMPLEMENTED" : _aidl_v == 68 ? "ACM_LIMIT_EXCEEDED" : _aidl_v == 69 ? "REQUESTED_FACILITY_NOT_IMPLEMENTED" : _aidl_v == 70 ? "ONLY_DIGITAL_INFORMATION_BEARER_AVAILABLE" : _aidl_v == 79 ? "SERVICE_OR_OPTION_NOT_IMPLEMENTED" : _aidl_v == 81 ? "INVALID_TRANSACTION_IDENTIFIER" : _aidl_v == 87 ? "USER_NOT_MEMBER_OF_CUG" : _aidl_v == 88 ? "INCOMPATIBLE_DESTINATION" : _aidl_v == 91 ? "INVALID_TRANSIT_NW_SELECTION" : _aidl_v == 95 ? "SEMANTICALLY_INCORRECT_MESSAGE" : _aidl_v == 96 ? "INVALID_MANDATORY_INFORMATION" : _aidl_v == 97 ? "MESSAGE_TYPE_NON_IMPLEMENTED" : _aidl_v == 98 ? "MESSAGE_TYPE_NOT_COMPATIBLE_WITH_PROTOCOL_STATE" : _aidl_v == 99 ? "INFORMATION_ELEMENT_NON_EXISTENT" : _aidl_v == 100 ? "CONDITIONAL_IE_ERROR" : _aidl_v == 101 ? "MESSAGE_NOT_COMPATIBLE_WITH_PROTOCOL_STATE" : _aidl_v == 102 ? "RECOVERY_ON_TIMER_EXPIRED" : _aidl_v == 111 ? "PROTOCOL_ERROR_UNSPECIFIED" : _aidl_v == 127 ? "INTERWORKING_UNSPECIFIED" : _aidl_v == 240 ? "CALL_BARRED" : _aidl_v == 241 ? "FDN_BLOCKED" : _aidl_v == 242 ? "IMSI_UNKNOWN_IN_VLR" : _aidl_v == 243 ? "IMEI_NOT_ACCEPTED" : _aidl_v == 244 ? "DIAL_MODIFIED_TO_USSD" : _aidl_v == 245 ? "DIAL_MODIFIED_TO_SS" : _aidl_v == 246 ? "DIAL_MODIFIED_TO_DIAL" : _aidl_v == 247 ? "RADIO_OFF" : _aidl_v == 248 ? "OUT_OF_SERVICE" : _aidl_v == 249 ? "NO_VALID_SIM" : _aidl_v == 250 ? "RADIO_INTERNAL_ERROR" : _aidl_v == 251 ? "NETWORK_RESP_TIMEOUT" : _aidl_v == 252 ? "NETWORK_REJECT" : _aidl_v == 253 ? "RADIO_ACCESS_FAILURE" : _aidl_v == 254 ? "RADIO_LINK_FAILURE" : _aidl_v == 255 ? "RADIO_LINK_LOST" : _aidl_v == 256 ? "RADIO_UPLINK_FAILURE" : _aidl_v == 257 ? "RADIO_SETUP_FAILURE" : _aidl_v == 258 ? "RADIO_RELEASE_NORMAL" : _aidl_v == 259 ? "RADIO_RELEASE_ABNORMAL" : _aidl_v == 260 ? "ACCESS_CLASS_BLOCKED" : _aidl_v == 261 ? "NETWORK_DETACH" : _aidl_v == 1000 ? "CDMA_LOCKED_UNTIL_POWER_CYCLE" : _aidl_v == 1001 ? "CDMA_DROP" : _aidl_v == 1002 ? "CDMA_INTERCEPT" : _aidl_v == 1003 ? "CDMA_REORDER" : _aidl_v == 1004 ? "CDMA_SO_REJECT" : _aidl_v == 1005 ? "CDMA_RETRY_ORDER" : _aidl_v == 1006 ? "CDMA_ACCESS_FAILURE" : _aidl_v == 1007 ? "CDMA_PREEMPTED" : _aidl_v == 1008 ? "CDMA_NOT_EMERGENCY" : _aidl_v == 1009 ? "CDMA_ACCESS_BLOCKED" : _aidl_v == 61441 ? "OEM_CAUSE_1" : _aidl_v == 61442 ? "OEM_CAUSE_2" : _aidl_v == 61443 ? "OEM_CAUSE_3" : _aidl_v == 61444 ? "OEM_CAUSE_4" : _aidl_v == 61445 ? "OEM_CAUSE_5" : _aidl_v == 61446 ? "OEM_CAUSE_6" : _aidl_v == 61447 ? "OEM_CAUSE_7" : _aidl_v == 61448 ? "OEM_CAUSE_8" : _aidl_v == 61449 ? "OEM_CAUSE_9" : _aidl_v == 61450 ? "OEM_CAUSE_10" : _aidl_v == 61451 ? "OEM_CAUSE_11" : _aidl_v == 61452 ? "OEM_CAUSE_12" : _aidl_v == 61453 ? "OEM_CAUSE_13" : _aidl_v == 61454 ? "OEM_CAUSE_14" : _aidl_v == 61455 ? "OEM_CAUSE_15" : _aidl_v == 65535 ? "ERROR_UNSPECIFIED" : Integer.toString(_aidl_v);
    }

    static String arrayToString(Object _aidl_v) {
        int[] iArr;
        if (_aidl_v == null) {
            return "null";
        }
        Class<?> _aidl_cls = _aidl_v.getClass();
        if (!_aidl_cls.isArray()) {
            throw new IllegalArgumentException("not an array: " + _aidl_v);
        }
        Class<?> comp = _aidl_cls.getComponentType();
        StringJoiner _aidl_sj = new StringJoiner(", ", NavigationBarInflaterView.SIZE_MOD_START, NavigationBarInflaterView.SIZE_MOD_END);
        if (comp.isArray()) {
            for (int _aidl_i = 0; _aidl_i < Array.getLength(_aidl_v); _aidl_i++) {
                _aidl_sj.add(arrayToString(Array.get(_aidl_v, _aidl_i)));
            }
        } else if (_aidl_cls != int[].class) {
            throw new IllegalArgumentException("wrong type: " + _aidl_cls);
        } else {
            for (int e : (int[]) _aidl_v) {
                _aidl_sj.add(toString(e));
            }
        }
        return _aidl_sj.toString();
    }
}
