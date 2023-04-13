package com.android.server.biometrics.sensors;

import android.content.Context;
import android.hardware.biometrics.BiometricAuthenticator;
import android.hardware.biometrics.BiometricAuthenticator.Identifier;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.IInvalidationCallback;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
/* loaded from: classes.dex */
public class InvalidationRequesterClient<S extends BiometricAuthenticator.Identifier> extends BaseClientMonitor {
    public final BiometricManager mBiometricManager;
    public final IInvalidationCallback mInvalidationCallback;
    public final BiometricUtils<S> mUtils;

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public int getProtoEnum() {
        return 14;
    }

    public InvalidationRequesterClient(Context context, int i, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext, BiometricUtils<S> biometricUtils) {
        super(context, null, null, i, context.getOpPackageName(), 0, i2, biometricLogger, biometricContext);
        this.mInvalidationCallback = new IInvalidationCallback.Stub() { // from class: com.android.server.biometrics.sensors.InvalidationRequesterClient.1
            public void onCompleted() {
                InvalidationRequesterClient.this.mUtils.setInvalidationInProgress(InvalidationRequesterClient.this.getContext(), InvalidationRequesterClient.this.getTargetUserId(), false);
                InvalidationRequesterClient invalidationRequesterClient = InvalidationRequesterClient.this;
                invalidationRequesterClient.mCallback.onClientFinished(invalidationRequesterClient, true);
            }
        };
        this.mBiometricManager = (BiometricManager) context.getSystemService(BiometricManager.class);
        this.mUtils = biometricUtils;
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public void start(ClientMonitorCallback clientMonitorCallback) {
        super.start(clientMonitorCallback);
        this.mUtils.setInvalidationInProgress(getContext(), getTargetUserId(), true);
        this.mBiometricManager.invalidateAuthenticatorIds(getTargetUserId(), getSensorId(), this.mInvalidationCallback);
    }
}
