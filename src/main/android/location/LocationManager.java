package android.location;

import android.Manifest;
import android.annotation.SystemApi;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.app.PropertyInvalidatedCache;
import android.compat.Compatibility;
import android.content.Context;
import android.location.GnssAntennaInfo;
import android.location.GnssMeasurementRequest;
import android.location.GnssMeasurementsEvent;
import android.location.GnssNavigationMessage;
import android.location.GnssStatus;
import android.location.GpsMeasurementsEvent;
import android.location.GpsNavigationMessageEvent;
import android.location.GpsStatus;
import android.location.IGnssAntennaInfoListener;
import android.location.IGnssMeasurementsListener;
import android.location.IGnssNavigationMessageListener;
import android.location.IGnssNmeaListener;
import android.location.IGnssStatusListener;
import android.location.ILocationCallback;
import android.location.ILocationListener;
import android.location.ILocationManager;
import android.location.LastLocationRequest;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.location.provider.IProviderRequestListener;
import android.location.provider.ProviderProperties;
import android.location.provider.ProviderRequest;
import android.p008os.Bundle;
import android.p008os.CancellationSignal;
import android.p008os.Handler;
import android.p008os.HandlerExecutor;
import android.p008os.ICancellationSignal;
import android.p008os.IRemoteCallback;
import android.p008os.Looper;
import android.p008os.PackageTagsList;
import android.p008os.Process;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.p008os.UserHandle;
import com.android.internal.listeners.ListenerExecutor;
import com.android.internal.listeners.ListenerTransport;
import com.android.internal.listeners.ListenerTransportManager;
import com.android.internal.util.ConcurrentUtils;
import com.android.internal.util.Preconditions;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;
/* loaded from: classes2.dex */
public class LocationManager {
    @SystemApi
    public static final String ACTION_ADAS_GNSS_ENABLED_CHANGED = "android.location.action.ADAS_GNSS_ENABLED_CHANGED";
    public static final String ACTION_GNSS_CAPABILITIES_CHANGED = "android.location.action.GNSS_CAPABILITIES_CHANGED";
    public static final long BLOCK_GPS_STATUS_USAGE = 144027538;
    public static final long BLOCK_IMMUTABLE_PENDING_INTENTS = 171317480;
    public static final long BLOCK_INCOMPLETE_LOCATIONS = 148964793;
    public static final long BLOCK_PENDING_INTENT_SYSTEM_API_USAGE = 169887240;
    public static final long BLOCK_UNTARGETED_PENDING_INTENTS = 148963590;
    private static final String CACHE_KEY_LOCATION_ENABLED_PROPERTY = "cache_key.location_enabled";
    public static final long DELIVER_HISTORICAL_LOCATIONS = 73144566;
    @SystemApi
    public static final String EXTRA_ADAS_GNSS_ENABLED = "android.location.extra.ADAS_GNSS_ENABLED";
    public static final String EXTRA_GNSS_CAPABILITIES = "android.location.extra.GNSS_CAPABILITIES";
    public static final String EXTRA_LOCATION_ENABLED = "android.location.extra.LOCATION_ENABLED";
    public static final String EXTRA_PROVIDER_ENABLED = "android.location.extra.PROVIDER_ENABLED";
    public static final String EXTRA_PROVIDER_NAME = "android.location.extra.PROVIDER_NAME";
    public static final String FUSED_PROVIDER = "fused";
    public static final long GET_PROVIDER_SECURITY_EXCEPTIONS = 150935354;
    @SystemApi
    public static final String GPS_HARDWARE_PROVIDER = "gps_hardware";
    public static final String GPS_PROVIDER = "gps";
    @Deprecated
    public static final String HIGH_POWER_REQUEST_CHANGE_ACTION = "android.location.HIGH_POWER_REQUEST_CHANGE";
    public static final String KEY_FLUSH_COMPLETE = "flushComplete";
    public static final String KEY_LOCATIONS = "locations";
    public static final String KEY_LOCATION_CHANGED = "location";
    public static final String KEY_PROVIDER_ENABLED = "providerEnabled";
    public static final String KEY_PROXIMITY_ENTERING = "entering";
    @Deprecated
    public static final String KEY_STATUS_CHANGED = "status";
    private static final long MAX_SINGLE_LOCATION_TIMEOUT_MS = 30000;
    public static final String METADATA_SETTINGS_FOOTER_STRING = "com.android.settings.location.FOOTER_STRING";
    public static final String MODE_CHANGED_ACTION = "android.location.MODE_CHANGED";
    public static final String NETWORK_PROVIDER = "network";
    public static final String PASSIVE_PROVIDER = "passive";
    public static final String PROVIDERS_CHANGED_ACTION = "android.location.PROVIDERS_CHANGED";
    public static final String SETTINGS_FOOTER_DISPLAYED_ACTION = "com.android.settings.location.DISPLAYED_FOOTER";
    private static volatile LocationEnabledCache sLocationEnabledCache = new LocationEnabledCache(4);
    private static final WeakHashMap<LocationListener, WeakReference<LocationListenerTransport>> sLocationListeners = new WeakHashMap<>();
    final Context mContext;
    final ILocationManager mService;

