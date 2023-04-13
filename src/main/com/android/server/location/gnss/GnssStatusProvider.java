package com.android.server.location.gnss;

import android.location.GnssStatus;
import android.location.IGnssStatusListener;
import android.location.util.identity.CallerIdentity;
import android.os.IBinder;
import android.util.Log;
import com.android.internal.listeners.ListenerExecutor;
import com.android.server.location.gnss.GnssListenerMultiplexer;
import com.android.server.location.gnss.hal.GnssNative;
import com.android.server.location.injector.AppOpsHelper;
import com.android.server.location.injector.Injector;
import com.android.server.location.injector.LocationUsageLogger;
import java.util.Collection;
import java.util.function.Function;
/* loaded from: classes.dex */
public class GnssStatusProvider extends GnssListenerMultiplexer<Void, IGnssStatusListener, Void> implements GnssNative.BaseCallbacks, GnssNative.StatusCallbacks, GnssNative.SvStatusCallbacks {
    public final AppOpsHelper mAppOpsHelper;
    public final GnssNative mGnssNative;
    public boolean mIsNavigating;
    public final LocationUsageLogger mLogger;

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public /* bridge */ /* synthetic */ boolean registerWithService(Object obj, Collection collection) {
        return registerWithService((Void) obj, (Collection<GnssListenerMultiplexer<Void, IGnssStatusListener, Void>.GnssListenerRegistration>) collection);
    }

    public GnssStatusProvider(Injector injector, GnssNative gnssNative) {
        super(injector);
        this.mIsNavigating = false;
        this.mAppOpsHelper = injector.getAppOpsHelper();
        this.mLogger = injector.getLocationUsageLogger();
        this.mGnssNative = gnssNative;
        gnssNative.addBaseCallbacks(this);
        gnssNative.addStatusCallbacks(this);
        gnssNative.addSvStatusCallbacks(this);
    }

    public void addListener(CallerIdentity callerIdentity, IGnssStatusListener iGnssStatusListener) {
        super.addListener(callerIdentity, (CallerIdentity) iGnssStatusListener);
    }

    public boolean registerWithService(Void r1, Collection<GnssListenerMultiplexer<Void, IGnssStatusListener, Void>.GnssListenerRegistration> collection) {
        if (this.mGnssNative.startSvStatusCollection()) {
            if (GnssManagerService.f1148D) {
                Log.d("GnssManager", "starting gnss sv status");
                return true;
            }
            return true;
        }
        Log.e("GnssManager", "error starting gnss sv status");
        return false;
    }

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public void unregisterWithService() {
        if (this.mGnssNative.stopSvStatusCollection()) {
            if (GnssManagerService.f1148D) {
                Log.d("GnssManager", "stopping gnss sv status");
                return;
            }
            return;
        }
        Log.e("GnssManager", "error stopping gnss sv status");
    }

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public void onRegistrationAdded(IBinder iBinder, GnssListenerMultiplexer<Void, IGnssStatusListener, Void>.GnssListenerRegistration gnssListenerRegistration) {
        this.mLogger.logLocationApiUsage(0, 3, gnssListenerRegistration.getIdentity().getPackageName(), gnssListenerRegistration.getIdentity().getAttributionTag(), null, null, true, false, null, gnssListenerRegistration.isForeground());
    }

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public void onRegistrationRemoved(IBinder iBinder, GnssListenerMultiplexer<Void, IGnssStatusListener, Void>.GnssListenerRegistration gnssListenerRegistration) {
        this.mLogger.logLocationApiUsage(1, 3, gnssListenerRegistration.getIdentity().getPackageName(), gnssListenerRegistration.getIdentity().getAttributionTag(), null, null, true, false, null, gnssListenerRegistration.isForeground());
    }

    @Override // com.android.server.location.gnss.hal.GnssNative.BaseCallbacks
    public void onHalRestarted() {
        resetService();
    }

    @Override // com.android.server.location.gnss.hal.GnssNative.StatusCallbacks
    public void onReportStatus(int i) {
        boolean z = true;
        if (i != 1) {
            z = (i == 2 || i == 4) ? false : this.mIsNavigating;
        }
        if (z != this.mIsNavigating) {
            this.mIsNavigating = z;
            if (z) {
                deliverToListeners(new ListenerExecutor.ListenerOperation() { // from class: com.android.server.location.gnss.GnssStatusProvider$$ExternalSyntheticLambda1
                    public final void operate(Object obj) {
                        ((IGnssStatusListener) obj).onGnssStarted();
                    }
                });
            } else {
                deliverToListeners(new ListenerExecutor.ListenerOperation() { // from class: com.android.server.location.gnss.GnssStatusProvider$$ExternalSyntheticLambda2
                    public final void operate(Object obj) {
                        ((IGnssStatusListener) obj).onGnssStopped();
                    }
                });
            }
        }
    }

    @Override // com.android.server.location.gnss.hal.GnssNative.StatusCallbacks
    public void onReportFirstFix(final int i) {
        deliverToListeners(new ListenerExecutor.ListenerOperation() { // from class: com.android.server.location.gnss.GnssStatusProvider$$ExternalSyntheticLambda0
            public final void operate(Object obj) {
                ((IGnssStatusListener) obj).onFirstFix(i);
            }
        });
    }

    @Override // com.android.server.location.gnss.hal.GnssNative.SvStatusCallbacks
    public void onReportSvStatus(final GnssStatus gnssStatus) {
        deliverToListeners(new Function() { // from class: com.android.server.location.gnss.GnssStatusProvider$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                ListenerExecutor.ListenerOperation lambda$onReportSvStatus$2;
                lambda$onReportSvStatus$2 = GnssStatusProvider.this.lambda$onReportSvStatus$2(gnssStatus, (GnssListenerMultiplexer.GnssListenerRegistration) obj);
                return lambda$onReportSvStatus$2;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ListenerExecutor.ListenerOperation lambda$onReportSvStatus$2(final GnssStatus gnssStatus, GnssListenerMultiplexer.GnssListenerRegistration gnssListenerRegistration) {
        if (this.mAppOpsHelper.noteOpNoThrow(1, gnssListenerRegistration.getIdentity())) {
            return new ListenerExecutor.ListenerOperation() { // from class: com.android.server.location.gnss.GnssStatusProvider$$ExternalSyntheticLambda4
                public final void operate(Object obj) {
                    ((IGnssStatusListener) obj).onSvStatusChanged(gnssStatus);
                }
            };
        }
        return null;
    }
}
