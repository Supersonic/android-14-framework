package com.android.internal.telephony.util;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
/* loaded from: classes.dex */
public class ConnectivitySettingsUtils {
    public static final String PRIVATE_DNS_DEFAULT_MODE = "private_dns_default_mode";
    public static final String PRIVATE_DNS_MODE = "private_dns_mode";
    public static final int PRIVATE_DNS_MODE_OFF = 1;
    public static final String PRIVATE_DNS_MODE_OFF_STRING = "off";
    public static final int PRIVATE_DNS_MODE_OPPORTUNISTIC = 2;
    public static final String PRIVATE_DNS_MODE_OPPORTUNISTIC_STRING = "opportunistic";
    public static final int PRIVATE_DNS_MODE_PROVIDER_HOSTNAME = 3;
    public static final String PRIVATE_DNS_MODE_PROVIDER_HOSTNAME_STRING = "hostname";
    public static final String PRIVATE_DNS_SPECIFIER = "private_dns_specifier";

    public static String getPrivateDnsModeAsString(int i) {
        if (i != 1) {
            if (i != 2) {
                if (i == 3) {
                    return PRIVATE_DNS_MODE_PROVIDER_HOSTNAME_STRING;
                }
                throw new IllegalArgumentException("Invalid private dns mode: " + i);
            }
            return PRIVATE_DNS_MODE_OPPORTUNISTIC_STRING;
        }
        return PRIVATE_DNS_MODE_OFF_STRING;
    }

    private static int getPrivateDnsModeAsInt(String str) {
        if (TextUtils.isEmpty(str)) {
            return 2;
        }
        str.hashCode();
        if (str.equals(PRIVATE_DNS_MODE_PROVIDER_HOSTNAME_STRING)) {
            return 3;
        }
        return !str.equals(PRIVATE_DNS_MODE_OFF_STRING) ? 2 : 1;
    }

    public static int getPrivateDnsMode(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        String string = Settings.Global.getString(contentResolver, PRIVATE_DNS_MODE);
        if (TextUtils.isEmpty(string)) {
            string = Settings.Global.getString(contentResolver, PRIVATE_DNS_DEFAULT_MODE);
        }
        return getPrivateDnsModeAsInt(string);
    }

    public static void setPrivateDnsMode(Context context, int i) {
        if (i != 1 && i != 2 && i != 3) {
            throw new IllegalArgumentException("Invalid private dns mode: " + i);
        }
        Settings.Global.putString(context.getContentResolver(), PRIVATE_DNS_MODE, getPrivateDnsModeAsString(i));
    }

    public static String getPrivateDnsHostname(Context context) {
        return Settings.Global.getString(context.getContentResolver(), PRIVATE_DNS_SPECIFIER);
    }

    public static void setPrivateDnsHostname(Context context, String str) {
        Settings.Global.putString(context.getContentResolver(), PRIVATE_DNS_SPECIFIER, str);
    }
}
