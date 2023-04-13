package com.android.server.location;

import android.annotation.EnforcePermission;
import android.annotation.RequiresPermission;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.app.compat.CompatChanges;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GeocoderParams;
import android.location.Geofence;
import android.location.GnssAntennaInfo;
import android.location.GnssCapabilities;
import android.location.GnssMeasurementCorrections;
import android.location.GnssMeasurementRequest;
import android.location.IGeocodeListener;
import android.location.IGnssAntennaInfoListener;
import android.location.IGnssMeasurementsListener;
import android.location.IGnssNavigationMessageListener;
import android.location.IGnssNmeaListener;
import android.location.IGnssStatusListener;
import android.location.ILocationCallback;
import android.location.ILocationListener;
import android.location.ILocationManager;
import android.location.LastLocationRequest;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationManagerInternal;
import android.location.LocationProvider;
import android.location.LocationRequest;
import android.location.LocationTime;
import android.location.provider.IProviderRequestListener;
import android.location.provider.ProviderProperties;
import android.location.util.identity.CallerIdentity;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.ICancellationSignal;
import android.os.PackageTagsList;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.WorkSource;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.Preconditions;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.location.eventlog.LocationEventLog;
import com.android.server.location.geofence.GeofenceManager;
import com.android.server.location.geofence.GeofenceProxy;
import com.android.server.location.gnss.GnssConfiguration;
import com.android.server.location.gnss.GnssLocationProvider;
import com.android.server.location.gnss.GnssManagerService;
import com.android.server.location.gnss.hal.GnssNative;
import com.android.server.location.injector.AlarmHelper;
import com.android.server.location.injector.AppForegroundHelper;
import com.android.server.location.injector.AppOpsHelper;
import com.android.server.location.injector.DeviceIdleHelper;
import com.android.server.location.injector.DeviceStationaryHelper;
import com.android.server.location.injector.EmergencyHelper;
import com.android.server.location.injector.Injector;
import com.android.server.location.injector.LocationPermissionsHelper;
import com.android.server.location.injector.LocationPowerSaveModeHelper;
import com.android.server.location.injector.LocationUsageLogger;
import com.android.server.location.injector.PackageResetHelper;
import com.android.server.location.injector.ScreenInteractiveHelper;
import com.android.server.location.injector.SettingsHelper;
import com.android.server.location.injector.SystemAlarmHelper;
import com.android.server.location.injector.SystemAppForegroundHelper;
import com.android.server.location.injector.SystemAppOpsHelper;
import com.android.server.location.injector.SystemDeviceIdleHelper;
import com.android.server.location.injector.SystemDeviceStationaryHelper;
import com.android.server.location.injector.SystemEmergencyHelper;
import com.android.server.location.injector.SystemLocationPermissionsHelper;
import com.android.server.location.injector.SystemLocationPowerSaveModeHelper;
import com.android.server.location.injector.SystemPackageResetHelper;
import com.android.server.location.injector.SystemScreenInteractiveHelper;
import com.android.server.location.injector.SystemSettingsHelper;
import com.android.server.location.injector.SystemUserInfoHelper;
import com.android.server.location.injector.UserInfoHelper;
import com.android.server.location.provider.AbstractLocationProvider;
import com.android.server.location.provider.LocationProviderManager;
import com.android.server.location.provider.MockLocationProvider;
import com.android.server.location.provider.PassiveLocationProvider;
import com.android.server.location.provider.PassiveLocationProviderManager;
import com.android.server.location.provider.StationaryThrottlingLocationProvider;
import com.android.server.location.provider.proxy.ProxyLocationProvider;
import com.android.server.location.settings.LocationSettings;
import com.android.server.location.settings.LocationUserSettings;
import com.android.server.p011pm.permission.LegacyPermissionManagerInternal;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
/* loaded from: classes.dex */
public class LocationManagerService extends ILocationManager.Stub implements LocationProviderManager.StateChangedListener {

    /* renamed from: D */
    public static final boolean f1147D = Log.isLoggable("LocationManagerService", 3);
    public final Context mContext;
    @GuardedBy({"mDeprecatedGnssBatchingLock"})
    public ILocationListener mDeprecatedGnssBatchingListener;
    @GuardedBy({"mLock"})
    public String mExtraLocationControllerPackage;
    @GuardedBy({"mLock"})
    public boolean mExtraLocationControllerPackageEnabled;
    public GeocoderProxy mGeocodeProvider;
    public final GeofenceManager mGeofenceManager;
    public final Injector mInjector;
    public final LocalService mLocalService;
    @GuardedBy({"mLock"})
    public LocationManagerInternal.LocationPackageTagsListener mLocationTagsChangedListener;
    public final PassiveLocationProviderManager mPassiveManager;
    public final Object mLock = new Object();
    public volatile GnssManagerService mGnssManagerService = null;
    public final Object mDeprecatedGnssBatchingLock = new Object();
    public final CopyOnWriteArrayList<LocationProviderManager> mProviderManagers = new CopyOnWriteArrayList<>();

    /* loaded from: classes.dex */
    public static class Lifecycle extends SystemService {
        public final LocationManagerService mService;
        public final SystemInjector mSystemInjector;
        public final LifecycleUserInfoHelper mUserInfoHelper;

        public Lifecycle(Context context) {
            super(context);
            LifecycleUserInfoHelper lifecycleUserInfoHelper = new LifecycleUserInfoHelper(context);
            this.mUserInfoHelper = lifecycleUserInfoHelper;
            SystemInjector systemInjector = new SystemInjector(context, lifecycleUserInfoHelper);
            this.mSystemInjector = systemInjector;
            this.mService = new LocationManagerService(context, systemInjector);
        }

        @Override // com.android.server.SystemService
        public void onStart() {
            publishBinderService("location", this.mService);
            LocationManager.invalidateLocalLocationEnabledCaches();
            LocationManager.disableLocalLocationEnabledCaches();
        }

        @Override // com.android.server.SystemService
        public void onBootPhase(int i) {
            if (i == 500) {
                this.mSystemInjector.onSystemReady();
                this.mService.onSystemReady();
            } else if (i == 600) {
                this.mService.onSystemThirdPartyAppsCanStart();
            }
        }

        @Override // com.android.server.SystemService
        public void onUserStarting(SystemService.TargetUser targetUser) {
            this.mUserInfoHelper.onUserStarted(targetUser.getUserIdentifier());
            this.mService.logLocationEnabledState();
        }

        @Override // com.android.server.SystemService
        public void onUserSwitching(SystemService.TargetUser targetUser, SystemService.TargetUser targetUser2) {
            this.mUserInfoHelper.onCurrentUserChanged(targetUser.getUserIdentifier(), targetUser2.getUserIdentifier());
        }

        @Override // com.android.server.SystemService
        public void onUserStopped(SystemService.TargetUser targetUser) {
            this.mUserInfoHelper.onUserStopped(targetUser.getUserIdentifier());
        }

        /* loaded from: classes.dex */
        public static class LifecycleUserInfoHelper extends SystemUserInfoHelper {
            public LifecycleUserInfoHelper(Context context) {
                super(context);
            }

            public void onUserStarted(int i) {
                dispatchOnUserStarted(i);
            }

            public void onUserStopped(int i) {
                dispatchOnUserStopped(i);
            }

            public void onCurrentUserChanged(int i, int i2) {
                dispatchOnCurrentUserChanged(i, i2);
            }
        }
    }

