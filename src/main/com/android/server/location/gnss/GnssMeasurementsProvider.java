package com.android.server.location.gnss;

import android.location.GnssMeasurementRequest;
import android.location.GnssMeasurementsEvent;
import android.location.IGnssMeasurementsListener;
import android.location.util.identity.CallerIdentity;
import android.os.IBinder;
import android.util.Log;
import com.android.internal.listeners.ListenerExecutor;
import com.android.server.location.gnss.GnssListenerMultiplexer;
import com.android.server.location.gnss.hal.GnssNative;
import com.android.server.location.injector.AppOpsHelper;
import com.android.server.location.injector.Injector;
import com.android.server.location.injector.LocationUsageLogger;
import com.android.server.location.injector.SettingsHelper;
import java.util.Collection;
import java.util.function.Function;
/* loaded from: classes.dex */
public final class GnssMeasurementsProvider extends GnssListenerMultiplexer<GnssMeasurementRequest, IGnssMeasurementsListener, GnssMeasurementRequest> implements SettingsHelper.GlobalSettingChangedListener, GnssNative.BaseCallbacks, GnssNative.MeasurementCallbacks {
    public final AppOpsHelper mAppOpsHelper;
    public final GnssNative mGnssNative;
    public final LocationUsageLogger mLogger;

    @Override // com.android.server.location.gnss.GnssListenerMultiplexer, com.android.server.location.listeners.ListenerMultiplexer
    public /* bridge */ /* synthetic */ Object mergeRegistrations(Collection collection) {
        return mergeRegistrations((Collection<GnssListenerMultiplexer<GnssMeasurementRequest, IGnssMeasurementsListener, GnssMeasurementRequest>.GnssListenerRegistration>) collection);
    }

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public /* bridge */ /* synthetic */ boolean registerWithService(Object obj, Collection collection) {
        return registerWithService((GnssMeasurementRequest) obj, (Collection<GnssListenerMultiplexer<GnssMeasurementRequest, IGnssMeasurementsListener, GnssMeasurementRequest>.GnssListenerRegistration>) collection);
    }

    /* loaded from: classes.dex */
    public class GnssMeasurementListenerRegistration extends GnssListenerMultiplexer<GnssMeasurementRequest, IGnssMeasurementsListener, GnssMeasurementRequest>.GnssListenerRegistration {
        public GnssMeasurementListenerRegistration(GnssMeasurementRequest gnssMeasurementRequest, CallerIdentity callerIdentity, IGnssMeasurementsListener iGnssMeasurementsListener) {
            super(gnssMeasurementRequest, callerIdentity, iGnssMeasurementsListener);
        }

        @Override // com.android.server.location.gnss.GnssListenerMultiplexer.GnssListenerRegistration, com.android.server.location.listeners.BinderListenerRegistration, com.android.server.location.listeners.RemovableListenerRegistration
        public void onRegister() {
            super.onRegister();
            executeOperation(new ListenerExecutor.ListenerOperation() { // from class: com.android.server.location.gnss.GnssMeasurementsProvider$GnssMeasurementListenerRegistration$$ExternalSyntheticLambda0
                public final void operate(Object obj) {
                    ((IGnssMeasurementsListener) obj).onStatusChanged(1);
                }
            });
        }

        @Override // com.android.server.location.listeners.ListenerRegistration
        public void onActive() {
            GnssMeasurementsProvider.this.mAppOpsHelper.startOpNoThrow(42, getIdentity());
        }

        @Override // com.android.server.location.listeners.ListenerRegistration
        public void onInactive() {
            GnssMeasurementsProvider.this.mAppOpsHelper.finishOp(42, getIdentity());
        }
    }

    public GnssMeasurementsProvider(Injector injector, GnssNative gnssNative) {
        super(injector);
        this.mAppOpsHelper = injector.getAppOpsHelper();
        this.mLogger = injector.getLocationUsageLogger();
        this.mGnssNative = gnssNative;
        gnssNative.addBaseCallbacks(this);
        gnssNative.addMeasurementCallbacks(this);
    }

    @Override // com.android.server.location.gnss.GnssListenerMultiplexer
    public boolean isSupported() {
        return this.mGnssNative.isMeasurementSupported();
    }

    @Override // com.android.server.location.gnss.GnssListenerMultiplexer
    public void addListener(GnssMeasurementRequest gnssMeasurementRequest, CallerIdentity callerIdentity, IGnssMeasurementsListener iGnssMeasurementsListener) {
        super.addListener((GnssMeasurementsProvider) gnssMeasurementRequest, callerIdentity, (CallerIdentity) iGnssMeasurementsListener);
    }

    @Override // com.android.server.location.gnss.GnssListenerMultiplexer
    public GnssListenerMultiplexer<GnssMeasurementRequest, IGnssMeasurementsListener, GnssMeasurementRequest>.GnssListenerRegistration createRegistration(GnssMeasurementRequest gnssMeasurementRequest, CallerIdentity callerIdentity, IGnssMeasurementsListener iGnssMeasurementsListener) {
        return new GnssMeasurementListenerRegistration(gnssMeasurementRequest, callerIdentity, iGnssMeasurementsListener);
    }

