package com.android.server.timezonedetector.location;

import android.service.timezone.TimeZoneProviderEvent;
import android.util.IndentingPrintWriter;
import com.android.server.timezonedetector.location.LocationTimeZoneProvider;
import java.time.Duration;
/* loaded from: classes2.dex */
public class DisabledLocationTimeZoneProvider extends LocationTimeZoneProvider {
    public static /* synthetic */ TimeZoneProviderEvent lambda$new$0(TimeZoneProviderEvent timeZoneProviderEvent) {
        return timeZoneProviderEvent;
    }

    @Override // com.android.server.timezonedetector.location.LocationTimeZoneProvider
    public void onDestroy() {
    }

    @Override // com.android.server.timezonedetector.location.LocationTimeZoneProvider
    public boolean onInitialize() {
        return false;
    }

    public DisabledLocationTimeZoneProvider(LocationTimeZoneProvider.ProviderMetricsLogger providerMetricsLogger, ThreadingDomain threadingDomain, String str, boolean z) {
        super(providerMetricsLogger, threadingDomain, str, new TimeZoneProviderEventPreProcessor() { // from class: com.android.server.timezonedetector.location.DisabledLocationTimeZoneProvider$$ExternalSyntheticLambda0
            @Override // com.android.server.timezonedetector.location.TimeZoneProviderEventPreProcessor
            public final TimeZoneProviderEvent preProcess(TimeZoneProviderEvent timeZoneProviderEvent) {
                TimeZoneProviderEvent lambda$new$0;
                lambda$new$0 = DisabledLocationTimeZoneProvider.lambda$new$0(timeZoneProviderEvent);
                return lambda$new$0;
            }
        }, z);
    }

    @Override // com.android.server.timezonedetector.location.LocationTimeZoneProvider
    public void onStartUpdates(Duration duration, Duration duration2) {
        throw new UnsupportedOperationException("Provider is disabled");
    }

    @Override // com.android.server.timezonedetector.location.LocationTimeZoneProvider
    public void onStopUpdates() {
        throw new UnsupportedOperationException("Provider is disabled");
    }

    @Override // com.android.server.timezonedetector.Dumpable
    public void dump(IndentingPrintWriter indentingPrintWriter, String[] strArr) {
        synchronized (this.mSharedLock) {
            indentingPrintWriter.println("{DisabledLocationTimeZoneProvider}");
            indentingPrintWriter.println("mProviderName=" + this.mProviderName);
            indentingPrintWriter.println("mCurrentState=" + this.mCurrentState);
        }
    }

    public String toString() {
        String str;
        synchronized (this.mSharedLock) {
            str = "DisabledLocationTimeZoneProvider{mProviderName=" + this.mProviderName + ", mCurrentState=" + this.mCurrentState + '}';
        }
        return str;
    }
}
