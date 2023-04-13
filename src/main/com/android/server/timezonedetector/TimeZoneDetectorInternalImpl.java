package com.android.server.timezonedetector;

import android.content.Context;
import android.os.Handler;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class TimeZoneDetectorInternalImpl implements TimeZoneDetectorInternal {
    public final Context mContext;
    public final CurrentUserIdentityInjector mCurrentUserIdentityInjector;
    public final Handler mHandler;
    public final TimeZoneDetectorStrategy mTimeZoneDetectorStrategy;

    public TimeZoneDetectorInternalImpl(Context context, Handler handler, CurrentUserIdentityInjector currentUserIdentityInjector, TimeZoneDetectorStrategy timeZoneDetectorStrategy) {
        Objects.requireNonNull(context);
        this.mContext = context;
        Objects.requireNonNull(handler);
        this.mHandler = handler;
        Objects.requireNonNull(currentUserIdentityInjector);
        this.mCurrentUserIdentityInjector = currentUserIdentityInjector;
        Objects.requireNonNull(timeZoneDetectorStrategy);
        this.mTimeZoneDetectorStrategy = timeZoneDetectorStrategy;
    }

    @Override // com.android.server.timezonedetector.TimeZoneDetectorInternal
    public void handleLocationAlgorithmEvent(final LocationAlgorithmEvent locationAlgorithmEvent) {
        Objects.requireNonNull(locationAlgorithmEvent);
        this.mHandler.post(new Runnable() { // from class: com.android.server.timezonedetector.TimeZoneDetectorInternalImpl$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                TimeZoneDetectorInternalImpl.this.lambda$handleLocationAlgorithmEvent$0(locationAlgorithmEvent);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleLocationAlgorithmEvent$0(LocationAlgorithmEvent locationAlgorithmEvent) {
        this.mTimeZoneDetectorStrategy.handleLocationAlgorithmEvent(locationAlgorithmEvent);
    }

    @Override // com.android.server.timezonedetector.TimeZoneDetectorInternal
    public MetricsTimeZoneDetectorState generateMetricsState() {
        return this.mTimeZoneDetectorStrategy.generateMetricsState();
    }
}
