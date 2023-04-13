package com.android.server.biometrics.sensors;

import android.content.Context;
import android.hardware.biometrics.BiometricAuthenticator;
import android.hardware.biometrics.BiometricAuthenticator.Identifier;
import java.util.List;
/* loaded from: classes.dex */
public interface BiometricUtils<T extends BiometricAuthenticator.Identifier> {
    void addBiometricForUser(Context context, int i, T t);

    List<T> getBiometricsForUser(Context context, int i);

    void removeBiometricForUser(Context context, int i, int i2);

    void setInvalidationInProgress(Context context, int i, boolean z);
}
