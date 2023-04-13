package android.sysprop;

import android.os.SystemProperties;
import java.util.Locale;
import java.util.Optional;
/* loaded from: classes.dex */
public final class WatchdogProperties {
    public static Boolean tryParseBoolean(String str) {
        if (str == null) {
            return null;
        }
        String lowerCase = str.toLowerCase(Locale.US);
        lowerCase.hashCode();
        char c = 65535;
        switch (lowerCase.hashCode()) {
            case 48:
                if (lowerCase.equals("0")) {
                    c = 0;
                    break;
                }
                break;
            case 49:
                if (lowerCase.equals("1")) {
                    c = 1;
                    break;
                }
                break;
            case 3569038:
                if (lowerCase.equals("true")) {
                    c = 2;
                    break;
                }
                break;
            case 97196323:
                if (lowerCase.equals("false")) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 3:
                return Boolean.FALSE;
            case 1:
            case 2:
                return Boolean.TRUE;
            default:
                return null;
        }
    }

    public static Integer tryParseInteger(String str) {
        try {
            return Integer.valueOf(str);
        } catch (NumberFormatException unused) {
            return null;
        }
    }

    public static Optional<Integer> fatal_count() {
        return Optional.ofNullable(tryParseInteger(SystemProperties.get("framework_watchdog.fatal_count")));
    }

    public static Optional<Integer> fatal_window_seconds() {
        return Optional.ofNullable(tryParseInteger(SystemProperties.get("framework_watchdog.fatal_window.second")));
    }

    public static Optional<Boolean> should_ignore_fatal_count() {
        return Optional.ofNullable(tryParseBoolean(SystemProperties.get("persist.debug.framework_watchdog.fatal_ignore")));
    }
}
