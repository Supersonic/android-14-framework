package com.android.server.timedetector;

import android.content.Context;
import android.os.Handler;
import com.android.server.timezonedetector.CurrentUserIdentityInjector;
import java.util.Objects;
/* loaded from: classes2.dex */
public class TimeDetectorInternalImpl implements TimeDetectorInternal {
    public final Context mContext;
    public final CurrentUserIdentityInjector mCurrentUserIdentityInjector;
    public final Handler mHandler;
    public final ServiceConfigAccessor mServiceConfigAccessor;
    public final TimeDetectorStrategy mTimeDetectorStrategy;

    public TimeDetectorInternalImpl(Context context, Handler handler, CurrentUserIdentityInjector currentUserIdentityInjector, ServiceConfigAccessor serviceConfigAccessor, TimeDetectorStrategy timeDetectorStrategy) {
        Objects.requireNonNull(context);
        this.mContext = context;
        Objects.requireNonNull(handler);
        this.mHandler = handler;
        Objects.requireNonNull(currentUserIdentityInjector);
        this.mCurrentUserIdentityInjector = currentUserIdentityInjector;
        Objects.requireNonNull(serviceConfigAccessor);
        this.mServiceConfigAccessor = serviceConfigAccessor;
        Objects.requireNonNull(timeDetectorStrategy);
        this.mTimeDetectorStrategy = timeDetectorStrategy;
    }

    @Override // com.android.server.timedetector.TimeDetectorInternal
    public void suggestNetworkTime(final NetworkTimeSuggestion networkTimeSuggestion) {
        Objects.requireNonNull(networkTimeSuggestion);
        this.mHandler.post(new Runnable() { // from class: com.android.server.timedetector.TimeDetectorInternalImpl$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                TimeDetectorInternalImpl.this.lambda$suggestNetworkTime$0(networkTimeSuggestion);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$suggestNetworkTime$0(NetworkTimeSuggestion networkTimeSuggestion) {
        this.mTimeDetectorStrategy.suggestNetworkTime(networkTimeSuggestion);
    }

    @Override // com.android.server.timedetector.TimeDetectorInternal
    public void suggestGnssTime(final GnssTimeSuggestion gnssTimeSuggestion) {
        Objects.requireNonNull(gnssTimeSuggestion);
        this.mHandler.post(new Runnable() { // from class: com.android.server.timedetector.TimeDetectorInternalImpl$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                TimeDetectorInternalImpl.this.lambda$suggestGnssTime$1(gnssTimeSuggestion);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$suggestGnssTime$1(GnssTimeSuggestion gnssTimeSuggestion) {
        this.mTimeDetectorStrategy.suggestGnssTime(gnssTimeSuggestion);
    }
}
