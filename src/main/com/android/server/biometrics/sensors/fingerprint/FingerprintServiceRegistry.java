package com.android.server.biometrics.sensors.fingerprint;

import android.hardware.biometrics.IBiometricService;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.hardware.fingerprint.IFingerprintAuthenticatorsRegisteredCallback;
import android.hardware.fingerprint.IFingerprintService;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.Utils;
import com.android.server.biometrics.sensors.BiometricServiceRegistry;
import java.util.List;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FingerprintServiceRegistry extends BiometricServiceRegistry<ServiceProvider, FingerprintSensorPropertiesInternal, IFingerprintAuthenticatorsRegisteredCallback> {
    public final IFingerprintService mService;

    public FingerprintServiceRegistry(IFingerprintService iFingerprintService, Supplier<IBiometricService> supplier) {
        super(supplier);
        this.mService = iFingerprintService;
    }

    @Override // com.android.server.biometrics.sensors.BiometricServiceRegistry
    public void registerService(IBiometricService iBiometricService, FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal) {
        try {
            iBiometricService.registerAuthenticator(fingerprintSensorPropertiesInternal.sensorId, 2, Utils.propertyStrengthToAuthenticatorStrength(fingerprintSensorPropertiesInternal.sensorStrength), new FingerprintAuthenticator(this.mService, fingerprintSensorPropertiesInternal.sensorId));
        } catch (RemoteException unused) {
            Slog.e("FingerprintServiceRegistry", "Remote exception when registering sensorId: " + fingerprintSensorPropertiesInternal.sensorId);
        }
    }

    @Override // com.android.server.biometrics.sensors.BiometricServiceRegistry
    public void invokeRegisteredCallback(IFingerprintAuthenticatorsRegisteredCallback iFingerprintAuthenticatorsRegisteredCallback, List<FingerprintSensorPropertiesInternal> list) throws RemoteException {
        iFingerprintAuthenticatorsRegisteredCallback.onAllAuthenticatorsRegistered(list);
    }
}
