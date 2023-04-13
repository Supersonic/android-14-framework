package com.android.server.biometrics.sensors;

import android.content.Context;
import android.hardware.biometrics.BiometricAuthenticator;
import android.hardware.biometrics.BiometricAuthenticator.Identifier;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import java.util.Map;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public abstract class RemovalClient<S extends BiometricAuthenticator.Identifier, T> extends HalClientMonitor<T> implements RemovalConsumer, EnrollmentModifier {
    public final Map<Integer, Long> mAuthenticatorIds;
    public final BiometricUtils<S> mBiometricUtils;
    public final boolean mHasEnrollmentsBeforeStarting;

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public int getProtoEnum() {
        return 4;
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public boolean interruptsPrecedingClients() {
        return true;
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void unableToStart() {
    }

    public RemovalClient(Context context, Supplier<T> supplier, IBinder iBinder, ClientMonitorCallbackConverter clientMonitorCallbackConverter, int i, String str, BiometricUtils<S> biometricUtils, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext, Map<Integer, Long> map) {
        super(context, supplier, iBinder, clientMonitorCallbackConverter, i, str, 0, i2, biometricLogger, biometricContext);
        this.mBiometricUtils = biometricUtils;
        this.mAuthenticatorIds = map;
        this.mHasEnrollmentsBeforeStarting = !biometricUtils.getBiometricsForUser(context, i).isEmpty();
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public void start(ClientMonitorCallback clientMonitorCallback) {
        super.start(clientMonitorCallback);
        startHalOperation();
    }

    @Override // com.android.server.biometrics.sensors.RemovalConsumer
    public void onRemoved(BiometricAuthenticator.Identifier identifier, int i) {
        if (identifier == null) {
            Slog.e("Biometrics/RemovalClient", "identifier was null, skipping onRemove()");
            try {
                if (getListener() != null) {
                    getListener().onError(getSensorId(), getCookie(), 6, 0);
                } else {
                    Slog.e("Biometrics/RemovalClient", "Error, listener was null, not sending onError callback");
                }
            } catch (RemoteException e) {
                Slog.w("Biometrics/RemovalClient", "Failed to send error to client for onRemoved", e);
            }
            this.mCallback.onClientFinished(this, false);
            return;
        }
        Slog.d("Biometrics/RemovalClient", "onRemoved: " + identifier.getBiometricId() + " remaining: " + i);
        this.mBiometricUtils.removeBiometricForUser(getContext(), getTargetUserId(), identifier.getBiometricId());
        try {
            if (getListener() != null) {
                getListener().onRemoved(identifier, i);
            }
        } catch (RemoteException e2) {
            Slog.w("Biometrics/RemovalClient", "Failed to notify Removed:", e2);
        }
        if (i == 0) {
            if (this.mBiometricUtils.getBiometricsForUser(getContext(), getTargetUserId()).isEmpty()) {
                Slog.d("Biometrics/RemovalClient", "Last biometric removed for user: " + getTargetUserId());
                this.mAuthenticatorIds.put(Integer.valueOf(getTargetUserId()), 0L);
            }
            this.mCallback.onClientFinished(this, true);
        }
    }

    @Override // com.android.server.biometrics.sensors.EnrollmentModifier
    public boolean hasEnrollmentStateChanged() {
        return (this.mBiometricUtils.getBiometricsForUser(getContext(), getTargetUserId()).isEmpty() ^ true) != this.mHasEnrollmentsBeforeStarting;
    }

    @Override // com.android.server.biometrics.sensors.EnrollmentModifier
    public boolean hasEnrollments() {
        return !this.mBiometricUtils.getBiometricsForUser(getContext(), getTargetUserId()).isEmpty();
    }
}
