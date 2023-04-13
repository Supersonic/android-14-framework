package com.android.internal.listeners;

import android.p008os.RemoteException;
import android.util.ArrayMap;
import com.android.internal.listeners.ListenerTransport;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
/* loaded from: classes4.dex */
public abstract class ListenerTransportManager<TTransport extends ListenerTransport<?>> {
    private final Map<Object, WeakReference<TTransport>> mRegistrations;

    protected abstract void registerTransport(TTransport ttransport) throws RemoteException;

    protected abstract void unregisterTransport(TTransport ttransport) throws RemoteException;

    /* JADX INFO: Access modifiers changed from: protected */
    public ListenerTransportManager(boolean allowServerSideTransportRemoval) {
        if (allowServerSideTransportRemoval) {
            this.mRegistrations = new WeakHashMap();
        } else {
            this.mRegistrations = new ArrayMap();
        }
    }

    public final void addListener(Object key, TTransport transport) {
        TTransport oldTransport;
        try {
            synchronized (this.mRegistrations) {
                WeakReference<TTransport> oldTransportRef = this.mRegistrations.get(key);
                if (oldTransportRef != null) {
                    oldTransport = oldTransportRef.get();
                } else {
                    oldTransport = null;
                }
                if (oldTransport == null) {
                    registerTransport(transport);
                } else {
                    registerTransport(transport, oldTransport);
                    oldTransport.unregister();
                }
                this.mRegistrations.put(key, new WeakReference<>(transport));
            }
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public final void removeListener(Object key) {
        TTransport transport;
        try {
            synchronized (this.mRegistrations) {
                WeakReference<TTransport> transportRef = this.mRegistrations.remove(key);
                if (transportRef != null && (transport = transportRef.get()) != null) {
                    transport.unregister();
                    unregisterTransport(transport);
                }
            }
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    protected void registerTransport(TTransport transport, TTransport oldTransport) throws RemoteException {
        registerTransport(transport);
        try {
            unregisterTransport(oldTransport);
        } catch (RemoteException e) {
            try {
                unregisterTransport(transport);
            } catch (RemoteException suppressed) {
                e.addSuppressed(suppressed);
            }
            throw e;
        }
    }
}
