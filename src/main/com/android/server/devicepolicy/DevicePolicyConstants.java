package com.android.server.devicepolicy;

import android.util.IndentingPrintWriter;
import android.util.KeyValueListParser;
import com.android.server.utils.Slogf;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class DevicePolicyConstants {
    public final int BATTERY_THRESHOLD_CHARGING;
    public final int BATTERY_THRESHOLD_NOT_CHARGING;
    public final double DAS_DIED_SERVICE_RECONNECT_BACKOFF_INCREASE;
    public final long DAS_DIED_SERVICE_RECONNECT_BACKOFF_SEC;
    public final long DAS_DIED_SERVICE_RECONNECT_MAX_BACKOFF_SEC;
    public final long DAS_DIED_SERVICE_STABLE_CONNECTION_THRESHOLD_SEC;
    public final boolean USE_TEST_ADMIN_AS_SUPERVISION_COMPONENT;

    public DevicePolicyConstants(String str) {
        KeyValueListParser keyValueListParser = new KeyValueListParser(',');
        try {
            keyValueListParser.setString(str);
        } catch (IllegalArgumentException unused) {
            Slogf.m24e("DevicePolicyManager", "Bad device policy settings: %s", str);
        }
        long j = keyValueListParser.getLong("das_died_service_reconnect_backoff_sec", TimeUnit.HOURS.toSeconds(1L));
        double d = keyValueListParser.getFloat("das_died_service_reconnect_backoff_increase", 2.0f);
        long j2 = keyValueListParser.getLong("das_died_service_reconnect_max_backoff_sec", TimeUnit.DAYS.toSeconds(1L));
        long j3 = keyValueListParser.getLong("das_died_service_stable_connection_threshold_sec", TimeUnit.MINUTES.toSeconds(2L));
        int i = keyValueListParser.getInt("battery_threshold_not_charging", 40);
        int i2 = keyValueListParser.getInt("battery_threshold_charging", 20);
        boolean z = keyValueListParser.getBoolean("use_test_admin_as_supervision_component", false);
        long max = Math.max(5L, j);
        double max2 = Math.max(1.0d, d);
        long max3 = Math.max(max, j2);
        this.DAS_DIED_SERVICE_RECONNECT_BACKOFF_SEC = max;
        this.DAS_DIED_SERVICE_RECONNECT_BACKOFF_INCREASE = max2;
        this.DAS_DIED_SERVICE_RECONNECT_MAX_BACKOFF_SEC = max3;
        this.DAS_DIED_SERVICE_STABLE_CONNECTION_THRESHOLD_SEC = j3;
        this.BATTERY_THRESHOLD_NOT_CHARGING = i;
        this.BATTERY_THRESHOLD_CHARGING = i2;
        this.USE_TEST_ADMIN_AS_SUPERVISION_COMPONENT = z;
    }

    public static DevicePolicyConstants loadFromString(String str) {
        return new DevicePolicyConstants(str);
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.println("Constants:");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.print("DAS_DIED_SERVICE_RECONNECT_BACKOFF_SEC: ");
        indentingPrintWriter.println(this.DAS_DIED_SERVICE_RECONNECT_BACKOFF_SEC);
        indentingPrintWriter.print("DAS_DIED_SERVICE_RECONNECT_BACKOFF_INCREASE: ");
        indentingPrintWriter.println(this.DAS_DIED_SERVICE_RECONNECT_BACKOFF_INCREASE);
        indentingPrintWriter.print("DAS_DIED_SERVICE_RECONNECT_MAX_BACKOFF_SEC: ");
        indentingPrintWriter.println(this.DAS_DIED_SERVICE_RECONNECT_MAX_BACKOFF_SEC);
        indentingPrintWriter.print("DAS_DIED_SERVICE_STABLE_CONNECTION_THRESHOLD_SEC: ");
        indentingPrintWriter.println(this.DAS_DIED_SERVICE_STABLE_CONNECTION_THRESHOLD_SEC);
        indentingPrintWriter.decreaseIndent();
    }
}
