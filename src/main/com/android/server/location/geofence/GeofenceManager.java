package com.android.server.location.geofence;

import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Geofence;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.location.util.identity.CallerIdentity;
import android.os.Binder;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.WorkSource;
import android.util.ArraySet;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.listeners.ListenerExecutor;
import com.android.server.FgThread;
import com.android.server.PendingIntentUtils;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.location.LocationPermissions;
import com.android.server.location.geofence.GeofenceManager;
import com.android.server.location.injector.Injector;
import com.android.server.location.injector.LocationPermissionsHelper;
import com.android.server.location.injector.LocationUsageLogger;
import com.android.server.location.injector.SettingsHelper;
import com.android.server.location.injector.UserInfoHelper;
import com.android.server.location.listeners.ListenerMultiplexer;
import com.android.server.location.listeners.PendingIntentListenerRegistration;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class GeofenceManager extends ListenerMultiplexer<GeofenceKey, PendingIntent, GeofenceRegistration, LocationRequest> implements LocationListener {
    public final Context mContext;
    @GuardedBy({"mLock"})
    public Location mLastLocation;
    @GuardedBy({"mLock"})
    public LocationManager mLocationManager;
    public final LocationPermissionsHelper mLocationPermissionsHelper;
    public final LocationUsageLogger mLocationUsageLogger;
    public final SettingsHelper mSettingsHelper;
    public final UserInfoHelper mUserInfoHelper;
    public final Object mLock = new Object();
    public final UserInfoHelper.UserListener mUserChangedListener = new UserInfoHelper.UserListener() { // from class: com.android.server.location.geofence.GeofenceManager$$ExternalSyntheticLambda2
        @Override // com.android.server.location.injector.UserInfoHelper.UserListener
        public final void onUserChanged(int i, int i2) {
            GeofenceManager.this.onUserChanged(i, i2);
        }
    };
    public final SettingsHelper.UserSettingChangedListener mLocationEnabledChangedListener = new SettingsHelper.UserSettingChangedListener() { // from class: com.android.server.location.geofence.GeofenceManager$$ExternalSyntheticLambda3
        @Override // com.android.server.location.injector.SettingsHelper.UserSettingChangedListener
        public final void onSettingChanged(int i) {
            GeofenceManager.this.onLocationEnabledChanged(i);
        }
    };
    public final SettingsHelper.UserSettingChangedListener mLocationPackageBlacklistChangedListener = new SettingsHelper.UserSettingChangedListener() { // from class: com.android.server.location.geofence.GeofenceManager$$ExternalSyntheticLambda4
        @Override // com.android.server.location.injector.SettingsHelper.UserSettingChangedListener
        public final void onSettingChanged(int i) {
            GeofenceManager.this.onLocationPackageBlacklistChanged(i);
        }
    };
    public final LocationPermissionsHelper.LocationPermissionsListener mLocationPermissionsListener = new LocationPermissionsHelper.LocationPermissionsListener() { // from class: com.android.server.location.geofence.GeofenceManager.1
        @Override // com.android.server.location.injector.LocationPermissionsHelper.LocationPermissionsListener
        public void onLocationPermissionsChanged(String str) {
            GeofenceManager.this.onLocationPermissionsChanged(str);
        }

        @Override // com.android.server.location.injector.LocationPermissionsHelper.LocationPermissionsListener
        public void onLocationPermissionsChanged(int i) {
            GeofenceManager.this.onLocationPermissionsChanged(i);
        }
    };

    /* loaded from: classes.dex */
    public static class GeofenceKey {
        public final Geofence mGeofence;
        public final PendingIntent mPendingIntent;

        public GeofenceKey(PendingIntent pendingIntent, Geofence geofence) {
            Objects.requireNonNull(pendingIntent);
            this.mPendingIntent = pendingIntent;
            Objects.requireNonNull(geofence);
            this.mGeofence = geofence;
        }

        public PendingIntent getPendingIntent() {
            return this.mPendingIntent;
        }

        public boolean equals(Object obj) {
            if (obj instanceof GeofenceKey) {
                GeofenceKey geofenceKey = (GeofenceKey) obj;
                return this.mPendingIntent.equals(geofenceKey.mPendingIntent) && this.mGeofence.equals(geofenceKey.mGeofence);
            }
            return false;
        }

        public int hashCode() {
            return this.mPendingIntent.hashCode();
        }
    }

    /* loaded from: classes.dex */
    public class GeofenceRegistration extends PendingIntentListenerRegistration<GeofenceKey, PendingIntent> {
        public Location mCachedLocation;
        public float mCachedLocationDistanceM;
        public final Location mCenter;
        public final Geofence mGeofence;
        public int mGeofenceState;
        public final CallerIdentity mIdentity;
        public boolean mPermitted;
        public final PowerManager.WakeLock mWakeLock;

        @Override // com.android.server.location.listeners.ListenerRegistration
        public String getTag() {
            return "GeofenceManager";
        }

        public GeofenceRegistration(Geofence geofence, CallerIdentity callerIdentity, PendingIntent pendingIntent) {
            super(pendingIntent);
            this.mGeofence = geofence;
            this.mIdentity = callerIdentity;
            Location location = new Location("");
            this.mCenter = location;
            location.setLatitude(geofence.getLatitude());
            location.setLongitude(geofence.getLongitude());
            PowerManager powerManager = (PowerManager) GeofenceManager.this.mContext.getSystemService(PowerManager.class);
            Objects.requireNonNull(powerManager);
            PowerManager.WakeLock newWakeLock = powerManager.newWakeLock(1, "GeofenceManager:" + callerIdentity.getPackageName());
            this.mWakeLock = newWakeLock;
            newWakeLock.setReferenceCounted(true);
            newWakeLock.setWorkSource(callerIdentity.addToWorkSource((WorkSource) null));
        }

        public Geofence getGeofence() {
            return this.mGeofence;
        }

        public CallerIdentity getIdentity() {
            return this.mIdentity;
        }

        @Override // com.android.server.location.listeners.PendingIntentListenerRegistration
        public PendingIntent getPendingIntentFromKey(GeofenceKey geofenceKey) {
            return geofenceKey.getPendingIntent();
        }

        @Override // com.android.server.location.listeners.RemovableListenerRegistration
        public GeofenceManager getOwner() {
            return GeofenceManager.this;
        }

        @Override // com.android.server.location.listeners.PendingIntentListenerRegistration, com.android.server.location.listeners.RemovableListenerRegistration
        public void onRegister() {
            super.onRegister();
            this.mGeofenceState = 0;
            this.mPermitted = GeofenceManager.this.mLocationPermissionsHelper.hasLocationPermissions(2, this.mIdentity);
        }

        @Override // com.android.server.location.listeners.ListenerRegistration
        public void onActive() {
            Location lastLocation = GeofenceManager.this.getLastLocation();
            if (lastLocation != null) {
                executeOperation(onLocationChanged(lastLocation));
            }
        }

        public boolean isPermitted() {
            return this.mPermitted;
        }

        public boolean onLocationPermissionsChanged(String str) {
            if (str == null || this.mIdentity.getPackageName().equals(str)) {
                return onLocationPermissionsChanged();
            }
            return false;
        }

        public boolean onLocationPermissionsChanged(int i) {
            if (this.mIdentity.getUid() == i) {
                return onLocationPermissionsChanged();
            }
            return false;
        }

        public final boolean onLocationPermissionsChanged() {
            boolean hasLocationPermissions = GeofenceManager.this.mLocationPermissionsHelper.hasLocationPermissions(2, this.mIdentity);
            if (hasLocationPermissions != this.mPermitted) {
                this.mPermitted = hasLocationPermissions;
                return true;
            }
            return false;
        }

        public double getDistanceToBoundary(Location location) {
            if (!location.equals(this.mCachedLocation)) {
                this.mCachedLocation = location;
                this.mCachedLocationDistanceM = this.mCenter.distanceTo(location);
            }
            return Math.abs(this.mGeofence.getRadius() - this.mCachedLocationDistanceM);
        }

        public ListenerExecutor.ListenerOperation<PendingIntent> onLocationChanged(Location location) {
            if (this.mGeofence.isExpired()) {
                remove();
                return null;
            }
            this.mCachedLocation = location;
            this.mCachedLocationDistanceM = this.mCenter.distanceTo(location);
            int i = this.mGeofenceState;
            if (this.mCachedLocationDistanceM <= Math.max(this.mGeofence.getRadius(), location.getAccuracy())) {
                this.mGeofenceState = 1;
                if (i != 1) {
                    return new ListenerExecutor.ListenerOperation() { // from class: com.android.server.location.geofence.GeofenceManager$GeofenceRegistration$$ExternalSyntheticLambda0
                        public final void operate(Object obj) {
                            GeofenceManager.GeofenceRegistration.this.lambda$onLocationChanged$0((PendingIntent) obj);
                        }
                    };
                }
            } else {
                this.mGeofenceState = 2;
                if (i == 1) {
                    return new ListenerExecutor.ListenerOperation() { // from class: com.android.server.location.geofence.GeofenceManager$GeofenceRegistration$$ExternalSyntheticLambda1
                        public final void operate(Object obj) {
                            GeofenceManager.GeofenceRegistration.this.lambda$onLocationChanged$1((PendingIntent) obj);
                        }
                    };
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onLocationChanged$0(PendingIntent pendingIntent) throws Exception {
            sendIntent(pendingIntent, true);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onLocationChanged$1(PendingIntent pendingIntent) throws Exception {
            sendIntent(pendingIntent, false);
        }

        public final void sendIntent(PendingIntent pendingIntent, boolean z) {
            Intent putExtra = new Intent().putExtra("entering", z);
            this.mWakeLock.acquire(30000L);
            try {
                pendingIntent.send(GeofenceManager.this.mContext, 0, putExtra, new PendingIntent.OnFinished() { // from class: com.android.server.location.geofence.GeofenceManager$GeofenceRegistration$$ExternalSyntheticLambda2
                    @Override // android.app.PendingIntent.OnFinished
                    public final void onSendFinished(PendingIntent pendingIntent2, Intent intent, int i, String str, Bundle bundle) {
                        GeofenceManager.GeofenceRegistration.this.lambda$sendIntent$2(pendingIntent2, intent, i, str, bundle);
                    }
                }, null, null, PendingIntentUtils.createDontSendToRestrictedAppsBundle(null));
            } catch (PendingIntent.CanceledException unused) {
                this.mWakeLock.release();
                GeofenceManager.this.removeRegistration(new GeofenceKey(pendingIntent, this.mGeofence), this);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$sendIntent$2(PendingIntent pendingIntent, Intent intent, int i, String str, Bundle bundle) {
            this.mWakeLock.release();
        }

        @Override // com.android.server.location.listeners.ListenerRegistration
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.mIdentity);
            ArraySet arraySet = new ArraySet(1);
            if (!this.mPermitted) {
                arraySet.add("na");
            }
            if (!arraySet.isEmpty()) {
                sb.append(" ");
                sb.append(arraySet);
            }
            sb.append(" ");
            sb.append(this.mGeofence);
            return sb.toString();
        }
    }

    public GeofenceManager(Context context, Injector injector) {
        this.mContext = context.createAttributionContext("GeofencingService");
        this.mUserInfoHelper = injector.getUserInfoHelper();
        this.mSettingsHelper = injector.getSettingsHelper();
        this.mLocationPermissionsHelper = injector.getLocationPermissionsHelper();
        this.mLocationUsageLogger = injector.getLocationUsageLogger();
    }

    public final LocationManager getLocationManager() {
        LocationManager locationManager;
        synchronized (this.mLock) {
            if (this.mLocationManager == null) {
                LocationManager locationManager2 = (LocationManager) this.mContext.getSystemService(LocationManager.class);
                Objects.requireNonNull(locationManager2);
                LocationManager locationManager3 = locationManager2;
                this.mLocationManager = locationManager2;
            }
            locationManager = this.mLocationManager;
        }
        return locationManager;
    }

    public void addGeofence(Geofence geofence, PendingIntent pendingIntent, String str, String str2) {
        LocationPermissions.enforceCallingOrSelfLocationPermission(this.mContext, 2);
        CallerIdentity fromBinder = CallerIdentity.fromBinder(this.mContext, str, str2, AppOpsManager.toReceiverId(pendingIntent));
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            putRegistration(new GeofenceKey(pendingIntent, geofence), new GeofenceRegistration(geofence, fromBinder, pendingIntent));
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void removeGeofence(final PendingIntent pendingIntent) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            removeRegistrationIf(new Predicate() { // from class: com.android.server.location.geofence.GeofenceManager$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$removeGeofence$0;
                    lambda$removeGeofence$0 = GeofenceManager.lambda$removeGeofence$0(pendingIntent, (GeofenceManager.GeofenceKey) obj);
                    return lambda$removeGeofence$0;
                }
            });
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public static /* synthetic */ boolean lambda$removeGeofence$0(PendingIntent pendingIntent, GeofenceKey geofenceKey) {
        return geofenceKey.getPendingIntent().equals(pendingIntent);
    }

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public boolean isActive(GeofenceRegistration geofenceRegistration) {
        return geofenceRegistration.isPermitted() && isActive(geofenceRegistration.getIdentity());
    }

    public final boolean isActive(CallerIdentity callerIdentity) {
        return callerIdentity.isSystemServer() ? this.mSettingsHelper.isLocationEnabled(this.mUserInfoHelper.getCurrentUserId()) : this.mSettingsHelper.isLocationEnabled(callerIdentity.getUserId()) && this.mUserInfoHelper.isVisibleUserId(callerIdentity.getUserId()) && !this.mSettingsHelper.isLocationPackageBlacklisted(callerIdentity.getUserId(), callerIdentity.getPackageName());
    }

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public void onRegister() {
        this.mUserInfoHelper.addListener(this.mUserChangedListener);
        this.mSettingsHelper.addOnLocationEnabledChangedListener(this.mLocationEnabledChangedListener);
        this.mSettingsHelper.addOnLocationPackageBlacklistChangedListener(this.mLocationPackageBlacklistChangedListener);
        this.mLocationPermissionsHelper.addListener(this.mLocationPermissionsListener);
    }

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public void onUnregister() {
        this.mUserInfoHelper.removeListener(this.mUserChangedListener);
        this.mSettingsHelper.removeOnLocationEnabledChangedListener(this.mLocationEnabledChangedListener);
        this.mSettingsHelper.removeOnLocationPackageBlacklistChangedListener(this.mLocationPackageBlacklistChangedListener);
        this.mLocationPermissionsHelper.removeListener(this.mLocationPermissionsListener);
    }

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public void onRegistrationAdded(GeofenceKey geofenceKey, GeofenceRegistration geofenceRegistration) {
        this.mLocationUsageLogger.logLocationApiUsage(1, 4, geofenceRegistration.getIdentity().getPackageName(), geofenceRegistration.getIdentity().getAttributionTag(), null, null, false, true, geofenceRegistration.getGeofence(), true);
    }

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public void onRegistrationRemoved(GeofenceKey geofenceKey, GeofenceRegistration geofenceRegistration) {
        this.mLocationUsageLogger.logLocationApiUsage(1, 4, geofenceRegistration.getIdentity().getPackageName(), geofenceRegistration.getIdentity().getAttributionTag(), null, null, false, true, geofenceRegistration.getGeofence(), true);
    }

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public boolean registerWithService(LocationRequest locationRequest, Collection<GeofenceRegistration> collection) {
        getLocationManager().requestLocationUpdates("fused", locationRequest, FgThread.getExecutor(), this);
        return true;
    }

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public void unregisterWithService() {
        synchronized (this.mLock) {
            getLocationManager().removeUpdates(this);
            this.mLastLocation = null;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public LocationRequest mergeRegistrations(Collection<GeofenceRegistration> collection) {
        long backgroundThrottleProximityAlertIntervalMs;
        Location lastLocation = getLastLocation();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        WorkSource workSource = null;
        double d = Double.MAX_VALUE;
        for (GeofenceRegistration geofenceRegistration : collection) {
            if (!geofenceRegistration.getGeofence().isExpired(elapsedRealtime)) {
                workSource = geofenceRegistration.getIdentity().addToWorkSource(workSource);
                if (lastLocation != null) {
                    double distanceToBoundary = geofenceRegistration.getDistanceToBoundary(lastLocation);
                    if (distanceToBoundary < d) {
                        d = distanceToBoundary;
                    }
                }
            }
        }
        if (Double.compare(d, Double.MAX_VALUE) < 0) {
            backgroundThrottleProximityAlertIntervalMs = (long) Math.min(7200000.0d, Math.max(this.mSettingsHelper.getBackgroundThrottleProximityAlertIntervalMs(), (d * 1000.0d) / 100.0d));
        } else {
            backgroundThrottleProximityAlertIntervalMs = this.mSettingsHelper.getBackgroundThrottleProximityAlertIntervalMs();
        }
        return new LocationRequest.Builder(backgroundThrottleProximityAlertIntervalMs).setMinUpdateIntervalMillis(0L).setHiddenFromAppOps(true).setWorkSource(workSource).build();
    }

    @Override // android.location.LocationListener
    public void onLocationChanged(final Location location) {
        synchronized (this.mLock) {
            this.mLastLocation = location;
        }
        deliverToListeners(new Function() { // from class: com.android.server.location.geofence.GeofenceManager$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                ListenerExecutor.ListenerOperation onLocationChanged;
                onLocationChanged = ((GeofenceManager.GeofenceRegistration) obj).onLocationChanged(location);
                return onLocationChanged;
            }
        });
        updateService();
    }

    public Location getLastLocation() {
        Location location;
        synchronized (this.mLock) {
            location = this.mLastLocation;
        }
        if (location == null) {
            location = getLocationManager().getLastLocation();
        }
        if (location == null || location.getElapsedRealtimeAgeMillis() <= BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS) {
            return location;
        }
        return null;
    }

    public static /* synthetic */ boolean lambda$onUserChanged$2(int i, GeofenceRegistration geofenceRegistration) {
        return geofenceRegistration.getIdentity().getUserId() == i;
    }

    public void onUserChanged(final int i, int i2) {
        if (i2 == 1 || i2 == 4) {
            updateRegistrations(new Predicate() { // from class: com.android.server.location.geofence.GeofenceManager$$ExternalSyntheticLambda8
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$onUserChanged$2;
                    lambda$onUserChanged$2 = GeofenceManager.lambda$onUserChanged$2(i, (GeofenceManager.GeofenceRegistration) obj);
                    return lambda$onUserChanged$2;
                }
            });
        }
    }

    public static /* synthetic */ boolean lambda$onLocationEnabledChanged$3(int i, GeofenceRegistration geofenceRegistration) {
        return geofenceRegistration.getIdentity().getUserId() == i;
    }

    public void onLocationEnabledChanged(final int i) {
        updateRegistrations(new Predicate() { // from class: com.android.server.location.geofence.GeofenceManager$$ExternalSyntheticLambda6
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$onLocationEnabledChanged$3;
                lambda$onLocationEnabledChanged$3 = GeofenceManager.lambda$onLocationEnabledChanged$3(i, (GeofenceManager.GeofenceRegistration) obj);
                return lambda$onLocationEnabledChanged$3;
            }
        });
    }

    public static /* synthetic */ boolean lambda$onLocationPackageBlacklistChanged$4(int i, GeofenceRegistration geofenceRegistration) {
        return geofenceRegistration.getIdentity().getUserId() == i;
    }

    public void onLocationPackageBlacklistChanged(final int i) {
        updateRegistrations(new Predicate() { // from class: com.android.server.location.geofence.GeofenceManager$$ExternalSyntheticLambda5
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$onLocationPackageBlacklistChanged$4;
                lambda$onLocationPackageBlacklistChanged$4 = GeofenceManager.lambda$onLocationPackageBlacklistChanged$4(i, (GeofenceManager.GeofenceRegistration) obj);
                return lambda$onLocationPackageBlacklistChanged$4;
            }
        });
    }

    public void onLocationPermissionsChanged(final String str) {
        updateRegistrations(new Predicate() { // from class: com.android.server.location.geofence.GeofenceManager$$ExternalSyntheticLambda7
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean onLocationPermissionsChanged;
                onLocationPermissionsChanged = ((GeofenceManager.GeofenceRegistration) obj).onLocationPermissionsChanged(str);
                return onLocationPermissionsChanged;
            }
        });
    }

    public void onLocationPermissionsChanged(final int i) {
        updateRegistrations(new Predicate() { // from class: com.android.server.location.geofence.GeofenceManager$$ExternalSyntheticLambda9
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean onLocationPermissionsChanged;
                onLocationPermissionsChanged = ((GeofenceManager.GeofenceRegistration) obj).onLocationPermissionsChanged(i);
                return onLocationPermissionsChanged;
            }
        });
    }
}
