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
public final class OtaProperties {
    private OtaProperties() {
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

    public static Optional<Boolean> warm_reset() {
        String value = SystemProperties.get("ota.warm_reset");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static void warm_reset(Boolean value) {
        SystemProperties.set("ota.warm_reset", value == null ? "" : value.booleanValue() ? "1" : AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS);
    }

    public static Optional<String> ab_ota_partitions() {
        String value = SystemProperties.get("ro.product.ab_ota_partitions");
        return Optional.ofNullable(tryParseString(value));
    }

    public static Optional<Boolean> virtual_ab_enabled() {
        String value = SystemProperties.get("ro.virtual_ab.enabled");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<Boolean> virtual_ab_retrofit() {
        String value = SystemProperties.get("ro.virtual_ab.retrofit");
        return Optional.ofNullable(tryParseBoolean(value));
    }

    public static Optional<String> other_vbmeta_digest() {
        String value = SystemProperties.get("ota.other.vbmeta_digest");
        return Optional.ofNullable(tryParseString(value));
    }

    public static void other_vbmeta_digest(String value) {
        SystemProperties.set("ota.other.vbmeta_digest", value == null ? "" : value.toString());
    }
}
