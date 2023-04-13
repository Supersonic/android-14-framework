package com.android.server.timezonedetector.location;

import android.os.SystemClock;
import com.android.server.timezonedetector.ConfigurationInternal;
import com.android.server.timezonedetector.ServiceConfigAccessor;
import com.android.server.timezonedetector.StateChangeListener;
import com.android.server.timezonedetector.location.LocationTimeZoneProviderController;
import java.time.Duration;
import java.util.Objects;
/* loaded from: classes2.dex */
public class LocationTimeZoneProviderControllerEnvironmentImpl extends LocationTimeZoneProviderController.Environment {
    public final StateChangeListener mConfigurationInternalChangeListener;
    public final ServiceConfigAccessor mServiceConfigAccessor;

    public LocationTimeZoneProviderControllerEnvironmentImpl(ThreadingDomain threadingDomain, ServiceConfigAccessor serviceConfigAccessor, final LocationTimeZoneProviderController locationTimeZoneProviderController) {
        super(threadingDomain);
        Objects.requireNonNull(serviceConfigAccessor);
        ServiceConfigAccessor serviceConfigAccessor2 = serviceConfigAccessor;
        this.mServiceConfigAccessor = serviceConfigAccessor2;
        StateChangeListener stateChangeListener = new StateChangeListener() { // from class: com.android.server.timezonedetector.location.LocationTimeZoneProviderControllerEnvironmentImpl$$ExternalSyntheticLambda0
            @Override // com.android.server.timezonedetector.StateChangeListener
            public final void onChange() {
                LocationTimeZoneProviderControllerEnvironmentImpl.this.lambda$new$0(locationTimeZoneProviderController);
            }
        };
        this.mConfigurationInternalChangeListener = stateChangeListener;
        serviceConfigAccessor2.addConfigurationInternalChangeListener(stateChangeListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(final LocationTimeZoneProviderController locationTimeZoneProviderController) {
        ThreadingDomain threadingDomain = this.mThreadingDomain;
        Objects.requireNonNull(locationTimeZoneProviderController);
        threadingDomain.post(new Runnable() { // from class: com.android.server.timezonedetector.location.LocationTimeZoneProviderControllerEnvironmentImpl$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                LocationTimeZoneProviderController.this.onConfigurationInternalChanged();
            }
        });
    }

    public void destroy() {
        this.mServiceConfigAccessor.removeConfigurationInternalChangeListener(this.mConfigurationInternalChangeListener);
    }

    @Override // com.android.server.timezonedetector.location.LocationTimeZoneProviderController.Environment
    public ConfigurationInternal getCurrentUserConfigurationInternal() {
        return this.mServiceConfigAccessor.getCurrentUserConfigurationInternal();
    }

    @Override // com.android.server.timezonedetector.location.LocationTimeZoneProviderController.Environment
    public Duration getProviderInitializationTimeout() {
        return this.mServiceConfigAccessor.getLocationTimeZoneProviderInitializationTimeout();
    }

    @Override // com.android.server.timezonedetector.location.LocationTimeZoneProviderController.Environment
    public Duration getProviderInitializationTimeoutFuzz() {
        return this.mServiceConfigAccessor.getLocationTimeZoneProviderInitializationTimeoutFuzz();
    }

    @Override // com.android.server.timezonedetector.location.LocationTimeZoneProviderController.Environment
    public Duration getUncertaintyDelay() {
        return this.mServiceConfigAccessor.getLocationTimeZoneUncertaintyDelay();
    }

    @Override // com.android.server.timezonedetector.location.LocationTimeZoneProviderController.Environment
    public Duration getProviderEventFilteringAgeThreshold() {
        return this.mServiceConfigAccessor.getLocationTimeZoneProviderEventFilteringAgeThreshold();
    }

    @Override // com.android.server.timezonedetector.location.LocationTimeZoneProviderController.Environment
    public long elapsedRealtimeMillis() {
        return SystemClock.elapsedRealtime();
    }
}
