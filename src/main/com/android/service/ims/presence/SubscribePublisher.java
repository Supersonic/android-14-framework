package com.android.service.ims.presence;
/* loaded from: classes.dex */
public interface SubscribePublisher {
    int getStackStatusForCapabilityRequest();

    int requestAvailability(String str, int i);

    int requestCapability(String[] strArr, int i);

    void updatePublisherState(int i);
}
