package com.android.server.stats.pull;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Slog;
import android.util.StatsEvent;
import com.android.internal.annotations.VisibleForTesting;
import com.android.service.nano.StringListParamProto;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public final class SettingsStatsUtil {
    public static final FlagsData[] GLOBAL_SETTINGS = {new FlagsData("GlobalFeature__boolean_whitelist", 1), new FlagsData("GlobalFeature__integer_whitelist", 2), new FlagsData("GlobalFeature__float_whitelist", 3), new FlagsData("GlobalFeature__string_whitelist", 4)};
    public static final FlagsData[] SECURE_SETTINGS = {new FlagsData("SecureFeature__boolean_whitelist", 1), new FlagsData("SecureFeature__integer_whitelist", 2), new FlagsData("SecureFeature__float_whitelist", 3), new FlagsData("SecureFeature__string_whitelist", 4)};
    public static final FlagsData[] SYSTEM_SETTINGS = {new FlagsData("SystemFeature__boolean_whitelist", 1), new FlagsData("SystemFeature__integer_whitelist", 2), new FlagsData("SystemFeature__float_whitelist", 3), new FlagsData("SystemFeature__string_whitelist", 4)};

    @VisibleForTesting
    public static List<StatsEvent> logGlobalSettings(Context context, int i, int i2) {
        FlagsData[] flagsDataArr;
        String[] strArr;
        ArrayList arrayList = new ArrayList();
        ContentResolver contentResolver = context.getContentResolver();
        for (FlagsData flagsData : GLOBAL_SETTINGS) {
            StringListParamProto list = getList(flagsData.mFlagName);
            if (list != null) {
                for (String str : list.element) {
                    arrayList.add(createStatsEvent(i, str, Settings.Global.getStringForUser(contentResolver, str, i2), i2, flagsData.mDataType));
                }
            }
        }
        return arrayList;
    }

    public static List<StatsEvent> logSystemSettings(Context context, int i, int i2) {
        FlagsData[] flagsDataArr;
        String[] strArr;
        ArrayList arrayList = new ArrayList();
        ContentResolver contentResolver = context.getContentResolver();
        for (FlagsData flagsData : SYSTEM_SETTINGS) {
            StringListParamProto list = getList(flagsData.mFlagName);
            if (list != null) {
                for (String str : list.element) {
                    arrayList.add(createStatsEvent(i, str, Settings.System.getStringForUser(contentResolver, str, i2), i2, flagsData.mDataType));
                }
            }
        }
        return arrayList;
    }

    public static List<StatsEvent> logSecureSettings(Context context, int i, int i2) {
        FlagsData[] flagsDataArr;
        String[] strArr;
        ArrayList arrayList = new ArrayList();
        ContentResolver contentResolver = context.getContentResolver();
        for (FlagsData flagsData : SECURE_SETTINGS) {
            StringListParamProto list = getList(flagsData.mFlagName);
            if (list != null) {
                for (String str : list.element) {
                    arrayList.add(createStatsEvent(i, str, Settings.Secure.getStringForUser(contentResolver, str, i2), i2, flagsData.mDataType));
                }
            }
        }
        return arrayList;
    }

    @VisibleForTesting
    public static StringListParamProto getList(String str) {
        String property = DeviceConfig.getProperty("settings_stats", str);
        if (TextUtils.isEmpty(property)) {
            return null;
        }
        try {
            return StringListParamProto.parseFrom(Base64.decode(property, 3));
        } catch (Exception e) {
            Slog.e("SettingsStatsUtil", "Error parsing string list proto", e);
            return null;
        }
    }

    public static StatsEvent createStatsEvent(int i, String str, String str2, int i2, int i3) {
        int i4;
        StatsEvent.Builder writeString = StatsEvent.newBuilder().setAtomId(i).writeString(str);
        boolean z = false;
        float f = 0.0f;
        if (TextUtils.isEmpty(str2)) {
            writeString.writeInt(0).writeBoolean(false).writeInt(0).writeFloat(0.0f).writeString("").writeInt(i2);
        } else {
            if (i3 != 1) {
                if (i3 == 2) {
                    try {
                        i4 = Integer.parseInt(str2);
                    } catch (NumberFormatException unused) {
                        Slog.w("SettingsStatsUtil", "Can not parse value to float: " + str2);
                    }
                    str2 = "";
                } else if (i3 == 3) {
                    try {
                        f = Float.parseFloat(str2);
                    } catch (NumberFormatException unused2) {
                        Slog.w("SettingsStatsUtil", "Can not parse value to float: " + str2);
                    }
                } else if (i3 != 4) {
                    Slog.w("SettingsStatsUtil", "Unexpected value type " + i3);
                } else {
                    i4 = 0;
                }
                i4 = 0;
                str2 = "";
            } else {
                boolean equals = "1".equals(str2);
                str2 = "";
                z = equals;
                i4 = 0;
            }
            writeString.writeInt(i3).writeBoolean(z).writeInt(i4).writeFloat(f).writeString(str2).writeInt(i2);
        }
        return writeString.build();
    }

    /* loaded from: classes2.dex */
    public static final class FlagsData {
        public int mDataType;
        public String mFlagName;

        public FlagsData(String str, int i) {
            this.mFlagName = str;
            this.mDataType = i;
        }
    }
}