    public LocationManagerService(Context context, Injector injector) {
        Context createAttributionContext = context.createAttributionContext("LocationService");
        this.mContext = createAttributionContext;
        this.mInjector = injector;
        LocalService localService = new LocalService();
        this.mLocalService = localService;
        LocalServices.addService(LocationManagerInternal.class, localService);
        this.mGeofenceManager = new GeofenceManager(createAttributionContext, injector);
        injector.getLocationSettings().registerLocationUserSettingsListener(new LocationSettings.LocationUserSettingsListener() { // from class: com.android.server.location.LocationManagerService$$ExternalSyntheticLambda0
            @Override // com.android.server.location.settings.LocationSettings.LocationUserSettingsListener
            public final void onLocationUserSettingsChanged(int i, LocationUserSettings locationUserSettings, LocationUserSettings locationUserSettings2) {
                LocationManagerService.this.onLocationUserSettingsChanged(i, locationUserSettings, locationUserSettings2);
            }
        });
        injector.getSettingsHelper().addOnLocationEnabledChangedListener(new SettingsHelper.UserSettingChangedListener() { // from class: com.android.server.location.LocationManagerService$$ExternalSyntheticLambda1
            @Override // com.android.server.location.injector.SettingsHelper.UserSettingChangedListener
            public final void onSettingChanged(int i) {
                LocationManagerService.this.onLocationModeChanged(i);
            }
        });
        injector.getSettingsHelper().addAdasAllowlistChangedListener(new SettingsHelper.GlobalSettingChangedListener() { // from class: com.android.server.location.LocationManagerService$$ExternalSyntheticLambda2
            @Override // com.android.server.location.injector.SettingsHelper.GlobalSettingChangedListener
            public final void onSettingChanged() {
                LocationManagerService.this.lambda$new$0();
            }
        });
        injector.getSettingsHelper().addIgnoreSettingsAllowlistChangedListener(new SettingsHelper.GlobalSettingChangedListener() { // from class: com.android.server.location.LocationManagerService$$ExternalSyntheticLambda3
            @Override // com.android.server.location.injector.SettingsHelper.GlobalSettingChangedListener
            public final void onSettingChanged() {
                LocationManagerService.this.lambda$new$1();
            }
        });
        injector.getUserInfoHelper().addListener(new UserInfoHelper.UserListener() { // from class: com.android.server.location.LocationManagerService$$ExternalSyntheticLambda4
            @Override // com.android.server.location.injector.UserInfoHelper.UserListener
            public final void onUserChanged(int i, int i2) {
                LocationManagerService.this.lambda$new$2(i, i2);
            }
        });
        PassiveLocationProviderManager passiveLocationProviderManager = new PassiveLocationProviderManager(createAttributionContext, injector);
        this.mPassiveManager = passiveLocationProviderManager;
        addLocationProviderManager(passiveLocationProviderManager, new PassiveLocationProvider(createAttributionContext));
        LegacyPermissionManagerInternal legacyPermissionManagerInternal = (LegacyPermissionManagerInternal) LocalServices.getService(LegacyPermissionManagerInternal.class);
        legacyPermissionManagerInternal.setLocationPackagesProvider(new LegacyPermissionManagerInternal.PackagesProvider() { // from class: com.android.server.location.LocationManagerService$$ExternalSyntheticLambda5
            @Override // com.android.server.p011pm.permission.LegacyPermissionManagerInternal.PackagesProvider
            public final String[] getPackages(int i) {
                String[] lambda$new$3;
                lambda$new$3 = LocationManagerService.this.lambda$new$3(i);
                return lambda$new$3;
            }
        });
        legacyPermissionManagerInternal.setLocationExtraPackagesProvider(new LegacyPermissionManagerInternal.PackagesProvider() { // from class: com.android.server.location.LocationManagerService$$ExternalSyntheticLambda6
            @Override // com.android.server.p011pm.permission.LegacyPermissionManagerInternal.PackagesProvider
            public final String[] getPackages(int i) {
                String[] lambda$new$4;
                lambda$new$4 = LocationManagerService.this.lambda$new$4(i);
                return lambda$new$4;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        refreshAppOpsRestrictions(-1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        refreshAppOpsRestrictions(-1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(int i, int i2) {
        if (i2 == 2) {
            refreshAppOpsRestrictions(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String[] lambda$new$3(int i) {
        return this.mContext.getResources().getStringArray(17236089);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String[] lambda$new$4(int i) {
        return this.mContext.getResources().getStringArray(17236088);
    }

    public LocationProviderManager getLocationProviderManager(String str) {
        if (str == null) {
            return null;
        }
        Iterator<LocationProviderManager> it = this.mProviderManagers.iterator();
        while (it.hasNext()) {
            LocationProviderManager next = it.next();
            if (str.equals(next.getName())) {
                if (next.isVisibleToCaller()) {
                    return next;
                }
                return null;
            }
        }
        return null;
    }

    public final LocationProviderManager getOrAddLocationProviderManager(String str) {
        synchronized (this.mProviderManagers) {
            Iterator<LocationProviderManager> it = this.mProviderManagers.iterator();
            while (it.hasNext()) {
                LocationProviderManager next = it.next();
                if (str.equals(next.getName())) {
                    return next;
                }
            }
            LocationProviderManager locationProviderManager = new LocationProviderManager(this.mContext, this.mInjector, str, this.mPassiveManager);
            addLocationProviderManager(locationProviderManager, null);
            return locationProviderManager;
        }
    }

    @VisibleForTesting
    public void addLocationProviderManager(LocationProviderManager locationProviderManager, AbstractLocationProvider abstractLocationProvider) {
        synchronized (this.mProviderManagers) {
            Preconditions.checkState(getLocationProviderManager(locationProviderManager.getName()) == null);
            locationProviderManager.startManager(this);
            if (abstractLocationProvider != null) {
                if (locationProviderManager != this.mPassiveManager) {
                    if (Settings.Global.getInt(this.mContext.getContentResolver(), "location_enable_stationary_throttle", 1) != 0) {
                        abstractLocationProvider = new StationaryThrottlingLocationProvider(locationProviderManager.getName(), this.mInjector, abstractLocationProvider);
                    }
                }
                locationProviderManager.setRealProvider(abstractLocationProvider);
            }
            this.mProviderManagers.add(locationProviderManager);
        }
    }

    public final void removeLocationProviderManager(LocationProviderManager locationProviderManager) {
        synchronized (this.mProviderManagers) {
            Preconditions.checkArgument(this.mProviderManagers.remove(locationProviderManager));
            locationProviderManager.setMockProvider(null);
            locationProviderManager.setRealProvider(null);
            locationProviderManager.stopManager();
        }
    }

    public void onSystemReady() {
        if (Build.IS_DEBUGGABLE) {
            AppOpsManager appOpsManager = (AppOpsManager) this.mContext.getSystemService(AppOpsManager.class);
            Objects.requireNonNull(appOpsManager);
            appOpsManager.startWatchingNoted(new int[]{1, 0}, new AppOpsManager.OnOpNotedListener() { // from class: com.android.server.location.LocationManagerService$$ExternalSyntheticLambda8
                public final void onOpNoted(String str, int i, String str2, String str3, int i2, int i3) {
                    LocationManagerService.this.lambda$onSystemReady$5(str, i, str2, str3, i2, i3);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onSystemReady$5(String str, int i, String str2, String str3, int i2, int i3) {
        if (isLocationEnabledForUser(UserHandle.getUserId(i))) {
            return;
        }
        Log.w("LocationManagerService", "location noteOp with location off - " + CallerIdentity.forTest(i, 0, str2, str3));
    }

    public void onSystemThirdPartyAppsCanStart() {
        ProxyLocationProvider create = ProxyLocationProvider.create(this.mContext, "network", "com.android.location.service.v3.NetworkLocationProvider", 17891661, 17039966);
        if (create != null) {
            addLocationProviderManager(new LocationProviderManager(this.mContext, this.mInjector, "network", this.mPassiveManager), create);
        } else {
            Log.w("LocationManagerService", "no network location provider found");
        }
        Preconditions.checkState(!this.mContext.getPackageManager().queryIntentServicesAsUser(new Intent("com.android.location.service.FusedLocationProvider"), 1572864, 0).isEmpty(), "Unable to find a direct boot aware fused location provider");
        ProxyLocationProvider create2 = ProxyLocationProvider.create(this.mContext, "fused", "com.android.location.service.FusedLocationProvider", 17891649, 17039938);
        if (create2 != null) {
            addLocationProviderManager(new LocationProviderManager(this.mContext, this.mInjector, "fused", this.mPassiveManager), create2);
        } else {
            Log.wtf("LocationManagerService", "no fused location provider found");
        }
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.location") && GnssNative.isSupported()) {
            this.mGnssManagerService = new GnssManagerService(this.mContext, this.mInjector, GnssNative.create(this.mInjector, new GnssConfiguration(this.mContext)));
            this.mGnssManagerService.onSystemReady();
            GnssLocationProvider create3 = !this.mContext.getResources().getBoolean(17891861) ? ProxyLocationProvider.create(this.mContext, "gps", "android.location.provider.action.GNSS_PROVIDER", 17891861, 17039942) : null;
            if (create3 == null) {
                create3 = this.mGnssManagerService.getGnssLocationProvider();
            } else {
                addLocationProviderManager(new LocationProviderManager(this.mContext, this.mInjector, "gps_hardware", this.mPassiveManager, Collections.singletonList("android.permission.LOCATION_HARDWARE")), this.mGnssManagerService.getGnssLocationProvider());
            }
            addLocationProviderManager(new LocationProviderManager(this.mContext, this.mInjector, "gps", this.mPassiveManager), create3);
        }
        GeocoderProxy createAndRegister = GeocoderProxy.createAndRegister(this.mContext);
        this.mGeocodeProvider = createAndRegister;
        if (createAndRegister == null) {
            Log.e("LocationManagerService", "no geocoder provider found");
        }
        if (HardwareActivityRecognitionProxy.createAndRegister(this.mContext) == null) {
            Log.e("LocationManagerService", "unable to bind ActivityRecognitionProxy");
        }
        if (this.mGnssManagerService != null && GeofenceProxy.createAndBind(this.mContext, this.mGnssManagerService.getGnssGeofenceProxy()) == null) {
            Log.e("LocationManagerService", "unable to bind to GeofenceProxy");
        }
        for (String str : this.mContext.getResources().getStringArray(17236149)) {
            String[] split = str.split(",");
            getOrAddLocationProviderManager(split[0].trim()).setMockProvider(new MockLocationProvider(new ProviderProperties.Builder().setHasNetworkRequirement(Boolean.parseBoolean(split[1])).setHasSatelliteRequirement(Boolean.parseBoolean(split[2])).setHasCellRequirement(Boolean.parseBoolean(split[3])).setHasMonetaryCost(Boolean.parseBoolean(split[4])).setHasAltitudeSupport(Boolean.parseBoolean(split[5])).setHasSpeedSupport(Boolean.parseBoolean(split[6])).setHasBearingSupport(Boolean.parseBoolean(split[7])).setPowerUsage(Integer.parseInt(split[8])).setAccuracy(Integer.parseInt(split[9])).build(), CallerIdentity.fromContext(this.mContext), Collections.emptySet()));
        }
    }

    public final void onLocationUserSettingsChanged(int i, LocationUserSettings locationUserSettings, LocationUserSettings locationUserSettings2) {
        if (locationUserSettings.isAdasGnssLocationEnabled() != locationUserSettings2.isAdasGnssLocationEnabled()) {
            boolean isAdasGnssLocationEnabled = locationUserSettings2.isAdasGnssLocationEnabled();
            if (f1147D) {
                Log.d("LocationManagerService", "[u" + i + "] adas gnss location enabled = " + isAdasGnssLocationEnabled);
            }
            LocationEventLog.EVENT_LOG.logAdasLocationEnabled(i, isAdasGnssLocationEnabled);
            this.mContext.sendBroadcastAsUser(new Intent("android.location.action.ADAS_GNSS_ENABLED_CHANGED").putExtra("android.location.extra.ADAS_GNSS_ENABLED", isAdasGnssLocationEnabled).addFlags(1073741824).addFlags(268435456), UserHandle.of(i));
        }
    }

    public final void onLocationModeChanged(int i) {
        boolean isLocationEnabled = this.mInjector.getSettingsHelper().isLocationEnabled(i);
        LocationManager.invalidateLocalLocationEnabledCaches();
        if (f1147D) {
            Log.d("LocationManagerService", "[u" + i + "] location enabled = " + isLocationEnabled);
        }
        LocationEventLog.EVENT_LOG.logLocationEnabled(i, isLocationEnabled);
        logLocationEnabledState();
        this.mContext.sendBroadcastAsUser(new Intent("android.location.MODE_CHANGED").putExtra("android.location.extra.LOCATION_ENABLED", isLocationEnabled).addFlags(1073741824).addFlags(268435456), UserHandle.of(i));
        refreshAppOpsRestrictions(i);
    }

    public final void logLocationEnabledState() {
        boolean z = false;
        for (int i : this.mInjector.getUserInfoHelper().getRunningUserIds()) {
            z = this.mInjector.getSettingsHelper().isLocationEnabled(i);
            if (z) {
                break;
            }
        }
        this.mInjector.getLocationUsageLogger().logLocationEnabledStateChanged(z);
    }

    public int getGnssYearOfHardware() {
        if (this.mGnssManagerService == null) {
            return 0;
        }
        return this.mGnssManagerService.getGnssYearOfHardware();
    }

    public String getGnssHardwareModelName() {
        return this.mGnssManagerService == null ? "" : this.mGnssManagerService.getGnssHardwareModelName();
    }

    public int getGnssBatchSize() {
        if (this.mGnssManagerService == null) {
            return 0;
        }
        return this.mGnssManagerService.getGnssBatchSize();
    }

    public void startGnssBatch(long j, ILocationListener iLocationListener, String str, String str2, String str3) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.LOCATION_HARDWARE", null);
        if (this.mGnssManagerService == null) {
            return;
        }
        long millis = TimeUnit.NANOSECONDS.toMillis(j);
        synchronized (this.mDeprecatedGnssBatchingLock) {
            stopGnssBatch();
            registerLocationListener("gps", new LocationRequest.Builder(millis).setMaxUpdateDelayMillis(millis * this.mGnssManagerService.getGnssBatchSize()).setHiddenFromAppOps(true).build(), iLocationListener, str, str2, str3);
            this.mDeprecatedGnssBatchingListener = iLocationListener;
        }
    }

    public void flushGnssBatch() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.LOCATION_HARDWARE", null);
        if (this.mGnssManagerService == null) {
            return;
        }
        synchronized (this.mDeprecatedGnssBatchingLock) {
            ILocationListener iLocationListener = this.mDeprecatedGnssBatchingListener;
            if (iLocationListener != null) {
                requestListenerFlush("gps", iLocationListener, 0);
            }
        }
    }

    public void stopGnssBatch() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.LOCATION_HARDWARE", null);
        if (this.mGnssManagerService == null) {
            return;
        }
        synchronized (this.mDeprecatedGnssBatchingLock) {
            ILocationListener iLocationListener = this.mDeprecatedGnssBatchingListener;
            if (iLocationListener != null) {
                this.mDeprecatedGnssBatchingListener = null;
                unregisterLocationListener(iLocationListener);
            }
        }
    }

    public boolean hasProvider(String str) {
        return getLocationProviderManager(str) != null;
    }

    public List<String> getAllProviders() {
        ArrayList arrayList = new ArrayList(this.mProviderManagers.size());
        Iterator<LocationProviderManager> it = this.mProviderManagers.iterator();
        while (it.hasNext()) {
            LocationProviderManager next = it.next();
            if (next.isVisibleToCaller()) {
                arrayList.add(next.getName());
            }
        }
        return arrayList;
    }

    public List<String> getProviders(Criteria criteria, boolean z) {
        ArrayList arrayList;
        if (!LocationPermissions.checkCallingOrSelfLocationPermission(this.mContext, 1)) {
            return Collections.emptyList();
        }
        synchronized (this.mLock) {
            arrayList = new ArrayList(this.mProviderManagers.size());
            Iterator<LocationProviderManager> it = this.mProviderManagers.iterator();
            while (it.hasNext()) {
                LocationProviderManager next = it.next();
                if (next.isVisibleToCaller()) {
                    String name = next.getName();
                    if (!z || next.isEnabled(UserHandle.getCallingUserId())) {
                        if (criteria == null || LocationProvider.propertiesMeetCriteria(name, next.getProperties(), criteria)) {
                            arrayList.add(name);
                        }
                    }
                }
            }
        }
        return arrayList;
    }

    public String getBestProvider(Criteria criteria, boolean z) {
        List<String> providers;
        synchronized (this.mLock) {
            providers = getProviders(criteria, z);
            if (providers.isEmpty()) {
                providers = getProviders(null, z);
            }
        }
        if (providers.isEmpty()) {
            return null;
        }
        return providers.contains("fused") ? "fused" : providers.contains("gps") ? "gps" : providers.contains("network") ? "network" : providers.get(0);
    }

    public String[] getBackgroundThrottlingWhitelist() {
        return (String[]) this.mInjector.getSettingsHelper().getBackgroundThrottlePackageWhitelist().toArray(new String[0]);
    }

    public PackageTagsList getIgnoreSettingsAllowlist() {
        return this.mInjector.getSettingsHelper().getIgnoreSettingsAllowlist();
    }

    public PackageTagsList getAdasAllowlist() {
        return this.mInjector.getSettingsHelper().getAdasAllowlist();
    }

    public ICancellationSignal getCurrentLocation(String str, LocationRequest locationRequest, ILocationCallback iLocationCallback, String str2, String str3, String str4) {
        CallerIdentity fromBinder = CallerIdentity.fromBinder(this.mContext, str2, str3, str4);
        int permissionLevel = LocationPermissions.getPermissionLevel(this.mContext, fromBinder.getUid(), fromBinder.getPid());
        LocationPermissions.enforceLocationPermission(fromBinder.getUid(), permissionLevel, 1);
        Preconditions.checkState((fromBinder.getPid() == Process.myPid() && str3 == null) ? false : true);
        LocationRequest validateLocationRequest = validateLocationRequest(str, locationRequest, fromBinder);
        LocationProviderManager locationProviderManager = getLocationProviderManager(str);
        boolean z = locationProviderManager != null;
        Preconditions.checkArgument(z, "provider \"" + str + "\" does not exist");
        return locationProviderManager.getCurrentLocation(validateLocationRequest, fromBinder, permissionLevel, iLocationCallback);
    }

    public void registerLocationListener(String str, LocationRequest locationRequest, ILocationListener iLocationListener, String str2, String str3, String str4) {
        CallerIdentity fromBinder = CallerIdentity.fromBinder(this.mContext, str2, str3, str4);
        int permissionLevel = LocationPermissions.getPermissionLevel(this.mContext, fromBinder.getUid(), fromBinder.getPid());
        LocationPermissions.enforceLocationPermission(fromBinder.getUid(), permissionLevel, 1);
        if (fromBinder.getPid() == Process.myPid() && str3 == null) {
            Log.w("LocationManagerService", "system location request with no attribution tag", new IllegalArgumentException());
        }
        LocationRequest validateLocationRequest = validateLocationRequest(str, locationRequest, fromBinder);
        LocationProviderManager locationProviderManager = getLocationProviderManager(str);
        boolean z = locationProviderManager != null;
        Preconditions.checkArgument(z, "provider \"" + str + "\" does not exist");
        locationProviderManager.registerLocationRequest(validateLocationRequest, fromBinder, permissionLevel, iLocationListener);
    }

    public void registerLocationPendingIntent(String str, LocationRequest locationRequest, PendingIntent pendingIntent, String str2, String str3) {
        CallerIdentity fromBinder = CallerIdentity.fromBinder(this.mContext, str2, str3, AppOpsManager.toReceiverId(pendingIntent));
        int permissionLevel = LocationPermissions.getPermissionLevel(this.mContext, fromBinder.getUid(), fromBinder.getPid());
        LocationPermissions.enforceLocationPermission(fromBinder.getUid(), permissionLevel, 1);
        Preconditions.checkArgument((fromBinder.getPid() == Process.myPid() && str3 == null) ? false : true);
        if (CompatChanges.isChangeEnabled(169887240L, fromBinder.getUid())) {
            if (locationRequest.isLowPower() || locationRequest.isHiddenFromAppOps() || locationRequest.isLocationSettingsIgnored() || !locationRequest.getWorkSource().isEmpty()) {
                throw new SecurityException("PendingIntent location requests may not use system APIs: " + locationRequest);
            }
        }
        LocationRequest validateLocationRequest = validateLocationRequest(str, locationRequest, fromBinder);
        LocationProviderManager locationProviderManager = getLocationProviderManager(str);
        boolean z = locationProviderManager != null;
        Preconditions.checkArgument(z, "provider \"" + str + "\" does not exist");
        locationProviderManager.registerLocationRequest(validateLocationRequest, fromBinder, permissionLevel, pendingIntent);
    }

    public final LocationRequest validateLocationRequest(String str, LocationRequest locationRequest, CallerIdentity callerIdentity) {
        if (!locationRequest.getWorkSource().isEmpty()) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.UPDATE_DEVICE_STATS", "setting a work source requires android.permission.UPDATE_DEVICE_STATS");
        }
        LocationRequest.Builder builder = new LocationRequest.Builder(locationRequest);
        if (!CompatChanges.isChangeEnabled(168936375L, Binder.getCallingUid()) && this.mContext.checkCallingPermission("android.permission.LOCATION_HARDWARE") != 0) {
            builder.setLowPower(false);
        }
        WorkSource workSource = new WorkSource(locationRequest.getWorkSource());
        if (workSource.size() > 0 && workSource.getPackageName(0) == null) {
            Log.w("LocationManagerService", "received (and ignoring) illegal worksource with no package name");
            workSource.clear();
        } else {
            List workChains = workSource.getWorkChains();
            if (workChains != null && !workChains.isEmpty() && ((WorkSource.WorkChain) workChains.get(0)).getAttributionTag() == null) {
                Log.w("LocationManagerService", "received (and ignoring) illegal worksource with no attribution tag");
                workSource.clear();
            }
        }
        if (workSource.isEmpty()) {
            callerIdentity.addToWorkSource(workSource);
        }
        builder.setWorkSource(workSource);
        LocationRequest build = builder.build();
        boolean isProvider = this.mLocalService.isProvider(null, callerIdentity);
        if (build.isLowPower() && CompatChanges.isChangeEnabled(168936375L, callerIdentity.getUid())) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.LOCATION_HARDWARE", "low power request requires android.permission.LOCATION_HARDWARE");
        }
        if (build.isHiddenFromAppOps()) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.UPDATE_APP_OPS_STATS", "hiding from app ops requires android.permission.UPDATE_APP_OPS_STATS");
        }
        if (build.isAdasGnssBypass()) {
            if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.automotive")) {
                throw new IllegalArgumentException("adas gnss bypass requests are only allowed on automotive devices");
            }
            if (!"gps".equals(str)) {
                throw new IllegalArgumentException("adas gnss bypass requests are only allowed on the \"gps\" provider");
            }
            if (!isProvider) {
                LocationPermissions.enforceCallingOrSelfBypassPermission(this.mContext);
            }
        }
        if (build.isLocationSettingsIgnored() && !isProvider) {
            LocationPermissions.enforceCallingOrSelfBypassPermission(this.mContext);
        }
        return build;
    }

    public void requestListenerFlush(String str, ILocationListener iLocationListener, int i) {
        LocationProviderManager locationProviderManager = getLocationProviderManager(str);
        boolean z = locationProviderManager != null;
        Preconditions.checkArgument(z, "provider \"" + str + "\" does not exist");
        Objects.requireNonNull(iLocationListener);
        locationProviderManager.flush(iLocationListener, i);
    }

    public void requestPendingIntentFlush(String str, PendingIntent pendingIntent, int i) {
        LocationProviderManager locationProviderManager = getLocationProviderManager(str);
        boolean z = locationProviderManager != null;
        Preconditions.checkArgument(z, "provider \"" + str + "\" does not exist");
        Objects.requireNonNull(pendingIntent);
        locationProviderManager.flush(pendingIntent, i);
    }

    public void unregisterLocationListener(ILocationListener iLocationListener) {
        Iterator<LocationProviderManager> it = this.mProviderManagers.iterator();
        while (it.hasNext()) {
            it.next().unregisterLocationRequest(iLocationListener);
        }
    }

    public void unregisterLocationPendingIntent(PendingIntent pendingIntent) {
        Iterator<LocationProviderManager> it = this.mProviderManagers.iterator();
        while (it.hasNext()) {
            it.next().unregisterLocationRequest(pendingIntent);
        }
    }

    public Location getLastLocation(String str, LastLocationRequest lastLocationRequest, String str2, String str3) {
        CallerIdentity fromBinder = CallerIdentity.fromBinder(this.mContext, str2, str3);
        int permissionLevel = LocationPermissions.getPermissionLevel(this.mContext, fromBinder.getUid(), fromBinder.getPid());
        boolean z = true;
        LocationPermissions.enforceLocationPermission(fromBinder.getUid(), permissionLevel, 1);
        if (fromBinder.getPid() == Process.myPid() && str3 == null) {
            z = false;
        }
        Preconditions.checkArgument(z);
        LastLocationRequest validateLastLocationRequest = validateLastLocationRequest(str, lastLocationRequest, fromBinder);
        LocationProviderManager locationProviderManager = getLocationProviderManager(str);
        if (locationProviderManager == null) {
            return null;
        }
        return locationProviderManager.getLastLocation(validateLastLocationRequest, fromBinder, permissionLevel);
    }

    public final LastLocationRequest validateLastLocationRequest(String str, LastLocationRequest lastLocationRequest, CallerIdentity callerIdentity) {
        LastLocationRequest build = new LastLocationRequest.Builder(lastLocationRequest).build();
        boolean isProvider = this.mLocalService.isProvider(null, callerIdentity);
        if (build.isHiddenFromAppOps()) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.UPDATE_APP_OPS_STATS", "hiding from app ops requires android.permission.UPDATE_APP_OPS_STATS");
        }
        if (build.isAdasGnssBypass()) {
            if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.automotive")) {
                throw new IllegalArgumentException("adas gnss bypass requests are only allowed on automotive devices");
            }
            if (!"gps".equals(str)) {
                throw new IllegalArgumentException("adas gnss bypass requests are only allowed on the \"gps\" provider");
            }
            if (!isProvider) {
                LocationPermissions.enforceCallingOrSelfBypassPermission(this.mContext);
            }
        }
        if (build.isLocationSettingsIgnored() && !isProvider) {
            LocationPermissions.enforceCallingOrSelfBypassPermission(this.mContext);
        }
        return build;
    }

    public LocationTime getGnssTimeMillis() {
        return this.mLocalService.getGnssTimeMillis();
    }

    @EnforcePermission(allOf = {"android.permission.LOCATION_HARDWARE", "android.permission.ACCESS_FINE_LOCATION"})
    public void injectLocation(Location location) {
        super.injectLocation_enforcePermission();
        Preconditions.checkArgument(location.isComplete());
        int callingUserId = UserHandle.getCallingUserId();
        LocationProviderManager locationProviderManager = getLocationProviderManager(location.getProvider());
        if (locationProviderManager == null || !locationProviderManager.isEnabled(callingUserId)) {
            return;
        }
        locationProviderManager.injectLastLocation(location, callingUserId);
    }

    public void requestGeofence(Geofence geofence, PendingIntent pendingIntent, String str, String str2) {
        this.mGeofenceManager.addGeofence(geofence, pendingIntent, str, str2);
    }

    public void removeGeofence(PendingIntent pendingIntent) {
        this.mGeofenceManager.removeGeofence(pendingIntent);
    }

    public void registerGnssStatusCallback(IGnssStatusListener iGnssStatusListener, String str, String str2, String str3) {
        if (this.mGnssManagerService != null) {
            this.mGnssManagerService.registerGnssStatusCallback(iGnssStatusListener, str, str2, str3);
        }
    }

    public void unregisterGnssStatusCallback(IGnssStatusListener iGnssStatusListener) {
        if (this.mGnssManagerService != null) {
            this.mGnssManagerService.unregisterGnssStatusCallback(iGnssStatusListener);
        }
    }

    public void registerGnssNmeaCallback(IGnssNmeaListener iGnssNmeaListener, String str, String str2, String str3) {
        if (this.mGnssManagerService != null) {
            this.mGnssManagerService.registerGnssNmeaCallback(iGnssNmeaListener, str, str2, str3);
        }
    }

    public void unregisterGnssNmeaCallback(IGnssNmeaListener iGnssNmeaListener) {
        if (this.mGnssManagerService != null) {
            this.mGnssManagerService.unregisterGnssNmeaCallback(iGnssNmeaListener);
        }
    }

    public void addGnssMeasurementsListener(GnssMeasurementRequest gnssMeasurementRequest, IGnssMeasurementsListener iGnssMeasurementsListener, String str, String str2, String str3) {
        if (this.mGnssManagerService != null) {
            this.mGnssManagerService.addGnssMeasurementsListener(gnssMeasurementRequest, iGnssMeasurementsListener, str, str2, str3);
        }
    }

    public void removeGnssMeasurementsListener(IGnssMeasurementsListener iGnssMeasurementsListener) {
        if (this.mGnssManagerService != null) {
            this.mGnssManagerService.removeGnssMeasurementsListener(iGnssMeasurementsListener);
        }
    }

    public void addGnssAntennaInfoListener(IGnssAntennaInfoListener iGnssAntennaInfoListener, String str, String str2, String str3) {
        if (this.mGnssManagerService != null) {
            this.mGnssManagerService.addGnssAntennaInfoListener(iGnssAntennaInfoListener, str, str2, str3);
        }
    }

    public void removeGnssAntennaInfoListener(IGnssAntennaInfoListener iGnssAntennaInfoListener) {
        if (this.mGnssManagerService != null) {
            this.mGnssManagerService.removeGnssAntennaInfoListener(iGnssAntennaInfoListener);
        }
    }

    @RequiresPermission("android.permission.INTERACT_ACROSS_USERS")
    public void addProviderRequestListener(IProviderRequestListener iProviderRequestListener) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS", null);
        Iterator<LocationProviderManager> it = this.mProviderManagers.iterator();
        while (it.hasNext()) {
            LocationProviderManager next = it.next();
            if (next.isVisibleToCaller()) {
                next.addProviderRequestListener(iProviderRequestListener);
            }
        }
    }

    public void removeProviderRequestListener(IProviderRequestListener iProviderRequestListener) {
        Iterator<LocationProviderManager> it = this.mProviderManagers.iterator();
        while (it.hasNext()) {
            it.next().removeProviderRequestListener(iProviderRequestListener);
        }
    }

    public void injectGnssMeasurementCorrections(GnssMeasurementCorrections gnssMeasurementCorrections) {
        if (this.mGnssManagerService != null) {
            this.mGnssManagerService.injectGnssMeasurementCorrections(gnssMeasurementCorrections);
        }
    }

    public GnssCapabilities getGnssCapabilities() {
        return this.mGnssManagerService == null ? new GnssCapabilities.Builder().build() : this.mGnssManagerService.getGnssCapabilities();
    }

    public List<GnssAntennaInfo> getGnssAntennaInfos() {
        if (this.mGnssManagerService == null) {
            return null;
        }
        return this.mGnssManagerService.getGnssAntennaInfos();
    }

    public void addGnssNavigationMessageListener(IGnssNavigationMessageListener iGnssNavigationMessageListener, String str, String str2, String str3) {
        if (this.mGnssManagerService != null) {
            this.mGnssManagerService.addGnssNavigationMessageListener(iGnssNavigationMessageListener, str, str2, str3);
        }
    }

    public void removeGnssNavigationMessageListener(IGnssNavigationMessageListener iGnssNavigationMessageListener) {
        if (this.mGnssManagerService != null) {
            this.mGnssManagerService.removeGnssNavigationMessageListener(iGnssNavigationMessageListener);
        }
    }

    public void sendExtraCommand(String str, String str2, Bundle bundle) {
        LocationPermissions.enforceCallingOrSelfLocationPermission(this.mContext, 1);
        this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_LOCATION_EXTRA_COMMANDS", null);
        Objects.requireNonNull(str);
        LocationProviderManager locationProviderManager = getLocationProviderManager(str);
        if (locationProviderManager != null) {
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();
            Objects.requireNonNull(str2);
            locationProviderManager.sendExtraCommand(callingUid, callingPid, str2, bundle);
        }
        this.mInjector.getLocationUsageLogger().logLocationApiUsage(0, 5, str);
        this.mInjector.getLocationUsageLogger().logLocationApiUsage(1, 5, str);
    }

    public ProviderProperties getProviderProperties(String str) {
        LocationProviderManager locationProviderManager = getLocationProviderManager(str);
        boolean z = locationProviderManager != null;
        Preconditions.checkArgument(z, "provider \"" + str + "\" does not exist");
        return locationProviderManager.getProperties();
    }

    public boolean isProviderPackage(String str, String str2, String str3) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_DEVICE_CONFIG", null);
        Iterator<LocationProviderManager> it = this.mProviderManagers.iterator();
        while (it.hasNext()) {
            LocationProviderManager next = it.next();
            if (str == null || str.equals(next.getName())) {
                CallerIdentity providerIdentity = next.getProviderIdentity();
                if (providerIdentity != null && providerIdentity.getPackageName().equals(str2) && (str3 == null || Objects.equals(providerIdentity.getAttributionTag(), str3))) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<String> getProviderPackages(String str) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_DEVICE_CONFIG", null);
        LocationProviderManager locationProviderManager = getLocationProviderManager(str);
        if (locationProviderManager == null) {
            return Collections.emptyList();
        }
        CallerIdentity providerIdentity = locationProviderManager.getProviderIdentity();
        if (providerIdentity == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(providerIdentity.getPackageName());
    }

    @EnforcePermission("android.permission.LOCATION_HARDWARE")
    public void setExtraLocationControllerPackage(String str) {
        super.setExtraLocationControllerPackage_enforcePermission();
        synchronized (this.mLock) {
            this.mExtraLocationControllerPackage = str;
        }
    }

    public String getExtraLocationControllerPackage() {
        String str;
        synchronized (this.mLock) {
            str = this.mExtraLocationControllerPackage;
        }
        return str;
    }

    @EnforcePermission("android.permission.LOCATION_HARDWARE")
    public void setExtraLocationControllerPackageEnabled(boolean z) {
        super.setExtraLocationControllerPackageEnabled_enforcePermission();
        synchronized (this.mLock) {
            this.mExtraLocationControllerPackageEnabled = z;
        }
    }

    public boolean isExtraLocationControllerPackageEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mExtraLocationControllerPackageEnabled && this.mExtraLocationControllerPackage != null;
        }
        return z;
    }

    public void setLocationEnabledForUser(boolean z, int i) {
        int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, false, "setLocationEnabledForUser", null);
        this.mContext.enforceCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS", null);
        LocationManager.invalidateLocalLocationEnabledCaches();
        this.mInjector.getSettingsHelper().setLocationEnabled(z, handleIncomingUser);
    }

    public boolean isLocationEnabledForUser(int i) {
        return this.mInjector.getSettingsHelper().isLocationEnabled(ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, false, "isLocationEnabledForUser", null));
    }

    public void setAdasGnssLocationEnabledForUser(final boolean z, int i) {
        int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, false, "setAdasGnssLocationEnabledForUser", null);
        LocationPermissions.enforceCallingOrSelfBypassPermission(this.mContext);
        this.mInjector.getLocationSettings().updateUserSettings(handleIncomingUser, new Function() { // from class: com.android.server.location.LocationManagerService$$ExternalSyntheticLambda9
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                LocationUserSettings withAdasGnssLocationEnabled;
                withAdasGnssLocationEnabled = ((LocationUserSettings) obj).withAdasGnssLocationEnabled(z);
                return withAdasGnssLocationEnabled;
            }
        });
    }

