package com.android.server.health;

import android.hardware.health.HealthInfo;
import android.hardware.health.IHealth;
import android.hardware.health.IHealthInfoCallback;
import android.os.RemoteException;
import android.os.Trace;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
@VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
/* loaded from: classes.dex */
public class HealthRegCallbackAidl {
    public final IHealthInfoCallback mHalInfoCallback = new HalInfoCallback();
    public final HealthInfoCallback mServiceInfoCallback;

    public HealthRegCallbackAidl(HealthInfoCallback healthInfoCallback) {
        this.mServiceInfoCallback = healthInfoCallback;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public void onRegistration(IHealth iHealth, IHealth iHealth2) {
        if (this.mServiceInfoCallback == null) {
            return;
        }
        Trace.traceBegin(524288L, "HealthUnregisterCallbackAidl");
        try {
            unregisterCallback(iHealth, this.mHalInfoCallback);
            Trace.traceEnd(524288L);
            Trace.traceBegin(524288L, "HealthRegisterCallbackAidl");
            try {
                registerCallback(iHealth2, this.mHalInfoCallback);
            } finally {
            }
        } finally {
        }
    }

    public static void unregisterCallback(IHealth iHealth, IHealthInfoCallback iHealthInfoCallback) {
        if (iHealth == null) {
            return;
        }
        try {
            iHealth.unregisterCallback(iHealthInfoCallback);
        } catch (RemoteException e) {
            Slog.w("HealthRegCallbackAidl", "health: cannot unregister previous callback (transaction error): " + e.getMessage());
        }
    }

    public static void registerCallback(IHealth iHealth, IHealthInfoCallback iHealthInfoCallback) {
        try {
            iHealth.registerCallback(iHealthInfoCallback);
            try {
                iHealth.update();
            } catch (RemoteException e) {
                Slog.e("HealthRegCallbackAidl", "health: cannot update after registering health info callback", e);
            }
        } catch (RemoteException e2) {
            Slog.e("HealthRegCallbackAidl", "health: cannot register callback, framework may cease to receive updates on health / battery info!", e2);
        }
    }

    /* loaded from: classes.dex */
    public class HalInfoCallback extends IHealthInfoCallback.Stub {
        @Override // android.hardware.health.IHealthInfoCallback
        public String getInterfaceHash() {
            return "notfrozen";
        }

        @Override // android.hardware.health.IHealthInfoCallback
        public int getInterfaceVersion() {
            return 2;
        }

        public HalInfoCallback() {
        }

        @Override // android.hardware.health.IHealthInfoCallback
        public void healthInfoChanged(HealthInfo healthInfo) throws RemoteException {
            HealthRegCallbackAidl.this.mServiceInfoCallback.update(healthInfo);
        }
    }
}