    public boolean registerWithService(GnssMeasurementRequest gnssMeasurementRequest, Collection<GnssListenerMultiplexer<GnssMeasurementRequest, IGnssMeasurementsListener, GnssMeasurementRequest>.GnssListenerRegistration> collection) {
        if (gnssMeasurementRequest.getIntervalMillis() == Integer.MAX_VALUE) {
            return true;
        }
        if (this.mGnssNative.stopMeasurementCollection()) {
            if (GnssManagerService.f1148D) {
                Log.d("GnssManager", "stopping gnss measurements");
            }
        } else {
            Log.e("GnssManager", "error stopping gnss measurements");
        }
        if (this.mGnssNative.startMeasurementCollection(gnssMeasurementRequest.isFullTracking(), gnssMeasurementRequest.isCorrelationVectorOutputsEnabled(), gnssMeasurementRequest.getIntervalMillis())) {
            if (GnssManagerService.f1148D) {
                Log.d("GnssManager", "starting gnss measurements (" + gnssMeasurementRequest + ")");
            }
            return true;
        }
        Log.e("GnssManager", "error starting gnss measurements");
        return false;
    }

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public void unregisterWithService() {
        if (this.mGnssNative.stopMeasurementCollection()) {
            if (GnssManagerService.f1148D) {
                Log.d("GnssManager", "stopping gnss measurements");
                return;
            }
            return;
        }
        Log.e("GnssManager", "error stopping gnss measurements");
    }

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public void onActive() {
        this.mSettingsHelper.addOnGnssMeasurementsFullTrackingEnabledChangedListener(this);
    }

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public void onInactive() {
        this.mSettingsHelper.removeOnGnssMeasurementsFullTrackingEnabledChangedListener(this);
    }

    @Override // com.android.server.location.injector.SettingsHelper.GlobalSettingChangedListener
    public void onSettingChanged() {
        updateService();
    }

    @Override // com.android.server.location.gnss.GnssListenerMultiplexer, com.android.server.location.listeners.ListenerMultiplexer
    public GnssMeasurementRequest mergeRegistrations(Collection<GnssListenerMultiplexer<GnssMeasurementRequest, IGnssMeasurementsListener, GnssMeasurementRequest>.GnssListenerRegistration> collection) {
        boolean isGnssMeasurementsFullTrackingEnabled = this.mSettingsHelper.isGnssMeasurementsFullTrackingEnabled();
        boolean z = false;
        int i = Integer.MAX_VALUE;
        for (GnssListenerMultiplexer<GnssMeasurementRequest, IGnssMeasurementsListener, GnssMeasurementRequest>.GnssListenerRegistration gnssListenerRegistration : collection) {
            GnssMeasurementRequest request = gnssListenerRegistration.getRequest();
            if (request.getIntervalMillis() != Integer.MAX_VALUE) {
                if (request.isFullTracking()) {
                    isGnssMeasurementsFullTrackingEnabled = true;
                }
                if (request.isCorrelationVectorOutputsEnabled()) {
                    z = true;
                }
                i = Math.min(i, request.getIntervalMillis());
            }
        }
        return new GnssMeasurementRequest.Builder().setFullTracking(isGnssMeasurementsFullTrackingEnabled).setCorrelationVectorOutputsEnabled(z).setIntervalMillis(i).build();
    }

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public void onRegistrationAdded(IBinder iBinder, GnssListenerMultiplexer<GnssMeasurementRequest, IGnssMeasurementsListener, GnssMeasurementRequest>.GnssListenerRegistration gnssListenerRegistration) {
        this.mLogger.logLocationApiUsage(0, 2, gnssListenerRegistration.getIdentity().getPackageName(), gnssListenerRegistration.getIdentity().getAttributionTag(), null, null, true, false, null, gnssListenerRegistration.isForeground());
    }

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public void onRegistrationRemoved(IBinder iBinder, GnssListenerMultiplexer<GnssMeasurementRequest, IGnssMeasurementsListener, GnssMeasurementRequest>.GnssListenerRegistration gnssListenerRegistration) {
        this.mLogger.logLocationApiUsage(1, 2, gnssListenerRegistration.getIdentity().getPackageName(), gnssListenerRegistration.getIdentity().getAttributionTag(), null, null, true, false, null, gnssListenerRegistration.isForeground());
    }

    @Override // com.android.server.location.gnss.hal.GnssNative.BaseCallbacks
    public void onHalRestarted() {
        resetService();
    }

    @Override // com.android.server.location.gnss.hal.GnssNative.MeasurementCallbacks
    public void onReportMeasurements(final GnssMeasurementsEvent gnssMeasurementsEvent) {
        deliverToListeners(new Function() { // from class: com.android.server.location.gnss.GnssMeasurementsProvider$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                ListenerExecutor.ListenerOperation lambda$onReportMeasurements$1;
                lambda$onReportMeasurements$1 = GnssMeasurementsProvider.this.lambda$onReportMeasurements$1(gnssMeasurementsEvent, (GnssListenerMultiplexer.GnssListenerRegistration) obj);
                return lambda$onReportMeasurements$1;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ListenerExecutor.ListenerOperation lambda$onReportMeasurements$1(final GnssMeasurementsEvent gnssMeasurementsEvent, GnssListenerMultiplexer.GnssListenerRegistration gnssListenerRegistration) {
        if (this.mAppOpsHelper.noteOpNoThrow(1, gnssListenerRegistration.getIdentity())) {
            return new ListenerExecutor.ListenerOperation() { // from class: com.android.server.location.gnss.GnssMeasurementsProvider$$ExternalSyntheticLambda1
                public final void operate(Object obj) {
                    ((IGnssMeasurementsListener) obj).onGnssMeasurementsReceived(gnssMeasurementsEvent);
                }
            };
        }
        return null;
    }
}
