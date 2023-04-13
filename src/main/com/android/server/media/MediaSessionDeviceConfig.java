package com.android.server.media;

import android.content.Context;
import android.provider.DeviceConfig;
import android.text.TextUtils;
import java.io.PrintWriter;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public class MediaSessionDeviceConfig {
    public static volatile long sMediaButtonReceiverFgsAllowlistDurationMs = 10000;
    public static volatile long sMediaSessionCallbackFgsAllowlistDurationMs = 10000;
    public static volatile long sMediaSessionCallbackFgsWhileInUseTempAllowDurationMs = 10000;

    public static void refresh(final DeviceConfig.Properties properties) {
        properties.getKeyset();
        properties.getKeyset().forEach(new Consumer() { // from class: com.android.server.media.MediaSessionDeviceConfig$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                MediaSessionDeviceConfig.lambda$refresh$0(properties, (String) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$refresh$0(DeviceConfig.Properties properties, String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1976080914:
                if (str.equals("media_button_receiver_fgs_allowlist_duration_ms")) {
                    c = 0;
                    break;
                }
                break;
            case -1060130895:
                if (str.equals("media_session_callback_fgs_while_in_use_temp_allow_duration_ms")) {
                    c = 1;
                    break;
                }
                break;
            case 1803361950:
                if (str.equals("media_session_calback_fgs_allowlist_duration_ms")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                sMediaButtonReceiverFgsAllowlistDurationMs = properties.getLong(str, 10000L);
                return;
            case 1:
                sMediaSessionCallbackFgsWhileInUseTempAllowDurationMs = properties.getLong(str, 10000L);
                return;
            case 2:
                sMediaSessionCallbackFgsAllowlistDurationMs = properties.getLong(str, 10000L);
                return;
            default:
                return;
        }
    }

    public static void initialize(Context context) {
        DeviceConfig.addOnPropertiesChangedListener("media", context.getMainExecutor(), new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.media.MediaSessionDeviceConfig$$ExternalSyntheticLambda0
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                MediaSessionDeviceConfig.refresh(properties);
            }
        });
        refresh(DeviceConfig.getProperties("media", new String[0]));
    }

    public static long getMediaButtonReceiverFgsAllowlistDurationMs() {
        return sMediaButtonReceiverFgsAllowlistDurationMs;
    }

    public static long getMediaSessionCallbackFgsAllowlistDurationMs() {
        return sMediaSessionCallbackFgsAllowlistDurationMs;
    }

    public static long getMediaSessionCallbackFgsWhileInUseTempAllowDurationMs() {
        return sMediaSessionCallbackFgsWhileInUseTempAllowDurationMs;
    }

    public static void dump(PrintWriter printWriter, String str) {
        printWriter.println("Media session config:");
        String str2 = str + "  %s: [cur: %s, def: %s]";
        printWriter.println(TextUtils.formatSimple(str2, new Object[]{"media_button_receiver_fgs_allowlist_duration_ms", Long.valueOf(sMediaButtonReceiverFgsAllowlistDurationMs), 10000L}));
        printWriter.println(TextUtils.formatSimple(str2, new Object[]{"media_session_calback_fgs_allowlist_duration_ms", Long.valueOf(sMediaSessionCallbackFgsAllowlistDurationMs), 10000L}));
        printWriter.println(TextUtils.formatSimple(str2, new Object[]{"media_session_callback_fgs_while_in_use_temp_allow_duration_ms", Long.valueOf(sMediaSessionCallbackFgsWhileInUseTempAllowDurationMs), 10000L}));
    }
}
