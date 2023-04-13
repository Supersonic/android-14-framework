package com.android.server.appbinding;

import android.util.KeyValueListParser;
import android.util.Slog;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class AppBindingConstants {
    public final double SERVICE_RECONNECT_BACKOFF_INCREASE;
    public final long SERVICE_RECONNECT_BACKOFF_SEC;
    public final long SERVICE_RECONNECT_MAX_BACKOFF_SEC;
    public final long SERVICE_STABLE_CONNECTION_THRESHOLD_SEC;
    public final int SMS_APP_BIND_FLAGS;
    public final boolean SMS_SERVICE_ENABLED;
    public final String sourceSettings;

    public AppBindingConstants(String str) {
        this.sourceSettings = str;
        KeyValueListParser keyValueListParser = new KeyValueListParser(',');
        try {
            keyValueListParser.setString(str);
        } catch (IllegalArgumentException unused) {
            Slog.e("AppBindingService", "Bad setting: " + str);
        }
        long j = keyValueListParser.getLong("service_reconnect_backoff_sec", 10L);
        double d = keyValueListParser.getFloat("service_reconnect_backoff_increase", 2.0f);
        long j2 = keyValueListParser.getLong("service_reconnect_max_backoff_sec", TimeUnit.HOURS.toSeconds(1L));
        boolean z = keyValueListParser.getBoolean("sms_service_enabled", true);
        int i = keyValueListParser.getInt("sms_app_bind_flags", 1140850688);
        long j3 = keyValueListParser.getLong("service_stable_connection_threshold_sec", TimeUnit.MINUTES.toSeconds(2L));
        long max = Math.max(5L, j);
        double max2 = Math.max(1.0d, d);
        long max3 = Math.max(max, j2);
        this.SERVICE_RECONNECT_BACKOFF_SEC = max;
        this.SERVICE_RECONNECT_BACKOFF_INCREASE = max2;
        this.SERVICE_RECONNECT_MAX_BACKOFF_SEC = max3;
        this.SERVICE_STABLE_CONNECTION_THRESHOLD_SEC = j3;
        this.SMS_SERVICE_ENABLED = z;
        this.SMS_APP_BIND_FLAGS = i;
    }

    public static AppBindingConstants initializeFromString(String str) {
        return new AppBindingConstants(str);
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.print(str);
        printWriter.print("Constants: ");
        printWriter.println(this.sourceSettings);
        printWriter.print(str);
        printWriter.print("  SERVICE_RECONNECT_BACKOFF_SEC: ");
        printWriter.println(this.SERVICE_RECONNECT_BACKOFF_SEC);
        printWriter.print(str);
        printWriter.print("  SERVICE_RECONNECT_BACKOFF_INCREASE: ");
        printWriter.println(this.SERVICE_RECONNECT_BACKOFF_INCREASE);
        printWriter.print(str);
        printWriter.print("  SERVICE_RECONNECT_MAX_BACKOFF_SEC: ");
        printWriter.println(this.SERVICE_RECONNECT_MAX_BACKOFF_SEC);
        printWriter.print(str);
        printWriter.print("  SERVICE_STABLE_CONNECTION_THRESHOLD_SEC: ");
        printWriter.println(this.SERVICE_STABLE_CONNECTION_THRESHOLD_SEC);
        printWriter.print(str);
        printWriter.print("  SMS_SERVICE_ENABLED: ");
        printWriter.println(this.SMS_SERVICE_ENABLED);
        printWriter.print(str);
        printWriter.print("  SMS_APP_BIND_FLAGS: 0x");
        printWriter.println(Integer.toHexString(this.SMS_APP_BIND_FLAGS));
    }
}
