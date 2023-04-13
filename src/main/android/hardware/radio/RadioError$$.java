package android.hardware.radio;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.security.keystore.KeyProperties;
import java.lang.reflect.Array;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public interface RadioError$$ {
    static String toString(int _aidl_v) {
        return _aidl_v == 0 ? KeyProperties.DIGEST_NONE : _aidl_v == 1 ? "RADIO_NOT_AVAILABLE" : _aidl_v == 2 ? "GENERIC_FAILURE" : _aidl_v == 3 ? "PASSWORD_INCORRECT" : _aidl_v == 4 ? "SIM_PIN2" : _aidl_v == 5 ? "SIM_PUK2" : _aidl_v == 6 ? "REQUEST_NOT_SUPPORTED" : _aidl_v == 7 ? "CANCELLED" : _aidl_v == 8 ? "OP_NOT_ALLOWED_DURING_VOICE_CALL" : _aidl_v == 9 ? "OP_NOT_ALLOWED_BEFORE_REG_TO_NW" : _aidl_v == 10 ? "SMS_SEND_FAIL_RETRY" : _aidl_v == 11 ? "SIM_ABSENT" : _aidl_v == 12 ? "SUBSCRIPTION_NOT_AVAILABLE" : _aidl_v == 13 ? "MODE_NOT_SUPPORTED" : _aidl_v == 14 ? "FDN_CHECK_FAILURE" : _aidl_v == 15 ? "ILLEGAL_SIM_OR_ME" : _aidl_v == 16 ? "MISSING_RESOURCE" : _aidl_v == 17 ? "NO_SUCH_ELEMENT" : _aidl_v == 18 ? "DIAL_MODIFIED_TO_USSD" : _aidl_v == 19 ? "DIAL_MODIFIED_TO_SS" : _aidl_v == 20 ? "DIAL_MODIFIED_TO_DIAL" : _aidl_v == 21 ? "USSD_MODIFIED_TO_DIAL" : _aidl_v == 22 ? "USSD_MODIFIED_TO_SS" : _aidl_v == 23 ? "USSD_MODIFIED_TO_USSD" : _aidl_v == 24 ? "SS_MODIFIED_TO_DIAL" : _aidl_v == 25 ? "SS_MODIFIED_TO_USSD" : _aidl_v == 26 ? "SUBSCRIPTION_NOT_SUPPORTED" : _aidl_v == 27 ? "SS_MODIFIED_TO_SS" : _aidl_v == 36 ? "LCE_NOT_SUPPORTED" : _aidl_v == 37 ? "NO_MEMORY" : _aidl_v == 38 ? "INTERNAL_ERR" : _aidl_v == 39 ? "SYSTEM_ERR" : _aidl_v == 40 ? "MODEM_ERR" : _aidl_v == 41 ? "INVALID_STATE" : _aidl_v == 42 ? "NO_RESOURCES" : _aidl_v == 43 ? "SIM_ERR" : _aidl_v == 44 ? "INVALID_ARGUMENTS" : _aidl_v == 45 ? "INVALID_SIM_STATE" : _aidl_v == 46 ? "INVALID_MODEM_STATE" : _aidl_v == 47 ? "INVALID_CALL_ID" : _aidl_v == 48 ? "NO_SMS_TO_ACK" : _aidl_v == 49 ? "NETWORK_ERR" : _aidl_v == 50 ? "REQUEST_RATE_LIMITED" : _aidl_v == 51 ? "SIM_BUSY" : _aidl_v == 52 ? "SIM_FULL" : _aidl_v == 53 ? "NETWORK_REJECT" : _aidl_v == 54 ? "OPERATION_NOT_ALLOWED" : _aidl_v == 55 ? "EMPTY_RECORD" : _aidl_v == 56 ? "INVALID_SMS_FORMAT" : _aidl_v == 57 ? "ENCODING_ERR" : _aidl_v == 58 ? "INVALID_SMSC_ADDRESS" : _aidl_v == 59 ? "NO_SUCH_ENTRY" : _aidl_v == 60 ? "NETWORK_NOT_READY" : _aidl_v == 61 ? "NOT_PROVISIONED" : _aidl_v == 62 ? "NO_SUBSCRIPTION" : _aidl_v == 63 ? "NO_NETWORK_FOUND" : _aidl_v == 64 ? "DEVICE_IN_USE" : _aidl_v == 65 ? "ABORTED" : _aidl_v == 66 ? "INVALID_RESPONSE" : _aidl_v == 501 ? "OEM_ERROR_1" : _aidl_v == 502 ? "OEM_ERROR_2" : _aidl_v == 503 ? "OEM_ERROR_3" : _aidl_v == 504 ? "OEM_ERROR_4" : _aidl_v == 505 ? "OEM_ERROR_5" : _aidl_v == 506 ? "OEM_ERROR_6" : _aidl_v == 507 ? "OEM_ERROR_7" : _aidl_v == 508 ? "OEM_ERROR_8" : _aidl_v == 509 ? "OEM_ERROR_9" : _aidl_v == 510 ? "OEM_ERROR_10" : _aidl_v == 511 ? "OEM_ERROR_11" : _aidl_v == 512 ? "OEM_ERROR_12" : _aidl_v == 513 ? "OEM_ERROR_13" : _aidl_v == 514 ? "OEM_ERROR_14" : _aidl_v == 515 ? "OEM_ERROR_15" : _aidl_v == 516 ? "OEM_ERROR_16" : _aidl_v == 517 ? "OEM_ERROR_17" : _aidl_v == 518 ? "OEM_ERROR_18" : _aidl_v == 519 ? "OEM_ERROR_19" : _aidl_v == 520 ? "OEM_ERROR_20" : _aidl_v == 521 ? "OEM_ERROR_21" : _aidl_v == 522 ? "OEM_ERROR_22" : _aidl_v == 523 ? "OEM_ERROR_23" : _aidl_v == 524 ? "OEM_ERROR_24" : _aidl_v == 525 ? "OEM_ERROR_25" : _aidl_v == 67 ? "SIMULTANEOUS_SMS_AND_CALL_NOT_ALLOWED" : _aidl_v == 68 ? "ACCESS_BARRED" : _aidl_v == 69 ? "BLOCKED_DUE_TO_CALL" : _aidl_v == 70 ? "RF_HARDWARE_ISSUE" : _aidl_v == 71 ? "NO_RF_CALIBRATION_INFO" : _aidl_v == 72 ? "ENCODING_NOT_SUPPORTED" : _aidl_v == 73 ? "FEATURE_NOT_SUPPORTED" : _aidl_v == 74 ? "INVALID_CONTACT" : _aidl_v == 75 ? "MODEM_INCOMPATIBLE" : _aidl_v == 76 ? "NETWORK_TIMEOUT" : _aidl_v == 77 ? "NO_SATELLITE_SIGNAL" : _aidl_v == 78 ? "NOT_SUFFICIENT_ACCOUNT_BALANCE" : _aidl_v == 79 ? "RADIO_TECHNOLOGY_NOT_SUPPORTED" : _aidl_v == 80 ? "SUBSCRIBER_NOT_AUTHORIZED" : _aidl_v == 81 ? "SWITCHED_FROM_SATELLITE_TO_TERRESTRIAL" : _aidl_v == 82 ? "UNIDENTIFIED_SUBSCRIBER" : Integer.toString(_aidl_v);
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
