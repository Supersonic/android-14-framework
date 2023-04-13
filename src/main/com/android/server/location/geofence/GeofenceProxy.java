package com.android.server.location.geofence;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.location.GeofenceHardwareService;
import android.hardware.location.IGeofenceHardware;
import android.location.IGeofenceProvider;
import android.location.IGpsGeofenceHardware;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import com.android.server.servicewatcher.CurrentUserServiceSupplier;
import com.android.server.servicewatcher.ServiceWatcher;
import java.util.Objects;
/* loaded from: classes.dex */
public final class GeofenceProxy implements ServiceWatcher.ServiceListener<CurrentUserServiceSupplier.BoundServiceInfo> {
    public volatile IGeofenceHardware mGeofenceHardware;
    public final IGpsGeofenceHardware mGpsGeofenceHardware;
    public final ServiceWatcher mServiceWatcher;

    @Override // com.android.server.servicewatcher.ServiceWatcher.ServiceListener
    public void onUnbind() {
    }

    public static GeofenceProxy createAndBind(Context context, IGpsGeofenceHardware iGpsGeofenceHardware) {
        GeofenceProxy geofenceProxy = new GeofenceProxy(context, iGpsGeofenceHardware);
        if (geofenceProxy.register(context)) {
            return geofenceProxy;
        }
        return null;
    }

    public GeofenceProxy(Context context, IGpsGeofenceHardware iGpsGeofenceHardware) {
        Objects.requireNonNull(iGpsGeofenceHardware);
        this.mGpsGeofenceHardware = iGpsGeofenceHardware;
        this.mServiceWatcher = ServiceWatcher.create(context, "GeofenceProxy", CurrentUserServiceSupplier.createFromConfig(context, "com.android.location.service.GeofenceProvider", 17891651, 17039940), this);
        this.mGeofenceHardware = null;
    }

    public void updateGeofenceHardware(IBinder iBinder) throws RemoteException {
        IGeofenceProvider.Stub.asInterface(iBinder).setGeofenceHardware(this.mGeofenceHardware);
    }

    public final boolean register(Context context) {
        boolean checkServiceResolves = this.mServiceWatcher.checkServiceResolves();
        if (checkServiceResolves) {
            this.mServiceWatcher.register();
            context.bindServiceAsUser(new Intent(context, GeofenceHardwareService.class), new GeofenceProxyServiceConnection(), 1, UserHandle.SYSTEM);
        }
        return checkServiceResolves;
    }

    @Override // com.android.server.servicewatcher.ServiceWatcher.ServiceListener
    public void onBind(IBinder iBinder, CurrentUserServiceSupplier.BoundServiceInfo boundServiceInfo) throws RemoteException {
        updateGeofenceHardware(iBinder);
    }

    /* loaded from: classes.dex */
    public class GeofenceProxyServiceConnection implements ServiceConnection {
        public GeofenceProxyServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IGeofenceHardware asInterface = IGeofenceHardware.Stub.asInterface(iBinder);
            try {
                asInterface.setGpsGeofenceHardware(GeofenceProxy.this.mGpsGeofenceHardware);
                GeofenceProxy.this.mGeofenceHardware = asInterface;
                GeofenceProxy geofenceProxy = GeofenceProxy.this;
                geofenceProxy.mServiceWatcher.runOnBinder(new C1062xd661182a(geofenceProxy));
            } catch (RemoteException e) {
                Log.w("GeofenceProxy", "unable to initialize geofence hardware", e);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            GeofenceProxy.this.mGeofenceHardware = null;
            GeofenceProxy geofenceProxy = GeofenceProxy.this;
            geofenceProxy.mServiceWatcher.runOnBinder(new C1062xd661182a(geofenceProxy));
        }
    }
}
