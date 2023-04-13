package com.android.server.biometrics.sensors;

import android.hardware.biometrics.BiometricAuthenticator;
/* loaded from: classes.dex */
public interface RemovalConsumer {
    void onRemoved(BiometricAuthenticator.Identifier identifier, int i);
}
