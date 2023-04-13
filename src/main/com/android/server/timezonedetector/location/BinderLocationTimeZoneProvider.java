package com.android.server.timezonedetector.location;

import android.service.timezone.TimeZoneProviderEvent;
import android.util.IndentingPrintWriter;
import com.android.server.timezonedetector.location.LocationTimeZoneProvider;
import com.android.server.timezonedetector.location.LocationTimeZoneProviderProxy;
import java.time.Duration;
import java.util.Objects;
/* loaded from: classes2.dex */
public class BinderLocationTimeZoneProvider extends LocationTimeZoneProvider {
    public final LocationTimeZoneProviderProxy mProxy;

    public BinderLocationTimeZoneProvider(LocationTimeZoneProvider.ProviderMetricsLogger providerMetricsLogger, ThreadingDomain threadingDomain, String str, LocationTimeZoneProviderProxy locationTimeZoneProviderProxy, boolean z) {
        super(providerMetricsLogger, threadingDomain, str, new ZoneInfoDbTimeZoneProviderEventPreProcessor(), z);
        Objects.requireNonNull(locationTimeZoneProviderProxy);
        this.mProxy = locationTimeZoneProviderProxy;
    }

    @Override // com.android.server.timezonedetector.location.LocationTimeZoneProvider
    public boolean onInitialize() {
        this.mProxy.initialize(new LocationTimeZoneProviderProxy.Listener() { // from class: com.android.server.timezonedetector.location.BinderLocationTimeZoneProvider.1
            @Override // com.android.server.timezonedetector.location.LocationTimeZoneProviderProxy.Listener
            public void onReportTimeZoneProviderEvent(TimeZoneProviderEvent timeZoneProviderEvent) {
                BinderLocationTimeZoneProvider.this.handleTimeZoneProviderEvent(timeZoneProviderEvent);
            }

            @Override // com.android.server.timezonedetector.location.LocationTimeZoneProviderProxy.Listener
            public void onProviderBound() {
                BinderLocationTimeZoneProvider.this.handleOnProviderBound();
            }

            @Override // com.android.server.timezonedetector.location.LocationTimeZoneProviderProxy.Listener
            public void onProviderUnbound() {
                BinderLocationTimeZoneProvider.this.handleTemporaryFailure("onProviderUnbound()");
            }
        });
        return true;
    }

    @Override // com.android.server.timezonedetector.location.LocationTimeZoneProvider
    public void onDestroy() {
        this.mProxy.destroy();
    }

    public final void handleOnProviderBound() {
        this.mThreadingDomain.assertCurrentThread();
        synchronized (this.mSharedLock) {
            LocationTimeZoneProvider.ProviderState providerState = this.mCurrentState.get();
            switch (providerState.stateEnum) {
                case 1:
                case 2:
                case 3:
                    LocationTimeZoneManagerService.debugLog("handleOnProviderBound mProviderName=" + this.mProviderName + ", currentState=" + providerState + ": Provider is started.");
                    break;
                case 4:
                    LocationTimeZoneManagerService.debugLog("handleOnProviderBound mProviderName=" + this.mProviderName + ", currentState=" + providerState + ": Provider is stopped.");
                    break;
                case 5:
                case 6:
                    LocationTimeZoneManagerService.debugLog("handleOnProviderBound, mProviderName=" + this.mProviderName + ", currentState=" + providerState + ": No state change required, provider is terminated.");
                    break;
                default:
                    throw new IllegalStateException("Unknown currentState=" + providerState);
            }
        }
    }

    @Override // com.android.server.timezonedetector.location.LocationTimeZoneProvider
    public void onStartUpdates(Duration duration, Duration duration2) {
        this.mProxy.setRequest(TimeZoneProviderRequest.createStartUpdatesRequest(duration, duration2));
    }

    @Override // com.android.server.timezonedetector.location.LocationTimeZoneProvider
    public void onStopUpdates() {
        this.mProxy.setRequest(TimeZoneProviderRequest.createStopUpdatesRequest());
    }

    @Override // com.android.server.timezonedetector.Dumpable
    public void dump(IndentingPrintWriter indentingPrintWriter, String[] strArr) {
        synchronized (this.mSharedLock) {
            indentingPrintWriter.println("{BinderLocationTimeZoneProvider}");
            indentingPrintWriter.println("mProviderName=" + this.mProviderName);
            indentingPrintWriter.println("mCurrentState=" + this.mCurrentState);
            indentingPrintWriter.println("mProxy=" + this.mProxy);
            indentingPrintWriter.println("State history:");
            indentingPrintWriter.increaseIndent();
            this.mCurrentState.dump(indentingPrintWriter);
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println("Proxy details:");
            indentingPrintWriter.increaseIndent();
            this.mProxy.dump(indentingPrintWriter, strArr);
            indentingPrintWriter.decreaseIndent();
        }
    }

    public String toString() {
        String str;
        synchronized (this.mSharedLock) {
            str = "BinderLocationTimeZoneProvider{mProviderName=" + this.mProviderName + ", mCurrentState=" + this.mCurrentState + ", mProxy=" + this.mProxy + '}';
        }
        return str;
    }
}
