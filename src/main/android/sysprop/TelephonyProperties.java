package android.sysprop;

import android.media.AudioSystem;
import android.p008os.SystemProperties;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;
/* loaded from: classes3.dex */
public final class TelephonyProperties {
    private TelephonyProperties() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static Boolean tryParseBoolean(String str) {
        char c;
        if (str == null) {
            return null;
        }
        String lowerCase = str.toLowerCase(Locale.US);
        switch (lowerCase.hashCode()) {
            case 48:
                if (lowerCase.equals(AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 49:
                if (lowerCase.equals("1")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 3569038:
                if (lowerCase.equals("true")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 97196323:
                if (lowerCase.equals("false")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
            case 1:
                return Boolean.TRUE;
            case 2:
            case 3:
                return Boolean.FALSE;
            default:
                return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Integer tryParseInteger(String str) {
        try {
            return Integer.valueOf(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Integer tryParseUInt(String str) {
        try {
            return Integer.valueOf(Integer.parseUnsignedInt(str));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Long tryParseLong(String str) {
        try {
            return Long.valueOf(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Long tryParseULong(String str) {
        try {
            return Long.valueOf(Long.parseUnsignedLong(str));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Double tryParseDouble(String str) {
        try {
            return Double.valueOf(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String tryParseString(String str) {
        if ("".equals(str)) {
            return null;
        }
        return str;
    }

    private static <T extends Enum<T>> T tryParseEnum(Class<T> enumType, String str) {
        try {
            return (T) Enum.valueOf(enumType, str.toUpperCase(Locale.US));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static <T> List<T> tryParseList(Function<String, T> elementParser, String str) {
        if ("".equals(str)) {
            return new ArrayList();
        }
        List<T> ret = new ArrayList<>();
        int p = 0;
        while (true) {
            StringBuilder sb = new StringBuilder();
            while (p < str.length() && str.charAt(p) != ',') {
                if (str.charAt(p) == '\\') {
                    p++;
                }
                if (p == str.length()) {
                    break;
                }
                sb.append(str.charAt(p));
                p++;
            }
            ret.add(elementParser.apply(sb.toString()));
            if (p == str.length()) {
                return ret;
            }
            p++;
        }
    }

    private static <T extends Enum<T>> List<T> tryParseEnumList(Class<T> enumType, String str) {
        String[] split;
        if ("".equals(str)) {
            return new ArrayList();
        }
        ArrayList arrayList = new ArrayList();
        for (String element : str.split(",")) {
            arrayList.add(tryParseEnum(enumType, element));
        }
        return arrayList;
    }

    private static String escape(String str) {
        return str.replaceAll("([\\\\,])", "\\\\$1");
    }

    private static <T> String formatList(List<T> list) {
        StringJoiner joiner = new StringJoiner(",");
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            T element = it.next();
            joiner.add(element == null ? "" : escape(element.toString()));
        }
        return joiner.toString();
    }

    private static String formatUIntList(List<Integer> list) {
        StringJoiner joiner = new StringJoiner(",");
        Iterator<Integer> it = list.iterator();
        while (it.hasNext()) {
            Integer element = it.next();
            joiner.add(element == null ? "" : escape(Integer.toUnsignedString(element.intValue())));
        }
        return joiner.toString();
    }

    private static String formatULongList(List<Long> list) {
        StringJoiner joiner = new StringJoiner(",");
        Iterator<Long> it = list.iterator();
        while (it.hasNext()) {
            Long element = it.next();
            joiner.add(element == null ? "" : escape(Long.toUnsignedString(element.longValue())));
        }
        return joiner.toString();
    }

    private static <T extends Enum<T>> String formatEnumList(List<T> list, Function<T, String> elementFormatter) {
        StringJoiner joiner = new StringJoiner(",");
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            T element = it.next();
            joiner.add(element == null ? "" : elementFormatter.apply(element));
        }
        return joiner.toString();
    }

    public static Optional<Boolean> airplane_mode_on() {
        String value = SystemProperties.get("persist.radio.airplane_mode_on");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static void airplane_mode_on(Boolean value) {
        SystemProperties.set("persist.radio.airplane_mode_on", value == null ? "" : value.booleanValue() ? "1" : AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS);
    }

    public static List<String> baseband_version() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_BASEBAND_VERSION);
        return tryParseList(new Function() { // from class: android.sysprop.TelephonyProperties$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String tryParseString;
                tryParseString = TelephonyProperties.tryParseString((String) obj);
                return tryParseString;
            }
        }, value);
    }

    public static void baseband_version(List<String> value) {
        SystemProperties.set(com.android.internal.telephony.TelephonyProperties.PROPERTY_BASEBAND_VERSION, value == null ? "" : formatList(value));
    }

    public static Optional<String> ril_impl() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_RIL_IMPL);
        return Optional.ofNullable(tryParseString(value));
    }

    public static List<String> operator_alpha() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_OPERATOR_ALPHA);
        return tryParseList(new Function() { // from class: android.sysprop.TelephonyProperties$$ExternalSyntheticLambda10
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String tryParseString;
                tryParseString = TelephonyProperties.tryParseString((String) obj);
                return tryParseString;
            }
        }, value);
    }

    public static void operator_alpha(List<String> value) {
        SystemProperties.set(com.android.internal.telephony.TelephonyProperties.PROPERTY_OPERATOR_ALPHA, value == null ? "" : formatList(value));
    }

    public static List<String> operator_numeric() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_OPERATOR_NUMERIC);
        return tryParseList(new Function() { // from class: android.sysprop.TelephonyProperties$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String tryParseString;
                tryParseString = TelephonyProperties.tryParseString((String) obj);
                return tryParseString;
            }
        }, value);
    }

    public static void operator_numeric(List<String> value) {
        SystemProperties.set(com.android.internal.telephony.TelephonyProperties.PROPERTY_OPERATOR_NUMERIC, value == null ? "" : formatList(value));
    }

    public static Optional<Boolean> operator_is_manual() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_OPERATOR_ISMANUAL);
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static List<Boolean> operator_is_roaming() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_OPERATOR_ISROAMING);
        return tryParseList(new Function() { // from class: android.sysprop.TelephonyProperties$$ExternalSyntheticLambda9
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Boolean tryParseBoolean;
                tryParseBoolean = TelephonyProperties.tryParseBoolean((String) obj);
                return tryParseBoolean;
            }
        }, value);
    }

    public static void operator_is_roaming(List<Boolean> value) {
        SystemProperties.set(com.android.internal.telephony.TelephonyProperties.PROPERTY_OPERATOR_ISROAMING, value == null ? "" : formatList(value));
    }

    public static List<String> operator_iso_country() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_OPERATOR_ISO_COUNTRY);
        return tryParseList(new Function() { // from class: android.sysprop.TelephonyProperties$$ExternalSyntheticLambda14
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String tryParseString;
                tryParseString = TelephonyProperties.tryParseString((String) obj);
                return tryParseString;
            }
        }, value);
    }

    public static void operator_iso_country(List<String> value) {
        SystemProperties.set(com.android.internal.telephony.TelephonyProperties.PROPERTY_OPERATOR_ISO_COUNTRY, value == null ? "" : formatList(value));
    }

    public static Optional<String> lte_on_cdma_product_type() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_LTE_ON_CDMA_PRODUCT_TYPE);
        return Optional.ofNullable(tryParseString(value));
    }

    public static Optional<Integer> lte_on_cdma_device() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_LTE_ON_CDMA_DEVICE);
        return Optional.ofNullable(tryParseInteger(value));
    }

    public static List<Integer> current_active_phone() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.CURRENT_ACTIVE_PHONE);
        return tryParseList(new Function() { // from class: android.sysprop.TelephonyProperties$$ExternalSyntheticLambda11
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer tryParseInteger;
                tryParseInteger = TelephonyProperties.tryParseInteger((String) obj);
                return tryParseInteger;
            }
        }, value);
    }

    public static void current_active_phone(List<Integer> value) {
        SystemProperties.set(com.android.internal.telephony.TelephonyProperties.CURRENT_ACTIVE_PHONE, value == null ? "" : formatList(value));
    }

    public static List<String> sim_state() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_SIM_STATE);
        return tryParseList(new Function() { // from class: android.sysprop.TelephonyProperties$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String tryParseString;
                tryParseString = TelephonyProperties.tryParseString((String) obj);
                return tryParseString;
            }
        }, value);
    }

    public static void sim_state(List<String> value) {
        SystemProperties.set(com.android.internal.telephony.TelephonyProperties.PROPERTY_SIM_STATE, value == null ? "" : formatList(value));
    }

    public static List<String> icc_operator_numeric() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_ICC_OPERATOR_NUMERIC);
        return tryParseList(new Function() { // from class: android.sysprop.TelephonyProperties$$ExternalSyntheticLambda5
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String tryParseString;
                tryParseString = TelephonyProperties.tryParseString((String) obj);
                return tryParseString;
            }
        }, value);
    }

    public static void icc_operator_numeric(List<String> value) {
        SystemProperties.set(com.android.internal.telephony.TelephonyProperties.PROPERTY_ICC_OPERATOR_NUMERIC, value == null ? "" : formatList(value));
    }

    public static List<String> icc_operator_alpha() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_ICC_OPERATOR_ALPHA);
        return tryParseList(new Function() { // from class: android.sysprop.TelephonyProperties$$ExternalSyntheticLambda7
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String tryParseString;
                tryParseString = TelephonyProperties.tryParseString((String) obj);
                return tryParseString;
            }
        }, value);
    }

    public static void icc_operator_alpha(List<String> value) {
        SystemProperties.set(com.android.internal.telephony.TelephonyProperties.PROPERTY_ICC_OPERATOR_ALPHA, value == null ? "" : formatList(value));
    }

    public static List<String> icc_operator_iso_country() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_ICC_OPERATOR_ISO_COUNTRY);
        return tryParseList(new Function() { // from class: android.sysprop.TelephonyProperties$$ExternalSyntheticLambda8
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String tryParseString;
                tryParseString = TelephonyProperties.tryParseString((String) obj);
                return tryParseString;
            }
        }, value);
    }

    public static void icc_operator_iso_country(List<String> value) {
        SystemProperties.set(com.android.internal.telephony.TelephonyProperties.PROPERTY_ICC_OPERATOR_ISO_COUNTRY, value == null ? "" : formatList(value));
    }

    public static List<String> data_network_type() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_DATA_NETWORK_TYPE);
        return tryParseList(new Function() { // from class: android.sysprop.TelephonyProperties$$ExternalSyntheticLambda13
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String tryParseString;
                tryParseString = TelephonyProperties.tryParseString((String) obj);
                return tryParseString;
            }
        }, value);
    }

    public static void data_network_type(List<String> value) {
        SystemProperties.set(com.android.internal.telephony.TelephonyProperties.PROPERTY_DATA_NETWORK_TYPE, value == null ? "" : formatList(value));
    }

    public static Optional<Boolean> in_ecm_mode() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_INECM_MODE);
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static void in_ecm_mode(Boolean value) {
        SystemProperties.set(com.android.internal.telephony.TelephonyProperties.PROPERTY_INECM_MODE, value == null ? "" : value.toString());
    }

    public static Optional<Long> ecm_exit_timer() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_ECM_EXIT_TIMER);
        return Optional.ofNullable(tryParseLong(value));
    }

    public static Optional<String> operator_idp_string() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_OPERATOR_IDP_STRING);
        return Optional.ofNullable(tryParseString(value));
    }

    public static void operator_idp_string(String value) {
        SystemProperties.set(com.android.internal.telephony.TelephonyProperties.PROPERTY_OPERATOR_IDP_STRING, value == null ? "" : value.toString());
    }

    public static List<String> otasp_num_schema() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_OTASP_NUM_SCHEMA);
        return tryParseList(new Function() { // from class: android.sysprop.TelephonyProperties$$ExternalSyntheticLambda12
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String tryParseString;
                tryParseString = TelephonyProperties.tryParseString((String) obj);
                return tryParseString;
            }
        }, value);
    }

    public static Optional<Boolean> disable_call() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_DISABLE_CALL);
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> ril_sends_multiple_call_ring() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_RIL_SENDS_MULTIPLE_CALL_RING);
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Integer> call_ring_delay() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_CALL_RING_DELAY);
        return Optional.ofNullable(tryParseInteger(value));
    }

    public static Optional<Integer> cdma_msg_id() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_CDMA_MSG_ID);
        return Optional.ofNullable(tryParseInteger(value));
    }

    public static void cdma_msg_id(Integer value) {
        SystemProperties.set(com.android.internal.telephony.TelephonyProperties.PROPERTY_CDMA_MSG_ID, value == null ? "" : value.toString());
    }

    public static Optional<Integer> wake_lock_timeout() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_WAKE_LOCK_TIMEOUT);
        return Optional.ofNullable(tryParseInteger(value));
    }

    public static Optional<Boolean> reset_on_radio_tech_change() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_RESET_ON_RADIO_TECH_CHANGE);
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static List<Boolean> sms_receive() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_SMS_RECEIVE);
        return tryParseList(new Function() { // from class: android.sysprop.TelephonyProperties$$ExternalSyntheticLambda6
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Boolean tryParseBoolean;
                tryParseBoolean = TelephonyProperties.tryParseBoolean((String) obj);
                return tryParseBoolean;
            }
        }, value);
    }

    public static List<Boolean> sms_send() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_SMS_SEND);
        return tryParseList(new Function() { // from class: android.sysprop.TelephonyProperties$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Boolean tryParseBoolean;
                tryParseBoolean = TelephonyProperties.tryParseBoolean((String) obj);
                return tryParseBoolean;
            }
        }, value);
    }

    public static Optional<Boolean> test_csim() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_TEST_CSIM);
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> ignore_nitz() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_IGNORE_NITZ);
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<String> multi_sim_config() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_MULTI_SIM_CONFIG);
        return Optional.ofNullable(tryParseString(value));
    }

    public static void multi_sim_config(String value) {
        SystemProperties.set(com.android.internal.telephony.TelephonyProperties.PROPERTY_MULTI_SIM_CONFIG, value == null ? "" : value.toString());
    }

    public static Optional<Boolean> reboot_on_modem_change() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_REBOOT_REQUIRED_ON_MODEM_CHANGE);
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Integer> videocall_audio_output() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_VIDEOCALL_AUDIO_OUTPUT);
        return Optional.ofNullable(tryParseInteger(value));
    }

    public static Optional<Boolean> enable_esim_ui_by_default() {
        String value = SystemProperties.get("esim.enable_esim_system_ui_by_default");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static List<Integer> default_network() {
        String value = SystemProperties.get("ro.telephony.default_network");
        return tryParseList(new Function() { // from class: android.sysprop.TelephonyProperties$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer tryParseInteger;
                tryParseInteger = TelephonyProperties.tryParseInteger((String) obj);
                return tryParseInteger;
            }
        }, value);
    }

    public static Optional<Boolean> data_roaming() {
        String value = SystemProperties.get("ro.com.android.dataroaming");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> mobile_data() {
        String value = SystemProperties.get("ro.com.android.mobiledata");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Integer> wps_info() {
        String value = SystemProperties.get("wifidirect.wps");
        return Optional.ofNullable(tryParseInteger(value));
    }

    public static Optional<Integer> max_active_modems() {
        String value = SystemProperties.get(com.android.internal.telephony.TelephonyProperties.PROPERTY_MAX_ACTIVE_MODEMS);
        return Optional.ofNullable(tryParseInteger(value));
    }

    public static Optional<Integer> sim_slots_count() {
        String value = SystemProperties.get("ro.telephony.sim_slots.count");
        return Optional.ofNullable(tryParseInteger(value));
    }
}
