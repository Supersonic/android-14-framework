package com.android.net.module.util;

import android.text.TextUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes5.dex */
public final class ProxyUtils {
    private static final String EXCL_REGEX = "[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*(\\.[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*)*";
    private static final String NAME_IP_REGEX = "[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*(\\.[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*)*";
    public static final int PROXY_EXCLLIST_INVALID = 5;
    public static final int PROXY_HOSTNAME_EMPTY = 1;
    public static final int PROXY_HOSTNAME_INVALID = 2;
    public static final int PROXY_PORT_EMPTY = 3;
    public static final int PROXY_PORT_INVALID = 4;
    public static final int PROXY_VALID = 0;
    private static final String HOSTNAME_REGEXP = "^$|^[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*(\\.[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*)*$";
    private static final Pattern HOSTNAME_PATTERN = Pattern.compile(HOSTNAME_REGEXP);
    private static final String EXCLLIST_REGEXP = "^$|^[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*(\\.[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*)*(,[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*(\\.[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*)*)*$";
    private static final Pattern EXCLLIST_PATTERN = Pattern.compile(EXCLLIST_REGEXP);

    public static List<String> exclusionStringAsList(String exclusionList) {
        if (exclusionList == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(exclusionList.toLowerCase(Locale.ROOT).split(","));
    }

    public static String exclusionListAsString(String[] exclusionList) {
        if (exclusionList == null) {
            return "";
        }
        return TextUtils.join(",", exclusionList);
    }

    public static int validate(String hostname, String port, String exclList) {
        Matcher match = HOSTNAME_PATTERN.matcher(hostname);
        Matcher listMatch = EXCLLIST_PATTERN.matcher(exclList);
        if (match.matches()) {
            if (listMatch.matches()) {
                if (hostname.length() <= 0 || port.length() != 0) {
                    if (port.length() > 0) {
                        if (hostname.length() == 0) {
                            return 1;
                        }
                        try {
                            int portVal = Integer.parseInt(port);
                            if (portVal <= 0 || portVal > 65535) {
                                return 4;
                            }
                            return 0;
                        } catch (NumberFormatException e) {
                            return 4;
                        }
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
