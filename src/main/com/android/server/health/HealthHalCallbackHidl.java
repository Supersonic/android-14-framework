package com.android.server.health;

import android.hardware.health.Translate;
import android.hardware.health.V2_0.HealthInfo;
import android.hardware.health.V2_0.IHealth;
import android.hardware.health.V2_0.Result;
import android.hardware.health.V2_1.IHealthInfoCallback;
import android.os.RemoteException;
import android.os.Trace;
import android.util.Slog;
import com.android.server.health.HealthServiceWrapperHidl;
/* loaded from: classes.dex */
public class HealthHalCallbackHidl extends IHealthInfoCallback.Stub implements HealthServiceWrapperHidl.Callback {
    public static final String TAG = HealthHalCallbackHidl.class.getSimpleName();
    public HealthInfoCallback mCallback;

    public static void traceBegin(String str) {
        Trace.traceBegin(524288L, str);
    }

    public static void traceEnd() {
        Trace.traceEnd(524288L);
    }

    public HealthHalCallbackHidl(HealthInfoCallback healthInfoCallback) {
        this.mCallback = healthInfoCallback;
    }

    @Override // android.hardware.health.V2_0.IHealthInfoCallback
    public void healthInfoChanged(HealthInfo healthInfo) {
        android.hardware.health.V2_1.HealthInfo healthInfo2 = new android.hardware.health.V2_1.HealthInfo();
        healthInfo2.legacy = healthInfo;
        healthInfo2.batteryCapacityLevel = -1;
        healthInfo2.batteryChargeTimeToFullNowSeconds = -1L;
        this.mCallback.update(Translate.h2aTranslate(healthInfo2));
    }

    @Override // android.hardware.health.V2_1.IHealthInfoCallback
    public void healthInfoChanged_2_1(android.hardware.health.V2_1.HealthInfo healthInfo) {
        this.mCallback.update(Translate.h2aTranslate(healthInfo));
    }

    @Override // com.android.server.health.HealthServiceWrapperHidl.Callback
    public void onRegistration(IHealth iHealth, IHealth iHealth2, String str) {
        int registerCallback;
        if (iHealth2 == null) {
            return;
        }
        traceBegin("HealthUnregisterCallback");
        if (iHealth != null) {
            try {
                try {
                    int unregisterCallback = iHealth.unregisterCallback(this);
                    if (unregisterCallback != 0) {
                        String str2 = TAG;
                        Slog.w(str2, "health: cannot unregister previous callback: " + Result.toString(unregisterCallback));
                    }
                } catch (RemoteException e) {
                    String str3 = TAG;
                    Slog.w(str3, "health: cannot unregister previous callback (transaction error): " + e.getMessage());
                }
            } finally {
            }
        }
        traceEnd();
        traceBegin("HealthRegisterCallback");
        try {
            try {
                registerCallback = iHealth2.registerCallback(this);
            } catch (RemoteException e2) {
                String str4 = TAG;
                Slog.e(str4, "health: cannot register callback (transaction error): " + e2.getMessage());
            }
            if (registerCallback != 0) {
                String str5 = TAG;
                Slog.w(str5, "health: cannot register callback: " + Result.toString(registerCallback));
                return;
            }
            iHealth2.update();
        } finally {
        }
    }
}
