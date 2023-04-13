package com.android.internal.telephony.imsphone;

import com.android.ims.RcsFeatureManager;
/* loaded from: classes.dex */
public interface ImsRcsStatusListener {
    void onRcsConnected(int i, RcsFeatureManager rcsFeatureManager);

    void onRcsDisconnected(int i);
}
