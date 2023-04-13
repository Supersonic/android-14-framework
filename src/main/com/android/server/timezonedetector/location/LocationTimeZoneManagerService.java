package com.android.server.timezonedetector.location;

import android.content.Context;
import android.os.Binder;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.util.IndentingPrintWriter;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.Preconditions;
import com.android.server.FgThread;
import com.android.server.SystemService;
import com.android.server.timezonedetector.Dumpable;
import com.android.server.timezonedetector.ServiceConfigAccessor;
import com.android.server.timezonedetector.ServiceConfigAccessorImpl;
import com.android.server.timezonedetector.StateChangeListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Callable;
/* loaded from: classes2.dex */
public class LocationTimeZoneManagerService extends Binder {
    public static final long BLOCKING_OP_WAIT_DURATION_MILLIS = Duration.ofSeconds(20).toMillis();
    public final Context mContext;
    public final Handler mHandler;
    @GuardedBy({"mSharedLock"})
    public LocationTimeZoneProviderController mLocationTimeZoneProviderController;
    @GuardedBy({"mSharedLock"})
    public LocationTimeZoneProviderControllerEnvironmentImpl mLocationTimeZoneProviderControllerEnvironment;
    @GuardedBy({"mSharedLock"})
    public final ProviderConfig mPrimaryProviderConfig = new ProviderConfig(0, "primary", "android.service.timezone.PrimaryLocationTimeZoneProviderService");
    @GuardedBy({"mSharedLock"})
    public final ProviderConfig mSecondaryProviderConfig = new ProviderConfig(1, "secondary", "android.service.timezone.SecondaryLocationTimeZoneProviderService");
    public final ServiceConfigAccessor mServiceConfigAccessor;
    public final Object mSharedLock;
    public final ThreadingDomain mThreadingDomain;

    /* loaded from: classes2.dex */
    public static class Lifecycle extends SystemService {
        public LocationTimeZoneManagerService mService;
        public final ServiceConfigAccessor mServiceConfigAccessor;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public Lifecycle(Context context) {
            super(context);
            Objects.requireNonNull(context);
            this.mServiceConfigAccessor = ServiceConfigAccessorImpl.getInstance(context);
        }

        @Override // com.android.server.SystemService
        public void onStart() {
            Context context = getContext();
            if (this.mServiceConfigAccessor.isGeoTimeZoneDetectionFeatureSupportedInConfig()) {
                LocationTimeZoneManagerService locationTimeZoneManagerService = new LocationTimeZoneManagerService(context, this.mServiceConfigAccessor);
                this.mService = locationTimeZoneManagerService;
                publishBinderService("location_time_zone_manager", locationTimeZoneManagerService);
                return;
            }
            Slog.d("LocationTZDetector", "Geo time zone detection feature is disabled in config");
        }

        @Override // com.android.server.SystemService
        public void onBootPhase(int i) {
            if (this.mServiceConfigAccessor.isGeoTimeZoneDetectionFeatureSupportedInConfig()) {
                if (i == 500) {
                    this.mService.onSystemReady();
                } else if (i == 600) {
                    this.mService.onSystemThirdPartyAppsCanStart();
                }
            }
        }
    }

    public LocationTimeZoneManagerService(Context context, ServiceConfigAccessor serviceConfigAccessor) {
        this.mContext = context.createAttributionContext("LocationTimeZoneService");
        Handler handler = FgThread.getHandler();
        this.mHandler = handler;
        HandlerThreadingDomain handlerThreadingDomain = new HandlerThreadingDomain(handler);
        this.mThreadingDomain = handlerThreadingDomain;
        this.mSharedLock = handlerThreadingDomain.getLockObject();
        Objects.requireNonNull(serviceConfigAccessor);
        this.mServiceConfigAccessor = serviceConfigAccessor;
    }

    public void onSystemReady() {
        this.mServiceConfigAccessor.addLocationTimeZoneManagerConfigListener(new StateChangeListener() { // from class: com.android.server.timezonedetector.location.LocationTimeZoneManagerService$$ExternalSyntheticLambda3
            @Override // com.android.server.timezonedetector.StateChangeListener
            public final void onChange() {
                LocationTimeZoneManagerService.this.handleServiceConfigurationChangedOnMainThread();
            }
        });
    }

