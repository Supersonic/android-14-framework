package com.android.server.location.provider;

import android.location.provider.ProviderRequest;
import android.os.Bundle;
import com.android.server.location.provider.AbstractLocationProvider;
/* loaded from: classes.dex */
public interface LocationProviderController {
    void flush(Runnable runnable);

    boolean isStarted();

    void sendExtraCommand(int i, int i2, String str, Bundle bundle);

    AbstractLocationProvider.State setListener(AbstractLocationProvider.Listener listener);

    void setRequest(ProviderRequest providerRequest);

    void start();

    void stop();
}
