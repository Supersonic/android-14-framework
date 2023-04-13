package android.sysprop;

import android.os.SystemProperties;
import java.util.Locale;
import java.util.Optional;
/* loaded from: classes.dex */
public final class SurfaceFlingerProperties {
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

    public static Optional<Boolean> has_wide_color_display() {
        return Optional.ofNullable(tryParseBoolean(SystemProperties.get("ro.surface_flinger.has_wide_color_display")));
    }

    public static Optional<Boolean> has_HDR_display() {
        return Optional.ofNullable(tryParseBoolean(SystemProperties.get("ro.surface_flinger.has_HDR_display")));
    }

    public static Optional<Boolean> enable_frame_rate_override() {
        return Optional.ofNullable(tryParseBoolean(SystemProperties.get("ro.surface_flinger.enable_frame_rate_override")));
    }
}
