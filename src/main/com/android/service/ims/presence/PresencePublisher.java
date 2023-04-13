package com.android.service.ims.presence;

import android.telephony.ims.RcsContactUceCapability;
/* loaded from: classes.dex */
public interface PresencePublisher {
    int getPublisherState();

    int requestPublication(RcsContactUceCapability rcsContactUceCapability, String str, int i);

    void updatePublisherState(int i);
}
