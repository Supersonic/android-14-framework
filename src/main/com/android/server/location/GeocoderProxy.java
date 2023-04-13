package com.android.server.location;

import android.content.Context;
import android.location.GeocoderParams;
import android.location.IGeocodeListener;
import android.location.IGeocodeProvider;
import android.os.IBinder;
import android.os.RemoteException;
import com.android.server.servicewatcher.CurrentUserServiceSupplier;
import com.android.server.servicewatcher.ServiceWatcher;
import java.util.Collections;
/* loaded from: classes.dex */
public class GeocoderProxy {
    public final ServiceWatcher mServiceWatcher;

    public static GeocoderProxy createAndRegister(Context context) {
        GeocoderProxy geocoderProxy = new GeocoderProxy(context);
        if (geocoderProxy.register()) {
            return geocoderProxy;
        }
        return null;
    }

    public GeocoderProxy(Context context) {
        this.mServiceWatcher = ServiceWatcher.create(context, "GeocoderProxy", CurrentUserServiceSupplier.createFromConfig(context, "com.android.location.service.GeocodeProvider", 17891650, 17039939), null);
    }

    public final boolean register() {
        boolean checkServiceResolves = this.mServiceWatcher.checkServiceResolves();
        if (checkServiceResolves) {
            this.mServiceWatcher.register();
        }
        return checkServiceResolves;
    }

    public void getFromLocation(final double d, final double d2, final int i, final GeocoderParams geocoderParams, final IGeocodeListener iGeocodeListener) {
        this.mServiceWatcher.runOnBinder(new ServiceWatcher.BinderOperation() { // from class: com.android.server.location.GeocoderProxy.1
            @Override // com.android.server.servicewatcher.ServiceWatcher.BinderOperation
            public void run(IBinder iBinder) throws RemoteException {
                IGeocodeProvider.Stub.asInterface(iBinder).getFromLocation(d, d2, i, geocoderParams, iGeocodeListener);
            }

            @Override // com.android.server.servicewatcher.ServiceWatcher.BinderOperation
            public void onError(Throwable th) {
                try {
                    iGeocodeListener.onResults(th.toString(), Collections.emptyList());
                } catch (RemoteException unused) {
                }
            }
        });
    }

    public void getFromLocationName(final String str, final double d, final double d2, final double d3, final double d4, final int i, final GeocoderParams geocoderParams, final IGeocodeListener iGeocodeListener) {
        this.mServiceWatcher.runOnBinder(new ServiceWatcher.BinderOperation() { // from class: com.android.server.location.GeocoderProxy.2
            @Override // com.android.server.servicewatcher.ServiceWatcher.BinderOperation
            public void run(IBinder iBinder) throws RemoteException {
                IGeocodeProvider.Stub.asInterface(iBinder).getFromLocationName(str, d, d2, d3, d4, i, geocoderParams, iGeocodeListener);
            }

            @Override // com.android.server.servicewatcher.ServiceWatcher.BinderOperation
            public void onError(Throwable th) {
                try {
                    iGeocodeListener.onResults(th.toString(), Collections.emptyList());
                } catch (RemoteException unused) {
                }
            }
        });
    }
}
