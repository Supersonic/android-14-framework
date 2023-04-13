package com.android.server.timezonedetector;

import android.app.time.LocationTimeZoneAlgorithmStatus;
import android.app.time.TelephonyTimeZoneAlgorithmStatus;
import android.app.time.TimeZoneCapabilities;
import android.app.time.TimeZoneCapabilitiesAndConfig;
import android.app.time.TimeZoneConfiguration;
import android.app.time.TimeZoneDetectorStatus;
import android.app.time.TimeZoneState;
import android.app.timezonedetector.ManualTimeZoneSuggestion;
import android.app.timezonedetector.TelephonyTimeZoneSuggestion;
import android.os.Handler;
import android.os.TimestampedValue;
import android.util.IndentingPrintWriter;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.timedetector.EnvironmentImpl$$ExternalSyntheticLambda1;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class TimeZoneDetectorStrategyImpl implements TimeZoneDetectorStrategy {
    @VisibleForTesting
    public static final int TELEPHONY_SCORE_HIGH = 3;
    @VisibleForTesting
    public static final int TELEPHONY_SCORE_HIGHEST = 4;
    @VisibleForTesting
    public static final int TELEPHONY_SCORE_LOW = 1;
    @VisibleForTesting
    public static final int TELEPHONY_SCORE_MEDIUM = 2;
    @VisibleForTesting
    public static final int TELEPHONY_SCORE_NONE = 0;
    @VisibleForTesting
    public static final int TELEPHONY_SCORE_USAGE_THRESHOLD = 2;
    @GuardedBy({"this"})
    public ConfigurationInternal mCurrentConfigurationInternal;
    @GuardedBy({"this"})
    public TimeZoneDetectorStatus mDetectorStatus;
    public final Environment mEnvironment;
    public final ServiceConfigAccessor mServiceConfigAccessor;
    @GuardedBy({"this"})
    public TimestampedValue<Boolean> mTelephonyTimeZoneFallbackEnabled;
    @GuardedBy({"this"})
    public final ArrayMapWithHistory<Integer, QualifiedTelephonyTimeZoneSuggestion> mTelephonySuggestionsBySlotIndex = new ArrayMapWithHistory<>(10);
    @GuardedBy({"this"})
    public final ReferenceWithHistory<LocationAlgorithmEvent> mLatestLocationAlgorithmEvent = new ReferenceWithHistory<>(10);
    @GuardedBy({"this"})
    public final ReferenceWithHistory<ManualTimeZoneSuggestion> mLatestManualSuggestion = new ReferenceWithHistory<>(10);
    @GuardedBy({"this"})
    public final List<StateChangeListener> mStateChangeListeners = new ArrayList();

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public interface Environment {
        void addDebugLogEntry(String str);

        void dumpDebugLog(PrintWriter printWriter);

        long elapsedRealtimeMillis();

        String getDeviceTimeZone();

        int getDeviceTimeZoneConfidence();

        void runAsync(Runnable runnable);

        void setDeviceTimeZoneAndConfidence(String str, int i, String str2);
    }

    public static TimeZoneDetectorStrategyImpl create(Handler handler, ServiceConfigAccessor serviceConfigAccessor) {
        return new TimeZoneDetectorStrategyImpl(serviceConfigAccessor, new EnvironmentImpl(handler));
    }

    @VisibleForTesting
    public TimeZoneDetectorStrategyImpl(ServiceConfigAccessor serviceConfigAccessor, Environment environment) {
        Objects.requireNonNull(environment);
        Environment environment2 = environment;
        this.mEnvironment = environment2;
        Objects.requireNonNull(serviceConfigAccessor);
        ServiceConfigAccessor serviceConfigAccessor2 = serviceConfigAccessor;
        this.mServiceConfigAccessor = serviceConfigAccessor2;
        this.mTelephonyTimeZoneFallbackEnabled = new TimestampedValue<>(environment2.elapsedRealtimeMillis(), Boolean.TRUE);
        synchronized (this) {
            serviceConfigAccessor2.addConfigurationInternalChangeListener(new StateChangeListener() { // from class: com.android.server.timezonedetector.TimeZoneDetectorStrategyImpl$$ExternalSyntheticLambda0
                @Override // com.android.server.timezonedetector.StateChangeListener
                public final void onChange() {
                    TimeZoneDetectorStrategyImpl.this.handleConfigurationInternalMaybeChanged();
                }
            });
            updateCurrentConfigurationInternalIfRequired("TimeZoneDetectorStrategyImpl:");
        }
    }

    @Override // com.android.server.timezonedetector.TimeZoneDetectorStrategy
    public synchronized TimeZoneCapabilitiesAndConfig getCapabilitiesAndConfig(int i, boolean z) {
        ConfigurationInternal configurationInternal;
        if (this.mCurrentConfigurationInternal.getUserId() == i) {
            configurationInternal = this.mCurrentConfigurationInternal;
        } else {
            configurationInternal = this.mServiceConfigAccessor.getConfigurationInternal(i);
        }
        return new TimeZoneCapabilitiesAndConfig(this.mDetectorStatus, configurationInternal.asCapabilities(z), configurationInternal.asConfiguration());
    }

    @Override // com.android.server.timezonedetector.TimeZoneDetectorStrategy
    public synchronized boolean updateConfiguration(int i, TimeZoneConfiguration timeZoneConfiguration, boolean z) {
        boolean updateConfiguration;
        updateConfiguration = this.mServiceConfigAccessor.updateConfiguration(i, timeZoneConfiguration, z);
        if (updateConfiguration) {
            updateCurrentConfigurationInternalIfRequired("updateConfiguration: userId=" + i + ", configuration=" + timeZoneConfiguration + ", bypassUserPolicyChecks=" + z);
        }
        return updateConfiguration;
    }

    @GuardedBy({"this"})
    public final void updateCurrentConfigurationInternalIfRequired(String str) {
        ConfigurationInternal currentUserConfigurationInternal = this.mServiceConfigAccessor.getCurrentUserConfigurationInternal();
        ConfigurationInternal configurationInternal = this.mCurrentConfigurationInternal;
        if (currentUserConfigurationInternal.equals(configurationInternal)) {
            return;
        }
        this.mCurrentConfigurationInternal = currentUserConfigurationInternal;
        String str2 = str + " [oldConfiguration=" + configurationInternal + ", newConfiguration=" + currentUserConfigurationInternal + "]";
        logTimeZoneDebugInfo(str2);
        updateDetectorStatus();
        notifyStateChangeListenersAsynchronously();
        doAutoTimeZoneDetection(this.mCurrentConfigurationInternal, str2);
    }

    @GuardedBy({"this"})
    public final void notifyStateChangeListenersAsynchronously() {
        for (StateChangeListener stateChangeListener : this.mStateChangeListeners) {
            Environment environment = this.mEnvironment;
            Objects.requireNonNull(stateChangeListener);
            environment.runAsync(new EnvironmentImpl$$ExternalSyntheticLambda1(stateChangeListener));
        }
    }

    @Override // com.android.server.timezonedetector.TimeZoneDetectorStrategy
    public synchronized void addChangeListener(StateChangeListener stateChangeListener) {
        this.mStateChangeListeners.add(stateChangeListener);
    }

    @Override // com.android.server.timezonedetector.TimeZoneDetectorStrategy
    public synchronized boolean confirmTimeZone(String str) {
        Objects.requireNonNull(str);
        String deviceTimeZone = this.mEnvironment.getDeviceTimeZone();
        if (deviceTimeZone.equals(str)) {
            if (this.mEnvironment.getDeviceTimeZoneConfidence() < 100) {
                Environment environment = this.mEnvironment;
                environment.setDeviceTimeZoneAndConfidence(deviceTimeZone, 100, "confirmTimeZone: timeZoneId=" + str);
            }
            return true;
        }
        return false;
    }

    @Override // com.android.server.timezonedetector.TimeZoneDetectorStrategy
    public synchronized TimeZoneState getTimeZoneState() {
        return new TimeZoneState(this.mEnvironment.getDeviceTimeZone(), this.mEnvironment.getDeviceTimeZoneConfidence() < 100);
    }

    @Override // com.android.server.timezonedetector.TimeZoneDetectorStrategy
    public void setTimeZoneState(TimeZoneState timeZoneState) {
        Objects.requireNonNull(timeZoneState);
        this.mEnvironment.setDeviceTimeZoneAndConfidence(timeZoneState.getId(), timeZoneState.getUserShouldConfirmId() ? 0 : 100, "setTimeZoneState()");
    }

    @Override // com.android.server.timezonedetector.TimeZoneDetectorStrategy
    public synchronized void handleLocationAlgorithmEvent(LocationAlgorithmEvent locationAlgorithmEvent) {
        ConfigurationInternal configurationInternal = this.mCurrentConfigurationInternal;
        Objects.requireNonNull(locationAlgorithmEvent);
        this.mLatestLocationAlgorithmEvent.set(locationAlgorithmEvent);
        if (updateDetectorStatus()) {
            notifyStateChangeListenersAsynchronously();
        }
        if (locationAlgorithmEvent.getAlgorithmStatus().couldEnableTelephonyFallback()) {
            enableTelephonyTimeZoneFallback("handleLocationAlgorithmEvent(), event=" + locationAlgorithmEvent);
        } else {
            disableTelephonyFallbackIfNeeded();
        }
        doAutoTimeZoneDetection(configurationInternal, "New location algorithm event received. event=" + locationAlgorithmEvent);
    }

    @Override // com.android.server.timezonedetector.TimeZoneDetectorStrategy
    public synchronized boolean suggestManualTimeZone(int i, ManualTimeZoneSuggestion manualTimeZoneSuggestion, boolean z) {
        ConfigurationInternal configurationInternal = this.mCurrentConfigurationInternal;
        if (configurationInternal.getUserId() != i) {
            Slog.w("time_zone_detector", "Manual suggestion received but user != current user, userId=" + i + " suggestion=" + manualTimeZoneSuggestion);
            return false;
        }
        Objects.requireNonNull(manualTimeZoneSuggestion);
        String zoneId = manualTimeZoneSuggestion.getZoneId();
        String str = "Manual time suggestion received: suggestion=" + manualTimeZoneSuggestion;
        TimeZoneCapabilities asCapabilities = configurationInternal.asCapabilities(z);
        if (asCapabilities.getSetManualTimeZoneCapability() != 40) {
            Slog.i("time_zone_detector", "User does not have the capability needed to set the time zone manually: capabilities=" + asCapabilities + ", timeZoneId=" + zoneId + ", cause=" + str);
            return false;
        }
        this.mLatestManualSuggestion.set(manualTimeZoneSuggestion);
        setDeviceTimeZoneIfRequired(zoneId, str);
        return true;
    }

    @Override // com.android.server.timezonedetector.TimeZoneDetectorStrategy
    public synchronized void suggestTelephonyTimeZone(TelephonyTimeZoneSuggestion telephonyTimeZoneSuggestion) {
        ConfigurationInternal configurationInternal = this.mCurrentConfigurationInternal;
        Objects.requireNonNull(telephonyTimeZoneSuggestion);
        this.mTelephonySuggestionsBySlotIndex.put(Integer.valueOf(telephonyTimeZoneSuggestion.getSlotIndex()), new QualifiedTelephonyTimeZoneSuggestion(telephonyTimeZoneSuggestion, scoreTelephonySuggestion(telephonyTimeZoneSuggestion)));
        doAutoTimeZoneDetection(configurationInternal, "New telephony time zone suggested. suggestion=" + telephonyTimeZoneSuggestion);
    }

    @Override // com.android.server.timezonedetector.TimeZoneDetectorStrategy
    public synchronized void enableTelephonyTimeZoneFallback(String str) {
        if (!((Boolean) this.mTelephonyTimeZoneFallbackEnabled.getValue()).booleanValue()) {
            ConfigurationInternal configurationInternal = this.mCurrentConfigurationInternal;
            this.mTelephonyTimeZoneFallbackEnabled = new TimestampedValue<>(this.mEnvironment.elapsedRealtimeMillis(), Boolean.TRUE);
            logTimeZoneDebugInfo("enableTelephonyTimeZoneFallback:  reason=" + str + ", currentUserConfig=" + configurationInternal + ", mTelephonyTimeZoneFallbackEnabled=" + this.mTelephonyTimeZoneFallbackEnabled);
            disableTelephonyFallbackIfNeeded();
            if (configurationInternal.isTelephonyFallbackSupported()) {
                doAutoTimeZoneDetection(configurationInternal, str);
            }
        }
    }

    @Override // com.android.server.timezonedetector.TimeZoneDetectorStrategy
    public synchronized MetricsTimeZoneDetectorState generateMetricsState() {
        QualifiedTelephonyTimeZoneSuggestion findBestTelephonySuggestion;
        findBestTelephonySuggestion = findBestTelephonySuggestion();
        return MetricsTimeZoneDetectorState.create(new OrdinalGenerator(new TimeZoneCanonicalizer()), this.mCurrentConfigurationInternal, this.mEnvironment.getDeviceTimeZone(), getLatestManualSuggestion(), findBestTelephonySuggestion == null ? null : findBestTelephonySuggestion.suggestion, getLatestLocationAlgorithmEvent());
    }

    @Override // com.android.server.timezonedetector.TimeZoneDetectorStrategy
    public boolean isTelephonyTimeZoneDetectionSupported() {
        boolean isTelephonyDetectionSupported;
        synchronized (this) {
            isTelephonyDetectionSupported = this.mCurrentConfigurationInternal.isTelephonyDetectionSupported();
        }
        return isTelephonyDetectionSupported;
    }

    @Override // com.android.server.timezonedetector.TimeZoneDetectorStrategy
    public boolean isGeoTimeZoneDetectionSupported() {
        boolean isGeoDetectionSupported;
        synchronized (this) {
            isGeoDetectionSupported = this.mCurrentConfigurationInternal.isGeoDetectionSupported();
        }
        return isGeoDetectionSupported;
    }

    public static int scoreTelephonySuggestion(TelephonyTimeZoneSuggestion telephonyTimeZoneSuggestion) {
        if (telephonyTimeZoneSuggestion.getZoneId() == null) {
            return 0;
        }
        int i = 4;
        if (telephonyTimeZoneSuggestion.getMatchType() != 5 && telephonyTimeZoneSuggestion.getMatchType() != 4) {
            i = 1;
            if (telephonyTimeZoneSuggestion.getQuality() == 1) {
                return 3;
            }
            if (telephonyTimeZoneSuggestion.getQuality() == 2) {
                return 2;
            }
            if (telephonyTimeZoneSuggestion.getQuality() != 3) {
                throw new AssertionError();
            }
        }
        return i;
    }

    @GuardedBy({"this"})
    public final void doAutoTimeZoneDetection(ConfigurationInternal configurationInternal, String str) {
        int detectionMode = configurationInternal.getDetectionMode();
        if (detectionMode == 0) {
            Slog.i("time_zone_detector", "Unknown detection mode: " + detectionMode + ", is location off?");
        } else if (detectionMode != 1) {
            if (detectionMode != 2) {
                if (detectionMode == 3) {
                    doTelephonyTimeZoneDetection(str);
                    return;
                }
                Slog.wtf("time_zone_detector", "Unknown detection mode: " + detectionMode);
            } else if (!doGeolocationTimeZoneDetection(str) && ((Boolean) this.mTelephonyTimeZoneFallbackEnabled.getValue()).booleanValue() && configurationInternal.isTelephonyFallbackSupported()) {
                doTelephonyTimeZoneDetection(str + ", telephony fallback mode");
            }
        }
    }

    @GuardedBy({"this"})
    public final boolean doGeolocationTimeZoneDetection(String str) {
        List<String> zoneIds;
        LocationAlgorithmEvent locationAlgorithmEvent = this.mLatestLocationAlgorithmEvent.get();
        if (locationAlgorithmEvent == null || locationAlgorithmEvent.getSuggestion() == null || (zoneIds = locationAlgorithmEvent.getSuggestion().getZoneIds()) == null) {
            return false;
        }
        if (zoneIds.isEmpty()) {
            return true;
        }
        String deviceTimeZone = this.mEnvironment.getDeviceTimeZone();
        if (!zoneIds.contains(deviceTimeZone)) {
            deviceTimeZone = zoneIds.get(0);
        }
        setDeviceTimeZoneIfRequired(deviceTimeZone, str);
        return true;
    }

    @GuardedBy({"this"})
    public final void disableTelephonyFallbackIfNeeded() {
        LocationAlgorithmEvent locationAlgorithmEvent = this.mLatestLocationAlgorithmEvent.get();
        if (locationAlgorithmEvent == null) {
            return;
        }
        GeolocationTimeZoneSuggestion suggestion = locationAlgorithmEvent.getSuggestion();
        if (((suggestion == null || suggestion.getZoneIds() == null) ? false : true) && ((Boolean) this.mTelephonyTimeZoneFallbackEnabled.getValue()).booleanValue()) {
            if (suggestion.getEffectiveFromElapsedMillis() > this.mTelephonyTimeZoneFallbackEnabled.getReferenceTimeMillis()) {
                this.mTelephonyTimeZoneFallbackEnabled = new TimestampedValue<>(this.mEnvironment.elapsedRealtimeMillis(), Boolean.FALSE);
                logTimeZoneDebugInfo("disableTelephonyFallbackIfNeeded: mTelephonyTimeZoneFallbackEnabled=" + this.mTelephonyTimeZoneFallbackEnabled);
            }
        }
    }

    public final void logTimeZoneDebugInfo(String str) {
        this.mEnvironment.addDebugLogEntry(str);
    }

    @GuardedBy({"this"})
    public final void doTelephonyTimeZoneDetection(String str) {
        QualifiedTelephonyTimeZoneSuggestion findBestTelephonySuggestion = findBestTelephonySuggestion();
        if (findBestTelephonySuggestion == null) {
            return;
        }
        if (findBestTelephonySuggestion.score >= 2) {
            String zoneId = findBestTelephonySuggestion.suggestion.getZoneId();
            if (zoneId == null) {
                Slog.w("time_zone_detector", "Empty zone suggestion scored higher than expected. This is an error: bestTelephonySuggestion=" + findBestTelephonySuggestion + ", detectionReason=" + str);
                return;
            }
            setDeviceTimeZoneIfRequired(zoneId, "Found good suggestion: bestTelephonySuggestion=" + findBestTelephonySuggestion + ", detectionReason=" + str);
        }
    }

    @GuardedBy({"this"})
    public final void setDeviceTimeZoneIfRequired(String str, String str2) {
        String deviceTimeZone = this.mEnvironment.getDeviceTimeZone();
        int deviceTimeZoneConfidence = this.mEnvironment.getDeviceTimeZoneConfidence();
        if (!str.equals(deviceTimeZone) || 100 > deviceTimeZoneConfidence) {
            this.mEnvironment.setDeviceTimeZoneAndConfidence(str, 100, "Set device time zone or higher confidence: newZoneId=" + str + ", cause=" + str2 + ", newConfidence=100");
        }
    }

    @GuardedBy({"this"})
    public final QualifiedTelephonyTimeZoneSuggestion findBestTelephonySuggestion() {
        int i;
        int i2;
        QualifiedTelephonyTimeZoneSuggestion qualifiedTelephonyTimeZoneSuggestion = null;
        for (int i3 = 0; i3 < this.mTelephonySuggestionsBySlotIndex.size(); i3++) {
            QualifiedTelephonyTimeZoneSuggestion valueAt = this.mTelephonySuggestionsBySlotIndex.valueAt(i3);
            if (valueAt != null && (qualifiedTelephonyTimeZoneSuggestion == null || (i = valueAt.score) > (i2 = qualifiedTelephonyTimeZoneSuggestion.score) || (i == i2 && valueAt.suggestion.getSlotIndex() < qualifiedTelephonyTimeZoneSuggestion.suggestion.getSlotIndex()))) {
                qualifiedTelephonyTimeZoneSuggestion = valueAt;
            }
        }
        return qualifiedTelephonyTimeZoneSuggestion;
    }

    @VisibleForTesting
    public synchronized QualifiedTelephonyTimeZoneSuggestion findBestTelephonySuggestionForTests() {
        return findBestTelephonySuggestion();
    }

    public final synchronized void handleConfigurationInternalMaybeChanged() {
        updateCurrentConfigurationInternalIfRequired("handleConfigurationInternalMaybeChanged:");
    }

    @GuardedBy({"this"})
    public final boolean updateDetectorStatus() {
        TimeZoneDetectorStatus createTimeZoneDetectorStatus = createTimeZoneDetectorStatus(this.mCurrentConfigurationInternal, this.mLatestLocationAlgorithmEvent.get());
        boolean z = !createTimeZoneDetectorStatus.equals(this.mDetectorStatus);
        if (z) {
            this.mDetectorStatus = createTimeZoneDetectorStatus;
        }
        return z;
    }

    @Override // com.android.server.timezonedetector.Dumpable
    public synchronized void dump(IndentingPrintWriter indentingPrintWriter, String[] strArr) {
        indentingPrintWriter.println("TimeZoneDetectorStrategy:");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("mCurrentConfigurationInternal=" + this.mCurrentConfigurationInternal);
        indentingPrintWriter.println("mDetectorStatus=" + this.mDetectorStatus);
        indentingPrintWriter.println("[Capabilities=" + this.mCurrentConfigurationInternal.asCapabilities(false) + "]");
        StringBuilder sb = new StringBuilder();
        sb.append("mEnvironment.getDeviceTimeZone()=");
        sb.append(this.mEnvironment.getDeviceTimeZone());
        indentingPrintWriter.println(sb.toString());
        indentingPrintWriter.println("mEnvironment.getDeviceTimeZoneConfidence()=" + this.mEnvironment.getDeviceTimeZoneConfidence());
        indentingPrintWriter.println("Misc state:");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("mTelephonyTimeZoneFallbackEnabled=" + formatDebugString(this.mTelephonyTimeZoneFallbackEnabled));
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("Time zone debug log:");
        indentingPrintWriter.increaseIndent();
        this.mEnvironment.dumpDebugLog(indentingPrintWriter);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("Manual suggestion history:");
        indentingPrintWriter.increaseIndent();
        this.mLatestManualSuggestion.dump(indentingPrintWriter);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("Location algorithm event history:");
        indentingPrintWriter.increaseIndent();
        this.mLatestLocationAlgorithmEvent.dump(indentingPrintWriter);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("Telephony suggestion history:");
        indentingPrintWriter.increaseIndent();
        this.mTelephonySuggestionsBySlotIndex.dump(indentingPrintWriter);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.decreaseIndent();
    }

    @VisibleForTesting
    public synchronized ManualTimeZoneSuggestion getLatestManualSuggestion() {
        return this.mLatestManualSuggestion.get();
    }

    @VisibleForTesting
    public synchronized QualifiedTelephonyTimeZoneSuggestion getLatestTelephonySuggestion(int i) {
        return this.mTelephonySuggestionsBySlotIndex.get(Integer.valueOf(i));
    }

    @VisibleForTesting
    public synchronized LocationAlgorithmEvent getLatestLocationAlgorithmEvent() {
        return this.mLatestLocationAlgorithmEvent.get();
    }

    @VisibleForTesting
    public synchronized boolean isTelephonyFallbackEnabledForTests() {
        return ((Boolean) this.mTelephonyTimeZoneFallbackEnabled.getValue()).booleanValue();
    }

    @VisibleForTesting
    public synchronized ConfigurationInternal getCachedCapabilitiesAndConfigForTests() {
        return this.mCurrentConfigurationInternal;
    }

    @VisibleForTesting
    public synchronized TimeZoneDetectorStatus getCachedDetectorStatusForTests() {
        return this.mDetectorStatus;
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static final class QualifiedTelephonyTimeZoneSuggestion {
        @VisibleForTesting
        public final int score;
        @VisibleForTesting
        public final TelephonyTimeZoneSuggestion suggestion;

        @VisibleForTesting
        public QualifiedTelephonyTimeZoneSuggestion(TelephonyTimeZoneSuggestion telephonyTimeZoneSuggestion, int i) {
            this.suggestion = telephonyTimeZoneSuggestion;
            this.score = i;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || QualifiedTelephonyTimeZoneSuggestion.class != obj.getClass()) {
                return false;
            }
            QualifiedTelephonyTimeZoneSuggestion qualifiedTelephonyTimeZoneSuggestion = (QualifiedTelephonyTimeZoneSuggestion) obj;
            return this.score == qualifiedTelephonyTimeZoneSuggestion.score && this.suggestion.equals(qualifiedTelephonyTimeZoneSuggestion.suggestion);
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.score), this.suggestion);
        }

        public String toString() {
            return "QualifiedTelephonyTimeZoneSuggestion{suggestion=" + this.suggestion + ", score=" + this.score + '}';
        }
    }

    public static String formatDebugString(TimestampedValue<?> timestampedValue) {
        return timestampedValue.getValue() + " @ " + Duration.ofMillis(timestampedValue.getReferenceTimeMillis());
    }

    public static TimeZoneDetectorStatus createTimeZoneDetectorStatus(ConfigurationInternal configurationInternal, LocationAlgorithmEvent locationAlgorithmEvent) {
        int i;
        if (configurationInternal.isAutoDetectionSupported()) {
            i = configurationInternal.getAutoDetectionEnabledBehavior() ? 3 : 2;
        } else {
            i = 1;
        }
        return new TimeZoneDetectorStatus(i, createTelephonyAlgorithmStatus(configurationInternal), createLocationAlgorithmStatus(configurationInternal, locationAlgorithmEvent));
    }

    public static LocationTimeZoneAlgorithmStatus createLocationAlgorithmStatus(ConfigurationInternal configurationInternal, LocationAlgorithmEvent locationAlgorithmEvent) {
        if (locationAlgorithmEvent != null) {
            return locationAlgorithmEvent.getAlgorithmStatus();
        }
        if (!configurationInternal.isGeoDetectionSupported()) {
            return LocationTimeZoneAlgorithmStatus.NOT_SUPPORTED;
        }
        if (configurationInternal.isGeoDetectionExecutionEnabled()) {
            return LocationTimeZoneAlgorithmStatus.RUNNING_NOT_REPORTED;
        }
        return LocationTimeZoneAlgorithmStatus.NOT_RUNNING;
    }

    public static TelephonyTimeZoneAlgorithmStatus createTelephonyAlgorithmStatus(ConfigurationInternal configurationInternal) {
        return new TelephonyTimeZoneAlgorithmStatus(!configurationInternal.isTelephonyDetectionSupported() ? 1 : 3);
    }
}
