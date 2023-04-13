package android.hardware.radio.network;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.security.keystore.KeyProperties;
import java.lang.reflect.Array;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public interface RegistrationFailCause$$ {
    static String toString(int _aidl_v) {
        return _aidl_v == 0 ? KeyProperties.DIGEST_NONE : _aidl_v == 2 ? "IMSI_UNKNOWN_IN_HLR" : _aidl_v == 3 ? "ILLEGAL_MS" : _aidl_v == 4 ? "IMSI_UNKNOWN_IN_VLR" : _aidl_v == 5 ? "IMEI_NOT_ACCEPTED" : _aidl_v == 6 ? "ILLEGAL_ME" : _aidl_v == 7 ? "GPRS_SERVICES_NOT_ALLOWED" : _aidl_v == 8 ? "GPRS_AND_NON_GPRS_SERVICES_NOT_ALLOWED" : _aidl_v == 9 ? "MS_IDENTITY_CANNOT_BE_DERIVED_BY_NETWORK" : _aidl_v == 10 ? "IMPLICITLY_DETACHED" : _aidl_v == 11 ? "PLMN_NOT_ALLOWED" : _aidl_v == 12 ? "LOCATION_AREA_NOT_ALLOWED" : _aidl_v == 13 ? "ROAMING_NOT_ALLOWED" : _aidl_v == 14 ? "GPRS_SERVICES_NOT_ALLOWED_IN_PLMN" : _aidl_v == 15 ? "NO_SUITABLE_CELLS" : _aidl_v == 15 ? "MSC_TEMPORARILY_NOT_REACHABLE" : _aidl_v == 17 ? "NETWORK_FAILURE" : _aidl_v == 20 ? "MAC_FAILURE" : _aidl_v == 21 ? "SYNC_FAILURE" : _aidl_v == 22 ? "CONGESTION" : _aidl_v == 23 ? "GSM_AUTHENTICATION_UNACCEPTABLE" : _aidl_v == 25 ? "NOT_AUTHORIZED_FOR_THIS_CSG" : _aidl_v == 26 ? "SMS_PROVIDED_BY_GPRS_IN_ROUTING_AREA" : _aidl_v == 32 ? "SERVICE_OPTION_NOT_SUPPORTED" : _aidl_v == 33 ? "SERVICE_OPTION_NOT_SUBSCRIBED" : _aidl_v == 34 ? "SERVICE_OPTION_TEMPORARILY_OUT_OF_ORDER" : _aidl_v == 38 ? "CALL_CANNOT_BE_IDENTIFIED" : _aidl_v == 40 ? "NO_PDP_CONTEXT_ACTIVATED" : _aidl_v == 48 ? "RETRY_UPON_ENTRY_INTO_NEW_CELL_1" : _aidl_v == 49 ? "RETRY_UPON_ENTRY_INTO_NEW_CELL_2" : _aidl_v == 50 ? "RETRY_UPON_ENTRY_INTO_NEW_CELL_3" : _aidl_v == 51 ? "RETRY_UPON_ENTRY_INTO_NEW_CELL_4" : _aidl_v == 52 ? "RETRY_UPON_ENTRY_INTO_NEW_CELL_5" : _aidl_v == 53 ? "RETRY_UPON_ENTRY_INTO_NEW_CELL_6" : _aidl_v == 54 ? "RETRY_UPON_ENTRY_INTO_NEW_CELL_7" : _aidl_v == 55 ? "RETRY_UPON_ENTRY_INTO_NEW_CELL_8" : _aidl_v == 56 ? "RETRY_UPON_ENTRY_INTO_NEW_CELL_9" : _aidl_v == 57 ? "RETRY_UPON_ENTRY_INTO_NEW_CELL_10" : _aidl_v == 58 ? "RETRY_UPON_ENTRY_INTO_NEW_CELL_11" : _aidl_v == 59 ? "RETRY_UPON_ENTRY_INTO_NEW_CELL_12" : _aidl_v == 60 ? "RETRY_UPON_ENTRY_INTO_NEW_CELL_13" : _aidl_v == 61 ? "RETRY_UPON_ENTRY_INTO_NEW_CELL_14" : _aidl_v == 62 ? "RETRY_UPON_ENTRY_INTO_NEW_CELL_15" : _aidl_v == 63 ? "RETRY_UPON_ENTRY_INTO_NEW_CELL_16" : _aidl_v == 95 ? "SEMANTICALLY_INCORRECT_MESSAGE" : _aidl_v == 96 ? "INVALID_MANDATORY_INFORMATION" : _aidl_v == 97 ? "MESSAGE_TYPE_NON_EXISTENT_OR_NOT_IMPLEMENTED" : _aidl_v == 98 ? "MESSAGE_TYPE_NOT_COMPATIBLE_WITH_PROTOCOL_STATE" : _aidl_v == 99 ? "INFORMATION_ELEMENT_NON_EXISTENT_OR_NOT_IMPLEMENTED" : _aidl_v == 100 ? "CONDITIONAL_IE_ERROR" : _aidl_v == 101 ? "MESSAGE_NOT_COMPATIBLE_WITH_PROTOCOL_STATE" : _aidl_v == 111 ? "PROTOCOL_ERROR_UNSPECIFIED" : Integer.toString(_aidl_v);
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
