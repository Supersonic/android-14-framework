package com.android.service.ims.presence;

import android.telephony.ims.RcsContactUceCapability;
import java.util.List;
/* loaded from: classes.dex */
public interface ContactCapabilityResponse {
    void onCapabilitiesUpdated(int i, List<RcsContactUceCapability> list, boolean z);

    void onError(int i, int i2);

    void onFinish(int i);

    void onSuccess(int i);

    void onTimeout(int i);
}
