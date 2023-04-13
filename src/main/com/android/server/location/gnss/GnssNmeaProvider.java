package com.android.server.location.gnss;

import android.location.IGnssNmeaListener;
import android.location.util.identity.CallerIdentity;
import android.util.Log;
import com.android.internal.listeners.ListenerExecutor;
import com.android.server.location.gnss.GnssNmeaProvider;
import com.android.server.location.gnss.hal.GnssNative;
import com.android.server.location.injector.AppOpsHelper;
import com.android.server.location.injector.Injector;
import java.util.Collection;
import java.util.function.Function;
/* loaded from: classes.dex */
public class GnssNmeaProvider extends GnssListenerMultiplexer<Void, IGnssNmeaListener, Void> implements GnssNative.BaseCallbacks, GnssNative.NmeaCallbacks {
    public final AppOpsHelper mAppOpsHelper;
    public final GnssNative mGnssNative;
    public final byte[] mNmeaBuffer;

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public /* bridge */ /* synthetic */ boolean registerWithService(Object obj, Collection collection) {
        return registerWithService((Void) obj, (Collection<GnssListenerMultiplexer<Void, IGnssNmeaListener, Void>.GnssListenerRegistration>) collection);
    }

    public GnssNmeaProvider(Injector injector, GnssNative gnssNative) {
        super(injector);
        this.mNmeaBuffer = new byte[120];
        this.mAppOpsHelper = injector.getAppOpsHelper();
        this.mGnssNative = gnssNative;
        gnssNative.addBaseCallbacks(this);
        gnssNative.addNmeaCallbacks(this);
    }

    public void addListener(CallerIdentity callerIdentity, IGnssNmeaListener iGnssNmeaListener) {
        super.addListener(callerIdentity, (CallerIdentity) iGnssNmeaListener);
    }

    public boolean registerWithService(Void r1, Collection<GnssListenerMultiplexer<Void, IGnssNmeaListener, Void>.GnssListenerRegistration> collection) {
        if (this.mGnssNative.startNmeaMessageCollection()) {
            if (GnssManagerService.f1148D) {
                Log.d("GnssManager", "starting gnss nmea messages collection");
                return true;
            }
            return true;
        }
        Log.e("GnssManager", "error starting gnss nmea messages collection");
        return false;
    }

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public void unregisterWithService() {
        if (this.mGnssNative.stopNmeaMessageCollection()) {
            if (GnssManagerService.f1148D) {
                Log.d("GnssManager", "stopping gnss nmea messages collection");
                return;
            }
            return;
        }
        Log.e("GnssManager", "error stopping gnss nmea messages collection");
    }

    @Override // com.android.server.location.gnss.hal.GnssNative.BaseCallbacks
    public void onHalRestarted() {
        resetService();
    }

    @Override // com.android.server.location.gnss.hal.GnssNative.NmeaCallbacks
    public void onReportNmea(long j) {
        deliverToListeners(new C10811(j));
    }

    /* renamed from: com.android.server.location.gnss.GnssNmeaProvider$1 */
    /* loaded from: classes.dex */
    public class C10811 implements Function<GnssListenerMultiplexer<Void, IGnssNmeaListener, Void>.GnssListenerRegistration, ListenerExecutor.ListenerOperation<IGnssNmeaListener>> {
        public String mNmea;
        public final /* synthetic */ long val$timestamp;

        public C10811(long j) {
            this.val$timestamp = j;
        }

        @Override // java.util.function.Function
        public ListenerExecutor.ListenerOperation<IGnssNmeaListener> apply(GnssListenerMultiplexer<Void, IGnssNmeaListener, Void>.GnssListenerRegistration gnssListenerRegistration) {
            if (GnssNmeaProvider.this.mAppOpsHelper.noteOpNoThrow(1, gnssListenerRegistration.getIdentity())) {
                if (this.mNmea == null) {
                    this.mNmea = new String(GnssNmeaProvider.this.mNmeaBuffer, 0, GnssNmeaProvider.this.mGnssNative.readNmea(GnssNmeaProvider.this.mNmeaBuffer, GnssNmeaProvider.this.mNmeaBuffer.length));
                }
                final long j = this.val$timestamp;
                return new ListenerExecutor.ListenerOperation() { // from class: com.android.server.location.gnss.GnssNmeaProvider$1$$ExternalSyntheticLambda0
                    public final void operate(Object obj) {
                        GnssNmeaProvider.C10811.this.lambda$apply$0(j, (IGnssNmeaListener) obj);
                    }
                };
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$apply$0(long j, IGnssNmeaListener iGnssNmeaListener) throws Exception {
            iGnssNmeaListener.onNmeaReceived(j, this.mNmea);
        }
    }
}
