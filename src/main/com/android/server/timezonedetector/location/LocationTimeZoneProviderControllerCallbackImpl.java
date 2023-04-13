package com.android.server.timezonedetector.location;

import com.android.server.LocalServices;
import com.android.server.timezonedetector.LocationAlgorithmEvent;
import com.android.server.timezonedetector.TimeZoneDetectorInternal;
import com.android.server.timezonedetector.location.LocationTimeZoneProviderController;
/* loaded from: classes2.dex */
public class LocationTimeZoneProviderControllerCallbackImpl extends LocationTimeZoneProviderController.Callback {
    public LocationTimeZoneProviderControllerCallbackImpl(ThreadingDomain threadingDomain) {
        super(threadingDomain);
    }

    @Override // com.android.server.timezonedetector.location.LocationTimeZoneProviderController.Callback
    public void sendEvent(LocationAlgorithmEvent locationAlgorithmEvent) {
        this.mThreadingDomain.assertCurrentThread();
        ((TimeZoneDetectorInternal) LocalServices.getService(TimeZoneDetectorInternal.class)).handleLocationAlgorithmEvent(locationAlgorithmEvent);
    }
}
