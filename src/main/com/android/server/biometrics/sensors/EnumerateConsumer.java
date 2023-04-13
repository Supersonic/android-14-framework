package com.android.server.biometrics.sensors;

import android.hardware.biometrics.BiometricAuthenticator;
/* loaded from: classes.dex */
public interface EnumerateConsumer {
    void onEnumerationResult(BiometricAuthenticator.Identifier identifier, int i);
}
