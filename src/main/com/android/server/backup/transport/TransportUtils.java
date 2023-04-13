package com.android.server.backup.transport;

import android.util.Log;
import android.util.Slog;
/* loaded from: classes.dex */
public class TransportUtils {
    public static void log(int i, String str, String str2) {
        if (i == -1) {
            Slog.wtf(str, str2);
        } else if (Log.isLoggable(str, i)) {
            Slog.println(i, str, str2);
        }
    }

    public static String formatMessage(String str, String str2, String str3) {
        StringBuilder sb = new StringBuilder();
        if (str != null) {
            sb.append(str);
            sb.append(" ");
        }
        if (str2 != null) {
            sb.append("[");
            sb.append(str2);
            sb.append("] ");
        }
        sb.append(str3);
        return sb.toString();
    }
}