    public boolean isAdasGnssLocationEnabledForUser(int i) {
        return this.mInjector.getLocationSettings().getUserSettings(ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, false, "isAdasGnssLocationEnabledForUser", null)).isAdasGnssLocationEnabled();
    }

    public boolean isProviderEnabledForUser(String str, int i) {
        return this.mLocalService.isProviderEnabledForUser(str, i);
    }

    @EnforcePermission("android.permission.CONTROL_AUTOMOTIVE_GNSS")
    @RequiresPermission("android.permission.CONTROL_AUTOMOTIVE_GNSS")
    public void setAutomotiveGnssSuspended(boolean z) {
        super.setAutomotiveGnssSuspended_enforcePermission();
        if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.automotive")) {
            throw new IllegalStateException("setAutomotiveGnssSuspended only allowed on automotive devices");
        }
        this.mGnssManagerService.setAutomotiveGnssSuspended(z);
    }

    @EnforcePermission("android.permission.CONTROL_AUTOMOTIVE_GNSS")
    @RequiresPermission("android.permission.CONTROL_AUTOMOTIVE_GNSS")
    public boolean isAutomotiveGnssSuspended() {
        super.isAutomotiveGnssSuspended_enforcePermission();
        if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.automotive")) {
            throw new IllegalStateException("isAutomotiveGnssSuspended only allowed on automotive devices");
        }
        return this.mGnssManagerService.isAutomotiveGnssSuspended();
    }

    public boolean geocoderIsPresent() {
        return this.mGeocodeProvider != null;
    }

    public void getFromLocation(double d, double d2, int i, GeocoderParams geocoderParams, IGeocodeListener iGeocodeListener) {
        Preconditions.checkArgument(CallerIdentity.fromBinder(this.mContext, geocoderParams.getClientPackage(), geocoderParams.getClientAttributionTag()).getUid() == geocoderParams.getClientUid());
        GeocoderProxy geocoderProxy = this.mGeocodeProvider;
        if (geocoderProxy != null) {
            geocoderProxy.getFromLocation(d, d2, i, geocoderParams, iGeocodeListener);
            return;
        }
        try {
            iGeocodeListener.onResults((String) null, Collections.emptyList());
        } catch (RemoteException unused) {
        }
    }

    public void getFromLocationName(String str, double d, double d2, double d3, double d4, int i, GeocoderParams geocoderParams, IGeocodeListener iGeocodeListener) {
        Preconditions.checkArgument(CallerIdentity.fromBinder(this.mContext, geocoderParams.getClientPackage(), geocoderParams.getClientAttributionTag()).getUid() == geocoderParams.getClientUid());
        GeocoderProxy geocoderProxy = this.mGeocodeProvider;
        if (geocoderProxy != null) {
            geocoderProxy.getFromLocationName(str, d, d2, d3, d4, i, geocoderParams, iGeocodeListener);
            return;
        }
        try {
            iGeocodeListener.onResults((String) null, Collections.emptyList());
        } catch (RemoteException unused) {
        }
    }

    public void addTestProvider(String str, ProviderProperties providerProperties, List<String> list, String str2, String str3) {
        CallerIdentity fromBinderUnsafe = CallerIdentity.fromBinderUnsafe(str2, str3);
        if (this.mInjector.getAppOpsHelper().noteOp(58, fromBinderUnsafe)) {
            getOrAddLocationProviderManager(str).setMockProvider(new MockLocationProvider(providerProperties, fromBinderUnsafe, new ArraySet(list)));
        }
    }

    public void removeTestProvider(String str, String str2, String str3) {
        if (this.mInjector.getAppOpsHelper().noteOp(58, CallerIdentity.fromBinderUnsafe(str2, str3))) {
            synchronized (this.mLock) {
                LocationProviderManager locationProviderManager = getLocationProviderManager(str);
                if (locationProviderManager == null) {
                    return;
                }
                locationProviderManager.setMockProvider(null);
                if (!locationProviderManager.hasProvider()) {
                    removeLocationProviderManager(locationProviderManager);
                }
            }
        }
    }

    public void setTestProviderLocation(String str, Location location, String str2, String str3) {
        if (this.mInjector.getAppOpsHelper().noteOp(58, CallerIdentity.fromBinderUnsafe(str2, str3))) {
            Preconditions.checkArgument(location.isComplete(), "incomplete location object, missing timestamp or accuracy?");
            LocationProviderManager locationProviderManager = getLocationProviderManager(str);
            if (locationProviderManager == null) {
                throw new IllegalArgumentException("provider doesn't exist: " + str);
            }
            locationProviderManager.setMockProviderLocation(location);
        }
    }

    public void setTestProviderEnabled(String str, boolean z, String str2, String str3) {
        if (this.mInjector.getAppOpsHelper().noteOp(58, CallerIdentity.fromBinderUnsafe(str2, str3))) {
            LocationProviderManager locationProviderManager = getLocationProviderManager(str);
            if (locationProviderManager == null) {
                throw new IllegalArgumentException("provider doesn't exist: " + str);
            }
            locationProviderManager.setMockProviderAllowed(z);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public int handleShellCommand(ParcelFileDescriptor parcelFileDescriptor, ParcelFileDescriptor parcelFileDescriptor2, ParcelFileDescriptor parcelFileDescriptor3, String[] strArr) {
        return new LocationShellCommand(this.mContext, this).exec(this, parcelFileDescriptor.getFileDescriptor(), parcelFileDescriptor2.getFileDescriptor(), parcelFileDescriptor3.getFileDescriptor(), strArr);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpPermission(this.mContext, "LocationManagerService", printWriter)) {
            final IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
            if (strArr.length > 0) {
                LocationProviderManager locationProviderManager = getLocationProviderManager(strArr[0]);
                if (locationProviderManager != null) {
                    indentingPrintWriter.println("Provider:");
                    indentingPrintWriter.increaseIndent();
                    locationProviderManager.dump(fileDescriptor, indentingPrintWriter, strArr);
                    indentingPrintWriter.decreaseIndent();
                    indentingPrintWriter.println("Event Log:");
                    indentingPrintWriter.increaseIndent();
                    LocationEventLog.EVENT_LOG.iterate(new Consumer() { // from class: com.android.server.location.LocationManagerService$$ExternalSyntheticLambda7
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            indentingPrintWriter.println((String) obj);
                        }
                    }, locationProviderManager.getName());
                    indentingPrintWriter.decreaseIndent();
                    return;
                } else if ("--gnssmetrics".equals(strArr[0])) {
                    if (this.mGnssManagerService != null) {
                        this.mGnssManagerService.dump(fileDescriptor, indentingPrintWriter, strArr);
                        return;
                    }
                    return;
                }
            }
            indentingPrintWriter.println("Location Manager State:");
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.println("User Info:");
            indentingPrintWriter.increaseIndent();
            this.mInjector.getUserInfoHelper().dump(fileDescriptor, indentingPrintWriter, strArr);
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println("Location Settings:");
            indentingPrintWriter.increaseIndent();
            this.mInjector.getSettingsHelper().dump(fileDescriptor, indentingPrintWriter, strArr);
            this.mInjector.getLocationSettings().dump(fileDescriptor, indentingPrintWriter, strArr);
            indentingPrintWriter.decreaseIndent();
            synchronized (this.mLock) {
                if (this.mExtraLocationControllerPackage != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Location Controller Extra Package: ");
                    sb.append(this.mExtraLocationControllerPackage);
                    sb.append(this.mExtraLocationControllerPackageEnabled ? " [enabled]" : " [disabled]");
                    indentingPrintWriter.println(sb.toString());
                }
            }
            indentingPrintWriter.println("Location Providers:");
            indentingPrintWriter.increaseIndent();
            Iterator<LocationProviderManager> it = this.mProviderManagers.iterator();
            while (it.hasNext()) {
                it.next().dump(fileDescriptor, indentingPrintWriter, strArr);
            }
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println("Historical Aggregate Location Provider Data:");
            indentingPrintWriter.increaseIndent();
            ArrayMap<String, ArrayMap<CallerIdentity, LocationEventLog.AggregateStats>> copyAggregateStats = LocationEventLog.EVENT_LOG.copyAggregateStats();
            for (int i = 0; i < copyAggregateStats.size(); i++) {
                indentingPrintWriter.print(copyAggregateStats.keyAt(i));
                indentingPrintWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
                indentingPrintWriter.increaseIndent();
                ArrayMap<CallerIdentity, LocationEventLog.AggregateStats> valueAt = copyAggregateStats.valueAt(i);
                for (int i2 = 0; i2 < valueAt.size(); i2++) {
                    indentingPrintWriter.print(valueAt.keyAt(i2));
                    indentingPrintWriter.print(": ");
                    valueAt.valueAt(i2).updateTotals();
                    indentingPrintWriter.println(valueAt.valueAt(i2));
                }
                indentingPrintWriter.decreaseIndent();
            }
            indentingPrintWriter.decreaseIndent();
            if (this.mGnssManagerService != null) {
                indentingPrintWriter.println("GNSS Manager:");
                indentingPrintWriter.increaseIndent();
                this.mGnssManagerService.dump(fileDescriptor, indentingPrintWriter, strArr);
                indentingPrintWriter.decreaseIndent();
            }
            indentingPrintWriter.println("Geofence Manager:");
            indentingPrintWriter.increaseIndent();
            this.mGeofenceManager.dump(fileDescriptor, indentingPrintWriter, strArr);
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println("Event Log:");
            indentingPrintWriter.increaseIndent();
            LocationEventLog.EVENT_LOG.iterate(new Consumer() { // from class: com.android.server.location.LocationManagerService$$ExternalSyntheticLambda7
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    indentingPrintWriter.println((String) obj);
                }
            });
            indentingPrintWriter.decreaseIndent();
        }
    }

    @Override // com.android.server.location.provider.LocationProviderManager.StateChangedListener
    public void onStateChanged(String str, AbstractLocationProvider.State state, AbstractLocationProvider.State state2) {
        if (!Objects.equals(state.identity, state2.identity)) {
            refreshAppOpsRestrictions(-1);
        }
        if (state.extraAttributionTags.equals(state2.extraAttributionTags) && Objects.equals(state.identity, state2.identity)) {
            return;
        }
        synchronized (this.mLock) {
            final LocationManagerInternal.LocationPackageTagsListener locationPackageTagsListener = this.mLocationTagsChangedListener;
            if (locationPackageTagsListener != null) {
                CallerIdentity callerIdentity = state.identity;
                final int uid = callerIdentity != null ? callerIdentity.getUid() : -1;
                CallerIdentity callerIdentity2 = state2.identity;
                final int uid2 = callerIdentity2 != null ? callerIdentity2.getUid() : -1;
                if (uid != -1) {
                    final PackageTagsList calculateAppOpsLocationSourceTags = calculateAppOpsLocationSourceTags(uid);
                    FgThread.getHandler().post(new Runnable() { // from class: com.android.server.location.LocationManagerService$$ExternalSyntheticLambda10
                        @Override // java.lang.Runnable
                        public final void run() {
                            locationPackageTagsListener.onLocationPackageTagsChanged(uid, calculateAppOpsLocationSourceTags);
                        }
                    });
                }
                if (uid2 != -1 && uid2 != uid) {
                    final PackageTagsList calculateAppOpsLocationSourceTags2 = calculateAppOpsLocationSourceTags(uid2);
                    FgThread.getHandler().post(new Runnable() { // from class: com.android.server.location.LocationManagerService$$ExternalSyntheticLambda11
                        @Override // java.lang.Runnable
                        public final void run() {
                            locationPackageTagsListener.onLocationPackageTagsChanged(uid2, calculateAppOpsLocationSourceTags2);
                        }
                    });
                }
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void refreshAppOpsRestrictions(int i) {
        PackageTagsList packageTagsList;
        if (i == -1) {
            for (int i2 : this.mInjector.getUserInfoHelper().getRunningUserIds()) {
                refreshAppOpsRestrictions(i2);
            }
            return;
        }
        Preconditions.checkArgument(i >= 0);
        boolean isLocationEnabled = this.mInjector.getSettingsHelper().isLocationEnabled(i);
        if (isLocationEnabled) {
            packageTagsList = null;
        } else {
            PackageTagsList.Builder builder = new PackageTagsList.Builder();
            Iterator<LocationProviderManager> it = this.mProviderManagers.iterator();
            while (it.hasNext()) {
                CallerIdentity providerIdentity = it.next().getProviderIdentity();
                if (providerIdentity != null) {
                    builder.add(providerIdentity.getPackageName(), providerIdentity.getAttributionTag());
                }
            }
            builder.add(this.mInjector.getSettingsHelper().getIgnoreSettingsAllowlist());
            builder.add(this.mInjector.getSettingsHelper().getAdasAllowlist());
            packageTagsList = builder.build();
        }
        AppOpsManager appOpsManager = (AppOpsManager) this.mContext.getSystemService(AppOpsManager.class);
        Objects.requireNonNull(appOpsManager);
        appOpsManager.setUserRestrictionForUser(0, !isLocationEnabled, this, packageTagsList, i);
        appOpsManager.setUserRestrictionForUser(1, !isLocationEnabled, this, packageTagsList, i);
    }

    public PackageTagsList calculateAppOpsLocationSourceTags(int i) {
        PackageTagsList.Builder builder = new PackageTagsList.Builder();
        Iterator<LocationProviderManager> it = this.mProviderManagers.iterator();
        while (it.hasNext()) {
            LocationProviderManager next = it.next();
            AbstractLocationProvider.State state = next.getState();
            CallerIdentity callerIdentity = state.identity;
            if (callerIdentity != null && callerIdentity.getUid() == i) {
                builder.add(state.identity.getPackageName(), state.extraAttributionTags);
                if (state.extraAttributionTags.isEmpty() || state.identity.getAttributionTag() != null) {
                    builder.add(state.identity.getPackageName(), state.identity.getAttributionTag());
                } else {
                    Log.e("LocationManagerService", next.getName() + " provider has specified a null attribution tag and a non-empty set of extra attribution tags - dropping the null attribution tag");
                }
            }
        }
        return builder.build();
    }

    /* loaded from: classes.dex */
    public class LocalService extends LocationManagerInternal {
        public LocalService() {
        }

        public boolean isProviderEnabledForUser(String str, int i) {
            int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, false, "isProviderEnabledForUser", null);
            LocationProviderManager locationProviderManager = LocationManagerService.this.getLocationProviderManager(str);
            if (locationProviderManager == null) {
                return false;
            }
            return locationProviderManager.isEnabled(handleIncomingUser);
        }

        public void addProviderEnabledListener(String str, LocationManagerInternal.ProviderEnabledListener providerEnabledListener) {
            LocationProviderManager locationProviderManager = LocationManagerService.this.getLocationProviderManager(str);
            Objects.requireNonNull(locationProviderManager);
            locationProviderManager.addEnabledListener(providerEnabledListener);
        }

        public void removeProviderEnabledListener(String str, LocationManagerInternal.ProviderEnabledListener providerEnabledListener) {
            LocationProviderManager locationProviderManager = LocationManagerService.this.getLocationProviderManager(str);
            Objects.requireNonNull(locationProviderManager);
            locationProviderManager.removeEnabledListener(providerEnabledListener);
        }

        public boolean isProvider(String str, CallerIdentity callerIdentity) {
            Iterator<LocationProviderManager> it = LocationManagerService.this.mProviderManagers.iterator();
            while (it.hasNext()) {
                LocationProviderManager next = it.next();
                if (str == null || str.equals(next.getName())) {
                    if (callerIdentity.equals(next.getProviderIdentity()) && next.isVisibleToCaller()) {
                        return true;
                    }
                }
            }
            return false;
        }

        public void sendNiResponse(int i, int i2) {
            if (LocationManagerService.this.mGnssManagerService != null) {
                LocationManagerService.this.mGnssManagerService.sendNiResponse(i, i2);
            }
        }

        public LocationTime getGnssTimeMillis() {
            Location lastLocationUnsafe;
            LocationProviderManager locationProviderManager = LocationManagerService.this.getLocationProviderManager("gps");
            if (locationProviderManager == null || (lastLocationUnsafe = locationProviderManager.getLastLocationUnsafe(-1, 2, false, Long.MAX_VALUE)) == null) {
                return null;
            }
            return new LocationTime(lastLocationUnsafe.getTime(), lastLocationUnsafe.getElapsedRealtimeNanos());
        }

        public void setLocationPackageTagsListener(final LocationManagerInternal.LocationPackageTagsListener locationPackageTagsListener) {
            synchronized (LocationManagerService.this.mLock) {
                LocationManagerService.this.mLocationTagsChangedListener = locationPackageTagsListener;
                if (locationPackageTagsListener != null) {
                    ArraySet arraySet = new ArraySet(LocationManagerService.this.mProviderManagers.size());
                    Iterator<LocationProviderManager> it = LocationManagerService.this.mProviderManagers.iterator();
                    while (it.hasNext()) {
                        CallerIdentity providerIdentity = it.next().getProviderIdentity();
                        if (providerIdentity != null) {
                            arraySet.add(Integer.valueOf(providerIdentity.getUid()));
                        }
                    }
                    Iterator it2 = arraySet.iterator();
                    while (it2.hasNext()) {
                        final int intValue = ((Integer) it2.next()).intValue();
                        final PackageTagsList calculateAppOpsLocationSourceTags = LocationManagerService.this.calculateAppOpsLocationSourceTags(intValue);
                        if (!calculateAppOpsLocationSourceTags.isEmpty()) {
                            FgThread.getHandler().post(new Runnable() { // from class: com.android.server.location.LocationManagerService$LocalService$$ExternalSyntheticLambda0
                                @Override // java.lang.Runnable
                                public final void run() {
                                    locationPackageTagsListener.onLocationPackageTagsChanged(intValue, calculateAppOpsLocationSourceTags);
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class SystemInjector implements Injector {
        public final AlarmHelper mAlarmHelper;
        public final SystemAppForegroundHelper mAppForegroundHelper;
        public final SystemAppOpsHelper mAppOpsHelper;
        public final Context mContext;
        public final SystemDeviceIdleHelper mDeviceIdleHelper;
        public final SystemDeviceStationaryHelper mDeviceStationaryHelper;
        @GuardedBy({"this"})
        public SystemEmergencyHelper mEmergencyCallHelper;
        public final SystemLocationPermissionsHelper mLocationPermissionsHelper;
        public final SystemLocationPowerSaveModeHelper mLocationPowerSaveModeHelper;
        public final LocationSettings mLocationSettings;
        public final LocationUsageLogger mLocationUsageLogger;
        public final PackageResetHelper mPackageResetHelper;
        public final SystemScreenInteractiveHelper mScreenInteractiveHelper;
        public final SystemSettingsHelper mSettingsHelper;
        @GuardedBy({"this"})
        public boolean mSystemReady;
        public final SystemUserInfoHelper mUserInfoHelper;

        public SystemInjector(Context context, SystemUserInfoHelper systemUserInfoHelper) {
            this.mContext = context;
            this.mUserInfoHelper = systemUserInfoHelper;
            this.mLocationSettings = new LocationSettings(context);
            this.mAlarmHelper = new SystemAlarmHelper(context);
            SystemAppOpsHelper systemAppOpsHelper = new SystemAppOpsHelper(context);
            this.mAppOpsHelper = systemAppOpsHelper;
            this.mLocationPermissionsHelper = new SystemLocationPermissionsHelper(context, systemAppOpsHelper);
            this.mSettingsHelper = new SystemSettingsHelper(context);
            this.mAppForegroundHelper = new SystemAppForegroundHelper(context);
            this.mLocationPowerSaveModeHelper = new SystemLocationPowerSaveModeHelper(context);
            this.mScreenInteractiveHelper = new SystemScreenInteractiveHelper(context);
            this.mDeviceStationaryHelper = new SystemDeviceStationaryHelper();
            this.mDeviceIdleHelper = new SystemDeviceIdleHelper(context);
            this.mLocationUsageLogger = new LocationUsageLogger();
            this.mPackageResetHelper = new SystemPackageResetHelper(context);
        }

        public synchronized void onSystemReady() {
            this.mUserInfoHelper.onSystemReady();
            this.mAppOpsHelper.onSystemReady();
            this.mLocationPermissionsHelper.onSystemReady();
            this.mSettingsHelper.onSystemReady();
            this.mAppForegroundHelper.onSystemReady();
            this.mLocationPowerSaveModeHelper.onSystemReady();
            this.mScreenInteractiveHelper.onSystemReady();
            this.mDeviceStationaryHelper.onSystemReady();
            this.mDeviceIdleHelper.onSystemReady();
            SystemEmergencyHelper systemEmergencyHelper = this.mEmergencyCallHelper;
            if (systemEmergencyHelper != null) {
                systemEmergencyHelper.onSystemReady();
            }
            this.mSystemReady = true;
        }

        @Override // com.android.server.location.injector.Injector
        public UserInfoHelper getUserInfoHelper() {
            return this.mUserInfoHelper;
        }

        @Override // com.android.server.location.injector.Injector
        public LocationSettings getLocationSettings() {
            return this.mLocationSettings;
        }

        @Override // com.android.server.location.injector.Injector
        public AlarmHelper getAlarmHelper() {
            return this.mAlarmHelper;
        }

        @Override // com.android.server.location.injector.Injector
        public AppOpsHelper getAppOpsHelper() {
            return this.mAppOpsHelper;
        }

        @Override // com.android.server.location.injector.Injector
        public LocationPermissionsHelper getLocationPermissionsHelper() {
            return this.mLocationPermissionsHelper;
        }

        @Override // com.android.server.location.injector.Injector
        public SettingsHelper getSettingsHelper() {
            return this.mSettingsHelper;
        }

        @Override // com.android.server.location.injector.Injector
        public AppForegroundHelper getAppForegroundHelper() {
            return this.mAppForegroundHelper;
        }

        @Override // com.android.server.location.injector.Injector
        public LocationPowerSaveModeHelper getLocationPowerSaveModeHelper() {
            return this.mLocationPowerSaveModeHelper;
        }

        @Override // com.android.server.location.injector.Injector
        public ScreenInteractiveHelper getScreenInteractiveHelper() {
            return this.mScreenInteractiveHelper;
        }

        @Override // com.android.server.location.injector.Injector
        public DeviceStationaryHelper getDeviceStationaryHelper() {
            return this.mDeviceStationaryHelper;
        }

        @Override // com.android.server.location.injector.Injector
        public DeviceIdleHelper getDeviceIdleHelper() {
            return this.mDeviceIdleHelper;
        }

        @Override // com.android.server.location.injector.Injector
        public synchronized EmergencyHelper getEmergencyHelper() {
            if (this.mEmergencyCallHelper == null) {
                SystemEmergencyHelper systemEmergencyHelper = new SystemEmergencyHelper(this.mContext);
                this.mEmergencyCallHelper = systemEmergencyHelper;
                if (this.mSystemReady) {
                    systemEmergencyHelper.onSystemReady();
                }
            }
            return this.mEmergencyCallHelper;
        }

        @Override // com.android.server.location.injector.Injector
        public LocationUsageLogger getLocationUsageLogger() {
            return this.mLocationUsageLogger;
        }

        @Override // com.android.server.location.injector.Injector
        public PackageResetHelper getPackageResetHelper() {
            return this.mPackageResetHelper;
        }
    }
}
