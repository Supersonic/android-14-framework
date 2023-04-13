package com.android.location.provider;

import android.content.Context;
import android.location.ILocationManager;
import android.location.Location;
import android.location.provider.ILocationProvider;
import android.location.provider.ILocationProviderManager;
import android.location.provider.ProviderProperties;
import android.location.provider.ProviderRequest;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.WorkSource;
import android.util.Log;
import com.android.location.provider.LocationProviderBase;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
@Deprecated
/* loaded from: classes.dex */
public abstract class LocationProviderBase {
    @Deprecated
    public static final String EXTRA_NO_GPS_LOCATION = "noGPSLocation";
    public static final String FUSED_PROVIDER = "fused";
    volatile boolean mAllowed;
    final String mAttributionTag;
    final IBinder mBinder;
    @Deprecated
    protected final ILocationManager mLocationManager;
    volatile ILocationProviderManager mManager;
    volatile ProviderProperties mProperties;
    final String mTag;

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public interface OnFlushCompleteCallback {
        void onFlushComplete();
    }

    protected abstract void onSetRequest(ProviderRequestUnbundled providerRequestUnbundled, WorkSource workSource);

    @Deprecated
    public LocationProviderBase(String tag, ProviderPropertiesUnbundled properties) {
        this(null, tag, properties);
    }

    public LocationProviderBase(Context context, String tag, ProviderPropertiesUnbundled properties) {
        this.mTag = tag;
        this.mAttributionTag = context != null ? context.getAttributionTag() : null;
        this.mBinder = new Service();
        this.mLocationManager = ILocationManager.Stub.asInterface(ServiceManager.getService("location"));
        this.mManager = null;
        this.mProperties = properties.getProviderProperties();
        this.mAllowed = true;
    }

    public IBinder getBinder() {
        return this.mBinder;
    }

    @Deprecated
    public void setEnabled(boolean enabled) {
        setAllowed(enabled);
    }

    public void setAllowed(boolean allowed) {
        synchronized (this.mBinder) {
            if (this.mAllowed == allowed) {
                return;
            }
            this.mAllowed = allowed;
            ILocationProviderManager manager = this.mManager;
            if (manager != null) {
                try {
                    manager.onSetAllowed(this.mAllowed);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                } catch (RuntimeException e2) {
                    Log.w(this.mTag, e2);
                }
            }
        }
    }

    public void setProperties(ProviderPropertiesUnbundled properties) {
        synchronized (this.mBinder) {
            this.mProperties = properties.getProviderProperties();
        }
        ILocationProviderManager manager = this.mManager;
        if (manager != null) {
            try {
                manager.onSetProperties(this.mProperties);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            } catch (RuntimeException e2) {
                Log.w(this.mTag, e2);
            }
        }
    }

    @Deprecated
    public void setAdditionalProviderPackages(List<String> packageNames) {
    }

    @Deprecated
    public boolean isEnabled() {
        return isAllowed();
    }

    public boolean isAllowed() {
        return this.mAllowed;
    }

    public void reportLocation(Location location) {
        ILocationProviderManager manager = this.mManager;
        if (manager != null) {
            try {
                manager.onReportLocation(stripExtras(location));
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            } catch (RuntimeException e2) {
                Log.w(this.mTag, e2);
            }
        }
    }

    public void reportLocations(List<Location> locations) {
        ILocationProviderManager manager = this.mManager;
        if (manager != null) {
            try {
                manager.onReportLocations(stripExtras(locations));
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            } catch (RuntimeException e2) {
                Log.w(this.mTag, e2);
            }
        }
    }

    protected void onInit() {
        onEnable();
    }

    @Deprecated
    protected void onEnable() {
    }

    @Deprecated
    protected void onDisable() {
    }

    protected void onFlush(OnFlushCompleteCallback callback) {
        callback.onFlushComplete();
    }

    @Deprecated
    protected void onDump(FileDescriptor fd, PrintWriter pw, String[] args) {
    }

    @Deprecated
    protected int onGetStatus(Bundle extras) {
        return 2;
    }

    @Deprecated
    protected long onGetStatusUpdateTime() {
        return 0L;
    }

    protected boolean onSendExtraCommand(String command, Bundle extras) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class Service extends ILocationProvider.Stub {
        Service() {
        }

        public void setLocationProviderManager(ILocationProviderManager manager) {
            synchronized (LocationProviderBase.this.mBinder) {
                try {
                    manager.onInitialize(LocationProviderBase.this.mAllowed, LocationProviderBase.this.mProperties, LocationProviderBase.this.mAttributionTag);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                } catch (RuntimeException e2) {
                    Log.w(LocationProviderBase.this.mTag, e2);
                }
                LocationProviderBase.this.mManager = manager;
            }
            LocationProviderBase.this.onInit();
        }

        public void setRequest(ProviderRequest request) {
            LocationProviderBase.this.onSetRequest(new ProviderRequestUnbundled(request), request.getWorkSource());
        }

        public void flush() {
            LocationProviderBase.this.onFlush(new OnFlushCompleteCallback() { // from class: com.android.location.provider.LocationProviderBase$Service$$ExternalSyntheticLambda0
                @Override // com.android.location.provider.LocationProviderBase.OnFlushCompleteCallback
                public final void onFlushComplete() {
                    LocationProviderBase.Service.this.onFlushComplete();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onFlushComplete() {
            ILocationProviderManager manager = LocationProviderBase.this.mManager;
            if (manager != null) {
                try {
                    manager.onFlushComplete();
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                } catch (RuntimeException e2) {
                    Log.w(LocationProviderBase.this.mTag, e2);
                }
            }
        }

        public void sendExtraCommand(String command, Bundle extras) {
            LocationProviderBase.this.onSendExtraCommand(command, extras);
        }
    }

    private static Location stripExtras(Location location) {
        Bundle extras = location.getExtras();
        if (extras != null && (extras.containsKey(EXTRA_NO_GPS_LOCATION) || extras.containsKey("indoorProbability") || extras.containsKey("coarseLocation"))) {
            location = new Location(location);
            Bundle extras2 = location.getExtras();
            extras2.remove(EXTRA_NO_GPS_LOCATION);
            extras2.remove("indoorProbability");
            extras2.remove("coarseLocation");
            if (extras2.isEmpty()) {
                location.setExtras(null);
            }
        }
        return location;
    }

    private static List<Location> stripExtras(List<Location> locations) {
        List<Location> mapped = locations;
        int size = locations.size();
        int i = 0;
        for (Location location : locations) {
            Location newLocation = stripExtras(location);
            if (mapped != locations) {
                mapped.add(newLocation);
            } else if (newLocation != location) {
                mapped = new ArrayList<>(size);
                int j = 0;
                for (Location copiedLocation : locations) {
                    if (j >= i) {
                        break;
                    }
                    mapped.add(copiedLocation);
                    j++;
                }
                mapped.add(newLocation);
            }
            i++;
        }
        return mapped;
    }
}
