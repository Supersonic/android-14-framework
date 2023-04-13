package com.android.server.location.gnss;

import android.location.LocationManagerInternal;
import android.location.util.identity.CallerIdentity;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.IInterface;
import android.util.ArraySet;
import com.android.internal.util.ConcurrentUtils;
import com.android.internal.util.Preconditions;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.location.gnss.GnssListenerMultiplexer;
import com.android.server.location.injector.AppForegroundHelper;
import com.android.server.location.injector.Injector;
import com.android.server.location.injector.LocationPermissionsHelper;
import com.android.server.location.injector.PackageResetHelper;
import com.android.server.location.injector.SettingsHelper;
import com.android.server.location.injector.UserInfoHelper;
import com.android.server.location.listeners.BinderListenerRegistration;
import com.android.server.location.listeners.ListenerMultiplexer;
import com.android.server.location.listeners.ListenerRegistration;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public abstract class GnssListenerMultiplexer<TRequest, TListener extends IInterface, TMergedRegistration> extends ListenerMultiplexer<IBinder, TListener, GnssListenerMultiplexer<TRequest, TListener, TMergedRegistration>.GnssListenerRegistration, TMergedRegistration> {
    public final AppForegroundHelper mAppForegroundHelper;
    public final LocationManagerInternal mLocationManagerInternal;
    public final LocationPermissionsHelper mLocationPermissionsHelper;
    public final PackageResetHelper mPackageResetHelper;
    public final SettingsHelper mSettingsHelper;
    public final UserInfoHelper mUserInfoHelper;
    public final UserInfoHelper.UserListener mUserChangedListener = new UserInfoHelper.UserListener() { // from class: com.android.server.location.gnss.GnssListenerMultiplexer$$ExternalSyntheticLambda0
        @Override // com.android.server.location.injector.UserInfoHelper.UserListener
        public final void onUserChanged(int i, int i2) {
            GnssListenerMultiplexer.this.onUserChanged(i, i2);
        }
    };
    public final LocationManagerInternal.ProviderEnabledListener mProviderEnabledChangedListener = new LocationManagerInternal.ProviderEnabledListener() { // from class: com.android.server.location.gnss.GnssListenerMultiplexer$$ExternalSyntheticLambda1
        public final void onProviderEnabledChanged(String str, int i, boolean z) {
            GnssListenerMultiplexer.this.onProviderEnabledChanged(str, i, z);
        }
    };
    public final SettingsHelper.GlobalSettingChangedListener mBackgroundThrottlePackageWhitelistChangedListener = new SettingsHelper.GlobalSettingChangedListener() { // from class: com.android.server.location.gnss.GnssListenerMultiplexer$$ExternalSyntheticLambda2
        @Override // com.android.server.location.injector.SettingsHelper.GlobalSettingChangedListener
        public final void onSettingChanged() {
            GnssListenerMultiplexer.this.onBackgroundThrottlePackageWhitelistChanged();
        }
    };
    public final SettingsHelper.UserSettingChangedListener mLocationPackageBlacklistChangedListener = new SettingsHelper.UserSettingChangedListener() { // from class: com.android.server.location.gnss.GnssListenerMultiplexer$$ExternalSyntheticLambda3
        @Override // com.android.server.location.injector.SettingsHelper.UserSettingChangedListener
        public final void onSettingChanged(int i) {
            GnssListenerMultiplexer.this.onLocationPackageBlacklistChanged(i);
        }
    };
    public final LocationPermissionsHelper.LocationPermissionsListener mLocationPermissionsListener = new LocationPermissionsHelper.LocationPermissionsListener() { // from class: com.android.server.location.gnss.GnssListenerMultiplexer.1
        @Override // com.android.server.location.injector.LocationPermissionsHelper.LocationPermissionsListener
        public void onLocationPermissionsChanged(String str) {
            GnssListenerMultiplexer.this.onLocationPermissionsChanged(str);
        }

        @Override // com.android.server.location.injector.LocationPermissionsHelper.LocationPermissionsListener
        public void onLocationPermissionsChanged(int i) {
            GnssListenerMultiplexer.this.onLocationPermissionsChanged(i);
        }
    };
    public final AppForegroundHelper.AppForegroundListener mAppForegroundChangedListener = new AppForegroundHelper.AppForegroundListener() { // from class: com.android.server.location.gnss.GnssListenerMultiplexer$$ExternalSyntheticLambda4
        @Override // com.android.server.location.injector.AppForegroundHelper.AppForegroundListener
        public final void onAppForegroundChanged(int i, boolean z) {
            GnssListenerMultiplexer.this.onAppForegroundChanged(i, z);
        }
    };
    public final PackageResetHelper.Responder mPackageResetResponder = new PackageResetHelper.Responder() { // from class: com.android.server.location.gnss.GnssListenerMultiplexer.2
        @Override // com.android.server.location.injector.PackageResetHelper.Responder
        public void onPackageReset(String str) {
            GnssListenerMultiplexer.this.onPackageReset(str);
        }

        @Override // com.android.server.location.injector.PackageResetHelper.Responder
        public boolean isResetableForPackage(String str) {
            return GnssListenerMultiplexer.this.isResetableForPackage(str);
        }
    };

    public static /* synthetic */ boolean lambda$onBackgroundThrottlePackageWhitelistChanged$2(GnssListenerRegistration gnssListenerRegistration) {
        return true;
    }

    public boolean isSupported() {
        return true;
    }

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public /* bridge */ /* synthetic */ boolean isActive(ListenerRegistration listenerRegistration) {
        return isActive((GnssListenerRegistration) ((GnssListenerRegistration) listenerRegistration));
    }

    /* loaded from: classes.dex */
    public class GnssListenerRegistration extends BinderListenerRegistration<IBinder, TListener> {
        public boolean mForeground;
        public final CallerIdentity mIdentity;
        public boolean mPermitted;
        public final TRequest mRequest;

        @Override // com.android.server.location.listeners.BinderListenerRegistration
        public IBinder getBinderFromKey(IBinder iBinder) {
            return iBinder;
        }

        @Override // com.android.server.location.listeners.ListenerRegistration
        public String getTag() {
            return "GnssManager";
        }

        public GnssListenerRegistration(TRequest trequest, CallerIdentity callerIdentity, TListener tlistener) {
            super(callerIdentity.isMyProcess() ? FgThread.getExecutor() : ConcurrentUtils.DIRECT_EXECUTOR, tlistener);
            this.mRequest = trequest;
            this.mIdentity = callerIdentity;
        }

        public final TRequest getRequest() {
            return this.mRequest;
        }

        public final CallerIdentity getIdentity() {
            return this.mIdentity;
        }

        @Override // com.android.server.location.listeners.RemovableListenerRegistration
        public GnssListenerMultiplexer<TRequest, TListener, TMergedRegistration> getOwner() {
            return GnssListenerMultiplexer.this;
        }

        public boolean isForeground() {
            return this.mForeground;
        }

        public boolean isPermitted() {
            return this.mPermitted;
        }

        @Override // com.android.server.location.listeners.BinderListenerRegistration, com.android.server.location.listeners.RemovableListenerRegistration
        public void onRegister() {
            super.onRegister();
            this.mPermitted = GnssListenerMultiplexer.this.mLocationPermissionsHelper.hasLocationPermissions(2, this.mIdentity);
            this.mForeground = GnssListenerMultiplexer.this.mAppForegroundHelper.isAppForeground(this.mIdentity.getUid());
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
            boolean hasLocationPermissions = GnssListenerMultiplexer.this.mLocationPermissionsHelper.hasLocationPermissions(2, this.mIdentity);
            if (hasLocationPermissions != this.mPermitted) {
                this.mPermitted = hasLocationPermissions;
                return true;
            }
            return false;
        }

        public boolean onForegroundChanged(int i, boolean z) {
            if (this.mIdentity.getUid() != i || z == this.mForeground) {
                return false;
            }
            this.mForeground = z;
            return true;
        }

        @Override // com.android.server.location.listeners.ListenerRegistration
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.mIdentity);
            ArraySet arraySet = new ArraySet(2);
            if (!this.mForeground) {
                arraySet.add("bg");
            }
            if (!this.mPermitted) {
                arraySet.add("na");
            }
            if (!arraySet.isEmpty()) {
                sb.append(" ");
                sb.append(arraySet);
            }
            if (this.mRequest != null) {
                sb.append(" ");
                sb.append(this.mRequest);
            }
            return sb.toString();
        }
    }

    public GnssListenerMultiplexer(Injector injector) {
        this.mUserInfoHelper = injector.getUserInfoHelper();
        this.mSettingsHelper = injector.getSettingsHelper();
        this.mLocationPermissionsHelper = injector.getLocationPermissionsHelper();
        this.mAppForegroundHelper = injector.getAppForegroundHelper();
        this.mPackageResetHelper = injector.getPackageResetHelper();
        LocationManagerInternal locationManagerInternal = (LocationManagerInternal) LocalServices.getService(LocationManagerInternal.class);
        Objects.requireNonNull(locationManagerInternal);
        this.mLocationManagerInternal = locationManagerInternal;
    }

    public void addListener(CallerIdentity callerIdentity, TListener tlistener) {
        addListener(null, callerIdentity, tlistener);
    }

    public void addListener(TRequest trequest, CallerIdentity callerIdentity, TListener tlistener) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            putRegistration(tlistener.asBinder(), createRegistration(trequest, callerIdentity, tlistener));
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public GnssListenerMultiplexer<TRequest, TListener, TMergedRegistration>.GnssListenerRegistration createRegistration(TRequest trequest, CallerIdentity callerIdentity, TListener tlistener) {
        return new GnssListenerRegistration(trequest, callerIdentity, tlistener);
    }

    public void removeListener(TListener tlistener) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            removeRegistration((GnssListenerMultiplexer<TRequest, TListener, TMergedRegistration>) tlistener.asBinder());
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean isActive(GnssListenerMultiplexer<TRequest, TListener, TMergedRegistration>.GnssListenerRegistration gnssListenerRegistration) {
        if (isSupported()) {
            CallerIdentity identity = gnssListenerRegistration.getIdentity();
            if (gnssListenerRegistration.isPermitted()) {
                return (gnssListenerRegistration.isForeground() || isBackgroundRestrictionExempt(identity)) && isActive(identity);
            }
            return false;
        }
        return false;
    }

    public final boolean isActive(CallerIdentity callerIdentity) {
        return callerIdentity.isSystemServer() ? this.mLocationManagerInternal.isProviderEnabledForUser("gps", this.mUserInfoHelper.getCurrentUserId()) : this.mLocationManagerInternal.isProviderEnabledForUser("gps", callerIdentity.getUserId()) && this.mUserInfoHelper.isVisibleUserId(callerIdentity.getUserId()) && !this.mSettingsHelper.isLocationPackageBlacklisted(callerIdentity.getUserId(), callerIdentity.getPackageName());
    }

    public final boolean isBackgroundRestrictionExempt(CallerIdentity callerIdentity) {
        if (callerIdentity.getUid() == 1000 || this.mSettingsHelper.getBackgroundThrottlePackageWhitelist().contains(callerIdentity.getPackageName())) {
            return true;
        }
        return this.mLocationManagerInternal.isProvider((String) null, callerIdentity);
    }

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public TMergedRegistration mergeRegistrations(Collection<GnssListenerMultiplexer<TRequest, TListener, TMergedRegistration>.GnssListenerRegistration> collection) {
        if (Build.IS_DEBUGGABLE) {
            for (GnssListenerMultiplexer<TRequest, TListener, TMergedRegistration>.GnssListenerRegistration gnssListenerRegistration : collection) {
                Preconditions.checkState(gnssListenerRegistration.getRequest() == null);
            }
            return null;
        }
        return null;
    }

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public void onRegister() {
        if (isSupported()) {
            this.mUserInfoHelper.addListener(this.mUserChangedListener);
            this.mLocationManagerInternal.addProviderEnabledListener("gps", this.mProviderEnabledChangedListener);
            this.mSettingsHelper.addOnBackgroundThrottlePackageWhitelistChangedListener(this.mBackgroundThrottlePackageWhitelistChangedListener);
            this.mSettingsHelper.addOnLocationPackageBlacklistChangedListener(this.mLocationPackageBlacklistChangedListener);
            this.mLocationPermissionsHelper.addListener(this.mLocationPermissionsListener);
            this.mAppForegroundHelper.addListener(this.mAppForegroundChangedListener);
            this.mPackageResetHelper.register(this.mPackageResetResponder);
        }
    }

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public void onUnregister() {
        if (isSupported()) {
            this.mUserInfoHelper.removeListener(this.mUserChangedListener);
            this.mLocationManagerInternal.removeProviderEnabledListener("gps", this.mProviderEnabledChangedListener);
            this.mSettingsHelper.removeOnBackgroundThrottlePackageWhitelistChangedListener(this.mBackgroundThrottlePackageWhitelistChangedListener);
            this.mSettingsHelper.removeOnLocationPackageBlacklistChangedListener(this.mLocationPackageBlacklistChangedListener);
            this.mLocationPermissionsHelper.removeListener(this.mLocationPermissionsListener);
            this.mAppForegroundHelper.removeListener(this.mAppForegroundChangedListener);
            this.mPackageResetHelper.unregister(this.mPackageResetResponder);
        }
    }

    public static /* synthetic */ boolean lambda$onUserChanged$0(int i, GnssListenerRegistration gnssListenerRegistration) {
        return gnssListenerRegistration.getIdentity().getUserId() == i;
    }

    public final void onUserChanged(final int i, int i2) {
        if (i2 == 1 || i2 == 4) {
            updateRegistrations(new Predicate() { // from class: com.android.server.location.gnss.GnssListenerMultiplexer$$ExternalSyntheticLambda5
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$onUserChanged$0;
                    lambda$onUserChanged$0 = GnssListenerMultiplexer.lambda$onUserChanged$0(i, (GnssListenerMultiplexer.GnssListenerRegistration) obj);
                    return lambda$onUserChanged$0;
                }
            });
        }
    }

    public final void onProviderEnabledChanged(String str, final int i, boolean z) {
        Preconditions.checkState("gps".equals(str));
        updateRegistrations(new Predicate() { // from class: com.android.server.location.gnss.GnssListenerMultiplexer$$ExternalSyntheticLambda6
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$onProviderEnabledChanged$1;
                lambda$onProviderEnabledChanged$1 = GnssListenerMultiplexer.lambda$onProviderEnabledChanged$1(i, (GnssListenerMultiplexer.GnssListenerRegistration) obj);
                return lambda$onProviderEnabledChanged$1;
            }
        });
    }

    public static /* synthetic */ boolean lambda$onProviderEnabledChanged$1(int i, GnssListenerRegistration gnssListenerRegistration) {
        return gnssListenerRegistration.getIdentity().getUserId() == i;
    }

    public final void onBackgroundThrottlePackageWhitelistChanged() {
        updateRegistrations(new Predicate() { // from class: com.android.server.location.gnss.GnssListenerMultiplexer$$ExternalSyntheticLambda7
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$onBackgroundThrottlePackageWhitelistChanged$2;
                lambda$onBackgroundThrottlePackageWhitelistChanged$2 = GnssListenerMultiplexer.lambda$onBackgroundThrottlePackageWhitelistChanged$2((GnssListenerMultiplexer.GnssListenerRegistration) obj);
                return lambda$onBackgroundThrottlePackageWhitelistChanged$2;
            }
        });
    }

    public static /* synthetic */ boolean lambda$onLocationPackageBlacklistChanged$3(int i, GnssListenerRegistration gnssListenerRegistration) {
        return gnssListenerRegistration.getIdentity().getUserId() == i;
    }

    public final void onLocationPackageBlacklistChanged(final int i) {
        updateRegistrations(new Predicate() { // from class: com.android.server.location.gnss.GnssListenerMultiplexer$$ExternalSyntheticLambda8
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$onLocationPackageBlacklistChanged$3;
                lambda$onLocationPackageBlacklistChanged$3 = GnssListenerMultiplexer.lambda$onLocationPackageBlacklistChanged$3(i, (GnssListenerMultiplexer.GnssListenerRegistration) obj);
                return lambda$onLocationPackageBlacklistChanged$3;
            }
        });
    }

    public final void onLocationPermissionsChanged(final String str) {
        updateRegistrations(new Predicate() { // from class: com.android.server.location.gnss.GnssListenerMultiplexer$$ExternalSyntheticLambda13
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean onLocationPermissionsChanged;
                onLocationPermissionsChanged = ((GnssListenerMultiplexer.GnssListenerRegistration) obj).onLocationPermissionsChanged(str);
                return onLocationPermissionsChanged;
            }
        });
    }

    public final void onLocationPermissionsChanged(final int i) {
        updateRegistrations(new Predicate() { // from class: com.android.server.location.gnss.GnssListenerMultiplexer$$ExternalSyntheticLambda12
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean onLocationPermissionsChanged;
                onLocationPermissionsChanged = ((GnssListenerMultiplexer.GnssListenerRegistration) obj).onLocationPermissionsChanged(i);
                return onLocationPermissionsChanged;
            }
        });
    }

    public final void onAppForegroundChanged(final int i, final boolean z) {
        updateRegistrations(new Predicate() { // from class: com.android.server.location.gnss.GnssListenerMultiplexer$$ExternalSyntheticLambda9
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean onForegroundChanged;
                onForegroundChanged = ((GnssListenerMultiplexer.GnssListenerRegistration) obj).onForegroundChanged(i, z);
                return onForegroundChanged;
            }
        });
    }

    public final void onPackageReset(final String str) {
        updateRegistrations(new Predicate() { // from class: com.android.server.location.gnss.GnssListenerMultiplexer$$ExternalSyntheticLambda10
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$onPackageReset$7;
                lambda$onPackageReset$7 = GnssListenerMultiplexer.lambda$onPackageReset$7(str, (GnssListenerMultiplexer.GnssListenerRegistration) obj);
                return lambda$onPackageReset$7;
            }
        });
    }

    public static /* synthetic */ boolean lambda$onPackageReset$7(String str, GnssListenerRegistration gnssListenerRegistration) {
        if (gnssListenerRegistration.getIdentity().getPackageName().equals(str)) {
            gnssListenerRegistration.remove();
            return false;
        }
        return false;
    }

    public final boolean isResetableForPackage(final String str) {
        return findRegistration(new Predicate() { // from class: com.android.server.location.gnss.GnssListenerMultiplexer$$ExternalSyntheticLambda11
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isResetableForPackage$8;
                lambda$isResetableForPackage$8 = GnssListenerMultiplexer.lambda$isResetableForPackage$8(str, (GnssListenerMultiplexer.GnssListenerRegistration) obj);
                return lambda$isResetableForPackage$8;
            }
        });
    }

    public static /* synthetic */ boolean lambda$isResetableForPackage$8(String str, GnssListenerRegistration gnssListenerRegistration) {
        return gnssListenerRegistration.getIdentity().getPackageName().equals(str);
    }

    @Override // com.android.server.location.listeners.ListenerMultiplexer
    public String getServiceState() {
        return !isSupported() ? "unsupported" : super.getServiceState();
    }
}
