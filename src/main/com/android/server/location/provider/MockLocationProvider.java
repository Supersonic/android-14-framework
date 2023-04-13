package com.android.server.location.provider;

import android.location.Location;
import android.location.LocationResult;
import android.location.provider.ProviderProperties;
import android.location.provider.ProviderRequest;
import android.location.util.identity.CallerIdentity;
import android.os.Bundle;
import com.android.internal.util.ConcurrentUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Set;
/* loaded from: classes.dex */
public class MockLocationProvider extends AbstractLocationProvider {
    public Location mLocation;

    @Override // com.android.server.location.provider.AbstractLocationProvider
    public void onExtraCommand(int i, int i2, String str, Bundle bundle) {
    }

    @Override // com.android.server.location.provider.AbstractLocationProvider
    public void onSetRequest(ProviderRequest providerRequest) {
    }

    public MockLocationProvider(ProviderProperties providerProperties, CallerIdentity callerIdentity, Set<String> set) {
        super(ConcurrentUtils.DIRECT_EXECUTOR, callerIdentity, providerProperties, set);
    }

    public void setProviderAllowed(boolean z) {
        setAllowed(z);
    }

    public void setProviderLocation(Location location) {
        Location location2 = new Location(location);
        location2.setIsFromMockProvider(true);
        this.mLocation = location2;
        reportLocation(LocationResult.wrap(new Location[]{location2}).validate());
    }

    @Override // com.android.server.location.provider.AbstractLocationProvider
    public void onFlush(Runnable runnable) {
        runnable.run();
    }

    @Override // com.android.server.location.provider.AbstractLocationProvider
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("last mock location=" + this.mLocation);
    }
}
