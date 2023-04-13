package com.android.server.notification;

import android.content.Context;
import android.os.SystemProperties;
/* loaded from: classes2.dex */
public class PropConfig {
    public static String[] getStringArray(Context context, String str, int i) {
        String str2 = SystemProperties.get(str, "UNSET");
        return !"UNSET".equals(str2) ? str2.split(",") : context.getResources().getStringArray(i);
    }
}
