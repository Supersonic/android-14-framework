package com.android.ims.rcs.uce;

import com.android.ims.RcsFeatureManager;
/* loaded from: classes.dex */
public interface ControllerBase {
    void onCarrierConfigChanged();

    void onDestroy();

    void onRcsConnected(RcsFeatureManager rcsFeatureManager);

    void onRcsDisconnected();
}
