package android.hardware.radio.V1_5;

import android.security.keystore.KeyProperties;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class RegistrationFailCause {
    public static final int CALL_CANNOT_BE_IDENTIFIED = 38;
    public static final int CONDITIONAL_IE_ERROR = 100;
    public static final int CONGESTION = 22;
    public static final int GPRS_AND_NON_GPRS_SERVICES_NOT_ALLOWED = 8;
    public static final int GPRS_SERVICES_NOT_ALLOWED = 7;
    public static final int GPRS_SERVICES_NOT_ALLOWED_IN_PLMN = 14;
    public static final int GSM_AUTHENTICATION_UNACCEPTABLE = 23;
    public static final int ILLEGAL_ME = 6;
    public static final int ILLEGAL_MS = 3;
    public static final int IMEI_NOT_ACCEPTED = 5;
    public static final int IMPLICITLY_DETACHED = 10;
    public static final int IMSI_UNKNOWN_IN_HLR = 2;
    public static final int IMSI_UNKNOWN_IN_VLR = 4;
    public static final int INFORMATION_ELEMENT_NON_EXISTENT_OR_NOT_IMPLEMENTED = 99;
    public static final int INVALID_MANDATORY_INFORMATION = 96;
    public static final int LOCATION_AREA_NOT_ALLOWED = 12;
    public static final int MAC_FAILURE = 20;
    public static final int MESSAGE_NOT_COMPATIBLE_WITH_PROTOCOL_STATE = 101;
    public static final int MESSAGE_TYPE_NON_EXISTENT_OR_NOT_IMPLEMENTED = 97;
    public static final int MESSAGE_TYPE_NOT_COMPATIBLE_WITH_PROTOCOL_STATE = 98;
    public static final int MSC_TEMPORARILY_NOT_REACHABLE = 15;
    public static final int MS_IDENTITY_CANNOT_BE_DERIVED_BY_NETWORK = 9;
    public static final int NETWORK_FAILURE = 17;
    public static final int NONE = 0;
    public static final int NOT_AUTHORIZED_FOR_THIS_CSG = 25;
    public static final int NO_PDP_CONTEXT_ACTIVATED = 40;
    public static final int NO_SUITABLE_CELLS = 15;
    public static final int PLMN_NOT_ALLOWED = 11;
    public static final int PROTOCOL_ERROR_UNSPECIFIED = 111;
    public static final int RETRY_UPON_ENTRY_INTO_NEW_CELL_1 = 48;
    public static final int RETRY_UPON_ENTRY_INTO_NEW_CELL_10 = 57;
    public static final int RETRY_UPON_ENTRY_INTO_NEW_CELL_11 = 58;
    public static final int RETRY_UPON_ENTRY_INTO_NEW_CELL_12 = 59;
    public static final int RETRY_UPON_ENTRY_INTO_NEW_CELL_13 = 60;
    public static final int RETRY_UPON_ENTRY_INTO_NEW_CELL_14 = 61;
    public static final int RETRY_UPON_ENTRY_INTO_NEW_CELL_15 = 62;
    public static final int RETRY_UPON_ENTRY_INTO_NEW_CELL_16 = 63;
    public static final int RETRY_UPON_ENTRY_INTO_NEW_CELL_2 = 49;
    public static final int RETRY_UPON_ENTRY_INTO_NEW_CELL_3 = 50;
    public static final int RETRY_UPON_ENTRY_INTO_NEW_CELL_4 = 51;
    public static final int RETRY_UPON_ENTRY_INTO_NEW_CELL_5 = 52;
    public static final int RETRY_UPON_ENTRY_INTO_NEW_CELL_6 = 53;
    public static final int RETRY_UPON_ENTRY_INTO_NEW_CELL_7 = 54;
    public static final int RETRY_UPON_ENTRY_INTO_NEW_CELL_8 = 55;
    public static final int RETRY_UPON_ENTRY_INTO_NEW_CELL_9 = 56;
    public static final int ROAMING_NOT_ALLOWED = 13;
    public static final int SEMANTICALLY_INCORRECT_MESSAGE = 95;
    public static final int SERVICE_OPTION_NOT_SUBSCRIBED = 33;
    public static final int SERVICE_OPTION_NOT_SUPPORTED = 32;
    public static final int SERVICE_OPTION_TEMPORARILY_OUT_OF_ORDER = 34;
    public static final int SMS_PROVIDED_BY_GPRS_IN_ROUTING_AREA = 26;
    public static final int SYNC_FAILURE = 21;

    public static final String toString(int o) {
        if (o == 0) {
            return KeyProperties.DIGEST_NONE;
        }
        if (o == 2) {
            return "IMSI_UNKNOWN_IN_HLR";
        }
        if (o == 3) {
            return "ILLEGAL_MS";
        }
        if (o == 4) {
            return "IMSI_UNKNOWN_IN_VLR";
        }
        if (o == 5) {
            return "IMEI_NOT_ACCEPTED";
        }
        if (o == 6) {
            return "ILLEGAL_ME";
        }
        if (o == 7) {
            return "GPRS_SERVICES_NOT_ALLOWED";
        }
        if (o == 8) {
            return "GPRS_AND_NON_GPRS_SERVICES_NOT_ALLOWED";
        }
        if (o == 9) {
            return "MS_IDENTITY_CANNOT_BE_DERIVED_BY_NETWORK";
        }
        if (o == 10) {
            return "IMPLICITLY_DETACHED";
        }
        if (o == 11) {
            return "PLMN_NOT_ALLOWED";
        }
        if (o == 12) {
            return "LOCATION_AREA_NOT_ALLOWED";
        }
        if (o == 13) {
            return "ROAMING_NOT_ALLOWED";
        }
        if (o == 14) {
            return "GPRS_SERVICES_NOT_ALLOWED_IN_PLMN";
        }
        if (o == 15) {
            return "NO_SUITABLE_CELLS";
        }
        if (o == 15) {
            return "MSC_TEMPORARILY_NOT_REACHABLE";
        }
        if (o == 17) {
            return "NETWORK_FAILURE";
        }
        if (o == 20) {
            return "MAC_FAILURE";
        }
        if (o == 21) {
            return "SYNC_FAILURE";
        }
        if (o == 22) {
            return "CONGESTION";
        }
        if (o == 23) {
            return "GSM_AUTHENTICATION_UNACCEPTABLE";
        }
        if (o == 25) {
            return "NOT_AUTHORIZED_FOR_THIS_CSG";
        }
        if (o == 26) {
            return "SMS_PROVIDED_BY_GPRS_IN_ROUTING_AREA";
        }
        if (o == 32) {
            return "SERVICE_OPTION_NOT_SUPPORTED";
        }
        if (o == 33) {
            return "SERVICE_OPTION_NOT_SUBSCRIBED";
        }
        if (o == 34) {
            return "SERVICE_OPTION_TEMPORARILY_OUT_OF_ORDER";
        }
        if (o == 38) {
            return "CALL_CANNOT_BE_IDENTIFIED";
        }
        if (o == 40) {
            return "NO_PDP_CONTEXT_ACTIVATED";
        }
        if (o == 48) {
            return "RETRY_UPON_ENTRY_INTO_NEW_CELL_1";
        }
        if (o == 49) {
            return "RETRY_UPON_ENTRY_INTO_NEW_CELL_2";
        }
        if (o == 50) {
            return "RETRY_UPON_ENTRY_INTO_NEW_CELL_3";
        }
        if (o == 51) {
            return "RETRY_UPON_ENTRY_INTO_NEW_CELL_4";
        }
        if (o == 52) {
            return "RETRY_UPON_ENTRY_INTO_NEW_CELL_5";
        }
        if (o == 53) {
            return "RETRY_UPON_ENTRY_INTO_NEW_CELL_6";
        }
        if (o == 54) {
            return "RETRY_UPON_ENTRY_INTO_NEW_CELL_7";
        }
        if (o == 55) {
            return "RETRY_UPON_ENTRY_INTO_NEW_CELL_8";
        }
        if (o == 56) {
            return "RETRY_UPON_ENTRY_INTO_NEW_CELL_9";
        }
        if (o == 57) {
            return "RETRY_UPON_ENTRY_INTO_NEW_CELL_10";
        }
        if (o == 58) {
            return "RETRY_UPON_ENTRY_INTO_NEW_CELL_11";
        }
        if (o == 59) {
            return "RETRY_UPON_ENTRY_INTO_NEW_CELL_12";
        }
        if (o == 60) {
            return "RETRY_UPON_ENTRY_INTO_NEW_CELL_13";
        }
        if (o == 61) {
            return "RETRY_UPON_ENTRY_INTO_NEW_CELL_14";
        }
        if (o == 62) {
            return "RETRY_UPON_ENTRY_INTO_NEW_CELL_15";
        }
        if (o == 63) {
            return "RETRY_UPON_ENTRY_INTO_NEW_CELL_16";
        }
        if (o == 95) {
            return "SEMANTICALLY_INCORRECT_MESSAGE";
        }
        if (o == 96) {
            return "INVALID_MANDATORY_INFORMATION";
        }
        if (o == 97) {
            return "MESSAGE_TYPE_NON_EXISTENT_OR_NOT_IMPLEMENTED";
        }
        if (o == 98) {
            return "MESSAGE_TYPE_NOT_COMPATIBLE_WITH_PROTOCOL_STATE";
        }
        if (o == 99) {
            return "INFORMATION_ELEMENT_NON_EXISTENT_OR_NOT_IMPLEMENTED";
        }
        if (o == 100) {
            return "CONDITIONAL_IE_ERROR";
        }
        if (o == 101) {
            return "MESSAGE_NOT_COMPATIBLE_WITH_PROTOCOL_STATE";
        }
        if (o == 111) {
            return "PROTOCOL_ERROR_UNSPECIFIED";
        }
        return "0x" + Integer.toHexString(o);
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList<>();
        int flipped = 0;
        list.add(KeyProperties.DIGEST_NONE);
        if ((o & 2) == 2) {
            list.add("IMSI_UNKNOWN_IN_HLR");
            flipped = 0 | 2;
        }
        if ((o & 3) == 3) {
            list.add("ILLEGAL_MS");
            flipped |= 3;
        }
        if ((o & 4) == 4) {
            list.add("IMSI_UNKNOWN_IN_VLR");
            flipped |= 4;
        }
        if ((o & 5) == 5) {
            list.add("IMEI_NOT_ACCEPTED");
            flipped |= 5;
        }
        if ((o & 6) == 6) {
            list.add("ILLEGAL_ME");
            flipped |= 6;
        }
        if ((o & 7) == 7) {
            list.add("GPRS_SERVICES_NOT_ALLOWED");
            flipped |= 7;
        }
        if ((o & 8) == 8) {
            list.add("GPRS_AND_NON_GPRS_SERVICES_NOT_ALLOWED");
            flipped |= 8;
        }
        if ((o & 9) == 9) {
            list.add("MS_IDENTITY_CANNOT_BE_DERIVED_BY_NETWORK");
            flipped |= 9;
        }
        if ((o & 10) == 10) {
            list.add("IMPLICITLY_DETACHED");
            flipped |= 10;
        }
        if ((o & 11) == 11) {
            list.add("PLMN_NOT_ALLOWED");
            flipped |= 11;
        }
        if ((o & 12) == 12) {
            list.add("LOCATION_AREA_NOT_ALLOWED");
            flipped |= 12;
        }
        if ((o & 13) == 13) {
            list.add("ROAMING_NOT_ALLOWED");
            flipped |= 13;
        }
        if ((o & 14) == 14) {
            list.add("GPRS_SERVICES_NOT_ALLOWED_IN_PLMN");
            flipped |= 14;
        }
        if ((o & 15) == 15) {
            list.add("NO_SUITABLE_CELLS");
            flipped |= 15;
        }
        if ((o & 15) == 15) {
            list.add("MSC_TEMPORARILY_NOT_REACHABLE");
            flipped |= 15;
        }
        if ((o & 17) == 17) {
            list.add("NETWORK_FAILURE");
            flipped |= 17;
        }
        if ((o & 20) == 20) {
            list.add("MAC_FAILURE");
            flipped |= 20;
        }
        if ((o & 21) == 21) {
            list.add("SYNC_FAILURE");
            flipped |= 21;
        }
        if ((o & 22) == 22) {
            list.add("CONGESTION");
            flipped |= 22;
        }
        if ((o & 23) == 23) {
            list.add("GSM_AUTHENTICATION_UNACCEPTABLE");
            flipped |= 23;
        }
        if ((o & 25) == 25) {
            list.add("NOT_AUTHORIZED_FOR_THIS_CSG");
            flipped |= 25;
        }
        if ((o & 26) == 26) {
            list.add("SMS_PROVIDED_BY_GPRS_IN_ROUTING_AREA");
            flipped |= 26;
        }
        if ((o & 32) == 32) {
            list.add("SERVICE_OPTION_NOT_SUPPORTED");
            flipped |= 32;
        }
        if ((o & 33) == 33) {
            list.add("SERVICE_OPTION_NOT_SUBSCRIBED");
            flipped |= 33;
        }
        if ((o & 34) == 34) {
            list.add("SERVICE_OPTION_TEMPORARILY_OUT_OF_ORDER");
            flipped |= 34;
        }
        if ((o & 38) == 38) {
            list.add("CALL_CANNOT_BE_IDENTIFIED");
            flipped |= 38;
        }
        if ((o & 40) == 40) {
            list.add("NO_PDP_CONTEXT_ACTIVATED");
            flipped |= 40;
        }
        if ((o & 48) == 48) {
            list.add("RETRY_UPON_ENTRY_INTO_NEW_CELL_1");
            flipped |= 48;
        }
        if ((o & 49) == 49) {
            list.add("RETRY_UPON_ENTRY_INTO_NEW_CELL_2");
            flipped |= 49;
        }
        if ((o & 50) == 50) {
            list.add("RETRY_UPON_ENTRY_INTO_NEW_CELL_3");
            flipped |= 50;
        }
        if ((o & 51) == 51) {
            list.add("RETRY_UPON_ENTRY_INTO_NEW_CELL_4");
            flipped |= 51;
        }
        if ((o & 52) == 52) {
            list.add("RETRY_UPON_ENTRY_INTO_NEW_CELL_5");
            flipped |= 52;
        }
        if ((o & 53) == 53) {
            list.add("RETRY_UPON_ENTRY_INTO_NEW_CELL_6");
            flipped |= 53;
        }
        if ((o & 54) == 54) {
            list.add("RETRY_UPON_ENTRY_INTO_NEW_CELL_7");
            flipped |= 54;
        }
        if ((o & 55) == 55) {
            list.add("RETRY_UPON_ENTRY_INTO_NEW_CELL_8");
            flipped |= 55;
        }
        if ((o & 56) == 56) {
            list.add("RETRY_UPON_ENTRY_INTO_NEW_CELL_9");
            flipped |= 56;
        }
        if ((o & 57) == 57) {
            list.add("RETRY_UPON_ENTRY_INTO_NEW_CELL_10");
            flipped |= 57;
        }
        if ((o & 58) == 58) {
            list.add("RETRY_UPON_ENTRY_INTO_NEW_CELL_11");
            flipped |= 58;
        }
        if ((o & 59) == 59) {
            list.add("RETRY_UPON_ENTRY_INTO_NEW_CELL_12");
            flipped |= 59;
        }
        if ((o & 60) == 60) {
            list.add("RETRY_UPON_ENTRY_INTO_NEW_CELL_13");
            flipped |= 60;
        }
        if ((o & 61) == 61) {
            list.add("RETRY_UPON_ENTRY_INTO_NEW_CELL_14");
            flipped |= 61;
        }
        if ((o & 62) == 62) {
            list.add("RETRY_UPON_ENTRY_INTO_NEW_CELL_15");
            flipped |= 62;
        }
        if ((o & 63) == 63) {
            list.add("RETRY_UPON_ENTRY_INTO_NEW_CELL_16");
            flipped |= 63;
        }
        if ((o & 95) == 95) {
            list.add("SEMANTICALLY_INCORRECT_MESSAGE");
            flipped |= 95;
        }
        if ((o & 96) == 96) {
            list.add("INVALID_MANDATORY_INFORMATION");
            flipped |= 96;
        }
        if ((o & 97) == 97) {
            list.add("MESSAGE_TYPE_NON_EXISTENT_OR_NOT_IMPLEMENTED");
            flipped |= 97;
        }
        if ((o & 98) == 98) {
            list.add("MESSAGE_TYPE_NOT_COMPATIBLE_WITH_PROTOCOL_STATE");
            flipped |= 98;
        }
        if ((o & 99) == 99) {
            list.add("INFORMATION_ELEMENT_NON_EXISTENT_OR_NOT_IMPLEMENTED");
            flipped |= 99;
        }
        if ((o & 100) == 100) {
            list.add("CONDITIONAL_IE_ERROR");
            flipped |= 100;
        }
        if ((o & 101) == 101) {
            list.add("MESSAGE_NOT_COMPATIBLE_WITH_PROTOCOL_STATE");
            flipped |= 101;
        }
        if ((o & 111) == 111) {
            list.add("PROTOCOL_ERROR_UNSPECIFIED");
            flipped |= 111;
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString((~flipped) & o));
        }
        return String.join(" | ", list);
    }
}
