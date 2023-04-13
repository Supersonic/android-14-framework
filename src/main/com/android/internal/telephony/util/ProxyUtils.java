package com.android.internal.telephony.util;

import android.text.TextUtils;
import com.android.internal.telephony.PhoneConfigurationManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public final class ProxyUtils {
    public static final int PROXY_EXCLLIST_INVALID = 5;
    public static final int PROXY_HOSTNAME_EMPTY = 1;
    public static final int PROXY_HOSTNAME_INVALID = 2;
    public static final int PROXY_PORT_EMPTY = 3;
    public static final int PROXY_PORT_INVALID = 4;
    public static final int PROXY_VALID = 0;
    private static final Pattern HOSTNAME_PATTERN = Pattern.compile("^$|^[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*(\\.[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*)*$");
    private static final Pattern EXCLLIST_PATTERN = Pattern.compile("^$|^[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*(\\.[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*)*(,[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*(\\.[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*)*)*$");

    public static List<String> exclusionStringAsList(String str) {
        if (str == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(str.toLowerCase(Locale.ROOT).split(","));
    }

    public static String exclusionListAsString(String[] strArr) {
        return strArr == null ? PhoneConfigurationManager.SSSS : TextUtils.join(",", strArr);
    }

    public static int validate(String str, String str2, String str3) {
        int parseInt;
        Matcher matcher = HOSTNAME_PATTERN.matcher(str);
        Matcher matcher2 = EXCLLIST_PATTERN.matcher(str3);
        if (matcher.matches()) {
            if (matcher2.matches()) {
                if (str.length() <= 0 || str2.length() != 0) {
                    if (str2.length() > 0) {
                        if (str.length() == 0) {
                            return 1;
                        }
                        try {
                            parseInt = Integer.parseInt(str2);
                        } catch (NumberFormatException unused) {
                        }
                        return (parseInt <= 0 || parseInt > 65535) ? 4 : 0;
                    }
                    return 0;
                }
                return 3;
            }
            return 5;
        }
        return 2;
    }
}