    public final void handleServiceConfigurationChangedOnMainThread() {
        this.mThreadingDomain.post(new Runnable() { // from class: com.android.server.timezonedetector.location.LocationTimeZoneManagerService$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                LocationTimeZoneManagerService.this.restartIfRequiredOnDomainThread();
            }
        });
    }

    public final void restartIfRequiredOnDomainThread() {
        this.mThreadingDomain.assertCurrentThread();
        synchronized (this.mSharedLock) {
            if (this.mLocationTimeZoneProviderController != null) {
                stopOnDomainThread();
                startOnDomainThread();
            }
        }
    }

    public void onSystemThirdPartyAppsCanStart() {
        startInternal(false);
    }

    public void start() {
        enforceManageTimeZoneDetectorPermission();
        startInternal(true);
    }

    public final void startInternal(boolean z) {
        Runnable runnable = new Runnable() { // from class: com.android.server.timezonedetector.location.LocationTimeZoneManagerService$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                LocationTimeZoneManagerService.this.startOnDomainThread();
            }
        };
        if (z) {
            this.mThreadingDomain.postAndWait(runnable, BLOCKING_OP_WAIT_DURATION_MILLIS);
        } else {
            this.mThreadingDomain.post(runnable);
        }
    }

    public void startWithTestProviders(final String str, final String str2, final boolean z) {
        enforceManageTimeZoneDetectorPermission();
        if (str == null && str2 == null) {
            throw new IllegalArgumentException("One or both test package names must be provided.");
        }
        this.mThreadingDomain.postAndWait(new Runnable() { // from class: com.android.server.timezonedetector.location.LocationTimeZoneManagerService$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                LocationTimeZoneManagerService.this.lambda$startWithTestProviders$0(str, str2, z);
            }
        }, BLOCKING_OP_WAIT_DURATION_MILLIS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startWithTestProviders$0(String str, String str2, boolean z) {
        synchronized (this.mSharedLock) {
            stopOnDomainThread();
            this.mServiceConfigAccessor.setTestPrimaryLocationTimeZoneProviderPackageName(str);
            this.mServiceConfigAccessor.setTestSecondaryLocationTimeZoneProviderPackageName(str2);
            this.mServiceConfigAccessor.setRecordStateChangesForTests(z);
            startOnDomainThread();
        }
    }

    public final void startOnDomainThread() {
        this.mThreadingDomain.assertCurrentThread();
        synchronized (this.mSharedLock) {
            if (!this.mServiceConfigAccessor.isGeoTimeZoneDetectionFeatureSupported()) {
                debugLog("Not starting location_time_zone_manager: it is disabled in service config");
                return;
            }
            if (this.mLocationTimeZoneProviderController == null) {
                LocationTimeZoneProvider createProvider = this.mPrimaryProviderConfig.createProvider();
                LocationTimeZoneProvider createProvider2 = this.mSecondaryProviderConfig.createProvider();
                LocationTimeZoneProviderController locationTimeZoneProviderController = new LocationTimeZoneProviderController(this.mThreadingDomain, new RealControllerMetricsLogger(), createProvider, createProvider2, this.mServiceConfigAccessor.getRecordStateChangesForTests());
                LocationTimeZoneProviderControllerEnvironmentImpl locationTimeZoneProviderControllerEnvironmentImpl = new LocationTimeZoneProviderControllerEnvironmentImpl(this.mThreadingDomain, this.mServiceConfigAccessor, locationTimeZoneProviderController);
                locationTimeZoneProviderController.initialize(locationTimeZoneProviderControllerEnvironmentImpl, new LocationTimeZoneProviderControllerCallbackImpl(this.mThreadingDomain));
                this.mLocationTimeZoneProviderControllerEnvironment = locationTimeZoneProviderControllerEnvironmentImpl;
                this.mLocationTimeZoneProviderController = locationTimeZoneProviderController;
            }
        }
    }

    public void stop() {
        enforceManageTimeZoneDetectorPermission();
        this.mThreadingDomain.postAndWait(new Runnable() { // from class: com.android.server.timezonedetector.location.LocationTimeZoneManagerService$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                LocationTimeZoneManagerService.this.stopOnDomainThread();
            }
        }, BLOCKING_OP_WAIT_DURATION_MILLIS);
    }

    public final void stopOnDomainThread() {
        this.mThreadingDomain.assertCurrentThread();
        synchronized (this.mSharedLock) {
            LocationTimeZoneProviderController locationTimeZoneProviderController = this.mLocationTimeZoneProviderController;
            if (locationTimeZoneProviderController != null) {
                locationTimeZoneProviderController.destroy();
                this.mLocationTimeZoneProviderController = null;
                this.mLocationTimeZoneProviderControllerEnvironment.destroy();
                this.mLocationTimeZoneProviderControllerEnvironment = null;
                this.mServiceConfigAccessor.resetVolatileTestConfig();
            }
        }
    }

    public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
        new LocationTimeZoneManagerShellCommand(this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
    }

    public void clearRecordedProviderStates() {
        enforceManageTimeZoneDetectorPermission();
        this.mThreadingDomain.postAndWait(new Runnable() { // from class: com.android.server.timezonedetector.location.LocationTimeZoneManagerService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                LocationTimeZoneManagerService.this.lambda$clearRecordedProviderStates$1();
            }
        }, BLOCKING_OP_WAIT_DURATION_MILLIS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$clearRecordedProviderStates$1() {
        synchronized (this.mSharedLock) {
            LocationTimeZoneProviderController locationTimeZoneProviderController = this.mLocationTimeZoneProviderController;
            if (locationTimeZoneProviderController != null) {
                locationTimeZoneProviderController.clearRecordedStates();
            }
        }
    }

    public LocationTimeZoneManagerServiceState getStateForTests() {
        enforceManageTimeZoneDetectorPermission();
        try {
            return (LocationTimeZoneManagerServiceState) this.mThreadingDomain.postAndWait(new Callable() { // from class: com.android.server.timezonedetector.location.LocationTimeZoneManagerService$$ExternalSyntheticLambda2
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    LocationTimeZoneManagerServiceState lambda$getStateForTests$2;
                    lambda$getStateForTests$2 = LocationTimeZoneManagerService.this.lambda$getStateForTests$2();
                    return lambda$getStateForTests$2;
                }
            }, BLOCKING_OP_WAIT_DURATION_MILLIS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ LocationTimeZoneManagerServiceState lambda$getStateForTests$2() throws Exception {
        synchronized (this.mSharedLock) {
            LocationTimeZoneProviderController locationTimeZoneProviderController = this.mLocationTimeZoneProviderController;
            if (locationTimeZoneProviderController == null) {
                return null;
            }
            return locationTimeZoneProviderController.getStateForTests();
        }
    }

    @Override // android.os.Binder
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpPermission(this.mContext, "LocationTZDetector", printWriter)) {
            IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter);
            synchronized (this.mSharedLock) {
                indentingPrintWriter.println("LocationTimeZoneManagerService:");
                indentingPrintWriter.increaseIndent();
                indentingPrintWriter.println("Primary provider config:");
                indentingPrintWriter.increaseIndent();
                this.mPrimaryProviderConfig.dump(indentingPrintWriter, strArr);
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.println("Secondary provider config:");
                indentingPrintWriter.increaseIndent();
                this.mSecondaryProviderConfig.dump(indentingPrintWriter, strArr);
                indentingPrintWriter.decreaseIndent();
                LocationTimeZoneProviderController locationTimeZoneProviderController = this.mLocationTimeZoneProviderController;
                if (locationTimeZoneProviderController == null) {
                    indentingPrintWriter.println("{Stopped}");
                } else {
                    locationTimeZoneProviderController.dump(indentingPrintWriter, strArr);
                }
                indentingPrintWriter.decreaseIndent();
            }
        }
    }

    public static void debugLog(String str) {
        if (Log.isLoggable("LocationTZDetector", 3)) {
            Slog.d("LocationTZDetector", str);
        }
    }

    public static void infoLog(String str) {
        if (Log.isLoggable("LocationTZDetector", 4)) {
            Slog.i("LocationTZDetector", str);
        }
    }

    public static void warnLog(String str) {
        warnLog(str, null);
    }

    public static void warnLog(String str, Throwable th) {
        if (Log.isLoggable("LocationTZDetector", 5)) {
            Slog.w("LocationTZDetector", str, th);
        }
    }

    public final void enforceManageTimeZoneDetectorPermission() {
        this.mContext.enforceCallingPermission("android.permission.MANAGE_TIME_AND_ZONE_DETECTION", "manage time and time zone detection");
    }

    /* loaded from: classes2.dex */
    public final class ProviderConfig implements Dumpable {
        public final int mIndex;
        public final String mName;
        public final String mServiceAction;

        /* JADX WARN: Code restructure failed: missing block: B:5:0x0008, code lost:
            if (r2 <= 1) goto L5;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public ProviderConfig(int i, String str, String str2) {
            boolean z = i >= 0;
            Preconditions.checkArgument(z);
            this.mIndex = i;
            Objects.requireNonNull(str);
            this.mName = str;
            Objects.requireNonNull(str2);
            this.mServiceAction = str2;
        }

        public LocationTimeZoneProvider createProvider() {
            RealProviderMetricsLogger realProviderMetricsLogger = new RealProviderMetricsLogger(this.mIndex);
            if (Objects.equals(getMode(), "disabled")) {
                return new DisabledLocationTimeZoneProvider(realProviderMetricsLogger, LocationTimeZoneManagerService.this.mThreadingDomain, this.mName, LocationTimeZoneManagerService.this.mServiceConfigAccessor.getRecordStateChangesForTests());
            }
            return new BinderLocationTimeZoneProvider(realProviderMetricsLogger, LocationTimeZoneManagerService.this.mThreadingDomain, this.mName, createBinderProxy(), LocationTimeZoneManagerService.this.mServiceConfigAccessor.getRecordStateChangesForTests());
        }

        @Override // com.android.server.timezonedetector.Dumpable
        public void dump(IndentingPrintWriter indentingPrintWriter, String[] strArr) {
            indentingPrintWriter.printf("getMode()=%s\n", new Object[]{getMode()});
            indentingPrintWriter.printf("getPackageName()=%s\n", new Object[]{getPackageName()});
        }

        public final String getMode() {
            if (this.mIndex == 0) {
                return LocationTimeZoneManagerService.this.mServiceConfigAccessor.getPrimaryLocationTimeZoneProviderMode();
            }
            return LocationTimeZoneManagerService.this.mServiceConfigAccessor.getSecondaryLocationTimeZoneProviderMode();
        }

        public final RealLocationTimeZoneProviderProxy createBinderProxy() {
            String str = this.mServiceAction;
            boolean isTestProvider = isTestProvider();
            return new RealLocationTimeZoneProviderProxy(LocationTimeZoneManagerService.this.mContext, LocationTimeZoneManagerService.this.mHandler, LocationTimeZoneManagerService.this.mThreadingDomain, str, getPackageName(), isTestProvider);
        }

        public final boolean isTestProvider() {
            if (this.mIndex == 0) {
                return LocationTimeZoneManagerService.this.mServiceConfigAccessor.isTestPrimaryLocationTimeZoneProvider();
            }
            return LocationTimeZoneManagerService.this.mServiceConfigAccessor.isTestSecondaryLocationTimeZoneProvider();
        }

        public final String getPackageName() {
            if (this.mIndex == 0) {
                return LocationTimeZoneManagerService.this.mServiceConfigAccessor.getPrimaryLocationTimeZoneProviderPackageName();
            }
            return LocationTimeZoneManagerService.this.mServiceConfigAccessor.getSecondaryLocationTimeZoneProviderPackageName();
        }
    }
}
