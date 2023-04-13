package com.android.ims;

import com.android.ims.internal.IImsServiceFeatureCallback;
/* loaded from: classes.dex */
public interface FeatureUpdates {
    void associate(ImsFeatureContainer imsFeatureContainer, int i);

    void invalidate();

    void registerFeatureCallback(int i, IImsServiceFeatureCallback iImsServiceFeatureCallback);

    void unregisterFeatureCallback(IImsServiceFeatureCallback iImsServiceFeatureCallback);

    void updateFeatureCapabilities(long j);

    void updateFeatureState(int i);
}
