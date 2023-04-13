package com.android.server.biometrics.log;

import android.content.Context;
import android.hardware.biometrics.common.OperationContext;
import com.android.server.biometrics.sensors.AuthSessionCoordinator;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public interface BiometricContext {
    AuthSessionCoordinator getAuthSessionCoordinator();

    BiometricContextSessionInfo getBiometricPromptSessionInfo();

    int getCurrentRotation();

    int getDockedState();

    int getFoldState();

    BiometricContextSessionInfo getKeyguardEntrySessionInfo();

    boolean isAod();

    boolean isAwake();

    boolean isDisplayOn();

    void subscribe(OperationContextExt operationContextExt, Consumer<OperationContext> consumer);

    void unsubscribe(OperationContextExt operationContextExt);

    OperationContextExt updateContext(OperationContextExt operationContextExt, boolean z);

    static BiometricContext getInstance(Context context) {
        return BiometricContextProvider.defaultProvider(context);
    }
}
