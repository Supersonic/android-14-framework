package com.android.server.biometrics.sensors.fingerprint;

import android.hardware.biometrics.fingerprint.PointerContext;
/* loaded from: classes.dex */
public interface Udfps {
    void onPointerDown(PointerContext pointerContext);

    void onPointerUp(PointerContext pointerContext);

    void onUiReady();
}