    static ILocationManager getService() throws RemoteException {
        try {
            return ILocationManager.Stub.asInterface(ServiceManager.getServiceOrThrow("location"));
        } catch (ServiceManager.ServiceNotFoundException e) {
            throw new RemoteException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class GnssLazyLoader {
        static final GnssStatusTransportManager sGnssStatusListeners = new GnssStatusTransportManager();
        static final GnssNmeaTransportManager sGnssNmeaListeners = new GnssNmeaTransportManager();
        static final GnssMeasurementsTransportManager sGnssMeasurementsListeners = new GnssMeasurementsTransportManager();
        static final GnssAntennaTransportManager sGnssAntennaInfoListeners = new GnssAntennaTransportManager();
        static final GnssNavigationTransportManager sGnssNavigationListeners = new GnssNavigationTransportManager();

        private GnssLazyLoader() {
        }
    }

    /* loaded from: classes2.dex */
    private static class ProviderRequestLazyLoader {
        static final ProviderRequestTransportManager sProviderRequestListeners = new ProviderRequestTransportManager();

        private ProviderRequestLazyLoader() {
        }
    }

    public LocationManager(Context context, ILocationManager service) {
        this.mContext = (Context) Objects.requireNonNull(context);
        this.mService = (ILocationManager) Objects.requireNonNull(service);
    }

    public String[] getBackgroundThrottlingWhitelist() {
        try {
            return this.mService.getBackgroundThrottlingWhitelist();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public String[] getIgnoreSettingsWhitelist() {
        return new String[0];
    }

    public PackageTagsList getIgnoreSettingsAllowlist() {
        try {
            return this.mService.getIgnoreSettingsAllowlist();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public PackageTagsList getAdasAllowlist() {
        try {
            return this.mService.getAdasAllowlist();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public String getExtraLocationControllerPackage() {
        try {
            return this.mService.getExtraLocationControllerPackage();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void setExtraLocationControllerPackage(String packageName) {
        try {
            this.mService.setExtraLocationControllerPackage(packageName);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void setExtraLocationControllerPackageEnabled(boolean enabled) {
        try {
            this.mService.setExtraLocationControllerPackageEnabled(enabled);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean isExtraLocationControllerPackageEnabled() {
        try {
            return this.mService.isExtraLocationControllerPackageEnabled();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    @Deprecated
    public void setLocationControllerExtraPackage(String packageName) {
        try {
            this.mService.setExtraLocationControllerPackage(packageName);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    @Deprecated
    public void setLocationControllerExtraPackageEnabled(boolean enabled) {
        try {
            this.mService.setExtraLocationControllerPackageEnabled(enabled);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isLocationEnabled() {
        return isLocationEnabledForUser(this.mContext.getUser());
    }

    @SystemApi
    public boolean isLocationEnabledForUser(UserHandle userHandle) {
        PropertyInvalidatedCache<Integer, Boolean> cache;
        if (userHandle.getIdentifier() >= 0 && (cache = sLocationEnabledCache) != null) {
            return cache.query(Integer.valueOf(userHandle.getIdentifier())).booleanValue();
        }
        try {
            return this.mService.isLocationEnabledForUser(userHandle.getIdentifier());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void setLocationEnabledForUser(boolean enabled, UserHandle userHandle) {
        try {
            this.mService.setLocationEnabledForUser(enabled, userHandle.getIdentifier());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public boolean isAdasGnssLocationEnabled() {
        try {
            return this.mService.isAdasGnssLocationEnabledForUser(this.mContext.getUser().getIdentifier());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void setAdasGnssLocationEnabled(boolean enabled) {
        try {
            this.mService.setAdasGnssLocationEnabledForUser(enabled, this.mContext.getUser().getIdentifier());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isProviderEnabled(String provider) {
        return isProviderEnabledForUser(provider, Process.myUserHandle());
    }

    @SystemApi
    public boolean isProviderEnabledForUser(String provider, UserHandle userHandle) {
        Preconditions.checkArgument(provider != null, "invalid null provider");
        try {
            return this.mService.isProviderEnabledForUser(provider, userHandle.getIdentifier());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    @Deprecated
    public boolean setProviderEnabledForUser(String provider, boolean enabled, UserHandle userHandle) {
        Preconditions.checkArgument(provider != null, "invalid null provider");
        return false;
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public void setAutomotiveGnssSuspended(boolean suspended) {
        try {
            this.mService.setAutomotiveGnssSuspended(suspended);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public boolean isAutomotiveGnssSuspended() {
        try {
            return this.mService.isAutomotiveGnssSuspended();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public Location getLastLocation() {
        return getLastKnownLocation(FUSED_PROVIDER);
    }

    public Location getLastKnownLocation(String provider) {
        return getLastKnownLocation(provider, new LastLocationRequest.Builder().build());
    }

    @SystemApi
    public Location getLastKnownLocation(String provider, LastLocationRequest lastLocationRequest) {
        Preconditions.checkArgument(provider != null, "invalid null provider");
        Preconditions.checkArgument(lastLocationRequest != null, "invalid null last location request");
        try {
            return this.mService.getLastLocation(provider, lastLocationRequest, this.mContext.getPackageName(), this.mContext.getAttributionTag());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void getCurrentLocation(String provider, CancellationSignal cancellationSignal, Executor executor, Consumer<Location> consumer) {
        getCurrentLocation(provider, new LocationRequest.Builder(0L).build(), cancellationSignal, executor, consumer);
    }

    @SystemApi
    @Deprecated
    public void getCurrentLocation(LocationRequest locationRequest, CancellationSignal cancellationSignal, Executor executor, Consumer<Location> consumer) {
        Preconditions.checkArgument(locationRequest.getProvider() != null);
        getCurrentLocation(locationRequest.getProvider(), locationRequest, cancellationSignal, executor, consumer);
    }

    public void getCurrentLocation(String provider, LocationRequest locationRequest, CancellationSignal cancellationSignal, Executor executor, Consumer<Location> consumer) {
        Preconditions.checkArgument(provider != null, "invalid null provider");
        Preconditions.checkArgument(locationRequest != null, "invalid null location request");
        if (cancellationSignal != null) {
            cancellationSignal.throwIfCanceled();
        }
        GetCurrentLocationTransport transport = new GetCurrentLocationTransport(executor, consumer, cancellationSignal);
        try {
            ICancellationSignal cancelRemote = this.mService.getCurrentLocation(provider, locationRequest, transport, this.mContext.getPackageName(), this.mContext.getAttributionTag(), AppOpsManager.toReceiverId(consumer));
            if (cancellationSignal != null) {
                cancellationSignal.setRemote(cancelRemote);
            }
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public void requestSingleUpdate(String provider, LocationListener listener, Looper looper) {
        Preconditions.checkArgument(provider != null, "invalid null provider");
        Handler handler = looper == null ? new Handler() : new Handler(looper);
        requestLocationUpdates(provider, new LocationRequest.Builder(0L).setMaxUpdates(1).setDurationMillis(30000L).build(), new HandlerExecutor(handler), listener);
    }

    @Deprecated
    public void requestSingleUpdate(Criteria criteria, LocationListener listener, Looper looper) {
        Preconditions.checkArgument(criteria != null, "invalid null criteria");
        Handler handler = looper == null ? new Handler() : new Handler(looper);
        requestLocationUpdates(FUSED_PROVIDER, new LocationRequest.Builder(0L).setQuality(criteria).setMaxUpdates(1).setDurationMillis(30000L).build(), new HandlerExecutor(handler), listener);
    }

    @Deprecated
    public void requestSingleUpdate(String provider, PendingIntent pendingIntent) {
        Preconditions.checkArgument(provider != null, "invalid null provider");
        requestLocationUpdates(provider, new LocationRequest.Builder(0L).setMaxUpdates(1).setDurationMillis(30000L).build(), pendingIntent);
    }

    @Deprecated
    public void requestSingleUpdate(Criteria criteria, PendingIntent pendingIntent) {
        Preconditions.checkArgument(criteria != null, "invalid null criteria");
        requestLocationUpdates(FUSED_PROVIDER, new LocationRequest.Builder(0L).setQuality(criteria).setMaxUpdates(1).setDurationMillis(30000L).build(), pendingIntent);
    }

    public void requestLocationUpdates(String provider, long minTimeMs, float minDistanceM, LocationListener listener) {
        requestLocationUpdates(provider, minTimeMs, minDistanceM, listener, (Looper) null);
    }

    public void requestLocationUpdates(String provider, long minTimeMs, float minDistanceM, LocationListener listener, Looper looper) {
        Handler handler = looper == null ? new Handler() : new Handler(looper);
        requestLocationUpdates(provider, minTimeMs, minDistanceM, new HandlerExecutor(handler), listener);
    }

    public void requestLocationUpdates(String provider, long minTimeMs, float minDistanceM, Executor executor, LocationListener listener) {
        Preconditions.checkArgument(provider != null, "invalid null provider");
        requestLocationUpdates(provider, LocationRequest.createFromDeprecatedProvider(provider, minTimeMs, minDistanceM, false), executor, listener);
    }

    @Deprecated
    public void requestLocationUpdates(long minTimeMs, float minDistanceM, Criteria criteria, LocationListener listener, Looper looper) {
        Handler handler = looper == null ? new Handler() : new Handler(looper);
        requestLocationUpdates(minTimeMs, minDistanceM, criteria, new HandlerExecutor(handler), listener);
    }

    @Deprecated
    public void requestLocationUpdates(long minTimeMs, float minDistanceM, Criteria criteria, Executor executor, LocationListener listener) {
        Preconditions.checkArgument(criteria != null, "invalid null criteria");
        requestLocationUpdates(FUSED_PROVIDER, LocationRequest.createFromDeprecatedCriteria(criteria, minTimeMs, minDistanceM, false), executor, listener);
    }

    public void requestLocationUpdates(String provider, long minTimeMs, float minDistanceM, PendingIntent pendingIntent) {
        Preconditions.checkArgument(provider != null, "invalid null provider");
        requestLocationUpdates(provider, LocationRequest.createFromDeprecatedProvider(provider, minTimeMs, minDistanceM, false), pendingIntent);
    }

    @Deprecated
    public void requestLocationUpdates(long minTimeMs, float minDistanceM, Criteria criteria, PendingIntent pendingIntent) {
        Preconditions.checkArgument(criteria != null, "invalid null criteria");
        requestLocationUpdates(FUSED_PROVIDER, LocationRequest.createFromDeprecatedCriteria(criteria, minTimeMs, minDistanceM, false), pendingIntent);
    }

    @SystemApi
    @Deprecated
    public void requestLocationUpdates(LocationRequest locationRequest, LocationListener listener, Looper looper) {
        Handler handler = looper == null ? new Handler() : new Handler(looper);
        requestLocationUpdates(locationRequest, new HandlerExecutor(handler), listener);
    }

    @SystemApi
    @Deprecated
    public void requestLocationUpdates(LocationRequest locationRequest, Executor executor, LocationListener listener) {
        if (locationRequest == null) {
            locationRequest = LocationRequest.create();
        }
        Preconditions.checkArgument(locationRequest.getProvider() != null);
        requestLocationUpdates(locationRequest.getProvider(), locationRequest, executor, listener);
    }

    @SystemApi
    @Deprecated
    public void requestLocationUpdates(LocationRequest locationRequest, PendingIntent pendingIntent) {
        if (locationRequest == null) {
            locationRequest = LocationRequest.create();
        }
        Preconditions.checkArgument(locationRequest.getProvider() != null);
        requestLocationUpdates(locationRequest.getProvider(), locationRequest, pendingIntent);
    }

    public void requestLocationUpdates(String provider, LocationRequest locationRequest, Executor executor, LocationListener listener) {
        Preconditions.checkArgument(provider != null, "invalid null provider");
        Preconditions.checkArgument(locationRequest != null, "invalid null location request");
        try {
            WeakHashMap<LocationListener, WeakReference<LocationListenerTransport>> weakHashMap = sLocationListeners;
            synchronized (weakHashMap) {
                WeakReference<LocationListenerTransport> reference = weakHashMap.get(listener);
                LocationListenerTransport transport = reference != null ? reference.get() : null;
                if (transport == null) {
                    transport = new LocationListenerTransport(listener, executor);
                } else {
                    Preconditions.checkState(transport.isRegistered());
                    transport.setExecutor(executor);
                }
                this.mService.registerLocationListener(provider, locationRequest, transport, this.mContext.getPackageName(), this.mContext.getAttributionTag(), AppOpsManager.toReceiverId(listener));
                weakHashMap.put(listener, new WeakReference<>(transport));
            }
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void requestLocationUpdates(String provider, LocationRequest locationRequest, PendingIntent pendingIntent) {
        Preconditions.checkArgument(provider != null, "invalid null provider");
        Preconditions.checkArgument(locationRequest != null, "invalid null location request");
        Preconditions.checkArgument(pendingIntent != null, "invalid null pending intent");
        if (Compatibility.isChangeEnabled((long) BLOCK_UNTARGETED_PENDING_INTENTS)) {
            Preconditions.checkArgument(pendingIntent.isTargetedToPackage(), "pending intent must be targeted to a package");
        }
        if (Compatibility.isChangeEnabled((long) BLOCK_IMMUTABLE_PENDING_INTENTS)) {
            Preconditions.checkArgument(!pendingIntent.isImmutable(), "pending intent must be mutable");
        }
        try {
            this.mService.registerLocationPendingIntent(provider, locationRequest, pendingIntent, this.mContext.getPackageName(), this.mContext.getAttributionTag());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public boolean injectLocation(Location location) {
        Preconditions.checkArgument(location != null, "invalid null location");
        Preconditions.checkArgument(location.isComplete(), "incomplete location object, missing timestamp or accuracy?");
        try {
            this.mService.injectLocation(location);
            return true;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void requestFlush(String provider, LocationListener listener, int requestCode) {
        boolean z = true;
        Preconditions.checkArgument(provider != null, "invalid null provider");
        Preconditions.checkArgument(listener != null, "invalid null listener");
        WeakHashMap<LocationListener, WeakReference<LocationListenerTransport>> weakHashMap = sLocationListeners;
        synchronized (weakHashMap) {
            WeakReference<LocationListenerTransport> ref = weakHashMap.get(listener);
            LocationListenerTransport transport = ref != null ? ref.get() : null;
            if (transport == null) {
                z = false;
            }
            Preconditions.checkArgument(z, "unregistered listener cannot be flushed");
            try {
                this.mService.requestListenerFlush(provider, transport, requestCode);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void requestFlush(String provider, PendingIntent pendingIntent, int requestCode) {
        Preconditions.checkArgument(provider != null, "invalid null provider");
        Preconditions.checkArgument(pendingIntent != null, "invalid null pending intent");
        try {
            this.mService.requestPendingIntentFlush(provider, pendingIntent, requestCode);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void removeUpdates(LocationListener listener) {
        Preconditions.checkArgument(listener != null, "invalid null listener");
        try {
            WeakHashMap<LocationListener, WeakReference<LocationListenerTransport>> weakHashMap = sLocationListeners;
            synchronized (weakHashMap) {
                WeakReference<LocationListenerTransport> ref = weakHashMap.remove(listener);
                LocationListenerTransport transport = ref != null ? ref.get() : null;
                if (transport != null) {
                    transport.unregister();
                    this.mService.unregisterLocationListener(transport);
                }
            }
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void removeUpdates(PendingIntent pendingIntent) {
        Preconditions.checkArgument(pendingIntent != null, "invalid null pending intent");
        try {
            this.mService.unregisterLocationPendingIntent(pendingIntent);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean hasProvider(String provider) {
        Preconditions.checkArgument(provider != null, "invalid null provider");
        try {
            return this.mService.hasProvider(provider);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<String> getAllProviders() {
        try {
            return this.mService.getAllProviders();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<String> getProviders(boolean enabledOnly) {
        try {
            return this.mService.getProviders(null, enabledOnly);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public List<String> getProviders(Criteria criteria, boolean enabledOnly) {
        Preconditions.checkArgument(criteria != null, "invalid null criteria");
        try {
            return this.mService.getProviders(criteria, enabledOnly);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public String getBestProvider(Criteria criteria, boolean enabledOnly) {
        Preconditions.checkArgument(criteria != null, "invalid null criteria");
        try {
            return this.mService.getBestProvider(criteria, enabledOnly);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public LocationProvider getProvider(String provider) {
        Preconditions.checkArgument(provider != null, "invalid null provider");
        if (!Compatibility.isChangeEnabled((long) GET_PROVIDER_SECURITY_EXCEPTIONS)) {
            if (NETWORK_PROVIDER.equals(provider) || FUSED_PROVIDER.equals(provider)) {
                try {
                    this.mContext.enforcePermission(Manifest.C0000permission.ACCESS_FINE_LOCATION, Process.myPid(), Process.myUid(), null);
                } catch (SecurityException e) {
                    this.mContext.enforcePermission(Manifest.C0000permission.ACCESS_COARSE_LOCATION, Process.myPid(), Process.myUid(), null);
                }
            } else {
                this.mContext.enforcePermission(Manifest.C0000permission.ACCESS_FINE_LOCATION, Process.myPid(), Process.myUid(), null);
            }
        }
        try {
            ProviderProperties properties = this.mService.getProviderProperties(provider);
            return new LocationProvider(provider, properties);
        } catch (RemoteException e2) {
            throw e2.rethrowFromSystemServer();
        } catch (IllegalArgumentException e3) {
            return null;
        }
    }

    public ProviderProperties getProviderProperties(String provider) {
        Preconditions.checkArgument(provider != null, "invalid null provider");
        try {
            return this.mService.getProviderProperties(provider);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    @Deprecated
    public boolean isProviderPackage(String packageName) {
        return isProviderPackage(null, packageName, null);
    }

    @SystemApi
    @Deprecated
    public boolean isProviderPackage(String provider, String packageName) {
        return isProviderPackage(provider, packageName, null);
    }

    @SystemApi
    public boolean isProviderPackage(String provider, String packageName, String attributionTag) {
        try {
            return this.mService.isProviderPackage(provider, (String) Objects.requireNonNull(packageName), attributionTag);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public List<String> getProviderPackages(String provider) {
        try {
            return this.mService.getProviderPackages(provider);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean sendExtraCommand(String provider, String command, Bundle extras) {
        Preconditions.checkArgument(provider != null, "invalid null provider");
        Preconditions.checkArgument(command != null, "invalid null command");
        try {
            this.mService.sendExtraCommand(provider, command, extras);
            return true;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void addTestProvider(String provider, boolean requiresNetwork, boolean requiresSatellite, boolean requiresCell, boolean hasMonetaryCost, boolean supportsAltitude, boolean supportsSpeed, boolean supportsBearing, int powerUsage, int accuracy) {
        addTestProvider(provider, new ProviderProperties.Builder().setHasNetworkRequirement(requiresNetwork).setHasSatelliteRequirement(requiresSatellite).setHasCellRequirement(requiresCell).setHasMonetaryCost(hasMonetaryCost).setHasAltitudeSupport(supportsAltitude).setHasSpeedSupport(supportsSpeed).setHasBearingSupport(supportsBearing).setPowerUsage(powerUsage).setAccuracy(accuracy).build());
    }

    public void addTestProvider(String provider, ProviderProperties properties) {
        addTestProvider(provider, properties, Collections.emptySet());
    }

    public void addTestProvider(String provider, ProviderProperties properties, Set<String> extraAttributionTags) {
        Preconditions.checkArgument(provider != null, "invalid null provider");
        Preconditions.checkArgument(properties != null, "invalid null properties");
        Preconditions.checkArgument(extraAttributionTags != null, "invalid null extra attribution tags");
        try {
            this.mService.addTestProvider(provider, properties, new ArrayList(extraAttributionTags), this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void removeTestProvider(String provider) {
        Preconditions.checkArgument(provider != null, "invalid null provider");
        try {
            this.mService.removeTestProvider(provider, this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setTestProviderLocation(String provider, Location location) {
        Preconditions.checkArgument(provider != null, "invalid null provider");
        Preconditions.checkArgument(location != null, "invalid null location");
        if (Compatibility.isChangeEnabled((long) BLOCK_INCOMPLETE_LOCATIONS)) {
            Preconditions.checkArgument(location.isComplete(), "incomplete location object, missing timestamp or accuracy?");
        } else {
            location.makeComplete();
        }
        try {
            this.mService.setTestProviderLocation(provider, location, this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public void clearTestProviderLocation(String provider) {
    }

    public void setTestProviderEnabled(String provider, boolean enabled) {
        Preconditions.checkArgument(provider != null, "invalid null provider");
        try {
            this.mService.setTestProviderEnabled(provider, enabled, this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public void clearTestProviderEnabled(String provider) {
        setTestProviderEnabled(provider, false);
    }

    @Deprecated
    public void setTestProviderStatus(String provider, int status, Bundle extras, long updateTime) {
    }

    @Deprecated
    public void clearTestProviderStatus(String provider) {
    }

    public void addProximityAlert(double latitude, double longitude, float radius, long expiration, PendingIntent pendingIntent) {
        Preconditions.checkArgument(pendingIntent != null, "invalid null pending intent");
        if (Compatibility.isChangeEnabled((long) BLOCK_UNTARGETED_PENDING_INTENTS)) {
            Preconditions.checkArgument(pendingIntent.isTargetedToPackage(), "pending intent must be targeted to a package");
        }
        if (Compatibility.isChangeEnabled((long) BLOCK_IMMUTABLE_PENDING_INTENTS)) {
            Preconditions.checkArgument(true ^ pendingIntent.isImmutable(), "pending intent must be mutable");
        }
        if (expiration < 0) {
            expiration = Long.MAX_VALUE;
        }
        try {
            Geofence fence = Geofence.createCircle(latitude, longitude, radius, expiration);
            this.mService.requestGeofence(fence, pendingIntent, this.mContext.getPackageName(), this.mContext.getAttributionTag());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void removeProximityAlert(PendingIntent intent) {
        Preconditions.checkArgument(intent != null, "invalid null pending intent");
        try {
            this.mService.removeGeofence(intent);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public GnssCapabilities getGnssCapabilities() {
        try {
            return this.mService.getGnssCapabilities();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getGnssYearOfHardware() {
        try {
            return this.mService.getGnssYearOfHardware();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public String getGnssHardwareModelName() {
        try {
            return this.mService.getGnssHardwareModelName();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<GnssAntennaInfo> getGnssAntennaInfos() {
        try {
            return this.mService.getGnssAntennaInfos();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public GpsStatus getGpsStatus(GpsStatus status) {
        if (Compatibility.isChangeEnabled((long) BLOCK_GPS_STATUS_USAGE)) {
            throw new UnsupportedOperationException("GpsStatus APIs not supported, please use GnssStatus APIs instead");
        }
        GnssStatus gnssStatus = GpsStatusTransport.sGnssStatus;
        int ttff = GpsStatusTransport.sTtff;
        if (gnssStatus != null) {
            if (status == null) {
                return GpsStatus.create(gnssStatus, ttff);
            }
            status.setStatus(gnssStatus, ttff);
            return status;
        } else if (status == null) {
            return GpsStatus.createEmpty();
        } else {
            return status;
        }
    }

    @Deprecated
    public boolean addGpsStatusListener(GpsStatus.Listener listener) {
        if (Compatibility.isChangeEnabled((long) BLOCK_GPS_STATUS_USAGE)) {
            throw new UnsupportedOperationException("GpsStatus APIs not supported, please use GnssStatus APIs instead");
        }
        GnssLazyLoader.sGnssStatusListeners.addListener(listener, new GpsStatusTransport(new HandlerExecutor(new Handler()), this.mContext, listener));
        return true;
    }

    @Deprecated
    public void removeGpsStatusListener(GpsStatus.Listener listener) {
        if (Compatibility.isChangeEnabled((long) BLOCK_GPS_STATUS_USAGE)) {
            throw new UnsupportedOperationException("GpsStatus APIs not supported, please use GnssStatus APIs instead");
        }
        GnssLazyLoader.sGnssStatusListeners.removeListener(listener);
    }

    @Deprecated
    public boolean registerGnssStatusCallback(GnssStatus.Callback callback) {
        return registerGnssStatusCallback(callback, (Handler) null);
    }

    public boolean registerGnssStatusCallback(GnssStatus.Callback callback, Handler handler) {
        if (handler == null) {
            handler = new Handler();
        }
        return registerGnssStatusCallback(new HandlerExecutor(handler), callback);
    }

    public boolean registerGnssStatusCallback(Executor executor, GnssStatus.Callback callback) {
        GnssLazyLoader.sGnssStatusListeners.addListener(callback, new GnssStatusTransport(executor, this.mContext, callback));
        return true;
    }

    public void unregisterGnssStatusCallback(GnssStatus.Callback callback) {
        GnssLazyLoader.sGnssStatusListeners.removeListener(callback);
    }

    @Deprecated
    public boolean addNmeaListener(GpsStatus.NmeaListener listener) {
        return false;
    }

    @Deprecated
    public void removeNmeaListener(GpsStatus.NmeaListener listener) {
    }

    @Deprecated
    public boolean addNmeaListener(OnNmeaMessageListener listener) {
        return addNmeaListener(listener, (Handler) null);
    }

    public boolean addNmeaListener(OnNmeaMessageListener listener, Handler handler) {
        if (handler == null) {
            handler = new Handler();
        }
        return addNmeaListener(new HandlerExecutor(handler), listener);
    }

    public boolean addNmeaListener(Executor executor, OnNmeaMessageListener listener) {
        GnssLazyLoader.sGnssNmeaListeners.addListener(listener, new GnssNmeaTransport(executor, this.mContext, listener));
        return true;
    }

    public void removeNmeaListener(OnNmeaMessageListener listener) {
        GnssLazyLoader.sGnssNmeaListeners.removeListener(listener);
    }

    @SystemApi
    @Deprecated
    public boolean addGpsMeasurementListener(GpsMeasurementsEvent.Listener listener) {
        return false;
    }

    @SystemApi
    @Deprecated
    public void removeGpsMeasurementListener(GpsMeasurementsEvent.Listener listener) {
    }

    @Deprecated
    public boolean registerGnssMeasurementsCallback(GnssMeasurementsEvent.Callback callback) {
        return registerGnssMeasurementsCallback(ConcurrentUtils.DIRECT_EXECUTOR, callback);
    }

    public boolean registerGnssMeasurementsCallback(GnssMeasurementsEvent.Callback callback, Handler handler) {
        if (handler == null) {
            handler = new Handler();
        }
        return registerGnssMeasurementsCallback(new GnssMeasurementRequest.Builder().build(), new HandlerExecutor(handler), callback);
    }

    public boolean registerGnssMeasurementsCallback(Executor executor, GnssMeasurementsEvent.Callback callback) {
        return registerGnssMeasurementsCallback(new GnssMeasurementRequest.Builder().build(), executor, callback);
    }

    @SystemApi
    @Deprecated
    public boolean registerGnssMeasurementsCallback(GnssRequest request, Executor executor, GnssMeasurementsEvent.Callback callback) {
        return registerGnssMeasurementsCallback(request.toGnssMeasurementRequest(), executor, callback);
    }

    public boolean registerGnssMeasurementsCallback(GnssMeasurementRequest request, Executor executor, GnssMeasurementsEvent.Callback callback) {
        GnssLazyLoader.sGnssMeasurementsListeners.addListener(callback, new GnssMeasurementsTransport(executor, this.mContext, request, callback));
        return true;
    }

    @SystemApi
    public void injectGnssMeasurementCorrections(GnssMeasurementCorrections measurementCorrections) {
        Preconditions.checkArgument(measurementCorrections != null);
        try {
            this.mService.injectGnssMeasurementCorrections(measurementCorrections);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void unregisterGnssMeasurementsCallback(GnssMeasurementsEvent.Callback callback) {
        GnssLazyLoader.sGnssMeasurementsListeners.removeListener(callback);
    }

    public boolean registerAntennaInfoListener(Executor executor, GnssAntennaInfo.Listener listener) {
        GnssLazyLoader.sGnssAntennaInfoListeners.addListener(listener, new GnssAntennaInfoTransport(executor, this.mContext, listener));
        return true;
    }

    public void unregisterAntennaInfoListener(GnssAntennaInfo.Listener listener) {
        GnssLazyLoader.sGnssAntennaInfoListeners.removeListener(listener);
    }

    @SystemApi
    @Deprecated
    public boolean addGpsNavigationMessageListener(GpsNavigationMessageEvent.Listener listener) {
        return false;
    }

    @SystemApi
    @Deprecated
    public void removeGpsNavigationMessageListener(GpsNavigationMessageEvent.Listener listener) {
    }

    @Deprecated
    public boolean registerGnssNavigationMessageCallback(GnssNavigationMessage.Callback callback) {
        return registerGnssNavigationMessageCallback(ConcurrentUtils.DIRECT_EXECUTOR, callback);
    }

    public boolean registerGnssNavigationMessageCallback(GnssNavigationMessage.Callback callback, Handler handler) {
        if (handler == null) {
            handler = new Handler();
        }
        return registerGnssNavigationMessageCallback(new HandlerExecutor(handler), callback);
    }

    public boolean registerGnssNavigationMessageCallback(Executor executor, GnssNavigationMessage.Callback callback) {
        GnssLazyLoader.sGnssNavigationListeners.addListener(callback, new GnssNavigationTransport(executor, this.mContext, callback));
        return true;
    }

    public void unregisterGnssNavigationMessageCallback(GnssNavigationMessage.Callback callback) {
        GnssLazyLoader.sGnssNavigationListeners.removeListener(callback);
    }

    @SystemApi
    public void addProviderRequestChangedListener(Executor executor, ProviderRequest.ChangedListener listener) {
        ProviderRequestLazyLoader.sProviderRequestListeners.addListener(listener, new ProviderRequestTransport(executor, listener));
    }

    @SystemApi
    public void removeProviderRequestChangedListener(ProviderRequest.ChangedListener listener) {
        ProviderRequestLazyLoader.sProviderRequestListeners.removeListener(listener);
    }

    @SystemApi
    @Deprecated
    public int getGnssBatchSize() {
        try {
            return this.mService.getGnssBatchSize();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    @Deprecated
    public boolean registerGnssBatchedLocationCallback(long periodNanos, boolean wakeOnFifoFull, BatchedLocationCallback callback, Handler handler) {
        if (handler == null) {
            handler = new Handler();
        }
        try {
            this.mService.startGnssBatch(periodNanos, new BatchedLocationCallbackTransport(callback, handler), this.mContext.getPackageName(), this.mContext.getAttributionTag(), AppOpsManager.toReceiverId(callback));
            return true;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    @Deprecated
    public void flushGnssBatch() {
        try {
            this.mService.flushGnssBatch();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    @Deprecated
    public boolean unregisterGnssBatchedLocationCallback(BatchedLocationCallback callback) {
        try {
            this.mService.stopGnssBatch();
            return true;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class GnssStatusTransportManager extends ListenerTransportManager<GnssStatusTransport> {
        GnssStatusTransportManager() {
            super(false);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.listeners.ListenerTransportManager
        public void registerTransport(GnssStatusTransport transport) throws RemoteException {
            LocationManager.getService().registerGnssStatusCallback(transport, transport.getPackage(), transport.getAttributionTag(), AppOpsManager.toReceiverId(transport.getListener()));
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.listeners.ListenerTransportManager
        public void unregisterTransport(GnssStatusTransport transport) throws RemoteException {
            LocationManager.getService().unregisterGnssStatusCallback(transport);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class GnssNmeaTransportManager extends ListenerTransportManager<GnssNmeaTransport> {
        GnssNmeaTransportManager() {
            super(false);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.listeners.ListenerTransportManager
        public void registerTransport(GnssNmeaTransport transport) throws RemoteException {
            LocationManager.getService().registerGnssNmeaCallback(transport, transport.getPackage(), transport.getAttributionTag(), AppOpsManager.toReceiverId(transport.getListener()));
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.listeners.ListenerTransportManager
        public void unregisterTransport(GnssNmeaTransport transport) throws RemoteException {
            LocationManager.getService().unregisterGnssNmeaCallback(transport);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class GnssMeasurementsTransportManager extends ListenerTransportManager<GnssMeasurementsTransport> {
        GnssMeasurementsTransportManager() {
            super(false);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.listeners.ListenerTransportManager
        public void registerTransport(GnssMeasurementsTransport transport) throws RemoteException {
            LocationManager.getService().addGnssMeasurementsListener(transport.getRequest(), transport, transport.getPackage(), transport.getAttributionTag(), AppOpsManager.toReceiverId(transport.getListener()));
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.listeners.ListenerTransportManager
        public void unregisterTransport(GnssMeasurementsTransport transport) throws RemoteException {
            LocationManager.getService().removeGnssMeasurementsListener(transport);
        }
    }

    /* loaded from: classes2.dex */
    private static class GnssAntennaTransportManager extends ListenerTransportManager<GnssAntennaInfoTransport> {
        GnssAntennaTransportManager() {
            super(false);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.listeners.ListenerTransportManager
        public void registerTransport(GnssAntennaInfoTransport transport) throws RemoteException {
            LocationManager.getService().addGnssAntennaInfoListener(transport, transport.getPackage(), transport.getAttributionTag(), AppOpsManager.toReceiverId(transport.getListener()));
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.listeners.ListenerTransportManager
        public void unregisterTransport(GnssAntennaInfoTransport transport) throws RemoteException {
            LocationManager.getService().removeGnssAntennaInfoListener(transport);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class GnssNavigationTransportManager extends ListenerTransportManager<GnssNavigationTransport> {
        GnssNavigationTransportManager() {
            super(false);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.listeners.ListenerTransportManager
        public void registerTransport(GnssNavigationTransport transport) throws RemoteException {
            LocationManager.getService().addGnssNavigationMessageListener(transport, transport.getPackage(), transport.getAttributionTag(), AppOpsManager.toReceiverId(transport.getListener()));
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.listeners.ListenerTransportManager
        public void unregisterTransport(GnssNavigationTransport transport) throws RemoteException {
            LocationManager.getService().removeGnssNavigationMessageListener(transport);
        }
    }

    /* loaded from: classes2.dex */
    private static class ProviderRequestTransportManager extends ListenerTransportManager<ProviderRequestTransport> {
        ProviderRequestTransportManager() {
            super(false);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.listeners.ListenerTransportManager
        public void registerTransport(ProviderRequestTransport transport) throws RemoteException {
            LocationManager.getService().addProviderRequestListener(transport);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.listeners.ListenerTransportManager
        public void unregisterTransport(ProviderRequestTransport transport) throws RemoteException {
            LocationManager.getService().removeProviderRequestListener(transport);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class GetCurrentLocationTransport extends ILocationCallback.Stub implements ListenerExecutor, CancellationSignal.OnCancelListener {
        volatile Consumer<Location> mConsumer;
        private final Executor mExecutor;

        GetCurrentLocationTransport(Executor executor, Consumer<Location> consumer, CancellationSignal cancellationSignal) {
            Preconditions.checkArgument(executor != null, "illegal null executor");
            Preconditions.checkArgument(consumer != null, "illegal null consumer");
            this.mExecutor = executor;
            this.mConsumer = consumer;
            if (cancellationSignal != null) {
                cancellationSignal.setOnCancelListener(this);
            }
        }

        @Override // android.p008os.CancellationSignal.OnCancelListener
        public void onCancel() {
            this.mConsumer = null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Consumer lambda$onLocation$0() {
            return this.mConsumer;
        }

        @Override // android.location.ILocationCallback
        public void onLocation(final Location location) {
            executeSafely(this.mExecutor, new Supplier() { // from class: android.location.LocationManager$GetCurrentLocationTransport$$ExternalSyntheticLambda0
                @Override // java.util.function.Supplier
                public final Object get() {
                    Consumer lambda$onLocation$0;
                    lambda$onLocation$0 = LocationManager.GetCurrentLocationTransport.this.lambda$onLocation$0();
                    return lambda$onLocation$0;
                }
            }, new ListenerExecutor.ListenerOperation<Consumer<Location>>() { // from class: android.location.LocationManager.GetCurrentLocationTransport.1
                @Override // com.android.internal.listeners.ListenerExecutor.ListenerOperation
                public void operate(Consumer<Location> consumer) {
                    consumer.accept(location);
                }

                @Override // com.android.internal.listeners.ListenerExecutor.ListenerOperation
                public void onPostExecute(boolean success) {
                    GetCurrentLocationTransport.this.mConsumer = null;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class LocationListenerTransport extends ILocationListener.Stub implements ListenerExecutor {
        private Executor mExecutor;
        private volatile LocationListener mListener;

        LocationListenerTransport(LocationListener listener, Executor executor) {
            Preconditions.checkArgument(listener != null, "invalid null listener");
            this.mListener = listener;
            setExecutor(executor);
        }

        void setExecutor(Executor executor) {
            Preconditions.checkArgument(executor != null, "invalid null executor");
            this.mExecutor = executor;
        }

        boolean isRegistered() {
            return this.mListener != null;
        }

        void unregister() {
            this.mListener = null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ LocationListener lambda$onLocationChanged$0() {
            return this.mListener;
        }

        @Override // android.location.ILocationListener
        public void onLocationChanged(final List<Location> locations, final IRemoteCallback onCompleteCallback) {
            executeSafely(this.mExecutor, new Supplier() { // from class: android.location.LocationManager$LocationListenerTransport$$ExternalSyntheticLambda2
                @Override // java.util.function.Supplier
                public final Object get() {
                    LocationListener lambda$onLocationChanged$0;
                    lambda$onLocationChanged$0 = LocationManager.LocationListenerTransport.this.lambda$onLocationChanged$0();
                    return lambda$onLocationChanged$0;
                }
            }, new ListenerExecutor.ListenerOperation<LocationListener>() { // from class: android.location.LocationManager.LocationListenerTransport.1
                @Override // com.android.internal.listeners.ListenerExecutor.ListenerOperation
                public void operate(LocationListener listener) {
                    listener.onLocationChanged(locations);
                }

                @Override // com.android.internal.listeners.ListenerExecutor.ListenerOperation
                public void onComplete(boolean success) {
                    IRemoteCallback iRemoteCallback = onCompleteCallback;
                    if (iRemoteCallback != null) {
                        try {
                            iRemoteCallback.sendResult(null);
                        } catch (RemoteException e) {
                            throw e.rethrowFromSystemServer();
                        }
                    }
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ LocationListener lambda$onFlushComplete$1() {
            return this.mListener;
        }

        @Override // android.location.ILocationListener
        public void onFlushComplete(final int requestCode) {
            executeSafely(this.mExecutor, new Supplier() { // from class: android.location.LocationManager$LocationListenerTransport$$ExternalSyntheticLambda0
                @Override // java.util.function.Supplier
                public final Object get() {
                    LocationListener lambda$onFlushComplete$1;
                    lambda$onFlushComplete$1 = LocationManager.LocationListenerTransport.this.lambda$onFlushComplete$1();
                    return lambda$onFlushComplete$1;
                }
            }, new ListenerExecutor.ListenerOperation() { // from class: android.location.LocationManager$LocationListenerTransport$$ExternalSyntheticLambda1
                @Override // com.android.internal.listeners.ListenerExecutor.ListenerOperation
                public final void operate(Object obj) {
                    ((LocationListener) obj).onFlushComplete(requestCode);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ LocationListener lambda$onProviderEnabledChanged$3() {
            return this.mListener;
        }

        @Override // android.location.ILocationListener
        public void onProviderEnabledChanged(final String provider, final boolean enabled) {
            executeSafely(this.mExecutor, new Supplier() { // from class: android.location.LocationManager$LocationListenerTransport$$ExternalSyntheticLambda3
                @Override // java.util.function.Supplier
                public final Object get() {
                    LocationListener lambda$onProviderEnabledChanged$3;
                    lambda$onProviderEnabledChanged$3 = LocationManager.LocationListenerTransport.this.lambda$onProviderEnabledChanged$3();
                    return lambda$onProviderEnabledChanged$3;
                }
            }, new ListenerExecutor.ListenerOperation() { // from class: android.location.LocationManager$LocationListenerTransport$$ExternalSyntheticLambda4
                @Override // com.android.internal.listeners.ListenerExecutor.ListenerOperation
                public final void operate(Object obj) {
                    LocationManager.LocationListenerTransport.lambda$onProviderEnabledChanged$4(enabled, provider, (LocationListener) obj);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onProviderEnabledChanged$4(boolean enabled, String provider, LocationListener listener) throws Exception {
            if (enabled) {
                listener.onProviderEnabled(provider);
            } else {
                listener.onProviderDisabled(provider);
            }
        }
    }

    @Deprecated
    /* loaded from: classes2.dex */
    private static class GpsAdapter extends GnssStatus.Callback {
        private final GpsStatus.Listener mGpsListener;

        GpsAdapter(GpsStatus.Listener gpsListener) {
            this.mGpsListener = gpsListener;
        }

        @Override // android.location.GnssStatus.Callback
        public void onStarted() {
            this.mGpsListener.onGpsStatusChanged(1);
        }

        @Override // android.location.GnssStatus.Callback
        public void onStopped() {
            this.mGpsListener.onGpsStatusChanged(2);
        }

        @Override // android.location.GnssStatus.Callback
        public void onFirstFix(int ttffMillis) {
            this.mGpsListener.onGpsStatusChanged(3);
        }

        @Override // android.location.GnssStatus.Callback
        public void onSatelliteStatusChanged(GnssStatus status) {
            this.mGpsListener.onGpsStatusChanged(4);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class GnssStatusTransport extends IGnssStatusListener.Stub implements ListenerTransport<GnssStatus.Callback> {
        private final String mAttributionTag;
        private final Executor mExecutor;
        private volatile GnssStatus.Callback mListener;
        private final String mPackageName;

        GnssStatusTransport(Executor executor, Context context, GnssStatus.Callback listener) {
            Preconditions.checkArgument(executor != null, "invalid null executor");
            Preconditions.checkArgument(listener != null, "invalid null callback");
            this.mExecutor = executor;
            this.mPackageName = context.getPackageName();
            this.mAttributionTag = context.getAttributionTag();
            this.mListener = listener;
        }

        public String getPackage() {
            return this.mPackageName;
        }

        public String getAttributionTag() {
            return this.mAttributionTag;
        }

        @Override // com.android.internal.listeners.ListenerTransport
        public void unregister() {
            this.mListener = null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.internal.listeners.ListenerTransport
        public GnssStatus.Callback getListener() {
            return this.mListener;
        }

        @Override // android.location.IGnssStatusListener
        public void onGnssStarted() {
            execute(this.mExecutor, new Consumer() { // from class: android.location.LocationManager$GnssStatusTransport$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((GnssStatus.Callback) obj).onStarted();
                }
            });
        }

        @Override // android.location.IGnssStatusListener
        public void onGnssStopped() {
            execute(this.mExecutor, new Consumer() { // from class: android.location.LocationManager$GnssStatusTransport$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((GnssStatus.Callback) obj).onStopped();
                }
            });
        }

        @Override // android.location.IGnssStatusListener
        public void onFirstFix(final int ttff) {
            execute(this.mExecutor, new Consumer() { // from class: android.location.LocationManager$GnssStatusTransport$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((GnssStatus.Callback) obj).onFirstFix(ttff);
                }
            });
        }

        @Override // android.location.IGnssStatusListener
        public void onSvStatusChanged(final GnssStatus gnssStatus) {
            execute(this.mExecutor, new Consumer() { // from class: android.location.LocationManager$GnssStatusTransport$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((GnssStatus.Callback) obj).onSatelliteStatusChanged(GnssStatus.this);
                }
            });
        }
    }

    @Deprecated
    /* loaded from: classes2.dex */
    private static class GpsStatusTransport extends GnssStatusTransport {
        static volatile GnssStatus sGnssStatus;
        static volatile int sTtff;

        GpsStatusTransport(Executor executor, Context context, GpsStatus.Listener listener) {
            super(executor, context, new GpsAdapter(listener));
        }

        @Override // android.location.LocationManager.GnssStatusTransport, android.location.IGnssStatusListener
        public void onFirstFix(int ttff) {
            sTtff = ttff;
            super.onFirstFix(ttff);
        }

        @Override // android.location.LocationManager.GnssStatusTransport, android.location.IGnssStatusListener
        public void onSvStatusChanged(GnssStatus gnssStatus) {
            sGnssStatus = gnssStatus;
            super.onSvStatusChanged(gnssStatus);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class GnssNmeaTransport extends IGnssNmeaListener.Stub implements ListenerTransport<OnNmeaMessageListener> {
        private final String mAttributionTag;
        private final Executor mExecutor;
        private volatile OnNmeaMessageListener mListener;
        private final String mPackageName;

        GnssNmeaTransport(Executor executor, Context context, OnNmeaMessageListener listener) {
            Preconditions.checkArgument(executor != null, "invalid null executor");
            Preconditions.checkArgument(listener != null, "invalid null listener");
            this.mExecutor = executor;
            this.mPackageName = context.getPackageName();
            this.mAttributionTag = context.getAttributionTag();
            this.mListener = listener;
        }

        public String getPackage() {
            return this.mPackageName;
        }

        public String getAttributionTag() {
            return this.mAttributionTag;
        }

        @Override // com.android.internal.listeners.ListenerTransport
        public void unregister() {
            this.mListener = null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.internal.listeners.ListenerTransport
        public OnNmeaMessageListener getListener() {
            return this.mListener;
        }

        @Override // android.location.IGnssNmeaListener
        public void onNmeaReceived(final long timestamp, final String nmea) {
            execute(this.mExecutor, new Consumer() { // from class: android.location.LocationManager$GnssNmeaTransport$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((OnNmeaMessageListener) obj).onNmeaMessage(nmea, timestamp);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class GnssMeasurementsTransport extends IGnssMeasurementsListener.Stub implements ListenerTransport<GnssMeasurementsEvent.Callback> {
        private final String mAttributionTag;
        private final Executor mExecutor;
        private volatile GnssMeasurementsEvent.Callback mListener;
        private final String mPackageName;
        private final GnssMeasurementRequest mRequest;

        GnssMeasurementsTransport(Executor executor, Context context, GnssMeasurementRequest request, GnssMeasurementsEvent.Callback listener) {
            Preconditions.checkArgument(executor != null, "invalid null executor");
            Preconditions.checkArgument(listener != null, "invalid null callback");
            Preconditions.checkArgument(request != null, "invalid null request");
            this.mExecutor = executor;
            this.mPackageName = context.getPackageName();
            this.mAttributionTag = context.getAttributionTag();
            this.mRequest = request;
            this.mListener = listener;
        }

        public String getPackage() {
            return this.mPackageName;
        }

        public String getAttributionTag() {
            return this.mAttributionTag;
        }

        public GnssMeasurementRequest getRequest() {
            return this.mRequest;
        }

        @Override // com.android.internal.listeners.ListenerTransport
        public void unregister() {
            this.mListener = null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.internal.listeners.ListenerTransport
        public GnssMeasurementsEvent.Callback getListener() {
            return this.mListener;
        }

        @Override // android.location.IGnssMeasurementsListener
        public void onGnssMeasurementsReceived(final GnssMeasurementsEvent event) {
            execute(this.mExecutor, new Consumer() { // from class: android.location.LocationManager$GnssMeasurementsTransport$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((GnssMeasurementsEvent.Callback) obj).onGnssMeasurementsReceived(GnssMeasurementsEvent.this);
                }
            });
        }

        @Override // android.location.IGnssMeasurementsListener
        public void onStatusChanged(final int status) {
            execute(this.mExecutor, new Consumer() { // from class: android.location.LocationManager$GnssMeasurementsTransport$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((GnssMeasurementsEvent.Callback) obj).onStatusChanged(status);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class GnssAntennaInfoTransport extends IGnssAntennaInfoListener.Stub implements ListenerTransport<GnssAntennaInfo.Listener> {
        private final String mAttributionTag;
        private final Executor mExecutor;
        private volatile GnssAntennaInfo.Listener mListener;
        private final String mPackageName;

        GnssAntennaInfoTransport(Executor executor, Context context, GnssAntennaInfo.Listener listener) {
            Preconditions.checkArgument(executor != null, "invalid null executor");
            Preconditions.checkArgument(listener != null, "invalid null listener");
            this.mExecutor = executor;
            this.mPackageName = context.getPackageName();
            this.mAttributionTag = context.getAttributionTag();
            this.mListener = listener;
        }

        public String getPackage() {
            return this.mPackageName;
        }

        public String getAttributionTag() {
            return this.mAttributionTag;
        }

        @Override // com.android.internal.listeners.ListenerTransport
        public void unregister() {
            this.mListener = null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.internal.listeners.ListenerTransport
        public GnssAntennaInfo.Listener getListener() {
            return this.mListener;
        }

        @Override // android.location.IGnssAntennaInfoListener
        public void onGnssAntennaInfoChanged(final List<GnssAntennaInfo> antennaInfos) {
            execute(this.mExecutor, new Consumer() { // from class: android.location.LocationManager$GnssAntennaInfoTransport$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((GnssAntennaInfo.Listener) obj).onGnssAntennaInfoReceived(antennaInfos);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class GnssNavigationTransport extends IGnssNavigationMessageListener.Stub implements ListenerTransport<GnssNavigationMessage.Callback> {
        private final String mAttributionTag;
        private final Executor mExecutor;
        private volatile GnssNavigationMessage.Callback mListener;
        private final String mPackageName;

        GnssNavigationTransport(Executor executor, Context context, GnssNavigationMessage.Callback listener) {
            Preconditions.checkArgument(executor != null, "invalid null executor");
            Preconditions.checkArgument(listener != null, "invalid null callback");
            this.mExecutor = executor;
            this.mPackageName = context.getPackageName();
            this.mAttributionTag = context.getAttributionTag();
            this.mListener = listener;
        }

        public String getPackage() {
            return this.mPackageName;
        }

        public String getAttributionTag() {
            return this.mAttributionTag;
        }

        @Override // com.android.internal.listeners.ListenerTransport
        public void unregister() {
            this.mListener = null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.internal.listeners.ListenerTransport
        public GnssNavigationMessage.Callback getListener() {
            return this.mListener;
        }

        @Override // android.location.IGnssNavigationMessageListener
        public void onGnssNavigationMessageReceived(final GnssNavigationMessage event) {
            execute(this.mExecutor, new Consumer() { // from class: android.location.LocationManager$GnssNavigationTransport$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((GnssNavigationMessage.Callback) obj).onGnssNavigationMessageReceived(GnssNavigationMessage.this);
                }
            });
        }

        @Override // android.location.IGnssNavigationMessageListener
        public void onStatusChanged(final int status) {
            execute(this.mExecutor, new Consumer() { // from class: android.location.LocationManager$GnssNavigationTransport$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((GnssNavigationMessage.Callback) obj).onStatusChanged(status);
                }
            });
        }
    }

    /* loaded from: classes2.dex */
    private static class ProviderRequestTransport extends IProviderRequestListener.Stub implements ListenerTransport<ProviderRequest.ChangedListener> {
        private final Executor mExecutor;
        private volatile ProviderRequest.ChangedListener mListener;

        ProviderRequestTransport(Executor executor, ProviderRequest.ChangedListener listener) {
            Preconditions.checkArgument(executor != null, "invalid null executor");
            Preconditions.checkArgument(listener != null, "invalid null callback");
            this.mExecutor = executor;
            this.mListener = listener;
        }

        @Override // com.android.internal.listeners.ListenerTransport
        public void unregister() {
            this.mListener = null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.internal.listeners.ListenerTransport
        public ProviderRequest.ChangedListener getListener() {
            return this.mListener;
        }

        @Override // android.location.provider.IProviderRequestListener
        public void onProviderRequestChanged(final String provider, final ProviderRequest request) {
            execute(this.mExecutor, new Consumer() { // from class: android.location.LocationManager$ProviderRequestTransport$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((ProviderRequest.ChangedListener) obj).onProviderRequestChanged(provider, request);
                }
            });
        }
    }

    @Deprecated
    /* loaded from: classes2.dex */
    private static class BatchedLocationCallbackWrapper implements LocationListener {
        private final BatchedLocationCallback mCallback;

        BatchedLocationCallbackWrapper(BatchedLocationCallback callback) {
            this.mCallback = callback;
        }

        @Override // android.location.LocationListener
        public void onLocationChanged(Location location) {
            this.mCallback.onLocationBatch(Collections.singletonList(location));
        }

        @Override // android.location.LocationListener
        public void onLocationChanged(List<Location> locations) {
            this.mCallback.onLocationBatch(locations);
        }
    }

    @Deprecated
    /* loaded from: classes2.dex */
    private static class BatchedLocationCallbackTransport extends LocationListenerTransport {
        BatchedLocationCallbackTransport(BatchedLocationCallback callback, Handler handler) {
            super(new BatchedLocationCallbackWrapper(callback), new HandlerExecutor(handler));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class LocationEnabledCache extends PropertyInvalidatedCache<Integer, Boolean> {
        private ILocationManager mManager;

        LocationEnabledCache(int numEntries) {
            super(numEntries, LocationManager.CACHE_KEY_LOCATION_ENABLED_PROPERTY);
        }

        @Override // android.app.PropertyInvalidatedCache
        public Boolean recompute(Integer userId) {
            Preconditions.checkArgument(userId.intValue() >= 0);
            if (this.mManager == null) {
                try {
                    this.mManager = LocationManager.getService();
                } catch (RemoteException e) {
                    e.rethrowFromSystemServer();
                }
            }
            try {
                return Boolean.valueOf(this.mManager.isLocationEnabledForUser(userId.intValue()));
            } catch (RemoteException e2) {
                throw e2.rethrowFromSystemServer();
            }
        }
    }

    public static void invalidateLocalLocationEnabledCaches() {
        PropertyInvalidatedCache.invalidateCache(CACHE_KEY_LOCATION_ENABLED_PROPERTY);
    }

    public static void disableLocalLocationEnabledCaches() {
        sLocationEnabledCache = null;
    }
}
