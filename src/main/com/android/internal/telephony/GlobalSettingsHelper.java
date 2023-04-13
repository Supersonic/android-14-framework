package com.android.internal.telephony;

import android.content.Context;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
/* loaded from: classes.dex */
public class GlobalSettingsHelper {
    public static int getInt(Context context, String str, int i, int i2) {
        return Settings.Global.getInt(context.getContentResolver(), getSettingName(context, str, i), i2);
    }

    public static boolean getBoolean(Context context, String str, int i, boolean z) {
        return Settings.Global.getInt(context.getContentResolver(), getSettingName(context, str, i), z ? 1 : 0) == 1;
    }

    public static boolean getBoolean(Context context, String str, int i) throws Settings.SettingNotFoundException {
        return Settings.Global.getInt(context.getContentResolver(), getSettingName(context, str, i)) == 1;
    }

    public static boolean setInt(Context context, String str, int i, int i2) {
        String settingName = getSettingName(context, str, i);
        boolean z = true;
        try {
            if (Settings.Global.getInt(context.getContentResolver(), settingName) == i2) {
                z = false;
            }
        } catch (Settings.SettingNotFoundException unused) {
        }
        if (z) {
            Settings.Global.putInt(context.getContentResolver(), settingName, i2);
        }
        return z;
    }

    public static boolean setBoolean(Context context, String str, int i, boolean z) {
        return setInt(context, str, i, z ? 1 : 0);
    }

    private static String getSettingName(Context context, String str, int i) {
        if (TelephonyManager.from(context).getSimCount() <= 1 || !SubscriptionManager.isValidSubscriptionId(i)) {
            return str;
        }
        return str + i;
    }
}
