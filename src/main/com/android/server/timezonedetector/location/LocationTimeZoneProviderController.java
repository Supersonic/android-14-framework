package com.android.server.timezonedetector.location;

import android.app.time.LocationTimeZoneAlgorithmStatus;
import android.service.timezone.TimeZoneProviderEvent;
import android.service.timezone.TimeZoneProviderSuggestion;
import android.util.IndentingPrintWriter;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.timezonedetector.ConfigurationInternal;
import com.android.server.timezonedetector.Dumpable;
import com.android.server.timezonedetector.GeolocationTimeZoneSuggestion;
import com.android.server.timezonedetector.LocationAlgorithmEvent;
import com.android.server.timezonedetector.ReferenceWithHistory;
import com.android.server.timezonedetector.location.LocationTimeZoneManagerServiceState;
import com.android.server.timezonedetector.location.LocationTimeZoneProvider;
import com.android.server.timezonedetector.location.ThreadingDomain;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public class LocationTimeZoneProviderController implements Dumpable {
    @GuardedBy({"mSharedLock"})
    public Callback mCallback;
    @GuardedBy({"mSharedLock"})
    public ConfigurationInternal mCurrentUserConfiguration;
    @GuardedBy({"mSharedLock"})
    public Environment mEnvironment;
    @GuardedBy({"mSharedLock"})
    public LocationAlgorithmEvent mLastEvent;
    public final MetricsLogger mMetricsLogger;
    public final LocationTimeZoneProvider mPrimaryProvider;
    public final boolean mRecordStateChanges;
    @GuardedBy({"mSharedLock"})
    public final ArrayList<String> mRecordedStates = new ArrayList<>(0);
    public final LocationTimeZoneProvider mSecondaryProvider;
    public final Object mSharedLock;
    @GuardedBy({"mSharedLock"})
    public final ReferenceWithHistory<String> mState;
    public final ThreadingDomain mThreadingDomain;
    public final ThreadingDomain.SingleRunnableQueue mUncertaintyTimeoutQueue;

    /* loaded from: classes2.dex */
    public interface MetricsLogger {
        void onStateChange(String str);
    }

    public LocationTimeZoneProviderController(ThreadingDomain threadingDomain, MetricsLogger metricsLogger, LocationTimeZoneProvider locationTimeZoneProvider, LocationTimeZoneProvider locationTimeZoneProvider2, boolean z) {
        ReferenceWithHistory<String> referenceWithHistory = new ReferenceWithHistory<>(10);
        this.mState = referenceWithHistory;
        Objects.requireNonNull(threadingDomain);
        this.mThreadingDomain = threadingDomain;
        Object lockObject = threadingDomain.getLockObject();
        this.mSharedLock = lockObject;
        this.mUncertaintyTimeoutQueue = threadingDomain.createSingleRunnableQueue();
        Objects.requireNonNull(metricsLogger);
        this.mMetricsLogger = metricsLogger;
        Objects.requireNonNull(locationTimeZoneProvider);
        this.mPrimaryProvider = locationTimeZoneProvider;
        Objects.requireNonNull(locationTimeZoneProvider2);
        this.mSecondaryProvider = locationTimeZoneProvider2;
        this.mRecordStateChanges = z;
        synchronized (lockObject) {
            referenceWithHistory.set("UNKNOWN");
        }
    }

    public void initialize(Environment environment, Callback callback) {
        this.mThreadingDomain.assertCurrentThread();
        synchronized (this.mSharedLock) {
            LocationTimeZoneManagerService.debugLog("initialize()");
            Objects.requireNonNull(environment);
            this.mEnvironment = environment;
            Objects.requireNonNull(callback);
            this.mCallback = callback;
            this.mCurrentUserConfiguration = environment.getCurrentUserConfigurationInternal();
            LocationTimeZoneProvider.ProviderListener providerListener = new LocationTimeZoneProvider.ProviderListener() { // from class: com.android.server.timezonedetector.location.LocationTimeZoneProviderController$$ExternalSyntheticLambda0
                @Override // com.android.server.timezonedetector.location.LocationTimeZoneProvider.ProviderListener
                public final void onProviderStateChange(LocationTimeZoneProvider.ProviderState providerState) {
                    LocationTimeZoneProviderController.this.onProviderStateChange(providerState);
                }
            };
            setState("PROVIDERS_INITIALIZING");
            this.mPrimaryProvider.initialize(providerListener);
            this.mSecondaryProvider.initialize(providerListener);
            setStateAndReportStatusOnlyEvent("STOPPED", "initialize()");
            alterProvidersStartedStateIfRequired(null, this.mCurrentUserConfiguration);
        }
    }

    public void onConfigurationInternalChanged() {
        this.mThreadingDomain.assertCurrentThread();
        synchronized (this.mSharedLock) {
            LocationTimeZoneManagerService.debugLog("onConfigChanged()");
            ConfigurationInternal configurationInternal = this.mCurrentUserConfiguration;
            ConfigurationInternal currentUserConfigurationInternal = this.mEnvironment.getCurrentUserConfigurationInternal();
            this.mCurrentUserConfiguration = currentUserConfigurationInternal;
            if (!currentUserConfigurationInternal.equals(configurationInternal)) {
                if (currentUserConfigurationInternal.getUserId() != configurationInternal.getUserId()) {
                    String str = "User changed. old=" + configurationInternal.getUserId() + ", new=" + currentUserConfigurationInternal.getUserId();
                    LocationTimeZoneManagerService.debugLog("Stopping providers: " + str);
                    stopProviders(str);
                    alterProvidersStartedStateIfRequired(null, currentUserConfigurationInternal);
                } else {
                    alterProvidersStartedStateIfRequired(configurationInternal, currentUserConfigurationInternal);
                }
            }
        }
    }

    @VisibleForTesting
    public boolean isUncertaintyTimeoutSet() {
        return this.mUncertaintyTimeoutQueue.hasQueued();
    }

    @VisibleForTesting
    public long getUncertaintyTimeoutDelayMillis() {
        return this.mUncertaintyTimeoutQueue.getQueuedDelayMillis();
    }

    public void destroy() {
        this.mThreadingDomain.assertCurrentThread();
        synchronized (this.mSharedLock) {
            stopProviders("destroy()");
            this.mPrimaryProvider.destroy();
            this.mSecondaryProvider.destroy();
            setStateAndReportStatusOnlyEvent("DESTROYED", "destroy()");
        }
    }

    @GuardedBy({"mSharedLock"})
    public final void setStateAndReportStatusOnlyEvent(String str, String str2) {
        setState(str);
        LocationAlgorithmEvent locationAlgorithmEvent = new LocationAlgorithmEvent(generateCurrentAlgorithmStatus(), null);
        locationAlgorithmEvent.addDebugInfo(str2);
        reportEvent(locationAlgorithmEvent);
    }

    @GuardedBy({"mSharedLock"})
    public final void reportSuggestionEvent(GeolocationTimeZoneSuggestion geolocationTimeZoneSuggestion, String str) {
        LocationAlgorithmEvent locationAlgorithmEvent = new LocationAlgorithmEvent(generateCurrentAlgorithmStatus(), geolocationTimeZoneSuggestion);
        locationAlgorithmEvent.addDebugInfo(str);
        reportEvent(locationAlgorithmEvent);
    }

    @GuardedBy({"mSharedLock"})
    public final void reportEvent(LocationAlgorithmEvent locationAlgorithmEvent) {
        LocationTimeZoneManagerService.debugLog("makeSuggestion: suggestion=" + locationAlgorithmEvent);
        this.mCallback.sendEvent(locationAlgorithmEvent);
        this.mLastEvent = locationAlgorithmEvent;
    }

    @GuardedBy({"mSharedLock"})
    public final void setState(String str) {
        if (Objects.equals(this.mState.get(), str)) {
            return;
        }
        this.mState.set(str);
        if (this.mRecordStateChanges) {
            this.mRecordedStates.add(str);
        }
        this.mMetricsLogger.onStateChange(str);
    }

    @GuardedBy({"mSharedLock"})
    public final void stopProviders(String str) {
        stopProviderIfStarted(this.mPrimaryProvider);
        stopProviderIfStarted(this.mSecondaryProvider);
        cancelUncertaintyTimeout();
        setStateAndReportStatusOnlyEvent("STOPPED", "Providers stopped: " + str);
    }

    @GuardedBy({"mSharedLock"})
    public final void stopProviderIfStarted(LocationTimeZoneProvider locationTimeZoneProvider) {
        if (locationTimeZoneProvider.getCurrentState().isStarted()) {
            stopProvider(locationTimeZoneProvider);
        }
    }

    @GuardedBy({"mSharedLock"})
    public final void stopProvider(LocationTimeZoneProvider locationTimeZoneProvider) {
        switch (locationTimeZoneProvider.getCurrentState().stateEnum) {
            case 1:
            case 2:
            case 3:
                LocationTimeZoneManagerService.debugLog("Stopping " + locationTimeZoneProvider);
                locationTimeZoneProvider.stopUpdates();
                return;
            case 4:
                LocationTimeZoneManagerService.debugLog("No need to stop " + locationTimeZoneProvider + ": already stopped");
                return;
            case 5:
            case 6:
                LocationTimeZoneManagerService.debugLog("Unable to stop " + locationTimeZoneProvider + ": it is terminated.");
                return;
            default:
                LocationTimeZoneManagerService.warnLog("Unknown provider state: " + locationTimeZoneProvider);
                return;
        }
    }

    @GuardedBy({"mSharedLock"})
    public final void alterProvidersStartedStateIfRequired(ConfigurationInternal configurationInternal, ConfigurationInternal configurationInternal2) {
        boolean z = configurationInternal != null && configurationInternal.isGeoDetectionExecutionEnabled();
        boolean isGeoDetectionExecutionEnabled = configurationInternal2.isGeoDetectionExecutionEnabled();
        if (z == isGeoDetectionExecutionEnabled) {
            return;
        }
        if (isGeoDetectionExecutionEnabled) {
            setStateAndReportStatusOnlyEvent("INITIALIZING", "initializing()");
            tryStartProvider(this.mPrimaryProvider, configurationInternal2);
            if (this.mPrimaryProvider.getCurrentState().isStarted()) {
                return;
            }
            tryStartProvider(this.mSecondaryProvider, configurationInternal2);
            if (this.mSecondaryProvider.getCurrentState().isStarted()) {
                return;
            }
            setStateAndReportStatusOnlyEvent("FAILED", "Providers are failed: primary=" + this.mPrimaryProvider.getCurrentState() + " secondary=" + this.mPrimaryProvider.getCurrentState());
            return;
        }
        stopProviders("Geo detection behavior disabled");
    }

    @GuardedBy({"mSharedLock"})
    public final void tryStartProvider(LocationTimeZoneProvider locationTimeZoneProvider, ConfigurationInternal configurationInternal) {
        switch (locationTimeZoneProvider.getCurrentState().stateEnum) {
            case 1:
            case 2:
            case 3:
                LocationTimeZoneManagerService.debugLog("No need to start " + locationTimeZoneProvider + ": already started");
                return;
            case 4:
                LocationTimeZoneManagerService.debugLog("Enabling " + locationTimeZoneProvider);
                locationTimeZoneProvider.startUpdates(configurationInternal, this.mEnvironment.getProviderInitializationTimeout(), this.mEnvironment.getProviderInitializationTimeoutFuzz(), this.mEnvironment.getProviderEventFilteringAgeThreshold());
                return;
            case 5:
            case 6:
                LocationTimeZoneManagerService.debugLog("Unable to start " + locationTimeZoneProvider + ": it is terminated");
                return;
            default:
                throw new IllegalStateException("Unknown provider state: provider=" + locationTimeZoneProvider);
        }
    }

    public void onProviderStateChange(LocationTimeZoneProvider.ProviderState providerState) {
        this.mThreadingDomain.assertCurrentThread();
        LocationTimeZoneProvider locationTimeZoneProvider = providerState.provider;
        assertProviderKnown(locationTimeZoneProvider);
        synchronized (this.mSharedLock) {
            if (Objects.equals(this.mState.get(), "PROVIDERS_INITIALIZING")) {
                LocationTimeZoneManagerService.warnLog("onProviderStateChange: Ignoring provider state change because both providers have not yet completed initialization. providerState=" + providerState);
                return;
            }
            switch (providerState.stateEnum) {
                case 1:
                case 4:
                case 6:
                    LocationTimeZoneManagerService.warnLog("onProviderStateChange: Unexpected state change for provider, provider=" + locationTimeZoneProvider);
                    break;
                case 2:
                case 3:
                    LocationTimeZoneManagerService.debugLog("onProviderStateChange: Received notification of a state change while started, provider=" + locationTimeZoneProvider);
                    handleProviderStartedStateChange(providerState);
                    break;
                case 5:
                    LocationTimeZoneManagerService.debugLog("Received notification of permanent failure for provider=" + locationTimeZoneProvider);
                    handleProviderFailedStateChange(providerState);
                    break;
                default:
                    LocationTimeZoneManagerService.warnLog("onProviderStateChange: Unexpected provider=" + locationTimeZoneProvider);
                    break;
            }
        }
    }

    public final void assertProviderKnown(LocationTimeZoneProvider locationTimeZoneProvider) {
        if (locationTimeZoneProvider == this.mPrimaryProvider || locationTimeZoneProvider == this.mSecondaryProvider) {
            return;
        }
        throw new IllegalArgumentException("Unknown provider: " + locationTimeZoneProvider);
    }

    @GuardedBy({"mSharedLock"})
    public final void handleProviderFailedStateChange(LocationTimeZoneProvider.ProviderState providerState) {
        LocationTimeZoneProvider locationTimeZoneProvider = providerState.provider;
        LocationTimeZoneProvider.ProviderState currentState = this.mPrimaryProvider.getCurrentState();
        LocationTimeZoneProvider.ProviderState currentState2 = this.mSecondaryProvider.getCurrentState();
        if (locationTimeZoneProvider == this.mPrimaryProvider) {
            if (!currentState2.isTerminated()) {
                tryStartProvider(this.mSecondaryProvider, this.mCurrentUserConfiguration);
            }
        } else if (locationTimeZoneProvider == this.mSecondaryProvider && currentState.stateEnum != 3 && !currentState.isTerminated()) {
            LocationTimeZoneManagerService.warnLog("Secondary provider unexpected reported a failure: failed provider=" + locationTimeZoneProvider.getName() + ", primary provider=" + this.mPrimaryProvider + ", secondary provider=" + this.mSecondaryProvider);
        }
        if (currentState.isTerminated() && currentState2.isTerminated()) {
            cancelUncertaintyTimeout();
            setStateAndReportStatusOnlyEvent("FAILED", "Both providers are terminated: primary=" + currentState.provider + ", secondary=" + currentState2.provider);
        }
    }

    @GuardedBy({"mSharedLock"})
    public final void handleProviderStartedStateChange(LocationTimeZoneProvider.ProviderState providerState) {
        LocationTimeZoneProvider locationTimeZoneProvider = providerState.provider;
        TimeZoneProviderEvent timeZoneProviderEvent = providerState.event;
        if (timeZoneProviderEvent == null) {
            long elapsedRealtimeMillis = this.mEnvironment.elapsedRealtimeMillis();
            handleProviderUncertainty(locationTimeZoneProvider, elapsedRealtimeMillis, "provider=" + locationTimeZoneProvider + ", implicit uncertainty, event=null");
            return;
        }
        if (!this.mCurrentUserConfiguration.isGeoDetectionExecutionEnabled()) {
            LocationTimeZoneManagerService.warnLog("Provider=" + locationTimeZoneProvider + " is started, but currentUserConfiguration=" + this.mCurrentUserConfiguration + " suggests it shouldn't be.");
        }
        int type = timeZoneProviderEvent.getType();
        if (type == 1) {
            LocationTimeZoneManagerService.warnLog("Provider=" + locationTimeZoneProvider + " is started, but event suggests it shouldn't be");
        } else if (type == 2) {
            handleProviderSuggestion(locationTimeZoneProvider, timeZoneProviderEvent);
        } else if (type == 3) {
            long creationElapsedMillis = timeZoneProviderEvent.getCreationElapsedMillis();
            handleProviderUncertainty(locationTimeZoneProvider, creationElapsedMillis, "provider=" + locationTimeZoneProvider + ", explicit uncertainty. event=" + timeZoneProviderEvent);
        } else {
            LocationTimeZoneManagerService.warnLog("Unknown eventType=" + timeZoneProviderEvent.getType());
        }
    }

    @GuardedBy({"mSharedLock"})
    public final void handleProviderSuggestion(LocationTimeZoneProvider locationTimeZoneProvider, TimeZoneProviderEvent timeZoneProviderEvent) {
        cancelUncertaintyTimeout();
        if (locationTimeZoneProvider == this.mPrimaryProvider) {
            stopProviderIfStarted(this.mSecondaryProvider);
        }
        TimeZoneProviderSuggestion suggestion = timeZoneProviderEvent.getSuggestion();
        setState("CERTAIN");
        GeolocationTimeZoneSuggestion createCertainSuggestion = GeolocationTimeZoneSuggestion.createCertainSuggestion(suggestion.getElapsedRealtimeMillis(), suggestion.getTimeZoneIds());
        reportSuggestionEvent(createCertainSuggestion, "Provider event received: provider=" + locationTimeZoneProvider + ", providerEvent=" + timeZoneProviderEvent + ", suggestionCreationTime=" + this.mEnvironment.elapsedRealtimeMillis());
    }

    @Override // com.android.server.timezonedetector.Dumpable
    public void dump(IndentingPrintWriter indentingPrintWriter, String[] strArr) {
        synchronized (this.mSharedLock) {
            indentingPrintWriter.println("LocationTimeZoneProviderController:");
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.println("mCurrentUserConfiguration=" + this.mCurrentUserConfiguration);
            indentingPrintWriter.println("providerInitializationTimeout=" + this.mEnvironment.getProviderInitializationTimeout());
            indentingPrintWriter.println("providerInitializationTimeoutFuzz=" + this.mEnvironment.getProviderInitializationTimeoutFuzz());
            indentingPrintWriter.println("uncertaintyDelay=" + this.mEnvironment.getUncertaintyDelay());
            indentingPrintWriter.println("mState=" + this.mState.get());
            indentingPrintWriter.println("mLastEvent=" + this.mLastEvent);
            indentingPrintWriter.println("State history:");
            indentingPrintWriter.increaseIndent();
            this.mState.dump(indentingPrintWriter);
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println("Primary Provider:");
            indentingPrintWriter.increaseIndent();
            this.mPrimaryProvider.dump(indentingPrintWriter, strArr);
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println("Secondary Provider:");
            indentingPrintWriter.increaseIndent();
            this.mSecondaryProvider.dump(indentingPrintWriter, strArr);
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.decreaseIndent();
        }
    }

    @GuardedBy({"mSharedLock"})
    public final void cancelUncertaintyTimeout() {
        this.mUncertaintyTimeoutQueue.cancel();
    }

    @GuardedBy({"mSharedLock"})
    public void handleProviderUncertainty(final LocationTimeZoneProvider locationTimeZoneProvider, final long j, String str) {
        Objects.requireNonNull(locationTimeZoneProvider);
        if (!this.mUncertaintyTimeoutQueue.hasQueued()) {
            LocationTimeZoneManagerService.debugLog("Starting uncertainty timeout: reason=" + str);
            final Duration uncertaintyDelay = this.mEnvironment.getUncertaintyDelay();
            this.mUncertaintyTimeoutQueue.runDelayed(new Runnable() { // from class: com.android.server.timezonedetector.location.LocationTimeZoneProviderController$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    LocationTimeZoneProviderController.this.lambda$handleProviderUncertainty$0(locationTimeZoneProvider, j, uncertaintyDelay);
                }
            }, uncertaintyDelay.toMillis());
        }
        if (locationTimeZoneProvider == this.mPrimaryProvider) {
            tryStartProvider(this.mSecondaryProvider, this.mCurrentUserConfiguration);
        }
    }

    /* renamed from: onProviderUncertaintyTimeout */
    public final void lambda$handleProviderUncertainty$0(LocationTimeZoneProvider locationTimeZoneProvider, long j, Duration duration) {
        this.mThreadingDomain.assertCurrentThread();
        synchronized (this.mSharedLock) {
            long elapsedRealtimeMillis = this.mEnvironment.elapsedRealtimeMillis();
            setState("UNCERTAIN");
            GeolocationTimeZoneSuggestion createUncertainSuggestion = GeolocationTimeZoneSuggestion.createUncertainSuggestion(j);
            reportSuggestionEvent(createUncertainSuggestion, "Uncertainty timeout triggered for " + locationTimeZoneProvider.getName() + ": primary=" + this.mPrimaryProvider + ", secondary=" + this.mSecondaryProvider + ", uncertaintyStarted=" + Duration.ofMillis(j) + ", afterUncertaintyTimeout=" + Duration.ofMillis(elapsedRealtimeMillis) + ", uncertaintyDelay=" + duration);
        }
    }

    @GuardedBy({"mSharedLock"})
    public final LocationTimeZoneAlgorithmStatus generateCurrentAlgorithmStatus() {
        return createAlgorithmStatus(this.mState.get(), this.mPrimaryProvider.getCurrentState(), this.mSecondaryProvider.getCurrentState());
    }

    public static LocationTimeZoneAlgorithmStatus createAlgorithmStatus(String str, LocationTimeZoneProvider.ProviderState providerState, LocationTimeZoneProvider.ProviderState providerState2) {
        return new LocationTimeZoneAlgorithmStatus(mapControllerStateToDetectionAlgorithmStatus(str), providerState.getProviderStatus(), providerState.getReportedStatus(), providerState2.getProviderStatus(), providerState2.getReportedStatus());
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static int mapControllerStateToDetectionAlgorithmStatus(String str) {
        char c;
        switch (str.hashCode()) {
            case -1166336595:
                if (str.equals("STOPPED")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -468307734:
                if (str.equals("PROVIDERS_INITIALIZING")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 433141802:
                if (str.equals("UNKNOWN")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 478389753:
                if (str.equals("DESTROYED")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 872357833:
                if (str.equals("UNCERTAIN")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1386911874:
                if (str.equals("CERTAIN")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1917201485:
                if (str.equals("INITIALIZING")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 2066319421:
                if (str.equals("FAILED")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        return (c == 0 || c == 1 || c == 2 || c == 3) ? 3 : 2;
    }

    public void clearRecordedStates() {
        this.mThreadingDomain.assertCurrentThread();
        synchronized (this.mSharedLock) {
            this.mRecordedStates.clear();
            this.mPrimaryProvider.clearRecordedStates();
            this.mSecondaryProvider.clearRecordedStates();
        }
    }

    public LocationTimeZoneManagerServiceState getStateForTests() {
        LocationTimeZoneManagerServiceState build;
        this.mThreadingDomain.assertCurrentThread();
        synchronized (this.mSharedLock) {
            LocationTimeZoneManagerServiceState.Builder builder = new LocationTimeZoneManagerServiceState.Builder();
            LocationAlgorithmEvent locationAlgorithmEvent = this.mLastEvent;
            if (locationAlgorithmEvent != null) {
                builder.setLastEvent(locationAlgorithmEvent);
            }
            builder.setControllerState(this.mState.get()).setStateChanges(this.mRecordedStates).setPrimaryProviderStateChanges(this.mPrimaryProvider.getRecordedStates()).setSecondaryProviderStateChanges(this.mSecondaryProvider.getRecordedStates());
            build = builder.build();
        }
        return build;
    }

    /* loaded from: classes2.dex */
    public static abstract class Environment {
        public final Object mSharedLock;
        public final ThreadingDomain mThreadingDomain;

        public abstract long elapsedRealtimeMillis();

        public abstract ConfigurationInternal getCurrentUserConfigurationInternal();

        public abstract Duration getProviderEventFilteringAgeThreshold();

        public abstract Duration getProviderInitializationTimeout();

        public abstract Duration getProviderInitializationTimeoutFuzz();

        public abstract Duration getUncertaintyDelay();

        public Environment(ThreadingDomain threadingDomain) {
            Objects.requireNonNull(threadingDomain);
            this.mThreadingDomain = threadingDomain;
            this.mSharedLock = threadingDomain.getLockObject();
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Callback {
        public final ThreadingDomain mThreadingDomain;

        public abstract void sendEvent(LocationAlgorithmEvent locationAlgorithmEvent);

        public Callback(ThreadingDomain threadingDomain) {
            Objects.requireNonNull(threadingDomain);
            this.mThreadingDomain = threadingDomain;
        }
    }
}
