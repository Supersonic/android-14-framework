package com.android.net.module.util;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
/* loaded from: classes5.dex */
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

    public static String getPrivateDnsModeAsString(int mode) {
        switch (mode) {
            case 1:
                return "off";
            case 2:
                return PRIVATE_DNS_MODE_OPPORTUNISTIC_STRING;
            case 3:
                return PRIVATE_DNS_MODE_PROVIDER_HOSTNAME_STRING;
            default:
                throw new IllegalArgumentException("Invalid private dns mode: " + mode);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static int getPrivateDnsModeAsInt(String mode) {
        char c;
        if (TextUtils.isEmpty(mode)) {
            return 2;
        }
        switch (mode.hashCode()) {
            case -539229175:
                if (mode.equals(PRIVATE_DNS_MODE_OPPORTUNISTIC_STRING)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -299803597:
                if (mode.equals(PRIVATE_DNS_MODE_PROVIDER_HOSTNAME_STRING)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 109935:
                if (mode.equals("off")) {
                    c = 0;
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
                return 1;
            case 1:
                return 3;
            case 2:
                return 2;
            default:
                return 2;
        }
    }

    public static int getPrivateDnsMode(Context context) {
        ContentResolver cr = context.getContentResolver();
        String mode = Settings.Global.getString(cr, "private_dns_mode");
        if (TextUtils.isEmpty(mode)) {
            mode = Settings.Global.getString(cr, "private_dns_default_mode");
        }
        return getPrivateDnsModeAsInt(mode);
    }

    public static void setPrivateDnsMode(Context context, int mode) {
        if (mode != 1 && mode != 2 && mode != 3) {
            throw new IllegalArgumentException("Invalid private dns mode: " + mode);
        }
        Settings.Global.putString(context.getContentResolver(), "private_dns_mode", getPrivateDnsModeAsString(mode));
    }

    public static String getPrivateDnsHostname(Context context) {
        return Settings.Global.getString(context.getContentResolver(), "private_dns_specifier");
    }

    public static void setPrivateDnsHostname(Context context, String specifier) {
        Settings.Global.putString(context.getContentResolver(), "private_dns_specifier", specifier);
    }
}
