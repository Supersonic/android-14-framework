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
public final class CarProperties {
    private CarProperties() {
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static Boolean tryParseBoolean(String str) {
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

    private static Integer tryParseInteger(String str) {
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

    private static String tryParseString(String str) {
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

    public static Optional<Integer> boot_user_override_id() {
        String value = SystemProperties.get("android.car.systemuser.bootuseroverrideid");
        return Optional.ofNullable(tryParseInteger(value));
    }

    public static void boot_user_override_id(Integer value) {
        SystemProperties.set("android.car.systemuser.bootuseroverrideid", value == null ? "" : value.toString());
    }

    public static Optional<String> trusted_device_device_name_prefix() {
        String value = SystemProperties.get("ro.android.car.trusteddevice.device_name_prefix");
        return Optional.ofNullable(tryParseString(value));
    }

    public static Optional<Integer> number_pre_created_users() {
        String value = SystemProperties.get("android.car.number_pre_created_users");
        return Optional.ofNullable(tryParseInteger(value));
    }

    public static void number_pre_created_users(Integer value) {
        SystemProperties.set("android.car.number_pre_created_users", value == null ? "" : value.toString());
    }

    public static Optional<Integer> number_pre_created_guests() {
        String value = SystemProperties.get("android.car.number_pre_created_guests");
        return Optional.ofNullable(tryParseInteger(value));
    }

    public static void number_pre_created_guests(Integer value) {
        SystemProperties.set("android.car.number_pre_created_guests", value == null ? "" : value.toString());
    }

    public static Optional<Boolean> user_hal_enabled() {
        String value = SystemProperties.get("android.car.user_hal_enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static void user_hal_enabled(Boolean value) {
        SystemProperties.set("android.car.user_hal_enabled", value == null ? "" : value.toString());
    }

    public static Optional<Integer> user_hal_timeout() {
        String value = SystemProperties.get("android.car.user_hal_timeout");
        return Optional.ofNullable(tryParseInteger(value));
    }

    public static void user_hal_timeout(Integer value) {
        SystemProperties.set("android.car.user_hal_timeout", value == null ? "" : value.toString());
    }

    public static Optional<Integer> device_policy_manager_timeout() {
        String value = SystemProperties.get("android.car.device_policy_manager_timeout");
        return Optional.ofNullable(tryParseInteger(value));
    }

    public static void device_policy_manager_timeout(Integer value) {
        SystemProperties.set("android.car.device_policy_manager_timeout", value == null ? "" : value.toString());
    }
}
