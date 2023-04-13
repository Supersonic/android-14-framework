package com.android.server.wifi;

import android.annotation.SystemApi;
import android.os.SystemService;
import android.util.Log;
import java.util.NoSuchElementException;
@SystemApi(client = SystemApi.Client.SYSTEM_SERVER)
/* loaded from: classes2.dex */
public class SupplicantManager {
    public static void start() {
        try {
            SystemService.start("wpa_supplicant");
        } catch (RuntimeException unused) {
            throw new NoSuchElementException("Failed to start Supplicant");
        }
    }

    public static void stop() {
        try {
            SystemService.stop("wpa_supplicant");
        } catch (RuntimeException e) {
            Log.w("SupplicantManager", "Failed to stop Supplicant", e);
        }
    }
}
