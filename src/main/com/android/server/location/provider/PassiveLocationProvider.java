package com.android.server.location.provider;

import android.content.Context;
import android.location.LocationResult;
import android.location.provider.ProviderProperties;
import android.location.provider.ProviderRequest;
import android.location.util.identity.CallerIdentity;
import android.os.Bundle;
import com.android.internal.util.ConcurrentUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Collections;
/* loaded from: classes.dex */
public class PassiveLocationProvider extends AbstractLocationProvider {
    public static final ProviderProperties PROPERTIES = new ProviderProperties.Builder().setPowerUsage(1).setAccuracy(1).build();

    @Override // com.android.server.location.provider.AbstractLocationProvider
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }

    @Override // com.android.server.location.provider.AbstractLocationProvider
    public void onExtraCommand(int i, int i2, String str, Bundle bundle) {
    }

    @Override // com.android.server.location.provider.AbstractLocationProvider
    public void onSetRequest(ProviderRequest providerRequest) {
    }

    public PassiveLocationProvider(Context context) {
        super(ConcurrentUtils.DIRECT_EXECUTOR, CallerIdentity.fromContext(context), PROPERTIES, Collections.emptySet());
        setAllowed(true);
    }

    public void updateLocation(LocationResult locationResult) {
        reportLocation(locationResult);
    }

    @Override // com.android.server.location.provider.AbstractLocationProvider
    public void onFlush(Runnable runnable) {
        runnable.run();
    }
}
