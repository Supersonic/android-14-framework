package com.android.server.location.gnss;

import android.location.GnssAntennaInfo;
import android.location.IGnssAntennaInfoListener;
import android.location.util.identity.CallerIdentity;
import android.os.Binder;
import android.os.IBinder;
import com.android.internal.listeners.ListenerExecutor;
import com.android.internal.util.ConcurrentUtils;
import com.android.server.FgThread;
import com.android.server.location.gnss.hal.GnssNative;
import com.android.server.location.listeners.BinderListenerRegistration;
import com.android.server.location.listeners.ListenerMultiplexer;
import com.android.server.location.listeners.ListenerRegistration;
import java.util.Collection;
import java.util.List;
/* loaded from: classes.dex */
public class GnssAntennaInfoProvider extends ListenerMultiplexer<IBinder, IGnssAntennaInfoListener, ListenerRegistration<IGnssAntennaInfoListener>, Void> implements GnssNative.BaseCallbacks, GnssNative.AntennaInfoCallbacks {
    public volatile List<GnssAntennaInfo> mAntennaInfos;
    public final GnssNative mGnssNative;

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public boolean isActive(ListenerRegistration<IGnssAntennaInfoListener> listenerRegistration) {
        return true;
    }

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public Void mergeRegistrations(Collection<ListenerRegistration<IGnssAntennaInfoListener>> collection) {
        return null;
    }

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public boolean registerWithService(Void r1, Collection<ListenerRegistration<IGnssAntennaInfoListener>> collection) {
        return true;
    }

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public void unregisterWithService() {
    }

    /* loaded from: classes.dex */
    public class AntennaInfoListenerRegistration extends BinderListenerRegistration<IBinder, IGnssAntennaInfoListener> {
        public final CallerIdentity mIdentity;

        @Override // com.android.server.location.listeners.BinderListenerRegistration
        public IBinder getBinderFromKey(IBinder iBinder) {
            return iBinder;
        }

        @Override // com.android.server.location.listeners.ListenerRegistration
        public String getTag() {
            return "GnssManager";
        }

        public AntennaInfoListenerRegistration(CallerIdentity callerIdentity, IGnssAntennaInfoListener iGnssAntennaInfoListener) {
            super(callerIdentity.isMyProcess() ? FgThread.getExecutor() : ConcurrentUtils.DIRECT_EXECUTOR, iGnssAntennaInfoListener);
            this.mIdentity = callerIdentity;
        }

        @Override // com.android.server.location.listeners.RemovableListenerRegistration
        public GnssAntennaInfoProvider getOwner() {
            return GnssAntennaInfoProvider.this;
        }

        @Override // com.android.server.location.listeners.ListenerRegistration
        public String toString() {
            return this.mIdentity.toString();
        }
    }

    public GnssAntennaInfoProvider(GnssNative gnssNative) {
        this.mGnssNative = gnssNative;
        gnssNative.addBaseCallbacks(this);
        gnssNative.addAntennaInfoCallbacks(this);
    }

    public List<GnssAntennaInfo> getAntennaInfos() {
        return this.mAntennaInfos;
    }

    public boolean isSupported() {
        return this.mGnssNative.isAntennaInfoSupported();
    }

    public void addListener(CallerIdentity callerIdentity, IGnssAntennaInfoListener iGnssAntennaInfoListener) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            putRegistration(iGnssAntennaInfoListener.asBinder(), new AntennaInfoListenerRegistration(callerIdentity, iGnssAntennaInfoListener));
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void removeListener(IGnssAntennaInfoListener iGnssAntennaInfoListener) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            removeRegistration((GnssAntennaInfoProvider) iGnssAntennaInfoListener.asBinder());
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @Override // com.android.server.location.gnss.hal.GnssNative.BaseCallbacks
    public void onHalStarted() {
        this.mGnssNative.startAntennaInfoListening();
    }

    @Override // com.android.server.location.gnss.hal.GnssNative.BaseCallbacks
    public void onHalRestarted() {
        this.mGnssNative.startAntennaInfoListening();
    }

    @Override // com.android.server.location.gnss.hal.GnssNative.AntennaInfoCallbacks
    public void onReportAntennaInfo(final List<GnssAntennaInfo> list) {
        if (list.equals(this.mAntennaInfos)) {
            return;
        }
        this.mAntennaInfos = list;
        deliverToListeners(new ListenerExecutor.ListenerOperation() { // from class: com.android.server.location.gnss.GnssAntennaInfoProvider$$ExternalSyntheticLambda0
            public final void operate(Object obj) {
                ((IGnssAntennaInfoListener) obj).onGnssAntennaInfoChanged(list);
            }
        });
    }
}
